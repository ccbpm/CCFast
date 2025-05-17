package bp.wf.template;

/** 
 流程属性
*/
public class FlowAttr
{

		///#region 基本属性
	/** 
	 编号
	*/
	public static final String No = "No";
	/** 
	 名称
	*/
	public static final String Name = "Name";
	/** 
	 CCType
	*/
	public static final String CCType = "CCType";
	/** 
	 抄送方式
	*/
	public static final String CCWay = "CCWay";
	/** 
	 流程类别
	*/
	public static final String FK_FlowSort = "FK_FlowSort";
	/** 
	 建立的日期。
	*/
	public static final String CreateDate = "CreateDate";
	/** 
	 创建人
	*/
	public static final String Creater = "Creater";
	/**
	 表单水印
	 */
	public static final String IsWaterMark = "IsWaterMark";
	/**
	 表单水印展示内容
	 */
	public static final String WaterMarkText = "WaterMarkText";
	/**
	 BillTable
	*/
	public static final String BillTable = "BillTable";
	/** 
	 开始工作节点类型
	*/
	public static final String StartNodeType = "StartNodeType";
	/** 
	 StartNodeID
	*/
	public static final String StartNodeID = "StartNodeID";
	/** 
	 能不能流程Num考核。
	*/
	public static final String IsCanNumCheck = "IsCanNumCheck";
	/** 
	 是否显示附件
	*/
	public static final String IsFJ = "IsFJ";
	/** 
	 标题生成规则
	*/
	public static final String TitleRole = "TitleRole";
	/** 
	 生成标题的节点
	*/
	public static final String TitleRoleNodes = "TitleRoleNodes";
	/** 
	 流程类型
	*/
	public static final String FlowType = "FlowType";
	/** 
	 流程运行类型
	*/
	public static final String FlowRunWay = "FlowRunWay";
	/** 
	 工作模式
	*/
	public static final String WorkModel = "WorkModel";
	/** 
	 运行的设置
	*/
	public static final String RunObj = "RunObj";
	/** 
	 是否有Bill
	*/
	public static final String NumOfBill = "NumOfBill";
	/** 
	 明细表数量
	*/
	public static final String NumOfDtl = "NumOfDtl";
	/** 
	 是否可以启动？
	*/
	public static final String IsCanStart = "IsCanStart";
	/** 
	 是否可以在手机里启用?
	*/
	public static final String IsStartInMobile = "IsStartInMobile";

	/** 
	 是否自动计算未来的处理人？
	*/
	public static final String IsFullSA = "IsFullSA";
	/** 
	 类型
	*/
	public static final String FlowAppType = "FlowAppType";
	/** 
	 图像类型
	*/
	public static final String ChartType = "ChartType";
	/** 
	 流程计划完成日期设置规则
	*/
	public static final String SDTOfFlowRole = "SDTOfFlowRole";
	public static final String SDTOfFlowRoleSQL = "SDTOfFlowRoleSQL";
	/** 
	 草稿
	*/
	public static final String Draft = "Draft";
	/** 
	 顺序号
	*/
	public static final String Idx = "Idx";
	/** 
	 参数
	*/
	public static final String Paras = "Paras";
	/** 
	 业务主表
	*/
	public static final String PTable = "PTable";
	/** 
	 表单连接
	*/
	public static final String FrmUrl = "FrmUrl";
	/** 
	 表单URL
	*/
	public static final String FlowDevModel = "FlowDevModel";
	/** 
	 流程标记
	*/
	public static final String FlowMark = "FlowMark";
	/** 
	 流程事件实体
	*/
	public static final String FlowEventEntity = "FlowEventEntity";
	/** 
	 外部客户参与流程规则
	*/
	public static final String GuestFlowRole = "GuestFlowRole";
	/** 
	 单据编号格式
	*/
	public static final String BillNoFormat = "BillNoFormat";
	/** 
	 待办字段s
	*/
	public static final String BuessFields = "BuessFields";
	/** 
	 高级查询人员字段
	*/
	public static final String AdvEmps = "AdvEmps";
	/** 
	 数据权限控制方式
	*/
	public static final String DRCtrlType = "DRCtrlType";
	/** 
	 是否可以批量发起?
	*/
	public static final String IsBatchStart = "IsBatchStart";
	/** 
	 批量发起填写的字段.
	*/
	public static final String BatchStartFields = "BatchStartFields";
	/** 
	 是否是MD5
	*/
	public static final String IsMD5 = "IsMD5";
	/** 
	 是否是数据加密
	*/
	public static final String IsJM = "IsJM";
	/** 
	 是否启用数据版本控制
	*/
	public static final String IsEnableDBVer = "IsEnableDBVer";

	public static final String CCStas = "CCStas";
	public static final String Note = "Note";
	/** 
	 运行的SQL
	*/
	public static final String RunSQL = "RunSQL";

