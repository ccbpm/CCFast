package bp.ccbill;

import bp.*;

public enum SearchDataRoleByDeptStation
{
	/** 
	 只查询自己创建的
	*/
	ByOnlySelf(0),
	/** 
	 查询本部门创建的包含兼职部门
	*/
	ByDept(1);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, SearchDataRoleByDeptStation> mappings;
	private static java.util.HashMap<Integer, SearchDataRoleByDeptStation> getMappings()
	{
		if (mappings == null)
		{
			synchronized (SearchDataRoleByDeptStation.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, SearchDataRoleByDeptStation>();
				}
			}
		}
		return mappings;
	}

	private SearchDataRoleByDeptStation(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static SearchDataRoleByDeptStation forValue(int value)
	{
		return getMappings().get(value);
	}
}
