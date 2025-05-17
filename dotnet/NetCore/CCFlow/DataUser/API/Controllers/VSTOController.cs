using Microsoft.AspNetCore.Mvc;
using System.Collections;
using System;
using BP.DA;
using BP.Difference;
using BP.En;
using BP.Sys;
using BP.Tools;
using BP.Web;
using BP.WF.HttpHandler;
using BP.WF.Template.Frm;
using BP.WF;
using Newtonsoft.Json;
using System.Collections.Generic;
using System.Data;
using System.Text;
using System.Web;
using System.Linq;
using System.IO;
using Microsoft.AspNetCore.Http;
using NPOI.SS.Formula.Eval;
using Org.BouncyCastle.Tsp;

namespace CCFlow.NetCore.DataUser.API.Controllers
{
    [Route("WF/[controller]/[Action]")]
    [ApiController]
    public class VSTOController : ControllerBase
    {
        /// <summary>
        /// office插件和wps插件编辑文件相关接口
        /// </summary>

        public const string DBFile = "DBFile";
        /// <summary>
        /// 返回信息格式:此数据格式可以根据自己的需要来定义.
        /// </summary>
        /// <param name="code">代码:200=成功，500=失败.</param>
        /// <param name="msg">消息:错误的信息.</param>
        /// <param name="data">数据:相关的json格式的数据.</param>
        /// <returns></returns>
        public static object Return_Info(int code, string msg, object data)
        {
            Hashtable ht = new Hashtable();
            ht.Add("code", code);
            ht.Add("message", msg);
            ht.Add("data", data);
            return ht;
        }

        /// <summary>
        /// 获取VSTO插件版本号
        /// </summary>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object GetVstoExtensionVersion()
        {
            return Return_Info(200, "执行成功", SystemConfig.VstoExtensionVersion);
        }

        /// <summary>
        /// 获得Excel文件
        /// </summary>
        /// <param name="token"></param>
        /// <param name="frmID"></param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object GenerTemplateFile(string token, string frmID)
        {
            try
            {
                Dev2Interface.Port_LoginByToken(token);

                MapData md;
                //如果是一个实体类.
                if (frmID.Contains("BP."))
                {
                    // 执行map同步.
                    Entities ens = ClassFactory.GetEns(frmID + "s");
                    Entity en = ens.GetNewEntity;
                    md = en.DTSMapToSys_MapData();
                }
                else
                {
                    md = new MapData(frmID);
                }

                byte[] bytes = null;

                md.ExcelGenerFile(ref bytes);

                string base64Str = bytes != null ? Convert.ToBase64String(bytes) : "";

                return Return_Info(200, "执行成功", base64Str);
            }
            catch (Exception ex)
            {
                return Return_Info(500, "获得Excel模板文件时出现异常，调用接口[GenerTemplateFile]\n" + ex.Message, "");
            }
        }

        /// <summary>
        /// 获得Excel文件
        /// </summary>
        /// <param name="token"></param>
        /// <param name="frmID"></param>
        /// <param name="pkValue"></param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object GenerExcelFile(string token, string frmID, string pkValue)
        {
            try
            {
                Dev2Interface.Port_LoginByToken(token);

                byte[] bytes = null;
                MapData md;
                //如果是一个实体类.
                if (frmID.Contains("BP."))
                {
                    // 执行map同步.
                    Entities ens = ClassFactory.GetEns(frmID + "s");
                    Entity en = ens.GetNewEntity;

                    md = en.DTSMapToSys_MapData();
                }
                else
                {
                    md = new MapData(frmID);
                }
                //创建excel表单描述，让其保存到excel表单指定的字段里, 扩展多个表单映射同一张表.
                MapFrmExcel mfe = new MapFrmExcel(md.No);

                md.ExcelGenerFile(pkValue, ref bytes, mfe.DBSave);

                if (bytes != null)
                {
                    //转为字符
                    string docStr = Convert.ToBase64String(bytes);
                    return Return_Info(200, "执行成功", docStr);
                }
                else
                {
                    return Return_Info(404, "没有获取到文件流", "");
                }
            }
            catch (Exception ex)
            {
                return Return_Info(500, ex.Message, "");
            }

        }

