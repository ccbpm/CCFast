package bp.port.dingtalk.DingModel;

/**
 * 功能描述
 *
 * @author: scott
 * @date: 2024年07月26日 18:37
 */
public class DingMsg {

    //发送的业务编号
    private Long WorkID;
    //钉钉访问许可
    private String AccessToken;
    //必须：是- 消息发送者员工ID
    private String sender;
    //必须：是- 群消息或者个人聊天会话Id，(通过JSAPI之pickConversation接口唤起联系人界面选择之后即可拿到会话ID，之后您可以使用获取到的cid调用此接口）
    private String cid;
    //必须：否- 员工ID列表（消息接收者，多个接收者用’ | '分隔）。特殊情况：指定为@all，则向该企业应用的全部成员发送
    private String touser;
    //必须：否- 部门id列表，多个接收者用’ | '分隔。当touser为@all时忽略本参数 touser或者toparty 二者有一个必填
    private String toparty;
    //必须：是- 企业应用id，这个值代表以哪个应用的名义发送消息
    private String agentid;


    /**
     * 发送的业务编号
     *
     * @return
     */
    public Long getWorkID() {
        return WorkID;
    }

    public void setWorkID(Long workID) {
        WorkID = workID;
    }

    /**
     * 钉钉访问许可
     *
     * @return
     */
    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(String accessToken) {
        AccessToken = accessToken;
    }

    ///#region 发送普通会话消息

    /**
     * 必须：是- 消息发送者员工ID
     *
     * @return
     */
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * 必须：是- 群消息或者个人聊天会话Id，
     * (通过JSAPI之pickConversation接口唤起联系人界面选择之后即可拿到会话ID，之后您可以使用获取到的cid调用此接口）
     *
     * @return
     */
    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
    ///#endregion

    ///#region 发送企业会话消息

    /**
     * 必须：否- 员工ID列表（消息接收者，多个接收者用’ | '分隔）。特殊情况：指定为@all，则向该企业应用的全部成员发送
     *
     * @return
     */
    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    /**
     * 必须：否- 部门id列表，多个接收者用’ | '分隔。当touser为@all时忽略本参数 touser或者toparty 二者有一个必填
     *
     * @return
     */
    public String getToparty() {
        return toparty;
    }

    public void setToparty(String toparty) {
        this.toparty = toparty;
    }

    /**
     * //必须：是- 企业应用id，这个值代表以哪个应用的名义发送消息
     *
     * @return
     */
    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }
    ///#endregion
}
