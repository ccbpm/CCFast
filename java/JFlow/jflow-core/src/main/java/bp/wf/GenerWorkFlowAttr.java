package bp.wf;

import bp.en.*;
/** 
 流程实例
*/
public class GenerWorkFlowAttr extends EntityNoNameAttr
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
	 流程状态
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
	 挂起时间
	*/
	public static final String HungupTime = "HungupTime";
	/** 
	 得分
	*/
	public static final String Cent = "Cent";
	/** 
	 当前工作到的节点.
	*/
	public static final String FK_Node = "FK_Node";
	/** 
	 当前工作角色
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
	 系统类别
	*/
	public static final String SysType = "SysType";
	/** 
	 优先级
	*/
	public static final String PRI = "PRI";
	/** 
	 流程应完成时间
	*/
	public static final String SDTOfFlow = "SDTOfFlow";
	/** 
	 流程预警时间
	*/
	public static final String SDTOfFlowWarning = "SDTOfFlowWarning";
	/** 
	 节点应完成时间
	*/
	public static final String SDTOfNode = "SDTOfNode";
	/** 
	 父流程ID
	*/
	public static final String PWorkID = "PWorkID";
	/** 
	 父亲流程的FID
	*/
	public static final String PFID = "PFID";
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
	 单据编号
	*/
	public static final String BillNo = "BillNo";
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
	public static final String FK_NY = "FK_NY";
	/** 
	 周次
	*/
	public static final String WeekNum = "WeekNum";
	/** 
	 发送人
	*/
	public static final String Sender = "Sender";
	/** 
	 发送日期
	*/
	public static final String SendDT = "SendDT";
	/** 
	 时间范围
	*/
	public static final String TSpan = "TSpan";
	/** 
	 待办状态(0=待办中,1=预警中,2=逾期中,3=按期完成,4=逾期完成)
	*/
	public static final String TodoSta = "TodoSta";
	/** 
	 会签状态
	*/
	public static final String HuiQianTaskSta = "HuiQianTaskSta";
	/** 
	 域/系统编号
	*/
	public static final String DomainExt = "DomainExt";

	public static final String PrjNo = "PrjNo";
	public static final String PrjName = "PrjName";

	public static final String OrgNo = "OrgNo";
	public static final String FlowNote = "FlowNote";
	/** 
	 耗时
	*/
	public static final String LostTimeHH = "LostTimeHH";


		///#endregion
}