        /// <summary>
        /// VSTO,执行保存Excel主表数据
        /// </summary>
        /// <param name="token"></param>
        /// <param name="frmID"></param>
        /// <param name="pkValue"></param>
        /// <param name="mainTableAtParas"></param>
        [HttpGet, HttpPost]
        public void SaveExcelFileMainTable(string token, string frmID, string pkValue, string mainTableAtParas)
        {
            Log.DebugWriteInfo("写入VSTO表单主表数据：" + mainTableAtParas);
            //执行登录.
            Dev2Interface.Port_LoginByToken(token);

            #region 求主表的主键类型.
            string pkType = null;
            string sql = "SELECT KeyOfEn FROM Sys_MapAttr WHERE FK_MapData='" + frmID + "' AND KeyOfEn='OID' ";
            if (DBAccess.RunSQLReturnTable(sql).Rows.Count == 1)
                pkType = "OID";

            if (pkType == null)
            {
                sql = "SELECT KeyOfEn FROM Sys_MapAttr WHERE FK_MapData='" + frmID + "' AND KeyOfEn='MyPK' ";
                if (DBAccess.RunSQLReturnTable(sql).Rows.Count == 1)
                    pkType = "MyPK";
            }

            if (pkType == null)
                pkType = "No";

            #endregion 求主表的主键类型.

            #region 处理EntityMyPK 类型的实体保存。
            if (pkType.Equals("MyPK")==true)
            {
                /* 具有MyPK 的实体，为了简便判断. */
                GEEntityMyPK wk = new GEEntityMyPK(frmID, pkValue);
                wk.ResetDefaultVal();

                if (mainTableAtParas != null)
                {
                    AtPara ap = new AtPara(mainTableAtParas);
                    foreach (string str in ap.HisHT.Keys)
                    {
                        if (wk.Row.ContainsKey(str))
                            wk.SetValByKey(str, ap.GetValStrByKey(str));
                        else
                            wk.Row.Add(str, ap.GetValStrByKey(str));
                    }
                }
                wk.MyPK = pkValue;

                // 保存实体.
                wk.Save();

            }
            #endregion 处理 EntityMyPK 类型的实体保存。

            #region 处理 EntityNoName 类型的实体保存。
            if (pkType.Equals("No") == true)
            {
                /* 具有MyPK 的实体，为了简便判断. */
                GEEntityNoName wk = new GEEntityNoName(frmID);
                wk.ResetDefaultVal();
                wk.SetValByKey("No", pkValue);
                wk.RetrieveFromDBSources();

                if (mainTableAtParas != null)
                {
                    AtPara ap = new AtPara(mainTableAtParas);
                    foreach (string str in ap.HisHT.Keys)
                    {
                        if (wk.Row.ContainsKey(str))
                            wk.SetValByKey(str, ap.GetValStrByKey(str));
                        else
                            wk.Row.Add(str, ap.GetValStrByKey(str));
                    }
                }
                wk.SetValByKey("No", pkValue);
                // 保存实体.
                wk.Save();

            }
            #endregion 处理 EntityMyPK 类型的实体保存。


            #region 处理 EntityOID 类型的实体保存。
            if (pkType.Equals("OID")==true)
            {
                GEEntityOID wk = new GEEntityOID(frmID, pkValue);
                wk.ResetDefaultVal();

                if (mainTableAtParas != null)
                {
                    AtPara ap = new AtPara(mainTableAtParas);
                    ap.SetVal("BillState", "1");
                    foreach (string str in ap.HisHT.Keys)
                    {
                        if (wk.Row.ContainsKey(str))
                            wk.SetValByKey(str, ap.GetValStrByKey(str));
                        else
                            wk.Row.Add(str, ap.GetValStrByKey(str));
                    }
                }
                wk.OID = long.Parse(pkValue);
                //保存.
                wk.Save();
            }
            #endregion 处理 EntityOID 类型的实体保存。

        }

        /// <summary>
        /// VSTO,执行保存Excel从表数据
        /// </summary>
        /// <param name="token"></param>
        /// <param name="frmID"></param>
        /// <param name="pkValue"></param>
        /// <param name="dsJson"></param>
        /// <param name="dsJsonOld"></param>
        /// <exception cref="Exception"></exception>
        [HttpGet, HttpPost]
        public object SaveExcelFileDtls([FromBody] RequestData requestData)
        {
            try
            {
                if (DataType.IsNullOrEmpty(requestData.token) || DataType.IsNullOrEmpty(requestData.frmID) || DataType.IsNullOrEmpty(requestData.pkValue))
                {
                    return Return_Info(500, "参数不能为空", null);
                }
                DataSet dsDtlsNew = JsonConvert.DeserializeObject<DataSet>(requestData.data);

                //执行登录.
                Dev2Interface.Port_LoginByToken(requestData.token);

                MapDtls dtls = new MapDtls(requestData.frmID);
                foreach (MapDtl mapDtl in dtls)
                {
                    if (!dsDtlsNew.Tables.Contains(mapDtl.No))
                    {
                        continue;
                    }
                    GEDtls gEDtls = new GEDtls(mapDtl.No);

                    //接收的table数据
                    DataTable dataTable = dsDtlsNew.Tables[mapDtl.No];

                    QueryObject queryObject = new QueryObject(gEDtls);
                    queryObject.AddWhere(mapDtl.RefPK, requestData.pkValue);
                    DataTable oldDataTable = queryObject.DoQueryToTable();

                    List<string> oldOidList = oldDataTable.AsEnumerable().Select(row => row.Field<long>("OID").ToString()).Distinct().ToList();

                    foreach (DataRow dr in dataTable.Rows)
                    {
                        //在原有数据基础上删除不存在的oid数据
                        if (oldOidList.Count > 0 && oldOidList.Contains(dr["OID"].ToString()))
                        {
                            oldOidList.RemoveAll(x => x == dr["OID"].ToString());
                        }

                        GEDtl daDtl = gEDtls.GetNewEntity as GEDtl;
                        daDtl.OID = int.Parse(dr["OID"].ToString());
                        int count = daDtl.RetrieveFromDBSources();
                        daDtl.RDT = DataType.CurrentDateTime;

                        //更新字段值
                        foreach (DataColumn dc in dataTable.Columns)
                        {
                            //设置属性.
                            daDtl.SetValByKey(dc.ColumnName, dr[dc.ColumnName]);
                        }
                        if (count > 0)
                        {
                            daDtl.Update();
                        }
                        else
                        {
                            daDtl.RefPK = requestData.pkValue;
                            daDtl.Insert();
                        }
                    }

                    if (oldOidList.Count > 0)
                    {
                        Paras ps = new Paras();
                        ps.SQL = "DELETE FROM " + mapDtl.PTable + " WHERE OID in ( " + ps.DBStr + "V)";
                        ps.Add("V", string.Join(",", oldOidList));
                        DBAccess.RunSQL(ps);


                        //    string deleteSql = "DELETE FROM " + mapDtl.PTable + " WHERE OID in ( " + string.Join(",", oldOidList) + ")";
                        //DBAccess.RunSQL(deleteSql);
                    }
                }
            }
            catch (Exception ex)
            {
                return Return_Info(500, ex.Message, null);
            }

            return Return_Info(200, "执行成功", null);
        }

