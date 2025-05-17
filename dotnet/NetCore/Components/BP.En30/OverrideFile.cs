using BP.DA;
using BP.Sys;
using BP.Web;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;

namespace BP.En
{
    /// <summary>
    /// 可以被重写的类 
    /// </summary>
    public class OverrideFile
    {
        /// <summary>
        /// 获得webApi数据:
        /// </summary>
        /// <param name="ht">参数KeyVal的模式</param>
        /// <param name="requestMethod">POST/GET</param>
        /// <param name="sfDBSrcNo">数据源编号</param>
        /// <param name="apiUrl">执行部分</param>
        /// <returns>返回的数据</returns>
        public static string Data_WebApi(Hashtable ht, string requestMethod, string sfDBSrcNo, string apiUrl, string paramData = "", string remark = "")
        {

            SFDBSrc mysrc = new SFDBSrc(sfDBSrcNo);

            #region  GET 解析路径变量 /{xxx}/{yyy} ? xxx=xxx
            if (requestMethod.ToUpper().Equals("GET") == true)
            {
                apiUrl = apiUrl.Replace("@WebUser.No", WebUser.No);
                apiUrl = apiUrl.Replace("@WebUser.Name", WebUser.Name);
                apiUrl = apiUrl.Replace("@WebUser.DeptNo", WebUser.DeptNo);
                apiUrl = apiUrl.Replace("@WebUser.DeptName", WebUser.DeptName);
                apiUrl = apiUrl.Replace("@WebUser.OrgNo", WebUser.OrgNo);
                if (apiUrl.Contains("{") == true)
                {
                    if (ht != null)
                    {
                        foreach (string key in ht.Keys)
                        {
                            apiUrl = apiUrl.Replace("{" + key + "}", ht[key].ToString());
                        }
                    }
                    apiUrl = mysrc.ConnString + apiUrl;
                }
                else
                {
                    if (ht != null)
                    {
                        foreach (string key in ht.Keys)
                        {
                            apiUrl = apiUrl.Replace("@" + key, ht[key].ToString());
                        }
                    }
                    apiUrl = mysrc.ConnString + apiUrl;
                }
                return BP.Tools.PubGlo.HttpPostConnect(apiUrl, "", requestMethod);
            }
            if (apiUrl.StartsWith("http") == false)
                apiUrl = mysrc.ConnString + apiUrl;
            #endregion

            if (DataType.IsNullOrEmpty(paramData))
                paramData = "";
            paramData = paramData.Replace("~~", "\"");
            paramData = paramData.Replace("~", "\"");
            if (DataType.IsNullOrEmpty(paramData) == false)
            {
                paramData = paramData.Replace("@WebUser.No", WebUser.No);
                paramData = paramData.Replace("@WebUser.Name", WebUser.Name);
                paramData = paramData.Replace("@WebUser.DeptNo", WebUser.DeptNo);
                paramData = paramData.Replace("@WebUser.DeptName", WebUser.DeptName);
                paramData = paramData.Replace("@WebUser.OrgNo", WebUser.OrgNo);
                if (ht != null)
                {
                    foreach (string key in ht.Keys)
                    {
                        paramData = paramData.Replace("@" + key, ht[key].ToString());
                    }
                }
                //没有替换的@值，并且配置了别名
                if (paramData.Contains("@") == true && DataType.IsNullOrEmpty(remark) == false)
                {
                    AtPara atpara = new AtPara(remark);
                    foreach (string key in atpara.HisHT.Keys)
                    {
                        string alias = atpara.GetValStrByKey(key);
                        string[] strs = alias.Split(',');
                        foreach (string str in strs)
                        {
                            if (DataType.IsNullOrEmpty(str) == true)
                                continue;
                            paramData = paramData.Replace("@" + key, ht[str].ToString());
                        }
                    }
                }
            }

            return BP.Tools.PubGlo.HttpPostConnect(apiUrl, paramData, requestMethod, true);
        }

        #region 可以重写的表单事件.
        /// <summary>
        /// 执行的事件
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="en"></param>
        public static string FrmEvent_LoadBefore(string frmID, Entity en)
        {
            return null;
        }
        /// <summary>
        /// 装载填充的事件.
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="en"></param>
        public static string FrmEvent_FrmLoadAfter(string frmID, Entity en)
        {
            return null;
        }
        /// <summary>
        /// 保存前事件
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="en"></param>
        public static string FrmEvent_SaveBefore(string frmID, Entity en)
        {
            return null;
        }
        /// <summary>
        /// 保存后事件
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="en"></param>
        public static string FrmEvent_SaveAfter(string frmID, Entity en)
        {
            return null;
        }
        public static string FrmBill_CheckStart(string frmID, Entity en)
        {
            return null;
        }
        /// <summary>
        /// 单据实体审核结束以后
        /// </summary>
        /// <param name="frmID">单据ID</param>
        /// <param name="en">实体</param>
        public static string FrmBill_CheckOver(string frmID, Entity en)
        {
            return null;
        }
        /// <summary>
        /// 单据重新审核(回滚)
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="en"></param>
        /// <returns></returns>
        public static string FrmBill_Reback(string frmID, Entity en)
        {
            return null;
        }
        /// <summary>
        /// 单据实体撤销审核
        /// </summary>
        /// <param name="frmID"></param>
        /// <param name="en"></param>
        public static string FrmBill_UnSend(string frmID, Entity en)
        {
            return null;
        }
        public static string FrmBill_OverBefore(string frmID, Entity en)
        {
            return null;
        }
        public static string FrmBill_OverAfter(string frmID, Entity en)
        {
            return null;
        }
        #endregion 可以重写的表单事件.
    }
}
