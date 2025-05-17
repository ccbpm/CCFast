using System;
using System.Collections;
using System.ComponentModel.DataAnnotations;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.Linq;
using System.Security.Cryptography;
using System.ServiceModel.Channels;
using System.Text.Json.Nodes;
using System.Text.RegularExpressions;
using System.Threading;
using System.Web.Http.Results;
using BP.CCFast.Portal;
using BP.DA;
using BP.Difference;
using BP.En;
using BP.Sys;
using BP.Sys.XML;
using BP.TA;
using BP.Tools;
using BP.Web;
using BP.WF.Template;
using FluentFTP;
using Google.Protobuf.WellKnownTypes;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Connections;
using Newtonsoft.Json.Linq;
using NPinyin;
using NPOI.HPSF;
using NPOI.OpenXmlFormats.Dml.Diagram;
using NPOI.OpenXmlFormats.Spreadsheet;
using NPOI.SS.Formula.Eval;
using NPOI.SS.Formula.Functions;
using Spire.Doc;
using DataType = BP.DA.DataType;

namespace BP.WF.HttpHandler
{
    /// <summary>
    /// AI表单
    /// </summary>
    public class WF_Admin_AI_Copilot : BP.WF.HttpHandler.DirectoryPageBase
    {
        public string Words
        {
            get
            {
                return this.GetRequestVal("Words");
            }
        }
        /// <summary>
        /// 发起指定的流程
        /// </summary>
        /// <returns></returns>
        public string Menu_Start_SpecFlow()
        {
            string sql = "";
            if (SystemConfig.CCBPMRunModel== CCBPMRunModel.Single)
              sql = "SELECT No,Name FROM WF_Flow WHERE Name LIKE '%" + this.Words + "%'";
            else
                sql = "SELECT No,Name FROM WF_Flow WHERE Name LIKE '%" + this.Words + "%' AND OrgNo='"+WebUser.OrgNo+"'";
            DataTable dt = DBAccess.RunSQLReturnTable(sql);
            return BP.Tools.Json.ToJson(dt);
        }
        /// <summary>
        /// 构造函数
        /// </summary>
        public WF_Admin_AI_Copilot()
        {
        }
    }
}