        /// <summary>
        /// 生成vsto模式的数据
        /// </summary>
        /// <param name="token"></param>
        /// <param name="frmID"></param>
        /// <param name="pkValue"></param>
        /// <param name="atParas"></param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object GenerDBForVSTOExcelFrmModel(string token, string frmID, string pkValue, string atParas)
        {
            //让他登录.
            Dev2Interface.Port_LoginByToken(token);

            //解析这个表单.
            try
            {
                DataSet ds = BP.WF.CCFormAPI.GenerDBForVSTOExcelFrmModel(frmID, pkValue, atParas);

                return Return_Info(200, "执行成功", Json.ToJson(ds));
            }
            catch (Exception ex)
            {
                return Return_Info(500, ex.Message, null);
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="workID"></param>
        /// <param name="createReportType"></param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object GetReportImagesData(long workID, string createReportType)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(createReportType))
                    return null;

                string dbStr = SystemConfig.AppCenterDBVarStr;
                Paras ps = new Paras();


                switch (createReportType)
                {
                    case "1":
                        ps.SQL = "SELECT FileFullName,FileExts,MyPK,FileName  FROM Sys_FrmAttachmentDB  WHERE  RefPKVal=" + dbStr + "RefPKVal";
                        ps.Add(FrmAttachmentDBAttr.RefPKVal, workID);
                        break;
                    case "2":
                        ps.SQL = "SELECT FileFullName,FileExts,MyPK,FileName  FROM Sys_FrmAttachmentDB  WHERE " +
                                 "RefPKVal in(SELECT WorkID FROM WF_GenerWorkFlow WHERE PWORKID=" + dbStr + "PWORKID)";
                        ps.Add("PWORKID", workID);
                        break;
                    default:
                        break;
                }
                DataTable dt = DBAccess.RunSQLReturnTable(ps);

                List<ReportImage> reImgsList = new List<ReportImage>();
                foreach (DataRow dr in dt.Rows)
                {
                    string fileFullName = dr["FileFullName"].ToString();
                    if (System.IO.File.Exists(fileFullName))
                    {
                        FileStream fs = new FileStream(fileFullName, FileMode.Open);
                        long size = fs.Length;
                        byte[] bytes = new byte[size];
                        fs.Read(bytes, 0, bytes.Length);
                        fs.Close();

                        reImgsList.Add(new ReportImage
                        {
                            ext = dr["FileExts"].ToString(), //frmDB.FileExts,
                            fileName = dr["FileName"].ToString(), //frmDB.FileName,
                            bytesData = Convert.ToBase64String(bytes),
                            mypk = dr["MyPK"].ToString() //frmDB.MyPK
                        });
                    }
                }

                return Return_Info(200, "执行成功", LitJson.JsonMapper.ToJson(reImgsList));
            }
            catch (Exception ex)
            {
                return Return_Info(500, ex.Message, null);
            }
        }

        /// <summary>
        /// 保存一个文件
        /// </summary>
        /// <param name="token"></param>
        /// <param name="frmID"></param>
        /// <param name="nodeID"></param>
        /// <param name="workID"></param>
        /// <param name="byt"></param>
        /// <param name="guid"></param>
        [HttpGet, HttpPost]
        public object SaveFrmAth(string token, string frmID, int nodeID, long workID, string base64Data, string guid)
        {
            try
            {
                Dev2Interface.Port_LoginByToken(token);
                MapData md = new MapData(frmID);
                FrmAttachments aths = new FrmAttachments(frmID);
                if (aths.Count == 0)
                {
                    BP.Sys.CCFormAPI.CreateOrSaveAthMulti(md.No, "Ath", "附件");
                    aths = new FrmAttachments(frmID);
                }
                FrmAttachment ath = aths[0] as FrmAttachment;

                //把文件写入.
                string rootPath = ath.SaveTo;
                string fileName = guid + "." + System.Drawing.Imaging.ImageFormat.Jpeg.ToString();
                string filePath = rootPath + fileName;
                if (System.IO.File.Exists(filePath) == true)
                    System.IO.File.Delete(filePath);

                byte[] byt = Convert.FromBase64String(base64Data);

                DataType.WriteFile(filePath, byt);

                FileInfo info = new FileInfo(filePath);
                FrmAttachmentDB dbUpload = new FrmAttachmentDB();
                dbUpload.MyPK = guid;
                dbUpload.NodeID = nodeID;
                dbUpload.Sort = null;
                dbUpload.FrmID = ath.FrmID;
                dbUpload.FK_FrmAttachment = ath.MyPK;

                dbUpload.FileExts = info.Extension;
                dbUpload.FileFullName = filePath;
                dbUpload.FileName = fileName;
                dbUpload.FileSize = info.Length;
                dbUpload.RDT = DataType.CurrentDateTimess;
                dbUpload.Rec = WebUser.No;
                dbUpload.RecName = WebUser.Name;
                dbUpload.RefPKVal = workID.ToString();

                dbUpload.UploadGUID = guid;
                dbUpload.DirectSave();
                return Return_Info(200, "成功", null);
            }
            catch (Exception ex)
            {
                return Return_Info(500, ex.Message, null);
            }

        }

