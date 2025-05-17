package bp.gpm.weixin;

import bp.tools.*;
import bp.en.*; import bp.en.Map;
import bp.wf.*;
import bp.*;

/** 
 公共方法处理
*/
public class Glo
{
	/** 
	 发送文本消息
	 
	 @param msgText 消息实体类
	 @return 发送消息结果
	*/
	public static MessageErrorModel PostMsgOfText(MsgText msgText)
	{
		return null;
		/*String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + msgText.getAccessToken();
		try
		{
			StringBuilder append_Json = new StringBuilder();
			append_Json.append("{");
			append_Json.append("\"msgtype\":\"text\"");
			append_Json.append(",\"touser\":\"" + msgText.getTouser() + "\"");
			append_Json.append(",\"agentid\":\"" + msgText.getAgentid() + "\"");
			append_Json.append(",\"text\":{");
			append_Json.append("\"content\":\"" + msgText.getContent() + "\"");
			append_Json.append("}");
			append_Json.append(",\"safe\":\"" + msgText.getSafe() + "\"");
			append_Json.append("}");


			String str = PubGlo.HttpPostConnect(url, append_Json.toString(), "POST",true);
			MessageErrorModel postVal = (MessageErrorModel) FormatToJson.<MessageErrorModel>ParseFromJson(str);
			return postVal;
		}
		catch (RuntimeException ex)
		{
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;*/
	}

	/** 
	 发送新闻消息
	 
	 @param msgNews 消息实体类
	 @return 发送消息结果
	*/
	public static MessageErrorModel PostMsgOfNews(MsgNews msgNews)
	{
		String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + msgNews.getAccessToken();
		try
		{
			StringBuilder append_Json = new StringBuilder();
			append_Json.append("{");
			append_Json.append("\"msgtype\":\"news\"");
			//按人员
			if (!bp.da.DataType.IsNullOrEmpty(msgNews.getTouser()))
			{
				append_Json.append(",\"touser\":\"" + msgNews.getTouser() + "\"");
			}
			//按部门
			if (!bp.da.DataType.IsNullOrEmpty(msgNews.getToparty()))
			{
				append_Json.append(",\"toparty\":\"" + msgNews.getToparty() + "\"");
			}
			//标签
			if (!bp.da.DataType.IsNullOrEmpty(msgNews.getTotag()))
			{
				append_Json.append(",\"totag\":\"" + msgNews.getTotag() + "\"");
			}

			append_Json.append(",\"agentid\":\"" + msgNews.getAgentid() + "\"");
			append_Json.append(",\"news\":{");

			append_Json.append("\"articles\":[");
			for (NewsArticles item : msgNews.getArticles())
			{
				append_Json.append("{");
				append_Json.append("\"title\":\"" + item.getTitle() + "\"");
				append_Json.append(",\"description\":\"" + item.getDescription() + "\"");
				if (!bp.da.DataType.IsNullOrEmpty(item.getUrl()))
				{
					append_Json.append(",\"url\":\"" + item.getUrl() + "\"");
				}
				if (!bp.da.DataType.IsNullOrEmpty(item.getPicurl()))
				{
					append_Json.append(",\"picurl\":\"" + item.getPicurl() + "\"");
				}
				append_Json.append("},");
			}
			append_Json.deleteCharAt(append_Json.length() - 1);

			append_Json.append("]}");
			append_Json.append("}");

			String str = PubGlo.HttpPostConnect(url, append_Json.toString(),"POST",true);
			//String str = new HttpWebResponseUtility().HttpResponsePost_Json(url, append_Json.ToString());
			MessageErrorModel postVal = (MessageErrorModel) FormatToJson.<MessageErrorModel>ParseFromJson(str);
			return postVal;
		}
		catch (RuntimeException ex)
		{
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/** 
	 发送待办消息
	 
	 @param WorkID 业务编号
	 @param sender 发送人
	 @return 
	*/
	public static MessageErrorModel PostEmpWorksMsgOfNews(long WorkID, String sender)
	{
		return null;
		/*
		//企业应用必须存在
		String agentId =  bp.difference.SystemConfig.getWX_AgentID() ?? null;
		if (agentId != null)
		{
		    String accessToken = new BP.EAI.Plugins.WXin.WeiXin().getAccessToken();//获取 AccessToken

		    //当前业务
		    GenerWorkFlow gwf = new GenerWorkFlow();
		    gwf.WorkID = WorkID;
		    gwf.RetrieveFromDBSources();
		    //接收人
		    Monitors empWorks = new Monitors();
		    QueryObject obj = new QueryObject(empWorks);
		    obj.AddWhere(MonitorAttr.WorkID, WorkID);
		    obj.addOr();
		    obj.AddWhere(MonitorAttr.FID, WorkID);
		    obj.DoQuery();
		    String toUsers = "";
		    foreach (Monitor empWork in empWorks)
		    {
		        if (toUsers.length() > 0)
		            toUsers += "|";
		        toUsers += empWork.FK_Emp;
		    }
		    if (toUsers.length() == 0)
		        return null;

		    NewsArticles newArticle = new NewsArticles();
		    newArticle.title = gwf.Title;

		    String msgConten = "流程名称：" + gwf.getFlowName() + "\n";
		    msgConten += "发 起 人：" + gwf.StarterName + "\n";
		    msgConten += "发起人部门：" + gwf.DeptName + "\n";
		    msgConten += "当前节点：" + gwf.NodeName + "\n";
		    msgConten += "发 送 人：" + sender + "\n";
		    newArticle.description = msgConten;

		    String New_Url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + bp.difference.SystemConfig.WX_CorpID
		        + "&redirect_uri=" + bp.difference.SystemConfig.WX_MessageUrl + "/CCMobile/action.aspx&response_type=code&scope=snsapi_base&state=empwork_" + WorkID + "#wechat_redirect";
		    newArticle.url = New_Url;

		    //http://discuz.comli.com/weixin/weather/icon/cartoon.jpg
		    //newArticle.picurl =  bp.difference.SystemConfig.WX_MessageUrl + "/DataUser/ICON/" + bp.difference.SystemConfig.SysNo + "/LogBig.png";

		    MsgNews wxMsg = new MsgNews();
		    wxMsg.Access_Token = accessToken;
		    wxMsg.agentid =  bp.difference.SystemConfig.getWX_AgentID();
		    wxMsg.touser = toUsers;
		    wxMsg.articles.Add(newArticle);
		    //执行发送
		    return WeiXinMessage.PostMsgOfNews(wxMsg);
		}
		return null;
		 * */
	}
}
