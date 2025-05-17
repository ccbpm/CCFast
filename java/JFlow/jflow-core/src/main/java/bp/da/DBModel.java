package bp.da;

/** 
 数据库部署类型
*/
public enum DBModel
{
	/** 
	 独立（集中模式）
	*/
	Single(0),
	/** 
	 域模式
	*/
	DomainExt(1);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, DBModel> mappings;
	private static java.util.HashMap<Integer, DBModel> getMappings() {
		if (mappings == null)
		{
			synchronized (DBModel.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, DBModel>();
				}
			}
		}
		return mappings;
	}

	private DBModel(int value)
	{intValue = value;
		getMappings().put(value, this);
	}

	public int getValue() {
		return intValue;
	}

	public static DBModel forValue(int value)
	{return getMappings().get(value);
	}
}