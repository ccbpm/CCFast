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

namespace BP.HttpHandler.CCFast
{
    /// <summary>
    /// 页面功能实体
    /// </summary>
    public class CCFast_Portal : BP.WF.HttpHandler.DirectoryPageBase
    {
        /// <summary>
        /// 构造函数
        /// </summary>
        public CCFast_Portal()
        {
        }

        #region  单个流程 .
        /// <summary>
        /// 单个流程
        /// 获得WorkID,Title 链接到 MyView.vue的页面上.
        /// </summary>
        /// <returns></returns>
        public string OneFlow_Init()
        {
            DataTable dt = BP.WF.Dev2Interface.DB_GenerRuning();
            return BP.Tools.Json.ToJson(dt);
        }
        #endregion 界面方法.

    }
}
