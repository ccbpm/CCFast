package bp.wf.template;

import bp.da.*;
import bp.port.*;
import bp.en.*; import bp.en.Map;
import bp.tools.DateUtils;
import bp.web.*;
import bp.sys.*;
import bp.wf.*;
import bp.wf.Glo;

import java.util.*;
import java.time.*;

/**
 流程
*/
public class FlowSheet extends EntityNoName
{

		///#region 属性.
	/**
	 流程事件实体
	*/
	public final String getFlowEventEntity()  {
		return this.GetValStringByKey(FlowAttr.FlowEventEntity);
	}
	public final void setFlowEventEntity(String value){
		this.SetValByKey(FlowAttr.FlowEventEntity, value);
	}
	/**
	 流程标记
	*/
	public final String getFlowMark() throws Exception {
		String str = this.GetValStringByKey(FlowAttr.FlowMark);
		if (Objects.equals(str, ""))
		{
			return this.getNo();
		}
		return str;
	}
	public final void setFlowMark(String value){
		this.SetValByKey(FlowAttr.FlowMark, value);
	}


		///#region   前置导航
	/**
	 前置导航方式
	*/
	public final StartGuideWay getStartGuideWay() {
		return StartGuideWay.forValue(this.GetValIntByKey(FlowAttr.StartGuideWay));

	}
	public final void setStartGuideWay(StartGuideWay value){
		this.SetValByKey(FlowAttr.StartGuideWay, value.getValue());
	}

	/**
	 前置导航参数1
	*/
	public final String getStartGuidePara1() throws Exception {
		String str = this.GetValStringByKey(FlowAttr.StartGuidePara1);
		return str.replace("~", "'");
	}
	public final void setStartGuidePara1(String value){
		this.SetValByKey(FlowAttr.StartGuidePara1, value);
	}

	/**
	 前置导航参数2
	*/

	public final String getStartGuidePara2() throws Exception {
		String str = this.GetValStringByKey(FlowAttr.StartGuidePara2);
		return str.replace("~", "'");
	}
	public final void setStartGuidePara2(String value){
		this.SetValByKey(FlowAttr.StartGuidePara2, value);
	}

	/**
	 前置导航参数3
	*/

	public final String getStartGuidePara3()  {
		return this.GetValStringByKey(FlowAttr.StartGuidePara3);
	}
	public final void setStartGuidePara3(String value){
		this.SetValByKey(FlowAttr.StartGuidePara3, value);
	}


	/**
	 启动方式
	*/
	public final FlowRunWay getFlowRunWay() {
		return FlowRunWay.forValue(this.GetValIntByKey(FlowAttr.FlowRunWay));

	}
	public final void setFlowRunWay(FlowRunWay value){
		this.SetValByKey(FlowAttr.FlowRunWay, value.getValue());
	}


	/**
	 运行内容
	*/

	public final String getRunObj()  {
		return this.GetValStringByKey(FlowAttr.RunObj);
	}
	public final void setRunObj(String value){
		this.SetValByKey(FlowAttr.RunObj, value);
	}


	/**
	 是否启用开始节点数据重置按钮
	*/

	public final boolean getItIsResetData()  {
		return this.GetValBooleanByKey(FlowAttr.IsResetData);
	}
	public final void setItIsResetData(boolean value){
		this.SetValByKey(FlowAttr.IsResetData, value);
	}

	/**
	 是否自动装载上一笔数据
	*/

	public final boolean getItIsLoadPriData()  {
		return this.GetValBooleanByKey(FlowAttr.IsLoadPriData);
	}
	public final void setItIsLoadPriData(boolean value){
		this.SetValByKey(FlowAttr.IsLoadPriData, value);
	}

		///#endregion


	/**
	 编号生成格式
	*/
	public final String getBillNoFormat()  {
		return this.GetValStringByKey(FlowAttr.BillNoFormat);
	}
	public final void setBillNoFormat(String value){
		this.SetValByKey(FlowAttr.BillNoFormat, value);
	}

		///#endregion 属性.


