package bp.wf.BuessUnit;

import bp.ccbill.GenerBill;
import bp.da.DBAccess;
import bp.da.DataTable;
import bp.sys.BuessUnitBase;
import bp.sys.EventListFrm;
import bp.wf.GenerWorkFlow;

public class BU_FrmBillRefDataSetChecking extends BuessUnitBase {
    /// <summary>
    /// 标题 - 也适合单据发起流程引用数据模式
    /// </summary>
    @Override
    public String getTitle() {

        return "设置单据审核状态";
    }
    /// <summary>
    /// 业务单元基类
    /// 1. 重写该类为业务单元子类.
    /// 2. 每个业务单元子类可以在流程事件节点时间设置.
    /// 3. 被继承的子类的必须在BP.*.DLL 里面,才能确保设置时候被映射到.
    /// 4. 子类在DoIt方法中根据WorkID 的书写业务逻辑.
    /// </summary>
    /**
     执行的方法
     */
    @Override
    public final String DoIt() throws Exception {
        GenerWorkFlow generWorkFlow = new GenerWorkFlow();
        generWorkFlow.setWorkID(this.WorkID);
        if (generWorkFlow.RetrieveFromDBSources() == 0)
        {
            bp.ccbill.Dev2Interface.MyBill_SetChecking(this.WorkID);
        }
        else
        {
            bp.ccbill.Dev2Interface.MyBill_SetChecking(generWorkFlow.getPWorkID());
        }

        return "单据设置审核模式成功";
    }

}
