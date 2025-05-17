package bp.wf.template;


/** 
 表单启用规则
*/
public enum FrmEnableRole
{
	/** 
	 始终启用
	*/
	Allways(0),
	/** 
	 有数据时启用
	*/
	WhenHaveData(1),
	/** 
	 有参数时启用
	*/
	WhenHaveFrmPara(2),
	/** 
	 按表单的字段表达式
	*/
	ByFrmFields(3),
	/** 
	 按SQL表达式
	*/
	BySQL(4),
	/** 
	 不启用
	*/
	Disable(5),
	/** 
	 按角色
	*/
	ByStation(6),
	/** 
	 按部门
	*/
	ByDept(7);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, FrmEnableRole> mappings;
	private static java.util.HashMap<Integer, FrmEnableRole> getMappings()
	{
		if (mappings == null)
		{
			synchronized (FrmEnableRole.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, FrmEnableRole>();
				}
			}
		}
		return mappings;
	}

	private FrmEnableRole(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static FrmEnableRole forValue(int value)
	{
		return getMappings().get(value);
	}
}
