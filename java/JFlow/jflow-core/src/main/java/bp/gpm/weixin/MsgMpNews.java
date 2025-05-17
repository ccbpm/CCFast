package bp.gpm.weixin;

import bp.da.*;
import bp.gpm.weixin.msg.*;
import bp.*;
import java.util.*;


	///#endregion


	///#region mpnews消息
/** 
 微信-mpnews消息-mpnews消息与news消息类似，不同的是图文消息内容存储在微信后台，并且支持保密选项。每个应用每天最多可以发送100次
*/
public class MsgMpNews extends MsgBase
{
	/** 
	 必须：是- 消息类型，此时固定为：mpnews （不支持主页型应用） 
	*/
	public final String getMsgtype()
	{
		return "mpnews";
	}
	/** 
	 必须：是- 图文消息，一个图文消息支持1到8条图文
	*/
	private ArrayList<MpNewsArticles> articles;
	public final ArrayList<MpNewsArticles> getArticles()
	{
		return articles;
	}
	public final void setArticles(ArrayList<MpNewsArticles> value)
	{
		articles = value;
	}
}
