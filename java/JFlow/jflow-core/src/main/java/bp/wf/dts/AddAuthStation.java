package bp.wf.dts;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.wf.template.*;
import bp.*;
import bp.wf.*;

/** 
 Method 的摘要说明
*/
public class AddAuthStation extends Method
{
	/** 
	 不带有参数的方法
	*/
	public AddAuthStation()
	{
		this.Title = "增加授权角色";
		this.Help = "1. 解决一个流程执行完成后，那些授权角色参与了该流程.";
		this.Help += "\t\n 2. 在WF_GenerWorkFlow 的Emps的字段上增加  @部门编号+下划线+角色编号; ";
		this.Help += "\t\n 3. 解决中科软的人员离职后的工作交接后，按照授权角色查询已经办理过的流量问题.";

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
		GenerWorkFlows ens = new GenerWorkFlows();
		ens.Retrieve("", "", null);

		//查询出来，没有做过同步的 并且流程已经完成的 流程实例.
		String sql = "SELECT WorkID FROM WF_GenerWorkFlow WHERE WFState=3 AND  Emps  NOT LIKE '%@Over' ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		//遍历这些实例.
		for (DataRow dr : dt.Rows)
		{
			long workid = Long.parseLong(dr.getValue(0).toString());

			GenerWorkFlow gwf = new GenerWorkFlow(workid);

			//查询出来当前流程的Track.
			sql = "SELECT * FROM ND" + Integer.parseInt(gwf.getFlowNo()) + "Track WHERE WorkID=" + workid + " ORDER BY RDT ";
			DataTable tarck = DBAccess.RunSQLReturnTable(sql);

			//查询出来节点.
			Nodes nds = new Nodes(gwf.getFlowNo());

			//遍历节点.
			for (Node nd : nds.ToJavaList())
			{
				if (this.ItIsHaveStation(nd) == false)
				{
					continue;
				}

				//求节点与角色的集合.
				NodeStations ndstations = new NodeStations(nd.getNodeID());
				if (ndstations.size() == 0)
				{
					continue;
				}

				//找到处理当前工作的人员集合.
				sql = "SELECT EmpFrom FROM ND" + Integer.parseInt(gwf.getFlowNo()) + "Track WHERE WorkID=" + workid + " AND NDFrom=" + nd.getNodeID() + " ORDER BY RDT ";
				DataTable dtTarck = DBAccess.RunSQLReturnTable(sql);

				for (DataRow drTrack : dtTarck.Rows)
				{
					String empNo = drTrack.get(0).toString();

					//获得人员的集合，与节点绑定的集合.
					sql = "SELECT A.FK_Dept, A.FK_Station FROM Port_DeptEmpStation A, WF_NodeStation B WHERE  ";
					sql += " WHERE A.FK_Station=B.FK_Station ";
					sql += " AND A.FK_Emp= '" + empNo + "'";
					sql += " AND B.FK_Node= " + nd.getNodeID();
					DataTable dtDeptStatio = DBAccess.RunSQLReturnTable(sql);

					for (DataRow mydtDeptStatio : dtDeptStatio.Rows)
					{
						String deptNo = mydtDeptStatio.get("FK_Dept").toString();
						String stationNo = mydtDeptStatio.get("FK_Station").toString();

						String str = "@" + deptNo + "_" + stationNo + ";";
						if (gwf.getTodoEmps().contains(str) == false)
						{
							gwf.setTodoEmps(gwf.getTodoEmps() + str);
						}
					}
				}
			}

			//设置同步标记执行更新.
			gwf.setTodoEmps(gwf.getTodoEmps() + "@Over");
			gwf.Update();

		}
		return "调度完成..";
	}

	public final boolean ItIsHaveStation(Node nd) throws Exception {
		if (nd.getHisDeliveryWay() == DeliveryWay.ByDeptAndStation)
		{
			return true;
		}
		if (nd.getHisDeliveryWay() == DeliveryWay.ByStation)
		{
			return true;
		}

		return false;
	}

}
