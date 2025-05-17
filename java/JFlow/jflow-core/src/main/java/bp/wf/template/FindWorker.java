package bp.wf.template;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.port.*;
import bp.sys.*;
import bp.web.WebUser;
import bp.wf.*;
import bp.wf.CCFormAPI;

import java.util.*;

/**
 找人规则
 */
public class FindWorker
{
	///#region 身份.
	private WebUserCopy _webUserCopy = null;
	public final WebUserCopy getWebUser() throws Exception {
		if (_webUserCopy == null)
		{
			_webUserCopy = new WebUserCopy();
			_webUserCopy.LoadWebUser();
		}
		return _webUserCopy;
	}
	public final void setWebUser(WebUserCopy value)
	{
		_webUserCopy = value;
	}

	///#endregion 身份.


	///#region 变量
	public WorkNode town = null;
	public WorkNode currWn = null;
	public Flow fl = null;
	private String dbStr = bp.difference.SystemConfig.getAppCenterDBVarStr();
	public Paras ps = null;
	private String JumpToEmp = null;
	private int JumpToNode = 0;
	private long WorkID = 0;

	///#endregion 变量

	public FindWorker()
	{

	}
	public final DataTable FindByWorkFlowModel() throws Exception {
		this.town = town;

		Node toNode = town.getHisNode();
		boolean isOpenSelecter = toNode.GetParaBoolen("IsOpenSelecter");
		DataTable dt = new DataTable();
		dt.Columns.Add("No", String.class);
		String sql;
		String FK_Emp;

		// 如果执行了两次发送，那前一次的轨迹就需要被删除,这里是为了避免错误。
		ps = new Paras();
		ps.Add("WorkID", this.WorkID);
		ps.Add("FK_Node", town.getHisNode().getNodeID());
		ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "WorkID AND FK_Node =" + dbStr + "FK_Node";
		DBAccess.RunSQL(ps);

		// 如果指定特定的人员处理。
		if (DataType.IsNullOrEmpty(JumpToEmp) == false)
		{
			String[] myEmpStrs = JumpToEmp.split("[,]", -1);
			for (String emp : myEmpStrs)
			{
				if (DataType.IsNullOrEmpty(emp))
				{
					continue;
				}
				DataRow dr = dt.NewRow();
				dr.setValue(0, emp);
				dt.Rows.add(dr);
			}
			return dt;
		}

		// 按上一节点发送人处理。
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByPreviousNodeEmp)
		{
			DataRow dr = dt.NewRow();
			dr.setValue(0, getWebUser().getNo());
			dt.Rows.add(dr);
			return dt;
		}

