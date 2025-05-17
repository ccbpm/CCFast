package bp.wf.template;



/** 
 流程部门权限控制类型(与报表查询相关)
*/
public enum FlowDeptDataRightCtrlType
{
	/** 
	 只能查看本部门
	*/
	MyDeptOnly,
	/** 
	 查看本部门与下级部门
	*/
	MyDeptAndBeloneToMyDeptOnly,
	/** 
	 按指定该流程的部门人员权限控制
	*/
	BySpecFlowDept,
	/** 
	 不控制，任何人都可以查看任何部门的数据.
	*/
	AnyoneAndAnydept;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static FlowDeptDataRightCtrlType forValue(int value)
	{
		return values()[value];
	}
}
