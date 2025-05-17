package bp.wf.port.admin2group;

import bp.en.*;
import bp.en.Map;
import bp.port.*;
import bp.wf.template.*;

import java.util.*;

/**
 * 组织管理员
 */
public class OrgAdminer extends EntityMyPK {

    ///#region 属性
    public final String getFrmTrees() throws Exception {
        return this.GetValStringByKey(OrgAdminerAttr.FrmTrees);
    }

    public final void setFrmTrees(String value) throws Exception {
        this.SetValByKey(OrgAdminerAttr.FrmTrees, value);
    }

    public final String getFlowSorts() throws Exception {
        return this.GetValStringByKey(OrgAdminerAttr.FlowSorts);
    }

    public final void setFlowSorts(String value) throws Exception {
        this.SetValByKey(OrgAdminerAttr.FlowSorts, value);
    }

    public final String getEmpName() throws Exception {
        return this.GetValStringByKey(OrgAdminerAttr.EmpName);
    }

    public final void setEmpName(String value) throws Exception {
        this.SetValByKey(OrgAdminerAttr.EmpName, value);
    }

    public final String getEmpNo() throws Exception {
        return this.GetValStringByKey(OrgAdminerAttr.FK_Emp);
    }

    public final void setEmpNo(String value) throws Exception {
        this.SetValByKey(OrgAdminerAttr.FK_Emp, value);
    }

    public final String getOrgNo() throws Exception {
        return this.GetValStringByKey(OrgAdminerAttr.OrgNo);
    }

    public final void setOrgNo(String value) throws Exception {
        this.SetValByKey(OrgAdminerAttr.OrgNo, value);
    }

    ///#endregion


    ///#region 构造方法
    @Override
    public UAC getHisUAC() {
        UAC uac = new UAC();
        uac.OpenForSysAdmin();
        uac.IsInsert = false;
        //uac.IsDelete = false;
        // uac.IsInsert = false;
        return uac;
    }

    /**
     * 组织管理员
     */
    public OrgAdminer() {
    }

    /**
     * 组织管理员
     *
     * @param mypk
     */
    public OrgAdminer(String mypk) throws Exception {
        this.setMyPK(mypk);
        this.Retrieve();
    }

    /**
     * 组织管理员
     */
    @Override
    public Map getEnMap() {
        if (this.get_enMap() != null) {
            return this.get_enMap();
        }
        Map map = new Map("Port_OrgAdminer", "组织管理员");

        //不能注释掉.
        map.AddMyPK(false);

        map.AddTBString(OrgAdminerAttr.OrgNo, null, "组织", false, false, 0, 100, 20);
        map.AddTBString(OrgAdminerAttr.FK_Emp, null, "管理员账号", true, true, 0, 100, 20);
        map.AddTBString(OrgAdminerAttr.EmpName, null, "管理员名称", true, true, 0, 50, 20);

        map.AddTBStringDoc(OrgAdminerAttr.FlowSorts, null, "管理的流程目录", true, true, true, 10);
        map.AddTBStringDoc(OrgAdminerAttr.FrmTrees, null, "管理的表单目录", true, true, true, 10);

        map.getAttrsOfOneVSM().AddGroupPanelModel(new OAFlowSorts(), new FlowSorts(), OAFlowSortAttr.RefOrgAdminer, OAFlowSortAttr.FlowSortNo, "流程目录权限", null, "Name", "No");

        map.getAttrsOfOneVSM().AddGroupPanelModel(new OAFrmTrees(), new SysFormTrees(), OAFrmTreeAttr.RefOrgAdminer, OAFrmTreeAttr.FrmTreeNo, "表单目录权限", null, "Name", "No");

        if (bp.web.WebUser.getNo() != null) {
            map.AddHidden("OrgNo", " = ", "@WebUser.OrgNo");
        }

        this.set_enMap(map);
        return this.get_enMap();
    }

    ///#endregion

    @Override
    protected boolean beforeUpdateInsertAction() throws Exception {
        String str = "";
        FlowSorts ens = new FlowSorts();
        ens.RetrieveInSQL("SELECT FlowSortNo FROM Port_OrgAdminerFlowSort WHERE  FK_Emp='" + this.getEmpNo() + "' AND OrgNo='" + this.getOrgNo() + "'");
        for (FlowSort item : ens.ToJavaList()) {
            str += "(" + item.getNo() + ")" + item.getName() + ";";
        }
        this.setFlowSorts(str);

        str = "";
        SysFormTrees enTrees = new SysFormTrees();
        enTrees.RetrieveInSQL("SELECT FrmTreeNo FROM Port_OrgAdminerFrmTree WHERE  FK_Emp='" + this.getEmpNo() + "' AND OrgNo='" + this.getOrgNo() + "'");
        for (SysFormTree item : enTrees.ToJavaList()) {
            str += "(" + item.getNo() + ")" + item.getName() + ";";
        }
        this.setFrmTrees(str);

        if (Objects.equals(this.getEmpName(), "") || this.getEmpName() == null) {
            Emp emp = new Emp(this.getEmpNo());
            this.setEmpName(emp.getName());
            this.setMyPK(this.getOrgNo() + "_" + this.getEmpNo());
        }

        return super.beforeUpdateInsertAction();
    }
}
