package bp.gpm.weixin;

import bp.da.*;
import bp.gpm.weixin.msg.*;
import bp.*;
import java.util.*;

/** 
 微信-文本消息
*/
public class MsgText extends MsgBase
{
	/** 
	 必须：是- 消息类型，此时固定为：text （支持消息型应用跟主页型应用） 
	*/
	public final String getMsgtype()
	{
		return "text";
	}
	/** 
	 必须：是- 消息内容，最长不超过2048个字节，注意：主页型应用推送的文本消息在微信端最多只显示20个字（包含中英文）
	*/
	private String content;
	public final String getContent()
	{
		return content;
	}
	public final void setContent(String value)
	{
		content = value;
	}


}
