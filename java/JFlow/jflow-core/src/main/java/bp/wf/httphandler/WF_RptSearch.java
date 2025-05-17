package bp.wf.httphandler;

import bp.da.*;
import bp.difference.handler.DirectoryPageBase;
import bp.sys.*;
import bp.web.*;
import bp.difference.*;
import bp.wf.*;
import bp.wf.Glo;

import java.util.*;

/** 
 页面功能实体
*/
public class WF_RptSearch extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_RptSearch() throws Exception {
	}


		///#region 流程分布.
	public final String DistributedOfMy_Init() throws Exception {
		DataSet ds = new DataSet();

		//我发起的流程.
		Paras ps = new Paras();
		ps.SQL = "select FK_Flow, FlowName,Count(WorkID) as Num FROM WF_GenerWorkFlow  WHERE WFState >1 And Starter=" + SystemConfig.getAppCenterDBVarStr() + "Starter GROUP BY FK_Flow, FlowName ";
		ps.Add("Starter", WebUser.getNo(), false);

		//String sql = "";
		//sql = "select FK_Flow, FlowName,Count(WorkID) as Num FROM WF_GenerWorkFlow  WHERE Starter='" + bp.web.WebUser.getNo() + "' GROUP BY FK_Flow, FlowName ";
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		dt.TableName = "Start";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).setColumnName("FK_Flow");
			dt.Columns.get(1).setColumnName("FlowName");
			dt.Columns.get(2).setColumnName("Num");
		}
		ds.Tables.add(dt);

		//待办.
		ps = new Paras();
		ps.SQL = "select FK_Flow, FlowName,Count(WorkID) as Num FROM wf_empworks  WHERE FK_Emp=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp GROUP BY FK_Flow, FlowName ";
		ps.Add("FK_Emp", WebUser.getNo(), false);
		//sql = "select FK_Flow, FlowName,Count(WorkID) as Num FROM wf_empworks  WHERE FK_Emp='" + bp.web.WebUser.getNo() + "' GROUP BY FK_Flow, FlowName ";
		DataTable dtTodolist = DBAccess.RunSQLReturnTable(ps);
		dtTodolist.TableName = "Todolist";
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dtTodolist.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dtTodolist.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dtTodolist.Columns.get("NUM").ColumnName = "Num";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dtTodolist.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dtTodolist.Columns.get("flowname").ColumnName = "FlowName";
			dtTodolist.Columns.get("num").ColumnName = "Num";
		}

		ds.Tables.add(dtTodolist);

		//正在运行的流程.
		DataTable dtRuning = Dev2Interface.DB_TongJi_Runing();
		dtRuning.TableName = "Runing";
		ds.Tables.add(dtRuning);

		//归档的流程.
		DataTable dtOK = Dev2Interface.DB_TongJi_FlowComplete();
		dtOK.TableName = "OK";
		ds.Tables.add(dtOK);

		//返回结果.
		return bp.tools.Json.ToJson(ds);
	}

		///#endregion



		///#region 功能列表
	/** 
	 功能列表
	 
	 @return 
	*/
	public final String Default_Init() throws Exception {
		Hashtable ht = new Hashtable();
		ht.put("MyStartFlow", "我发起的流程");
		ht.put("MyJoinFlow", "我审批的流程");



		//   ht.Add("MyDeptFlow", "我本部门发起的流程");
		//  ht.Add("MySubDeptFlow", "我本部门与子部门发起的流程");
		// ht.Add("AdvFlowsSearch", "高级查询");

		return bp.tools.Json.ToJsonEntitiesNoNameMode(ht);
	}

		///#endregion


		///#region 执行父类的重写方法.
	/** 
	 默认执行的方法
	 
	 @return 
	*/
	@Override
	protected String DoDefaultMethod() throws Exception {
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


		///#region KeySearch.htm
	/** 
	 功能列表
	 
	 @return 
	*/
	public final String KeySearch_Query() throws Exception {
		String keywords = this.GetRequestVal("TB_KWds");
		//对输入的关键字进行验证
		keywords = Glo.CheckKeyWord(keywords);
		if (Glo.CheckKeyWordInSql(keywords))
		{
			return "@err:请输入正确字符！";
		}

		Paras ps = new Paras();

		String sql = "";
		sql = "SELECT A.FlowName,A.NodeName,A.FK_Flow,A.FK_Node,A.WorkID,A.FID,A.Title,A.StarterName,A.RDT,A.WFSta,A.Emps, A.TodoEmps, A.WFState " + " FROM WF_GenerWorkFlow A " + " WHERE (A.Title LIKE '%" + keywords + "%' " + " or A.Starter LIKE '%" + keywords + "%' " + " or A.StarterName LIKE '%" + keywords + "%') " + " AND (A.Emps LIKE '@%" + WebUser.getNo() + "%' " + " or A.TodoEmps LIKE '%" + WebUser.getNo() + "%') " + " AND A.WFState!=0 ";

		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			sql += " AND A.OrgNo='" + WebUser.getOrgNo() + "' ";
		}

		ps.SQL = sql;


		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		dt.TableName = "WF_GenerWorkFlow";

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("FID").ColumnName = "FID";
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
			dt.Columns.get("WFSTA").ColumnName = "WFSta";
			dt.Columns.get("EMPS").ColumnName = "Emps";
			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps"; //处理人.
			dt.Columns.get("WFSTATE").ColumnName = "WFState"; //处理人.
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("fid").ColumnName = "FID";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("startername").ColumnName = "StarterName";
			dt.Columns.get("wfsta").ColumnName = "WFSta";
			dt.Columns.get("emps").ColumnName = "Emps";
			dt.Columns.get("todoemps").ColumnName = "TodoEmps"; //处理人.
			dt.Columns.get("wfstate").ColumnName = "WFState"; //处理人.
		}

		if (dt != null)
		{
			dt.Columns.Add("TDTime");
			for (DataRow dr : dt.Rows)
			{

				dr.setValue("TDTime", bp.wf.httphandler.CCMobile.GetTraceNewTime(dr.getValue("FK_Flow").toString(), Integer.parseInt(dr.getValue("WorkID").toString()), Integer.parseInt(dr.getValue("FID").toString())));
			}
		}
		return bp.tools.Json.ToJson(dt);
	}
	/** 
	 判断是否可以执行当前工作？
	 
	 @return 
	*/
	public final String KeySearch_GenerOpenUrl() throws Exception {
		if (Dev2Interface.Flow_IsCanDoCurrentWork(this.getWorkID(), WebUser.getNo()) == true)
		{
			return "1";
		}
		else
		{
			return "0";
		}
	}


		///#endregion

}