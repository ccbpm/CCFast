package bp.wf;


/** 
 发起流程参数列表,为了防止参数错误.
*/
public class StartFlowParaNameList
{

		///#region 功能的参数标记.
	/** 
	 是否删除草稿
	*/
	public static final String IsDeleteDraft = "IsDeleteDraft";

		///#endregion 功能的参数标记.


		///#region 从一个指定表里向开始节点copy数据.
	/** 
	 指定的表名称.
	*/
	public static final String FromTableName = "FromTableName";
	/** 
	 主键
	*/
	public static final String FromTablePK = "FromTablePK";
	/** 
	 主键值
	*/
	public static final String FromTablePKVal = "FromTablePKVal";

		///#endregion


		///#region 从一个指定的节点里copy数据到开始节点表里.
	public static final String CopyFormWorkID = "CopyFormWorkID";
	public static final String CopyFormNode = "CopyFormNode";

		///#endregion


		///#region 父子流程相关.
	public static final String PFlowNo = "PFlowNo";
	public static final String PNodeID = "PNodeID";
	public static final String PWorkID = "PWorkID";
	public static final String PFID = "PFID";
	/** 
	 发起人
	*/
	public static final String PEmp = "PEmp";

		///#endregion.


		///#region 流程跳转相关.
	public static final String JumpToNode = "JumpToNode";
	public static final String JumpToEmp = "JumpToEmp";

		///#endregion 流程跳转相关.
}
