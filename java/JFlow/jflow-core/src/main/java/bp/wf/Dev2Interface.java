package bp.wf;

import bp.da.*;
import bp.en.*;
import bp.port.*;
import bp.port.Dept;
import bp.sys.*;
import bp.tools.*;
import bp.web.*;
import bp.wf.data.CHNode;
import bp.wf.template.*;
import bp.difference.*;
import bp.wf.template.sflow.*;
import bp.wf.port.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.concurrent.*;

/**
 此接口为程序员二次开发使用,在阅读代码前请注意如下事项.
 1, CCFlow 的对外的接口都是以静态方法来实现的.
 2, 以 DB_ 开头的是需要返回结果集合的接口.
 3, 以 Flow_ 是流程接口.
 4, 以 Node_ 是节点接口.
 5, 以 Port_ 是组织架构接口.
 6, 以 DTS_ 是调度. data tranr system.
 8, 以 WorkOpt_ 用工作处理器相关的接口。
 9, 以 Frm_ 处理文档打印相关的工作。
*/
public class Dev2Interface
{

	private static final int THREAD_POOL_SIZE = 10; // 线程池大小
	private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

		///#region 数量.
	/**
	 待办工作数量
	*/
	public static int getTodolistEmpWorks() throws Exception {
		String userNo = WebUser.getNo();

		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		String wfSql = "";

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			wfSql = " AND A.OrgNo='" + WebUser.getOrgNo() + "'";
		}
		ps.SQL = "SELECT  COUNT(WorkID) as Num FROM WF_EmpWorks A WHERE   A.FK_Emp=" + dbstr + "FK_Emp " + wfSql;

		ps.Add("FK_Emp", userNo);
		return DBAccess.RunSQLReturnValInt(ps);
	}

	/**
	 * 待办待阅数量
	 * @return
	 */
	public static int getTodolistUnRead()
	{
		String userNo = WebUser.getNo();

		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		String wfSql = "AND  IsRead=0  ";

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			wfSql = " AND OrgNo='" + WebUser.getOrgNo() + "'";
		}

		ps.SQL = "SELECT count(b.WorkID) as Num FROM WF_Generworkflow a JOIN WF_GenerWorkerList b ON a.WorkID=b.WorkID WHERE b.FK_Emp=" + dbstr + "FK_Emp AND b.IsPass=0  AND a.WFState <> 0 " + wfSql;
		ps.Add("FK_Emp", userNo);
		return DBAccess.RunSQLReturnValInt(ps);
	}

	/**
	 * 待办已阅数量
	 * @return
	 */
	public static int getTodolistRead()
	{
		String userNo = WebUser.getNo();

		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		String wfSql = "AND  IsRead=1  ";

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			wfSql = " AND OrgNo='" + WebUser.getOrgNo() + "'";
		}

		ps.SQL = "SELECT count(b.WorkID) as Num FROM WF_Generworkflow a JOIN WF_GenerWorkerList b ON a.WorkID=b.WorkID WHERE b.FK_Emp=" + dbstr + "FK_Emp AND b.IsPass=0  AND a.WFState <> 0 " + wfSql;
		ps.Add("FK_Emp", userNo);
		return DBAccess.RunSQLReturnValInt(ps);
	}

	/**
	 抄送数量
	*/
	public static int getTodolistCCWorks()
	{
		Paras ps = new Paras();
		ps.SQL = "SELECT count(MyPK) as Num FROM WF_CCList WHERE CCTo=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp";
		ps.Add("FK_Emp", WebUser.getNo(), false);
		return DBAccess.RunSQLReturnValInt(ps, 0);
	}

	/**
	 * 抄送待阅数量
	 * @return
	 */
	public static int getCCListUnRead()
	{
		Paras ps = new Paras();
		ps.SQL = "SELECT count(MyPK) as Num FROM WF_CCList WHERE CCTo=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp  AND Sta=0";
		ps.Add("FK_Emp", WebUser.getNo(), false);
		return DBAccess.RunSQLReturnValInt(ps, 0);
	}

	/**
	 * 抄送已阅数量
	 * @return
	 */
	public static int getCCListRead()
	{
		Paras ps = new Paras();
		ps.SQL = "SELECT count(MyPK) as Num FROM WF_CCList WHERE CCTo=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp AND Sta=1";
		ps.Add("FK_Emp", WebUser.getNo(), false);
		return DBAccess.RunSQLReturnValInt(ps, 0);
	}

	/**
	 根据workid返回抄送数据
	*/

	public static String Node_CCWorks(int WorkID, int CCType)
	{
		return Node_CCWorks(WorkID, CCType, "");
	}

	public static String Node_CCWorks(int WorkID)
	{
		return Node_CCWorks(WorkID, -1, "");
	}

	public static String Node_CCWorks(int WorkID, int CCType, String keyword)
	{
		String sql = "select  * from wf_cclist where workid=" + WorkID;
		if (CCType >= 0)
		{
			sql += " and CCType=" + CCType;
		}
		if (!DataType.IsNullOrEmpty(keyword))
		{
			sql += " and (CCToName LIKE '%" + keyword + "%' OR CCToDeptName LIKE '%" + keyword + "%' OR CCToOrgName LIKE '%" + keyword + "%')";
		}
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return Json.ToJson(dt);

	}
	/**
	 返回挂起流程数量
	*/
	public static int getTodolistHungupNum()
	{
		String sql = "";
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			sql = "SELECT COUNT(WorkID) AS Num FROM WF_GenerWorkFlow WHERE WFState=4 AND Sender LIKE '" + WebUser.getNo() + "%' ";
		}
		else
		{
			sql = "SELECT COUNT(WorkID) AS Num FROM WF_GenerWorkFlow WHERE WFState=4 AND Sender LIKE '" + WebUser.getNo() + "%' AND OrgNo='" + WebUser.getOrgNo() + "' ";
		}

		return DBAccess.RunSQLReturnValInt(sql);
	}
	/**
	 退回给当前用户的数量
	*/
	public static int getTodolistReturnNum()
	{
		String sql = "SELECT  COUNT(WorkID) AS Num from WF_GenerWorkFlow where WFState=5 and TodoEmps like '%" + WebUser.getNo() + ";%' ";
		return DBAccess.RunSQLReturnValInt(sql);
	}
	/**
	 待办逾期的数量
	*/
	public static int getTodolistOverWorkNum()
	{
		String sql = "";
		if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			sql = "SELECT COUNT(*) FROM WF_GenerWorkerlist WHERE IsPass=0 AND FK_Emp='" + WebUser.getNo() + "' AND STR_TO_DATE(SDT,'%Y-%m-%d %H:%i') < now()";

		}
		else if (SystemConfig.getAppCenterDBType() == DBType.DM ||SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{

			sql = "SELECT COUNT(*) from (SELECT *  FROM WF_GenerWorkerlist WHERE IsPass=0 AND FK_Emp='" + WebUser.getNo() + "' AND REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}') AND (sysdate - TO_DATE(SDT, 'yyyy-mm-dd hh24:mi:ss')) > 0 ";
			sql += "UNION SELECT * FROM WF_GenerWorkerlist WHERE IsPass=0 AND FK_Emp='" + WebUser.getNo() + "' AND REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2}$') AND (sysdate - TO_DATE(SDT, 'yyyy-mm-dd')) > 0 )";


		}
		else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB)
		{
			sql = "SELECT COUNT(*) FROM WF_GenerWorkerlist WHERE IsPass=0 AND FK_Emp='" + WebUser.getNo() + "' AND  to_timestamp(CASE WHEN SDT='无' THEN '' ELSE SDT END, 'yyyy-mm-dd hh24:MI:SS') < NOW()";
		}
		else
		{
			sql = "SELECT COUNT(*) FROM WF_GenerWorkerlist WHERE IsPass=0 AND FK_Emp='" + WebUser.getNo() + "' AND  convert(varchar(100),SDT,120) < CONVERT(varchar(100), GETDATE(), 120)";
		}
		return DBAccess.RunSQLReturnValInt(sql);
	}
	/**
	 在途的工作数量
	*/
	public static int getTodolistRuning()
	{
		int state = WFState.Runing.getValue();

		String sql = "SELECT count( distinct A.WorkID ) as Num FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B " +
				"WHERE A.TodoEmps  not like '%" + WebUser.getNo() + ",%' AND  A.WorkID=B.WorkID " +
				"AND B.FK_Emp=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp AND B.IsEnable=1 " +
				"AND (B.IsPass=1 OR B.IsPass<-1) AND A.WFState NOT IN ( 0, 1, 3 ) ";

		Paras ps = new Paras();

		ps.SQL = sql + Check_CCBPMRunModel();
		ps.Add("FK_Emp", WebUser.getNo(), false);
		Log.DebugWriteInfo("SQL在途的工作数量 : " + ps.SQL);
		return DBAccess.RunSQLReturnValInt(ps);
	}
	/**
	 获取草稿箱流程数量
	*/
	public static int getTodolistDraft()
	{
		/*获取数据.*/
		String dbStr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "SELECT count(a.WorkID ) as Num FROM WF_GenerWorkFlow A WHERE WFState=1 AND Starter=" + dbStr + "Starter";
		ps.Add(GenerWorkFlowAttr.Starter, WebUser.getNo(), false);
		return DBAccess.RunSQLReturnValInt(ps);
	}
	/**
	 会签的数量
	*/
	public static int getTodolistHuiQian()
	{
		/*获取数据.*/
		String dbStr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		String sql = "SELECT count(*)";
		sql += " FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B ";
		sql += " WHERE A.WorkID=B.WorkID and a.FK_Node=b.FK_Node ";
		sql += " AND (B.IsPass=90 OR A.AtPara LIKE '%HuiQianZhuChiRen=" + WebUser.getNo() + "%') ";
		sql += " AND B.FK_Emp=" + dbStr + "FK_Emp";
		//ps.SQL = "SELECT COUNT(workid) as Num FROM WF_GenerWorkerlist WHERE FK_Emp=" + dbStr + "FK_Emp AND IsPass=90";
		ps.SQL = sql;
		ps.Add(GenerWorkerListAttr.FK_Emp, WebUser.getNo(), false);
		return DBAccess.RunSQLReturnValInt(ps);
	}
	/**
	 获取已经完成流程数量

	 @return
	*/
	public static int getTodolistComplete()
	{
		/* 如果不是删除流程注册表. */
		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		ps.SQL = "SELECT count(WorkID) Num FROM WF_GenerWorkFlow WHERE (Emps LIKE '%@" + WebUser.getNo() + "@%' OR Emps LIKE '%@" + WebUser.getNo() + ",%' OR Emps LIKE '%," + WebUser.getNo() + "@%') AND (WFState=3 OR WFState=11)" ;
		return DBAccess.RunSQLReturnValInt(ps, 0);
	}
	/**
	 我发起的流程在处理的工作数量
	*/
	public static int getMyStartRuning()
	{
		Paras ps = new Paras();
		ps.SQL = "SELECT COUNT(DISTINCT a.WorkID) as Num FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B WHERE A.WorkID=B.WorkID AND A.TodoEmps  not like '%" + WebUser.getNo() + ",%' AND B.FK_Emp=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp AND B.IsEnable=1 AND  (B.IsPass=1 or B.IsPass < 0) AND B.IsPass != -2 ";
		ps.Add("FK_Emp", WebUser.getNo(), false);
		return DBAccess.RunSQLReturnValInt(ps);
	}
	/**
	 我发起已完成的流程
	*/
	public static int getMyStartComplete()
	{
		Paras ps = new Paras();
		ps.SQL = "SELECT count(WorkID) Num FROM WF_GenerWorkFlow WHERE  Starter=" + SystemConfig.getAppCenterDBVarStr() + "Starter AND (WFState=3 OR WFState=11 )" ;
		ps.Add("Starter", WebUser.getNo(), false);
		return DBAccess.RunSQLReturnValInt(ps, 0);
	}
	/**
	 共享任务个数
	*/
	public static int getTodolistSharing()
	{
		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		String wfSql = "  (WFState=" + WFState.Askfor.getValue() + " OR WFState=" + WFState.Runing.getValue() + " OR WFState=" + WFState.Shift.getValue() + " OR WFState=" + WFState.ReturnSta.getValue() + ") AND TaskSta=" + TaskSta.Sharing.getValue();
		String sql;
		String realSql = null;

		/*不是授权状态*/
		ps.SQL = "SELECT COUNT(WorkID) FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp=" + dbstr + "FK_Emp ";
		ps.Add("FK_Emp", WebUser.getNo(), false);
		return DBAccess.RunSQLReturnValInt(ps);

	}
	/**
	 申请下来的工作个数
	*/
	public static int getTodolistApply()
	{
		if (bp.wf.Glo.isEnableTaskPool() == false)
		{
			return 0;
		}

		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		String wfSql = "  (WFState=" + WFState.Askfor.getValue() + " OR WFState=" + WFState.Runing.getValue() + " OR WFState=" + WFState.Shift.getValue() + " OR WFState=" + WFState.ReturnSta.getValue() + ") AND TaskSta=" + TaskSta.Takeback.getValue();
		String sql;
		String realSql;
		/*不是授权状态*/
		ps.SQL = "SELECT COUNT(WorkID) FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp=" + dbstr + "FK_Emp";
		ps.Add("FK_Emp", WebUser.getNo(), false);
		return DBAccess.RunSQLReturnValInt(ps);
	}

		///#endregion 数量.


		///#region 自动执行
	/**
	 处理延期的任务.根据节点属性的设置

	 @return 返回处理的消息
	*/
	public static String DTS_DealDeferredWork() throws Exception {
		bp.wf.dts.DTS_DealDeferredWork en = new bp.wf.dts.DTS_DealDeferredWork();
		en.Do();

		return "执行成功..";
	}
	/**
	 自动执行开始节点数据
	 说明:根据自动执行的流程设置，自动启动发起的流程。
	 比如：您根据ccflow的自动启动流程的设置，自动启动该流程，不使用ccflow的提供的服务程序，您需要按如下步骤去做。
	 1, 写一个自动调度的程序。
	 2，根据自己的时间需要调用这个接口。

	 @param fl 流程实体,您可以 new Flow(flowNo); 来传入.
	*/
	public static void DTS_AutoStarterFlow(Flow fl) throws Exception {

			///#region 读取数据.
		MapExt me = new MapExt();
		int i = me.Retrieve(MapExtAttr.FK_MapData, "ND" + Integer.parseInt(fl.getNo()) + "01", MapExtAttr.ExtType, "PageLoadFull");
		if (i == 0)
		{
			Log.DebugWriteError("没有为流程(" + fl.getName() + ")的开始节点设置发起数据,请参考说明书解决.");
			return;
		}

		// 获取从表数据.
		DataSet ds = new DataSet();
		String[] dtlSQLs = me.getTag1().split("[*]", -1);
		for (String sql : dtlSQLs)
		{
			if (DataType.IsNullOrEmpty(sql))
			{
				continue;
			}

			String[] tempStrs = sql.split("[=]", -1);
			String dtlName = tempStrs[0];
			DataTable dtlTable = DBAccess.RunSQLReturnTable(sql.replace(dtlName + "=", ""));
			dtlTable.setTableName( dtlName);
			ds.Tables.add(dtlTable);
		}

			///#endregion 读取数据.


			///#region 检查数据源是否正确.
		String errMsg = "";
		// 获取主表数据.
		DataTable dtMain = DBAccess.RunSQLReturnTable(me.getTag());
		if (dtMain.Columns.contains("Starter") == false)
		{
			errMsg += "@配值的主表中没有Starter列.";
		}

		if (dtMain.Columns.contains("MainPK") == false)
		{
			errMsg += "@配值的主表中没有MainPK列.";
		}

		if (errMsg.length() > 2)
		{
			Log.DebugWriteError("流程(" + fl.getName() + ")的开始节点设置发起数据,不完整." + errMsg);
			return;
		}

			///#endregion 检查数据源是否正确.


			///#region 处理流程发起.

		String nodeTable = "ND" + Integer.parseInt(fl.getNo()) + "01";
		MapData md = new MapData(nodeTable);

		for (DataRow dr : dtMain.Rows)
		{
			String mainPK = dr.getValue("MainPK").toString();
			String sql = "SELECT OID FROM " + md.getPTable() + " WHERE MainPK='" + mainPK + "'";
			if (DBAccess.RunSQLReturnTable(sql).Rows.size() != 0)
			{
				continue; //说明已经调度过了
			}

			String starter = dr.getValue("Starter").toString();
			if (!Objects.equals(WebUser.getNo(), starter))
			{
				WebUser.Exit();
				Emp emp = new Emp();
				emp.setUserID(starter);
				if (emp.RetrieveFromDBSources() == 0)
				{
					Log.DebugWriteInfo("@数据驱动方式发起流程(" + fl.getName() + ")设置的发起人员:" + emp.getUserID() + "不存在。");
					continue;
				}

				WebUser.SignInOfGener(emp, "CH", false, false, null, null);
			}


				///#region  给值.
			Work wk = fl.NewWork();
			for (DataColumn dc : dtMain.Columns)
			{
				wk.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName).toString());
			}

			if (ds.Tables.size()!= 0)
			{
				String refPK = dr.getValue("MainPK").toString();
				MapDtls dtls = wk.getHisNode().getMapData().getMapDtls(); // new MapDtls(nodeTable);
				for (MapDtl dtl : dtls.ToJavaList())
				{
					for (DataTable dt : ds.Tables)
					{
						if (!Objects.equals(dt.TableName, dtl.getNo()))
						{
							continue;
						}

						//删除原来的数据。
						GEDtl dtlEn = dtl.getHisGEDtl();
						dtlEn.Delete(GEDtlAttr.RefPK, String.valueOf(wk.getOID()));

						// 执行数据插入。
						for (DataRow drDtl : dt.Rows)
						{
							if (!Objects.equals(drDtl.get("RefMainPK").toString(), refPK))
							{
								continue;
							}

							dtlEn = dtl.getHisGEDtl();

							for (DataColumn dc : dt.Columns)
							{
								dtlEn.SetValByKey(dc.ColumnName, drDtl.get(dc.ColumnName).toString());
							}

							dtlEn.setRefPK(String.valueOf(wk.getOID()));
							dtlEn.Insert();
						}
					}
				}
			}

				///#endregion  给值.

			// 处理发送信息.
			Node nd = fl.getHisStartNode();
			try
			{
				WorkNode wn = new WorkNode(wk, nd);
				String msg = wn.NodeSend().ToMsgOfHtml();
				//Log.DefaultLogWriteLineInfo(msg);
			}
			catch (RuntimeException ex)
			{
				Log.DebugWriteError(ex.getMessage());
			}
		}

			///#endregion 处理流程发起.

	}

		///#endregion


		///#region 数据集合接口(如果您想获取一个结果集合的接口，都是以DB_开头的.)


		///#region 与子流程相关.
	/**
	 获取流程事例的运行轨迹数据.
	 说明：使用这些数据可以生成流程的操作日志.

	 @param workid 工作ID
	 @return GenerWorkFlows
	*/
	public static GenerWorkFlows DB_SubFlows(long workid) throws Exception {
		GenerWorkFlows gwf = new GenerWorkFlows();
		gwf.Retrieve(GenerWorkFlowAttr.PWorkID, workid, null);
		return gwf;
	}

		///#endregion 获取流程事例的轨迹图


		///#region 获取流程事例的轨迹图
	/**
	 获取流程时间轴，包含了分合流流程，父子流程，延续子流程

	 @param fk_flow
	 @param workid
	 @param fid
	 @return
	*/

	public static DataTable DB_GenerTrackTable(String fk_flow, long workid, long fid) throws Exception {
		return DB_GenerTrackTable(fk_flow, workid, fid, false);
	}

	public static DataTable DB_GenerTrackTable(String fk_flow, long workid, long fid, boolean isWx) throws Exception {
		String sqlOfWhere1 = "";
		DataTable dt = null;
		String dbStr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();

		if (fid == 0)
		{
			sqlOfWhere1 = " WHERE (FID=" + dbStr + "WorkID11 OR WorkID=" + dbStr + "WorkID12 )  ";
			ps.Add("WorkID11", workid);
			ps.Add("WorkID12", workid);
		}
		else
		{ //获取分合流的数据
			sqlOfWhere1 = " WHERE (FID=" + dbStr + "FID11 OR WorkID=" + dbStr + "FID12 ) ";
			ps.Add("FID11", fid);
			ps.Add("FID12", fid);
		}

		String sql = "";

		if (isWx)
		{
			if (SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
			{
				sql = "SELECT '' ExtField,MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT NodeName,NDTo,NDToT,EmpFrom,EmpFromT FK_EmpText,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Exer,Tag FROM ND" + Integer.parseInt(fk_flow) + "Track " + sqlOfWhere1;
				sql += " UNION SELECT '' ExtField,MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT,NDTo,NDToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Exer,Tag FROM ND" + Integer.parseInt(fk_flow) + "Track " + sqlOfWhere1;
				sql += " ORDER BY RDT ASC ";
			}else if (SystemConfig.getAppCenterDBType() == DBType.DM)
			{
				sql = "SELECT '' ExtField,MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT NodeName,NDTo,NDToT,EmpFrom,EmpFromT FK_EmpText,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Exer,Tag FROM ND" + Integer.parseInt(fk_flow) + "Track " + sqlOfWhere1;
				sql += " UNION ALL SELECT '' ExtField,MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT,NDTo,NDToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Exer,Tag FROM ND" + Integer.parseInt(fk_flow) + "Track " + sqlOfWhere1;
				sql += " ORDER BY RDT ASC ";
			}
			else
			{
				sql = "SELECT * From";
				sql += "(SELECT '' ExtField,MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT NodeName,NDTo,NDToT,EmpFrom,EmpFromT FK_EmpText,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Exer,Tag FROM ND" + Integer.parseInt(fk_flow) + "Track " + sqlOfWhere1;
				sql += "UNION SELECT '' ExtField,MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT,NDTo,NDToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Exer,Tag FROM ND" + Integer.parseInt(fk_flow) + "Track " + sqlOfWhere1 + ")";
				sql += " AS Track  ORDER BY RDT ASC ";
			}

		}
		else
		{
			if (SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
			{
				sql = "SELECT '' ExtField,MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT,NDTo,NDToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Exer,Tag FROM ND" + Integer.parseInt(fk_flow) + "Track " + sqlOfWhere1;
				sql += " UNION SELECT '' ExtField,MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT,NDTo,NDToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Exer,Tag FROM ND" + Integer.parseInt(fk_flow) + "Track " + sqlOfWhere1;
				sql += "  ORDER BY RDT ASC ";
			}else if (SystemConfig.getAppCenterDBType() == DBType.DM)
			{
				sql = "SELECT '' ExtField,MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT,NDTo,NDToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Exer,Tag FROM ND" + Integer.parseInt(fk_flow) + "Track " + sqlOfWhere1;
				sql += " UNION ALL SELECT '' ExtField,MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT,NDTo,NDToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Exer,Tag FROM ND" + Integer.parseInt(fk_flow) + "Track " + sqlOfWhere1;
				sql += "  ORDER BY RDT ASC ";
			}
			else
			{
				sql = "SELECT * From";
				sql += "(SELECT '' ExtField,MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT,NDTo,NDToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Exer,Tag FROM ND" + Integer.parseInt(fk_flow) + "Track " + sqlOfWhere1;
				sql += "UNION SELECT '' ExtField,MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT,NDTo,NDToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Exer,Tag FROM ND" + Integer.parseInt(fk_flow) + "Track " + sqlOfWhere1 + ")";
				sql += " AS Track  ORDER BY RDT ASC ";

			}

		}

		ps.SQL = sql;


		try
		{
			dt = DBAccess.RunSQLReturnTable(ps);
		}
		catch (java.lang.Exception e)
		{
			// 处理track表.
			Track.CreateOrRepairTrackTable(fk_flow);
			dt = DBAccess.RunSQLReturnTable(ps);
		}

		//把列名转化成区分大小写.
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("MYPK").ColumnName = "MyPK";
			dt.Columns.get("ACTIONTYPE").ColumnName = "ActionType";
			dt.Columns.get("ACTIONTYPETEXT").ColumnName = "ActionTypeText";
			dt.Columns.get("FID").ColumnName = "FID";
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("NDFROM").ColumnName = "NDFrom";
			dt.Columns.get("NDFROMT").ColumnName = "NDFromT";
			dt.Columns.get("NDTO").ColumnName = "NDTo";
			dt.Columns.get("NDTOT").ColumnName = "NDToT";
			dt.Columns.get("EMPFROM").ColumnName = "EmpFrom";
			dt.Columns.get("EMPFROMT").ColumnName = "EmpFromT";
			dt.Columns.get("EMPTO").ColumnName = "EmpTo";
			dt.Columns.get("EMPTOT").ColumnName = "EmpToT";
			dt.Columns.get("RDT").ColumnName = "RDT";
			dt.Columns.get("WORKTIMESPAN").ColumnName = "WorkTimeSpan";
			dt.Columns.get("MSG").ColumnName = "Msg";
			dt.Columns.get("NODEDATA").ColumnName = "NodeData";
			dt.Columns.get("EXER").ColumnName = "Exer";
			dt.Columns.get("TAG").ColumnName = "Tag";
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("mypk").ColumnName = "MyPK";
			dt.Columns.get("actiontype").ColumnName = "ActionType";
			dt.Columns.get("actiontypetext").ColumnName = "ActionTypeText";
			dt.Columns.get("fid").ColumnName = "FID";
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("ndfrom").ColumnName = "NDFrom";
			dt.Columns.get("ndfromt").ColumnName = "NDFromT";
			dt.Columns.get("ndto").ColumnName = "NDTo";
			dt.Columns.get("ndtot").ColumnName = "NDToT";
			dt.Columns.get("empfrom").ColumnName = "EmpFrom";
			dt.Columns.get("empfromt").ColumnName = "EmpFromT";
			dt.Columns.get("empto").ColumnName = "EmpTo";
			dt.Columns.get("emptot").ColumnName = "EmpToT";
			dt.Columns.get("rdt").ColumnName = "RDT";
			dt.Columns.get("worktimespan").ColumnName = "WorkTimeSpan";
			dt.Columns.get("msg").ColumnName = "Msg";
			dt.Columns.get("nodedata").ColumnName = "NodeData";
			dt.Columns.get("exer").ColumnName = "Exer";
			dt.Columns.get("tag").ColumnName = "Tag";
		}

		//把track加入里面去.
		dt.setTableName("Track");
		return dt;
	}
	/**
	 获取一个流程

	 @param fk_flow 流程编号
	 @param userNo 操作员编号
	 @return
	*/
	public static DataTable DB_GenerNDxxxRpt(String fk_flow, String userNo)
	{
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "SELECT * FROM ND" + Integer.parseInt(fk_flow) + "Rpt WHERE FlowStarter=" + dbstr + "FlowStarter  ORDER BY RDT";
		ps.Add(GERptAttr.FlowStarter, userNo, false);
		return DBAccess.RunSQLReturnTable(ps);
	}

		///#endregion 获取流程事例的轨迹图


		///#region 获取能够发送或者抄送人员的列表.
	/**
	 获取可以执行指定节点人的列表

	 @param fk_node 节点编号
	 @param workid 工作ID
	 @return
	*/
	public static DataSet DB_CanExecSpecNodeEmps(int fk_node, long workid)
	{
		DataSet ds = new DataSet();
		Paras ps = new Paras();
		ps.SQL = "SELECT No,Name,FK_Dept FROM Port_Emp ";
		DataTable dtEmp = DBAccess.RunSQLReturnTable(ps);
		dtEmp.setTableName("Emps");
		ds.Tables.add(dtEmp);

		ps = new Paras();
		ps.SQL = "SELECT No,Name FROM Port_Dept ";
		DataTable dtDept = DBAccess.RunSQLReturnTable(ps);
		dtDept.setTableName("Depts");
		ds.Tables.add(dtDept);
		return ds;
	}
	/**
	 获得可以抄送的人员列表

	 @param fk_node 节点编号
	 @param workid 工作ID
	 @return
	*/
	public static DataSet DB_CanCCSpecNodeEmps(int fk_node, long workid)
	{
		DataSet ds = new DataSet();
		Paras ps = new Paras();
		ps.SQL = "SELECT No,Name,FK_Dept FROM Port_Emp ";
		DataTable dtEmp = DBAccess.RunSQLReturnTable(ps);
		dtEmp.setTableName( "Emps");
		ds.Tables.add(dtEmp);


		ps = new Paras();
		ps.SQL = "SELECT No,Name FROM Port_Dept ";
		DataTable dtDept = DBAccess.RunSQLReturnTable(ps);
		dtDept.setTableName( "Depts");
		ds.Tables.add(dtDept);

		return ds;
	}

		///#endregion 获取能够发送或者抄送人员的列表.


		///#region 获取操送列表
	/**
	 获取指定人员的抄送列表
	 说明:可以根据这个列表生成指定用户的抄送数据.

	 @param domainExt 域.
	 @return 返回该人员的所有抄送列表,结构同表WF_CCList.
	*/

	public static DataTable DB_CCList(String domainExt)
	{
		return DB_CCList(domainExt, null);
	}

	public static DataTable DB_CCList()
	{
		return DB_CCList("", "");
	}

	public static DataTable DB_CCList(String domainExt, String fk_flow)
	{
		String sqlWhere = "";
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			sqlWhere = " AND OrgNo='" + WebUser.getOrgNo() + "'";
		}
		// 增加一个Sta列.
		Paras ps = new Paras();
		if (DataType.IsNullOrEmpty(domainExt))
		{
			if (DataType.IsNullOrEmpty(fk_flow))
			{
				ps.SQL = "SELECT a.MyPK,A.Title,A.FlowNo as FK_Flow,A.FlowName,A.WorkID,'' as Doc,A.RecEmpNo as Rec,A.RecEmpName as RecName,A.RDT,A.FID,A.NodeIDCC AS FK_Node,A.NodeIDCCName AS NodeName,B.WFSta,A.Sta,A.NodeIDCC,A.NodeIDCCName FROM WF_CCList A, WF_GenerWorkFlow B WHERE A.CCTo=" + SystemConfig.getAppCenterDBVarStr() + "CCTo AND B.WorkID=A.WorkID AND A.Sta >=0  ORDER BY RDT DESC ";
				ps.Add("CCTo", WebUser.getNo(), false);
			}
			else
			{
				ps.SQL = "SELECT a.MyPK,A.Title,A.FlowNo as FK_Flow,A.FlowName,A.WorkID,'' as Doc,A.RecEmpNo as Rec,A.RecEmpName as RecName,A.RDT,A.FID,A.NodeIDCC AS FK_Node,A.NodeIDCCName AS NodeName,B.WFSta,A.Sta,A.NodeIDCC,A.NodeIDCCName FROM WF_CCList A, WF_GenerWorkFlow B WHERE A.CCTo=" + SystemConfig.getAppCenterDBVarStr() + "CCTo  AND B.FK_Flow=" + SystemConfig.getAppCenterDBVarStr() + "FK_Flow AND B.WorkID=A.WorkID AND A.Sta >=0 ORDER BY RDT DESC ";
				ps.Add("CCTo", WebUser.getNo(), false);
				ps.Add("FK_Flow", fk_flow, false);
			}
		}
		else
		{
			if (DataType.IsNullOrEmpty(fk_flow))
			{
				ps.SQL = "SELECT a.MyPK,A.Title,A.FlowNo as FK_Flow,A.FlowName,A.WorkID,'' as Doc,A.RecEmpNo as Rec,A.RecEmpName as RecName,A.RDT,A.FID,A.NodeIDCC AS FK_Node,A.NodeIDCCName AS NodeName,B.WFSta,A.Sta,A.NodeIDCC,A.NodeIDCCName FROM WF_CCList A, WF_GenerWorkFlow B WHERE A.CCTo=" + SystemConfig.getAppCenterDBVarStr() + "CCTo AND B.WorkID=A.WorkID AND B.DomainExt=" + SystemConfig.getAppCenterDBVarStr() + "DomainExt AND A.Sta >=0 ORDER BY RDT DESC ";
				ps.Add("CCTo", WebUser.getNo(), false);
				ps.Add("DomainExt", domainExt, false);
			}
			else
			{
				ps.SQL = "SELECT a.MyPK,A.Title,A.FlowNo as FK_Flow,A.FlowName,A.WorkID,'' as Doc,A.RecEmpNo as Rec,A.RecEmpName as RecName,A.RDT,A.FID,A.NodeIDCC AS FK_Node,A.NodeIDCCName AS NodeName,B.WFSta,A.Sta,A.NodeIDCC,A.NodeIDCCName FROM WF_CCList A, WF_GenerWorkFlow B WHERE A.CCTo=" +  SystemConfig.getAppCenterDBVarStr() + "CCTo AND B.WorkID=A.WorkID AND B.FK_Flow=" + SystemConfig.getAppCenterDBVarStr() + "FK_Flow AND B.DomainExt=" +  SystemConfig.getAppCenterDBVarStr() + "DomainExt AND A.Sta >=0 ORDER BY RDT DESC ";
				ps.Add("CCTo", WebUser.getNo(), false);
				ps.Add("DomainExt", domainExt, false);
				ps.Add("FK_Flow", fk_flow, false);
			}
		}
		//ps.SQL = ps.SQL + sqlWhere;//
        //抄送检查物理表逻辑
		DataTable dt = new DataTable();
        try {
            dt = DBAccess.RunSQLReturnTable(ps);
        } catch (Exception ex) {
            String msg = ex.getMessage() == null ? "" : ex.getMessage();
            if (msg.contains("does not exist")
                    || msg.contains("doesn't exist")
                    || msg.contains("field list")) {
                try {
                    //修复抄送表
                    new CCList().CheckPhysicsTable();
                    //重新执行一次
                    dt = DBAccess.RunSQLReturnTable(ps);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("MYPK").ColumnName = "MyPK";
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("DOC").ColumnName = "Doc";
			dt.Columns.get("REC").ColumnName = "Rec";
			dt.Columns.get("RECNAME").ColumnName = "RecName";
			dt.Columns.get("RDT").ColumnName = "RDT";
			dt.Columns.get("FID").ColumnName = "FID";
			dt.Columns.get("WFSTA").ColumnName = "WFSta";
			dt.Columns.get("STA").ColumnName = "Sta";
			dt.Columns.get("NODEIDCC").ColumnName = "NodeIDCC";
			dt.Columns.get("NODEIDCCNAME").ColumnName = "NodeIDCCName";
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("mypk").ColumnName = "MyPK";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("doc").ColumnName = "Doc";
			dt.Columns.get("rec").ColumnName = "Rec";
			dt.Columns.get("recname").ColumnName = "RecName";
			dt.Columns.get("rdt").ColumnName = "RDT";
			dt.Columns.get("fid").ColumnName = "FID";
			dt.Columns.get("wfsta").ColumnName = "WFSta";
			dt.Columns.get("sta").ColumnName = "Sta";
			dt.Columns.get("nodeidcc").ColumnName = "NodeIDCC";
			dt.Columns.get("nodeidccname").ColumnName = "NodeIDCCName";
		}
		return dt;
	}
	/**
	 获取抄送列表

	 @param sta 抄送状态
	 @param domainExt 域
	 @return
	*/

	public static DataTable DB_CCList(CCSta sta, String domainExt)
	{
		return DB_CCList(sta, domainExt, null);
	}

	public static DataTable DB_CCList(CCSta sta)
	{
		return DB_CCList(sta, null, null);
	}

	public static DataTable DB_CCList(CCSta sta, String domainExt, String fk_flow)
	{
		String dbStr = SystemConfig.getAppCenterDBVarStr();

		Paras ps = new Paras();
		if (domainExt == null)
		{
			if (fk_flow == null)
			{
				StringBuilder sql=new StringBuilder("");
				sql.append("SELECT C.MyPK,C.Title,C.FlowNo as FK_Flow,C.FlowName,C.WorkID,'' as Doc,C.RecEmpNo as Rec,C.RecEmpName as RecName,C.RDT,C.FID,C.NodeIDCC AS FK_Node,C.NodeIDCCName AS NodeName,C.Sta,C.NodeIDCC,C.NodeIDCCName,C.ReadDT ")
				.append(" ,w.RDT CDT,w.WFState,w.SendDT")
				.append(" FROM WF_CCList C left join wf_generworkflow w on C.WorkID=w.WorkID")
				.append(" WHERE C.Sta=" + dbStr + "Sta AND C.CCTo=" + dbStr + "CCTo ")
				.append(" ORDER BY C.RDT DESC");
				ps.SQL = sql.toString();
				ps.Add("Sta", sta.getValue());
				ps.Add("CCTo", WebUser.getNo(), false);
			}
			else
			{
				ps.SQL = "SELECT MyPK,Title,FlowNo as FK_Flow,FlowName,WorkID,'' as Doc,RecEmpNo as Rec,RecEmpName as RecName,RDT,FID,NodeIDCC AS FK_Node,NodeIDCCName AS NodeName,Sta,NodeIDCC,NodeIDCCName FROM WF_CCList WHERE Sta=" + dbStr + "Sta AND CCTo=" + dbStr + "CCTo  AND FK_Flow=" + dbStr + "FK_Flow  ORDER BY RDT DESC";
				ps.Add("Sta", sta.getValue());
				ps.Add("CCTo", WebUser.getNo(), false);
				ps.Add("FK_Flow", fk_flow, false);

			}
		}
		else
		{
			if (fk_flow == null)
			{
				ps.SQL = "SELECT MyPK,Title,FlowNo as FK_Flow,FlowName,WorkID,'' as Doc,RecEmpNo as Rec,RecEmpName as RecName,RDT,FID,NodeIDCC AS FK_Node,NodeIDCCName AS NodeName,Sta,NodeIDCC,NodeIDCCName FROM WF_CCList WHERE Sta=" + dbStr + "Sta AND CCTo=" + dbStr + "CCTo AND DomainExt=" + dbStr + "DomainExt  ORDER BY RDT DESC";
				ps.Add("Sta", sta.getValue());
				ps.Add("CCTo", WebUser.getNo(), false);
				ps.Add("DomainExt", domainExt, false);
			}
			else
			{
				ps.SQL = "SELECT MyPK,Title,FlowNo as FK_Flow,FlowName,WorkID,'' as Doc,RecEmpNo as Rec,RecEmpName as RecName,RDT,FID,NodeIDCC AS FK_Node,NodeIDCCName AS NodeName,Sta,NodeIDCC,NodeIDCCName FROM WF_CCList WHERE Sta=" + dbStr + "Sta AND CCTo=" + dbStr + "CCTo AND DomainExt=" + dbStr + "DomainExt AND FK_Flow=" + dbStr + "FK_Flow  ORDER BY RDT DESC";
				ps.Add("Sta", sta.getValue());
				ps.Add("CCTo", WebUser.getNo(), false);
				ps.Add("DomainExt", domainExt, false);
				ps.Add("FK_Flow", fk_flow, false);
			}
		}

		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("MYPK").ColumnName = "MyPK";
			dt.Columns.get("STA").ColumnName = "Sta";

			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			//dt.Columns.get("DOC").ColumnName = "Doc";
			dt.Columns.get("REC").ColumnName = "Rec";
			dt.Columns.get("RECNAME").ColumnName = "RecName";
			dt.Columns.get("RDT").ColumnName = "RDT";
			dt.Columns.get("FID").ColumnName = "FID";
			dt.Columns.get("NODEIDCC").ColumnName = "NodeIDCC";
			dt.Columns.get("NODEIDCCNAME").ColumnName = "NodeIDCCName";
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("mypk").ColumnName = "MyPK";
			dt.Columns.get("sta").ColumnName = "Sta";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			dt.Columns.get("workid").ColumnName = "WorkID";
			//dt.Columns.get("doc").ColumnName = "Doc";
			dt.Columns.get("rec").ColumnName = "Rec";
			dt.Columns.get("recname").ColumnName = "RecName";
			dt.Columns.get("rdt").ColumnName = "RDT";
			dt.Columns.get("fid").ColumnName = "FID";
			dt.Columns.get("nodeidcc").ColumnName = "NodeIDCC";
			dt.Columns.get("nodeidccname").ColumnName = "NodeIDCCName";
		}
		dt.Columns.Add("CCTo");
		dt.Columns.Add("CCToName");
		dt.Columns.Add("DeptNo");
		dt.Columns.Add("DeptName");
		for(int j=0;j<dt.Rows.size();j++){
			String WorkID=dt.Rows.get(j).getValue("WorkID").toString();
			String FK_Flow=dt.Rows.get(j).getValue("FK_Flow").toString();
			//workid创建时间
			String createTime=queryWorkIDCreateTime(FK_Flow, Long.valueOf(WorkID));
			dt.Rows.get(j).put("CDT",createTime);
			//被抄送人信息
			dt.Rows.get(j).put("CCTo",WebUser.getNo());
			dt.Rows.get(j).put("CCToName",WebUser.getName());
			dt.Rows.get(j).put("DeptNo",WebUser.getDeptNo());
			dt.Rows.get(j).put("DeptName",WebUser.getDeptName());
		}
		return dt;
	}
	/**
	 获取指定人员的抄送列表(未读)

	 @param FK_Emp 人员编号,如果是null,则返回所有的.
	 @return 返回该人员的未读的抄送列表
	*/

	public static DataTable DB_CCList_UnRead(String FK_Emp, String domainExt)
	{
		return DB_CCList_UnRead(FK_Emp, domainExt, null);
	}

	public static DataTable DB_CCList_UnRead(String FK_Emp)
	{
		return DB_CCList_UnRead(FK_Emp, null, null);
	}

	public static DataTable DB_CCList_UnRead(String FK_Emp, String domainExt, String fk_flow)
	{
		return DB_CCList(CCSta.UnRead, domainExt, fk_flow);
	}
	/**
	 获取指定人员的抄送列表(已读)

	 @return 返回该人员的已读的抄送列表
	*/

	public static DataTable DB_CCList_Read(String domainExt)
	{
		return DB_CCList_Read(domainExt, null);
	}

	public static DataTable DB_CCList_Read()
	{
		return DB_CCList_Read(null, null);
	}

	public static DataTable DB_CCList_Read(String domainExt, String fk_flow)
	{
		return DB_CCList(CCSta.Read, domainExt, fk_flow);
	}
	/**
	 获取指定人员的抄送列表(已删除)

	 @return 返回该人员的已删除的抄送列表
	*/

	public static DataTable DB_CCList_Delete(String domainExt)
	{
		return DB_CCList_Delete(domainExt, null);
	}

	public static DataTable DB_CCList_Delete(String domainExt, String fk_flow)
	{
		return DB_CCList(CCSta.Del, domainExt, fk_flow);
	}
	/**
	 获取指定人员的抄送列表(已回复)

	 @return 返回该人员的已删除的抄送列表
	*/
	public static DataTable DB_CCList_CheckOver(String domainExt)
	{
		return DB_CCList(CCSta.CheckOver, domainExt);
	}

		///#endregion


		///#region 获取当前操作员可以发起的流程集合
	/**
	 获取指定人员能够发起流程的集合.
	 说明:利用此接口可以生成用户的发起的流程列表.

	 @param userNo 操作员编号
	 @return BP.WF.Flows 可发起的流程对象集合,如何使用该方法形成发起工作列表,请参考:\WF\UC\Start.ascx
	*/
	public static Flows DB_GenerCanStartFlowsOfEntities(String userNo) throws Exception {
		String sql = "";
		// 采用新算法.
		sql = "SELECT FK_Flow FROM V_FlowStarterBPM WHERE FK_Emp='" + userNo + "'";

		Flows fls = new Flows();
		QueryObject qo = new QueryObject(fls);
		qo.AddWhereInSQL("No", sql);
		qo.addAnd();
		qo.AddWhere(FlowAttr.IsCanStart, true);
		qo.addOrderBy("FK_FlowSort", FlowAttr.Idx);
		qo.DoQuery();
		return fls;

	}
	/**
	 获得指定人的流程发起列表

	 @param userNo 发起人编号
	 @return
	*/

	public static DataTable DB_StarFlows(String userNo)
	{
		return DB_StarFlows(userNo, null);
	}

	public static DataTable DB_StarFlows(String userNo, String domainExt)
	{
		DataTable dt = DB_Start(userNo, domainExt);
		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("NO").ColumnName = "No";
			dt.Columns.get("NAME").ColumnName = "Name";
			dt.Columns.get("ISBATCHSTART").ColumnName = "IsBatchStart";
			dt.Columns.get("ICON").ColumnName = "Icon";
			dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
			dt.Columns.get("FK_FLOWSORTTEXT").ColumnName = "FK_FlowSortText";
			dt.Columns.get("DOMAINEXT").ColumnName = "DomainExt";
			dt.Columns.get("ISSTARTINMOBILE").ColumnName = "IsStartInMobile";
			dt.Columns.get("IDX").ColumnName = "Idx";
			dt.Columns.get("WORKMODEL").ColumnName = "WorkModel";
			dt.Columns.get("FLOWMARK").ColumnName = "FlowMark";

		}
		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("no").ColumnName = "No";
			dt.Columns.get("name").ColumnName = "Name";
			dt.Columns.get("isbatchstart").ColumnName = "IsBatchStart";
			dt.Columns.get("icon").ColumnName = "Icon";
			dt.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";
			dt.Columns.get("fk_flowsorttext").ColumnName = "FK_FlowSortText";
			dt.Columns.get("domainext").ColumnName = "DomainExt";
			dt.Columns.get("isstartinmobile").ColumnName = "IsStartInMobile";
			dt.Columns.get("idx").ColumnName = "Idx";
			dt.Columns.get("workmodel").ColumnName = "WorkModel";
			dt.Columns.get("flowmark").ColumnName = "FlowMark";
		}
		return dt;
	}
	/**
	 获得指定人的流程发起列表

	 @param userNo 发起人编号
	 @return
	*/

	public static DataTable DB_Start(String userNo)
	{
		return DB_Start(userNo, null);
	}

	public static DataTable DB_Start()
	{
		return DB_Start(null, null);
	}

	public static DataTable DB_Start(String userNo, String domainExt)
	{
		if (DataType.IsNullOrEmpty(userNo) == true)
		{
			userNo = WebUser.getUserID();
		}

		//任何人都可以启动.
		String sql0 = "";
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			sql0 = "SELECT A.FK_Flow FROM WF_Node A ";
			sql0 += " WHERE  NodePosType=0  AND DeliveryWay=4  ";
		}
		else
		{
			sql0 = "SELECT A.FK_Flow FROM WF_Node A, WF_Flow B ";
			sql0 += " WHERE A.NodePosType=0  AND A.DeliveryWay=4 AND A.FK_Flow=B.No ";
			sql0 += " AND B.OrgNo='" + WebUser.getOrgNo() + "'";
		}

		//按岗位计算.
		String sql1 = "SELECT A.FK_Flow FROM WF_Node A, WF_NodeStation B, Port_DeptEmpStation C";
		sql1 += " WHERE  A.NodePosType=0  AND A.NodeID=B.FK_Node AND B.FK_Station=C.FK_Station ";
		sql1 += "  AND (A.DeliveryWay=0 OR A.DeliveryWay=14) ";
		sql1 += "  AND C.FK_Emp='" + userNo + "'";

		//按部门计算、按绑定的其他组织的部门
		String sql2 = "SELECT A.FK_Flow FROM WF_Node A, WF_NodeDept B, Port_DeptEmp C ";
		sql2 += " WHERE  A.NodePosType=0  AND A.NodeID=B.FK_Node AND B.FK_Dept=C.FK_Dept ";
		sql2 += "  AND A.DeliveryWay=1 OR A.DeliveryWay=101";
		sql2 += "  AND C.FK_Emp='" + userNo + "' ";

		//按人员计算.
		String sql3 = "SELECT A.FK_Flow FROM WF_Node A, WF_NodeEmp B ";
		sql3 += " WHERE  A.NodePosType=0  AND  A.NodeID=B.FK_Node ";
		sql3 += "  AND A.DeliveryWay=3";
		sql3 += "  AND B.FK_Emp='" + userNo + "' ";

		// 按照岗位与部门的交集计算.
		String sql4 = "SELECT A.FK_Flow FROM WF_Node A, WF_NodeDept B, WF_NodeStation C, Port_DeptEmpStation D ";
		sql4 += " WHERE  A.NodePosType=0 AND A.NodeID=B.FK_Node AND A.NodeID=C.FK_Node ";
		sql4 += "  AND A.DeliveryWay=9 ";
		sql4 += "  AND B.FK_Dept =D.FK_Dept ";
		sql4 += "  AND C.FK_Station =D.FK_Station ";
		sql4 += "  AND D.FK_Emp='" + userNo + "' ";

		//按照指定的组织可以发起
		String sql6 = "";
		String sql7 = "";
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
		{
//			sql6 = "SELECT A.FK_Flow FROM WF_Node A,WF_FlowOrg B ";
//			sql6 += "WHERE A.NodePosType = 0 ";
//			sql6 += "AND A.FK_Flow = B.FlowNo ";
//			sql6 += "AND(A.DeliveryWay = 22 OR A.DeliveryWay = 51) ";
//			sql6 += " AND B.OrgNo='" + WebUser.getOrgNo() + "'";
			//分享给指定组织的流程.
			sql6 = "SELECT A.No,A.Name,A.IsBatchStart,A.Icon,A.FK_FlowSort,C.Name AS FK_FlowSortText,C.DomainExt,C.MobileColorType,A.IsStartInMobile,A.Idx,A.WorkModel,C.Idx AS SortIdx  FROM WF_Flow A,WF_FlowOrg B,WF_FlowSort C WHERE A.No=B.FlowNo AND A.FK_FlowSort=C.No AND A.ShareSln=2 AND B.OrgNo ='" + WebUser.getOrgNo() + "' ";

			//其他组织分享的给所有组织的流程..
			sql7 = "SELECT A.No,A.Name,A.IsBatchStart,A.Icon,A.FK_FlowSort,C.Name AS FK_FlowSortText,C.DomainExt,C.MobileColorType,A.IsStartInMobile,A.Idx,A.WorkModel,C.Idx AS SortIdx  FROM WF_Flow A,WF_FlowSort C WHERE A.FK_FlowSort=C.No AND A.ShareSln=1 AND A.OrgNo !='"+WebUser.getOrgNo()+"' ";

		}

		// 按照设置的组织计算. WF_FlowOrg
		String sql5 = "";
		String sqlUnion = "";
		sqlUnion += sql0 + "  UNION ";
		sqlUnion += sql1 + " UNION ";
		sqlUnion += sql2 + " UNION ";
		sqlUnion += sql3 + " UNION ";
		/*if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
		{
			sqlUnion += sql6 + " UNION ";
			sqlUnion += sql7 + " UNION ";
		}*/
		sqlUnion += sql4 + "  ";



		String sql = "SELECT A.No,A.Name,A.IsBatchStart,A.Icon,A.FK_FlowSort,B.Name AS FK_FlowSortText,B.DomainExt,B.MobileColorType,A.IsStartInMobile,A.Idx,A.WorkModel,B.Idx AS SortIdx,A.FlowMark";
		//if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.DM)
		//	sql = "SELECT A.\"No\",A.Name,A.IsBatchStart,A.Icon,A.FK_FlowSort,B.Name AS FK_FlowSortText,B.\"DomainExt\",B.MobileColorType,A.IsStartInMobile,A.Idx,A.WorkModel";
		sql += " FROM WF_Flow A, WF_FlowSort B ";
		sql += " WHERE A.FK_FlowSort=B.No AND A.IsCanStart=1 AND A.No IN (" + sqlUnion + ")";

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
		{
			sql += " AND ( B.OrgNo='" + WebUser.getOrgNo() + "' )";
			sql +=  " UNION " +sql6 ;
			sql += " UNION " + sql7 ;
			sql = "SELECT * From ("+sql+")A ORDER BY Idx, SortIdx ";
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			return dt;
		}

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			sql += " AND A.OrgNo='" + WebUser.getOrgNo() + "'";
		}

		sql += " ORDER BY B.Idx, A.Idx";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return dt;

	}
	/**
	 获得一个人可以发起的流程列表

	 @param userNo
	 @return
	*/

	public static DataTable DB_Start_Old(String userNo)
	{
		return DB_Start_Old(userNo, null);
	}

	public static DataTable DB_Start_Old()
	{
		return DB_Start_Old(null, null);
	}

	public static DataTable DB_Start_Old(String userNo, String domainExt)
	{
		if (DataType.IsNullOrEmpty(userNo) == true)
		{
			userNo = WebUser.getUserID();
		}

		String sqlEnd = "";
		if (DataType.IsNullOrEmpty(domainExt) == false)
		{
			sqlEnd = "  AND C.doMainExt='" + domainExt + "'";
		}

		// 组成查询的sql. .sql部分有变动.
		String sql = "SELECT A.No,A.Name,a.IsBatchStart,a.FK_FlowSort,C.Name AS FK_FlowSortText,C.DomainExt,A.IsStartInMobile, A.Idx,A.WorkModel";
		sql += " FROM WF_Flow A, V_FlowStarterBPM B, WF_FlowSort C  ";

		sql += " WHERE A.No=B.FK_Flow AND A.IsCanStart=1 AND A.FK_FlowSort=C.No  AND B.FK_Emp='" + userNo + "' " + sqlEnd;

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
		{
			sql += " AND ( B.OrgNo='" + WebUser.getOrgNo() + "' )";
		}

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			sql += " AND A.OrgNo='" + WebUser.getOrgNo() + "'";
		}

		sql += " ORDER BY C.Idx, A.Idx";

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
		{
			sql = "(" + sql + ") UNION ";
			String sqlTable = " (SELECT A.FK_Flow AS FK_Flow,A.FlowName As FlowName,C.No AS FK_Emp,B.OrgNo AS OrgNo From WF_Node A,WF_FlowOrg B,Port_Emp C WHERE A.FK_Flow=B.FlowNo AND A.DeliveryWay = 22 AND B.OrgNo='" + WebUser.getOrgNo() + "') B";
			sql += " (SELECT A.No,A.Name,a.IsBatchStart,a.FK_FlowSort,C.Name AS FK_FlowSortText,C.DomainExt,A.IsStartInMobile, A.Idx,A.WorkModel";
			sql += " FROM WF_Flow A, " + sqlTable + " , WF_FlowSort C  ";

			sql += " WHERE A.No=B.FK_Flow AND A.IsCanStart=1 AND A.FK_FlowSort=C.No  AND B.FK_Emp='" + userNo + "'  ORDER BY C.Idx, A.Idx)  ";
		}

		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("NO").ColumnName = "No";
			dt.Columns.get("NAME").ColumnName = "Name";
			dt.Columns.get("ISBATCHSTART").ColumnName = "IsBatchStart";
			dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
			dt.Columns.get("FK_FLOWSORTTEXT").ColumnName = "FK_FlowSortText";
			dt.Columns.get("ISSTARTINMOBILE").ColumnName = "IsStartInMobile";
			dt.Columns.get("IDX").ColumnName = "Idx";
			dt.Columns.get("WORKMODEL").ColumnName = "WorkModel";
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("no").ColumnName = "No";
			dt.Columns.get("name").ColumnName = "Name";
			dt.Columns.get("isbatchstart").ColumnName = "IsBatchStart";
			dt.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";
			dt.Columns.get("fk_flowsorttext").ColumnName = "FK_FlowSortText";
			dt.Columns.get("isstartinmobile").ColumnName = "IsStartInMobile";
			dt.Columns.get("idx").ColumnName = "Idx";
			dt.Columns.get("workmodel").ColumnName = "WorkModel";

		}
		return dt;
	}
	/**
	 获取指定用户可以发起的流程类别

	 @param userNo 用户编号
	 @return
	*/
	public static DataTable DB_GenerCanStartFlowsTree(String userNo)
	{
		//发起.
		DataTable table = DB_Start(userNo);
		table.Columns.Add("ParentNo");
		table.Columns.Add("ICON");
		String flowSort = String.format("select No,Name,ParentNo from WF_FlowSort");

		DataTable sortTable = DBAccess.RunSQLReturnTable(flowSort);
		for (DataRow row : sortTable.Rows)
		{
			DataRow newRow = table.NewRow();
			newRow.setValue("No", row.get("No"));
			newRow.setValue("Name", row.get("Name"));
			newRow.setValue("ParentNo", row.get("ParentNo"));
			newRow.setValue("ICON", "icon-tree_folder");
			table.Rows.add(newRow);
		}

		for (DataRow row : table.Rows)
		{
			if (DataType.IsNullOrEmpty(row.get("ParentNo").toString()))
			{
				row.setValue("ParentNo", row.get("FK_FlowSort"));
			}
			if (DataType.IsNullOrEmpty(row.get("ICON").toString()))
			{
				row.setValue("ICON", "icon-4");
			}
		}
		return table;
	}


	/**
	 获取(同表单)合流点上的子线程
	 说明:如果您要想在合流点看到所有的子线程运行的状态.

	 @param nodeIDOfHL 合流点ID
	 @param workid 工作ID
	 @return 与表WF_GenerWorkerlist结构类同的datatable.
	*/
	public static DataTable DB_GenerHLSubFlowDtl_TB(int nodeIDOfHL, long workid) throws Exception {
		Node nd = new Node(nodeIDOfHL);
		Work wk = nd.getHisWork();
		wk.setOID(workid);
		wk.Retrieve();

		GenerWorkerLists wls = new GenerWorkerLists();
		QueryObject qo = new QueryObject(wls);
		qo.AddWhere(GenerWorkerListAttr.FID, wk.getOID());
		qo.addAnd();
		qo.AddWhere(GenerWorkerListAttr.IsEnable, 1);
		qo.addAnd();
		qo.AddWhere(GenerWorkerListAttr.FK_Node, nd.getFromNodes().get(0).GetValByKey(NodeAttr.NodeID));

		DataTable dt = qo.DoQueryToTable();
		if (dt.Rows.size() == 1)
		{
			qo.clear();
			qo.AddWhere(GenerWorkerListAttr.FID, wk.getOID());
			qo.addAnd();
			qo.AddWhere(GenerWorkerListAttr.IsEnable, 1);
			return qo.DoQueryToTable();
		}
		return dt;
	}
	/**
	 获取(异表单)合流点上的子线程

	 @param nodeIDOfHL 合流点ID
	 @param workid 工作ID
	 @return 与表WF_GenerWorkerlist结构类同的datatable.
	*/
	public static DataTable DB_GenerHLSubFlowDtl_YB(int nodeIDOfHL, long workid) throws Exception {
		Node nd = new Node(nodeIDOfHL);
		Work wk = nd.getHisWork();
		wk.setOID(workid);
		wk.Retrieve();

		GenerWorkerLists wls = new GenerWorkerLists();
		QueryObject qo = new QueryObject(wls);
		qo.AddWhere(GenerWorkerListAttr.FID, wk.getOID());
		qo.addAnd();
		qo.AddWhere(GenerWorkerListAttr.IsEnable, 1);
		qo.addAnd();
		qo.AddWhere(GenerWorkerListAttr.IsPass, 0);
		return qo.DoQueryToTable();
	}

		///#endregion 获取当前操作员可以发起的流程集合


		///#region 流程草稿
	/**
	 获取当前操作员的指定流程的流程草稿数据

	 @param flowNo 流程编号
	 @return 返回草稿数据集合,列信息. OID=工作ID,Title=标题,RDT=记录日期,FK_Flow=流程编号,FID=流程ID, FK_Node=节点ID
	*/

	public static DataTable DB_GenerDraftDataTable(String flowNo)
	{
		return DB_GenerDraftDataTable(flowNo, null);
	}

	public static DataTable DB_GenerDraftDataTable()
	{
		return DB_GenerDraftDataTable(null, null);
	}

	public static DataTable DB_GenerDraftDataTable(String flowNo, String domainExt)
	{
		/*获取数据.*/
		String dbStr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		if (DataType.IsNullOrEmpty(domainExt) == true)
		{
			if (flowNo == null)
			{
				ps.SQL = "SELECT WorkID,Title,FK_Flow,FlowName,RDT,FlowNote,AtPara,FK_Node,FID FROM WF_GenerWorkFlow A WHERE WFState=1 AND Starter=" + dbStr + "Starter ORDER BY RDT";
				ps.Add(GenerWorkFlowAttr.Starter, WebUser.getNo(), false);
			}
			else
			{
				ps.SQL = "SELECT WorkID,Title,FK_Flow,FlowName,RDT,FlowNote,AtPara,FK_Node,FID FROM WF_GenerWorkFlow A WHERE WFState=1 AND Starter=" + dbStr + "Starter AND FK_Flow=" + dbStr + "FK_Flow ORDER BY RDT";
				ps.Add(GenerWorkFlowAttr.FK_Flow, flowNo, false);
				ps.Add(GenerWorkFlowAttr.Starter, WebUser.getNo(), false);
			}

		}
		else
		{
			if (flowNo == null)
			{
				ps.SQL = "SELECT WorkID,Title,FK_Flow,FlowName,RDT,FlowNote,AtPara,FK_Node,FID FROM WF_GenerWorkFlow A WHERE WFState=1 AND Starter=" + dbStr + "Starter AND DomainExt=" + dbStr + "DomainExt ORDER BY RDT";
				ps.Add(GenerWorkFlowAttr.Starter, WebUser.getNo(), false);
				ps.Add(GenerWorkFlowAttr.DomainExt, domainExt, false);
			}
			else
			{
				ps.SQL = "SELECT WorkID,Title,FK_Flow,FlowName,RDT,FlowNote,AtPara,FK_Node,FID FROM WF_GenerWorkFlow A WHERE WFState=1 AND Starter=" + dbStr + "Starter AND FK_Flow=" + dbStr + "FK_Flow AND DomainExt=" + dbStr + "DomainExt ORDER BY RDT";
				ps.Add(GenerWorkFlowAttr.FK_Flow, flowNo, false);
				ps.Add(GenerWorkFlowAttr.Starter, WebUser.getNo(), false);
				ps.Add(GenerWorkFlowAttr.DomainExt, domainExt, false);
			}
		}

		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("RDT").ColumnName = "RDT";
			dt.Columns.get("FLOWNOTE").ColumnName = "FlowNote";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("ATPARA").ColumnName = "AtPara";
			dt.Columns.get("FID").ColumnName = "FID";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("rdt").ColumnName = "RDT";
			dt.Columns.get("flownote").ColumnName = "FlowNote";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("atpara").ColumnName = "AtPara";
			dt.Columns.get("fid").ColumnName = "FID";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
		}
		return dt;
	}

		///#endregion 流程草稿


		///#region 我关注的流程
	/**
	 获得我关注的流程列表

	 @param flowNo 流程编号
	 @param userNo 操作员编号
	 @return 返回当前关注的流程列表.
	*/

	public static DataTable DB_Focus(String flowNo, String userNo)
	{
		return DB_Focus(flowNo, userNo, null);
	}

	public static DataTable DB_Focus(String flowNo)
	{
		return DB_Focus(flowNo, null, null);
	}

	public static DataTable DB_Focus()
	{
		return DB_Focus(null, null, null);
	}

	public static DataTable DB_Focus(String flowNo, String userNo, String domainExt)
	{
		if (Objects.equals(flowNo, ""))
		{
			flowNo = null;
		}

		if (userNo == null)
		{
			userNo = WebUser.getNo();
		}

		//if (domainExt == null)
		//    domainExt = "";

		//执行sql.
		Paras ps = new Paras();
		ps.SQL = "SELECT * FROM WF_GenerWorkFlow WHERE AtPara LIKE  '%F_" + userNo + "=1%'";
		if (flowNo != null)
		{
			ps.SQL = "SELECT * FROM WF_GenerWorkFlow WHERE AtPara LIKE  '%F_" + userNo + "=1%' AND FK_Flow=" + SystemConfig.getAppCenterDBVarStr() + "FK_Flow";
			ps.Add("FK_Flow", flowNo, false);
		}
		if (DataType.IsNullOrEmpty(domainExt) == false && DataType.IsNullOrEmpty(flowNo) == false)
		{
			ps.SQL = "SELECT * FROM WF_GenerWorkFlow WHERE AtPara LIKE  '%F_" + userNo + "=1%' AND FK_Flow=" + SystemConfig.getAppCenterDBVarStr() + "FK_Flow AND  DomainExt=" + SystemConfig.getAppCenterDBVarStr() + "DomainExt";
			ps.Add("FK_Flow", flowNo, false);
			ps.Add("DomainExt", domainExt, false);
		}

		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		//添加oracle的处理
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("WFSTA").ColumnName = "WFSta";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";
			dt.Columns.get("RDT").ColumnName = "RDT";
			dt.Columns.get("BILLNO").ColumnName = "BillNo";
			dt.Columns.get("FLOWNOTE").ColumnName = "FlowNote";
			dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FK_DEPT").ColumnName = "FK_Dept";
			dt.Columns.get("FID").ColumnName = "FID";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			dt.Columns.get("WFSTATE").ColumnName = "WFState";
			dt.Columns.get("FK_NY").ColumnName = "FK_NY";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("SENDER").ColumnName = "Sender";
			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dt.Columns.get("PRI").ColumnName = "PRI";
			dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";
			dt.Columns.get("SDTOFFLOW").ColumnName = "SDTOfFlow";
			dt.Columns.get("PFLOWNO").ColumnName = "PFlowNo";
			dt.Columns.get("PWORKID").ColumnName = "PWorkID";
			dt.Columns.get("PNODEID").ColumnName = "PNodeID";
			dt.Columns.get("PFID").ColumnName = "PFID";
			dt.Columns.get("PEMP").ColumnName = "PEmp";
			dt.Columns.get("GUESTNO").ColumnName = "GuestNo";
			dt.Columns.get("GUESTNAME").ColumnName = "GuestName";
			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";
			dt.Columns.get("TODOEMPSNUM").ColumnName = "TodoEmpsNum";
			dt.Columns.get("TASKSTA").ColumnName = "TaskSta";
			dt.Columns.get("ATPARA").ColumnName = "AtPara";
			dt.Columns.get("EMPS").ColumnName = "Emps";
			dt.Columns.get("GUID").ColumnName = "GUID";
			dt.Columns.get("WEEKNUM").ColumnName = "WeekNum";
			dt.Columns.get("TSPAN").ColumnName = "TSpan";
			dt.Columns.get("TODOSTA").ColumnName = "TodoSta";
			dt.Columns.get("SYSTYPE").ColumnName = "SysType";

			// dt.Columns.get("CFLOWNO").ColumnName = "CFlowNo";
			// dt.Columns.get("CWORKID").ColumnName = "CWorkID";
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("startername").ColumnName = "StarterName";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("wfsta").ColumnName = "WFSta";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("rdt").ColumnName = "RDT";
			dt.Columns.get("billno").ColumnName = "BillNo";
			dt.Columns.get("flownote").ColumnName = "FlowNote";
			dt.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("fk_dept").ColumnName = "FK_Dept";
			dt.Columns.get("fid").ColumnName = "FID";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			dt.Columns.get("wfstate").ColumnName = "WFState";
			dt.Columns.get("fk_ny").ColumnName = "FK_NY";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("sender").ColumnName = "Sender";
			dt.Columns.get("deptname").ColumnName = "DeptName";
			dt.Columns.get("pri").ColumnName = "PRI";
			dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";
			dt.Columns.get("sdtofflow").ColumnName = "SDTOfFlow";
			dt.Columns.get("pflowno").ColumnName = "PFlowNo";
			dt.Columns.get("pworkid").ColumnName = "PWorkID";
			dt.Columns.get("pnodeid").ColumnName = "PNodeID";
			dt.Columns.get("pfid").ColumnName = "PFID";
			dt.Columns.get("pemp").ColumnName = "PEmp";
			dt.Columns.get("guestno").ColumnName = "GuestNo";
			dt.Columns.get("guestname").ColumnName = "GuestName";
			dt.Columns.get("todoemps").ColumnName = "TodoEmps";
			dt.Columns.get("todoempsnum").ColumnName = "TodoEmpsNum";
			dt.Columns.get("tasksta").ColumnName = "TaskSta";
			dt.Columns.get("atpara").ColumnName = "AtPara";
			dt.Columns.get("emps").ColumnName = "Emps";
			dt.Columns.get("guid").ColumnName = "GUID";
			dt.Columns.get("weeknum").ColumnName = "WeekNum";
			dt.Columns.get("tspan").ColumnName = "TSpan";
			dt.Columns.get("todosta").ColumnName = "TodoSta";
			dt.Columns.get("systype").ColumnName = "SysType";

			// dt.Columns.get("CFLOWNO").ColumnName = "CFlowNo";
			// dt.Columns.get("CWORKID").ColumnName = "CWorkID";
		}
		return dt;
	}

		///#endregion 我关注的流程


		///#region 获取当前操作员的共享工作
	/**
	 获得待办

	 @param userNo 操作员编号
	 @param fk_node 节点ID
	 @param flowNo 流程编号
	 @return
	*/

	public static DataTable DB_Todolist(String userNo, int fk_node, String flowNo) throws Exception {
		return DB_Todolist(userNo, fk_node, flowNo, null);
	}

	public static DataTable DB_Todolist(String userNo, int fk_node) throws Exception {
		return DB_Todolist(userNo, fk_node, null, null);
	}

//	public static DataTable DB_Todolist(String userNo)
//	{
//		return DB_Todolist(userNo, 0, null, null);
//	}

	public static DataTable DB_Todolist(String userNo, int fk_node, String flowNo, String domainExt) throws Exception {
		String sql = "";
		sql = "SELECT A.* FROM WF_GenerWorkFlow A, WF_FlowSort B, WF_Flow C, WF_GenerWorkerlist D ";
		sql += " WHERE (WFState=2 OR WFState=5 OR WFState=8)";
		sql += " AND A.FK_FlowSort=B.No ";
		sql += " AND A.FK_Flow=C.No ";
		sql += " AND A.FK_Node=D.FK_Node ";
		sql += " AND A.WorkID=D.WorkID ";
		sql += " AND D.IsPass=0  "; // = 90 是会签主持人.
		sql += " AND D.FK_Emp='" + userNo + "'";

		//节点ID.
		if (fk_node != 0)
		{
			sql += " AND A.FK_Node=" + fk_node;
		}

		//流程编号.
		if (flowNo != null)
		{
			sql += " AND C.No='" + flowNo + "'";
		}

		//域.
		if (domainExt != null)
		{
			sql += " AND B.DomainExt='" + domainExt + "'";
		}


		//  sql += "  ORDER BY  B.Idx, C.Idx, A.RDT DESC ";
		sql += "  ORDER BY  A.RDT DESC ";


		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		//添加oracle的处理
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("PRI").ColumnName = "PRI";
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("TITLE").ColumnName = "Title";
			// dt.Columns.get("ISREAD").ColumnName = "IsRead";
			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
			dt.Columns.get("WFSTATE").ColumnName = "WFState";
			dt.Columns.get("FK_DEPT").ColumnName = "FK_Dept";
			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("PWORKID").ColumnName = "PWorkID";
			dt.Columns.get("PFLOWNO").ColumnName = "PFlowNo";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";
			dt.Columns.get("FID").ColumnName = "FID";
			dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
			dt.Columns.get("SYSTYPE").ColumnName = "SysType";
			dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";
			dt.Columns.get("GUESTNO").ColumnName = "GuestNo";
			dt.Columns.get("GUESTNAME").ColumnName = "GuestName";
			dt.Columns.get("BILLNO").ColumnName = "BillNo";
			dt.Columns.get("FLOWNOTE").ColumnName = "FlowNote";
			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";
			dt.Columns.get("TODOEMPSNUM").ColumnName = "TodoEmpsNum";
			dt.Columns.get("TODOSTA").ColumnName = "TodoSta";
			dt.Columns.get("TASKSTA").ColumnName = "TaskSta";
			dt.Columns.get("SENDER").ColumnName = "Sender";
			dt.Columns.get("ATPARA").ColumnName = "AtPara";
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("pri").ColumnName = "PRI";
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("title").ColumnName = "Title";
			// dt.Columns.get("ISREAD").ColumnName = "IsRead";
			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("startername").ColumnName = "StarterName";
			dt.Columns.get("wfstate").ColumnName = "WFState";
			dt.Columns.get("fk_dept").ColumnName = "FK_Dept";
			dt.Columns.get("deptname").ColumnName = "DeptName";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("pworkid").ColumnName = "PWorkID";
			dt.Columns.get("pflowno").ColumnName = "PFlowNo";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("fid").ColumnName = "FID";
			dt.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";
			dt.Columns.get("systype").ColumnName = "SysType";
			dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";
			dt.Columns.get("guestno").ColumnName = "GuestNo";
			dt.Columns.get("guestname").ColumnName = "GuestName";
			dt.Columns.get("billno").ColumnName = "BillNo";
			dt.Columns.get("flownote").ColumnName = "FlowNote";
			dt.Columns.get("todoemps").ColumnName = "TodoEmps";
			dt.Columns.get("todoempsnum").ColumnName = "TodoEmpsNum";
			dt.Columns.get("todosta").ColumnName = "TodoSta";
			dt.Columns.get("tasksta").ColumnName = "TaskSta";
			dt.Columns.get("sender").ColumnName = "Sender";
			dt.Columns.get("atpara").ColumnName = "AtPara";
		}
		//#region 加入单据的待办.
		if (fk_node > 0 || DataType.IsNullOrEmpty(flowNo) == false)
		{
		}
		else
		{
			dt = DB_GenerEmpWorksOfDataTable_AddFrmBill(dt, userNo);
		}
		//#endregion 加入单据的待办
		return dt;
	}
	private static DataTable DB_GenerEmpWorksOfDataTable_AddFrmBill(DataTable dt, String userNo) throws Exception {
		DataTable dtBill = bp.ccbill.Dev2Interface.DB_Todolist(userNo);
		for (DataRow dr : dtBill.Rows)
		{
			DataRow myDR = dt.NewRow();
			myDR.setValue("WorkID", dr.get("WorkID"));
			myDR.setValue("PRI", 0); //优先级.
			myDR.setValue("FID", 0); //FID.
			myDR.setValue("TodoEmpsNum", 0); //待办人员数量.
			myDR.setValue("TaskSta", 0); //任务状态.
			myDR.setValue("TodoSta", 0); //待办状态.

			myDR.setValue("Title", dr.get("Title"));
			myDR.setValue("Starter", dr.get("Starter"));
			myDR.setValue("StarterName", dr.get("StarterName"));
			myDR.setValue("WFState", dr.get("BillState"));
			myDR.setValue("FK_Flow", dr.get("FrmID"));
			myDR.setValue("FlowName", dr.get("FrmName"));

			myDR.setValue("FK_Node", 0); // dr["FrmID"];
			myDR.setValue("NodeName", dr.get("FrmName"));
			//  myDR["RDT"] = dr["RDT"];
			myDR.setValue("TodoEmps", dr.get("EmpNo"));
			myDR.setValue("BillNo", dr.get("BillNo"));
			myDR.setValue("SDTOfNode", dr.get("SDTOfNode"));
			myDR.setValue("SysType", "FrmBill");

			myDR.setValue("FK_Dept", dr.get("DeptNo"));
			myDR.setValue("DeptName", dr.get("DeptName"));
			myDR.setValue("RDT", dr.get("RDT"));
			myDR.setValue("AtPara", dr.get("AtPara"));

			myDR.setValue("Sender", dr.get("Sender"));
			myDR.setValue("SendDT", dr.get("SendDT"));
			myDR.setValue("ADT", DataType.getCurrentDate()); //接受日期.
			myDR.setValue("ADT",dr.get("SendDT")); //接受日期.
			dt.Rows.add(myDR);
		}
		return dt;
	}

	/**
	 获取当前人员待处理的工作

	 @param userNo 用户编号
	 @param nodeID 指定的节点，如果为0就是所有的节点.
	 @param wfstate WFState状态，=5退回的，=2是正在运行的.
	 @param domainExt 域名
	 @return 返回待办WF_EmpWorks的视图待办.
	*/
	public static DataTable DB_GenerEmpWorksOfDataTable(String userNo, int nodeID, String wfstate, String domainExt, String flowNo, String isRead) throws Exception {
		return DB_GenerEmpWorksOfDataTable(userNo, nodeID, wfstate, domainExt, flowNo, isRead, "ADT");
	}

	public static DataTable DB_GenerEmpWorksOfDataTable(String userNo, int nodeID, String wfstate, String domainExt, String flowNo) throws Exception {
		return DB_GenerEmpWorksOfDataTable(userNo, nodeID, wfstate, domainExt, flowNo, null, "ADT");
	}

	public static DataTable DB_GenerEmpWorksOfDataTable(String userNo, int nodeID, String wfstate, String domainExt) throws Exception {
		return DB_GenerEmpWorksOfDataTable(userNo, nodeID, wfstate, domainExt, null, null, "ADT");
	}

	public static DataTable DB_GenerEmpWorksOfDataTable(String userNo, int nodeID, String wfstate) throws Exception {
		return DB_GenerEmpWorksOfDataTable(userNo, nodeID, wfstate, null, null, null, "ADT");
	}

	public static DataTable DB_GenerEmpWorksOfDataTable(String userNo, int nodeID) throws Exception {
		return DB_GenerEmpWorksOfDataTable(userNo, nodeID, null, null, null, null, "ADT");
	}

	public static DataTable DB_GenerEmpWorksOfDataTable(String userNo) throws Exception {
		return DB_GenerEmpWorksOfDataTable(userNo, 0, null, null, null, null, "ADT");
	}

	public static DataTable DB_GenerEmpWorksOfDataTable(String userNo, int nodeID, String wfstate, String domainExt, String flowNo, String isRead, String orderBy) throws Exception {
		if (DataType.IsNullOrEmpty(userNo) == true)
		{
			throw new RuntimeException("err@登录信息丢失.");
		}
		if (DataType.IsNullOrEmpty(orderBy) == true)
		{
			orderBy = "ADT";
		}
		String whereSQL = " ";

		if (DataType.IsNullOrEmpty(domainExt) == false)
		{
			whereSQL = " AND A.DomainExt='" + domainExt + "'";
		}

		if (DataType.IsNullOrEmpty(wfstate) == false)
		{
			whereSQL = " AND A.WFState='" + wfstate + "'";
		}

		if (nodeID != 0)
		{
			whereSQL = " AND A.FK_Node='" + nodeID + "'";
		}

		if (flowNo != null)
		{
			whereSQL = " AND A.FK_Flow='" + flowNo + "'";
		}

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			whereSQL = " AND A.OrgNo='" + WebUser.getOrgNo() + "'";
		}

		//获得授权信息.
		Auths aths = new Auths();
		aths.Retrieve(AuthAttr.AutherToEmpNo, userNo, null);

		String sql;
		/*不是授权状态*/
		if (Objects.equals(SystemConfig.getCustomerNo(), "TianYe"))
		{
			if (bp.wf.Glo.isEnableTaskPool() == true)
			{
				sql = "SELECT A.* FROM BPM.WF_EmpWorks A, BPM.WF_Flow B, BPM.WF_FlowSort C WHERE A.FK_Flow=B.No AND B.FK_FlowSort=C.No AND A.FK_Emp='" + userNo + "' AND A.TaskSta=0 " + whereSQL + " ORDER BY C.Idx, B.Idx, ADT DESC ";
			}
			else
			{
				sql = "SELECT A.* FROM BPM.WF_EmpWorks A, BPM.WF_Flow B, BPM.WF_FlowSort C WHERE A.FK_Flow=B.No AND B.FK_FlowSort=C.No AND A.FK_Emp='" + userNo + "'  " + whereSQL + " ORDER BY C.Idx,B.Idx, A.ADT DESC ";
			}
		}
		else
		{
			if (bp.wf.Glo.isEnableTaskPool() == true)
			{
				sql = "SELECT  A.*, null as Auther FROM WF_EmpWorks A WHERE  TaskSta=0 AND A.FK_Emp='" + userNo + "' " + whereSQL + "";
			}
			else
			{
				sql = "SELECT  A.*, null as Auther FROM WF_EmpWorks A WHERE  A.FK_Emp='" + userNo + "' " + whereSQL + "";
			}

			for (Auth ath : aths.ToJavaList())
			{

				String todata = ath.getTakeBackDT().replace("-", "");
				if (DataType.IsNullOrEmpty(ath.getTakeBackDT()) == false)
				{
					int mydt = Integer.parseInt(todata);
					int nodt = Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
					if (mydt < nodt)
					{
						continue;
					}
					sql += " UNION ";

					if (ath.getAuthType() == AuthorWay.SpecFlows)
					{
						sql += "SELECT A.*,'" + ath.getAuther() + "' as Auther FROM WF_EmpWorks A WHERE  FK_Emp='" + ath.getAuther() + "' AND FK_Flow='" + ath.getFlowNo() + "' AND WFState>1 " + whereSQL + "";
					}
					else
					{
						sql += "SELECT A.*,'" + ath.getAuther() + "' as Auther FROM WF_EmpWorks A WHERE  FK_Emp='" + ath.getAuther() + "' AND WFState>1 " + whereSQL + "";
					}


				}
			}

			//增加筛选已读或未读判断
			if(DataType.IsNullOrEmpty(isRead) == false)
			{
				sql += " AND IsRead = " + isRead;
			}

			//如果按照流程顺序排序
			if (orderBy.equals("FlowIdx"))
			{
				sql += " ORDER BY FlowSortIdx, FlowIdx ASC,ADT DESC";
			}
			else
			{
				sql += " ORDER BY " + orderBy + " DESC";
			}

		}

		//获得待办.
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if(SystemConfig.getCustomerNo().equals("TianYu")){
			String  sortSQL="WITH RECURSIVE cte AS ( SELECT No, ParentNo, CAST(Name AS CHAR(3000)) AS path FROM WF_FlowSort WHERE No!='1' UNION ALL"
					+" SELECT n.No, n.ParentNo, CONCAT(cte.path, '_', n.Name) FROM WF_FlowSort n INNER JOIN cte ON n.ParentNo = cte.No)"
					+"SELECT No, ParentNo, path FROM cte";
			DataTable sortDt = DBAccess.RunSQLReturnTable(sortSQL);
			dt.Columns.Add("SortName");
			for(DataRow dr :dt.Rows){
				if(dr.getValue("FK_FlowSort")==null || dr.getValue("FK_FlowSort").toString().equals(""))
					dr.setValue("SortName","");
				else{
					List<DataRow> drr = sortDt.select("No='"+dr.getValue("FK_FlowSort").toString()+"'");
					if(drr.size()>0)
						dr.setValue("SortName",drr.get(0).getValue("path"));
					else
						dr.setValue("SortName","");
				}

			}
		}

		//添加oracle的处理
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("PRI").ColumnName = "PRI";
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("ISREAD").ColumnName = "IsRead";
			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
			dt.Columns.get("WFSTATE").ColumnName = "WFState";
			dt.Columns.get("FK_DEPT").ColumnName = "FK_Dept";
			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("PWORKID").ColumnName = "PWorkID";
			dt.Columns.get("PFLOWNO").ColumnName = "PFlowNo";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";
			//dt.Columns.get("WORKERDEPT").ColumnName = "WorkerDept";
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("RDT").ColumnName = "RDT";
			dt.Columns.get("ADT").ColumnName = "ADT";
			dt.Columns.get("SDT").ColumnName = "SDT";
			dt.Columns.get("FK_EMP").ColumnName = "FK_Emp";
			dt.Columns.get("FID").ColumnName = "FID";
			dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
			dt.Columns.get("SYSTYPE").ColumnName = "SysType";
			dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";
			dt.Columns.get("SENDDT").ColumnName = "SendDT";
			//dt.Columns.get("PRESSTIMES").ColumnName = "PressTimes";
			dt.Columns.get("GUESTNO").ColumnName = "GuestNo";
			dt.Columns.get("GUESTNAME").ColumnName = "GuestName";
			dt.Columns.get("BILLNO").ColumnName = "BillNo";
			//dt.Columns.get("FLOWNOTE").ColumnName = "FlowNote";
			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";
			dt.Columns.get("TODOEMPSNUM").ColumnName = "TodoEmpsNum";
			dt.Columns.get("TODOSTA").ColumnName = "TodoSta";
			dt.Columns.get("TASKSTA").ColumnName = "TaskSta";
			dt.Columns.get("LISTTYPE").ColumnName = "ListType";
			dt.Columns.get("SENDER").ColumnName = "Sender";
			dt.Columns.get("ATPARA").ColumnName = "AtPara";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("pri").ColumnName = "PRI";
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("isread").ColumnName = "IsRead";
			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("startername").ColumnName = "StarterName";
			dt.Columns.get("wfstate").ColumnName = "WFState";
			dt.Columns.get("fk_dept").ColumnName = "FK_Dept";
			dt.Columns.get("deptname").ColumnName = "DeptName";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("pworkid").ColumnName = "PWorkID";
			dt.Columns.get("pflowno").ColumnName = "PFlowNo";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			//  dt.Columns.get("workerdept").ColumnName = "WorkerDept";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("rdt").ColumnName = "RDT";
			dt.Columns.get("adt").ColumnName = "ADT";
			dt.Columns.get("sdt").ColumnName = "SDT";
			dt.Columns.get("fk_emp").ColumnName = "FK_Emp";
			dt.Columns.get("fid").ColumnName = "FID";
			dt.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";
			dt.Columns.get("systype").ColumnName = "SysType";
			dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";
			dt.Columns.get("senddt").ColumnName = "SendDT";
			//dt.Columns.get("presstimes").ColumnName = "PressTimes";
			dt.Columns.get("guestno").ColumnName = "GuestNo";
			dt.Columns.get("guestname").ColumnName = "GuestName";
			dt.Columns.get("billno").ColumnName = "BillNo";
			//dt.Columns.get("flownote").ColumnName = "FlowNote";
			dt.Columns.get("todoemps").ColumnName = "TodoEmps";
			dt.Columns.get("todoempsnum").ColumnName = "TodoEmpsNum";
			dt.Columns.get("todosta").ColumnName = "TodoSta";
			dt.Columns.get("tasksta").ColumnName = "TaskSta";
			dt.Columns.get("listtype").ColumnName = "ListType";
			dt.Columns.get("sender").ColumnName = "Sender";
			dt.Columns.get("atpara").ColumnName = "AtPara";

		}
		for(int j=0;j<dt.Rows.size();j++){
			String WorkID=dt.Rows.get(j).getValue("WorkID").toString();
			String FK_Flow=dt.Rows.get(j).getValue("FK_Flow").toString();
			//workid创建时间
			String createTime=queryWorkIDCreateTime(FK_Flow, Long.valueOf(WorkID));
			dt.Rows.get(j).put("RDT",createTime);

		}

		// #region 加入单据的待办.
		if (nodeID > 0 || DataType.IsNullOrEmpty(flowNo) == false)
		{
		}
		else
		{
			dt = DB_GenerEmpWorksOfDataTable_AddFrmBill(dt, userNo);
		}
        //    #endregion 加入单据的待办
		return dt;
	}
	/**
	 近期发起的工作.

	 @return
	*/

	public static DataTable DB_RecentStart() throws Exception {
		return DB_RecentStart(null);
	}

	public static DataTable DB_RecentStart(String flowNo) throws Exception {
		GenerWorkFlows gwfs = new GenerWorkFlows();
		QueryObject qo = new QueryObject(gwfs);
		qo.Top = 300;
		if (DataType.IsNullOrEmpty(flowNo) == false)
		{
			qo.AddWhere(GenerWorkFlowAttr.FK_Flow, flowNo);
			qo.addAnd();
			qo.AddWhere(GenerWorkFlowAttr.Starter, WebUser.getNo());
			qo.addAnd();
			qo.AddWhere(GenerWorkFlowAttr.WFState,">",1);
			qo.DoQuery();
		}
		else
		{
			qo.AddWhere(GenerWorkFlowAttr.Starter, WebUser.getNo());
			qo.addAnd();
			qo.AddWhere(GenerWorkFlowAttr.WFState,">",1);
			qo.DoQuery();
		}
		try {
			return gwfs.ToDataTableField("dt");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 超时工作

	 @return
	*/

	public static DataTable DB_Timeout()
	{
		return DB_Timeout(null);
	}

	public static DataTable DB_Timeout(String userNo)
	{
		if (userNo == null)
		{
			userNo = WebUser.getNo();
		}

		String sql = "SELECT A.Title,A.WorkID, A.FK_Flow as FlowNo, A.FlowName,A.FK_Node as NodeID,A.NodeName,";
		sql += " A.Starter,A.StarterName,A.PRI,B.SDT,B.IsRead,A.WFState,A.RDT,A.BillNo ";
		sql += " FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B ";
		sql += " WHERE A.WorkID = B.WorkID AND A.FK_Node = B.FK_Node AND B.SDT >= '" + DataType.getCurrentDate() + (SystemConfig.getAppCenterDBType() == DBType.MSSQL ? "' AND LEN(B.SDT) > 8" : "' AND LENGTH(B.SDT) > 8 ");
		sql += " AND B.FK_Emp ='" + userNo + "' AND A.WFState>1";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		//添加oracle的处理
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("FLOWNO").ColumnName = "FlowNo";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("NODEID").ColumnName = "NodeID";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";
			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
			dt.Columns.get("PRI").ColumnName = "PRI";
			dt.Columns.get("ISREAD").ColumnName = "IsRead";
			dt.Columns.get("SDT").ColumnName = "SDT";
			dt.Columns.get("WFSTATE").ColumnName = "WFState";
			dt.Columns.get("RDT").ColumnName = "RDT";
			dt.Columns.get("BILLNO").ColumnName = "BillNo";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("flowno").ColumnName = "FlowNo";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("nodeid").ColumnName = "NodeID";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("startername").ColumnName = "StarterName";
			dt.Columns.get("pri").ColumnName = "PRI";
			dt.Columns.get("isread").ColumnName = "IsRead";
			dt.Columns.get("sdt").ColumnName = "SDT";
			dt.Columns.get("wfstate").ColumnName = "WFState";
			dt.Columns.get("rdt").ColumnName = "RDT";
			dt.Columns.get("billno").ColumnName = "BillNo";
		}
		return dt;
	}
	/**
	 获得待办(包括被授权的待办)
	 区分是自己的待办，还是被授权的待办通过数据源的 FK_Emp 字段来区分。

	 @return
	*/

	public static DataTable DB_Todolist() throws Exception {
		return DB_Todolist(null);
	}

	public static DataTable DB_Todolist(String userNo) throws Exception {
		if (userNo == null)
		{
			userNo = WebUser.getNo();
			if (WebUser.getIsAuthorize() == false)
			{
				throw new RuntimeException("@授权登录的模式下不能调用此接口.");
			}
		}

		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		//String wfSql = "  WFState=" + (int)WFState.Askfor + " OR WFState=" + (int)WFState.Runing + "  OR WFState=" + (int)WFState.AskForReplay + " OR WFState=" + (int)WFState.Shift + " OR WFState=" + (int)WFState.ReturnSta + " OR WFState=" + (int)WFState.Fix;
		String wfSql = "  1=1  ";

		/*不是授权状态*/
		if (bp.wf.Glo.isEnableTaskPool() == true)
		{
			ps.SQL = "SELECT * FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp=" + dbstr + "FK_Emp AND TaskSta!=1 ";
		}
		else
		{
			ps.SQL = "SELECT * FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp=" + dbstr + "FK_Emp ";
		}

		ps.Add("FK_Emp", userNo, false);

		//获取授权给他的人员列表.
		Auths aths = new Auths();
		aths.Retrieve(AuthAttr.Auther, userNo, null);
		for (Auth auth : aths.ToJavaList())
		{
			//判断是否授权到期.
			if (Objects.equals(auth.getTakeBackDT(), ""))
			{
				continue;
			}

			switch (auth.getAuthType())
			{
				case All:
					if (bp.wf.Glo.isEnableTaskPool() == true)
					{
						ps.SQL += " UNION  SELECT * FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp='" + auth.getAuther() + "' AND TaskSta!=1  ";
					}
					else
					{
						ps.SQL += " UNION  SELECT * FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp='" + auth.getAuther() + "' ";
					}
					break;
				case SpecFlows:
					if (bp.wf.Glo.isEnableTaskPool() == true)
					{
						ps.SQL += " UNION SELECT * FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp='" + auth.getAuther() + "' AND  FK_Flow = '" + auth.getFlowNo() + "' AND TaskSta!=0 ";
					}
					else
					{
						ps.SQL += " UNION SELECT * FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp='" + auth.getAuther() + "' AND  FK_Flow = '" + auth.getFlowNo() + "'  ";
					}
					break;
				case None: //非授权状态下.
					continue;
				default:
					throw new RuntimeException("no such way...");
			}
		}
		return DBAccess.RunSQLReturnTable(ps);
	}
	/// <summary>
	/// 冻结流程
	/// </summary>
	/// <returns></returns>
	public static GenerWorkFlows DB_Fix() throws Exception {
		GenerWorkFlows gwfs = new GenerWorkFlows();
		QueryObject qo = new QueryObject(gwfs);
		qo.AddWhere("WFState", 9);
		qo.addAnd();
		qo.AddWhere(GenerWorkFlowAttr.TodoEmps, " LIKE ", "%" + WebUser.getNo() + "%");
		qo.addOrderBy("WorkID");
		qo.DoQuery();
		return gwfs;
	}
	/**
	 获得已完成数据统计列表

	 @param userNo 操作员编号
	 @return 具有FlowNo,FlowName,Num三个列的指定人员的待办列表
	*/
	public static DataTable DB_FlowCompleteGroup(String userNo)
	{
		/* 如果不是删除流程注册表. */
		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		ps.SQL = "SELECT FK_Flow as No,FlowName,COUNT(*) Num FROM WF_GenerWorkFlow WHERE Emps LIKE '%@" + userNo + "@%' AND FID=0 AND  (WFState=3 OR WFState=11) GROUP BY FK_Flow,FlowName";
		return DBAccess.RunSQLReturnTable(ps);
	}
	/**
	 获取指定页面已经完成流程

	 @param userNo 用户编号
	 @param flowNo 流程编号
	 @param pageSize 每页的数量
	 @param pageIdx 第几页
	 @return 用户编号
	*/
	public static DataTable DB_FlowComplete(String userNo, String flowNo, int pageSize, int pageIdx) throws Exception {
		/* 如果不是删除流程注册表. */
		GenerWorkFlows ens = new GenerWorkFlows();
		QueryObject qo = new QueryObject(ens);
		if (flowNo != null)
		{
			qo.AddWhere(GenerWorkFlowAttr.FK_Flow, flowNo);
			qo.addAnd();
		}
		qo.AddWhere(GenerWorkFlowAttr.FID, 0);
		qo.addAnd();
		qo.AddWhere(GenerWorkFlowAttr.WFState, "IN", "(3,11)");
		qo.addAnd();
		qo.AddWhere(GenerWorkFlowAttr.Emps, " LIKE ", " '%@" + userNo + "@%'");
		/**小周鹏修改-----------------------------START**/
		// qo.DoQuery(GenerWorkFlowAttr.WorkID,pageSize, pageIdx);
		qo.DoQuery(GenerWorkFlowAttr.WorkID, pageSize, pageIdx, GenerWorkFlowAttr.RDT, true);
		/**小周鹏修改-----------------------------END**/
		return ens.ToDataTableField("dt");
	}
	/**
	 查询指定流程中已完成的流程

	 @param userNo
	 @param pageCount
	 @param pageSize
	 @param pageIdx
	 @param strFlow
	 @return
	*/
	public static DataTable DB_FlowComplete(String userNo, int pageCount, int pageSize, int pageIdx, String strFlow) throws Exception {
		/* 如果不是删除流程注册表. */
		GenerWorkFlows ens = new GenerWorkFlows();
		QueryObject qo = new QueryObject(ens);
		qo.AddWhere(GenerWorkFlowAttr.FID, 0);
		qo.addAnd();
		qo.AddWhere(GenerWorkFlowAttr.WFState, "IN", "(3,11)");
		qo.addAnd();
		qo.AddWhere(GenerWorkFlowAttr.Emps, " LIKE ", " '%@" + userNo + "@%'");
		qo.addAnd();
		qo.AddWhere(GenerWorkFlowAttr.FK_Flow, strFlow);
		qo.DoQuery(GenerWorkFlowAttr.WorkID, pageSize, pageIdx);
		return ens.ToDataTableField("dt");
	}
	/**
	 查询指定流程中已完成的公告流程

	 @param pageSize 每页条数
	 @param pageIdx 页码
	 @param strFlow 流程编号
	 @return
	*/
	public static DataTable DB_FlowComplete(String strFlow, int pageSize, int pageIdx) throws Exception {

		/* 如果不是删除流程注册表. */
		GenerWorkFlows ens = new GenerWorkFlows();
		QueryObject qo = new QueryObject(ens);
		qo.AddWhere(GenerWorkFlowAttr.FID, 0);
		qo.addAnd();
		qo.AddWhere(GenerWorkFlowAttr.WFState, "IN", "(3,11)");
		qo.addAnd();
		qo.AddWhere(GenerWorkFlowAttr.FK_Flow, strFlow);
		qo.DoQuery(GenerWorkFlowAttr.WorkID, pageSize, pageIdx);
		return ens.ToDataTableField("dt");

	}

	/**
	 获取某一个人已完成的流程

	 @param userNo 用户编码
	 @return
	*/

	public static DataTable DB_FlowComplete(String userNo)
	{
		return DB_FlowComplete(userNo, false);
	}

	public static DataTable DB_FlowComplete(String userNo, boolean isMyStart)
	{
		DataTable dt = null;
		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		if (isMyStart == true)
		{
			ps.SQL = "SELECT 'RUNNING' AS Type, T.* FROM WF_GenerWorkFlow T WHERE T.Starter=" + dbstr + "Starter AND T.FID=0 AND (T.WFState=3 OR T.WFState=11) ORDER BY  RDT DESC";
			ps.Add("Starter", userNo, false);
		}
		else
		{
			ps.SQL = "SELECT 'RUNNING' AS Type, T.* FROM WF_GenerWorkFlow T WHERE (T.Emps LIKE '%@" + userNo + "@%' OR  T.Emps LIKE '%@" + userNo + ",%') AND T.FID=0 AND (T.WFState=3 OR T.WFState=11) ORDER BY  RDT DESC";
		}

		dt = DBAccess.RunSQLReturnTable(ps);

		//需要翻译.
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("TYPE").ColumnName = "Type";
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
			dt.Columns.get("SYSTYPE").ColumnName = "SysType";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("WFSTA").ColumnName = "WFSta";
			dt.Columns.get("WFSTATE").ColumnName = "WFState";
			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
			dt.Columns.get("SENDER").ColumnName = "Sender";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";
			dt.Columns.get("FK_DEPT").ColumnName = "FK_Dept";
			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";
			dt.Columns.get("SDTOFFLOW").ColumnName = "SDTOfFlow";
			dt.Columns.get("PFLOWNO").ColumnName = "PflowNo";
			dt.Columns.get("PWORKID").ColumnName = "PWorkID";
			dt.Columns.get("PNODEID").ColumnName = "PNodeID";
			dt.Columns.get("PEMP").ColumnName = "PEmp";
			dt.Columns.get("GUESTNO").ColumnName = "GuestNo";
			dt.Columns.get("GUESTNAME").ColumnName = "GuestName";
			dt.Columns.get("BILLNO").ColumnName = "BillNo";
			dt.Columns.get("FLOWNOTE").ColumnName = "FlowNote";
			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";
			dt.Columns.get("TODOEMPSNUM").ColumnName = "TodoEmpsNum";
			dt.Columns.get("TASKSTA").ColumnName = "TaskSta";
			dt.Columns.get("ATPARA").ColumnName = "AtPara";
			dt.Columns.get("EMPS").ColumnName = "Emps";
			dt.Columns.get("doMainExt").ColumnName = "DomainExt";
			dt.Columns.get("SENDDT").ColumnName = "SendDT";
			dt.Columns.get("WEEKNUM").ColumnName = "WeekNum";
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("type").ColumnName = "Type";
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";
			dt.Columns.get("systype").ColumnName = "SysType";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("wfsta").ColumnName = "WFSta";
			dt.Columns.get("wfstate").ColumnName = "WFState";
			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("startername").ColumnName = "StarterName";
			dt.Columns.get("sender").ColumnName = "Sender";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("fk_dept").ColumnName = "FK_Dept";
			dt.Columns.get("deptname").ColumnName = "DeptName";
			dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";
			dt.Columns.get("sdtofflow").ColumnName = "SDTOfFlow";
			dt.Columns.get("pflowno").ColumnName = "PflowNo";
			dt.Columns.get("pworkid").ColumnName = "PWorkID";
			dt.Columns.get("pnodeid").ColumnName = "PNodeID";
			dt.Columns.get("pemp").ColumnName = "PEmp";
			dt.Columns.get("guestno").ColumnName = "GuestNo";
			dt.Columns.get("guestname").ColumnName = "GuestName";
			dt.Columns.get("billno").ColumnName = "BillNo";
			dt.Columns.get("flownote").ColumnName = "FlowNote";
			dt.Columns.get("todoemps").ColumnName = "TodoEmps";
			dt.Columns.get("todoempsnum").ColumnName = "TodoEmpsNum";
			dt.Columns.get("tasksta").ColumnName = "TaskSta";
			dt.Columns.get("atpara").ColumnName = "AtPara";
			dt.Columns.get("emps").ColumnName = "Emps";
			dt.Columns.get("domainExt").ColumnName = "DomainExt";
			dt.Columns.get("senddt").ColumnName = "SendDT";
			dt.Columns.get("weeknum").ColumnName = "WeekNum";
		}
		return dt;
	}
	/**
	 获取已经完成流程

	 @return
	*/
	public static DataTable DB_TongJi_FlowComplete()
	{
		DataTable dt = null;

		/* 如果不是删除流程注册表. */
		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		ps.SQL = "SELECT T.FK_Flow, T.FlowName, COUNT(T.WorkID) as Num FROM WF_GenerWorkFlow T WHERE (T.Emps LIKE '%@" + WebUser.getNo() + "@%' OR  T.Emps LIKE '%@" + WebUser.getNo() + ",%') AND T.FID=0 AND (T.WFState=3 OR T.WFState=11) GROUP BY T.FK_Flow,T.FlowName";
		dt = DBAccess.RunSQLReturnTable(ps);

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("NUM").ColumnName = "Num";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("num").ColumnName = "Num";
		}

		return dt;

	}
	/**
	 获取已经完成流程

	 @return
	*/
	public static DataTable DB_FlowComplete()
	{
		/* 如果不是删除流程注册表. */
		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		ps.SQL = "SELECT 'RUNNING' AS Type, T.* FROM WF_GenerWorkFlow T WHERE (T.Emps LIKE '%@" + WebUser.getNo() + "@%' OR T.Emps LIKE '%@" + WebUser.getNo() + ",%') AND T.FID=0 AND T.WFState=" + WFState.CompleteEnd.getValue() + " ORDER BY  RDT DESC";
		DataTable dt = DBAccess.RunSQLReturnTable(ps);

		//需要翻译.
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("TYPE").ColumnName = "Type";
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
			dt.Columns.get("SYSTYPE").ColumnName = "SysType";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("WFSTA").ColumnName = "WFSta";
			dt.Columns.get("WFSTATE").ColumnName = "WFState";
			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
			dt.Columns.get("SENDER").ColumnName = "Sender";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";
			dt.Columns.get("FK_DEPT").ColumnName = "FK_Dept";
			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";
			dt.Columns.get("SDTOFFLOW").ColumnName = "SDTOfFlow";
			dt.Columns.get("PFLOWNO").ColumnName = "PflowNo";
			dt.Columns.get("PWORKID").ColumnName = "PWorkID";
			dt.Columns.get("PNODEID").ColumnName = "PNodeID";
			dt.Columns.get("PEMP").ColumnName = "PEmp";
			dt.Columns.get("GUESTNO").ColumnName = "GuestNo";
			dt.Columns.get("GUESTNAME").ColumnName = "GuestName";
			dt.Columns.get("BILLNO").ColumnName = "BillNo";
			dt.Columns.get("FLOWNOTE").ColumnName = "FlowNote";
			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";
			dt.Columns.get("TODOEMPSNUM").ColumnName = "TodoEmpsNum";
			dt.Columns.get("TASKSTA").ColumnName = "TaskSta";
			dt.Columns.get("ATPARA").ColumnName = "AtPara";
			dt.Columns.get("EMPS").ColumnName = "Emps";
			dt.Columns.get("doMainExt").ColumnName = "DomainExt";
			dt.Columns.get("SENDDT").ColumnName = "SendDT";
			dt.Columns.get("WEEKNUM").ColumnName = "WeekNum";
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("type").ColumnName = "Type";
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";
			dt.Columns.get("systype").ColumnName = "SysType";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("wfsta").ColumnName = "WFSta";
			dt.Columns.get("wfstate").ColumnName = "WFState";
			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("startername").ColumnName = "StarterName";
			dt.Columns.get("sender").ColumnName = "Sender";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("fk_dept").ColumnName = "FK_Dept";
			dt.Columns.get("deptname").ColumnName = "DeptName";
			dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";
			dt.Columns.get("sdtofflow").ColumnName = "SDTOfFlow";
			dt.Columns.get("pflowno").ColumnName = "PflowNo";
			dt.Columns.get("pworkid").ColumnName = "PWorkID";
			dt.Columns.get("pnodeid").ColumnName = "PNodeID";
			dt.Columns.get("pemp").ColumnName = "PEmp";
			dt.Columns.get("guestno").ColumnName = "GuestNo";
			dt.Columns.get("guestname").ColumnName = "GuestName";
			dt.Columns.get("billno").ColumnName = "BillNo";
			dt.Columns.get("flownote").ColumnName = "FlowNote";
			dt.Columns.get("todoemps").ColumnName = "TodoEmps";
			dt.Columns.get("todoempsnum").ColumnName = "TodoEmpsNum";
			dt.Columns.get("tasksta").ColumnName = "TaskSta";
			dt.Columns.get("atpara").ColumnName = "AtPara";
			dt.Columns.get("emps").ColumnName = "Emps";
			dt.Columns.get("domainExt").ColumnName = "DomainExt";
			dt.Columns.get("senddt").ColumnName = "SendDT";
			dt.Columns.get("weeknum").ColumnName = "WeekNum";
		}
		return dt;
	}
	///// <summary>
	///// 获取某一个人已完成的工作
	///// </summary>
	///// <param name="userNo"></param>
	///// <returns></returns>
	//public static DataTable DB_FlowComplete(String userNo)
	//{

	//    /* 如果不是删除流程注册表. */
	//    Paras ps = new Paras();
	//    String dbstr =  bp.difference.SystemConfig.getAppCenterDBVarStr();
	//    ps.SQL = "SELECT 'RUNNING' AS Type, T.* FROM WF_GenerWorkFlow T WHERE (T.Emps LIKE '%@" + userNo + "@%' OR  T.Emps LIKE '%@" + userNo + ",%') AND T.FID=0 AND T.WFState=" + (int)WFState.CompleteEnd + " ORDER BY  RDT DESC";
	//    return DBAccess.RunSQLReturnTable(ps);

	//}
	/**
	 获取某一个人某个流程已完成的工作

	 @param userNo
	 @return
	*/
	public static DataTable DB_FlowComplete(String userNo, String flowNo)
	{
		/* 如果不是删除流程注册表. */
		Paras ps = new Paras();
		ps.SQL = "SELECT 'RUNNING' AS Type, T.* FROM WF_GenerWorkFlow T WHERE (T.Emps LIKE '%@" + userNo + "@%' OR  T.Emps LIKE '%@" + userNo + ",%') AND T.FK_Flow='" + flowNo + "' AND T.FID=0 AND (T.WFState=3 OR T.WFState=11) ORDER BY  RDT DESC";
		return DBAccess.RunSQLReturnTable(ps);
	}
	/**
	 获取已经完成

	 @return
	*/
	public static DataTable DB_FlowCompleteAndCC()
	{
		DataTable dt = DB_FlowComplete();
		DataTable ccDT = DB_CCList_CheckOver(WebUser.getNo());

		try
		{
			dt.Columns.Add("MyPK");
			dt.Columns.Add("Sta");
		}
		catch (RuntimeException e)
		{

		}

		for (DataRow row : ccDT.Rows)
		{
			DataRow newRow = dt.NewRow();

			for (DataColumn column : ccDT.Columns)
			{
				for (DataColumn dtColumn : dt.Columns)
				{
					if (Objects.equals(column.ColumnName, dtColumn.ColumnName))
					{
						newRow.setValue(column.ColumnName, row.get(dtColumn.ColumnName));
					}

				}

			}
			newRow.setValue("Type", "CC");
			dt.Rows.add(newRow);
		}
//		dt.DefaultView.setSort("RDT DESC");
//		return dt.DefaultView.ToTable();
		return dt;
	}

	/**
	 * 获得确认的待办
	 * @param isConfim 是否确认的工作？ true=已经确认, false= 未确认.
	 * @return 返回WF_GenerWorkFlow结构的数据.
	 * @throws Exception
	 */
	public static DataTable DB_Todolist_Confim(Boolean isConfim) throws Exception {

		String userNo = WebUser.getNo();
		String sql = "SELECT * FROM WF_EmpWorks WHERE FK_Emp = '" + userNo + "' ";

		if (isConfim == true)
			sql += " AND AtPara LIKE '%@C_" + userNo + "=1%'";
		else
			sql += " AND AtPara NOT LIKE '%@C_" + userNo + "=1%'";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		return dt;
	}
	/**
	 获取当前登录账号完成的流程

	 @param fk_flow 流程编号
	 @param title 标题
	 @return
	*/
	public static DataTable DB_FlowComplete2(String fk_flow, String title)
	{

		/* 如果不是删除流程注册表. */
		Paras ps = new Paras();
		if (DataType.IsNullOrEmpty(fk_flow))
		{
			if (DataType.IsNullOrEmpty(title))
			{
				ps.SQL = "SELECT 'RUNNING' AS Type,* FROM WF_GenerWorkFlow WHERE Emps LIKE '%@" + WebUser.getNo() + "@%' AND FID=0 AND (WFState=3 OR WFState=11) and FK_Flow!='010' order by RDT desc";
			}
			else
			{
				ps.SQL = "SELECT 'RUNNING' AS Type,* FROM WF_GenerWorkFlow WHERE Emps LIKE '%@" + WebUser.getNo() + "@%' and Title Like '%" + title + "%' AND FID=0 AND (WFState=3 OR WFState=11) and FK_Flow!='010' order by RDT desc";
			}
		}
		else
		{
			if (DataType.IsNullOrEmpty(title))
			{
				ps.SQL = "SELECT 'RUNNING' AS Type,* FROM WF_GenerWorkFlow WHERE Emps LIKE '%@" + WebUser.getNo() + "@%' AND FID=0 AND (WFState=3 OR WFState=11) and FK_Flow='" + fk_flow + "' order by RDT desc";
			}
			else
			{
				ps.SQL = "SELECT 'RUNNING' AS Type,* FROM WF_GenerWorkFlow WHERE Emps LIKE '%@" + WebUser.getNo() + "@%' and Title Like '%" + title + "%' AND FID=0 AND (WFState=3 OR WFState=11) and FK_Flow='" + fk_flow + "' order by RDT desc";
			}
		}
		return DBAccess.RunSQLReturnTable(ps);

	}
	/**
	 获取已完成及已回复的抄送列表信息

	 @param fk_flow
	 @param title
	 @return
	*/
	public static DataTable DB_FlowCompleteAndCC2(String fk_flow, String title)
	{
		DataTable dt = DB_FlowComplete2(fk_flow, title);
		DataTable ccDT = DB_CCList_CheckOver(WebUser.getNo());
		try
		{
			dt.Columns.Add("MyPK");
			dt.Columns.Add("Sta");
		}
		catch (RuntimeException e)
		{

		}

		for (DataRow row : ccDT.Rows)
		{
			DataRow newRow = dt.NewRow();

			for (DataColumn column : ccDT.Columns)
			{
				for (DataColumn dtColumn : dt.Columns)
				{
					if (Objects.equals(column.ColumnName, dtColumn.ColumnName))
					{
						newRow.setValue(column.ColumnName, row.get(dtColumn.ColumnName));
					}

				}

			}
			newRow.setValue("Type", "CC");
			dt.Rows.add(newRow);
		}
//		dt.DefaultView.setSort("RDT DESC");
//		return dt.DefaultView.ToTable();
		return dt;
	}
	/**
	 获得任务池的工作列表

	 @return 任务池的工作列表
	*/
	public static DataTable DB_TaskPool()
	{
		if (bp.wf.Glo.isEnableTaskPool() == false)
		{
			throw new RuntimeException("@你必须在Web.config中启用IsEnableTaskPool才可以执行此操作。");
		}

		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		String wfSql = "  (WFState=" + WFState.Askfor.getValue() + " OR WFState=" + WFState.Runing.getValue() + " OR WFState=" + WFState.Shift.getValue() + " OR WFState=" + WFState.ReturnSta.getValue() + ") AND TaskSta=" + TaskSta.Sharing.getValue();
		String sql;
		String realSql = null;

		/*不是授权状态*/
		ps.SQL = "SELECT * FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp=" + dbstr + "FK_Emp ";
		ps.Add("FK_Emp", WebUser.getNo(), false);

		//@杜. 这里需要翻译.
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("ISREAD").ColumnName = "IsRead";
			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
			dt.Columns.get("WFSTATE").ColumnName = "WFState";
			dt.Columns.get("FK_DEPT").ColumnName = "FK_Dept";
			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("PWORKID").ColumnName = "PWorkID";

			dt.Columns.get("PFLOWNO").ColumnName = "PFlowNo";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			// dt.Columns.get("WORKERDEPT").ColumnName = "WorkerDept";
			dt.Columns.get("FK_EMP").ColumnName = "FK_Emp";
			dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";

			dt.Columns.get("SYSTYPE").ColumnName = "SysType";
			dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";
			dt.Columns.get("GUESTNO").ColumnName = "GuestNo";
			dt.Columns.get("GUESTNAME").ColumnName = "GuestName";
			dt.Columns.get("BILLNO").ColumnName = "BillNo";

			dt.Columns.get("FLOWNOTE").ColumnName = "FlowNote";
			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";
			dt.Columns.get("TODOEMPSNUM").ColumnName = "TodoEmpsNum";
			dt.Columns.get("TODOSTA").ColumnName = "TodoSta";
			dt.Columns.get("TASKSTA").ColumnName = "TaskSta";

			dt.Columns.get("LISTTYPE").ColumnName = "ListType";
			dt.Columns.get("SENDER").ColumnName = "Sender";
			dt.Columns.get("ATPARA").ColumnName = "AtPara";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("isread").ColumnName = "IsRead";
			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("startername").ColumnName = "StarterName";
			dt.Columns.get("wfstate").ColumnName = "WFState";
			dt.Columns.get("fk_dept").ColumnName = "FK_Dept";
			dt.Columns.get("deptname").ColumnName = "DeptName";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("pworkid").ColumnName = "PWorkID";

			dt.Columns.get("pflowno").ColumnName = "PFlowNo";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			//  dt.Columns.get("workerdept").ColumnName = "WorkerDept";
			dt.Columns.get("fk_emp").ColumnName = "FK_Emp";
			dt.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";

			dt.Columns.get("systype").ColumnName = "SysType";
			dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";
			dt.Columns.get("guestno").ColumnName = "GuestNo";
			dt.Columns.get("guestname").ColumnName = "GuestName";
			dt.Columns.get("billno").ColumnName = "BillNo";

			dt.Columns.get("flownote").ColumnName = "FlowNote";
			dt.Columns.get("todoemps").ColumnName = "TodoEmps";
			dt.Columns.get("todoempsnum").ColumnName = "TodoEmpsNum";
			dt.Columns.get("todosta").ColumnName = "TodoSta";
			dt.Columns.get("tasksta").ColumnName = "TaskSta";

			dt.Columns.get("listtype").ColumnName = "ListType";
			dt.Columns.get("sender").ColumnName = "Sender";
			dt.Columns.get("atpara").ColumnName = "AtPara";
		}


		return dt;
	}
	/**
	 获得我从任务池里申请下来的工作列表

	 @return
	*/
	public static DataTable DB_TaskPoolOfMyApply()
	{
		if (bp.wf.Glo.isEnableTaskPool() == false)
		{
			throw new RuntimeException("@你必须在Web.config中启用IsEnableTaskPool才可以执行此操作。");
		}

		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		String wfSql = "  (WFState=" + WFState.Askfor.getValue() + " OR WFState=" + WFState.Runing.getValue() + " OR WFState=" + WFState.Shift.getValue() + " OR WFState=" + WFState.ReturnSta.getValue() + ") AND TaskSta=" + TaskSta.Takeback.getValue();
		String sql;
		String realSql;

		/*不是授权状态*/
		// ps.SQL = "SELECT * FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp=" + dbstr + "FK_Emp ORDER BY FK_Flow,ADT DESC ";
		//ps.SQL = "SELECT * FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp=" + dbstr + "FK_Emp ORDER BY ADT DESC ";
		ps.SQL = "SELECT * FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp=" + dbstr + "FK_Emp";

		// ps.SQL = "select v1.*,v2.name,v3.name as ParentName from (" + realSql + ") as v1 left join JXW_Inc v2 on v1.WorkID=v2.OID left join Jxw_Inc V3 on v1.PWorkID = v3.OID ORDER BY v1.ADT DESC";

		ps.Add("FK_Emp", WebUser.getNo(), false);

		//@杜. 这里需要翻译.
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("ISREAD").ColumnName = "IsRead";
			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
			dt.Columns.get("WFSTATE").ColumnName = "WFState";
			dt.Columns.get("FK_DEPT").ColumnName = "FK_Dept";
			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("PWORKID").ColumnName = "PWorkID";

			dt.Columns.get("PFLOWNO").ColumnName = "PFlowNo";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			//   dt.Columns.get("WORKERDEPT").ColumnName = "WorkerDept";
			dt.Columns.get("FK_EMP").ColumnName = "FK_Emp";
			dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";

			dt.Columns.get("SYSTYPE").ColumnName = "SysType";
			dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";
			dt.Columns.get("GUESTNO").ColumnName = "GuestNo";
			dt.Columns.get("GUESTNAME").ColumnName = "GuestName";
			dt.Columns.get("BILLNO").ColumnName = "BillNo";

			dt.Columns.get("FLOWNOTE").ColumnName = "FlowNote";
			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";
			dt.Columns.get("TODOEMPSNUM").ColumnName = "TodoEmpsNum";
			dt.Columns.get("TODOSTA").ColumnName = "TodoSta";
			dt.Columns.get("TASKSTA").ColumnName = "TaskSta";

			dt.Columns.get("LISTTYPE").ColumnName = "ListType";
			dt.Columns.get("SENDER").ColumnName = "Sender";
			dt.Columns.get("ATPARA").ColumnName = "AtPara";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("isread").ColumnName = "IsRead";
			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("startername").ColumnName = "StarterName";
			dt.Columns.get("wfstate").ColumnName = "WFState";
			dt.Columns.get("fk_dept").ColumnName = "FK_Dept";
			dt.Columns.get("deptname").ColumnName = "DeptName";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("pworkid").ColumnName = "PWorkID";

			dt.Columns.get("pflowno").ColumnName = "PFlowNo";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			//   dt.Columns.get("workerdept").ColumnName = "WorkerDept";
			dt.Columns.get("fk_emp").ColumnName = "FK_Emp";
			dt.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";

			dt.Columns.get("systype").ColumnName = "SysType";
			dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";
			dt.Columns.get("guestno").ColumnName = "GuestNo";
			dt.Columns.get("guestname").ColumnName = "GuestName";
			dt.Columns.get("billno").ColumnName = "BillNo";

			dt.Columns.get("flownote").ColumnName = "FlowNote";
			dt.Columns.get("todoemps").ColumnName = "TodoEmps";
			dt.Columns.get("todoempsnum").ColumnName = "TodoEmpsNum";
			dt.Columns.get("todosta").ColumnName = "TodoSta";
			dt.Columns.get("tasksta").ColumnName = "TaskSta";

			dt.Columns.get("listtype").ColumnName = "ListType";
			dt.Columns.get("sender").ColumnName = "Sender";
			dt.Columns.get("atpara").ColumnName = "AtPara";
		}

		return dt;
	}
	/**
	 将要执行的工作

	 @return
	*/
	public static DataTable DB_FutureTodolist()
	{
		String sql = "SELECT A.* FROM WF_GenerWorkFlow A, WF_SelectAccper B WHERE A.WorkID=B.WorkID AND B.FK_Node NOT IN(SELECT FK_Node FROM WF_GenerWorkerList C WHERE C.WorkID=A.WorkID) AND B.FK_Emp='" + WebUser.getNo() + "' AND WFState!=3 AND WFState >1";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			String columnName = "";
			for (DataColumn col : dt.Columns)
			{
				columnName = col.ColumnName.toUpperCase();
				switch (columnName)
				{
					case "ATPARA":
						col.ColumnName = "AtPara";
						break;
					case "WORKID":
						col.ColumnName = "WorkID";
						break;
					case "FK_FLOW":
						col.ColumnName = "FK_Flow";
						break;
					case "FK_FLOWSORT":
						col.ColumnName = "FK_FlowSort";
						break;
					case "FK_NODE":
						col.ColumnName = "FK_Node";
						break;
					case "FLOWNAME":
						col.ColumnName = "FlowName";
						break;
					case "NODENAME":
						col.ColumnName = "NodeName";
						break;
					case "BILLNO":
						col.ColumnName = "BillNo";
						break;
					case "PWORKID":
						col.ColumnName = "PWorkID";
						break;
					case "ORGNO":
						col.ColumnName = "OrgNo";
						break;
					case "WFSTATE":
						col.ColumnName = "WFSate";
						break;
					case "WFSTA":
						col.ColumnName = "WFSta";
						break;
					case "TITLE":
						col.ColumnName = "Title";
						break;
					case "STARTER":
						col.ColumnName = "Starter";
						break;
					case "STARTERNAME":
						col.ColumnName = "StarterName";
						break;
					case "EMPS":
						col.ColumnName = "Emps";
						break;
					case "TODOEMPS":
						col.ColumnName = "TodoEmps";
						break;
				}
			}
		}
		return dt;
	}

	/**
	 获得指定流程挂起工作列表

	 @return 返回从视图WF_EmpWorks查询出来的数据.
	*/

	public static String DB_GenerHungupList() throws Exception {
		return DB_GenerHungupList(null);
	}

	public static String DB_GenerHungupList(String flowNo) throws Exception {
		GenerWorkFlows gwfs = new GenerWorkFlows();
		QueryObject qo = new QueryObject(gwfs);
		int state = WFState.Hungup.getValue();
		qo.AddWhere(GenerWorkFlowAttr.WFState, state);
		qo.addAnd();
		qo.AddWhere(GenerWorkFlowAttr.AtPara, " LIKE ", "'%@Hunguper=" + WebUser.getNo() + "%'");
		if (flowNo != null)
		{
			qo.addAnd();
			qo.AddWhere(GenerWorkFlowAttr.FK_Flow, flowNo);
		}
		qo.addOrderByDesc(GenerWorkFlowAttr.HungupTime);
		qo.DoQuery();
		return gwfs.ToJson("dt");
	}
	/**
	 获得逻辑删除的流程

	 @return 返回从视图WF_EmpWorks查询出来的数据.
	*/
	public static DataTable DB_GenerDeleteWorkList() throws Exception {
		return DB_GenerDeleteWorkList(WebUser.getNo(), null);
	}
	/**
	 获得逻辑删除的流程:根据流程编号

	 @param userNo 操作员编号
	 @param fk_flow 流程编号(可以为空)
	 @return WF_GenerWorkFlow数据结构的集合
	*/
	public static DataTable DB_GenerDeleteWorkList(String userNo, String fk_flow) throws Exception {
		String sql;
		int state = WFState.Delete.getValue();
		if (DataType.IsNullOrEmpty(fk_flow))
		{
			sql = "SELECT A.WorkID FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B WHERE  A.WFState=" + state + " AND A.WorkID=B.WorkID AND B.FK_Emp='" + WebUser.getNo() + "' AND B.IsEnable=1   ";
		}
		else
		{
			sql = "SELECT A.WorkID FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B WHERE A.FK_Flow='" + fk_flow + "'  AND A.WFState=" + state + " AND A.WorkID=B.WorkID AND B.FK_Emp='" + WebUser.getNo() + "' AND B.IsEnable=1 ";
		}
		GenerWorkFlows gwfs = new GenerWorkFlows();
		gwfs.RetrieveInSQL(GenerWorkFlowAttr.WorkID, "(" + sql + ")");
		return gwfs.ToDataTableField("dt");
	}

		///#endregion 获取当前操作员的共享工作


		///#region 获取流程数据
	/**
	 根据流程状态获取指定流程数据

	 @param fk_flow 流程编号
	 @param sta 流程状态
	 @return 数据表OID,Title,RDT,FID
	*/
	public static DataTable DB_NDxxRpt(String fk_flow, WFState sta) throws Exception {
		Flow fl = new Flow(fk_flow);
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		String sql = "SELECT OID,Title,RDT,FID FROM " + fl.getPTable() + " WHERE WFState=" + sta.getValue() + " AND Rec=" + dbstr + "Rec";
		Paras ps = new Paras();
		ps.SQL = sql;
		ps.Add("Rec", WebUser.getNo(), false);
		return DBAccess.RunSQLReturnTable(ps);
	}

		///#endregion

	//#region 工作部件的数据源获取。
	/// <summary>
	/// 对会签节点增加退回到全部人员.
	/// </summary>
	/// <param name="dt"></param>
	/// <returns></returns>
	public static DataTable DB_GenerWillReturnNodes_DealDT(DataTable dt, String flowNo)
	{
		String sql = "SELECT TodolistModel,NodeID,Name FROM WF_Node WHERE FK_Flow='" + flowNo + "' AND TodolistModel=1";
		DataTable mydt = DBAccess.RunSQLReturnTable(sql);
		if (mydt.Rows.size() == 0)
			return dt;

		String nds = "";
		int idx = 0;
		for (int i = 0; i < dt.Rows.size(); i++)
		{
			DataRow dr = dt.Rows.get(i);

			idx++;
			String nodeID = dr.getValue(0).toString();
			String nodeName = dr.getValue(1).toString();
			if (nds.contains(nodeID + ",") == true)
				continue;
			nds += nodeID + ",";

			for (DataRow mydr1 : mydt.Rows)
			{
				String nodeID1 = mydr1.getValue(1).toString();
				if (nodeID.equals(nodeID1) == false)
					continue;

				DataRow mydr = dt.NewRow();
				mydr.setValue(0, nodeID);
				mydr.setValue(1,nodeName);
				mydr.setValue(2,"AllEmps");
				mydr.setValue(3, "全部人员");

				dt.Rows.AddRow(mydr, idx + 1);
				i++;
				break;
			}
		}
		return dt;
	}
		///#region 工作部件的数据源获取。
	/**
	 获取当前节点可以退回的节点

	 @param workid 工作ID
	 @return No节点编号,Name节点名称,Rec记录人,RecName记录人名称
	*/
	public static DataTable DB_GenerWillReturnNodes(long workid) throws Exception {
		DataTable dt = new DataTable("obt");
		dt.Columns.Add("No", String.class); // 节点ID
		dt.Columns.Add("Name", String.class); // 节点名称.
		dt.Columns.Add("Rec", String.class); // 被退回节点上的操作员编号.
		dt.Columns.Add("RecName", String.class); // 被退回节点上的操作员名称.
		dt.Columns.Add("IsBackTracking", String.class); // 该节点是否可以退回并原路返回？ 0否, 1是.
		dt.Columns.Add("AtPara", String.class); // 该节点是否可以退回并原路返回？ 0否, 1是.


		GenerWorkFlow gwf = new GenerWorkFlow(workid);
		Node nd = new Node(gwf.getNodeID());

		//判断流程实例的节点和当前节点是否相同
		if (gwf.getTodoEmps().contains(WebUser.getNo() + ",") == false)
		{
			if (Flow_IsCanDoCurrentWork(gwf.getWorkID(), WebUser.getNo()) == false)
			{
				throw new RuntimeException("@当前工作处于{" + gwf.getNodeName() + "}节点,您({" + WebUser.getName() + "})没有处理权限.");
			}
		}
		//增加退回到父流程节点的设计.
		if (nd.getItIsStartNode() == true)
		{
			/*如果是开始的节点有可能退回到子流程上去.*/
			if (gwf.getPWorkID() == 0)
			{
				throw new RuntimeException("@当前节点是开始节点并且不是子流程，您不能执行退回。");
			}

			GenerWorkerLists gwls = new GenerWorkerLists();
			int i = gwls.Retrieve(GenerWorkerListAttr.WorkID, gwf.getPWorkID(), GenerWorkerListAttr.RDT);

			String nodes = "";
			for (GenerWorkerList gwl : gwls.ToJavaList())
			{
				DataRow dr = dt.NewRow();
				dr.setValue("No", String.valueOf(gwl.getNodeID()));

				if (nodes.contains(String.valueOf(gwl.getNodeID()) + ",") == true)
				{
					continue;
				}

				nodes += String.valueOf(gwl.getNodeID()) + ",";

				dr.setValue("Name", gwl.getNodeName());
				dr.setValue("Rec", gwl.getEmpNo());
				dr.setValue("RecName", gwl.getEmpName());
				dr.setValue("IsBackTracking", "0");

				dt.Rows.add(dr);
			}
			return DB_GenerWillReturnNodes_DealDT(dt, gwf.getFlowNo());
		}

		if (nd.getItIsSubThread() == true)
		{
			/*如果是子线程，它只能退回它的上一个节点，现在写死了，其它的设置不起作用了。*/
			Nodes nds = nd.getFromNodes();
			for (Node ndFrom : nds.ToJavaList())
			{
				Work wk;
				switch (ndFrom.getHisRunModel())
				{
					case FL:
					case FHL:
						wk = ndFrom.getHisWork();
						wk.setOID(gwf.getFID());
						if (wk.RetrieveFromDBSources() == 0)
						{
							continue;
						}
						break;
					case SubThreadSameWorkID:
					case SubThreadUnSameWorkID:
						wk = ndFrom.getHisWork();
						wk.setOID(workid);
						if (wk.RetrieveFromDBSources() == 0)
						{
							continue;
						}
						break;
					case Ordinary:
					default:
						throw new RuntimeException("流程设计异常，子线程的上一个节点不能是普通节点。");
				}

				if (ndFrom.getNodeID() == gwf.getNodeID())
				{
					continue;
				}


				String mysql = "SELECT  a.FK_Emp as Rec, a.EmpName as RecName FROM WF_GenerWorkerlist a WHERE a.FK_Node=" + ndFrom.getNodeID() + " AND  (a.WorkID=" + workid + " AND a.FID=" + gwf.getFID() + " )  ORDER BY RDT DESC ";
				DataTable mydt = DBAccess.RunSQLReturnTable(mysql);
				if (mydt.Rows.size() == 0)
				{
					continue;
				}

				DataRow dr = dt.NewRow();
				dr.setValue("No", String.valueOf(ndFrom.getNodeID()));
				dr.setValue("Name", ndFrom.getName());
				dr.setValue("Rec", mydt.Rows.get(0).getValue(0));
				dr.setValue("RecName", mydt.Rows.get(0).getValue(1));

				if (ndFrom.getItIsBackTracking() == true)
				{
					dr.setValue("IsBackTracking", "1");
				}
				else
				{
					dr.setValue("IsBackTracking", "0");
				}

				dt.Rows.add(dr);
			} //结束循环.

			if (dt.Rows.size() == 0)
			{
				throw new RuntimeException("err@没有获取到应该退回的节点列表.");
			}
			return DB_GenerWillReturnNodes_DealDT(dt, gwf.getFlowNo());
		}

		String sql = "";

		WorkNode wn = new WorkNode(workid, gwf.getNodeID());
		WorkNodes wns = new WorkNodes();
		switch (nd.getHisReturnRole())
		{
			case CanNotReturn:
				return DB_GenerWillReturnNodes_DealDT(dt, gwf.getFlowNo());
			case ReturnAnyNodes:
				if (nd.getItIsHL() || nd.getItIsFLHL())
				{
					/*如果当前点是分流，或者是分合流，就不按退回规则计算了。*/
					sql = "SELECT A.FK_Node AS No,a.NodeName as Name, a.FK_Emp as Rec, a.EmpName as RecName, b.IsBackTracking FROM WF_GenerWorkerlist a, WF_Node b WHERE a.FK_Node=b.NodeID AND a.FID=" + gwf.getFID() + " AND a.WorkID=" + workid + " AND a.FK_Node!=" + gwf.getNodeID() + " AND a.IsPass=1 ORDER BY RDT DESC ";
					dt = DBAccess.RunSQLReturnTable(sql);
					if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
					{
						dt.Columns.get("NO").ColumnName = "No";
						dt.Columns.get("NAME").ColumnName = "Name";
						dt.Columns.get("REC").ColumnName = "Rec";
						dt.Columns.get("RECNAME").ColumnName = "RecName";
						dt.Columns.get("ISBACKTRACKING").ColumnName = "IsBackTracking";
					}
					if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
					{
						dt.Columns.get("no").ColumnName = "No";
						dt.Columns.get("name").ColumnName = "Name";
						dt.Columns.get("rec").ColumnName = "Rec";
						dt.Columns.get("recname").ColumnName = "RecName";
						dt.Columns.get("isbacktracking").ColumnName = "IsBackTracking";
					}
					return dt;
				}

				if (nd.getTodolistModel() == TodolistModel.Order)
				{
					sql = "SELECT A.FK_Node as No,a.NodeName as Name, a.FK_Emp as Rec, a.EmpName as RecName, b.IsBackTracking, a.AtPara FROM WF_GenerWorkerlist a, WF_Node b WHERE a.FK_Node=b.NodeID AND (a.WorkID=" + workid + " AND a.IsEnable=1 AND a.IsPass=1 AND a.FK_Node!=" + gwf.getNodeID() + ") OR (a.FK_Node=" + gwf.getNodeID() + " AND a.IsPass <0)  ORDER BY a.RDT DESC";
				}
				else
				{
					sql = "SELECT a.FK_Node as No,a.NodeName as Name, a.FK_Emp as Rec, a.EmpName as RecName, b.IsBackTracking, a.AtPara FROM WF_GenerWorkerlist a,WF_Node b WHERE a.FK_Node=b.NodeID AND a.WorkID=" + workid + " AND a.IsEnable=1 AND a.IsPass=1 AND a.FK_Node!=" + gwf.getNodeID() + " AND a.AtPara NOT LIKE '%@IsHuiQian=1%' ORDER BY a.RDT DESC";
				}

				// sql = "SELECT A.NDFrom AS No, A.NDFromT AS Name, A.EmpFrom AS Rec, A.EmpFromT AS RecName, B.IsBackTracking, A.Msg FROM ND" + int.Parse(nd.getFlowNo()) + "Track A, WF_Node B WHERE A.NDFrom=B.NodeID AND A.WorkID = " + workid + " AND A.ActionType in(" + (int)ActionType.Start + "," + (int)ActionType.Forward + "," + (int)ActionType.ForwardFL + "," + (int)ActionType.ForwardHL + ") AND A.NDFrom != " + fk_node + " ORDER BY A.RDT DESC";
				// ss
				// Log.DebugWriteWarning(sql);

				dt = DBAccess.RunSQLReturnTable(sql);

				if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
				{
					dt.Columns.get("NO").ColumnName = "No";
					dt.Columns.get("NAME").ColumnName = "Name";
					dt.Columns.get("REC").ColumnName = "Rec";
					dt.Columns.get("RECNAME").ColumnName = "RecName";
					dt.Columns.get("ISBACKTRACKING").ColumnName = "IsBackTracking";
					dt.Columns.get("ATPARA").ColumnName = "AtPara"; //参数.
				}
				if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
				{
					dt.Columns.get("no").ColumnName = "No";
					dt.Columns.get("name").ColumnName = "Name";
					dt.Columns.get("rec").ColumnName = "Rec";
					dt.Columns.get("recname").ColumnName = "RecName";
					dt.Columns.get("isbacktracking").ColumnName = "IsBackTracking";

					dt.Columns.get("atpara").ColumnName = "AtPara"; //参数.
				}


					///#region 增加上，可以退回到延续流程的节点.  @lizhen
				if (gwf.getPWorkID() != 0)
				{
					/* 满足省联社的需求.
					 * 1. 判断是否有延续子流程，延续到当前节点上来？，如果有则把父流程的节点也显示出来。
					 * 2.
					 */
					sql = "SELECT FK_Flow,FK_Node FROM WF_NodeSubFlow WHERE YanXuToNode LIKE '%" + gwf.getNodeID() + "%'";
					DataTable mydt = DBAccess.RunSQLReturnTable(sql);

					for (DataRow drSubFlow : mydt.Rows)
					{
						String flowNo = drSubFlow.get(0).toString();
						String nodeID = drSubFlow.get(1).toString();

						GenerWorkerLists gwls = new GenerWorkerLists();
						int i = gwls.Retrieve(GenerWorkerListAttr.WorkID, gwf.getPWorkID(), GenerWorkerListAttr.FK_Node);
						String nodes = "";
						for (GenerWorkerList gwl : gwls.ToJavaList())
						{
							DataRow dr = dt.NewRow();
							dr.setValue("No", String.valueOf(gwl.getNodeID()));
							if (nodes.contains(String.valueOf(gwl.getNodeID()) + ",") == true)
							{
								continue;
							}

							nodes += String.valueOf(gwl.getNodeID()) + ",";

							bp.wf.Flow fl = new Flow(gwl.getFlowNo());

							dr.setValue("Name", fl.getName() + ":" + gwl.getNodeName());
							dr.setValue("Rec", gwl.getEmpNo());
							dr.setValue("RecName", gwl.getEmpName());
							dr.setValue("IsBackTracking", "0");
							dt.Rows.add(dr);
						}
					}
				}

					///#endregion 增加上，可以退回到延续流程的节点.

				//GenerWorkFlow gwf = new GenerWorkFlow(workid);
				//if (gwf.PNodeID==  )

				return DB_GenerWillReturnNodes_DealDT(dt, gwf.getFlowNo());
			case ReturnPreviousNode:

				if (nd.getItIsHL() || nd.getItIsFLHL())
				{
					/*如果当前点是分流，或者是分合流，就不按退回规则计算了。*/
					//  if (nd.getItIsSubThread()==true)
					sql = "SELECT A.FK_Node AS No,a.NodeName as Name, a.FK_Emp as Rec, a.EmpName as RecName, b.IsBackTracking, a.AtPara FROM WF_GenerWorkerlist a," + " WF_Node b WHERE a.FK_Node=b.NodeID AND a.FID=0 AND a.WorkID=" + workid + " AND a.IsPass=1 AND A.FK_Node!=" + gwf.getNodeID() + " ORDER BY A.RDT ASC ";

					dt = DBAccess.RunSQLReturnTable(sql);
					if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
					{
						dt.Columns.get("NO").ColumnName = "No";
						dt.Columns.get("NAME").ColumnName = "Name";
						dt.Columns.get("REC").ColumnName = "Rec";
						dt.Columns.get("RECNAME").ColumnName = "RecName";
						dt.Columns.get("ISBACKTRACKING").ColumnName = "IsBackTracking";
						dt.Columns.get("ATPARA").ColumnName = "AtPara"; //参数.
					}

					while (1 == 1)
					{
						if (dt.Rows.size() == 1)
						{
							break;
						}
						dt.Rows.remove(dt.Rows.size() - 1);
					}
					return DB_GenerWillReturnNodes_DealDT(dt, gwf.getFlowNo());
				}

				WorkNode mywnP = wn.GetPreviousWorkNode();


				if (nd.getTodolistModel() == TodolistModel.Order)
				{
					sql = "SELECT A.FK_Node as No,a.NodeName as Name, a.FK_Emp as Rec, a.EmpName as RecName, b.IsBackTracking,a.AtPara FROM WF_GenerWorkerlist a, WF_Node b WHERE a.FK_Node=b.NodeID AND (a.WorkID=" + workid + " AND a.IsEnable=1 AND a.IsPass=1 AND a.FK_Node=" + mywnP.getHisNode().getNodeID()+ ") OR (a.FK_Node=" + mywnP.getHisNode().getNodeID()+ " AND a.IsPass <0)  ORDER BY a.RDT DESC";
					dt = DBAccess.RunSQLReturnTable(sql);
				}
				else
				{
					sql = "SELECT A.FK_Node as \"No\",a.NodeName as \"Name\", a.FK_Emp as \"Rec\", a.EmpName as \"RecName\", b.IsBackTracking as \"IsBackTracking\", a.AtPara as \"AtPara\"  FROM WF_GenerWorkerlist a,WF_Node b WHERE a.FK_Node=b.NodeID AND a.WorkID=" + workid + " AND a.IsEnable=1 AND a.IsPass=1 AND a.FK_Node=" + mywnP.getHisNode().getNodeID()+ "  AND ( A.AtPara NOT LIKE '%@IsHuiQian=1%' OR a.AtPara IS NULL) ORDER BY a.RDT DESC ";
					DataTable mydt = DBAccess.RunSQLReturnTable(sql);

					if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
					{
						mydt.Columns.get("NO").ColumnName = "No";
						mydt.Columns.get("NAME").ColumnName = "Name";
						mydt.Columns.get("REC").ColumnName = "Rec";
						mydt.Columns.get("RECNAME").ColumnName = "RecName";
						mydt.Columns.get("ISBACKTRACKING").ColumnName = "IsBackTracking";
						mydt.Columns.get("ATPARA").ColumnName = "AtPara";
					}

					if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
					{
						mydt.Columns.get("no").ColumnName = "No";
						mydt.Columns.get("name").ColumnName = "Name";
						mydt.Columns.get("rec").ColumnName = "Rec";
						mydt.Columns.get("recname").ColumnName = "RecName";
						mydt.Columns.get("isbacktracking").ColumnName = "IsBackTracking";
						mydt.Columns.get("atpara").ColumnName = "AtPara";
					}

					if (mydt.Rows.size() != 0)
					{
						return DB_GenerWillReturnNodes_DealDT(mydt, gwf.getFlowNo());
					}

					//有可能是跳转过来的节点.//edited by liuxc,2017-05-26,改RDT排序为CDT排序，更准确，以避免有时找错上一步节点的情况发生
					if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
					{
						sql = "SELECT top 1 A.FK_Node as No,a.NodeName as Name, a.FK_Emp as Rec, a.EmpName as RecName, b.IsBackTracking,a.AtPara FROM WF_GenerWorkerlist a,WF_Node b WHERE a.FK_Node=b.NodeID AND a.WorkID=" + workid + " AND a.IsEnable=1 AND a.IsPass=1 ORDER BY a.CDT DESC ";
					}
					else if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
					{
						sql = "SELECT a.FK_Node as No,a.NodeName as Name, a.FK_Emp as Rec, a.EmpName as RecName, b.IsBackTracking,a.AtPara FROM WF_GenerWorkerlist a,WF_Node b WHERE a.FK_Node=b.NodeID AND a.WorkID=" + workid + " AND a.IsEnable=1 AND a.IsPass=1 AND rownum =1  ORDER BY a.CDT DESC ";
					}
					else if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL|| SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
					{
						sql = "SELECT a.FK_Node as No,a.NodeName as Name, a.FK_Emp as Rec, a.EmpName as RecName, b.IsBackTracking,a.AtPara FROM WF_GenerWorkerlist a,WF_Node b WHERE a.FK_Node=b.NodeID AND a.WorkID=" + workid + " AND a.IsEnable=1 AND a.IsPass=1 ORDER BY a.CDT DESC LIMIT 1";
					}
					else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX)
					{
						sql = "SELECT a.FK_Node as No,a.NodeName as Name, a.FK_Emp as Rec, a.EmpName as RecName, b.IsBackTracking,a.AtPara FROM WF_GenerWorkerlist a,WF_Node b WHERE a.FK_Node=b.NodeID AND a.WorkID=" + workid + " AND a.IsEnable=1 AND a.IsPass=1 ORDER BY a.CDT DESC LIMIT 1";
					}
					else
					{
						throw new RuntimeException("获取上一步节点，未涉及的数据库类型");
					}

					dt = DBAccess.RunSQLReturnTable(sql);

					if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
					{
						dt.Columns.get("NO").ColumnName = "No";
						dt.Columns.get("NAME").ColumnName = "Name";
						dt.Columns.get("REC").ColumnName = "Rec";
						dt.Columns.get("RECNAME").ColumnName = "RecName";
						dt.Columns.get("ISBACKTRACKING").ColumnName = "IsBackTracking";
						dt.Columns.get("ATPARA").ColumnName = "AtPara";
					}
					if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
					{
						dt.Columns.get("no").ColumnName = "No";
						dt.Columns.get("name").ColumnName = "Name";
						dt.Columns.get("rec").ColumnName = "Rec";
						dt.Columns.get("recname").ColumnName = "RecName";
						dt.Columns.get("isbacktracking").ColumnName = "IsBackTracking";
						dt.Columns.get("atpara").ColumnName = "AtPara";
					}
					return DB_GenerWillReturnNodes_DealDT(mydt, gwf.getFlowNo());
				}
				break;
			case ReturnSpecifiedNodes: //退回指定的节点。
				if (wns.size() == 0)
				{
					wns.GenerByWorkID(wn.getHisNode().getHisFlow(), workid);
				}

				NodeReturns rnds = new NodeReturns();
				rnds.Retrieve(NodeReturnAttr.FK_Node, gwf.getNodeID(), null);
				if (rnds.size() == 0)
				{
					String returnNodes = wn.getHisNode().GetParaString("ReturnNodes");
					if (DataType.IsNullOrEmpty(returnNodes) == true)
						throw new RuntimeException("@流程设计错误，您设置该节点可以退回指定的节点，但是指定的节点集合为空，请在节点属性设置它的制订节点。");
					String[] strs = returnNodes.split(",");
					NodeReturn rnd = null;
					for (int i = 0; i < strs.length; i++)
					{
						if (DataType.IsNullOrEmpty(strs[i]) == true)
							continue;
						rnd = new NodeReturn();
						rnd.setNodeID(gwf.getNodeID());
						rnd.setReturnTo(Integer.parseInt(strs[i]));
						rnds.AddEntity(rnd);
					}
				}

				for (NodeReturn item : rnds.ToJavaList())
				{
					GenerWorkerLists gwls = new GenerWorkerLists();
					int i = gwls.Retrieve(GenerWorkerListAttr.FK_Node, item.getReturnTo(), GenerWorkerListAttr.WorkID, workid, null);
					if (i == 0)
					{
						continue;
					}

					for (GenerWorkerList gwl : gwls.ToJavaList())
					{
						DataRow dr = dt.NewRow();
						dr.setValue("No", String.valueOf(gwl.getNodeID()));
						dr.setValue("Name", gwl.getNodeName());
						dr.setValue("Rec", gwl.getEmpNo());
						dr.setValue("RecName", gwl.getEmpName());
						Node mynd = new Node(item.getNodeID());
						if (mynd.getItIsBackTracking() == true) //是否可以原路返回.
						{
							dr.setValue("IsBackTracking", "1");
						}
						else
						{
							dr.setValue("IsBackTracking", "0");
						}

						dt.Rows.add(dr);
					}
				}

				// if (dt.Rows.size() == 0)
				//   throw new Exception("err@" + rnds.ToJson());

				break;
			case ReturnStartNode:
				int nodeID = Integer.parseInt(Integer.parseInt(nd.getFlowNo())+"01");
				GenerWorkerList gwl = new GenerWorkerList();
				int i = gwl.Retrieve(GenerWorkerListAttr.FK_Node, nodeID, GenerWorkerListAttr.WorkID, workid);
				if (i == 0)
					throw new RuntimeException("err@不可能出现的错误，退回到开始节点找不到申请人的处理信息");

				DataRow drr = dt.NewRow();
				drr.setValue("No", String.valueOf(gwl.getNodeID()));
				drr.setValue("Name", gwl.getNodeName());
				drr.setValue("Rec", gwl.getEmpNo());
				drr.setValue("RecName", gwl.getEmpName());
				Node mynd = new Node(nodeID);
				if (mynd.getItIsBackTracking() == true) //是否可以原路返回.
				{
					drr.setValue("IsBackTracking", "1");
				}
				else
				{
					drr.setValue("IsBackTracking", "0");
				}

				dt.Rows.add(drr);
				break;
			case ByReturnLine: //按照流程图画的退回线执行退回.
				Directions dirs = new Directions();
				dirs.Retrieve(DirectionAttr.Node, gwf.getNodeID(), null);
				if (dirs.size() == 0)
				{
					throw new RuntimeException("@流程设计错误:当前节点没有画向后退回的退回线,更多的信息请参考退回规则.");
				}

				for (Direction dir : dirs.ToJavaList())
				{
					Node toNode = new Node(dir.getToNode());
					sql = "SELECT a.FK_Emp,a.EmpName FROM WF_GenerWorkerlist a, WF_Node b WHERE   a.FK_Node=" + toNode.getNodeID() + " AND a.WorkID=" + workid + " AND a.IsEnable=1 AND a.IsPass=1";
					DataTable dt1 = DBAccess.RunSQLReturnTable(sql);
					if (dt1.Rows.size() == 0)
					{
						continue;
					}

					DataRow dr = dt.NewRow();
					dr.setValue("No", toNode.getNodeID());
					dr.setValue("Name", toNode.getName());
					dr.setValue("Rec", dt1.Rows.get(0).getValue(0));
					dr.setValue("RecName", dt1.Rows.get(0).getValue(0));
					if (toNode.getItIsBackTracking()== true)
					{
						dr.setValue("IsBackTracking", "1");
					}
					else
					{
						dr.setValue("IsBackTracking", "0");
					}



					dt.Rows.add(dr);
				}
				break;
			default:
				throw new RuntimeException("@没有判断的退回类型。");
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("NO").ColumnName = "No";
			dt.Columns.get("NAME").ColumnName = "Name";
			dt.Columns.get("REC").ColumnName = "Rec";
			dt.Columns.get("RECNAME").ColumnName = "RecName";
			dt.Columns.get("ISBACKTRACKING").ColumnName = "IsBackTracking";
			dt.Columns.get("ATPARA").ColumnName = "AtPara";
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("no").ColumnName = "No";
			dt.Columns.get("name").ColumnName = "Name";
			dt.Columns.get("rec").ColumnName = "Rec";
			dt.Columns.get("recname").ColumnName = "RecName";
			dt.Columns.get("isbacktracking").ColumnName = "IsBackTracking";
			dt.Columns.get("atpara").ColumnName = "AtPara";
		}

		if (dt.Rows.size() == 0)
		{
			throw new RuntimeException("@没有计算出来要退回的节点，请管理员确认节点退回规则是否合理？当前节点名称:" + nd.getName() + ",退回规则:" + nd.getHisReturnRole().toString());
		}
		return DB_GenerWillReturnNodes_DealDT(dt, gwf.getFlowNo());
	}
		///#endregion 工作部件的数据源获取

		///#region 获取当前操作员的在途工作
	/**
	 获取未完成的流程(也称为在途流程:我参与的但是此流程未完成)
	 该接口为在途菜单提供数据,在在途工作中，可以执行撤销发送。

	 @param userNo 操作员
	 @param fk_flow 流程编号
	 @param isMyStarter 是否仅仅查询我发起的在途流程
	 @return 返回从数据视图WF_GenerWorkflow查询出来的数据.
	*/

	public static DataTable DB_GenerRuning(String userNo, String fk_flow, boolean isMyStarter, String domainExt)
	{
		return DB_GenerRuning(userNo, fk_flow, isMyStarter, domainExt, false);
	}

	public static DataTable DB_GenerRuning(String userNo, String fk_flow, boolean isMyStarter)
	{
		return DB_GenerRuning(userNo, fk_flow, isMyStarter, null, false);
	}

	public static DataTable DB_GenerRuning(String userNo, String fk_flow)
	{
		return DB_GenerRuning(userNo, fk_flow, false, null, false);
	}

	public static DataTable DB_GenerRuning(String userNo, String fk_flow, boolean isMyStarter, String domainExt, boolean isContainFuture)
	{
		String dbStr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();

		String domainExtSQL = "";
		if (domainExt == null)
		{
			if (SystemConfig.getAppCenterDBType() == DBType.DM)
				domainExtSQL = " AND A.\"DomainExt\"='" + domainExt + "' ";
			else
				domainExtSQL = " AND A.DomainExt='" + domainExt + "' ";
		}

		//获取用户当前所在的节点
		String currNode = "";
		switch (DBAccess.getAppCenterDBType())
		{
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				currNode = "(SELECT FK_Node FROM (SELECT G.FK_Node FROM WF_GenerWorkFlow A,WF_GenerWorkerlist G WHERE G.WorkID = A.WorkID AND G.FK_Emp='" + WebUser.getNo() + "' " + domainExtSQL + " Order by G.RDT DESC ) WHERE RowNum=1)";
				break;
			case MySQL:
			case PostgreSQL:
			case UX:
			case HGDB:
			case GBASE8CByMySQL:
			case GBASE8A:
				currNode = "(SELECT  FK_Node FROM WF_GenerWorkerlist G WHERE   G.WorkID = A.WorkID AND FK_Emp='" + WebUser.getNo() + "' Order by RDT DESC LIMIT 1)";
				break;
			case MSSQL:
				currNode = "(SELECT TOP 1 FK_Node FROM WF_GenerWorkerlist G WHERE  G.WorkID = A.WorkID AND FK_Emp='" + WebUser.getNo() + "' Order by RDT DESC)";
				break;
			default:
				break;
		}


		//授权模式.
		String sql = "";
		String futureSQL = "";
		if (isContainFuture == true)
		{
			switch (DBAccess.getAppCenterDBType())
			{
				case MySQL:
					futureSQL = " UNION SELECT A.WorkID,a.Starter,A.StarterName,A.Title,A.DeptName,D.Name AS NodeName,A.RDT,B.FK_Node,A.FK_Flow,A.FID,A.FlowName,C.EmpName AS TodoEmps," + currNode + " AS CurrNode ,1 AS RunType,A.WFState, 0 AS WhoExeIt FROM WF_GenerWorkFlow A, WF_SelectAccper B,"
							+ "(SELECT GROUP_CONCAT(B.EmpName SEPARATOR ';') AS EmpName, B.WorkID,B.FK_Node FROM WF_GenerWorkFlow A, WF_SelectAccper B WHERE A.WorkID = B.WorkID  group By B.FK_Node,B.WorkID) C,WF_Node D"
							+ " WHERE A.WorkID = B.WorkID AND B.WorkID = C.WorkID AND B.FK_Node = C.FK_Node AND A.FK_Node = D.NodeID AND B.FK_Emp = '" + WebUser.getNo() + "' " + domainExtSQL
							+ " AND B.FK_Node Not in(Select DISTINCT FK_Node From WF_GenerWorkerlist G where G.WorkID = B.WorkID) AND A.WFState NOT IN(0,1,3)";
					break;
				case MSSQL:
					futureSQL = " UNION SELECT A.WorkID,a.Starter,A.StarterName,A.Title,A.DeptName,D.Name AS NodeName,A.RDT,B.FK_Node,A.FK_Flow,A.FID,A.FlowName,C.EmpName AS TodoEmps ," + currNode + " AS CurrNode ,1 AS RunType,A.WFState, 0 AS WhoExeIt FROM WF_GenerWorkFlow A, WF_SelectAccper B,"
							+ "(SELECT EmpName=STUFF((Select ';'+FK_Emp+','+EmpName From WF_SelectAccper t Where t.FK_Node=B.FK_Node FOR xml path('')) , 1 , 1 , '') , B.WorkID,B.FK_Node FROM WF_GenerWorkFlow A, WF_SelectAccper B WHERE A.WorkID = B.WorkID  group By B.FK_Node,B.WorkID) C,WF_Node D"
							+ " WHERE A.WorkID = B.WorkID AND B.WorkID = C.WorkID AND B.FK_Node = C.FK_Node AND A.FK_Node = D.NodeID AND B.FK_Emp = '" + WebUser.getNo() + "' " + domainExtSQL
							+ " AND B.FK_Node Not in(Select DISTINCT FK_Node From WF_GenerWorkerlist G where G.WorkID = B.WorkID) AND A.WFState NOT IN(0,1,3)";
					break;
				default:
					break;
			}
		}

		if (DataType.IsNullOrEmpty(fk_flow))
		{
			if (isMyStarter == true)
			{
				sql = "SELECT DISTINCT a.WorkID,a.Starter,a.StarterName,a.Title,a.DeptName,a.NodeName,a.RDT,a.FK_Node,a.FK_Flow,a.FID ,a.FlowName,a.TodoEmps," + currNode + " AS CurrNode,0 AS RunType,a.WFState, 0 AS WhoExeIt,a.PWorkID,a.PFlowNo FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B WHERE A.TodoEmps  not like '%" + WebUser.getNo() + ",%' AND  A.WorkID=B.WorkID AND B.FK_Emp=" + dbStr + "FK_Emp AND B.IsEnable=1 AND B.IsPass != -2 AND   (B.IsPass=1 or B.IsPass < -1) AND  A.Starter=" + dbStr + "Starter AND A.WFState NOT IN ( 0, 1, 3 )";
				sql += Check_CCBPMRunModel();
				if (isContainFuture == true)
				{
					sql += futureSQL;
				}
				sql += " UNION SELECT DISTINCT a.WorkID,a.Starter,a.StarterName,a.Title,a.DeptName,a.NodeName,a.RDT,a.FK_Node,a.FK_Flow,a.FID ,a.FlowName,a.TodoEmps,A.FK_Node AS CurrNode,0 AS RunType,a.WFState, 1 AS WhoExeIt,a.PWorkID,a.PFlowNo FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B,WF_Node C WHERE   A.WorkID=B.WorkID AND A.FK_Node=C.NodeID AND B.FK_Node=C.NodeID AND C.WhoExeIt=0 AND B.FK_Emp=" + dbStr + "FK_Emp AND B.IsEnable=1 AND B.IsPass = 0 AND B.WhoExeIt=1 AND  A.Starter=" + dbStr + "Starter AND A.WFState NOT IN ( 0, 1, 3 )";
				sql += Check_CCBPMRunModel();
				ps.SQL = sql;
				ps.Add("FK_Emp", userNo, false);
				ps.Add("Starter", userNo, false);
			}
			else
			{
				sql = "SELECT DISTINCT a.WorkID,a.Starter,a.StarterName,a.Title,a.DeptName,a.NodeName,a.RDT,a.FK_Node,a.FK_Flow,a.FID ,a.FlowName,a.TodoEmps ," + currNode + " AS CurrNode,0 AS RunType,a.WFState, 0 AS WhoExeIt,a.PWorkID,a.PFlowNo FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B WHERE A.TodoEmps  not like '%" + WebUser.getNo() + ",%' AND  A.WorkID=B.WorkID AND B.FK_Emp=" + dbStr + "FK_Emp AND B.IsEnable=1 AND B.IsPass != -2 AND  (B.IsPass=1 or B.IsPass < -1) AND A.WFState NOT IN ( 0, 1, 3 )";
				sql += Check_CCBPMRunModel();
				if (isContainFuture == true)
				{
					sql += futureSQL;
				}
				//没有搞明白什么意思，现在删除掉 by zhoupeng. @yln
				//sql += " UNION SELECT DISTINCT a.WorkID,a.StarterName,a.Title,a.DeptName,a.NodeName,a.RDT,a.FK_Node,a.FK_Flow,a.FID ,a.FlowName,a.TodoEmps,A.FK_Node AS CurrNode,0 AS RunType,a.WFState, 1 AS WhoExeIt FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B,WF_Node C WHERE   A.WorkID=B.WorkID AND A.FK_Node=C.NodeID AND B.FK_Node=C.NodeID AND C.WhoExeIt=0 AND B.FK_Emp=" + dbStr + "FK_Emp AND B.IsEnable=1 AND B.IsPass = 0 AND B.WhoExeIt=1  AND A.WFState NOT IN ( 0, 1, 3 )";
				//sql += Check_CCBPMRunModel();
				ps.SQL = sql;
				ps.Add("FK_Emp", userNo, false);
			}
		}
		else
		{
			if (isMyStarter == true)
			{
				sql = "SELECT DISTINCT a.WorkID,a.Starter,a.StarterName,a.Title,a.DeptName,a.NodeName,a.RDT,a.FK_Node,a.FK_Flow,a.FID ,a.FlowName,a.TodoEmps ," + currNode + " AS CurrNode,0 AS RunType,a.WFState, 0 AS WhoExeIt,a.PWorkID,a.PFlowNo FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B WHERE A.TodoEmps  not like '%" + WebUser.getNo() + ",%' AND  A.FK_Flow=" + dbStr + "FK_Flow  AND A.WorkID=B.WorkID AND B.FK_Emp=" + dbStr + "FK_Emp AND B.IsEnable=1 AND B.IsPass != -2 AND  (B.IsPass=1 or B.IsPass < -1 ) AND  A.Starter=" + dbStr + "Starter AND A.WFState NOT IN ( 0, 1, 3 )";
				sql += Check_CCBPMRunModel();
				if (isContainFuture == true)
				{
					sql += futureSQL;
				}
				sql += " UNION SELECT DISTINCT a.WorkID,a.Starter,a.StarterName,a.Title,a.DeptName,a.NodeName,a.RDT,a.FK_Node,a.FK_Flow,a.FID ,a.FlowName,a.TodoEmps,A.FK_Node AS CurrNode,0 AS RunType,a.WFState, 1 AS WhoExeIt,a.PWorkID,a.PFlowNo FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B,WF_Node C WHERE   A.FK_Flow=" + dbStr + "FK_Flow AND A.WorkID=B.WorkID AND A.FK_Node=C.NodeID AND B.FK_Node=C.NodeID AND C.WhoExeIt=0 AND B.FK_Emp=" + dbStr + "FK_Emp AND B.IsEnable=1 AND B.IsPass = 0 AND B.WhoExeIt=1 AND  A.Starter=" + dbStr + "Starter AND A.WFState NOT IN ( 0, 1, 3 )";
				sql += Check_CCBPMRunModel();
				ps.SQL = sql;
				ps.Add("FK_Flow", fk_flow, false);
				ps.Add("FK_Emp", userNo, false);
				ps.Add("Starter", userNo, false);
			}
			else
			{
				sql = "SELECT DISTINCT a.WorkID,a.Starter,a.StarterName,a.Title,a.DeptName,a.NodeName,a.RDT,a.FK_Node,a.FK_Flow,a.FID ,a.FlowName,a.TodoEmps ," + currNode + " AS CurrNode,0 AS RunType,a.WFState, 0 AS WhoExeIt,a.PWorkID,a.PFlowNo   FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B WHERE A.TodoEmps  not like '%" + WebUser.getNo() + ",%' AND  A.FK_Flow=" + dbStr + "FK_Flow  AND A.WorkID=B.WorkID AND B.FK_Emp=" + dbStr + "FK_Emp AND B.IsEnable=1 AND B.IsPass != -2 AND (B.IsPass=1 or B.IsPass < -1 ) AND A.WFState NOT IN ( 0, 1, 3 )";
				sql += Check_CCBPMRunModel();
				if (isContainFuture == true)
				{
					sql += futureSQL;
				}
				sql += " UNION SELECT DISTINCT a.WorkID,a.Starter,a.StarterName,a.Title,a.DeptName,a.NodeName,a.RDT,a.FK_Node,a.FK_Flow,a.FID ,a.FlowName,a.TodoEmps,A.FK_Node AS CurrNode,0 AS RunType,a.WFState, 1 AS WhoExeIt,a.PWorkID,a.PFlowNo  FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B,WF_Node C WHERE   A.FK_Flow=" + dbStr + "FK_Flow AND A.WorkID=B.WorkID AND A.FK_Node=C.NodeID AND B.FK_Node=C.NodeID AND C.WhoExeIt=0 AND B.FK_Emp=" + dbStr + "FK_Emp AND B.IsEnable=1 AND B.IsPass = 0 AND B.WhoExeIt=1  AND A.WFState NOT IN ( 0, 1, 3 )";
				sql += Check_CCBPMRunModel();

				ps.SQL = sql;
				ps.Add("FK_Flow", fk_flow, false);
				ps.Add("FK_Emp", userNo, false);
			}
		}

		ps.SQL = ps.SQL + " Order By RDT DESC";
		//获得sql.
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
			dt.Columns.get("TITLE").ColumnName = "Title";

			dt.Columns.get("NODENAME").ColumnName = "NodeName";
			dt.Columns.get("RDT").ColumnName = "RDT";

			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";

			dt.Columns.get("FID").ColumnName = "FID";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";

			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";

			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";

			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";

			dt.Columns.get("CURRNODE").ColumnName = "CurrNode";
			dt.Columns.get("RUNTYPE").ColumnName = "RunType";
			dt.Columns.get("WHOEXEIT").ColumnName = "WhoExeIt";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("startername").ColumnName = "StarterName";
			dt.Columns.get("title").ColumnName = "Title";

			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("rdt").ColumnName = "RDT";

			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";

			dt.Columns.get("fid").ColumnName = "FID";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";

			dt.Columns.get("flowname").ColumnName = "FlowName";

			dt.Columns.get("deptname").ColumnName = "DeptName";

			dt.Columns.get("todoemps").ColumnName = "TodoEmps";

			dt.Columns.get("currnode").ColumnName = "CurrNode";
			dt.Columns.get("runtype").ColumnName = "RunType";
			dt.Columns.get("whoexeit").ColumnName = "WhoExeIt";
		}

		return dt;
	}

	public static String Check_CCBPMRunModel(){
		String sql = "";
		//for 宁波港SaaS模式增加OrgNo判断
		if(bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS){
			sql += " AND A.OrgNo = '" + WebUser.getOrgNo() + "'";
		}
		return sql;
	}
	/**
	 在途统计:用于流程查询

	 @return 返回 FK_Flow,FlowName,Num 三个列.
	*/
	public static DataTable DB_TongJi_Runing()
	{
		String dbStr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();

		ps.SQL = "SELECT FK_Flow,FlowName, Count(WorkID) as Num FROM WF_GenerWorkFlow WHERE Emps like '%@" + WebUser.getNo() + "," + WebUser.getName() + "@%' AND TodoEmps NOT like '%" + WebUser.getNo() + "," + WebUser.getName() + ";%' AND WFState != 3  GROUP BY FK_Flow, FlowName";
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("NUM").ColumnName = "Num";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("num").ColumnName = "Num";
		}

		return dt;
	}
	/**
	 统计流程状态

	 @return 返回：流程类别编号，名称，流程编号，流程名称，TodoSta0代办中,TodoSta1预警中,TodoSta2预期中,TodoSta3已办结.
	*/
	public static DataTable DB_TongJi_TodoSta()
	{
		String dbStr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();

		ps.SQL = "SELECT a.FK_Flow,a.FlowName, Count(a.WorkID) as Num FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B WHERE A.WorkID=B.WorkID AND B.FK_Emp=" + dbStr + "FK_Emp AND B.IsEnable=1 AND  (B.IsPass=1 or B.IsPass < 0) GROUP BY A.FK_Flow, A.FlowName";
		ps.Add("FK_Emp", WebUser.getNo(), false);

		return DBAccess.RunSQLReturnTable(ps);
	}
	/**
	 获取未完成的流程(也称为在途流程:我参与的但是此流程未完成)

	 @param userNo
	 @param fk_flow
	 @param titleKey
	 @return
	*/
	public static DataTable DB_GenerRuning2(String userNo, String fk_flow, String titleKey)
	{
		String sql;
		int state = WFState.Runing.getValue();
		if (DataType.IsNullOrEmpty(fk_flow))
		{
			if (DataType.IsNullOrEmpty(titleKey))
			{
				sql = "SELECT a.* FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B WHERE A.WorkID=B.WorkID AND B.FK_Emp='" + userNo + "' AND B.IsEnable=1 AND  (B.IsPass=1 or B.IsPass < 0) and A.FK_Flow!='010'";
			}
			else
			{
				sql = "SELECT a.* FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B WHERE A.WorkID=B.WorkID AND B.FK_Emp='" + userNo + "' AND B.IsEnable=1 AND  (B.IsPass=1 or B.IsPass < 0) and A.FK_Flow!='010' and A.Title Like '%" + titleKey + "%'";
			}
		}
		else
		{
			if (DataType.IsNullOrEmpty(titleKey))
			{
				sql = "SELECT a.* FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B WHERE A.FK_Flow='" + fk_flow + "'  AND A.WorkID=B.WorkID AND B.FK_Emp='" + userNo + "' AND B.IsEnable=1 AND (B.IsPass=1 or B.IsPass < 0 )";
			}
			else
			{
				sql = "SELECT a.* FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B WHERE A.FK_Flow='" + fk_flow + "'  AND A.WorkID=B.WorkID AND B.FK_Emp='" + userNo + "' AND B.IsEnable=1 AND (B.IsPass=1 or B.IsPass < 0 ) and A.Title Like '%" + titleKey + "%' ";
			}
		}

		return DBAccess.RunSQLReturnTable(sql);
	}

	/**
	 获取未完成的流程(也称为在途流程:我参与的但是此流程未完成)

	 @return 返回从数据视图WF_GenerWorkflow查询出来的数据.
	*/

	public static DataTable DB_GenerRuning(String userNo, boolean isContainFuture)
	{
		return DB_GenerRuning(userNo, isContainFuture, null);
	}

//	public static DataTable DB_GenerRuning(String userNo)
//	{
//		return DB_GenerRuning(userNo, false, null);
//	}

	public static DataTable DB_GenerRuning()
	{
		return DB_GenerRuning(null, false, null);
	}

	public static DataTable DB_GenerRuning(String userNo, boolean isContainFuture, String domainExt)
	{
		if (userNo == null)
		{
			userNo = WebUser.getNo();
		}

		DataTable dt = DB_GenerRuning(userNo, null, false, domainExt, isContainFuture);

		/*暂时屏蔽type的拼接，拼接后转json会报错 于庆海修改*/
		/*dt.Columns.Add("Type");
		foreach (DataRow row in dt.Rows)
		{
		    row["Type"] = "RUNNING";
		}*/

//		dt.DefaultView.setSort("RDT DESC");
//		return dt.DefaultView.ToTable();
		return dt;
	}
	/**
	 获取某一个人的在途（参与、未完成的工作、历史任务）

	 @param userNo
	 @return
	*/
	public static DataTable DB_GenerRuning(String userNo)
	{
		DataTable dt = DB_GenerRuning(userNo, null);
		dt.Columns.Add("Type");

		for (DataRow row : dt.Rows)
		{
			row.setValue("Type", "RUNNING");
		}

//		dt.DefaultView.setSort("RDT DESC");
//		return dt.DefaultView.ToTable();
		return dt;
	}
	/**
	 把抄送的信息也发送

	 @return
	*/
	public static DataTable DB_GenerRuningAndCC()
	{
		DataTable dt = DB_GenerRuning();
		DataTable ccDT = DB_CCList_CheckOver(WebUser.getNo());
		try
		{
			dt.Columns.Add("MyPK");
			dt.Columns.Add("Sta");
		}
		catch (RuntimeException e)
		{

		}

		for (DataRow row : ccDT.Rows)
		{
			DataRow newRow = dt.NewRow();

			for (DataColumn column : ccDT.Columns)
			{
				for (DataColumn dtColumn : dt.Columns)
				{
					if (Objects.equals(column.ColumnName, dtColumn.ColumnName))
					{
						newRow.setValue(column.ColumnName, row.get(dtColumn.ColumnName));
					}
				}
			}
			newRow.setValue("Type", "CC");
			dt.Rows.add(newRow);
		}
//		dt.DefaultView.setSort("RDT DESC");
//		return dt.DefaultView.ToTable();
		return dt;
	}
	/**
	 为什么需要这个接口？

	 @param name
	 @param fk_flow
	 @param title
	 @return
	*/
	public static DataTable DB_GenerRuning3(String name, String fk_flow, String title)
	{
		DataTable dt = DB_GenerRuning2(name, fk_flow, title);

		dt.Columns.Add("Type");

		for (DataRow row : dt.Rows)
		{
			row.setValue("Type", "RUNNING");
		}

//		dt.DefaultView.setSort("RDT DESC");
//		return dt.DefaultView.ToTable();
		return dt;
	}


		///#endregion 获取当前操作员的共享工作


		///#region 获取当前的批处理工作
	/**
	 获取当前节点的批处理工作

	 @param FK_Node
	 @return
	*/
	public static DataTable GetBatch(int FK_Node) throws Exception {

		bp.wf.Node nd = new bp.wf.Node(FK_Node);
		Flow fl = nd.getHisFlow();
		String fromTable = "";

		fromTable = fl.getPTable();

		String sql = "SELECT a.*, b.Starter,b.Title as STitle,b.ADT,b.WorkID FROM " + fromTable + " a , WF_EmpWorks b WHERE a.OID=B.WorkID AND b.WFState Not IN (7) AND b.FK_Node=" + nd.getNodeID() + " AND b.FK_Emp='" + WebUser.getNo() + "'";
		// String sql = "SELECT Title,RDT,ADT,SDT,FID,WorkID,Starter FROM WF_EmpWorks WHERE FK_Emp='" + WebUser.getNo() + "'";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return dt;
	}

		///#endregion 获取当前的批处理工作
		///#endregion
		///#region 登录接口

	/**
	 按照token登录 2021.07.01 采用新方式.

	 @param token
	*/
	public static String Port_LoginByToken(String token)
	{
		try
		{
			if (DataType.IsNullOrEmpty(token))
				throw new RuntimeException("err@ token 不能为空.");
			token = token.trim();
			if (DataType.IsNullOrEmpty(token) == true || token.contains(" "))
				throw new RuntimeException("err@非法的Token.");
			if (token.length() <= 6)
				throw new Exception("err@非法的Token." + token);

			if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			{
				Token mytk = new Token();
				mytk.setMyPK(token);
				if (mytk.RetrieveFromDBSources() == 0)
				{
					throw new RuntimeException("@token=[" + token + "]失效");
				}
				bp.wf.Dev2Interface.Port_Login(mytk.getEmpNo(), mytk.getOrgNo());
				WebUser.setToken(token);
				return mytk.getEmpNo();
			}

			//如果是宽泛模式.
			if (SystemConfig.getTokenModel() == 0)
			{
				Token tk = new Token();
				tk.setMyPK(token);
				if (tk.RetrieveFromDBSources() == 0)
				{
					throw new RuntimeException("err@ token 过期或失效.");
				}

				WebUser.setNo(tk.getEmpNo());
				WebUser.setName(tk.getEmpName());

				WebUser.setDeptNo(tk.getDeptNo());
				WebUser.setDeptName(tk.getDeptName());

				WebUser.setOrgNo(tk.getOrgNo());
				WebUser.setOrgName(tk.getOrgName());
				WebUser.setToken(token);
				return tk.getEmpNo();
			}


			//String sql = "SELECT No,OrgNo FROM WF_Emp WHERE AtPara LIKE '%" + token + "%'";
			Paras paras = new Paras();
			String sql ="SELECT No,OrgNo FROM WF_Emp WHERE AtPara LIKE ";
			if (SystemConfig.getAppCenterDBVarStr().equals("@") || SystemConfig.getAppCenterDBType( ) == DBType.MySQL || SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
				sql+=SystemConfig.getAppCenterDBType( ) == DBType.MySQL?(" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() +  "Token,'%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + "Token+'%'");
			else
				sql+=" '%'||" + SystemConfig.getAppCenterDBVarStr() + "Token||'%'";
			paras.SQL=sql;
			paras.Add("Token",token);
			DataTable dt = DBAccess.RunSQLReturnTable(paras);
			if (dt.Rows.size() != 1)
			{
				throw new RuntimeException("err@token失效." + token);
			}

			String no = dt.Rows.get(0).getValue(0).toString();
			String orgNo = dt.Rows.get(0).getValue(1) != null ? dt.Rows.get(0).getValue(1).toString() : "";
			//执行登录.
			bp.wf.Dev2Interface.Port_Login(no, orgNo);
			WebUser.setToken(token);
			return no;
		}
		catch (RuntimeException ex)
		{
			throw ex;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 登录

	 @param userID 人员编号
	 @param orgNo 组织结构编码
	 @return
	*/

	public static void Port_Login(String userID, String orgNo) throws Exception {
		Port_Login(userID, orgNo, null);
	}

	public static void Port_Login(String userID) throws Exception {
		Port_Login(userID, null, null);
	}

	public static void Port_Login(String userID, String orgNo, String deptNo) throws Exception {
		/* 仅仅传递了人员编号，就按照人员来取.*/
		Emp emp = new Emp();
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			if (orgNo == null)
			{
				throw new RuntimeException("err@缺少OrgNo参数.");
			}
			emp.setNo(orgNo + "_" + userID);
			emp.setOrgNo(orgNo);
			if (emp.RetrieveFromDBSources() == 0)
			{
				throw new RuntimeException("err@用户名:" + emp.GetValByKey("No") + "不存在.");
			}

			WebUser.SignInOfGener(emp, "CH", false, false, null, null);
			return;
		}

		emp.setNo(userID);

		int i = emp.RetrieveFromDBSources();
		if (i == 0)
		{
			throw new RuntimeException("err@用户名:" + emp.GetValByKey("No") + "不存在.");
		}

		String sql = "";
		// 两个同是是Null.
		if (DataType.IsNullOrEmpty(deptNo) == true && DataType.IsNullOrEmpty(orgNo) == true)
		{
			if (DataType.IsNullOrEmpty(emp.getDeptNo()) == true)
			{
				//寻找随机的部门编号登录》
				//如果选择的组织不存在，就从他的隶属部门里去找一个部门。
				if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
				{
					sql = "SELECT A.FK_Dept,B.OrgNo FROM Port_DeptEmp A, Port_Dept B WHERE A.FK_Dept=B.No AND A.FK_Emp='" + emp.getNo() + "'";
				}
				else
				{
					sql = "SELECT A.FK_Dept,B.OrgNo FROM Port_DeptEmp A, Port_Dept B WHERE A.FK_Dept=B.No AND A.FK_Emp='" + emp.getNo() + "' AND  B.OrgNo!='' AND B.OrgNo IS NOT NULL ";
				}

				deptNo = DBAccess.RunSQLReturnStringIsNull(sql, "");
				if (DataType.IsNullOrEmpty(deptNo) == true)
				{
					throw new RuntimeException("err@用户[" + emp.getNo() + "]没有部门，无法登录.");
				}
				emp.setDeptNo(deptNo);
				WebUser.SignInOfGener(emp, "CH", false, false, null, null);
				return;
			}
			WebUser.setCurrentUser(emp.getUserID());
			WebUser.SignInOfGener(emp, "CH", false, false, null, null);
			return;
		}

		//如果部门编号不为空.
		if (DataType.IsNullOrEmpty(deptNo) == false)
		{
			if (emp.getDeptNo().equals(deptNo) == false)
			{
				//判断当前人员是否在该部门下
				sql = "SELECT COUNT(*) From Port_DeptEmp WHERE FK_Emp='" + emp.getNo() + "' AND FK_Dept='" + deptNo + "'";
				if (DBAccess.RunSQLReturnValInt(sql) == 0)
				{
					throw new RuntimeException("err@用户[" + emp.getNo() + "]不在部门[" + deptNo + "]，无法登录.");
				}
			}
			emp.setDeptNo(deptNo);
			WebUser.SignInOfGener(emp, "CH", false, false, null, null);
			return;
		}

		if (orgNo == null)
		{
			return;
		}

		//当前人员的 orgNo 是组织编号.
		if (emp.getOrgNo().equals(orgNo) == true)
		{
			if (DataType.IsNullOrEmpty(emp.getDeptNo()) == true)
			{
				//当前人员部门不存在。
				if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
				{
					sql = "SELECT A.FK_Dept,B.OrgNo FROM Port_DeptEmp A, Port_Dept B WHERE A.FK_Dept=B.No AND A.FK_Emp='" + emp.getNo() + "'";
				}
				else
				{
					sql = "SELECT A.FK_Dept,B.OrgNo FROM Port_DeptEmp A, Port_Dept B WHERE A.FK_Dept=B.No AND A.FK_Emp='" + emp.getNo() + "' AND  B.OrgNo='" + orgNo + "'";
				}

				deptNo = DBAccess.RunSQLReturnStringIsNull(sql, "");
				if (DataType.IsNullOrEmpty(deptNo) == true)
				{
					throw new RuntimeException("err@用户[" + emp.getNo() + "]没有部门，无法登录.");
				}
				emp.setDeptNo(deptNo);
				WebUser.SignInOfGener(emp, "CH", false, false, null, null);
				return;
			}
			WebUser.SignInOfGener(emp, "CH", false, false, null, null);
			return;
		}



		if (emp.getOrgNo().equals(orgNo) == false)
		{
			//如果选择的组织不存在，就从他的隶属部门里去找一个部门。
			sql = "SELECT A.FK_Dept FROM Port_DeptEmp A, Port_Dept B WHERE A.FK_Dept=B.No AND A.FK_Emp='" + emp.getNo() + "' AND B.OrgNo='" + orgNo + "' ";
			deptNo = DBAccess.RunSQLReturnString(sql);
			if (DataType.IsNullOrEmpty(deptNo) == true)
			{
				throw new RuntimeException("err@用户[" + emp.getNo() + "]在OrgNo[" + orgNo + "]下没有部门.");
			}


		}

		emp.setDeptNo(deptNo);
		WebUser.SignInOfGener(emp, "CH", false, false, null, null);
	}
	/**
	 注销当前登录
	*/
	public static void Port_SigOut() throws Exception {
		WebUser.Exit();
	}
	/**
	 获取未读的消息
	 用于消息提醒.

	 @param userNo 用户ID
	*/
	public static String Port_SMSInfo(String userNo)
	{
		Paras ps = new Paras();
		ps.SQL = "SELECT MyPK, EmailTitle  FROM sys_sms where SendTo=" + SystemConfig.getAppCenterDBVarStr() + "SendTo AND IsAlert=0";
		ps.Add("SendTo", userNo, false);
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		String strs = "";
		for (DataRow dr : dt.Rows)
		{
			strs += "@" + dr.getValue(0) + "=" + dr.getValue(1).toString();
		}
		ps = new Paras();
		ps.SQL = "UPDATE  Sys_SMS SET IsAlert=1 WHERE  SendTo=" + SystemConfig.getAppCenterDBVarStr() + "SendTo AND IsAlert=0";
		ps.Add("SendTo", userNo, false);
		DBAccess.RunSQL(ps);
		return strs;
	}
	/**
	 发送消息

	 @param userNo 信息接收人
	 @param msgTitle 标题
	 @param msgDoc 内容
	*/
	public static void Port_SendMsg(String userNo, String msgTitle, String msgDoc, String msgFlag) throws Exception {
		Port_SendMsg(userNo, msgTitle, msgDoc, msgFlag, bp.wf.SMSMsgType.Self, null, 0, 0, 0);
	}
	/**
	 生成token
	 @return
	*/

	public static String Port_GenerToken() throws Exception {
		return Port_GenerToken("PC");
	}

	public static String Port_GenerToken(String logDev) throws Exception {
		return Port_GenerToken(logDev,"");
	}
	public static String Port_GenerToken(String logDev,String token) throws Exception {
		return Port_GenerToken(logDev,token,false);
	}
	public static String Port_GenerToken(String logDev,String token,boolean isGenerNewToken) throws Exception {
		return Port_GenerToken(logDev,token,false,WebUser.getNo());
	}
	public static String Port_GenerToken(String logDev, String token, boolean isGenerNewToken, String userNo) throws Exception {

		if (DataType.IsNullOrEmpty(token) == true)
		{
			token = DBAccess.GenerGUID(0, null, null);
		}

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			excuteFirstLogin();
			Token mytk = new Token();
			mytk.setMyPK(token);
			mytk.setEmpNo(WebUser.getNo());
			mytk.setEmpName(WebUser.getName());

			mytk.setDeptNo(WebUser.getDeptNo());
			mytk.setDeptName(WebUser.getDeptName());

			mytk.setOrgNo(WebUser.getOrgNo());
			mytk.setOrgName(WebUser.getOrgName());
			mytk.Insert();
			WebUser.setToken(mytk.getMyPK());
			return mytk.getMyPK();
		}

		//单点模式,严格模式.
		if (SystemConfig.getTokenModel() ==1)
		{
			return Port_GenerToken_2021(userNo, logDev, 0, isGenerNewToken);
		}
		excuteFirstLogin();
		//记录token.
		Token tk = new Token();
		tk.setMyPK(token);

		tk.setEmpNo(WebUser.getNo());
		tk.setEmpName(WebUser.getName());

		tk.setDeptNo(WebUser.getDeptNo());
		tk.setDeptName(WebUser.getDeptName());

		tk.setOrgNo(WebUser.getOrgNo());
		//  tk.setOrgName( bp.web.WebUser.getOrgName();
		tk.setRDT(DataType.getCurrentDateTime()); //记录日期.

		if (logDev.equals("PC"))
		{
			tk.setSheBei(0);
		}
		else
		{
			tk.setSheBei(1);
		}
		tk.Insert();

		WebUser.setToken(tk.getMyPK());
		return tk.getMyPK();
	}

	/**
	 * 判断是否第一次登录
	 */
	private static void excuteFirstLogin()   {
		String no=WebUser.getNo();
		Token tk = new Token();
		tk.setEmpNo(no);
		boolean isExist= false;
		try {
			isExist = tk.IsExit("EmpNo",no);
		} catch (Exception e) {
		}
		WebUser.setIsFirstLogin("0");
		if(!isExist && SystemConfig.getIsPassWordChangeBYFirstLogin().equals("1")){
			WebUser.setIsFirstLogin("1");
		}
	}
	/**
	 外部写入token

	 @param token
	 @return
	*/

	public static String Port_WriteToken(String token) throws Exception {
		return Port_WriteToken(token, "PC");
	}

	public static String Port_WriteToken(String token, String logDev) throws Exception {
		if (DataType.IsNullOrEmpty(token) == true)
		{
			throw new RuntimeException("err@传入的Token值为空,请使用Port_GenerToken方法");
		}
		if (SystemConfig.getTokenModel() == 1)
		{
			if (logDev == null)
			{
				logDev = "PC";
			}

			String key = "Token_" + logDev; //para的参数.

			WFEmp emp = new WFEmp(WebUser.getNo());
			emp.SetPara("Online", "1");
			emp.SetPara(key, token);
			emp.Update();
			WebUser.setToken(token);
			return token;
		}
		//记录token.
		Token tk = new Token();
		tk.setMyPK(token);
		int i = tk.RetrieveFromDBSources();
		if (i == 1)
		{
			if (tk.getEmpNo().equals(WebUser.getNo()) == false)
			{
				throw new RuntimeException("err@传入的Token值在Port_Token中已经存在但是与登录账号不匹配");
			}
			WebUser.setToken(tk.getMyPK());

			tk.setDeptNo(WebUser.getDeptNo());
			tk.setDeptName(WebUser.getDeptName());
			tk.setOrgNo(WebUser.getOrgNo());
			tk.setOrgName( WebUser.getOrgName());
			tk.Update();
			return token;
		}
		tk.setEmpNo(WebUser.getNo());
		tk.setEmpName(WebUser.getName());

		tk.setDeptNo(WebUser.getDeptNo());
		tk.setDeptName(WebUser.getDeptName());

		tk.setOrgNo(WebUser.getOrgNo());
		tk.setOrgName( WebUser.getOrgName());
		tk.setRDT(DataType.getCurrentDateTime()); //记录日期.

		if (logDev.equals("PC"))
		{
			tk.setSheBei(0);
		}
		else
		{
			tk.setSheBei(1);
		}
		tk.Insert();

		WebUser.setToken(tk.getMyPK());
		return tk.getMyPK();

	}

	/**
	 获取Toekn. 旧版本.

	 @param userNo 用户编号
	 @param logDev 登录设备
	 @param activeMinutes 有效时间
	 @return
	*/

	private static String Port_GenerToken_2021(String userNo, String logDev, int activeMinutes) throws Exception {
		return Port_GenerToken_2021(userNo, logDev, activeMinutes, false);
	}

	private static String Port_GenerToken_2021(String userNo, String logDev) throws Exception {
		return Port_GenerToken_2021(userNo, logDev, 0, false);
	}

	private static String Port_GenerToken_2021(String userNo) throws Exception {
		return Port_GenerToken_2021(userNo, "PC", 0, false);
	}

	private static String Port_GenerToken_2021(String userNo, String logDev, int activeMinutes, boolean isGenerNewToken) throws Exception {
		if (DataType.IsNullOrEmpty(logDev))
		{
			logDev = "PC";
		}

		if (activeMinutes == 0)
		{
			activeMinutes = 300; //默认为300分钟.
		}

		String key = "Token_" + logDev; //para的参数.
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			userNo = WebUser.getOrgNo() + "_" + userNo;
		}
		WFEmp emp = new WFEmp(userNo);
		emp.SetPara("Online", "1");
		emp.setOrgNo(WebUser.getOrgNo());

		String myGuid = emp.GetParaString(key); //获得token.
		String guidOID_Dt = emp.GetParaString(key + "_DT"); // token 的过期日期.

		WebUser.setIsFirstLogin("0");
			///#region 如果是第1次登录，就生成新的token.
		if (isGenerNewToken == true || DataType.IsNullOrEmpty(myGuid) == true || DataType.IsNullOrEmpty(guidOID_Dt) == true)
		{
			String guid = DBAccess.GenerGUID(0, null, null);
			emp.SetPara(key, guid);

			LocalDateTime dt = LocalDateTime.now();
			dt = dt.plusMinutes(activeMinutes);

			emp.SetPara(key + "_DT", dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			emp.Update();
			WebUser.setToken(guid);
			if(SystemConfig.getIsPassWordChangeBYFirstLogin().equals("1")){
				WebUser.setIsFirstLogin("1");
			}
			return guid;
		}

			///#endregion 如果是第1次登录，就生成新的token.

		//判断是否超时.
		Date dtTo = DataType.ParseSysDateTime2DateTime(guidOID_Dt);
		if (dtTo.compareTo(Calendar.getInstance().getTime()) < 0)
		{
			//超时，就返回一个新的token.
			LocalDateTime dtUpdate = LocalDateTime.now();
			dtUpdate = dtUpdate.plusMinutes(activeMinutes);

			myGuid = DBAccess.GenerGUID(0, null, null); //生成新的GUID.

			emp.SetPara(key + "_DT", dtUpdate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			emp.SetPara(key, myGuid);
			emp.Update();
			WebUser.setToken(myGuid);
		}
		else
		{
			//重新登录，需要重新计算超时时间
			LocalDateTime dtUpdate = LocalDateTime.now();
			dtUpdate = dtUpdate.plusMinutes(activeMinutes);
			emp.SetPara(key + "_DT", dtUpdate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			emp.Update();
		}
		WebUser.setToken(myGuid);
		return myGuid;
	}
	/**
	 验证用户的合法性

	 @param userNo 用户编号
	 @return 是否匹配
	*/
	public static boolean Port_CheckUserLogin(String userNo, String SID) throws Exception {
		return true;
/*
		if (DataType.IsNullOrEmpty(userNo))
		{
			return false;
		}

		if (DataType.IsNullOrEmpty(SID))
		{
			return false;
		}

		Paras ps = new Paras();
		ps.SQL = "SELECT SID FROM Port_Emp WHERE No=" + SystemConfig.getAppCenterDBVarStr() + "No";
		ps.Add("No", userNo, false);

		String mysid = DBAccess.RunSQLReturnStringIsNull(ps, null);
		if (mysid == null)
		{
			throw new RuntimeException("@没有取得用户(" + userNo + ")的SID.");
		}


		if (Objects.equals(mysid, SID))
		{
			return true;
		}
		return false;*/
	}
	/**
	 发送邮件与消息(如果传入4大流程参数将会增加一个工作链接)

	 @param userNo 信息接收人
	 @param title 标题
	 @param msgDoc 内容
	 @param msgFlag 消息标志
	 @param flowNo 流程编号
	 @param nodeID 节点ID
	 @param workID 工作ID
	 @param fid FID
	*/

	public static void Port_SendMsg(String userNo, String title, String msgDoc, String msgFlag, String msgType, String flowNo, long nodeID, long workID, long fid) throws Exception {
		Port_SendMsg(userNo, title, msgDoc, msgFlag, msgType, flowNo, nodeID, workID, fid, null);
	}

	public static void Port_SendMsg(String userNo, String title, String msgDoc, String msgFlag, String msgType, String flowNo, long nodeID, long workID, long fid, String pushModel) throws Exception {
		String url = "";
		if (workID != 0)
		{
			//url = Glo.HostURL + "WF/Do.htm?Token=" + userNo + "_" + workID + "_" + nodeID;
			//url = url.replace("//", "/");
			//url = url.replace("//", "/");
			//if (msgType == BP.WF.SMSMsgType.DoPress)
			//    url = url + "&DoType=OF";
			//if (msgType == BP.WF.SMSMsgType.CC)
			//    url = url + "&DoType=DoOpenCC";

			//msgDoc += " <hr>打开工作: " + url;
		}
		if (DataType.IsNullOrEmpty(msgFlag) == true)
		{
			msgFlag = "WKAlt" + nodeID + "_" + workID;
		}

		String atParas = "@FK_Flow=" + flowNo + "@WorkID=" + workID + "@NodeID=" + nodeID + "@FK_Node=" + nodeID;
		bp.wf.SMS.SendMsg(userNo, title, msgDoc, msgFlag, msgType, atParas, workID, pushModel, url);
	}
	/**
	 写入消息
	 用途可以处理提醒.

	 @param sendToUserNo 发送给的操作员ID
	 @param sendDT 发送时间，如果null 则表示立刻发送。
	 @param title 标题
	 @param doc 内容
	 @param msgFlag 消息标记
	 @return 写入成功或者失败.
	*/
	public static boolean Port_WriteToSMS(String sendToUserNo, String sendDT, String title, String doc, String msgFlag, long workid) throws Exception {
		SMS.SendMsg(sendToUserNo, title, doc, msgFlag, "Info", "", workid);
		return true;
	}

	public static void Port_SendEmail(String mailAddr, String title, String docs)
	{
		SMS.SendEmailNowAsync(mailAddr, title, docs);
	}

	/**
	 发送消息

	 @param sendToEmpNo 接收人
	 @param smsDoc 消息内容
	 @param emailTitle 邮件标题
	 @param msgType 消息类型(例如工作到达后、发送成功后)
	 @param msgGroupFlag 消息分组（与消息类型有关联）
	 @param sendEmpNo 发送人
	 @param openUrl 连接URL
	 @param pushModel 可以接受消息的类型(如邮件、短信、丁丁、微信等)
	 @param msgPK 唯一标志,防止发送重复.
	*/

	public static void Port_SendMessage(String sendToEmpNo, String smsDoc, String emailTitle, String msgType, String msgGroupFlag, String sendEmpNo, String openUrl, String pushModel, long workID, String msgPK) throws Exception {
		Port_SendMessage(sendToEmpNo, smsDoc, emailTitle, msgType, msgGroupFlag, sendEmpNo, openUrl, pushModel, workID, msgPK, null);
	}

	public static void Port_SendMessage(String sendToEmpNo, String smsDoc, String emailTitle, String msgType, String msgGroupFlag, String sendEmpNo, String openUrl, String pushModel, long workID) throws Exception {
		Port_SendMessage(sendToEmpNo, smsDoc, emailTitle, msgType, msgGroupFlag, sendEmpNo, openUrl, pushModel, workID, null, null);
	}

	public static void Port_SendMessage(String sendToEmpNo, String smsDoc, String emailTitle, String msgType, String msgGroupFlag, String sendEmpNo, String openUrl, String pushModel, long workID, String msgPK, String atParas) throws Exception {
		String[] arrEmp = sendEmpNo.split(",");
		for (int i = 0; i < arrEmp.length; i++) {
			String item = arrEmp[i];
			if(DataType.IsNullOrEmpty(item)){
				continue;
			}
			Emp emp = null;
			try
			{
				if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
				{
					emp = new Emp(WebUser.getOrgNo() + "_" + item);
				}
				else
				{
					emp = new Emp(item);
				}
			}
			catch (RuntimeException ex)
			{
				return; //没有这个人的信息，就return.
			}

			SMS sms = new SMS();
			if (DataType.IsNullOrEmpty(msgPK) == false)
			{
				/*如果有唯一标志,就判断是否有该数据，没有该数据就允许插入.*/
				if (sms.IsExit(SMSAttr.MyPK, msgPK) == true)
				{
					return;
				}
				sms.setMyPK(msgPK);
			}
			else
			{
				sms.setMyPK(DBAccess.GenerGUID(0, null, null));
			}

			sms.setHisEmailSta(MsgSta.UnRun);
			sms.setHisMobileSta(MsgSta.UnRun);

			if (sendEmpNo == null)
			{
				sms.setSender(WebUser.getNo());
			}
			else
			{
				sms.setSender(sendEmpNo);
			}

			//发送给人员ID , 有可能这个人员空的.
			sms.setSendToEmpNo(sendToEmpNo);


			///#region 邮件信息
			//邮件地址.
			sms.setEmail(emp.getEmail());
			//邮件标题.
			sms.setTitle(emailTitle);
			sms.setDocOfEmail(smsDoc);

			///#endregion 邮件信息


			///#region 短消息信息
			sms.setMobile(emp.getTel());
			sms.setMobileInfo(smsDoc);
			sms.setTitle(emailTitle);

			///#endregion 短消息信息

			// 其他属性.
			sms.setRDT(DataType.getCurrentDateTime());

			sms.setMsgType(msgType); // 消息类型.

			sms.setMsgFlag(msgGroupFlag); // 消息分组标志,用于批量删除.

			sms.setAtPara(atParas);

			sms.setWorkID(workID);


			sms.SetPara("OpenUrl", openUrl);
			sms.SetPara("PushModel", pushModel);

			// 先保留本机一份.
			sms.Insert();
		}
	}


	/**
	 获取最新的消息

	 @param userNo 用户编号
	 @param dateLastTime 上次获取的时间
	 @return 返回消息：返回两个列的数据源MsgType,Num.
	*/
	public static DataTable Port_GetNewMsg(String userNo, String dateLastTime)
	{
		Paras ps = new Paras();
		ps.SQL = "SELECT MsgType , Count(*) as Num FROM Sys_SMS WHERE SendTo=" + SystemConfig.getAppCenterDBVarStr() + "SendTo AND RDT >" + SystemConfig.getAppCenterDBVarStr() + "RDT Group By MsgType";
		ps.Add(bp.wf.SMSAttr.SendTo, userNo, false);
		ps.Add(bp.wf.SMSAttr.RDT, dateLastTime, false);
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		return dt;
	}
	/**
	 获取最新的消息

	 @param userNo 用户编号
	 @return
	*/
	public static DataTable Port_GetNewMsg(String userNo)
	{
		Paras ps = new Paras();
		ps.SQL = "SELECT MsgType , Count(*) as Num FROM Sys_SMS WHERE SendTo=" + SystemConfig.getAppCenterDBVarStr() + "SendTo  Group By MsgType";
		ps.Add(bp.wf.SMSAttr.SendTo, userNo, false);
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		return dt;
	}

		///#endregion 登录接口


		///#region 与流程有关的接口
	/**
	 写入日志

	 @param flowNo 流程编号
	 @param nodeFromID 节点从
	 @param nodeFromName 节点名称
	 @param workid 工作ID
	 @param fid FID
	 @param msg 信息
	 @param at 日志类型
	 @param tag 参数:用@符号隔开比如, @PWorkID=101@PFlowNo=003
	 @param cFlowInfo 子流程信息
	 @param nodeData 节点数据(日志信息)
	*/

	public static void WriteTrack(String flowNo, int nodeFromID, String nodeFromName, long workid, long fid, String msg, ActionType at, String tag, String cFlowInfo, String writeImg, String optionMsg, String empNoTo, String empNameTo, String empNoFrom, String empNameFrom, String rdt, String nodeData) throws Exception {
		WriteTrack(flowNo, nodeFromID, nodeFromName, workid, fid, msg, at, tag, cFlowInfo, writeImg, optionMsg, empNoTo, empNameTo, empNoFrom, empNameFrom, rdt, null, nodeData);
	}

	public static void WriteTrack(String flowNo, int nodeFromID, String nodeFromName, long workid, long fid, String msg, ActionType at, String tag, String cFlowInfo, String writeImg, String optionMsg, String empNoTo, String empNameTo, String empNoFrom, String empNameFrom) throws Exception {
		WriteTrack(flowNo, nodeFromID, nodeFromName, workid, fid, msg, at, tag, cFlowInfo, writeImg, optionMsg, empNoTo, empNameTo, empNoFrom, empNameFrom, null, null, null);
	}

	public static void WriteTrack(String flowNo, int nodeFromID, String nodeFromName, long workid, long fid, String msg, ActionType at, String tag, String cFlowInfo, String writeImg, String optionMsg, String empNoTo, String empNameTo, String empNoFrom) throws Exception {
		WriteTrack(flowNo, nodeFromID, nodeFromName, workid, fid, msg, at, tag, cFlowInfo, writeImg, optionMsg, empNoTo, empNameTo, empNoFrom, null, null, null, null);
	}

	public static void WriteTrack(String flowNo, int nodeFromID, String nodeFromName, long workid, long fid, String msg, ActionType at, String tag, String cFlowInfo, String writeImg, String optionMsg, String empNoTo, String empNameTo) throws Exception {
		WriteTrack(flowNo, nodeFromID, nodeFromName, workid, fid, msg, at, tag, cFlowInfo, writeImg, optionMsg, empNoTo, empNameTo, null, null, null, null, null);
	}

	public static void WriteTrack(String flowNo, int nodeFromID, String nodeFromName, long workid, long fid, String msg, ActionType at, String tag, String cFlowInfo, String writeImg, String optionMsg, String empNoTo) throws Exception {
		WriteTrack(flowNo, nodeFromID, nodeFromName, workid, fid, msg, at, tag, cFlowInfo, writeImg, optionMsg, empNoTo, null, null, null, null, null, null);
	}

	public static void WriteTrack(String flowNo, int nodeFromID, String nodeFromName, long workid, long fid, String msg, ActionType at, String tag, String cFlowInfo, String writeImg, String optionMsg) throws Exception {
		WriteTrack(flowNo, nodeFromID, nodeFromName, workid, fid, msg, at, tag, cFlowInfo, writeImg, optionMsg, null, null, null, null, null, null, null);
	}

	public static String WriteTrack(String flowNo, int nodeFromID, String nodeFromName, long workid, long fid, String msg, ActionType at, String tag, String cFlowInfo, String writeImg) throws Exception {
		return WriteTrack(flowNo, nodeFromID, nodeFromName, workid, fid, msg, at, tag, cFlowInfo, writeImg, null, null, null, null, null, null, null, null);
	}

	public static String WriteTrack(String flowNo, int nodeFromID, String nodeFromName, long workid, long fid, String msg, ActionType at, String tag, String cFlowInfo, String writeImg, String optionMsg, String empNoTo, String empNameTo, String empNoFrom, String empNameFrom, String rdt, String fwcView, String nodeData) throws Exception {
		if (at == ActionType.CallChildenFlow)
		{
			if (DataType.IsNullOrEmpty(cFlowInfo) == true)
			{
				throw new RuntimeException("@必须输入信息cFlowInfo信息,在 CallChildenFlow 模式下.");
			}
		}

		if (DataType.IsNullOrEmpty(optionMsg))
		{
			optionMsg = Track.GetActionTypeT(at);
		}

		Track t = new Track();
		t.setWorkID(workid);
		t.setFID(fid);

		//记录日期.
		LocalDateTime d = LocalDateTime.MIN;
		if (DataType.IsNullOrEmpty(rdt))
		{
			t.setRDT(DataType.getCurrentDateTimess());
		}
		else
		{
			t.setRDT(rdt);
		}

		t.setHisActionType(at);
		t.setActionTypeText(optionMsg);

		t.setNDFrom(nodeFromID);
		t.setNDFromT(nodeFromName);

		if (empNoFrom == null)
		{
			t.setEmpFrom(WebUser.getNo());
		}
		else
		{
			t.setEmpFrom(empNoFrom);
		}

		if (empNameFrom == null)
		{
			t.setEmpFromT(WebUser.getName());
		}
		else
		{
			t.setEmpFromT(empNameFrom);
		}

		t.FlowNo = flowNo;

		t.setNDTo(nodeFromID);
		t.setNDToT(nodeFromName);

		if (empNoTo == null)
		{
			t.setEmpTo(WebUser.getNo());
			t.setEmpToT(WebUser.getName());
		}
		else
		{
			t.setEmpTo(empNoTo);
			t.setEmpToT(empNameTo);
		}

		t.WriteDB = writeImg;
		t.setMsg(msg);
		t.setNodeData("@DeptNo=" + WebUser.getDeptNo() + "@DeptName=" + WebUser.getDeptName());
		if(nodeData != null){
			t.setNodeData(nodeData);
		}

		if (tag != null)
		{
			t.setTag(tag);
		}
		if (fwcView != null)
		{
			t.setTag(t.getTag() + fwcView);
			GenerWorkerList gwl = new GenerWorkerList();
			gwl.setWorkID(workid);
			gwl.setNodeID(nodeFromID);
			gwl.setEmpNo(WebUser.getNo());
			if (gwl.RetrieveFromDBSources()==1)
			{
			gwl.SetPara("FWCView",fwcView.replace("@FWCView=",""));
			gwl.Update();
			}
		}

		try
		{
			t.Insert();

		}
		catch (java.lang.Exception e)
		{
			t.CheckPhysicsTable();
			t.Insert();
		}


			///#region 特殊判断.
		if (at == ActionType.CallChildenFlow)
		{
			/* 如果是吊起子流程，就要向它父流程信息里写数据，让父流程可以看到能够发起那些流程数据。*/
			AtPara ap = new AtPara(tag);
			bp.wf.GenerWorkFlow gwf = new GenerWorkFlow(ap.GetValInt64ByKey(GenerWorkFlowAttr.PWorkID));
			t.setWorkID(gwf.getWorkID());

			t.setNDFrom(gwf.getNodeID());
			t.setNDFromT(gwf.getNodeName());

			t.setNDTo(t.getNDFrom());
			t.setNDToT(t.getNDFromT());

			t.FlowNo = gwf.getFlowNo();

			t.setHisActionType(ActionType.StartChildenFlow);
			t.setTag("@CWorkID=" + workid + "@CFlowNo=" + flowNo);
			t.setMsg(cFlowInfo);
			t.Insert();
		}
		return t.getMyPK();
			///#endregion 特殊判断.
	}
	/**
	 写入日志

	 @param flowNo 流程编号
	 @param nodeFrom 节点从
	 @param workid 工作ID
	 @param fid fID
	 @param msg 信息
	*/
	public static String WriteTrackInfo(String flowNo, int nodeFrom, String ndFromName, long workid, long fid, String msg, String optionMsg) throws Exception {
		return WriteTrack(flowNo, nodeFrom, ndFromName, workid, fid, msg, ActionType.Info, null, null, optionMsg);
	}
	/**
	 写入工作审核日志:

	 @param workid 工作ID
	 @param msg 审核信息
	 @param optionName 操作名称(比如:科长审核、部门经理审批),如果为空就是"审核".
	*/

	public static void Node_WriteWorkCheck(long workid, String msg, String optionName, String writeImg) throws Exception {
		Node_WriteWorkCheck(workid, msg, optionName, writeImg, null);
	}

	public static void Node_WriteWorkCheck(long workid, String msg, String optionName, String writeImg, String fwcView) throws Exception {
		Node_WriteWorkCheck(workid, msg, optionName, writeImg, fwcView,"");
	}
	public static void Node_WriteWorkCheck(long workid, String msg, String optionName, String writeImg, String fwcView,String checkTime) throws Exception {
		String dbStr = SystemConfig.getAppCenterDBVarStr();

		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workid);
		gwf.RetrieveFromDBSources();

		String flowNo = gwf.getFlowNo();
		int currNodeID = gwf.getNodeID();
		long fid = gwf.getFID();

		//求主键 2017.10.6以前的逻辑.
		String tag = currNodeID + "_" + workid + "_" + fid + "_" + WebUser.getNo();

		//求当前是否是会签.  zhangsan,张三;李四;王五;
		String nodeName = gwf.getNodeName();
		Node nd = new Node(currNodeID);
		if (nd.getItIsStartNode() == false)
		{
			if (gwf.getHuiQianTaskSta() == HuiQianTaskSta.HuiQianing && !Objects.equals(gwf.getHuiQianZhuChiRen(), WebUser.getNo()))
			{
				nodeName = nd.getName() + "(会签)";
			}
		}

		//待办抢办模式，一个节点只能有一条记录.
		Paras ps = new Paras();
		if (nd.getTodolistModel() == TodolistModel.QiangBan || nd.getTodolistModel() == TodolistModel.Sharing)
		{
			//先删除其他人员写入的数据. 此脚本是2016.11.30号的,为了解决柳州的问题，需要扩展.
			ps.SQL = "DELETE FROM ND" + Integer.parseInt(flowNo) + "Track WHERE  WorkID=" + dbStr + "WorkID  AND NDFrom=" + dbStr + "NDFrom AND ActionType=" + ActionType.WorkCheck.getValue();
			ps.Add(TrackAttr.WorkID, workid);
			ps.Add(TrackAttr.NDFrom, currNodeID);
			DBAccess.RunSQL(ps);

			//写入日志.
			WriteTrack(flowNo, currNodeID, nodeName, workid, fid, msg, ActionType.WorkCheck, tag, null, writeImg, optionName, null, null, null, null, checkTime, fwcView,null);
			return;
		}

		String trackTable = "ND" + Integer.parseInt(flowNo) + "Track";
		ps.SQL = "UPDATE  " + trackTable + " SET NDFromT=" + dbStr + "NDFromT, Msg=" + dbStr + "Msg, RDT=" + dbStr + "RDT,NodeData=" + dbStr + "NodeData WHERE ActionType=" + ActionType.WorkCheck.getValue() + " AND  NDFrom=" + dbStr + "NDFrom AND  NDTo=" + dbStr + "NDTo AND WorkID=" + dbStr + "WorkID AND FID=" + dbStr + "FID AND EmpFrom=" + dbStr + "EmpFrom";
		ps.Add(TrackAttr.NDFromT, nodeName, false);
		ps.Add(TrackAttr.Msg, msg, false);
		ps.Add(TrackAttr.NDFrom, currNodeID);
		ps.Add(TrackAttr.NDTo, currNodeID);
		ps.Add(TrackAttr.WorkID, workid);
		ps.Add(TrackAttr.FID, fid);
		ps.Add(TrackAttr.EmpFrom, WebUser.getNo(), false);
		if(DataType.IsNullOrEmpty(checkTime)==false)
			ps.Add(TrackAttr.RDT, checkTime);
		else
			ps.Add(TrackAttr.RDT, DataType.getCurrentDateTimess(), false);
		ps.Add(TrackAttr.NodeData, "@DeptNo=" + WebUser.getDeptNo() + "@DeptName=" + WebUser.getDeptName(), false);

		int num = DBAccess.RunSQL(ps);

		if (num == 1 && DataType.IsNullOrEmpty(writeImg) == false ) //&& writeImg.contains("data:image/png;base64,") == true)
		{
			String myPK = DBAccess.RunSQLReturnStringIsNull("SELECT MyPK From " + trackTable + " Where ActionType=" + ActionType.WorkCheck.getValue() + " AND  NDFrom=" + currNodeID + " AND  NDTo=" + currNodeID + " AND WorkID=" + workid + " AND FID=" + fid + " AND EmpFrom='" + WebUser.getNo() + "'", "");
			DBAccess.SaveBigTextToDB(writeImg, trackTable, "MyPK", myPK, "WriteDB");
			return;
		}


		ps.clear();
		ps.SQL = "DELETE FROM ND" + Integer.parseInt(flowNo) + "Track WHERE  NDFrom=" + dbStr + "NDFrom AND  NDTo=" + dbStr + "NDTo AND WorkID=" + dbStr + "WorkID AND FID=" + dbStr + "FID AND EmpFrom=" + dbStr + "EmpFrom";
		ps.Add(TrackAttr.NDFrom, currNodeID);
		ps.Add(TrackAttr.NDTo, currNodeID);
		ps.Add(TrackAttr.WorkID, workid);
		ps.Add(TrackAttr.FID, fid);
		ps.Add(TrackAttr.EmpFrom, WebUser.getNo(), false);
		DBAccess.RunSQL(ps);

		//如果没有更新到，就写入.
		WriteTrack(flowNo, currNodeID, nodeName, workid, fid, msg, ActionType.WorkCheck, tag, null, writeImg, optionName, null, null, null, null, null, fwcView,null);

	}
	/**
	 写入日志组件

	 @param flowNo 流程编号
	 @param nodeFrom
	 @param workid
	 @param fid
	 @param msg
	 @param optionName
	*/
	public static void WriteTrackDailyLog(String flowNo, int nodeFrom, String nodeFromName, long workid, long fid, String msg, String optionName) throws Exception {
		String dbStr = SystemConfig.getAppCenterDBVarStr();
		String today = DataType.getCurrentDate();

		Paras ps = new Paras();
		ps.SQL = "UPDATE  ND" + Integer.parseInt(flowNo) + "Track SET Msg=" + dbStr + "Msg WHERE  RDT LIKE '" + today + "%' AND WorkID=" + dbStr + "WorkID  AND NDFrom=" + dbStr + "NDFrom AND EmpFrom=" + dbStr + "EmpFrom AND ActionType=" + ActionType.WorkCheck.getValue();
		ps.Add(TrackAttr.Msg, msg, false);
		ps.Add(TrackAttr.WorkID, workid);
		ps.Add(TrackAttr.NDFrom, nodeFrom);
		ps.Add(TrackAttr.EmpFrom, WebUser.getNo(), false);
		if (DBAccess.RunSQL(ps) == 0)
		{
			//如果没有更新到，就写入.
			WriteTrack(flowNo, nodeFrom, nodeFromName, workid, fid, msg, ActionType.WorkCheck, null, null, optionName);
		}
	}

	/// <summary>
	/// 公文写入轨迹
	/// </summary>
	/// <param name="flowNo">流程编号</param>
	/// <param name="nodeFrom">节点ID</param>
	/// <param name="nodeFromName">节点名称</param>
	/// <param name="workid">工作ID</param>
	/// <param name="fid">FID</param>
	/// <param name="msg">消息</param>
	public static void WriteTrackGovDoc(String flowNo, int nodeFrom, String nodeFromName, long workid, long fid, String msg, byte[] bytes) throws Exception {
		String val = WriteTrack(flowNo, nodeFrom, nodeFromName, workid, fid, msg, ActionType.GovDoc, null, null, "无");
		//保存文件到数据库.
		try{
			DBAccess.SaveBytesToDB(bytes,  "ND" + Integer.parseInt(flowNo)+ "Track", "MyPK", val, "DBFile");
		}catch(Exception ex){
			Log.DebugWriteError(ex);
		}
	}
	/**
	 写入周报组件 一旦写入数据,只可更新   每周一次 qin

	 @param flowNo 流程编号
	 @param nodeFrom
	 @param workid
	 @param fid
	 @param msg
	 @param optionName
	*/
	public static void WriteTrackWeekLog(String flowNo, int nodeFrom, String nodeFromName, long workid, long fid, String msg, String optionName) throws Exception {
		String dbStr = SystemConfig.getAppCenterDBVarStr();

		Date dTime = new Date();
		Date startWeek = DateUtils.addDay(dTime,
				(1 - Integer.parseInt(String.format("%d", DateUtils.dayForWeek(dTime))))); // 本周第一天

		Hashtable ht = new Hashtable(); //当前日期所属的week包含哪些日期
		for (int i = 1; i < 7; i++)
		{
			ht.put(i + 1, DateUtils.parse(DateUtils.addDay(startWeek, i), "yyyy-MM-dd"));
		}
		ht.put(1, DateUtils.parse(startWeek, "yyyy-MM-dd"));

		boolean isExitWeek = false; //本周是否已经有插入数据
		String insertDate = null;
		DataTable dt;
		String sql = null;

		for (Object de : ht.entrySet()) {
			sql = "SELECT * FROM ND" + Integer.parseInt(flowNo) + "Track  WHERE  RDT LIKE '" + ht.get(de).toString()
					+ "%' AND WorkID=" + workid + "  AND NDFrom='" + nodeFrom + "' AND EmpFrom='" + WebUser.getNo()
					+ "' AND ActionType=" + ActionType.WorkCheck.getValue();
			if (DBAccess.RunSQLReturnCOUNT(sql) != 0) {
				isExitWeek = true;
				insertDate = ht.get(de).toString();
				break;
			}
		}

		//如果本周已经插入了记录，那么更新
		if (isExitWeek)
		{
			Paras ps = new Paras();
			ps.SQL = "UPDATE  ND" + Integer.parseInt(flowNo) + "Track SET RDT='" + DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss") + "',Msg=" + dbStr + "Msg WHERE  RDT LIKE '" + insertDate + "%' AND WorkID=" + dbStr + "WorkID  AND NDFrom=" + dbStr + "NDFrom AND EmpFrom=" + dbStr + "EmpFrom AND ActionType=" + ActionType.WorkCheck.getValue();
			ps.Add(TrackAttr.Msg, msg, false);
			ps.Add(TrackAttr.WorkID, workid);
			ps.Add(TrackAttr.NDFrom, nodeFrom);
			ps.Add(TrackAttr.EmpFrom, WebUser.getNo(), false);

			DBAccess.RunSQL(ps);
		}
		else
		{
			WriteTrack(flowNo, nodeFrom, nodeFromName, workid, fid, msg, ActionType.WorkCheck, null, null, optionName);
		}
	}
	/**
	 写入月报组件  同周报一样每月一条记录 qin

	 @param flowNo 流程编号
	 @param nodeFrom
	 @param workid
	 @param fid
	 @param msg
	 @param optionName
	*/
	public static void WriteTrackMonthLog(String flowNo, int nodeFrom, String nodeFromName, long workid, long fid, String msg, String optionName) throws Exception {
		String dbStr = SystemConfig.getAppCenterDBVarStr();
		String today = DataType.getCurrentDate();

		Date dTime = new Date();
		Date startDay = DateUtils.addDay(dTime, 1 - DateUtils.getDayOfMonth(dTime)); // 本月第一天

		int days = DateUtils.getDayOfMonth(startDay);
		Hashtable ht = new Hashtable();

		for (int i = 1; i < days; i++) {
			ht.put(i + 1, DateUtils.parse(DateUtils.addDay(startDay, i), "yyyy-MM-dd"));
		}
		ht.put(1, DateUtils.parse(startDay, "yyyy-MM-dd"));

		boolean isExitMonth = false; //本月是否已经有插入数据
		String insertDate = null;
		DataTable dt;
		String sql = null;

		for (Object de : ht.entrySet()) {
			sql = "SELECT * FROM ND" + Integer.parseInt(flowNo) + "Track  WHERE  RDT LIKE '" + ht.get(de).toString()
					+ "%' AND WorkID=" + workid + "  AND NDFrom='" + nodeFrom + "' AND EmpFrom='" + WebUser.getNo()
					+ "' AND ActionType=" + ActionType.WorkCheck.getValue();

			if (DBAccess.RunSQLReturnCOUNT(sql) != 0) {
				isExitMonth = true;
				insertDate = ht.get(de).toString();
				break;
			}
		}

		if (isExitMonth) {
			Paras ps = new Paras();
			ps.SQL = "UPDATE  ND" + Integer.parseInt(flowNo) + "Track SET RDT='"
					+ DateUtils.parse(new Date(), "yyyy-MM-dd HH:mm:ss") + "' Msg=" + dbStr + "Msg WHERE  RDT LIKE '"
					+ insertDate + "%' AND WorkID=" + dbStr + "WorkID  AND NDFrom=" + dbStr + "NDFrom AND EmpFrom="
					+ dbStr + "EmpFrom AND ActionType=" + ActionType.WorkCheck.getValue();
			ps.Add(TrackAttr.Msg, msg);
			ps.Add(TrackAttr.WorkID, workid);
			ps.Add(TrackAttr.NDFrom, nodeFrom);
			ps.Add(TrackAttr.EmpFrom, WebUser.getNo());

			DBAccess.RunSQL(ps);
		} else {
			WriteTrack(flowNo, nodeFrom, nodeFromName, workid, fid, msg, ActionType.WorkCheck, null, null,null, optionName);
		}
	}
	/**
	 修改审核信息标识
	 比如：在默认的情况下是"审核”，现在要把ActionTypeText 修改成"组长审核。"。

	 @param flowNo 流程编号
	 @param workid 工作ID
	 @param nodeFrom 节点ID
	 @param label 要修改成的标签
	 @return 是否成功
	*/
	public static boolean WriteTrackWorkCheckLabel(String flowNo, long workid, int nodeFrom, String label)
	{
		String table = "ND" + Integer.parseInt(flowNo) + "Track";
		String sql = "SELECT MyPK FROM " + table + " WHERE NDFrom=" + nodeFrom + " AND WorkID=" + workid + " And NDTo='0' ORDER BY RDT DESC ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			return false;
		}

		String pk = dt.Rows.get(0).getValue(0).toString();
		sql = "UPDATE " + table + " SET " + TrackAttr.ActionTypeText + "='" + label + "' WHERE MyPK=" + pk;
		DBAccess.RunSQL(sql);
		return true;
	}

	/**
	 前进,获取等标签
	 比如：在默认的情况下是"逻辑删除”，现在要把ActionTypeText 修改成"删除(清场)。"。

	 @param flowNo 流程编号
	 @param workid 工作ID
	 @param nodeFrom 节点ID
	 @param label 要修改成的标签
	 @return 是否成功
	*/
	public static boolean WriteTrackLabel(String flowNo, long workid, int nodeFrom, String label)
	{
		String table = "ND" + Integer.parseInt(flowNo) + "Track";
		String sql = "SELECT MyPK FROM " + table + " WHERE NDFrom=" + nodeFrom + " AND WorkID=" + workid + "  ORDER BY RDT DESC ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			return false;
		}

		String pk = dt.Rows.get(0).getValue(0).toString();
		sql = "UPDATE " + table + " SET " + TrackAttr.ActionTypeText + "='" + label + "' WHERE MyPK=" + pk;
		DBAccess.RunSQL(sql);
		return true;
	}
	/**
	 获取Track 表中的审核的信息

	 @param flowNo
	 @param workId
	 @param nodeFrom
	 @return
	*/

	public static String GetCheckInfo(String flowNo, long workId, int nodeFrom)
	{
		return GetCheckInfo(flowNo, workId, nodeFrom, null);
	}

	public static String GetCheckInfo(String flowNo, long workId, int nodeFrom, String isNullAsVal)
	{
		String table = "ND" + Integer.parseInt(flowNo) + "Track";
		String sql = "SELECT Msg FROM " + table + " WHERE NDFrom=" + nodeFrom + " AND ActionType=" + ActionType.WorkCheck.getValue() + " AND EmpFrom='" + WebUser.getNo() + "' AND WorkID=" + workId + " ORDER BY RDT DESC ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			if (isNullAsVal == null)
			{
				return bp.wf.Glo.getDefValWFNodeFWCDefInfo();
			}
			else
			{
				return isNullAsVal;
			}
		}
		String checkinfo = dt.Rows.get(0).getValue(0).toString();
		if (DataType.IsNullOrEmpty(checkinfo))
		{
			if (isNullAsVal == null)
			{
				return bp.wf.Glo.getDefValWFNodeFWCDefInfo();
			}
			else
			{
				return isNullAsVal;
			}
		}

		return checkinfo;
	}
	/**
	 获取指定Track表根据WorkID,发送节点,发送人获取Tag信息

	 @param flowNo 流程编号
	 @param workId 流程实例WorkID
	 @param nodeFrom 发送节点ID
	 @param empFrom 发送人员编号
	 @return
	*/
	public static String GetCheckTag(String flowNo, long workId, int nodeFrom, String empFrom)
	{
		String table = "ND" + Integer.parseInt(flowNo) + "Track";
		String sql = "SELECT Tag FROM " + table + " WHERE NDFrom=" + nodeFrom + " AND ActionType=" + ActionType.WorkCheck.getValue() + " AND EmpFrom='" + empFrom + "' AND WorkID=" + workId + " ORDER BY RDT DESC ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			return "";
		}
		String checkinfo = dt.Rows.get(0).getValue(0).toString();
		if (DataType.IsNullOrEmpty(checkinfo))
		{
			return "";
		}

		return checkinfo;
	}
	public static String GetCheckRDT(String flowNo, long workId, int nodeFrom, String empFrom)
	{
		String table = "ND" + Integer.parseInt(flowNo) + "Track";
		String sql = "SELECT RDT FROM " + table + " WHERE NDFrom=" + nodeFrom + " AND ActionType=" + ActionType.WorkCheck.getValue() + " AND EmpFrom='" + empFrom + "' AND WorkID=" + workId + " ORDER BY RDT DESC ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			return "";
		}
		String checkinfo = dt.Rows.get(0).getValue(0).toString();
		if (DataType.IsNullOrEmpty(checkinfo))
		{
			return "";
		}

		return checkinfo;
	}
	/**
	 获取队列节点Track 表中的审核的信息(队列节点中处理人 共享同一处理意见)

	 @param flowNo
	 @param workId
	 @param nodeFrom
	 @return
	*/
	public static String GetOrderCheckInfo(String flowNo, long workId, int nodeFrom)
	{
		String table = "ND" + Integer.parseInt(flowNo) + "Track";
		String sql = "SELECT Msg FROM " + table + " WHERE NDFrom=" + nodeFrom + " AND ActionType=" + ActionType.WorkCheck.getValue() + " AND WorkID=" + workId + " ORDER BY RDT DESC ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			//BP.Sys.FrmWorkCheck fwc = new FrmWorkCheck(nodeFrom);
			//return fwc.FWCDefInfo;
			return null;
		}
		String checkinfo = dt.Rows.get(0).getValue(0).toString();
		return checkinfo;
	}
	/**
	 删除审核信息,用于退回后.

	 @param flowNo 流程编号
	 @param workId 工作ID
	 @param nodeFrom 节点从
	 @return 删除自己的审核信息
	*/
	public static void DeleteCheckInfo(String flowNo, long workId, int nodeFrom)
	{
		String table = "ND" + Integer.parseInt(flowNo) + "Track";
		String sql = "DELETE FROM " + table + " WHERE NDFrom=" + nodeFrom + " AND ActionType=" + ActionType.WorkCheck.getValue() + " AND EmpFrom='" + WebUser.getNo() + "' AND WorkID=" + workId;
		DBAccess.RunSQL(sql);
	}
	/**
	 获取指定Track表根据WorkID,发送节点获取请求加签的Msg信息

	 @param flowNo 流程编号
	 @param workId 流程实例WorkID
	 @param nodeFrom 发送节点ID
	 @return
	*/
	public static String GetAskForHelpReInfo(String flowNo, long workId, int nodeFrom)
	{
		String table = "ND" + Integer.parseInt(flowNo) + "Track";
		String sql = "SELECT Msg FROM " + table + " WHERE NDFrom=" + nodeFrom + " AND ActionType=" + ActionType.AskforHelp.getValue() + " AND EmpFrom='" + WebUser.getNo() + "' AND WorkID=" + workId + " ORDER BY RDT DESC ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			return "";
		}

		String checkinfo = dt.Rows.get(0).getValue(0).toString();
		return checkinfo;
	}

	/**
	 更新Track信息

	 @param flowNo
	 @param workId
	 @param nodeFrom
	 @param actionType
	 @param strMsg
	 @return
	*/
	public static void SetTrackInfo(String flowNo, long workId, int nodeFrom, int actionType, String strMsg)
	{
		String table = "ND" + Integer.parseInt(flowNo) + "Track";

		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "UPDATE " + table + " SET Msg=" + dbstr + "Msg  WHERE ActionType=" + dbstr + "ActionType and WorkID=" + dbstr + "WorkID and NDFrom=" + dbstr + "NDFrom";
		ps.Add("Msg", strMsg, false);
		ps.Add("ActionType", actionType);
		ps.Add("WorkID", workId);
		ps.Add("NDFrom", nodeFrom);
		DBAccess.RunSQL(ps);
	}

	/**
	 设置BillNo信息

	 @param flowNo
	 @param workID
	 @param newBillNo
	*/
	public static void SetBillNo(String flowNo, long workID, String newBillNo) throws Exception {
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkFlow SET BillNo=" + dbstr + "BillNo  WHERE WorkID=" + dbstr + "WorkID";
		ps.Add("BillNo", newBillNo, false);
		ps.Add("WorkID", workID);
		DBAccess.RunSQL(ps);

		Flow fl = new Flow(flowNo);
		ps = new Paras();
		ps.SQL = "UPDATE " + fl.getPTable() + " SET BillNo=" + dbstr + "BillNo WHERE OID=" + dbstr + "OID";
		ps.Add("BillNo", newBillNo, false);
		ps.Add("OID", workID);
		DBAccess.RunSQL(ps);
	}

	/**
	 设置父流程信息

	 @param subFlowNo 子流程编号
	 @param subFlowWorkID 子流程workid
	 @param parentWorkID 父流程WorkID
c	*/

	public static void SetParentInfo(String subFlowNo, long subFlowWorkID, long parentWorkID, String parentEmpNo, int parentNodeID) throws Exception {
		SetParentInfo(subFlowNo, subFlowWorkID, parentWorkID, parentEmpNo, parentNodeID, false);
	}

	public static void SetParentInfo(String subFlowNo, long subFlowWorkID, long parentWorkID, String parentEmpNo) throws Exception {
		SetParentInfo(subFlowNo, subFlowWorkID, parentWorkID, parentEmpNo, 0, false);
	}

	public static void SetParentInfo(String subFlowNo, long subFlowWorkID, long parentWorkID) throws Exception {
		SetParentInfo(subFlowNo, subFlowWorkID, parentWorkID, null, 0, false);
	}

	public static void SetParentInfo(String subFlowNo, long subFlowWorkID, long parentWorkID, String parentEmpNo, int parentNodeID, boolean isCopyParentNo) throws Exception {
		//创建父流程.
		GenerWorkFlow pgwf = new GenerWorkFlow(parentWorkID);

		if (parentNodeID != 0)
		{
			pgwf.setNodeID(parentNodeID);
		}

		if (parentEmpNo == null)
		{
			parentEmpNo = WebUser.getNo();
		}

		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkFlow SET PFlowNo=" + dbstr + "PFlowNo, PWorkID=" + dbstr + "PWorkID,PNodeID=" + dbstr + "PNodeID,PEmp=" + dbstr + "PEmp WHERE WorkID=" + dbstr + "WorkID";
		ps.Add(GenerWorkFlowAttr.PFlowNo, pgwf.getFlowNo(), false);
		ps.Add(GenerWorkFlowAttr.PWorkID, parentWorkID);
		ps.Add(GenerWorkFlowAttr.PNodeID, pgwf.getNodeID());
		ps.Add(GenerWorkFlowAttr.PEmp, parentEmpNo, false);
		ps.Add(GenerWorkFlowAttr.WorkID, subFlowWorkID);

		DBAccess.RunSQL(ps);

		Flow fl = new Flow(subFlowNo);
		ps = new Paras();
		ps.SQL = "UPDATE " + fl.getPTable() + " SET PFlowNo=" + dbstr + "PFlowNo, PWorkID=" + dbstr + "PWorkID,PNodeID=" + dbstr + "PNodeID, PEmp=" + dbstr + "PEmp WHERE OID=" + dbstr + "OID";
		ps.Add(GERptAttr.PFlowNo, pgwf.getFlowNo(), false);
		ps.Add(GERptAttr.PWorkID, pgwf.getWorkID());
		ps.Add(GERptAttr.PNodeID, pgwf.getNodeID());
		ps.Add(GERptAttr.PEmp, parentEmpNo, false);
		ps.Add(GERptAttr.OID, subFlowWorkID);
		DBAccess.RunSQL(ps);

		//把数据copy过来.
		if (isCopyParentNo == true)
		{
			Flow_CopyDataFromSpecWorkID(subFlowWorkID, pgwf.getWorkID());
		}
	}

	/**
	 复制数据从指定的WorkID里

	 @param workID 当前workID
	 @param fromWorkID
	*/
	public static void Flow_CopyDataFromSpecWorkID(long workID, long fromWorkID) throws Exception {

			///#region  当前数据对象.
		Emp emp = new Emp(WebUser.getNo());
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		Node nd = new Node(gwf.getNodeID());
		Work wk = nd.getHisWork();
		wk.setOID(workID);
		wk.Retrieve();
		Flow flow = nd.getHisFlow();
		GERpt rpt = flow.getHisGERpt();
		rpt.setOID(workID);
		rpt.Retrieve();

			///#endregion  当前数据对象.


			///#region  数据源对象.
		GenerWorkFlow gwfFrom = new GenerWorkFlow(fromWorkID);
		Flow pFlow = new Flow(gwfFrom.getFlowNo());

		String sql = "SELECT * FROM " + pFlow.getPTable() + " WHERE OID=" + fromWorkID;
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() != 1)
		{
			throw new RuntimeException("@不应该查询不到父流程的数据[" + sql + "], 可能的情况之一,请确认该父流程的调用节点是子线程，但是没有把子线程的FID参数传递进来。");
		}

			///#endregion  数据源对象.

		wk.Copy(dt.Rows.get(0));
		rpt.Copy(dt.Rows.get(0));
		//设置单号为空.
		wk.SetValByKey("BillNo", "");
		rpt.setBillNo("");


			///#region 从调用的节点上copy.
		bp.wf.Node fromNd = new bp.wf.Node(gwfFrom.getNodeID());
		Work wkFrom = fromNd.getHisWork();
		wkFrom.setOID(fromWorkID);
		if (wkFrom.RetrieveFromDBSources() == 0)
		{
			throw new RuntimeException("@父流程的工作ID不正确，没有查询到数据" + fromWorkID);
		}
		//wk.Copy(wkFrom);
		//rpt.Copy(wkFrom);

			///#endregion 从调用的节点上copy.




			///#region 特殊赋值.
		/*如果不是 执行的从已经完成的流程copy.*/
		wk.SetValByKey(GERptAttr.PFlowNo, gwfFrom.getFlowNo());
		wk.SetValByKey(GERptAttr.PNodeID, gwfFrom.getNodeID());
		wk.SetValByKey(GERptAttr.PWorkID, gwfFrom.getWorkID());


		//忘记了增加这句话.
		rpt.SetValByKey(GERptAttr.PEmp, WebUser.getNo());

		rpt.SetValByKey(GERptAttr.FID, 0);
		rpt.SetValByKey(GERptAttr.FlowStartRDT, DataType.getCurrentDateTime());
		rpt.SetValByKey(GERptAttr.FlowEnderRDT, DataType.getCurrentDateTime());
		rpt.SetValByKey(GERptAttr.WFState, WFState.Blank.getValue());
		rpt.SetValByKey(GERptAttr.FlowStarter, emp.getUserID());
		rpt.SetValByKey(GERptAttr.FlowEnder, emp.getUserID());
		rpt.SetValByKey(GERptAttr.FlowEndNode, gwf.getNodeID());
		rpt.SetValByKey(GERptAttr.FK_Dept, emp.getDeptNo());
		rpt.SetValByKey(GERptAttr.FK_NY, DataType.getCurrentYearMonth());

		if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserNameOnly)
		{
			rpt.SetValByKey(GERptAttr.FlowEmps, "@" + emp.getName() + "@");
		}

		if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserIDUserName)
		{
			rpt.SetValByKey(GERptAttr.FlowEmps, "@" + emp.getUserID() + "@");
		}

		if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserIDUserName)
		{
			rpt.SetValByKey(GERptAttr.FlowEmps, "@" + emp.getUserID() + "," + emp.getName() + "@");
		}


		if (rpt.getEnMap().getPhysicsTable().equals(wk.getEnMap().getPhysicsTable()) == false)
		{
			wk.Update(); //更新工作节点数据.
		}
		rpt.Update(); // 更新流程数据表.

			///#endregion 特殊赋值.


			///#region 复制其他数据..
		//复制明细。
		MapDtls dtls = wk.getHisMapDtls();
		if (!dtls.isEmpty())
		{
			MapDtls dtlsFrom = wkFrom.getHisMapDtls();
			int idx = 0;
			if (dtlsFrom.size() == dtls.size())
			{
				for (MapDtl dtl : dtls.ToJavaList())
				{
					if (dtl.getItIsCopyNDData() == false)
					{
						continue;
					}

					//new 一个实例.
					GEDtl dtlData = new GEDtl(dtl.getNo());

					//删除以前的数据.
					try
					{
						sql = "DELETE FROM " + dtlData.getEnMap().getPhysicsTable() + " WHERE RefPK=" + wk.getOID();
						DBAccess.RunSQL(sql);
					}
					catch (RuntimeException ex)
					{
					}

					MapDtl dtlFrom = dtlsFrom.get(idx) instanceof MapDtl ? (MapDtl)dtlsFrom.get(idx) : null;

					GEDtls dtlsFromData = new GEDtls(dtlFrom.getNo());
					dtlsFromData.Retrieve(GEDtlAttr.RefPK, fromWorkID, null);
					for (GEDtl geDtlFromData : dtlsFromData.ToJavaList())
					{
						//是否启用多附件
						FrmAttachmentDBs dbs = null;
						if (dtl.getItIsEnableAthM() == true)
						{
							//根据从表的OID 获取附件信息
							dbs = new FrmAttachmentDBs();
							dbs.Retrieve(FrmAttachmentDBAttr.RefPKVal, geDtlFromData.getOID(), null);
						}

						dtlData.Copy(geDtlFromData);
						dtlData.setRefPK(String.valueOf(wk.getOID()));
						dtlData.setFID(wk.getOID());
						if (flow.getNo().equals(gwf.getFlowNo()) == false && (flow.getStartLimitRole() == bp.wf.StartLimitRole.OnlyOneSubFlow))
						{
							dtlData.SaveAsOID(geDtlFromData.getOID()); //为子流程的时候，仅仅允许被调用1次.
						}
						else
						{
							dtlData.InsertAsNew();
							if (dbs != null && !dbs.isEmpty())
							{
								//复制附件信息
								FrmAttachmentDB newDB = new FrmAttachmentDB();
								for (FrmAttachmentDB db : dbs.ToJavaList())
								{
									newDB.Copy(db);
									newDB.setRefPKVal(String.valueOf(dtlData.getOID()));
									newDB.setFID(dtlData.getOID());
									newDB.setMyPK(DBAccess.GenerGUID(0, null, null));
									newDB.Insert();
								}
							}
						}
					}
				}
			}
		}

		//复制附件数据。
		if (!wk.getHisFrmAttachments().isEmpty())
		{
			if (!wkFrom.getHisFrmAttachments().isEmpty())
			{
				int toNodeID = wk.getNodeID();

				//删除数据。
				DBAccess.RunSQL("DELETE FROM Sys_FrmAttachmentDB WHERE FK_MapData='ND" + toNodeID + "' AND RefPKVal='" + wk.getOID() + "'");
				FrmAttachmentDBs athDBs = new FrmAttachmentDBs("ND" + gwfFrom.getNodeID(), String.valueOf(gwfFrom.getWorkID()));

				for (FrmAttachmentDB athDB : athDBs.ToJavaList())
				{
					FrmAttachmentDB athDB_N = new FrmAttachmentDB();
					athDB_N.Copy(athDB);
					athDB_N.setFrmID("ND" + toNodeID);
					athDB_N.setRefPKVal( String.valueOf(wk.getOID()));
					athDB_N.setFKFrmAttachment(athDB_N.getFKFrmAttachment().replace("ND" + gwfFrom.getNodeID(), "ND" + toNodeID));

					if (athDB_N.getHisAttachmentUploadType() == AttachmentUploadType.Single)
					{
						/*如果是单附件.*/
						athDB_N.setMyPK(athDB_N.getFKFrmAttachment() + "_" + wk.getOID());
						if (athDB_N.getIsExits())
						{
							continue; //说明上一个节点或者子线程已经copy过了, 但是还有子线程向合流点传递数据的可能，所以不能用break.
						}
						athDB_N.Insert();
					}
					else
					{
						athDB_N.setMyPK(athDB_N.getUploadGUID() + "_" + athDB_N.getFrmID() + "_" + wk.getOID());
						athDB_N.Insert();
					}
				}
			}
		}

			///#endregion 复制表单其他数据.


			///#region 复制独立表单数据.
		//求出来被copy的节点有多少个独立表单.
		FrmNodes fnsFrom = new FrmNodes(fromNd.getNodeID());
		if (fnsFrom.size() != 0)
		{
			//求当前节点表单的绑定的表单.
			FrmNodes fns = new FrmNodes(nd.getNodeID());
			if (fns.size() != 0)
			{
				//开始遍历当前绑定的表单.
				for (FrmNode fn : fns.ToJavaList())
				{
					for (FrmNode fnFrom : fnsFrom.ToJavaList())
					{
						if (!Objects.equals(fn.getFrmID(), fnFrom.getFrmID()))
						{
							continue;
						}

						GEEntityOID geEnFrom = new GEEntityOID(fnFrom.getFrmID());
						geEnFrom.setOID(gwfFrom.getWorkID());
						if (geEnFrom.RetrieveFromDBSources() == 0)
						{
							continue;
						}

						//执行数据copy , 复制到本身.
						geEnFrom.CopyToOID(wk.getOID());
					}
				}
			}
		}

			///#endregion 复制独立表单数据.
	}

	/**
	 获取指定流程Rpt表的流程实例的信息

	 @param flowNo 流程编号
	 @param workID 流程实例WorkID
	 @return
	*/
	public static GERpt Flow_GenerGERpt(String flowNo, long workID) throws Exception {
		GERpt rpt = new GERpt("ND" + Integer.parseInt(flowNo) + "Rpt", workID);
		return rpt;
	}

	/**
	 近期工作
	*/
	public static String Flow_RecentWorkInit()
	{
		/* 近期工作. */
		String sql = "";
		String empNo = WebUser.getNo();
		sql += "SELECT w.*,'' SDT,'' EDT FROM WF_GenerWorkFlow w   WHERE ";
		sql += " (Emps LIKE '%@" + empNo + "@%' OR Emps LIKE '%@" + empNo + ",%' OR Emps LIKE '%," + empNo + "@%')";
		// sql += " AND Starter!='" + empNo + "'"; //不能是我发起的.

		//if (DataType.IsNullOrEmpty(this.FlowNo) == false)
		//    sql += " AND FK_Flow='" + this.FlowNo + "'"; //如果有流程编号,就按他过滤.

		sql += " AND WFState >1 ORDER BY RDT DESC ";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		//添加oracle的处理
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("PRI").ColumnName = "PRI";
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("FID").ColumnName = "FID";
			dt.Columns.get("WFSTATE").ColumnName = "WFState";
			dt.Columns.get("WFSTA").ColumnName = "WFSta";
			dt.Columns.get("WEEKNUM").ColumnName = "WeekNum";
			dt.Columns.get("TSPAN").ColumnName = "TSpan";
			dt.Columns.get("TODOSTA").ColumnName = "TodoSta";
			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dt.Columns.get("TODOEMPSNUM").ColumnName = "TodoEmpsNum";
			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("TASKSTA").ColumnName = "TaskSta";
			dt.Columns.get("SYSTYPE").ColumnName = "SysType";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("SENDER").ColumnName = "Sender";
			dt.Columns.get("SENDDT").ColumnName = "SendDT";
			dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";
			dt.Columns.get("SDTOFFLOW").ColumnName = "SDTOfFlow";
			dt.Columns.get("RDT").ColumnName = "RDT";
			dt.Columns.get("PWORKID").ColumnName = "PWorkID";
			dt.Columns.get("PFLOWNO").ColumnName = "PFlowNo";
			dt.Columns.get("PFID").ColumnName = "PFID";
			dt.Columns.get("PEMP").ColumnName = "PEmp";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";

			dt.Columns.get("GUID").ColumnName = "Guid";
			dt.Columns.get("GUESTNO").ColumnName = "GuestNo";
			dt.Columns.get("GUESTNAME").ColumnName = "GuestName";
			dt.Columns.get("FLOWNOTE").ColumnName = "FlowNote";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("FK_NY").ColumnName = "FK_NY";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FK_DEPT").ColumnName = "FK_Dept";
			dt.Columns.get("EMPS").ColumnName = "Emps";
			dt.Columns.get("doMainExt").ColumnName = "DomainExt";
			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dt.Columns.get("BILLNO").ColumnName = "BillNo";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("pri").ColumnName = "PRI";
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("fid").ColumnName = "FID";
			dt.Columns.get("wfstate").ColumnName = "WFState";
			dt.Columns.get("wfsta").ColumnName = "WFSta";
			dt.Columns.get("weeknum").ColumnName = "WeekNum";
			dt.Columns.get("tspan").ColumnName = "TSpan";
			dt.Columns.get("todosta").ColumnName = "TodoSta";
			dt.Columns.get("deptname").ColumnName = "DeptName";
			dt.Columns.get("todoempsnum").ColumnName = "TodoEmpsNum";
			dt.Columns.get("todoemps").ColumnName = "TodoEmps";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("tasksta").ColumnName = "TaskSta";
			dt.Columns.get("systype").ColumnName = "SysType";
			dt.Columns.get("startername").ColumnName = "StarterName";
			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("sender").ColumnName = "Sender";
			dt.Columns.get("senddt").ColumnName = "SendDT";
			dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";
			dt.Columns.get("sdtofflow").ColumnName = "SDTOfFlow";
			dt.Columns.get("rdt").ColumnName = "RDT";
			dt.Columns.get("pworkid").ColumnName = "PWorkID";
			dt.Columns.get("pflowno").ColumnName = "PFlowNo";
			dt.Columns.get("pfid").ColumnName = "PFID";
			dt.Columns.get("pemp").ColumnName = "PEmp";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("guid").ColumnName = "Guid";
			dt.Columns.get("guestno").ColumnName = "GuestNo";
			dt.Columns.get("guestname").ColumnName = "GuestName";
			dt.Columns.get("flownote").ColumnName = "FlowNote";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("fk_ny").ColumnName = "FK_NY";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			dt.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("fk_dept").ColumnName = "FK_Dept";
			dt.Columns.get("emps").ColumnName = "Emps";
			dt.Columns.get("domainExt").ColumnName = "DomainExt";
			dt.Columns.get("deptname").ColumnName = "DeptName";
			dt.Columns.get("billno").ColumnName = "BillNo";
		}

		for(int j=0;j<dt.Rows.size();j++){
			String WorkID=dt.Rows.get(j).getValue("WorkID").toString();
			String FK_Flow=dt.Rows.get(j).getValue("FK_Flow").toString();
			//workid创建时间
			String createTime=queryWorkIDCreateTime(FK_Flow, Long.valueOf(WorkID));
			dt.Rows.get(j).put("RDT",createTime);
			//节点时间
			DataTable dtrack=queryNodeTrack(FK_Flow,WorkID);
			for(int i=dtrack.Rows.size()-1;i>=0;i--){
				String EmpFrom= dtrack.Rows.get(i).getValue("EmpFrom").toString();
				//只取最后一次的处理时间
				if( EmpFrom.equals(empNo)){
					//发送时间
					String EDT=dtrack.Rows.get(i).getValue("RDT").toString();
					//接收时间
					String SDT=EDT;
					if(i>0){
						SDT=dtrack.Rows.get(i-1).getValue("RDT").toString();
					}else{
						SDT=createTime;
					}
					dt.Rows.get(j).put("SDT",SDT);
					dt.Rows.get(j).put("EDT",EDT);
					break;
				}
			}

		}
		return Json.ToJson(dt);
	}
	//获取流程节点轨迹表数据
	private static DataTable queryNodeTrack(String flowID, String workID){
		String tableName = "ND" + Integer.parseInt(flowID) + "track";
		Paras pens = new Paras();
		//轨迹表
		StringBuilder sql=new StringBuilder("");
//            sql.append(" select ActionType,NDfrom,NDfromT,NdTo,NdToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,'' ReceiveTime from ")
		sql.append(" select NDfrom NodeID,ActionType,ActionTypeText,'' ReceiveTime ,RDT,EmpFrom,EmpFromT,EmpTo,EmpToT from ")
				.append(tableName).append(" where ActionType not in(21,22,26,32) ");  //21:抄送  22:审核 26:跳转  32:信息Route
		sql.append(" and workID =").append(SystemConfig.getAppCenterDBVarStr()).append("v");
		pens.Add("v",workID);
		pens.SQL=sql.toString();
		DataTable dt= DBAccess.RunSQLReturnTable(pens);
		return dt;
	}
	//获取workID 创建时间
	private static String queryWorkIDCreateTime(String flowID, Long workID)  {
		GenerWorkFlow gwf = null;
		try {
			gwf = new GenerWorkFlow(workID);
		} catch (Exception e) {
			return "";
		}
		String atpara=gwf.getRow().get("AtPara").toString();
		//第一个节点创建时间
		String createTime=gwf.getRow().get("RDT").toString();
		if(atpara.contains("@CreateRDT")){
			String [] arr=atpara.split("@");
			for(String str:arr){
				if(str.contains("CreateRDT")){
					createTime=str.split("=")[1];
				}
			}
		}
		return createTime;
	}
	/**
	 产生一个新的工作

	 @param flowNo 流程编号
	 @return 返回当前操作员创建的工作
	*/
	public static Work Flow_GenerWork(String flowNo) throws Exception {
		Flow fl = new Flow(flowNo);
		Work wk = fl.NewWork();
		wk.ResetDefaultVal(null, null, 0);
		return wk;
	}
	/**
	 把流程从非正常运行状态恢复到正常运行状态
	 比如现在的流程的状态是，删除，挂起，现在恢复成正常运行。

	 @param flowNo 流程编号
	 @param workID 工作ID
	 @param msg 原因
	 @return 执行信息
	*/
	public static void Flow_DoComeBackWorkFlow(String flowNo, long workID, String msg) throws Exception {
		WorkFlow wf = new WorkFlow(workID);
		wf.DoComeBackWorkFlow(msg);
	}
	/**
	 恢复已完成的流程数据到指定的节点，如果节点为0就恢复到最后一个完成的节点上去.
	 恢复失败抛出异常

	 @param flowNo 要恢复的流程编号
	 @param workid 要恢复的workid
	 @param backToNodeID 恢复到的节点编号，如果是0，标示回复到流程最后一个节点上去.
	 @param note 恢复的原因，此原因会记录到日志.
	*/
	public static String Flow_DoRebackWorkFlow(String flowNo, long workid, int backToNodeID, String note) throws Exception {
		return Flow_DoRebackWorkFlow(flowNo,workid,backToNodeID,note,"");
	}
	public static String Flow_DoRebackWorkFlow(String flowNo, long workid, int backToNodeID, String note, String empNo) throws Exception {
		FlowSheet fs = new FlowSheet(flowNo);
		return fs.DoRebackFlowData(workid, backToNodeID, note,empNo);
	}
	/**
	 执行删除流程:彻底的删除流程.
	 清除的内容如下:
	 1, 流程引擎中的数据.
	 2, 节点数据,NDxxRpt数据.
	 3, 轨迹表数据.

	 @param workID 工作ID
	 @return 执行信息
	*/

	public static String Flow_DoDeleteFlowByReal(long workID)
	{
		return Flow_DoDeleteFlowByReal(workID, false);
	}

	public static String Flow_DoDeleteFlowByReal(long workID, boolean isDelSubFlow)
	{
		//if (WebUser.getIsAdmin()==false)
		//    throw
		try
		{
			WorkFlow.DeleteFlowByReal(workID, isDelSubFlow);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw new RuntimeException("err@删除前错误，" + ex.getStackTrace());
		}
		return "删除成功";
	}
	/**
	 删除指定流程实例的草稿信息

	 @param flowNo 流程编号
	 @param workID 流程实例
	 @param isDelSubFlow 是否删除子流程
	 @return
	*/
	public static String Flow_DoDeleteDraft(String flowNo, long workID, boolean isDelSubFlow) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workID);
		gwf.RetrieveFromDBSources();
		if (!Objects.equals(gwf.getStarter(), WebUser.getNo()) && WebUser.getIsAdmin() == false)
		{
			return "err@流程不是您发起的，或者您不是管理员所以您不能删除该草稿。";
		}

		//删除流程。
		try {
			gwf.Delete();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		String dbstr = SystemConfig.getAppCenterDBVarStr();

		Paras ps = new Paras();

		Flow fl = new Flow(flowNo);
		ps = new Paras();
		ps.SQL = "DELETE FROM " + fl.getPTable() + " WHERE OID=" + dbstr + "OID";
		ps.Add("OID", workID);
		DBAccess.RunSQL(ps);

		//删除开始节点数据.
		Node nd = fl.getHisStartNode();
		Work wk = nd.getHisWork();
		ps = new Paras();
		ps.SQL = "DELETE FROM " + wk.getEnMap().getPhysicsTable() + " WHERE OID=" + dbstr + "OID";
		ps.Add("OID", workID);
		DBAccess.RunSQL(ps);

		Log.DebugWriteError(WebUser.getName() + "删除了FlowNo 为'" + flowNo + "',workID为'" + workID + "'的数据");

		return "草稿删除成功";
	}
	/**
	 删除已经完成的流程
	 注意:它不触发事件.

	 @param flowNo 流程编号
	 @param workID 工作ID
	 @param isDelSubFlow 是否删除子流程
	 @param note 删除原因
	 @return 删除过程信息
	*/
	public static String Flow_DoDeleteWorkFlowAlreadyComplete(String flowNo, long workID, boolean isDelSubFlow, String note) throws Exception {
		return WorkFlow.DoDeleteWorkFlowAlreadyComplete(flowNo, workID, isDelSubFlow, note);
	}
	/**
	 删除流程并写入日志
	 清除的内容如下:
	 1, 流程引擎中的数据.
	 2, 节点数据,NDxxRpt数据.
	 并作如下处理:
	 1, 保留track数据.
	 2, 写入流程删除记录表.

	 @param flowNo 流程编号
	 @param workID 工作ID
	 @param deleteNote 删除原因
	 @param isDelSubFlow 是否要删除它的子流程
	 @return 执行信息
	*/
	public static String Flow_DoDeleteFlowByWriteLog(String flowNo, long workID, String deleteNote, boolean isDelSubFlow) throws Exception {
		WorkFlow wf = new WorkFlow(workID);
		return wf.DoDeleteWorkFlowByWriteLog(deleteNote, isDelSubFlow);
	}
	/**
	 执行逻辑删除流程:此流程并非真正的删除仅做了流程删除标记
	 比如:逻辑删除工单.
	 @param workID 工作ID
	 @param msg 逻辑删除的原因
	 @param isDelSubFlow 逻辑删除的原因
	 @return 执行信息,执行不成功抛出异常.
	*/
	public static String Flow_DoDeleteFlowByFlag(long workID, String msg, boolean isDelSubFlow) throws Exception {
		WorkFlow wf = new WorkFlow(workID);
		wf.DoDeleteWorkFlowByFlag(msg);
		if (isDelSubFlow)
		{
			//删除子线程
			GenerWorkFlows gwfs = new GenerWorkFlows();
			gwfs.Retrieve(GenerWorkFlowAttr.FID, workID, null);
			for (GenerWorkFlow item : gwfs.ToJavaList())
			{
				Flow_DoDeleteFlowByFlag(item.getWorkID(), "删除子流程:" + msg, false);
			}
			//删除子流程
			gwfs = new GenerWorkFlows();
			gwfs.Retrieve(GenerWorkFlowAttr.PWorkID, workID, null);
			for (GenerWorkFlow item : gwfs.ToJavaList())
			{
				Flow_DoDeleteFlowByFlag(item.getWorkID(), "删除子流程:" + msg, false);
			}
		}
		return "删除成功";
	}
	/**
	 撤销删除流程
	 说明:如果一个流程处于逻辑删除状态,要回复正常运行状态,就执行此接口.

	 @param flowNo 流程编号
	 @param workID 工作流程ID
	 @param msg 撤销删除的原因
	 @return 执行消息,如果撤销不成功则抛出异常.
	*/
	public static String Flow_DoUnDeleteFlowByFlag(String flowNo, long workID, String msg) throws Exception {
		WorkFlow wf = new WorkFlow(workID);
		wf.DoUnDeleteWorkFlowByFlag(msg);
		return "撤销删除成功.";
	}
	/**
	 执行-撤销发送
	 说明:如果流程转入了下一个节点,就会执行失败,就会抛出异常.

	 @param flowNo 流程编号
	 @param workID 工作ID
	 @return 返回成功执行信息
	*/

	public static String Flow_DoUnSend(String flowNo, long workID, int unSendToNode) throws Exception {
		return Flow_DoUnSend(flowNo, workID, unSendToNode, 0);
	}

	public static String Flow_DoUnSend(String flowNo, long workID) throws Exception {
		return Flow_DoUnSend(flowNo, workID, 0, 0);
	}

	public static String Flow_DoUnSend(String flowNo, long workID, int unSendToNode, long fid) throws Exception {
		if (unSendToNode == 0)
		{
			//获取用户当前所在的节点
			String currNode = "";
			switch (DBAccess.getAppCenterDBType())
			{
				case Oracle:
				case KingBaseR3:
				case KingBaseR6:
				case GBASE8CByOracle:
					currNode = "SELECT FK_Node FROM (SELECT  FK_Node FROM WF_GenerWorkerlist WHERE FK_Emp='" + WebUser.getNo() + "' Order by RDT DESC ) WHERE rownum=1";
					break;
				case MySQL:
				case PostgreSQL:
				case UX:
				case HGDB:
				case GBASE8CByMySQL:
				case GBASE8A:
					currNode = "SELECT  FK_Node FROM WF_GenerWorkerlist WHERE FK_Emp='" + WebUser.getNo() + "' Order by RDT DESC LIMIT 1";
					break;
				case MSSQL:
					currNode = "SELECT TOP 1 FK_Node FROM WF_GenerWorkerlist WHERE FK_Emp='" + WebUser.getNo() + "' Order by RDT DESC";
					break;
				default:
					currNode = "SELECT  FK_Node FROM WF_GenerWorkerlist WHERE FK_Emp='" + WebUser.getNo() + "' Order by RDT DESC";
					break;
			}
			unSendToNode = DBAccess.RunSQLReturnValInt(currNode, 0);
		}

		WorkUnSend unSend = new WorkUnSend(flowNo, workID, unSendToNode, fid);

		return unSend.DoUnSend();
	}

	/**
	 获得当前节点上一步发送日志记录

	 @param WorkID 工作流程ID
	 @param FK_Node 当前节点编号
	 @return 上一节点发送记录
	*/
	public static DataTable Flow_GetPreviousNodeTrack(long WorkID, int FK_Node) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(WorkID);
		if (gwf.RetrieveFromDBSources() == 0)
		{
			throw new RuntimeException("err@没有查询到相关业务实例");
		}

		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras pas = new Paras();
		switch (SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
				pas.SQL = "SELECT TOP 1 MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT,NDTo,NDToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Tag,Exer FROM ND" + Integer.parseInt(gwf.getFlowNo()) + "Track WHERE WorkID=" + dbstr + "WorkID  AND NDTo=" + dbstr + "NDTo AND (ActionType=1 OR ActionType=" + ActionType.Skip.getValue() + ") ORDER BY RDT DESC";
				break;
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				pas.SQL = "SELECT MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT,NDTo,NDToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Tag,Exer FROM ND" + Integer.parseInt(gwf.getFlowNo()) + "Track  WHERE WorkID=" + dbstr + "WorkID  AND NDTo=" + dbstr + "NDTo AND (ActionType=1 OR ActionType=" + ActionType.Skip.getValue() + ") AND ROWNUM=1 ORDER BY RDT DESC ";
				break;
			case MySQL:
			case GBASE8CByMySQL:
			case GBASE8A:
				pas.SQL = "SELECT MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT,NDTo,NDToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Tag,Exer FROM ND" + Integer.parseInt(gwf.getFlowNo()) + "Track  WHERE WorkID=" + dbstr + "WorkID AND NDTo=" + dbstr + "NDTo AND (ActionType=1 OR ActionType=" + ActionType.Skip.getValue() + ") ORDER BY RDT DESC limit 0,1 ";
				break;
			case PostgreSQL:
			case UX:
			case HGDB:
				pas.SQL = "SELECT MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT,NDTo,NDToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Tag,Exer FROM ND" + Integer.parseInt(gwf.getFlowNo()) + "Track  WHERE WorkID=" + dbstr + "WorkID AND NDTo=" + dbstr + "NDTo AND (ActionType=1 OR ActionType=" + ActionType.Skip.getValue() + ") ORDER BY RDT DESC limit 1 ";
				break;
			default:
				break;
		}
		pas.Add("WorkID", WorkID);
		pas.Add("NDTo", FK_Node);
		return DBAccess.RunSQLReturnTable(pas);
	}

	/**
	 执行冻结
	 @param workid 工作ID
	 @param isFixSubFlows 是否冻结子流程？
	 @param msg 冻结原因.
	 @return 冻结的信息.
	*/
	public static String Flow_DoFix(long workid, boolean isFixSubFlows, String msg) throws Exception {
		String info = "";
		try
		{
			// 执行冻结.
			WorkFlow wf = new WorkFlow(workid);
			info = wf.DoFix(msg);
		}
		catch (RuntimeException ex)
		{
			info += ex.getMessage();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (isFixSubFlows == false)
		{
			return info;
		}

		GenerWorkFlows gwfs = new GenerWorkFlows();
		gwfs.Retrieve(GenerWorkFlowAttr.PWorkID, workid, null);

		for (GenerWorkFlow item : gwfs.ToJavaList())
		{
			try
			{
				// 执行冻结.
				WorkFlow wf = new WorkFlow(item.getWorkID());
				info += wf.DoFix(msg);
				GenerWorkFlows subgwfs = new GenerWorkFlows();
				subgwfs.Retrieve(GenerWorkFlowAttr.PWorkID, item.getWorkID(), null);
				for (GenerWorkFlow subitem : subgwfs.ToJavaList())
				{
					try
					{
						// 执行冻结.
						WorkFlow subwf = new WorkFlow(subitem.getWorkID());
						info += subwf.DoFix(msg);
					}
					catch (RuntimeException ex)
					{
						info += "err@" + ex.getMessage();
					}
				}
			}
			catch (RuntimeException ex)
			{
				info += "err@" + ex.getMessage();
			}
		}

		return info;
	}
	/**
	 执行解除冻结
	 于挂起的区别是,冻结需要有权限的人才可以执行解除冻结，
	 挂起是自己的工作可以挂起也可以解除挂起。
	 @param workid workid
	 @param msg 解除原因
	*/
	public static String Flow_DoUnFix(long workid, String msg) throws Exception {
		// 执行冻结.
		WorkFlow wf = new WorkFlow(workid);
		return wf.DoUnFix(msg);
	}
	/**
	 执行流程结束
	 说明:正常的流程结束.
	 @param workID 工作ID
	 @param msg 流程结束原因
	 @return 返回成功执行信息
	*/

	public static String Flow_DoFlowOver(long workID, String msg) throws Exception {
		return Flow_DoFlowOver(workID, msg, WFState.CompleteEnd);
	}

	public static String Flow_DoFlowOver(long workID, String msg, WFState wfState) throws Exception {
		WorkFlow wf = new WorkFlow(workID);
		String flowNo = wf.getHisGenerWorkFlow().getFlowNo();

		Node nd = new Node(wf.getHisGenerWorkFlow().getNodeID());
		GERpt rpt = new GERpt("ND" + Integer.parseInt(flowNo) + "Rpt");
		rpt.setOID(workID);
		rpt.RetrieveFromDBSources();
		//msg = wf.DoFlowOver(ActionType.FlowOver, msg, nd, rpt, stopFlowType);
		msg = wf.DoFlowOver(ActionType.FlowOver, msg, nd, rpt, wfState);
		Work wk = nd.getHisWork();
		wk.setOID(workID);
		wk.RetrieveFromDBSources();
		WorkNode wn = new WorkNode(wk, nd);
		msg += WorkNodePlus.SubFlowEvent(wn); //onjs有可能为null的处理

		return msg;
	}
	/**
	 获得到达节点的集合

	 @param workID workID
	 @return 要到达的节点对象.
	*/
	public static Directions Node_GetNextStepNodesByWorkID(int workID) throws Exception {
		int nodeID = DBAccess.RunSQLReturnValInt("SELECT FK_Node FROM WF_GenerWorkFlow WHERE WorkID=" + workID);
		return Node_GetNextStepNodesByNodeID(nodeID);
	}
	/**
	 获得到达节点的集合.

	 @param nodeID 当前节点ID
	 @return 数据源:Directions
	 @exception Exception 如果当前节点是到达节点是自动计算的，就抛出异常.
	*/
	public static Directions Node_GetNextStepNodesByNodeID(int nodeID) throws Exception {
		Node nd = new Node(nodeID);
		if (nd.getCondModel() == DirCondModel.ByLineCond)
		{
			throw new RuntimeException("err@当前节点转向规则是自动计算，不能获取到达的节点集合.");
		}

		Directions dirs = new Directions();
		dirs.Retrieve(DirectionAttr.Node, nodeID, "Idx");
		return dirs;
	}
	/**
	 获得到指定节点上的工作人员集合
	 @param nodeID 节点ID
	 @return
	 @exception Exception
	*/
	public static DataTable Node_GetNextStepEmpsByNodeID(int nodeID) throws Exception {
		return Node_GetNextStepEmpsByNodeID(nodeID, 0);
	}

	public static DataTable Node_GetNextStepEmpsByNodeID(int nodeID, long workID) throws Exception {
		if (workID == 0)
		{
			workID = DBAccess.RunSQLReturnValInt("SELECT WorkID FROM WF_GenerWorkFlow WHERE FK_Node=" + nodeID + " AND TodoEmps LIKE '%" + WebUser.getNo() + ",%'");
		}

		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		WorkNode toWN = new WorkNode(workID, nodeID);
		try
		{
			Flow fl = new Flow(gwf.getFlowNo());
			WorkNode currWN = new WorkNode(workID, gwf.getNodeID());

			FindWorker fw = new FindWorker();
			DataTable dt = fw.DoIt(fl, currWN, toWN);
			if (dt.Columns.size()== 1)
			{
				dt.Columns.Add("Name");
				for (DataRow dr : dt.Rows)
				{
					dr.setValue("Name", DBAccess.RunSQLReturnString("SELECT Name FROM Port_Emp WHERE No='" + dr.getValue(0).toString() + "'"));
				}
			}
			dt.Columns.get(0).ColumnName = "No";
			dt.Columns.get(1).ColumnName = "Name";

			return dt;
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("err@不支持或者没有判断的模式:" + toWN.getHisNode().getHisDeliveryWay().toString() + "，技术信息:" + ex.getMessage());
		}
	}
	/**
	 获得执行下一步骤的节点ID，这个功能是在流程未发送前可以预先知道
	 它就要到达那一个节点上去,以方便在当前节点发送前处理业务逻辑.
	 1,首先保证当前人员是可以执行当前节点的工作.
	 2,其次保证获取下一个节点只有一个.

	 @param fk_flow 流程编号
	 @param workid 工作ID
	 @return 下一步骤的所要到达的节点, 如果获取不到就会抛出异常.
	*/
	public static int Node_GetNextStepNode(String fk_flow, long workid) throws Exception {
		////检查当前人员是否可以执行当前工作.
		//if (BP.WF.Dev2Interface.Flow_CheckIsCanDoCurrentWork( workid, WebUser.getNo()) == false)
		//    throw new Exception("@当前人员不能执行此节点上的工作.");

		//获取当前nodeID.
		int currNodeID = bp.wf.Dev2Interface.Node_GetCurrentNodeID(fk_flow, workid);

		//获取
		Node nd = new Node(currNodeID);
		Work wk = nd.getHisWork();
		wk.setOID(workid);
		wk.Retrieve();

		WorkNode wn = new WorkNode(wk, nd);
		if (wn.rptGe == null)
		{
			wn.rptGe = nd.getHisFlow().getHisGERpt();
			if (wk.getFID() != 0)
			{
				wn.rptGe.setOID(wk.getFID());
			}
			else
			{
				wn.rptGe.setOID(workid);
			}

			wn.rptGe.RetrieveFromDBSources();
			wk.setRow(wn.rptGe.getRow());
		}
		return wn.NodeSend_GenerNextStepNode().getNodeID();
	}
	/**
	 获取指定的workid 在运行到的节点编号

	 @param workID 需要找到的workid
	 @return 返回节点编号. 如果没有找到，就会抛出异常.
	*/
	public static int Flow_GetCurrentNode(long workID)
	{
		Paras ps = new Paras();
		ps.SQL = "SELECT FK_Node FROM WF_GenerWorkFlow WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID";
		ps.Add("WorkID", workID);
		return DBAccess.RunSQLReturnValInt(ps);
	}
	/**
	 获取指定节点的Work

	 @param nodeID 节点ID
	 @param workID 工作ID
	 @return 当前工作
	*/
	public static Work Flow_GetCurrentWork(int nodeID, long workID) throws Exception {
		Node nd = new Node(nodeID);
		Work wk = nd.getHisWork();
		wk.setOID(workID);
		wk.Retrieve();
		return wk;
	}
	/**
	 获取当前工作节点的Work

	 @param workID 工作ID
	 @return 当前工作节点的Work
	*/
	public static Work Flow_GetCurrentWork(long workID) throws Exception {
		Node nd = new Node(Flow_GetCurrentNode(workID));
		Work wk = nd.getHisWork();
		wk.setOID(workID);
		wk.Retrieve();
		wk.ResetDefaultVal(null, null, 0);
		return wk;
	}
	/**
	 指定 workid 当前节点由哪些人可以执行.

	 @param workID 需要找到的workid
	 @return 返回当前处理人员列表,数据结构与WF_GenerWorkerlist一致.
	*/
	public static DataTable Flow_GetWorkerList(long workID)
	{
		Paras ps = new Paras();
		ps.SQL = "SELECT * FROM WF_GenerWorkerlist WHERE IsEnable=1 AND IsPass=0 AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID";
		ps.Add("WorkID", workID);
		return DBAccess.RunSQLReturnTable(ps);
	}
	/**
	 根据流程标记获得流程编号

	 @param flowMark 流程属性的流程标记
	 @return 流程编号
	*/
	public static String Flow_GetFlowNoByFlowMark(String flowMark)
	{
		String sql = "SELECT No FROM WF_Flow WHERE FlowMark='" + flowMark + "'";
		return DBAccess.RunSQLReturnStringIsNull(sql, null);
	}
	/**
	 检查是否可以发起流程

	 @param flowNo 流程编号
	 @param userNo 用户编号
	 @return 是否可以发起当前流程
	*/

	public static boolean Flow_IsCanStartThisFlow(String flowNo, String userNo, String pFlowNo, int pNodeID) throws Exception {
		return Flow_IsCanStartThisFlow(flowNo, userNo, pFlowNo, pNodeID, 0);
	}

	public static boolean Flow_IsCanStartThisFlow(String flowNo, String userNo, String pFlowNo) throws Exception {
		return Flow_IsCanStartThisFlow(flowNo, userNo, pFlowNo, 0, 0);
	}

	public static boolean Flow_IsCanStartThisFlow(String flowNo, String userNo) throws Exception {
		return Flow_IsCanStartThisFlow(flowNo, userNo, null, 0, 0);
	}

	public static boolean Flow_IsCanStartThisFlow(String flowNo, String userNo, String pFlowNo, int pNodeID, long pworkID) throws Exception {
		if (DataType.IsNullOrEmpty(WebUser.getNo()))
		{
			throw new RuntimeException("err@在判断是否可以发起该流程是出现错误，没有获取到用户的登录信息，请重新登录。");
		}


			///#region 判断开始节点是否可以发起.
		Node nd = new Node(Integer.parseInt(flowNo + "01"));
		if (nd.getHisDeliveryWay() == DeliveryWay.ByGuest || nd.getItIsGuestNode() == true)
		{
			if (WebUser.getNo().equals("Guest") == false)
			{
				throw new RuntimeException("err@当前节点是外部用户处理节点，但是目前您{" + WebUser.getNo() + "}不是外部用户帐号。");
			}
			return true;
		}

		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		int num = 0;

		switch (nd.getHisDeliveryWay())
		{
			case ByStation:
			case ByStationOnly:
				ps.SQL = "SELECT COUNT(A.FK_Node) as Num FROM WF_NodeStation A, Port_DeptEmpStation B WHERE A.FK_Station= B.FK_Station AND  A.FK_Node=" + dbstr + "FK_Node AND B.FK_Emp=" + dbstr + "FK_Emp";
				ps.Add("FK_Node", nd.getNodeID());
				ps.Add("FK_Emp", userNo, false);
				num = DBAccess.RunSQLReturnValInt(ps);
				break;
			case ByTeamOrgOnly:
			case ByTeamDeptOnly:
				ps.SQL = "SELECT COUNT(A.FK_Node) as Num FROM WF_NodeTeam A, Port_TeamEmp B, Port_Emp C WHERE B.FK_Emp=C.No AND A.FK_Team= B.FK_Team AND  A.FK_Node=" + dbstr + "FK_Node AND B.FK_Emp=" + dbstr + "FK_Emp AND C.OrgNo=" + dbstr + "OrgNo";
				ps.Add("FK_Node", nd.getNodeID());
				ps.Add("FK_Emp", userNo, false);
				ps.Add("OrgNo", WebUser.getOrgNo(), false);
				num = DBAccess.RunSQLReturnValInt(ps);
				break;
			case ByTeamOnly:
				ps.SQL = "SELECT COUNT(A.FK_Node) as Num FROM WF_NodeTeam A, Port_TeamEmp B WHERE A.FK_Group= B.FK_Group AND  A.FK_Node=" + dbstr + "FK_Node AND B.FK_Emp=" + dbstr + "FK_Emp";
				ps.Add("FK_Node", nd.getNodeID());
				ps.Add("FK_Emp", userNo, false);
				num = DBAccess.RunSQLReturnValInt(ps);
				break;
			case ByDept:

				ps.SQL = "SELECT COUNT(A.FK_Node) as Num FROM WF_NodeDept A, Port_DeptEmp B WHERE A.FK_Dept= B.FK_Dept AND  A.FK_Node=" + dbstr + "FK_Node AND B.FK_Emp=" + dbstr + "FK_Emp";
				ps.Add("FK_Node", nd.getNodeID());
				ps.Add("FK_Emp", userNo, false);
				num = DBAccess.RunSQLReturnValInt(ps);

				if (num == 0)
				{
					ps.clear();
					ps.SQL = "SELECT COUNT(A.FK_Node) as Num FROM WF_NodeDept A, Port_Emp B WHERE A.FK_Dept= B.FK_Dept AND  A.FK_Node=" + dbstr + "FK_Node AND B.No=" + dbstr + "FK_Emp";
					ps.Add("FK_Node", nd.getNodeID());
					ps.Add("FK_Emp", userNo, false);
					num = DBAccess.RunSQLReturnValInt(ps);
				}

				break;

			case ByBindEmp:
				ps.SQL = "SELECT COUNT(*) AS Num FROM WF_NodeEmp WHERE FK_Emp=" + dbstr + "FK_Emp AND FK_Node=" + dbstr + "FK_Node";
				if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
				{
					ps.Add("FK_Emp", WebUser.getOrgNo() + "_" + userNo, false);
				}
				else
				{
					ps.Add("FK_Emp", userNo, false);
				}
				ps.Add("FK_Node", nd.getNodeID());
				num = DBAccess.RunSQLReturnValInt(ps);
				break;
			case ByDeptAndStation:


				String sql = "SELECT COUNT(A.FK_Node) as Num FROM WF_NodeDept A, Port_DeptEmp B, WF_NodeStation C, Port_DeptEmpStation D";
				sql += " WHERE A.FK_Dept= B.FK_Dept AND  A.FK_Node=" + dbstr + "FK_Node AND B.FK_Emp=" + dbstr + "FK_Emp AND  A.FK_Node=C.FK_Node AND C.FK_Station=D.FK_Station AND D.FK_Emp=" + dbstr + "FK_Emp";
				ps.SQL = sql;
				ps.Add("FK_Node", nd.getNodeID());
				ps.Add("FK_Emp", userNo, false);
				num = DBAccess.RunSQLReturnValInt(ps);
				break;
			case BySelected:
			case BySelected_1:
			case BySelected_2:
				num = 1;
				break;
			case BySelectedOrgs: //按照绑定的组织计算.
				ps.SQL = "SELECT COUNT(*) AS Num FROM WF_FlowOrg WHERE OrgNo=" + dbstr + "OrgNo ";
				ps.Add("OrgNo", WebUser.getOrgNo(), false);
				num = DBAccess.RunSQLReturnValInt(ps);
				break;
			default:
				throw new RuntimeException("@开始节点不允许设置此访问规则：" + nd.getHisDeliveryWay());
		}


		if (num == 0)
		{
			return false;
		}

		if (pFlowNo == null)
		{
			return true;
		}

			///#endregion 判断开始节点是否可以发起.


			///#region 检查流程发起限制规则. 为周大福项目增加判断.
		if (pNodeID == 0)
		{
			return true;
		}

		//当前节点所有配置的子流程.
		SubFlowHands subflows = new SubFlowHands(pNodeID);

		//当前的子流程.
		for (SubFlowHand item : subflows.ToJavaList())
		{
			if (item.getSubFlowNo().equals(flowNo) == false)
			{
				continue;
			}

			if (item.getStartOnceOnly() == true)
			{
				String sql = "SELECT Starter, RDT FROM WF_GenerWorkFlow WHERE PWorkID=" + pworkID + " AND FK_Flow='" + flowNo + "' AND WFState >=2 ";
				DataTable dt = DBAccess.RunSQLReturnTable(sql);
				if (dt.Rows.size() == 0)
				{
					// return true; //没有人发起，他可以发起。
				}
				else
				{
					throw new RuntimeException("该流程只能允许发起一次.");
				}
			}

			if (item.getCompleteReStart() == true)
			{
				String sql = "SELECT Starter, RDT,WFState FROM WF_GenerWorkFlow WHERE PWorkID=" + pworkID + " AND FK_Flow='" + flowNo + "' AND WFState != 3";
				DataTable dt = DBAccess.RunSQLReturnTable(sql);
				if (dt.Rows.size() != 0)
				{
					if (dt.Rows.size() == 1 && Integer.parseInt(dt.Rows.get(0).getValue("WFState").toString()) == 0)
					{

					}
					else
					{
						throw new RuntimeException("该流程已经启动还没有运行结束，不能再次启动.");
					}


				}

			}


			if (item.getItIsEnableSpecFlowStart() == true)
			{
				//指定的流程发起之后，才能启动该流程。
				String[] fls = item.getSpecFlowStart().split("[,]", -1);
				for (String flStr : fls)
				{
					if (DataType.IsNullOrEmpty(flStr) == true)
					{
						continue;
					}

					String sql = "SELECT Starter, RDT FROM WF_GenerWorkFlow WHERE PWorkID=" + pworkID + " AND FK_Flow='" + flStr + "' AND WFState >=2 ";
					DataTable dt = DBAccess.RunSQLReturnTable(sql);
					if (dt.Rows.size() == 0)
					{
						bp.wf.Flow myflow = new Flow(flStr);
						throw new RuntimeException("流程:[" + myflow.getName() + "]没有发起,您不能启动[" + item.getSubFlowName() + "]。");
					}
				}
			}

			if (item.getItIsEnableSpecFlowOver() == true)
			{
				//指定的流程发起之后，才能启动该流程。
				String[] fls = item.getSpecFlowOver().split("[,]", -1);
				for (String flStr : fls)
				{
					if (DataType.IsNullOrEmpty(flStr) == true)
					{
						continue;
					}

					String sql = "SELECT Starter, RDT FROM WF_GenerWorkFlow WHERE PWorkID=" + pworkID + " AND FK_Flow='" + flStr + "' AND WFState =3 ";
					DataTable dt = DBAccess.RunSQLReturnTable(sql);
					if (dt.Rows.size() == 0)
					{
						bp.wf.Flow myflow = new Flow(flStr);
						throw new RuntimeException("流程:[" + myflow.getName() + "]没有完成,您不能启动[" + item.getSubFlowName() + "]。");
					}
				}
			}
		}

			///#endregion 检查流程发起限制规则.


			///#region 判断流程属性的规则.
		Flow fl = new Flow(flowNo);
		if (fl.getStartLimitRole() == StartLimitRole.None)
		{
			return true;
		}

		//只有一个子流程,才能发起.
		if (fl.getStartLimitRole() == StartLimitRole.OnlyOneSubFlow)
		{
			if (pworkID == 0)
			{
				return true;
			}

			String sql = "SELECT Starter, RDT FROM WF_GenerWorkFlow WHERE PWorkID=" + pworkID + " AND FK_Flow='" + fl.getNo() + "' AND WFState >=2 ";
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
			{
				return true;
			}

			throw new RuntimeException("该流程只能允许发起一个子流程.");
		}

			///#endregion 判断流程属性的规则.

		return true;
	}
	/**
	 子线程退回到分流点的处理

	 @param workID
	 @param nodeId
	 @return
	*/
	public static boolean Flow_IsCanToFLTread(long workID, long fid, int nodeId) throws Exception {
		if (workID == 0)
		{
			Node nd = new Node(nodeId);
			if (fid != 0 && (nd.getHisRunModel() == RunModel.FL || nd.getHisRunModel() == RunModel.FHL))
			{
				return true;
			}

		}
		else
		{
			GenerWorkFlow gwf = new GenerWorkFlow(workID);
			Node nd = new Node(gwf.getNodeID());

			//子线程退回到分流点
			if (gwf.getWFState() == WFState.ReturnSta && fid != 0 && (nd.getHisRunModel() == RunModel.FL || nd.getHisRunModel() == RunModel.FHL))
			{
				return true;
			}
		}

		return false;
	}
	/**
	 获得正在运行中的子流程的数量
	 @param pWorkID 父流程的workid
	 @return 获得正在运行中的子流程的数量。如果是0，表示所有的流程的子流程都已经结束。
	*/
	public static int Flow_NumOfSubFlowRuning(long pWorkID)
	{
		String sql = "SELECT COUNT(*) AS num FROM WF_GenerWorkFlow WHERE WFState NOT IN (3,11) AND PWorkID=" + pWorkID;
		return DBAccess.RunSQLReturnValInt(sql);
	}
	/**
	 获得正在运行中的子流程的数量
	 @param pWorkID 父流程的workid
	 @param currWorkID 不包含当前的工作节点ID
	 @return 获得正在运行中的子流程的数量。如果是0，表示所有的流程的子流程都已经结束。
	*/
	public static int Flow_NumOfSubFlowRuning(long pWorkID, long currWorkID)
	{
		String sql = "SELECT COUNT(*) AS num FROM WF_GenerWorkFlow WHERE WFState NOT IN (3,11) AND WorkID!=" + currWorkID + " AND PWorkID=" + pWorkID;
		return DBAccess.RunSQLReturnValInt(sql);
	}
	/**
	 根据WorkID判断是否已经发起过流程实例

	 @param workID
	 @return
	*/
	public static boolean Flow_IsInGenerWork(long workID)
	{

		if (workID == 0)
		{
			return false;
		}

		String sql = "select * from WF_Generworkflow where WorkID='" + workID + "'";
		return DBAccess.RunSQLReturnCOUNT(sql) > 0;
	}
	/**
	 检查指定节点上的所有子流程是否完成？
	 For: 深圳熊伟.

	 @param nodeID 节点ID
	 @param workID 工作ID
	 @return 返回该节点上的子流程是否完成？
	*/
	public static boolean Flow_CheckAllSubFlowIsOver(int nodeID, long workID)
	{
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE  PNodeID=" + dbstr + "PNodeID AND PWorkID=" + dbstr + "PWorkID AND WFState NOT IN (3,11)";
		ps.Add(GenerWorkFlowAttr.PNodeID, nodeID);
		ps.Add(GenerWorkFlowAttr.PWorkID, workID);
		ps.Add(GenerWorkFlowAttr.WFState, WFState.CompleteEnd.getValue());
		if (DBAccess.RunSQLReturnValInt(ps) == 0)
		{
			return true;
		}
		return false;
	}
	/**
	 检查当前人员是否有权限处理当前的工作

	 @param workID
	 @param userNo
	 @return
	*/
	public static boolean Flow_IsCanDoCurrentWork(long workID, String userNo) throws Exception {
		if (workID == 0)
		{
			return true;
		}

		//判断是否有待办.
		GenerWorkerList gwl = new GenerWorkerList();
		int inum = gwl.Retrieve(GenerWorkerListAttr.WorkID, workID, GenerWorkerListAttr.FK_Emp, userNo, GenerWorkerListAttr.IsPass, 0);
		if (inum >= 1)
		{
			return true;
		}

		GenerWorkFlow mygwf = new GenerWorkFlow(workID);


			///#region 判断是否是开始节点.
		/* 判断是否是开始节点 . */
		String str = String.valueOf(mygwf.getNodeID());
		if (str.endsWith("01") == true)
		{
			if (mygwf.getStarter().equals(userNo))
			{
				return true;
			}

			String mysql = "SELECT FK_Emp, IsPass FROM WF_GenerWorkerlist WHERE WorkID=" + workID + " AND FK_Node=" + mygwf.getNodeID();
			DataTable mydt = DBAccess.RunSQLReturnTable(mysql);
			if (mydt.Rows.size() == 0)
			{
				return true;
			}

			for (DataRow dr : mydt.Rows)
			{
				String fk_emp = dr.getValue(0).toString();
				String isPass = dr.getValue(1).toString();
				if (fk_emp.equals(userNo) && (isPass.equals("0") || isPass.equals("80") || isPass.equals("90")))
				{
					return true;
				}
			}
			return false;
		}

			///#endregion 判断是否是开始节点.

		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "SELECT c.RunModel,c.IsGuestNode, a.GuestNo, a.TaskSta, a.WFState, IsPass FROM WF_GenerWorkFlow a, WF_GenerWorkerlist b, WF_Node c WHERE  b.FK_Node=c.NodeID AND a.WorkID=b.WorkID AND a.FK_Node=b.FK_Node  AND b.FK_Emp=" + dbstr + "FK_Emp AND (b.IsEnable=1 OR b.IsPass>=70 OR IsPass=0)   AND a.WorkID=" + dbstr + "WorkID ";
		ps.Add("FK_Emp", userNo, false);
		ps.Add("WorkID", workID);
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		if (dt.Rows.size() == 0)
		{

				///#region 判断是否有授权信息？
			Auths auths = new Auths();
			if (DataType.IsNullOrEmpty(WebUser.getAuth()) == true)
			{
				auths.Retrieve(AuthAttr.AutherToEmpNo, userNo, null);
			}
			else
			{
				auths.Retrieve(AuthAttr.AutherToEmpNo, userNo, AuthAttr.Auther, WebUser.getAuth(), null);
			}
			//不存在授权待办
			if (auths.size() == 0)
			{
				return false;
			}
			for (Auth item : auths.ToJavaList())
			{
				ps = new Paras();
				ps.SQL = "SELECT c.RunModel,c.IsGuestNode, a.GuestNo, a.TaskSta, a.WFState, IsPass FROM WF_GenerWorkFlow a, WF_GenerWorkerlist b, WF_Node c WHERE  b.FK_Node=c.NodeID AND a.WorkID=b.WorkID AND a.FK_Node=b.FK_Node  AND b.FK_Emp=" + dbstr + "FK_Emp AND (b.IsEnable=1 OR b.IsPass>=70 OR IsPass=0)   AND a.WorkID=" + dbstr + "WorkID ";
				ps.Add("FK_Emp", item.getAuther(), false);
				ps.Add("WorkID", workID);
				dt = DBAccess.RunSQLReturnTable(ps);
				if (dt.Rows.size() == 0)
				{
					continue;
				}

				//判断是否是待办.
				int myisPassTemp = Integer.parseInt(dt.Rows.get(0).getValue("IsPass").toString());
				//新增加的标记,=90 就是会签主持人执行会签的状态. 翻译.
				if (myisPassTemp == 90 || myisPassTemp == 80 || myisPassTemp == 0)
				{
					return true;
				}
			}

				///#endregion 判断是否有授权信息？

			return false;
		}

		//判断是否是待办.
		int myisPass = Integer.parseInt(dt.Rows.get(0).getValue("IsPass").toString());

		//新增加的标记,=90 就是会签主持人执行会签的状态. 翻译.
		if (myisPass == 90)
		{
			return true;
		}

		if (myisPass == 80)
		{
			return true;
		}

		if (myisPass != 0)
		{
			return false;
		}

		WFState wfsta = WFState.forValue(Integer.parseInt(dt.Rows.get(0).getValue("WFState").toString()));
		if (wfsta == WFState.CompleteByNode || wfsta== WFState.CompleteEnd || wfsta == WFState.Delete)
		{
			return false;
		}

		//判断是否是客户处理节点.
		int isGuestNode = Integer.parseInt(dt.Rows.get(0).getValue("IsGuestNode").toString());
		if (isGuestNode == 1)
		{
			if (dt.Rows.get(0).getValue("GuestNo").toString().equals(GuestUser.getNo()) == true)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		return true;
	}
	/**
	 检查当前人员是否有权限处理当前的工作.

	 @param nodeID 节点ID
	 @param workID 工作ID
	 @param userNo 要判断的操作人员
	 @return 返回指定的人员是否有操作当前工作的权限
	*/
	public static boolean Flow_IsCanDoCurrentWorkGuest(int nodeID, long workID, String userNo)
	{
		if (workID == 0)
		{
			return true;
		}

		if (WebUser.getNo().equals("admin") == true)
		{
			return true;
		}

		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		//ps.SQL = "SELECT c.RunModel FROM WF_GenerWorkFlow a , WF_GenerWorkerlist b, WF_Node c WHERE a.FK_Node=" + dbstr + "FK_Node AND b.FK_Node=c.NodeID AND a.WorkID=b.WorkID AND a.FK_Node=b.FK_Node  AND b.FK_Emp=" + dbstr + "FK_Emp AND b.IsEnable=1 AND a.workid=" + dbstr + "WorkID";
		//ps.Add("FK_Node", nodeID);
		//ps.Add("FK_Emp", userNo);
		//ps.Add("WorkID", workID);
		String sql = "SELECT c.RunModel, a.TaskSta FROM WF_GenerWorkFlow a , WF_GenerWorkerlist b, WF_Node c WHERE a.FK_Node='" + nodeID + "'  AND b.FK_Node=c.NodeID AND a.WorkID=b.WorkID AND a.FK_Node=b.FK_Node  AND b.GuestNo='" + userNo + "' AND b.IsEnable=1 AND a.WorkID=" + workID;

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			return false;
		}

		int i = Integer.parseInt(dt.Rows.get(0).getValue(0).toString());
		TaskSta TaskStai = TaskSta.forValue(Integer.parseInt(dt.Rows.get(0).getValue(1).toString()));
		if (TaskStai == TaskSta.Sharing)
		{
			return false;
		}

		RunModel rm = RunModel.forValue(i);
		switch (rm)
		{
			case Ordinary:
				return true;
			case FL:
				return true;
			case HL:
				return true;
			case FHL:
				return true;
			case SubThreadSameWorkID:
			case SubThreadUnSameWorkID:
				return true;
			default:
				break;
		}

		if (DBAccess.RunSQLReturnValInt(ps) == 0)
		{
			return false;
		}

		return true;
	}
	/**
	 是否可以查看流程数据
	 用于判断是否可以查看流程轨迹图.
	 edit: zhoupeng 2015-03-25

	 @param flowNo 流程编号
	 @param workid 工作ID
	 @return
	*/

	public static boolean Flow_IsCanViewTruck(String flowNo, long workid) throws Exception {
		return Flow_IsCanViewTruck(flowNo, workid, null);
	}

	public static boolean Flow_IsCanViewTruck(String flowNo, long workid, String userNo) throws Exception {
		if (userNo == null)
		{
			userNo = WebUser.getNo();
		}
		if (WebUser.getNo().equals("admin") == true)
		{
			return true;
		}

		//先从轨迹里判断.
		String dbStr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "SELECT count(MyPK) as Num FROM ND" + Integer.parseInt(flowNo) + "Track WHERE (WorkID=" + dbStr + "WorkID OR FID=" + dbStr + "FID) AND (EmpFrom=" + dbStr + "Emp1 OR EmpTo=" + dbStr + "Emp2)";
		ps.Add(bp.wf.TrackAttr.WorkID, workid);
		ps.Add(bp.wf.TrackAttr.FID, workid);
		ps.Add("Emp1", WebUser.getNo(), false);
		ps.Add("Emp2", WebUser.getNo(), false);

		if (DBAccess.RunSQLReturnValInt(ps) > 1)
		{
			return true;
		}

		//在查看该流程的发起者，与当前人是否在同一个部门，如果是也返回true.
		ps = new Paras();
		ps.SQL = "SELECT FK_Dept FROM WF_GenerWorkFlow WHERE WorkID=" + dbStr + "WorkID OR WorkID=" + dbStr + "FID";
		ps.Add(bp.wf.TrackAttr.WorkID, workid);
		ps.Add(bp.wf.TrackAttr.FID, workid);

		String fk_dept = DBAccess.RunSQLReturnStringIsNull(ps, null);
		if (fk_dept == null)
		{
			bp.wf.Flow fl = new Flow(flowNo);
			ps.SQL = "SELECT FK_Dept FROM " + fl.getPTable() + " WHERE OID=" + dbStr + "WorkID OR OID=" + dbStr + "FID";
			fk_dept = DBAccess.RunSQLReturnStringIsNull(ps, null);
			if (fk_dept == null)
			{
				throw new RuntimeException("@流程引擎数据被删除.");
			}
		}
		if (Objects.equals(WebUser.getDeptNo(), fk_dept))
		{
			return true;
		}

		return false;
	}
	/**
	 删除子线程

	 @param workid 子线程的工作ID
	 @param info 删除信息
	*/
	public static String Flow_DeleteSubThread(long workid, String info) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.SetValByKey(GenerWorkFlowAttr.WorkID, workid);
		if (gwf.RetrieveFromDBSources() > 0)
		{
			WorkFlow wf = new WorkFlow(workid);
			String msg = wf.DoDeleteWorkFlowByReal(false);

			bp.wf.Dev2Interface.WriteTrackInfo(gwf.getFlowNo(), gwf.getNodeID(), gwf.getNodeName(), gwf.getFID(), 0, info, "删除子线程");
			return msg;
		}
		return null;
	}
	/**
	 执行工作催办

	 @param workID 工作ID
	 @param msg 催办消息
	 @return 返回执行结果
	*/

	public static String Flow_DoPress(long workID, String msg) throws Exception {
		return Flow_DoPress(workID, msg, false);
	}

	public static String Flow_DoPress(long workID, String msg, boolean isPressSubFlow) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		if (gwf.getIsOver() == true)
		{
			return "err@流程已完成，您不能催办。";
		}

		if (String.valueOf(gwf.getNodeID()).endsWith("01") == true)
		{
			return "info@当前节点是开始节点，您不能执行催办.";
		}

		/*找到当前待办的工作人员*/
		GenerWorkerLists wls = new GenerWorkerLists();
		wls.Retrieve("FK_Node", gwf.getNodeID(), "WorkID", workID, "IsPass", 0, null);
		if (wls.size() == 0)
		{
			wls.Retrieve("FID", workID, "IsPass", 0, null);
			if (wls.size() == 0)
			{
				return "err@系统错误，没有找到要催办的人。";
			}
		}

		String toEmp = "", toEmpName = "";
		String mailTitle = "催办:" + gwf.getTitle() + ", 发送人:" + WebUser.getName();




		for (GenerWorkerList wl : wls.ToJavaList())
		{
			if (wl.getItIsEnable() == false)
			{
				continue;
			}

			toEmp += wl.getEmpNo() + ",";
			toEmpName += wl.getEmpName() + ",";

			// 发消息.
				bp.wf.Dev2Interface.Port_SendMsg(wl.getEmpNo(), mailTitle, msg, null, bp.wf.SMSMsgType.DoPress, gwf.getFlowNo(),
						gwf.getNodeID(), gwf.getWorkID(), gwf.getFID(),null);


			//wl.setPressTimes(wl.getPressTimes() + 1);
			wl.Update();
		}

		//写入日志.
		WorkNode wn = new WorkNode(workID, gwf.getNodeID());
		wn.AddToTrack(ActionType.Press, toEmp, toEmpName, gwf.getNodeID(), gwf.getNodeName(), msg);

		//如果催办子流程.
		if (isPressSubFlow == true)
		{
			String subMsg = "";
			GenerWorkFlows gwfs = gwf.getHisSubFlowGenerWorkFlows();
			for (GenerWorkFlow item : gwfs.ToJavaList())
			{
				subMsg += "@已经启动对子线程:" + item.getTitle() + "的催办,消息如下:";
				subMsg += Flow_DoPress(item.getWorkID(), msg, false);
			}
			return "系统已把您的信息通知给:" + toEmpName + "" + subMsg;
		}
		else
		{
			return "系统已把您的信息通知给:" + toEmpName;
		}
	}

	/**
	 * 执行批量移交
	 * @param fromOneUserNo 当事人的人员账号
	 * @param workIDs 流程实例编号，多个用逗号分开.
	 * @param toOneUserNo 移交给的人员编号.
	 * @return 执行信息
	 * @throws Exception
	 */
	public static String Flow_Batch_Shift(String fromOneUserNo, String workIDs, String toOneUserNo) throws Exception {
		String[] splitWorkID = workIDs.split(",");
		for (String item : splitWorkID)
		{
			long workid = Long.parseLong(item);
			Dev2Interface.Node_Shift(fromOneUserNo, workid, toOneUserNo, "批量移交.");
		}

		Emp emp = new Emp(toOneUserNo);

		//移交未来处理人.
		//String sql = " SELECT MyPK FROM WF_SelectAccper A, WF_Node B WHERE A.FK_Node =B.NodeID  AND B.FK_Flow in ('" + flowNos + "')   and FK_Emp ='" + fromOneUserNo + "' ";
		String sql = " SELECT MyPK FROM WF_SelectAccper WHERE WorkID IN ('" + workIDs + "')   AND FK_Emp ='" + fromOneUserNo + "' ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		for (DataRow dr : dt.Rows)
		{
			String mypk = dr.getValue("MyPK").toString();
			SelectAccper sa = new SelectAccper(mypk);
			sa.Delete(); //删除原来的数据.

			sa.setEmpNo(emp.getNo());
			sa.setEmpName(emp.getName());
			sa.Save(); //执行保存.
		}

		return "执行成功, 合计 : 待办 " + splitWorkID.length + " 个, 未来处理待办 " + dt.Rows.size() + " 个, 数据被移交给 [" + toOneUserNo + "]";
	}

	/**
	 重新设置流程标题
	 可以在节点的任何位置调用它,产生新的标题。

	 @param workid 工作ID
	 @return 是否设置成功
	*/
	public static boolean Flow_ReSetFlowTitle(long workid) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(workid);

		Flow fl = new Flow(gwf.getFlowNo());
		GERpt rpt = new GERpt("ND" + Integer.parseInt(fl.getNo()) + "Rpt", workid);

		String title = bp.wf.WorkFlowBuessRole.GenerTitle(fl, rpt);
		return Flow_SetFlowTitle(gwf.getFlowNo(), workid, title);
	}
	/**
	 设置流程参数
	 该参数，用户可以在流程实例中获得到.

	 @param workid 工作ID
	 @param paras 参数,格式：@GroupMark=xxxx@IsCC=1
	 @return 是否设置成功
	*/
	public static boolean Flow_SetFlowParas(String flowNo, long workid, String paras) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workid);
		if (gwf.RetrieveFromDBSources() == 0)
		{
			throw new RuntimeException("创建流程ID不存在.");
		}

		String[] strs = paras.split("[@]", -1);
		for (String item : strs)
		{
			if (DataType.IsNullOrEmpty(item))
			{
				continue;
			}
			//GroupMark=xxxx
			String[] mystr = item.split("[=]", -1);
			gwf.SetPara(mystr[0], mystr[1]);
		}
		gwf.Update();
		return true;
	}
	/**
	 设置流程应完成日期.

	 @param workid 工作ID
	 @param sdt 应完成日期
	*/
	public static void Flow_SetSDTOfFlow(long workid, String sdt) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(workid);
		gwf.setSDTOfFlow(sdt);
		gwf.Update();
	}
	/**
	 设置流程标题

	 @param flowNo 流程编号
	 @param workid 工作ID
	 @param title 标题
	 @return 是否设置成功
	*/
	public static boolean Flow_SetFlowTitle(String flowNo, long workid, String title) throws Exception {
		//替换标题中出现的英文 ""引号，造成在获取数据时，造成异常
		title = title.replace('"', '“');
		title = title.replace('"', '”');

		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkFlow SET Title=" + dbstr + "Title WHERE WorkID=" + dbstr + "WorkID";
		ps.Add(GenerWorkFlowAttr.Title, title, false);
		ps.Add(GenerWorkFlowAttr.WorkID, workid);
		DBAccess.RunSQL(ps);

		if (DataType.IsNullOrEmpty(flowNo) == true)
		{
			flowNo = DBAccess.RunSQLReturnString("SELECT FK_Flow FROM WF_GenerWorkFlow WHERE WorkID=" + workid);
		}

		Flow fl = new Flow(flowNo);
		ps = new Paras();
		ps.SQL = "UPDATE " + fl.getPTable() + " SET Title=" + dbstr + "Title WHERE OID=" + dbstr + "WorkID";
		ps.Add(GenerWorkFlowAttr.Title, title, false);
		ps.Add(GenerWorkFlowAttr.WorkID, workid);
		int num = DBAccess.RunSQL(ps);
		if (num == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	/**
	 调度流程
	 说明：
	 1，通常是由admin执行的调度。
	 2，特殊情况下，需要从一个人的待办调度到另外指定的节点，制定的人员上。

	 @param workid 工作ID
	 @param toNodeID 调度到节点
	 @param toEmper 调度到人员
	*/
	public static String Flow_Schedule(long workid, int toNodeID, String toEmper) throws Exception {
		String dbstr = SystemConfig.getAppCenterDBVarStr();

		Node nd = new Node(toNodeID);
		Emp emp = new Emp(toEmper);

		// 找到GenerWorkFlow,并执行更新.
		GenerWorkFlow gwf = new GenerWorkFlow(workid);
		gwf.setWFState(WFState.Runing);
		gwf.setTaskSta(TaskSta.None);
		gwf.setTodoEmps(toEmper + "," + emp.getName() + ";");
		gwf.setNodeID(toNodeID);
		gwf.setNodeName(nd.getName());
		//gwf.StarterName =emp.getName();
		gwf.Update();

		//让其都设置完成。
		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=1 WHERE WorkID=" + dbstr + "WorkID";
		ps.Add(GenerWorkFlowAttr.WorkID, workid);
		DBAccess.RunSQL(ps);

		// 更新流程数据信息。
		Flow fl = new Flow(gwf.getFlowNo());
		ps = new Paras();
		ps.SQL = "UPDATE " + fl.getPTable() + " SET FlowEnder=" + dbstr + "FlowEnder,FlowEndNode=" + dbstr + "FlowEndNode WHERE OID=" + dbstr + "OID";
		ps.Add(GERptAttr.FlowEnder, toEmper, false);
		ps.Add(GERptAttr.FlowEndNode, toNodeID);
		ps.Add(GERptAttr.OID, workid);
		DBAccess.RunSQL(ps);

		// 执行更新.
		GenerWorkerLists gwls = new GenerWorkerLists(workid);
		GenerWorkerList gwl = gwls.get(gwls.size() - 1) instanceof GenerWorkerList ? (GenerWorkerList)gwls.get(gwls.size() - 1) : null; //获得最后一个。
		gwl.setWorkID(workid);
		gwl.setNodeID(toNodeID);
		gwl.setNodeName(nd.getName());
		gwl.setEmpNo(toEmper);
		gwl.setEmpName(emp.getName());
		gwl.setItIsPass(false);
		gwl.setItIsEnable(true);
		gwl.setItIsRead(false);
		gwl.setWhoExeIt(nd.getWhoExeIt());
		//  gwl.Sender = bp.web.WebUser.getNo();
		gwl.setHungupTimes(0);
		gwl.setFID(gwf.getFID());
		gwl.setDeptNo(emp.getDeptNo());
		gwl.setDeptName(emp.getDeptText());

		if (gwl.Update() == 0)
		{
			gwl.Insert();
		}

		String sql = "SELECT COUNT(*) FROM WF_EmpWorks where WorkID=" + workid + " and fk_emp='" + toEmper + "'";
		int i = DBAccess.RunSQLReturnValInt(sql);
		if (i == 0)
		{
			throw new RuntimeException("@调度错误");
		}

		return "该流程(" + gwf.getTitle() + ")，已经调度到(" + nd.getName() + "),分配给(" + emp.getName() + ")";
	}
	/**
	 设置流程运行模式
	 如果是自动流程. 负责人:liuxianchen.
	 调用地方/WorkOpt/TransferCustom.aspx

	 @param flowNo 流程编号
	 @param workid 工作ID
	 @param runType 是否自动运行？ 如果自动运行，就按照流程设置的规则运行。
	 非自动运行，就按照用户自己定义的运转顺序计算。
	 @param paras 手工运行的参数格式为: @节点ID1`子流程No`处理模式`接受人1,接受人n`抄送人1NO,抄送人nNO`抄送人1Name,抄送人nName@节点ID2`子流程No`处理模式`接受人1,接受人n`抄送人1NO,抄送人nNO`抄送人1Name,抄送人nName
	*/
	public static void Flow_SetFlowTransferCustom(String flowNo, long workid, TransferCustomType runType, String paras) throws Exception {
		//删除以前存储的参数.
		DBAccess.RunSQL("DELETE FROM WF_TransferCustom WHERE WorkID=" + workid);

		//保存参数.
		// 参数格式为  @104`SubFlow002`1`zhangsan,lisi`wangwu,chenba`王五,陈八@......
		String[] strs = paras.split("[@]", -1);
		int idx = 0, cidx = 0;
		for (String str : strs)
		{
			if (DataType.IsNullOrEmpty(str))
			{
				continue;
			}

			if (str.contains("`") == false)
			{
				continue;
			}

			// 处理字符串.
			String[] vals = str.split("[`]", -1);
			int nodeid = Integer.parseInt(vals[0]); // 节点ID.
			String subFlow = vals[1]; // 调用的子流程.
			int todomodel = Integer.parseInt(vals[2]); //处理模式.

			TransferCustom tc = new TransferCustom();
			tc.setIdx(idx); //顺序.
			tc.setNodeID(nodeid); // 节点.
			tc.setWorkID(workid); //工作ID.
			tc.setWorker(vals[3]); //工作人员.
			tc.setSubFlowNo(subFlow); //子流程.
			tc.setMyPK(tc.getNodeID() + "_" + tc.getWorkID() + "_" + idx);
			tc.setTodolistModel(TodolistModel.forValue(todomodel)); //处理模式.
			tc.Save();
			idx++;

			//设置抄送
			String[] ccs = vals[4].split("[,]", -1);
			String[] ccNames = vals[5].split("[,]", -1);
			SelectAccper sa = new SelectAccper();
			sa.Delete(SelectAccperAttr.FK_Node, nodeid, SelectAccperAttr.WorkID, workid, SelectAccperAttr.AccType, 1);

			cidx = 0;
			for (int i = 0; i < ccs.length; i++)
			{
				if ((ccs[i] == null || ccs[i].isEmpty() || Objects.equals(ccs[i], "0")))
				{
					continue;
				}

				sa = new SelectAccper();
				sa.setMyPK(nodeid + "_" + workid + "_" + ccs[i]);
				sa.setEmpNo(ccs[i].trim());
				sa.setEmpName(ccNames[i].trim());
				sa.setNodeID(nodeid);

				sa.setWorkID(workid);
				sa.setAccType(1);
				sa.setIdx(cidx);
				sa.Insert();
				cidx++;
			}
		}

		// 设置运行模式.
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workid);
		if (gwf.RetrieveFromDBSources() == 0)
		{
			//gwf.WFSta = WFSta.Runing;
			gwf.setWFState(WFState.Blank);

			gwf.setStarter(WebUser.getNo());
			gwf.setStarterName(WebUser.getName());

			gwf.setFlowNo(flowNo);
			bp.wf.Flow fl = new Flow(flowNo);
			gwf.setFlowSortNo(fl.getFlowSortNo());

			gwf.setSysType(fl.getSysType());
			gwf.setDeptNo(WebUser.getDeptNo());

			gwf.setTransferCustomType(runType);
			gwf.Insert();
			return;
		}
		gwf.setTransferCustomType(runType);
		gwf.Update();
	}
	/**
	 设置流程运行模式
	 启用新的接口原来的接口参数格式太复杂,仍然保留.
	 标准格式:@NodeID=节点ID;Worker=操作人员1,操作人员2,操作人员n,TodolistModel=多人处理模式;SubFlowNo=可发起的子流程编号;SDT=应完成时间;
	 标准简洁格式:@NodeID=节点ID;Worker=操作人员1,操作人员2,操作人员n;@NodeID=节点ID2;Worker=操作人员1,操作人员2,操作人员n;
	 完整格式: @NodeID=101;Worker=zhangsan,lisi;@TodolistModel=1;SubFlowNo=001;SDT=2015-12-12;@NodeID=102;Worker=zhangsan,lisi;@TodolistModel=1;SubFlowNo=001;SDT=2015-12-12;
	 简洁格式: @NodeID=101;Worker=zhangsan,lisi;@NodeID=102;Worker=wagnwu,zhaoliu;

	 @param flowNo
	 @param workid
	 @param runType
	 @param paras 格式为:@节点编号1;处理人员1,处理人员2,处理人员n(可选);应处理时间(可选)
	*/
	public static void Flow_SetFlowTransferCustomV201605(String flowNo, long workid, TransferCustomType runType, String paras) throws Exception {

			///#region 更新状态.
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workid);
		if (gwf.RetrieveFromDBSources() == 0)
		{
			//  gwf.WFSta = WFSta.Runing;
			gwf.setWFState(WFState.Blank);

			gwf.setStarter(WebUser.getNo());
			gwf.setStarterName(WebUser.getName());

			gwf.setFlowNo(flowNo);
			bp.wf.Flow fl = new Flow(flowNo);
			gwf.setFlowSortNo(fl.getFlowSortNo());
			gwf.setSysType(fl.getSysType());

			gwf.setDeptNo(WebUser.getDeptNo());

			gwf.setTransferCustomType(runType);
			gwf.Insert();
			return;
		}
		gwf.setTransferCustomType(runType);
		gwf.Update();
		if (runType == TransferCustomType.ByCCBPMDefine)
		{
			return; // 如果是按照设置的模式运行，就要更改状态后退出它.
		}

			///#endregion

		//删除以前存储的参数.
		DBAccess.RunSQL("DELETE FROM WF_TransferCustom WHERE WorkID=" + workid);

		//保存参数.
		// 参数格式为 格式为:@节点编号1;处理人员1,处理人员2,处理人员n;应处理时间(可选)
		// 例如1: @101;zhangsan,lisi,wangwu;2016-05-12;@102;liming,xiaohong,xiaozhang;2016-05-12
		// 例如2: @101;zhangsan,lisi,wangwu;@102;liming,xiaohong,xiaozhang;2016-05-12

		String[] strs = paras.split("[@]", -1);
		int idx = 0, cidx = 0;
		for (String str : strs)
		{
			if (DataType.IsNullOrEmpty(str))
			{
				continue;
			}

			if (str.contains(";") == false)
			{
				continue;
			}

			// 处理字符串.
			String[] vals = str.split("[;]", -1);
			int nodeid = Integer.parseInt(vals[0]); // 节点ID.
			String subFlow = vals[1]; // 调用的子流程.
			int todomodel = Integer.parseInt(vals[2]); //处理模式.

			TransferCustom tc = new TransferCustom();
			tc.setIdx(idx); //顺序.
			tc.setNodeID(nodeid); // 节点.
			tc.setWorkID(workid); //工作ID.
			tc.setWorker(vals[3]); //工作人员.
			tc.setSubFlowNo(subFlow); //子流程.
			tc.setMyPK(tc.getNodeID() + "_" + tc.getWorkID() + "_" + idx);
			tc.setTodolistModel(TodolistModel.forValue(todomodel)); //处理模式.
			tc.Save();
			idx++;

			//设置抄送
			String[] ccs = vals[4].split("[,]", -1);
			String[] ccNames = vals[5].split("[,]", -1);
			SelectAccper sa = new SelectAccper();
			sa.Delete(SelectAccperAttr.FK_Node, nodeid, SelectAccperAttr.WorkID, workid, SelectAccperAttr.AccType, 1);

			cidx = 0;
			for (int i = 0; i < ccs.length; i++)
			{
				if ((ccs[i] == null || ccs[i].isEmpty() || Objects.equals(ccs[i], "0")))
				{
					continue;
				}

				sa = new SelectAccper();
				sa.setMyPK(nodeid + "_" + workid + "_" + ccs[i]);
				sa.setEmpNo(ccs[i].trim());
				sa.setEmpName(ccNames[i].trim());
				sa.setNodeID(nodeid);
				sa.setWorkID(workid);
				sa.setAccType(1);
				sa.setIdx(cidx);
				sa.Insert();
				cidx++;
			}
		}
	}
	/**
	 是否可以删除该流程？

	 @param flowNo 流程编号
	 @param workid 工作ID
	 @return 是否可以删除该流程
	*/
	public static boolean Flow_IsCanDeleteFlowInstance(String flowNo, long workid, String userNo) throws Exception {
		if (WebUser.getIsAdmin() == true)
		{
			return true;
		}

		//是否是发起人.
		if (1 == 1)
		{
			Paras ps = new Paras();
			ps.SQL = "SELECT WorkID FROM WF_GenerWorkFlow WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND Starter=" + SystemConfig.getAppCenterDBVarStr() + "Starter";
			ps.Add("WorkID", workid);
			ps.Add("Starter", userNo, false);
			String user = DBAccess.RunSQLReturnStringIsNull(ps, null);
			if (user == null)
			{
				return false;
			}

			return true;
		}

		return false;
	}

		///#region 与流程有关的接口
		/// <summary>
		/// 复制并发起一个流程
		/// </summary>
		/// <param name="workid">历史的workid</param>
		/// <returns>返回生成的新的workid</returns>
		public static long Flow_StartAsWorkID(long workid_History) throws Exception {

			GenerWorkFlow gwf = new GenerWorkFlow(workid_History);
			long workidNew = Dev2Interface.Node_CreateBlankWork(gwf.getFlowNo(), "");

			String frmID = "ND" + Integer.valueOf(gwf.getFlowNo()) + "Rpt";
			GERpt rpt = new GERpt(frmID, workid_History);
			rpt.setOID(workidNew);
			rpt.setFlowEmps("");
			rpt.setFlowEnder(WebUser.getNo());
			rpt.setFlowStarter(WebUser.getNo());
			rpt.setFlowStartRDT(DataType.getCurrentDateTime());
			rpt.setFlowEndNode(Integer.valueOf(gwf.getFlowNo() + "01"));
			rpt.Update();

			//复制表单库的表单.
			FrmNodes fns = new FrmNodes();
			fns.Retrieve(FrmNodeAttr.FK_Node, Integer.valueOf(gwf.getFlowNo() + "01"));
			if (fns.size() == 0)
			{
				//2. 查询数据.
				GEEntityOID ge = new GEEntityOID("ND" + rpt.getFlowEndNode());
				ge.setOID(workid_History);
				ge.RetrieveFromDBSources();

				ge.CopyToOID(workidNew); //执行copy.
				ge.SetValByKey("RecNo", WebUser.getNo());
				ge.SetValByKey("RecName", WebUser.getName());
				ge.Update();
			}

			//多表单的情况.
			for (FrmNode fn : fns.ToJavaList())
			{
				//1. 检查能否启用该表单.
				if (fn.getFrmEnableRole() == FrmEnableRole.Disable)
					continue;

				//2. 查询数据.
				GEEntityOID ge = new GEEntityOID(fn.getFrmID());
				ge.setOID(workid_History);
				if (ge.RetrieveFromDBSources() == 0)
					continue;

				ge.CopyToOID(workidNew); //执行copy.

				ge.SetValByKey("RecNo", WebUser.getNo());
				ge.SetValByKey("RecName", WebUser.getName());
				ge.Update();
			}
			return workidNew;
		}
	/**
	 增加一个评论

	 @param flowNo 流程编号
	 @param workid 工作ID
	 @param fid 父工作ID
	 @param msg 消息
	 @param empNo 评论人编号
	 @param empName 评论人名称
	 @return 插入ID主键
	*/
	public static String Flow_BBSAdd(String flowNo, long workid, long fid, String msg, String empNo, String empName) throws Exception {
		return Glo.AddToTrack(ActionType.FlowBBS, flowNo, workid, fid, 0, null, empNo, empName, 0, null, empNo, empName, msg, null);
	}
	/**
	 删除一个评论.

	 @param flowNo 流程编号
	 @param mypk 主键
	 @return 返回删除信息.AddToTrack
	*/
	public static String Flow_BBSDelete(String flowNo, String mypk, String username)
	{
		Paras pss = new Paras();
		pss.SQL = "SELECT EMPFROM FROM ND" + Integer.parseInt(flowNo) + "Track WHERE MyPK=" + SystemConfig.getAppCenterDBVarStr() + "MyPK ";
		pss.Add("MyPK", mypk, false);
		String str = DBAccess.RunSQLReturnString(pss);
		if (str.equals(username) || Objects.equals(str, username))
		{
			Paras ps = new Paras();
			ps.SQL = "DELETE FROM ND" + Integer.parseInt(flowNo) + "Track WHERE MyPK=" + SystemConfig.getAppCenterDBVarStr() + "MyPK ";
			ps.Add("MyPK", mypk, false);
			DBAccess.RunSQL(ps);
			return "删除成功.";
		}
		else
		{
			return "删除失败,仅能删除自己评论!";
		}
	}
	/**
	 取消设置关注

	 @param workid 要取消设置的工作ID
	*/
	public static void Flow_Focus(long workid) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workid);
		int i = gwf.RetrieveFromDBSources();
		if (i == 0)
		{
			throw new RuntimeException("@ 设置关注错误：没有找到 WorkID= " + workid + " 的实例。");
		}

		String isFocus = gwf.GetParaString("F_" + WebUser.getNo(), "0"); //edit by liuxc,2016-10-22,修复关注/取消关注逻辑错误

		if (Objects.equals(isFocus, "0"))
		{
			gwf.SetPara("F_" + WebUser.getNo(), "1");
		}
		else
		{
			gwf.SetPara("F_" + WebUser.getNo(), "0");
		}

		gwf.DirectUpdate();
	}
	/**
	 调整流程：

	 @param workid 要调整的WorkID
	 @param toNodeID 调整到的节点ID
	 @param toEmpIDs 人员集合：如果为空，则根据当前节点的接受人规则获取人员.
	 @param note 调整原因
	 @return
	*/
	public static String Flow_ReSend(long workid, int toNodeID, String toEmpIDs, String note) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(workid);
		if(gwf.getIsOver())
			return "err@该流程已经运行完成您不能执行调整,可以执行回滚.";

		if (gwf.getFID() != 0)
		{
			return "err@当前是子线程节点，您不能跳转.";
		}
		bp.wf.Node nd = new Node(toNodeID);
		if (nd.getItIsSubThread() == true)
		{
			return "err@不能跳转到子线程节点上.";
		}

		int curNodeID = gwf.getNodeID();
		String curNodeName = gwf.getNodeName();


			///#region 处理要调整到的人员
		Emps emps = new Emps();
		String[] strs = toEmpIDs.split("[,]", -1);

		String todoEmps = "";
		int num = 0;
		Emp emp = new Emp();
		for (String empID : strs)
		{
			if (DataType.IsNullOrEmpty(empID) == true)
			{
				continue;
			}

			emp.setUserID(empID);
			if (emp.RetrieveFromDBSources() == 0)
			{
				return "err@" + empID + "错误,不存在.";
			}

			todoEmps += emp.getUserID() + "," + emp.getName() +";";
			num++;
			emps.AddEntity(emp);
		}

			///#endregion 处理要调整到的人员

		//设置人员.
		gwf.SetValByKey(GenerWorkFlowAttr.TodoEmps, todoEmps);
		gwf.setTodoEmpsNum(num);
		gwf.setHuiQianTaskSta(HuiQianTaskSta.None);
		gwf.setWFState(WFState.Runing);

		//给当前人员产生待办，删除他.
		GenerWorkerList gwl = new GenerWorkerList();

		//删除当前节点的d待办.
		gwl.Delete(GenerWorkerListAttr.WorkID, workid, GenerWorkerListAttr.FK_Node, gwf.getNodeID());
		//删除要调整到的节点数据.
		gwl.Delete(GenerWorkerListAttr.WorkID, workid, GenerWorkerListAttr.FK_Node, toNodeID);


		//调整流程强制结束.
		GenerWorkFlows gwfs = new GenerWorkFlows();
		gwfs.Retrieve(GenerWorkFlowAttr.FID, workid, null);
		for (GenerWorkFlow item : gwfs.ToJavaList())
		{
			bp.wf.Dev2Interface.Flow_DoFlowOver(item.getWorkID(), "调整强制结束");
		}

		Date dtOfShould = new Date();

		if (nd.getHisCHWay() == CHWay.ByTime)
		{
			CHNode chNode = new CHNode();
			chNode.setMyPK(workid + "_" + nd.getNodeID());
			if (chNode.RetrieveFromDBSources() != 0)
			{
				dtOfShould = DateUtils.parse(chNode.getEndDT());
			}
			else
			{
				//按天、小时考核
				if (nd.GetParaInt("CHWayOfTimeRole", 0) == 0)
				{
					//增加天数. 考虑到了节假日.
					//判断是修改了节点期限的天数
					int timeLimit = nd.getTimeLimit();
					dtOfShould = Glo.AddDayHoursSpan(new Date(), timeLimit, nd.getTimeLimitHH(), nd.getTimeLimitMM(), nd.getTWay());
				}
				//按照节点字段设置
				if (nd.GetParaInt("CHWayOfTimeRole", 0) == 1)
				{
					//获取设置的字段、
					String keyOfEn = nd.GetParaString("CHWayOfTimeRoleField");
					if (DataType.IsNullOrEmpty(keyOfEn) == true)
					{
						nd.setHisCHWay(CHWay.None);
					}
					else
					{
						Work work = nd.getHisWork();
						work.setOID(workid);
						work.RetrieveFromDBSources();
						dtOfShould = DataType.ParseSysDateTime2DateTime(work.GetValByKey(keyOfEn).toString());
					}

				}
			}
		}

		//求警告日期.
		Date dtOfWarning = new Date();
		if (nd.getWarningDay() == 0)
			dtOfWarning = dtOfShould;
		else
			//计算警告日期.
			//增加小时数. 考虑到了节假日.
			dtOfWarning = Glo.AddDayHoursSpan(new Date(), (int)nd.getWarningDay(), 0, 0, nd.getTWay());

		String todoEmpsExts = "";
		for (Emp item : emps.ToJavaList())
		{
			//插入一条信息，让调整的人员显示待办.
			gwl.setWorkID(workid);
			gwl.setEmpNo(item.getNo());
			gwl.setEmpName(item.getName());
			gwl.setNodeID(toNodeID);
			gwl.setPassInt(0);
			gwl.setItIsRead(false);
			gwl.setWhoExeIt(0);
			gwl.setDeptNo(item.getDeptNo());
			gwl.setDeptName(item.getDeptText());
			gwl.setNodeName(nd.getName());
			gwl.setFlowNo(nd.getFlowNo());
			gwl.setItIsEnable(true);
			//应完成日期.
			if (nd.getHisCHWay() == CHWay.None)
				gwl.setSDT("无");
			else
				gwl.setSDT(DateUtils.format(dtOfShould,DataType.getSysDateTimeFormat() + ":ss"));

			//警告日期.
			gwl.setDTOfWarning(DateUtils.format(dtOfWarning,DataType.getSysDateTimeFormat()));
			gwl.Insert();

			todoEmpsExts += item.getUserID() + "," + item.getName() + ";";
		}

		//更新当前节点状态.
		gwf.setNodeID(toNodeID);
		gwf.setNodeName(nd.getName());

		//设置当前的处理人.
		gwf.setTodoEmpsNum(emps.size());
		gwf.setTodoEmps(todoEmpsExts);

		//发送人.
		gwf.setSender(WebUser.getNo() + "," + WebUser.getName() + ";");
		gwf.setSendDT(DataType.getCurrentDateTime());
		gwf.setParasToNodes("");
		if(nd.getItIsStartNode()){
			gwf.setStarter(emp.getNo());
			gwf.setStarterName(emp.getName());
		}
		gwf.Update();

		// 加入track.
		Glo.AddToTrack(ActionType.Adjust, gwf.getFlowNo(), gwf.getWorkID(), gwf.getFID(), curNodeID, curNodeName, WebUser.getNo(), WebUser.getName(), nd.getNodeID(), nd.getName(), gwf.getTodoEmps(), gwf.getTodoEmps(), note, null);

		//修改rpt表数据.
		String sql = "UPDATE " + nd.getHisFlow().getPTable() + " SET  FlowEnder='" + WebUser.getNo() + "',FlowEnderRDT='" + DataType.getCurrentDateTime() + "',FlowEndNode=" + toNodeID + " WHERE OID=" + workid;
		DBAccess.RunSQL(sql);

		return "调整成功,调整到节点:" + gwf.getNodeName() + " , 调整给:" + todoEmpsExts;
	}
	/**
	 取消、确认.

	 @param workid 要取消设置的工作ID
	*/
	public static void Flow_Confirm(long workid) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workid);
		int i = gwf.RetrieveFromDBSources();
		if (i == 0)
		{
			throw new RuntimeException("@ 设置关注错误：没有找到 WorkID= " + workid + " 的实例。");
		}

		String isFocus = gwf.GetParaString("C_" + WebUser.getNo(), "0");

		if (Objects.equals(isFocus, "0"))
		{
			gwf.SetPara("C_" + WebUser.getNo(), "1");
		}
		else
		{
			gwf.SetPara("C_" + WebUser.getNo(), "0");
		}

		gwf.DirectUpdate();
	}
	/**
	 获得工作进度-用于展示流程的进度图

	 @param workID workID
	 @return 返回进度数据
	*/
	public static DataSet DB_JobSchedule(long workID) throws Exception {
		String sql = "";
		DataSet ds = new DataSet();

		/*
		 * 流程控制主表, 可以得到流程状态，停留节点，当前的执行人.
		 * 该表里有如下字段是重点:
		 *  0. WorkID 流程ID.
		 *  1. WFState 字段用于标识当前流程的状态..
		 *  2. FK_Node 停留节点.
		 *  3. NodeName 停留节点名称.
		 *  4. TodoEmps 停留的待办人员.
		 */
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		ds.Tables.add(gwf.ToDataTableField("WF_GenerWorkFlow"));


		/*节点信息: 节点信息表,存储每个环节的节点信息数据.
		 * NodeID 节点ID.
		 * Name 名称.
		 * X,Y 节点图形位置，如果使用进度图就不需要了.
		*/
		NodeSimples nds = new NodeSimples(gwf.getFlowNo());
		ds.Tables.add(nds.ToDataTableField("WF_Node"));

		/*
		 * 节点的连接线.
		 */
		Directions dirs = new Directions(gwf.getFlowNo());
		ds.Tables.add(dirs.ToDataTableField("WF_Direction"));


			///#region 运动轨迹
		/*
		 * 运动轨迹： 构造的一个表，用与存储运动轨迹.
		 *
		 */
		DataTable dtHistory = new DataTable();
		dtHistory.setTableName( "Track");
		dtHistory.Columns.Add("FK_Node"); //节点ID.
		dtHistory.Columns.Add("NodeName"); //名称.
		dtHistory.Columns.Add("RunModel"); //节点类型.
		dtHistory.Columns.Add("EmpNo"); //人员编号.
		dtHistory.Columns.Add("EmpName"); //名称
		dtHistory.Columns.Add("DeptName"); //部门名称
		dtHistory.Columns.Add("RDT"); //记录日期.
		dtHistory.Columns.Add("SDT"); //应完成日期(可以不用.)
		dtHistory.Columns.Add("IsPass"); //是否通过?

		//执行人.

		//历史执行人.
		sql = "SELECT C.Name AS DeptName,A.MyPK,A.ActionType,A.ActionTypeText,A.FID,A.WorkID,A.NDFrom,A.NDFromT,A.NDTo,A.NDToT,A.EmpFrom,A.EmpFromT,A.EmpTo,A.EmpToT,A.RDT,A.WorkTimeSpan,A.Msg,A.NodeData,A.Tag,A.Exer FROM ND" + Integer.parseInt(gwf.getFlowNo()) + "Track A, Port_Emp B, Port_Dept C  ";
		sql += " WHERE (A.WorkID=" + workID + " OR A.FID=" + workID + ") AND (A.ActionType=1 OR A.ActionType=0  OR A.ActionType=6  OR A.ActionType=7) AND (A.EmpFrom=B.getNo()) AND (B.FK_Dept=C.getNo()) ";
		sql += " ORDER BY A.RDT ";

		DataTable dtTrack = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dtTrack.Columns.get("NDFROM").ColumnName = ("NDFrom");
			dtTrack.Columns.get("NDFROMT").ColumnName = "NDFromT";
			dtTrack.Columns.get("EMPFROM").ColumnName = "EmpFrom";
			dtTrack.Columns.get("EMPFROMT").ColumnName = "EmpFromT";
			dtTrack.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dtTrack.Columns.get("RDT").ColumnName = "RDT";

		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dtTrack.Columns.get("ndfrom").ColumnName = "NDFrom";
			dtTrack.Columns.get("ndfromt").ColumnName = "NDFromT";
			dtTrack.Columns.get("empfrom").ColumnName = "EmpFrom";
			dtTrack.Columns.get("empfromt").ColumnName = "EmpFromT";
			dtTrack.Columns.get("deptname").ColumnName = "DeptName";
			dtTrack.Columns.get("rdt").ColumnName = "RDT";

		}

		for (DataRow drTrack : dtTrack.Rows)
		{

			DataRow dr = dtHistory.NewRow();
			dr.setValue("FK_Node", drTrack.get("NDFrom"));
			//dr["ActionType"] = drTrack["NDFrom");
			dr.setValue("NodeName", drTrack.get("NDFromT"));
			dr.setValue("EmpNo", drTrack.get("EmpFrom"));
			dr.setValue("EmpName", drTrack.get("EmpFromT"));
			dr.setValue("DeptName", drTrack.get("DeptName")); //部门名称.
			dr.setValue("RDT", drTrack.get("RDT"));
			dr.setValue("SDT", "");
			dr.setValue("IsPass", 1); // gwl.PassInt; //是否通过.
			dtHistory.Rows.add(dr);
		}

		//如果流程没有完成.
		if (gwf.getIsOver() ==false && 1 == 2)
		{
			DataRow dr = dtHistory.NewRow();
			dr.setValue("FK_Node", gwf.getNodeID());
			//dr["ActionType"] = drTrack["NDFrom");
			dr.setValue("NodeName", gwf.getNodeName());
			dr.setValue("EmpNo", WebUser.getNo());
			dr.setValue("EmpName", WebUser.getName());
			dr.setValue("DeptName", WebUser.getDeptName()); //部门名称.
			dr.setValue("RDT", DataType.getCurrentDate());
			dr.setValue("SDT", "");
			dr.setValue("IsPass", 0); // gwl.PassInt; //是否通过.
			dtHistory.Rows.add(dr);
		}

		if (dtHistory.Rows.size() == 0)
		{
			DataRow dr = dtHistory.NewRow();
			dr.setValue("FK_Node", gwf.getNodeID());
			dr.setValue("NodeName", gwf.getNodeName());
			dr.setValue("EmpNo", gwf.getStarter());
			dr.setValue("EmpName", gwf.getStarterName());
			dr.setValue("RDT", gwf.getRDT());
			dr.setValue("SDT", gwf.getSDTOfNode());
			dtHistory.Rows.add(dr);
		}

		// 给 dtHistory runModel 赋值.
		for (NodeSimple nd : nds.ToJavaList())
		{
			int runMode = nd.GetValIntByKey(NodeAttr.RunModel);
			for (DataRow dr : dtHistory.Rows)
			{
				if (Integer.parseInt(dr.getValue("FK_Node").toString()) == nd.getNodeID())
				{
					dr.setValue("RunModel", runMode);
				}
			}
		}
		ds.Tables.add(dtHistory);

			///#endregion 运动轨迹


			///#region 游离态
		TransferCustoms tranfs = new TransferCustoms(workID);
		ds.Tables.add(tranfs.ToDataTableField("WF_TransferCustom"));

			///#endregion 游离态

		return ds;
	}
	/**
	 获取当前登录人的委托人

	 @return
	*/
	public static DataTable DB_AuthorEmps()
	{
		if (WebUser.getNo() == null)
		{
			throw new RuntimeException("@ 非法用户，请执行登录后再试。");
		}

		return DBAccess.RunSQLReturnTable("SELECT * FROM WF_EMP WHERE AUTHOR='" + WebUser.getNo() + "'");
	}
	/**
	 获取委托给当前登录人的流程待办信息

	 @param empNo 授权人员编号
	 @return
	*/
	public static DataTable DB_AuthorEmpWorks(String empNo)
	{
		//if (WebUser.getNo() == null)
		//    throw new Exception("@ 非法用户，请执行登录后再试。");

		//WF.Port.WFEmp emp = new bp.port.WFEmp(empNo);
		//if (!DataType.IsNullOrEmpty(emp.Author) && emp.Author == WebUser.No && emp.AuthorIsOK == true)
		//{
		//    String sql = "";
		//    String wfSql = "  WFState=" + (int)WFState.Askfor + " OR WFState=" + (int)WFState.Runing + "  OR WFState=" + (int)WFState.AskForReplay + " OR WFState=" + (int)WFState.Shift + " OR WFState=" + (int)WFState.ReturnSta + " OR WFState=" + (int)WFState.Fix;
		//    switch (emp.HisAuthorWay)
		//    {
		//        case Port.AuthorWay.All:
		//            if (BP.WF.Glo.IsEnableTaskPool == true)
		//            {
		//                sql = "SELECT * FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp='" + emp.getNo() + "' AND TaskSta!=1  ORDER BY ADT DESC";
		//            }
		//            else
		//            {
		//                sql = "SELECT * FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp='" + emp.getNo() + "' ORDER BY ADT DESC";
		//            }

		//            break;
		//        case Port.AuthorWay.SpecFlows:
		//            if (BP.WF.Glo.IsEnableTaskPool == true)
		//            {
		//                sql = "SELECT * FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp='" + emp.getNo() + "' AND  FK_Flow IN " + emp.AuthorFlows + " AND TaskSta!=0    ORDER BY ADT DESC";
		//            }
		//            else
		//            {
		//                sql = "SELECT * FROM WF_EmpWorks WHERE (" + wfSql + ") AND FK_Emp='" + emp.getNo() + "' AND  FK_Flow IN " + emp.AuthorFlows + "   ORDER BY ADT DESC";
		//            }

		//            break;
		//    }
		//    return DBAccess.RunSQLReturnTable(sql);
		//}
		return null;
	}

		///#endregion 与流程有关的接口



		///#endregion 与流程有关的接口


		///#region get 属性节口
	/**
	 获得流程运行过程中的参数

	 @param nodeID 节点ID
	 @param workid 工作ID
	 @return 如果没有就返回null,有就返回@参数名0=参数值0@参数名1=参数值1
	*/
	public static String GetFlowParas(int nodeID, long workid) throws Exception {
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "SELECT Paras FROM WF_GenerWorkerlist WHERE FK_Node=" + dbstr + "FK_Node AND WorkID=" + dbstr + "WorkID";
		ps.Add(GenerWorkerListAttr.FK_Node, nodeID);
		ps.Add(GenerWorkerListAttr.WorkID, workid);
		return DBAccess.RunSQLReturnStringIsNull(ps, null);
	}

		///#endregion get 属性节口


		///#region 工作有关接口
	/**
	 发起流程

	 @param flowNo 流程编号
	 @param ht 节点表单:主表数据以Key Value 方式传递(可以为空)
	 @param workDtls 节点表单:从表数据，从表名称与从表单的从表编号要对应(可以为空)
	 @param nextNodeID 发起后要跳转到的节点(可以为空)
	 @param nextWorker 发起后要跳转到的节点并指定的工作人员(可以为空)
	 @return 发送到第二个节点的执行信息
	*/
	public static SendReturnObjs Node_StartWork(String flowNo, Hashtable ht, DataSet workDtls, int nextNodeID, String nextWorker) throws Exception {
		return Node_StartWork(flowNo, ht, workDtls, nextNodeID, nextWorker, 0, null);
	}
	/**
	 发起流程

	 @param flowNo 流程编号
	 @param htWork 节点表单:主表数据以Key Value 方式传递(可以为空)
	 @param workDtls 节点表单:从表数据，从表名称与从表单的从表编号要对应(可以为空)
	 @param nextNodeID 发起后要跳转到的节点(可以为空)
	 @param nextWorker 发起后要跳转到的节点并指定的工作人员(可以为空)
	 @param parentWorkID 父流程的workid，如果没有可以为0
	 @param parentFlowNo 父流程的编号，如果没有可以为空
	 @return 发送到第二个节点的执行信息
	*/
	public static SendReturnObjs Node_StartWork(String flowNo, Hashtable htWork, DataSet workDtls, int nextNodeID, String nextWorker, long parentWorkID, String parentFlowNo) throws Exception {
		Flow fl = new Flow(flowNo);
		Work wk = fl.NewWork();
		long workID = wk.getOID();
		if (htWork != null)
		{
			for (Object str : htWork.keySet())
			{
				switch (str.toString())
				{
					case GERptAttr.OID:
					case WorkAttr.MD5:
					case WorkAttr.Emps:
					case GERptAttr.FID:
					case GERptAttr.FK_Dept:
					case GERptAttr.Rec:
					case GERptAttr.Title:
						continue;
					default:
						break;
				}
				wk.SetValByKey(str.toString(), htWork.get(str));
			}
		}

		wk.setOID(workID);
		if (workDtls != null)
		{
			//保存从表
			for (DataTable dt : workDtls.Tables)
			{
				for (MapDtl dtl : wk.getHisMapDtls().ToJavaList())
				{
					if (!Objects.equals(dt.TableName, dtl.getNo()))
					{
						continue;
					}

					//获取dtls
					GEDtls daDtls = new GEDtls(dtl.getNo());
					daDtls.Delete(GEDtlAttr.RefPK, wk.getOID()); // 清除现有的数据.

					bp.en.Entity tempVar = daDtls.getNewEntity();
					GEDtl daDtl = tempVar instanceof GEDtl ? (GEDtl)tempVar : null;
					daDtl.setRefPK(String.valueOf(wk.getOID()));

					// 为从表复制数据.
					for (DataRow dr : dt.Rows)
					{
						try {
							daDtl.ResetDefaultVal(null, null, 0);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}

						//明细列.
						for (DataColumn dc : dt.Columns)
						{
							//设置属性.
							daDtl.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName));
						}

						daDtl.setRefPK(String.valueOf(wk.getOID()));
						daDtl.Insert(); //插入数据.
					}
				}
			}
		}

		WorkNode wn = null;
		try {
			wn = new WorkNode(wk, fl.getHisStartNode());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Node nextNoode = null;
		if (nextNodeID != 0)
		{
			nextNoode = new Node(nextNodeID);
		}

		SendReturnObjs objs = wn.NodeSend(nextNoode, nextWorker);
		if (parentWorkID != 0)
		{
			DBAccess.RunSQL("UPDATE WF_GenerWorkFlow SET PWorkID=" + parentWorkID + ",PFlowNo='" + parentFlowNo + "' WHERE WorkID=" + objs.getVarWorkID());
		}


			///#region 更新发送参数.
		if (htWork != null)
		{
			String paras = "";
			for (Object key : htWork.keySet())
			{
				paras += "@" + key + "=" + htWork.get(key).toString();
			}

			if (DataType.IsNullOrEmpty(paras) == false && Glo.isEnableTrackRec() == true)
			{
				String dbstr = SystemConfig.getAppCenterDBVarStr();
				Paras ps = new Paras();
				ps.SQL = "UPDATE WF_GenerWorkerlist SET AtPara=" + dbstr + "Paras WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + dbstr + "FK_Node";
				ps.Add(GenerWorkerListAttr.Paras, paras, false);
				ps.Add(GenerWorkerListAttr.WorkID, workID);
				ps.Add(GenerWorkerListAttr.FK_Node, Integer.parseInt(flowNo + "01"));
				try
				{
					DBAccess.RunSQL(ps);
				}
				catch (java.lang.Exception e)
				{
					GenerWorkerList gwl = new GenerWorkerList();
					try {
						gwl.CheckPhysicsTable();
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					}
					DBAccess.RunSQL(ps);
				}
			}
		}

			///#endregion 更新发送参数.

		return objs;
	}


	/**
	 创建一个空白的 WorkID

	 @param flowNo 流程编号
	 @param userNo 用户编号
	 @return 执行结果
	*/

	public static long Node_CreateBlankWork(String flowNo, String userNo) throws Exception {
		return Node_CreateBlankWork(flowNo, userNo, 0);
	}

	public static long Node_CreateBlankWork(String flowNo, String userNo, long oldWorkID) throws Exception {
		long workid = Node_CreateBlankWork(flowNo, null, null, userNo);
		if (oldWorkID == 0)
		{
			return workid;
		}

		//复制开始节点表单的数据.
		Node nd = new Node(Integer.parseInt(flowNo + "01"));
		Work wk = nd.getHisWork();
		wk.setOID(oldWorkID);
		wk.Retrieve();
		wk.setOID(workid);
		wk.Update();

		//复制从表数据.

		//复制附件数据.

		return workid;
	}
	/**
	 把流程模板标记，转化为

	 @param flowNoOrFlowMark
	 @return
	*/
	public static String Flow_TurnFlowMark2FlowNo(String flowNoOrFlowMark)
	{
		if (DataType.IsNullOrEmpty(flowNoOrFlowMark))
			return flowNoOrFlowMark;

		if (DataType.IsNumStr(flowNoOrFlowMark))
			return flowNoOrFlowMark;

		String sql = "";
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
			sql = "SELECT No FROM WF_Flow WHERE FlowMark='" + flowNoOrFlowMark + "'  AND IsCanStart=1";
		else
			sql = "SELECT No FROM WF_Flow WHERE FlowMark='" + flowNoOrFlowMark + "' AND OrgNo='" + WebUser.getOrgNo() + "'  AND IsCanStart=1";
		//return DBAccess.RunSQLReturnStringIsNull(sql, "err@流程标记[" + flowNoOrFlowMark + "]不存在.");
		flowNoOrFlowMark= DBAccess.RunSQLReturnStringIsNull(sql, flowNoOrFlowMark);
		if (DataType.IsNumStr(flowNoOrFlowMark))
			return flowNoOrFlowMark;

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
			sql = "SELECT No FROM WF_Flow WHERE FlowMark='" + flowNoOrFlowMark + "'  ";
		else
			sql = "SELECT No FROM WF_Flow WHERE FlowMark='" + flowNoOrFlowMark + "' AND OrgNo='" + WebUser.getOrgNo() + "'   ";
		return DBAccess.RunSQLReturnStringIsNull(sql, flowNoOrFlowMark);
	}
	/**
	 创建WorkID

	 @param flowNo 流程编号
	 @param ht 表单参数，可以为null。
	 @param workDtls 明细表参数，可以为null。
	 @param starter 流程的发起人
	 @param title 创建工作时的标题，如果为null，就按设置的规则生成。
	 @param parentWorkID 父流程的WorkID,如果没有父流程就传入为0.
	 @param parentFID 父流程的FID,如果没有父流程就传入为0.
	 @param parentFlowNo 父流程的流程编号,如果没有父流程就传入为null.
	 @param jumpToNode 要跳转到的节点,如果没有则为0.
	 @param jumpToEmp 要跳转到的人员,如果没有则为null.
	 @param todoEmps 待办人员,如果没有则为null.
	 @return 为开始节点创建工作后产生的WorkID.
	*/

	public static long Node_CreateBlankWork(String flowNo, java.util.Hashtable ht, DataSet workDtls, String starter, String title, long parentWorkID, long parentFID, String parentFlowNo, int parentNodeID, String parentEmp, int jumpToNode, String jumpToEmp, String todoEmps) throws Exception {
		return Node_CreateBlankWork(flowNo, ht, workDtls, starter, title, parentWorkID, parentFID, parentFlowNo, parentNodeID, parentEmp, jumpToNode, jumpToEmp, todoEmps, null);
	}

	public static long Node_CreateBlankWork(String flowNo, java.util.Hashtable ht, DataSet workDtls, String starter, String title, long parentWorkID, long parentFID, String parentFlowNo, int parentNodeID, String parentEmp, int jumpToNode, String jumpToEmp) throws Exception {
		return Node_CreateBlankWork(flowNo, ht, workDtls, starter, title, parentWorkID, parentFID, parentFlowNo, parentNodeID, parentEmp, jumpToNode, jumpToEmp, null, null);
	}

	public static long Node_CreateBlankWork(String flowNo, java.util.Hashtable ht, DataSet workDtls, String starter, String title, long parentWorkID, long parentFID, String parentFlowNo, int parentNodeID, String parentEmp, int jumpToNode) throws Exception {
		return Node_CreateBlankWork(flowNo, ht, workDtls, starter, title, parentWorkID, parentFID, parentFlowNo, parentNodeID, parentEmp, jumpToNode, null, null, null);
	}

	public static long Node_CreateBlankWork(String flowNo, java.util.Hashtable ht, DataSet workDtls, String starter, String title, long parentWorkID, long parentFID, String parentFlowNo, int parentNodeID, String parentEmp) throws Exception {
		return Node_CreateBlankWork(flowNo, ht, workDtls, starter, title, parentWorkID, parentFID, parentFlowNo, parentNodeID, parentEmp, 0, null, null, null);
	}

	public static long Node_CreateBlankWork(String flowNo, java.util.Hashtable ht, DataSet workDtls, String starter, String title, long parentWorkID, long parentFID, String parentFlowNo, int parentNodeID) throws Exception {
		return Node_CreateBlankWork(flowNo, ht, workDtls, starter, title, parentWorkID, parentFID, parentFlowNo, parentNodeID, null, 0, null, null, null);
	}

	public static long Node_CreateBlankWork(String flowNo, java.util.Hashtable ht, DataSet workDtls, String starter, String title, long parentWorkID, long parentFID, String parentFlowNo) throws Exception {
		return Node_CreateBlankWork(flowNo, ht, workDtls, starter, title, parentWorkID, parentFID, parentFlowNo, 0, null, 0, null, null, null);
	}

	public static long Node_CreateBlankWork(String flowNo, java.util.Hashtable ht, DataSet workDtls, String starter, String title, long parentWorkID, long parentFID) throws Exception {
		return Node_CreateBlankWork(flowNo, ht, workDtls, starter, title, parentWorkID, parentFID, null, 0, null, 0, null, null, null);
	}

	public static long Node_CreateBlankWork(String flowNo, java.util.Hashtable ht, DataSet workDtls, String starter, String title, long parentWorkID) throws Exception {
		return Node_CreateBlankWork(flowNo, ht, workDtls, starter, title, parentWorkID, 0, null, 0, null, 0, null, null, null);
	}

	public static long Node_CreateBlankWork(String flowNo, java.util.Hashtable ht, DataSet workDtls, String starter, String title) throws Exception {
		return Node_CreateBlankWork(flowNo, ht, workDtls, starter, title, 0, 0, null, 0, null, 0, null, null, null);
	}

	public static long Node_CreateBlankWork(String flowNo, java.util.Hashtable ht, DataSet workDtls, String starter) throws Exception {
		return Node_CreateBlankWork(flowNo, ht, workDtls, starter, null, 0, 0, null, 0, null, 0, null, null, null);
	}

	public static long Node_CreateBlankWork(String flowNo, java.util.Hashtable ht, DataSet workDtls) throws Exception {
		return Node_CreateBlankWork(flowNo, ht, workDtls, null, null, 0, 0, null, 0, null, 0, null, null, null);
	}

	public static long Node_CreateBlankWork(String flowNo, java.util.Hashtable ht) throws Exception {
		return Node_CreateBlankWork(flowNo, ht, null, null, null, 0, 0, null, 0, null, 0, null, null, null);
	}

	public static long Node_CreateBlankWork(String flowNo) throws Exception {
		return Node_CreateBlankWork(flowNo, null, null, null, null, 0, 0, null, 0, null, 0, null, null, null);
	}

	public static long Node_CreateBlankWork(String flowNo, Hashtable ht, DataSet workDtls, String starter, String title, long parentWorkID, long parentFID, String parentFlowNo, int parentNodeID, String parentEmp, int jumpToNode, String jumpToEmp, String todoEmps, String isStartSameLevelFlow) throws Exception {

		flowNo = Flow_TurnFlowMark2FlowNo(flowNo);
		parentFlowNo = Flow_TurnFlowMark2FlowNo(parentFlowNo);

		//把一些其他的参数也增加里面去,传递给ccflow.
		Hashtable htPara = new Hashtable();
		if(ht == null)
			ht =  new Hashtable();

		if (parentWorkID != 0)
		{
			ht.put(StartFlowParaNameList.PWorkID, parentWorkID);
			ht.put(StartFlowParaNameList.PFID, parentFID);
			ht.put(StartFlowParaNameList.PFlowNo, parentFlowNo);
			ht.put(StartFlowParaNameList.PNodeID, parentNodeID);
			if(DataType.IsNullOrEmpty(parentEmp) == true)
				parentEmp = WebUser.getNo();
			ht.put(StartFlowParaNameList.PEmp, parentEmp);
		}

		String dbstr = SystemConfig.getAppCenterDBVarStr();
		if (DataType.IsNullOrEmpty(starter))
		{
			starter = WebUser.getNo();
		}

		Flow fl = new Flow(flowNo);
		Node nd = new Node(fl.getStartNodeID());

		// 下一个工作人员。
		Emp empStarter = new Emp(starter);
		if (starter.equals(WebUser.getNo()))
		{
			empStarter.setDeptNo(WebUser.getDeptNo());
			empStarter.SetValByKey("FK_DeptText", WebUser.getDeptName());
		}
		Work wk = fl.NewWork(empStarter, ht);
		long workID = wk.getOID();


			///#region 给各个属性-赋值
		wk.setOID(workID);
		if (workDtls != null)
		{
			//保存从表
			for (DataTable dt : workDtls.Tables)
			{
				for (MapDtl dtl : wk.getHisMapDtls().ToJavaList())
				{
					if (!Objects.equals(dt.TableName, dtl.getNo()))
					{
						continue;
					}

					//获取dtls
					GEDtls daDtls = new GEDtls(dtl.getNo());
					daDtls.Delete(GEDtlAttr.RefPK, wk.getOID()); // 清除现有的数据.

					bp.en.Entity tempVar = daDtls.getNewEntity();
					GEDtl daDtl = tempVar instanceof GEDtl ? (GEDtl)tempVar : null;
					daDtl.setRefPK(String.valueOf(wk.getOID()));

					// 为从表复制数据.
					for (DataRow dr : dt.Rows)
					{
						try {
							daDtl.ResetDefaultVal(null, null, 0);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						daDtl.setRefPK(String.valueOf(wk.getOID()));

						//明细列.
						for (DataColumn dc : dt.Columns)
						{
							//设置属性.
							daDtl.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName));
						}
						daDtl.InsertAsOID(DBAccess.GenerOID("Dtl")); //插入数据.
					}
				}
			}
		}

			///#endregion 赋值

		Paras ps = new Paras();
		if (DataType.IsNullOrEmpty(title) == true)
		{
			title = bp.wf.WorkFlowBuessRole.GenerTitle(fl, wk);
			if (title.contains("@") == true)
			{
				GERpt rptGe = fl.getHisGERpt();
				rptGe.setOID(wk.getOID());
				rptGe.Retrieve();
				title = bp.wf.WorkFlowBuessRole.GenerTitle(fl, rptGe);
			}
		}


		//执行对报表的数据表WFState状态的更新,让它为runing的状态.
		ps = new Paras();
		ps.SQL = "UPDATE " + fl.getPTable() + " SET WFState=0, Title=" + dbstr + "Title WHERE OID=" + dbstr + "OID";
		//ps.Add(GERptAttr.FK_Dept, empStarter.DeptNo);
		ps.Add(GERptAttr.Title, title, false);
		ps.Add(GERptAttr.OID, wk.getOID());
		DBAccess.RunSQL(ps);

		// 设置父流程信息.
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(wk.getOID());
		int i = gwf.RetrieveFromDBSources();

		//将流程信息提前写入wf_GenerWorkFlow,避免查询不到
		gwf.setFlowName(fl.getName());
		gwf.setFlowNo(flowNo);
		gwf.setFlowSortNo(fl.getFlowSortNo());
		gwf.setSysType(fl.getSysType());

		gwf.setDeptNo(empStarter.getDeptNo());
		gwf.setDeptName(empStarter.getDeptText());
		gwf.setNodeID(fl.getStartNodeID());
		gwf.setNodeName(nd.getName());
		gwf.setWFState(WFState.Blank);
		gwf.setTitle(title);

		gwf.setStarter(empStarter.getUserID());
		gwf.setStarterName(empStarter.getName());
		gwf.setRDT(DataType.getCurrentDateTimess());
		gwf.setPWorkID(parentWorkID);
		gwf.setPFID(parentFID);
		gwf.setPFlowNo(parentFlowNo);
		gwf.setPNodeID(parentNodeID);

		if (fl.getItIsDBTemplate() == true)
		{
			gwf.setParasDBTemplate(true);
		}

		gwf.SetPara("CreateRDT", DataType.getCurrentDateTimess());//创建时间

		if (i == 0)
			gwf.Insert(); //插入.
		else
			gwf.Update();
		GenerWorkerList gwl = new GenerWorkerList();
		i=gwl.Retrieve(GenerWorkerListAttr.WorkID, gwf.getWorkID(), GenerWorkerListAttr.FK_Node, gwf.getNodeID(), GenerWorkerListAttr.FK_Emp, WebUser.getNo());
		if(i == 0)
		{
			gwl.setWorkID(gwf.getWorkID());
			gwl.setEmpNo(gwf.getStarter());
			gwl.setNodeID(gwf.getNodeID());
			gwl.setNodeName(gwf.getNodeName());
			gwl.setDeptNo(WebUser.getDeptNo());
			gwl.setDeptName(WebUser.getDeptName());
			gwl.setEmpName(WebUser.getName());
			gwl.setItIsEnable(true);
			gwl.setItIsRead(true);
			gwl.setItIsPass(false);
			gwl.setFlowNo(flowNo);
			gwl.Insert();

		}

		//更新 domian.
		DBAccess.RunSQL("UPDATE WF_GenerWorkFlow  SET DomainExt=(SELECT DomainExt FROM WF_FlowSort WHERE WF_FlowSort.No=WF_GenerWorkFlow.FK_FlowSort) WHERE WorkID=" + wk.getOID());

		if (parentWorkID != 0)
		{
			try {
				Dev2Interface.SetParentInfo(flowNo, wk.getOID(), parentWorkID); //设置父流程信息
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}


///#warning 增加是防止手动启动子流程或者平级子流程时关闭子流程页面找不到待办 保存到待办。

		if (isStartSameLevelFlow != null)
		{
			bp.wf.Dev2Interface.Node_SaveWork(wk.getOID(), null);
		}
		// 如果有跳转.
		if (jumpToNode != 0)
		{
			bp.wf.Dev2Interface.Node_SendWork(flowNo, wk.getOID(), null, null, jumpToNode, jumpToEmp);
		}
		return wk.getOID();
	}
	/**
	 增加待办人员

	 @param workid 工作ID
	 @param todoEmps 要增加的处理人员,多个人员用逗号分开.
	*/
	public static void Node_AddTodolist(long workid, String todoEmps) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(workid);
		if (gwf.getIsOver() == true)
		{
			throw new RuntimeException("流程：" + gwf.getTitle() + "已经完成,您不能增加接受人.");
		}

			///#region 增加待办人员.

		GenerWorkerList gwl = new GenerWorkerList();
		gwl.Retrieve(GenerWorkerListAttr.WorkID, workid, GenerWorkerListAttr.FK_Node, gwf.getNodeID());

		String[] empStrs = todoEmps.split("[,]", -1); //分开字符串.
		String tempStrs = ""; //临时变量，防止重复插入.
		for (String emp : empStrs)
		{
			if (DataType.IsNullOrEmpty(emp) == true)
			{
				continue;
			}
			if (tempStrs.contains("," + emp + ",") == true)
			{
				continue;
			}

			//插入待办.
			gwl = new GenerWorkerList();
			gwl.setWorkID(workid);
			gwl.setNodeID(gwf.getNodeID());
			gwl.setEmpNo(emp);
			int i = 0;
			try {
				i = gwl.RetrieveFromDBSources();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (i == 1)
			{
				continue;
			}

			Emp empEn = new Emp(emp);

			gwl.setEmpName(empEn.getName());
			gwl.setNodeName(gwf.getNodeName());
			gwl.setFID(0);
			gwl.setFlowNo(gwf.getFlowNo());
			gwl.setDeptNo(empEn.getDeptNo());
			gwl.setDeptName(empEn.getDeptText());

			gwl.setSDT("无");
			gwl.setDTOfWarning(DataType.getCurrentDateTime());
			gwl.setItIsEnable(true);
			gwl.setItIsPass(false);
			gwl.setPRI(gwf.getPRI());
			try {
				gwl.Insert();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			tempStrs += "," + emp + ",";
		}

			///#endregion 增加待办人员.

		if (gwf.getWFState() == WFState.Blank)
		{
			gwf.setWFState(WFState.Runing);
			gwf.Update();
		}
	}

	/**
	 移除待办人员

	 @param workid 工作ID
	 @param empNo 要移除的处理人员.
	 */
	public static void Node_RemoveTodolist(long workid, String empNo) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(workid);
		if (gwf.getIsOver() == true)
		{
			throw new RuntimeException("流程：" + gwf.getTitle() + "已经完成,您不能增加接受人.");
		}

		GenerWorkerLists gwls = new GenerWorkerLists();
		gwls.Retrieve(GenerWorkerListAttr.WorkID, workid, GenerWorkerListAttr.FK_Node, gwf.getNodeID(),GenerWorkerListAttr.IsPass,0);
		if(gwls.size() == 1)
			throw new RuntimeException("err@当前的节点未处理的待办人员只有一个，不能移除");

		//删除当前人员
		GenerWorkerList gwl = new GenerWorkerList();
		gwl.Delete(GenerWorkerListAttr.WorkID, workid, GenerWorkerListAttr.FK_Node, gwf.getNodeID(),GenerWorkerListAttr.FK_Emp,empNo);

	}
	/**
	 创建开始节点工作
	 创建后可以创办人形成一个待办.

	 @param flowNo 流程编号
	 @param htWork 表单参数，可以为null。
	 @param workDtls 明细表参数，可以为null。
	 @param flowStarter 流程的发起人，如果为null就是当前人员。
	 @param title 创建工作时的标题，如果为null，就按设置的规则生成。
	 @param parentWorkID 父流程的WorkID,如果没有父流程就传入为0.
	 @param parentFlowNo 父流程的流程编号,如果没有父流程就传入为null.
	 @return 为开始节点创建工作后产生的WorkID.
	*/

	public static long Node_CreateStartNodeWork(String flowNo, java.util.Hashtable htWork, DataSet workDtls, String flowStarter, String title, long parentWorkID, String parentFlowNo) throws Exception {
		return Node_CreateStartNodeWork(flowNo, htWork, workDtls, flowStarter, title, parentWorkID, parentFlowNo, 0);
	}

	public static long Node_CreateStartNodeWork(String flowNo, java.util.Hashtable htWork, DataSet workDtls, String flowStarter, String title, long parentWorkID) throws Exception {
		return Node_CreateStartNodeWork(flowNo, htWork, workDtls, flowStarter, title, parentWorkID, null, 0);
	}

	public static long Node_CreateStartNodeWork(String flowNo, java.util.Hashtable htWork, DataSet workDtls, String flowStarter, String title) throws Exception {
		return Node_CreateStartNodeWork(flowNo, htWork, workDtls, flowStarter, title, 0, null, 0);
	}

	public static long Node_CreateStartNodeWork(String flowNo, java.util.Hashtable htWork, DataSet workDtls, String flowStarter) throws Exception {
		return Node_CreateStartNodeWork(flowNo, htWork, workDtls, flowStarter, null, 0, null, 0);
	}

	public static long Node_CreateStartNodeWork(String flowNo, java.util.Hashtable htWork, DataSet workDtls) throws Exception {
		return Node_CreateStartNodeWork(flowNo, htWork, workDtls, null, null, 0, null, 0);
	}

	public static long Node_CreateStartNodeWork(String flowNo, java.util.Hashtable htWork) throws Exception {
		return Node_CreateStartNodeWork(flowNo, htWork, null, null, null, 0, null, 0);
	}

	public static long Node_CreateStartNodeWork(String flowNo) throws Exception {
		return Node_CreateStartNodeWork(flowNo, null, null, null, null, 0, null, 0);
	}

	public static long Node_CreateStartNodeWork(String flowNo, Hashtable htWork, DataSet workDtls, String flowStarter, String title, long parentWorkID, String parentFlowNo, int parentNDFrom) throws Exception {

		if (DataType.IsNullOrEmpty(flowStarter))
		{
			flowStarter = WebUser.getNo();
		}

		Flow fl = new Flow(flowNo);
		Node nd = new Node(fl.getStartNodeID());

		// 下一个工作人员。
		Emp emp = new Emp(flowStarter);
		Work wk = fl.NewWork(flowStarter);


			///#region 给各个属性-赋值
		if (htWork != null)
		{
			for (Object str : htWork.keySet())
			{
				if(str == null)
					continue;
				switch (str.toString())
				{
					case GERptAttr.OID:
					case WorkAttr.MD5:
					case GERptAttr.FID:
					case GERptAttr.FK_Dept:
					case GERptAttr.Rec:
					case GERptAttr.Title:
						continue;
					default:
						break;
				}
				wk.SetValByKey(str.toString(), htWork.get(str));
			}
			//将参数保存到业务表
			try {
				wk.DirectUpdate();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		if (workDtls != null)
		{
			//保存从表
			for (DataTable dt : workDtls.Tables)
			{
				for (MapDtl dtl : wk.getHisMapDtls().ToJavaList())
				{
					if (!Objects.equals(dt.TableName, dtl.getNo()))
					{
						continue;
					}

					//获取dtls
					GEDtls daDtls = new GEDtls(dtl.getNo());
					daDtls.Delete(GEDtlAttr.RefPK, wk.getOID()); // 清除现有的数据.

					bp.en.Entity tempVar = daDtls.getNewEntity();
					GEDtl daDtl = tempVar instanceof GEDtl ? (GEDtl)tempVar : null;
					daDtl.setRefPK(String.valueOf(wk.getOID()));

					// 为从表复制数据.
					for (DataRow dr : dt.Rows)
					{
						try {
							daDtl.ResetDefaultVal(null, null, 0);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						daDtl.setRefPK(String.valueOf(wk.getOID()));

						//明细列.
						for (DataColumn dc : dt.Columns)
						{
							//设置属性.
							daDtl.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName));
						}
						daDtl.InsertAsOID(DBAccess.GenerOID("Dtl")); //插入数据.
					}
				}
			}
		}

		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(wk.getOID());
		int i = 0;
		try {
			i = gwf.RetrieveFromDBSources();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		gwf.setFlowName(fl.getName());
		gwf.setFlowNo(flowNo);
		gwf.setFlowSortNo(fl.getFlowSortNo());
		gwf.setSysType(fl.getSysType());

		gwf.setDeptNo(emp.getDeptNo());
		gwf.setDeptName(emp.getDeptText());
		gwf.setNodeID(fl.getStartNodeID());

		gwf.setNodeName(nd.getName());

		//默认是空白流程
		//gwf.WFSta = WFSta.Etc;
		gwf.setWFState(WFState.Blank);
		//保存到草稿
		if (fl.getDraftRole() == DraftRole.SaveToDraftList)
		{
			gwf.setWFState(WFState.Draft);
		}
		else if (fl.getDraftRole() == DraftRole.SaveToTodolist)
		{
			//保存到待办
			//  gwf.WFSta = WFSta.Runing;
			gwf.setWFState(WFState.Runing);
		}

		if (DataType.IsNullOrEmpty(title))
		{
			gwf.setTitle(bp.wf.WorkFlowBuessRole.GenerTitle(fl, wk));
		}
		else
		{
			gwf.setTitle(title);
		}

		gwf.setStarter(emp.getUserID());
		gwf.setStarterName(emp.getName());
		gwf.setRDT(DataType.getCurrentDateTimess());

		if (htWork != null && htWork.containsKey("PRI") == true)
		{
			gwf.setPRI(Integer.parseInt(htWork.get("PRI").toString()));
		}

		if (htWork != null && htWork.containsKey("SDTOfNode") == true)
		{
			/*节点应完成时间*/
			gwf.setSDTOfNode(htWork.get("SDTOfNode").toString());
		}

		if (htWork != null && htWork.containsKey("SDTOfFlow") == true)
		{
			/*流程应完成时间*/
			gwf.setSDTOfNode(htWork.get("SDTOfFlow").toString());
		}

		gwf.setPWorkID(parentWorkID);
		gwf.setPFlowNo(parentFlowNo);
		gwf.setPNodeID(parentNDFrom);
		if (i == 0)
		{
			gwf.Insert();
		}
		else
		{
			gwf.Update();
		}

		// 产生工作列表.
		GenerWorkerList gwl = new GenerWorkerList();
		gwl.setWorkID(wk.getOID());
		if (gwl.RetrieveFromDBSources() == 0)
		{
			gwl.setEmpNo(emp.getUserID());
			gwl.setEmpName(emp.getName());

			gwl.setNodeID(nd.getNodeID());
			gwl.setNodeName(nd.getName());
			gwl.setFID(0);

			gwl.setFlowNo(fl.getNo());
			gwl.setDeptNo(emp.getDeptNo());
			gwl.setDeptName(emp.getDeptText());


			gwl.setSDT("无");
			gwl.setDTOfWarning(DataType.getCurrentDateTime());
			gwl.setItIsEnable(true);

			gwl.setItIsPass(false);
			//     gwl.Sender = WebUser.getNo();
			gwl.setPRI(gwf.getPRI());
			try {
				gwl.Insert();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

			///#endregion 为开始工作创建待办

		// 执行对报表的数据表WFState状态的更新
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "UPDATE " + fl.getPTable() + " SET WFState=" + dbstr + "WFState,WFSta=" + dbstr + "WFSta,Title=" + dbstr + "Title,FK_Dept=" + dbstr + "FK_Dept,PFlowNo=" + dbstr + "PFlowNo,PWorkID=" + dbstr + "PWorkID WHERE OID=" + dbstr + "OID";

		//默认启用草稿.
		if (fl.getDraftRole() == DraftRole.None)
		{
			ps.Add("WFState", WFState.Blank.getValue());
			ps.Add("WFSta", WFSta.Etc.getValue());
		}
		else if (fl.getDraftRole() == DraftRole.SaveToDraftList)
		{
			//保存到草稿
			ps.Add("WFState", WFState.Draft.getValue());
			ps.Add("WFSta", WFSta.Etc.getValue());
		}
		else if (fl.getDraftRole() == DraftRole.SaveToTodolist)
		{
			//保存到待办
			ps.Add("WFState", WFState.Runing.getValue());
			ps.Add("WFSta", WFSta.Runing.getValue());
		}
		ps.Add("Title", gwf.getTitle(), false);
		ps.Add("FK_Dept", gwf.getDeptNo(), false);

		ps.Add("PFlowNo", gwf.getPFlowNo(), false);
		ps.Add("PWorkID", gwf.getPWorkID());

		ps.Add("OID", wk.getOID());
		DBAccess.RunSQL(ps);

		////写入日志.
		//WorkNode wn = new WorkNode(wk, nd);
		//wn.AddToTrack(ActionType.CallSubFlow, flowStarter, emp.getName(), nd.getNodeID(), nd.Name, "来自" + WebUser.getNo() + "," + WebUser.getName()
		//    + "工作发起.");


			///#region 更新发送参数.
		if (htWork != null)
		{
			String paras = "";
			for (Object key : htWork.keySet())
			{
				paras += "@" + key + "=" + htWork.get(key).toString();
			}

			if (DataType.IsNullOrEmpty(paras) == false && Glo.isEnableTrackRec() == true)
			{
				ps = new Paras();
				ps.SQL = "UPDATE WF_GenerWorkerlist SET AtPara=" + dbstr + "Paras WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + dbstr + "FK_Node";
				ps.Add(GenerWorkerListAttr.Paras, paras, false);
				ps.Add(GenerWorkerListAttr.WorkID, wk.getOID());
				ps.Add(GenerWorkerListAttr.FK_Node, nd.getNodeID());
				DBAccess.RunSQL(ps);
			}
		}

			///#endregion 更新发送参数.

		return wk.getOID();
	}
	/**
	 执行工作发送

	 @param fk_flow 工作编号
	 @param workID 工作ID
	 @param ht 节点表单数据
	 @return 返回发送结果
	*/

	public static SendReturnObjs Node_SendWork(String fk_flow, long workID, java.util.Hashtable ht) throws Exception {
		return Node_SendWork(fk_flow, workID, ht, null);
	}

	public static SendReturnObjs Node_SendWork(String fk_flow, long workID) throws Exception {
		return Node_SendWork(fk_flow, workID, null, null);
	}

	public static SendReturnObjs Node_SendWork(String fk_flow, long workID, Hashtable ht, DataSet dsDtl) throws Exception {
		return Node_SendWork(fk_flow, workID, ht, dsDtl, 0, null);
	}
	/**
	 发送工作
	 @param workID 工作ID
	 @param toNodeID 发送到的节点编号，如果是0就让ccflow自动计算.
	 @param toEmps 发送到的人员,多个人员用逗号分开比如：zhangsan,lisi. 如果是null则表示让ccflow自动计算.
	 @return 返回执行信息
	*/
	public static SendReturnObjs Node_SendWork(String fk_flow, long workID, int toNodeID, String toEmps) throws Exception {
		return Node_SendWork(fk_flow, workID, null, null, toNodeID, toEmps);
	}
	/**
	 发送工作

	 @param fk_flow 流程编号
	 @param workID 工作ID
	 @param htWork 节点表单数据(Hashtable中的key与节点表单的字段名相同,value 就是字段值)
	 @return 执行信息
	*/
	public static SendReturnObjs Node_SendWork(String fk_flow, long workID, Hashtable htWork, int toNodeID, String nextWorkers) throws Exception {
		return Node_SendWork(fk_flow, workID, htWork, null, toNodeID, nextWorkers, WebUser.getNo(), WebUser.getName(), WebUser.getDeptNo(), WebUser.getDeptName(), null, 0, 0);
	}
	/**
	 发送工作

	 @param fk_flow 流程编号
	 @param workID 工作ID
	 @param htWork 节点表单数据(Hashtable中的key与节点表单的字段名相同,value 就是字段值)
	 @param workDtls 节点表单明从表数据(dataset可以包含多个table，每个table的名称与从表名称相同，列名与从表的字段相同, OID,RefPK列需要为空或者null )
	 @param toNodeID 到达的节点，如果是0表示让ccflow自动寻找，否则就按照该参数发送。
	 @param nextWorkers 下一步的接受人，如果多个人员用逗号分开，比如:zhangsan,lisi,
	 如果为空，则标识让ccflow按照节点访问规则自动寻找。
	 @return 执行信息
	*/
	public static SendReturnObjs Node_SendWork(String fk_flow, long workID, Hashtable htWork, DataSet workDtls, int toNodeID, String nextWorkers) throws Exception {
		return Node_SendWork(fk_flow, workID, htWork, workDtls, toNodeID, nextWorkers, WebUser.getNo(), WebUser.getName(), WebUser.getDeptNo(), WebUser.getDeptName(), null, 0, 0);
	}
	/**
	 发送工作

	 @param fk_flow 流程编号
	 @param workID 工作ID
	 @param htWork 节点表单数据(Hashtable中的key与节点表单的字段名相同,value 就是字段值)
	 @param workDtls 节点表单明从表数据(dataset可以包含多个table，每个table的名称与从表名称相同，列名与从表的字段相同, OID,RefPK列需要为空或者null )
	 @param toNodeID 到达的节点，如果是0表示让ccflow自动寻找，否则就按照该参数发送。
	 如果为空，则标识让ccflow按照节点访问规则自动寻找。
	 @param execUserNo 执行人编号
	 @param execUserName 执行人名称
	 @param execUserDeptNo 执行人部门名称
	 @param execUserDeptName 执行人部门编号
	 @return 发送的结果对象
	*/

	public static SendReturnObjs Node_SendWork(String fk_flow, long workID, java.util.Hashtable htWork, DataSet workDtls, int toNodeID, String toEmps, String execUserNo, String execUserName, String execUserDeptNo, String execUserDeptName, String title, long fid, long pworkid) throws Exception {
		return Node_SendWork(fk_flow, workID, htWork, workDtls, toNodeID, toEmps, execUserNo, execUserName, execUserDeptNo, execUserDeptName, title, fid, pworkid, false);
	}

	public static SendReturnObjs Node_SendWork(String fk_flow, long workID, Hashtable htWork, DataSet workDtls, int toNodeID, String toEmps, String execUserNo, String execUserName, String execUserDeptNo, String execUserDeptName, String title, long fid, long pworkid, boolean IsReturnNode) throws Exception {
		boolean isSend = true;
		if (htWork != null)
		{
			bp.wf.Dev2Interface.Node_SaveWork(workID, htWork, workDtls,null,isSend);
		}

		//获得当前的节点ID.
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		Node nd = new Node(gwf.getNodeID());

		if (execUserNo == null)
		{
			execUserNo = WebUser.getNo();
		}

		// 变量.
		Work sw = nd.getHisWork();
		sw.setOID(workID);
		int i = sw.RetrieveFromDBSources();
		if(i == 0 && nd.getItIsSubThread() == false){
			sw.setOID(pworkid);
			sw.RetrieveFromDBSources();
		}

		Node ndOfToNode = null; //到达节点ID
		if (toNodeID != 0)
		{
			ndOfToNode = new Node(toNodeID);
		}

		//补偿性修复.
		if (nd.getItIsSubThread() == false && sw.getFID() != 0)
		{
			sw.DirectUpdate();
		}

		SendReturnObjs objs;
		//执行流程发送.
		WorkNode wn = new WorkNode(sw, nd);
		wn.setExecer(execUserNo);
		wn.setHisGenerWorkFlow(gwf);
		wn.setExecerName(execUserName);
		if (execUserNo.equals("Guest") == true)
		{
			wn.setExecer(GuestUser.getNo());
			wn.setExecerName(GuestUser.getName());
		}

		wn.title = title; // 设置标题，有可能是从外部传递过来的标题.
		wn.SendHTOfTemp = htWork;

		if (ndOfToNode == null)
		{
			objs = wn.NodeSend(null, toEmps, IsReturnNode);
		}
		else
		{
			objs = wn.NodeSend(ndOfToNode, toEmps, IsReturnNode);
		}

		if (gwf.getScripNodeID() != gwf.getNodeID() || DataType.IsNullOrEmpty(gwf.GetParaString("HungupCheckMsg")) == false)
		{
			//清空小纸条数据
			gwf.setScripNodeID(0);
			gwf.setScripMsg("");
			// 清空挂起的信息
			gwf.SetPara("HungupSta", -1); //同意.
			gwf.SetPara("HungupCheckMsg", "");
			gwf.Update();
		}



			///#region 更新发送参数.
		if (htWork != null)
		{
			String dbstr = SystemConfig.getAppCenterDBVarStr();
			Paras ps = new Paras();

			String paras = "";
			for (Object key : htWork.keySet())
			{
				if (htWork.get(key) == null)
				{
					paras += "@" + key + "=''";
				}
				else
				{
					paras += "@" + key + "=" + htWork.get(key).toString();
				}

				switch (key.toString())
				{
					case WorkSysFieldAttr.SysSDTOfFlow:
						ps = new Paras();
						ps.SQL = "UPDATE WF_GenerWorkFlow SET SDTOfFlow=" + dbstr + "SDTOfFlow WHERE WorkID=" + dbstr + "WorkID";
						ps.Add(GenerWorkFlowAttr.SDTOfFlow, htWork.get(key).toString(), false);
						ps.Add(GenerWorkerListAttr.WorkID, workID);
						DBAccess.RunSQL(ps);

						break;
					case WorkSysFieldAttr.SysSDTOfNode:
						ps = new Paras();
						ps.SQL = "UPDATE WF_GenerWorkFlow SET SDTOfNode=" + dbstr + "SDTOfNode WHERE WorkID=" + dbstr + "WorkID";
						ps.Add(GenerWorkFlowAttr.SDTOfNode, htWork.get(key).toString(), false);
						ps.Add(GenerWorkerListAttr.WorkID, workID);
						DBAccess.RunSQL(ps);

						ps = new Paras();
						ps.SQL = "UPDATE WF_GenerWorkerlist SET SDT=" + dbstr + "SDT WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + dbstr + "FK_Node";
						ps.Add(GenerWorkerListAttr.SDT, htWork.get(key).toString(), false);
						ps.Add(GenerWorkerListAttr.WorkID, workID);
						ps.Add(GenerWorkerListAttr.FK_Node, objs.getVarToNodeID());
						DBAccess.RunSQL(ps);
						break;
					default:
						break;
				}
			}

			if (DataType.IsNullOrEmpty(paras) == false && Glo.isEnableTrackRec() == true)
			{
				ps = new Paras();
				ps.SQL = "UPDATE WF_GenerWorkerlist SET AtPara=" + dbstr + "Paras WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + dbstr + "FK_Node";
				ps.Add(GenerWorkerListAttr.Paras, paras, false);
				ps.Add(GenerWorkerListAttr.WorkID, workID);
				ps.Add(GenerWorkerListAttr.FK_Node, nd.getNodeID());
				DBAccess.RunSQL(ps);
			}
		}

		//判断流程是否启动流程时限
		if (nd.getItIsStartNode() && wn.getHisGenerWorkFlow().getWFState() != WFState.ReturnSta)
		{
			Date dtOfFlow = new Date();
			Date dtOfFlowWarning = new Date();
			Part part = new Part();
			part.setMyPK(nd.getFlowNo() + "_0_DeadLineRole");
			int count = part.RetrieveFromDBSources();
			if (count != 0 && part.getTag0().equals("1"))
			{
				int tag1 = 0;
				int tag2 = 0;
				int tag7 = 0;
				if (DataType.IsNumStr(part.getTag1()))
				{
					tag1 = Integer.parseInt(part.getTag1());
				}
				if (DataType.IsNumStr(part.getTag2()))
				{
					tag2 = Integer.parseInt(part.getTag2());
				}
				if (DataType.IsNumStr(part.getTag7()))
				{
					tag7 = Integer.parseInt(part.getTag7());
				}
				switch (tag7)
				{
					case 0:
						tag7 = 12;
						break;
					case 1:
						tag7 = 24;
						break;
					case 2:
						tag7 = 48;
						break;
					case 3:
						tag7 = 72;
						break;
					default:
						break;
				}
				//获取时限时间
				dtOfFlow = Glo.AddDayHoursSpan(new Date(), tag1, tag2, Integer.parseInt(part.getTag3()), TWay.forValue(Integer.parseInt(part.getTag4())));
				//计算警告日期.  时限时间-预警时间
				dtOfFlowWarning = Glo.AddDayHoursSpan(new Date(), (tag1 * 24 + tag2 - tag7) / 24, (tag1 * 24 + tag2 - tag7) % 24, Integer.parseInt(part.getTag3()), TWay.forValue(Integer.parseInt(part.getTag4())));
				String dbstr = SystemConfig.getAppCenterDBVarStr();
				Paras ps = new Paras();
				ps.SQL = "UPDATE WF_GenerWorkFlow SET SDTOfFlow=" + dbstr + "SDTOfFlow,SDTOfFlowWarning=" + dbstr + "SDTOfFlowWarning WHERE WorkID=" + dbstr + "WorkID";
				ps.Add(GenerWorkFlowAttr.SDTOfFlow, DataType.SysDateTimeFormat(dtOfFlow), false);
				ps.Add(GenerWorkFlowAttr.SDTOfFlowWarning, DataType.SysDateTimeFormat(dtOfFlowWarning), false);
				ps.Add(GenerWorkerListAttr.WorkID, workID);
				DBAccess.RunSQL(ps);

			}
		}

		///#endregion 更新发送参数.

		return objs;

	}
	/**
	 单个子线程发送

	 @param workid 流程ID
	 @param nodeID 干流程ID
	 @param toNodeID 发送到的子线程ID
	*/
	public static String Node_SendSubTread(long workid, int nodeID, int toNodeID) throws Exception {
		//1.子线程的流程实例改成运行状态
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workid);
		if (gwf.RetrieveFromDBSources() == 0)
		{
			return "该退回的子线程已经取消,不能再发送";
		}
		gwf.setWFState(WFState.Runing);
		gwf.setNodeID(toNodeID);
		gwf.Update();
		//2.当前子线程的处理人改成待办状态
		String sql = "UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND  FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node";
		Paras ps = new Paras();
		ps.SQL = sql;
		ps.Add("WorkID", workid);
		ps.Add("FK_Node", toNodeID);
		DBAccess.RunSQL(ps);

		DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=-2 WHERE WorkID=" + workid + " AND  FK_Node=" + nodeID + " AND IsPass=0 AND FK_Emp='" + WebUser.getNo() + "'");
		//判断当前流程是退回并重新发送的最后一个子线程吗
		sql = "SELECT COUNT(*) FROM WF_GenerWorkFlow WHERE FID=" + gwf.getFID() + " AND WFState=5";
		if (DBAccess.RunSQLReturnValInt(sql) == 0)
		{
			//修改赶流程上的状态为运行状态
			sql = "UPDATE WF_GenerWorkFlow SET WFState=2 WHERE WorkID=" + gwf.getFID();
			DBAccess.RunSQL(sql);
			//修改干流程当前人员的状态为已完成
			sql = "UPDATE WF_GenerWorkerlist SET IsPass=1 WHERE WorkID=" + gwf.getFID() + " AND  FK_Emp='" + WebUser.getNo() + "' AND FK_Node=" + nodeID;
			DBAccess.RunSQL(sql);
			return "url@MyView";
		}
		return "发送成功";
	}
	/**
	 增加在队列工作中增加一个处理人.
	 这个处理顺序系统已经自动处理了.

	 @param flowNo 流程编号
	 @param nodeID 工作ID
	 @param workid workid
	 @param fid fid
	 @param empNo 要增加的处理人编号
	 @param empName 要增加的处理人名称
	*/
	public static void Node_InsertOrderEmp(String flowNo, int nodeID, long workid, long fid, String empNo, String empName) throws Exception {
		GenerWorkerList gwl = new GenerWorkerList();
		int i = gwl.Retrieve(GenerWorkerListAttr.WorkID, workid, GenerWorkerListAttr.FK_Node, nodeID);
		if (i == 0)
		{
			throw new RuntimeException("@没有找到当前工作人员的待办，请检查该流程是否已经运行到该节点上来了。");
		}

		gwl.setPassInt(100);
		gwl.setItIsEnable(true);
		gwl.setEmpNo(empNo);
		gwl.setEmpName(empName);

		try
		{
			gwl.Insert();
		}
		catch (java.lang.Exception e)
		{
			throw new RuntimeException("@该人员已经存在处理队列中，您不能增加.");
		}

		//开始更新他们的顺序, 首先从数据库里获取他们的顺序.     lxl职位由小到大
		String sql = "SELECT " + bp.sys.base.Glo.getUserNo() + ",Name FROM Port_Emp WHERE No IN (SELECT FK_Emp FROM WF_GenerWorkerlist WHERE WorkID=" + workid + " AND FK_Node=" + nodeID + " AND IsPass >=100 ) ORDER BY IDX desc";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		int idx = 100;
		for (DataRow dr : dt.Rows)
		{
			idx++;
			String myEmpNo = dr.getValue(0).toString();
			sql = "UPDATE WF_GenerWorkerlist SET IsPass=" + idx + " WHERE FK_Emp='" + myEmpNo + "' AND WorkID=" + workid + " AND FK_Node=" + nodeID;
			DBAccess.RunSQL(sql);
		}
	}
	/**
	 抄送到部门

	 @param workid
	 @param cctype 可选
	 @return 返回执行结果.
	*/

	public static String Node_CCToDept(long workid, String deptID, int cctype, boolean isWriteTolog) throws Exception {
		return Node_CCToDept(workid, deptID, cctype, isWriteTolog, false);
	}

	public static String Node_CCToDept(long workid, String deptID, int cctype) throws Exception {
		return Node_CCToDept(workid, deptID, cctype, false, false);
	}

	public static String Node_CCToDept(long workid, String deptID) throws Exception {
		return Node_CCToDept(workid, deptID, 0, false, false);
	}

	public static String Node_CCToDept(long workid, String deptID, int cctype, boolean isWriteTolog, boolean isSendMsg) throws Exception {
		if (DataType.IsNullOrEmpty(deptID) == true)
		{
			return "没有指定人";
		}

		GenerWorkFlow gwf = new GenerWorkFlow(workid);
		Node fromNode = new Node(gwf.getNodeID());

		Emp emp = new Emp(); //构造实体类
		Dept dept = new Dept(deptID);


		String sql = "SELECT " + bp.sys.base.Glo.getUserNo() + ",Name FROM Port_Emp A WHERE A.FK_Dept='" + deptID + "'";
		sql += " UNION ";
		sql += " SELECT " + bp.sys.base.Glo.getUserNo() + ",Name FROM Port_Emp A, Port_DeptEmp B WHERE A.No=B.FK_Emp AND B.FK_Dept='" + deptID + "'";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		CCList list = new CCList();

		String names = "";
		String empNos = ","; //人员编号s 防止重复.
		for (DataRow dr : dt.Rows)
		{
			String empNo = dr.getValue(0).toString();
			String empName = dr.getValue(1).toString();
			if (DataType.IsNullOrEmpty(empNo) == true)
			{
				continue;
			}

			//如果已经包含了.
			if (empNos.contains("," + empNo + ",") == true)
			{
				continue;
			}

			empNos += empNo + ",";

			names += empName + "、";

			//list.setMyPK(DBAccess.GenerOIDByGUID().ToString(); // workID + "_" + fk_node + "_" + empNo;
			if (SystemConfig.getCustomerNo().equals("GXJSZX") == true)
			{
				list.setMyPK(gwf.getWorkID() + "_" + gwf.getNodeID() + "_" + empNo + "_" + cctype);
			}
			else
			{
				list.setMyPK(gwf.getWorkID() + "_" + gwf.getNodeID() + "_" + empNo);
			}
			if (list.getIsExits() == true)
			{
				continue; //判断是否存在?
			}

			list.setFlowNo(gwf.getFlowNo());
			list.setFlowName(gwf.getFlowName());
			list.setNodeIDWork(fromNode.getNodeID());
			list.setNodeName(gwf.getNodeName());
			list.setTitle(gwf.getTitle());
			list.setDoc(gwf.getTitle());
			list.setCCTo(empNo);
			list.setCCToName(empName);

			//增加抄送人部门.
			list.setRDT(DataType.getCurrentDateTime());
			list.setRecEmpNo(WebUser.getNo());
			list.setRecEmpName(WebUser.getName());

			list.setWorkID(gwf.getWorkID());
			list.setFID(gwf.getFID());
			list.setPFlowNo(gwf.getPFlowNo());
			list.setPWorkID(gwf.getPWorkID());

			//是否要写入待办.
			if (fromNode.getCCWriteTo()== CCWriteTo.CCList)
			{
				list.setInEmpWorks(false); //added by liuxc,2015.7.6
			}
			else
			{
				list.setInEmpWorks(true); //added by liuxc,2015.7.6
			}

			//写入待办和写入待办与抄送列表,状态不同
			if (fromNode.getCCWriteTo()== CCWriteTo.All || fromNode.getCCWriteTo()== CCWriteTo.Todolist)
			{
				list.setHisSta(fromNode.getCCWriteTo()== CCWriteTo.All ? CCSta.UnRead : CCSta.Read);
			}

			if (fromNode.getItIsEndNode() == true) //结束节点只写入抄送列表
			{
				list.setHisSta(CCSta.UnRead);
				list.setInEmpWorks(false);
			}

			try
			{
				list.Insert();
			}
			catch (java.lang.Exception e)
			{
				try {
					list.CheckPhysicsTable();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
				list.Update();
			}

			//如果需要发送消息.
			if (isSendMsg == true)
			{
				//发送消息给他们.
				try {
					Dev2Interface.Port_SendMsg(emp.getUserID(), gwf.getTitle(), "抄送消息:" + gwf.getTitle(), "CC" + gwf.getNodeID() + "_" + gwf.getWorkID() + "_" + emp.getUserID(), SMSMsgType.CC, gwf.getFlowNo(), gwf.getNodeID(), gwf.getWorkID(), gwf.getFID());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (isWriteTolog == true)
		{
			//记录日志.
			Glo.AddToTrack(ActionType.CC, gwf.getFlowNo(), gwf.getWorkID(), gwf.getFID(), gwf.getNodeID(), gwf.getNodeName(), WebUser.getNo(), WebUser.getName(), gwf.getNodeID(), gwf.getNodeName(), empNos, names, gwf.getTitle(), null);
		}

		return "已经成功的把工作抄送给:" + names;
	}
	/**
	 抄送部门

	 @param workid 工作ID
	 @param deptIDs 部门IDs
	 @return 返回执行的结果
	*/

	public static String Node_CCToDepts(long workid, String deptIDs) throws Exception {
		return Node_CCToDepts(workid, deptIDs, 0);
	}

	public static String Node_CCToDepts(long workid, String deptIDs, int cctype) throws Exception {
		String[] deptStrs = deptIDs.split("[,]", -1);
		for (String deptID : deptStrs)
		{
			Node_CCToDept(workid, deptID, cctype, false, false);
		}
		return "成功执行.";
	}
	/**
	 抄送部门和用户

	 @param workid 工作ID
	 @param deptIDs 部门IDs
	 @param toEmps 用户账号
	 @return 返回执行的结果
	*/

	public static String Node_CCToDepts(long workid, String deptIDs, String toEmps) throws Exception {
		return Node_CCToDepts(workid, deptIDs, toEmps, 0);
	}

	public static String Node_CCToDepts(long workid, String deptIDs, String toEmps, int cctype) throws Exception {
		String[] deptStrs = deptIDs.split("[,]", -1);
		for (String deptID : deptStrs)
		{
			Node_CCToDept(workid, deptID, cctype, false, false);
		}
		if (DataType.IsNullOrEmpty(toEmps) == true)
		{
			return "成功执行.";
		}
		return Node_CCTo(workid, toEmps, cctype);

	}
	/**
	 写入抄送

	 @param workid 工作ID
	 @param toEmps 抄送给: zhangsan,lisi,wangwu
	 @return 执行结果
	*/

	public static String Node_CCTo(long workid, String toEmps) throws Exception {
		return Node_CCTo(workid, toEmps, 0);
	}

	public static String Node_CCTo(long workid, String toEmps, int cctype) throws Exception {
		if (DataType.IsNullOrEmpty(toEmps) == true)
		{
			return "没有指定人";
		}

		GenerWorkFlow gwf = new GenerWorkFlow(workid);
		Node fromNode = new Node(gwf.getNodeID());

		toEmps = toEmps.replace(";", ",");
		String[] strs = toEmps.split("[,]", -1);
		Emp emp = new Emp();
		Dept dept = new Dept();
		CCList list = new CCList();

		String names = "";
		for (String str : strs)
		{
			if (DataType.IsNullOrEmpty(str) == true)
			{
				continue;
			}

			emp.setUserID(str);
			int i = emp.RetrieveFromDBSources();
			if (i == 0)
			{
				continue;
			}

			//根据人员的部门编号获取所在部门名称
			dept.setNo(emp.getDeptNo());
			dept.RetrieveFromDBSources();

			names += emp.getName() +"、";

			//list.setMyPK(DBAccess.GenerOIDByGUID().ToString(); // workID + "_" + fk_node + "_" + empNo;
			if (SystemConfig.getCustomerNo().equals("GXJSZX") == true)
			{
				list.setMyPK(gwf.getWorkID() + "_" + gwf.getNodeID() + "_" + emp.getUserID() + "_" + cctype);
			}
			else
			{
				list.setMyPK(gwf.getWorkID() + "_" + gwf.getNodeID() + "_" + emp.getUserID());
			}
			try {
				if (list.getIsExits() == true)
				{
					continue; //判断是否存在?
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}


			list.setFlowNo(gwf.getFlowNo());
			list.setFlowName(gwf.getFlowName());
			list.setNodeIDWork(fromNode.getNodeID());
			list.setNodeName(gwf.getNodeName());
			list.setTitle(gwf.getTitle());
			list.setDoc(gwf.getTitle());
			list.setCCTo(emp.getUserID());
			list.setCCToName(emp.getName());

			//增加抄送人部门.
			list.setRDT(DataType.getCurrentDateTime());
			list.setRecEmpNo(WebUser.getNo());
			list.setRecEmpName(WebUser.getName());
			list.setWorkID(gwf.getWorkID());
			list.setFID(gwf.getFID());
			list.setPFlowNo(gwf.getPFlowNo());
			list.setPWorkID(gwf.getPWorkID());

			//是否要写入待办.
			if (fromNode.getCCWriteTo()== CCWriteTo.CCList)
			{
				list.setInEmpWorks(false); //added by liuxc,2015.7.6
			}
			else
			{
				list.setInEmpWorks(true); //added by liuxc,2015.7.6
			}

			//写入待办和写入待办与抄送列表,状态不同
			if (fromNode.getCCWriteTo()== CCWriteTo.All || fromNode.getCCWriteTo()== CCWriteTo.Todolist)
			{
				list.setHisSta(fromNode.getCCWriteTo()== CCWriteTo.All ? CCSta.UnRead : CCSta.Read);
			}

			if (fromNode.getItIsEndNode() == true) //结束节点只写入抄送列表
			{
				list.setHisSta(CCSta.UnRead);
				list.setInEmpWorks(false);
			}

			try
			{
				list.Insert();
			}
			catch (java.lang.Exception e)
			{
				list.CheckPhysicsTable();
				list.Update();
			}

			//发送消息给他们.
			bp.wf.Dev2Interface.Port_SendMsg(emp.getUserID(), gwf.getTitle(), "抄送消息:" + gwf.getTitle(), "CC" + gwf.getNodeID() + "_" + gwf.getWorkID() + "_" + emp.getUserID(), SMSMsgType.CC, gwf.getFlowNo(), gwf.getNodeID(), gwf.getWorkID(), gwf.getFID());
		}

		//记录日志.
		// Glo.AddToTrack(ActionType.CC, gwf.FlowNo, gwf.WorkID, gwf.FID, gwf.getNodeID(), gwf.NodeName,
		/**    WebUser.getNo(), WebUser.getName(), gwf.getNodeID(), gwf.NodeName, toEmps, names, gwf.Title, null);
		*/

		return "已经成功的把工作抄送给:" + names;
	}
	/**
	 把抄送写入待办列表

	 @param workID 工作ID
	 @param ccToEmpNo 抄送给
	 @param ccToEmpName 抄送给名称
	 @return
	*/
	public static String Node_CC_WriteTo_Todolist(int fk_node, long workID, String ccToEmpNo, String ccToEmpName) throws Exception {
		return Node_CC_WriteTo_CClist(fk_node, workID, ccToEmpNo, ccToEmpName, "", "");
	}
	/**
	 执行抄送

	 @param workID 工作ID
	 @param toEmpNo 抄送人员编号
	 @param toEmpName 抄送人员人员名称
	 @param msgTitle 标题
	 @param msgDoc 内容
	 @return 执行信息
	*/
	public static String Node_CC_WriteTo_CClist(int fk_node, long workID, String toEmpNo, String toEmpName, String msgTitle, String msgDoc) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workID);
		if (gwf.RetrieveFromDBSources() == 0)
		{
			Node nd = new Node(fk_node);
			gwf.setNodeID(fk_node);
			gwf.setFlowNo(nd.getFlowNo());
			gwf.setFlowName(nd.getFlowName());
			gwf.setNodeName(nd.getName());
		}

		Node fromNode = new Node(fk_node);

		CCList list = new CCList();
		list.setMyPK(String.valueOf(DBAccess.GenerOIDByGUID())); // workID + "_" + fk_node + "_" + empNo;
		list.setFlowNo(gwf.getFlowNo());
		list.setFlowName(gwf.getFlowName());
		list.setNodeIDWork(fk_node);
		list.setNodeName(gwf.getNodeName());
		list.setTitle(msgTitle);
		list.setDoc(msgDoc);
		list.setCCTo(toEmpNo);
		list.setCCToName(toEmpName);

		//增加抄送人部门.
		Emp emp = new Emp(toEmpNo);
		list.setRDT(DataType.getCurrentDateTime());
		list.setRecEmpNo(WebUser.getNo());
		list.setRecEmpName(WebUser.getName());

		list.setWorkID(gwf.getWorkID());
		list.setFID(gwf.getFID());
		list.setPFlowNo(gwf.getPFlowNo());
		list.setPWorkID(gwf.getPWorkID());
		//  list.setNDFrom(ndFrom;

		//是否要写入待办.
		if (fromNode.getCCWriteTo()== CCWriteTo.CCList)
		{
			list.setInEmpWorks(false); //added by liuxc,2015.7.6
		}
		else
		{
			list.setInEmpWorks(true); //added by liuxc,2015.7.6
		}

		//写入待办和写入待办与抄送列表,状态不同
		if (fromNode.getCCWriteTo()== CCWriteTo.All || fromNode.getCCWriteTo()== CCWriteTo.Todolist)
		{
			list.setHisSta(fromNode.getCCWriteTo()== CCWriteTo.All ? CCSta.UnRead : CCSta.Read);
		}

		try {
			if (fromNode.getItIsEndNode() == true) //结束节点只写入抄送列表
			{
				list.setHisSta(CCSta.UnRead);
				list.setInEmpWorks(false);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		try
		{
			list.Insert();
		}
		catch (java.lang.Exception e)
		{
			try {
				list.CheckPhysicsTable();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			list.Update();
		}

		//
		bp.wf.Dev2Interface.Port_SendMsg(toEmpNo, msgTitle, msgDoc, "CC" + gwf.getNodeID() + "_" + gwf.getWorkID(), SMSMsgType.CC, gwf.getFlowNo(), gwf.getNodeID(), gwf.getWorkID(), gwf.getFID());

		//记录日志.
		Glo.AddToTrack(ActionType.CC, gwf.getFlowNo(), workID, gwf.getFID(), gwf.getNodeID(), gwf.getNodeName(), WebUser.getNo(), WebUser.getName(), gwf.getNodeID(), gwf.getNodeName(), toEmpNo, toEmpName, msgTitle, null);

		return "已经成功的把工作抄送给:" + toEmpNo + "," + toEmpName;
	}
	/**
	 执行抄送

	 @param fk_node 节点
	 @param workID 工作ID
	 @param title 标题
	 @param doc 内容
	 @param toEmps 到人员(zhangsan,张三;lisi,李四;wangwu,王五;)
	 @param toDepts 到部门，格式:001,002,003
	 @param toStations 到角色 格式:001,002,003
	 @param toStations 到权限组 格式:001,002,003
	*/

	public static String Node_CC_WriteTo_CClist(int fk_node, long workID, String title, String doc, String toEmps, String toDepts, String toStations) throws Exception {
		return Node_CC_WriteTo_CClist(fk_node, workID, title, doc, toEmps, toDepts, toStations, null);
	}

	public static String Node_CC_WriteTo_CClist(int fk_node, long workID, String title, String doc, String toEmps) throws Exception {
		return Node_CC_WriteTo_CClist(fk_node, workID, title, doc, toEmps, null, null, null);
	}

	public static String Node_CC_WriteTo_CClist(int fk_node, long workID, String title, String doc) throws Exception {
		return Node_CC_WriteTo_CClist(fk_node, workID, title, doc, null, null, null, null);
	}

	public static String Node_CC_WriteTo_CClist(int fk_node, long workID, String title, String doc, String toEmps, String toDepts, String toStations, String toGroups) throws Exception {

		Node nd = new Node(fk_node);

		//计算出来曾经抄送过的人.
		String sql = "SELECT CCTo FROM WF_CCList WHERE FK_Node=" + fk_node + " AND WorkID=" + workID;
		DataTable mydt = DBAccess.RunSQLReturnTable(sql);
		String toAllEmps = ",";
		for (DataRow dr : mydt.Rows)
		{
			toAllEmps += dr.getValue(0).toString() + ",";
		}

		//录制本次抄送的人员.
		String ccRec = "";

		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workID);
		try {
			if (gwf.RetrieveFromDBSources() == 0)
			{
				gwf.setNodeID(fk_node);
				gwf.setFlowNo(nd.getFlowNo());
				gwf.setFlowName(nd.getFlowName());
				gwf.setNodeName(nd.getName());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}


		///#region 处理抄送到人员.
		if (toEmps != null)
		{
			String[] empStrs = toEmps.split("[;]", -1);
			for (String empStr : empStrs)
			{
				if (DataType.IsNullOrEmpty(empStr) == true || empStr.contains(",") == false)
				{
					continue;
				}

				String[] strs = empStr.split("[,]", -1);
				String empNo = strs[0];
				String empName = strs[1];

				if (toAllEmps.contains("," + empNo + ",") == true)
				{
					continue;
				}

				CCList list = new CCList();
				list.setMyPK(String.valueOf(DBAccess.GenerOIDByGUID())); // workID + "_" + fk_node + "_" + empNo;
				list.setFlowNo(gwf.getFlowNo());
				list.setFlowName(gwf.getFlowName());
				list.setNodeIDWork(fk_node);
				list.setNodeName(gwf.getNodeName());
				list.setTitle(title);
				list.setDoc(doc);
				list.setCCTo(empNo);
				list.setCCToName(empName);
				list.setRDT(DataType.getCurrentDateTime());
				list.setRecEmpNo(WebUser.getNo());
				list.setRecEmpName(WebUser.getName());
				list.setWorkID(gwf.getWorkID());
				list.setFID(gwf.getFID());
				list.setPFlowNo(gwf.getPFlowNo());
				list.setPWorkID(gwf.getPWorkID());
				// list.setNDFrom(ndFrom;

				//是否要写入待办.
				if (nd.getCCWriteTo() == CCWriteTo.CCList)
				{
					list.setInEmpWorks(false); //added by liuxc,2015.7.6
				}
				else
				{
					list.setInEmpWorks(true); //added by liuxc,2015.7.6
				}

				//写入待办和写入待办与抄送列表,状态不同
				if (nd.getCCWriteTo() == CCWriteTo.All || nd.getCCWriteTo() == CCWriteTo.Todolist)
				{
					list.setHisSta(nd.getCCWriteTo() == CCWriteTo.All ? CCSta.UnRead : CCSta.Read);
				}

				if (nd.getItIsEndNode() == true) //结束节点只写入抄送列表
				{
					list.setHisSta(CCSta.UnRead);
					list.setInEmpWorks(false);
				}

				try
				{
					list.Insert();
				}
				catch (java.lang.Exception e)
				{
					try {
						list.CheckPhysicsTable();
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					}
					list.Update();
				}

				ccRec += "" + list.getCCToName() + ";";
				//人员编号,加入这个集合.
				toAllEmps += empNo + ",";
			}
		}

			///#endregion 处理抄送到人员.


			///#region 处理抄送到部门.
		if (toDepts != null)
		{
			toDepts = toDepts.replace(";", ",");

			String[] deptStrs = toDepts.split("[,]", -1);
			for (String deptNo : deptStrs)
			{
				if (DataType.IsNullOrEmpty(deptNo) == true)
				{
					continue;
				}

				sql = "SELECT " + bp.sys.base.Glo.getUserNo() + ",Name,FK_Dept FROM Port_Emp WHERE FK_Dept='" + deptNo + "'";
				DataTable dt = DBAccess.RunSQLReturnTable(sql);
				for (DataRow dr : dt.Rows)
				{
					String empNo = dr.getValue(0).toString();
					String empName = dr.getValue(1).toString();
					if (toAllEmps.contains("," + empNo + ",") == true)
					{
						continue;
					}

					CCList list = new CCList();
					list.setMyPK(String.valueOf(DBAccess.GenerOIDByGUID())); // workID + "_" + fk_node + "_" + empNo;
					list.setFlowNo(gwf.getFlowNo());
					list.setFlowName(gwf.getFlowName());
					list.setNodeIDWork(fk_node);
					list.setNodeName(gwf.getNodeName());
					list.setTitle(title);
					list.setDoc(doc);
					list.setCCTo(empNo);
					list.setCCToName(empName);
					list.setRDT(DataType.getCurrentDateTime());
					list.setRecEmpNo(WebUser.getNo());
					list.setRecEmpName(WebUser.getName());
					list.setWorkID(gwf.getWorkID());
					list.setFID(gwf.getFID());
					list.setPFlowNo(gwf.getPFlowNo());
					list.setPWorkID(gwf.getPWorkID());
					// list.setNDFrom(ndFrom;

					//是否要写入待办.
					if (nd.getCCWriteTo() == CCWriteTo.CCList)
					{
						list.setInEmpWorks(false); //added by liuxc,2015.7.6
					}
					else
					{
						list.setInEmpWorks(true); //added by liuxc,2015.7.6
					}

					//写入待办和写入待办与抄送列表,状态不同
					if (nd.getCCWriteTo() == CCWriteTo.All || nd.getCCWriteTo() == CCWriteTo.Todolist)
					{
						list.setHisSta(nd.getCCWriteTo() == CCWriteTo.All ? CCSta.UnRead : CCSta.Read);
					}

					try {
						if (nd.getItIsEndNode() == true) //结束节点只写入抄送列表
						{
							list.setHisSta(CCSta.UnRead);
							list.setInEmpWorks(false);
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

					try
					{
						list.Insert();
					}
					catch (java.lang.Exception e2)
					{
						list.CheckPhysicsTable();
						list.Update();
					}

					//录制本次抄送到的人员.
					ccRec += "" + list.getCCToName() + ";";

					//人员编号,加入这个集合.
					toAllEmps += empNo + ",";
				}
			}
		}

			///#endregion 处理抄送到部门.


			///#region 处理抄送到角色.
		if (toStations != null)
		{
			String empNo = "No";
			if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			{
				empNo = "UserID as No";
			}

			toStations = toStations.replace(";", ",");
			String[] mystas = toStations.split("[,]", -1);
			for (String staNo : mystas)
			{
				if (DataType.IsNullOrEmpty(staNo) == true)
				{
					continue;
				}

				sql = "SELECT A." + empNo + ", A.Name, a.FK_Dept FROM Port_Emp A, Port_DeptEmpStation B  WHERE a.No=B.FK_Emp AND B.FK_Station='" + staNo + "'";

				DataTable dt = DBAccess.RunSQLReturnTable(sql);
				for (DataRow dr : dt.Rows)
				{
					empNo = dr.getValue(0).toString();
					String empName = dr.getValue(1).toString();
					if (toAllEmps.contains("," + empNo + ",") == true)
					{
						continue;
					}

					CCList list = new CCList();
					list.setMyPK(String.valueOf(DBAccess.GenerOIDByGUID())); // workID + "_" + fk_node + "_" + empNo;
					list.setFlowNo(gwf.getFlowNo());
					list.setFlowName(gwf.getFlowName());
					list.setNodeIDWork(fk_node);
					list.setNodeName(gwf.getNodeName());
					list.setTitle(title);
					list.setDoc(doc);
					list.setCCTo(empNo);
					list.setCCToName(empName);
					list.setRDT(DataType.getCurrentDateTime());
					list.setRecEmpNo(WebUser.getNo());
					list.setRecEmpName(WebUser.getName());
					list.setWorkID(gwf.getWorkID());
					list.setFID(gwf.getFID());
					list.setPFlowNo(gwf.getPFlowNo());
					list.setPWorkID(gwf.getPWorkID());
					// list.setNDFrom(ndFrom;

					//是否要写入待办.
					if (nd.getCCWriteTo() == CCWriteTo.CCList)
					{
						list.setInEmpWorks(false); //added by liuxc,2015.7.6
					}
					else
					{
						list.setInEmpWorks(true); //added by liuxc,2015.7.6
					}

					//写入待办和写入待办与抄送列表,状态不同
					if (nd.getCCWriteTo() == CCWriteTo.All || nd.getCCWriteTo() == CCWriteTo.Todolist)
					{
						list.setHisSta(nd.getCCWriteTo() == CCWriteTo.All ? CCSta.UnRead : CCSta.Read);
					}

					try {
						if (nd.getItIsEndNode() == true) //结束节点只写入抄送列表
						{
							list.setHisSta(CCSta.UnRead);
							list.setInEmpWorks(false);
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

					try
					{
						list.Insert();
					}
					catch (java.lang.Exception e3)
					{
						list.CheckPhysicsTable();
						list.Update();
					}

					//录制本次抄送到的人员.
					ccRec += "" + list.getCCToName() + ";";

					//人员编号,加入这个集合.
					toAllEmps += empNo + ",";
				}
			}
		}

			///#endregion.


			///#region 抄送到组.
		if (toGroups != null)
		{
			toGroups = toGroups.replace(";", ",");
			String[] groups = toGroups.split("[,]", -1);

			for (String group : groups)
			{
				if (DataType.IsNullOrEmpty(group) == true)
				{
					continue;
				}

				String empNo = "No";
				if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
				{
					empNo = "UserID as No";
				}

				//解决分组下的角色人员.
				sql = "SELECT a." + empNo + ",a.Name, A.FK_Dept FROM Port_Emp A, Port_DeptEmpStation B, GPM_GroupStation C  WHERE A.No=B.FK_Emp AND B.FK_Station=C.FK_Station AND C.FK_Group='" + group + "'";
				sql += " UNION ";
				sql += "SELECT A." + empNo + ", A.Name, A.FK_Dept FROM Port_Emp A, Port_TeamEmp B  WHERE A.No=B.FK_Emp AND B.FK_Group='" + group + "'";

				DataTable dt = DBAccess.RunSQLReturnTable(sql);
				for (DataRow dr : dt.Rows)
				{
					empNo = dr.getValue(0).toString();
					String empName = dr.getValue(1).toString();
					if (toAllEmps.contains("," + empNo + ",") == true)
					{
						continue;
					}

					CCList list = new CCList();
					list.setMyPK(String.valueOf(DBAccess.GenerOIDByGUID())); // workID + "_" + fk_node + "_" + empNo;
					list.setFlowNo(gwf.getFlowNo());
					list.setFlowName(gwf.getFlowName());
					list.setNodeIDWork(fk_node);
					list.setNodeName(gwf.getNodeName());
					list.setTitle(title);
					list.setDoc(doc);
					list.setCCTo(empNo);
					list.setCCToName(empName);
					list.setRDT(DataType.getCurrentDateTime());
					list.setRecEmpNo(WebUser.getNo());
					list.setRecEmpName(WebUser.getName());
					list.setWorkID(gwf.getWorkID());
					list.setFID(gwf.getFID());
					list.setPFlowNo(gwf.getPFlowNo());
					list.setPWorkID(gwf.getPWorkID());
					// list.setNDFrom(ndFrom;

					//是否要写入待办.
					if (nd.getCCWriteTo() == CCWriteTo.CCList)
					{
						list.setInEmpWorks(false); //added by liuxc,2015.7.6
					}
					else
					{
						list.setInEmpWorks(true); //added by liuxc,2015.7.6
					}

					//写入待办和写入待办与抄送列表,状态不同
					if (nd.getCCWriteTo() == CCWriteTo.All || nd.getCCWriteTo() == CCWriteTo.Todolist)
					{
						list.setHisSta(nd.getCCWriteTo() == CCWriteTo.All ? CCSta.UnRead : CCSta.Read);
					}

					try {
						if (nd.getItIsEndNode() == true) //结束节点只写入抄送列表
						{
							list.setHisSta(CCSta.UnRead);
							list.setInEmpWorks(false);
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

					try
					{
						list.Insert();
					}
					catch (java.lang.Exception e4)
					{
						list.CheckPhysicsTable();
						list.Update();
					}

					//录制本次抄送到的人员.
					ccRec += "" + list.getCCToName() + ";";

					//人员编号,加入这个集合.
					toAllEmps += empNo + ",";
				}
			}
		}

			///#endregion 抄送到组

		return ccRec;

		////记录日志.
		//Glo.AddToTrack(ActionType.CC, gwf.FlowNo, workID, gwf.FID, gwf.getNodeID(), gwf.NodeName,
		//    WebUser.getNo(), WebUser.getName(), gwf.getNodeID(), gwf.NodeName, toAllEmps, toAllEmps, title, null);
	}

	/**
	 执行删除

	 @param mypk 删除
	*/
	public static void Node_CC_DoDel(String mypk) throws Exception
	{
		Paras ps = new Paras();
		ps.SQL = "DELETE FROM WF_CCList WHERE MyPK=" + SystemConfig.getAppCenterDBVarStr() + "MyPK";
		ps.Add(CCListAttr.MyPK, mypk, false);
		DBAccess.RunSQL(ps);
	}

	/**
	 设置抄送状态

	 @param nodeID 节点ID
	 @param workid 工作ID
	 @param empNo 人员编号
	 @param sta 状态
	*/
	public static void Node_CC_SetSta(int nodeID, long workid, String empNo, CCSta sta)
	{
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_CCList   SET Sta=" + dbstr + "Sta,CDT=" + dbstr + "CDT WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + dbstr + "FK_Node AND CCTo=" + dbstr + "CCTo";
		ps.Add(CCListAttr.Sta, sta.getValue());
		ps.Add(CCListAttr.CDT, DataType.getCurrentDateTime(), false);
		ps.Add(CCListAttr.WorkID, workid);
		ps.Add(CCListAttr.NodeIDWork, nodeID);
		ps.Add(CCListAttr.CCTo, empNo, false);
		DBAccess.RunSQL(ps);
	}
	/**
	 执行读取

	 @param mypk 主键
	*/

	public static void Node_CC_SetRead(String mypk) throws Exception
	{
		Node_CC_SetRead(mypk, null);
	}

	public static void Node_CC_SetRead(String mypk, String bbsSetInfo)
	{
		if (DataType.IsNullOrEmpty(mypk))
		{
			return;
		}

		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_CCList SET Sta=" + SystemConfig.getAppCenterDBVarStr() + "Sta,ReadDT=" + SystemConfig.getAppCenterDBVarStr() + "ReadDT  WHERE MyPK=" + SystemConfig.getAppCenterDBVarStr() + "MyPK";
		ps.Add(CCListAttr.Sta, CCSta.Read.getValue()); // @lizhen.
		ps.Add(CCListAttr.ReadDT, DataType.getCurrentDateTime(), false); //设置读取日期.
		ps.Add(CCListAttr.MyPK, mypk, false);
		DBAccess.RunSQL(ps);
	}

	/**
	 设置抄送执行完成
	 @param workid 工作ID
	*/

	public static String Node_CC_SetCheckOver(long workid) throws Exception {
		return Node_CC_SetCheckOver(workid, null);
	}

	public static String Node_CC_SetCheckOver(long workid, String checkInfo) throws Exception {
		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_CCList SET Sta=" + SystemConfig.getAppCenterDBVarStr() + "Sta,CDT=" + SystemConfig.getAppCenterDBVarStr() + "CDT  WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND CCTo=" + SystemConfig.getAppCenterDBVarStr() + "CCTo ";
		ps.Add(CCListAttr.Sta, CCSta.CheckOver.getValue());
		ps.Add(CCListAttr.CDT, DataType.getCurrentDateTime(), false); //设置完成日期.
		ps.Add(CCListAttr.WorkID, workid);
		ps.Add(CCListAttr.CCTo, WebUser.getNo(), false);
		int val = DBAccess.RunSQL(ps);
		if (val == 0)
		{
			return "err@执行失败,没有更新到workid=" + workid + ",CCTo=" + WebUser.getNo() + ", 的抄送数据.";
		}

		GenerWorkFlow gwf = new GenerWorkFlow(workid);

		//BP.WF.Dev2Interface.Flow_BBSAdd(gwf.FlowNo, workid, gwf.FID, checkInfo, bp.web.WebUser.getNo(), bp.web.WebUser.getName());
		return "执行成功.";
	}
	/**
	 设置抄送执行完成

	 @param workid 工作ID
	 @param checkInfo 审核信息
	*/

	public static String Node_CC_SetCheckOver(String flowNo, long workid, long fid, String checkInfo, String empNo) throws Exception {
		return Node_CC_SetCheckOver(flowNo, workid, fid, checkInfo, empNo, null);
	}

	public static String Node_CC_SetCheckOver(String flowNo, long workid, long fid, String checkInfo) throws Exception {
		return Node_CC_SetCheckOver(flowNo, workid, fid, checkInfo, null, null);
	}

	public static String Node_CC_SetCheckOver(String flowNo, long workid, long fid) throws Exception {
		return Node_CC_SetCheckOver(flowNo, workid, fid, null, null, null);
	}

	public static String Node_CC_SetCheckOver(String flowNo, long workid, long fid, String checkInfo, String empNo, String empName) throws Exception {
		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_CCList SET Sta=" + SystemConfig.getAppCenterDBVarStr() + "Sta,CDT=" + SystemConfig.getAppCenterDBVarStr() + "CDT  WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND CCTo=" + SystemConfig.getAppCenterDBVarStr() + "CCTo ";
		ps.Add(CCListAttr.Sta, CCSta.CheckOver.getValue());
		ps.Add(CCListAttr.CDT, DataType.getCurrentDateTime(), false); //设置完成日期.
		ps.Add(CCListAttr.WorkID, workid);
		ps.Add(CCListAttr.CCTo, WebUser.getNo(), false);
		int val = DBAccess.RunSQL(ps);
		if (val == 0)
		{
			return "err@执行失败,没有更新到workid=" + workid + ",CCTo=" + WebUser.getNo() + ", 的抄送数据.";
		}

		bp.wf.Dev2Interface.Flow_BBSAdd(flowNo, workid, fid, checkInfo, empNo, empName);
		return "执行成功.";
	}
	/**
	 批量审核
	 @param workids 多个工作ID使用逗号分割比如:'111,233,444'
	*/

	public static String Node_CC_SetCheckOverBatch(String workids) throws Exception {
		return Node_CC_SetCheckOverBatch(workids, null);
	}

	public static String Node_CC_SetCheckOverBatch(String workids, String checkInfo) throws Exception {
		if (checkInfo == null)
		{
			checkInfo = "已阅";
		}

		String[] ids = workids.split("[,]", -1);
		String info = "";
		for (String id : ids)
		{
			if (DataType.IsNullOrEmpty(id) == true)
			{
				continue;
			}

			GenerWorkFlow gwf = new GenerWorkFlow(Long.parseLong(id));

			//表单方案.
			String frmID = null;
			FrmNodes fns = new FrmNodes();
			fns.Retrieve(FrmNodeAttr.FK_Node, gwf.getNodeID(), null);
			for (FrmNode fn : fns.ToJavaList())
			{
				if (fn.getFrmID().equals("ND" + gwf.getNodeID()) == true)
				{
					continue;
				}

				frmID = fn.getFrmID();
				break;
			}

			if (frmID == null)
			{
				Node_CC_SetCheckOver(gwf.getWorkID(), checkInfo);
				continue;
			}

			//设置阅读track. 这里已经设置了已阅.
			bp.wf.Dev2Interface.Track_WriteBBS(frmID, frmID, gwf.getWorkID(), checkInfo, gwf.getFID(), gwf.getFlowNo(), gwf.getFlowName(), gwf.getNodeID(), gwf.getNodeName());

			//info += Node_CC_SetCheckOver(Int64.Parse(id), checkInfo);
		}
		return "执行成功.";
	}
	/**
	 设置抄送读取

	 @param nodeID 节点ID
	 @param workid 工作ID
	 @param empNo 读取人员编号
	*/

	public static void Node_CC_SetRead(int nodeID, long workid, String empNo)
	{
		Node_CC_SetRead(nodeID, workid, empNo, null);
	}

	public static void Node_CC_SetRead(int nodeID, long workid, String empNo, String bbsCheckInfo)
	{
		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_CCList SET Sta=" + SystemConfig.getAppCenterDBVarStr() + "Sta,ReadDT=" + SystemConfig.getAppCenterDBVarStr() + "ReadDT  WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node AND CCTo=" + SystemConfig.getAppCenterDBVarStr() + "CCTo";
		ps.Add(CCListAttr.Sta, CCSta.UnRead.getValue());
		ps.Add(CCListAttr.ReadDT, DataType.getCurrentDateTime(), false); //设置读取日期.
		ps.Add(CCListAttr.WorkID, workid);
		ps.Add(CCListAttr.NodeIDWork, nodeID);
		ps.Add(CCListAttr.CCTo, empNo, false);

		ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkerlist SET IsRead=1 WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node AND FK_Emp=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp";
		ps.Add(GenerWorkerListAttr.WorkID, workid);
		ps.Add(GenerWorkerListAttr.FK_Node, nodeID);
		ps.Add(GenerWorkerListAttr.FK_Emp, empNo, false);
		DBAccess.RunSQL(ps);

		//   if (bbsCheckInfo!=null)
		//     BP.WF.Dev2Interface.Track_WriteBBS()
	}
	/**
	 执行抄送

	 @param fk_flow 流程编号
	 @param fk_node 节点编号
	 @param workID 工作ID
	 @param toEmpNo 抄送给人员编号
	 @param toEmpName 抄送给人员名称
	 @param msgTitle 消息标题
	 @param msgDoc 消息内容
	 @param pFlowNo 父流程编号(可以为null)
	 @param FID FID(可以为0)
	 @return
	*/

	public static String Node_CC(String fk_flow, int fk_node, long workID, String toEmpNo, String toEmpName, String msgTitle, String msgDoc, long FID, String pFlowNo) throws Exception {
		return Node_CC(fk_flow, fk_node, workID, toEmpNo, toEmpName, msgTitle, msgDoc, FID, pFlowNo, 0);
	}

	public static String Node_CC(String fk_flow, int fk_node, long workID, String toEmpNo, String toEmpName, String msgTitle, String msgDoc, long FID) throws Exception {
		return Node_CC(fk_flow, fk_node, workID, toEmpNo, toEmpName, msgTitle, msgDoc, FID, null, 0);
	}

	public static String Node_CC(String fk_flow, int fk_node, long workID, String toEmpNo, String toEmpName, String msgTitle, String msgDoc) throws Exception {
		return Node_CC(fk_flow, fk_node, workID, toEmpNo, toEmpName, msgTitle, msgDoc, 0, null, 0);
	}

	public static String Node_CC(String fk_flow, int fk_node, long workID, String toEmpNo, String toEmpName, String msgTitle, String msgDoc, long FID, String pFlowNo, long pWorkID) throws Exception {
		Flow fl = new Flow(fk_flow);
		Node nd = new Node(fk_node);

		GenerWorkFlow gwf = new GenerWorkFlow(workID);

		CCList list = new CCList();
		//list.setMyPK(DBAccess.GenerOIDByGUID().ToString(); // workID + "_" + fk_node + "_" + empNo;
		list.setMyPK(workID + "_" + fk_node + "_" + toEmpNo);
		list.setFID(FID);
		list.setFlowNo(fk_flow);
		list.setFlowName(fl.getName());
		list.setNodeIDWork(fk_node);
		list.setNodeName(nd.getName());
		list.setTitle(msgTitle);
		list.setDoc(msgDoc);
		list.setCCTo(toEmpNo);
		list.setCCToName(toEmpName);
		list.setInEmpWorks(nd.getCCWriteTo() == CCWriteTo.CCList ? false : true); //added by liuxc,2015.7.6
		//写入待办和写入待办与抄送列表,状态不同
		if (nd.getCCWriteTo() == CCWriteTo.All || nd.getCCWriteTo() == CCWriteTo.Todolist)
		{
			list.setHisSta(nd.getCCWriteTo() == CCWriteTo.All ? CCSta.UnRead : CCSta.Read);
		}
		try {
			if (nd.getItIsEndNode() == true) //结束节点只写入抄送列表
			{
				list.setHisSta(CCSta.UnRead);
				list.setInEmpWorks(false);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//增加抄送人部门.
		Emp emp = new Emp(toEmpNo);

		list.setRDT(DataType.getCurrentDateTime()); //抄送日期.
		list.setRecEmpNo(WebUser.getNo());
		list.setRecEmpName(WebUser.getName());
		list.setWorkID(workID);
		list.setPFlowNo(pFlowNo);
		list.setPWorkID(pWorkID);
		list.setDomainExt(gwf.getDomainExt());
		list.setOrgNo(gwf.getOrgNo()); //设置组织编号.

		try
		{
			list.Insert();
		}
		catch (java.lang.Exception e)
		{
			// list.CheckPhysicsTable();
			try {
				list.Update();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}


		//记录日志.
		Glo.AddToTrack(ActionType.CC, fk_flow, workID, 0, nd.getNodeID(), nd.getName(), WebUser.getNo(), WebUser.getName(), nd.getNodeID(), nd.getName(), toEmpNo, toEmpName, msgTitle, null);

		//发送邮件.
		bp.wf.Dev2Interface.Port_SendMsg(toEmpNo, WebUser.getName() + "把工作:" + gwf.getTitle(), "抄送:" + msgTitle, "CC" + nd.getNodeID() + "_" + workID + "_", bp.wf.SMSMsgType.CC, gwf.getFlowNo(), gwf.getNodeID(), gwf.getWorkID(), gwf.getFID());

		return "已经成功的把工作抄送给:" + toEmpNo + "," + toEmpName;

	}
	/**
	 抄送到人员s
	 @param nodeID
	 @param workID
	 @param empIDs
	 @param msgTitle
	 @param msgDoc
	 @param FID
	 @param pFlowNo
	 @return 返回执行结果
	*/

	public static String Node_CCToEmps(int nodeID, long workID, String empIDs, String msgTitle, String msgDoc, long FID, String pFlowNo) throws Exception {
		return Node_CCToEmps(nodeID, workID, empIDs, msgTitle, msgDoc, FID, pFlowNo, 0);
	}

	public static String Node_CCToEmps(int nodeID, long workID, String empIDs, String msgTitle, String msgDoc, long FID) throws Exception {
		return Node_CCToEmps(nodeID, workID, empIDs, msgTitle, msgDoc, FID, null, 0);
	}

	public static String Node_CCToEmps(int nodeID, long workID, String empIDs, String msgTitle, String msgDoc) throws Exception {
		return Node_CCToEmps(nodeID, workID, empIDs, msgTitle, msgDoc, 0, null, 0);
	}

	public static String Node_CCToEmps(int nodeID, long workID, String empIDs, String msgTitle, String msgDoc, long FID, String pFlowNo, long pWorkID) throws Exception {
		if (DataType.IsNullOrEmpty(empIDs) == true)
		{
			return "";
		}

		Node nd = new Node(nodeID);
		DataTable dt = DBAccess.RunSQLReturnTable("SELECT No,Name FROM Port_Emp WHERE No IN (" +bp.port.Glo.GenerWhereInSQL(empIDs) + ")");
		if (dt.Rows.size() == 0)
		{
			return "";
		}

		String nos = "";
		String names = "";
		CCList list = new CCList();
		for (DataRow item : dt.Rows)
		{
			String toEmpNo = item.getValue(0).toString();
			String toEmpName = item.getValue(1).toString();
			names += toEmpName + ",";
			nos += toEmpNo + ",";

			list.setMyPK(workID + "_" + nodeID + "_" + toEmpNo);
			list.setFID(FID);
			list.setFlowNo(nd.getFlowNo());
			list.setFlowName(nd.getFlowName());
			list.setNodeIDWork(nodeID);
			list.setNodeName(nd.getName());
			list.SetValByKey(CCListAttr.NodeIDCC,nodeID);
			list.SetValByKey(CCListAttr.NodeIDCCName,nd.getName());
			list.setTitle(msgTitle);
			list.setDoc(msgDoc);
			list.setCCTo(toEmpNo);
			list.setCCToName(toEmpName);
			list.setInEmpWorks(nd.getCCWriteTo() == CCWriteTo.CCList ? false : true); //added by liuxc,2015.7.6
																				  //写入待办和写入待办与抄送列表,状态不同
			if (nd.getCCWriteTo() == CCWriteTo.All || nd.getCCWriteTo() == CCWriteTo.Todolist)
			{
				list.setHisSta(nd.getCCWriteTo() == CCWriteTo.All ? CCSta.UnRead : CCSta.Read);
			}
			try {
				if (nd.getItIsEndNode() == true) //结束节点只写入抄送列表
				{
					list.setHisSta(CCSta.UnRead);
					list.setInEmpWorks(false);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			list.setRDT(DataType.getCurrentDateTime()); //抄送日期.
			list.setRecEmpNo(WebUser.getNo());
			list.setRecEmpName(WebUser.getName());
			list.setWorkID(workID);
			list.setPFlowNo(pFlowNo);
			list.setPWorkID(pWorkID);
			list.setOrgNo(WebUser.getOrgNo()); //设置组织编号.
			try
			{
				list.Insert();
			}
			catch (java.lang.Exception e)
			{
				// list.CheckPhysicsTable();
				try {
					list.Update();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}

			//发送邮件.
			bp.wf.Dev2Interface.Port_SendMsg(toEmpNo, msgTitle, "CC" + nd.getNodeID() + "_" + workID + "_", bp.wf.SMSMsgType.CC, "CC", nd.getFlowNo(), nd.getNodeID(), workID, 0, null);
		}
		//记录日志.
		Glo.AddToTrack(ActionType.CC, nd.getFlowNo(), workID, 0, nd.getNodeID(), nd.getName(), WebUser.getNo(), WebUser.getName(), nd.getNodeID(), nd.getName(), nos, names, msgTitle, null);

		return "手动抄送给:" + names;

	}
	/**
	 删除空白

	 @param workID 要删除的ID
	*/
	public static void Node_DeleteBlank(long workID) throws Exception {
		//设置引擎表.
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workID);
		if (gwf.RetrieveFromDBSources() == 1)
		{
			//if (gwf.NodeID != int.Parse(gwf.FlowNo + "01"))
			//    throw new Exception("@该流程非Blank流程不能删除:" + gwf.Title);

			//if (gwf.WFState != WFState.Blank)
			//    throw new Exception("@非Blank状态不能删除");
			gwf.Delete();
		}

		//删除流程.
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Flow fl = new Flow(gwf.getFlowNo());
		Paras ps = new Paras();
		ps.SQL = "DELETE FROM " + fl.getPTable() + " WHERE OID=" + dbstr + "OID ";
		ps.Add(GERptAttr.OID, workID);
		DBAccess.RunSQL(ps);
	}
	/**
	 删除草稿

	 @param workID 工作ID
	*/
	public static void Node_DeleteDraft(long workID) throws Exception {
		//设置引擎表.
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workID);
		if (gwf.RetrieveFromDBSources() == 1)
		{
			//if (gwf.NodeID != int.Parse(gwf.FlowNo + "01"))
			//    throw new Exception("@该流程非草稿流程不能删除:" + gwf.Title);
			//if (gwf.WFState != WFState.Draft)
			//    throw new Exception("@非草稿状态不能删除");
			gwf.Delete();
		}

		//删除流程.
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Flow fl = new Flow(gwf.getFlowNo());
		Paras ps = new Paras();
		ps.SQL = "DELETE FROM " + fl.getPTable() + " WHERE OID=" + dbstr + "OID ";
		ps.Add(GERptAttr.OID, workID);
		DBAccess.RunSQL(ps);
	}
	/**
	 把草稿设置待办
	 @param workID
	*/
	public static void Node_SetDraft2Todolist(long workID) throws Exception {
		//设置引擎表.
		GenerWorkFlow gwf = new GenerWorkFlow(workID);

		if (gwf.getWFState() == WFState.Draft || gwf.getWFState() == WFState.Blank)
		{
			if (gwf.getNodeID() != Integer.parseInt(gwf.getFlowNo() + "01"))
			{
				throw new RuntimeException("@设置待办错误，只有在开始节点时才能设置待办，现在的节点是:" + gwf.getNodeName());
			}

			GenerWorkerList gwl = new GenerWorkerList();
			int i = gwl.Retrieve(GenerWorkerListAttr.FK_Node, gwf.getNodeID(), GenerWorkerListAttr.WorkID, gwf.getWorkID());
			if (i == 0)
			{
				gwl.setWorkID(gwf.getWorkID());
				gwl.setNodeID(gwf.getNodeID());
				gwl.setNodeName(gwf.getNodeName());
				gwl.setEmpNo(WebUser.getNo());
				gwl.setEmpName(WebUser.getName());
				gwl.setFlowNo(gwf.getFlowNo());
				gwl.setPassInt(0);
				gwl.setItIsEnable(true);
				gwl.setItIsRead(false);
				gwl.setRDT(DataType.getCurrentDateTimess());
				gwl.setCDT(DataType.getCurrentDateTimess());
				gwl.setDTOfWarning(DataType.getCurrentDateTime());
				try {
					gwl.Insert();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else
			{
				gwl.setPassInt(0);
				gwl.Update();
			}

			gwf.setTodoEmps(gwl.getEmpNo() + "," + gwl.getEmpName() + ";");
			gwf.setTodoEmpsNum(1);
			gwf.setWFState(WFState.Runing);
			gwf.Update();

			//重置标题
			try {
				Flow_ReSetFlowTitle(gwf.getWorkID());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return;
		}
	}
	/**
	 设置当前工作的应该完成日期.

	 @param workID 设置的WorkID.
	 @param sdt 应完成日期
	*/
	public static void Node_SetSDT(long workID, String sdt)
	{
		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkerlist SET SDT=" + SystemConfig.getAppCenterDBVarStr() + "SDT WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND IsPass=0";
		ps.Add("SDT", sdt, false);
		ps.Add("WorkID", workID);
		DBAccess.RunSQL(ps);

		ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkFlow SET SDTOfNode=" + SystemConfig.getAppCenterDBVarStr() + "SDTOfNode WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID ";
		ps.Add("SDTOfNode", sdt, false);
		ps.Add("WorkID", workID);
		DBAccess.RunSQL(ps);

	}
	/**
	 设置当前工作状态为草稿,如果启用了草稿, 请在开始节点的表单保存按钮下增加上它.
	 注意:必须是在开始节点时调用.
	 @param workID 工作ID
	*/
	public static void Node_SetDraft(long workID) throws Exception {
		//设置引擎表.
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workID);
		if (gwf.RetrieveFromDBSources() == 0)
		{
			throw new RuntimeException("@工作丢失..");
		}

		if (gwf.getWFState() == WFState.Blank)
		{
			if (gwf.getNodeID() != Integer.parseInt(gwf.getFlowNo() + "01"))
			{
				throw new RuntimeException("@设置草稿错误，只有在开始节点时才能设置草稿，现在的节点是:" + gwf.getTitle());
			}

			gwf.setTodoEmps(WebUser.getNo() + "," + WebUser.getName() + ";");
			gwf.setTodoEmpsNum(1);
			gwf.setWFState(WFState.Draft);
			gwf.Update();

			GenerWorkerList gwl = new GenerWorkerList();
			gwl.setWorkID(workID);
			gwl.setNodeID(Integer.parseInt(gwf.getFlowNo() + "01"));
			gwl.setEmpNo(WebUser.getNo());
			try {
				if (gwl.RetrieveFromDBSources() == 0)
				{
					gwl.setEmpName(WebUser.getName());
					gwl.setPassInt(0);
					gwl.setSDT("无");
					gwl.setDTOfWarning(DataType.getCurrentDateTime());
					gwl.setItIsEnable(true);
					gwl.setItIsRead(true);
					gwl.setItIsPass(false);
					gwl.Insert();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		Flow fl = null;
		try {
			fl = new Flow(gwf.getFlowNo());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//String sql = "UPDATE "+fl.PTable+" SET WFStarter=1, FlowStater='"+WebUser.No+"' WHERE OID="+workID;

		String sql = "UPDATE " + fl.getPTable() + " SET  FlowStarter='" + WebUser.getNo() + "',WFState=1 WHERE OID=" + workID;
		DBAccess.RunSQL(sql);
	}
	/**
	 保存参数，向工作流引擎传入的参数变量.

	 @param workID 工作ID
	 @param paras 参数
	 @return
	*/
	public static boolean Flow_SaveParas(long workID, String paras) throws Exception {
		AtPara ap = new AtPara(paras);
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		for (String key : ap.getHisHT().keySet())
		{
			gwf.SetPara(key, ap.GetValStrByKey(key));
		}
		gwf.Update();
		return true;
	}

	/**
	 保存

	 @param workID workid
	 @param wk 节点表单参数
	 @return
	*/


	public static String Node_SaveWork(long workID, Hashtable wk) throws Exception {
		return Node_SaveWork(workID, wk, null,null);
	}
	public static String Node_SaveWork(long workID, Hashtable wk,DataSet dsDtls) throws Exception {
		return Node_SaveWork(workID, wk, dsDtls,null);
	}
	public static String Node_SaveWork(long workID, Hashtable wk,DataSet dsDtls,DataSet dsAths) throws Exception {
		return Node_SaveWork(workID, wk, dsDtls,dsAths,false);
	}
	/**
	 保存
	 @param workID 工作ID
	 @param htWork 工作数据
	 @return 返回执行信息
	*/
	public static String Node_SaveWork(long workID, Hashtable htWork, DataSet dsDtls,DataSet dsAths,boolean isSend) throws Exception {
		if (htWork == null)
			return "参数错误，htWork 不能为空, 保存失败。";

		isSend = true;
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		try
		{
			Node nd = new Node(gwf.getNodeID());
			if (nd.getItIsStartNode() == false)
			{
				if (nd.getItIsEndNode() == false && WebUser.getIsAdmin() == false)
				{
					if (Dev2Interface.Flow_IsCanDoCurrentWork(workID, WebUser.getNo()) == false)
					{
						return "没有执行保存.";
					}
				}
				//这里取消了保存异常.
				//throw new Exception("err@工作已经发送到下一个环节,您不能执行保存.");
			}

			if (nd.getHisFormType() == NodeFormType.RefOneFrmTree)
			{
				FrmNode frmNode = new FrmNode(nd.getNodeID(), nd.getNodeFrmID());
				switch (frmNode.getWhoIsPK())
				{
					case FID:
						workID = gwf.getFID();
						break;
					case PWorkID:
						workID = gwf.getPWorkID();
						break;
					case P2WorkID:
						GenerWorkFlow gwfP = null;
						try {
							gwfP = new GenerWorkFlow(gwf.getPWorkID());
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						workID = gwfP.getPWorkID();
						break;
					case P3WorkID:
						String sql = "SELECT PWorkID FROM WF_GenerWorkFlow WHERE WorkID=(SELECT PWorkID FROM WF_GenerWorkFlow WHERE WorkID=" + gwf.getPWorkID() + ")";
						workID = DBAccess.RunSQLReturnValInt(sql, 0);
						break;
					default:
						break;
				}
			}
			Work wk = nd.getHisWork();
			if (workID != 0)
			{
				wk.setOID(workID);
				wk.RetrieveFromDBSources();
			}
			try {
				wk.ResetDefaultVal(null, null, 0);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}


			///#region 赋值.
			//Attrs attrs = wk.getEnMap().getAttrs();
			for (Object str : htWork.keySet())
			{
				if(str == null)
					continue;
				switch (str.toString())
				{
					case GERptAttr.OID:
					case WorkAttr.MD5:
					case WorkAttr.Emps:
					case GERptAttr.FID:
					case GERptAttr.FK_Dept:
					case GERptAttr.Rec:
					case GERptAttr.Title:
						continue;
					default:
						break;
				}
				if (wk.getRow().containsKey(str))
				{
					wk.SetValByKey(str.toString(), htWork.get(str));
				}
				else
				{
					wk.getRow().put(str.toString(), htWork.get(str));
				}
			}

				///#endregion 赋值.

			wk.setRec(WebUser.getNo());
			wk.SetValByKey(GERptAttr.FK_Dept, WebUser.getDeptNo());
			ExecEvent.DoFrm(nd.getMapData(), EventListFrm.SaveBefore, wk);
			wk.Save();


			///#region 保存从表
			if (dsDtls != null)
			{
				//保存从表
				for (DataTable dt : dsDtls.Tables)
				{
					for (MapDtl dtl : wk.getHisMapDtls().ToJavaList())
					{
						if(dt.TableName.equals(dtl.getNo()) == false){
							if(DataType.IsNullOrEmpty(dtl.getAlias()) == true)
								continue;
							if(DataType.IsNullOrEmpty(dtl.getAlias()) ==false && dt.TableName.equals(dtl.getAlias()) == false)
								continue;
						}

						//获取dtls
						GEDtls daDtls = new GEDtls(dtl.getNo());
						daDtls.Delete(GEDtlAttr.RefPK, workID); // 清除现有的数据.

						// 为从表复制数据.
						for (DataRow dr : dt.Rows)
						{
							bp.en.Entity tempVar = daDtls.getNewEntity();
							GEDtl daDtl = tempVar instanceof GEDtl ? (GEDtl)tempVar : null;
							daDtl.setRefPK(String.valueOf(workID));
							//明细列.
							for (DataColumn dc : dt.Columns)
							{
								//设置属性.
								daDtl.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName));
							}

							daDtl.ResetDefaultVal(null, null, 0);

							daDtl.setRefPK(String.valueOf(workID));
							daDtl.setRDT(DataType.getCurrentDateTime());

							//执行保存.
							daDtl.InsertAsOID(DBAccess.GenerOID("Dtl")); //插入数据.
						}
					}
				}
			}

			///#endregion 保存从表结束

			///验证从表必填项
			if(isSend == true){
				MapDtls mapdtls = new MapDtls();
				mapdtls.Retrieve(MapDtlAttr.FK_MapData, nd.getNodeFrmID(),null);
				for (MapDtl mapDtl : mapdtls.ToJavaList())
				{
					String msg = "err@请重新填写明细表必填字段：";
					MapAttrs mapAttrs = mapDtl.getMapAttrs();
					GEDtls dtls = new GEDtls(mapDtl.getNo());
					dtls.Retrieve(GEDtlAttr.RefPK, workID, "OID");

					for (GEDtl dtl : dtls.ToJavaList())
					{
						for (MapAttr mapAttr : mapAttrs.ToJavaList())
						{
							if (mapAttr.getUIIsInput())
							{
								String val = dtl.getRow().GetValByKey(mapAttr.getKeyOfEn()).toString();
								if(DataType.IsNullOrEmpty(val)){
									msg += "明细表【"+mapDtl.getName()+" 】："+mapAttr.getName()+"，";
								}
							}

						}
					}

					if(msg.length() >17){
						msg += "@";
						throw new RuntimeException(msg);
					}
				}
			}

			//#region 保存附件.
			if (dsAths != null)
			{
				//定义对象.
				FrmAttachmentDBs dbs = new FrmAttachmentDBs();
				//保存从表
				for(DataTable dt : dsAths.Tables)
				{
					for (FrmAttachment ath : wk.getHisFrmAttachments().ToJavaList())
					{
						if (dt.TableName.equals(ath.getNoOfObj()) == false)
							continue;

						dbs.Delete(FrmAttachmentDBAttr.RefPKVal, workID); // 清除现有的数据.

						//临时路径
						String rootTempPath = SystemConfig.getPathOfTemp() + workID;
						if ((new File(rootTempPath)).isDirectory() == false)
						{
							(new File(rootTempPath)).mkdirs();
						}

						String pkval = String.valueOf(gwf.getWorkID());
						if(ath.getAthSaveWay() == AthSaveWay.SaveLink)
						{
							//求主键. 如果该表单挂接到流程上.
							if (gwf.getNodeID() != 0)
							{
								//判断表单方案。
								FrmNode fn = new FrmNode(gwf.getNodeID(), ath.getFrmID());
								if (fn.getFrmSln() == FrmSln.Readonly)
									return "err@"+ath.getName()+"不允许上传附件.";
								//是默认的方案的时候.
								if (fn.getFrmSln() == FrmSln.Default)
								{
									//判断当前方案设置的whoIsPk ，让附件集成 whoIsPK 的设置。
									if (fn.getWhoIsPK() == WhoIsPK.FID)
										pkval = String.valueOf(gwf.getFID());

									if (fn.getWhoIsPK() == WhoIsPK.PWorkID)
										pkval = String.valueOf(gwf.getPWorkID());
								}
								//自定义方案.
								if (fn.getFrmSln() == FrmSln.Self)
								{
									FrmAttachment athDesc = new FrmAttachment(ath.getMyPK() + "_" + gwf.getNodeID());
									if (athDesc.getHisCtrlWay() == AthCtrlWay.FID)
										pkval = String.valueOf(gwf.getFID());

									if (athDesc.getHisCtrlWay() == AthCtrlWay.PWorkID)
										pkval =  String.valueOf(gwf.getPWorkID());
								}
							}
						}

						// 复制数据.
						for(DataRow dr : dt.Rows)
						{
							String fileName = dr.getValue(0)==null?"" :dr.getValue(0).toString(); //文件名称.
							String fileUrl = dr.getValue(1)==null?"":dr.getValue(1).toString();  //文件路径. http://xxx.xxx.xxx/myfile.doc;
							if (DataType.IsNullOrEmpty(fileUrl) == true || DataType.IsNullOrEmpty(fileName)==true)
								return "err@附件的存储的URL或者附件名称不能为空";
							try
							{
								//1.下载文件到本机.

								String tempPath = rootTempPath + "/" + fileName;

								// 提交下载任务给线程池
								executorService.submit(new AthDownloadTask(fileUrl, tempPath));

								//保存数据.
								if(ath.getAthSaveWay() == AthSaveWay.SaveLink)
								{
									FrmAttachmentDB athDB = new FrmAttachmentDB();
									athDB.setMyPK(DBAccess.GenerGUID());
									athDB.setSort("");
									athDB.setNodeID(gwf.getNodeID());
									athDB.setRefPKVal(pkval);
									athDB.setFrmID(ath.getFrmID());
									athDB.setFKFrmAttachment(ath.getMyPK());
									athDB.setFileName(fileName);
									athDB.setFileExts(fileName.substring(fileName.lastIndexOf(".")));
									File info = new File(tempPath);
									athDB.setFileSize((float)info.length());
									athDB.setFileFullName(fileUrl);
									athDB.setRDT(DataType.getCurrentDateTimess());
									athDB.setRec(bp.web.WebUser.getNo());
									athDB.setRecName(bp.web.WebUser.getName());
									athDB.setDeptNo(WebUser.getDeptNo());
									athDB.setDeptName(WebUser.getDeptName());
									athDB.Insert();
									info.delete();
									continue;
								}
								//保存附件到服务器
								bp.wf.Dev2Interface.CCForm_AddAth(gwf.getNodeID(), gwf.getFlowNo(), workID, ath.getMyPK(), ath.getFrmID(), tempPath, fileName, null,"", gwf.getFID(), gwf.getPWorkID());

							}
							catch (Exception ex)
							{
							}
						}
					}
				}
			}
			//#endregion 保存附件.


				///#region 更新发送参数.
			if (htWork != null)
			{
				String paras = "";
				for (Object key : htWork.keySet())
				{
					 Object val= htWork.get(key);
					if (val == null)
						val = "";
					paras += "@" + key + "=" + val.toString();
				}

				if (DataType.IsNullOrEmpty(paras) == false && Glo.isEnableTrackRec() == true)
				{
					String dbstr = SystemConfig.getAppCenterDBVarStr();
					Paras ps = new Paras();
					ps.SQL = "UPDATE WF_GenerWorkerlist SET AtPara=" + dbstr + "Paras WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + dbstr + "FK_Node";
					ps.Add(GenerWorkerListAttr.Paras, paras, false);
					ps.Add(GenerWorkerListAttr.WorkID, workID);
					ps.Add(GenerWorkerListAttr.FK_Node, nd.getNodeID());
					DBAccess.RunSQL(ps);
				}
			}

				///#endregion 更新发送参数.

			if (nd.getSaveModel() == SaveModel.NDAndRpt)
			{
				/* 如果保存模式是节点表与Node与Rpt表. */
				WorkNode wn = new WorkNode(wk, nd);
				GERpt rptGe = nd.getHisFlow().getHisGERpt();
				rptGe.SetValByKey("OID", workID);
				wn.rptGe = rptGe;
				if (rptGe.RetrieveFromDBSources() == 0)
				{
					rptGe.SetValByKey("OID", workID);
					wn.DoCopyWorkToRpt(wk);

					if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserIDUserName)
					{
						rptGe.SetValByKey(GERptAttr.FlowEmps, "@" + WebUser.getNo() + "," + WebUser.getName() + "@");
					}

					if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserIDOnly)
					{
						rptGe.SetValByKey(GERptAttr.FlowEmps, "@" + WebUser.getNo() + "@");
					}

					if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserNameOnly)
					{
						rptGe.SetValByKey(GERptAttr.FlowEmps, "@" + WebUser.getName() + "@");
					}

					rptGe.SetValByKey(GERptAttr.FlowStarter, WebUser.getNo());
					rptGe.SetValByKey(GERptAttr.FlowStartRDT, DataType.getCurrentDateTime());
					rptGe.SetValByKey(GERptAttr.WFState, 0);
					rptGe.SetValByKey(GERptAttr.FK_NY, DataType.getCurrentYearMonth());
					rptGe.SetValByKey(GERptAttr.FK_Dept, WebUser.getDeptNo());
					rptGe.Insert();
				}
				else
				{
					wn.DoCopyWorkToRpt(wk);
					rptGe.Update();
				}
			}else{
				if (htWork.get("AtPara")!=null && DataType.IsNullOrEmpty(htWork.get("AtPara").toString()) == false)
					DBAccess.RunSQL("UPDATE " + nd.getHisFlow().getPTable() + " SET AtPara='" + htWork.get("AtPara") + "' WHERE OID=" + wk.getOID());
			}
			//获取表单树的数据
			bp.wf.WorkNode workNode = new WorkNode(workID, gwf.getNodeID());
			Work treeWork = workNode.CopySheetTree();
			if (treeWork != null)
			{
				wk.Copy(treeWork);
				wk.Update();
			}


				///#region 处理保存后事件
			boolean isHaveSaveAfter = false;
			try
			{
				//处理表单保存后.
				String s = ExecEvent.DoFrm(nd.getMapData(), EventListFrm.SaveAfter, wk);
				//执行保存前事件.
				s += ExecEvent.DoNode(EventListNode.NodeFrmSaveAfter, workNode, null);

				if (s != null)
				{
					/*如果不等于null,说明已经执行过数据保存，就让其从数据库里查询一次。*/
					wk.RetrieveFromDBSources();
					isHaveSaveAfter = true;
				}
			}
			catch (RuntimeException ex)
			{
				return "err@在执行保存后的事件期间出现错误:" + ex.getMessage();
			}

				///#endregion

			Flow fl = new Flow(gwf.getFlowNo());
			//不是开始节点
			String titleRoleNodes = fl.getTitleRoleNodes();
			if (DataType.IsNullOrEmpty(titleRoleNodes) == false)
			{
				if (titleRoleNodes.contains( String.valueOf(nd.getNodeID()))   || titleRoleNodes.equals("*") == true)
				{
					//设置标题.
					String title = bp.wf.WorkFlowBuessRole.GenerTitle(fl, wk);

					//修改RPT表的标题
					wk.SetValByKey(GERptAttr.Title, title);
					wk.Update();

					//更新标题.
					gwf.setTitle(title); //标题.
					gwf.Update();
				}
			}
			 //#region 草稿规则.
			if (nd.getItIsStartNode() && gwf.getWFState() == WFState.Blank)
			{
				DraftRole draft =fl.getDraftRole();

				//保存待办.
				if (draft == DraftRole.SaveToTodolist)
					DBAccess.RunSQL("UPDATE WF_GenerWorkFlow SET WFState=2 WHERE WorkID=" + workID);
				//保存草稿.
				if (draft == DraftRole.SaveToDraftList)
					bp.wf.Dev2Interface.Node_SetDraft(workID);
			}
           //#endregion 标题生成规则.
			return "保存成功.";
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("err@保存错误:" + ex.getMessage() + ", 技术信息：" + ex.getStackTrace());
		}
	}
	/**
	 保存独立表单

	 @param fk_mapdata 独立表单ID
	 @param workID 工作ID
	 @param htData 独立表单数据Key Value 格式存放.
	 @return 返回执行信息
	*/
	public static void Node_SaveFlowSheet(String fk_mapdata, long workID, Hashtable htData) throws Exception {
		Node_SaveFlowSheet(fk_mapdata, workID, htData, null);
	}
	/**
	 保存独立表单

	 @param fk_mapdata 独立表单ID
	 @param workID 工作ID
	 @param htData 独立表单数据Key Value 格式存放.
	 @param workDtls 从表数据
	 @return 返回执行信息
	*/
	public static void Node_SaveFlowSheet(String fk_mapdata, long workID, Hashtable htData, DataSet workDtls) throws Exception {
		MapData md = new MapData(fk_mapdata);
		GEEntityOID en = md.getHisGEEn();
		en.SetValByKey("OID", workID);
		int i = en.RetrieveFromDBSources();

		for (Object key : htData.keySet())
		{
			en.SetValByKey(key.toString(), htData.get(key).toString());
		}

		en.SetValByKey("OID", workID);

		ExecEvent.DoFrm(md, EventListFrm.SaveBefore, en);

		if (i == 0)
		{
			en.Insert();
		}
		else
		{
			en.Update();
		}

		try {
			ExecEvent.DoFrm(md, EventListFrm.SaveAfter, en);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}


		if (workDtls != null)
		{
			MapDtls dtls = new MapDtls(fk_mapdata);
			//保存从表
			for (DataTable dt : workDtls.Tables)
			{
				for (MapDtl dtl : dtls.ToJavaList())
				{
					if (!Objects.equals(dt.TableName, dtl.getNo()))
					{
						continue;
					}
					//获取dtls
					GEDtls daDtls = new GEDtls(dtl.getNo());
					try {
						daDtls.Delete(GEDtlAttr.RefPK, workID); // 清除现有的数据.
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

					bp.en.Entity tempVar = daDtls.getNewEntity();
					GEDtl daDtl = tempVar instanceof GEDtl ? (GEDtl)tempVar : null;
					daDtl.setRefPK(String.valueOf(workID));

					// 为从表复制数据.
					for (DataRow dr : dt.Rows)
					{
						daDtl.ResetDefaultVal(null, null, 0);
						daDtl.setRefPK(String.valueOf(workID));

						//明细列.
						for (DataColumn dc : dt.Columns)
						{
							//设置属性.
							daDtl.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName));
						}
						try {
							daDtl.InsertAsOID(DBAccess.GenerOID("Dtl")); //插入数据.
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
		ExecEvent.DoFrm(md, EventListFrm.SaveAfter, en);
	}
	/**
	 从任务池里取出来一个子任务

	 @param workid 工作ID
	*/
	public static boolean Node_TaskPoolTakebackOne(long workid) throws Exception {
		if (Glo.isEnableTaskPool() == false)
		{
			throw new RuntimeException("@配置没有设置成共享任务池的状态。");
		}

		GenerWorkFlow gwf = new GenerWorkFlow(workid);
		if (gwf.getTaskSta() == TaskSta.None)
		{
			throw new RuntimeException("@该任务非共享任务。");
		}

		if (gwf.getTaskSta() == TaskSta.Takeback)
		{
			throw new RuntimeException("@该任务已经被其他人取走。");
		}

		//更新状态。
		gwf.setTaskSta(TaskSta.Takeback);
		gwf.Update();

		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		//设置已经被取走的状态。
		ps.SQL = "UPDATE WF_GenerWorkerlist SET IsEnable=-1 WHERE IsEnable=1 AND WorkID=" + dbstr + "WorkID AND FK_Node=" + dbstr + "FK_Node AND FK_Emp!=" + dbstr + "FK_Emp ";
		ps.Add(GenerWorkerListAttr.WorkID, workid);
		ps.Add(GenerWorkerListAttr.FK_Node, gwf.getNodeID());
		ps.Add(GenerWorkerListAttr.FK_Emp, WebUser.getNo(), false);
		int i = DBAccess.RunSQL(ps);

		try {
			Dev2Interface.WriteTrackInfo(gwf.getFlowNo(), gwf.getNodeID(), gwf.getNodeName(), workid, 0, "任务被" + WebUser.getName() + "从任务池取走.", "获取");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (i > 0)
		{
			Paras ps1 = new Paras();
			//取走后 将WF_GenerWorkFlow 中的 TodoEmps,TodoEmpsNum 修改下  杨玉慧
			ps1.SQL = "UPDATE WF_GenerWorkFlow SET TodoEmps=" + dbstr + "TodoEmps,TodoEmpsNum=1 WHERE  WorkID=" + dbstr + "WorkID";
			String toDoEmps = WebUser.getNo() + "," + WebUser.getName();
			ps1.Add(GenerWorkFlowAttr.TodoEmps, toDoEmps, false);
			ps1.Add(GenerWorkerListAttr.WorkID, workid);
			Log.DebugWriteInfo(toDoEmps);
			Log.DebugWriteInfo(ps1.SQL);
			DBAccess.RunSQL(ps1);
		}

		if (i == 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 获取当前节点可以发送到其他节点的集合

	 @param gwf
	 @param nd
	 @return
	*/
	public static DataTable Node_GenerDTOfToNodes(GenerWorkFlow gwf, Node nd) throws Exception {
		//增加转向下拉框数据.
		if (nd.getCondModel() == DirCondModel.ByDDLSelected || nd.getCondModel() == DirCondModel.ByButtonSelected)
		{
		}
		else
		{
			return null;
		}
		DataTable dtToNDs = new DataTable("ToNodes");
		dtToNDs.Columns.Add("No", String.class); //节点ID.
		dtToNDs.Columns.Add("Name", String.class); //到达的节点名称.
		dtToNDs.Columns.Add("IsSelectEmps", String.class); //是否弹出选择人的对话框？
		dtToNDs.Columns.Add("IsSelected", String.class); //是否选择？
		dtToNDs.Columns.Add("DeliveryParas", String.class); //自定义URL

		DataRow dr = dtToNDs.NewRow();

		if (nd.getItIsStartNode() == true || (gwf.getTodoEmps().contains(WebUser.getNo() + ",") == true))
		{
			/*如果当前不是主持人,如果不是主持人，就不让他显示下拉框了.*/

			/*如果当前节点，是可以显示下拉框的.*/
			//Nodes nds = nd.getHisToNodes();

			NodeSimples nds = nd.getHisToNodeSimples();


				///#region 增加到达延续子流程节点。 @lizhen.
			if (nd.getSubFlowYanXuNum() >= 1)
			{
				SubFlowYanXus ygflows = new SubFlowYanXus(nd.getNodeID());
				for (SubFlowYanXu item : ygflows.ToJavaList())
				{
					String[] yanxuToNDs = item.getYanXuToNode().split("[,]", -1);
					for (String str : yanxuToNDs)
					{
						if (DataType.IsNullOrEmpty(str) == true)
						{
							continue;
						}

						int toNodeID = Integer.parseInt(str);

						Node subNode = new Node(toNodeID);

						dr = dtToNDs.NewRow(); //创建行。 @lizhen.

						//延续子流程跳转过了开始节点
						if (toNodeID == Integer.parseInt(Integer.parseInt(item.getSubFlowNo()) + "01"))
						{
							dr.setValue("No", String.valueOf(toNodeID));
							dr.setValue("Name", "启动:" + item.getSubFlowName() + " - " + subNode.getName());
							dr.setValue("IsSelectEmps", "1");
							dr.setValue("IsSelected", "0");
							dtToNDs.Rows.add(dr);
						}
						else
						{

							dr.setValue("No", String.valueOf(toNodeID));
							dr.setValue("Name", "启动:" + item.getSubFlowName() + " - " + subNode.getName());
							if (subNode.getHisDeliveryWay() == DeliveryWay.BySelected)
							{
								dr.setValue("IsSelectEmps", "1");
							}
							else
							{
								dr.setValue("IsSelectEmps", "0");
							}
							dr.setValue("IsSelected", "0");
							dtToNDs.Rows.add(dr);
						}
					}
				}
			}

				///#endregion 增加到达延续子流程节点。


				///#region 到达其他节点.
			//上一次选择的节点.
			int defalutSelectedNodeID = 0;
			if (nds.size() > 1)
			{
				String mysql = "";
				// 找出来上次发送选择的节点.
				if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
				{
					mysql = "SELECT  top 1 NDTo FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track A WHERE A.NDFrom=" + nd.getNodeID() + " AND ActionType=1 ORDER BY WorkID DESC";
				}
				else if (SystemConfig.getAppCenterDBType() == DBType.DM ||SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
				{
					mysql = "SELECT * FROM ( SELECT  NDTo FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track A WHERE A.NDFrom=" + nd.getNodeID() + " AND ActionType=1 ORDER BY WorkID DESC ) WHERE ROWNUM =1";
				}
				else if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
				{
					mysql = "SELECT  NDTo FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track A WHERE A.NDFrom=" + nd.getNodeID() + " AND ActionType=1 ORDER BY WorkID  DESC limit 1,1";
				}
				else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX)
				{
					mysql = "SELECT  NDTo FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track A WHERE A.NDFrom=" + nd.getNodeID() + " AND ActionType=1 ORDER BY WorkID  DESC limit 1";
				}

				//获得上一次发送到的节点.
				defalutSelectedNodeID = DBAccess.RunSQLReturnValInt(mysql, 0);
			}

				///#region 为天业集团做一个特殊的判断.
			if (Objects.equals(SystemConfig.getCustomerNo(), "TianYe") && nd.getName().contains("董事长") == true)
			{
				/*如果是董事长节点, 如果是下一个节点默认的是备案. */
				for (NodeSimple item : nds.ToJavaList())
				{
					if (item.getName().contains("备案") == true && item.getName().contains("待") == false)
					{
						defalutSelectedNodeID = item.getNodeID();
						break;
					}
				}
			}

				///#endregion 为天业集团做一个特殊的判断.


				///#region 是否增加退回的节点
			int returnNode = 0;
			if (gwf.getWFState() == WFState.ReturnSta && nd.GetParaInt("IsShowReturnNodeInToolbar", 0) == 1)
			{
				String trackTable = "ND" + Integer.parseInt(gwf.getFlowNo()) + "Track";
				String sql = "SELECT NDFrom,NDFromT From " + trackTable + " WHERE WorkID=" + gwf.getWorkID() + " AND NDTo=" + gwf.getNodeID() + " AND EmpTo='" + WebUser.getNo() + "' Order By RDT DESC";
				DataTable dt = DBAccess.RunSQLReturnTable(sql);
				/* ReturnWorks returnWorks = new ReturnWorks();
				QueryObject qo = new QueryObject(returnWorks);
				qo.AddWhere(ReturnWorkAttr.WorkID, gwf.WorkID);
				qo.addAnd();
				qo.AddWhere(ReturnWorkAttr.ReturnToNode, gwf.getNodeID());
				qo.addAnd();
				qo.AddWhere(ReturnWorkAttr.ReturnToEmp, WebUser.getNo());
				qo.addOrderByDesc(ReturnWorkAttr.RDT);
				qo.DoQuery();*/
				if (dt.Rows.size() != 0)
				{
					dr = dtToNDs.NewRow();
					dr.setValue("No", dt.Rows.get(0).getValue("NDFrom"));
					dr.setValue("Name", dt.Rows.get(0).getValue("NDFromT") + "(退回)");
					dr.setValue("IsSelected", "1");
					dr.setValue("IsSelectEmps", "0");
					dtToNDs.Rows.add(dr);
					returnNode = Integer.parseInt(dt.Rows.get(0).getValue("NDFrom").toString());
					defalutSelectedNodeID = 0; //设置默认。
				}
			}

				///#endregion 是否增加退回的节点.

			for (NodeSimple item : nds.ToJavaList())
			{
				if (item.getNodeID() == returnNode)
				{
					continue;
				}

				dr = dtToNDs.NewRow();
				dr.setValue("No", item.getNodeID());
				dr.setValue("Name", item.getName());

				//判断到达的节点是不是双向箭头的节点
				if (item.getItIsResetAccepter() == false && item.getHisToNDs().contains("@" + nd.getNodeID()) == true && nd.getHisToNDs().contains("@" + item.getNodeID()) == true)
				{
					GenerWorkerLists gwls = new GenerWorkerLists();
					gwls.Retrieve(GenerWorkerListAttr.WorkID, gwf.getWorkID(), GenerWorkerListAttr.FK_Node, item.getNodeID(), GenerWorkerListAttr.IsPass, 1, null);
					if (gwls.size() > 0)
					{
						dr.setValue("IsSelectEmps", "0");
						//设置默认选择的节点.
						if (defalutSelectedNodeID == item.getNodeID())
						{
							dr.setValue("IsSelected", "1");
						}
						else
						{
							dr.setValue("IsSelected", "0");
						}

						dtToNDs.Rows.add(dr);
						continue;
					}
				}

				if (item.getHisDeliveryWay() == DeliveryWay.BySelected)
				{
					dr.setValue("IsSelectEmps", "1");
				}
				else if (item.getHisDeliveryWay() == DeliveryWay.BySelfUrl)
				{
					dr.setValue("IsSelectEmps", "2");
					dr.setValue("DeliveryParas", item.getDeliveryParas());
				}
				else if (item.getHisDeliveryWay() == DeliveryWay.BySelectedEmpsOrgModel)
				{
					dr.setValue("IsSelectEmps", "3");
				}
				else if (item.getHisDeliveryWay() == DeliveryWay.BySelectEmpByOfficer)
				{
					dr.setValue("IsSelectEmps", "5");
				}
				else
				{
					dr.setValue("IsSelectEmps", "0"); //是不是，可以选择接受人.
				}

				//设置默认选择的节点.
				if (defalutSelectedNodeID == item.getNodeID())
				{
					dr.setValue("IsSelected", "1");
				}
				else
				{
					dr.setValue("IsSelected", "0");
				}

				dtToNDs.Rows.add(dr);
			}

				///#endregion 到达其他节点。
		}

		return dtToNDs;
	}

	/**
	 放入一个任务
	 @param workid 工作ID
	*/
	public static void Node_TaskPoolPutOne(long workid) throws Exception {
		if (Glo.isEnableTaskPool() == false)
		{
			throw new RuntimeException("@配置没有设置成共享任务池的状态。");
		}

		GenerWorkFlow gwf = new GenerWorkFlow(workid);
		if (gwf.getTaskSta() == TaskSta.None)
		{
			throw new RuntimeException("@该任务非共享任务。");
		}

		if (gwf.getTaskSta() == TaskSta.Sharing)
		{
			throw new RuntimeException("@该任务已经是共享状态。");
		}

		// 更新 状态。
		gwf.setTaskSta(TaskSta.Sharing);
		gwf.Update();

		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		//设置已经被取走的状态。
		ps.SQL = "UPDATE WF_GenerWorkerlist SET IsEnable=1 WHERE IsEnable=-1 AND WorkID=" + dbstr + "WorkID ";
		ps.Add(GenerWorkerListAttr.WorkID, workid);
		int i = DBAccess.RunSQL(ps);
		if (i < 0) //有可能是只有一个人
		{
			throw new RuntimeException("@流程数据错误,不应当更新不到数据。");
		}

		if (i > 0)
		{
			Paras ps1 = new Paras();
			//设置已经被取走的状态。
			ps1.SQL = "SELECT FK_Emp,EmpName FROM WF_GenerWorkerlist  WHERE IsEnable=1 AND WorkID=" + dbstr + "WorkID AND FK_Node=" + dbstr + "FK_Node ";
			ps1.Add(GenerWorkerListAttr.WorkID, workid);
			ps1.Add(GenerWorkerListAttr.FK_Node, gwf.getNodeID());
			ps1.Add(GenerWorkerListAttr.FK_Emp, WebUser.getNo(), false);
			DataTable toDoEmpsTable = DBAccess.RunSQLReturnTable(ps1);
			String toDoEmps = "";
			String toDoEmpsNum = "";
			if (toDoEmpsTable == null || toDoEmpsTable.Rows.size() == 0)
			{
				throw new RuntimeException("@流程数据错误,没有找到需更新的待处理人。");
			}

			toDoEmpsNum = String.valueOf(toDoEmpsTable.Rows.size());
			for (DataRow dr : toDoEmpsTable.Rows)
			{
				toDoEmps += String.format("%1$s,%2$s", dr.getValue("FK_Emp").toString(), dr.getValue("EmpName").toString()) + ";";
			}
			Paras ps2 = new Paras();
			//将任务放回后 将WF_GenerWorkFlow 中的 TodoEmps,TodoEmpsNum 修改下  杨玉慧
			ps2.SQL = "UPDATE WF_GenerWorkFlow SET TodoEmps=" + dbstr + "TodoEmps,TodoEmpsNum=" + dbstr + "TodoEmpsNum WHERE  WorkID=" + dbstr + "WorkID";
			ps2.Add(GenerWorkFlowAttr.TodoEmps, toDoEmps, false);
			ps2.Add(GenerWorkFlowAttr.TodoEmpsNum, toDoEmpsNum, false);
			ps2.Add(GenerWorkerListAttr.WorkID, workid);
			Log.DebugWriteInfo(toDoEmps);
			Log.DebugWriteInfo(ps2.SQL);
			DBAccess.RunSQL(ps2);
		}

		bp.wf.Dev2Interface.WriteTrackInfo(gwf.getFlowNo(), gwf.getNodeID(), gwf.getNodeName(), workid, 0, "任务被" + WebUser.getName() + "放入了任务池.", "放入");
	}
	/**
	 通用选择器下一点接收人保存到SelectAccper表中

	 @param workID
	 @param toNodeID
	 @param emps
	 @param stas
	 @param depts
	*/

	public static String Node_AddNextStepAccepters(long workID, int toNodeID, String emps, String depts, String stas) throws Exception {
		return Node_AddNextStepAccepters(workID, toNodeID, emps, depts, stas, true);
	}

	public static String Node_AddNextStepAccepters(long workID, int toNodeID, String emps, String depts) throws Exception {
		return Node_AddNextStepAccepters(workID, toNodeID, emps, depts, null, true);
	}


	public static String Node_AddNextStepAccepters(long workID, int toNodeID) throws Exception {
		return Node_AddNextStepAccepters(workID, toNodeID, null, null, null, true);
	}

	public static String Node_AddNextStepAccepters(long workID, int toNodeID, String emps, String depts, String stas, boolean del_Selected) throws Exception {
		if (DataType.IsNullOrEmpty(emps) == true && DataType.IsNullOrEmpty(stas) == true && DataType.IsNullOrEmpty(depts) == true)
		{
			return "err@请选择下一个节点的接受人";
		}

		SelectAccper sa = new SelectAccper();
		//删除历史选择
		if (del_Selected == true)
		{
			sa.Delete(SelectAccperAttr.FK_Node, toNodeID, SelectAccperAttr.WorkID, workID);
		}

		//检查是否是单选？
		Selector st = new Selector(toNodeID);
		if (st.getItIsSimpleSelector() == true)
		{
			sa.Delete(SelectAccperAttr.FK_Node, toNodeID, SelectAccperAttr.WorkID, workID);
		}
		String sendEmps = ",";
		Emp emp = null;
		//发送到人
		if (emps != null)
		{
			String[] empStrs = emps.split("[,]", -1);
			for (String empNo : empStrs)
			{
				if (DataType.IsNullOrEmpty(empNo) == true)
				{
					continue;
				}


				emp = new Emp();
				emp.setUserID(empNo);
				try {
					if (emp.RetrieveFromDBSources() == 0)
					{
						continue;
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				sendEmps += emp.getUserID() + ",";
				sa.setEmpNo(emp.getUserID());
				sa.setEmpName(emp.getName());
				sa.setDeptName(emp.getDeptText());
				sa.setNodeID(toNodeID);
				sa.setWorkID(workID);
				sa.ResetPK();
				try {
					if (sa.IsExits() == false)
					{
						sa.Insert();
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		String sql = "";
		String empno = "";
		String empname = "";
		//发送到部门
		if (depts != null)
		{
			String[] deptStrs = depts.split("[,]", -1);
			for (String deptNo : deptStrs)
			{
				if (DataType.IsNullOrEmpty(deptNo) == true)
				{
					continue;
				}

				sql = "SELECT " + bp.sys.base.Glo.getUserNo() + ",Name,FK_Dept FROM Port_Emp WHERE FK_Dept='" + deptNo + "'";
				DataTable dt = DBAccess.RunSQLReturnTable(sql);
				for (DataRow dr : dt.Rows)
				{
					empno = dr.getValue(0).toString();
					empname = dr.getValue(1).toString();
					if (sendEmps.contains("," + empno + ",") == true)
					{
						continue;
					}
					emp = new Emp();
					sendEmps += empno + ",";
					sa.setEmpNo(empno);
					sa.setEmpName(empname);
					sa.setDeptName(emp.getDeptText());
					sa.setNodeID(toNodeID);
					sa.setWorkID(workID);
					sa.ResetPK();

					try {
						sa.Save();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}


				}

			}
		}
		if (stas != null)
		{
			String empNo = "No";
			if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			{
				empNo = "UserID as No";
			}

			stas = stas.replace(";", ",");
			String[] mystas = stas.split("[,]", -1);
			for (String staNo : mystas)
			{
				if (DataType.IsNullOrEmpty(staNo) == true)
				{
					continue;
				}
				sql = "SELECT A." + empNo + ", A.Name, a.FK_Dept FROM Port_Emp A, Port_DeptEmpStation B  WHERE a.No=B.FK_Emp AND B.FK_Station='" + staNo + "'";

				DataTable dt = DBAccess.RunSQLReturnTable(sql);
				for (DataRow dr : dt.Rows)
				{
					empno = dr.getValue(0).toString();
					empname = dr.getValue(1).toString();
					if (sendEmps.contains("," + empno + ",") == true)
					{
						continue;
					}
					emp = new Emp();
					sendEmps += empno + ",";
					sa.setEmpNo(empno);
					sa.setEmpName(empname);
					sa.setDeptName(emp.getDeptText());
					sa.setNodeID(toNodeID);
					sa.setWorkID(workID);
					sa.ResetPK();

					try {
						sa.Save();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}

			}
		}

		if (DataType.IsNullOrEmpty(sendEmps) == true)
		{
			return "err@请选择下一个节点的接受人";
		}
		else
		{
			return sendEmps;
		}


	}

	/**
	 增加下一步骤的接受人(用于当前步骤向下一步骤发送时增加接受人)
	 @param workID 工作ID
	 @param toNodeID 到达的节点ID
	 @param emps 如果多个就用逗号分开
	*/

	public static void Node_AddNextStepAccepters(long workID, int toNodeID, String emps) throws Exception {
		Node_AddNextStepAccepters(workID, toNodeID, emps, true);
	}

	public static void Node_AddNextStepAccepters(long workID, int toNodeID, String emps, boolean del_Selected) throws Exception {
		if (DataType.IsNullOrEmpty(emps) == true)
		{
			return;
		}

		SelectAccper sa = new SelectAccper();

		String selectEmps = ",";
		//删除历史选择
		if (del_Selected == true)
		{
			sa.Delete(SelectAccperAttr.FK_Node, toNodeID, SelectAccperAttr.WorkID, workID);
		}

		//检查是否是单选？
		Selector st = new Selector(toNodeID);
		if (st.getItIsSimpleSelector() == true)
		{
			sa.Delete(SelectAccperAttr.FK_Node, toNodeID, SelectAccperAttr.WorkID, workID);
		}
		else
		{
			SelectAccpers sas = new SelectAccpers();
			sas.Retrieve(SelectAccperAttr.WorkID, workID, SelectAccperAttr.FK_Node, toNodeID, null);

			for (SelectAccper item : sas.ToJavaList())
			{
				selectEmps += item.getEmpNo() + ",";
			}
		}


		String[] empStrs = emps.split("[,]", -1);
		if (empStrs.length == 0)
		{
			return;
		}

			///#region 筛选需要执行的任务
		DataTable dt = DBAccess.RunSQLReturnTable("Select e.FK_Dept, e.No as UserID, e.Name, d.Name as FK_DeptText from Port_Emp e left join port_dept d on e.FK_Dept = d.No where e.No IN ('" + String.join("','", empStrs) + "')");
		ArrayList<DataRow> empRowList = new ArrayList<DataRow>();
		for (DataRow dr : dt.Rows)
		{
			String empNo = dr.getValue(1).toString();

			if (selectEmps.indexOf("," + empNo + ",") >= 0)
			{
				continue;
			}
			empRowList.add(dr);
		}
		int taskCount = empRowList.size();
		if (taskCount == 0)
		{
			return; // 没有任务直接返回
		}
		///#endregion
		///#region 多线程并行写入
		//启用线程的时候设置持有上下文的Request容器
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		RequestContextHolder.setRequestAttributes(servletRequestAttributes,true);
		final int POOL_SIZE = empRowList.size();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(
				POOL_SIZE,
				POOL_SIZE,
				POOL_SIZE, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(POOL_SIZE),
				new ThreadPoolExecutor.CallerRunsPolicy());
		List<CompletableFuture<Void>> futures = new ArrayList<CompletableFuture<Void>>();
		for (DataRow dr : empRowList)
		{
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					String empNo = dr.getValue(1).toString();
					String MyPK = toNodeID + "_" + workID + "_" + empNo;
					String sql = "INSERT INTO wf_selectaccper (MyPK, FK_Emp, EmpName, DeptName, FK_Node, WorkID, FK_Dept,AccType) VALUES ";
					sql += "( '" + MyPK + "','" + empNo + "','" + dr.getValue(2) + "','" + dr.getValue(3) + "'," + toNodeID + "," + workID + ",'" + dr.getValue("FK_Dept") + "',0 )";
					DBAccess.RunSQL(sql);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}, executor);
			futures.add(future);

		}
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
		executor.shutdown();
		///#endregion
	}
	/**
	 增加下一步骤的接受人(用于当前步骤向下一步骤发送时增加接受人)

	 @param workID 工作ID
	 @param formNodeID 从节点ID
	 @param emp 接收人
	 @param tag 分组维度，可以为空.是为了分流节点向下发送时候，可能有一个工作人员两个或者两个以上的子线程的情况出现。
	 tag 是个维度，这个维度可能是一个类别，一个批次，一个标记，总之它是一个字符串。详细: http://bbs.ccbpm.cn/showtopic-3065.aspx
	*/
	public static void Node_AddNextStepAccepter(long workID, int formNodeID, String emp, String tag) throws Exception {
		SelectAccper sa = new SelectAccper();
		sa.Delete(SelectAccperAttr.FK_Node, formNodeID, SelectAccperAttr.WorkID, workID, SelectAccperAttr.FK_Emp, emp, SelectAccperAttr.Tag, tag);

		Emp empEn = new Emp(emp);
		sa.setMyPK(formNodeID + "_" + workID + "_" + emp + "_" + tag);
		sa.setTag(tag);
		sa.setEmpNo(emp);
		sa.setEmpName(empEn.getName());
		sa.setNodeID(formNodeID);

		sa.setWorkID(workID);
		sa.Insert();
	}
	/**
	 撤销挂起

	 @param workid 要撤销的原因
	 @return 执行结果
	*/
	public static String Node_HungupWork_Un(long workid) throws Exception {
		bp.wf.WorkFlow wf = new WorkFlow(workid);
		return wf.DoHungupWork_Un();
	}
	/**
	 节点工作挂起

	 @param workid 工作ID
	 @param wayInt 挂起方式0，永久挂起, 1.挂起到指定的日期.
	 @param reldata 解除挂起日期(可以为空)
	 @param hungNote 挂起原因
	 @return 返回执行信息
	*/
	public static String Node_HungupWork(long workid, int wayInt, String reldata, String hungNote) throws Exception {
		HungupWay way = HungupWay.forValue(wayInt);
		bp.wf.WorkFlow wf = new WorkFlow(workid);
		return wf.DoHungup(way, reldata, hungNote);
	}

	/**
	 同意挂起

	 @param workid 工作ID
	*/
	public static String Node_HungupWorkAgree(long workid) throws Exception {
		bp.wf.WorkFlow wf = new WorkFlow(workid);
		return wf.HungupWorkAgree();
	}
	/**
	 拒绝挂起

	 @param workid
	 @param msg
	*/
	public static String Node_HungupWorkReject(long workid, String msg) throws Exception {
		bp.wf.WorkFlow wf = new WorkFlow(workid);
		return wf.HungupWorkReject(msg);
	}
	/**
	 获取该节点上的挂起时间

	 @param flowNo 流程编号
	 @param nodeID 节点ID
	 @param workid 工作ID
	 @return 返回时间串，如果没有挂起的动作就抛出异常.
	*/
	public static long Node_GetHungupTimeSpan(String flowNo, int nodeID, long workid)
	{
		String dbstr = SystemConfig.getAppCenterDBVarStr();

		String instr = ActionType.Hungup.getValue() + "," + ActionType.UnHungup.getValue();
		Paras ps = new Paras();
		ps.SQL = "SELECT * FROM ND" + Integer.parseInt(flowNo) + "Track WHERE WorkID=" + dbstr + "WorkID AND " + TrackAttr.ActionType + " in (" + instr + ")  and  NDFrom=" + dbstr + "NDFrom ";
		ps.Add(TrackAttr.WorkID, workid);
		ps.Add(TrackAttr.NDFrom, nodeID);
		DataTable dt = DBAccess.RunSQLReturnTable(ps);

		Date dtStart = new Date();
		Date dtEnd = new Date();
		for (DataRow item : dt.Rows)
		{
			ActionType at = (ActionType)item.get(TrackAttr.ActionType);

			//挂起时间.
			if (at == ActionType.Hungup)
			{
				dtStart = DataType.ParseSysDateTime2DateTime(item.get(TrackAttr.RDT).toString());
			}

			//解除挂起时间.
			if (at == ActionType.UnHungup)
			{
				dtEnd = DataType.ParseSysDateTime2DateTime(item.get(TrackAttr.RDT).toString());
			}
		}

		long ts = dtEnd.getTime() - dtStart.getTime();
		return ts;
	}
	/**
	 执行加签
	 @param workid 工作ID
	 @param askForEmp 请求人员
	 @param askForNote 内容
	 @return
	*/
	public static String Node_Askfor(long workid, AskforHelpSta askforSta, String askForEmp, String askForNote) throws Exception {
		//检查人员是否存在.
		Emp emp = new Emp();
		emp.setUserID(askForEmp);
		if (emp.RetrieveFromDBSources() == 0)
		{
			throw new RuntimeException("@要加签的人员编号错误:" + askForEmp);
		}

		//获得当前流程注册信息.
		bp.wf.GenerWorkFlow gwf = new GenerWorkFlow(workid);

		//检查当前人员是否开可以执行当前的工作?
		if (Flow_IsCanDoCurrentWork(gwf.getWorkID(), WebUser.getNo()) == false)
		{
			throw new RuntimeException("@当前的工作已经被别人处理或者您没有处理该工作的权限.");
		}

		//检查被加签的人是否在当前的队列中.
		GenerWorkerLists gwls = new GenerWorkerLists(workid, gwf.getNodeID());
		if (gwls.contains(GenerWorkerListAttr.FK_Emp, askForEmp, GenerWorkerListAttr.IsEnable, 0) == true)
		{
			throw new RuntimeException("@加签失败，您选择的加签人可以处理当前的工作。");
		}

		gwf.setWFState(WFState.Askfor); // 更新流程为加签状态.
		try {
			gwf.Update();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// 设置当前状态为 2 表示加签状态.
		if (gwls.size() == 0)
		{
			/*可能是第一个节点.*/
			GenerWorkerList gwl = new GenerWorkerList();
			gwl.Copy(gwf);
			gwl.setWorkID(workid);
			gwl.setEmpNo(askForEmp);
			gwl.setNodeID(gwf.getNodeID());
			gwl.setNodeName(gwl.getNodeName());
			gwl.setEmpNo(WebUser.getNo());
			gwl.setEmpName(WebUser.getName());
			gwl.setDeptNo(WebUser.getDeptNo());
			gwl.setDeptName(WebUser.getDeptName());

			gwl.setPassInt(askforSta.getValue());
			try {
				gwl.Insert();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			//重新查询.
			gwls = new GenerWorkerLists(workid, gwf.getNodeID());

			//设置流程标题.
			if (gwf.getTitle().length() == 0)
			{
				Flow_SetFlowTitle(gwf.getFlowNo(), workid, "来自" + WebUser.getName() + "的工作加签.");
			}
		}
		// endWarning.


		// 处理状态.
		for (GenerWorkerList item : gwls.ToJavaList())
		{
			if (item.getItIsEnable() == false)
			{
				continue;
			}

			if (item.getEmpNo().equals(WebUser.getNo()))
			{
				// GenerWorkerList gwl = gwls[0] as GenerWorkerList;
				item.setPassInt(askforSta.getValue());
				try {
					item.Update();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				// 更换主键后，执行insert ,让被加签人有代办工作.
				item.setPassInt(0);
				item.setEmpNo(emp.getUserID());
				item.setEmpName(emp.getName());
				try
				{
					item.Insert();
				}
				catch (java.lang.Exception e)
				{
					item.Update();
				}
			}
			else
			{
				try {
					item.Update();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		//写入日志.
		bp.wf.Dev2Interface.WriteTrack(gwf.getFlowNo(), gwf.getNodeID(), gwf.getNodeName(), workid, gwf.getFID(), askForNote, ActionType.AskforHelp, "", null, null, emp.getUserID(), emp.getName());

		Flow fl = new Flow(gwf.getFlowNo());
		bp.wf.Dev2Interface.Port_SendMsg(askForEmp, gwf.getTitle(), askForNote, "AK" + gwf.getNodeID() + "_" + gwf.getWorkID(), SMSMsgType.AskFor, gwf.getFlowNo(), gwf.getNodeID(), workid, gwf.getFID());
		//更新状态.
		DBAccess.RunSQL("UPDATE " + fl.getPTable() + " SET WFState=" + WFState.Askfor.getValue() + " WHERE OID=" + workid);

		//设置成工作未读。
		bp.wf.Dev2Interface.Node_SetWorkUnRead(workid, askForEmp);

		String msg = "您的工作已经提交给(" + askForEmp + " " + emp.getName() + ")加签了。";

		//加签后事件
		Node hisNode = null;
		try {
			hisNode = new Node(gwf.getNodeID());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Work currWK = hisNode.getHisWork();
		currWK.setOID(gwf.getWorkID());
		currWK.Retrieve();

		WorkNode wn = new WorkNode(currWK, hisNode);

		//执行加签后的事件.
		msg += ExecEvent.DoNode(EventListNode.AskerAfter, wn, null);

		return msg;
	}
	/**
	 答复加签信息

	 @param workid 工作ID
	 @param replyNote 答复信息
	 @return
	*/
	public static String Node_AskforReply(long workid, String replyNote) throws Exception {
		//把回复信息临时的写入 流程注册信息表以便让发送方法获取这个信息写入日志.
		GenerWorkFlow gwf = new GenerWorkFlow(workid);
		gwf.setParasAskForReply(replyNote);
		gwf.Update();

		Node nd = new Node(gwf.getNodeID());
		String info = "";
		try
		{
			//执行发送, 在发送的方法里面已经做了判断了,并且把 回复的信息写入了日志.
			info = bp.wf.Dev2Interface.Node_SendWork(gwf.getFlowNo(), workid).ToMsgOfHtml();
		}
		catch (RuntimeException ex)
		{
			if (ex.getMessage().contains("请选择下一步骤工作") == true || ex.getMessage().contains("用户没有选择发送到的节点") == true)
			{
				if (nd.getCondModel() == DirCondModel.ByDDLSelected)
				{
					/*如果抛出异常，我们就让其转入选择到达的节点里, 在节点里处理选择人员. */
					return "SelectNodeUrl@./WorkOpt/ToNodes.htm?FK_Flow=" + gwf.getFlowNo() + "&FK_Node=" + gwf.getNodeID() + "&WorkID=" + gwf.getWorkID() + "&FID=" + gwf.getFID();

				}
				return "err@下一个节点的接收人规则是，当前节点选择来选择，在当前节点属性里您没有启动接受人按钮，系统自动帮助您启动了，请关闭窗口重新打开。" + ex.getMessage();
			}
			return ex.getMessage();
		}

		Node node = null;
		try {
			node = new Node(gwf.getNodeID());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Work wk = node.getHisWork();
		wk.setOID(workid);
		wk.RetrieveFromDBSources();

		//恢复加签后执行事件.
		WorkNode wn = new WorkNode(wk, node);
		info += ExecEvent.DoNode(EventListNode.AskerReAfter, wn, null);
		return info;
	}
	/**
	 执行工作分配

	 @param flowNo 流程编号
	 @param nodeID 节点ID
	 @param workID 工作ID
	 @param fid FID
	 @param toEmps 要分配的人，多个人用逗号分开.
	 @param msg 分配原因.
	 @return 分配信息.
	*/
	public static String Node_Allot(String flowNo, int nodeID, long workID, long fid, String toEmps, String msg) throws Exception {
		//生成实例.
		GenerWorkerLists gwls = null;
		try {
			gwls = new GenerWorkerLists(workID, nodeID);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		//要分配给的人员.
		String[] empStrs = toEmps.split("[,]", -1);
		for (String empNo : empStrs)
		{
			if (DataType.IsNullOrEmpty(empNo) == true)
			{
				continue;
			}

			//人员实体.
			Emp empEmp = new Emp(empNo);

			GenerWorkerList gwl = null; //接收人

			//开始找接收人.
			for (GenerWorkerList item : gwls.ToJavaList())
			{
				if (item.getEmpNo().equals(empNo))
				{
					gwl = item;
					break;
				}
			}

			// 没有找到的情况, 就获得一个实例，作为数据样本然后把数据insert.
			if (gwl == null)
			{
				gwl = gwls.get(0) instanceof GenerWorkerList ? (GenerWorkerList)gwls.get(0) : null;
				gwl.setEmpNo(empEmp.getUserID());
				gwl.setEmpName(empEmp.getName());
				gwl.setItIsEnable(true);
				gwl.setPassInt(0);
				try {
					gwl.Insert();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				continue;
			}

			//如果被禁用了，就启用他.
			if (gwl.getItIsEnable() == false)
			{
				gwl.setItIsEnable(true);
				gwl.Update();
			}
		}
		return "分配成功.";
	}
	/**
	 工作移交

	 @param workID
	 @param toEmps 要移交给的人员,多个人用逗号分开.比如:zhangsan,lisi,wangwu
	 @param msg 移交原因.
	 @return 执行的信息.
	*/
	public static String Node_Shift(long workID, String toEmps, String msg) throws Exception {
		return  Node_Shift(WebUser.getNo(), workID, toEmps, msg);
	}

	/**
	 * 工作移交
	 * @param fromUser 当事人
	 * @param workID
	 * @param toEmps 要移交给的人员,多个人用逗号分开.比如:zhangsan,lisi,wangwu
	 * @param msg 移交原因.
	 * @return 执行的信息.
	 * @throws Exception
	 */
	public static String Node_Shift(String fromUser, long workID, String toEmps, String msg) throws Exception {
		if (DataType.IsNullOrEmpty(toEmps) == true)
		{
			return "err@要移交的人员不能为空.";
		}

		/**处理参数格式.
		*/
		toEmps = toEmps.replace(";", ",");
		toEmps = toEmps.replace("，", ",");

		//如果仅仅移交一个人.
		if (toEmps.indexOf(",") == -1)
		{
			return bp.wf.ShiftWork.Node_Shift_ToEmp(fromUser, workID, toEmps, msg);
		}

		//移交给多个人.
		return bp.wf.ShiftWork.Node_Shift_ToEmps(fromUser, workID, toEmps, msg);
	}
	/**
	 撤销移交

	 @param workID 工作ID
	 @return 返回撤销信息
	*/
	public static String Node_ShiftUn(long workID) throws Exception {
		return bp.wf.ShiftWork.DoUnShift(workID);
	}

	/**
	 执行工作退回(退回指定的点)

	 @param workID 退回的工作ID
	 @param returnToNodeID 退回到节点,如果是0,就退回上一个节点.
	 @param returnToEmp 退回到人员,如果是null，就退回给指定节点的处理人。标记:退回当前节点的所有人：AllEmps
	 @param msg 退回信息
	 @param isBackToThisNode 退回后是否返回当前节点？
	 @param pageData 页面数据
	 @return
	*/

	public static String Node_ReturnWork(long workID, int returnToNodeID, String returnToEmp, String msg, boolean isBackToThisNode, String pageData) throws Exception {
		return Node_ReturnWork(workID, returnToNodeID, returnToEmp, msg, isBackToThisNode, pageData, true);
	}

	public static String Node_ReturnWork(long workID, int returnToNodeID, String returnToEmp, String msg, boolean isBackToThisNode) throws Exception {
		return Node_ReturnWork(workID, returnToNodeID, returnToEmp, msg, isBackToThisNode, null, true);
	}

	public static String Node_ReturnWork(long workID, int returnToNodeID, String returnToEmp, String msg) throws Exception {
		return Node_ReturnWork(workID, returnToNodeID, returnToEmp, msg, false, null, true);
	}

	public static String Node_ReturnWork(long workID, int returnToNodeID, String returnToEmp, String msg, boolean isBackToThisNode, String pageData, boolean isKillEtcThread) throws Exception {

		if (DataType.IsNullOrEmpty(msg) == true)
		{
			throw new RuntimeException("退回信息不能为空.");
		}

		//补偿处理退回错误.
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		String sql = "";
		DataTable dt;

		if (DataType.IsNullOrEmpty(returnToEmp) == false)
		{
			if (returnToEmp.equals("AllEmps") == true)
				sql = "SELECT WorkID,FK_Emp,EmpName,FK_Dept FROM WF_GenerWorkerlist WHERE WorkID=" + workID + " AND FK_Node=" + returnToNodeID;
			else
				sql = "SELECT WorkID,FK_Emp,EmpName,FK_Dept FROM WF_GenerWorkerlist WHERE WorkID=" + workID + " AND FK_Emp='" + returnToEmp + "' AND FK_Node=" + returnToNodeID;
		}
		else
		{
			sql = "SELECT WorkID,FK_Emp FROM WF_GenerWorkerlist WHERE WorkID=" + workID + " AND FK_Node=" + returnToNodeID;
		}
		dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			// 有可能是父流程.  @lizhen
			Node pNode = new Node(returnToNodeID);

			if (pNode.getFlowNo().equals(gwf.getPFlowNo()) == false)
			{
				throw new RuntimeException("err@被退回到的节点数据错误，请联系管理员.");
			}

			if (gwf.getPWorkID() == 0)
			{
				throw new RuntimeException("err@被退回到的节点数据错误，请联系管理员.");
			}

			String empNo = dt.Rows.get(0).toString();
			String empName = dt.Rows.get(2).toString();
			String deptNo = dt.Rows.get(3).toString();

			//需要处理父流程的数据.
			GenerWorkFlow gwfP = new GenerWorkFlow(gwf.getPWorkID());
			gwfP.setWFState(WFState.ReturnSta); //设置退回状态.
			gwfP.setNodeID(pNode.getNodeID());
			gwfP.setNodeName(pNode.getName());
			gwfP.setTodoEmps( empNo + "," + empName + ";");
			gwfP.setTodoEmpsNum(0);
			gwfP.setSendDT(DataType.getCurrentDateTime());
			gwfP.setSender(WebUser.getNo() + "," + WebUser.getName() + ";");

			gwfP.Update();

			//设置所有的人员信息为已经完成.
			sql = "UPDATE  WF_GenerWorkerlist SET IsPass=1 WHERE WorkID=" + gwf.getPWorkID();
			DBAccess.RunSQL(sql);

			sql = "UPDATE  WF_GenerWorkerlist SET IsPass=0,IsRead=0,RDT='" + DataType.getCurrentDateTimess() + "' WHERE  FK_Emp='" + empNo + "' AND  WorkID=" + gwf.getPWorkID() + " AND FK_Node=" + gwf.getPNodeID();
			int i = DBAccess.RunSQL(sql);
			if (i == 0)
			{
				//找到父节点的gwfs
				GenerWorkerList gwl = new GenerWorkerList();
				gwl.setNodeID(pNode.getNodeID());
				gwl.setWorkID(gwf.getPWorkID());
				gwl.setEmpNo(returnToEmp);
				gwl.setPassInt(0);
				gwl.setDeptNo(deptNo);
				gwl.setEmpName(empName);
				gwl.setRDT(DataType.getCurrentDateTimess());
				gwl.setSDT(DataType.getCurrentDateTime());
				gwl.setFlowNo(pNode.getFlowNo());
				gwl.setItIsRead(false);
				gwl.setSender(WebUser.getNo());
				gwl.Save();
			}


			//把当前的流程删除掉.
			bp.wf.Dev2Interface.Flow_DoDeleteFlowByReal(workID, false);

			return "已经退回给父流程“" + gwfP.getFlowName() + "”节点“" + gwfP.getNodeName() + "”，退回给“" + empName + "”";
		}
		if (DataType.IsNullOrEmpty(returnToEmp) == true && dt.Rows.size() > 0)
		{
			returnToEmp = dt.Rows.get(0).getValue(1).toString();
		}

		String info = "";
		WorkReturn wr = new WorkReturn(gwf.getFlowNo(), workID, gwf.getFID(), gwf.getNodeID(), returnToNodeID, returnToEmp, isBackToThisNode, msg, pageData);

		Node returnToNode = new Node(wr.ReturnToNodeID);
		if (isKillEtcThread == true && gwf.getFID() != 0 && (returnToNode.getHisRunModel() == RunModel.FL || returnToNode.getHisRunModel() == RunModel.FHL))
		{
			//子线程退回到分流节点. 要删除其他的子线程.
			info = wr.DoItOfKillEtcThread();
		}
		else
		{
			info = wr.DoIt();
		}

		//检查退回的数据是否正确？
		/*sql = "SELECT WorkID FROM WF_GenerWorkerlist WHERE WorkID=" + workID + " AND FK_Emp='" + wr.ReturnToEmp + "' AND IsPass=0";
		dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0 && gwf.getFID() == 0)
		{
			*//*说明数据错误了,回滚回来.*//*
			bp.wf.Dev2Interface.Flow_ReSend(gwf.getWorkID(), gwf.getNodeID(), WebUser.getNo(), "退回错误的回滚.");
			throw new RuntimeException("err@退回出现系统错误，请联系管理员或者在执行一次退回.WorkID=" + workID);
		}*/
		return info;
	}
	/**
	 退回

	 @param workID 工作ID
	 @param returnToNodeID 要退回的节点,0 表示上一个节点或者指定的节点.
	 @param msg 退回信息
	 @param isBackToThisNode 是否原路返回
	 @return 执行结果
	*/
	public static String Node_ReturnWork(long workID, int returnToNodeID, String msg, boolean isBackToThisNode) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		return Node_ReturnWork(workID, returnToNodeID, null, msg, isBackToThisNode);
	}
	/**
	 退回

	 @param fk_flow 流程编号
	 @param workID 工作ID
	 @param fid 流程ID
	 @param currentNodeID 当前节点
	 @param returnToNodeID 退回到节点
	 @param msg 退回消息
	 @param isBackToThisNode 是否原路返回
	 @return 退回执行的信息，执行不成功就抛出异常。
	*/
	public static String Node_ReturnWork(String fk_flow, long workID, long fid, int currentNodeID, int returnToNodeID, String msg, boolean isBackToThisNode) throws Exception {
		return Node_ReturnWork(workID, returnToNodeID, null, msg, isBackToThisNode);
	}
	/**
	 获取当前工作的NodeID
	 @param workid 工作ID
	 @return 指定工作的NodeID.
	*/
	public static int Node_GetCurrentNodeID(String flowNo, long workid)
	{
		int nodeID = DBAccess.RunSQLReturnValInt("SELECT FK_Node FROM WF_GenerWorkFlow WHERE WorkID=" + workid + " AND FK_Flow='" + flowNo + "'", 0);
		if (nodeID == 0)
		{
			return Integer.parseInt(flowNo + "01");
		}
		return nodeID;
	}
	/**
	 增加子线程

	 @param workID 干流程的workid
	 @param empStrs 要增加的子线程的工作人员，多个人员用逗号分开.
	*/
	public static String Node_FHL_AddSubThread(long workID, String empStrs, int toNodeID) throws Exception {

			///#region 检查参数是否正确.
		empStrs = empStrs.replace("，", ",");
		empStrs = empStrs.replace("；", ",");
		empStrs = empStrs.replace(";", ",");
		String[] strs = empStrs.split("[,]", -1);
		String err = "";
		for (String str : strs)
		{
			if (DataType.IsNullOrEmpty(str) == true)
			{
				continue;
			}
			Emp emp = new Emp();
			emp.setNo(str);
			if (emp.getIsExits());
			{
				err += "人员账号：" + str + "不正确。";
			}
		}

		if (DataType.IsNullOrEmpty(err) == false)
		{
			return "err@错误：" + err;
		}

			///#endregion 检查参数是否正确.


			///#region 求分流节点 Node . 求分流节点下一个子线程节点


		GenerWorkFlow gwf = new GenerWorkFlow(workID);

		Node nd = new Node(gwf.getNodeID());
		if (nd.getHisRunModel() == RunModel.FL)
		{
			/* 如果节点是分流，则要在分流上,就不需要处理，因为是在分流上增加. */
		}

		//如果运行到合流节点，需要找到分流节点的，节点ID.
		if (nd.getHisRunModel() == RunModel.HL)
		{
			GenerWorkerLists gwls = new GenerWorkerLists();
			gwls.Retrieve(GenerWorkerListAttr.WorkID, gwf.getWorkID(), null);

			for (GenerWorkerList item : gwls.ToJavaList())
			{
				Node myNode = null;
				try {
					myNode = new Node(item.getNodeID());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				if (myNode.getHisRunModel() == RunModel.FL)
				{
					nd = myNode;
					break;
				}
			}
		}

		//生成子线程的gwf.
		GenerWorkFlow gwfSub = new GenerWorkFlow();
		gwfSub.Copy(gwf);
		gwfSub.setNodeID(nd.getNodeID());
		gwfSub.setNodeName(nd.getName());

		//找到分流节点的下一个子线程节点.
		Node ndSubThread = null;
		if (toNodeID != 0)
		{
			ndSubThread = new Node(toNodeID);
		}
		else
		{
			Nodes nds = nd.getHisToNodes();
			for (Node item : nds.ToJavaList())
			{
				if (item.getItIsSubThread() == true)
				{
					ndSubThread = item;
				}
			}
			if (ndSubThread == null)
			{
				return "err@没有找到分流节点下的子线程.";
			}
		}



			///#endregion 求分流节点 Node, 求分流节点下一个子线程节点.


			///#region 开始追加子线程的处理人.

		// 求出来一个分流节点 gwl;
		GenerWorkerList gwl = new GenerWorkerList();
		try {
			gwl.Retrieve(GenerWorkerListAttr.WorkID, gwf.getWorkID(), GenerWorkerListAttr.FK_Node, nd.getNodeID());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		gwl.setRDT(DataType.getCurrentDateTimess());
		gwl.setSDT(DataType.getCurrentDateTime());
		gwl.setItIsPass(false);
		gwl.setFID(gwf.getWorkID()); // 设置FID.
		gwl.setItIsRead(false);

		Work wk = nd.getHisWork();
		wk.setOID(gwf.getWorkID());
		wk.Retrieve();

		String msg = "";
		for (String empNo : strs)
		{
			if (DataType.IsNullOrEmpty(empNo) == true)
			{
				continue;
			}

			Emp emp = new Emp(empNo);

			//生成子线程的gwf.
			gwfSub.setFID(gwf.getWorkID());
			gwfSub.setWorkID(DBAccess.GenerOID("WorkID"));
			gwfSub.setSendDT(DataType.getCurrentDateTime());
			gwfSub.setRDT(DataType.getCurrentDateTime());
			gwfSub.setSDTOfFlow(DataType.getCurrentDateTime());
			gwfSub.setSDTOfNode(DataType.getCurrentDateTime());

			gwfSub.setNodeID(ndSubThread.getNodeID());
			gwfSub.setNodeName(ndSubThread.getName());
			gwfSub.setWFState(WFState.Runing);
			gwfSub.setFID(gwf.getWorkID());
			gwfSub.setTodoEmps(emp.getNo() + "," + emp.getName() +";");
			try {
				gwfSub.DirectInsert();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			gwl.setWorkID(gwfSub.getWorkID());

			//生成干流程上的. gwl.
			gwl.setNodeID(nd.getNodeID());
			gwl.setNodeName(nd.getName());
			gwl.setDeptNo(WebUser.getDeptNo());
			gwl.setDeptName(WebUser.getDeptName());
			gwl.setEmpNo(WebUser.getNo());
			gwl.setEmpName(WebUser.getName());
			gwl.setPassInt(-2);
			gwl.setRDT(DataType.getCurrentDateTimess());
			gwl.setSDT(DataType.getCurrentDateTime());
			gwl.setCDT(DataType.getCurrentDateTimess());
			try {
				gwl.Insert();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}


			//生成子线程的. gwl.
			gwl.setNodeID(ndSubThread.getNodeID());
			gwl.setNodeName(ndSubThread.getName());
			gwl.setEmpNo(emp.getNo());
			gwl.setEmpName(emp.getName());
			gwl.setDeptNo(emp.getDeptNo());
			gwl.setDeptName(emp.getDeptText());
			gwl.setPassInt(0);
			gwl.setRDT(DataType.getCurrentDateTimess());
			gwl.setSDT(DataType.getCurrentDateTime());
			gwl.setCDT(DataType.getCurrentDateTimess());
			gwl.Insert();

			//复制工作信息.
			Work wkSub = ndSubThread.getHisWork();
			wkSub.Copy(wk);
			wkSub.setOID(gwl.getWorkID());
			wkSub.setFID(gwl.getFID());
			wkSub.SaveAsOID(gwl.getWorkID());

			msg += "增加成功:" + emp.getNo() + "," + emp.getName();
		}

			///#endregion 开始追加子线程的处理人.

		return "执行信息如下：" + msg;
	}

	/**
	 删除子线程

	 @param workid 工作ID
	*/
	public static void Node_FHL_KillSubFlow(long workid) throws Exception {
		WorkFlow wkf = new WorkFlow(workid);
		wkf.DoDeleteWorkFlowByFlag("删除子线程.");
	}
	/**
	 合流点驳回子线程

	 @param NodeSheetfReject 流程编号
	 @param fid 流程ID
	 @param workid 子线程ID
	 @param msg 驳回消息
	*/
	public static String Node_FHL_DoReject(int NodeSheetfReject, long fid, long workid, String msg) throws Exception {
		WorkFlow wkf = new WorkFlow(workid);
		return wkf.DoReject(fid, NodeSheetfReject, msg);
	}

	/**
	 跳转审核取回

	 @param fromNodeID 从节点ID
	 @param workid 工作ID
	 @param tackToNodeID 取回到的节点ID
	 @return
	*/

	public static String Node_Tackback(int fromNodeID, long workid, int tackToNodeID) throws Exception {
		return Node_Tackback(fromNodeID, workid, tackToNodeID, null);
	}

	public static String Node_Tackback(int fromNodeID, long workid, int tackToNodeID, String doMsg) throws Exception {
		if (doMsg == null)
		{
			doMsg = " 执行跳转审核的取回";
		}

		/*
		 * 1,首先检查是否有此权限.
		 * 2, 执行工作跳转.
		 * 3, 执行写入日志.
		 */
		Node nd = new Node(tackToNodeID);
		switch (nd.getHisDeliveryWay())
		{
			case ByPreviousNodeFormEmpsField:
				break;
		}

		WorkNode wn = new WorkNode(workid, fromNodeID);
		String msg = wn.NodeSend(new Node(tackToNodeID), WebUser.getNo()).ToMsgOfHtml();
		wn.AddToTrack(ActionType.Tackback, WebUser.getNo(), WebUser.getName(), tackToNodeID, nd.getName(), doMsg);
		return msg;
	}
	/**
	 执行抄送已阅

	 @param fk_flow 流程编号
	 @param fk_node 流程节点
	 @param workid 工作id
	 @param fid 流程id
	 @param checkNote 填写意见
	*/
	public static void Node_DoCCCheckNote(String fk_flow, int fk_node, long workid, long fid, String checkNote) throws Exception {
		NodeWorkCheck fwc = new NodeWorkCheck(fk_node);

		bp.wf.Dev2Interface.Node_WriteWorkCheck(workid, checkNote, fwc.getFWCOpLabel(), null);

		//设置审核完成.
		bp.wf.Dev2Interface.Node_CC_SetSta(fk_node, workid, WebUser.getNo(), bp.wf.CCSta.CheckOver);
	}
	/**
	 设置是此工作为读取状态

	 @param nodeID 节点编号
	 @param workid 工作ID
	*/
	public static void Node_SetWorkRead(int nodeID, long workid) throws Exception {
		Node_SetWorkRead(nodeID, workid, WebUser.getNo());
	}
	/**
	 设置是此工作为读取状态

	 @param nodeID 节点ID
	 @param workid WorkID
	 @param empNo 操作员
	*/
	public static void Node_SetWorkRead(int nodeID, long workid, String empNo) throws Exception {
		Node nd = new Node(nodeID);

		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkerlist SET IsRead=1 WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + dbstr + "FK_Node AND FK_Emp=" + dbstr + "FK_Emp";
		ps.Add("WorkID", workid);
		ps.Add("FK_Node", nodeID);
		ps.Add("FK_Emp", empNo, false);
		if (DBAccess.RunSQL(ps) == 0)
		{
			//throw new Exception("设置的工作不存在，或者当前的登录人员[" + empNo + "]已经改变，请重新登录。");
		}

		// 判断当前节点的已读回执.
		if (nd.getReadReceipts() == ReadReceipts.None || nd.getItIsStartNode() == true)
		{
			return;
		}

		boolean isSend = false;
		if (nd.getReadReceipts() == ReadReceipts.Auto)
		{
			isSend = true;
		}

		if (nd.getReadReceipts() == ReadReceipts.BySysField)
		{
			/*获取上一个节点ID */
			Nodes fromNodes = nd.getFromNodes();
			int fromNodeID = 0;
			for (Node item : fromNodes.ToJavaList())
			{
				ps = new Paras();
				ps.SQL = "SELECT FK_Node FROM WF_GenerWorkerlist WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + dbstr + "FK_Node ";
				ps.Add("WorkID", workid);
				ps.Add("FK_Node", item.getNodeID());
				DataTable dt = DBAccess.RunSQLReturnTable(ps);
				if (dt.Rows.size() == 0)
				{
					continue;
				}

				fromNodeID = item.getNodeID();
				break;
			}
			if (fromNodeID == 0)
			{
				throw new RuntimeException("@没有找到它的上一步工作。");
			}

			try
			{
				ps = new Paras();
				ps.SQL = "SELECT " + bp.wf.WorkSysFieldAttr.SysIsReadReceipts + " FROM ND" + fromNodeID + "    WHERE OID=" + dbstr + "OID";
				ps.Add("OID", workid);
				DataTable dt1 = DBAccess.RunSQLReturnTable(ps);
				if (Objects.equals(dt1.Rows.get(0).getValue(0).toString(), "1"))
				{
					isSend = true;
				}
			}
			catch (RuntimeException ex)
			{
				throw new RuntimeException("@流程设计错误:" + ex.getMessage() + " 在当前节点上个您设置了安上一步的表单字段决定是否回执，但是上一个节点表单中没有约定的字段。");
			}
		}

		if (nd.getReadReceipts() == ReadReceipts.BySDKPara)
		{
			/*如果是按开发者参数*/

			/*获取上一个节点ID*/
			Nodes fromNodes = nd.getFromNodes();
			int fromNodeID = 0;
			for (Node item : fromNodes.ToJavaList())
			{
				ps = new Paras();
				ps.SQL = "SELECT FK_Node FROM WF_GenerWorkerlist  WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + dbstr + "FK_Node ";
				ps.Add("WorkID", workid);
				ps.Add("FK_Node", item.getNodeID());
				DataTable dt = DBAccess.RunSQLReturnTable(ps);
				if (dt.Rows.size() == 0)
				{
					continue;
				}

				fromNodeID = item.getNodeID();
				break;
			}
			if (fromNodeID == 0)
			{
				throw new RuntimeException("@没有找到它的上一步工作。");
			}

			String paras = null;
			try {
				paras = Dev2Interface.GetFlowParas(fromNodeID, workid);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (DataType.IsNullOrEmpty(paras) || paras.contains("@" + bp.wf.WorkSysFieldAttr.SysIsReadReceipts + "=") == false)
			{
				throw new RuntimeException("@流程设计错误:在当前节点上个您设置了按开发者参数决定是否回执，但是没有找到该参数。");
			}

			// 开发者参数.
			if (paras.contains("@" + bp.wf.WorkSysFieldAttr.SysIsReadReceipts + "=1") == true)
			{
				isSend = true;
			}
		}


		if (isSend == true)
		{
			/*如果是自动的已读回执，就让它发送给当前节点的上一个发送人。*/

			// 获取流程标题.
			ps = new Paras();
			ps.SQL = "SELECT Title FROM WF_GenerWorkFlow WHERE WorkID=" + dbstr + "WorkID ";
			ps.Add("WorkID", workid);
			DataTable dt = DBAccess.RunSQLReturnTable(ps);
			String title = dt.Rows.get(0).getValue(0).toString();

			// 获取流程的发送人.
			ps = new Paras();
			ps.SQL = "SELECT " + GenerWorkerListAttr.Sender + " FROM WF_GenerWorkerlist WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + dbstr + "FK_Node ";
			ps.Add("WorkID", workid);
			ps.Add("FK_Node", nodeID);
			dt = DBAccess.RunSQLReturnTable(ps);
			String sender = dt.Rows.get(0).getValue(0).toString();
			if(DataType.IsNullOrEmpty(sender) == false && sender.contains(",") == true){
				sender = sender.split(",")[0];
			}
			//发送已读回执。
			bp.wf.Dev2Interface.Port_SendMsg(sender, "已读回执:" + title, "您发送的工作已经被" + WebUser.getName() + "在" + DataType.getCurrentDateTimeCNOfShort() + " 打开.", "RP" + workid + "_" + nodeID, bp.wf.SMSMsgType.Self, nd.getFlowNo(), nd.getNodeID(), workid, 0);
		}

		//执行节点打开后事件.
		Work wk = nd.getHisWork();
		wk.setOID(workid);
		wk.RetrieveFromDBSources();


		WorkNode wn = new WorkNode(wk, nd);

		//执行事件.
		try {
			ExecEvent.DoNode(EventListNode.WhenReadWork, wn, null, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		/**nd.getHisFlow().DoFlowEventEntity(EventListNode.WhenReadWork, nd, wk, null);
		*/

	}
	/**
	 设置工作未读取

	 @param workid 工作ID
	 @param userNo 要设置的人
	*/
	public static void Node_SetWorkUnRead(long workid, String userNo)
	{
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkerlist SET IsRead=0 WHERE WorkID=" + dbstr + "WorkID AND FK_Emp=" + dbstr + "FK_Emp";
		ps.Add("WorkID", workid);
		ps.Add("FK_Emp", userNo, false);
		DBAccess.RunSQL(ps);
	}
	/**
	 设置工作未读取

	 @param workid 工作ID
	*/
	public static void Node_SetWorkUnRead(long workid)
	{
		Node_SetWorkUnRead(workid, WebUser.getNo());
	}

		///#endregion 工作有关接口


		///#region 会签相关操作.
	/**
	 获得当前节点会签人的信息
	 @param workID 工作ID
	*/
	public static DataSet Node_HuiQian_Init(long workID) throws Exception {
		//要找到主持人.
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		//判断流程实例的节点和当前节点是否相同
		if (gwf.getTodoEmps().contains(WebUser.getNo() + ",") == false)
		{
			if (bp.wf.Dev2Interface.Flow_IsCanDoCurrentWork(gwf.getWorkID(), WebUser.getNo()) == false)
			{
				throw new RuntimeException("err@当前工作处于{" + gwf.getNodeName() + "}节点,您({" + WebUser.getName() + "})没有处理权限.");
			}
		}

		if (gwf.getHuiQianTaskSta() == HuiQianTaskSta.HuiQianOver)
		{
			throw new RuntimeException("err@会签工作已经完成，您不能在执行会签。");
		}

		//查询出来集合.
		GenerWorkerLists ens = new GenerWorkerLists(workID, gwf.getNodeID());
		BtnLab btnLab = new BtnLab(gwf.getNodeID());
		if (btnLab.getHuiQianRole() != HuiQianRole.TeamupGroupLeader || (btnLab.getHuiQianRole() == HuiQianRole.TeamupGroupLeader && btnLab.getHuiQianLeaderRole() != HuiQianLeaderRole.OnlyOne))
		{
			for (GenerWorkerList item : ens.ToJavaList())
			{

				if ((gwf.getHuiQianZhuChiRen().contains(item.getEmpNo() + ",") == true || (DataType.IsNullOrEmpty(gwf.getHuiQianZhuChiRen()) == true && gwf.GetParaString("AddLeader").contains(item.getEmpNo() + ",") == false && gwf.getTodoEmps().contains(item.getEmpNo() + ",") == true)) && !Objects.equals(item.getEmpNo(), WebUser.getNo()) && item.getItIsHuiQian() == false)
				{
					item.setEmpName("<img src='../Img/zhuichiren.png' border=0 />" + item.getEmpName());
					item.setEmpName(item.getEmpName());
					if (item.getItIsPass() == true)
					{
						item.setPassInt(1001);
					}
					else
					{
						item.setPassInt(100);
					}
					continue;
				}

				//标记为自己.
				if (item.getEmpNo().equals(WebUser.getNo()))
				{
					item.setEmpName("" + item.getEmpName());
					if (item.getItIsPass() == true)
					{
						item.setPassInt(9901);
					}
					else
					{
						item.setPassInt(99);
					}
				}
			}
		}

		//赋值部门名称。
		DataTable mydt = null;
		try {
			mydt = ens.ToDataTableField("WF_GenerWorkList");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		mydt.Columns.Add("FK_DeptT", String.class);
		for (DataRow dr : mydt.Rows)
		{
			String fk_emp = dr.getValue("FK_Emp").toString();
			for (GenerWorkerList item : ens.ToJavaList())
			{
				if (item.getEmpNo().equals(fk_emp))
				{
					dr.setValue("FK_DeptT", item.getDeptName());
				}
			}
		}

		//获取当前人员的流程处理信息
		GenerWorkerList gwlOfMe = new GenerWorkerList();
		gwlOfMe.Retrieve(GenerWorkerListAttr.FK_Emp, WebUser.getNo(), GenerWorkerListAttr.WorkID, workID, GenerWorkerListAttr.FK_Node, gwf.getNodeID());

		DataSet ds = new DataSet();
		ds.Tables.add(mydt);
		try {
			ds.Tables.add(gwlOfMe.ToDataTableField("My_GenerWorkList"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return ds;
	}
	/**
	 增加会签人

	 @param workID 工作ID
	 @param huiQianType huiQianType=AddLeader增加组长,
	 @param empStrs
	 @return
	*/
	public static String Node_HuiQian_AddEmps(long workID, String huiQianType, String empStrs) throws Exception {
		if (DataType.IsNullOrEmpty(empStrs) == true)
		{
			return "err@您没有选择人员.";
		}

		empStrs = empStrs.replace("，", ",");
		empStrs = empStrs.replace(";", ",");
		empStrs = empStrs.replace("；", ",");


		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		String addLeader = gwf.GetParaString("AddLeader");
		if (gwf.getTodoEmps().contains(WebUser.getNo() + ",") == false)
		{
			//判断是不是第二会签主持人
			if (addLeader.contains(WebUser.getNo() + ",") == false)
			{
				return "err@您不是会签主持人，您不能执行该操作。";
			}
		}

		GenerWorkerList gwlOfMe = new GenerWorkerList();
		int num = 0;
		try {
			num = gwlOfMe.Retrieve(GenerWorkerListAttr.FK_Emp, WebUser.getNo(), GenerWorkerListAttr.WorkID, gwf.getWorkID(), GenerWorkerListAttr.FK_Node, gwf.getNodeID());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (num == 0)
		{
			return "err@没有查询到当前人员的工作列表数据.";
		}

		Node nd = new Node(gwf.getNodeID());
		String err = "";
		String[] myEmpStrs = empStrs.split("[,]", -1);
		int addCount = 0;
		for (String empStr : myEmpStrs)
		{
			if (DataType.IsNullOrEmpty(empStr) == true)
			{
				continue;
			}

			Emp emp = new Emp(empStr);

			//查查是否存在队列里？
			try {
				num = gwlOfMe.Retrieve(GenerWorkerListAttr.FK_Emp, emp.getUserID(), GenerWorkerListAttr.WorkID, gwf.getWorkID(), GenerWorkerListAttr.FK_Node, gwf.getNodeID());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			if (num == 1)
			{
				// err += " 人员[" + emp.getUserID() + "," + emp.getName() +"]已经在队列里.";
				continue;
			}
			addCount++;
			//增加组长
			if (DataType.IsNullOrEmpty(huiQianType) == false && huiQianType.equals("AddLeader"))
			{
				addLeader += emp.getUserID() + ",";
			}

			//查询出来其他列的数据.
			try {
				gwlOfMe.Retrieve(GenerWorkerListAttr.FK_Emp, WebUser.getNo(), GenerWorkerListAttr.WorkID, gwf.getWorkID(), GenerWorkerListAttr.FK_Node, gwf.getNodeID());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			gwlOfMe.SetPara("HuiQianType", "");
			gwlOfMe.setEmpNo(emp.getUserID());
			gwlOfMe.setEmpName(emp.getName());
			gwlOfMe.setPassInt(-1); //设置不可以用.
			gwlOfMe.setDeptNo(emp.getDeptNo());
			gwlOfMe.setDeptName(emp.getDeptText()); //部门名称.
			gwlOfMe.setItIsRead(false);
			gwlOfMe.SetPara("HuiQianZhuChiRen", WebUser.getNo());
			//表明后增加的组长
			if (DataType.IsNullOrEmpty(huiQianType) == false && huiQianType.equals("AddLeader"))
			{
				gwlOfMe.SetPara("HuiQianType", huiQianType);
			}


				///#region 计算会签时间.
			if (nd.getHisCHWay() == CHWay.None)
			{
				gwlOfMe.setSDT("无");
			}
			else
			{
				//给会签人设置应该完成日期. 考虑到了节假日.
				Date dtOfShould = Glo.AddDayHoursSpan(new Date(), nd.getTimeLimit(), nd.getTimeLimitHH(), nd.getTimeLimitMM(), nd.getTWay());
				//应完成日期.
				gwlOfMe.setSDT(DateUtils.format(dtOfShould,DataType.getSysDateTimeFormat() + ":ss"));
			}

			//求警告日期.
			Date dtOfWarning = new Date();
			if (nd.getWarningDay() == 0)
			{
				//  dtOfWarning = "无";
			}
			else
			{
				//计算警告日期。
				// 增加小时数. 考虑到了节假日.
				dtOfWarning = Glo.AddDayHoursSpan(new Date(), (int)nd.getWarningDay(), 0, 0, nd.getTWay());
			}
			gwlOfMe.setDTOfWarning(DateUtils.format(dtOfWarning,DataType.getSysDateTimeFormat()));

				///#endregion 计算会签时间.

			gwlOfMe.setSender(WebUser.getNo() + "," + WebUser.getName()); //发送人为当前人.
			gwlOfMe.setItIsHuiQian(true);
			gwlOfMe.Insert(); //插入作为待办.

		}
		gwf.SetPara("AddLeader", addLeader);
		gwf.Update();
		if (err.equals("") == true || (addCount > 0 && err.equals("") == false))
		{
			return "增加成功.";
		}
		return "err@" + err;
	}
	/**
	 增加会签主持人

	 @param workID 流程ID
	 @return
	*/
	public static String Node_HuiQian_AddLeader(long workID) throws Exception {
		//生成变量.
		GenerWorkFlow gwf = new GenerWorkFlow(workID);

		//判断流程实例的节点和当前节点是否相同
		if (gwf.getTodoEmps().contains(WebUser.getNo() + ",") == false)
		{
			if (bp.wf.Dev2Interface.Flow_IsCanDoCurrentWork(gwf.getWorkID(), WebUser.getNo()) == false)
			{
				return "err@当前工作处于{" + gwf.getNodeName() + "}节点,您({" + WebUser.getName() + "})没有处理权限.";
			}
		}

		if (gwf.getHuiQianTaskSta() == HuiQianTaskSta.HuiQianOver)
		{
			/*只有一个人的情况下, 并且是会签完毕状态，就执行 */
			return "info@当前工作已经到您的待办理了,会签工作已经完成.";
		}
		String leaders = gwf.GetParaString("AddLeader");

		//获取加签的人
		GenerWorkerLists gwfs = new GenerWorkerLists();
		try {
			gwfs.Retrieve(GenerWorkerListAttr.WorkID, gwf.getWorkID(), GenerWorkerListAttr.FK_Node, gwf.getNodeID(), GenerWorkerListAttr.IsPass, -1, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		String empsLeader = "";


		for (GenerWorkerList item : gwfs.ToJavaList())
		{
			if (leaders.contains(item.getEmpNo() + ","))
			{
				empsLeader += item.getEmpNo() + "," + item.getEmpName() + ";";

				String title = "会签邀请通知：" + gwf.getTitle() + "， 来自:" + WebUser.getName();
				String doc = bp.web.WebUser.getName() + ",邀请您对工作｛" + gwf.getTitle() + "｝进行会签,请您在{" + item.getSDT() + "}前完成. 打开工作 {Url} .";

				//发送消息
				bp.wf.Dev2Interface.Port_SendMsg(item.getEmpNo(), title, doc, "HuiQian", "HuiQianType", gwf.getFlowNo(), gwf.getNodeID(), gwf.getWorkID(), gwf.getFID());
			}

		}
		if (DataType.IsNullOrEmpty(empsLeader) == true)
		{
			return "没有增加新的主持人";
		}
		leaders = "('" + leaders.substring(0, leaders.length() - 1).replace(",", "','") + "')";
		//恢复他的状态.
		String sql = "UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE WorkID=" + workID + " AND FK_Node=" + gwf.getNodeID() + " AND IsPass=-1 AND FK_Emp In" + leaders;
		DBAccess.RunSQL(sql);

		gwf.setTodoEmps(gwf.getTodoEmps() + empsLeader);
		gwf.setHuiQianTaskSta(HuiQianTaskSta.HuiQianing);
		Node nd = null;
		try {
			nd = new Node(gwf.getNodeID());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (nd.getHuiQianLeaderRole() == HuiQianLeaderRole.OnlyOne && nd.getTodolistModel() == TodolistModel.TeamupGroupLeader)
		{

			gwf.setHuiQianZhuChiRen(WebUser.getNo());
			gwf.setHuiQianZhuChiRenName(WebUser.getName());
		}
		else
		{
			//多人的组长模式或者协作模式
			if (DataType.IsNullOrEmpty(gwf.getHuiQianZhuChiRen()) == true)
			{
				gwf.setHuiQianZhuChiRen(gwf.getTodoEmps());
			}
		}

		gwf.Update();
		return "主持人增加成功";
	}
	/**
	 执行会签
	 @param workid 工作ID
	 @return 返回执行结果
	*/

	public static String Node_HuiQianDone(long workid) throws Exception {
		return Node_HuiQianDone(workid, 0);
	}

	public static String Node_HuiQianDone(long workid, int toNodeID) throws Exception {
		//生成变量.
		GenerWorkFlow gwf = new GenerWorkFlow(workid);
		if (gwf.getHuiQianTaskSta() == HuiQianTaskSta.HuiQianOver)
		{
			/*只有一个人的情况下: 并且是会签完毕状态,就执行 */
			return "info@当前工作已经到您的待办理了,会签工作已经完成.";
		}

		if (gwf.getHuiQianTaskSta() == HuiQianTaskSta.None)
		{
			Paras ps = new Paras();
			ps.SQL = "SELECT COUNT(WorkID) FROM WF_GenerWorkerlist WHERE FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND (IsPass=0 OR IsPass=-1) AND FK_Emp!=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp";
			ps.Add("FK_Node", gwf.getNodeID());
			ps.Add("WorkID", gwf.getWorkID());
			ps.Add("FK_Emp", WebUser.getNo(), false);
			if (DBAccess.RunSQLReturnValInt(ps, 0) == 0)
			{
				return "close@您没有设置会签人，请在文本框输入会签人，或者选择会签人。";
			}
		}

		//判断当前节点的会签类型.
		Node nd = null;
		try {
			nd = new Node(gwf.getNodeID());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		//设置当前接单是会签的状态.
		gwf.setHuiQianTaskSta(HuiQianTaskSta.HuiQianing); //设置为会签状态.
		if (nd.getHuiQianLeaderRole() == HuiQianLeaderRole.OnlyOne && nd.getTodolistModel() == TodolistModel.TeamupGroupLeader)
		{

			gwf.setHuiQianZhuChiRen(WebUser.getNo());
			gwf.setHuiQianZhuChiRenName(WebUser.getName());
		}
		else
		{
			//多人的组长模式或者协作模式
			if (DataType.IsNullOrEmpty(gwf.getHuiQianZhuChiRen()) == true)
			{
				gwf.setHuiQianZhuChiRen(gwf.getTodoEmps());
			}
		}

		//求会签人.
		GenerWorkerLists gwfs = new GenerWorkerLists();
		try {
			gwfs.Retrieve(GenerWorkerListAttr.WorkID, gwf.getWorkID(), GenerWorkerListAttr.FK_Node, gwf.getNodeID(), GenerWorkerListAttr.IsPass, -1, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		String empsOfHuiQian = "会签人:";
		for (GenerWorkerList item : gwfs.ToJavaList())
		{
			empsOfHuiQian += item.getEmpNo() + "," + item.getEmpName() + ";";

			String title = "会签邀请通知：" + gwf.getTitle() + "， 来自:" + WebUser.getName();
			String doc = bp.web.WebUser.getName() + ",邀请您对工作｛" + gwf.getTitle() + "｝进行会签,请您在{" + item.getSDT() + "}前完成. 打开工作 {Url} .";

			//发送消息
			bp.wf.Dev2Interface.Port_SendMsg(item.getEmpNo(), title, doc, "HuiQian", "HuiQianType", gwf.getFlowNo(), gwf.getNodeID(), gwf.getWorkID(), gwf.getFID());

		}


		//改变了节点就把会签状态去掉.
		gwf.setHuiQianSendToNodeIDStr("");
		gwf.setTodoEmps(gwf.getTodoEmps() + empsOfHuiQian);
		try {
			gwf.Update();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		String sql = "";
		//是否启用会签待办列表, 如果启用了，主持人会签后就转到了HuiQianList.htm里面了.
		if (bp.wf.Glo.isEnableHuiQianList() == true)
		{
			//设置当前操作人员的状态.
			sql = "UPDATE WF_GenerWorkerlist SET IsPass=90 WHERE WorkID=" + gwf.getWorkID() + " AND FK_Node=" + gwf.getNodeID() + " AND FK_Emp='" + WebUser.getNo() + "'";
			DBAccess.RunSQL(sql);
		}

		//恢复他的状态.
		sql = "UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE WorkID=" + gwf.getWorkID() + " AND FK_Node=" + gwf.getNodeID() + " AND IsPass=-1";
		DBAccess.RunSQL(sql);

		//执行会签,写入日志.
		try {
			Dev2Interface.WriteTrack(gwf.getFlowNo(), gwf.getNodeID(), gwf.getNodeName(), gwf.getWorkID(), gwf.getFID(), empsOfHuiQian, ActionType.HuiQian, "执行会签", null, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		String str = "";
		if (nd.getTodolistModel() == TodolistModel.TeamupGroupLeader)
		{
			/*如果是组长模式.*/
			str = "close@保存成功.\t\n该工作已经移动到会签列表中了,等到所有的人会签完毕后,就可以出现在待办列表里.";
			str += "\t\n如果您要增加或者移除会签人请到会签列表找到该记录,执行操作.";

			//删除自己的意见，以防止其他人员看到.
			bp.wf.Dev2Interface.DeleteCheckInfo(gwf.getFlowNo(), gwf.getWorkID(), gwf.getNodeID());
			return str;
		}

		if (nd.getTodolistModel() == TodolistModel.Teamup)
		{
			if (toNodeID == 0)
			{
				return "Send@[" + nd.getName() + "]会签成功执行.";
			}

			Node toND = null;
			try {
				toND = new Node(toNodeID);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			//如果到达的节点是按照接受人来选择,就转向接受人选择器.
			if (toND.getHisDeliveryWay() == DeliveryWay.BySelected)
			{
				return "url@Accepter.htm?FK_Node=" + gwf.getNodeID() + "&FID=" + gwf.getFID() + "&WorkID=" + gwf.getWorkID() + "&FK_Flow=" + gwf.getFlowNo() + "&ToNode=" + toNodeID;
			}
			else
			{
				return "Send@执行发送操作";
			}
		}

		return str;
	}
	/**
	 删除会签人员

	 @param workID
	 @param emp 要删除的编号
	 @return 返回执行的
	*/
	public static String Node_HuiQian_Delete(long workID, String emp) throws Exception {
		if (emp.equals(WebUser.getNo()) == true)
		{
			throw new RuntimeException("err@您不能移除您自己");
		}

		//要找到主持人.
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		String addLeader = gwf.GetParaString("AddLeader");
		if (gwf.getTodoEmps().contains(WebUser.getNo() + ",") == false && addLeader.contains(WebUser.getNo() + ",") == false)
		{
			throw new RuntimeException("err@您不是主持人，您不能删除。");
		}

		//删除该数据.
		GenerWorkerList gwlOfMe = new GenerWorkerList();
		try {
			gwlOfMe.Delete(GenerWorkerListAttr.FK_Emp, emp, GenerWorkerListAttr.WorkID, gwf.getWorkID(), GenerWorkerListAttr.FK_Node, gwf.getNodeID());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		//如果已经没有会签待办了,就设置当前人员状态为0.  增加这部分.
		Paras ps = new Paras();
		ps.SQL = "SELECT COUNT(WorkID) FROM WF_GenerWorkerlist WHERE FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND IsPass=0 ";
		ps.Add("FK_Node", gwf.getNodeID());
		ps.Add("WorkID", gwf.getWorkID());
		if (DBAccess.RunSQLReturnValInt(ps) == 0)
		{
			gwf.setHuiQianTaskSta(HuiQianTaskSta.None); //设置为 None . 不能设置会签完成,不然其他的就没有办法处理了.
			gwf.Update();
			ps = new Paras();
			ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND FK_Emp=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp";
			ps.Add("FK_Node", gwf.getNodeID());
			ps.Add("WorkID", gwf.getWorkID());
			ps.Add("FK_Emp", WebUser.getNo(), false);
			DBAccess.RunSQL(ps);
		}

		//从待办里移除.
		Emp myemp = new Emp(emp);
		String str = gwf.getTodoEmps();
		str = str.replace(myemp.getUserID() + "," + myemp.getName() +";", "");
		str = str.replace(myemp.getName() +";", "");
		addLeader = addLeader.replace(emp + ",", "");
		gwf.SetPara("AddLeader", addLeader);
		gwf.setTodoEmps(str);
		try {
			gwf.Update();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//删除该人员的审核信息
		String sql = "DELETE FROM ND" + Integer.parseInt(gwf.getFlowNo()) + "Track WHERE WorkID = " + gwf.getWorkID() + " AND ActionType = " + ActionType.WorkCheck.getValue() + " AND NDFrom = " + gwf.getNodeID() + " AND NDTo = " + gwf.getNodeID() + " AND EmpFrom = '" + emp + "'";
		DBAccess.RunSQL(sql);
		return "执行成功.";
	}

		///#endregion


		///#region 写入轨迹.
	/**
	 写入BBS

	 @param frmID 表单ID
	 @param frmName 表单名称
	 @param workID 工作ID
	 @param msg 消息
	 @param fid 流程ID
	 @param flowNo 流程编号
	 @param flowName 流程名称
	 @param nodeID 节点ID
	*/

	public static void Track_WriteBBS(String frmID, String frmName, long workID, String msg, long fid, String flowNo, String flowName, int nodeID) throws Exception {
		Track_WriteBBS(frmID, frmName, workID, msg, fid, flowNo, flowName, nodeID, "");
	}

	public static void Track_WriteBBS(String frmID, String frmName, long workID, String msg, long fid, String flowNo, String flowName) throws Exception {
		Track_WriteBBS(frmID, frmName, workID, msg, fid, flowNo, flowName, 0, "");
	}

	public static void Track_WriteBBS(String frmID, String frmName, long workID, String msg, long fid, String flowNo) throws Exception {
		Track_WriteBBS(frmID, frmName, workID, msg, fid, flowNo, "", 0, "");
	}

	public static void Track_WriteBBS(String frmID, String frmName, long workID, String msg, long fid) throws Exception {
		Track_WriteBBS(frmID, frmName, workID, msg, fid, "", "", 0, "");
	}

	public static void Track_WriteBBS(String frmID, String frmName, long workID, String msg) throws Exception {
		Track_WriteBBS(frmID, frmName, workID, msg, 0, "", "", 0, "");
	}

	public static void Track_WriteBBS(String frmID, String frmName, long workID, String msg, long fid, String flowNo, String flowName, int nodeID, String nodeName) throws Exception {
		bp.ccbill.Track tk = new bp.ccbill.Track();
		tk.setWorkID(String.valueOf(workID));
		tk.setFrmID(frmID);
		tk.setFrmName(frmName);
		tk.setActionType("BBS");
		tk.setActionTypeText("评论");

		tk.setRec(WebUser.getNo());
		tk.setRecName(WebUser.getName());
		tk.setDeptNo(WebUser.getDeptNo());
		tk.setDeptName(WebUser.getDeptName());

		tk.setMyPK(tk.getFrmID() + "_" + tk.getWorkID() + "_" + tk.getRec() + "_100");
		tk.setMsg(msg);
		tk.setRDT(DataType.getCurrentDateTime());

		//流程信息.
		tk.setNodeID(nodeID);
		tk.setNodeName(nodeName);
		tk.setFuncNo(flowNo);
		tk.setFuncName(flowName);
		tk.setFID(fid);

		tk.Save();

		//修改抄送状态
		bp.wf.Dev2Interface.Node_CC_SetCheckOver(workID);
	}

		///#endregion 写入轨迹.


		///#region 流程属性与节点属性变更接口.
	/**
	 更改流程属性

	 @param fk_flow 流程编号
	 @param attr1 字段1
	 @param v1 值1
	 @param attr2 字段2(可为null)
	 @param v2 值2(可为null)
	 @return 执行结果
	*/
	public static String ChangeAttr_Flow(String fk_flow, String attr1, Object v1, String attr2, Object v2) throws Exception {
		Flow fl = new Flow(fk_flow);
		if (attr1 != null)
		{
			fl.SetValByKey(attr1, v1);
		}

		if (attr2 != null)
		{
			fl.SetValByKey(attr2, v2);
		}

		fl.Update();
		return "修改成功";
	}

		///#endregion 流程属性与节点属性变更接口.


		///#region ccform 接口

	/**
	  获得指定轨迹的json数据.

	 @param flowNo 流程编号
	 @param mypk 流程主键
	 @return 返回当时的表单json字符串
	*/
	public static String CCFrom_GetFrmDBJson(String flowNo, String mypk) throws Exception
	{
		return DBAccess.GetBigTextFromDB("ND" + Integer.parseInt(flowNo) + "Track", "MyPK", mypk, "FrmDB");
	}
	/**
	 SDK签章接口

	 @param workid 工作ID
	 @param nodeid 签章节点ID
	 @param deptno 部门编号
	 @param stationno 角色编号
	 @return 返回非null值时，为签章失败
	*/
	public static String CCForm_Seal(long workid, int nodeid, String deptno, String stationno)
	{
		try
		{
			FrmEleDBs eleDBs = new FrmEleDBs("ND" + nodeid, String.valueOf(workid));

			if (!eleDBs.isEmpty())
			{
				eleDBs.Delete(FrmEleDBAttr.FK_MapData, "ND" + nodeid, FrmEleDBAttr.RefPKVal, workid);
			}

			String sealimg = bp.wf.Glo.getCCFlowAppPath() + "DataUser/Seal/" + deptno + "_" + stationno + ".jpg";

			if ((new File(SystemConfig.getPathOfWebApp() + sealimg)).isFile() == false)
			{
				return "签章文件：" + sealimg + "不存在，请联系管理员！";
			}

			FrmEleDB athDB_N = new FrmEleDB();
			athDB_N.setFrmID("ND" + nodeid);
			athDB_N.setRefPKVal( String.valueOf(workid));
			athDB_N.setEleID(String.valueOf(workid));
			athDB_N.GenerPKVal();
			athDB_N.setTag1(sealimg);
			athDB_N.DirectInsert();

			return null;
		}
		catch (RuntimeException ex)
		{
			return "签章错误：" + ex.getMessage();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

		///#endregion ccform 接口


		///#region 页面.
	/**
	 附件上传接口

	 @param nodeid 节点ID
	 @param flowNo 流程ID
	 @param workid 工作ID
	 @param athNo 附件属性No
	 @param frmID FK_MapData
	 @param filePath 附件路径
	 @param fileName 附件名称
	 @param sort 分类
	 @return
	*/

	public static String CCForm_AddAth(int nodeid, String flowNo, long workid, String athNo, String frmID, String filePath, String fileName, String sort, int fid) throws Exception {
		return CCForm_AddAth(nodeid, flowNo, workid, athNo, frmID, filePath, fileName, sort,"", fid, 0);
	}

	public static String CCForm_AddAth(int nodeid, String flowNo, long workid, String athNo, String frmID, String filePath, String fileName, String sort) throws Exception {
		return CCForm_AddAth(nodeid, flowNo, workid, athNo, frmID, filePath, fileName, sort,"", 0, 0);
	}

	public static String CCForm_AddAth(int nodeid, String flowNo, long workid, String athNo, String frmID, String filePath, String fileName) throws Exception {
		return CCForm_AddAth(nodeid, flowNo, workid, athNo, frmID, filePath, fileName, null,"", 0, 0);
	}

	public static String CCForm_AddAth(int nodeid, String flowNo, long workid, String athNo, String frmID, String filePath, String fileName, String sort,String myNote, long fid, long pworkid) throws Exception {
		String pkVal = String.valueOf(workid);
		// 多附件描述.
		FrmAttachment athDesc = new FrmAttachment(athNo);
		MapData mapData = new MapData(frmID);
		String msg = null;


			///#region 获取表单方案
		//求主键. 如果该表单挂接到流程上.
		if (nodeid != 0)
		{
			//判断表单方案。
			FrmNode fn = new FrmNode(nodeid, frmID);
			if (fn.getFrmSln() == FrmSln.Readonly)
			{
				return "err@不允许上传附件.";
			}

			//是默认的方案的时候.
			if (fn.getFrmSln() == FrmSln.Default)
			{
				//判断当前方案设置的whoIsPk ，让附件集成 whoIsPK 的设置。
				if (fn.getWhoIsPK() == WhoIsPK.FID)
				{
					pkVal = String.valueOf(fid);
				}

				if (fn.getWhoIsPK() == WhoIsPK.PWorkID)
				{
					pkVal = String.valueOf(pworkid);
				}
			}

			//自定义方案.
			if (fn.getFrmSln() == FrmSln.Self)
			{
				if (athDesc.getHisCtrlWay() == AthCtrlWay.FID)
				{
					pkVal = String.valueOf(fid);
				}

				if (athDesc.getHisCtrlWay() == AthCtrlWay.PWorkID)
				{
					pkVal = String.valueOf(pworkid);
				}
			}
		}


			///#endregion 获取表单方案

		//获取上传文件是否需要加密
		boolean fileEncrypt = SystemConfig.isEnableAthEncrypt();


			///#region 文件上传的iis服务器上 or db数据库里.
		if (athDesc.getAthSaveWay() == AthSaveWay.IISServer)
		{
			String savePath = athDesc.getSaveTo();

			if (savePath.contains("@") == true || savePath.contains("*") == true)
			{
				/*如果有变量*/
				savePath = savePath.replace("*", "@");

				if (savePath.contains("@") && nodeid != 0)
				{
					/*如果包含 @ */
					bp.wf.Flow flow = new bp.wf.Flow(flowNo);
					bp.wf.GERpt myen = flow.getHisGERpt();
					myen.setOID(workid);
					myen.RetrieveFromDBSources();
					savePath = bp.wf.Glo.DealExp(savePath, myen, null);
				}
				if (savePath.contains("@") == true)
				{
					throw new RuntimeException("@路径配置错误,变量没有被正确的替换下来." + savePath);
				}
			}
			else
			{
				savePath = athDesc.getSaveTo() + "/" + pkVal;
			}

			//替换关键的字串.
			savePath = savePath.replace("\\\\", "/");

			try
			{
				if ((new File(savePath)).isDirectory() == false)
				{
					(new File(savePath)).mkdirs();
				}
			}
			catch (RuntimeException ex)
			{
				throw new RuntimeException("err@创建路径出现错误，可能是没有权限或者路径配置有问题:" + savePath + "@异常信息:" + ex.getMessage());
			}

			String guid = DBAccess.GenerGUID(0, null, null);
			String ext = fileName.substring(fileName.lastIndexOf("."));
			String realSaveTo = savePath + "/" + guid + "." + fileName;
			realSaveTo = realSaveTo.replace("~", "-");
			realSaveTo = realSaveTo.replace("'", "-");
			realSaveTo = realSaveTo.replace("*", "-");
			if (fileEncrypt == true)
			{
				String strtmp = realSaveTo + ".tmp";
				if ((new File(filePath)).isFile() == true)
				{
					try {
						Files.copy(Paths.get(filePath), Paths.get(strtmp), StandardCopyOption.COPY_ATTRIBUTES); //先明文保存到本地(加个后缀名.tmp)
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				else
				{
					return "err@需要保存的文件不存在";
				}

				AesEncodeUtil.encryptFile(strtmp, strtmp.replace(".tmp", "")); //加密
				(new File(strtmp)).delete(); //删除临时文件
			}
			else
			{
				//文件保存的路径
				if ((new File(filePath)).isFile() == true)
				{
					try {
						Files.copy(Paths.get(filePath), Paths.get(realSaveTo), StandardCopyOption.COPY_ATTRIBUTES);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				else
				{
					return "err@需要保存的文件不存在";
				}
			}

			File info = new File(realSaveTo);
			FrmAttachmentDB dbUpload = new FrmAttachmentDB();
			dbUpload.setMyPK(guid); // athDesc.getFrmID() + oid.ToString();
			dbUpload.setNodeID(nodeid);
			dbUpload.setSort(sort);
			dbUpload.setFrmID(athDesc.getFrmID());
			dbUpload.setFKFrmAttachment(athNo);

			dbUpload.setFileExts(ext);
			dbUpload.setFID(fid);
			if (fileEncrypt == true)
			{
				dbUpload.SetPara("IsEncrypt", 1);
			}


				///#region 处理文件路径，如果是保存到数据库，就存储pk.
			if (athDesc.getAthSaveWay() == AthSaveWay.IISServer)
			{
				//文件方式保存
				dbUpload.setFileFullName(realSaveTo);
			}

			if (athDesc.getAthSaveWay() == AthSaveWay.FTPServer)
			{
				//保存到数据库
				dbUpload.setFileFullName(dbUpload.getMyPK());
			}

				///#endregion 处理文件路径，如果是保存到数据库，就存储pk.

			dbUpload.setFileName(fileName);
			dbUpload.setFileSize((float)info.length());
			dbUpload.setRDT(DataType.getCurrentDateTimess());
			dbUpload.setRec(WebUser.getNo());
			dbUpload.setRecName(WebUser.getName());
			dbUpload.setDeptNo(WebUser.getDeptNo());
			dbUpload.setDeptName(WebUser.getDeptName());
			dbUpload.setRefPKVal(pkVal);
			dbUpload.setMyNote(myNote);

			dbUpload.SetValByKey(FrmAttachmentDBAttr.NoOfObj, athDesc.getNoOfObj());
			dbUpload.Insert();

			if (athDesc.getAthSaveWay() == AthSaveWay.DB)
			{
				//执行文件保存.
				DBAccess.SaveFileToDB(realSaveTo, dbUpload.getEnMap().getPhysicsTable(), "MyPK", dbUpload.getMyPK(), "FDB");
			}
		}

			///#endregion 文件上传的iis服务器上 or db数据库里.


		//#region 保存到数据库 / FTP服务器上 / OSS服务器上.
		if (athDesc.getAthSaveWay() == AthSaveWay.DB || athDesc.getAthSaveWay() == AthSaveWay.FTPServer || athDesc.getAthSaveWay() == AthSaveWay.OSS)
		{
			String guid = DBAccess.GenerGUID(0, null, null);

			//把文件临时保存到一个位置.
			String temp = SystemConfig.getPathOfTemp() + "" + guid + ".tmp";

			if (fileEncrypt == true)
			{

				String strtmp = SystemConfig.getPathOfTemp() + "" + guid + "_Desc" + ".tmp";
				if ((new File(filePath)).isFile() == true)
				{
					Files.copy(Paths.get(filePath), Paths.get(strtmp), StandardCopyOption.COPY_ATTRIBUTES); //先明文保存到本地(加个后缀名.tmp)
				}
				else
				{
					return "err@需要保存的文件不存在";
				}

				AesEncodeUtil.encryptFile(strtmp, temp); //加密
				(new File(strtmp)).delete(); //删除临时文件
			}
			else
			{
				//文件保存的路径
				if ((new File(filePath)).isFile() == true)
				{
					try {
						Files.copy(Paths.get(filePath), Paths.get(temp), StandardCopyOption.COPY_ATTRIBUTES);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				else
				{
					return "err@需要保存的文件不存在";
				}
			}
			String ext = fileName.substring(fileName.lastIndexOf("."));

			File info = new File(temp);
			FrmAttachmentDB dbUpload = new FrmAttachmentDB();
			dbUpload.setMyPK(DBAccess.GenerGUID(0, null, null));
			dbUpload.setSort(sort);
			dbUpload.setNodeID(nodeid);
			dbUpload.setFrmID(athDesc.getFrmID());

			dbUpload.setFKFrmAttachment(athDesc.getMyPK());
			dbUpload.setFileExts(ext);
			dbUpload.setFID(fid); //流程id.
			if (fileEncrypt == true)
			{
				dbUpload.SetPara("IsEncrypt", 1);
			}

			if (athDesc.getAthUploadWay() == AthUploadWay.Inherit)
			{
				/*如果是继承，就让他保持本地的PK. */
				dbUpload.setRefPKVal( pkVal.toString());
			}

			if (athDesc.getAthUploadWay() == AthUploadWay.Interwork)
			{
				/*如果是协同，就让他是PWorkID. */
				Paras ps = new Paras();
				ps.SQL = "SELECT PWorkID FROM WF_GenerWorkFlow WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID";
				ps.Add("WorkID", pkVal, false);
				String pWorkID = String.valueOf(DBAccess.RunSQLReturnValInt(ps, 0));
				if (pWorkID == null || Objects.equals(pWorkID, "0"))
				{
					pWorkID = pkVal;
				}

				dbUpload.setRefPKVal( pWorkID);
			}

			dbUpload.setFrmID(athDesc.getFrmID());
			dbUpload.setFKFrmAttachment(athDesc.getMyPK());
			dbUpload.setFileName(fileName);
			dbUpload.setFileSize((float)info.length());
			dbUpload.setRDT(DataType.getCurrentDateTimess());
			dbUpload.setRec(WebUser.getNo());
			dbUpload.setRecName(WebUser.getName());
			dbUpload.setDeptNo(WebUser.getDeptNo());
			dbUpload.setDeptName(WebUser.getDeptName());
			dbUpload.setUploadGUID(guid);
			dbUpload.setMyNote(myNote);

			if (athDesc.getAthSaveWay() == AthSaveWay.DB)
			{
				dbUpload.Insert();
				//把文件保存到指定的字段里.
				dbUpload.SaveFileToDB("FileDB", temp);
			}

			if (athDesc.getAthSaveWay() == AthSaveWay.FTPServer)
			{
				/*保存到fpt服务器上.*/
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
				String ny = sdf.format(new Date());
				String workDir = ny + "/" + athDesc.getFrmID() + "/";
				FtpUtil ftpUtil = bp.wf.Glo.getFtpUtil();

				ftpUtil.changeWorkingDirectory(workDir, true);

				// 把文件放在FTP服务器上去.
				ftpUtil.uploadFile(guid + "." + dbUpload.getFileExts(), temp);

				ftpUtil.releaseConnection();
				//设置路径.
				dbUpload.setFileFullName(ny + "//" + athDesc.getFrmID() + "//" + guid + "." + dbUpload.getFileExts());
				dbUpload.Insert();
				(new File(temp)).delete();
			}
			// 文件上传到OSS服务器上
			if(athDesc.getAthSaveWay() == AthSaveWay.OSS)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
				String ny = sdf.format(new Date());
				String fName = ny + File.separator + athDesc.getFrmID() + File.separator + guid + "." + dbUpload.getFileExts();
				//调用OSS工具类上传
				OSSUploadFileUtils.uploadFile(fName, Files.newInputStream(new File(temp).toPath()));
				//设置路径.
				dbUpload.setFileFullName(fName);
				dbUpload.Insert();
				(new File(temp)).delete();
			}

		}

			///#endregion 保存到数据库.

		return "";
	}
		///#endregion 页面.


		///#region 与工作处理器相关的接口
	/**
	 获得一个节点要转向的节点

	 @param flowNo 流程编号
	 @param ndFrom 节点从
	 @param workid 工作ID
	 @return 返回可以到达的节点
	*/
	public static Nodes WorkOpt_GetToNodes(String flowNo, int ndFrom, long workid, long FID) throws Exception {
		Nodes nds = new Nodes();

		Node nd = new Node(ndFrom);
		Nodes toNDs = nd.getHisToNodes();

		Flow fl = nd.getHisFlow();

		//获得表单的数据，以方便计算方向条件.
		GERpt rpt = fl.getHisGERpt();
		if (FID == 0)
		{
			rpt.setOID(workid);
		}
		else
		{
			rpt.setOID(FID);
		}
		rpt.Retrieve();

		//方向.
		Directions dirs = new Directions(nd.getNodeID());

		//同表单的子线程.
		Nodes sameSheetNodes = new Nodes();

		//首先输出普通的节点。
		for (Direction dir : dirs.ToJavaList())
		{
			Node mynd = new Node(dir.getToNode());
			if (mynd.getItIsSubThread() == true)
			{
				sameSheetNodes.AddEntity(mynd);
				continue; //如果是子线程节点.
			}

			//是否可以处理？
			boolean bIsCanDo = true;


				///#region 判断方向条件,如果设置了方向条件，判断是否可以通过，不能通过的，就不让其显示.
			Conds conds = new Conds();
			int i = 0;
			try {
				i = conds.Retrieve(CondAttr.FK_Node, nd.getNodeID(), CondAttr.ToNodeID, mynd.getNodeID(), CondAttr.CondType, CondType.Dir.getValue(), CondAttr.Idx);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			// 设置方向条件，就判断它。
			if (i > 0)
			{
				bIsCanDo = conds.GenerResult(rpt, null);
			}

			//条件不符合则不通过
			if (bIsCanDo == false)
			{
				continue;
			}

				///#endregion

			nds.AddEntity(mynd);
		}

		//同表单子线程.
		for (Node mynd : sameSheetNodes.ToJavaList())
		{
			if (mynd.getItIsSubThread() == false)
			{
				continue; //如果是子线程节点.
			}

			if (mynd.getHisRunModel() == RunModel.SubThreadUnSameWorkID)
			{
				continue; //如果是异表单的分合流.
			}


				///#region 判断方向条件,如果设置了方向条件，判断是否可以通过，不能通过的，就不让其显示.
			Conds conds = new Conds();
			int i = conds.Retrieve(CondAttr.FK_Node, nd.getNodeID(), CondAttr.ToNodeID, mynd.getNodeID(), CondAttr.CondType, CondType.Dir.getValue(), CondAttr.Idx);

			//数量.
			if (conds.size() == 0)
			{
				nds.AddEntity(mynd);
				continue;
			}

			//是否可以处理.
			boolean bIsCanDo = false;
			for (Direction dir : dirs.ToJavaList())
			{
				if (dir.getToNode() == mynd.getNodeID())
				{
					bIsCanDo = conds.GenerResult(rpt, null);
				}
			}

				///#endregion

			//如果通过了.
			if (bIsCanDo == true)
			{
				nds.AddEntity(mynd);
			}
		}

		// 检查是否具有异表单的子线程.
		boolean isHave = false;
		for (Node mynd : toNDs.ToJavaList())
		{
			if (mynd.getHisRunModel() == RunModel.SubThreadUnSameWorkID)
			{
				isHave = true;
			}
		}

		if (isHave)
		{
			Node myn1d = new Node();
			myn1d.setNodeID(0);
			myn1d.setName("可以分发启动的节点");
			nds.AddEntity(myn1d);

			/*增加异表单的子线程*/
			for (Node mynd : toNDs.ToJavaList())
			{
				if (mynd.getHisRunModel() != RunModel.SubThreadUnSameWorkID)
				{
					continue;
				}


					///#region 判断方向条件,如果设置了方向条件，判断是否可以通过，不能通过的，就不让其显示.
				Conds conds = new Conds();
				int i = conds.Retrieve(CondAttr.FK_Node, nd.getNodeID(), CondAttr.ToNodeID, mynd.getNodeID(), CondAttr.CondType, CondType.Dir.getValue(), CondAttr.Idx);
				// 设置方向条件，就判断它。
				if (i > 0)
				{
					//判断是否可以通过.
					if (conds.GenerResult(rpt, null) == false)
					{
						continue;
					}
				}

					///#endregion

				nds.AddEntity(mynd);
			}
		}

		//返回它.
		return nds;
	}
	/**
	 在节点选择转向功能界面，获得当前人员上一次选择的节点，在界面里让其自动选择。
	 以改善用户操作体验，就类似于默认记忆上一次的操作功能。

	 @param flowNo 流程编号
	 @param nodeID 当前节点编号
	 @return 返回上一次当前用户选择的节点,如果没有找到（当前用户第一次发送的情况下找不到）就返回0.
	*/
	public static int WorkOpt_ToNodes_GetLasterSelectNodeID(String flowNo, int nodeID)
	{
		String sql = "";
		switch (SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
			case Access:
				sql = "SELECT TOP 1 NDTo FROM ND" + Integer.parseInt(flowNo) + "Track WHERE EmpFrom='" + WebUser.getNo() + "' AND NDFrom=" + nodeID + " AND (ActionType=" + ActionType.Forward.getValue() + " OR ActionType=" + ActionType.ForwardFL.getValue() + " OR ActionType=" + ActionType.SubThreadForward.getValue() + ")  ORDER BY RDT DESC";
				break;
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				sql = "SELECT NDTo FROM ND" + Integer.parseInt(flowNo) + "Track WHERE  RowNum=1 AND EmpFrom='" + WebUser.getNo() + "' AND NDFrom=" + nodeID + " AND (ActionType=" + ActionType.Forward.getValue() + " OR ActionType=" + ActionType.ForwardFL.getValue() + " OR ActionType=" + ActionType.SubThreadForward.getValue() + ")  ORDER BY RDT DESC";
				break;
			case MySQL:
			case GBASE8A:
			case GBASE8CByMySQL:
				sql = "SELECT NDTo FROM ND" + Integer.parseInt(flowNo) + "Track WHERE EmpFrom='" + WebUser.getNo() + "' AND NDFrom=" + nodeID + " AND (ActionType=" + ActionType.Forward.getValue() + " OR ActionType=" + ActionType.ForwardFL.getValue() + " OR ActionType=" + ActionType.SubThreadForward.getValue() + ") limit 0,1";
				break;
			case Informix:
				sql = "SELECT first 1 NDTo FROM ND" + Integer.parseInt(flowNo) + "Track WHERE EmpFrom='" + WebUser.getNo() + "' AND NDFrom=" + nodeID + " AND (ActionType=" + ActionType.Forward.getValue() + " OR ActionType=" + ActionType.ForwardFL.getValue() + " OR ActionType=" + ActionType.SubThreadForward.getValue() + ")  ORDER BY RDT DESC";
				break;
			case PostgreSQL:
			case UX:
			case HGDB:
				sql = "SELECT NDTo FROM ND" + Integer.parseInt(flowNo) + "Track WHERE EmpFrom='" + WebUser.getNo() + "' AND NDFrom=" + nodeID + " AND (ActionType=" + ActionType.Forward.getValue() + " OR ActionType=" + ActionType.ForwardFL.getValue() + " OR ActionType=" + ActionType.SubThreadForward.getValue() + ") ORDER BY RDT DESC limit 1";
				break;
			default:
				throw new RuntimeException("@没有实现该类型的数据库支持.");
		}
		return DBAccess.RunSQLReturnValInt(sql, 0);
	}
	/**
	 发送到节点

	 @param flowNo
	 @param nodeID
	 @param workid
	 @param fid
	 @param toNodes
	*/
	public static SendReturnObjs WorkOpt_SendToNodes(String flowNo, int nodeID, long workid, long fid, String toNodes) throws Exception {
		//把参数更新到数据库里面.
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workid);
		gwf.RetrieveFromDBSources();
		gwf.setParasToNodes(toNodes);
		gwf.Save();

		Node nd = new Node(nodeID);
		Work wk = nd.getHisWork();
		wk.setOID(workid);
		wk.Retrieve();

		// 以下代码是从 MyFlow.htm Send 方法copy 过来的，需要保持业务逻辑的一致性，所以代码需要保持一致.
		WorkNode firstwn = new WorkNode(wk, nd);
		String msg = "";
		SendReturnObjs objs = firstwn.NodeSend();
		return objs;
	}
	/**
	 获得接收人的数据源

	 @param nodeID 指定节点
	 @param WorkID 工作ID
	 @return
	*/

	public static DataSet WorkOpt_AccepterDB(int nodeID, long WorkID) throws Exception {
		return WorkOpt_AccepterDB(nodeID, WorkID, 0);
	}

	public static DataSet WorkOpt_AccepterDB(int nodeID, long WorkID, long FID) throws Exception {
		DataSet ds = new DataSet();

		Selector en = new Selector(nodeID);
		switch (en.getSelectorModel())
		{
			case Station:
				DataTable dt = WorkOpt_Accepter_ByStation(nodeID);
				dt.setTableName( "Port_Emp");
				ds.Tables.add(dt);
				break;
			case SQL:
				ds = WorkOpt_Accepter_BySQL(nodeID);
				break;
			case Dept:
				ds = WorkOpt_Accepter_ByDept(nodeID);
				break;
			case Emp:
				ds = WorkOpt_Accepter_ByEmp(nodeID);
				break;
			case Url:
			default:
				break;
		}
		return ds;
	}
	/**
	 获取节点绑定角色人员

	 @param nodeID 指定的节点
	 @return
	*/
	private static DataTable WorkOpt_Accepter_ByStation(int nodeID) throws Exception {
		if (nodeID == 0)
		{
			throw new RuntimeException("@流程设计错误，没有转向的节点。举例说明: 当前是A节点。如果您在A点的属性里启用了[接受人]按钮，那么他的转向节点集合中(就是A可以转到的节点集合比如:A到B，A到C, 那么B,C节点就是转向节点集合)，必须有一个节点是的节点属性的[访问规则]设置为[由上一步发送人员选择]");
		}

		NodeStations stas = new NodeStations(nodeID);
		if (stas.size() == 0)
		{
			bp.wf.Node toNd = new bp.wf.Node(nodeID);
			throw new RuntimeException("@流程设计错误：设计员没有设计节点[" + toNd.getName() + "]，接受人的角色范围。");
		}

		String empNo = "No";
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			empNo = "UserID as No";
		}

		// 优先解决本部门的问题。
		String sql = "";

		sql = "SELECT A." + empNo + ",A.Name, A.FK_Dept, B.Name as DeptName FROM Port_Emp A,Port_Dept B WHERE A.FK_Dept=B.No AND a.NO IN ( ";
		sql += "SELECT FK_EMP FROM Port_DeptEmpStation WHERE FK_STATION ";
		sql += "IN (SELECT FK_STATION FROM WF_NodeStation WHERE FK_Node=" + nodeID + ") ";
		sql += ") AND a.No IN (SELECT " + empNo + " FROM Port_Emp WHERE FK_Dept ='" + WebUser.getDeptNo() + "')";
		sql += " ORDER BY B.Idx,B.No,A.Idx,A.No ";


		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() != 0)
		{
			return dt;
		}

		//组织结构中所有角色人员
		sql = "SELECT A." + empNo + ",A.Name, A.FK_Dept, B.Name as DeptName FROM Port_Emp A,Port_Dept B WHERE A.FK_Dept=B.No AND a.NO IN ( ";
		sql += "SELECT FK_Emp FROM  Port_DeptEmpStation WHERE FK_STATION ";
		sql += "IN (SELECT FK_Station FROM WF_NodeStation WHERE FK_Node=" + nodeID + ") ";
		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			sql += " AND Port_DeptEmpStation.OrgNo='" + WebUser.getOrgNo() + "'";
		}
		sql += ") ORDER BY A.FK_Dept,A.No ";
		return DBAccess.RunSQLReturnTable(sql);
	}
	/**
	 按sql方式
	*/
	private static DataSet WorkOpt_Accepter_BySQL(int nodeID) throws Exception {
		DataSet ds = new DataSet();
		Selector MySelector = new Selector(nodeID);
		String sqlGroup = MySelector.getSelectorP1();
		sqlGroup = sqlGroup.replace("@WebUser.No", WebUser.getNo());
		sqlGroup = sqlGroup.replace("@WebUser.Name", WebUser.getName());
		sqlGroup = sqlGroup.replace("@WebUser.FK_Dept", WebUser.getDeptNo());

		String sqlDB = MySelector.getSelectorP2();
		sqlDB = sqlDB.replace("@WebUser.No", WebUser.getNo());
		sqlDB = sqlDB.replace("@WebUser.Name", WebUser.getName());
		sqlDB = sqlDB.replace("@WebUser.FK_Dept", WebUser.getDeptNo());

		DataTable dtGroup = DBAccess.RunSQLReturnTable(sqlGroup);
		dtGroup.setTableName( "Port_Dept");
		ds.Tables.add(dtGroup);
		DataTable dtDB = DBAccess.RunSQLReturnTable(sqlDB);
		dtDB.setTableName( "Port_Emp");
		ds.Tables.add(dtDB);

		return ds;
	}

	/**
	 获取接收人选择器，按部门绑定

	 @param nodeID
	 @return
	*/
	private static DataSet WorkOpt_Accepter_ByDept(int nodeID)
	{
		String empNo = "No";
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			empNo = "UserID as No";
		}

		DataSet ds = new DataSet();
		String orderByIdx = "Idx,";
		String sqlGroup = "SELECT No,Name FROM Port_Dept WHERE No IN (SELECT FK_Dept FROM WF_NodeDept WHERE FK_Node='" + nodeID + "') ORDER BY " + orderByIdx + "No";
		String sqlDB = "SELECT " + empNo + ",Name, FK_Dept FROM Port_Emp WHERE FK_Dept IN (SELECT FK_Dept FROM WF_NodeDept WHERE FK_Node='" + nodeID + "') ORDER BY " + orderByIdx + "No";

		DataTable dtGroup = DBAccess.RunSQLReturnTable(sqlGroup);
		dtGroup.setTableName( "Port_Dept");
		ds.Tables.add(dtGroup);

		DataTable dtDB = DBAccess.RunSQLReturnTable(sqlDB);
		dtDB.setTableName( "Port_Emp");
		ds.Tables.add(dtDB);
		return ds;
	}
	/**
	 按BindByEmp 方式
	*/
	private static DataSet WorkOpt_Accepter_ByEmp(int nodeID)
	{
		String orderByIdx = "Idx,";
		String sqlGroup = "SELECT No,Name FROM Port_Dept WHERE No IN (SELECT FK_Dept FROM Port_Emp WHERE No in(SELECT FK_EMP FROM WF_NodeEmp WHERE FK_Node='" + nodeID + "')) ORDER BY " + orderByIdx + "No";


		String sqlDB = "SELECT " + bp.sys.base.Glo.getUserNo() + ",Name,FK_Dept FROM Port_Emp WHERE No in (SELECT FK_EMP FROM WF_NodeEmp WHERE FK_Node='" + nodeID + "') ORDER BY " + orderByIdx + "No";

		DataSet ds = new DataSet();
		DataTable dtGroup = DBAccess.RunSQLReturnTable(sqlGroup);
		dtGroup.setTableName( "Port_Dept");
		ds.Tables.add(dtGroup);

		DataTable dtDB = DBAccess.RunSQLReturnTable(sqlDB);
		dtDB.setTableName( "Port_Emp");
		ds.Tables.add(dtDB);
		return ds;
	}

	/**
	 设置指定的节点接受人

	 @param nodeID 节点ID
	 @param workid 工作ID
	 @param fid 流程ID
	 @param emps 指定的人员集合zhangsan,lisi,wangwu
	 @param isNextTime 是否下次自动设置
	*/
	public static void WorkOpt_SetAccepter(int nodeID, long workid, long fid, String emps, boolean isNextTime) throws Exception {
		SelectAccpers ens = new SelectAccpers();
		ens.Delete(SelectAccperAttr.FK_Node, nodeID, SelectAccperAttr.WorkID, workid);

		//下次是否记忆选择，清空掉。
		String sql = "UPDATE WF_SelectAccper SET " + SelectAccperAttr.IsRemember + " = 0 WHERE Rec='" + WebUser.getNo() + "' AND IsRemember=1 AND FK_Node=" + nodeID;
		DBAccess.RunSQL(sql);

		//开始执行保存.
		String[] strs = emps.split("[,]", -1);
		for (String str : strs)
		{
			if (DataType.IsNullOrEmpty(str) == true)
			{
				continue;
			}

			SelectAccper en = new SelectAccper();
			en.setMyPK(nodeID + "_" + workid + "_" + str);
			en.setEmpNo(str);
			en.setNodeID(nodeID);

			en.setWorkID(workid);
			en.setRec(WebUser.getNo());
			en.setItIsRemember(isNextTime);
			try {
				en.Insert();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	/**
	 发送到节点

	 @param flowNo
	 @param nodeID
	 @param workid
	 @param fid
	 @param toNodeID
	 @param toEmps
	 @param isRememberMe
	*/
	public static SendReturnObjs WorkOpt_SendToEmps(String flowNo, int nodeID, long workid, long fid, int toNodeID, String toEmps, boolean isRememberMe) throws Exception {
		WorkOpt_SetAccepter(toNodeID, workid, fid, toEmps, isRememberMe);

		Node nd = new Node(nodeID);
		Work wk = nd.getHisWork();
		wk.setOID(workid);
		wk.Retrieve();

		// 以下代码是从 MyFlow.htm Send 方法copy 过来的，需要保持业务逻辑的一致性，所以代码需要保持一致.
		WorkNode firstwn = new WorkNode(wk, nd);
		String msg = "";
		SendReturnObjs objs = firstwn.NodeSend();
		return objs;
	}

		///#endregion


		///#region 表单：
	/**
	 附件上传.

	 @param FileByte
	 @param fileName
	 @return
	*/
	public static String UploadFile(byte[] FileByte, String fileName) throws IOException {
		String path = SystemConfig.getHostURLOfBS() + "/DataUser/UploadFile";
		if (!(new File(path)).isDirectory())
		{
			(new File(path)).mkdirs();
		}

		String filePath = path + "/" + fileName;
		if ((new File(filePath)).isFile())
		{
			(new File(filePath)).delete();
		}

		//这里使用绝对路径来索引
		FileOutputStream stream = new FileOutputStream(filePath);
		stream.write(FileByte, 0, FileByte.length);
		stream.close();

		return filePath;
	}
	/**
	 把表单生成pdf文件.

	 @param workID 工作ID
	 @param filePath 要生成的文件路径，全名.
	 @return
	*/
	public static String Frm_BuliderPDFMergeForFromTree(long workID, int nodeId, String filePath, String name)
	{
		try
		{
			if (nodeId == 0)
			{
				GenerWorkFlow gwf = new GenerWorkFlow(workID);
				nodeId = gwf.getNodeID();
			}
			Node nd = new Node(nodeId);
			bp.wf.MakeForm2Html.MakeCCFormToPDF(nd, workID, nd.getFlowNo(), name, filePath, "");

			return "执行成功.";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}

		///#endregion


		///#region 调度相关的操作.
	/**
	 更新时间状态, 交付给 huangzhimin.
	 作用：按照当前的时间，每天两次更新WF_GenerWorkFlow 的 TodoSta 状态字段。
	 该字段： 0=正常(绿牌), 1=预警(黄牌), 2=逾期(红牌), 3=按时完成(绿牌) , 4=逾期完成(红牌).
	 该方法作用是，每天，中午时间段，与下午时间段，执行更新这两个状态，仅仅更新两次。
	*/
	public static void DTS_GenerWorkFlowTodoSta() throws Exception {
		// 中午的更新, 与发送邮件通知.
		boolean isPM = false;


			///#region 求出是否可以更新状态.
		if (LocalDateTime.now().getHour() >= 9 && LocalDateTime.now().getHour() < 12)
		{
			isPM = true;
			String timeKey = "DTSTodoStaPM" + DataType.getCurrentDateByFormart("yyMMdd");
			Paras ps = new Paras();
			ps.SQL = "SELECT Val FROM Sys_GloVar WHERE No='" + timeKey + "'";
			String time = DBAccess.RunSQLReturnStringIsNull(ps, null);
			if (time == null)
			{
				GloVar var = new GloVar();
				var.setNo(timeKey);
				var.setName("时效调度 WFTodoSta PM 调度");
				var.setGroupKey("WF");
				var.setVal(timeKey); //更新时间点.
				var.setNote("时效调度PM" + timeKey);
				var.Insert();
				time = var.getVal();
			}
			else
			{
				/*如果有数据，就返回，说明已经执行过了。*/
				return;
			}
		}

		//下午时间段.
		if (LocalDateTime.now().getHour() >= 13 && LocalDateTime.now().getHour() < 18)
		{
			String timeKey = "DTSTodoStaAM" + DataType.getCurrentDateByFormart("yyMMdd");
			Paras ps = new Paras();
			ps.SQL = "SELECT Val FROM Sys_GloVar WHERE No='" + timeKey + "'";
			String time = null;
			try {
				time = DBAccess.RunSQLReturnStringIsNull(ps, null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (time == null)
			{
				GloVar var = new GloVar();
				var.setNo(timeKey);
				var.setName("时效调度 WFTodoSta AM 调度");
				var.setGroupKey("WF");
				var.setVal(timeKey); //更新时间点.
				var.setNote("时效调度AM" + timeKey);
				var.Insert();
				time = var.getVal();
			}
			else
			{
				/*如果有数据，就返回，说明已经执行过了。*/
				return;
			}
		}

			///#endregion 求出是否可以更新状态.


		bp.wf.dts.DTS_GenerWorkFlowTodoSta en = new bp.wf.dts.DTS_GenerWorkFlowTodoSta();
		en.Do();

	}
	/**
	 预警与逾期的提醒.
	*/
	private static void DTS_SendMsgToWorker() throws Exception {

			///#region 处理预警的消息发送.
		if (LocalDateTime.now().getHour() >= 0 && LocalDateTime.now().getHour() < 12)
		{
			String timeKey = "DTSWarningPM" + DataType.getCurrentDateByFormart("yyMMdd");
			Paras ps = new Paras();
			ps.SQL = "SELECT Val FROM Sys_GloVar WHERE No='" + timeKey + "'";
			String time = DBAccess.RunSQLReturnStringIsNull(ps, null);
			if (time != null)
			{
				return;
			}

			bp.wf.dts.DTS_SendMsgToWarningWorker en = new bp.wf.dts.DTS_SendMsgToWarningWorker();
			en.Do();

		}

			///#endregion
	}
	/**
	 生成工作的 TimeSpan
	*/
	public static void DTS_GenerWorkFlowTimeSpan() throws Exception {
		Date date = new Date();
		int hour = DateUtils.getHour(date);
		if (hour >= 8 && hour < 10 && DataType.getCurrentWeek() == DayOfWeek.MONDAY.getValue())
		{
			String timeKey = "DTSTimeSpanPM" + DataType.getCurrentDateByFormart("yyMMdd");
			Paras ps = new Paras();
			ps.SQL = "SELECT Val FROM Sys_GloVar WHERE No='" + timeKey + "'";
			String time = DBAccess.RunSQLReturnStringIsNull(ps, null);
			if (time == null)
			{
				GloVar var = new GloVar();
				var.setNo(timeKey);
				var.setName("设置时间段" + timeKey + "一周执行一次.");
				var.setGroupKey("WF");
				var.setVal(timeKey); //更新时间点.
				var.setNote("设置时间段PM" + timeKey);
				var.Insert();
			}
			else
			{
				return;
			}
		}
		//执行调度.
		bp.wf.dts.DTS_GenerWorkFlowTimeSpan ts = new bp.wf.dts.DTS_GenerWorkFlowTimeSpan();
		ts.Do();
	}

	/**
	 根据WorkID获取根节点的WorkID

	 @param workId
	 @return
	*/
	public static long GetRootWorkIDBySQL(long workId, long pworkid) throws Exception {
		if (pworkid == 0)
		{
			return workId;
		}
		GenerWorkFlow gwf = new GenerWorkFlow(pworkid);
		if (gwf.getPWorkID() == 0)
		{
			return pworkid;
		}
		gwf = new GenerWorkFlow(gwf.getPWorkID());
		if (gwf.getPWorkID() == 0)
		{
			return gwf.getWorkID();
		}
		return gwf.getPWorkID();


	}


		///#endregion
	/**
	 根据当前的WorkID递归获取它的子级及父级

	 @param workid
	 @param workids
	 @return
	*/
	public static String GetParentChildWorkID(long workid, String workids) throws Exception {
		//加上当前workid
		workids += workid + ",";
		String sql = "";
		//递归获取该流程的父级WorkID;
		GenerWorkFlow gwf = new GenerWorkFlow(workid);

		//获取子级
		sql = "SELECT WORKID FROM WF_GenerWorkFlow Where PWorkID = " + workid;
		String vals = DBAccess.RunSQLReturnStringIsNull(sql, "");
		if (DataType.IsNullOrEmpty(vals) == true)
		{
			return workids;
		}
		else
		{
			String[] strs = vals.split("[,]", -1);
			for (String str : strs)
			{
				workids = GetParentChildWorkID(Long.parseLong(str), workids);
			}
		}

		if (gwf.getPWorkID() == 0)
		{
			return workids;
		}

		//获取他的父级
		sql = "SELECT PWorkID FROM WF_GenerWorkFlow WHERE WorkID = " + gwf.getPWorkID();
		long pworkid = DBAccess.RunSQLReturnValInt(sql);
		if (pworkid == 0)
		{
			return workids;
		}
		GetParentChildWorkID(pworkid, workids);

		return workids;
	}
	/**
	 求出WhoIsPK的

	 @param workid
	 @param pworkid
	 @param fid 值
	 @return PKVAl
	*/
	public static String GetAthRefPKVal(long workid, long pworkid, long fid, int fk_node, String fk_mapData, FrmAttachment athDesc) throws Exception {
		long pkval = 0;
		if (fk_node == 0 || fk_node == 9999)
		{
			if (workid != 0)
			{
				return String.valueOf(workid);
			}
			return "0";

		}

		AthCtrlWay athCtrlWay = athDesc.getHisCtrlWay();
		Node nd = new Node(fk_node);
		//表单方案
		FrmNode fn = new FrmNode(fk_node, fk_mapData);
		//树形表单
		if (nd.getHisFormType() == NodeFormType.SheetTree)
		{
			athCtrlWay = AthCtrlWay.WorkID;
		}
		//单表单
		else if (nd.getHisFormType() == NodeFormType.RefOneFrmTree)
		{
			switch (fn.getWhoIsPK())
			{
				case OID:
					athCtrlWay = AthCtrlWay.WorkID;
					break;
				case FID:
					athCtrlWay = AthCtrlWay.FID;
					break;
				case PWorkID:
					athCtrlWay = AthCtrlWay.PWorkID;
					break;
				case P2WorkID:
					athCtrlWay = AthCtrlWay.P2WorkID;
					break;
				case P3WorkID:
					athCtrlWay = AthCtrlWay.P3WorkID;
					break;
				case RootFlowWorkID:
					athCtrlWay = AthCtrlWay.RootFlowWorkID;
					break;
				default:
					athCtrlWay = athDesc.getHisCtrlWay();
					break;
			}

		}

		//根据控制权限获取RefPK的值
		if (athCtrlWay == AthCtrlWay.WorkID || athCtrlWay == AthCtrlWay.PK)
		{
			if(nd.getItIsSubThread()==false && fid!=0)
				pkval = fid;
			else
				pkval = workid;
		}
		if (athCtrlWay == AthCtrlWay.FID)
		{
			if (nd.getItIsSubThread() == true)
				pkval = fid;
			else
				pkval = workid;
		}

		//如果是父流程的数据. @lizhen
		if (athCtrlWay == AthCtrlWay.PWorkID)
		{
			if (pworkid == 0)
			{
				pworkid = DBAccess.RunSQLReturnValInt("SELECT PWorkID FROM WF_GenerWorkFlow WHERE WorkID=" + workid, 0);
			}
			pkval = pworkid;
		}

		if (athCtrlWay == AthCtrlWay.P2WorkID)
		{
			//根据流程的PWorkID获取他的爷爷流程
			pkval = DBAccess.RunSQLReturnValInt("SELECT PWorkID FROM WF_GenerWorkFlow WHERE WorkID=" + pworkid, 0);
		}
		if (athCtrlWay == AthCtrlWay.P3WorkID)
		{
			String sql = "Select PWorkID From WF_GenerWorkFlow Where WorkID=(Select PWorkID From WF_GenerWorkFlow Where WorkID=" + pworkid + ")";
			//根据流程的PWorkID获取他的P2流程
			pkval = DBAccess.RunSQLReturnValInt(sql, 0);
		}
		if (athCtrlWay == AthCtrlWay.RootFlowWorkID)
		{
			pkval = bp.wf.Dev2Interface.GetRootWorkIDBySQL(workid, pworkid);
		}
		return String.valueOf(pkval);
	}
	/**
	 获取当前从表关联流程实例的外键值 RefPKVal

	 @param workid
	 @param pworkid
	 @param fid
	 @param fk_node
	 @param fk_mapData
	 @param mapDtl
	 @return
	*/
	public static String GetDtlRefPKVal(long workid, long pworkid, long fid, int fk_node, String fk_mapData, MapDtl mapDtl) throws Exception {
		long pkval = 0;
		if (fk_node == 0 || fk_node == 9999)
		{
			return "0";
		}

		DtlOpenType dtlOpenType = mapDtl.getDtlOpenType();
		Node nd = new Node(fk_node);
		//表单方案
		FrmNode fn = new FrmNode(fk_node, fk_mapData);
		//树形表单
		if (nd.getHisFormType() == NodeFormType.SheetTree)
		{
			dtlOpenType = DtlOpenType.ForWorkID;
		}
		//单表单
		else if (nd.getHisFormType() == NodeFormType.RefOneFrmTree)
		{
			switch (fn.getWhoIsPK())
			{
				case OID:
					dtlOpenType = DtlOpenType.ForWorkID;
					break;
				case FID:
					dtlOpenType = DtlOpenType.ForFID;
					break;
				case PWorkID:
					dtlOpenType = DtlOpenType.ForPWorkID;
					break;
				case P2WorkID:
					dtlOpenType = DtlOpenType.ForP2WorkID;
					break;
				case P3WorkID:
					dtlOpenType = DtlOpenType.ForP3WorkID;
					break;
				case RootFlowWorkID:
					dtlOpenType = DtlOpenType.RootFlowWorkID;
					break;
				default:
					dtlOpenType = mapDtl.getDtlOpenType();
					break;
			}
		}

		//根据控制权限获取RefPK的值
		if (dtlOpenType == DtlOpenType.ForWorkID)
		{
			if(nd.getItIsSubThread()==false && fid!=0)
				pkval = fid;
			else
				pkval = workid;
		}
		if (dtlOpenType == DtlOpenType.ForFID)
		{
			if (nd.getItIsSubThread() == true)
			{
				pkval = fid;
			}
			else
			{
				pkval = workid;
			}
		}
		if (dtlOpenType == DtlOpenType.ForPWorkID)
		{
			if (pworkid != 0)
				pkval = pworkid;
			else
				pkval = workid;
		}

		if (dtlOpenType == DtlOpenType.ForP2WorkID)
		{
			if (pworkid != 0)
			{
				pkval = pworkid;
			}
		}

		if (dtlOpenType == DtlOpenType.ForP2WorkID)
		{
			//根据流程的PWorkID获取他的爷爷流程
			pkval = DBAccess.RunSQLReturnValInt("SELECT PWorkID FROM WF_GenerWorkFlow WHERE WorkID=" + pworkid, 0);
		}
		if (dtlOpenType == DtlOpenType.ForP3WorkID)
		{
			String sql = "SELECT PWorkID FROM WF_GenerWorkFlow WHERE WorkID=(Select PWorkID From WF_GenerWorkFlow Where WorkID=" + pworkid + ")";
			//根据流程的PWorkID获取他的P2流程
			pkval = DBAccess.RunSQLReturnValInt(sql, 0);
		}
		if (dtlOpenType == DtlOpenType.RootFlowWorkID)
		{
			if (fid != 0)
			{
				pkval = bp.wf.Dev2Interface.GetRootWorkIDBySQL(fid, pworkid);
			}
			else
			{
				pkval = bp.wf.Dev2Interface.GetRootWorkIDBySQL(workid, pworkid);
			}
		}
		return String.valueOf(pkval);
	}
	/**
	 保存开发者表单的数据

	 @param htmlCode
	 @param fk_mapData
	 @return
	*/
	public static String SaveDevelopForm(String htmlCode, String fk_mapData) throws Exception {
		//保存到DataUser/CCForm/HtmlTemplateFile/文件夹下
		String filePath = SystemConfig.getPathOfDataUser() + "CCForm/HtmlTemplateFile/";
		if ((new File(filePath)).isDirectory() == false)
		{
			(new File(filePath)).mkdirs();
		}

		filePath = filePath + fk_mapData + ".htm";
		//写入到html 中
		DataType.WriteFile(filePath, htmlCode);

		//保存类型。
		MapData md = new MapData(fk_mapData);
		if (md.getHisFrmType() != FrmType.Develop)
		{
			md.setHisFrmType(FrmType.Develop);
			md.Update();
		}
		// HtmlTemplateFile 保存到数据库中
		DBAccess.SaveBigTextToDB(htmlCode, "Sys_MapData", "No", fk_mapData, "HtmlTemplateFile");

		//检查数据完整性
		GEEntity en = new GEEntity(fk_mapData);
		en.CheckPhysicsTable();
		return "保存成功";
	}
	/**
	 根据根节点获取子节点集合的SQL语句

	 @param paretNo
	 @param ptable
	 @return
	 @exception Exception
	*/
	public static String GetDeptNoSQLByParentNo(String paretNo, String ptable)
	{
		switch (SystemConfig.getAppCenterDBType())
		{
			case MySQL:
			case GBASE8CByMySQL:
			case GBASE8A:
				return "SELECT No FROM(SELECT * FROM " + ptable + " WHERE parentNo IS NOT NULL) au," + " (SELECT @pid:= '" + paretNo + "') pd" + " WHERE FIND_IN_SET(parentNo, @pid) > 0" + " AND @pid := concat(@pid, ',', No)" + " union select No from " + ptable + " where No = '" + paretNo + "'; ";
			case MSSQL:
				return "WITH allsub(No,Name,ParentNo) as (" + " SELECT No, Name, ParentNo FROM " + ptable + " where No = '" + paretNo + "'" + " UNION ALL SELECT a.No,a.Name,a.ParentNo FROM " + ptable + " a, allsub b where a.ParentNo = b.No" + " )" + " SELECT No FROM allsub";

			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				return "SELECT D.No FROM " + ptable + " D start with D.No='" + paretNo + "' connect by prior D.setNo(D.ParentNo";
			case UX:
				return "";
			case DM:
				return "";
			case PostgreSQL:
			case HGDB:
				return "";
			default:
				throw new RuntimeException(SystemConfig.getAppCenterDBType() + "的数据库还没有解析根据父节点获取子级的SQL");

		}
	}
	/**
	 根据当前节点编号获取所有父级的信息

	 @param no
	 @param ptable
	 @param orgNo
	 @return
	 @exception Exception
	*/
	public static String GetParentNameByCurrNo(String no, String ptable, String orgNo)
	{
		String sql = "";

		switch (SystemConfig.getAppCenterDBType())
		{
			case MySQL:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql = "SELECT GROUP_CONCAT(t.Name separator'/') FROM (" + " SELECT @No idlist," + " (SELECT @No:= GROUP_CONCAT(ParentNo separator ',') FROM " + ptable + " WHERE FIND_IN_SET(No, @No)) sub" + " FROM " + ptable + ",(SELECT @No:='" + no + "') vars" + " WHERE @No is not null ) tl," + ptable + " t";
				if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
				{
					sql += " WHERE FIND_IN_SET(t.No, tl.idlist)";
				}
				else
				{
					sql += " WHERE FIND_IN_SET(t.No, tl.idlist) AND t.OrgNo ='" + orgNo + "'";
				}
				break;
			case MSSQL:
				if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
				{
					sql = "WITH allsub(No,Name,ParentNo) as (" + " SELECT No, Name, ParentNo FROM " + ptable + " WHERE No='" + no + "'" + "  UNION ALL SELECT a.No,a.Name,a.ParentNo FROM " + ptable + " a, allsub b WHERE a.setNo(b.ParentNo)" + " SELECT name +'/' FROM allsub FOR XML PATH(''); ";
				}
				else
				{
					sql = "WITH allsub(No,Name,ParentNo,OrgNo) as (" + " SELECT No, Name, ParentNo,OrgNo FROM " + ptable + " WHERE No='" + no + "'" + "  UNION ALL SELECT a.No,a.Name,a.ParentNo,a.OrgNo FROM " + ptable + " a, allsub b WHERE a.setNo(b.ParentNo)" + " SELECT name +'/' FROM allsub WHERE OrgNo!='" + orgNo + "'  FOR XML PATH(''); ";
				}
				break;
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
				{
					sql = "SELECT  replace(wm_concat(D.Name),',','/') FROM " + ptable + " D START WITH D.No='" + no + "' connect by prior D.ParentNo = D.No;";
				}
				else
				{
					sql = "SELECT  replace(wm_concat(D.Name),',','/') FROM " + ptable + " D START WITH D.No='" + no + "' connect by prior D.ParentNo = D.No AND D.OrgNo='" + orgNo + "';";
				}
				break;
			case UX:
			case DM:
			case PostgreSQL:
			case HGDB:
			default:
				throw new RuntimeException(SystemConfig.getAppCenterDBType() + "的数据库还没有解析根据父节点获取子级的SQL");
		}

		return DBAccess.RunSQLReturnStringIsNull(sql, "");
	}

	/**
	 * 根据OrgNo获取组织可以发起的流程信息
	 * @param orgNO
	 * @return
	 */
	public static DataTable DB_StartByOrg(String orgNO)
	{
		String sql = "SELECT No,Name FROM  WF_Flow  WHERE OrgNo='" + orgNO + "' ORDER BY Idx";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return dt;
	}
	/**
	 * 获得指定流程的节点信息.
	 * @param flowNo  流程ID
	 * @param orgNo  组织ID
	 * @return
	 */
	public static DataTable Flow_InfoByOrg(String flowNo, String orgNo)
	{
		String sql = "SELECT A.nodeid,A.mark,A.name FROM WF_Node A, WF_Flow B  " +
				"WHERE B.NO='"+flowNo+"' AND A.FK_Flow=B.No  AND B.OrgNo='" + orgNo + "' ORDER BY  A.nodeid";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return dt;
	}

	/**
	 * 根据节点ID获得该节点可退回的节点信息.
	 * @param nodeId  节点ID
	 * @return
	 */
	public static Hashtable Node_ReturnInfo(int nodeId) throws Exception {
		//returnrole 退回规则,枚举类型:0 不能退回;1 只能退回上一个节点;2 可以退回任意节点;3 退回指定的节点;
		//isbacktracking 是否可以原路返回,枚举类型:0 不允许原路返回;1 由退回人决定;2 强制原路返回;
		//String sql = "SELECT isbacktracking,returnrole, FROM WF_Node WHERE nodeid='" + nodeId + "'";
		Node nd = new Node(nodeId);
		String isbacktracking = (String) nd.GetValStrByKey(NodeAttr.IsBackTracking);
		ReturnRole returnrole = nd.getHisReturnRole();
		DataTable dt = new DataTable();

		if(returnrole == ReturnRole.ReturnSpecifiedNodes){
			String sql="select nodeid,name,mark from wf_node where nodeid in (" +
					"select B.returnto from wf_node A ,wf_nodereturn B where A.nodeid=B.fk_node and B.fk_node='"+nodeId+"') order by nodeid";
			dt = DBAccess.RunSQLReturnTable(sql);
			//vue3的情况获取
			if(dt == null || dt.Rows.size()==0){
				String returnNodes = nd.GetParaString("ReturnNodes");

				if (DataType.IsNullOrEmpty(returnNodes) == true)
					throw new RuntimeException("@流程设计错误，您设置该节点可以退回指定的节点，但是指定的节点集合为空，请在节点属性设置它的制订节点。");
				String[] strs = returnNodes.split(",");
				NodeReturn rnd = null;
				DataRow dw2=null;
				for (int i = 0; i < strs.length; i++)
				{
					if (DataType.IsNullOrEmpty(strs[i]) == true)
						continue;
					dw2 = dt.NewRow();
					Node ndi = new Node(strs[i]);
					dw2.setValue("nodeid",ndi.getNodeID());
					dw2.setValue("name",ndi.getName());
					dw2.setValue("mark",ndi.getMark());
					dt.Rows.add(dw2);
				}
			}
		}else {
			Node nd2 = new Node(Integer.valueOf(nd.getFlowNo()+"01"));
			DataRow dw = dt.NewRow();
			dw.setValue("nodeid",nd2.getNodeID());
			dw.setValue("name",nd2.getName());
			dw.setValue("mark",nd2.getMark());
			dt.Rows.add(dw);
		}

		Hashtable ht = new Hashtable();
		ht.put("returnrole", returnrole.getValue());
		ht.put("isbacktracking", isbacktracking);
		ht.put("returnnode", dt.Rows);
		return ht;
	}
	public static String GenerateTokenToThird(String userNo,String logDev) throws Exception {
		//单点模式,严格模式.
		if (SystemConfig.getTokenModel() == 1)
			return Port_GenerToken_2021(userNo, logDev, 0, false);

		//记录token.
		bp.port.Token tk = new Token();
		tk.setMyPK(DBAccess.GenerGUID());

		tk.setEmpName(bp.web.WebUser.getNo());
		tk.setEmpName(bp.web.WebUser.getName());

		tk.setDeptNo(WebUser.getDeptNo());
		tk.setDeptName(WebUser.getDeptName());

		tk.setOrgNo(bp.web.WebUser.getOrgNo());
		tk.setOrgName(bp.web.WebUser.getOrgName());
		tk.setRDT(DataType.getCurrentDateTime()); //记录日期.

		if (logDev.equals("PC"))
		{
			tk.setSheBei(0);
		}
		else
		{
			tk.setSheBei(1);
		}
		tk.Insert();
		WebUser.setToken(tk.getMyPK());

		return tk.getMyPK();
	}

	public static boolean Flow_CheckFlowIsOver(long workID) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		if (gwf.getIsOver()==true)
			return true;

		WorkNode wn = new WorkNode(workID, gwf.getNodeID());
		wn.CheckFrm1ToN();
		return wn.Func_CheckCompleteCondition();
	}
	/**
	 * 计算未来流程运行路线
	 * @param WorkID
	 * @param FlowNo
	 * @param NodeID
	 * @return
	 * @throws Exception
	 */
	public static Nodes GetPreplaceWokerFreeNodes(long WorkID, String FlowNo, int NodeID) throws Exception{
		Nodes nodes = new Nodes();
		Node node = new Node(NodeID);
		nodes = NodeSend_GenerNextStepNode_Ext(node, nodes, FlowNo, WorkID);
		return nodes;
	}
	public static Nodes NodeSend_GenerNextStepNode_Ext(Node node, Nodes nodes, String FlowNo, long WorkID) throws Exception{
		Directions dirs = new Directions();
		dirs.Retrieve(DirectionAttr.Node, node.getNodeID(), "Idx");
		if(dirs.size()==1){
			Direction d=(Direction)dirs.get(0);
			Node nd=new Node(d.getToNode());
			nodes.add(nd); //把他加入到里面.
			return NodeSend_GenerNextStepNode_Ext(nd,nodes,FlowNo,WorkID);
		}
		else{
			for (Direction dir: dirs.ToJavaList()){
				//查询出来他的条件.
				Conds conds = new Conds();
				conds.Retrieve(CondAttr.FK_Node, node.getNodeID(), CondAttr.ToNodeID, dir.getToNode(), CondAttr.CondType, CondType.Dir.getValue(), CondAttr.Idx);

				if(conds.size()==0)
					continue;
				//可以到达的节点.
				if (conds.size() == 1&&node.getHisDeliveryWay()==DeliveryWay.PreplaceWokerFree)
				{
					Node nd=new Node(dir.getToNode());
					Hashtable ht=new Hashtable();
					nodes.add(nd);
					continue;
				}

				WebUserCopy webUserCopy=new WebUserCopy();
				webUserCopy.setNo(WebUser.getNo());
				webUserCopy.setName(WebUser.getName());
				webUserCopy.setDeptNo(WebUser.getDeptNo());

				GERpt geRpt=new GERpt("ND"+Integer.parseInt(FlowNo)+"Rpt",WorkID);
				//按条件计算.
				if (conds.GenerResult(geRpt, webUserCopy,node) == true)
				{
					node = new Node(dir.getToNode());
					if (node.getHisNodeType() == NodeType.UserNode)
					{
						Node nd=new Node(dir.getToNode());
						nodes.add(nd);
						return NodeSend_GenerNextStepNode_Ext(nd,nodes,FlowNo,WorkID);
					}
				}
			}
		}
		return nodes;
	}
}
