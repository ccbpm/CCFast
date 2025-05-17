package bp.wf;


/**
 日志类型
*/
public enum ActionType
{
	/**
	 发起
	*/
	Start(0),
	/**
	 前进(发送)
	*/
	Forward(1),
	/**
	 退回
	*/
	Return(2),
	/**
	 退回并原路返回.
	*/
	ReturnAndBackWay(201),
	/**
	 移交
	*/
	Shift(3),
	/**
	 撤消移交
	*/
	UnShift(4),
	/**
	 撤消发送
	*/
	UnSend(5),
	/**
	 分流前进
	*/
	ForwardFL(6),
	/**
	 合流前进
	*/
	ForwardHL(7),
	/**
	 流程正常结束
	*/
	FlowOver(8),
	/**
	 调用起子流程
	*/
	CallChildenFlow(9),
	/**
	 启动子流程
	*/
	StartChildenFlow(10),
	/**
	 子线程前进
	*/
	SubThreadForward(11),
	/**
	 取回
	*/
	Tackback(12),
	/**
	 恢复已完成的流程
	*/
	RebackOverFlow(13),
	/**
	 强制终止流程 For lijian:2012-10-24.
	*/
	FlowOverByCoercion(14),
	/**
	 挂起
	*/
	Hungup(15),
	/**
	 取消挂起
	*/
	UnHungup(16),
	/**
	 强制移交
	*/
	ShiftByCoercion(17),
	/**
	 催办
	*/
	Press(18),
	/**
	 逻辑删除流程(撤销流程)
	*/
	DeleteFlowByFlag(19),
	/**
	 恢复删除流程(撤销流程)
	*/
	UnDeleteFlowByFlag(20),
	/**
	 抄送
	*/
	CC(21),
	/**
	 工作审核(日志)
	*/
	WorkCheck(22),
	/**
	 自动工作审核(日志)
	 */
	AutoWorkCheck(2201),
	/**
	 删除子线程
	*/
	DeleteSubThread(23),
	/**
	 请求加签
	*/
	AskforHelp(24),
	/**
	 加签向下发送
	*/
	ForwardAskfor(25),
	/**
	 自动条转的方式向下发送
	*/
	Skip(26),
	/**
	 队列发送
	*/
	Order(27),
	/**
	 协作发送
	*/
	TeampUp(28),
	/**
	 流程评论
	*/
	FlowBBS(29),
	/**
	 执行会签
	*/
	HuiQian(30),
	/**
	 调整流程
	*/
	Adjust(31),
	/**
	 路由节点
	*/
	Route(32),
	/**
	 * 公文
	 */
	GovDoc(34),
	/**
	 信息
	*/
	Info(100);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, ActionType> mappings;
	private static java.util.HashMap<Integer, ActionType> getMappings()
	{
		if (mappings == null)
		{
			synchronized (ActionType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, ActionType>();
				}
			}
		}
		return mappings;
	}

	private ActionType(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static ActionType forValue(int value)
	{
		return getMappings().get(value);
	}
}
