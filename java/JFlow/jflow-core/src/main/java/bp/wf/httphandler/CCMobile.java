package bp.wf.httphandler;

import bp.da.*;
import bp.gpm.weixin.WeiXinEntity;
import bp.sys.*;
import bp.tools.AesEncodeUtil;
import bp.tools.HttpClientUtil;
import bp.web.*;
import bp.port.*;
import bp.en.*;
import bp.wf.port.admin2group.*;
import bp.difference.*;
import bp.ccfast.ccmenu.*;
import bp.wf.*;
import net.sf.json.JSONObject;

import java.util.*;
import java.io.*;

/**
 页面功能实体
*/
public class CCMobile extends bp.difference.handler.DirectoryPageBase
{


	/**
	 构造函数
	*/
	public CCMobile() throws Exception {
		WebUser.setSheBei("Mobile");
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
		throw new RuntimeException("@标记[" + this.getDoType() + "]，没有找到.");
	}

		///#endregion 执行父类的重写方法.

	public final String Login_Init() throws Exception {
		bp.wf.httphandler.WF ace = new WF();
		return ace.Login_Init();
	}

	public final String Login_Submit() throws Exception {
		String userNo = this.GetRequestVal("TB_No");
		if (DataType.IsNullOrEmpty(userNo) == true)
		{
			userNo = this.GetRequestVal("TB_UserNo");
		}
		String pass = this.GetRequestVal("TB_PW");
		if (DataType.IsNullOrEmpty(pass) == true)
		{
			pass = this.GetRequestVal("TB_Pass");
		}
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			Emp saemp = null;
			if (DataType.IsNullOrEmpty(this.getOrgNo()) == false)
			{
				String empNo = this.getOrgNo() + "_" + userNo;
				saemp.setNo(empNo);
				if (saemp.RetrieveFromDBSources() == 0)
				{
					return "err@账号" + userNo + "在组织" + this.getOrgNo() + "还未注册.";
				}
			}
			else
			{
				//获取当前用户
				Emps emps = new Emps();
				emps.Retrieve(EmpAttr.UserID, userNo, EmpAttr.EmpSta, 1, null);
				if (emps.size() == 0)
				{
					return "err@还未注册该账号或该账号已经禁用.";
				}
				saemp = emps.get(0) instanceof Emp ? (Emp)emps.get(0) : null;
			}
			if (saemp.CheckPass(pass) == false)
			{
				return "err@用户名或者密码错误.";
			}
			String token = Dev2Interface.Port_GenerToken(saemp.getNo(),"PC");
			//调用登录方法.
			Dev2Interface.Port_Login(saemp.getUserID(), saemp.getOrgNo());
			return "url@Home.htm?Token=" + token + "&UserNo=" + saemp.getUserID();
		}
		Emp emp = new Emp();
		emp.setUserID(userNo);
		if (emp.RetrieveFromDBSources() == 0)
		{
			if (DBAccess.IsExitsTableCol("Port_Emp", "NikeName") == true)
			{
				/*如果包含昵称列,就检查昵称是否存在.*/
				Paras ps = new Paras();
				ps.SQL = "SELECT " + bp.sys.base.Glo.getUserNo() + " FROM Port_Emp WHERE NikeName=" + SystemConfig.getAppCenterDBVarStr() + "userNo";
				ps.Add("userNo", userNo, false);
				String no = DBAccess.RunSQLReturnStringIsNull(ps, null);
				if (no == null)
				{
					return "err@用户名或者密码错误.";
				}

				emp.setUserID(no);
				int i = emp.RetrieveFromDBSources();
				if (i == 0)
				{
					return "err@用户名或者密码错误.";
				}
			}
			else if (DBAccess.IsExitsTableCol("Port_Emp", "Tel") == true)
			{
				/*如果包含昵称列,就检查昵称是否存在.*/
				Paras ps = new Paras();
				ps.SQL = "SELECT " + bp.sys.base.Glo.getUserNo() + " FROM Port_Emp WHERE Tel=" + SystemConfig.getAppCenterDBVarStr() + "userNo";
				ps.Add("userNo", userNo, false);
				//String sql = "SELECT No FROM Port_Emp WHERE NikeName='" + userNo + "'";
				String no = DBAccess.RunSQLReturnStringIsNull(ps, null);
				if (no == null)
				{
					return "err@用户名或者密码错误.";
				}

				emp.setUserID(no);
				int i = emp.RetrieveFromDBSources();
				if (i == 0)
				{
					return "err@用户名或者密码错误.";
				}
			}
			else
			{
				return "err@用户名或者密码错误.";
			}
		}

		if (emp.CheckPass(pass) == false)
		{
			return "err@用户名或者密码错误.";
		}

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{

			if (DBAccess.IsExitsTableCol("Port_Emp", "EmpSta") == true)
			{
				String sql = "SELECT EmpSta FROM Port_Emp WHERE No='" + emp.getNo() + "'";
				if (DBAccess.RunSQLReturnValInt(sql, 1) == 1)
				{
					return "err@该用户已经被禁用.";
				}
			}
			String token = Dev2Interface.Port_GenerToken(emp.getNo(),"PC");
			//调用登录方法.
			Dev2Interface.Port_Login(emp.getUserID());
			return "url@/FastMobilePortal/Default.htm?Token=" + token + "&UserNo=" + emp.getUserID();
		}
		//获得当前管理员管理的组织数量.
		OrgAdminers adminers = null;

