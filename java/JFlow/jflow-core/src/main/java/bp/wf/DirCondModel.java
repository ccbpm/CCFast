package bp.wf;
/** 
 方向条件控制规则
*/
public enum DirCondModel
{
	/** 
	 由连接线控制
	*/
	ByLineCond(0),
	/** 
	 发送后手工选择到达节点与接受人（用于子线程节点）
	*/
	ByPopSelect(1),
	/** 
	 下拉框模式
	*/
	ByDDLSelected(2),
	/** 
	 按照按钮选择
	*/
	ByButtonSelected(3);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, DirCondModel> mappings;
	private static java.util.HashMap<Integer, DirCondModel> getMappings()
	{
		if (mappings == null)
		{
			synchronized (DirCondModel.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, DirCondModel>();
				}
			}
		}
		return mappings;
	}

	private DirCondModel(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static DirCondModel forValue(int value)
	{
		return getMappings().get(value);
	}
}
