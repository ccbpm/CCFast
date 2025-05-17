package bp.wf.dts;

import bp.port.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;

/** 
 Method 的摘要说明
*/
public class OrgInit_NameOfPath extends Method
{
	/** 
	 组织结构处理部门全路径
	*/
	public OrgInit_NameOfPath()
	{
		this.Title = "组织结构-部门全路径NameOfPath";
		this.Help = "循环所有部门，重新生成NameOfPath";
		this.GroupName = "组织结构";

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
		try
		{
			Depts depts = new Depts();
			depts.RetrieveAll();
			for (Dept dept : depts.ToJavaList())
			{
				dept.GenerNameOfPath();
			}

			return "执行成功...";
		}
		catch (RuntimeException ex)
		{
			return "执行失败：" + ex.getMessage();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