		//首先判断是否配置了获取下一步接受人员的sql.
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySQL || town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySQLTemplate || town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySQLAsSubThreadEmpsAndData)
		{

			if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySQLTemplate)
			{
				SQLTemplate st = new SQLTemplate(town.getHisNode().getDeliveryParas());
				sql = st.getDocs();
			}
			else
			{
				if (town.getHisNode().getDeliveryParas().length() < 4)
				{
					throw new RuntimeException("@您设置的当前节点按照SQL，决定下一步的接受人员，但是你没有设置SQL.");
				}
				sql = town.getHisNode().getDeliveryParas();
			}


			//特殊的变量.
			sql = sql.replace("@FK_Node", String.valueOf(this.town.getHisNode().getNodeID()));
			sql = sql.replace("@NodeID", String.valueOf(this.town.getHisNode().getNodeID()));

			sql = sql.replace("@WorkID", String.valueOf(this.currWn.getHisWork().getOID()));
			sql = sql.replace("@FID", String.valueOf(this.currWn.getHisWork().getFID()));
			sql = sql.replace("@WebUser.DeptNo", WebUser.getDeptNo());


			if (this.currWn.getHisNode().getFormType() == NodeFormType.RefOneFrmTree)
			{
				GEEntity en = new GEEntity(this.currWn.getHisNode().getNodeFrmID(), this.currWn.getHisWork().getOID());
				sql = bp.wf.Glo.DealExp(sql, en, null);
			}
			else
			{
				sql = bp.wf.Glo.DealExp(sql, this.currWn.rptGe, null);
			}

			//如果里面还有变量，就可能是系统变量.
			if (sql.contains("@") == true)
			{
				GenerWorkFlow gwf = this.currWn.getHisGenerWorkFlow();
				AtPara ap = gwf.getatPara();
				for (String key : ap.getHisHT().keySet())
				sql = sql.replace("@" + key + "", ap.GetValStrByKey(key));
			}
			if (sql.contains("@") == true)
				throw new Exception("err@接受人的表达式设置错误,没有找到变量[" + sql + "].");
			//if (sql.contains("@GuestUser.getNo()"))
			//    sql = sql.replace("@GuestUser.getNo()", GuestUser.getNo());
			//if (sql.contains("@GuestUser.getName()"))
			//    sql = sql.replace("@GuestUser.getName()", GuestUser.getName());

			dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
			{
				throw new RuntimeException("@没有找到可接受的工作人员。@技术信息：执行的SQL没有发现人员:" + sql);
			}
			return dt;
		}


		///#region 按绑定部门计算,该部门一人处理标识该工作结束(子线程)..
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySetDeptAsSubthread)
		{
			if (this.town.getHisNode().getItIsSubThread() == false)
			{
				throw new RuntimeException("@您设置的节点接收人方式为：按绑定部门计算,该部门一人处理标识该工作结束(子线程)，但是当前节点非子线程节点。");
			}

			sql = "SELECT " + bp.sys.base.Glo.getUserNo() + ", Name,FK_Dept AS GroupMark FROM Port_Emp WHERE FK_Dept IN (SELECT FK_Dept FROM WF_NodeDept WHERE FK_Node=" + town.getHisNode().getNodeID() + ")";
			dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
			{
				throw new RuntimeException("@没有找到可接受的工作人员,接受人方式为, ‘按绑定部门计算,该部门一人处理标识该工作结束(子线程)’ @技术信息：执行的SQL没有发现人员:" + sql);
			}
			return dt;
		}

		///#endregion 按绑定部门计算,该部门一人处理标识该工作结束(子线程)..


		///#region 按照明细表,作为子线程的接收人.
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByDtlAsSubThreadEmps)
		{
			if (this.town.getHisNode().getItIsSubThread() == false)
			{
				throw new RuntimeException("@您设置的节点接收人方式为：以分流点表单的明细表数据源确定子线程的接收人，但是当前节点非子线程节点。");
			}

			this.currWn.getHisNode().WorkID = this.WorkID; //为获取表单ID ( NodeFrmID )提供参数.
			MapDtls dtls = new MapDtls(this.currWn.getHisNode().getNodeFrmID());
			String msg = null;
			for (MapDtl dtl : dtls.ToJavaList())
			{
				try
				{
					String empFild = town.getHisNode().getDeliveryParas();
					if (DataType.IsNullOrEmpty(empFild))
					{
						empFild = " UserNo ";
					}

					ps = new Paras();
					ps.SQL = "SELECT " + empFild + ", * FROM " + dtl.getPTable() + " WHERE RefPK=" + dbStr + "OID ORDER BY OID";
					if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL)
					{
						ps.SQL = "SELECT " + empFild + ", A.* FROM " + dtl.getPTable() + " A WHERE RefPK=" + dbStr + "OID ORDER BY OID";
					}
					ps.Add("OID", this.WorkID);
					dt = DBAccess.RunSQLReturnTable(ps);
					if (dt.Rows.size() == 0)
					{
						throw new RuntimeException("@没有找到可接受的工作人员,流程设计错误，到达的节点（" + town.getHisNode().getName() + "）在指定的节点中没有数据，无法找到子线程的工作人员。");
					}
					return dt;
				}
				catch (RuntimeException ex)
				{
					msg += ex.getMessage();
					//if (dtls.size()== 1)
					//    throw new Exception("@估计是流程设计错误,没有在分流节点的明细表中设置");
				}
			}
			throw new RuntimeException("@没有找到分流节点的明细表作为子线程的发起的数据源，流程设计错误，请确认分流节点表单中的明细表是否有UserNo约定的系统字段。" + msg);
		}

		///#endregion 按照明细表,作为子线程的接收人.


		///#region 按节点绑定的人员处理.
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByBindEmp)
		{
			sql = "SELECT A.No,A.Name FROM Port_Emp A, WF_NodeEmp B WHERE FK_Node=" + town.getHisNode().getNodeID() + " AND A.No=B.FK_Emp ";
			if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
				sql = "SELECT A.UserID No,A.Name  FROM Port_Emp A, WF_NodeEmp B WHERE FK_Node=" + town.getHisNode().getNodeID() + " AND A.UserID=B.FK_Emp AND A.OrgNo='" + WebUser.getOrgNo() + "' ";

			dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
				throw new Exception("@流程设计错误:下一个节点(" + town.getHisNode().getName() + ")没有绑定工作人员 . ");
			return dt;
		}

		///#endregion 按节点绑定的人员处理.

		String empNo = getWebUser().No;
		String empDept = getWebUser().DeptNo;


		///#region 找指定节点的人员直属领导 .
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByEmpLeader)
		{
			//查找指定节点的人员， 如果没有节点，就是当前的节点.
			String para = town.getHisNode().getDeliveryParas();
			if (DataType.IsNullOrEmpty(para) == true)
				para = String.valueOf(this.currWn.getHisNode().getNodeID());

			String[] strs = para.split("[,]", -1);
			for (String str : strs)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}

				ps = new Paras();
				ps.SQL = "SELECT FK_Emp FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node AND (IsPass=1 OR FK_Emp="+dbStr+"FK_Emp)";
				ps.Add("OID", this.WorkID);
				ps.Add("FK_Node", Integer.parseInt(str));
				ps.Add("FK_Emp",WebUser.getNo());
				DataTable dtt = DBAccess.RunSQLReturnTable(ps);
				if (dtt.Rows.size() == 0)
					continue;
				for(DataRow dr : dtt.Rows){
					empNo = dr.getValue(0).toString();
					//查找人员的直属leader
					sql = "";
					if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single || bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
						sql = "SELECT Leader,FK_Dept FROM Port_Emp WHERE No='" + empNo + "'";
					else
						sql = "SELECT Leader,FK_Dept FROM Port_Emp WHERE No='" + getWebUser().OrgNo + "_" + empNo + "'";

					DataTable dtEmp = DBAccess.RunSQLReturnTable(sql);

					//查找他的leader, 如果没有就找部门领导.
					String leader = dtEmp.Rows.get(0).getValue(0) instanceof String ? (String)dtEmp.Rows.get(0).getValue(0) : null;
					String deptNo = dtEmp.Rows.get(0).getValue(1) instanceof String ? (String)dtEmp.Rows.get(0).getValue(1) : null;
					if (DataType.IsNullOrEmpty(leader) == true)
					{
						sql = "SELECT Leader FROM Port_Dept WHERE No='" + deptNo + "'";
						leader = DBAccess.RunSQLReturnStringIsNull(sql, null);
						if (leader == null)
						{
							throw new RuntimeException("@没有找到可接受的工作人员,流程设计错误:下一个节点(" + town.getHisNode().getName() + ")设置的按照直属领导计算，没有维护(" + getWebUser().getNo() + "," + getWebUser().getName() +")的直属领导 . ");
						}
					}
					if (DataType.IsNullOrEmpty(leader) == false)
					{
						DataRow drr = dt.NewRow();
						drr.setValue(0,leader);
						dt.Rows.add(drr);
					}
				}
			}
			if (dt.Rows.size() == 0 )
				throw new Exception("@没有找到可接受的工作人员。@技术信息：找指定节点的人员直属领导:" + para);
			return dt;
		}

		///#endregion .按照部门负责人计算


		///#region 按照部门负责人计算.
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByDeptLeader)
		{
			return ByDeptLeader();
		}
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByDeptSpecer)
		{
			return ByDeptSpecer();
		}


		///#endregion .按照部门负责人计算


		///#region 按照部门分管领导计算.
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByDeptShipLeader)
		{
			return ByDeptShipLeader();
		}


		///#endregion .按照部门负责人计算


		///#region 按照选择的人员处理。
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySelected
				|| town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySelectedForPrj
				|| town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByFEE
				|| town.getHisNode().getHisDeliveryWay() == DeliveryWay.PreplaceWokerFix
				|| town.getHisNode().getHisDeliveryWay() == DeliveryWay.PreplaceWokerFree
				|| town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySelected_2)
		{
			ps = new Paras();
			ps.Add("FK_Node", this.town.getHisNode().getNodeID());
			ps.Add("WorkID", this.currWn.getHisWork().getOID());
			ps.SQL = "SELECT FK_Emp FROM WF_SelectAccper WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND AccType=0 ORDER BY IDX";
			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() == 0)
			{
				/*从上次发送设置的地方查询. */
				SelectAccpers sas = new SelectAccpers();
				int i = sas.QueryAccepterPriSetting(this.town.getHisNode().getNodeID());
				if (i == 0)
				{
					if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySelected || town.getHisNode().getHisDeliveryWay()  == DeliveryWay.BySelected_2 || town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySelectedForPrj)
					{
						Node currNode = this.currWn.getHisNode();
						if (toNode.getItIsResetAccepter() == false && toNode.getHisToNDs().contains("@" + currNode.getNodeID()) == true && currNode.getHisToNDs().contains("@" + toNode.getNodeID()) == true)
						{
							sql = "SELECT EmpFrom From ND" + Integer.parseInt(toNode.getFlowNo()) + "Track WHERE WorkID=" + this.WorkID + " AND NDFrom=" + toNode.getNodeID() + " AND NDTo=" + currNode.getNodeID() + " AND ActionType IN(0,1,6,7,11)";
							DataTable dtt = DBAccess.RunSQLReturnTable(sql);
							if (dtt.Rows.size() > 0)
							{
								for (DataRow drr : dtt.Rows)
								{
									DataRow dr = dt.NewRow();
									dr.setValue(0, drr.getValue(0).toString());
									dt.Rows.add(dr);
								}
								return dt;
							}
						}
						if (town.getHisNode().getHisDeliveryWay()  == DeliveryWay.BySelected_2)
							throw new Exception("url@./WorkOpt/Accepter.htm?FK_Flow=" + toNode.getFlowNo() + "&FK_Node=" + this.currWn.getHisNode().getNodeID() + "&ToNode=" + toNode.getNodeID() + "&WorkID=" + this.WorkID + "PageName=Accepter");

						Selector select = new Selector(toNode.getNodeID());
						if (select.getSelectorModel() == SelectorModel.GenerUserSelecter || town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySelected)
						{
							throw new RuntimeException("url@./WorkOpt/AccepterOfGener.htm?FK_Flow=" + toNode.getFlowNo() + "&FK_Node=" + this.currWn.getHisNode().getNodeID() + "&ToNode=" + toNode.getNodeID() + "&WorkID=" + this.WorkID + "&PageName=AccepterOfGener");
						}
						else
						{
							throw new RuntimeException("url@./WorkOpt/Accepter.htm?FK_Flow=" + toNode.getFlowNo() + "&FK_Node=" + this.currWn.getHisNode().getNodeID() + "&ToNode=" + toNode.getNodeID() + "&WorkID=" + this.WorkID + "PageName=Accepter");
						}
					}
					else
					{
						throw new RuntimeException("@流程设计错误，请重写FEE，然后为节点(" + town.getHisNode().getNo() + "," + 	town.getHisNode().getName() + ",接收人规则:"+ town.getHisNode().getHisDeliveryWay().toString() + "," + town.getHisNode().getHisDeliveryWayText() + ")设置接受人员，详细请参考cc流程设计手册。");
					}
				}

				//插入里面.
				for (SelectAccper item : sas.ToJavaList())
				{
					DataRow dr = dt.NewRow();
					dr.setValue(0, item.getEmpNo());
					dt.Rows.add(dr);
				}
				return dt;
			}
			return dt;
		}

		///#endregion 按照选择的人员处理。


		///#region 按照指定节点的处理人计算。
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySpecNodeEmp || town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByStarter)
		{
			/* 按指定节点的人员计算 */
			String strs = town.getHisNode().getDeliveryParas();
			if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByStarter)
			{
				long myworkid = this.currWn.getWorkID();
				if (this.currWn.getHisWork().getFID() != 0)
				{
					myworkid = this.currWn.getHisWork().getFID();
				}
				dt = DBAccess.RunSQLReturnTable("SELECT Starter as No, StarterName as Name FROM WF_GenerWorkFlow WHERE WorkID=" + myworkid);
				if (dt.Rows.size() == 1)
				{
					return dt;
				}

				/* 有可能当前节点就是第一个节点，那个时间还没有初始化数据，就返回当前人. */
				if (this.currWn.getHisNode().getItIsStartNode())
				{
					DataRow dr = dt.NewRow();
					dr.setValue(0, getWebUser().getNo());
					dt.Rows.add(dr);
					return dt;
				}

				if (dt.Rows.size() == 0)
				{
					throw new RuntimeException("@没有找到可接受的工作人员,流程设计错误，到达的节点（" + town.getHisNode().getName() + "）无法找到开始节点的工作人员。");
				}
				else
				{
					return dt;
				}

			}

			// 首先从本流程里去找。
			strs = strs.replace(";", ",");
			String[] ndStrs = strs.split("[,]", -1);
			for (String nd : ndStrs)
			{
				if (DataType.IsNullOrEmpty(nd))
				{
					continue;
				}

				if (DataType.IsNumStr(nd) == false)
				{
					throw new RuntimeException("流程设计错误:您设置的节点(" + town.getHisNode().getName() + ")的接收方式为按指定的节点角色投递，但是您没有在访问规则设置中设置节点编号。");
				}

				ps = new Paras();
				String workSQL = "";
				//获取指定节点的信息
				Node specNode = new Node(nd);
				//指定节点是子线程
				if (specNode.getItIsSubThread() == true)
				{
					if (this.currWn.getHisNode().getItIsSubThread() == true)
					{
						workSQL = "FID=" + this.currWn.getHisWork().getFID();
					}
					else
					{
						workSQL = "FID=" + this.WorkID;
					}
				}
				else
				{
					if (this.currWn.getHisNode().getItIsSubThread() == true)
					{
						workSQL = "WorkID=" + this.currWn.getHisWork().getFID();
					}
					else
					{
						workSQL = "WorkID=" + this.WorkID;
					}

				}

				ps.SQL = "SELECT DISTINCT(FK_Emp) FROM WF_GenerWorkerlist WHERE " + workSQL + " AND FK_Node=" + dbStr + "FK_Node AND IsEnable=1 ";
				ps.Add("FK_Node", Integer.parseInt(nd));

				DataTable dt_ND = DBAccess.RunSQLReturnTable(ps);
				//添加到结果表
				if (dt_ND.Rows.size() != 0)
				{
					for (DataRow row : dt_ND.Rows)
					{
						DataRow dr = dt.NewRow();
						dr.setValue(0, row.getValue(0).toString());
						dt.Rows.add(dr);
					}
					//此节点已找到数据则不向下找，继续下个节点
					continue;
				}

				//就要到轨迹表里查,因为有可能是跳过的节点.
				ps = new Paras();
				ps.SQL = "SELECT DISTINCT(" + TrackAttr.EmpFrom + ") FROM ND" + Integer.parseInt(fl.getNo()) + "Track WHERE" + " (ActionType=" + dbStr + "ActionType1 OR ActionType=" + dbStr + "ActionType2 OR ActionType=" + dbStr + "ActionType3" + "  OR ActionType=" + dbStr + "ActionType4 OR ActionType=" + dbStr + "ActionType5 OR ActionType=" + dbStr + "ActionType6)" + "   AND NDFrom=" + dbStr + "NDFrom AND " + workSQL;
				ps.Add("ActionType1", ActionType.Skip.getValue());
				ps.Add("ActionType2", ActionType.Forward.getValue());
				ps.Add("ActionType3", ActionType.ForwardFL.getValue());
				ps.Add("ActionType4", ActionType.ForwardHL.getValue());
				ps.Add("ActionType5", ActionType.SubThreadForward.getValue());
				ps.Add("ActionType6", ActionType.Start.getValue());
				ps.Add("NDFrom", Integer.parseInt(nd));

				dt_ND = DBAccess.RunSQLReturnTable(ps);
				if (dt_ND.Rows.size() != 0)
				{
					for (DataRow row : dt_ND.Rows)
					{
						DataRow dr = dt.NewRow();
						dr.setValue(0, row.get(0).toString());
						dt.Rows.add(dr);
					}
					continue;
				}

				//从Selector中查找
				ps = new Paras();
				ps.SQL = "SELECT DISTINCT(FK_Emp) FROM WF_SelectAccper WHERE FK_Node=" + dbStr + "FK_Node AND " + workSQL;
				ps.Add("FK_Node", Integer.parseInt(nd));


				dt_ND = DBAccess.RunSQLReturnTable(ps);
				//添加到结果表
				if (dt_ND.Rows.size() != 0)
				{
					for (DataRow row : dt_ND.Rows)
					{
						DataRow dr = dt.NewRow();
						dr.setValue(0, row.get(0).toString());
						dt.Rows.add(dr);
					}
					//此节点已找到数据则不向下找，继续下个节点
					continue;
				}


			}

			//本流程里没有有可能该节点是配置的父流程节点,也就是说子流程的一个节点与父流程指定的节点的工作人员一致.
			GenerWorkFlow gwf = new GenerWorkFlow(this.WorkID);
			if (gwf.getPWorkID() != 0)
			{
				for (String pnodeiD : ndStrs)
				{
					if (DataType.IsNullOrEmpty(pnodeiD))
					{
						continue;
					}

					Node nd = new Node(Integer.parseInt(pnodeiD));
					if (!Objects.equals(nd.getFlowNo(), gwf.getPFlowNo()))
					{
						continue; // 如果不是父流程的节点，就不执行.
					}

					ps = new Paras();
					ps.SQL = "SELECT FK_Emp FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node AND IsPass=1 AND IsEnable=1 ";
					ps.Add("FK_Node", nd.getNodeID());
					if (this.currWn.getHisNode().getItIsSubThread() == true)
					{
						ps.Add("OID", gwf.getPFID());
					}
					else
					{
						ps.Add("OID", gwf.getPWorkID());
					}

					DataTable dt_PWork = DBAccess.RunSQLReturnTable(ps);
					if (dt_PWork.Rows.size() != 0)
					{
						for (DataRow row : dt_PWork.Rows)
						{
							DataRow dr = dt.NewRow();
							dr.setValue(0, row.get(0).toString());
							dt.Rows.add(dr);
						}
						//此节点已找到数据则不向下找，继续下个节点
						continue;
					}

					//就要到轨迹表里查,因为有可能是跳过的节点.
					ps = new Paras();
					ps.SQL = "SELECT " + TrackAttr.EmpFrom + " FROM ND" + Integer.parseInt(fl.getNo()) + "Track WHERE (ActionType=" + dbStr + "ActionType1 OR ActionType=" + dbStr + "ActionType2 OR ActionType=" + dbStr + "ActionType3 OR ActionType=" + dbStr + "ActionType4 OR ActionType=" + dbStr + "ActionType5) AND NDFrom=" + dbStr + "NDFrom AND WorkID=" + dbStr + "WorkID";
					ps.Add("ActionType1", ActionType.Start.getValue());
					ps.Add("ActionType2", ActionType.Forward.getValue());
					ps.Add("ActionType3", ActionType.ForwardFL.getValue());
					ps.Add("ActionType4", ActionType.ForwardHL.getValue());
					ps.Add("ActionType5", ActionType.Skip.getValue());

					ps.Add("NDFrom", nd.getNodeID());

					if (this.currWn.getHisNode().getItIsSubThread() == true)
					{
						ps.Add("WorkID", gwf.getPFID());
					}
					else
					{
						ps.Add("WorkID", gwf.getPWorkID());
					}

					dt_PWork = DBAccess.RunSQLReturnTable(ps);
					if (dt_PWork.Rows.size() != 0)
					{
						for (DataRow row : dt_PWork.Rows)
						{
							DataRow dr = dt.NewRow();
							dr.setValue(0, row.get(0).toString());
							dt.Rows.add(dr);
						}
					}
				}
			}
			//返回指定节点的处理人
			if (dt.Rows.size() != 0)
			{
				return dt;
			}

			throw new RuntimeException("@流程设计错误，到达的节点（" + town.getHisNode().getName() + "）在指定的节点(" + strs + ")中没有数据，无法找到工作的人员。 @技术信息如下: 投递方式:BySpecNodeEmp sql=" + ps.getSQLNoPara());
		}

		///#endregion 按照节点绑定的人员处理。


		///#region 按照上一个节点表单指定字段的人员处理。
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByPreviousNodeFormEmpsField)
		{
			// 为河南安防增加接受人规则按从表获取.
			int A5DataFrom = town.getHisNode().GetParaInt("A5DataFrom", 0);
			// 检查接受人员规则,是否符合设计要求.
			String specEmpFields = town.getHisNode().getDeliveryParas();
			if (DataType.IsNullOrEmpty(specEmpFields))
			{
				specEmpFields = "SysSendEmps";
			}

			String emps = "";
			DataTable dtVals = null;


			///#region 0.按主表的字段计算.
			if (A5DataFrom == 0)
			{
				if (this.currWn.rptGe.getEnMap().getAttrs().contains(specEmpFields) == false)
					throw new RuntimeException("@您设置的接受人规则是按照表单指定的字段，决定下一步的接受人员，该字段{" + specEmpFields + "}已经删除或者丢失。");
				String ptable = this.currWn.rptGe.getEnMap().getPhysicsTable();

				String emps1 = this.currWn.rptGe.GetValStringByKey(specEmpFields);
				sql = "SELECT No,Name FROM Port_Emp WHERE No IN (" + bp.da.DataType.DealFromatSQLWhereIn(emps1) + ")";
				dt = DBAccess.RunSQLReturnTable(sql);
				if (dt.Rows.size() == 0) {
					//获得数据.
					dt = CCFormAPI.GenerPopData2022(String.valueOf(this.WorkID), specEmpFields);
					if (dt.Rows.size() == 0) {
						throw new RuntimeException("@没有找到可接受的工作人员,没有在字段[ " + specEmpFields + " ]中指定接受人，工作无法向下发送。");
					}
				}
				return dt;
			}

			///#endregion 按主表的字段计算.


			///#region 1.按从表的字段计算.
			String dtlID = this.town.getHisNode().GetParaString("A5DataDtl");
			if (dtlID != null)
			{
				throw new RuntimeException("@到达节点[" + this.town.getHisNode().getName() + "]的接受人规则是按照从表的字段计算，但是您没有配置从表的字段");
			}

			MapDtl dtl = new MapDtl();
			dtl.setNo(dtlID);
			if (dtl.RetrieveFromDBSources() == 0)
			{
				throw new RuntimeException("@到达节点[" + this.town.getHisNode().getName() + "]的接受人规则是按照从表的字段计算，从表[" + dtlID + "]被删除.");
			}


			MapAttrs dtlAttrs = new MapAttrs();
			dtlAttrs.Retrieve("FK_MapData", dtlID, null);

			if (dtlAttrs.GetCountByKey("KeyOfEn", specEmpFields) == 0)
			{
				throw new RuntimeException("@到达节点[" + this.town.getHisNode().getName() + "]的接受人规则是按照从表的字段计算，从表[" + dtlID + "]的字段[" + specEmpFields + "]，被删除.");
			}

			//获得数据.
			dt = CCFormAPI.GenerPopData2022(String.valueOf(this.WorkID), specEmpFields);
			if (dt.Rows.size() == 0 )
			{
				throw new RuntimeException("@没有找到可接受的工作人员@没有在字段[ " + specEmpFields + " ]中指定接受人，工作无法向下发送。");
			}

			///#endregion 按从表的字段计算.
		}
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByPreviousNodeFormEmpsFrmDtl){
			///#region 1.按从表的字段计算.
			String dtlID = this.town.getHisNode().getDeliveryParas();
			if (DataType.IsNullOrEmpty(dtlID)==true)
			{
				throw new RuntimeException("@到达节点[" + this.town.getHisNode().getName() + "]的接受人规则是按照从表的字段计算，但是您没有配置从表的字段");
			}

			MapDtl dtl = new MapDtl();
			dtl.setNo(dtlID);
			if (dtl.RetrieveFromDBSources() == 0)
			{
				throw new RuntimeException("@到达节点[" + this.town.getHisNode().getName() + "]的接受人规则是按照从表的字段计算，从表[" + dtlID + "]被删除.");
			}


			MapAttrs dtlAttrs = new MapAttrs();
			dtlAttrs.Retrieve("FK_MapData", dtlID, null);
			String specEmpFields = this.town.getHisNode().GetParaString("DtlField");
			if (dtlAttrs.GetCountByKey("KeyOfEn", specEmpFields) == 0)
			{
				throw new RuntimeException("@到达节点[" + this.town.getHisNode().getName() + "]的接受人规则是按照从表的字段计算，从表[" + dtlID + "]的字段[" + specEmpFields + "]，被删除.");
			}
			String dtlRefPKVal = Dev2Interface.GetDtlRefPKVal(this.WorkID, this.currWn.getHisGenerWorkFlow().getPWorkID(), this.currWn.getHisGenerWorkFlow().getFID(), this.currWn.getHisGenerWorkFlow().getNodeID(), dtl.getFrmID(), dtl);
			DataTable dtt = DBAccess.RunSQLReturnTable("SELECT "+specEmpFields+" From "+dtl.getPTable()+" WHERE RefPK='"+dtlRefPKVal+"'");
			if (dtt.Rows.size() == 0)
			{
				throw new RuntimeException("@没有找到可接受的工作人员@没有在字段[ " + specEmpFields + " ]中指定接受人，工作无法向下发送。");
			}
			String str = "";
			String empNos=",";
			DataRow drr = null;
			for(DataRow dr :dtt.Rows){
				str = dr.getValue(0).toString();
				if(str.contains(",")==true){
					String[] strs = str.split(",");
					for(String no : strs){
						if(empNos.contains(","+no+",")==false){
							empNos+=no+",";
							drr = dt.NewRow();
							drr.setValue(0,no);
							dt.Rows.add(drr);
						}
					}
				}else{
					if(empNos.contains(","+str+",")==false){
						empNos+=str+",";
						drr = dt.NewRow();
						drr.setValue(0,str);
						dt.Rows.add(drr);
					}
				}
			}
			return dt;
		}

		///#endregion 按照上一个节点表单指定字段的人员处理。
		//#region 字段是部门编号。
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByPreviousNodeFormDepts)
		{
			// 检查接受人员规则,是否符合设计要求.
			String specEmpFields = town.getHisNode().getDeliveryParas();
			if (DataType.IsNullOrEmpty(specEmpFields))
				specEmpFields = "SysSendEmps";

			String emps = "";
			DataTable dtVals = null;

			if (this.currWn.rptGe.getEnMap().getAttrs().contains(specEmpFields) == false)
				throw new Exception("@您设置的接受人规则是按照表单指定的字段作为部门的编号，决定下一步的接受人员，该字段{" + specEmpFields + "}已经删除或者丢失。");

			//获得数据.
			dt = CCFormAPI.GenerPopData2022(String.valueOf(this.WorkID), specEmpFields);
			if (dt.Rows.size() == 0)
				throw new Exception("@没有找到可接受的工作人员@没有在字段[ " + specEmpFields + " ]中指定接受人（部门字段），工作无法向下发送。");

			for (DataRow dr : dt.Rows)
			{
				String deptNo = dr.get(0).toString();
				dr.setValue(0,DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Dept WHERE No='" + deptNo + "'"));
			}
			return dt;

		}
        //    #endregion 字段是部门编号。

        //    #region 字段是权限组:
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByPreviousNodeFormEmpsTeam)
		{

			// 检查接受人员规则,是否符合设计要求.
			String specEmpFields = town.getHisNode().getDeliveryParas();
			if (DataType.IsNullOrEmpty(specEmpFields))
				specEmpFields = "SysSendEmps";

			String emps = "";
			DataTable dtVals = null;

			if (this.currWn.rptGe.getEnMap().getAttrs().contains(specEmpFields) == false)
				throw new Exception("@您设置的接受人规则是按照表单指定的字段作为部门的编号，决定下一步的接受人员，该字段{" + specEmpFields + "}已经删除或者丢失。");

			//获得数据.
			dt = CCFormAPI.GenerPopData2022(String.valueOf(this.WorkID), specEmpFields);
			if (dt.Rows.size() == 0)
				throw new Exception("@没有找到可接受的工作人员@没有在字段[ " + specEmpFields + " ]中指定接受人（部门字段），工作无法向下发送。");

			DataTable mydt1 = new DataTable();
			mydt1.Columns.Add("No");
			mydt1.Columns.Add("Name");

			for (DataRow dr : dt.Rows)
			{
				String deptNo = dr.get(0).toString();
				DataTable dtTemp = DBAccess.RunSQLReturnTable("SELECT FK_Emp FROM Port_TeamEmp WHERE FK_Teamp='" + deptNo + "'");
			}
			return dt;
		}
        //    #endregion 字段是部门编号。

		///#region 绑定字典表。
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySFTable)
		{
			String pkval = town.getHisNode().getDeliveryParas();
			SFTable table = new SFTable(pkval);
			DataTable mydtTable = table.GenerHisDataTable(this.currWn.rptGe.getRow());
			return mydtTable;
		}

		///#endregion 绑定字典表。



		///#region 按照上一个节点表单指定字段的 【部门的领导】处理。
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByPreviousNodeFormDepts)
		{
			// 检查接受人员规则,是否符合设计要求.
			String specEmpFields = town.getHisNode().getDeliveryParas();
			if (DataType.IsNullOrEmpty(specEmpFields) == true)
			{
				throw new RuntimeException("@您设置的接受人规则是按照表单指定的字段是部门，但是没有选择表单字段");
			}
			if (this.currWn.rptGe.getEnMap().getAttrs().contains(specEmpFields) == false)
			{
				throw new RuntimeException("@您设置的接受人规则是按照表单指定的部门字段，决定下一步的接受人员，该字段{" + specEmpFields + "}已经删除或者丢失。");
			}

			//判断该字段是否启用了pop返回值？
			sql = "SELECT  Tag1 AS VAL FROM Sys_FrmEleDB WHERE RefPKVal=" + this.WorkID + " AND EleID='" + specEmpFields + "'";
			String depts = "";
			DataTable dtVals = DBAccess.RunSQLReturnTable(sql);

			//获取接受人并格式化接受人,
			if (dtVals.Rows.size() > 0)
			{
				for (DataRow dr : dtVals.Rows)
				{
					depts += dr.getValue(0).toString() + ",";
				}
			}
			else
			{
				depts = this.currWn.rptGe.GetValStringByKey(specEmpFields);
			}


			depts = depts.replace(" ", ""); //去掉空格.
			if (depts.endsWith(","))
			{
				depts = depts.substring(0, depts.length() - 1);
			}
			if (DataType.IsNullOrEmpty(depts) == false)
			{
				depts = "'" + depts.replace(",", "','") + "'";
			}

			if (DataType.IsNullOrEmpty(depts) == true && town.getHisNode().getHisWhenNoWorker() == false)
			{
				throw new RuntimeException("@您设置的接受人规则是按照表单指定的部门字段，没有选择部门");
			}
			//获取人员
			sql = "SELECT  Leader as No From Port_Dept WHERE No IN(" + depts + ")";
			DataTable dtt = DBAccess.RunSQLReturnTable(sql);
			if (dtt.Rows.size() == 0)
			{
				throw new RuntimeException("@您设置的接受人规则是按照表单指定的部门字段，填写的部门中不存在人员");
			}
			return dtt;
		}

		///#endregion 按照上一个节点表单指定字段的 【部门】处理。


		///#region 按照上一个节点表单指定字段的 【角色】处理。
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByPreviousNodeFormStationsAI || town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByPreviousNodeFormStationsOnly)
		{
			// 检查接受人员规则,是否符合设计要求.
			String specEmpFields = town.getHisNode().getDeliveryParas();
			if (DataType.IsNullOrEmpty(specEmpFields))
			{
				specEmpFields = "SysSendEmps";
			}

			if (this.currWn.rptGe.getEnMap().getAttrs().contains(specEmpFields) == false)
			{
				throw new RuntimeException("@您设置的接受人规则是按照表单指定的角色字段，决定下一步的接受人员，该字段{" + specEmpFields + "}已经删除或者丢失。");
			}

			//判断该字段是否启用了pop返回值？
			sql = "SELECT  Tag1 AS VAL FROM Sys_FrmEleDB WHERE RefPKVal=" + this.WorkID + " AND EleID='" + specEmpFields + "'";
			String emps = "";
			DataTable dtVals = DBAccess.RunSQLReturnTable(sql);

			//获得角色信息.
			String stas = "";

			//获取接受人并格式化接受人,
			if (dtVals.Rows.size() > 0)
			{
				for (DataRow dr : dtVals.Rows)
				{
					emps += dr.getValue(0).toString() + ",";
				}
			}
			else
			{
				emps = this.currWn.rptGe.GetValStringByKey(specEmpFields);
			}
			emps = emps.replace(" ", ""); //去掉空格.
			if (emps.contains(",") && emps.contains(";"))
			{
				/*如果包含,; 例如 zhangsan,张三;lisi,李四;*/
				String[] myemps1 = emps.split("[;]", -1);
				for (String str : myemps1)
				{
					if (DataType.IsNullOrEmpty(str))
					{
						continue;
					}

					String[] ss = str.split("[,]", -1);
					stas += "," + ss[0];

					//DataRow dr = dt.NewRow();
					// = ss[0];
					//dt.Rows.add(dr);
				}
				if (dt.Rows.size() == 0)
				{
					throw new RuntimeException("@没有找到可接受的工作人员@输入的接受人员角色信息错误;[" + emps + "]。");
				}
				else
				{
					return dt;
				}
			}
			else
			{
				emps = emps.replace(";", ",");
				emps = emps.replace("；", ",");
				emps = emps.replace("，", ",");
				emps = emps.replace("、", ",");
				emps = emps.replace("@", ",");

				if (DataType.IsNullOrEmpty(emps))
				{
					throw new RuntimeException("@没有找到可接受的工作人员@没有在字段[" + this.currWn.getHisWork().getEnMap().getAttrs().GetAttrByKey(specEmpFields).getDesc() + "]中指定接受人，工作无法向下发送。");
				}

				// 把它加入接受人员列表中.
				String[] myemps = emps.split("[,]", -1);


				int nodeID = town.getHisNode().getNodeID();
				//NodeStation ns = new NodeStation();
				//ns.Delete(NodeStationAttr.FK_Node, nodeID);

				for (String s : myemps)
				{
					if (DataType.IsNullOrEmpty(s))
					{
						continue;
					}
					stas += "," + s;
				}
			}

			if (DataType.IsNullOrEmpty(stas) == true)
			{
				throw new RuntimeException("err@按照上一个节点表单指定字段的,没有找到选择的岗位信息.");
			}

			//根据角色：集合获取信息.
			stas = stas.substring(1);

			//把这次的岗位s存储到临时变量,以方便用到下一个节点多人处理规则，按岗位删除时用到。
			this.currWn.getHisGenerWorkFlow().SetPara("NodeStas" + town.getHisNode().getNodeID(), stas);


			// 仅按角色计算.  以下都有要重写.
			if (toNode.getHisDeliveryWay() == DeliveryWay.ByPreviousNodeFormStationsOnly)
			{
				dt = WorkFlowBuessRole.FindWorker_GetEmpsByStations(stas);
				if (dt.Rows.size() == 0)
				{
					throw new RuntimeException("err@没有找到可接受的工作人员@按照字段角色(仅按角色计算)找接受人错误,当前部门下没有您选择的角色人员.");
				}

				return dt;
			}


			///#region 按角色智能计算, 还是集合模式.
			if (toNode.getDeliveryStationReqEmpsWay() == 0 && town.getHisNode().getHisWhenNoWorker() == false)
			{
				String deptNo = getWebUser().DeptNo;
				dt = WorkFlowBuessRole.FindWorker_GetEmpsByDeptAI(stas, deptNo);
				if (dt.Rows.size() == 0)
				{
					throw new RuntimeException("err@没有找到可接受的工作人员@按照字段角色(智能)找接受人错误,当前部门与父级部门下没有您选择的角色人员.");
				}
				return dt;
			}

			///#endregion 按角色智能计算, 要判断切片模式,还是集合模式.


			///#region 按角色智能计算, 切片模式. 需要对每个角色都要找到接受人，然后把这些接受人累加起来.
			if (toNode.getDeliveryStationReqEmpsWay() == 1 || toNode.getDeliveryStationReqEmpsWay() == 2)
			{
				String deptNo = getWebUser().DeptNo;
				String[] temps = stas.split("[,]", -1);
				for (String str : temps)
				{
					//求一个角色下的人员.
					DataTable mydt1 = WorkFlowBuessRole.FindWorker_GetEmpsByDeptAI(str, deptNo);

					//如果是严谨模式.
					if (toNode.getDeliveryStationReqEmpsWay() == 1 && mydt1.Rows.size() == 0)
					{
						Station st = new Station(str);
						throw new RuntimeException("@角色[" + st.getName() +"]下，没有找到人不能发送下去，请检查组织结构是否完整。");
					}

					//累加.
					for (DataRow dr : mydt1.Rows)
					{
						DataRow mydr = dt.NewRow();
						mydr.setValue(0, dr.getValue(0).toString());
						dt.Rows.add(mydr);
					}
				}
			}

			///#endregion 按角色智能计算, 切片模式.

			return dt;
		}

		///#endregion 按照上一个节点表单指定字段的[角色]人员处理.


		///#region 为省立医院增加，按照指定的部门范围内的角色计算..
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.FindSpecDeptEmpsInStationlist)
		{

			sql = "SELECT A.FK_Emp FROM Port_DeptEmpStation A WHERE A.FK_DEPT ='" + getWebUser().getDeptNo() + "' AND A.FK_Station in(";
			sql += "select FK_Station from WF_NodeStation where FK_node=" + town.getHisNode().getNodeID() + ")";

			dt = DBAccess.RunSQLReturnTable(sql);

			if (dt.Rows.size() > 0)
			{
				return dt;
			}
			throw new Exception("@没有找到可接受的工作人员@节点访问规则(" + town.getHisNode().getHisDeliveryWay().toString() + ")错误:节点(" + town.getHisNode().getNodeID() + "," + town.getHisNode().getName() + "), 按照角色与部门的交集确定接受人的范围错误，没有找到人员:SQL=" + sql);
		}

		///#endregion 按部门与角色的交集计算.
		///#region 按部门与角色的交集计算.
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByDeptAndStation && town.getHisNode().GetParaBoolen("AccepterRoleSubDepts") == false)
		{
			//added by liuxc,2015.6.29.
			sql = "SELECT pdes.fk_emp AS No"
					+ " FROM   Port_DeptEmpStation pdes"
					+ "        INNER JOIN WF_NodeDept wnd"
					+ "             ON  wnd.fk_dept = pdes.fk_dept"
					+ "             AND wnd.fk_node = " + town.getHisNode().getNodeID()
					+ "        INNER JOIN WF_NodeStation wns"
					+ "             ON  wns.FK_Station = pdes.fk_station"
					+ "             AND wns.fk_node =" + town.getHisNode().getNodeID()
					+ " ORDER BY"
					+ "        pdes.fk_emp";

			dt = DBAccess.RunSQLReturnTable(sql);

			if (dt.Rows.size() > 0)
			{
				return dt;
			}
			throw new Exception("@没有找到可接受的工作人员@节点访问规则(" + town.getHisNode().getHisDeliveryWay().toString() + ")错误:节点(" + town.getHisNode().getNodeID() + "," + town.getHisNode().getName() + "), 按照角色与部门的交集确定接受人的范围错误，没有找到人员:SQL=" + sql);

		}

		///#endregion 按部门与角色的交集计算.

		// 按部门与角色的交集计算. - 包含子部门.
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByDeptAndStation && town.getHisNode().GetParaBoolen("AccepterRoleSubDepts") == true)
		{
			NodeDepts depts = new NodeDepts();
			depts.Retrieve("FK_Node", town.getHisNode().getNodeID());

			//遍历部门集合.
			for (NodeDept dept : depts.ToJavaList()){
				dt = ByDeptAndStationExt(dt, town.getHisNode(), dept.getDeptNo());
			}

			if (dt.Rows.size() > 0)
				return dt;
			throw new Exception("@没有找到可接受的工作人员@节点访问规则(" + town.getHisNode().getHisDeliveryWay().toString() + ")错误:节点(" + town.getHisNode().getNodeID() + "," + town.getHisNode().getName() + "), 按照角色与部门的交集确定接受人的范围错误，没有找到人员");

		}
		///#region 判断节点部门里面是否设置了部门，如果设置了就按照它的部门处理。
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByDept)
		{
			ps = new Paras();
			ps.Add("FK_Node", this.town.getHisNode().getNodeID());
			ps.SQL = "SELECT A.No,A.Name FROM Port_Emp A,Port_DeptEmp B WHERE A.No=B.FK_Emp AND B.FK_Dept IN(SELECT FK_dept FROM WF_NodeDept WHERE FK_Node =" + dbStr + "FK_Node)";
			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() == 0)
			{
				throw new Exception("err@没有找到可接受的工作人员@按照 [按绑定的部门计算] 计算接收人的时候出现错误，没有找到人，请检查节点绑定的部门下的人员.");
			}
			return dt;
		}

		///#endregion 判断节点部门里面是否设置了部门，如果设置了，就按照它的部门处理。


		///#region 用户组 计算
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByTeamOnly)
		{
			ps = new Paras();
			sql = "SELECT A.FK_Emp FROM Port_TeamEmp A, WF_NodeTeam B WHERE A.FK_Team=B.FK_Team AND B.FK_Node=" + town.getHisNode().getNodeID();
			dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() > 0)
			{
				return dt;
			}
			throw new Exception("@没有找到可接受的工作人员@节点访问规则错误:节点(" + town.getHisNode().getNodeID() + "," + town.getHisNode().getName() + "), 仅按用户组计算，没有找到人员:SQL=" + sql);

		}
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByTeamOrgOnly)
		{
			sql = "SELECT DISTINCT A.FK_Emp FROM Port_TeamEmp A, WF_NodeTeam B  WHERE A.FK_Team=B.FK_Team AND B.FK_Node=" + toNode.getNodeID();
			dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() > 0)
			{
				return dt;
			}

			throw new Exception("@没有找到可接受的工作人员@节点访问规则错误:节点(" + town.getHisNode().getNodeID() + "," + town.getHisNode().getName() + "), 仅按用户组计算，没有找到人员:SQL=" + sql);
		}

		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByTeamDeptOnly)
		{
			sql = "SELECT DISTINCT A.FK_Emp FROM Port_TeamEmp A, WF_NodeTeam B, Port_DeptEmp C WHERE A.FK_Emp=C.FK_Emp AND A.FK_Team=B.FK_Team AND B.FK_Node=" + toNode.getNodeID() + " AND C.FK_Dept='" + getWebUser().getDeptNo() + "'";
			dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() > 0)
			{
				return dt;
			}
			throw new Exception("@没有找到可接受的工作人员@节点访问规则错误:节点(" + town.getHisNode().getNodeID() + "," + town.getHisNode().getName() + "), 仅按用户组计算，没有找到人员:SQL=" + sql);
		}

		///#endregion

		///#region 56.按照指定的部门集合与节点设置角色交集计算.
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByStationSpecDepts)
		{
			Node nd = town.getHisNode();
			String sqlDepts = nd.ARDeptModelDeptsSQL(this.town.getWorkID()); // 获得部门的sqls.
			DataTable dtDepts = DBAccess.RunSQLReturnTable(sqlDepts);
			int dgModel = nd.GetParaInt("DGModel56", 0);
			if (dtDepts.Rows.size() == 1)
			{
				//如果只有一个部门.
				String deptNo = dtDepts.Rows.get(0).getValue(0).toString();

				///#region 判断递归模式. 0=递归并累加,递归到根节点,并把找到的人累加起来.
				if (dgModel == 0)
				{
					DataTable dtEmps = new DataTable(); //定义容器.
					dtEmps.Columns.Add("EmpNo");
					String empNos = "";
					String curEmpNo = "";
					while (true)
					{
						String mysql = " SELECT DISTINCT(FK_Emp) FROM Port_DeptEmpStation A,WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + nd.getNodeID() + " AND A.FK_Dept='" + deptNo + "'";
						dt = DBAccess.RunSQLReturnTable(mysql);
						//插入里面去.
						for (DataRow dr : dt.Rows)
						{
							curEmpNo = dr.getValue(0).toString();
							if(empNos.contains(curEmpNo+",") == false){
								DataRow mydr = dtEmps.NewRow();
								mydr.setValue(0, dr.getValue(0));
								dtEmps.Rows.add(mydr);
								empNos+=curEmpNo+",";
							}
						}
						//找到上一级部门.
						deptNo = DBAccess.RunSQLReturnStringIsNull("SELECT ParentNo FROM Port_Dept WHERE No='" + deptNo + "'", null);
						if (deptNo == null || deptNo.equals("0") == true)
						{
							break;
						}
						continue;
					}

					//判断是否有此数据.
					if (dtEmps.Rows.size() == 0 && town.getHisNode().getHisWhenNoWorker() == false)
					{
						throw new RuntimeException("err@到达节点[" + this.town.getHisNode().getName() + "],接受人规则是[按照指定的部门集合与节点设置角色交集计算],没有获得接受人.");
					}
					return dtEmps;
				}

				///#endregion 判断递归模式. 0=递归并累加,递归到根节点,并把找到的人累加起来.

				///#region 判断递归模式. 1=递归不累加,向根节点递归,如果找到人,就不在递归了.
				if (dgModel == 1)
				{
					while (true)
					{
						String mysql = " SELECT FK_Emp FROM Port_DeptEmpStation A,WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + nd.getNodeID() + " AND A.FK_Dept='" + deptNo + "'";
						dt = DBAccess.RunSQLReturnTable(mysql);
						if (dt.Rows.size() == 0)
						{
							mysql = " SELECT FK_Emp FROM Port_DeptEmp A,WF_NodeStation B WHERE A.StationNo=B.FK_Station AND B.FK_Node=" + nd.getNodeID() + " AND A.FK_Dept='" + deptNo + "'";
							dt = DBAccess.RunSQLReturnTable(mysql);
							if (dt.Rows.size() == 0) {
								deptNo = DBAccess.RunSQLReturnStringIsNull("SELECT ParentNo FROM Port_Dept WHERE No='" + deptNo + "'", null).toString();
								if (deptNo == null || deptNo.equals("0") == true) {
									throw new RuntimeException("err@到达节点[" + this.town.getHisNode().getName() + "],接受人规则是[按照指定的部门集合与节点设置角色交集计算],没有获得接受人.");
								}
								continue;
							}
						}
						return dt;
					}
				}

				///#endregion 判断递归模式.1=递归不累加,向根节点递归,如果找到人,就不在递归了.

				///#region 判断递归模式. 2=不递归, 仅仅按照指定的部门寻找.
				if (dgModel == 2)
				{
					String mysql = " SELECT FK_Emp FROM Port_DeptEmpStation A,WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + nd.getNodeID() + " AND A.FK_Dept='" + deptNo + "'";
					dt = DBAccess.RunSQLReturnTable(mysql);
					if (dt.Rows.size() == 0)
					{
						mysql = " SELECT FK_Emp FROM Port_DeptEmp A,WF_NodeStation B WHERE A.StationNo=B.FK_Station AND B.FK_Node=" + nd.getNodeID() + " AND A.FK_Dept='" + deptNo + "'";
						dt = DBAccess.RunSQLReturnTable(mysql);
						if (dt.Rows.size() == 0 && town.getHisNode().getHisWhenNoWorker() == false)
						{
							throw new RuntimeException("err@到达节点[" + this.town.getHisNode().getName() + "],接受人规则是[按照指定的部门集合与节点设置角色交集计算],没有获得接受人.");
						}
						//throw new RuntimeException("err@到达节点[" + this.town.getHisNode().getName() + "],接受人规则是[按照指定的部门集合与节点设置角色交集计算],没有获得接受人.");
					}
					return dt;
				}

				///#endregion 判断递归模式.2=不递归, 仅仅按照指定的部门寻找.
			}

			String sqlStations = "SELECT FK_Station FROM WF_NodeStation WHERE FK_Node=" + nd.getNodeID();
			//获得两个的交集.
			String mysql1 = " SELECT FK_Emp FROM Port_DeptEmpStation WHERE FK_Station IN(" + sqlStations + ") AND FK_Dept IN (" + sqlDepts + ")";
			dt = DBAccess.RunSQLReturnTable(mysql1);
			if (dt.Rows.size() == 0)
			{
				throw new RuntimeException("err@没有找到可接受的工作人员@到达节点[" + this.town.getHisNode().getName() + "],接受人规则是[按照指定的部门集合与节点设置角色交集计算],没有获得接受人.");
			}
			return dt;
		}

		///#endregion 按照指定的角色集合与部门的交集计算



		///#region 57. 按照指定的角色集合与节点设置部门交集计算.
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByStationSpecStas)
		{
			Node nd = town.getHisNode();
			String sqlStations = nd.ARStaModelStasSQL(this.town.getWorkID()); //获得部门的sqls.
			String sqlDepts = "SELECT FK_Dept FROM WF_NodeDept WHERE FK_Node=" + nd.getNodeID();
			DataTable dtDepts = DBAccess.RunSQLReturnTable(sqlDepts);
			if (dtDepts.Rows.size() == 1)
			{
				//如果只有一个部门.
				String deptNo = dtDepts.Rows.get(0).getValue(0).toString();
				while (true)
				{
					String mysql = "SELECT FK_Emp FROM Port_DeptEmpStation A WHERE FK_Station IN (" + sqlStations + ") AND FK_Dept='" + deptNo + "'";
					dt = DBAccess.RunSQLReturnTable(mysql);
					if (dt.Rows.size() == 0)
					{
						deptNo = DBAccess.RunSQLReturnStringIsNull("SELECT ParentNo FROM Port_Dept WHERE No='" + deptNo + "'",null).toString();
						if ((deptNo == null || deptNo.equals("0") == true) && town.getHisNode().getHisWhenNoWorker() == false)
						{
							throw new RuntimeException("err@到达节点[" + this.town.getHisNode().getName() + "],接受人规则是[按照指定的角色集合与节点设置部门交集计算],没有获得接受人.");
						}
						continue;
					}
					return dt;
				}
			}

			//获得两个的交集.
			String mysql1 = " SELECT FK_Emp FROM Port_DeptEmpStation WHERE FK_Station IN(" + sqlStations + ") AND FK_Dept IN (" + sqlDepts + ")";
			dt = DBAccess.RunSQLReturnTable(mysql1);
			if (dt.Rows.size() == 0)
			{
				throw new RuntimeException("err@到达节点[" + this.town.getHisNode().getName() + "],接受人规则是[按照指定的角色集合与节点设置部门交集计算],没有获得接受人.");
			}
			return dt;
		}

		///#endregion 按照指定的部门集合与岗位的交集计算





		///#region 仅按角色计算
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByStationOnly)
		{
			ps = new Paras();
			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			{
				//2020-4-25 按照角色倒序排序 修改原因队列模式时，下级角色处理后发给上级角色， 角色越高数值越小
				sql = "SELECT A.FK_Emp FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND A.OrgNo=" + dbStr + "OrgNo AND B.FK_Node=" + dbStr + "FK_Node ORDER BY A.FK_Station desc";
				ps.Add("OrgNo", getWebUser().OrgNo, false);
				ps.Add("FK_Node", town.getHisNode().getNodeID());
				ps.SQL = sql;
				dt = DBAccess.RunSQLReturnTable(ps);
			}
			else
			{
				//2020-4-25 按照角色倒序排序 修改原因队列模式时，下级角色处理后发给上级角色， 角色越高数值越小
				sql = "SELECT A.FK_Emp FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node ORDER BY A.FK_Station desc";
				ps.Add("FK_Node", town.getHisNode().getNodeID());
				ps.SQL = sql;
				dt = DBAccess.RunSQLReturnTable(ps);
			}
			if (dt.Rows.size() > 0)
			{
				return dt;
			}
			throw new Exception("@没有找到可接受的工作人员@节点访问规则错误:流程[" + town.getHisNode().getFlowName() + "]节点[" + town.getHisNode().getNodeID() + "," + town.getHisNode().getName() + "], 仅按角色计算，没有找到人员。");
		}

		///#endregion


		///#region 按配置的人员路由表计算
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByFromEmpToEmp)
		{
			String[] fromto = town.getHisNode().getDeliveryParas().split("[@]", -1);

			String defUser = "";

			for (String str : fromto)
			{
				if(DataType.IsNullOrEmpty(str))
					continue;
				str = str.replace("\n","");
				String[] kv = str.split("[,]", -1);

				if (kv[0].equals("Defalut") == true)
				{
					defUser = kv[1];
					continue;
				}

				if (Objects.equals(kv[0], getWebUser().getNo()))
				{
					String empTo = kv[1];
					//bp.port.Emp emp = new bp.port.Emp(empTo);
					DataRow dr = dt.NewRow();
					dr.setValue(0, empTo);
					//  dr[1] = emp.getName();
					dt.Rows.add(dr);
					return dt;
				}
			}

			if (DataType.IsNullOrEmpty(defUser) == false)
			{
				String empTo = defUser;
				DataRow dr = dt.NewRow();
				dr.setValue(0, empTo);
				dt.Rows.add(dr);
				return dt;
			}

			throw new RuntimeException("@接收人规则是按照人员路由表设置的，但是系统管理员没有为您配置路由,当前节点;" + town.getHisNode().getName());
		}

		//#region 路由计算: 部门2人员,格式: @1001,lisi@1002,zhaoliu 解释为: 财务部发给李四，开发部发给赵六.
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByFromDeptToEmp)
		{
			String[] fromto = town.getHisNode().getDeliveryParas().split("@");
			String defUser = "";
			for (String str : fromto)
			{
				if(DataType.IsNullOrEmpty(str))
					continue;
				str = str.replace("\n","");
				String[] kv = str.split(",");
				if (kv[0].equals("Defalut") == true)
				{
					defUser = kv[1];
					continue;
				}

				//如果碰到了当前的人员部门.
				if (kv[0].equals(WebUser.getDeptNo()) == true)
				{
					String empTo = kv[1];
					//BP.Port.Emp emp = new BP.Port.Emp(empTo);
					DataRow dr = dt.NewRow();

					dr.setValue(0, empTo);
					//  dr[1] = emp.Name;
					dt.Rows.add(dr);
					return dt;
				}
			}

			if (DataType.IsNullOrEmpty(defUser) == false)
			{
				String empTo = defUser;
				DataRow dr = dt.NewRow();
				dr.setValue(0, empTo);
				dt.Rows.add(dr);
				return dt;
			}
			throw new Exception("@接收人规则是按照人员路由表设置的，但是系统管理员没有为您配置路由,当前节点;" + town.getHisNode().getName());
		}
		//#endregion

		//#region 路由计算: 字段2人员,格式: @001,lisi@002,zhaoliu 解释为: 公章发给李四，财务章发给赵六.
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByFromAttrToEmp)
		{
			String attrKey = town.getHisNode().GetParaString("RouteAttr2Emp");
			if (DataType.IsNullOrEmpty(attrKey) == true)
				throw new Exception("err@节点[" + town.getHisNode().getName() + "]接收人规则配置[ByFromAttrToEmp]错误:没有配置字段.");

			if (this.currWn.rptGe.getEnMap().getAttrs().contains(attrKey) == false)
				throw new Exception("err@节点[" + town.getHisNode().getName() + "]接收人规则配置[ByFromAttrToEmp]错误:配置字段[" + attrKey + "],在表单里不存在.");

			String attrVal = this.currWn.rptGe.GetValStringByKey(attrKey);

			if (DataType.IsNullOrEmpty(attrVal) == true)
				throw new Exception("err@节点[" + town.getHisNode().getName() + "]接收人规则配置[ByFromAttrToEmp]错误:配置字段[" + attrKey + "]值为空.");

			//String[] fromto = town.getHisNode().getDeliveryParas().split(";");
			String[] fromto = town.getHisNode().getDeliveryParas().split("@");
			String defUser = "";
			for (String str : fromto)
			{
				if(DataType.IsNullOrEmpty(str))
					continue;
				str = str.replace("\n","");
				String[] kv = str.split(",");
				if (kv[0].equals("Defalut") == true)
				{
					defUser = kv[1];
					continue;
				}

				if (kv[0].equals(attrVal) == true)
				{
					String empTo = kv[1];
					//BP.Port.Emp emp = new BP.Port.Emp(empTo);
					DataRow dr = dt.NewRow();
					dr.setValue(0, empTo);
					//  dr[1] = emp.Name;
					dt.Rows.add(dr);
					return dt;
				}
			}

			if (DataType.IsNullOrEmpty(defUser) == false)
			{
				String empTo = defUser;
				DataRow dr = dt.NewRow();
				dr.setValue(0, empTo);
				dt.Rows.add(dr);
				return dt;
			}
			throw new Exception("@接收人规则是按照人员路由表设置的，但是系统管理员没有为您配置路由,当前节点;" + town.getHisNode().getName());
		}
		//#endregion



		///#region 按照自定义的URL来计算
