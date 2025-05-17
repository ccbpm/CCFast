package bp.wf;
/** 
 运行模式
*/
public enum RunModel
{
	/** 
	 普通
	*/
	Ordinary(0),
	/** 
	 合流
	*/
	HL(1),
	/** 
	 分流
	*/
	FL(2),
	/** 
	 分合流
	*/
	FHL(3),
	/** 
	 同表单子线程
	*/
	SubThreadSameWorkID(4),
	/** 
	 异表单子线程
	*/
	SubThreadUnSameWorkID(5);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, RunModel> mappings;
	private static java.util.HashMap<Integer, RunModel> getMappings()
	{
		if (mappings == null)
		{
			synchronized (RunModel.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, RunModel>();
				}
			}
		}
		return mappings;
	}

	private RunModel(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static RunModel forValue(int value)
	{
		return getMappings().get(value);
	}
}
