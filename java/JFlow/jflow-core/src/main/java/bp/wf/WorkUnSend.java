package bp.wf;

import bp.web.*;
import bp.da.*;
import bp.port.*;
import bp.sys.*;
import bp.wf.template.*;
import bp.difference.*;
import java.util.*;
import java.math.*;

/** 
 撤销发送
*/
public class WorkUnSend
{

		///#region 属性.
	private String _AppType = null;
	/** 
	 虚拟目录的路径
	*/
	public final String getAppType()
	{
		if (_AppType == null)
		{
			if (SystemConfig.isBSsystem() == false)
			{
				_AppType = "WF";
			}
			else
			{

				boolean b = ContextHolderUtils.getRequest().getRequestURI().toLowerCase().contains("oneflow");
				if (b)
				{
					_AppType = "WF/OneFlow";
				}
				else
				{
					_AppType = "WF";
				}

			}
		}
		return _AppType;
	}
	private String _VirPath = null;
	/** 
	 虚拟目录的路径
	*/
	public final String getVirPath()
	{
		if (_VirPath == null)
		{
			if (SystemConfig.isBSsystem())
			{
				_VirPath = Glo.getCCFlowAppPath(); //BP.Sys.Base.Glo.Request.ApplicationPath;
			}
			else
			{
				_VirPath = "";
			}
		}
		return _VirPath;
	}
	public String FlowNo = null;
	private Flow _HisFlow = null;
	public final Flow getHisFlow() throws Exception {
		if (_HisFlow == null)
		{
			this._HisFlow = new Flow(this.FlowNo);
		}
		return this._HisFlow;
	}
	/** 
	 工作ID
	*/
	public long WorkID = 0;
	/** 
	 FID
	*/
	public long FID = 0;
	/** 
	 是否是干流
	*/
	public final boolean getItIsMainFlow()
	{
		if (this.FID != 0 && this.FID != this.WorkID)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

		///#endregion

	/** 
	 撤销发送
	*/

	public WorkUnSend(String flowNo, long workID, int unSendToNode)
	{
		this(flowNo, workID, unSendToNode, 0);
	}

	public WorkUnSend(String flowNo, long workID)
	{
		this(flowNo, workID, 0, 0);
	}

	public WorkUnSend(String flowNo, long workID, int unSendToNode, long fid)
	{
		this.FlowNo= flowNo;
		this.WorkID = workID;
		this.FID = fid;
		this.UnSendToNode = unSendToNode; //撤销到节点.
	}
	public int UnSendToNode = 0;
	/** 
	 得到当前的进行中的工作。
	 
	 @return 		 
	*/
	public final WorkNode GetCurrentWorkNode() throws Exception {
		int currNodeID = 0;
		GenerWorkFlow gwf = new GenerWorkFlow(this.WorkID);
		gwf.setWorkID(this.WorkID);
		if (gwf.RetrieveFromDBSources() == 0)
		{
			// this.DoFlowOver(ActionType.FlowOver, "非正常结束，没有找到当前的流程记录。");
			throw new RuntimeException("@" + String.format("工作流程%1$s已经完成。", this.WorkID));
		}

		Node nd = new Node(gwf.getNodeID());
		Work work = nd.getHisWork();
		work.setOID(this.WorkID);
		work.setNodeID(nd.getNodeID());
		work.SetValByKey("FK_Dept", WebUser.getDeptNo());
		if (work.RetrieveFromDBSources() == 0)
		{
			Log.DebugWriteError("@WorkID=" + this.WorkID + ",FK_Node=" + gwf.getNodeID() + ".不应该出现查询不出来工作."); // 没有找到当前的工作节点的数据，流程出现未知的异常。
			work.setRec(WebUser.getNo());
			try
			{
				work.Insert();
			}
			catch (RuntimeException ex)
			{
				Log.DebugWriteError("@没有找到当前的工作节点的数据，流程出现未知的异常" + ex.getMessage() + ",不应该出现"); // 没有找到当前的工作节点的数据
			}
		}
		work.setFID(gwf.getFID());
		WorkNode wn = new WorkNode(work, nd);
		return wn;
	}
	/** 
	 执行子线程的撤销.
	 
	 @return 
	*/
	private String DoThreadUnSend() throws Exception {
		//定义当前的节点.
		WorkNode wn = this.GetCurrentWorkNode();

		GenerWorkFlow gwf = new GenerWorkFlow(this.WorkID);
		Node nd = new Node(gwf.getNodeID());


			///#region 求的撤销的节点.
		int cancelToNodeID = 0;

		if (nd.getHisCancelRole() == CancelRole.SpecNodes)
		{
			/*1.指定的节点可以撤销，首先判断是否设置指定的节点.*/

			//
			NodeCancels ncs = new NodeCancels();
			ncs.Retrieve(NodeCancelAttr.FK_Node, wn.getHisNode().getNodeID(), null);
			String cancelNodes = wn.getHisNode().GetParaString("CancelNodes");
			if (ncs.size() == 0 && DataType.IsNullOrEmpty(cancelNodes)==true)
			{
				throw new RuntimeException("@流程设计错误, 您设置了当前节点(" + wn.getHisNode().getName() + ")可以让指定的节点人员撤销，但是您没有设置指定的节点.");
			}

			//获取Track表
			String truckTable = "ND" + Integer.parseInt(wn.getHisNode().getFlowNo()) + "Track";

			//获取到当前节点走过的节点 与 设定可撤销节点的交集
			String sql = "SELECT DISTINCT(FK_Node) FROM  WF_GenerWorkerlist WHERE ";
			sql += " FK_Node IN(SELECT CancelTO FROM WF_NodeCancel WHERE FK_Node=" + wn.getHisNode().getNodeID() + ") AND FK_Emp='" + WebUser.getNo() + "'";

			String nds = DBAccess.RunSQLReturnString(sql);
			if (DataType.IsNullOrEmpty(nds))
			{

				throw new RuntimeException("@您不能执行撤消发送，两种原因：1，你不具备撤销该节点的功能；2.流程设计错误，你指定的可以撤销的节点不在流程运转中走过的节点.");
			}

			//获取可以删除到的节点
			cancelToNodeID = Integer.parseInt(nds.split("[,]", -1)[0]);

		}

		if (nd.getHisCancelRole() == CancelRole.OnlyNextStep)
		{
			/*如果仅仅允许撤销上一步骤.*/
			WorkNode wnPri = wn.GetPreviousWorkNode();

			GenerWorkerList wl = new GenerWorkerList();
			int num = wl.Retrieve(GenerWorkerListAttr.FK_Emp, WebUser.getNo(), GenerWorkerListAttr.FK_Node, wnPri.getHisNode().getNodeID());
			if (num == 0)
			{
				throw new RuntimeException("@您不能执行撤消发送，因为当前工作不是您发送的或下一步工作已处理。");
			}
			cancelToNodeID = wnPri.getHisNode().getNodeID();

		}

		if (cancelToNodeID == 0)
		{
			throw new RuntimeException("@没有求出要撤销到的节点.");
		}

			///#endregion 求的撤销的节点.

		/********** 开始执行撤销. **********************/
		Node cancelToNode = new Node(cancelToNodeID);

		switch (cancelToNode.getHisNodeWorkType())
		{
			case StartWorkFL:
			case WorkFHL:
			case WorkFL:

				// 调用撤消发送前事件。
				ExecEvent.DoNode(EventListNode.UndoneBefore, nd, wn.getHisWork(), null);

				bp.wf.Dev2Interface.Node_FHL_KillSubFlow(this.WorkID); //杀掉子线程.

				// 调用撤消发送前事件。
				ExecEvent.DoNode(EventListNode.UndoneAfter, nd, wn.getHisWork(), null);
				return "KillSubThared@子线程撤销成功.";
			default:
				break;

		}
		//  if (cancelToNode.getHisNodeWorkType() == NodeWorkType.StartWorkFL)


		WorkNode wnOfCancelTo = new WorkNode(this.WorkID, cancelToNodeID);

		// 调用撤消发送前事件。
		String msg = ExecEvent.DoNode(EventListNode.UndoneBefore, nd, wn.getHisWork(), null);


			///#region 删除当前节点数据。

		// 删除产生的工作列表。
		DeleteSpanNodesGenerWorkerListData();
		//GenerWorkerLists wls = new GenerWorkerLists();
		//wls.Delete(GenerWorkerListAttr.WorkID, this.WorkID, GenerWorkerListAttr.FK_Node, gwf.getNodeID());


		// 删除附件信息。
		DBAccess.RunSQL("DELETE FROM Sys_FrmAttachmentDB WHERE FK_MapData='ND" + gwf.getNodeID() + "' AND RefPKVal='" + this.WorkID + "'");

			///#endregion 删除当前节点数据。

		// 更新.
		gwf.setNodeID(cancelToNode.getNodeID());
		gwf.setNodeName(cancelToNode.getName());
		//如果不启动自动记忆，删除tonodes,用于 选择节点发送。撤消后，可重新选择节点发送
		if (cancelToNode.getItIsRememberMe() == false)
		{
			gwf.setParasToNodes("");
		}

		if (cancelToNode.getItIsEnableTaskPool() && Glo.isEnableTaskPool())
		{
			gwf.setTaskSta(TaskSta.Takeback);
		}
		else
		{
			gwf.setTaskSta(TaskSta.None);
		}

		gwf.setTodoEmps(WebUser.getNo() + "," + WebUser.getName() + ";");
		gwf.Update();

		if (cancelToNode.getItIsEnableTaskPool() && Glo.isEnableTaskPool())
		{
			//设置全部的人员不可用。
			DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0,  IsEnable=-1 WHERE WorkID=" + this.WorkID + " AND FK_Node=" + gwf.getNodeID());

			//设置当前人员可用。
			DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0,  IsEnable=1  WHERE WorkID=" + this.WorkID + " AND FK_Node=" + gwf.getNodeID() + " AND FK_Emp='" + WebUser.getNo() + "'");
		}
		else
		{
			DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0  WHERE WorkID=" + this.WorkID + " AND FK_Node=" + gwf.getNodeID());
		}

		//更新当前节点，到rpt里面。
		DBAccess.RunSQL("UPDATE " + this.getHisFlow().getPTable() + " SET FlowEndNode=" + gwf.getNodeID() + " WHERE OID=" + this.WorkID);

		// 记录日志..
		wn.AddToTrack(ActionType.UnSend, WebUser.getNo(), WebUser.getName(), cancelToNode.getNodeID(), cancelToNode.getName(), "无");

		// 删除数据.
		if (wn.getHisNode().getItIsStartNode())
		{
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkFlow WHERE WorkID=" + this.WorkID);
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + this.WorkID + " AND FK_Node=" + nd.getNodeID());
		}

		if (wn.getHisNode().getItIsEval())
		{
			/*如果是质量考核节点，并且撤销了。*/
			DBAccess.RunSQL("DELETE FROM WF_CHEval WHERE FK_Node=" + wn.getHisNode().getNodeID() + " AND WorkID=" + this.WorkID);
		}


			///#region 恢复工作轨迹，解决工作抢办。
		if (cancelToNode.getItIsStartNode() == false && cancelToNode.getItIsEnableTaskPool() == false)
		{
			WorkNode ppPri = wnOfCancelTo.GetPreviousWorkNode();
			GenerWorkerList wl = new GenerWorkerList();
			wl.Retrieve(GenerWorkerListAttr.FK_Node, wnOfCancelTo.getHisNode().getNodeID(), GenerWorkerListAttr.WorkID, this.WorkID);
			// DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE FK_Node=" + backtoNodeID + " AND WorkID=" + this.WorkID);
			RememberMe rm = new RememberMe();
			rm.Retrieve(RememberMeAttr.FK_Node, wnOfCancelTo.getHisNode().getNodeID(), RememberMeAttr.FK_Emp, ppPri.getHisWork().getRec());

			String[] myEmpStrs = rm.getObjs().split("[@]", -1);
			for (String s : myEmpStrs)
			{
				if (DataType.IsNullOrEmpty(s) == true)
				{
					continue;
				}

				if (Objects.equals(s, wl.getEmpNo()))
				{
					continue;
				}
				GenerWorkerList wlN = new GenerWorkerList();
				wlN.Copy(wl);
				wlN.setEmpNo(s);

			   Emp myEmp = new Emp(s);
				wlN.setEmpName(myEmp.getName());
				wlN.setDeptNo(myEmp.getDeptNo());
				wlN.setDeptName(myEmp.getDeptText());

				wlN.Insert();
			}
		}

			///#endregion 恢复工作轨迹，解决工作抢办。


			///#region 如果是开始节点, 检查此流程是否有子线程，如果有则删除它们。
		if (nd.getItIsStartNode())
		{
			/*要检查一个是否有 子流程，如果有，则删除它们。*/
			GenerWorkFlows gwfs = new GenerWorkFlows();
			gwfs.Retrieve(GenerWorkFlowAttr.PWorkID, this.WorkID, null);
			if (gwfs.size() > 0)
			{
				for (GenerWorkFlow item : gwfs.ToJavaList())
				{
					/*删除每个子线程.*/
					bp.wf.Dev2Interface.Flow_DoDeleteFlowByReal(item.getWorkID(), true);
				}
			}
		}

			///#endregion


			///#region 计算完成率。
		boolean isSetEnable = false; //是否关闭合流节点待办.
		String mysql = "SELECT COUNT(DISTINCT WorkID) FROM WF_GenerWorkerlist WHERE FID=" + this.FID + " AND IsPass=1 AND FK_Node IN (SELECT Node FROM WF_Direction WHERE ToNode=" + wn.getHisNode().getNodeID() + ")";
		BigDecimal numOfPassed = DBAccess.RunSQLReturnValDecimal(mysql, BigDecimal.valueOf(0), 1);

		if (nd.getPassRate().compareTo(new BigDecimal("100")) == 0)
		{
			isSetEnable = true;
		}
		else
		{
			mysql = "SELECT COUNT(DISTINCT WorkID) FROM WF_GenerWorkFlow WHERE FID=" + this.FID;
			BigDecimal numOfAll = DBAccess.RunSQLReturnValDecimal(mysql, BigDecimal.valueOf(0), 1);

			BigDecimal rate = numOfPassed.divide(numOfAll.multiply(BigDecimal.valueOf(100)));
			if (nd.getPassRate().compareTo(rate) > 0)
			{
				isSetEnable = true;
			}
		}

		GenerWorkFlow maingwf = new GenerWorkFlow(this.FID);
		maingwf.SetPara("ThreadCount", numOfPassed.toString());
		maingwf.Update();

		//是否关闭合流节点待办.
		if (isSetEnable == true)
		{
			DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=3 WHERE WorkID=" + this.FID + " AND  FK_Node=" + wn.getHisNode().getNodeID());
		}

			///#endregion

		//调用撤消发送后事件。
		msg += ExecEvent.DoNode(EventListNode.UndoneAfter, nd, wn.getHisWork(), null);

		if (wnOfCancelTo.getHisNode().getItIsStartNode())
		{
			return "@撤消执行成功. " + msg;
		}
		else
		{
			return "@撤消执行成功. " + msg;
		}

//		return "工作已经被您撤销到:" + cancelToNode.getName();
	}
	/** 
	 
	 
	 @return 
	*/
	public final String DoUnSend() throws Exception {
		String str = DoUnSendIt();
		if(str.contains("err@") == false){
			String table = "ND" + Integer.parseInt(FlowNo) + "Track";
			String dbStr = SystemConfig.getAppCenterDBVarStr();
			int nodeID = DBAccess.RunSQLReturnValInt("SELECT FK_Node FROM WF_GenerWorkFlow WHERE WorkID=" + this.WorkID, 0);

			//2. 检查是否原路返回? 如果有，撤销后改变为 退回状态. for:北科大.
			/* 检查该退回是否是原路返回 ? */
			Paras ps = new Paras();
			ps.SQL = "SELECT NDFrom,EmpFrom,EmpFromT FROM "+ table + " WHERE ActionType IN(2,201) AND WorkID=" + dbStr + "WorkID  AND NDTo=" +  nodeID + "   ORDER BY RDT DESC";
			ps.Add(TrackAttr.WorkID, this.WorkID);
			DataTable mydt = DBAccess.RunSQLReturnTable(ps);
			if (mydt.Rows.size() != 0)
				DBAccess.RunSQL("UPDATE WF_GenerWorkFlow SET WFState=5 WHERE WorkID="+this.WorkID);

			/*int fk_node = DBAccess.RunSQLReturnValInt("SELECT FK_Node FROM WF_GenerWorkFlow WHERE WorkID=" + this.WorkID, 0);
			//将自己审核的信息和保存的历史审核信息都删除.（历史审核信息保存在ActionType=1的Msg字段中以'WorkCheck@XXX'的形式保存）
			String sql = "DELETE FROM ND" + Integer.parseInt(FlowNo) + "Track WHERE WorkID = " + this.WorkID + " AND ActionType IN (" + ActionType.WorkCheck.getValue() + "," + ActionType.Forward.getValue() + ") AND NDFrom = " + fk_node + " AND EmpFrom = '" + WebUser.getNo() + "'";
			DBAccess.RunSQL(sql);*/
		}

		return str;
	}
	/** 
	 执行撤消
	*/
	private String DoUnSendIt() throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(this.WorkID);
		this.FlowNo = gwf.getFlowNo();

