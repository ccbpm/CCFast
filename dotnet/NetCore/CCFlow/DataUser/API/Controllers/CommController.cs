using BP.Difference;
using BP.WF.HttpHandler;
using System;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Routing;


namespace CCFlow.NetCore.DataUser.API.Controllers
{
    [Route("WF/[controller]/[Action]")]
    [ApiController]
    [ApiExplorerSettings(IgnoreApi = true)]
    public class CommController : ControllerBase
    {
        [HttpGet, HttpPost]
        public string ProcessRequest()
        {
            DirectoryPageBase ctrl = Activator.CreateInstance(CtrlType) as DirectoryPageBase;
            try
            {

                //执行方法返回json.
                string data = ctrl.DoMethod(ctrl, ctrl.DoType);
                return data;

                //返回执行的结果.
                //HttpContextHelper.Current.Response.Write(data);
            }
            catch (Exception ex)
            {
                string paras = "";
                foreach (string key in HttpContextHelper.RequestQueryStringKeys)
                {
                    paras += "@" + key + "=" + HttpContextHelper.RequestQueryString(key);
                }

                string err = "";
                //返回执行错误的结果.
                if (ex.InnerException != null)
                    err = "err@在执行类[" + CtrlType.ToString() + "]，方法[" + ctrl.DoType + "]错误 \t\n @" + ex.InnerException.Message + " \t\n @技术信息:" + ex.StackTrace + " \t\n相关参数:" + paras;
                else
                    err = "err@在执行类[" + CtrlType.ToString() + "]，方法[" + ctrl.DoType + "]错误 \t\n @" + ex.Message + " \t\n @技术信息:" + ex.StackTrace + " \t\n相关参数:" + paras;

                if (BP.Web.WebUser.No == null)
                    err = "err@登录时间过长,请重新登录. @其他信息:" + err;

                //记录错误日志以方便分析.
                BP.DA.Log.DebugWriteError(err);
                return err;
                //HttpContextHelper.Response.Write(err);
            }
        }
        public Type CtrlType
        {
            get
            {
                return typeof(WF_Comm);
            }
        }
    }

}
