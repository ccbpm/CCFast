using System;
using System.Collections.Generic;
using System.Collections;
using System.Data;
using BP.WF;
using BP.En;
using BP.DA;
using BP.Web;
using BP.Sys;
using BP.WF.Template;
using Aliyun.OSS;
using Org.BouncyCastle.Bcpg;
using BP.Difference;
using BP.CCBill.Template;
using NPOI.SS.Formula.Functions;
using System.Runtime.CompilerServices;
using BP.WF.DTS;
using NPOI.OpenXmlFormats.Dml;
using System.Net;
using System.IO;
using BP.WF.Admin;
using static NPOI.HSSF.Util.HSSFColor;
using System.Security.Cryptography;
using iTextSharp.text.html;

namespace BP.CCBill
{
    /// <summary>
    /// 接口调用
    /// </summary>
    public class Dev2Interface
    {
        /// <summary>
        /// 增加日志
        /// </summary>
        /// <param name="at"></param>
        /// <param name="frmID"></param>
        /// <param name="workID"></param>
        /// <param name="msg"></param>
        /// <returns></returns>
        public static void WriteTrack(string frmID, string frmWorkID, string at, string msg, string paras = null,
            string funcNo = null, string funcName = null, int nodeID = 0, Int64 workIDOfFlow = 0, string frmName = "")
        {
            BP.CCBill.Track tk = new BP.CCBill.Track();
            tk.WorkID = frmWorkID;
            tk.FrmID = frmID;
            tk.FrmName = frmName;
            tk.ActionType = at;

            switch (at)
            {
                case FrmActionType.BBS:
                    tk.ActionTypeText = "评论";
                    break;
                case FrmActionType.Create:
                    tk.ActionTypeText = "创建";
                    break;
                case FrmActionType.DataVerReback:
                    tk.ActionTypeText = "数据版本";
                    break;
                case FrmActionType.Save:
                    tk.ActionTypeText = "保存";
                    break;
                case FrmActionType.StartFlow:
                    tk.ActionTypeText = "发起流程";
                    break;
                case "Func":
                    tk.ActionTypeText = "执行功能"; //@gaoxin.
                    break;
                case "Info":
                    tk.ActionTypeText = "信息"; //@gaoxin.
                    break;
                default:
                    tk.ActionTypeText = "其他";
                    break;
            }

            tk.Rec = WebUser.No;
            tk.RecName = WebUser.Name;
            tk.DeptNo = WebUser.DeptNo;
            tk.DeptName = WebUser.DeptName;

            // 流程信息。
            tk.WorkIDOfFlow = workIDOfFlow;
            tk.NodeID = nodeID;
            if (funcName != null)
                tk.FuncName = funcName;
            if (funcNo != null)
                tk.FuncNo = funcNo;

            //tk.setMyPK(tk.FrmID + "_" + tk.WorkID + "_" + tk.Rec + "_" + (int)BP.CCBill.FrmActionType.BBS;
            tk.Msg = msg;
            tk.RDT = DataType.CurrentDateTime;
            if (paras != null)
                tk.SetValByKey("AtPara", paras); //@gaoxin.

            ////流程信息.
            //tk.NodeID = nodeID;
            //tk.NodeName = nodeName;
            //tk.FlowNo = flowNo;
            //tk.FlowName = flowName;
            //tk.FID = fid;
            tk.Insert();
        }
        /// <summary>
        /// 创建单据的WorkID
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="userNo"></param>
        /// <param name="htParas"></param>
        /// <param name="billNo"></param>
        /// <param name="pDictID"></param>
        /// <param name="pDictWorkID"></param>
        /// <returns></returns>
        public static Int64 CreateBlankBillID(string frmID, string userNo = null, Hashtable htParas = null, string pDictFrmID = null,
            Int64 pDictWorkID = 0)
        {
            if (userNo == null)
                userNo = WebUser.No;

            GenerBill gb = new GenerBill();
            int i = gb.Retrieve(GenerBillAttr.FrmID, frmID, GenerBillAttr.Starter, userNo, GenerBillAttr.BillState, 0);
            if (i == 1)
            {
                GERpt rpt1 = new GERpt(frmID);
                rpt1.OID = gb.WorkID;
                int count = rpt1.RetrieveFromDBSources();

                if (htParas != null)
                    rpt1.Copy(htParas);

                rpt1.SetValByKey("BillState", 0);
                rpt1.SetValByKey("Starter", gb.Starter);
                rpt1.SetValByKey("StarterName", gb.StarterName);
                rpt1.SetValByKey("DeptNo", WebUser.DeptNo);
                rpt1.SetValByKey("RDT", gb.RDT);
                rpt1.SetValByKey("Title", gb.Title);
                rpt1.SetValByKey("BillNo", gb.BillNo);
                if (SystemConfig.CCBPMRunModel != CCBPMRunModel.Single)
                    rpt1.SetValByKey("OrgNo", WebUser.OrgNo);
                if (pDictFrmID != null)
                {
                    rpt1.SetValByKey("PWorkID", pDictWorkID);
                    rpt1.SetValByKey("PFrmID", pDictFrmID);
                }
                if (count == 0)
                    rpt1.InsertAsOID(gb.WorkID);
                else
                    rpt1.Update();
                return gb.WorkID;
            }


            FrmBill fb = new FrmBill(frmID);
            gb.WorkID = DBAccess.GenerOID("WorkID");
            gb.BillState = BillState.Blank; //初始化状态.
            gb.Starter = BP.Web.WebUser.No;
            gb.StarterName = BP.Web.WebUser.Name;
            gb.FrmName = fb.Name; //单据名称.
            gb.FrmID = fb.No; //单据ID

            //if (DataType.IsNullOrEmpty(billNo) == false)
            //    gb.BillNo = billNo; //BillNo
            gb.DeptNo = BP.Web.WebUser.DeptNo;
            gb.DeptName = BP.Web.WebUser.DeptName;
            gb.FrmTreeNo = fb.FormTreeNo; //单据类别.
            gb.RDT = DataType.CurrentDateTime;
            gb.IdxName = "启动";

            //父字典信息.
            if (pDictFrmID != null)
            {
                gb.PFrmID = pDictFrmID;
                gb.PWorkID = pDictWorkID;
            }

            //创建rpt.
            BP.WF.GERpt rpt = new BP.WF.GERpt(frmID);

            //设置标题.
            if (fb.EntityType == EntityType.FrmBill)
            {
                gb.Title = Dev2Interface.GenerTitle(fb.TitleRole, rpt);
                gb.BillNo = BP.CCBill.Dev2Interface.GenerBillNo(fb.BillNoFormat, gb.WorkID, null, frmID);
            }

            if (fb.EntityType == EntityType.EntityTree || fb.EntityType == EntityType.FrmDict)
            {
                rpt.EnMap.CodeStruct = fb.EnMap.CodeStruct;
                gb.BillNo = rpt.GenerNewNoByKey("BillNo");
                // BP.CCBill.Dev2Interface.GenerBillNo(fb.BillNoFormat, gb.WorkID, null, frmID);
            }
            gb.DirectInsert(); //执行插入.

            //如果.
            if (htParas != null)
                rpt.Copy(htParas);

            //更新基础的数据到表单表.
            // rpt = new BP.WF.GERpt(frmID);
            rpt.SetValByKey("BillState", (int)gb.BillState);
            rpt.SetValByKey("Starter", gb.Starter);
            rpt.SetValByKey("StarterName", gb.StarterName);
            rpt.SetValByKey("DeptNo", WebUser.DeptNo);
            rpt.SetValByKey("RDT", gb.RDT);
            rpt.SetValByKey("Title", gb.Title);
            rpt.SetValByKey("BillNo", gb.BillNo);
            if (SystemConfig.CCBPMRunModel != CCBPMRunModel.Single)
                rpt.SetValByKey("OrgNo", WebUser.OrgNo);
            if (pDictFrmID != null)
            {
                rpt.SetValByKey("PWorkID", pDictWorkID);
                rpt.SetValByKey("PFrmID", pDictFrmID);
            }

            rpt.OID = gb.WorkID;
            rpt.InsertAsOID(gb.WorkID);

            BP.CCBill.Dev2Interface.WriteTrack(frmID, rpt.OID.ToString(), FrmActionType.Create, "创建记录");
            return gb.WorkID;
        }
        /// <summary>
        /// 保存工作
        /// </summary>
        /// <param name="workID">单据ID</param>
        /// <param name="bodyData">主表数据:@Key1=Field1@Key2=Field2</param>
        /// <param name="dsDtls">从表:支持多个从表.</param>
        /// <param name="dsAths">附件:支持多个附件.</param>
        /// <returns>返回执行结果</returns>
        public static string Bill_Save(Int64 workID, string bodyData, DataSet dsDtls = null, DataSet dsAths = null)
        {
            #region 1. 赋值+保存主表数据.
            GenerBill gb = new GenerBill(workID);
            GEEntityOID wk = new GEEntityOID(gb.FrmID, workID);
            AtPara ap = new AtPara(bodyData);
            //Attrs attrs = wk.EnMap.Attrs;
            foreach (string str in ap.HisHT.Keys)
            {
                switch (str)
                {
                    case GERptAttr.OID:
                    case WorkAttr.MD5:
                    case WorkAttr.Emps:
                    case GERptAttr.FID:
                    case GERptAttr.FK_Dept:
                    case GERptAttr.Rec:
                    case GERptAttr.Title:
                        continue;
                    default:
                        break;
                }
                if (wk.Row.ContainsKey(str))
                {
                    wk.SetValByKey(str, ap.HisHT[str]);
                }
                else
                {
                    wk.Row.Add(str, ap.HisHT[str]);
                }
            }
            wk.OID = workID;
            //  en.Rec = WebUser.No;
            // en.SetValByKey(GERptAttr.FK_Dept, WebUser.DeptNo);
            //    ExecEvent.DoFrm(gb.FrmID EventListFrm.SaveBefore, en);
            wk.Update();
            #endregion 赋值+保存主表数据.


            #region 保存从表
            if (dsDtls != null)
            {
                MapDtls dtls = new MapDtls(gb.FrmID);

                //保存从表
                foreach (DataTable dt in dsDtls.Tables)
                {
                    foreach (MapDtl dtl in dtls)
                    {
                        if (dt.TableName.Equals(dtl.No) == false)
                        {
                            if (DataType.IsNullOrEmpty(dtl.Alias) == true)
                                continue;
                            if (DataType.IsNullOrEmpty(dtl.Alias) == false && dt.TableName.Equals(dtl.Alias) == false)
                                continue;
                        }

                        //获取dtls
                        GEDtls daDtls = new GEDtls(dtl.No);
                        daDtls.Delete(GEDtlAttr.RefPK, workID); // 清除现有的数据.

                        // 为从表复制数据.
                        foreach (DataRow dr in dt.Rows)
                        {
                            GEDtl daDtl = daDtls.GetNewEntity as GEDtl;
                            daDtl.RefPK = workID.ToString();
                            //明细列.
                            foreach (DataColumn dc in dt.Columns)
                            {
                                //设置属性.
                                daDtl.SetValByKey(dc.ColumnName, dr[dc.ColumnName]);
                            }

                            daDtl.ResetDefaultVal();

                            daDtl.RefPK = workID.ToString();
                            daDtl.RDT = DataType.CurrentDateTime;

                            //执行保存.
                            daDtl.InsertAsOID(DBAccess.GenerOID("Dtl")); //插入数据.
                        }
                    }
                }
            }
            #endregion 保存从表结束

            #region 保存附件.
            if (dsAths != null)
            {
                //定义对象.
                FrmAttachmentDBs dbs = new FrmAttachmentDBs();
                FrmAttachments aths = new FrmAttachments(gb.FrmID);
                //保存从表
                foreach (DataTable dt in dsAths.Tables)
                {
                    foreach (FrmAttachment ath in aths)
                    {
                        if (dt.TableName.Equals(ath.NoOfObj) == false)
                            continue;
                        if (ath.FrmID.Equals(gb.FrmID) == false)
                            continue;

                        dbs.Delete(FrmAttachmentDBAttr.RefPKVal, workID, FrmAttachmentDBAttr.FK_FrmAttachment, gb.FrmID + "_" + dt.TableName); // 清除现有的数据.
                                                                                                                                               //临时路径
                        string rootTempPath = SystemConfig.PathOfTemp + workID;
                        if (System.IO.Directory.Exists(rootTempPath) == false)
                            System.IO.Directory.CreateDirectory(rootTempPath);
                        string pkval = workID.ToString();

                        // 复制数据.
                        foreach (DataRow dr in dt.Rows)
                        {
                            string fileName = dr[0].ToString(); //文件名称. 我的附件.
                            string fileUrl = dr[1].ToString();  //文件路径. http://xxx.xxx.xxx/myfile.doc;
                            if (DataType.IsNullOrEmpty(fileUrl) == true || DataType.IsNullOrEmpty(fileName) == true)
                                return "err@附件的存储的URL或者附件名称不能为空";
                            //保存数据.
                            if (ath.AthSaveWay == AthSaveWay.SaveLink)
                            {
                                FrmAttachmentDB athDB = new FrmAttachmentDB();
                                athDB.setMyPK(DBAccess.GenerGUID());
                                athDB.Sort = "";
                                //  athDB.NodeID = gwf.NodeID;
                                athDB.RefPKVal = pkval;
                                athDB.FrmID = ath.FrmID;
                                athDB.FK_FrmAttachment = ath.MyPK;
                                athDB.FileName = fileName;
                                athDB.FileExts = fileName.Substring(fileName.LastIndexOf("."));
                                FileWebRequest myReq = (FileWebRequest)WebRequest.Create(fileUrl);
                                FileWebResponse myRes = (FileWebResponse)myReq.GetResponse();
                                float len = myRes.ContentLength / 1024;
                                myRes.Close();
                                athDB.FileSize = len;
                                athDB.FileFullName = fileUrl;
                                athDB.RDT = DataType.CurrentDateTimess;
                                athDB.Rec = BP.Web.WebUser.No;
                                athDB.RecName = BP.Web.WebUser.Name;
                                athDB.DeptNo = WebUser.DeptNo;
                                athDB.DeptName = WebUser.DeptName;
                                athDB.Insert();
                                continue;
                            }
                            try
                            {
                                //1.下载文件到本机.
                                string tempPath = rootTempPath + "/" + fileName;
                                //请求网络路径地址
                                System.Net.HttpWebRequest request = (System.Net.HttpWebRequest)System.Net.WebRequest.Create(fileUrl);
                                request.Timeout = 5000; // 超时时间
                                                        //获得请求结果
                                System.Net.HttpWebResponse response = (System.Net.HttpWebResponse)request.GetResponse();

                                Stream stream = response.GetResponseStream();
                                //先创建文件
                                Stream sos = new System.IO.FileStream(tempPath, System.IO.FileMode.Create);
                                byte[] img = new byte[1024];
                                int total = stream.Read(img, 0, img.Length);
                                while (total > 0)
                                {
                                    //之后再输出内容
                                    sos.Write(img, 0, total);
                                    total = stream.Read(img, 0, img.Length);
                                }
                                stream.Close();
                                stream.Dispose();
                                sos.Close();
                                sos.Dispose();

                                //保存附件到服务器
                                BP.WF.Dev2Interface.CCForm_AddAth(0, gb.FrmID, workID, ath.MyPK, ath.FrmID, tempPath, fileName, null, 0, 0);

                            }
                            catch (Exception ex)
                            {
                            }
                        }
                    }
                }
            }
            #endregion 保存附件.

            return "保存成功.";
        }
        /// <summary>
        /// 创建一个空白的实体.
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="userNo"></param>
        /// <param name="htParas"></param>
        /// <returns></returns>
        public static string CreateBlankEntityNoName(string frmID, string userNo, Hashtable htParas)
        {
            if (userNo == null)
                userNo = WebUser.No;

            // 创建一个实体, 先检查一下是否有空白的数据.
            GEEntityNoName rpt = new GEEntityNoName(frmID);
            int i = rpt.Retrieve("RecNo", userNo, "EntityState", 0);
            if (i >= 1)
            {
                if (htParas != null)
                    rpt.Copy(htParas);

                rpt.SetValByKey("RDT", DataType.CurrentDate);
                if (SystemConfig.CCBPMRunModel != CCBPMRunModel.Single)
                    rpt.SetValByKey("OrgNo", WebUser.OrgNo);
                rpt.Update();
                return rpt.No; //如果有空白的数据，就返回给他.
            }

            //执行copy数据.
            if (htParas != null)
                rpt.Copy(htParas);

            FrmEntityNoName fb = new FrmEntityNoName(frmID);

            //更新基础的数据到表单表.
            rpt.SetValByKey("EntityState", 0);
            rpt.SetValByKey("RecNo", WebUser.No);
            rpt.SetValByKey("RecName", WebUser.Name);
            rpt.SetValByKey("DeptNo", WebUser.DeptNo);
            rpt.SetValByKey("OrgNo", WebUser.OrgNo);
            rpt.SetValByKey("RDT", DataType.CurrentDate);

            //设置编号生成规则.
            rpt.EnMap.CodeStruct = fb.BillNoFormat; // "4";//  fb.BillNoFormat;

            //rpt.SetValByKey("Title", gb.Title);
            rpt.SetValByKey("No", rpt.GenerNewNoByKey("No"));
            rpt.ResetDefaultVal();
            rpt.Insert();

            //  rpt.InsertAsNo(rpt.OID);
            BP.CCBill.Dev2Interface.WriteTrack(frmID, rpt.No, FrmActionType.Create, "创建记录");
            return rpt.No;
        }
        /// <summary>
        /// 执行发送
        /// </summary>
        /// <param name="frmID">节点ID</param>
        /// <param name="workID">工作ID</param>
        /// <param name="toEmpNo">到达的人员编号,如果为空就按照默认的来设置。</param>
        /// <returns>返回执行结果</returns>
        public static string Bill_Send(string frmID, Int64 workID, string toEmpNo = null)
        {
            #region 处理将要发送到的人员.
            if (DataType.IsNullOrEmpty(toEmpNo) == true)
            {
                string sql = "SELECT EmpNo FROM Frm_GenerBillWorker WHERE WorkID=" + workID + " ORDER BY Idx";
                DataTable dt = DBAccess.RunSQLReturnTable(sql);
                if (dt.Rows.Count == 0)
                {
                }
            }
            #endregion 处理将要发送到的人员.

            return "";
        }
        /// <summary>
        /// 设置审核模式: 启动单据后设置审核模式.
        /// </summary>
        /// <param name="frmID">表单信息</param>
        /// <param name="workID">工作ID</param>
        /// <param name="toEmpNo">设置内容</param>
        /// <returns></returns>
        public static string Bill_InitBillCheckModel(string frmID, Int64 workID)
        {
            FrmBill frmBill = new FrmBill(frmID);

            //如果没有设置审核模式.
            if (frmBill.BillCheckModel.Equals("None") || frmBill.BillCheckModel.Equals("0") || DataType.IsNullOrEmpty(frmBill.BillCheckModel))
            {
                return "info@当前没有设置审核模式.";
            }
            //按照固定人员审核.
            if (frmBill.BillCheckModel.Equals("ByEmpNos") == true)
            {
                string empNos = frmBill.BillCheckTag;
                return Bill_CheckerGoToOrder(frmID, workID, empNos);
            }

            //按照SQL固定人员审核.
            if (frmBill.BillCheckModel.Equals("BySQL") == true)
            {
                string sql = frmBill.BillCheckTag;
                GEEntity en = new GEEntity(frmID, workID);

                sql = BP.WF.Glo.DealSQLExp(sql, en, null);
                DataTable dt = DBAccess.RunSQLReturnTable(sql);
                string empNos = "";
                foreach (DataRow dr in dt.Rows)
                {
                    empNos += dr[0].ToString() + ",";
                }
                return Bill_CheckerGoToOrder(frmID, workID, empNos);
            }

            //按照：流程审核.
            if (frmBill.BillCheckModel.Equals("ByFlowNo") == true)
            {
                string flowNo = frmBill.BillCheckTag;
                GEEntity en = new GEEntity(frmID, workID);

                Int64 flowWorkID = en.GetParaInt("WorkID", 0);

                //创建一个空白的workid.
                if (flowWorkID == 0)
                    flowWorkID = BP.WF.Dev2Interface.Node_CreateBlankWork(flowNo);

                //设置参数.
                BP.WF.Dev2Interface.Node_SaveWork(flowWorkID, en.Row);
                //设置草稿.
                BP.WF.Dev2Interface.Node_SetDraft(flowWorkID);

                //返回流程信息,调用流程.
                return "@FlowNo=" + flowNo + "@WorkID=" + flowWorkID;
            }
            return "";
        }
        /// <summary>
        /// 审核人预置 preplace 
        /// </summary>
        /// <param name="workID">工作ID</param>
        /// <param name="empNosOfChecker"></param>
        /// <returns>执行结果</returns>
        public static string Bill_PreplaceChecker(Int64 workID, string empNosOfChecker)
        {
            GenerBill gb = new GenerBill(workID);
            if (gb.BillState == BillState.FlowOver)
                throw new Exception("err@该单据已经完成，您不能设置审核人员.");
            gb.SetPara("PreplaceChecker", empNosOfChecker);
            if (gb.BillState == BillState.Blank)
                gb.BillState = BillState.Editing;
            gb.Update();

            return "审核人预置成功，在用户提交的时候,就显示这些处理人.";
        }
        /// <summary>
        /// 预置审核人员列表（使用接口为人员设置路径）
        /// 1. 仅仅是预置处理人，如果需要启动审批，则要调用审批流程。
        /// </summary>
        /// <param name="frmID">表单ID</param>
        /// <param name="workID">工作ID</param>
        /// <param name="empNosOfChecker">审核人员,格式为:zhangsan,lisi,wangwu</param>
        /// <returns>返回执行结果</returns>
        /// <exception cref="Exception"></exception>
        public static string Bill_CheckerInit(Int64 workID, string empNosOfChecker)
        {
            empNosOfChecker = empNosOfChecker.Trim();
            GenerBill gb = new GenerBill(workID);
            if (gb.BillState == BillState.FlowOver)
                throw new Exception("err@该单据已经完成，您不能设置审核人员.");

            try
            {
                //删除现在已经有的人员.
                DBAccess.RunSQL("DELETE FROM Frm_GenerWorker WHERE WorkID=" + workID);
            }
            catch (Exception ex)
            {
                GenerWorker lis2t = new GenerWorker();
                lis2t.CheckPhysicsTable();
            }

            //检查人员是否正确？
            BP.Port.Emps emps = new BP.Port.Emps();
            string[] strs = empNosOfChecker.Split(',');
            string err = "";
            foreach (string empNo in strs)
            {
                if (DataType.IsNullOrEmpty(empNo) == true)
                    continue;
                BP.Port.Emp emp = new BP.Port.Emp();
                emp.No = empNo;
                if (emp.RetrieveFromDBSources() == 0)
                {
                    err += " 人员账号错误:[" + empNo + "],不存在.";
                }
                emps.AddEntity(emp); //增加进去.
            }
            if (DataType.IsNullOrEmpty(err) == false)
                throw new Exception("设置审核人员错误:" + err);

            GenerWorker list = new GenerWorker();
            list.SetValByKey("FrmID", gb.FrmID);
            list.SetValByKey("WorkID", workID);

            //审核人员.
            int idx = 0;
            string names = "";
            foreach (BP.Port.Emp emp in emps)
            {
                idx++;

                string mypk = workID + "_" + emp.No + "_" + idx;
                list.setMyPK(mypk);
                list.SetValByKey("EmpNo", emp.No);

                if (idx == 1)
                    list.SetValByKey("PassSta", 1); //设置第一个待办.
                else
                    list.SetValByKey("PassSta", 0);
                list.Insert(); //设置审批人员.

                if (idx == 1)
                    names += "" + emp.No + "," + emp.Name;
                else
                    names += "、" + emp.No + "," + emp.Name;
            }
            string msg = "启动简易审核:下达给" + names;
            BP.CCBill.Dev2Interface.WriteTrack(gb.FrmID, workID.ToString(), "StartFlow", msg);
            return "启动成功.";
        }
        /// <summary>
        /// 设置审核人进行审核,进入审核队列.
        /// 1.发起审批.
        /// </summary>
        /// <param name="frmID">表单ID</param>
        /// <param name="workID">oid</param>
        /// <param name="empNosOfChecker">审核人,多个审核人用逗号分开，比如:zhangsan,lisi,wangwu</param>
        /// <returns>返回设置结果</returns>
        public static string Bill_CheckerGoToOrder(string frmID, Int64 workID, string empNosOfChecker)
        {
            empNosOfChecker = empNosOfChecker.Trim();
            empNosOfChecker = empNosOfChecker.Replace(",,", ",");
            empNosOfChecker = empNosOfChecker.Replace(",,", ",");
            empNosOfChecker = empNosOfChecker.Replace(",,", ",");

            GenerBill gb = new GenerBill(workID);
            if (gb.BillState == BillState.FlowOver)
                throw new Exception("err@该单据已经完成，您不能设置审核人员.");
            try
            {
                //删除现在已经有的人员.
                DBAccess.RunSQL("DELETE FROM Frm_GenerWorker WHERE WorkID=" + workID);
            }
            catch (Exception ex)
            {
                GenerWorker lis2t = new GenerWorker();
                lis2t.CheckPhysicsTable();
            }

            //检查人员是否正确？
            BP.Port.Emps emps = new BP.Port.Emps();
            string[] strs = empNosOfChecker.Split(',');
            string err = "";
            foreach (string empNo in strs)
            {
                if (DataType.IsNullOrEmpty(empNo) == true)
                    continue;
                BP.Port.Emp emp = new BP.Port.Emp();
                emp.No = empNo;
                if (emp.RetrieveFromDBSources() == 0)
                {
                    err += "\t\n人员账号错误:[" + empNo + "],不存在.";
                }
                emps.AddEntity(emp); //增加进去.
            }
            if (DataType.IsNullOrEmpty(err) == false)
                throw new Exception("err@设置审核人员错误:" + err);

            GenerWorker list = new GenerWorker();
            list.SetValByKey("FrmID", frmID);
            list.SetValByKey("WorkID", workID);
            list.SetValByKey("EmpNo", WebUser.No);
            list.SetValByKey("EmpName", WebUser.Name); //设置人员.
            list.SetValByKey("DeptNo", WebUser.DeptNo);
            list.SetValByKey("DeptName", WebUser.DeptName);
            list.SetValByKey("Idx", 0); //设置顺序号.
            list.SetValByKey("PassSta", 2); //审核通过.
            list.SetValByKey("CheckerNote", "发起审核"); //审核通过.
            list.SetValByKey("RDT", DataType.CurrentDateTime); //审核通过.
            list.SetValByKey("SendDT", DataType.CurrentDateTime); //审核日期.
            list.SetValByKey("MyPK", workID + "_" + WebUser.No + "_0"); //设置主键.
            list.Insert();

            list.SetValByKey("SendDT", "无"); //审核日期.

            //审核人员.
            int idx = 0;
            string names = "";
            string currNos = "";
            string currNames = "";
            string currNoNames = "";
            foreach (BP.Port.Emp emp in emps)
            {
                idx++;
                string mypk = workID + "_" + emp.No + "_" + idx;
                list.setMyPK(mypk);
                list.SetValByKey("EmpNo", emp.No);
                list.SetValByKey("EmpName", emp.Name); //设置人员.
                list.SetValByKey("DeptNo", emp.DeptNo);
                list.SetValByKey("DeptName", emp.DeptText);
                list.SetValByKey("Idx", idx); //设置顺序号.

                if (idx == 1)
                    list.SetValByKey("PassSta", 0); //设置第一个待办.
                else
                    list.SetValByKey("PassSta", 1); //等待审核.
                list.Insert(); //设置审批人员.

                if (idx == 1)
                {
                    currNos = emp.No;
                    currNames = emp.Name;
                    names += "" + emp.Name;
                    currNoNames += emp.No + "," + emp.Name + ";";
                }
                else
                {
                    names += "、" + emp.Name;
                    currNoNames += emp.No + "," + emp.Name + ";";
                }
            }

            gb.BillState = BillState.Checking;
            //设置当前审核人的信息.
            gb.SetValByKey(GenerBillAttr.CurrIdx, 1);
            gb.SetValByKey(GenerBillAttr.CurrCheckerNos, currNos);
            gb.SetValByKey(GenerBillAttr.CurrCheckerNames, currNames);
            gb.SetValByKey(GenerBillAttr.Emps, WebUser.No + "," + WebUser.Name + ";");
            gb.Update();  //设置审核状态.

            string msg = "启动简易审核:下达给:" + currNoNames;
            BP.CCBill.Dev2Interface.WriteTrack(frmID, workID.ToString(), "StartFlow", msg);
            return msg;
        }
        /// <summary>
        /// 创建一个实体ID
        /// </summary>
        /// <param name="frmID">实体ID</param>
        /// <param name="userNo">用户编号</param>
        /// <param name="htParas">参数</param>
        /// <returns>一个实例的workid</returns>
        public static Int64 CreateBlankDictID(string frmID, string userNo, Hashtable htParas)
        {
            if (userNo == null)
                userNo = WebUser.No;
            // 创建一个实体, 先检查一下是否有空白的数据.
            GERpt rpt = new GERpt(frmID);
            int i = rpt.Retrieve("Starter", userNo, "BillState", 0);
            if (i >= 1)
            {
                if (htParas != null)
                    rpt.Copy(htParas);

                rpt.SetValByKey("RDT", DataType.CurrentDate);
                rpt.FID = 0;
                if (SystemConfig.CCBPMRunModel != CCBPMRunModel.Single)
                    rpt.SetValByKey("OrgNo", WebUser.OrgNo);
                rpt.Update();
                return rpt.OID; //如果有空白的数据，就返回给他.
            }


            //执行copy数据.
            if (htParas != null)
                rpt.Copy(htParas);

            FrmBill fb = new FrmBill(frmID);

            //更新基础的数据到表单表.
            rpt.SetValByKey("BillState", 0);
            rpt.SetValByKey("Starter", WebUser.No);
            rpt.SetValByKey("StarterName", WebUser.Name);
            rpt.SetValByKey("DeptNo", WebUser.DeptNo);
            rpt.SetValByKey("DeptName", WebUser.DeptName);
            rpt.SetValByKey("RDT", DataType.CurrentDate);
            if (SystemConfig.CCBPMRunModel != CCBPMRunModel.Single)
                rpt.SetValByKey("OrgNo", WebUser.OrgNo);
            //设置编号生成规则.
            //  rpt.EnMap.CodeStruct = "4";// fb.BillNoFormat; 

            string title = Dev2Interface.GenerTitle(fb.TitleRole, rpt);
            rpt.SetValByKey("Title", title);
            //   rpt.SetValByKey("BillNo", rpt.GenerNewNoByKey("BillNo"));
            rpt.OID = DBAccess.GenerOID("WorkID");
            rpt.ResetDefaultVal();
            rpt.FID = 0;
            rpt.InsertAsOID(rpt.OID);

            //生成单据编号.
            string billNo = Dev2Interface.GenerBillNo(fb.BillNoFormat, rpt.OID, rpt, fb.No);
            rpt.SetValByKey("BillNo", billNo);
            rpt.Update(); //更新.

            GenerBill gb = new GenerBill();
            gb.Title = title;
            gb.BillNo = billNo;
            gb.WorkID = rpt.OID;
            gb.Starter = WebUser.No;
            gb.StarterName = WebUser.Name;
            gb.BillState = BillState.Blank;
            gb.DeptName = WebUser.DeptName;
            gb.DeptNo = WebUser.No;
            gb.FrmID = fb.No;
            gb.FrmName = fb.Name;
            gb.Insert();

            BP.CCBill.Dev2Interface.WriteTrack(frmID, rpt.OID.ToString(), FrmActionType.Create, "创建记录");

            return rpt.OID;
        }
        /// <summary>
        /// 保存实体数据
        /// </summary>
        /// <param name="frmID">表单ID</param>
        /// <param name="workid">工作ID</param>
        /// <param name="htParas">参数数据</param>
        /// <returns></returns>
        public static void SaveDictWork(string frmID, Int64 workid, Hashtable htParas)
        {
            // 创建一个实体, 先检查一下是否有空白的数据.
            GERpt rpt = new GERpt(frmID);
            rpt.OID = workid;
            if (rpt.RetrieveFromDBSources() == 0)
            {
                if (htParas != null)
                    rpt.Copy(htParas);

                //设置编号生成规则.
                FrmBill fb = new FrmBill(frmID);
                rpt.EnMap.CodeStruct = fb.BillNoFormat;
                rpt.SetValByKey("BillNo", rpt.GenerNewNoByKey("BillNo"));
                rpt.InsertAsOID(workid);
            }
            else
            {
                //执行copy数据.
                if (htParas != null)
                    rpt.Copy(htParas);
            }

            //更新基础的数据到表单表.
            rpt.SetValByKey("BillState", 100);
            rpt.SetValByKey("Starter", WebUser.No);
            rpt.SetValByKey("StarterName", WebUser.Name);
            rpt.SetValByKey("FK_Dept", WebUser.DeptNo);
            rpt.SetValByKey("RDT", DataType.CurrentDate);
            rpt.Update();

            BP.CCBill.Dev2Interface.WriteTrack(frmID, workid.ToString(), FrmActionType.Save, "执行保存");
        }
        /// <summary>
        /// 保存
        /// </summary>
        /// <param name="frmID">表单ID</param>
        /// <param name="workID">工作ID</param>
        /// <returns>返回保存结果</returns>
        public static string SaveBillWork(string frmID, Int64 workID)
        {
            FrmBill fb = new FrmBill(frmID);

            GenerBill gb = new GenerBill();
            gb.WorkID = workID;
            int i = gb.RetrieveFromDBSources();
            if (i == 0)
                return "";

            if (DataType.IsNullOrEmpty(fb.BillCheckModel) == true || fb.BillCheckModel.Equals("None") || gb.BillState == BillState.Blank || gb.BillState == BillState.Draft)
                gb.BillState = BillState.Editing;

            //创建rpt.
            BP.WF.GERpt rpt = new BP.WF.GERpt(gb.FrmID, workID);

            if (fb.EntityType == EntityType.EntityTree || fb.EntityType == EntityType.FrmDict)
            {
                gb.Title = rpt.Title;
                gb.Update();
                return "保存成功...";
            }
            //单据编号.
            if (DataType.IsNullOrEmpty(gb.BillNo) == true && !(fb.EntityType == EntityType.EntityTree || fb.EntityType == EntityType.FrmDict))
            {
                gb.BillNo = BP.CCBill.Dev2Interface.GenerBillNo(fb.BillNoFormat, workID, null, fb.PTable);
                //更新单据里面的billNo字段.
                if (DBAccess.IsExitsTableCol(fb.PTable, "BillNo") == true)
                    DBAccess.RunSQL("UPDATE " + fb.PTable + " SET BillNo='" + gb.BillNo + "' WHERE OID=" + workID);
            }

            //标题.
            if (DataType.IsNullOrEmpty(gb.Title) == true && !(fb.EntityType == EntityType.EntityTree || fb.EntityType == EntityType.FrmDict))
            {
                gb.Title = Dev2Interface.GenerTitle(fb.TitleRole, rpt);
                //更新单据里面的 Title 字段.
                if (DBAccess.IsExitsTableCol(fb.PTable, "Title") == true)
                    DBAccess.RunSQL("UPDATE " + fb.PTable + " SET Title='" + gb.Title + "' WHERE OID=" + workID);
            }

            gb.Update();

            //把通用的字段更新到数据库.
            rpt.Title = gb.Title;
            rpt.BillNo = gb.BillNo;
            rpt.SetValByKey("BillState", (int)gb.BillState);
            rpt.Update();

            BP.CCBill.Dev2Interface.WriteTrack(frmID, rpt.OID.ToString(), FrmActionType.Save, "保存");

            return "保存成功...";
        }
        /// <summary>
        /// 退回给指定的人
        /// </summary>
        /// <param name="workID">工作ID</param>
        /// <param name="toIdx">退回到节点</param>
        /// <param name="msg">退回信息</param>
        /// <returns>执行结果.</returns>
        public static string MyBill_ReturnWork(Int64 workID, int toIdx, string msg)
        {
            GenerBill gb = new GenerBill(workID);
            GenerWorkers wks = new GenerWorkers();
            if (toIdx == 0)
            {
                //如果退回到开始节点.
                gb.CurrIdx = 0;
                gb.SetValByKey("CurrCheckerNos", gb.Starter);
                gb.SetValByKey("CurrCheckerNames", gb.StarterName);
                gb.SetValByKey("SendDT", DataType.CurrentDateTime);
                gb.BillState = BillState.ReturnSta;
                string info2 = "退回成功,退回发起人[" + gb.Starter + "," + gb.StarterName + "] \t\n 退回原因:" + msg + "\t\n 退回人:" + WebUser.No + "," + WebUser.Name + " 日期:" + DataType.CurrentDateTime;
                gb.SetValByKey("Msg", info2);

                gb.Update();

                //@0=待办@1=未开始@2=已通过@3=退回
                string sql = "UPDATE FRM_GenerWorker SET PassSta=1 WHERE WorkID=" + workID + " AND Idx > 0";
                DBAccess.RunSQL(sql);

                //设置待办.
                sql = "UPDATE FRM_GenerWorker SET PassSta=0 WHERE WorkID=" + workID + " AND Idx = 0";
                DBAccess.RunSQL(sql);

                BP.CCBill.Dev2Interface.WriteTrack(gb.FrmID, workID.ToString(), "ReturnWork", "退回", info2);
                return info2;
            }

            wks.Retrieve("WorkID", workID, "Idx", gb.CurrIdx);//查询出来当前的Idx.
            if (wks.Count == 0)
                return "err@退回错误，数据错误，没有找到当前的数据.";

            GenerWorker fromWK = wks[0] as GenerWorker;
            fromWK.PassSta = PassSta.UnPass;
            fromWK.CheckerNote = msg;
            fromWK.Update(); //把退回的.

            wks.Retrieve("WorkID", workID, "Idx", toIdx);
            if (wks.Count == 0)
                return "err@退回错误，数据错误，没有找到要退回到的节点数据.";
            GenerWorker toWK = wks[0] as GenerWorker;
            toWK.PassSta = PassSta.ReturnSta;
            //   toWK.CheckerNote = msg;
            toWK.Update();

            string info = "退回信息:\t\n" + msg + "\t\n退回人:" + fromWK.EmpNo + "," + fromWK.EmpName + " 时间:" + DataType.CurrentDateTime;
            gb.CurrIdx = toIdx;
            gb.SetValByKey("CurrCheckerNos", toWK.EmpNo);
            gb.SetValByKey("CurrCheckerNames", toWK.EmpName);
            gb.SetValByKey("SendDT", DataType.CurrentDateTime);
            gb.SetValByKey("Msg", info);
            gb.BillState = BillState.ReturnSta; //退回中.
            gb.Update();
            BP.CCBill.Dev2Interface.WriteTrack(gb.FrmID, workID.ToString(), "ReturnWork", "退回", info);
            return "退回成功,退回到节点[" + toWK.Idx + "],退回给[" + toWK.EmpNo + "," + toWK.EmpName + "]";
        }
        /// <summary>
        /// 执行发送
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="workID"></param>
        /// <param name="msg"></param>
        /// <returns></returns>
        public static string MyBill_Send(string frmID, Int64 workID, string msg)
        {
            GenerBill gb = new GenerBill(workID);
            string mypk = gb.WorkID + "_" + WebUser.No + "_" + gb.CurrIdx;
            GenerWorker wk = new GenerWorker();
            wk.MyPK = mypk;
            if (wk.RetrieveFromDBSources() == 0)
                return "err@当前表单已经到达:" + gb.CurrCheckerNames + ",您不能在审核.";

            wk.PassSta = PassSta.Passed;
            wk.SendDT = DataType.CurrentDateTime;
            wk.Update();

            //写入审核意见.
            BP.CCBill.Dev2Interface.WriteTrack(frmID, workID.ToString(), "Check", msg);
            GenerWorkers lists = new GenerWorkers();
            lists.Retrieve("WorkID", workID, "Idx", gb.CurrIdx + 1, "PassSta", (int)PassSta.UnPass);
            if (lists.Count != 0)
            {
                string checkerNo = "";
                string checkerNames = "";
                string toEmps = "";
                foreach (GenerWorker item in lists)
                {
                    item.PassSta = PassSta.Checking;
                    item.RDT = DataType.CurrentDateTime; //设置当前的日期.
                    item.Update();
                    checkerNo += item.EmpNo + ",";
                    checkerNames += item.EmpName;
                    toEmps += item.EmpNo + "," + item.EmpName + ";";
                }
                gb.BillState = BillState.Checking;
                gb.SetValByKey("CurrIdx", gb.CurrIdx + 1);
                gb.SetValByKey("CurrCheckerNos", checkerNo);
                gb.SetValByKey("CurrCheckerNames", checkerNames);
                gb.SetValByKey("SendDT", DataType.CurrentDateTime);
                gb.SetValByKey("Sender", WebUser.Name);

                if (gb.Emps.Contains(WebUser.No + ",") == false)
                    gb.Emps += WebUser.No + "," + WebUser.Name + ";";
                gb.Update();
                BP.CCBill.Dev2Interface.WriteTrack(frmID, workID.ToString(), "Send", "单据审核发送,发送给" + checkerNames, checkerNo);
                return "您已经审核完成,单据流转给:" + toEmps;
            }
            //说明到了最后一个节点了.通知发起人,让其有一个待办.
            if (lists.Count == 0)
            {
                //删除所有的数据, 保留：不然回滚以后找不到人.
                //   DBAccess.RunSQL("DELETE FROM FRM_GenerWorker WHERE WorkID=" + workID);
                wk.EmpNo = gb.Starter;
                wk.EmpName = gb.StarterName;
                wk.PassSta = PassSta.Checking; //设置审核状态.

                mypk = gb.WorkID + "_" + gb.Starter + "_200";
                wk.SetValByKey("MyPK", mypk); //设置主键.
                wk.WorkID = workID;
                wk.Idx = 200;
                wk.Insert(); //最后一个节点结束.

                gb.SetValByKey("Msg", "单据审核完毕通知.");
            }
            if (gb.Emps.Contains(WebUser.No + ",") == false)
                gb.Emps += WebUser.No + "," + WebUser.Name + ";";
            gb.CurrIdx = 0;
            gb.SetValByKey("CurrCheckerNos", ""); //设置当前处理人为空. 
            gb.SetValByKey("CurrCheckerNames", "");
            gb.BillState = BillState.FlowOver;
            gb.SetValByKey("SendDT", DataType.CurrentDateTime);
            gb.Update(); //更新

            //调用事件.
            MapData md = new MapData(frmID);
            GEEntity en = new GEEntity(frmID, workID);

            //执行单据审核结束.
            string frmEvent = ExecEvent.DoFrm(md, EventListFrm.CheckOver, en);

            BP.CCBill.Dev2Interface.WriteTrack(frmID, workID.ToString(), EventListFrm.CheckOver, "审核完毕.");
            if (DataType.IsNullOrEmpty(frmEvent) == true)
                return "该单据已经归档,审核完毕,通知给发起人:[" + gb.Starter + "," + gb.StarterName + "]";
            else
                return "该单据已经归档,审核完毕,通知给发起人:[" + gb.Starter + "," + gb.StarterName + "]，事件消息:" + frmEvent;
        }
        /// <summary>
        /// 设置单据只读(审核)
        /// </summary>
        /// <param name="workID">实体ID</param>
        public static void MyBill_SetChecking(Int64 workID)
        {
            GenerBill gb = new GenerBill(workID);
            gb.BillState = BillState.Checking;
            gb.Update();

            GEEntityOID ge = new GEEntityOID(gb.FrmID, gb.WorkID);
            ge.SetValByKey("BillState", (int)BillState.Checking);
            ge.Update();
        }
        /// <summary>
        /// 设置单据流程完成
        /// </summary>
        /// <param name="workID"></param>
        public static void MyBill_SetFrmOver(Int64 workID)
        {
            GenerBill gb = new GenerBill(workID);
            gb.BillState = BillState.FrmOver;
            gb.Update();

            GEEntityOID ge = new GEEntityOID(gb.FrmID, gb.WorkID);
            ge.SetValByKey("BillState", (int)BillState.FrmOver);
            ge.Update();
        }
        public static void MyBill_SetEditing(Int64 workID)
        {
            GenerBill gb = new GenerBill(workID);
            gb.BillState = BillState.Editing;
            gb.Update();

            GEEntityOID ge = new GEEntityOID(gb.FrmID, gb.WorkID);
            ge.SetValByKey("BillState", (int)BillState.Editing);
            ge.Update();
        }
        /// <summary>
        /// 撤销发送:简易审核的撤销发送.
        /// </summary>
        /// <param name="frmID">表单ID</param>
        /// <param name="workID">工作ID</param>
        /// <returns>执行结果</returns>
        public static string Bill_UnSend(string frmID, Int64 workID)
        {
            GenerBill gb = new GenerBill(workID);
            string emps = "," + gb.Emps + ",";
            if (emps.Contains(WebUser.No + ",") == false)
                return "err@您不是单据审核的参与人，您不能撤销.";

            //如果是单据的创始人.
            if (gb.Starter.Equals(WebUser.No) == true)
            {
                gb.BillState = BillState.Editing;
                gb.Emps = WebUser.No + "," + WebUser.Name + ";";
                gb.TodoEmps = "";
                gb.TodoEmpsNum = 0;
                gb.CurrIdx = 0;
                gb.CurrCheckerNos = "";
                gb.CurrCheckerNames = "";
                gb.Update();
                DBAccess.RunSQL("DELETE FROM FRM_GenerWorker WHERE WorkID=" + workID);

                //执行更新单据状态.
                BP.WF.GERpt myrpt = new BP.WF.GERpt(gb.FrmID, workID);
                myrpt.SetValByKey("BillState", (int)BillState.Editing);
                myrpt.Update();

                //执行撤销审核.
                MapData md1 = new MapData(frmID);
                GEEntity en1 = new GEEntity(frmID, workID);
                string msg1 = ExecEvent.DoFrm(md1, "UnSend", en1);

                BP.CCBill.Dev2Interface.WriteTrack(frmID, workID.ToString(), FrmActionType.UnSubmit, "发起人撤销提交，重新编辑。");

                if (DataType.IsNullOrEmpty(msg1) == true)
                    return "撤销成功，您是发起人可以继续编辑，然后提交。" + msg1;
                return "撤销成功，您是发起人可以继续编辑，然后提交。其他:" + msg1;
            }

            //@0=待办@1=未开始@2=已通过@3=退回

            //获取当前的人员的节点ID.
            string sql = "SELECT Idx FROM Frm_GenerWorker WHERE WorkID=" + workID + " AND EmpNo='" + WebUser.No + "' AND PassSta=2 ";
            int myIdx = DBAccess.RunSQLReturnValInt(sql, 0);
            if (myIdx == 0)
                return "err@系统错误，没有找到您的审核节点.";
            if (myIdx == 0)
                return "err@系统错误，不应该是0.";

            GenerWorkers gws = new GenerWorkers();
            gws.Retrieve("WorkID", workID, "Idx");

            //把中间的节点更新为未开始.
            bool isGotoMyIdx = false;
            bool isGotoCurrIdx = false;
            foreach (GenerWorker item in gws)
            {
                if (item.Idx == myIdx)
                    isGotoMyIdx = true;

                if (item.Idx == gb.CurrIdx)
                    isGotoCurrIdx = true;

                if (isGotoMyIdx && isGotoCurrIdx)
                    DBAccess.RunSQL("UPDATE Frm_GenerWorker SET PassSta=1  WHERE  Idx=" + item.Idx + " AND WorkID=" + workID);
            }

            //设置未开始.
            DBAccess.RunSQL("UPDATE Frm_GenerWorker SET PassSta=1  WHERE  Idx=" + gb.CurrIdx + " AND WorkID=" + workID);

            //设置他是审核状态.
            DBAccess.RunSQL("UPDATE Frm_GenerWorker SET PassSta=0  WHERE  Idx=" + myIdx + " AND WorkID=" + workID);
            //  PassSta.
            //设置为归档状态.
            gb.BillState = BillState.Checking;
            gb.CurrIdx = myIdx;
            gb.CurrCheckerNos = WebUser.No;
            gb.CurrCheckerNames = WebUser.Name;
            gb.Emps = gb.Emps.Replace(WebUser.No + "," + WebUser.Name + ";", ""); // 1.0去掉审核人.
            gb.Update();

            //执行撤销审核.
            MapData md = new MapData(frmID);
            GEEntity en = new GEEntity(frmID, workID);
            string msg = ExecEvent.DoFrm(md, "UnSend", en);

            BP.CCBill.Dev2Interface.WriteTrack(frmID, workID.ToString(), FrmActionType.UnSubmit, "撤销提交");
            if (DataType.IsNullOrEmpty(msg) == true)
                return "撤销成功" + msg;
            else
                return "撤销成功:" + msg;
        }
        public static string UnSubmitWork(Int64 workID)
        {
            GenerBill gb = new GenerBill(workID);

            //设置为归档状态.
            gb.BillState = BillState.Editing;

            FrmBill fb = new FrmBill(gb.FrmID);

            //创建rpt.
            BP.WF.GERpt rpt = new BP.WF.GERpt(gb.FrmID, workID);

            if (fb.EntityType == EntityType.EntityTree || fb.EntityType == EntityType.FrmDict)
            {
                gb.Title = rpt.Title;
                gb.Update();
                return "提交成功...";
            }

            //单据编号.
            if (DataType.IsNullOrEmpty(gb.BillNo) == true && !(fb.EntityType == EntityType.EntityTree || fb.EntityType == EntityType.FrmDict))
            {
                gb.BillNo = BP.CCBill.Dev2Interface.GenerBillNo(fb.BillNoFormat, workID, null, fb.PTable);
                //更新单据里面的billNo字段.
                if (DBAccess.IsExitsTableCol(fb.PTable, "BillNo") == true)
                    DBAccess.RunSQL("UPDATE " + fb.PTable + " SET BillNo='" + gb.BillNo + "' WHERE OID=" + workID);
            }

            //标题.
            if (DataType.IsNullOrEmpty(gb.Title) == true && !(fb.EntityType == EntityType.EntityTree || fb.EntityType == EntityType.FrmDict))
            {
                gb.Title = Dev2Interface.GenerTitle(fb.TitleRole, rpt);
                //更新单据里面的 Title 字段.
                if (DBAccess.IsExitsTableCol(fb.PTable, "Title") == true)
                    DBAccess.RunSQL("UPDATE " + fb.PTable + " SET Title='" + gb.Title + "' WHERE OID=" + workID);
            }
            gb.Update();

            //把通用的字段更新到数据库.
            rpt.Title = gb.Title;
            rpt.BillNo = gb.BillNo;
            rpt.Update();


            //增加到日志.
            BP.CCBill.Dev2Interface.WriteTrack(gb.FrmID, workID.ToString(), FrmActionType.UnSubmit, "撤销提交.");
            return "提交成功...";
        }
        /// <summary>
        /// 提交
        /// </summary>
        /// <param name="frmID">表单ID</param>
        /// <param name="workID">工作ID</param>
        /// <returns>返回保存结果</returns>
        public static string SubmitWork(Int64 workID)
        {

            GenerBill gb = new GenerBill();
            gb.WorkID = workID;
            int i = gb.RetrieveFromDBSources();
            if (i == 0)
                return "";

            //设置为归档状态.
            gb.BillState = BillState.FrmOver;
            FrmBill fb = new FrmBill(gb.FrmID);
            //创建rpt.
            BP.WF.GERpt rpt = new BP.WF.GERpt(gb.FrmID, workID);

            if (fb.EntityType == EntityType.EntityTree || fb.EntityType == EntityType.FrmDict)
            {
                gb.Title = rpt.Title;
                gb.Update();
                return "提交成功...";
            }

            //单据编号.
            if (DataType.IsNullOrEmpty(gb.BillNo) == true && !(fb.EntityType == EntityType.EntityTree || fb.EntityType == EntityType.FrmDict))
            {
                gb.BillNo = BP.CCBill.Dev2Interface.GenerBillNo(fb.BillNoFormat, workID, null, fb.PTable);
                //更新单据里面的billNo字段.
                if (DBAccess.IsExitsTableCol(fb.PTable, "BillNo") == true)
                    DBAccess.RunSQL("UPDATE " + fb.PTable + " SET BillNo='" + gb.BillNo + "' WHERE OID=" + workID);
            }

            //标题.
            if (DataType.IsNullOrEmpty(gb.Title) == true && !(fb.EntityType == EntityType.EntityTree || fb.EntityType == EntityType.FrmDict))
            {
                gb.Title = Dev2Interface.GenerTitle(fb.TitleRole, rpt);
                //更新单据里面的 Title 字段.
                if (DBAccess.IsExitsTableCol(fb.PTable, "Title") == true)
                    DBAccess.RunSQL("UPDATE " + fb.PTable + " SET Title='" + gb.Title + "' WHERE OID=" + workID);
            }

            gb.Update();

            //把通用的字段更新到数据库.
            rpt.Title = gb.Title;
            rpt.BillNo = gb.BillNo;
            rpt.Update();
            BP.CCBill.Dev2Interface.WriteTrack(gb.FrmID, workID.ToString(), FrmActionType.Submit, "执行提交.");
            return "提交成功...";
        }
        /// <summary>
        /// 设置草稿
        /// </summary>
        /// <param name="workID">工作ID</param>
        /// <returns>返回保存结果</returns>
        public static string SaveAsDraft(Int64 workID)
        {
            GenerBill gb = new GenerBill(workID);
            if (gb.BillState != BillState.Blank)
                return "err@只有在None的模式下才能保存草稿。";

            gb.BillState = BillState.Draft;
            gb.Update();

            FrmBill frmBill = new FrmBill(gb.FrmID);

            //更新状态.
            DBAccess.RunSQL("UPDATE " + frmBill.PTable + " SET BillState=1 WHERE OID=" + workID);

            return "保存成功...";
        }
        /// <summary>
        /// 设置审核人员
        /// </summary>
        /// <param name="workID">工作ID</param>
        /// <param name="empNos">人员编号，按照顺序执行.</param>
        /// <returns>返回执行结果</returns>
        public static string MyBill_GenerWorkerByEmpNos(Int64 workID, string empNos)
        {
            GenerBill gb = new GenerBill(workID);
            if (gb.BillState == BillState.FlowOver)
                throw new Exception("err@当前是归档状态，您不能设置工作人员。");

            //删除现在的数据.
            DBAccess.RunSQL("DELETE FROM Frm_GenerWorker WHERE WorkID=" + workID);
            GenerWorker worker = new GenerWorker();
            string[] emps = empNos.Split(',');
            for (int i = 0; i < emps.Length; i++)
            {
                string empNo = emps[i].Trim();
                BP.Port.Emp emp = new Port.Emp(empNo);

                string mypk = i + "_" + workID + "_" + emp.No;
                worker.SetValByKey("Idx", i); //设置步骤.
                worker.SetValByKey("WorkID", gb.WorkID); //设置工作ID.
                worker.SetValByKey("EmpNo", empNo); //设置人员信息.
                worker.SetValByKey("EmpName", emp.Name);

                //设置状态.
                if (i == 0)
                {
                    worker.SetValByKey("PassSta", 0);
                    gb.SetValByKey("CurrCheckerNos", emp.No);
                    gb.SetValByKey("CurrCheckerNames", emp.Name);
                    gb.SetValByKey("CurrIdx", 1);
                }
                else
                    worker.SetValByKey("PassSta", 1); //为未执行的状态.
                worker.setMyPK(mypk); //设置主键.
                worker.Insert();
            }
            gb.BillState = BillState.Checking; //审核中.
            //设置人员.
            gb.Emps = WebUser.No + "," + WebUser.Name + ";";
            gb.Update(); //更新.

            return "设置成功.";
        }
        /// <summary>
        /// 删除单据
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="workID"></param>
        /// <returns>执行结果</returns>
        public static string MyBill_Delete(string frmID, Int64 workID)
        {
            //整理参数.
            DBRoles rols = new DBRoles();
            rols.Retrieve("FrmID", frmID, "DBRole", "RecDelete"); //获得删除规则.
            string mydepts = "" + WebUser.DeptNo + ","; //我的部门.
            string mystas = ""; //我的角色.
            DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.UserID + "'");
            foreach (DataRow dr in mydeptsDT.Rows)
            {
                mydepts += dr[0].ToString() + ",";
                mystas += dr[1].ToString() + ",";
            }
            FrmBill fb = new FrmBill(frmID);
            GEEntityOID billEn = new GEEntityOID(frmID, workID);

