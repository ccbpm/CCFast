package bp.sys;
/** 
 数字签名类型
*/
public enum SignType
{
	/** 
	 无
	*/
	None,
	/** 
	 图片
	*/
	Pic,
	/** 
	 山东CA签名.
	*/
	CA,
	/** 
	 广东CA
	*/
	GDCA,
	/** 
	 图片盖章
	*/
	GZCA;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static SignType forValue(int value)
	{
		return values()[value];
	}
}
