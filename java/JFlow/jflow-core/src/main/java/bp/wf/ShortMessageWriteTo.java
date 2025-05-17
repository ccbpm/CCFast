package bp.wf;

/** 
 短消息写入规则（当系统产生短消息的时候，需要写入的方式。）
*/
public enum ShortMessageWriteTo
{
	/** 
	 写入Sys_SMS表
	*/
	ToSMSTable(0),
	/** 
	 写入WebServices.
	 WS地址: \DataUser\PortalInterface.asmx 的 WriteShortMessage
	*/
	ToWebservices(1),
	/** 
	 写入丁丁
	*/
	ToDingDing(2),
	/** 
	 写入微信.
	*/
	ToWeiXin(3),
	/** 
	 写入CCIM
	*/
	CCIM(4);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, ShortMessageWriteTo> mappings;
	private static java.util.HashMap<Integer, ShortMessageWriteTo> getMappings()
	{
		if (mappings == null)
		{
			synchronized (ShortMessageWriteTo.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, ShortMessageWriteTo>();
				}
			}
		}
		return mappings;
	}

	private ShortMessageWriteTo(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static ShortMessageWriteTo forValue(int value)
	{
		return getMappings().get(value);
	}
}
