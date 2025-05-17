package bp.wf;

public enum GuestFlowRole
{
	/** 
	 不参与
	*/
	None,
	/** 
	 开始节点参与
	*/
	StartNodeJoin,
	/** 
	 中间节点参与
	*/
	MiddleNodeJoin;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static GuestFlowRole forValue(int value)
	{
		return values()[value];
	}
}
