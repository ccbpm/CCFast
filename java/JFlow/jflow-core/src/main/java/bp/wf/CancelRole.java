package bp.wf;

/** 
 撤销规则
*/
public enum CancelRole
{
	/** 
	 仅上一步
	*/
	OnlyNextStep,
	/** 
	 不能撤销
	*/
	None,
	/** 
	 上一步与开始节点.
	*/
	NextStepAndStartNode,
	/** 
	 可以撤销指定的节点
	*/
	SpecNodes;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static CancelRole forValue(int value)
	{
		return values()[value];
	}
}
