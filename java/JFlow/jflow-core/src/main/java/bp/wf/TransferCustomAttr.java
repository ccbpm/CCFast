package bp.wf;

import bp.en.*;
/** 
 自定义运行路径 属性
*/
public class TransferCustomAttr extends EntityMyPKAttr
{

		///#region 基本属性
	/** 
	 工作ID
	*/
	public static final String WorkID = "WorkID";
	/** 
	 节点ID
	*/
	public static final String FK_Node = "FK_Node";
	/** 
	 节点名称
	*/
	public static final String NodeName = "NodeName";
	/** 
	 处理人编号（多个人用逗号分开）
	*/
	public static final String Worker = "Worker";
	/** 
	 处理人显示（多个人用逗号分开）
	*/
	public static final String WorkerName = "WorkerName";
	/** 
	 顺序
	*/
	public static final String Idx = "Idx";
	/** 
	 发起时间
	*/
	public static final String StartDT = "StartDT";
	/** 
	 计划完成日期
	*/
	public static final String PlanDT = "PlanDT";
	/** 
	 要启用的子流程编号
	*/
	public static final String SubFlowNo = "SubFlowNo";
	/** 
	 是否通过了
	*/
	public static final String TodolistModel = "TodolistModel";
	/** 
	 是否启用
	*/
	public static final String IsEnable = "IsEnable";

		///#endregion
}
