using BP.CCBill;
using BP.DA;
using BP.Difference;
using BP.En;
using BP.Sys;
using BP.Web;
using BP.WF;
using BP.WF.HttpHandler;
using BP.WF.Template;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Routing;
using NPOI.SS.Formula.Eval;
using System;
using System.Collections;
using System.Data;
using System.Linq;
using System.Net.Http;

namespace CCFlow.NetCore.DataUser.API.Controllers
{
    [Route("WF/[controller]/[Action]")]
    [ApiController]
    public class CCFromAPI : ControllerBase
    {
        /// <summary>
        /// 返回信息格式
        /// </summary>
        /// <param name="code">200=成功，500=异常</param>
        /// <param name="msg"></param>
        /// <param name="data"></param>
        /// <returns></returns>
        protected static object Return_Info(int code, string msg, string data)
        {
            Hashtable ht = new Hashtable();
            ht.Add("code", code); // 200=成功，500=失败.
            ht.Add("message", msg);  //执行信息.
            ht.Add("data", data);  //一般是json.
            return ht;
            //string json = "{\"code\":" + code + ",\"msg\":\"" + msg + "\",\"data\":\"" + data + "\"}";
            //return new HttpResponseMessage { Content = new StringContent(json, System.Text.Encoding.GetEncoding("UTF-8"), "application/json") };
        }

        #region 组织结构接口.
        /// <summary>
        /// 切换部门-角色
        /// </summary>
        /// <param name="token">密钥</param>
        /// <param name="deptNo">部门编号</param>
        /// <param name="staNo">角色编号,可以为空.</param>
        /// <returns>执行结果</returns>
        [HttpGet, HttpPost]
        public object Port_Change(string token, string deptNo, string staNo = null)
        {
            //根据token登录
            Port_GenerToken(token);
            try
            {
                //  Int64 workid = Dev2Interface.Port_CheckUserLogin(flowNo, BP.Web.WebUser.No);
                return Return_Info(200, "创建WorkID成功", "ZHING");
            }
            catch (Exception ex)
            {
                return Return_Info(500, "创建WorkID失败", ex.Message);
            }
        }
        /// <summary>
        /// 系统登陆
        /// </summary>
        /// <param name="privateKey">密钥,默认:DiGuaDiGua,IamCCBPM</param>
        /// <param name="userNo">登陆账号</param>
        /// <param name="orgNo">组织编号</param>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object Port_Login(string privateKey, string userNo, string orgNo = null)
        {
            try
            {
                if (DataType.IsNullOrEmpty(userNo) == true)
                    return Return_Info(500, "账号不能为空", "");

                string localKey = SystemConfig.GetValByKey("PrivateKey", "DiGuaDiGua,IamCCBPM");
                if (SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
                {
                    BP.WF.Port.Admin2Group.Org org = new BP.WF.Port.Admin2Group.Org(orgNo);
                    string key = org.GetValStrByKey("PrivateKey");
                    if (DataType.IsNullOrEmpty(key) == false)
                        localKey = key;
                }

                if (DataType.IsNullOrEmpty(localKey) == true)
                    localKey = "DiGuaDiGua,IamCCBPM";
                if (localKey.Equals(privateKey) == false)
                    return Return_Info(500, "私约错误，请检查全局文件中配置 PrivateKey", "");

                //执行本地登录.
                BP.WF.Dev2Interface.Port_Login(userNo, orgNo);
                string token = BP.WF.Dev2Interface.Port_GenerToken();

                Hashtable ht = new Hashtable();
                ht.Add("No", WebUser.No);
                ht.Add("Name", WebUser.Name);
                ht.Add("FK_Dept", WebUser.DeptNo);
                ht.Add("FK_DeptName", WebUser.DeptName);
                ht.Add("OrgNo", WebUser.OrgNo);
                ht.Add("OrgName", WebUser.OrgName);
                ht.Add("Token", token);
                // return ReturnMessage();
                return Return_Info(200, "登陆成功", BP.Tools.Json.ToJson(ht));
            }
            catch (Exception ex)
            {
                return Return_Info(500, "登陆失败", ex.Message);
            }
        }
        /// <summary>
        /// 退出登录
        /// </summary>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object Port_LoginOut(string userNo, string orgNo = null)
        {
            try
            {
              BP.WF.Dev2Interface.Port_SigOut();
                return Return_Info(200, "退出成功", null);
            }
            catch (Exception ex)
            {
                return Return_Info(500, "退出失败", ex.Message);
            }
        }
        /// <summary>
        /// 根据Token值登录
        /// </summary>
        /// <param name="token"></param>
        [HttpGet, HttpPost]
        protected void Port_GenerToken(string token)
        {
            BP.WF.Dev2Interface.Port_LoginByToken(token);
        }
        #endregion 组织结构接口.


        
    }

}
