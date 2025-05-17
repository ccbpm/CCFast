package bp.wf.data;


/** 
  报表基类属性
*/
public class NDXRptBaseAttr
{
	/** 
	 标题
	*/
	public static final String Title = "Title";
	/** 
	 参与人员
	*/
	public static final String FlowEmps = "FlowEmps";
	/** 
	 紧急程度
	*/
	public static final String PRI = "PRI";
	/** 
	 流程ID
	*/
	public static final String FID = "FID";
	/** 
	 Workid
	*/
	public static final String OID = "OID";
	/** 
	 发起年月
	*/
	public static final String FK_NY = "FK_NY";
	/** 
	 发起人ID
	*/
	public static final String FlowStarter = "FlowStarter";
	/** 
	 发起时间
	*/
	public static final String FlowStartRDT = "FlowStartRDT";
	/** 
	 发起人部门编号
	*/
	public static final String FK_Dept = "FK_Dept";
	/** 
	 流程状态
	*/
	public static final String WFState = "WFState";
	/** 
	 流程
	*/
	public static final String WFSta = "WFSta";
	/** 
	 结束人
	*/
	public static final String FlowEnder = "FlowEnder";
	/** 
	 最后处理时间
	*/
	public static final String FlowEnderRDT = "FlowEnderRDT";
	/** 
	 跨度
	*/
	public static final String FlowDaySpan = "FlowDaySpan";
	/** 
	 停留节点
	*/
	public static final String FlowEndNode = "FlowEndNode";
	/** 
	 客户编号
	*/
	public static final String GuestNo = "GuestNo";
	/** 
	 客户名称
	*/
	public static final String GuestName = "GuestName";
	/** 
	 BillNo
	*/
	public static final String BillNo = "BillNo";


		///#region 项目相关.
	/** 
	 项目编号
	*/
	public static final String PrjNo = "PrjNo";
	/** 
	 项目名称
	*/
	public static final String PrjName = "PrjName";

		///#endregion 项目相关.


		///#region 父子流程属性.
	/** 
	 父流程WorkID
	*/
	public static final String PWorkID = "PWorkID";
	/** 
	 父流程编号
	*/
	public static final String PFlowNo = "PFlowNo";
	/** 
	 调用子流程的节点
	*/
	public static final String PNodeID = "PNodeID";
	/** 
	 吊起子流程的人
	*/
	public static final String PEmp = "PEmp";
	/** 
	 参数
	*/
	public static final String AtPara = "AtPara";

		///#endregion 父子流程属性.
}