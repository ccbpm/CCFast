using System;
using System.Data;
using System.Text;
using BP.DA;
using BP.Sys;
using BP.Web;
using BP.Difference;
using BP.En;
using NPOI.SS.Formula.Functions;
using System.Collections.Generic;
using ICSharpCode.SharpZipLib.BZip2;
using ICSharpCode.SharpZipLib.Zip;
using System.IO;

namespace BP.App
{
    /// <summary>
    /// 页面功能实体
    /// </summary>
    public class Handler_Demo : BP.WF.HttpHandler.DirectoryPageBase
    {
        /// <summary>
        /// 构造函数
        /// </summary>
        public Handler_Demo()
        {
        }

        /// <summary>
        /// 注销学籍
        /// </summary>
        /// <returns></returns>
        public string Student_ZhuXiaoXueJi()
        {
            string name = this.GetRequestVal("Name");
            string stuNo = this.GetRequestVal("No");
            return "注销成功:"+stuNo+" Name:"+name;
        }
        /// <summary>
        /// 缴纳班费
        /// </summary>
        /// <returns></returns>
        public string Student_JiaoNaXueFei()
        {
            string name = this.GetRequestVal("Name");
            string stuNo = this.GetRequestVal("No");
            return "缴纳成功:" + stuNo + " Name:" + name;
        }
    }


}
