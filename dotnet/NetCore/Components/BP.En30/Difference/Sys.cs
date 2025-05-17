using BP.DA;
using BP.Web;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;

namespace BP.Difference
{
    /// <summary>
    /// 公共的方法.
    /// </summary>
    public static class Glo
    {
        /// <summary>
        /// 获得ID地址
        /// </summary>
        public static string GetIP
        {
            get
            {
                return "127.0.0.1";
            }
        }
        public static string DealExp(string exp, Hashtable ht)
        {
            if (exp == null)
                exp = string.Empty;

            exp = exp.Replace("~", "'");
            exp = exp.Replace("/#", "+"); //为什么？
            exp = exp.Replace("/$", "-"); //为什么？
            if (exp.Contains("@WebUser.No"))
                exp = exp.Replace("@WebUser.No", BP.Web.WebUser.No);

            if (exp.Contains("@WebUser.Name"))
                exp = exp.Replace("@WebUser.Name", BP.Web.WebUser.Name);

            if (exp.Contains("@WebUser.FK_DeptName"))
                exp = exp.Replace("@WebUser.FK_DeptName", BP.Web.WebUser.DeptName);

            if (exp.Contains("@WebUser.FK_Dept"))
                exp = exp.Replace("@WebUser.FK_Dept", BP.Web.WebUser.DeptNo);

            if (exp.Contains("@WebUser.DeptNo"))
                exp = exp.Replace("@WebUser.DeptNo", BP.Web.WebUser.DeptNo);

            if (exp.Contains("@") == true && ht != null)
            {
                foreach (string key in ht.Keys)
                {
                    //值为空或者null不替换
                    if (ht[key] == null || ht[key].Equals("") == true)
                        continue;

                    if (exp.Contains("@" + key))
                        exp = exp.Replace("@" + key, ht[key].ToString());

                    //不包含@则返回SQL语句
                    if (exp.Contains("@") == false)
                        break;
                }
            }

            if (exp.Contains("@") && BP.Difference.SystemConfig.isBSsystem == true)
            {
                /*如果是bs*/
                foreach (string key in HttpContextHelper.RequestParamKeys)
                {
                    if (string.IsNullOrEmpty(key))
                        continue;
                    exp = exp.Replace("@" + key, HttpContextHelper.RequestParams(key));
                }
            }

            //if (exp.Contains("@") == true)
            //    throw new Exception("@外键类型SQL错误," + exp + "部分查询条件没有被替换.");

            return exp;
        }
        public static string RequestParas
        {
            get
            {
                string urlExt = "";
                string rawUrl = "";
                if (HttpContextHelper.Request != null && HttpContextHelper.Request.QueryString.HasValue)
                    rawUrl = HttpContextHelper.Request.QueryString.Value;
                rawUrl = rawUrl.Substring(1); // 去掉开头的问号?
                string[] paras = rawUrl.Split('&');
                foreach (string para in paras)
                {
                    if (para == null
                        || para == ""
                        || para.Contains("=") == false)
                        continue;
                    urlExt += "&" + para;
                }
                return urlExt;
            }
        }
        public static string DealSQLStringEnumFormat(string cfgString)
        {
            //把这个string,转化SQL. @tuanyuan=团员@dangyuan=党员
            AtPara ap = new AtPara(cfgString);
            string sql = "";
            foreach (string item in ap.HisHT.Keys)
            {
                sql += " SELECT '" + item + "' as No, '" + ap.GetValStrByKey(item) + "' as Name FROM Port_Emp WHERE No = 'admin' UNION ";
            }
            sql = sql.Substring(0, sql.Length - 6);
            return sql;
        }
    }
}
