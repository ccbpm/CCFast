package bp.en;
/** 
 实体类型
*/
public enum EnType
{
	/** 
	 系统实体
	*/
	Sys,
	/** 
	 管理员维护的实体
	*/
	Admin,
	/** 
	 应用程序实体
	*/
	App,
	/** 
	 第三方的实体（可以更新）
	*/
	ThirdPartApp,
	/** 
	 视图(更新无效)
	*/
	View,
	/** 
	 可以纳入权限管理
	*/
	PowerAble,
	/** 
	 其他
	*/
	Etc,
	/** 
	 明细或着点对点。
	*/
	Dtl,
	/** 
	 点对点
	*/
	Dot2Dot,
	/** 
	 XML　类型
	*/
	XML,
	/** 
	 扩展类型，它用于查询的需要。
	*/
	Ext;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static EnType forValue(int value) 
	{
		return values()[value];
	}
}