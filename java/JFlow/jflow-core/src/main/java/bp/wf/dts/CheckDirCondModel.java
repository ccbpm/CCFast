package bp.wf.dts;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.wf.template.*;
import bp.*;
import bp.wf.*;

/** 
 升级ccflow6 要执行的调度
*/
public class CheckDirCondModel extends Method
{
	/** 
	 不带有参数的方法
	*/
	public CheckDirCondModel()
	{
		this.Title = "检查所有流程方向条件设置是否正确?";
		this.Help = "1.检查DirCondModel,配置的模式是否正确.";
		this.Help += "\t\n 2.检查条件中配置的SQL";
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
		String err = "";

		// 查询出来按照连接线.
		Nodes nds = new Nodes();
		nds.Retrieve(NodeAttr.CondModel, DirCondModel.ByLineCond.getValue(), null);

		String sql = "方向条件未配置----------------------";
		for (Node item : nds.ToJavaList())
		{
			if (item.getHisToNDNum() <= 0)
			{
				continue;
			}

			NodeSimples toNDs = item.getHisToNodeSimples();
			int num = 0;
			for (NodeSimple nd : toNDs.ToJavaList())
			{
				sql = "SELECT * FROM WF_Cond WHERE CondType=2 and FK_Node=" + item.getNodeID() + " AND ToNodeID=" + nd.getNodeID();
				DataTable DT = DBAccess.RunSQLReturnTable(sql);
				if (DT.Rows.size() == 0)
				{
					num++;
				}
			}
			if (num > 1)
			{
				err += "<br>@流程[" + item.getFlowNo() + "," + item.getFlowName() + "],节点[" + item.getNodeID() + "," + item.getName() + "]方向条件设置错误,到达的节点有[" + toNDs.size() + "]个，没有设置连接线条件的有[" + num + "]个";
			}
		}


		return err;
	}
}
