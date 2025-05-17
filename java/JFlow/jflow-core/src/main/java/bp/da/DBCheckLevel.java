package bp.da;


/** 
 数据检查级别
*/
public enum DBCheckLevel
{
	/** 
	 低,只出报告,不操作任何数据
	*/
	Low(1),
	/** 
	 中,出检查报告,删除外键的左右空格.
	*/
	Middle(2),
	/** 
	 高,删除对应不上的数据.
	*/
	High(3);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, DBCheckLevel> mappings;
	private static java.util.HashMap<Integer, DBCheckLevel> getMappings() {
		if (mappings == null)
		{
			synchronized (DBCheckLevel.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, DBCheckLevel>();
				}
			}
		}
		return mappings;
	}

	private DBCheckLevel(int value)
	{intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()  {
		return intValue;
	}

	public static DBCheckLevel forValue(int value)
	{return getMappings().get(value);
	}

}