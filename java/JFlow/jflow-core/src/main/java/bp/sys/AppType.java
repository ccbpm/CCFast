package bp.sys;


/** 
 表单类型
*/
public enum AppType
{
	/** 
	 独立表单
	*/
	Application(0),
	/** 
	 节点表单
	*/
	Node(1);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, AppType> mappings;
	private static java.util.HashMap<Integer, AppType> getMappings()
	{
		if (mappings == null)
		{
			synchronized (AppType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, AppType>();
				}
			}
		}
		return mappings;
	}

	private AppType(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static AppType forValue(int value)
	{
		return getMappings().get(value);
	}
}
