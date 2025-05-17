package bp.gpm.weixin;

import bp.da.*;
import bp.gpm.weixin.msg.*;
import bp.*;
import java.util.*;

public class MessageErrorModel
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
	private String invaliduser;
	public final String getInvaliduser()
	{
		return invaliduser;
	}
	public final void setInvaliduser(String value)
	{
		invaliduser = value;
	}
	private String invalidparty;
	public final String getInvalidparty()
	{
		return invalidparty;
	}
	public final void setInvalidparty(String value)
	{
		invalidparty = value;
	}
	private String invalidtag;
	public final String getInvalidtag()
	{
		return invalidtag;
	}
	public final void setInvalidtag(String value)
	{
		invalidtag = value;
	}
}
