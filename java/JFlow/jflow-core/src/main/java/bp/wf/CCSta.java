package bp.wf;


/** 
 抄送状态
*/
public enum CCSta
{
	/** 
	 未读
	*/
	UnRead,
	/** 
	 已读
	*/
	Read,
	/** 
	 已回复
	*/
	CheckOver,
	/** 
	 已删除
	*/
	Del;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static CCSta forValue(int value)
	{
		return values()[value];
	}
}
