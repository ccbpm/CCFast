package bp.difference.handler;


import bp.cloud.Emp;
import bp.da.DataType;
import bp.da.LogType;
import bp.difference.SystemConfig;
import bp.port.EmpAttr;
import bp.sys.CCBPMRunModel;
import bp.tools.HttpClientUtil;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;

public class CCMobileHandler  extends bp.difference.handler.DirectoryPageBase{
    /**
     * 获取钉钉企业ID
     * @return
     */
    public String getDingCorpID(){
        return SystemConfig.getDing_CorpID();
    }
    /**
     * 钉钉验证
     *
     * @return
     * @throws Exception
     */
    public String getDingTalkUserInfo()throws Exception {
        try {
            HashMap<String,Object> resultMap=new HashMap<>();
            String randomCode = this.GetRequestVal("rcode");
            //获取token
            String token = "";
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "钉钉Code:" + randomCode);
            String webPath = "https://oapi.dingtalk.com/gettoken?appkey=" + SystemConfig.getDing_AppKey() + "&appsecret=" + SystemConfig.getDing_AppSecret();
            //增加header参数
            java.util.Map<String, String> headerMap = new Hashtable<String, String>();
            headerMap.put("Content-Type", "application/json");
            String tokenData = HttpClientUtil.doGet(webPath);
            JSONObject tokenJson = JSONObject.fromObject(tokenData);
            if (tokenJson.getString("errcode").equals("0"))
                token = tokenJson.getString("access_token");
            else
                return "err@:获取token出错";

            //获取人员信息
            String url = "https://oapi.dingtalk.com/user/getuserinfo?access_token=" + token + "&code=" + randomCode;
            String json = HttpClientUtil.doGet(url);
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "钉钉JSON:" + json);
            JSONObject jd = JSONObject.fromObject(json);
            //根据userid获取userinfo
            if (!DataType.IsNullOrEmpty(jd.getString("userid"))) {
                String userUrl = "https://oapi.dingtalk.com/topapi/v2/user/get?access_token=" + token + "&userid=" + jd.getString("userid");
                String userData=HttpClientUtil.doGet(userUrl);
                JSONObject userInfo = JSONObject.fromObject(userData);
                if (userInfo.getString("errcode").equals("0")) {
                    JSONObject userResult = userInfo.getJSONObject("result");
                    String tel = userResult.getString("mobile");
                    bp.wf.weixin.Emp emp=new bp.wf.weixin.Emp();
                    if (emp.IsExit(EmpAttr.Tel, tel)) {
                        String userToken="";
                        if(SystemConfig.getCCBPMRunModel()== CCBPMRunModel.SAAS){
                            bp.wf.Dev2Interface.Port_Login(emp.getNo());
                            userToken=bp.wf.Dev2Interface.Port_GenerToken();
                        }else{
                            userToken=bp.wf.Dev2Interface.Port_GenerToken(emp.getNo());
                            bp.wf.Dev2Interface.Port_Login(emp.getNo());
                        }

                        resultMap.put("token",userToken);
                        resultMap.put("userNo",emp.getNo());
                    }
                } else
                    return "err@:获取用户信息出错";
            } else {
                bp.wf.weixin.Emp emp=new bp.wf.weixin.Emp();
                if (emp.IsExit(EmpAttr.UserID, jd.getString("userid"))) {
                    String userToken="";
                    if(SystemConfig.getCCBPMRunModel()== CCBPMRunModel.SAAS){
                        bp.wf.Dev2Interface.Port_Login(emp.getNo());
                        userToken=bp.wf.Dev2Interface.Port_GenerToken();
                    }else{
                        userToken=bp.wf.Dev2Interface.Port_GenerToken(emp.getNo());
                        bp.wf.Dev2Interface.Port_Login(emp.getNo());
                    }
                    resultMap.put("token",userToken);
                    resultMap.put("userNo",emp.getNo());
                }
            }
            System.out.println("返回信息:" +resultMap.toString());
            return bp.tools.Json.ToJson(resultMap);
        } catch (Exception ex) {
            System.out.println("钉钉请求异常信息:" +ex.toString());
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "钉钉请求异常信息:" + ex.toString());
            return "err@:获取用户id出错";
        }
    }
    /**
     * WeChatEnterprise自动登录
     * @return
     * @throws Exception
     */
    public String Auto_Login() throws Exception{
        try {
            HashMap<String,Object> resultMap=new HashMap<>();

            String code = this.GetRequestVal("dcode");
            String toPage=this.GetRequestVal("state");

            String url="https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+SystemConfig.getWX_CorpID()+"&corpsecret="+SystemConfig.getWX_AppSecret();
            String json=HttpClientUtil.doGet(url);
            JSONObject jsonObject = JSONObject.fromObject(json);
            if(!jsonObject.getString("errcode").equals("0"))
                return "err@:获取token失败";

            String access_token = jsonObject.getString("access_token");
            System.out.println(json);

            // 第四步：拉取用户信息
            String infoUrl = "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token=" + access_token
                    + "&code=" + code;
            String info=HttpClientUtil.doGet(infoUrl);
            JSONObject userInfo = JSONObject.fromObject(info);
            if(!userInfo.getString("errcode").equals("0"))
                return "err@:获取用户失败";

            String userid=userInfo.getString("userid");
            bp.wf.weixin.Emp emp=new bp.wf.weixin.Emp();
            if(emp.IsExit(EmpAttr.UserID,userid)){
                String token="";
                if(SystemConfig.getCCBPMRunModel()== CCBPMRunModel.SAAS){
                    bp.wf.Dev2Interface.Port_Login(emp.getNo());
                    token=bp.wf.Dev2Interface.Port_GenerToken();
                }else{
                    token=bp.wf.Dev2Interface.Port_GenerToken(emp.getNo());
                    bp.wf.Dev2Interface.Port_Login(emp.getNo());
                }
                resultMap.put("token",token);
                resultMap.put("userNo",emp.getNo());
            }
            else{
                String webPath="https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token="+access_token+"&userid="+userid;
                info=HttpClientUtil.doGet(webPath);
                System.out.println(info);
                userInfo = JSONObject.fromObject(info);
                String tel=userInfo.getString("mobile");
                if(emp.IsExit(EmpAttr.Tel,tel)){
                    String token="";
                    if(SystemConfig.getCCBPMRunModel()== CCBPMRunModel.SAAS){
                        bp.wf.Dev2Interface.Port_Login(emp.getNo());
                        token=bp.wf.Dev2Interface.Port_GenerToken();
                    }else{
                        token=bp.wf.Dev2Interface.Port_GenerToken(emp.getNo());
                        bp.wf.Dev2Interface.Port_Login(emp.getNo());
                    }
                    resultMap.put("token",token);
                    resultMap.put("userNo",emp.getNo());
                }
            }
            resultMap.put("page",toPage);
            System.out.println(resultMap.toString());
            return bp.tools.Json.ToJson(resultMap);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage().toString());
            return "err@"+ex.getMessage();
        }
    }
}
