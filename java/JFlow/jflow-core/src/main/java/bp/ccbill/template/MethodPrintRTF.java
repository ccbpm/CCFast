package bp.ccbill.template;

import bp.ccfast.ccmenu.PowerCenterAttr;
import bp.da.DBAccess;
import bp.da.DataType;
import bp.en.EntityNoName;
import bp.en.Map;
import bp.en.UAC;
import bp.web.WebUser;

/**
 * 打印RTF
 */
public class MethodPrintRTF extends EntityNoName {

    @Override
    public UAC getHisUAC()
    {
        UAC uac = new UAC();
        if (WebUser.getIsAdmin())
        {
            uac.IsUpdate = true;
            return uac;
        }
        return super.getHisUAC();
    }

    @Override
    public Map getEnMap() {
        if (this.get_enMap() != null)
        {
            return this.get_enMap();
        }

        Map map = new Map("Frm_Method", "连接");

        //主键.
        map.AddTBStringPK(MethodAttr.No, null, "编号", true, true, 0, 50, 10);
        map.AddTBString(MethodAttr.Name, null, "链接标签", true, false, 0, 300, 10);
        map.AddTBString(MethodAttr.MethodID, null, "方法ID", false, true, 0, 300, 10);
        map.AddTBString(MethodAttr.GroupID, null, "分组ID", false, true, 0, 50, 10);

        //功能标记.
        map.AddTBString(MethodAttr.MethodModel, null, "方法模式", false, false, 0, 300, 10);
        map.AddTBString(MethodAttr.Mark, null, "Mark", false, true, 0, 300, 10);
        map.AddTBString(MethodAttr.Icon, null, "图标", true, false, 0, 50, 10, true);
        //是否显示到列表.
        map.AddBoolean(MethodAttr.IsList, false, "是否显示在列表?", true, true);
        map.AddMyFile("rtf模板文件", "*.rtf");
        map.AddTBAtParas();


        this.set_enMap(map);
        return this.get_enMap();
    }

    @Override
    protected boolean beforeInsert() throws Exception
    {
        if (DataType.IsNullOrEmpty(this.getNo()) == true)
        {
            this.setNo(DBAccess.GenerGUID(0, null, null));
        }
        return super.beforeInsert();
    }
}