        /// <summary>
        /// 获得附件byte
        /// </summary>
        /// <param name="token"></param>
        /// <param name="frmAttachmentMyPk"></param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object GenerFrmAttachmentByte(string token, string frmAttachmentMyPk)
        {
            try
            {

                lock (WF_Comm.pblock)
                {
                    //生成文件流
                    byte[] bytes = null;
                    Dev2Interface.Port_LoginByToken(token);

                    if (DataType.IsNullOrEmpty(frmAttachmentMyPk))
                    {
                        return Return_Info(500, "参数不能为空", null);
                    }

                    FrmAttachmentDB frmAttachmentDB = new FrmAttachmentDB();

                    frmAttachmentDB.MyPK = frmAttachmentMyPk;
                    int count = frmAttachmentDB.Retrieve();

                    if (count == 0)
                    {
                        return Return_Info(500, "文件不存在", null);
                    }
                    FrmAttachment frmAttachment = new FrmAttachment();
                    frmAttachment.MyPK = frmAttachmentDB.FK_FrmAttachment;
                    frmAttachment.Retrieve();


                    //临时文件
                    string temFilePath = "";

                    //获取配置项是否加密
                    bool fileEncrypt = SystemConfig.isEnableAthEncrypt;

                    //获取此文件是否加密
                    bool isEncrypt = frmAttachmentDB.GetParaBoolen("IsEncrypt");
                    if (frmAttachment.AthSaveWay == AthSaveWay.IISServer)
                    {

                        temFilePath = frmAttachmentDB.FileFullName + ".tmp";
                        if (fileEncrypt == true && isEncrypt == true)
                        {


                            if (System.IO.File.Exists(temFilePath) == true)
                                System.IO.File.Delete(temFilePath);
                            EncHelper.DecryptDES(frmAttachmentDB.FileFullName, temFilePath);
                        }
                        else
                        {
                            bytes = DataType.ConvertFileToByte(frmAttachmentDB.FileFullName);
                        }
                    }

                    if (frmAttachment.AthSaveWay == AthSaveWay.FTPServer)
                    {
                        //下载文件到临时位置
                        string orignalFile = frmAttachmentDB.GenerTempFile(frmAttachment.AthSaveWay);
                        temFilePath = orignalFile + ".temp";
                        if (fileEncrypt == true && isEncrypt == true)
                        {
                            EncHelper.DecryptDES(orignalFile, temFilePath);

                            //将下载的临时文件删除掉
                            if (System.IO.File.Exists(orignalFile) == true)
                                System.IO.File.Delete(orignalFile);
                        }

                        else
                            temFilePath = orignalFile;
                    }
                    if (frmAttachment.AthSaveWay == AthSaveWay.DB)
                    {

                        //加密文件处理
                        if (fileEncrypt == true && isEncrypt == true)
                        {
                            //根据流生成初试源文件
                            string orignalFile = frmAttachmentDB.GenerTempFile(frmAttachment.AthSaveWay);
                            temFilePath = orignalFile + ".temp";

                            //根据初试源文件生成解密后的文件
                            EncHelper.DecryptDES(orignalFile, temFilePath);
                            if (System.IO.File.Exists(orignalFile) == true)
                                System.IO.File.Delete(orignalFile);
                        }
                        else
                        {
                            //不是加密文件直接读取流
                            bytes = frmAttachmentDB.GetFileFromDB(DBFile, null);
                        }
                    }

                    if (bytes == null)
                    {
                        bytes = DataType.ConvertFileToByte(temFilePath);
                    }
                    string base64Str = bytes != null ? Convert.ToBase64String(bytes) : "";

                    //将临时文件删掉
                    if (System.IO.File.Exists(temFilePath))
                    {
                        System.IO.File.Delete(temFilePath);
                    }
                    Hashtable hs = new Hashtable();
                    hs.Add("bytes", bytes);
                    hs.Add("name", frmAttachmentDB.FileName);
                    return Return_Info(200, "成功", hs);

                }
            }
            catch (Exception ex)
            {
                return Return_Info(500, ex.Message, null);
            }
            return Return_Info(500, "文件不存在", null);
        }

        /// <summary>
        /// 获得VSTO表单模版
        /// </summary>
        /// <param name="token"></param>
        /// <param name="frmID"></param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object GenerFrmVSTOTemplate(string token, string frmID)
        {

            try
            {
                lock (WF_Comm.frmVSTOTemplateLock)
                    Dev2Interface.Port_LoginByToken(token);

                if (DataType.IsNullOrEmpty(frmID))
                {
                    return Return_Info(500, "参数不能为空", null);
                }

                DataSet ds = new DataSet();
                MapData mapData = new MapData(frmID);
                DataTable mapDataTable = mapData.ToDataTableField("Sys_MapData");
                ds.Tables.Add(mapDataTable);

                MapAttrs mapAttrs = new MapAttrs(frmID);
                DataTable mapAttrDataTable = mapAttrs.ToDataTableField("Sys_MapAttr");
                ds.Tables.Add(mapAttrDataTable);

                DataTable mapDtlDataTable = mapData.MapDtls.ToDataTableField("Sys_MapDtl");
                ds.Tables.Add(mapDtlDataTable);

                //加入字段分组信息
                DataTable Sys_GroupField = mapData.GroupFields.ToDataTableField("Sys_GroupField");
                ds.Tables.Add(Sys_GroupField);


                foreach (MapDtl item in mapData.MapDtls)
                {
                    MapAttrs mapDtlAttrs = new MapAttrs(item.No);
                    DataTable mapDtlAttrDataTable = mapDtlAttrs.ToDataTableField("Sys_MapAttr_" + item.No);
                    ds.Tables.Add(mapDtlAttrDataTable);
                }

                //生成文件流
                byte[] bytes = null;
                string fileName = frmID + ".xlsx";
                string frmVSTOTemplateFilePath = SystemConfig.PathOfDataUser + "FrmVSTOTemplate/" + fileName;


                if (System.IO.File.Exists(frmVSTOTemplateFilePath))
                {
                    bytes = DataType.ConvertFileToByte(frmVSTOTemplateFilePath);
                }
                else
                {
                    string emptyFrmVSTOTemplateFilePath = SystemConfig.PathOfDataUser + "FrmVSTOTemplate/EmptyTemplate.xlsx";

                    if (System.IO.File.Exists(emptyFrmVSTOTemplateFilePath))
                    {
                        bytes = DataType.ConvertFileToByte(emptyFrmVSTOTemplateFilePath);
                    }
                    else
                    {
                        return Return_Info(500, "初始化模板文件不存在", null);
                    }
                }

                string base64Str = bytes != null ? Convert.ToBase64String(bytes) : "";
                Hashtable hs = new Hashtable();
                hs.Add("bytes", bytes);
                hs.Add("name", fileName);
                hs.Add("dataSet", ds);
                return Return_Info(200, "成功", hs);
            }
            catch (Exception ex)
            {
                return Return_Info(500, ex.Message, null);
            }
            return Return_Info(500, "文件不存在", null);
        }

