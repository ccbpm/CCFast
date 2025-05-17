package bp.ccbill;

/**
 单据状态
*/
public enum BillState
{
	/** 
	 空白(公用)
	*/
	Blank(0),
	/** 
	 草稿(公用)
	*/
	Draft(1),
	/** 
	 编辑中(公用)
	*/
	Editing(2),
	/**
      开始审核
	 */
	Checking ( 3),
	/**
	 退回中(流程)
	 */
	ReturnSta(5),
	/**
	 单据归档
	*/
	FrmOver(100),
	/**
	 流程审核结束
	 */
	FlowOver(200);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, BillState> mappings;
	private static java.util.HashMap<Integer, BillState> getMappings()
	{
		if (mappings == null)
		{
			synchronized (BillState.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, BillState>();
				}
			}
		}
		return mappings;
	}

	private BillState(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static BillState forValue(int value)
	{
		return getMappings().get(value);
	}
}
