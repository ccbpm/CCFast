using BP.En;
namespace BP.WF.DTS
{
    /// <summary>
    /// 创建索引
    /// </summary>
    public class ReadEmailSendWork : Method
    {
        /// <summary>
        /// 创建索引
        /// </summary>
        public ReadEmailSendWork()
        {
            this.Title = "读取邮件发送流程";
            this.Help = "未完成...";
        }
        /// <summary>
        /// 设置执行变量
        /// </summary>
        /// <returns></returns>
        public override void Init()
        {
        }
        /// <summary>
        /// 当前的操纵员是否可以执行这个方法
        /// </summary>
        public override bool IsCanDo
        {
            get
            {
                if (BP.Web.WebUser.No.Equals("admin")==true)
                    return true;
                return false;
            }
        }
        /// <summary>
        /// 执行
        /// </summary>
        /// <returns>返回执行结果</returns>
        public override object Do()
        {
            string info = "开始为Track表创建索引.";
            //Flows fls = new Flows();
            //foreach (Flow fl in fls)
            //{
            //    info += fl.CreateIndex();
            //}
            return info;
        }
    }
}
