package bp.sys;

/** 
 百分比显示方式
*/
public enum PercentModel
{
	/** 
	 不显示
	*/
	None,
	/** 
	 纵向
	*/
	Vertical,
	/** 
	 横向
	*/
	Transverse;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static PercentModel forValue(int value)
	{
		return values()[value];
	}
}
