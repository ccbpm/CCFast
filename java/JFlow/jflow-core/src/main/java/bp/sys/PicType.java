package bp.sys;

public enum PicType
{
	/** 
	 自动签名
	*/
	Auto,
	/** 
	 手动签名
	*/
	ShouDong;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static PicType forValue(int value)
	{
		return values()[value];
	}
}
