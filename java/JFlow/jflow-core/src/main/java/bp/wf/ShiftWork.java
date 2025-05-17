package bp.wf;

import bp.sys.*;
import bp.tools.DateUtils;
import bp.web.*;
import bp.port.*;
import bp.da.*;
import bp.wf.data.CHNode;
import bp.wf.template.*;
import java.util.*;

public class ShiftWork
{
	/**
	 工作移交
	 @param fromUser 当事人
	 @param workID 工作ID
	 @param toEmp 要移交的人
	 @param msg 移交信息
	 @return 执行结果
	*/
	public static String Node_Shift_ToEmp(String fromUser, long workID, String toEmp, String msg) throws Exception {
		//移交人
		if(DataType.IsNullOrEmpty(fromUser) == true)
			fromUser = WebUser.getNo();

		if (toEmp.equals(fromUser) == true)
		{
			throw new RuntimeException("err@您不能移交给您自己。");
		}

		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		if (gwf.getWFSta() == WFSta.Complete)
		{
			throw new RuntimeException("err@流程已经完成，您不能执行移交了。");
		}

		int i = 0;
		//人员.
		Emp emp = new Emp(toEmp);
		Node nd = new Node(gwf.getNodeID());
		Work work = nd.getHisWork();
		work.setOID(workID);
		work.RetrieveFromDBSources();

		Date dtOfShould = new Date(); // LocalDateTime.MIN;
		//增加天数. 考虑到了节假日.
		if (nd.getHisCHWay() == CHWay.ByTime) {
			//按天、小时考核
			if (nd.GetParaInt("CHWayOfTimeRole", 0) == 0) {
				//增加天数. 考虑到了节假日.
				//判断是修改了节点期限的天数
				int timeLimit = nd.getTimeLimit();
				dtOfShould = Glo.AddDayHoursSpan(new Date(), timeLimit, nd.getTimeLimitHH(), nd.getTimeLimitMM(), nd.getTWay());
			}
			//按照节点字段设置
			if (nd.GetParaInt("CHWayOfTimeRole", 0) == 1) {
				//获取设置的字段、
				String keyOfEn = nd.GetParaString("CHWayOfTimeRoleField");
				if (DataType.IsNullOrEmpty(keyOfEn) == true) {
					nd.setHisCHWay(CHWay.None);
				} else {
					dtOfShould = DataType.ParseSysDateTime2DateTime(work.GetValByKey(keyOfEn).toString());
				}
			}
		}

		// 应完成日期.
		String sdt = nd.getHisCHWay() == CHWay.None ? "无" : DataType.SysDateTimeFormat(dtOfShould);

		//检查被移交人是否在当前的待办列表里否？
		GenerWorkerList gwl = new GenerWorkerList();
		i = gwl.Retrieve(GenerWorkerListAttr.FK_Emp, emp.getUserID(), GenerWorkerListAttr.FK_Node, nd.getNodeID(), GenerWorkerListAttr.WorkID, workID);
		if (i == 1)
		{
			return "err@移交失败，您所移交的人员(" + emp.getUserID() + " " + emp.getName() + ")已经在代办列表里.";
		}
		Emp femp = new Emp(fromUser);
		if (nd.getTodolistModel() == TodolistModel.Order || nd.getTodolistModel() == TodolistModel.Teamup || nd.getTodolistModel() == TodolistModel.TeamupGroupLeader)
		{
			/*如果是队列模式，或者是协作模式, 就直接把自己的gwl 信息更新到被移交人身上. */
			//把自己的待办更新到被移交人身上.
			gwl = new GenerWorkerList();
			int myNum = gwl.Retrieve(GenerWorkerListAttr.FK_Emp,fromUser,GenerWorkerListAttr.FK_Node,gwf.getNodeID(),GenerWorkerListAttr.WorkID,workID);

			//String sql = "UPDATE WF_GenerWorkerlist SET IsRead=0, FK_Emp='" + emp.getUserID() + "', EmpName='" + emp.getName() + "' WHERE FK_Emp='" + fromUser + "' AND FK_Node=" + gwf.getNodeID() + " AND WorkID=" + workID;
			//int myNum = DBAccess.RunSQL(sql);

			if(myNum == 1){
				gwl.Delete();
				gwl.setItIsRead(false);
				gwl.setEmpNo(emp.getUserID());
				gwl.setEmpName(emp.getName());
				gwl.setSDT(sdt);
				gwl.setRDT(DataType.getCurrentDateTimess());
				gwl.setSender(femp.getUserID() + "," + femp.getName());
				gwl.Insert();
			}
				///#region 判断是否是,admin的移交.
			if (myNum == 0)
			{
				//说明移交人是 admin，执行的.
				GenerWorkerLists mygwls = new GenerWorkerLists();
				mygwls.Retrieve(GenerWorkerListAttr.WorkID, workID, GenerWorkerListAttr.FK_Node, gwf.getNodeID(), null);
				if (mygwls.size() == 0)
				{
					throw new RuntimeException("err@系统错误，没有找到待办.");
				}

				//把他们都删除掉.
				mygwls.Delete(GenerWorkerListAttr.WorkID, workID, GenerWorkerListAttr.FK_Node, gwf.getNodeID());
				//取出来第1个，把人员信息改变掉.
				for (GenerWorkerList item : mygwls.ToJavaList())
				{
					item.setEmpNo(femp.getNo());
					item.setEmpName(femp.getName());

					item.setDeptNo(femp.getDeptNo());
					item.setDeptName(femp.getDeptText());

					item.setItIsRead(false);
					item.setSDT(sdt);
					item.setRDT(DataType.getCurrentDateTimess());
					item.setSender(WebUser.getNo() + "," + WebUser.getName());
					item.Insert(); //执行插入.
					break;
				}
			}

				///#endregion 判断是否是,admin的移交.

			//记录日志.
			Glo.AddToTrack(ActionType.Shift, nd.getFlowNo(), workID, gwf.getFID(), nd.getNodeID(), nd.getName(), WebUser.getNo(), WebUser.getName(), nd.getNodeID(), nd.getName(), toEmp, emp.getName(), msg, null);

			//移交后事件
			String atPara1 = "@SendToEmpIDs=" + emp.getUserID();
			String info = "@" + ExecEvent.DoNode(EventListNode.ShitAfter, nd, work, null, atPara1);
			if(info == null || info.equals("@null")) info="";
			//处理移交后发送的消息事件,发送消息.
			PushMsgs pms1 = new PushMsgs();
			pms1.Retrieve(PushMsgAttr.NodeID, nd.getNodeID(), PushMsgAttr.EventNo, EventListNode.ShitAfter, null);
			for (PushMsg pm : pms1.ToJavaList())
			{
				pm.DoSendMessage(nd, nd.getHisWork(), null, null, null, emp.getUserID());
			}

			if(gwf.getWFState()!=WFState.Runing)
				gwf.SetPara("WFState",gwf.getWFState().getValue());
			gwf.setWFState(WFState.Shift);
			//gwf.setTodoEmpsNum(1);
			gwf.setTodoEmps(gwf.getTodoEmps().replace(femp.getUserID()+","+femp.getName()+";","")+ emp.getUserID() + "," + emp.getName() + ";");
			gwf.Update();
			return "info@工作移交成功。@您已经成功的把工作移交给：" + emp.getUserID() + " , " + emp.getName() + info;
		}

		//非协作模式.
		gwl = new GenerWorkerList();
		gwl.Retrieve(GenerWorkerListAttr.FK_Node, gwf.getNodeID(), GenerWorkerListAttr.WorkID, gwf.getWorkID(), GenerWorkerListAttr.FK_Emp, fromUser);
		gwl.Delete();
		gwl.setEmpNo(emp.getUserID());
		gwl.setEmpName(emp.getName());
		gwl.setItIsEnable(true);
		gwl.setSDT(sdt);
		gwl.setRDT(DataType.getCurrentDateTimess());
		gwl.setSender(fromUser + "," + femp.getName());


		gwl.Insert();
		if(gwf.getWFState()!=WFState.Runing)
			gwf.SetPara("WFState",gwf.getWFState().getValue());
		if(nd.getItIsStartNode() == true){
			gwf.setStarter(emp.getUserID());
			gwf.setStarterName(emp.getName());
		}
		gwf.setWFState(WFState.Shift);
		//gwf.setTodoEmpsNum(1);
		gwf.setTodoEmps(gwf.getTodoEmps().replace(femp.getUserID()+","+femp.getName()+";","")+ emp.getUserID() + "," + emp.getName() + ";");
		gwf.Update();

		//记录日志.
		Glo.AddToTrack(ActionType.Shift, nd.getFlowNo(), workID, gwf.getFID(), nd.getNodeID(), nd.getName(), femp.getUserID(), femp.getName(), nd.getNodeID(), nd.getName(), toEmp, emp.getName(), msg, null);

		String inf1o = "@工作移交成功。@您已经成功的把工作移交给：" + emp.getUserID() + " , " + emp.getName();
		//移交后事件
		String atPara = "@SendToEmpIDs=" + emp.getUserID();
		WorkNode wn = new WorkNode(work, nd);
		String result = ExecEvent.DoNode(EventListNode.ShitAfter, wn, null, atPara);
		if(DataType.IsNullOrEmpty(result) == false)
			inf1o += "@" + result;
		return inf1o;
	}
	/**
	 工作移交
	 @param fromUser 当事人
	 @param workID 工作ID
	 @param toEmps 要移交的多个人,比如:zhangsan,lisi
	 @param msg
	 @return 执行信息.err@说明执行错误.
	*/
	public static String Node_Shift_ToEmps(String fromUser, long workID, String toEmps, String msg) throws Exception {
		//移交人
		if(DataType.IsNullOrEmpty(fromUser) == true)
			fromUser = WebUser.getNo();

		if (toEmps.equals(fromUser) == true)
		{
			throw new RuntimeException("err@您不能移交给您自己。");
		}
		Emp femp = new Emp(fromUser);
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		if (gwf.getWFSta() == WFSta.Complete)
		{
			throw new RuntimeException("err@流程已经完成，您不能执行移交了。");
		}

		//定义变量,查询出来当前的人员列表.
		GenerWorkerLists gwls = new GenerWorkerLists();
		gwls.Retrieve(GenerWorkerListAttr.FK_Node, gwf.getNodeID(), GenerWorkerListAttr.WorkID, workID, null);
		//定义变量.
		GenerWorkerList gwl = new GenerWorkerList();

		int i = 0;
		//人员.
		Node nd = new Node(gwf.getNodeID());
		Work work = nd.getHisWork();
		work.setOID(workID);
		work.RetrieveFromDBSources();

		Date dtOfShould = new Date(); // LocalDateTime.MIN;
		//增加天数. 考虑到了节假日.
		if (nd.getHisCHWay() == CHWay.ByTime) {
			//按天、小时考核
			if (nd.GetParaInt("CHWayOfTimeRole", 0) == 0) {
				//增加天数. 考虑到了节假日.
				//判断是修改了节点期限的天数
				int timeLimit = nd.getTimeLimit();
				dtOfShould = Glo.AddDayHoursSpan(new Date(), timeLimit, nd.getTimeLimitHH(), nd.getTimeLimitMM(), nd.getTWay());
			}
			//按照节点字段设置
			if (nd.GetParaInt("CHWayOfTimeRole", 0) == 1) {
				//获取设置的字段、
				String keyOfEn = nd.GetParaString("CHWayOfTimeRoleField");
				if (DataType.IsNullOrEmpty(keyOfEn) == true) {
					nd.setHisCHWay(CHWay.None);
				} else {
					dtOfShould = DataType.ParseSysDateTime2DateTime(work.GetValByKey(keyOfEn).toString());
				}
			}
		}
		// 应完成日期.
		String sdt = "";
		if (nd.getHisCHWay() == CHWay.None)
			sdt = "无";
		else
			sdt = DataType.SysDateTimeFormat(dtOfShould);

		String info = null;
		String[] strs = toEmps.split("[,]", -1);
		String empNames = "";
		for (String toEmp : strs)
		{
			Emp emp = new Emp(toEmp);
			//检查被移交人是否在当前的待办列表里否？
			i = gwl.Retrieve(GenerWorkerListAttr.FK_Emp, emp.getUserID(), GenerWorkerListAttr.FK_Node, nd.getNodeID(), GenerWorkerListAttr.WorkID, workID);
			if (i == 1)
			{
				info += "err@移交失败，您所移交的人员(" + emp.getUserID() + " " + emp.getName() + ")已经在代办列表里.";
				continue;
			}

			empNames+=emp.getName()+",";
			if (nd.getTodolistModel() == TodolistModel.Order || nd.getTodolistModel() == TodolistModel.Teamup || nd.getTodolistModel() == TodolistModel.TeamupGroupLeader)
			{
				/*如果是队列模式，或者是协作模式, 就直接把自己的gwl 信息更新到被移交人身上. */

				//写入移交数据.
				gwl = (GenerWorkerList)gwls.get(0);
				gwl.setEmpNo(emp.getUserID());
				gwl.setEmpName(emp.getName());
				gwl.setPassInt(0);
				gwl.setSDT(sdt);
				gwl.setRDT(DataType.getCurrentDateTimess());
				gwl.setSender(femp.getUserID() + "," + femp.getName());
				gwl.Insert();

				//记录日志.
				//Glo.AddToTrack(ActionType.Shift, nd.getFlowNo(), workID, gwf.getFID(), nd.getNodeID(), nd.getName(), WebUser.getNo(), WebUser.getName(), nd.getNodeID(), nd.getName(), toEmp, emp.getName(), msg, null);

				//移交后事件
				String atPara1 = "@SendToEmpIDs=" + emp.getUserID();
				info += "@" + ExecEvent.DoNode(EventListNode.ShitAfter, nd, work, null, atPara1);
				String result = ExecEvent.DoNode(EventListNode.ShitAfter, nd, work, null, atPara1);
				if(DataType.IsNullOrEmpty(result) == false)
					info += "@" + result;


				//处理移交后发送的消息事件,发送消息.
				PushMsgs pms1 = new PushMsgs();
				pms1.Retrieve(PushMsgAttr.NodeID, nd.getNodeID(), PushMsgAttr.EventNo, EventListNode.ShitAfter, null);
				for (PushMsg pm : pms1.ToJavaList())
				{
					pm.DoSendMessage(nd, nd.getHisWork(), null, null, null, emp.getUserID());
				}

				info += "info@成功移交给:" + emp.getUserID() + "," + emp.getName();
				continue;
			}

			//非协作模式.
			//写入移交数据.
			gwl = (GenerWorkerList)gwls.get(0);
			gwl.setEmpNo(emp.getUserID());
			gwl.setEmpName(emp.getName());
			gwl.setPassInt(0);
			gwl.setSDT(sdt);
			gwl.setRDT(DataType.getCurrentDateTimess());
			gwl.setSender(femp.getUserID() + "," + femp.getName());

			gwl.Insert();
			info += "info@成功移交给:" + emp.getUserID() + "," + emp.getName();
		}

		//重新查询.
		gwls.Retrieve(GenerWorkerListAttr.FK_Node, gwf.getNodeID(), GenerWorkerListAttr.WorkID, workID, null);

		//工作处理人员.
		String todoEmps = "";
		for (GenerWorkerList mygwl : gwls.ToJavaList())
		{
			todoEmps += mygwl.getEmpNo() + "," + mygwl.getEmpName() + ";";
		}

		//更新主表信息.
		if(gwf.getWFState()!=WFState.Runing)
			gwf.SetPara("WFState",gwf.getWFState().getValue());
		gwf.setWFState(WFState.Shift);
		gwf.setTodoEmpsNum(gwls.size());
		gwf.setTodoEmps(todoEmps); //处理人员.
		gwf.Update();

		//删除自己的待办.
		DBAccess.RunSQL("DELETE FROM WF_GenerWorkerList WHERE WorkID=" + gwf.getWorkID() + " AND FK_Node=" + gwf.getNodeID() + " AND FK_Emp='" + fromUser + "'");

		if(empNames.endsWith(","))
			empNames = empNames.substring(0,empNames.length()-1);
		//记录日志.
		Glo.AddToTrack(ActionType.Shift, nd.getFlowNo(), workID, gwf.getFID(), nd.getNodeID(), nd.getName(), fromUser, femp.getName(), nd.getNodeID(), nd.getName(), toEmps, empNames, msg, null);

		//移交后事件.
		String atPara = "@SendToEmpIDs=" + toEmps;
		WorkNode wn = new WorkNode(work, nd);
		String result = ExecEvent.DoNode(EventListNode.ShitAfter, wn, null, atPara);
		if(DataType.IsNullOrEmpty(result) == false)
			info += "@" + result;
		return info;
	}
	/**
	 撤消移交

	 @return
	*/
	public static String DoUnShift(long workid) throws Exception {

		GenerWorkFlow gwf = new GenerWorkFlow(workid);
		GenerWorkerLists wls = new GenerWorkerLists();
		wls.Retrieve(GenerWorkerListAttr.WorkID, workid, GenerWorkerListAttr.FK_Node, gwf.getNodeID(), null);
		if (wls.size() == 0)
		{
			return "移交失败没有当前的工作。";
		}

		Node nd = new Node(gwf.getNodeID());
		Work wk1 = nd.getHisWork();
		wk1.setOID(workid);
		wk1.Retrieve();

		// 记录日志.
		WorkNode wn = new WorkNode(wk1, nd);
		wn.AddToTrack(ActionType.UnShift, WebUser.getNo(), WebUser.getName(), nd.getNodeID(), nd.getName(), "撤消移交");

		//删除撤销信息.
		//DBAccess.RunSQL("DELETE FROM WF_ShiftWork WHERE WorkID=" + workid + " AND FK_Node=" + gwf.getNodeID());

		//更新流程主表字段信息
		gwf.setWFState(WFState.Runing);
		//gwf.Update();
		String sql="";
		if (wls.size() == 1)
		{
			GenerWorkerList wl = (GenerWorkerList)wls.get(0);
			sql = "UPDATE WF_GenerWorkerList SET " +
					"FK_Emp = '" + WebUser.getNo() + "', " +
					"FK_EmpText = '" + WebUser.getName() + "', " +
					"IsEnable = 1, " +
					"IsPass = 0 " +
					" WHERE WorkID = " + workid + " AND FK_Emp = '" + wl.getEmpNo() + "' AND FK_Node = " + gwf.getNodeID();
			DBAccess.RunSQL(sql);
			gwf.setTodoEmps(WebUser.getNo() + "," + WebUser.getName() + ";");
			gwf.Update();
			return "@撤消移交成功。";
		}

		GenerWorkerList mywl = null;
		//获取移交的人员信息
		String trackTable = "ND" + Integer.parseInt(gwf.getFlowNo()) + "Track";
		sql = "SELECT EmpTo From " + trackTable + " WHERE ActionType=3 AND WorkID=" + gwf.getWorkID() + " AND NDFrom=" + gwf.getNodeID() + " AND EmpFrom='" + WebUser.getNo() + "'";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		for(DataRow dr : dt.Rows){
			for (GenerWorkerList wl : wls.ToJavaList())
			{
				if (wl.getEmpNo().equals(WebUser.getNo()))
				{
					mywl = wl;
					wl.Delete();
					continue;
				}
				if (wl.getEmpNo().equals(dr.getValue(0).toString()))
				{
					mywl = wl;
					wl.Delete();
					gwf.setTodoEmps(gwf.getTodoEmps().replace(wl.getEmpNo() + "," + wl.getEmpName() + ";", ""));
					break;
				}
			}
		}

		if (mywl == null)
			return "err@撤消移交失败，没有找到移交人的待办";
		gwf.setTodoEmps(gwf.getTodoEmps()+WebUser.getNo() + "," + WebUser.getName() + ";");
		gwf.Update();
		GenerWorkerList wkNew = new GenerWorkerList();
		wkNew.Copy(mywl);
		wkNew.setEmpNo(WebUser.getNo());
		wkNew.setEmpName(WebUser.getName());
		wkNew.setItIsEnable(true);
		wkNew.setItIsPass(false);
		wkNew.Insert();
		return "@撤消移交成功";
	}
}
