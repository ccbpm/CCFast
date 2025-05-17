package bp.wf;


/** 
 抄送到角色计算方式
*/
public enum CCStaWay
{
	/** 
	 仅按角色计算
	*/
	StationOnly,
	/** 
	 按角色智能计算(当前节点的人员角色)
	*/
	StationSmartCurrNodeWorker,
	/** 
	 按角色智能计算(接受节点的人员角色)
	*/
	StationSmartNextNodeWorker,
	/** 
	 按角色与部门的交集
	*/
	StationAndDept,
	/** 
	 按直线部门找角色下的人员(当前节点)
	*/
	StationDeptUpLevelCurrNodeWorker,
	/** 
	 按直线部门找角色下的人员
	*/
	StationDeptUpLevelNextNodeWorker;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static CCStaWay forValue(int value)
	{
		return values()[value];
	}
}
