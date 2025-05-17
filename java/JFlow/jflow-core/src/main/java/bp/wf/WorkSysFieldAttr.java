package bp.wf;


/** 
 系统约定字段列表
*/
public class WorkSysFieldAttr
{
	/** 
	 发送人员字段
	 用在节点发送时确定下一个节点接受人员, 类似与发送邮件来选择接受人.
	 并且在下一个节点属性的 访问规则中选择【按表单SysSendEmps字段计算】有效。
	*/
	public static final String SysSendEmps = "SysSendEmps";
	/** 
	 抄送人员字段
	 当前的工作需要抄送时, 就需要在当前节点表单中，增加此字段。
	 并且在节点属性的抄送规则中选择【按表单SysCCEmps字段计算】有效。
	 如果有多个操作人员，字段的接受值用逗号分开。比如: zhangsan,lisi,wangwu
	*/
	public static final String SysCCEmps = "SysCCEmps";
	/** 
	 流程应完成日期
	 说明：在开始节点表单中增加此字段，用来标记此流程应当完成的日期.
	 用户在发送后就会把此值记录在WF_GenerWorkFlow 的 SDTOfFlow 中.
	 此字段显示在待办，发起，在途，删除，挂起列表里.
	*/
	public static final String SysSDTOfFlow = "SysSDTOfFlow";
	/** 
	 节点应完成时间
	 说明：在开始节点表单中增加此字段，用来标记此节点的下一个节点应该完成的日期.
	*/
	public static final String SysSDTOfNode = "SysSDTOfNode";
	/** 
	 PWorkID 调用
	*/
	public static final String PWorkID = "PWorkID";
	/** 
	 FromNode
	*/
	public static final String FromNode = "FromNode";
	/** 
	 是否需要已读回执
	*/
	public static final String SysIsReadReceipts = "SysIsReadReceipts";


		///#region 与质量考核相关的字段
	/** 
	 编号
	*/
	public static final String EvalEmpNo = "EvalEmpNo";
	/** 
	 名称
	*/
	public static final String EvalEmpName = "EvalEmpName";
	/** 
	 分值
	*/
	public static final String EvalCent = "EvalCent";
	/** 
	 内容
	*/
	public static final String EvalNote = "EvalNote";

		///#endregion 与质量考核相关的字段
}