	/** 
	 流程轨迹中显示的Tab标签页的控制
	*/
	//public const String IsFrmEnable = "IsFrmEnable";
	public static final String IsTruckEnable = "IsTruckEnable";
	public static final String IsTimeBaseEnable = "IsTimeBaseEnable";
	public static final String IsTableEnable = "IsTableEnable";
	//public const String IsOPEnable = "IsOPEnable";
	/** 
	 排序方式
	*/
	public static final String TrackOrderBy = "TrackOrderBy";
	/** 
	 子流程轨迹图中的显示
	*/
	public static final String SubFlowShowType = "SubFlowShowType";

		///#endregion 基本属性


		///#region 发起限制规则.
	/** 
	 发起限制规则
	*/
	public static final String StartLimitRole = "StartLimitRole";
	/** 
	 规则内容
	*/
	public static final String StartLimitPara = "StartLimitPara";
	/** 
	 规则提示
	*/
	public static final String StartLimitAlert = "StartLimitAlert";
	/** 
	 提示时间
	*/
	public static final String StartLimitWhen = "StartLimitWhen";

		///#endregion 发起限制规则.


		///#region 开始节点数据导入规则.
	/** 
	 发起前置规则
	*/
	public static final String StartGuideWay = "StartGuideWay";
	/** 
	 超链接
	*/
	public static final String StartGuideLink = "StartGuideLink";
	/** 
	 标签
	*/
	public static final String StartGuideLab = "StartGuideLab";
	/** 
	 发起前置参数1
	*/
	public static final String StartGuidePara1 = "StartGuidePara1";
	/** 
	 发起前置参数2
	*/
	public static final String StartGuidePara2 = "StartGuidePara2";
	/** 
	 StartGuidePara3
	*/
	public static final String StartGuidePara3 = "StartGuidePara3";
	/** 
	 是否启用开始节点的数据重置？
	*/
	public static final String IsResetData = "IsResetData";
	/** 
	 是否启用导入历史数据按钮?
	*/
	public static final String IsImpHistory = "IsImpHistory";
	/** 
	 是否自动装载上一笔数据？
	*/
	public static final String IsLoadPriData = "IsLoadPriData";
	/** 
	 是否启用数据模版？
	*/
	public static final String IsDBTemplate = "IsDBTemplate";
	/** 
	 系统类别（第2级流程树节点编号）
	*/
	public static final String SysType = "SysType";

		///#endregion 开始节点数据导入规则.


		///#region 父子流程
	/** 
	 版本号
	*/
	public static final String Ver = "Ver";
	/** 
	 子流程结束时，让父流程自动运行到下一步
	*/
	public static final String IsToParentNextNode = "IsToParentNextNode";
	/** 
	 OrgNo
	*/
	public static final String OrgNo = "OrgNo";
	/** 
	 创建日期
	*/
	public static final String RDT = "RDT";

		///#endregion 父子流程


		///#region 数据同步方式.
	/**
	 数据同步方式.DTSWay
	 */
	public static final String DTSWay = "DTSWay";
	/** 
	 业务表主键
	*/
	public static final String DTSBTablePK = "DTSBTablePK";
	/** 
	 执行同步时间点
	*/
	public static final String DTSTime = "DTSTime";
	/** 
	 同步格式配置.
	*/
	public static final String DTSSpecNodes = "DTSSpecNodes";
	public static final String DTSField = "DTSField";
	public static final String DTSFields = "DTSFields";
	/** 
	 业务表
	*/
	public static final String DTSBTable = "DTSBTable";
	/** 
	 数据源
	*/
	public static final String DTSDBSrc = "DTSDBSrc";
	/** 
	 webapi
	*/
	public static final String DTSWebAPI = "DTSWebAPI";

		///#endregion


		///#region 权限组.
	/** 
	 发起人可看
	*/
	public static final String PStarter = "PStarter";
	/** 
	 参与人可看
	*/
	public static final String PWorker = "PWorker";
	/** 
	 被抄送人可看
	*/
	public static final String PCCer = "PCCer";
	/** 
	 本部门人可看
	*/
	public static final String PMyDept = "PMyDept";
	/** 
	 直属上级部门可看
	*/
	public static final String PPMyDept = "PPMyDept";
	/** 
	 上级部门可看
	*/
	public static final String PPDept = "PPDept";
	/** 
	 平级部门可看
	*/
	public static final String PSameDept = "PSameDept";
	/** 
	 指定部门可看
	*/
	public static final String PSpecDept = "PSpecDept";
	/** 
	 指定的角色可看
	*/
	public static final String PSpecSta = "PSpecSta";
	/** 
	 指定的权限组可看
	*/
	public static final String PSpecGroup = "PSpecGroup";
	/** 
	 指定的人员可看
	*/
	public static final String PSpecEmp = "PSpecEmp";

		///#endregion 权限组.
	/** 
	 流程发起测试人
	*/
	public static final String Tester = "Tester";

	public static final String DevModelType = "DevModelType";
	public static final String DevModelPara = "DevModelPara";
}
