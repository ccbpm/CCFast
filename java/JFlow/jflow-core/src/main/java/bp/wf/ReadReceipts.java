package bp.wf;
/** 
 已读回执类型
*/
public enum ReadReceipts
{
	/** 
	 不回执
	*/
	None,
	/** 
	 自动回执
	*/
	Auto,
	/** 
	 由系统字段决定
	*/
	BySysField,
	/** 
	 按开发者参数
	*/
	BySDKPara;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ReadReceipts forValue(int value)
	{
		return values()[value];
	}
}
