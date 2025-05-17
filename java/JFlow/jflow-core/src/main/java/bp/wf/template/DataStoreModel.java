package bp.wf.template;


/** 
 流程数据存储模式
*/
public enum DataStoreModel
{
	/** 
	 轨迹模式
	*/
	ByCCFlow,
	/** 
	 数据合并模式
	*/
	SpecTable;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static DataStoreModel forValue(int value)
	{
		return values()[value];
	}
}
