package bp.wf.dts;

import bp.da.DataRow;
import bp.da.DataTable;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;

/** 
 重新生成关键字
*/
public class GenerSKeyWords extends Method
{
	/** 
	 重新生成关键字
	*/
	public GenerSKeyWords()
	{
		this.Title = "重新生成关键字SKeyWords（为所有的流程，根据新的规则生成流程关键字 SKeyWords）";
		this.Help = "您也可以打开流程属性一个个的单独执行。";
		this.GroupName = "流程维护";
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
		return false;
	}
	/** 
	 执行
	 
	 @return 返回执行结果
	*/
	@Override
	public Object Do()
	{
		String sql = "SELECT WorkID,FK_Flow FROM WF_GenerWorkFlow WHERE SKeyWords is null or SKeyWords='' ";
		DataTable dt = bp.da.DBAccess.RunSQLReturnTable(sql);

		for (DataRow item : dt.Rows)
		{
			int workid = Integer.parseInt(item.get(0).toString());
			String flowNo = item.get(1).toString();
			try
			{
				GERpt rpt = new GERpt("ND" + Integer.parseInt(flowNo) + "Rpt", workid);

				GenerWorkFlow gwf = new GenerWorkFlow(workid);
				WorkFlow wf = new WorkFlow(workid);
				wf.GenerSKeyWords(gwf, rpt); //生成关键字.
			}
			catch (RuntimeException ex)
			{
				continue;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return "执行成功...";
	}
}