		///#region 构造方法
	/**
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (WebUser.getNo().equals("admin") == true)
		{
			uac.IsUpdate = true;
		}
		return uac;
	}
	/**
	 流程
	*/
	public FlowSheet()
	{
	}
	/**
	 流程

	 @param _No 编号
	*/
	public FlowSheet(String _No) throws Exception {
		this.setNo(_No);
			this.Retrieve();
	}
	/**
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_Flow", "流程");
		map.setCodeStruct( "3");


			///#region 基本属性。
		map.AddGroupAttr("基本属性", "");
		map.AddTBStringPK(FlowAttr.No, null, "编号", true, true, 1, 10, 3);
		map.SetHelperUrl(FlowAttr.No, "http://ccbpm.mydoc.io/?v=5404&t=17023"); //使用alert的方式显示帮助信息.

		map.AddDDLEntities(FlowAttr.FK_FlowSort, "01", "流程类别", new FlowSorts(), true);

		map.SetHelperUrl(FlowAttr.FK_FlowSort, "http://ccbpm.mydoc.io/?v=5404&t=17024");
		map.AddTBString(FlowAttr.Name, null, "名称", true, false, 0, 50, 10, true);

		// add 2013-02-14 唯一确定此流程的标记
		map.AddTBString(FlowAttr.FlowMark, null, "流程标记", true, false, 0, 150, 10);
		map.AddTBString(FlowAttr.FlowEventEntity, null, "流程事件实体", true, true, 0, 150, 10);
		map.SetHelperUrl(FlowAttr.FlowMark, "http://ccbpm.mydoc.io/?v=5404&t=16847");
		map.SetHelperUrl(FlowAttr.FlowEventEntity, "http://ccbpm.mydoc.io/?v=5404&t=17026");

		// add 2013-02-05.
		map.AddTBString(FlowAttr.TitleRole, null, "标题生成规则", true, false, 0, 150, 10, true);
		map.SetHelperUrl(FlowAttr.TitleRole, "http://ccbpm.mydoc.io/?v=5404&t=17040");

		//add  2013-08-30.
		map.AddTBString(FlowAttr.BillNoFormat, null, "单据编号格式", true, false, 0, 50, 10, false);
		map.SetHelperUrl(FlowAttr.BillNoFormat, "http://ccbpm.mydoc.io/?v=5404&t=17041");

		// add 2014-10-19.
		map.AddDDLSysEnum(FlowAttr.ChartType, FlowChartType.Icon.getValue(), "节点图形类型", true, true, "ChartType", "@0=几何图形@1=肖像图片");

		map.AddBoolean(FlowAttr.IsCanStart, true, "可以独立启动否？(独立启动的流程可以显示在发起流程列表里)", true, true, true);
		map.SetHelperUrl(FlowAttr.IsCanStart, "http://ccbpm.mydoc.io/?v=5404&t=17027");


		map.AddBoolean(FlowAttr.IsMD5, false, "是否是数据加密流程(MD5数据加密防篡改)", true, true, true);
		map.SetHelperUrl(FlowAttr.IsMD5, "http://ccbpm.mydoc.io/?v=5404&t=17028");

		map.AddBoolean(FlowAttr.IsWaterMark, false, "表单是否启用水印", true, true,true);
		map.AddDDLSysEnum(FlowAttr.WaterMarkText,0, "'水印默认展示'", true, true, "WaterMarkText","@0=系统默认值@1=登录人员账号@2=登录人员名称@3=当前日期(yyyy年MM月dd日)@4=当前日期(yyyy年MM月dd日HH时mm分)@5=登录人员名称+当前日期(yyyy年MM月dd日)@6=登录人员名称+当前日期(yyyy年MM月dd日HH时mm分)");

		map.AddBoolean(FlowAttr.IsFullSA, false, "是否自动计算未来的处理人？", true, true, true);
		map.SetHelperUrl(FlowAttr.IsFullSA, "http://ccbpm.mydoc.io/?v=5404&t=17034");



		map.AddBoolean(FlowAttr.GuestFlowRole, false, "是否外部用户参与流程(非组织结构人员参与的流程)", true, true, false);
		map.AddDDLSysEnum(FlowAttr.GuestFlowRole, GuestFlowRole.None.getValue(), "外部用户参与流程规则", true, true, "GuestFlowRole", "@0=不参与@1=开始节点参与@2=中间节点参与");

		//批量发起 add 2013-12-27.
		map.AddBoolean(FlowAttr.IsBatchStart, false, "是否可以批量发起流程？(如果是就要设置发起的需要填写的字段,多个用逗号分开)", true, true, true);
		map.AddTBString(FlowAttr.BatchStartFields, null, "发起字段s", true, false, 0, 500, 10, true);
		map.SetHelperUrl(FlowAttr.IsBatchStart, "http://ccbpm.mydoc.io/?v=5404&t=17047");


		// 草稿
		map.AddDDLSysEnum(FlowAttr.Draft, DraftRole.None.getValue(), "草稿规则", true, true, FlowAttr.Draft, "@0=无(不设草稿)@1=保存到待办@2=保存到草稿箱");
		map.SetHelperUrl(FlowAttr.Draft, "http://ccbpm.mydoc.io/?v=5404&t=17037");



			///#endregion 基本属性。

		//查询条件.
		map.AddSearchAttr(FlowAttr.FK_FlowSort, 130);
		//   map.AddSearchAttr(FlowAttr.TimelineRole);

		//绑定组织.
		//map.getAttrsOfOneVSM().add(new FlowOrgs(), new bp.wf.port.admin2group.Orgs(), FlowOrgAttr.FlowNo, FlowOrgAttr.OrgNo, FlowAttr.Name, FlowAttr.No, "可以发起的组织", 0, null, null);


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region  公共方法
	/**
	 事件

	 @return
	*/
	public final String DoAction() {
		return "../../Admin/AttrNode/Action.htm?NodeID=0&FK_Flow=" + this.getNo() + "&tk=" + (new Random()).nextDouble();
	}
	public final String DoDBSrc()
	{
		return "../../Comm/Sys/SFDBSrcNewGuide.htm";
	}


