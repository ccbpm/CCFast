package bp.sys;

/** 
 实体类型
*/
public enum EntityType
{
	/** 
	 独立表单
	*/
	SingleFrm(0),
	/** 
	 单据
	*/
	FrmBill(1),
	/** 
	 实体
	*/
	FrmDict(2),
	/** 
	 实体树
	*/
	EntityTree(3),
	/**
	 调查问卷
	 */
	FrmAsk(4),
	/**
	 entityNoName实体
	 */
	FrmEntityNoName(5),
	/** 
	 数据源类型
	*/
	DBList(100);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, EntityType> mappings;
	private static java.util.HashMap<Integer, EntityType> getMappings()
	{
		if (mappings == null)
		{
			synchronized (EntityType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, EntityType>();
				}
			}
		}
		return mappings;
	}

	private EntityType(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static EntityType forValue(int value)
	{
		return getMappings().get(value);
	}
}
