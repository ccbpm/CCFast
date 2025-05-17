package bp.pub;


/** 
 折线图图显示类型
*/
public enum LineChartShowType
{
	/** 
	 不显示
	*/
	None,
	/** 
	 横向
	*/
	HengXiang,
	/** 
	 竖向
	*/
	ShuXiang;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static LineChartShowType forValue(int value)
	{
		return values()[value];
	}
}
