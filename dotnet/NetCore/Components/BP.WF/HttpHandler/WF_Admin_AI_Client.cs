using System;
using System.Collections;
using System.Data;
using System.Diagnostics;
using System.IO;
using System.Net;
using System.Text;
using BP.DA;
using BP.En;
using BP.Sys;
using BP.Tools;
using BP.WF.Data;
using static System.Net.WebRequestMethods;

namespace BP.WF.HttpHandler
{
    /// <summary>
    /// AI表单
    /// </summary>
    public class WF_Admin_AI_Client
    {
        #region 公共变量.
        //BPM服务器地址.
        public static string BPMHost_Local = BP.Difference.SystemConfig.AppSettings["HostURLAI"];
        //public static string BPMHost = "http://shengchan.ccflow.org";
        public static string BPMHost = "http://101.43.32.218:8616";
        // + "/WF/API";
        //密钥.
        public static string PrivateKey = "DiGuaDiGua,IamCCBPM";

        //拼接访问地址
        static String webPath = "";
        #endregion 公共变量.

        #region AI接口.
        /// <summary>
        /// 通用问答
        /// </summary>
        /// <param name="word"></param>
        /// <returns></returns>
        public static string GenerAsk(string words, string names, Boolean debugMode = false, Boolean localMode = false)
        {
            //string subPath = "/generate/attrs";
            //webPath = BPMHost + subPath;
            //String postData = HttpPostConnect(webPath + "?token=xxx&Words=" + words + "&names=" + names + debugCmd, "");
            //return postData;
            return "此部分尚未完成.";
        }
        public static string CopilotCmd(string words, string names, Boolean debugMode = false, Boolean localMode = false)
        {
            string subPath = "/copilot/cmd";
            webPath = BPMHost + subPath;
            if (localMode && !String.IsNullOrEmpty(BPMHost_Local))
            {
                webPath = BPMHost_Local + subPath;
            }
            String debugCmd = debugMode ? "&debug=1" : "";
            String postData = HttpPostConnect(webPath + "?token=xxx&Words=" + words + "&names=" + names + debugCmd, "");
            return postData;
        }
        

        /// <summary>
        /// 根据模板生成代码
        /// </summary>
        /// <param name="words">模板</param>
        /// <param name="ddl">表字段</param>
        /// <returns></returns>
        public static string AIFrm_GenerCode(string words, string ddl, string codefomat= "javascript", Boolean debugMode = false, Boolean localMode = false)
        {
            string subPath = "/generate/code";
            webPath = BPMHost + subPath;
            if (localMode && !String.IsNullOrEmpty(BPMHost_Local))
            {
                webPath = BPMHost_Local + subPath;
            }

            String debugCmd = debugMode ? "&debug=1" : "";
            //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
            String postData = "Words=" + words + "&ddl=" + ddl;
            //String postData = "Words=word1&ddl=ddl1";
            String responseData = HttpPostConnect(webPath + "?token=xxx&code=" + codefomat + debugCmd, postData);
            return responseData;
        }

        public static string AIFrm_GenerCode_0(string words, string ddl, string codefomat = "javascript", Boolean debugMode = false, Boolean localMode = false)
        {
            string subPath = "/generate/code";
            webPath = BPMHost + subPath;
            if (localMode && !String.IsNullOrEmpty(BPMHost_Local))
            {
                webPath = BPMHost_Local + subPath;
            }

            //String postData = HttpPostConnect(webPath + "?token=xxx", json, "POST",true);
            String debugCmd = debugMode ? "&debug=1" : "";
            //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
            String postData = HttpPostConnect(webPath + "?token=xxx&Words=" + words + "&code=" + codefomat + "&ddl=" + ddl + debugCmd, "");
            return postData;
        }

