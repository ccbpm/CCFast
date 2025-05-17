package bp.port.dingtalk.DingModel;

/**
 * 功能描述
 *
 * @author: scott
 * @date: 2024年07月26日 14:30
 */
public class DingAccessToken {
    //返回码
    private String errcode;
    //对返回码的文本描述内容
    private String errmsg;
    private String accessToken;
    private String expiresIn;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }
}
