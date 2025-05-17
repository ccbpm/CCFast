package bp.sys;


/** 
 运行模式
*/
public enum AthRunModel
{
	/** 
	 记录模式
	*/
	RecordModel,
	/** 
	 固定模式
	*/
	FixModel;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static AthRunModel forValue(int value)
	{
		return values()[value];
	}
}
