using System;
using System.Collections;
using System.Data;
using System.Text;
using System.Web;
using BP.DA;
using BP.Sys;
using BP.Web;
using BP.En;
using BP.WF;
using BP.WF.HttpHandler;
using BP.Difference;
using BP.CCBill.Template;
using LitJson;
using BP.WF.Template.Frm;
using System.Text.RegularExpressions;
using NPOI.SS.Formula.Functions;
using Spire.Doc;
using FieldType = BP.En.FieldType;

namespace BP.CCBill
{
    /// <summary>
    /// 页面功能实体
    /// </summary>
    public class WF_CCBill : DirectoryPageBase
    {
        #region 构造方法.
        /// <summary>
        /// 方法ID
        /// </summary>
        public string MethodID
        {
            get
            {
                return this.GetRequestVal("MethodID");
            }
        }
        /// <summary>
        /// 方法编号
        /// </summary>
        public string MethodNo
        {
            get
            {
                return this.GetRequestVal("MethodNo");
            }
        }
        /// <summary>
        /// 构造函数
        /// </summary>
        public WF_CCBill()
        {
        }
        #endregion 构造方法.

        public string GL_DictRefBill()
        {
            string billID = this.GetRequestVal("Tag1");
            string dictFrmID = this.FrmID;//  this.GetRequestVal("DictFrmID");
            string refDictNo = this.GetRequestVal("RefDictNo");
            string refDictName = this.GetRequestVal("RefDictName");


            //获得她的值.
            MapData md = new MapData(dictFrmID);
            string dictEnNo = DBAccess.RunSQLReturnString("SELECT BillNo FROM " + md.PTable + " WHERE OID=" + this.WorkID);

            GEEntitys dtls = new GEEntitys(billID);
            QueryObject qo = new QueryObject(dtls);
            qo.AddWhere(refDictNo, dictEnNo);
            qo.addAnd();
            qo.AddWhere("BillState", ">", 0);
            qo.DoQuery();


            //生成数据.
            DataSet ds = new DataSet();
            ds.Tables.Add(dtls.ToDataTableField());

            //构造显示的列.
            MapAttrs attrs = new MapAttrs();
            attrs.Retrieve("FK_MapData", billID, "GroupID,Idx");
            DataTable dtCol = new DataTable();
            dtCol.Columns.Add("Key");
            dtCol.Columns.Add("Name");
            dtCol.Columns.Add("IsShow");
            foreach (MapAttr item in attrs)
            {
                if (item.UIVisible == false)
                    continue;
                if (item.KeyOfEn.Equals(refDictNo) == true)
                    continue;
                if (item.KeyOfEn.Equals(refDictName) == true)
                    continue;


                switch (item.KeyOfEn)
                {
                    case "FID":
                    case "OrgNo":
                    case "OID":
                    case "Rec":
                    case "AtPara":
                    case "Name":
                        continue;
                    default:
                        break;
                }

                DataRow dr = dtCol.NewRow();
                dr[0] = item.KeyOfEn;
                dr[1] = item.Name;

                if (item.UIVisible)
                    dr[2] = 1;
                else
                    dr[2] = 0;

                dtCol.Rows.Add(dr);
            }
            ds.Tables.Add(dtCol);
            return BP.Tools.Json.ToJson(ds);
        }

        /// <summary>
        /// 维度转换.
        /// </summary>
        /// <returns></returns>
        public string D2_Init()
        {
            //把json转化datatable.
            string json = this.GetRequestVal("json");
            DataTable mydt = BP.Tools.Json.ToDataTable(json);

            string clo1 = mydt.Columns[0].ColumnName;
            string clo2 = mydt.Columns[2].ColumnName;
            //移除
            mydt.Columns.Remove(clo1);
            mydt.Columns.Remove(clo2);

            //转换2D模式.
            DataTable dt2d = BP.Tools.PubGlo.DataTable2D(mydt);
            return BP.Tools.Json.ToJson(dt2d);
        }

        #region 单据处理.
        public string MyBill_CreateCheckFlowNo()
        {
            FrmBill md = new FrmBill(this.FrmID);
            if (md.BillCheckModel.Equals("ByFlowNo") == false)
                md.SetValByKey("BillCheckModel", "ByFlowNo");

            //检查流程编号.
            string flowNo = md.BillCheckTag;
            if (DataType.IsNullOrEmpty(flowNo) == true)
            {
                //   BP.WF.CCFlowAPI.cre
                flowNo = "";
            }
            return flowNo;
        }
        /// <summary>
        /// 创建空白的WorkID.
        /// </summary>
        /// <returns></returns>
        public string MyBill_CreateBlankBillID()
        {
            #region 检查一下单据发起的模式.
            FrmBill md = new FrmBill(this.FrmID);
            if (md.BillCheckModel.Equals("ByFlowNo") == true)
            {

                string flowNo = this.FlowNo;
                if (DataType.IsNullOrEmpty(flowNo) == true)
                    flowNo = md.BillCheckTag;

                Int64 workID = BP.WF.Dev2Interface.Node_CreateBlankWork(flowNo);

                GenerWorkFlow gwf = new GenerWorkFlow(workID);

                GEEntityOID geBill = new GEEntityOID(this.FrmID);
                geBill.OID = workID;
                if (geBill.RetrieveFromDBSources() == 0)
                {
                    geBill.SetValByKey("Title", gwf.Title);
                    geBill.SetValByKey("BillNo", gwf.BillNo);
                    geBill.SetValByKey("BillState", (int)BillState.Checking);

                    geBill.SetValByKey("DeptNo", WebUser.DeptName);
                    geBill.SetValByKey("Starter", WebUser.No);
                    geBill.SetValByKey("StarterName", WebUser.Name);
                    geBill.Insert();
                }
                else
                {
                    geBill.SetValByKey("Title", gwf.Title);
                    geBill.SetValByKey("BillNo", gwf.BillNo);
                    geBill.SetValByKey("BillState", (int)BillState.Checking);

                    geBill.SetValByKey("DeptNo", WebUser.DeptName);
                    geBill.SetValByKey("Starter", WebUser.No);
                    geBill.SetValByKey("StarterName", WebUser.Name);
                    geBill.Update();
                }
                //设置参数.
                BP.WF.Dev2Interface.Flow_SetFlowParas(flowNo, workID, "@BillFrmID=" + this.FrmID);
                return workID + "@" + gwf.FlowNo;
            }
            #endregion 检查一下单据发起的模式.

            string PFrmID = this.GetRequestVal("PFrmID");
            string pWorkID = this.GetRequestVal("PWorkID");
            if (DataType.IsNullOrEmpty(pWorkID) == true)
                pWorkID = "0";
            Int64 billOID = 0;
            try
            {
                billOID = BP.CCBill.Dev2Interface.CreateBlankBillID(this.FrmID, BP.Web.WebUser.No, null, PFrmID, long.Parse(pWorkID));
            }
            catch
            {
                billOID = BP.CCBill.Dev2Interface.CreateBlankBillID(this.FrmID, BP.Web.WebUser.No, null, PFrmID, long.Parse(pWorkID));
            }
            return billOID.ToString();
        }
        /// <summary>
        /// 发起的单据
        /// </summary>
        /// <returns></returns>
        public string DB_StartDicts()
        {
            return BP.Tools.Json.ToJson(BP.CCBill.Dev2Interface.DB_StartDicts());
        }

        public string MyDict_ToolBarInit_EntityNoName()
        {

            //  string billState = en.GetValStrByKey("EntityState");
            FrmDict fm = new FrmDict(this.FrmID);

            string sql = "";
            sql = "SELECT EntityState,RecNo FROM " + fm.PTable + " WHERE No='" + this.No + "'";
            DataTable dt = DBAccess.RunSQLReturnTable(sql);
            if (dt.Rows.Count == 0)
                throw new Exception("数据已经不存在:" + sql);

            //返回json. 
            Hashtable ht = new Hashtable();
            ht.Add("EntityState", dt.Rows[0][0].ToString());
            ht.Add("RecNo", dt.Rows[0][1].ToString());

            int dictState = int.Parse(dt.Rows[0][0].ToString());

            int NewEnable = 0;
            int SaveEnable = 0;
            int DeleteEnable = 0;
            int FilingDoneEnable = 0;
            int FilingUnEnable = 0;

            //空白状态.
            if (dictState == 0)
            {
                NewEnable = 1; //新建.
                SaveEnable = 1; //保存.
                DeleteEnable = 1; //删除.
                FilingDoneEnable = 0; //归档.
                FilingUnEnable = 0; //撤销归档.
            }

            //草稿.
            if (dictState == 1)
            {
                NewEnable = 1; //新建.
                SaveEnable = 1; //保存.
                DeleteEnable = 1; //删除.
                FilingDoneEnable = 0; //归档.
                FilingUnEnable = 0; //撤销归档.
            }

            //编辑中.
            if (dictState == 2)
            {
                NewEnable = 1; //新建.
                SaveEnable = 1; //保存.
                DeleteEnable = 1; //删除.
                FilingDoneEnable = 1; //归档.
                FilingUnEnable = 0; //撤销归档.
            }

            //归档.
            if (dictState == 3)
            {
                NewEnable = 1; //新建.
                SaveEnable = 0; //保存.
                DeleteEnable = 0; //删除.
                FilingDoneEnable = 0; //归档.
                FilingUnEnable = 1; //撤销归档.
            }

            #region 整理参数
            DBRoles rols = new DBRoles();
            rols.Retrieve("FrmID", this.FrmID);
            string mydepts = "" + WebUser.DeptNo + ","; //我的部门.
            string mystas = ""; //我的角色.
            DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.UserID + "'");
            foreach (DataRow dr in mydeptsDT.Rows)
            {
                mydepts += dr[0].ToString() + ",";
                mystas += dr[1].ToString() + ",";
            }
            #endregion 整理参数


            if (NewEnable == 1 && CheckRoles(rols, "RecNew", mydepts, mystas) == false)
                NewEnable = 0;

            if (SaveEnable == 1 && CheckRoles(rols, "RecSave", mydepts, mystas) == false)
                SaveEnable = 0;

            if (DeleteEnable == 1 && CheckRoles(rols, "RecDelete", mydepts, mystas) == false)
                DeleteEnable = 0;

            if (FilingDoneEnable == 1 && CheckRoles(rols, "RecFiling", mydepts, mystas) == false)
                FilingDoneEnable = 0;

            if (FilingUnEnable == 1 && CheckRoles(rols, "RecFiling", mydepts, mystas) == false)
                FilingUnEnable = 0;

            ht.Add("NewEnable", NewEnable.ToString());
            ht.Add("SaveEnable", SaveEnable.ToString());
            ht.Add("DeleteEnable", DeleteEnable.ToString());
            ht.Add("FilingDoneEnable", FilingDoneEnable.ToString());
            ht.Add("FilingUnEnable", FilingUnEnable.ToString());
            return BP.Tools.Json.ToJson(ht);
        }
        /// <summary>
        /// 返回单据的状态
        /// </summary>
        /// <returns></returns>
        public string MyDict_ToolBarInit()
        {
            MapData md = new MapData(this.FrmID);
            if (md.EntityType == EntityType.FrmEntityNoName)
                return MyDict_ToolBarInit_EntityNoName();

            var en = new GEEntity(this.FrmID, this.WorkID);
            string billState = en.GetValStrByKey("BillState");


            FrmDict fm = new FrmDict(this.FrmID);

            string sql = "";

            string wfstate = "-1";

            sql = "SELECT -1 as WFState,BillState,Starter FROM " + fm.PTable + " WHERE OID=" + this.WorkID;
            if (DBAccess.IsExitsTableCol(en.EnMap.PhysicsTable, "WFState") == true)
                sql = "SELECT WFState,BillState,Starter FROM " + fm.PTable + " WHERE OID=" + this.WorkID;

            DataTable dt = DBAccess.RunSQLReturnTable(sql);
            //返回json. 
            Hashtable ht = new Hashtable();

            ht.Add("WFState", dt.Rows[0][0].ToString());
            ht.Add("BillState", dt.Rows[0][1].ToString());
            ht.Add("Starter", dt.Rows[0][2].ToString());

            int dictState = int.Parse(dt.Rows[0][1].ToString());
            string wfStateStr = dt.Rows[0][0].ToString();
            if (DataType.IsNullOrEmpty(wfStateStr))
                wfStateStr = "-1";
            int wfState = int.Parse(wfStateStr);

            if (dictState == 100) dictState = 3;
            if (wfState == 3) dictState = 3;

            int NewEnable = 0;
            int SaveEnable = 0;
            int DeleteEnable = 0;
            int FilingDoneEnable = 0;
            int FilingUnEnable = 0;

            //空白状态.
            if (dictState == 0)
            {
                NewEnable = 1; //新建.
                SaveEnable = 1; //保存.
                DeleteEnable = 1; //删除.
                FilingDoneEnable = 0; //归档.
                FilingUnEnable = 0; //撤销归档.
            }

            //草稿.
            if (dictState == 1)
            {
                NewEnable = 1; //新建.
                SaveEnable = 1; //保存.
                DeleteEnable = 1; //删除.
                FilingDoneEnable = 0; //归档.
                FilingUnEnable = 0; //撤销归档.
            }

            //编辑中.
            if (dictState == 2)
            {
                NewEnable = 1; //新建.
                SaveEnable = 1; //保存.
                DeleteEnable = 1; //删除.
                FilingDoneEnable = 1; //归档.
                FilingUnEnable = 0; //撤销归档.
            }

            //归档.
            if (dictState == 3)
            {
                NewEnable = 1; //新建.
                SaveEnable = 0; //保存.
                DeleteEnable = 0; //删除.
                FilingDoneEnable = 0; //归档.
                FilingUnEnable = 1; //撤销归档.
            }

            #region 整理参数
            DBRoles rols = new DBRoles();
            rols.Retrieve("FrmID", this.FrmID);
            string mydepts = "" + WebUser.DeptNo + ","; //我的部门.
            string mystas = ""; //我的角色.
            DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.UserID + "'");
            foreach (DataRow dr in mydeptsDT.Rows)
            {
                mydepts += dr[0].ToString() + ",";
                mystas += dr[1].ToString() + ",";
            }
            #endregion 整理参数


            if (NewEnable == 1 && CheckRoles(rols, "RecNew", mydepts, mystas) == false)
                NewEnable = 0;

            if (SaveEnable == 1 && CheckRoles(rols, "RecSave", mydepts, mystas) == false)
                SaveEnable = 0;

            if (DeleteEnable == 1 && CheckRoles(rols, "RecDelete", mydepts, mystas) == false)
                DeleteEnable = 0;

            if (FilingDoneEnable == 1 && CheckRoles(rols, "RecFiling", mydepts, mystas) == false)
                FilingDoneEnable = 0;

            if (FilingUnEnable == 1 && CheckRoles(rols, "RecFiling", mydepts, mystas) == false)
                FilingUnEnable = 0;

            ht.Add("NewEnable", NewEnable.ToString());
            ht.Add("SaveEnable", SaveEnable.ToString());
            ht.Add("DeleteEnable", DeleteEnable.ToString());
            ht.Add("FilingDoneEnable", FilingDoneEnable.ToString());
            ht.Add("FilingUnEnable", FilingUnEnable.ToString());

