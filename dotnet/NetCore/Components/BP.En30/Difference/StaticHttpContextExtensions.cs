using BP.Difference;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Collections.Generic;
using System.Text;

namespace BP.Web
{
    /// <summary>
    /// 静态HttpContext扩展方法。为HttpContextConfigHelper服务。
    /// </summary>
    public static class StaticHttpContextExtensions
    {

        // Microsoft.Extensions.DependencyInjection.HttpServiceCollectionExtensions.AddHttpContextAccessor(Microsoft.Extensions.DependencyInjection.IServiceCollection)

        /// <summary>
        /// 在Startup.cs中从Configure中调用。
        /// 需要先在ConfigureServices() 方法中调用AddHttpContextAccessor()方法
        /// </summary>
        /// <param name="app"></param>
        /// <returns></returns>
        public static IApplicationBuilder UseStaticHttpContext(this IApplicationBuilder app)
        {
            var httpContextAccessor = app.ApplicationServices.GetRequiredService<IHttpContextAccessor>();
            HttpContextHelper.Configure(httpContextAccessor);
            return app;
        }
    }
}
