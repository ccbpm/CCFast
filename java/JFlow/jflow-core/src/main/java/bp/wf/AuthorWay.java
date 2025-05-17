package bp.wf;


/** 
 授权方式
*/
public enum AuthorWay
{
	/** 
	 不授权
	*/
	None(0),
	/** 
	 全部授权
	*/
	All(1),
	/** 
	 指定流程授权
	*/
	SpecFlows(2);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, AuthorWay> mappings;
	private static java.util.HashMap<Integer, AuthorWay> getMappings()
	{
		if (mappings == null)
		{
			synchronized (AuthorWay.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, AuthorWay>();
				}
			}
		}
		return mappings;
	}

	private AuthorWay(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static AuthorWay forValue(int value)
	{
		return getMappings().get(value);
	}
}
