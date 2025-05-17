package bp.wf;
public enum SelectorModel
{
	/** 
	 角色
	*/
	Station(0),
	/** 
	 部门
	*/
	Dept(1),
	/** 
	 操作员
	*/
	Emp(2),
	/** 
	 SQL
	*/
	SQL(3),
	/** 
	 SQL模版计算
	*/
	SQLTemplate(4),
	/** 
	 通用的人员选择器.
	*/
	GenerUserSelecter(5),
	/** 
	 按部门与角色的交集
	*/
	DeptAndStation(6),
	/** 
	 自定义链接
	*/
	Url(7),
	/** 
	 通用部门角色人员选择器
	*/
	AccepterOfDeptStationEmp(8),
	/** 
	 按角色智能计算(操作员所在部门)
	*/
	AccepterOfDeptStationOfCurrentOper(9),
	/** 
	 按本组织用户计算.
	*/
	TeamOrgOnly(10),
	/** 
	 按全组织用户计算
	*/
	TeamOnly(11),
	/** 
	 按本组织部门用户计算
	*/
	TeamDeptOnly(12),
	/** 
	 按照角色智能计算
	*/
	ByStationAI(13),
	/** 
	 按照webapi接口计算
	*/
	ByWebAPI(14),
	/** 
	 按照webapi接口计算
	*/
	ByMyDeptEmps(15);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, SelectorModel> mappings;
	private static java.util.HashMap<Integer, SelectorModel> getMappings()
	{
		if (mappings == null)
		{
			synchronized (SelectorModel.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, SelectorModel>();
				}
			}
		}
		return mappings;
	}

	private SelectorModel(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static SelectorModel forValue(int value)
	{
		return getMappings().get(value);
	}
}
