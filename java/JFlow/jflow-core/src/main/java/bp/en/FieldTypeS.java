package bp.en;
/** 
 逻辑类型
*/
public enum FieldTypeS
{
	/**
	 普通类型
	 */
	Normal(0),
	/**
	 枚举类型
	 */
	Enum(1),
	/**
	 外键
	 */
	FK(2);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, FieldTypeS> mappings;
	private static java.util.HashMap<Integer, FieldTypeS> getMappings()  {
		if (mappings == null)
		{
			synchronized (FieldTypeS.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, FieldTypeS>();
				}
			}
		}
		return mappings;
	}

	private FieldTypeS(int value)
	{intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static FieldTypeS forValue(int value)
	{
		return getMappings().get(value);
	}
}