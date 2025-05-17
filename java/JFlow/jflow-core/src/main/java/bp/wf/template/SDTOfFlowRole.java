package bp.wf.template;


/** 
 流程计划完成日期计算规则
*/
public enum SDTOfFlowRole
{
	/** 
	 不计算
	*/
	None,
	/** 
	 按照指定的字段计算
	*/
	BySpecDateField,
	/** 
	 按照sql
	*/
	BySQL,
	/** 
	 所有的节点之和.
	*/
	ByAllNodes,
	/** 
	 按照设置的天数
	*/
	ByDays,
	/** 
	 按照时间规则计算
	*/
	TimeDT,
	/** 
	 为子流程时的规则
	*/
	ChildFlowDT,
	/** 
	 按照发起字段不能重复规则
	*/
	AttrNonredundant;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static SDTOfFlowRole forValue(int value)
	{
		return values()[value];
	}
}
