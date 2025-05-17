package bp.App.ShuCai;

import bp.en.EntityNoName;
import bp.en.EntityNoNameAttr;
import bp.en.Map;

public class QuXian extends EntityNoName {
    @Override
    public Map getEnMap() {
        if (this.get_enMap() != null)
        {
            return this.get_enMap();
        }
        Map map = new Map("GIS_QuXian", "市县");
        map.setItIsAutoGenerNo(true);
        map.AddTBStringPK(EntityNoNameAttr.No, null, "编号", true, true, 3, 3, 3);
        map.AddTBString(EntityNoNameAttr.Name, null, "名称", true, false, 0, 50, 200);
        this.set_enMap(map);
        return this.get_enMap();
    }
}
