package bp.wf;

/** 
 自动跳转规则
*/
public enum AutoJumpRole
{
	/** 
	 处理人就是提交人
	*/
	DealerIsDealer,
	/** 
	 处理人已经出现过
	*/
	DealerIsInWorkerList,
	/** 
	 处理人与上一步相同
	*/
	DealerAsNextStepWorker;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static AutoJumpRole forValue(int value)
	{
		return values()[value];
	}
}
