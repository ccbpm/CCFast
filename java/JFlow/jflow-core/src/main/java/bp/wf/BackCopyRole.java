package bp.wf;

/** 
 子流程数据反填父流程数据规则
*/
public enum BackCopyRole
{
	/** 
	 不反填
	*/
	None,
	/** 
	 字段自动匹配
	*/
	AutoFieldMatch,
	/** 
	 按照设置的格式匹配
	*/
	FollowSetFormat,
	/** 
	 混合模式
	*/
	MixedMode;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static BackCopyRole forValue(int value)
	{
		return values()[value];
	}
}
