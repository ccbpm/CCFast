package bp.wf;
/** 
 装在前提示
*/
public enum StartLimitWhen
{
	/** 
	 表单装载后
	*/
	StartFlow,
	/** 
	 发送前检查
	*/
	SendWhen;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static StartLimitWhen forValue(int value)
	{
		return values()[value];
	}
}