        /// <summary>
        /// wps获得公文
        /// </summary>
        /// <param name="token"></param>
        /// <param name="mypk"></param>
        /// <param name="flowNo"></param>
        /// <returns></returns>
        [HttpGet]
        [ActionName("GenerGongWenTrackByte")]
        public object GenerGongWenTrackByteWPS(string token, string mypk, int flowNo)
        {
            try
            {
                //生成文件流
                byte[] bytes = null;
                if (DataType.IsNullOrEmpty(token) || DataType.IsNullOrEmpty(mypk) || flowNo == 0)
                {
                    return Return_Info(500, "参数不能为空", "");

                }

                Dev2Interface.Port_LoginByToken(token);
                string tableTrack = "ND" + flowNo + "Track";
                bytes = DBAccess.GetByteFromDB(tableTrack, "MyPk", mypk, DBFile);

                if (bytes == null || bytes.Length == 0)
                {

                    return Return_Info(500, "历史公文不存在", "");
                }
                HttpContextHelper.DownLoadFileByBytes(bytes, DBAccess.GenerGUID() + ".docx");
            }
            catch (Exception ex)
            {
                return Return_Info(500, ex.Message, "");
            }
            return Return_Info(500, "历史公文不存在", "");
        }

        #region vsto 组件功能.
        #region Old API
        /// <summary>
        /// 获取VSTO插件版本号
        /// </summary>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object GetVstoCCFormWordExtensionVersion()
        {
            return Return_Info(200, "执行成功", "1.1.0.4");
        }

        #endregion Old API
        /// <summary>
        /// 初始化公文组件
        /// </summary>
        /// <param name="token">登录人员的信息</param>
        /// <param name="workID">工作ID</param>
        /// <returns>执行结果</returns>
        [HttpGet, HttpPost]
        public object VSTOWord_GongWen_Init(string token, long workID)
        {
            //根据token登录.
            Port_GenerToken(token);
            try
            {
                GenerWorkFlow gwf = new GenerWorkFlow(workID);

                //是否可以查看
                if (Dev2Interface.Flow_IsCanViewTruck(gwf.FlowNo, workID, WebUser.No) == false)
                {
                    string msg = "err@您无权查看该工作,";
                    msg += "\t\n如下情况可以查看该工作.";
                    msg += "\t\n1. 该流程发起人, 审批人，抄送人，可以查看.";
                    msg += "\t\n2. 默认与发起人是同一个部门的人可以查看.";
                    msg += "\t\n3. 二级管理员可以查看本组织的工作.";
                    msg += "\t\n4. 超级管理员可以查看.";
                    msg += "\t\n5. 流程属性的权限控制设置权限的人可以查看.";
                    msg += "\t\n6. 如果该流程的数据，任何人都可以查看，请在流程属性里设置权限控制，任何人可见.";
                    throw new Exception(msg);
                }

                //是否可以操作
                if (Dev2Interface.Flow_IsCanDoCurrentWork(workID, WebUser.No) == false)
                {
                    string msg = "err@您无权处理该工作.";
                    throw new Exception(msg);
                }

                //是否已经有了模板文件
                if (Dev2Interface.Flow_IsHaveDocFile(workID) == false)
                {
                    string msg = "err@没有上传公文模板，请您上传模板后重试.";
                    throw new Exception(msg);
                }

                return Return_Info(200, "执行成功", gwf.ToJson());
            }
            catch (Exception ex)
            {
                return Return_Info(500, "失败", ex.Message);
            }
        }

        /// <summary>
        /// 创建公文
        /// </summary>
        /// <param name="token">Token</param>
        /// <param name="workID">WorkID</param>
        /// <param name="templateFileNo">模板主键</param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object VSTOWord_GongWen_Create(string token, string workID, string templateFileNo)
        {
            //根据token登录.
            Port_GenerToken(token);
            try
            {
                GenerWorkFlow gwf = new GenerWorkFlow(workID);

                //选择一个模板来创建
                string dbstr = SystemConfig.AppCenterDBVarStr;
                Paras ps = new Paras();
                ps.SQL = "SELECT * FROM WF_Part a WHERE  MyPK=" + dbstr + "MyPK ";
                ps.Add("MyPK", templateFileNo);
                DataTable dt = DBAccess.RunSQLReturnTable(ps);

                if (dt.Rows.Count == 0)
                {
                    throw new Exception("err@没有找到公文模板.请检查WF_Part表,主键 [MyPK] = [" + templateFileNo + "] ,如没有数据请上传公文模板.");
                }

                //拿到文件路径
                string filePath = dt.Rows[0]["MyFilePath"].ToString();

                //转为二进制
                var bytes = DataType.ConvertFileToByte(filePath);

                //文件二进制存储到业务表PTable的DocFile字段
                Flow fl = new Flow(gwf.FlowNo);
                DBAccess.SaveBytesToDB(bytes, fl.PTable, "OID", workID, "DocFile");

                return Return_Info(200, "创建成功", "");
            }
            catch (Exception ex)
            {
                return Return_Info(500, "失败", ex.Message);
            }

        }

        /// <summary>
        /// 获得公文
        /// </summary>
        /// <param name="token"></param>
        /// <param name="workId">workid</param>
        /// <param name="fkNodeId">节点id</param>
        /// <param name="templateFileName">指定公文模板文件名称</param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object GenerGongWenByte(string token, int workId, int fkNodeId, string gongWenTemplateFile)
        {
            try
            {
                //
                lock (WF_Comm.gongWenWordTemplateLock)
                {
                    //生成文件流
                    byte[] bytes = null;
                    if (DataType.IsNullOrEmpty(token))
                    {
                        return Return_Info(500, "参数不能为空", null);
                    }
                    Dev2Interface.Port_LoginByToken(token);
                    Node nd = new Node(fkNodeId);
                    Work wk = nd.HisWork;
                    wk.OID = workId;
                    wk.RetrieveFromDBSources();

                    bytes = wk.GetFileFromDB(DBFile, "");

                    string fileName = "空白模板.docx";
                    if (!DataType.IsNullOrEmpty(gongWenTemplateFile))
                    {
                        fileName = HttpUtility.UrlDecode(gongWenTemplateFile, Encoding.UTF8);
                    }
                    if (bytes == null || bytes.Length == 0)
                    {

                        string frmVSTOTemplateFilePath = SystemConfig.PathOfDataUser + "DocFlow\\" + fileName;
                        if (System.IO.File.Exists(frmVSTOTemplateFilePath))
                        {
                            bytes = DataType.ConvertFileToByte(frmVSTOTemplateFilePath);
                        }
                        else
                        {
                            return Return_Info(500, "初始化模板文件不存在", null);
                        }


                    }
                    string base64Str = bytes != null ? Convert.ToBase64String(bytes) : "";
                    Hashtable hs = new Hashtable();
                    hs.Add("bytes", bytes);
                    hs.Add("name", DBAccess.GenerGUID() + ".docx");
                    return Return_Info(200, "成功", hs);
                }
            }
            catch (Exception ex)
            {
                return Return_Info(500, ex.Message, null);
            }
            return Return_Info(500, "文件不存在", null);
        }

