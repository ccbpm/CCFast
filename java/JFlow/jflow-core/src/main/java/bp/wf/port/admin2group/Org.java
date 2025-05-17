package bp.wf.port.admin2group;

import bp.da.*;
import bp.difference.*;
import bp.en.*;
import bp.en.Map;
import bp.sys.CCBPMRunModel;
import bp.wf.template.*;

/**
 * 独立组织
 */
public class Org extends EntityNoName {

    ///#region 属性

    /**
     * 父节点编号
     */
    public final String getAdminer() throws Exception {
        return this.GetValStrByKey(OrgAttr.Adminer);
    }

    public final void setAdminer(String value) throws Exception {
        this.SetValByKey(OrgAttr.Adminer, value);
    }
    public final String getParentNo()
    {
        return this.GetValStrByKey(OrgAttr.ParentNo);
    }
    public final void setParentNo(String value)
    {
        this.SetValByKey(OrgAttr.ParentNo, value);
    }
    public final String getParentName()
    {
        return this.GetValStrByKey(OrgAttr.ParentName);
    }
    public final void setParentName(String value)
    {
        this.SetValByKey(OrgAttr.ParentName, value);
    }

    /**
     * 管理员名称
     */
    public final String getAdminerName() throws Exception {
        return this.GetValStrByKey(OrgAttr.AdminerName);
    }

    public final void setAdminerName(String value) throws Exception {
        this.SetValByKey(OrgAttr.AdminerName, value);
    }

    ///#endregion


    ///#region 构造函数

    /**
     * 独立组织
     */
    public Org() {
    }

    /**
     * 独立组织
     *
     * @param no 编号
     */
    public Org(String no) throws Exception {
        super(no);
    }

    ///#endregion


    ///#region 重写方法

    /**
     * UI界面上的访问控制
     */
    @Override
    public UAC getHisUAC() {
        UAC uac = new UAC();
        uac.OpenForSysAdmin();
        uac.IsDelete = false;
        uac.IsInsert = false;
        return uac;
    }

