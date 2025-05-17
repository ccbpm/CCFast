package bp.wf;

/** 
 消息类型
*/
public enum SendReturnMsgType
{
	/** 
	 消息
	*/
	Info,
	/** 
	 系统消息
	*/
	SystemMsg;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static SendReturnMsgType forValue(int value)
	{
		return values()[value];
	}
}