	public final String DoBindFlowSheet() {
		return "../../Admin/Sln/BindFrms.htm?s=d34&ShowType=FlowFrms&FK_Node=0&FK_Flow=" + this.getNo() + "&ExtType=StartFlow&RefNo=" + DataType.getCurrentDateTime();
	}
	/**
	 批量发起字段

	 @return
	*/
	public final String DoBatchStartFields() {
		return "../../Admin/AttrFlow/BatchStartFields.htm?s=d34&FK_Flow=" + this.getNo() + "&ExtType=StartFlow&RefNo=" + DataType.getCurrentDateTime();
	}
	/**
	 执行流程数据表与业务表数据手工同步

	 @return
	*/
	public final String DoBTableDTS() throws Exception {
		Flow fl = new Flow(this.getNo());
		return fl.DoBTableDTS();

	}
	public final String DoRebackFlowData(long workid, int backToNodeID, String note) throws Exception {
		return DoRebackFlowData(workid, backToNodeID,note,"");
	}
	/**
	 回滚已完成的流程数据到指定的节点，如果节点为0就恢复到最后一个完成的节点上去.
	 @param workid 要恢复的workid
	 @param backToNodeID 恢复到的节点编号，如果是0，标示回复到流程最后一个节点上去.
	 @param note
	 @return
	*/
	public final String DoRebackFlowData(long workid, int backToNodeID, String note,String empNo) throws Exception {
		if (DataType.IsNullOrEmpty(note) == true)
		{
			return "请填写恢复已完成的流程原因.";
		}
		if (note.length() <= 2)
		{
			return "填写回滚原因不能少于三个字符.";
		}

		Flow fl = new Flow(this.getNo());
		GERpt rpt = new GERpt("ND" + Integer.parseInt(this.getNo()) + "Rpt");
		rpt.setOID(workid);
		int i = rpt.RetrieveFromDBSources();
		if (i == 0)
		{
			throw new RuntimeException("@错误，流程数据丢失。");
		}

		if (backToNodeID == 0)
		{
			backToNodeID = rpt.getFlowEndNode();
		}

		Emp empStarter = new Emp(rpt.getFlowStarter());

		// 最后一个节点.
		Node endN = new Node(backToNodeID);
		Date dtOfShould = new Date(); // LocalDateTime.MIN;
		//增加天数. 考虑到了节假日.
		if (endN.getHisCHWay() == CHWay.ByTime) {
			//按天、小时考核
			if (endN.GetParaInt("CHWayOfTimeRole", 0) == 0) {
				//增加天数. 考虑到了节假日.
				//判断是修改了节点期限的天数
				int timeLimit = endN.getTimeLimit();
				dtOfShould = Glo.AddDayHoursSpan(new Date(), timeLimit, endN.getTimeLimitHH(), endN.getTimeLimitMM(), endN.getTWay());
			}
			//按照节点字段设置
			if (endN.GetParaInt("CHWayOfTimeRole", 0) == 1) {
				//获取设置的字段、
				String keyOfEn = endN.GetParaString("CHWayOfTimeRoleField");
				if (DataType.IsNullOrEmpty(keyOfEn) == true) {
					endN.setHisCHWay(CHWay.None);
				} else {
					dtOfShould = DataType.ParseSysDateTime2DateTime(rpt.GetValByKey(keyOfEn).toString());
				}
			}
		}

		// 应完成日期.
		String sdt = endN.getHisCHWay() == CHWay.None ? "无" : DataType.SysDateTimeFormat(dtOfShould);
		GenerWorkFlow gwf = null;
		try
		{

				///#region 创建流程引擎主表数据.
			gwf = new GenerWorkFlow();
			gwf.setWorkID(workid);
			if (gwf.RetrieveFromDBSources() == 0)
			{
				return "err@丢失了 GenerWorkFlow 数据,无法回滚.";
			}

			if (gwf.getIsOver()==false)
			{
				return "err@仅仅能对已经完成的流程才能回滚,当前流程走到了[" + gwf.getNodeName() + "]工作人员[" + gwf.getTodoEmps() + "].";
			}

			gwf.setFlowNo(this.getNo());
			gwf.setFlowName(this.getName());
			gwf.setWorkID(workid);
			gwf.setPWorkID(rpt.getPWorkID());
			gwf.setPFlowNo(rpt.getPFlowNo());
			gwf.setPNodeID(rpt.getPNodeID());
			gwf.setPEmp(rpt.getPEmp());


			gwf.setNodeID(backToNodeID);
			gwf.setNodeName(endN.getName());

			gwf.setStarter(rpt.getFlowStarter());
			gwf.setStarterName(empStarter.getName());
			gwf.setFlowSortNo(fl.getFlowSortNo());
			gwf.setSysType(fl.getSysType());
			gwf.setTitle(rpt.getTitle());
			gwf.setWFState(WFState.ReturnSta); // 设置为退回的状态
			gwf.setDeptNo(rpt.getDeptNo());

			Dept dept = new Dept(empStarter.getDeptNo());

			gwf.setDeptName(dept.getName());
			gwf.setPRI(1);

			Date date = DateUtils.addDay(new Date(), 3);
			String dttime = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss");
			gwf.setSDTOfNode(dttime);
			gwf.setSDTOfFlow(dttime);

				///#endregion 创建流程引擎主表数据

			String ndTrack = "ND" + Integer.parseInt(this.getNo()) + "Track";
			String actionType = ActionType.Forward.getValue() + "," + ActionType.FlowOver.getValue() + "," + ActionType.ForwardFL.getValue() + "," + ActionType.ForwardHL.getValue()+","+ActionType.TeampUp.getValue()+","+ActionType.Skip.getValue();
			String sql = "SELECT  * FROM " + ndTrack + " WHERE   ActionType IN (" + actionType + ")  and WorkID=" + workid + " ORDER BY RDT DESC, NDFrom ";
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
			{
				throw new RuntimeException("@工作ID为:" + workid + "的数据不存在.");
			}

			String starter = "";
			boolean isMeetSpecNode = false;
			GenerWorkerList currWl = new GenerWorkerList();
			String todoEmps = "";
			int num = 0;
			for (DataRow dr : dt.Rows)
			{
				int ndFrom = Integer.parseInt(dr.getValue("NDFrom").toString());
				Node nd = new Node(ndFrom);

				String ndFromT = dr.getValue("NDFromT").toString();
				String EmpFrom = dr.getValue(TrackAttr.EmpFrom).toString();
				String EmpFromT = dr.getValue(TrackAttr.EmpFromT).toString();
				String nodeData = dr.getValue(TrackAttr.NodeData).toString();

				// 增加上 工作人员的信息.
				GenerWorkerList gwl = new GenerWorkerList();
				gwl.setWorkID(workid);
				gwl.setFlowNo(this.getNo());

				gwl.setNodeID(ndFrom);
				gwl.setNodeName(ndFromT);
				if (gwf.getNodeID() == backToNodeID){
					gwl.setItIsPass(false);
					gwl.setSDT(sdt);
					gwl.setRDT(DataType.getCurrentDateTimess());
				}
				else {
					gwl.setItIsPass(true);
					gwl.setSDT(dr.getValue("RDT").toString());
				}

				if (gwl.getNodeID() == backToNodeID)
				{
					if(DataType.IsNullOrEmpty(empNo) == false){
						if(EmpFrom.equals(empNo))
							gwl.setItIsPass(false);
					}else
						gwl.setItIsPass(false);
					currWl = gwl;
				}

				gwl.setEmpNo(EmpFrom);
				gwl.setEmpName(EmpFromT);
				if (gwl.getIsExits())
				{
					continue; //有可能是反复退回的情况.
				}

				Emp emp = new Emp(gwl.getEmpNo());
				String deptNo = emp.getDeptNo();
				String deptName = emp.getDeptText();
				if(DataType.IsNullOrEmpty(nodeData) == false){
					AtPara atPara = new AtPara(nodeData);
					deptNo = atPara.GetValStrByKey("DeptNo");
					if(DataType.IsNullOrEmpty(deptNo))
						deptNo = emp.getDeptNo();
					deptName = atPara.GetValStrByKey("DeptName");
					if(DataType.IsNullOrEmpty(deptName))
						deptName = emp.getDeptNo();
				}
				gwl.setDeptNo(deptNo);
				gwl.setDeptName(deptName);

				todoEmps += emp.getUserID() + "," + emp.getName() + ";";
				num++;

				//gwl.setSDT(dr.getValue("RDT").toString());
				gwl.setDTOfWarning(gwf.getSDTOfNode());
				gwl.setItIsEnable(true);
				gwl.setWhoExeIt(nd.getWhoExeIt());
				gwl.Insert();

				if (gwl.getNodeID() == backToNodeID)
					break;
			}

			//设置当前处理人员.
			gwf.SetValByKey(GenerWorkFlowAttr.TodoEmps, todoEmps);
			gwf.setTodoEmpsNum(num);

			gwf.Update();
			//更新流程表的状态.
			rpt.setFlowEnder(currWl.getEmpNo());
			rpt.setWFState(WFState.ReturnSta); //设置为退回的状态
			rpt.setFlowEndNode(currWl.getNodeID());
			rpt.Update();

			// 向接受人发送一条消息.
			Dev2Interface.Port_SendMsg(currWl.getEmpNo(), "工作恢复:" + gwf.getTitle(), "工作被:" + WebUser.getNo() + " 恢复." + note, "ReBack" + workid, SMSMsgType.SendSuccess, this.getNo(), Integer.parseInt(this.getNo() + "01"), workid, 0);

			//写入该日志.
			WorkNode wn = new WorkNode(workid, currWl.getNodeID());
			wn.AddToTrack(ActionType.RebackOverFlow, currWl.getEmpNo(), currWl.getEmpName(), currWl.getNodeID(), currWl.getNodeName(), note);

			return "@已经还原成功,现在的流程已经复原到(" + currWl.getNodeName() + "). @当前工作处理人为(" + currWl.getEmpNo() + " , " + currWl.getEmpName() + ")  @请通知他处理工作.";
		}
		catch (RuntimeException ex)
		{
			//此表的记录删除已取消
			//gwf.Delete();
			GenerWorkerList wl = new GenerWorkerList();
			wl.Delete(GenerWorkerListAttr.WorkID, workid);
			gwf.setWFState(WFState.CompleteEnd);
			gwf.Update();
			String sqls = "";
			sqls += "@UPDATE " + fl.getPTable() + " SET WFState=" + WFState.CompleteEnd.getValue() + " WHERE OID=" + workid;
			DBAccess.RunSQLs(sqls);
			return "<font color=red>回滚期间出现错误</font><hr>" + ex.getMessage();
		}
	}
	/**
	 重新产生标题，根据新的规则.
	*/
	public final String DoGenerFlowEmps() throws Exception {
		if (!Objects.equals(WebUser.getNo(), "admin"))
		{
			return "非admin用户不能执行。";
		}

		Flow fl = new Flow(this.getNo());

		GenerWorkFlows gwfs = new GenerWorkFlows();
		gwfs.Retrieve(GenerWorkFlowAttr.FK_Flow, this.getNo(), null);

		for (GenerWorkFlow gwf : gwfs.ToJavaList())
		{
			String emps = "";
			String sql = "SELECT EmpFrom FROM ND" + Integer.parseInt(this.getNo()) + "Track  WHERE WorkID=" + gwf.getWorkID();

			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			for (DataRow dr : dt.Rows)
			{
				if (emps.contains("," + dr.getValue(0).toString() + ","))
				{
					continue;
				}
			}

			sql = "UPDATE " + fl.getPTable() + " SET FlowEmps='" + emps + "' WHERE OID=" + gwf.getWorkID();
			DBAccess.RunSQL(sql);

			sql = "UPDATE WF_GenerWorkFlow SET Emps='" + emps + "' WHERE WorkID=" + gwf.getWorkID();
			DBAccess.RunSQL(sql);
		}

		Node nd = fl.getHisStartNode();
		Works wks = nd.getHisWorks();
		wks.RetrieveAllFromDBSource(WorkAttr.Rec);
		String table = nd.getHisWork().getEnMap().getPhysicsTable();
		String tableRpt = "ND" + Integer.parseInt(this.getNo()) + "Rpt";
		bp.sys.MapData md = new bp.sys.MapData(tableRpt);
		for (Entity en : wks)
		{
			Work wk=(Work)en;
			if (!Objects.equals(wk.getRec(), WebUser.getNo()))
			{
				WebUser.Exit();
				try
				{
					Emp emp = new Emp(wk.getRec());
					WebUser.SignInOfGener(emp, "CH", false, false, null, null);
				}
				catch (java.lang.Exception e)
				{
					continue;
				}
			}
			String sql = "";
			String title = WorkFlowBuessRole.GenerTitle(fl, wk);
			Paras ps = new Paras();
			ps.Add("Title", title, false);
			ps.Add("OID", wk.getOID());
			ps.SQL = "UPDATE " + table + " SET Title=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "Title WHERE OID=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "OID";
			DBAccess.RunSQL(ps);

			ps.SQL = "UPDATE " + md.getPTable() + " SET Title=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "Title WHERE OID=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "OID";
			DBAccess.RunSQL(ps);

			ps.SQL = "UPDATE WF_GenerWorkFlow SET Title=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "Title WHERE WorkID=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "OID";
			DBAccess.RunSQL(ps);


		}
		Emp emp1 = new Emp("admin");
		WebUser.SignInOfGener(emp1, "CH", false, false, null, null);

		return "全部生成成功,影响数据(" + wks.size() + ")条";
	}

