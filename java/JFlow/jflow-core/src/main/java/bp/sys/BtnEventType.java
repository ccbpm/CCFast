package bp.sys;

/** 
 按钮事件类型 - 与sl 中设置的要相同。
*/
public enum BtnEventType
{
	/** 
	 禁用
	*/
	Disable(0),
	/** 
	 运行存储过程
	*/
	RunSP(1),
	/** 
	 运行sql
	*/
	RunSQL(2),
	/** 
	 执行URL
	*/
	RunURL(3),
	/** 
	 运行webservices
	*/
	RunWS(4),
	/** 
	 运行Exe文件.
	*/
	RunExe(5),
	/** 
	 运行JS
	*/
	RunJS(6);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, BtnEventType> mappings;
	private static java.util.HashMap<Integer, BtnEventType> getMappings()
	{
		if (mappings == null)
		{
			synchronized (BtnEventType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, BtnEventType>();
				}
			}
		}
		return mappings;
	}

	private BtnEventType(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static BtnEventType forValue(int value)
	{
		return getMappings().get(value);
	}
}
