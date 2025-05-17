package bp.gpm.weixin;

import bp.da.*;
import bp.gpm.weixin.msg.*;
import bp.*;
import java.util.*;

public class AccessToken
{
	private String access_token;
	public final String getAccessToken()
	{
		return access_token;
	}
	public final void setAccessToken(String value)
	{
		access_token = value;
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