		//查询他管理多少组织.
		adminers = new OrgAdminers();
		adminers.Retrieve(OrgAdminerAttr.FK_Emp, emp.getUserID(), null);
		if (adminers.size() == 0)
		{
			Orgs orgs = new Orgs();
			int i = orgs.Retrieve("Adminer", this.GetRequestVal("TB_No"), null);
			if (i == 0)
			{
				String token = Dev2Interface.Port_GenerToken(emp.getNo(),"PC");
				//调用登录方法.
				Dev2Interface.Port_Login(emp.getUserID(), emp.getOrgNo());
				return "url@/FastMobilePortal/Default.htm?Token=" + token + "&UserNo=" + emp.getUserID() + "&OrgNo=" + emp.getOrgNo();
			}

			for (Org org : orgs.ToJavaList())
			{
				OrgAdminer oa = new OrgAdminer();
				oa.setEmpNo(WebUser.getNo());
				oa.setOrgNo(org.getNo());
				oa.Save();
			}
			adminers.Retrieve(OrgAdminerAttr.FK_Emp, emp.getUserID(), null);
		}

		//设置他的组织，信息.
		WebUser.setNo(emp.getUserID()); //登录帐号.
		WebUser.setDeptNo(emp.getDeptNo());
		WebUser.setDeptName(emp.getDeptText());



		String token = Dev2Interface.Port_GenerToken(emp.getNo(),"PC");
		//执行登录.
		Dev2Interface.Port_Login(emp.getUserID(), emp.getOrgNo());

		//判断是否是多个组织的情况.
		if (adminers.size() == 1)
		{
			return "url@/FastMobilePortal/Default.htm?Token=" + token + "&UserNo=" + emp.getUserID() + "&OrgNo=" + emp.getOrgNo();
		}

