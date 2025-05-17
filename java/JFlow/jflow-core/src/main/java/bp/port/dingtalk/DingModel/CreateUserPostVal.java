package bp.port.dingtalk.DingModel;

/**
 * 新增人员后消息
 */
public class CreateUserPostVal {
    /**
     * 返回码
     */
    private String errcode;

    public final String getErrcode() {
        return errcode;
    }

    public final void setErrcode(String value) {
        errcode = value;
    }

    /**
     * 对返回码的文本描述内容
     */
    private String errmsg;

    public final String getErrmsg() {
        return errmsg;
    }

    public final void setErrmsg(String value) {
        errmsg = value;
    }

    /**
     * 员工唯一标识userid
     */
    private String userid;

    public final String getUserid() {
        return userid;
    }

    public final void setUserid(String value) {
        userid = value;
    }
}
