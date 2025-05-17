package bp.port.dingtalk.DingModel;

/**
 * 超链接消息
 *
 * @author: scott
 * @date: 2024年07月26日 18:15
 */
public class DingMsgLink extends DingMsg {
    private String msgtype;
    private String messageUrl;
    private String picUrl;
    private String title;
    private String text;

    /**
     * 必须：是- 消息类型，此时固定为：link
     * @return
     */
    public String getMsgtype() {
        return "link";
    }

    /**
     * 必须：是- 消息点击链接地址
     * @return
     */
    public String getMessageUrl() {
        return messageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }

    /**
     * 必须：是- 图片媒体文件id，可以调用上传媒体文件接口获取
     * @return
     */
    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    /**
     * 必须：是- 消息标题
     * @return
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 必须：是- 消息内容
     * @return
     */
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
