package bp.sys;
/** 
 事件标记列表
*/
public class EventListFlow
{

		///#region 流程事件
	/** 
	 当创建workid的时候.
	*/
	public static final String FlowOnCreateWorkID = "FlowOnCreateWorkID";
	/** 
	 流程完成时.
	*/
	public static final String FlowOverBefore = "FlowOverBefore";
	/** 
	 结束后.
	*/
	public static final String FlowOverAfter = "FlowOverAfter";
	/** 
	 流程删除前
	*/
	public static final String BeforeFlowDel = "BeforeFlowDel";
	/** 
	 流程删除后
	*/
	public static final String AfterFlowDel = "AfterFlowDel";

		///#endregion 流程事件
}
