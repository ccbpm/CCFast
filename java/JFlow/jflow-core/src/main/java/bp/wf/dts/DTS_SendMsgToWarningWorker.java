package bp.wf.dts;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;

/** 
 向预期的工作人员发送提醒消息 的摘要说明
*/
public class DTS_SendMsgToWarningWorker extends Method
{
	/** 
	 向预期的工作人员发送提醒消息
	*/
	public DTS_SendMsgToWarningWorker()
	{
		this.Title = "向预期的工作人员发送提醒消息";
		this.Help = "该方法每天的8点自动执行";
	}
	/** 
	 设置执行变量
	 
	 @return 
	*/
	@Override
	public void Init()
	{
		//this.Warning = "您确定要执行吗？";
		//HisAttrs.AddTBString("P1", null, "原密码", true, false, 0, 10, 10);
		//HisAttrs.AddTBString("P2", null, "新密码", true, false, 0, 10, 10);
		//HisAttrs.AddTBString("P3", null, "确认", true, false, 0, 10, 10);
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
		return false;
	}
	/** 
	 执行
	 
	 @return 返回执行结果
	*/
	@Override
	public Object Do() throws Exception {

		/*查找一天预警1次的消息记录，并执行推送。*/
		String sql = "SELECT A.WorkID, A.Title, A.FlowName, A.TodoSta, B.FK_Emp, b.EmpName, C.WAlertWay  FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B, WF_Node C  ";
		sql += " WHERE A.WorkID=B.WorkID AND A.FK_Node=C.NodeID AND a.TodoSta=1 AND ( C.WAlertRole=1 OR C.WAlertRole=2 ) ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		for (DataRow dr : dt.Rows)
		{
			CHAlertWay way = CHAlertWay.forValue(Integer.parseInt(dr.getValue("WAlertWay").toString())); //提醒方式.
			long workid = Long.parseLong(dr.getValue("WorkID").toString());
			String title = dr.getValue("Title").toString();
			String flowName = dr.getValue("FlowName").toString();
			String empNo = dr.getValue("FK_Emp").toString();
			String empName = dr.getValue("EmpName").toString();

			bp.wf.port.WFEmp emp = new bp.wf.port.WFEmp(empNo);

			if (way == CHAlertWay.ByEmail)
			{
				String titleMail = "";
				String docMail = "";
				//  BP.WF.Dev2Interface.Port_SendEmail(emp.getEmail(), titleMail, "");
			}

			if (way == CHAlertWay.BySMS)
			{
				String titleMail = "";
				String docMail = "";
				//BP.WF.Dev2Interface.Port_SendMsg(emp.getEmail(), titleMail, "");
			}
		}
		return "执行成功...";
	}
}
