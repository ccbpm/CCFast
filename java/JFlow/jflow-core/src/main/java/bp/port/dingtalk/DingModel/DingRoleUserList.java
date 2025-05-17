package bp.port.dingtalk.DingModel;

import java.util.List;

/**
 * 获取指定角色的员工列表
 *
 * @author: scott
 * @date: 2024年07月26日 16:13
 */
public class DingRoleUserList {
    private String name;
    private String userid;
    private List<DingRoleUserScopes> manageScopes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<DingRoleUserScopes> getManageScopes() {
        return manageScopes;
    }

    public void setManageScopes(List<DingRoleUserScopes> manageScopes) {
        this.manageScopes = manageScopes;
    }
}
