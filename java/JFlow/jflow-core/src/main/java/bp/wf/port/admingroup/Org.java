package bp.wf.port.admingroup;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import bp.web.*;
import bp.wf.port.admin2group.*;
import bp.wf.template.*;
import bp.wf.*;

/**
 * 独立组织
 */
public class Org extends EntityNoName {

    ///#region 属性

    /**
     * 父级组织编号
     */
    public final String getParentNo() throws Exception {
        return this.GetValStrByKey(OrgAttr.ParentNo);
    }

    public final void setParentNo(String value) throws Exception {
        this.SetValByKey(OrgAttr.ParentNo, value);
    }

    /**
     * 父级组织名称
     */
    public final String getParentName() throws Exception {
        return this.GetValStrByKey(OrgAttr.ParentName);
    }

    public final void setParentName(String value) throws Exception {
        this.SetValByKey(OrgAttr.ParentName, value);
    }

    /**
     * 父节点编号
     */
    public final String getAdminer() throws Exception {
        return this.GetValStrByKey(OrgAttr.Adminer);
    }

    public final void setAdminer(String value) throws Exception {
        this.SetValByKey(OrgAttr.Adminer, value);
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
        uac.IsDelete = true;
	   /* if (DataType.IsNullOrEmpty(this.getNo()) == true)
	    {
	        uac.IsUpdate = true;
	        uac.IsInsert = true;
	        return uac;
	    }*/
        uac.IsInsert = false;
        uac.IsUpdate = true;
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

        map.AddTBStringPK(OrgAttr.No, null, "编号(与部门编号相同)", true, false, 1, 30, 40);
        map.AddTBString(OrgAttr.Name, null, "组织名称", true, false, 0, 60, 200, true);
        map.AddTBString(OrgAttr.ParentNo, null, "父级组织编号", false, false, 0, 60, 200, true);
        map.AddTBString(OrgAttr.ParentName, null, "父级组织名称", false, false, 0, 60, 200, true);

        map.AddTBString(OrgAttr.Adminer, null, "主要管理员(创始人)", true, true, 0, 60, 200, true);
        map.AddTBString(OrgAttr.AdminerName, null, "管理员名称", true, true, 0, 60, 200, true);
        map.AddTBString("SSOUrl", null, "SSOUrl", true, false, 0, 200, 200, true);
        map.AddTBInt(OrgAttr.OrgSta, 0, "组织状态", true, false);
        map.AddTBInt("FlowNums", 0, "流程数", true, true);
        map.AddTBInt("FrmNums", 0, "表单数", true, true);
        map.AddTBInt("Users", 0, "用户数", true, true);
        map.AddTBInt("Depts", 0, "部门数", true, true);
        map.AddTBInt("GWFS", 0, "运行中流程", true, true);
        map.AddTBInt("GWFSOver", 0, "结束的流程", true, true);
        map.AddTBInt(OrgAttr.Idx, 0, "排序", true, false);

        RefMethod rm = new RefMethod();
        rm.Title = "检查正确性";
        rm.ClassMethodName = this.toString() + ".DoCheck";
        map.AddRefMethod(rm);

        rm = new RefMethod();
        rm.Title = "修改主管理员";
        rm.ClassMethodName = this.toString() + ".ChangeAdminer";
        rm.getHisAttrs().AddTBString("adminer", null, "新主管理员编号", true, false, 0, 100, 100);
        map.AddRefMethod(rm);

        rm = new RefMethod();
        rm.Title = "取消独立组织";
        rm.ClassMethodName = this.toString() + ".DeleteOrg";
        rm.Warning = "您确定要取消独立组织吗？系统将要删除该组织以及该组织的管理员，但是不删除部门数据.";
        map.AddRefMethod(rm);

        //管理员.
        map.AddDtl(new OrgAdminers(), OrgAdminerAttr.OrgNo, null, DtlEditerModel.DtlSearch, "icon-people");

        //rm = new RefMethod();
        //rm.Title = "在集团下新增组织";
        //rm.ClassMethodName = this.ToString() + ".AddOrg";
        //rm.getHisAttrs().AddTBString("no", null, "组织编号", true, false, 0, 100, 100);
        //rm.getHisAttrs().AddTBString("name", null, "组织名称", true, false, 0, 100, 100);
        //rm.getHisAttrs().AddTBString("adminer", null, "管理员编号", true, false, 0, 100, 100);
        //rm.getHisAttrs().AddTBString("adminName", null, "管理员名称", true, false, 0, 100, 100);
        //map.AddRefMethod(rm);

        this.set_enMap(map);
        return this.get_enMap();
    }

