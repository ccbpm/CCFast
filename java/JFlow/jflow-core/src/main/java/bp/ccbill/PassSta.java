package bp.ccbill;

/**
 单据状态
*/
public enum PassSta
{
	/**
	 审核中
	*/
	Checking(0),
	/**
	 未审核
	*/
	UnPass(1),
	/**
	 审核通过
	*/
	Passed(2),
	/**
	 退回
	 */
	ReturnSta ( 3);

	public static final int SIZE = Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, PassSta> mappings;
	private static java.util.HashMap<Integer, PassSta> getMappings()
	{
		if (mappings == null)
		{
			synchronized (PassSta.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, PassSta>();
				}
			}
		}
		return mappings;
	}

	private PassSta(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static PassSta forValue(int value)
	{
		return getMappings().get(value);
	}
}
