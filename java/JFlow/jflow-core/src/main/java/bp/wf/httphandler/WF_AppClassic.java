package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.web.*;
import bp.port.*;
import bp.en.*;
import bp.wf.data.*;
import bp.difference.*;
import bp.wf.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;
import java.io.*;

/**
 页面功能实体
*/
public class WF_AppClassic extends bp.difference.handler.DirectoryPageBase
{
	/**
	 构造函数
	*/
	public WF_AppClassic()
	{
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

	/**
	 蓝信登录

	 @return
	*/
	public final String LanXin_Login() throws Exception {
		String code = GetRequestVal("code");

		if (DataType.IsNullOrEmpty(WebUser.getToken()) == false)
		{
			//刷新token
			String urlr = "http://xjtyjt.e.lanxin.cn:11180//sns/oauth2/refresh_token?refresh_token=" + WebUser.getToken() + "&appid=100243&grant_type=refresh_token";
			String resultr = bp.tools.HttpClientUtil.doPost(urlr);
			JSONObject jdr = JSONObject.fromObject(resultr);
			resultr = jdr.get("errcode").toString();
			if (Objects.equals(resultr, "0"))
			{
				WebUser.setToken(jdr.get("access_token").toString());
			}
			return WebUser.getNo();
		}


		//获取Token
		String url = "http://xjtyjt.e.lanxin.cn:11180/sns/oauth2/access_token?code=" + code + "&appid=100243&grant_type=authorization_code";
		String result = bp.tools.HttpClientUtil.doPost(url);
		JSONObject jd = JSONObject.fromObject(result);
		result = jd.get("errcode").toString();
		if (!Objects.equals(result, "0"))
		{
			return "err@" + jd.get("errmsg").toString();
		}
		String access_token = jd.get("access_token").toString();
		String openId = jd.get("openid").toString();

		//获取用户信息
		url = "http://xjtyjt.e.lanxin.cn:11180/sns/userinfo?access_token=" + access_token + "&mobile=" + openId;
		result = bp.tools.HttpClientUtil.doPost(url);
		jd = JSONObject.fromObject(result);
		result = jd.get("errcode").toString();
		if (!Objects.equals(result, "0"))
		{
			return "err@" + jd.get("errmsg").toString();
		}
		JSONArray jsonArray  = JSONArray.fromObject(jd.get("openOrgMemberList"));
		jd = jsonArray.optJSONObject(0);
		String userNo = jd.get("serialNumber").toString();
		String tel = jd.get("mobile").toString();

		/**单点登录*/
		Paras ps = new Paras();
		ps.SQL = "SELECT No FROM Port_Emp WHERE No=" + SystemConfig.getAppCenterDBVarStr() + "No and Tel=" + SystemConfig.getAppCenterDBVarStr() + "Tel";
		ps.Add("No", userNo, false);
		ps.Add("Tel", tel, false);
		String No = DBAccess.RunSQLReturnString(ps);
		if (DataType.IsNullOrEmpty(No))
		{
			return "err@用户信息不正确，请联系管理员";
		}

		Dev2Interface.Port_Login(userNo);
		WebUser.setToken(access_token);
		result = jd.get("errcode").toString();
		return userNo;
	}

	/**
	 初始化Home

	 @return
	*/
	public final String Home_Init() throws Exception {
		Hashtable ht = new Hashtable();
		ht.put("UserNo", WebUser.getNo());
		ht.put("UserName", WebUser.getName());

		//系统名称.
		ht.put("SysName", SystemConfig.getSysName());
		ht.put("CustomerName", SystemConfig.getCustomerName());

		ht.put("Todolist_EmpWorks", Dev2Interface.getTodolistEmpWorks());
		ht.put("Todolist_Runing", Dev2Interface.getTodolistRuning());
		ht.put("Todolist_Sharing", Dev2Interface.getTodolistSharing());
		ht.put("Todolist_CCWorks", Dev2Interface.getTodolistCCWorks());
		ht.put("Todolist_Apply", Dev2Interface.getTodolistApply()); //申请下来的任务个数.
		ht.put("Todolist_Draft", Dev2Interface.getTodolistDraft()); //草稿数量.
		ht.put("Todolist_Complete", Dev2Interface.getTodolistComplete()); //完成数量.
		ht.put("UserDeptName", WebUser.getDeptName());

		//我发起
		MyStartFlows myStartFlows = new MyStartFlows();
		QueryObject obj = new QueryObject(myStartFlows);
		obj.AddWhere(MyStartFlowAttr.Starter, WebUser.getNo());
		obj.addAnd();
		//运行中\已完成\挂起\退回\转发\加签\批处理\
		obj.addLeftBracket();
		obj.AddWhere("WFState=2 or WFState=3 or WFState=4 or WFState=5 or WFState=6 or WFState=8 or WFState=10");
		obj.addRightBracket();
		obj.DoQuery();
		ht.put("Todolist_MyStartFlow", myStartFlows.size());

		//我参与
		MyJoinFlows myFlows = new MyJoinFlows();
		obj = new QueryObject(myFlows);
		obj.AddWhere("Emps like '%" + WebUser.getNo() + "%'");
		obj.DoQuery();
		ht.put("Todolist_MyFlow", myFlows.size());

		return bp.tools.Json.ToJsonEntityModel(ht);
	}
	public final String Index_Init() throws Exception {
		Hashtable ht = new Hashtable();
		ht.put("Todolist_Runing", Dev2Interface.getTodolistRuning()); //运行中.
		ht.put("Todolist_EmpWorks", Dev2Interface.getTodolistEmpWorks()); //待办
		ht.put("Todolist_CCWorks", Dev2Interface.getTodolistCCWorks()); //抄送.

		//本周.
		ht.put("TodayNum", Dev2Interface.getTodolistCCWorks()); //抄送.

		return bp.tools.Json.ToJsonEntityModel(ht);
	}


		///#region 登录界面.
	public final String Portal_Login()
	{
		String userNo = this.GetRequestVal("UserNo");

		try
		{
			Emp emp = new Emp(userNo);

			Dev2Interface.Port_Login(emp.getUserID());
			return ".";
		}
		catch (Exception ex)
		{
			return "err@用户[" + userNo + "]登录失败." + ex.getMessage();
		}

	}
	/**
	 登录.

	 @return
	*/
	public final String Login_Submit()
	{
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

			Emp emp = new Emp();
			emp.setUserID(userNo);
			//是否存在用户
			boolean isExist = emp.RetrieveFromDBSources() == 0 ? false : true;
			if (isExist == false && DBAccess.IsExitsTableCol("Port_Emp", "NikeName") == true)
			{
				/*如果包含昵称列,就检查昵称是否存在.*/
				Paras ps = new Paras();
				ps.SQL = "SELECT No FROM Port_Emp WHERE NikeName=" + SystemConfig.getAppCenterDBVarStr() + "NikeName";
				ps.Add("NikeName", userNo, false);
				String no = DBAccess.RunSQLReturnStringIsNull(ps, null);
				if (DataType.IsNullOrEmpty(no) == false)
				{
					emp.setNo(no);
					if (emp.RetrieveFromDBSources() != 0)
					{
						isExist = true;
					}
				}
			}
			if (isExist == false && DBAccess.IsExitsTableCol("Port_Emp", "Tel") == true)
			{
				/*如果包含Name列,就检查Name是否存在.*/
				Paras ps = new Paras();
				ps.SQL = "SELECT No FROM Port_Emp WHERE Tel=" + SystemConfig.getAppCenterDBVarStr() + "Tel";
				ps.Add("Tel", userNo, false);
				String no = DBAccess.RunSQLReturnStringIsNull(ps, null);
				if (DataType.IsNullOrEmpty(no) == false)
				{
					emp.setNo(no);
					if (emp.RetrieveFromDBSources() != 0)
					{
						isExist = true;
					}
				}
			}
			if (isExist == false && DBAccess.IsExitsTableCol("Port_Emp", "Email") == true)
			{
				/*如果包含Name列,就检查Name是否存在.*/
				Paras ps = new Paras();
				ps.SQL = "SELECT No FROM Port_Emp WHERE Email=" + SystemConfig.getAppCenterDBVarStr() + "Email";
				ps.Add("Email", userNo, false);
				String no = DBAccess.RunSQLReturnStringIsNull(ps, null);
				if (DataType.IsNullOrEmpty(no) == false)
				{
					emp.setNo(no);
					if (emp.RetrieveFromDBSources() != 0)
					{
						isExist = true;
					}
				}
			}
			if (isExist == false)
			{
				return "err@用户名不存在.";
			}

			if (emp.CheckPass(pass) == false)
			{
				return "err@用户名或者密码错误.";
			}

			//调用登录方法.
			Dev2Interface.Port_GenerToken(emp.getNo());
			Dev2Interface.Port_Login(emp.getUserID());


			return "登录成功";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}


	/**
	 执行登录

	 @return
	*/
	public final String Login_Init() throws Exception {
		String doType = GetRequestVal("LoginType");
		if (DataType.IsNullOrEmpty(doType) == false && doType.equals("Out") == true)
		{
			//清空cookie
			WebUser.Exit();
		}


		if (this.getDoWhat() != null && this.getDoWhat().equals("Login") == true)
		{
			//调用登录方法.
			Dev2Interface.Port_Login(this.getUserNo(), this.getSID());
			return "url@Home.htm?UserNo=" + this.getUserNo();
			//this.Login_Submit();
			//return;
		}

		Hashtable ht = new Hashtable();
		ht.put("SysName", SystemConfig.getSysName());
		ht.put("ServiceTel", SystemConfig.getServiceTel());
		ht.put("CustomerName", SystemConfig.getCustomerName());
		if (WebUser.getNoOfRel() == null)
		{
			ht.put("UserNo", "");
			ht.put("UserName", "");
		}
		else
		{
			ht.put("UserNo", WebUser.getNo());

			String name = WebUser.getName();

			if (DataType.IsNullOrEmpty(name) == true)
			{
				ht.put("UserName", WebUser.getNo());
			}
			else
			{
				ht.put("UserName", name);
			}
		}

		return bp.tools.Json.ToJsonEntityModel(ht);
	}

		///#endregion 登录界面.


		///#region Welcome.htm 欢迎页面.
	public final String Welcome_Init() throws Exception {

		Hashtable ht = new Hashtable();
		// 待办.
		ht.put("Todlist", bp.wf.Dev2Interface.getTodolistEmpWorks()); //流程数

		//发起.
		ht.put("Start", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState > 1  AND Starter='" + WebUser.getNo() + "'")); //实例数.

		//逾期.
//		ht.put("OverTime", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState=3 AND WFSta=1 ")); //实例数.
		ht.put("OverTime", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState=2 AND WFSta=0 AND SDTOfNode < CURDATE() ")); //实例数.

		//退回数
		ht.put("ReturnNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState = 5  AND TodoEmps LIKE '%" + WebUser.getNo() + ",%'"));

		//草稿.
		ht.put("Darft", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState=1  AND Starter='" + WebUser.getNo() + "'"));

		//运行中.
		ht.put("Runing", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) as Num FROM WF_GenerWorkerlist WHERE IsPass!=0 AND FK_Emp='" + WebUser.getNo() + "'"));

		return bp.tools.Json.ToJson(ht);
	}
	/**
	 流程监控折线图数据获取

	 @return
	*/
	public final String Welcome_EchartDataSet() throws Exception {
		DataSet ds = new DataSet();

		String whereStr = "";
		String whereStrPuls = "";

		if (bp.wf.Glo.getCCBPMRunModel() != CCBPMRunModel.GroupInc)
		{
			whereStr += " WHERE OrgNo = '" + WebUser.getOrgNo() + "'";
			whereStrPuls += " AND OrgNo = '" + WebUser.getOrgNo() + "'";
		}


			///#region 完成的流程-按月分析
		//按期完成
		String sql = "SELECT FK_NY, count(WorkID) as Num FROM WF_GenerWorkFlow WHERE WFState=3 AND SendDT<=SDTOfNode And WFSta=1 " + whereStrPuls + " GROUP BY FK_NY ";
		DataTable ComplateFlowsByNY = DBAccess.RunSQLReturnTable(sql);
		ComplateFlowsByNY.TableName = "ComplateFlowsByNY";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			ComplateFlowsByNY.Columns.get(0).ColumnName = "FK_NY";
			ComplateFlowsByNY.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(ComplateFlowsByNY);

		//逾期完成
		sql = "SELECT FK_NY, count(WorkID) as Num FROM WF_GenerWorkFlow WHERE WFState=3 AND SendDT>SDTOfNode And WFSta=1 " + whereStrPuls + " GROUP BY FK_NY ";
		DataTable OverComplateFlowsByNY = DBAccess.RunSQLReturnTable(sql);
		OverComplateFlowsByNY.TableName = "OverComplateFlowsByNY";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			OverComplateFlowsByNY.Columns.get(0).ColumnName = "FK_NY";
			OverComplateFlowsByNY.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(OverComplateFlowsByNY);

			///#endregion 完成的流程-按月分析


			///#region 运行中的流程
		//按部门
		//1.全部待办
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			sql = "SELECT B.Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B WHERE A.FK_Dept=B.No GROUP BY B.Name";
		}
		else
		{
			sql = "SELECT B.Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B WHERE A.FK_Dept=B.No AND B.OrgNo='" + WebUser.getOrgNo() + "' GROUP BY B.Name";
		}
		DataTable TodoListAllByDept = DBAccess.RunSQLReturnTable(sql);
		TodoListAllByDept.TableName = "TodoListAllByDept";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListAllByDept.Columns.get(0).ColumnName = "Name";
			TodoListAllByDept.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListAllByDept);

		//2.退回的数据
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			sql = "SELECT B.Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B,WF_GenerWorkFlow C WHERE A.FK_Dept=B.No AND A.WorkID=C.WorkID AND C.WFState=5 GROUP BY B.Name";
		}
		else
		{
			sql = "SELECT B.Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B,WF_GenerWorkFlow C WHERE A.FK_Dept=B.No AND A.WorkID=C.WorkID AND C.WFState=5 AND B.OrgNo='" + WebUser.getOrgNo() + "' GROUP BY B.Name";
		}
		DataTable TodoListReturnByDept = DBAccess.RunSQLReturnTable(sql);
		TodoListReturnByDept.TableName = "TodoListReturnByDept";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListReturnByDept.Columns.get(0).ColumnName = "Name";
			TodoListReturnByDept.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListReturnByDept);

