using Quartz;
using System;
using System.Net.Http;
using System.Threading.Tasks;

namespace CCFlow.NetCore.DataUser.TaskScheduler;

[DisallowConcurrentExecution]
public class AutoStartFlow: IJob
{
    static readonly HttpClient client = new HttpClient()
    {
        BaseAddress = new Uri("http://localhost:56146")
    };
    const string taskUrl = "/WF/Comm/Handler.ashx?DoType=HttpHandler&DoMethod=ccbpmServices&HttpHandlerName=BP.WF.HttpHandler.WF_WorkOpt";

    public async Task Execute(IJobExecutionContext context)
    {
        try
        {
            string msg = await client.GetStringAsync(taskUrl);
            BP.DA.Log.DebugWriteInfo(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss") + " - 自动发起成功 msg: " + msg);
        }
        catch (Exception e)
        {
            BP.DA.Log.DebugWriteError(DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss") + " - 自动发起错误：" + e.Message);
        }
    }
}