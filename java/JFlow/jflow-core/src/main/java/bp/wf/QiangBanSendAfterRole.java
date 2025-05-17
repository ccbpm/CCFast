package bp.wf;

/** 
 抢办发送后执行规则
*/
public enum QiangBanSendAfterRole
{
	/** 
	 不处理
	*/
	None(0),
	/** 
	 抄送给其他人
	*/
	CCToEtcEmps(1),
	/** 
	 发送消息给其他人
	*/
	SendMsgToEtcEmps(2);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, QiangBanSendAfterRole> mappings;
	private static java.util.HashMap<Integer, QiangBanSendAfterRole> getMappings()
	{
		if (mappings == null)
		{
			synchronized (QiangBanSendAfterRole.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, QiangBanSendAfterRole>();
				}
			}
		}
		return mappings;
	}

	private QiangBanSendAfterRole(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static QiangBanSendAfterRole forValue(int value)
	{
		return getMappings().get(value);
	}
}