        /// <summary>
        /// 获得分析数据
        /// </summary>
        /// <param name="frmID">表单ID</param>
        /// <param name="words">提示词</param>
        /// <returns></returns>
        public static string AIFrm_GenerFX(string frmID, string words, Boolean debugMode = false, Boolean localMode = false)
        {
            string subPath = "/find/rules";
            webPath = BPMHost + subPath;
            if (localMode && !String.IsNullOrEmpty(BPMHost_Local))
            {
                webPath = BPMHost_Local + subPath;
            }

            //String postData = HttpPostConnect(webPath + "?token=xxx", json, "POST",true);
            String debugCmd = debugMode ? "&debug=1" : "";
            //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
            String postData = HttpPostConnect(webPath + "?token=xxx&Words=" + words + debugCmd, "");
            return postData;
        }
        /// <summary>
        /// 通过AI获得字段.
        /// </summary>
        /// <param name="words">提示词</param>
        /// <param name="names">已经有的字段名称</param>
        /// <param name="debugMode">是否启动debug模式，debug
        /// debug=1，在AIHost端，不调用大模型，直接返回一个预置的json，用于调试json格式目的。
        /// debug=0，则调用大模型，返回值是实际的数据  
        /// </param>
        /// <param name="localMode">是否启动本地模式
        /// 本地模式就是指在本地建立AIHost服务进行调用。
        /// 本地模式后，仍需设置0实际访问模式或者Debug模式
        /// </param>
        /// <returns>返回结果dataSet模式的.</returns>
        public static string AIFrm_GenerAttrs_ByWords(String frmID, string words, string names, Boolean debugMode = false, Boolean localMode = false)
        {
            string subPath = "/generate/attrs";
            webPath = BPMHost + subPath;
            if (localMode && !String.IsNullOrEmpty(BPMHost_Local))
            {
                webPath = BPMHost_Local + subPath;
            }

            //String postData = HttpPostConnect(webPath + "?token=xxx", json, "POST",true);
            String debugCmd = debugMode ? "&debug=1" : "";
            //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
            String postData = HttpPostConnect(webPath + "?token=xxx&Words=" + words + "&names=" + names + debugCmd, "");
            return postData;
        }

        /// <summary>
        /// 通过AI获得从表
        /// </summary>
        /// <param name="words">提示词</param>
        /// <param name="names">已经有的从表名称</param>
        /// <returns></returns>
        public static string AIFrm_GenerDetails_ByWord(string words, string names, Boolean debugMode = false, Boolean localMode = false)
        {
            string subPath = "/generate/details";
            webPath = BPMHost + subPath;
            if (localMode && !String.IsNullOrEmpty(BPMHost_Local))
            {
                webPath = BPMHost_Local + subPath;
            }

            //String postData = HttpPostConnect(webPath + "?token=xxx", json, "POST",true);
            String debugCmd = debugMode ? "&debug=1" : "";
            //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
            String postData = HttpPostConnect(webPath + "?token=xxx&Words=" + words + "&names=" + names + debugCmd, "");
            return postData;
        }
    

