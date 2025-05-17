package bp.wf;
/** 
 根据子流程运行模式控制父流程的运行到下一个节点/结束
*/
public enum SubFlowRunModel
{
	/** 
	 无，不设置
	*/
	None,
	/** 
	 子流程结束
	*/
	FlowOver,
	/** 
	 子流程运行到指定节点
	*/
	SpecifiedNodes;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static SubFlowRunModel forValue(int value)
	{
		return values()[value];
	}
}
