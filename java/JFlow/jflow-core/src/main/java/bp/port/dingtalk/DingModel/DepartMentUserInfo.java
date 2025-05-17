package bp.port.dingtalk.DingModel;

/**
 * 部门人员详细信息
 *
 * @author: scott
 * @date: 2024年07月26日 15:27
 */
public class DepartMentUserInfo {
    //员工唯一标识ID（不可修改）
    private String userid;
    //成员名称
    private String name;
    //表示人员在此部门中的排序，列表是按order的倒序排列输出的，即从大到小排列输出的
    private String order;
    //表示人员在此部门中的排序，列表是按order的倒序排列输出的，即从大到小排列输出的
    private String orderInDepts;
    //钉钉ID
    private String dingId;
    //手机号（ISV不可见）
    private String mobile;
    //分机号（ISV不可见）
    private String tel;
    //办公地点（ISV不可见）
    private String workPlace;
    //备注（ISV不可见）
    private String remark;
    //是否是企业的管理员, true表示是, false表示不是
    private Boolean isAdmin;
    //是否为企业的老板, true表示是, false表示不是
    private Boolean isBoss;
    //是否隐藏号码, true表示是, false表示不是
    private Boolean isHide;
    //是否是部门的主管, true表示是, false表示不是
    private Boolean isLeader;
    //表示该用户是否激活了钉钉
    private Boolean active;
    //成员所属部门id列表"department": [1, 2]
    private Object department;
    //职位信息
    private String position;
    //邮箱
    private String email;
    //头像url"./dingtalk/abc.jpg"
    private String avatar;
    //员工工号
    private String jobnumber;
    /**
     * 扩展属性，可以设置多种属性(但手机上最多只能显示10个扩展属性，具体显示哪些属性
     * 请到OA管理后台->设置->通讯录信息设置和OA管理后台->设置->手机端显示信息设置)
     * extattr": {"爱好":"旅游","年龄":"24"}
     */
    private Object extattr;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderInDepts() {
        return orderInDepts;
    }

    public void setOrderInDepts(String orderInDepts) {
        this.orderInDepts = orderInDepts;
    }

    public String getDingId() {
        return dingId;
    }

    public void setDingId(String dingId) {
        this.dingId = dingId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getBoss() {
        return isBoss;
    }

    public void setBoss(Boolean boss) {
        isBoss = boss;
    }

    public Boolean getHide() {
        return isHide;
    }

    public void setHide(Boolean hide) {
        isHide = hide;
    }

    public Boolean getLeader() {
        return isLeader;
    }

    public void setLeader(Boolean leader) {
        isLeader = leader;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Object getDepartment() {
        return department;
    }

    public void setDepartment(Object department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getJobnumber() {
        return jobnumber;
    }

    public void setJobnumber(String jobnumber) {
        this.jobnumber = jobnumber;
    }

    public Object getExtattr() {
        return extattr;
    }

    public void setExtattr(Object extattr) {
        this.extattr = extattr;
    }
}
