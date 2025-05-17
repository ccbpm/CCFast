package bp.wf.weixin;

import bp.da.*;
import bp.tools.*;
import bp.wf.weixin.gzh.WeiXinGZHModel;

public class WeiXinGZHEntity
{

		///#region 基本配置.
	/** 
	 微信公众号应用的分配的单位ID.
	 格式:wx8eac6a18c5efec30
	*/
	public static String getAppid()
	{
		return bp.difference.SystemConfig.getWXGZH_Appid(); // "wx8eac6a18c5efec30";
	}
	/** 
	 微信公众号开发则密码.
	*/
	public static String getAppSecret()
	{
		return bp.difference.SystemConfig.getWXGZH_AppSecret(); // "KfFkE9AZ3Zp09zTuKvmqWLgtLj-_cHMPTvV992apOWgSKJHcbjpbu1jYVXh7gI7K";
	}

		///#endregion 基本配置.


		///#region 获取用户access_token
	/** 
	 获得token,每间隔x分钟，就会失效.
	 
	 @return token
	*/
	public static WeiXinGZHModel.AccessToken getAccessToken(String code) throws Exception {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + getAppid() + "&secret=" + getAppSecret() + "&code=" + code + "&grant_type=authorization_code";

		WeiXinGZHModel.AccessToken at = new WeiXinGZHModel.AccessToken();
		String str = DataType.ReadURLContext(url, 5000);
		at = (WeiXinGZHModel.AccessToken) FormatToJson.ParseFromJson(str);

		return at;
	}
	/** 
	 微信网页开发获取token
	 
	 @return 
	*/
	public static WeiXinGZHModel.AccessToken getAccessToken() throws Exception {
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + getAppid() + "&secret=" + getAppSecret();

		WeiXinGZHModel.AccessToken at = new WeiXinGZHModel.AccessToken();
		String str = DataType.ReadURLContext(url, 5000);
		at = (WeiXinGZHModel.AccessToken) FormatToJson.ParseFromJson(str);
		return at;
	}

		///#endregion 获取用户access_token


		///#region 获取用户信息
	public static WeiXinGZHModel.GZHUser getUserInfo(String access_token, String openid) throws Exception {
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid + "&lang=zh_CN";

		WeiXinGZHModel.GZHUser user = new WeiXinGZHModel.GZHUser();
		String str = DataType.ReadURLContext(url, 5000);
		user = (WeiXinGZHModel.GZHUser) FormatToJson.ParseFromJson(str);
		return user;
	}

		///#endregion 获取用户信息
}
