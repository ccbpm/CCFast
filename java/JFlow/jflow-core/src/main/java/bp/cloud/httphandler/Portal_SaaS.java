package bp.cloud.httphandler;

import bp.da.*;
import bp.difference.*;
import bp.port.Emp;
import bp.port.EmpAttr;
import bp.port.Emps;
import bp.cloud.*;
import net.sf.json.JSONObject;

import java.util.Hashtable;

/** 
 页面功能实体
*/
public class Portal_SaaS extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public Portal_SaaS()
	{
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
	 获取组织
	 
	 @return 
	*/
	public final String SelectOneOrg_Init() throws Exception {
		Orgs orgs = new Orgs();
		orgs.RetrieveAll();
		DataTable dt = orgs.ToDataTableField("Orgs");
		return bp.tools.Json.ToJson(dt);
	}

	/**
	 * JFlow要用到，根据OrgNo获取对应的组织数据
	 * @return
	 * @throws Exception
	 */
	public final String GetOrgByNo() throws Exception {
		String no = this.GetRequestVal("OrgNo");
		if(DataType.IsNullOrEmpty(no)){
			return "";
		}
		bp.cloud.Org org = new Org();
		org.setNo(no);
		if(org.RetrieveFromDBSources() == 0) {
			return "err@组织不存在.";
		}
		return org.ToJson();
	}

	/**
	 * JFlow要用到，根据UserID获取对应的组织集合
	 * @return
	 * @throws Exception
	 */
	public String GetOrgs() throws Exception {
		DataTable orgs = new DataTable();
		orgs.Columns.Add("no");	//组织编号
		orgs.Columns.Add("name"); //组织名称
		String userNo = this.GetRequestVal("UserID");
		bp.cloud.Emps emps = new bp.cloud.Emps();
		emps.Retrieve("UserID", userNo);
		DataTable dt = emps.ToDataTableField();
		for (int i = 0; i < dt.Rows.size(); i++)
		{
			DataRow emp = dt.Rows.get(i);
			String orgNo = emp.getValue("OrgNo").toString();
			bp.cloud.Org org = new bp.cloud.Org();
			org.setNo(orgNo);
			if (org.RetrieveFromDBSources() == 0)
			{
				return "err@组织不存在.组织编号["+ orgNo + "]";
			}

			DataRow dr = orgs.NewRow();
			dr.setValue("no", org.getNo());
			dr.setValue("name", org.getName());
			orgs.Rows.add(dr);
		}
		return bp.tools.Json.ToJson(orgs);
	}
	public String User_OrgNos() throws Exception {
		String userId = this.GetRequestVal("TB_No");
		String pass = this.GetRequestVal("TB_PW");
		if (DataType.IsNullOrEmpty(userId) == true)
			return "err@请输入用户的登录账号";

		Paras ps = new Paras();
		String sql = " SELECT A.OrgNo as No,B.Name,B.OrgSta From Port_Emp A ,Port_Org B Where  A.OrgNo=B.No  " +
				"AND UserID=" + SystemConfig.getAppCenterDBVarStr() + "UserID and pass= " + SystemConfig.getAppCenterDBVarStr() + "Pass" +
				" and OrgSta='1'";
		ps.Add("UserID", userId);
		ps.Add("Pass", pass.toLowerCase());
		DataTable dt = DBAccess.RunSQLReturnTable(sql, ps);
		if(dt.Rows.size() == 0){
			return "err@用户名不存在或密码错误.";
		}
		/*if ((dt.Rows.get(0).getValue("pass")).toString().toLowerCase().equals(pass.toLowerCase()) == false) {
			return "err@用户名或者密码错误.";
		}*/
		DataTable dtorg = DBAccess.RunSQLReturnTable(sql, ps);

		return bp.tools.Json.ToJson(dtorg);
	}
	public final String Login_Submit()
	{
		try
		{
			String orgNo = this.getOrgNo();
			String userNo = this.GetRequestVal("TB_No");
			String pass = this.GetRequestVal("TB_PW");
			if (pass == null)
			{
				pass = this.GetRequestVal("TB_Pass");
			}
			pass = pass.trim();
			Emp emp = new Emp();
			emp.setNo(this.getOrgNo() + "_" + userNo);
			if (emp.RetrieveFromDBSources() == 0)
			{
				return "err@用户名[" + userNo + "]不存在.";
			}

			if (emp.CheckPass(pass) == false)
			{
				return "err@密码错误.";
			}

			//BP.Cloud.Emp emp2 = new BP.Cloud.Emp(emp.getNo());
			bp.wf.Dev2Interface.Port_Login(userNo, this.getOrgNo());
			String token = bp.wf.Dev2Interface.Port_GenerToken();
			// log
			JSONObject log = new JSONObject();
			log.put("userID", emp.getUserID());
			log.put("deptText", emp.getDeptText());
			log.put("orgNo", orgNo);
			bp.sys.base.Glo.WriteUserLog(log.toString(), "用户登录[Saas]");
			// end
			/*Hashtable ht = new Hashtable();
            ht.Add("No", emp.No);
            ht.Add("Name", emp.Name);
            ht.Add("Token", token);
            ht.Add("FK_Dept", emp.FK_Dept);
            ht.Add("FK_DeptName", emp.FK_DeptText);
            ht.Add("OrgNo", emp.OrgNo);

            return BP.Tools.Json.ToJson(ht);*/
			return "url@/Portal/Standard/Default.htm?Token=" + token + "&UserNo=" + emp.getUserID() + "&OrgNo=" + emp.getOrgNo();
		} catch (Exception ex) {
			return "err@" + ex.getMessage();
		}
	}
	public final String Login_SubmitSaaSOption()
	{
		try
		{
			String userNo = this.GetRequestVal("TB_No");
			if (DataType.IsNullOrEmpty(userNo) == true)
			{
				return "err@账号不能为空.";
			}

			if (userNo.toLowerCase().equals("admin") == true)
			{
				return "err@请登录admin后台.";
			}

			String pass = this.GetRequestVal("TB_PW");
			if (pass == null)
			{
				pass = this.GetRequestVal("TB_Pass");
			}
			pass = pass.trim();
			Emps emps = new Emps();
			if (emps.Retrieve(EmpAttr.UserID, userNo, null) == 0)
			{
				return "err@用户名[" + userNo + "]不存在.";
			}

			Emp myemp = emps.get(0) instanceof Emp ? (Emp)emps.get(0) : null;

			//检查密码
			Emp emp1 = new Emp();
			emp1.setNo(myemp.getNo());
			emp1.RetrieveFromDBSources();
			if (emp1.CheckPass(pass) == false)
			{
				return "err@密码错误.";
			}

			//BP.Cloud.Emp emp2 = new BP.Cloud.Emp(emp.getNo());
			bp.wf.Dev2Interface.Port_Login(userNo, myemp.getOrgNo());
			String token = bp.wf.Dev2Interface.Port_GenerToken();
			Hashtable ht = new Hashtable();
			ht.put("No", myemp.getOrgNo());
			ht.put("Name", myemp.getName());
			ht.put("Token", token);
			ht.put("FK_Dept", myemp.GetValByKey("FK_Dept"));
			ht.put("FK_DeptName", myemp.GetValByKey("FK_DeptText"));
			ht.put("OrgNo", myemp.getOrgNo());

			return bp.tools.Json.ToJson(ht);
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}


		///#region 执行父类的重写方法.
	/** 
	 默认执行的方法
	 
	 @return 
	*/
	@Override
	protected String DoDefaultMethod()
	{
		switch (this.getDoType())
		{
			case "DtlFieldUp": //字段上移
				return "执行成功.";
			default:
				break;
		}

		//找不不到标记就抛出异常.
		throw new RuntimeException("@标记[" + this.getDoType() + "]，没有找到. @RowURL:" + ContextHolderUtils.getRequest().getRequestURI());
	}

		///#endregion 执行父类的重写方法.


		///#region xxx 界面 .

		///#endregion xxx 界面方法.

}
