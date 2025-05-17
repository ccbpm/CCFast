package bp.ccbill;

import bp.*;

public enum SearchDataRole
{
	/** 
	 只查询自己创建的
	*/
	ByOnlySelf(0),
	/** 
	 查询本部门创建的包含兼职部门
	*/
	ByDept(1),
	/** 
	 查询本部门（包含兼职部门）及子级部门
	*/
	ByDeptAndSSubLevel(2),
	/** 
	 根据角色设定的部门的集合
	*/
	ByStationDept(3),
	/** 
	 查询所有用户创建的数据信息
	*/
	SearchAll(4);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, SearchDataRole> mappings;
	private static java.util.HashMap<Integer, SearchDataRole> getMappings()
	{
		if (mappings == null)
		{
			synchronized (SearchDataRole.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, SearchDataRole>();
				}
			}
		}
		return mappings;
	}

	private SearchDataRole(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static SearchDataRole forValue(int value)
	{
		return getMappings().get(value);
	}
}
