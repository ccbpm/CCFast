package bp.ccbill;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import java.util.*;

/** 
  轨迹-属性
*/
public class TrackAttr extends EntityMyPKAttr
{
	/** 
	 记录日期
	*/
	public static final String RDT = "RDT";
	/** 
	 WorkID
	*/
	public static final String WorkID = "WorkID";
	/** 
	 活动类型
	*/
	public static final String ActionType = "ActionType";
	/** 
	 活动类型名称
	*/
	public static final String ActionTypeText = "ActionTypeText";
	/** 
	 记录人
	*/
	public static final String Rec = "Rec";
	/** 
	 记录人名称
	*/
	public static final String RecName = "RecName";
	/** 
	 部门No
	*/
	public static final String FK_Dept = "FK_Dept";
	/** 
	 部门名称
	*/
	public static final String DeptName = "DeptName";
	/** 
	 参数信息
	*/
	public static final String Tag = "Tag";
	/** 
	 表单数据
	*/
	public static final String FrmDB = "FrmDB";
	/** 
	 消息
	*/
	public static final String Msg = "Msg";
	/** 
	 表单ID
	*/
	public static final String FrmID = "FrmID";
	/** 
	 表单名称
	*/
	public static final String FrmName = "FrmName";


		///#region 流程相关信息.
	/** 
	 FID
	*/
	public static final String FID = "FID";
	/** 
	 流程ID
	*/
	public static final String FlowNo = "FlowNo";
	/** 
	 流程名称
	*/
	public static final String FlowName = "FlowName";
	/** 
	 节点ID
	*/
	public static final String NodeID = "NodeID";
	/** 
	 节点名字
	*/
	public static final String NodeName = "NodeName";
	/** 
	 流程的WorkID.
	*/
	public static final String WorkIDOfFlow = "WorkIDOfFlow";

		///#endregion 流程相关信息.
}
