package bp.wf;

/** 
 会签模式
*/
public enum HuiQianRole
{
	None(0),
	/** 
	 队列(按照顺序处理，有最后一个人发送到下一个节点)
	*/
	Teamup(1),
	/** 
	 协作组长模式
	*/
	TeamupGroupLeader(4);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, HuiQianRole> mappings;
	private static java.util.HashMap<Integer, HuiQianRole> getMappings()
	{
		if (mappings == null)
		{
			synchronized (HuiQianRole.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, HuiQianRole>();
				}
			}
		}
		return mappings;
	}

	private HuiQianRole(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static HuiQianRole forValue(int value)
	{
		return getMappings().get(value);
	}
}
