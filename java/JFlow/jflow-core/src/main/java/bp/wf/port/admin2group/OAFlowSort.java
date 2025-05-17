package bp.wf.port.admin2group;

import bp.en.*;
import bp.en.Map;
import bp.wf.template.*;

/**
 * 组织管理员
 */
public class OAFlowSort extends EntityMyPK {

    ///#region 属性
    public final String getEmpNo() throws Exception {
        return this.GetValStringByKey(OAFlowSortAttr.FK_Emp);
    }

    public final void setEmpNo(String value) throws Exception {
        this.SetValByKey(OAFlowSortAttr.FK_Emp, value);
    }

    public final String getOrgNo() throws Exception {
        return this.GetValStringByKey(OAFlowSortAttr.OrgNo);
    }

    public final void setOrgNo(String value) throws Exception {
        this.SetValByKey(OAFlowSortAttr.OrgNo, value);
    }

    ///#endregion


    ///#region 构造方法

    /**
     * 组织管理员
     */
    public OAFlowSort() {
    }

    /**
     * 组织管理员
     */
    @Override
    public Map getEnMap() {
        if (this.get_enMap() != null) {
            return this.get_enMap();
        }
        Map map = new Map("Port_OrgAdminerFlowSort", "组织管理员流程目录权限");
        map.AddMyPK(true);
        map.AddTBString(OAFlowSortAttr.OrgNo, null, "组织", true, false, 0, 100, 20);
        map.AddTBString(OAFlowSortAttr.FK_Emp, null, "管理员", true, false, 0, 100, 20);
        map.AddTBString(OAFlowSortAttr.RefOrgAdminer, null, "组织管理员", true, false, 0, 100, 20);

        //map.AddDDLEntities(OAFlowSortAttr.FK_Emp, null, "管理员", new Emps(), false);
        //map.AddDDLEntities(OAFlowSortAttr.RefOrgAdminer, null, "管理员", new Emps(), false);
        map.AddDDLEntities(OAFlowSortAttr.FlowSortNo, null, "流程目录", new FlowSorts(), false);

        this.set_enMap(map);
        return this.get_enMap();
    }

    ///#endregion

    @Override
    protected boolean beforeInsert() throws Exception {
        String str = this.GetValStringByKey("RefOrgAdminer");

        this.setMyPK(str + "_" + this.GetValStringByKey("FlowSortNo"));
        OrgAdminer oa = new OrgAdminer(str);
        this.setOrgNo(oa.getOrgNo());
        this.setEmpNo(oa.getEmpNo());
        return super.beforeInsert();
    }

    @Override
    protected void afterInsert() throws Exception {
        //插入入后更改OrgAdminer中
        String str = "";
        FlowSorts ens = new FlowSorts();
        ens.RetrieveInSQL("SELECT FlowSortNo FROM Port_OrgAdminerFlowSort WHERE  FK_Emp='" + this.getEmpNo() + "' AND OrgNo='" + this.getOrgNo() + "'");
        for (FlowSort item : ens.ToJavaList()) {
            str += "(" + item.getNo() + ")" + item.getName() + ";";
        }
        OrgAdminer adminer = new OrgAdminer(this.GetValStringByKey("RefOrgAdminer"));
        adminer.SetValByKey("FlowSorts", str);
        adminer.Update();
        super.afterInsert();
    }
}
