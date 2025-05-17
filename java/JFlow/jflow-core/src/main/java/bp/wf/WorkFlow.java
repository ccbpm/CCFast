package bp.wf;

import bp.en.*; import bp.en.Map;
import bp.web.*;
import bp.da.*;
import bp.port.*;
import bp.sys.*;
import bp.wf.template.*;
import bp.wf.data.*;
import bp.difference.*;
import bp.*;
import java.util.*;
import java.time.*;
import java.math.*;

/**
 WF 的摘要说明。
 工作流
 这里包含了两个方面
 工作的信息．
 流程的信息．
*/
public class WorkFlow
{

		///#region 当前工作统计信息
	/**
	 正常范围的运行的个数。
	*/
	public static int NumOfRuning(String FK_Emp)
	{
		String sql = "SELECT COUNT(*) FROM V_WF_CURRWROKS WHERE FK_Emp='" + FK_Emp + "' AND WorkTimeState=0";
		return DBAccess.RunSQLReturnValInt(sql);
	}
	/**
	 进入警告期限的个数
	*/
	public static int NumOfAlert(String FK_Emp)
	{
		String sql = "SELECT COUNT(*) FROM V_WF_CURRWROKS WHERE FK_Emp='" + FK_Emp + "' AND WorkTimeState=1";
		return DBAccess.RunSQLReturnValInt(sql);
	}
	/**
	 逾期
	*/
	public static int NumOfTimeout(String FK_Emp)
	{
		String sql = "SELECT COUNT(*) FROM V_WF_CURRWROKS WHERE FK_Emp='" + FK_Emp + "' AND WorkTimeState=2";
		return DBAccess.RunSQLReturnValInt(sql);
	}

		///#endregion


		///#region  权限管理
	/**
	 是不是能够作当前的工作。

	 @param empId 工作人员ID
	 @return 是不是能够作当前的工作
	*/
	public final boolean ItIsCanDoCurrentWork(String empId) throws Exception {
		WorkNode wn = this.GetCurrentWorkNode();
		return bp.wf.Dev2Interface.Flow_IsCanDoCurrentWork(wn.getWorkID(), empId);

			///#region 使用dev2InterFace 中的算法
		//return true;
		// 找到当前的工作节点

		// 判断是不是开始工作节点..
		/*if(wn.getHisNode().getItIsStartNode())
		{
			// 从物理上判断是不是有这个权限。
			// return WorkFlow.IsCanDoWorkCheckByEmpStation(wn.Hisnode.getNodeID(), empId);
			return true;
		}

		// 判断他的工作生成的工作者.
		GenerWorkerLists gwls = new GenerWorkerLists(this.getWorkID(), wn.getHisNode().getNodeID());
		if (gwls.size() == 0)
		{
			//return true;
			//throw new Exception("@工作流程定义错误,没有找到能够执行此项工作的人员.相关信息:工作ID="+this.WorkID+",节点ID="+wn.Hisnode.getNodeID() );
			throw new RuntimeException("@工作流程定义错误,没有找到能够执行此项工作的人员.相关信息:WorkID=" + this.getWorkID() + ",NodeID=" + wn.getHisNode().getNodeID());
		}

		for (GenerWorkerList en : gwls.ToJavaList())
		{
			if (Objects.equals(en.getEmpNo(), empId))
			{
				return true;
			}
		}
		return false;*/

			///#endregion
	}

		///#endregion