        /// <summary>
        /// 通过AI获得节点
        /// </summary>
        /// <param name="words">提示词</param>
        /// <param name="existnodes">已经加入的节点名集合</param>
        /// <param name="debugMode">是否启动debug模式，debug
        /// debug=1，在AIHost端，不调用大模型，直接返回一个预置的json，用于调试json格式目的。
        /// debug=0，则调用大模型，返回值是实际的数据
        /// </param>
        /// <param name="localMode">是否启动本地模式
        /// 本地模式就是指在本地建立AIHost服务进行调用。
        /// 本地模式也有实际访问大模型，以及Debug模式
        /// </param>
        /// <returns>退回节点信息，json格式</returns>
        public static DataTable AIFlow_GenerFlow_ByWords(string words, string existnodes = "", Boolean debugMode = false, Boolean localMode = false)
        {
            string subPath = "/generate/nodes";
            webPath = BPMHost + subPath;
            if (localMode && !String.IsNullOrEmpty(BPMHost_Local))
                webPath = BPMHost_Local + subPath;

            words = words.Trim();
            //String postData = HttpPostConnect(webPath + "?token=xxx", json, "POST",true);
            String debugCmd = debugMode ? "&debug=1" : "";
            //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
            string postData = HttpPostConnect(webPath + "?token=xxx&Words=" + words + "&existnodes=" + existnodes + debugCmd, "");

            DataTable dt = new DataTable();
            if (string.IsNullOrEmpty(postData) || postData == "[]")
            {
                dt.Columns.Add(new DataColumn("nodeNo"));
                dt.Columns.Add(new DataColumn("nodeName"));
                dt.Columns.Add(new DataColumn("nodeRole"));
                dt.Columns.Add(new DataColumn("nodeDesc"));
                dt.Columns.Add(new DataColumn("flowName"));
            }
            else
            {
                dt = BP.Tools.Json.ToDataTable(postData);
            }

            
            //   dt.TableName = "我的流程";
            dt.Columns["nodeNo"].ColumnName = "No";
            dt.Columns["nodeName"].ColumnName = "Name";
            dt.Columns["nodeRole"].ColumnName = "Stations";
            dt.Columns["nodeDesc"].ColumnName = "Desc";
            dt.Columns["flowName"].ColumnName = "AI生成的流程";
            return dt;
        }
        public static string App_ByWords(string words, Boolean debugModel = false, Boolean localMode = false)
        {
            return AIFlow_GenerApp_ByWords(words, "", debugModel, localMode);
        }

        /// <summary>
        /// 通过AI获得应用
        /// 模块，菜单，操作
        /// </summary>
        /// <param name="words">提示词</param>
        /// <param name="existmenus">已经加入的menu集合</param>
        /// <param name="debugMode">是否启动debug模式，debug
        /// debug=1，在AIHost端，不调用大模型，直接返回一个预置的json，用于调试json格式目的。
        /// debug=0，则调用大模型，返回值是实际的数据
        /// </param>
        /// <param name="localMode">是否启动本地模式
        /// 本地模式就是指在本地建立AIHost服务进行调用。
        /// 本地模式也有实际访问大模型，以及Debug模式
        /// </param>
        /// <returns>退回节点信息，json格式</returns>
        public static string AIFlow_GenerApp_ByWords(string words, string existmenus = "", Boolean debugMode = false, Boolean localMode = false)
        {
            string subPath = "/generate/menus";
            webPath = BPMHost + subPath;
            if (localMode && !String.IsNullOrEmpty(BPMHost_Local))
            {
                webPath = BPMHost_Local + subPath;
            }

            words = words.Trim();
            //String postData = HttpPostConnect(webPath + "?token=xxx", json, "POST",true);
            String debugCmd = debugMode ? "&debug=1" : "";
            //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
            string postData = HttpPostConnect(webPath + "?token=xxx&Words=" + words + "&existnodes=" + existmenus + debugCmd, "");

            //DataTable dt = new DataTable();
            DataSet ds = new DataSet();
            if (string.IsNullOrEmpty(postData) || postData == "[]")
            {
            }
            else
            {
                //ds = BP.Tools.Json.ToDataSet(postData);
            }

            return postData;
        }

