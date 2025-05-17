package bp.App.YNJT;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.difference.handler.CommonFileUtils;
import bp.en.TSEntityMyPK;
import bp.en.TSEntityNoName;
import bp.en.UIContralType;
import bp.sys.*;
import bp.web.WebUser;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

public class Handler_YNJT extends bp.difference.handler.DirectoryPageBase{
    /**
     构造函数
     */
//ORIGINAL LINE: public Handler_AD()
    public Handler_YNJT()
    {
    }

    /**
     汇总报表.

     @return
     */
    public final String YuSuan_MyTaskHZ() throws Exception {
        String myTaskFrmID = "TS.YS.MyTask";
        TSEntityMyPK myTask = new TSEntityMyPK(myTaskFrmID, this.getMyPK());
        String batchNo = myTask.GetValStringByKey("BatchNo");
        String orgNo = myTask.GetValStringByKey("OrgNo");
        int taskState = myTask.GetValIntByKey("TaskState");
        long workID = myTask.GetValIntByKey("WorkID");
        String frmIDOfSB = myTask.GetValStringByKey("FrmID");

        //获取本组织下下一级的申报任务,并且检查是否完整.
        String err = "";

        GEEntityMyPKs tasks = new GEEntityMyPKs(myTaskFrmID);
        tasks.Retrieve("POrgNo", WebUser.getOrgNo(), "BatchNo", batchNo, "FrmID", frmIDOfSB); //找到子级任务.

       /* //检查POrgNo 数据完整性.
        String err = "";
        for (GEEntityMyPK item : tasks.ToJavaList())
        {
            String pOrgNo = item.GetValStringByKey("POrgNo");
            orgNo = item.GetValStringByKey("OrgNo");
            String myPK = item.GetValStringByKey("MyPK");
            if (DataType.IsNullOrEmpty(pOrgNo) == true)
            {
                pOrgNo = DBAccess.RunSQLReturnStringIsNull("SELECT ParentNo FROM YS_Org WHERE No='" + orgNo + "'", null);
                if (DataType.IsNullOrEmpty(pOrgNo) == true)
                {
                    err += "err@没有找到任务ID:[" + item.getMyPK() + "]组织编号[" + orgNo + "]的父节点的OrgNo,导致数据无法汇总.";
                    continue;
                }
                DBAccess.RunSQL("UPDATE YS_Task SET POrgNo='" + pOrgNo + "' WHERE MyPK='" + item.getMyPK() + "'");
            }
        }
        if (DataType.IsNullOrEmpty(err) == false)
        {
            return err;
        }

        //获取本组织下下一级的申报任务,并且检查是否完整.
        tasks = new GEEntityMyPKs(myTaskFrmID);
        tasks.Retrieve("POrgNo", WebUser.getOrgNo(), "BatchNo", batchNo);*/
        if (tasks.size() == 0)
        {
            return "err@没有找到本组织下一级的申报数据:" + err;
        }
        for (GEEntityMyPK item : tasks.ToJavaList())
        {
            taskState = item.GetValIntByKey("TaskState");
            String myPK = item.GetValStringByKey("MyPK");
            if (taskState != 3)
            {
                err += "任务ID:[" + item.getMyPK() + "],申报人[" + item.GetValStringByKey("StarterName") + "],部门:[" + item.GetValStringByKey("DeptName") + "]尚未提交.";
            }
        }
        if (DataType.IsNullOrEmpty(err) == false)
        {
            return err;
        }

        //获得从表.
        String dtlFrmID = DBAccess.RunSQLReturnStringIsNull("SELECT No FROM Sys_MapDtl WHERE FK_MapData='" + frmIDOfSB + "'", null);
        if (dtlFrmID == null)
        {
            return "err@系统错误，没有找到申报的从表ID,表单ID[" + frmIDOfSB + "]";
        }

        MapDtl dtl = new MapDtl(dtlFrmID);
        MapAttrs attrs = new MapAttrs(dtl.getNo());
        MapAttr attrFK = null;
        for (MapAttr item : attrs.ToJavaList())
        {
            if (item.getUIContralType() == UIContralType.DDL && DataType.IsNullOrEmpty(item.getUIBindKey()) == false)
            {
                attrFK = item;
            }
            continue;
            // if (item.KeyOfEn.Equals("Val"))
        }
        if (attrFK == null)
        {
            return "err@没有找到表单:[" + frmIDOfSB + "]申报的从表[" + (dtl == null ? null : dtl.getName()) + "]的外键属性.";
        }

        //获得从表的数据.
        SFTable tb = new SFTable(attrFK.getUIBindKey());
        DataTable dt = tb.GenerHisDataTable();

        //获得从表数据,没有数据就初始化他们.
        GEDtls dtlsDB = new GEDtls(dtl.getNo(), workID);
        if (dtlsDB.size() == 0)
        {
            //插入数据.
            int idx = 0;
            for (DataRow dr : dt.Rows)
            {
                String no = dr.get(0) instanceof String ? (String)dr.get(0) : null;
                String name = dr.get(1) instanceof String ? (String)dr.get(1) : null;
                GEDtl dtlDB = dtlsDB.getNewEntity() instanceof GEDtl ? (GEDtl)dtlsDB.getNewEntity() : null;

                if (dtlDB != null)
                {
                    dtlDB.SetValByKey(attrFK.getKeyOfEn(), no);
                }
                if (dtlDB != null)
                {
                    dtlDB.SetValByKey(attrFK.getKeyOfEn() + "T", name);
                }
                if (dtlDB != null){
                    dtlDB.SetValByKey("Idx", idx);
                }
                if (dtlDB != null){
                    dtlDB.SetValByKey("RefPK", workID);//设置主键.
                }
                if (dtlDB != null)
                {
                    dtlDB.Insert();
                }
                idx++;
            }
            dtlsDB = new GEDtls((dtl == null ? null : dtl.getNo()), workID); //重新查询.
        }

        //开始汇总数据,找到隶属的workIDs
        String workIDs = "";
        for (GEEntityMyPK item : tasks.ToJavaList())
        {
            workIDs += "," + item.GetValIntByKey("WorkID") + "";
        }
        workIDs = workIDs.substring(1);

        String sql = "SELECT SUM(BenQi) as Num FROM " + (dtl == null ? null : dtl.getPTable()) + " WHERE RefPK IN (" + workIDs + ")";

        for (GEDtl item : dtlsDB.ToJavaList())
        {
            String mysql = sql + " AND " + attrFK.getKeyOfEn() + "='" + item.GetValByKey(attrFK.getKeyOfEn()) + "'";
            float num = DBAccess.RunSQLReturnValFloat(mysql);

            DBAccess.RunSQL("UPDATE " + (dtl == null ? null : dtl.getPTable()) + " SET BenQi='" + num + "' WHERE RefPK=" + workID + " AND " + attrFK.getKeyOfEn() + "='" + item.GetValByKey(attrFK.getKeyOfEn()) + "'");
        }

        return "info@汇总成功,一共[" + tasks.size() + "]个组织数据汇总成功，此方法可以反复执行。";
    }
}
