package bp.port.dingtalk.DingModel;

import java.util.List;

/**
 * 角色分组
 *
 * @author: scott
 * @date: 2024年07月26日 15:59
 */
public class DingRoleGroupList {
    private String groupId;
    private String name;
    private List<DingRoleList> roles;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DingRoleList> getRoles() {
        return roles;
    }

    public void setRoles(List<DingRoleList> roles) {
        this.roles = roles;
    }
}