        public static string Text_App_ByWords(string words, Boolean Debug = false)
        {
            DataSet ds = new DataSet();

            #region 0. 基础信息. - 系统名称.
            DataTable dt = new DataTable();
            dt.TableName = "BaseInfo";
            dt.Columns.Add("SystemNo");
            dt.Columns.Add("SystemName");

            DataRow dr = dt.NewRow();
            dr[0] = "car";
            dr[1] = "车辆管理";
            dt.Rows.Add(dr);
            ds.Tables.Add(dt);
            #endregion 0. 基础信息.

            #region 1.生成模块.
            dt = new DataTable();
            dt.TableName = "Moduel";
            dt.Columns.Add("No");
            dt.Columns.Add("Name");

            dr = dt.NewRow();
            dr[0] = "001"; //模块编号
            dr[1] = "车辆台账管理";
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr[0] = "002"; //模块编号
            dr[1] = "加油信息管理";
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr[0] = "003"; //模块编号
            dr[1] = "报告与统计";
            dt.Rows.Add(dr);
            ds.Tables.Add(dt); //增加一个dataset.
            #endregion 1. 生成模块

            #region 2.生成菜单.
            dt = new DataTable();
            dt.TableName = "Menu";
            dt.Columns.Add("No");
            dt.Columns.Add("Name");
            dt.Columns.Add("MenuType");
            dt.Columns.Add("ModuelNo"); //模块的编号.

            dr = dt.NewRow();
            dr["No"] = "001";
            dr["Name"] = "车辆台账";
            dr["MenuType"] = "Dict";
            dr["ModuelNo"] = "001"; //模块编号.
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr["No"] = "002";
            dr["Name"] = "加油记录列表";
            dr["MenuType"] = "Bill";
            dr["ModuelNo"] = "002"; //模块编号.
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr["No"] = "005";
            dr["Name"] = "车辆使用报告";
            dr["MenuType"] = "Rpt";
            dr["ModuelNo"] = "003"; //模块编号.

            dr = dt.NewRow();
            dr["No"] = "005";
            dr["Name"] = "加油费用统计";
            dr["MenuType"] = "Rpt";
            dr["ModuelNo"] = "003"; //模块编号.
            dt.Rows.Add(dr);
            ds.Tables.Add(dt);
            #endregion 2.生成菜单.

            string str= BP.Tools.Json.ToJson(ds);
            return str;
        }

        public static string Test_AIFlow_GenerFlow_ByWords(string words, string existnodes = "", Boolean Debug = false)
        {
            DataSet ds = new DataSet();

            DataTable dt = new DataTable();
            dt.Columns.Add("No");
            dt.Columns.Add("Name");
            dt.Columns.Add("X");
            dt.Columns.Add("Y");

            DataRow dr = dt.NewRow();
            dr[0] = "001";
            dr[1] = "填写申请";
            dr[2] = "100";
            dr[3] = "200";
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr[0] = "002";
            dr[1] = "部门审批";
            dr[2] = "100";
            dr[3] = "200";
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr[0] = "003";
            dr[1] = "人力资源审批";
            dr[2] = "100";
            dr[3] = "200";
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr[0] = "004";
            dr[1] = "反馈给申请人";
            dr[2] = "100";
            dr[3] = "200";
            dt.Rows.Add(dr);

            ds.Tables.Add(dt); //增加一个dataset.
            return BP.Tools.Json.ToJson(dt);
        }
        public static string AIFlow_GenerNodesAndFrm_ByFile(string words)
        {
            return null;
        }
        /// <summary>
        /// 通过AI获得App应用
        /// </summary>
        /// <param name="words"></param>
        /// <param name="names"></param>
        /// <returns></returns>
        public static string AIApp_GenerApp(string words, string names)
        {
            webPath = BPMHost + "/generate/table";
            String postData = HttpPostConnect(webPath + "?token=XXX&Words=" + words + "&names=" + names, "");
            return postData;
        }

