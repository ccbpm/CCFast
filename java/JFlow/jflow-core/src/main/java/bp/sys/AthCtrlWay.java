package bp.sys;


public enum AthCtrlWay
{
	/** 
	 表单主键
	*/
	PK,
	/** 
	 FID
	*/
	FID,
	/** 
	 父流程ID
	*/
	PWorkID,
	/** 
	 仅仅查看自己的
	*/
	MySelfOnly,
	/** 
	 工作ID,对流程有效.
	*/
	WorkID,
	/** 
	 P2流程
	*/
	P2WorkID,
	/** 
	 P3流程
	*/
	P3WorkID,
	/** 
	 根流程的WorkID
	*/
	RootFlowWorkID;


	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static AthCtrlWay forValue(int value)
	{
		return values()[value];
	}
}
