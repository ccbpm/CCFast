package bp.wf;

/** 
 删除流程规则
 @0=不能删除
 @1=逻辑删除
 @2=记录日志方式删除: 数据删除后，记录到WF_DeleteWorkFlow中。
 @3=彻底删除：
 @4=让用户决定删除方式
*/
public enum DelWorkFlowRole
{
	/** 
	 不能删除
	*/
	None,
	/** 
	 按照标记删除(需要交互,填写删除原因)
	*/
	DeleteByFlag,
	/** 
	 删除到日志库(需要交互,填写删除原因)
	*/
	DeleteAndWriteToLog,
	/** 
	 彻底的删除(不需要交互，直接干净彻底的删除)
	*/
	DeleteReal,
	/** 
	 让用户决定删除方式(需要交互)
	*/
	ByUser;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static DelWorkFlowRole forValue(int value)
	{
		return values()[value];
	}
}
