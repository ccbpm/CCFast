package bp.wf.data;

import bp.wf.*;
import bp.port.*;
import bp.en.*;
import bp.en.Map;

/**
 流程监控
*/
public class Monitor extends Entity
{

		///#region 基本属性
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.Readonly();
		return uac;
	}
	/**
	 主键
	*/
	@Override
	public String getPK()
	{
		return MonitorAttr.WorkID;
	}
	/**
	 工作流程编号
	*/
	public final String getFK_Flow()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.FK_Flow);
	}
	public final void setFK_Flow(String value) throws Exception
	{
		SetValByKey(MonitorAttr.FK_Flow, value);
	}
	/**
	 BillNo
	*/
	public final String getBillNo()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.BillNo);
	}
	public final void setBillNo(String value) throws Exception
	{
		SetValByKey(MonitorAttr.BillNo, value);
	}
	/**
	 流程名称
	*/
	public final String getFlowName()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.FlowName);
	}
	public final void setFlowName(String value) throws Exception
	{
		SetValByKey(MonitorAttr.FlowName, value);
	}
	/**
	 优先级
	*/
	public final int getPRI()  throws Exception
	{
		return this.GetValIntByKey(MonitorAttr.PRI);
	}
	public final void setPRI(int value) throws Exception
	{
		SetValByKey(MonitorAttr.PRI, value);
	}
	/**
	 待办人员数量
	*/
	public final int getTodoEmpsNum()  throws Exception
	{
		return this.GetValIntByKey(MonitorAttr.TodoEmpsNum);
	}
	public final void setTodoEmpsNum(int value) throws Exception
	{
		SetValByKey(MonitorAttr.TodoEmpsNum, value);
	}
	/**
	 待办人员列表
	*/
	public final String getTodoEmps()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.TodoEmps);
	}
	public final void setTodoEmps(String value) throws Exception
	{
		SetValByKey(MonitorAttr.TodoEmps, value);
	}
	/**
	 参与人
	*/
	public final String getEmps()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.Emps);
	}
	public final void setEmps(String value) throws Exception
	{
		SetValByKey(MonitorAttr.Emps, value);
	}
	/**
	 状态
	*/
	public final TaskSta getTaskSta()throws Exception
	{
		return TaskSta.forValue(this.GetValIntByKey(MonitorAttr.TaskSta));
	}
	public final void setTaskSta(TaskSta value)throws Exception
	{
		SetValByKey(MonitorAttr.TaskSta, value.getValue());
	}
	/**
	 类别编号
	*/
	public final String getFK_Emp()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.FK_Emp);
	}
	public final void setFK_Emp(String value) throws Exception
	{
		SetValByKey(MonitorAttr.FK_Emp, value);
	}
	/**
	 部门编号
	*/
	public final String getFK_Dept()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.FK_Dept);
	}
	public final void setFK_Dept(String value) throws Exception
	{
		SetValByKey(MonitorAttr.FK_Dept, value);
	}
	/**
	 标题
	*/
	public final String getTitle()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.Title);
	}
	public final void setTitle(String value) throws Exception
	{
		SetValByKey(MonitorAttr.Title, value);
	}
	/**
	 客户编号
	*/
	public final String getGuestNo()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.GuestNo);
	}
	public final void setGuestNo(String value) throws Exception
	{
		SetValByKey(MonitorAttr.GuestNo, value);
	}
	/**
	 客户名称
	*/
	public final String getGuestName()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.GuestName);
	}
	public final void setGuestName(String value) throws Exception
	{
		SetValByKey(MonitorAttr.GuestName, value);
	}
	/**
	 产生时间
	*/
	public final String getRDT()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.RDT);
	}
	public final void setRDT(String value) throws Exception
	{
		SetValByKey(MonitorAttr.RDT, value);
	}
	/**
	 节点应完成时间
	*/
	public final String getSDTOfNode()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.SDTOfNode);
	}
	public final void setSDTOfNode(String value) throws Exception
	{
		SetValByKey(MonitorAttr.SDTOfNode, value);
	}
	/**
	 流程应完成时间
	*/
	public final String getSDTOfFlow()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.SDTOfFlow);
	}
	public final void setSDTOfFlow(String value) throws Exception
	{
		SetValByKey(MonitorAttr.SDTOfFlow, value);
	}
	/**
	 流程ID
	*/
	public final long getWorkID()  throws Exception
	{
		return this.GetValInt64ByKey(MonitorAttr.WorkID);
	}
	public final void setWorkID(long value) throws Exception
	{
		SetValByKey(MonitorAttr.WorkID, value);
	}
	/**
	 主线程ID
	*/
	public final long getFID()  throws Exception
	{
		return this.GetValInt64ByKey(MonitorAttr.FID);
	}
	public final void setFID(long value) throws Exception
	{
		SetValByKey(MonitorAttr.FID, value);
	}
	/**
	 父节点流程编号.
	*/
	public final long getPWorkID()  throws Exception
	{
		return this.GetValInt64ByKey(MonitorAttr.PWorkID);
	}
	public final void setPWorkID(long value) throws Exception
	{
		SetValByKey(MonitorAttr.PWorkID, value);
	}
	/**
	 父流程调用的节点
	*/
	public final int getPNodeID()  throws Exception
	{
		return this.GetValIntByKey(MonitorAttr.PNodeID);
	}
	public final void setPNodeID(int value) throws Exception
	{
		SetValByKey(MonitorAttr.PNodeID, value);
	}
	/**
	 PFlowNo
	*/
	public final String getPFlowNo()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.PFlowNo);
	}
	public final void setPFlowNo(String value) throws Exception
	{
		SetValByKey(MonitorAttr.PFlowNo, value);
	}
	/**
	 吊起子流程的人员
	*/
	public final String getPEmp()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.PEmp);
	}
	public final void setPEmp(String value) throws Exception
	{
		SetValByKey(MonitorAttr.PEmp, value);
	}
	/**
	 发起人
	*/
	public final String getStarter()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.Starter);
	}
	public final void setStarter(String value) throws Exception
	{
		SetValByKey(MonitorAttr.Starter, value);
	}
	/**
	 发起人名称
	*/
	public final String getStarterName()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.StarterName);
	}
	public final void setStarterName(String value){
		this.SetValByKey(MonitorAttr.StarterName, value);
	}
	/**
	 发起人部门名称
	*/
	public final String getDeptName()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.DeptName);
	}
	public final void setDeptName(String value){
		this.SetValByKey(MonitorAttr.DeptName, value);
	}
	/**
	 当前节点名称
	*/
	public final String getNodeName()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.NodeName);
	}
	public final void setNodeName(String value){
		this.SetValByKey(MonitorAttr.NodeName, value);
	}
	/**
	 当前工作到的节点
	*/
	public final int getFK_Node()  throws Exception
	{
		return this.GetValIntByKey(MonitorAttr.FK_Node);
	}
	public final void setNodeID(int value) throws Exception
	{
		SetValByKey(MonitorAttr.FK_Node, value);
	}
	/**
	 工作流程状态
	*/
	public final WFState getWFState()throws Exception
	{
		return WFState.forValue(this.GetValIntByKey(MonitorAttr.WFState));
	}
	public final void setWFState(WFState value) throws Exception {
		if (value == WFState.CompleteEnd  || value == WFState.CompleteByNode)
		{
			SetValByKey(MonitorAttr.WFSta, WFSta.Complete.getValue());
		}
		else if (value == WFState.Delete)
		{
			SetValByKey(MonitorAttr.WFSta, WFSta.Etc.getValue());
		}
		else
		{
			SetValByKey(MonitorAttr.WFSta, WFSta.Runing.getValue());
		}

		SetValByKey(MonitorAttr.WFState, value.getValue());
	}
	/**
	 状态(简单)
	*/
	public final WFSta getWFSta()throws Exception
	{
		return WFSta.forValue(this.GetValIntByKey(MonitorAttr.WFSta));
	}
	public final void setWFSta(WFSta value)throws Exception
	{
		SetValByKey(MonitorAttr.WFSta, value.getValue());
	}
	public final String getWFStateText()throws Exception
	{
		WFState ws = this.getWFState();
		switch (ws)
		{
			case CompleteEnd:
				return "已完成";
			case Runing:
				return "在运行";

			case Askfor:
				return "加签";
			default:
				return "未判断";
		}
	}
	/**
	 GUID
	*/
	public final String getGUID()  throws Exception
	{
		return this.GetValStrByKey(MonitorAttr.GUID);
	}
	public final void setGUID(String value) throws Exception
	{
		SetValByKey(MonitorAttr.GUID, value);
	}

		///#endregion


		///#region 参数属性.

		///#endregion 参数属性.


		///#region 构造函数
	/**
	 工作实例
	*/
	public Monitor()
	{
	}
	public Monitor(long workId) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(MonitorAttr.WorkID, workId);
		if (qo.DoQuery() == 0)
		{
			throw new RuntimeException("工作 WF_GenerWorkFlow [" + workId + "]不存在。");
		}
	}
	/**
	 执行修复
	*/
	public final void DoRepair()throws Exception
	{
	}
	/**
	 重写基类方法
	*/
	@Override
	public bp.en.Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_EmpWorks", "流程监控");
		map.setEnType(EnType.View);

		map.AddTBIntPK(MonitorAttr.WorkID, 0, "工作ID", true, true);
		map.AddTBInt(MonitorAttr.FID, 0, "FID", false, false);
		map.AddTBString(MonitorAttr.Title, null, "流程标题", true, false, 0, 300, 10,true);
		map.AddTBString(MonitorAttr.FK_Emp, null, "当前处理人员", true, false, 0, 100, 10);
		map.AddDDLEntities(MonitorAttr.FK_Flow, null, "流程", new Flows(), false);
		map.AddDDLEntities(MonitorAttr.FK_Dept, null, "发起部门", new Depts(), false);
		map.AddTBString(MonitorAttr.Starter, null, "发起人编号", true, false, 0, 100, 10);
		map.AddTBString(MonitorAttr.StarterName, null, "名称", true, false, 0, 30, 10);
		map.AddTBString(MonitorAttr.NodeName, null, "停留节点", true, false, 0, 100, 10);
		map.AddTBString(MonitorAttr.TodoEmps, null, "处理人", true, false, 0, 100, 10);

		map.AddTBStringDoc(MonitorAttr.FlowNote, null, "备注", true, false,true);

		map.AddTBInt(MonitorAttr.FK_Node, 0, "FK_Node", false, false);

			//map.AddTBString(MonitorAttr.WorkerDept, null, "工作人员部门编号",
			//    false, false, 0, 30, 10);

			//查询条件.
		map.AddSearchAttr(MonitorAttr.FK_Dept, 130);
		map.AddSearchAttr(MonitorAttr.FK_Flow, 130);

			////增加隐藏的查询条件.
			//SearchNormal search = new SearchNormal(MonitorAttr.WorkerDept, "部门",
			//    MonitorAttr.WorkerDept, "=", bp.web.WebUser.getDeptNo(), 0, true);
			//map.AttrsOfSearch.Add(search);

		RefMethod rm = new RefMethod();
		rm.Title = "流程轨迹";
		rm.ClassMethodName = this.toString() + ".DoTrack";
		rm.Icon = "../../WF/Img/FileType/doc.gif";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Icon = "../../WF/Img/Btn/CC.gif";
		rm.Title = "移交";
		rm.ClassMethodName = this.toString() + ".DoShift";
		rm.getHisAttrs().AddDDLEntities("ToEmp", null, "移交给:", new bp.wf.data.MyDeptEmps(),true);
		rm.getHisAttrs().AddTBString("Note", null, "移交原因", true, false, 0, 300, 100);
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Icon = "../../WF/Img/Btn/Delete.gif";
		rm.Title = "删除";
		rm.Warning = "您确定要删除该流程吗？";
		rm.ClassMethodName = this.toString() + ".DoDelete";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Icon = "../../WF/Img/Btn/Back.png";

		rm.Title = "回滚";

		rm.ClassMethodName = this.toString() + ".DoComeBack";
		rm.getHisAttrs().AddTBInt("NodeID", 0, "回滚到节点", true, false);
		rm.getHisAttrs().AddTBString("Note", null, "回滚原因", true, false, 0, 300, 100);
		map.AddRefMethod(rm);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 执行功能.
	public final String DoTrack()throws Exception
	{
		return "../../WFRpt.htm?WorkID=" + this.getWorkID() + "&FID=" + this.getFID() + "&FK_Flow=" + this.getFK_Flow();
	}
	/**
	 执行移交

	 param ToEmp
	 param Note
	 @return
	*/
	public final String DoShift(String ToEmp, String Note) throws Exception {
		if (Dev2Interface.Flow_IsCanViewTruck(this.getFK_Flow(), this.getWorkID()) == false)
		{
			return "您没有操作该流程数据的权限.";
		}

		try
		{
			Dev2Interface.Node_Shift(this.getWorkID(), ToEmp, Note);
			return "移交成功";
		}
		catch (RuntimeException ex)
		{
			return "移交失败@" + ex.getMessage();
		}
	}
	/**
	 执行删除

	 @return
	*/
	public final String DoDelete()throws Exception
	{
		if (Dev2Interface.Flow_IsCanViewTruck(this.getFK_Flow(), this.getWorkID()) == false)
		{
			return "您没有操作该流程数据的权限.";
		}

		try
		{
			Dev2Interface.Flow_DoDeleteFlowByReal(this.getWorkID(), true);
			return "删除成功";
		}
		catch (RuntimeException ex)
		{
			return "删除失败@" + ex.getMessage();
		}
	}

	/**
	 回滚

	 param nodeid 节点ID
	 param note 回滚原因
	 @return 回滚的结果
	*/
	public final String DoComeBack(int nodeid, String note) throws Exception {
		bp.wf.template.FlowSheet fl = new bp.wf.template.FlowSheet(this.getFK_Flow());
		return fl.DoRebackFlowData(this.getWorkID(), nodeid, note);
	}

		///#endregion
}
