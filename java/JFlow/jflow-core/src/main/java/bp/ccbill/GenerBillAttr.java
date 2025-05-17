package bp.ccbill;

/** 
 单据控制表 - Attr
*/
public class GenerBillAttr
{

		///#region 基本属性1
	/** 
	 工作ID
	*/
	public static final String WorkID = "WorkID";
	/** 
	 表单ID
	*/
	public static final String FrmID = "FrmID";
	/** 
	 关联的单据号
	*/
	public static final String FrmName = "FrmName";

		///#endregion


		///#region 基本属性
	/** 
	 TSpan
	*/
	public static final String TSpan = "TSpan";
	/** 
	 单据状态
	*/
	public static final String BillState = "BillState";
	/** 
	 单据状态(简单)
	*/
	public static final String BillSta = "BillSta";
	/** 
	 标题
	*/
	public static final String Title = "Title";
	/** 
	 发起人
	*/
	public static final String Starter = "Starter";
	/** 
	 产生时间
	*/
	public static final String RDT = "RDT";
	/** 
	 完成时间
	*/
	public static final String CDT = "CDT";
	/** 
	 当前步骤.
	*/
	public static final String Idx = "Idx";
	/** 
	 步骤名称
	*/
	public static final String IdxName = "IdxName";
	/** 
	 部门
	*/
	public static final String DeptNo = "DeptNo";
	/** 
	 部门名称
	*/
	public static final String DeptName = "DeptName";
	/** 
	 年月
	*/
	public static final String FK_NY = "FK_NY";
	/** 
	 单据ID
	*/
	public static final String FID = "FID";
	/** 
	 发起人名称
	*/
	public static final String StarterName = "StarterName";
	/** 
	 单据类别
	*/
	public static final String FK_FrmTree = "FK_FrmTree";
	/** 
	 优先级
	*/
	public static final String PRI = "PRI";
	/** 
	 单据应完成时间
	*/
	public static final String SDTOfFlow = "SDTOfFlow";
	/** 
	 节点应完成时间
	*/
	public static final String SDTOfNode = "SDTOfNode";
	/** 
	 父单据ID
	*/
	public static final String PWorkID = "PWorkID";
	/** 
	 父单据编号
	*/
	public static final String PFrmID = "PFrmID";
	/** 
	 父单据节点
	*/
	public static final String PNodeID = "PNodeID";
	/** 
	 子单据的调用人.
	*/
	public static final String PEmp = "PEmp";
	/** 
	 客户编号(对于客户发起的单据有效)
	*/
	public static final String GuestNo = "GuestNo";
	/** 
	 客户名称
	*/
	public static final String GuestName = "GuestName";
	/** 
	 单据编号
	*/
	public static final String BillNo = "BillNo";
	/** 
	 备注
	*/
	public static final String FlowNote = "FlowNote";
	/** 
	 待办人员
	*/
	public static final String TodoEmps = "TodoEmps";
	/** 
	 待办人员数量
	*/
	public static final String TodoEmpsNum = "TodoEmpsNum";
	/** 
	 任务状态
	*/
	public static final String TaskSta = "TaskSta";
	/** 
	 临时存放的参数
	*/
	public static final String AtPara = "AtPara";
	/** 
	 参与人
	*/
	public static final String Emps = "Emps";
	/// 所有人员
	/// </summary>
	public static final String EmpOfAll = "EmpOfAll";
	/** 
	 GUID
	*/
	public static final String GUID = "GUID";

	public static final String Sender = "Sender";
	public static final String SendDT = "SendDT";
	/** 
	 待办状态
	*/
	public static final String TodoSta = "TodoSta";
	/// <summary>
	/// 当前的步骤.
	/// </summary>
	public static final String CurrIdx = "CurrIdx";
	/// <summary>
	/// 当前的审核人员编号s
	/// </summary>
	public static final String CurrCheckerNos = "CurrCheckerNos";
	/// <summary>
	/// 当前的审核人名称.
	/// </summary>
	public static final String CurrCheckerNames = "CurrCheckerNames";
		///#endregion
}
