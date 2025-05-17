package bp.en;

public enum MsgShowType
{
	/** 
	 本界面
	*/
	SelfAlert,
	/** 
	 提示框
	*/
	SelfMsgWindows,
	/** 
	 新窗口
	*/
	Blank;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static MsgShowType forValue(int value)
	{
		return values()[value];
	}
}