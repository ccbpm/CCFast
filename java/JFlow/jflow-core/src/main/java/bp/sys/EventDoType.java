package bp.sys;
/** 
 事件执行内容
*/
public enum EventDoType
{
	/** 
	 禁用
	*/
	Disable(0),
	/** 
	 执行存储过程
	*/
	SP(1),
	/** 
	 运行SQL
	*/
	SQL(2),
	/** 
	 自定义URL
	*/
	URLOfSelf(3),
	/** 
	 自定义WS
	*/
	WSOfSelf(4),
	/** 
	 执行ddl文件的类与方法
	*/
	SpecClass(5),
	/** 
	 基类
	*/
	EventBase(6),
	/** 
	 执行的业务单元
	*/
	BuessUnit(7),
	/** 
	 自定义WebApi
	*/
	WebApi(8),
	/** 
	 SFProduces
	*/
	SFProcedure(9);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, EventDoType> mappings;
	private static java.util.HashMap<Integer, EventDoType> getMappings()
	{
		if (mappings == null)
		{
			synchronized (EventDoType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, EventDoType>();
				}
			}
		}
		return mappings;
	}

	private EventDoType(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static EventDoType forValue(int value)
	{
		return getMappings().get(value);
	}
}