		if (gwf.getWFState() == WFState.CompleteByNode)
		{
			return "err@该流程已经完成，您不能撤销。";
		}

		// 如果停留的节点是分合流。
		Node nd = new Node(gwf.getNodeID());

		/*该节点不允许撤销.*/
		if (nd.getHisCancelRole() == CancelRole.None)
		{
			return "err@当前节点，不允许撤销。";
		}

		if (nd.getItIsStartNode() && nd.getHisNodeWorkType() != NodeWorkType.StartWorkFL)
		{
			return "err@当前节点是开始节点，所以您不能撤销。";
		}

		//如果撤销到的节点和当前流程运行到的节点相同，则是分流、或者分河流
		if (this.UnSendToNode == nd.getNodeID())
		{
			//如果当前节点是分流、分合流节点则可以撤销
			if (nd.getHisNodeWorkType() == NodeWorkType.StartWorkFL || nd.getHisNodeWorkType() == NodeWorkType.WorkFL || nd.getHisNodeWorkType() == NodeWorkType.WorkFHL)
			{
				//获取当前节点的子线程
				String truckTable = "ND" + Integer.parseInt(nd.getFlowNo()) + "Track";
				String threadSQL = "SELECT FK_Node,WorkID,Emps FROM WF_GenerWorkFlow  WHERE FID=" + this.WorkID + " AND FK_Node" + " IN(SELECT DISTINCT(NDTo) FROM " + truckTable + "  WHERE ActionType=" + ActionType.ForwardFL.getValue() + " AND WorkID=" + this.WorkID + " AND NDFrom='" + nd.getNodeID() + "'" + "  ) ";
				DataTable dt = DBAccess.RunSQLReturnTable(threadSQL);
				if (dt == null || dt.Rows.size() == 0)
				{
					return "err@流程运行错误：当不存在子线程时,改过程应该处于待办状态";
				}

				String toEmps = "";
				for (DataRow dr : dt.Rows)
				{
					Node threadnd = new Node(dr.getValue("FK_Node").toString());
					// 调用撤消发送前事件。
					ExecEvent.DoNode(EventListNode.UndoneBefore, nd, nd.getHisWork(), null);

					bp.wf.Dev2Interface.Node_FHL_KillSubFlow(Long.parseLong(dr.getValue("WorkID").toString())); //杀掉子线程.

					// 调用撤消发送前事件。
					Work work = nd.getHisWork();
					work.setOID(this.WorkID);
					work.setNodeID(nd.getNodeID());
					ExecEvent.DoNode(EventListNode.UndoneAfter, nd, work, null);

					toEmps += dr.getValue("Emps").toString().replace('@', ',');
				}
				//恢复上一步发送人
				dt = Dev2Interface.Flow_GetPreviousNodeTrack(this.WorkID, nd.getNodeID());
				if (dt != null && dt.Rows.size() > 0)
				{
					gwf.setSender(dt.Rows.get(0).getValue("EmpFrom").toString() + "," + dt.Rows.get(0).getValue("EmpFromT").toString() + ";");
				}

				if (nd.getItIsEnableTaskPool() && Glo.isEnableTaskPool())
				{
					gwf.setTaskSta(TaskSta.Takeback);
				}
				else
				{
					gwf.setTaskSta(TaskSta.None);
				}

				gwf.setTodoEmps(WebUser.getNo() + "," + WebUser.getName() + ";");
				gwf.Update();
				//并且修改当前人员的待办
				DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE WorkID=" + this.WorkID + " AND FK_Node=" + gwf.getNodeID() + " AND FK_Emp='" + WebUser.getNo() + "'");
				return "撤销成功";
			}
		}

