package bp.gpm.weixin;

import bp.da.*;
import bp.gpm.weixin.msg.*;
import bp.*;
import java.util.*;

/** 
 获取企业的jsapi_ticket
*/
public class Ticket
{
	private String errcode;
	public final String getErrcode()
	{
		return errcode;
	}
	public final void setErrcode(String value)
	{
		errcode = value;
	}
	private String errmsg;
	public final String getErrmsg()
	{
		return errmsg;
	}
	public final void setErrmsg(String value)
	{
		errmsg = value;
	}
	private String ticket;
	public final String getTicket()
	{
		return ticket;
	}
	public final void setTicket(String value)
	{
		ticket = value;
	}
	private String expires_in;
	public final String getExpiresIn()
	{
		return expires_in;
	}
	public final void setExpiresIn(String value)
	{
		expires_in = value;
	}
}
