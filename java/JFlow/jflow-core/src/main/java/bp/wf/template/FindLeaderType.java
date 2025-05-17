package bp.wf.template;


/** 
 找领导类型
*/
public enum FindLeaderType
{
	/** 
	 提交人
	*/
	Submiter,
	/** 
	 指定节点的提交人
	*/
	SpecNodeSubmiter,
	/** 
	 特定字段的提交人
	*/
	BySpecField;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static FindLeaderType forValue(int value)
	{
		return values()[value];
	}
}
