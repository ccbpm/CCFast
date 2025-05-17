package bp.wf;

import bp.en.*;
import bp.da.*;
import bp.port.*;
import bp.difference.*;
import bp.sys.*;
import bp.tools.DateUtils;
import bp.web.WebUser;
import bp.wf.template.*;
import bp.wf.data.*;
import bp.wf.template.sflow.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.math.*;
import java.util.stream.Collectors;

/**
 WF 的摘要说明.
 工作流.
 这里包含了两个方面
 工作的信息．
 流程的信息.
 */
public class WorkNode
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
	private GuestUserCopy _guestUserCopy = null;
	public final GuestUserCopy getGuestUser()
	{
		if (_guestUserCopy == null)
		{
			_guestUserCopy = new GuestUserCopy();
			_guestUserCopy.LoadWebUser();
		}
		return _guestUserCopy;
	}

	private WFState _WFState = WFState.Runing;
	public final void setWFState() throws Exception {
		_WFState = this.getHisGenerWorkFlow().getWFState();
	}
	public final void setGuestUser(GuestUserCopy value)
	{
		_guestUserCopy = value;
	}


	///#endregion 身份.



	///#region 权限判断
	/**
	 判断一个人能不能对这个工作节点进行操作。

	 @param empId
	 @return
	 */
	private boolean IsCanOpenCurrentWorkNode(String empId) throws Exception {
		WFState stat = this.getHisGenerWorkFlow().getWFState();
		if (stat == WFState.Runing)
		{
			if (this.getHisNode().getItIsStartNode())
			{
				/*如果是开始工作节点，从工作角色判断他有没有工作的权限。*/
				return WorkFlow.IsCanDoWorkCheckByEmpStation(this.getHisNode().getNodeID(), empId);
			}
			else
			{
				/* 如果是初始化阶段,判断他的初始化节点 */
				GenerWorkerList wl = new GenerWorkerList();
				wl.setWorkID(this.getHisWork().getOID());
				wl.setEmpNo(empId);

				Emp myEmp = new Emp(empId);
				wl.setEmpName(myEmp.getName());

				wl.setNodeID(this.getHisNode().getNodeID());
				wl.setNodeName(this.getHisNode().getName());
				return wl.getIsExits();
			}
		}
		else
		{
			/* 如果是初始化阶段 */
			return false;
		}
	}

	///#endregion


	///#region 属性/变量.
	/**
	 子线程是否有分组标志.
	 */
	public boolean ItIsHaveSubThreadGroupMark = false;

	/**
	 执行人
	 */
	private String _execer11 = null;
	/**
	 实际执行人，执行工作发送时，有时候当前 WebUser.No 并非实际的执行人。
	 */
	public final String getExecer() throws Exception {
		if (_execer11 == null || Objects.equals(_execer11, ""))
		{
			if (getWebUser().IsAuthorize == true)
			{
				_execer11 = getWebUser().Auth;
			}
			else
			{
				_execer11 = getWebUser().No;
			}
		}
		return _execer11;
	}
	public final void setExecer(String value)
	{
		_execer11 = value;
	}
	private String _execerName = null;
	/**
	 实际执行人名称(请参考实际执行人)
	 */
	public final String getExecerName() throws Exception {
		if (_execerName == null || Objects.equals(_execerName, ""))
		{
			if (getWebUser().IsAuthorize == true)
			{
				_execerName = getWebUser().AuthName;
			}
			else
			{
				_execerName = getWebUser().getName();
			}
		}
		return _execerName;
	}
	public final void setExecerName(String value)
	{
		_execerName = value;
	}
	private String _execerDeptName = null;
	/**
	 实际执行人名称(请参考实际执行人)
	 */
	public final String getExecerDeptName() throws Exception {
		if (_execerDeptName == null)
		{
			_execerDeptName = getWebUser().DeptName;
		}
		return _execerDeptName;
	}
	public final void setExecerDeptName(String value)
	{
		_execerDeptName = value;
	}
	private String _execerDeptNo = null;
	/**
	 实际执行人名称(请参考实际执行人)
	 */
	public final String getExecerDeptNo() throws Exception {
		if (_execerDeptNo == null)
		{
			_execerDeptNo = getWebUser().DeptNo;
		}
		return _execerDeptNo;
	}
	public final void setExecerDeptNo(String value)
	{
		_execerDeptNo = value;
	}
	/**
	 虚拟目录的路径
	 */
	private String _VirPath = null;
	/**
	 虚拟目录的路径
	 */
	public final String getVirPath()
	{
		if (_VirPath == null && SystemConfig.isBSsystem())
		{
			_VirPath = Glo.getCCFlowAppPath(); //BP.Sys.Base.Glo.Request.ApplicationPath;
		}
		return _VirPath;
	}
	private String _AppType = null;
	/**
	 虚拟目录的路径
	 */
	public final String getAppType()
	{
		if (SystemConfig.isBSsystem() == false)
		{
			return "CCFlow";
		}

		if (_AppType == null && SystemConfig.isBSsystem())
		{
			_AppType = "WF";
		}
		return _AppType;
	}
	private String nextStationName = "";
	public WorkNode town = null;
	private boolean IsFindWorker = false;
	public final boolean getItIsSubFlowWorkNode() throws Exception {
		if (this.getHisWork().getFID() == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	///#endregion 属性/变量.


	///#region GenerWorkerList 相关方法.
	//查询出每个节点表里的接收人集合（Emps）。
	public final String GenerEmps(Node nd) throws Exception {
		String str = "";
		for (GenerWorkerList wl : this.HisWorkerLists.ToJavaList())
		{
			str = wl.getEmpNo() + ",";
		}
		return str;
	}
	/**
	 产生它的工作者

	 @param town WorkNode
	 @return 产生的工作人员
	 */
	public final GenerWorkerLists Func_GenerWorkerLists(WorkNode town) throws Exception {
		this.town = town;
		DataTable dt = new DataTable();
		dt.Columns.Add("No", String.class);
		String FK_Emp;

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

			/*如果是抢办或者共享.*/

			// 如果执行了两次发送，那前一次的轨迹就需要被删除,这里是为了避免错误。
			ps = new Paras();
			ps.Add("WorkID", this.getHisWork().getOID());
			ps.Add("FK_Node", town.getHisNode().getNodeID());
			ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "WorkID AND FK_Node =" + dbStr + "FK_Node";
			DBAccess.RunSQL(ps);

			return InitWorkerLists(town, dt);
		}

		// 如果执行了两次发送，那前一次的轨迹就需要被删除,这里是为了避免错误,
		ps = new Paras();
		ps.Add("WorkID", this.getHisWork().getOID());
		ps.Add("FK_Node", town.getHisNode().getNodeID());
		ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "WorkID AND FK_Node =" + dbStr + "FK_Node";
		DBAccess.RunSQL(ps);

		//开始找人.
		Node toNode = town.getHisNode();
		if ((this.getTodolistModel() == TodolistModel.Teamup || this.getTodolistModel() == TodolistModel.TeamupGroupLeader) && (toNode.getHisDeliveryWay() == DeliveryWay.ByStation || toNode.getHisDeliveryWay() == DeliveryWay.BySenderParentDeptLeader || toNode.getHisDeliveryWay() == DeliveryWay.BySenderParentDeptStations))
		{
			FindWorker fw = new FindWorker();
			fw.currWn = this;
			return Teamup_InitWorkerLists(fw, town);
		}
		//如果是自动计算未来接受人.
		if (this.getHisFlow().getItIsFullSA() == true && this.getHisNode().getItIsStartNode() == false)
		{
			String sql = "SELECT FK_Emp FROM WF_SelectAccper WHERE FK_Node=" + town.getHisNode().getNodeID() + "  AND WorkID=" + this.getWorkID();
			dt = DBAccess.RunSQLReturnTable(sql);
		}
		if(dt.Rows.size()==0){
			FindWorker fw = new FindWorker();
			fw.currWn = this;
			dt = fw.DoIt(this.getHisFlow(), this, town);
			if (dt == null)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@没有找到接收人.", "WorkNode", "not_found_receiver"));
			}
		}
		return InitWorkerLists(town, dt);
	}

	/**
	 子线程获取下一个节点的处理人

	 @param town
	 @return
	 @exception Exception
	 */
	public final GenerWorkerLists Func_GenerWorkerLists_Thread(WorkNode town) throws Exception {
		this.town = town;
		DataTable dt = new DataTable();
		dt.Columns.Add("No", String.class);
		String sql;
		String FK_Emp;

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

			/*如果是抢办或者共享.*/

			// 如果执行了两次发送，那前一次的轨迹就需要被删除,这里是为了避免错误。
			ps = new Paras();
			ps.Add("WorkID", this.getHisWork().getOID());
			ps.Add("FK_Node", town.getHisNode().getNodeID());
			ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "WorkID AND FK_Node =" + dbStr + "FK_Node";
			DBAccess.RunSQL(ps);

			return InitWorkerLists(town, dt);
		}

		// 如果执行了两次发送，那前一次的轨迹就需要被删除,这里是为了避免错误,
		ps = new Paras();
		ps.Add("WorkID", this.getHisWork().getOID());
		ps.Add("FK_Node", town.getHisNode().getNodeID());
		ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "WorkID AND FK_Node =" + dbStr + "FK_Node";
		DBAccess.RunSQL(ps);

		/*如果设置了安ccbpm的BPM模式*/
		FindWorker fw = new FindWorker();
		fw.currWn = this;
		//如果是协作模式且下一个节点的接收人和身份相关的处理
		Node toNode = town.getHisNode();
		if ((this.getTodolistModel() == TodolistModel.Teamup || this.getTodolistModel() == TodolistModel.TeamupGroupLeader) && (toNode.getHisDeliveryWay() == DeliveryWay.ByStation || toNode.getHisDeliveryWay() == DeliveryWay.BySenderParentDeptLeader || toNode.getHisDeliveryWay() == DeliveryWay.BySenderParentDeptStations))
		{
			return Teamup_InitWorkerLists(fw, town);
		}

		dt = fw.DoIt(this.getHisFlow(), this, town);
		if (dt == null || dt.Rows.size() == 0)
		{
			return null;
		}

		return InitWorkerLists(town, dt);
	}

	/**
	 协作模式下处理下一个节点的接收人

	 @param fw
	 @param town
	 @return
	 @exception Exception
	 */
	private GenerWorkerLists Teamup_InitWorkerLists(FindWorker fw, WorkNode town) throws Exception {
		String currEmpNo = getWebUser().No;
		String currDeptNo = getWebUser().DeptNo;
		try
		{
			//下一个节点的接收人集合
			DataTable empDt = new DataTable();
			empDt.Columns.Add("No");

			//获取处理当前节点业务所有人所在部门集合
			String sql = "SELECT FK_Emp,FK_Dept From WF_GenerWorkerlist Where WorkID=" + this.getWorkID() + " AND FK_Node=" + this.getHisNode().getNodeID() + " AND (IsPass=1 OR IsPass=0)";
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
			{
				throw new RuntimeException("err@获取处理当前节点业务所有人所在部门失败");
			}
			if (dt.Rows.size() == 1)
			{
				dt = fw.DoIt(this.getHisFlow(), this, town);
				if (dt == null)
				{
					throw new RuntimeException(bp.wf.Glo.multilingual("@没有找到接收人.", "WorkNode", "not_found_receiver"));
				}
				return InitWorkerLists(town, dt);
			}
			String deptNos = ",";
			String deptNo = "";
			String empNo = "";
			for (DataRow dr : dt.Rows)
			{
				empNo = dr.getValue(0).toString();
				deptNo = dr.getValue(1).toString();
				if (deptNos.contains("," + deptNo + ",") == true)
				{
					continue;
				}
				if (empNo.equals(getWebUser().getNo()) == true)
				{
					DataTable ddt = fw.DoIt(this.getHisFlow(), this, town);
					if (ddt != null && ddt.Rows.size() > 0)
					{
						for(DataRow drt : ddt.Rows){
							DataRow drdt = empDt.NewRow();
							drdt.setValue("No", drt.getValue(0));
							empDt.Rows.add(drdt);
						}
					}
				}
				else
				{
					getWebUser().setNo(empNo);
					getWebUser().setDeptNo(deptNo);
					DataTable ddt = fw.DoIt(this.getHisFlow(), this, town);
					if (ddt != null && ddt.Rows.size() > 0)
					{
						for(DataRow drt : ddt.Rows){
							DataRow drdt = empDt.NewRow();
							drdt.setValue("No", drt.getValue(0));
							empDt.Rows.add(drdt);
						}
					}
				}
			}
			if (empDt.Rows.size() == 0)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@没有找到接收人.", "WorkNode", "not_found_receiver"));
			}
			return InitWorkerLists(town, empDt);
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException(ex.getMessage());
		}
		finally
		{
			getWebUser().setNo(currEmpNo);
			getWebUser().setDeptNo(currDeptNo);

		}
	}

	///#endregion GenerWorkerList 相关方法.

	private String dbStr = SystemConfig.getAppCenterDBVarStr();
	public Paras ps = new Paras();
	/**
	 递归删除两个节点之间的数据

	 @param nds 到达的节点集合
	 */
	public final void DeleteToNodesData(Nodes nds)
	{
		return;
	}


	///#region 根据工作角色生成工作者
	private Node _ndFrom = null;
	private Node getNdFrom()
	{
		if (_ndFrom == null)
		{
			_ndFrom = this.getHisNode();
		}
		return _ndFrom;
	}
	private void setNdFrom(Node value)
	{
		_ndFrom = value;
	}
	/**
	 初始化工作人员

	 @param town 到达的wn
	 @param dt 数据源
	 @return GenerWorkerLists
	 */

	private GenerWorkerLists InitWorkerLists(WorkNode town, DataTable dt) throws Exception {
		return InitWorkerLists(town, dt, 0);
	}

	private GenerWorkerLists InitWorkerLists(WorkNode town, DataTable dt, long fid) throws Exception {
		if (dt.Rows.size() == 0)
		{
			throw new RuntimeException(bp.wf.Glo.multilingual("@没有找到接收人,InitWorkerLists.", "WorkNode", "not_found_receiver")); // 接收人员列表为空
		}

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			String orgNo = bp.web.WebUser.getOrgNo();
			for (DataRow dr : dt.Rows)
			{
				String str = dr.getValue(0) instanceof String ? (String)dr.getValue(0) : null;
				if (str == null)
				{
					continue;
				}
				str = str.replace(orgNo + "_", "");
				dr.setValue(0, str);
			}
		}

		this.getHisGenerWorkFlow().setTodoEmpsNum(-1);


		///#region 判断发送的类型，处理相关的FID.
		// 定义下一个节点的接收人的 FID 与 WorkID.
		long nextUsersWorkID = this.getWorkID();
		long nextUsersFID = this.getHisWork().getFID();

		// 是否是分流到子线程。
		boolean isFenLiuToSubThread = false;
		if (this.getHisNode().getItIsFLHL() == true && town.getHisNode().getItIsSubThread() == true)
		{
			isFenLiuToSubThread = true;
			nextUsersWorkID = 0;
			nextUsersFID = this.getHisWork().getOID();
		}


		// 子线程 到 合流点or 分合流点.
		boolean isSubThreadToFenLiu = false;
		if (this.getHisNode().getItIsSubThread() == true && town.getHisNode().getItIsFLHL() == true)
		{
			nextUsersWorkID = this.getHisWork().getFID();
			nextUsersFID = 0;
			isSubThreadToFenLiu = true;
		}

		// 子线程到子线程.
		boolean isSubthread2Subthread = false;
		if (this.getHisNode().getItIsSubThread() == true && town.getHisNode().getItIsSubThread() == true)
		{
			nextUsersWorkID = this.getHisWork().getOID();
			nextUsersFID = this.getHisWork().getFID();
			isSubthread2Subthread = true;
		}

		///#endregion 判断发送的类型，处理相关的FID.

		int toNodeId = town.getHisNode().getNodeID();
		this.HisWorkerLists = new GenerWorkerLists();
		this.HisWorkerLists.clear();


		///#region 限期时间  town.getHisNode().TSpan-1

		Date dtOfShould = new Date();

		if (town.getHisNode().getHisCHWay() == CHWay.ByTime)
		{
			CHNode chNode = new CHNode();
			chNode.setMyPK(this.getHisGenerWorkFlow().getWorkID() + "_" + this.town.getHisNode().getNodeID());
			if (chNode.RetrieveFromDBSources() != 0)
			{
				dtOfShould = DateUtils.parse(chNode.getEndDT());
			}
			else
			{
				//按天、小时考核
				if (town.getHisNode().GetParaInt("CHWayOfTimeRole", 0) == 0)
				{
					//增加天数. 考虑到了节假日.
					//判断是修改了节点期限的天数
					int timeLimit = this.town.getHisNode().getTimeLimit();
					dtOfShould = Glo.AddDayHoursSpan(new Date(), timeLimit, this.town.getHisNode().getTimeLimitHH(), this.town.getHisNode().getTimeLimitMM(), this.town.getHisNode().getTWay());
				}
				//按照节点字段设置
				if (town.getHisNode().GetParaInt("CHWayOfTimeRole", 0) == 1)
				{
					//获取设置的字段、
					String keyOfEn = town.getHisNode().GetParaString("CHWayOfTimeRoleField");
					if (DataType.IsNullOrEmpty(keyOfEn) == true)
					{
						town.getHisNode().setHisCHWay(CHWay.None);
					}
					else
					{
						dtOfShould = DataType.ParseSysDateTime2DateTime(this.getHisWork().GetValByKey(keyOfEn).toString());
					}

				}
			}

			//流转自定义的流程并且考核规则按照流转自定义设置
			//if (this.HisGenerWorkFlow.TransferCustomType == TransferCustomType.ByWorkerSet
			//    && town.getHisNode().GetParaInt("CHWayOfTimeRole") == 2)
			//{
			//    //获取当前节点的流转自定义时间
			//    TransferCustom tf = new TransferCustom();
			//    tf.setMyPK(town.Hisnode.getNodeID() + "_" + this.getWorkID();
			//    if (tf.RetrieveFromDBSources() != 0)
			//    {
			//        if (DataType.IsNullOrEmpty(tf.PlanDT) == true)
			//            throw new Exception("err@在流转自定义期间，没有设置计划完成日期。");
			//        dtOfShould = DataType.ParseSysDateTime2DateTime(tf.PlanDT);
			//    }
			//}
		}

		//求警告日期.
		Date dtOfWarning = new Date();
		if (this.town.getHisNode().getWarningDay() == 0)
		{
			dtOfWarning = dtOfShould;
		}
		else
		{
			//计算警告日期.
			//增加小时数. 考虑到了节假日.
			dtOfWarning = Glo.AddDayHoursSpan(new Date(), (int)this.town.getHisNode().getWarningDay(), 0, 0, this.town.getHisNode().getTWay());
		}

		switch (this.getHisNode().getHisNodeWorkType())
		{
			case StartWorkFL:
			case WorkFHL:
			case WorkFL:
			case WorkHL:
				break;
			default:
				this.getHisGenerWorkFlow().setNodeID(town.getHisNode().getNodeID());
				this.getHisGenerWorkFlow().setSDTOfNode(DataType.SysDateTimeFormat(dtOfShould));
				//暂时注释掉，忘记使用情况
				//this.HisGenerWorkFlow.SetPara("CH" + this.town.Hisnode.getNodeID(), this.HisGenerWorkFlow.SDTOfNode);
				//this.HisGenerWorkFlow.SDTOfFlow = dtOfFlow.ToString(DataType.getSysDateTimeFormat());
				//this.HisGenerWorkFlow.SDTOfFlowWarning = dtOfFlowWarning.ToString(DataType.getSysDateTimeFormat());
				this.getHisGenerWorkFlow().setTodoEmpsNum(dt.Rows.size());
				break;
		}

		///#endregion 限期时间  town.getHisNode().TSpan-1


		///#region 处理 人员列表 数据源。
		// 定义是否有分组mark. 如果有三列，就说明该集合中有分组 mark. 就是要处理一个人多个子线程的情况.

		this.ItIsHaveSubThreadGroupMark = true;

		if (dt.Columns.size()<= 2)
		{
			this.ItIsHaveSubThreadGroupMark = false;
		}

		if (dt.Rows.size() == 1)
		{
			/* 如果只有一个人员 */
			GenerWorkerList wl = new GenerWorkerList();
			if (isFenLiuToSubThread)
			{
				/*  说明这是分流点向下发送
				 *  在这里产生临时的workid.
				 */
				wl.setWorkID(DBAccess.GenerOIDByGUID());
			}
			else
			{
				wl.setWorkID(nextUsersWorkID);
			}
			wl.setFID(nextUsersFID);
			wl.setNodeID(toNodeId);
			wl.setNodeName(town.getHisNode().getName());
			wl.setEmpNo(dt.Rows.get(0).getValue(0).toString());
			Emp emp = new Emp();
			emp.setUserID(wl.getEmpNo());
			if (emp.RetrieveFromDBSources() == 0)
			{
				String[] para = new String[1];
				para[0] = wl.getEmpNo();
				String str = bp.wf.Glo.multilingual("@设置接收人规则错误, 接收人[" + wl.getEmpNo() + "][{0}]不存在或者被停用。", "WorkNode", "invalid_setting_receiver", para);
				throw new RuntimeException("err@" + str);
			}

			wl.setEmpName(emp.getName());
			if (dt.Rows.get(0).getTable().Columns.contains("FK_Dept"))
			{
				wl.setDeptNo(dt.Rows.get(0).getValue(1).toString());
				Dept dept = new Dept(wl.getDeptNo());
				wl.setDeptName(dept.getName());
				if (dept.RetrieveFromDBSources() == 0)
				{
					wl.setDeptNo(emp.getDeptNo());
					wl.setDeptName(emp.getDeptText());
				}
			}
			else
			{
				wl.setDeptNo(emp.getDeptNo());
				wl.setDeptName(emp.getDeptText());
			}
			wl.setWhoExeIt(town.getHisNode().getWhoExeIt()); //设置谁执行它.

			//应完成日期.
			if (town.getHisNode().getHisCHWay() == CHWay.None)
			{
				wl.setSDT("无");
			}
			else
			{
				wl.setSDT(DateUtils.format(dtOfShould,DataType.getSysDateTimeFormat() + ":ss"));
			}

			//警告日期.
			wl.setDTOfWarning(DateUtils.format(dtOfWarning,DataType.getSysDateTimeFormat()));

			wl.setFlowNo(town.getHisNode().getFlowNo());

			// and 2015-01-14 , 如果有三列，就约定为最后一列是分组标志， 有标志就把它放入标志里 .
			if (this.ItIsHaveSubThreadGroupMark == true)
			{
				wl.setGroupMark(dt.Rows.get(0).getValue(2).toString()); //第3个列是分组标记.
				if (DataType.IsNullOrEmpty(wl.getGroupMark()))
				{
					String[] para = new String[1];
					para[0] = wl.getEmpNo();
					bp.wf.Glo.multilingual("@[{0}]分组标记中没有值,会导致无法按照分组标记去生成子线程,请检查配置的信息是否正确.", "WorkNode", "no_value_in_group_tags", para);
				}
			}

			//设置发送人.
			if (Objects.equals(bp.web.WebUser.getNo(), "Guest"))
			{
				wl.setSender(getGuestUser().getNo() + "," + getGuestUser().getName());
				wl.setGuestNo(getGuestUser().getNo());
				wl.setGuestName(getGuestUser().getName());
			}
			else
			{
				wl.setSender(getWebUser().getNo() + "," + getWebUser().getName());
			}

			//判断下一个节点是否是外部用户处理人节点？
			if (town.getHisNode().getItIsGuestNode())
			{
				if (!Objects.equals(this.getHisGenerWorkFlow().getGuestNo(), ""))
				{
					wl.setGuestNo(this.getHisGenerWorkFlow().getGuestNo());
					wl.setGuestName(this.getHisGenerWorkFlow().getGuestName());
				}
				else
				{
					/*这种情况是，不是外部用户发起的流程。*/
					if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySQL)
					{
						Object tempVar = town.getHisNode().getDeliveryParas();
						String mysql = tempVar instanceof String ? (String)tempVar : null;
						DataTable mydt = DBAccess.RunSQLReturnTable(Glo.DealExp(mysql, this.rptGe));

						wl.setGuestNo(mydt.Rows.get(0).getValue(0).toString());
						wl.setGuestName(mydt.Rows.get(0).getValue(1).toString());

						this.getHisGenerWorkFlow().setGuestNo(wl.getGuestNo());
						this.getHisGenerWorkFlow().setGuestName(wl.getGuestName());
					}
					else if (town.getHisNode().getHisDeliveryWay() == DeliveryWay.ByPreviousNodeFormEmpsField)
					{
						wl.setGuestNo(this.getHisWork().GetValStrByKey(town.getHisNode().getDeliveryParas()));
						wl.setGuestName("外部用户");
						this.getHisGenerWorkFlow().setGuestNo(wl.getGuestNo());
						this.getHisGenerWorkFlow().setGuestName(wl.getGuestName());
					}
					else
					{
						String[] para = new String[1];
						para[0] = this.town.getHisNode().getName();
						bp.wf.Glo.multilingual("@当前节点[{0}]是中间节点，并且是外部用户处理节点，您需要正确的设置这个外部用户接收人规则。", "WorkNode", "invalid_setting_external_receiver", para);
					}
				}
			}

			wl.Insert();
			this.HisWorkerLists.AddEntity(wl);

			RememberMe rm = new RememberMe(); // this.GetHisRememberMe(town.HisNode);
			rm.setObjs("@" + wl.getEmpNo() + "@");
			rm.setObjsExt(rm.getObjsExt() + bp.wf.Glo.DealUserInfoShowModel(wl.getEmpNo(), wl.getEmpName()));
			rm.setEmps("@" + wl.getEmpNo() + "@");
			rm.setEmpsExt(bp.wf.Glo.DealUserInfoShowModel(wl.getEmpNo(), wl.getEmpName()));
			this.HisRememberMe = rm;
		}

		//为了缓解代码量，与处理效率问题。
		if (dt.Rows.size() > 1)
		{
			WorkNodePlus.InitWorkerList_Ext(this, town, dt, toNodeId, dtOfShould, dtOfWarning, nextUsersFID, isFenLiuToSubThread, nextUsersWorkID);
		}

		if (this.HisWorkerLists.size() == 0)
		{
			String[] para = new String[3];
			para[0] = this.town.getHisNode().getHisRunModel().toString();
			para[1] = this.town.getHisNode().getName();
			para[2] = this.getHisWorkFlow().getHisFlow().getName();
			bp.wf.Glo.multilingual("@根据部门[{0}]产生工作人员出现错误，流程[{1}]中节点[{2}]定义错误,没有找到接收此工作的工作人员.", "WorkNode", "generate_receiver_error_by_depart", para);
		}
		///#endregion 处理 人员列表 数据源。
		///#region 设置流程数量,其他的信息为任务池提供数据。
		String hisEmps = "";
		int num = 0;
		for (GenerWorkerList wl : this.HisWorkerLists.ToJavaList())
		{
			if (wl.getPassInt() == 0 && wl.getItIsEnable() == true)
			{
				num++;
				hisEmps += wl.getEmpNo() + "," + wl.getEmpName() + ";";
			}
		}

		if (num == 0)
		{
			throw new RuntimeException("@不应该产生的结果错误,没有找到接受人.");
		}

		this.getHisGenerWorkFlow().setTodoEmpsNum(num);
		this.getHisGenerWorkFlow().setTodoEmps(hisEmps);

		///#endregion


		///#region  求出日志类型，并加入变量中。
		ActionType at = ActionType.Forward;
		switch (town.getHisNode().getHisNodeWorkType())
		{
			case StartWork:
			case StartWorkFL:
				at = ActionType.Start;
				break;
			case Work:
				if (this.getHisNode().getHisNodeWorkType() == NodeWorkType.WorkFL || this.getHisNode().getHisNodeWorkType() == NodeWorkType.WorkFHL)
				{
					at = ActionType.ForwardFL;
				}
				else
				{
					at = ActionType.Forward;
				}
				break;
			case WorkHL:
				at = ActionType.ForwardHL;
				break;
			case SubThreadWork:

				switch (this.getHisNode().getHisNodeWorkType())
				{
					case StartWorkFL:
					case WorkFL:
					case WorkFHL:
						at = ActionType.ForwardFL;
						break;
					case WorkHL:
						throw new RuntimeException(bp.wf.Glo.multilingual("err@流程设计错误: 当前节点是合流节点，而到达的节点是子线程。", "WorkNode", "workflow_error_1", new String[0]));
						//break;
					case Work:
						throw new RuntimeException(bp.wf.Glo.multilingual("err@流程设计错误: 当前节点是合流节点，而到达的节点是子线程。", "WorkNode", "workflow_error_2", new String[0]));
						//break;
					default:
						at = ActionType.Forward;
						break;
				}
				break;
			default:
				break;
		}

		if(at == ActionType.Forward){
			try{
				//如果当前账户中含有会签主持人，状态改成加签
				GenerWorkerList gwl = new GenerWorkerList(this.getWorkID(),this.getHisNode().getNodeID(),WebUser.getNo());
				String huiQianZhuChiRen = gwl.GetParaString("HuiQianZhuChiRen","");
				if(DataType.IsNullOrEmpty(huiQianZhuChiRen)==false)
					at = ActionType.TeampUp;
			}catch(Exception ex){

			}
		}

		///#region 如果是子线城前进.
		if (at == ActionType.SubThreadForward)
		{
			String emps = "";
			for (GenerWorkerList wl : this.HisWorkerLists.ToJavaList())
			{
				this.AddToTrack(at, wl, bp.wf.Glo.multilingual("子线程", "WorkNode", "sub_thread", new String[0]), this.town.getHisWork().getOID(), this.getHisNode());
			}
			//写入到日志.
		}

		///#endregion 如果是子线城前进.


		///#region 如果是非子线城前进.
		if (at != ActionType.SubThreadForward)
		{
			if (this.HisWorkerLists.size() == 1)
			{
				GenerWorkerList wl = this.HisWorkerLists.get(0) instanceof GenerWorkerList ? (GenerWorkerList)this.HisWorkerLists.get(0) : null;
				this.AddToTrack(at, wl.getEmpNo(), wl.getEmpName(), wl.getNodeID(), wl.getNodeName(), null, this.getNdFrom(), null);
			}
			else
			{
				String[] para = new String[1];
				para[0] = String.valueOf(this.HisWorkerLists.size());
				String info = bp.wf.Glo.multilingual("共({0})人接收:", "WorkNode", "total_receivers", para);

				String empNos = "";
				String empNames = "";
				for (GenerWorkerList wl : this.HisWorkerLists.ToJavaList())
				{
					info += bp.wf.Glo.DealUserInfoShowModel(wl.getDeptName(), wl.getEmpName()) + ":";

					empNos += wl.getEmpNo() + ",";
					empNames += wl.getEmpName() + ",";
				}

				//写入到日志.
				this.AddToTrack(at, empNos, empNames, town.getHisNode().getNodeID(), town.getHisNode().getName(), bp.wf.Glo.multilingual("多人接收(见信息栏)", "WorkNode", "multiple_receivers", new String[0]), this.getNdFrom(), info);
			}
		}

		///#endregion 如果是非子线城前进.


		///#region 把数据加入变量中.
		String ids = "";
		String names = "";
		String idNames = "";
		if (this.HisWorkerLists.size() == 1)
		{
			GenerWorkerList gwl = (GenerWorkerList)this.HisWorkerLists.get(0);
			ids = gwl.getEmpNo();
			names = gwl.getEmpName();

			//设置状态。
			this.getHisGenerWorkFlow().setTaskSta(TaskSta.None);
		}
		else
		{
			for (GenerWorkerList gwl : this.HisWorkerLists.ToJavaList())
			{
				ids += gwl.getEmpNo() + ",";
				names += gwl.getEmpName() + ",";
			}

			//设置状态, 如果该流程使用了启用共享任务池。
			if (town.getHisNode().getItIsEnableTaskPool() && this.getHisNode().getHisRunModel() == RunModel.Ordinary)
			{
				this.getHisGenerWorkFlow().setTaskSta(TaskSta.Sharing);
			}
			else
			{
				this.getHisGenerWorkFlow().setTaskSta(TaskSta.None);
			}
		}

		this.addMsg(SendReturnMsgFlag.VarAcceptersID, ids, ids, SendReturnMsgType.SystemMsg);
		this.addMsg(SendReturnMsgFlag.VarAcceptersName, names, names, SendReturnMsgType.SystemMsg);
		this.addMsg(SendReturnMsgFlag.VarAcceptersNID, idNames, idNames, SendReturnMsgType.SystemMsg);

		///#endregion

		return this.HisWorkerLists;
	}

	///#endregion


	///#region 条件
	private Conds _HisNodeCompleteConditions = null;
	/**
	 节点完成任务的条件
	 条件与条件之间是or 的关系, 就是说,如果任何一个条件满足,这个工作人员在这个节点上的任务就完成了.
	 */
	public final Conds getHisNodeCompleteConditions() throws Exception {
		if (this._HisNodeCompleteConditions == null)
		{
			_HisNodeCompleteConditions = new Conds(CondType.Node, this.getHisNode().getNodeID(), this.getWorkID(), this.rptGe);
			return _HisNodeCompleteConditions;
		}
		return _HisNodeCompleteConditions;
	}

	///#endregion


	///#region 关于质量考核
	///// <summary>
	///// 得到以前的已经完成的工作节点.
	///// </summary>
	///// <returns></returns>
	//public WorkNodes GetHadCompleteWorkNodes()
	//{
	//    WorkNodes mywns = new WorkNodes();
	//    WorkNodes wns = new WorkNodes(this.getHisNode().HisFlow, this.HisWork.OID);
	//    foreach (WorkNode wn in wns)
	//    {
	//        if (wn.IsComplete)
	//            mywns.Add(wn);
	//    }
	//    return mywns;
	//}

	///#endregion


	///#region 流程公共方法
	private Flow _HisFlow = null;
	public final Flow getHisFlow() throws Exception {
		if (_HisFlow == null)
		{
			_HisFlow = this.getHisNode().getHisFlow();
		}
		return _HisFlow;
	}
	public Node JumpToNode = null;
	public String JumpToEmp = null;


	///#region NodeSend 的附属功能.


	public final Node NodeSend_GenerNextStepNode() throws Exception {
		Node node = NodeSend_GenerNextStepNodeExt();
		if (node.getHisBatchRole() == BatchRole.None)
		{
			this.getHisGenerWorkFlow().setItIsCanBatch(false);
		}
		else
		{
			this.getHisGenerWorkFlow().setItIsCanBatch(true);
		}

		return node;
	}
	/**
	 获得下一个节点.

	 @return
	 */

	/*private Node NodeSend_GenerNextStepNodeExt() throws Exception {
		return NodeSend_GenerNextStepNodeExt(false);
	}*/

	private Node NodeSend_GenerNextStepNodeExt() throws Exception {
		//如果要是跳转到的节点，自动跳转规则规则就会失效.
		if (this.JumpToNode != null)
		{
			return this.JumpToNode;
		}
		// 被zhoupeng注释，因为有可能遇到跳转.
		//if (this.getHisNode().HisToNodes.size()== 1)
		//    return (Node)this.getHisNode().HisToNodes[0];

		// 判断是否有用户选择的节点.
		if (this.getHisNode().getCondModel() == DirCondModel.ByPopSelect)
		{
			// 获取用户选择的节点.
			String nodes = this.getHisGenerWorkFlow().getParasToNodes();
			if (DataType.IsNullOrEmpty(nodes) || nodes.equals(",") == true)
			{
				if (this.getHisNode().getHisToNodes().size()== 1)
				{
					Node mynd= this.getHisNode().getHisToNodes().get(0) instanceof Node ? (Node) this.getHisNode().getHisToNodes().get(0) : null;

					if (mynd.getAutoJumpRole1() == true
							|| mynd.getAutoJumpRole2() == true
							|| mynd.getAutoJumpRole0() == true)
					{
						throw new Exception("err@流程模板设计错误，节点[" + mynd.getName() + "]设置了跳转规则，但是上一个节点的方向条件是选择的不起作用，请取消跳转规则或者设置自动计算模式的转向规则，不让其选择。");
					}
					return mynd;
				}

				throw new RuntimeException(bp.wf.Glo.multilingual("@用户没有选择发送到的节点.", "WorkNode", "no_choice_of_target_node", new String[0]));
			}

			String[] mynodes = nodes.split("[,]", -1);
			for (String item : mynodes)
			{
				if (DataType.IsNullOrEmpty(item))
				{
					continue;
				}

				//排除到达自身节点.
				if (Objects.equals(String.valueOf(this.getHisNode().getNodeID()), item))
				{
					continue;
				}

				Node mynd= new Node(Integer.parseInt(item));
				if (mynd.getAutoJumpRole1() == true
						|| mynd.getAutoJumpRole2() == true
						|| mynd.getAutoJumpRole0() == true
						|| mynd.getAutoJumpRole3() == true)
				{
					throw new Exception("err@流程模板设计错误，节点[" + mynd.getName() + "]设置了跳转规则，但是上一个节点的方向条件是选择的不起作用，请取消跳转规则或者设置自动计算模式的转向规则，不让其选择。");
				}
				return mynd;
			}

			//设置他为空,以防止下一次发送出现错误.
			this.getHisGenerWorkFlow().setParasToNodes("");
		}

		Node nd = NodeSend_GenerNextStepNode_Ext1();
		return nd;
	}
	/**
	 知否执行了跳转.
	 */
	public boolean ItIsSkip = false;
	/**
	 获取下一步骤的工作节点.

	 @return
	 */
	public final Node NodeSend_GenerNextStepNode_Ext1() throws Exception {
		//如果要是跳转到的节点，自动跳转规则规则就会失效。
		if (this.JumpToNode != null)
		{
			return this.JumpToNode;
		}

		Node mynd = this.getHisNode();
		Work mywork = this.getHisWork();
		int beforeSkipNodeID = 0; //added by liuxc,2015-7-13,标识自动跳转之前的节点ID



		///#region (最后)判断是否有延续流程.
		if (this.getHisNode().getSubFlowYanXuNum() > 0)
		{
			SubFlowYanXus ygflows = new SubFlowYanXus(this.getHisNode().getNodeID());
			if (ygflows.size() != 0 && 1 == 2)
			{
				for (SubFlowYanXu item : ygflows.ToJavaList())
				{
					boolean isPass = false;

					if (item.getExpType() == ConnDataFrom.Paras)
					{
						isPass = bp.wf.Glo.CondExpPara(item.getCondExp(), this.rptGe.getRow(), this.getWorkID());
					}

					if (item.getExpType() == ConnDataFrom.SQL)
					{
						isPass = bp.wf.Glo.CondExpSQL(item.getCondExp(), this.rptGe.getRow(), this.getWorkID());
					}

					if (isPass == true)
					{
						return new Node(Integer.parseInt(item.getSubFlowNo() + "01"));
					}
				}
			}
		}

		///#endregion (最后)判断是否有延续流程.


		///#region 计算到达的节点.
		this.setNdFrom(this.getHisNode());
		String Executor = ""; //实际执行人
		String ExecutorName = ""; //实际执行人名称
		while (true)
		{
			//上一步的工作节点.
			int prvNodeID = mynd.getNodeID();
			if (mynd.getItIsEndNode())
			{
				/*如果是最后一个节点了,仍然找不到下一步节点...*/
				if (this.getHisGenerWorkFlow().getTransferCustomType() == TransferCustomType.ByCCBPMDefine)
				{
					this.getHisWorkFlow().getHisGenerWorkFlow().setNodeID(mynd.getNodeID());
					this.getHisWorkFlow().getHisGenerWorkFlow().setNodeName(mynd.getName());
					this.getHisGenerWorkFlow().setNodeID(mynd.getNodeID());
					this.getHisGenerWorkFlow().setNodeName(mynd.getName());
					this.getHisGenerWorkFlow().Update();
					if (DataType.IsNullOrEmpty(Executor) == false)
					{
						// this.Execer = Executor;
						this.setExecerName(ExecutorName);
					}
					String msg = this.getHisWorkFlow().DoFlowOver(ActionType.FlowOver, "流程已经走到最后一个节点，流程成功结束。", mynd, this.rptGe,  Executor, ExecutorName);
					CC(this.getHisNode());
					this.addMsg(SendReturnMsgFlag.End, msg);
					this.addMsg(SendReturnMsgFlag.IsStopFlow, "1", bp.wf.Glo.multilingual("流程已经结束", "WorkNode", "wf_end_success"), SendReturnMsgType.Info);

					this.setStopFlow(true);
				}

				return mynd;
			}

			// 获取它的下一步节点.
			Node nd = this.NodeSend_GenerNextStepNode_Ext(mynd);
			nd.WorkID =this.getWorkID(); //为获取表单ID ( NodeFrmID )提供参数.

			mynd = nd;
			Work skipWork = null;
			if (!Objects.equals(mywork.NodeFrmID, nd.getNodeFrmID()))
			{
				/* 跳过去的节点也要写入数据，不然会造成签名错误。*/
				skipWork = nd.getHisWork();

				if (!Objects.equals(skipWork.getEnMap().getPhysicsTable(), this.rptGe.getEnMap().getPhysicsTable()))
				{
					skipWork.Copy(this.rptGe);
					skipWork.Copy(mywork);

					skipWork.setOID(this.getWorkID());
					if (nd.getItIsSubThread() == true)
					{
						skipWork.setFID(mywork.getFID());
					}

					skipWork.setRec(this.getExecer());

					skipWork.ResetDefaultVal(null, null, 0);

					// 把里面的默认值也copy报表里面去.
					rptGe.Copy(skipWork);

					//如果存在就修改
					if (skipWork.IsExit(skipWork.getPK(), this.getWorkID()) == true)
					{
						int count = skipWork.RetrieveFromDBSources();
						if (count == 1)
						{
							skipWork.DirectUpdate();
						}
						else
						{
							skipWork.DirectInsert();
						}
					}
					else
					{
						skipWork.InsertAsOID(this.getWorkID());
					}
				}

				ItIsSkip = true;
				mywork = skipWork;
			}

			if (!DataType.IsNullOrEmpty(Executor))
			{
				this.setExecer(Executor);
				this.setExecerName(ExecutorName);
			}

			//判断是否是跳转时间 0=发送的时候检测。. 1=打开的时候检测.
			if (nd.getSkipTime() == 1)
			{
				return nd;
			}

			//如果没有设置跳转规则，就返回他们.
			if (nd.getAutoJumpRole0() == false && nd.getAutoJumpRole1() == false && nd.getAutoJumpRole2() == false && nd.getAutoJumpRole3() == false && nd.getHisWhenNoWorker() == false)
			{
				return nd;
			}

			DataTable dt = new DataTable();

			//如果是自动计算未来接受人.
			if (this.getHisFlow().getItIsFullSA() == true)
			{
				String sql = "SELECT FK_Emp FROM WF_SelectAccper WHERE FK_Node=" + nd.getNodeID() + "  AND WorkID=" + this.getWorkID();
				dt = DBAccess.RunSQLReturnTable(sql);
			}
			WorkNode toWn = new WorkNode(this.getWorkID(), nd.getNodeID());
			if(dt.Rows.size() == 0){
				FindWorker fw = new FindWorker();
				dt = fw.DoIt(this.getHisFlow(), this, toWn); // 找到下一步骤的接收人.
			}
			if (skipWork == null)
			{
				skipWork = toWn.getHisWork();
			}
			Executor = ""; //实际执行人
			ExecutorName = ""; //  实际执行人名称
			Emp emp = new Emp();
			//判断是不是需要增加自动审核的意见
			boolean isFWC = false;
			if (nd.getFrmWorkCheckSta() == FrmWorkCheckSta.Enable)
			{
				FrmNodes frmNodes = new FrmNodes();
				frmNodes.Retrieve(FrmNodeAttr.FK_Node, nd.getNodeID());
				if (frmNodes.size() == 0) isFWC = true;
				if(frmNodes.size()>=1)
				{
					Entity frmNode = frmNodes.Filter(FrmNodeAttr.IsEnableFWC, "1");
					if(frmNode!=null) isFWC = true;
				}

			}
			if (dt == null || dt.Rows.size() == 0)
			{
				if (nd.getHisWhenNoWorker() == true)
				{
					this.AddToTrack(ActionType.Skip, this.getExecer(), this.getExecerName(), nd.getNodeID(), nd.getName(), bp.wf.Glo.multilingual("自动跳转(启用跳转规则,当没有找到处理人时)", "WorkNode", "system_error_jump_automatically_1", new String[0]), getNdFrom());
					if(isFWC == true)
					{
						String tag = nd.getNodeID() + "_" + this.getWorkID() + "_" + this.getHisGenerWorkFlow().getFID() + "_" + WebUser.getNo();
						Dev2Interface.WriteTrack(nd.getFlowNo(), nd.getNodeID(), nd.getName(), this.getWorkID(), this.getHisGenerWorkFlow().getFID(), nd.GetValStringByKey(NodeWorkCheckAttr.FWCDefInfo, "同意"), ActionType.AutoWorkCheck, tag,null,null);

					}
					setNdFrom(nd);
					continue;
				}
				else
				{
					//抛出异常.
					String[] para = new String[1];
					para[0] = nd.getName();
					throw new RuntimeException(bp.wf.Glo.multilingual("@没有找到节点[{0}]的处理人", "WorkNode", "system_error_not_found_operator", para));
				}
			}


			///#region 处理人就是发起人
			if (nd.getAutoJumpRole0())
			{
				boolean isHave = false;
				for (DataRow dr : dt.Rows)
				{
					// 如果出现了 处理人就是发起人的情况.
					if (Objects.equals(dr.getValue(0).toString(), this.getHisGenerWorkFlow().getStarter()))
					{

						///#region 处理签名，让签名的人是发起人。


						Attrs attrs = skipWork.getEnMap().getAttrs();
						boolean isUpdate = false;
						for (Attr attr : attrs)
						{
							if (attr.getUIIsReadonly() && attr.getUIVisible() == true && DataType.IsNullOrEmpty(attr.getDefaultValOfReal()) == false)
							{
								if (Objects.equals(attr.getDefaultValOfReal(), "@WebUser.No"))
								{
									skipWork.SetValByKey(attr.getKey(), this.getHisGenerWorkFlow().getStarter());
									isUpdate = true;
								}
								if (Objects.equals(attr.getDefaultValOfReal(), "@WebUser.Name"))
								{
									skipWork.SetValByKey(attr.getKey(), this.getHisGenerWorkFlow().getStarterName());
									isUpdate = true;
								}
								if (Objects.equals(attr.getDefaultValOfReal(), "@WebUser.getDeptNo()"))
								{
									skipWork.SetValByKey(attr.getKey(), this.getHisGenerWorkFlow().getDeptNo());
									isUpdate = true;
								}
								if (Objects.equals(attr.getDefaultValOfReal(), "@WebUser.getDeptName()"))
								{
									skipWork.SetValByKey(attr.getKey(), this.getHisGenerWorkFlow().getDeptName());
									isUpdate = true;
								}
							}
						}
						if (isUpdate)
						{
							skipWork.DirectUpdate();
						}

						///#endregion 处理签名，让签名的人是发起人。

						isHave = true;
						Executor = dr.getValue(0).toString();
						emp = new Emp(Executor);
						ExecutorName = emp.getName();
						break;
					}
				}

				if (isHave == true)
				{
					/*如果发现了，当前人员包含处理人集合. */
					this.AddToTrack(ActionType.Skip, Executor, ExecutorName, nd.getNodeID(), nd.getName(), bp.wf.Glo.multilingual("自动跳转(处理人就是发起人)", "WorkNode", "system_error_jump_automatically_2", new String[0]), getNdFrom());
					if (isFWC == true)
					{
						String tag = nd.getNodeID() + "_" + this.getWorkID() + "_" + this.getHisGenerWorkFlow().getFID() + "_" + Executor;
						Dev2Interface.WriteTrack(nd.getFlowNo(), nd.getNodeID(), nd.getName(), this.getWorkID(), this.getHisGenerWorkFlow().getFID(), nd.GetValStringByKey(NodeWorkCheckAttr.FWCDefInfo, "同意"),
								ActionType.AutoWorkCheck, tag, null, null, null, Executor, ExecutorName, Executor, ExecutorName);
					}
					//增加当前节点的处理人的GenerWorkerList
					GenerWorkerList gwl = new GenerWorkerList();
					gwl.setWorkID(this.getWorkID());
					gwl.setFID(this.getHisGenerWorkFlow().getFID());
					gwl.setNodeID(nd.getNodeID());
					gwl.setNodeName(nd.getName());
					gwl.setEmpNo(Executor);
					gwl.setEmpName(ExecutorName);
					gwl.setDeptNo(emp.getDeptNo());
					gwl.setDeptName(emp.getDeptText());
					gwl.setWhoExeIt(nd.getWhoExeIt());
					gwl.setSDT(DataType.getCurrentDateTime());
					gwl.setDTOfWarning(DataType.getCurrentDateTime());
					gwl.setCDT(DataType.getCurrentDateTime());
					gwl.setFlowNo(nd.getFlowNo());
					gwl.setSender(getWebUser().getNo() + "," + getWebUser().getName());
					gwl.setPassInt(1);
					gwl.setItIsRead(true);
					gwl.Insert();
					ExecEvent.DoNode(EventListNode.SendWhen, nd, skipWork, null);
					ExecEvent.DoNode(EventListNode.SendSuccess, mynd, skipWork, this.HisMsgObjs, null);
					setNdFrom(nd);
					CC(nd); //执行抄送


					_SkipNode = nd;
					continue;
				}

				//如果没有跳转,判断是否,其他两个条件是否设置.
				if (nd.getAutoJumpRole1() == false && nd.getAutoJumpRole2() == false && nd.getAutoJumpRole3() == false)
				{
					return nd;
				}
			}

			///#endregion


			///#region 处理人已经出现过
			if (nd.getAutoJumpRole1() == true)
			{
				boolean isHave = false;
				for (DataRow dr : dt.Rows)
				{
					// 如果出现了处理人就是提交人的情况.
					String sql = "SELECT COUNT(*) FROM WF_GenerWorkerlist WHERE FK_Emp='" + dr.getValue(0).toString() + "' AND WorkID=" + this.getWorkID();
					if (DBAccess.RunSQLReturnValInt(sql) !=0)
					{
						/*这里不处理签名.*/
						isHave = true;
						Executor = dr.getValue(0).toString();
						emp = new Emp(Executor);
						ExecutorName = emp.getName();
						break;
					}
				}

				if (isHave == true)
				{
					this.AddToTrack(ActionType.Skip, Executor, ExecutorName, nd.getNodeID(), nd.getName(), bp.wf.Glo.multilingual("自动跳转(操作人已经完成)", "WorkNode", "system_error_jump_automatically_3", new String[0]), getNdFrom());
					if (isFWC == true)
					{
						String tag = nd.getNodeID() + "_" + this.getWorkID() + "_" + this.getHisGenerWorkFlow().getFID() + "_" + Executor;
						Dev2Interface.WriteTrack(nd.getFlowNo(), nd.getNodeID(), nd.getName(), this.getWorkID(), this.getHisGenerWorkFlow().getFID(), nd.GetValStringByKey(NodeWorkCheckAttr.FWCDefInfo, "同意"),
								ActionType.AutoWorkCheck, tag, null, null, null, Executor, ExecutorName, Executor, ExecutorName);
					}
					//增加当前节点的处理人的GenerWorkerList
					GenerWorkerList gwl = new GenerWorkerList();
					gwl.setWorkID(this.getWorkID());
					gwl.setFID(this.getHisGenerWorkFlow().getFID());
					gwl.setNodeID(nd.getNodeID());
					gwl.setNodeName(nd.getName());
					gwl.setEmpNo(Executor);
					gwl.setEmpName(ExecutorName);
					if (dt.Rows.get(0).getTable().Columns.contains("FK_Dept"))
					{
						gwl.setDeptNo(dt.Rows.get(0).getValue(1).toString());
						Dept dept = new Dept(gwl.getDeptNo());
						gwl.setDeptName(dept.getName());
						if (dept.RetrieveFromDBSources() == 0)
						{
							gwl.setDeptNo(emp.getDeptNo());
							gwl.setDeptName(emp.getDeptText());
						}
					}
					else
					{
						gwl.setDeptNo(emp.getDeptNo());
						gwl.setDeptName(emp.getDeptText());
					}
					gwl.setWhoExeIt(nd.getWhoExeIt());
					gwl.setSDT(DataType.getCurrentDateTime());
					gwl.setDTOfWarning(DataType.getCurrentDateTime());
					gwl.setCDT(DataType.getCurrentDateTime());
					gwl.setFlowNo(nd.getFlowNo());
					gwl.setSender(getWebUser().getNo() + "," + getWebUser().getName());
					gwl.setPassInt(1);
					gwl.setItIsRead(true);
					gwl.Insert();
					ExecEvent.DoNode(EventListNode.SendWhen, nd, skipWork, null);
					ExecEvent.DoNode(EventListNode.SendSuccess, mynd, skipWork, this.HisMsgObjs, null);
					setNdFrom(nd);
					CC(nd); //执行抄送

					_SkipNode = nd;
					continue;
				}

				//如果没有跳转,判断是否,其他两个条件是否设置.
				if (nd.getAutoJumpRole2() == false && nd.getAutoJumpRole3() == false)
				{
					return nd;
				}
			}

			///#endregion 处理人已经出现过


			///#region 处理人与上一步相同
			if (nd.getAutoJumpRole2())
			{
				boolean isHave = false;
				for (DataRow dr : dt.Rows)
				{
					//By lyc 20240701宁波港测试的问题，SAAS版找上一步处理人时人员编号为UserID
					String fk_emp = dr.getValue(0).toString();
					if(SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS){
						bp.port.Emp pe = new Emp(fk_emp);
						fk_emp = pe.getUserID();
					}
					String sql = "SELECT COUNT(*) FROM WF_GenerWorkerlist WHERE FK_Emp='" + fk_emp + "' AND WorkID=" + this.getWorkID() + " AND FK_Node=" + (beforeSkipNodeID > 0 ? beforeSkipNodeID : prvNodeID); //edited by liuxc,2015-7-13
					if (DBAccess.RunSQLReturnValInt(sql) == 1)
					{
						/*这里不处理签名.*/
						isHave = true;
						Executor = dr.getValue(0).toString();
						emp = new Emp(Executor);
						ExecutorName = emp.getName();
						break;
					}
				}

				if (isHave == true)
				{
					//added by liuxc,2015-7-13,生成跳过的节点数据
					//记录最开始相同处理人的节点ID，用来上面查找SQL判断
					if (beforeSkipNodeID == 0)
					{
						beforeSkipNodeID = prvNodeID;
					}

					Work wk = nd.getHisWork();
					wk.Copy(mywork);
					//存储在相同的表中，不需要拷贝
					if (!Objects.equals(wk.NodeFrmID, nd.getNodeFrmID()))
					{
						if (!Objects.equals(wk.getEnMap().getPhysicsTable(), this.rptGe.getEnMap().getPhysicsTable()))
						{
							wk.Copy(this.rptGe);
						}
						wk.setRec(getWebUser().getNo());

						wk.setOID(this.getWorkID());
						wk.ResetDefaultVal(null, null, 0);
					}

					//执行表单填充，如果有，修改新昌方面同时修改本版本，added by liuxc,2015-10-16
					Object tempVar = nd.getMapData().getMapExts().GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.PageLoadFull);
					MapExt item = tempVar instanceof MapExt ? (MapExt)tempVar : null;
					bp.wf.CCFormAPI.DealPageLoadFull(wk, item, nd.getMapData().getMapAttrs(), nd.getMapData().getMapDtls());

					wk.DirectSave();

					//added by liuxc,2015-10-16

					///#region //此处时，跳转的节点如果有签章，则签章路径会计算错误，需要重新计算一下签章路径，暂时没找到好法子，将UCEn.ascx.cs中的计算签章的逻辑挪过来使用
					FrmImgs imgs = new FrmImgs();
					imgs.Retrieve(FrmImgAttr.FrmID, "ND" + nd.getNodeID(), FrmImgAttr.ImgAppType, 1, FrmImgAttr.IsEdit, 1, null);

					for (FrmImg img : imgs.ToJavaList())
					{
						//获取登录人角色
						String stationNo = "";
						//签章对应部门
						String fk_dept = getWebUser().DeptNo;
						//部门来源类别
						String sealType = "0";
						//签章对应角色
						String fk_station = img.getTag0();
						//表单字段
						String sealField = "";
						String imgSrc = "";
						String sql = "";

						//如果设置了部门与角色的集合进行拆分
						if (!DataType.IsNullOrEmpty(img.getTag0()) && img.getTag0().contains("^") && img.getTag0().split("[^ ]", -1).length == 4)
						{
							fk_dept = img.getTag0().split("[^ ]", -1)[0];
							fk_station = img.getTag0().split("[^ ]", -1)[1];
							sealType = img.getTag0().split("[^ ]", -1)[2];
							sealField = img.getTag0().split("[^ ]", -1)[3];
							//如果部门没有设定，就获取部门来源
							if (Objects.equals(fk_dept, "all"))
							{
								//发起人
								if (Objects.equals(sealType, "1"))
								{
									sql = "SELECT FK_Dept FROM WF_GenerWorkFlow WHERE WorkID=" + wk.getOID();
									fk_dept = DBAccess.RunSQLReturnString(sql);
								}
								//表单字段
								if (Objects.equals(sealType, "2") && !DataType.IsNullOrEmpty(sealField))
								{
									//判断字段是否存在
									for (MapAttr attr : nd.getMapData().getMapAttrs().ToJavaList())
									{
										if (Objects.equals(attr.getKeyOfEn(), sealField))
										{
											fk_dept = wk.GetValStrByKey(sealField);
											break;
										}
									}
								}
							}
						}

						sql = String.format(" select FK_Station from Port_DeptStation where FK_Dept ='%1$s' and FK_Station in (select FK_Station from  Port_DeptEmpStation where FK_Emp='%2$s')", fk_dept, getWebUser().getNo());
						dt = DBAccess.RunSQLReturnTable(sql);
						for (DataRow dr : dt.Rows)
						{
							if (fk_station.contains(dr.getValue(0) + ","))
							{
								stationNo = dr.getValue(0).toString();
								break;
							}
						}

						try
						{
							imgSrc = bp.wf.Glo.getCCFlowAppPath() + "DataUser/Seal/" + fk_dept + "_" + stationNo + ".jpg";
							//设置主键
							String myPK = DataType.IsNullOrEmpty(img.getEnPK()) ? "seal" : img.getEnPK();
							myPK = myPK + "_" + wk.getOID() + "_" + img.getMyPK();

							FrmEleDB imgDb = new FrmEleDB(myPK);
							//判断是否存在
							if (imgDb != null && !DataType.IsNullOrEmpty(imgDb.getFrmID()))
							{
								imgDb.setFrmID(DataType.IsNullOrEmpty(img.getEnPK()) ? "seal" : img.getEnPK());
								imgDb.setEleID(String.valueOf(wk.getOID()));
								imgDb.setRefPKVal( img.getMyPK());
								imgDb.setTag1(imgSrc);
								imgDb.Update();
							}
							else
							{
								imgDb.setFrmID(DataType.IsNullOrEmpty(img.getEnPK()) ? "seal" : img.getEnPK());
								imgDb.setEleID(String.valueOf(wk.getOID()));
								imgDb.setRefPKVal( img.getMyPK());
								imgDb.setTag1(imgSrc);
								imgDb.Insert();
							}
						}
						catch (RuntimeException ex)
						{
						}
					}

					this.AddToTrack(ActionType.Skip, Executor, ExecutorName, nd.getNodeID(), nd.getName(), bp.wf.Glo.multilingual("自动跳转(操作人与上一步相同)", "WorkNode", "system_error_jump_automatically_2", new String[0]), getNdFrom());
					if (isFWC == true)
					{
						String tag = nd.getNodeID() + "_" + this.getWorkID() + "_" + this.getHisGenerWorkFlow().getFID() + "_" + Executor;
						Dev2Interface.WriteTrack(nd.getFlowNo(), nd.getNodeID(), nd.getName(), this.getWorkID(), this.getHisGenerWorkFlow().getFID(), nd.GetValStringByKey(NodeWorkCheckAttr.FWCDefInfo, "同意"),
								ActionType.AutoWorkCheck, tag, null, null, null, Executor, ExecutorName, Executor, ExecutorName);
					}
					//增加当前节点的处理人的GenerWorkerList
					GenerWorkerList gwl = new GenerWorkerList();
					gwl.setWorkID(this.getWorkID());
					gwl.setFID(this.getHisGenerWorkFlow().getFID());
					gwl.setNodeID(nd.getNodeID());
					gwl.setNodeName(nd.getName());
					gwl.setEmpNo(Executor);
					gwl.setEmpName(ExecutorName);

					if (dt.Rows.get(0).getTable().Columns.contains("FK_Dept"))
					{
						gwl.setDeptNo(dt.Rows.get(0).getValue(1).toString());
						Dept dept = new Dept(gwl.getDeptNo());
						gwl.setDeptName(dept.getName());
						if (dept.RetrieveFromDBSources() == 0)
						{
							gwl.setDeptNo(emp.getDeptNo());
							gwl.setDeptName(emp.getDeptText());
						}
					}
					else
					{
						gwl.setDeptNo(emp.getDeptNo());
						gwl.setDeptName(emp.getDeptText());
					}
					gwl.setWhoExeIt(nd.getWhoExeIt());
					gwl.setSDT(DataType.getCurrentDateTime());
					gwl.setDTOfWarning(DataType.getCurrentDateTime());
					gwl.setCDT(DataType.getCurrentDateTime());
					gwl.setFlowNo(nd.getFlowNo());
					gwl.setSender(getWebUser().getNo() + "," + getWebUser().getName());
					gwl.setPassInt(1);
					gwl.setItIsRead(true);
					gwl.Insert();
					ExecEvent.DoNode(EventListNode.SendWhen, nd, wk, null);
					ExecEvent.DoNode(EventListNode.SendSuccess, mynd, wk);
					CC(nd); //执行抄送

					///#endregion
					setNdFrom(nd);
					_SkipNode = nd;
					continue;
				}
				//没有跳出转的条件，就返回本身.
				if (nd.getAutoJumpRole3() == false)
					return nd;
			}

			///#endregion 处理人与上一步相同
			//未来节点处理人已经出现过
			if (nd.getAutoJumpRole3() == true)
			{
				if (this.getHisFlow().getItIsFullSA() == false)
					return nd;
				//将要跳转的节点配置了流程完成条件，先计算完成条件
				if(nd.getCondsOfFlowComplete().size() >0){
					if(nd.getCondsOfFlowComplete().GenerResult(this.rptGe, this.getWebUser())){
						return nd;
					}
				}
				boolean isHave = false;
				for (DataRow dr : dt.Rows)
				{
					// 如果出现了处理人就是提交人的情况.
					String sql = "SELECT COUNT(*) FROM WF_SelectAccper WHERE FK_Emp='" + dr.getValue(0).toString() + "' AND WorkID=" + this.getWorkID() + " AND FK_Node NOT IN(SELECT DISTINCT(FK_Node) FROM WF_GenerWorkerlist WHERE  WorkID=" + this.getWorkID() + ") AND FK_Node !=" + nd.getNodeID();
					if (DBAccess.RunSQLReturnValInt(sql) >0)
					{
						/*这里不处理签名.*/
						isHave = true;
						Executor = dr.getValue(0).toString();
						emp = new Emp(Executor);
						ExecutorName = emp.getName();
						break;
					}
				}

				if (isHave == true)
				{
					this.AddToTrack(ActionType.Skip, Executor, ExecutorName, nd.getNodeID(), nd.getName(), bp.wf.Glo.multilingual("自动跳转(操作人已经完成)", "WorkNode", "system_error_jump_automatically_3", new String[0]), getNdFrom());
					if (isFWC == true)
					{
						String tag = nd.getNodeID() + "_" + this.getWorkID() + "_" + this.getHisGenerWorkFlow().getFID() + "_" + Executor;
						bp.wf.Dev2Interface.WriteTrack(nd.getFlowNo(), nd.getNodeID(), nd.getName(), this.getWorkID(), this.getHisGenerWorkFlow().getFID(), nd.GetValStringByKey(NodeWorkCheckAttr.FWCDefInfo, "同意"),
								ActionType.AutoWorkCheck, tag, null, null, null, Executor, ExecutorName, Executor, ExecutorName);
					}
					//增加当前节点的处理人的GenerWorkerList
					GenerWorkerList gwl = new GenerWorkerList();
					gwl.setWorkID(this.getWorkID());
					gwl.setFID(this.getHisGenerWorkFlow().getFID());
					gwl.setNodeID(nd.getNodeID());
					gwl.setNodeName(nd.getName());
					gwl.setEmpNo(Executor);
					gwl.setEmpName(ExecutorName);
					if (dt.Rows.get(0).getTable().Columns.contains("FK_Dept"))
					{
						gwl.setDeptNo(dt.Rows.get(0).getValue(1).toString());
						Dept dept = new Dept(gwl.getDeptNo());
						gwl.setDeptName(dept.getName());
						if (dept.RetrieveFromDBSources() == 0)
						{
							gwl.setDeptNo(emp.getDeptNo());
							gwl.setDeptName(emp.getDeptText());
						}
					}
					else
					{
						gwl.setDeptNo(emp.getDeptNo());
						gwl.setDeptName(emp.getDeptText());
					}

					gwl.setWhoExeIt(nd.getWhoExeIt());
					gwl.setSDT(DataType.getCurrentDateTime());
					gwl.setDTOfWarning(DataType.getCurrentDateTime());
					gwl.setCDT(DataType.getCurrentDateTime());
					gwl.setFlowNo(nd.getFlowNo());
					gwl.setSender(getWebUser().getNo() + "," + getWebUser().getName());
					gwl.setPassInt(1);
					gwl.setItIsRead(true);
					gwl.Insert();
					ExecEvent.DoNode(EventListNode.SendWhen, nd, skipWork, null);
					ExecEvent.DoNode(EventListNode.SendSuccess, mynd, skipWork, this.HisMsgObjs, null);
					CC(nd);//执行抄送

					setNdFrom(nd);
					_SkipNode = nd;
					continue;
				}
				return nd;
			}



			///#region 按照 表达式 处理. yln
			if (DataType.IsNullOrEmpty(nd.getAutoJumpExp()) == false)
			{


				Object tempVar2 = nd.getAutoJumpExp();
				String exp = tempVar2 instanceof String ? (String)tempVar2 : null;
				try
				{
					exp = bp.wf.Glo.DealExp(exp, this.rptGe, null);

					if (exp.toLowerCase().contains("select") == true)
					{
						float val = DBAccess.RunSQLReturnValFloat(exp, 0);
						if (val >= 0)
						{
							return nd;
						}
					}
					else
					{
						if (exp.contains("?") == true)
						{
							exp += "&";
						}
						else
						{
							exp += "?";
						}

						exp += "WorkID=" + rptGe.getOID() + "&Token=" + getWebUser().Token + "&UserNo=" + getWebUser().No;

						String str = DataType.ReadURLContext(exp, 10000);
						float val = Float.parseFloat(str);
						if (val <= 0)
						{
							return nd;
						}
					}
				}
				catch (RuntimeException ex)
				{
					throw new RuntimeException("err@判断exp跳转错误，表达式:" + exp + " 信息:" + ex.getMessage());
				}
			}

			///#endregion 按照 表达式 处理.


			if (nd.getHisWhenNoWorker() == true)
			{
				return nd;
			}

			mynd = nd;
			setNdFrom(nd);
		} //结束循环。
		//throw new RuntimeException(bp.wf.Glo.multilingual("@找到下一步节点.", "WorkNode", "found_next_node", new String[0]));
	}

	private void CC(Node node) throws Exception {
		//if(this.getSkipNode()!=null && this.getSkipNode().getNodeID() !=node.getNodeID())
		//	node = this.getSkipNode();
		WorkCC cc = new WorkCC(this, this.getWebUser());
		cc.DoCC("WorkNode"); //执行抄送动作.
		//当前节点是否启用手工抄送
		if(node.getHisCCRole() == CCRoleEnum.HandCC){
			WorkOpt workOpt = new WorkOpt();
			workOpt.setMyPK(this.getWebUser().getNo()+"_"+node.getNodeID()+"_"+this.getWorkID());
			if(workOpt.RetrieveFromDBSources()==1){
				String emps = workOpt.getCCEmps();
				emps +=","+ bp.port.Glo.GenerEmpNosByDeptNos(workOpt.getCCDepts());
				emps +=","+ bp.port.Glo.GenerEmpNosByStationNos(workOpt.getCCStations());
				String ccMsg = Dev2Interface.Node_CCToEmps(workOpt.getNodeID(), workOpt.getWorkID(), emps, "来自" + WebUser.getName() + "的抄送", workOpt.getCCNote());
				if(DataType.IsNullOrEmpty(ccMsg) == false)
					this.addMsg("HandlerCC",ccMsg);
			}
		}
		//String ccRole = node.GetParaString("CCRoleNum");
		//if (DataType.IsNullOrEmpty(ccRole) == true)
		//{
		//    //for vue3版本.
		//    CCRoles ens = new CCRoles();
		//    ens.Retrieve(CCRoleAttr.NodeID, node.getNodeID());
		//    node.SetPara("CCRoleNum", ens.size());
		//    node.Update();

		//    // String msg = WorkFlowBuessRole.DoCCAuto(node, this.rptGe, this.getWorkID(), this.HisWork.FID);
		//   // this.addMsg("CC", BP.WF.Glo.multilingual("@自动抄送给:{0}.", "WorkNode", "cc", msg));
		//    return;
		//}
		//if (DataType.IsNullOrEmpty(ccRole) == false && ccRole.equals("0") == false)
		//{
		//    CCRoles ens = new CCRoles();
		//    ens.Retrieve(CCRoleAttr.NodeID, node.getNodeID());

		//    // String msg = WorkFlowBuessRole.DoCCAuto(node, this.rptGe, this.getWorkID(), this.HisWork.FID);
		//   // this.addMsg("CC", BP.WF.Glo.multilingual("@自动抄送给:{0}.", "WorkNode", "cc", msg));
		//    return;
		//}
	}
	/**
	 处理OrderTeamup退回模式
	 */
	public final void DealReturnOrderTeamup() throws Exception {
		/*如果协作，顺序方式.*/
		GenerWorkerList gwl = new GenerWorkerList();
		gwl.setEmpNo(getWebUser().getNo());
		gwl.setNodeID(this.getHisNode().getNodeID());
		gwl.setWorkID(this.getWorkID());
		if (gwl.RetrieveFromDBSources() == 0)
		{
			throw new RuntimeException(bp.wf.Glo.multilingual("@没有找到自己期望的数据.", "WorkNode", "not_found_my_expected_data", new String[0]));
		}

		gwl.setItIsPass(true);
		gwl.Update();
		///#region 要计算当前人员的应完成日期
		// 计算出来 退回到节点的应完成时间.
		//增加天数. 考虑到了节假日.
		Date dtOfShould = Glo.AddDayHoursSpan(new Date(), this.getHisNode().getTimeLimit(), this.getHisNode().getTimeLimitHH(), this.getHisNode().getTimeLimitMM(), this.getHisNode().getTWay());

		// 应完成日期.
		String sdt = DateUtils.format(dtOfShould,DataType.getSysDateTimeFormat());
		///#endregion
		//更新日期，为了考核.
		if (this.getHisNode().getHisCHWay() == CHWay.None)
			sdt = "无";
		GenerWorkerLists ens = new GenerWorkerLists();
		String varAcceptersID="";
		String varAcceptersName="";
		String todoEmps ="";
		if(this.JumpToNode.getItIsBackResetAccepter() == true){
			ens.Retrieve(GenerWorkerListAttr.WorkID,this.getWorkID(),GenerWorkerListAttr.FK_Node,this.JumpToNode.getNodeID());
			if(ens.size() == 0)
				throw new RuntimeException(bp.wf.Glo.multilingual("@没有找到接收人期望的数据，在协作模式的按照顺序退回的时候.", "WorkNode", "not_found_receiver_expected_data", new String[0]));

			for(GenerWorkerList curgwl :ens.ToJavaList()){
				curgwl.setSDT(sdt);
				curgwl.setRDT(DataType.getCurrentDateTimess());
				curgwl.setItIsPass(false);
				curgwl.setItIsRead(false);
				if(this.JumpToNode.getTodolistModel() == TodolistModel.Order && curgwl.getIdx()!=1)
					curgwl.setPassInt(curgwl.getIdx()+100);
				curgwl.Update();
				varAcceptersID+=curgwl.getEmpNo()+",";
				varAcceptersName+=curgwl.getEmpName()+",";
				todoEmps+=curgwl.getEmpNo()+","+curgwl.getEmpName()+";";
			}
			varAcceptersID = varAcceptersID.substring(0,varAcceptersID.length()-1);
			varAcceptersName = varAcceptersName.substring(0,varAcceptersName.length()-1);
		}else{
			gwl.setEmpNo(this.JumpToEmp);
			gwl.setNodeID(this.JumpToNode.getNodeID());
			gwl.setWorkID(this.getWorkID());
			if (gwl.RetrieveFromDBSources() == 0)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@没有找到接收人期望的数据，在协作模式的按照顺序退回的时候.", "WorkNode", "not_found_receiver_expected_data", new String[0]));
			}
			gwl.setSDT(sdt);
			gwl.setRDT(DataType.getCurrentDateTimess());
			gwl.setItIsPass(false);
			gwl.Update();
			varAcceptersID+=gwl.getEmpNo();
			varAcceptersName+= gwl.getEmpName();
			todoEmps+=gwl.getEmpNo()+","+gwl.getEmpName()+";";
			ens.AddEntity(gwl);
		}
		this.HisWorkerLists = ens;

		this.addMsg(SendReturnMsgFlag.VarAcceptersID, varAcceptersID, varAcceptersID, SendReturnMsgType.SystemMsg);
		this.addMsg(SendReturnMsgFlag.VarAcceptersName, varAcceptersName,varAcceptersName, SendReturnMsgType.SystemMsg);
		String[] para = new String[2];
		para[0] = varAcceptersID;
		para[1] = varAcceptersName;
		String str = bp.wf.Glo.multilingual("@当前工作已经发送给退回人({0},{1}).", "WorkNode", "current_work_send_to_returner", para);

		this.addMsg(SendReturnMsgFlag.OverCurr, str, null, SendReturnMsgType.Info);

		this.getHisGenerWorkFlow().setWFState(WFState.Runing);
		this.getHisGenerWorkFlow().setNodeID(this.JumpToNode.getNodeID());
		this.getHisGenerWorkFlow().setNodeName(this.JumpToNode.getName());

		this.getHisGenerWorkFlow().setTodoEmps(todoEmps);
		this.getHisGenerWorkFlow().setTodoEmpsNum(ens.size());
		this.getHisGenerWorkFlow().setTaskSta(TaskSta.None);
		this.getHisGenerWorkFlow().Update();
	}
	/**
	 获取下一步骤的工作节点

	 @return
	 */
	public Node NodeSend_GenerNextStepNode_Ext(Node currNode) throws Exception {
		Nodes nds = currNode.getHisToNodes();
		if (nds.size() == 1)
		{
			Node toND = (Node)nds.get(0);
			if (toND.getHisNodeType() == NodeType.UserNode)
			{
				return toND;
			}
			AddToTrack(ActionType.Route, "", "", toND.getNodeID(), toND.getName(), "节点" + currNode.getName() + "经过路由" + toND.getName(),currNode);
			return NodeSend_GenerNextStepNode_Ext(toND);
		}
		if (nds.size() == 0)
		{
			throw new RuntimeException(bp.wf.Glo.multilingual("@没找到下一步节点.", "WorkNode", "not_found_next_node", new String[0]));
		}
		//获得所有的方向,按照优先级, 按照条件处理方向，如果成立就返回.
		Directions dirs = new Directions(currNode.getNodeID());
		//@yln
		Node nd = null;
		if (dirs.size() == 1)
		{
			nd = new Node(dirs.get(0).GetValIntByKey(DirectionAttr.ToNode));
			if (nd.getHisNodeType() == NodeType.UserNode)
			{
				return nd;
			}
			AddToTrack(ActionType.Route, "", "", nd.getNodeID(), nd.getName(), "节点[" + currNode.getName() + "]经过路由" + nd.getName(),currNode);
			return NodeSend_GenerNextStepNode_Ext(nd);
		}

		//定义没有条件的节点集合.
		Directions dirs0Cond = new Directions();
		if (this.SendHTOfTemp != null)
		{
			for (Object key : this.SendHTOfTemp.keySet())
			{
				if(DataType.IsNullOrEmpty(this.SendHTOfTemp.get(key)) == false){
					if (rptGe.getRow().containsKey(key) == true)
					{
						this.rptGe.getRow().SetValByKey(key.toString(), this.SendHTOfTemp.get(key));
					}
					else
					{
						this.rptGe.getRow().put(key.toString(), this.SendHTOfTemp.get(key));
					}
				}
			}
		}

		//判断当前节点是否是子线程
		if(this.getHisNode().getItIsSubThread() == true)
		{
			GenerWorkFlow gwf = new GenerWorkFlow();
			gwf.setWorkID(this.getWorkID());
			if (gwf.RetrieveFromDBSources() == 1)
			{
				AtPara atPara = gwf.getatPara();
				for(String key : atPara.getHisHT().keySet())
				{
					if (DataType.IsNullOrEmpty(key) == true)
						continue;
					this.rptGe.SetValByKey(key, atPara.GetValStrByKey(key));
				}

			}
		}
		for (Direction dir : dirs.ToJavaList())
		{
			//查询出来他的条件.
			Conds conds = new Conds();
			conds.Retrieve(CondAttr.FK_Node, currNode.getNodeID(), CondAttr.ToNodeID, dir.getToNode(), CondAttr.CondType, CondType.Dir.getValue(), CondAttr.Idx);

			//可以到达的节点.
			if (conds.size() == 0)
			{
				dirs0Cond.AddEntity(dir); //把他加入到里面.
				continue;
			}

			//按条件计算.
			if (conds.GenerResult(this.rptGe, this.getWebUser(),this.getHisNode()) == true)
			{
				nd = new Node(dir.getToNode());
				if (nd.getHisNodeType() == NodeType.UserNode)
				{
					return nd;
				}
				AddToTrack(ActionType.Route, "", "", nd.getNodeID(), nd.getName(), "节点" + currNode.getName() + "经过路由" + nd.getName(),currNode);
				return NodeSend_GenerNextStepNode_Ext(nd);
			}
		}

		if (dirs0Cond.size() == 0)
		{
			throw new RuntimeException("err@流程设计错误:[" + currNode.getNodeID() + "," + currNode.getName() + "]到达的节点，所有的条件都不成立.");
		}

		if (dirs0Cond.size() != 1)
		{
			throw new RuntimeException("err@流程设计错误:当前节点[" + currNode.getName() + "]的到达的节点，超过1个节点，没有设置方向条件.");
		}

		int toNodeID = dirs0Cond.get(0).GetValIntByKey(DirectionAttr.ToNode);
		//@yln
		nd = new Node(toNodeID);
		if (nd.getHisNodeType() == NodeType.UserNode)
		{
			return nd;
		}
		AddToTrack(ActionType.Route, "", "", nd.getNodeID(), nd.getName(), "节点" + currNode.getName() + "经过路由" + nd.getName(),currNode);
		return NodeSend_GenerNextStepNode_Ext(nd);
	}
	/**
	 获取下一步骤的节点集合

	 @return
	 */
	public final Nodes Func_GenerNextStepNodes() throws Exception {
		//如果跳转节点已经有了变量.
		if (this.JumpToNode != null)
		{
			Nodes myNodesTo = new Nodes();
			myNodesTo.AddEntity(this.JumpToNode);
			return myNodesTo;
		}

		if (this.getHisNode().getHisToNodes().size()== 1)
		{
			return this.getHisNode().getHisToNodes();
		}



		///#region 如果使用户选择的.
		if (this.getHisNode().getCondModel() == DirCondModel.ByDDLSelected || this.getHisNode().getCondModel() == DirCondModel.ByPopSelect || this.getHisNode().getCondModel() == DirCondModel.ByButtonSelected)
		{
			// 获取用户选择的节点.
			String nodes = this.getHisGenerWorkFlow().getParasToNodes();
			if (DataType.IsNullOrEmpty(nodes))
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@用户没有选择发送到的节点", "WorkNode", "no_choice_of_target_node", new String[0]));
			}

			Nodes nds = new Nodes();
			String[] mynodes = nodes.split("[,]", -1);
			for (String item : mynodes)
			{
				if (DataType.IsNullOrEmpty(item))
				{
					continue;
				}
				nds.AddEntity(new Node(Integer.parseInt(item)));
			}
			return nds;
		}

		///#endregion 如果使用户选择的.


		Nodes toNodes = this.getHisNode().getHisToNodes();
		// 如果只有一个转向节点, 就不用判断条件了,直接转向他.
		if (toNodes.size() == 1)
		{
			return toNodes;
		}

		//dcsAll.Retrieve(CondAttr.FK_Node, this.Hisnode.getNodeID(), CondAttr.Idx);


		///#region 获取能够通过的节点集合，如果没有设置方向条件就默认通过.
		Nodes myNodes = new Nodes();
		int toNodeId = 0;
		int numOfWay = 0;

		for (Node nd : toNodes.ToJavaList())
		{
			Conds dcs = new Conds();
			dcs.Retrieve(CondAttr.FK_Node, this.getHisNode().getNodeID(), CondAttr.ToNodeID, nd.getNodeID(), CondAttr.CondType, CondType.Dir.getValue(), CondAttr.Idx);

			if (dcs.size() == 0)
			{
				myNodes.AddEntity(nd);
				continue;
			}

			if (dcs.GenerResult(this.rptGe, this.getWebUser()) == true)
			{
				myNodes.AddEntity(nd);
				continue;
			}

		}

		///#endregion 获取能够通过的节点集合，如果没有设置方向条件就默认通过.


		///#region 走到最后，发现一个条件都不符合，就找没有设置方向条件的节点. （@杜翻译）
		if (myNodes.size() == 0)
		{
			/*如果没有找到其他节点，就找没有设置方向条件的节点.*/
			for (Node nd : toNodes.ToJavaList())
			{
				Conds conds = new Conds();
				int i = conds.Retrieve(CondAttr.FK_Node, nd.getNodeID(), CondAttr.CondType, 2, null);
				if (i == 0)
				{
					continue;
				}

				//增加到节点集合.
				myNodes.AddEntity(nd);
			}

			//如果没有设置方向条件的节点有多个，就清除在后面抛出异常.
			if (myNodes.size() != 1)
			{
				myNodes.clear();
			}
		}

		///#endregion 走到最后，发现一个条件都不符合，就找没有设置方向条件的节点.


		if (myNodes.size() == 0)
		{
			String[] para = new String[3];
			para[0] = this.getExecerName();
			para[1] = String.valueOf(this.getHisNode().getNodeID());
			para[2] = this.getHisNode().getName();
			throw new RuntimeException(bp.wf.Glo.multilingual("@当前用户({0})定义节点的方向条件错误:从节点({1}-{2})到其它所有节点的转向条件都不成立.", "WorkNode", "error_node_jump_condition", para));
		}

		return myNodes;
	}
	/**
	 设置当前工作已经完成.

	 @return
	 */
	private String Func_DoSetThisWorkOver() throws Exception {
		//设置结束人.
		this.rptGe.SetValByKey(GERptAttr.FK_Dept, this.getHisGenerWorkFlow().getDeptNo()); //此值不能变化.
		this.rptGe.SetValByKey(GERptAttr.FlowEnder, this.getExecer());
		this.rptGe.SetValByKey(GERptAttr.FlowEnderRDT, DataType.getCurrentDateTime());
		if (this.town == null)
		{
			this.rptGe.SetValByKey(GERptAttr.FlowEndNode, this.getHisNode().getNodeID());
		}
		else
		{
			if (this.getHisNode().getHisRunModel() == RunModel.FL || this.getHisNode().getHisRunModel() == RunModel.FHL)
			{
				this.rptGe.SetValByKey(GERptAttr.FlowEndNode, this.getHisNode().getNodeID());
			}
			else
			{
				this.rptGe.SetValByKey(GERptAttr.FlowEndNode, this.town.getHisNode().getNodeID());
			}
		}

		//有可能日期是空的。
		if (rptGe.getFlowStartRDT().length() >= 8)
		{
			this.rptGe.SetValByKey(GERptAttr.FlowDaySpan, DataType.GeTimeLimits(rptGe.getFlowStartRDT(), DataType.getCurrentDateTime()));
		}

		//如果两个物理表不想等.
		if (this.getHisWork().getEnMap().getPhysicsTable().equals(this.rptGe.getEnMap().getPhysicsTable()) == false)
		{
			// 更新状态。
			this.getHisWork().SetValByKey("CDT", DataType.getCurrentDateTime());
			this.getHisWork().setRec(this.getExecer());

			//判断是不是MD5流程？
			if (this.getHisFlow().getItIsMD5())
			{
				this.getHisWork().SetValByKey("MD5", Glo.GenerMD5(this.getHisWork()));
			}

			if (this.getHisNode().getItIsStartNode())
			{
				this.getHisWork().SetValByKey(GERptAttr.Title, this.getHisGenerWorkFlow().getTitle());
			}

			this.getHisWork().DirectUpdate();
		}

		//若当前要处理的是被授权的
		Auths aths = new Auths();
		aths.Retrieve(AuthAttr.AutherToEmpNo, this.getExecer());
		for (Auth ath : aths.ToJavaList())
		{
			String todata = ath.getTakeBackDT().replace("-", "");
			if (DataType.IsNullOrEmpty(ath.getTakeBackDT()) == false)
			{
				int mydt = Integer.parseInt(todata);
				int nodt = Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
				if (mydt > nodt)
				{
					ps.SQL = "SELECT count(WorkID) as Num FROM WF_GenerWorkerlist WHERE IsPass=0 AND FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND FK_Emp = '"+ ath.getAuther() + "'";
					ps.clear();
					ps.Add("FK_Node", this.getHisNode().getNodeID());
					ps.Add("WorkID", this.getWorkID());
					int num = DBAccess.RunSQLReturnValInt(ps,0);
					if (num > 0) {
						ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=1,IsRead=1  WHERE WorkID=" + dbStr + "WorkID AND  FK_Emp='" + ath.getAuther() + "' AND FK_Node=" + dbStr + "FK_Node";
						ps.clear();
						ps.Add("FK_Node", this.getHisNode().getNodeID());
						ps.Add("WorkID", this.getWorkID());
						DBAccess.RunSQL(ps);
					}

				}
			}
		}

		///#region 2014-08-02 删除了其他人员的待办，增加了 IsPass=0 参数.
		if (this.town != null && this.town.getHisNode().getNodeID() == this.getHisNode().getNodeID())
		{
			// 清除其他的工作者.
			//ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE IsPass=0 AND FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND FK_Emp <> " + dbStr + "FK_Emp";
			//ps.Clear();
			//ps.Add("FK_Node", this.Hisnode.getNodeID());
			//ps.Add("WorkID", this.getWorkID());
			//ps.Add("FK_Emp", this.Execer);
			//DBAccess.RunSQL(ps);
		}
		else
		{
			// 清除其他的工作者.
			ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE IsPass=0 AND FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND FK_Emp <> " + dbStr + "FK_Emp";
			ps.clear();
			ps.Add("FK_Node", this.getHisNode().getNodeID());
			ps.Add("WorkID", this.getWorkID());
			ps.Add("FK_Emp", this.getExecer(), false);
			DBAccess.RunSQL(ps);
		}

		///#endregion 2014-08-02 删除了其他人员的待办，增加了 IsPass=0 参数.

		if (this.town != null && this.town.getHisNode().getNodeID() == this.getHisNode().getNodeID())
		{
			/*如果是当前节点发给当前节点，就更新上一个节点全部完成。 */
			ps = new Paras();
			ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=1 WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND FK_Emp=" + dbStr + "FK_Emp AND IsPass=0";
			ps.Add("FK_Node", this.getHisNode().getNodeID());
			ps.Add("WorkID", this.getWorkID());
			ps.Add("FK_Emp", this.getExecer(), false);
			DBAccess.RunSQL(ps);
		}
		else
		{
			/*如果不是当前节点发给当前节点，就更新上一个节点全部完成。 */
			ps = new Paras();
			ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=1 WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND IsPass=0";
			ps.Add("FK_Node", this.getHisNode().getNodeID());
			ps.Add("WorkID", this.getWorkID());
			DBAccess.RunSQL(ps);
		}

		// 给generworkflow赋值。
		if (this.isStopFlow() == true)
		{
			this.getHisGenerWorkFlow().setWFState(WFState.CompleteEnd);
		}
		else
		{
			this.getHisGenerWorkFlow().setWFState(WFState.Runing);
		}

		// 流程应完成时间。
		if (this.getHisWork().getEnMap().getAttrs().contains(WorkSysFieldAttr.SysSDTOfFlow))
		{
			this.getHisGenerWorkFlow().setSDTOfFlow(this.getHisWork().GetValStrByKey(WorkSysFieldAttr.SysSDTOfFlow));
		}

		// 下一个节点应完成时间。
		if (this.getHisWork().getEnMap().getAttrs().contains(WorkSysFieldAttr.SysSDTOfNode))
		{
			this.getHisGenerWorkFlow().setSDTOfNode(this.getHisWork().GetValStrByKey(WorkSysFieldAttr.SysSDTOfNode));
		}

		//执行更新。
		if (this.isStopFlow() == false)
		{
			this.getHisGenerWorkFlow().Update();
		}
		return bp.wf.Glo.multilingual("@流程已经完成.", "WorkNode", "workflow_completed");
	}

	///#endregion 附属功能
	/**
	 普通节点到普通节点

	 @param toND 要到达的下一个节点
	 @return 执行消息
	 */
	private void NodeSend_11(Node toND) throws Exception {
		String sql = "";
		String errMsg = "";
		Work toWK = toND.getHisWork();
		toWK.setOID(this.getWorkID());
		toWK.setFID(this.getHisWork().getFID());

		// 如果执行了跳转.
		if (this.ItIsSkip == true)
		{
			toWK.RetrieveFromDBSources(); //有可能是跳转.
		}


		///#region 执行数据初始化
		// town.
		WorkNode town = new WorkNode(toWK, toND);

		errMsg = bp.wf.Glo.multilingual("@初试化他们的工作人员 - 期间出现错误.", "WorkNode", "error_initialize_workflow_operator");

		// 初试化他们的工作人员．
		current_gwls = this.Func_GenerWorkerLists(town);
		if (town.getHisNode().getHuiQianRole()== HuiQianRole.TeamupGroupLeader && town.getHisNode().getTodolistModel() == TodolistModel.TeamupGroupLeader && town.getHisNode().getHuiQianLeaderRole() == HuiQianLeaderRole.OnlyOne && current_gwls.size() > 1)
		{
			throw new RuntimeException(bp.wf.Glo.multilingual("@接收人出错! 详情:{0}.", "WorkNode", "error_sendToemps_data", "@节点" + town.getHisNode().getNodeID() + "是组长会签模式，接受人只能选择一人"));
		}

		if (town.getHisNode().getTodolistModel() == TodolistModel.Order && current_gwls.size() > 1)
		{
			/*如果到达的节点是队列流程节点，就要设置他们的队列顺序.*/
			//int idx = 0;
			for (GenerWorkerList gwl : current_gwls.ToJavaList())
			{
				if (gwl.getIdx() != 1)
				{
					gwl.setPassInt(gwl.getIdx()+100);
					gwl.Update();
				}
			}
		}

		if ((town.getHisNode().getTodolistModel() == TodolistModel.Teamup || town.getHisNode().getTodolistModel() == TodolistModel.TeamupGroupLeader) && current_gwls.size() > 1)
		{
			/*如果是协作模式 */
			if (town.getHisNode().getFWCOrderModel() == 1)
			{
				/* 如果是协作模式，并且显示排序按照官职大小排序. */
				Date dt = new Date();
				for (GenerWorkerList gwl : current_gwls.ToJavaList())
				{
					dt =  DateUtils.addMinutes(dt, 5);
					String rdt = DateUtils.format(dt,"yyyy-MM-dd HH:mm:ss");

					bp.wf.Dev2Interface.WriteTrack(
							this.getHisFlow().getNo(),
							town.getHisNode().getNodeID(),
							town.getHisNode().getName(),
							this.getWorkID(),
							town.getHisWork().getFID(),
							"",
							ActionType.WorkCheck,
							null,
							null,
							null,
							"",
							gwl.getEmpNo(),
							gwl.getEmpName(),
							gwl.getEmpNo(),
							gwl.getEmpName()
					);
				}
			}
		}



		///#region 保存目标节点数据.
		if (this.getHisWork().getEnMap().getPhysicsTable() != toWK.getEnMap().getPhysicsTable())
		{
			errMsg = bp.wf.Glo.multilingual("@保存目标节点数据 - 期间出现错误.", "WorkNode", "error_saving_target_node_data");

			//为下一步骤初始化数据.
			GenerWorkerList gwl = current_gwls.get(0) instanceof GenerWorkerList ? (GenerWorkerList)current_gwls.get(0) : null;
			toWK.setRec(gwl.getEmpNo());
			String emps = gwl.getEmpNo();
			if (current_gwls.size() != 1)
			{
				for (GenerWorkerList item : current_gwls.ToJavaList())
				{
					emps += item.getEmpNo() + ",";
				}
			}
			//toWK.Emps = emps;

			try
			{

				int count = toWK.RetrieveFromDBSources();
				if (count > 0)
				{
					toWK.DirectUpdate(); // 如果执行了跳转.
				}
				else
				{
					toWK.DirectInsert();
				}

			}
			catch (RuntimeException ex)
			{
				Log.DebugWriteError(bp.wf.Glo.multilingual("@出现SQL异常! 可能是没有修复表或者重复发送.详情:{0}.", "WorkNode", "sql_exception_1", ex.getMessage()));
				try
				{
					toWK.CheckPhysicsTable();
					toWK.DirectUpdate();
				}
				catch (RuntimeException ex1)
				{
					Log.DebugWriteError(bp.wf.Glo.multilingual("@保存工作出错! 详情:{0}.", "WorkNode", "error_saving_data", ex1.getMessage()));
					throw new RuntimeException(bp.wf.Glo.multilingual("@保存工作出错! 详情:{0}.", "WorkNode", "error_saving_data", toWK.getEnDesc() + ex1.getMessage()));
				}
			}
		}


		///#endregion 保存目标节点数据.

		//@加入消息集合里。
		this.SendMsgToThem(current_gwls);

		if (toND.getItIsGuestNode() == true)
		{
			String htmlInfo = bp.wf.Glo.multilingual("@发送给如下{0}位处理人{1}.", "WorkNode", "send_to_the_following_operators", String.valueOf(this.HisRememberMe.getNumOfObjs()), this.getHisGenerWorkFlow().getGuestNo() + " " + this.getHisGenerWorkFlow().getGuestName());
			String textInfo = bp.wf.Glo.multilingual("@发送给如下{0}位处理人{1}.", "WorkNode", "send_to_the_following_operators", String.valueOf(this.HisRememberMe.getNumOfObjs()), this.getHisGenerWorkFlow().getGuestName());
			this.addMsg(SendReturnMsgFlag.ToEmps, textInfo, htmlInfo);
		}
		else
		{
			String htmlInfo = bp.wf.Glo.multilingual("@发送给如下{0}位处理人{1}.", "WorkNode", "send_to_the_following_operators", String.valueOf(this.HisRememberMe.getNumOfObjs()), this.HisRememberMe.getEmpsExt());
			String textInfo = bp.wf.Glo.multilingual("@发送给如下{0}位处理人{1}.", "WorkNode", "send_to_the_following_operators", String.valueOf(this.HisRememberMe.getNumOfObjs()), this.HisRememberMe.getObjsExt());
			this.addMsg(SendReturnMsgFlag.ToEmps, textInfo, htmlInfo);
		}


		///#region 处理审核问题,更新审核组件插入的审核意见中的 到节点，到人员。
		Paras ps = new Paras();
		try
		{
			ps.SQL = "UPDATE ND" + Integer.parseInt(toND.getFlowNo()) + "Track SET NDTo=" + dbStr + "NDTo,NDToT=" + dbStr + "NDToT,EmpTo=" + dbStr + "EmpTo,EmpToT=" + dbStr + "EmpToT WHERE NDFrom=" + dbStr + "NDFrom AND EmpFrom=" + dbStr + "EmpFrom AND WorkID=" + dbStr + "WorkID AND ActionType=" + ActionType.WorkCheck.getValue();

			ps.Add(TrackAttr.NDTo, this.getHisNode().getNodeID());
			ps.Add(TrackAttr.NDToT, this.getHisNode().getName(), false);
			ps.Add(TrackAttr.EmpTo, HisRememberMe.getEmpNoStr(), false);
			ps.Add(TrackAttr.EmpToT, HisRememberMe.getEmpNameStr(), false);
			ps.Add(TrackAttr.NDFrom, this.getHisNode().getNodeID());
			ps.Add(TrackAttr.EmpFrom, getWebUser().No, false);
			ps.Add(TrackAttr.WorkID, this.getWorkID());
			DBAccess.RunSQL(ps);
		}
		catch (RuntimeException ex)
		{

			try
			{

				///#region  如果更新失败，可能是由于数据字段大小引起。
				Flow flow = new Flow(toND.getFlowNo());

				String updateLengthSql = String.format("  alter table %1$s alter column %2$s varchar(2000) ", "ND" + Integer.parseInt(toND.getFlowNo()) + "Track", "EmpFromT");
				DBAccess.RunSQL(updateLengthSql);

				updateLengthSql = String.format("  alter table %1$s alter column %2$s varchar(2000) ", "ND" + Integer.parseInt(toND.getFlowNo()) + "Track", "EmpFrom");
				DBAccess.RunSQL(updateLengthSql);

				updateLengthSql = String.format("  alter table %1$s alter column %2$s varchar(2000) ", "ND" + Integer.parseInt(toND.getFlowNo()) + "Track", "EmpTo");
				DBAccess.RunSQL(updateLengthSql);
				updateLengthSql = String.format("  alter table %1$s alter column %2$s varchar(2000) ", "ND" + Integer.parseInt(toND.getFlowNo()) + "Track", "EmpToT");
				DBAccess.RunSQL(updateLengthSql);

				DBAccess.RunSQL(ps);

				///#endregion
			}
			catch (RuntimeException myex)
			{
				throw new RuntimeException("err@处理track表出现错误:" + myex.getMessage());
			}
		}

		///#endregion 处理审核问题.

		//String htmlInfo = string.Format("@任务自动发送给{0}如下处理人{1}.", this.nextStationName,this._RememberMe.EmpsExt);
		//String textInfo = string.Format("@任务自动发送给{0}如下处理人{1}.", this.nextStationName,this._RememberMe.ObjsExt);


		/*if (this.HisWorkerLists.size() >= 2 && this.getHisNode().getItIsTask())
		{
			//Img 的路径问题.
			this.addMsg(SendReturnMsgFlag.AllotTask, null, "<a href='./WorkOpt/AllotTask.htm?WorkID=" + this.getWorkID() + "&FK_Node=" + toND.getNodeID() + "&FK_Flow=" + toND.getFlowNo() + "'  target=_self><img src='" + SystemConfig.getHostURLOfBS() + "/WF/Img/AllotTask.gif' border=0/>指定特定的处理人处理</a>。", SendReturnMsgType.Info);
		}


		if (this.getHisNode().getHisFormType() != NodeFormType.SDKForm && this.getHisNode().getHisCancelRole() != CancelRole.None)
		{
			if (this.getHisNode().getItIsStartNode())
			{
				this.addMsg(SendReturnMsgFlag.ToEmpExt, null, "@<a href='./WorkOpt/UnSend.htm?DoType=UnSend&UserNo=" + getWebUser().getNo() + "&Token=" + getWebUser().Token + "&WorkID=" + this.getHisWork().getOID() + "&FK_Flow=" + toND.getFlowNo() + "' ><img src='" + SystemConfig.getHostURLOfBS() + "/WF/Img/Action/UnSend.png' style=display:inline border=0/>撤销本次发送</a>", SendReturnMsgType.Info);
			}
			else
			{
				this.addMsg(SendReturnMsgFlag.ToEmpExt, null, "@<a href='./WorkOpt/UnSend.htm?DoType=UnSend&UserNo=" + getWebUser().getNo() + "&Token=" + getWebUser().Token + "&WorkID=" + this.getHisWork().getOID() + "&FK_Flow=" + toND.getFlowNo() + "' ><img src='" + SystemConfig.getHostURLOfBS() + "/WF/Img/Action/UnSend.png' style=display:inline border=0 />撤销本次发送</a> ", SendReturnMsgType.Info);
			}
		}*/

		this.getHisGenerWorkFlow().setNodeID(toND.getNodeID());
		this.getHisGenerWorkFlow().setNodeName(toND.getName());
		this.getHisGenerWorkFlow().SetPara("ThreadCount", 0);
		String str1 = bp.wf.Glo.multilingual("@下一步工作[{0}]成功启动.", "WorkNode", "start_next_node_work_success", toND.getName());
		String str2 = bp.wf.Glo.multilingual("@下一步工作<font color=blue>[{0}]</font>成功启动.", "WorkNode", "start_next_node_work_success", toND.getName());

		this.addMsg(SendReturnMsgFlag.WorkStartNode, str1, str2);

		//this.addMsg(SendReturnMsgFlag.WorkStartNode, Glo.Multilingual("WorkNode","WorkStartNode",toND.getName()), "WorkStartNode1");


		///#endregion


		///#region  初始化发起的工作节点。
		if (this.getHisWork().getEnMap().getPhysicsTable().equals(toWK.getEnMap().getPhysicsTable()) == false)
		{
			/* 如果两个数据源不想等，就执行copy。 */
			this.CopyData(toWK, toND, false);
		}
		///#endregion 初始化发起的工作节点.
		///#region 判断是否是质量评价。
		if (toND.getItIsEval())
		{
			/*如果是质量评价流程*/
			toWK.SetValByKey(WorkSysFieldAttr.EvalEmpNo, this.getExecer());
			toWK.SetValByKey(WorkSysFieldAttr.EvalEmpName, this.getExecerName());
			toWK.SetValByKey(WorkSysFieldAttr.EvalCent, 0);
			toWK.SetValByKey(WorkSysFieldAttr.EvalNote, "");
		}

		///#endregion

	}

	/**
	 处理分流点向下发送 to 异表单.
	 @return
	 */
	private void NodeSend_24_UnSameSheet(Nodes toNDs) throws Exception {
		//NodeSend_2X_GenerFH();

		/*分别启动每个节点的信息.*/
		String msg = "";

		//定义系统变量.
		String workIDs = "";
		String empIDs = "";
		String empNames = "";
		String toNodeIDs = "";

		/*
		 * for:计算中心.
		 * 1. 首先要查询出来到达的节点是否有历史数据?
		 * 2. 产生历史数据的有 子线程的退回，撤销发送.
		 */

		GenerWorkFlows gwfThreads = new GenerWorkFlows();
		gwfThreads.Retrieve(GenerWorkFlowAttr.FID, this.getWorkID(), null);

		String msg_str = "";
		for (Node nd : toNDs.ToJavaList())
		{
			//删除垃圾数据.
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkerList WHERE FK_Node=" + nd.getNodeID() + " AND FID=" + this.getWorkID());

			long workIDSubThread = 0;

			FrmNodes fns = new FrmNodes();
			fns.Retrieve(FrmNodeAttr.FK_Node, nd.getNodeID(), null);


			///#region 如果生成workid的规则模式为0,  异表单子线程WorkID生成规则  UnSameSheetWorkIDModel  0= 仅生成一个WorkID,  1=按接受人生成WorkID,
			if (nd.getUSSWorkIDRole() == 0)
			{
				boolean isNew = true;
				GenerWorkFlow gwf = new GenerWorkFlow();
				for (GenerWorkFlow gwfT : gwfThreads.ToJavaList())
				{
					if (gwfT.getNodeID() == nd.getNodeID())
					{
						gwf = gwfT; //设置.
						workIDSubThread = gwfT.getWorkID();
						isNew = false;
						break;
					}
				}
				if (workIDSubThread == 0)
				{
					if (SystemConfig.GetValByKeyInt("GenerWorkIDModel", 0) == 0)
					{
						workIDSubThread = DBAccess.GenerOID("WorkID");
					}
					else
					{
						workIDSubThread = DBAccess.GenerOIDByGUID();
					}
				}


				//产生一个工作信息。
				Work wk = nd.getHisWork();
				wk.Copy(this.rptGe);

				wk.setFID(this.getHisWork().getOID());
				wk.setOID(workIDSubThread);
				if (isNew == true)
				{
					wk.DirectInsert(); //执行插入.
				}
				else
				{
					wk.DirectUpdate(); //执行保存.
				}


				///#region 检查子线程是否有绑定的表单? 如果有就给它主表的数据.
				for (FrmNode fn : fns.ToJavaList())
				{
					//不是始终启用，就不给数据.
					if (fn.getFrmEnableRole() != FrmEnableRole.Allways)
					{
						continue;
					}

					GEEntityOID ge = new GEEntityOID(fn.getFrmID(),workIDSubThread);
					if (fn.getWhoIsPK() != WhoIsPK.FID)
					{
						//ge.setOID(this.getHisWork().getOID());
						ge.Copy(this.getHisWork());
						ge.setOID(workIDSubThread);
						ge.SetValByKey("FID",this.getHisWork().getOID());
						if (ge.getIsExits()==false)
							ge.InsertAsOID(ge.getOID());
						else
							ge.Update();
						wk.RetrieveFromDBSources();

					}
				}

				///#endregion 检查子线程是否有绑定的表单? 如果有就给它主表的数据.

				//获得它的工作者。
				WorkNode town = new WorkNode(wk, nd);
				current_gwls = this.Func_GenerWorkerLists_Thread(town);
				if (current_gwls == null)
				{
					//@yln 判断该节点是否存在跳转
					if (nd.getHisWhenNoWorker() == true)
					{

						//记录跳转信息
						this.AddToTrack(ActionType.Skip, getWebUser().No, getWebUser().getName(), nd.getNodeID(), nd.getName(), bp.wf.Glo.multilingual("自动跳转(启用跳转规则,当没有找到处理人时)", "WorkNode", "system_error_jump_automatically_1", new String[0]), nd);
						//没有找到人就跳转
						Node nextNode = this.NodeSend_GenerNextStepNode_Ext(nd);
						//判断当前节点是不是合流点
						if (nextNode.getHisRunModel() == RunModel.HL || nextNode.getHisRunModel() == RunModel.FHL)
						{
							//处理业务逻辑
							NodeSend_ToGenerWorkFlow(gwf, wk, nd, isNew, getWebUser().DeptNo, getWebUser().DeptName, getWebUser().getNo() + "," + getWebUser().getName());
							gwf.SetPara("FLNodeID", this.getHisNode().getNodeID());
							gwf.SetPara("ThreadCount", toNDs.size());
							gwf.Update();
							//发送到分流点
							SendReturnObjs returnObj = bp.wf.Dev2Interface.Node_SendWork(gwf.getFlowNo(), gwf.getWorkID());
							this.addMsg("Send", returnObj.ToMsgOfHtml());
							GenerWorkFlow maingwf = new GenerWorkFlow(this.getWorkID());
							this.setHisGenerWorkFlow(maingwf);
							continue;
						}
						town = new WorkNode(wk, nextNode);
						current_gwls = this.Func_GenerWorkerLists(town);
					}
					else
					{
						msg += bp.wf.Glo.multilingual("@没有找到节点[{0}]的处理人员,所以此节点无法成功启动.", "WorkNode", "not_found_node_operator", nd.getName());
						continue;
					}

				}


				///#region 生成待办.
				String operators = "";
				int i = 0;
				GenerWorkerList oneGWL = null; //获得这个变量，在gwf中使用.
				String todoEmps = "";
				for (GenerWorkerList wl : current_gwls.ToJavaList())
				{
					oneGWL = wl; //获得这个变量，在gwf中使用.

					i += 1;
					operators += wl.getEmpNo() + ", " + wl.getEmpName() + ";";
					empIDs += wl.getEmpNo() + ",";
					empNames += wl.getEmpName() + ",";

					ps = new Paras();
					ps.SQL = "UPDATE WF_GenerWorkerlist SET WorkID=" + dbStr + "WorkID1,FID=" + dbStr + "FID WHERE FK_Emp=" + dbStr + "FK_Emp AND WorkID=" + dbStr + "WorkID2 AND FK_Node=" + dbStr + "FK_Node ";
					ps.Add("WorkID1", wk.getOID());
					ps.Add("FID", this.getWorkID());

					ps.Add("FK_Emp", wl.getEmpNo(), false);
					ps.Add("WorkID2", wl.getWorkID());
					ps.Add("FK_Node", wl.getNodeID());
					DBAccess.RunSQL(ps);

					//设置当前的workid.
					wl.setWorkID(wk.getOID());


					//更新工作信息.
					wk.setRec(wl.getEmpNo());
					//wk.Emps = "@" + wl.EmpNo;
					//wk.setRDT(DataType.getCurrentDateTimess();
					wk.DirectUpdate();

					//为子线程产生分流节点的发送副本。
					wl.setFID(this.getWorkID());
					wl.setEmpNo(getWebUser().getNo());
					wl.setEmpName(getWebUser().getName());
					wl.setPassInt(-2);
					wl.setItIsRead(true);
					wl.setNodeID(this.getHisNode().getNodeID());
					wl.setNodeName(this.getHisNode().getName());
					wl.setDeptNo(getWebUser().DeptNo);
					wl.setDeptName(getWebUser().DeptName);
					if (wl.IsExits() == false)
					{
						wl.Insert();
					}
				}

				///#endregion 生成待办.


				///#region 生成子线程的GWF.
				gwf.setWorkID(wk.getOID());
				//干流、子线程关联字段
				gwf.setFID(this.getWorkID());

				//父流程关联字段
				gwf.setPWorkID(this.getHisGenerWorkFlow().getPWorkID());
				gwf.setPFlowNo(this.getHisGenerWorkFlow().getPFlowNo());
				gwf.setPNodeID(this.getHisGenerWorkFlow().getPNodeID());

				//工程类项目关联字段
				gwf.setPrjNo(this.getHisGenerWorkFlow().getPrjNo());
				gwf.setPrjName(this.getHisGenerWorkFlow().getPrjName());

				///#warning 需要修改成标题生成规则。
				///#warning 让子流程的Titlte与父流程的一样.

				gwf.setTitle(this.getHisGenerWorkFlow().getTitle()); // WorkNode.GenerTitle(this.rptGe);
				gwf.setWFState(WFState.Runing);
				gwf.setRDT(DataType.getCurrentDateTimess());
				gwf.setStarter(this.getHisGenerWorkFlow().getStarter());
				gwf.setStarterName(this.getHisGenerWorkFlow().getStarterName());
				gwf.setFlowNo(nd.getFlowNo());
				gwf.setFlowName(nd.getFlowName());
				gwf.setFlowSortNo(this.getHisNode().getHisFlow().getFlowSortNo());
				gwf.setSysType(this.getHisNode().getHisFlow().getSysType());

				gwf.setNodeID(nd.getNodeID());
				gwf.setNodeName(nd.getName());
				gwf.setDeptNo(oneGWL.getDeptNo());
				gwf.setDeptName(oneGWL.getDeptName());
				gwf.setTodoEmps(operators);
				gwf.setDomainExt(this.getHisGenerWorkFlow().getDomainExt()); //域.
				gwf.setSender(getWebUser().getNo() + "," + getWebUser().getName() +";");
				if (DataType.IsNullOrEmpty(this.getHisFlow().getBuessFields()) == false)
				{
					//存储到表里atPara  @BuessFields=电话^Tel^18992323232;地址^Addr^山东成都;
					String[] expFields = this.getHisFlow().getBuessFields().split("[,]", -1);
					String exp = "";
					Attrs attrs = this.rptGe.getEnMap().getAttrs();
					for (String item : expFields)
					{
						if (DataType.IsNullOrEmpty(item) == true)
						{
							continue;
						}
						if (attrs.contains(item) == false)
						{
							continue;
						}

						Attr attr = attrs.GetAttrByKey(item);
						exp += attr.getDesc() + "^" + attr.getKey() + "^" + this.rptGe.GetValStrByKey(item);
					}
					gwf.setBuessFields(exp); //表达式字段.
				}

				if (isNew == true)
				{
					gwf.DirectInsert();
				}
				else
				{
					gwf.DirectUpdate();
				}

				///#endregion 生成子线程的GWF.
				msg += bp.wf.Glo.multilingual("@节点[{0}]成功启动, 发送给{1}位处理人:{2}.", "WorkNode", "found_node_operator", nd.getName(), String.valueOf(i), operators);
			}

			///#endregion 如果生成workid的规则模式为0,  异表单子线程WorkID生成规则  UnSameSheetWorkIDModel  0= 仅生成一个WorkID,  1=按接受人生成WorkID,



			///#region 如果生成workid的规则模式为 1 ,  异表单子线程WorkID生成规则  UnSameSheetWorkIDModel  0= 仅生成一个WorkID,  1=按接受人生成WorkID,
			if (nd.getUSSWorkIDRole() == 1)
			{
				//产生一个工作信息。
				Work wk = nd.getHisWork();
				//  wk.Copy(this.HisWork);
				wk.Copy(this.rptGe);
				wk.setFID(this.getHisWork().getOID());

				//获得它的工作者。
				WorkNode town = new WorkNode(wk, nd);
				current_gwls = this.Func_GenerWorkerLists(town); //获得数量.
				if (current_gwls.size() == 0)
				{
					msg += bp.wf.Glo.multilingual("@没有找到节点[{0}]的处理人员,所以此节点无法成功启动.", "WorkNode", "not_found_node_operator", nd.getName());
					// wk.Delete();
					continue;
				}


				///#region 检查子线程是否有绑定的表单? 如果有就给它主表的数据.
				for (FrmNode fn : fns.ToJavaList())
				{
					//不是始终启用，就不给数据.
					if (fn.getFrmEnableRole() != FrmEnableRole.Allways)
					{
						continue;
					}

					GEEntityOID ge = new GEEntityOID(fn.getFrmID());
					if (fn.getWhoIsPK() != WhoIsPK.FID)
					{
						ge.setOID(this.getHisWork().getOID());
						ge.Copy(this.getHisWork());
						if (ge.getIsExits())
						{
							ge.InsertAsOID(ge.getOID());
						}
						else
						{
							ge.Update();
						}
					}
				}

				///#endregion 检查子线程是否有绑定的表单? 如果有就给它主表的数据.


				///#region 生成待办.
				String operators = "";
				int i = 0;
				GenerWorkerList oneGWL = null; //获得这个变量，在gwf中使用.
				String todoEmps = "";
				for (GenerWorkerList wl : current_gwls.ToJavaList())
				{
					if (SystemConfig.GetValByKeyInt("GenerWorkIDModel", 0) == 0)
					{
						workIDSubThread = DBAccess.GenerOID("WorkID");
					}
					else
					{
						workIDSubThread = DBAccess.GenerOIDByGUID();
					}

					GenerWorkFlow gwf = new GenerWorkFlow();
					gwf.setWorkID(workIDSubThread);
					gwf.setFID(this.getWorkID());

					wk.setOID(workIDSubThread);
					wk.DirectInsert(); //执行插入.

					oneGWL = wl; //获得这个变量，在gwf中使用.

					i += 1;
					operators += wl.getEmpNo() + ", " + wl.getEmpName() + ";";
					empIDs += wl.getEmpNo() + ",";
					empNames += wl.getEmpName() + ",";

					ps = new Paras();
					ps.SQL = "UPDATE WF_GenerWorkerlist SET WorkID=" + dbStr + "WorkID1,FID=" + dbStr + "FID WHERE FK_Emp=" + dbStr + "FK_Emp AND WorkID=" + dbStr + "WorkID2 AND FK_Node=" + dbStr + "FK_Node ";
					ps.Add("WorkID1", wk.getOID());
					ps.Add("FID", this.getWorkID());

					ps.Add("FK_Emp", wl.getEmpNo(), false);
					ps.Add("WorkID2", wl.getWorkID());
					ps.Add("FK_Node", wl.getNodeID());
					DBAccess.RunSQL(ps);

					//设置当前的workid.
					wl.setWorkID(wk.getOID());

					//更新工作信息.
					wk.setRec(wl.getEmpNo());

					//为子线程产生分流节点的发送副本。
					wl.setFID(this.getWorkID());
					wl.setEmpNo(getWebUser().getNo());
					wl.setEmpName(getWebUser().getName());
					wl.setPassInt(-2);
					wl.setItIsRead(true);
					wl.setNodeID(this.getHisNode().getNodeID());
					wl.setNodeName(this.getHisNode().getName());
					wl.setDeptNo(getWebUser().DeptNo);
					wl.setDeptName(getWebUser().DeptName);
					if (wl.IsExits() == false)
					{
						wl.Insert();
					}

					gwf.setWorkID(wk.getOID());
					//干流、子线程关联字段
					gwf.setFID(this.getWorkID());

					//父流程关联字段
					gwf.setPWorkID(this.getHisGenerWorkFlow().getPWorkID());
					gwf.setPFlowNo(this.getHisGenerWorkFlow().getPFlowNo());
					gwf.setPNodeID(this.getHisGenerWorkFlow().getPNodeID());

					//工程类项目关联字段
					gwf.setPrjNo(this.getHisGenerWorkFlow().getPrjNo());
					gwf.setPrjName(this.getHisGenerWorkFlow().getPrjName());

					///#warning 需要修改成标题生成规则。
					///#warning 让子流程的Titlte与父流程的一样.

					gwf.setTitle(this.getHisGenerWorkFlow().getTitle()); // WorkNode.GenerTitle(this.rptGe);
					gwf.setWFState(WFState.Runing);
					gwf.setRDT(DataType.getCurrentDateTimess());
					gwf.setStarter(this.getHisGenerWorkFlow().getStarter());
					gwf.setStarterName(this.getHisGenerWorkFlow().getStarterName());
					gwf.setFlowNo(nd.getFlowNo());
					gwf.setFlowName(nd.getFlowName());
					gwf.setFlowSortNo(this.getHisNode().getHisFlow().getFlowSortNo());
					gwf.setSysType(this.getHisNode().getHisFlow().getSysType());

					gwf.setNodeID(nd.getNodeID());
					gwf.setNodeName(nd.getName());
					gwf.setDeptNo(oneGWL.getDeptNo());
					gwf.setDeptName(oneGWL.getDeptName());
					gwf.setTodoEmps(wl.getEmpNo() + ", " + wl.getEmpName() + ";");
					gwf.setDomainExt(this.getHisGenerWorkFlow().getDomainExt()); //域.
					gwf.setSender(getWebUser().getNo() + "," + getWebUser().getName() +";");
					if (DataType.IsNullOrEmpty(this.getHisFlow().getBuessFields()) == false)
					{
						//存储到表里atPara  @BuessFields=电话^Tel^18992323232;地址^Addr^山东成都;
						String[] expFields = this.getHisFlow().getBuessFields().split("[,]", -1);
						String exp = "";
						Attrs attrs = this.rptGe.getEnMap().getAttrs();
						for (String item : expFields)
						{
							if (DataType.IsNullOrEmpty(item) == true)
							{
								continue;
							}
							if (attrs.contains(item) == false)
							{
								continue;
							}

							Attr attr = attrs.GetAttrByKey(item);
							exp += attr.getDesc() + "^" + attr.getKey() + "^" + this.rptGe.GetValStrByKey(item);
						}
						gwf.setBuessFields(exp); //表达式字段.
					}

					gwf.Insert(); //插入数据.
				}

				///#endregion 生成待办.

				msg += bp.wf.Glo.multilingual("@节点[{0}]成功启动, 发送给{1}位处理人:{2}.", "WorkNode", "found_node_operator", nd.getName(), String.valueOf(i), operators);
			}

			///#endregion 如果生成workid的规则模式为0,  异表单子线程WorkID生成规则  UnSameSheetWorkIDModel  0= 仅生成一个WorkID,  1=按接受人生成WorkID,
		}

		//加入分流异表单，提示信息。
		this.addMsg("FenLiuUnSameSheet", msg);

		//加入变量。
		this.addMsg(SendReturnMsgFlag.VarTreadWorkIDs, workIDs, workIDs, SendReturnMsgType.SystemMsg);
		this.addMsg(SendReturnMsgFlag.VarAcceptersID, empIDs, empIDs, SendReturnMsgType.SystemMsg);
		this.addMsg(SendReturnMsgFlag.VarAcceptersName, empNames, empNames, SendReturnMsgType.SystemMsg);
		this.addMsg(SendReturnMsgFlag.VarToNodeIDs, toNodeIDs, toNodeIDs, SendReturnMsgType.SystemMsg);

		//写入日志.
		if (this.getHisNode().getItIsStartNode() == true)
		{
			this.AddToTrack(ActionType.Start, empIDs, empNames, this.getHisNode().getNodeID(), this.getHisNode().getName(), msg);
		}
		else
		{
			this.AddToTrack(ActionType.Forward, empIDs, empNames, this.getHisNode().getNodeID(), this.getHisNode().getName(), msg);
		}

	}

	private void NodeSend_ToGenerWorkFlow(GenerWorkFlow gwf, Work wk, Node nd, boolean isNew, String deptNo, String deptName, String todoEmps) throws Exception {
		gwf.setWorkID(wk.getOID());
		//干流、子线程关联字段
		gwf.setFID(this.getWorkID());

		//父流程关联字段
		gwf.setPWorkID(this.getHisGenerWorkFlow().getPWorkID());
		gwf.setPFlowNo(this.getHisGenerWorkFlow().getPFlowNo());
		gwf.setPNodeID(this.getHisGenerWorkFlow().getPNodeID());

		//工程类项目关联字段
		gwf.setPrjNo(this.getHisGenerWorkFlow().getPrjNo());
		gwf.setPrjName(this.getHisGenerWorkFlow().getPrjName());

		/**#warning 需要修改成标题生成规则。
		 #warning 让子流程的Titlte与父流程的一样.
		 */

		gwf.setTitle(this.getHisGenerWorkFlow().getTitle()); // WorkNode.GenerTitle(this.rptGe);
		gwf.setWFState(WFState.Runing);
		gwf.setRDT(DataType.getCurrentDateTimess());
		gwf.setStarter(this.getHisGenerWorkFlow().getStarter());
		gwf.setStarterName(this.getHisGenerWorkFlow().getStarterName());
		gwf.setFlowNo(nd.getFlowNo());
		gwf.setFlowName(nd.getFlowName());
		gwf.setFlowSortNo(this.getHisNode().getHisFlow().getFlowSortNo());
		gwf.setSysType(this.getHisNode().getHisFlow().getSysType());

		gwf.setNodeID(nd.getNodeID());
		gwf.setNodeName(nd.getName());
		gwf.setDeptNo(deptNo);
		gwf.setDeptName(deptName);
		gwf.setTodoEmps(todoEmps);
		gwf.setDomainExt(this.getHisGenerWorkFlow().getDomainExt()); //域.
		gwf.setSender(getWebUser().getNo() + "," + getWebUser().getName() +";");
		if (DataType.IsNullOrEmpty(this.getHisFlow().getBuessFields()) == false)

		{
			//存储到表里atPara  @BuessFields=电话^Tel^18992323232;地址^Addr^山东成都;
			String[] expFields = this.getHisFlow().getBuessFields().split("[,]", -1);
			String exp = "";
			Attrs attrs = this.rptGe.getEnMap().getAttrs();
			for (String item : expFields)
			{
				if (DataType.IsNullOrEmpty(item) == true)
				{
					continue;
				}
				if (attrs.contains(item) == false)
				{
					continue;
				}

				Attr attr = attrs.GetAttrByKey(item);
				exp += attr.getDesc() + "^" + attr.getKey() + "^" + this.rptGe.GetValStrByKey(item);
			}
			gwf.setBuessFields(exp); //表达式字段.
		}

		if (isNew == true)
		{
			try {
				gwf.DirectInsert();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		else
		{
			gwf.DirectUpdate();
		}

	}

	/**
	 产生分流点

	 @param toWN
	 @return
	 */
	private GenerWorkerLists NodeSend_24_SameSheet_GenerWorkerList(WorkNode toWN)
	{
		return null;
	}
	/**
	 当前产生的接收人员列表.
	 */
	private GenerWorkerLists current_gwls = null;
	/**
	 处理分流点向下发送 to 同表单.

	 @param toNode 到达的分流节点
	 */
	private void NodeSend_24_SameSheet(Node toNode) throws Exception {
		if (Objects.equals(this.getHisGenerWorkFlow().getTitle(), bp.wf.Glo.multilingual("未生成", "WorkNode", "not_generated")))
		{
			this.getHisGenerWorkFlow().setTitle(bp.wf.WorkFlowBuessRole.GenerTitle(this.getHisFlow(), this.getHisWork()));
		}



		///#region 产生下一步骤的工作人员
		// 发起.
		Work wk = toNode.getHisWork();
		wk.Copy(this.rptGe);
		wk.Copy(this.getHisWork()); //复制过来主表基础信息。
		wk.setFID(this.getHisWork().getOID()); // 把该工作FID设置成干流程上的工作ID.

		// 到达的节点.
		town = new WorkNode(wk, toNode);

		// 产生下一步骤要执行的人员.
		current_gwls = this.Func_GenerWorkerLists(town);

		//删除当前工作人员给每个子线程增加的GenerWorkerlist数据
		//current_gwls.Delete(GenerWorkerListAttr.FK_Node, this.Hisnode.getNodeID(), GenerWorkerListAttr.FID, this.getWorkID()); //首先清除.

		//判断当前节点是否存在分合流信息（退回的原因）。
		boolean IsHaveFH = false;
		ps = new Paras();
		ps.SQL = "SELECT COUNT(WorkID) FROM WF_GenerWorkerlist WHERE FID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node";
		ps.Add("OID", this.getHisWork().getOID());
		ps.Add("FK_Node", toNode.getNodeID());
		if (DBAccess.RunSQLReturnValInt(ps) != 0)
		{
			IsHaveFH = true;
		}


		///#endregion 产生下一步骤的工作人员


		///#region 复制数据.
		MapDtls dtlsFrom = new MapDtls("ND" + this.getHisNode().getNodeID());

		/**定义系统变量.
		 */
		String workIDs = "";

		DataTable dtWork = null;
		if (toNode.getHisDeliveryWay() == DeliveryWay.BySQLAsSubThreadEmpsAndData)
		{
			/*如果是按照查询SQL，确定明细表的接收人与子线程的数据。*/
			String sql = toNode.getDeliveryParas();
			sql = bp.wf.Glo.DealExp(sql, this.getHisWork());
			dtWork = DBAccess.RunSQLReturnTable(sql);
		}
		if (toNode.getHisDeliveryWay() == DeliveryWay.ByDtlAsSubThreadEmps)
		{
			/*如果是按照明细表，确定明细表的接收人与子线程的数据。*/
			for (MapDtl dtl : dtlsFrom.ToJavaList())
			{
				//加上顺序，防止变化，人员编号变化，处理明细表中接收人重复的问题。
				String sql = "SELECT * FROM " + dtl.getPTable() + " WHERE RefPK=" + this.getWorkID() + " ORDER BY OID";
				dtWork = DBAccess.RunSQLReturnTable(sql);
				if (dtWork.Columns.contains("UserNo") || dtWork.Columns.contains(toNode.getDeliveryParas()))
				{
					break;
				}
				else
				{
					dtWork = null;
				}
			}
		}

		String groupMark = "";
		int idx = -1;
		for (GenerWorkerList wl : current_gwls.ToJavaList())
		{
			if (this.ItIsHaveSubThreadGroupMark == true)
			{
				/*如果启用了批次处理,子线程的问题..*/
				if (groupMark.contains("@" + wl.getEmpNo() + "," + wl.getGroupMark()) == false)
				{
					groupMark += "@" + wl.getEmpNo() + "," + wl.getGroupMark();
				}
				else
				{
					wl.Delete(); //删除该条垃圾数据.
					continue;
				}
			}

			idx++;
			Work mywk = toNode.getHisWork();

			mywk.Copy(this.rptGe);

			///#region 复制从表数据到子线程主表中
			//拷贝SQL查询字段的结果数据
			if (dtWork != null)
			{
				/*用IDX处理是为了解决，人员重复出现在数据源并且还能根据索引对应的上。*/
				DataRow dr = dtWork.Rows.get(idx);
				if ((dtWork.Columns.contains("UserNo") && dr.getValue("UserNo").toString().equals(wl.getEmpNo())) || (dtWork.Columns.contains("No") && dr.getValue("No").toString().equals(wl.getEmpNo())) || dtWork.Columns.contains(toNode.getDeliveryParas()) && dr.getValue(toNode.getDeliveryParas()).toString().equals(wl.getEmpNo()))
				{
					mywk.Copy(dr);
				}
			}

			///#endregion 复制从表数据到子线程主表中



			//是否是分组工作流程, 定义变量是为了，不让其在重复插入work数据。
			boolean isGroupMarkWorklist = false;
			boolean isHaveEmp = false;
			if (IsHaveFH)
			{
				/* 如果曾经走过分流合流，就找到同一个人员同一个FID下的OID ，做这当前线程的ID。*/
				ps = new Paras();
				ps.SQL = "SELECT WorkID,FK_Node FROM WF_GenerWorkerlist WHERE FK_Node=" + dbStr + "FK_Node AND FID=" + dbStr + "FID AND FK_Emp=" + dbStr + "FK_Emp AND WorkID!=" + dbStr + "WorkID ORDER BY RDT DESC";
				ps.Add("FK_Node", toNode.getNodeID());
				ps.Add("FID", this.getWorkID());
				ps.Add("FK_Emp", wl.getEmpNo(), false);
				ps.Add("WorkID", wl.getWorkID());
				DataTable dt = DBAccess.RunSQLReturnTable(ps);
				if (dt.Rows.size() == 0)
				{
					/*没有发现，就说明以前分流节点中没有这个人的分流信息. */
					if (SystemConfig.GetValByKeyInt("GenerWorkIDModel", 0) == 0)
					{
						mywk.setOID(DBAccess.GenerOID("WorkID"));
					}
					else
					{
						mywk.setOID(DBAccess.GenerOIDByGUID());
					}
				}
				else
				{
					int workid_old = (int)dt.Rows.get(0).getValue(0);
					int fk_Node_nearly = (int)dt.Rows.get(0).getValue(1);
					Node nd_nearly = new Node(fk_Node_nearly);
					Work nd_nearly_work = nd_nearly.getHisWork();
					nd_nearly_work.setOID(workid_old);
					if (nd_nearly_work.RetrieveFromDBSources() != 0)
					{
						mywk.Copy(nd_nearly_work);
						mywk.setOID(workid_old);
						isHaveEmp = true;
					}
					else
					{
						if (SystemConfig.GetValByKeyInt("GenerWorkIDModel", 0) == 0)
						{
							mywk.setOID(DBAccess.GenerOID("WorkID"));
						}
						else
						{
							mywk.setOID(DBAccess.GenerOIDByGUID());
						}
					}

				}
			}
			else
			{
				//为子线程产生WorkID.
				/* edit by zhoupeng 2015.12.24 平安夜. 处理国机的需求，判断是否有分组的情况，如果有就要找到分组的workid
				 * 让其同一个分组，只能生成一个workid。
				 * */
				if (this.ItIsHaveSubThreadGroupMark == true)
				{
					//查询该GroupMark 是否已经注册到流程引擎主表里了.
					String sql = "SELECT WorkID FROM WF_GenerWorkFlow WHERE AtPara LIKE '%GroupMark=" + wl.getGroupMark() + "%' AND FID=" + this.getWorkID();
					DataTable dt = DBAccess.RunSQLReturnTable(sql);
					if (dt.Rows.size() == 0)
					{
						if (SystemConfig.GetValByKeyInt("GenerWorkIDModel", 0) == 0)
						{
							mywk.setOID(DBAccess.GenerOID("WorkID"));
						}
						else
						{
							mywk.setOID(DBAccess.GenerOIDByGUID());
						}
					}
					else
					{
						mywk.setOID(Long.parseLong(dt.Rows.get(0).getValue(0).toString())); //使用该分组下的，已经注册过的分组的WorkID，而非产生一个新的WorkID。
						isGroupMarkWorklist = true; //是分组数据，让其work 就不要在重复插入了.
					}
				}
				else
				{
					if (SystemConfig.GetValByKeyInt("GenerWorkIDModel", 0) == 0)
					{
						mywk.setOID(DBAccess.GenerOID("WorkID"));
					}
					else
					{
						mywk.setOID(DBAccess.GenerOIDByGUID()); //DBAccess.GenerOID();
					}
				}
			}

			//非分组工作人员.
			if (isGroupMarkWorklist == false)
			{
				if (this.getHisWork().getFID() == 0)
				{
					mywk.setFID(this.getHisWork().getOID());
				}

				mywk.setRec(wl.getEmpNo());

				//判断是不是MD5流程？
				if (this.getHisFlow().getItIsMD5())
				{
					mywk.SetValByKey("MD5", Glo.GenerMD5(mywk));
				}
			}


			//非分组工作人员.
			if (isGroupMarkWorklist == false)
			{
				//保存主表数据.
				if (isHaveEmp == true)
				{
					mywk.Update();
				}
				else
				{
					mywk.InsertAsOID(mywk.getOID());
				}
				//给系统变量赋值，放在发送后返回对象里.
				workIDs += mywk.getOID() + ",";

				//复制数据
				SendToSameSheet_CopyData(toNode, mywk, dtlsFrom);
			}


			///#region (循环最后处理)产生工作的信息
			// 产生工作的信息。
			GenerWorkFlow gwf = new GenerWorkFlow();
			gwf.setWorkID(mywk.getOID());
			if (gwf.RetrieveFromDBSources() == 0)
			{
				gwf.setFID(this.getWorkID());
				gwf.setNodeID(toNode.getNodeID());

				if (this.getHisNode().getItIsStartNode())
				{
					gwf.setTitle(bp.wf.WorkFlowBuessRole.GenerTitle(this.getHisFlow(), this.getHisWork()) + "(" + wl.getEmpName() + ")");
				}
				else
				{
					gwf.setTitle(this.getHisGenerWorkFlow().getTitle() + "(" + wl.getEmpName() + ")");
				}

				gwf.setWFState(WFState.Runing);
				gwf.setRDT(DataType.getCurrentDateTimess());
				gwf.setStarter(this.getExecer());
				gwf.setStarterName(this.getExecerName());
				gwf.setFlowNo(toNode.getFlowNo());
				gwf.setFlowName(toNode.getFlowName());

				//干流、子线程关联字段
				gwf.setFID(this.getWorkID());

				//父流程关联字段
				gwf.setPWorkID(this.getHisGenerWorkFlow().getPWorkID());
				gwf.setPFlowNo(this.getHisGenerWorkFlow().getPFlowNo());
				gwf.setPNodeID(this.getHisGenerWorkFlow().getPNodeID());

				//域.
				gwf.setDomainExt(this.getHisGenerWorkFlow().getDomainExt());


				//工程类项目关联字段
				gwf.setPrjNo(this.getHisGenerWorkFlow().getPrjNo());
				gwf.setPrjName(this.getHisGenerWorkFlow().getPrjName());

				gwf.setFlowSortNo(toNode.getHisFlow().getFlowSortNo());
				gwf.setNodeName(toNode.getName());
				gwf.setDeptNo(wl.getDeptNo());
				gwf.setDeptName(wl.getDeptName());
				gwf.setTodoEmps(wl.getEmpNo() + "," + wl.getEmpName() + ";");
				if (!Objects.equals(wl.getGroupMark(), ""))
				{
					gwf.setParasGroupMark(wl.getGroupMark());
				}

				gwf.setSender(getWebUser().getNo() + "," + getWebUser().getName() +";");

				if (DataType.IsNullOrEmpty(this.getHisFlow().getBuessFields()) == false)
				{
					//存储到表里atPara  @BuessFields=电话^Tel^18992323232;地址^Addr^山东成都;
					String[] expFields = this.getHisFlow().getBuessFields().split("[,]", -1);
					String exp = "";
					Attrs attrs = this.rptGe.getEnMap().getAttrs();
					for (String item : expFields)
					{
						if (DataType.IsNullOrEmpty(item) == true)
						{
							continue;
						}
						if (attrs.contains(item) == false)
						{
							continue;
						}

						Attr attr = attrs.GetAttrByKey(item);
						exp += attr.getDesc() + "^" + attr.getKey() + "^" + this.rptGe.GetValStrByKey(item);
					}
					gwf.setBuessFields(exp);
				}

				gwf.DirectInsert();
			}
			else
			{
				if (!Objects.equals(wl.getGroupMark(), ""))
				{
					gwf.setParasGroupMark(wl.getGroupMark());
				}

				gwf.setWFState(WFState.Runing);
				gwf.setSender(getWebUser().getNo() + "," + getWebUser().getName() +";");
				gwf.setNodeID(toNode.getNodeID());
				gwf.setNodeName(toNode.getName());
				gwf.Update();
			}


			// 插入当前分流节点的处理人员,让其可以在在途里看到工作.
			//非分组工作人员.
			if (isGroupMarkWorklist == false)
			{
				GenerWorkerList flGwl = new GenerWorkerList();
				flGwl.Copy(wl);
				if (isHaveEmp == true)
				{
					flGwl.setWorkID(mywk.getOID());
				}
				flGwl.setEmpNo(getWebUser().getNo());
				flGwl.setEmpName(getWebUser().getName());
				flGwl.setNodeID(this.getHisNode().getNodeID());
				flGwl.setSender(getWebUser().getNo() + "," + getWebUser().getName());
				//flGwl.setDeptNo(WebUser.getDeptNo();
				//flGwl.setDeptName(WebUser.getDeptName();
				flGwl.setPassInt(-2); // -2; //标志该节点是干流程人员处理的节点.
				//  wl.setFID(0; //如果是干流，
				flGwl.Save();
			}

			//把临时的workid 更新到.
			if (isHaveEmp == false)
			{
				ps = new Paras();
				ps.SQL = "UPDATE WF_GenerWorkerlist SET WorkID=" + dbStr + "WorkID1 WHERE WorkID=" + dbStr + "WorkID2";
				ps.Add("WorkID1", mywk.getOID());
				ps.Add("WorkID2", wl.getWorkID()); //临时的ID,更新最新的workid.
				int num = DBAccess.RunSQL(ps);
				if (num == 0)
				{
					throw new RuntimeException("@不应该更新不到它。");
				}
			}
			else
			{
				//修改状态，不是0或者1改成0
				ps = new Paras();
				ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE WorkID=" + dbStr + "WorkID AND FK_Emp=" + dbStr + "FK_Emp AND FK_Node=" + dbStr + "FK_Node AND IsPass NOT IN(0,1)";
				ps.Add("WorkID", mywk.getOID());
				ps.Add("FK_Emp", wl.getEmpNo(), false);
				ps.Add("FK_Node", wl.getNodeID());
				int num = DBAccess.RunSQL(ps);
				wl.Delete();
			}


			//设置当前的workid. 临时的id有变化.
			wl.setWorkID(mywk.getOID());

			///#endregion 产生工作的信息.
		}

		///#endregion 复制数据.


		///#region 处理消息提示
		String info = bp.wf.Glo.multilingual("@分流节点[{0}]成功启动, 发送给{1}位处理人:{2}.", "WorkNode", "found_node_operator", toNode.getName(), String.valueOf(this.HisRememberMe.getNumOfObjs()), this.HisRememberMe.getEmpsExt());

		this.addMsg("FenLiuInfo", info);

		//把子线程的 WorkIDs 加入系统变量.
		this.addMsg(SendReturnMsgFlag.VarTreadWorkIDs, workIDs, workIDs, SendReturnMsgType.SystemMsg);

		// 如果是开始节点，就可以允许选择接收人。
		/*if (this.getHisNode().getItIsStartNode() && current_gwls.size() >= 2 && this.getHisNode().getItIsTask())
		{
			this.addMsg("AllotTask", "@<img src='./Img/AllotTask.gif' border=0 /><a href='./WorkOpt/AllotTask.htm?WorkID=" + this.getWorkID() + "&FID=" + this.getWorkID() + "&NodeID=" + toNode.getNodeID() + "' target=_self >修改接收对象</a>.");
		}

		if (this.getHisNode().getHisCancelRole() != CancelRole.None)
		{
			if (this.getHisNode().getItIsStartNode())
			{
				this.addMsg("UnDo", "@<a href='./WorkOpt/UnSend.htm?DoType=UnSend&UserNo=" + getWebUser().getNo() + "&Token=" + getWebUser().Token + "&WorkID=" + this.getWorkID() + "&FK_Flow=" + toNode.getFlowNo() + "' ><img src='" + SystemConfig.getHostURLOfBS() + "/WF/Img/Action/UnSend.png' style=display:inline border=0/>撤销本次发送</a>.");
			}
			else
			{
				this.addMsg("UnDo", "@<a href='./WorkOpt/UnSend.htm?DoType=UnSend&UserNo=" + getWebUser().getNo() + "&Token=" + getWebUser().Token + "&WorkID=" + this.getWorkID() + "&FK_Flow=" + toNode.getFlowNo() + "' ><img src='" + SystemConfig.getHostURLOfBS() + "/WF/Img/Action/UnSend.png' style=display:inline border=0/>撤销本次发送</a>.");
			}
		}*/

		///#endregion 处理消息提示
	}
	/**
	 发送给同表单子线程时干流程数据拷贝到子线程

	 @param toNode
	 @param mywk
	 @param dtlsFrom
	 */
	private void SendToSameSheet_CopyData(Node toNode, Work mywk, MapDtls dtlsFrom) throws Exception {

		if (dtlsFrom.size() > 1)
		{
			for (MapDtl d : dtlsFrom.ToJavaList())
			{
				d.HisGEDtls_temp = null;
			}
		}
		MapDtls dtlsTo = null;
		if (dtlsFrom.size() >= 1)
		{
			dtlsTo = new MapDtls("ND" + toNode.getNodeID());
		}


		///#region  复制附件信息
		//获得当前流程数节点数据.
		FrmAttachmentDBs athDBs = new FrmAttachmentDBs("ND" + this.getHisNode().getNodeID(), String.valueOf(this.getWorkID()));
		if (!athDBs.isEmpty())
		{
			/* 说明当前节点有附件数据 */
			athDBs.Delete(FrmAttachmentDBAttr.FK_MapData, "ND" + toNode.getNodeID(), FrmAttachmentDBAttr.RefPKVal, mywk.getOID());

			for (FrmAttachmentDB athDB : athDBs.ToJavaList())
			{
				FrmAttachmentDB athDB_N = new FrmAttachmentDB();
				athDB_N.Copy(athDB);
				athDB_N.setFrmID("ND" + toNode.getNodeID());
				athDB_N.setRefPKVal( String.valueOf(mywk.getOID()));
				athDB_N.setFKFrmAttachment(athDB_N.getFKFrmAttachment().replace("ND" + this.getHisNode().getNodeID(), "ND" + toNode.getNodeID()));

				if (athDB_N.getHisAttachmentUploadType() == AttachmentUploadType.Single)
				{
					//注意如果是单附件主键的命名规则不能变化，否则会导致与前台约定获取数据错误。
					athDB_N.setMyPK(athDB_N.getFKFrmAttachment() + "_" + mywk.getOID());
					try
					{
						athDB_N.DirectInsert();
					}
					catch (java.lang.Exception e)
					{
						athDB_N.setMyPK(DBAccess.GenerGUID(0, null, null));
						athDB_N.Insert();
					}
				}
				else
				{
					try
					{
						// 多附件就是: FK_MapData+序列号的方式, 替换主键让其可以保存,不会重复.
						athDB_N.setMyPK(athDB_N.getUploadGUID() + "_" + athDB_N.getFrmID() + "_" + athDB_N.getRefPKVal());
						athDB_N.DirectInsert();
					}
					catch (java.lang.Exception e2)
					{
						athDB_N.setMyPK(DBAccess.GenerGUID(0, null, null));
						athDB_N.Insert();
					}
				}
			}
		}

		///#endregion  复制附件信息


		///#region  复制签名信息
		if (this.getHisNode().getMapData().getFrmImgs().size()> 0)
		{
			for (FrmImg img : this.getHisNode().getMapData().getFrmImgs().ToJavaList())
			{
				//排除图片
				if (img.getHisImgAppType() == ImgAppType.Img)
				{
					continue;
				}
				//获取数据
				FrmEleDBs eleDBs = new FrmEleDBs(img.getMyPK(), String.valueOf(this.getWorkID()));
				if (!eleDBs.isEmpty())
				{
					eleDBs.Delete(FrmEleDBAttr.FK_MapData, img.getMyPK().replace("ND" + this.getHisNode().getNodeID(), "ND" + toNode.getNodeID()), FrmEleDBAttr.EleID, this.getWorkID());

					/*说明当前节点有附件数据*/
					for (FrmEleDB eleDB : eleDBs.ToJavaList())
					{
						FrmEleDB eleDB_N = new FrmEleDB();
						eleDB_N.Copy(eleDB);
						eleDB_N.setFrmID(img.getEnPK().replace("ND" + this.getHisNode().getNodeID(), "ND" + toNode.getNodeID()));
						eleDB_N.setRefPKVal( img.getEnPK().replace("ND" + this.getHisNode().getNodeID(), "ND" + toNode.getNodeID()));
						eleDB_N.setEleID(String.valueOf(mywk.getOID()));
						eleDB_N.GenerPKVal();
						eleDB_N.Save();
					}
				}
			}
		}

		///#endregion  复制附件信息


		///#region 复制图片上传附件。
		if (this.getHisNode().getMapData().getFrmImgAths().size()> 0)
		{
			FrmImgAthDBs frmImgAthDBs = new FrmImgAthDBs("ND" + this.getHisNode().getNodeID(), String.valueOf(this.getWorkID()));
			if (!frmImgAthDBs.isEmpty())
			{
				frmImgAthDBs.Delete(FrmAttachmentDBAttr.FK_MapData, "ND" + toNode.getNodeID(), FrmAttachmentDBAttr.RefPKVal, mywk.getOID());

				/*说明当前节点有附件数据*/
				for (FrmImgAthDB imgAthDB : frmImgAthDBs.ToJavaList())
				{
					FrmImgAthDB imgAthDB_N = new FrmImgAthDB();
					imgAthDB_N.Copy(imgAthDB);
					imgAthDB_N.setFrmID("ND" + toNode.getNodeID());
					imgAthDB_N.setRefPKVal( String.valueOf(mywk.getOID()));
					imgAthDB_N.setFKFrmImgAth(imgAthDB_N.getFKFrmImgAth().replace("ND" + this.getHisNode().getNodeID(), "ND" + toNode.getNodeID()));
					imgAthDB_N.Save();
				}
			}
		}

		///#endregion 复制图片上传附件。


		///#region  复制从表信息.
		if (!dtlsFrom.isEmpty() && !dtlsTo.isEmpty())
		{
			int i = -1;
			for (MapDtl dtl : dtlsFrom.ToJavaList())
			{
				i++;
				if (dtlsTo.size() <= i)
				{
					continue;
				}

				MapDtl toDtl = (MapDtl)dtlsTo.get(i);
				if (toDtl.getItIsCopyNDData() == false)
				{
					continue;
				}

				if (Objects.equals(toDtl.getPTable(), dtl.getPTable()))
				{
					continue;
				}

				//获取明细数据。
				GEDtls gedtls = null;
				if (dtl.HisGEDtls_temp == null)
				{
					gedtls = new GEDtls(dtl.getNo());
					QueryObject qo = null;
					qo = new QueryObject(gedtls);
					switch (dtl.getDtlOpenType())
					{
						case ForEmp:
							qo.AddWhere(GEDtlAttr.RefPK, this.getWorkID());
							break;
						case ForWorkID:
							qo.AddWhere(GEDtlAttr.RefPK, this.getWorkID());
							break;
						case ForFID:
							qo.AddWhere(GEDtlAttr.FID, this.getWorkID());
							break;
					}
					qo.DoQuery();
					dtl.HisGEDtls_temp = gedtls;
				}
				gedtls = dtl.HisGEDtls_temp;

				int unPass = 0;
				DBAccess.RunSQL("DELETE FROM " + toDtl.getPTable() + " WHERE RefPK=" + dbStr + "RefPK", "RefPK", mywk.getOID());
				for (GEDtl gedtl : gedtls.ToJavaList())
				{
					GEDtl dtCopy = new GEDtl(toDtl.getNo());
					dtCopy.Copy(gedtl);
					dtCopy.MapDtlNo=toDtl.getNo();
					dtCopy.setRefPK(String.valueOf(mywk.getOID()));
					dtCopy.setOID(0);
					String sql=dtCopy.getSQLCache().Insert;
					//缓存sql中存在当前从表时才执行
					if(sql.contains(dtCopy.MapDtlNo)){
						dtCopy.Insert();
					}

					///#region  复制从表单条 - 附件信息 - M2M- M2MM
					if (toDtl.getItIsEnableAthM())
					{
						/*如果启用了多附件,就复制这条明细数据的附件信息。*/
						athDBs = new FrmAttachmentDBs(dtl.getNo(), String.valueOf(gedtl.getOID()));
						if (!athDBs.isEmpty())
						{
							i = 0;
							for (FrmAttachmentDB athDB : athDBs.ToJavaList())
							{
								i++;
								FrmAttachmentDB athDB_N = new FrmAttachmentDB();
								athDB_N.Copy(athDB);
								athDB_N.setFrmID(toDtl.getNo());
								athDB_N.setMyPK(toDtl.getNo() + "_" + dtCopy.getOID() + "_" + String.valueOf(i));
								athDB_N.setFKFrmAttachment(athDB_N.getFKFrmAttachment().replace("ND" + this.getHisNode().getNodeID(), "ND" + toNode.getNodeID()));
								athDB_N.setRefPKVal( String.valueOf(dtCopy.getOID()));
								athDB_N.DirectInsert();
							}
						}
					}

					///#endregion  复制从表单条 - 附件信息
				}
			}
		}

		///#endregion  复制附件信息
	}
	/**
	 合流点到普通点发送
	 1. 首先要检查完成率.
	 2, 按普通节点向普通节点发送.

	 @return
	 */
	private void NodeSend_31(Node nd) throws Exception {
		//检查完成率.

		// 与1-1一样的逻辑处理.
		this.NodeSend_11(nd);
	}
	/**
	 子线程向下发送

	 @return
	 */
	private String NodeSend_4x()
	{
		return null;
	}
	/**
	 子线程向合流点

	 @return
	 */
	private void NodeSend_53_SameSheet_To_HeLiu(Node toNode) throws Exception {
		Work toNodeWK = toNode.getHisWork();
		toNodeWK.Copy(this.getHisWork());
		toNodeWK.setOID(this.getHisWork().getFID());
		toNodeWK.setFID(0);
		this.town = new WorkNode(toNodeWK, toNode);

		// 获取到达当前合流节点上 与上一个分流点之间的子线程节点的集合。
		String spanNodes = this.SpanSubTheadNodes(toNode);


		///#region 处理FID.
		long fid = this.getHisWork().getFID();
		if (fid == 0)
		{
			if (this.getHisNode().getItIsSubThread() == false)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@当前节点非子线程节点.", "WorkNode", "not_sub_thread"));
			}
			fid = this.getHisGenerWorkFlow().getFID();
			if (fid == 0)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@丢失FID信息.", "WorkNode", "missing_FID"));
			}

			this.getHisWork().setFID(fid);
		}

		///#endregion FID

		// 先查询一下是否有人员，在合流节点上，如果没有就让其初始化人员.
		current_gwls = new GenerWorkerLists();
		current_gwls.Retrieve(GenerWorkerListAttr.WorkID, this.getHisWork().getFID(), GenerWorkerListAttr.FK_Node, toNode.getNodeID(), null);

		if (current_gwls.size() == 0)
		{
			current_gwls = this.Func_GenerWorkerLists(this.town); // 初试化他们的工作人员．
		}


		String toEmpsStr = "";
		String emps = "";
		for (GenerWorkerList wl : current_gwls.ToJavaList())
		{
			toEmpsStr += bp.wf.Glo.DealUserInfoShowModel(wl.getEmpNo(), wl.getEmpName());

			if (current_gwls.size() == 1)
			{
				emps = wl.getEmpNo();
			}
			else
			{
				emps += "@" + wl.getEmpNo();
			}
		}

		//写入日志, 2020.07.26 by zhoupeng.
		ActionType at = ActionType.SubThreadForward;
		this.AddToTrack(at, emps, toEmpsStr, toNode.getNodeID(), toNode.getName(), bp.wf.Glo.multilingual("子线程", "WorkNode", "sub_thread"), this.getHisNode());

		//增加变量.
		this.addMsg(SendReturnMsgFlag.VarAcceptersID, emps.replace("@", ","), SendReturnMsgType.SystemMsg);
		this.addMsg(SendReturnMsgFlag.VarAcceptersName, toEmpsStr, SendReturnMsgType.SystemMsg);

		/*
		 * 更新它的节点 worklist 信息, 说明当前节点已经完成了.
		 * 不让当前的操作员能看到自己的工作。
		 */


		///#region 设置父流程状态 设置当前的节点为:
		//根据Node判断该节点是否绑定表单库的表单
		boolean isCopyData = true;
		//分流节点和子线程的节点绑定的表单相同
		if (toNode.getHisFormType() == NodeFormType.RefOneFrmTree && toNode.getNodeFrmID().equals(this.getHisNode().getNodeFrmID()) == true)
		{
			isCopyData = false;
		}
		if (isCopyData == true)
		{
			Work mainWK = town.getHisWork();
			mainWK.setOID(this.getHisWork().getFID());
			mainWK.RetrieveFromDBSources();

			// 复制报表上面的数据到合流点上去。
			DataTable dt = DBAccess.RunSQLReturnTable("SELECT * FROM " + this.getHisFlow().getPTable() + " WHERE OID=" + dbStr + "OID", "OID", this.getHisWork().getFID());
			for (DataColumn dc : dt.Columns)
			{
				mainWK.SetValByKey(dc.ColumnName, dt.Rows.get(0).getValue(dc.ColumnName));
			}

			mainWK.setRec(getWebUser().getNo());
			mainWK.setOID(this.getHisWork().getFID());
			mainWK.Save();
		}


		// 产生合流汇总从表数据.
		this.GenerHieLiuHuiZhongDtlData_2013(toNode);

		//设置当前子线程已经通过.
		ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=1  WHERE WorkID=" + dbStr + "WorkID AND FID=" + dbStr + "FID AND IsPass=0";
		ps.Add("WorkID", this.getWorkID());
		ps.Add("FID", this.getHisWork().getFID());
		DBAccess.RunSQL(ps);


		//合流节点上的工作处理者。
		GenerWorkerLists gwls = new GenerWorkerLists(this.getHisWork().getFID(), toNode.getNodeID());
		current_gwls = gwls;

		/* 合流点需要等待各个分流点全部处理完后才能看到它。*/
		String mysql = "";


///#warning 对于多个分合流点可能会有问题。
		mysql = "SELECT COUNT(distinct WorkID) AS Num FROM WF_GenerWorkerlist WHERE IsEnable=1 AND FID=" + this.getHisWork().getFID() + " AND FK_Node IN (" + spanNodes + ")";
		BigDecimal numAll = new BigDecimal(DBAccess.RunSQLReturnValInt(mysql));

		GenerWorkFlow gwf = new GenerWorkFlow(this.getHisWork().getFID());
		//记录子线程到达合流节点数
		int count = gwf.GetParaInt("ThreadCount", 0);
		gwf.SetPara("ThreadCount", count + 1);
		gwf.setTodoEmps(toEmpsStr);
		gwf.Update();


		BigDecimal numPassed = new BigDecimal(gwf.GetParaInt("ThreadCount", 0));

		BigDecimal passRate = numPassed.divide(numAll,4,BigDecimal.ROUND_CEILING).multiply(BigDecimal.valueOf(100));
		if (toNode.getPassRate().compareTo(passRate) <= 0)
		{
			/* 这时已经通过,可以让主线程看到待办. */
			ps = new Paras();
			ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID";
			ps.Add("FK_Node", toNode.getNodeID());
			ps.Add("WorkID", this.getHisWork().getFID());
			int num = DBAccess.RunSQL(ps);
			if (num == 0)
			{
				throw new RuntimeException("@不应该更新不到它.");
			}

			gwf.setEmps(gwf.getEmps() + "@" + this.getHisGenerWorkFlow().getEmps());
			//gwf.Para("ThreadCount", 0);
			gwf.Update();

		}
		else
		{

///#warning 为了不让其显示在途的工作需要， =3 不是正常的处理模式。
			ps = new Paras();
			ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=3 WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID";
			ps.Add("FK_Node", toNode.getNodeID());
			ps.Add("WorkID", this.getHisWork().getFID());
			int num = DBAccess.RunSQL(ps);
			if (num == 0)
			{
				throw new RuntimeException("@不应该更新不到它.");
			}

			gwf.setEmps(gwf.getEmps() + "@" + this.getHisGenerWorkFlow().getEmps());
			gwf.Update();
		}


		this.getHisGenerWorkFlow().setNodeID(toNode.getNodeID());
		this.getHisGenerWorkFlow().setNodeName(toNode.getName());

		//改变当前流程的当前节点.
		ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkFlow SET WFState=" + WFState.Runing.getValue() + ",  FK_Node=" + dbStr + "FK_Node,NodeName=" + dbStr + "NodeName WHERE WorkID=" + dbStr + "WorkID";
		ps.Add("FK_Node", toNode.getNodeID());
		ps.Add("NodeName", toNode.getName(), false);
		ps.Add("WorkID", this.getHisWork().getFID());
		DBAccess.RunSQL(ps);



		///#endregion 设置父流程状态

		this.addMsg("InfoToHeLiu", bp.wf.Glo.multilingual("@流程已经运行到合流节点[{0}]. @您的工作已经发送给如下人员[{1}]. @您是第{2}个到达此节点的处理人.", "WorkNode", "first_node_person", toNode.getName(), toEmpsStr, (String.valueOf(count))));


		///#region 处理国机的需求, 把最后一个子线程的主表数据同步到合流节点的Rpt里面去.(不是很合理) 2015.12.30
		Work towk = town.getHisWork();
		towk.setOID(this.getHisWork().getFID());
		towk.RetrieveFromDBSources();
		towk.Copy(this.getHisWork());
		towk.DirectUpdate();

		///#endregion 处理国机的需求, 把最后一个子线程的主表数据同步到合流节点的Rpt里面去.

	}

	/**
	 节点向下运动
	 */
	private void NodeSend_Send_5_5() throws Exception {
		//执行设置当前人员的完成时间. for: anhua 2013-12-18.
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkerlist SET CDT=" + dbstr + "CDT WHERE WorkID=" + dbstr + "WorkID AND FK_Node=" + dbstr + "FK_Node AND FK_Emp=" + dbstr + "FK_Emp";
		ps.Add(GenerWorkerListAttr.CDT, DataType.getCurrentDateTimess(), false);
		ps.Add(GenerWorkerListAttr.WorkID, this.getWorkID());
		ps.Add(GenerWorkerListAttr.FK_Node, this.getHisNode().getNodeID());
		ps.Add(GenerWorkerListAttr.FK_Emp, this.getExecer(), false);
		DBAccess.RunSQL(ps);

		switch (this.getHisNode().getHisRunModel())
		{
			case Ordinary: // 1： 普通节点向下发送的
				Node toND = this.NodeSend_GenerNextStepNode();
				if (this.isStopFlow())
				{
					return;
				}

				if (this.getHisNode().getFlowNo().equals(toND.getFlowNo())==false)
				{
					NodeSendToYGFlow(toND, JumpToEmp);
					return;
				}

				//写入到达信息.
				this.addMsg(SendReturnMsgFlag.VarToNodeID, String.valueOf(toND.getNodeID()), String.valueOf(toND.getNodeID()), SendReturnMsgType.SystemMsg);
				this.addMsg(SendReturnMsgFlag.VarToNodeName, toND.getName(), toND.getName(), SendReturnMsgType.SystemMsg);
				switch (toND.getHisRunModel())
				{
					case Ordinary: //1-1 普通节to普通节点
						this.NodeSend_11(toND);
						break;
					case FL: // 1-2 普通节to分流点
						this.NodeSend_11(toND);
						break;
					case HL: //1-3 普通节to合流点
						this.NodeSend_11(toND);
						// throw new Exception("@流程设计错误:请检查流程获取详细信息, 普通节点下面不能连接合流节点(" + toND.getName() +").");
						break;
					case FHL: //1-4 普通节点to分合流点
						this.NodeSend_11(toND);
						// throw new Exception("@流程设计错误:请检查流程获取详细信息, 普通节点下面不能连接分合流节点(" + toND.getName() +").");
						break;
					case SubThreadSameWorkID: //1-5 普通节to子线程点
					case SubThreadUnSameWorkID: //1-5 普通节to子线程点
						throw new RuntimeException(bp.wf.Glo.multilingual("@流程设计错误: 普通节点下面[" + this.getHisNode().getName() + "]不能连接子线程节点{0}", "WorkNode", "workflow_error_3", toND.getName()));
					default:
						throw new RuntimeException(bp.wf.Glo.multilingual("@没有判断的节点类型({0}).", "WorkNode", "node_type_does_not_exist", toND.getName()));
				}
				break;
			case FL: // 2: 分流节点向下发送的
				Nodes toNDs = this.Func_GenerNextStepNodes();
				if (toNDs.size() == 1)
				{
					Node toND2 = toNDs.get(0) instanceof Node ? (Node)toNDs.get(0) : null;
					//加入系统变量.
					this.addMsg(SendReturnMsgFlag.VarToNodeID, String.valueOf(toND2.getNodeID()), String.valueOf(toND2.getNodeID()), SendReturnMsgType.SystemMsg);
					this.addMsg(SendReturnMsgFlag.VarToNodeName, toND2.getName(), toND2.getName(), SendReturnMsgType.SystemMsg);

					switch (toND2.getHisRunModel())
					{
						case Ordinary: //2.1 分流点to普通节点
							this.NodeSend_11(toND2); // 按普通节点到普通节点处理.
							break;
						case FL: //2.2 分流点to分流点
							//  throw new Exception("@流程设计错误:请检查流程获取详细信息, 分流点(" + this.getHisNode().getName() +")下面不能连接分流节点(" + toND2.getName() +").");
						case HL: //2.3 分流点to合流点,分合流点.
						case FHL:
							this.NodeSend_11(toND2); // 按普通节点到普通节点处理.
							break;
						// throw new Exception("@流程设计错误:请检查流程获取详细信息, 分流点(" + this.getHisNode().getName() +")下面不能连接合流节点(" + toND2.getName() +").");
						case SubThreadSameWorkID: // 2.4 分流点to子线程点
						case SubThreadUnSameWorkID: // 2.4 分流点to子线程点

							if (toND2.getHisRunModel() == RunModel.SubThreadSameWorkID)
							{
								this.getHisGenerWorkFlow().setNodeName(this.getHisGenerWorkFlow().getNodeName() + "," + toND2.getName());
								this.getHisGenerWorkFlow().SetPara("ThreadCount", 0);
								this.getHisGenerWorkFlow().DirectUpdate();

								NodeSend_24_SameSheet(toND2);
							}
							else
							{
								//为计算中心：执行更新.
								String names = "";
								for (Node mynd : toNDs.ToJavaList())
								{
									names += "," + mynd.getName();
								}
								this.getHisGenerWorkFlow().setNodeName(this.getHisGenerWorkFlow().getNodeName() + names);
								this.getHisGenerWorkFlow().SetPara("ThreadCount", 0); //子线程增加方向条件，出现跳转的修改
								this.getHisGenerWorkFlow().DirectUpdate();
								NodeSend_24_UnSameSheet(toNDs); //可能是只发送1个异表单



							}
							break;
						default:
							throw new RuntimeException(bp.wf.Glo.multilingual("@没有判断的节点类型({0}).", "WorkNode", "node_type_does_not_exist", toND2.getName()));
							//break;
					}
				}
				else
				{
					/* 如果有多个节点，检查一下它们必定是子线程节点否则，就是设计错误。*/
					boolean isHaveSameSheet = false;
					boolean isHaveUnSameSheet = false;
					for (Node nd : toNDs.ToJavaList())
					{
						switch (nd.getHisRunModel())
						{
							case Ordinary:
								NodeSend_11(nd); //按普通节点到普通节点处理.
								break;
							case FL:
							case FHL:
							case HL:
								NodeSend_11(nd); //按普通节点到普通节点处理.
								break;
							default:
								break;
						}
						if (nd.getHisRunModel() == RunModel.SubThreadSameWorkID)
						{
							isHaveSameSheet = true;
						}

						if (nd.getHisRunModel() == RunModel.SubThreadUnSameWorkID)
						{
							isHaveUnSameSheet = true;
						}
					}

					if (isHaveSameSheet == false && isHaveUnSameSheet == false)
						throw new RuntimeException(bp.wf.Glo.multilingual("@不支持流程模式: 分流节点同时启动了多个线性节点.", "WorkNode", "workflow_error_5"));

					if (isHaveUnSameSheet && isHaveSameSheet)
					{
						throw new RuntimeException(bp.wf.Glo.multilingual("@不支持流程模式: 分流节点同时启动了同表单的子线程与异表单的子线程.", "WorkNode", "workflow_error_4"));
					}

					if (isHaveSameSheet == true)
					{
						throw new RuntimeException(bp.wf.Glo.multilingual("@不支持流程模式: 分流节点同时启动了多个同表单的子线程.", "WorkNode", "workflow_error_5"));
					}

					this.getHisGenerWorkFlow().SetPara("ThreadCount", 0); //子线程增加方向条件，出现跳转的修改
					this.getHisGenerWorkFlow().DirectUpdate();
					//启动多个异表单子线程节点.
					this.NodeSend_24_UnSameSheet(toNDs);

					//为计算中心：执行更新.
					String names = "";
					for (Node mynd : toNDs.ToJavaList())
					{
						names += "," + mynd.getName();
					}
					this.getHisGenerWorkFlow().setNodeName(this.getHisGenerWorkFlow().getNodeName() + names);
					this.getHisGenerWorkFlow().DirectUpdate();

				}
				break;
			case HL: // 3: 合流节点向下发送
				Node toND3 = this.NodeSend_GenerNextStepNode();
				if (this.isStopFlow())
				{
					return;
				}

				//加入系统变量.
				this.addMsg(SendReturnMsgFlag.VarToNodeID, String.valueOf(toND3.getNodeID()), String.valueOf(toND3.getNodeID()), SendReturnMsgType.SystemMsg);
				this.addMsg(SendReturnMsgFlag.VarToNodeName, toND3.getName(), toND3.getName(), SendReturnMsgType.SystemMsg);

				switch (toND3.getHisRunModel())
				{
					case Ordinary: //3.1 普通工作节点
						this.NodeSend_31(toND3); // 让它与普通点点普通点一样的逻辑.
						break;
					case FL: //3.2 分流点
						this.NodeSend_31(toND3); // 让它与普通点点普通点一样的逻辑.
						break;
					case HL: //3.3 合流点
					case FHL:
						this.NodeSend_31(toND3); // 让它与普通点点普通点一样的逻辑.
						break;
					//throw new Exception("@流程设计错误:请检查流程获取详细信息, 合流点(" + this.getHisNode().getName() +")下面不能连接合流节点(" + toND3.getName() +").");
					case SubThreadUnSameWorkID: //3.4 子线程
					case SubThreadSameWorkID: //3.4 子线程
						throw new RuntimeException(bp.wf.Glo.multilingual("@流程设计错误: 合流节点({0})下面不能连接子线程节点({1})", "WorkNode", "workflow_error_6", this.getHisNode().getName(), toND3.getName()));
					default:
						throw new RuntimeException(bp.wf.Glo.multilingual("@没有判断的节点类型({0}).", "WorkNode", "node_type_does_not_exist", toND3.getName()));
				}
				break;
			case FHL: // 4: 分流节点向下发送的
				if (this.isStopFlow())
				{
					return;
				}
				Nodes toND4s = this.Func_GenerNextStepNodes();
				if (toND4s.size() == 1)
				{
					Node toND4 = toND4s.get(0) instanceof Node ? (Node)toND4s.get(0) : null;
					//加入系统变量.
					this.addMsg(SendReturnMsgFlag.VarToNodeID,  String.valueOf(toND4.getNodeID()),  String.valueOf(toND4.getNodeID()), SendReturnMsgType.SystemMsg);
					this.addMsg(SendReturnMsgFlag.VarToNodeName, toND4.getName(), toND4.getName(), SendReturnMsgType.SystemMsg);

					switch (toND4.getHisRunModel())
					{
						case Ordinary: //4.1 普通工作节点
							this.NodeSend_11(toND4); // 让它与普通点点普通点一样的逻辑.
							break;
						case FL: //4.2 分流点
							throw new RuntimeException(bp.wf.Glo.multilingual("@流程设计错误: 合流节点({0})下面不能连接分流节点({1})", "WorkNode", "workflow_error_7", this.getHisNode().getName(), toND4.getName()));
						case HL: //4.3 合流点
						case FHL:
							this.NodeSend_11(toND4); // 让它与普通点点普通点一样的逻辑.
							break;
						case SubThreadSameWorkID: //4.5 子线程
						case SubThreadUnSameWorkID: //4.5 子线程

							if (toND4.getHisRunModel() == RunModel.SubThreadSameWorkID)
							{

								this.getHisGenerWorkFlow().SetPara("ThreadCount", 0);
								this.getHisGenerWorkFlow().DirectUpdate();
								NodeSend_24_SameSheet(toND4);

								// 为广西计算中心.
								this.getHisGenerWorkFlow().setNodeName(this.getHisGenerWorkFlow().getNodeName() + "," + toND4.getName());

								this.getHisGenerWorkFlow().DirectUpdate();
							}
							else
							{
								Nodes toNDs4 = this.Func_GenerNextStepNodes();
								this.getHisGenerWorkFlow().SetPara("ThreadCount", 0);
								this.getHisGenerWorkFlow().DirectUpdate();
								NodeSend_24_UnSameSheet(toNDs4); //可能是只发送1个异表单

								//为计算中心：执行更新.
								String names = "";
								for (Node mynd : toNDs4.ToJavaList())
								{
									names += "," + mynd.getName();
								}
								this.getHisGenerWorkFlow().setNodeName(this.getHisGenerWorkFlow().getNodeName() + names);
								this.getHisGenerWorkFlow().DirectUpdate();
							}
							break;
						default:
							throw new RuntimeException(bp.wf.Glo.multilingual("@没有判断的节点类型({0}).", "WorkNode", "node_type_does_not_exist", toND4.getName()));
					}
				}
				else
				{
					/* 如果有多个节点，检查一下它们必定是子线程节点否则，就是设计错误。*/
					boolean isHaveSameSheet = false;
					boolean isHaveUnSameSheet = false;
					for (Node nd : toND4s.ToJavaList())
					{
						switch (nd.getHisRunModel())
						{
							case Ordinary:
								NodeSend_11(nd); //按普通节点到普通节点处理.
								break;
							case FL:
							case FHL:
							case HL:
								NodeSend_11(nd); //按普通节点到普通节点处理.
								break;
							default:
								break;
						}
						if (nd.getHisRunModel() == RunModel.SubThreadSameWorkID)
						{
							isHaveSameSheet = true;
						}

						if (nd.getHisRunModel() == RunModel.SubThreadUnSameWorkID)
						{
							isHaveUnSameSheet = true;
						}
					}

					if (isHaveUnSameSheet && isHaveSameSheet)
					{
						throw new RuntimeException(bp.wf.Glo.multilingual("@不支持流程模式: 分流节点同时启动了同表单的子线程与异表单的子线程.", "WorkNode", "workflow_error_4"));
					}

					if (isHaveSameSheet == true)
					{
						throw new RuntimeException(bp.wf.Glo.multilingual("@不支持流程模式: 分流节点同时启动了多个同表单的子线程.", "WorkNode", "workflow_error_5"));
					}

					this.getHisGenerWorkFlow().SetPara("ThreadCount", 0);
					this.getHisGenerWorkFlow().DirectUpdate();
					//启动多个异表单子线程节点.
					this.NodeSend_24_UnSameSheet(toND4s);

					//为计算中心：执行更新.
					String names = "";
					for (Node mynd : toND4s.ToJavaList())
					{
						names += "," + mynd.getName();
					}
					this.getHisGenerWorkFlow().setNodeName(this.getHisGenerWorkFlow().getNodeName() + names);
					this.getHisGenerWorkFlow().DirectUpdate();

				}
				break;
			case SubThreadSameWorkID: // 5: 子线程节点向下发送的
			case SubThreadUnSameWorkID:
				Node toND5 = this.NodeSend_GenerNextStepNode();
				if (this.isStopFlow())
				{
					return;
				}

				//加入系统变量.
				this.addMsg(SendReturnMsgFlag.VarToNodeID, String.valueOf(toND5.getNodeID()), String.valueOf(toND5.getNodeID()), SendReturnMsgType.SystemMsg);
				this.addMsg(SendReturnMsgFlag.VarToNodeName, toND5.getName(), toND5.getName(), SendReturnMsgType.SystemMsg);

				switch (toND5.getHisRunModel())
				{
					case Ordinary: //5.1 普通工作节点
						throw new RuntimeException(bp.wf.Glo.multilingual("@流程设计错误: 子线程节点({0})下面不能连接普通节点({1})", "WorkNode", "workflow_error_8", this.getHisNode().getName(), toND5.getName()));
						//break;
					case FL: //5.2 分流点
						throw new RuntimeException(bp.wf.Glo.multilingual("@流程设计错误: 子线程节点({0})下面不能连接分流节点({1})", "WorkNode", "workflow_error_9", this.getHisNode().getName(), toND5.getName()));
					case HL: //5.3 合流点
					case FHL: //5.4 分合流点
						if (this.getHisNode().getHisRunModel() == RunModel.SubThreadSameWorkID)
						{
							this.NodeSend_53_SameSheet_To_HeLiu(toND5);
						}
						else
						{
							this.NodeSend_53_UnSameSheet_To_HeLiu(toND5);
						}

						//把合流点设置未读.
						ps = new Paras();
						ps.SQL = "UPDATE WF_GenerWorkerlist SET IsRead=0 WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND  FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node";
						ps.Add("WorkID", this.getHisWork().getFID());
						ps.Add("FK_Node", toND5.getNodeID());
						DBAccess.RunSQL(ps);
						break;
					case SubThreadSameWorkID: // 5.5 子线程
					case SubThreadUnSameWorkID:

						//为计算中心增加,子线程停留节点.
						GenerWorkFlow gwfZhuGan = new GenerWorkFlow(this.getHisGenerWorkFlow().getFID());
						if (gwfZhuGan.getNodeName().contains("," + toND5.getName()) == false)
						{
							gwfZhuGan.setNodeName(gwfZhuGan.getNodeName().replace("," + this.getHisNode().getName(), "," + toND5.getName()));
							if (gwfZhuGan.getNodeName().contains("," + toND5.getName()) == false)
							{
								gwfZhuGan.setNodeName(gwfZhuGan.getNodeName() + "," + toND5.getName());
							}
							gwfZhuGan.DirectUpdate(); //执行更新.
						}


						if (toND5.getHisRunModel() == this.getHisNode().getHisRunModel())
						{

							///#region 删除到达节点的子线程如果有，防止退回信息垃圾数据问题,如果退回处理了这个部分就不需要处理了.
							ps = new Paras();
							ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE FID=" + dbStr + "FID  AND FK_Node=" + dbStr + "FK_Node";
							ps.Add("FID", this.getHisWork().getFID());
							ps.Add("FK_Node", toND5.getNodeID());

							///#endregion 删除到达节点的子线程如果有，防止退回信息垃圾数据问题，如果退回处理了这个部分就不需要处理了.

							this.NodeSend_11(toND5); //与普通节点一样.
						}
						else
						{
							throw new RuntimeException(bp.wf.Glo.multilingual("@流程设计错误：两个连续子线程的子线程模式不一样(从节点{0}到节点{1}).", "WorkNode", "workflow_error_10", this.getHisNode().getName(), toND5.getName()));
						}
						break;
					default:
						throw new RuntimeException(bp.wf.Glo.multilingual("@没有判断的节点类型({0}).", "WorkNode", "node_type_does_not_exist", toND5.getName()));
				}
				break;
			default:
				throw new RuntimeException(bp.wf.Glo.multilingual("@没有判断的执行节点类型({0}).", "WorkNode", "node_type_does_not_exist", this.getHisNode().getHisRunModelT()));
		}
	}


	///#region 执行数据copy.
	public final void CopyData(Work toWK, Node toND, boolean isSamePTable) throws Exception {
		//如果存储模式为, 合并模式.
		if (toND.getItIsSubThread() == false)
		{
			return;
		}

		String errMsg = "如果两个数据源不想等，就执行 copy - 期间出现错误.";
		if (isSamePTable == true)
		{
			return;
		}


		///#region 主表数据copy.
		if (isSamePTable == false)
		{
			toWK.SetValByKey("OID", this.getHisWork().getOID()); //设定它的ID.
			if (this.getHisNode().getItIsStartNode() == false)
			{
				toWK.Copy(this.rptGe);
			}

			toWK.Copy(this.getHisWork()); // 执行 copy 上一个节点的数据。
			toWK.setRec(this.getExecer());

			//要考虑FID的问题.
			if (this.getHisNode().getItIsSubThread() == true && toND.getItIsSubThread() == true)
			{
				toWK.setFID(this.getHisWork().getFID());
			}

			try
			{
				//判断是不是MD5流程？
				if (this.getHisFlow().getItIsMD5())
				{
					toWK.SetValByKey("MD5", Glo.GenerMD5(toWK));
				}

				try {
					if (toWK.getIsExits())
					{
						toWK.Update();
					}
					else
					{
						toWK.Insert();
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			catch (RuntimeException ex)
			{
				try {
					toWK.CheckPhysicsTable();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				try
				{
					toWK.Copy(this.getHisWork()); // 执行 copy 上一个节点的数据。
					toWK.setRec(this.getExecer());
					toWK.SaveAsOID(toWK.getOID());
				}
				catch (RuntimeException ex11)
				{
					try {
						if (toWK.Update() == 0)
						{
							throw new RuntimeException(ex.getMessage() + " == " + ex11.getMessage());
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}


	}

	///#endregion


	///#region 返回对象处理.
	public SendReturnObjs HisMsgObjs = null;
	public final void addMsg(String flag, String msg)
	{
		addMsg(flag, msg, null, SendReturnMsgType.Info);
	}
	public final void addMsg(String flag, String msg, SendReturnMsgType msgType)
	{
		addMsg(flag, msg, null, msgType);
	}
	public final void addMsg(String flag, String msg, String msgofHtml, SendReturnMsgType msgType)
	{
		if (HisMsgObjs == null)
		{
			HisMsgObjs = new SendReturnObjs();
		}
		this.HisMsgObjs.AddMsg(flag, msg, msgofHtml, msgType);
	}
	public final void addMsg(String flag, String msg, String msgofHtml)
	{
		addMsg(flag, msg, msgofHtml, SendReturnMsgType.Info);
	}

	///#endregion 返回对象处理.


	///#region 方法
	/**
	 发送失败是撤消数据。
	 */
	public final void DealEvalUn() throws Exception {

		//数据发送。
		Eval eval = new Eval();
		if (this.getHisNode().getItIsFLHL() == false)
		{
			eval.setMyPK(this.getWorkID() + "_" + this.getHisNode().getNodeID());
			eval.Delete();
		}

		// 分合流的情况，它是明细表产生的质量评价。
		MapDtls dtls = this.getHisNode().getMapData().getMapDtls();
		for (MapDtl dtl : dtls.ToJavaList())
		{
			if (dtl.getItIsHLDtl() == false)
			{
				continue;
			}

			//获取明细数据。
			GEDtls gedtls = new GEDtls(dtl.getNo());
			QueryObject qo = null;
			qo = new QueryObject(gedtls);
			switch (dtl.getDtlOpenType())
			{
				case ForEmp:
					qo.AddWhere(GEDtlAttr.RefPK, this.getWorkID());
					break;
				case ForWorkID:
					qo.AddWhere(GEDtlAttr.RefPK, this.getWorkID());
					break;
				case ForFID:
					qo.AddWhere(GEDtlAttr.FID, this.getWorkID());
					break;
			}
			qo.DoQuery();

			for (GEDtl gedtl : gedtls.ToJavaList())
			{
				eval = new Eval();
				eval.setMyPK(gedtl.getOID() + "_" + gedtl.getRec());
				eval.Delete();
			}
		}
	}
	/**
	 处理质量考核
	 */
	public final void DealEval() throws Exception {
		if (this.getHisNode().getItIsEval() == false)
		{
			return;
		}

		Eval eval = new Eval();

		if (this.getHisNode().getItIsFLHL() == false)
		{
			eval.setMyPK(this.getWorkID() + "_" + this.getHisNode().getNodeID());
			eval.Delete();

			eval.setTitle(this.getHisGenerWorkFlow().getTitle());

			eval.setWorkID(this.getWorkID());
			eval.setNodeID(this.getHisNode().getNodeID());
			eval.setNodeName(this.getHisNode().getName());

			eval.setFlowNo(this.getHisNode().getFlowNo());
			eval.setFlowName(this.getHisNode().getFlowName());

			eval.setDeptNo(this.getExecerDeptNo());
			eval.setDeptName(this.getExecerDeptName());

			eval.setRec(this.getExecer());
			eval.setRecName(this.getExecerName());

			eval.setRDT(DataType.getCurrentDateTime());
			eval.setNY(DataType.getCurrentYearMonth());

			eval.setEvalEmpNo(this.getHisWork().GetValStringByKey(WorkSysFieldAttr.EvalEmpNo));
			eval.setEvalEmpName(this.getHisWork().GetValStringByKey(WorkSysFieldAttr.EvalEmpName));
			eval.setEvalCent(this.getHisWork().GetValStringByKey(WorkSysFieldAttr.EvalCent));
			eval.setEvalNote(this.getHisWork().GetValStringByKey(WorkSysFieldAttr.EvalNote));

			eval.Insert();
			return;
		}

		// 分合流的情况，它是明细表产生的质量评价。
		bp.sys.MapDtls dtls = this.getHisNode().getMapData().getMapDtls();
		for (MapDtl dtl : dtls.ToJavaList())
		{
			if (dtl.getItIsHLDtl() == false)
			{
				continue;
			}

			//获取明细数据。
			GEDtls gedtls = new GEDtls(dtl.getNo());
			QueryObject qo = null;
			qo = new QueryObject(gedtls);
			switch (dtl.getDtlOpenType())
			{
				case ForEmp:
					qo.AddWhere(GEDtlAttr.RefPK, this.getWorkID());
					break;
				case ForWorkID:
					qo.AddWhere(GEDtlAttr.RefPK, this.getWorkID());
					break;
				case ForFID:
					qo.AddWhere(GEDtlAttr.FID, this.getWorkID());
					break;
			}
			qo.DoQuery();

			for (GEDtl gedtl : gedtls.ToJavaList())
			{
				eval = new Eval();
				eval.setMyPK(gedtl.getOID() + "_" + gedtl.getRec());
				eval.Delete();

				eval.setTitle(this.getHisGenerWorkFlow().getTitle());

				eval.setWorkID(this.getWorkID());
				eval.setNodeID(this.getHisNode().getNodeID());
				eval.setNodeName(this.getHisNode().getName());

				eval.setFlowNo(this.getHisNode().getFlowNo());
				eval.setFlowName(this.getHisNode().getFlowName());

				eval.setDeptNo(this.getExecerDeptNo());
				eval.setDeptName(this.getExecerDeptName());

				eval.setRec(this.getExecer());
				eval.setRecName(this.getExecerName());

				eval.setRDT(DataType.getCurrentDateTime());
				eval.setNY(DataType.getCurrentYearMonth());

				eval.setEvalEmpNo(gedtl.GetValStringByKey(WorkSysFieldAttr.EvalEmpNo));
				eval.setEvalEmpName(gedtl.GetValStringByKey(WorkSysFieldAttr.EvalEmpName));
				eval.setEvalCent(gedtl.GetValStringByKey(WorkSysFieldAttr.EvalCent));
				eval.setEvalNote(gedtl.GetValStringByKey(WorkSysFieldAttr.EvalNote));
				eval.Insert();
			}
		}
	}


	///#endregion


	/**
	 工作流发送业务处理
	 */
	public final SendReturnObjs NodeSend() throws Exception {
		SendReturnObjs sendObj = NodeSend(null, null, false);
		return sendObj;
	}

	/**
	 1变N,用于分流节点，向子线程copy数据。

	 @return
	 */
	public final void CheckFrm1ToN() throws Exception {
		//只有分流，合流才能执行1ToN.
		if (this.getHisNode().getHisRunModel() == RunModel.Ordinary || this.getHisNode().getHisRunModel() == RunModel.HL || this.getHisNode().getItIsSubThread() == true)
		{
			return;
		}

		//初始化变量.
		if (frmNDs == null)
		{
			frmNDs = new FrmNodes(this.getHisNode().getFlowNo(), this.getHisNode().getNodeID());
		}

		for (FrmNode fn : frmNDs.ToJavaList())
		{
			if (fn.getItIs1ToN() == false)
			{
				continue;
			}


			///#region 获得实体主键.
			// 处理主键.
			long pk = 0; // this.getWorkID();
			switch (fn.getWhoIsPK())
			{
				case FID:
					pk = this.getHisWork().getFID();
					break;
				case OID:
					pk = this.getHisWork().getOID();
					break;
				case PWorkID:
					if (this.rptGe == null)
					{
						this.rptGe = new GERpt("ND" + Integer.parseInt(this.getHisFlow().getNo()) + "Rpt", this.getWorkID());
					}
					pk = this.rptGe.getPWorkID();
					break;
				default:
					throw new RuntimeException(bp.wf.Glo.multilingual("@未判断的类型:{0}.", "WorkNode", "not_found_value", fn.getWhoIsPK().toString()));
			}

			if (pk == 0)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@未能获取表单主键.", "WorkNode", "not_found_form_primary_key"));
			}

			///#endregion 获得实体主键.

			//初始化这个实体.
			GEEntity geEn = new GEEntity(fn.getFrmID(), pk);

			//首先删除垃圾数据.
			geEn.Delete("FID", this.getWorkID());

			//循环子线程，然后插入数据.
			for (GenerWorkerList item : current_gwls.ToJavaList())
			{
				geEn.setPKVal(item.getWorkID()); //子线程的WorkID作为.
				geEn.SetValByKey("FID", this.getWorkID());


				///#region 处理默认变量.
				//foreach (Attr attr in geEn.getEnMap().getAttrs())
				//{
				//    if (attr.getDefaultValOfReal() == "@RDT")
				//    {
				//        geEn.SetValByKey(attr.getKey(), DataType.getCurrentDateTime());
				//        continue;
				//    }

				//    if (attr.getDefaultValOfReal() == "@WebUser.No")
				//    {
				//        geEn.SetValByKey(attr.getKey(), item.FK_Emp);
				//        continue;
				//    }

				//    if (attr.getDefaultValOfReal() == "@WebUser.Name")
				//    {
				//        geEn.SetValByKey(attr.getKey(), item.EmpNam);
				//        continue;
				//    }

				//    if (attr.getDefaultValOfReal() == "@WebUser.getDeptNo()")
				//    {
				//        Emp emp = new Emp(item.EmpNo);
				//        geEn.SetValByKey(attr.getKey(), emp.getDeptNo());
				//        continue;
				//    }

				//    if (attr.getDefaultValOfReal() == "@WebUser.getDeptName()")
				//    {
				//        Emp emp = new Emp(item.EmpNo);
				//        geEn.SetValByKey(attr.getKey(), emp.getDeptText());
				//        continue;
				//    }
				//}

				///#endregion 处理默认变量.

				geEn.DirectInsert();
			}
		}
	}
	/**
	 当前节点的-表单绑定.
	 */
	private FrmNodes frmNDs = null;
	/**
	 汇总子线程的表单到合流节点上去

	 @return
	 */
	public final void CheckFrmHuiZongToDtl() throws Exception {
		//只有分流，合流才能执行1ToN.
		if (this.getHisNode().getItIsSubThread() == false)
		{
			return;
		}

		//初始化变量.
		if (frmNDs == null)
		{
			frmNDs = new FrmNodes(this.getHisNode().getFlowNo(), this.getHisNode().getNodeID());
		}

		for (FrmNode fn : frmNDs.ToJavaList())
		{
			//如果该表单不需要汇总，就不处理他.
			if (Objects.equals(fn.getHuiZong(), "0") || Objects.equals(fn.getHuiZong(), ""))
			{
				continue;
			}


			///#region 获得实体主键.
			// 处理主键.
			long pk = 0; // this.getWorkID();
			switch (fn.getWhoIsPK())
			{
				case FID:
					pk = this.getHisWork().getFID();
					break;
				case OID:
					pk = this.getHisWork().getOID();
					break;
				case PWorkID:
					pk = this.rptGe.getPWorkID();
					break;
				default:
					throw new RuntimeException(bp.wf.Glo.multilingual("@未判断的类型:{0}.", "WorkNode", "not_found_value", fn.getWhoIsPK().toString()));
			}

			if (pk == 0)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@未能获取表单主键.", "WorkNode", "not_found_form_primary_key"));
			}

			///#endregion 获得实体主键.


			//初始化这个实体,获得这个实体的数据.
			GEEntity rpt = new GEEntity(fn.getFrmID(), pk);
			//
			String[] strs = fn.getHuiZong().trim().split("[@]", -1);

			//实例化这个数据.
			MapDtl dtl = new MapDtl(strs[1].toString());

			//把数据汇总到指定的表里.
			GEDtl dtlEn = dtl.getHisGEDtl();
			dtlEn.setOID((int)this.getWorkID());
			int i = dtlEn.RetrieveFromDBSources();
			dtlEn.Copy(rpt);

			dtlEn.setOID((int)this.getWorkID());
			dtlEn.setRDT(DataType.getCurrentDateTime());
			dtlEn.setRec(bp.web.WebUser.getNo());

			dtlEn.setRefPK(String.valueOf(this.getHisWork().getFID()));
			dtlEn.setFID(0);

			if (i == 0)
			{
				dtlEn.SaveAsOID((int)this.getWorkID());
			}
			else
			{
				try {
					dtlEn.Update();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	/**
	 检查是否填写审核意见

	 @return
	 */
	private boolean CheckFrmIsFullCheckNote() throws Exception {
		return true;
		//检查是否写入了审核意见.
		// if (this.getHisNode().getFrmWorkCheckSta() == FrmWorkCheckSta.Enable)
		// {
		// 	/*检查审核意见 */
		// 	String sql = "SELECT Msg \"Msg\",EmpToT \"EmpToT\" FROM ND" + Integer.parseInt(this.getHisNode().getFlowNo()) + "Track WHERE  EmpFrom='" + getWebUser().getNo() + "' AND NDFrom=" + this.getHisNode().getNodeID() + " AND WorkID=" + this.getWorkID() + " AND ActionType=" + ActionType.WorkCheck.getValue();
		// 	DataTable dt = DBAccess.RunSQLReturnTable(sql);
		// 	if (dt.Rows.size() <= 0)
		// 	{
		// 		//throw new RuntimeException("err@请为[" + this.getHisNode().getName() + "]填写审核意见.");
		// 	}

		// 	if (DataType.IsNullOrEmpty(dt.Rows.get(0).getValue(0).toString()) == true)
		// 	{
		// 		//throw new RuntimeException("err@节点[" + this.getHisNode().getName() + "]审核意见不能为空.");
		// 	}
		// }
	}
	/**
	 检查独立表单上必须填写的项目.

	 @return
	 */
	public final boolean CheckFrmIsNotNull() throws Exception {
		//if (this.getHisNode().HisFormType != NodeFormType.SheetTree)
		//    return true;
		//判断绑定的树形表单
		//增加节点表单的必填项判断.
		String err = "";
		if (this.getHisNode().getHisFormType() == NodeFormType.SheetTree)
		{
			//获取绑定的表单.
			String frms = this.getHisGenerWorkFlow().getParasFrms();
			FrmNodes nds = null;
			if (DataType.IsNullOrEmpty(frms) == false)
			{
				//设置前置导航，选择表单的操作
				frms = "'" + frms.replace(",", "','") + "'";
				nds = new FrmNodes();
				QueryObject qury = new QueryObject(nds);
				qury.AddWhere(FrmNodeAttr.FK_Flow, this.getHisNode().getFlowNo());
				qury.addAnd();
				qury.AddWhere(FrmNodeAttr.FK_Node, this.getHisNode().getNodeID());
				qury.addAnd();
				qury.AddWhere(FrmNodeAttr.FK_Frm, "In", "(" + frms + ")");
				qury.addOrderBy(FrmNodeAttr.Idx);
				qury.DoQuery();

			}
			else
			{
				nds = new FrmNodes(this.getHisNode().getFlowNo(), this.getHisNode().getNodeID());
			}

			for (FrmNode item : nds.ToJavaList())
			{
				if (item.getFrmEnableRole() == FrmEnableRole.Disable)
				{
					continue;
				}

				if (item.getHisFrmType() != FrmType.FoolForm && item.getHisFrmType() != FrmType.Develop)
				{
					continue;
				}

				if (item.getFrmSln() == FrmSln.Readonly)
				{
					continue;
				}

				MapData md = new MapData();
				md.setNo(item.getFrmID());
				md.Retrieve();
				if (md.getHisFrmType() != FrmType.FoolForm && md.getHisFrmType() != FrmType.Develop)
				{
					continue;
				}

				//判断WhoIsPK
				long pkVal = this.getWorkID();
				if (item.getWhoIsPK() == WhoIsPK.FID)
				{
					pkVal = this.getHisGenerWorkFlow().getFID();
				}
				if (item.getWhoIsPK() == WhoIsPK.PWorkID)
				{
					pkVal = this.getHisGenerWorkFlow().getPWorkID();
				}
				if (item.getWhoIsPK() == WhoIsPK.P2WorkID)
				{
					GenerWorkFlow gwf = new GenerWorkFlow(this.getHisGenerWorkFlow().getPWorkID());
					if (gwf != null && gwf.getPWorkID() != 0)
					{
						pkVal = gwf.getPWorkID();
					}
				}
				if (item.getWhoIsPK() == WhoIsPK.P3WorkID)
				{
					String sql = "SELECT PWorkID FROM WF_GenerWorkFlow Where WorkID=(SELECT PWorkID FROM WF_GenerWorkFlow WHERE WorkID=" + this.getHisGenerWorkFlow().getPWorkID() + ")";
					pkVal = DBAccess.RunSQLReturnValInt(sql, 0);
				}


				MapAttrs mapAttrs = md.getMapAttrs();
				//主表实体.
				GEEntityOID en = new GEEntityOID(item.getFrmID());
				en.setOID(pkVal);
				int i = en.RetrieveFromDBSources();
				if (i == 0)
				{
					continue;
				}

				Row row = en.getRow();
				if (item.getFrmSln() == FrmSln.Self)
				{
					// 查询出来自定义的数据.
					FrmFields ffs1 = new FrmFields();
					ffs1.Retrieve(FrmFieldAttr.FK_Node, this.getHisNode().getNodeID(), FrmFieldAttr.FrmID, md.getNo(), null);
					//获取整合后的mapAttrs
					for (FrmField frmField : ffs1.ToJavaList())
					{
						for (MapAttr mapAttr : mapAttrs.ToJavaList())
						{
							if (frmField.getKeyOfEn().equals(mapAttr.getKeyOfEn()))
							{
								mapAttr.setUIIsInput(frmField.getItIsNotNull());
								break;
							}
						}
					}
				}

				//String frmErr = "";
				for (MapAttr mapAttr : mapAttrs.ToJavaList())
				{
					if (mapAttr.getUIIsInput()== false)
					{
						continue;
					}

					String str = row.get(mapAttr.getKeyOfEn()) == null ? "" : row.get(mapAttr.getKeyOfEn()).toString();
					/*如果是检查不能为空 */
					if (str == null || DataType.IsNullOrEmpty(str) == true || Objects.equals(str.trim(), ""))
					{
						err += bp.wf.Glo.multilingual("@表单【{0}】字段{1},【{2}】不能为空.", "WorkNode", "form_field_must_not_be_null_1", md.getName(), mapAttr.getKeyOfEn(), mapAttr.getName());
					}
				}
				//  if (DataType.IsNullOrEmpty(frmErr)==false)
				//    err+=" @表单："+md.getName()
			}

			if (!err.equals(""))
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("err@提交前检查到如下必填字段填写不完整:{0}.", "WorkNode", "detected_error", err));
			}

			return true;
		}

		if (this.getHisNode().getItIsNodeFrm() == true)
		{
			MapAttrs attrs = this.getHisNode().getMapData().getMapAttrs();
			Row row = this.getHisWork().getRow();
			for (MapAttr attr : attrs.ToJavaList())
			{
				if (attr.getUIIsInput()== false)
				{
					continue;
				}

				String val = row.GetValStrByKey(attr.getKeyOfEn());
				String str = null;
				if (val != null)
				{
					str = val.toString();
				}


				/*如果是检查不能为空 */
				if (DataType.IsNullOrEmpty(str) == true)
				{
					err += bp.wf.Glo.multilingual("@字段{0},{1}不能为空.", "WorkNode", "form_field_must_not_be_null_2", attr.getKeyOfEn(), attr.getName());
				}
			}


			///#region 检查附件个数的完整性. - 该部分代码稳定后，移动到独立表单的检查上去。
			for (FrmAttachment ath : this.getHisWork().getHisFrmAttachments().ToJavaList())
			{

				///#region 增加阅读规则. @祝梦娟.
				if (ath.getReadRole() != 0)
				{
					//查询出来当前的数据.
					GenerWorkerList gwl = new GenerWorkerList();
					gwl.Retrieve(GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.FK_Emp, getWebUser().No, GenerWorkerListAttr.FK_Node, this.getHisNode().getNodeID());

					//获得已经下载或者读取的数据. 格式为: a2e06fbf-2bae-44fb-9176-9a0047751e83,a2e06fbf-we-44fb-9176-9a0047751e83
					String ids = gwl.GetParaString(ath.getNoOfObj());
					if (ids.contains("ALL") == false)
					{
						//获得当前节点的上传附件.
						FrmAttachmentDBs dbs = bp.wf.CCFormAPI.GenerFrmAttachmentDBs(ath, String.valueOf(this.getWorkID()), ath.getMyPK(), this.getWorkID(), 0, 0, false);

						//String sql = "SELECT MyPK,FileName FROM Sys_FrmAttachmentDB WHERE RefPKVal=" + this.getWorkID() + " AND FK_FrmAttachment='" + ath.getMyPK() + "' AND Rec!='" + bp.web.WebUser.getNo() + "'";
						//DataTable dt = DBAccess.RunSQLReturnTable(sql);
						String errFileUnRead = "";
						for (FrmAttachmentDB db : dbs.ToJavaList())
						{
							String guid = db.getMyPK();
							if (ids.contains(guid) == false)
							{
								errFileUnRead += bp.wf.Glo.multilingual("@文件({0})未阅读.", "WorkNode", "document_not_read", db.getFileName());
							}

						}

						//如果有未阅读的文件.
						if (DataType.IsNullOrEmpty(errFileUnRead) == false)
						{
							//未阅读不让其发送.
							if (ath.getReadRole() == 1)
							{
								throw new RuntimeException("err" + bp.wf.Glo.multilingual("@您还有如下文件没有阅读:{0}.", "WorkNode", "you_have_document_not_read", errFileUnRead));
							}

							//未阅读记录日志并让其发送.
							if (ath.getReadRole() == 2)
							{
								//AthUnReadLog log = new AthUnReadLog();
								//log.setMyPK(this.getWorkID() + "_" + this.Hisnode.getNodeID() + "_" + WebUser.getNo());
								//log.Delete();

								//log.FK_Emp = WebUser.getNo();
								//log.FK_EmpDept = WebUser.getDeptNo();
								//log.FK_EmpDeptName = WebUser.getDeptName();
								//log.getFlowNo() = this.getHisNode().getFlowNo();
								//log.getFlowName() = this.HisFlow.Name;

								//log.NodeID = this.Hisnode.getNodeID();
								//log.getFlowName() = this.HisFlow.Name;
								//log.SendDT = DataType.getCurrentDateTime();
								//log.setWorkID(this.getWorkID();

								//log.Insert(); //插入到数据库.

							}
						}
					}
				}

				///#endregion 增加阅读规则.

				if (ath.getUploadFileNumCheck() == UploadFileNumCheck.None)
				{
					continue;
				}

				long pkval = this.getWorkID();
				if (ath.getHisCtrlWay()== AthCtrlWay.FID)
				{
					pkval = this.getHisGenerWorkFlow().getFID();
				}
				if (ath.getHisCtrlWay()== AthCtrlWay.PWorkID)
				{
					pkval = this.getHisGenerWorkFlow().getPWorkID();
				}
				if (ath.getHisCtrlWay()== AthCtrlWay.PWorkID)
				{
					pkval = DBAccess.RunSQLReturnValInt("SELECT PWorkID FROM WF_GenerWorkFlow WHERE WorkID=" + this.getHisGenerWorkFlow().getPWorkID(), 0);
				}
				if (ath.getHisCtrlWay()== AthCtrlWay.P3WorkID)
				{
					pkval = DBAccess.RunSQLReturnValInt("Select PWorkID From WF_GenerWorkFlow Where WorkID=(Select PWorkID From WF_GenerWorkFlow Where WorkID=" + this.getHisGenerWorkFlow().getPWorkID() + ")", 0);
				}

				if (ath.getUploadFileNumCheck() == UploadFileNumCheck.NotEmpty)
				{
					Paras ps = new Paras();
					ps.SQL = "SELECT COUNT(MyPK) as Num FROM Sys_FrmAttachmentDB WHERE NoOfObj=" + SystemConfig.getAppCenterDBVarStr() + "NoOfObj AND RefPKVal=" + SystemConfig.getAppCenterDBVarStr() + "RefPKVal";
					ps.Add("NoOfObj", ath.getNoOfObj(), false);
					ps.Add("RefPKVal", pkval);
					int count = DBAccess.RunSQLReturnValInt(ps);
					if (count == 0)
					{
						err += bp.wf.Glo.multilingual("@您没有上传附件:{0}.", "WorkNode", "not_upload_attachment", ath.getName());
					}

					if (ath.getNumOfUpload() > count)
					{
						err += bp.wf.Glo.multilingual("@您上传的附件数量小于最低上传数量要求.", "WorkNode", "attachment_less_than_required");
					}
				}

				if (ath.getUploadFileNumCheck() == UploadFileNumCheck.EverySortNoteEmpty)
				{


					Paras ps = new Paras();
					ps.SQL = "SELECT COUNT(MyPK) as Num, Sort FROM Sys_FrmAttachmentDB WHERE  NoOfObj=" + SystemConfig.getAppCenterDBVarStr() + "NoOfObj AND RefPKVal=" + SystemConfig.getAppCenterDBVarStr() + "RefPKVal Group BY Sort";
					ps.Add("NoOfObj", ath.getNoOfObj(), false);
					ps.Add("RefPKVal", pkval);

					DataTable dt = DBAccess.RunSQLReturnTable(ps);
					if (dt.Rows.size() == 0)
					{
						err += bp.wf.Glo.multilingual("@您没有上传附件:{0}.", "WorkNode", "not_upload_attachment", ath.getName());
					}


					String sort = ath.getSort().replace(";", ",");
					String[] strs = sort.split("[,]", -1);
					for (String str : strs)
					{
						boolean isHave = false;
						for (DataRow dr : dt.Rows)
						{
							if (Objects.equals(dr.getValue(1).toString(), str))
							{
								isHave = true;
								break;
							}
						}
						if (isHave == false)
						{
							err += bp.wf.Glo.multilingual("@您没有上传附件:{0}.", "WorkNode", "not_upload_attachment", str);
						}
					}
				}
			}

			///#endregion 检查附件个数的完整性.



			///#region 检查图片附件的必填，added by liuxc,2016-11-1
			for (FrmImgAth imgAth : this.getHisNode().getMapData().getFrmImgAths().ToJavaList())
			{
				if (!imgAth.getItIsRequired())
				{
					continue;
				}

				Paras ps = new Paras();
				ps.SQL = "SELECT COUNT(MyPK) as Num FROM Sys_FrmImgAthDB WHERE FK_MapData=" + SystemConfig.getAppCenterDBVarStr() + "FK_MapData AND FK_FrmImgAth=" + SystemConfig.getAppCenterDBVarStr() + "FK_FrmImgAth AND RefPKVal=" + SystemConfig.getAppCenterDBVarStr() + "RefPKVal";
				ps.Add("FK_MapData", "ND" + this.getHisNode().getNodeID(), false);
				ps.Add("FK_FrmImgAth", imgAth.getMyPK(), false);
				ps.Add("RefPKVal", this.getWorkID());
				if (DBAccess.RunSQLReturnValInt(ps) == 0)
				{
					err += bp.wf.Glo.multilingual("@您没有上传图片附件:{0}.", "WorkNode", "not_upload_attachment", imgAth.getCtrlID().toString());
				}

			}

			///#endregion 检查图片附件的必填，added by liuxc,2016-11-1

			if (!Objects.equals(err, ""))
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("err@提交前检查到如下必填字段填写不完整:{0}.", "WorkNode", "detected_error", err));
			}

			CheckFrmIsFullCheckNote();
			return true;
		}

		//查询出来所有的设置。
		FrmFields ffs = new FrmFields();

		QueryObject qo = new QueryObject(ffs);
		qo.AddWhere(FrmFieldAttr.FK_Node, this.getHisNode().getNodeID());
		qo.addAnd();
		qo.addLeftBracket();
		qo.AddWhere(FrmFieldAttr.IsNotNull, 1);
		qo.addOr();
		qo.AddWhere(FrmFieldAttr.IsWriteToFlowTable, 1);
		qo.addRightBracket();
		qo.DoQuery();

		if (ffs.size() == 0)
		{
			return true;
		}

		FrmNodes frmNDs = new FrmNodes(this.getHisNode().getFlowNo(), this.getHisNode().getNodeID());
		err = "";
		for (FrmNode item : frmNDs.ToJavaList())
		{
			MapData md = new MapData(item.getFrmID());

			//可能是url.
			if (md.getHisFrmType() == FrmType.Url)
			{
				continue;
			}

			//如果使用默认方案,就return出去.
			if (item.getFrmSln() == FrmSln.Default)
			{
				continue;
			}

			//检查是否有？
			boolean isHave = false;
			for (FrmField myff : ffs.ToJavaList())
			{
				if (!Objects.equals(myff.getFrmID(), item.getFrmID()))
				{
					continue;
				}
				isHave = true;
				break;
			}
			if (isHave == false)
			{
				continue;
			}

			// 处理主键.
			long pk = 0; // this.getWorkID();

			switch (item.getWhoIsPK())
			{
				case FID:
					pk = this.getHisWork().getFID();
					break;
				case OID:
					pk = this.getHisWork().getOID();
					break;
				case PWorkID:
					pk = this.rptGe.getPWorkID();
					break;
				default:
					throw new RuntimeException(bp.wf.Glo.multilingual("@未判断的类型:{0}.", "WorkNode", "not_found_value", item.getWhoIsPK().toString()));
			}

			if (pk == 0)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@未能获取表单主键.", "WorkNode", "not_found_form_primary_key"));
			}

			//获取表单值
			GEEntityOID en = new GEEntityOID(md.getNo());
			en.setOID(pk);
			if (en.RetrieveFromDBSources() == 0)
			{
				err += bp.wf.Glo.multilingual("@表单{0}没有输入数据.", "WorkNode", "not_found_value", md.getName());
				continue;
			}
			//循环判断数据是否在Work中存在
			for(String keyOfEn : en.getRow().keySet())
			{
				if (this.getHisWork().getRow().contains(keyOfEn) == true)
					this.getHisWork().SetValByKey(keyOfEn, en.GetValStringByKey(keyOfEn));
			}

			// 检查数据是否完整.
			for (FrmField ff : ffs.ToJavaList())
			{
				if (!Objects.equals(ff.getFrmID(), item.getFrmID()))
				{
					continue;
				}

				//获得数据.
				String val = "";
				val = en.GetValStringByKey(ff.getKeyOfEn()); //dt.Rows.get(0).getValue(ff.KeyOfEn).toString();

				if (ff.getItIsNotNull() == true)
				{
					/*如果是检查不能为空 */
					if (DataType.IsNullOrEmpty(val) == true || Objects.equals(val.trim(), ""))
					{
						err += bp.wf.Glo.multilingual("@表单{0}字段{1},{2}不能为空.", "WorkNode", "form_field_must_not_be_null_1", md.getName(), ff.getKeyOfEn(), ff.getName());
					}

				}

				//判断是否需要写入流程数据表.
				if (ff.getItIsWriteToFlowTable() == true)
				{
					this.getHisWork().SetValByKey(ff.getKeyOfEn(), val);
					//this.rptGe.SetValByKey(ff.KeyOfEn, val);
				}
			}
		}
		if (!Objects.equals(err, ""))
		{
			throw new RuntimeException(bp.wf.Glo.multilingual("@提交前检查到如下必填字段填写不完整({0}).", "WorkNode", "not_found_value", err));
		}

		return true;
	}
	/**
	 copy表单树的数据

	 @return
	 */
	public final Work CopySheetTree() throws Exception {
		if (this.getHisNode().getHisFormType() != NodeFormType.SheetTree && this.getHisNode().getHisFormType() != NodeFormType.RefOneFrmTree)
		{
			return null;
		}

		//查询出来所有的设置。
		FrmFields ffs = new FrmFields();
		QueryObject qo = new QueryObject(ffs);
		qo.AddWhere(FrmFieldAttr.FK_Node, this.getHisNode().getNodeID());
		qo.addAnd();
		qo.AddWhere(FrmFieldAttr.IsWriteToFlowTable, 1);
		qo.DoQuery();
		if (ffs.size() == 0)
		{
			return null;
		}
		FrmNodes frmNDs = new FrmNodes(this.getHisNode().getFlowNo(), this.getHisNode().getNodeID());
		String err = "";
		for (FrmNode item : frmNDs.ToJavaList())
		{
			MapData md = new MapData(item.getFrmID());

			//可能是url.
			if (md.getHisFrmType() == FrmType.Url)
			{
				continue;
			}

			//检查是否有？
			boolean isHave = false;
			for (FrmField myff : ffs.ToJavaList())
			{
				if (!Objects.equals(myff.getFrmID(), item.getFrmID()))
				{
					continue;
				}
				isHave = true;
				break;
			}

			if (isHave == false)
			{
				continue;
			}

			// 处理主键.
			long pk = 0; // this.getWorkID();

			switch (item.getWhoIsPK())
			{
				case FID:
					try {
						pk = this.getHisWork().getFID();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					break;
				case OID:
					pk = this.getHisWork().getOID();
					break;
				case PWorkID:
					if (this.rptGe == null)
					{
						try {
							this.rptGe = new GERpt("ND" + Integer.parseInt(this.getHisFlow().getNo()) + "Rpt", this.getWorkID());
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
					pk = this.rptGe.getPWorkID();
					break;
				case P2WorkID:
					//获取P2WorkID
					GenerWorkFlow gwf = new GenerWorkFlow(this.getHisGenerWorkFlow().getPWorkID());
					if (gwf != null && gwf.getPWorkID() != 0)
					{
						pk = gwf.getPWorkID();
					}
					break;
				case P3WorkID:
					String sql = "Select PWorkID From WF_GenerWorkFlow Where WorkID=(Select PWorkID From WF_GenerWorkFlow Where WorkID=" + this.getHisGenerWorkFlow().getPWorkID() + ")";
					pk = DBAccess.RunSQLReturnValInt(sql, 0);


					break;
				default:
					throw new RuntimeException(bp.wf.Glo.multilingual("@未判断的类型:{0}.", "WorkNode", "not_found_value", item.getWhoIsPK().toString()));
			}

			if (pk == 0)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@未能获取表单主键.", "WorkNode", "not_found_form_primary_key"));
			}

			//获取表单值
			ps = new Paras();
			ps.SQL = "SELECT * FROM " + md.getPTable() + " WHERE OID=" + ps.getDBStr() + "OID";
			ps.Add(WorkAttr.OID, pk);
			DataTable dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() == 0)
			{
				continue;
			}

			// 检查数据是否完整.
			for (FrmField ff : ffs.ToJavaList())
			{
				if (!Objects.equals(ff.getFrmID(), item.getFrmID()))
				{
					continue;
				}

				if (dt.Columns.contains(ff.getKeyOfEn()) == false)
				{
					continue;
				}

				//获得数据.
				String val = "";
				val = dt.Rows.get(0).getValue(ff.getKeyOfEn()).toString();
				this.getHisWork().SetValByKey(ff.getKeyOfEn(), val);
			}
		}

		return this.getHisWork();
	}
	/**
	 执行抄送
	 */
	public final void DoCC()
	{
	}
	/**
	 通知主持人

	 @return
	 */
	private String DealAlertZhuChiRen(String huiQianZhuChiRen) throws Exception {
		/*有两个待办，就说明当前人员是最后一个会签人，就要把主持人的状态设置为 0 */
		//获得主持人信息.
		GenerWorkerList gwl = new GenerWorkerList();
		int i = gwl.Retrieve(GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.FK_Emp, huiQianZhuChiRen, GenerWorkerListAttr.IsPass, 90);
		if (i != 1)
		{
			return bp.wf.Glo.multilingual("@您已经会签完毕.", "WorkNode", "you_have_finished");
		}

		gwl.setPassInt(0); //从会签列表里移动到待办.
		gwl.setItIsRead(false); //设置为未读.

		String str1 = bp.wf.Glo.multilingual("@工作会签完毕.", "WorkNode", "you_have_finished");
		String str2 = bp.wf.Glo.multilingual("@{0}工作已经完成,请到待办列表查看.", "WorkNode", "you_have_finished_todo", this.getHisGenerWorkFlow().getTitle());
		bp.wf.Dev2Interface.Port_SendMsg(gwl.getEmpNo(), str1, str2, "HuiQian" + this.getWorkID() + "_" + getWebUser().No, "HuiQian", getHisGenerWorkFlow().getFlowNo(), this.getHisGenerWorkFlow().getNodeID(), this.getWorkID(), 0);

		//设置为未读.
		bp.wf.Dev2Interface.Node_SetWorkUnRead(this.getHisGenerWorkFlow().getWorkID());

		//设置最后处理人.
		this.getHisGenerWorkFlow().setTodoEmps(gwl.getEmpNo() + "," + gwl.getEmpName() + ";");
		try {
			this.getHisGenerWorkFlow().Update();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}


		///#region 处理天业集团对主持人的考核.
		/*
		 * 对于会签人的时间计算
		 * 1, 从主持人接收工作时间点起，到最后一个一次分配会签人止，作为第一时间段。
		 * 2，所有会签人会签完毕后到会签人执行发送时间点止作为第2个时间段。
		 * 3，第1个时间端+第2个时间段为主持人所处理该工作的时间，时效考核的内容按照这个两个时间段开始计算。
		 */
		if (this.getHisNode().getHisCHWay() == CHWay.ByTime)
		{
			/*如果是按照时效考核.*/

			//获得最后一次执行会签的时间点.
			String sql = "SELECT RDT FROM ND" + Integer.parseInt(this.getHisNode().getFlowNo()) + "TRACK WHERE WorkID=" + this.getWorkID() + " AND ActionType=30 ORDER BY RDT";
			String lastDTOfHuiQian = DBAccess.RunSQLReturnStringIsNull(sql, null);

			//取出来下达给主持人的时间点.
			String dtOfToZhuChiRen = gwl.getRDT();

			//获得两个时间间隔.
			Date t_lastDTOfHuiQian = DataType.ParseSysDate2DateTime(lastDTOfHuiQian);
			Date t_dtOfToZhuChiRen = DataType.ParseSysDate2DateTime(dtOfToZhuChiRen);

			long ts = (t_lastDTOfHuiQian.getTime() - t_dtOfToZhuChiRen.getTime()) / (1000 * 60);

			//生成该节点设定的 时间范围.
			int hour = this.getHisNode().getTimeLimit() * 24 + this.getHisNode().getTimeLimitHH();
			// int.Parse(this.HisNode.TSpanHour.ToString());
			long tsLimt = hour * 60 + this.getHisNode().getTimeLimitMM() * 60;

			//获得剩余的时间范围.
			int myLeftTS = (int) (tsLimt - ts) / 60;

			//计算应该完成的日期.
			Date dtNow = new Date();
			dtNow = DateUtils.addHours(dtNow, myLeftTS);

			//设置应该按成的日期.
			if (this.getHisNode().getHisCHWay() == CHWay.None)
			{
				gwl.setSDT("无");
			}
			else
			{
				gwl.setSDT(DataType.SysDateTimeFormat(dtNow));
			}

			//设置预警日期, 为了方便提前1天预警.
			dtNow = DateUtils.addHours(dtNow, -1);
			gwl.setDTOfWarning(DataType.SysDateTimeFormat(dtNow));
		}

		///#endregion 处理天业集团对会签人的考核.

		gwl.Update();

		return bp.wf.Glo.multilingual("您是最后一个会签该工作的处理人，已经提醒主持人({0}, {1})处理当前工作.", "WorkNode", "you_are_the_last_operator", gwl.getEmpNo(), gwl.getEmpName());
	}
	/**
	 如果是协作.

	 @return 是否执行到最后一个人？
	 */
	public final boolean DealTeamUpNode() throws Exception {
		GenerWorkerLists gwls = new GenerWorkerLists();
		gwls.Retrieve(GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.FK_Node, this.getHisNode().getNodeID(), null);

		if (gwls.size() == 1)
		{
			return false; //让其向下执行,因为只有一个人,就没有顺序的问题.
		}
		int xzoRole = this.getHisNode().GetParaInt("XieZuoOverRole"); //协作完成规则
		BigDecimal xzoRate = new BigDecimal(this.getHisNode().GetParaFloat("XieZuoOverRate")); //协作完成率
		//审核组件的立场-完成率
		if(xzoRole == 1){
			//获取审核通过的审核立场的数据
			int agreeCount = 0;
			int noCount = 0;
			for(GenerWorkerList gwl : gwls.ToJavaList()){
				if(gwl.getEmpNo().equals(WebUser.getNo()) || gwl.getPassInt() == 1){
					String fwcView = gwl.GetParaString("FWCView","");
					if(DataType.IsNullOrEmpty(fwcView))
						continue;
					if(fwcView.equals("1"))
						agreeCount++;
					else
						noCount++;
				}
			}
			BigDecimal agreeRate = new BigDecimal(agreeCount).divide(new BigDecimal(gwls.size()),4,BigDecimal.ROUND_CEILING).multiply(new BigDecimal(100));
			BigDecimal nodeRate =new BigDecimal(noCount).divide(new BigDecimal(gwls.size()),4,BigDecimal.ROUND_CEILING).multiply(new BigDecimal(100));
			if(agreeRate.compareTo(xzoRate)>=0|| nodeRate.compareTo(new BigDecimal(100).subtract(xzoRate))>=0){
				String huiqianNo = this.getHisGenerWorkFlow().getHuiQianZhuChiRen();
				String huiqianName = this.getHisGenerWorkFlow().getHuiQianZhuChiRenName();

				this.getHisGenerWorkFlow().setSender(huiqianNo + "," + huiqianName + ";");
				this.getHisGenerWorkFlow().setTodoEmpsNum(1);
				this.getHisGenerWorkFlow().setTodoEmps(getWebUser().getName() +";");
				this.getHisGenerWorkFlow().setHuiQianTaskSta(HuiQianTaskSta.None);
				return false;
			}
		}
		//按照完成数
		if(xzoRole == 2){
			//获取完成数量
			Entities ens = gwls.GetEntitiesByKey(GenerWorkerListAttr.IsPass,"1");
			if(ens!=null){
				BigDecimal passRate = new BigDecimal(ens.size()+1).divide(new BigDecimal(gwls.size()),4,BigDecimal.ROUND_CEILING).multiply(new BigDecimal(100));
				if (xzoRate.compareTo(passRate) <= 0){
					String huiqianNo = this.getHisGenerWorkFlow().getHuiQianZhuChiRen();
					String huiqianName = this.getHisGenerWorkFlow().getHuiQianZhuChiRenName();

					this.getHisGenerWorkFlow().setSender(huiqianNo + "," + huiqianName + ";");
					this.getHisGenerWorkFlow().setTodoEmpsNum(1);
					this.getHisGenerWorkFlow().setTodoEmps(getWebUser().getName() +";");
					this.getHisGenerWorkFlow().setHuiQianTaskSta(HuiQianTaskSta.None);
					return false;
				}
			}
		}
		//出现指定的人员
		if(xzoRole == 3){
			String emps = this.getHisNode().GetParaString("XieZuoOverRate");
			if(DataType.IsNullOrEmpty(emps)==false && (","+emps+",").contains(","+WebUser.getNo()+",")==true){
				String huiqianNo = this.getHisGenerWorkFlow().getHuiQianZhuChiRen();
				String huiqianName = this.getHisGenerWorkFlow().getHuiQianZhuChiRenName();

				this.getHisGenerWorkFlow().setSender(huiqianNo + "," + huiqianName + ";");
				this.getHisGenerWorkFlow().setTodoEmpsNum(1);
				this.getHisGenerWorkFlow().setTodoEmps(getWebUser().getName() +";");
				this.getHisGenerWorkFlow().setHuiQianTaskSta(HuiQianTaskSta.None);
				return false;
			}
		}
		//出现指定的所有人员账号
		if (xzoRole == 4)
		{
			String emps = ","+this.getHisNode().GetParaString("XieZuoOverRate")+",";

			String[] empArr = this.getHisNode().GetParaString("XieZuoOverRate").split(",");
			int countPass = 0;
			for(GenerWorkerList gwl : gwls.ToJavaList())
			{
				if (gwl.getPassInt() == 1 && emps.contains(","+gwl.getEmpNo()+","))
				{
					countPass++;
				}
			}
			if( emps.contains(","+WebUser.getNo()+","))
				countPass++;
			if (countPass==empArr.length)
			{
				String huiqianNo = this.getHisGenerWorkFlow().getHuiQianZhuChiRen();
				String huiqianName = this.getHisGenerWorkFlow().getHuiQianZhuChiRenName();

				this.getHisGenerWorkFlow().setSender(huiqianNo + "," + huiqianName + ";");
				this.getHisGenerWorkFlow().setTodoEmpsNum(1);
				this.getHisGenerWorkFlow().setTodoEmps(getWebUser().getName() +";");
				this.getHisGenerWorkFlow().setHuiQianTaskSta(HuiQianTaskSta.None);
				return false;
			}

		}

		//查看是否我是最后一个？
		int num = 0;
		String todoEmps = ""; //记录没有处理的人.
		String todoNos = "";
		for (GenerWorkerList item : gwls.ToJavaList())
		{
			if (item.getPassInt() == 0 || item.getPassInt() == 90)
			{
				if (item.getEmpNo().equals(getWebUser().getNo()) == false)
				{
					todoEmps += bp.wf.Glo.DealUserInfoShowModel(item.getEmpNo(), item.getEmpName()) + " ";
					todoNos += item.getEmpNo() + ";";
				}

				num++;
			}
		}

		if (num == 1)
		{
			if (this.getHisGenerWorkFlow().getHuiQianTaskSta() == HuiQianTaskSta.None)
			{
				this.getHisGenerWorkFlow().setSender(getWebUser().getNo() + "," + getWebUser().getName() +";");
				this.getHisGenerWorkFlow().setTodoEmpsNum(1);
				this.getHisGenerWorkFlow().setTodoEmps(getWebUser().getName() +";");
			}
			else
			{
				String huiqianNo = this.getHisGenerWorkFlow().getHuiQianZhuChiRen();
				String huiqianName = this.getHisGenerWorkFlow().getHuiQianZhuChiRenName();

				this.getHisGenerWorkFlow().setSender(huiqianNo + "," + huiqianName + ";");
				this.getHisGenerWorkFlow().setTodoEmpsNum(1);
				this.getHisGenerWorkFlow().setTodoEmps(getWebUser().getName() +";");
				this.getHisGenerWorkFlow().setHuiQianTaskSta(HuiQianTaskSta.None);
			}

			return false; //只有一个待办,说明自己就是最后的一个人.
		}

		//把当前的待办设置已办，并且提示未处理的人。
		for (GenerWorkerList gwl : gwls.ToJavaList())
		{
			if (gwl.getEmpNo().equals(getWebUser().getNo()) == false)
			{
				continue;
			}

			//设置当前不可以用.
			gwl.setPassInt(1);
			gwl.Update();

			// 检查完成条件。
			if (this.getHisNode().getItIsEndNode() == false)
			{
				this.CheckCompleteCondition();
			}

			//判断当前人是否被加签人
			String HuiQianZhuChiRen = gwl.GetParaString("HuiQianZhuChiRen","");
			ActionType actionType = ActionType.TeampUp;
			if(DataType.IsNullOrEmpty(HuiQianZhuChiRen)==true)
				actionType = ActionType.Forward;
			//写入日志.
			if (this.getHisGenerWorkFlow().getHuiQianTaskSta() != HuiQianTaskSta.None)
			{
				this.AddToTrack(actionType, todoNos, todoEmps, this.getHisNode().getNodeID(), this.getHisNode().getName(), bp.wf.Glo.multilingual("会签", "WorkNode", "cross_signing"));
			}
			else
			{
				this.AddToTrack(actionType, todoNos, todoEmps, this.getHisNode().getNodeID(), this.getHisNode().getName(), bp.wf.Glo.multilingual("协作发送", "WorkNode", "cross_signing"));
			}

			//替换人员信息.
			String emps = this.getHisGenerWorkFlow().getTodoEmps();

			emps = emps.replace(getWebUser().getNo() + "," + getWebUser().getName() +";", "");
			emps = emps.replace(getWebUser().getNo() + "," + getWebUser().getName(), "");

			this.getHisGenerWorkFlow().setTodoEmps(emps);

			//处理会签问题
			this.addMsg(SendReturnMsgFlag.OverCurr, bp.wf.Glo.multilingual("@您已经完成签完工作. 当前未处理会签工作的人还有:{0}.", "WorkNode", "you_have_finished_1", todoEmps), null, SendReturnMsgType.Info);

			return true;
		}

		throw new RuntimeException("@不应该运行到这里，DealTeamUpNode。当前登录人员[" + getWebUser().getNo() + "," + getWebUser().getName() +"],请确认人员信息.WorkID=" + this.getWorkID());
	}
	/**
	 如果是协作
	 */
	public final boolean DealTeamupGroupLeader() throws Exception {
		GenerWorkerLists gwls = new GenerWorkerLists();
		gwls.Retrieve(GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.FK_Node, this.getHisNode().getNodeID(), GenerWorkerListAttr.IsPass);

		if (gwls.size() == 1)
		{
			return false; //让其向下执行,因为只有一个人,就没有顺序的问题.
		}


		///#region  判断自己是否是组长？如果是组长，就让返回false, 让其运动到最后一个节点，因为组长同意了，就全部同意了。
		if (this.getHisNode().getTeamLeaderConfirmRole() == TeamLeaderConfirmRole.ByDeptFieldLeader)
		{
			String sql = "SELECT COUNT(No) AS num FROM Port_Dept WHERE Leader='" + getWebUser().getNo() + "'";
			if (DBAccess.RunSQLReturnValInt(sql, 0) == 1)
			{
				return false;
			}
		}

		if (this.getHisNode().getTeamLeaderConfirmRole() == TeamLeaderConfirmRole.BySQL)
		{
			String sql = this.getHisNode().getTeamLeaderConfirmDoc();
			sql = bp.wf.Glo.DealExp(sql, this.getHisWork());
			sql = sql.replace("~", "'");
			sql = sql.replace("@WorkID", String.valueOf(this.getWorkID()));
			DataTable dt = DBAccess.RunSQLReturnTable(sql);

			String userNo = getWebUser().No;
			for (DataRow dr : dt.Rows)
			{
				String str = dr.getValue(0) instanceof String ? (String)dr.getValue(0) : null;
				if (Objects.equals(str, userNo))
				{
					return false;
				}
			}
			//获取未处理的待办人员
			String todoEmpNo = "";
			String todoEmpName = "";
			for (GenerWorkerList gwl : gwls.ToJavaList())
			{
				if (gwl.getEmpNo().equals(getWebUser().getNo()) == false)
				{
					continue;
				}
				if ((gwl.getPassInt() == 0 || gwl.getPassInt() == 90) && (gwl.getEmpNo().equals(getWebUser().getNo()) == false))
				{
					todoEmpName += bp.wf.Glo.DealUserInfoShowModel(gwl.getEmpNo(), gwl.getEmpName()) + ";";
					todoEmpNo += gwl.getEmpNo() + ";";
				}
			}
			//把当前的待办设置已办
			for (GenerWorkerList gwl : gwls.ToJavaList())
			{
				if (gwl.getEmpNo().equals(getWebUser().getNo()) == false)
				{
					continue;
				}

				//设置当前已经完成.
				gwl.setPassInt(1);
				gwl.Update();

				//调用发送成功事件.
				String sendSuccess = ExecEvent.DoNode(EventListNode.SendSuccess, this, this.HisMsgObjs, null);
				this.HisMsgObjs.AddMsg("info21", sendSuccess, sendSuccess, SendReturnMsgType.Info);

				//执行时效考核.
				if (this.rptGe == null)
				{
					Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), this.rptGe.getFID(), this.rptGe.getTitle(), gwl);
				}
				else
				{
					Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), 0, this.getHisGenerWorkFlow().getTitle(), gwl);
				}

				this.AddToTrack(ActionType.TeampUp, todoEmpNo, todoEmpName, this.getHisNode().getNodeID(), this.getHisNode().getName(), "多人处理规则：协作组长模式");
			}
			this.addMsg(SendReturnMsgFlag.CondInfo, bp.wf.Glo.multilingual("@当前工作未处理的人还有: {0},所以不能发送到下一步.", "WorkNode", "you_have_finished_1", todoEmpName), null, SendReturnMsgType.Info);
			return true;
		}

		if (this.getHisNode().getTeamLeaderConfirmRole() == TeamLeaderConfirmRole.HuiQianLeader)
		{
			//当前人员的流程处理信息
			GenerWorkerList gwlOfMe = new GenerWorkerList();
			gwlOfMe.Retrieve(GenerWorkerListAttr.FK_Emp, getWebUser().No, GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.FK_Node, this.getHisNode().getNodeID());
			String myhqzcr = gwlOfMe.GetParaString("HuiQianZhuChiRen");
			String myhqType = gwlOfMe.GetParaString("HuiQianType");
			myhqType = DataType.IsNullOrEmpty(myhqType) == true ? "" : myhqType;

			//只有一个组长的模式
			if (this.getHisNode().getHuiQianLeaderRole() == HuiQianLeaderRole.OnlyOne)
			{
				/* 当前人是组长，检查是否可以可以发送,检查自己是否是最后一个人 ？ */
				if (this.getHisGenerWorkFlow().getTodoEmps().contains(getWebUser().getNo() + ",") == true && DataType.IsNullOrEmpty(myhqzcr) == true)
				{
					String todoEmps = ""; // 记录没有处理的人.
					int num = 0;
					for (GenerWorkerList item : gwls.ToJavaList())
					{
						if (item.getPassInt() == 0 || item.getPassInt() == 90)
						{
							if (item.getEmpNo().equals(getWebUser().getNo()) == false)
							{
								todoEmps += bp.wf.Glo.DealUserInfoShowModel(item.getEmpNo(), item.getEmpName()) + " ";
							}
							num++;
						}
					}

					if (num == 1)
					{
						this.getHisGenerWorkFlow().setSender(bp.wf.Glo.DealUserInfoShowModel(getWebUser().No, getWebUser().getName()) + ";");
						this.getHisGenerWorkFlow().setHuiQianTaskSta(HuiQianTaskSta.None);
						this.getHisGenerWorkFlow().setHuiQianZhuChiRen("");
						this.getHisGenerWorkFlow().setHuiQianZhuChiRenName("");
						return false; // 只有一个待办,说明自己就是最后的一个人.
					}

					this.addMsg(SendReturnMsgFlag.CondInfo, "@当前工作未处理的会签人有: " + todoEmps + ",您不能执行发送.", null, SendReturnMsgType.Info);
					return true;
				}
			}
			//任意组长都可以发发送，只要该组长加签的人已经处理完，他点击发送其他人的待办消失
			if (this.getHisNode().getHuiQianLeaderRole() == HuiQianLeaderRole.EveryOneMain)
			{
				/* 当前人是组长，检查是否可以可以发送,检查自己是不是最后一个待办处理人 ？*/
				if (this.getHisGenerWorkFlow().getTodoEmps().contains(getWebUser().getNo() + ",") == true && (DataType.IsNullOrEmpty(myhqzcr) == true || myhqType.equals("AddLeader") == true))
				{
					String todoEmps = ""; // 记录没有处理的人.
					String hqzcr = "";
					String hqType = "";
					int num = 0;
					for (GenerWorkerList item : gwls.ToJavaList())
					{
						//主持人
						hqzcr = item.GetParaString("HuiQianZhuChiRen");
						hqzcr = DataType.IsNullOrEmpty(hqzcr) == true ? "" : hqzcr;
						//加签的类型 普通人，主持人
						hqType = item.GetParaString("HuiQianType");
						hqType = DataType.IsNullOrEmpty(hqType) == true ? "" : hqType;

						if ((item.getPassInt() == 0 || item.getPassInt() == 90) && (item.getEmpNo().equals(getWebUser().getNo()) || hqzcr.equals(getWebUser().getNo()) && hqType.equals("AddLeader") == false))
						{
							if (item.getEmpNo().equals(getWebUser().getNo()) == false)
							{
								todoEmps += bp.wf.Glo.DealUserInfoShowModel(item.getEmpNo(), item.getEmpName()) + " ";
							}
							num++;
						}
					}
					//说明当前自己加签的人员已经处理完成，自己是最后一个人
					if (num == 1)
					{
						//删除其他人的待办信息
						String sql = "UPDATE  WF_GenerWorkerlist  Set IsPass=1 WHERE WorkID=" + this.getWorkID() + " AND FK_Node=" + this.getHisNode().getNodeID() + " AND IsPass=0 AND FK_Emp!='" + getWebUser().getNo() + "'";
						DBAccess.RunSQL(sql);
						this.getHisGenerWorkFlow().setSender(bp.wf.Glo.DealUserInfoShowModel(getWebUser().No, getWebUser().getName()) + ";");
						this.getHisGenerWorkFlow().setHuiQianTaskSta(HuiQianTaskSta.None);
						this.getHisGenerWorkFlow().setHuiQianZhuChiRen("");
						this.getHisGenerWorkFlow().setHuiQianZhuChiRenName("");
						return false;
					}

					this.addMsg(SendReturnMsgFlag.CondInfo, "@当前工作未处理的会签人有: " + todoEmps + ",您不能执行发送.", null, SendReturnMsgType.Info);
					return true;
				}
			}

			//最后一个组长可以发发送
			if (this.getHisNode().getHuiQianLeaderRole() == HuiQianLeaderRole.LastOneMain)
			{
				/* 当前人是组长，检查是否可以可以发送,检查自己加签的人是否都已经处理完成 ？*/
				if (this.getHisGenerWorkFlow().getTodoEmps().contains(getWebUser().getNo() + ",") == true && (DataType.IsNullOrEmpty(myhqzcr) == true || myhqType.equals("AddLeader") == true))
				{
					String todoEmps = ""; // 记录没有处理的人.
					String todoNos = "";
					String todohqzcrEmps = ""; //记录未处理的主持人
					String hqzcr = "";
					String hqType = "";
					int num = 0; //自己及自己加签人的待办
					int othernum = 0; //其它组长的待办

					for (GenerWorkerList item : gwls.ToJavaList())
					{
						//主持人
						hqzcr = item.GetParaString("HuiQianZhuChiRen");
						hqzcr = DataType.IsNullOrEmpty(hqzcr) == true ? "" : hqzcr;
						//加签的类型 普通人，主持人
						hqType = item.GetParaString("HuiQianType");
						hqType = DataType.IsNullOrEmpty(hqType) == true ? "" : hqType;

						if (item.getPassInt() == 0 || item.getPassInt() == 90)
						{
							if (item.getEmpNo().equals(getWebUser().getNo()) || hqzcr.equals(getWebUser().getNo()) && hqType.equals("AddLeader") == false)
							{
								if (item.getEmpNo().equals(getWebUser().getNo()) == false)
								{
									todoEmps += bp.wf.Glo.DealUserInfoShowModel(item.getEmpNo(), item.getEmpName()) + ";";
									todoNos += item.getEmpNo() + ";";
								}

								num++;
							}
							if (item.getEmpNo().equals(getWebUser().getNo()) == false && (DataType.IsNullOrEmpty(hqzcr) == true || hqType.equals("AddLeader") == true))
							{
								todohqzcrEmps += bp.wf.Glo.DealUserInfoShowModel(item.getEmpNo(), item.getEmpName()) + " ";
								othernum++;
							}

						}
					}
					//说明当前自己加签的人员已经处理完成，并且是最后一个组长未处理待办
					if (num == 1 && othernum == 0)
					{
						//删除其他人的待办信息
						String sql = "UPDATE  WF_GenerWorkerlist  Set IsPass=1 WHERE WorkID=" + this.getWorkID() + " AND FK_Node=" + this.getHisNode().getNodeID() + " AND IsPass=0 AND FK_Emp!='" + getWebUser().getNo() + "'";
						DBAccess.RunSQL(sql);
						this.getHisGenerWorkFlow().setSender(bp.wf.Glo.DealUserInfoShowModel(getWebUser().No, getWebUser().Name) + ";");
						this.getHisGenerWorkFlow().setHuiQianTaskSta(HuiQianTaskSta.None);
						this.getHisGenerWorkFlow().setHuiQianZhuChiRen("");
						this.getHisGenerWorkFlow().setHuiQianZhuChiRenName("");
						return false;
					}
					//当前组长加签的人员已经处理完，自己的待办可以结束
					if (num == 1 && othernum != 0)
					{
						// 设置当前已经完成.
						gwlOfMe.setPassInt(1);
						gwlOfMe.Update();

						// 检查完成条件。
						if (this.getHisNode().getItIsEndNode() == false)
						{
							this.CheckCompleteCondition();
						}
						// 调用发送成功事件.
						String sendSuccess = ExecEvent.DoNode(EventListNode.SendSuccess, this);
						this.HisMsgObjs.AddMsg("info21", sendSuccess, sendSuccess, SendReturnMsgType.Info);

						// 执行时效考核.
						if (this.rptGe == null)
						{
							Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), this.rptGe.getFID(), this.rptGe.getTitle(), gwlOfMe);
						}
						else
						{
							Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), 0, this.getHisGenerWorkFlow().getTitle(), gwlOfMe);
						}

						this.AddToTrack(ActionType.TeampUp, todoNos, todoEmps, this.getHisNode().getNodeID(), this.getHisNode().getName(), "协作发送");
						String emps = this.getHisGenerWorkFlow().getTodoEmps();
						emps = emps.replace(getWebUser().getName() +";", "");
						this.getHisGenerWorkFlow().setTodoEmps(emps);
						this.getHisGenerWorkFlow().DirectUpdate();
						this.addMsg(SendReturnMsgFlag.CondInfo, "@当前工作未处理的会签人有: " + todohqzcrEmps, null, SendReturnMsgType.Info);
						return true;
					}
					if (DataType.IsNullOrEmpty(todohqzcrEmps) == false)
					{
						this.addMsg(SendReturnMsgFlag.CondInfo, "@当前工作未处理的会签人有: " + todoEmps + ",组长有：" + todohqzcrEmps + ",您不能执行发送.", null, SendReturnMsgType.Info);
					}
					else
					{
						this.addMsg(SendReturnMsgFlag.CondInfo, "@当前工作未处理的会签人有: " + todoEmps + ",您不能执行发送.", null, SendReturnMsgType.Info);
					}
					return true;
				}
			}


			///#region 加签人的处理
			//查看是否我是最后一个？ 主持人必须是相同的人
			int mynum = 0;
			int cnum = 0; //当前加签人所属主持人下的待办数
			String todoEmps1 = ""; //记录没有处理的人.
			String todoEmpNos = "";
			for (GenerWorkerList item : gwls.ToJavaList())
			{
				//主持人
				String hqzcr = item.GetParaString("HuiQianZhuChiRen");
				hqzcr = DataType.IsNullOrEmpty(hqzcr) == true ? "" : hqzcr;
				if (item.getPassInt() == 0 || item.getPassInt() == 90)
				{
					if (item.getEmpNo().equals(getExecer()) == false)
					{
						todoEmps1 += bp.wf.Glo.DealUserInfoShowModel(item.getEmpNo(), item.getEmpName()) + " ";
						todoEmpNos += item.getEmpNo() + ";";
					}

					if (myhqzcr.equals(hqzcr) || item.getEmpNo().equals(myhqzcr))
					{
						cnum++;
					}
					mynum++;
				}

			}

			if (mynum == 1)
			{
				this.getHisGenerWorkFlow().setSender(getWebUser().getNo() + "," + getWebUser().getName() +";");
				this.getHisGenerWorkFlow().setHuiQianTaskSta(HuiQianTaskSta.None);
				this.getHisGenerWorkFlow().setHuiQianZhuChiRen("");
				this.getHisGenerWorkFlow().setHuiQianZhuChiRenName("");
				return false; //只有一个待办,说明自己就是最后的一个人.
			}

			//把当前的待办设置已办，并且提示未处理的人。
			for (GenerWorkerList gwl : gwls.ToJavaList())
			{
				if (gwl.getEmpNo().equals(getExecer()) == false)
				{
					continue;
				}

				//设置当前已经完成.
				gwl.setPassInt(1);
				gwl.Update();

				// 检查完成条件。
				if (this.getHisNode().getItIsEndNode() == false)
				{
					this.CheckCompleteCondition();
				}

				//调用发送成功事件.
				String sendSuccess = ExecEvent.DoNode(EventListNode.SendSuccess, this, this.HisMsgObjs, null);

				this.HisMsgObjs.AddMsg("info21", sendSuccess, sendSuccess, SendReturnMsgType.Info);

				//执行时效考核.
				if (this.rptGe == null)
				{
					Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), this.rptGe.getFID(), this.rptGe.getTitle(), gwl);
				}
				else
				{
					Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), 0, this.getHisGenerWorkFlow().getTitle(), gwl);
				}

				this.AddToTrack(ActionType.TeampUp, todoEmpNos, todoEmps1, this.getHisNode().getNodeID(), this.getHisNode().getName(), "协作发送");

				//cut 当前的人员.
				String emps = this.getHisGenerWorkFlow().getTodoEmps();
				emps = emps.replace(getWebUser().getNo() + "," + getWebUser().getName() +";", "");
				emps = emps.replace(getWebUser().getName() +";", "");
				emps = emps.replace(getWebUser().Name, "");

				this.getHisGenerWorkFlow().setTodoEmps(emps);
				this.getHisGenerWorkFlow().DirectUpdate();

				//处理会签问题，
				if (cnum == 2)
				{
					String msg = this.DealAlertZhuChiRen(myhqzcr);
					this.addMsg(SendReturnMsgFlag.OverCurr, msg, null, SendReturnMsgType.Info);
				}
				else
				{
					this.addMsg(SendReturnMsgFlag.OverCurr, bp.wf.Glo.multilingual("@您已经完成签完工作. 当前未处理会签工作的人还有:{0}.", "WorkNode", "you_have_finished_1", todoEmps1), null, SendReturnMsgType.Info);
				}
				return true;


				///#endregion 加签人的处理

			}

			///#endregion
		}
		throw new RuntimeException("@不应该运行到这里。");
	}
	/**
	 处理队列节点

	 @return 是否可以向下发送?
	 */
	public final boolean DealOradeNode() throws Exception {
		GenerWorkerLists gwls = new GenerWorkerLists();
		gwls.Retrieve(GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.FK_Node, this.getHisNode().getNodeID(), GenerWorkerListAttr.IsPass);

		if (gwls.size() == 1)
		{
			return false; //让其向下执行,因为只有一个人。就没有顺序的问题.
		}

		int idx = -100;
		for (GenerWorkerList gwl : gwls.ToJavaList())
		{
			idx++;
			if (!Objects.equals(gwl.getEmpNo(), getWebUser().getNo()))
			{
				continue;
			}

			//设置当前不可以用. //审核组件显示有问题IsPass设置成1审核通过
			gwl.setPassInt(1);
			gwl.Update();
		}

		for (GenerWorkerList gwl : gwls.ToJavaList())
		{
			if (gwl.getPassInt() > 10)
			{
				/*就开始发到这个人身上. */
				gwl.setPassInt(0);
				gwl.Update();

				// 检查完成条件。
				//if (this.getHisNode().getItIsEndNode() == false)
				//{
				//	this.CheckCompleteCondition();
				//}
				//写入日志.
				this.AddToTrack(ActionType.Order, gwl.getEmpNo(), gwl.getEmpName(), this.getHisNode().getNodeID(), this.getHisNode().getName(), bp.wf.Glo.multilingual("队列发送", "WorkNode", "queue_transferred"));

				this.addMsg(SendReturnMsgFlag.VarAcceptersID, gwl.getEmpNo(), gwl.getEmpNo(), SendReturnMsgType.SystemMsg);
				this.addMsg(SendReturnMsgFlag.VarAcceptersName, gwl.getEmpName(), gwl.getEmpName(), SendReturnMsgType.SystemMsg);
				this.addMsg(SendReturnMsgFlag.OverCurr, bp.wf.Glo.multilingual("@当前工作已经发送给({0},{1}).", "WorkNode", "send_to_the_operator", gwl.getEmpNo(), gwl.getEmpName()), null, SendReturnMsgType.Info);

				//执行更新.
				if (this.getHisGenerWorkFlow().getEmps().contains("@" + getWebUser().getNo() + "," + getWebUser().getName() +"@") == false || this.getHisGenerWorkFlow().getEmps().contains("@" + getWebUser().getNo() + "@") == false)
				{
					this.getHisGenerWorkFlow().setEmps(this.getHisGenerWorkFlow().getEmps() + getWebUser().getNo() + "," + getWebUser().getName() +"@");
				}

				this.rptGe.setFlowEmps(this.getHisGenerWorkFlow().getEmps());
				this.rptGe.setWFState(WFState.Runing);

				this.rptGe.Update(GERptAttr.FlowEmps, this.rptGe.getFlowEmps(), GERptAttr.WFState, WFState.Runing.getValue());


				this.getHisGenerWorkFlow().setWFState(WFState.Runing);
				this.getHisGenerWorkFlow().Update();
				return true;
			}
		}

		// 如果是最后一个，就要他向下发送。
		return false;
	}
	/**
	 检查阻塞模式
	 */
	private void CheckBlockModel() throws Exception {
		if (this.getHisNode().getBlockModel() == BlockModel.None)
		{
			return;
		}

		try
		{
			String blockMsg = this.getHisNode().getBlockAlert();

			if ((this.getHisNode().getBlockAlert() == null || this.getHisNode().getBlockAlert().isEmpty()))
			{
				blockMsg = bp.wf.Glo.multilingual("@符合发送阻塞规则，不能向下发送.", "WorkNode", "cannot_send_to_next");
			}

			if (this.getHisNode().getBlockModel() == BlockModel.CurrNodeAll)
			{
				/*如果设置检查是否子流程结束.*/
				GenerWorkFlows gwls = new GenerWorkFlows();
				if (this.getHisNode().getItIsSubThread() == true)
				{
					/*如果是子流程,仅仅检查自己子流程上发起的workid.*/
					QueryObject qo = new QueryObject(gwls);
					qo.AddWhere(GenerWorkFlowAttr.PWorkID, this.getWorkID());
					qo.addAnd();
					qo.AddWhere(GenerWorkFlowAttr.PNodeID, this.getHisNode().getNodeID());
					qo.addAnd();
					qo.AddWhere(GenerWorkFlowAttr.PFlowNo, this.getHisFlow().getNo());
					qo.addAnd();
					qo.AddWhere(GenerWorkFlowAttr.WFSta, WFSta.Runing.getValue());
					qo.DoQuery();
					if (gwls.size() == 0)
					{
						return;
					}
				}
				else
				{
					/*检查，以前的子线程是否发起过流程 与以前的分子线程节点是否发起过子流程。 */
					QueryObject qo = new QueryObject(gwls);

					qo.addLeftBracket();
					qo.AddWhere(GenerWorkFlowAttr.PFID, this.getWorkID());
					qo.addOr();
					qo.AddWhere(GenerWorkFlowAttr.PWorkID, this.getWorkID());
					qo.addRightBracket();

					qo.addAnd();

					qo.addLeftBracket();
					qo.AddWhere(GenerWorkFlowAttr.PNodeID, this.getHisNode().getNodeID());
					qo.addAnd();
					qo.AddWhere(GenerWorkFlowAttr.PFlowNo, this.getHisFlow().getNo());
					qo.addAnd();
					qo.AddWhere(GenerWorkFlowAttr.WFSta, WFSta.Runing.getValue());
					qo.addRightBracket();

					qo.DoQuery();
					if (gwls.size() == 0)
					{
						return;
					}
				}

				String err = "";
				err += bp.wf.Glo.multilingual("@如下子流程没有完成，你不能向下发送。@---------------------------------", "WorkNode", "cannot_send_to_next_1");
				String wf_id = bp.wf.Glo.multilingual("@流程ID:", "WorkNode", "workflow_id");
				String wf_title = bp.wf.Glo.multilingual(",标题:", "WorkNode", "workflow_title");
				String wf_operator = bp.wf.Glo.multilingual(",当前执行人:", "WorkNode", "current_operator");
				String wf_step = bp.wf.Glo.multilingual(",运行到节点:", "WorkNode", "current_step");
				for (GenerWorkFlow gwf : gwls.ToJavaList())
				{
					err += wf_id + gwf.getWorkID() + wf_title + gwf.getTitle() + wf_operator + gwf.getTodoEmps() + wf_step + gwf.getNodeName();
				}

				err = bp.wf.Glo.DealExp(blockMsg, this.rptGe) + err;
				throw new RuntimeException(err);
			}

			if (this.getHisNode().getBlockModel() == BlockModel.SpecSubFlow)
			{
				/*如果按照特定的格式判断阻塞*/
				String exp = this.getHisNode().getBlockExp();
				if (exp.contains("@") == false)
				{
					throw new RuntimeException(bp.wf.Glo.multilingual("@设置错误，该节点的阻塞配置格式({0})错误，请参考帮助来解决。", "WorkNode", "error_in_param_setting", exp));
				}

				String[] strs = exp.split("[@]", -1);
				String err = "";
				for (String str : strs)
				{
					if (DataType.IsNullOrEmpty(str) == true)
					{
						continue;
					}

					if (str.contains("=") == false)
					{
						throw new RuntimeException(bp.wf.Glo.multilingual("@阻塞设置的格式不正确:{0}.", "WorkNode", "error_in_param_setting", str));
					}

					String[] nodeFlow = str.split("[=]", -1);
					int nodeid = Integer.parseInt(nodeFlow[0]); //启动子流程的节点.
					String subFlowNo = nodeFlow[1];

					GenerWorkFlows gwls = new GenerWorkFlows();

					if (this.getHisNode().getItIsSubThread() == true)
					{
						/* 如果是子线程，就不需要管，主干节点的问题。*/
						QueryObject qo = new QueryObject(gwls);
						qo.AddWhere(GenerWorkFlowAttr.PWorkID, this.getWorkID());
						qo.addAnd();
						qo.AddWhere(GenerWorkFlowAttr.PNodeID, nodeid);
						qo.addAnd();
						qo.AddWhere(GenerWorkFlowAttr.PFlowNo, this.getHisFlow().getNo());
						qo.addAnd();
						qo.AddWhere(GenerWorkFlowAttr.FK_Flow, subFlowNo);
						qo.addAnd();
						qo.AddWhere(GenerWorkFlowAttr.WFSta, WFSta.Runing.getValue());

						qo.DoQuery();
						if (gwls.size() == 0)
						{
							continue;
						}
					}
					else
					{
						/* 非子线程，就需要考虑，从该节点上，发起的子线程的 ，主干节点的问题。*/
						QueryObject qo = new QueryObject(gwls);

						qo.addLeftBracket();
						qo.AddWhere(GenerWorkFlowAttr.PFID, this.getWorkID());
						qo.addOr();
						qo.AddWhere(GenerWorkFlowAttr.PWorkID, this.getWorkID());
						qo.addRightBracket();

						qo.addAnd();

						qo.addLeftBracket();
						qo.AddWhere(GenerWorkFlowAttr.PNodeID, nodeid);
						qo.addAnd();
						qo.AddWhere(GenerWorkFlowAttr.PFlowNo, this.getHisFlow().getNo());
						//qo.addAnd();
						//qo.AddWhere(GenerWorkFlowAttr.WFSta, (int)WFSta.Runing);
						qo.addAnd();
						qo.AddWhere(GenerWorkFlowAttr.FK_Flow, subFlowNo);
						qo.addRightBracket();

						qo.DoQuery();
						if (gwls.size() != 0 && (gwls.get(0) instanceof GenerWorkFlow ? (GenerWorkFlow)gwls.get(0) : null).getWFSta() == WFSta.Complete)
						{
							continue;
						}
					}

					err += bp.wf.Glo.multilingual("@如下子流程没有完成，你不能向下发送。@---------------------------------", "WorkNode", "cannot_send_to_next_1");
					String sub_wf_id = bp.wf.Glo.multilingual("@子流程ID:", "WorkNode", "sub_workflow_id");
					String sub_wf_name = bp.wf.Glo.multilingual(",子流程名称:", "WorkNode", "sub_workflow_title");
					String sub_wf_title = bp.wf.Glo.multilingual(",子流程标题:", "WorkNode", "sub_workflow_title");
					String sub_wf_operator = bp.wf.Glo.multilingual(",当前执行人:", "WorkNode", "current_operator");
					String sub_wf_step = bp.wf.Glo.multilingual(",运行到节点:", "WorkNode", "current_step");

					for (GenerWorkFlow gwf : gwls.ToJavaList())
					{
						err += bp.wf.Glo.multilingual("@子流程ID:{0}", "WorkNode", "sub_workflow_id", String.valueOf(gwf.getWorkID())) + "\n" + bp.wf.Glo.multilingual(",子流程名称:{0}", "WorkNode", "sub_workflow_title", gwf.getFlowName()) + "\n" + bp.wf.Glo.multilingual(",子流程标题:{0}", "WorkNode", "sub_workflow_title", gwf.getTitle()) + "\n" + bp.wf.Glo.multilingual(",当前执行人:{0}", "WorkNode", "current_operator", gwf.getTodoEmps()) + "\n" + bp.wf.Glo.multilingual(",运行到节点:{0}", "WorkNode", "current_step", gwf.getNodeName());
					}
				}

				if (DataType.IsNullOrEmpty(err) == true)
				{
					return;
				}

				err = bp.wf.Glo.DealExp(blockMsg, this.rptGe) + err;
				throw new RuntimeException(err);
			}

			if (this.getHisNode().getBlockModel() == BlockModel.BySQL)
			{

				String sql = this.getHisNode().getBlockExp();
				sql = bp.wf.Glo.DealExp(sql, this.rptGe);

				sql = sql.replace("@WorkID", String.valueOf(this.getWorkID()));
				sql = sql.replace("@OID", String.valueOf(this.getWorkID()));

				/*按 sql 判断阻塞*/
				BigDecimal d = DBAccess.RunSQLReturnValDecimal(Glo.DealExp(sql, this.rptGe), BigDecimal.valueOf(0), 1);
				//如果值大于0进行阻塞
				if (d.compareTo(BigDecimal.valueOf(0)) > 0)
				{
					throw new RuntimeException("@" + Glo.DealExp(blockMsg, this.rptGe));
				}
				return;
			}

			if (this.getHisNode().getBlockModel() == BlockModel.ByExp)
			{
				/*按表达式阻塞. 格式为: @ABC=123 */
				//this.MsgOfCond = "@以表单值判断方向，值 " + en.getEnDesc() + "." + this.AttrKey + " (" + en.GetValStringByKey(this.AttrKey) + ") 操作符:(" + this.FK_Operator + ") 判断值:(" + this.OperatorValue.ToString() + ")";
				String exp = this.getHisNode().getBlockExp();
				String[] strs = exp.trim().split("[ ]", -1);

				String key = StringHelper.trimStart(strs[0].trim(), '@');
				String oper = strs[1].trim();
				String val = strs[2].trim();
				val = val.replace("'", "");
				val = val.replace("%", "");
				val = val.replace("~", "");
				Row row = this.rptGe.getRow();
				String valPara = null;
				if (row.containsKey(key) == false)
				{
					try
					{
						boolean isHave = false;
						if (SystemConfig.isBSsystem() == true)
						{
							for (String param : ContextHolderUtils.getRequest().getParameterMap().keySet())
							{
								if (DataType.IsNullOrEmpty(param) || param.equals(key) == false)
								{
									continue;
								}
								valPara = ContextHolderUtils.getRequest().getParameter(key);
								isHave = true;
								break;
							}
						}
						if (isHave == false)
						{
							String expression = exp + " Key=(" + key + ") oper=(" + oper + ")Val=(" + val + ")";
							throw new RuntimeException(bp.wf.Glo.multilingual("@判断条件时错误,请确认参数是否拼写错误,没有找到对应的表达式:{0}.", "WorkNode", "expression_setting_error", expression));
						}
					}
					catch (java.lang.Exception e)
					{
						//有可能是常量.
						valPara = key;
					}
				}
				else
				{
					valPara = row.get(key).toString().trim();
				}


				///#region 开始执行判断.
				if (Objects.equals(oper, "="))
				{
					//如果表达式成立，就阻塞.
					if (valPara.equals(val) == true)
					{
						throw new RuntimeException("@" + Glo.DealExp(blockMsg, this.rptGe));
					}
					return;
				}

				if (Objects.equals(oper.toUpperCase(), "LIKE"))
				{
					if (valPara.contains(val) == true)
					{
						throw new RuntimeException("@" + Glo.DealExp(blockMsg, this.rptGe));
					}
					return;
				}

				if (Objects.equals(oper, ">"))
				{
					if (Float.parseFloat(valPara) > Float.parseFloat(val))
					{
						throw new RuntimeException("@" + Glo.DealExp(blockMsg, this.rptGe));
					}

					return;
				}

				if (Objects.equals(oper, ">="))
				{
					if (Float.parseFloat(valPara) >= Float.parseFloat(val))
					{
						throw new RuntimeException("@" + Glo.DealExp(blockMsg, this.rptGe));
					}

					return;
				}

				if (Objects.equals(oper, "<"))
				{
					if (Float.parseFloat(valPara) < Float.parseFloat(val))
					{
						throw new RuntimeException("@" + Glo.DealExp(blockMsg, this.rptGe));
					}

					return;
				}

				if (Objects.equals(oper, "<="))
				{
					if (Float.parseFloat(valPara) <= Float.parseFloat(val))
					{
						throw new RuntimeException("@" + Glo.DealExp(blockMsg, this.rptGe));
					}

					return;
				}

				if (Objects.equals(oper, "!="))
				{
					if (Float.parseFloat(valPara) != Float.parseFloat(val))
					{
						throw new RuntimeException("@" + Glo.DealExp(blockMsg, this.rptGe));
					}

					return;
				}

				String expression1 = exp + " Key=" + key + " oper=" + oper + " Val=" + val + ")";
				throw new RuntimeException(bp.wf.Glo.multilingual("@阻塞模式参数配置格式错误:{0}.", "WorkNode", "error_in_param_setting", expression1));

				///#endregion 开始执行判断.
			}

			//为父流程时，指定的子流程未运行到指定节点，则阻塞
			if (this.getHisNode().getBlockModel() == BlockModel.SpecSubFlowNode)
			{
				/*如果按照特定的格式判断阻塞*/
				String exp = this.getHisNode().getBlockExp();
				if (exp.contains("@") == false)
				{
					throw new RuntimeException(bp.wf.Glo.multilingual("@设置错误，该节点的阻塞配置格式({0})错误，请参考帮助来解决。", "WorkNode", "error_in_param_setting", exp));
				}


				String[] strs = exp.split("[@]", -1);
				String err = "";
				for (String str : strs)
				{
					if (DataType.IsNullOrEmpty(str) == true)
					{
						continue;
					}

					if (str.contains("=") == false)
					{
						throw new RuntimeException(bp.wf.Glo.multilingual("@阻塞设置的格式不正确:{0}.", "WorkNode", "error_in_param_setting", str));
					}


					String[] nodeFlow = str.split("[=]", -1);
					int nodeid = Integer.parseInt(nodeFlow[0]); //启动子流程的节点.
					int subFlowNode = Integer.parseInt(nodeFlow[1]); //子流程的节点
					Node subNode = new Node(subFlowNode);
					GenerWorkFlows gwfs = new GenerWorkFlows();
					GenerWorkerLists gwls = new GenerWorkerLists();

					if (this.getHisNode().getItIsSubThread() == true)
					{
						/* 如果是子线程，就不需要管，主干节点的问题。*/
						QueryObject qo = new QueryObject(gwfs);
						qo.AddWhere(GenerWorkFlowAttr.PWorkID, this.getWorkID());
						qo.addAnd();
						qo.AddWhere(GenerWorkFlowAttr.PNodeID, nodeid);
						qo.addAnd();
						qo.AddWhere(GenerWorkFlowAttr.PFlowNo, this.getHisFlow().getNo());
						qo.addAnd();
						qo.AddWhere(GenerWorkFlowAttr.FK_Flow, subNode.getFlowNo());
						qo.DoQuery();
						//该子流程已经运行
						if (gwfs.size() != 0)
						{
							GenerWorkFlow gwf = (GenerWorkFlow)gwfs.get(0);
							if (gwf.getIsOver()==true) //子流程结束
							{
								continue;
							}

							//判断是否运行到指定的节点
							gwls.Retrieve(GenerWorkerListAttr.WorkID, gwf.getWorkID(), GenerWorkerListAttr.FK_Node, subFlowNode, GenerWorkerListAttr.IsPass, 1, null);
							if (gwls.size() != 0)
							{
								continue;
							}

							gwls.Retrieve(GenerWorkerListAttr.FID, gwf.getWorkID(), GenerWorkerListAttr.FK_Node, subFlowNode, GenerWorkerListAttr.IsPass, 1, null);
							if (gwls.size() != 0)
							{
								continue;
							}
						}

					}
					else
					{
						/* 非子线程，就需要考虑，从该节点上，发起的子线程的 ，主干节点的问题。*/
						QueryObject qo = new QueryObject(gwfs);

						qo.addLeftBracket();
						qo.AddWhere(GenerWorkFlowAttr.PFID, this.getWorkID());
						qo.addOr();
						qo.AddWhere(GenerWorkFlowAttr.PWorkID, this.getWorkID());
						qo.addRightBracket();

						qo.addAnd();

						qo.addLeftBracket();
						qo.AddWhere(GenerWorkFlowAttr.PNodeID, nodeid);
						qo.addAnd();
						qo.AddWhere(GenerWorkFlowAttr.PFlowNo, this.getHisFlow().getNo());
						qo.addAnd();
						qo.AddWhere(GenerWorkFlowAttr.FK_Flow, subNode.getFlowNo());
						qo.addRightBracket();

						qo.DoQuery();
						//该子流程已经运行
						if (gwfs.size() != 0)
						{
							GenerWorkFlow gwf = (GenerWorkFlow)gwfs.get(0);
							if (gwf.getIsOver()==true) //子流程结束
							{
								continue;
							}

							//判断是否运行到指定的节点
							String sql = "";
							if (gwf.getFID() == 0)
							{
								sql = "SELECT count(*) as Num FROM ND" + Integer.parseInt(gwf.getFlowNo()) + "Track WHERE WorkID=" + gwf.getWorkID() + " AND (NDFrom=" + subFlowNode + " or NDTo=" + subFlowNode + " )";
							}
							else
							{
								sql = "SELECT count(*) as Num FROM ND" + Integer.parseInt(gwf.getFlowNo()) + "Track WHERE FID=" + gwf.getWorkID() + " AND (NDFrom=" + subFlowNode + " or NDTo=" + subFlowNode + " )";
							}

							if (DBAccess.RunSQLReturnValInt(sql) != 0)
							{
								continue;
							}

							//做第2次判断.
							if (gwf.getFID() == 0)
							{
								sql = "SELECT count(*) as Num FROM WF_GenerWorkerlist WHERE WorkID=" + gwf.getWorkID() + " AND FK_Node=" + subFlowNode;
							}
							else
							{
								sql = "SELECT count(*) as Num FROM WF_GenerWorkerlist  WHERE WorkID=" + gwf.getFID() + " AND FK_Node=" + subFlowNode;
							}

							if (DBAccess.RunSQLReturnValInt(sql) != 0)
							{
								continue;
							}

						}
					}

					err += bp.wf.Glo.multilingual("@如下子流程没有完成，你不能向下发送。@---------------------------------", "WorkNode", "cannot_send_to_next_1");
					String sub_wf_id = bp.wf.Glo.multilingual("@子流程ID:", "WorkNode", "sub_workflow_id");
					String sub_wf_name = bp.wf.Glo.multilingual(",子流程名称:", "WorkNode", "sub_workflow_title");
					String sub_wf_title = bp.wf.Glo.multilingual(",子流程标题:", "WorkNode", "sub_workflow_title");
					String sub_wf_operator = bp.wf.Glo.multilingual(",当前执行人:", "WorkNode", "current_operator");
					String sub_wf_step = bp.wf.Glo.multilingual(",运行到节点:", "WorkNode", "current_step");

					for (GenerWorkFlow gwf : gwfs.ToJavaList())
					{
						err += bp.wf.Glo.multilingual("@子流程ID:{0}", "WorkNode", "sub_workflow_id", String.valueOf(gwf.getWorkID())) + "\n" + bp.wf.Glo.multilingual(",子流程名称:{0}", "WorkNode", "sub_workflow_title", gwf.getFlowName()) + "\n" + bp.wf.Glo.multilingual(",子流程标题:{0}", "WorkNode", "sub_workflow_title", gwf.getTitle()) + "\n" + bp.wf.Glo.multilingual(",当前执行人:{0}", "WorkNode", "current_operator", gwf.getTodoEmps()) + "\n" + bp.wf.Glo.multilingual(",运行到节点:{0}", "WorkNode", "current_step", gwf.getNodeName());
					}
				}

				if (DataType.IsNullOrEmpty(err) == true)
				{
					return;
				}

				err = bp.wf.Glo.DealExp(blockMsg, this.rptGe) + err;
				throw new RuntimeException(err);
			}

			//为平级流程时，指定的子流程未运行到指定节点，则阻塞
			if (this.getHisNode().getBlockModel() == BlockModel.SameLevelSubFlow)
			{
				/*如果按照特定的格式判断阻塞*/
				String exp = this.getHisNode().getBlockExp();

				String[] strs = exp.split("[,]", -1);
				String err = "";
				for (String str : strs)
				{
					if (DataType.IsNullOrEmpty(str) == true)
					{
						continue;
					}

					int nodeid = Integer.parseInt(str); //平级子流程的节点
					Node subNode = new Node(nodeid);
					GenerWorkFlows gwfs = new GenerWorkFlows();
					GenerWorkerLists gwls = new GenerWorkerLists();


					QueryObject qo = new QueryObject(gwfs);
					qo.AddWhere(GenerWorkFlowAttr.PWorkID, this.getHisGenerWorkFlow().getPWorkID());
					//qo.addAnd();
					//qo.AddWhere(GenerWorkFlowAttr.PNodeID, this.HisGenerWorkFlow.PNodeID);
					qo.addAnd();
					qo.AddWhere(GenerWorkFlowAttr.PFlowNo, this.getHisGenerWorkFlow().getPFlowNo());
					qo.addAnd();
					qo.AddWhere(GenerWorkFlowAttr.FK_Flow, subNode.getFlowNo());
					qo.DoQuery();
					//该子流程已经运行
					if (gwfs.size() != 0)
					{
						GenerWorkFlow gwf = (GenerWorkFlow)gwfs.get(0);
						if (gwf.getIsOver()==true) //子流程结束
						{
							continue;
						}

						//判断是否运行到指定的节点
						long workId = gwf.getWorkID();
						gwls.Retrieve(GenerWorkerListAttr.WorkID, gwf.getWorkID(), GenerWorkerListAttr.FK_Node, nodeid, GenerWorkerListAttr.IsPass, 1, null);
						if (gwls.size() != 0)
						{
							continue;
						}
					}
					err += bp.wf.Glo.multilingual("@如下子流程没有完成，你不能向下发送。@---------------------------------", "WorkNode", "cannot_send_to_next_1");
					String sub_wf_id = bp.wf.Glo.multilingual("@子流程ID:", "WorkNode", "sub_workflow_id");
					String sub_wf_name = bp.wf.Glo.multilingual(",子流程名称:", "WorkNode", "sub_workflow_title");
					String sub_wf_title = bp.wf.Glo.multilingual(",子流程标题:", "WorkNode", "sub_workflow_title");
					String sub_wf_operator = bp.wf.Glo.multilingual(",当前执行人:", "WorkNode", "current_operator");
					String sub_wf_step = bp.wf.Glo.multilingual(",运行到节点:", "WorkNode", "current_step");

					for (GenerWorkFlow gwf : gwfs.ToJavaList())
					{
						err += bp.wf.Glo.multilingual("@子流程ID:{0}", "WorkNode", "sub_workflow_id", String.valueOf(gwf.getWorkID())) + "\n" + bp.wf.Glo.multilingual(",子流程名称:{0}", "WorkNode", "sub_workflow_title", gwf.getFlowName()) + "\n" + bp.wf.Glo.multilingual(",子流程标题:{0}", "WorkNode", "sub_workflow_title", gwf.getTitle()) + "\n" + bp.wf.Glo.multilingual(",当前执行人:{0}", "WorkNode", "current_operator", gwf.getTodoEmps()) + "\n" + bp.wf.Glo.multilingual(",运行到节点:{0}", "WorkNode", "current_step", gwf.getNodeName());
					}
				}

				if (DataType.IsNullOrEmpty(err) == true)
				{
					return;
				}

				err = bp.wf.Glo.DealExp(blockMsg, this.rptGe) + err;
				throw new RuntimeException(err);
			}
			throw new RuntimeException("@该阻塞模式没有实现...");
		}
		catch (RuntimeException ex)
		{

			//  throw ex;

			//提示：宜昌的友好提示 211102
			if (this.getHisNode().getBlockModel() == BlockModel.BySQL)
			{
				throw new RuntimeException("阻塞原因：" + this.getHisNode().getBlockAlert());
			}


			//正确的提示: 宜昌的需要这样的明确的提示信息.
			throw new RuntimeException("设置的阻塞规则错误:" + this.getHisNode().getBlockModel() + ",exp:" + this.getHisNode().getBlockExp() + "异常信息" + ex.getMessage());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 发送到延续子流程.

	 @param node
	 @param toEmpIDs
	 @return
	 */
	private SendReturnObjs NodeSendToYGFlow(Node node, String toEmpIDs) throws Exception {
		SubFlowYanXu subFlow = new SubFlowYanXu();
		subFlow.setMyPK(this.getHisNode().getNodeID() + "_" + node.getFlowNo() + "_" + 2);
		if (subFlow.RetrieveFromDBSources() == 0)
		{
			throw new RuntimeException(bp.wf.Glo.multilingual("@延续子流程配置信息丢失，请联系管理员.", "WorkNode", "not_found_receiver"));
		}

		String sql = "";
		if (DataType.IsNullOrEmpty(toEmpIDs))
		{
			toEmpIDs = "";

			DataTable dt = null;


			///#region 按照人员选择
			if (node.getHisDeliveryWay() == DeliveryWay.BySelected)
			{
				sql = "SELECT FK_Emp AS No, EmpName AS Name FROM WF_SelectAccper WHERE FK_Node=" + node.getNodeID() + " AND WorkID=" + this.getWorkID() + " AND AccType=0";
				dt = DBAccess.RunSQLReturnTable(sql);
				if (dt.Rows.size() == 0)
				{
					throw new RuntimeException(bp.wf.Glo.multilingual("@没有为延续子流程设置接收人.", "WorkNode", "not_found_receiver"));
				}
			}

			///#endregion 按照人员选择.


			///#region 按照角色与部门的交集.
			if (node.getHisDeliveryWay() == DeliveryWay.ByDeptAndStation)
			{
				sql = "SELECT pdes.fk_emp AS No"
						+ " FROM   Port_DeptEmpStation pdes"
						+ "        INNER JOIN WF_NodeDept wnd"
						+ "             ON  wnd.fk_dept = pdes.fk_dept"
						+ "             AND wnd.fk_node = " + node.getNodeID()
						+ "        INNER JOIN WF_NodeStation wns"
						+ "             ON  wns.FK_Station = pdes.fk_station"
						+ "             AND wnd.fk_node =" + node.getNodeID()
						+ " ORDER BY"
						+ "        pdes.fk_emp";
				dt = DBAccess.RunSQLReturnTable(sql);

				if (dt.Rows.size() == 0)
				{
					String[] para = new String[4];
					para[0] = node.getHisDeliveryWay().toString();
					para[1] = String.valueOf(node.getNodeID());
					para[2] = node.getName();
					para[3] = sql;

					throw new RuntimeException(bp.wf.Glo.multilingual("@节点访问规则({0})错误:节点({1},{2}), 按照角色与部门的交集确定接收人的范围错误，没有找到人员:SQL={3}.", "WorkNode", "error_in_access_rules_setting", para));
				}
			}

			///#endregion 按照角色与部门的交集


			///#region 仅按角色计算
			if (node.getHisDeliveryWay() == DeliveryWay.ByStationOnly)
			{
				ps = new Paras();


				if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
				{
					sql = "SELECT A.FK_Emp No FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node ORDER BY A.FK_Emp";
					ps.Add("FK_Node", node.getNodeID());
					ps.SQL = sql;
				}
				else
				{
					sql = "SELECT A.FK_Emp No FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND A.OrgNo=" + dbStr + "OrgNo AND B.FK_Node=" + dbStr + "FK_Node  ORDER BY A.FK_Emp";
					ps.Add("OrgNo", bp.web.WebUser.getOrgNo(), false);
					ps.Add("FK_Node", node.getNodeID());
					ps.SQL = sql;
				}

				dt = DBAccess.RunSQLReturnTable(ps);
				if (dt.Rows.size() == 0)
				{
					String[] para2 = new String[3];
					para2[0] = node.getHisDeliveryWay().toString();
					para2[1] = String.valueOf(node.getNodeID());
					para2[2] = node.getName();
					// para2[3] = ps.getSQLNoPara();
					throw new RuntimeException(bp.wf.Glo.multilingual("@节点访问规则{0}错误:节点({1},{2}), 仅按角色计算，没有找到人员.", "WorkNode", "error_in_access_rules_setting", para2));
				}
			}

			///#endregion


			///#region 仅按用户组计算
			if (node.getHisDeliveryWay() == DeliveryWay.ByTeamOnly)
			{
				sql = "SELECT A.FK_Emp No FROM Port_TeamEmp A, WF_NodeTeam B WHERE A.FK_Team=B.FK_Team AND B.FK_Node=" + node.getNodeID();
				dt = DBAccess.RunSQLReturnTable(sql);
				if (dt.Rows.size() == 0)
				{
					String[] para2 = new String[4];
					para2[0] = node.getHisDeliveryWay().toString();
					para2[1] = String.valueOf(node.getNodeID());
					para2[2] = node.getName();
					para2[3] = sql;
					throw new RuntimeException(bp.wf.Glo.multilingual("@节点访问规则{0}错误:节点({1},{2}), 仅按用户组计算，没有找到人员:SQL={3}.", "WorkNode", "error_in_access_rules_setting", para2));
				}
			}

			///#endregion


			///#region 按用户组计算（本部门）
			if (node.getHisDeliveryWay() == DeliveryWay.ByTeamDeptOnly)
			{
				sql = "SELECT A.FK_Emp No FROM Port_TeamEmp A, WF_NodeTeam B, Port_DeptEmp C WHERE A.FK_Emp=C.FK_Emp AND A.FK_Team=B.FK_Team AND B.FK_Node=" + node.getNodeID() + " AND C.FK_Dept='" + getWebUser().getDeptNo() + "' ORDER BY A.FK_Emp";
				dt = DBAccess.RunSQLReturnTable(sql);
				if (dt.Rows.size() == 0)
				{
					String[] para2 = new String[4];
					para2[0] = node.getHisDeliveryWay().toString();
					para2[1] = String.valueOf(node.getNodeID());
					para2[2] = node.getName();
					para2[3] = sql;
					throw new RuntimeException(bp.wf.Glo.multilingual("@节点访问规则{0}错误:节点({1},{2}), 仅按用户组计算，没有找到人员:SQL={3}.", "WorkNode", "error_in_access_rules_setting", para2));
				}
			}

			///#endregion


			///#region 按用户组计算(本组织)
			if (node.getHisDeliveryWay() == DeliveryWay.ByTeamOrgOnly)
			{
				sql = "SELECT A.FK_Emp No FROM Port_TeamEmp A, WF_NodeTeam B, Port_Emp C WHERE A.FK_Emp=C." + bp.sys.base.Glo.getUserNoWhitOutAS() + " AND A.FK_Team=B.FK_Team AND B.FK_Node=" + dbStr + "FK_Node AND C.OrgNo=" + dbStr + "OrgNo ORDER BY A.FK_Emp";
				ps = new Paras();
				ps.Add("FK_Node", node.getNodeID());
				ps.Add("OrgNo", getWebUser().OrgNo, false);

				ps.SQL = sql;
				dt = DBAccess.RunSQLReturnTable(ps);
				if (dt.Rows.size() == 0)
				{
					String[] para2 = new String[4];
					para2[0] = node.getHisDeliveryWay().toString();
					para2[1] = String.valueOf(node.getNodeID());
					para2[2] = node.getName();
					para2[3] = ps.getSQLNoPara();
					throw new RuntimeException(bp.wf.Glo.multilingual("@节点访问规则{0}错误:节点({1},{2}), 仅按用户组计算，没有找到人员:SQL={3}.", "WorkNode", "error_in_access_rules_setting", para2));
				}
			}

			///#endregion


			///#region 按绑定的人计算
			if (node.getHisDeliveryWay() == DeliveryWay.ByBindEmp)
			{
				ps = new Paras();
				ps.Add("FK_Node", node.getNodeID());
				ps.SQL = "SELECT FK_Emp AS No FROM WF_NodeEmp WHERE FK_Node=" + dbStr + "FK_Node ORDER BY FK_Emp";
				dt = DBAccess.RunSQLReturnTable(ps);
				if (dt.Rows.size() == 0)
				{
					throw new RuntimeException(bp.wf.Glo.multilingual("@流程设计错误:没找到下一个节点(" + town.getHisNode().getName() + ")的接收人.", "WorkNode", "system_error_not_found_operator", town.getHisNode().getName()));
				}
			}

			///#endregion

			if (dt == null)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("err@您启动的子流程或者延续流程开始节点没有明确的设置接收人.", "WorkNode", "not_found_receiver"));
			}

			if (dt.Rows.size() == 0)
			{
				throw new RuntimeException("err@请选择接受人.");
			}

			if (dt.Rows.size() != 1)
			{
				throw new RuntimeException("err@必须选择一个接受人.");
			}

			toEmpIDs = dt.Rows.get(0).getValue("No").toString();
			////组装到达的人员. 延续子流程的第一个节点的发起人只有一个人 @lizhen.
			//foreach (DataRow dr in dt.Rows)
			//    toEmpIDs =  dr["No").toString();
		}

		if (DataType.IsNullOrEmpty(toEmpIDs) == true)
		{
			throw new RuntimeException(bp.wf.Glo.multilingual("@延续子流程目前仅仅支持选择接收人方式.", "WorkNode", "not_found_receiver"));
		}

		String starter = toEmpIDs;
		long workid = 0;
		boolean IsSendToStartNode = true;
		//if (subFlow.YanXuToNode != int.Parse(int.Parse(subFlow.SubFlowNo) + "01"))
		//    IsSendToStartNode = false;

		if (node.getNodeID() != Integer.parseInt(Integer.parseInt(subFlow.getSubFlowNo()) + "01"))
		{
			IsSendToStartNode = false;
		}

		if (IsSendToStartNode == false)
		{
			starter = getWebUser().No;
		}

		if (subFlow.getHisSubFlowModel() == SubFlowModel.SubLevel) //下级子流程
		{
			workid = bp.wf.Dev2Interface.Node_CreateBlankWork(node.getFlowNo(), null, null, starter, null, this.getWorkID(), 0, this.getHisNode().getFlowNo(), this.getHisNode().getNodeID(), bp.web.WebUser.getNo(), 0, null);
		}
		else if (subFlow.getHisSubFlowModel() == SubFlowModel.SameLevel) //平级子流程
		{
			workid = bp.wf.Dev2Interface.Node_CreateBlankWork(node.getFlowNo(), null, null, starter, null, this.getHisGenerWorkFlow().getPWorkID(), 0, this.getHisGenerWorkFlow().getPFlowNo(), this.getHisGenerWorkFlow().getPNodeID(), this.getHisGenerWorkFlow().getPEmp(), 0, null);
			//存储同级子流程的信息
			GenerWorkFlow subYXGWF = new GenerWorkFlow(workid);
			subYXGWF.SetPara("SLWorkID", this.getWorkID());
			subYXGWF.SetPara("SLFlowNo", this.getHisNode().getFlowNo());
			subYXGWF.SetPara("SLNodeID", this.getHisNode().getNodeID());
			subYXGWF.SetPara("SLEmp", bp.web.WebUser.getNo());
			subYXGWF.Update();
		}

		//复制当前信息.
		Work wk = node.getHisWork();
		wk.setOID(workid);
		wk.RetrieveFromDBSources();
		wk.Copy(this.getHisWork());
		wk.Update();

		//为接收人显示待办.
		//if (subFlow.YanXuToNode == int.Parse(int.Parse(subFlow.SubFlowNo) + "01"))
		if (node.getNodeID() == Integer.parseInt(Integer.parseInt(subFlow.getSubFlowNo()) + "01"))
		{
			// 产生工作列表.
			GenerWorkerList gwl = new GenerWorkerList();
			int count = gwl.Retrieve(GenerWorkerListAttr.WorkID, workid, GenerWorkerListAttr.FK_Node, node.getNodeID());
			if (count == 0)
			{
				Emp emp = new Emp(toEmpIDs);
				gwl.setWorkID(workid);
				gwl.setEmpNo(toEmpIDs);
				gwl.setEmpName(emp.getName());

				gwl.setNodeID(node.getNodeID());
				gwl.setNodeName(node.getName());
				gwl.setFID(0);

				gwl.setFlowNo(node.getFlowNo());
				gwl.setDeptNo(emp.getDeptNo());
				gwl.setDeptName(emp.getDeptText());

				gwl.setSDT("无");
				gwl.setDTOfWarning(DataType.getCurrentDateTimess());
				gwl.setItIsEnable(true);

				gwl.setItIsPass(false);
				gwl.Save();

			}
			bp.wf.Dev2Interface.Node_SetDraft2Todolist(workid);
		}
		else
		{
			//执行发送到下一个环节..
			//SendReturnObjs sendObjs = BP.WF.Dev2Interface.Node_SendWork(subFlow.SubFlowNo, workid, subFlow.YanXuToNode, toEmpIDs);
			SendReturnObjs sendObjs = bp.wf.Dev2Interface.Node_SendWork(subFlow.getSubFlowNo(), workid, node.getNodeID(), toEmpIDs);
		}

		//设置变量.
		this.addMsg(SendReturnMsgFlag.VarToNodeID, String.valueOf(node.getNodeID()), String.valueOf(workid), SendReturnMsgType.SystemMsg);
		this.addMsg(SendReturnMsgFlag.VarAcceptersID, toEmpIDs, toEmpIDs, SendReturnMsgType.SystemMsg);

		//设置消息.
		this.addMsg("Msg1", bp.wf.Glo.multilingual("子流程({0})已经启动,发送给({1})处理人.", "WorkNode", "sub_wf_started", node.getFlowName(), toEmpIDs));
		if (SystemConfig.getCustomerNo().equals("ASSET") == false)
		{
			this.addMsg("Msg2", bp.wf.Glo.multilingual("当前您的待办不可见,需要等待子流程完成后您的待办才能显示,您可以从在途里查看工作进度.", "WorkNode", "to_do_list_invisible"));
		}


		//设置当前工作操作员不可见.
		sql = "UPDATE WF_GenerWorkerlist SET IsPass=80 WHERE WorkID=" + this.getWorkID() + " AND IsPass=0";
		DBAccess.RunSQL(sql);

		return HisMsgObjs;
	}

	/**
	 工作流发送业务处理.
	 升级日期:2012-11-11.
	 升级原因:代码逻辑性不清晰,有遗漏的处理模式.
	 修改人:zhoupeng.
	 修改地点:厦门.
	 ----------------------------------- 说明 -----------------------------
	 1，方法体分为三大部分: 发送前检查\5*5算法\发送后的业务处理.
	 2, 详细请参考代码体上的说明.
	 3, 发送后可以直接获取它的

	 @param jumpToNode 要跳转的节点,可以为空.
	 @param jumpToEmp 要跳转的人,可以为空.
	 @return 返回执行结果
	 */

	public final SendReturnObjs NodeSend(Node jumpToNode, String jumpToEmp) throws Exception {
		return NodeSend(jumpToNode, jumpToEmp, false);
	}

	public final SendReturnObjs NodeSend(Node jumpToNode, String jumpToEmp, boolean IsReturnNode) throws Exception {
		this.setWFState();
		//判断 guest 节点.

		if (this.getHisNode().getItIsGuestNode())
		{
			if (this.getExecer().equals("Guest") == false)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@当前节点({0})是客户执行节点,所以当前登录人员应当是Guest,现在是:{1}.", "WorkNode", "should_gust", this.getHisNode().getName(), this.getExecer()));
			}
		}


		///#region 第1: 安全性检查.
		//   第1: 检查是否可以处理当前的工作.
		if ((this.getHisNode().getItIsStartNode() == false || (this.getHisNode().getItIsStartNode() == true && this.getHisGenerWorkFlow().getWFState() == WFState.ReturnSta)) && this.getHisGenerWorkFlow().getTodoEmps().contains(getWebUser().getNo() + ",") == false)
		{
			if (bp.wf.Dev2Interface.Flow_IsCanDoCurrentWork(this.getWorkID(), this.getExecer()) == false)
			{
				throw new RuntimeException("@当前工作{" + this.getHisFlow().getNo() + " - WorkID=" + this.getWorkID() + "} 您好，(" + this.getExecerName() + ")请勿重复操作，当前您没有处理权限.");
			}
		}

		///#endregion 安全性检查.


		///#region 第2: 调用发起前的事件接口,处理用户定义的业务逻辑.
		String sendWhen = ExecEvent.DoNode(EventListNode.SendWhen, this);

		//返回格式. @Info=xxxx@ToNodeID=xxxx@ToEmps=xxxx@IsStopFlow=0
		if (sendWhen != null && sendWhen.indexOf("@") >= 0)
		{
			AtPara ap = new AtPara(sendWhen);
			int nodeid = ap.GetValIntByKey("ToNodeID", 0);
			if (nodeid != 0)
			{
				jumpToNode = new Node(nodeid);
			}

			//监测是否有停止流程的标志？
			this.setStopFlow(ap.GetValBoolenByKey("IsStopFlow", false));

			String toEmps = ap.GetValStrByKey("ToEmps");
			if (DataType.IsNullOrEmpty(toEmps) == false)
			{
				jumpToEmp = toEmps;
			}

			//处理str信息.
			sendWhen = sendWhen.replace("@Info=", "");
			sendWhen = sendWhen.replace("@IsStopFlow=1", "");
			sendWhen = sendWhen.replace("@ToNodeID=" + String.valueOf(nodeid), "");
			sendWhen = sendWhen.replace("@ToEmps=" + toEmps, "");
		}

		if (sendWhen != null)
		{
			/*说明有事件要执行,把执行后的数据查询到实体里*/
			this.getHisWork().RetrieveFromDBSources();
			this.getHisWork().ResetDefaultVal(null, null, 0);
			this.getHisWork().setRec(this.getExecer());
			if (DataType.IsNullOrEmpty(sendWhen) == false)
			{
				if (sendWhen.startsWith("false") || sendWhen.startsWith("False") || sendWhen.startsWith("error") || sendWhen.startsWith("Error"))
				{
					this.addMsg(SendReturnMsgFlag.SendWhen, sendWhen);
					sendWhen = sendWhen.replace("false", "");
					sendWhen = sendWhen.replace("False", "");
					throw new RuntimeException(bp.wf.Glo.multilingual("@执行发送前事件失败:{0}.", "WorkNode", "error_send", sendWhen));
				}
			}

			//把发送sendWhen 消息提示给用户.
			if (sendWhen.equals("null") == true)
			{
				sendWhen = "";
			}
			this.addMsg("SendWhen", sendWhen, sendWhen, SendReturnMsgType.Info);
		}

		//加入系统变量.
		this.addMsg(SendReturnMsgFlag.VarCurrNodeID, String.valueOf(this.getHisNode().getNodeID()), String.valueOf(this.getHisNode().getNodeID()), SendReturnMsgType.SystemMsg);
		this.addMsg(SendReturnMsgFlag.VarCurrNodeName, this.getHisNode().getName(), this.getHisNode().getName(), SendReturnMsgType.SystemMsg);
		this.addMsg(SendReturnMsgFlag.VarWorkID, String.valueOf(this.getWorkID()), String.valueOf(this.getWorkID()), SendReturnMsgType.SystemMsg);

		if (this.isStopFlow() == true)
		{
			/*在检查完后，反馈来的标志流程已经停止了。*/

			//查询出来当前节点的工作报表.
			this.rptGe = this.getHisFlow().getHisGERpt();
			this.rptGe.SetValByKey("OID", this.getWorkID());
			this.rptGe.RetrieveFromDBSources();
			CC(this.getHisNode()); //抄送到其他节点.

			this.Func_DoSetThisWorkOver(); //设置工作完成.

			this.rptGe.setWFState(WFState.CompleteEnd);
			this.rptGe.Update();
			this.getHisGenerWorkFlow().Update(); //added by liuxc,2016-10-24,最后节点更新Sender字段

			//执行考核.
			Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), 0, this.getHisGenerWorkFlow().getTitle());

			//判断当前流程是否子流程，是否启用该流程结束后，主流程自动运行到下一节点
			String msg = WorkNodePlus.SubFlowEvent(this);
			if (DataType.IsNullOrEmpty(msg) == false)
			{
				this.HisMsgObjs.AddMsg("info", msg, msg, SendReturnMsgType.Info);
			}


			return this.HisMsgObjs;
		}

		///#endregion 处理发送前事件.

		//设置跳转节点，如果有可以为null.
		this.JumpToNode = jumpToNode;
		this.JumpToEmp = jumpToEmp;

		// 为广西计算中心增加自动返回的节点, 发送之后，让其自动返回给发送人.
		if (this.getHisNode().getItIsSendBackNode() == true)
		{
			WorkNode wn = WorkNodePlus.IsSendBackNode(this);
			this.JumpToEmp = wn.JumpToEmp;
			this.JumpToNode = wn.JumpToNode; //计算要到达的人.
		}

		//定义变量.
		//String sql = null;
		//DateTime dt = DateTime.Now;
		this.getHisWork().setRec(this.getExecer());
		// this.setWorkID(this.HisWork.OID;


		///#region 第一步: 检查当前操作员是否可以发送: 共分如下 3 个步骤.
		//第1.2.1: 如果是开始节点，就要检查发起流程限制条件.
		if (this.getHisNode().getItIsStartNode() == true)
		{
			if (WorkNodePlus.CheckIsCanStartFlow_SendStartFlow(this.getHisFlow(), this.getHisWork()) == false)
			{
				String er = bp.wf.Glo.DealExp(this.getHisFlow().getStartLimitAlert(), this.getHisWork());
				throw new RuntimeException(bp.wf.Glo.multilingual("@违反了流程发起限制条件:{0}.", "WorkNode", "error_send", er));
			}
		}

		// 第1.3: 判断当前流程状态,如果是加签状态, 处理.
		if (this.getHisNode().getItIsStartNode() == false && this.getHisGenerWorkFlow().getWFState() == WFState.Askfor)
		{
			SendReturnObjs objs = WorkNodePlus.DealAskForState(this);
			if (objs != null)
			{
				return objs;
			}
		}

		// 第3: 如果是是合流点，有子线程未完成的情况.(不能删除或手工删除需要抛异常，自动删除则直接删除子流程)
		if (this.getHisNode().getItIsHL()|| this.getHisNode().getHisRunModel() == RunModel.FHL)
		{
			WorkNodePlus.DealHeLiuState(this);
		}


		///#endregion 第一步: 检查当前操作员是否可以发送

		//查询出来当前节点的工作报表.
		if (this.rptGe == null || this.rptGe.getOID()==0)
		{
			this.rptGe = this.getHisFlow().getHisGERpt();
			if (this.getHisNode().getItIsSubThread() == true)
			{
				this.rptGe.SetValByKey("OID", this.getHisGenerWorkFlow().getFID());
			}
			else
			{
				this.rptGe.SetValByKey("OID", this.getWorkID());
			}

			int i = this.rptGe.RetrieveFromDBSources();
			if (i == 0)
			{
				GenerWorkFlowExt ext=new GenerWorkFlowExt();
				ext.setWorkID(this.getWorkID());
				ext.RetrieveFromDBSources();
				ext.RepairDataIt();
				this.rptGe.RetrieveFromDBSources();

				//throw new RuntimeException("err@系统错误，不应该查询不出来." + this.rptGe.getEnMap().getPhysicsTable() + " WorkID=" + this.rptGe.getOID());
			}
		}

		//检查阻塞模式.
		this.CheckBlockModel();

		// 检查FormTree必填项目,如果有一些项目没有填写就抛出异常.
		this.CheckFrmIsNotNull();

		// 处理自动运行 - 预先设置未来的运行节点.
		// this.DealAutoRunEnable();

		//把数据更新到数据库里.
		this.getHisWork().DirectUpdate();
		if (!Objects.equals(this.getHisWork().getEnMap().getPhysicsTable(), this.rptGe.getEnMap().getPhysicsTable()))
		{
			// 有可能外部参数传递过来导致，rpt表数据没有发生变化。
			this.rptGe.Copy(this.getHisWork());
			//首先执行保存，不然会影响条件的判断 by dgq 2016-1-14
			this.rptGe.Update();
		}

		//如果是队列节点, 就判断当前的队列人员是否走完。
		if (this.getTodolistModel() == TodolistModel.Order)
		{
			if (this.DealOradeNode() == true)
			{
				//调用发送成功事件.
				String sendSuccess = ExecEvent.DoNode(EventListNode.SendSuccess, this);

				this.HisMsgObjs.AddMsg("info21", sendSuccess, sendSuccess, SendReturnMsgType.Info);

				String todoEmps = this.getHisGenerWorkFlow().getTodoEmps();
				todoEmps = todoEmps.replace(getWebUser().getNo() + "," + getWebUser().getName() +";", "");
				todoEmps = todoEmps.replace(getWebUser().getNo() + "," + getWebUser().Name, "");
				this.getHisGenerWorkFlow().setTodoEmps(todoEmps);

				//执行时效考核.
				Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), this.rptGe.getFID(), this.rptGe.getTitle());
				CC(this.getHisNode());
				//删除增加的抄送信息
				WorkOpt workOpt = new WorkOpt();
				workOpt.setMyPK(this.getWebUser().getNo()+"_"+this.getHisNode().getNodeID()+"_"+this.getWorkID());
				workOpt.Delete();
				this.rptGe.setFlowEnderRDT(DataType.getCurrentDateTimess());

				//设置当前的流程所有的用时.
				this.rptGe.setFlowDaySpan(DataType.GeTimeLimits(this.rptGe.GetValStringByKey(GERptAttr.FlowStartRDT), DataType.getCurrentDateTime()));
				this.rptGe.Update();
				return this.HisMsgObjs;
			}
		}
		// 如果是连续多级主管模式，分为两种情况，先实现审批终点是角色的情况,另外一种通讯录模式没有实现
		/*if(this.getHisNode().getHisDeliveryWay() == DeliveryWay.ByMLeader0)
		{
			//如果当前还有队列，生成待办列表，退出
			String findLeader = "SELECT Leader as FK_Emp FROM Port_Emp WHERE No='" + WebUser.getNo() + "'";
			DataTable dtEmp = DBAccess.RunSQLReturnTable(findLeader);
			//是否有直接主管
			String stringLeader= dtEmp.Rows.get(0).getValue(0).toString();
			boolean hasLeader = ! DataType.IsNullOrEmpty(stringLeader);

			//判断当前是不是审批终点，根据角色

			NodeStations nodeStations = new NodeStations();
			QueryObject qo = new QueryObject(nodeStations);
			qo.AddWhere(NodeStationAttr.FK_Node, this.getHisNode().getNodeID());
			qo.addAnd();
			qo.AddWhereInSQL(NodeStationAttr.FK_Station,"SELECT FK_Station from Port_DeptEmpStation where FK_Emp='"+ WebUser.getNo() + "'");
			qo.DoQuery();

			Boolean isLastApporver = nodeStations.size()<=0 ? false:true;

			if (hasLeader&& !isLastApporver)
			{
				//给下一个主管创建待办
				//Node nd = new Node(this.HisNode.NodeID);
				////Work work = nd.HisWork;
				//WorkNode town = new WorkNode(nd.HisWork,nd);
				//GenerWorkerLists generWorkerLists =  this.InitWorkerLists(town, dtEmp);
				Emp emp = new Emp(stringLeader);

				GenerWorkerList gwl = new GenerWorkerList();
				gwl.setWorkID(this.getWorkID());
				gwl.setFID(this.getHisGenerWorkFlow().getFID());
				gwl.setNodeID(this.getHisNode().getNodeID());
				gwl.setNodeName(this.getHisNode().getName());
				gwl.setEmpNo(emp.getNo());
				gwl.setEmpName(emp.getName());
				gwl.setDeptNo(emp.getDeptNo());
				gwl.setDeptName(emp.getDeptText());
				gwl.setWhoExeIt(this.getHisNode().getWhoExeIt());
				gwl.setSDT(DataType.getCurrentDateTime());
				gwl.setDTOfWarning(DataType.getCurrentDateTime());
				gwl.setCDT(DataType.getCurrentDateTime());
				gwl.setFlowNo(this.getHisNode().getFlowNo());
				gwl.setSender( WebUser.getNo() + "," + WebUser.getName());
				gwl.setPassInt(0);
				gwl.setItIsRead(true);
				gwl.Insert();

				//给当前发送人修改待办状态
				GenerWorkerList generWorkerList = new GenerWorkerList();
				generWorkerList.Retrieve(GenerWorkerListAttr.FK_Emp,WebUser.getNo(),GenerWorkerListAttr.WorkID,this.getWorkID(),GenerWorkerListAttr.FK_Node,this.getHisNode().getNodeID());
				generWorkerList.setItIsPass(true);
				generWorkerList.Update();
				//当前登录人增加日志
				this.AddToTrack(ActionType.Order, generWorkerList.getEmpNo(), generWorkerList.getEmpName(), this.getHisNode().getNodeID(),
						this.getHisNode().getName(), bp.wf.Glo.multilingual("队列发送", "WorkNode", "queue_transferred"));
				//下一步主管提醒
				//  foreach (GenerWorkerList gwl in generWorkerLists)
				// {
				this.addMsg(SendReturnMsgFlag.VarAcceptersID, gwl.getEmpNo(), gwl.getEmpNo(), SendReturnMsgType.SystemMsg);
				this.addMsg(SendReturnMsgFlag.VarAcceptersName, gwl.getEmpName(), gwl.getEmpName(), SendReturnMsgType.SystemMsg);
				this.addMsg(SendReturnMsgFlag.OverCurr, bp.wf.Glo.multilingual("@当前工作已经发送给({0},{1}).", "WorkNode", "send_to_the_operator", gwl.getEmpNo(), gwl.getEmpName()), null, SendReturnMsgType.Info);
				//}

				//执行更新.
				if (this.getHisGenerWorkFlow().getEmps().contains("@" + WebUser.getNo() + "," + WebUser.getName() + "@") == false
						|| this.getHisGenerWorkFlow().getEmps().contains("@" + WebUser.getNo() + "@") == false)
					this.getHisGenerWorkFlow().setEmps(this.getHisGenerWorkFlow().getEmps()+WebUser.getNo()+"," +WebUser.getName() + "@");
				this.rptGe.setFlowEmps(this.getHisGenerWorkFlow().getEmps());
				this.rptGe.setWFState(WFState.Runing);
				this.getHisGenerWorkFlow().setWFState(WFState.Runing);
				this.getHisGenerWorkFlow().Update();

				//调用发送成功事件.
				String sendSuccess = ExecEvent.DoNode(EventListNode.SendSuccess, this);
				this.HisMsgObjs.AddMsg("info21", sendSuccess, sendSuccess, SendReturnMsgType.Info);
				//执行时效考核.
				Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), this .rptGe.getFID(), this.rptGe.getTitle());
				this.rptGe.setFlowEnderRDT(DataType.getCurrentDate());
				//设置当前的流程所有的用时.
				this.rptGe.setFlowDaySpan( DataType.GeTimeLimits(this.rptGe.GetValStringByKey(GERptAttr.FlowStartRDT), DataType.getCurrentDateTime()) )  ;
				this.rptGe.Update();
				return this.HisMsgObjs;
			}

			//如果是最后一个节点，发送即可
		}*/
		//如果是协作模式节点, 就判断当前的队列人员是否走完.
		if (this.getTodolistModel() == TodolistModel.Teamup)
		{
			//判断删除其他人员待办的规则.
			String skipEmps="";
			if (this.getHisNode().getGenerWorkerListDelRole() != 0)
			{
				skipEmps = WorkNodePlus.GenerWorkerListDelRole(this.getHisNode(), this.getHisGenerWorkFlow());
			}

			//,增加了此部分.
			String todoEmps = this.getHisGenerWorkFlow().getTodoEmps();
			todoEmps = todoEmps.replace(getWebUser().getNo() + "," + getWebUser().getName() +";", "");
			todoEmps = todoEmps.replace(getWebUser().getNo() + "," + getWebUser().Name, "");
			if(DataType.IsNullOrEmpty(skipEmps)==false){
				String[] empStrs = skipEmps.split("@");
				for(String empStr :empStrs){
					todoEmps = todoEmps.replace(empStr +";", "");
					todoEmps = todoEmps.replace(empStr, "");
				}
			}

			// 追加当前操作人
			String emps = this.getHisGenerWorkFlow().getEmps();
			if (emps.contains("@" + getWebUser().getNo() + "@") == false)
			{
				emps = emps + getWebUser().getNo() + "@";
			}
			emps+=skipEmps;
			this.getHisGenerWorkFlow().setEmps(emps);
			this.getHisGenerWorkFlow().setTodoEmps(todoEmps);
			this.getHisGenerWorkFlow().Update(GenerWorkFlowAttr.TodoEmps, todoEmps, GenerWorkFlowAttr.Emps, emps);

			/* 如果是协作*/
			if (this.DealTeamUpNode() == true)
			{
				/*
				 * 1. 判断是否传递过来到达节点，到达人员信息，如果传递过来，就可能是主持人在会签之后执行的发送.
				 * 2. 会签之后执行的发送，就要把到达节点，到达人员存储到数据表里.
				 */

				if (jumpToNode != null)
				{
					/*如果是就记录下来发送到达的节点ID,到达的人员ID.*/
					this.getHisGenerWorkFlow().setHuiQianSendToNodeIDStr(this.getHisNode().getNodeID() + "," + jumpToNode.getNodeID());
					if (jumpToEmp == null)
					{
						this.getHisGenerWorkFlow().setHuiQianSendToEmps("");
					}
					else
					{
						this.getHisGenerWorkFlow().setHuiQianSendToEmps(jumpToEmp);
					}

					this.getHisGenerWorkFlow().Update();

				}
				//this.getHisGenerWorkFlow().setHuiQianTaskSta(HuiQianTaskSta.HuiQianing);
				//调用发送成功事件.
				String sendSuccess = ExecEvent.DoNode(EventListNode.SendSuccess, this, this.HisMsgObjs, null);
				this.HisMsgObjs.AddMsg("info1", sendSuccess, sendSuccess, SendReturnMsgType.Info);

				//执行时效考核.
				Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), this.rptGe.getFID(), this.rptGe.getTitle());
				CC(this.getHisNode());
				//删除增加的抄送信息
				WorkOpt workOpt = new WorkOpt();
				workOpt.setMyPK(this.getWebUser().getNo()+"_"+this.getHisNode().getNodeID()+"_"+this.getWorkID());
				workOpt.Delete();
				this.rptGe.setFlowEnderRDT(DataType.getCurrentDateTimess());

				//设置当前的流程所有的用时.
				this.rptGe.setFlowDaySpan(DataType.GeTimeLimits(this.rptGe.GetValStringByKey(GERptAttr.FlowStartRDT), DataType.getCurrentDateTime()));
				this.rptGe.Update();

				return this.HisMsgObjs;
			}
			this.getHisGenerWorkFlow().setHuiQianTaskSta(HuiQianTaskSta.None);
			//取出来已经存储的到达节点，节点人员信息. 在tempUp模式的会签时，主持人发送会把发送到节点，发送给人员的信息
			// 存储到wf_generworkflow里面.
			if (this.JumpToNode == null)
			{
				/* 如果是就记录下来发送到达的节点ID,到达的人员ID.*/
				String strs = this.getHisGenerWorkFlow().getHuiQianSendToNodeIDStr();

				if (strs.contains(",") == true)
				{
					String[] ndStrs = strs.split("[,]", -1);
					int fromNodeID = Integer.parseInt(ndStrs[0]);
					int toNodeID = Integer.parseInt(ndStrs[1]);
					if (fromNodeID == this.getHisNode().getNodeID())
					{
						JumpToNode = new Node(toNodeID);
						JumpToEmp = this.getHisGenerWorkFlow().getHuiQianSendToEmps();
					}
				}
			}
		}

		//如果是协作组长模式节点, 就判断当前的队列人员是否走完.
		if (this.getTodolistModel() == TodolistModel.TeamupGroupLeader)
		{
			/* 如果是协作组长模式.*/
			if (this.DealTeamupGroupLeader() == true)
			{
				//调用发送成功事件.
				String sendSuccess = ExecEvent.DoNode(EventListNode.SendSuccess, this, this.HisMsgObjs);
				this.HisMsgObjs.AddMsg("info1", sendSuccess, sendSuccess, SendReturnMsgType.Info);
				this.rptGe.setFlowEnderRDT(DataType.getCurrentDateTimess());

				//设置当前的流程所有的用时.
				this.rptGe.setFlowDaySpan(DataType.GeTimeLimits(this.rptGe.GetValStringByKey(GERptAttr.FlowStartRDT), DataType.getCurrentDateTime()));
				this.rptGe.Update();
				return this.HisMsgObjs;
			}
			this.getHisGenerWorkFlow().setHuiQianTaskSta(HuiQianTaskSta.None);
		}

		//如果当前节点是子线程，如果合流节点是退回状态，就要冻结子线程的发送动作。
		if (this.getHisNode().getHisNodeWorkType() == NodeWorkType.SubThreadWork)
		{
			GenerWorkFlow gwfMain = new GenerWorkFlow(this.getHisGenerWorkFlow().getFID());
			if (gwfMain.getWFState() == WFState.ReturnSta)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("err@发送错误:当前流程已经被退回，您不能执行发送操作。技术信息:当前工作节点是子线程状态，主线程是退回状态。", "WorkNode", "send_error_1"));
			}
		}

		//为台州处理 抢办模式下发送后提示给其他人信息.
		if (this.getHisNode().getTodolistModel()== TodolistModel.QiangBan && this.getHisNode().getQiangBanSendAfterRole() != QiangBanSendAfterRole.None && this.getHisGenerWorkFlow().getTodoEmpsNum() > 1)
		{
			//查询出来当前节点的人员.
			GenerWorkerLists gwls = new GenerWorkerLists();
			gwls.Retrieve(GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.FK_Node, this.getHisNode().getNodeID(), null);

			String emps = "";
			for (GenerWorkerList item : gwls.ToJavaList())
			{
				if (item.getEmpNo().equals(this.getExecer()) == true)
				{
					continue; //如果当前人员，就排除掉.
				}

				//要抄送给其他人.
				if (this.getHisNode().getQiangBanSendAfterRole() == QiangBanSendAfterRole.CCToEtcEmps)
				{
					emps += item.getEmpNo() + ",";
				}

				//要发送消息给其他人.
				if (this.getHisNode().getQiangBanSendAfterRole() == QiangBanSendAfterRole.SendMsgToEtcEmps)
				{
					Dev2Interface.Port_SendMsg(item.getEmpNo(), this.getHisGenerWorkFlow().getTitle() + "(被[" + getWebUser().getName() +"]抢办)", "", "QiangBan");
				}
			}

			if (this.getHisNode().getQiangBanSendAfterRole() == QiangBanSendAfterRole.CCToEtcEmps)
			{
				Dev2Interface.Node_CCTo(this.getWorkID(), emps);
			}
		}

		// 启动事务,这里没有实现,在后面做的代码补偿.
		//DBAccess.DoTransactionBegin();

		try
		{
			if (this.getHisNode().getItIsStartNode())
			{
				InitStartWorkDataV2(); // 初始化开始节点数据, 如果当前节点是开始节点.
			}

			//处理发送人，把发送人的信息放入wf_generworkflow 2015-01-14. 原来放入WF_GenerWorkerlist.
			if (this.getHisGenerWorkFlow().getSender().contains(",") == false)
			{
				oldSender = this.getHisGenerWorkFlow().getSender(); //旧发送人,在回滚的时候把该发送人赋值给他.
			}
			else
			{
				oldSender = this.getHisGenerWorkFlow().getSender().split("[,]", -1)[0];
			}
			this.getHisGenerWorkFlow().setSender(getWebUser().getNo() + "," + getWebUser().getName() +";");


			///#region 处理退回的情况.
			if (this.getHisGenerWorkFlow().getWFState() == WFState.ReturnSta || this.getHisGenerWorkFlow().GetParaInt("WFState") == WFState.ReturnSta.getValue())
			{
				this.getHisGenerWorkFlow().SetPara("WFState","0");
				///#region 当前节点是分流节点但是是子线程退回的节点,需要直接发送给子线程
				if ((this.getHisNode().getHisRunModel() == RunModel.FL || this.getHisNode().getHisRunModel() == RunModel.FHL) && this.getHisGenerWorkFlow().getFID() != 0 && this.JumpToNode == null)
				{
					Paras ps = new Paras();
					ps.SQL = "SELECT NDFrom,EmpFrom,EmpFromT FROM ND" + Integer.parseInt(this.getHisNode().getFlowNo()) + "Track WHERE ActionType IN(2,201) AND WorkID=" + dbStr + "WorkID  ORDER BY RDT DESC";
					ps.Add(TrackAttr.WorkID, this.getWorkID());
					DataTable mydt11 = DBAccess.RunSQLReturnTable(ps);
					if (mydt11.Rows.size() == 0)
					{
						throw new RuntimeException(bp.wf.Glo.multilingual("@没有找到退回流程的记录.", "WorkNode", "not_found_my_expected_data", new String[0]));
					}

					this.JumpToNode = new Node(Integer.parseInt(mydt11.Rows.get(0).getValue(0).toString()));
					this.JumpToEmp = mydt11.Rows.get(0).getValue(1).toString();
					String toEmpName = mydt11.Rows.get(0).getValue(2).toString();

					/**处理发送的数据*/
					GenerWorkerList myGwl = new GenerWorkerList();
					myGwl.setEmpNo(getWebUser().getNo());
					myGwl.setNodeID(this.getHisNode().getNodeID());
					myGwl.setWorkID(this.getWorkID());
					if (myGwl.RetrieveFromDBSources() == 0)
					{
						throw new RuntimeException(bp.wf.Glo.multilingual("@没有找到自己期望的数据，再退回并发送的时候.", "WorkNode", "not_found_my_expected_data", new String[0]));
					}
					myGwl.setItIsPass(false);
					myGwl.setPassInt(-2);
					myGwl.Update();

					GenerWorkerLists gwls = new GenerWorkerLists();
					gwls.Retrieve(GenerWorkerListAttr.WorkID, this.getHisGenerWorkFlow().getWorkID(), GenerWorkerListAttr.FK_Node, this.JumpToNode.getNodeID(), GenerWorkerListAttr.IsPass, 5, null);
					if (gwls.size() == 0)
					{
						throw new RuntimeException(bp.wf.Glo.multilingual("@没有找到退回节点的工作人员列表数据.[WorkID=" + this.getHisGenerWorkFlow().getWorkID() + "]", "WorkNode", "not_found_receiver_expected_data", new String[0]));
					}

					GenerWorkerList gwl = gwls.get(0) instanceof GenerWorkerList ? (GenerWorkerList)gwls.get(0) : null;


					///#region 要计算当前人员的应完成日期
					// 计算出来 退回到节点的应完成时间.
					//增加天数. 考虑到了节假日.
					Date dtOfShould = Glo.AddDayHoursSpan(new Date(), this.getHisNode().getTimeLimit(), this.getHisNode().getTimeLimitHH(), this.getHisNode().getTimeLimitMM(), this.getHisNode().getTWay());

					// 应完成日期.
					String sdt = DateUtils.format(dtOfShould,DataType.getSysDateTimeFormat());

					///#endregion

					//更新日期，为了考核.
					if (this.getHisNode().getHisCHWay() == CHWay.None)
					{
						gwl.setSDT("无");
					}
					else
					{
						gwl.setSDT(sdt);
					}
					gwl.setRDT(DataType.getCurrentDateTimess());
					gwl.setPassInt(0);
					gwl.setItIsPass(false);
					gwl.Update();

					GenerWorkerLists ens = new GenerWorkerLists();
					ens.AddEntity(gwl);
					this.HisWorkerLists = ens;

					this.addMsg(SendReturnMsgFlag.VarAcceptersID, gwl.getEmpNo(), gwl.getEmpNo(), SendReturnMsgType.SystemMsg);
					this.addMsg(SendReturnMsgFlag.VarAcceptersName, gwl.getEmpName(), gwl.getEmpName(), SendReturnMsgType.SystemMsg);
					String[] para = new String[2];
					para[0] = gwl.getEmpNo();
					para[1] = gwl.getEmpName();
					String str = bp.wf.Glo.multilingual("@当前工作已经发送给退回人({0},{1}).", "WorkNode", "current_work_send_to_returner", para);

					this.addMsg(SendReturnMsgFlag.OverCurr, str, null, SendReturnMsgType.Info);

					this.getHisGenerWorkFlow().setWFState(WFState.Runing);
					this.getHisGenerWorkFlow().setNodeID(gwl.getNodeID());
					this.getHisGenerWorkFlow().setNodeName(gwl.getNodeName());

					this.getHisGenerWorkFlow().setTodoEmps(gwl.getEmpNo() + "," + gwl.getEmpName() + ";");
					this.getHisGenerWorkFlow().setTodoEmpsNum(0);
					this.getHisGenerWorkFlow().setTaskSta(TaskSta.None);
					this.getHisGenerWorkFlow().Update();

					//写入track.
					this.AddToTrack(ActionType.Forward, this.JumpToEmp, gwl.getEmpNo(), this.JumpToNode.getNodeID(), this.JumpToNode.getName(), bp.wf.Glo.multilingual("退回后发送", "WorkNode", "send_error_2"));

					//调用发送成功事件.
					String sendSuccess = ExecEvent.DoNode(EventListNode.SendSuccess, this, this.HisMsgObjs);

					this.HisMsgObjs.AddMsg("info21", sendSuccess, sendSuccess, SendReturnMsgType.Info);

					//执行时效考核.
					Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), this.rptGe.getFID(), this.rptGe.getTitle());
					this.rptGe.setFlowEnderRDT(DataType.getCurrentDateTimess());

					//设置当前的流程所有的用时.
					this.rptGe.setFlowDaySpan(DataType.GeTimeLimits(this.rptGe.GetValStringByKey(GERptAttr.FlowStartRDT), DataType.getCurrentDateTime()));
					this.rptGe.Update();
					CC(this.getHisNode());

					return this.HisMsgObjs;
				}

				///#endregion 当前节点是分流节点但是是子线程退回的节点

				/* 检查该退回是否是原路返回 ? */
				ps = new Paras();
				ps.SQL = "SELECT NDFrom,EmpFrom,EmpFromT FROM ND" + Integer.parseInt(this.getHisNode().getFlowNo()) + "Track WHERE ActionType IN(2,201) AND WorkID=" + dbStr + "WorkID  AND NDTo=" + this.getHisGenerWorkFlow().getNodeID() + "   ORDER BY RDT DESC";

				ps.Add(TrackAttr.WorkID, this.getWorkID());
				DataTable mydt = DBAccess.RunSQLReturnTable(ps);

				boolean isBackTracking = this.getHisGenerWorkFlow().GetParaBoolen("IsBackTracking");
				int returnNodeID = 0;
				if (mydt.Rows.size() != 0)
				{
					returnNodeID = Integer.parseInt(mydt.Rows.get(0).getValue(0).toString());
					//isBackTracking = int.Parse(mydt.Rows.get(0).getValue(3).toString());
				}
				if (mydt.Rows.size() != 0 && isBackTracking == true && (this.JumpToNode == null || this.JumpToNode.getNodeID() == returnNodeID))
				{
					//有可能查询出来多个，因为按时间排序了，只取出最后一次退回的，看看是否有退回并原路返回的信息。

					/*确认这次退回，是退回并原路返回 ,  在这里初始化它的工作人员, 与将要发送的节点. */
					this.JumpToNode = new Node(Integer.parseInt(mydt.Rows.get(0).getValue(0).toString()));

					this.JumpToEmp = mydt.Rows.get(0).getValue(1).toString();
					String toEmpName = mydt.Rows.get(0).getValue(2).toString();


					///#region 如果当前是退回, 并且当前的运行模式是按照流程图运行.
					if (this.getHisGenerWorkFlow().getTransferCustomType() == TransferCustomType.ByCCBPMDefine)
					{
						if (this.JumpToNode.getTodolistModel()== TodolistModel.Order || this.JumpToNode.getTodolistModel()== TodolistModel.TeamupGroupLeader || this.JumpToNode.getTodolistModel()== TodolistModel.Teamup)
						{
							/*如果是多人处理节点.*/
							this.DealReturnOrderTeamup();

							//写入track.
							this.AddToTrack(ActionType.Forward, this.JumpToEmp, toEmpName, this.JumpToNode.getNodeID(), this.JumpToNode.getName(), bp.wf.Glo.multilingual("退回后发送", "WorkNode", "send_error_2"));

							//调用发送成功事件.
							String sendSuccess = ExecEvent.DoNode(EventListNode.SendSuccess, this, this.HisMsgObjs);

							this.HisMsgObjs.AddMsg("info21", sendSuccess, sendSuccess, SendReturnMsgType.Info);

							//执行时效考核.
							Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), this.rptGe.getFID(), this.rptGe.getTitle());
							this.rptGe.setFlowEnderRDT(DataType.getCurrentDateTimess());

							//设置当前的流程所有的用时.
							this.rptGe.setFlowDaySpan(DataType.GeTimeLimits(this.rptGe.GetValStringByKey(GERptAttr.FlowStartRDT), DataType.getCurrentDateTime()));
							this.rptGe.Update();
							CC(this.getHisNode());
							return this.HisMsgObjs;
						}
					}

					///#endregion 如果当前是退回, 并且当前的运行模式是按照流程图运行.*/


					///#region  如果当前是退回. 并且当前的运行模式按照自由流程设置方式运行
					if (this.getHisGenerWorkFlow().getTransferCustomType() == TransferCustomType.ByWorkerSet)
					{
						if (this.getHisGenerWorkFlow().getTodolistModel() == TodolistModel.Order || this.JumpToNode.getTodolistModel()== TodolistModel.TeamupGroupLeader || this.getHisGenerWorkFlow().getTodolistModel() == TodolistModel.Teamup)
						{
							/*如果是多人处理节点.*/
							this.DealReturnOrderTeamup();

							//写入track.
							this.AddToTrack(ActionType.Forward, this.JumpToEmp, toEmpName, this.JumpToNode.getNodeID(), this.JumpToNode.getName(), bp.wf.Glo.multilingual("退回后发送(按照自定义运行模式)", "WorkNode", "send_error_2"));

							//调用发送成功事件.
							String sendSuccess = ExecEvent.DoNode(EventListNode.SendSuccess, this, this.HisMsgObjs);
							this.HisMsgObjs.AddMsg("info21", sendSuccess, sendSuccess, SendReturnMsgType.Info);

							//执行时效考核.
							Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), this.rptGe.getFID(), this.rptGe.getTitle());
							this.rptGe.setFlowEnderRDT(DataType.getCurrentDateTimess());

							//设置当前的流程所有的用时.
							this.rptGe.setFlowDaySpan(DataType.GeTimeLimits(this.rptGe.GetValStringByKey(GERptAttr.FlowStartRDT), DataType.getCurrentDateTime()));
							this.rptGe.Update();
							CC(this.getHisNode());
							return this.HisMsgObjs;
						}
					}

					///#endregion  如果当前是退回. 并且当前的运行模式按照自由流程设置方式运行


					///#region 当前节点不是队列，协助组长，协作模式的处理
					if (this.JumpToNode.getItIsBackResetAccepter() == true)
					{
						//重新计算处理人
						try
						{
							WorkNode town = new WorkNode(this.getWorkID(), this.JumpToNode.getNodeID());
							FindWorker fw = new FindWorker();
							DataTable empdt = fw.DoIt(this.getHisFlow(), this, town);
							String empNos = "";
							String empName = "";
							if (empdt != null)
							{
								for (DataRow dr : empdt.Rows)
								{
									empNos += dr.getValue("No").toString() + ",";
								}
								this.JumpToEmp = empNos;
							}

						}
						catch (RuntimeException ex)
						{
							//throw new RuntimeException(ex.getMessage());
							String msg = ex.getMessage();
							if (msg.indexOf("url@") != -1)
							{
								if(DataType.IsNullOrEmpty(jumpToEmp) == false)
									this.JumpToEmp = jumpToEmp;
								else
									throw new RuntimeException(ex.getMessage());
								//this.addMsg("退回并原路返回", "退回并原路返回重新计算接收人," + this.JumpToNode.getNodeID() + "-" + this.JumpToNode.getName() + "的接收人规则由上一步发送人选择,待办仍发送到退回人" + this.JumpToEmp);
							}
							else
							{
								throw new RuntimeException(ex.getMessage());
							}
						}

					}

					///#endregion 当前节点不是队列，协助组长，协作模式的处理
				}

			}

			///#endregion 处理退回的情况.

			//做了不可能性的判断.
			if (this.getHisGenerWorkFlow().getNodeID() != this.getHisNode().getNodeID())
			{
				/*
				// 2020-05-21 在计算中心出现一次错误. 节点, 当前的节点，与FK_Flow不再一个流程里面。
				// 没有找到原因.
				*/
				String[] para = new String[5];
				para[0] = String.valueOf(this.getWorkID());
				para[1] = String.valueOf(this.getHisGenerWorkFlow().getNodeID());
				para[2] = this.getHisGenerWorkFlow().getNodeName();
				para[3] = String.valueOf(this.getHisNode().getNodeID());
				para[4] = this.getHisNode().getName();
				throw new RuntimeException(bp.wf.Glo.multilingual("@流程出现错误:工作ID={0},当前活动点({1} {2})与发送点({3} {4})不一致.", "WorkNode", "send_error_3", para));
			}

			// 检查完成条件。
			if (jumpToNode != null && this.getHisNode().getItIsEndNode() == true)
			{
				/* 是跳转的情况，并且是最后的节点，就不检查流程完成条件。*/
			}
			else
			{
				//检查流程完成条件.
				this.CheckCompleteCondition();
			}


			///#region  处理自由流程. add by zhoupeng. 2014-11-23.
			if (jumpToNode == null && this.getHisGenerWorkFlow().getTransferCustomType() == TransferCustomType.ByWorkerSet)
			{
				if (this.getHisNode().GetParaBoolen(NodeAttr.IsYouLiTai) == true)
				{
					// 如果没有指定要跳转到的节点，并且当前处理手工干预的运行状态.
					_transferCustom = TransferCustom.GetNextTransferCustom(this.getWorkID(), this.getHisNode().getNodeID());
					if (_transferCustom == null)
					{
						/* 表示执行到这里结束流程. */
						this.setStopFlow(true);
						this.getHisGenerWorkFlow().setWFState(WFState.CompleteEnd);
						this.rptGe.setWFState(WFState.CompleteEnd);
						String msg1 = this.getHisWorkFlow().DoFlowOver(ActionType.FlowOver, bp.wf.Glo.multilingual("流程已经按照设置的步骤成功结束", "WorkNode", "wf_end_success"), this.getHisNode(), this.rptGe);
						CC(this.getHisNode());
						this.addMsg(SendReturnMsgFlag.End, msg1);
					}
					else
					{
						this.JumpToNode = new Node(_transferCustom.getNodeID());
						this.JumpToEmp = _transferCustom.getWorker();
						this.getHisGenerWorkFlow().setTodolistModel(_transferCustom.getTodolistModel());
					}
				}
				else
				{
					//当前为自由流程，需要先判断它的下一个节点是否为固定节点，为固定节点需要发送给固定节点，为游离态则运行自定义的节点
					Nodes nds = (new Directions()).GetHisToNodes(this.getHisNode().getNodeID(), false);
					if (nds.size() == 0)
					{
						/* 表示执行到这里结束流程. */
						this.setStopFlow(true);
						this.getHisGenerWorkFlow().setWFState(WFState.CompleteEnd);
						this.rptGe.setWFState(WFState.CompleteEnd);
						String msg1 = this.getHisWorkFlow().DoFlowOver(ActionType.FlowOver, bp.wf.Glo.multilingual("流程已经按照设置的步骤成功结束", "WorkNode", "wf_end_success"), this.getHisNode(), this.rptGe);
						CC(this.getHisNode());
						this.addMsg(SendReturnMsgFlag.End, msg1);
					}
					if (nds.size() == 1)
					{
						Node toND = (Node)nds.get(0);
						if (toND.GetParaBoolen(NodeAttr.IsYouLiTai) == true)
						{
							// 如果没有指定要跳转到的节点，并且当前处理手工干预的运行状态.
							_transferCustom = TransferCustom.GetNextTransferCustom(this.getWorkID(), this.getHisNode().getNodeID());
							this.JumpToNode = new Node(_transferCustom.getNodeID());
							this.JumpToEmp = _transferCustom.getWorker();
							this.getHisGenerWorkFlow().setTodolistModel(_transferCustom.getTodolistModel());
						}
						else
						{
							this.JumpToNode = toND;
						}
					}
					if (nds.size() > 1)
					{
						//如果都是游离态就按照自由流程运行，否则抛异常
						for (Node nd : nds.ToJavaList())
						{
							if (nd.GetParaBoolen(NodeAttr.IsYouLiTai) == false)
							{
								throw new RuntimeException("err@该流程运行是自由流程，" + this.getHisNode().getName() + "需要设置方向条件，或者把此节点转向的所有节点设置为游离态");
							}
						}
						// 如果没有指定要跳转到的节点，并且当前处理手工干预的运行状态.
						_transferCustom = TransferCustom.GetNextTransferCustom(this.getWorkID(), this.getHisNode().getNodeID());
						this.JumpToNode = new Node(_transferCustom.getNodeID());
						this.JumpToEmp = _transferCustom.getWorker();
						this.getHisGenerWorkFlow().setTodolistModel(_transferCustom.getTodolistModel());
					}
				}
			}

			///#endregion  处理自由流程. add by zhoupeng. 2014-11-23.

			// 处理质量考核，在发送前。
			this.DealEval();

			// 加入系统变量.
			if (this.isStopFlow())
			{
				this.addMsg(SendReturnMsgFlag.IsStopFlow, "1", bp.wf.Glo.multilingual("流程已经结束", "WorkNode", "wf_end_success"), SendReturnMsgType.Info);
			}
			else
			{
				this.addMsg(SendReturnMsgFlag.IsStopFlow, "0", bp.wf.Glo.multilingual("流程未结束", "WorkNode", "wf_end_success"), SendReturnMsgType.SystemMsg);
			}

			if (this.isStopFlow() == true)
			{
				//设置缓存中的流程状态
				this.getHisGenerWorkFlow().setWFState(WFState.CompleteEnd);
				this.rptGe.setWFState(WFState.CompleteEnd);
				// 执行 自动 启动子流程.
				CallAutoSubFlow(this.getHisNode(), 0); //启动本节点上的.

				//执行考核
				Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), 0, this.getHisGenerWorkFlow().getTitle());
				this.rptGe.setFlowEnderRDT(DataType.getCurrentDateTimess());

				//设置当前的流程所有的用时.
				this.rptGe.setFlowDaySpan(DataType.GeTimeLimits(this.rptGe.GetValStringByKey(GERptAttr.FlowStartRDT), DataType.getCurrentDateTime()));
				this.rptGe.Update();

				//执行抄送. 2020-04-28 修改只要启动抄送规则就执行抄送
				//CC(this.getHisNode());

				//判断当前流程是否子流程，是否启用该流程结束后，主流程自动运行到下一节点
				String msg = WorkNodePlus.SubFlowEvent(this);
				if (DataType.IsNullOrEmpty(msg) == false)
				{
					this.HisMsgObjs.AddMsg("info", msg, msg, SendReturnMsgType.Info);
				}
				//流程结束时 同步数据
				if (this.getHisFlow().getDTSWay() == DataDTSWay.WhenStopFlow)
				{
					WorkNodePlus.DTSData(this.getHisFlow(), this.getHisGenerWorkFlow(), this.rptGe, this.getHisNode(), this.isStopFlow());
				}


				return HisMsgObjs;
			}

			//@增加发送到子流程的判断.
			if (jumpToNode != null && this.getHisNode().getFlowNo().equals(jumpToNode.getFlowNo())==false)
			{
				/*判断是否是延续子流程. */
				return NodeSendToYGFlow(jumpToNode, jumpToEmp);
			}


			///#region 2019-09-25 计算未来处理人.
			if (this.getHisNode().getItIsStartNode() == true && this.getHisFlow().getItIsFullSA() == true)
			{
				if(DataType.IsNullOrEmpty(this.JumpToEmp)) {
					FullSA fa = new FullSA();
					fa.DoIt2023(this); //自动计算接受人.

					//设置版本号.
					this.getHisGenerWorkFlow().SetPara("SADataVer", this.getHisFlow().GetParaString("SADataVer"));
				}
			}
			///#region 不是开始节点，流程创建数据版本后重新计算未来处理人
			if (this.getHisFlow().getItIsFullSA() == true && this.getHisNode().getItIsStartNode() == false) {
				//判断是否需要重新计算？根据版本号。
				if (this.getHisGenerWorkFlow().GetParaString("SADataVer").equals(this.getHisFlow().GetParaString("SADataVer")) == false) {
					FullSA sa = new FullSA();
					sa.DoIt2023(this); //设置重新计算接收人方向.
					this.getHisGenerWorkFlow().SetPara("SADataVer", this.getHisGenerWorkFlow().GetParaString("SADataVer"));
				}
			}
			///#endregion 不是开始节点，流程创建数据版本后重新计算未来处理人
			///#endregion 计算未来处理人.


			///#region 2019-09-25 计算业务字段存储到 wf_generworkflow atpara字段里，用于显示待办信息.
			if (this.getHisNode().getItIsStartNode() && DataType.IsNullOrEmpty(this.getHisFlow().getBuessFields()) == false)
			{
				//存储到表里atPara  @BuessFields=电话^Tel^18992323232;地址^Addr^山东成都;
				String[] expFields = this.getHisFlow().getBuessFields().split("[,]", -1);
				String exp = "";
				Attrs attrs = this.rptGe.getEnMap().getAttrs();
				for (String item : expFields)
				{
					if (DataType.IsNullOrEmpty(item) == true)
					{
						continue;
					}
					if (attrs.contains(item) == false)
					{
						continue;
					}

					Attr attr = attrs.GetAttrByKey(item);
					exp += attr.getDesc() + "^" + attr.getKey() + "^" + this.rptGe.GetValStrByKey(item);
				}
				this.getHisGenerWorkFlow().setBuessFields(exp);
			}

			///#endregion 计算业务字段存储到 wf_generworkflow atpara字段里，用于显示待办信息.


			///#region 第二步: 进入核心的流程运转计算区域. 5*5 的方式处理不同的发送情况.
			// 执行节点向下发送的25种情况的判断.
			this.NodeSend_Send_5_5();

			//通过 55 之后要判断是否要结束流程，如果结束流程就执行相关的更新。
			if (this.isStopFlow())
			{
				this.rptGe.setWFState(WFState.CompleteEnd);
				this.Func_DoSetThisWorkOver();

				this.getHisGenerWorkFlow().setWFState(WFState.CompleteEnd);
				this.getHisGenerWorkFlow().Update(); //added by liuxc,2016-10=24,最后节点更新Sender字段
				//判断当前流程是否子流程，是否启用该流程结束后，主流程自动运行到下一节点
				String msg = WorkNodePlus.SubFlowEvent(this);
				if (DataType.IsNullOrEmpty(msg) == false)
				{
					this.HisMsgObjs.AddMsg("info", msg, msg, SendReturnMsgType.Info);
				}

			}

			if (this.isStopFlow() == false)
			{
				//如果是退回状态，就把是否原路返回的轨迹去掉.
				if (this.getHisGenerWorkFlow().getWFState() == WFState.ReturnSta)
				{
					this.getHisGenerWorkFlow().SetPara("IsBackTracking", 0);
				}

				this.Func_DoSetThisWorkOver();

				//判断当前流程是子流程，并且启用运行到该节点时主流程自动运行到下一个节点
				String msg = WorkNodePlus.SubFlowEvent(this);
				if (DataType.IsNullOrEmpty(msg) == false)
				{
					this.HisMsgObjs.AddMsg("info", msg, msg, SendReturnMsgType.Info);
				}


				if (town != null && town.getHisNode().getHisBatchRole()== BatchRole.Group)
				{
					this.getHisGenerWorkFlow().setItIsCanBatch(true);
					this.getHisGenerWorkFlow().Update();
				}
			}

			//计算从发送到现在的天数.
			this.rptGe.setFlowDaySpan(DataType.GeTimeLimits(this.getHisGenerWorkFlow().getRDT()));
			this.rptGe.setFlowEndNode(this.getHisGenerWorkFlow().getNodeID());
			long fid = this.rptGe.getFID();
			this.rptGe.setTitle(this.getHisGenerWorkFlow().getTitle());
			this.rptGe.Update();

			///#endregion 第二步: 5*5 的方式处理不同的发送情况.


			///#region 第三步: 处理发送之后的业务逻辑.
			//把当前节点表单数据copy的流程数据表里.
			this.DoCopyCurrentWorkDataToRpt();

			//处理合理节点的1变N的问题.
			this.CheckFrm1ToN();

			//处理子线程的独立表单向合流节点的独立表单明细表的数据汇总.
			this.CheckFrmHuiZongToDtl();

			///#endregion 第三步: 处理发送之后的业务逻辑.


			///#region 执行抄送.
			//执行抄送.
			if (this.getHisNode().getItIsEndNode() == false && this.isStopFlow()==false)
			{
				//执行抄送
				CC(this.getHisNode());
			}

			//DBAccess.DoTransactionCommit(); //提交事务.

			///#endregion 处理主要业务逻辑.


			///#region 执行 自动 启动子流程.
			CallAutoSubFlow(this.getHisNode(), 0); //启动本节点上的.
			if (this.town != null)
			{
				CallAutoSubFlow(this.town.getHisNode(), 1);
			}

			///#endregion 执行启动子流程.


			///#region 处理流程数据与业务表的数据同步.
//			if (this.getHisFlow().getDTSWay() != DataDTSWay.None)
//			{
//				WorkNodePlus.DTSData(this.getHisFlow(), this.getHisGenerWorkFlow(), this.rptGe, this.getHisNode(), this.isStopFlow());
//			}
			if (this.getHisFlow().getDTSWay() != DataDTSWay.None)
			{
				WorkNodePlus.DTSData(this.getHisFlow(), this.getHisGenerWorkFlow(), this.rptGe, this.getHisNode(), this.isStopFlow());
			}
			///#endregion 处理流程数据与业务表的数据同步.


			///#region 处理发送成功后的消息提示
			if (this.getHisNode().getHisTurnToDeal() == TurnToDeal.SpecMsg)
			{
				String htmlInfo = "";
				String textInfo = "";


				///#region 判断当前处理人员，可否处理下一步工作.
				if (this.town != null && this.HisRememberMe != null && this.HisRememberMe.getEmps().contains("@" + getWebUser().getNo() + "@") == true)
				{
					String url = "MyFlow.htm?FK_Flow=" + this.getHisFlow().getNo() + "&WorkID=" + this.getWorkID() + "&FK_Node=" + town.getHisNode().getNodeID() + "&FID=" + this.rptGe.getFID();
					textInfo = bp.wf.Glo.multilingual("@下一步工作您仍然可以处理。", "WorkNode", "have_permission_next");
					this.addMsg(SendReturnMsgFlag.MsgOfText, textInfo, "");
				}

				///#endregion 判断当前处理人员，可否处理下一步工作.

				String msgOfSend = this.getHisNode().getTurnToDealDoc();
				if (msgOfSend.contains("@"))
				{
					Attrs attrs = this.getHisWork().getEnMap().getAttrs();
					for (Attr attr : attrs)
					{
						if (msgOfSend.contains("@") == false)
						{
							continue;
						}
						msgOfSend = msgOfSend.replace("@" + attr.getKey(), this.getHisWork().GetValStrByKey(attr.getKey()));
					}
				}

				if (msgOfSend.contains("@") == true)
				{
					/*说明有一些变量在系统运行里面.*/
					Object tempVar = msgOfSend;
					String msgOfSendText = tempVar instanceof String ? (String)tempVar : null;
					for (SendReturnObj item : this.HisMsgObjs)
					{
						if (DataType.IsNullOrEmpty(item.MsgFlag))
						{
							continue;
						}

						if (msgOfSend.contains("@") == false)
						{
							break;
						}

						msgOfSendText = msgOfSendText.replace("@" + item.MsgFlag, item.MsgOfText);

						if (item.MsgOfHtml != null)
						{
							msgOfSend = msgOfSend.replace("@" + item.MsgFlag, item.MsgOfHtml);
						}
						else
						{
							msgOfSend = msgOfSend.replace("@" + item.MsgFlag, item.MsgOfText);
						}
					}

					this.HisMsgObjs.OutMessageHtml = msgOfSend + htmlInfo;
					this.HisMsgObjs.OutMessageText = msgOfSendText + textInfo;
				}
				else
				{
					this.HisMsgObjs.OutMessageHtml = msgOfSend;
					this.HisMsgObjs.OutMessageText = msgOfSend;
				}

				//return msgOfSend;
			}

			///#endregion 处理发送成功后事件.


			///#region 如果需要跳转.
			if (town != null)
			{
				if (this.town.getHisNode().getItIsSubThread() == true && this.town.getHisNode().getItIsSubThread() == true)
				{
					this.addMsg(SendReturnMsgFlag.VarToNodeID, String.valueOf(town.getHisNode().getNodeID()), String.valueOf(town.getHisNode().getNodeID()), SendReturnMsgType.SystemMsg);
					this.addMsg(SendReturnMsgFlag.VarToNodeName, town.getHisNode().getName(), town.getHisNode().getName(), SendReturnMsgType.SystemMsg);
				}


///#warning 如果这里设置了自动跳转，现在去掉了. 2014-11-07.
				//if (town.getHisNode().HisDeliveryWay == DeliveryWay.ByPreviousOperSkip)
				//{
				//    town.NodeSend();
				//    this.HisMsgObjs = town.HisMsgObjs;
				//}
			}

			///#endregion 如果需要跳转.


			///#region 设置流程的标记.
			if (this.getHisNode().getItIsStartNode())
			{
				if (this.rptGe.getPWorkID() != 0 && this.getHisGenerWorkFlow().getPWorkID() == 0)
				{
					bp.wf.Dev2Interface.SetParentInfo(this.getHisFlow().getNo(), this.getWorkID(), this.rptGe.getPWorkID());

					//写入track, 调用了父流程.
					Node pND = new Node(rptGe.getPNodeID());
					fid = 0;
					if (pND.getHisNodeWorkType() == NodeWorkType.SubThreadWork)
					{
						GenerWorkFlow gwf = new GenerWorkFlow(this.rptGe.getPWorkID());
						fid = gwf.getFID();
					}

					String paras = "@CFlowNo=" + this.getHisFlow().getNo() + "@CWorkID=" + this.getWorkID();

					Glo.AddToTrack(ActionType.StartChildenFlow, rptGe.getPFlowNo(), rptGe.getPWorkID(), fid, pND.getNodeID(), pND.getName(), getWebUser().No, getWebUser().Name, pND.getNodeID(), pND.getName(), getWebUser().No, getWebUser().Name, "<a href='" + SystemConfig.getHostURLOfBS() + "/WF/WFRpt.htm?FK_Flow=" + this.getHisFlow().getNo() + "&WorkID=" + this.getWorkID() + "' target=_blank >打开子流程</a>", paras);
				}
				else if (SystemConfig.isBSsystem() == true)
				{
					/*如果是BS系统*/
					String pflowNo = ContextHolderUtils.getRequest().getParameter("PFlowNo");
					if (DataType.IsNullOrEmpty(pflowNo) == false && DataType.IsNumStr(pflowNo))
					{
						String pWorkID = ContextHolderUtils.getRequest().getParameter("PWorkID"); // BP.Sys.Base.Glo.Request.QueryString["PWorkID");
						String pNodeID = ContextHolderUtils.getRequest().getParameter("PNodeID"); // BP.Sys.Base.Glo.Request.QueryString["PNodeID");
						String pEmp = ContextHolderUtils.getRequest().getParameter("PEmp"); // BP.Sys.Base.Glo.Request.QueryString["PEmp");

						// 设置成父流程关系.
						bp.wf.Dev2Interface.SetParentInfo(this.getHisFlow().getNo(), this.getWorkID(), Long.parseLong(pWorkID));

						//写入track, 调用了父流程.
						Node pND = new Node(pNodeID);
						fid = 0;
						if (pND.getHisNodeWorkType() == NodeWorkType.SubThreadWork)
						{
							GenerWorkFlow gwf = new GenerWorkFlow(Long.parseLong(pWorkID));
							fid = gwf.getFID();
						}
						String paras = "@CFlowNo=" + this.getHisFlow().getNo() + "@CWorkID=" + this.getWorkID();
						Glo.AddToTrack(ActionType.StartChildenFlow, pflowNo, Long.parseLong(pWorkID), Long.parseLong(String.valueOf(fid)), pND.getNodeID(), pND.getName(), getWebUser().No, getWebUser().Name, pND.getNodeID(), pND.getName(), getWebUser().No, getWebUser().Name, "<a href='" + SystemConfig.getHostURLOfBS() + "/WF/WFRpt.htm?FK_Flow=" + this.getHisFlow().getNo() + "&WorkID=" + this.getWorkID() + "' target=_blank >" + bp.wf.Glo.multilingual("打开子流程", "WorkNode", "open_sub_wf") + "</a>", paras);
					}
				}
			}

			///#endregion 设置流程的标记.

			//执行时效考核.
			Glo.InitCH(this.getHisFlow(), this.getHisNode(), this.getWorkID(), this.rptGe.getFID(), this.rptGe.getTitle());


			///#region 触发下一个节点的自动发送, 处理国机的需求.  （去掉:2019-05-05）
			if (this.HisMsgObjs.getVarToNodeID() != 0 && this.town != null && 1 == 2 && this.town.getHisNode().getWhoExeIt() != 0)
			{
				String currUser = bp.web.WebUser.getNo();
				String[] myEmpStrs = this.HisMsgObjs.getVarAcceptersID().split("[,]", -1);
				for (String emp : myEmpStrs)
				{
					if (DataType.IsNullOrEmpty(emp))
					{
						continue;
					}

					try
					{
						//让这个人登录.
						Emp empEn = new Emp(emp);
						bp.wf.Dev2Interface.Port_Login(emp);
						if (this.getHisNode().getItIsSubThread() == true && this.town.getHisNode().getItIsSubThread() == false)
						{
							/*如果当前的节点是子线程，并且发送到的节点非子线程。
							 * 就是子线程发送到非子线程的情况。
							 */
							this.HisMsgObjs = bp.wf.Dev2Interface.Node_SendWork(this.getHisNode().getFlowNo(), this.getHisWork().getFID());
						}
						else
						{
							this.HisMsgObjs = bp.wf.Dev2Interface.Node_SendWork(this.getHisNode().getFlowNo(), this.getHisWork().getOID());
						}
					}
					catch (java.lang.Exception e)
					{
						// 可能是正常的阻挡发送，操作不必提示。
						//this.HisMsgObjs.AddMsg("Auto"
					}
					bp.wf.Dev2Interface.Port_Login(currUser);
					//使用一个人处理就可以了.
					break;
				}
			}

			///#endregion 触发下一个节点的自动发送。


			///#region 判断当前处理人员，可否处理下一步工作.
			if (this.isStopFlow() == false && this.town != null && this.HisRememberMe != null && this.HisRememberMe.getEmps().contains("@" + getWebUser().getNo() + "@") == true)
			{
				String url = "MyFlow.htm?FK_Flow=" + this.getHisFlow().getNo() + "&WorkID=" + this.getWorkID() + "&FK_Node=" + town.getHisNode().getNodeID() + "&FID=" + this.rptGe.getFID();
				//    String htmlInfo = "@<a href='" + url + "' >下一步工作您仍然可以处理，点击这里现在处理。</a>.";
				String textInfo = bp.wf.Glo.multilingual("@下一步工作您仍然可以处理。", "WorkNode", "have_permission_next");
			}

			///#endregion 判断当前处理人员，可否处理下一步工作.


			String userNo = bp.web.WebUser.getNo();
			this.Deal_Event();
			//返回这个对象.
			return this.HisMsgObjs;
		}
		catch (RuntimeException ex)
		{
			//当下一个节点的接收人规则为上一个人员选择的时候，就会抛出人员选择器链接,让其选择人员.
			if (ex.getMessage().indexOf("url@") == 0)
			{
				throw new RuntimeException(ex.getMessage());
			}

			this.WhenTranscactionRollbackError(ex);

			//DBAccess.DoTransactionRollback();

			Log.DebugWriteError(Arrays.toString(ex.getStackTrace()));

			throw new RuntimeException(ex.getMessage());
			//throw new Exception(ex.Message + "  tech@info:" + ex.getStackTrace());
		}
	}

	/**
	 自动启动子流程
	 */
	public final void CallAutoSubFlow(Node nd, int invokeTime) throws Exception {
		//自动发起流程的数量.
		//if (nd.SubFlowAutoNum == 0)
		//    return;

		SubFlowAutos subs = new SubFlowAutos(nd.getNodeID());
		if (subs.size() == 0)
		{
			return;
		}

		for (SubFlowAuto sub : subs.ToJavaList())
		{
			if (sub.getInvokeTime() != invokeTime)
			{
				continue;
			}

			//启动下级子流程.
			if (sub.getHisSubFlowModel() == SubFlowModel.SubLevel)
			{

				///#region 判断启动权限.
				if (sub.getStartOnceOnly() == true)
				{
					/* 如果仅仅被启动一次.*/
					String sql = "SELECT COUNT(*) as Num FROM WF_GenerWorkFlow WHERE PWorkID=" + this.getWorkID() + " AND FK_Flow='" + sub.getSubFlowNo() + "'";
					if (DBAccess.RunSQLReturnValInt(sql) > 0)
					{
						continue; //已经启动了，就不启动了。
					}
				}

				if (sub.getCompleteReStart() == true)
				{
					/* 该子流程启动的流程运行结束后才可以启动.*/
					String sql = "SELECT Starter, RDT,WFState FROM WF_GenerWorkFlow WHERE PWorkID=" + this.getWorkID() + " AND FK_Flow='" + sub.getSubFlowNo() + "' AND WFSta !=" + WFSta.Complete.getValue();
					DataTable dt = DBAccess.RunSQLReturnTable(sql);
					if (dt.Rows.size() == 1 && Integer.parseInt(dt.Rows.get(0).getValue("WFState").toString()) != 0)
					{
						continue; //已经启动的流程运行没有结束了，就不启动了。 WFState 是草稿
					}
				}
				//指定的流程启动后,才能启动该子流程。
				if (sub.getItIsEnableSpecFlowStart() == true)
				{
					String[] fls = sub.getSpecFlowStart().split("[,]", -1);
					boolean isHave = false;
					for (String fl : fls)
					{
						if (DataType.IsNullOrEmpty(fl) == true)
						{
							continue;
						}

						String sql = "SELECT COUNT(*) as Num FROM WF_GenerWorkFlow WHERE PWorkID=" + this.getWorkID() + " AND FK_Flow='" + fl + "'";
						if (DBAccess.RunSQLReturnValInt(sql) == 0)
						{
							isHave = true;
							break; //还没有启动过.
						}
					}
					if (isHave == true)
					{
						continue; //就不能启动该子流程.
					}
				}

				//指定的流程结束后,才能启动该子流程。
				if (sub.getItIsEnableSpecFlowOver() == true)
				{
					String[] fls = sub.getSpecFlowOver().split("[,]", -1);
					boolean isHave = false;
					for (String fl : fls)
					{
						if (DataType.IsNullOrEmpty(fl) == true)
						{
							continue;
						}

						String sql = "SELECT COUNT(*) as Num FROM WF_GenerWorkFlow WHERE PWorkID=" + this.getWorkID() + " AND FK_Flow='" + fl + "' AND WFState=3";
						if (DBAccess.RunSQLReturnValInt(sql) == 0)
						{
							isHave = true;
							break; //还没有启动过/或者没有完成.
						}
					}
					if (isHave == true)
					{
						continue; //就不能启动该子流程.
					}
				}

				if (sub.getItIsEnableSQL() == true)
				{
					String sql = sub.getSpecSQL();
					if (DataType.IsNullOrEmpty(sql) == true)
					{
						continue;
					}

					sql = bp.wf.Glo.DealExp(sql, this.rptGe);
					if (DBAccess.RunSQLReturnValInt(sql) == 0) //不能执行子流程
					{
						continue;
					}
				}

				//按指定子流程节点
				if (sub.getItIsEnableSameLevelNode() == true)
				{
					throw new RuntimeException("配置错误，按指定平级子流程节点只使用触发平级子流程，不能触发下级子流程");
				}


				// #region 判断数据源类型.0.仅仅发起一次，使用当前表单数据源.
				if (sub.getDBSrcType() == 0) {

					///#region 检查sendModel.
					// 设置开始节点待办.
					if (sub.getSendModel() == 0) {
						//创建workid.
						long subWorkID = bp.wf.Dev2Interface.Node_CreateBlankWork(sub.getSubFlowNo(), getWebUser().getNo());

						//设置父子关系.
						bp.wf.Dev2Interface.SetParentInfo(sub.getSubFlowNo(), subWorkID, this.getHisGenerWorkFlow().getWorkID(), getWebUser().No, nd.getNodeID());

						//执行保存.
						bp.wf.Dev2Interface.Node_SaveWork(subWorkID, this.rptGe.getRow());

						//为开始节点设置待办.
						bp.wf.Dev2Interface.Node_AddTodolist(subWorkID, getWebUser().getNo());

						bp.wf.Dev2Interface.Flow_ReSetFlowTitle(subWorkID);

						//写入消息.
						this.addMsg("SubFlow" + sub.getSubFlowNo(), "流程[" + sub.getFlowName() + "]启动成功.");
					}

					//发送到下一个环节去.
					if (sub.getSendModel() == 1) {
						//创建workid.
						long subWorkID = bp.wf.Dev2Interface.Node_CreateBlankWork(sub.getSubFlowNo(), getWebUser().getNo());

						//设置父子关系.
						bp.wf.Dev2Interface.SetParentInfo(sub.getSubFlowNo(), subWorkID, this.getHisGenerWorkFlow().getWorkID(), null, 0, true);

						//执行发送到下一个环节..
						SendReturnObjs sendObjs = bp.wf.Dev2Interface.Node_SendWork(sub.getSubFlowNo(), subWorkID, this.rptGe.getRow(), null);
						this.addMsg("SubFlow" + sub.getSubFlowNo(), sendObjs.ToMsgOfHtml());
					}

					if (sub.getSubFlowHidTodolist() == true) {
						//发送子流程后不显示父流程待办，设置父流程已经的待办已经处理 100
						int nodeID = 0;
						if (nd.getNodeID() == this.town.getHisNode().getNodeID()) {
							nodeID = nd.getNodeID();
						} else {
							nodeID = this.town.getHisNode().getNodeID();
						}
						DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=100 Where WorkID=" + this.getHisGenerWorkFlow().getWorkID() + " AND FK_Node=" + nodeID);
					}
				} //判断数据源类型.0.仅仅发起一次，使用当前表单数据源.


				// #region 判断数据源类型.1.使用SQL数据源发起流程.
				if (sub.getDBSrcType() == 1)
				{
					String sql = sub.getDBSrcDoc();
					if (DataType.IsNullOrEmpty(sql) == true)
						continue;

					sql = bp.wf.Glo.DealExp(sql, this.rptGe);
					DataTable dt = DBAccess.RunSQLReturnTable(sql);
					Hashtable ht = this.rptGe.getRow();

					//遍历数据源.
					for (DataRow dr : dt.Rows)
					{
						for (DataColumn dc : dt.Columns) {
							if (dt.Columns.contains(dc.ColumnName))
								ht.put(dc.ColumnName, dr.get(dc.ColumnName));
							//	else
							//		ht.put(dc.ColumnName, dr[dc.ColumnName]);
						}

						//创建workid.
						long subWorkID = Dev2Interface.Node_CreateBlankWork(sub.getSubFlowNo(), WebUser.getNo());

						//设置父子关系.
						Dev2Interface.SetParentInfo(sub.getSubFlowNo(), subWorkID, this.getHisGenerWorkFlow().getWorkID(), null, 0, true);

						//执行发送到下一个环节..
						SendReturnObjs sendObjs = Dev2Interface.Node_SendWork(sub.getSubFlowNo(), subWorkID, ht, null);
						this.addMsg("SubFlow" + sub.getSubFlowNo(), sendObjs.ToMsgOfHtml());


						if (sub.getSubFlowHidTodolist() == true)
						{
							//发送子流程后不显示父流程待办，设置父流程已经的待办已经处理 100
							int nodeID = 0;
							if (nd.getNodeID() == this.town.getHisNode().getNodeID())
								nodeID = nd.getNodeID();
							else
								nodeID = this.town.getHisNode().getNodeID();
							DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=100 Where WorkID=" + this.getHisGenerWorkFlow().getWorkID() + " AND FK_Node=" + nodeID);
						}
						// #endregion 检查sendModel.
					} //结束循环.
				}
				//  #endregion 判断数据源类型.1.使用SQL数据源发起流程
				///#endregion 检查sendModel.
			}

			//如果要自动启动平级的子流程，就需要判断当前是是否是子流程，如果不是子流程，就不能启动。
			if (sub.getHisSubFlowModel() == SubFlowModel.SameLevel && this.getHisGenerWorkFlow().getPWorkID() != 0)
			{
				///#region 判断启动权限.
				if (sub.getStartOnceOnly() == true)
				{
					/* 如果仅仅被启动一次.*/
					String sql = "SELECT COUNT(*) as Num FROM WF_GenerWorkFlow WHERE PWorkID=" + this.getHisGenerWorkFlow().getPWorkID() + " AND FK_Flow='" + sub.getSubFlowNo() + "'";
					if (DBAccess.RunSQLReturnValInt(sql) > 0)
						continue; //已经启动了，就不启动了。
				}

				if (sub.getCompleteReStart() == true)
				{
					/* 该子流程启动的流程运行结束后才可以启动.*/
					String sql = "SELECT Starter, RDT,WFState FROM WF_GenerWorkFlow WHERE PWorkID=" + this.getHisGenerWorkFlow().getPWorkID() + " AND FK_Flow='" + sub.getSubFlowNo() + "' AND WFSta !=" + WFSta.Complete.getValue();
					DataTable dt = DBAccess.RunSQLReturnTable(sql);
					if (dt.Rows.size() == 1 && Integer.parseInt(dt.Rows.get(0).getValue("WFState").toString()) != 0)
						continue; //已经启动的流程运行没有结束了，就不启动了。 WFState 0是草稿可以发起
				}


				//指定的流程启动后,才能启动该子流程。
				if (sub.getItIsEnableSpecFlowStart() == true)
				{
					String[] fls = sub.getSpecFlowStart().split("[,]", -1);
					boolean isHave = false;
					for (String fl : fls)
					{
						if (DataType.IsNullOrEmpty(fl) == true)
							continue;

						String sql = "SELECT COUNT(*) as Num FROM WF_GenerWorkFlow WHERE PWorkID=" + this.getHisGenerWorkFlow().getPWorkID() + " AND FK_Flow='" + fl + "'";
						if (DBAccess.RunSQLReturnValInt(sql) == 0)
						{
							isHave = true;
							break; //还没有启动过.
						}
					}
					if (isHave == true)
						continue; //就不能启动该子流程.
				}

				if (sub.getItIsEnableSpecFlowOver() == true)
				{
					String[] fls = sub.getSpecFlowOver().split("[,]", -1);
					boolean isHave = false;
					for (String fl : fls)
					{
						if (DataType.IsNullOrEmpty(fl) == true)
							continue;

						String sql = "SELECT COUNT(*) as Num FROM WF_GenerWorkFlow WHERE PWorkID=" + this.getHisGenerWorkFlow().getPWorkID() + " AND FK_Flow='" + fl + "' AND WFState=3";
						if (DBAccess.RunSQLReturnValInt(sql) == 0)
						{
							isHave = true;
							break; //还没有启动过.
						}
					}
					if (isHave == true)
					{
						continue; //就不能启动该子流程.
					}
				}
				//按指定的SQL配置，如果结果值是>=1就执行
				if (sub.getItIsEnableSQL() == true)
				{
					String sql = sub.getSpecSQL();
					if (DataType.IsNullOrEmpty(sql) == true)
					{
						continue;
					}

					sql = bp.wf.Glo.DealExp(sql, this.rptGe);
					if (DBAccess.RunSQLReturnValInt(sql) == 0) //不能执行子流程
					{
						continue;
					}
				}

				//按指定子流程节点
				if (sub.getItIsEnableSameLevelNode() == true)
				{
					String levelNodes = sub.getSameLevelNode();
					if (DataType.IsNullOrEmpty(levelNodes) == true)
					{
						continue;
					}

					String[] nodes = levelNodes.split("[;]", -1);
					boolean isHave = false;
					for (String val : nodes)
					{
						String[] flowNode = val.split("[,]", -1);
						if (flowNode.length != 2)
						{
							isHave = true;
							break; //不能启动.
						}


						GenerWorkFlow gwfSub = new GenerWorkFlow();
						int count = gwfSub.Retrieve(GenerWorkFlowAttr.PWorkID, this.getHisGenerWorkFlow().getPWorkID(), GenerWorkFlowAttr.FK_Flow, flowNode[0]);
						if (count == 0)
						{
							isHave = true;
							break; //不能启动.
						}
						if (gwfSub.getWFSta() != WFSta.Complete)
						{
							//判断该节点是不是子线程
							Node subNode = new Node(Integer.parseInt(flowNode[1]));
							String sql = "";
							if (subNode.getItIsSubThread() == true)
							{
								sql = "SELECT COUNT(*) as Num FROM WF_GenerWorkerlist WHERE FID=" + gwfSub.getWorkID() + " AND FK_Flow='" + flowNode[0] + "' AND FK_Node=" + Integer.parseInt(flowNode[1]) + " AND IsEnable=1 AND IsPass=1";
							}
							else
							{
								sql = "SELECT COUNT(*) as Num FROM WF_GenerWorkerlist WHERE WorkID=" + gwfSub.getWorkID() + " AND FK_Flow='" + flowNode[0] + "' AND FK_Node=" + Integer.parseInt(flowNode[1]) + " AND IsEnable=1 AND IsPass=1";
							}
							if (DBAccess.RunSQLReturnValInt(sql) == 0)
							{
								isHave = true;
								break; //不能启动.
							}
						}

					}
					if (isHave == true)
					{
						continue;
					}

				}

				///#endregion


				///#region 检查sendModel.
				// 设置开始节点待办.
				if (sub.getSendModel() == 0)
				{
					//创建workid.
					long subWorkID = bp.wf.Dev2Interface.Node_CreateBlankWork(sub.getSubFlowNo(), getWebUser().getNo());

					//设置父子关系.
					bp.wf.Dev2Interface.SetParentInfo(sub.getSubFlowNo(), subWorkID, this.getHisGenerWorkFlow().getPWorkID(), getWebUser().No, nd.getNodeID(), true);

					//执行保存.
					bp.wf.Dev2Interface.Node_SaveWork(subWorkID, this.rptGe.getRow());

					//为开始节点设置待办.
					bp.wf.Dev2Interface.Node_AddTodolist(subWorkID, getWebUser().getNo());

					bp.wf.Dev2Interface.Flow_ReSetFlowTitle(subWorkID);


					//增加启动该子流程的同级子流程信息
					GenerWorkFlow gwf = new GenerWorkFlow(subWorkID);
					gwf.SetPara("SLFlowNo", this.getHisNode().getFlowNo());
					gwf.SetPara("SLNodeID", this.getHisNode().getNodeID());
					gwf.SetPara("SLWorkID", this.getHisGenerWorkFlow().getWorkID());
					gwf.Update();

					//写入消息.
					this.addMsg("SubFlow" + sub.getSubFlowNo(), "流程[" + sub.getFlowName() + "]启动成功.");
				}

				//发送到下一个环节去.
				if (sub.getSendModel() == 1)
				{
					//创建workid.
					long subWorkID = bp.wf.Dev2Interface.Node_CreateBlankWork(sub.getSubFlowNo(), getWebUser().getNo());

					//设置父子关系.
					bp.wf.Dev2Interface.SetParentInfo(sub.getSubFlowNo(), subWorkID, this.getHisGenerWorkFlow().getPWorkID(), getWebUser().No, nd.getNodeID());

					//增加启动该子流程的同级子流程信息
					GenerWorkFlow gwf = new GenerWorkFlow(subWorkID);
					gwf.SetPara("SLFlowNo", this.getHisNode().getFlowNo());
					gwf.SetPara("SLNodeID", this.getHisNode().getNodeID());
					gwf.SetPara("SLWorkID", this.getHisGenerWorkFlow().getWorkID());
					gwf.Update();


					//执行发送到下一个环节..
					SendReturnObjs sendObjs = bp.wf.Dev2Interface.Node_SendWork(sub.getSubFlowNo(), subWorkID, this.rptGe.getRow(), null);

					this.addMsg("SubFlow" + sub.getSubFlowNo(), sendObjs.ToMsgOfHtml());
				}

				///#endregion 检查sendModel.

			}
		}

		return;
	}
	/**
	 处理事件
	 */
	private void Deal_Event() throws Exception {

		///#region 处理节点到达事件..
		//执行发送到达事件.
		if (this.isStopFlow() == false && this.town != null)
		{
			ExecEvent.DoNode(EventListNode.WorkArrive, this.town);
		}

		///#endregion 处理节点到达事件.


		///#region 处理发送成功后事件.
		try
		{
			//调起发送成功后的事件，把参数传入进去。
			if (this.SendHTOfTemp != null)
			{
				for (Object key : this.SendHTOfTemp.keySet())
				{
					if (rptGe.getRow().containsKey(key) == true)
					{
						this.rptGe.getRow().SetValByKey(key.toString(), this.SendHTOfTemp.get(key) instanceof String ? (String)this.SendHTOfTemp.get(key) : null);
					}
					else
					{
						this.rptGe.getRow().put(key.toString(), this.SendHTOfTemp.get(key) instanceof String ? (String)this.SendHTOfTemp.get(key) : null);
					}
				}
			}

			//执行发送.
			if (this.isStopFlow() == false){
				String sendSuccess = ExecEvent.DoNode(EventListNode.SendSuccess, this);
				if (sendSuccess != null)
				{
					this.addMsg(SendReturnMsgFlag.SendSuccessMsg, sendSuccess);
				}
			}

		}
		catch (RuntimeException ex)
		{
			this.addMsg(SendReturnMsgFlag.SendSuccessMsgErr, "err@执行事件出现SendSuccessMsgErr：" + ex.getMessage());
		}

		///#endregion 处理发送成功后事件.
	}
	/**
	 手工的回滚提交失败信息，补偿没有事务的缺陷。

	 @param ex
	 */
	private void WhenTranscactionRollbackError(RuntimeException ex) throws Exception {
		/*在提交错误的情况下，回滚数据。*/


		///#region 如果是分流点下同表单发送失败再次发送就出现错误.
		if (this.town != null && this.town.getHisNode().getHisNodeWorkType() == NodeWorkType.SubThreadWork && this.town.getHisNode().getHisRunModel() == RunModel.SubThreadSameWorkID)
		{
			/*如果是子线程*/
			DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE FID=" + this.getWorkID() + " AND FK_Node=" + this.town.getHisNode().getNodeID());
			//删除子线程数据.
			if (DBAccess.IsExitsObject(this.town.getHisWork().getEnMap().getPhysicsTable()) == true)
			{
				DBAccess.RunSQL("DELETE FROM " + this.town.getHisWork().getEnMap().getPhysicsTable() + " WHERE FID=" + this.getWorkID());
			}
		}

		///#endregion 如果是分流点下同表单发送失败再次发送就出现错误.

		try
		{
			//有可能删除之前的日志，即退回又运行到该节点，处理的办法是求出轨迹运行的最后处理时间.
			String maxDT = DBAccess.RunSQLReturnStringIsNull("Select Max(RDT) FROM ND" + Integer.parseInt(this.getHisFlow().getNo()) + "Track WHERE WorkID=" + this.getWorkID(), null);
			if (maxDT != null)
			{
				//删除发生的日志.
				DBAccess.RunSQL("DELETE FROM ND" + Integer.parseInt(this.getHisFlow().getNo()) + "Track WHERE WorkID=" + this.getWorkID() + " AND NDFrom=" + this.getHisNode().getNodeID() + " AND ActionType=" + ActionType.Forward.getValue() + " AND RDT='" + maxDT + "'");
			}

			// 删除考核信息。
			this.DealEvalUn();

			// 把工作的状态设置回来。
			if (this.getHisNode().getItIsStartNode())
			{
				ps = new Paras();
				ps.SQL = "UPDATE " + this.getHisFlow().getPTable() + " SET WFState=" + _WFState.getValue() + " WHERE OID=" + dbStr + "OID ";
				ps.Add(GERptAttr.OID, this.getWorkID());
				DBAccess.RunSQL(ps);
				//  this.HisWork.Update(GERptAttr.WFState, (int)WFState.Runing);
			}

			// 把流程的状态设置回来。
			GenerWorkFlow gwf = new GenerWorkFlow();
			gwf.setWorkID(this.getWorkID());
			if (gwf.RetrieveFromDBSources() == 0)
			{
				return;
			}
			//还原WF_GenerWorkList
			if (gwf.getIsOver()==false && this.getHisNode().getItIsStartNode() == false)
			{
				String ndTrack = "ND" + Integer.parseInt(this.getHisFlow().getNo()) + "Track";
				String actionType = ActionType.Forward.getValue() + "," + ActionType.FlowOver.getValue() + "," + ActionType.ForwardFL.getValue() + "," + ActionType.ForwardHL.getValue();
				String sql = "SELECT  * FROM " + ndTrack + " WHERE   ActionType IN (" + actionType + ")  and WorkID=" + this.getWorkID() + " ORDER BY RDT DESC, NDFrom ";
				DataTable dt = DBAccess.RunSQLReturnTable(sql);
				if (dt.Rows.size() == 0)
				{
					throw new RuntimeException("@工作ID为:" + this.getWorkID() + "的数据不存在.");
				}

				String starter = "";
				boolean isMeetSpecNode = false;
				GenerWorkerList currWl = new GenerWorkerList();
				for (DataRow dr : dt.Rows)
				{
					int ndFrom = Integer.parseInt(dr.getValue("NDFrom").toString());
					Node nd = new Node(ndFrom);

					String ndFromT = dr.getValue("NDFromT").toString();
					String EmpFrom = dr.getValue(TrackAttr.EmpFrom).toString();
					String EmpFromT = dr.getValue(TrackAttr.EmpFromT).toString();

					// 增加上 工作人员的信息.
					GenerWorkerList gwl = new GenerWorkerList();
					gwl.setWorkID(this.getWorkID());
					gwl.setFlowNo(this.getHisFlow().getNo());

					gwl.setNodeID(ndFrom);
					gwl.setNodeName(ndFromT);

					if (gwl.getNodeID() == this.getHisNode().getNodeID())
					{
						gwl.setItIsPass(false);
						currWl = gwl;
					}
					else
					{
						gwl.setItIsPass(true);
					}

					gwl.setEmpNo(EmpFrom);
					gwl.setEmpName(EmpFromT);
					if (gwl.getIsExits())
					{
						continue; //有可能是反复退回的情况.
					}

					Emp emp = new Emp(gwl.getEmpNo());
					gwl.setDeptNo(emp.getDeptNo());

					gwl.setSDT(dr.getValue("RDT").toString());
					gwl.setDTOfWarning(gwf.getSDTOfNode());

					gwl.setItIsEnable(true);
					gwl.setWhoExeIt(nd.getWhoExeIt());
					gwl.Insert();
				}
			}
			else
			{
				//执行数据.
				ps = new Paras();
				ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=0 WHERE FK_Emp=" + dbStr + "FK_Emp AND WorkID=" + dbStr + "WorkID AND FK_Node=" + dbStr + "FK_Node ";
				//ps.AddFK_Emp();
				ps.Add("FK_Emp", getWebUser().No, false);
				ps.Add("WorkID", this.getWorkID());
				ps.Add("FK_Node", this.getHisNode().getNodeID());
				DBAccess.RunSQL(ps);
			}


			if (gwf.getWFState() != WFState.Blank || gwf.getNodeID() != this.getHisNode().getNodeID())
			{
				/* 如果这两项其中有一项有变化。*/
				gwf.setNodeID(this.getHisNode().getNodeID());
				gwf.setNodeName(this.getHisNode().getName());
				gwf.setWFState(_WFState);

				//设置他的旧发送人.
				if (DataType.IsNullOrEmpty(oldSender) == false)
				{
					Emp emp = new Emp(oldSender);
					this.getHisGenerWorkFlow().setSender(emp.getUserID() + "," + emp.getName() + ";");
				}
				gwf.Update();
			}

			// Node startND = this.getHisNode().HisFlow.HisStartNode;
			//2024-07-02 代码先注释掉，出现抛异常的情况下表单数据丢失的情况
			/*Nodes nds = this.getHisNode().getHisToNodes();
			for (Node nd : nds.ToJavaList())
			{
				if (nd.getNodeID() == this.getHisNode().getNodeID())
				{
					continue;
				}

				Work mwk = nd.getHisWork();
				if (mwk.getEnMap().getPhysicsTable() == this.getHisFlow().getPTable() || mwk.getEnMap().getPhysicsTable() == this.getHisWork().getEnMap().getPhysicsTable())
				{
					continue;
				}

				mwk.setOID(this.getWorkID());
				try
				{
					mwk.DirectDelete();
				}
				catch (java.lang.Exception e)
				{
					mwk.CheckPhysicsTable();
					mwk.DirectDelete();
				}
			}*/

			//执行发送失败事件，让开发人员回滚相关数据.
			ExecEvent.DoNode(EventListNode.SendError, this);

		}
		catch (RuntimeException ex1)
		{
			if (this.town != null && this.town.getHisWork() != null)
			{
				this.town.getHisWork().CheckPhysicsTable();
			}

			if (this.rptGe != null)
			{
				this.rptGe.CheckPhysicsTable();
			}
			String er1 = bp.wf.Glo.multilingual("@发送失败后,回滚发送失败数据出现错误:{0}.", "WorkNode", "wf_eng_error_4", Arrays.toString(ex1.getStackTrace()));
			String er2 = bp.wf.Glo.multilingual("@回滚发送失败数据出现错误:{0}.", "WorkNode", "wf_eng_error_3");
			throw new RuntimeException(ex.getMessage() + er1 + er2);
		}
	}

	///#endregion


	///#region 用户到的变量
	public GenerWorkerLists HisWorkerLists = null;
	private GenerWorkFlow _HisGenerWorkFlow;
	public final GenerWorkFlow getHisGenerWorkFlow() throws Exception {
		if (_HisGenerWorkFlow == null && this.getWorkID() != 0)
		{

			_HisGenerWorkFlow = new GenerWorkFlow(this.getWorkID());


			SendNodeWFState = _HisGenerWorkFlow.getWFState(); //设置发送前的节点状态。
		}
		return _HisGenerWorkFlow;
	}
	public final void setHisGenerWorkFlow(GenerWorkFlow value)
	{
		_HisGenerWorkFlow = value;
	}
	private long _WorkID = 0;
	/**
	 工作ID.
	 */
	public final long getWorkID()
	{
		return _WorkID;
	}
	public final void setWorkID(long value)
	{
		_WorkID = value;
	}
	/**
	 原来的发送人.
	 */
	private String oldSender = null;

	///#endregion


	public GERpt rptGe = null;
	private void InitStartWorkDataV2() throws Exception {
		//判断开始节点是不是流程发起的节点，有可能是退回或者其他流程流转过来的节点数据
		boolean isStart = true;
		if (DataType.IsNullOrEmpty(this.getHisGenerWorkFlow().getSender()) == false)
		{
			isStart = false;
		}
		if (isStart == false)
		{
			return;
		}
		if (isStart == true)
		{
			this.rptGe.SetValByKey("RDT", DataType.getCurrentDateTimess());
		}

		/*如果是开始流程判断是不是被吊起的流程，如果是就要向父流程写日志。*/
		if (SystemConfig.isBSsystem())
		{
			String fk_nodeFrom = ContextHolderUtils.getRequest().getParameter("FromNode"); // BP.Sys.Base.Glo.Request.QueryString["FromNode");
			if (DataType.IsNullOrEmpty(fk_nodeFrom) == false)
			{
				Node ndFrom = new Node(Integer.parseInt(fk_nodeFrom));
				String PWorkID = ContextHolderUtils.getRequest().getParameter("PWorkID");
				if (DataType.IsNullOrEmpty(PWorkID))
				{
					PWorkID = ContextHolderUtils.getRequest().getParameter("PWorkID"); //BP.Sys.Base.Glo.Request.QueryString["PWorkID");
				}

				String pTitle = DBAccess.RunSQLReturnStringIsNull("SELECT Title FROM  ND" + Integer.parseInt(ndFrom.getFlowNo()) + "01 WHERE OID=" + PWorkID, "");

				////记录当前流程被调起。
				//  this.AddToTrack(ActionType.StartSubFlow, WebUser.getNo(),
				//  WebUser.getName(), ndFrom.getNodeID(), ndFrom.getFlowName() + "\t\n" + ndFrom.getFlowName(), "被父流程(" + ndFrom.getFlowName() + ":" + pTitle + ")调起.");

				//记录父流程被调起。
				String st1 = null;
				try {
					st1 = Glo.multilingual("{0}发起工作流{1}", "WorkNode", "start_wf", this.getExecerName(), ndFrom.getFlowName());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				String st2 = bp.wf.Glo.multilingual("发起子流程:{0}", "WorkNode", "start_sub_wf", this.getHisFlow().getName());
				bp.wf.Dev2Interface.WriteTrack(this.getHisFlow().getNo(), this.getHisNode().getNodeID(), this.getHisNode().getName(), this.getWorkID(), 0, st1, ActionType.CallChildenFlow, "@PWorkID=" + PWorkID + "@PFlowNo=" + ndFrom.getHisFlow().getNo(), st2, null);
			}
		}

		DBAccess.RunSQL("UPDATE WF_GenerWorkerList SET CDT='" + DataType.getCurrentDateTime() + "',SDT='' , Sender='" + getWebUser().getNo() + "," + getWebUser().getName() +"',IsPass=1,IsRead=1 WHERE FK_Emp='" + getWebUser().getNo() + "' AND FK_Node=" + this.getHisNode().getNodeID() + " AND WorkID=" + this.getWorkID());


		// 再一次生成单据编号.
		if (DataType.IsNullOrEmpty(this.getHisFlow().getBillNoFormat()) == false)
		{
			this.getHisGenerWorkFlow().setBillNo(WorkFlowBuessRole.GenerBillNo(this.getHisFlow().getBillNoFormat(), this.getWorkID(), this.rptGe, this.getHisFlow().getPTable()));
			this.rptGe.setBillNo(this.getHisGenerWorkFlow().getBillNo());
			this.getHisWork().SetValByKey("BillNo", this.getHisGenerWorkFlow().getBillNo());
			if (DataType.IsNullOrEmpty(this.getHisFlow().getTitleRole()) == false && this.getHisFlow().getTitleRole().contains("@BillNo"))
			{
				String title = WorkFlowBuessRole.GenerTitle(this.getHisFlow(), this.getHisWork());
				if (title.contains("@") == true)
				{
					title = WorkFlowBuessRole.GenerTitle(this.getHisFlow(), this.rptGe);
				}
				this.getHisGenerWorkFlow().setTitle(title);
				this.rptGe.setTitle(title);
			}
			if(this.SendHTOfTemp !=null && this.SendHTOfTemp.containsKey("BillNo"))
				this.SendHTOfTemp.put("BillNo",this.rptGe.getBillNo());
		}
		/* 产生开始工作流程记录. */

		///#region 设置流程标题.
		if (this.getHisGenerWorkFlow().getTitle() == null)
		{
			this.getHisGenerWorkFlow().setTitle(bp.wf.WorkFlowBuessRole.GenerTitle(this.getHisFlow(), this.getHisWork()));
		}

		//流程标题.
		this.rptGe.setTitle(this.getHisGenerWorkFlow().getTitle());

		///#endregion 设置流程标题.

		this.getHisWork().SetValByKey("Title", this.getHisGenerWorkFlow().getTitle());
		if (isStart == true)
		{
			this.getHisGenerWorkFlow().setRDT(DataType.getCurrentDateTimess()); // this.HisWork.RDT;
		}
		if (this.getHisGenerWorkFlow().getWFState() == WFState.Runing)
		{
			this.getHisGenerWorkFlow().setStarter(this.getExecer());
			this.getHisGenerWorkFlow().setStarterName(this.getExecerName());
		}
		this.getHisGenerWorkFlow().setFlowNo(this.getHisNode().getFlowNo());
		this.getHisGenerWorkFlow().setFlowName(this.getHisNode().getFlowName());
		this.getHisGenerWorkFlow().setFlowSortNo(this.getHisNode().getHisFlow().getFlowSortNo());
		this.getHisGenerWorkFlow().setSysType(this.getHisNode().getHisFlow().getSysType());
		this.getHisGenerWorkFlow().setNodeID(this.getHisNode().getNodeID());
		this.getHisGenerWorkFlow().setNodeName(this.getHisNode().getName());
		this.getHisGenerWorkFlow().setDeptNo(getWebUser().DeptNo);
		this.getHisGenerWorkFlow().setDeptName(getWebUser().DeptName);

		//按照指定的字段计算
		if (this.getHisFlow().getSDTOfFlowRole() == SDTOfFlowRole.BySpecDateField)
		{
			try
			{
				this.getHisGenerWorkFlow().setSDTOfFlow(this.getHisWork().GetValStrByKey(this.getHisFlow().GetParaString("SDTOfFlowRole_DateField")));
				this.getHisGenerWorkFlow().setRDTOfSetting(this.getHisWork().GetValStrByKey(this.getHisFlow().GetParaString("SDTOfFlowRole_StartDateField")));
			}
			catch (RuntimeException ex)
			{
				String err1 = bp.wf.Glo.multilingual("可能是流程设计错误,获取开始节点[" + this.getHisGenerWorkFlow().getTitle() + "]的整体流程应完成时间有错误,是否包含SysSDTOfFlow字段? 异常信息:{0}.", "WorkNode", "wf_eng_error_5", ex.getMessage());
				Log.DebugWriteError(err1);
				/*获取开始节点的整体流程应完成时间有错误,是否包含SysSDTOfFlow字段? .*/
				if (this.getHisWork().getEnMap().getAttrs().contains(WorkSysFieldAttr.SysSDTOfFlow) == false)
				{
					String err2 = bp.wf.Glo.multilingual("流程设计错误，您设置的流程时效属性是按开始节点表单SysSDTOfFlow字段计算,但是开始节点表单不包含字段 SysSDTOfFlow ,系统错误信息:{0}.", "WorkNode", "wf_eng_error_5", ex.getMessage());
					throw new RuntimeException(err2);
				}
				throw new RuntimeException(bp.wf.Glo.multilingual("@初始化开始节点数据错误:{0}.", "WorkNode", "wf_eng_error_5", ex.getMessage()));
			}
		}
		//按照指定的SQL计算
		if (this.getHisFlow().getSDTOfFlowRole() == SDTOfFlowRole.BySQL)
		{
			String sql = this.getHisFlow().getSDTOfFlowRoleSQL();
			//配置的SQL为空
			if (DataType.IsNullOrEmpty(sql) == false)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@计算流程应完成时间错误,初始化开始节点数据错误:{0}.", "WorkNode", "wf_eng_error_5", "配置的SQL为空"));
			}

			//替换SQL中的参数
			sql = bp.wf.Glo.DealExp(sql, this.getHisWork());
			String sdtOfFlow = DBAccess.RunSQLReturnString(sql);
			if (DataType.IsNullOrEmpty(sdtOfFlow) == false)
			{
				this.getHisGenerWorkFlow().setSDTOfFlow(sdtOfFlow);
			}
			else
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@计算流程应完成时间错误,初始化开始节点数据错误:{0}.", "WorkNode", "wf_eng_error_5", "根据SQL配置查询的结果为空"));
			}
		}

		//按照所有节点之和,
		if (this.getHisFlow().getSDTOfFlowRole() == SDTOfFlowRole.ByAllNodes)
		{
			//获取流程的所有节点
			Nodes nds = new Nodes(this.getHisFlow().getNo());
			Date sdtOfFlow = new Date();
			for (Node nd : nds.ToJavaList())
			{
				if (nd.getItIsStartNode() == true)
				{
					continue;
				}
				if (nd.getHisCHWay() == CHWay.ByTime && nd.GetParaInt("CHWayOfTimeRole", 0) == 0)
				{ //按天、小时考核
					//增加天数. 考虑到了节假日.
					//判断是修改了节点期限的天数
					int timeLimit = nd.getTimeLimit();
					sdtOfFlow = Glo.AddDayHoursSpan(sdtOfFlow, timeLimit, nd.getTimeLimitHH(), nd.getTimeLimitMM(), nd.getTWay());
				}
			}
			this.getHisGenerWorkFlow().setSDTOfFlow(DateUtils.format(sdtOfFlow,DataType.getSysDateTimeFormat()));
		}
		//按照设置的天数
		if (this.getHisFlow().getSDTOfFlowRole() == SDTOfFlowRole.ByDays)
		{
			//获取设置的天数
			int day = this.getHisFlow().GetParaInt("SDTOfFlowRole_Days", 0);
			if (day == 0)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@初始化开始节点数据错误:{0}.", "WorkNode", "wf_eng_error_5", "设置流程完成时间不能为0天"));
			}
			this.getHisGenerWorkFlow()
					.setSDTOfFlow(DateUtils.format(DateUtils.addDay(new Date(), day), DataType.getSysDateTimeFormat()));		}
		//加入两个参数. 2013-02-17
		if (this.getHisGenerWorkFlow().getPWorkID() != 0)
		{
			this.rptGe.setPWorkID(this.getHisGenerWorkFlow().getPWorkID());
			this.rptGe.setPFlowNo(this.getHisGenerWorkFlow().getPFlowNo());
			this.rptGe.setPNodeID(this.getHisGenerWorkFlow().getPNodeID());
			this.rptGe.setPEmp(this.getHisGenerWorkFlow().getPEmp());
		}

		if (isStart == true)
		{
			this.rptGe.setFlowStartRDT(DataType.getCurrentDateTimess());
		}
		this.rptGe.setFlowEnderRDT(DataType.getCurrentDateTimess());
		//设置发起时间
		if (isStart == true)
		{
			this.rptGe.SetValByKey("RDT", DataType.getCurrentDateTimess());
		}
	}
	/**
	 执行将当前工作节点的数据copy到Rpt里面去.
	 */
	public final void DoCopyCurrentWorkDataToRpt() throws Exception {
		/* 如果两个表一致就返回..*/
		// 把当前的工作人员增加里面去.
		String str = rptGe.GetValStrByKey(GERptAttr.FlowEmps);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = "@";
		}

		if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserIDOnly)
		{
			if (str.contains("@" + this.getExecer() + "@") == false)
			{
				rptGe.SetValByKey(GERptAttr.FlowEmps, str + this.getExecer() + "@");
			}
		}

		if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserNameOnly)
		{
			if (str.contains("@" + getWebUser().getName() +"@") == false)
			{
				rptGe.SetValByKey(GERptAttr.FlowEmps, str + this.getExecerName() + "@");
			}
		}

		if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserIDUserName)
		{
			try {
				if (str.contains("@" + this.getExecer() + "," + this.getExecerName()) == false)
				{
					rptGe.SetValByKey(GERptAttr.FlowEmps, str + this.getExecer() + "," + this.getExecerName() + "@");
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		rptGe.setFlowEnder(this.getExecer());
		rptGe.setFlowEnderRDT(DataType.getCurrentDateTimess());

		//设置当前的流程所有的用时.
		rptGe.setFlowDaySpan(DataType.GeTimeLimits(this.rptGe.GetValStringByKey(GERptAttr.FlowStartRDT), DataType.getCurrentDateTime()));

		try {
			if (this.getHisNode().getItIsEndNode() || this.isStopFlow())
			{
				rptGe.setWFState(WFState.CompleteEnd);
			}
			else
			{
				rptGe.setWFState(WFState.Runing);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (this.getHisWork().getEnMap().getPhysicsTable().equals(this.getHisFlow().getPTable()) == false)
		{
			/*将当前的属性复制到rpt表里面去.*/
			DoCopyWorkToRpt(this.getHisWork());
		}
		try {
			rptGe.DirectUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 执行数据copy.

	 @param fromWK
	 */
	public final void DoCopyWorkToRpt(Work fromWK) throws Exception {
		for (Attr attr : fromWK.getEnMap().getAttrs())
		{
			switch (attr.getKey())
			{
				case bp.wf.GERptAttr.FK_NY:
				case bp.wf.GERptAttr.FK_Dept:
				case bp.wf.GERptAttr.FlowDaySpan:
				case bp.wf.GERptAttr.FlowEmps:
				case bp.wf.GERptAttr.FlowEnder:
				case bp.wf.GERptAttr.FlowEnderRDT:
				case bp.wf.GERptAttr.FlowEndNode:
				case bp.wf.GERptAttr.FlowStarter:
				case bp.wf.GERptAttr.Title:
				case bp.wf.GERptAttr.WFSta:
					continue;
				default:
					break;
			}

			Object obj = fromWK.GetValByKey(attr.getKey());
			if (obj == null)
			{
				continue;
			}
			this.rptGe.SetValByKey(attr.getKey(), obj);
		}
		if (this.getHisNode().getItIsStartNode())
		{
			this.rptGe.SetValByKey("Title", fromWK.GetValByKey("Title"));
		}
	}
	/**
	 增加日志

	 @param at 类型
	 @param toEmp 到人员
	 @param toEmpName 到人员名称
	 @param toNDid 到节点
	 @param toNDName 到节点名称
	 @param msg 消息
	 */
	public final void AddToTrack(ActionType at, String toEmp, String toEmpName, int toNDid, String toNDName, String msg) throws Exception {
		AddToTrack(at, toEmp, toEmpName, toNDid, toNDName, msg, this.getHisNode());
	}
	/**
	 增加日志

	 @param at
	 @param gwl
	 @param msg
	 */
	public final void AddToTrack(ActionType at, GenerWorkerList gwl, String msg, long subTreadWorkID, Node nd) throws Exception {
		Track t = new Track();

		if (this.getHisGenerWorkFlow().getFID() == 0)
		{
			t.setWorkID(subTreadWorkID);
			t.setFID(this.getHisWork().getOID());
		}
		else
		{
			t.setWorkID(this.getHisWork().getOID());
			t.setFID(this.getHisGenerWorkFlow().getFID());
		}

		t.setRDT(DataType.getCurrentDateTimess());
		t.setHisActionType(at);

		t.setNDFrom(getNdFrom().getNodeID());
		t.setNDFromT(getNdFrom().getName());

		t.setEmpFrom(this.getExecer());
		t.setEmpFromT(this.getExecerName());

		t.setNDTo(gwl.getNodeID());
		t.setNDToT(gwl.getNodeName());

		t.setEmpTo(gwl.getEmpNo());
		t.setEmpToT(gwl.getEmpName());
		t.setMsg(msg);
		t.FlowNo =this.getHisNode().getFlowNo();
		if(getNdFrom().GetValBooleanByKey("SubmitSFEnable") == true){
			//获取当前人员的GenerWorkerList数据
			GenerWorkerList gwl1 = new GenerWorkerList(this.getHisWork().getOID(),getNdFrom().getNodeID(),WebUser.getNo());
			t.setNodeData("@DeptNo="+gwl1.getDeptNo()+"@DeptName="+gwl1.getDeptName()+"@StaNo="+gwl1.GetValStringByKey("StaNo"));
		}
		else
			t.setNodeData("@DeptNo=" + getWebUser().getDeptNo() + "@DeptName=" + getWebUser().DeptName);

		switch (at)
		{
			case Forward:
			case ForwardAskfor:
			case Start:
			case UnSend:
			case ForwardFL:
			case ForwardHL:
			case TeampUp:
			case Order:
			case SubThreadForward:
			case FlowOver:
			case DeleteFlowByFlag:
			case Skip:
				//判断是否有焦点字段，如果有就把它记录到日志里。
				if (this.getHisNode().getFocusField().length() > 1)
				{
//					String exp = this.getHisNode().getFocusField();
//					if (this.rptGe != null)
//					{
//						exp = bp.wf.Glo.DealExp(exp, this.rptGe);
//					}
//					else
//					{
//						exp = bp.wf.Glo.DealExp(exp, this.getHisWork());
//					}
//
//					t.setMsg(t.getMsg() + exp);
//					if (t.getMsg().contains("@"))
//					{
//						String[] para = new String[4];
//						para[0] = String.valueOf(this.getHisNode().getNodeID());
//						para[1] = this.getHisNode().getName();
//						para[2] = this.getHisNode().getFocusField();
//						para[3] = t.getMsg();
//						Log.DebugWriteError(bp.wf.Glo.multilingual("@在节点({0}, {1})焦点字段被删除了,表达式为:{2}替换的结果为:{3}.", "WorkNode", "delete_focus_field", para));
//					}

				}

				//判断是否有审核组件，把审核信息存储在Msg中
				if (this.getHisNode().getFrmWorkCheckSta() == FrmWorkCheckSta.Enable)
				{
					//获取审核组件信息
					String sql = "SELECT Msg,MyPK From ND" + Integer.parseInt(this.getHisNode().getFlowNo()) + "Track Where WorkID=" + t.getWorkID() + " AND FID=" + t.getFID() + " AND (ActionType=" + ActionType.WorkCheck.getValue() + " OR ActionType="+ActionType.AutoWorkCheck.getValue()+") AND NDFrom=" + getNdFrom().getNodeID() + " AND EmpFrom='" + getWebUser().getNo() + "' ORDER BY RDT DESC";
					DataTable dt = DBAccess.RunSQLReturnTable(sql);
					if (dt.Rows.size() > 0)
					{
						t.setMsg(t.getMsg() + "WorkCheck@" + dt.Rows.get(0).getValue(0).toString());
						t.WriteDB = dt.Rows.get(0).getValue(1).toString();
					}
					else
					{
						t.setMsg(t.getMsg() + "WorkCheck@");
					}

					//把审核组件的立场信息保存在track表中
					String checkTag = Dev2Interface.GetCheckTag(this.getHisNode().getFlowNo(), this.getWorkID(), this.getHisNode().getNodeID(), getWebUser().getNo());
					String[] strs = checkTag.split("[@]", -1);
					for (String str : strs)
					{
						if (str.contains("FWCView") == true)
						{
							t.setTag(t.getTag() + "@" + str);
							break;
						}
					}
				}
				break;
			default:
				break;
		}

		try
		{
			t.Insert();
		}
		catch (java.lang.Exception e)
		{
			t.CheckPhysicsTable();
			t.Insert();
		}


		///#region 处理数据版本.
		if (at == ActionType.SubThreadForward || at == ActionType.StartChildenFlow || at == ActionType.Start || at == ActionType.Forward || at == ActionType.SubThreadForward || at == ActionType.ForwardHL || at == ActionType.FlowOver)
		{
			if (this.getHisNode().getItIsFL())
			{
				at = ActionType.ForwardFL;
			}

			//写入数据轨迹.
			WorkNodePlus.AddNodeFrmTrackDB(this.getHisFlow(), this.getHisNode(), t, this.getHisWork());
			//t.FrmDB11 = this.HisWork.ToJson();
		}

		///#endregion 处理数据版本.

		if (at == ActionType.SubThreadForward || at == ActionType.StartChildenFlow || at == ActionType.Start || at == ActionType.Forward || at == ActionType.SubThreadForward || at == ActionType.ForwardHL || at == ActionType.FlowOver)
		{
			this.getHisGenerWorkFlow().setParasLastSendTruckID(t.getMyPK());
		}
	}
	/**
	 增加日志

	 @param at 类型
	 @param toEmp 到人员
	 @param toEmpName 到人员名称
	 @param toNDid 到节点
	 @param toNDName 到节点名称
	 @param msg 消息
	 */

	public final void AddToTrack(ActionType at, String toEmp, String toEmpName, int toNDid, String toNDName, String msg, Node ndFrom) throws Exception {
		AddToTrack(at, toEmp, toEmpName, toNDid, toNDName, msg, ndFrom, null);
	}

	public final void AddToTrack(ActionType at, String toEmp, String toEmpName, int toNDid, String toNDName, String msg, Node ndFrom, String tag) throws Exception {
		Track t = new Track();

		t.setWorkID(this.getHisWork().getOID());
		t.setFID(this.getHisWork().getFID());

		t.setRDT(DataType.getCurrentDateTimess());

		t.setHisActionType(at);

		t.setNDFrom(ndFrom.getNodeID());
		t.setNDFromT(ndFrom.getName());

		t.setEmpFrom(this.getExecer());
		t.setEmpFromT(this.getExecerName());
		t.FlowNo = this.getHisNode().getFlowNo();
		t.setTag(tag + "@SendNode=" + this.getHisNode().getNodeID());

		if (toNDid == 0)
		{
			toNDid = this.getHisNode().getNodeID();
			toNDName = this.getHisNode().getName();
		}

		t.setNDTo(toNDid);
		t.setNDToT(toNDName);

		t.setEmpTo(toEmp);
		t.setEmpToT(toEmpName);
		t.setMsg(msg);
		if(ndFrom.GetValBooleanByKey("SubmitSFEnable") == true){
			GenerWorkerList gwl = new GenerWorkerList();
			gwl.setWorkID(this.getHisWork().getOID());
			gwl.setNodeID(ndFrom.getNodeID());
			gwl.setEmpNo(WebUser.getNo());
			int i = gwl.RetrieveFromDBSources();
			if(i == 1)
				t.setNodeData("@DeptNo="+gwl.getDeptNo()+"@DeptName="+gwl.getDeptName()+"@StaNo="+gwl.GetValStringByKey("StaNo"));
		}
		else
			t.setNodeData("@DeptNo=" + getWebUser().getDeptNo() + "@DeptName=" + getWebUser().DeptName);

		switch (at)
		{
			case Forward:
			case ForwardAskfor:
			case Start:
			case UnSend:
			case ForwardFL:
			case ForwardHL:
			case TeampUp:
			case Order:
			case SubThreadForward:
			case FlowOver:
			case DeleteFlowByFlag:
			case Skip:
				//判断是否有焦点字段，如果有就把它记录到日志里。
				if (this.getHisNode().getFocusField().length() > 1)
				{
					String exp = this.getHisNode().getFocusField();
					if (this.rptGe != null)
					{
						exp = bp.wf.Glo.DealExp(exp, this.rptGe);
					}
					else
					{
						exp = bp.wf.Glo.DealExp(exp, this.getHisWork());
					}

					t.setMsg(t.getMsg() + exp);
					if (t.getMsg().contains("@"))
					{
						String[] para = new String[4];
						para[0] = String.valueOf(this.getHisNode().getNodeID());
						para[1] = this.getHisNode().getName();
						para[2] = this.getHisNode().getFocusField();
						para[3] = t.getMsg();
						//BP.DA.Log.DebugWriteError(BP.WF.Glo.multilingual("@在节点({0}, {1})焦点字段被删除了,表达式为:{2}替换的结果为:{3}.", "WorkNode", "delete_focus_field", para));
					}
				}
				//判断是否有审核组件，把审核信息存储在Msg中
				if (this.getHisNode().getFrmWorkCheckSta() == FrmWorkCheckSta.Enable)
				{
					//获取审核组件信息
					String sql = "SELECT Msg,MyPK From ND" + Integer.parseInt(this.getHisNode().getFlowNo()) + "Track Where WorkID=" + t.getWorkID() + " AND FID=" + t.getFID() + " AND (ActionType=" + ActionType.WorkCheck.getValue() + " OR ActionType=" + ActionType.AutoWorkCheck.getValue()+") AND NDFrom=" + ndFrom.getNodeID() + " AND EmpFrom='" + getWebUser().getNo() + "' ORDER BY RDT DESC";
					DataTable dt = DBAccess.RunSQLReturnTable(sql);
					if (dt.Rows.size() > 0)
					{
						t.setMsg(t.getMsg() + "WorkCheck@" + dt.Rows.get(0).getValue(0).toString());
						t.WriteDB = dt.Rows.get(0).getValue(1).toString();
					}
					else
					{
						t.setMsg(t.getMsg() + "WorkCheck@");
					}

					//String sql = "SELECT Msg From ND" + int.Parse(this.getHisNode().getFlowNo()) + "Track Where WorkID=" + t.WorkID + " AND FID=" + t.FID + " AND ActionType=" + (int)ActionType.WorkCheck + " AND NDFrom=" + this.Hisnode.getNodeID() + " AND EmpFrom='" + WebUser.getNo() + "'";
					//t.Msg += "WorkCheck@" + DBAccess.RunSQLReturnStringIsNull(sql, "");
					//把审核组件的立场信息保存在track表中
					String checkTag = Dev2Interface.GetCheckTag(this.getHisNode().getFlowNo(), this.getWorkID(), this.getHisNode().getNodeID(), getWebUser().getNo());
					String[] strs = checkTag.split("[@]", -1);
					for (String str : strs)
					{
						if (str.contains("FWCView") == true)
						{
							t.setTag(t.getTag() + "@" + str);
							break;
						}
					}
				}
				break;
			default:
				break;
		}
		try
		{
			// t.setMyPK(t.WorkID + "_" + t.FID + "_"  + t.NDFrom + "_" + t.NDTo +"_"+t.EmpFrom+"_"+t.EmpTo+"_"+ DateTime.Now.ToString("yyMMddHHmmss");
			t.Insert();
		}
		catch (java.lang.Exception e)
		{
			Track.CreateOrRepairTrackTable(t.FlowNo);
			t.Insert();
		}


		///#region 增加,日志.
		if (at == ActionType.SubThreadForward || at == ActionType.StartChildenFlow || at == ActionType.Start || at == ActionType.Forward || at == ActionType.SubThreadForward || at == ActionType.ForwardHL || at == ActionType.FlowOver)
		{
			if (this.getHisNode().getItIsFL())
			{
				at = ActionType.ForwardFL;
			}

			WorkNodePlus.AddNodeFrmTrackDB(this.getHisFlow(), this.getHisNode(), t, this.getHisWork());
			//t.FrmDB = this.HisWork.ToJson();
		}

		///#endregion 增加.


		if (at == ActionType.SubThreadForward || at == ActionType.StartChildenFlow || at == ActionType.Start || at == ActionType.Forward || at == ActionType.SubThreadForward || at == ActionType.ForwardHL || at == ActionType.FlowOver)
		{
			this.getHisGenerWorkFlow().setParasLastSendTruckID(t.getMyPK());
		}
		this.getHisGenerWorkFlow().setSendDT(DataType.getCurrentDateTime());
		this.getHisGenerWorkFlow().Update();
		DBAccess.RunSQL("UPDATE WF_GenerWorkerList SET CDT='" + DataType.getCurrentDateTimess() + "' WHERE WorkID=" + this.getWorkID() + " AND FK_Node=" + this.getHisNode().getNodeID() + " AND FK_Emp='" + bp.web.WebUser.getNo() + "'");
	}
	/**
	 向他们发送消息

	 @param gwls 接收人
	 */
	public final void SendMsgToThem(GenerWorkerLists gwls) throws Exception {
		//if (BP.WF.Glo.IsEnableSysMessage == false)
		//    return;
		//求到达人员的IDs
		String toEmps = "";
		for (GenerWorkerList gwl : gwls.ToJavaList())
		{
			toEmps += gwl.getEmpNo() + ",";
		}

		//处理工作到达事件.
		PushMsgs pms = this.town.getHisNode().getHisPushMsgs();
		for (PushMsg pm : pms.ToJavaList())
		{
			if (!Objects.equals(pm.getEventNo(), EventListNode.WorkArrive))
			{
				continue;
			}

			String msg = pm.DoSendMessage(this.town.getHisNode(), this.town.getHisWork(), null, null, null, toEmps);

			this.addMsg("alert" + pm.getMyPK(), msg, msg, SendReturnMsgType.Info);
		}
		return;
	}
	/**
	 发送前的流程状态。
	 */
	private WFState SendNodeWFState = WFState.Blank;
	/**
	 合流节点是否全部完成？
	 */
	private boolean IsOverMGECheckStand = false;
	private boolean _IsStopFlow = false;
	private boolean isStopFlow()
	{
		return _IsStopFlow;
	}
	private void setStopFlow(boolean value) throws Exception {
		_IsStopFlow = value;
		if (_IsStopFlow == true)
		{
			if (this.rptGe != null)
			{
				this.rptGe.setWFState(WFState.CompleteEnd);
				this.rptGe.Update("WFState", WFState.CompleteEnd.getValue());
			}
		}
	}
	/**
	 检查
	 */
	private void CheckCompleteCondition_IntCompleteEmps() throws Exception {
		String sql = "SELECT FK_Emp,EmpName FROM WF_GenerWorkerlist WHERE WorkID=" + this.getWorkID() + " AND IsPass=1";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		String emps = "@";
		String flowEmps = "@";
		for (DataRow dr : dt.Rows)
		{
			if (emps.contains("@" + dr.getValue(0).toString() + "@") || emps.contains("@" + dr.getValue(0).toString() + "," + dr.getValue(1).toString() + "@"))
			{
				continue;
			}

			emps = emps + dr.getValue(0).toString() + "," + dr.getValue(1).toString() + "@";
			flowEmps = flowEmps + dr.getValue(0).toString() + "," + dr.getValue(1).toString() + "@";
		}
		//追加当前操作人
		if (emps.contains("@" + getWebUser().getNo() + ",") == false)
		{
			emps = emps + getWebUser().getNo() + "," + getWebUser().getName() +"@";
			flowEmps = flowEmps + getWebUser().getNo() + "," + getWebUser().getName() +"@";
		}
		// 给他们赋值.
		this.rptGe.setFlowEmps(flowEmps);
		this.getHisGenerWorkFlow().setEmps(emps);
	}
	/**
	 检查流程、节点的完成条件

	 @return
	 */
	private void CheckCompleteCondition() throws Exception {
		// 执行初始化人员.
		this.CheckCompleteCondition_IntCompleteEmps();

		// 如果结束流程，就增加如下信息 翻译.
		this.getHisGenerWorkFlow().setSender(getWebUser().getNo() + "," + getWebUser().getName() +";");
		this.getHisGenerWorkFlow().setSendDT(DataType.getCurrentDateTime());

		this.rptGe.setFlowEnder(bp.web.WebUser.getNo());
		this.rptGe.setFlowEnderRDT(DataType.getCurrentDateTime());

		this.setStopFlow(false);
		if (this.getHisNode().getItIsEndNode())
		{
			/* 如果流程完成 */
			//   CCWork cc = new CCWork(this);
			// 在流程完成锁前处理消息收听，否则WF_GenerWorkerlist就删除了。

			if (this.getHisGenerWorkFlow().getTransferCustomType() == TransferCustomType.ByCCBPMDefine)
			{
				this.setStopFlow(true);
				this.getHisGenerWorkFlow().setWFState(WFState.CompleteEnd);
				this.rptGe.setWFState(WFState.CompleteEnd);
				String msg = this.getHisWorkFlow().DoFlowOver(ActionType.FlowOver, "流程已经走到最后一个节点，流程成功结束。", this.getHisNode(), this.rptGe);
				CC(this.getHisNode());
				this.addMsg(SendReturnMsgFlag.End, msg);
			}
			return;
		}

		this.addMsg(SendReturnMsgFlag.OverCurr, bp.wf.Glo.multilingual("当前工作[{0}]已经完成", "WorkNode", "current_work_completed_para", this.getHisNode().getName()));


		///#region 判断流程条件.
		try
		{
			String str = bp.wf.Glo.multilingual("符合流程完成条件", "WorkNode", "match_workflow_completed");
			if (this.getHisNode().getHisToNodes().size()== 0 && this.getHisNode().getItIsStartNode())
			{
				// 在流程完成锁前处理消息收听，否则WF_GenerWorkerlist就删除了。
				/* 如果流程完成 */
				this.getHisWorkFlow().DoFlowOver(ActionType.FlowOver, str, this.getHisNode(), this.rptGe);
				this.setStopFlow(true);
				String str1 = bp.wf.Glo.multilingual("工作已经成功处理(一个节点的流程)。", "WorkNode", "match_workflow_completed");
				String str2 = bp.wf.Glo.multilingual("工作已经成功处理(一个节点的流程)。 @查看<img src='./Img/Btn/PrintWorkRpt.gif' >", "WorkNode", "match_wf_completed_condition");
				CC(this.getHisNode());
				this.addMsg(SendReturnMsgFlag.OneNodeSheetver, str1, str2, SendReturnMsgType.Info);
				return;
			}

			if (this.getHisNode().getCondsOfFlowComplete().size()>= 1 && this.getHisNode().getCondsOfFlowComplete().GenerResult(this.rptGe, this.getWebUser()))
			{

				String stopMsg = this.getHisFlow().getCondsOfFlowComplete().getConditionDesc();
				/* 如果流程完成 */
				String overMsg = this.getHisWorkFlow().DoFlowOver(ActionType.FlowOver, str + ": " + stopMsg, this.getHisNode(), this.rptGe);
				this.setStopFlow(true);
				CC(this.getHisNode());
				// String path = BP.Sys.Base.Glo.Request.ApplicationPath;
				this.addMsg(SendReturnMsgFlag.MacthFlowOver, "@" + str + stopMsg + "" + overMsg, "@" + str + stopMsg + "" + overMsg, SendReturnMsgType.Info);
				return;
			}
		}
		catch (RuntimeException ex)
		{
			String str = bp.wf.Glo.multilingual("@判断流程({0})完成条件出现错误:{1}.", "WorkNode", "error_workflow_complete_condition", Arrays.toString(ex.getStackTrace()), this.getHisNode().getName());

			throw new RuntimeException(str);
		}
		///#endregion
	}


	///#region 启动多个节点
	/**
	 生成为什么发送给他们

	 @param fNodeID
	 @param toNodeID
	 @return
	 */
	public final String GenerWhySendToThem(int fNodeID, int toNodeID)
	{
		return "";
		//return "@<a href='WhySendToThem.aspx?NodeID=" + fNodeID + "&ToNodeID=" + toNodeID + "&WorkID=" + this.getWorkID() + "' target=_blank >" + this.ToE("WN20", "为什么要发送给他们？") + "</a>";
	}
	/**
	 工作流程ID
	 */
	public static long FID = 0;
	/**
	 没有FID

	 @param nd
	 @return
	 */
	private String StartNextWorkNodeHeLiu_WithOutFID(Node nd)
	{
		throw new RuntimeException("未完成:StartNextWorkNodeHeLiu_WithOutFID");
	}
	/**
	 异表单子线程向合流点运动

	 @param nd
	 */
	private void NodeSend_53_UnSameSheet_To_HeLiu(Node nd) throws Exception {

		Work heLiuWK = nd.getHisWork();


		///#region 处理FID.
		long fid = this.getHisWork().getFID();
		if (fid == 0)
		{
			if (this.getHisNode().getItIsSubThread() == false)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@当前节点非子线程节点.", "WorkNode", "not_sub_thread"));
			}
			fid = this.getHisGenerWorkFlow().getFID();
			if (fid == 0)
			{
				throw new RuntimeException(bp.wf.Glo.multilingual("@丢失FID信息.", "WorkNode", "missing_FID"));
			}

			this.getHisWork().setFID(fid);
			this.getHisWork().Update();
		}

		///#endregion FID

		heLiuWK.setOID(this.getHisWork().getFID());
		if (heLiuWK.RetrieveFromDBSources() == 0) //查询出来数据.
		{
			heLiuWK.DirectInsert();
		}

		//根据Node判断该节点是否绑定表单库的表单
		boolean isCopyData = true;
		//分流节点和子线程的节点绑定的表单相同
		if (nd.getHisFormType() == NodeFormType.RefOneFrmTree && nd.getNodeFrmID().equals(this.getHisNode().getNodeFrmID()) == true)
		{
			isCopyData = false;
		}

		if (isCopyData == true)
		{
			heLiuWK.Copy(this.getHisWork()); // 执行copy.
		}

		heLiuWK.setOID(this.getHisWork().getFID());
		heLiuWK.setFID(0);

		this.town = new WorkNode(heLiuWK, nd);

		//合流节点上的工作处理者。
		GenerWorkerLists gwls = new GenerWorkerLists(this.getHisWork().getFID(), nd.getNodeID());
		current_gwls = gwls;

		GenerWorkFlow gwf = new GenerWorkFlow(this.getHisWork().getFID());
		if (gwls.size() == 0)
		{
			// 说明第一次到达河流节点。
			current_gwls = this.Func_GenerWorkerLists(this.town);
			gwls = current_gwls;

			gwf.setNodeID(nd.getNodeID());
			gwf.setNodeName(nd.getName());
			gwf.setTodoEmpsNum(gwls.size());

			String todoEmps = "";
			for (GenerWorkerList item : gwls.ToJavaList())
			{
				todoEmps += item.getEmpNo() + "," + item.getEmpName() + ";";
			}

			gwf.setTodoEmps(todoEmps);
			gwf.setWFState(WFState.Runing);
			//第一次到达设计Gen
			gwf.Update();
		}

		//记录子线程到达合流节点数
		int count = gwf.GetParaInt("ThreadCount", 0);
		gwf.SetPara("ThreadCount", count + 1);
		gwf.Update();

		String FK_Emp = "";
		String toEmpsStr = "";
		String emps = "";
		String empNos = "";
		for (GenerWorkerList wl : gwls.ToJavaList())
		{
			empNos += wl.getEmpNo() + ",";
			toEmpsStr += wl.getEmpName() + ",";
			if (gwls.size() == 1)
			{
				emps = wl.getEmpNo() + "," + wl.getEmpName();
			}
			else
			{
				emps += "@" + wl.getEmpNo() + "," + wl.getEmpName();
			}
		}

		ActionType at = ActionType.SubThreadForward;
		this.AddToTrack(at, empNos, toEmpsStr, nd.getNodeID(), nd.getName(), bp.wf.Glo.multilingual("子线程向合流节点发送", "WorkNode", "sub_thread"), this.getHisNode());

		/*
		 * 更新它的节点 worklist 信息, 说明当前节点已经完成了.
		 * 不让当前的操作员能看到自己的工作。
		 */

		///#region 处理合流节点表单数据。



		///#region 复制主表数据. edit 2014-11-20 向合流点汇总数据.
		//复制当前节点表单数据.
		heLiuWK.setFID(0);
		heLiuWK.setRec(FK_Emp);
		heLiuWK.Copy(this.getHisWork());
		heLiuWK.setOID(this.getHisWork().getFID());
		heLiuWK.DirectUpdate(); //在更新一次.

		/* 把数据复制到rpt数据表里. */
		this.rptGe.setOID(this.getHisWork().getFID());
		this.rptGe.RetrieveFromDBSources();
		this.rptGe.Copy(this.getHisWork());
		this.rptGe.DirectUpdate();

		///#endregion 复制主表数据.


		///#endregion 处理合流节点表单数据

		//设置当前子线程已经通过.
		ps = new Paras();
		ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=1  WHERE WorkID=" + dbStr + "WorkID AND FID=" + dbStr + "FID AND IsPass=0";
		ps.Add("WorkID", this.getWorkID());
		ps.Add("FID", this.getHisWork().getFID());
		DBAccess.RunSQL(ps);

		if (this.getHisNode().getTodolistModel()== bp.wf.TodolistModel.QiangBan)
		{
			ps = new Paras();
			ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "WorkID AND FID=" + dbStr + "FID AND FK_Emp!=" + dbStr + "FK_Emp AND IsPass=0";
			ps.Add("WorkID", this.getWorkID());
			ps.Add("FID", this.getHisWork().getFID());
			ps.Add("FK_Emp", getWebUser().No, false);
			DBAccess.RunSQL(ps);
		}

		String info = "";

		/* 合流点需要等待各个分流点全部处理完后才能看到它。*/
		String sql1 = "";

///#warning 对于多个分合流点可能会有问题。
		ps = new Paras();
		ps.SQL = "SELECT COUNT(distinct WorkID) AS Num FROM WF_GenerWorkerlist WHERE  FID=" + dbStr + "FID AND FK_Node IN (" + this.SpanSubTheadNodes(nd) + ")";
		ps.Add("FID", this.getHisWork().getFID());
		BigDecimal numAll1 = new BigDecimal(DBAccess.RunSQLReturnValInt(ps));
		//说明出现跳转情况，计算出合流点发送的子线程
		int hlNodeID = this.getHisGenerWorkFlow().GetParaInt("FLNodeID", 0);
		if (hlNodeID != 0)
		{
			numAll1 = new BigDecimal(this.getHisGenerWorkFlow().GetParaInt("ThreadCount", 0));
		}
		BigDecimal numPassed = new BigDecimal(gwf.GetParaInt("ThreadCount", 0));

		BigDecimal passRate1 = numPassed.divide(numAll1,4,BigDecimal.ROUND_CEILING).multiply(new BigDecimal(100));
		if (nd.getPassRate().compareTo(passRate1) <= 0)
		{
			ps = new Paras();
			ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=0,FID=0 WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID";
			ps.Add("FK_Node", nd.getNodeID());
			ps.Add("WorkID", this.getHisWork().getFID());
			DBAccess.RunSQL(ps);

			//ps = new Paras();
			//ps.SQL = "UPDATE WF_GenerWorkFlow SET FK_Node=" + dbStr + "FK_Node,NodeName=" + dbStr + "NodeName WHERE WorkID=" + dbStr + "WorkID";
			//ps.Add("FK_Node", nd.getNodeID());
			//ps.Add("NodeName", nd.Name);
			//ps.Add("WorkID", this.HisWork.FID);
			//DBAccess.RunSQL(ps);

			ps = new Paras();
			ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE  FID=" + dbStr + "FID  AND IsPass=-2";
			ps.Add("FID", this.getHisWork().getFID());
			DBAccess.RunSQL(ps);

			gwf.setNodeID(nd.getNodeID());
			gwf.setNodeName(nd.getName());
			gwf.setEmps(gwf.getEmps() + "@" + this.getHisGenerWorkFlow().getEmps());
			//gwf.Para("ThreadCount", 0);
			gwf.Update();
			info = bp.wf.Glo.multilingual("@下一步合流节点[{0}]工作成功启动.", "WorkNode", "start_next_combined_node_work_success", nd.getName());
		}
		else
		{

///#warning 为了不让其显示在途的工作需要， =3 不是正常的处理模式。
			ps = new Paras();
			ps.SQL = "UPDATE WF_GenerWorkerlist SET IsPass=3,FID=0 WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID";
			ps.Add("FK_Node", nd.getNodeID());
			ps.Add("WorkID", this.getHisWork().getFID());
			DBAccess.RunSQL(ps);

			gwf.setEmps(gwf.getEmps() + "@" + this.getHisGenerWorkFlow().getEmps());
			gwf.Update();
		}

		this.getHisGenerWorkFlow().setNodeID(nd.getNodeID());
		this.getHisGenerWorkFlow().setNodeName(nd.getName());

		// 产生合流汇总从表数据.
		this.GenerHieLiuHuiZhongDtlData_2013(nd);

		this.addMsg(SendReturnMsgFlag.VarAcceptersID, emps, SendReturnMsgType.SystemMsg);

		this.addMsg("HeLiuInfo", bp.wf.Glo.multilingual("@下一步的工作处理人[{0}]", "WorkNode", "next_node_operator", emps) + info, SendReturnMsgType.Info);
	}
	/**
	 产生合流汇总数据
	 把子线程的子表主表数据放到合流点的从表上去

	 @param ndOfHeLiu
	 */
	private void GenerHieLiuHuiZhongDtlData_2013(Node ndOfHeLiu) throws Exception {

		///#region 汇总明细表.
		MapDtls mydtls = ndOfHeLiu.getHisWork().getHisMapDtls();
		for (MapDtl dtl : mydtls.ToJavaList())
		{
			if (dtl.getItIsHLDtl() == false)
			{
				continue;
			}

			GEDtl geDtl = dtl.getHisGEDtl();
			geDtl.Copy(this.getHisWork());
			geDtl.setRefPK(String.valueOf(this.getHisWork().getFID())); // RefPK 就是当前子线程的FID.
			geDtl.setRec(this.getExecer());
			geDtl.setRDT(DataType.getCurrentDateTime());


			///#region 判断是否是质量评价
			if (ndOfHeLiu.getItIsEval())
			{
				/*如果是质量评价流程*/
				geDtl.SetValByKey(WorkSysFieldAttr.EvalEmpNo, this.getExecer());
				geDtl.SetValByKey(WorkSysFieldAttr.EvalEmpName, this.getExecerName());
				geDtl.SetValByKey(WorkSysFieldAttr.EvalCent, 0);
				geDtl.SetValByKey(WorkSysFieldAttr.EvalNote, "");
			}

			///#endregion


			///#region 执行插入数据.
			try
			{
				geDtl.InsertAsOID(this.getHisWork().getOID());
			}
			catch (java.lang.Exception e)
			{
				geDtl.Update();
			}

			///#endregion 执行插入数据.



			///#region 还要处理附件的 copy 汇总. 如果子线程上有附件组件.
			if (dtl.getItIsEnableAthM() == true)
			{
				/*如果启用了多附件。*/
				//取出来所有的上个节点的数据集合.
				FrmAttachments athSLs = this.getHisWork().getHisFrmAttachments();
				if (athSLs.isEmpty())
				{
					break; //子线程上没有附件组件.
				}

				//求子线程的汇总附件集合 (处理如果子线程上有多个附件，其中一部分附件需要汇总另外一部分不需要汇总的模式)
				String strs = "";
				for (FrmAttachment item : athSLs.ToJavaList())
				{
					if (item.getItIsToHeLiuHZ() == true)
					{
						strs += "," + item.getMyPK() + ",";
					}
				}

				//如果没有找到，并且附件集合只有1个，就设置他为子线程的汇总附件，可能是设计人员忘记了设计.
				if (Objects.equals(strs, "") && athSLs.size() == 1)
				{
					FrmAttachment athT = athSLs.get(0) instanceof FrmAttachment ? (FrmAttachment)athSLs.get(0) : null;
					athT.setItIsToHeLiuHZ(true);
					athT.Update();
					strs = "," + athT.getMyPK() + ",";
				}

				// 没有找到要执行的附件.
				if (Objects.equals(strs, ""))
				{
					break;
				}

				//取出来所有的上个节点的数据集合.
				FrmAttachmentDBs athDBs = new FrmAttachmentDBs();
				athDBs.Retrieve(FrmAttachmentDBAttr.FK_MapData, this.getHisWork().NodeFrmID, FrmAttachmentDBAttr.RefPKVal, this.getHisWork().getOID(), null);

				if (athDBs.isEmpty())
				{
					break; //子线程没有上传附件.
				}


				/*说明当前节点有附件数据*/
				for (FrmAttachmentDB athDB : athDBs.ToJavaList())
				{
					if (strs.contains("," + athDB.getFKFrmAttachment() + ",") == false)
					{
						continue;
					}

					FrmAttachmentDB athDB_N = new FrmAttachmentDB();
					athDB_N.Copy(athDB);
					athDB_N.setFrmID(dtl.getNo());
					athDB_N.setRefPKVal( String.valueOf(geDtl.getOID()));
					athDB_N.setFKFrmAttachment(dtl.getNo() + "_AthMDtl");
					athDB_N.setUploadGUID("");
					athDB_N.setFID(this.getHisWork().getFID());

					//生成新的GUID.
					athDB_N.setMyPK(DBAccess.GenerGUID(0, null, null));
					athDB_N.Insert();
				}

			}

			///#endregion 还要处理附件的copy 汇总.
			break;
		}

		///#endregion 汇总明细表.


		///#region 复制附件。
		//合流点附件的集合
		FrmAttachments aths = ndOfHeLiu.getHisWork().getHisFrmAttachments(); // new FrmAttachments("ND" + this.Hisnode.getNodeID());
		if (aths.isEmpty())
		{
			return;
		}
		for (FrmAttachment ath : aths.ToJavaList())
		{
			//合流的汇总的多附件数据。
			if (ath.getItIsHeLiuHuiZong()== false)
			{
				continue;
			}
			//附件标识
			String noOfObj = ath.getNoOfObj();

			//如果附件标识相同的附件数据汇总
			FrmAttachments athSLs = this.getHisWork().getHisFrmAttachments();
			if (athSLs.isEmpty())
			{
				break; //子线程上没有附件组件.
			}

			//求子线程的汇总附件集合NoOfObj相同才可以汇总一起
			String strs = "";
			for (FrmAttachment item : athSLs.ToJavaList())
			{
				if (item.getItIsToHeLiuHZ() == true && item.getNoOfObj().equals(noOfObj) == true)
				{
					strs += "," + item.getNoOfObj() + ",";
				}
			}

			//如果没有找到，并且附件集合只有1个，就设置他为子线程的汇总附件，可能是设计人员忘记了设计.
			if (Objects.equals(strs, "") && athSLs.size() == 1)
			{
				FrmAttachment athT = athSLs.get(0) instanceof FrmAttachment ? (FrmAttachment)athSLs.get(0) : null;
				athT.setItIsToHeLiuHZ(true);
				athT.Update();
				strs = "," + athT.getMyPK() + ",";
				noOfObj = athT.getNoOfObj();
			}

			// 没有找到要执行的附件.
			if (Objects.equals(strs, ""))
			{
				break;
			}

			//取出来所有的上个节点的数据集合.
			FrmAttachmentDBs athDBs = new FrmAttachmentDBs();

			athDBs.Retrieve(FrmAttachmentDBAttr.NoOfObj, noOfObj, FrmAttachmentDBAttr.RefPKVal, this.getHisWork().getOID(), null);

			if (athDBs.isEmpty())
			{
				break; //子线程没有上传附件.
			}

			/*说明当前节点有附件数据*/
			for (FrmAttachmentDB athDB : athDBs.ToJavaList())
			{
				//判断是否已经存在附件，避免重复上传
				FrmAttachmentDB athNDB = new FrmAttachmentDB();
				int num = athNDB.Retrieve(FrmAttachmentDBAttr.FK_MapData, "ND" + ndOfHeLiu.getNodeID(), FrmAttachmentDBAttr.RefPKVal, String.valueOf(this.getHisWork().getFID()), FrmAttachmentDBAttr.UploadGUID, athDB.getUploadGUID());
				if (num > 0)
				{
					continue;
				}

				FrmAttachmentDB athDB_N = new FrmAttachmentDB();
				athDB_N.Copy(athDB);
				athDB_N.setFrmID("ND" + ndOfHeLiu.getNodeID());
				athDB_N.setRefPKVal( String.valueOf(this.getHisWork().getFID()));
				athDB_N.setFKFrmAttachment(ath.getMyPK());

				//生成新的GUID.
				athDB_N.setMyPK(DBAccess.GenerGUID(0, null, null));
				athDB_N.Insert();
			}
			break;
		}

		///#endregion 复制附件。


		///#region 复制Ele。
		FrmEleDBs eleDBs = new FrmEleDBs("ND" + this.getHisNode().getNodeID(), String.valueOf(this.getWorkID()));
		if (!eleDBs.isEmpty())
		{
			/*说明当前节点有附件数据*/
			int idx = 0;
			for (FrmEleDB eleDB : eleDBs.ToJavaList())
			{
				idx++;
				FrmEleDB eleDB_N = new FrmEleDB();
				eleDB_N.Copy(eleDB);
				eleDB_N.setFrmID("ND" + ndOfHeLiu.getNodeID());
				eleDB_N.setMyPK(eleDB_N.getMyPK().replace("ND" + this.getHisNode().getNodeID(), "ND" + ndOfHeLiu.getNodeID()));
				eleDB_N.setRefPKVal( String.valueOf(this.getHisWork().getFID()));
				eleDB_N.Save();
			}
		}

		///#endregion 复制Ele。


	}
	/**
	 子线程节点
	 */
	private String _SpanSubTheadNodes = null;
	/**
	 获取分流与合流之间的子线程节点集合.

	 @param toHLNode
	 @return
	 */
	private String SpanSubTheadNodes(Node toHLNode) throws Exception {
		_SpanSubTheadNodes = "";
		SpanSubTheadNodes_DiGui(toHLNode.getFromNodes());
		if (Objects.equals(_SpanSubTheadNodes, ""))
		{
			throw new RuntimeException(bp.wf.Glo.multilingual("获取分合流之间的子线程节点集合为空，请检查流程设计，在分合流之间的节点必须设置为子线程节点。", "WorkNode", "wf_eng_error_6"));
		}

		_SpanSubTheadNodes = _SpanSubTheadNodes.substring(1);
		return _SpanSubTheadNodes;

	}
	private void SpanSubTheadNodes_DiGui(Nodes subNDs) throws Exception {
		for (Node nd : subNDs.ToJavaList())
		{
			if (nd.getHisNodeWorkType() == NodeWorkType.SubThreadWork)
			{
				//判断是否已经包含，不然可能死循环
				if (_SpanSubTheadNodes.contains("," + nd.getNodeID()))
				{
					continue;
				}

				_SpanSubTheadNodes += "," + nd.getNodeID();
				SpanSubTheadNodes_DiGui(nd.getFromNodes());
			}
		}
	}



	///#endregion


	///#region 基本属性
	/**
	 工作
	 */
	private Work _HisWork = null;
	/**
	 工作
	 */
	public final Work getHisWork()
	{
		return this._HisWork;
	}
	/**
	 节点
	 */
	private Node _HisNode = null;

	private Node _SkipNode = null;

	public final Node getSkipNode()
	{
		return this._SkipNode;
	}
	/**
	 节点
	 */
	public final Node getHisNode()
	{
		return this._HisNode;
	}
	public RememberMe HisRememberMe = null;
	public final RememberMe GetHisRememberMe(Node nd) throws Exception {
		if (HisRememberMe == null || HisRememberMe.getNodeID() != nd.getNodeID())
		{
			HisRememberMe = new RememberMe();
			HisRememberMe.setEmpNo(this.getExecer());
			HisRememberMe.setNodeID(nd.getNodeID());
			HisRememberMe.RetrieveFromDBSources();
		}
		return this.HisRememberMe;
	}
	private WorkFlow _HisWorkFlow = null;
	/**
	 工作流程
	 */
	public final WorkFlow getHisWorkFlow() throws Exception {
		if (_HisWorkFlow == null)
		{
			_HisWorkFlow = new WorkFlow(this.getHisNode().getHisFlow(), this.getHisWork().getOID(), this.getHisWork().getFID());
		}
		return _HisWorkFlow;
	}
	/**
	 当前节点的工作是不是完成。
	 */
	public final boolean getItIsComplete() throws Exception {
		if (this.getHisGenerWorkFlow().getWFState() == WFState.CompleteEnd || this.getHisGenerWorkFlow().getWFState() == WFState.CompleteByNode)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public TransferCustom _transferCustom = null;
	public final TodolistModel getTodolistModel() throws Exception {
		//如果当前的节点是按照ccbpm定义的方式运行的，就返回当前节点的多人待办模式，否则就返回自定义的模式。
		/**if (this.HisGenerWorkFlow.TransferCustomType == TransferCustomType.ByCCBPMDefine)
		 */
		return this.getHisNode().getTodolistModel();
		//return this.HisGenerWorkFlow.TodolistModel;
	}

	///#endregion


	///#region 构造方法
	/**
	 建立一个工作节点事例.

	 @param workId 工作ID
	 @param nodeId 节点ID
	 */
	public WorkNode(long workId, int nodeId) throws Exception {

		this.setWorkID(workId);
		Node nd = new Node(nodeId);
		Work wk = nd.getHisWork();
		wk.setOID(workId);
		int i = wk.RetrieveFromDBSources();
		if (i == 0)
		{
			this.rptGe = nd.getHisFlow().getHisGERpt();
			if (wk.getFID() != 0)
			{
				this.rptGe.setOID(wk.getFID());
			}
			else
			{
				this.rptGe.setOID(this.getWorkID());
			}

			try {
				this.rptGe.RetrieveFromDBSources();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			wk.setRow(rptGe.getRow());
		}
		this._HisWork = wk;
		this._HisNode = nd;
		this._SkipNode = nd;
	}
	public Hashtable SendHTOfTemp = null;
	public String title = null;
	/**
	 建立一个工作节点事例

	 @param wk 工作
	 @param nd 节点
	 */
	public WorkNode(Work wk, Node nd) throws Exception {
		this.setWorkID(wk.getOID());
		this._HisWork = wk;
		this._HisNode = nd;
		this._SkipNode = nd;
	}

	///#endregion


	///#region 运算属性
	private void Repair()
	{
	}
	public final WorkNode GetPreviousWorkNode_FHL(long workid) throws Exception {
		Nodes nds = this.getHisNode().getFromNodes();
		for (Node nd : nds.ToJavaList())
		{
			if (nd.getItIsSubThread() == true)
			{
				Work wk = nd.getHisWork();
				wk.setOID(workid);
				if (wk.RetrieveFromDBSources() != 0)
				{
					WorkNode wn = new WorkNode(wk, nd);
					return wn;
				}
			}
		}
		return null;
	}
	public final WorkNodes GetPreviousWorkNodes_FHL() throws Exception {
		// 如果没有找到转向他的节点,就返回,当前的工作.
		if (this.getHisNode().getItIsStartNode())
		{
			throw new RuntimeException(bp.wf.Glo.multilingual("@此节点是开始节点,没有上一步工作.", "WorkNode", "not_found_pre_node_1"));
		}
		//此节点是开始节点,没有上一步工作.

		if (this.getHisNode().getHisNodeWorkType() == NodeWorkType.WorkHL || this.getHisNode().getHisNodeWorkType() == NodeWorkType.WorkFHL)
		{
		}
		else
		{
			throw new RuntimeException(bp.wf.Glo.multilingual("@当前工作节点不是分合流节点。", "WorkNode", "current_node_not_separate"));
		}

		WorkNodes wns = new WorkNodes();
		Nodes nds = this.getHisNode().getFromNodes();
		for (Node nd : nds.ToJavaList())
		{
			Works wks = (Works)nd.getHisWorks();
			wks.Retrieve(WorkAttr.FID, this.getHisWork().getOID(), null);

			if (wks.size() == 0)
				continue;

			for (Entity en : wks)
			{
				Work wk=(Work)en;
				WorkNode wn = new WorkNode(wk, nd);
				wns.Add(wn);
			}
		}
		return wns;
	}

	/// <summary>
	/// 检查一下流程完成条件.
	/// </summary>
	/// <returns></returns>
	public boolean Func_CheckCompleteCondition() throws Exception {
		if (this.getHisNode().getItIsSubThread() == true)
			throw new Exception(bp.wf.Glo.multilingual("@流程设计错误：不允许在子线程上设置流程完成条件。", "WorkNode", "error_sub_thread", new String[0]));

		this.setStopFlow(false);
		String[] para = new String[1];
		para[0] = this.getHisNode().getName();
		this.addMsg("CurrWorkOver", Glo.multilingual("@当前节点工作[{0}]已经完成。", "WorkNode", "current_node_completed", para));

		//判断流程条件.
		try
		{
			String matched_str = Glo.multilingual("符合流程完成条件", "WorkNode", "match_workflow_completed", new String[0]);
			if (this.getHisNode().getHisToNodes().stream().count() == 0 && this.getHisNode().getItIsStartNode())
			{
				/* 如果流程完成 */
				String overMsg = this.getHisWorkFlow().DoFlowOver(ActionType.FlowOver, matched_str, this.getHisNode(), this.rptGe);
				CC(this.getHisNode());
				if (this.getHisGenerWorkFlow().getTransferCustomType() == TransferCustomType.ByCCBPMDefine)
					this.setStopFlow(true);

				this.addMsg("OneNodeFlowOver", Glo.multilingual("@工作已经成功处理(一个节点的流程)。", "WorkNode", "node_completed_success", new String[0]));
			}

			if (this.getHisNode().getCondsOfFlowComplete().stream().count() >= 1
					&& this.getHisNode().getCondsOfFlowComplete().GenerResult(this.rptGe, this.getWebUser()))
			{
				/*如果有流程完成条件，并且流程完成条件是通过的。*/
				String stopMsg = this.getHisNode().getCondsOfFlowComplete().getConditionDesc();
				/* 如果流程完成 */
				String overMsg = this.getHisWorkFlow().DoFlowOver(ActionType.FlowOver, matched_str + ":" + stopMsg, this.getHisNode(), this.rptGe);
				this.setStopFlow(true);
				CC(this.getHisNode());
				// string path = BP.Sys.Base.Glo.Request.ApplicationPath;
				String mymsg = "@" + matched_str + " " + stopMsg + " " + overMsg;
				this.addMsg(SendReturnMsgFlag.FlowOver, mymsg, mymsg, SendReturnMsgType.Info);
			}

			//停止流程.
			return this.isStopFlow();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			String str = Glo.multilingual("@判断流程({0})完成条件出现错误:{1}.",
					"WorkNode",
					"error_workflow_complete_condition", ex.getMessage(), this.getHisNode().getName());
			throw new Exception(str);
		}
	}
	/**
	 得当他的上一步工作
	 1, 从当前的找到他的上一步工作的节点集合.
	 如果没有找到转向他的节点,就返回,当前的工作.

	 @return 得当他的上一步工作
	 */
	public final WorkNode GetPreviousWorkNode() throws Exception {
		// 如果没有找到转向他的节点,就返回,当前的工作.
		if (this.getHisNode().getItIsStartNode())
		{
			throw new RuntimeException(bp.wf.Glo.multilingual("@此节点是开始节点,没有上一步工作.", "WorkNode", "not_found_pre_node_1")); //此节点是开始节点,没有上一步工作.
		}

		String sql = "";
		int nodeid = 0;
		String truckTable = "ND" + Integer.parseInt(this.getHisNode().getFlowNo()) + "Track";
		sql = "SELECT NDFrom,Tag FROM " + truckTable + " WHERE WorkID=" + this.getWorkID() + " AND NDTo='" + this.getHisNode().getNodeID() + "' AND ";
		sql += " (ActionType=1 OR ActionType=" + ActionType.Skip.getValue() + "  OR ActionType=" + ActionType.ForwardFL.getValue() + " ";
		sql += "  OR  ActionType=" + ActionType.ForwardHL.getValue() + " "; //合流.
		sql += "  OR  ActionType=" + ActionType.ForwardAskfor.getValue() + " "; //会签.
		sql += "   )";
		sql += " ORDER BY RDT DESC";

		//首先获取实际发送节点，不存在时再使用from节点.
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt != null && dt.Rows.size() > 0)
		{
			nodeid = Integer.parseInt(dt.Rows.get(0).getValue("NDFrom").toString());
			if (dt.Rows.get(0).getValue("Tag") != null && dt.Rows.get(0).getValue("Tag").toString().contains("SendNode=") == true)
			{
				String tag = dt.Rows.get(0).getValue("Tag").toString();
				String[] strs = tag.split("[@]", -1);
				for (String str : strs)
				{
					if (str == null || Objects.equals(str, "") || str.contains("SendNode=") == false)
					{
						continue;
					}
					String[] mystr = str.split("[=]", -1);
					if (mystr.length == 2)
					{
						String sendNode = mystr[1];
						if (DataType.IsNullOrEmpty(sendNode) == false && sendNode.equals("0") == false)
						{
							nodeid = Integer.parseInt(sendNode);
						}
					}
				}
			}
		}

		if (nodeid == 0)
		{
			switch (this.getHisNode().getHisRunModel())
			{
				case HL:
				case FHL:
					sql = "SELECT NDFrom FROM " + truckTable + " WHERE WorkID=" + this.getWorkID() + " ORDER BY RDT DESC";
					break;
				case SubThreadSameWorkID:
				case SubThreadUnSameWorkID:
					sql = "SELECT NDFrom FROM " + truckTable + " WHERE WorkID=" + this.getWorkID() + " AND NDTo=" + this.getHisNode().getNodeID() + " " + " AND ( ActionType=" + ActionType.SubThreadForward.getValue() + " OR  ActionType=" + ActionType.ForwardFL.getValue() + ")  ORDER BY RDT DESC";
					if (DBAccess.RunSQLReturnCOUNT(sql) == 0)
					{
						sql = "SELECT NDFrom FROM " + truckTable + " WHERE WorkID=" + this.getHisWork().getFID() + " AND NDTo=" + this.getHisNode().getNodeID() + " " + " AND (ActionType=" + ActionType.SubThreadForward.getValue() + " OR  ActionType=" + ActionType.ForwardFL.getValue() + ") ORDER BY RDT DESC";
					}

					break;
				default:
					sql = "SELECT FK_Node FROM WF_GenerWorkerlist WHERE WorkID=" + this.getWorkID() + " AND FK_Node!='" + this.getHisNode().getNodeID() + "' ORDER BY RDT,FK_Node ";
					//throw new Exception("err@没有判断的类型:"+this.getHisNode().HisRunModel);
					//根据当前节点获取上一个节点，不用管那个人发送的
					break;
			}
			nodeid = DBAccess.RunSQLReturnValInt(sql, 0);
		}
		if (nodeid == 0)
		{
			throw new RuntimeException(bp.wf.Glo.multilingual("@没有找到上一步节点", "WorkNode", "not_found_pre_node_2") + ":" + sql);
		}

		Node nd = null;
		try {
			nd = new Node(nodeid);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Work wk = nd.getHisWork();
		wk.setOID(this.getWorkID());
		wk.RetrieveFromDBSources();

		WorkNode wn = new WorkNode(wk, nd);
		return wn;
	}

	///#endregion
}
