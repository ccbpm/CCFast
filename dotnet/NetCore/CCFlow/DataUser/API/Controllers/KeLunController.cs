using BP.DA;
using Microsoft.AspNetCore.Mvc;
using System.Data;
using System;
using System.Collections;

namespace CCFlow.NetCore.DataUser.API.Controllers
{
    [Route("WF/[controller]/[Action]")]
    [ApiController]
    public class KeLunController : ControllerBase
    {
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
        /// 低代码初始化菜单
        /// </summary>
        /// <returns></returns>
        [HttpGet, HttpPost]
        public object InitGPMForKeLunVue()
        {
            try
            {
                string sqlGPM = "select  *    FROM GPM_Menu WHERE ( 1 = 1 ) and GPM_Menu.MenuModel='Dict' ";
                DataTable dataTable = DBAccess.RunSQLReturnTable(sqlGPM);
                foreach (DataRow row in dataTable.Rows)
                {
                    string no = row["No"].ToString();
                    string urlExt = row["urlExt"].ToString();
                    string FrmID = row["FrmID"].ToString();
                    if (!DataType.IsNullOrEmpty(FrmID))
                        continue;
                    if (DataType.IsNullOrEmpty(urlExt))
                    {
                        continue;
                    }
                    string sqlUrlPath = "UPDATE GPM_Menu set urlpath='/src/CCFast/CCBill/SearchDict.vue'  where no='" + no + "';";
                    string sqlFrmID = "UPDATE GPM_Menu set FrmID='" + urlExt + "'  where no='" + no + "';";
                    string formattedString = string.Format("{0}?displayMode=table&FrmID={0}", urlExt);
                    string sqlUrlExt = "UPDATE GPM_Menu set UrlExt='" + formattedString + "'  where no='" + no + "';";
                    string sqlIsEnable = "UPDATE GPM_Menu set IsEnable=1 where no='" + no + "';";
                    DBAccess.RunSQL(sqlUrlPath + sqlFrmID + sqlUrlExt + sqlIsEnable);
                }
            }
            catch (Exception ex)
            {
                return Return_Info(500, ex.Message, "");
            }
            return Return_Info(500, "文件不存在", "");
        }

    }
}
