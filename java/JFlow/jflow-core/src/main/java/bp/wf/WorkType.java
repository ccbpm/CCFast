package bp.wf;

/** 
 工作类型
*/
public enum WorkType
{
	/** 
	 普通的
	*/
	Ordinary,
	/** 
	 自动的
	*/
	Auto;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static WorkType forValue(int value)
	{
		return values()[value];
	}
}
