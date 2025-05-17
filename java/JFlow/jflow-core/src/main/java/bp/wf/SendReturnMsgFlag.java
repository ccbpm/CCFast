package bp.wf;

/** 
 消息标记
*/
public class SendReturnMsgFlag
{
	/** 
	 符合工作流程完成条件
	*/
	public static final String MacthFlowOver = "MacthFlowOver";
	/** 
	 当前工作[{0}]已经完成
	*/
	public static final String CurrWorkOver = "CurrWorkOver";
	/** 
	 符合完成条件,流程完成
	*/
	public static final String FlowOverByCond = "FlowOverByCond";
	/** 
	 到人员
	*/
	public static final String ToEmps = "ToEmps";
	/** 
	 到人员的扩展信息
	*/
	public static final String ToEmpExt = "ToEmpExt";
	/** 
	 分配任务
	*/
	public static final String AllotTask = "AllotTask";
	/** 
	 合流结束
	*/
	public static final String HeLiuOver = "HeLiuOver";
	/** 
	 工作报告
	*/
	public static final String WorkRpt = "WorkRpt";
	/** 
	 启动节点
	*/
	public static final String WorkStartNode = "WorkStartNode";
	/** 
	 工作启动
	*/
	public static final String WorkStart = "WorkStart";
	/** 
	 流程结束
	*/
	public static final String FlowOver = "FlowOver";
	/** 
	 发送成功后的事件异常
	*/
	public static final String SendSuccessMsgErr = "SendSuccessMsgErr";
	/** 
	 发送成功信息
	*/
	public static final String SendSuccessMsg = "SendSuccessMsg";
	/** 
	 分流程信息
	*/
	public static final String FenLiuInfo = "FenLiuInfo";
	/** 
	 抄送消息
	*/
	public static final String CCMsg = "CCMsg";
	/** 
	 编辑接受者
	*/
	public static final String EditAccepter = "EditAccepter";
	/** 
	 新建流程
	*/
	public static final String NewFlowUnSend = "NewFlowUnSend";
	/** 
	 撤销发送
	*/
	public static final String UnSend = "UnSend";
	/** 
	 报表
	*/
	public static final String Rpt = "Rpt";
	/** 
	 发送时
	*/
	public static final String SendWhen = "SendWhen";
	/** 
	 当前流程结束
	*/
	public static final String End = "End";
	/** 
	 当前流程完成
	*/
	public static final String OverCurr = "OverCurr";
	/** 
	 流程方向信息
	*/
	public static final String CondInfo = "CondInfo";
	/** 
	 一个节点完成
	*/
	public static final String OneNodeSheetver = "OneNodeSheetver";
	/** 
	 单据信息
	*/
	public static final String BillInfo = "BillInfo";
	/** 
	 文本信息(系统不会生成)
	*/
	public static final String MsgOfText = "MsgOfText";
	/** 
	 消息收听信息
	*/
	public static final String ListenInfo = "ListenInfo";
	/** 
	 流程是否结束？
	*/
	public static final String IsStopFlow = "IsStopFlow";


		///#region 系统变量
	/** 
	 工作ID
	*/
	public static final String VarWorkID = "VarWorkID";
	/** 
	 当前节点ID
	*/
	public static final String VarCurrNodeID = "VarCurrNodeID";
	/** 
	 当前节点名称
	*/
	public static final String VarCurrNodeName = "VarCurrNodeName";
	/** 
	 到达节点ID
	*/
	public static final String VarToNodeID = "VarToNodeID";
	/** 
	 到达的节点集合
	*/
	public static final String VarToNodeIDs = "VarToNodeIDs";
	/** 
	 到达节点名称
	*/
	public static final String VarToNodeName = "VarToNodeName";
	/** 
	 接受人集合的名称(用逗号分开)
	*/
	public static final String VarAcceptersName = "VarAcceptersName";
	/** 
	 接受人集合的ID(用逗号分开)
	*/
	public static final String VarAcceptersID = "VarAcceptersID";
	/** 
	 接受人集合的ID Name(用逗号分开)
	*/
	public static final String VarAcceptersNID = "VarAcceptersNID";
	/** 
	 子线程的WorkIDs
	*/
	public static final String VarTreadWorkIDs = "VarTreadWorkIDs";

		///#endregion 系统变量
}
