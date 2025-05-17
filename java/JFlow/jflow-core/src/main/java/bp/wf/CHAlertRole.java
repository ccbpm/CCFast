package bp.wf;


/** 
 工作提醒规则
*/
public enum CHAlertRole
{
	/** 
	 不提醒
	*/
	None,
	/** 
	 一天一次
	*/
	OneDayOneTime,
	/** 
	 一天两次
	*/
	OneDayTowTime;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static CHAlertRole forValue(int value)
	{
		return values()[value];
	}
}