	/**
	 重新产生标题，根据新的规则.
	*/
	public final String DoGenerTitle() throws Exception {
		if (!Objects.equals(WebUser.getNo(), "admin"))
		{
			return "非admin用户不能执行。";
		}
		Flow fl = new Flow(this.getNo());
		Node nd = fl.getHisStartNode();
		Works wks = nd.getHisWorks();
		wks.RetrieveAllFromDBSource(WorkAttr.Rec);
		String table = nd.getHisWork().getEnMap().getPhysicsTable();
		String tableRpt = "ND" + Integer.parseInt(this.getNo()) + "Rpt";
		bp.sys.MapData md = new bp.sys.MapData(tableRpt);
		for (Entity en : wks)
		{
			Work wk=(Work)en;
			if (!Objects.equals(wk.getRec(), WebUser.getNo()))
			{
				WebUser.Exit();
				try
				{
					Emp emp = new Emp(wk.getRec());
					WebUser.SignInOfGener(emp, "CH", false, false, null, null);
				}
				catch (java.lang.Exception e)
				{
					continue;
				}
			}
			String sql = "";
			String title = WorkFlowBuessRole.GenerTitle(fl, wk);
			Paras ps = new Paras();
			ps.Add("Title", title, false);
			ps.Add("OID", wk.getOID());
			ps.SQL = "UPDATE " + table + " SET Title=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "Title WHERE OID=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "OID";
			DBAccess.RunSQL(ps);

			ps.SQL = "UPDATE " + md.getPTable() + " SET Title=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "Title WHERE OID=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "OID";
			DBAccess.RunSQL(ps);

			ps.SQL = "UPDATE WF_GenerWorkFlow SET Title=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "Title WHERE WorkID=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "OID";
			DBAccess.RunSQL(ps);


		}
		Emp emp1 = new Emp("admin");
		WebUser.SignInOfGener(emp1, "CH", false, false, null, null);

		return "全部生成成功,影响数据(" + wks.size() + ")条";
	}
	/**
	 流程监控

	 @return
	*/
	public final String DoDataManger() throws Exception {
		//PubClass.WinOpen(Glo.CCFlowAppPath + "WF/Rpt/OneFlow.htm?FK_Flow=" + this.getNo() + "&ExtType=StartFlow&RefNo=", 700, 500);
		return "../../Comm/Search.htm?s=d34&EnsName=BP.WF.Data.GenerWorkFlowViews&FK_Flow=" + this.getNo() + "&ExtType=StartFlow&RefNo=";
	}

