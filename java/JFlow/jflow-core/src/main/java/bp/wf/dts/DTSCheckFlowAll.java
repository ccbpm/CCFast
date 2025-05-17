package bp.wf.dts;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;

/** 
 修复表单物理表字段长度 的摘要说明
*/
public class DTSCheckFlowAll extends Method
{
	/** 
	 不带有参数的方法
	*/
	public DTSCheckFlowAll()
	{
		this.Title = "体检全部流程";
		this.Help = "只能功能与单独体检流程相同，体检流程不会伤害数据。";
		this.Help += "<br>1，修复节点表单、流程报表物理表。";
		this.Help += "<br>2，生成预先流程与节点计算数据，从而优化流程执行速度。";
		this.Help += "<br>3，修复流程报表数据。";
		this.Help += "<br>4，系统不会提示体检结果。";
		this.Help += "<br>5，体检的时间长度与流程数量，节点数量，表单字段多少有关系，请耐心等待。";
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
		Flows fls = new Flows();
		fls.RetrieveAllFromDBSource();
		for (Flow fl : fls.ToJavaList())
		{
			fl.DoCheck();
		}

		return "提示：" + fls.size() + "个流程参与了体检。";
	}
}
