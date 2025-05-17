package bp.wf.template;


/**
 消息推送属性
*/
public class PushMsgAttr
{
	/**
	 流程编号
	*/
	public static final String FlowNo = "FlowNo";
	/**
	 节点
	*/
	public static final String NodeID = "NodeID";
	/**
	 事件
	*/
	public static final String EventNo = "EventNo";
	/**
	 推送方式
	*/
	public static final String PushWayNo = "PushWayNo";
	public static final String PushWayName = "PushWayName";
	/**
	 推送处理内容
	*/
	public static final String PushDoc = "PushDoc";
	/**
	 推送处理内容 tag.
	*/
	public static final String Tag = "Tag";


		///#region 消息设置.
	/**
	 是否启用发送邮件
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

		///#endregion 消息设置.

	/**
	 短信字段
	*/
	public static final String SMSField = "SMSField";
	/**
	 接受短信的节点.
	*/
	public static final String SMSNodes = "SMSNodes";
	/**
	 推送方式
	*/
	public static final String IsEnableSMS = "IsEnableSMS";
	/**
	 短消息发送设置
	*/
	public static final String SMSPushModel = "SMSPushModel";
	/**
	 邮件字段
	*/
	public static final String MailAddress = "MailAddress";
	/**
	 邮件推送方式
	*/
	public static final String IsEnableEmail = "IsEnableEmail";
	/**
	 推送邮件的节点s
	*/
	public static final String MailNodes = "MailNodes";
	/**
	 按照指定的SQL
	*/
	public static final String BySQL = "BySQL";
	/**
	 发送给指定的接受人
	*/
	public static final String ByEmps = "ByEmps";
	public static final String PushWayExp1 = "PushWayExp1";
	public static final String PushWayExp2 = "PushWayExp2";
}
