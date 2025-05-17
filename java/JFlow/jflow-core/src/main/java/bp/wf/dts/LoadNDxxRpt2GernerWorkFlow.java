package bp.wf.dts;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;

/** 
 装载已经完成的流程数据到WF_GenerWorkflow
*/
public class LoadNDxxRpt2GernerWorkFlow extends Method
{
	/** 
	 装载已经完成的流程数据到WF_GenerWorkflow
	*/
	public LoadNDxxRpt2GernerWorkFlow()
	{
		this.Title = "装载已经完成的流程数据到WF_GenerWorkflow（升级扩展流程数据完成模式下的旧数据查询不到的问题）";
		this.Help = "升级扩展流程数据完成模式下的旧数据查询不到的问题。";
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
	public Object Do() throws Exception {
		Flows ens = new Flows();
		for (Flow en : ens.ToJavaList())
		{
			String sql = "SELECT * FROM " + en.getPTable() + " WHERE OID NOT IN (SELECT WorkID FROM WF_GenerWorkFlow WHERE FK_Flow='" + en.getNo() + "')";
			DataTable dt = DBAccess.RunSQLReturnTable(sql);

			for (DataRow dr : dt.Rows)
			{
				GenerWorkFlow gwf = new GenerWorkFlow();
				gwf.setWorkID(Long.parseLong(dr.getValue(GERptAttr.OID).toString()));
				gwf.setFID(Long.parseLong(dr.getValue(GERptAttr.FID).toString()));

				gwf.setFlowSortNo(en.getFlowSortNo());
				gwf.setSysType(en.getSysType());

				gwf.setFlowNo(en.getNo());
				gwf.setFlowName(en.getName());
				gwf.setTitle(dr.getValue(GERptAttr.Title).toString());
				gwf.setWFState(WFState.forValue(Integer.parseInt(dr.getValue(GERptAttr.WFState).toString())));
				//    gwf.WFSta = WFSta.Complete;

				gwf.setStarter(dr.getValue(GERptAttr.FlowStarter).toString());
				gwf.setStarterName(dr.getValue(GERptAttr.FlowStarter).toString());
				gwf.setRDT(dr.getValue(GERptAttr.FlowStartRDT).toString());
				gwf.setNodeID(Integer.parseInt(dr.getValue(GERptAttr.FlowEndNode).toString()));
				gwf.setDeptNo(dr.getValue(GERptAttr.FK_Dept).toString());

				bp.port.Dept dept = null;
				try
				{
					dept = new bp.port.Dept(gwf.getDeptNo());
					gwf.setDeptName(dept.getName());
				}
				catch (java.lang.Exception e)
				{
					gwf.setDeptName(gwf.getDeptName());
				}

				try
				{
					gwf.setPRI(Integer.parseInt(dr.getValue(GERptAttr.PRI).toString()));
				}
				catch (java.lang.Exception e2)
				{

				}

				//  gwf.SDTOfNode = dr[NDXRptBaseAttr.FK_Dept).toString();
				// gwf.SDTOfFlow = dr[NDXRptBaseAttr.FK_Dept).toString();

				gwf.setPFlowNo(dr.getValue(GERptAttr.PFlowNo).toString());
				gwf.setPWorkID(Long.parseLong(dr.getValue(GERptAttr.PWorkID).toString()));
				gwf.setPNodeID(Integer.parseInt(dr.getValue(GERptAttr.PNodeID).toString()));
				gwf.setPEmp(dr.getValue(GERptAttr.PEmp).toString());

				//gwf.CFlowNo = dr[NDXRptBaseAttr.CFlowNo).toString();
				//gwf.CWorkID = Int64.Parse(dr[NDXRptBaseAttr.CWorkID).toString());

				gwf.setGuestNo(dr.getValue(GERptAttr.GuestNo).toString());
				gwf.setGuestName(dr.getValue(GERptAttr.GuestName).toString());
				gwf.setBillNo(dr.getValue(GERptAttr.BillNo).toString());
				//gwf.FlowNote = dr[NDXRptBaseAttr.flowno).toString();

				gwf.SetValByKey("Emps", dr.getValue(GERptAttr.FlowEmps).toString());
				//   gwf.AtPara = dr[NDXRptBaseAttr.FK_Dept).toString();
				//  gwf.GUID = dr[NDXRptBaseAttr.gu).toString();
				gwf.Insert();
			}

		}
		return "执行成功...";
	}
}
