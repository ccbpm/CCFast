package bp.sys;

/** 
 按钮访问
*/
public enum BtnUAC
{
	/** 
	 不处理
	*/
	None,
	/** 
	 按人员
	*/
	ByEmp,
	/** 
	 按角色
	*/
	ByStation,
	/** 
	 按部门
	*/
	ByDept,
	/** 
	 按sql
	*/
	BySQL;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static BtnUAC forValue(int value)
	{
		return values()[value];
	}
}
