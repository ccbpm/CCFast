package bp.sys;


/** 
 附件删除规则
*/
public enum AthDeleteWay
{
	/** 
	 不删除 0
	*/
	None,
	/** 
	 删除所有 1
	*/
	DelAll,
	/** 
	 只删除自己上传 2
	*/
	DelSelf;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static AthDeleteWay forValue(int value)
	{
		return values()[value];
	}
}