        /// <summary>
        /// 获得doc文件
        /// </summary>
        /// <param name="token"></param>
        /// <param name="workID"></param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object VSTOWord_GongWen_GetDocFile(string token, long workID)
        {
            //根据token登录.
            Port_GenerToken(token);
            try
            {
                GenerWorkFlow gwf = new GenerWorkFlow(workID);
                Flow fl = new Flow(gwf.FlowNo);
                //获取文件流
                byte[] bytes = DBAccess.GetByteFromDB(fl.PTable, "OID", workID.ToString(), "DocFile");

                //转为字符
                string docStr = Encoding.UTF8.GetString(bytes);

                return Return_Info(200, "执行成功", docStr);
            }
            catch (Exception ex)
            {
                return Return_Info(500, "失败", ex.Message);
            }
        }

        /// <summary>
        /// 获得pdf文件
        /// </summary>
        /// <param name="token"></param>
        /// <param name="workID"></param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object VSTOWord_GongWen_GetPDFFile(string token, long workID)
        {
            //根据token登录.
            Port_GenerToken(token);
            try
            {


                return Return_Info(200, "暂缓", "xxxx");
            }
            catch (Exception ex)
            {
                return Return_Info(500, "失败", ex.Message);
            }
        }

        /// <summary>
        /// 保存公文
        /// </summary>
        /// <param name="token">Token</param>
        /// <param name="workID">WorkID</param>
        /// <param name="bytes">文件流</param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object VSTOWord_GongWen_SaveFile(string token, long workID, byte[] bytes)
        {
            //根据token登录.
            Port_GenerToken(token);
            try
            {
                GenerWorkFlow gwf = new GenerWorkFlow(workID);
                //文件二进制存储到业务表PTable的DocFile字段
                Flow fl = new Flow(gwf.FlowNo);
                DBAccess.SaveBytesToDB(bytes, fl.PTable, "OID", workID, "DocFile");

                return Return_Info(200, "公文保存成功", "");
            }
            catch (Exception ex)
            {
                return Return_Info(500, "失败", ex.Message);
            }
        }

        #region wps功能
        /// <summary>
        /// wps获得公文文件流
        /// </summary>
        /// <param name="token"></param>
        /// <param name="workId"></param>
        /// <param name="fkNodeId"></param>
        /// <param name="response"></param>
        /// <param name="request"></param>
        /// <returns></returns>
        [HttpPost, HttpGet]
        public object GenerGongWenByteWPS(string token, int workId, int fkNodeId, string gongWenTemplateFile)
        {
            try
            {
                //生成文件流
                byte[] bytes = null;
                if (DataType.IsNullOrEmpty(token) || workId == 0 || fkNodeId == 0)
                {
                    return Return_Info(500, "参数不能为空", "");

                }
                Dev2Interface.Port_LoginByToken(token);
                Node nd = new Node(fkNodeId);
                Work wk = nd.HisWork;
                wk.OID = workId;
                wk.RetrieveFromDBSources();

                bytes = wk.GetFileFromDB(DBFile, "");

                string fileName = "空白模板.docx";
                if (!DataType.IsNullOrEmpty(gongWenTemplateFile))
                {
                    fileName = HttpUtility.UrlDecode(gongWenTemplateFile, Encoding.UTF8);
                }
                if (bytes == null || bytes.Length == 0)
                {

                    string frmVSTOTemplateFilePath = SystemConfig.PathOfDataUser + "DocFlow/" + fileName;

                    if (System.IO.File.Exists(frmVSTOTemplateFilePath))
                    {
                        bytes = DataType.ConvertFileToByte(frmVSTOTemplateFilePath);

                    }
                    else
                    {
                        return Return_Info(500, "初始化模板文件不存在", "");
                    }
                }
                HttpContextHelper.DownLoadFileByBytes(bytes, DBAccess.GenerGUID() + ".docx");
            }
            catch (Exception ex)
            {
                return Return_Info(500, ex.Message, "");
            }
            return Return_Info(500, "公文不存在", "");
        }

        /// <summary>
        /// WPS上传公文
        /// </summary>
        /// <param name="token"></param>
        /// <param name="workId"></param>
        /// <param name="fkNodeId"></param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object SaveGongWenWordWPS(string token, int workId, int fkNodeId)
        {
            try
            {
                if (HttpContextHelper.RequestFilesCount < 0 || DataType.IsNullOrEmpty(token) || workId == 0 || fkNodeId == 0)
                {
                    return Return_Info(500, "参数不能为空", "");
                }
                Dev2Interface.Port_LoginByToken(token);
                IFormFile file = HttpContextHelper.RequestFiles(0);
                Dev2Interface.Port_LoginByToken(token);
                // 二进制数组
                byte[] bytes = new byte[file.Length];

                //创建Stream对象，并指向上传文件
                Stream fileStream = file.OpenReadStream();
                //从当前流中读取字节，读入字节数组中
                fileStream.Read(bytes, 0, (int)file.Length);

                Node nd = new Node(fkNodeId);
                Work wk = nd.HisWork;
                wk.OID = workId;
                int i = wk.RetrieveFromDBSources();
                if (i == 0)
                {
                    throw new Exception("该流程的表单数据不存在");

                }
                wk.SaveBytesToDB(DBFile, bytes);
                return "File uploaded successfully";
            }
            catch (Exception ex)
            {
                return Return_Info(500, ex.Message, "");
            }
        }


