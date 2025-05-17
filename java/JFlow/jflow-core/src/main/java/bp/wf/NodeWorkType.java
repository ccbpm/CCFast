package bp.wf;
/** 
 节点工作类型
*/
public enum NodeWorkType
{
	Work(0),
	/** 
	 开始节点
	*/
	StartWork(1),
	/** 
	 开始节点分流
	*/
	StartWorkFL(2),
	/** 
	 合流节点
	*/
	WorkHL(3),
	/** 
	 分流节点
	*/
	WorkFL(4),
	/** 
	 分合流
	*/
	WorkFHL(5),
	/** 
	 子流程
	*/
	SubThreadWork(6);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, NodeWorkType> mappings;
	private static java.util.HashMap<Integer, NodeWorkType> getMappings()
	{
		if (mappings == null)
		{
			synchronized (NodeWorkType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, NodeWorkType>();
				}
			}
		}
		return mappings;
	}

	private NodeWorkType(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static NodeWorkType forValue(int value)
	{
		return getMappings().get(value);
	}
}
