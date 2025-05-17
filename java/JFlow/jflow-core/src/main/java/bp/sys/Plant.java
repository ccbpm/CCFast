package bp.sys;


/** 
 运行平台
*/
public enum Plant
{
	/** 
	 默认不打开.
	*/
	CSharp,
	/** 
	 打开
	*/
	Java;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static Plant forValue(int value)
	{
		return values()[value];
	}
}
