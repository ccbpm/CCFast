package bp.gpm.weixin.msg;

import bp.*;
import bp.gpm.weixin.*;

/** 
 微信-消息公共类
*/
public abstract class MsgBase
{
	/** 
	 必须：是- 调用接口凭证
	*/
	private String Access_Token;
	public final String getAccessToken()
	{
		return Access_Token;
	}
	public final void setAccessToken(String value)
	{
		Access_Token = value;
	}
	/** 
	 必须：否- 成员ID列表（消息接收者，多个接收者用'|'分隔，最多支持1000个）。特殊情况：指定为@all，则向关注该企业应用的全部成员发送 
	*/
	private String touser;
	public final String getTouser()
	{
		return touser;
	}
	public final void setTouser(String value)
	{
		touser = value;
	}
	/** 
	 必须：否- 部门ID列表，多个接收者用'|'分隔，最多支持100个。当touser为@all时忽略本参数 
	*/
	private String toparty;
	public final String getToparty()
	{
		return toparty;
	}
	public final void setToparty(String value)
	{
		toparty = value;
	}
	/** 
	 必须：否- 标签ID列表，多个接收者用'|'分隔。当touser为@all时忽略本参数 
	*/
	private String totag;
	public final String getTotag()
	{
		return totag;
	}
	public final void setTotag(String value)
	{
		totag = value;
	}
	/** 
	 必须：是- 企业应用的id，整型。可在应用的设置页面查看 
	*/
	private String agentid;
	public final String getAgentid()
	{
		return agentid;
	}
	public final void setAgentid(String value)
	{
		agentid = value;
	}
	/** 
	 必须：否- ccflow 业务ID
	*/
	private String WorkID;
	public final String getWorkID()
	{
		return WorkID;
	}
	public final void setWorkID(String value)
	{
		WorkID = value;
	}

	/** 
	 表示是否是保密消息
	*/
	private String _Safe = "0";
	/** 
	 必须：否- 表示是否是保密消息，0表示否，1表示是，默认0
	*/
	public final String getSafe()
	{
		return this._Safe;
	}
	public final void setSafe(String value)
	{
		this._Safe = value;
	}
}
