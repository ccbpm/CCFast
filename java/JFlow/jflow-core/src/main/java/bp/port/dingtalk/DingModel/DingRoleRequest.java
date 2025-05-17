package bp.port.dingtalk.DingModel;

/**
 * 获取指定角色的员工列表的消息体
 *
 * @author: scott
 * @date: 2024年07月26日 15:56
 */
public class DingRoleRequest {
    private String requestId;
    private String errcode;
    private String errmsg;
    private DingRolePageVo result;

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

    public DingRolePageVo getResult() {
        return result;
    }

    public void setResult(DingRolePageVo result) {
        this.result = result;
    }
}
