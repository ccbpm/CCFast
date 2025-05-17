package bp.cloud.httphandler;

import bp.cloud.Dev2Interface;
import bp.da.*;
import bp.sys.*;
import bp.web.*;
import bp.port.*;
import bp.wf.template.*;
import bp.wf.xml.*;
import bp.difference.*;
import java.util.*;

/** 
 页面功能实体
*/
public class Admin extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public Admin()
	{
	}


		///#region 执行父类的重写方法.

		///#endregion 执行父类的重写方法.


		///#region 界面 .
	public final String Register_Init()
	{
		return "注册页面";
	}

		///#endregion xxx 界面方法.

	public final String Organization_CreateOrg()
	{
		String orgNo = this.GetRequestVal("OrgNo");
		String orgName = this.GetRequestVal("OrgName");

		return Dev2Interface.Port_CreateOrg(orgNo, orgName);
	}

	public final String CheckEncryptEnable()
	{
		if (SystemConfig.getIsEnablePasswordEncryption() == true)
		{
			return "1";
		}
		return "0";
	}

	/** 
	 登录
	 
	 @return 
	*/
	public final String Login_AdminOnlySaas()
	{
		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.SAAS)
		{
			return "err@必须是saas模式才能使用此接口登录.";
		}

		try
		{
			String userNo = this.GetRequestVal("TB_No");
			if (userNo == null)
			{
				userNo = this.GetRequestVal("TB_UserNo");
			}
			String pass = this.GetRequestVal("TB_PW");
			if (pass == null)
			{
				pass = this.GetRequestVal("TB_Pass");
			}

			//从数据库里查询.
			Emp emp = new Emp();
			emp.setNo("admin");
			if (emp.RetrieveFromDBSources() == 0)
			{
				return "err@丢失了admin用户.";
			}

			if (emp.CheckPass(pass) == false)
			{
				return "err@用户名密码错误.";
			}
			bp.wf.Dev2Interface.Port_Login("admin", SystemConfig.DeptDefaultNo());


			String token = bp.wf.Dev2Interface.Port_GenerToken();
			WebUser.setToken(token);
			Hashtable ht = new Hashtable();
			ht.put("Token", token);
			return bp.tools.Json.ToJson(ht);
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}



	/** 
	 登录的时候判断.
	 
	 @return 
	*/
	public final String Login_Submit() throws Exception {
		Emp emp = new Emp();
		emp.setNo(this.GetRequestVal("TB_Adminer").trim());
		if (emp.RetrieveFromDBSources() == 0)
		{
			return "err@用户名或密码错误.";
		}

		String pass = this.GetRequestVal("TB_PassWord2").trim();
		if (emp.CheckPass(pass) == false)
		{
			return "err@用户名或密码错误.";
		}

		//让其登录.

		bp.web.GuestUser.Exit();
		bp.wf.Dev2Interface.Port_Login(emp.getNo());
		String token = bp.wf.Dev2Interface.Port_GenerToken("PC");

		//判断当前管理员有多少个企业.
		Depts depts = new Depts();
		depts.Retrieve("Adminer", emp.getNo(), null);

		//如果部门为0，就让其注册.
		if (depts.size() == 0)
		{
			return "url@/RegisterOrg.html";
		}

		//如果只有一个部门.
		if (depts.size() == 1)
		{
			Dept dept = depts.get(0) instanceof Dept ? (Dept)depts.get(0) : null;
			emp.setDeptNo(dept.getNo());
			emp.Update();
			return "url@/Admin/Portal/Default.htm?Token=" + token + "&UserNo=" + emp.getNo();
		}

		//转入到选择一个企业的页面.
		return "url@/Admin/Portal/SelectOneInc.htm?Token=" + token + "&UserNo=" + emp.getNo();
	}

	/** 
	 查询可以登录的企业.
	 
	 @return 
	*/
	public final String SelectOneInc_Init() throws Exception {
		Depts depts = new Depts();
		depts.Retrieve(DeptAttr.Adminer, WebUser.getNo());
		return depts.ToJson("dt");
	}

	public final String SelectOneInc_SelectIt() throws Exception {
		String no = this.GetRequestVal("No");
		Emp emp = new Emp(WebUser.getNo());
		emp.setDeptNo(no);
		emp.Update();
		return "url@Admin/Portal/Default.htm?Token=" + WebUser.getToken() + "&UserNo=" + emp.getNo();
	}

	/** 
	  要返回的数据.
	 
	 @return 
	*/
	public final String Default_Init()
	{
		return "";
	}

	/** 
	 流程树
	 
	 @return 
	*/
	public final String Default_FlowsTree() throws Exception {
		//组织数据源.
		String sql = "SELECT * FROM (SELECT 'F'+No as NO,'F'+ParentNo PARENTNO, NAME, IDX, 1 ISPARENT,'FLOWTYPE' TTYPE, -1 DTYPE FROM WF_FlowSort WHERE OrgNo='" + WebUser.getDeptNo() + "' union  SELECT NO, 'F'+FK_FlowSort as PARENTNO,(NO + '.' + NAME) as NAME,IDX,0 ISPARENT,'FLOW' TTYPE, 0 as DTYPE FROM WF_Flow) A  ORDER BY DTYPE, IDX ";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		//判断是否为空，如果为空，则创建一个流程根结点，added by liuxc,2016-01-24
		if (dt.Rows.size() == 0)
		{
			FlowSort fs = new FlowSort();
			fs.setNo("99");
			fs.setParentNo("0");
			fs.setName("流程树");
			fs.Insert();

			dt.Rows.AddDatas("F99", "F0", "流程树", 0, 1, "FLOWTYPE", -1);
		}
		else
		{
			DataRow[] drs = dt.Select("NAME='流程树'");
			if (drs.length > 0 && !Objects.equals(drs[0].get("PARENTNO"), "F0"))
			{
				drs[0].setValue("PARENTNO", "F0");
			}
		}

		String str = bp.tools.Json.ToJson(dt);
		return str;
	}

	/** 
	 查询表单树
	 
	 @return 
	*/
	public final String Default_FrmTree()
	{
		//组织数据源.
		String sqls = "";
		/* sqls = "SELECT No,ParentNo,Name, Idx FROM Sys_FormTree WHERE OrgNo=" + WebUser.getDeptNo() + " ORDER BY Idx ASC ";
		 sqls += "SELECT No, FK_FormTree as ParentNo,Name,Idx,0 IsParent  FROM Sys_MapData    ";*/
		sqls = "SELECT No,ParentNo,Name, Idx, 1 IsParent, 'FORMTYPE' TType FROM Sys_FormTree WHERE OrgNo='" + WebUser.getDeptNo() + "' ORDER BY Idx ASC ; ";
		sqls += "SELECT No, FK_FormTree as ParentNo,Name,Idx,0 IsParent, 'FORM' TType FROM Sys_MapData  WHERE AppType=0 AND FK_FormTree IN (SELECT No FROM Sys_FormTree WHERE OrgNo='" + WebUser.getDeptNo() + "') ORDER BY Idx ASC";

		DataSet ds = DBAccess.RunSQLReturnDataSet(sqls);
		DataTable dtSort = ds.Tables.get(0); //类别表.
		DataTable dtForm = ds.Tables.get(1).clone(); //表单表,这个是最终返回的数据.
		//增加顶级目录.
		DataRow[] rowsOfSort = dtSort.Select("ParentNo='0'");
		DataRow drFormRoot = dtForm.NewRow();
		drFormRoot.setValue(0, rowsOfSort[0].getValue("No"));
		drFormRoot.setValue(1, "0");
		drFormRoot.setValue(2, rowsOfSort[0].getValue("Name"));
		drFormRoot.setValue(3, rowsOfSort[0].getValue("Idx"));
		drFormRoot.setValue(4, rowsOfSort[0].getValue("IsParent"));
		drFormRoot.setValue(5, rowsOfSort[0].getValue("TType"));
		dtForm.Rows.AddRow(drFormRoot); //增加顶级类别..

		//把类别数据组装到form数据里.
		for (DataRow dr : dtSort.Rows)
		{
			DataRow drForm = dtForm.NewRow();
			drForm.setValue(0, dr.getValue("No"));
			drForm.setValue(1, dr.getValue("ParentNo"));
			drForm.setValue(2, dr.getValue("Name"));
			drForm.setValue(3, dr.getValue("Idx"));
			drForm.setValue(4, dr.getValue("IsParent"));
			drForm.setValue(5, dr.getValue("TType"));
			dtForm.Rows.AddRow(drForm); //类别.
		}

		for (DataRow row : ds.getTables().get(1).Rows) {
			dtForm.Rows.AddRow(row);
		}

		String str = bp.tools.Json.ToJson(dtForm);
		return str;
	}
	/** 
	 获取设计器 - 系统维护菜单数据
	 系统维护管理员菜单 需要翻译
	 
	 @return 
	*/
	public final String Default_AdminMenu() throws Exception {
		//查询全部.
		AdminMenus groups = new AdminMenus();
		groups.RetrieveAll();

		return bp.tools.Json.ToJson(groups.ToDataTable());
	}

		///#region 执行父类的重写方法.
	/** 
	 默认执行的方法
	 
	 @return 
	*/
	@Override
	protected String DoDefaultMethod()
	{
		return "err@没有判断的标记:" + this.getDoType();
	}

		///#endregion 执行父类的重写方法.
}
