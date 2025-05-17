package bp.port.dingtalk;

import bp.da.Log;
import bp.port.dingtalk.DingModel.DingMsgLink;
import bp.port.dingtalk.DingModel.DingMsgOA;
import bp.port.dingtalk.DingModel.DingMsgText;
import bp.port.dingtalk.DingModel.DingPostReturnVal;
import bp.port.dingtalk.ddsdk.RequestHelper;
import bp.tools.FormatToJson;

/**
 * 钉钉消息处理类
 * by dgq 2016.5.9
 */
public class DingTalk_Message {
    /**
     * 普通消息
     *
     * @param dingMsg
     * @return
     */
    public static DingPostReturnVal Msg_AgentText_Send(DingMsgText dingMsg) {
        String url = "https://oapi.dingtalk.com/message/send?access_token=" + dingMsg.getAccessToken();
        try {
            StringBuilder append_Json = new StringBuilder();
            append_Json.append("{");
            append_Json.append("\"touser\":\"" + dingMsg.getTouser() + "\"");
            append_Json.append(",\"msgtype\":\"text\"");
            append_Json.append(",\"agentid\":\"" + dingMsg.getAgentid() + "\"");
            append_Json.append(",\"text\":{\"content\":\"" + dingMsg.getContent() + "\"}");
            append_Json.append("}");
            String str = RequestHelper.Post(url, append_Json.toString());
            DingPostReturnVal postVal = (DingPostReturnVal) FormatToJson.ParseFromJson(str);
            return postVal;
        } catch (Exception ex) {
            Log.DebugWriteError(ex.getMessage());
        }
        return null;
    }

    /**
     * 带有超链接消息
     *
     * @param dingMsg
     * @return
     */
    public static DingPostReturnVal Msg_AgentLink_Send(DingMsgLink dingMsg) {
        String url = "https://oapi.dingtalk.com/message/send?access_token=" + dingMsg.getAccessToken();
        try {
            StringBuilder append_Json = new StringBuilder();
            append_Json.append("{");
            append_Json.append("\"touser\":\"" + dingMsg.getTouser() + "\"");
            append_Json.append(",\"msgtype\":\"link\"");
            append_Json.append(",\"agentid\":\"" + dingMsg.getAgentid() + "\"");
            append_Json.append(",\"link\":{");
            append_Json.append("\"messageUrl\":\"" + dingMsg.getMessageUrl() + "\"");
            append_Json.append(",\"picUrl\":\"" + dingMsg.getPicUrl() + "\"");
            append_Json.append(",\"title\":\"" + dingMsg.getTitle() + "\"");
            append_Json.append(",\"text\":\"" + dingMsg.getText() + "\"");
            append_Json.append("}");
            append_Json.append("}");
            String str = RequestHelper.Post(url, append_Json.toString());
            DingPostReturnVal postVal = (DingPostReturnVal) FormatToJson.ParseFromJson(str);
            return postVal;
        } catch (Exception ex) {
            Log.DebugWriteError(ex.getMessage());
        }
        return null;
    }

    /**
     * 发送OA型消息
     *
     * @param dingMsg
     * @return
     */
    public static DingPostReturnVal Msg_OAText_Send(DingMsgOA dingMsg) {
        String url = "https://oapi.dingtalk.com/message/send?access_token=" + dingMsg.getAccessToken();
        try {
            StringBuilder append_Json = new StringBuilder();
            append_Json.append("{");
            append_Json.append("\"touser\":\"" + dingMsg.getTouser() + "\"");
            append_Json.append(",\"msgtype\":\"oa\"");
            append_Json.append(",\"agentid\":\"" + dingMsg.getAgentid() + "\"");
            append_Json.append(",\"oa\":{");
            append_Json.append("\"message_url\":\"" + dingMsg.getMessageUrl() + "\"");

            append_Json.append(",\"head\":{");
            append_Json.append("\"bgcolor\":\"" + dingMsg.getHeadBgcolor() + "\"");
            append_Json.append(",\"text\":\"" + dingMsg.getHeadText() + "\"");
            append_Json.append("}");

            append_Json.append(",\"body\":{");
            append_Json.append("\"title\":\"" + dingMsg.getBodyTitle() + "\"");
            if (!dingMsg.getBodyForm().isEmpty()) {
                append_Json.append(",\"form\":[");
                for (String itemKey : dingMsg.getBodyForm().keySet()) {
                    append_Json.append("{");
                    append_Json.append("\"key\":\"" + itemKey + "\"");
                    append_Json.append(",\"value\":\"" + dingMsg.getBodyForm().get(itemKey) + "\"");
                    append_Json.append("},");
                }
                append_Json.deleteCharAt(append_Json.length() - 1);
                append_Json.append("]");
            }

            append_Json.append(",\"author\":\"" + dingMsg.getBodyAuthor() + "\"");
            append_Json.append("}");
            append_Json.append("}");
            append_Json.append("}");
            String str = RequestHelper.Post(url, append_Json.toString());
            DingPostReturnVal postVal = (DingPostReturnVal) FormatToJson.ParseFromJson(str);
            return postVal;
        } catch (Exception ex) {
            Log.DebugWriteError(ex.getMessage());
        }
        return null;
    }
}
