package bp.gpm.weixin;

import bp.da.*;
import bp.gpm.weixin.msg.*;
import bp.*;
import java.util.*;

/** 
 微信-voice消息
*/
public class MsgVoice extends MsgBase
{
	/** 
	 必须：是- 消息类型，此时固定为：voice （不支持主页型应用）
	*/
	public final String getMsgtype()
	{
		return "voice";
	}
	/** 
	 必须：是- 语音文件id，可以调用上传临时素材或者永久素材接口获取
	*/
	private String media_id;
	public final String getMediaId()
	{
		return media_id;
	}
	public final void setMediaId(String value)
	{
		media_id = value;
	}
}