		//如果启用了对方已读，就不能撤销.
		if (nd.getCancelDisWhenRead() == true)
		{
			//撤销到的节点是干流程节点/子线程撤销到子线程
			int i = DBAccess.RunSQLReturnValInt("SELECT SUM(IsRead) AS Num FROM WF_GenerWorkerlist WHERE WorkID=" + this.WorkID + " AND FK_Node=" + gwf.getNodeID(), 0);
			if (i >= 1)
			{
				return "err@当前待办已经有[" + i + "]个工作人员打开了该工作,您不能撤销.";
			}

			//干流节点撤销到子线程
			i = DBAccess.RunSQLReturnValInt("SELECT SUM(IsRead) AS Num FROM WF_GenerWorkerlist WHERE WorkID=" + this.FID + " AND FK_Node=" + gwf.getNodeID(), 0);
			if (i >= 1)
			{
				return "err@当前待办已经有[" + i + "]个工作人员打开了该工作,您不能撤销.";
			}
		}

		//对方已审批就不能撤销
		int count = DBAccess.RunSQLReturnValInt("SELECT SUM(IsPass) AS Num FROM WF_GenerWorkerlist WHERE IsPass=1 AND (WorkID=" + this.WorkID + " OR WorkID="+this.FID+") AND FK_Node=" + gwf.getNodeID()+" AND FK_Emp!='"+WebUser.getNo()+"'", 0);
		if (count >= 1)
		{
			return "err@当前待办已经有[" + count + "]个工作人员处理了该工作,您不能撤销.";
		}


