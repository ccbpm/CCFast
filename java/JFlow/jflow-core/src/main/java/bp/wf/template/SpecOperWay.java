package bp.wf.template;

/** 
 指定操作员方式
*/
public enum SpecOperWay
{
	/** 
	 当前的人员
	*/
	CurrOper(0),
	/** 
	 指定节点人员
	*/
	SpecNodeOper(10),
	/** 
	 指定表单人员
	*/
	SpecSheetField(22),
	/** 
	 指定人员编号
	*/
	SpenEmpNo(33);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, SpecOperWay> mappings;
	private static java.util.HashMap<Integer, SpecOperWay> getMappings()
	{
		if (mappings == null)
		{
			synchronized (SpecOperWay.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, SpecOperWay>();
				}
			}
		}
		return mappings;
	}

	SpecOperWay(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static SpecOperWay forValue(int value)
	{
		return getMappings().get(value);
	}
}
