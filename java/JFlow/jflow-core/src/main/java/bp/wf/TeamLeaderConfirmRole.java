package bp.wf;
/** 
 组长确认规则
*/
public enum TeamLeaderConfirmRole
{
	/** 
	 按照部门表的字段 Leader 模式计算.
	*/
	ByDeptFieldLeader,
	/** 
	 按照SQL计算.
	*/
	BySQL,
	/** 
	 会签时主持人计算
	*/
	HuiQianLeader;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static TeamLeaderConfirmRole forValue(int value)
	{
		return values()[value];
	}
}
