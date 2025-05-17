package bp.wf;


/** 
 消息类型
*/
public class SMSMsgType
{
	/** 
	 自定义消息
	*/
	public static final String Self = "Self";
	/** 
	 抄送消息
	*/
	public static final String CC = "CC";
	/** 
	 待办消息
	*/
	public static final String SendSuccess = "SendSuccess";
	/** 
	 其他
	*/
	public static final String Etc = "Etc";
	/** 
	 退回消息
	*/
	public static final String ReturnAfter = "ReturnAfter";
	/** 
	 移交消息
	*/
	public static final String Shift = "Shift";
	/** 
	 加签消息
	*/
	public static final String AskFor = "AskFor";
	/** 
	 挂起消息
	*/
	public static final String Hungup = "HangUp";
	/** 
	 催办消息
	*/
	public static final String DoPress = "DoPress";
	/** 
	 拒绝挂起的信息
	*/
	public static final String RejectHungup = "RejectHungup";
	/** 
	 错误信息
	*/
	public static final String Err = "Err";
}
