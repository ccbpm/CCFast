package bp.sys;


public enum DtlOpenType
{
	/** 
	 对人员开放
	*/
	ForEmp(0),
	/** 
	 对工作开放
	*/
	ForWorkID(1),
	/** 
	 对流程开放
	*/
	ForFID(2),
	/**
	 父工作ID
	*/
	ForPWorkID(3),
	/**
	 * 按照workid+人员账号字段.
	 */
	ForWorkIDAndSpecEmpNo(4),

	ForP2WorkID(5),

	ForP3WorkID(6),
	/** 
	 根流程的WorkID
	*/
	RootFlowWorkID(7);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;

	private static java.util.HashMap<Integer, DtlOpenType> mappings;
	private static java.util.HashMap<Integer, DtlOpenType> getMappings() {
		if (mappings == null)
		{
			synchronized (DtlOpenType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, DtlOpenType>();
				}
			}
		}
		return mappings;
	}

	private DtlOpenType(int value){
		intValue = value;
		getMappings().put(value, this);
	}
	public int getValue()
	{
		return intValue;
	}

	public static DtlOpenType forValue(int value)
	{
		return getMappings().get(value);
	}
}
