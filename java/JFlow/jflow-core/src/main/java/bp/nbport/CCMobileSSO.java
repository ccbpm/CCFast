package bp.nbport;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.port.Emp;
import bp.tools.HttpClientUtil;
import bp.tools.Json;
import bp.web.WebUser;

import java.util.Hashtable;

/**
 * 移动端第三方单点登录、页面路由配置
 */
public class CCMobileSSO extends bp.difference.handler.DirectoryPageBase {
    private Object getAccessTokenURL = SystemConfig.getAppSettings().get("NBGGetAccessTokenURL");
    private Object getUserURL = SystemConfig.getAppSettings().get("NBGGetUserURL");

    /**
     * 构造函数
     */
    public CCMobileSSO() throws Exception {
        WebUser.setSheBei("Mobile");
    }

    public static Object Return_Info(String path, String param) {
        Hashtable ht = new Hashtable();
        ht.put("code", 100);
        ht.put("path", path);
        ht.put("param", param);
        return ht;
    }

    public static Object Return_Error(String path) {
        Hashtable ht = new Hashtable();
        ht.put("code", 200);
        ht.put("path", path);
        ht.put("param", "");
        return ht;
    }

    /**
     * 用户通过点击浙港通的工作台的这个入口进入到工作流首页，进行流程的发起，在这之前，工作流需要获取点进的用户
     *
     * @return
     * @throws Exception
     */
    public final String getUserInfo() throws Exception {
        String ticket = this.GetRequestVal("Ticket");
        // TODO 测试时使用
        ticket = "e220619ec21a4305a81d7106ac9153ba";
        if (DataType.IsNullOrEmpty(ticket) == true) {
            return "err@ticket未传值，请联系管理员。";
        }

        String appId = SystemConfig.getAppSettings().get("NBGID").toString();
        String appSecret = SystemConfig.getAppSettings().get("NBGSecret").toString();
        String userNo = "";
        try {
            // 获取AccessToken，根据轻应用 id 和轻应用 secret 换取接口令牌，用于调用各类接口
            String accessToken = getAccessToken(appId, appSecret);
            // 根据客户端生成的 ticket、外部企业自建应用 appId 获取当前用户上下文信息
            userNo = getAppUserInfo(accessToken, appId, ticket);
        }catch ( Exception ex){
            return Json.ToJson(Return_Error(ex.getMessage()));
        }

        //登录
        Emp myEmp = new Emp();
        int i = myEmp.Retrieve("no", userNo);
        if (i == 0) {
            return Json.ToJson(Return_Error("err@不存在的用户信息.No=" + userNo ));
        }
        bp.wf.Dev2Interface.Port_Login(userNo, myEmp.getOrgNo());
        String token = bp.wf.Dev2Interface.Port_GenerToken();

        String doWhat = this.GetRequestVal("DoWhat");
        if (DataType.IsNullOrEmpty(doWhat) == false) {
            //解析DoParam参数
            String doParam = this.GetRequestVal("DoParam");
            //将{xx:xx,xx:xx}转为&xx=xx&xx=xx
            doParam = doParam.replace("{", "&").replace("}", "").replace(",", "&").replace(":", "=");
            //根据DoWhat判断跳转路由
            if (doWhat.equals("DealWork")) {
                return Json.ToJson(Return_Info("CCMobile/MyFlow", "Token=" + token + doParam));
            }
            //在此增加其他页面的跳转,如下
            //if(doWhat.equals("XXXX")){
            //    return Json.ToJson(Return_Info("CCMobile/XXXX", "Token=" + token + "&UserNo=" + userNo + doParam));
            //}
            return Json.ToJson(Return_Error("没有判断的类型，DoWhat=" + doWhat));
        } else {
            //验证Ticket后并不存在DoWhat参数时转到移动端首页
            return Json.ToJson(Return_Info("CCMobilePortal/Home", "Token=" + token));
        }

    }

    public String getAccessToken(String appId, String appSecret) throws Exception {

        java.util.Map<String, String> headerMap = new Hashtable<String, String>();
        headerMap.put("Content-Type", "application/json");
        String param = "";
        param += "{";
        param += "\"appId\":\"" + appId + "\",";
        param += "\"secret\":\"" + appSecret + "\",";
        param += "\"timestamp\":\"" + System.currentTimeMillis() + "\",";
        param += "\"scope\":\"app\"";
        param += "}";

        if (DataType.IsNullOrEmpty(getAccessTokenURL)) {
            throw new RuntimeException("err@未配置NBGGetAccessTokenURL，请检查jflow.properties");
        }

        String gettokenData = HttpClientUtil.doPost(getAccessTokenURL.toString(), param, headerMap);
        net.sf.json.JSONObject j = net.sf.json.JSONObject.fromObject(gettokenData);
        //请求失败，
        if (!j.get("success").toString().equals("true")) {
            bp.da.Log.DefaultLogWriteLine(LogType.Error, "获取AccessToken失败:" + j.toString());
            String msg = "该模块访问令牌失效，请联系管理员。URL:" + getAccessTokenURL + ",param:" + param;
            throw new RuntimeException(msg);
        }
        String tokenData = net.sf.json.JSONObject.fromObject(j.get("data")).get("accessToken").toString();

        return tokenData;
    }

    public String getAppUserInfo(String accessToken, String appId, String ticket) throws Exception {
        if (DataType.IsNullOrEmpty(getUserURL)) {
            throw new RuntimeException("err@未配置NBGGetUserURL，请检查jflow.properties");
        }
        String url = getUserURL.toString() + "?accessToken=" + accessToken;
        java.util.Map<String, String> headerMap = new Hashtable<String, String>();
        //Base64 base64 = new Base64();
        headerMap.put("Content-Type", "application/json");
        String param = "";
        param += "{";
        param += "\"appid\":\"" + appId + "\",";
        param += "\"ticket\":\"" + ticket + "\"";
        param += "}";

        String getUserData = HttpClientUtil.doPost(url, param, headerMap);
        net.sf.json.JSONObject j = net.sf.json.JSONObject.fromObject(getUserData);
        //请求失败，
        if (j.get("success") != null && !j.get("success").toString().equals("true")) {
            bp.da.Log.DefaultLogWriteLine(LogType.Error, "根据客户端生成的 ticket、外部企业自建应用 appId 获取当前用户上下文信息失败:" + j);
            String msg = "请求地址：" + url + "， 参数：" + param + "，响应：" + getUserData + "。获取当前用户信息失败！";
            throw new RuntimeException(msg);
        }
        String userData = j.get("data").toString();
        net.sf.json.JSONObject userJson = net.sf.json.JSONObject.fromObject(userData);
        String userId = userJson.get("mobile").toString();
        return userId;
    }
}