//C# TO JAVA CONVERTER TASK: The following line could not be converted:
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySelfUrl)
		{
			ps = new Paras();
			ps.Add("FK_Node", this.town.getHisNode().getNodeID());
			ps.Add("WorkID", this.currWn.getHisWork().getOID());
			ps.SQL = "SELECT FK_Emp FROM WF_SelectAccper WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND AccType=0 ORDER BY IDX";
			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() == 0)
			{
				/*从上次发送设置的地方查询. */
				SelectAccpers sas = new SelectAccpers();
				int i = sas.QueryAccepterPriSetting(this.town.getHisNode().getNodeID());
				if (i == 0 && town.getHisNode().getHisWhenNoWorker() == false)
				{
					GenerWorkFlow gwf = new GenerWorkFlow(this.WorkID);
					if (DataType.IsNullOrEmpty(toNode.getDeliveryParas()) == true)
					{
						throw new RuntimeException("节点" + toNode.getNodeID() + "_" + toNode.getName() +"设置的接收人规则是自定义的URL,现在未获取到设置的信息");
					}
					else
					{
						throw new RuntimeException("BySelfUrl@" + toNode.getDeliveryParas() + "?FK_Flow=" + toNode.getFlowNo() + "&FK_Node=" + this.currWn.getHisNode().getNodeID() + "&ToNode=" + toNode.getNodeID() + "&WorkID=" + this.WorkID + "&PWorkID=" + gwf.getPWorkID() + "&FID=" + gwf.getFID());
					}
				}

				//插入里面.
				for (SelectAccper item : sas.ToJavaList())
				{
					DataRow dr = dt.NewRow();
					dr.setValue(0, item.getEmpNo());
					dt.Rows.add(dr);
				}
				return dt;
			}
			return dt;
		}

		///#endregion 按照自定义的URL来计算


		///#region 按照设置的WebAPI接口获取的数据计算 - 新版本.
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByAPIUrl)
		{
			//组织参数.
			String paras = "@WorkID=" + this.WorkID + "@OID=" + this.WorkID;

			Part part = new Part("AR" + this.town.getHisNode().getNodeID());
			String strs = part.ARWebApi(paras);
			if (strs.equals("err@") == true)
			{
				throw new RuntimeException(strs);
			}

			strs = strs.replace("，", ",");
			strs = strs.replace(";", ",");
			strs = strs.replace("；", ",");

			String[] mystars = strs.split("[,]", -1);

			for (String str : mystars)
			{
				DataRow dr = dt.NewRow();
				dr.setValue("No", str);
				dt.Rows.add(dr);
			}
			return dt;
		}

		///#endregion 按照设置的WebAPI接口获取的数据计算


		///#region 按照设置的WebAPI接口获取的数据计算(旧版本)
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByAPIUrl && 1 == 2)
		{
			//返回值
			String postData = "";
			//用户输入的webAPI地址
			String apiUrl = town.getHisNode().getDeliveryParas();
			if (apiUrl.contains("@WebApiHost")) //可以替换配置文件中配置的webapi地址
			{
				//apiUrl = apiUrl.replace("@WebApiHost", bp.difference.SystemConfig.getAppSettings().get("WebApiHost"));
			}
			//如果有参数
			if (apiUrl.contains("?"))
			{
				//api接口地址
				String apiHost = apiUrl.split("[?]", -1)[0];
				//api参数
				String apiParams = apiUrl.split("[?]", -1)[1];
				//参数替换
				apiParams = bp.wf.Glo.DealExp(apiParams, town.getHisWork());
				Hashtable bodyJson = new Hashtable();
				if (apiParams.contains("&"))
				{
					String[] bodyParams = apiParams.split("[&]", -1);
					for (String item : bodyParams)
					{
						String[] keyVals = item.split("[=]", -1);
						bodyJson.put(keyVals[0], keyVals[1]);
					}
				}
				else
				{
					String[] keyVals = apiParams.split("[=]", -1);
					bodyJson.put(keyVals[0], keyVals[1]);
				}

				//执行POST
				postData = bp.tools.PubGlo.HttpPostConnect(apiHost, bp.tools.Json.ToJson(bodyJson),"POST",true);

				if ((Objects.equals(postData, "[]") || Objects.equals(postData, "") || postData == null) && town.getHisNode().getHisWhenNoWorker() == false)
				{
					throw new RuntimeException("节点" + town.getHisNode().getNodeID() + "_" + town.getHisNode().getName() + "设置的WebAPI接口返回的数据出错，请检查接口返回值。");
				}

				dt = bp.tools.Json.ToDataTable(postData);
				return dt;
			}
			else
			{ //如果没有参数
				postData = bp.tools.PubGlo.HttpPostConnect(apiUrl, "", "GET", false);
				if ((Objects.equals(postData, "[]") || Objects.equals(postData, "") || postData == null) && town.getHisNode().getHisWhenNoWorker() == false)
				{
					throw new RuntimeException("节点" + town.getHisNode().getNodeID() + "_" + town.getHisNode().getName() + "设置的WebAPI接口返回的数据出错，请检查接口返回值。");
				}

				dt = bp.tools.Json.ToDataTable(postData);
				return dt;
			}
		}

		///#endregion 按照设置的WebAPI接口获取的数据计算


		///#region 按照组织模式人员选择器
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySelectedEmpsOrgModel)
		{
			ps = new Paras();
			ps.Add("FK_Node", this.town.getHisNode().getNodeID());
			ps.Add("WorkID", this.currWn.getHisWork().getOID());
			ps.SQL = "SELECT FK_Emp FROM WF_SelectAccper WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND AccType=0 ORDER BY IDX";
			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() == 0)
			{
				/*从上次发送设置的地方查询. */
				SelectAccpers sas = new SelectAccpers();
				int i = sas.QueryAccepterPriSetting(this.town.getHisNode().getNodeID());
				if (i == 0)
				{
					throw new RuntimeException("url@./WorkOpt/AccepterOfOrg.htm?FK_Flow=" + toNode.getFlowNo() + "&FK_Node=" + this.currWn.getHisNode().getNodeID() + "&ToNode=" + toNode.getNodeID() + "&WorkID=" + this.WorkID);
				}

				//插入里面.
				for (SelectAccper item : sas.ToJavaList())
				{
					DataRow dr = dt.NewRow();
					dr.setValue(0, item.getEmpName());
					dt.Rows.add(dr);
				}
				return dt;
			}
			return dt;
		}

		///#endregion 按照组织模式人员选择器

		///#region 选择其他组织的联络员
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySelectEmpByOfficer)
		{
			ps = new Paras();
			ps.Add("FK_Node", this.town.getHisNode().getNodeID());
			ps.Add("WorkID", this.currWn.getHisWork().getOID());
			ps.SQL = "SELECT FK_Emp FROM WF_SelectAccper WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND AccType=0 ORDER BY IDX";
			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() == 0)
			{
				/*从上次发送设置的地方查询. */
				SelectAccpers sas = new SelectAccpers();
				int i = sas.QueryAccepterPriSetting(this.town.getHisNode().getNodeID());
				if (i == 0)
				{
					throw new RuntimeException("url@./WorkOpt/AccepterOfOfficer.htm?FK_Flow=" + toNode.getFlowNo() + "&FK_Node=" + this.currWn.getHisNode().getNodeID() + "&ToNode=" + toNode.getNodeID() + "&WorkID=" + this.WorkID);
				}

				//插入里面.
				for (SelectAccper item : sas.ToJavaList())
				{
					DataRow dr = dt.NewRow();
					dr.setValue(0, item.getEmpName());
					dt.Rows.add(dr);
				}
				return dt;
			}
			return dt;
		}

		///#endregion 选择其他组织的联络员


		///#region 发送人的上级部门的负责人: 2022.2.20 benjing. by zhoupeng
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySenderParentDeptLeader)
		{
			Dept dept = new Dept(getWebUser().DeptNo);
			String deptNo = dept.getParentNo();
			sql = "SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + deptNo + "'";
			dt = DBAccess.RunSQLReturnTable(sql);
			String leaderNo = null;
			if (dt.Rows.size() == 1)
			{
				leaderNo = dt.Rows.get(0).getValue(0) instanceof String ? (String)dt.Rows.get(0).getValue(0) : null;
				//如果领导是当前操作员，就让其找上一级的部门领导。
				if (leaderNo != null && getWebUser().No.equals(leaderNo) == true)
				{
					leaderNo = null;
				}
			}

			if (dt.Rows.size() == 0 || DataType.IsNullOrEmpty(leaderNo) == true)
			{
				//如果没有找到,就到父节点去找.
				Dept pDept = new Dept(deptNo);
				sql = "SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + pDept.getNo() + "'";
				dt = DBAccess.RunSQLReturnTable(sql);
				return dt;
				// throw new Exception("err@按照 [发送人的上级部门的负责人] 计算接收人的时候出现错误，您没有维护部门[" + pDept.getName() +"]的部门负责人.");
			}
			return dt;
		}

		///#endregion 发送人的上级部门的负责人 2022.2.20 benjing.


		///#region 发送人上级部门指定的角色 2022.2.20 beijing. by zhoupeng
		if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySenderParentDeptStations)
		{
			//当前人员身份 sf
			Hashtable sf = GetEmpDeptBySFModel();
			empDept = sf.get("DeptNo").toString();
			empNo = sf.get("EmpNo").toString();

			Dept dept = new Dept(empDept);
			String deptNo = dept.getParentNo();

			sql = "SELECT A.FK_Emp,FK_Dept FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + town.getHisNode().getNodeID() + " AND A.FK_Dept='" + deptNo + "'";
			dt = DBAccess.RunSQLReturnTable(sql);
			/*if (dt.Rows.size() == 0)
			{
			    bp.port.Dept pDept = new bp.port.Dept(deptNo);
			    throw new Exception("err@按照 [发送人上级部门指定的角色] 计算接收人的时候出现错误，没有找到人，请检查节点绑定的角色以及该部门【" + pDept.getName() +"】下的人员设置的角色.");
			}*/
			return dt;
		}
		///#region 发送连续多级主管 - 指定角色模式 模式分为两种情况，先实现审批终点是角色的情况,另外一种通讯录模式没有实现
		//ByMLeaderRole

		if(town.getHisNode().getHisDeliveryWay()== DeliveryWay.ByMLeader0)
		{
			return ByMLeader0();
		}

		/* 如果执行节点 与 接受节点角色集合一致 */
		String currGroupStaNDs = this.currWn.getHisNode().getGroupStaNDs();
		String toNodeTeamStaNDs = town.getHisNode().getGroupStaNDs();

		if (DataType.IsNullOrEmpty(currGroupStaNDs) == false && currGroupStaNDs.equals(toNodeTeamStaNDs) == true &&
				this.currWn.getHisNode().GetParaInt("ShenFenModel", 0) == 0
				&& town.getHisNode().GetParaInt("ShenFenModel", 0) == 0)
		{
			/* 说明，就把当前人员做为下一个节点处理人。*/
			DataRow dr = dt.NewRow();
			if (dt.Columns.size()== 0)
			{
				dt.Columns.Add("No");
			}

			dr.setValue(0, getWebUser().getNo());
			dt.Rows.add(dr);
			return dt;
		}

		//获取当前人员信息的
		Hashtable ht = GetEmpDeptBySFModel();
		empDept = ht.get("DeptNo").toString();
		empNo = ht.get("EmpNo").toString();
		//按角色智能计算, 要判断切片模式,还是集合模式.
		///#region 按角色智能计算, 切片模式. 需要对每个角色都要找到接受人，然后把这些接受人累加起来.
		if (toNode.getDeliveryStationReqEmpsWay() == 1 || toNode.getDeliveryStationReqEmpsWay() == 2)
		{
			DataTable staDT = DBAccess.RunSQLReturnTable("SELECT FK_Station From WF_NodeStation WHERE FK_Node="+toNode.getNodeID());
			String stas="";
			for(DataRow dr : staDT.Rows){
				stas+=dr.getValue(0)+",";
			}
			String[] deptNos = empDept.split("[,]", -1);
			String[] temps = stas.split("[,]", -1);
			for(String deptNo : deptNos){
				if(DataType.IsNullOrEmpty(deptNo)==true)
					continue;
				for (String str : temps)
				{
					if(DataType.IsNullOrEmpty(str))
						continue;
					//求一个角色下的人员.
					DataTable mydt1 = WorkFlowBuessRole.FindWorker_GetEmpsByDeptAI(str, deptNo);

					//如果是严谨模式.
					if (toNode.getDeliveryStationReqEmpsWay() == 1 && mydt1.Rows.size() == 0)
					{
						Station st = new Station(str);
						throw new RuntimeException("@角色[" + st.getName() +"]下，没有找到人不能发送下去，请检查组织结构是否完整。");
					}

					//累加.
					for (DataRow dr : mydt1.Rows)
					{
						DataRow mydr = dt.NewRow();
						mydr.setValue(0, dr.getValue(0).toString());
						dt.Rows.add(mydr);
					}
				}
			}

			if(dt.Rows.size()>0)
				return dt;
		}

		/* 如果执行节点 与 接受节点角色集合不一致 */
		if ((DataType.IsNullOrEmpty(toNodeTeamStaNDs) == true && DataType.IsNullOrEmpty(currGroupStaNDs) == true) || currGroupStaNDs.equals(toNodeTeamStaNDs) == false)
		{
			/* 没有查询到的情况下, 先按照本部门计算。添加FK_Dept*/


			sql = "SELECT FK_Emp as No,FK_Dept FROM Port_DeptEmpStation A, WF_NodeStation B         WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node AND A.FK_Dept IN(" + DataType.DealFromatSQLWhereIn(empDept) + ")";
			ps = new Paras();
			ps.SQL = sql;
			ps.Add("FK_Node", town.getHisNode().getNodeID());

			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() == 0)
			{
				NodeStations nextStations = town.getHisNode().getNodeStations();
				if (nextStations.size() == 0)
				{
					throw new RuntimeException("@没有找到可接受的工作人员@节点没有角色:" + town.getHisNode().getNodeID() + "  " + town.getHisNode().getName());
				}
			}
			else
			{
				boolean isInit = false;
				for (DataRow dr : dt.Rows)
				{
					if (Objects.equals(dr.getValue(0).toString(), getWebUser().getNo()))
					{
						/* 如果角色分组不一样，并且结果集合里还有当前的人员，就说明了出现了当前操作员，拥有本节点上的角色也拥有下一个节点的工作角色
						 导致：节点的分组不同，传递到同一个人身上。 */
						isInit = true;
					}
				}


///#warning edit by peng, 用来确定不同角色集合的传递包含同一个人的处理方式。

				//  if (isInit == false || isInit == true)
				return dt;
			}
		}

		/*这里去掉了向下级别寻找的算法. */


		/* 没有查询到的情况下, 按照最大匹配数 提高一个级别计算，递归算法未完成。
		 * 因为:以上已经做的角色的判断，就没有必要在判断其它类型的节点处理了。
		 * */
		String[] deptNos = empDept.split(",");
		for(String deptNo : deptNos){
			if(DataType.IsNullOrEmpty(deptNo))
				continue;
			Object tempVar = deptNo;
			String nowDeptID = tempVar instanceof String ? (String)tempVar : null;
			//获取当前节点的岗位

			//第1步:直线父级寻找.
			while (true)
			{
				Dept myDept = new Dept(nowDeptID);
				nowDeptID = myDept.getParentNo();
				if (Objects.equals(nowDeptID, "-1") || Objects.equals(nowDeptID.toString(), "0"))
				{
					break; //一直找到了最高级仍然没有发现，就跳出来循环从当前操作员人部门向下找。
					//throw new RuntimeException("@按角色计算没有找到(" + town.getHisNode().getName() + ")接受人.");
				}

				//检查指定的父部门下面是否有该人员.
				DataTable mydtTemp = this.Func_GenerWorkerList_SpecDept(nowDeptID, empNo);
				if (mydtTemp.Rows.size() != 0)
				{
					return mydtTemp;
				}

				continue;
			}

			//第2步：父级的平级.如果是0查找，1不查找父级的平级
			int StationFindWay = town.getHisNode().GetParaInt("StationFindWay", 0);
			if (StationFindWay == 0)
			{
				Object tempVar2 = deptNo;
				nowDeptID = tempVar2 instanceof String ? (String)tempVar2 : null;
				while (true)
				{
					Dept myDept = new Dept(nowDeptID);
					nowDeptID = myDept.getParentNo();
					if (Objects.equals(nowDeptID, "-1") || Objects.equals(nowDeptID.toString(), "0"))
					{
						break; //一直找到了最高级仍然没有发现，就跳出来循环从当前操作员人部门向下找。
						//throw new RuntimeException("@按角色计算没有找到(" + town.getHisNode().getName() + ")接受人.");
					}

					//该部门下的所有子部门是否有人员.
					DataTable mydtTemp = Func_GenerWorkerList_SpecDept_SameLevel(nowDeptID, empNo);
					if (mydtTemp.Rows.size() != 0)
					{
						return mydtTemp;
					}
				}
			}
			//向下级寻找.
			if (StationFindWay == 2)
			{
				Object tempVar2 = deptNo;
				nowDeptID = tempVar2 instanceof String ? (String)tempVar2 : null;

				//找下级1，2,3 级.
				Depts subDepts = new Depts();
				subDepts.Retrieve("ParentNo", nowDeptID); //1级子部门集合.
				for (Dept sbDept : subDepts.ToJavaList())
				{
					//该部门下的所有子部门是否有人员.
					DataTable mydtTemp = Func_GenerWorkerList_SpecDept_SameLevel(sbDept.getNo(), empNo);
					if (mydtTemp.Rows.size() != 0)
						return mydtTemp;

					//找下级1，2级.
					Depts subSubDepts = new Depts();
					subSubDepts.Retrieve("ParentNo", sbDept.getNo());
					for (Dept subSubDept : subSubDepts.ToJavaList())  //2级子部门集合.
					{
						//该部门下的所有子部门是否有人员.
						DataTable mydtTemp1 = Func_GenerWorkerList_SpecDept_SameLevel(subSubDept.getNo(), empNo);
						if (mydtTemp1.Rows.size() != 0)
							return mydtTemp1;

						//找下级1，2级.
						Depts sub3Depts = new Depts();
						sub3Depts.Retrieve("ParentNo", subSubDept.getNo());
						for (Dept sub3Dept : sub3Depts.ToJavaList())  //3级子部门集合.
						{
							//该部门下的所有子部门是否有人员.
							DataTable mydtTemp2 = Func_GenerWorkerList_SpecDept_SameLevel(sub3Dept.getNo(), empNo);
							if (mydtTemp2.Rows.size() != 0)
								return mydtTemp2;
						}
					}
				}
			}
			/*如果向上找没有找到，就考虑从本级部门上向下找。只找一级下级的平级 */
			Object tempVar3 = deptNo;
			nowDeptID = tempVar3 instanceof String ? (String)tempVar3 : null;

			//递归出来子部门下有该角色的人员 返回数据添加FK_Dept
			DataTable mydt = Func_GenerWorkerList_SpecDept_SameLevel(nowDeptID, empNo);
			if(mydt.Rows.size()!=0)
				return mydt;
		}
		//如果递归没有找到人,就全局搜索角色.
		sql = "SELECT A.FK_Emp,FK_Dept FROM  Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node ORDER BY A.FK_Emp";
		ps = new Paras();
		ps.Add("FK_Node", town.getHisNode().getNodeID());
		ps.SQL = sql;
		dt = DBAccess.RunSQLReturnTable(ps);

		if (dt.Rows.size() > 0)
			return dt;

		throw new Exception("@没有找到可接受的工作人员@按角色智能计算没有找到(" + town.getHisNode().getName() + ")接受人 @当前工作人员:" + getWebUser().getNo() + ",名称:" + getWebUser().getName() +" , 部门编号:" + getWebUser().getDeptNo() + " 部门名称：" + getWebUser().DeptName);


		///#endregion  按照角色来执行。
	}
	/// <summary>
	/// 连续多级主管.
	/// </summary>
	/// <returns></returns>
	/// <exception cref="Exception"></exception>
	private DataTable ByMLeader0() throws Exception {



		//当前人员身份 sf
		Hashtable sf = GetEmpDeptBySFModel();
		String empDept = sf.get("DeptNo").toString();
		String empNo = sf.get("EmpNo").toString();
		String fromUserNo = "";
		String leader = "";
		Emp emp = new Emp();

		//若人员身份是按部门编号，不能以当前人员账号为empno
		if(empNo != "" && empNo != null){
			//假定身份是当前人员.
			fromUserNo = empNo;
			emp = new Emp(fromUserNo);
		}


		DataTable dt = new DataTable();
		dt.Columns.Add(new DataColumn("No", String.class));
		dt.Columns.Add(new DataColumn("Name", String.class));

		Node toNode = this.town.getHisNode();

		//如果是多人处理规则，必须配置队列模式，这里强制修改。
		if (toNode.getTodolistModel() != TodolistModel.Order)
		{
			toNode.setTodolistModel(TodolistModel.Order);
			toNode.Update();
			// throw new Exception("err@节点["+toNode.Name+"]连续多级主管审批，配置的接收人规则必须是");
		}

		//获取当前节点绑定的岗位.
		NodeStations nds = new NodeStations();
		nds.Retrieve("FK_Node", toNode.getNodeID());

		//  #region  判断0: 没有任何配置,就直接找他的领导,一直找到头
		if (toNode.GetParaInt("JiBieModel") == 0 && nds.size() == 0)
		{
			for (int i = 0; i < 100; i++)
			{
				if(fromUserNo != "" && fromUserNo !=null){
					emp = new Emp(fromUserNo);
				}
				leader = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Emp WHERE No='" + fromUserNo + "'");
				//没有直属领导就到，部门领导去找.
				if(DataType.IsNullOrEmpty(emp.getUserID()) == false){
					if (DataType.IsNullOrEmpty(leader) == true){
						leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + emp.getDeptNo() + "' AND B.Leader IS  Not null  ");
					}

					if (fromUserNo.equals(leader)){
						leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No=(select ParentNo from port_dept where No='" + emp.getDeptNo() + "') AND B.Leader IS  Not null");
					};

					if (leader == null)
					{
						throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo()  + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
					}
				}else{
					if (DataType.IsNullOrEmpty(leader) == true)
						leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + empDept + "' AND B.Leader IS  Not null  ");

					if (fromUserNo.equals(leader)){
						leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No=(select ParentNo from port_dept where No='" + empDept + "') AND B.Leader IS  Not null");
					};

					if (leader == null)
					{
						throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo() + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
					}

				}

				//if (fromUserNo.equals(leader)) break;

				Emp empLeader = new Emp(leader);

				//增加到里面.
				DataRow dr = dt.NewRow();
				dr.setValue(0,empLeader.getNo());
				dr.setValue(1,empLeader.getName());
				dt.Rows.add(dr);
				fromUserNo = leader;  //设置当前的人员.

				//判断是否是最高部门？
				Dept dept = new Dept(empLeader.getDeptNo());
				if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
				{
					if (dept.getParentNo().equals("0") == true)
						break;
				}
				else
				{
					if (dept.getNo().equals(WebUser.getOrgNo()) == true)
						break;
				}
			}
		}
		//#endregion 判断0: 没有任何配置,就直接找他的领导,一直找到头

		// #region  判断1: 仅仅配置岗位的情况.
		if (toNode.GetParaInt("JiBieModel") == 0 && nds.size() != 0)
		{
			for (int i = 0; i < 100; i++)
			{
				if(DataType.IsNullOrEmpty(fromUserNo) == false){
					emp = new Emp(fromUserNo);
				}

				//0智能计算判断领导
				if(toNode.GetParaInt("LeaderType") == 0){
					leader = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Emp WHERE No='" + fromUserNo + "'");
					//没有直属领导就到，部门领导去找.
					if(DataType.IsNullOrEmpty(emp.getUserID()) == false){
						if (DataType.IsNullOrEmpty(leader) == true){
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + emp.getDeptNo() + "' AND B.Leader IS  Not null  ");
						}

						if (fromUserNo.equals(leader)){
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No=(select ParentNo from port_dept where No='" + emp.getDeptNo() + "') AND B.Leader IS  Not null");
						};

						if (leader == null)
						{
							throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo()  + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
						}
					}else{
						if (DataType.IsNullOrEmpty(leader) == true)
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + empDept + "' AND B.Leader IS  Not null  ");

						if (fromUserNo.equals(leader)){
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No=(select ParentNo from port_dept where No='" + empDept + "') AND B.Leader IS  Not null");
						};

						if (leader == null)
						{
							throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo() + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
						}

					}
				}

				//1仅按部门领导
				if(toNode.GetParaInt("LeaderType") == 1){
					//查找部门领导
					if(DataType.IsNullOrEmpty(emp.getUserID()) == false){
						leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + emp.getDeptNo() + "' AND B.Leader IS  Not null  ");
						if (fromUserNo.equals(leader)){
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No=(select ParentNo from port_dept where No='" + emp.getDeptNo() + "') AND B.Leader IS  Not null");
						};

						if (leader == null)
						{
							throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo()  + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
						}
					}else{
						if (DataType.IsNullOrEmpty(leader) == true)
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + empDept + "' AND B.Leader IS  Not null  ");

						if (fromUserNo.equals(leader)){
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No=(select ParentNo from port_dept where No='" + empDept + "') AND B.Leader IS  Not null");
						};

						if (leader == null)
						{
							throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo() + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
						}

					}
				}
				//仅按人员直属领导
				if(toNode.GetParaInt("LeaderType") == 2){
					if(DataType.IsNullOrEmpty(emp.getUserID()) == false){
						leader = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Emp WHERE No='" + emp.getUserID() + "'");

						if (leader == null)
						{
							throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo()  + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
						}
					}else{
						leader = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Emp WHERE No='" + fromUserNo + "'");


						if (leader == null)
						{
							throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo() + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
						}

					}
				}



				Emp empLeader = new Emp(leader);

				//增加到里面.
				DataRow dr = dt.NewRow();
				dr.setValue(0,empLeader.getNo());
				dr.setValue(1,empLeader.getName());
				dt.Rows.add(dr);
				fromUserNo = leader;  //设置当前的人员.

				//判断是否是最高部门？
				Dept dept = new Dept(empLeader.getDeptNo());
				if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
				{
					if (dept.getParentNo().equals("0") == true)
						break;
				}
				else
				{
					if (dept.getNo().equals(WebUser.getOrgNo()) == true)
						break;
				}


				//判断当前人员是否拥有岗位？
				String sql = "SELECT * FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND A.FK_Emp='" + leader + "' AND B.FK_Node=" + toNode.getNodeID();
				if (DBAccess.RunSQLReturnTable(sql).Rows.size() != 0)
					break;



			}
		}
		// #endregion 判断1: 仅仅配置岗位的情况

		//#region  判断2: 仅仅配置级别的情况.
		if (toNode.GetParaInt("JiBieModel") == 1 && nds.size() == 0)
		{
			for (int i = 0; i <= toNode.GetParaInt("JiBieLevel"); i++)
			{
				if(fromUserNo != "" && fromUserNo !=null){
					emp = new Emp(fromUserNo);
				}
				//智能模式计算领导
				if(toNode.GetParaString("LeaderType") == "0"){
					leader = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Emp WHERE No='" + fromUserNo + "'");
					//没有直属领导就到，部门领导去找.
					if(DataType.IsNullOrEmpty(emp.getUserID()) == false){
						if (DataType.IsNullOrEmpty(leader) == true){
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + emp.getDeptNo() + "' AND B.Leader IS  Not null  ");
						}

						if (fromUserNo.equals(leader)){
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No=(select ParentNo from port_dept where No='" + emp.getDeptNo() + "') AND B.Leader IS  Not null");
						};

						if (leader == null)
						{
							throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo()  + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
						}
					}else{
						if (DataType.IsNullOrEmpty(leader) == true)
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + empDept + "' AND B.Leader IS  Not null  ");

						if (fromUserNo.equals(leader)){
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No=(select ParentNo from port_dept where No='" + empDept + "') AND B.Leader IS  Not null");
						};

						if (leader == null)
						{
							throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo() + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
						}

					}
				}
				//仅按部门领导
				if(toNode.GetParaString("LeaderType") == "1"){
					leader = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Emp WHERE No='" + fromUserNo + "'");
					//没有直属领导就到，部门领导去找.
					if(DataType.IsNullOrEmpty(emp.getUserID()) == false){
						if (DataType.IsNullOrEmpty(leader) == true){
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + emp.getDeptNo() + "' AND B.Leader IS  Not null  ");
						}

						if (fromUserNo.equals(leader)){
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No=(select ParentNo from port_dept where No='" + emp.getDeptNo() + "') AND B.Leader IS  Not null");
						};

						if (leader == null)
						{
							throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo()  + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
						}
					}else{
						if (DataType.IsNullOrEmpty(leader) == true)
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + empDept + "' AND B.Leader IS  Not null  ");

						if (fromUserNo.equals(leader)){
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No=(select ParentNo from port_dept where No='" + empDept + "') AND B.Leader IS  Not null");
						};

						if (leader == null)
						{
							throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo() + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
						}

					}
				}
				//仅按人员领导
				if(toNode.GetParaString("LeaderType") == "2"){
					leader = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Emp WHERE No='" + fromUserNo + "'");
					//没有直属领导就到，部门领导去找.
					if(DataType.IsNullOrEmpty(emp.getUserID()) == false){
						if (DataType.IsNullOrEmpty(leader) == true){
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + emp.getDeptNo() + "' AND B.Leader IS  Not null  ");
						}

						if (fromUserNo.equals(leader)){
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No=(select ParentNo from port_dept where No='" + emp.getDeptNo() + "') AND B.Leader IS  Not null");
						};

						if (leader == null)
						{
							throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo()  + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
						}
					}else{
						if (DataType.IsNullOrEmpty(leader) == true)
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + empDept + "' AND B.Leader IS  Not null  ");

						if (fromUserNo.equals(leader)){
							leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No=(select ParentNo from port_dept where No='" + empDept + "') AND B.Leader IS  Not null");
						};

						if (leader == null)
						{
							throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo() + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
						}

					}
				}

				//if (fromUserNo.equals(leader)) break;

				Emp empLeader = new Emp(leader);
				//增加到里面.
				DataRow dr = dt.NewRow();
				dr.setValue(0,empLeader.getNo());
				dr.setValue(1,empLeader.getName());
				dt.Rows.add(dr);

				fromUserNo = leader;  //设置当前的人员.

				//判断是否是最高部门？
				Dept dept = new Dept(empLeader.getDeptNo());
				if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
				{
					if (dept.getParentNo().equals("0") == true)
						break;
				}
				else
				{
					if (dept.getNo().equals(WebUser.getOrgNo()) == true)
						break;
				}
			}
		}
		//#endregion 判断2: 仅仅配置级别的情况

		//#region  判断3: 配置级别+岗位.
		if (toNode.GetParaInt("JiBieModel") == 1 && nds.size() != 0)
		{
			for (int i = 0; i <= toNode.GetParaInt("JiBieLevel"); i++)
			{
				leader = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Emp WHERE No='" + fromUserNo + "'");
				//没有直属领导就到，部门领导去找.
				if(DataType.IsNullOrEmpty(emp.getUserID()) == false){
					if (DataType.IsNullOrEmpty(leader) == true){
						leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + emp.getDeptNo() + "' AND B.Leader IS  Not null  ");
					}

					if (fromUserNo.equals(leader)){
						leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No=(select ParentNo from port_dept where No='" + emp.getDeptNo() + "') AND B.Leader IS  Not null");
					};

					if (leader == null)
					{
						throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo()  + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
					}
				}else{
					if (DataType.IsNullOrEmpty(leader) == true)
						leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + empDept + "' AND B.Leader IS  Not null  ");

					if (fromUserNo.equals(leader)){
						leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No=(select ParentNo from port_dept where No='" + empDept + "') AND B.Leader IS  Not null");
					};

					if (leader == null)
					{
						throw new Exception("err@组织结构配置不完善,没有为[" + emp.getNo() + "," + emp.getName() + "] 配置他的直属领导或部门领导.");
					}

				}

				//if (fromUserNo.equals(leader)) break;

				Emp empLeader = new Emp(leader);
				//增加到里面.
				DataRow dr = dt.NewRow();
				dr.setValue(0,empLeader.getNo());
				dr.setValue(1,empLeader.getName());
				dt.Rows.add(dr);

				fromUserNo = leader;  //设置当前的人员.


				//判断是否是最高部门？
				Dept dept = new Dept(empLeader.getDeptNo());
				if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
				{
					if (dept.getParentNo().equals("0") == true)
						break;
				}
				else
				{
					if (dept.getNo().equals(WebUser.getOrgNo()) == true)
						break;
				}

				//判断当前人员是否拥有岗位？
				String sql = "SELECT * FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND A.FK_Emp='" + leader + "' AND B.FK_Node=" + toNode.getNodeID();
				if (DBAccess.RunSQLReturnTable(sql).Rows.size() != 0)
					break;

			}
		}
		//#endregion 判断2: 仅仅配置级别的情况

		//如果是去掉最后一个接受人.
		if (toNode.GetValIntByKey("CutEnderEmp") == 1 && dt.Rows.size() >= 2)
		{
			dt.Rows.remove(dt.Rows.get(dt.Rows.size() - 1));
		}
		return dt;
	}
	/// <summary>
	/// 根据部门编号获得
	/// </summary>
	/// <param name="dt"></param>
	/// <param name="nd"></param>
	/// <param name="deptNo"></param>
	/// <returns></returns>
	private DataTable ByDeptAndStationExt(DataTable dt, Node nd, String deptNo) throws Exception {
		String sql = "SELECT A.No,A.Name FROM Port_Emp A, Port_DeptEmpStation B, WF_NodeStation C ";
		sql += " WHERE A.No=B.FK_Emp AND B.FK_Station=C.FK_Station AND C.FK_Node='" + nd.getNodeID() + "' AND B.FK_Dept='" + deptNo + "'";
		DataTable mydt = DBAccess.RunSQLReturnTable(sql);

		//合并数据.
		for (DataRow mydr : mydt.Rows)
		{
			String myNo = mydr.getValue(0).toString();
			String myName = mydr.getValue(1).toString();

			boolean isHave = false;
			for (DataRow dr : dt.Rows)
			{
				String no = dr.getValue(0).toString();
				if (myNo.contains(no) == true)
				{
					isHave = true;
					break;
				}
			}
			if (isHave == true)
				continue;
			DataRow dataRow = dt.NewRow();
			dataRow.setValue(0, myNo);
			dt.Rows.add(dataRow);
		}

		Depts depts = new Depts();
		depts.Retrieve("ParentNo", deptNo);
		for (Dept dept : depts.ToJavaList())
		{
			dt = ByDeptAndStationExt(dt, nd, dept.getNo());
		}
		return dt;
	}


	private Hashtable GetEmpDeptBySFModel() throws Exception {
		Node nd = town.getHisNode();
		Hashtable ht = new Hashtable();
		//身份模式.
		int sfModel = nd.GetParaInt("ShenFenModel");

		//身份参数.
		String sfVal = nd.GetParaString("ShenFenVal");

		//按照当前节点的身份计算
		if (sfModel == 0)
		{
			ht.put("EmpNo", WebUser.getNo());
			ht.put("DeptNo", WebUser.getDeptNo());
			return ht;
		}
		//按照指定节点的身份计算.
		if (sfModel == 1)
		{
			if (DataType.IsNullOrEmpty(sfVal))
				sfVal =String.valueOf(currWn.getHisNode().getNodeID());

			Paras ps = new Paras();
			ps.SQL = "SELECT FK_Emp,FK_Dept FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node Order By RDT DESC";
			ps.Add("OID", this.WorkID);
			ps.Add("FK_Node",  Integer.parseInt(sfVal));

			DataTable dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() == 0) {
				//如果启用了计算未来处理人
				if(currWn.getHisNode().getHisFlow().getItIsFullSA() == true){
					ps.SQL = "SELECT FK_Emp,FK_Dept FROM WF_SelectAccper WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "OID";
					dt = DBAccess.RunSQLReturnTable(ps);
					if (dt.Rows.size() == 0) {
						throw new Exception("err@未找到接收人，到达节点[" + this.town.getHisNode().getNodeID() + "," + town.getHisNode().getName() + "]，" +
								"流程编号["+currWn.getHisNode().getHisFlow().getNo()+"]，WorkID["+this.WorkID+"]，" +
								"身份模式["+sfModel+"]，身份参数["+sfVal+"]，请查询数据表WF_GenerWorkerlist、WF_SelectAccper");
					}
				}else {
					throw new Exception("err@未找到接收人，到达节点[" + this.town.getHisNode().getNodeID() + "," + town.getHisNode().getName() + "]，" +
							"流程编号["+currWn.getHisNode().getHisFlow().getNo()+"]，WorkID["+this.WorkID+"]，" +
							"身份模式["+sfModel+"]，身份参数["+sfVal+"]，请查询数据表WF_GenerWorkerlist、WF_SelectAccper");
				}
			}
			ht.put("EmpNo", dt.Rows.get(0).getValue(0).toString());
			ht.put("DeptNo", dt.Rows.get(0).getValue(1).toString());
		}

		//按照 字段的值的人员编号作为身份计算.
		if (sfModel == 2)
		{
			if (DataType.IsNullOrEmpty(sfVal) == true)
				throw new Exception("err@流程模板配置错误，到达节点[" + this.town.getHisNode().getNodeID() + "," + town.getHisNode().getName() +"]根据字段值作为人员编号，没有配置字段值:" + sfVal);
			//获得字段的值.
			String empNo = "";
			if (currWn.getHisNode().getHisFormType() == NodeFormType.RefOneFrmTree)
				empNo = currWn.getHisWork().GetValStrByKey(sfVal);
			else
				empNo = currWn.rptGe.GetValStrByKey(sfVal);
			bp.port.Emp emp = new bp.port.Emp();
			emp.setUserID(empNo);
			if (emp.RetrieveFromDBSources() == 0)
				throw new Exception("err@根据字段值:" + sfVal + "在Port_Emp中没有找到人员信息");
			ht.put("EmpNo", emp.getNo());
			ht.put("DeptNo", emp.getDeptNo());
		}
		//按照字段的值作为部门编号
		if (sfModel == 3){
			if (DataType.IsNullOrEmpty(sfVal) == true)
				throw new Exception("err@流程模板配置错误，到达节点[" + this.town.getHisNode().getNodeID() + "," + town.getHisNode().getName() +"]根据字段值作为人员编号，没有配置字段值:" + sfVal);
			//获得字段的值.
			String deptNo = "";
			if (currWn.getHisNode().getHisFormType() == NodeFormType.RefOneFrmTree)
				deptNo = currWn.getHisWork().GetValStrByKey(sfVal);
			else
				deptNo = currWn.rptGe.GetValStrByKey(sfVal);
			ht.put("DeptNo", deptNo);
			ht.put("EmpNo", "");
		}
		//按照WF_GenerWorkFlow中AtPara中的字段值作为部门编号
		if (sfModel == 4){
			if (DataType.IsNullOrEmpty(sfVal) == true)
				throw new Exception("err@流程模板配置错误，到达节点[" + this.town.getHisNode().getNodeID() + "," + town.getHisNode().getName() +"]根据字段值作为人员编号，没有配置字段值:" + sfVal);
			AtPara atPara = currWn.getHisGenerWorkFlow().getatPara();
			boolean isHaveVal = false;
			for(String key : atPara.getHisHT().keySet()){
				if(DataType.IsNullOrEmpty(key))
					continue;
				if(key.equals(sfVal) == true){
					isHaveVal = true;
					ht.put("DeptNo", atPara.GetValStrByKey(key));
					ht.put("EmpNo", WebUser.getNo());
					break;
				}

			}
			if(isHaveVal == false)
				throw new Exception("err@人员身份按系统参数(部门编号)作为人员身份获取错误，到达节点[" + this.town.getHisNode().getNodeID() + "," + town.getHisNode().getName() +"]根据WF_GenerWorkFlow中AtPara没有获取到:" + sfVal+"的值");
		}
		if (sfModel == 5)
		{
			if (DataType.IsNullOrEmpty(sfVal) == true)
				throw new Exception("err@流程模板配置错误，到达节点[" + this.town.getHisNode().getNodeID() + "," + town.getHisNode().getName() + "]根据字段值作为人员编号，没有配置字段值:" + sfVal);
			//获得字段的值.
			String deptNo = "";
			if (currWn.getHisNode().getHisFormType() == NodeFormType.RefOneFrmTree)
				deptNo = currWn.getHisWork().GetValStrByKey(sfVal);
			else
				deptNo = currWn.rptGe.GetValStrByKey(sfVal);

			Dept dept = new Dept(deptNo);
			if (DataType.IsNullOrEmpty(dept.getLeader()) == true)
				throw new Exception("err@部门[" + dept.getName() + "],没有维护部门领导，导致身份无法获取.");

			ht.put("DeptNo", deptNo);
			DataTable empNo = DBAccess.RunSQLReturnTable("select No From Port_Emp where FK_Dept ='"+deptNo+"'");
			if (empNo.Rows.size() == 0)
				throw new Exception("err@部门[" + dept.getName() + "],没有维护部门人员，导致身份无法获取.");
			ht.put("EmpNo",empNo.Rows.get(0).getValue("No"));
		}

		return ht;
	}
	/**
	 找部门的领导
	 @return
	 */
	private DataTable ByDeptLeader() throws Exception {

		String sqlDepts = this.town.getHisNode().ARDeptModelDeptsSQL(this.town.getWorkID()); // 获得部门的sqls.
		DataTable dtDepts = DBAccess.RunSQLReturnTable(sqlDepts);

		DataTable dt = new DataTable();
		dt.Columns.Add("No");
		dt.Columns.Add("Name");

		String depts = "";
		for (DataRow dr : dtDepts.Rows)
		{
			//depts += dr.get("FK_Dept").toString() + ",";
			depts += dr.getValue(0).toString() + ",";

		}
		depts = bp.port.Glo.GenerWhereInSQL(depts);

		String sql = "";
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			sql = "SELECT a.UserID as No,a.Name FROM  Port_Emp a, Port_Dept b  WHERE a.UserID=b.Leader AND B.No in (" + depts + ") ";
		else
			sql = "SELECT a.No,a.Name FROM  Port_Emp a, Port_Dept b  WHERE a.No=b.Leader AND B.No in (" + depts + ") ";
		return DBAccess.RunSQLReturnTable(sql);
	}

	private DataTable ByDeptSpecer() throws Exception {

		String sqlDepts = this.town.getHisNode().ARDeptModelDeptsSQL(this.town.getWorkID()); // 获得部门的sqls.
		DataTable dtDepts = DBAccess.RunSQLReturnTable(sqlDepts);

		DataTable dt = new DataTable();
		dt.Columns.Add("No");
		dt.Columns.Add("Name");

		String depts = "";
		for (DataRow dr : dtDepts.Rows) {
//			depts += dr.get(0).toString() + ",";
			depts += dr.getValue(0).toString() + ",";
		}
		depts = bp.port.Glo.GenerWhereInSQL(depts);

		String sql = "";
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			sql = "SELECT a.UserID as No,a.Name FROM  Port_Emp a, Port_Dept b  WHERE a.UserID=b.Specer AND B.No in (" + depts + ") ";
		else
			sql = "SELECT a.No,a.Name FROM  Port_Emp a, Port_Dept b  WHERE a.No=b.Specer AND B.No in (" + depts + ") ";
		return DBAccess.RunSQLReturnTable(sql);
	}
	/**
	 找部门的分管领导

	 @return
	 */
