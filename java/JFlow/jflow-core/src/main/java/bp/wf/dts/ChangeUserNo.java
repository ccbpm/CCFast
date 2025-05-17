package bp.wf.dts;

import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;
import java.util.*;

/** 
 修改人员编号 的摘要说明
*/
public class ChangeUserNo extends Method
{
	/** 
	 不带有参数的方法
	*/
	public ChangeUserNo()
	{
		this.Title = "修改人员编号（原来一个操作中编号叫A,现在修改成B）";
		this.Help = "请慎重执行，执行前请先备份数据库，系统会把生成的SQL放在日志里，打开日志文件(" + bp.difference.SystemConfig.getPathOfDataUser() + "\\Log)，然后找到这些sql.";
		this.GroupName = "系统维护";

	}
	/** 
	 设置执行变量
	 
	 @return 
	*/
	@Override
	public void Init()
	{
		this.Warning = "您确定要执行吗？";
		getHisAttrs().AddTBString("P1", null, "原用户名", true, false, 0, 10, 10);
		getHisAttrs().AddTBString("P2", null, "新用户名", true, false, 0, 10, 10);
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
	public Object Do() throws Exception {
		String oldNo = this.GetValStrByKey("P1");
		String newNo = this.GetValStrByKey("P2");

		String sqls = "";

		sqls += "UPDATE Port_Emp Set No='" + newNo + "' WHERE No='" + oldNo + "'";
		sqls += "\t\n UPDATE Port_DeptEmpStation Set FK_Emp='" + newNo + "' WHERE FK_Emp='" + oldNo + "'";

		MapDatas mds = new MapDatas();
		mds.RetrieveAll();

		for (MapData md : mds.ToJavaList())
		{
			MapAttrs mattrs = new MapAttrs(md.getNo());
			for (MapAttr attr : mattrs.ToJavaList())
			{
				if (attr.getUIIsEnable() == false && Objects.equals(attr.getDefValReal(), "@WebUser.No"))
				{
					sqls += "\t\n UPDATE " + md.getPTable() + " SET ";
				}
				continue;

			}
			sqls += "UPDATE";

		}

		return "执行成功..." + sqls;
	}
}
