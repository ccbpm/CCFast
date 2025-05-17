package bp.wf.data;

/** 
 我部门的流程
*/
public class MyDeptFlowAttr
{

		///#region 基本属性
	/** 
	 工作ID
	*/
	public static final String WorkID = "WorkID";
	/** 
	 工作流
	*/
	public static final String FK_Flow = "FK_Flow";
	/** 
	 流程状态
	*/
	public static final String WFState = "WFState";
	/** 
	 流程状态(简单)
	*/
	public static final String WFSta = "WFSta";
	/** 
	 标题
	*/
	public static final String Title = "Title";
	/** 
	 发起人
	*/
	public static final String Starter = "Starter";
	/** 
	 产生时间
	*/
	public static final String RDT = "RDT";
	/** 
	 完成时间
	*/
	public static final String CDT = "CDT";
	/** 
	 得分
	*/
	public static final String Cent = "Cent";
	/** 
	 当前工作到的节点.
	*/
	public static final String FK_Node = "FK_Node";
	/** 
	 当前工作岗位
	*/
	public static final String FK_Station = "FK_Station";
	/** 
	 部门
	*/
	public static final String FK_Dept = "FK_Dept";
	/** 
	 流程ID
	*/
	public static final String FID = "FID";
	/** 
	 是否启用
	*/
	public static final String IsEnable = "IsEnable";
	/** 
	 流程名称
	*/
	public static final String FlowName = "FlowName";
	/** 
	 发起人名称
	*/
	public static final String StarterName = "StarterName";
	/** 
	 节点名称
	*/
	public static final String NodeName = "NodeName";
	/** 
	 部门名称
	*/
	public static final String DeptName = "DeptName";
	/** 
	 流程类别
	*/
	public static final String FK_FlowSort = "FK_FlowSort";
	/** 
	 优先级
	*/
	public static final String PRI = "PRI";
	/** 
	 流程应完成时间
	*/
	public static final String SDTOfFlow = "SDTOfFlow";
	/** 
	 节点应完成时间
	*/
	public static final String SDTOfNode = "SDTOfNode";
	/** 
	 父流程ID
	*/
	public static final String PWorkID = "PWorkID";
	/** 
	 父流程编号
	*/
	public static final String PFlowNo = "PFlowNo";
	/** 
	 父流程节点
	*/
	public static final String PNodeID = "PNodeID";
	/** 
	 子流程的调用人.
	*/
	public static final String PEmp = "PEmp";
	/** 
	 客户编号(对于客户发起的流程有效)
	*/
	public static final String GuestNo = "GuestNo";
	/** 
	 客户名称
	*/
	public static final String GuestName = "GuestName";
	/** 
	 时间段
	*/
	public static final String TSpan = "TSpan";
	/** 
	 单据编号
	*/
	public static final String BillNo = "BillNo";
	/** 
	 备注
	*/
	public static final String FlowNote = "FlowNote";
	/** 
	 待办人员
	*/
	public static final String TodoEmps = "TodoEmps";
	/** 
	 待办人员数量
	*/
	public static final String TodoEmpsNum = "TodoEmpsNum";
	/** 
	 任务状态
	*/
	public static final String TaskSta = "TaskSta";
	/** 
	 临时存放的参数
	*/
	public static final String AtPara = "AtPara";
	/** 
	 参与人
	*/
	public static final String Emps = "Emps";
	/** 
	 GUID
	*/
	public static final String GUID = "GUID";

		///#endregion
}