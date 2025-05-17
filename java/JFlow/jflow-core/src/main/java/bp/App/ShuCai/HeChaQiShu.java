package bp.App.ShuCai;

import bp.en.EntityNoAttr;
import bp.en.EntityNoName;
import bp.en.EntityNoNameAttr;
import bp.en.Map;

public class HeChaQiShu extends EntityNoName {
    @Override
    public Map getEnMap() {

        if (this.get_enMap() != null)
        {
            return this.get_enMap();
        }
        Map map = new Map("GIS_HeChaQiShu", "核查期数");
        map.setItIsAutoGenerNo(false);
        map.AddTBStringPK(EntityNoAttr.No, null, "编号", true, false, 3, 30, 20);
        map.AddTBString(EntityNoNameAttr.Name, null, "核查期数", true, false, 0, 50, 100);
        this.set_enMap(map);
        return this.get_enMap();
    }
}
