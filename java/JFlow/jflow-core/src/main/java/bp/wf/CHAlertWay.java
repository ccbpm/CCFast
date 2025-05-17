package bp.wf;


/** 
 工作提醒方式
*/
public enum CHAlertWay
{
	/** 
	 邮件
	*/
	ByEmail,
	/** 
	 短消息
	*/
	BySMS,
	/** 
	 即时通讯
	*/
	ByCCIM;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static CHAlertWay forValue(int value)
	{
		return values()[value];
	}
}
