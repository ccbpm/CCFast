package bp.wf.data;

import bp.da.*;
import bp.wf.*;
import bp.port.*;
import bp.sys.*;
import bp.en.*;
import bp.en.Map;
import bp.*;
import bp.wf.*;
import java.util.*;

/** 
 我授权的流程
*/
public class MyAuthto extends Entity
{

		///#region 基本属性
	@Override
	public UAC getHisUAC() 
	{
		UAC uac = new UAC();
		uac.Readonly();
		try {
			uac.IsExp = UserRegedit.HaveRoleForExp(this.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uac;
	}
	/** 
	 主键
	*/
	@Override
	public String getPK()
	{
		return MyAuthtoAttr.WorkID;
	}
	/** 
	 备注
	*/
	public final String getFlowNote()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.FlowNote);
	}
	public final void setFlowNote(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.FlowNote, value);
	}
	/** 
	 工作流程编号
	*/
	public final String getFK_Flow()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.FK_Flow);
	}
	public final void setFK_Flow(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.FK_Flow, value);
	}
	/** 
	 BillNo
	*/
	public final String getBillNo()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.BillNo);
	}
	public final void setBillNo(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.BillNo, value);
	}
	/** 
	 流程名称
	*/
	public final String getFlowName()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.FlowName);
	}
	public final void setFlowName(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.FlowName, value);
	}
	/** 
	 优先级
	*/
	public final int getPRI()  throws Exception
	{
		return this.GetValIntByKey(MyAuthtoAttr.PRI);
	}
	public final void setPRI(int value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.PRI, value);
	}
	/** 
	 待办人员数量
	*/
	public final int getTodoEmpsNum()  throws Exception
	{
		return this.GetValIntByKey(MyAuthtoAttr.TodoEmpsNum);
	}
	public final void setTodoEmpsNum(int value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.TodoEmpsNum, value);
	}
	/** 
	 待办人员列表
	*/
	public final String getTodoEmps()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.TodoEmps);
	}
	public final void setTodoEmps(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.TodoEmps, value);
	}
	/** 
	 参与人
	*/
	public final String getEmps()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.Emps);
	}
	public final void setEmps(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.Emps, value);
	}
	/** 
	 状态
	*/
	public final TaskSta getTaskSta()throws Exception
	{
		return TaskSta.forValue(this.GetValIntByKey(MyAuthtoAttr.TaskSta));
	}
	public final void setTaskSta(TaskSta value)throws Exception
	{
		SetValByKey(MyAuthtoAttr.TaskSta, value.getValue());
	}
	/** 
	 类别编号
	*/
	public final String getFK_FlowSort()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.FK_FlowSort);
	}
	public final void setFK_FlowSort(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.FK_FlowSort, value);
	}
	/** 
	 部门编号
	*/
	public final String getFK_Dept()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.FK_Dept);
	}
	public final void setFK_Dept(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.FK_Dept, value);
	}
	/** 
	 标题
	*/
	public final String getTitle()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.Title);
	}
	public final void setTitle(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.Title, value);
	}
	/** 
	 客户编号
	*/
	public final String getGuestNo()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.GuestNo);
	}
	public final void setGuestNo(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.GuestNo, value);
	}
	/** 
	 客户名称
	*/
	public final String getGuestName()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.GuestName);
	}
	public final void setGuestName(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.GuestName, value);
	}
	/** 
	 产生时间
	*/
	public final String getRDT()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.RDT);
	}
	public final void setRDT(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.RDT, value);
	}
	/** 
	 节点应完成时间
	*/
	public final String getSDTOfNode()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.SDTOfNode);
	}
	public final void setSDTOfNode(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.SDTOfNode, value);
	}
	/** 
	 流程应完成时间
	*/
	public final String getSDTOfFlow()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.SDTOfFlow);
	}
	public final void setSDTOfFlow(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.SDTOfFlow, value);
	}
	/** 
	 流程ID
	*/
	public final long getWorkID()  throws Exception
	{
		return this.GetValInt64ByKey(MyAuthtoAttr.WorkID);
	}
	public final void setWorkID(long value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.WorkID, value);
	}
	/** 
	 主线程ID
	*/
	public final long getFID()  throws Exception
	{
		return this.GetValInt64ByKey(MyAuthtoAttr.FID);
	}
	public final void setFID(long value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.FID, value);
	}
	/** 
	 父节点流程编号.
	*/
	public final long getPWorkID()  throws Exception
	{
		return this.GetValInt64ByKey(MyAuthtoAttr.PWorkID);
	}
	public final void setPWorkID(long value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.PWorkID, value);
	}
	/** 
	 父流程调用的节点
	*/
	public final int getPNodeID()  throws Exception
	{
		return this.GetValIntByKey(MyAuthtoAttr.PNodeID);
	}
	public final void setPNodeID(int value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.PNodeID, value);
	}
	/** 
	 PFlowNo
	*/
	public final String getPFlowNo()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.PFlowNo);
	}
	public final void setPFlowNo(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.PFlowNo, value);
	}
	/** 
	 吊起子流程的人员
	*/
	public final String getPEmp()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.PEmp);
	}
	public final void setPEmp(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.PEmp, value);
	}
	/** 
	 发起人
	*/
	public final String getStarter()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.Starter);
	}
	public final void setStarter(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.Starter, value);
	}
	/** 
	 发起人名称
	*/
	public final String getStarterName()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.StarterName);
	}
	public final void setStarterName(String value){
		this.SetValByKey(MyAuthtoAttr.StarterName, value);
	}
	/** 
	 发起人部门名称
	*/
	public final String getDeptName()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.DeptName);
	}
	public final void setDeptName(String value){
		this.SetValByKey(MyAuthtoAttr.DeptName, value);
	}
	/** 
	 当前节点名称
	*/
	public final String getNodeName()  throws Exception
	{
		return this.GetValStrByKey(MyAuthtoAttr.NodeName);
	}
	public final void setNodeName(String value){
		this.SetValByKey(MyAuthtoAttr.NodeName, value);
	}
	/** 
	 当前工作到的节点
	*/
	public final int getFK_Node()  throws Exception
	{
		return this.GetValIntByKey(MyAuthtoAttr.FK_Node);
	}
	public final void setNodeID(int value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.FK_Node, value);
	}
	/** 
	 工作流程状态
	*/
	public final WFState getWFState()throws Exception
	{
		return WFState.forValue(this.GetValIntByKey(MyAuthtoAttr.WFState));
	}
	public final void setWFState(WFState value) throws Exception {
		if (value == WFState.CompleteEnd  || value == WFState.CompleteByNode)
		{
			SetValByKey(MyAuthtoAttr.WFSta, WFSta.Complete.getValue());
		}
		else if (value == WFState.Delete)
		{
			SetValByKey(MyAuthtoAttr.WFSta, WFSta.Etc.getValue());
		}
		else
		{
			SetValByKey(MyAuthtoAttr.WFSta, WFSta.Runing.getValue());
		}

		SetValByKey(MyAuthtoAttr.WFState, value.getValue());
	}
	/** 
	 状态(简单)
	*/
	public final WFSta getWFSta()throws Exception
	{
		return WFSta.forValue(this.GetValIntByKey(MyAuthtoAttr.WFSta));
	}
	public final void setWFSta(WFSta value)throws Exception
	{
		SetValByKey(MyAuthtoAttr.WFSta, value.getValue());
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
		return this.GetValStrByKey(MyAuthtoAttr.GUID);
	}
	public final void setGUID(String value) throws Exception
	{
		SetValByKey(MyAuthtoAttr.GUID, value);
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

	public final String getParasAskForReply() throws Exception
	{
		return this.GetParaString("AskForReply");
	}

	public final void setParasAskForReply(String value) throws Exception
	{
		this.SetPara("AskForReply", value);
	}

		///#endregion 参数属性.


		///#region 构造函数
	/** 
	 我授权的流程
	*/
	public MyAuthto()
	{
	}
	/** 
	 我授权的流程
	 
	 param workId 工作ID
	*/
	public MyAuthto(long workId) throws Exception {
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

		Map map = new Map("WF_GenerWorkFlow", "我授权的流程");
		map.setEnType(EnType.View);

		map.AddTBIntPK(MyAuthtoAttr.WorkID, 0, "WorkID", false, false);
		map.AddTBString(MyAuthtoAttr.Title, null, "标题", true, false, 0, 300, 200, true);

		map.AddDDLEntities(MyAuthtoAttr.FK_Flow, null, "流程", new Flows(), false);

		map.AddTBString(MyAuthtoAttr.BillNo, null, "单据编号", true, true, 0, 100, 50);
		map.AddTBInt(MyAuthtoAttr.FK_Node, 0, "节点编号", false, false);

		map.AddDDLSysEnum(MyAuthtoAttr.WFSta, 0, "状态", true, true, MyAuthtoAttr.WFSta, "@0=运行中@1=已完成@2=其他");
		map.AddTBString(MyAuthtoAttr.Starter, null, "发起人", false, false, 0, 100, 100);
		map.AddTBDate(MyAuthtoAttr.RDT, "发起日期", true, true);

		map.AddTBString(MyAuthtoAttr.NodeName, null, "停留节点", true, true, 0, 100, 100, false);
		map.AddTBString(MyAuthtoAttr.TodoEmps, null, "当前处理人", true, false, 0, 100, 100, false);
		map.AddTBString(MyFlowAttr.Emps, null, "参与人", false, false, 0, 4000, 100, true);
		//    map.AddDDLSysEnum(MyFlowAttr.TSpan, 0, "时间段", true, false, MyFlowAttr.TSpan, "@0=本周@1=上周@2=两周以前@3=三周以前@4=更早");

			//隐藏字段.
		map.AddTBInt(MyAuthtoAttr.WFState, 0, "状态", false, false);
		map.AddTBInt(MyAuthtoAttr.FID, 0, "FID", false, false);
		map.AddTBInt(MyFlowAttr.PWorkID, 0, "PWorkID", false, false);
		map.AddTBString(MyFlowAttr.AtPara, null, "AtPara", false, false, 0, 4000, 100, false);

		map.AddSearchAttr(MyAuthtoAttr.WFSta, 130);

		map.DTSearchWay=DTSearchWay.ByDate;
		map.DTSearchLabel = "发起日期";
		map.DTSearchKey=MyAuthtoAttr.RDT;


			///#region 增加多个隐藏条件.
			////我授权的流程.
			//SearchNormal search = new SearchNormal(MyAuthtoAttr.Starter, "发起人",
			//    MyAuthtoAttr.Starter, "=", bp.web.WebUser.getNo(), 0, true);
			//map.AttrsOfSearch.Add(search);

			//search = new SearchNormal(MyAuthtoAttr.WFState, "流程状态",
			//    MyAuthtoAttr.WFState, " not in", "('0')", 0, true);
			//map.AttrsOfSearch.Add(search);

		SearchNormal search = new SearchNormal(MyAuthtoAttr.AtPara, "授权人", MyAuthtoAttr.AtPara, " LIKE ", " '%@Auth=" + bp.web.WebUser.getName() + "%' ", 0, true);
		map.getSearchNormals().add(search);


			///#endregion 增加多个隐藏条件.


		RefMethod rm = new RefMethod();
		rm.Title = "轨迹";
		rm.ClassMethodName = this.toString() + ".DoTrack";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Icon = "../../WF/Img/Track.png";
		//rm.ItIsForEns = true;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "表单";
		rm.ClassMethodName = this.toString() + ".DoForm";
		rm.Icon = "../../WF/Img/Form.png";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		//rm.ItIsForEns = true;
		map.AddRefMethod(rm);

			//rm = new RefMethod();
			//rm.Title = "打印表单";
			//rm.ClassMethodName = this.ToString() + ".DoPrintFrm";
			//rm.refMethodType = RefMethodType.LinkeWinOpen;
			//rm.ItIsForEns = false;
			//map.AddRefMethod(rm);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 执行诊断
	public final String DoPrintFrm()throws Exception
	{
		return "../../WorkOpt/Packup.htm?FileType=zip,pdf&WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFK_Flow() + "&NodeID=" + this.getFK_Node() + "&FK_Node=" + this.getFK_Node();
	}
	public final String DoTrack()throws Exception
	{
		return "../../WFRpt.htm?CurrTab=Truck&WorkID=" + this.getWorkID() + "&FID=" + this.getFID() + "&FK_Flow=" + this.getFK_Flow() + "&FK_Node=" + this.getFK_Node();
	}
	/** 
	 打开表单
	 
	 @return 
	*/
	public final String DoForm()throws Exception
	{
		return "../MyViewGener.htm?HttpHandlerName=bp.wf.httphandler.WF_MyView&WorkID=" + this.getWorkID() + "&NodeID=" + this.getFK_Node() + "&FK_Node=" + this.getFK_Node() + "&FID=" + this.getFID() + "&UserNo=" + bp.web.WebUser.getNo() + "&FK_Flow=" + this.getFK_Flow();
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
			return "/WF/WFRpt.htm?CurrTab=Frm&WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFK_Flow() + "&FK_Node=" + this.getFK_Node() + "&DoType=View&MyPK=" + myPk + "&PWorkID=" + this.getPWorkID();
		}

		Node nd = new Node(this.getFK_Node());
		nd.WorkID = this.getWorkID(); //为获取表单ID ( NodeFrmID )提供参数.

		return "/WF/CCForm/FrmGener.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFK_Flow() + "&FK_MapData=" + nd.getNodeFrmID() + "&ReadOnly=1&IsEdit=0";
	}

		///#endregion
}