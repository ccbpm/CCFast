package bp.wf;

/** 
 流程删除日志
*/
public class WorkFlowDeleteLogAttr
{

		///#region 基本属性
	/** 
	 工作ID
	*/
	public static final String OID = "OID";
	/** 
	 流程编号
	*/
	public static final String FK_Flow = "FK_Flow";
	/** 
	 流程类别
	*/
	public static final String FK_FlowSort = "FK_FlowSort";
	/** 
	 删除人员
	*/
	public static final String Oper = "Oper";
	/** 
	 删除原因
	*/
	public static final String DeleteNote = "DeleteNote";
	/** 
	 删除日期
	*/
	public static final String DeleteDT = "DeleteDT";
	/** 
	 删除人员
	*/
	public static final String OperDept = "OperDept";
	/** 
	 删除人员名称
	*/
	public static final String OperDeptName = "OperDeptName";
	/** 
	 删除节点节点
	*/
	public static final String DeleteNode = "DeleteNode";
	/** 
	 删除节点节点名称
	*/
	public static final String DeleteNodeName = "DeleteNodeName";
	/** 
	 删除节点后是否需要原路返回？
	*/
	public static final String IsBackTracking = "IsBackTracking";

		///#endregion


		///#region 流程属性
	/** 
	 标题
	*/
	public static final String Title = "Title";
	/** 
	 参与人员
	*/
	public static final String FlowEmps = "FlowEmps";
	/** 
	 流程ID
	*/
	public static final String FID = "FID";
	/** 
	 发起年月
	*/
	public static final String FK_NY = "FK_NY";
	/** 
	 发起人ID
	*/
	public static final String FlowStarter = "FlowStarter";
	/** 
	 发起日期
	*/
	public static final String FlowStartDeleteDT = "FlowStartDeleteDT";
	/** 
	 发起人部门ID
	*/
	public static final String FK_Dept = "FK_Dept";
	/** 
	 结束人
	*/
	public static final String FlowEnder = "FlowEnder";
	/** 
	 最后活动日期
	*/
	public static final String FlowEnderDeleteDT = "FlowEnderDeleteDT";
	/** 
	 跨度
	*/
	public static final String FlowDaySpan = "FlowDaySpan";
	/** 
	 结束节点
	*/
	public static final String FlowEndNode = "FlowEndNode";
	/** 
	 父流程WorkID
	*/
	public static final String PWorkID = "PWorkID";
	/** 
	 父流程编号
	*/
	public static final String PFlowNo = "PFlowNo";

		///#endregion
}
