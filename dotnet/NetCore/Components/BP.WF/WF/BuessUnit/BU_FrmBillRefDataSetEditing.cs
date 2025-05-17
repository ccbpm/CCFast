using System;
using System.Collections;
using System.Data;
using BP.DA;
using BP.En;
using BP.Web;
using BP.Sys;
using BP.CCBill;

namespace BP.WF.BuessUnit
{
    /// <summary>
    /// 业务单元基类
    /// 1. 重写该类为业务单元子类.
    /// 2. 每个业务单元子类可以在流程事件节点时间设置.
    /// 3. 被继承的子类的必须在BP.*.DLL 里面,才能确保设置时候被映射到.
    /// 4. 子类在DoIt方法中根据WorkID 的书写业务逻辑.
    /// </summary>
    public class BU_FrmBillRefDataSetEditing : BuessUnitBase
    {
        /// <summary>
        /// 标题
        /// </summary>
        public override string Title
        {
            get
            {
                return "设置单据编辑状态.";
            }
        }
        /// <summary>
        /// 执行的方法
        /// </summary>
        public override string DoIt()
        {
            GenerWorkFlow generWorkFlow = new GenerWorkFlow();
            generWorkFlow.WorkID = this.WorkID;
            if (generWorkFlow.RetrieveFromDBSources() == 0)
                BP.CCBill.Dev2Interface.MyBill_SetEditing(this.WorkID);
            else
                BP.CCBill.Dev2Interface.MyBill_SetEditing(generWorkFlow.PWorkID);

            return "单据设置审核模式成功";
        }
    }
}
