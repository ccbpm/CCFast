package bp.ccbill;

import bp.da.*;
import bp.web.*;
import bp.wf.httphandler.*;
import bp.*;
import java.util.*;

/**
 页面功能实体
*/
public class WF_CCBill_Portal extends bp.difference.handler.DirectoryPageBase
{

		///#region 构造方法.
	/**
	 构造函数
	*/
	public WF_CCBill_Portal()
	{
	}

		///#endregion 构造方法.


		///#region 欢迎页面初始化.
	/**
	 欢迎页面初始化

	 @return
	*/
	public final String Default_Init()
	{
		Hashtable ht = new Hashtable();
		ht.put("FlowNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(No) FROM WF_Flow")); //流程数
		ht.put("NodeNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(NodeID) FROM WF_Node")); //节点数据
		ht.put("FromNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(No) FROM Sys_MapData")); //表单数

		ht.put("FlowInstaceNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState >1 ")); //实例数.
		ht.put("TodolistNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState=2 ")); //待办数
		ht.put("ReturnNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState=5 ")); //退回数.

		ht.put("OverTimeNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState=5 ")); //逾期数.

		return bp.tools.Json.ToJson(ht);
	}
	/**
	 获得数据

	 @return
	*/
	public final String Default_DataSet() throws Exception {
		DataSet ds = new DataSet();

		//月份分组.
		String sql = "SELECT FK_NY, count(WorkID) as Num FROM WF_GenerWorkFlow WHERE WFState >1 GROUP BY FK_NY ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.setTableName("DTNY");
		ds.Tables.add(dt);

		//部门分组.
		sql = "SELECT DeptName, count(WorkID) as Num FROM WF_EmpWorks WHERE WFState >1 GROUP BY DeptName";
		DataTable deptNums = DBAccess.RunSQLReturnTable(sql);
		deptNums.setTableName("DeptNums");
		ds.Tables.add(deptNums);

		//流程分组.
		sql = "SELECT FlowName, count(WorkID) as Num FROM WF_EmpWorks WHERE WFState >1 GROUP BY FlowName";
		DataTable flowNums = DBAccess.RunSQLReturnTable(sql);
		flowNums.setTableName("FlowNums");
		ds.Tables.add(flowNums);
		return bp.tools.Json.ToJson(ds);
	}

		///#endregion 欢迎页面初始化.


		///#region 移动页面.
	/**
	 获取首页中草稿、待办、已完成的数据

	 @return
	*/
	public final String Default_TodoNums() throws Exception {
		Hashtable ht = new Hashtable();

		//我发起的草稿
		ht.put("Todolist_Draft", bp.wf.Dev2Interface.getTodolistDraft());

		////我发起在处理的流程
		ht.put("MyStart_Runing", bp.wf.Dev2Interface.getMyStartRuning());

		////我发起已完成的流程
		ht.put("MyStart_Complete", bp.wf.Dev2Interface.getMyStartComplete());

		//我处理的待办数量
		ht.put("Todolist_EmpWorks", bp.wf.Dev2Interface.getTodolistEmpWorks());

		//抄送待阅数量
		ht.put("CCList_UnRead", bp.wf.Dev2Interface.getCCListUnRead());

		//抄送已阅数量
		ht.put("CCList_Read", bp.wf.Dev2Interface.getCCListRead());

		//待办待阅数量
		ht.put("Todolist_UnRead", bp.wf.Dev2Interface.getTodolistUnRead());

		//待办已阅数量
		ht.put("Todolist_Read", bp.wf.Dev2Interface.getTodolistRead());
		return bp.tools.Json.ToJson(ht);
	}
	/**
	 获得最近使用的流程.

	 @return
	*/
	public final String Default_FlowsNearly()
	{
		String sql = "";
		int top = GetRequestValInt("Top");
		if (top == 0)
		{
			top = 8;
		}

		switch (bp.difference.SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
				sql = " SELECT TOP " + top + " FK_Flow,FlowName,F.Icon FROM WF_GenerWorkFlow G ,WF_Flow F WHERE  F.IsCanStart=1 AND F.No=G.FK_Flow AND Starter='" + WebUser.getNo() + "' GROUP BY FK_Flow,FlowName,ICON ORDER By Max(SendDT) DESC";
				break;
			case MySQL:
			case PostgreSQL:
			case UX:
			case HGDB:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql = " SELECT DISTINCT A.FK_Flow,A.FlowName,B.Icon,A.SendDT  FROM WF_GenerWorkFlow A ,WF_Flow B WHERE B.IsCanStart=1 AND B.No=A.FK_Flow AND A.Starter='" + WebUser.getNo() + "'  Order By A.SendDT  limit " + top;
				break;
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				sql = " SELECT * FROM (SELECT DISTINCT FK_Flow as \"FK_Flow\",FlowName as \"FlowName\",F.Icon ,max(SendDT) SendDT FROM WF_GenerWorkFlow G ,WF_Flow F WHERE F.IsCanStart=1 AND F.No=G.FK_Flow AND Starter='" + WebUser.getNo() + "' GROUP BY FK_Flow,FlowName,ICON Order By SendDT) WHERE  rownum <=" + top;
				break;
			default:
				throw new RuntimeException("err@系统暂时还未开发使用" + bp.difference.SystemConfig.getAppCenterDBType() + "数据库");
		}
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}

	/**
	 常用菜单

	 @return
	*/
	public final String Default_MenusOfFlag()
	{

		String sql = "";
		int top = GetRequestValInt("Top");
		if (top == 0)
		{
			top = 8;
		}

		switch (bp.difference.SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
				sql = " SELECT TOP " + top + "  No,Name,Icon FROM GPM_Menu WHERE  LEN(MenuModel )  >1 ";
				break;
			case MySQL:
			case PostgreSQL:
			case UX:
			case HGDB:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql = " SELECT   No,Name,Icon FROM GPM_Menu WHERE  LEN(MenuModel )  >1 limit " + top;
				break;
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				sql = " SELECT   No,Name,Icon FROM GPM_Menu WHERE  LEN(MenuModel )  >1 rownum " + top;
				break;
			default:
				throw new RuntimeException("err@系统暂时还未开发使用" + bp.difference.SystemConfig.getAppCenterDBType() + "数据库");
		}
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}

	public final String Fast_Mobile_Default_Init11()
	{
		//DataSet ds = new DataSet();
		//ds.Table

		//最新使用的流程.
		String sql = "SELECT ";


		//最常用的菜单.


		//系统



		return "";
	}

		///#endregion

}
