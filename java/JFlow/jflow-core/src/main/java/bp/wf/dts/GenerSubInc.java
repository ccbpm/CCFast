package bp.wf.dts;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;

/** 
 升级ccflow6 要执行的调度
*/
public class GenerSubInc extends Method
{
	/** 
	 不带有参数的方法
	*/
	public GenerSubInc()
	{
		this.Title = "为子公司生成表单树，流程树目录.";
		this.Help = "实施初始化.";
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
	public Object Do() throws Exception {
		//找到根目录.
		String sql = "SELECT No FROM WF_FlowSort where ParentNo='0'";
		String rootNo = DBAccess.RunSQLReturnString(sql, null);
		if (rootNo == null)
		{
			return "没有找到根目录节点" + sql;
		}

		//求出分公司集合(组织结构集合)
		sql = "SELECT No,Name FROM Port_Body where dtype!=3";
		DataTable dtInc = DBAccess.RunSQLReturnTable(sql);


		//把该模版发布到子公司里面去.
		for (DataRow dr : dtInc.Rows)
		{
			String incNo = dr.getValue("No").toString();
			String incName = dr.getValue("Name").toString();

			//检查该公司是否创建了树节点, 如果没有就插入一个.
			bp.wf.template.FlowSort fs = new bp.wf.template.FlowSort();
			fs.setNo(incNo);
			if (fs.RetrieveFromDBSources() == 0)
			{
				fs.setName(incName);
				fs.setOrgNo(incNo);
				fs.setParentNo(rootNo);
				fs.setOrgNo(incNo);
				fs.Insert();
			}
		}

		return "执行成功";
	}
}
