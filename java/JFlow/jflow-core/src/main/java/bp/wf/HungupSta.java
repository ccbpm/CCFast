package bp.wf;

public enum HungupSta
{
	/** 
	 申请
	*/
	Apply,
	/** 
	 同意
	*/
	Agree,
	/** 
	 拒绝
	*/
	Reject;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static HungupSta forValue(int value)
	{
		return values()[value];
	}
}