			///#region 如果是越轨流程状态 .
		String sql = "SELECT COUNT(*) AS Num FROM WF_GenerWorkerlist WHERE WorkID=" + this.WorkID + " AND IsPass=80";
		if (DBAccess.RunSQLReturnValInt(sql, 0) != 0)
		{
			//求出来越轨子流程workid并把它删除掉.
			GenerWorkFlow gwfSubFlow = new GenerWorkFlow();
			int i = gwfSubFlow.Retrieve(GenerWorkFlowAttr.PWorkID, this.WorkID);
			if (i == 1)
			{
				bp.wf.Dev2Interface.Flow_DoDeleteFlowByReal(gwfSubFlow.getWorkID(), true);
			}

			//执行回复当前节点待办..
			sql = "UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE IsPass=80 AND FK_Node=" + gwf.getNodeID() + " AND WorkID=" + this.WorkID;
			DBAccess.RunSQL(sql);

			return "撤销延续流程执行成功，撤销到[" + gwf.getNodeName() + "],撤销给[" + gwf.getTodoEmps() + "]";
		}

			///#endregion 如果是越轨流程状态 .

		//if (BP.WF.Dev2Interface.Flow_IsCanDoCurrentWork(this.WorkID, WebUser.getNo()) == true)
		//    return "info@您有处理当前工作的权限,可能您已经执行了撤销,请使用退回或者发送功能.";



			///#region 判断是否是会签状态,是否是会签人做的撤销. 主持人是不能撤销的.
		if (gwf.getHuiQianTaskSta() != HuiQianTaskSta.None)
		{
			boolean IsEnableUnSendWhenHuiQian = SystemConfig.GetValByKeyBoolen("IsEnableUnSendWhenHuiQian",false);
			if (IsEnableUnSendWhenHuiQian == false)
			{
				return "info@当前节点是会签状态，您不能执行撤销.";
			}

			GenerWorkerList gwl = new GenerWorkerList();
			int numOfmyGwl = gwl.Retrieve(GenerWorkerListAttr.FK_Emp, WebUser.getNo(), GenerWorkerListAttr.WorkID, this.WorkID, GenerWorkerListAttr.FK_Node, gwf.getNodeID());

			//如果没有找到当前会签人.
			if (numOfmyGwl == 0)
			{
				return "err@当前节点[" + gwf.getNodeName() + "]是会签状态,[" + gwf.getTodoEmps() + "]在执行会签,您不能执行撤销.";
			}

			if (gwl.getItIsHuiQian() == true)
			{

			}

			//如果是会签人，就让其显示待办.
			gwl.setPassInt(0);
			gwl.setItIsEnable(true);
			gwl.Update();

			// 在待办人员列表里加入他. 要判断当前人员是否是主持人,如果是主持人的话，主持人是否在发送的时候，
			// 就选择方向与接受人.
			if (Objects.equals(gwf.getHuiQianZhuChiRen(), WebUser.getNo()))
			{
				gwf.setTodoEmps(WebUser.getNo() + "," + WebUser.getName() + ";" + gwf.getTodoEmps());
			}
			else
			{
				gwf.setTodoEmps(gwf.getTodoEmps() + WebUser.getName() + ";");
			}

			gwf.Update();

			return "会签人撤销成功.";
		}

			///#endregion 判断是否是会签状态,是否是会签人做的撤销.

		if (gwf.getFID() != 0)
		{
			//执行子线程的撤销.
			return DoThreadUnSend();
		}

		//定义当前的节点.
		WorkNode wn = this.GetCurrentWorkNode();


			///#region 求的撤销的节点.
		/* 查询出来. */
		sql = "SELECT FK_Node FROM WF_GenerWorkerlist WHERE FK_Emp='" + WebUser.getNo() + "' AND IsPass=1 AND IsEnable=1 AND WorkID=" + this.WorkID + " ORDER BY CDT DESC ";
		int cancelToNodeID = DBAccess.RunSQLReturnValInt(sql, 0); //计算要撤销到的节点.
		if (cancelToNodeID == 0)
		{
			return "err@您没有权限操作该工作.";
		}

		if (nd.getHisCancelRole() == CancelRole.SpecNodes)
		{
			/*指定的节点可以撤销,首先判断当前人员是否有权限.*/
			NodeCancels ncs = new NodeCancels();
			ncs.Retrieve(NodeCancelAttr.FK_Node, wn.getHisNode().getNodeID(), null);
			String cancelNodes = wn.getHisNode().GetParaString("CancelNodes");
			if (ncs.size() == 0 && DataType.IsNullOrEmpty(cancelNodes) == true)
				return "err@流程设计错误, 您设置了当前节点(" + wn.getHisNode().getName() + ")可以让指定的节点人员撤销，但是您没有设置指定的节点.";
			if ((ncs.contains(NodeCancelAttr.CancelTo, cancelToNodeID) == false && (cancelNodes+",").contains(cancelToNodeID+",")==false) && cancelToNodeID != gwf.getNodeID())
				return "err@撤销流程错误,您没有权限执行撤销发送,当前节点不可以执行撤销.";
		}

		if (nd.getHisCancelRole() == CancelRole.OnlyNextStep)
		{
			/*如果仅仅允许撤销上一步骤.*/
			WorkNode wnPri = wn.GetPreviousWorkNode();
			if (wnPri.getHisNode().getNodeID() != cancelToNodeID && cancelToNodeID != gwf.getNodeID())
			{
				return "err@您不能执行撤消发送，因为当前工作不是您发送的或下一步工作已处理。";
			}
		}

		//求出来要撤销到的节点. 
		Node cancelToNode = new Node(cancelToNodeID);

			///#endregion 求的撤销的节点.

		//协作模式下的撤销.
		if (cancelToNodeID == gwf.getNodeID() && cancelToNode.getTodolistModel() == TodolistModel.Teamup)
		{
			gwf.setTodoEmps(gwf.getTodoEmps() + WebUser.getNo() + "," + WebUser.getName() + ";");
			gwf.setTodoEmpsNum(gwf.getTodoEmpsNum() + 1);
			gwf.Update();

			GenerWorkerList gwl = new GenerWorkerList(this.WorkID, cancelToNodeID, WebUser.getNo());
			gwl.setPassInt(0);
			gwl.Update();
			return "@协作模式下,撤销成功.";
		}


		if (this.UnSendToNode != 0 && gwf.getNodeID() != this.UnSendToNode)
		{
			Node toNode = new Node(this.UnSendToNode);
			/* 要撤销的节点是分流节点，并且当前节点不在分流节点而是在合流节点的情况， for:华夏银行.
			* 1, 分流节点发送给n个人.
			* 2, 其中一个人发送到合流节点，另外一个人退回给分流节点。
			* 3，现在分流节点的人接收到一个待办，并且需要撤销整个分流节点的发送.
			* 4, UnSendToNode 这个时间没有值，并且当前干流节点的停留的节点与要撤销到的节点不一致。
			*/
			if (toNode.getHisNodeWorkType() == NodeWorkType.WorkFL && nd.getHisNodeWorkType() == NodeWorkType.WorkHL)
			{
				return DoUnSendInFeiLiuHeiliu(gwf);
			}
		}


