using System;
using System.Data;
using System.Text;
using BP.DA;
using BP.Sys;
using BP.Web;
using BP.Difference;
using BP.En;
using NPOI.SS.Formula.Functions;

namespace BP.App.YNJT
{
    /// <summary>
    /// 页面功能实体
    /// </summary>
    public class Handler_YNJT : WF.HttpHandler.DirectoryPageBase
    {
        /// <summary>
        /// 导入从表的Demo.
        /// </summary>
        /// <returns></returns>
        public string ImpContract()
        {
            var files = HttpContextHelper.RequestFiles();
            string ext = ".xls";
            string fileName = System.IO.Path.GetFileName(files[0].FileName);
            if (fileName.Contains(".xlsx") == true)
                ext = ".xlsx";
            //设置文件名
            string fileNewName = DateTime.Now.ToString("yyyyMMddHHmmssff") + ext;

            //文件存放路径
            string filePath = BP.Difference.SystemConfig.PathOfTemp + fileNewName;
            HttpContextHelper.UploadFile(files[0], filePath);

            //获得excel数据
            DataTable dt = BP.DA.DBLoad.ReadExcelFileToDataTable(filePath);

            //获得参数.
            string frmID = this.FrmID;
            Int64 workID = this.GetRequestValInt64("WorkID");

            //1. 首先检查数据是否完整.
            string msg = "";
            foreach (DataRow dr in dt.Rows)
            {
                string? id = dr["物料编号"].ToString();

                //首先判断物料编号是否存在.
                string sql = "SELECT * FROM XX WHERE XXX='" + id + "'";
                DataTable Mydt = DBAccess.RunSQLReturnTable(sql);
                if (Mydt.Rows.Count == 0)
                    msg += " 物料编号错误." + id;
            }
            if (msg.Length > 5)
                return "err@数据不完整请检查：" + msg;

            //2。开始插入数据.
            foreach (DataRow dr in dt.Rows)
            {
                string? id = dr["物料编号"].ToString();
                string? name = dr["物料名称"].ToString();

                //入库.
                string sql = "INSERT INTO XXXX (XXX,XXX,RefPKVal)VALUES('','','" + workID + "')";
                DBAccess.RunSQL(sql);
            }
            return msg;
        }
        /// <summary>
        /// 构造函数
        /// </summary>
        public Handler_YNJT()
        {

        }

        /// <summary>
        /// 汇总报表.
        /// </summary>
        /// <returns></returns>
        public string YuSuan_MyTaskHZ()
        {
            string myTaskFrmID = "TS.YS.MyTask";
            TSEntityMyPK myTask = new TSEntityMyPK(myTaskFrmID, MyPK);
            string batchNo = myTask.GetValStringByKey("BatchNo");
            string orgNo = myTask.GetValStringByKey("OrgNo");
            int taskState = myTask.GetValIntByKey("TaskState");
            long workID = myTask.GetValIntByKey("WorkID");
            string frmIDOfSB = myTask.GetValStringByKey("FrmID");

            //获取本组织下下一级的申报任务,并且检查是否完整.
            string err = "";
            GEEntityMyPKs tasks = new GEEntityMyPKs(myTaskFrmID);
            tasks.Retrieve("POrgNo", WebUser.OrgNo, "BatchNo", batchNo, "FrmID", frmIDOfSB); //找到子级任务.
            if (tasks.Count == 0)
                return "err@没有找到本组织下一级的申报数据:" + err;
            foreach (GEEntityMyPK item in tasks)
            {
                taskState = item.GetValIntByKey("TaskState");
                string myPK = item.GetValStringByKey("MyPK");
                if (taskState != 3)
                    err += "任务ID:[" + item.MyPK + "],申报人[" + item.GetValStringByKey("StarterName") + "],部门:[" + item.GetValStringByKey("DeptName") + "]尚未提交.";
            }

            //如果子级有未申报的数据，就抛出异常.
            if (DataType.IsNullOrEmpty(err) == false && 1 == 2)
                return err;

            //获得从表.
            string dtlFrmID = DBAccess.RunSQLReturnStringIsNull("SELECT No FROM Sys_MapDtl WHERE FK_MapData='" + frmIDOfSB + "'", null);
            if (dtlFrmID == null)
                return "err@系统错误，没有找到申报的从表ID,表单ID[" + frmIDOfSB + "]";

            MapDtl dtl = new MapDtl(dtlFrmID);
            MapAttrs attrs = new MapAttrs(dtl.No);
            MapAttr attrFK = null;
            foreach (MapAttr item in attrs)
            {
                if (item.UIContralType == UIContralType.DDL && DataType.IsNullOrEmpty(item.UIBindKey) == false)
                    attrFK = item; continue;
                // if (item.KeyOfEn.Equals("Val"))
            }
            if (attrFK == null)
                return "err@没有找到表单:[" + frmIDOfSB + "]申报的从表[" + dtl?.Name + "]的外键属性.";

            //获得从表的数据.
            SFTable tb = new SFTable(attrFK.UIBindKey);
            DataTable dt = tb.GenerHisDataTable();

            //获得从表数据,没有数据就初始化他们.
            GEDtls dtlsDB = new GEDtls(dtl.No, workID);
            if (dtlsDB.Count == 0)
            {
                //插入数据.
                int idx = 0;
                foreach (DataRow dr in dt.Rows)
                {
                    string? no = dr[0] as string;
                    string? name = dr[1] as string;
                    GEDtl? dtlDB = dtlsDB.GetNewEntity as GEDtl;

                    dtlDB?.SetValByKey(attrFK.KeyOfEn, no);
                    dtlDB?.SetValByKey(attrFK.KeyOfEn + "T", name);
                    dtlDB?.SetValByKey("Idx", idx);
                    dtlDB?.SetValByKey("RefPK", workID); //设置主键.
                    dtlDB?.Insert();
                    idx++;
                }
                dtlsDB = new GEDtls(dtl?.No, workID); //重新查询.
            }

            //开始汇总数据,找到隶属的workIDs
            string workIDs = "";
            foreach (GEEntityMyPK item in tasks)
            {
                workIDs += "," + item.GetValIntByKey("WorkID") + "";
            }
            workIDs = workIDs.Substring(1);

            string sql = "SELECT SUM(BenQi) as Num FROM " + dtl?.PTable + " WHERE RefPK IN (" + workIDs + ")";

            foreach (GEDtl item in dtlsDB)
            {
                string mysql = sql + " AND " + attrFK.KeyOfEn + "='" + item.GetValByKey(attrFK.KeyOfEn) + "'";
                var num = DBAccess.RunSQLReturnValFloat(mysql);

                DBAccess.RunSQL("UPDATE " + dtl?.PTable + " SET BenQi='" + num + "' WHERE RefPK=" + workID + " AND " + attrFK.KeyOfEn + "='" + item.GetValByKey(attrFK.KeyOfEn) + "'");
            }

            return "info@汇总成功,一共[" + tasks.Count + "]个组织数据汇总成功，此方法可以反复执行。";
        }


    }


}