            return BP.Tools.Json.ToJson(ht);
        }

        /// <summary>
        /// 创建空白的DictID.
        /// </summary>
        /// <returns></returns>
        public string MyDict_CreateBlankDictID()
        {
            MapData md = new MapData(this.FrmID);

            if (md.EntityType == EntityType.FrmEntityNoName)
                return BP.CCBill.Dev2Interface.CreateBlankEntityNoName(this.FrmID, null, null);
            else
                return BP.CCBill.Dev2Interface.CreateBlankDictID(this.FrmID, null, null).ToString();
        }
        /// <summary>
        /// 执行保存
        /// </summary>
        /// <returns></returns>
        public string MyBill_SaveIt()
        {
            //创建entity 并执行copy方法.
            GEEntityOID rpt = new GEEntityOID(this.FrmID, this.WorkID);
            Attrs attrs = rpt.EnMap.Attrs;
            try
            {
                Hashtable ht = this.GetMainTableHT();
                foreach (string item in ht.Keys)
                {
                    rpt.SetValByKey(item, ht[item]);
                }
            }
            catch (Exception ex)
            {
                return "err@方法：MyBill_SaveIt错误，在执行  GetMainTableHT 期间" + ex.Message;
            }
            //执行保存.
            try
            {
                rpt.OID = this.WorkID;
                rpt.Update();
                string str = BP.CCBill.Dev2Interface.SaveBillWork(this.FrmID, this.WorkID);
                return str;
            }
            catch (Exception ex)
            {
                return "err@方法：MyBill_SaveIt 错误，在执行 SaveWork 期间出现错误:" + ex.Message;
            }
        }
        public string MyBill_Submit()
        {
            //执行保存.
            GEEntityOID rpt = new GEEntityOID(this.FrmID, this.WorkID);
            Hashtable ht = GetMainTableHT();
            foreach (string item in ht.Keys)
            {
                rpt.SetValByKey(item, ht[item]);
            }

            rpt.OID = this.WorkID;
            rpt.SetValByKey("BillState", (int)BillState.FrmOver);
            rpt.Update();

            string str = BP.CCBill.Dev2Interface.SubmitWork(this.WorkID);
            return str;
        }
        /// <summary>
        /// 撤销归档
        /// </summary>
        /// <returns></returns>
        public string MyBill_ArchiveUn()
        {
            FrmBill fb = new FrmBill(this.FrmID);

            GenerBill gb = new GenerBill();
            gb.WorkID = this.WorkID;
            int i = gb.RetrieveFromDBSources();
            if (i == 0)
                return "err@信息丢失";

            //设置为归档状态.
            gb.BillState = BillState.Editing;
            gb.Update();
            GEEntityOID rpt = new GEEntityOID(this.FrmID, this.WorkID);
            rpt.OID = this.WorkID;
            rpt.SetValByKey("BillState", (int)BillState.Editing);
            rpt.Update();

            BP.CCBill.Dev2Interface.WriteTrack(gb.FrmID, this.WorkID.ToString(), FrmActionType.UnArchive, "撤销归档成功.");
            return "撤销归档成功";
        }
        /// <summary>
        /// 预置审核人,这个时候，仅仅是把审核人暂存到参数里.
        /// 如果提交的时候，就把这些审核人取出来，发起流程.
        /// </summary>
        /// <returns></returns>
        public string MyBill_PreplaceChecker()
        {
            return BP.CCBill.Dev2Interface.Bill_PreplaceChecker(this.WorkID, this.RefNo);
        }
        /// <summary>
        /// 撤销发送
        /// </summary>
        /// <returns></returns>
        public string MyBill_UnSend()
        {
            //执行保存.
            GEEntityOID rpt = new GEEntityOID(this.FrmID, this.WorkID);
            Hashtable ht = GetMainTableHT();
            foreach (string item in ht.Keys)
            {
                rpt.SetValByKey(item, ht[item]);
            }

            rpt.OID = this.WorkID;
            //    rpt.SetValByKey("BillState", (int)BillState.Editing);
            rpt.Update();

            string str = BP.CCBill.Dev2Interface.Bill_UnSend(this.FrmID, this.WorkID);
            return str;
        }
        public string MyBill_RebackFlow()
        {
            //处理单据审核回滚.
            GenerBill gb = new GenerBill(this.WorkID);
            MapData md = new MapData(gb.FrmID);
            GEEntity ge = new GEEntity(gb.FrmID, this.WorkID);
            //执行回滚
            string frmEvent = ExecEvent.DoFrm(md, EventListFrm.Reback, ge);
            if (DataType.IsNullOrEmpty(frmEvent) == false && frmEvent.Contains("err@") == true)
                return frmEvent;

            gb.BillState = BillState.ReturnSta;
            gb.SetValByKey("SDTOfFlow", "无");
            gb.SetValByKey("SDTOfNode", "无");
            gb.SetValByKey("Idx", 0);
            gb.SetValByKey("CurrIdx", 0); //当前的步骤.

            //设置当前处理人.
            gb.SetValByKey("CurrCheckerNos", gb.Starter);
            gb.SetValByKey("CurrCheckerNames", gb.StarterName);

            string info2 = "回滚审批成功:发起人[" + gb.Starter + "," + gb.StarterName + "] \t\n 原因:" + this.Msg + "\t\n 执行人:" + WebUser.No + "," + WebUser.Name + " 日期:" + DataType.CurrentDateTime;
            gb.SetValByKey("Msg", info2); //消息.
            gb.Update();

            GenerWorkers wks = new GenerWorkers();
            wks.Retrieve("WorkID", this.WorkID, "Idx");
            foreach (GenerWorker wk in wks)
            {
                if (wk.Idx == 100)
                {
                    wk.Delete();
                    continue; //删除最后的一个通知节点.
                }

                wk.RDT = DataType.CurrentDateTime;
                if (wk.Idx == 0)
                {
                    wk.PassSta = PassSta.ReturnSta; //退回状态.
                    wk.Update();
                }
                else
                {
                    wk.PassSta = PassSta.UnPass; //未通过.
                }
                wk.SendDT = "无";
                wk.Update();
            }
            return info2 + frmEvent;
        }
        /// <summary>
        /// 删除按钮是否可用?
        /// </summary>
        /// <returns></returns>
        public string MyBillBtnsEnable_Delete()
        {
            DBRoles rls = new DBRoles();
            rls.Retrieve("FrmID", this.FrmID, "DBRole", "RecDelete", "IsEnable", 1);
            GenerBill gb = new GenerBill(this.WorkID);
            //首先检查默认的,自己紧急删除自己的.
            if (rls.Count == 0 && gb.Starter.Equals(WebUser.No) == true)
                return "1";

            foreach (DBRole rl in rls)
            {
                if (rl.MarkID.Equals("None") == true)
                    return "1";

                if (rl.MarkID.Equals("SelfOnly") == true && gb.Starter.Equals(WebUser.No) == true)
                    return "1";

                if (rl.MarkID.Equals("DeptLeader") == true)
                {
                    string sql = "SELECT Leader FROM Port_Dept WHERE No IN (SELECT FK_Dept FROM Port_DeptEmp WHERE FK_Emp='" + WebUser.No + "')";
                    string empNo = DBAccess.RunSQLReturnStringIsNull(sql, "");
                    if (empNo.Equals(WebUser.No) == true)
                        return "1";
                }

                if (rl.MarkID.Equals("ByStations") == true)
                {
                    string sql = "SELECT FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.No + "'";
                    DataTable dt = DBAccess.RunSQLReturnTable(sql);
                    string vals = "," + rl.Docs + ",";
                    foreach (DataRow dr in dt.Rows)
                    {
                        if (vals.Contains("," + dr[0].ToString() + ",") == true)
                            return "1";
                    }
                }

                if (rl.MarkID.Equals("ByDepts") == true)
                {
                    string sql = "SELECT FK_Dept FROM Port_DeptEmp WHERE FK_Emp='" + WebUser.No + "'";
                    DataTable dt = DBAccess.RunSQLReturnTable(sql);
                    string vals = "," + rl.Docs + ",";
                    foreach (DataRow dr in dt.Rows)
                    {
                        if (vals.Contains("," + dr[0].ToString() + ",") == true)
                            return "1";
                    }
                }

                if (rl.MarkID.Equals("ByEmps") == true)
                {
                    string vals = "," + rl.Docs + ",";
                    if (vals.Contains("," + WebUser.No + ",") == true)
                        return "1";
                }

                if (rl.MarkID.Equals("Adminer") == true && WebUser.No.Equals("admin"))
                    return "1";

                if (rl.MarkID.Equals("Admin2") == true && WebUser.IsAdmin == true)
                    return "1";
            }
            return "0";
        }
        /// <summary>
        /// 是否可以发起流程？
        /// </summary>
        /// <returns></returns>
        public string MyBillBtnsEnable_FlowEtcIsCanStartFlow()
        {
            string sql = "";
            return "";
        }
        /// <summary>
        /// 提交审核的权限
        /// </summary>
        /// <returns></returns>
        public string MyBillBtnsEnable_SubmitCheck()
        {
            DBRoles rls = new DBRoles();
            rls.Retrieve("FrmID", this.FrmID, "DBRole", "SubmitCheck", "IsEnable", 1);
            GenerBill gb = new GenerBill(this.WorkID);
            //首先检查默认的,自己紧急删除自己的.
            if (rls.Count == 0 && gb.Starter.Equals(WebUser.No) == true)
                return "1";

            foreach (DBRole rl in rls)
            {
                if (rl.MarkID.Equals("None") == true)
                    return "1";

                if (rl.MarkID.Equals("SelfOnly") == true && gb.Starter.Equals(WebUser.No) == true)
                    return "1";

                if (rl.MarkID.Equals("DeptLeader") == true)
                {
                    string sql = "SELECT Leader FROM Port_Dept WHERE No IN (SELECT FK_Dept FROM Port_DeptEmp WHERE FK_Emp='" + WebUser.No + "')";
                    string empNo = DBAccess.RunSQLReturnStringIsNull(sql, "");
                    if (empNo.Equals(WebUser.No) == true)
                        return "1";
                }

                if (rl.MarkID.Equals("ByStations") == true)
                {
                    string sql = "SELECT FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.No + "'";
                    DataTable dt = DBAccess.RunSQLReturnTable(sql);
                    string vals = "," + rl.Docs + ",";
                    foreach (DataRow dr in dt.Rows)
                    {
                        if (vals.Contains("," + dr[0].ToString() + ",") == true)
                            return "1";
                    }
                }
                if (rl.MarkID.Equals("ByDepts") == true)
                {
                    string sql = "SELECT FK_Dept FROM Port_DeptEmp WHERE FK_Emp='" + WebUser.No + "'";
                    DataTable dt = DBAccess.RunSQLReturnTable(sql);
                    string vals = "," + rl.Docs + ",";
                    foreach (DataRow dr in dt.Rows)
                    {
                        if (vals.Contains("," + dr[0].ToString() + ",") == true)
                            return "1";
                    }
                }
                if (rl.MarkID.Equals("ByEmps") == true)
                {
                    string vals = "," + rl.Docs + ",";
                    if (vals.Contains("," + WebUser.No + ",") == true)
                        return "1";
                }
                if (rl.MarkID.Equals("Adminer") == true && WebUser.No.Equals("admin"))
                    return "1";
                if (rl.MarkID.Equals("Admin2") == true && WebUser.IsAdmin == true)
                    return "1";
            }
            return "0";
        }
        public string WriteTrack()
        {
            BP.CCBill.Dev2Interface.WriteTrack(this.FrmID, this.PKVal.ToString(), "Info", this.Msg);
            return "写入成功.";
        }
        public string MyBill_Send()
        {
            return BP.CCBill.Dev2Interface.MyBill_Send(this.FrmID, this.WorkID, this.RefNo);
        }
        public string MyBill_ReturnWork()
        {
            string msg = this.GetRequestVal("Msg");
            string idx = this.GetRequestVal("ReturnToIdx");
            if (DataType.IsNullOrEmpty(idx) == true)
                idx = "0"; //退回到开始节点.
            return BP.CCBill.Dev2Interface.MyBill_ReturnWork(this.WorkID, int.Parse(idx), msg);
        }
        /// <summary>
        /// 开始节点读取通知内容结束.
        /// </summary>
        /// <returns></returns>
        public string MyBill_StarterReadOver()
        {
            GenerBill gb = new GenerBill(this.WorkID);
            if (gb.BillState != BillState.FlowOver)
                return "err@数据错误，当前不是完成状态.";
            DBAccess.RunSQL("DELETE FROM Frm_GenerWorker WHERE WorkID=" + this.WorkID);
            string str = gb.GetValStrByKey("Msg"); //提示完成信息.
            if (DataType.IsNullOrEmpty(str) == true)
                return "单据简易审核已经结束.";
            return str;
        }
        /// <summary>
        /// 撤销提交-OLD 应该深处嗲
        /// </summary>
        /// <returns></returns>
        public string MyBill_UnSubmit()
        {
            //执行保存.
            GEEntityOID rpt = new GEEntityOID(this.FrmID, this.WorkID);
            Hashtable ht = GetMainTableHT();
            foreach (string item in ht.Keys)
            {
                rpt.SetValByKey(item, ht[item]);
            }

            rpt.OID = this.WorkID;
            rpt.SetValByKey("BillState", (int)BillState.Editing);
            rpt.Update();

            string str = BP.CCBill.Dev2Interface.UnSubmitWork(this.WorkID);
            return str;
        }
        /// <summary>
        /// 开始节点初始化审核信息
        /// </summary>
        /// <returns></returns>
        public string MyBill_CheckerInit()
        {
            FrmBill frm = new FrmBill(this.FrmID);

            if (DataType.IsNullOrEmpty(frm.BillCheckModel) == true || frm.BillCheckModel.Equals("None"))
                return "err@当前表单不需要审核.";

            if (frm.BillCheckModel.Equals("SelfCheck") == true)
                return "info@SelfCheck,当前是自定义审核路径，请打开自定义审核路径页面.";

            if (frm.BillCheckModel.Equals("ByFlowNo") == true)
                return "info@ByFlowNo,当前是按照流程审核,请打开流程." + frm.BillCheckTag;

            //如果是按照API创建的,设置到审核队列，不允许修改.
            if (frm.BillCheckModel.Equals("ByAPI") == true)
            {
                GenerBill gb = new GenerBill(this.WorkID);
                string emps = gb.GetParaString("PreplaceChecker"); //获取预置的处理人.
                if (DataType.IsNullOrEmpty(emps) == true)
                    return "err@当前简易审核按照API设置的，但是没有获取到，预置的审核人员。";
                //设置处理人.
                string str = Dev2Interface.Bill_CheckerGoToOrder(this.FrmID, this.WorkID, emps);
                BP.CCBill.Dev2Interface.WriteTrack(this.FrmID, this.WorkID.ToString(), "Info", this.Msg + " 启动信息:" + str);

                //执行事件.
                MapData md = new MapData(gb.FrmID);
                GEEntityOID ge = new GEEntityOID(gb.FrmID, this.WorkID); 
                string frmEvent = ExecEvent.DoFrm(md, EventListFrm.CheckStart, ge);
                return "启动成功:" + str + "."+frmEvent;
            }

            //如果是按照设置的人员，不允许修改.
            if (frm.BillCheckModel.Equals("BySettingEmpNos") == true)
            {
                string empNos = "";

                for (int index = 1; index < 9; index++)
                {
                    string emps = DBAccess.RunSQLReturnStringIsNull("SELECT CheckEmpNo" + index + " FROM Sys_MapData WHERE No='" + this.FrmID + "'", null);
                    if (emps == null || DataType.IsNullOrEmpty(emps) == true)
                        break;
                    empNos += "," + emps;
                }
                if (DataType.IsNullOrEmpty(empNos) == true)
                    return "err@当前简易审核按照预置人员设置的，但是没有获取到，预置的审核人员，请在单据简易审核中维护审批人员路径。";

                //设置处理人.
                string str = Dev2Interface.Bill_CheckerGoToOrder(this.FrmID, this.WorkID, empNos);
                BP.CCBill.Dev2Interface.WriteTrack(this.FrmID, this.WorkID.ToString(), "Info", this.Msg + " 启动信息:" + str);

                //执行事件.
                MapData md = new MapData(this.FrmID);
                GEEntityOID ge = new GEEntityOID(this.FrmID, this.WorkID);
                string frmEvent = ExecEvent.DoFrm(md, EventListFrm.CheckStart, ge);
                return str+frmEvent;
            }

            //如果是按照SQL创建的,设置到审核队列，不允许修改.
            if (frm.BillCheckModel.Equals("BySQL") == true)
            {
                string sql = frm.BillCheckTag;
                DataTable dt = DBAccess.RunSQLReturnTable(sql);
                string emps = "";
                foreach (DataRow dr in dt.Rows)
                    emps += dr[0].ToString() + ",";
                //sql= GlowWF
                //GenerBill gb = new GenerBill(this.WorkID);
                //string emps = gb.GetParaString("PreplaceChecker"); //获取预置的处理人.
                //if (DataType.IsNullOrEmpty(emps) == true)
                //    return "err@当前简易审核按照API设置的，但是没有获取到，预置的审核人员。";
                //设置处理人.
                string str = Dev2Interface.Bill_CheckerGoToOrder(this.FrmID, this.WorkID, emps);
                BP.CCBill.Dev2Interface.WriteTrack(this.FrmID, this.WorkID.ToString(), "Info", this.Msg + " 启动信息:" + str);

                //执行事件.
                MapData md = new MapData(this.FrmID);
                GEEntityOID ge = new GEEntityOID(this.FrmID, this.WorkID);
                string frmEvent = ExecEvent.DoFrm(md, EventListFrm.CheckStart, ge);
                return str+""+ frmEvent;
            }

            return "err@没有判断的支持类型." + frm.BillCheckModel;
        }
        /// <summary>
        /// 开始节点提交审核
        /// </summary>
        /// <returns></returns>
        public string MyBill_CheckSubmit()
        {
            //执行保存.
            GEEntityOID rpt = new GEEntityOID(this.FrmID, this.WorkID);
            Hashtable ht = GetMainTableHT();
            foreach (string item in ht.Keys)
            {
                rpt.SetValByKey(item, ht[item]);
            }

            rpt.OID = this.WorkID;
            rpt.SetValByKey("BillState", (int)BillState.Editing);
            rpt.Update();

            string str = BP.CCBill.Dev2Interface.UnSubmitWork(this.WorkID);
            return str;
        }

        public string MyEntityNoName_SaveIt()
        {
            try
            {
                //执行保存.
                MapData md = new MapData(this.FrmID);
                GEEntityNoName rpt = new GEEntityNoName(this.FrmID, this.No);
                //rpt = BP.Pub.PubClass.CopyFromRequest(rpt) as GEEntity;
                Hashtable ht = GetMainTableHT();
                foreach (string item in ht.Keys)
                    rpt.SetValByKey(item, ht[item]);

                // 处理变量，这个rpt2有可能会重新查询.
                GEEntityNoName rpt2 = new GEEntityNoName(this.FrmID);
                rpt2.Row = rpt.Row.Clone() as BP.En.Row;

                //执行保存前事件
                ExecEvent.DoFrm(md, EventListFrm.SaveBefore, rpt2, null);

                rpt.No = this.No;
                rpt.SetValByKey("EntityState", 2); //设置编辑中.
                rpt.Update();

                //执行保存后事件
                ExecEvent.DoFrm(md, EventListFrm.SaveAfter, rpt, null);
                return "保存成功.";
            }
            catch (Exception ex)
            {
                return "err@" + ex.Message;
            }
        }

        /// <summary>
        /// 执行保存
        /// </summary>
        /// <returns></returns>
        public string MyDict_SaveIt()
        {
            try
            {
                //执行保存.
                MapData md = new MapData(this.FrmID);
                GEEntityOID rpt = new GEEntityOID(this.FrmID, this.WorkID);
                //rpt = BP.Pub.PubClass.CopyFromRequest(rpt) as GEEntity;
                Hashtable ht = GetMainTableHT();
                foreach (string item in ht.Keys)
                    rpt.SetValByKey(item, ht[item]);

                // 处理变量，这个rpt2有可能会重新查询.
                GEEntityOID rpt2 = new GEEntityOID(this.FrmID);
                rpt2.Row = rpt.Row.Clone() as BP.En.Row;

                //执行保存前事件
                ExecEvent.DoFrm(md, EventListFrm.SaveBefore, rpt2, null);

                rpt.OID = this.WorkID;
                rpt.SetValByKey("BillState", 2); //设置编辑中.
                rpt.Update();

                //执行保存后事件
                ExecEvent.DoFrm(md, EventListFrm.SaveAfter, rpt, null);
                return "保存成功.";
            }
            catch (Exception ex)
            {
                return "err@" + ex.Message;
            }
        }
        /// <summary>
        /// 设置归档
        /// </summary>
        /// <returns></returns>
        public string MyDict_FilingDone()
        {
            GEEntityOID rpt = new GEEntityOID(this.FrmID, this.WorkID);
            rpt.OID = this.WorkID;
            rpt.SetValByKey("BillState", (int)DictState.Filing); //设置编辑中.
            rpt.Update();
            return "设置成功.";
        }
        public string MyEntityNoName_FilingDone()
        {
            GEEntityNoName rpt = new GEEntityNoName(this.FrmID, this.No);
            rpt.SetValByKey("EntityState", (int)DictState.Filing); //设置编辑中.
            rpt.Update();
            return "设置成功.";
        }
        public string MyEntityNoName_FilingDoneUn()
        {
            GEEntityNoName rpt = new GEEntityNoName(this.FrmID, this.No);
            rpt.SetValByKey("EntityState", 2); //设置编辑中.
            rpt.Update();
            return "设置成功.";
        }
        public string MyDict_FilingDoneUn()
        {
            GEEntityOID rpt = new GEEntityOID(this.FrmID, this.WorkID);
            rpt.OID = this.WorkID;
            rpt.SetValByKey("BillState", 2); //设置编辑中.
            rpt.Update();
            return "设置成功.";
        }

        public string MyDict_Delete()
        {
            GEEntityOID rpt = new GEEntityOID(this.FrmID, this.WorkID);
            rpt.OID = this.WorkID;
            rpt.Delete();
            return "删除成功.";
        }
      
        public string MyEntityNoName_Delete()
        {
            GEEntityNoName rpt = new GEEntityNoName(this.FrmID, this.No);
            rpt.SetValByKey("EntityState", -1); //设置删除状态.
            rpt.Update();
            return "设置成功.";
        }
        /// <summary>
        /// 方法初始化
        /// </summary>
        /// <returns></returns>
        public string MyDict_MethodFunc_Init()
        {
            DataSet ds = new DataSet();
            //根据MethodNo获取MapAttrs
            MapAttrs mapAttrs = new MapAttrs();
            mapAttrs.Retrieve(MapAttrAttr.FK_MapData, this.MethodNo, MapAttrAttr.Idx);
            ds.Tables.Add(mapAttrs.ToDataTableField("Sys_MapAttr"));
            //获取MapExt
            MapExts mapExts = new MapExts();
            mapExts.Retrieve(MapExtAttr.FK_MapData, this.MethodNo);
            ds.Tables.Add(mapExts.ToDataTableField("Sys_MapExt"));

            Map map = new Map(this.MethodNo, this.MethodNo);
            GEEntity en = new GEEntity(this.MethodNo);
            Attrs attrs = new Attrs();
            foreach (MapAttr mapAttr in mapAttrs)
                map.AddAttr(mapAttr.HisAttr);
            Cache.SetMap(this.MethodNo, map);
            en.ResetDefaultVal();
            ds.Tables.Add(en.ToDataTableField("MainTable"));

            #region 把外键与枚举放入里面去.

            //加入外键.
            foreach (MapAttr mapAttr in mapAttrs)
            {
                string uiBindKey = mapAttr.UIBindKey;
                if (mapAttr.LGType != FieldTypeS.FK)
                    continue;

                bool UIVisible = mapAttr.UIVisible;
                bool uiIsEnable = mapAttr.UIIsEnable;
                if (UIVisible == false || uiIsEnable == false)
                    continue;
                if (DataType.IsNullOrEmpty(uiBindKey) == true)
                    continue;

                // 检查是否有下拉框自动填充。
                string keyOfEn = mapAttr.KeyOfEn;
                string fk_mapDat = mapAttr.FrmID;

                // 判断是否存在.
                if (ds.Tables.Contains(uiBindKey) == true)
                    continue;

                DataTable dt = BP.Pub.PubClass.GetDataTableByUIBineKey(uiBindKey);
                dt.TableName = keyOfEn;

                ds.Tables.Add(dt);
            }
            //加入枚举的外键.
            SysEnums ens = new SysEnums();

            if (BP.Difference.SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
            {
                string enumKeySQL = "SELECT UIBindKey FROM Sys_MapAttr WHERE FK_MapData = '" + this.No + "' AND LGType = 1 ";
                string sqlWhere = " EnumKey IN (" + enumKeySQL + ") AND OrgNo='" + BP.Web.WebUser.OrgNo + "'";
                string sqlEnum = "SELECT * FROM " + BP.Sys.Base.Glo.SysEnum() + " WHERE " + sqlWhere;
                sqlEnum += " UNION ";
                sqlEnum += "SELECT * FROM " + BP.Sys.Base.Glo.SysEnum() + " WHERE EnumKey IN (" + enumKeySQL + ") AND EnumKey NOT IN (SELECT EnumKey FROM Sys_Enum WHERE " + sqlWhere + ") AND (OrgNo Is Null Or OrgNo='')";
                sqlEnum += "Order By IntKey";
                DataTable dt = DBAccess.RunSQLReturnTable(sqlEnum);

                QueryObject.InitEntitiesByDataTable(ens, dt, null);
            }
            else
            {
                ens.RetrieveInSQL(SysEnumAttr.EnumKey, "SELECT UIBindKey FROM Sys_MapAttr WHERE FK_MapData='" + this.No + "' AND LGType=1 ", SysEnumAttr.IntKey);
            }
            ds.Tables.Add(ens.ToDataTableField("Sys_Enum"));
            #endregion 把外键与枚举放入里面去.
            return BP.Tools.Json.ToJson(ds);
        }
        /// <summary>
        /// 打印rtf 
        /// </summary>
        /// <returns></returns>
        public string MyDict_PrintRTF()
        {
            string frmID = this.GetRequestVal("FrmID");
            FrmDict frmDict = new FrmDict(frmID);
            //方法ID.
            string mID = this.GetRequestVal("Method");

            BP.CCBill.Template.Method md = new BP.CCBill.Template.Method();
            md.No = mID;
            md.RetrieveFromDBSources();


            FrmPrintTemplate temp = new FrmPrintTemplate();
            temp.MyPK = md.No;
            int i = temp.RetrieveFromDBSources();
            //temp.TempFilePath = md.GetValStringByKey("MyFilePath"); //这里需要路径支持, 
            temp.TempFilePath = md.No;
            temp.FrmID = this.FrmID;
            temp.TemplateFileModel = TemplateFileModel.VSTOForWord; //word模式。
            temp.HisPrintFileType = (PrintFileType)md.GetValIntByKey("PrintFileType", 1); // = 1; //word模式。
            if (i == 0)
                temp.Insert();
            else
                temp.Update();
            BP.WF.HttpHandler.WF_WorkOpt opt = new WF_WorkOpt();
            string fileUrl = "";
            if (frmDict.EntityType != EntityType.FrmEntityNoName)
                fileUrl = opt.PrintDoc_FormDoneIt(null, this.WorkID, 0, this.FrmID, temp);
            else
                fileUrl = opt.PrintDoc_EntityNoNameDoneIt(this.No, this.FrmID, temp);
            if (temp.HisPrintFileType == PrintFileType.Word)
                fileUrl = fileUrl.Replace("file@word@", "");
            if (temp.HisPrintFileType == PrintFileType.PDF)
                fileUrl = fileUrl.Replace("file@pdf@", "");
            Hashtable ht = new Hashtable();
            ht.Add("FileName", md.Name);
            ht.Add("FileUrl", fileUrl);
            return BP.Tools.Json.ToJson(ht);
        }
        /// <summary>
        /// 执行保存
        /// </summary>
        /// <returns></returns>
        public string MyDict_Submit()
        {
            //   return "err@不在支持提交功能.";
            //执行保存.
            MapData md = new MapData(this.FrmID);
            GEEntityOID rpt = new GEEntityOID(this.FrmID, this.WorkID);
            //rpt = BP.Pub.PubClass.CopyFromRequest(rpt) as GEEntity;

            Hashtable ht = GetMainTableHT();
            foreach (string item in ht.Keys)
            {
                rpt.SetValByKey(item, ht[item]);
            }

            //执行保存前事件
            ExecEvent.DoFrm(md, EventListFrm.SaveBefore, rpt, null);

            rpt.OID = this.WorkID;
            rpt.SetValByKey("BillState", (int)BillState.FrmOver);
            rpt.Update();

            //执行保存后事件
            ExecEvent.DoFrm(md, EventListFrm.SaveAfter, rpt, null);
            return "归档成功.";
        }
        public string MyDict_Draft()
        {
            //   return "err@不在支持提交功能.";
            //执行保存.
            MapData md = new MapData(this.FrmID);
            GEEntityOID rpt = new GEEntityOID(this.FrmID, this.WorkID);

            Hashtable ht = GetMainTableHT();
            foreach (string item in ht.Keys)
            {
                rpt.SetValByKey(item, ht[item]);
            }

            //执行保存前事件
            ExecEvent.DoFrm(md, EventListFrm.SaveBefore, rpt, null);

            rpt.OID = this.WorkID;
            rpt.SetValByKey("BillState", (int)BillState.Draft);
            rpt.Update();

            //执行保存后事件
            ExecEvent.DoFrm(md, EventListFrm.SaveAfter, rpt, null);
            return "设置草稿成功..";
        }

        public string GetFrmEntitys()
        {
            GEEntitys rpts = new GEEntitys(this.FrmID);
            QueryObject qo = new QueryObject(rpts);
            qo.AddWhere("BillState", " != ", 0);
            qo.DoQuery();
            return BP.Tools.Json.ToJson(rpts.ToDataTableField());
        }
        private Hashtable GetMainTableHT()
        {
            Hashtable htMain = new Hashtable();
            foreach (string key in HttpContextHelper.RequestParamKeys)
            {
                if (key == null)
                    continue;

                string myKey = key;
                string val = HttpContextHelper.RequestParams(key);
                myKey = myKey.Replace("TB_", "");
                myKey = myKey.Replace("DDL_", "");
                myKey = myKey.Replace("CB_", "");
                myKey = myKey.Replace("RB_", "");
                val = HttpUtility.UrlDecode(val, Encoding.UTF8);

                if (htMain.ContainsKey(myKey) == true)
                    htMain[myKey] = val;
                else
                    htMain.Add(myKey, val);
            }

            return htMain;
        }

        public string MyBill_SaveAsDraft()
        {
            string str = BP.CCBill.Dev2Interface.SaveBillWork(this.FrmID, this.WorkID);
            return str;
        }
        /// <summary>
        /// 删除单据
        /// </summary>
        /// <returns></returns>
        public string MyBill_Delete()
        {
            return BP.CCBill.Dev2Interface.MyBill_Delete(this.FrmID, this.WorkID);
        }
        public string MyBill_Deletes()
        {
            return BP.CCBill.Dev2Interface.MyBill_DeleteBills(this.FrmID, this.GetRequestVal("WorkIDs"));
        }
        /// <summary>
        /// 删除多个
        /// </summary>
        /// <returns></returns>
        public string MyDict_Deletes()
        {
            return BP.CCBill.Dev2Interface.MyDict_DeleteDicts(this.FrmID, this.GetRequestVal("WorkIDs"));
        }

        public string MyEntityTree_Deletes()
        {
            return BP.CCBill.Dev2Interface.MyDict_DeleteDicts(this.FrmID, this.GetRequestVal("Nos"));
        }

        public string MyEntityTree_Delete()
        {
            return BP.CCBill.Dev2Interface.MyEntityTree_Delete(this.FrmID, this.GetRequestVal("BillNo"));
        }
        public string MyEntityNoName_Deletes()
        {
            return BP.CCBill.Dev2Interface.MyEntityNoName_Deletes(this.FrmID, this.GetRequestVal("Nos"));
        }
        /// <summary>
        /// 单据初始化
        /// </summary>
        /// <returns></returns>
        public string MyBill_Init()
        {
            //获得发起列表. 
            DataTable dt = BP.CCBill.Dev2Interface.DB_StartBills(BP.Web.WebUser.No);

            //返回组合
            return BP.Tools.Json.ToJson(dt);
        }
        /// <summary>
        /// 重新生成title.
        /// </summary>
        /// <returns></returns>
        public string MyBill_GenerTitle()
        {
            GenerBill gb = new GenerBill(this.WorkID);
            FrmBill fb = new FrmBill(gb.FrmID);
            GEEntity ge = new GEEntity(gb.FrmID, this.WorkID);

            string title = Dev2Interface.GenerTitle(fb.TitleRole, ge);
            gb.SetValByKey("Title", title);
            gb.Update();

            ge.SetValByKey("Title", title);
            ge.Update();
            return "设置成功.";
        }
        /// <summary>
        /// 设置标题.
        /// </summary>
        /// <returns></returns>
        public string MyBill_SetTitle()
        {
            GenerBill gb = new GenerBill(this.WorkID);

            FrmBill fb = new FrmBill(gb.FrmID);
            GEEntity ge = new GEEntity(gb.FrmID, this.WorkID);

            gb.SetValByKey("Title", this.Msg);
            gb.Update();
            ge.SetValByKey("Title", this.Msg);
            ge.Update();

            return "设置成功.";
        }
        /// <summary>
        /// 复制单据数据
        /// </summary>
        /// <returns></returns>
        public string MyBill_Copy()
        {
            return BP.CCBill.Dev2Interface.MyBill_Copy(this.FrmID, this.WorkID);
        }
        #endregion 单据处理.

        #region 单据处理.GL
        /// <summary>
        /// 待办
        /// </summary>
        /// <returns></returns>
        public string DB_Todolist()
        {
            //获得发起列表. 
            DataTable dt = BP.CCBill.Dev2Interface.DB_Todolist(WebUser.No, this.FrmID);
            //返回组合
            return BP.Tools.Json.ToJson(dt);
        }
        /// <summary>
        /// 发起列表.
        /// </summary>
        /// <returns></returns>
        public string DB_StartBills()
        {
            //获得发起列表. 
            DataTable dt = BP.CCBill.Dev2Interface.DB_StartBills(BP.Web.WebUser.No);

            //返回组合
            return BP.Tools.Json.ToJson(dt);
        }
        /// <summary>
        /// 草稿列表
        /// </summary>
        /// <returns></returns>
        public string DB_Draft()
        {
            //草稿列表.
            DataTable dt = BP.CCBill.Dev2Interface.DB_Draft(this.FrmID, BP.Web.WebUser.No);

            //返回组合
            return BP.Tools.Json.DataTableToJson(dt, false);
        }
        public string DB_Recent()
        {
            //获得发起列表. 
            DataTable dt = BP.CCBill.Dev2Interface.DB_Recent(WebUser.No);
            //返回组合
            return BP.Tools.Json.DataTableToJson(dt, false);
        }

        #endregion 单据处理.

        #region 获取查询条件
        public string Search_ToolBar()
        {
            DataSet ds = new DataSet();

            DataTable dt = new DataTable();

            //根据FrmID获取Mapdata
            MapData md = new MapData(this.FrmID);
            if (md.EntityType == EntityType.DBList)
            {
                DBListDBSrc dbList = new DBListDBSrc(this.FrmID);
                ds.Tables.Add(dbList.ToDataTableField("Sys_DBList"));
            }
            //如果设置按照时间字段的月度，季度，年度查询数据，需要查询数据显示的最小年份
            if (md.DTSearchWay != DTSearchWay.None && md.GetParaInt("DTShowWay") == 1)
            {
                GEEntity en = new GEEntity(this.FrmID);
                try
                {
                    string sql = "SELECT min(" + md.DTSearchKey + ") From " + en.EnMap.PhysicsTable;
                    md.SetPara("DateShowYear", DBAccess.RunSQLReturnStringIsNull(sql, ""));
                }
                catch (Exception e)
                {
                    GEEntity rpt = new GEEntity(this.FrmID);
                    rpt.CheckPhysicsTable();
                    string sql = "SELECT min(" + md.DTSearchKey + ") From " + en.EnMap.PhysicsTable;
                    md.SetPara("DateShowYear", DBAccess.RunSQLReturnStringIsNull(sql, ""));
                }

            }
            ds.Tables.Add(md.ToDataTableField("Sys_MapData"));
            //获取字段属性
            MapAttrs attrs = new MapAttrs(this.FrmID);

            #region //增加枚举/外键字段信息
            dt.Columns.Add("Field", typeof(string));
            dt.Columns.Add("Name", typeof(string));
            dt.Columns.Add("Width", typeof(int));
            dt.TableName = "Attrs";
            dt.PrimaryKey = new DataColumn[] { dt.Columns["Field"] };
            ds.Tables.Add(dt);
            string[] ctrls = md.RptSearchKeys.Split('*');
            DataTable dtNoName = null;

            MapAttr mapattr;
            DataRow dr = null;
            MapExts mapExts = new MapExts();
            QueryObject qo = new QueryObject(mapExts);
            qo.AddWhere("FK_MapData", this.FrmID);
            qo.addAnd();
            qo.AddWhereIn("ExtType", "('ActiveDDLSearchCond','AutoFullDLLSearchCond')");
            qo.DoQuery();
            ds.Tables.Add(mapExts.ToDataTableField("Sys_MapExt"));
            foreach (string ctrl in ctrls)
            {
                //增加判断，如果URL中有传参，则不进行此SearchAttr的过滤条件显示
                if (DataType.IsNullOrEmpty(ctrl) || !DataType.IsNullOrEmpty(HttpContextHelper.RequestParams(ctrl)))
                    continue;

                mapattr = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, ctrl) as MapAttr;
                if (mapattr == null)
                    continue;

                dr = dt.NewRow();
                dr["Field"] = mapattr.KeyOfEn;
                dr["Name"] = mapattr.Name;
                dr["Width"] = mapattr.UIWidth;
                dt.Rows.Add(dr);

                Attr attr = mapattr.HisAttr;
                if (mapattr == null)
                    continue;

                if (attr.Key.Equals("FK_Dept") || attr.Key.Equals("DeptNo"))
                    continue;

                if (attr.ItIsEnum == true)
                {
                    SysEnums ses = new SysEnums(mapattr.UIBindKey);
                    DataTable dtEnum = ses.ToDataTableField();
                    dtEnum.TableName = mapattr.KeyOfEn;
                    ds.Tables.Add(dtEnum);
                    continue;
                }
                if (attr.ItIsFK == true)
                {
                    Entities ensFK = attr.HisFKEns;
                    if (ensFK != null)
                    {
                        ensFK.RetrieveAll();
                        DataTable dtEn = ensFK.ToDataTableField();
                        dtEn.TableName = attr.Key;
                        ds.Tables.Add(dtEn);
                    }



                }
                //绑定SQL的外键
                if (ds.Tables.Contains(attr.Key) == false)
                {
                    DataTable dtSQl = null;
                    MapExt mapExt = mapExts.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLLSearchCond, MapExtAttr.AttrOfOper, attr.Key) as MapExt;
                    if (mapExt != null)
                    {
                        string fullSQL = mapExt.Doc.Clone() as string;
                        if (fullSQL == null)
                            throw new Exception("err@字段[" + attr.Key + "]下拉框AutoFullDLLSearchCond，没有配置SQL");

                        fullSQL = fullSQL.Replace("~", "'");
                        fullSQL = BP.WF.Glo.DealExp(fullSQL, null, null);
                        dtSQl = DBAccess.RunSQLReturnTable(fullSQL);
                    }
                    else if (DataType.IsNullOrEmpty(attr.UIBindKey) == false)
                    {
                        dtSQl = BP.Pub.PubClass.GetDataTableByUIBineKey(attr.UIBindKey);
                    }
                    if (dtSQl != null)
                    {
                        foreach (DataColumn col in dtSQl.Columns)
                        {
                            string colName = col.ColumnName.ToLower();
                            switch (colName)
                            {
                                case "no":
                                case "NO":
                                    col.ColumnName = "No";
                                    break;
                                case "name":
                                case "NAME":
                                    col.ColumnName = "Name";
                                    break;
                                case "parentno":
                                case "PARENTNO":
                                    col.ColumnName = "ParentNo";
                                    break;
                                default:
                                    break;
                            }
                        }
                        dtSQl.TableName = attr.Key;
                        ds.Tables.Add(dtSQl);
                    }
                }

            }

            //数据查询权限除只查看自己创建的数据外增加部门的查询条件
            SearchDataRole searchDataRole = (SearchDataRole)md.GetParaInt("SearchDataRole");
            if (searchDataRole != SearchDataRole.ByOnlySelf)
            {
                DataTable dd = GetDeptDataTable(searchDataRole, md);
                if (dd.Rows.Count == 0 && md.GetParaInt("SearchDataRoleByDeptStation") == 1)
                    dd = GetDeptAndSubLevel();
                if (dd.Rows.Count != 0)
                {
                    //增加部门的查询条件
                    if (dt.Rows.Contains("FK_Dept") == false)
                    {
                        dr = dt.NewRow();
                        dr["Field"] = "FK_Dept";
                        dr["Name"] = "部门";
                        dr["Width"] = 120;
                        dt.Rows.Add(dr);
                    }

                    dd.TableName = "FK_Dept";
                    ds.Tables.Add(dd);

                }
            }
            Methods methods = new Methods();
            //实体类方法
            try
            {
                methods.Retrieve(MethodAttr.FrmID, this.FrmID, MethodAttr.IsSearchBar, 1, MethodAttr.Idx);
            }
            catch (Exception e)
            {
                methods.GetNewEntity.CheckPhysicsTable();
                methods.Retrieve(MethodAttr.FrmID, this.FrmID, MethodAttr.IsSearchBar, 1, MethodAttr.IsEnable, 1, MethodAttr.Idx);

            }
            ds.Tables.Add(methods.ToDataTableField("Frm_Method"));


            Collections colls = Search_BtnPower();
            ds.Tables.Add(methods.ToDataTableField("ToolBtns"));
            ds.Tables.Add(colls.ToDataTableField("Frm_Collection"));

            return BP.Tools.Json.ToJson(ds);

        }
        #endregion 查询条件

        private DataTable GetDeptDataTable(SearchDataRole searchDataRole, MapData md)
        {
            //增加部门的外键
            DataTable dt = new DataTable();
            string sql = "";
            if (searchDataRole == SearchDataRole.ByDept)
            {
                sql = "SELECT D.No,D.Name From Port_Dept D,Port_DeptEmp E WHERE D.No=E.FK_Dept AND E.FK_Emp='" + WebUser.No + "'";
                dt = DBAccess.RunSQLReturnTable(sql);
            }
            if (searchDataRole == SearchDataRole.ByDeptAndSSubLevel)
            {
                dt = GetDeptAndSubLevel();
            }
            if (searchDataRole == SearchDataRole.ByStationDept)
            {
                sql = "SELECT D.No,D.Name From Port_Dept D WHERE D.No IN(SELECT F.FK_Dept FROM Frm_StationDept F,Port_DeptEmpStation P Where F.FK_Station = P.FK_Station AND F.FK_Frm='" + md.No + "' AND P.FK_Emp='" + WebUser.UserID + "')";
                dt = DBAccess.RunSQLReturnTable(sql);
            }
            foreach (DataColumn col in dt.Columns)
            {
                string colName = col.ColumnName.ToLower();
                switch (colName)
                {
                    case "no":
                        col.ColumnName = "No";
                        break;
                    case "name":
                        col.ColumnName = "Name";
                        break;

                    default:
                        break;
                }

            }
            return dt;
        }
        private DataTable GetDeptAndSubLevel()
        {
            //获取本部门和兼职部门
            string sql = "SELECT D.No,D.Name From Port_Dept D,Port_DeptEmp E WHERE D.No=E.FK_Dept AND E.FK_Emp='" + WebUser.No + "'";
            DataTable dt = DBAccess.RunSQLReturnTable(sql);
            dt.PrimaryKey = new DataColumn[] { dt.Columns["No"] };
            DataTable dd = dt.Copy();
            foreach (DataRow dr in dd.Rows)
            {
                GetSubLevelDeptByParentNo(dt, dr[0].ToString());
            }
            return dt;
        }

        private void GetSubLevelDeptByParentNo(DataTable dt, string parentNo)
        {
            string sql = "SELECT No,Name FROM Port_Dept Where ParentNo='" + parentNo + "'";
            DataTable dd = DBAccess.RunSQLReturnTable(sql);

            foreach (DataRow dr in dd.Rows)
            {
                if (dt.Rows.Contains(dr[0].ToString()) == true)
                    continue;
                dt.Rows.Add(dr.ItemArray);

                GetSubLevelDeptByParentNo(dt, dr[0].ToString());

            }
        }
        public string Search_TreeData()
        {
            MapData mapData = new MapData(this.FrmID);
            int listShowWay = mapData.GetParaInt("ListShowWay");
            string listShowWayKey = mapData.GetParaString("ListShowWayKey");
            if (DataType.IsNullOrEmpty(listShowWayKey) == true)
                return "err@树形结构展示的字段不存在，请检查查询条件设置中展示方式配置是否正确";
            MapAttr mapAttr = new MapAttr(this.FrmID + "_" + listShowWayKey);
            //获取绑定的数据源
            if (DataType.IsNullOrEmpty(mapAttr.UIBindKey) == true)
                return "err@字段" + mapAttr.Name + "绑定的外键或者外部数据源不存在,请检查字段属性[外键SFTable]是否为空";
            DataTable dt = BP.Pub.PubClass.GetDataTableByUIBineKey(mapAttr.UIBindKey);
            return BP.Tools.Json.ToJson(dt);

        }

        /// <summary>
        /// 实体、单据列表显示的字段
        /// </summary>
        /// <returns></returns>
        public string Search_MapAttr()
        {
            FrmDict frmDict = new FrmDict(this.FrmID);
            if (frmDict.EntityType == EntityType.DBList)
                return Search_MapAttrForDB();
            #region 查询显示的列

            MapAttrs mattrs = new MapAttrs();
            mattrs.Retrieve(MapAttrAttr.FK_MapData, this.FrmID, MapAttrAttr.Idx);

            MapExts mapExts = new MapExts();
            QueryObject qo = new QueryObject(mapExts);
            qo.AddWhere(MapExtAttr.FK_MapData, this.FrmID);
            qo.addAnd();
            qo.AddWhereIn(MapExtAttr.ExtType, "('MultipleChoiceSmall','SingleChoiceSmall')");
            qo.DoQuery();
            foreach (MapExt mapExt in mapExts)
            {
                //获取mapAttr
                MapAttr mapAttr = mattrs.GetEntityByKey(this.FrmID + "_" + mapExt.AttrOfOper) as MapAttr;
                string searchVisable = mapAttr.atPara.GetValStrByKey("SearchVisable");
                if (searchVisable == "0")
                    continue;
                mapAttr.SetPara("SearchVisable", 0);
                mapAttr.Update();
                mapAttr = mattrs.GetEntityByKey(this.FrmID + "_" + mapExt.AttrOfOper + "T") as MapAttr;
                mapAttr.SetPara("SearchVisable", 1);
                mapAttr.Update();
            }
            DataRow row = null;
            DataTable dt = new DataTable("Attrs");
            dt.Columns.Add("KeyOfEn", typeof(string));
            dt.Columns.Add("Name", typeof(string));
            dt.Columns.Add("Width", typeof(int));
            dt.Columns.Add("UIContralType", typeof(int));
            dt.Columns.Add("LGType", typeof(int));
            dt.Columns.Add("MyDataType", typeof(int));
            dt.Columns.Add("UIBindKey", typeof(string));
            dt.Columns.Add("AtPara", typeof(string));
            dt.Columns.Add("IsRichText", typeof(int));
            int showColModel = frmDict.GetValIntByKey("ShowColModel");
            //设置标题、单据号位于开始位置
            foreach (MapAttr attr in mattrs)
            {
                if (showColModel == 0 && attr.UIVisible == false)
                    continue;
                if (showColModel == 1)
                {
                    string searchVisable = attr.atPara.GetValStrByKey("SearchVisable");
                    if (searchVisable == "0")
                        continue;
                    if (DataType.IsNullOrEmpty(searchVisable) == true && attr.UIVisible == false)
                        continue;
                }

                row = dt.NewRow();
                row["KeyOfEn"] = attr.KeyOfEn;
                row["Name"] = attr.Name;
                row["Width"] = attr.UIWidthInt;
                row["UIContralType"] = attr.UIContralType;
                row["LGType"] = attr.LGType;
                row["MyDataType"] = attr.MyDataType;
                row["UIBindKey"] = attr.UIBindKey;
                row["AtPara"] = attr.GetValStringByKey("AtPara");
                row["IsRichText"] = attr.TextModel == 3 ? 1 : 0;
                dt.Rows.Add(row);
            }

            #endregion 查询显示的列
            DataSet ds = new DataSet();
            ds.Tables.Add(dt);
            //增加枚举
            MapData mapData = new MapData(this.FrmID);
            ds.Tables.Add(mapData.SysEnums.ToDataTableField("Sys_Enum"));
            //查询一行数据的操作
            Methods methods = new Methods();
            methods.Retrieve(MethodAttr.FrmID, this.FrmID, MethodAttr.IsList, 1, MethodAttr.Idx);

            ds.Tables.Add(methods.ToDataTableField("Frm_Method"));

            return BP.Tools.Json.ToJson(ds);
        }
        /// <summary>
        /// 获取查询列表的按钮权限
        /// </summary>
        /// <returns></returns>
        public Collections Search_BtnPower()
        {
            //获取该表单所有操作按钮的权限
            Collections colls = new Collections();
            QueryObject qo = new QueryObject(colls);
            qo.AddWhere(CollectionAttr.FrmID, this.FrmID);
            qo.addAnd();
            qo.AddWhere(CollectionAttr.IsEnable, ">", 0);
            qo.addOrderBy("Idx");
            qo.DoQuery();
            if (colls.Count == 0)
            {
                //查询
                Collection collection = new Collection();
                collection.FrmID = this.FrmID;
                collection.MethodID = "Search";
                collection.Name = "查询";
                collection.MethodModel = "Search";
                collection.Mark = "Search";
                collection.No = collection.FrmID + "_" + collection.MethodID;
                collection.SetValByKey("Idx", 0);
                collection.Insert();

                //新建
                collection = new Collection();
                collection.FrmID = this.FrmID;
                collection.MethodID = "New";
                collection.Name = "新建";
                collection.MethodModel = "New";
                collection.Mark = "New";
                collection.No = collection.FrmID + "_" + collection.MethodID;
                collection.SetValByKey("Idx", 1);
                collection.Insert();

                //删除
                collection = new Collection();
                collection.FrmID = this.FrmID;
                collection.MethodID = "Delete";
                collection.Name = "删除";
                collection.MethodModel = "Delete";
                collection.Mark = "Delete";
                collection.No = collection.FrmID + "_" + collection.MethodID;
                collection.SetValByKey("Idx", 2);
                collection.SetPara("EnName", "TS.CCBill.CollectionDelete");
                collection.Insert();

                // 分析.
                collection = new Collection();
                collection.FrmID = this.FrmID;
                collection.MethodID = "Group";
                collection.Name = "分析";
                collection.MethodModel = "Group";
                collection.Mark = "Group";
                collection.No = collection.FrmID + "_" + collection.MethodID;
                collection.SetValByKey("Idx", 3);
                collection.SetValByKey("IsEnable", false);
                collection.Insert();

                //导出
                collection = new Collection();
                collection.FrmID = this.FrmID;
                collection.MethodID = "ExpExcel";
                collection.Name = "导出Excel";
                collection.MethodModel = "ExpExcel";
                collection.Mark = "ExpExcel";
                collection.No = collection.FrmID + "_" + collection.MethodID;
                collection.SetValByKey("Idx", 4);
                collection.Insert();

                //导入
                collection = new Collection();
                collection.FrmID = this.FrmID;
                collection.MethodID = "ImpExcel";
                collection.Name = "导入Excel";
                collection.MethodModel = "ImpExcel";
                collection.Mark = "ImpExcel";
                collection.No = collection.FrmID + "_" + collection.MethodID;
                collection.SetValByKey("Idx", 5);
                collection.Insert();

                colls.Retrieve(GroupMethodAttr.FrmID, this.FrmID, "Idx");
            }

            #region 整理参数
            DBRoles rols = new DBRoles();
            rols.Retrieve("FrmID", this.FrmID);
            string mydepts = "" + WebUser.DeptNo + ","; //我的部门.
            string mystas = ""; //我的角色.
            DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.UserID + "'");
            foreach (DataRow dr in mydeptsDT.Rows)
            {
                mydepts += dr[0].ToString() + ",";
                mystas += dr[1].ToString() + ",";
            }
            #endregion 整理参数

            //组织集合.
            Collections collsNew = new Collections();
            foreach (Collection item in colls)
            {

                #region 判断新建按钮
                if (item.MethodID.Equals("New") == true)
                {
                    if (this.CheckRoles(rols, "RecNew", mydepts, mystas) == true)
                    {
                        collsNew.AddEntity(item);
                        continue;
                    }
                    continue;
                }
                #endregion 判断新建按钮

                #region 导出
                if (item.MethodID.Equals("ExpExcel") == true)
                {
                    if (this.CheckRoles(rols, "ExpExcel", mydepts, mystas) == true)
                    {
                        collsNew.AddEntity(item);
                        continue;
                    }
                    continue;
                }
                #endregion 导出

                #region 导入
                if (item.MethodID.Equals("ImpExcel") == true)
                {
                    if (this.CheckRoles(rols, "ImpExcel", mydepts, mystas) == true)
                    {
                        collsNew.AddEntity(item);
                        continue;
                    }
                    continue;
                }
                #endregion 导出


                collsNew.AddEntity(item);
            }
            //判断是否可以新建.
            return collsNew;

        }
        public bool CheckRoles(DBRoles rols, string dbRole, string strDepps, string strStas)
        {
            if (rols.GetCountByKey("DBRole", dbRole) == 0)
                return true;

            int num = 0;
            foreach (DBRole rol in rols)
            {
                if (rol.GetValStringByKey("DBRole").Equals(dbRole) == false)
                    continue;

                num++;
                string markID = rol.GetValStringByKey("MarkID");
                string docs = rol.GetValStringByKey("Docs");
                if (markID.Equals("None") == true)
                    return true;

                if (markID.Equals("ByStations") == true && BP.DA.DataType.IsHaveIt(docs, strStas) == true)
                    return true;
                if (markID.Equals("ByDepts") == true && BP.DA.DataType.IsHaveIt(docs, strDepps) == true)
                    return true;
                if (markID.Equals("ByEmps") == true && BP.DA.DataType.IsHaveIt(docs, "," + BP.Web.WebUser.No + ",") == true)
                    return true;
                if (markID.Equals("Adminer") == true && BP.Web.WebUser.No.Equals("admin") == true)
                    return true;
                if (markID.Equals("Admin2") == true && BP.Web.WebUser.IsAdmin == true)
                    return true;

                if (markID.Equals("SQL") == true)
                {
                    string sql = BP.WF.Glo.DealExp(docs, null, "");
                    if (DBAccess.RunSQLReturnValFloat(sql) > 0)
                        return true;
                }
            }
            if (num == 0) return true;
            return false;
        }


        /// <summary>
        /// 获取数据源实体列表显示的列及操作列方法
        /// </summary>
        /// <returns></returns>
        public string Search_MapAttrForDB()
        {
            DBList dblist = new DBList(this.FrmID);
            #region 查询显示的列
            MapAttrs mapattrs = new MapAttrs();
            mapattrs.Retrieve(MapAttrAttr.FK_MapData, this.FrmID, MapAttrAttr.Idx);

            //查询列表数据源显示的列
            if (DataType.IsNullOrEmpty(dblist.ExpList) == true)
                return "err@数据源实体的列表数据源不能为空，请联系设计人员，检查错误原因.";

            //查询结果集返回的字段列表
            DataTable listDT = null;
            string explist = dblist.ExpList;
            //替换
            if (dblist.DBType.Equals("local"))
            {

                if (explist.ToUpper().Contains("WHERE") == false)
                    explist += " WHERE 1=2";
                else
                    explist += " AND 1=2";
                if (DataType.IsNullOrEmpty(dblist.DBSrc) == true)
                    dblist.DBSrc = "local";
                explist = BP.WF.Glo.DealExp(explist, null);
                SFDBSrc dbSrc = new SFDBSrc(dblist.DBSrc);
                listDT = dbSrc.RunSQLReturnTable(explist);
            }


            DataRow row = null;
            DataTable dt = new DataTable("Attrs");
            dt.Columns.Add("KeyOfEn", typeof(string));
            dt.Columns.Add("Name", typeof(string));
            dt.Columns.Add("Width", typeof(int));
            dt.Columns.Add("UIContralType", typeof(int));
            dt.Columns.Add("LGType", typeof(int));
            dt.Columns.Add("MyDataType", typeof(int));
            dt.Columns.Add("UIBindKey", typeof(string));
            dt.Columns.Add("AtPara", typeof(string));
            if (listDT == null)
            {
                foreach (MapAttr attr in mapattrs)
                {
                    string searchVisable = attr.atPara.GetValStrByKey("SearchVisable");
                    if (searchVisable == "0")
                        continue;
                    if (DataType.IsNullOrEmpty(searchVisable) == true && attr.UIVisible == false)
                        continue;
                    row = dt.NewRow();
                    row["KeyOfEn"] = attr.KeyOfEn;
                    row["Name"] = attr.Name;
                    row["Width"] = attr.UIWidthInt;
                    row["UIContralType"] = attr.UIContralType;
                    row["LGType"] = attr.LGType;
                    row["MyDataType"] = attr.MyDataType;
                    row["UIBindKey"] = attr.UIBindKey;
                    row["AtPara"] = attr.GetValStringByKey("AtPara");
                    dt.Rows.Add(row);
                }
            }
            else
            {
                //设置标题、单据号位于开始位置
                foreach (DataColumn col in listDT.Columns)
                {
                    //获取key
                    string key = col.ColumnName;
                    if (DataType.IsNullOrEmpty(key) == true)
                        continue;
                    MapAttr attr = mapattrs.GetEntityByKey(this.FrmID + "_" + key) as MapAttr;
                    row = dt.NewRow();
                    if (attr == null)
                    {
                        row["KeyOfEn"] = key;
                        row["Name"] = key;
                        row["Width"] = 120;
                        row["UIContralType"] = UIContralType.TB;
                        row["LGType"] = FieldTypeS.Normal;
                        row["MyDataType"] = DataType.AppString;
                        row["UIBindKey"] = "";
                        row["AtPara"] = "";
                        dt.Rows.Add(row);
                        continue;
                    }
                    string searchVisable = attr.atPara.GetValStrByKey("SearchVisable");
                    if (searchVisable == "0")
                        continue;
                    if (DataType.IsNullOrEmpty(searchVisable) == true && attr.UIVisible == false)
                        continue;
                    row["KeyOfEn"] = attr.KeyOfEn;
                    row["Name"] = attr.Name;
                    row["Width"] = attr.UIWidthInt;
                    row["UIContralType"] = attr.UIContralType;
                    row["LGType"] = attr.LGType;
                    row["MyDataType"] = attr.MyDataType;
                    row["UIBindKey"] = attr.UIBindKey;
                    row["AtPara"] = attr.GetValStringByKey("AtPara");
                    dt.Rows.Add(row);
                }

            }

            #endregion 查询显示的列
            DataSet ds = new DataSet();
            ds.Tables.Add(dt);
            //增加枚举
            MapData mapData = new MapData(this.FrmID);
            ds.Tables.Add(mapData.SysEnums.ToDataTableField("Sys_Enum"));
            #region 把外键表加入 DataSet
            DataTable ddlTable = new DataTable();
            ddlTable.Columns.Add("No");

            foreach (MapAttr attr in mapattrs)
            {
                //为空、枚举值就continue.
                if (DataType.IsNullOrEmpty(attr.UIBindKey) == true || attr.LGType == FieldTypeS.Enum)
                    continue;
                DataTable mydt = BP.Pub.PubClass.GetDataTableByUIBineKey(attr.UIBindKey);
                if (mydt == null)
                {
                    DataRow ddldr = ddlTable.NewRow();
                    ddldr["No"] = attr.UIBindKey;
                    ddlTable.Rows.Add(ddldr);
                }
                else
                {
                    ds.Tables.Add(mydt);
                }
            }
            ddlTable.TableName = "UIBindKey";
            ds.Tables.Add(ddlTable);
            #endregion End把外键表加入DataSet

            //查询一行数据的操作
            Methods methods = new Methods();
            methods.Retrieve(MethodAttr.FrmID, this.FrmID, MethodAttr.IsList, 1, MethodAttr.Idx);

            ds.Tables.Add(methods.ToDataTableField("Frm_Method"));

            return BP.Tools.Json.ToJson(ds);
        }

        public string SearchDB_UrlSearchData(string urlExt, string postData)
        {
            urlExt = BP.WF.Glo.DealExp(urlExt, null);
            if (urlExt.Contains("http") == false)
            {
                /*如果没有绝对路径 */
                if (BP.Difference.SystemConfig.isBSsystem)
                {
                    /*在cs模式下自动获取*/
                    string host = HttpContextHelper.RequestUrlHost;//BP.Sys.Base.Glo.Request.Url.Host;
                    if (urlExt.Contains("@AppPath"))
                        urlExt = urlExt.Replace("@AppPath", "http://" + host + HttpContextHelper.RequestApplicationPath);//BP.Sys.Base.Glo.Request.ApplicationPath
                    else
                        urlExt = "http://" + HttpContextHelper.RequestUrlAuthority + urlExt;
                }

                if (BP.Difference.SystemConfig.isBSsystem == false)
                {
                    /*在cs模式下它的baseurl 从web.config中获取.*/
                    string cfgBaseUrl = BP.Difference.SystemConfig.AppSettings["HostURL"];
                    if (DataType.IsNullOrEmpty(cfgBaseUrl))
                    {
                        string err = "调用url失败:没有在web.config中配置BaseUrl,导致url事件不能被执行.";
                        BP.DA.Log.DebugWriteError(err);
                        throw new Exception(err);
                    }
                    urlExt = cfgBaseUrl + urlExt;
                }
            }

            string json = BP.Tools.PubGlo.HttpPostConnect(urlExt, postData, "POST", true);
            return json;
        }
        private int times = 0;
        public string Search_Init()
        {
            DataSet ds = new DataSet();
            DataTable dt = null;
            MapData md = new MapData(this.FrmID);
            try
            {
                #region 查询语句

                //取出来查询条件.
                UserRegedit ur = new UserRegedit(WebUser.No, this.FrmID + "_SearchAttrs");

                Entities rpts = null;

                if (md.EntityType == EntityType.FrmEntityNoName)
                    rpts = new GEEntityNoNames(this.FrmID);
                else
                    rpts = new GEEntitys(this.FrmID);

                Attrs attrs = rpts.GetNewEntity.EnMap.Attrs;

                QueryObject qo = new QueryObject(rpts);
                bool isFirst = true; //是否第一次拼接SQL

                #region 关键字字段.
                string keyWord = ur.SearchKey;

                if (DataType.IsNullOrEmpty(keyWord) == false && keyWord.Length >= 1)
                {
                    Attr attrPK = new Attr();
                    foreach (Attr attr in attrs)
                    {
                        if (attr.ItIsPK)
                        {
                            attrPK = attr;
                            break;
                        }
                    }
                    int i = 0;
                    string enumKey = ","; //求出枚举值外键.
                    foreach (Attr attr in attrs)
                    {
                        switch (attr.MyFieldType)
                        {
                            case FieldType.Enum:
                                enumKey = "," + attr.Key + "Text,";
                                break;
                            case FieldType.FK:
                                continue;
                            default:
                                break;
                        }

                        if (attr.MyDataType != DataType.AppString)
                            continue;

                        //排除枚举值关联refText.
                        if (attr.MyFieldType == FieldType.RefText)
                        {
                            if (enumKey.Contains("," + attr.Key + ",") == true)
                                continue;
                        }

                        if (attr.Key.Equals("FK_Dept") || attr.Key.Equals("DeptNo"))
                            continue;

                        i++;
                        if (i == 1)
                        {
                            isFirst = false;
                            /* 第一次进来。 */
                            qo.addLeftBracket();
                            if (BP.Difference.SystemConfig.AppCenterDBVarStr == "@" || BP.Difference.SystemConfig.AppCenterDBVarStr == "?")
                                qo.AddWhere(attr.Key, " LIKE ", BP.Difference.SystemConfig.AppCenterDBType == DBType.MySQL ? (" CONCAT('%'," + BP.Difference.SystemConfig.AppCenterDBVarStr + "SKey,'%')") : (" '%'+" + BP.Difference.SystemConfig.AppCenterDBVarStr + "SKey+'%'"));
                            else
                                qo.AddWhere(attr.Key, " LIKE ", " '%'||" + BP.Difference.SystemConfig.AppCenterDBVarStr + "SKey||'%'");
                            continue;
                        }
                        qo.addOr();

                        if (BP.Difference.SystemConfig.AppCenterDBVarStr == "@" || BP.Difference.SystemConfig.AppCenterDBVarStr == "?")
                            qo.AddWhere(attr.Key, " LIKE ", BP.Difference.SystemConfig.AppCenterDBType == DBType.MySQL ? ("CONCAT('%'," + BP.Difference.SystemConfig.AppCenterDBVarStr + "SKey,'%')") : ("'%'+" + BP.Difference.SystemConfig.AppCenterDBVarStr + "SKey+'%'"));
                        else
                            qo.AddWhere(attr.Key, " LIKE ", "'%'||" + BP.Difference.SystemConfig.AppCenterDBVarStr + "SKey||'%'");

                    }
                    qo.MyParas.Add("SKey", keyWord);
                    qo.addRightBracket();
                }
                else if (DataType.IsNullOrEmpty(md.GetParaString("StringSearchKeys")) == false)
                {
                    string field = "";//字段名
                    string fieldValue = "";//字段值
                    int idx = 0;

                    //获取查询的字段
                    string[] searchFields = md.GetParaString("StringSearchKeys").Split('*');
                    foreach (String str in searchFields)
                    {
                        if (DataType.IsNullOrEmpty(str) == true)
                            continue;

                        //字段名
                        string[] items = str.Split(',');
                        if (items.Length == 2 && DataType.IsNullOrEmpty(items[0]) == true)
                            continue;
                        field = items[0];
                        //字段名对应的字段值
                        fieldValue = ur.GetParaString(field);
                        if (DataType.IsNullOrEmpty(fieldValue) == true)
                            continue;
                        idx++;
                        if (idx == 1)
                        {
                            isFirst = false;
                            /* 第一次进来。 */
                            qo.addLeftBracket();
                            if (BP.Difference.SystemConfig.AppCenterDBVarStr == "@" || BP.Difference.SystemConfig.AppCenterDBVarStr == "?")
                                qo.AddWhere(field, " LIKE ", BP.Difference.SystemConfig.AppCenterDBType == DBType.MySQL ? (" CONCAT('%'," + BP.Difference.SystemConfig.AppCenterDBVarStr + field + ",'%')") : (" '%'+" + BP.Difference.SystemConfig.AppCenterDBVarStr + field + "+'%'"));
                            else
                                qo.AddWhere(field, " LIKE ", " '%'||" + BP.Difference.SystemConfig.AppCenterDBVarStr + field + "||'%'");
                            qo.MyParas.Add(field, fieldValue);
                            continue;
                        }
                        qo.addAnd();

                        if (BP.Difference.SystemConfig.AppCenterDBVarStr == "@" || BP.Difference.SystemConfig.AppCenterDBVarStr == "?")
                            qo.AddWhere(field, " LIKE ", BP.Difference.SystemConfig.AppCenterDBType == DBType.MySQL ? ("CONCAT('%'," + BP.Difference.SystemConfig.AppCenterDBVarStr + field + ",'%')") : ("'%'+" + BP.Difference.SystemConfig.AppCenterDBVarStr + field + "+'%'"));
                        else
                            qo.AddWhere(field, " LIKE ", "'%'||" + BP.Difference.SystemConfig.AppCenterDBVarStr + field + "||'%'");
                        qo.MyParas.Add(field, fieldValue);


                    }
                    if (idx != 0)
                        qo.addRightBracket();
                }

                #endregion 关键字段查询

                #region 时间段的查询
                if (md.GetParaInt("DTSearchWay") != (int)DTSearchWay.None && DataType.IsNullOrEmpty(ur.DTFrom) == false)
                {
                    string dtFrom = ur.DTFrom; // this.GetTBByID("TB_S_From").Text.Trim().Replace("/", "-");
                    string dtTo = ur.DTTo; // this.GetTBByID("TB_S_To").Text.Trim().Replace("/", "-");

                    //按日期查询
                    if (md.GetParaInt("DTSearchWay") == (int)DTSearchWay.ByDate)
                    {
                        if (isFirst == false)
                            qo.addAnd();
                        else
                            isFirst = false;
                        qo.addLeftBracket();
                        dtTo += " 23:59:59";
                        qo.SQL = md.GetParaString("DTSearchKey") + " >= '" + dtFrom + "'";
                        qo.addAnd();
                        qo.SQL = md.GetParaString("DTSearchKey") + " <= '" + dtTo + "'";
                        qo.addRightBracket();
                    }

                    if (md.GetParaInt("DTSearchWay") == (int)DTSearchWay.ByDateTime)
                    {
                        //取前一天的24：00
                        if (dtFrom.Trim().Length == 10) //2017-09-30
                            dtFrom += " 00:00:00";
                        if (dtFrom.Trim().Length == 16) //2017-09-30 00:00
                            dtFrom += ":00";

                        dtFrom = DateTime.Parse(dtFrom).AddDays(-1).ToString("yyyy-MM-dd") + " 24:00";

                        if (dtTo.Trim().Length < 11 || dtTo.Trim().IndexOf(' ') == -1)
                            dtTo += " 24:00";

                        if (isFirst == false)
                            qo.addAnd();
                        else
                            isFirst = false;
                        qo.addLeftBracket();
                        qo.SQL = md.GetParaString("DTSearchKey") + " >= '" + dtFrom + "'";
                        qo.addAnd();
                        qo.SQL = md.GetParaString("DTSearchKey") + " <= '" + dtTo + "'";
                        qo.addRightBracket();
                    }
                }
                #endregion 时间段的查询

                #region 外键或者枚举的查询
                //获得关键字.
                AtPara ap = new AtPara(ur.Vals);
                Attr ddattr = null;
                foreach (string str in ap.HisHT.Keys)
                {
                    string val = ap.GetValStrByKey(str);
                    if (val.Equals("all") || val.Equals("null") || val.Equals(""))
                        continue;
                    if (isFirst == false)
                        qo.addAnd();
                    else
                        isFirst = false;

                    qo.addLeftBracket();
                    ddattr = attrs.GetAttrByKeyOfEn(str);
                    if (val.IndexOf(",") != -1)
                    {
                        if (ddattr.ItIsNum == true)
                        {
                            qo.AddWhere(str, "IN", "(" + val + ")");
                            qo.addRightBracket();
                            continue;
                        }
                        val = "('" + val.Replace(",", "','") + "')";
                        qo.AddWhere(str, "IN", val);
                        qo.addRightBracket();
                        continue;
                    }
                    if (BP.Difference.SystemConfig.AppCenterDBFieldIsParaDBType == true)
                    {
                        object typeVal = BP.Sys.Base.Glo.GenerRealType(attrs, str, ap.GetValStrByKey(str));
                        qo.AddWhere(str, typeVal);

                    }
                    else
                    {
                        qo.AddWhere(str, ap.GetValStrByKey(str));
                    }

                    qo.addRightBracket();
                }
                #endregion 外键或者枚举的查询

                #region 设置隐藏字段的过滤查询

                FrmBill frmBill = new FrmBill(this.FrmID);
                string hidenField = frmBill.GetValStringByKey("HidenField");

                if (frmBill.GetParaInt("HidenWay") == 1)
                {
                    if (DataType.IsNullOrEmpty(hidenField) == false)
                    {
                        hidenField = hidenField.Replace("_WebUser", "@WebUser");

                        hidenField = hidenField.Replace("@WebUser.No", WebUser.No);
                        hidenField = hidenField.Replace("@WebUser.Name", WebUser.Name);
                        hidenField = hidenField.Replace("@WebUser.FK_DeptName", WebUser.DeptName);
                        hidenField = hidenField.Replace("@WebUser.DeptName", WebUser.DeptName);

                        hidenField = hidenField.Replace("@WebUser.FK_Dept", WebUser.DeptNo);
                        hidenField = hidenField.Replace("@WebUser.DeptNo", WebUser.DeptNo);

                        hidenField = hidenField.Replace("@WebUser.OrgNo", WebUser.OrgNo);

                        hidenField = Regex.Replace(hidenField, "~", "'");



                        if (hidenField.IndexOf("_") != -1)
                            return "err@隐藏条件" + hidenField + "还有未替换的_符号";

                        if (isFirst == false)
                            qo.addAnd();
                        else
                            isFirst = false;
                        qo.addSQL(hidenField);
                    }
                }
                #endregion 设置隐藏字段的查询

                #endregion 查询语句

                if (isFirst == false)
                {
                    qo.addAnd();
                }

                if (md.EntityType == EntityType.FrmEntityNoName)
                {
                    qo.AddWhere("EntityState", ">", 0);
                }
                else
                {
                    qo.AddWhere("BillState", ">", 0);
                }

                isFirst = false;

                //增加表单字段的查询
                foreach (string key in HttpContextHelper.RequestParamKeys)
                {
                    if (string.IsNullOrEmpty(key) || key.Equals("T") == true
                        || key.Equals("t") == true || key.Equals("HttpHandlerName") == true
                        || key.Equals("DoMethod") == true || key.Equals("DoType") == true)
                        continue;
                    if (attrs.Contains(key) == true)
                    {
                        if (isFirst == false)
                            qo.addAnd();
                        qo.AddWhere(key, HttpContextHelper.RequestParams(key));
                        continue;
                    }

                }



                //获取配置信息
                string fieldSet = frmBill.FieldSet;
                string oper = "";
                if (DataType.IsNullOrEmpty(fieldSet) == false)
                {
                    string ptable = rpts.GetNewEntity.EnMap.PhysicsTable;
                    dt = new DataTable("Search_FieldSet");
                    dt.Columns.Add("Field");
                    dt.Columns.Add("Type");
                    dt.Columns.Add("Value");
                    DataRow dr;
                    string[] strs = fieldSet.Split('@');
                    foreach (string str in strs)
                    {
                        if (DataType.IsNullOrEmpty(str) == true)
                            continue;
                        string[] item = str.Split('=');
                        if (item.Length == 2)
                        {
                            if (item[1].Contains(",") == true)
                            {
                                string[] ss = item[1].Split(',');
                                foreach (string s in ss)
                                {
                                    dr = dt.NewRow();
                                    dr["Field"] = attrs.GetAttrByKey(s).Desc;
                                    dr["Type"] = item[0];
                                    dt.Rows.Add(dr);

                                    oper += item[0] + "(" + ptable + "." + s + ")" + ",";
                                }
                            }
                            else
                            {
                                dr = dt.NewRow();
                                dr["Field"] = attrs.GetAttrByKey(item[1]).Desc;
                                dr["Type"] = item[0];
                                dt.Rows.Add(dr);

                                oper += item[0] + "(" + ptable + "." + item[1] + ")" + ",";
                            }
                        }
                    }
                    oper = oper.Substring(0, oper.Length - 1);
                    DataTable dd = qo.GetSumOrAvg(oper);

                    for (int i = 0; i < dt.Rows.Count; i++)
                    {
                        DataRow ddr = dt.Rows[i];
                        ddr["Value"] = dd.Rows[0][i];
                    }
                    ds.Tables.Add(dt);
                }

                #region 处理数据权限. - 查询范围.
                DataTable dtDBRole = DBAccess.RunSQLReturnTable("SELECT MarkID,Docs FROM Frm_DBRole WHERE FrmID='" + this.FrmID + "' AND DBRole='DBList' AND IsEnable=1 ");
                //首先判断是否有全部的权限。
                if (this.CheckDB(dtDBRole, "All") == true)
                {
                    //不处理.
                }
                else if (this.CheckDB(dtDBRole, "NOrg") == true)
                {
                    /*是否可以查看本组织以及下级组织的数据.*/
                    //最后按照人员的权限判断.
                    if (isFirst == false)
                        qo.addAnd();
                    else
                        isFirst = false;

                    //查看当前人员所有的下级组织.
                    string sql = "SELECT No FROM Port_Org WHERE TreeNos LIKE '%," + WebUser.OrgNo + ",%'";
                    qo.AddWhereInSQL("OrgNo", sql);
                }
                else if (this.CheckDB(dtDBRole, "POrg") == true)
                {
                    /*是否可以查看本组织以及下级组织的数据.*/
                    //最后按照人员的权限判断.
                    if (isFirst == false)
                        qo.addAnd();
                    else
                        isFirst = false;

                    string sql = "SELECT No FROM Port_Org WHERE No='" + WebUser.OrgNo + "' OR ParentNo='" + WebUser.OrgNo + "'";
                    qo.AddWhereInSQL("OrgNo", sql);
                }
                else if (this.CheckDB(dtDBRole, "OrgOnly") == true)
                {
                    /*是否可以查看本组织以及下级组织的数据.*/
                    //最后按照人员的权限判断.
                    if (isFirst == false)
                        qo.addAnd();
                    else
                        isFirst = false;
                    qo.AddWhereInSQL("OrgNo", "=", WebUser.OrgNo);
                }
                else if (this.CheckDB(dtDBRole, "Exp") == true)
                {
                    /*判断是否有表达式，有表达式优先*/
                    string exp = DBAccess.RunSQLReturnString("SELECT Docs FROM Frm_DBRole WHERE FrmID='" + this.FrmID + "' AND DBRole='DBList' AND MarkID='ByExp' AND IsEnable=1 ");
                    exp = BP.WF.Glo.DealExp(exp, null);
                    if (isFirst == false)
                        qo.addAnd();
                    else
                        isFirst = false;
                    qo.addSQL(exp);
                }
                else
                {
                    //检查是否有部门的权限?
                    if (this.CheckDB(dtDBRole, "Dept") == true)
                    {
                        if (isFirst == false)
                            qo.addAnd();
                        else
                            isFirst = false;

                        qo.AddWhere("DeptNo", "=", BP.Web.WebUser.DeptNo);
                    }
                    else
                    {
                        //最后按照人员的权限判断.
                        if (isFirst == false)
                            qo.addAnd();
                        else
                            isFirst = false;
                        if (md.EntityType == EntityType.FrmEntityNoName)
                            qo.AddWhere("RecNo", "=", BP.Web.WebUser.No);
                        else
                            qo.AddWhere("Starter", "=", BP.Web.WebUser.No);
                    }
                }
                #endregion 处理数据权限.

                //获得行数.
                ur.SetPara("RecCount", qo.GetCount());
                ur.Save();

                string pkVal = "OID";
                if (md.EntityType == EntityType.FrmEntityNoName)
                    pkVal = "No";

                if (DataType.IsNullOrEmpty(ur.OrderBy) == false && DataType.IsNullOrEmpty(ur.OrderWay) == false)
                    qo.DoQuery(pkVal, this.PageSize, this.PageIdx, ur.OrderBy, ur.OrderWay);
                else
                    qo.DoQuery(pkVal, this.PageSize, this.PageIdx);

                DataTable mydt = rpts.ToDataTableField();
                mydt.TableName = "DT";

                ds.Tables.Add(mydt); //把数据加入里面.
            }
            catch (Exception ex)
            {
                if (times == 0)
                {
                    times++;
                    if (md.EntityType == EntityType.FrmBill)
                    {
                        FrmBill fb = new FrmBill(this.EnsName);
                        fb.CheckEnityTypeAttrsFor_Bill();
                    }
                    if (md.EntityType == EntityType.FrmEntityNoName)
                    {
                        FrmEntityNoName fb = new FrmEntityNoName(this.EnsName);
                        fb.CheckEnityTypeAttrsFor_EntityNoName();
                    }
                    GEEntity en = new GEEntity(this.FrmID);
                    en.CheckPhysicsTable();
                    return Search_Init();
                }
                return "err@" + ex.Message;
            }


            return BP.Tools.Json.ToJson(ds);
        }
        /// <summary>
        /// 权限控制.
        /// </summary>
        /// <param name="dt"></param>
        /// <param name="dbScop">All=全部权限,Dept=本部门的权限,Self=自己的权限.</param>
        /// <returns></returns>
        private bool CheckDB(DataTable dt, string dbScop)
        {
            if (dt.Rows.Count == 0)
                return true;

            #region 是否是本组织或者下级组织.
            if (dbScop.Equals("POrg") == true || dbScop.Equals("OrgOnly") == true)
            {
                foreach (DataRow dr in dt.Rows)
                {
                    string markID = dr[0].ToString();
                    string strs = "," + dr[1].ToString() + ",";
                    string sql = "";
                    //如果是部门.
                    if (markID.Equals("POrg") == true)
                        return true;
                    if (markID.Equals("OrgOnly") == true || markID.Equals("POrg") == true)
                        return true;
                }
            }
            #endregion 是否是本组织或者下级组织.

            #region .可以被直线父级组织所看到
            if (dbScop.Equals("NOrg") == true)
            {
                foreach (DataRow dr in dt.Rows)
                {
                    string markID = dr[0].ToString();
                    string strs = "," + dr[1].ToString() + ",";
                    string sql = "";
                    //如果是部门.
                    if (markID.Equals("NOrg") == true)
                        return true;
                }
            }
            #endregion 可以被直线父级组织所看到.


            #region 判断全部范围.
            if (dbScop.Equals("All") == true)
            {
                foreach (DataRow dr in dt.Rows)
                {
                    string markID = dr[0].ToString();
                    string strs = "," + dr[1].ToString() + ",";
                    string sql = "";
                    //如果是部门.
                    if (markID.Equals("ByDepts") == true)
                    {
                        sql = "SELECT FK_Dept FROM Port_DeptEmp WHERE FK_Emp='" + WebUser.No + "'";
                        sql += " UNION ";
                        sql += "SELECT FK_Dept FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.No + "'";

                        DataTable mydt = DBAccess.RunSQLReturnTable(sql);
                        foreach (DataRow mydr in mydt.Rows)
                        {
                            string myNo = mydr[0].ToString();
                            if (strs.Contains("," + myNo + ",") == true)
                                return true;
                        }
                    }

                    //如果标记是岗位.
                    if (markID.Equals("ByStations") == true)
                    {
                        sql = "SELECT FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.No + "'";
                        DataTable mydt = DBAccess.RunSQLReturnTable(sql);
                        foreach (DataRow mydr in mydt.Rows)
                        {
                            string myNo = mydr[0].ToString();
                            if (strs.Contains("," + myNo + ",") == true)
                                return true;
                        }
                    }

                    //指定的人是否可以查看全部数据?.
                    if (markID.Equals("ByEmps") == true)
                    {
                        if (strs.Contains("," + WebUser.No + ",") == true)
                            return true;
                    }

                    //指定的人是否可以查看全部数据?.
                    if (markID.Equals("Adminer") == true && WebUser.No.Equals("admin") == true)
                    {
                        return true;
                    }
                    //指定的人是否可以查看全部数据?.
                    if (markID.Equals("Admin2") == true && WebUser.IsAdmin)
                    {
                        return true;
                    }
                }
            }
            #endregion 判断全部范围.

            #region 判断部门范围.
            if (dbScop.Equals("Dept") == true)
            {
                foreach (DataRow dr in dt.Rows)
                {
                    string markID = dr[0].ToString();
                    string sql = "";
                    //如果是部门, 判断他是否是领导?
                    if (markID.Equals("DeptLeader") == true)
                    {
                        sql = "SELECT No FROM Port_Dept WHERE Leader='" + WebUser.No + "' AND No='" + BP.Web.WebUser.DeptNo + "'";
                        DataTable mydt = DBAccess.RunSQLReturnTable(sql);
                        if (mydt.Rows.Count == 1)
                            return true;
                    }

                    //是否可以查看同部门的数据..
                    if (markID.Equals("DeptOnly") == true)
                        return true;
                }
            }
            #endregion 判断全部范围.

            #region 判断Exp 
            if (dbScop.Equals("Exp") == true)
            {
                foreach (DataRow dr in dt.Rows)
                {
                    string markID = dr[0].ToString();
                    //如果是部门, 判断他是否是领导?
                    if (markID.Equals("ByExp") == true)
                        return true;
                }
                return false;
            }
            #endregion 判断Exp 



            return false;
        }

        public string SearchDB_Init()
        {

            DataSet ds = new DataSet();
            #region 查询语句
            DBList md = new DBList(this.FrmID);
            if (DataType.IsNullOrEmpty(md.ExpList) == true)
                return "err@列表数据源和的查询不能为空";

            string expList = md.ExpList;
            expList = BP.WF.Glo.DealExp(expList, null);
            //取出来查询条件.
            UserRegedit ur = new UserRegedit(WebUser.No, this.FrmID + "_SearchAttrs");

            GEEntitys rpts = new GEEntitys(this.FrmID);

            Attrs attrs = rpts.GetNewEntity.EnMap.Attrs;
            string systemKeys = "BillState,RDT,Starter,StarterName,OrgNo,AtPara,";//创建表单时的系统字段

            //获取查询条件
            DataTable whereDT = new DataTable();
            whereDT.Columns.Add("Key");
            whereDT.Columns.Add("Oper");
            whereDT.Columns.Add("Value");
            whereDT.Columns.Add("Type");
            DataRow dr;
            #region 关键字字段.
            string keyWord = ur.SearchKey;
            Hashtable ht = new Hashtable();

            if (md.GetParaBoolen("IsSearchKey") == true)
            {
                if (DataType.IsNullOrEmpty(keyWord) == false && keyWord.Length >= 1)
                {
                    ht.Add("SearchKey", keyWord);
                }
                else
                {
                    Attr attrPK = new Attr();
                    foreach (Attr attr in attrs)
                    {
                        if (attr.ItIsPK)
                        {
                            attrPK = attr;
                            break;
                        }
                    }
                    int i = 0;
                    string enumKey = ","; //求出枚举值外键.
                    foreach (Attr attr in attrs)
                    {
                        if (systemKeys.IndexOf(attr.Key + ",") != -1)
                            continue;
                        switch (attr.MyFieldType)
                        {
                            case FieldType.Enum:
                                enumKey = "," + attr.Key + "Text,";
                                break;
                            case FieldType.FK:
                                continue;
                            default:
                                break;
                        }

                        if (attr.MyDataType != DataType.AppString)
                            continue;

                        //排除枚举值关联refText.
                        if (attr.MyFieldType == FieldType.RefText)
                        {
                            if (enumKey.Contains("," + attr.Key + ",") == true)
                                continue;
                        }

                        if (attr.Key.Equals("FK_Dept") || attr.Key.Equals("DeptNo"))
                            continue;
                        i++;
                        dr = whereDT.NewRow();
                        dr["Key"] = attr.Key;
                        dr["Oper"] = "like";
                        dr["Value"] = keyWord;
                        dr["Type"] = "SearchKey";
                        whereDT.Rows.Add(dr);

                    }
                    ht.Add("SearchKey", keyWord);

                }

            }
            else if (DataType.IsNullOrEmpty(md.GetParaString("StringSearchKeys")) == false)
            {
                string field = "";//字段名
                string fieldValue = "";//字段值

                //获取查询的字段
                string[] searchFields = md.GetParaString("StringSearchKeys").Split('*');
                foreach (String str in searchFields)
                {
                    if (DataType.IsNullOrEmpty(str) == true)
                        continue;

                    //字段名
                    string[] items = str.Split(',');
                    if (items.Length == 2 && DataType.IsNullOrEmpty(items[0]) == true)
                        continue;
                    field = items[0];
                    //字段名对应的字段值
                    fieldValue = ur.GetParaString(field);
                    if (DataType.IsNullOrEmpty(fieldValue) == true)
                    {
                        ht.Add(field, "");
                        continue;
                    }

                    dr = whereDT.NewRow();
                    dr["Key"] = field;
                    dr["Oper"] = "like";
                    dr["Value"] = fieldValue;
                    dr["Type"] = "StringKey";
                    whereDT.Rows.Add(dr);
                    ht.Add(field, fieldValue);
                }

            }

            #endregion 关键字段查询

            #region 时间段的查询
            if (md.GetParaInt("DTSearchWay") != (int)DTSearchWay.None)
            {
                if (DataType.IsNullOrEmpty(ur.DTFrom) == true)
                {
                    ht.Add("DTFrom", ur.DTFrom);
                    ht.Add("DTTo", ur.DTTo);

                }
                else
                {
                    string dtFrom = ur.DTFrom; // this.GetTBByID("TB_S_From").Text.Trim().Replace("/", "-");
                    string dtTo = ur.DTTo; // this.GetTBByID("TB_S_To").Text.Trim().Replace("/", "-");

                    //按日期查询
                    if (md.GetParaInt("DTSearchWay") == (int)DTSearchWay.ByDate)
                    {
                        dr = whereDT.NewRow();
                        dr["Key"] = md.GetParaString("DTSearchKey");
                        dr["Oper"] = ">=";
                        dr["Value"] = dtFrom;
                        dr["Type"] = "Date";
                        whereDT.Rows.Add(dr);
                        dtTo += " 23:59:59";
                        dr = whereDT.NewRow();
                        dr["Key"] = md.GetParaString("DTSearchKey");
                        dr["Oper"] = "<=";
                        dr["Value"] = dtTo;
                        dr["Type"] = "Date";
                        whereDT.Rows.Add(dr);
                        ht.Add("DTFrom", dtFrom);
                        ht.Add("DTTo", dtTo);
                    }

                    if (md.GetParaInt("DTSearchWay") == (int)DTSearchWay.ByDateTime)
                    {
                        //取前一天的24：00
                        if (dtFrom.Trim().Length == 10) //2017-09-30
                            dtFrom += " 00:00:00";
                        if (dtFrom.Trim().Length == 16) //2017-09-30 00:00
                            dtFrom += ":00";

                        dtFrom = DateTime.Parse(dtFrom).AddDays(-1).ToString("yyyy-MM-dd") + " 24:00";

                        if (dtTo.Trim().Length < 11 || dtTo.Trim().IndexOf(' ') == -1)
                            dtTo += " 24:00";

                        dr = whereDT.NewRow();
                        dr["Key"] = md.GetParaString("DTSearchKey");
                        dr["Oper"] = ">=";
                        dr["Value"] = dtFrom;
                        dr["Type"] = "Date";
                        whereDT.Rows.Add(dr);
                        dr = whereDT.NewRow();
                        dr["Key"] = md.GetParaString("DTSearchKey");
                        dr["Oper"] = "<=";
                        dr["Value"] = dtTo;
                        dr["Type"] = "Date";
                        whereDT.Rows.Add(dr);
                        ht.Add("DTFrom", dtFrom);
                        ht.Add("DTTo", dtTo);
                    }
                }

            }
            #endregion 时间段的查询

            #region 外键或者枚举的查询

            //获得关键字.
            AtPara ap = new AtPara(ur.Vals);
            Attr ddattr = null;
            foreach (string str in ap.HisHT.Keys)
            {
                string val = ap.GetValStrByKey(str);
                if (val.Equals("all"))
                {
                    ht.Add(str, "");
                    continue;
                }

                dr = whereDT.NewRow();
                dr["Key"] = str;
                dr["Oper"] = "=";
                if (val.IndexOf(",") != -1)
                    dr["Oper"] = "IN";

                dr["Value"] = val;
                dr["Type"] = "Select";
                whereDT.Rows.Add(dr);
                ht.Add(str, ap.GetValStrByKey(str));
            }
            #endregion 外键或者枚举的查询


            //增加表单字段的查询
            foreach (string key in HttpContextHelper.RequestParamKeys)
            {
                if (string.IsNullOrEmpty(key) || key.Equals("T") == true
                    || key.Equals("t") == true || key.Equals("HttpHandlerName") == true
                    || key.Equals("DoMethod") == true || key.Equals("DoType") == true)
                    continue;
                if (attrs.Contains(key) == true)
                {
                    dr = whereDT.NewRow();
                    dr["Key"] = key;
                    dr["Oper"] = "=";
                    dr["Value"] = HttpContextHelper.RequestParams(key);
                    dr["Type"] = "Normal";
                    whereDT.Rows.Add(dr);
                    ht.Add(key, HttpContextHelper.RequestParams(key));
                    continue;
                }

            }
            #endregion
            SFDBSrc dbsrc = new SFDBSrc();
            dbsrc.No = md.DBSrc;
            if (dbsrc.RetrieveFromDBSources() == 1)
            {
                string dbSrcType = dbsrc.DBSrcType;
                if (dbSrcType.Equals("WebApi") == true)
                    md.DBType = 1;
                if (dbSrcType.Equals("Dubbo") == false && dbSrcType.Equals("WebApi") == false)
                {
                    if (expList.ToUpper().Contains("SELECT") == false || expList.Trim().Contains(" ") == false)
                        md.DBType = 2;
                }
            }
            #region 数据源SQL
            if (md.DBType == 0)
            {

                string mainTable = "A.";
                string mainTablePK = md.MainTablePK;

                string whereSQL = "";
                bool isFirstSearchKey = true;
                bool isFirstDateKey = true;

                foreach (DataRow dataRow in whereDT.Rows)
                {
                    string key = dataRow["Key"].ToString();

                    if (expList.IndexOf("@" + Key) != -1)
                    {
                        expList = expList.Replace("@" + Key, dataRow["Value"].ToString());
                        continue;
                    }
                    string type = dataRow["Type"].ToString();
                    if (type.Equals("SearchKey") == true)
                    {
                        if (isFirstSearchKey)
                        {
                            isFirstSearchKey = false;
                            whereSQL += " AND (" + mainTable + key + " like '%" + dataRow["Value"].ToString() + "%' ";
                        }
                        else
                            whereSQL += " OR " + mainTable + key + " like '%" + dataRow["Value"].ToString() + "%' ";
                    }
                    if (isFirstSearchKey == false && type.Equals("SearchKey") == false)
                    {
                        whereSQL += ")";
                        isFirstSearchKey = true;
                    }

                    if (type.Equals("StringKey") == true)
                        whereSQL += " AND " + mainTable + key + " like '%" + dataRow["Value"].ToString() + "%' ";
                    //时间解析
                    if (type.Equals("Date") == true)
                    {

                        if (isFirstDateKey == true)
                        {
                            isFirstDateKey = false;
                            whereSQL += " AND (" + mainTable + key + " " + dataRow["Oper"].ToString() + " '" + dataRow["Value"].ToString() + "' ";
                            continue;
                        }
                        if (isFirstDateKey == false)
                            whereSQL += " AND " + mainTable + key + " " + dataRow["Oper"].ToString() + " '" + dataRow["Value"].ToString() + "')";
                    }
                    if (type.Equals("Select") == true || type.Equals("Normal") == true)
                    {
                        string oper = dataRow["Oper"].ToString();
                        string val = dataRow["Value"].ToString();
                        if (oper.Equals("IN") == true)
                        {
                            ddattr = attrs.GetAttrByKeyOfEn(key);
                            if (ddattr != null)
                            {
                                if (ddattr.ItIsNum)
                                    whereSQL += " AND " + mainTable + key + " " + oper + " (" + val + ") ";
                                else
                                {
                                    val = "('" + val.Replace(",", "','") + "')";
                                    whereSQL += " AND " + mainTable + key + " " + oper + val;
                                }
                            }

                        }
                        else
                            whereSQL += " AND " + mainTable + key + " " + oper + " '" + val + "'";

                    }
                }

                if (isFirstSearchKey == false)
                    whereSQL += ")";
                //expCount = expCount + whereSQL;
                //expList = expList + whereSQL;
                string hidenField = md.GetParaString("HidenField");

                if (DataType.IsNullOrEmpty(hidenField) == false)
                {
                    hidenField = hidenField.Replace("_WebUser.No", WebUser.No);
                    hidenField = hidenField.Replace("_WebUser.Name", WebUser.Name);
                    hidenField = hidenField.Replace("_WebUser.FK_DeptName", WebUser.DeptName);
                    hidenField = hidenField.Replace("_WebUser.DeptName", WebUser.DeptName);
                    hidenField = hidenField.Replace("_WebUser.FK_Dept", WebUser.DeptNo);
                    hidenField = hidenField.Replace("_WebUser.DeptNo", WebUser.DeptNo);
                    hidenField = hidenField.Replace("_WebUser.OrgNo", WebUser.OrgNo);
                    if (hidenField.IndexOf("@") != -1)
                        return "err@隐藏条件" + hidenField + "还有未替换的_符号";
                    whereSQL += " AND (" + hidenField + ")";
                }

                expList = "SELECT * From(" + expList + ") AS A WHERE 1=1 " + whereSQL;//查询列数的
                string expCount = "SELECT Count(*) From(" + expList + ") AS A WHERE 1=1 " + whereSQL;//查询总条数的
                string expPageSize = "SELECT A.OID  From(" + expList + ") AS A WHERE 1=1 " + whereSQL;//查询分页使用的SQL语句

                if (DataType.IsNullOrEmpty(md.DBSrc) == true)
                    md.DBSrc = "local";

                expCount = BP.WF.Glo.DealExp(expCount, null, null);
                expPageSize = BP.WF.Glo.DealExp(expPageSize, null, null);


                int count = dbsrc.RunSQLReturnInt(expCount, 0);


                dbsrc.DoQuery(rpts, expList, expPageSize, "OID", attrs, count, this.PageSize, this.PageIdx, ur.OrderBy);
                ur.SetPara("RecCount", count);
                ur.Save();
                DataTable dt = rpts.ToDataTableField("DT");
                ds.Tables.Add(dt); //把数据加入里面.

            }
            #endregion 数据源SQL
            #region URL请求数据
            if (md.DBType == 1)
            {
                ht.Add("PageSize", this.PageSize);
                ht.Add("PageIdx", this.PageIdx);
                // 请求的参数作为JSON字符串发送给列表URL
                string postData = BP.Tools.Json.ToJson(ht);
                if (DataType.IsNullOrEmpty(md.ExpList) == true)
                    return "err@根据URL请求数据的URL为空，请检查配置";
                string json = SearchDB_UrlSearchData(md.ExpList, postData);
                if (DataType.IsNullOrEmpty(json) == true)
                    return "err@根据URL请求数据列表数据为空";
                JsonData jd = JsonMapper.ToObject(json);
                string count = jd["count"].ToString();
                if (DataType.IsNullOrEmpty(count) == true)
                    ur.SetPara("RecCount", 0);
                else
                    ur.SetPara("RecCount", Int32.Parse(count));
                ur.Save();
                string data = jd["data"].ToJson();
                DataTable dt = new DataTable("DT");
                if (DataType.IsNullOrEmpty(data) == false)
                {
                    dt = BP.Tools.Json.ToDataTable(data);
                    dt.TableName = "DT";
                    ds.Tables.Add(dt); //把数据加入里面.
                }

            }
            #endregion URL请求数据

            #region 存储过程的查询
            if (md.DBType == 2)
            {
                string sql = md.ExpList;
                sql = sql.Replace("~", "'");
                Paras paras = new Paras();
                foreach (string key in ht.Keys)
                {
                    paras.Add(key, ht[key]);
                }
                string hidenField = md.GetParaString("HidenField");

                if (DataType.IsNullOrEmpty(hidenField) == false)
                {
                    hidenField = hidenField.Replace("_WebUser.No", WebUser.No);
                    hidenField = hidenField.Replace("_WebUser.Name", WebUser.Name);
                    hidenField = hidenField.Replace("_WebUser.FK_DeptName", WebUser.DeptName);
                    hidenField = hidenField.Replace("_WebUser.DeptName", WebUser.DeptName);

                    hidenField = hidenField.Replace("_WebUser.FK_Dept", WebUser.DeptNo);
                    hidenField = hidenField.Replace("_WebUser.DeptNo", WebUser.DeptNo);
                    hidenField = hidenField.Replace("_WebUser.OrgNo", WebUser.OrgNo);
                    if (hidenField.IndexOf("@") != -1)
                        return "err@隐藏条件" + hidenField + "还有未替换的_符号";
                    string[] strs = hidenField.Split(',');
                    foreach (string str in strs)
                    {
                        if (DataType.IsNullOrEmpty(str) == true)
                            continue;
                        string[] strVal = str.Split('=');
                        if (strVal.Length == 1)
                            paras.Add(strVal[0], "");
                        else
                            paras.Add(strVal[0], strVal[1]);
                    }

                }
                DataTable dt = DBAccess._delRunProcReturnTable(sql, paras);
                dt.TableName = "DT";
                ds.Tables.Add(dt); //把数据加入里面.
            }
            #endregion 存储过程的查询

            return BP.Tools.Json.ToJson(ds);
        }
        /// <summary>
        /// 初始化
        /// </summary>
        /// <returns></returns>
        public string GenerBill_Init()
        {
            GenerBills bills = new GenerBills();
            bills.Retrieve(GenerBillAttr.Starter, WebUser.No);
            return bills.ToJson();
        }
        /// <summary>
        /// 查询初始化
        /// </summary>
        /// <returns></returns>
        public string SearchData_Init()
        {
            DataSet ds = new DataSet();
            string sql = "";

            string tSpan = this.GetRequestVal("TSpan");
            if (tSpan == "")
                tSpan = null;

            #region 1、获取时间段枚举/总数.
            SysEnums ses = new SysEnums("TSpan");
            DataTable dtTSpan = ses.ToDataTableField();
            dtTSpan.TableName = "TSpan";
            ds.Tables.Add(dtTSpan);

            GenerBill gb = new GenerBill();
            gb.CheckPhysicsTable();

            sql = "SELECT TSpan as No, COUNT(WorkID) as Num FROM Frm_GenerBill WHERE FrmID='" + this.FrmID + "'  AND Starter='" + WebUser.No + "' AND BillState >= 1 GROUP BY TSpan";

            DataTable dtTSpanNum = DBAccess.RunSQLReturnTable(sql);
            foreach (DataRow drEnum in dtTSpan.Rows)
            {
                string no = drEnum["IntKey"].ToString();
                foreach (DataRow dr in dtTSpanNum.Rows)
                {
                    if (dr["No"].ToString() == no)
                    {
                        drEnum["Lab"] = drEnum["Lab"].ToString() + "(" + dr["Num"] + ")";
                        break;
                    }
                }
            }
            #endregion

            #region 2、处理流程类别列表.
            sql = " SELECT  A.BillState as No, B.Lab as Name, COUNT(WorkID) as Num FROM Frm_GenerBill A, " + BP.Sys.Base.Glo.SysEnum() + " B ";
            sql += " WHERE A.BillState=B.IntKey AND B.EnumKey='BillState' AND  A.Starter='" + WebUser.No + "' AND BillState >=1";
            if (tSpan.Equals("-1") == false)
                sql += "  AND A.TSpan=" + tSpan;

            sql += "  GROUP BY A.BillState, B.Lab  ";

            DataTable dtFlows = DBAccess.RunSQLReturnTable(sql);
            if (BP.Difference.SystemConfig.AppCenterDBFieldCaseModel != FieldCaseModel.None)
            {
                dtFlows.Columns[0].ColumnName = "No";
                dtFlows.Columns[1].ColumnName = "Name";
                dtFlows.Columns[2].ColumnName = "Num";
            }
            dtFlows.TableName = "Flows";
            ds.Tables.Add(dtFlows);
            #endregion

            #region 3、处理流程实例列表.
            string sqlWhere = "";
            sqlWhere = "(1 = 1)AND Starter = '" + WebUser.No + "' AND BillState >= 1";
            if (tSpan.Equals("-1") == false)
            {
                sqlWhere += "AND (TSpan = '" + tSpan + "') ";
            }

            if (this.FlowNo != null)
            {
                sqlWhere += "AND (FrmID = '" + this.FrmID + "')  ";
            }
            else
            {
                // sqlWhere += ")";
            }
            sqlWhere += "ORDER BY RDT DESC";

            string fields = " WorkID,FrmID,FrmName,Title,BillState,Starter,StarterName,Sender,RDT ";

            switch (BP.Difference.SystemConfig.AppCenterDBType)
            {
                case DBType.MySQL:
                case DBType.PostgreSQL:
                case DBType.UX:
                case DBType.HGDB:
                    sql = "SELECT  " + fields + " FROM Frm_GenerBill WHERE " + sqlWhere + " LIMIT 50";
                    break;
                case DBType.MSSQL:
                    sql = "SELECT  TOP 50 " + fields + " FROM Frm_GenerBill WHERE " + sqlWhere;
                    break;
                case DBType.Oracle:
                case DBType.DM:
                case DBType.KingBaseR3:
                case DBType.KingBaseR6:
                case DBType.GBASE8CByOracle:
                    sql = "SELECT " + fields + " FROM (SELECT * FROM Frm_GenerBill WHERE " + sqlWhere + ") WHERE rownum <= 50";
                    break;
                default:
                    throw new Exception("err@没有判断的数据库类型.");
                    break;
            }



            DataTable mydt = DBAccess.RunSQLReturnTable(sql);
            if (BP.Difference.SystemConfig.AppCenterDBFieldCaseModel != FieldCaseModel.None)
            {
                mydt.Columns[0].ColumnName = "WorkID";
                mydt.Columns[1].ColumnName = "FrmID";
                mydt.Columns[2].ColumnName = "FrmName";
                mydt.Columns[3].ColumnName = "Title";
                mydt.Columns[4].ColumnName = "BillState";
                mydt.Columns[5].ColumnName = "Starter";
                mydt.Columns[6].ColumnName = "StarterName";
                mydt.Columns[7].ColumnName = "Sender";
                mydt.Columns[8].ColumnName = "RDT";
            }

            mydt.TableName = "Frm_Bill";
            if (mydt != null)
            {
                mydt.Columns.Add("TDTime");
                foreach (DataRow dr in mydt.Rows)
                {
                    //   dr["TDTime"] =  GetTraceNewTime(dr["FK_Flow"].ToString(), int.Parse(dr["WorkID"].ToString()), int.Parse(dr["FID"].ToString()));
                }
            }
            #endregion

            ds.Tables.Add(mydt);

            return BP.Tools.Json.ToJson(ds);
        }
        #endregion 查询.

        #region 单据导出
        public string Search_Exp()
        {
            FrmBill frmBill = new FrmBill(this.FrmID);
            GEEntitys rpts = new GEEntitys(this.FrmID);

            string name = "数据导出";
            string filename = frmBill.Name + "_" + DataType.CurrentDateTimeCNOfLong + ".xls";
            string filePath = BP.Tools.ExportExcelUtil.ExportDGToExcel(Search_Data(), rpts.GetNewEntity, null, null, filename);
            return filePath;
        }

        public DataTable Search_Data()
        {
            DataSet ds = new DataSet();

            #region 查询语句

            MapData md = new MapData(this.FrmID);

            //取出来查询条件.
            UserRegedit ur = new UserRegedit(WebUser.No, this.FrmID + "_SearchAttrs");

            GEEntitys rpts = new GEEntitys(this.FrmID);

            Attrs attrs = rpts.GetNewEntity.EnMap.Attrs;

            QueryObject qo = new QueryObject(rpts);

            #region 关键字字段.
            string keyWord = ur.SearchKey;
            bool isFirst = true; //是否第一次拼接SQL

            if (md.GetParaBoolen("IsSearchKey") && DataType.IsNullOrEmpty(keyWord) == false && keyWord.Length >= 1)
            {
                Attr attrPK = new Attr();
                foreach (Attr attr in attrs)
                {
                    if (attr.ItIsPK)
                    {
                        attrPK = attr;
                        break;
                    }
                }
                int i = 0;
                string enumKey = ","; //求出枚举值外键.
                foreach (Attr attr in attrs)
                {
                    switch (attr.MyFieldType)
                    {
                        case FieldType.Enum:
                            enumKey = "," + attr.Key + "Text,";
                            break;
                        case FieldType.FK:
                            continue;
                        default:
                            break;
                    }

                    if (attr.MyDataType != DataType.AppString)
                        continue;

                    //排除枚举值关联refText.
                    if (attr.MyFieldType == FieldType.RefText)
                    {
                        if (enumKey.Contains("," + attr.Key + ",") == true)
                            continue;
                    }

                    if (attr.Key.Equals("FK_Dept") || attr.Key.Equals("DeptNo"))
                        continue;

                    i++;
                    if (i == 1)
                    {
                        isFirst = false;
                        /* 第一次进来。 */
                        qo.addLeftBracket();
                        if (BP.Difference.SystemConfig.AppCenterDBVarStr == "@" || BP.Difference.SystemConfig.AppCenterDBVarStr == "?")
                            qo.AddWhere(attr.Key, " LIKE ", BP.Difference.SystemConfig.AppCenterDBType == DBType.MySQL ? (" CONCAT('%'," + BP.Difference.SystemConfig.AppCenterDBVarStr + "SKey,'%')") : (" '%'+" + BP.Difference.SystemConfig.AppCenterDBVarStr + "SKey+'%'"));
                        else
                            qo.AddWhere(attr.Key, " LIKE ", " '%'||" + BP.Difference.SystemConfig.AppCenterDBVarStr + "SKey||'%'");
                        continue;
                    }
                    qo.addOr();

                    if (BP.Difference.SystemConfig.AppCenterDBVarStr == "@" || BP.Difference.SystemConfig.AppCenterDBVarStr == "?")
                        qo.AddWhere(attr.Key, " LIKE ", BP.Difference.SystemConfig.AppCenterDBType == DBType.MySQL ? ("CONCAT('%'," + BP.Difference.SystemConfig.AppCenterDBVarStr + "SKey,'%')") : ("'%'+" + BP.Difference.SystemConfig.AppCenterDBVarStr + "SKey+'%'"));
                    else
                        qo.AddWhere(attr.Key, " LIKE ", "'%'||" + BP.Difference.SystemConfig.AppCenterDBVarStr + "SKey||'%'");

                }
                qo.MyParas.Add("SKey", keyWord);
                qo.addRightBracket();
            }
            else if (DataType.IsNullOrEmpty(md.GetParaString("StringSearchKeys")) == false)
            {
                string field = "";//字段名
                string fieldValue = "";//字段值
                int idx = 0;

                //获取查询的字段
                string[] searchFields = md.GetParaString("StringSearchKeys").Split('*');
                foreach (String str in searchFields)
                {
                    if (DataType.IsNullOrEmpty(str) == true)
                        continue;

                    //字段名
                    string[] items = str.Split(',');
                    if (items.Length == 2 && DataType.IsNullOrEmpty(items[0]) == true)
                        continue;
                    field = items[0];
                    //字段名对应的字段值
                    fieldValue = ur.GetParaString(field);
                    if (DataType.IsNullOrEmpty(fieldValue) == true)
                        continue;
                    idx++;
                    if (idx == 1)
                    {
                        isFirst = false;
                        /* 第一次进来。 */
                        qo.addLeftBracket();
                        if (BP.Difference.SystemConfig.AppCenterDBVarStr == "@" || BP.Difference.SystemConfig.AppCenterDBVarStr == "?")
                            qo.AddWhere(field, " LIKE ", BP.Difference.SystemConfig.AppCenterDBType == DBType.MySQL ? (" CONCAT('%'," + BP.Difference.SystemConfig.AppCenterDBVarStr + field + ",'%')") : (" '%'+" + BP.Difference.SystemConfig.AppCenterDBVarStr + field + "+'%'"));
                        else
                            qo.AddWhere(field, " LIKE ", " '%'||" + BP.Difference.SystemConfig.AppCenterDBVarStr + field + "||'%'");
                        qo.MyParas.Add(field, fieldValue);
                        continue;
                    }
                    qo.addAnd();

                    if (BP.Difference.SystemConfig.AppCenterDBVarStr == "@" || BP.Difference.SystemConfig.AppCenterDBVarStr == "?")
                        qo.AddWhere(field, " LIKE ", BP.Difference.SystemConfig.AppCenterDBType == DBType.MySQL ? ("CONCAT('%'," + BP.Difference.SystemConfig.AppCenterDBVarStr + field + ",'%')") : ("'%'+" + BP.Difference.SystemConfig.AppCenterDBVarStr + field + "+'%'"));
                    else
                        qo.AddWhere(field, " LIKE ", "'%'||" + BP.Difference.SystemConfig.AppCenterDBVarStr + field + "||'%'");
                    qo.MyParas.Add(field, fieldValue);


                }
                if (idx != 0)
                    qo.addRightBracket();
            }

            #endregion 关键字段查询

            #region 时间段的查询
            if (md.GetParaInt("DTSearchWay") != (int)DTSearchWay.None && DataType.IsNullOrEmpty(ur.DTFrom) == false)
            {
                string dtFrom = ur.DTFrom; // this.GetTBByID("TB_S_From").Text.Trim().Replace("/", "-");
                string dtTo = ur.DTTo; // this.GetTBByID("TB_S_To").Text.Trim().Replace("/", "-");

                //按日期查询
                if (md.GetParaInt("DTSearchWay") == (int)DTSearchWay.ByDate)
                {
                    if (isFirst == false)
                        qo.addAnd();
                    else
                        isFirst = false;
                    qo.addLeftBracket();
                    dtTo += " 23:59:59";
                    qo.SQL = md.GetParaString("DTSearchKey") + " >= '" + dtFrom + "'";
                    qo.addAnd();
                    qo.SQL = md.GetParaString("DTSearchKey") + " <= '" + dtTo + "'";
                    qo.addRightBracket();
                }

                if (md.GetParaInt("DTSearchWay") == (int)DTSearchWay.ByDateTime)
                {
                    //取前一天的24：00
                    if (dtFrom.Trim().Length == 10) //2017-09-30
                        dtFrom += " 00:00:00";
                    if (dtFrom.Trim().Length == 16) //2017-09-30 00:00
                        dtFrom += ":00";

                    dtFrom = DateTime.Parse(dtFrom).AddDays(-1).ToString("yyyy-MM-dd") + " 24:00";

                    if (dtTo.Trim().Length < 11 || dtTo.Trim().IndexOf(' ') == -1)
                        dtTo += " 24:00";

                    if (isFirst == false)
                        qo.addAnd();
                    else
                        isFirst = false;
                    qo.addLeftBracket();
                    qo.SQL = md.GetParaString("DTSearchKey") + " >= '" + dtFrom + "'";
                    qo.addAnd();
                    qo.SQL = md.GetParaString("DTSearchKey") + " <= '" + dtTo + "'";
                    qo.addRightBracket();
                }
            }
            #endregion 时间段的查询

            #region 外键或者枚举的查询

            //获得关键字.
            AtPara ap = new AtPara(ur.Vals);
            foreach (string str in ap.HisHT.Keys)
            {

                string val = ap.GetValStrByKey(str);
                if (val.Equals("all"))
                    continue;
                if (isFirst == false)
                    qo.addAnd();
                else
                    isFirst = false;

                qo.addLeftBracket();

                if (BP.Difference.SystemConfig.AppCenterDBFieldIsParaDBType == true)
                {
                    object typeVal = BP.Sys.Base.Glo.GenerRealType(attrs, str, ap.GetValStrByKey(str));
                    qo.AddWhere(str, typeVal);
                }
                else
                {
                    qo.AddWhere(str, ap.GetValStrByKey(str));
                }
                qo.addRightBracket();
            }
            #endregion 外键或者枚举的查询

            #region 设置隐藏字段的过滤查询
            FrmBill frmBill = new FrmBill(this.FrmID);
            string hidenField = frmBill.GetParaString("HidenField");

            if (DataType.IsNullOrEmpty(hidenField) == false)
            {
                hidenField = hidenField.Replace("_WebUser.No", WebUser.No);
                hidenField = hidenField.Replace("_WebUser.Name", WebUser.Name);
                hidenField = hidenField.Replace("_WebUser.FK_DeptName", WebUser.DeptName);
                hidenField = hidenField.Replace("_WebUser.DeptName", WebUser.DeptName);

                hidenField = hidenField.Replace("_WebUser.FK_Dept", WebUser.DeptNo);
                hidenField = hidenField.Replace("_WebUser.DeptNo", WebUser.DeptNo);

                hidenField = hidenField.Replace("_WebUser.OrgNo", WebUser.OrgNo);

                if (isFirst == false)
                    qo.addAnd();
                else
                    isFirst = false;
                qo.addSQL(hidenField);
            }

            #endregion 设置隐藏字段的查询


            if (isFirst == false)
                qo.addAnd();

            qo.AddWhere("BillState", "!=", 0);

            #endregion 查询语句

            qo.addOrderBy("OID");
            qo.DoQuery();
            return rpts.ToDataTableField();

        }
        #endregion  执行导出

        #region 单据导入
        public string ImpData_Done()
        {
            if (BP.Difference.SystemConfig.CustomerNo.Equals("ASSET") == true)
                return ImpData_ASSETDone();
            //IFormFileCollection files = HttpContextHelper.RequestFiles();
            if (HttpContextHelper.RequestFilesCount == 0)
                return "err@请选择要导入的数据信息。";

            string errInfo = "";

            string ext = ".xls";
            string fileName = System.IO.Path.GetFileName(HttpContextHelper.RequestFiles(0).FileName);
            if (fileName.Contains(".xlsx"))
                ext = ".xlsx";


            //设置文件名
            string fileNewName = DBAccess.GenerGUID() + ext;

            //文件存放路径
            string filePath = BP.Difference.SystemConfig.PathOfTemp + "/" + fileNewName;
            HttpContextHelper.UploadFile(HttpContextHelper.RequestFiles(0), filePath);

            //从excel里面获得数据表.
            DataTable dt = DBLoad.ReadExcelFileToDataTable(filePath);

            //删除临时文件
            System.IO.File.Delete(filePath);

            if (dt.Rows.Count == 0)
                return "err@无导入的数据";

            //获得entity.
            FrmBill bill = new FrmBill(this.FrmID);

            if (bill.EntityType == EntityType.FrmEntityNoName)
                return EntityNoName_ImpData(dt, bill);

            GEEntitys rpts = new GEEntitys(this.FrmID);
            GEEntity en = new GEEntity(this.FrmID);



            string noColName = ""; //编号(针对实体表单).
            string nameColName = ""; //名称(针对实体表单).

            BP.En.Map map = en.EnMap;
            Attr attr = map.GetAttrByKey("BillNo");
            noColName = attr.Desc; //
            String codeStruct = bill.EnMap.CodeStruct;
            attr = map.GetAttrByKey("Title");
            nameColName = attr.Desc; //

            //定义属性.
            Attrs attrs = map.Attrs;

            int impWay = this.GetRequestValInt("ImpWay");

            #region 清空方式导入.
            //清空方式导入.
            int count = 0;//导入的行数
            int changeCount = 0;//更新的行数
            String successInfo = "";
            if (impWay == 0)
            {
                rpts.ClearTable();
                GEEntityOID myen = new GEEntityOID(this.FrmID);

                foreach (DataRow dr in dt.Rows)
                {
                    //如果是实体单据,导入的excel必须包含BillNo
                    if (bill.EntityType == EntityType.FrmDict && dt.Columns.Contains(noColName) == false)
                        return "err@导入的excel不包含编号列";
                    string no = "";
                    if (dt.Columns.Contains(noColName) == true)
                        no = dr[noColName].ToString();
                    string name = "";
                    if (dt.Columns.Contains(nameColName) == true)
                        name = dr[nameColName].ToString();
                    myen.OID = 0;

                    //判断是否是自增序列，序列的格式
                    if (DataType.IsNullOrEmpty(codeStruct) == false && DataType.IsNullOrEmpty(no) == false)
                        no = no.PadLeft(System.Int32.Parse(codeStruct), '0');

                    myen.SetValByKey("BillNo", no);
                    if (bill.EntityType == EntityType.FrmDict)
                    {
                        if (myen.Retrieve("BillNo", no) == 1)
                        {
                            errInfo += "err@编号[" + no + "][" + name + "]重复.";
                            continue;
                        }
                    }


                    //给实体赋值
                    errInfo += SetEntityAttrVal(no, dr, attrs, myen, dt, 0, bill);
                    count++;
                    successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的导入成功</span><br/>";
                }
            }

            #endregion 清空方式导入.

            #region 更新方式导入
            if (impWay == 1 || impWay == 2)
            {
                foreach (DataRow dr in dt.Rows)
                {
                    //如果是实体单据,导入的excel必须包含BillNo
                    if (bill.EntityType == EntityType.FrmDict && dt.Columns.Contains(noColName) == false)
                        return "err@导入的excel不包含编号列";
                    string no = "";
                    if (dt.Columns.Contains(noColName) == true)
                        no = dr[noColName].ToString();

                    string name = "";
                    if (dt.Columns.Contains(nameColName) == true)
                        name = dr[nameColName].ToString();
                    //判断是否是自增序列，序列的格式
                    if (DataType.IsNullOrEmpty(codeStruct) == false && DataType.IsNullOrEmpty(no) == false)
                    {
                        no = no.PadLeft(System.Int32.Parse(codeStruct), '0');
                    }
                    GEEntityOID myen = rpts.GetNewEntity as GEEntityOID;
                    myen.SetValByKey("BillNo", no);
                    if (myen.Retrieve("BillNo", no) == 1 && bill.EntityType == EntityType.FrmDict)
                    {
                        //给实体赋值
                        errInfo += SetEntityAttrVal(no, dr, attrs, myen, dt, 1, bill);
                        changeCount++;
                        successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的更新成功</span><br/>";
                        continue;
                    }


                    //给实体赋值
                    errInfo += SetEntityAttrVal(no, dr, attrs, myen, dt, 0, bill);
                    count++;
                    successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的导入成功</span><br/>";
                }
            }
            #endregion

            return "errInfo=" + errInfo + "@Split" + "count=" + count + "@Split" + "successInfo=" + successInfo + "@Split" + "changeCount=" + changeCount;
        }

        private string SetEntityAttrVal(string no, DataRow dr, Attrs attrs, GEEntityOID en, DataTable dt, int saveType, FrmBill fbill)
        {

            //单据数据不存在
            if (saveType == 0)
            {
                Int64 oid = 0;
                if (fbill.EntityType == EntityType.FrmDict)
                    oid = BP.CCBill.Dev2Interface.CreateBlankDictID(fbill.No, WebUser.No, null);
                if (fbill.EntityType == EntityType.FrmBill)
                    oid = BP.CCBill.Dev2Interface.CreateBlankBillID(fbill.No, WebUser.No, null);
                en.OID = oid;
                en.RetrieveFromDBSources();
            }

            string errInfo = "";
            //按照属性赋值.
            foreach (Attr item in attrs)
            {
                if (item.Key.Equals("BillNo") && dt.Columns.Contains(item.Desc) == true)
                {
                    en.SetValByKey(item.Key, no);
                    continue;
                }
                if (item.Key.Equals("Title") && dt.Columns.Contains(item.Desc) == true)
                {
                    en.SetValByKey(item.Key, dr[item.Desc].ToString());
                    continue;
                }

                if (dt.Columns.Contains(item.Desc) == false)
                    continue;

                //枚举处理.
                if (item.MyFieldType == FieldType.Enum)
                {
                    string val = dr[item.Desc].ToString();

                    SysEnum se = new SysEnum();
                    int i = se.Retrieve(SysEnumAttr.EnumKey, item.UIBindKey, SysEnumAttr.Lab, val);

                    if (i == 0)
                    {
                        errInfo += "err@枚举[" + item.Key + "][" + item.Desc + "]，值[" + val + "]不存在.";
                        continue;
                    }

                    en.SetValByKey(item.Key, se.IntKey);
                    continue;
                }

                //外键处理.
                if (item.MyFieldType == FieldType.FK)
                {
                    string val = dr[item.Desc].ToString();
                    Entity attrEn = item.HisFKEn;
                    int i = attrEn.Retrieve("Name", val);
                    if (i == 0)
                    {
                        errInfo += "err@外键[" + item.Key + "][" + item.Desc + "]，值[" + val + "]不存在.";
                        continue;
                    }

                    if (i != 1)
                    {
                        errInfo += "err@外键[" + item.Key + "][" + item.Desc + "]，值[" + val + "]重复..";
                        continue;
                    }

                    //把编号值给他.
                    en.SetValByKey(item.Key, attrEn.GetValByKey("No"));
                    continue;
                }

                //boolen类型的处理..
                if (item.MyDataType == DataType.AppBoolean)
                {
                    string val = dr[item.Desc].ToString();
                    if (val == "是" || val == "有")
                        en.SetValByKey(item.Key, 1);
                    else
                        en.SetValByKey(item.Key, 0);
                    continue;
                }

                string myval = dr[item.Desc].ToString();
                en.SetValByKey(item.Key, myval);
            }
            if (DataType.IsNullOrEmpty(en.GetValStrByKey("BillNo")) == true && DataType.IsNullOrEmpty(fbill.BillNoFormat) == false)
                en.SetValByKey("BillNo", Dev2Interface.GenerBillNo(fbill.BillNoFormat, en.OID, en, fbill.No));

            if (DataType.IsNullOrEmpty(en.GetValStrByKey("Title")) == true && DataType.IsNullOrEmpty(fbill.TitleRole) == false)
                en.SetValByKey("Title", Dev2Interface.GenerTitle(fbill.TitleRole, en));

            en.SetValByKey("BillState", (int)BillState.Editing);
            en.Update();

            GenerBill gb = new GenerBill();
            gb.WorkID = en.OID;
            if (gb.RetrieveFromDBSources() == 0)
            {
                gb.BillState = BillState.FrmOver; //初始化状态.
                gb.Starter = BP.Web.WebUser.No;
                gb.StarterName = BP.Web.WebUser.Name;
                gb.FrmName = fbill.Name; //单据名称.
                gb.FrmID = fbill.No; //单据ID
                if (en.Row.ContainsKey("Title") == true)
                    gb.Title = en.GetValStringByKey("Title");
                if (en.Row.ContainsKey("BillNo") == true)
                    gb.BillNo = en.GetValStringByKey("BillNo");
                gb.FrmTreeNo = fbill.FormTreeNo; //单据类别.
                gb.RDT = DataType.CurrentDateTime;
                gb.Insert();

            }
            else
            {
                gb.BillState = BillState.Editing;
                if (en.Row.ContainsKey("Title") == true)
                    gb.Title = en.GetValStringByKey("Title");
                if (en.Row.ContainsKey("BillNo") == true)
                    gb.BillNo = en.GetValStringByKey("BillNo");
                gb.Update();
            }

            return errInfo;
        }
        public string EntityNoName_ImpData(DataTable dt, FrmBill bill)
        {

            string errInfo = "";

            GEEntityNoNames rpts = new GEEntityNoNames(this.FrmID);
            GEEntityNoName en = new GEEntityNoName(this.FrmID);

            string noColName = ""; //编号(针对实体表单).
            string nameColName = ""; //名称(针对实体表单).

            BP.En.Map map = en.EnMap;
            Attr attr = map.GetAttrByKey("No");
            noColName = attr.Desc; //
            String codeStruct = bill.EnMap.CodeStruct;
            attr = map.GetAttrByKey("Name");
            nameColName = attr.Desc; //

            //定义属性.
            Attrs attrs = map.Attrs;

            int impWay = this.GetRequestValInt("ImpWay");

            #region 清空方式导入.
            //清空方式导入.
            int count = 0;//导入的行数
            int changeCount = 0;//更新的行数
            String successInfo = "";
            if (impWay == 0)
            {
                rpts.ClearTable();
                GEEntityNoName myen = new GEEntityNoName(this.FrmID);

                foreach (DataRow dr in dt.Rows)
                {
                    //如果是实体单据,导入的excel必须包含BillNo
                    if (dt.Columns.Contains(noColName) == false)
                        return "err@导入的excel不包含编号列";
                    string no = "";
                    if (dt.Columns.Contains(noColName) == true)
                        no = dr[noColName].ToString();
                    string name = "";
                    if (dt.Columns.Contains(nameColName) == true)
                        name = dr[nameColName].ToString();

                    //判断是否是自增序列，序列的格式
                    //if (DataType.IsNullOrEmpty(codeStruct) == false && DataType.IsNullOrEmpty(no) == false)
                    //   no = no.PadLeft(System.Int32.Parse(codeStruct), '0');

                    myen.SetValByKey("No", no);
                    if (myen.Retrieve("No", no) == 1)
                    {
                        errInfo += "err@编号[" + no + "][" + name + "]重复.";
                        continue;
                    }


                    //给实体赋值
                    errInfo += SetEntityNoNameAttrVal(no, dr, attrs, myen, dt, 0, bill);
                    count++;
                    successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的导入成功</span><br/>";
                }
            }

            #endregion 清空方式导入.

            #region 更新方式导入
            if (impWay == 1 || impWay == 2)
            {
                foreach (DataRow dr in dt.Rows)
                {
                    //如果是实体单据,导入的excel必须包含BillNo
                    if (dt.Columns.Contains(noColName) == false)
                        return "err@导入的excel不包含编号列";
                    string no = "";
                    if (dt.Columns.Contains(noColName) == true)
                        no = dr[noColName].ToString();

                    string name = "";
                    if (dt.Columns.Contains(nameColName) == true)
                        name = dr[nameColName].ToString();
                    //判断是否是自增序列，序列的格式
                    //if (DataType.IsNullOrEmpty(codeStruct) == false && DataType.IsNullOrEmpty(no) == false)
                    //{
                    //   no = no.PadLeft(System.Int32.Parse(codeStruct), '0');
                    //}
                    GEEntityNoName myen = rpts.GetNewEntity as GEEntityNoName;
                    myen.SetValByKey("No", no);
                    if (myen.Retrieve("No", no) == 1)
                    {
                        //给实体赋值
                        errInfo += SetEntityNoNameAttrVal(no, dr, attrs, myen, dt, 1, bill);
                        changeCount++;
                        successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的更新成功</span><br/>";
                        continue;
                    }


                    //给实体赋值
                    errInfo += SetEntityNoNameAttrVal(no, dr, attrs, myen, dt, 0, bill);
                    count++;
                    successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的导入成功</span><br/>";
                }
            }
            #endregion

            return "errInfo=" + errInfo + "@Split" + "count=" + count + "@Split" + "successInfo=" + successInfo + "@Split" + "changeCount=" + changeCount;
        }

        private string SetEntityNoNameAttrVal(string no, DataRow dr, Attrs attrs, GEEntityNoName en, DataTable dt, int saveType, FrmBill fbill)
        {

            //单据数据不存在
            if (saveType == 0)
            {
                if (DataType.IsNullOrEmpty(no) == true)
                {
                    no = BP.CCBill.Dev2Interface.CreateBlankEntityNoName(fbill.No, WebUser.No, null);
                    en.RetrieveFromDBSources();
                }
                else
                {
                    en.SetValByKey("RecNo", WebUser.No);
                    en.SetValByKey("RecName", WebUser.Name);
                    en.SetValByKey("DeptNo", WebUser.DeptNo);
                    en.SetValByKey("OrgNo", WebUser.OrgNo);
                    en.SetValByKey("RDT", DataType.CurrentDate);
                    en.Insert();
                }
            }


            string errInfo = "";
            //按照属性赋值.
            foreach (Attr item in attrs)
            {
                if (item.Key.Equals("No") && dt.Columns.Contains(item.Desc) == true)
                {
                    en.SetValByKey(item.Key, no);
                    continue;
                }
                if (item.Key.Equals("Name") && dt.Columns.Contains(item.Desc) == true)
                {
                    en.SetValByKey(item.Key, dr[item.Desc].ToString());
                    continue;
                }

                if (dt.Columns.Contains(item.Desc) == false)
                    continue;

                //枚举处理.
                if (item.MyFieldType == FieldType.Enum)
                {
                    string val = dr[item.Desc].ToString();

                    SysEnum se = new SysEnum();
                    int i = se.Retrieve(SysEnumAttr.EnumKey, item.UIBindKey, SysEnumAttr.Lab, val);

                    if (i == 0)
                    {
                        errInfo += "err@枚举[" + item.Key + "][" + item.Desc + "]，值[" + val + "]不存在.";
                        continue;
                    }

                    en.SetValByKey(item.Key, se.IntKey);
                    continue;
                }

                //外键处理.
                if (item.MyFieldType == FieldType.FK)
                {
                    string val = dr[item.Desc].ToString();
                    Entity attrEn = item.HisFKEn;
                    int i = attrEn.Retrieve("Name", val);
                    if (i == 0)
                    {
                        errInfo += "err@外键[" + item.Key + "][" + item.Desc + "]，值[" + val + "]不存在.";
                        continue;
                    }

                    if (i != 1)
                    {
                        errInfo += "err@外键[" + item.Key + "][" + item.Desc + "]，值[" + val + "]重复..";
                        continue;
                    }

                    //把编号值给他.
                    en.SetValByKey(item.Key, attrEn.GetValByKey("No"));
                    continue;
                }

                //boolen类型的处理..
                if (item.MyDataType == DataType.AppBoolean)
                {
                    string val = dr[item.Desc].ToString();
                    if (val == "是" || val == "有")
                        en.SetValByKey(item.Key, 1);
                    else
                        en.SetValByKey(item.Key, 0);
                    continue;
                }

                string myval = dr[item.Desc].ToString();
                en.SetValByKey(item.Key, myval);
            }

            en.SetValByKey("EntityState", (int)BillState.Editing);
            en.Update();
            return errInfo;
        }
        #endregion
        /**
         * 针对于北京农芯科技的单据导入的处理
         */
        public string ImpData_ASSETDone()
        {
            //IFormFileCollection files = HttpContextHelper.RequestFiles();
            if (HttpContextHelper.RequestFilesCount == 0)
                return "err@请选择要导入的数据信息。";

            string errInfo = "";

            string ext = ".xls";
            string fileName = System.IO.Path.GetFileName(HttpContextHelper.RequestFiles(0).FileName);
            if (fileName.Contains(".xlsx"))
                ext = ".xlsx";


            //设置文件名
            string fileNewName = DBAccess.GenerGUID() + ext;

            //文件存放路径
            string filePath = BP.Difference.SystemConfig.PathOfTemp + "/" + fileNewName;
            HttpContextHelper.UploadFile(HttpContextHelper.RequestFiles(0), filePath);

            //从excel里面获得数据表.
            DataTable dt = DBLoad.ReadExcelFileToDataTable(filePath);

            //删除临时文件
            System.IO.File.Delete(filePath);

            if (dt.Rows.Count == 0)
                return "err@无导入的数据";

            //获得entity.
            FrmBill bill = new FrmBill(this.FrmID);
            GEEntitys rpts = new GEEntitys(this.FrmID);
            GEEntity en = new GEEntity(this.FrmID);


            string noColName = ""; //编号(唯一值)
            string nameColName = ""; //名称
            BP.En.Map map = en.EnMap;
            //获取表单的主键，合同类的(合同编号),人员信息类的(身份证号),其他(BillNo)
            bool isContractBill = false;
            bool isPersonBill = false;

            if (dt.Columns.Contains("合同编号") == true)
            {
                noColName = "合同编号";
                isContractBill = true;
            }

            else if (dt.Columns.Contains("身份证号") == true)
            {
                noColName = "身份证号";
                isPersonBill = true;
            }

            else
            {

                Attr attr = map.GetAttrByKey("BillNo");
                noColName = attr.Desc;
                attr = map.GetAttrByKey("Title");
                nameColName = attr.Desc;
            }


            string codeStruct = bill.EnMap.CodeStruct;


            //定义属性.
            Attrs attrs = map.Attrs;

            int impWay = this.GetRequestValInt("ImpWay");

            #region 清空方式导入.
            //清空方式导入.
            int count = 0;//导入的行数
            int changeCount = 0;//更新的行数
            String successInfo = "";
            if (impWay == 0)
            {
                rpts.ClearTable();
                GEEntityOID myen = new GEEntityOID(this.FrmID);

                foreach (DataRow dr in dt.Rows)
                {
                    //如果是实体单据,导入的excel必须包含BillNo
                    if (bill.EntityType == EntityType.FrmDict && dt.Columns.Contains(noColName) == false)
                        return "err@导入的excel不包含编号列";
                    string no = "";
                    if (dt.Columns.Contains(noColName) == true)
                        no = dr[noColName].ToString();
                    string name = "";
                    if (dt.Columns.Contains(nameColName) == true)
                        name = dr[nameColName].ToString();
                    myen.OID = 0;

                    if (isContractBill == false && isPersonBill == false)
                    {
                        //判断是否是自增序列，序列的格式
                        if (DataType.IsNullOrEmpty(codeStruct) == false && DataType.IsNullOrEmpty(no) == false)
                            no = no.PadLeft(System.Int32.Parse(codeStruct), '0');

                        myen.SetValByKey("BillNo", no);
                        if (bill.EntityType == EntityType.FrmDict)
                        {
                            if (myen.Retrieve("BillNo", no) == 1)
                            {
                                errInfo += "err@编号[" + no + "][" + name + "]重复.";
                                continue;
                            }
                        }
                    }



                    //给实体赋值
                    errInfo += SetEntityAttrValForASSET(no, dr, attrs, myen, dt, 0, bill);
                    count++;
                    successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的导入成功</span><br/>";
                }
            }

            #endregion 清空方式导入.

            #region 更新方式导入
            if (impWay == 1 || impWay == 2)
            {
                foreach (DataRow dr in dt.Rows)
                {
                    //如果是实体单据,导入的excel必须包含BillNo
                    if (bill.EntityType == EntityType.FrmDict && dt.Columns.Contains(noColName) == false)
                        return "err@导入的excel不包含编号列";
                    string no = "";
                    if (dt.Columns.Contains(noColName) == true)
                        no = dr[noColName].ToString();

                    string name = "";
                    if (dt.Columns.Contains(nameColName) == true)
                        name = dr[nameColName].ToString();

                    GEEntityOID myen = rpts.GetNewEntity as GEEntityOID;
                    //合同类
                    if (isContractBill == true || isPersonBill == true)
                    {
                        Attr attr = map.GetAttrByDesc(noColName);
                        myen.SetValByKey(attr.Key, no);
                        //存在就编辑修改数据
                        if (myen.Retrieve(attr.Key, no) == 1)
                        {
                            //给实体赋值
                            errInfo += SetEntityAttrValForASSET(no, dr, attrs, myen, dt, 1, bill);
                            changeCount++;
                            successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的更新成功</span><br/>";
                            continue;
                        }
                        else
                        {
                            //给实体赋值
                            errInfo += SetEntityAttrValForASSET(no, dr, attrs, myen, dt, 0, bill);
                            count++;
                            successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的导入成功</span><br/>";
                            continue;
                        }

                    }
                    else
                    {
                        //判断是否是自增序列，序列的格式
                        if (DataType.IsNullOrEmpty(codeStruct) == false && DataType.IsNullOrEmpty(no) == false)
                        {
                            no = no.PadLeft(System.Int32.Parse(codeStruct), '0');
                        }
                        myen.SetValByKey("BillNo", no);
                        if (myen.Retrieve("BillNo", no) == 1 && bill.EntityType == EntityType.FrmDict)
                        {
                            //给实体赋值
                            errInfo += SetEntityAttrValForASSET(no, dr, attrs, myen, dt, 1, bill);
                            changeCount++;
                            successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的更新成功</span><br/>";
                            continue;
                        }
                    }

                    //给实体赋值
                    errInfo += SetEntityAttrValForASSET(no, dr, attrs, myen, dt, 0, bill);
                    count++;
                    successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的导入成功</span><br/>";
                }
            }
            #endregion

            return "errInfo=" + errInfo + "@Split" + "count=" + count + "@Split" + "successInfo=" + successInfo + "@Split" + "changeCount=" + changeCount;
        }
        private string SetEntityAttrValForASSET(string no, DataRow dr, Attrs attrs, GEEntityOID en, DataTable dt, int saveType, FrmBill fbill)
        {

            //单据数据不存在
            if (saveType == 0)
            {
                Int64 oid = 0;
                if (fbill.EntityType == EntityType.FrmDict)
                    oid = BP.CCBill.Dev2Interface.CreateBlankDictID(fbill.No, WebUser.No, null);
                if (fbill.EntityType == EntityType.FrmBill)
                    oid = BP.CCBill.Dev2Interface.CreateBlankBillID(fbill.No, WebUser.No, null);
                en.OID = oid;
                en.RetrieveFromDBSources();
            }

            string errInfo = "";
            //按照属性赋值.
            foreach (Attr item in attrs)
            {
                if (item.Key.Equals("BillNo") && dt.Columns.Contains(item.Desc) == true)
                {
                    en.SetValByKey(item.Key, no);
                    continue;
                }
                if (item.Key.Equals("Title") && dt.Columns.Contains(item.Desc) == true)
                {
                    en.SetValByKey(item.Key, dr[item.Desc].ToString());
                    continue;
                }

                if (dt.Columns.Contains(item.Desc) == false)
                    continue;
                string val = dr[item.Desc].ToString();
                //枚举处理.
                if (item.MyFieldType == FieldType.Enum)
                {
                    SysEnum se = new SysEnum();
                    int i = se.Retrieve(SysEnumAttr.EnumKey, item.UIBindKey, SysEnumAttr.Lab, val);

                    if (i == 0)
                    {
                        errInfo += "err@枚举[" + item.Key + "][" + item.Desc + "]，值[" + val + "]不存在.";
                        continue;
                    }

                    en.SetValByKey(item.Key, se.IntKey);
                    //en.SetValByKey(item.Key.Replace("Code",""), val);
                    continue;
                }


                //外键处理.
                if (item.MyFieldType == FieldType.FK)
                {
                    Entity attrEn = item.HisFKEn;
                    int i = attrEn.Retrieve("Name", val);
                    if (i == 0)
                    {
                        errInfo += "err@外键[" + item.Key + "][" + item.Desc + "]，值[" + val + "]不存在.";
                        continue;
                    }

                    if (i != 1)
                    {
                        errInfo += "err@外键[" + item.Key + "][" + item.Desc + "]，值[" + val + "]重复..";
                        continue;
                    }

                    //把编号值给他.
                    en.SetValByKey(item.Key, attrEn.GetValByKey("No"));
                    if (item.Key.EndsWith("BaseCode") == true)
                        en.SetValByKey(item.Key.Replace("BaseCode", "BaseName"), val);
                    else
                        en.SetValByKey(item.Key.Replace("Code", ""), val);
                    continue;
                }
                //外部数据源
                if (item.MyFieldType == FieldType.Normal && item.MyDataType == DataType.AppString && item.UIContralType == UIContralType.DDL)
                {
                    string uiBindKey = item.UIBindKey;
                    if (DataType.IsNullOrEmpty(uiBindKey) == true)
                        errInfo += "err@外部数据源[" + item.Key + "][" + item.Desc + "]，绑定的外键为空";
                    DataTable mydt = BP.Pub.PubClass.GetDataTableByUIBineKey(uiBindKey);
                    if (mydt.Rows.Count == 0)
                        errInfo += "err@外部数据源[" + item.Key + "][" + item.Desc + "],对应的外键没有获取到外键列表";
                    bool isHave = false;

                    //给赋值名称
                    if (item.Key.EndsWith("BaseCode") == true)
                        en.SetValByKey(item.Key.Replace("BaseCode", "BaseName"), val);
                    else
                        en.SetValByKey(item.Key.Replace("Code", ""), val);

                    en.SetValByKey(item.Key + "T", val);
                    foreach (DataRow mydr in mydt.Rows)
                    {
                        if (mydr["Name"].ToString().Equals(val) == true)
                        {
                            en.SetValByKey(item.Key, mydr["No"].ToString());
                            isHave = true;
                            break;
                        }
                    }

                    if (isHave == false)
                        errInfo += "err@外部数据源[" + item.Key + "][" + item.Desc + "],没有获取到" + val + "对应的Code值";


                    continue;
                }

                //boolen类型的处理..
                if (item.MyDataType == DataType.AppBoolean)
                {
                    if (val == "是" || val == "有")
                        en.SetValByKey(item.Key, 1);
                    else
                        en.SetValByKey(item.Key, 0);
                    continue;
                }
                if (item.MyDataType == DataType.AppDate)
                {
                    if (DataType.IsNullOrEmpty(val) == false)
                    {

                    }

                }

                if (item.Key.EndsWith("BaseName") == true)
                {
                    BP.Port.Depts depts = new BP.Port.Depts();
                    depts.Retrieve(BP.Port.DeptAttr.Name, val);
                    if (depts.Count != 0)
                        en.SetValByKey(item.Key.Replace("BaseName", "BaseCode"), (depts[0] as BP.Port.Dept).No);
                    en.SetValByKey(item.Key, val);
                    continue;
                }
                else
                {
                    if (item.Key.Equals("CI_SmallBusinessFormatCode"))
                    {
                        string mypk = "MultipleChoiceSmall_" + fbill.No + "_" + item.Key;
                        MapExt mapExt = new MapExt();
                        mapExt.setMyPK(mypk);
                        if (mapExt.RetrieveFromDBSources() == 1 && mapExt.DoWay.Equals("3") && DataType.IsNullOrEmpty(mapExt.Tag3) == false)
                        {
                            string newVal = "," + val + ",";
                            string keyVal = "";
                            DataTable dataTable = BP.Pub.PubClass.GetDataTableByUIBineKey(mapExt.Tag3);
                            foreach (DataRow drr in dataTable.Rows)
                            {
                                if (drr["Name"] != null && newVal.Contains("," + drr["Name"].ToString() + ",") == true)
                                    keyVal += drr["No"].ToString() + ",";
                            }
                            keyVal = keyVal.Substring(0, keyVal.Length - 1);

                            en.SetValByKey(item.Key, keyVal);
                            en.SetValByKey(item.Key.Replace("Code", ""), val);
                            en.SetValByKey(item.Key + "T", val);
                        }
                        else
                        {
                            en.SetValByKey(item.Key, val);
                        }
                    }
                    else
                    {
                        if (item.ItIsNum)
                        {
                            if (DataType.IsNullOrEmpty(val) == true || val.Equals("null") == true)
                                val = "0";
                        }
                        en.SetValByKey(item.Key, val);
                    }


                }


            }
            if (DataType.IsNullOrEmpty(en.GetValStrByKey("BillNo")) == true && DataType.IsNullOrEmpty(fbill.BillNoFormat) == false)
                en.SetValByKey("BillNo", Dev2Interface.GenerBillNo(fbill.BillNoFormat, en.OID, en, fbill.No));

            if (DataType.IsNullOrEmpty(en.GetValStrByKey("Title")) == true && DataType.IsNullOrEmpty(fbill.TitleRole) == false)
                en.SetValByKey("Title", Dev2Interface.GenerTitle(fbill.TitleRole, en));
            en.SetValByKey("Rec", WebUser.No);
            en.SetValByKey("BillState", (int)BillState.Editing);
            en.SetValByKey("WFState", WFState.CompleteEnd);
            en.Update();

            GenerBill gb = new GenerBill();
            gb.WorkID = en.OID;
            if (gb.RetrieveFromDBSources() == 0)
            {
                gb.BillState = BillState.FrmOver; //初始化状态.
                gb.Starter = BP.Web.WebUser.No;
                gb.StarterName = BP.Web.WebUser.Name;
                gb.FrmName = fbill.Name; //单据名称.
                gb.FrmID = fbill.No; //单据ID
                if (en.Row.ContainsKey("Title") == true)
                    gb.Title = en.GetValStringByKey("Title");
                if (en.Row.ContainsKey("BillNo") == true)
                    gb.BillNo = en.GetValStringByKey("BillNo");
                gb.FrmTreeNo = fbill.FormTreeNo; //单据类别.
                gb.RDT = DataType.CurrentDateTime;
                gb.Insert();

            }
            else
            {
                gb.BillState = BillState.Editing;
                if (en.Row.ContainsKey("Title") == true)
                    gb.Title = en.GetValStringByKey("Title");
                if (en.Row.ContainsKey("BillNo") == true)
                    gb.BillNo = en.GetValStringByKey("BillNo");
                gb.Update();
            }

            return errInfo;
        }

        #region 执行父类的重写方法.
        /// <summary>
        /// 默认执行的方法
        /// </summary>
        /// <returns></returns>
        protected override string DoDefaultMethod()
        {
            switch (this.DoType)
            {
                case "DtlFieldUp": //字段上移
                    return "执行成功.";
                default:
                    break;
            }

            //找不不到标记就抛出异常.
            throw new Exception("@标记[" + this.DoType + "]，没有找到. @RowURL:" + HttpContextHelper.RequestRawUrl);
        }
        #endregion 执行父类的重写方法.

        #region 获得demo信息.
        public string MethodDocDemoJS_Init()
        {
            MethodFunc func = new MethodFunc(this.MyPK);
            return func.MethodDoc_JavaScript_Demo;
        }
        public string MethodDocDemoSQL_Init()
        {
            MethodFunc func = new MethodFunc(this.MyPK);
            return func.MethodDoc_SQL_Demo;
        }
        #endregion 获得demo信息.

        #region 处理SQL文中注释信息.
        public static string MidStrEx(string sourse, string startstr, string endstr)
        {
            int startindex, endindex;
            string tmpstr = string.Empty;
            string tmpstr2 = string.Empty;
            try
            {
                startindex = sourse.IndexOf(startstr);
                if (startindex == -1)
                    return sourse;
                int i = 0;
                while (startindex != -1)
                {
                    if (i == 0)
                    {
                        endindex = sourse.IndexOf(endstr);
                        if (startindex != 0)
                        {
                            endindex = endindex - startindex;
                        }
                        tmpstr = sourse.Remove(startindex, endindex + endstr.Length);
                    }
                    else
                    {
                        endindex = tmpstr.IndexOf(endstr);
                        if (startindex != 0)
                        {
                            endindex = endindex - startindex;
                        }
                        tmpstr = tmpstr.Remove(startindex, endindex + endstr.Length);

                    }

                    if (endindex == -1)
                        return tmpstr;
                    // tmpstr = tmpstr.Substring(endindex + endstr.Length);
                    startindex = tmpstr.IndexOf(startstr);
                    i++;
                }
                //result = tmpstr.Remove(endindex);

            }
            catch (Exception ex)
            {
                BP.DA.Log.DebugWriteError("MidStrEx Err:" + ex.Message);
            }
            return tmpstr;
        }
        #endregion 处理SQL文中注释信息..

        #region 实体单据查询启动指定子流程显示的字段
        public string DictFlow_MapAttrs()
        {
            DataSet ds = new DataSet();
            string fk_mapData = "ND" + int.Parse(this.FlowNo) + "01";

            //查询出单流程的所有字段
            MapAttrs mattrs = new MapAttrs();
            mattrs.Retrieve(MapAttrAttr.FK_MapData, fk_mapData, MapAttrAttr.Idx);

            ds.Tables.Add(mattrs.ToDataTableField("Sys_MapAttr"));

            MapAttrs mattrsOfSystem1 = new MapAttrs();
            //判断表单中是否存在默认值@WebUser.No,@WebUser.FK_Dept,@RDT
            bool isHaveNo = false;
            bool isHaveRDT = false;
            bool isHaveTitle = false;

            //系统字段字符串
            string sysFields = "";
            foreach (MapAttr mapAttr in mattrs)
            {

                if (mapAttr.KeyOfEn.Equals(GERptAttr.Rec) || mapAttr.KeyOfEn.Equals(GERptAttr.RDT) || mapAttr.KeyOfEn.Equals(GERptAttr.CDT))
                    continue;
                if (mapAttr.KeyOfEn.Equals(GERptAttr.Title) == true)
                {
                    mattrsOfSystem1.AddEntity(mapAttr);
                    isHaveTitle = true;
                    continue;
                }

                switch (mapAttr.DefValReal)
                {

                    case "@WebUser.No":
                    case "@WebUser.Name":
                        sysFields += "," + mapAttr.KeyOfEn;
                        isHaveNo = true;
                        mattrsOfSystem1.AddEntity(mapAttr);

                        break;

                    case "@RDT":
                        mattrsOfSystem1.AddEntity(mapAttr);
                        isHaveRDT = true;
                        sysFields += "," + mapAttr.KeyOfEn;
                        break;
                    default: break;
                }
            }


            //默认显示的系统字段 标题、发起人、发起时间、当前所在节点、状态 , 系统字段需要在RPT中查找
            string fields = "(";
            if (isHaveTitle == false)
                fields += "'" + GERptAttr.Title + "',";
            if (isHaveNo == false)
                fields += "'" + GERptAttr.FlowStarter + "',";

            if (isHaveRDT == false)
                fields += "'" + GERptAttr.FlowStartRDT + "',";
            fields += "'" + GERptAttr.WFState + "','" + GERptAttr.FlowEndNode + "')";
            MapAttrs mattrsOfSystem = new MapAttrs();
            QueryObject qo = new QueryObject(mattrsOfSystem);
            qo.AddWhere(MapAttrAttr.FK_MapData, "ND" + int.Parse(this.FlowNo) + "Rpt");
            qo.addAnd();
            qo.AddWhereIn(MapAttrAttr.KeyOfEn, fields);
            //qo.addOrderBy(MapAttrAttr.Idx);
            //qo.addOrderByOfSelf("CHARINDEX(" + MapAttrAttr.KeyOfEn + ",'" + fields.Replace("'", "") + "')");
            qo.DoQuery();
            mattrsOfSystem.AddEntities(mattrsOfSystem1);

            ds.Tables.Add(mattrsOfSystem.ToDataTableField("Sys_MapAttrOfSystem"));

            //系统字段字符串
            fields = fields.Replace("(", "").Replace(")", "").Replace("'", "") + ",";
            sysFields += ",OID,FID,RDT,CDT,Rec,FK_Dept,DeptNo,MyNum,FK_NY,Emps,Title," + fields;
            DataTable dt = new DataTable();
            dt.Columns.Add("Field");
            dt.TableName = "Sys_Fields";
            DataRow dr = dt.NewRow();
            dr["Field"] = sysFields;
            dt.Rows.Add(dr);
            ds.Tables.Add(dt);

            //用户查询注册信息中记录使用到的流程业务表中的字段
            UserRegedit ur = new UserRegedit(WebUser.No, "ND" + int.Parse(this.FlowNo) + "Rpt_SearchAttrs");
            ur.SetPara("RptField", "," + fields);
            ur.Update();

            return BP.Tools.Json.ToJson(ds);
        }
        #endregion

        #region 实体单据启动多个子流程的查询
        public string DictFlow_Search()
        {
            //实体单据的信息
            string frmID = this.GetRequestVal("FrmID");
            string frmOID = this.GetRequestVal("FrmOID");

            //表单编号
            string fk_mapData = "ND" + int.Parse(this.FlowNo) + "01";

            //当前用户查询信息表
            UserRegedit ur = new UserRegedit(WebUser.No, "ND" + int.Parse(this.FlowNo) + "Rpt_SearchAttrs");

            //表单属性
            MapData mapData = new MapData(fk_mapData);

            //流程的系统字段
            string rptFields = ur.GetParaString("RptField");
            rptFields = rptFields.Substring(1, rptFields.Length - 1);
            rptFields = "('" + rptFields.Replace(",", "','") + "'" + ",'" + GERptAttr.FlowStarter + "','" + GERptAttr.FK_Dept + "','" + GERptAttr.FlowEmps + "','" + GERptAttr.FlowEndNode + "','" + GERptAttr.PWorkID + "','" + GERptAttr.PFlowNo + "')";
            MapAttrs mattrsOfSystem = new MapAttrs();
            QueryObject qo = new QueryObject(mattrsOfSystem);
            qo.AddWhere(MapAttrAttr.FK_MapData, "ND" + int.Parse(this.FlowNo) + "Rpt");
            qo.addAnd();
            qo.AddWhereIn(MapAttrAttr.KeyOfEn, rptFields);
            qo.DoQuery();

            //流程表单对应的所有字段
            MapAttrs attrs = new MapAttrs();
            attrs.Retrieve(MapAttrAttr.FK_MapData, fk_mapData, MapAttrAttr.Idx);
            attrs.AddEntities(mattrsOfSystem);

            //流程表单对应的流程数据
            GEEntitys ens = new GEEntitys(fk_mapData);
            GEEntity en = ens.GetNewEntity as GEEntity;
            foreach (MapAttr mapAttr in mattrsOfSystem)
                en.EnMap.AddAttr(mapAttr.HisAttr);
            Cache.SQL_Cache.Remove(fk_mapData);

            qo = new QueryObject(ens);
            qo.AddWhere(GERptAttr.PWorkID, frmOID);
            qo.addAnd();
            qo.AddWhere(GERptAttr.PFlowNo, frmID);
            qo.AddWhere(" AND  WFState > 1 ");
            //qo.addAnd();
            qo.AddWhere(" AND FID = 0 ");
            if (DataType.IsNullOrEmpty(ur.OrderBy) == false)
                if (ur.OrderWay.ToUpper().Equals("DESC") == true)
                    qo.addOrderByDesc(ur.OrderBy);
                else
                    qo.addOrderBy(ur.OrderBy);
            ur.Update();
            qo.DoQuery();

            return BP.Tools.Json.ToJson(ens.ToDataTableField("FlowSearch_Data"));
        }
        #endregion  实体单据启动多个子流程的查询

        public string RefDict_CreateBillWorkID()
        {
            Int64 refOID = GetRequestValInt64("RefOID");
            string refDict = GetRequestVal("RefDict");
            //获取关联实体表单的数据信息
            GERpt refRpt = new GERpt(refDict, refOID);
            string billNo = this.GetRequestVal("BillNo");
            Int64 workID = BP.CCBill.Dev2Interface.CreateBlankBillID(this.FrmID, BP.Web.WebUser.No, null, billNo);

            GenerBill gb = new GenerBill(workID);
            gb.BillState = BillState.Draft;
            gb.Update();
            //获取当前单据表单的数据信息
            GERpt rpt = new GERpt(this.FrmID, workID);
            rpt.Copy(refRpt);
            rpt.SetValByKey("BillState", (int)gb.BillState);
            rpt.Update();
            return workID.ToString();
        }

        #region 外部流程网页授权URL
        public string DictFlow_Qcode()
        {
            string state = "FlowNo_" + this.FlowNo + "|OrgNo_" + WebUser.OrgNo + "|FrmID_" + this.FrmID + "|FrmOID_" + this.GetRequestVal("FrmOID");
            //回调url
            string redirect_uri = HttpUtility.UrlEncode("http://www.ccbpm.cn/WF/CCBill/DictFlowStart.htm");
            //授权链接
            string oatuth2 = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + BP.Difference.SystemConfig.AppID + "&redirect_uri=" + redirect_uri + "&response_type=code&scope=snsapi_userinfo&&state=" + state + "#wechat_redirect";
            return oatuth2;
        }
        #endregion 外部流程网页授权URL

        #region 方法处理.
        public string MyDict_DoBill_Start()
        {
            //创建单据
            Int64 workid = BP.CCBill.Dev2Interface.CreateBlankBillID(this.FrmID);

            string workids = GetRequestVal("WorkIDs");
            if (DataType.IsNullOrEmpty(workids) == true)
                return "err@请选择需要操作的行";
            string fromFrmID = GetRequestVal("FromFrmID");
            #region 把实体表单的数据集合拷贝到单据从表数据中
            GEEntitys ens = new GEEntitys(fromFrmID);
            QueryObject qo = new QueryObject(ens);
            qo.AddWhereIn("OID", "(" + workids + ")");
            qo.DoQuery();
            GEDtl gedtl = null;
            string mapdtlNo = this.FrmID + "Dtl1";
            GEDtls gedtls = new GEDtls(mapdtlNo);
            gedtls.Retrieve(GEDtlAttr.RefPK, workid);
            foreach (GEEntityOID en in ens)
            {
                //先判断从表中是不是存在该实体数据，存在continue;
                if (gedtls.IsExits("DictOID", en.OID) == true)
                    continue;
                gedtl = new GEDtl(mapdtlNo);
                gedtl.Copy(en);
                gedtl.RefPKInt64 = workid;
                gedtl.SetValByKey("DictOID", en.OID);
                gedtl.OID = 0;
                gedtl.Insert();
            }
            #endregion 把实体表单的数据集合拷贝到单据从表数据中

            return "./MyBill.htm?FrmID=" + this.FrmID + "&WorkID=" + workid;
        }
        public string MyDict_DoFlowBatchBaseData_StartFlow()
        {
            //创建工作.
            Int64 workid = BP.WF.Dev2Interface.Node_CreateBlankWork(this.FlowNo);

            string workids = GetRequestVal("WorkIDs");
            if (DataType.IsNullOrEmpty(workids) == true)
                return "err@请选择需要操作的行";
            string fromFrmID = GetRequestVal("FromFrmID");
            #region 把实体表单的数据集合拷贝到流程从表数据中
            GEEntityOIDs ens = new GEEntityOIDs(fromFrmID);
            QueryObject qo = new QueryObject(ens);
            qo.AddWhereIn("OID", "(" + workids + ")");
            qo.DoQuery();
            GEDtl gedtl = null;
            string mapdtlNo = "ND" + int.Parse(this.FlowNo) + "01" + "Dtl1";
            GEDtls gedtls = new GEDtls(mapdtlNo);
            gedtls.Retrieve(GEDtlAttr.RefPK, workid);
            foreach (GEEntityOID en in ens)
            {
                //先判断从表中是不是存在该实体数据，存在continue;
                if (gedtls.IsExits("DictOID", en.OID) == true)
                    continue;
                gedtl = new GEDtl(mapdtlNo);
                gedtl.Copy(en);
                gedtl.RefPKInt64 = workid;
                gedtl.SetValByKey("DictOID", en.OID);
                gedtl.OID = 0;
                gedtl.Insert();
            }
            #endregion 把实体表单的数据集合拷贝到单据从表数据中

            //更新标记, 表示:该流程被谁发起.
            GenerWorkFlow gwf = new GenerWorkFlow(workid);
            gwf.PWorkID = this.WorkID;
            gwf.PFlowNo = fromFrmID;

            gwf.SetPara("FlowBaseData", "1"); //启动了修改基础资料流程..
            gwf.SetPara("MethodNo", this.MethodNo); //启动了修改基础资料流程..
            gwf.SetPara("DictFrmID", fromFrmID); //启动了修改基础资料流程..
            gwf.SetPara("DictWorkID", workids); //启动了修改基础资料流程..
            gwf.Update();

            //写日志.
            BP.CCBill.Dev2Interface.WriteTrack(fromFrmID, "0", FrmActionType.StartFlow, "启动:" + gwf.FlowName + ",标题:" + gwf.Title);
            return "../MyFlow.htm?FK_Flow=" + this.FlowNo + "&WorkID=" + workid;
        }
        /// <summary>
        /// 执行流程:变更基础资料
        /// </summary>
        /// <returns></returns>
        public string MyDict_DoFlowBaseData_StartFlow()
        {
            BP.CCBill.Template.Method md = new BP.CCBill.Template.Method(this.MethodNo);

            GEEntity en = new GEEntity(md.FrmID, this.WorkID);

            Hashtable ht = new Hashtable();

            Attrs attrs = en.EnMap.Attrs;
            foreach (Attr item in attrs)
            {
                if (item.Key.Equals("BillNo") == false && BP.WF.Glo.FlowFields.Contains("," + item.Key + ",") == true)
                    continue;

                string val = en.GetValStrByKey(item.Key);
                ht.Add(item.Key, val);
                ht.Add("bak" + item.Key, val);
            }

            //创建工作.
            Int64 workid = BP.WF.Dev2Interface.Node_CreateBlankWork(md.FlowNo, ht);

            //更新标记, 表示:该流程被谁发起.
            GenerWorkFlow gwf = new GenerWorkFlow(workid);
            gwf.PWorkID = this.WorkID;
            gwf.PFlowNo = md.FrmID;

            gwf.SetPara("FlowBaseData", "1"); //启动了修改基础资料流程..
            gwf.SetPara("MethodNo", this.MethodNo); //启动了修改基础资料流程..
            gwf.SetPara("DictFrmID", md.FrmID); //启动了修改基础资料流程..
            gwf.SetPara("DictWorkID", this.WorkID); //启动了修改基础资料流程..
            gwf.Update();

            //写日志.
            BP.CCBill.Dev2Interface.WriteTrack(md.FrmID, this.WorkID.ToString(), FrmActionType.StartFlow, "启动:" + gwf.FlowName + ",标题:" + gwf.Title, null, md.FlowNo, md.Name, int.Parse(md.FlowNo + "01"), workid);

            //   GEEntity frm=new GEEntity("ND"+int.Parse())
            return "../MyFlow.htm?FK_Flow=" + md.FlowNo + "&WorkID=" + workid;
        }
        /// <summary>
        /// 发起其他业务流程
        /// </summary>
        /// <returns></returns>
        public string MyDict_DoFlowEtc_StartFlow()
        {
            BP.CCBill.Template.Method md = new BP.CCBill.Template.Method(this.MethodNo);

            GEEntity en = new GEEntity(md.FrmID, this.WorkID);

            #region 处理发起限制.
            //判断是否可以发起流程? 处理发起限制.
            DBRoles rls = new DBRoles();
            rls.Retrieve("FrmID", md.FrmID, "DBRole", "StartLimit", "Docs", md.FlowNo);
            if (rls.Count != 0)
            {
                foreach (DBRole item in rls)
                {
                    if (item.MarkID.Equals("OnlyStartSelfCreateRec") == true && en.GetValByKey("Starter").Equals(WebUser.No) == false)
                        return "err@改单据不是您创建的，您不能发起流程。";
                    //如果有未完成的流程，就不能发起.
                    if (item.MarkID.Equals("UnOverFlow") == true)
                    {
                        string sql = "SELECT Title FROM WF_GenerWorkFlow WHERE PWorkID='" + this.WorkID + "' AND WFState IN(5,2)";
                        DataTable dt = DBAccess.RunSQLReturnTable(sql);
                        if (dt.Rows.Count != 0)
                            return "err@有未完成的流程，当前流程不能启动，需要等待上一个流程完成后，该流程才可以重新发起。";
                    }
                }
            }
            #endregion 处理发起限制.


            Hashtable ht = new Hashtable();
            Attrs attrs = en.EnMap.Attrs;
            foreach (Attr item in attrs)
            {
                string val = en.GetValStrByKey(item.Key);
                if (item.Key.Equals("Title"))
                {
                    ht.Add("DictName", val);
                    continue;
                }

                if (item.Key.Equals("BillNo"))
                {
                    ht.Add("DictNo", val);
                    continue;
                }
                if (BP.WF.Glo.FlowFields.Contains("," + item.Key + ",") == true)
                    continue;
                ht.Add("bak" + item.Key, val);
                ht.Add(item.Key, val);

            }

            //创建工作.
            Int64 workid = BP.WF.Dev2Interface.Node_CreateBlankWork(md.MethodID, ht);

            //更新标记, 表示:该流程被谁发起.
            GenerWorkFlow gwf = new GenerWorkFlow(workid);
            gwf.PWorkID = this.WorkID;
            gwf.PFlowNo = this.FrmID;
            gwf.SetPara("DictFlowEtc", "1"); //启动了其他业务流程.
            gwf.Update();
            BP.WF.Dev2Interface.Node_SetDraft(workid);
            int nodeID = Int32.Parse(Int32.Parse(md.MethodID) + "01");
            Node nd = new Node(nodeID);
            //判断当前节点是否是绑定当前表单
            if (nd.HisFormType == NodeFormType.RefOneFrmTree)
            {
                BP.WF.Template.FrmNode frmNode = new BP.WF.Template.FrmNode();
                frmNode.setMyPK(this.FrmID + "_" + nodeID + "_" + nd.FlowNo);
                int i = frmNode.RetrieveFromDBSources();
                if (i == 1 && frmNode.WhoIsPK == WF.Template.WhoIsPK.PWorkID)
                {
                    //绑定表单并且是PWorkID,不需要拷贝数据
                    Work wk = nd.HisWork;
                    wk.Delete(WorkAttr.OID, workid);
                    BP.CCBill.Dev2Interface.WriteTrack(md.FrmID, this.WorkID.ToString(), FrmActionType.StartFlow, "启动:" + gwf.FlowName + ",标题:" + gwf.Title,
               null, md.FlowNo, md.Name, int.Parse(md.FlowNo + "01"), workid);
                    return "../MyFlow.htm?FK_Flow=" + md.FlowNo + "&WorkID=" + workid + "&PWorkID=" + this.WorkID;
                }
            }
            #region 复制明细,从表
            //复制从表数据.
            MapDtls dtls = new MapDtls(md.FrmID);
            //获取当前流程开始节点的表单
            String frmID = "ND" + Int32.Parse(md.MethodID) + "01";
            MapData mapData = new MapData(frmID);
            MapDtls dtls1 = mapData.MapDtls;
            foreach (MapDtl dtl in dtls)
            {
                //判断从表是否存在，根据别名计算
                Entity enn = dtls1.GetEntityByKey(MapDtlAttr.Alias, dtl.Alias);
                if (enn == null) continue;
                MapDtl dtl1 = (MapDtl)enn;
                //删除旧的数据.
                DBAccess.RunSQL("DELETE FROM " + dtl1.PTable + " WHERE RefPK='" + workid + "'");

                GEDtls ensDtl = new GEDtls(dtl.No);
                ensDtl.Retrieve(GEDtlAttr.RefPK, this.WorkID.ToString());

                foreach (GEDtl enDtl in ensDtl)
                {
                    enDtl.RefPK = workid.ToString();
                    enDtl.InsertAsNew();
                }
            }

            //复制附件数据.
            FrmAttachments aths = new FrmAttachments(md.FrmID);
            FrmAttachments aths1 = new FrmAttachments(mapData.No);
            foreach (FrmAttachment ath in aths)
            {
                Entity enn = aths1.GetEntityByKey(FrmAttachmentAttr.NoOfObj, ath.NoOfObj);
                if (enn == null) continue;
                //删除可能存在的新oid数据。
                DBAccess.RunSQL("DELETE FROM Sys_FrmAttachmentDB WHERE NoOfObj='" + ath.NoOfObj + "' AND RefPKVal='" + workid + "'");

                //找出旧数据.
                FrmAttachmentDBs athDBs = new FrmAttachmentDBs(md.FrmID, this.WorkID.ToString());
                foreach (FrmAttachmentDB athDB in athDBs)
                {
                    FrmAttachmentDB athDB_N = new FrmAttachmentDB();
                    athDB_N.Copy(athDB);

                    athDB_N.FrmID = mapData.No;
                    athDB_N.RefPKVal = workid.ToString();

                    if (athDB_N.HisAttachmentUploadType == AttachmentUploadType.Single)
                    {
                        /*如果是单附件.*/
                        athDB_N.setMyPK(athDB_N.FK_FrmAttachment + "_" + workid);
                        if (athDB_N.IsExits == true)
                        {
                            continue; //说明上一个节点或者子线程已经copy过了, 但是还有子线程向合流点传递数据的可能，所以不能用break.
                        }

                        athDB_N.Insert();
                    }
                    else
                    {
                        athDB_N.setMyPK(DBAccess.GenerGUID());
                        athDB_N.Insert();
                    }
                }
            }
            #endregion 复制明细,从表
            //写日志.
            BP.CCBill.Dev2Interface.WriteTrack(md.FrmID, this.WorkID.ToString(), FrmActionType.StartFlow, "启动:" + gwf.FlowName + ",标题:" + gwf.Title,
                null, md.FlowNo, md.Name, int.Parse(md.FlowNo + "01"), workid);

            //GEEntity frm=new GEEntity("ND"+int.Parse())
            return "../MyFlow.htm?FK_Flow=" + md.FlowNo + "&WorkID=" + workid + "&PWorkID=" + this.WorkID;
        }
        #endregion

        /// <summary>
        /// 草稿列表
        /// </summary>
        /// <returns></returns>
        public string Draft_Init()
        {
            //草稿列表.
            DataTable dt = BP.CCBill.Dev2Interface.DB_Draft(this.FrmID, BP.Web.WebUser.No);

            //返回组合
            return BP.Tools.Json.DataTableToJson(dt, false);
        }
        public string Recent_Init()
        {
            //草稿列表.
            DataTable dt = BP.CCBill.Dev2Interface.DB_Draft(this.FrmID, BP.Web.WebUser.No);

            //返回组合
            return BP.Tools.Json.DataTableToJson(dt, false);
        }
        /// <summary>
        /// 执行
        /// </summary>
        /// <returns>返回执行结果</returns>
        public string DoMethod_ExeSQL()
        {
            MethodFunc func = new MethodFunc(this.MyPK);
            string doc = func.MethodDoc_SQL;
            string workID = this.WorkIDStr;
            if (DataType.IsNullOrEmpty(workID) == true)
            {
                //批量执行方法
                string workids = this.GetRequestVal("WorkIDs");
                if (DataType.IsNullOrEmpty(workids) == true)
                    throw new Exception("err@执行方法获取到的WorkID或者WorkIDs不能为空");
                string[] strs = workids.Split(',');
                workID = strs[0];
                doc = doc.Replace("@WorkIDs", workids);
            }
            GEEntity en = new GEEntity(func.FrmID, workID);

            doc = BP.WF.Glo.DealExp(doc, en, null); //替换里面的内容.
            string sql = MidStrEx(doc, "/*", "*/");
            try
            {
                DBAccess.RunSQLs(sql);
                if (func.MsgSuccess.Equals(""))
                    func.MsgSuccess = "执行成功.";

                BP.CCBill.Dev2Interface.WriteTrack(this.FrmID, workID, "Func", "执行方法", func.Name);

                return func.MsgSuccess;
            }
            catch (Exception ex)
            {
                if (func.MsgErr.Equals(""))
                    func.MsgErr = "执行失败(DoMethod_ExeSQL).";
                return "err@" + func.MsgErr + " @ " + ex.Message;
            }
        }
        public string DoMethod_ExecFunc()
        {
            MethodFunc func = new MethodFunc(this.MyPK);
            string doc = func.Docs;
            BuessUnitBase en = BP.Sys.Base.Glo.GetBuessUnitEntityByEnName(doc);
            if (en == null)
                throw new Exception("err@类名错误：" + doc);
            try
            {
                string workID = this.WorkIDStr;
                en.WorkID = Int64.Parse(workID);
                en.DoIt();

                BP.CCBill.Dev2Interface.WriteTrack(this.FrmID, workID, "执行方法", func.Name);
                return func.MsgSuccess;
            }
            catch (Exception ex)
            {
                if (func.MsgErr.Equals(""))
                    func.MsgErr = "执行失败(DoMethod_ExecFunc).";
                return "err@" + func.MsgErr + " @ " + ex.Message;
            }
        }
        /// <summary>
        /// 解析异常
        /// </summary>
        /// <param name="ex">异常</param>
        /// <param name="sql">执行的SQL</param>
        /// <returns></returns>
        public string DealException(Exception ex, string sql)
        {
            string errmsg = "";
            string exMessage = ex.Message.ToLower();
            //字段不存在的异常
            if (exMessage != null && (exMessage.Contains("does not exist") || exMessage.Contains("unknown column")))
            {
                errmsg = "err@执行失败，物理表的字段不存在，请创建字段.";
            }
            //表不存在的异常
            if (exMessage != null && (exMessage.Contains("doesn't exist")))
            {
                errmsg = "err@执行失败，物理表不存在，请您创建物理表或者表名配置错误.";
            }

            if (DataType.IsNullOrEmpty(sql) == false)
            {
                errmsg += "SQL=" + sql;
            }
            return errmsg;
        }
        /// <summary>
        /// 执行SQL
        /// </summary>
        /// <returns></returns>
        public string DoMethodPara_ExeSQL()
        {
            MethodFunc func = new MethodFunc(this.PKVal);
            string doc = func.MethodDoc_SQL;
            string workID = this.WorkIDStr;
            if (DataType.IsNullOrEmpty(workID) == true)
            {
                //批量执行方法
                string workids = this.GetRequestVal("WorkIDs");
                if (DataType.IsNullOrEmpty(workids) == true)
                    throw new Exception("err@执行方法获取到的WorkID或者WorkIDs不能为空");
                string[] strs = workids.Split(',');
                workID = strs[0];
                doc = doc.Replace("@WorkIDs", workids);
            }
            GEEntity en = new GEEntity(func.FrmID, workID);

            #region 替换参数变量.
            if (doc.Contains("@") == true)
            {
                MapAttrs mattrs = new MapAttrs();
                mattrs.Retrieve(MapAttrAttr.FK_MapData, this.PKVal);
                foreach (MapAttr item in mattrs)
                {
                    if (doc.Contains("@") == false)
                        break;
                    if (item.UIContralType == UIContralType.TB)
                    {
                        doc = doc.Replace("@" + item.KeyOfEn, this.GetRequestVal("TB_" + item.KeyOfEn));
                        continue;
                    }

                    if (item.UIContralType == UIContralType.DDL)
                    {
                        doc = doc.Replace("@" + item.KeyOfEn, this.GetRequestVal("DDL_" + item.KeyOfEn));
                        continue;
                    }


                    if (item.UIContralType == UIContralType.CheckBok)
                    {
                        doc = doc.Replace("@" + item.KeyOfEn, this.GetRequestVal("CB_" + item.KeyOfEn));
                        continue;
                    }

                    if (item.UIContralType == UIContralType.RadioBtn)
                    {
                        doc = doc.Replace("@" + item.KeyOfEn, this.GetRequestVal("RB_" + item.KeyOfEn));
                        continue;
                    }
                }
            }
            #endregion 替换参数变量.

            doc = BP.WF.Glo.DealExp(doc, en, null); //替换里面的内容.
            string sql = MidStrEx(doc, "/*", "*/");
            #region 开始执行SQLs.
            try
            {
                DBAccess.RunSQLs(sql);
                if (func.MsgSuccess.Equals(""))
                    func.MsgSuccess = "执行成功.";

                return func.MsgSuccess;
            }
            catch (Exception ex)
            {
                string de = DealException(ex, sql);
                if (DataType.IsNullOrEmpty(de) == false)
                {
                    return de;
                }
                if (func.MsgErr.Equals(""))
                    func.MsgErr = "执行失败.";

                return "err@" + func.MsgErr + " @ " + ex.Message;
            }
            #endregion 开始执行SQLs.

            BP.CCBill.Dev2Interface.WriteTrack(this.FrmID, workID, "执行方法", func.Name);

            return "err@" + func.MethodDocTypeOfFunc + ",执行的类型没有解析.";
        }

        public string DoMethodPara_ExeSQL_V3()
        {
            MethodFunc func = new MethodFunc(this.PKVal);
            string doc = func.MethodDoc_SQL;
            string workID = this.WorkIDStr;
            if (DataType.IsNullOrEmpty(workID) == true)
            {
                //批量执行方法
                string workids = this.GetRequestVal("WorkIDs");
                if (DataType.IsNullOrEmpty(workids) == true)
                    throw new Exception("err@执行方法获取到的WorkID或者WorkIDs不能为空");
                string[] strs = workids.Split(',');
                workID = strs[0];
                doc = doc.Replace("@WorkIDs", workids);
            }
            GEEntity en = new GEEntity(func.FrmID, workID);

            #region 替换参数变量.
            string parasDesc = "";
            if (doc.Contains("@") == true)
            {
                MapAttrs mattrs = new MapAttrs();
                mattrs.Retrieve(MapAttrAttr.FK_MapData, this.PKVal);
                foreach (MapAttr item in mattrs)
                {
                    if (doc.Contains("@") == false)
                        break;
                    doc = doc.Replace("@" + item.KeyOfEn, this.GetRequestVal(item.KeyOfEn));

                    parasDesc += "@" + item.Name + "=" + this.GetRequestVal(item.KeyOfEn);
                }
            }
            #endregion 替换参数变量.

            doc = BP.WF.Glo.DealExp(doc, en, null); //替换里面的内容.
            string sql = MidStrEx(doc, "/*", "*/");

            #region 开始执行SQLs.
            try
            {
                DBAccess.RunSQLs(sql);
                if (func.MsgSuccess.Equals(""))
                    func.MsgSuccess = "执行成功.";
                BP.CCBill.Dev2Interface.WriteTrack(this.FrmID, workID, "Func", "执行成功", parasDesc, func.MethodID, func.Name);

                return func.MsgSuccess;
            }
            catch (Exception ex)
            {
                BP.CCBill.Dev2Interface.WriteTrack(this.FrmID, workID, "Func", "执行方法失败:" + func.Name + func.MsgErr + ex.Message, parasDesc, func.MethodID, func.Name);

                string de = DealException(ex, sql);
                if (DataType.IsNullOrEmpty(de) == false)
                    return de;

                if (func.MsgErr.Equals(""))
                    func.MsgErr = "执行失败.";

                return "err@" + func.MsgErr + " @ " + ex.Message;
            }
            #endregion 开始执行SQLs.

            //无法执行到这里
            //BP.CCBill.Dev2Interface.WriteTrack(this.FrmID, workID, "执行方法", func.Name);

            //return "err@" + func.MethodDocTypeOfFunc + ",执行的类型没有解析.";
        }
        /// <summary>
        /// 执行url.
        /// </summary>
        /// <returns></returns>
        public string DoMethodPara_ExeUrl()
        {
            MethodFunc func = new MethodFunc(this.PKVal);
            string doc = func.MethodDoc_Url;
            if (this.WorkID == 0)
            {
                //批量执行方法
                string workids = this.GetRequestVal("WorkIDs");
                if (DataType.IsNullOrEmpty(workids) == true)
                    throw new Exception("err@执行方法获取到的WorkID或者WorkIDs不能为空");
                string[] strs = workids.Split(',');
                this.WorkID = Int64.Parse(strs[0]);
                doc = doc.Replace("@WorkIDs", workids);
            }
            GEEntity en = new GEEntity(func.FrmID, this.WorkID);

            #region 替换参数变量.
            if (doc.Contains("@") == true)
            {
                MapAttrs mattrs = new MapAttrs();
                mattrs.Retrieve(MapAttrAttr.FK_MapData, this.PKVal);
                foreach (MapAttr item in mattrs)
                {
                    if (doc.Contains("@") == false)
                        break;
                    if (item.UIContralType == UIContralType.TB)
                    {
                        doc = doc.Replace("@" + item.KeyOfEn, this.GetRequestVal("TB_" + item.KeyOfEn));
                        continue;
                    }

                    if (item.UIContralType == UIContralType.DDL)
                    {
                        doc = doc.Replace("@" + item.KeyOfEn, this.GetRequestVal("DDL_" + item.KeyOfEn));
                        continue;
                    }


                    if (item.UIContralType == UIContralType.CheckBok)
                    {
                        doc = doc.Replace("@" + item.KeyOfEn, this.GetRequestVal("CB_" + item.KeyOfEn));
                        continue;
                    }

                    if (item.UIContralType == UIContralType.RadioBtn)
                    {
                        doc = doc.Replace("@" + item.KeyOfEn, this.GetRequestVal("RB_" + item.KeyOfEn));
                        continue;
                    }
                }
            }
            #endregion 替换参数变量.

            doc = BP.WF.Glo.DealExp(doc, en, null); //替换里面的内容.

            #region 开始执行SQLs.
            try
            {
                doc += "&MethodName=" + func.MethodID;
                DataType.ReadURLContext(doc, 99999);
                if (func.MsgSuccess.Equals(""))
                    func.MsgSuccess = "执行成功.";

                return func.MsgSuccess;
            }
            catch (Exception ex)
            {
                string de = DealException(ex, doc);
                if (DataType.IsNullOrEmpty(de) == false)
                {
                    return de;
                }
                if (func.MsgErr.Equals(""))
                    func.MsgErr = "执行失败.";

                return "err@" + func.MsgErr + " @ " + ex.Message;
            }
            #endregion 开始执行SQLs.

            BP.CCBill.Dev2Interface.WriteTrack(this.FrmID, this.WorkID.ToString(), "执行方法", func.Name);

            return "err@" + func.MethodDocTypeOfFunc + ",执行的类型没有解析.";
        }
    }
}