        /// <summary>
        /// WPS获得附件字节
        /// </summary>
        /// <param name="token"></param>
        /// <param name="frmAttachmentMyPk"></param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object GenerFrmAttachmentByteWPS(string token, string frmAttachmentMyPk)
        {
            try
            {
                lock (WF_Comm.pblock)
                {
                    //生成文件流
                    byte[] bytes = null;
                    if (DataType.IsNullOrEmpty(frmAttachmentMyPk) || DataType.IsNullOrEmpty(token))
                    {
                        return Return_Info(500, "参数不能为空", "");
                    }
                    Dev2Interface.Port_LoginByToken(token);

                    FrmAttachmentDB frmAttachmentDB = new FrmAttachmentDB();

                    frmAttachmentDB.setMyPK(frmAttachmentMyPk);
                    int count = frmAttachmentDB.Retrieve();

                    if (count == 0)
                    {
                        return Return_Info(500, "文件不存在", "");
                    }
                    FrmAttachment frmAttachment = new FrmAttachment();
                    frmAttachment.setMyPK(frmAttachmentDB.FK_FrmAttachment);
                    frmAttachment.Retrieve();

                    //临时文件
                    string temFilePath = "";

                    //获取配置项是否加密
                    bool fileEncrypt = SystemConfig.isEnableAthEncrypt;

                    //获取此文件是否加密
                    bool isEncrypt = frmAttachmentDB.GetParaBoolen("IsEncrypt");
                    if (frmAttachment.AthSaveWay == AthSaveWay.IISServer)
                    {

                        temFilePath = frmAttachmentDB.FileFullName + ".tmp";
                        if (fileEncrypt == true && isEncrypt == true)
                        {
                            if (System.IO.File.Exists(temFilePath) == true)
                            {
                                System.IO.File.Delete(temFilePath);
                            }
                            EncHelper.EncryptDES(frmAttachmentDB.FileFullName, temFilePath);
                        }
                        else
                        {
                            bytes = DataType.ConvertFileToByte(frmAttachmentDB.FileFullName);
                        }
                    }

                    if (frmAttachment.AthSaveWay == AthSaveWay.FTPServer)
                    {
                        //下载文件到临时位置
                        string orignalFile = frmAttachmentDB.GenerTempFile(frmAttachment.AthSaveWay);
                        temFilePath = orignalFile + ".temp";
                        if (fileEncrypt == true && isEncrypt == true)
                        {
                            EncHelper.DecryptDES(frmAttachmentDB.FileFullName, temFilePath);

                            //将下载的临时文件删除掉
                            if (System.IO.File.Exists(orignalFile))
                            {
                                System.IO.File.Delete(orignalFile);
                            }

                        }
                        else
                            temFilePath = orignalFile;
                    }
                    if (frmAttachment.AthSaveWay == AthSaveWay.DB)
                    {

                        //加密文件处理
                        if (fileEncrypt == true && isEncrypt == true)
                        {
                            //根据流生成初试源文件
                            string orignalFile = frmAttachmentDB.GenerTempFile(frmAttachment.AthSaveWay);
                            temFilePath = orignalFile + ".temp";
                            byte[] bytesEncrypt = frmAttachmentDB.GetFileFromDB(DBFile, null);
                            if (bytesEncrypt == null || bytesEncrypt.Length == 0)
                            {
                                throw new Exception("文件不存在");
                            }

                            //根据初试源文件生成解密后的文件
                            EncHelper.DecryptDES(orignalFile, temFilePath);
                            if (System.IO.File.Exists(orignalFile))
                            {
                                System.IO.File.Delete(orignalFile);
                            }
                        }
                        else
                        {
                            //不是加密文件直接读取流
                            bytes = frmAttachmentDB.GetFileFromDB(DBFile, null);
                        }
                    }

                    if (bytes == null)
                    {
                        bytes = DataType.ConvertFileToByte(temFilePath);
                    }

                    HttpContextHelper.DownLoadFileByBytes(bytes, frmAttachmentDB.FileName);
                }
            }
            catch (Exception ex)
            {
                return Return_Info(500, ex.Message, "");
            }
            return Return_Info(500, "文件不存在", "");

        }
        /// <summary>
        /// wps保存附件
        /// </summary>
        /// <param name="token"></param>
        /// <param name="frmAttachmentMyPk"></param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object SaveFrmAttachmentWPS(string token, string frmAttachmentMyPk)
        {
            try
            {
                lock (WF_Comm.pblock)
                {
                    if (HttpContextHelper.RequestFilesCount < 0 || DataType.IsNullOrEmpty(token) || DataType.IsNullOrEmpty(frmAttachmentMyPk))
                    {
                        return Return_Info(500, "参数不能为空", "");
                    }
                    IFormFile file = HttpContextHelper.RequestFiles(0);
                    Dev2Interface.Port_LoginByToken(token);

                    FrmAttachmentDB frmAttachmentDB = new FrmAttachmentDB();

                    frmAttachmentDB.setMyPK(frmAttachmentMyPk);
                    int count = frmAttachmentDB.Retrieve();
                    if (count == 0)
                    {
                        return Return_Info(500, "文件不存在", "");
                    }
                    FrmAttachment frmAttachment = new FrmAttachment();
                    frmAttachment.setMyPK(frmAttachmentDB.FK_FrmAttachment);
                    frmAttachment.Retrieve();

                    //获取配置项是否加密
                    bool fileEncrypt = SystemConfig.isEnableAthEncrypt;
                    string tempFile = "";
                    //获取此文件是否加密
                    bool isEncrypt = frmAttachmentDB.GetParaBoolen("IsEncrypt");

                    if (frmAttachment.AthSaveWay == AthSaveWay.IISServer)
                    {
                        if (fileEncrypt == true && isEncrypt == true)
                        {
                            //现将上传的文件转存为临时文件
                            tempFile = SystemConfig.PathOfTemp + frmAttachmentDB.MyPK + ".tmp";
                            if (System.IO.File.Exists(tempFile))
                            {
                                System.IO.File.Delete(tempFile);
                            }

                            //将上传的文件转为临时文件
                            HttpContextHelper.UploadFile(file, tempFile);

                            //将原始文件删除掉
                            if (System.IO.File.Exists(frmAttachmentDB.FileFullName))
                            {
                                System.IO.File.Delete(frmAttachmentDB.FileFullName);
                            }

                            //对临时文件进行加密并输出成新文件
                            EncHelper.EncryptDES(tempFile, frmAttachmentDB.FileFullName);
                        }
                        else
                        {
                            //将原始文件删除掉然后保存新文件
                            if (System.IO.File.Exists(frmAttachmentDB.FileFullName))
                            {
                                System.IO.File.Delete(frmAttachmentDB.FileFullName);
                            }
                            HttpContextHelper.UploadFile(file, frmAttachmentDB.FileFullName);
                        }
                    }

                    //数据库存储
                    if (frmAttachment.AthSaveWay == AthSaveWay.DB)
                    {
                        //将上传的文件存储为临时文件
                        tempFile = SystemConfig.PathOfTemp + frmAttachmentDB.MyPK + ".tmp";
                        if (System.IO.File.Exists(tempFile))
                        {
                            System.IO.File.Delete(tempFile);
                        }
                        //将将上传的文件存为临时文件
                        HttpContextHelper.UploadFile(file, tempFile);
                        if (fileEncrypt == true && isEncrypt == true)
                        {


                            //声明加密的文件路径
                            string tempEncryptFile = tempFile + ".encryptTemp";
                            if (System.IO.File.Exists(tempEncryptFile))
                            {
                                System.IO.File.Delete(tempEncryptFile);
                            }

                            //根据初试源文件生成加密后的文件
                            EncHelper.EncryptDES(tempFile, tempEncryptFile);

                            //保存流到数据库
                            frmAttachmentDB.SaveFileToDB(DBFile, tempEncryptFile);

                            //删除掉临时加密后的文件
                            if (System.IO.File.Exists(tempEncryptFile))
                            {
                                System.IO.File.Delete(tempEncryptFile);
                            }
                            if (System.IO.File.Exists(tempFile))
                            {
                                System.IO.File.Delete(tempFile);
                            }

                        }
                        else
                        {
                            frmAttachmentDB.SaveFileToDB(DBFile, tempFile);
                        }
                    }

                    if (frmAttachment.AthSaveWay == AthSaveWay.FTPServer)
                    {
                        //将上传的文件存储为临时文件
                        tempFile = SystemConfig.PathOfTemp + frmAttachmentDB.MyPK + ".tmp";
                        if (System.IO.File.Exists(tempFile))
                        {
                            System.IO.File.Delete(tempFile);//删除临时文件

                        }
                        HttpContextHelper.UploadFile(file, tempFile);
                        BP.FtpConnection ftpconn = null;
                        try
                        {

                            ftpconn = new BP.FtpConnection(SystemConfig.FTPServerIP,
                                          SystemConfig.FTPServerPort,
                                          SystemConfig.FTPUserNo, SystemConfig.FTPUserPassword);
                        }
                        catch
                        {
                            throw new Exception("err@FTP连接失败请检查账号,密码，端口号是否正确");
                        }
                        //2024_03//ND501//b93c100d-bc04-4cd5-ba74-32169f57da23.xlsx 拆分路径
                        string[] strsOld = frmAttachmentDB.FileFullName.Split(new string[] { "//" }, 0);

                        //判断目录年月是否存在.
                        if (ftpconn.DirectoryExist(strsOld[0]) == false)
                            ftpconn.CreateDirectory(strsOld[0]);
                        ftpconn.SetCurrentDirectory(strsOld[0]);

                        //判断目录是否存在.
                        if (ftpconn.DirectoryExist(strsOld[1]) == false)
                            ftpconn.CreateDirectory(strsOld[1]);

                        //设置当前目录，为操作的目录。
                        ftpconn.SetCurrentDirectory(strsOld[1]);

                        string newFileName = DBAccess.GenerGUID() + "." + frmAttachmentDB.FileExts;
                        //对路径重新生成guid名称避免ftp缓存
                        frmAttachmentDB.FileFullName = strsOld[0] + "//" + strsOld[1] + "//" + newFileName;
                        frmAttachmentDB.Update();

                        //新文件存在的话也删除掉
                        if (ftpconn.FileExist(newFileName))
                        {
                            ftpconn.DeleteFile(newFileName);
                        }
                        //加密文件的情况下
                        if (fileEncrypt == true && isEncrypt == true)
                        {
                            string tmpEncrypt = SystemConfig.PathOfTemp + frmAttachmentDB.MyPK + ".encryptTmp";
                            HttpContextHelper.UploadFile(file, tempFile);
                            EncHelper.EncryptDES(tempFile, tmpEncrypt);//加密

                            ftpconn.PutFile(tmpEncrypt, newFileName);

                            //删除掉加密的临时文件
                            if (System.IO.File.Exists(tmpEncrypt) == true)
                                System.IO.File.Delete(tmpEncrypt);
                        }
                        else
                        {
                            ftpconn.PutFile(tempFile, newFileName);
                        }
                        //将原始文件删除掉
                        if (ftpconn.FileExist(strsOld[2]))
                        {
                            ftpconn.DeleteFile(strsOld[2]);
                        }
                        ftpconn.Close();
                        ftpconn = null;
                    }
                    if (System.IO.File.Exists(tempFile))
                    {
                        System.IO.File.Delete(tempFile);
                    }
                }

                return "File uploaded successfully";
            }
            catch (Exception ex)
            {
                return Return_Info(500, ex.Message, "");
            }
        }
        #endregion

        /// <summary>
        /// 根据Token值登录
        /// </summary>
        /// <param name="token"></param>
        protected void Port_GenerToken(string token)
        {
            Dev2Interface.Port_LoginByToken(token);
        }
        public class ReportImage
        {
            public string ext;
            public string fileName;
            public string bytesData;
            public string mypk;
        }
    }
    #endregion

}
