using System;
using System.Collections.Generic;
using System.Text;

namespace BP.Web
{
    public class NetCoreAppHelper
    {
        /// <summary>
        /// 获取Web应用程序的ServiceProvider。主要用于灵活获取依赖注入的类对象。
        /// </summary>
        public static IServiceProvider ServiceProvider { get; set; }

    }
}
