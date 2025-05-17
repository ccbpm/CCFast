package bp.wf;

import bp.en.*;

/** 
 消息属性
*/
public class SMSAttr extends EntityMyPKAttr
{
	/** 
	 消息标记（有此标记的不在发送）
	*/
	public static final String MsgFlag = "MsgFlag";
	/** 
	 状态 0 未发送， 1 发送成功，2发送失败.
	*/
	public static final String EmailSta = "EmailSta";
	/** 
	 邮件
	*/
	public static final String Email = "Email";
	/** 
	 邮件标题
	*/
	public static final String EmailTitle = "EmailTitle";
	/** 
	 邮件内容
	*/
	public static final String EmailDoc = "EmailDoc";
	/** 
	 发送人
	*/
	public static final String Sender = "Sender";
	/** 
	 发送给
	*/
	public static final String SendTo = "SendTo";
	/** 
	 插入日期
	*/
	public static final String RDT = "RDT";
	/** 
	 发送日期
	*/
	public static final String SendDT = "SendDT";
	/** 
	 是否读取
	*/
	public static final String IsRead = "IsRead";
	/** 
	 状态 0 未发送， 1 发送成功，2发送失败.
	*/
	public static final String MobileSta = "MobileSta";
	/** 
	 手机
	*/
	public static final String Mobile = "Mobile";
	/** 
	 手机信息
	*/
	public static final String MobileInfo = "MobileInfo";
	/** 
	 是否提示过了
	*/
	public static final String IsAlert = "IsAlert";
	/** 
	 消息类型
	*/
	public static final String MsgType = "MsgType";
	/** 
	 其他参数.
	*/
	public static final String Paras = "Paras";
	/** 
	 打开的连接
	*/
	public static final String OpenUrl = "OpenUrl";
	/** 
	 接受消息的工具 丁丁、微信
	*/
	public static final String PushModel = "PushModel";
	/** 
	 主键
	*/
	public static final String WorkID = "WorkID";
}
