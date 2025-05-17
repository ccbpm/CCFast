package bp.sys;

/** 
 明细表工作方式
*/
public enum DtlModel
{
	/** 
	 普通的
	*/
	Ordinary,
	/** 
	 固定列
	*/
	FixRow;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static DtlModel forValue(int value)
	{
		return values()[value];
	}
}
