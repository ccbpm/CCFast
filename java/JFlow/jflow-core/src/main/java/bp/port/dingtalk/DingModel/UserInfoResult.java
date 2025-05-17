package bp.port.dingtalk.DingModel;

/**
 * 功能描述
 *
 * @author: scott
 * @date: 2024年07月26日 14:24
 */
public class UserInfoResult {
    private String admin;
    private String remark;
    private String email;
    private String mobile;

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
