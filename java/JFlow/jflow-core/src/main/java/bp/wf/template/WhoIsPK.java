package bp.wf.template;

/** 
 谁是主键？
*/
public enum WhoIsPK
{
	/** 
	 流程ID是主键 
	*/
	OID,
	/** 
	  FID是主键(干流程的WorkID)
	*/
	FID,
	/** 
	 父流程ID是主键
	*/
	PWorkID,
	/** 
	 延续流程ID是主键
	*/
	CWorkID,
	/** 
	 爷爷流程ID是主键
	*/
	P2WorkID,
	/** 
	 太爷爷流程ID是主键
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

	public static WhoIsPK forValue(int value)
	{
		return values()[value];
	}
}
