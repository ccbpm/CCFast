package bp.wf;
/** 
 子线程删除规则
*/
public enum ThreadKillRole
{
	/** 
	 不能删除，不许等到全部完成才可以向下运动。
	*/
	None,
	/** 
	 需要手工的删除才可以向下运动。
	*/
	ByHand,
	/** 
	 自动删除未完成的子线程。
	*/
	ByAuto;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ThreadKillRole forValue(int value)
	{
		return values()[value];
	}
}
