package bp.sys;

/** 
 按日期查询方式
*/
public enum DTSearchWay
{
	/** 
	 不设置
	*/
	None,
	/** 
	 按日期
	*/
	ByDate,
	/** 
	 按日期时间
	*/
	ByDateTime,
	/** 
	 按照年月
	*/
	ByYearMonth,
	/** 
	 按照年份
	*/
	ByYear;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static DTSearchWay forValue(int value)
	{
		return values()[value];
	}
}
