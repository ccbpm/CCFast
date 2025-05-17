package bp.wf;

/** 
 流程状态(详)
 ccflow根据是否启用草稿分两种工作模式,它的设置是在web.config 是 IsEnableDraft 节点来配置的.
 1, 不启用草稿  IsEnableDraft = 0.
	这种模式下，就没有草稿状态, 一个用户进入工作界面后就生成一个Blank, 用户保存时，也是存储blank状态。
 2, 启用草稿.
*/
public enum WFState
{
	/** 
	 空白
	*/
	Blank(0),
	/** 
	 草稿
	*/
	Draft(1),
	/** 
	 运行中
	*/
	Runing(2),
	/** 
	 已完成
	*/
	CompleteEnd(3),
	/** 
	 挂起
	*/
	Hungup(4),
	/** 
	 退回
	*/
	ReturnSta(5),
	/** 
	 转发(移交)
	*/
	Shift(6),
	/** 
	 删除(逻辑删除状态)
	*/
	Delete(7),
	/** 
	 加签
	*/
	Askfor(8),
	/** 
	 冻结
	*/
	Fix(9),
	/** 
	 加签回复状态
	*/
	AskForReplay(10),
	/**
	 非常完成(中间节点完成)
	 */
	CompleteByNode(11);
	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, WFState> mappings;
	private static java.util.HashMap<Integer, WFState> getMappings()
	{
		if (mappings == null)
		{
			synchronized (WFState.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, WFState>();
				}
			}
		}
		return mappings;
	}

	private WFState(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static WFState forValue(int value)
	{
		return getMappings().get(value);
	}
}
