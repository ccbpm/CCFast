package bp.sys;


/** 
 导入模式
*/
public enum ImpModel
{
	/** 
	 不执行导入
	*/
	None(0),
	/** 
	 表格模式
	*/
	Table(1),
	/** 
	 按照Excel文件模式
	*/
	ExcelFile(2),
	/** 
	 单据模式
	*/
	BillModel(3);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, ImpModel> mappings;
	private static java.util.HashMap<Integer, ImpModel> getMappings()
	{
		if (mappings == null)
		{
			synchronized (ImpModel.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, ImpModel>();
				}
			}
		}
		return mappings;
	}

	private ImpModel(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static ImpModel forValue(int value)
	{
		return getMappings().get(value);
	}
}
