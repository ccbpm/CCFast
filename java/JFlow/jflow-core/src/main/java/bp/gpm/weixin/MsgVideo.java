package bp.gpm.weixin;

import bp.da.*;
import bp.gpm.weixin.msg.*;
import bp.*;
import java.util.*;

/** 
 微信-video消息
*/
public class MsgVideo extends MsgBase
{
	/** 
	 必须：是- 消息类型，此时固定为：video （不支持主页型应用）
	*/
	public final String getMsgtype()
	{
		return "video";
	}
	/** 
	 必须：是- 视频媒体文件id，可以调用上传临时素材或者永久素材接口获取 
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
	/** 
	 必须：否- 视频消息的标题，不超过128个字节，超过会自动截断
	*/
	private String title;
	public final String getTitle()
	{
		return title;
	}
	public final void setTitle(String value)
	{
		title = value;
	}
	/** 
	 必须：否- 视频消息的描述，不超过512个字节，超过会自动截断 
	*/
	private String description;
	public final String getDescription()
	{
		return description;
	}
	public final void setDescription(String value)
	{
		description = value;
	}
}
