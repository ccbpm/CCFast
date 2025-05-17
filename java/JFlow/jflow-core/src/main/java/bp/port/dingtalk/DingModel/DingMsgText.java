package bp.port.dingtalk.DingModel;

/**
 * 文本和超链接消息
 *
 * @author: scott
 * @date: 2024年07月26日 18:13
 */
public class DingMsgText extends DingMsg {
    private String msgtype;
    private String content;

    /**
     * 必须：是- 消息类型，此时固定为：text
     * @return
     */
    public String getMsgtype() {
        return "text";
    }

    /**
     * 必须：是- 消息内容
     * @return
     */
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
