package bp.wf;


/** 
 节点工作批处理
*/
public enum BatchRole
{
	/** 
	 不可以
	*/
	None,
	/** 
	 批量审批
	*/
	WorkCheckModel,
	/** 
	 分组批量审核
	*/
	Group;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static BatchRole forValue(int value)
	{
		return values()[value];
	}
}
