package bp.gpm.weixin;

import bp.da.*;
import bp.gpm.weixin.msg.*;
import bp.*;
import java.util.*;


	///#region news消息

/** 
 微信-news消息
*/
public class MsgNews extends MsgBase
{
	/** 
	 必须：是- 息类型，此时固定为：news （不支持主页型应用）
	*/
	public final String getMsgtype()
	{
		return "news";
	}

	private ArrayList<NewsArticles> _Articles = new ArrayList<NewsArticles>();
	/** 
	 必须：是- 图文消息，一个图文消息支持1到8条图文
	*/
	public final ArrayList<NewsArticles> getArticles()
	{
		return _Articles;
	}
}
