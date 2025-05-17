package bp.wf.template;


/** 
 条件类型
*/
public enum CondType
{
	/** 
	 节点完成条件
	*/
	Node(0),
	/** 
	 流程完成条件
	*/
	Flow(1),
	/** 
	 方向条件
	*/
	Dir(2),
	/** 
	 启动子流程
	*/
	SubFlow(3);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, CondType> mappings;
	private static java.util.HashMap<Integer, CondType> getMappings()
	{
		if (mappings == null)
		{
			synchronized (CondType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, CondType>();
				}
			}
		}
		return mappings;
	}

	private CondType(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static CondType forValue(int value)
	{
		return getMappings().get(value);
	}
}
