package cn.jflow.boot.controller;

import bp.da.DataType;
import bp.difference.ContextHolderUtils;
import bp.difference.SystemConfig;
import bp.port.Emp;
import bp.tools.HttpClientUtil;
import bp.wf.GenerWorkFlow;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/ControllerOfMessage")
public class ControllerOfMessage {

    /**
     * 消息类型
     * @return
     */
    public String getDoType() {

        return ContextHolderUtils.getRequest().getParameter("DoType");
    }

    public String getSender() {

        return ContextHolderUtils.getRequest().getParameter("sender");
    }

    public String getSenderTo() {

        return ContextHolderUtils.getRequest().getParameter("sendTo");
    }

    public String getContent() {

        return ContextHolderUtils.getRequest().getParameter("content");
    }

    public String getTel() {

        return ContextHolderUtils.getRequest().getParameter("tel");
    }

    public String getTitle() {

        return ContextHolderUtils.getRequest().getParameter("title");
    }
    public String getOID() {

        return ContextHolderUtils.getRequest().getParameter("oid");
    }

    /**
     * 消息参数
     * @return
     */
    public String getOpenUrl() {

        return ContextHolderUtils.getRequest().getParameter("openUrl");
    }

    @RequestMapping(value = "/SendMessage")
    public boolean SendMessage(HttpServletRequest request, HttpServletResponse response) throws Exception{
        //这里有空指针
        if(DataType.IsNullOrEmpty(this.getDoType()))
            return true;
        /*ServletInputStream in = request.getInputStream();
        String message = readLine(in);
        System.out.println(message);
        //获取参数
        JSONObject jd = JSONObject.fromObject(message);
        String sender="";
        String sendTo="";
        String msgInfo = "";
        String tel ="";
        String openUrl="";
        String title ="";
        if(jd!=null){
            sender = jd.get("sender").toString();
            sendTo = jd.get("sendTo").toString();
            msgInfo = jd.get("content").toString();
            tel = jd.get("tel").toString();
            title = jd.get("title").toString();
            openUrl = jd.get("openUrl").toString();
        }
        //web服务*/
        if(this.getDoType().equals("SendToWebServices")){

        }
//        String doType = this.getDoType();
//        String tel = this.getTel();
//        String title = this.getTitle();
//        String msgInfo = this.getContent();
//        String sender = this.getSender();
        String sendTo = this.getSenderTo();
        Emp emp = new Emp(sendTo);
        //钉钉
        if(this.getDoType().equals("SendToDingDing")) {
            //获取钉应用toekn
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
            OapiGettokenRequest apiRequest = new OapiGettokenRequest();
            //传入参数
            apiRequest.setAppkey(SystemConfig.getDing_AppKey());
            apiRequest.setAppsecret(SystemConfig.getDing_AppSecret());
            apiRequest.setHttpMethod("GET");
            OapiGettokenResponse oapiResponse = client.execute(apiRequest);
            //获取返回结果
            String data=oapiResponse.getBody();
            JSONObject json = JSONObject.fromObject(data);
            //获取token
            String token=json.getString("access_token");

            //发送消息
            client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
            OapiMessageCorpconversationAsyncsendV2Request sendRequest = new OapiMessageCorpconversationAsyncsendV2Request();
            //应用id
            sendRequest.setAgentId(Long.parseLong(SystemConfig.getDing_AgentID()));
            //发送给谁
            sendRequest.setUseridList(emp.getAppUserID());
            sendRequest.setToAllUser(false);

            GenerWorkFlow generWorkFlow=new GenerWorkFlow(Long.parseLong(this.getOID()));

            //发送OA类型的消息
            OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
            //点击消息时的回调地址
            String messageUrl=SystemConfig.getAppSettings().get("messageOfDingDingPath")+"?actionType=todo&fk_flow="+generWorkFlow.getFlowNo()+"&workId="+generWorkFlow.getWorkID()+"&m="+DataType.getCurrentDateTimess();
            msg.setOa(new OapiMessageCorpconversationAsyncsendV2Request.OA());
            msg.getOa().setHead(new OapiMessageCorpconversationAsyncsendV2Request.Head());
            //设置head
            msg.getOa().getHead().setText("审核通知");
            msg.getOa().getHead().setBgcolor("FFBBBBBB");
            //设置消息回调地址
            msg.getOa().setMessageUrl(messageUrl);
            msg.getOa().setBody(new OapiMessageCorpconversationAsyncsendV2Request.Body());
            //设置消息体标题
            msg.getOa().getBody().setTitle(generWorkFlow.getTitle());
            //设置消息体内容
            msg.getOa().getBody().setContent("您有一条待办工作需审核。");
            //设置消息体格式
            List<OapiMessageCorpconversationAsyncsendV2Request.Form> list=new ArrayList<>();
            OapiMessageCorpconversationAsyncsendV2Request.Form form=new OapiMessageCorpconversationAsyncsendV2Request.Form();
            form.setKey("发起人");
            form.setValue(generWorkFlow.getStarterName());
            list.add(form);
            form=new OapiMessageCorpconversationAsyncsendV2Request.Form();
            form.setKey("发送时间");
            form.setValue(DataType.getCurrentDateTime());
            list.add(form);
            msg.getOa().getBody().setForm(list);

            //设置消息类型
            msg.setMsgtype("oa");

            sendRequest.setMsg(msg);

            OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(sendRequest, token);
            System.out.println(rsp.getBody());
            return true;
        }
        //微信
        if(this.getDoType().equals("SendToWeiXin")){
            GenerWorkFlow generWorkFlow=new GenerWorkFlow(Long.parseLong(this.getOID()));
            //WeiXin weiXin = new WeiXin();
            boolean flag=false;
            this.getSenderTo();
            if(!DataType.IsNullOrEmpty(SystemConfig.getWX_AgentID()))
            {
                //第一步，先获取token
                String access_token ="";
                String url="https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+SystemConfig.getWX_CorpID()+"&corpsecret="+SystemConfig.getWX_AppSecret();
                String json= HttpClientUtil.doGet(url);
                JSONObject jsonObject = JSONObject.fromObject(json);
                if(jsonObject.getString("errcode").equals("0")) {
                    access_token = jsonObject.getString("access_token");

                    //第二步，组装消息体
                    String messageUrl=SystemConfig.getAppSettings().get("messageOfQYWXPath").toString();
                    String state="MyFlow_"+generWorkFlow.getFlowNo()+"_"+generWorkFlow.getWorkID();
                    String reddirectUrl="https://open.weixin.qq.com/connect/oauth2/authorize?"
                        +"appid="+SystemConfig.getWX_CorpID()+"&redirect_uri="+messageUrl+""
                            +"&response_type=code&scope=snsapi_base&state="+state+""
                            +"&agentID="+SystemConfig.getWX_AgentID()+"#wechat_redirect";
                    String msgBody="{" +
                            "   \"touser\" : \"UserID1|UserID2|UserID3\"," +
                            "   \"toparty\" : \"PartyID1 | PartyID2\"," +
                            "   \"totag\" : \"TagID1 | TagID2\"," +
                            "   \"msgtype\" : \"textcard\"," +
                            "   \"agentid\" : 1," +
                            "   \"textcard\" : {" +
                            "            \"title\" : \"审核通知\"," +
                            "            \"description\" : \"<div class=\\\"gray\\\">\""+DataType.getCurrentDateTime()+"\"</div> <div class=\\\"normal\\\">\""+getTitle()+"\"</div><div class=\\\"highlight\\\">需要您进行审核。</div>\"," +
                            "            \"url\" : \""+reddirectUrl+"\"," +
                            "                        \"btntxt\":\"查看详情\"" +
                            "   }," +
                            "   \"enable_id_trans\": 0," +
                            "   \"enable_duplicate_check\": 0," +
                            "   \"duplicate_check_interval\": 1800" +
                            "}";

                    //第三步，执行消息推送
                    url="https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token="+access_token;

                    String jsonData=bp.tools.PubGlo.HttpPostConnect(url, msgBody,"POST",true);
                    jsonObject = JSONObject.fromObject(jsonData);
                }

            }
            if(!DataType.IsNullOrEmpty(SystemConfig.getWXGZH_Appid()))
            {
                //第一步，先获取token
                String access_token ="";
                String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + SystemConfig.getWXGZH_Appid() + "&secret=" + SystemConfig.getWXGZH_AppSecret();
                String json= HttpClientUtil.doGet(url);
                JSONObject jsonObject = JSONObject.fromObject(json);
                if(jsonObject.getString("errcode").equals("0")) {
                    access_token = jsonObject.getString("access_token");
                    String redircet_uri = SystemConfig.getAppSettings().get("messageOfWXPath").toString();
                    String state="MyFlow_"+generWorkFlow.getFlowNo()+"_"+generWorkFlow.getWorkID();
                    String reddirectUrl="https://open.weixin.qq.com/connect/oauth2/authorize?"
                        +"appid="+SystemConfig.getWXGZH_Appid()+"&redirect_uri="+redircet_uri+""
                            +"&response_type=code&scope=snsapi_userinfo&state="+state+""
                            +"&connect_redirect=1#wechat_redirect";
                    //需要根据选中的消息模版，定义参数格式
//                    String postData = "{\"touser\":\"" + this.getSenderTo() + "\",\"template_id\":\"" + SystemConfig.getWeiXin_TemplateId() + "\"," +
//                            "\"url\":\"" + redircet_uri + "\",\"data\":{\"thing5\":{\"value\":\"审核通知\"}," +
//                            "\"time3\":{\"value\":\"" + DataType.getCurrentDateTime() + "\"}}}";
                    String postData ="";
                    String jsonData=bp.tools.PubGlo.HttpPostConnect(url, postData,"POST",true);
                    jsonObject = JSONObject.fromObject(jsonData);
                }
            }
            if(flag == false)
                throw new Exception("发送消息失败");
            return true;
        }
        //即时通
        if(this.getDoType().equals("SendToCCMSG")){

        }
        throw new Exception("暂时不支持的消息类型"+this.getDoType());

    }

    private static String readLine(ServletInputStream in) throws Exception {
        byte[] buf = new byte[8 * 1024];
        StringBuffer sbuf = new StringBuffer();
        int result;

        do {
            result = in.readLine(buf, 0, buf.length); // does +=
            if (result != -1) {
                sbuf.append(new String(buf, 0, result, "UTF-8"));
            }
        } while (result == buf.length);

        if (sbuf.length() == 0) {
            return null;
        }

        int len = sbuf.length();
        if (sbuf.charAt(len - 2) == '\r') {
            sbuf.setLength(len - 2); // cut \r\n
        } else {
            sbuf.setLength(len - 1); // cut \n
        }
        return sbuf.toString();
    }

}
