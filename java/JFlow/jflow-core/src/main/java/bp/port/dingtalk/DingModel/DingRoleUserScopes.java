package bp.port.dingtalk.DingModel;

/**
 * 获取指定角色的员工所属的部门集合
 *
 * @author: scott
 * @date: 2024年07月26日 16:17
 */
public class DingRoleUserScopes {
    private String deptId;
    private String name;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
