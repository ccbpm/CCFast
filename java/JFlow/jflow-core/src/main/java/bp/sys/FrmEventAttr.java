package bp.sys;

/** 
 事件属性
*/
public class FrmEventAttr
{
	/** 
	 事件类型
	*/
	public static final String FK_Event = "EventID";
	/** 
	 表单ID
	*/
	public static final String FrmID = "FrmID";
	/** 
	 流程编号
	*/
	public static final String FK_Flow = "FK_Flow";
	/** 
	 节点ID
	*/
	public static final String FK_Node = "FK_Node";
	/** 
	 事件源
	*/
	public static final String EventSource = "EventSource";
	/** 
	 关联的流程编号
	*/
	public static final String RefFlowNo = "RefFlowNo";
	/** 
	 执行类型
	*/
	public static final String EventDoType = "EventDoType";
	/** 
	 数据源
	*/
	public static final String FK_DBSrc = "FK_DBSrc";
	/** 
	 执行内容
	*/
	public static final String DoDoc = "DoDoc";
	/** 
	 标签
	*/
	public static final String MsgOK = "MsgOK";
	/** 
	 执行错误提示
	*/
	public static final String MsgError = "MsgError";


		///#region 消息设置.
	/** 
	 控制方式
	*/
	public static final String MsgCtrl = "MsgCtrl";
	/** 
	 邮件是否启用
	*/
	public static final String MailEnable = "MailEnable";
	/** 
	 消息标题
	*/
	public static final String MailTitle = "MailTitle";
	/** 
	 消息内容模版
	*/
	public static final String MailDoc = "MailDoc";
	/** 
	 是否启用短信
	*/
	public static final String SMSEnable = "SMSEnable";
	/** 
	 短信内容模版
	*/
	public static final String SMSDoc = "SMSDoc";
	/** 
	 是否推送？
	*/
	public static final String MobilePushEnable = "MobilePushEnable";
	/** 
	 DLL路径
	*/
	public static final String MonthedDLL = "MonthedDLL";
	/** 
	 DLL中所选的类名
	*/
	public static final String MonthedClass = "MonthedClass";
	/// <summary>
	/// nodeID
	/// </summary>
	public static final String RefPKVal = "RefPKVal";
	/** 
	 DLL中所选类中的方法字符串
	*/
	public static final String MonthedName = "MonthedName";
	/** 
	 DLL中所选类所选方法的参数表达式
	*/
	public static final String MonthedParas = "MonthedParas";

		///#endregion 消息设置.
}
