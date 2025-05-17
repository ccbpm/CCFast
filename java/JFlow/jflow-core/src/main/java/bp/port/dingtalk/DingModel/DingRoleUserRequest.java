package bp.port.dingtalk.DingModel;

/**
 * 功能描述
 *
 * @author: scott
 * @date: 2024年07月26日 16:01
 */
public class DingRoleUserRequest {
    private String requestId;
    private String errcode;
    private String errmsg;
    private DingRoleUserPageVo result;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

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

    public DingRoleUserPageVo getResult() {
        return result;
    }

    public void setResult(DingRoleUserPageVo result) {
        this.result = result;
    }
}
