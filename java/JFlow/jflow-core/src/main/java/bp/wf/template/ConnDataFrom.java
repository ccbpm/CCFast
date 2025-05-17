package bp.wf.template;


/** 
 条件数据源
*/
public enum ConnDataFrom
{
	/** 
	 表单数据
	*/
	NodeForm(0),
	/** 
	 独立表单
	*/
	StandAloneFrm(1),
	/** 
	 角色数据
	*/
	Stas(2),
	/** 
	 Depts
	*/
	Depts(3),
	/** 
	 按sql计算.
	*/
	SQL(4),
	/** 
	 按sql模版计算.
	*/
	SQLTemplate(5),
	/** 
	 按参数
	*/
	Paras(6),
	/** 
	 按Url.
	*/
	Url(7),
	/** 
	 按WebApi返回值
	*/
	WebApi(8),
	/** 
	 按照审核组件立场
	*/
	WorkCheck(9),
	/// <summary>
	/// 按部门负责人
	/// </summary>
	CondLeaderOfDept(10),
	/**
	 按照人员
	*/
	CondEmp(11),

	/** 
	 操作符
	*/
	CondOperator(100);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, ConnDataFrom> mappings;
	private static java.util.HashMap<Integer, ConnDataFrom> getMappings()
	{
		if (mappings == null)
		{
			synchronized (ConnDataFrom.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, ConnDataFrom>();
				}
			}
		}
		return mappings;
	}

	private ConnDataFrom(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static ConnDataFrom forValue(int value)
	{
		return getMappings().get(value);
	}
}