    /**
     * Map
     */
    @Override
    public Map getEnMap() {
        if (this.get_enMap() != null) {
            return this.get_enMap();
        }

        Map map = new Map("Port_Org", "独立组织");
        // map.EnType = EnType.View; //独立组织是一个视图.


        ///#region 基本属性.
        map.AddTBStringPK(OrgAttr.No, null, "编号", true, false, 1, 30, 40);
        map.AddTBString(OrgAttr.Name, null, "组织名称", true, false, 0, 60, 200, true);

        map.AddTBString(OrgAttr.Adminer, null, "创始人", true, true, 0, 60, 200, false);
        map.AddTBString(OrgAttr.AdminerName, null, "名称", true, true, 0, 60, 200, false);

        map.AddTBString(OrgAttr.ParentNo, null, "ParentNo", false, false, 0, 60, 200, false);
        map.AddTBString(OrgAttr.ParentName, null, "ParentName", false, false, 0, 60, 200, false);

        map.AddTBInt("FlowNums", 0, "流程数", true, true);
        map.AddTBInt("FrmNums", 0, "表单数", true, true);
        map.AddTBInt("Users", 0, "用户数", true, true);
        map.AddTBInt("Depts", 0, "部门数", true, true);

        map.AddTBInt("GWFS", 0, "运行中流程", true, true);
        map.AddTBInt("GWFSOver", 0, "结束的流程", true, true);

        map.AddTBString("PrivateKey", null, "PrivateKey", true, false, 0, 100, 200, true);
        map.SetHelperAlert("PrivateKey", "接口调用密钥.");

        map.AddTBString("SSOUrl", null, "SSOUrl", true, false, 0, 100, 200, true);
        map.SetHelperAlert("SSOUrl", "单点登录的Url:配置格式: http://xxxx.xxx.xxx.xx:9090/XX.do?Token={$Token}");

        map.AddDDLStringEnum("JieMi", "None", "解密方式", "@None=不解密@AES=AES解密", true, null, false);
        map.AddTBString("KeyOfJieMi", null, "盐值", true, false, 0, 100, 200, false);


        ///#endregion 基本属性.


        ///#region 方法执行.
        RefMethod rm = new RefMethod();

        //rm = new RefMethod();
        //rm.Title = "组织结构";
        //rm.ClassMethodName = this.ToString() + ".DoOrganization";
        //rm.Icon = "icon-organization";
        //rm.refMethodType = RefMethodType.RightFrameOpen;
        //map.AddRefMethod(rm);

        //rm = new RefMethod();
        //rm.Title = "人员台账";
        //rm.ClassMethodName = this.ToString() + ".DoEmps";
        //rm.refMethodType = RefMethodType.RightFrameOpen;
        //map.AddRefMethod(rm);


        //rm = new RefMethod();
        //rm.Title = "角色类型";
        //rm.ClassMethodName = this.ToString() + ".DoStationTypes";
        //rm.refMethodType = RefMethodType.RightFrameOpen;
        //map.AddRefMethod(rm);

        //rm = new RefMethod();
        //rm.Title = "角色";
        //rm.ClassMethodName = this.ToString() + ".DoStations";
        //rm.refMethodType = RefMethodType.RightFrameOpen;
        //map.AddRefMethod(rm);

        //rm = new RefMethod();
        //rm.Title = "清空菜单权限缓存";
        //rm.ClassMethodName = this.ToString() + ".AddClearUserRegedit";
        //rm.refMethodType = RefMethodType.Func;
        //map.AddRefMethod(rm);

        rm = new RefMethod();
        rm.Title = "检查正确性";
        rm.ClassMethodName = this.toString() + ".DoCheck";
        rm.Icon = "icon-check";
        //rm.getHisAttrs().AddTBString("No", null, "子公司管理员编号", true, false, 0, 100, 100);
        map.AddRefMethod(rm);

        rm = new RefMethod();
        rm.Title = "增加管理员";
        rm.Icon = "icon-user";
        rm.ClassMethodName = this.toString() + ".AddAdminer";
        rm.getHisAttrs().AddTBString("adminer", null, "管理员编号", true, false, 0, 100, 100);
        map.AddRefMethod(rm);
        //管理员.
        map.AddDtl(new OrgAdminers(), OrgAdminerAttr.OrgNo, null, DtlEditerModel.DtlSearch, "icon-people");


        ///#endregion 方法执行.


        this.set_enMap(map);
        return this.get_enMap();
    }

    ///#endregion

    public final String DoOrganization() {
        return "/GPM/Organization.htm";
    }

    public final String DoEmps() {
        return "/WF/Comm/Search.htm?EnsName=bp.port.Emps";
    }

    public final String DoStationTypes() {
        return "/WF/Comm/Ens.htm?EnsName=bp.port.StationTypes";
    }

    public final String DoStations() {
        return "/WF/Comm/Search.htm?EnsName=bp.port.Stations";
    }

    /**
     * 清除缓存
     *
     * @return
     */
    public final String AddClearUserRegedit() throws Exception {
        DBAccess.RunSQL("DELETE FROM Sys_UserRegedit WHERE OrgNo='" + this.getNo() + "' AND CfgKey='Menus'");
        return "执行成功.";
    }

