package bp.wf;

import bp.da.*;
import bp.port.Emp;
import bp.sys.*;
import bp.tools.HttpClientUtil;
import bp.wf.template.*;
import bp.difference.*;
import com.aliyun.dingtalktodo_1_0.models.*;
import com.aliyun.tea.*;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 事件重写.
 */
public class OverrideEvent {

    public static String SendToEmail(SMS sms) {
        return "";
    }

    public static String SendToMobile(SMS sms) {
        return "";
    }

    public static String SendToDingDing(SMS sms) throws Exception {
        bp.da.Log.DefaultLogWriteLine(LogType.Info, "发送钉钉消息");
        String sendTo = sms.getSendToEmpNo();
        Emp emp = new Emp(sendTo);
        //获取钉应用toekn
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest apiRequest = new OapiGettokenRequest();
        //传入参数
        apiRequest.setAppkey(SystemConfig.getDing_AppKey());
        apiRequest.setAppsecret(SystemConfig.getDing_AppSecret());
        apiRequest.setHttpMethod("GET");
        OapiGettokenResponse oapiResponse = client.execute(apiRequest);
        //获取返回结果
        String data = oapiResponse.getBody();
        JSONObject json = JSONObject.fromObject(data);
        bp.da.Log.DefaultLogWriteLine(LogType.Info, "获取钉钉Token" + json);
        //获取token
        String token = json.getString("access_token");

        //发送消息
        client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request sendRequest = new OapiMessageCorpconversationAsyncsendV2Request();
        //应用id
        sendRequest.setAgentId(Long.parseLong(SystemConfig.getDing_AgentID()));
        //发送给谁
        if (!DataType.IsNullOrEmpty(emp.getAppUserID())) {
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "钉钉:发送给USERID----" + emp.getAppUserID());
            sendRequest.setUseridList(emp.getAppUserID());
            sms.setSendToEmpNo(emp.getAppUserID());
        } else {
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "钉钉:发送给No-" + emp.getNo());
            sendRequest.setUseridList(emp.getNo());
            sms.setSendToEmpNo(emp.getNo());
        }
        sendRequest.setToAllUser(false);

        bp.da.Log.DefaultLogWriteLine(LogType.Info, "钉钉:发送给No-" + sendRequest.toString());

        GenerWorkFlow generWorkFlow = new GenerWorkFlow(sms.getWorkID());


        //发送OA类型的消息
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        //点击消息时的回调地址
        String messageUrl = SystemConfig.getAppSettings().get("messageOfDingDingPath") + "?actionType=todo&fk_flow=" + generWorkFlow.getFlowNo() + "&workId=" + generWorkFlow.getWorkID() + "&m=" + DataType.getCurrentDateTimess();
        msg.setOa(new OapiMessageCorpconversationAsyncsendV2Request.OA());
        msg.getOa().setHead(new OapiMessageCorpconversationAsyncsendV2Request.Head());
        //设置head
        if (generWorkFlow.getWFState() == WFState.CompleteEnd)
            msg.getOa().getHead().setText("审核已完成");
        else
            msg.getOa().getHead().setText("审核通知");
        msg.getOa().getHead().setBgcolor("FFBBBBBB");
        //设置消息回调地址
        msg.getOa().setMessageUrl(messageUrl);
        msg.getOa().setBody(new OapiMessageCorpconversationAsyncsendV2Request.Body());
        //设置消息体标题
        msg.getOa().getBody().setTitle(generWorkFlow.getTitle());
        //设置消息体内容
        if (generWorkFlow.getWFState() == WFState.CompleteEnd)
            msg.getOa().getBody().setContent(generWorkFlow.getTitle() + "已完成。");
        else
            msg.getOa().getBody().setContent("您有一条待办工作需审核。");
        //设置消息体格式
        List<OapiMessageCorpconversationAsyncsendV2Request.Form> list = new ArrayList<>();
        OapiMessageCorpconversationAsyncsendV2Request.Form form = new OapiMessageCorpconversationAsyncsendV2Request.Form();
        form.setKey("发起人");
        form.setValue(generWorkFlow.getStarterName());
        list.add(form);
        form = new OapiMessageCorpconversationAsyncsendV2Request.Form();
        form.setKey("发送时间");
        form.setValue(DataType.getCurrentDateTime());
        list.add(form);
        msg.getOa().getBody().setForm(list);

        //设置消息类型
        msg.setMsgtype("oa");

        sendRequest.setMsg(msg);

        bp.da.Log.DefaultLogWriteLine(LogType.Info, "钉钉消息----" + messageUrl);

        OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(sendRequest, token);
        System.out.println(rsp.getBody());
        bp.da.Log.DefaultLogWriteLine(LogType.Info, "发送钉钉消息" + rsp.getBody());
        SendToDingDingTodoList(sms);
        return "发送成功";
    }

    public static com.aliyun.dingtalktodo_1_0.Client createClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalktodo_1_0.Client(config);
    }


    public static String SendToDingDingTodoList(SMS sms) throws Exception {
        GenerWorkFlow generWorkFlow = new GenerWorkFlow(sms.getWorkID());
        if (generWorkFlow.getWFState() == WFState.CompleteEnd) {
            DeleteDingDingTodoList(generWorkFlow);
            return "";
        }
        com.aliyun.dingtalktodo_1_0.Client client = createClient();

        // 用老版本的api获取token
        DingTalkClient clientToken = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest apiRequest = new OapiGettokenRequest();
        //传入参数
        apiRequest.setAppkey(SystemConfig.getDing_AppKey());
        apiRequest.setAppsecret(SystemConfig.getDing_AppSecret());
        apiRequest.setHttpMethod("GET");
        OapiGettokenResponse oapiResponse = clientToken.execute(apiRequest);
        //获取返回结果
        String data = oapiResponse.getBody();
        JSONObject json = JSONObject.fromObject(data);
        bp.da.Log.DefaultLogWriteLine(LogType.Info, "获取钉钉Token" + json);
        //获取token
        String token = json.getString("access_token");

        //获取接收人unionId
        DingTalkClient client1 = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
        OapiV2UserGetRequest req = new OapiV2UserGetRequest();
        req.setUserid(sms.getSendToEmpNo());
        req.setLanguage("zh_CN");
        OapiV2UserGetResponse rsp = client1.execute(req, token);
        JSONObject userJson = JSONObject.fromObject(rsp.getBody());
        JSONObject result = userJson.getJSONObject("result");
        String unionId = result.getString("unionid");

        //使用人员帐号、workid、流程编号、节点id组织成业务taskId
        String sourceId = sms.getSendToEmpNo() + "_" + generWorkFlow.getWorkID() + "_" + generWorkFlow.getFlowNo() + "_" + generWorkFlow.getNodeID();

        CreateTodoTaskRequest.CreateTodoTaskRequestNotifyConfigs notifyConfigs = new CreateTodoTaskRequest.CreateTodoTaskRequestNotifyConfigs()
                .setDingNotify("1");

        List<CreateTodoTaskRequest.CreateTodoTaskRequestContentFieldList> contentFieldLists = new ArrayList<>();
        //详情页url跳转地址。
        String messageUrl = SystemConfig.getAppSettings().get("messageOfDingDingPath") + "?actionType=todo&fk_flow=" + generWorkFlow.getFlowNo() + "&workId=" + generWorkFlow.getWorkID() + "&m=" + DataType.getCurrentDateTimess();
        CreateTodoTaskRequest.CreateTodoTaskRequestDetailUrl detailUrl = new CreateTodoTaskRequest.CreateTodoTaskRequestDetailUrl()
                .setAppUrl(messageUrl)
                .setPcUrl(messageUrl);

        //设置todo header以及request
        com.aliyun.dingtalktodo_1_0.models.CreateTodoTaskHeaders createTodoTaskHeaders = new com.aliyun.dingtalktodo_1_0.models.CreateTodoTaskHeaders();
        createTodoTaskHeaders.setXAcsDingtalkAccessToken(token);
        //设置request
        com.aliyun.dingtalktodo_1_0.models.CreateTodoTaskRequest createTodoTaskRequest = new com.aliyun.dingtalktodo_1_0.models.CreateTodoTaskRequest();
        // sourceId   业务系统侧的唯一标识ID，即业务ID
        createTodoTaskRequest.setSourceId(sourceId);
        createTodoTaskRequest.setOperatorId(unionId);
        createTodoTaskRequest.setCreatorId(unionId);
        //标题
        createTodoTaskRequest.setSubject(generWorkFlow.getTitle());
        //根据关键字生成待办模版
        GERpt rpt = new GERpt("ND" + Integer.parseInt(generWorkFlow.getFlowNo()) + "Rpt");
        rpt.Retrieve(GERptAttr.OID, generWorkFlow.getWorkID());
        Node nd = new Node(generWorkFlow.getNodeID());
        String exp = nd.getFocusField() + ";";
        if (!DataType.IsNullOrEmpty(exp)) {
            String[] des = exp.split(";");
            for (String item : des) {
                if (DataType.IsNullOrEmpty(item))
                    continue;
                String itemVal = bp.wf.Glo.DealExp(item, rpt, null);
                String[] strs = itemVal.split(":");
                CreateTodoTaskRequest.CreateTodoTaskRequestContentFieldList contentFieldList0 =
                        new CreateTodoTaskRequest.CreateTodoTaskRequestContentFieldList();
                contentFieldList0.setFieldKey(strs[0]);
                contentFieldList0.setFieldValue(strs[1]);
                contentFieldLists.add(contentFieldList0);
            }
        }

        // 描述
        createTodoTaskRequest.setDescription("合同名称");

        // 执行者的unionId，不填，无法显示待办。
        createTodoTaskRequest.setExecutorIds(java.util.Arrays.asList(
                unionId
        ));

        createTodoTaskRequest.setContentFieldList(contentFieldLists);
        createTodoTaskRequest.setDetailUrl(detailUrl);

        // 生成的待办是否仅展示在执行者的待办列表中。
        createTodoTaskRequest.setIsOnlyShowExecutor(true);
        // 优先级，取值：20 普通
        createTodoTaskRequest.setPriority(20);
        // notifyConfigs  DING通知配置，目前仅支持取值为1，表示应用内DING。
        createTodoTaskRequest.setNotifyConfigs(notifyConfigs);


        try {
            //创建之前，先删除原有待办，避免重复和垃圾数据
            DeleteDingDingTodoList(generWorkFlow);
            // 执行创建
            com.aliyun.dingtalktodo_1_0.models.CreateTodoTaskResponse response = client.createTodoTaskWithOptions(unionId, createTodoTaskRequest, createTodoTaskHeaders, new com.aliyun.teautil.models.RuntimeOptions());
            String msg = response.getBody().getId();
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "待办信息" + JSONObject.fromObject(response.getBody()));
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }

        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }

        }
        return "发送成功";
    }


    public static String DeleteDingDingTodoList(GenerWorkFlow gwf) throws Exception {
        com.aliyun.dingtalktodo_1_0.Client client = createClient();

        // 用老版本的api获取token
        DingTalkClient clientToken = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest apiRequest = new OapiGettokenRequest();
        //传入参数
        apiRequest.setAppkey(SystemConfig.getDing_AppKey());
        apiRequest.setAppsecret(SystemConfig.getDing_AppSecret());
        apiRequest.setHttpMethod("GET");
        OapiGettokenResponse oapiResponse = clientToken.execute(apiRequest);
        //获取返回结果
        String data = oapiResponse.getBody();
        JSONObject json = JSONObject.fromObject(data);
        bp.da.Log.DefaultLogWriteLine(LogType.Info, "获取钉钉Token" + json);
        //获取token
        String token = json.getString("access_token");

        //获取上一步的处理人
        String sql = "select NDFrom from ND" + Integer.parseInt(gwf.getFlowNo()) + "Track where WorkID='" + gwf.getWorkID() + "' and NDTo='" + gwf.getNodeID() + "' Order By RDT DESC LIMIT 0,1";
        String ndTo = bp.da.DBAccess.RunSQLReturnString(sql);
        String empSql = "select EmpTo from ND" + Integer.parseInt(gwf.getFlowNo()) + "Track where WorkID='" + gwf.getWorkID() + "' and NDTo='" + ndTo + "' and (ActionType=1 or ActionType=2) Order By RDT DESC LIMIT 0,1";
        String empTo = bp.da.DBAccess.RunSQLReturnString(empSql);

        if (DataType.IsNullOrEmpty(empTo))
            return "";

        String[] emps = empTo.split(",");
        for (String empStr : emps) {
            if (DataType.IsNullOrEmpty(empStr))
                continue;

            //获取人员unionid
            DingTalkClient client1 = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
            OapiV2UserGetRequest req = new OapiV2UserGetRequest();
            req.setUserid(empStr);
            req.setLanguage("zh_CN");
            OapiV2UserGetResponse rsp = client1.execute(req, token);
            JSONObject userJson = JSONObject.fromObject(rsp.getBody());
            JSONObject result = userJson.getJSONObject("result");
            String unionId = result.getString("unionid");

            //查询钉钉待办
            QueryOrgTodoTasksHeaders queryOrgTodoTasksHeaders = new QueryOrgTodoTasksHeaders();
            queryOrgTodoTasksHeaders.xAcsDingtalkAccessToken = token;
            QueryOrgTodoTasksRequest queryOrgTodoTasksRequest = new QueryOrgTodoTasksRequest()
                    .setNextToken("0");
            try {
                QueryOrgTodoTasksResponse queryOrgTodoTasksResponse = client.queryOrgTodoTasksWithOptions(unionId, queryOrgTodoTasksRequest, queryOrgTodoTasksHeaders, new RuntimeOptions());
                JSONObject jsonObject = JSONObject.fromObject(queryOrgTodoTasksResponse.getBody());
                JSONArray todoCards = jsonObject.getJSONArray("todoCards");
                if (todoCards.size() <= 0)
                    continue;

                //待办详情
                for (int i = 0; i < todoCards.size(); i++) {
                    JSONObject list = todoCards.getJSONObject(i);
                    String taskId = list.getString("taskId");
                    String sourceId = list.getString("sourceId");
                    String[] str = sourceId.split("_");

                    //查询本系统中，是否存在当前节点、当前流程、当前处理人的待办
                    DataTable dt = bp.da.DBAccess.RunSQLReturnTable("select * from wf_empworks where workid='" + gwf.getWorkID() + "' and fk_emp='" + empStr + "'");

                    //如果人员帐号相同、workid相同、流程编号相同、节点id相同、本系统中不存在待办，说明已经完成，执行删除
                    if ((str[0].equals(empStr) && str[1].equals(String.valueOf(gwf.getWorkID()))
                            && str[2].equals(gwf.getFlowNo()) && str[3].equals(ndTo)) || dt.Rows.size() == 0) {
                        //设置todo header以及request
                        com.aliyun.dingtalktodo_1_0.models.DeleteTodoTaskHeaders deleteTodoTaskHeaders = new com.aliyun.dingtalktodo_1_0.models.DeleteTodoTaskHeaders();
                        deleteTodoTaskHeaders.setXAcsDingtalkAccessToken(token);

                        com.aliyun.dingtalktodo_1_0.models.DeleteTodoTaskRequest deleteTodoTaskRequest = new com.aliyun.dingtalktodo_1_0.models.DeleteTodoTaskRequest();
                        // operatorId 当前操作者的用户的unionId。
                        deleteTodoTaskRequest.setOperatorId(unionId);
                        DeleteTodoTaskResponse deleteTodoTaskResponse = client.deleteTodoTaskWithOptions(unionId, taskId, deleteTodoTaskRequest, deleteTodoTaskHeaders, new RuntimeOptions());
                        System.out.println("2:" + JSONObject.fromObject(deleteTodoTaskResponse.getBody()));
                    }

                }

            } catch (TeaException err) {
                if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                    // err 中含有 code 和 message 属性，可帮助开发定位问题
                }

            } catch (Exception _err) {
                TeaException err = new TeaException(_err.getMessage(), _err);
                if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                    // err 中含有 code 和 message 属性，可帮助开发定位问题
                }

            }
        }

        return "发送成功";
    }

    private static StringBuilder checkMsg;

    /**
     * 检查配置项是否完整
     *
     * @return 如果完整返回true，否则返回false
     */
    private static boolean CheckWeiXinConfig() {
        checkMsg = new StringBuilder();
        /*
            例如：
            #微信企业号相关配置
            #企业号id，唯一的
            CorpID=xx
            #企业微信应用ID
            AgentID=1111
            #应用的开发者密码
            AppSecret=sssss
            #接收消息服务器配置URL http://weixin.ccflow.org/WF/WXZFH/Start
            #接收消息服务器配置Token
            WeiXinToken=sdsds
            #接收消息服务器配置EncodingAESKey
            EncodingAESKey=dededer
            #启用企业微信消息发送
            MessageIsEnableWeiXin=1
            #消息链接网址
            WeiXin_MessageUrl=http://您的前端访问域名/#/WeChatEnterprise
         */
        if (DataType.IsNullOrEmpty(SystemConfig.getWX_CorpID())) {
            checkMsg.append(",");
            checkMsg.append("CorpID");
        }
        if (DataType.IsNullOrEmpty(SystemConfig.getWX_AgentID())) {
            checkMsg.append(",");
            checkMsg.append("AgentID");
        }
        if (DataType.IsNullOrEmpty(SystemConfig.getWX_AppSecret())) {
            checkMsg.append(",");
            checkMsg.append("AppSecret");
        }
        if (DataType.IsNullOrEmpty(SystemConfig.getWX_EncodingAESKey())) {
            checkMsg.append(",");
            checkMsg.append("EncodingAESKey");
        }
        if (DataType.IsNullOrEmpty(SystemConfig.getWX_MessageUrl())) {
            checkMsg.append(",");
            checkMsg.append("WeiXin_MessageUrl");
        }
        if (DataType.IsNullOrEmpty(SystemConfig.getWX_WeiXinToken())) {
            checkMsg.append(",");
            checkMsg.append("WeiXinToken");
        }
        if (!DataType.IsNullOrEmpty(checkMsg.toString())) {
            checkMsg.substring(1);
            return false;
        }
        return true;
    }

    /**
     * 企业微信发送消息提醒
     * 需要配置项
     *
     * @param sms
     * @return
     * @throws Exception
     */
    public static void SendToWeiXin(SMS sms) throws Exception {
        //检查配置项是否完整
        if (!CheckWeiXinConfig()) {
            bp.da.Log.DebugWriteError("err@检测到您的企业微信相关配置项 [" + checkMsg.toString() + "] 没有配置，请您在jflow.properties配置文件中进行配置");
            return;
        }
        GenerWorkFlow generWorkFlow = new GenerWorkFlow(sms.getWorkID());
        String agentId = SystemConfig.getWX_AgentID();
        String corpId = SystemConfig.getWX_CorpID();
        String appSecret = SystemConfig.getWX_AppSecret();
        String messageUrl = SystemConfig.getWX_MessageUrl();
        String sendTo = sms.getSendToEmpNo();
        if(DataType.IsNullOrEmpty(sendTo)){
            bp.da.Log.DebugWriteError("err@没有获取到SendTo参数，数据：" + bp.tools.Json.ToJson(sms.ToDataTableField()));
            return;
        }
        //微信企业号
        if (!DataType.IsNullOrEmpty(agentId)) {
            //第一步，先获取token
            String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpId + "&corpsecret=" + appSecret;
            String json = HttpClientUtil.doGet(url);
            JSONObject jsonObject = JSONObject.fromObject(json);

            if (jsonObject.getString("errcode").equals("0")) {
                //第二步，组装消息体
                String state = "MyFlow_" + generWorkFlow.getFlowNo() + "_" + generWorkFlow.getWorkID();
                String reddirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
                        + "appid=" + corpId + "&redirect_uri=" +URLEncoder.encode(messageUrl, "UTF-8")  + "&response_type=code&scope=snsapi_base&state=" + state + "&agentID=" + agentId + "#wechat_redirect";
                //文本卡片消息参数说明请参考： https://developer.work.weixin.qq.com/document/path/90236
                String msgBody = "{" +
                        "   \"touser\" : \"" + sendTo + "\"," +
                        "   \"msgtype\" : \"textcard\"," +
                        "   \"agentid\" : \"" + agentId + "\"," +
                        "   \"textcard\" : {" +
                        "            \"title\" : \"审核通知\"," +
                        "            \"description\" : \"<div class='gray'>" + DataType.getCurrentDateTime() + "</div> <div class='normal'>" + sms.getTitle() + "</div><div class='highlight'>需要您进行审核。</div>\"," +
                        "            \"url\" : \"" + reddirectUrl + "\"," +
                        "                        \"btntxt\":\"查看详情\"" +
                        "   }," +
                        "   \"enable_id_trans\": 0," +
                        "   \"enable_duplicate_check\": 0," +
                        "   \"duplicate_check_interval\": 1800" +
                        "}";

                //第三步，执行消息推送
                String access_token = jsonObject.getString("access_token");
                url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?debug=1&access_token=" + access_token;

                String jsonData = bp.tools.PubGlo.HttpPostConnect(url, msgBody, "POST", true);
                jsonObject = JSONObject.fromObject(jsonData);
                if (jsonObject.getInt("errcode") != 0) {
                    bp.da.Log.DebugWriteError("执行企业微信消息推送的URL：[" + url + "]");
                    bp.da.Log.DebugWriteError("企业微信消息推送的请求Body：[" + msgBody + "]");
                    bp.da.Log.DebugWriteError("企业微信消息推送失败，错误码：[" + jsonObject.getInt("errcode") + "]，\n\r\t错误信息：[" + jsonObject.getString("errmsg") + "]");
                } else {
                    bp.da.Log.DebugWriteInfo("企业微信消息推送成功，返回信息：" + jsonObject);
                }
            }
        }
        //微信公众号
        if (!DataType.IsNullOrEmpty(SystemConfig.getWXGZH_Appid())) {
            //第一步，先获取token
            String access_token = "";
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + SystemConfig.getWXGZH_Appid() + "&secret=" + SystemConfig.getWXGZH_AppSecret();
            String json = HttpClientUtil.doGet(url);
            JSONObject jsonObject = JSONObject.fromObject(json);
            if (jsonObject.getString("errcode").equals("0")) {
                access_token = jsonObject.getString("access_token");
                String redircet_uri = SystemConfig.getAppSettings().get("messageOfWXPath").toString();
                String state = "MyFlow_" + generWorkFlow.getFlowNo() + "_" + generWorkFlow.getWorkID();
                String reddirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
                        + "appid=" + SystemConfig.getWXGZH_Appid() + "&redirect_uri=" + redircet_uri + ""
                        + "&response_type=code&scope=snsapi_userinfo&state=" + state + ""
                        + "&connect_redirect=1#wechat_redirect";
                //需要根据选中的消息模版，定义参数格式
//                    String postData = "{\"touser\":\"" + this.getSenderTo() + "\",\"template_id\":\"" + SystemConfig.getWeiXin_TemplateId() + "\"," +
//                            "\"url\":\"" + redircet_uri + "\",\"data\":{\"thing5\":{\"value\":\"审核通知\"}," +
//                            "\"time3\":{\"value\":\"" + DataType.getCurrentDateTime() + "\"}}}";
                String postData = "";
                String jsonData = bp.tools.PubGlo.HttpPostConnect(url, postData, "POST", true);
                jsonObject = JSONObject.fromObject(jsonData);
            }

            if (jsonObject.getInt("errcode") != 0) {
                bp.da.Log.DebugWriteError("发送失败，错误码：[" + jsonObject.getInt("errcode") + "]，错误信息：[" + jsonObject.getString("errmsg") + "]");
            } else {
                bp.da.Log.DebugWriteInfo("发送成功，返回信息：" + jsonObject);
            }
        }
    }


    public static String MessageIsEnableSelf(SMS sms) {
        if (SystemConfig.getCustomerNo().equals(CUSTOMER_NO) == true) {

        }
        return "";
    }

    /**
     * 流程事件-总体拦截器.
     *
     * @param eventMark 流程标记
     * @param wn        worknode
     * @param paras     参数
     * @param checkNote 审核意见.
     * @return 执行信息.
     */
    public static String DoIt(String eventMark, WorkNode wn, String paras, String checkNote, int returnToNodeID, String returnToEmps, String returnMsg) throws Exception {
        //发送成功.
        if (eventMark.equals(EventListNode.SendSuccess) == true) {

            // xxx   WFState=0 空白.
            //	long workid=bp.wf.Dev2Interface.Node_CreateBlankWork("qingjia");
            //	bp.wf.Dev2Interface.Node_SetDraft(workid); //WFSTate=1
            //创建一个新的workid.
            //	long workid2=bp.wf.Dev2Interface.Node_CreateBlankWork("qingjia");
            //写入公文版本在track表中
            //OfficeBtnEnable 0-不可用 1-可编辑 2-不可编辑
            String officeBtnEnable = wn.getHisNode().GetValStrByKey(BtnAttr.OfficeBtnEnable);
            byte[] data = null;
            if (officeBtnEnable.equals("1")) {
                try {
                    //不抛出异常
                    data = wn.getHisWork().GetFileFromDB("DBFile", null);

                } catch (Exception ex) {
                    bp.da.Log.DebugWriteError(ex);
                }
                if (data != null) {
                    String msg = "发送保存公文数据";
                    if (wn.getHisNode().getFrmWorkCheckSta() == FrmWorkCheckSta.Enable) {
                        msg = checkNote;
                    }

                    Dev2Interface.WriteTrackGovDoc(wn.getHisFlow().getNo(), wn.getHisNode().getNodeID(), wn.getHisNode().getName(), wn.getWorkID(), wn.getHisWork().getFID(), msg, data);

                }

            }
            return SendSuccess(wn);
        }

        //发送前事件.
        if (eventMark.equals(EventListNode.SendWhen) == true)
            return SendWhen(wn);

        //退回后事件.
        if (eventMark.equals(EventListNode.ReturnAfter) == true) {
            if (returnToNodeID == 0 && DataType.IsNullOrEmpty(paras) == false) {
                AtPara atPara = new AtPara(paras);
                returnToNodeID = atPara.GetValIntByKey("ToNode");
            }

            return ReturnAfter(wn, returnToNodeID, returnToEmps, returnMsg);
        }

        // 流程结束事件.
        if (eventMark.equals(EventListFlow.FlowOverAfter) == true) {
            return FlowOverAfter(wn);
        }

        return null;
    }
    public static String ShitAfter(WorkNode wn) throws Exception {
        GEEntityOID ge = new GEEntityOID("Frm_XXX", 199);

        return null;
    }
    public static String UndoneAfter(WorkNode wn)
    {
        return null;
    }

    private static final String DBVARSTR = SystemConfig.getAppCenterDBVarStr();
    private static final String CUSTOMER_NO = "JiaoTong"; //太原交通
    private static final String BUSINESS_DEPT_NO = "1011115"; //商务招采部门编号

    public static String SendWhen(WorkNode wn) throws Exception {
        String returnstr = null;

        if (SystemConfig.getCustomerNo().equals(CUSTOMER_NO) == true) {
            //读取配置 @lyc-2024/06/26
            String businessNodes1 = SystemConfig.GetValByKey("BusinessNodes1", ""); //处理部门负责人+商务招采部门负责人的节点
            String businessNodes2 = SystemConfig.GetValByKey("BusinessNodes2", ""); //处理业务分管领导=商务招采分管领导的节点
            if (!DataType.IsNullOrEmpty(businessNodes1) && !DataType.IsNullOrEmpty(businessNodes2)) {
                //【节点 = 部门负责人审核&商务招采部门负责人审核】的业务逻辑
                String[] nodes = businessNodes1.split("@");
                for (String item : nodes) {
                    if (DataType.IsNullOrEmpty(item)) {
                        continue;
                    }
                    //处理找部门负责人的逻辑
                    if (wn.getHisNode().getNodeID() == Integer.parseInt(item)) {
                        // 商务招采部门负责人
                        String businessDeptLeader = getDeptLeader(BUSINESS_DEPT_NO);

                        // 申请人的部门编号
                        String deptNo = wn.getHisWork().GetValStrByKey("FK_Dept");

                        // 如果申请人是商务招采部门的
                        if (BUSINESS_DEPT_NO.equals(deptNo)) {
                            wn.JumpToEmp = businessDeptLeader;
                        } else {
                            // 返回部门负责人+商务招采部门负责人
                            String deptLeader = getDeptLeader(deptNo);
                            wn.JumpToEmp = deptLeader + "," + businessDeptLeader;
                        }
                        if (!DataType.IsNullOrEmpty(wn.JumpToEmp)) {
                            returnstr = "@ToEmps=" + wn.JumpToEmp;
                        }
                    }
                }
                //【节点 = 业务分管领导审核&商务招采分管领导审核】的业务逻辑
                nodes = businessNodes2.split("@");
                for (String item : nodes) {
                    if (DataType.IsNullOrEmpty(item)) {
                        continue;
                    }
                    //处理找业务分管领导的逻辑
                    if (wn.getHisNode().getNodeID() == Integer.parseInt(item)) {
                        // 商务招采分管领导
                        String businessDeptLeader = getDeptSpecer(BUSINESS_DEPT_NO);

                        // 业务分管领导编号 SELECT FK_Dept  FROM wf_generworkerlist WHERE WorkID = 194766979 AND FK_Node LIKE '%01'
                        String sql = "SELECT FK_Dept  FROM wf_generworkerlist WHERE WorkID=" + DBVARSTR + "WorkID AND FK_Node LIKE '%01'";
                        Paras ps = new Paras();
                        ps.SQL = sql;
                        ps.Add("WorkID", wn.getHisWork().GetValStrByKey("OID"));

                        String deptNo = DBAccess.RunSQLReturnString(ps);

                        // 如果申请人是商务招采部门的
                        if (BUSINESS_DEPT_NO.equals(deptNo)) {
                            wn.JumpToEmp = businessDeptLeader;
                        } else {
                            // 返回业务分管领导+商务招采分管领导
                            String deptLeader = getDeptSpecer(deptNo);
                            wn.JumpToEmp = deptLeader + "," + businessDeptLeader;
                        }
                        if (!DataType.IsNullOrEmpty(wn.JumpToEmp)) {
                            returnstr = "@ToEmps=" + wn.JumpToEmp;
                        }
                    }
                }
            }
        }
        return returnstr;
    }

    // 封装获取部门负责人的方法
    private static String getDeptLeader(String deptNo) {
        // 使用PreparedStatement来避免SQL注入
        Paras ps = new Paras();
        ps.SQL = "SELECT Leader FROM port_dept WHERE No=" + SystemConfig.getAppCenterDBVarStr() + "No";
        ps.Add("No", deptNo);
        String leader = DBAccess.RunSQLReturnString(ps); // DBAccess支持带参数的查询
        return leader;
    }

    // 封装获取部门分管领导的方法
    private static String getDeptSpecer(String deptNo) {
        // 使用PreparedStatement来避免SQL注入
        Paras ps = new Paras();
        ps.SQL = "SELECT Specer FROM port_dept WHERE No=" + SystemConfig.getAppCenterDBVarStr() + "No";
        ps.Add("No", deptNo);
        String specer = DBAccess.RunSQLReturnString(ps); // DBAccess支持带参数的查询
        return specer;
    }

    private static FlowSort GetParentFlowSort(String flowSortNo) throws Exception {

        FlowSort fs = new FlowSort(flowSortNo);
        if (fs.getParentNo().equals("0"))
            return fs;
        // 如果他的父节点没有找到，那么将他本身作为父节点
        if (new FlowSort(fs.getParentNo()).RetrieveFromDBSources() == 0) {
            return fs;
        }

        return GetParentFlowSort(fs.getParentNo());

    }

    /**
     * 执行发送.
     *
     * @param wn
     * @return
     */
    public static String SendSuccess(WorkNode wn) throws Exception {
        // #region 把消息推送到待办中心.
        if (SystemConfig.getTodoCenter().equals("ccbpm") == true)
        {
            //Node toND = wn.town.HisNode;
            //BP.TodoAPI.Dev2InterfaceRestfulAPI.Send("ccbpm", wn.HisWork.OID.ToString(), WebUser.No, toND.NodeID.ToString(), toND.Name,
            //    wn.HisGenerWorkFlow.TodoEmps, "http://mywork/WF/MyFlow.vue?WorkID=" + wn.HisWork.OID, wn.HisGenerWorkFlow.SDTOfNode);
        }
         //   #endregion 把消息推送到待办中心.
        if (SystemConfig.getCustomerNo().equals("NingBoGang") == true) {
            // 新增待办待阅信息接口地址
            String newToDoUrl = SystemConfig.getAppSettings().get("NewToDoUrl").toString();
            // 待办待阅转已读接口接口地址
            String toDoConReadUrl = SystemConfig.getAppSettings().get("ToDoConReadUrl").toString();
            //获取临时调用接口凭证accessToken接口地址
            String accessTokenUrl = SystemConfig.getAppSettings().get("AccessTokenUrl").toString();
            //业务系统id
            String centerPushAppId=SystemConfig.getAppSettings().get("CenterPushAppId").toString();
            //获取临时调用接口凭证 accessToken 接口使用的systemcode
            String centerPushSystemCode=SystemConfig.getAppSettings().get("CenterPushSystemCode").toString();

            //获取临时调用接口凭证 accessToken 接口
            String accessToken = nbgGetAccessToken(centerPushAppId,centerPushSystemCode,accessTokenUrl);

            String[] todoemps = wn.getHisGenerWorkFlow().getTodoEmps().split(";");
            for (String todoemp : todoemps) {
                //如果是开始节点，调用新增待办待阅信息接口
                if (wn.getHisNode().getItIsStartNode() == true) {
                    // 调用新增待办待阅信息接口
                    nbgStartInt(wn, todoemp, newToDoUrl,accessToken,centerPushAppId);
                } else if (wn.getHisNode().getItIsEndNode() == true) {
                    // 如果是结束节点,调用待办待阅转已读接口
                    nbgEndInt(wn, todoemp, toDoConReadUrl, "1",accessToken,centerPushAppId);
                } else {
                    // 调用待办待阅转已读接口
                    nbgEndInt(wn, todoemp, toDoConReadUrl, "1",accessToken,centerPushAppId);
                    //调用新增待办待阅信息接口
                    nbgStartInt(wn, todoemp, newToDoUrl,accessToken,centerPushAppId);
                }
            }
        }
        if (SystemConfig.getCustomerNo().equals("TianYu") == true) {
            if (wn.getHisNode().getItIsStartNode() == false)
                return null; //如果不是开始节点发送,就不处理.

            //模板目录.
            String sortNo = wn.getHisFlow().getFlowSortNo();

            //找到系统编号.
            FlowSort fs = GetParentFlowSort(sortNo);

            //子系统:当前目录的上一级目录必定是子系统,系统约定的.
            SubSystem system = new SubSystem(fs.getNo());

            //检查是否配置了?
            if (DataType.IsNullOrEmpty(system.getTokenPiv()) == true)
                return null;

            //执行事件.
            DoPost("SendSuccess", wn, system);
        }
        return null;
    }

    /**
     * 执行退回操作.
     *
     * @param wn
     * @param toNodeID
     * @param toEmp
     * @param info
     * @return
     */
    public static String ReturnAfter(WorkNode wn, int toNodeID, String toEmp, String info) throws Exception {
        if (SystemConfig.getCustomerNo().equals("NingBoGang") == true) {
            // 新增待办待阅信息接口地址
            String newToDoUrl = SystemConfig.getAppSettings().get("NewToDoUrl").toString();
            // 待办待阅转已读接口接口地址
            String toDoConReadUrl = SystemConfig.getAppSettings().get("ToDoConReadUrl").toString();
            //获取临时调用接口凭证accessToken接口地址
            String accessTokenUrl = SystemConfig.getAppSettings().get("AccessTokenUrl").toString();
            //业务系统id
            String centerPushAppId=SystemConfig.getAppSettings().get("CenterPushAppId").toString();
            //获取临时调用接口凭证 accessToken 接口使用的systemcode
            String centerPushSystemCode=SystemConfig.getAppSettings().get("CenterPushSystemCode").toString();

            //获取临时调用接口凭证 accessToken 接口
            String accessToken = nbgGetAccessToken(centerPushAppId,centerPushSystemCode,accessTokenUrl);

            String[] todoemps = wn.getHisGenerWorkFlow().getTodoEmps().split(";");
            for (String todoemp : todoemps) {
                //如果是开始节点，调用新增待办待阅信息接口
                if (wn.getHisNode().getItIsStartNode() == true) {
                    // 调用新增待办待阅信息接口
                    nbgStartInt(wn, todoemp, newToDoUrl,accessToken,centerPushAppId);

                } else if (wn.getHisNode().getItIsEndNode() == true) {
                    // 如果是结束节点,调用待办待阅转已读接口
                    nbgEndInt(wn, todoemp, toDoConReadUrl, "2",accessToken,centerPushAppId);
                } else {
                    // 调用待办待阅转已读接口
                    nbgEndInt(wn, todoemp, toDoConReadUrl, "2",accessToken,centerPushAppId);
                    //调用新增待办待阅信息接口
                    nbgStartInt(wn, todoemp, newToDoUrl,accessToken,centerPushAppId);
                }
            }
        }
        if (SystemConfig.getCustomerNo().equals("TianYu") == true) {
            if (String.valueOf(toNodeID).endsWith("01") == false) {
                return null; //如果不是退回的开始节点.
            }

            //模板目录.
            String sortNo = wn.getHisFlow().getFlowSortNo();

            //找到系统编号.
            //找到系统编号.
            FlowSort fs = GetParentFlowSort(sortNo);

            //子系统:当前目录的上一级目录必定是子系统,系统约定的.
            SubSystem system = new SubSystem(fs.getNo());

            //检查是否配置了?
            if (DataType.IsNullOrEmpty(system.getTokenPiv()) == true) {
                return null;
            }

            //执行事件.
            DoPost("ReturnAfter", wn, system);
        }
        return null;
    }

    public static String nbgGetAccessToken(String centerPushAppId,String centerPushSystemCode,String accessTokenUrl) throws Exception {
        java.util.Map<String, String> headerMap = new Hashtable<String, String>();

        headerMap.put("Content-Type", "application/json");
        String param = "";
        param += "{";
        param += "\"systemCode\":\""+centerPushAppId+",";
        param += "\"appId\":\"" + centerPushAppId + "\",";
        param += "\"timeStamp\":\"" +  System.currentTimeMillis() + "\"";
        param += "}";

        String redata = HttpClientUtil.doPost(accessTokenUrl, param, headerMap);
        net.sf.json.JSONObject j = net.sf.json.JSONObject.fromObject(redata);

        //请求成功
        if (j.get("code") != null && "0000".equals(j.get("code").toString())) {
            return j.get("result").toString();
        } else {
            //code不等于0000时返回响应结果
            throw new Exception("获取临时token失败：code:"+j.get("code")+",message:"+j.get("message")+",result:"+j.get("result")) ;
        }
    }

    public static String FlowOverAfter(WorkNode wn) throws Exception {
        if (SystemConfig.getCustomerNo().equals("TianYu") == true) {
            //模板目录.
            String sortNo = wn.getHisFlow().getFlowSortNo();

            //找到系统编号.
            //找到系统编号.
            FlowSort fs = GetParentFlowSort(sortNo);

            //子系统:当前目录的上一级目录必定是子系统,系统约定的.
            SubSystem system = new SubSystem(fs.getNo());

            //检查是否配置了?
            if (DataType.IsNullOrEmpty(system.getTokenPiv()) == true) {
                return null;
            }

            //执行事件.
            DoPost("FlowOverAfter", wn, system);
        }
        return null;
    }

    /**
     * 宁波港调用新增待办待阅信息接口
     *
     * @param wn         工作节点（包含工作信息、流程信息）
     * @param todoemp
     * @param newToDoUrl 接口地址
     * @return
     * @throws Exception
     */
    public static String nbgStartInt(WorkNode wn, String todoemp, String newToDoUrl,String accessToken,String centerPushAppId) throws Exception {
        java.util.Map<String, String> headerMap = new Hashtable<String, String>();
        //Base64 base64 = new Base64();
        String[] sendArr = wn.getHisGenerWorkFlow().getSender().replace(";", "").split(",");
        String[] todoArr = todoemp.split(",");

        headerMap.put("Content-Type", "application/json");
        String param = "";
        param += "{";
        param += "\"systemCode\":\""+accessToken+"\",";
        param += "\"appId\":\""+centerPushAppId+"\",";
        param += "\"timeStamp\":\""+  System.currentTimeMillis()+"\",";
        param += "\"contentId\":\"" + wn.getWorkID() + "_" + wn.getHisNode().getNodeID() + "\",";
        param += "\"messageTitle\":\"" + wn.getHisWork().GetValByKey("Title") + "\",";
        param += "\"messageSenderId\":\"" + sendArr[0] + "\",";
        param += "\"messageReceiverId\":\"" + todoArr[0] + "\",";
        param += "\"messageSenderName\":\"" + sendArr[1] + "\",";
        param += "\"messageReceiverName\":\"" + todoArr[1] + "\",";
        param += "\"messageUrlPortal\":\"\",";
        param += "\"messageUrlZgb\":\"" + SystemConfig.getAppSettings().get("VueHostURL").toString() + "/#/MobileSSO?DoWhat=DealWork&DoParam={WorkID:" + wn.getWorkID() + ",FK_Flow:" + wn.getHisNode().getFlowNo() + "}\",";
        param += "\"messageKind\":\"2\",";
        param += "\"messageStatus\":\"0\",";
        param += "\"messageType\":\"28\",";
        param += "\"contentType\":\"\",";
        param += "\"step\":\"\",";
        param += "\"messageInfo\":\"\"";
        param += "}";

        String redata = HttpClientUtil.doPost(newToDoUrl, param, headerMap);
        net.sf.json.JSONObject j = net.sf.json.JSONObject.fromObject(redata);
        //请求失败，
        if (j.get("status") != null) {
            String msg = "err@调用新增待办待阅信息接口失败，[" + newToDoUrl + "]接口响应：" + j + "，参数：" + param;
            bp.da.Log.DebugWriteError(msg);
            throw new RuntimeException(msg);
        }
        //请求成功
        if (j.get("code") != null && "0000".equals(j.get("code").toString())) {
            return "请求成功，接口响应：" + j;
        } else {
            //code不等于0000时返回响应结果
            return "接口响应：" + j;
        }
    }

    /**
     * 宁波港调用待办待阅转已读接口
     *
     * @param wn         工作节点（包含工作信息、流程信息）
     * @param todoemp
     * @param newToDoUrl 接口地址
     * @param actionType 1：待办转已办 2：删除该条待办
     * @return
     * @throws Exception
     */
    public static String nbgEndInt(WorkNode wn, String todoemp, String newToDoUrl, String actionType,String accessToken,String centerPushAppId) throws Exception {
        //从轨迹表中查询节点编号,接收人手机号（从哪个节点发送过来的）
        String dbStr = bp.difference.SystemConfig.getAppCenterDBVarStr();
        Paras ps = new Paras();
        ps.SQL = "SELECT NDFrom,EmpTo FROM ND" + Integer.parseInt(wn.getHisFlow().getNo()) + "Track WHERE ActionType=1 AND NDTo=" + dbStr + "NDTo AND WorkID=" + dbStr + "WorkID ORDER BY RDT DESC";
        ps.Add("NDTo", wn.getHisNode().getNodeID());
        ps.Add("WorkID", wn.getWorkID());
        DataTable dt_NDFrom = DBAccess.RunSQLReturnTable(ps);
        if (dt_NDFrom.Rows.size() == 0) {
            String msg = "err@调用待办待阅转已读接口失败，在轨迹表中获取上一节点时出现错误，SQL=" + ps.SQL + "，参数=" + ps.toString();
            bp.da.Log.DebugWriteError(msg);
            throw new RuntimeException(msg);
        }
        String ndFrom = dt_NDFrom.getValue(0, "NDFrom").toString();
        String empTo = dt_NDFrom.getValue(0, "EmpTo").toString();
        java.util.Map<String, String> headerMap = new Hashtable<String, String>();
        //Base64 base64 = new Base64();
        String[] todoArr = todoemp.split(",");
        headerMap.put("Content-Type", "application/json");
        String param = "";
        param += "{";
        param += "\"systemCode\":\""+accessToken+"\",";
        param += "\"appId\":\""+centerPushAppId+"\",";
        param += "\"timeStamp\":\""+  System.currentTimeMillis()+"\",";
        param += "\"messageType\":\"28\",";
        param += "\"messageId\":\"" + wn.getWorkID() + "_" + ndFrom + "\",";
        param += "\"action\":\"" + actionType + "\",";
        param += "\"userId\":\"" + empTo + "\"";
        param += "}";

        String redata = HttpClientUtil.doPost(newToDoUrl, param, headerMap);
        net.sf.json.JSONObject j = net.sf.json.JSONObject.fromObject(redata);
        //请求失败，
        if (j.get("status") != null) {
            String msg = "err@调用待办待阅转已读接口失败，[" + newToDoUrl + "]接口响应：" + j + "，参数：" + param;
            bp.da.Log.DebugWriteError(msg);
            throw new RuntimeException(msg);
        }
        //请求成功
        if (j.get("code") != null && "0000".equals(j.get("code").toString())) {
            return "请求成功，接口响应：" + j;
        } else {
            //code不等于0000时返回响应结果
            return "接口响应：" + j;
        }
    }

    /**
     * 执行天宇的回调.
     *
     * @param eventMark 事件目录.
     * @param wn
     * @param system
     * @return
     */
    public static String DoPost(String eventMark, WorkNode wn, SubSystem system) throws Exception {
        String myEventMark = "0";
        if (eventMark.equals("ReturnAfter")) {
            myEventMark = "3";
        }
        if (eventMark.equals("SendSuccess")) {
            myEventMark = "1";
        }
        if (eventMark.equals("FlowOverAfter")) {
            myEventMark = "2";
        }

        String apiParas = system.getApiParas(); //配置的json字符串.
        apiParas = apiParas.replace("~", "\"");

        apiParas = apiParas.replace("@WorkID", String.valueOf(wn.getWorkID())); //工作ID.
        apiParas = apiParas.replace("@FlowNo", wn.getHisFlow().getNo()); //流程编号.
        apiParas = apiParas.replace("@NodeID", String.valueOf(wn.getHisNode().getNodeID())); //节点ID.
        apiParas = apiParas.replace("@TimeSpan", String.valueOf(DBAccess.GenerOID("TS"))); //时间戳.
        apiParas = apiParas.replace("@EventMark", myEventMark); //稳超定义的，事件标记.
        apiParas = apiParas.replace("@EventID", eventMark); //EventID 定义的事件类型.
        apiParas = apiParas.replace("@SPYJ", "xxx无xx"); //审批意见.

        //如果表单的数据有,就执行一次替换.
        apiParas = bp.wf.Glo.DealExp(apiParas, wn.rptGe);
        if (apiParas.contains("@") == true) {
            GenerWorkFlow gwf = new GenerWorkFlow(wn.getWorkID());
            AtPara atpara = gwf.getatPara();
            for (String key : atpara.getHisHT().keySet()) {
                apiParas = apiParas.replace("@" + key, atpara.GetValStrByKey(key));
                if (apiParas.contains("@") == false)
                    break;
            }
        }

        //需要补充算法. @WenChao.
        String sign = "密钥:" + system.getTokenPiv() + ",公约" + system.getTokenPublie();
        apiParas = apiParas.replace("@sign", sign); //签名，用于安全验证.

        if (apiParas.contains("@") == true) {
            throw new RuntimeException("err@配置的参数没有替换下来:" + apiParas);
        }

        //替换url参数.
        String url = system.getCallBack(); //回调的全路径.
        url = bp.wf.Glo.DealExp(url, wn.rptGe);

        //执行post.
        String data = bp.tools.PubGlo.HttpPostConnect(url, apiParas, system.getRequestMethod(), system.getItIsJson());
        return data;
    }

    public static void SendToRabbitMQ(SMS sms) {
        // 消息内容需要用户自定义，如不知如何获取数据请联系CCFLOW
        String messageId = UUID.randomUUID().toString();
        //long workid = sms.getWorkID(); // 流程实例编号
        //String title = sms.getTitle(); // 默认的流程标题
        String meaageData = "{\"title\":\"测试\"}";
        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("data", meaageData);
        map.put("current", current);
        ContextHolderUtils.getRabbitUtils().sendMessage("TestChanel", "123", map);
    }
}
