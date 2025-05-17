package bp.ccbill.template;

import bp.en.*;
import bp.en.Map;

/**
 * 台账子流程
 */
public class DBRole extends EntityMyPK {

    ///#region 基本属性

    /**
     * 表单ID
     */
    public final String getFrmID() {
        return this.GetValStringByKey(DBRoleAttr.FrmID);
    }

    public final void setFrmID(String value) {
        this.SetValByKey(DBRoleAttr.FrmID, value);
    }

    public final String getDBRole1()
    {
        return this.GetValStringByKey(DBRoleAttr.DBRole);
    }
    public final void setDBRole(String value)
    {
        this.SetValByKey(DBRoleAttr.DBRole, value);
    }
    public final String getMarkID()
    {
        return this.GetValStringByKey(DBRoleAttr.MarkID);
    }
    public final void setMarkID(String value)
    {
        this.SetValByKey(DBRoleAttr.MarkID, value);
    }
    public final String getDocs()
    {
        return this.GetValStringByKey(DBRoleAttr.Docs);
    }
    public final void setDocs(String value)
    {
        this.SetValByKey(DBRoleAttr.Docs, value);
    }

    /**
     * 顺序号
     */
    public final int getIdx() {
        return this.GetValIntByKey(DBRoleAttr.Idx);
    }

    public final void setIdx(int value) {
        this.SetValByKey(DBRoleAttr.Idx, value);
    }

    ///#endregion


    ///#region 构造方法

    /**
     * 台账子流程
     */
    public DBRole() {
    }

    /**
     * 重写基类方法
     */
    @Override
    public Map getEnMap() {
        if (this.get_enMap() != null) {
            return this.get_enMap();
        }

        Map map = new Map("Frm_DBRole", "数据权限");

        map.AddMyPK(true);

        map.AddTBString(DBRoleAttr.FrmID, null, "表单ID", false, false, 0, 300, 10);
        map.AddTBString(DBRoleAttr.DBRole, null, "规则", false, false, 0, 300, 10);
        map.AddTBString(DBRoleAttr.MarkID, null, "权限标记", true, true, 0, 50, 10);
        map.AddTBString(DBRoleAttr.MarkName, null, "权限标记", true, true, 0, 50, 10);
        map.AddTBString(DBRoleAttr.Docs, null, "控制内容", true, true, 0, 50, 10);
        map.AddBoolean(DBRoleAttr.IsEnable, true, "IsEnable", true, true);
        map.AddTBAtParas();

        this.set_enMap(map);
        return this.get_enMap();
    }

    ///#endregion
}
