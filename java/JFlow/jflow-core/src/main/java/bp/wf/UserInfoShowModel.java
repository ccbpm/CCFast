package bp.wf;
/** 
 用户信息显示格式
*/
public enum UserInfoShowModel
{
	/** 
	 用户ID,用户名
	*/
	UserIDUserName(0),
	/** 
	 用户ID
	*/
	UserIDOnly(1),
	/** 
	 用户名
	*/
	UserNameOnly(2);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, UserInfoShowModel> mappings;
	private static java.util.HashMap<Integer, UserInfoShowModel> getMappings()
	{
		if (mappings == null)
		{
			synchronized (UserInfoShowModel.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, UserInfoShowModel>();
				}
			}
		}
		return mappings;
	}

	private UserInfoShowModel(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static UserInfoShowModel forValue(int value)
	{
		return getMappings().get(value);
	}
}
