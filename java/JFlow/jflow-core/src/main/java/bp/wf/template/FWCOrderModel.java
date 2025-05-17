package bp.wf.template;


/** 
 协作模式下操作员显示顺序
*/
public enum FWCOrderModel
{
	/** 
	 按审批时间先后排序
	*/
	RDT(0),
	/** 
	 按照接受人员列表先后顺序(官职大小)
	*/
	SqlAccepter(1);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, FWCOrderModel> mappings;
	private static java.util.HashMap<Integer, FWCOrderModel> getMappings()
	{
		if (mappings == null)
		{
			synchronized (FWCOrderModel.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, FWCOrderModel>();
				}
			}
		}
		return mappings;
	}

	private FWCOrderModel(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static FWCOrderModel forValue(int value)
	{
		return getMappings().get(value);
	}
}