    @Override
    protected boolean beforeUpdateInsertAction() throws Exception {
        this.SetValByKey("FlowNums", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS a FROM WF_Flow WHERE OrgNo='" + this.getNo() + "'"));
        this.SetValByKey("FrmNums", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS a FROM Sys_MapData WHERE OrgNo='" + this.getNo() + "'"));

        this.SetValByKey("Users", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS a FROM Port_Emp WHERE OrgNo='" + this.getNo() + "'"));
        this.SetValByKey("Depts", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS a FROM Port_Dept WHERE OrgNo='" + this.getNo() + "'"));
        this.SetValByKey("GWFS", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS a FROM WF_GenerWorkFlow WHERE OrgNo='" + this.getNo() + "' AND WFState!=3"));
        this.SetValByKey("GWFSOver", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS a FROM WF_GenerWorkFlow WHERE OrgNo='" + this.getNo() + "' AND WFState=3"));
        return super.beforeUpdateInsertAction();
    }

    public final String AddAdminer(String adminer) throws Exception {
        bp.port.Emp emp = new bp.port.Emp();
        if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS) {
            emp.setNo(this.getNo() + "_" + adminer);
        } else {
            emp.setNo(adminer);
        }

        if (emp.RetrieveFromDBSources() == 0) {
            return "err@管理员编号错误.";
        }

        //检查超级管理员是否存在？
        OrgAdminer oa = new OrgAdminer();
        oa.setEmpNo(emp.getNo());
        oa.setEmpName(emp.getName());
        oa.setOrgNo(this.getNo());
        oa.setMyPK(this.getNo() + "_" + oa.getEmpNo());
        if (oa.RetrieveFromDBSources() == 1) {
            return "err@管理员已经存在.";
        }
        //插入到管理员.
        oa.setEmpNo(emp.getNo());
        oa.DirectInsert();

        //如果不在同一个组织.就给他一个兼职部门.
        bp.port.DeptEmps depts = new bp.port.DeptEmps();
        depts.Retrieve("OrgNo", this.getNo(), "FK_Emp", emp.getNo(), null);
        if (depts.isEmpty()) {
            bp.port.DeptEmp de = new bp.port.DeptEmp();
            de.setDeptNo(this.getNo());
            de.setEmpNo(emp.getNo());
            de.setMyPK(this.getNo() + "_" + emp.getNo());
            de.setOrgNo(this.getNo());
            de.Save();
        }

        //检查超级管理员是否存在？
        return "管理员增加成功,请关闭当前记录重新打开,请给管理员[ " + emp.getName() + "]分配权限";
    }

    private void SetOrgNo(String deptNo) throws Exception {
        DBAccess.RunSQL("UPDATE Port_Emp SET OrgNo='" + this.getNo() + "' WHERE FK_Dept='" + deptNo + "'");
        DBAccess.RunSQL("UPDATE Port_DeptEmp SET OrgNo='" + this.getNo() + "' WHERE FK_Dept='" + deptNo + "'");
        DBAccess.RunSQL("UPDATE Port_DeptEmpStation SET OrgNo='" + this.getNo() + "' WHERE FK_Dept='" + deptNo + "'");

        Depts depts = new Depts();
        depts.Retrieve(DeptAttr.ParentNo, deptNo, null);
        String sql = "";
        for (Dept item : depts.ToJavaList()) {
            //如果部门下组织不能检查更新
            sql = "SELECT COUNT(*) From Port_Org Where No='" + item.getNo() + "'";
            if (DBAccess.RunSQLReturnValInt(sql) == 1) {
                continue;
            }

            DBAccess.RunSQL("UPDATE Port_Emp SET OrgNo='" + this.getNo() + "' WHERE FK_Dept='" + item.getNo() + "'");
            DBAccess.RunSQL("UPDATE Port_DeptEmp SET OrgNo='" + this.getNo() + "' WHERE FK_Dept='" + item.getNo() + "'");
            DBAccess.RunSQL("UPDATE Port_DeptEmpStation SET OrgNo='" + this.getNo() + "' WHERE FK_Dept='" + item.getNo() + "'");

            if (item.getOrgNo().equals(this.getNo()) == false) {
                item.setOrgNo(this.getNo());
                item.Update();
            }
            //递归调用.
            SetOrgNo(item.getNo());
        }
    }

    public final String DoCheck() throws Exception {
        if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS) {
            return "err@saas版的检查在开发中..";
        }

        String err = "";


        ///#region 组织结构信息检查.
        //检查orgNo的部门是否存在？
        Dept dept = new Dept();
        dept.setNo(this.getNo());
        if (dept.RetrieveFromDBSources() == 0) {
            return "err@部门组织结构树上缺少[" + this.getNo() + "]的部门.";
        }

        if (this.getName().equals(dept.getName()) == false) {
            this.setName(dept.getName());
            err += "info@部门名称与组织名称已经同步.";
        }
        this.Update(); //执行更新.

        //设置子集部门，的OrgNo.
        if (DBAccess.IsView("Port_Dept") == false) {
            this.SetOrgNo(this.getNo());
        }

        ///#endregion 组织结构信息检查.


        ///#region 检查流程树.
        FlowSort fs = new bp.wf.template.FlowSort();
        fs.setNo(this.getNo());
        if (fs.RetrieveFromDBSources() == 1) {
            fs.setOrgNo(this.getNo());
            fs.setName("流程树");
            fs.DirectUpdate();
        } else {
            //获得根目录节点.
            FlowSort root = new FlowSort();
            int i = root.Retrieve(FlowSortAttr.ParentNo, "0");

            //设置流程树权限.
            fs.setNo(this.getNo());
            fs.setName(this.getName());
            fs.setName("流程树");
            fs.setParentNo(root.getNo());
            fs.setOrgNo(this.getNo());
            fs.setIdx(999);
            fs.DirectInsert();

            //创建下一级目录.
            bp.en.EntityTree tempVar = fs.DoCreateSubNode(null);
            FlowSort en = tempVar instanceof FlowSort ? (FlowSort) tempVar : null;

            en.setName("发文流程");
            en.setOrgNo(this.getNo());
            en.setDomainExt("FaWen");
            en.DirectUpdate();

            bp.en.EntityTree tempVar2 = fs.DoCreateSubNode(null);
            en = tempVar2 instanceof FlowSort ? (FlowSort) tempVar2 : null;
            en.setName("收文流程");
            en.setOrgNo(this.getNo());
            en.setDomainExt("ShouWen");
            en.DirectUpdate();

            bp.en.EntityTree tempVar3 = fs.DoCreateSubNode(null);
            en = tempVar3 instanceof FlowSort ? (FlowSort) tempVar3 : null;
            en.setName("业务流程");
            en.setOrgNo(this.getNo());
            en.setDomainExt("Work");
            en.DirectUpdate();
            bp.en.EntityTree tempVar4 = fs.DoCreateSubNode(null);
            en = tempVar4 instanceof FlowSort ? (FlowSort) tempVar4 : null;
            en.setName("会议流程");
            en.setOrgNo(this.getNo());
            en.setDomainExt("Meet");
            en.DirectUpdate();
        }

        ///#endregion 检查流程树.


        ///#region 检查表单树.
        //表单根目录.
        SysFormTree ftRoot = new SysFormTree();
        int val = ftRoot.Retrieve(FlowSortAttr.ParentNo, "0");
        if (val == 0) {
            val = ftRoot.Retrieve(FlowSortAttr.No, "100");
            if (val == 0) {
                ftRoot.setNo("100");
                ftRoot.setName("表单库");
                ftRoot.setParentNo("0");
                ftRoot.Insert();
            } else {
                ftRoot.setParentNo("0");
                ftRoot.setName("表单库");
                ftRoot.Update();
            }
        }

        //设置表单树权限.
        SysFormTree ft = new SysFormTree();
        ft.setNo(this.getNo());
        if (ft.RetrieveFromDBSources() == 0) {
            ft.setName(this.getName());
            ft.setName("表单树(" + this.getName() + ")");
            ft.setParentNo(ftRoot.getNo());
            ft.setOrgNo(this.getNo());
            ft.setIdx(999);
            ft.DirectInsert();

            //创建两个目录.
            bp.en.EntityTree tempVar5 = ft.DoCreateSubNode(null);
            SysFormTree mySubFT = tempVar5 instanceof SysFormTree ? (SysFormTree) tempVar5 : null;
            mySubFT.setName("表单目录1");
            mySubFT.setOrgNo(this.getNo());
            mySubFT.DirectUpdate();


            bp.en.EntityTree tempVar6 = ft.DoCreateSubNode(null);
            mySubFT = tempVar6 instanceof SysFormTree ? (SysFormTree) tempVar6 : null;
            mySubFT.setName("表单目录2");
            mySubFT.setOrgNo(this.getNo());
            mySubFT.DirectUpdate();
        } else {
            ft.setName(this.getName());
            ft.setName("表单树(" + this.getName() + ")"); //必须这个命名，否则找不到。
            ft.setParentNo(ftRoot.getNo());
            ft.setOrgNo(this.getNo());
            ft.setIdx(999);
            ft.DirectUpdate();

            //检查数量.
            SysFormTrees frmSorts = new SysFormTrees();
            frmSorts.Retrieve("OrgNo", this.getNo(), null);
            if (frmSorts.size() <= 1) {
                //创建两个目录.
                bp.en.EntityTree tempVar7 = ft.DoCreateSubNode(null);
                SysFormTree mySubFT = tempVar7 instanceof SysFormTree ? (SysFormTree) tempVar7 : null;
                mySubFT.setName("表单目录1");
                mySubFT.setOrgNo(this.getNo());
                mySubFT.DirectUpdate();

                bp.en.EntityTree tempVar8 = ft.DoCreateSubNode(null);
                mySubFT = tempVar8 instanceof SysFormTree ? (SysFormTree) tempVar8 : null;
                mySubFT.setName("表单目录2");
                mySubFT.setOrgNo(this.getNo());
                mySubFT.DirectUpdate();
            }
        }

        ///#endregion 检查表单树.


        ///#region 删除无效的数据.
        String sqls = "";
        if (DBAccess.IsView("Port_DeptEmp") == false) {
            sqls += "@DELETE FROM Port_DeptEmp WHERE FK_Dept not in (select no from port_dept)";
            sqls += "@DELETE FROM Port_DeptEmp WHERE FK_Emp not in (select no from Port_Emp)";
        }
        if (DBAccess.IsView("Port_DeptEmpStation") == false) {
            sqls += "@DELETE FROM Port_DeptEmpStation WHERE FK_Dept not in (select no from port_dept)";
            sqls += "@DELETE FROM Port_DeptEmpStation WHERE FK_Emp not in (select no from Port_Emp)";
            sqls += "@DELETE FROM Port_DeptEmpStation WHERE FK_Station not in (select no from port_Station)";
        }
        //删除无效的管理员,
        if (DBAccess.IsView("Port_OrgAdminer") == false) {
            sqls += "@DELETE from Port_OrgAdminer where OrgNo not in (select No from port_dept)";
            sqls += "@DELETE from Port_OrgAdminer where FK_Emp not in (select No from Port_Emp)";
        }
        //删除无效的组织.
        if (DBAccess.IsView("Port_Org") == false) {
            sqls += "@DELETE from Port_Org where No not in (select No from port_dept)";
        }
        DBAccess.RunSQLs(sqls);

        ///#endregion 删除无效的数据.


        ///#region 检查人员信息.. 应该增加在整体检查的提示
        //String sql = "SELECT * FROM Port_Emp WHERE OrgNo NOT IN (SELECT No from Port_Dept )";
        //DataTable dt = DBAccess.RunSQLReturnTable(sql);
        //if (dt.Rows.size() != 0)
        //    err += " 人员表里有:" + dt.Rows.size() + "笔 组织编号有丢失. 请处理:" + sql;

        //sql = "SELECT * FROM Port_Emp WHERE FK_DEPT NOT IN (SELECT No from Port_Dept )";
        //dt = DBAccess.RunSQLReturnTable(sql);
        //if (dt.Rows.size() != 0)
        //    err += " 人员表里有:" + dt.Rows.size() + "笔数据部门编号丢失. 请处理:" + sql;

        ///#endregion 检查组织编号信息.

        if (DataType.IsNullOrEmpty(err) == true) {
            return "系统正确";
        }

        //检查表单树.
        return "err@" + err;
    }
}
