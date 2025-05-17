package bp.wf;


/** 
 流程启动类型
*/
public enum FlowRunWay
{
	/** 
	 手工启动
	*/
	HandWork(0),
	/** 
	 指定人员按时启动
	*/
	SpecEmp(1),
	/** 
	 数据集按时启动
	*/
	SelectSQLModel(2),
	/** 
	 触发式启动
	*/
	WF_TaskTableInsertModel(3),
	/** 
	 指定人员按时启动高级模式
	*/
	SpecEmpAdv(4),
	/** 
	  让管理员启动流程发送到指定
	*/
	LetAdminSendSpecEmp(5);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, FlowRunWay> mappings;
	private static java.util.HashMap<Integer, FlowRunWay> getMappings()
	{
		if (mappings == null)
		{
			synchronized (FlowRunWay.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, FlowRunWay>();
				}
			}
		}
		return mappings;
	}

	private FlowRunWay(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static FlowRunWay forValue(int value)
	{
		return getMappings().get(value);
	}
}
