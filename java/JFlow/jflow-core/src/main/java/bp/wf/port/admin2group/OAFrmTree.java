package bp.wf.port.admin2group;

import bp.en.*;
import bp.en.Map;
import bp.wf.template.*;

/**
 * 组织管理员-
 */
public class OAFrmTree extends EntityMyPK {

    ///#region 属性
    public final String getEmpNo() throws Exception {
        return this.GetValStringByKey(OAFrmTreeAttr.FK_Emp);
    }

    public final void setEmpNo(String value) throws Exception {
        this.SetValByKey(OAFrmTreeAttr.FK_Emp, value);
    }

    public final String getOrgNo() throws Exception {
        return this.GetValStringByKey(OAFrmTreeAttr.OrgNo);
    }

    public final void setOrgNo(String value) throws Exception {
        this.SetValByKey(OAFrmTreeAttr.OrgNo, value);
    }

    ///#endregion


    ///#region 构造方法

    /**
     * 组织管理员
     */
    public OAFrmTree() {
    }

    /**
     * 组织管理员
     */
    @Override
    public Map getEnMap() {
        if (this.get_enMap() != null) {
            return this.get_enMap();
        }
        Map map = new Map("Port_OrgAdminerFrmTree", "表单目录权限");
        map.AddMyPK(true);
        map.AddTBString(OAFrmTreeAttr.OrgNo, null, "组织", true, false, 0, 100, 20);
        map.AddTBString(OAFrmTreeAttr.FK_Emp, null, "管理员", true, false, 0, 100, 20);
        map.AddTBString(OAFrmTreeAttr.RefOrgAdminer, null, "组织管理员", true, false, 0, 100, 20);

        //map.AddDDLEntities(OAFrmTreeAttr.FK_Emp, null, "管理员", new Emps(), false);
        //map.AddDDLEntities(OAFrmTreeAttr.RefOrgAdminer, null, "管理员", new Emps(), false);
        map.AddDDLEntities(OAFrmTreeAttr.FrmTreeNo, null, "表单目录", new SysFormTrees(), false);

        this.set_enMap(map);
        return this.get_enMap();
    }

    ///#endregion

    @Override
    protected boolean beforeInsert() throws Exception {
        String str = this.GetValStringByKey("RefOrgAdminer");

        this.setMyPK(this.GetValStringByKey("RefOrgAdminer") + "_" + this.GetValStringByKey("FrmTreeNo"));

        OrgAdminer oa = new OrgAdminer(str);

        this.setOrgNo(oa.getOrgNo());
        this.setEmpNo(oa.getEmpNo());

        return super.beforeInsert();
    }

    @Override
    protected void afterInsert() throws Exception {
        //插入入后更改OrgAdminer中
        String str = "";
        SysFormTrees enTrees = new SysFormTrees();
        enTrees.RetrieveInSQL("SELECT FrmTreeNo FROM Port_OrgAdminerFrmTree WHERE  FK_Emp='" + this.getEmpNo() + "' AND OrgNo='" + this.getOrgNo() + "'");
        for (SysFormTree item : enTrees.ToJavaList()) {
            str += "(" + item.getNo() + ")" + item.getName() + ";";
        }
        OrgAdminer adminer = new OrgAdminer(this.GetValStringByKey("RefOrgAdminer"));
        adminer.SetValByKey("FrmTrees", str);
        adminer.Update();
        super.afterInsert();
    }
}
