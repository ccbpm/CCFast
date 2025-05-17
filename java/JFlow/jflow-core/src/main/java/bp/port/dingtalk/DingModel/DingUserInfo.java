package bp.port.dingtalk.DingModel;


public class DingUserInfo {
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

    private UserInfoResult result;

    public final UserInfoResult getResult() {
        return result;
    }

    public final void setResult(UserInfoResult value) {
        result = value;
    }
}