        /// <summary>
        /// 通过AI获得图标关联.
        /// </summary>
        /// <param name="words">提示词</param>
        /// <param name="debugMode">是否启动debug模式，debug
        /// debug=1，在AIHost端，不调用大模型，直接返回一个预置的json，用于调试json格式目的。
        /// debug=0，则调用大模型，返回值是实际的数据  
        /// </param>
        /// <param name="localMode">是否启动本地模式
        /// 本地模式就是指在本地建立AIHost服务进行调用。
        /// 本地模式后，仍需设置0实际访问模式或者Debug模式
        /// </param>
        /// <returns>返回结果dataSet模式的.</returns>
        public static string AIFrm_MatchIcons_ByWords( string words, Boolean debugMode = false, Boolean localMode = false)
        {
            string subPath = "/match/icons";
            webPath = BPMHost + subPath;
            if (localMode && !String.IsNullOrEmpty(BPMHost_Local))
            {
                webPath = BPMHost_Local + subPath;
            }

            //String postData = HttpPostConnect(webPath + "?token=xxx", json, "POST",true);
            String debugCmd = debugMode ? "&debug=1" : "";
            //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
            String postData = HttpPostConnect(webPath + "?token=xxx&Words=" + words + "&names=" +  debugCmd, "");
            return postData;
        }

        /// <summary>
        /// 生成测试数据
        /// </summary>
        /// <param name="words">提示词</param>
        /// <returns></returns>
        public static string AiFrm_GenerTestDB_ByWords( string words, Boolean debugMode = false, Boolean localMode = false)
        {
            string subPath = "/generate/testdata";
            webPath = BPMHost + subPath;
            if (localMode && !String.IsNullOrEmpty(BPMHost_Local))
            {
                webPath = BPMHost_Local + subPath;
            }

            //String postData = HttpPostConnect(webPath + "?token=xxx", json, "POST",true);
            String debugCmd = debugMode ? "&debug=1" : "";
            //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
            String postData = HttpPostConnect(webPath + "?token=xxx&Words=" + words + debugCmd, "");
            return postData;
        }

        
        /// <summary>
        /// 生成大屏分析
        /// </summary>
        /// <param name="words">提示词</param>
        /// <returns></returns>
        public static string AiFrm_GenerWindows_ByWords(string words, Boolean debugMode = false, Boolean localMode = false)
        {
            string subPath = "/generate/chartsql";
            webPath = BPMHost + subPath;
            if (localMode && !String.IsNullOrEmpty(BPMHost_Local))
            {
                webPath = BPMHost_Local + subPath;
            }

            //String postData = HttpPostConnect(webPath + "?token=xxx", json, "POST",true);
            String debugCmd = debugMode ? "&debug=1" : "";
            //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
            String postData = HttpPostConnect(webPath + "?token=xxx&Words=" + words + debugCmd, "");
            return postData;
        }

        #endregion AI接口.

        public static string HttpPostConnect(string serverUrl, string postData, string requestMethod = "POST", bool isJsonModel = false)
        {

            var dataArray = Encoding.UTF8.GetBytes(postData);
            //创建请求
            var request = (HttpWebRequest)HttpWebRequest.Create(serverUrl);
            request.Method = requestMethod;
            request.ContentLength = dataArray.Length;
            //设置上传服务的数据格式  设置之后不好使
            //request.ContentType = "application/x-www-form-urlencoded";
            //请求的身份验证信息为默认
            request.Credentials = CredentialCache.DefaultCredentials;

            if (isJsonModel == true)
                request.ContentType = "application/json";
            else
                request.ContentType = "application/x-www-form-urlencoded";

            ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12;
            //请求超时时间
            request.Timeout = 1000 * 120;
            //创建输入流
            Stream dataStream;
            if (requestMethod.ToLower().Equals("get") == false)
            {
                try
                {
                    dataStream = request.GetRequestStream();
                    //发送请求
                    dataStream.Write(dataArray, 0, dataArray.Length);
                    dataStream.Close();
                }
                catch (Exception)
                {
                    return "0";//连接服务器失败
                }
            }

            HttpWebResponse res;
            try
            {
                res = (HttpWebResponse)request.GetResponse();
            }
            catch (WebException ex)
            {
                res = (HttpWebResponse)ex.Response;
            }
            StreamReader sr = new StreamReader(res.GetResponseStream(), Encoding.UTF8);
            //读取返回消息
            string data = sr.ReadToEnd();
            sr.Close();
            return data;
        }

    }
}
