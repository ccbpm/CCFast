using Microsoft.AspNetCore.Mvc;
using System.Collections;
using BP.DA;
using BP.Difference;
using System.Data;

namespace CCFlow.NetCore.DataUser.API.Controllers
{
    [Route("WF/[controller]/[Action]")]
    [ApiController]
    public class DemoController : ControllerBase
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
        /// 获得人员
        /// </summary>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object Port_Emps(string deptNo)
        {
            Paras ps=new Paras();
            ps.SQL=  "SELECT No,Name FROM Port_Emp WHERE FK_Dept="+ps.DBStr+"DeptNo";
            ps.Add("DeptNo", deptNo);
            DataTable dt = DBAccess.RunSQLReturnTable(ps);
            string json = BP.Tools.Json.ToJson(dt);
            return Return_Info(200, "执行成功", json);
        }
        /// <summary>
        /// 获得部门
        /// </summary>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object Port_Depts()
        {
            string sql = "SELECT No,Name,ParentNo FROM Port_Dept ";
            DataTable dt = DBAccess.RunSQLReturnTable(sql);
            string json = BP.Tools.Json.ToJson(dt);
            return Return_Info(200, "执行成功", json);
        }
        /// <summary>
        /// 执行过程
        /// </summary>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object Demo_Produces()
        {
            string sql = "SELECT No,Name,ParentNo FROM Port_Dept ";
            DataTable dt = DBAccess.RunSQLReturnTable(sql);
            string json = BP.Tools.Json.ToJson(dt);
            return Return_Info(200, "执行成功", json);
        }
    }
}
