package bp.App.YNJT;

import bp.ccbill.GenerBill;
import bp.da.DBAccess;
import bp.da.DataTable;
import bp.sys.BuessUnitBase;
import bp.sys.EventListFrm;

public class BuessUnitFrmBill extends BuessUnitBase {
    @Override
    public String getTitle() {
        return "(云南交投)单据启动审核时、审核完毕之后就自动设置为任务状态.";
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
    public String DoIt() throws Exception {
        //单据审核回滚: 检查上级是否汇总如果汇总了，就不能回滚.
        if (EventListFrm.Reback.equals(this.EventID) == true)
        {

            String sql = "UPDATE YS_Task SET TaskState=2 WHERE WorkID=" + WorkID; //设置状态为审核中.
            DBAccess.RunSQL(sql);

            GenerBill gb = new GenerBill(this.WorkID);

            sql = "SELECT * FROM YS_Task WHERE WorkID=" + this.WorkID;
            DataTable dt = DBAccess.RunSQLReturnTable(sql);
            //ORIGINAL LINE: string? pOrg = dt.Rows[0]["POrgNo"].ToString();
            String pOrg = dt.Rows.get(0).get("POrgNo").toString();
            //ORIGINAL LINE: string? batchNo = dt.Rows[0]["BatchNo"].ToString();
            String batchNo = dt.Rows.get(0).get("BatchNo").toString();

            //找他的父级数据,检查状态是否是未申报的状态，如果不是就抛出异常.
            sql = "SELECT TaskState FROM YS_Task WHERE BatchNo='" + batchNo + "' AND FrmID='" + gb.getFrmID() + "' AND OrgNo='" + pOrg + "'";
            int sta = DBAccess.RunSQLReturnValInt(sql);
            if (sta == 3)
            {
                return "err@该任务的上级已经汇总了，不能回滚重新填报了.";
            }

            return "info@上级没有汇总，可以回滚。";
            //  string? pOrg = dt.Rows[0]["POrgNo"].ToString();
        }
        if (EventListFrm.CheckOver.equals(this.EventID) == true)
        {
            String sql = "UPDATE YS_Task SET TaskState=3 WHERE WorkID=" + this.WorkID;//设置审核完毕.
            int val = DBAccess.RunSQL(sql);
            if (val == 0)
            {
                return "err@系统错误，提交给上级汇总失败。";
            }

            sql = "SELECT * FROM YS_Task  WHERE WorkID=" + this.WorkID;
            DataTable dt = DBAccess.RunSQLReturnTable(sql);
            if (dt.Rows.size() == 0)
            {
                return "err@没有找到任务,WorkID:" + this.WorkID;
            }

            //ORIGINAL LINE: string? batchNo = dt.Rows[0]["BatchNo"].ToString();
            String batchNo = dt.Rows.get(0).get("BatchNo").toString();
            //ORIGINAL LINE: string? orgName = dt.Rows[0]["OrgName"].ToString();
            String orgName = dt.Rows.get(0).get("OrgName").toString();

            bp.ccbill.Dev2Interface.WriteTrack("YS_Batch", batchNo, "Submit", "组织:" + orgName + "已经提交.");
            return "表单申报已经完成,提交给上级汇总.";
        }

        //设置启动审核.
        if (EventListFrm.CheckStart.equals(this.EventID) == true)
        {
            String sql = "UPDATE YS_Task SET TaskState=2 WHERE WorkID=" + this.WorkID;//设置状态为审核中.
            int val = DBAccess.RunSQL(sql);
            if (val == 0)
            {
                return "err@系统错误，提交给上级汇总失败。";
            }

            sql = "SELECT * FROM YS_Task WHERE WorkID=" + this.WorkID;
            DataTable dt = DBAccess.RunSQLReturnTable(sql);

            //ORIGINAL LINE: string? myPK = dt.Rows[0]["MyPK"].ToString();
            String myPK = dt.Rows.get(0).get("MyPK").toString();
            bp.ccbill.Dev2Interface.WriteTrack("YS_Task", myPK, this.EventID, "启动审核.");
            return "启动审核成功.";
        }

        return "没有判断的执行标记:" + this.EventID + "," + this.HisEn.getClassID() + ",WorkID:" + this.WorkID;
        // return "测试成功WorkID:" + this.WorkID + "&FrmID=" + this.HisEn.ClassID;
    }
}