// C# TO JAVA CONVERTER TASK: Local functions are not converted by C# to Java Converter:
	private DataTable ByDeptShipLeader() throws Exception {

		Node nd = town.getHisNode();

		//身份模式.
		int sfModel = nd.GetParaInt("ShenFenModel");

		//身份参数.
		String sfVal = nd.GetParaString("ShenFenVal");

		//按照当前节点的身份计算
		if (sfModel == 0)
			return ByDeptShipLeader_Nodes( String.valueOf(currWn.getHisNode().getNodeID()));

		//按照指定节点的身份计算.
		if (sfModel == 1)
			return ByDeptShipLeader_Nodes(sfVal);

		//按照 字段的值的人员编号作为身份计算.
		if (sfModel == 2)
		{
			//获得字段的值.
			String empNo = "";
			if (currWn.getHisNode().getHisFormType() == NodeFormType.RefOneFrmTree)
				empNo = currWn.getHisWork().GetValStrByKey(sfVal);
			else
				empNo = currWn.rptGe.GetValStrByKey(sfVal);
			bp.port.Emp emp = new bp.port.Emp();
			emp.setUserID(empNo);
			if (emp.RetrieveFromDBSources() == 0)
			{
				throw new Exception("err@根据字段值:" + sfVal + "在Port_Emp中没有找到人员信息");
			}
			return ByDeptShipLeader_Fields(emp.getNo(), emp.getDeptNo());
		}

		throw new Exception("err@没有判断的身份模式.");
	}
	private DataTable ByDeptLeader_Nodes(String nodes) throws Exception {
		DataTable dt = null;
		//查找指定节点的人员， 如果没有节点，就是当前的节点.
		if (DataType.IsNullOrEmpty(nodes) == true)
			nodes = String.valueOf(this.currWn.getHisNode().getNodeID());

		Paras ps = new Paras();
		ps.SQL = "SELECT FK_Emp,FK_Dept FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node Order By RDT DESC";
		ps.Add("OID", this.WorkID);
		ps.Add("FK_Node",  Integer.parseInt(nodes));

		dt = DBAccess.RunSQLReturnTable(ps);
		if (dt.Rows.size() == 0)
			throw new Exception("err@不符合常理，没有找到数据");
		String empNo = dt.Rows.get(0).getValue(0).toString();
		String deptNo = dt.Rows.get(0).getValue(1).toString();
		return ByDeptLeader_Fields(empNo, deptNo);
	}
	private DataTable ByDeptShipLeader_Nodes(String nodes) throws Exception {
		DataTable dt = null;
		//查找指定节点的人员， 如果没有节点，就是当前的节点.
		if (DataType.IsNullOrEmpty(nodes) == true)
			nodes = String.valueOf(this.currWn.getHisNode().getNodeID());

		Paras ps = new Paras();
		ps.SQL = "SELECT FK_Emp,FK_Dept FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node Order By RDT DESC";
		ps.Add("OID", this.WorkID);
		ps.Add("FK_Node", Integer.parseInt(nodes));

		dt = DBAccess.RunSQLReturnTable(ps);
		if (dt.Rows.size() == 0)
			throw new Exception("err@不符合常理，没有找到数据");
		String empNo = dt.Rows.get(0).getValue(0).toString();
		String deptNo = dt.Rows.get(0).getValue(1).toString();
		return ByDeptShipLeader_Fields(empNo, deptNo);
	}
	private DataTable ByDeptLeader_Fields(String empNo, String empDept) throws Exception {
		String sql = "SELECT Leader FROM Port_Dept WHERE No='" + empDept + "'";
		String myEmpNo = DBAccess.RunSQLReturnStringIsNull(sql, null);

		if (DataType.IsNullOrEmpty(myEmpNo) == true)
		{
			//如果部门的负责人为空，则查找Port_Emp中的Learder信息
			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
				sql = "SELECT Leader FROM Port_Emp WHERE UserID='" + empNo + "' AND OrgNo='" + WebUser.getOrgNo() + "'";
			else
				sql = "SELECT Leader FROM Port_Emp WHERE No='" + empNo + "'";

			myEmpNo = DBAccess.RunSQLReturnStringIsNull(sql, null);
			if (DataType.IsNullOrEmpty(myEmpNo) == true)
			{
				Dept mydept = new Dept(empDept);
				throw new Exception("@流程设计错误:下一个节点(" + town.getHisNode().getName() +")设置的按照部门负责人计算，当前您的部门(" + mydept.getNo() + "," + mydept.getName() +")没有维护负责人 . ");
			}
		}

		//如果有这个人,并且是当前人员，说明他本身就是经理或者部门负责人.
		if (myEmpNo.equals(empNo) == true)
		{
			sql = "SELECT Leader FROM Port_Dept WHERE No=(SELECT PARENTNO FROM PORT_DEPT WHERE NO='" + empDept + "')";
			myEmpNo = DBAccess.RunSQLReturnStringIsNull(sql, null);
			if (DataType.IsNullOrEmpty(myEmpNo) == true)
			{
				Dept mydept = new Dept(empDept);
				throw new Exception("@流程设计错误:下一个节点(" + town.getHisNode().getName() +")设置的按照部门负责人计算，当前您的部门(" + mydept.getName() +")上级没有维护负责人 . ");
			}
		}
		return DBAccess.RunSQLReturnTable(sql);
	}
	private DataTable ByDeptShipLeader_Fields(String empNo, String empDept) throws Exception {
		bp.port.Dept mydept = new bp.port.Dept(empDept);
		Paras ps = new Paras();
		ps.Add("No", empDept);
		ps.SQL = "SELECT ShipLeader FROM Port_Dept WHERE No='" + empDept + "'";

		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		if (dt.Rows.size() != 0 && dt.Rows.get(0).getValue(0) != null && DataType.IsNullOrEmpty(dt.Rows.get(0).getValue(0).toString()) == true)
		{
			//如果部门的负责人为空，则查找Port_Emp中的Learder信息
			ps.clear();
			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
				ps.SQL = "SELECT ShipLeader FROM Port_Emp WHERE UserID='" + empNo + "' AND OrgNo='" + WebUser.getOrgNo() + "'";
			else
				ps.SQL = "SELECT ShipLeader FROM Port_Emp WHERE No='" + empNo + "'";

			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() != 0 && dt.Rows.get(0).getValue(0) != null && DataType.IsNullOrEmpty(dt.Rows.get(0).getValue(0).toString()) == true)
				throw new Exception("@流程设计错误:下一个节点(" + town.getHisNode().getName() +")设置的按照部门负责人计算，当前您的部门(" + mydept.getNo() + "," + mydept.getName() +")没有维护负责人 . ");
		}

		//如果有这个人,并且是当前人员，说明他本身就是经理或者部门负责人.
		if (dt.Rows.get(0).getValue(0).toString().equals(empNo) == true)
		{
			ps.SQL = "SELECT ShipLeader FROM Port_Dept WHERE No=(SELECT PARENTNO FROM PORT_DEPT WHERE NO='" + empDept + "')";
			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() == 0)
				throw new Exception("@流程设计错误:下一个节点(" + town.getHisNode().getName() +")设置的按照部门负责人计算，当前您的部门(" + mydept.getName() +")上级没有维护负责人 . ");
		}
		return dt;
	}
	/**
	 获得指定部门下是否有该角色的人员.

	 @param deptNo 部门编号
	 @param empNo 人员编号
	 @return
	 */
	public DataTable Func_GenerWorkerList_SpecDept(String deptNo, String empNo) throws Exception {
		String sql;

		Paras ps = new Paras();
		if (this.town.getHisNode().getItIsExpSender() == true) {
			/* 不允许包含当前处理人. */
			sql = "SELECT FK_Emp as No FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node AND A.FK_Dept=" + dbStr + "FK_Dept AND A.FK_Emp!=" + dbStr + "FK_Emp";

			ps.SQL = sql;
			ps.Add("FK_Node", town.getHisNode().getNodeID());
			ps.Add("FK_Dept", deptNo);
			ps.Add("FK_Emp", empNo);
		} else {
			sql = "SELECT FK_Emp as No FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node AND A.FK_Dept=" + dbStr + "FK_Dept";

			ps.SQL = sql;
			ps.Add("FK_Node", town.getHisNode().getNodeID());
			ps.Add("FK_Dept", deptNo);
		}

		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		return dt;
	}
	/**
	 获得本部门的人员

	 @param deptNo
	 @param empNo
	 @return
	 */
	public DataTable Func_GenerWorkerList_SpecDept_SameLevel(String deptNo, String empNo) throws Exception {
		String sql;
		Paras ps = new Paras();
		if (this.town.getHisNode().getItIsExpSender() == true) {
			/* 不允许包含当前处理人. */
			sql = "SELECT FK_Emp as No FROM Port_DeptEmpStation A, WF_NodeStation B, Port_Dept C WHERE A.FK_Dept=C.No AND A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node AND C.ParentNo=" + dbStr + "FK_Dept AND A.FK_Emp!=" + dbStr + "FK_Emp";

			ps.SQL = sql;
			ps.Add("FK_Node", town.getHisNode().getNodeID());
			ps.Add("FK_Dept", deptNo);
			ps.Add("FK_Emp", empNo);
		} else {
			sql = "SELECT FK_Emp as No FROM Port_DeptEmpStation A, WF_NodeStation B, Port_Dept C  WHERE A.FK_Dept=C.No AND A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node AND C.ParentNo=" + dbStr + "FK_Dept";
			ps.SQL = sql;
			ps.Add("FK_Node", town.getHisNode().getNodeID());
			ps.Add("FK_Dept", deptNo);
		}

		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		return dt;
	}
	public final DataTable DoIt(Flow fl, WorkNode currWn, WorkNode toWn) throws Exception {
		return DoIt(fl, currWn, toWn,false);
	}

	public final DataTable DoIt(Flow fl, WorkNode currWn, WorkNode toWn,boolean isFromFullSA) throws Exception {
		// 给变量赋值.
		this.fl = fl;
		this.currWn = currWn;
		this.town = toWn;
		this.WorkID = currWn.getWorkID();

		if (this.town.getHisNode().getItIsGuestNode())
		{
			/*到达的节点是客户参与的节点. add by zhoupeng 2016.5.11*/
			DataTable mydt = new DataTable();
			mydt.Columns.Add("No", String.class);
			mydt.Columns.Add("Name", String.class);

			DataRow dr = mydt.NewRow();
			dr.setValue("No", "Guest");
			dr.setValue("Name", "外部用户");
			mydt.Rows.add(dr);
			return mydt;
		}

			//如果到达的节点是按照workflow的模式。
		if (toWn.getHisNode().getHisDeliveryWay() != DeliveryWay.ByCCFlowBPM)
		{
			DataTable re_dt = null;//this.FindByWorkFlowModel();
			boolean isOpenSelecter = toWn.getHisNode().GetParaBoolen("IsOpenSelecter");
			try
			{
				re_dt = this.FindByWorkFlowModel();
			}
			catch(Exception ex)
			{
				String msg = ex.getMessage();
				String backMsg = msg.replace("@没有找到可接受的工作人员@","@");
				if (isFromFullSA)
				{
					if (msg.contains("@没有找到可接受的工作人员") == true && (town.getHisNode().getHisWhenNoWorker() == true))
					{
						re_dt = new DataTable();
						return re_dt;
					}
					throw new Exception(msg);
				}


				if (msg.contains("@没有找到可接受的工作人员") == true)
				{
					if(isOpenSelecter == true)
						throw new Exception("url@./WorkOpt/AccepterOfGener.htm?FK_Flow=" + toWn.getHisNode().getFlowNo() + "&FK_Node=" + this.currWn.getHisNode().getNodeID() + "&ToNode=" + toWn.getHisNode().getNodeID() + "&WorkID=" + this.WorkID + "&PageName=AccepterOfGener");
					if(town.getHisNode().getHisWhenNoWorker() == true)
					{
						re_dt = new DataTable();
						return re_dt;
					}
				}
				throw new Exception(backMsg);
			}
			if (re_dt.Rows.size() == 1)
			{
				return re_dt; //如果只有一个人，就直接返回，就不处理了。
			}

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
			///#region 根据配置追加接收人 by dgq 2015.5.18

			String paras = this.town.getHisNode().getDeliveryParas();
			if (paras.contains("@Spec"))
			{
				//如果返回null ,则创建表
				if (re_dt == null)
				{
					re_dt = new DataTable();
					re_dt.Columns.Add("No", String.class);
				}

				//获取配置规则
				String[] reWays = this.town.getHisNode().getDeliveryParas().split("[@]", -1);
				for (String reWay : reWays)
				{
					if (DataType.IsNullOrEmpty(reWay))
					{
						continue;
					}
					String[] specItems = reWay.split("[=]", -1);
					//配置规则错误
					if (specItems.length != 2)
					{
						continue;
					}
					//规则名称，SpecStations、SpecEmps
					String specName = specItems[0];
					//规则内容
					String specContent = specItems[1];
					switch (specName)
					{
						case "SpecStations": //按角色
							String[] stations = specContent.split("[,]", -1);
							for (String station : stations)
							{
								if (DataType.IsNullOrEmpty(station))
								{
									continue;
								}

								//获取角色下的人员
								String sql = "";
								if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
								{
									sql = "SELECT FK_Emp FROM Port_DeptEmpStation WHERE FK_Station='" + station + "'";
								}
								else
								{
									sql = "SELECT FK_Emp FROM Port_DeptEmpStation WHERE FK_Station='" + station + "' AND OrgNo='" + WebUser.getOrgNo() + "'";
								}

								DataTable dt_Emps = DBAccess.RunSQLReturnTable(sql);
								for (DataRow empRow : dt_Emps.Rows)
								{
									//排除为空编号
									if (empRow.getValue(0) == null || DataType.IsNullOrEmpty(empRow.getValue(0).toString()))
									{
										continue;
									}

									DataRow dr = re_dt.NewRow();
									dr.setValue(String.valueOf(0), empRow.getValue(0));
									re_dt.Rows.add(dr);
								}
							}
							break;
						case "SpecEmps": //按人员编号
							String[] myEmpStrs = specContent.split("[,]", -1);
							for (String emp : myEmpStrs)
							{
								//排除为空编号
								if (DataType.IsNullOrEmpty(emp))
									continue;

								DataRow dr = re_dt.NewRow();
								dr.setValue(String.valueOf(0), emp);
								re_dt.Rows.add(dr);
							}
							break;
					}
				}
			}
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
			///#endregion

			//本节点接收人不允许包含上一步发送人 。
			if (this.town.getHisNode().getItIsExpSender() == true && re_dt.Rows.size() >= 2)
			{
				/*
				 * 排除了接受人分组的情况, 因为如果有了分组，就破坏了分组的结构了.
				 *
				 */
				//复制表结构
				DataTable dt = re_dt.clone();
				for (DataRow row : re_dt.Rows)
				{
					//排除当前登录人
					if (Objects.equals(row.getValue(0).toString(), WebUser.getNo()))
						continue;

					DataRow dr = dt.NewRow();
					dr.setValue(0, row.getValue(0));

					if (row.getTable().Columns.size() == 2)
						dr.setValue(1, row.getValue(1));

					dt.Rows.add(dr);
				}
				return dt;
			}
			return re_dt;
		}

		//没有找到人的情况，就返回空.
		return null;
	}



}
