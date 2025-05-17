package bp.wf;


/** 
 阻塞模式
*/
public enum BlockModel
{
	/** 
	 不阻塞
	*/
	None(0),
	/** 
	 当前节点的有未完成的子线程
	*/
	CurrNodeAll(1),
	/** 
	 按照约定的格式阻塞.
	*/
	SpecSubFlow(2),
	/** 
	 按照配置的sql阻塞,返回大于等于1表示阻塞,否则不阻塞.
	*/
	BySQL(3),
	/** 
	 按照表达式阻塞，表达式类似方向条件的表达式.
	*/
	ByExp(4),
	/** 
	 为父流程时，指定的子流程未运行到指定节点，则阻塞
	*/
	SpecSubFlowNode(5),
	/** 
	 为平级子流程时，指定的子流程未运行到指定节点，则阻塞
	*/
	SameLevelSubFlow(6);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, BlockModel> mappings;
	private static java.util.HashMap<Integer, BlockModel> getMappings()
	{
		if (mappings == null)
		{
			synchronized (BlockModel.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, BlockModel>();
				}
			}
		}
		return mappings;
	}

	private BlockModel(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static BlockModel forValue(int value)
	{
		return getMappings().get(value);
	}
}
