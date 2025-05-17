package bp.wf.data;


/** 
 完成状态
*/
public enum CHSta
{
	/** 
	 按期完成
	*/
	AnQi(0),
	/** 
	 预期完成
	*/
	YuQi(1);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, CHSta> mappings;
	private static java.util.HashMap<Integer, CHSta> getMappings() {
		if (mappings == null)
		{
			synchronized (CHSta.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, CHSta>();
				}
			}
		}
		return mappings;
	}

	private CHSta(int value)
	{intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static CHSta forValue(int value)
	{
		return getMappings().get(value);
	}
}