package bp.wf;

/** 
 自动发起
*/
public enum AutoStart
{
	/** 
	 手工启动（默认）
	*/
	 None(0),
	/** 
	 按照指定的人员
	*/
	 ByDesignee(1),
	/** 
	 数据集按时启动
	*/
	 ByTineData(2),
	/** 
	 触发试启动
	*/
	 ByTrigger(3);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, AutoStart> mappings;
	private static java.util.HashMap<Integer, AutoStart> getMappings()
	{
		if (mappings == null)
		{
			synchronized (AutoStart.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, AutoStart>();
				}
			}
		}
		return mappings;
	}

	private AutoStart(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static AutoStart forValue(int value)
	{
		return getMappings().get(value);
	}
}
