package bp.wf.template.sflow;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 子流程属性
*/
public class SubFlowAttr extends bp.en.EntityOIDNameAttr
{

		///#region 基本属性
	/** 
	 标题
	*/
	public static final String SubFlowNo = "SubFlowNo";
	/** 
	 流程名称
	*/
	public static final String SubFlowName = "SubFlowName";
	/** 
	 子流程状态
	*/
	public static final String SubFlowSta = "SubFlowSta";
	/** 
	 顺序号
	*/
	public static final String Idx = "Idx";
	/** 
	 批量发送后是否隐藏父流程的待办.
	*/
	public static final String SubFlowHidTodolist = "SubFlowHidTodolist";
	/** 
	 显示在那里？
	*/
	public static final String YGWorkWay = "YGWorkWay";
	/** 
	 主流程编号
	*/
	public static final String FK_Flow = "FK_Flow";
	/** 
	 节点ID
	*/
	public static final String FK_Node = "FK_Node";
	/** 
	 表达式类型
	*/
	public static final String ExpType = "ExpType";
	/** 
	 条件表达式
	*/
	public static final String CondExp = "CondExp";
	/** 
	 调用时间
	*/
	public static final String InvokeTime = "InvokeTime";
	/** 
	 越轨子流程退回类型
	*/
	public static final String YBFlowReturnRole = "YBFlowReturnRole";
	/** 
	 要退回的节点
	*/
	public static final String ReturnToNode = "ReturnToNode";
	/** 
	 延续到的节点
	*/
	public static final String YanXuToNode = "YanXuToNode";
	/** 
	 子流程类型
	*/
	public static final String SubFlowType = "SubFlowType";
	/** 
	 子流程模式
	*/
	public static final String SubFlowModel = "SubFlowModel";

		///#endregion


		///#region 子流程的发起.
	/** 
	 如果当前为子流程，仅仅只能被调用1次，不能被重复调用。
	*/
	public static final String StartOnceOnly = "StartOnceOnly";
	/** 
	 如果当前为子流程，只有该流程结束后才可以重新启用
	*/
	public static final String CompleteReStart = "CompleteReStart";
	/** 
	 是否启动
	*/
	public static final String IsEnableSpecFlowStart = "IsEnableSpecFlowStart";
	/** 
	 指定的流程启动后，才能启动该子流程.
	*/
	public static final String SpecFlowStart = "SpecFlowStart";
	/** 
	 备注
	*/
	public static final String SpecFlowStartNote = "SpecFlowStartNote";
	/** 
	 是否启用
	*/
	public static final String IsEnableSpecFlowOver = "IsEnableSpecFlowOver";
	/** 
	 指定的子流程结束后，才能启动该子流程.
	*/
	public static final String SpecFlowOver = "SpecFlowOver";
	/** 
	 备注
	*/
	public static final String SpecFlowOverNote = "SpecFlowOverNote";
	/** 
	 是否启用按指定的SQL启动
	*/
	public static final String IsEnableSQL = "IsEnableSQL";
	/** 
	 SQL语句
	*/
	public static final String SpecSQL = "SpecSQL";
	/** 
	 是否启动按指定平级子流程节点
	*/
	public static final String IsEnableSameLevelNode = "IsEnableSameLevelNode";
	/** 
	 平级子流程节点
	*/
	public static final String SameLevelNode = "SameLevelNode";

	public static final String DBSrcDoc = "DBSrcDoc";
	public static final String DBSrcType = "DBSrcType";

	/** 
	 启动模式
	*/
	public static final String SubFlowStartModel = "SubFlowStartModel";
	/** 
	 展现风格.
	*/
	public static final String SubFlowShowModel = "SubFlowShowModel";

		///#endregion

	/** 
	 自动启动子流程：发送规则.
	*/
	public static final String SendModel = "SendModel";
	/** 
	 父流程字段值拷贝到对应子流程字段中
	*/
	public static final String SubFlowCopyFields = "SubFlowCopyFields";
	/** 
	 子流程结束后填充父流程的规则
	*/
	public static final String BackCopyRole = "BackCopyRole";
	/** 
	 子流程字段值拷贝到对应父流程字段中
	*/
	public static final String ParentFlowCopyFields = "ParentFlowCopyFields";
	/** 
	 父流程自动运行到下一步的规则
	*/
	public static final String ParentFlowSendNextStepRole = "ParentFlowSendNextStepRole";
	/** 
	 父流程自动结束的规则
	*/
	public static final String ParentFlowOverRole = "ParentFlowOverRole";
	/** 
	 指定的子流程节点
	*/
	public static final String SubFlowNodeID = "SubFlowNodeID";
	/** 
	 位置x
	*/
	public static final String X = "X";
	/** 
	 位置y
	*/
	public static final String Y = "Y";
}
