package bp.wf;
public enum NodeType
{
	/** 
	 用户节点
	*/
	UserNode(0),
	/** 
	 路由节点
	*/
	RouteNode(1),
	/** 
	 抄送节点
	*/
	CCNode(2),
	/** 
	 子流程节点
	*/
	SubFlowNode(3);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, NodeType> mappings;
	private static java.util.HashMap<Integer, NodeType> getMappings()
	{
		if (mappings == null)
		{
			synchronized (NodeType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, NodeType>();
				}
			}
		}
		return mappings;
	}

	private NodeType(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static NodeType forValue(int value)
	{
		return getMappings().get(value);
	}
}
