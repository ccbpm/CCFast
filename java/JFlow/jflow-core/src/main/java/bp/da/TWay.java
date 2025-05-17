package bp.da;
/** 
 时间计算方式
*/
public enum TWay
{
	/** 
	 计算节假日
	*/
	Holiday,
	/** 
	 不计算节假日
	*/
	AllDays;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static TWay forValue(int value)
	{
		return values()[value];
	}
}