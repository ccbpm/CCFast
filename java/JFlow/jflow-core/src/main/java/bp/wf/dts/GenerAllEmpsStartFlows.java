package bp.wf.dts;

import bp.da.DataTable;
import bp.port.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;

/** 
 Method 的摘要说明
*/
public class GenerAllEmpsStartFlows extends Method
{
	/** 
	 不带有参数的方法
	*/
	public GenerAllEmpsStartFlows()
	{
		this.Title = "为每个人重置发起流程列表";
		this.Help = "一个操作员能发起那些流程是自动计算出来的，计算的成本有些高.";
		this.Help += "为了解决该问题，执行该方法，可以提高效率.";
		this.GroupName = "流程自动执行定时任务";
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
	public Object Do() throws Exception {

		Emps ens = new Emps();
		ens.RetrieveAll(99999);

		for (Emp en : ens.ToJavaList())
		{
			Dev2Interface.Port_Login(en.getNo());
			DataTable dt = Dev2Interface.DB_Start(en.getNo());
		}

		return "调度完成..";
	}

}
