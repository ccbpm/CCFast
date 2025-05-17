package bp.wf.template;


/** 
 寻找领导模式
*/
public enum FindLeaderModel
{
	/** 
	 直接领导
	*/
	DirLeader,
	/** 
	 指定职务级别的领导
	*/
	SpecDutyLevelLeader,
	/** 
	 特定职务领导
	*/
	DutyLeader,
	/** 
	 特定角色
	*/
	SpecStation;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static FindLeaderModel forValue(int value)
	{
		return values()[value];
	}
}
