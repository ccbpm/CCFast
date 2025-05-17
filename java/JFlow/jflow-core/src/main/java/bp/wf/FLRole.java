package bp.wf;

/** 
 分流规则
*/
public enum FLRole
{
	/** 
	 按照接受人
	*/
	ByEmp,
	/** 
	 按照部门
	*/
	ByDept,
	/** 
	 按照角色
	*/
	ByStation;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static FLRole forValue(int value)
	{
		return values()[value];
	}
}
