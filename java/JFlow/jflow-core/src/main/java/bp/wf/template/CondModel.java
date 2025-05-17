package bp.wf.template;


//方向条件控制
public enum CondModel
{
	/** 
	 主观选择：下拉框模式
	*/
	 DropDown(0),
	/** 
	 主观选择：按钮模式
	*/
	 Radio(1),
	/** 
	 由连接线控制
	*/
	 Line(2),
	/** 
	 发送后手工选择到达节点与接受人
	*/
	 ManuallySelect(3);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, CondModel> mappings;
	private static java.util.HashMap<Integer, CondModel> getMappings()
	{
		if (mappings == null)
		{
			synchronized (CondModel.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, CondModel>();
				}
			}
		}
		return mappings;
	}

	private CondModel(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static CondModel forValue(int value)
	{
		return getMappings().get(value);
	}
}
