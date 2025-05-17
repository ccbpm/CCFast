package bp.difference.handler;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import bp.da.DBAccess;
import bp.da.DataTable;
import bp.da.DataType;
import bp.da.Paras;
import bp.difference.SystemConfig;
import bp.tools.HttpClientUtil;
import net.sf.json.JSONObject;


@Controller
@RequestMapping("/WF/WXZFH")
@Scope("request") 
public class SignClientController {
	@RequestMapping(value="SignClient.do",method=RequestMethod.GET)
    public void callBack(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try
		{
			System.out.println("开始校验");
			String code=req.getParameter("code");
			System.out.println("code:"+code);
	        /* start 获取微信用户基本信息  */
	        if (DataType.IsNullOrEmpty(code)) {
	        	resp.getWriter().write("@err无法获取code");;
	        }
	        //第二步：通过code换取网页授权access_token
	        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + SystemConfig.getWXGZH_Appid()
	                + "&secret=" + SystemConfig.getWXGZH_AppSecret()
	                + "&code=" + code
	                + "&grant_type=authorization_code";

	        String json=HttpClientUtil.doGet(url);
	        JSONObject jsonObject = JSONObject.fromObject(json);
	        String openid = jsonObject.get("openid").toString();
	        System.out.println("openid:"+openid);
	        if (DataType.IsNullOrEmpty(openid)) {
	        	resp.getWriter().write("@err无法获取openid");
	        }
	        String access_token = jsonObject.getString("access_token");
	        String refresh_token = jsonObject.getString("refresh_token");
	       

	        //第五步验证access_token是否失效；展示都不需要
	        String chickUrl = "https://api.weixin.qq.com/sns/auth?access_token=" + access_token + "&openid=" + openid;
	        
	        String userInfojson=HttpClientUtil.doGet(chickUrl);
	        JSONObject chickuserInfo = JSONObject.fromObject(userInfojson);
//	        System.out.println(chickuserInfo.toString());
	        if (!"0".equals(chickuserInfo.getString("errcode"))) {
	            // 第三步：刷新access_token（如果需要）-----暂时没有使用,参考文档https://mp.weixin.qq.com/wiki，
	            String refreshTokenUrl = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + openid + "&grant_type=refresh_token&refresh_token=" + refresh_token;

	            String tokenJson=HttpClientUtil.doGet(refreshTokenUrl);
	            JSONObject refreshInfo = JSONObject.fromObject(tokenJson);
	             /*
	              * { "access_token":"ACCESS_TOKEN",
	                 "expires_in":7200,
	                 "refresh_token":"REFRESH_TOKEN",
	                 "openid":"OPENID",
	                 "scope":"SCOPE" }
	              */
	            access_token = refreshInfo.getString("access_token");
	        }
	        System.out.println("access_token:"+access_token);
	        // 第四步：拉取用户信息(需scope为 snsapi_userinfo)
	        String infoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token
	                + "&openid=" + openid
	                + "&lang=zh_CN";
	        String info=HttpClientUtil.doGet(infoUrl);
	        JSONObject userInfo = JSONObject.fromObject(info);
	         /*
	          {    "openid":" OPENID",
	             " nickname": NICKNAME,
	             "sex":"1",
	             "province":"PROVINCE"
	             "city":"CITY",
	             "country":"COUNTRY",
	             "headimgurl":    "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46",
	             "privilege":[ "PRIVILEGE1" "PRIVILEGE2"     ],
	             "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
	             }
	          */      
	        String nickname = userInfo.getString("nickname");//名字
	        System.out.println("nickname:"+nickname);
	        
	        Paras ps=new Paras();
	        String dbstr = SystemConfig.getAppCenterDBVarStr();
	        ps.SQL="SELECT No,Name from Port_Emp where Wei_UserID="+dbstr+"Wei_UserID ";
	        ps.Add("Wei_UserID", openid);
	        DataTable dt=DBAccess.RunSQLReturnTable(ps);
	        
	        System.out.println("Wei_UserID:"+dt.Rows.size());
	        
	        if(dt.Rows.size()<=0){
	        	System.out.println("returnmsg:Logon.htm?openid="+openid);
	        	resp.getWriter().write("Logon.htm?openid="+openid);
	        }
	        else{
	        	bp.wf.Dev2Interface.Port_Login(dt.Rows.get(0).getValue("No").toString());
	        	System.out.println("returnmsg:Home.htm");
	        	resp.getWriter().write("Home.htm");
	        }
		}
		catch(Exception ex){
			System.out.println("err:"+ex.getMessage());
			resp.getWriter().write("@err"+ex.getMessage());
		}
	}
	
}