            //执行删除.
            return MyBill_DeleteExt(fb, rols, billEn, mydepts, mystas);
        }
        /// <summary>
        /// 删除单据
        /// </summary>
        /// <param name="fb">权限实体</param>
        /// <param name="dbRols">权限实体</param>
        /// <param name="dbRols">权限实体</param>
        /// <param name="billEn">单据实体</param>
        /// <param name="mydepts">部门编号</param>
        /// <param name="mystas">岗位编号</param>
        /// <returns></returns>
        private static string MyBill_DeleteExt(FrmBill fb, DBRoles dbRols, GEEntityOID billEn, string mydepts, string mystas)
        {
            if (dbRols.Count == 0)
            {
                billEn.Delete();
                return "单据:" + billEn.GetValStringByKey("BillNo") + ",标题:" + billEn.GetValStringByKey("Title") + ",删除成功.";
            }

            string starter = billEn.GetValStringByKey("Starter"); //发起人.
            string deptNo = billEn.GetValStringByKey("DeptNo");
            string orgNo = billEn.GetValStringByKey("OrgNo");
            string billNo = billEn.GetValStringByKey("BillNo");
            string title = billEn.GetValStringByKey("Title");

            //是否可以删除?
            bool isDelete = false;
            foreach (DBRole item in dbRols)
            {
                if (item.MarkID.Equals("None") == true)
                {
                    isDelete = true;
                    break;
                }
                if (item.MarkID.Equals("SelfOnly") == true && starter.Equals(WebUser.No) == true)
                {
                    isDelete = true;
                    break;
                }
                if (item.MarkID.Equals("DeptLeader") == true)
                {
                    string deptLeader = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Dept WHERE No='" + deptNo + "'");
                    if (deptLeader != null && deptLeader.Equals(WebUser.No) == true)
                    {
                        isDelete = true;
                        break;
                    }
                }
              
                if (item.MarkID.Equals("ByStations") == true && BP.DA.DataType.IsHaveIt(item.Docs, mystas) == true)
                {
                    isDelete = true;
                    break;
                }
                if (item.MarkID.Equals("ByDepts") == true && BP.DA.DataType.IsHaveIt(item.Docs, mydepts) == true)
                {
                    isDelete = true;
                    break;
                }
                if (item.MarkID.Equals("ByEmps") == true && BP.DA.DataType.IsHaveIt(item.Docs, "," + BP.Web.WebUser.No + ",") == true)
                {
                    isDelete = true;
                    break;
                }
                if (item.MarkID.Equals("Adminer") == true && BP.Web.WebUser.No.Equals("admin") == true)
                {
                    isDelete = true;
                    break;
                }
                if (item.MarkID.Equals("Admin2") == true && BP.Web.WebUser.IsAdmin == true)
                {
                    isDelete = true;
                    break;
                }
                if (item.MarkID.Equals("SQL") == true)
                {
                    string sql = BP.WF.Glo.DealExp(item.Docs, null, "");
                    if (DBAccess.RunSQLReturnValFloat(sql) > 0)
                    {
                        isDelete = true;
                        break;
                    }
                }
            }
            if (isDelete == false)
                return "您没有权限删除单号[" + billNo + "]标题为[" + title + "]的数据.";

            //检查是否是逻辑删除.
            if (dbRols.GetEntityByKey("MarkID", "DeleteByFlag") == null)
            {
                string sqls = "DELETE FROM Frm_GenerBill WHERE WorkID=" + billEn.OID;
                sqls += "@DELETE FROM Frm_GenerWorker WHERE WorkID=" + billEn.OID;
                sqls += "@DELETE FROM " + fb.PTable + " WHERE OID=" + billEn.OID;
                DBAccess.RunSQLs(sqls);
            }
            else
            {
                string sqls = "DELETE FROM Frm_GenerBill WHERE WorkID=" + billEn.OID;
                sqls += "@DELETE FROM Frm_GenerWorker WHERE WorkID=" + billEn.OID;
                sqls += "@UPDATE " + fb.PTable + " SET BillState=-1  WHERE OID=" + billEn.OID;
                DBAccess.RunSQLs(sqls);
            }
            return "单号[" + billNo + "]删除成功.";
        }

