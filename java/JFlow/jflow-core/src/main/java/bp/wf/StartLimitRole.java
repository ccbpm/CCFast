package bp.wf;

/** 
 流程发起限制
*/
public enum StartLimitRole
{
	/** 
	 不限制
	*/
	None(0),
	/** 
	 一人一天一次
	*/
	Day(1),
	/** 
	 一人一周一次
	*/
	Week(2),
	/** 
	 一人一月一次
	*/
	Month(3),
	/** 
	 一人一季度一次
	*/
	JD(4),
	/** 
	 一人一年一次
	*/
	Year(5),
	/** 
	 发起的列不能重复,(多个列可以用逗号分开)
	*/
	ColNotExit(6),
	/** 
	 设置的SQL数据源为空,或者返回结果为零时可以启动.
	*/
	ResultIsZero(7),
	/** 
	 设置的SQL数据源为空,或者返回结果为零时不可以启动.
	*/
	ResultIsNotZero(8),
	/** 
	 为子流程时仅仅只能被调用1次.
	*/
	OnlyOneSubFlow(9);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, StartLimitRole> mappings;
	private static java.util.HashMap<Integer, StartLimitRole> getMappings()
	{
		if (mappings == null)
		{
			synchronized (StartLimitRole.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, StartLimitRole>();
				}
			}
		}
		return mappings;
	}

	private StartLimitRole(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static StartLimitRole forValue(int value)
	{
		return getMappings().get(value);
	}
}