		//3.逾期的数据
		if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			sql = "SELECT B.Name, count(DISTINCT C.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B,WF_GenerWorkFlow C WHERE A.FK_Dept=B.No AND A.WorkID=C.WorkID  and STR_TO_DATE(A.SDT,'%Y-%m-%d %H:%i') <STR_TO_DATE(C.SDTOfNode,'%Y-%m-%d %H:%i') GROUP BY B.Name";

		}
		else if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			sql = "SELECT  B.Name, count(DISTINCT C.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B,WF_GenerWorkFlow C WHERE A.FK_Dept=B.No AND A.WorkID=C.WorkID  and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}') AND(TO_DATE(C.SDTOfNode, 'yyyy-mm-dd hh24:mi:ss') - TO_DATE(A.SDT, 'yyyy-mm-dd hh24:mi:ss')) > 0 GROUP BY B.Name ";
			sql += "UNION SELECT  B.Name, count(DISTINCT C.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B,WF_GenerWorkFlow C WHERE A.FK_Dept=B.No AND A.WorkID=C.WorkID and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2}$') AND (TO_DATE(SDTOfNode, 'yyyy-mm-dd') - TO_DATE(SDT, 'yyyy-mm-dd')) > 0 GROUP BY B.Name";
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB)
		{
			sql = "SELECT  B.Name, count(DISTINCT C.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B,WF_GenerWorkFlow C WHERE A.FK_Dept=B.No AND A.WorkID=C.WorkID  and to_timestamp(CASE WHEN A.SDT='无' THEN '' ELSE A.SDT END, 'yyyy-mm-dd hh24:MI:SS') < to_timestamp(CASE WHEN C.SDTOfNode='无' THEN '' ELSE C.SDTOfNode END, 'yyyy-mm-dd hh24:MI:SS') GROUP BY B.Name";
		}
		else
		{
			sql = "SELECT  B.Name, count(DISTINCT C.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B,WF_GenerWorkFlow C WHERE A.FK_Dept=B.No AND A.WorkID=C.WorkID  and convert(varchar(100),A.SDT,120) < CONVERT(varchar(100), C.SDTOfNode, 120) GROUP BY B.Name";
		}

		DataTable TodoListOverTByDept = DBAccess.RunSQLReturnTable(sql);
		TodoListOverTByDept.TableName = "TodoListOverTByDept";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListOverTByDept.Columns.get(0).ColumnName = "Name";
			TodoListOverTByDept.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListOverTByDept);

		//4.预警的数据
		//按流程

		//1.全部待办
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			sql = "SELECT B.Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_Flow B WHERE A.FK_Flow=B.No GROUP BY B.Name";
		}
		else
		{
			sql = "SELECT B.Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_Flow B WHERE A.FK_Flow=B.No AND B.OrgNo='" + WebUser.getOrgNo() + "' GROUP BY B.Name";
		}
		DataTable TodoListAllByFlow = DBAccess.RunSQLReturnTable(sql);
		TodoListAllByFlow.TableName = "TodoListAllByFlow";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListAllByFlow.Columns.get(0).ColumnName = "Name";
			TodoListAllByFlow.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListAllByFlow);

		//2.退回的数据
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			sql = "SELECT B.FlowName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Flow=B.FK_Flow AND A.WorkID=B.WorkID AND B.WFState=5 GROUP BY B.FlowName";
		}
		else
		{
			sql = "SELECT B.FlowName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Flow=B.FK_Flow AND A.WorkID=B.WorkID AND B.WFState=5 AND B.OrgNo='" + WebUser.getOrgNo() + "' GROUP BY B.FlowName";
		}
		DataTable TodoListReturnByFlow = DBAccess.RunSQLReturnTable(sql);
		TodoListReturnByFlow.TableName = "TodoListReturnByFlow";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListReturnByFlow.Columns.get(0).ColumnName = "Name";
			TodoListReturnByFlow.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListReturnByFlow);

		//3.逾期的数据
		if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			sql = "SELECT B.FlowName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Flow=B.FK_Flow AND A.WorkID=B.WorkID  and STR_TO_DATE(A.SDT,'%Y-%m-%d %H:%i') <STR_TO_DATE(B.SDTOfNode,'%Y-%m-%d %H:%i') GROUP BY B.FlowName";

		}
		else if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			sql = "SELECT  B.FlowName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Flow=B.FK_Flow AND A.WorkID=B.WorkID  and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}') AND(TO_DATE(B.SDTOfNode, 'yyyy-mm-dd hh24:mi:ss') - TO_DATE(A.SDT, 'yyyy-mm-dd hh24:mi:ss')) > 0 GROUP BY B.FlowName ";
			sql += "UNION SELECT  B.FlowName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Flow=B.FK_Flow AND A.WorkID=B.WorkID and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2}$') AND (TO_DATE(B.SDTOfNode, 'yyyy-mm-dd') - TO_DATE(SDT, 'yyyy-mm-dd')) > 0 GROUP BY B.FlowName";
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB)
		{
			sql = "SELECT B.FlowName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Flow=B.FK_Flow AND A.WorkID=B.WorkID  and to_timestamp(CASE WHEN A.SDT='无' THEN '' ELSE A.SDT END, 'yyyy-mm-dd hh24:MI:SS') < to_timestamp(CASE WHEN B.SDTOfNode='无' THEN '' ELSE B.SDTOfNode END, 'yyyy-mm-dd hh24:MI:SS') GROUP BY B.FlowName";
		}
		else
		{
			sql = "SELECT B.FlowName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Flow=B.FK_Flow AND A.WorkID=B.WorkID  and convert(varchar(100),A.SDT,120) < CONVERT(varchar(100), B.SDTOfNode, 120) GROUP BY B.FlowName";
		}

		DataTable TodoListOverTByFlow = DBAccess.RunSQLReturnTable(sql);
		TodoListOverTByFlow.TableName = "TodoListOverTByFlow";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListOverTByFlow.Columns.get(0).ColumnName = "Name";
			TodoListOverTByFlow.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListOverTByFlow);


		//按人员(仅限一个部门中的人员）
		//获取当前人员所在部门的所有人员
		sql = "SELECT A.No,A.Name From Port_Emp A,Port_DeptEmp B Where A.No=B.FK_Emp AND B.FK_Dept='" + WebUser.getDeptNo() + "' order By A.Idx";
		DataTable Emps = DBAccess.RunSQLReturnTable(sql);
		Emps.TableName = "Emps";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			Emps.Columns.get(0).ColumnName = "No";
			Emps.Columns.get(1).ColumnName = "Name";
		}
		ds.Tables.add(Emps);

		//1.全部待办
		sql = "SELECT EmpName AS Name, count(WorkID) as Num FROM WF_GenerWorkerlist WHERE FK_Dept='" + WebUser.getDeptNo() + "' GROUP BY EmpName";
		DataTable TodoListAllByEmp = DBAccess.RunSQLReturnTable(sql);
		TodoListAllByEmp.TableName = "TodoListAllByEmp";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListAllByEmp.Columns.get(0).ColumnName = "Name";
			TodoListAllByEmp.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListAllByEmp);

		//2.退回的数据
		sql = "SELECT A.EmpName AS Name, count( A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE  A.FK_Dept='" + WebUser.getDeptNo() + "' AND A.WorkID=B.WorkID AND B.WFState=5 GROUP BY A.EmpName";
		DataTable TodoListReturnByEmp = DBAccess.RunSQLReturnTable(sql);
		TodoListReturnByEmp.TableName = "TodoListReturnByEmp";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListReturnByEmp.Columns.get(0).ColumnName = "Name";
			TodoListReturnByEmp.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListReturnByEmp);

		//3.逾期的数据
		if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			sql = "SELECT A.EmpName AS Name, count( A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE  A.FK_Dept='" + WebUser.getDeptNo() + "' AND A.WorkID=B.WorkID  and STR_TO_DATE(A.SDT,'%Y-%m-%d %H:%i') <STR_TO_DATE(B.SDTOfNode,'%Y-%m-%d %H:%i') GROUP BY A.EmpName";

		}
		else if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			sql = "SELECT  A.EmpName AS Name, count( A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE  A.FK_Dept='" + WebUser.getDeptNo() + "' AND A.WorkID=B.WorkID  and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}') AND(TO_DATE(B.SDTOfNode, 'yyyy-mm-dd hh24:mi:ss') - TO_DATE(A.SDT, 'yyyy-mm-dd hh24:mi:ss')) > 0 GROUP BY A.EmpName ";
			sql += "UNION SELECT A.EmpName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Dept='" + WebUser.getDeptNo() + "' AND A.WorkID=B.WorkID and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2}$') AND (TO_DATE(B.SDTOfNode, 'yyyy-mm-dd') - TO_DATE(SDT, 'yyyy-mm-dd')) > 0 GROUP BY A.EmpName";
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB)
		{
			sql = "SELECT A.EmpName AS Name, count( A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Dept='" + WebUser.getDeptNo() + "' AND A.WorkID=B.WorkID  and to_timestamp(CASE WHEN A.SDT='无' THEN '' ELSE A.SDT END, 'yyyy-mm-dd hh24:MI:SS') < to_timestamp(CASE WHEN B.SDTOfNode='无' THEN '' ELSE B.SDTOfNode END, 'yyyy-mm-dd hh24:MI:SS') GROUP BY A.EmpName";
		}
		else
		{
			sql = "SELECT A.EmpName AS Name, count( A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Dept='" + WebUser.getDeptNo() + "' AND A.WorkID=B.WorkID  and convert(varchar(100),A.SDT,120) < CONVERT(varchar(100), B.SDTOfNode, 120) GROUP BY A.EmpName";
		}

		DataTable TodoListOverTByEmp = DBAccess.RunSQLReturnTable(sql);
		TodoListOverTByEmp.TableName = "TodoListOverTByEmp";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListOverTByEmp.Columns.get(0).ColumnName = "Name";
			TodoListOverTByEmp.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListOverTByEmp);

			///#endregion 运行中的流程

		return bp.tools.Json.ToJson(ds);
	}

		///#endregion



		///#region 流程监控
	/**
	 流程监控的基础数据

	 @return
	*/
	public final String Watchdog_Init()
	{
		String whereStr = "";
		String whereStrPuls = "";


		if (bp.wf.Glo.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			whereStr += " WHERE OrgNo = '" + WebUser.getOrgNo() + "'";
			whereStrPuls += " AND OrgNo = '" + WebUser.getOrgNo() + "'";

		}

		Hashtable ht = new Hashtable();
		ht.put("FlowNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(No) FROM WF_Flow " + whereStr)); //流程数

		//所有的实例数量.
		ht.put("FlowInstaceNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState >1 " + whereStrPuls)); //实例数.

		//所有的已完成数量.
		ht.put("FlowComplete", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState=3 AND WFSta=1 " + whereStrPuls)); //实例数.

		//所有的待办数量.
		ht.put("TodolistNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState=2 " + whereStrPuls));

		//退回数.
		ht.put("ReturnNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState=5 " + whereStrPuls));

		return bp.tools.Json.ToJson(ht);
	}
	/**
	 流程监控折线图数据获取

	 @return
	*/
	public final String Watchdog_EchartDataSet() throws Exception {
		DataSet ds = new DataSet();

		String whereStr = "";
		String whereStrPuls = "";

		if (bp.wf.Glo.getCCBPMRunModel() != CCBPMRunModel.GroupInc)
		{
			whereStr += " WHERE OrgNo = '" + WebUser.getOrgNo() + "'";
			whereStrPuls += " AND OrgNo = '" + WebUser.getOrgNo() + "'";
		}


			///#region 完成的流程-按月分析
		//按期完成
		String sql = "SELECT FK_NY, count(WorkID) as Num FROM WF_GenerWorkFlow WHERE WFState=3 AND SendDT<=SDTOfNode And WFSta=1 " + whereStrPuls + " GROUP BY FK_NY ";
		DataTable ComplateFlowsByNY = DBAccess.RunSQLReturnTable(sql);
		ComplateFlowsByNY.TableName = "ComplateFlowsByNY";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			ComplateFlowsByNY.Columns.get(0).ColumnName = "FK_NY";
			ComplateFlowsByNY.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(ComplateFlowsByNY);

		//逾期完成
		sql = "SELECT FK_NY, count(WorkID) as Num FROM WF_GenerWorkFlow WHERE WFState=3 AND SendDT>SDTOfNode And WFSta=1 " + whereStrPuls + " GROUP BY FK_NY ";
		DataTable OverComplateFlowsByNY = DBAccess.RunSQLReturnTable(sql);
		OverComplateFlowsByNY.TableName = "OverComplateFlowsByNY";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			OverComplateFlowsByNY.Columns.get(0).ColumnName = "FK_NY";
			OverComplateFlowsByNY.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(OverComplateFlowsByNY);

			///#endregion 完成的流程-按月分析


			///#region 运行中的流程
		//按部门
		//1.全部待办
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			sql = "SELECT B.Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B WHERE A.FK_Dept=B.No GROUP BY B.Name";
		}
		else
		{
			sql = "SELECT B.Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B WHERE A.FK_Dept=B.No AND B.OrgNo='" + WebUser.getOrgNo() + "' GROUP BY B.Name";
		}
		DataTable TodoListAllByDept = DBAccess.RunSQLReturnTable(sql);
		TodoListAllByDept.TableName = "TodoListAllByDept";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListAllByDept.Columns.get(0).ColumnName = "Name";
			TodoListAllByDept.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListAllByDept);

		//2.退回的数据
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			sql = "SELECT B.Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B,WF_GenerWorkFlow C WHERE A.FK_Dept=B.No AND A.WorkID=C.WorkID AND C.WFState=5 GROUP BY B.Name";
		}
		else
		{
			sql = "SELECT B.Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B,WF_GenerWorkFlow C WHERE A.FK_Dept=B.No AND A.WorkID=C.WorkID AND C.WFState=5 AND B.OrgNo='" + WebUser.getOrgNo() + "' GROUP BY B.Name";
		}
		DataTable TodoListReturnByDept = DBAccess.RunSQLReturnTable(sql);
		TodoListReturnByDept.TableName = "TodoListReturnByDept";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListReturnByDept.Columns.get(0).ColumnName = "Name";
			TodoListReturnByDept.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListReturnByDept);

		//3.逾期的数据
		if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			sql = "SELECT B.Name, count(DISTINCT C.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B,WF_GenerWorkFlow C WHERE A.FK_Dept=B.No AND A.WorkID=C.WorkID  and STR_TO_DATE(A.SDT,'%Y-%m-%d %H:%i') <STR_TO_DATE(C.SDTOfNode,'%Y-%m-%d %H:%i') GROUP BY B.Name";

		}
		else if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			sql = "SELECT  B.Name, count(DISTINCT C.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B,WF_GenerWorkFlow C WHERE A.FK_Dept=B.No AND A.WorkID=C.WorkID  and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}') AND(TO_DATE(C.SDTOfNode, 'yyyy-mm-dd hh24:mi:ss') - TO_DATE(A.SDT, 'yyyy-mm-dd hh24:mi:ss')) > 0 GROUP BY B.Name ";
			sql += "UNION SELECT  B.Name, count(DISTINCT C.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B,WF_GenerWorkFlow C WHERE A.FK_Dept=B.No AND A.WorkID=C.WorkID and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2}$') AND (TO_DATE(SDTOfNode, 'yyyy-mm-dd') - TO_DATE(SDT, 'yyyy-mm-dd')) > 0 GROUP BY B.Name";
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB)
		{
			sql = "SELECT  B.Name, count(DISTINCT C.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B,WF_GenerWorkFlow C WHERE A.FK_Dept=B.No AND A.WorkID=C.WorkID  and to_timestamp(CASE WHEN A.SDT='无' THEN '' ELSE A.SDT END, 'yyyy-mm-dd hh24:MI:SS') < to_timestamp(CASE WHEN C.SDTOfNode='无' THEN '' ELSE C.SDTOfNode END, 'yyyy-mm-dd hh24:MI:SS') GROUP BY B.Name";
		}
		else
		{
			sql = "SELECT  B.Name, count(DISTINCT C.WorkID) as Num FROM WF_GenerWorkerlist A,Port_Dept B,WF_GenerWorkFlow C WHERE A.FK_Dept=B.No AND A.WorkID=C.WorkID  and convert(varchar(100),A.SDT,120) < CONVERT(varchar(100), C.SDTOfNode, 120) GROUP BY B.Name";
		}

		DataTable TodoListOverTByDept = DBAccess.RunSQLReturnTable(sql);
		TodoListOverTByDept.TableName = "TodoListOverTByDept";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListOverTByDept.Columns.get(0).ColumnName = "Name";
			TodoListOverTByDept.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListOverTByDept);

		//4.预警的数据
		//按流程

		//1.全部待办
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			sql = "SELECT B.Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_Flow B WHERE A.FK_Flow=B.No GROUP BY B.Name";
		}
		else
		{
			sql = "SELECT B.Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_Flow B WHERE A.FK_Flow=B.No AND B.OrgNo='" + WebUser.getOrgNo() + "' GROUP BY B.Name";
		}
		DataTable TodoListAllByFlow = DBAccess.RunSQLReturnTable(sql);
		TodoListAllByFlow.TableName = "TodoListAllByFlow";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListAllByFlow.Columns.get(0).ColumnName = "Name";
			TodoListAllByFlow.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListAllByFlow);

		//2.退回的数据
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			sql = "SELECT B.FlowName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Flow=B.FK_Flow AND A.WorkID=B.WorkID AND B.WFState=5 GROUP BY B.FlowName";
		}
		else
		{
			sql = "SELECT B.FlowName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Flow=B.FK_Flow AND A.WorkID=B.WorkID AND B.WFState=5 AND B.OrgNo='" + WebUser.getOrgNo() + "' GROUP BY B.FlowName";
		}
		DataTable TodoListReturnByFlow = DBAccess.RunSQLReturnTable(sql);
		TodoListReturnByFlow.TableName = "TodoListReturnByFlow";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListReturnByFlow.Columns.get(0).ColumnName = "Name";
			TodoListReturnByFlow.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListReturnByFlow);

		//3.逾期的数据
		if (SystemConfig.getAppCenterDBType() == DBType.MySQL|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			sql = "SELECT B.FlowName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Flow=B.FK_Flow AND A.WorkID=B.WorkID  and STR_TO_DATE(A.SDT,'%Y-%m-%d %H:%i') <STR_TO_DATE(B.SDTOfNode,'%Y-%m-%d %H:%i') GROUP BY B.FlowName";

		}
		else if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			sql = "SELECT  B.FlowName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Flow=B.FK_Flow AND A.WorkID=B.WorkID  and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}') AND(TO_DATE(B.SDTOfNode, 'yyyy-mm-dd hh24:mi:ss') - TO_DATE(A.SDT, 'yyyy-mm-dd hh24:mi:ss')) > 0 GROUP BY B.FlowName ";
			sql += "UNION SELECT  B.FlowName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Flow=B.FK_Flow AND A.WorkID=B.WorkID and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2}$') AND (TO_DATE(B.SDTOfNode, 'yyyy-mm-dd') - TO_DATE(SDT, 'yyyy-mm-dd')) > 0 GROUP BY B.FlowName";
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB)
		{
			sql = "SELECT B.FlowName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Flow=B.FK_Flow AND A.WorkID=B.WorkID  and to_timestamp(CASE WHEN A.SDT='无' THEN '' ELSE A.SDT END, 'yyyy-mm-dd hh24:MI:SS') < to_timestamp(CASE WHEN B.SDTOfNode='无' THEN '' ELSE B.SDTOfNode END, 'yyyy-mm-dd hh24:MI:SS') GROUP BY B.FlowName";
		}
		else
		{
			sql = "SELECT B.FlowName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Flow=B.FK_Flow AND A.WorkID=B.WorkID  and convert(varchar(100),A.SDT,120) < CONVERT(varchar(100), B.SDTOfNode, 120) GROUP BY B.FlowName";
		}

		DataTable TodoListOverTByFlow = DBAccess.RunSQLReturnTable(sql);
		TodoListOverTByFlow.TableName = "TodoListOverTByFlow";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListOverTByFlow.Columns.get(0).ColumnName = "Name";
			TodoListOverTByFlow.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListOverTByFlow);


		//按人员(仅限一个部门中的人员）
		//获取当前人员所在部门的所有人员
		sql = "SELECT A.No,A.Name From Port_Emp A,Port_DeptEmp B Where A.No=B.FK_Emp AND B.FK_Dept='" + WebUser.getDeptNo() + "' order By A.Idx";
		DataTable Emps = DBAccess.RunSQLReturnTable(sql);
		Emps.TableName = "Emps";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			Emps.Columns.get(0).ColumnName = "No";
			Emps.Columns.get(1).ColumnName = "Name";
		}

		ds.Tables.add(Emps);

		//1.全部待办
		sql = "SELECT EmpName AS Name, count(WorkID) as Num FROM WF_GenerWorkerlist WHERE FK_Dept='" + WebUser.getDeptNo() + "' GROUP BY EmpName";
		DataTable TodoListAllByEmp = DBAccess.RunSQLReturnTable(sql);
		TodoListAllByEmp.TableName = "TodoListAllByEmp";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListAllByEmp.Columns.get(0).ColumnName = "Name";
			TodoListAllByEmp.Columns.get(1).ColumnName = "Num";
		}


		ds.Tables.add(TodoListAllByEmp);

		//2.退回的数据
		sql = "SELECT A.EmpName AS Name, count( A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE  A.FK_Dept='" + WebUser.getDeptNo() + "' AND A.WorkID=B.WorkID AND B.WFState=5 GROUP BY A.EmpName";
		DataTable TodoListReturnByEmp = DBAccess.RunSQLReturnTable(sql);
		TodoListReturnByEmp.TableName = "TodoListReturnByEmp";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListReturnByEmp.Columns.get(0).ColumnName = "Name";
			TodoListReturnByEmp.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListReturnByEmp);

		//3.逾期的数据
		if (SystemConfig.getAppCenterDBType() == DBType.MySQL|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			sql = "SELECT A.EmpName AS Name, count( A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE  A.FK_Dept='" + WebUser.getDeptNo() + "' AND A.WorkID=B.WorkID  and STR_TO_DATE(A.SDT,'%Y-%m-%d %H:%i') <STR_TO_DATE(B.SDTOfNode,'%Y-%m-%d %H:%i') GROUP BY A.EmpName";

		}
		else if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			sql = "SELECT  A.EmpName AS Name, count( A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE  A.FK_Dept='" + WebUser.getDeptNo() + "' AND A.WorkID=B.WorkID  and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}') AND(TO_DATE(B.SDTOfNode, 'yyyy-mm-dd hh24:mi:ss') - TO_DATE(A.SDT, 'yyyy-mm-dd hh24:mi:ss')) > 0 GROUP BY A.EmpName ";
			sql += "UNION SELECT A.EmpName AS Name, count(DISTINCT A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Dept='" + WebUser.getDeptNo() + "' AND A.WorkID=B.WorkID and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2}$') AND (TO_DATE(B.SDTOfNode, 'yyyy-mm-dd') - TO_DATE(SDT, 'yyyy-mm-dd')) > 0 GROUP BY A.EmpName";
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB)
		{
			sql = "SELECT A.EmpName AS Name, count( A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Dept='" + WebUser.getDeptNo() + "' AND A.WorkID=B.WorkID  and to_timestamp(CASE WHEN A.SDT='无' THEN '' ELSE A.SDT END, 'yyyy-mm-dd hh24:MI:SS') < to_timestamp(CASE WHEN B.SDTOfNode='无' THEN '' ELSE B.SDTOfNode END, 'yyyy-mm-dd hh24:MI:SS') GROUP BY A.EmpName";
		}
		else
		{
			sql = "SELECT A.EmpName AS Name, count( A.WorkID) as Num FROM WF_GenerWorkerlist A,WF_GenerWorkFlow B WHERE A.FK_Dept='" + WebUser.getDeptNo() + "' AND A.WorkID=B.WorkID  and convert(varchar(100),A.SDT,120) < CONVERT(varchar(100), B.SDTOfNode, 120) GROUP BY A.EmpName";
		}

		DataTable TodoListOverTByEmp = DBAccess.RunSQLReturnTable(sql);
		TodoListOverTByEmp.TableName = "TodoListOverTByEmp";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodoListOverTByEmp.Columns.get(0).ColumnName = "Name";
			TodoListOverTByEmp.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodoListOverTByEmp);
		///#endregion 运行中的流程
		return bp.tools.Json.ToJson(ds);
	}

		///#endregion 流程监控

}
