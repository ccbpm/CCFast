package bp.port.dingtalk.DingModel;

import java.util.Hashtable;

/**
 * OA消息内容
 *
 * @author: scott
 * @date: 2024年07月26日 18:19
 */
public class DingMsgOA extends DingMsg {
    private String msgtype;
    private String messageUrl;
    private String headbgcolor;
    private String headtext;
    private String bodytitle;
    private Hashtable<String, Object> bodyform;
    private String bodycontent;
    private String bodyimage;
    private String bodyfilecount;
    private String bodyauthor;

    /**
     * 必须：是- 消息类型，此时固定为：oa
     *
     * @return
     */
    public String getMsgtype() {
        return "oa";
    }

    /**
     * 必须：是- 客户端点击消息时跳转到的H5地址
     *
     * @return
     */
    public String getMessageUrl() {
        return messageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }

    /**
     * 必须：是- 消息头部的背景颜色。长度限制为8个英文字符，其中前2为表示透明度，后6位表示颜色值。不要添加0x
     *
     * @return
     */
    public String getHeadBgcolor() {
        return headbgcolor;
    }

    public void setHeadBgcolor(String headbgcolor) {
        this.headbgcolor = headbgcolor;
    }

    /**
     * 必须：是- 消息的头部标题（仅适用于发送普通场景）
     *
     * @return
     */
    public String getHeadText() {
        return headtext;
    }

    public void setHeadText(String headtext) {
        this.headtext = headtext;
    }

    /**
     * 必须：否- 消息体的标题
     *
     * @return
     */
    public String getBodyTitle() {
        return bodytitle;
    }

    public void setBodyTitle(String bodytitle) {
        this.bodytitle = bodytitle;
    }

    /**
     * 必须：否- 消息体的表单，最多显示6个，超过会被隐藏
     *
     * @return
     */
    public Hashtable<String, Object> getBodyForm() {
        return bodyform;
    }

    public void setBodyForm(Hashtable bodyform) {
        this.bodyform = bodyform;
    }

    /**
     * 必须：否- 消息体的内容，最多显示3行
     *
     * @return
     */
    public String getBodyContent() {
        return bodycontent;
    }

    public void setBodyContent(String bodycontent) {
        this.bodycontent = bodycontent;
    }

    /**
     * 必须：否- 消息体中的图片media_id
     *
     * @return
     */
    public String getBodyImage() {
        return bodyimage;
    }

    public void setBodyImage(String bodyimage) {
        this.bodyimage = bodyimage;
    }

    /**
     * 必须：否- 自定义的附件数目。此数字仅供显示，钉钉不作验证
     *
     * @return
     */
    public String getBodyFileCount() {
        return bodyfilecount;
    }

    public void setBodyFileCount(String bodyfilecount) {
        this.bodyfilecount = bodyfilecount;
    }

    /**
     * 必须：否- 自定义的作者名字
     *
     * @return
     */
    public String getBodyAuthor() {
        return bodyauthor;
    }

    public void setBodyAuthor(String bodyauthor) {
        this.bodyauthor = bodyauthor;
    }
}