			///#region 判断当前节点的模式.
		switch (nd.getHisNodeWorkType())
		{
			case WorkFHL:
				//如果是撤销的节点是断头路的节点.
				if (cancelToNode.getItIsSendBackNode() == true)
				{
					//不需要处理，按照正常的模式处理.
				}
				else
				{
					return this.DoUnSendFeiLiu(gwf);
				}
				break;
			case WorkFL:
			case StartWorkFL:
				break;
			case WorkHL:
				if (this.getItIsMainFlow())
				{
					/* 首先找到与他最近的一个分流点，
					 * 并且判断当前的操作员是不是分流点上的工作人员。*/

					//如果是撤销的节点是断头路的节点.
					if (cancelToNode.getItIsSendBackNode() == true)
					{
						//不需要处理，按照正常的模式处理.
					}
					else
					{
						return this.DoUnSendHeiLiu_Main(gwf);
					}
				}
				else
				{
					return this.DoUnSendSubFlow(gwf); //是子流程时.
				}
				break;
			case SubThreadWork:
				break;
			default:
				break;
		}

			///#endregion 判断当前节点的模式.

		/********** 开始执行撤销. **********************/


			///#region 如果撤销到的节点是普通的节点，并且当前的节点是分流(分流)节点，并且分流(分流)节点已经发送下去了,就不允许撤销了.
		if (cancelToNode.getHisRunModel() == RunModel.Ordinary && nd.getHisRunModel() == RunModel.HL && nd.getHisRunModel() == RunModel.FHL && nd.getHisRunModel() == RunModel.FL)
		{
			/* 检查一下是否还有没有完成的子线程，如果有就抛出不允许撤销的异常。 */
			sql = "SELECT COUNT(*) as NUM FROM WF_GenerWorkerlist WHERE FID=" + this.WorkID + " AND IsPass=0";
			if (DBAccess.RunSQLReturnValInt(sql) != 0)
			{
				return "err@不允许撤销，因为有未完成的子线程.";
			}

			//  return this.DoUnSendHeiLiu_Main(gwf);
		}

			///#endregion 如果撤销到的节点是普通的节点，并且当前的节点是分流节点，并且分流节点已经发送下去了.



			///#region 如果当前是协作组长模式,就要考虑当前是否是会签节点，如果是会签节点，就要处理。
		if (cancelToNode.getTodolistModel() == TodolistModel.TeamupGroupLeader || cancelToNode.getTodolistModel() == TodolistModel.Teamup)
		{
			sql = "SELECT ActionType FROM ND" + Integer.parseInt(this.FlowNo) + "Track WHERE NDFrom=" + cancelToNodeID + " AND EmpFrom='" + WebUser.getNo() + "' AND WorkID=" + this.WorkID + " Order By RDT DESC";
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			for (DataRow dr : dt.Rows)
			{
				int ac = Integer.parseInt(dr.getValue(0).toString());
				ActionType at = ActionType.forValue(ac);
				if (at == ActionType.TeampUp)
				{
					/*如果是写作人员，就不允许他撤销 */
					return "err@您是节点[" + cancelToNode.getName() + "]的会签人，您不能执行撤销。";
				}
				break;
			}
		}

				///#endregion 如果当前是协作组长模式

