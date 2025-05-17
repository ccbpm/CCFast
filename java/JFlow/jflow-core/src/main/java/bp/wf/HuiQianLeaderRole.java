package bp.wf;

/** 
 组长会签规则
*/
public enum HuiQianLeaderRole
{
	/** 
	 仅有一个组长
	*/
	OnlyOne(0),
	/** 
	 最后一个组长为主
	*/
	LastOneMain(1),
	/** 
	 任意组长为主
	*/
	EveryOneMain(2);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, HuiQianLeaderRole> mappings;
	private static java.util.HashMap<Integer, HuiQianLeaderRole> getMappings()
	{
		if (mappings == null)
		{
			synchronized (HuiQianLeaderRole.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, HuiQianLeaderRole>();
				}
			}
		}
		return mappings;
	}

	private HuiQianLeaderRole(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static HuiQianLeaderRole forValue(int value)
	{
		return getMappings().get(value);
	}
}