		return "url@SelectOneOrg.htm?Token=" + token + "&UserNo=" + emp.getUserID() + "&OrgNo=" + emp.getOrgNo();
	}


	public final String Login_SubmitSingle() throws Exception {
		String userNo = this.GetRequestVal("TB_No");
		String pass = this.GetRequestVal("TB_PW");

		Emp emp = new Emp();
		emp.setNo(userNo);
		if (emp.RetrieveFromDBSources() == 0)
		{
			if (DBAccess.IsExitsTableCol("Port_Emp", "NikeName") == true)
			{
				/*如果包含昵称列,就检查昵称是否存在.*/
				Paras ps = new Paras();
				ps.SQL = "SELECT " + bp.sys.base.Glo.getUserNo() + " FROM Port_Emp WHERE NikeName=" + SystemConfig.getAppCenterDBVarStr() + "userNo";
				ps.Add("userNo", userNo, false);
				//String sql = "SELECT No FROM Port_Emp WHERE NikeName='" + userNo + "'";
				String no = DBAccess.RunSQLReturnStringIsNull(ps, null);
				if (no == null)
				{
					return "err@用户名或者密码错误.";
				}

				emp.setUserID(no);
				int i = emp.RetrieveFromDBSources();
				if (i == 0)
				{
					return "err@用户名或者密码错误.";
				}
			}
			else if (DBAccess.IsExitsTableCol("Port_Emp", "Tel") == true)
			{
				/*如果包含昵称列,就检查昵称是否存在.*/
				Paras ps = new Paras();
				ps.SQL = "SELECT " + bp.sys.base.Glo.getUserNo() + " FROM Port_Emp WHERE Tel=" + SystemConfig.getAppCenterDBVarStr() + "userNo";
				ps.Add("userNo", userNo, false);
				//String sql = "SELECT No FROM Port_Emp WHERE NikeName='" + userNo + "'";
				String no = DBAccess.RunSQLReturnStringIsNull(ps, null);
				if (no == null)
				{
					return "err@用户名或者密码错误.";
				}

				emp.setUserID(no);
				int i = emp.RetrieveFromDBSources();
				if (i == 0)
				{
					return "err@用户名或者密码错误.";
				}
			}
			else
			{
				return "err@用户名或者密码错误.";
			}
		}

		if (emp.CheckPass(pass) == false)
		{
			return "err@用户名或者密码错误.";
		}

		//调用登录方法.
		Dev2Interface.Port_Login(emp.getUserID());


		return "登录成功.";
	}
	/**
	 会签列表

	 @return
	*/
	public final String HuiQianList_Init()
	{
		WF wf = new WF();
		return wf.HuiQianList_Init();
	}

	public final String GetUserInfo()
	{
		if (WebUser.getNo() == null)
		{
			return "{err:'nologin'}";
		}

		StringBuilder append = new StringBuilder();
		append.append("{");
		String userPath = SystemConfig.getPathOfWebApp() + "DataUser/UserIcon/";
		String userIcon = userPath + WebUser.getNo() + "Biger.png";
		if ((new File(userIcon)).isFile())
		{
			append.append("UserIcon:'" + WebUser.getNo() + "Biger.png'");
		}
		else
		{
			append.append("UserIcon:'DefaultBiger.png'");
		}
		append.append(",UserName:'" + WebUser.getName() + "'");
		append.append(",UserDeptName:'" + WebUser.getDeptName() + "'");
		append.append("}");
		return append.toString();
	}
	public final String StartGuide_MulitSend() throws Exception {
		WF_MyFlow en = new WF_MyFlow();
		return en.StartGuide_MulitSend();
	}
	public final String Home_Init() throws Exception {
		Hashtable ht = new Hashtable();
		ht.put("UserNo", WebUser.getNo());
		ht.put("UserName", WebUser.getName());

		//系统名称.
		ht.put("SysName", SystemConfig.getSysName());
		ht.put("CustomerName", SystemConfig.getCustomerName());

		ht.put("Todolist_EmpWorks", Dev2Interface.getTodolistEmpWorks());
		ht.put("Todolist_Runing", Dev2Interface.getTodolistRuning());
		ht.put("Todolist_Complete", Dev2Interface.getTodolistComplete());
		ht.put("Todolist_CCWorks", Dev2Interface.getTodolistCCWorks());

		ht.put("Todolist_HuiQian", Dev2Interface.getTodolistHuiQian()); //会签数量.
		ht.put("Todolist_Drafts", Dev2Interface.getTodolistDraft()); //会签数量.

		return bp.tools.Json.ToJsonEntityModel(ht);
	}
	public final String Default_Init() throws Exception {
		DataSet ds = new DataSet();
		//获取最近发起的流程
		String sql = "";
		int top = GetRequestValInt("Top");
		if (top == 0)
		{
			top = 8;
		}

		switch (SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
				sql = " SELECT TOP " + top + "  FK_Flow,F.Name as FlowName,F.Icon FROM WF_GenerWorkFlow G ,WF_Flow F WHERE  F.IsCanStart=1 AND F.No=G.FK_Flow AND Starter='" + WebUser.getNo() + "' ORDER By SendDT DESC";
				break;
			case MySQL:
			case PostgreSQL:
			case UX:
			case HGDB:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql = "SELECT  FK_Flow,F.Name as FlowName,F.Icon FROM WF_GenerWorkFlow G ,WF_Flow F WHERE  F.IsCanStart=1 AND F.No=G.FK_Flow AND Starter='" + WebUser.getNo() + "'  Group By FK_Flow,F.Name,F.Icon  Order By Max(SendDT) DESC limit " + top;
				break;
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				sql = " SELECT * FROM (SELECT DISTINCT FK_Flow as \"FK_Flow\",F.Name as \"FlowName\",F.Icon FROM WF_GenerWorkFlow G ,WF_Flow F WHERE F.IsCanStart=1 AND F.No=G.FK_Flow AND Starter='" + WebUser.getNo() + "' GROUP BY FK_Flow,FlowName,ICON Order By SendDT DESC) WHERE  rownum <=" + top;
				break;
			default:
				throw new RuntimeException("err@系统暂时还未开发使用" + SystemConfig.getAppCenterDBType() + "数据库");
		}
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.setTableName("Flows");
		//把列名转化成区分大小写.
		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("ICON").ColumnName = "Icon";
		}
		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("icon").ColumnName = "Icon";
		}
		ds.Tables.add(dt);
		//应用中心
		MySystems systems = new MySystems();
		systems.RetrieveAll();
		MySystems systemsCopy = new MySystems();
		//权限中心.
		PowerCenters pcs = new PowerCenters();
		pcs.RetrieveAll();
		String mydepts = "" + WebUser.getDeptNo() + ","; //我的部门.
		String mystas = ""; //我的角色.
		DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.getUserID() + "'");
		for (DataRow dr : mydeptsDT.Rows)
		{
			mydepts += dr.getValue(0).toString() + ",";
			mystas += dr.getValue(1).toString() + ",";
		}

		//首先解决系统的权限.
		String ids = "";
		for (MySystem item : systems.ToJavaList())
		{
			//如果被禁用了.
			if (item.getItIsEnable() == false)
			{
				continue;
			}

			//找到关于系统的控制权限集合.
			bp.en.Entities tempVar = pcs.GetEntitiesByKey(PowerCenterAttr.CtrlPKVal, item.getNo());
			PowerCenters mypcs = tempVar instanceof PowerCenters ? (PowerCenters)tempVar : null;
			//如果没有权限控制的描述，就默认有权限.
			if (mypcs == null)
			{
				systemsCopy.AddEntity(item);
				continue;
			}

			//控制遍历权限.
			for (PowerCenter pc : mypcs.ToJavaList())
			{
				if (pc.getCtrlModel().equals("Anyone") == true)
				{
					systemsCopy.AddEntity(item);
					break;
				}
				if (pc.getCtrlModel().equals("Adminer") == true && WebUser.getNo().equals("admin") == true)
				{
					systemsCopy.AddEntity(item);
					break;
				}

				if (pc.getCtrlModel().equals("AdminerAndAdmin2") == true && WebUser.getIsAdmin() == true)
				{
					systemsCopy.AddEntity(item);
					break;
				}
				ids = "," + pc.getIDs() + ",";
				if (pc.getCtrlModel().equals("Emps") == true && ids.contains("," + WebUser.getNo() + ",") == true)
				{
					systemsCopy.AddEntity(item);
					break;
				}

				//是否包含部门？
				if (pc.getCtrlModel().equals("Depts") == true && DataType.IsHaveIt(pc.getIDs(), mydepts) == true)
				{
					systemsCopy.AddEntity(item);
					break;
				}

				//是否包含角色？
				if (pc.getCtrlModel().equals("Stations") == true && DataType.IsHaveIt(pc.getIDs(), mystas) == true)
				{
					systemsCopy.AddEntity(item);
					break;
				}

				//SQL？
				if (pc.getCtrlModel().equals("SQL") == true)
				{
					sql = bp.wf.Glo.DealExp(pc.getIDs(), null, "");
					if (DBAccess.RunSQLReturnValFloat(sql) > 0)
					{
						systemsCopy.AddEntity(item);
					}
					break;
				}
			}
		}
		DataTable sysDt = systemsCopy.ToDataTableField("Systems");
		//把列名转化成区分大小写.
		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			sysDt.Columns.get("NO").ColumnName = "No";
			sysDt.Columns.get("NAME").ColumnName = "Name";
			sysDt.Columns.get("ISENABLE").ColumnName = "IsEnable";
			sysDt.Columns.get("ICON").ColumnName = "Icon";
			sysDt.Columns.get("ORGNO").ColumnName = "OrgNo";
			sysDt.Columns.get("IDX").ColumnName = "Idx";
		}
		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			sysDt.Columns.get("no").ColumnName = "No";
			sysDt.Columns.get("name").ColumnName = "Name";
			sysDt.Columns.get("isenable").ColumnName = "IsEnable";
			sysDt.Columns.get("icon").ColumnName = "Icon";
			sysDt.Columns.get("orgno").ColumnName = "OrgNo";
			sysDt.Columns.get("idx").ColumnName = "Idx";
		}
		ds.Tables.add(sysDt);
		return bp.tools.Json.ToJson(ds);
	}
	/**
	 查询

	 @return
	*/
	public final String Home_Init_WorkCount()
	{
		Paras ps = new Paras();
		ps.SQL = "SELECT  TSpan as No, '' as Name, COUNT(WorkID) as Num, FROM WF_GenerWorkFlow WHERE Emps LIKE '%" + SystemConfig.getAppCenterDBVarStr() + "Emps%' GROUP BY TSpan";
		ps.Add("Emps", WebUser.getNo(), false);
		//String sql = "SELECT  TSpan as No, '' as Name, COUNT(WorkID) as Num, FROM WF_GenerWorkFlow WHERE Emps LIKE '%" + WebUser.getNo() + "%' GROUP BY TSpan";
		DataSet ds = new DataSet();
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		ds.Tables.add(dt);

		dt.Columns.get(0).ColumnName = "TSpan";
		dt.Columns.get(1).ColumnName = "Num";

		String sql = "SELECT IntKey as No, Lab as Name FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey='TSpan'";
		DataTable dt1 = DBAccess.RunSQLReturnTable(sql);
		for (DataRow dr : dt.Rows)
		{
			for (DataRow mydr : dt1.Rows)
			{

			}
		}

		return bp.tools.Json.ToJson(dt);
	}
	public final String MyFlow_Init() throws Exception {
		bp.wf.httphandler.WF_MyFlow wfPage = new bp.wf.httphandler.WF_MyFlow();
		return wfPage.MyFlow_Init();
	}

	public final String Runing_Init() throws Exception {
		WF wfPage = new WF();
		return wfPage.Runing_Init();
	}

	/**
	 新版本.

	 @return
	*/
	public final String Todolist_Init() throws Exception {
		String fk_node = this.GetRequestVal("FK_Node");
		String showWhat = this.GetRequestVal("ShowWhat");
		String orderBy = this.GetRequestVal("OrderBy");
		DataTable dt = Dev2Interface.DB_GenerEmpWorksOfDataTable(WebUser.getNo(), this.getNodeID(), showWhat, null, null, orderBy);
		dt.Columns.Add("WFStateLabel");
		for (DataRow dr : dt.Rows)
		{
			int sta = Integer.parseInt(dr.getValue("WFState").toString());
			dr.setValue("WFStateLabel", "待办");
			if (sta == 5)
			{
				dr.setValue("WFStateLabel", "退回");
			}
			if (sta == 4)
			{
				String atpara = dr.getValue("AtPara").toString();
				if (atpara.contains("@HungupSta=2") == true)
				{
					dr.setValue("WFStateLabel", "拒绝挂起");
				}
				if (atpara.contains("@HungupSta=1") == true)
				{
					dr.setValue("WFStateLabel", "同意挂起");
				}
				if (atpara.contains("@HungupSta=0") == true)
				{
					dr.setValue("WFStateLabel", "挂起等待审批");
				}
			}

		}


		return bp.tools.Json.ToJson(dt);
	}
	/**
	 查询已完成.

	 @return
	*/
	public final String Complete_Init()
	{
		DataTable dt = null;
		dt = Dev2Interface.DB_FlowComplete();
		return bp.tools.Json.ToJson(dt);
	}
	public final String DB_GenerReturnWorks()
	{
		/* 如果工作节点退回了*/
		String flowNo = DBAccess.RunSQLReturnString("SELECT FK_Flow From WF_Node WHERE NodeID=" + this.getNodeID());
		String trackTable = "ND" + Integer.parseInt(flowNo) + "Track";
		String sql = "SELECT NDFrom,NDFromT,EmpFrom,EmpFromT,Msg,RDT From " + trackTable + " WHERE WorkID=" + this.getWorkID() + " AND NDTo=" + this.getNodeID() + " And ActionType=2 Order By RDT";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "ReturnNode";
			dt.Columns.get(1).ColumnName = "ReturnNodeName";
			dt.Columns.get(2).ColumnName = "Returner";
			dt.Columns.get(3).ColumnName = "ReturnerName";
			dt.Columns.get(4).ColumnName = "BeiZhu";
			dt.Columns.get(5).ColumnName = "RDT";
		}
		return bp.tools.Json.ToJson(dt);
	}

	public final String Start_Init() throws Exception {
		WF wfPage = new WF();
		return wfPage.Start_Init();
	}

	public final String HandlerMapExt() throws Exception {
		WF_CCForm en = new WF_CCForm();
		return en.HandlerMapExt();
	}

	/**
	 打开手机端

	 @return
	*/
	public final String Do_OpenFlow() throws Exception {
		String sid = this.GetRequestVal("Token");
		String[] strs = sid.split("_");
		if(strs.length != 2){
			return "err@执行接口[Do_OpenFlow]出现异常，您的Token参数不正确请按照'用户名_WorkID'规则传参";
		}
		GenerWorkerList wl = new GenerWorkerList();
		int i = wl.Retrieve(GenerWorkerListAttr.FK_Emp, strs[0], GenerWorkerListAttr.WorkID, strs[1], GenerWorkerListAttr.IsPass, 0);

		if (i == 0)
		{
			return "err@提示:此工作已经被别人处理或者此流程已删除。";
		}

		Emp empOF = new Emp(wl.getEmpNo());
		bp.web.WebUser.SignInOfGener(empOF, "CH", false, false, null, null);
		return "url@CCMobileMyFlow.htm?FK_Flow=" + this.getFlowNo() + "&WorkID=" + this.getWorkID()  + "&FK_Node=" + wl.getNodeID() + "&FID=" + wl.getFID();
		//return "MyFlow.htm?FK_Flow=" + wl.getFlowNo() + "&WorkID=" + wl.getWorkID() + "&FK_Node=" + wl.getNodeID() + "&FID=" + wl.getFID();
	}
	/**
	 流程单表单查看.

	 @return json
	*/
	public final String FrmView_Init() throws Exception {
		WF wf = new WF();
		return wf.FrmView_Init();
	}
	/**
	 撤销发送

	 @return
	*/
	public final String FrmView_UnSend()
	{
		WF_WorkOpt_OneWork en = new WF_WorkOpt_OneWork();
		return en.OP_UnSend();
	}

	public final String AttachmentUpload_Down() throws Exception {
		WF_CCForm ccform = new WF_CCForm();
		return ccform.AttachmentUpload_Down();
	}

	public final String AttachmentUpload_DownByStream() throws Exception {
		WF_CCForm ccform = new WF_CCForm();
		return ccform.AttachmentUpload_DownByStream();
	}


		///#region 关键字查询.

	/**
	 执行查询

	 @return
	*/
	public final String SearchKey_Query() throws Exception {
		WF_RptSearch search = new WF_RptSearch();
		return search.KeySearch_Query();
	}

		///#endregion 关键字查询.


		///#region 查询.
	/**
	 初始化

	 @return
	*/
	public final String Search_Init() throws Exception {
		DataSet ds = new DataSet();
		String sql = "";

		String sqlOrgNoWhere = "";
		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			sqlOrgNoWhere = " AND OrgNo='" + WebUser.getOrgNo() + "'";
		}

		String tSpan = this.GetRequestVal("TSpan");
		if (Objects.equals(tSpan, ""))
		{
			tSpan = null;
		}


			///#region 1、获取时间段枚举/总数.
		SysEnums ses = new SysEnums("TSpan");
		DataTable dtTSpan = ses.ToDataTableField("dt");
		dtTSpan.setTableName("TSpan");
		ds.Tables.add(dtTSpan);

		if (this.getFlowNo() == null)
		{
			sql = "SELECT  TSpan as No, COUNT(WorkID) as Num FROM WF_GenerWorkFlow WHERE (Emps LIKE '%" + WebUser.getNo() + "%' OR Starter='" + WebUser.getNo() + "') AND WFState > 1 " + sqlOrgNoWhere + "  GROUP BY TSpan";
		}
		else
		{
			sql = "SELECT  TSpan as No, COUNT(WorkID) as Num FROM WF_GenerWorkFlow WHERE FK_Flow='" + this.getFlowNo() + "' AND (Emps LIKE '%" + WebUser.getNo() + "%' OR Starter='" + WebUser.getNo() + "')  AND WFState > 1  " + sqlOrgNoWhere + " GROUP BY TSpan";
		}

		DataTable dtTSpanNum = DBAccess.RunSQLReturnTable(sql);
		for (DataRow drEnum : dtTSpan.Rows)
		{
			String no = drEnum.getValue("IntKey").toString();
			for (DataRow dr : dtTSpanNum.Rows)
			{
				if (Objects.equals(dr.getValue("No").toString(), no))
				{
					drEnum.setValue("Lab", drEnum.get("Lab").toString() + "(" + dr.getValue("Num") + ")");
					break;
				}
			}
		}

			///#endregion


			///#region 2、处理流程类别列表.
		if (Objects.equals(tSpan, "-1"))
		{
			sql = "SELECT  FK_Flow as No, FlowName as Name, COUNT(WorkID) as Num FROM WF_GenerWorkFlow WHERE (Emps LIKE '%" + WebUser.getNo() + "%' OR TodoEmps LIKE '%" + WebUser.getNo() + ",%' OR Starter='" + WebUser.getNo() + "')  AND WFState > 1 AND FID = 0  " + sqlOrgNoWhere + " GROUP BY FK_Flow, FlowName";
		}
		else
		{
			sql = "SELECT  FK_Flow as No, FlowName as Name, COUNT(WorkID) as Num FROM WF_GenerWorkFlow WHERE TSpan=" + tSpan + " AND (Emps LIKE '%" + WebUser.getNo() + "%' OR TodoEmps LIKE '%" + WebUser.getNo() + ",%' OR Starter='" + WebUser.getNo() + "')  AND WFState > 1 AND FID = 0  " + sqlOrgNoWhere + " GROUP BY FK_Flow, FlowName";
		}

		DataTable dtFlows = DBAccess.RunSQLReturnTable(sql);

		dtFlows.Columns.get(0).ColumnName = "No";
		dtFlows.Columns.get(1).ColumnName = "Name";
		dtFlows.Columns.get(2).ColumnName = "Num";

		dtFlows.setTableName("Flows");
		ds.Tables.add(dtFlows);

			///#endregion


			///#region 3、处理流程实例列表.
		GenerWorkFlows gwfs = new GenerWorkFlows();
		String sqlWhere = "";
		sqlWhere = "(1 = 1)AND (((Emps LIKE '%" + WebUser.getNo() + "%')OR(TodoEmps LIKE '%" + WebUser.getNo() + "%')OR(Starter = '" + WebUser.getNo() + "')) AND (WFState > 1) " + sqlOrgNoWhere;
		if (!Objects.equals(tSpan, "-1"))
		{
			sqlWhere += "AND (TSpan = '" + tSpan + "') ";
		}

		if (this.getFlowNo() != null)
		{
			sqlWhere += "AND (FK_Flow = '" + this.getFlowNo() + "')) ";
		}
		else
		{
			sqlWhere += ")";
		}
		sqlWhere += "ORDER BY RDT DESC";

		if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6  || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			sql = "SELECT NVL(WorkID, 0) WorkID,NVL(FID, 0) FID ,FK_Flow,FlowName,Title, NVL(WFSta, 0) WFSta,WFState,  Starter, StarterName,Sender,NVL(RDT, '2018-05-04 19:29') RDT,NVL(FK_Node, 0) FK_Node,NodeName, TodoEmps FROM (select * from WF_GenerWorkFlow where " + sqlWhere + ") where rownum <= 500";
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
		{
			sql = "SELECT  TOP 500 ISNULL(WorkID, 0) WorkID,ISNULL(FID, 0) FID ,FK_Flow,FlowName,Title, ISNULL(WFSta, 0) WFSta,WFState,  Starter, StarterName,Sender,ISNULL(RDT, '2018-05-04 19:29') RDT,ISNULL(FK_Node, 0) FK_Node,NodeName, TodoEmps FROM WF_GenerWorkFlow where " + sqlWhere;
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.MySQL  || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL  || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			sql = "SELECT IFNULL(WorkID, 0) WorkID,IFNULL(FID, 0) FID ,FK_Flow,FlowName,Title, IFNULL(WFSta, 0) WFSta,WFState,  Starter, StarterName,Sender,IFNULL(RDT, '2018-05-04 19:29') RDT,IFNULL(FK_Node, 0) FK_Node,NodeName, TodoEmps FROM WF_GenerWorkFlow where " + sqlWhere + " LIMIT 500";
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX)
		{
			sql = "SELECT COALESCE(WorkID, 0) WorkID,COALESCE(FID, 0) FID ,FK_Flow,FlowName,Title, COALESCE(WFSta, 0) WFSta,WFState,  Starter, StarterName,Sender,COALESCE(RDT, '2018-05-04 19:29') RDT,COALESCE(FK_Node, 0) FK_Node,NodeName, TodoEmps FROM WF_GenerWorkFlow where " + sqlWhere + " LIMIT 500";
		}
		DataTable mydt = DBAccess.RunSQLReturnTable(sql);

		mydt.Columns.get(0).ColumnName = "WorkID";
		mydt.Columns.get(1).ColumnName = "FID";
		mydt.Columns.get(2).ColumnName = "FK_Flow";
		mydt.Columns.get(3).ColumnName = "FlowName";
		mydt.Columns.get(4).ColumnName = "Title";
		mydt.Columns.get(5).ColumnName = "WFSta";
		mydt.Columns.get(6).ColumnName = "WFState";
		mydt.Columns.get(7).ColumnName = "Starter";
		mydt.Columns.get(8).ColumnName = "StarterName";
		mydt.Columns.get(9).ColumnName = "Sender";
		mydt.Columns.get(10).ColumnName = "RDT";
		mydt.Columns.get(11).ColumnName = "FK_Node";
		mydt.Columns.get(12).ColumnName = "NodeName";
		mydt.Columns.get(13).ColumnName = "TodoEmps";

		mydt.setTableName("WF_GenerWorkFlow");
		if (mydt != null)
		{
			mydt.Columns.Add("TDTime");
			for (DataRow dr : mydt.Rows)
			{
				dr.setValue("TDTime", GetTraceNewTime(dr.getValue("FK_Flow").toString(), Integer.parseInt(dr.getValue("WorkID").toString()), Integer.parseInt(dr.getValue("FID").toString())));
			}
		}

			///#endregion


		ds.Tables.add(mydt);

		return bp.tools.Json.ToJson(ds);
	}
	public static String GetTraceNewTime(String fk_flow, long workid, long fid) throws Exception {

			///#region 获取track数据.
		String sqlOfWhere2 = "";
		String sqlOfWhere1 = "";
		String dbStr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		if (fid == 0)
		{
			sqlOfWhere1 = " WHERE (FID=" + dbStr + "WorkID11 OR WorkID=" + dbStr + "WorkID12 )  ";
			ps.Add("WorkID11", workid);
			ps.Add("WorkID12", workid);
		}
		else
		{
			sqlOfWhere1 = " WHERE (FID=" + dbStr + "FID11 OR WorkID=" + dbStr + "FID12 ) ";
			ps.Add("FID11", fid);
			ps.Add("FID12", fid);
		}

		String sql = "";
		sql = "SELECT MAX(RDT) FROM ND" + Integer.parseInt(fk_flow) + "Track " + sqlOfWhere1;
		sql = "SELECT RDT FROM  ND" + Integer.parseInt(fk_flow) + "Track  WHERE RDT=(" + sql + ")";
		ps.SQL = sql;

		try
		{
			return DBAccess.RunSQLReturnString(ps);
		}
		catch (java.lang.Exception e)
		{
			// 处理track表.
			Track.CreateOrRepairTrackTable(fk_flow);
			return DBAccess.RunSQLReturnString(ps);
		}

			///#endregion 获取track数据.
	}
	/**
	 查询

	 @return
	*/
	public final String Search_Search() throws Exception {
		String TSpan = this.GetRequestVal("TSpan");
		String FK_Flow = this.GetRequestVal("FK_Flow");

		GenerWorkFlows gwfs = new GenerWorkFlows();
		QueryObject qo = new QueryObject(gwfs);
		qo.AddWhere(GenerWorkFlowAttr.Emps, " LIKE ", "%" + WebUser.getNo() + "%");
		if (!DataType.IsNullOrEmpty(TSpan))
		{
			qo.addAnd();
			qo.AddWhere(GenerWorkFlowAttr.TSpan, this.GetRequestVal("TSpan"));
		}
		if (!DataType.IsNullOrEmpty(FK_Flow))
		{
			qo.addAnd();
			qo.AddWhere(GenerWorkFlowAttr.FK_Flow, this.GetRequestVal("FK_Flow"));
		}
		qo.Top = 50;

		if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			qo.DoQuery();
			DataTable dt = gwfs.ToDataTableField("Ens");
			return bp.tools.Json.ToJson(dt);
		}
		else
		{
			DataTable dt = qo.DoQueryToTable();
			return bp.tools.Json.ToJson(dt);
		}
	}

	/**
	 * 获取钉钉企业ID
	 * @return
	 */
	public String getDingCorpID(){
		return SystemConfig.getDing_CorpID();
	}
	/**
	 * 钉钉验证
	 *
	 * @return
	 * @throws Exception
	 */
	public String getDingTalkUserInfo()throws Exception {
		try {
			HashMap<String,Object> resultMap=new HashMap<>();
			String randomCode = this.GetRequestVal("rcode");
			//获取token
			String token = "";
			bp.da.Log.DefaultLogWriteLine(LogType.Info, "钉钉Code:" + randomCode);
			String webPath = "https://oapi.dingtalk.com/gettoken?appkey=" + SystemConfig.getDing_AppKey() + "&appsecret=" + SystemConfig.getDing_AppSecret();
			//增加header参数
			java.util.Map<String, String> headerMap = new Hashtable<String, String>();
			headerMap.put("Content-Type", "application/json");
			String tokenData = HttpClientUtil.doGet(webPath);
			JSONObject tokenJson = JSONObject.fromObject(tokenData);
			if (tokenJson.getString("errcode").equals("0"))
				token = tokenJson.getString("access_token");
			else
				return "err@:获取token出错";

			//获取人员信息
			String url = "https://oapi.dingtalk.com/user/getuserinfo?access_token=" + token + "&code=" + randomCode;
			String json = HttpClientUtil.doGet(url);
			bp.da.Log.DefaultLogWriteLine(LogType.Info, "钉钉JSON:" + json);
			JSONObject jd = JSONObject.fromObject(json);
			//根据userid获取userinfo
			if (!DataType.IsNullOrEmpty(jd.getString("userid"))) {
				String userUrl = "https://oapi.dingtalk.com/topapi/v2/user/get?access_token=" + token + "&userid=" + jd.getString("userid");
				String userData=HttpClientUtil.doGet(userUrl);
				JSONObject userInfo = JSONObject.fromObject(userData);
				if (userInfo.getString("errcode").equals("0")) {
					JSONObject userResult = userInfo.getJSONObject("result");
					String tel = userResult.getString("mobile");
					bp.da.Log.DefaultLogWriteLine(LogType.Info, "钉钉mobile:" + tel);
					bp.wf.weixin.Emp emp=new bp.wf.weixin.Emp();
					if (emp.IsExit(EmpAttr.Tel, tel)) {
						String userToken="";
						if(SystemConfig.getCCBPMRunModel()== CCBPMRunModel.SAAS){
							bp.wf.Dev2Interface.Port_Login(emp.getNo());
							userToken=bp.wf.Dev2Interface.Port_GenerToken();
						}else{
							userToken=bp.wf.Dev2Interface.Port_GenerToken(emp.getNo());
							bp.wf.Dev2Interface.Port_Login(emp.getNo());
						}
						bp.da.Log.DefaultLogWriteLine(LogType.Info, "userToken:" + userToken);
						return userToken;
					}
					else if(emp.IsExit(EmpAttr.No, jd.getString("userid"))){
						String userToken="";
						if(SystemConfig.getCCBPMRunModel()== CCBPMRunModel.SAAS){
							bp.wf.Dev2Interface.Port_Login(emp.getNo());
							userToken=bp.wf.Dev2Interface.Port_GenerToken();
						}else{
							userToken=bp.wf.Dev2Interface.Port_GenerToken(emp.getNo());
							bp.wf.Dev2Interface.Port_Login(emp.getNo());
						}
						bp.da.Log.DefaultLogWriteLine(LogType.Info, "userToken1:" + userToken);
						return userToken;
					}
				} else
					return "err@:获取用户信息出错";
			} else {
				bp.wf.weixin.Emp emp=new bp.wf.weixin.Emp();
				if (emp.IsExit(EmpAttr.UserID, jd.getString("userid"))) {
					String userToken="";
					if(SystemConfig.getCCBPMRunModel()== CCBPMRunModel.SAAS){
						bp.wf.Dev2Interface.Port_Login(emp.getNo());
						userToken=bp.wf.Dev2Interface.Port_GenerToken();
					}else{
						userToken=bp.wf.Dev2Interface.Port_GenerToken(emp.getNo());
						bp.wf.Dev2Interface.Port_Login(emp.getNo());
					}
					bp.da.Log.DefaultLogWriteLine(LogType.Info, "userToken2:" + userToken);
					return userToken;
				}
				else if (emp.IsExit(EmpAttr.No, jd.getString("userid"))) {
					String userToken="";
					if(SystemConfig.getCCBPMRunModel()== CCBPMRunModel.SAAS){
						bp.wf.Dev2Interface.Port_Login(emp.getNo());
						userToken=bp.wf.Dev2Interface.Port_GenerToken();
					}else{
						userToken=bp.wf.Dev2Interface.Port_GenerToken(emp.getNo());
						bp.wf.Dev2Interface.Port_Login(emp.getNo());
					}
					bp.da.Log.DefaultLogWriteLine(LogType.Info, "userToken3:" + userToken);
					return userToken;
				}
			}
			System.out.println("返回信息:" +resultMap.toString());
			return bp.tools.Json.ToJson(resultMap);
		} catch (Exception ex) {
			System.out.println("钉钉请求异常信息:" +ex.toString());
			bp.da.Log.DefaultLogWriteLine(LogType.Info, "钉钉请求异常信息:" + ex.toString());
			return "err@:获取用户id出错";
		}
	}
	/**
	 * 企业微信免登
	 * @return
	 * @throws Exception
	 */
	public String Auto_Login() throws Exception {
		final String ERROR_USER_VALIDATION_FAILED = "err@用户验证失败";
		try {
			String code = this.GetRequestVal("dcode");
			String state = this.GetRequestVal("state"); // 执行标记.
			bp.da.Log.DebugWriteError("参数dcode[" + code + "],state[" + state + "]");


			// 获取 AccessToken
			String accessToken = WeiXinEntity.getAccessToken();
			bp.da.Log.DebugWriteError("token:" + accessToken);

			// 获取userid
			String userInfo = WeiXinEntity.getUserId(code, accessToken);
			bp.da.Log.DebugWriteError("用户信息:" + userInfo);

			JSONObject userObj = JSONObject.fromObject(userInfo);

			if(!userObj.has("userid")){
				return "err@企业微信获取用户信息失败，token=["+accessToken+"],userInfo=["+userInfo+"]";
			}

			String userIdValue = userObj.getString("userid");

			bp.wf.weixin.Emp emp = new bp.wf.weixin.Emp();
			// 判断帐号
			if (emp.IsExit(bp.port.EmpAttr.No, userIdValue)) {
				bp.wf.Dev2Interface.Port_Login(userIdValue);
				return bp.wf.Dev2Interface.Port_GenerToken();
			} else {
				// 判断userid
				if (emp.IsExit(bp.port.EmpAttr.UserID, userIdValue)) {
					bp.wf.Dev2Interface.Port_Login(emp.getNo());
					return bp.wf.Dev2Interface.Port_GenerToken();
				}
			}
			return ERROR_USER_VALIDATION_FAILED + "，用户[" + userIdValue + "] 不存在";
		} catch (Exception ex) {
			bp.da.Log.DebugWriteError(ex.getMessage());
			return "err@" + ex.getMessage();
		}
	}
		///#endregion
	/**
	 *  微信小程序通过Tel自动登录
	 *
	 * @return
	 * @throws Exception
	 */
	public String getWXAppletUserInfo()throws Exception {
		String Tel = this.GetRequestVal("Tel");
		try {
			//AES加密key
			String  key= SystemConfig.getIsEncryptionKey();
			Tel= AesEncodeUtil.decryptAES(Tel, key);
			HashMap<String,Object> resultMap=new HashMap<>();
			bp.wf.weixin.Emp emp=new bp.wf.weixin.Emp();
			if (emp.IsExit(EmpAttr.Tel, Tel)) {
				String userToken="";
				if(SystemConfig.getCCBPMRunModel()== CCBPMRunModel.SAAS){
					bp.wf.Dev2Interface.Port_Login(emp.getNo());
					userToken=bp.wf.Dev2Interface.Port_GenerToken();
				}else{
					bp.wf.Dev2Interface.Port_Login(emp.getNo());
					userToken=bp.wf.Dev2Interface.Port_GenerToken(emp.getNo());
				}
				return userToken;
			}
		 } catch (Exception ex) {
			return "err@获取用户出错"+ ex.getMessage();
		 }
		   return "err@无此用户:"+Tel;
	}

}
