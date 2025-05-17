package bp.wf.data;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.wf.*;
import bp.sys.*;
import bp.en.*;
import bp.en.Map;
/** 
 我发起的流程
*/
public class MyStartFlow extends Entity
{

		///#region 基本属性
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.Readonly();
		uac.IsExp = true;
		return uac;
	}
	/** 
	 主键
	*/
	@Override
	public String getPK()
	{
		return MyStartFlowAttr.WorkID;
	}
	/** 
	 备注
	*/
	public final String getFlowNote()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.FlowNote);
	}
	public final void setFlowNote(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.FlowNote, value);
	}
	/** 
	 工作流程编号
	*/
	public final String getFK_Flow()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.FK_Flow);
	}
	public final void setFK_Flow(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.FK_Flow, value);
	}
	/** 
	 BillNo
	*/
	public final String getBillNo()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.BillNo);
	}
	public final void setBillNo(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.BillNo, value);
	}
	/** 
	 流程名称
	*/
	public final String getFlowName()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.FlowName);
	}
	public final void setFlowName(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.FlowName, value);
	}
	/** 
	 优先级
	*/
	public final int getPRI()  throws Exception
	{
		return this.GetValIntByKey(MyStartFlowAttr.PRI);
	}
	public final void setPRI(int value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.PRI, value);
	}
	/** 
	 待办人员数量
	*/
	public final int getTodoEmpsNum()  throws Exception
	{
		return this.GetValIntByKey(MyStartFlowAttr.TodoEmpsNum);
	}
	public final void setTodoEmpsNum(int value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.TodoEmpsNum, value);
	}
	/** 
	 待办人员列表
	*/
	public final String getTodoEmps()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.TodoEmps);
	}
	public final void setTodoEmps(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.TodoEmps, value);
	}
	/** 
	 参与人
	*/
	public final String getEmps()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.Emps);
	}
	public final void setEmps(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.Emps, value);
	}
	/** 
	 状态
	*/
	public final TaskSta getTaskSta()throws Exception
	{
		return TaskSta.forValue(this.GetValIntByKey(MyStartFlowAttr.TaskSta));
	}
	public final void setTaskSta(TaskSta value)throws Exception
	{
		SetValByKey(MyStartFlowAttr.TaskSta, value.getValue());
	}
	/** 
	 类别编号
	*/
	public final String getFK_FlowSort()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.FK_FlowSort);
	}
	public final void setFK_FlowSort(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.FK_FlowSort, value);
	}
	/** 
	 部门编号
	*/
	public final String getFK_Dept()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.FK_Dept);
	}
	public final void setFK_Dept(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.FK_Dept, value);
	}
	/** 
	 标题
	*/
	public final String getTitle()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.Title);
	}
	public final void setTitle(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.Title, value);
	}
	/** 
	 客户编号
	*/
	public final String getGuestNo()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.GuestNo);
	}
	public final void setGuestNo(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.GuestNo, value);
	}
	/** 
	 客户名称
	*/
	public final String getGuestName()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.GuestName);
	}
	public final void setGuestName(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.GuestName, value);
	}
	/** 
	 产生时间
	*/
	public final String getRDT()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.RDT);
	}
	public final void setRDT(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.RDT, value);
	}
	/** 
	 节点应完成时间
	*/
	public final String getSDTOfNode()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.SDTOfNode);
	}
	public final void setSDTOfNode(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.SDTOfNode, value);
	}
	/** 
	 流程应完成时间
	*/
	public final String getSDTOfFlow()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.SDTOfFlow);
	}
	public final void setSDTOfFlow(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.SDTOfFlow, value);
	}
	/** 
	 流程ID
	*/
	public final long getWorkID()  throws Exception
	{
		return this.GetValInt64ByKey(MyStartFlowAttr.WorkID);
	}
	public final void setWorkID(long value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.WorkID, value);
	}
	/** 
	 主线程ID
	*/
	public final long getFID()  throws Exception
	{
		return this.GetValInt64ByKey(MyStartFlowAttr.FID);
	}
	public final void setFID(long value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.FID, value);
	}
	/** 
	 父节点流程编号.
	*/
	public final long getPWorkID()  throws Exception
	{
		return this.GetValInt64ByKey(MyStartFlowAttr.PWorkID);
	}
	public final void setPWorkID(long value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.PWorkID, value);
	}
	/** 
	 父流程调用的节点
	*/
	public final int getPNodeID()  throws Exception
	{
		return this.GetValIntByKey(MyStartFlowAttr.PNodeID);
	}
	public final void setPNodeID(int value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.PNodeID, value);
	}
	/** 
	 PFlowNo
	*/
	public final String getPFlowNo()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.PFlowNo);
	}
	public final void setPFlowNo(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.PFlowNo, value);
	}
	/** 
	 吊起子流程的人员
	*/
	public final String getPEmp()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.PEmp);
	}
	public final void setPEmp(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.PEmp, value);
	}
	/** 
	 发起人
	*/
	public final String getStarter()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.Starter);
	}
	public final void setStarter(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.Starter, value);
	}
	/** 
	 发起人名称
	*/
	public final String getStarterName()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.StarterName);
	}
	public final void setStarterName(String value){
		this.SetValByKey(MyStartFlowAttr.StarterName, value);
	}
	/** 
	 发起人部门名称
	*/
	public final String getDeptName()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.DeptName);
	}
	public final void setDeptName(String value){
		this.SetValByKey(MyStartFlowAttr.DeptName, value);
	}
	/** 
	 当前节点名称
	*/
	public final String getNodeName()  throws Exception
	{
		return this.GetValStrByKey(MyStartFlowAttr.NodeName);
	}
	public final void setNodeName(String value){
		this.SetValByKey(MyStartFlowAttr.NodeName, value);
	}
	/** 
	 当前工作到的节点
	*/
	public final int getFK_Node()  throws Exception
	{
		return this.GetValIntByKey(MyStartFlowAttr.FK_Node);
	}
	public final void setNodeID(int value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.FK_Node, value);
	}
	/** 
	 工作流程状态
	*/
	public final WFState getWFState()throws Exception
	{
		return WFState.forValue(this.GetValIntByKey(MyStartFlowAttr.WFState));
	}
	public final void setWFState(WFState value) throws Exception {
		if (value == WFState.CompleteEnd || value == WFState.CompleteByNode)
		{
			SetValByKey(MyStartFlowAttr.WFSta, WFSta.Complete.getValue());
		}
		else if (value == WFState.Delete)
		{
			SetValByKey(MyStartFlowAttr.WFSta, WFSta.Etc.getValue());
		}
		else
		{
			SetValByKey(MyStartFlowAttr.WFSta, WFSta.Runing.getValue());
		}

		SetValByKey(MyStartFlowAttr.WFState, value.getValue());
	}
	/** 
	 状态(简单)
	*/
	public final WFSta getWFSta()throws Exception
	{
		return WFSta.forValue(this.GetValIntByKey(MyStartFlowAttr.WFSta));
	}
	public final void setWFSta(WFSta value)throws Exception
	{
		SetValByKey(MyStartFlowAttr.WFSta, value.getValue());
	}
	public final String getWFStateText()throws Exception
	{
		WFState ws = this.getWFState();
		switch (ws)
		{
			case CompleteByNode:
				return "已完成（非正常）";
			case CompleteEnd:
				return "已完成";
			case Runing:
				return "在运行";
			case Hungup:
				return "挂起";
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
		return this.GetValStrByKey(MyStartFlowAttr.GUID);
	}
	public final void setGUID(String value) throws Exception
	{
		SetValByKey(MyStartFlowAttr.GUID, value);
	}

		///#endregion


		///#region 参数属性.

	public final String getParasToNodes() throws Exception
	{
		return this.GetParaString("ToNodes");
	}

	public final void setParasToNodes(String value) throws Exception
	{
		this.SetPara("ToNodes", value);
	}
	/** 
	 加签信息
	*/

	public final String getParasAskForReply()
	{
		return this.GetParaString("AskForReply");
	}

	public final void setParasAskForReply(String value)
	{
		this.SetPara("AskForReply", value);
	}

		///#endregion 参数属性.


		///#region 构造函数
	/** 
	 产生的工作流程
	*/
	public MyStartFlow()
	{
	}
	public MyStartFlow(long workId) throws Exception {
		this.setWorkID(workId);
		this.Retrieve();
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

		Map map = new Map("WF_GenerWorkFlow", "我发起的流程");
		map.setEnType(EnType.View);

		map.AddTBIntPK(MyStartFlowAttr.WorkID, 0, "WorkID", false, false);
		map.AddTBString(MyStartFlowAttr.Title, null, "标题", true, false, 0, 300, 200, true);

		map.AddDDLEntities(MyStartFlowAttr.FK_Flow, null, "流程", new Flows(), false);

		map.AddTBString(MyStartFlowAttr.BillNo, null, "单据编号", true, true, 0, 100, 50);
		map.AddTBInt(MyStartFlowAttr.FK_Node, 0, "节点编号", false, false);

		map.AddDDLSysEnum(MyStartFlowAttr.WFSta, 0, "状态", true, true, MyStartFlowAttr.WFSta, "@0=运行中@1=已完成@2=其他");
		map.AddTBString(MyStartFlowAttr.Starter, null, "发起人", false, false, 0, 100, 100);
		map.AddTBDate(MyStartFlowAttr.RDT, "发起日期", true, true);

		map.AddTBString(MyStartFlowAttr.NodeName, null, "停留节点", true, true, 0, 100, 100, false);
		map.AddTBString(MyStartFlowAttr.TodoEmps, null, "当前处理人", true, false, 0, 100, 100, false);
		map.AddTBStringDoc(MyFlowAttr.FlowNote, null, "备注", true, false, true);


		map.AddTBString(MyFlowAttr.Emps, null, "参与人", false, false, 0, 4000, 100, true);

			// map.AddDDLSysEnum(MyFlowAttr.TSpan, 0, "时间段", true, false, MyFlowAttr.TSpan, "@0=本周@1=上周@2=两周以前@3=三周以前@4=更早");

			//隐藏字段.
		map.AddTBInt(MyStartFlowAttr.WFState, 0, "状态", false, false);
		map.AddTBInt(MyStartFlowAttr.FID, 0, "FID", false, false);
		map.AddTBInt(MyFlowAttr.PWorkID, 0, "PWorkID", false, false);

		map.AddSearchAttr(MyStartFlowAttr.WFSta, 130);
		map.AddHidden(MyStartFlowAttr.FID, "=", "0");

		map.DTSearchWay=DTSearchWay.ByDate;
		map.DTSearchLabel = "发起日期";
		map.DTSearchKey=MyStartFlowAttr.RDT;

			//我发起的流程.
		SearchNormal search = new SearchNormal(MyStartFlowAttr.Starter, "发起人", MyStartFlowAttr.Starter, "=", bp.web.WebUser.getNo(), 0, true);

		map.getSearchNormals().add(search);

		search = new SearchNormal(MyStartFlowAttr.WFState, "流程状态", MyStartFlowAttr.WFState, "not in", "('0')", 0, true);
		map.getSearchNormals().add(search);

	   RefMethod rm = new RefMethod();
			/*  rm.Title = "轨迹";
			 rm.ClassMethodName = this.ToString() + ".DoTrack";
			 rm.refMethodType = RefMethodType.LinkeWinOpen;
			 rm.Icon = "../../WF/Img/Track.png";
			 rm.ItIsForEns = true;
			 map.AddRefMethod(rm);*/

		rm = new RefMethod();
		rm.Title = "表单/轨迹";
		rm.ClassMethodName = this + ".DoOpenLastForm";
		rm.Icon = "../../WF/Img/Form.png";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.ItIsForEns = true;
		map.AddRefMethod(rm);

		  /*  rm = new RefMethod();
		    rm.Title = "打印表单";
		    rm.ClassMethodName = this.ToString() + ".DoPrintFrm";
		    rm.refMethodType = RefMethodType.LinkeWinOpen;
		    rm.ItIsForEns = false;
		    map.AddRefMethod(rm);*/

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	public final String DoPrintFrm()throws Exception
	{
		return "../../WorkOpt/Packup.htm?FileType=zip,pdf&WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFK_Flow() + "&NodeID=" + this.getFK_Node() + "&FK_Node=" + this.getFK_Node();
	}


		///#region 执行诊断
	public final String DoTrack()throws Exception
	{
		return "../../MyFlowView.htm?CurrTab=Truck&WorkID=" + this.getWorkID() + "&FID=" + this.getFID() + "&FK_Flow=" + this.getFK_Flow() + "&FK_Node=" + this.getFK_Node();
	}
	/** 
	 打开最后一个节点表单
	 
	 @return 
	*/
	public final String DoOpenLastForm()throws Exception
	{
		Paras pss = new Paras();
		pss.SQL = "SELECT MYPK FROM ND" + Integer.parseInt(this.getFK_Flow()) + "Track WHERE ActionType=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "ActionType AND WorkID=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "WorkID ORDER BY RDT DESC";
		pss.Add("ActionType", ActionType.Forward.getValue());
		pss.Add("WorkID", this.getWorkID());
		DataTable dt = DBAccess.RunSQLReturnTable(pss);
		if (dt != null && dt.Rows.size() > 0)
		{
			String myPk = dt.Rows.get(0).getValue(0).toString();
			return SystemConfig.getHostURLOfBS() +"/WF/MyView.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFK_Flow() + "&FK_Node=" + this.getFK_Node() + "&DoType=View&MyPK=" + myPk + "&PWorkID=" + this.getPWorkID();
		}

		Node nd = new Node(this.getFK_Node());
		nd.WorkID = this.getWorkID(); //为获取表单ID ( NodeFrmID )提供参数.

		return SystemConfig.getHostURLOfBS() +"/WF/CCForm/FrmGener.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFK_Flow() + "&FK_MapData=" + nd.getNodeFrmID() + "&ReadOnly=1&IsEdit=0";
	}

		///#endregion
}