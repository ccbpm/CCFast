package bp.wf;


/** 
 加签模式
*/
public enum AskforHelpSta
{
	/** 
	 加签后直接发送
	*/
	AfterDealSend(5),
	/** 
	 加签后由我直接发送
	*/
	AfterDealSendByWorker(6);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, AskforHelpSta> mappings;
	private static java.util.HashMap<Integer, AskforHelpSta> getMappings()
	{
		if (mappings == null)
		{
			synchronized (AskforHelpSta.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, AskforHelpSta>();
				}
			}
		}
		return mappings;
	}

	private AskforHelpSta(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static AskforHelpSta forValue(int value)
	{
		return getMappings().get(value);
	}
}