		///#region 流程公共方法
	/**
	 执行驳回
	 应用场景:子流程向分合点驳回时

	 @param fid
	 @param fk_node 被驳回的节点
	 @param msg
	 @return
	*/
	public final String DoHungupReject(long fid, int fk_node, String msg) throws Exception {
		GenerWorkerList wl = new GenerWorkerList();
		int i = wl.Retrieve(GenerWorkerListAttr.FID, fid, GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.FK_Node, fk_node);

		//if (i == 0)
		//    throw new Exception("系统错误，没有找到应该找到的数据。");

		i = wl.Delete();
		//if (i == 0)
		//    throw new Exception("系统错误，没有删除应该删除的数据。");

		wl = new GenerWorkerList();
		i = wl.Retrieve(GenerWorkerListAttr.FID, fid, GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.IsPass, 3);

		//if (i == 0)
		//    throw new Exception("系统错误，想找到退回的原始起点没有找到。");

		Node nd = new Node(fk_node);
		// 更新当前流程管理表的设置当前的节点。
		DBAccess.RunSQL("UPDATE WF_GenerWorkFlow SET FK_Node=" + fk_node + ", NodeName='" + nd.getName() + "' WHERE WorkID=" + this.getWorkID());

		wl.setItIsPass(false);
		wl.Update();

		return "工作已经驳回到(" + wl.getEmpNo() + " , " + wl.getEmpName() + ")";
		// wl.HisNode
	}
	/**
	 逻辑删除流程

	 @param msg 逻辑删除流程原因，可以为空。
	*/
	public final void DoDeleteWorkFlowByFlag(String msg)
	{
		try
		{
			GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());

			bp.wf.Node nd = new Node(gwf.getNodeID());
			Work wk = nd.getHisWork();
			wk.setOID(this.getWorkID());
			wk.RetrieveFromDBSources();

			//定义workNode.
			WorkNode wn = new WorkNode(wk, nd);

			//调用结束前事件.
			ExecEvent.DoFlow(EventListFlow.BeforeFlowDel, wn, null);

			//记录日志 感谢 itdos and 888 , 提出了这个问题..
			wn.AddToTrack(ActionType.DeleteFlowByFlag, WebUser.getNo(), WebUser.getName(), wn.getHisNode().getNodeID(), wn.getHisNode().getName(), msg);

			//更新-流程数据表的状态.
			String sql = "UPDATE  " + this.getHisFlow().getPTable() + " SET WFState=" + WFState.Delete.getValue() + " WHERE OID=" + this.getWorkID();
			DBAccess.RunSQL(sql);

			//删除他的工作者，不让其有待办.
			sql = "DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + this.getWorkID();
			DBAccess.RunSQL(sql);

			//设置产生的工作流程为.
			gwf.setWFState(bp.wf.WFState.Delete);
			gwf.Update();

			//调用结束后事件.
			ExecEvent.DoFlow(EventListFlow.AfterFlowDel, wn, null);

		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteError("@逻辑删除出现错误:" + ex.getMessage());
			throw new RuntimeException("@逻辑删除出现错误:" + ex.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 恢复逻辑删除流程

	 @param msg 回复原因,可以为空.
	*/
	public final void DoUnDeleteWorkFlowByFlag(String msg)
	{
		try
		{
			DBAccess.RunSQL("UPDATE WF_GenerWorkFlow SET WFState=" + WFState.Runing.getValue() + " WHERE  WorkID=" + this.getWorkID());

			//设置产生的工作流程为.
			GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());

			//回复数据.
			bp.wf.Dev2Interface.Flow_DoRebackWorkFlow(gwf.getFlowNo(), gwf.getWorkID(), gwf.getNodeID(), msg);

			WorkNode wn = new WorkNode(getWorkID(), gwf.getNodeID());
			wn.AddToTrack(ActionType.UnDeleteFlowByFlag, WebUser.getNo(), WebUser.getName(), wn.getHisNode().getNodeID(), wn.getHisNode().getName(), msg);
		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteError("@逻辑删除出现错误:" + ex.getMessage());
			throw new RuntimeException("@逻辑删除出现错误:" + ex.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 删除已经完成的流程

	 @param flowNo 流程编号
	 @param workID 工作ID
	 @param isDelSubFlow 是否要删除子流程
	 @param note 删除原因
	 @return 删除信息
	*/
	public static String DoDeleteWorkFlowAlreadyComplete(String flowNo, long workID, boolean isDelSubFlow, String note) throws Exception {
		Log.DebugWriteInfo("开始删除流程:流程编号:" + flowNo + "-WorkID:" + workID + "-" + ". 是否要删除子流程:" + isDelSubFlow + ";删除原因:" + note);

		Flow fl = new Flow(flowNo);


			///#region 记录流程删除日志
		GERpt rpt = new GERpt("ND" + Integer.parseInt(flowNo) + "Rpt");
		rpt.SetValByKey(GERptAttr.OID, workID);
		rpt.Retrieve();
		WorkFlowDeleteLog log = new WorkFlowDeleteLog();
		log.setOID(workID);
		try
		{
			log.Copy(rpt);
			log.setDeleteDT(DataType.getCurrentDateTime());
			log.setOperDept(WebUser.getDeptNo());
			log.setOperDeptName(WebUser.getDeptName());
			log.setOper(WebUser.getNo());
			log.setDeleteNote(note);
			log.setOID(workID);
			log.setFlowNo(flowNo);
			log.setFlowSortNo(fl.getFlowSortNo());
			log.InsertAsOID(log.getOID());
		}
		catch (RuntimeException ex)
		{
			log.CheckPhysicsTable();
			log.Delete();
			return ex.getStackTrace().toString();
		}

			///#endregion 记录流程删除日志

		DBAccess.RunSQL("DELETE FROM ND" + Integer.parseInt(flowNo) + "Track WHERE WorkID=" + workID);
		DBAccess.RunSQL("DELETE FROM " + fl.getPTable() + " WHERE OID=" + workID);
		DBAccess.RunSQL("DELETE FROM WF_CHEval WHERE  WorkID=" + workID); // 删除质量考核数据。

		String info = "";


			///#region 正常的删除信息.
		String msg = "";
		try
		{
			// 删除单据信息.
			DBAccess.RunSQL("DELETE FROM WF_CCList WHERE WorkID=" + workID);

			// 删除移交.
			// DBAccess.RunSQL("DELETE FROM WF_ForwardWork WHERE WorkID=" + workID);

			//删除它的工作.
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkFlow WHERE (WorkID=" + workID + " OR FID=" + workID + " ) AND FK_Flow='" + flowNo + "'");
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE (WorkID=" + workID + " OR FID=" + workID + " ) AND FK_Flow='" + flowNo + "'");

			//删除所有节点上的数据.
			Nodes nds = fl.getHisNodes();
			for (Node nd : nds.ToJavaList())
			{
				try
				{
					DBAccess.RunSQL("DELETE FROM ND" + nd.getNodeID() + " WHERE OID=" + workID + " OR FID=" + workID);
				}
				catch (RuntimeException ex)
				{
					msg += "@ delete data error " + ex.getMessage();
				}
			}
			if (!Objects.equals(msg, ""))
			{
				Log.DebugWriteInfo(msg);
			}
		}
		catch (RuntimeException ex)
		{
			String err = "@删除工作流程 Err " + ex.getMessage();
			Log.DebugWriteError(err);
			throw new RuntimeException(err);
		}
		info = "@删除流程删除成功";

			///#endregion 正常的删除信息.


			///#region 删除该流程下面的子流程.
		if (isDelSubFlow)
		{
			GenerWorkFlows gwfs = new GenerWorkFlows();
			gwfs.Retrieve(GenerWorkFlowAttr.PWorkID, workID, null);
			for (GenerWorkFlow item : gwfs.ToJavaList())
			{
				bp.wf.Dev2Interface.Flow_DoDeleteFlowByReal(item.getWorkID(), true);
			}
		}

			///#endregion 删除该流程下面的子流程.

		Log.DebugWriteInfo("@[" + fl.getName() + "]流程被[" + WebUser.getNo() + WebUser.getName() + "]删除，WorkID[" + workID + "]。");
		return "已经完成的流程被您删除成功.";
	}
	/**
	 执行驳回
	 应用场景:子流程向分合点驳回时

	 @param fid
	 @param fk_node 被驳回的节点
	 @param msg
	 @return
	*/
	public final String DoReject(long fid, int fk_node, String msg) throws Exception {
		GenerWorkerList wl = new GenerWorkerList();
		int i = wl.Retrieve(GenerWorkerListAttr.FID, fid, GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.FK_Node, fk_node);

		//if (i == 0)
		//    throw new Exception("系统错误，没有找到应该找到的数据。");

		i = wl.Delete();
		//if (i == 0)
		//    throw new Exception("系统错误，没有删除应该删除的数据。");

		wl = new GenerWorkerList();
		i = wl.Retrieve(GenerWorkerListAttr.FID, fid, GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.IsPass, 3);

		//if (i == 0)
		//    throw new Exception("系统错误，想找到退回的原始起点没有找到。");

		Node nd = new Node(fk_node);
		// 更新当前流程管理表的设置当前的节点。
		DBAccess.RunSQL("UPDATE WF_GenerWorkFlow SET FK_Node=" + fk_node + ", NodeName='" + nd.getName() + "' WHERE WorkID=" + this.getWorkID());

		wl.setItIsPass(false);
		wl.Update();

		return "工作已经驳回到(" + wl.getEmpNo() + " , " + wl.getEmpName() + ")";
		// wl.HisNode
	}
	/**
	 删除子线程

	 @return 返回删除结果.
	*/
	private String DoDeleteSubThread() throws Exception {
		WorkNode wn = this.GetCurrentWorkNode();
		Emp empOfWorker = new Emp(WebUser.getNo());


			///#region 正常的删除信息.
		String msg = "";
		try
		{
			long workId = this.getWorkID();
			String flowNo = this.getHisFlow().getNo();
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("获取流程的 ID 与流程编号 出现错误。" + ex.getMessage());
		}

		try
		{
			// 删除质量考核信息.
			DBAccess.RunSQL("DELETE FROM WF_CHEval WHERE WorkID=" + this.getWorkID()); // 删除质量考核数据。

			// 删除抄送信息.
			DBAccess.RunSQL("DELETE FROM WF_CCList WHERE WorkID=" + this.getWorkID());

			// 删除移交.
			// DBAccess.RunSQL("DELETE FROM WF_ForwardWork WHERE WorkID=" + this.WorkID);

			//删除它的工作.
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkFlow WHERE (WorkID=" + this.getWorkID() + " ) AND FK_Flow='" + this.getHisFlow().getNo() + "'");
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE (WorkID=" + this.getWorkID() + " ) AND FK_Flow='" + this.getHisFlow().getNo() + "'");

			if (!Objects.equals(msg, ""))
			{
				Log.DebugWriteInfo(msg);
			}
		}
		catch (RuntimeException ex)
		{
			String err = "@删除工作流程[" + this.getHisGenerWorkFlow().getWorkID() + "," + this.getHisGenerWorkFlow().getTitle() + "] Err " + ex.getMessage();
			Log.DebugWriteError(err);
			throw new RuntimeException(err);
		}
		String info = "@删除流程删除成功";

			///#endregion 正常的删除信息.


			///#region 处理分流程删除的问题完成率的问题。
		if (1 == 2)
		{
			/* 目前还没有必要，因为在分流点,才有计算完成率的需求. */
			String sql = "";
			/*
			 * 取出来获取停留点,没有获取到说明没有任何子线程到达合流点的位置.
			 */
			sql = "SELECT FK_Node FROM WF_GenerWorkerlist WHERE WorkID=" + this.getFID() + " AND IsPass=3";
			int fk_node = DBAccess.RunSQLReturnValInt(sql, 0);
			if (fk_node != 0)
			{
				/* 说明它是待命的状态 */
				Node nextNode = new Node(fk_node);
				if (nextNode.getPassRate().compareTo(BigDecimal.ZERO) > 0)
				{
					/* 找到等待处理节点的上一个点 */
					Nodes priNodes = nextNode.getFromNodes();
					if (priNodes.size() != 1)
					{
						throw new RuntimeException("@没有实现子流程不同线程的需求。");
					}

					Node priNode = (Node)priNodes.get(0);


						///#region 处理完成率
					sql = "SELECT COUNT(*) AS Num FROM WF_GenerWorkerlist WHERE FK_Node=" + priNode.getNodeID() + " AND FID=" + this.getFID() + " AND IsPass=1";
					BigDecimal ok = new BigDecimal(DBAccess.RunSQLReturnValInt(sql));
					sql = "SELECT COUNT(*) AS Num FROM WF_GenerWorkerlist WHERE FK_Node=" + priNode.getNodeID() + " AND FID=" + this.getFID();
					BigDecimal all = new BigDecimal(DBAccess.RunSQLReturnValInt(sql));
					if (all.compareTo(BigDecimal.ZERO) == 0)
					{
						/*说明:所有的子线程都被杀掉了, 就应该整个流程结束。*/
						WorkFlow wf = new WorkFlow(this.getHisFlow(), this.getFID());
						info += "@所有的子线程已经结束。";
						info += "@结束主流程信息。";
						info += "@" + wf.DoFlowOver(ActionType.FlowOver, "合流点流程结束", null, null, WFState.CompleteEnd);
					}

					BigDecimal passRate = ok.divide(all).multiply(new BigDecimal("100"));
					if (nextNode.getPassRate().compareTo(passRate) <= 0)
					{
						/*说明全部的人员都完成了，就让合流点显示它。*/
						DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0  WHERE IsPass=3  AND WorkID=" + this.getFID() + " AND FK_Node=" + fk_node);
					}

						///#endregion 处理完成率
				}
			} // 结束有待命的状态判断。

			if (fk_node == 0)
			{
				/* 说明:没有找到等待启动工作的合流节点. */
				GenerWorkFlow gwf = new GenerWorkFlow(this.getFID());
				Node fND = new Node(gwf.getNodeID());
				switch (fND.getHisNodeWorkType())
				{
					case WorkHL: //主流程运行到合流点上了
						break;
					default:
						/*** 解决删除最后一个子流程时要把干流程也要删除。*/
						//sql = "SELECT COUNT(*) AS Num FROM WF_GenerWorkerlist WHERE FK_Node=" +this.HisGenerWorkFlow +" AND FID=" + this.FID;
						//int num = DBAccess.RunSQLReturnValInt(sql);
						//if (num == 0)
						//{
						//    /*说明没有子进程，就要把这个流程执行完成。*/
						//    WorkFlow wf = new WorkFlow(this.HisFlow, this.FID);
						//    info += "@所有的子线程已经结束。";
						//    info += "@结束主流程信息。";
						//    info += "@" + wf.DoFlowOver(ActionType.FlowOver, "主流程结束");
						//}
						break;
				}
			}
		}

			///#endregion


			///#region 写入删除日志.
		wn.AddToTrack(ActionType.DeleteSubThread, empOfWorker.getUserID(), empOfWorker.getName(), wn.getHisNode().getNodeID(), wn.getHisNode().getName(), "子线程被:" + WebUser.getName() + "删除.");

			///#endregion 写入删除日志.

		return "子线程被删除成功.";
	}
	/**
	 删除已经完成的流程

	 @param workid 工作ID
	 @param isDelSubFlow 是否删除子流程
	 @return 删除错误会抛出异常
	*/
	public static void DeleteFlowByReal(long workid, boolean isDelSubFlow) throws Exception {
		//检查流程是否完成，如果没有完成就调用workflow流程删除.
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workid);
		int i = gwf.RetrieveFromDBSources();
		if (i == 0)
		{
			throw new RuntimeException("err@错误：该流程应不存在");
		}

		bp.wf.Flow fl = new Flow(gwf.getFlowNo());
		String toEmps = gwf.getEmps().replace('@', ','); //流程的所有处理人
		if (i != 0)
		{
			if (gwf.getIsOver()==false)
			{
				WorkFlow wf = new WorkFlow(workid);
				//发送退回消息
				PushMsgs pms1 = new PushMsgs();
				pms1.Retrieve(PushMsgAttr.NodeID, gwf.getNodeID(), PushMsgAttr.EventNo, EventListFlow.AfterFlowDel, null);
				Node node = new Node(gwf.getNodeID());
				for (PushMsg pm : pms1.ToJavaList())
				{
					Work work = node.getHisWork();
					work.setOID(gwf.getWorkID());
					work.setNodeID(node.getNodeID());
					work.SetValByKey("FK_Dept", WebUser.getDeptNo());
					pm.DoSendMessage(node, work, null, null, null, toEmps);
				}

				wf.DoDeleteWorkFlowByReal(isDelSubFlow);
				return;
			}
		}


			///#region 删除独立表单的数据.
		FrmNodes fns = new FrmNodes();
		fns.Retrieve(FrmNodeAttr.FK_Flow, gwf.getFlowNo(), null);
		String strs = "";
		for (FrmNode frmNode : fns.ToJavaList())
		{
			if (strs.contains("@" + frmNode.getFrmID()) == true)
			{
				continue;
			}

			strs += "@" + frmNode.getFrmID() + "@";
			try
			{
				MapData md = new MapData(frmNode.getFrmID());
				DBAccess.RunSQL("DELETE FROM " + md.getPTable() + " WHERE OID=" + workid);
			}
			catch (java.lang.Exception e)
			{

			}
		}

			///#endregion 删除独立表单的数据.

		//删除流程数据.
		DBAccess.RunSQL("DELETE FROM ND" + Integer.parseInt(gwf.getFlowNo()) + "Track WHERE WorkID=" + workid);
		DBAccess.RunSQL("DELETE FROM " + fl.getPTable() + " WHERE OID=" + workid);
		DBAccess.RunSQL("DELETE FROM WF_CHEval WHERE  WorkID=" + workid); // 删除质量考核数据。


			///#region 正常的删除信息.
		Log.DebugWriteInfo("@[" + fl.getName() + "]流程被[" + WebUser.getNo() + WebUser.getName() + "]删除，WorkID[" + workid + "]。");
		String msg = "";

		// 删除单据信息.
		DBAccess.RunSQL("DELETE FROM WF_CCList WHERE WorkID=" + workid);

		//发送退回消息
		PushMsgs pms = new PushMsgs();
		pms.Retrieve(PushMsgAttr.NodeID, gwf.getNodeID(), PushMsgAttr.EventNo, EventListFlow.AfterFlowDel, null);
		Node pnd = new Node(gwf.getNodeID());
		for (PushMsg pm : pms.ToJavaList())
		{
			Work work = pnd.getHisWork();
			work.setOID(gwf.getWorkID());
			work.setNodeID(pnd.getNodeID());
			work.SetValByKey("FK_Dept", WebUser.getDeptNo());

			pm.DoSendMessage(pnd, work, null, null, null, toEmps);
		}

		//删除它的工作.
		DBAccess.RunSQL("DELETE FROM WF_GenerWorkFlow WHERE (WorkID=" + workid + " OR FID=" + workid + " ) AND FK_Flow='" + gwf.getFlowNo() + "'");
		DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE (WorkID=" + workid + " OR FID=" + workid + " ) AND FK_Flow='" + gwf.getFlowNo() + "'");

		//删除所有节点上的数据.
		Nodes nodes = new Nodes(gwf.getFlowNo()); // this.HisFlow.HisNodes;
		for (Node node : nodes.ToJavaList())
		{
			try
			{
				if (DBAccess.IsExitsObject("ND" + node.getNodeID()) == false)
				{
					continue;
				}

				DBAccess.RunSQL("DELETE FROM ND" + node.getNodeID() + " WHERE OID=" + workid + " OR FID=" + workid);
			}
			catch (RuntimeException ex)
			{
				msg += "@ delete data error " + ex.getMessage();
			}

			MapDtls dtls = new MapDtls("ND" + node.getNodeID());
			for (MapDtl dtl : dtls.ToJavaList())
			{
				try
				{
					DBAccess.RunSQL("DELETE FROM " + dtl.getPTable() + " WHERE RefPK = " + workid );
				}
				catch (java.lang.Exception e2)
				{
				}
			}
		}

		MapDtls mydtls = new MapDtls("ND" + Integer.parseInt(gwf.getFlowNo()) + "Rpt");
		for (MapDtl dtl : mydtls.ToJavaList())
		{
			try
			{
				DBAccess.RunSQL("DELETE FROM " + dtl.getPTable() + " WHERE RefPK = " + workid );
			}
			catch (java.lang.Exception e3)
			{
			}
		}

		if (!Objects.equals(msg, ""))
		{
			Log.DebugWriteInfo(msg);
		}


			///#endregion 正常的删除信息.
	}
	/**
	 删除子线程

	 @return 删除的消息
	*/
	public final String DoDeleteSubThread2015() throws Exception {
		if (this.getFID() == 0)
		{
			throw new RuntimeException("@该流程非子线程流程实例，不能执行该方法。");
		}


			///#region 正常的删除信息.
		String msg = "";
		try
		{
			long workId = this.getWorkID();
			String flowNo = this.getHisFlow().getNo();
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("获取流程的 ID 与流程编号 出现错误。" + ex.getMessage());
		}

		try
		{
			// 删除质量考核信息.
			DBAccess.RunSQL("DELETE FROM WF_CHEval WHERE WorkID=" + this.getWorkID()); // 删除质量考核数据。

			// 删除抄送信息.
			DBAccess.RunSQL("DELETE FROM WF_CCList WHERE WorkID=" + this.getWorkID());

			// 删除移交.
			// DBAccess.RunSQL("DELETE FROM WF_ForwardWork WHERE WorkID=" + this.WorkID);

			//删除它的工作.
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkFlow WHERE WorkID=" + this.getWorkID());
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + this.getWorkID());

			if (!Objects.equals(msg, ""))
			{
				Log.DebugWriteInfo(msg);
			}
		}
		catch (RuntimeException ex)
		{
			String err = "@删除工作流程[" + this.getHisGenerWorkFlow().getWorkID() + "," + this.getHisGenerWorkFlow().getTitle() + "] Err " + ex.getMessage();
			Log.DebugWriteError(err);
			throw new RuntimeException(err);
		}
		String info = "@删除流程删除成功";

			///#endregion 正常的删除信息.


			///#region 处理分流程删除的问题完成率的问题。
		if (1 == 2)
		{
			/*
			 * 开发说明：
			 * 1，当前是删除子线程操作,当前的节点就是子线程节点.
			 * 2, 删除子线程的动作，1，合流点。2，分流点。
			 * 3，这里要解决合流节点的完成率的问题.
			 */


///#warning 应该删除一个子线程后，就需要计算完成率的问题。但是目前应用到该场景极少,因为。能够看到河流点信息，说明已经到达了完成率了。

			/* 目前还没有必要，因为在分流点,才有计算完成率的需求. */
			String sql = "";
			/*
			 * 取出来获取停留点,没有获取到说明没有任何子线程到达合流点的位置.
			 */

			sql = "SELECT FK_Node FROM WF_GenerWorkerlist WHERE WorkID=" + this.getFID() + " AND IsPass=3";
			int fk_node = DBAccess.RunSQLReturnValInt(sql, 0);
			if (fk_node != 0)
			{
				/* 说明它是待命的状态 */
				Node nextNode = new Node(fk_node);
				if (nextNode.getPassRate().compareTo(BigDecimal.ZERO) > 0)
				{
					/* 找到等待处理节点的上一个点 */
					Nodes priNodes = nextNode.getFromNodes();
					if (priNodes.size() != 1)
					{
						throw new RuntimeException("@没有实现子流程不同线程的需求。");
					}

					Node priNode = (Node)priNodes.get(0);


						///#region 处理完成率
					sql = "SELECT COUNT(*) AS Num FROM WF_GenerWorkerlist WHERE FK_Node=" + priNode.getNodeID() + " AND FID=" + this.getFID() + " AND IsPass=1";
					BigDecimal ok = new BigDecimal(DBAccess.RunSQLReturnValInt(sql));
					sql = "SELECT COUNT(*) AS Num FROM WF_GenerWorkerlist WHERE FK_Node=" + priNode.getNodeID() + " AND FID=" + this.getFID();
					BigDecimal all = new BigDecimal(DBAccess.RunSQLReturnValInt(sql));
					if (all.compareTo(BigDecimal.ZERO) == 0)
					{
						/*说明:所有的子线程都被杀掉了, 就应该整个流程结束。*/
						WorkFlow wf = new WorkFlow(this.getHisFlow(), this.getFID());
						info += "@所有的子线程已经结束。";
						info += "@结束主流程信息。";
						info += "@" + wf.DoFlowOver(ActionType.FlowOver, "合流点流程结束", null, null, WFState.CompleteEnd);
					}

					BigDecimal passRate = ok.divide(all.multiply(BigDecimal.valueOf(100)));
					if (nextNode.getPassRate().compareTo(passRate) <= 0)
					{
						/* 说明: 全部的人员都完成了，就让合流点显示它。*/
						DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0  WHERE IsPass=3  AND WorkID=" + this.getFID() + " AND FK_Node=" + fk_node);
					}

						///#endregion 处理完成率
				}
			} // 结束有待命的状态判断。

			if (fk_node == 0)
			{
				/* 说明:没有找到等待启动工作的合流节点. */
				GenerWorkFlow gwf = new GenerWorkFlow(this.getFID());
				Node fND = new Node(gwf.getNodeID());
				switch (fND.getHisNodeWorkType())
				{
					case WorkHL: //主流程运行到合流点上了
						break;
					default:
						/*** 解决删除最后一个子流程时要把干流程也要删除。*/
						//sql = "SELECT COUNT(*) AS Num FROM WF_GenerWorkerlist WHERE FK_Node=" +this.HisGenerWorkFlow +" AND FID=" + this.FID;
						//int num = DBAccess.RunSQLReturnValInt(sql);
						//if (num == 0)
						//{
						//    /*说明没有子进程，就要把这个流程执行完成。*/
						//    WorkFlow wf = new WorkFlow(this.HisFlow, this.FID);
						//    info += "@所有的子线程已经结束。";
						//    info += "@结束主流程信息。";
						//    info += "@" + wf.DoFlowOver(ActionType.FlowOver, "主流程结束");
						//}
						break;
				}
			}
		}

			///#endregion



		//检查是否是最后一个子线程被删除了？如果是，就需要当分流节点产生待办.
		GenerWorkFlow gwfMain = new GenerWorkFlow(this.getFID());

		/*说明仅仅停留在分流节点,还没有到合流节点上去.
		 * 删除子线程的时候，判断是否是最后一个子线程,如果是，就要把他设置为待办状态。
		 * 1.首先要找到.
		 * 2.xxxx.
		 */
		//  String sql = "SELECT COUNT(*) FROM WF_GenerWorkerlist WHERE FK_Node=";
		String mysql = "SELECT COUNT(*)  as Num FROM WF_GenerWorkerlist WHERE IsPass=0 AND FID=" + this.getFID();
		int num = DBAccess.RunSQLReturnValInt(mysql);
		if (num == 0)
		{
			/* 说明当前主流程上是分流节点，但是已经没有子线程的待办了。
			 * 就是说，删除子流程的时候，删除到最后已经没有活动或者已经完成的子线程了.
			 * */

			GenerWorkerList gwl = new GenerWorkerList();
			int i = gwl.Retrieve(GenerWorkerListAttr.FK_Node, gwfMain.getNodeID(), GenerWorkerListAttr.WorkID, gwfMain.getWorkID(), GenerWorkerListAttr.FK_Emp, WebUser.getNo());
			if (i == 0)
			{
				Node ndMain = new Node(gwfMain.getNodeID());
				if (ndMain.getItIsHL() == true)
				{
					/* 有可能是当前节点已经到了合流节点上去了, 要判断合流节点是否有代办？如果没有代办，就撤销到分流节点上去.
					 *
					 * 就要检查他是否有代办.
					 */
					mysql = "SELECT COUNT(*)  as Num FROM WF_GenerWorkerlist WHERE IsPass=0 AND FK_Node=" + gwfMain.getNodeID();
					num = DBAccess.RunSQLReturnValInt(mysql);
					if (num == 0)
					{
						/*如果没有待办，就说明，当前节点已经运行到合流节点，但是不符合合流节点的完成率，导致合流节点上的人员看不到待办.
						 * 这种情况，就需要让当前分流节点产生待办.
						 */

						mysql = "SELECT FK_Node FROM WF_GenerWorkerlist WHERE FID=0 AND WorkID=" + gwfMain.getWorkID() + " ORDER BY RDT DESC ";
						int fenLiuNodeID = DBAccess.RunSQLReturnValInt(mysql);

						Node nd = new Node(fenLiuNodeID);
						if (nd.getItIsFL() == false)
						{
							throw new RuntimeException("@程序错误，没有找到最近的一个分流节点.");
						}

						GenerWorkerLists gwls = new GenerWorkerLists();
						gwls.Retrieve(GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.FK_Node, fenLiuNodeID, null);
						for (GenerWorkerList item : gwls.ToJavaList())
						{
							item.setItIsRead(false);
							item.setPassInt(0);
							item.setSDT(DataType.getCurrentDateTimess());
							item.Update();
						}
					}
				}
			}
			else
			{
				gwl.setItIsRead(false);
				gwl.setPassInt(0);
				gwl.setSDT(DataType.getCurrentDateTimess());
				gwl.Update();
				return "子线程被删除成功,这是最后一个删除的子线程已经为您在{" + gwfMain.getNodeName() + "}产生了待办,<a href='/WF/MyFlow.htm?WorkID=" + gwfMain.getWorkID() + "&FK_Flow=" + gwfMain.getFlowNo() + "'>点击处理工作</a>.";

			}
		}
		return "子线程被删除成功.";
	}

	/**
	 彻底的删除流程

	 @param isDelSubFlow 是否要删除子流程
	 @return 删除的消息
	*/
	public final String DoDeleteWorkFlowByReal(boolean isDelSubFlow) throws Exception {
		if (this.getFID() != 0)
		{
			return DoDeleteSubThread2015();
		}

		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(this.getWorkID());
		if (gwf.RetrieveFromDBSources() == 0)
		{
			return "删除成功.";
		}

		String info = "";
		WorkNode wn = this.GetCurrentWorkNode();

		// 处理删除前事件。
		ExecEvent.DoFlow(EventListFlow.BeforeFlowDel, wn, null);


			///#region 删除独立表单的数据.
		FrmNodes fns = new FrmNodes();
		fns.Retrieve(FrmNodeAttr.FK_Flow, this.getHisFlow().getNo(), null);
		String strs = "";
		for (FrmNode nd : fns.ToJavaList())
		{
			if (strs.contains("@" + nd.getFrmID()) == true)
			{
				continue;
			}

			strs += "@" + nd.getFrmID() + "@";
			try
			{
				MapData md = new MapData(nd.getFrmID());
				DBAccess.RunSQL("DELETE FROM " + md.getPTable() + " WHERE OID=" + this.getWorkID());
			}
			catch (java.lang.Exception e)
			{
			}
		}

			///#endregion 删除独立表单的数据.

		//删除流程数据.
		DBAccess.RunSQL("DELETE FROM ND" + Integer.parseInt(this.getHisFlow().getNo()) + "Track WHERE WorkID=" + this.getWorkID());
		DBAccess.RunSQL("DELETE FROM " + this.getHisFlow().getPTable() + " WHERE OID=" + this.getWorkID());
		DBAccess.RunSQL("DELETE FROM WF_CHEval WHERE  WorkID=" + this.getWorkID()); // 删除质量考核数据。


			///#region 正常的删除信息.
		Log.DebugWriteInfo("@[" + this.getHisFlow().getName() + "]流程被[" + WebUser.getNo() + WebUser.getName() + "]删除，WorkID[" + this.getWorkID() + "]。");
		String msg = "";
		try
		{
			long workId = this.getWorkID();
			String flowNo = this.getHisFlow().getNo();
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("获取流程的 ID 与流程编号 出现错误。" + ex.getMessage());
		}

		try
		{
			// 删除单据信息.
			DBAccess.RunSQL("DELETE FROM WF_CCList WHERE WorkID=" + this.getWorkID());

			//删除它的工作.
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkFlow WHERE (WorkID=" + this.getWorkID() + " OR FID=" + this.getWorkID() + " ) AND FK_Flow='" + this.getHisFlow().getNo() + "'");
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE (WorkID=" + this.getWorkID() + " OR FID=" + this.getWorkID() + " ) AND FK_Flow='" + this.getHisFlow().getNo() + "'");

			//删除所有节点上的数据.
			Nodes nds = this.getHisFlow().getHisNodes();
			for (Node nd : nds.ToJavaList())
			{
				MapDtls dtls = new MapDtls("ND" + nd.getNodeID());
				for (MapDtl dtl : dtls.ToJavaList())
				{
					try
					{
						DBAccess.RunSQL("DELETE FROM " + dtl.getPTable() + " WHERE RefPk = " + this.getWorkID());
					}
					catch (java.lang.Exception e2)
					{
					}
				}
				try
				{
					if (DBAccess.IsExitsObject("ND" + nd.getNodeID()) == false)
					{
						continue;
					}

					DBAccess.RunSQL("DELETE FROM ND" + nd.getNodeID() + " WHERE OID=" + this.getWorkID() + " OR FID=" + this.getWorkID());
				}
				catch (RuntimeException ex)
				{
					msg += "@ delete data error " + ex.getMessage();
				}


			}
			if (!Objects.equals(msg, ""))
			{
				Log.DebugWriteInfo(msg);
			}
		}
		catch (RuntimeException ex)
		{
			String err = "@删除工作流程[" + this.getHisGenerWorkFlow().getWorkID() + "," + this.getHisGenerWorkFlow().getTitle() + "] Err " + ex.getMessage();
			Log.DebugWriteError(err);
			throw new RuntimeException(err);
		}
		info = "@删除流程删除成功";

			///#endregion 正常的删除信息.


			///#region 处理分流程删除的问题完成率的问题。
		if (this.getFID() != 0)
		{
			String sql = "";
			/*
			 * 取出来获取停留点,没有获取到说明没有任何子线程到达合流点的位置.
			 */
			sql = "SELECT FK_Node FROM WF_GenerWorkerlist WHERE WorkID=" + wn.getHisWork().getFID() + " AND IsPass=3";
			int fk_node = DBAccess.RunSQLReturnValInt(sql, 0);
			if (fk_node != 0)
			{
				/* 说明它是待命的状态 */
				Node nextNode = new Node(fk_node);
				if (nextNode.getPassRate().compareTo(BigDecimal.ZERO) > 0)
				{
					/* 找到等待处理节点的上一个点 */
					Nodes priNodes = nextNode.getFromNodes();
					if (priNodes.size() != 1)
					{
						throw new RuntimeException("@没有实现子流程不同线程的需求。");
					}

					Node priNode = (Node)priNodes.get(0);


						///#region 处理完成率
					sql = "SELECT COUNT(*) AS Num FROM WF_GenerWorkerlist WHERE FK_Node=" + priNode.getNodeID() + " AND FID=" + wn.getHisWork().getFID() + " AND IsPass=1";
					BigDecimal ok = new BigDecimal(DBAccess.RunSQLReturnValInt(sql));
					sql = "SELECT COUNT(*) AS Num FROM WF_GenerWorkerlist WHERE FK_Node=" + priNode.getNodeID() + " AND FID=" + wn.getHisWork().getFID();
					BigDecimal all = new BigDecimal(DBAccess.RunSQLReturnValInt(sql));
					if (all.compareTo(BigDecimal.ZERO) == 0)
					{
						/*说明:所有的子线程都被杀掉了, 就应该整个流程结束。*/
						WorkFlow wf = new WorkFlow(this.getHisFlow(), this.getFID());
						info += "@所有的子线程已经结束。";
						info += "@结束主流程信息。";
						info += "@" + wf.DoFlowOver(ActionType.FlowOver, "合流点流程结束", null, null);
					}

					BigDecimal passRate = ok.divide(all.multiply(BigDecimal.valueOf(100)));
					if (nextNode.getPassRate().compareTo(passRate) <= 0)
					{
						/*说明全部的人员都完成了，就让合流点显示它。*/
						DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0  WHERE IsPass=3  AND WorkID=" + wn.getHisWork().getFID() + " AND FK_Node=" + fk_node);
					}

						///#endregion 处理完成率
				}
			} // 结束有待命的状态判断。

			if (fk_node == 0)
			{
				/* 说明:没有找到等待启动工作的合流节点. */
				gwf = new GenerWorkFlow(this.getFID());
				Node fND = new Node(gwf.getNodeID());
				switch (fND.getHisNodeWorkType())
				{
					case WorkHL: //主流程运行到合流点上了
						break;
					default:
						/* 解决删除最后一个子流程时要把干流程也要删除。*/
						sql = "SELECT COUNT(*) AS Num FROM WF_GenerWorkerlist WHERE FK_Node=" + wn.getHisNode().getNodeID() + " AND FID=" + wn.getHisWork().getFID();
						int num = DBAccess.RunSQLReturnValInt(sql);
						if (num == 0)
						{
							/*说明没有子进程，就要把这个流程执行完成。*/
							WorkFlow wf = new WorkFlow(this.getHisFlow(), this.getFID());
							info += "@所有的子线程已经结束。";
							info += "@结束主流程信息。";
							info += "@" + wf.DoFlowOver(ActionType.FlowOver, "主流程结束", null, null);
						}
						break;
				}
			}
		}

			///#endregion


			///#region 删除该流程下面的子流程.
		if (isDelSubFlow)
		{
			GenerWorkFlows gwfs = new GenerWorkFlows();
			gwfs.Retrieve(GenerWorkFlowAttr.PWorkID, this.getWorkID(), null);

			for (GenerWorkFlow item : gwfs.ToJavaList())
			{
				bp.wf.Dev2Interface.Flow_DoDeleteFlowByReal(item.getWorkID(), true);
			}
		}

			///#endregion 删除该流程下面的子流程.

		// 处理删除hou事件。
		ExecEvent.DoFlow(EventListFlow.AfterFlowDel, wn, null);

		return info;
	}

	/**
	 删除工作流程记录日志，并保留运动轨迹.

	 @param isDelSubFlow 是否要删除子流程
	 @return
	*/
	public final String DoDeleteWorkFlowByWriteLog(String info, boolean isDelSubFlow) throws Exception {
		GERpt rpt = new GERpt("ND" + Integer.parseInt(this.getHisFlow().getNo()) + "Rpt", this.getWorkID());
		WorkFlowDeleteLog log = new WorkFlowDeleteLog();
		log.setOID(this.getWorkID());
		try
		{
			log.Copy(rpt);
			log.setDeleteDT(DataType.getCurrentDateTime());
			log.setOperDept(WebUser.getDeptNo());
			log.setOperDeptName(WebUser.getDeptName());
			log.setOper(WebUser.getNo());
			log.setDeleteNote(info);
			log.setOID(this.getWorkID());
			log.setFlowNo(this.getHisFlow().getNo());
			log.InsertAsOID(log.getOID());
			return DoDeleteWorkFlowByReal(isDelSubFlow);
		}
		catch (RuntimeException ex)
		{
			log.CheckPhysicsTable();
			log.Delete();
			throw new RuntimeException(Arrays.toString(ex.getStackTrace()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


		///#region 流程的强制终止\删除 或者恢复使用流程,
	/**
	 恢复流程.

	 @param msg 回复流程的原因
	*/
	public final void DoComeBackWorkFlow(String msg)
	{
		try
		{
			//设置产生的工作流程为
			GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
			gwf.setWFState(WFState.Runing);
			gwf.DirectUpdate();

			// 增加消息
			WorkNode wn = this.GetCurrentWorkNode();
			GenerWorkerLists wls = new GenerWorkerLists(wn.getHisWork().getOID(), String.valueOf(wn.getHisNode().getNodeID()));
			if (wls.size() == 0)
			{
				throw new RuntimeException("@恢复流程出现错误,产生的工作者列表");
			}

			for (GenerWorkerList item : wls.ToJavaList())
			{
				bp.wf.Dev2Interface.Port_SendMsg(item.getEmpNo(), "流程恢复通知:" + gwf.getTitle(), "该流程[" + gwf.getTitle() + "]，请打开待办处理.", "rback");
			}
		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteError("@恢复流程出现错误." + ex.getMessage());
			throw new RuntimeException("@恢复流程出现错误." + ex.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

		///#endregion

	/**
	 得到当前的进行中的工作。

	 @return
	*/
	public final WorkNode GetCurrentWorkNode() throws Exception {
		int currNodeID = 0;
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(this.getWorkID());
		if (gwf.RetrieveFromDBSources() == 0)
		{
			this.DoFlowOver(ActionType.FlowOver, "非正常结束，没有找到当前的流程记录。", null, null);
			throw new RuntimeException("@" + String.format("工作流程%1$s已经完成。", this.getHisGenerWorkFlow().getTitle()));
		}

		Node nd = new Node(gwf.getNodeID());
		Work work = nd.getHisWork();
		work.setOID(this.getWorkID());
		work.setNodeID(nd.getNodeID());
		work.SetValByKey("FK_Dept", WebUser.getDeptNo());
		if (work.RetrieveFromDBSources() == 0)
		{
			Log.DebugWriteError("@WorkID=" + this.getWorkID() + ",FK_Node=" + gwf.getNodeID() + ".不应该出现查询不出来工作."); // 没有找到当前的工作节点的数据，流程出现未知的异常。
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
		wn.setHisGenerWorkFlow(gwf);
		return wn;
	}
	/**
	 结束分流的节点

	 @param gwf
	 @return
	*/
	public final String DoFlowOverFeiLiu(GenerWorkFlow gwf) throws Exception {
		// 查询出来有少没有完成的流程。
		int i = DBAccess.RunSQLReturnValInt("SELECT COUNT(*) FROM WF_GenerWorkFlow WHERE FID=" + gwf.getFID() + " AND WFState!=1");
		switch (i)
		{
			case 0:
				throw new RuntimeException("@不应该的错误。");
			case 1:
				DBAccess.RunSQL("DELETE FROM WF_GenerWorkFlow  WHERE FID=" + gwf.getFID() + " OR WorkID=" + gwf.getFID());
				DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE FID=" + gwf.getFID() + " OR WorkID=" + gwf.getFID());

				Work wk = this.getHisFlow().getHisStartNode().getHisWork();
				wk.setOID(gwf.getFID());
				wk.Update();

				return "@当前的工作已经完成，该流程上所有的工作都已经完成。";
			default:
				DBAccess.RunSQL("UPDATE WF_GenerWorkFlow SET WFState=1 WHERE WorkID=" + this.getWorkID());
				DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=1 WHERE WorkID=" + this.getWorkID());
				return "@当前的工作已经完成。";
		}
	}
	/**
	 处理子线程完成.

	 @return
	*/
	public final String DoFlowThreadOver() throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		Node nd = new Node(gwf.getNodeID());

		//DBAccess.RunSQL("DELETE FROM WF_GenerWorkFlow   WHERE WorkID=" + this.WorkID);
		DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + this.getWorkID());

		String sql = "SELECT count(*) FROM WF_GenerWorkerlist WHERE  FID=" + this.getFID();
		int num = DBAccess.RunSQLReturnValInt(sql);
		if (DBAccess.RunSQLReturnValInt(sql) == 0)
		{
			/*说明这是最后一个*/
			WorkFlow wf = new WorkFlow(this.getFID());
			wf.DoFlowOver(ActionType.FlowOver, "子线程结束", null, null);
			return "@当前子线程已完成，干流程已完成。";
		}
		else
		{
			return "@当前子线程已完成，干流程还有(" + num + ")个子线程未完成。";
		}
	}


	/// <summary>
	/// 执行流程完成
	/// </summary>
	/// <param name="at"></param>
	/// <param name="stopMsg"></param>
	/// <param name="currNode"></param>
	/// <param name="rpt"></param>
	/// <param name="wfStateOver">结束类型:自定义参数</param>
	/// <param name="empNo"></param>
	/// <param name="empName"></param>
	/// <returns></returns>
	public final String DoFlowOver(ActionType at, String stopMsg, Node currNode, GERpt rpt, String empNo) throws Exception {
		return DoFlowOver(at, stopMsg, currNode, rpt, null, empNo, "");
	}
	public final String DoFlowOver(ActionType at, String stopMsg, Node currNode, GERpt rpt, String empNo, String empName) throws Exception {
		return DoFlowOver(at, stopMsg, currNode, rpt, WFState.CompleteEnd, empNo, empName);
	}
	public final String DoFlowOver(ActionType at, String stopMsg, Node currNode, GERpt rpt, WFState wfStateOver) throws Exception {
		return DoFlowOver(at, stopMsg, currNode, rpt, wfStateOver, "", "");
	}

	public final String DoFlowOver(ActionType at, String stopMsg, Node currNode, GERpt rpt) throws Exception {
		return DoFlowOver(at, stopMsg, currNode, rpt, WFState.CompleteEnd, "", "");
	}
	public int _IsComplete = 0;

	public final String DoFlowOver(ActionType at, String stopMsg, Node currNode, GERpt rpt, WFState wfStateOver, String empNo, String empName) throws Exception {
		if (null == currNode)
		{
			return "err@当前节点为空..";
		}

		if (wfStateOver != WFState.CompleteEnd && wfStateOver != WFState.CompleteByNode)
		{
			throw new Exception("err@流程状态设置错误,不是结束状态:CompleteEnd/CompleteByNode");
		}

		if (DataType.IsNullOrEmpty(stopMsg))
		{
			stopMsg += "流程结束";
		}

		//获得当前的节点.
		WorkNode wn = this.GetCurrentWorkNode();
		if(rpt == null)
		{
			GERpt rptGe = this.getHisFlow().getHisGERpt();
			if (wn.getHisNode().getItIsSubThread() == true)
				rptGe.SetValByKey("OID", this.getHisGenerWorkFlow().getFID());
			else
				rptGe.SetValByKey("OID", this.getWorkID());

			rptGe.RetrieveFromDBSources();
			rpt = rptGe;
		}
		wn.rptGe = rpt;

		//调用结束前事件.
		String mymsg = ExecEvent.DoFlow(EventListFlow.FlowOverBefore, wn, null);
		//String mymsg = this.HisFlow.DoFlowEventEntity(EventListFlow.FlowOverBefore, currNode, rpt, null);
		if (mymsg != null)
		{
			stopMsg += "@" + mymsg;
		}

		String exp = currNode.getFocusField();
		if (DataType.IsNullOrEmpty(exp) == false && exp.length() > 1)
		{
			if (rpt != null)
			{
				stopMsg += bp.wf.Glo.DealExp(exp, rpt, null);
			}
		}

		//IsMainFlow== false 这个位置是子线程
		if (this.getItIsMainFlow() == false)
		{
			/* 处理子线程完成*/
			stopMsg += this.DoFlowThreadOver();
		}


			///#region 处理明细表的汇总.
		this._IsComplete = 1;

			///#endregion 处理明细表的汇总.


			///#region 处理后续的业务.

		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		if (1 == 2)
		{
			// 是否删除流程注册表的数据？
			ps = new Paras();
			ps.SQL = "DELETE FROM WF_GenerWorkFlow WHERE WorkID=" + dbstr + "WorkID1 OR FID=" + dbstr + "WorkID2 ";
			ps.Add("WorkID1", this.getWorkID());
			ps.Add("WorkID2", this.getWorkID());
			DBAccess.RunSQL(ps);
		}


		// 删除子线程产生的 流程注册信息.
		if (this.getFID() == 0)
		{
			ps = new Paras();
			ps.SQL = "DELETE FROM WF_GenerWorkFlow WHERE FID=" + dbstr + "WorkID";
			ps.Add("WorkID", this.getWorkID());
			DBAccess.RunSQL(ps);
		}

		// 清除工作者.
		ps = new Paras();
		ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + dbstr + "WorkID1 OR FID=" + dbstr + "WorkID2 ";
		ps.Add("WorkID1", this.getWorkID());
		ps.Add("WorkID2", this.getWorkID());
		DBAccess.RunSQL(ps);

		//把当前的人员字符串加入到参与人里面去,以方便查询.
		String emps = WebUser.getNo() + "," + WebUser.getName() + "@";

		// 设置流程完成状态.
		ps = new Paras();
		if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3
				|| SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB
				|| SystemConfig.getAppCenterDBType() == DBType.UX || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			ps.SQL = "UPDATE " + this.getHisFlow().getPTable() + " SET  FlowEmps= FlowEmps ||'" + emps + "', WFState=:WFState,WFSta=:WFSta WHERE OID=" + dbstr + "OID";
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.MySQL  ||  SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL  ||  SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			ps.SQL = "UPDATE " + this.getHisFlow().getPTable() + " SET FlowEmps= CONCAT(FlowEmps ,'" + emps + "'), WFState=" + dbstr + "WFState,WFSta=" + dbstr + "WFSta WHERE OID=" + dbstr + "OID";
		}
		else
		{
			ps.SQL = "UPDATE " + this.getHisFlow().getPTable() + " SET FlowEmps= FlowEmps + '" + emps + "', WFState=" + dbstr + "WFState,WFSta=" + dbstr + "WFSta WHERE OID=" + dbstr + "OID";
		}

		ps.Add("WFState", wfStateOver.getValue());
		ps.Add("WFSta", WFSta.Complete.getValue());
		ps.Add("OID", this.getWorkID());
		DBAccess.RunSQL(ps);

		//加入轨迹.
		if (DataType.IsNullOrEmpty(empNo) == true)
		{
			empNo = WebUser.getNo();
			empName = WebUser.getName();
		}
		wn.AddToTrack(at, empNo, empName, wn.getHisNode().getNodeID(), wn.getHisNode().getName(), stopMsg);

		//执行流程结束.
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		//增加参与的人员
		if (gwf.getEmps().contains("@" + WebUser.getNo() + ",") == false)
		{
			gwf.setEmps(gwf.getEmps() + "@" + WebUser.getNo() + "," + WebUser.getName());
		}

		gwf.setWFState(wfStateOver);
		if (wfStateOver == WFState.CompleteByNode)
			gwf.SetPara("StopFlowType", 1); //结束流程类型.
		else
			gwf.SetPara("StopFlowType", 0); //结束流程类型.
		gwf.Update();

		//生成关键字.
		this.GenerSKeyWords(gwf, wn.rptGe);
		//流程发送成功事件
		String sendSuccess = ExecEvent.DoNode(EventListNode.SendSuccess, wn);

		//调用结束后事件.
		String result = ExecEvent.DoFlow(EventListFlow.FlowOverAfter, wn, null);
		if(result!=null)
			stopMsg +=result;

		//处理单据实体.
		if (DataType.IsNullOrEmpty(gwf.GetParaString("BillFrmID")) == false)
		{
			String frmID = gwf.GetParaString("BillFrmID");
			GEEntityOID gE = new GEEntityOID(frmID, gwf.getWorkID()); //设置完成.
			gE.SetValByKey("BillState", 100); //设置已完成.
			gE.Update(); //更新.
		}
		if (DataType.IsNullOrEmpty(gwf.GetParaString("EntityNoNameFrmID")) == false)
		{
			//string frmID = gwf.GetParaString("EntityNoNameFrmID");
			//GEEntityNoName gE = new GEEntityNoName(frmID); //设置完成.
			//gE.SetValByKey("BillState", 3); //设置已完成.
			//gE.Update(); //更新.
		}
				///#endregion 处理后续的业务.

		//执行最后一个子流程发送后的检查，不管是否成功，都要结束该流程。
		stopMsg += WorkNodePlus.SubFlowEvent(wn);

		//String dbstr =  bp.difference.SystemConfig.getAppCenterDBVarStr();


			///#region 处理审核问题,更新审核组件插入的审核意见中的 到节点，到人员。
		ps = new Paras();
		ps.SQL = "UPDATE ND" + Integer.parseInt(currNode.getFlowNo()) + "Track SET NDTo=" + dbstr + "NDTo,NDToT=" + dbstr + "NDToT,EmpTo=" + dbstr + "EmpTo,EmpToT=" + dbstr + "EmpToT WHERE NDFrom=" + dbstr + "NDFrom AND EmpFrom=" + dbstr + "EmpFrom AND WorkID=" + dbstr + "WorkID AND ActionType=" + ActionType.WorkCheck.getValue();
		ps.Add(TrackAttr.NDTo, currNode.getNodeID());
		ps.Add(TrackAttr.NDToT, "", false);
		ps.Add(TrackAttr.EmpTo, "", false);
		ps.Add(TrackAttr.EmpToT, "", false);
		ps.Add(TrackAttr.NDFrom, currNode.getNodeID());
		ps.Add(TrackAttr.EmpFrom, WebUser.getNo(), false);
		ps.Add(TrackAttr.WorkID, this.getWorkID());
		DBAccess.RunSQL(ps);

			///#endregion 处理审核问题.

		//如果存在 BillState列，执行更新, 让其可见.
		if (rpt.getEnMap().getAttrs().contains("BillState") == true)
		{
			rpt.SetValByKey("BillState", 100);
		}
		else
		{
			String ptable = "ND" + Integer.parseInt(gwf.getFlowNo()) + "Rpt";
			if (rpt.getEnMap().getPhysicsTable().equals(ptable) == false && DBAccess.IsExitsTableCol(rpt.getEnMap().getPhysicsTable(), "BillState") == true)
			{
				DBAccess.RunSQL("UPDATE " + rpt.getEnMap().getPhysicsTable() + " SET BillState=100 WHERE OID=" + this.getWorkID());
			}
		}
		//修改绑定表单中BillState的状态
		if(wn.getHisNode().getHisFormType() == NodeFormType.RefOneFrmTree){
			Work wk = wn.getHisNode().getHisWork();
			String table = wk.getEnMap().getPhysicsTable();
			if(DBAccess.IsExitsTableCol(table,"BillState") == true){
				DBAccess.RunSQL("UPDATE "+table+" SET BillState=100 WHERE OID="+this.getWorkID());
			}
			/*Work wk = wn.getHisNode().getHisWork();
			wk.setOID(this.getWorkID());
			if(wk.RetrieveFromDBSources()==1 && wk.getEnMap().getAttrs().contains("BillState") == true){
				wk.SetValByKey("BillState", 100);
				wk.DirectUpdate();
			}*/
		}

		if (sendSuccess != null)
		{
			return "@" + sendSuccess + stopMsg;
		}

		return stopMsg;
	}
	/**
	 归档关键字查询

	 @param gwf
	 @param en
	*/
	public final void GenerSKeyWords(GenerWorkFlow gwf, Entity en) throws Exception {
		//获取WF_GenerWorkFlow的关键字.
		String keyworkd = gwf.getTitle() + gwf.getTodoEmps() + "," + gwf.getFlowName();
		for (Attr item : en.getEnMap().getAttrs().ToJavaList())
		{
			if (item.getUIContralType() == UIContralType.DDL)
			{
				continue;
			}

			if (item.getMyDataType ()== DataType.AppString && item.getMaxLength() <= 100)
			{
				keyworkd += en.GetValStrByKey(item.getKey()) + ",";
				continue;
			}
			keyworkd += en.GetValStrByKey(item.getKey()) + ",";
		}

		if (DBAccess.IsExitsTableCol("WF_GenerWorkFlow", "SKeyWords") == false)
		{
			return;
		}

		Paras pa = new Paras();
		pa.SQL = "UPDATE WF_GenerWorkFlow SET SKeyWords=" + SystemConfig.getAppCenterDBVarStr() + "SKeyWords WHERE WorkID=" + this.getWorkID();
		pa.Add("SKeyWords", keyworkd, false);
		DBAccess.RunSQL(pa);
	}
	public final String GenerFHStartWorkInfo() throws Exception {
		String msg = "";
		DataTable dt = DBAccess.RunSQLReturnTable("SELECT Title,RDT,Rec,OID FROM ND" + this.getStartNodeID() + " WHERE FID=" + this.getFID());
		switch (dt.Rows.size())
		{
			case 0:
				Node nd = new Node(this.getStartNodeID());
				throw new RuntimeException("@没有找到他们开始节点的数据，流程异常。FID=" + this.getFID() + "，节点：" + nd.getName() + "节点ID：" + nd.getNodeID());
			case 1:
				msg = String.format("@发起人： %1$s  日期：%2$s 发起的流程 标题：%3$s ，已经成功完成。", dt.Rows.get(0).getValue("Rec").toString(), dt.Rows.get(0).getValue("RDT").toString(), dt.Rows.get(0).getValue("Title").toString());
				break;
			default:
				msg = "@下列(" + dt.Rows.size() + ")位人员发起的流程已经完成。";
				for (DataRow dr : dt.Rows)
				{
					msg += "<br>发起人：" + dr.getValue("Rec") + " 发起日期：" + dr.getValue("RDT") + " 标题：" + dr.getValue("Title") + "<a href='./../../WF/WFRpt.htm?WorkID=" + dr.getValue("OID") + "&FK_Flow=" + this.getHisFlow().getNo() + "' target=_blank>详细...</a>";
				}
				break;
		}
		return msg;
	}
	public final int getStartNodeID() {
		return Integer.parseInt(this.getHisFlow().getNo() + "01");
	}

	/**
	 执行冻结

	 @param fixMsg 冻结原因
	*/
	public final String DoFix(String fixMsg) throws Exception {
		if (this.getHisGenerWorkFlow().getWFState() == WFState.Fix)
		{
			throw new RuntimeException("@当前已经是冻结的状态您不能执行再冻结.");
		}

		if (DataType.IsNullOrEmpty(fixMsg))
		{
			fixMsg = "无";
		}


		/* 获取它的工作者，向他们发送消息。*/
		GenerWorkerLists wls = new GenerWorkerLists(this.getWorkID(), this.getHisFlow().getNo());
		String emps = "";

		for (GenerWorkerList wl : wls.ToJavaList())
		{
			if (wl.getItIsEnable() == false)
			{
				continue; //不发送给禁用的人。
			}
			emps += wl.getEmpNo() + "," + wl.getEmpName() + ";";
			//写入消息。
			bp.wf.Dev2Interface.Port_SendMsg(wl.getEmpNo(), this.getHisGenerWorkFlow().getTitle(), fixMsg, "Fix" + wl.getWorkID(), "Fix", wl.getFlowNo(), wl.getNodeID(), wl.getWorkID(), wl.getFID());
		}

		/* 执行 WF_GenerWorkFlow 冻结. */
		int sta = WFState.Fix.getValue();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkFlow SET WFState=" + dbstr + "WFState WHERE WorkID=" + dbstr + "WorkID";
		ps.Add(GenerWorkFlowAttr.WFState, sta);
	ps.Add(GenerWorkFlowAttr.WorkID, this.getWorkID());
		DBAccess.RunSQL(ps);

		Node nd = new Node(this.getHisGenerWorkFlow().getNodeID());
		if(nd.getTodolistModel() == TodolistModel.Order || nd.getTodolistModel() == TodolistModel.Teamup || nd.getTodolistModel() == TodolistModel.TeamupGroupLeader){
			ps = new Paras();
			ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=" + dbstr + "IsPass WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + this.getHisGenerWorkFlow().getNodeID()+" AND FK_Emp="+dbstr+"FK_Emp";
			ps.Add(GenerWorkerListAttr.IsPass, 9);
			ps.Add(GenerWorkerListAttr.WorkID, this.getWorkID());
			ps.Add(GenerWorkerListAttr.FK_Emp, WebUser.getNo());
			DBAccess.RunSQL(ps);
		}else{
			ps = new Paras();
			ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=" + dbstr + "IsPass WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + this.getHisGenerWorkFlow().getNodeID();
			ps.Add(GenerWorkerListAttr.IsPass, 9);
			ps.Add(GenerWorkerListAttr.WorkID, this.getWorkID());
			DBAccess.RunSQL(ps);
		}



		// 更新流程报表的状态。
		ps = new Paras();
		ps.SQL = "UPDATE " + this.getHisFlow().getPTable() + " SET WFState=" + dbstr + "WFState WHERE OID=" + dbstr + "OID";
		ps.Add(GERptAttr.WFState, sta);
		ps.Add(GERptAttr.OID, this.getWorkID());
		DBAccess.RunSQL(ps);

		// 记录日志..
		//WorkNode wn = new WorkNode(this.WorkID, this.HisGenerWorkFlow.getNodeID());
		//wn.AddToTrack(ActionType.Info, WebUser.getNo(), WebUser.getName(), wn.Hisnode.getNodeID(), wn.getHisNode().Name, fixMsg,);

		return this.getWorkID() + "-" + this.getHisFlow().getName() + "已经成功执行冻结";
	}
	/**
	 执行解除冻结

	 @param unFixMsg 冻结原因
	*/
	public final String DoUnFix(String unFixMsg) throws Exception {
		if (this.getHisGenerWorkFlow().getWFState() != WFState.Fix)
		{
			throw new RuntimeException("@当前非冻结的状态您不能执行解除冻结.");
		}

		if (DataType.IsNullOrEmpty(unFixMsg))
		{
			unFixMsg = "无";
		}


		/*** 获取它的工作者，向他们发送消息。*/

		//GenerWorkerLists wls = new GenerWorkerLists(this.WorkID, this.HisFlow.getNo());

		//String url = Glo.ServerIP + "/" + this.VirPath + this.AppType + "/WorkOpt/OneWork/OneWork.htm?CurrTab=Track&FK_Flow=" + this.HisFlow.getNo() + "&WorkID=" + this.WorkID + "&FID=" + this.HisGenerWorkFlow.FID + "&FK_Node=" + this.HisGenerWorkFlow.NodeID;
		//String mailDoc = "详细信息:<A href='" + url + "'>打开流程轨迹</A>.";
		//String title = "工作:" + this.HisGenerWorkFlow.Title + " 被" + WebUser.getName() + "冻结" + unFixMsg;
		//String emps = "";
		//foreach (GenerWorkerList wl in wls)
		//{
		//    if (wl.ItIsEnable == false)
		//        continue; //不发送给禁用的人。

		//    emps += wl.EmpNo + "," + wl.EmpName + ";";

		//    //写入消息。
		//    BP.WF.Dev2Interface.Port_SendMsg(wl.EmpNo, title, mailDoc, "Fix" + wl.WorkID, BP.Sys.SMSMsgType.Self, wl.FK_Flow, wl.getNodeID(), wl.WorkID, wl.FID);
		//}

		/* 执行 WF_GenerWorkFlow 冻结. */
		int sta = WFState.Runing.getValue();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkFlow SET WFState=" + dbstr + "WFState WHERE WorkID=" + dbstr + "WorkID";
		ps.Add(GenerWorkFlowAttr.WFState, sta);
		ps.Add(GenerWorkFlowAttr.WorkID, this.getWorkID());
		DBAccess.RunSQL(ps);

		// 更新流程报表的状态。
		ps = new Paras();
		ps.SQL = "UPDATE " + this.getHisFlow().getPTable() + " SET WFState=" + dbstr + "WFState WHERE OID=" + dbstr + "OID";
		ps.Add(GERptAttr.WFState, sta);
		ps.Add(GERptAttr.OID, this.getWorkID());
		DBAccess.RunSQL(ps);
		Node nd = new Node(this.getHisGenerWorkFlow().getNodeID());
		if(nd.getTodolistModel() == TodolistModel.Order || nd.getTodolistModel() == TodolistModel.Teamup || nd.getTodolistModel() == TodolistModel.TeamupGroupLeader){
			ps = new Paras();
			ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=" + dbstr + "IsPass WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + this.getHisGenerWorkFlow().getNodeID()+" AND FK_Emp="+dbstr+"FK_Emp";
			ps.Add(GenerWorkerListAttr.IsPass, 0);
			ps.Add(GenerWorkerListAttr.WorkID, this.getWorkID());
			ps.Add(GenerWorkerListAttr.FK_Emp, WebUser.getNo());
			DBAccess.RunSQL(ps);
		}else{
			ps = new Paras();
			ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=" + dbstr + "IsPass WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + this.getHisGenerWorkFlow().getNodeID();
			ps.Add(GenerWorkerListAttr.IsPass, 0);
			ps.Add(GenerWorkerListAttr.WorkID, this.getWorkID());
			DBAccess.RunSQL(ps);
		}


		// 记录日志..
		WorkNode wn = new WorkNode(this.getWorkID(), this.getHisGenerWorkFlow().getNodeID());
		//wn.AddToTrack(ActionType.Info, WebUser.getNo(), WebUser.getName(), wn.Hisnode.getNodeID(), wn.getHisNode().Name, unFixMsg);

		return "已经成功执行解除冻结:";
	}

		///#endregion


		///#region 基本属性
	/**
	 他的节点
	*/
	private Nodes _HisNodes = null;
	/**
	 节点s
	*/
	public final Nodes getHisNodes() throws Exception {
		if (this._HisNodes == null)
		{
			this._HisNodes = this.getHisFlow().getHisNodes();
		}
		return this._HisNodes;
	}
	/**
	 工作节点s(普通的工作节点)
	*/
	private WorkNodes _HisWorkNodesOfWorkID = null;
	/**
	 工作节点s
	*/
	public final WorkNodes getHisWorkNodesOfWorkID() throws Exception {
		if (this._HisWorkNodesOfWorkID == null)
		{
			this._HisWorkNodesOfWorkID = new WorkNodes();
			this._HisWorkNodesOfWorkID.GenerByWorkID(this.getHisFlow(), this.getWorkID());
		}
		return this._HisWorkNodesOfWorkID;
	}
	/**
	 工作节点s
	*/
	private WorkNodes _HisWorkNodesOfFID = null;
	/**
	 工作节点s
	*/
	public final WorkNodes getHisWorkNodesOfFID() throws Exception {
		if (this._HisWorkNodesOfFID == null)
		{
			this._HisWorkNodesOfFID = new WorkNodes();
			this._HisWorkNodesOfFID.GenerByFID(this.getHisFlow(), this.getFID());
		}
		return this._HisWorkNodesOfFID;
	}
	/**
	 工作流程
	*/
	private Flow _HisFlow = null;
	/**
	 工作流程
	*/
	public final Flow getHisFlow()
	{
		return this._HisFlow;
	}
	private GenerWorkFlow _HisGenerWorkFlow = null;
	public final GenerWorkFlow getHisGenerWorkFlow() throws Exception {
		if (_HisGenerWorkFlow == null)
		{
			_HisGenerWorkFlow = new GenerWorkFlow(this.getWorkID());
		}
		return _HisGenerWorkFlow;
	}
	public final void setHisGenerWorkFlow(GenerWorkFlow value)
	{
		_HisGenerWorkFlow = value;
	}
	/**
	 工作ID
	*/
	private long _WorkID = 0;
	/**
	 工作ID
	*/
	public final long getWorkID()
	{
		return this._WorkID;
	}
	/**
	 工作ID
	*/
	private long _FID = 0;
	/**
	 工作ID
	*/
	public final long getFID()
	{
		return this._FID;
	}
	public final void setFID(long value)
	{
		this._FID = value;
	}
	/**
	 是否是干流
	*/
	public final boolean getItIsMainFlow()
	{
		if (this.getFID() != 0 && this.getFID() != this.getWorkID())
		{
			return false;
		}
		else
		{
			return true;
		}
	}

		///#endregion


		///#region 构造方法
	public WorkFlow(long wkid) throws Exception {
		this.setHisGenerWorkFlow(new GenerWorkFlow());
		this.getHisGenerWorkFlow().RetrieveByAttr(GenerWorkerListAttr.WorkID, wkid);
		this._FID = this.getHisGenerWorkFlow().getFID();
		if (wkid == 0)
		{
			throw new RuntimeException("@没有指定工作ID, 不能创建工作流程.");
		}

		Flow flow = new Flow(this.getHisGenerWorkFlow().getFlowNo());
		this._HisFlow = flow;
		this._WorkID = wkid;

	}

	public WorkFlow(Flow flow, long wkid) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(wkid);
		gwf.RetrieveFromDBSources();

		this._FID = gwf.getFID();
		if (wkid == 0)
		{
			throw new RuntimeException("@没有指定工作ID, 不能创建工作流程.");
		}
		//Flow flow= new Flow(FlowNo);
		this._HisFlow = flow;
		this._WorkID = wkid;
	}
	/**
	 建立一个工作流事例

	 @param flow 流程No
	 @param wkid 工作ID
	*/
	public WorkFlow(Flow flow, long wkid, long fid)
	{
		this._FID = fid;
		if (wkid == 0)
		{
			throw new RuntimeException("@没有指定工作ID, 不能创建工作流程.");
		}
		//Flow flow= new Flow(FlowNo);
		this._HisFlow = flow;
		this._WorkID = wkid;
	}
	public WorkFlow(String FK_flow, long wkid, long fid) throws Exception {
		this._FID = fid;

		Flow flow = new Flow(FK_flow);
		if (wkid == 0)
		{
			throw new RuntimeException("@没有指定工作ID, 不能创建工作流程.");
		}
		//Flow flow= new Flow(FlowNo);
		this._HisFlow = flow;
		this._WorkID = wkid;
	}

		///#endregion


//		///#region 运算属性
//	public int _IsComplete = -1;
//	/**
//	 是不是完成
//	*/
//	public final boolean getItIsComplete() throws Exception {
//
//		//  bool s = !DBAccess.IsExits("select workid from WF_GenerWorkFlow WHERE WorkID=" + this.WorkID + " AND FK_Flow='" + this.HisFlow.getNo() + "'");
//
//		GenerWorkFlow generWorkFlow = new GenerWorkFlow(this.getWorkID());
//		if (generWorkFlow.getWFState() == WFState.CompleteEnd)
//		{
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//
//	}
//	/**
//	 是不是完成
//	*/
//	public final String isCompleteStr() throws Exception {
//		if (this.getItIsComplete())
//		{
//			return "已";
//		}
//		else
//		{
//			return "未";
//		}
//	}
//
//		///#endregion


		///#region 静态方法

	/**
	 是否这个工作人员能执行这个工作

	 @param nodeId 节点
	 @param empId 工作人员
	 @return 能不能执行
	*/
	public static boolean IsCanDoWorkCheckByEmpStation(int nodeId, String empId)
	{
		boolean isCan = false;
		// 判断角色对应关系是不是能够执行.
		String sql = "SELECT a.FK_Node FROM WF_NodeStation a,  Port_DeptEmpStation b WHERE (a.FK_Station=b.FK_Station) AND (a.FK_Node=" + nodeId + " AND b.FK_Emp='" + empId + "' )";
		isCan = DBAccess.IsExits(sql);
		if (isCan)
		{
			return true;
		}
		// 判断他的主要工作角色能不能执行它.
		sql = "select FK_Node from WF_NodeStation WHERE FK_Node=" + nodeId + " AND ( FK_Station in (select FK_Station from Port_DeptEmpStation WHERE FK_Emp='" + empId + "') ) ";
		return DBAccess.IsExits(sql);
	}
	/**
	 是否这个工作人员能执行这个工作

	 @param nodeId 节点
	 @param dutyNo 工作人员
	 @return 能不能执行
	*/
	public static boolean IsCanDoWorkCheckByEmpDuty(int nodeId, String dutyNo)
	{
		String sql = "SELECT a.FK_Node FROM WF_NodeDuty  a,  Port_EmpDuty b WHERE (a.FK_Duty=b.FK_Duty) AND (a.FK_Node=" + nodeId + " AND b.FK_Duty=" + dutyNo + ")";
		if (DBAccess.RunSQLReturnTable(sql).Rows.size() == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	/**
	 在物理上能构作这项工作的人员。

	 @param nodeId 节点ID
	 @return
	*/
	public static DataTable CanDoWorkEmps(int nodeId)
	{
		String sql = "select a.FK_Node, b.EmpID from WF_NodeStation  a,  Port_DeptEmpStation b WHERE (a.FK_Station=b.FK_Station) AND (a.FK_Node=" + nodeId + " )";
		return DBAccess.RunSQLReturnTable(sql);
	}
	/**
	 GetEmpsBy

	 @param dt
	 @return
	*/
	public final Emps GetEmpsBy(DataTable dt) throws Exception {
		// 形成能够处理这件事情的用户几何。
		Emps emps = new Emps();
		for (DataRow dr : dt.Rows)
		{
			emps.AddEntity(new Emp(dr.getValue("EmpID").toString()));
		}
		return emps;
	}


		///#endregion


		///#region 流程方法

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


				_AppType = "WF";

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
				_VirPath = bp.sys.Glo.getRequest().getRemoteAddr();
			}
			else
			{
				_VirPath = "";
			}
		}
		return _VirPath;
	}
	/**
	 撤销挂起

	 @return
	*/
	public final String DoHungupWork_Un() throws Exception {

		String checker = this.getHisGenerWorkFlow().GetParaString("HungupChecker");

		//删除领导审核的数据.
		DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE FK_Node=" + this.getHisGenerWorkFlow().getNodeID() + " AND FK_Emp='" + checker + "' AND WorkID=" + this.getHisGenerWorkFlow().getWorkID());

		this.getHisGenerWorkFlow().setHungupTime(DataType.getCurrentDateTime());
		this.getHisGenerWorkFlow().setWFState(WFState.Runing);
		this.getHisGenerWorkFlow().Update();
		String ptable = this.getHisFlow().getPTable();

		// 记录日志..
		WorkNode wn = new WorkNode(this.getWorkID(), this.getHisGenerWorkFlow().getNodeID());
		wn.AddToTrack(ActionType.UnHungup, WebUser.getNo(), WebUser.getName(), wn.getHisNode().getNodeID(), wn.getHisNode().getName(), "撤销挂起");


		return "成功撤销.";
	}
	/**
	 执行挂起

	 @param way 挂起方式
	 @param relData 释放日期
	 @param hungNote 挂起原因
	 @return
	*/
	public final String DoHungup(HungupWay way, String relData, String hungNote) throws Exception {
		Node nd = new Node(this.getHisGenerWorkFlow().getNodeID());
		if(nd.getItIsStartNode())
			return "err@开始节点不能启用挂起状态";

		if (this.getHisGenerWorkFlow().getWFState() == WFState.Hungup)
		{
			return "err@当前已经是挂起的状态您不能执行在挂起.";
		}

		if (DataType.IsNullOrEmpty(hungNote) == true)
		{
			hungNote = "无";
		}

		if (way == HungupWay.SpecDataRel)
		{
			if (relData.length() < 10)
			{
				throw new RuntimeException("err@解除挂起的日期不正确(" + relData + ")");
			}
		}
		if (relData == null)
		{
			relData = "";
		}

		/* 获取它的工作者，向他们发送消息。*/
		GenerWorkerLists wls = new GenerWorkerLists(this.getWorkID(), this.getHisFlow().getNo());
		//String mailDoc = "详细信息:<A href='" + url + "'>打开流程轨迹</A>.";
		String title = "工作:" + this.getHisGenerWorkFlow().getTitle() + " 被" + WebUser.getName() + "挂起" + hungNote;
		String emps = "";

		GenerWorkerList gwl = null;
		for (GenerWorkerList wl : wls.ToJavaList())
		{
			if (wl.getItIsEnable() == false)
			{
				continue; //不发送给禁用的人。
			}

			//BP.WF.Port.WFEmp emp = new bp.port.WFEmp(wl.EmpNo);
			emps += wl.getEmpNo() + "," + wl.getEmpName() + ";";

			gwl = wl;

			//写入消息。
			bp.wf.Dev2Interface.Port_SendMsg(wl.getEmpNo(), title, title, "Hungup" + wl.getWorkID(), bp.wf.SMSMsgType.Hungup, wl.getFlowNo(), wl.getNodeID(), wl.getWorkID(), wl.getFID());
		}

		/* 执行 WF_GenerWorkFlow 挂起. */
		int hungSta = WFState.Hungup.getValue();
		String dbstr = SystemConfig.getAppCenterDBVarStr();


		//发送人.
		String[] sender = this.getHisGenerWorkFlow().getSender().split("[,]", -1);
		gwl.setEmpNo(sender[0]);
		gwl.setEmpName(sender[1]);
		gwl.setWorkID(this.getHisGenerWorkFlow().getWorkID());
		gwl.setNodeID(this.getHisGenerWorkFlow().getNodeID());
		gwl.setPassInt(0);
		gwl.setItIsRead(false);
		gwl.setSDT("2030-01-01 10:00");
		gwl.setRDT(DataType.getCurrentDateTimess());
		//   gwl.setFID(this.HisGenerWorkFlow.FID;
		gwl.Save();

		//更新挂起状态.
		this.getHisGenerWorkFlow().setWFState(WFState.Hungup);
		this.getHisGenerWorkFlow().SetPara("Hunguper", WebUser.getNo());
		this.getHisGenerWorkFlow().SetPara("HunguperName", WebUser.getName());
		this.getHisGenerWorkFlow().SetPara("HungupWay", way.getValue());
		this.getHisGenerWorkFlow().SetPara("HungupRelDate", relData);
		this.getHisGenerWorkFlow().SetPara("HungupNote", hungNote);
		this.getHisGenerWorkFlow().SetPara("HungupSta", HungupSta.Apply.getValue()); //设置申请状态.
		this.getHisGenerWorkFlow().SetPara("HungupChecker", gwl.getEmpNo()); //要审批的人.

		this.getHisGenerWorkFlow().setHungupTime(DataType.getCurrentDateTime());
		this.getHisGenerWorkFlow().Update();

		// 更新流程报表的状态。
		Paras ps = new Paras();
		ps.SQL = "UPDATE " + this.getHisFlow().getPTable() + " SET WFState=" + dbstr + "WFState WHERE OID=" + dbstr + "OID";
		ps.Add(GERptAttr.WFState, hungSta);
		ps.Add(GERptAttr.OID, this.getWorkID());
		DBAccess.RunSQL(ps);

		// 记录日志..
		WorkNode wn = new WorkNode(this.getWorkID(), this.getHisGenerWorkFlow().getNodeID());
		wn.AddToTrack(ActionType.Hungup, WebUser.getNo(), WebUser.getName(), wn.getHisNode().getNodeID(), wn.getHisNode().getName(), hungNote);

		return "已经成功执行挂起,并且已经通知给:" + emps;
	}
	/**
	 同意挂起

	 @return
	*/
	public final String HungupWorkAgree() throws Exception {
		if (this.getHisGenerWorkFlow().getWFState() != WFState.Hungup)
		{
			throw new RuntimeException("@非挂起状态,您不能解除挂起.");
		}

		this.getHisGenerWorkFlow().SetPara("HungupSta", HungupSta.Agree.getValue()); //同意.
		this.getHisGenerWorkFlow().SetPara("HungupChecker", WebUser.getNo());
		this.getHisGenerWorkFlow().SetPara("HungupCheckerName", WebUser.getName());
		this.getHisGenerWorkFlow().SetPara("HungupCheckRDT", DataType.getCurrentDateTime());
		this.getHisGenerWorkFlow().Update();

		//如果是按照指定的日期解除挂起.
		int way = this.getHisGenerWorkFlow().GetParaInt("HungupWay", 0);
		if (way == 1)
		{
			String relDT = this.getHisGenerWorkFlow().GetParaString("HungupRelDate");
			DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET SDT='" + relDT + "' WHERE WorkID=" + this.getWorkID() + " AND FK_Node=" + this.getHisGenerWorkFlow().getNodeID());
		}
		//删除当前的待办.
		GenerWorkerList gwl = new GenerWorkerList();
		gwl.Delete(GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.FK_Node, this.getHisGenerWorkFlow().getNodeID(), GenerWorkerListAttr.FK_Emp, WebUser.getNo());

		//更新业务表的状态.
		DBAccess.RunSQL("UPDATE " + this.getHisFlow().getPTable() + " SET WFState=" + WFState.Hungup.getValue() + " WHERE OID=" + this.getWorkID());

		return "已经同意挂起.";
	}

	/**
	 新增取消挂起的API

	 @return
	 @exception Exception
	*/
	public final String CancelHungupWork() throws Exception {
		if (this.getHisGenerWorkFlow().getWFState() != WFState.Hungup)
		{
			throw new RuntimeException("@非挂起状态,您不能取消挂起.");
		}

		/* 执行解除挂起. */
		this.getHisGenerWorkFlow().setWFState(WFState.Runing); //这里仅仅更新大的状态就好，不在处理AtPara的参数状态.
		this.getHisGenerWorkFlow().Update();

		// 更新流程报表的状态。
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps = new Paras();
		ps.SQL = "UPDATE " + this.getHisFlow().getPTable() + " SET WFState=2 WHERE OID=" + dbstr + "OID";
		ps.Add(GERptAttr.OID, this.getWorkID());
		DBAccess.RunSQL(ps);

		// 更新工作者的挂起时间。
		ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkerlist SET  DTOfUnHungup=" + dbstr + "DTOfUnHungup WHERE FK_Node=" + dbstr + "FK_Node AND WorkID=" + dbstr + "WorkID";
	ps.Add(GenerWorkerListAttr.DTOfUnHungup, DataType.getCurrentDateTime(), false);
	ps.Add(GenerWorkerListAttr.FK_Node, this.getHisGenerWorkFlow().getNodeID());
	ps.Add(GenerWorkFlowAttr.WorkID, this.getWorkID());
		DBAccess.RunSQL(ps);

		// 记录日志..
		WorkNode wn = new WorkNode(this.getWorkID(), this.getHisGenerWorkFlow().getNodeID());
		wn.AddToTrack(ActionType.UnHungup, WebUser.getNo(), WebUser.getName(), wn.getHisNode().getNodeID(), wn.getHisNode().getName(), "");
		return "您已经取消挂起，该流程实例已经进入了正常运行状态.";
	}
	/**
	 拒绝挂起 @wwh

	 @return
	*/
	public final String HungupWorkReject(String msg) throws Exception {
		if (this.getHisGenerWorkFlow().getWFState() != WFState.Hungup)
		{
			throw new RuntimeException("@非挂起状态,您不能解除挂起.");
		}

		/* 执行解除挂起. */
		this.getHisGenerWorkFlow().setWFState(WFState.Hungup);

		this.getHisGenerWorkFlow().SetPara("HungupSta", HungupSta.Reject.getValue()); //不同意.
		this.getHisGenerWorkFlow().SetPara("HungupNodeID", this.getHisGenerWorkFlow().getNodeID()); //不同意.
		this.getHisGenerWorkFlow().SetPara("HungupCheckMsg", msg); //拒绝原因.
		this.getHisGenerWorkFlow().Update();

		// 更新流程报表的状态。
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps = new Paras();
		ps.SQL = "UPDATE " + this.getHisFlow().getPTable() + " SET WFState=4 WHERE OID=" + dbstr + "OID";
		ps.Add(GERptAttr.OID, this.getWorkID());
		DBAccess.RunSQL(ps);

		// 更新工作者的挂起时间。
		ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkerlist SET  DTOfUnHungup=" + dbstr + "DTOfUnHungup WHERE FK_Node=" + dbstr + "FK_Node AND WorkID=" + dbstr + "WorkID";
	ps.Add(GenerWorkerListAttr.DTOfUnHungup, DataType.getCurrentDateTime(), false);
	ps.Add(GenerWorkerListAttr.FK_Node, this.getHisGenerWorkFlow().getNodeID());
	ps.Add(GenerWorkFlowAttr.WorkID, this.getWorkID());
		DBAccess.RunSQL(ps);


		/* 获取它的工作者，向他们发送消息。*/
		GenerWorkerLists wls = new GenerWorkerLists(this.getWorkID());
		wls.Retrieve(GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.FK_Node, this.getHisGenerWorkFlow().getNodeID(), null);

		//String url = Glo.ServerIP + "/" + this.VirPath + this.AppType + "/MyFlow.htm?FK_Flow=" + this.HisFlow.getNo() + "&WorkID=" + this.WorkID + "&FID=" + this.HisGenerWorkFlow.FID + "&FK_Node=" + this.HisGenerWorkFlow.NodeID;
		//String mailDoc = "详细信息:<A href='" + url + "'>打开流程</A>.";
		//mailDoc += " 拒绝原因:" + msg;
		String title = "工作:" + this.getHisGenerWorkFlow().getTitle() + " 被" + WebUser.getName() + "拒绝挂起.";
		String emps = "";
		for (GenerWorkerList wl : wls.ToJavaList())
		{
			if (wl.getItIsEnable() == false)
			{
				continue; //不发送给禁用的人。
			}

			emps += wl.getEmpNo() + "," + wl.getEmpName() + ";";

			//写入消息。
			bp.wf.Dev2Interface.Port_SendMsg(wl.getEmpNo(), title, msg, "RejectHungup" + wl.getNodeID() + this.getWorkID(), bp.wf.SMSMsgType.RejectHungup, getHisGenerWorkFlow().getFlowNo(), getHisGenerWorkFlow().getNodeID(), this.getWorkID(), this.getFID());
		}

		// 记录日志..
		WorkNode wn = new WorkNode(this.getWorkID(), this.getHisGenerWorkFlow().getNodeID());
		wn.AddToTrack(ActionType.UnHungup, WebUser.getNo(), WebUser.getName(), wn.getHisNode().getNodeID(), wn.getHisNode().getName(), "拒绝挂起，通知给:" + emps);

		//删除当前的待办(当前人是，审核人.)
		GenerWorkerList gwl = new GenerWorkerList();
		gwl.Delete(GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.FK_Node, this.getHisGenerWorkFlow().getNodeID(), GenerWorkerListAttr.FK_Emp, WebUser.getNo());


		//更新业务表的状态.
		DBAccess.RunSQL("UPDATE " + this.getHisFlow().getPTable() + " SET WFState=" + WFState.Runing.getValue() + " WHERE OID=" + this.getWorkID());

		return "您不同意对方挂起，该流程实例已经进入了正常运行状态.";
	}

		///#endregion
}
