package bp.wf.httphandler;

import bp.da.*;
import bp.*;
import bp.wf.*;

/** 
 页面功能实体
*/
public class WF_WorkOpt_FlowOperation extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_WorkOpt_FlowOperation()
	{

	}

	public final String FlowTrackDateByWorkID()
	{
		String trackTable = "ND" + Integer.parseInt(this.getFlowNo()) + "Track";
		String sql = "SELECT NDFrom,NDFromT,EmpFrom,EmpFromT,RDT From " + trackTable + " Where WorkID=" + this.getWorkID() + " AND ActionType IN(0,1,6,7,8,11,27) Order By RDT";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase || bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get(0).ColumnName = "NDFrom";
			dt.Columns.get(1).ColumnName = "NDFromT";
			dt.Columns.get(2).ColumnName = "EmpFrom";
			dt.Columns.get(3).ColumnName = "EmpFromT";
			dt.Columns.get(4).ColumnName = "RDT";
		}
		return bp.tools.Json.ToJson(dt);
	}

	/** 
	 流程调整
	 
	 @return 
	*/
	public final String ReSend() throws Exception {
		int toNodeID = this.GetRequestValInt("ToNodeID");
		String toEmps = this.GetRequestVal("Emps");
		String note = this.GetRequestVal("Note");
		return Dev2Interface.Flow_ReSend(this.getWorkID(), toNodeID, toEmps, note);
	}


}
