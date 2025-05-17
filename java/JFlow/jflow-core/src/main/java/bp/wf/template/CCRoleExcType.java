package bp.wf.template;

/** 
 执行类型
*/
public enum CCRoleExcType
{
	/** 
	 按表单字段计算
	*/
	ByFrmField(0),
	/** 
	 按人员
	*/
	ByEmps(1),
	/** 
	 按照岗位
	*/
	ByStations(2),
	/** 
	 按部门
	*/
	ByDepts(3),
	/** 
	 按SQL
	*/
	BySQLs(4),
	/** 
	 按照节点绑定的接受人计算.
	*/
	ByDeliveryWay(5);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, CCRoleExcType> mappings;
	private static java.util.HashMap<Integer, CCRoleExcType> getMappings()
	{
		if (mappings == null)
		{
			synchronized (CCRoleExcType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, CCRoleExcType>();
				}
			}
		}
		return mappings;
	}

	private CCRoleExcType(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static CCRoleExcType forValue(int value)
	{
		return getMappings().get(value);
	}
}
