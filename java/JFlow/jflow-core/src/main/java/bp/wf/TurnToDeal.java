package bp.wf;
/** 
 节点完成转向处理
*/
public enum TurnToDeal
{
	/** 
	 按系统默认的提示
	*/
	CCFlowMsg,
	/** 
	 指定消息
	*/
	SpecMsg,
	/** 
	 指定Url
	*/
	SpecUrl,
	/** 
	 发送后关闭
	*/
	TurntoClose,
	/** 
	 按条件转向
	*/
	TurnToByCond;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static TurnToDeal forValue(int value)
	{
		return values()[value];
	}
}
