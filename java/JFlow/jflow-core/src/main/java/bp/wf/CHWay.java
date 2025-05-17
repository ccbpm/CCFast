package bp.wf;


/** 
 考核规则
*/
public enum CHWay
{
	/** 
	 不考核
	*/
	None,
	/** 
	 按照时效考核
	*/
	ByTime,
	/** 
	 按照工作量考核
	*/
	ByWorkNum,
	/** 
	 是否是考核质量点
	*/
	IsQuality;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static CHWay forValue(int value)
	{
		return values()[value];
	}
}
