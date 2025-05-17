package bp.wf.dts;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;

/** 
 修复表单物理表字段长度 的摘要说明
*/
public class DTSAutoRpt extends Method
{
	/** 
	 不带有参数的方法
	*/
	public DTSAutoRpt()
	{
		this.Title = "自动报表";
		this.Help = "放在定时任务里，读取WF_AutoRpt数据配置，想指定的人员推送数据。";
	}
	/** 
	 设置执行变量
	 
	 @return 
	*/
	@Override
	public void Init()
	{
	}
	/** 
	 当前的操纵员是否可以执行这个方法
	*/
	@Override
	public boolean getIsCanDo()
	{
		return true;
	}
	/** 
	 执行
	 
	 @return 返回执行结果
	*/
	@Override
	public Object Do()
	{
		//BP.WF.Template.AutoRpts rpts = new AutoRpts();
		//rpts.RetrieveAll();

		//String html = "执行内容如下:";
		//foreach (BP.WF.Template.AutoRpt rpt in rpts)
		//{
		//    if (DataType.IsNullOrEmpty(rpt.StartDT) == false)
		//    {
		//        html += "<br>" + rpt.No + rpt.getName() +"没有启用.";
		//        continue;
		//    }

		//    #region 判断是否可以启动?
		//    //要发起的时间点.
		//    string[] strs = rpt.StartDT.Split('@');
		//    String datetime = DateTime.Now.ToString("HH:mm");
		//    foreach (String str in strs)
		//    {
		//        if (DataType.IsNullOrEmpty(str)==true)
		//            continue;


		//    }
		//    #endregion 判断是否可以启动?

		//    #region 获得可以发送的人员集合.
		//    Hashtable htEmps = new Hashtable();

		//    foreach (var item in rpt.ToEmps.Split(','))
		//    {
		//    }
		//    #endregion 获得可以发送的人员集合.


		//}

		return null;
		//return html;
	}
}