    ///#endregion

    /**
     * 调用admin2Group的检查.
     * 1. 是否出现错误.
     * 1. 数据是否完整.
     *
     * @return
     */
    public final String DoCheck() throws Exception {
        bp.wf.port.admin2group.Org org = new bp.wf.port.admin2group.Org(this.getNo());
        return org.DoCheck();
    }

    /**
     * 去掉独立组织
     *
     * @return
     */
    public final String DeleteOrg() throws Exception {
        if (WebUser.getNo().equals("admin") == false) {
            return "err@只有admin帐号才可以执行。";
        }

        if (this.getNo().equals("100") == true) {
            return "err@admin组织不能取消.";
        }

        //流程类别.
        FlowSorts fss = new FlowSorts();
        fss.Retrieve(OrgAdminerAttr.OrgNo, this.getNo(), null);
        for (FlowSort en : fss.ToJavaList()) {
            Flows fls = new Flows();
            fls.Retrieve(FlowAttr.FK_FlowSort, en.getNo(), null);

            if (fls.size() != 0) {
                return "err@在流程目录：" + en.getName() + "有[" + fls.size() + "]个流程没有删除。";
            }
        }

        //表单类别.
        SysFormTrees ftTrees = new SysFormTrees();
        ftTrees.Retrieve(SysFormTreeAttr.OrgNo, this.getNo(), null);
        for (FlowSort en : fss.ToJavaList()) {
            bp.sys.MapDatas mds = new bp.sys.MapDatas();
            mds.Retrieve(bp.sys.MapDataAttr.FK_FormTree, en.getNo(), null);
            if (!mds.isEmpty()) {
                return "err@在表单目录：" + en.getName() + "有[" + mds.size() + "]个表单没有删除。";
            }
        }

        Org oa = new Org();
        oa.Delete(OrgAttr.No, this.getNo());

        OrgAdminers oas = new OrgAdminers();
        oas.Delete(OrgAdminerAttr.OrgNo, this.getNo());

        FlowSorts fs = new FlowSorts();
        fs.Delete(OrgAdminerAttr.OrgNo, this.getNo());

        fss.Delete(OrgAdminerAttr.OrgNo, this.getNo()); //删除流程目录.
        ftTrees.Delete(SysFormTreeAttr.OrgNo, this.getNo()); //删除表单目录。

        //更新到admin的组织下.
        String sqls = "UPDATE Port_Emp SET OrgNo='" + WebUser.getOrgNo() + "' WHERE OrgNo='" + this.getNo() + "'";
        sqls += "@UPDATE Port_Dept SET OrgNo='" + WebUser.getOrgNo() + "' WHERE OrgNo='" + this.getNo() + "'";
        sqls += "@UPDATE Port_DeptEmp SET OrgNo='" + WebUser.getOrgNo() + "' WHERE OrgNo='" + this.getNo() + "'";
        sqls += "@UPDATE Port_DeptEmpStation SET OrgNo='" + WebUser.getOrgNo() + "' WHERE OrgNo='" + this.getNo() + "'";
        DBAccess.RunSQLs(sqls);

        this.Delete();
        return "info@成功注销组织,请关闭窗口刷新页面.";
    }

    /**
     * 更改管理员（admin才能操作）
     *
     * @param adminer
     * @return
     */
    public final String ChangeAdminer(String adminer) throws Exception {
        if (WebUser.getNo().equals("admin") == false) {
            return "err@非admin管理员，您无法执行该操作.";
        }

        bp.port.Emp emp = new bp.port.Emp();
        emp.setUserID(adminer);
        if (emp.RetrieveFromDBSources() == 0) {
            return "err@管理员编号错误.";
        }

        String old = this.getAdminer();

        this.setAdminer(emp.getUserID());
        this.setAdminerName(emp.getName());
        this.Update();

        //检查超级管理员是否存在？
        OrgAdminer oa = new OrgAdminer();
        oa.setEmpNo(old);
        oa.setOrgNo(this.getNo());
        oa.Delete(OrgAdminerAttr.FK_Emp, old, OrgAdminerAttr.OrgNo, this.getNo());

        //插入到管理员.
        oa.setEmpNo(emp.getUserID());
        oa.Save();

        //检查超级管理员是否存在？
        return "修改成功,请关闭当前记录重新打开.";
    }
}
