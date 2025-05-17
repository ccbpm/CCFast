package bp.wf;

/** 
 普通工作节点处理模式
*/
public enum TodolistModel
{
	/** 
	 抢办(谁抢到谁来办理,办理完后其他人就不能办理.)
	*/
	QiangBan(0),
	/** 
	 协作(没有处理顺序，接受的人都要去处理,由最后一个人发送到下一个节点)
	*/
	Teamup(1),
	/** 
	 队列(按照顺序处理，有最后一个人发送到下一个节点)
	*/
	Order(2),
	/** 
	 共享模式(需要申请，申请后才能执行)
	*/
	Sharing(3),
	/** 
	 协作组长模式
	*/
	TeamupGroupLeader(4);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, TodolistModel> mappings;
	private static java.util.HashMap<Integer, TodolistModel> getMappings()
	{
		if (mappings == null)
		{
			synchronized (TodolistModel.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, TodolistModel>();
				}
			}
		}
		return mappings;
	}

	private TodolistModel(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static TodolistModel forValue(int value)
	{
		return getMappings().get(value);
	}
}
