using Microsoft.AspNetCore.Builder;
using System;

namespace CCFlow
{
    public static class CcHandlerAppBuilderExtensions
    {
        public static IApplicationBuilder UseCcHandler(this IApplicationBuilder app)
        {
            if (app == null)
            {
                throw new ArgumentNullException(nameof(app));
            }

            return app.UseMiddleware<CcHandlerMiddleware>();
        }
    }
}
