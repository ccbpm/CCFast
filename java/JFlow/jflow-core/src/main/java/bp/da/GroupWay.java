package bp.da;



/** 
 分组方式
*/
public enum GroupWay
{
	/** 
	 求合
	*/
	BySum,
	/** 
	 求平均
	*/
	ByAvg;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static GroupWay forValue(int value) 
	{
		return values()[value];
	}
}