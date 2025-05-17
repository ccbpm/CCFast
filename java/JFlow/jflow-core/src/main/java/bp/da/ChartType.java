package bp.da;



/** 
 图表类型
*/
public enum ChartType
{
	/** 
	 柱状图
	*/
	Histogram,
	/** 
	 丙状图
	*/
	Pie,
	/** 
	 折线图
	*/
	Line;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ChartType forValue(int value) 
	{
		return values()[value];
	}
}