	/**
	 定义报表

	 @return
	*/
	public final String DoAutoStartIt() throws Exception {
		Flow fl = new Flow();
		fl.setNo(this.getNo());
		fl.RetrieveFromDBSources();
		return fl.DoAutoStartIt();
	}
	/**
	 删除流程

	 @param workid
	 @param note
	 @return
	*/
	public final String DoDelDataOne(int workid, String note)
	{
		try
		{
			Dev2Interface.Flow_DoDeleteFlowByReal(workid, true);
			return "删除成功 workid=" + workid + "  理由:" + note;
		}
		catch (RuntimeException ex)
		{
			return "删除失败:" + ex.getMessage();
		}
	}


	/**
	 执行运行

	 @return
	*/
	public final String DoRunIt() {
		return "../../Admin/TestFlow.htm?FK_Flow=" + this.getNo() + "&Lang=CH";
	}
	/**
	 执行检查

	 @return
	*/
	public final String DoCheck() throws Exception {
		//Flow fl = new Flow();
		//fl.setNo(this.No;
		//fl.RetrieveFromDBSources();

		return "/WF/Admin/AttrFlow/CheckFlow.htm?FK_Flow=" + this.getNo();

		//return fl.DoCheck();
	}

	/**
	 删除数据.

	 @return
	*/
	public final String DoDelData() throws Exception {
		Flow fl = new Flow();
		fl.setNo(this.getNo());
		fl.RetrieveFromDBSources();
		return fl.DoDelData();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		//更新流程版本
		Flow.UpdateVer(this.getNo());
		return super.beforeUpdate();
	}
	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		//同步流程数据表.
		String ndxxRpt = "ND" + Integer.parseInt(this.getNo()) + "Rpt";
		Flow fl = new Flow(this.getNo());
		if (!fl.getPTable().equals("ND" + Integer.parseInt(this.getNo()) + "Rpt"))
		{
			MapData md = new bp.sys.MapData(ndxxRpt);
			if (!Objects.equals(md.getPTable(), fl.getPTable()))
			{
				md.Update();
			}
		}
		super.afterInsertUpdateAction();
	}

		///#endregion
}
