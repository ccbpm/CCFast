package bp.port.dingtalk.DingModel;


/**
 * 部门列表明细
 */
public class DepartMentDetailInfo {
    /**
     * 部门id
     */
    private String id;

    public final String getId() {
        return id;
    }

    public final void setId(String value) {
        id = value;
    }

    /**
     * 部门名称
     */
    private String name;

    public final String getName() {
        return name;
    }

    public final void setName(String value) {
        name = value;
    }

    /**
     * 父部门id，根部门为1
     */
    private String parentid;

    public final String getParentid() {
        return parentid;
    }

    public final void setParentid(String value) {
        parentid = value;
    }

    /**
     * 是否同步创建一个关联此部门的企业群, true表示是, false表示不是
     */
    private String createDeptGroup;

    public final String getCreateDeptGroup() {
        return createDeptGroup;
    }

    public final void setCreateDeptGroup(String value) {
        createDeptGroup = value;
    }

    /**
     * 当群已经创建后，是否有新人加入部门会自动加入该群, true表示是, false表示不是
     */
    private String autoAddUser;

    public final String getAutoAddUser() {
        return autoAddUser;
    }

    public final void setAutoAddUser(String value) {
        autoAddUser = value;
    }
}
