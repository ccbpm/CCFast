package bp.wf.template;

import bp.en.*;
/** 
 任务 属性
*/
public class TaskAttr extends EntityMyPKAttr
{

		///#region 基本属性
	/** 
	 发起人
	*/
	public static final String Starter = "Starter";
	/** 
	 流程
	*/
	public static final String FK_Flow = "FK_Flow";
	/** 
	 参数
	*/
	public static final String Paras = "Paras";
	/** 
	 任务状态
	*/
	public static final String TaskSta = "TaskSta";
	/** 
	 Msg
	*/
	public static final String Msg = "Msg";
	/** 
	 发起时间
	*/
	public static final String StartDT = "StartDT";
	/** 
	 插入日期
	*/
	public static final String RDT = "RDT";

	/** 
	 到达节点（可以为0）
	*/
	public static final String ToNode = "ToNode";
	/** 
	 到达人员（可以为空）
	*/
	public static final String ToEmps = "ToEmps";

		///#endregion
}
