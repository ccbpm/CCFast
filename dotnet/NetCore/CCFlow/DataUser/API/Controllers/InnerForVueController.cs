using BP.DA;
using BP.Difference;
using BP.Sys;
using BP.Web;
using System.Collections;
using System.Data;
using System;
using Microsoft.AspNetCore.Mvc;

namespace CCFlow.NetCore.DataUser.API.Controllers
{
    [Route("WF/[controller]/[Action]")]
    [ApiController]
    public class InnerForVueController: ControllerBase
    {
        /// <summary>
        /// 返回信息格式:此数据格式可以根据自己的需要来定义.
        /// </summary>
        /// <param name="code">代码:200=成功，500=失败.</param>
        /// <param name="msg">消息:错误的信息.</param>
        /// <param name="data">数据:相关的json格式的数据.</param>
        /// <returns></returns>
        public static Object Return_Info(int code, string msg, object data)
        {
            Hashtable ht = new Hashtable();
            ht.Add("code", code);
            ht.Add("message", msg);
            ht.Add("data", data);
            return ht;
        }

        [HttpGet, HttpPost]
        public object Search_Emps(string token, string keyword)
        {
            //根据token登录
            BP.WF.Dev2Interface.Port_LoginByToken(token);

            string sqlTemplate = "";
            string execSql = "";
            if (DataType.IsNullOrEmpty(keyword))
            {
                return "err@请输入关键字";
            }
            if (SystemConfig.CCBPMRunModel == CCBPMRunModel.Single)
            {
                sqlTemplate = "SELECT A.No,A.Name,B.Name AS DeptName FROM Port_Emp A,Port_Dept B WHERE A.FK_Dept=B.No AND ( A.No LIKE '%@Key%' OR A.Name LIKE '%@Key%' OR PinYin LIKE  '%@Key%') AND A.EmpSta = 0 Order By A.Idx ";
                execSql = sqlTemplate.Replace("@Key", keyword);
                Console.WriteLine(execSql);
            }
            else
            {
                sqlTemplate = "SELECT A.No,A.Name,B.Name AS DeptName FROM Port_Emp A,Port_Dept B WHERE A.FK_Dept=B.No AND ( A.No LIKE '%@Key%' OR A.Name LIKE '%@Key%' OR PinYin LIKE  '%@Key%' AND A.OrgNo='@WebUser.OrgNo') AND A.EmpSta = 0 Order By A.Idx ";
                execSql = sqlTemplate.Replace("@Key", keyword);
                execSql = execSql.Replace("@WebUser.OrgNo", WebUser.OrgNo);
            }
            if (SystemConfig.AppCenterDBType == DBType.MSSQL)
                execSql = execSql.Replace("SELECT", "SELECT Top 20");
            else if (SystemConfig.AppCenterDBType == DBType.Oracle
                    || SystemConfig.AppCenterDBType == DBType.DM
                    || SystemConfig.AppCenterDBType == DBType.KingBaseR3
                    || SystemConfig.AppCenterDBType == DBType.KingBaseR6
                    || SystemConfig.AppCenterDBType == DBType.GBASE8CByOracle)
                execSql = "SELECT  * FROM(" + execSql + ")  WHERE ROWNUM =20";
            else
                execSql += " LIMIT 20";

            DataTable dt = DBAccess.RunSQLReturnTable(execSql);

            if (BP.Difference.SystemConfig.AppCenterDBFieldCaseModel == FieldCaseModel.UpperCase)
            {
                dt.Columns["NO"].ColumnName = "No";
                dt.Columns["NAME"].ColumnName = "Name";
                dt.Columns["DEPTNAME"].ColumnName = "DeptName";
            }

            if (BP.Difference.SystemConfig.AppCenterDBFieldCaseModel == FieldCaseModel.Lowercase)
            {
                dt.Columns["no"].ColumnName = "No";
                dt.Columns["name"].ColumnName = "Name";
                dt.Columns["deptname"].ColumnName = "DeptName";
            }
            return Return_Info(200, "", BP.Tools.Json.ToJson(dt));
        }
    }
}