		//记录撤销前的处理人
		String todoEmps = gwf.getTodoEmps();
		if (DataType.IsNullOrEmpty(todoEmps) == false)
		{
			String[] strs = todoEmps.split("[;]", -1);
			todoEmps = "";
			for (String str : strs)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}
				todoEmps += str.split("[,]", -1)[0] + ",";
			}
		}

		WorkNode wnOfCancelTo = new WorkNode(this.WorkID, cancelToNodeID);

		// 调用撤消发送前事件。
		String msg = ExecEvent.DoNode(EventListNode.UndoneBefore, nd, wn.getHisWork(), null);
		if (msg == null)
		{
			msg = "";
		}


			///#region 删除当前节点数据。
		// 删除产生的工作列表。
		//DeleteSpanNodesGenerWorkerListData();
		GenerWorkerLists wls = new GenerWorkerLists();
		wls.Delete(GenerWorkerListAttr.WorkID, this.WorkID, GenerWorkerListAttr.FK_Node, gwf.getNodeID());


		// 删除附件信息。
		DBAccess.RunSQL("DELETE FROM Sys_FrmAttachmentDB WHERE FK_MapData='ND" + gwf.getNodeID() + "' AND RefPKVal='" + this.WorkID + "'");

			///#endregion 删除当前节点数据。

		// 更新.
		gwf.setNodeID(cancelToNode.getNodeID());
		gwf.setNodeName(cancelToNode.getName());
		//恢复上一步发送人
		DataTable dtPrevTrack = Dev2Interface.Flow_GetPreviousNodeTrack(this.WorkID, cancelToNode.getNodeID());
		if (dtPrevTrack != null && dtPrevTrack.Rows.size() > 0)
		{
			gwf.setSender(dtPrevTrack.Rows.get(0).getValue("EmpFrom").toString() + "," + dtPrevTrack.Rows.get(0).getValue("EmpFromT").toString() + ";");
		}

		if (cancelToNode.getItIsEnableTaskPool() && Glo.isEnableTaskPool())
		{
			gwf.setTaskSta(TaskSta.Takeback);
		}
		else
		{
			gwf.setTaskSta(TaskSta.None);
		}

		gwf.setTodoEmps(WebUser.getNo() + "," + WebUser.getName() + ";");
		gwf.Update();

		if (cancelToNode.getItIsEnableTaskPool() && Glo.isEnableTaskPool())
		{
			//设置全部的人员不可用。
			DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0,  IsEnable=-1 WHERE WorkID=" + this.WorkID + " AND FK_Node=" + gwf.getNodeID());

			//设置当前人员可用。
			DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0,  IsEnable=1  WHERE WorkID=" + this.WorkID + " AND FK_Node=" + gwf.getNodeID() + " AND FK_Emp='" + WebUser.getNo() + "'");
		}
		else
		{
			DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE WorkID=" + this.WorkID + " AND FK_Node=" + gwf.getNodeID() + " AND FK_Emp='" + WebUser.getNo() + "'");
		}

		//更新当前节点，到rpt里面。
		DBAccess.RunSQL("UPDATE " + this.getHisFlow().getPTable() + " SET FlowEndNode=" + gwf.getNodeID() + " WHERE OID=" + this.WorkID);

		// 记录日志..
		wn.AddToTrack(ActionType.UnSend, WebUser.getNo(), WebUser.getName(), cancelToNode.getNodeID(), cancelToNode.getName(), "无");

		//删除审核组件设置"协作模式下操作员显示顺序”为"按照接受人员列表先后顺序(官职大小)"，而生成的待审核轨迹信息
		NodeWorkCheck fwc = new NodeWorkCheck(nd.getNodeID());
		if (fwc.getFWCSta() == FrmWorkCheckSta.Enable && fwc.getFWCOrderModel() == FWCOrderModel.SqlAccepter)
		{
			DBAccess.RunSQL("DELETE FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track WHERE WorkID = " + this.WorkID + " AND ActionType = " + ActionType.WorkCheck.getValue() + " AND NDFrom = " + nd.getNodeID() + " AND NDTo = " + nd.getNodeID() + " AND (Msg = '' OR Msg IS NULL)");
		}

		// 删除数据.
		if (wn.getHisNode().getItIsStartNode())
		{
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkFlow WHERE WorkID=" + this.WorkID);
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + this.WorkID + " AND FK_Node=" + nd.getNodeID());
		}
		else
		{
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + this.WorkID + " AND FK_Node=" + nd.getNodeID());
		}

		//首先删除当前节点的,审核意见. 2020.06.11
		// 如果是断头路节点为了响应计算中心的需求.
		if (nd.getItIsSendBackNode() == false)
		{
			String delTrackSQl = "DELETE FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track WHERE WorkID=" + this.WorkID + " AND NDFrom=" + nd.getNodeID() + " AND ActionType =22 ";
			DBAccess.RunSQL(delTrackSQl);
		}

		if (wn.getHisNode().getItIsEval())
		{
			/*如果是质量考核节点，并且撤销了。*/
			DBAccess.RunSQL("DELETE FROM WF_CHEval WHERE FK_Node=" + wn.getHisNode().getNodeID() + " AND WorkID=" + this.WorkID);
		}


			///#region 恢复工作轨迹，解决工作抢办。
		if (cancelToNode.getItIsStartNode() == false && cancelToNode.getItIsEnableTaskPool() == false)
		{
			WorkNode ppPri = wnOfCancelTo.GetPreviousWorkNode();
			GenerWorkerList wl = new GenerWorkerList();
			wl.Retrieve(GenerWorkerListAttr.FK_Node, wnOfCancelTo.getHisNode().getNodeID(), GenerWorkerListAttr.WorkID, this.WorkID);
			// DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE FK_Node=" + backtoNodeID + " AND WorkID=" + this.WorkID);
			RememberMe rm = new RememberMe();
			rm.Retrieve(RememberMeAttr.FK_Node, wnOfCancelTo.getHisNode().getNodeID(), RememberMeAttr.FK_Emp, ppPri.getHisWork().getRec());

			String[] empStrs = rm.getObjs().split("[@]", -1);
			for (String s : empStrs)
			{
				if (DataType.IsNullOrEmpty(s) == true)
				{
					continue;
				}

				if (Objects.equals(s, wl.getEmpNo()))
				{
					continue;
				}
				GenerWorkerList wlN = new GenerWorkerList();
				wlN.Copy(wl);
				wlN.setEmpNo(s);

				Emp myEmp = new Emp(s);
				wlN.setEmpName(myEmp.getName());
				wlN.setDeptNo(myEmp.getDeptNo());
				wlN.setDeptName(myEmp.getDeptText());

				wlN.Insert();
			}
		}

			///#endregion 恢复工作轨迹，解决工作抢办。


			///#region 如果是开始节点, 检查此流程是否有子流程，如果有则删除它们。
		if (nd.getItIsStartNode())
		{
			/*要检查一个是否有 子流程，如果有，则删除它们。*/
			GenerWorkFlows gwfs = new GenerWorkFlows();
			gwfs.Retrieve(GenerWorkFlowAttr.PWorkID, this.WorkID, null);
			if (gwfs.size() > 0)
			{
				for (GenerWorkFlow item : gwfs.ToJavaList())
				{
					/*删除每个子线程.*/
					bp.wf.Dev2Interface.Flow_DoDeleteFlowByReal(item.getWorkID(), true);
				}
			}
		}

			///#endregion
		String atPara = "@ToNode=" + cancelToNodeID + "@SendToEmpIDs=" + todoEmps;
		//调用撤消发送后事件。
		String nodeMsg = ExecEvent.DoNode(EventListNode.UndoneAfter, nd, wn.getHisWork(), null, atPara);
		if (DataType.IsNullOrEmpty(nodeMsg) == false)
		{
			msg += nodeMsg;
		}
		if (wnOfCancelTo.getHisNode().getItIsStartNode())
		{

			switch (wnOfCancelTo.getHisNode().getHisFormType())
			{
				case FoolForm:
				case Develop:
					return "@撤消执行成功." + msg;
				default:
					return "@撤销成功." + msg;
			}
		}
		else
		{
			// 更新是否显示。
			//  DBAccess.RunSQL("UPDATE WF_ForwardWork SET IsRead=1 WHERE WORKID=" + this.WorkID + " AND FK_Node=" + cancelToNode.getNodeID());
			switch (wnOfCancelTo.getHisNode().getHisFormType())
			{
				case FoolForm:
				case Develop:
					return "@撤消执行成功. " + msg;
				default:
					return "撤销成功:" + msg;
			}
		}
