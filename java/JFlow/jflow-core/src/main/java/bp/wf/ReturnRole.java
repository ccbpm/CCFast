package bp.wf;


/**
 节点工作退回规则
*/
public enum ReturnRole
{
	/**
	 不能退回
	*/
	CanNotReturn,
	/**
	 只能退回上一个节点
	*/
	ReturnPreviousNode,
	/**
	 可退回以前任意节点(默认)
	*/
	ReturnAnyNodes,
	/**
	 可退回指定的节点
	*/
	ReturnSpecifiedNodes,
	/**
	 * 退回到开始节点
	 */
	ReturnStartNode,
	/**
	 由流程图设计的退回路线来决定
	*/
	ByReturnLine;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ReturnRole forValue(int value)
	{
		return values()[value];
	}
}
