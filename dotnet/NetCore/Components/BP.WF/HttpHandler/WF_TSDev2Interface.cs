using System;
using System.Collections.Generic;
using System.Data;
using System.Text;
using System.Web;
using BP.DA;
using BP.Sys;
using BP.Web;
using BP.Port;
using BP.En;
using BP.WF;
using BP.WF.Template;
using BP.Difference;
using System.IO;
using System.Collections;
using NPOI.Util;

namespace BP.WF.HttpHandler
{
    /// <summary>
    /// 页面功能实体
    /// </summary>
    public class WF_TSDev2Interface : BP.WF.HttpHandler.DirectoryPageBase
    {
        #region 参数.
        public string Paras
        {
            get
            {
                return this.GetRequestVal("Paras");
            }
        }
        #endregion
        /// <summary>
        /// 构造函数
        /// </summary>
        public WF_TSDev2Interface()
        {
        }
        public string Flow_Start()
        {
            BP.WF.HttpHandler.WF_MyFlow hand = new WF_MyFlow();
            return hand.MyFlow_Init();
        }
        /// <summary>
        /// 创建空白的WorkID.
        /// </summary>
        /// <returns></returns>
        public string Node_CreateBlankWork()
        {
            string strs = this.Paras;
            AtPara ap = new AtPara(strs);
            Int64 workid = BP.WF.Dev2Interface.Node_CreateBlankWork(this.FlowNo, ap.HisHT);
            return workid.ToString();
        }
        public string Node_SetDraft()
        {
            BP.WF.Dev2Interface.Node_SetDraft(this.WorkID);
            return "执行成功";
        }
        /// <summary>
        /// 执行发送动作.
        /// </summary>
        /// <returns></returns>
        public string Node_SendWork()
        {
            string toEmps = this.GetRequestVal("ToEmps");
            return BP.WF.Dev2Interface.Node_SendWork(this.FlowNo, this.WorkID, this.ToNodeID, toEmps).ToMsgOfText();
        }
        public string Flow_DeleteFlow()
        {
            return BP.WF.Dev2Interface.Flow_DoDeleteFlowByReal(this.WorkID, false);
        }

        /// <summary>
        /// 删除草稿
        /// </summary>
        /// <returns></returns>
        public string Flow_DoDeleteDraft()
        {
            return BP.WF.Dev2Interface.Flow_DoDeleteDraft(this.FlowNo, this.WorkID, false);
        }
        /// <summary>
        /// 执行退回操作
        /// </summary>
        /// <returns></returns>
        public string Node_ReturnWork()
        {
            string msg = this.GetRequestVal("Msg");
            return BP.WF.Dev2Interface.Node_ReturnWork(this.WorkID, this.ToNodeID, msg, false);
        }
        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public string Port_SendMsg()
        {
            string title = this.GetRequestVal("Title");
            string docs = this.GetRequestVal("Docs");
            string toEmpNos = this.GetRequestVal("ToEmpNos");

            Dev2Interface.Port_SendMsg(toEmpNos, title, docs, "Msg", "Msg", "", 0, 0, 0, null);
            return "执行成功.";
        }
        public string Port_SendMsgByBillInfo()
        {
            string title = this.GetRequestVal("Title");
            string docs = this.GetRequestVal("Docs");
            string toEmpNos = this.GetRequestVal("ToEmpNos");
            string frmID = this.GetRequestVal("FrmID");
            Int64 workID = this.GetRequestValInt64("WorkID");

            Dev2Interface.Port_SendMsg(toEmpNos, title, docs, "FrmBill"+workID+frmID, "FrmBill", frmID, workID, 0, 0, null);
            return "执行成功.";
        }
        public string Port_SendMsgByFlowInfo()
        {
            string title = this.GetRequestVal("Title");
            string docs = this.GetRequestVal("Docs");
            string toEmpNos = this.GetRequestVal("ToEmpNos");
            Int64 workID = this.GetRequestValInt64("WorkID");

            GenerWorkFlow gwf = new GenerWorkFlow(workID);
            Dev2Interface.Port_SendMsg(toEmpNos, title, docs, "FlowInfo"+workID, "FlowInfo", gwf.FlowNo, workID, 0, 0, null);
            return "执行成功.";
        }

        public string UploadFile()
        {

            try
            {
                string fileName = this.GetRequestVal("fileName");
                if (HttpContextHelper.RequestFilesCount == 0)
                    return "err@请选择要上传的文件。";

                string fileType = this.GetRequestVal("fileType");

                string path = BP.Difference.SystemConfig.PathOfDataUser + "UploadFile";

                if (!DataType.IsNullOrEmpty(fileType))
                {
                    path = path + "/" + fileType;
                }
                if (!System.IO.Directory.Exists(path))
                    System.IO.Directory.CreateDirectory(path);

                string distPath = fileName;
                if (!DataType.IsNullOrEmpty(fileType))
                {
                    distPath = fileType + "/" + distPath;
                }
                string filePath = path + "/" + fileName;
                string relativePath = "/DataUser/UploadFile/" + distPath;
                if (System.IO.File.Exists(filePath))
                {
                    System.IO.File.Delete(filePath);
                }
                //这里使用绝对路径来索引
                HttpContextHelper.UploadFile(HttpContextHelper.RequestFiles(0), filePath);
                return relativePath;
            }
            catch (IOException ex)
            {
                return ex.ToString();
            }

        }
    }
}
