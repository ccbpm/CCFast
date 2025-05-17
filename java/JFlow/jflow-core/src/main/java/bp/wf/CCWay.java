package bp.wf;


/** 
 抄送方式
*/
public enum CCWay
{
	/** 
	 按照信息发送
	*/
	ByMsg,
	/** 
	 按照e-mail
	*/
	ByEmail,
	/** 
	 按照电话
	*/
	ByPhone,
	/** 
	 按照数据库功能
	*/
	ByDBFunc;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static CCWay forValue(int value)
	{
		return values()[value];
	}
}
