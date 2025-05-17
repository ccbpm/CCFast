package bp.sys;

/** 
 运行模式
*/
public enum CCBPMRunModel
{
	/** 
	 单机版
	*/
	Single,
	/** 
	 集团模式
	*/
	GroupInc,
	/** 
	 多租户模式
	*/
	SAAS;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static CCBPMRunModel forValue(int value)
	{
		return values()[value];
	}
}
