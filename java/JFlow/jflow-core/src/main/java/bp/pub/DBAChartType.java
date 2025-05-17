package bp.pub;


/** 
 显示类型.
*/
public enum DBAChartType
{
	Table,
	Column,
	Pie,
	Line;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static DBAChartType forValue(int value)
	{
		return values()[value];
	}
}
