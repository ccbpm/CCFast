package bp.wf.dts;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;

/** 
 升级ccflow6 要执行的调度
*/
public class UpdateEmpDeptNo extends Method
{
	/** 
	 不带有参数的方法
	*/
	public UpdateEmpDeptNo()
	{
		this.Title = "从Port_DeptEmp表里,随机抽出来一个部门编号给Port_Emp的FK_Dept。";
		this.Help = "宁波项目:人员表里没有主部门.";
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
		if (bp.web.WebUser.getNo().equals("admin") == true)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/** 
	 执行
	 
	 @return 返回执行结果
	*/
	@Override
	public Object Do()
	{
		String sql = "";

		return "执行完成.";
	}
}
