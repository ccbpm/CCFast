package bp.sys;

/** 
 棫行处理
*/
public enum WhenOverSize
{
	/** 
	 不处理
	*/
	None,
	/** 
	 增加一行
	*/
	AddRow,
	/** 
	 翻页
	*/
	TurnPage;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static WhenOverSize forValue(int value)
	{
		return values()[value];
	}
}
