package bp.wf.BuessUnit;

import bp.sys.BuessUnitBase;
import bp.wf.GenerWorkFlow;

public class BU_FrmBillRefDataSetEditing extends BuessUnitBase {
    @Override
    public String getTitle() {

        return "设置单据编辑状态.";
    }
    /**
     执行的方法
     */
    @Override
    public final String DoIt() throws Exception {
        GenerWorkFlow generWorkFlow = new GenerWorkFlow();
        generWorkFlow.setWorkID (this.WorkID);
        if (generWorkFlow.RetrieveFromDBSources() == 0)
        {
            bp.ccbill.Dev2Interface.MyBill_SetEditing(this.WorkID);
        }
        else
        {
            bp.ccbill.Dev2Interface.MyBill_SetEditing(generWorkFlow.getPWorkID());
        }

        return "单据设置审核模式成功";
    }


}
