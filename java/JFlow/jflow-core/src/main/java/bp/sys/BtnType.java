package bp.sys;

/** 
 按钮类型
*/
public enum BtnType
{
	/** 
	 保存
	*/
	Save(0),
	/** 
	 打印
	*/
	Print(1),
	/** 
	 删除
	*/
	Delete(2),
	/** 
	 增加
	*/
	Add(3),
	/** 
	 自定义
	*/
	Self(100);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, BtnType> mappings;
	private static java.util.HashMap<Integer, BtnType> getMappings()
	{
		if (mappings == null)
		{
			synchronized (BtnType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, BtnType>();
				}
			}
		}
		return mappings;
	}

	private BtnType(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static BtnType forValue(int value)
	{
		return getMappings().get(value);
	}
}
