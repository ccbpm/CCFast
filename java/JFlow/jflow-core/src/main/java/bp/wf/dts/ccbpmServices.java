package bp.wf.dts;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.port.*;
import bp.sys.*;
import bp.wf.template.*;
import bp.web.*;
import bp.difference.*;
import bp.*;
import bp.wf.*;
import java.util.*;
import java.time.*;

/** 
 ccbpm服务
*/
public class ccbpmServices extends Method
{
	/** 
	 ccbpm服务
	*/
	public ccbpmServices()
	{
		this.Title = "ccbpm流程服务 ";
		this.Help = "1,自动发送邮件. 2,自动发起流程. 3,自动执行节点任务..";
		this.Help += "所有自动任务的综合.";

		this.GroupName = "流程自动执行定时任务";
	}
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
	 开始执行方法.
	 
	 @return 
	*/
	@Override
	public Object Do() throws Exception {

		Dev2Interface.Port_Login("admin");

		//自动发起流程.
		AutoRunStratFlows fls = new AutoRunStratFlows();
		fls.Do();

		//执行自动任务,机器执行的节点.
		AutoRun_WhoExeIt myen = new AutoRun_WhoExeIt();
		myen.Do();

		//扫描触发式自动发起流程表.
		//自动发起流程.
		AutoRunWF_Task wf_task = new AutoRunWF_Task();
		wf_task.Do();


		//扫描消息表,想外发送消息.
		DoSendMsg();

		//扫描逾期流程数据，处理逾期流程.
		DTS_DealDeferredWork en = new DTS_DealDeferredWork();
		en.Do();

		//同步待办时间戳
		DTS_GenerWorkFlowTimeSpan en2 = new DTS_GenerWorkFlowTimeSpan();
		en2.Do();

		//更新WF_GenerWorkerFlow.TodoSta状态.
		DTS_GenerWorkFlowTodoSta en3 = new DTS_GenerWorkFlowTodoSta();
		en3.Do();

		//执行自动任务.
		Auto_Rpt_Dtl_DTS dtRpts = new Auto_Rpt_Dtl_DTS();
		dtRpts.Do();

		return "执行完成...";
	}
	/** 
	 逾期流程
	*/
	private void DoOverDueFlow() throws Exception {
		//特殊处理天津的需求.
		if (Objects.equals(SystemConfig.getCustomerNo(), ""))
		{
			DoTianJinSpecFunc();
		}

			///#region  流程逾期
		//判断是否有流程逾期的消息设置
		DataTable dt = null;
		String sql = "SELECT a.FK_Flow,a.WorkID,a.Title,a.FK_Node,a.SDTOfNode,a.Starter,a.TodoEmps ";
		sql += "FROM WF_GenerWorkFlow a, WF_Node b";
		sql += " WHERE a.SDTOfFlow<='" + DataType.getCurrentDateTime() + "' ";
		sql += " AND WFState=2 and b.OutTimeDeal!=0";
		sql += " AND a.FK_Node=b.NodeID";
		dt = DBAccess.RunSQLReturnTable(sql);
		// 遍历循环,逾期表进行处理.
		for (DataRow dr : dt.Rows)
		{
			String fk_flow = dr.getValue("FK_Flow") + "";
			int fk_node = Integer.parseInt(dr.getValue("FK_Node") + "");
			long workid = Long.parseLong(dr.getValue("WorkID") + "");
			String title = dr.getValue("Title") + "";

			//判断流程是否设置逾期消息
			PushMsg pushMsg = new PushMsg();
			int count = pushMsg.Retrieve(PushMsgAttr.FlowNo, fk_flow, PushMsgAttr.NodeID, 0, PushMsgAttr.EventNo, EventListNode.FlowOverDue);
			if (count != 0)
			{
				Node node = new Node(fk_node);
				pushMsg.DoSendMessage(node, node.getHisWork(), null, null, null, null);
			}
			continue;
		}

			///#endregion 流程逾期


			///#region  流程预警
		sql = "SELECT a.FK_Flow,a.WorkID,a.Title,a.FK_Node,a.SDTOfNode,a.Starter,a.TodoEmps ";
		sql += "FROM WF_GenerWorkFlow a, WF_Node b";
		sql += " WHERE a.SDTOfFlowWarning<='" + DataType.getCurrentDateTime() + "' ";
		sql += " AND WFState=2 and b.OutTimeDeal!=0";
		sql += " AND a.FK_Node=b.NodeID";
		dt = DBAccess.RunSQLReturnTable(sql);
		// 遍历循环,预警表进行处理.
		for (DataRow dr : dt.Rows)
		{
			String fk_flow = dr.getValue("FK_Flow") + "";
			int fk_node = Integer.parseInt(dr.getValue("FK_Node") + "");
			long workid = Long.parseLong(dr.getValue("WorkID") + "");
			String title = dr.getValue("Title") + "";
			//判断流程是否设置逾期消息
			PushMsg pushMsg = new PushMsg();
			int count = pushMsg.Retrieve(PushMsgAttr.FlowNo, fk_flow, PushMsgAttr.NodeID, 0, PushMsgAttr.EventNo, EventListNode.FlowWarning);
			if (count != 0)
			{
				Node node = new Node(fk_node);
				pushMsg.DoSendMessage(node, node.getHisWork(), null, null, null, null);
			}
			continue;
		}

			///#endregion  流程预警
		DataTable generTab = null;


			///#region 节点预警
		sql = "SELECT a.FK_Flow,a.WorkID,a.Title,a.FK_Node,a.SDTOfNode,a.Starter,a.TodoEmps ";
		sql += "FROM WF_GenerWorkFlow a, WF_Node b";
		sql += " WHERE a.SDTOfNode>='" + DataType.getCurrentDateTime() + "' ";
		sql += " AND WFState=2 and b.OutTimeDeal!=0";
		sql += " AND a.FK_Node=b.NodeID";
		generTab = DBAccess.RunSQLReturnTable(sql);
		for (DataRow row : generTab.Rows)
		{
			String fk_flow = row.get("FK_Flow") + "";
			int fk_node = Integer.parseInt(row.get("FK_Node") + "");
			long workid = Long.parseLong(row.get("WorkID") + "");
			String title = row.get("Title") + "";
			String compleateTime = row.get("SDTOfNode") + "";
			String starter = row.get("Starter") + "";
			Node node = new Node(fk_node);
			if (node.getItIsStartNode())
			{
				continue;
			}
			PushMsgs pushMsgs = new PushMsgs();
			int count = pushMsgs.Retrieve(PushMsgAttr.FlowNo, node.getFlowNo(), PushMsgAttr.NodeID, node.getNodeID(), PushMsgAttr.EventNo, EventListNode.NodeWarning, null);
			int maxHour = 0;
			int minHour = 0;
			if (count != 0)
			{
				for (PushMsg pushMsg : pushMsgs.ToJavaList())
				{
					if (pushMsg.GetParaInt("NoticeType", 0) == 0)
					{
						pushMsg.DoSendMessage(node, node.getHisWork(), null, null, null, null);
						continue;
					}
					else
					{
						if (pushMsg.GetParaInt("NoticeHour", 0) > maxHour)
						{
							maxHour = pushMsg.GetParaInt("NoticeHour", 0);
						}
						if (pushMsg.GetParaInt("NoticeHour", 0) < minHour)
						{
							minHour = pushMsg.GetParaInt("NoticeHour", 0);
						}
					}
				}

				//计算当天时间和节点应完成日期的时间差
				int hours = DataType.SpanDays(compleateTime);
				int noticeHour = 0;
				if (hours > minHour) //如果小于最新提醒天数则不发消息
				{
					for (PushMsg pushMsg : pushMsgs.ToJavaList())
					{
						if (pushMsg.GetParaInt("NoticeType", 0) == 1)
						{
							noticeHour = pushMsg.GetParaInt("NoticeHour", 0);
							if (noticeHour == hours)
							{
								pushMsg.DoSendMessage(node, node.getHisWork(), null, null, null, null);
								continue;
							}
						}
					}
				}

			}
		}

			///#endregion  节点预警


			///#region 找到要节点逾期的数据.

		sql = "SELECT a.FK_Flow,a.WorkID,a.Title,a.FK_Node,a.SDTOfNode,a.Starter,a.TodoEmps ";
		sql += "FROM WF_GenerWorkFlow a, WF_Node b";
		sql += " WHERE a.SDTOfNode<='" + DataType.getCurrentDateTime() + "' ";
		sql += " AND WFState=2 and b.OutTimeDeal!=0";
		sql += " AND a.FK_Node=b.NodeID";
		generTab = DBAccess.RunSQLReturnTable(sql);


			///#endregion 找到要逾期的数据.

		// 遍历循环,逾期表进行处理.
		String msg = "";
		String info = "";
		for (DataRow row : generTab.Rows)
		{
			String fk_flow = row.get("FK_Flow") + "";
			int fk_node = Integer.parseInt(row.get("FK_Node") + "");
			long workid = Long.parseLong(row.get("WorkID") + "");
			String title = row.get("Title") + "";
			String compleateTime = row.get("SDTOfNode") + "";
			String starter = row.get("Starter") + "";

			GenerWorkerLists gwls = new GenerWorkerLists();
			gwls.Retrieve(GenerWorkerListAttr.WorkID, workid, GenerWorkerListAttr.FK_Node, fk_node, null);

			boolean isLogin = false;
			for (GenerWorkerList item : gwls.ToJavaList())
			{
				if (item.getItIsEnable() == false)
				{
					continue;
				}

				Emp emp = new Emp(item.getEmpNo());
				WebUser.SignInOfGener(emp, "CH", false, false, null, null);
				isLogin = true;
			}

			if (isLogin == false)
			{
				Emp emp = new Emp("admin");
				WebUser.SignInOfGener(emp, "CH", false, false, null, null);
			}

			try
			{
				Node node = new Node(fk_node);
				if (node.getItIsStartNode())
				{
					continue;
				}

					///#region 启动逾期消息设置
				PushMsgs pushMsgs = new PushMsgs();
				int count = pushMsgs.Retrieve(PushMsgAttr.FlowNo, node.getFlowNo(), PushMsgAttr.NodeID, node.getNodeID(), PushMsgAttr.EventNo, EventListNode.NodeOverDue, null);
				int maxDay = 0;
				int minDay = 0;
				if (count != 0)
				{
					for (PushMsg pushMsg : pushMsgs.ToJavaList())
					{
						if (pushMsg.GetParaInt("NoticeType", 0) == 0)
						{
							pushMsg.DoSendMessage(node, node.getHisWork(), null, null, null, null);
							continue;
						}
						else
						{
							if (pushMsg.GetParaInt("NoticeDay", 0) > maxDay)
							{
								maxDay = pushMsg.GetParaInt("NoticeDay", 0);
							}
							if (pushMsg.GetParaInt("NoticeDay", 0) < minDay)
							{
								minDay = pushMsg.GetParaInt("NoticeDay", 0);
							}
						}
					}

					//计算当天时间和节点应完成日期的时间差
					int days = DataType.SpanDays(DataType.getCurrentDate(), compleateTime);
					int noticeDay = 0;
					if (days > minDay) //如果小于最新提醒天数则不发消息
					{
						for (PushMsg pushMsg : pushMsgs.ToJavaList())
						{
							if (pushMsg.GetParaInt("NoticeType", 0) == 1)
							{
								noticeDay = pushMsg.GetParaInt("NoticeDay", 0);
								if (noticeDay == days || days > maxDay)
								{
									pushMsg.DoSendMessage(node, node.getHisWork(), null, null, null, null);
									continue;
								}
							}
						}
					}

				}

					///#endregion 启动逾期消息设置

				//获得该节点的处理内容.
				String doOutTime = node.GetValStrByKey(NodeAttr.DoOutTime);
				switch (node.getHisOutTimeDeal())
				{
					case None: //逾期不处理.
						continue;
					case AutoJumpToSpecNode: //跳转到指定的节点.
						try
						{
							int jumpNode = Integer.parseInt(doOutTime);
							Node jumpToNode = new Node(jumpNode);

							//设置默认同意.
							Dev2Interface.Node_WriteWorkCheck(workid, "同意（预期自动审批）", null, null);

							//执行发送.
							info = Dev2Interface.Node_SendWork(fk_flow, workid, null, null, jumpToNode.getNodeID(), null).ToMsgOfText();

							msg = "流程 '" + node.getFlowName() + "',标题: '" + title + "'的应该完成时间为'" + compleateTime + "',当前节点'" + node.getName() + "'超时处理规则为'自动跳转'," + info;

							Log.DebugWriteInfo(msg);

						}
						catch (RuntimeException ex)
						{
							msg = "流程 '" + node.getFlowName() + "',WorkID=" + workid + ",标题: '" + title + "'的应该完成时间为'" + compleateTime + "',当前节点'" + node.getName() + "'超时处理规则为'自动跳转',跳转异常:" + ex.getMessage();
							Log.DebugWriteError(msg);
						}
						break;
					case AutoShiftToSpecUser: //走动移交给.
						// 判断当前的处理人是否是.
						Emp empShift = new Emp(doOutTime);
						try
						{
							Dev2Interface.Node_Shift(workid, empShift.getUserID(), "流程节点已经逾期,系统自动移交");

							msg = "流程 '" + node.getFlowName() + "',标题: '" + title + "'的应该完成时间为'" + compleateTime + "',当前节点'" + node.getName() + "'超时处理规则为'移交到指定的人',已经自动移交给'" + empShift.getName() + ".";
							Log.DebugWriteInfo(msg);
						}
						catch (RuntimeException ex)
						{
							msg = "流程 '" + node.getFlowName() + "' ,标题:'" + title + "'的应该完成时间为'" + compleateTime + "',当前节点'" + node.getName() + "'超时处理规则为'移交到指定的人',移交异常：" + ex.getMessage();
							Log.DebugWriteError(msg);
						}
						break;
					case AutoTurntoNextStep:
						try
						{
							GenerWorkerList workerList = new GenerWorkerList();
							workerList.RetrieveByAttrAnd(GenerWorkerListAttr.WorkID, workid, GenerWorkFlowAttr.FK_Node, fk_node);

							WebUser.SignInOfGener(workerList.getHisEmp(), "CH", false, false, null, null);
							WorkNode firstwn = new WorkNode(workid, fk_node);
							String sendIfo = firstwn.NodeSend().ToMsgOfText();
							msg = "流程  '" + node.getFlowName() + "',标题: '" + title + "'的应该完成时间为'" + compleateTime + "',当前节点'" + node.getName() + "'超时处理规则为'自动发送到下一节点',发送消息为:" + sendIfo;
							Log.DebugWriteInfo(msg);
						}
						catch (RuntimeException ex)
						{
							msg = "流程  '" + node.getFlowName() + "',标题: '" + title + "'的应该完成时间为'" + compleateTime + "',当前节点'" + node.getName() + "'超时处理规则为'自动发送到下一节点',发送异常:" + ex.getMessage();
							Log.DebugWriteError(msg);
						}
						break;
					case DeleteFlow:
						info = Dev2Interface.Flow_DoDeleteFlowByReal(workid, true);
						msg = "流程  '" + node.getFlowName() + "',标题: '" + title + "'的应该完成时间为'" + compleateTime + "',当前节点'" + node.getName() + "'超时处理规则为'删除流程'," + info;
						Log.DebugWriteInfo(msg);
						break;
					case RunSQL:
						try
						{
							Work wk = node.getHisWork();
							wk.setOID(workid);
							wk.Retrieve();

							doOutTime = bp.wf.Glo.DealExp(doOutTime, wk, null);

							//替换字符串
							doOutTime.replace("@OID", workid + "");
							doOutTime.replace("@FK_Flow", fk_flow);
							doOutTime.replace("@FK_Node", String.valueOf(fk_node));
							doOutTime.replace("@Starter", starter);
							if (doOutTime.contains("@"))
							{
								msg = "流程 '" + node.getFlowName() + "',标题:  '" + title + "'的应该完成时间为'" + compleateTime + "',当前节点'" + node.getName() + "'超时处理规则为'执行SQL'.有未替换的SQL变量.";
								Log.DebugWriteInfo(msg);
								break;
							}

							//执行sql.
							DBAccess.RunSQL(doOutTime);
						}
						catch (RuntimeException ex)
						{
							msg = "流程  '" + node.getFlowName() + "',标题: '" + title + "'的应该完成时间为'" + compleateTime + "',当前节点'" + node.getName() + "'超时处理规则为'执行SQL'.运行SQL出现异常:" + ex.getMessage();
							Log.DebugWriteError(msg);
						}
						break;
					case SendMsgToSpecUser:
						try
						{
							Emp myemp = new Emp(doOutTime);

							boolean boo = Dev2Interface.Port_WriteToSMS(myemp.getUserID(), DataType.getCurrentDateTime(), "系统发送逾期消息", "您的流程:'" + title + "'的完成时间应该为'" + compleateTime + "',流程已经逾期,请及时处理!", "系统消息", workid);
							if (boo)
							{
								msg = "'" + title + "'逾期消息已经发送给:'" + myemp.getName() + "'";
							}
							else
							{
								msg = "'" + title + "'逾期消息发送未成功,发送人为:'" + myemp.getName() + "'";
							}
							Log.DebugWriteInfo(msg);
						}
						catch (RuntimeException ex)
						{
							msg = "流程  '" + node.getFlowName() + "',标题: '" + title + "'的应该完成时间为'" + compleateTime + "',当前节点'" + node.getName() + "'超时处理规则为'执行SQL'.运行SQL出现异常:" + ex.getMessage();
							Log.DebugWriteError(msg);
						}
						break;
					default:
						msg = "流程 '" + node.getFlowName() + "',标题: '" + title + "'的应该完成时间为'" + compleateTime + "',当前节点'" + node.getName() + "'没有找到相应的超时处理规则.";
						Log.DebugWriteError(msg);
						break;
				}
			}
			catch (RuntimeException ex)
			{
				Log.DebugWriteError(ex.toString());
			}
		}
		Log.DebugWriteInfo("结束扫描逾期流程数据.");
	}
	/** 
	 特殊处理天津的流程
	 当指定的节点，到了10号，15号自动向下发送.
	*/
	private void DoTianJinSpecFunc()
	{
		if (LocalDateTime.now().getDayOfMonth() == 10 || LocalDateTime.now().getDayOfMonth() == 15)
		{
			/* 一个是10号自动审批，一个是15号自动审批. */
		}
		else
		{
			return;
		}


			///#region 找到要逾期的数据.
		DataTable generTab = null;
		String sql = "SELECT a.FK_Flow,a.WorkID,a.Title,a.FK_Node,a.SDTOfNode,a.Starter,a.TodoEmps ";
		sql += "FROM WF_GenerWorkFlow a, WF_Node b";
		sql += " WHERE  ";
		sql += "   a.FK_Node=b.NodeID  ";

		if (LocalDateTime.now().getDayOfMonth() == 10)
		{
			sql += "   AND  b.NodeID=13304 ";
		}

		if (LocalDateTime.now().getDayOfMonth() == 15)
		{
			sql += "AND b.NodeID=13302 ";
		}

		generTab = DBAccess.RunSQLReturnTable(sql);

			///#endregion 找到要逾期的数据.

		// 遍历循环,逾期表进行处理.
		String msg = "";
		for (DataRow row : generTab.Rows)
		{
			String fk_flow = row.get("FK_Flow") + "";
			String fk_node = row.get("FK_Node") + "";
			long workid = Long.parseLong(row.get("WorkID") + "");
			String title = row.get("Title") + "";
			String compleateTime = row.get("SDTOfNode") + "";
			String starter = row.get("Starter") + "";
			try
			{
				Node node = new Node(Integer.parseInt(fk_node));

				try
				{
					GenerWorkerList workerList = new GenerWorkerList();
					workerList.RetrieveByAttrAnd(GenerWorkerListAttr.WorkID, workid, GenerWorkFlowAttr.FK_Node, fk_node);

					WebUser.SignInOfGener(workerList.getHisEmp(), "CH", false, false, null, null);

					WorkNode firstwn = new WorkNode(workid, Integer.parseInt(fk_node));
					String sendIfo = firstwn.NodeSend().ToMsgOfText();
					msg = "流程  '" + node.getFlowName() + "',标题: '" + title + "'的应该完成时间为'" + compleateTime + "',当前节点'" + node.getName() + "'超时处理规则为'自动发送到下一节点',发送消息为:" + sendIfo;

					//输出消息.
					Log.DebugWriteInfo(msg);
				}
				catch (RuntimeException ex)
				{
					msg = "流程  '" + node.getFlowName() + "',标题: '" + title + "'的应该完成时间为'" + compleateTime + "',当前节点'" + node.getName() + "'超时处理规则为'自动发送到下一节点',发送异常:" + ex.getMessage();
					Log.DebugWriteError(msg);
				}
			}
			catch (RuntimeException ex)
			{
				Log.DebugWriteError(ex.toString());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		Log.DebugWriteInfo("结束扫描逾期流程数据.");
	}
	/** 
	 发送消息
	*/
	private void DoSendMsg() throws Exception {
		int idx = 0;

			///#region 发送消息
		SMSs sms = new SMSs();
		QueryObject qo = new QueryObject(sms);
		sms.Retrieve(SMSAttr.EmailSta, MsgSta.UnRun.getValue(), null);
		for (SMS sm : sms.ToJavaList())
		{
			if (sm.getEmail().length() == 0)
			{
				sm.setHisEmailSta(MsgSta.RunOK);
				sm.Update();
				continue;
			}
			try
			{
				this.SendMail(sm);
			}
			catch (RuntimeException ex)
			{
				Log.DebugWriteError(ex.getMessage());
			}
		}

			///#endregion 发送消息
	}
	/** 
	 发送邮件。
	 
	 @param sms
	*/
	public final void SendMail(SMS sms) throws Exception {


//
//			///#region 发送邮件.
//		if (DataType.IsNullOrEmpty(sms.getEmail()))
//		{
//			Emp emp = null; // new BP.WF.Port.WFEmp(sms.SendToEmpNo);
//			if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
//			{
//				emp = new Emp(WebUser.getOrgNo() + "_" + sms.getSendToEmpNo());
//			}
//			else
//			{
//				emp = new Emp(sms.getSendToEmpNo());
//			}
//			sms.setEmail(emp.getEmail());
//		}
//
//		System.Net.Mail.MailMessage myEmail = new System.Net.Mail.MailMessage();
//		myEmail.From = new MailAddress("ccbpmtester@tom.com", "ccbpm123", System.Text.Encoding.UTF8);
//
//		myEmail.To.Add(sms.getEmail());
//		myEmail.Subject = sms.getTitle();
//		myEmail.SubjectEncoding = System.Text.Encoding.UTF8; //邮件标题编码
//
//		myEmail.Body = sms.getDocOfEmail();
//		myEmail.BodyEncoding = System.Text.Encoding.UTF8; //邮件内容编码
//		myEmail.IsBodyHtml = true; //是否是HTML邮件
//
//		myEmail.Priority = MailPriority.High; //邮件优先级
//
//		SmtpClient client = new SmtpClient();
//
//		//邮件地址.
//		String emailAddr = SystemConfig.GetValByKey("SendEmailAddress", null);
//		if (emailAddr == null)
//		{
//			emailAddr = "ccbpmtester@tom.com";
//		}
//
//		String emailPassword = SystemConfig.GetValByKey("SendEmailPass", null);
//		if (emailPassword == null)
//		{
//			emailPassword = "ccbpm123";
//		}
//
//		//是否启用ssl?
//		boolean isEnableSSL = false;
//		String emailEnableSSL = SystemConfig.GetValByKey("SendEmailEnableSsl", null);
//		if (emailEnableSSL == null || Objects.equals(emailEnableSSL, "0"))
//		{
//			isEnableSSL = false;
//		}
//		else
//		{
//			isEnableSSL = true;
//		}
//
//		client.Credentials = new System.Net.NetworkCredential(emailAddr, emailPassword);
//
//		//上述写你的邮箱和密码
//		client.Port = SystemConfig.GetValByKeyInt("SendEmailPort", 25); //使用的端口
//		client.Host = SystemConfig.GetValByKey("SendEmailHost", "smtp.tom.com");
//
//		//是否启用加密,有的邮件服务器发送配置不成功就是因为此参数的错误。
//		client.EnableSsl = SystemConfig.GetValByKeyBoolen("SendEmailEnableSsl", isEnableSSL);
//
//		Object userState = myEmail;
//		try
//		{
//			client.SendAsync(myEmail, userState);
//			sms.setHisEmailSta(MsgSta.RunOK);
//			sms.Update();
//		}
//		catch (System.Net.Mail.SmtpException ex)
//		{
//			Log.DebugWriteError(ex.getMessage());
//			throw ex;
//		}

			///#endregion 发送邮件.
	}
}