        public static string MyBill_DeleteBills(string frmID, string workIds)
        {
            //1. 整理参数.
            DBRoles rols = new DBRoles();
            rols.Retrieve("FrmID", frmID, "DBRole", "RecDelete");
            string mydepts = "" + WebUser.DeptNo + ","; //我的部门.
            string mystas = ""; //我的角色.
            DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.UserID + "'");
            foreach (DataRow dr in mydeptsDT.Rows)
            {
                mydepts += dr[0].ToString() + ",";
                mystas += dr[1].ToString() + ",";
            }
            FrmBill fb = new FrmBill(frmID);

            string[] workids = workIds.Split(',');
            string msg = "";
            int idx = 0;
            foreach (string workID in workids)
            {
                if (DataType.IsNullOrEmpty(workID) == true)
                    continue;

                GEEntityOID billEn = new GEEntityOID(frmID,  Int64.Parse( workID));

                //执行删除消息
                idx++;
                msg += "第"+idx+"条,删除消息:"+ MyBill_DeleteExt(fb, rols, billEn, mydepts, mystas);
            }
            return msg;
        }
        /// <summary>
        /// 删除实体
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="workID"></param>
        /// <returns></returns>
        public static string MyDict_Delete(string frmID, Int64 workID)
        {
            FrmBill fb = new FrmBill(frmID);
            string sql = "@DELETE FROM " + fb.PTable + " WHERE OID=" + workID;
            DBAccess.RunSQLs(sql);
            return "删除成功.";
        }
        /// <summary>
        /// 是否可以删除?
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="workID"></param>
        /// <param name="en"></param>
        /// <returns></returns>
        public static bool MyDict_IsDelete(string frmID, Int64 workID, Entity en)
        {
            return true;
        }
        /// <summary>
        /// 表单是否只读？
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="workID"></param>
        /// <param name="en"></param>
        /// <returns></returns>
        public static bool MyDict_IsReadonly(string frmID, Int64 workID, Entity en)
        {
            //获得该表单的控制权限.
            DataTable dtDBRole = DBAccess.RunSQLReturnTable("SELECT MarkID,Docs,AtPara FROM Frm_DBRole WHERE FrmID='" + frmID + "' AND DBRole='RecReadonly' AND IsEnable=1 ");
            if (dtDBRole.Rows.Count == 0)
                return false;

            //判断是否符合只读的规则.
            foreach (DataRow dr in dtDBRole.Rows)
            {
                string markID = dr[0].ToString();
                string docs = dr[1].ToString();
                string atPara = dr[2].ToString();
                if (markID.Equals("BySQL"))
                {
                    string sql = BP.Difference.Glo.DealExp(docs, en.Row);

                    float val = DBAccess.RunSQLReturnValFloat(sql);
                    if (val >= 0)
                        return true;
                }

                if (markID.Equals("ByField"))
                {
                    atPara = atPara.Replace("==", "=dengyu");
                    AtPara ap = new AtPara(atPara);
                    string attrKey = ap.GetValStrByKey("AttrKey");
                    string OperatorValue = ap.GetValStrByKey("OperatorValue");
                    string OperatorMark = ap.GetValStrByKey("OperatorMark");

                    string enVal = en.GetValStrByKey(attrKey);

                    //如果是等于.
                    if (OperatorMark.Equals("dengyu") == true)
                    {
                        if (enVal.Equals(OperatorValue) == true)
                            return true;
                    }
                    if (OperatorMark.Equals("budengyu") == true)
                    {
                        if (enVal.Equals(OperatorValue) == false)
                            return true;
                    }

                    if (OperatorMark.Equals("baohan") == true)
                    {
                        if (enVal.Contains(OperatorValue) == true)
                            return true;
                    }
                }
            }
            return false; //默认可以编辑，不是zhi'du 
        }
        /// <summary>
        /// 删除
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="nos"></param>
        /// <returns></returns>
        public static string MyEntityNoName_Deletes(string frmID, string nos)
        {
            Collection collection = new Collection();
            collection.No = frmID + "_Delete";
            collection.Retrieve();
            int deleteRole = collection.GetValIntByKey(CollectionAttr.IsEnable);
            MapData fb = new MapData(frmID);

            string[] strs = nos.Split(',');

            foreach (string no in strs)
            {
                if (string.IsNullOrEmpty(no) == true)
                    continue;

                if (deleteRole == 1)
                {
                    string sql = "DELETE FROM " + fb.PTable + " WHERE No='" + no + "'";
                    DBAccess.RunSQLs(sql);
                }
                if (deleteRole == 2)
                {
                    DBAccess.RunSQLs("UPDATE  " + fb.PTable + " SET EntityState=-1 WHERE No='" + no + "'");
                }
            }
            return "删除成功.";
        }
        /// <summary>
        /// 删除实体单据
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="workID"></param>
        /// <returns></returns>
        public static string MyDict_DeleteDicts(string frmID, string workIds)
        {
            Collection collection = new Collection();
            collection.No = frmID + "_Delete";
            collection.Retrieve();
            int deleteRole = collection.GetValIntByKey(CollectionAttr.IsEnable);
            FrmBill fb = new FrmBill(frmID);
            if (deleteRole == 1)
            {
                string sql = "DELETE FROM " + fb.PTable + " WHERE OID IN (" + workIds + ")";
                DBAccess.RunSQLs(sql);
            }
            if (deleteRole == 2)
            {
                DBAccess.RunSQLs("UPDATE  " + fb.PTable + " SET BillState=-1 WHERE OID IN (" + workIds + ")");
            }
            return "删除成功.";
        }
        /// <summary>
        /// 删除树形结构的实体表单
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="billNo"></param>
        /// <returns></returns>
        public static string MyEntityTree_Delete(string frmID, string billNo)
        {
            FrmBill fb = new FrmBill(frmID);
            string sql = "DELETE FROM " + fb.PTable + " WHERE BillNo='" + billNo + "' OR ParentNo='" + billNo + "'";
            DBAccess.RunSQLs(sql);
            return "删除成功.";
        }
        /// <summary>
        /// 表单数据复制
        /// </summary>
        /// <param name="frmID">表单ID</param>
        /// <param name="workid">WorkID</param>
        /// <returns>复制的主表数据</returns>
        public static string FlowFrm_Copy(string frmID, Int64 workid)
        {
            //旧的WorkID
            Int64 oldWorkID = workid;
            //复制主表数据（包括二进制）
            GEEntityOID en = new GEEntityOID(frmID, oldWorkID);
            en.SaveAsNew();
            //新的WorkID
            Int64 newWorkID = en.OID;

            //判断是否有从表，并进行复制
            MapDtls mds = new MapDtls(frmID);
            if (mds != null && mds.Count > 0)
            {
                //遍历从表s
                foreach (MapDtl mdtl in mds)
                {
                    //获取旧的从表数据
                    GEDtls dtls = new GEDtls(mdtl.No);
                    dtls.Retrieve(GEDtlAttr.RefPK, oldWorkID);

                    //遍历旧的从表数据
                    foreach (GEDtl gedtl in dtls)
                    {
                        //替换新的RefPK
                        gedtl.RefPK = newWorkID.ToString();
                        //执行插入，开始复制从表数据
                        gedtl.SaveAsNew();
                    }
                }
            }

            return en.ToJson();
        }
        /// <summary>
        /// 复制单据数据
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="workID"></param>
        /// <returns></returns>
        public static string MyBill_Copy(string frmID, Int64 workID)
        {
            //获取单据的属性
            FrmBill fb = new FrmBill(frmID);

            GenerBill gb = new GenerBill();
            gb.WorkID = DBAccess.GenerOID("WorkID");
            gb.BillState = BillState.Editing; //初始化状态.
            gb.Starter = BP.Web.WebUser.No;
            gb.StarterName = BP.Web.WebUser.Name;
            gb.FrmName = fb.Name; //单据名称.
            gb.FrmID = fb.No; //单据ID

            gb.FrmTreeNo = fb.FormTreeNo; //单据类别.
            gb.RDT = DataType.CurrentDateTime;
            gb.IdxName = "启动";

            //创建rpt.
            BP.WF.GERpt rpt = new BP.WF.GERpt(frmID, workID);

            //设置标题.
            gb.Title = Dev2Interface.GenerTitle(fb.TitleRole, rpt);
            gb.BillNo = BP.CCBill.Dev2Interface.GenerBillNo(fb.BillNoFormat, gb.WorkID, null, frmID);
            gb.DirectInsert(); //执行插入.

            //更新基础的数据到表单表.
            rpt.SetValByKey("BillState", (int)gb.BillState);
            rpt.SetValByKey("Starter", gb.Starter);
            rpt.SetValByKey("StarterName", gb.StarterName);
            rpt.SetValByKey("RDT", gb.RDT);
            rpt.SetValByKey("Title", gb.Title);
            rpt.SetValByKey("BillNo", gb.BillNo);
            rpt.OID = gb.WorkID;
            rpt.InsertAsOID(gb.WorkID);

            #region 复制其他数据.

            //复制明细。
            MapDtls dtls = new MapDtls(frmID);
            if (dtls.Count > 0)
            {
                foreach (MapDtl dtl in dtls)
                {
                    if (dtl.ItIsCopyNDData == false)
                        continue;

                    //new 一个实例.
                    GEDtl dtlData = new GEDtl(dtl.No);

                    GEDtls dtlsFromData = new GEDtls(dtl.No);
                    dtlsFromData.Retrieve(GEDtlAttr.RefPK, workID);
                    foreach (GEDtl geDtlFromData in dtlsFromData)
                    {
                        //是否启用多附件
                        FrmAttachmentDBs dbs = null;
                        if (dtl.ItIsEnableAthM == true)
                        {
                            //根据从表的OID 获取附件信息
                            dbs = new FrmAttachmentDBs();
                            dbs.Retrieve(FrmAttachmentDBAttr.RefPKVal, geDtlFromData.OID);
                        }

                        dtlData.Copy(geDtlFromData);
                        dtlData.RefPK = rpt.OID.ToString();
                        dtlData.InsertAsNew();
                        if (dbs != null && dbs.Count != 0)
                        {
                            //复制附件信息
                            FrmAttachmentDB newDB = new FrmAttachmentDB();
                            foreach (FrmAttachmentDB db in dbs)
                            {
                                newDB.Copy(db);
                                newDB.RefPKVal = dtlData.OID.ToString();
                                newDB.FID = dtlData.OID;
                                newDB.setMyPK(DBAccess.GenerGUID());
                                newDB.Insert();
                            }
                        }

                    }
                }

            }

            //获取附件组件、
            FrmAttachments athDecs = new FrmAttachments(frmID);
            //复制附件数据。
            if (athDecs.Count > 0)
            {
                foreach (FrmAttachment athDec in athDecs)
                {
                    FrmAttachmentDBs aths = new FrmAttachmentDBs();
                    aths.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, athDec.MyPK, FrmAttachmentDBAttr.RefPKVal, workID);
                    foreach (FrmAttachmentDB athDB in aths)
                    {
                        FrmAttachmentDB athDB_N = new FrmAttachmentDB();
                        athDB_N.Copy(athDB);
                        athDB_N.RefPKVal = rpt.OID.ToString();
                        athDB_N.setMyPK(DBAccess.GenerGUID());
                        athDB_N.Insert();
                    }
                }
            }
            #endregion 复制表单其他数据.

            BP.CCBill.Dev2Interface.WriteTrack(frmID, workID.ToString(), "复制", "执行复制");
            return "复制成功.";
        }
        public static DataTable DB_StartDicts()
        {
            string sql = "SELECT A.No,A.Name,B.Name AS SortName,A.Icon FROM Sys_MapData A, Sys_FormTree B ";
            sql += "  WHERE  A.FK_FormTree =B.No and A.EntityType=2  AND A.OrgNo='" + BP.Web.WebUser.OrgNo + "'";

            DataTable dt = DBAccess.RunSQLReturnTable(sql);

            if (SystemConfig.AppCenterDBFieldCaseModel != FieldCaseModel.None)
            {
                dt.Columns[0].ColumnName = "No";
                dt.Columns[1].ColumnName = "Name";
                dt.Columns[2].ColumnName = "SortName";
                dt.Columns[3].ColumnName = "Icon";
            }
            return dt;
        }
        /// <summary>
        /// 获得发起列表
        /// </summary>
        /// <param name="empID"></param>
        /// <returns></returns>
        public static DataTable DB_StartBills(string empID)
        {
            string sql = "SELECT A.No,A.Name,B.Name AS SortName,A.Icon FROM Sys_MapData A, Sys_FormTree B ";
            sql += "  WHERE  A.FK_FormTree =B.No and A.EntityType =1  AND A.OrgNo='" + BP.Web.WebUser.OrgNo + "' ";

            DataTable dt = DBAccess.RunSQLReturnTable(sql);

            if (SystemConfig.AppCenterDBFieldCaseModel != FieldCaseModel.None)
            {
                dt.Columns[0].ColumnName = "No";
                dt.Columns[1].ColumnName = "Name";
                dt.Columns[2].ColumnName = "SortName";
                dt.Columns[3].ColumnName = "Icon";
            }
            return dt;
        }
        /// <summary>
        /// 近期发起的单据
        /// </summary>
        /// <param name="empID"></param>
        /// <returns></returns>
        public static DataTable DB_Recent(string empID)
        {
            GenerBills gbs = new GenerBills();
            QueryObject qo = new QueryObject(gbs);

            qo.addLeftBracket();
            qo.AddWhere(GenerBillAttr.CurrCheckerNos, " NOT LIKE ", "%" + WebUser.No + "%");
            qo.addAnd();
            qo.AddWhere(GenerBillAttr.Emps, " LIKE ", "%" + WebUser.No + "%");
            qo.addRightBracket();

            qo.addAnd();
            qo.AddWhere(GenerBillAttr.BillState, " >= ", 1);
            qo.addOrderBy("RDT");
            qo.Top = 200;
            qo.DoQuery();
            return gbs.ToDataTableField();
        }
        /// <summary>
        /// 近期发起的单据
        /// </summary>
        /// <param name="empID"></param>
        /// <returns></returns>
        public static DataTable DB_Todolist(string empNo = null, string frmID = null)
        {
            if (empNo == null)
                empNo = WebUser.No;
            // PassSta.
            string sql = "";
            sql = "SELECT A.BillNo, A.WorkID,A.Title,A.FrmID,A.FrmName,A.Starter,A.StarterName,A.RDT,B.EmpNo,B.EmpName,B.DeptNo,B.DeptName,B.Idx,A.BillState,A.AtPara,";
            sql += " A.RDT,A.Sender,A.SendDT,A.SDTOfNode,B.RDT AS ADT ";
            sql += " FROM Frm_GenerBill A, FRM_GenerWorker B ";
            sql += " WHERE a.WorkID=B.WorkID AND (B.PassSta=3 OR B.PassSta=5 OR B.PassSta=0 ) ";
            sql += " AND B.EmpNo='" + empNo + "'";  //5=退回，3=审核中,0=待办审核中. 
            if (DataType.IsNullOrEmpty(frmID) == false)
                sql += " AND B.FrmID='" + frmID + "'";

            DataTable dt = null;
            try
            {
                dt = DBAccess.RunSQLReturnTable(sql);
            }
            catch
            {
                GenerBill gb = new GenerBill();
                gb.CheckPhysicsTable();
                GenerWorker gw = new GenerWorker();
                gw.CheckPhysicsTable();
                dt = DBAccess.RunSQLReturnTable(sql);
            }

            if (SystemConfig.AppCenterDBFieldCaseModel == FieldCaseModel.UpperCase)
            {
                dt.Columns["WORKID"].ColumnName = "WorkID";
                dt.Columns["TITLE"].ColumnName = "Title";
                dt.Columns["FRMID"].ColumnName = "FrmID";
                dt.Columns["BILLNO"].ColumnName = "BillNo";
                dt.Columns["ATPARA"].ColumnName = "AtPara";

                dt.Columns["RDT"].ColumnName = "RDT";
                dt.Columns["SENDER"].ColumnName = "Sender";
                dt.Columns["SENDDT"].ColumnName = "SendDT";
                dt.Columns["SDTOFNODE"].ColumnName = "SDTOfNode";

                dt.Columns["FRMNAME"].ColumnName = "FrmName";
                dt.Columns["STARTER"].ColumnName = "Starter";
                dt.Columns["STARTERNAME"].ColumnName = "StarterName";

                dt.Columns["RDT"].ColumnName = "RDT";
                dt.Columns["EMPNO"].ColumnName = "EmpNo";
                dt.Columns["EMPNAME"].ColumnName = "EmpName";

                dt.Columns["DEPTNO"].ColumnName = "DeptNo";
                dt.Columns["DEPTNAME"].ColumnName = "DeptName";
                dt.Columns["IDX"].ColumnName = "Idx";
            }

            if (SystemConfig.AppCenterDBFieldCaseModel == FieldCaseModel.Lowercase)
            {
                dt.Columns["workid"].ColumnName = "WorkID";
                dt.Columns["title"].ColumnName = "Title";
                dt.Columns["billno"].ColumnName = "BillNo";
                dt.Columns["atpara"].ColumnName = "AtPara";

                dt.Columns["frmid"].ColumnName = "FrmID";
                dt.Columns["frmname"].ColumnName = "FrmName";
                dt.Columns["starter"].ColumnName = "Starter";
                dt.Columns["startername"].ColumnName = "StarterName";

                dt.Columns["rdt"].ColumnName = "RDT";
                dt.Columns["sender"].ColumnName = "Sender";
                dt.Columns["senddt"].ColumnName = "SendDT";
                dt.Columns["sdtofnode"].ColumnName = "SDTOfNode";

                dt.Columns["rdt"].ColumnName = "RDT";
                dt.Columns["empno"].ColumnName = "EmpNo";
                dt.Columns["empname"].ColumnName = "EmpName";

                dt.Columns["deptno"].ColumnName = "DeptNo";
                dt.Columns["deptname"].ColumnName = "DeptName";
                dt.Columns["idx"].ColumnName = "Idx";
            }
            return dt;
        }
        /// <summary>
        /// 草稿列表
        /// </summary>
        /// <param name="frmID">单据ID</param>
        /// <param name="empID">操作员</param>
        /// <returns></returns>
        public static DataTable DB_Draft(string frmID, string empID)
        {
            if (DataType.IsNullOrEmpty(empID) == true)
                empID = BP.Web.WebUser.No;

            GenerBills bills = new GenerBills();
            bills.Retrieve(GenerBillAttr.FrmID, frmID, GenerBillAttr.Starter, empID);

            return bills.ToDataTableField();
        }

        public static string GenerTitle(string titleRole, Entity wk)
        {
            if (DataType.IsNullOrEmpty(titleRole))
            {
                // 为了保持与ccflow4.5的兼容,从开始节点属性里获取.
                Attr myattr = wk.EnMap.Attrs.GetAttrByKey("Title");
                if (myattr == null)
                    myattr = wk.EnMap.Attrs.GetAttrByKey("Title");

                if (myattr != null)
                    titleRole = myattr.DefaultVal.ToString();

                if (DataType.IsNullOrEmpty(titleRole) || titleRole.Contains("@") == false)
                    titleRole = "@WebUser.DeptName-@WebUser.No,@WebUser.Name在@RDT发起.";
            }

            if (titleRole == "@OutPara" || DataType.IsNullOrEmpty(titleRole) == true)
                titleRole = "@WebUser.DeptName-@WebUser.No,@WebUser.Name在@RDT发起.";

            titleRole = titleRole.Replace("@WebUser.No", WebUser.No);
            titleRole = titleRole.Replace("@WebUser.Name", WebUser.Name);
            titleRole = titleRole.Replace("@WebUser.DeptNameOfFull", WebUser.DeptNameOfFull);
            titleRole = titleRole.Replace("@WebUser.DeptName", WebUser.DeptName);
            titleRole = titleRole.Replace("@WebUser.DeptNo", WebUser.DeptNo);
            titleRole = titleRole.Replace("@RDT", DataType.CurrentDateByFormart("yy年MM月dd日HH时mm分"));
            if (titleRole.Contains("@"))
            {
                Attrs attrs = wk.EnMap.Attrs;

                // 优先考虑外键的替换,因为外键文本的字段的长度相对较长。
                foreach (Attr attr in attrs)
                {
                    if (titleRole.Contains("@") == false)
                        break;
                    if (attr.ItIsRefAttr == false)
                        continue;
                    titleRole = titleRole.Replace("@" + attr.Key, wk.GetValStrByKey(attr.Key));
                }

                //在考虑其它的字段替换.
                foreach (Attr attr in attrs)
                {
                    if (titleRole.Contains("@") == false)
                        break;

                    if (attr.ItIsRefAttr == true)
                        continue;
                    titleRole = titleRole.Replace("@" + attr.Key, wk.GetValStrByKey(attr.Key));
                }
            }
            titleRole = titleRole.Replace('~', '-');
            titleRole = titleRole.Replace("'", "”");

            // 为当前的工作设置title.
            wk.SetValByKey("Title", titleRole);
            return titleRole;
        }
        /// <summary>
        /// 生成单据编号
        /// </summary>
        /// <param name="billNo">单据编号规则</param>
        /// <param name="workid">工作ID</param>
        /// <param name="en">实体类</param>
        /// <param name="frmID">表单ID</param>
        /// <returns>生成的单据编号</returns>
        public static string GenerBillNo(string billNo, Int64 workid, Entity en, string frmID)
        {
            if (DataType.IsNullOrEmpty(billNo))
                billNo = "3";

            if (billNo.Contains("@"))
                billNo = BP.WF.Glo.DealExp(billNo, en, null);

            /*如果，Bill 有规则 */
            billNo = billNo.Replace("{YYYY}", DateTime.Now.ToString("yyyy"));
            billNo = billNo.Replace("{yyyy}", DateTime.Now.ToString("yyyy"));

            billNo = billNo.Replace("{yy}", DateTime.Now.ToString("yy"));
            billNo = billNo.Replace("{YY}", DateTime.Now.ToString("yy"));

            billNo = billNo.Replace("{MM}", DateTime.Now.ToString("MM"));
            billNo = billNo.Replace("{mm}", DateTime.Now.ToString("MM"));

            billNo = billNo.Replace("{DD}", DateTime.Now.ToString("dd"));
            billNo = billNo.Replace("{dd}", DateTime.Now.ToString("dd"));
            billNo = billNo.Replace("{HH}", DateTime.Now.ToString("HH"));
            billNo = billNo.Replace("{hh}", DateTime.Now.ToString("HH"));

            billNo = billNo.Replace("{LSH}", workid.ToString());
            billNo = billNo.Replace("{WorkID}", workid.ToString());
            billNo = billNo.Replace("{OID}", workid.ToString());

            if (billNo.Contains("@WebUser.DeptZi"))
            {
                string val = DBAccess.RunSQLReturnStringIsNull("SELECT Zi FROM Port_Dept WHERE No='" + WebUser.DeptNo + "'", "");
                billNo = billNo.Replace("@WebUser.DeptZi", val.ToString());
            }

            string sql = "";
            int num = 0;
            string supposeBillNo = billNo;  //假设单据号，长度与真实单据号一致
            List<KeyValuePair<int, int>> loc = new List<KeyValuePair<int, int>>();  //流水号位置，流水号位数
            string lsh; //流水号设置码
            int lshIdx = -1;    //流水号设置码所在位置

            for (int i = 2; i < 9; i++)
            {
                lsh = "{LSH" + i + "}";

                if (!supposeBillNo.Contains(lsh))
                    continue;

                while (supposeBillNo.Contains(lsh))
                {
                    //查找流水号所在位置
                    lshIdx = supposeBillNo.IndexOf(lsh);
                    //将找到的流水号码替换成假设的流水号
                    supposeBillNo = (lshIdx == 0 ? "" : supposeBillNo.Substring(0, lshIdx))
                                    + string.Empty.PadLeft(i, '_')
                                    +
                                    (lshIdx + 6 < supposeBillNo.Length
                                         ? supposeBillNo.Substring(lshIdx + 6)
                                         : "");
                    //保存当前流程号所处位置，及流程号长度，以便之后使用替换成正确的流水号
                    loc.Add(new KeyValuePair<int, int>(lshIdx, i));
                }
            }

            //数据库中查找符合的单据号集合,NOTE:此处需要注意，在LIKE中带有左广方括号时，要使用一对广播号将其转义
            sql = "SELECT BillNo FROM Frm_GenerBill WHERE BillNo LIKE '" + supposeBillNo.Replace("[", "[[]") + "'"
                + " AND WorkID <> " + workid
                + " AND FrmID ='" + frmID + "' "
                + " ORDER BY BillNo DESC ";

            string maxBillNo = DBAccess.RunSQLReturnString(sql);
            int ilsh = 0;

            if (DataType.IsNullOrEmpty(maxBillNo))
            {
                //没有数据，则所有流水号都从1开始
                foreach (KeyValuePair<int, int> kv in loc)
                {
                    supposeBillNo = (kv.Key == 0 ? "" : supposeBillNo.Substring(0, kv.Key))
                                    + "1".PadLeft(kv.Value, '0')
                                    +
                                    (kv.Key + kv.Value < supposeBillNo.Length
                                         ? supposeBillNo.Substring(kv.Key + kv.Value)
                                         : "");
                }
            }
            else
            {
                //有数据，则从右向左开始判断流水号，当右侧的流水号达到最大值，则左侧的流水号自动加1
                Dictionary<int, int> mlsh = new Dictionary<int, int>();
                int plus1idx = -1;

                for (int i = loc.Count - 1; i >= 0; i--)
                {
                    //获取单据号中当前位的流水码数
                    ilsh = Convert.ToInt32(maxBillNo.Substring(loc[i].Key, loc[i].Value));

                    if (plus1idx >= 0)
                    {
                        //如果当前码位被置为+1，则+1，同时将标识置为-1
                        ilsh++;
                        plus1idx = -1;
                    }
                    else
                    {
                        mlsh.Add(loc[i].Key, i == loc.Count - 1 ? ilsh + 1 : ilsh);
                        continue;
                    }

                    if (ilsh >= Convert.ToInt32(string.Empty.PadLeft(loc[i].Value, '9')))
                    {
                        //右侧已经达到最大值
                        if (i > 0)
                        {
                            //记录前位的码
                            mlsh.Add(loc[i].Key, 1);
                        }
                        else
                        {
                            supposeBillNo = "单据号超出范围";
                            break;
                        }

                        //则将前一个流水码位，标记为+1
                        plus1idx = i - 1;
                    }
                    else
                    {
                        mlsh.Add(loc[i].Key, ilsh + 1);
                    }
                }

                if (supposeBillNo == "单据号超出范围")
                    return supposeBillNo;

                //拼接单据号
                foreach (KeyValuePair<int, int> kv in loc)
                {
                    supposeBillNo = (kv.Key == 0 ? "" : supposeBillNo.Substring(0, kv.Key))
                                    + mlsh[kv.Key].ToString().PadLeft(kv.Value, '0')
                                    +
                                    (kv.Key + kv.Value < supposeBillNo.Length
                                         ? supposeBillNo.Substring(kv.Key + kv.Value)
                                         : "");
                }
            }

            billNo = supposeBillNo;

            return billNo;
        }
    }
}
