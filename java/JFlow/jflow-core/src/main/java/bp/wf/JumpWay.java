package bp.wf;

/** 
 节点工作退回规则
*/
public enum JumpWay
{
	/** 
	 不能跳转
	*/
	CanNotJump,
	/** 
	 向后跳转
	*/
	Next,
	/** 
	 向前跳转
	*/
	Previous,
	/** 
	 任何节点
	*/
	AnyNode,
	/** 
	 任意点
	*/
	JumpSpecifiedNodes;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static JumpWay forValue(int value)
	{
		return values()[value];
	}
}
