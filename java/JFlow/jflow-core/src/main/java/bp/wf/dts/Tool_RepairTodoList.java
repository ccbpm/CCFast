package bp.wf.dts;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;
import java.util.*;

/** 
 升级ccflow6 要执行的调度
*/
public class Tool_RepairTodoList extends Method
{
	/** 
	 不带有参数的方法
	*/
	public Tool_RepairTodoList()
	{
		this.Title = "修改撤销不存在待办的问题.";
		this.Help = "如果仍然出现，请反馈给开发人员，属于系统错误..";
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
		String sql = "SELECT WORKID FROM WF_GenerWorkFlow A WHERE WFSta <>1 AND WFState =2  AND WorkID Not IN (Select WorkID From WF_GenerWorkerlist) ORDER BY RDT desc";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		String msg = "";
		for (DataRow dr : dt.Rows)
		{
			long workid = Long.parseLong(dr.getValue("WorkID").toString());
			GenerWorkFlow gwf = new GenerWorkFlow(workid);
			String todoEmps = gwf.getTodoEmps();
			// 如果不存在待办，则增加待办
			sql = "SELECT *  From WF_GenerWorkerlist WHERE WORKID=" + workid + " AND instr('" + todoEmps + "',FK_Emp)>0  AND IsPass=0";
			if (DBAccess.RunSQLReturnCOUNT(sql) > 0)
			{
				continue;
			}
			if (DataType.IsNullOrEmpty(todoEmps) == true)
			{
				continue;
			}
			String[] strs = todoEmps.split("[;]", -1);
			for (String str : strs)
			{
				if (DataType.IsNullOrEmpty(str))
				{
					continue;
				}

				GenerWorkerList gwl = new GenerWorkerList();
				gwl.setWorkID(workid);
				String[] empStrs = str.split("[,]", -1);

				gwl.setEmpNo(empStrs[0]);
				gwl.setNodeID(gwf.getNodeID());
				if (empStrs.length == 2)
				{
					gwl.setEmpName(empStrs[1]);
				}
				gwl.setFlowNo(gwf.getFlowNo());
				gwl.setRDT(gwf.getSDTOfNode());
				gwl.setCDT(gwf.getSDTOfNode());
				gwl.setItIsEnable(true);
				gwl.setItIsRead(false);
				gwl.setItIsPass(false);
				gwl.setWhoExeIt(0);
				gwl.Save();
			}

		}
		return "执行成功.";
	}

	public final String ss() throws Exception {
		String sql = "SELECT AtPara,WorkID FROM WF_GENERWORKFLOW WHERE WFState !=3 AND atpara like '%@HuiQianTaskSta=1%' ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		String msg = "";
		for (DataRow dr : dt.Rows)
		{
			String at = dr.getValue("AtPara").toString();
			long workid = Long.parseLong(dr.getValue("WorkID").toString());

			GenerWorkFlow gwf = new GenerWorkFlow(workid);
			GenerWorkerLists gwls = new GenerWorkerLists(workid);
			gwls.Retrieve(GenerWorkerListAttr.WorkID, workid, GenerWorkerListAttr.FK_Node, gwf.getNodeID(), null);
			if (gwls.size() == 1)
			{
				if (gwf.getHuiQianTaskSta() == HuiQianTaskSta.HuiQianing)
				{
					gwf.setHuiQianTaskSta(HuiQianTaskSta.None);
					gwf.Update();
				}
			}



			AtPara ap = new AtPara(at);

			String para = "";
			for (String item : ap.getHisHT().keySet())
			{
				if (item.indexOf("CH") == 0)
				{
					continue;
				}

				para += "@" + item + "=" + ap.GetValStrByKey(item);
			}

			if (Objects.equals(para, ""))
			{
				continue;
			}

			DBAccess.RunSQL("UPDATE WF_GENERWORKFLOW SET AtPara='" + para + "' where workID=" + workid + " ");
		}

		return "执行成功.";

	}
}
