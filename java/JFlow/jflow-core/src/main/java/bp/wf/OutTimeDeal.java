package bp.wf;
/** 
 待办工作超时处理方式
*/
public enum OutTimeDeal
{
	/** 
	 不处理
	*/
	None(0),
	/** 
	 自动的转向下一步骤
	*/
	AutoTurntoNextStep(1),
	/** 
	 自动跳转到指定的点
	*/
	AutoJumpToSpecNode(2),
	/** 
	 自动移交到指定的人员
	*/
	AutoShiftToSpecUser(3),
	/** 
	 向指定的人员发送消息
	*/
	SendMsgToSpecUser(4),
	/** 
	 删除流程
	*/
	DeleteFlow(5),
	/** 
	 执行SQL
	*/
	RunSQL(6);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, OutTimeDeal> mappings;
	private static java.util.HashMap<Integer, OutTimeDeal> getMappings()
	{
		if (mappings == null)
		{
			synchronized (OutTimeDeal.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, OutTimeDeal>();
				}
			}
		}
		return mappings;
	}

	private OutTimeDeal(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static OutTimeDeal forValue(int value)
	{
		return getMappings().get(value);
	}
}