//		return "工作已经被您撤销到:" + cancelToNode.getName();
	}
	/** 
	 撤消分流点
	 1, 把分流节点的人员设置成待办。
	 2，删除所有该分流点发起的子线程。
	 
	 @param gwf
	 @return 
	*/
	private String DoUnSendFeiLiu(GenerWorkFlow gwf) throws Exception {
		//首先要检查，当前的处理人是否是分流节点的处理人？如果是，就要把，未走完的所有子线程都删除掉。
		GenerWorkerList gwl = new GenerWorkerList();
		int i = gwl.Retrieve(GenerWorkerListAttr.WorkID, this.WorkID, GenerWorkerListAttr.FK_Node, gwf.getNodeID(), GenerWorkerListAttr.FK_Emp, WebUser.getNo());
		if (i == 0)
		{
			throw new RuntimeException("@您不能执行撤消发送，因为当前工作不是您发送的。");
		}

		//处理事件.
		Node nd = new Node(gwf.getNodeID());
		Work wk = nd.getHisWork();
		wk.setOID(gwf.getWorkID());
		wk.RetrieveFromDBSources();

		String msg = ExecEvent.DoNode(EventListNode.UndoneBefore, nd, wk, null);

		// 记录日志..
		WorkNode wn = new WorkNode(wk, nd);
		wn.AddToTrack(ActionType.UnSend, WebUser.getNo(), WebUser.getName(), gwf.getNodeID(), gwf.getNodeName(), "");

		//删除上一个节点的数据。
		for (Node ndNext : nd.getHisToNodes().ToJavaList())
		{
			i = DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE FID=" + this.WorkID + " AND FK_Node=" + ndNext.getNodeID());
			if (i == 0)
			{
				continue;
			}

			if (ndNext.getItIsSubThread() == true)
			{
				/*如果到达的节点是子线程,就查询出来发起的子线程。*/
				GenerWorkFlows gwfs = new GenerWorkFlows();
				gwfs.Retrieve(GenerWorkFlowAttr.FID, this.WorkID, null);
				for (GenerWorkFlow en : gwfs.ToJavaList())
				{
					bp.wf.Dev2Interface.Flow_DeleteSubThread(en.getWorkID(), "合流节点撤销发送前，删除子线程.");
				}
				continue;
			}

			// 删除工作记录。
			Works wks = ndNext.getHisWorks();
		}

		//设置当前节点。
		DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE WorkID=" + this.WorkID + " AND FK_Node=" + gwf.getNodeID() + " AND IsPass=1");

		// 设置当前节点的状态.
		Node cNode = new Node(gwf.getNodeID());
		Work cWork = cNode.getHisWork();
		cWork.setOID(this.WorkID);
		msg += ExecEvent.DoNode(EventListNode.UndoneAfter, nd, wk, null);

		return "@撤消执行成功." + msg;
	}
	/** 
	 分合流的撤销发送.
	 
	 @param gwf
	 @return 
	*/
	private String DoUnSendInFeiLiuHeiliu(GenerWorkFlow gwf) throws Exception {
		//首先要检查，当前的处理人是否是分流节点的处理人？如果是，就要把，未走完的所有子线程都删除掉。
		GenerWorkerList gwl = new GenerWorkerList();

		//删除合流节点的处理人.
		gwl.Delete(GenerWorkerListAttr.WorkID, this.WorkID, GenerWorkerListAttr.FK_Node, gwf.getNodeID());

		//查询已经走得分流节点待办.
		int i = gwl.Retrieve(GenerWorkerListAttr.WorkID, this.WorkID, GenerWorkerListAttr.FK_Node, this.UnSendToNode, GenerWorkerListAttr.FK_Emp, WebUser.getNo());
		if (i == 0)
		{
			throw new RuntimeException("@您不能执行撤消发送，因为当前分流工作不是您发送的。");
		}

		// 更新分流节点，让其出现待办.
		gwl.setPassInt(0);
		gwl.setItIsRead(false);
		gwl.setSDT(DataType.getCurrentDateTimess()); //这里计算时间有问题.
		gwl.Update();

		// 把设置当前流程运行到分流流程上.
		gwf.setNodeID(this.UnSendToNode);
		Node nd = new Node(this.UnSendToNode);
		gwf.setNodeName(nd.getName());
		gwf.setSender(WebUser.getNo() + "," + WebUser.getName() + ";");
		gwf.setSendDT(DataType.getCurrentDateTimess());
		gwf.Update();


		Work wk = nd.getHisWork();
		wk.setOID(gwf.getWorkID());
		wk.RetrieveFromDBSources();

		String msg = ExecEvent.DoNode(EventListNode.UndoneBefore, nd, wk, null);

		// 记录日志..
		WorkNode wn = new WorkNode(wk, nd);
		wn.AddToTrack(ActionType.UnSend, WebUser.getNo(), WebUser.getName(), gwf.getNodeID(), gwf.getNodeName(), "");


		//删除上一个节点的数据。
		for (Node ndNext : nd.getHisToNodes().ToJavaList())
		{
			i = DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE FID=" + this.WorkID + " AND FK_Node=" + ndNext.getNodeID());
			if (i == 0)
			{
				continue;
			}

			if (ndNext.getItIsSubThread() == true)
			{
				/*如果到达的节点是子线程,就查询出来发起的子线程。*/
				GenerWorkFlows gwfs = new GenerWorkFlows();
				gwfs.Retrieve(GenerWorkFlowAttr.FID, this.WorkID, null);
				for (GenerWorkFlow en : gwfs.ToJavaList())
				{
					bp.wf.Dev2Interface.Flow_DeleteSubThread(en.getWorkID(), "合流节点撤销发送前，删除子线程.");
				}
				continue;
			}

			// 删除工作记录。
			Works wks = ndNext.getHisWorks();
		}


		// 设置当前节点的状态.
		Node cNode = new Node(gwf.getNodeID());
		Work cWork = cNode.getHisWork();
		cWork.setOID(this.WorkID);
		msg += ExecEvent.DoNode(EventListNode.UndoneAfter, nd, wk, null);
		if (cNode.getItIsStartNode())
		{

			return "@撤消执行成功." + msg;

		}
		else
		{

			return "@撤消执行成功." + msg;

		}
	}
	/** 
	 执行撤销发送
	 
	 @param gwf
	 @return 
	*/
	public final String DoUnSendHeiLiu_Main(GenerWorkFlow gwf) throws Exception {
		Node currNode = new Node(gwf.getNodeID());

		Node priFLNode = currNode.getHisPriFLNode(); //获得上一个节点.
		GenerWorkerList wl = new GenerWorkerList();

		//判断改操作人员是否是分流节点上的人员.
		int i = wl.Retrieve(GenerWorkerListAttr.FK_Node, priFLNode.getNodeID(), GenerWorkerListAttr.FK_Emp, WebUser.getNo());
		if (i == 0)
		{
			return "@不是您把工作发送到当前节点上，所以您不能撤消。";
		}

		WorkNode wn = this.GetCurrentWorkNode();
		WorkNode wnPri = new WorkNode(this.WorkID, priFLNode.getNodeID());

		// 记录日志..
		wnPri.AddToTrack(ActionType.UnSend, WebUser.getNo(), WebUser.getName(), wnPri.getHisNode().getNodeID(), wnPri.getHisNode().getName(), "无");

		//删除当前节点的流程
		DeleteSpanNodesGenerWorkerListData();
		//GenerWorkerLists wls = new GenerWorkerLists();
		//wls.Delete(GenerWorkerListAttr.WorkID, this.WorkID, GenerWorkerListAttr.FK_Node, gwf.getNodeID().ToString());

		//更改流程信息
		gwf.setNodeID(wnPri.getHisNode().getNodeID());
		gwf.setNodeName(wnPri.getHisNode().getName());
		gwf.Update();

		DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE WorkID=" + this.WorkID + " AND FK_Node=" + gwf.getNodeID());

		//删除子线程的功能
		for (Node ndNext : wnPri.getHisNode().getHisToNodes().ToJavaList())
		{
			i = DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE FID=" + this.WorkID + " AND FK_Node=" + ndNext.getNodeID());
			if (i == 0)
			{
				continue;
			}

			if (ndNext.getItIsSubThread() == true)
			{
				/*如果到达的节点是子线程,就查询出来发起的子线程。*/
				GenerWorkFlows gwfs = new GenerWorkFlows();
				gwfs.Retrieve(GenerWorkFlowAttr.FID, this.WorkID, null);
				for (GenerWorkFlow en : gwfs.ToJavaList())
				{
					bp.wf.Dev2Interface.Flow_DeleteSubThread(en.getWorkID(), "合流节点撤销发送前，删除子线程.");
				}
				continue;
			}

			// 删除工作记录。
			Works wks = ndNext.getHisWorks();
		}



			///#region 恢复工作轨迹，解决工作抢办。
		if (wnPri.getHisNode().getItIsStartNode() == false)
		{
			WorkNode ppPri = wnPri.GetPreviousWorkNode();
			wl = new GenerWorkerList();
			wl.Retrieve(GenerWorkerListAttr.FK_Node, wnPri.getHisNode().getNodeID(), GenerWorkerListAttr.WorkID, this.WorkID);
			RememberMe rm = new RememberMe();
			rm.Retrieve(RememberMeAttr.FK_Node, wnPri.getHisNode().getNodeID(), RememberMeAttr.FK_Emp, ppPri.getHisWork().getRec());

			String[] empStrs = rm.getObjs().split("[@]", -1);
			for (String s : empStrs)
			{
				if (DataType.IsNullOrEmpty(s) == true)
				{
					continue;
				}

				if (Objects.equals(s, wl.getEmpNo()))
				{
					continue;
				}
				GenerWorkerList wlN = new GenerWorkerList();
				wlN.Copy(wl);
				wlN.setEmpNo(s);

			   bp.wf.port.WFEmp myEmp = new bp.wf.port.WFEmp(s);
				wlN.setEmpName(myEmp.getName());


				wlN.Insert();
			}
		}

			///#endregion 恢复工作轨迹，解决工作抢办。

		// 删除以前的节点数据.

		if (wnPri.getHisNode().getItIsStartNode())
		{

			if (wnPri.getHisNode().getHisFormType() != NodeFormType.SDKForm)
			{
				return "@撤消执行成功.";
			}
			else
			{
				return "@撤销成功.";
			}
		}
		else
		{

			return "@撤消执行成功.";
		}
	}
	public final String DoUnSendSubFlow(GenerWorkFlow gwf) throws Exception {
		WorkNode wn = this.GetCurrentWorkNode();
		WorkNode wnPri = wn.GetPreviousWorkNode();

		GenerWorkerList wl = new GenerWorkerList();
		int num = wl.Retrieve(GenerWorkerListAttr.FK_Emp, WebUser.getNo(), GenerWorkerListAttr.FK_Node, wnPri.getHisNode().getNodeID());
		if (num == 0)
		{
			return "@您不能执行撤消发送，因为当前工作不是您发送的。";
		}

		// 处理事件。
		String msg = ExecEvent.DoNode(EventListNode.UndoneBefore, wn.getHisNode(), wn.getHisWork(), null);

		// 删除工作者。
		DeleteSpanNodesGenerWorkerListData();

		gwf.setNodeID(wnPri.getHisNode().getNodeID());
		gwf.setNodeName(wnPri.getHisNode().getName());
		gwf.Update();

		DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE WorkID=" + this.WorkID + " AND FK_Node=" + gwf.getNodeID());
		//ShiftWorks fws = new ShiftWorks();
		//fws.Delete(ShiftWorkAttr.FK_Node, wn.getHisNode().getNodeID().ToString(), ShiftWorkAttr.WorkID, this.WorkID.ToString());


			///#region 判断撤消的百分比条件的临界点条件
		if (wn.getHisNode().getPassRate().compareTo(BigDecimal.ZERO) != 0)
		{
			BigDecimal all = new BigDecimal(DBAccess.RunSQLReturnValInt("SELECT COUNT(*) NUM FROM WF_GenerWorkerlist WHERE FID=" + this.FID + " AND FK_Node=" + wnPri.getHisNode().getNodeID()));
			BigDecimal ok = new BigDecimal(DBAccess.RunSQLReturnValInt("SELECT COUNT(*) NUM FROM WF_GenerWorkerlist WHERE FID=" + this.FID + " AND IsPass=1 AND FK_Node=" + wnPri.getHisNode().getNodeID()));
			BigDecimal rate = ok.divide(all.multiply(BigDecimal.valueOf(100)));
			if (wn.getHisNode().getPassRate().compareTo(rate) <= 0)
			{
				DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE FK_Node=" + wn.getHisNode().getNodeID() + " AND WorkID=" + this.FID);
			}
			else
			{
				DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=3 WHERE FK_Node=" + wn.getHisNode().getNodeID() + " AND WorkID=" + this.FID);
			}
		}

			///#endregion

		// 处理事件。
		msg += ExecEvent.DoNode(EventListNode.UndoneAfter, wn.getHisNode(), wn.getHisWork(), null);

		// 记录日志..
		wn.AddToTrack(ActionType.UnSend, WebUser.getNo(), WebUser.getName(), wn.getHisNode().getNodeID(), wn.getHisNode().getName(), "无");



		return "@撤消执行成功." + msg;


	}


	/** 
	 删除两个节点之间的业务数据与流程引擎控制数据.
	*/
	private void DeleteSpanNodesGenerWorkerListData() throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(this.WorkID);

		Node unSendNode = new Node(this.UnSendToNode);
		Paras ps = new Paras();
		String dbStr = SystemConfig.getAppCenterDBVarStr();

		// 删除FH, 不管是否有这笔数据.
		ps.clear();

		/*撤销到某个节点，就需要清除 两个节点之间的数据, 包括WF_GenerWorkerlist的数据.*/
		if (unSendNode.getItIsStartNode() == true)
		{
			// 删除其子线程流程.
			ps.clear();
			ps.SQL = "DELETE FROM WF_GenerWorkFlow WHERE FID=" + dbStr + "FID ";
			ps.Add("FID", this.WorkID);
			DBAccess.RunSQL(ps);

			/*如果撤销到了开始的节点，就删除出开始节点以外的数据，不要删除节点表单数据，这样会导致流程轨迹打不开.*/
			ps.clear();
			ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE FK_Node!=" + dbStr + "FK_Node AND (WorkID=" + dbStr + "WorkID1 OR FID=" + dbStr + "WorkID2)";
			ps.Add(GenerWorkerListAttr.FK_Node, unSendNode.getNodeID());
			ps.Add("WorkID1", this.WorkID);
			ps.Add("WorkID2", this.WorkID);
			DBAccess.RunSQL(ps);
			return;
		}

		/*找到撤销到的节点，把从这个时间点以来的数据都要删除掉.*/
		ps.clear();
		ps.SQL = "SELECT RDT,ActionType,NDFrom FROM ND" + Integer.parseInt(gwf.getFlowNo()) + "Track WHERE  NDFrom=" + dbStr + "NDFrom AND WorkID=" + dbStr + "WorkID AND ActionType=" + ActionType.Forward.getValue() + " ORDER BY RDT desc ";
		ps.Add("NDFrom", unSendNode.getNodeID());
		ps.Add("WorkID", this.WorkID);
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		if (dt.Rows.size() >= 1)
		{
			String rdt = dt.Rows.get(0).getValue(0).toString();

			ps.clear();
			ps.SQL = "SELECT ActionType,NDFrom FROM ND" + Integer.parseInt(gwf.getFlowNo()) + "Track WHERE   RDT >=" + dbStr + "RDT AND WorkID=" + dbStr + "WorkID ORDER BY RDT ";
			ps.Add("RDT", rdt, false);
			ps.Add("WorkID", this.WorkID);
			dt = DBAccess.RunSQLReturnTable(ps);

			for (DataRow dr : dt.Rows)
			{
				ActionType at = ActionType.forValue(Integer.parseInt(dr.getValue("ActionType").toString()));
				int nodeid = Integer.parseInt(dr.getValue("NDFrom").toString());
				if (nodeid == unSendNode.getNodeID())
				{
					continue;
				}

				//删除中间的节点.
				ps.clear();
				ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE FK_Node=" + dbStr + "FK_Node AND (WorkID=" + dbStr + "WorkID1 OR FID=" + dbStr + "WorkID2) ";
				ps.Add("FK_Node", nodeid);
				ps.Add("WorkID1", this.WorkID);
				ps.Add("WorkID2", this.WorkID);
				DBAccess.RunSQL(ps);

				//删除审核意见
				ps.clear();
				ps.SQL = "DELETE FROM ND" + Integer.parseInt(unSendNode.getFlowNo()) + "Track WHERE NDFrom=" + dbStr + "NDFrom AND  (WorkID=" + dbStr + "WorkID1 OR FID=" + dbStr + "WorkID2) AND ActionType=22";
				ps.Add("NDFrom", nodeid);
				ps.Add("WorkID1", this.WorkID);
				ps.Add("WorkID2", this.WorkID);
				DBAccess.RunSQL(ps);
			}
		}


		//删除当前节点的数据.
		ps.clear();
		ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE FK_Node=" + dbStr + "FK_Node AND (WorkID=" + dbStr + "WorkID1 OR FID=" + dbStr + "WorkID2) ";
		ps.Add("FK_Node", gwf.getNodeID());
		ps.Add("WorkID1", this.WorkID);
		ps.Add("WorkID2", this.WorkID);
		DBAccess.RunSQL(ps);

	}
}
