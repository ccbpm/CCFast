package bp.wf;

/** 
 挂起方式
*/
public enum HungupWay
{
	/** 
	 永久挂起
	*/
	Forever,
	/** 
	 在指定的日期解除
	*/
	SpecDataRel;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static HungupWay forValue(int value)
	{
		return values()[value];
	}
}
