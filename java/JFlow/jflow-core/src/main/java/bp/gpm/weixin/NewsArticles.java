package bp.gpm.weixin;

import bp.da.*;
import bp.gpm.weixin.msg.*;
import bp.*;
import java.util.*;

/** 
 News消息内容
*/
public class NewsArticles
{
	/** 
	 必须：否- 标题，不超过128个字节，超过会自动截断
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
	 必须：否- 描述，不超过512个字节，超过会自动截断
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
	/** 
	 必须：否- 点击后跳转的链接
	*/
	private String url;
	public final String getUrl()
	{
		return url;
	}
	public final void setUrl(String value)
	{
		url = value;
	}
	/** 
	 必须：否- 图文消息的图片链接，支持JPG、PNG格式，较好的效果为大图640*320，小图80*80。如不填，在客户端不显示图片
	*/
	private String picurl;
	public final String getPicurl()
	{
		return picurl;
	}
	public final void setPicurl(String value)
	{
		picurl = value;
	}
}
