using BP.DA;
using System;
using System.Collections.Generic;
using System.Data;
using System.Text;

namespace BP.WF
{
    /// <summary>
    /// 审核工作节点
    /// </summary>
    public class WorkCheck
    {
        /// <summary>
        /// 工作ID
        /// </summary>
        public Int64 WorkID = 0;
        public Int64 FID = 0;
        /// <summary>
        /// 节点ID
        /// </summary>
        public int NodeID = 0;
        public int FWCTimeModel = 0;
        /// <summary>
        /// 流程编号
        /// </summary>
        public string FlowNo = null;
        public WorkCheck(string flowNo, int nodeID, Int64 workid, Int64 fid, int fwcTimeModel)
        {
            this.FlowNo = flowNo;
            this.NodeID = nodeID;
            this.WorkID = workid;
            this.FID = fid;
            this.FWCTimeModel = fwcTimeModel;
        }
        /// <summary>
        /// 获取主键32位
        /// </summary>
        /// <returns></returns>
        public int GetMyPK32()
        {
            try
            {
                int newPK = int.Parse(this.WorkID.ToString()) + this.NodeID + int.Parse(this.FlowNo);
                string myPk = "";
                string sql = "SELECT TOP 1 RDT FROM WF_GenerWorkerlist WHERE WorkID={0} AND FK_Node={1} AND FK_Flow='{2}' ORDER BY RDT DESC";
                DataTable dt = DBAccess.RunSQLReturnTable(string.Format(sql, this.WorkID, this.NodeID, this.FlowNo));
                if (dt != null && dt.Rows.Count > 0)
                {
                    myPk = dt.Rows[0]["RDT"].ToString();
                    myPk = myPk.Replace("-", "").Replace(":", "").Replace(" ", "");
                    myPk = myPk.Substring(4);
                    newPK = int.Parse(this.WorkID.ToString()) + this.NodeID + int.Parse(this.FlowNo) + int.Parse(myPk);
                }
                return newPK;
            }
            catch (Exception ex)
            {
                return 0;
            }
        }
        /// <summary>
        /// 获取主键
        /// </summary>
        /// <returns></returns>
        public Int64 GetMyPK()
        {
            try
            {
                Int64 newPK = Int64.Parse(this.WorkID.ToString()) + this.NodeID + Int64.Parse(this.FlowNo);
                string myPk = "";
                string sql = "SELECT TOP 1 RDT FROM WF_GenerWorkerlist WHERE WorkID={0} AND FK_Node={1} AND FK_Flow='{2}' ORDER BY RDT DESC";


                DataTable dt = DBAccess.RunSQLReturnTable(string.Format(sql, this.WorkID, this.NodeID, this.FlowNo));
                if (dt != null && dt.Rows.Count > 0)
                {
                    myPk = dt.Rows[0]["RDT"].ToString();
                    myPk = myPk.Replace("-", "").Replace(":", "").Replace(" ", "");
                    myPk = myPk.Substring(2);
                    newPK = Int64.Parse(this.WorkID.ToString()) + this.NodeID + Int64.Parse(this.FlowNo) + Int64.Parse(myPk);
                }
                return newPK;
            }
            catch (Exception ex)
            {
                return 0;
            }
        }
        public Tracks HisWorkChecks
        {
            get
            {
                if (_HisWorkChecks == null)
                {
                    _HisWorkChecks = new Tracks();
                    BP.En.QueryObject qo = new En.QueryObject(_HisWorkChecks);

                    if (this.FID != 0)
                    {
                        qo.AddWhere(TrackAttr.WorkID, this.FID);
                        qo.addOr();
                        //qo.AddWhere(TrackAttr.WorkID, this.WorkID);
                        qo.addSQL(TrackAttr.WorkID + "=" + this.WorkID);
                    }
                    else
                    {
                        qo.AddWhere(TrackAttr.WorkID, this.WorkID);

                        if (this.WorkID != 0)
                        {
                            qo.addOr();
                            qo.AddWhere(TrackAttr.FID, this.WorkID);
                        }
                    }
                    // 如果存在旧数据，还是按照时间排序，否则按照主键排序
                    String tableName = "ND" + int.Parse(this.FlowNo) + "Track";
                    String strPK = DBAccess.RunSQLReturnString("SELECT MIN(MyPK) FROM " + tableName + " WHERE WorkID = " + this.WorkID);
                    if (this.FWCTimeModel == 0)
                        if (DataType.IsNullOrEmpty(strPK) ==false && strPK.Length < 18) qo.addOrderByDesc(TrackAttr.RDT);
                        else qo.addOrderByDesc(TrackAttr.MyPK);
                    if (this.FWCTimeModel == 1)
                        if (DataType.IsNullOrEmpty(strPK) == false && strPK.Length < 18) qo.addOrderBy(TrackAttr.RDT);
                        else qo.addOrderBy(TrackAttr.MyPK);

                    string sql = qo.SQL;
                    sql = sql.Replace("WF_Track", tableName);
                    DataTable dt = null;

                    //修复track 表.
                    try
                    {
                        dt = DBAccess.RunSQLReturnTable(sql, qo.MyParas);
                    }
                    catch (Exception ex)
                    {
                        Track.CreateOrRepairTrackTable(this.FlowNo);
                        dt = DBAccess.RunSQLReturnTable(sql, qo.MyParas);
                    }

                    //dt.DefaultView.Sort = "RDT desc";

                    //放入到track里面.
                    BP.En.QueryObject.InitEntitiesByDataTable(_HisWorkChecks, dt, null);
                }
                return _HisWorkChecks;
            }
        }
        private Tracks _HisWorkChecks = null;
    }
}
