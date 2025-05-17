package bp.wf.dts;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;

/** 
 修改人员编号 的摘要说明
*/
public class GenerPinYinForEmp extends Method
{
	/** 
	 不带有参数的方法
	*/
	public GenerPinYinForEmp()
	{
		this.Title = "为人员生成拼音，放入到 Port_Emp.PinYin 字段里.";
		this.Help = "为了检索方便，为所有的人员生成拼音, 方便在会签，移交，接受人查询。";
	}
	/** 
	 设置执行变量
	 
	 @return 
	*/
	@Override
	public void Init()
	{
		//this.Warning = "您确定要执行吗？";
		//HisAttrs.AddTBString("P1", null, "原用户名", true, false, 0, 10, 10);
		//HisAttrs.AddTBString("P2", null, "新用户名", true, false, 0, 10, 10);
	}
	/** 
	 当前的操纵员是否可以执行这个方法
	*/
	@Override
	public boolean getIsCanDo()
	{
		if (bp.web.WebUser.getIsAdmin() == true)
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
	public Object Do() throws Exception {
		if (DBAccess.IsView("Port_Emp", bp.difference.SystemConfig.getAppCenterDBType()) == true)
		{
			return "Port_Emp 是一个视图无法生成拼音.";
		}

		if (DBAccess.IsExitsTableCol("Port_Emp", bp.port.EmpAttr.PinYin) == false)
		{
			return "Port_Emp 不包含PinYin 这一列,无法生成拼音.";
		}

		bp.port.Emps emps = new bp.port.Emps();
		emps.RetrieveAll();
		for (bp.port.Emp item : emps.ToJavaList())
		{
			if (item.getPinYin().contains("/") == true)
			{
				continue;
			}
			item.Update();
		}
		return "执行成功...";
	}
}
