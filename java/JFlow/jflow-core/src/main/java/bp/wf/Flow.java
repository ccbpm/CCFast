package bp.wf;

import bp.da.*;
import bp.sys.*;
import bp.port.*;
import bp.en.*; import bp.en.Map;
import bp.tools.BaseFileUtils;
import bp.wf.template.*;
import bp.difference.*;
import bp.web.*;
import bp.wf.template.sflow.*;
import bp.wf.template.frm.*;
import bp.*;

import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;

/**
 流程
 记录了流程的信息．
 流程的编号，名称，建立时间．
 */
public class Flow extends bp.en.EntityNoName
{

	///#region 参数属性.
	/**
	 待办的业务字段 2019-09-25 by zhoupeng
	 */
	public final String getBuessFields() {
		return this.GetParaString(FlowAttr.BuessFields);
	}
	public final void setBuessFields(String value)  {
		this.SetPara(FlowAttr.BuessFields, value);
	}


	///#endregion 参数属性.


	///#region 业务数据表同步属性.
	/**
	 同步方式
	 */
	public final DataDTSWay getDTSWay() {
		return DataDTSWay.forValue(this.GetValIntByKey(FlowAttr.DTSWay));
	}
	public final void setDTSWay(DataDTSWay value)  {
		this.SetValByKey(FlowAttr.DTSWay, value.getValue());
	}

	public final FlowDTSTime getDTSTime() {
		return FlowDTSTime.forValue(this.GetValIntByKey(FlowAttr.DTSTime));
	}
	public final void setDTSTime(FlowDTSTime value)  {
		this.SetValByKey(FlowAttr.DTSTime, value.getValue());
	}

	/**
	 数据源
	 */
	public final String getDTSDBSrc() throws Exception {
		String str = this.GetParaString(FlowAttr.DTSDBSrc);
		if (DataType.IsNullOrEmpty(str))
		{
			return "local";
		}
		return str;
	}
	public final void setDTSDBSrc(String value)  {
		this.SetPara(FlowAttr.DTSDBSrc, value);
	}
	/**
	 webapi
	 */
	public final String getDTSWebAPI() throws Exception {
		String str = this.GetParaString(FlowAttr.DTSWebAPI);
		if (DataType.IsNullOrEmpty(str))
		{
			return "local";
		}
		return str;
	}
	public final void setDTSWebAPI(String value)  {
		this.SetPara(FlowAttr.DTSWebAPI, value);
	}
	/**
	 业务表
	 */
	public final String getDTSBTable() {
		return this.GetParaString(FlowAttr.DTSBTable);
	}
	public final void setDTSBTable(String value)  {
		this.SetPara(FlowAttr.DTSBTable, value);
	}
	public final String getDTSBTablePK() {
		return this.GetParaString(FlowAttr.DTSBTablePK);
	}
	public final void setDTSBTablePK(String value)  {
		this.SetPara(FlowAttr.DTSBTablePK, value);
	}
	/**
	 要同步的节点s
	 */
	public final String getDTSSpecNodes() {
		return this.GetParaString(FlowAttr.DTSSpecNodes);
	}
	public final void setDTSSpecNodes(String value)  {
		this.SetPara(FlowAttr.DTSSpecNodes, value);
	}
	/**
	 同步的字段对应关系.
	 */
	public final String getDTSFields() {
		return this.GetValStringByKey(FlowAttr.DTSFields);
	}
	public final void setDTSFields(String value)  {
		this.SetValByKey(FlowAttr.DTSFields, value);
	}

	///#endregion 业务数据表同步属性.


	///#region 基础属性.
	/**
	 消息推送.
	 */
	public final PushMsgs getPushMsgs() throws Exception {
		PushMsgs ens = (PushMsgs) this.GetEntitiesAttrFromAutoNumCache(new PushMsgs(), PushMsgAttr.FlowNo, this.getNo(), null, null, null);
		return ens instanceof PushMsgs ? (PushMsgs)ens : null;
	}
	/**
	 流程事件实体
	 */
	public final String getFlowEventEntity() {
		return this.GetValStringByKey(FlowAttr.FlowEventEntity);
	}
	public final void setFlowEventEntity(String value)  {
		this.SetValByKey(FlowAttr.FlowEventEntity, value);
	}
	/**
	 流程设计模式
	 */
	public final FlowDevModel getFlowDevModel() {
		return FlowDevModel.forValue(this.GetValIntByKey(FlowAttr.FlowDevModel));
	}
	public final void setFlowDevModel(FlowDevModel value)  {
		this.SetValByKey(FlowAttr.FlowDevModel, value.getValue());
	}
	/**
	 url
	 */
	public final String getFrmUrl() {
		return this.GetValStringByKey(FlowAttr.FrmUrl);
	}
	public final void setFrmUrl(String value)  {
		this.SetValByKey(FlowAttr.FrmUrl, value);
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
	public final void setFlowMark(String value)  {
		this.SetValByKey(FlowAttr.FlowMark, value);
	}
	public final String getOrgNo() throws Exception {
		String str = this.GetValStringByKey(FlowAttr.OrgNo);
		return str;
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(FlowAttr.OrgNo, value);
	}
	/**
	 节点图形类型
	 */
	public final int getChartType() {
		return this.GetValIntByKey(FlowAttr.ChartType);
	}
	public final void setChartType(int value)  {
		this.SetValByKey(FlowAttr.ChartType, value);
	}

	///#endregion


	///#region 发起限制.
	/**
	 发起限制.
	 */
	public final StartLimitRole getStartLimitRole() {
		return StartLimitRole.forValue(this.GetValIntByKey(FlowAttr.StartLimitRole));
	}
	public final void setStartLimitRole(StartLimitRole value)  {
		this.SetValByKey(FlowAttr.StartLimitRole, value.getValue());
	}
	/**
	 发起内容
	 */
	public final String getStartLimitPara() {
		return this.GetValStringByKey(FlowAttr.StartLimitPara);
	}
	public final void setStartLimitPara(String value)  {
		this.SetValByKey(FlowAttr.StartLimitPara, value);
	}
	public final String getStartLimitAlert() throws Exception {
		String s = this.GetValStringByKey(FlowAttr.StartLimitAlert);
		if (Objects.equals(s, ""))
		{
			return "您已经启动过该流程，不能重复启动。";
		}
		return s;
	}
	public final void setStartLimitAlert(String value)  {
		this.SetValByKey(FlowAttr.StartLimitAlert, value);
	}
	/**
	 限制触发时间
	 */
	public final StartLimitWhen getStartLimitWhen() {
		return StartLimitWhen.forValue(this.GetValIntByKey(FlowAttr.StartLimitWhen));
	}
	public final void setStartLimitWhen(StartLimitWhen value)  {
		this.SetValByKey(FlowAttr.StartLimitWhen, value.getValue());
	}

	///#endregion 发起限制.


	///#region 导航模式
	/**
	 发起导航方式
	 */
	public final StartGuideWay getStartGuideWay() {
		return StartGuideWay.forValue(this.GetValIntByKey(FlowAttr.StartGuideWay));
	}
	public final void setStartGuideWay(StartGuideWay value)  {
		this.SetValByKey(FlowAttr.StartGuideWay, value.getValue());
	}
	/**
	 右侧的超链接
	 */
	public final String getStartGuideLink() {
		return this.GetValStringByKey(FlowAttr.StartGuideLink);
	}
	public final void setStartGuideLink(String value)  {
		this.SetValByKey(FlowAttr.StartGuideLink, value);
	}
	/**
	 标签
	 */
	public final String getStartGuideLab() {
		return this.GetValStringByKey(FlowAttr.StartGuideLab);
	}
	public final void setStartGuideLab(String value)  {
		this.SetValByKey(FlowAttr.StartGuideLab, value);
	}
	/**
	 前置导航参数1
	 */
	public final String getStartGuidePara1() throws Exception {
		//if (this.StartGuideWay == StartGuideWay.BySelfUrl)
		//{
		//    String str = this.GetValStringByKey(FlowAttr.StartGuidePara1);
		//    if (str.contains("?") == false)
		//        str = str + "?1=2";
		//    return str.replace("~", "'");
		//}
		//else
		//{
		return this.GetValStringByKey(FlowAttr.StartGuidePara1);
		// }
	}
	public final void setStartGuidePara1(String value)  {
		this.SetValByKey(FlowAttr.StartGuidePara1, value);
	}


	/**
	 流程发起参数2
	 */
	public final String getStartGuidePara2() throws Exception {
		String str = this.GetValStringByKey(FlowAttr.StartGuidePara2);
		str = str.replace("~", "'");
		//if (DataType.IsNullOrEmpty(str) == null)
		//{
		//    if (this.StartGuideWay == BP.WF.Template.StartGuideWay.ByHistoryUrl)
		//    {
		//    }
		//}
		return str;
	}
	public final void setStartGuidePara2(String value)  {
		this.SetValByKey(FlowAttr.StartGuidePara2, value);
	}
	/**
	 流程发起参数3
	 */
	public final String getStartGuidePara3()  {
		return this.GetValStrByKey(FlowAttr.StartGuidePara3);
	}
	public final void setStartGuidePara3(String value)  {
		this.SetValByKey(FlowAttr.StartGuidePara3, value);
	}
	/**
	 是否启用数据重置按钮？
	 */
	public final boolean getItIsResetData() {
		return this.GetValBooleanByKey(FlowAttr.IsResetData);
	}
	/**
	 是否启用导入历史数据按钮?
	 */
	public final boolean getItIsImpHistory() {
		return this.GetValBooleanByKey(FlowAttr.IsImpHistory);
	}
	/**
	 是否自动装载上一笔数据?
	 */
	public final boolean getItIsLoadPriData() {
		return this.GetValBooleanByKey(FlowAttr.IsLoadPriData);
	}

	///#endregion


	///#region 其他属性
	/**
	 草稿规则
	 */
	public final DraftRole getDraftRole() {
		return DraftRole.forValue(this.GetValIntByKey(FlowAttr.Draft));
	}
	public final void setDraftRole(DraftRole value)  {
		this.SetValByKey(FlowAttr.Draft, value.getValue());
	}
	public String Tag = null;
	/**
	 运行类型
	 */
	public final FlowRunWay getHisFlowRunWay() {
		return FlowRunWay.forValue(this.GetValIntByKey(FlowAttr.FlowRunWay));
	}
	public final void setHisFlowRunWay(FlowRunWay value)  {
		this.SetValByKey(FlowAttr.FlowRunWay, value.getValue());
	}
	/**
	 运行对象
	 */
	public final String getRunObj()  {
		return this.GetValStrByKey(FlowAttr.RunObj);
	}
	public final void setRunObj(String value)  {
		this.SetValByKey(FlowAttr.RunObj, value);
	}
	/**
	 时间点规则
	 */
	public final SDTOfFlowRole getSDTOfFlowRole() {
		return SDTOfFlowRole.forValue(this.GetValIntByKey(FlowAttr.SDTOfFlowRole));
	}
	/**
	 按照SQL来计算流程完成时间
	 */
	public final String getSDTOfFlowRoleSQL()  {
		return this.GetValStrByKey(FlowAttr.SDTOfFlowRoleSQL);
	}
	/**
	 流程部门数据查询权限控制方式
	 */
	public final FlowDeptDataRightCtrlType getHisFlowDeptDataRightCtrlType() {
		return FlowDeptDataRightCtrlType.forValue(this.GetValIntByKey(FlowAttr.DRCtrlType));
	}
	public final void setHisFlowDeptDataRightCtrlType(FlowDeptDataRightCtrlType value)  {
		this.SetValByKey(FlowAttr.DRCtrlType, value);
	}


	/**
	 是否可以在手机里启用
	 */
	public final boolean getItIsStartInMobile() {
		return this.GetValBooleanByKey(FlowAttr.IsStartInMobile);
	}
	public final void setItIsStartInMobile(boolean value)  {
		this.SetValByKey(FlowAttr.IsStartInMobile, value);
	}

	///#endregion 业务处理


	///#region 创建新工作.
	/**
	 创建新工作web方式调用的

	 @return
	 */
	public final Work NewWork() throws Exception {
		return NewWork(WebUser.getNo());
	}
	/**
	 创建新工作.web方式调用的

	 @param empNo 人员编号
	 @return
	 */
	public final Work NewWork(String empNo) throws Exception {
		Emp emp = new Emp(empNo);
		return NewWork(emp, null);
	}
	/**
	 产生一个开始节点的新工作

	 @param emp 发起人
	 @param paras 参数集合,如果是CS调用，要发起子流程，要从其他table里copy数据,就不能从request里面取,可以传递为null.
	 @return 返回的Work.
	 */
	public final Work NewWork(Emp emp, Hashtable paras) throws Exception {
		// 检查是否可以发起该流程？
		if (CCFlowAPI.CheckIsCanStartFlow_InitStartFlow(this) == false)
		{
			throw new RuntimeException("err@您违反了该流程的【" + this.getStartLimitRole() + "】限制规则。" + this.getStartLimitAlert());
		}

		GenerWorkFlow gwf = new GenerWorkFlow();

		///#region 组织参数.
		//如果是bs系统.
		if (paras == null)
			paras = new Hashtable();

		if (SystemConfig.isBSsystem() == true)
		{
			for (String k : ContextHolderUtils.getRequest().getParameterMap().keySet())
			{
				if (k == null || k.equals("OID") || k.equals("WorkID") || paras.containsKey("PWorkID") == true)
					continue;
				if (paras.containsKey(k))
					paras.put(k, ContextHolderUtils.getRequest().getParameter(k));
				else
					paras.put(k, ContextHolderUtils.getRequest().getParameter(k));
			}
		}

		///#endregion 组织参数.

		boolean isDelDraft = false;
		if (paras.containsKey(StartFlowParaNameList.IsDeleteDraft) && paras.get(StartFlowParaNameList.IsDeleteDraft).toString().equals("1"))
			isDelDraft = true;

		//开始节点.
		bp.wf.Node nd = new bp.wf.Node(this.getStartNodeID());

		//从草稿里看看是否有新工作？
		Work wk = nd.getHisWork();
		try
		{
			wk.ResetDefaultVal(null, null, 0);
		}
		catch (RuntimeException ex)
		{
			wk.ResetDefaultVal(null, null, 0);
		}

		String dbstr = SystemConfig.getAppCenterDBVarStr();

		Paras ps = new Paras();
		GERpt rpt = this.getHisGERpt();

		//是否新创建的WorkID
		boolean IsNewWorkID = false;
		/*如果要启用草稿,就创建一个新的WorkID .*/
		if (this.getDraftRole() != DraftRole.None)
		{
			IsNewWorkID = true;
		}

		String errInfo = "";
		try
		{
			//从报表里查询该数据是否存在？
			if (this.getGuestFlowRole() != GuestFlowRole.None && DataType.IsNullOrEmpty(GuestUser.getNo()) == false)
			{
				/*是客户参与的流程，并且具有客户登录的信息。*/
				ps.SQL = "SELECT OID,FlowEndNode FROM " + this.getPTable() + " WHERE GuestNo=" + dbstr + "GuestNo AND WFState=" + dbstr + "WFState AND FK_Flow='" + this.getNo() + "' ";
				ps.Add(GERptAttr.GuestNo, GuestUser.getNo(), false);
				ps.Add(GERptAttr.WFState, WFState.Blank.getValue());
				DataTable dt = DBAccess.RunSQLReturnTable(ps);
				if (dt.Rows.size() > 0 && IsNewWorkID == false)
				{
					wk.setOID(Long.parseLong(dt.Rows.get(0).getValue(0).toString()));

					//查询出来当前gwf.
					gwf.setWorkID(wk.getOID());
					gwf.Retrieve();

				}
			}
			else
			{
				ps.SQL = "SELECT WorkID,FK_Node FROM WF_GenerWorkFlow WHERE WFState=0 AND Starter=" + dbstr + "FlowStarter AND FK_Flow=" + dbstr + "FK_Flow  ";
				ps.Add(GERptAttr.FlowStarter, emp.getUserID(), false);
				ps.Add(GenerWorkFlowAttr.FK_Flow, this.getNo(), false);
				DataTable dt = DBAccess.RunSQLReturnTable(ps);

				//如果没有启用草稿，并且存在草稿就取第一条
				if (dt.Rows.size() > 0)
				{
					wk.setOID(Long.parseLong(dt.Rows.get(0).getValue(0).toString()));
					wk.RetrieveFromDBSources();

					//查询出来当前gwf.
					gwf.setWorkID(wk.getOID());
					if (gwf.getWorkID() == 0)
					{
						gwf.Delete();
						return NewWork(emp, paras);
					}

					gwf.Retrieve();
				}
			}

			//启用草稿或空白就创建WorkID
			if (wk.getOID() == 0)
			{
				/* 说明没有空白,就创建一个空白..*/
				if (nd.getHisFormType() == NodeFormType.FoolForm || nd.getHisFormType() == NodeFormType.Develop|| nd.getHisFormType() == NodeFormType.FoolTruck)
				{
					wk.ResetDefaultVal(null, null, 0);
				}
				else {
					wk.ResetDefaultVal(wk.NodeFrmID, this.getNo(), wk.getNodeID());
				}
				wk.setRec(WebUser.getNo());

				wk.SetValByKey(GERptAttr.WFState, WFState.Blank.getValue());

				/*这里产生WorkID ,这是唯一产生WorkID的地方.*/
				if (SystemConfig.GetValByKeyInt("GenerWorkIDModel", 0) == 0)
				{
					wk.setOID(DBAccess.GenerOID("WorkID"));
				}
				else
				{
					wk.setOID(DBAccess.GenerOIDByGUID());
				}

				//把尽量可能的流程字段放入，否则会出现冲掉流程字段属性.
				wk.SetValByKey(GERptAttr.FK_NY, DataType.getCurrentYearMonth());
				wk.SetValByKey(GERptAttr.FK_Dept, emp.getDeptNo());
				wk.setFID(0);
				for (Object k : paras.keySet())
				{
					if(k == null) continue;
					if (paras.get(k)==null) continue;

					// String val = ;
					wk.SetValByKey(k.toString(),paras.get(k) );
				}

				///#region 写入work 数据.
				try
				{
					wk.DirectInsert();
				}
				catch (RuntimeException ex)
				{
					wk.CheckPhysicsTable();
					//检查报表,执行插入数据. 2020.08.18 增加.
					this.CheckRpt();

					//if (wk.RetrieveFromDBSources()!=0)

					wk.DirectInsert(); //执行插入.
				}

				///#endregion 写入work 数据.

				//设置参数.
				for (Object k : paras.keySet())
				{
					if(k == null) continue;
					if (paras.get(k)==null) continue;

					// String val = ;
					rpt.SetValByKey(k.toString(),paras.get(k) );
				}

				//两个表相同，就执行更新.
				if (this.getPTable().equals(wk.getEnMap().getPhysicsTable()) == true)
				{
					/*如果开始节点表与流程报表相等.*/
					rpt.setOID(wk.getOID());
					rpt.RetrieveFromDBSources();
					rpt.setFID(0);
					rpt.setFlowStartRDT(DataType.getCurrentDateTime());
					rpt.setTitle(bp.wf.WorkFlowBuessRole.GenerTitle(this, wk));
					//WebUser.getNo() + "," + bp.web.WebUser.getName() + "在" + DataType.getCurrentDate()CNOfShort + "发起.";
					rpt.setWFState(WFState.Blank);
					rpt.setFlowStarter(emp.getUserID());
					rpt.setNY(DataType.getCurrentYearMonth());
					if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserNameOnly)
					{
						rpt.setFlowEmps("@" + emp.getName() + "@");
					}

					if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserIDUserName)
					{
						rpt.setFlowEmps("@" + emp.getUserID() + "@");
					}

					if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserIDUserName)
					{
						rpt.setFlowEmps("@" + emp.getUserID() + "," + emp.getName() + "@");
					}

					rpt.setFlowEnderRDT(DataType.getCurrentDateTime());
					rpt.setFlowStartRDT(DataType.getCurrentDateTime());

					rpt.setDeptNo(emp.getDeptNo());
					rpt.setDeptName(emp.getDeptText());
					rpt.setFlowEnder(emp.getUserID());
					rpt.setFlowEndNode(this.getStartNodeID());
					rpt.setFlowStarter(emp.getUserID());
					rpt.setWFState(WFState.Blank);
					rpt.setFID(0);
					rpt.Update();
				}
				else
				{
					rpt.setOID(wk.getOID());
					rpt.setFID(0);
					rpt.setFlowStartRDT(DataType.getCurrentDateTime());
					rpt.setFlowEnderRDT(DataType.getCurrentDateTime());

					rpt.setTitle(bp.wf.WorkFlowBuessRole.GenerTitle(this, wk));
					// rpt.Title = WebUser.getNo() + "," + bp.web.WebUser.getName() + "在" + DataType.getCurrentDate()CNOfShort + "发起.";

					rpt.setWFState(WFState.Blank);
					rpt.setFlowStarter(emp.getUserID());

					rpt.setFlowEndNode(this.getStartNodeID());
					if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserNameOnly)
					{
						rpt.setFlowEmps("@" + emp.getName() + "@");
					}

					if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserIDUserName)
					{
						rpt.setFlowEmps("@" + emp.getUserID() + "@");
					}

					if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserIDUserName)
					{
						rpt.setFlowEmps("@" + emp.getUserID() + "," + emp.getName() + "@");
					}

					rpt.setNY(DataType.getCurrentYearMonth());
					rpt.setDeptNo(emp.getDeptNo());
					rpt.setDeptName(emp.getDeptText());
					rpt.setFlowEnder(emp.getUserID());
					rpt.setFlowStarter(emp.getUserID());
					rpt.SaveAsOID((int)wk.getOID()); //执行保存.
				}

				//调用 OnCreateWorkID的方法.  add by zhoupeng 2016.12.4 for LIMS.
				ExecEvent.DoFlow(EventListFlow.FlowOnCreateWorkID, wk, nd, null);
			}

			if (wk.getOID() != 0)
			{
				rpt.setOID(wk.getOID());
				int i = rpt.RetrieveFromDBSources();
				if (i == 0)
				{
					rpt.setFID(0);
					rpt.setFlowStartRDT(DataType.getCurrentDateTime());
					rpt.setFlowEnderRDT(DataType.getCurrentDateTime());
					rpt.setFlowEnder(WebUser.getNo());
					rpt.setWFState(bp.wf.WFState.forValue(0));
					rpt.setFlowEndNode(nd.getNodeID());
					rpt.setFlowStarter(WebUser.getNo());
					rpt.InsertAsOID(wk.getOID());
					//if (rpt.getEnMap().getPhysicsTable().equals(wk.getEnMap().getPhysicsTable()) == true)
					//{
					//    rpt.Update();
					//}

					//GenerWorkFlow gwfw = new GenerWorkFlow();
					//gwfw.WorkID = wk.OID;
					//gwfw.Delete();
					//throw new Exception("err@没有保存到流程表单数据" + rpt.getEnMap().getPhysicsTable() + ",表单表" + wk.getEnMap().getPhysicsTable() + " 系统错误." + rpt.OID + ",请联系管理员.");
				}
				rpt.setFID(0);
				rpt.setFlowStartRDT(DataType.getCurrentDateTime());
				rpt.setFlowEnderRDT(DataType.getCurrentDateTime());

			}
		}
		catch (RuntimeException ex)
		{
			wk.CheckPhysicsTable();

			//检查报表.
			this.CheckRpt();
			//int i = wk.DirectInsert();
			//if (i == 0)
			throw new RuntimeException("@创建工作失败：请您刷新一次，如果问题仍然存在请反馈给管理员，技术信息：" + ex.getStackTrace() + " @ 技术信息:" + ex.getMessage());
		}

		//在创建WorkID的时候调用的事件.
		ExecEvent.DoFlow(EventListFlow.FlowOnCreateWorkID, wk, nd, null);


		///#region copy数据.
		// 记录这个id ,不让其它在复制时间被修改。
		if (IsNewWorkID == true)
		{
			// 处理传递过来的参数。
			int i = 0;
			String expKeys = "OID,DoType,HttpHandlerName,DoMethod,t,";
			for (Object k : paras.keySet())
			{
				if (expKeys.indexOf("," + k + ",") != -1)
				{
					continue;
				}

				String str = paras.get(k) instanceof String ? (String)paras.get(k) : null;
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}
				i++;
				wk.SetValByKey(k.toString(), str);
			}
		}

		///#endregion copy数据.


		///#region 处理删除草稿的需求。
		if (isDelDraft == true)
		{
			//重新设置默认值.
			wk.ResetDefaultValAllAttr();

			MapDtls dtls = wk.getHisMapDtls();
			for (MapDtl dtl : dtls.ToJavaList())
			{
				try
				{
					DBAccess.RunSQL("DELETE FROM " + dtl.getPTable() + " WHERE RefPK='" + wk.getOID() + "'");
				}
				catch (RuntimeException ex)
				{

				}

			}
			//删除附件数据。
			DBAccess.RunSQL("DELETE FROM Sys_FrmAttachmentDB WHERE FK_MapData='ND" + wk.getNodeID() + "' AND RefPKVal='" + wk.getOID() + "'");
		}

		///#endregion 处理删除草稿的需求。


		///#region 处理开始节点, 如果传递过来 FromTableName 就是要从这个表里copy数据。
		if (paras.containsKey("FromTableName"))
		{
			String tableName = paras.get("FromTableName").toString();
			String tablePK = paras.get("FromTablePK").toString();
			String tablePKVal = paras.get("FromTablePKVal").toString();

			DataTable dt = DBAccess.RunSQLReturnTable("SELECT * FROM " + tableName + " WHERE " + tablePK + "='" + tablePKVal + "'");
			if (dt.Rows.size() == 0)
			{
				throw new RuntimeException("@利用table传递数据错误，没有找到指定的行数据，无法为用户填充数据。");
			}

			String innerKeys = ",OID,RDT,CDT,FID,WFState,WorkID,WORKID,WFSTATE";
			for (DataColumn dc : dt.Columns)
			{
				if (innerKeys.contains("," + dc.ColumnName.toUpperCase() + ","))
				{
					continue;
				}

				wk.SetValByKey(dc.ColumnName, dt.Rows.get(0).getValue(dc.ColumnName).toString());
				rpt.SetValByKey(dc.ColumnName, dt.Rows.get(0).getValue(dc.ColumnName).toString());
			}
			rpt.Update();
		}

		///#endregion 处理开始节点, 如果传递过来 FromTableName 就是要从这个表里copy数据。


		///#region 获取特殊标记变量
		// 获取特殊标记变量.
		String PFlowNo = null;
		String PNodeIDStr = null;
		String PWorkIDStr = null;
		String PFIDStr = null;

		String CopyFormWorkID = null;
		if (paras.containsKey("CopyFormWorkID") == true)
		{
			CopyFormWorkID = paras.get("CopyFormWorkID").toString();
			PFlowNo = this.getNo();
			PNodeIDStr = paras.get("CopyFormNode").toString();
			PWorkIDStr = CopyFormWorkID;
			PFIDStr = "0";
		}

		if (paras.containsKey("PWorkID") == true && paras.containsKey("PNodeID") == true && Long.parseLong(paras.get("PWorkID").toString()) != 0 && Integer.parseInt(paras.get("PNodeID").toString()) != 0)
		{
			if (paras.get("PFlowNo") == null)
			{
				int nodeId = Integer.parseInt(paras.get("PNodeID").toString());
				Node node = new Node(nodeId);
				PFlowNo = node.getFlowNo();

			}
			else
			{
				PFlowNo = paras.get("PFlowNo").toString();
			}

			PNodeIDStr = paras.get("PNodeID").toString();
			//如果是延续子流程产生的PWorkID
			PWorkIDStr = paras.get("PWorkID").toString();
			PFIDStr = "0";
			if (paras.containsKey("PFID") == true)
			{
				PFIDStr = paras.get("PFID").toString(); //父流程.
			}
		}

		///#endregion 获取特殊标记变量


		///#region  判断是否装载上一条数据.
		if (this.getItIsLoadPriData() == true && this.getStartGuideWay() == StartGuideWay.None)
		{
			/* 如果需要从上一个流程实例上copy数据. */
			String sql = "SELECT OID FROM " + this.getPTable() + " WHERE FlowStarter='" + WebUser.getNo() + "' AND OID!=" + wk.getOID() + " ORDER BY RDT DESC";
			String workidPri = DBAccess.RunSQLReturnStringIsNull(sql, "0");
			if (Objects.equals(workidPri, "0"))
			{
				/*说明没有第一笔数据.*/
			}
			else
			{
				PFlowNo = this.getNo();
				PNodeIDStr = Integer.parseInt(this.getNo()) + "01";
				PWorkIDStr = workidPri;
				PFIDStr = "0";
				CopyFormWorkID = workidPri;
			}
		}

		///#endregion  判断是否装载上一条数据.


		///#region 处理流程之间的数据传递1。
		if (DataType.IsNullOrEmpty(PNodeIDStr) == false && DataType.IsNullOrEmpty(PWorkIDStr) == false)
		{
			long PWorkID = Long.parseLong(PWorkIDStr);
			long PNodeID = 0;
			if (CopyFormWorkID != null)
			{
				PNodeID = Long.parseLong(PNodeIDStr);
			}

			/* 如果是从另外的一个流程上传递过来的，就考虑另外的流程数据。*/


			///#region copy 首先从父流程的 NDxxxRpt copy.
			long pWorkIDReal = 0;
			Flow pFlow = new Flow(PFlowNo);
			String pOID = "";
			if (DataType.IsNullOrEmpty(PFIDStr) == true || Objects.equals(PFIDStr, "0"))
			{
				pOID = String.valueOf(PWorkID);
			}
			else
			{
				pOID = PFIDStr;
			}

			String sql = "SELECT * FROM " + pFlow.getPTable() + " WHERE OID=" + pOID;
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() != 1)
			{
				throw new RuntimeException("@不应该查询不到父流程的数据[" + sql + "], 可能的情况之一,请确认该父流程的调用节点是子线程，但是没有把子线程的FID参数传递进来。");
			}

			wk.Copy(dt.Rows.get(0));
			rpt.Copy(dt.Rows.get(0));

			//设置单号为空.
			wk.SetValByKey("BillNo", "");
			rpt.setBillNo("");

			///#endregion copy 首先从父流程的NDxxxRpt copy.


			///#region 从调用的节点上copy.
			bp.wf.Node fromNd = new bp.wf.Node(Integer.parseInt(PNodeIDStr));
			Work wkFrom = fromNd.getHisWork();
			wkFrom.setOID(PWorkID);
			if (wkFrom.RetrieveFromDBSources() == 0)
			{
				throw new RuntimeException("@父流程的工作ID不正确，没有查询到数据" + PWorkID);
			}
			wk.Copy(wkFrom);
			SubFlows subFlows = new SubFlows();
			subFlows.Retrieve(SubFlowAttr.SubFlowNo, this.getNo(), SubFlowAttr.FK_Node, Integer.parseInt(PNodeIDStr), null);
			if (subFlows.size() != 0)
			{
				SubFlow subFlow = subFlows.get(0) instanceof SubFlow ? (SubFlow)subFlows.get(0) : null;
				if (DataType.IsNullOrEmpty(subFlow.getSubFlowCopyFields()) == false)
				{


					Attrs attrs = wkFrom.getEnMap().getAttrs();
					//父流程把子流程不同字段进行匹配赋值
					AtPara ap = new AtPara(subFlow.getSubFlowCopyFields());
					for (String str : ap.getHisHT().keySet())
					{
						Object val = ap.GetValStrByKey(str);
						if (DataType.IsNullOrEmpty(val.toString()) == true)
						{
							continue;
						}

						wk.SetValByKey(val.toString(), wkFrom.GetValByKey(str));
						rpt.SetValByKey(val.toString(), wkFrom.GetValByKey(str));
						if (dt.Columns.contains(str))
						{
							wk.SetValByKey(val.toString(), dt.Rows.get(0).getValue(str));
							rpt.SetValByKey(val.toString(), dt.Rows.get(0).getValue(str));
						}
					}
				}
			}

			///#endregion 从调用的节点上copy.


			///#region 获取web变量.
			for (Object k : paras.keySet())
			{
				if (Objects.equals(k, "OID"))
				{
					continue;
				}

				wk.SetValByKey(k.toString(), paras.get(k));
				rpt.SetValByKey(k.toString(), paras.get(k));
			}

			///#endregion 获取web变量.


			///#region 特殊赋值.
			// 在执行copy后，有可能这两个字段会被冲掉。
			if (CopyFormWorkID != null)
			{
				/*如果不是 执行的从已经完成的流程copy.*/

				wk.SetValByKey(GERptAttr.PFlowNo, PFlowNo);
				wk.SetValByKey(GERptAttr.PNodeID, PNodeID);
				wk.SetValByKey(GERptAttr.PWorkID, PWorkID);

				rpt.SetValByKey(GERptAttr.PFlowNo, PFlowNo);
				rpt.SetValByKey(GERptAttr.PNodeID, PNodeID);
				rpt.SetValByKey(GERptAttr.PWorkID, PWorkID);

				//忘记了增加这句话.
				rpt.SetValByKey(GERptAttr.PEmp, WebUser.getNo());

				//要处理单据编号 BillNo .
				if (!Objects.equals(this.getBillNoFormat(), ""))
				{
					rpt.SetValByKey(GERptAttr.BillNo, bp.wf.WorkFlowBuessRole.GenerBillNo(this.getBillNoFormat(), rpt.getOID(), rpt, this.getPTable()));

					//设置单据编号.
					wk.SetValByKey(GERptAttr.BillNo, rpt.getBillNo());
				}

				rpt.SetValByKey(GERptAttr.FID, 0);
				rpt.SetValByKey(GERptAttr.FlowStartRDT, DataType.getCurrentDateTime());
				rpt.SetValByKey(GERptAttr.FlowEnderRDT, DataType.getCurrentDateTime());
				rpt.SetValByKey(GERptAttr.WFState, WFState.Blank.getValue());
				rpt.SetValByKey(GERptAttr.FlowStarter, emp.getUserID());
				rpt.SetValByKey(GERptAttr.FlowEnder, emp.getUserID());
				rpt.SetValByKey(GERptAttr.FlowEndNode, this.getStartNodeID());
				rpt.SetValByKey(GERptAttr.FK_Dept, emp.getDeptNo());
				rpt.SetValByKey(GERptAttr.FK_NY, DataType.getCurrentYearMonth());

				if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserNameOnly)
				{
					rpt.SetValByKey(GERptAttr.FlowEmps, "@" + emp.getName() + "@");
				}

				if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserIDUserName)
				{
					rpt.SetValByKey(GERptAttr.FlowEmps, "@" + emp.getUserID() + "@");
				}

				if (Glo.getUserInfoShowModel() == UserInfoShowModel.UserIDUserName)
				{
					rpt.SetValByKey(GERptAttr.FlowEmps, "@" + emp.getUserID() + "," + emp.getName() + "@");
				}

			}

			if (rpt.getEnMap().getPhysicsTable().equals(wk.getEnMap().getPhysicsTable()) == false)
			{
				wk.Update(); //更新工作节点数据.
			}
			rpt.Update(); // 更新流程数据表.

			///#endregion 特殊赋值.


			///#region 复制其他数据..
			//复制明细。
			MapDtls dtls = wk.getHisMapDtls();
			if (!dtls.isEmpty())
			{
				MapDtls dtlsFrom = wkFrom.getHisMapDtls();
				int idx = 0;
				if (dtlsFrom.size() == dtls.size())
				{
					for (MapDtl dtl : dtls.ToJavaList())
					{

						if (dtl.getItIsCopyNDData() == false)
						{
							idx++;
							continue;
						}


						//new 一个实例.
						GEDtl dtlData = new GEDtl(dtl.getNo());

						//删除以前的数据.
						try
						{
							sql = "DELETE FROM " + dtlData.getEnMap().getPhysicsTable() + " WHERE RefPK=" + wk.getOID();
							DBAccess.RunSQL(sql);
						}
						catch (RuntimeException ex)
						{
							dtlData.CheckPhysicsTable();
						}

						MapDtl dtlFrom = dtlsFrom.get(idx) instanceof MapDtl ? (MapDtl)dtlsFrom.get(idx) : null;

						MapExts dtlMapExts = new MapExts(dtlFrom.getNo());

						GEDtls dtlsFromData = new GEDtls(dtlFrom.getNo());
						dtlsFromData.Retrieve(GEDtlAttr.RefPK, PWorkID, null);
						for (GEDtl geDtlFromData : dtlsFromData.ToJavaList())
						{
							//是否启用多附件
							FrmAttachmentDBs dbs = null;
							if (dtl.getItIsEnableAthM()== true)
							{
								//根据从表的OID 获取附件信息
								dbs = new FrmAttachmentDBs();
								dbs.Retrieve(FrmAttachmentDBAttr.RefPKVal, geDtlFromData.getOID(), null);
							}

							dtlData.Copy(geDtlFromData);
							dtlData.setRefPK(String.valueOf(wk.getOID()));
							dtlData.setFID(wk.getOID());
							if (this.getNo().equals(PFlowNo) == false && (this.getStartLimitRole() == bp.wf.StartLimitRole.OnlyOneSubFlow))
							{
								dtlData.SaveAsOID(geDtlFromData.getOID()); //为子流程的时候，仅仅允许被调用1次.
							}
							else
							{
								dtlData.InsertAsNew();
								if (dbs != null && !dbs.isEmpty())
								{
									//复制附件信息
									FrmAttachmentDB newDB = new FrmAttachmentDB();
									for (FrmAttachmentDB db : dbs.ToJavaList())
									{
										newDB.Copy(db);
										newDB.setRefPKVal(String.valueOf(dtlData.getOID()));
										newDB.setFID(dtlData.getOID());
										newDB.setMyPK(DBAccess.GenerGUID(0, null, null));
										newDB.Insert();
									}
								}
							}
							///#region 处理从表中的搜索多选
							for (MapExt en : dtlMapExts.ToJavaList())
							{
								//搜索多选
								if (en.getExtType().equals("MultipleChoiceSearch"))
								{
									try
									{
										//上一笔数据
										String refPK = String.valueOf(geDtlFromData.getOID());
										// 新生成的数据
										String newRefPK = String.valueOf(dtlData.getOID());
										//查出上一笔多选的数据
										sql = "SELECT * FROM Sys_FrmEleDB WHERE FK_MapData='" + dtlFrom.getNo() + "' AND RefPKVal = " + refPK;
										DataTable frmEleDBs = DBAccess.RunSQLReturnTable(sql);
										//复制多选的数据
										for (DataRow frmEn : frmEleDBs.Rows)
										{
											FrmEleDB frmEleDB = new FrmEleDB();
											frmEleDB.Copy(frmEn);
											frmEleDB.SetValByKey("RefPKVal", newRefPK);
											//重写MyPK
											frmEleDB.SetValByKey("MyPK", frmEleDB.getEleID() + "_" + frmEleDB.getFrmID() + "_" + frmEleDB.getRefPKVal() + "_" + frmEleDB.getTag1());
											if (frmEleDB.RetrieveFromDBSources() == 0)
											{
												frmEleDB.Insert(); // 执行插入
											}
											else
											{
												frmEleDB.Update(); // 执行更新
											}
										}
									}
									catch (Exception ex)
									{
										dtlData.CheckPhysicsTable();
									}
								}
							}
							///#endregion 处理从表中的搜索多选
						}
						idx++;
					}
				}
			}

			//复制附件数据。
			if (!wk.getHisFrmAttachments().isEmpty())
			{
				if (!wkFrom.getHisFrmAttachments().isEmpty())
				{
					int toNodeID = wk.getNodeID();

					//删除数据。
					DBAccess.RunSQL("DELETE FROM Sys_FrmAttachmentDB WHERE FK_MapData='ND" + toNodeID + "' AND RefPKVal='" + wk.getOID() + "'");
					FrmAttachmentDBs athDBs = new FrmAttachmentDBs("ND" + PNodeIDStr, String.valueOf(PWorkID));

					for (FrmAttachmentDB athDB : athDBs.ToJavaList())
					{
						FrmAttachmentDB athDB_N = new FrmAttachmentDB();
						athDB_N.Copy(athDB);
						athDB_N.setFrmID("ND" + toNodeID);
						athDB_N.setRefPKVal(String.valueOf(wk.getOID()));
						athDB_N.setFKFrmAttachment(athDB_N.getFKFrmAttachment().replace("ND" + PNodeIDStr, "ND" + toNodeID));

						if (athDB_N.getHisAttachmentUploadType() == AttachmentUploadType.Single)
						{
							/*如果是单附件.*/
							athDB_N.setMyPK(athDB_N.getFKFrmAttachment() + "_" + wk.getOID());
							if (athDB_N.getIsExits() == true)
							{
								continue; //说明上一个节点或者子线程已经copy过了, 但是还有子线程向合流点传递数据的可能，所以不能用break.
							}
							athDB_N.Insert();
						}
						else
						{
							athDB_N.setMyPK(athDB_N.getUploadGUID() + "_" + athDB_N.getFrmID() + "_" + wk.getOID());
							athDB_N.Insert();
						}
					}
				}
			}

			///#endregion 复制表单其他数据.


			///#region 复制独立表单数据.
			//求出来被copy的节点有多少个独立表单.
			FrmNodes fnsPearent = new FrmNodes(fromNd.getNodeID());
			if (fnsPearent.size() != 0)
			{
				//求当前节点表单的绑定的表单.
				FrmNodes fns = new FrmNodes(nd.getNodeID());
				if (fns.size() != 0)
				{
					//开始遍历当前绑定的表单.
					for (FrmNode fn : fns.ToJavaList())
					{
						if (fn.getFrmEnableRole() == FrmEnableRole.Disable)
						{
							continue;
						}
						if (fn.getWhoIsPK() != WhoIsPK.OID)
						{
							continue; //仅仅是workid的时候，才能copy.
						}

						for (FrmNode fnParent : fnsPearent.ToJavaList())
						{
							if (fnParent.getFrmEnableRole() == FrmEnableRole.Disable)
							{
								continue;
							}
							if (fn.getFrmID().equals(fnParent.getFrmID()) == false)
							{
								continue;
							}

							//父流程表单.
							GEEntityOID geEnFrom = new GEEntityOID(fnParent.getFrmID());
							geEnFrom.setOID(PWorkID);
							if (geEnFrom.RetrieveFromDBSources() == 0)
							{
								continue;
							}

							//执行数据copy , 复制到本身,表单.
							geEnFrom.CopyToOID(wk.getOID());
						}
					}
				}
			}

			///#endregion 复制独立表单数据.


			///#region 处理图片附件
			MapAttrs attrsRptDtl = new MapAttrs(wk.NodeFrmID);
			for(MapAttr en : attrsRptDtl.ToJavaList())
			{
				if(en.getUIContralType() == UIContralType.FrmImgAth)
				{
					//把上一笔数据的图片附件复制一份
					String imgSrc = ""; // 上一笔数据的附件路径
					String newImgSrc = ""; // 当前数据的附件路径
					if (wk.NodeFrmID.contains("ND"))
					{
						imgSrc = SystemConfig.getPathOfDataUser() + "/ImgAth/Data/" + en.getKeyOfEn() + "_" + CopyFormWorkID + ".png";
						newImgSrc = SystemConfig.getPathOfDataUser() + "/ImgAth/Data/" + en.getKeyOfEn() + "_" + wk.getOID() + ".png";
					}
					else
					{
						imgSrc = SystemConfig.getPathOfDataUser() + "/ImgAth/Data/" + wk.NodeFrmID + "_" + en.getKeyOfEn() + "_" + CopyFormWorkID + ".png";
						newImgSrc = SystemConfig.getPathOfDataUser() + "/ImgAth/Data/" + wk.NodeFrmID + "_" + en.getKeyOfEn() + "_" + wk.getOID() + ".png";
					}
					//判断是否上传了图片附件
					File imgAth = new File(imgSrc);
					if(imgAth.exists() == true)
					{
						//判断路径不为空
						if (DataType.IsNullOrEmpty(imgSrc) == false && DataType.IsNullOrEmpty(newImgSrc) == false)
						{
							BaseFileUtils.copyFileCover(imgSrc, newImgSrc, true);
						}
					}

				}
			}
			///#endregion 处理图片附件
		}

		///#endregion 处理流程之间的数据传递1。


		///#region 处理单据编号.
		//生成单据编号.
		if (this.getBillNoFormat().length() > 3)
		{
			Object tempVar = this.getBillNoFormat();
			String billNoFormat = tempVar instanceof String ? (String)tempVar : null;
			String billNo = rpt.getBillNo();
			if (DataType.IsNullOrEmpty(billNo) == true)
			{
				//生成单据编号.
				rpt.setBillNo(bp.wf.WorkFlowBuessRole.GenerBillNo(billNoFormat, rpt.getOID(), rpt, this.getPTable()));
				if (wk.getRow().containsKey(GERptAttr.BillNo) == true)
				{
					wk.SetValByKey(GERptAttr.BillNo, rpt.getBillNo());
				}
				rpt.Update();
			}
		}

		///#endregion 处理单据编号.


		///#region 处理流程之间的数据传递2, 如果是直接要跳转到指定的节点上去.
		if (paras.containsKey("JumpToNode") == true)
		{
			wk.setRec(WebUser.getNo());
			//wk.SetValByKey("FK_NY", DataType.getCurrentYearMonth());
			//wk.setDeptNo(emp.getDeptNo();
			//wk.SetValByKey("FK_DeptName", emp.getDeptText());
			//wk.SetValByKey("FK_DeptText", emp.getDeptText());
			wk.setFID(0);
			// wk.SetValByKey(GERptAttr.RecText, emp.getName());

			int jumpNodeID = Integer.parseInt(paras.get("JumpToNode").toString());
			Node jumpNode = new Node(jumpNodeID);

			String jumpToEmp = paras.get("JumpToEmp").toString();
			if (DataType.IsNullOrEmpty(jumpToEmp))
			{
				jumpToEmp = emp.getUserID();
			}

			WorkNode wn = new WorkNode(wk, nd);
			wn.NodeSend(jumpNode, jumpToEmp);

			WorkFlow wf = new WorkFlow(this, wk.getOID(), wk.getFID());

			gwf = new GenerWorkFlow(rpt.getOID());
			rpt.setWFState(WFState.Runing);
			rpt.Update();

			return wf.GetCurrentWorkNode().getHisWork();
		}

		///#endregion 处理流程之间的数据传递。


		///#region 最后整理wk数据.
		wk.setRec(emp.getUserID());
		//  wk.SetValByKey(WorkAttr.RDT, DataType.getCurrentDateTime());
		// wk.SetValByKey(WorkAttr.CDT, DataType.getCurrentDateTime());
		wk.SetValByKey("FK_NY", DataType.getCurrentYearMonth());
		wk.SetValByKey("FK_Dept", emp.getDeptNo());
		wk.SetValByKey("FK_DeptName", emp.getDeptText());
		wk.SetValByKey("FK_DeptText", emp.getDeptText());

		if (rpt.getEnMap().getAttrs().contains("BillNo") == true)
		{
			wk.SetValByKey(GERptAttr.BillNo, rpt.getBillNo());
		}

		wk.setFID(0);
		wk.Update();


		///#endregion 最后整理参数.


		///#region 给 generworkflow 初始化数据. add 2015-08-06

		gwf.setFlowNo(this.getNo());
		gwf.setFlowSortNo(this.getFlowSortNo());
		gwf.setSysType(this.getSysType());
		gwf.setNodeID(nd.getNodeID());
		//gwf.WorkID = wk.OID;
		gwf.setWFState(WFState.Blank);
		gwf.setFlowName(this.getName());

//		gwf.setNodeID(nd.getNodeID());
		gwf.setNodeName(nd.getName());
		gwf.setStarter(WebUser.getNo());
		gwf.setStarterName(WebUser.getName());
		gwf.setDeptNo(WebUser.getDeptNo());
		gwf.setDeptName(WebUser.getDeptName());
		gwf.setRDT(DataType.getCurrentDateTimess());
		gwf.setTitle(rpt.getTitle());
		gwf.setBillNo(rpt.getBillNo());
		if (gwf.getTitle().contains("@") == true)
		{
			gwf.setTitle(bp.wf.WorkFlowBuessRole.GenerTitle(this, rpt));
		}
		if (DataType.IsNullOrEmpty(PNodeIDStr) == false && DataType.IsNullOrEmpty(PWorkIDStr) == false)
		{
			if (DataType.IsNullOrEmpty(PFIDStr) == false)
			{
				gwf.setPFID(Long.parseLong(PFIDStr));
			}
			gwf.setPEmp(rpt.getPEmp());
			gwf.setPFlowNo(rpt.getPFlowNo());
			gwf.setPNodeID(rpt.getPNodeID());
			gwf.setPWorkID(rpt.getPWorkID());
		}
		gwf.setOrgNo(WebUser.getOrgNo());

		if (gwf.getWorkID() == 0)
		{
			gwf.setWorkID(wk.getOID());
			gwf.DirectInsert();
		}
		else
		{
			gwf.DirectUpdate();
		}

		///#endregion 给 generworkflow 初始化数据.


		return wk;
	}

	///#endregion 创建新工作.


	///#region 其他通用方法.
	/**
	 检查是否可以支持自动计算接受人.

	 @return
	 */
	public final String DoCheckFullSA() throws Exception {
		Nodes nds = this.getHisNodes();

		String msg = "";
		for (Node nd : nds.ToJavaList())
		{
			if (nd.getItIsStartNode() == true)
			{
				continue;
			}
			if (nd.getHisNodeType() != NodeType.UserNode)
			{
				continue;
			}

			if (nd.getHisRunModel() != RunModel.Ordinary)
			{
				msg += "@节点:" + nd.getNodeID() + "," + nd.getName() + ",不是工作节点,不支持分合流.";
			}

			if (nd.getHisDeliveryWay() == DeliveryWay.BySelected)
			{
				msg += "@节点:" + nd.getNodeID() + "," + nd.getName() + ",接收人规则是[主管选择]的,不支持自动计算接收人.";
			}

			if (nd.getHisDeliveryWay() == DeliveryWay.BySelected_2)
			{
				msg += "@节点:" + nd.getNodeID() + "," + nd.getName() + ",接收人规则是[固定范围选择]的,不支持自动计算接收人.";
			}

			//设置按连接线.计算方向.
			if (nd.getCondModel() != DirCondModel.ByLineCond)
			{
				nd.setCondModel(DirCondModel.ByLineCond);
				nd.Update();
			}
		}

		if (DataType.IsNullOrEmpty(msg) == true)
		{
			return "info@流程设置正确符合，自动计算接收人的预期.";
		}

		return "err@设置自动计算接受人不符合要求,详情如下." + msg;
	}
	public final String DoBTableDTS() throws Exception {
		if (this.getDTSWay() == DataDTSWay.None)
		{
			return "执行失败，您没有设置同步方式。";
		}

		String info = "";
		GenerWorkFlows gwfs = new GenerWorkFlows();
		gwfs.Retrieve(GenerWorkFlowAttr.FK_Flow, this.getNo(), null);
		for (GenerWorkFlow gwf : gwfs.ToJavaList())
		{
			GERpt rpt = this.getHisGERpt();
			rpt.setOID(gwf.getWorkID());
			rpt.RetrieveFromDBSources();

			info += "@开始同步:" + gwf.getTitle() + ",WorkID=" + gwf.getWorkID();
			if (gwf.getWFSta() == WFSta.Complete)
			{
				WorkNodePlus.DTSData(this, gwf, rpt, new Node(gwf.getNodeID()), true);
			}
			else
			{
				WorkNodePlus.DTSData(this, gwf, rpt, new Node(gwf.getNodeID()), false);
			}
			info += "同步成功.";
		}
		return info;
	}
	/**
	 自动发起

	 @return
	 */
	public final String DoAutoStartIt() throws Exception {
		switch (this.getHisFlowRunWay())
		{
			case SpecEmp: //指定人员按时运行。
				String RunObj = this.getRunObj();
				String FK_Emp = RunObj.substring(0, RunObj.indexOf('@'));
				Emp emp = new Emp();
				emp.setUserID(FK_Emp);
				if (emp.RetrieveFromDBSources() == 0)
				{
					return "启动自动启动流程错误：发起人(" + FK_Emp + ")不存在。";
				}

				WebUser.SignInOfGener(emp, "CH", false, false, null, null);
				String info_send = bp.wf.Dev2Interface.Node_StartWork(this.getNo(), null, null, 0, null, 0, null).ToMsgOfText();
				if (!Objects.equals(WebUser.getNo(), "admin"))
				{
					emp = new Emp("admin");

					WebUser.SignInOfGener(emp, "CH", false, false, null, null);
					return info_send;
				}
				return info_send;
			case SelectSQLModel: //按数据集合驱动的模式执行。
				break;
			default:
				return "@该流程您没有设置为自动启动的流程类型。";
		}

		String msg = "";
		MapExt me = new MapExt();
		me.setMyPK("ND" + Integer.parseInt(this.getNo()) + "01_" + MapExtXmlList.StartFlow);
		int i = me.RetrieveFromDBSources();
		if (i == 0)
		{
			Log.DebugWriteError("没有为流程(" + this.getName() + ")的开始节点设置发起数据,请参考说明书解决.");
			return "没有为流程(" + this.getName() + ")的开始节点设置发起数据,请参考说明书解决.";
		}
		if (DataType.IsNullOrEmpty(me.getTag()))
		{
			Log.DebugWriteError("没有为流程(" + this.getName() + ")的开始节点设置发起数据,请参考说明书解决.");
			return "没有为流程(" + this.getName() + ")的开始节点设置发起数据,请参考说明书解决.";
		}

		// 获取从表数据.
		DataSet ds = new DataSet();
		String[] dtlSQLs = me.getTag1().split("[*]", -1);
		for (String sql : dtlSQLs)
		{
			if (DataType.IsNullOrEmpty(sql))
			{
				continue;
			}

			String[] tempStrs = sql.split("[=]", -1);
			String dtlName = tempStrs[0];
			DataTable dtlTable = DBAccess.RunSQLReturnTable(sql.replace(dtlName + "=", ""));
			dtlTable.TableName = dtlName;
			ds.Tables.add(dtlTable);
		}


		///#region 检查数据源是否正确.
		String errMsg = "";
		// 获取主表数据.
		DataTable dtMain = DBAccess.RunSQLReturnTable(me.getTag());
		if (dtMain.Rows.size() == 0)
		{
			return "流程(" + this.getName() + ")此时无任务,查询语句:" + me.getTag().replace("'", "”");
		}

		msg += "@查询到(" + dtMain.Rows.size() + ")条任务.";

		if (dtMain.Columns.contains("Starter") == false)
		{
			errMsg += "@配值的主表中没有Starter列.";
		}

		if (dtMain.Columns.contains("MainPK") == false)
		{
			errMsg += "@配值的主表中没有MainPK列.";
		}

		if (errMsg.length() > 2)
		{
			return "流程(" + this.getName() + ")的开始节点设置发起数据,不完整." + errMsg;
		}

		///#endregion 检查数据源是否正确.


		///#region 处理流程发起.

		String fk_mapdata = "ND" + Integer.parseInt(this.getNo()) + "01";

		MapData md = new MapData(fk_mapdata);
		int idx = 0;
		for (DataRow dr : dtMain.Rows)
		{
			idx++;

			String mainPK = dr.getValue("MainPK").toString();
			String sql = "SELECT OID FROM " + md.getPTable() + " WHERE MainPK='" + mainPK + "'";
			if (DBAccess.RunSQLReturnTable(sql).Rows.size() != 0)
			{
				msg += "@" + this.getName() + ",第" + idx + "条,此任务在之前已经完成。";
				continue; //说明已经调度过了
			}

			String starter = dr.getValue("Starter").toString();
			if (!Objects.equals(WebUser.getNo(), starter))
			{
				WebUser.Exit();
				Emp emp = new Emp();
				emp.setUserID(starter);
				if (emp.RetrieveFromDBSources() == 0)
				{
					msg += "@" + this.getName() + ",第" + idx + "条,设置的发起人员:" + emp.getUserID() + "不存在.";
					msg += "@数据驱动方式发起流程(" + this.getName() + ")设置的发起人员:" + emp.getUserID() + "不存在。";
					continue;
				}
				WebUser.SignInOfGener(emp, "CH", false, false, null, null);
			}


			///#region  给值.
			Work wk = this.NewWork();
			for (DataColumn dc : dtMain.Columns)
			{
				wk.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName).toString());
			}

			if (ds.Tables.size()!= 0)
			{
				// MapData md = new MapData(nodeTable);
				MapDtls dtls = md.getMapDtls(); // new MapDtls(nodeTable);
				for (MapDtl dtl : dtls.ToJavaList())
				{
					for (DataTable dt : ds.Tables)
					{
						if (!Objects.equals(dt.TableName, dtl.getNo()))
						{
							continue;
						}

						//删除原来的数据。
						GEDtl dtlEn = dtl.getHisGEDtl();
						dtlEn.Delete(GEDtlAttr.RefPK, String.valueOf(wk.getOID()));

						// 执行数据插入。
						for (DataRow drDtl : dt.Rows)
						{
							if (!Objects.equals(drDtl.get("RefMainPK").toString(), mainPK))
							{
								continue;
							}

							dtlEn = dtl.getHisGEDtl();
							for (DataColumn dc : dt.Columns)
							{
								dtlEn.SetValByKey(dc.ColumnName, drDtl.get(dc.ColumnName).toString());
							}

							dtlEn.setRefPK(String.valueOf(wk.getOID()));
							dtlEn.setOID(0);
							dtlEn.Insert();
						}
					}
				}
			}

			///#endregion  给值.

			// 处理发送信息.
			Node nd = this.getHisStartNode();
			try
			{
				WorkNode wn = new WorkNode(wk, nd);
				String infoSend = wn.NodeSend().ToMsgOfHtml();
				Log.DebugWriteInfo(msg);
				msg += "@" + this.getName() + ",第" + idx + "条,发起人员:" + WebUser.getNo() + "-" + WebUser.getName() + "已完成.\r\n" + infoSend;
				//this.SetText("@第（" + idx + "）条任务，" + WebUser.getNo() + " - " + WebUser.getName() + "已经完成。\r\n" + msg);
			}
			catch (RuntimeException ex)
			{
				msg += "@" + this.getName() + ",第" + idx + "条,发起人员:" + WebUser.getNo() + "-" + WebUser.getName() + "发起时出现错误.\r\n" + ex.getMessage();
			}
			msg += "<hr>";
		}
		return msg;

		///#endregion 处理流程发起.
	}

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
	 清楚缓存.

	 @return
	 */
	public final String ClearCache() throws Exception {
		Cache.ClearCache();
		//清空流程中的缓存
		//获取改流程中的节点数据
		Nodes nds = new Nodes(this.getNo());
		for (Node nd : nds.ToJavaList())
		{
			//判断表单的类型
			if (nd.getHisFormType() == NodeFormType.FoolForm || nd.getHisFormType() == NodeFormType.Develop)
			{
				bp.sys.CCFormAPI.AfterFrmEditAction("ND" + nd.getNodeID());
			}
		}
		return "清除成功.";
	}
	/**
	 校验流程

	 @return
	 */
	public final String DoCheck() throws Exception {
		FlowCheckError en = new FlowCheckError(this);
		return en.DoCheck();
//		DBAccess.RunSQL("DELETE FROM Sys_MapExt WHERE DoWay='0' or DoWay='None'");
//		Cache.ClearCache();
//
//		//删除缓存数据.
//		this.ClearAutoNumCache(true);
//
//		Nodes nds = new Nodes(this.getNo());
//
//
//			///#region 检查独立表单
//		FrmNodes fns = new FrmNodes();
//		fns.Retrieve(FrmNodeAttr.FK_Flow, this.getNo(), null);
//		String frms = "";
//		String err = "";
//		MapData md = new MapData();
//		for (FrmNode item : fns.ToJavaList())
//		{
//			if (frms.contains(item.getFKFrm() + ","))
//			{
//				continue;
//			}
//			frms += item.getFKFrm() + ",";
//			md.setNo(item.getFKFrm());
//			if (md.IsExits() == false)
//			{
//				err += "@节点" + item.getNodeID() + "绑定的表单:" + item.getFKFrm() + ",已经被删除了.";
//			}
//			md.ClearCache();
//		}
//
//			///#endregion
//
//		try
//		{
//
//				///#region 对流程的设置做必要的检查.
//			// 设置流程名称.
//			DBAccess.RunSQL("UPDATE WF_Node SET FlowName = (SELECT Name FROM WF_Flow WHERE NO=WF_Node.FK_Flow) WHERE FK_Flow='" + this.getNo() + "'");
//
//			//设置单据编号只读格式.
//			DBAccess.RunSQL("UPDATE Sys_MapAttr SET UIIsEnable=0 WHERE KeyOfEn='BillNo' AND UIIsEnable=1 ");
//
//			//开始节点不能有会签.
//			DBAccess.RunSQL("UPDATE WF_Node SET HuiQianRole=0 WHERE NodePosType=0 AND HuiQianRole !=0  AND FK_Flow='" + this.getNo() + "'");
//
//			//开始节点不能有退回.
//			// DBAccess.RunSQL("UPDATE WF_Node SET ReturnRole=0 WHERE NodePosType=0 AND ReturnRole !=0");
//
//				///#endregion 对流程的设置做必要的检查.
//
//			//删除垃圾,非法数据.
//			String sqls = "DELETE FROM Sys_FrmSln WHERE FK_MapData NOT IN (SELECT No from Sys_MapData)";
//			sqls = "";
//			sqls += "@ DELETE FROM WF_Direction WHERE Node=ToNode";
//			DBAccess.RunSQLs(sqls);
//
//			//更新计算数据.
//			//this.NumOfBill = DBAccess.RunSQLReturnValInt("SELECT count(*) FROM Sys_FrmPrintTemplate WHERE NodeID IN (SELECT NodeID FROM WF_Flow WHERE No='" + this.getNo() + "')");
//			//this.NumOfDtl = DBAccess.RunSQLReturnValInt("SELECT count(*) FROM Sys_MapDtl WHERE FK_MapData='ND" + int.Parse(this.getNo()) + "Rpt'");
//			//this.DirectUpdate();
//
//			String msg = "@  =======  关于《" + this.getName() + " 》流程检查报告  ============";
//			msg += "@信息输出分为三种: 信息  警告  错误. 如果遇到输出的错误，则必须要去修改或者设置.";
//			msg += "@流程检查目前还不能覆盖100%的错误,需要手工的运行一次才能确保流程设计的正确性.";
//
//
//				///#region 对节点进行检查
//			//节点表单字段数据类型检查--begin---------
//			msg += CheckFormFields(nds);
//			//表单字段数据类型检查-------End-----
//
//			//获得字段用于校验sql.
//			MapAttrs mattrs = new MapAttrs("ND" + Integer.parseInt(this.getNo()) + "Rpt");
//
//			//查询所有的条件.
//			Conds conds = new Conds();
//			conds.Retrieve(CondAttr.FK_Flow, this.getNo(), null);
//
//
//			//删除垃圾数据.
//			String sql = "DELETE FROM WF_Direction  WHERE Node NOT IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "') AND FK_Flow='" + this.getNo() + "' ";
//			DBAccess.RunSQL(sql);
//			sql = "DELETE FROM WF_Direction  WHERE ToNode NOT IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "') AND FK_Flow='" + this.getNo() + "' ";
//			DBAccess.RunSQL(sql);
//
//			for (Node nd : nds.ToJavaList())
//			{
//				nd.CleanObject();
//				//流程是极简模式，设置每一个节点的NodeFrmID为开始节点表单
//				if (this.getFlowDevModel() == FlowDevModel.JiJian)
//				{
//					nd.SetValByKey(NodeAttr.NodeFrmID, "ND" + Integer.parseInt(this.getNo()) + "01");
//				}
//
//
//					///#region 设置路由节点或者用户节点到路由节点的转向规则为连接线
//				//路由节点
//				if (nd.getHisNodeType() == NodeType.RouteNode)
//				{
//					nd.setCondModel(DirCondModel.ByLineCond);
//				}
//				//到达的节点
//				Nodes toNDs = nd.getHisToNodes();
//				for (Node toND : toNDs.ToJavaList())
//				{
//					if (toND.getHisNodeType() == NodeType.RouteNode)
//					{
//						nd.setCondModel(DirCondModel.ByLineCond);
//						break;
//					}
//				}
//
//					///#endregion 设置路由节点或者用户节点到路由节点的转向规则为连接线
//
//				try
//				{
//					//设置它的位置类型.
//					nd.SetValByKey(NodeAttr.NodePosType, nd.GetHisNodePosType().getValue());
//				}
//				catch (Exception ex)
//				{
//					msg += "@错误: 节点ID:(" + nd.getNodeID() + ")名称:(" + nd.getName() + ") 到达节点错误："+ex.getMessage();
//					msg += "@警告: 该问题解决后，请在执行检查流程。";
//					return msg;
//				}
//
//				msg += "@信息: -------- 开始检查节点ID:(" + nd.getNodeID() + ")名称:(" + nd.getName() + ")信息 -------------";
//					///#region 对节点的访问规则进行检查
//				msg += "@信息:开始对节点的访问规则进行检查.";
//				if (nd.getHisNodeType() == NodeType.UserNode)
//				{
//					switch (nd.getHisDeliveryWay())
//					{
//						case ByStation:
//						case FindSpecDeptEmpsInStationlist:
//							if (nd.getNodeStations().size()== 0)
//							{
//								msg += "@错误:您设置了该节点的访问规则是按角色，但是您没有为节点绑定角色。";
//							}
//							break;
//						case ByDept:
//							if (nd.getNodeDepts().size()== 0)
//							{
//								msg += "@错误:您设置了该节点的访问规则是按部门，但是您没有为节点绑定部门。";
//							}
//							break;
//						case ByBindEmp:
//							if (nd.getNodeEmps().size()== 0)
//							{
//								msg += "@错误:您设置了该节点的访问规则是按人员，但是您没有为节点绑定人员。";
//							}
//							break;
//						case BySpecNodeEmp: //按指定的角色计算.
//						case BySpecNodeEmpStation: //按指定的角色计算.
//							if (nd.getDeliveryParas().trim().length() == 0)
//							{
//								msg += "@错误:您设置了该节点的访问规则是按指定的角色计算，但是您没有设置节点编号.";
//							}
//							break;
//						case ByDeptAndStation: //按部门与角色的交集计算.
//							String mysql = "";
//							//added by liuxc,2015.6.30.
//
//							mysql = "SELECT pdes.fk_emp AS No"
//									+ " FROM   Port_DeptEmpStation pdes"
//									+ "        INNER JOIN WF_NodeDept wnd"
//									+ "             ON  wnd.fk_dept = pdes.fk_dept"
//									+ "             AND wnd.fk_node = " + nd.getNodeID()
//									+ "        INNER JOIN WF_NodeStation wns"
//									+ "             ON  wns.FK_Station = pdes.fk_station"
//									+ "             AND wnd.fk_node =" + nd.getNodeID()
//									+ " ORDER BY"
//									+ "        pdes.fk_emp";
//
//
//							DataTable mydt = DBAccess.RunSQLReturnTable(mysql);
//							if (mydt.Rows.size() == 0)
//							{
//								msg += "@错误:按照角色与部门的交集计算错误，没有人员集合{" + mysql + "}";
//							}
//							break;
//						case BySQL:
//						case BySQLAsSubThreadEmpsAndData:
//							if (nd.getDeliveryParas().trim().length() == 0)
//							{
//								msg += "@错误:您设置了该节点的访问规则是按SQL查询，但是您没有在节点属性里设置查询sql，此sql的要求是查询必须包含No,Name两个列，sql表达式里支持@+字段变量，详细参考开发手册.";
//							}
//							else
//							{
//								try
//								{
//									sql = nd.getDeliveryParas();
//									sql = bp.wf.Glo.DealExp(sql, this.getHisGERpt(), null);
//
//									sql = sql.replace("''''", "''"); //出现双引号的问题.
//									if (sql.contains("@"))
//									{
//										throw new RuntimeException("您编写的sql变量填写不正确，实际执行中，没有被完全替换下来" + sql);
//									}
//
//									DataTable testDB = null;
//									try
//									{
//										testDB = DBAccess.RunSQLReturnTable(sql);
//									}
//									catch (RuntimeException ex)
//									{
//										msg += "@错误:您设置了该节点的访问规则是按SQL查询,执行此语句错误." + ex.getMessage();
//									}
//
//									if (testDB.Columns.contains("no") == false || testDB.Columns.contains("name") == false)
//									{
//										msg += "@错误:您设置了该节点的访问规则是按SQL查询，设置的sql不符合规则，此sql的要求是查询必须包含No,Name两个列，sql表达式里支持@+字段变量，详细参考开发手册.";
//									}
//								}
//								catch (RuntimeException ex)
//								{
//									msg += ex.getMessage();
//								}
//							}
//							break;
//						case ByPreviousNodeFormEmpsField:
//						case ByPreviousNodeFormStationsAI:
//						case ByPreviousNodeFormStationsOnly:
//						case ByPreviousNodeFormDepts:
//							//去rpt表中，查询是否有这个字段.
//							String str1 = String.valueOf(nd.getNodeID()).substring(0, String.valueOf(nd.getNodeID()).length() - 2);
//							MapAttrs rptAttrs = new MapAttrs();
//							rptAttrs.Retrieve(MapAttrAttr.FK_MapData, "ND" + str1 + "Rpt", MapAttrAttr.KeyOfEn);
//
//							if (rptAttrs.contains(MapAttrAttr.KeyOfEn, nd.getDeliveryParas()) == false)
//							{
//								/*检查节点字段是否有FK_Emp字段*/
//								msg += "@错误:您设置了该节点的访问规则是[06.按上一节点表单指定的字段值作为本步骤的接受人]，但是您没有在节点属性的[访问规则设置内容]里设置指定的表单字段，详细参考开发手册.";
//							}
//							//if (mattrs.contains(BP.Sys.MapAttrAttr.KeyOfEn, "FK_Emp") == false)
//							//{
//							//    /*检查节点字段是否有FK_Emp字段*/
//							//    msg += "@错误:您设置了该节点的访问规则是按指定节点表单人员，但是您没有在节点表单中增加FK_Emp字段，详细参考开发手册 .";
//							//}
//							break;
//						case BySelected: // 由上一步发送人员选择
//							if (nd.getItIsStartNode())
//							{
//								//msg += "@错误:开始节点不能设置指定的选择人员访问规则。";
//								break;
//							}
//							break;
//						case ByPreviousNodeEmp: // 由上一步发送人员选择
//							if (nd.getItIsStartNode())
//							{
//								msg += "@错误:节点访问规则设置错误:开始节点，不允许设置与上一节点的工作人员相同.";
//								break;
//							}
//							break;
//						default:
//							break;
//					}
//				}
//
//				msg += "@对节点的访问规则进行检查完成....";
//
//					///#endregion
//
//
//					///#region 检查节点完成条件，方向条件的定义.
//				if (conds.size() != 0)
//				{
//					msg += "@信息:开始检查(" + nd.getName() + ")方向条件:";
//					for (Cond cond : conds.ToJavaList())
//					{
//						msg += cond.getAttrKey() + cond.getAttrName() + cond.getOperatorValue() + "、";
//					}
//					msg += "@(" + nd.getName() + ")方向条件检查完成.....";
//				}
//
//					///#endregion 检查节点完成条件的定义.
//
//				/*#region 如果是引用的表单库的表单，就要检查该表单是否有FID字段，没有就自动增加.
//				MapAttr mattr = new MapAttr();
//				if (nd.getHisFormType() == NodeFormType.RefOneFrmTree)
//				{
//
//				    mattr.setMyPK(nd.NodeFrmID + "_FID");
//				    if (mattr.RetrieveFromDBSources() == 0)
//				    {
//				        mattr.setKeyOfEn("FID");
//				        mattr.setFrmID(nd.getNodeFrmID());
//				        mattr.setMyDataType(DataType.AppInt);
//				        mattr.setUIVisible(false);
//				        mattr.setName("FID(自动增加)");
//				        mattr.Insert();
//
//
//				    }
//				    GEEntity en = new GEEntity(nd.getNodeFrmID());
//				    en.CheckPhysicsTable();
//				}
//				mattr.setMyPK(nd.NodeFrmID + "_AtPara");
//				if (mattr.RetrieveFromDBSources() == 0)
//				{
//				    mattr.setFrmID(nd.getNodeFrmID());
//				    mattr.setHisEditType(EditType.UnDel;
//				    mattr.setKeyOfEn(GERptAttr.AtPara);
//				    mattr.setName("参数"); // 单据编号
//				    mattr.setMyDataType(DataType.AppString);
//				    mattr.setUIContralType(UIContralType.TB);
//				    mattr.setLGType(FieldTypeS.Normal);
//				    mattr.setUIVisible(false);
//				    mattr.setUIIsEnable(false);
//				    mattr.setUIIsLine (false);
//				    mattr.setMinLen(0);
//				    mattr.setMaxLen(4000);
//				    mattr.setIdx(-1);
//				    mattr.Insert();
//
//
//				}
//				#endregion 如果是引用的表单库的表单，就要检查该表单是否有FID字段，没有就自动增加.*/
//
//				//如果是子线城，子线程的表单必须是轨迹模式。
//				if (nd.getItIsSubThread() == true)
//				{
//					md = new MapData("ND" + nd.getNodeID());
//					if (!Objects.equals(md.getPTable(), "ND" + nd.getNodeID()))
//					{
//						md.setPTable("ND" + nd.getNodeID());
//						md.Update();
//						md.ClearCache();
//					}
//
//					//检查数据表.
//					GEEntity geEn = new GEEntity(md.getNo());
//					geEn.CheckPhysicsTable();
//				}
//
//			}
//
//				///#endregion
//
//			/**#region 检查延续流程,子流程发起。
//			SubFlowYanXus ygflows = new SubFlowYanXus();
//			ygflows.Retrieve(SubFlowYanXuAttr.SubFlowNo, this.No, SubFlowYanXuAttr.SubFlowType,
//			    (int)SubFlowType.YanXuFlow);
//			foreach (SubFlowYanXu flow in ygflows)
//			{
//			    Flow fl = new Flow(flow.SubFlowNo);
//
//
//			    if (fl.SubFlowOver == SubFlowOver.SendParentFlowToNextStep)
//			    {
//			        Node nd = new Node(flow.FK_Node);
//			        if (nd.getHisToNodes().size()> 1)
//			        {
//			            msg += "@当前节点[" + nd.getName() +"]的可以启动子流程或者延续流程.被启动的子流程设置了当子流程结束时让父流程自动运行到下一个节点，但是当前节点有分支，导致流程无法运行到下一个节点.";
//			        }
//
//			        if (nd.getHisToNodes().size()== 1)
//			        {
//			            Node toNode = nd.getHisToNodes()[0] as Node;
//			            if (nd.getHisDeliveryWay() == DeliveryWay.BySelected)
//			                msg += "@当前节点[" + nd.getName() +"]的可以启动子流程或者延续流程.被启动的子流程设置了当子流程结束时让父流程自动运行到下一个节点，但是当前节点有分支，导致流程无法运行到下一个节点.";
//			        }
//
//			    }
//			}
//			#endregion 检查越轨流程,子流程发起。**/
//
//			msg += "@流程的基础信息: ------ ";
//			msg += "@编号:  " + this.getNo() + " 名称:" + this.getName() + " , 存储表:" + this.getPTable();
//
//			msg += "@信息:开始检查节点流程报表.";
//			String str = this.DoCheck_CheckRpt(this.getHisNodes());
//			if (DataType.IsNullOrEmpty(str) == false)
//			{
//				msg += "@错误:表单枚举,外键字段UIBindKey信息丢失,请描述该字段的设计过程，反馈给开发人员,并删除错误字段重新在表单上创建。错误字段信息如下:";
//				msg += "@[" + str + "]";
//			}
//
//
//				///#region 检查焦点字段设置是否还有效
//			msg += "@信息:开始检查节点的焦点字段";
//
//			//获得gerpt字段.
//			GERpt rpt = this.getHisGERpt();
//			for (Attr attr : rpt.getEnMap().getAttrs())
//			{
//				rpt.SetValByKey(attr.getKey(), "0");
//			}
//
//			//处理焦点字段.
//			for (Node nd : nds.ToJavaList())
//			{
//				if (Objects.equals(nd.getFocusField().trim(), "") && nd.getHisNodeType() == NodeType.UserNode)
//				{
//					Work wk = nd.getHisWork();
//					String attrKey = "";
//					for (Attr attr : wk.getEnMap().getAttrs())
//					{
//						if (attr.getUIVisible() == true && attr.getUIIsDoc() && attr.getUIIsReadonly() == false && (attr.getKey().contains("Check") || attr.getKey().contains("Note")))
//						{
//							attrKey = attr.getDesc() + ":@" + attr.getKey();
//						}
//					}
//					if (Objects.equals(attrKey, ""))
//					{
//						msg += "@警告:节点ID:" + nd.getNodeID() + " 名称:" + nd.getName() + "属性里没有设置焦点字段，会导致信息写入轨迹表空白，为了能够保证流程轨迹是可读的请设置焦点字段.";
//					}
//					else
//					{
//						msg += "@信息:节点ID:" + nd.getNodeID() + " 名称:" + nd.getName() + "属性里没有设置焦点字段，会导致信息写入轨迹表空白，为了能够保证流程轨迹是可读的系统自动设置了焦点字段为" + attrKey + ".";
//						nd.setFocusField(attrKey);
//						nd.DirectUpdate();
//					}
//					continue;
//				}
//
//				Object tempVar = nd.getFocusField();
//				String strs = tempVar instanceof String ? (String)tempVar : null;
//				strs = bp.wf.Glo.DealExp(strs, rpt, "err");
//				if (strs.contains("@") == true)
//				{
//					// msg += "@错误:焦点字段（" + nd.getFocusField() + "）在节点(step:" + nd.Step + " 名称:" + nd.getName() +")属性里的设置已无效，表单里不存在该字段.";
//					//删除节点属性中的焦点字段
//					nd.setFocusField("");
//					nd.Update();
//				}
//				else
//				{
//					msg += "@提示:节点的(" + nd.getNodeID() + "," + nd.getName() + ")焦点字段（" + nd.getFocusField() + "）设置完整检查通过.";
//				}
//
//				if (this.getItIsMD5())
//				{
//					if (nd.getHisWork().getEnMap().getAttrs().contains(WorkAttr.MD5) == false)
//					{
//						nd.RepareMap(this);
//					}
//				}
//			}
//			msg += "@信息:检查节点的焦点字段完成.";
//
//				///#endregion
//
//
//				///#region 检查质量考核点.
//			msg += "@信息:开始检查质量考核点";
//			for (Node nd : nds.ToJavaList())
//			{
//				if (nd.getItIsEval())
//				{
//					/*如果是质量考核点，检查节点表单是否具别质量考核的特别字段？*/
//					sql = "SELECT COUNT(*) FROM Sys_MapAttr WHERE FK_MapData='ND" + nd.getNodeID() + "' AND KeyOfEn IN ('EvalEmpNo','EvalEmpName','EvalEmpCent')";
//					if (DBAccess.RunSQLReturnValInt(sql) != 3)
//					{
//						msg += "@信息:您设置了节点(" + nd.getNodeID() + "," + nd.getName() + ")为质量考核节点，但是您没有在该节点表单中设置必要的节点考核字段.";
//					}
//				}
//			}
//			msg += "@检查质量考核点完成.";
//
//				///#endregion
//
//
//				///#region 检查如果是合流节点必须不能是由上一个节点指定接受人员.
//			for (Node nd : nds.ToJavaList())
//			{
//				//如果是合流节点.
//				if (nd.getHisNodeWorkType() == NodeWorkType.WorkHL || nd.getHisNodeWorkType() == NodeWorkType.WorkFHL)
//				{
//					if (nd.getHisDeliveryWay() == DeliveryWay.BySelected)
//					{
//						msg += "@错误:节点ID:" + nd.getNodeID() + " 名称:" + nd.getName() + "是合流或者分合流节点，但是该节点设置的接收人规则为由上一步指定，这是错误的，应该为自动计算而非每个子线程人为的选择.";
//					}
//				}
//			}
//
//				///#endregion 检查如果是合流节点必须不能是由上一个节点指定接受人员。
//
//			//如果协作模式的节点，方向条件规则是下拉框的，修改为按线的.
//			sql = "UPDATE WF_Node SET CondModel = 2 WHERE CondModel = 1 AND TodolistModel = 1";
//			DBAccess.RunSQL(sql);
//
//			msg += "@流程报表检查完成...";
//
//			// 检查流程， 处理计算字段.
//			Node.CheckFlow(nds, this.getNo());
//			for (Node nd : nds.ToJavaList())
//			{
//
//				nd.ClearAutoNumCache(true);
//				nd.setRow(null);
//				Cache2019.DeleteRow("BP.WF.Node", nd.getNodeID() + "");
//			}
//
//			//创建track.
//			Track.CreateOrRepairTrackTable(this.getNo());
//
//			//String mysql = "SELECT No FROM Sys_MapDtl WHERE No=FK_MapData";
//			//DataTable dt = DBAccess.RunSQLReturnTable(sql);
//			//foreach (DataRow item in dt.Rows)
//			//{
//			// //MapDtl md = new MapDtl();
//			//}
//			return msg;
//		}
//		catch (RuntimeException ex)
//		{
//			throw new RuntimeException("@检查流程错误:" + ex.getMessage() + " @" + ex.getStackTrace());
//		}
	}
	/**
	 节点表单字段数据类型检查，名字相同的字段出现类型不同的处理方法：
	 依照不同于NDxxRpt表中同名字段类型为基准

	 @return 检查结果
	 */
	private String CheckFormFields(Nodes nds)
	{
		StringBuilder errorAppend = new StringBuilder();
		errorAppend.append("@信息: -------- 流程节点表单的字段类型检查: ------ ");
		try
		{
			String fk_mapdatas = "'ND" + Integer.parseInt(this.getNo()) + "Rpt'";
			for (Node nd : nds.ToJavaList())
			{
				fk_mapdatas += ",'ND" + nd.getNodeID() + "'";
			}

			//筛选出类型不同的字段.
			String checkSQL = "SELECT   AA.KEYOFEN, COUNT(*) AS MYNUM FROM (" + "  SELECT A.KEYOFEN,  MYDATATYPE,  COUNT(*) AS MYNUM " + "  FROM SYS_MAPATTR A WHERE FK_MAPDATA IN (" + fk_mapdatas + ") GROUP BY KEYOFEN, MYDATATYPE" + ")  AA GROUP BY  AA.KEYOFEN HAVING COUNT(*) > 1";
			DataTable dt_Fields = DBAccess.RunSQLReturnTable(checkSQL);
			for (DataRow row : dt_Fields.Rows)
			{
				String keyOfEn = row.get("KEYOFEN").toString();
				String myNum = row.get("MYNUM").toString();
				int iMyNum = 0;
				if (DataType.IsNullOrEmpty(myNum) == false)
					iMyNum = Integer.parseInt(myNum);
				//存在2种以上数据类型，有手动进行调整
				if (iMyNum > 2)
				{
					errorAppend.append("@错误：字段名" + keyOfEn + "在此流程表(" + fk_mapdatas + ")中存在2种以上数据类型(如：int，float,varchar,datetime)，请手动修改。");
					return errorAppend.toString();
				}

				//存在2种数据类型，以不同于NDxxRpt字段类型为主
				MapAttr baseMapAttr = new MapAttr();
				MapAttr rptMapAttr = new MapAttr("ND" + Integer.parseInt(this.getNo()) + "Rpt", keyOfEn);

				//Rpt表中不存在此字段
				if (rptMapAttr == null || Objects.equals(rptMapAttr.getMyPK(), ""))
				{
					this.DoCheck_CheckRpt(this.getHisNodes());
					rptMapAttr = new MapAttr("ND" + Integer.parseInt(this.getNo()) + "Rpt", keyOfEn);
					this.getHisGERpt().CheckPhysicsTable();
				}

				//Rpt表中不存在此字段,直接结束
				if (rptMapAttr == null || Objects.equals(rptMapAttr.getMyPK(), ""))
				{
					continue;
				}

				for (Node nd : nds.ToJavaList())
				{
					MapAttr ndMapAttr = new MapAttr("ND" + nd.getNodeID(), keyOfEn);
					if (ndMapAttr == null || Objects.equals(ndMapAttr.getMyPK(), ""))
					{
						continue;
					}

					//找出与NDxxRpt表中字段数据类型不同的表单
					if (rptMapAttr.getMyDataType() != ndMapAttr.getMyDataType())
					{
						baseMapAttr = ndMapAttr;
						break;
					}
				}
				errorAppend.append("@基础表" + baseMapAttr.getFrmID() + "，字段" + keyOfEn + "数据类型为：" + baseMapAttr.getMyDataTypeStr());
				//根据基础属性类修改数据类型不同的表单
				for (Node nd : nds.ToJavaList())
				{
					MapAttr ndMapAttr = new MapAttr("ND" + nd.getNodeID(), keyOfEn);
					//不包含此字段的进行返回,类型相同的进行返回
					if (ndMapAttr == null || Objects.equals(ndMapAttr.getMyPK(), "") || Objects.equals(baseMapAttr.getMyPK(), ndMapAttr.getMyPK()) || baseMapAttr.getMyDataType() == ndMapAttr.getMyDataType())
					{
						continue;
					}

					ndMapAttr.setName(baseMapAttr.getName());
					ndMapAttr.setMyDataType(baseMapAttr.getMyDataType());
					ndMapAttr.setUIWidth(baseMapAttr.getUIWidth());
					ndMapAttr.setUIHeight(baseMapAttr.getUIHeight());
					ndMapAttr.setMinLen(baseMapAttr.getMinLen());
					ndMapAttr.setMaxLen(baseMapAttr.getMaxLen());
					if (ndMapAttr.Update() > 0)
					{
						errorAppend.append("@修改了" + "ND" + nd.getNodeID() + " 表，字段" + keyOfEn + "修改为：" + baseMapAttr.getMyDataTypeStr());
					}
					else
					{
						errorAppend.append("@错误:修改" + "ND" + nd.getNodeID() + " 表，字段" + keyOfEn + "修改为：" + baseMapAttr.getMyDataTypeStr() + "失败。");
					}
				}
				//修改NDxxRpt
				rptMapAttr.setName(baseMapAttr.getName());
				rptMapAttr.setMyDataType(baseMapAttr.getMyDataType());
				rptMapAttr.setUIWidth(baseMapAttr.getUIWidth());
				rptMapAttr.setUIHeight(baseMapAttr.getUIHeight());
				rptMapAttr.setMinLen(baseMapAttr.getMinLen());
				rptMapAttr.setMaxLen(baseMapAttr.getMaxLen());
				if (rptMapAttr.Update() > 0)
				{
					errorAppend.append("@修改了" + "ND" + Integer.parseInt(this.getNo()) + "Rpt 表，字段" + keyOfEn + "修改为：" + baseMapAttr.getMyDataTypeStr());
				}
				else
				{
					errorAppend.append("@错误:修改" + "ND" + Integer.parseInt(this.getNo()) + "Rpt 表，字段" + keyOfEn + "修改为：" + baseMapAttr.getMyDataTypeStr() + "失败。");
				}
			}
		}
		catch (RuntimeException ex)
		{
			errorAppend.append("@错误:" + ex.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return errorAppend.toString();
	}

	///#endregion 其他方法.



	///#region 产生数据模板。
	private final static String PathFlowDesc;
	static
	{
		PathFlowDesc = SystemConfig.getPathOfDataUser() + "FlowDesc/";
	}
	/**
	 生成流程模板

	 @return
	 */
	public final String GenerFlowXmlTemplete() throws Exception {
		String name = this.getName();
		name = bp.tools.StringExpressionCalculate.ReplaceBadCharOfFileName(name);

		String path = this.getNo() + "." + name;
		path = PathFlowDesc + path + "/";

		this.DoExpFlowXmlTemplete(path);

		name = path + name + ".xml";
		return name;
	}
	/**
	 生成流程模板

	 @param path
	 @return
	 */
	public final DataSet DoExpFlowXmlTemplete(String path) throws Exception {
		if ((new File(path)).isDirectory() == false)
		{
			(new File(path)).mkdirs();
		}

		DataSet ds;
		try
		{
			ds = GetFlow(path);
		}
		catch (RuntimeException e)
		{
			throw new RuntimeException("err@流程模板导出失败：" + e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (ds != null)
		{
			String name = this.getName();
			name = bp.tools.StringExpressionCalculate.ReplaceBadCharOfFileName(name);
			name = name + ".xml";
			String filePath = path + "/" + name;
			ds.WriteXml(filePath,XmlWriteMode.WriteSchema, ds);
		}
		return ds;
	}

	//xml文件是否正在操作中
	private static boolean isXmlLocked;
	/**
	 备份当前流程到用户xml文件
	 用户每次保存时调用
	 捕获异常写入日志,备份失败不影响正常保存
	 */
	public final void WriteToXml()
	{
		try
		{
			String name = this.getNo() + "." + this.getName();
			name = bp.tools.StringExpressionCalculate.ReplaceBadCharOfFileName(name);
			String path = PathFlowDesc + name + "/";
			DataSet ds = GetFlow(path);
			if (ds == null)
			{
				return;
			}

			String directory = this.getNo() + "." + this.getName();
			directory = bp.tools.StringExpressionCalculate.ReplaceBadCharOfFileName(directory);
			path = PathFlowDesc + directory + "/";
			String xmlName = path + "Flow" + ".xml";

			if (!isXmlLocked)
			{
				if (!DataType.IsNullOrEmpty(path))
				{
					if (!(new File(path)).isDirectory())
					{
						(new File(path)).mkdirs();
					}
					else if ((new File(xmlName)).isFile())
					{
						FileTime time = Files.getLastModifiedTime(Paths.get(xmlName));
						Date lastModified = new Date(time.toMillis());
						SimpleDateFormat dateFormat = new SimpleDateFormat("@yyyyMMddHHmmss");
						String formattedDateTime = dateFormat.format(lastModified);
						String xmlNameOld = path + "Flow" + formattedDateTime + ".xml";

						isXmlLocked = true;
						if ((new File(xmlNameOld)).isFile())
						{
							(new File(xmlNameOld)).delete();
						}
						Files.move(Paths.get(xmlName), Paths.get(xmlNameOld));
					}
				}

				if (DataType.IsNullOrEmpty(xmlName) == false)
				{
					ds.WriteXml(xmlName, XmlWriteMode.WriteSchema, ds);
					isXmlLocked = false;
				}
			}
		}
		catch (RuntimeException | IOException e)
		{
			isXmlLocked = false;
			Log.DebugWriteError("流程模板文件备份错误:" + e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public final DataSet GetFlow(String path) throws Exception {
		// 把所有的数据都存储在这里。
		DataSet ds = new DataSet();

		// 流程信息。
		String sql = "SELECT * FROM WF_Flow WHERE No='" + this.getNo() + "'";

		Flow fl = new Flow(this.getNo());
		DataTable dtFlow = fl.ToDataTableField("WF_Flow");
		dtFlow.Rows.get(0).setValue("FlowMark","");
		dtFlow.TableName = "WF_Flow";
		ds.Tables.add(dtFlow);

		//增加同步信息
		SyncDatas syncDatas = new SyncDatas(this.getNo());
		DataTable syncDataDt = syncDatas.ToDataTableField("WF_SyncData");
		ds.Tables.add(syncDataDt);

		SyncDataFields syncDataFields = new SyncDataFields();
		syncDataFields.Retrieve(SyncDataFieldAttr.FlowNo,this.getNo());
		DataTable syncDataFieldDt = syncDatas.ToDataTableField("WF_SyncDataField");
		ds.Tables.add(syncDataFieldDt);

		// 节点信息
		TemplateNodes tempNds = new TemplateNodes(this.getNo());
		DataTable dtTNodes = tempNds.ToDataTableField("WF_TemplateNode");
		ds.Tables.add(dtTNodes);

		Nodes nds = new Nodes(this.getNo());
		DataTable dtNodes = nds.ToDataTableField("WF_Node");
		ds.Tables.add(dtNodes);

		FlowOrgs flowOrgs = new FlowOrgs();
		flowOrgs.Retrieve(FlowOrgAttr.FlowNo,this.getNo());
		DataTable dtFlowOrgs = flowOrgs.ToDataTableField("WF_FlowOrg");
		ds.Tables.add(dtFlowOrgs);

		//节点属性
		NodeExts ndexts = new NodeExts(this.getNo());
		DataTable dtNodeExts = ndexts.ToDataTableField("WF_NodeExt");
		ds.Tables.add(dtNodeExts);

		NodeWorkChecks nodeWorkChecks = new NodeWorkChecks();
		nodeWorkChecks.Retrieve(NodeAttr.FK_Flow, this.getNo(), NodeAttr.Step);
		DataTable dtNodeWorkChecks = nodeWorkChecks.ToDataTableField("WF_NodeWorkCheck");
		ds.Tables.add(dtNodeWorkChecks);
		//接收人规则
		Selectors selectors = new Selectors(this.getNo());
		DataTable dtSelectors = selectors.ToDataTableField("WF_Selector");
		ds.Tables.add(dtSelectors);

		//节点消息
		PushMsgs pushMsgs = new PushMsgs();
		try {
			pushMsgs.Retrieve(PushMsgAttr.FlowNo, this.getNo(), null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		ds.Tables.add(pushMsgs.ToDataTableField("WF_PushMsg"));

		// 单据模版.
		FrmPrintTemplates tmps = new FrmPrintTemplates(this.getNo());
		String pks = "";
		for (FrmPrintTemplate tmp : tmps.ToJavaList())
		{
			try
			{
				if (path != null)
				{
					Files.copy(Paths.get(SystemConfig.getPathOfDataUser() + "/CyclostyleFile/" + tmp.getMyPK() + ".rtf"), Paths.get(path + "/" + tmp.getMyPK() + ".rtf"), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
				}
			}
			catch (java.lang.Exception e)
			{
				pks += "@" + tmp.getPKVal();
				tmp.Delete();
			}
		}
		tmps.remove(pks);
		ds.Tables.add(tmps.ToDataTableField("Sys_FrmPrintTemplate"));


		String sqlin = "SELECT NodeID FROM WF_Node WHERE fk_flow='" + this.getNo() + "'";

		// 条件信息
		Conds cds = new Conds(this.getNo());
		ds.Tables.add(cds.ToDataTableField("WF_Cond"));

		// 节点与表单绑定.
		FrmNodes fns = new FrmNodes();
		fns.Retrieve(FrmNodeAttr.FK_Flow, this.getNo(), null);
		ds.Tables.add(fns.ToDataTableField("WF_FrmNode"));


		// 表单方案.
		FrmFields ffs = new FrmFields();
		ffs.Retrieve(FrmFieldAttr.FK_Flow, this.getNo(), null);
		ds.Tables.add(ffs.ToDataTableField("Sys_FrmSln"));

		// 方向
		Directions dirs = new Directions();
		dirs.Retrieve(DirectionAttr.FK_Flow, this.getNo(), null);
		ds.Tables.add(dirs.ToDataTableField("WF_Direction"));

		// 流程标签.
		LabNotes labs = new LabNotes(this.getNo());
		ds.Tables.add(labs.ToDataTableField("WF_LabNote"));

		// 可退回的节点。
		NodeReturns nrs = new NodeReturns();
		nrs.RetrieveInSQL(NodeReturnAttr.FK_Node, sqlin);
		ds.Tables.add(nrs.ToDataTableField("WF_NodeReturn"));


		// 工具栏。
		NodeToolbars tools = new NodeToolbars();
		tools.RetrieveInSQL(NodeToolbarAttr.FK_Node, sqlin);
		ds.Tables.add(tools.ToDataTableField("WF_NodeToolbar"));


		// 节点与部门。
		NodeDepts ndepts = new NodeDepts();
		ndepts.RetrieveInSQL(NodeDeptAttr.FK_Node, sqlin);
		ds.Tables.add(ndepts.ToDataTableField("WF_NodeDept"));


		// 节点与角色权限。
		NodeStations nss = new NodeStations();
		nss.RetrieveInSQL(NodeStationAttr.FK_Node, sqlin);
		ds.Tables.add(nss.ToDataTableField("WF_NodeStation"));

		// 节点与人员。
		NodeEmps nes = new NodeEmps();
		nes.RetrieveInSQL(NodeEmpAttr.FK_Node, sqlin);
		ds.Tables.add(nes.ToDataTableField("WF_NodeEmp"));

		//自动抄送规则
		CCRoles ccRoles = new CCRoles();
		ccRoles.RetrieveInSQL(CCRoleAttr.NodeID, sqlin);
		ds.Tables.add(ccRoles.ToDataTableField("WF_CCRole"));

		//子流程。
		SubFlows fls = new SubFlows();
		fls.RetrieveInSQL(SubFlowAttr.FK_Node, sqlin);
		ds.Tables.add(fls.ToDataTableField("WF_NodeSubFlow"));

		//表单信息，包含从表.
		sql = "SELECT No FROM Sys_MapData WHERE " + Glo.MapDataLikeKey(this.getNo(), "No");
		MapDatas mds = new MapDatas();
		mds.RetrieveInSQL(MapDataAttr.No, sql);
		DataTable dt = mds.ToDataTableField("Sys_MapData");
		dt.Columns.Add("HtmlTemplateFile");
		for (DataRow dr : dt.Rows)
		{
			if (Integer.parseInt(dr.getValue("FrmType").toString()) == FrmType.Develop.getValue())
			{
				String htmlCode = DBAccess.GetBigTextFromDB("Sys_MapData", "No", dr.getValue("No").toString(), "HtmlTemplateFile");
				dr.setValue("HtmlTemplateFile", htmlCode);
			}
		}

		ds.Tables.add(dt);

		// Sys_MapDtl
		sql = "SELECT No FROM Sys_MapDtl WHERE " + Glo.MapDataLikeKey(this.getNo(), "FK_MapData");
		MapDtls mdtls = new MapDtls();
		mdtls.RetrieveInSQL(sql);
		ds.Tables.add(mdtls.ToDataTableField("Sys_MapDtl"));


		String frmIDs= "";
		for(MapDtl mapDtl :mdtls.ToJavaList()){
			frmIDs+="'"+mapDtl.getNo()+"',";
		}
		if(DataType.IsNullOrEmpty(frmIDs) == false)
			frmIDs = frmIDs.substring(0,frmIDs.length()-1);

		String andSQL = "";
		if (DataType.IsNullOrEmpty(frmIDs) == false)
			andSQL = " OR FK_MapData IN(" + frmIDs + ")";

		// Sys_MapAttr.
		sql = "SELECT MyPK FROM Sys_MapAttr WHERE " + Glo.MapDataLikeKey(this.getNo(), "FK_MapData")+andSQL;
		sql += " UNION "; //增加多附件的扩展列.
		sql += "SELECT MyPK FROM Sys_MapAttr WHERE FK_MapData IN ( SELECT MyPK FROM Sys_FrmAttachment WHERE FK_Node=0 AND " + Glo.MapDataLikeKey(this.getNo(), "FK_MapData") + " ) ";

		MapAttrs attrs = new MapAttrs();
		attrs.RetrieveInSQL(MapAttrAttr.MyPK, sql);
		ds.Tables.add(attrs.ToDataTableField("Sys_MapAttr"));


		// Sys_EnumMain
		sql = "SELECT No FROM Sys_EnumMain WHERE No IN (SELECT UIBindKey from Sys_MapAttr WHERE " + Glo.MapDataLikeKey(this.getNo(), "FK_MapData")+andSQL+")";
		SysEnumMains ses = new SysEnumMains();
		ses.RetrieveInSQL(SysEnumMainAttr.No, sql);
		ds.Tables.add(ses.ToDataTableField("Sys_EnumMain"));


		// Sys_Enum
		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.SAAS)
		{
			sql = "SELECT MyPK FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey IN ( SELECT No FROM Sys_EnumMain WHERE No IN (SELECT UIBindKey from Sys_MapAttr WHERE " + Glo.MapDataLikeKey(this.getNo(), "FK_MapData")+andSQL + " ) )";
		}
		else
		{
			sql = "SELECT MyPK FROM " + bp.sys.base.Glo.SysEnum() + " WHERE RefPK IN ( SELECT No FROM Sys_EnumMain WHERE No IN (SELECT UIBindKey from Sys_MapAttr WHERE " + Glo.MapDataLikeKey(this.getNo(), "FK_MapData")+andSQL+ " ) )";
		}
		SysEnums sesDtl = new SysEnums();
		sesDtl.RetrieveInSQL("MyPK", sql);
		ds.Tables.add(sesDtl.ToDataTableField("Sys_Enum"));




		// Sys_MapExt
		sql = "SELECT MyPK FROM Sys_MapExt WHERE  " + Glo.MapDataLikeKey(this.getNo(), "FK_MapData")+andSQL;
		MapExts mexts = new MapExts();
		mexts.RetrieveInSQL(sql);
		DataTable mapDt = mexts.ToDataTableField("Sys_MapExt");
		if (mexts.getIsExits("ExtType", "HtmlText") == true)
		{
			mapDt.Columns.Add("HtmlText", String.class);
			for (DataRow dr : mapDt.Rows)
			{
				if (dr.getValue("ExtType").equals("HtmlText") == true)
				{
					String text = DBAccess.GetBigTextFromDB("Sys_MapExt", "MyPK", dr.getValue("MyPK").toString(), "HtmlText");
					dr.setValue("HtmlText", text);
				}
			}

		}
		ds.Tables.add(mapDt);

		// Sys_GroupField
		if (DataType.IsNullOrEmpty(frmIDs) == false)
			andSQL = " OR FrmID IN(" + frmIDs + ")";
		sql = "SELECT OID FROM Sys_GroupField WHERE   " + Glo.MapDataLikeKey(this.getNo(), "FrmID")+andSQL; // +" " + Glo.MapDataLikeKey(this.No, "EnName");
		GroupFields gfs = new GroupFields();
		gfs.RetrieveInSQL(sql);
		ds.Tables.add(gfs.ToDataTableField("Sys_GroupField"));


		// Sys_MapFrame
		sql = "SELECT MyPK FROM Sys_MapFrame WHERE" + Glo.MapDataLikeKey(this.getNo(), "FK_MapData");
		MapFrames mfs = new MapFrames();
		mfs.RetrieveInSQL("MyPK", sql);
		ds.Tables.add(mfs.ToDataTableField("Sys_MapFrame"));


		// Sys_FrmRB.
		sql = "SELECT MyPK FROM Sys_FrmRB WHERE " + Glo.MapDataLikeKey(this.getNo(), "FK_MapData");
		FrmRBs frmRBs = new FrmRBs();
		frmRBs.RetrieveInSQL(sql);
		ds.Tables.add(frmRBs.ToDataTableField("Sys_FrmRB"));

		// Sys_FrmImgAth.
		sql = "SELECT MyPK FROM Sys_FrmImgAth WHERE " + Glo.MapDataLikeKey(this.getNo(), "FK_MapData");
		FrmImgAths frmIs = new FrmImgAths();
		frmIs.RetrieveInSQL(sql);
		ds.Tables.add(frmIs.ToDataTableField("Sys_FrmImgAth"));

		// Sys_FrmImg.
		sql = "SELECT MyPK FROM Sys_FrmImg WHERE " + Glo.MapDataLikeKey(this.getNo(), "FK_MapData");
		FrmImgs frmImgs = new FrmImgs();
		frmImgs.RetrieveInSQL(sql);
		ds.Tables.add(frmImgs.ToDataTableField("Sys_FrmImg"));


		// Sys_FrmAttachment.
		sql = "SELECT MyPK FROM Sys_FrmAttachment WHERE FK_Node=0 AND " + Glo.MapDataLikeKey(this.getNo(), "FK_MapData");
		FrmAttachments frmaths = new FrmAttachments();
		frmaths.RetrieveInSQL(sql);
		ds.Tables.add(frmaths.ToDataTableField("Sys_FrmAttachment"));

		// Sys_FrmEvent.
		sql = "SELECT MyPK FROM Sys_FrmEvent WHERE RefFlowNo='" + this.getNo() + "'";
		FrmEvents frmevens = new FrmEvents();
		frmevens.RetrieveInSQL(sql);
		ds.Tables.add(frmevens.ToDataTableField("Sys_FrmEvent"));
		return ds;
	}
	///#endregion 产生数据模板。


	///#region 其他公用方法1
	/**
	 重新设置Rpt表
	 */
	public final void CheckRptOfReset() throws Exception {
		String fk_mapData = "ND" + Integer.parseInt(this.getNo()) + "Rpt";
		String sql = "DELETE FROM Sys_MapAttr WHERE FK_MapData='" + fk_mapData + "'";
		DBAccess.RunSQL(sql);

		sql = "DELETE FROM Sys_MapData WHERE No='" + fk_mapData + "'";
		DBAccess.RunSQL(sql);
		this.DoCheck_CheckRpt(this.getHisNodes());
	}

	/**
	 检查与修复报表数据

	 @param nds
	 @param dt
	 */
	private String CheckRptData(Nodes nds, DataTable dt) throws Exception {
		GERpt rpt = new GERpt("ND" + Integer.parseInt(this.getNo()) + "Rpt");
		String err = "";
		for (DataRow dr : dt.Rows)
		{
			rpt.ResetDefaultVal(null, null, 0);
			int oid = Integer.parseInt(dr.getValue(0).toString());
			rpt.SetValByKey("OID", oid);
			Work startWork = null;
			Work endWK = null;
			String flowEmps = "@";
			for (Node nd : nds.ToJavaList())
			{
				try
				{
					Work wk = nd.getHisWork();
					wk.setOID(oid);
					if (wk.RetrieveFromDBSources() == 0)
					{
						continue;
					}

					rpt.Copy(wk);
					if (nd.getNodeID() == Integer.parseInt(this.getNo() + "01"))
					{
						startWork = wk;
					}

					endWK = wk;
				}
				catch (RuntimeException ex)
				{
					err += ex.getMessage();
				}
			}

			if (startWork == null || endWK == null)
			{
				continue;
			}

			rpt.SetValByKey("OID", oid);
			rpt.setNY(startWork.GetValStrByKey("RDT").substring(0, 7));
			rpt.setDeptNo(startWork.GetValStrByKey("FK_Dept"));
			if (DataType.IsNullOrEmpty(rpt.getDeptNo()))
			{
				String fk_dept = "";

				if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
				{
					fk_dept = DBAccess.RunSQLReturnString("SELECT FK_Dept FROM Port_Emp WHERE " + bp.sys.base.Glo.getUserNoWhitOutAS() + "='" + startWork.getRec() + "' AND OrgNo='" + WebUser.getOrgNo() + "'");
				}
				else
				{
					fk_dept = DBAccess.RunSQLReturnString("SELECT FK_Dept FROM Port_Emp WHERE " + bp.sys.base.Glo.getUserNoWhitOutAS() + "='" + startWork.getRec() + "'");
				}


				rpt.setDeptNo(fk_dept);

				startWork.SetValByKey("FK_Dept", fk_dept);
				startWork.Update();
			}
			rpt.setTitle(startWork.GetValStrByKey("Title"));

			String wfState = DBAccess.RunSQLReturnStringIsNull("SELECT WFState FROM WF_GenerWorkFlow WHERE WorkID=" + oid, "1");
			rpt.setWFState(WFState.forValue(Integer.parseInt(wfState)));
			rpt.setFlowStarter(startWork.getRec());
			rpt.setFlowStartRDT(DBAccess.RunSQLReturnStringIsNull("SELECT WFState FROM WF_GenerWorkFlow WHERE WorkID=" + oid, "1"));
			rpt.setFID(startWork.GetValIntByKey("FID"));
			rpt.setFlowEmps(flowEmps);
			rpt.setFlowEnder(endWK.getRec());
			//rpt.FlowEnderRDT = endWK.RDT;
			rpt.setFlowEndNode(endWK.getNodeID());

			//修复标题字段。
			WorkNode wn = new WorkNode(startWork, this.getHisStartNode());
			rpt.setTitle(bp.wf.WorkFlowBuessRole.GenerTitle(this, startWork));
			//try
			//{
			//    TimeSpan ts = endWK.RDT_DateTime - startWork.RDT_DateTime;
			//    rpt.FlowDaySpan =  ts.Days;
			//}
			//catch
			//{
			//}
			rpt.InsertAsOID(rpt.getOID());
		} // 结束循环。
		return err;
	}
	/**
	 生成明细报表信息

	 @param nds
	 */
	private void CheckRptDtl(Nodes nds) throws Exception {
		MapDtls dtlsDtl = new MapDtls();
		dtlsDtl.Retrieve(MapDtlAttr.FK_MapData, "ND" + Integer.parseInt(this.getNo()) + "Rpt", null);
		for (MapDtl dtl : dtlsDtl.ToJavaList())
		{
			dtl.Delete();
		}

		//  dtlsDtl.Delete(MapDtlAttr.FK_MapData, "ND" + int.Parse(this.getNo()) + "Rpt");
		for (Node nd : nds.ToJavaList())
		{
			if (nd.getItIsEndNode() == false)
			{
				continue;
			}

			// 取出来从表.
			MapDtls dtls = null;
			try {
				dtls = new MapDtls("ND" + nd.getNodeID());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (dtls.isEmpty())
			{
				continue;
			}

			String rpt = "ND" + Integer.parseInt(this.getNo()) + "Rpt";
			int i = 0;
			for (MapDtl dtl : dtls.ToJavaList())
			{
				i++;
				String rptDtlNo = "ND" + Integer.parseInt(this.getNo()) + "RptDtl" + String.valueOf(i);
				MapDtl rtpDtl = new MapDtl();
				rtpDtl.setNo(rptDtlNo);
				if (rtpDtl.RetrieveFromDBSources() == 0)
				{
					rtpDtl.Copy(dtl);
					rtpDtl.setNo(rptDtlNo);
					rtpDtl.setFrmID(rpt);
					rtpDtl.setPTable(rptDtlNo);
					rtpDtl.Insert();
				}

				MapAttrs attrsRptDtl = new MapAttrs(rptDtlNo);
				MapAttrs attrs = new MapAttrs(dtl.getNo());
				for (MapAttr attr : attrs.ToJavaList())
				{
					if (attrsRptDtl.contains(MapAttrAttr.KeyOfEn, attr.getKeyOfEn()) == true)
					{
						continue;
					}

					MapAttr attrN = new MapAttr();
					attrN.Copy(attr);
					attrN.setFrmID(rptDtlNo);
					switch (attr.getKeyOfEn())
					{
						case "FK_NY":
							attrN.setUIVisible(true);
							attrN.setIdx( 100);
							attrN.setUIWidth(60);
							break;
						case "RDT":
							attrN.setUIVisible(true);
							attrN.setIdx( 100);
							attrN.setUIWidth(60);
							break;
						case "Rec":
							attrN.setUIVisible(true);
							attrN.setIdx( 100);
							attrN.setUIWidth(60);
							break;
						default:
							break;
					}

					attrN.Save();
				}

				GEDtl geDtl = new GEDtl(rptDtlNo);
				geDtl.CheckPhysicsTable();
			}
		}
	}

	/**
	 检查数据报表.

	 @param nds
	 */
	private String DoCheck_CheckRpt(Nodes nds) throws Exception {
		String msg = "";
		String fk_mapData = "ND" + Integer.parseInt(this.getNo()) + "Rpt";
		String flowId = String.valueOf(Integer.parseInt(this.getNo()));

		//生成该节点的 nds 比如  "'ND101','ND102','ND103'"
		String ndsstrs = "";
		for (bp.wf.Node nd : nds.ToJavaList())
		{
			ndsstrs += "'ND" + nd.getNodeID() + "',";
		}
		ndsstrs = ndsstrs.substring(0, ndsstrs.length() - 1);


		///#region 插入字段。
		String sql = "SELECT distinct KeyOfEn FROM Sys_MapAttr WHERE FK_MapData IN (" + ndsstrs + ")";
		if (SystemConfig.getAppCenterDBType() == DBType.MySQL)
		{
			sql = "SELECT A.* FROM (" + sql + ") AS A ";
			String sql3 = "DELETE FROM Sys_MapAttr WHERE KeyOfEn NOT IN (" + sql + ") AND FK_MapData='" + fk_mapData + "' ";
			DBAccess.RunSQL(sql3); // 删除不存在的字段.
		}
		else
		{
			String sql2 = "DELETE FROM Sys_MapAttr WHERE KeyOfEn NOT IN (" + sql + ") AND FK_MapData='" + fk_mapData + "' ";
			DBAccess.RunSQL(sql2); // 删除不存在的字段.
		}

		//所有节点表单字段的合集.
		sql = "SELECT MyPK, KeyOfEn,DefVal,Name,LGType,MyDataType,UIContralType,UIBindKey,FK_MapData FROM Sys_MapAttr WHERE FK_MapData IN (" + ndsstrs + ")";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		//求已经存在的字段集合。
		sql = "SELECT KeyOfEn FROM Sys_MapAttr WHERE FK_MapData='ND" + flowId + "Rpt'";
		DataTable dtExits = DBAccess.RunSQLReturnTable(sql);
		String pks = "@";
		for (DataRow dr : dtExits.Rows)
		{
			pks += dr.getValue(0) + "@";
		}

		//查询出来已经有的映射.
		MapAttrs attrs = new MapAttrs(fk_mapData);

		//遍历 - 所有节点表单字段的合集
		for (DataRow dr : dt.Rows)
		{
			//如果是枚举，外键字段，判断是否判定了对应的枚举和外键
			int lgType = Integer.parseInt(dr.getValue("LGType").toString());
			int contralType = Integer.parseInt(dr.getValue("UIContralType").toString());

			if ((lgType == 2 && contralType == 1) || (lgType == 0 && contralType == 1 && Integer.parseInt(dr.getValue("MyDataType").toString()) == 1))
			{
				if (dr.getValue("UIBindKey") == null || DataType.IsNullOrEmpty(dr.getValue("UIBindKey").toString()) == true)
				{
					msg += "表单" + dr.getValue("FK_MapData").toString() + "中,外键/外部数据源字段:" + dr.getValue("Name").toString() + "(" + dr.getValue("KeyOfEn").toString() + ");";
				}
			}
			if (lgType == 1 && (dr.getValue("UIBindKey") == null || DataType.IsNullOrEmpty(dr.getValue("UIBindKey").toString()) == true))
			{
				msg += "表单" + dr.getValue("FK_MapData").toString() + "中,枚举字段:" + dr.getValue("Name").toString() + "(" + dr.getValue("KeyOfEn").toString() + ");";
			}

			if (pks.contains("@" + dr.getValue("KeyOfEn").toString() + "@") == true)
			{
				continue;
			}

			String mypk = dr.getValue("MyPK").toString();

			pks += dr.getValue("KeyOfEn").toString() + "@";

			//找到这个属性.
			MapAttr ma = new MapAttr(mypk);
			ma.setMyPK("ND" + flowId + "Rpt_" + ma.getKeyOfEn());
			ma.setFrmID("ND" + flowId + "Rpt");
			ma.setUIIsEnable(false);

			if (ma.getDefValReal().contains("@"))
			{
				/*如果是一个有变量的参数.*/
				ma.setDefVal("");
			}

			//如果包含他,就说已经存在.
			if (attrs.contains("MyPK", ma.getMyPK()) == true)
			{
				continue;
			}
			// 如果不存在.
			ma.Insert();
		}

		// 创建mapData.
		MapData md = new MapData();
		md.setNo("ND" + flowId + "Rpt");
		if (md.RetrieveFromDBSources() == 0)
		{
			md.setName(this.getName());
			md.setPTable(this.getPTable());
			md.Insert();
		}
		else
		{
			if (md.getName().equals(this.getName()) == false || md.getPTable().equals(this.getPTable()) == false)
			{
				md.setName(this.getName());
				md.setPTable(this.getPTable());
				md.Update();
			}

		}

		///#endregion 插入字段。


		///#region 补充上流程字段到NDxxxRpt.
		int groupID = 0;
		for (MapAttr attr : attrs.ToJavaList())
		{
			switch (attr.getKeyOfEn())
			{
				case GERptAttr.FK_Dept:
					attr.setUIContralType(UIContralType.TB);
					attr.setLGType(FieldTypeS.Normal);
					attr.setUIVisible(true);
					attr.setGroupID(groupID); // gfs[0].GetValIntByKey("OID");
					attr.setUIIsEnable(false);
					attr.setDefVal("");
					attr.setMaxLen(100);
					attr.Update();
					break;

				case "FK_NY":
					//  attr.setUIBindKey("BP.Pub.NYs";
					attr.setUIContralType(UIContralType.TB);
					attr.setLGType(FieldTypeS.Normal);
					attr.setUIVisible(true);
					attr.setUIIsEnable(false);
					attr.setGroupID(groupID);
					attr.Update();
					break;
				case "FK_Emp":
					break;
				default:
					break;
			}
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.Title) == false)
		{
			/* 标题 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.Title); // "FlowEmps";
			attr.setName("标题");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(true);
			attr.setMinLen(0);
			attr.setMaxLen(400);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.OID) == false)
		{
			/* WorkID */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setKeyOfEn("OID");
			attr.setName("WorkID");
			attr.setMyDataType(DataType.AppInt);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setDefVal("0");
			attr.setHisEditType(EditType.Readonly);
			attr.Insert();
		}


		if (attrs.contains(md.getNo() + "_" + GERptAttr.FID) == false)
		{
			/* WorkID */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setKeyOfEn("FID");
			attr.setName("FID");
			attr.setMyDataType(DataType.AppInt);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setDefVal("0");
			attr.setHisEditType(EditType.Readonly);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.WFState) == false)
		{
			/* 流程状态 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.WFState);
			attr.setName("流程状态");
			attr.setMyDataType(DataType.AppInt);
			attr.setUIBindKey(GERptAttr.WFState);
			attr.setUIContralType(UIContralType.DDL);
			attr.setLGType(FieldTypeS.Enum);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(1000);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.WFSta) == false)
		{
			/* 流程状态Ext */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.WFSta);
			attr.setName("状态");
			attr.setMyDataType(DataType.AppInt);
			attr.setUIBindKey(GERptAttr.WFSta);
			attr.setUIContralType(UIContralType.DDL);
			attr.setLGType(FieldTypeS.Enum);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(1000);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FlowEmps) == false)
		{
			/* 参与人 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.FlowEmps); // "FlowEmps";
			attr.setName("参与人");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(true);
			attr.setMinLen(0);
			attr.setMaxLen(1000);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FlowStarter) == false)
		{
			/* 发起人 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.FlowStarter);
			attr.setName("发起人");
			attr.setMyDataType(DataType.AppString);

			//attr.setUIBindKey("bp.port.Emps";
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FlowStartRDT) == false)
		{
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.FlowStartRDT); // "FlowStartRDT";
			attr.setName("发起时间");
			attr.setMyDataType(DataType.AppDateTime);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setIdx( -101);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FlowEnder) == false)
		{
			/* 发起人 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.FlowEnder);
			attr.setName("结束人");
			attr.setMyDataType(DataType.AppString);
			// attr.setUIBindKey("bp.port.Emps";
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FlowEnderRDT) == false)
		{
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.FlowEnderRDT); // "FlowStartRDT";
			attr.setName("结束时间");
			attr.setMyDataType(DataType.AppDateTime);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setIdx( -101);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FlowEndNode) == false)
		{
			/* 结束节点 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.FlowEndNode);
			attr.setName("结束节点");
			attr.setMyDataType(DataType.AppInt);
			attr.setDefVal("0");
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setHisEditType(EditType.UnDel);
			attr.setIdx( -101);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FlowDaySpan) == false)
		{
			/* FlowDaySpan */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.FlowDaySpan); // "FlowStartRDT";
			attr.setName("流程时长(天)");
			attr.setMyDataType(DataType.AppFloat);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(true);
			attr.setUIIsLine (false);
			attr.setIdx( -101);
			attr.setDefVal("0");
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.PFlowNo) == false)
		{
			/* 父流程 流程编号 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.PFlowNo);
			attr.setName("父流程编号"); //  父流程流程编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(true);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.PNodeID) == false)
		{
			/* 父流程WorkID */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.PNodeID);
			attr.setName("父流程启动的节点");
			attr.setMyDataType(DataType.AppInt);
			attr.setDefVal("0");
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setHisEditType(EditType.UnDel);
			attr.setIdx( -101);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.PWorkID) == false)
		{
			/* 父流程WorkID */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.PWorkID);
			attr.setName("父流程WorkID");
			attr.setMyDataType(DataType.AppInt);
			attr.setDefVal("0");
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setHisEditType(EditType.UnDel);
			attr.setIdx( -101);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.PEmp) == false)
		{
			/* 调起子流程的人员 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.PEmp);
			attr.setName("调起子流程的人员");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(true);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.BillNo) == false)
		{
			/* 父流程 流程编号 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.BillNo);
			attr.setName("单据编号"); //  单据编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.Insert();
		}




		if (attrs.contains(md.getNo() + "_" + GERptAttr.AtPara) == false)
		{
			/* 父流程 流程编号 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.AtPara);
			attr.setName("参数"); // 单据编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setMinLen(0);
			attr.setMaxLen(4000);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.GUID) == false)
		{
			/* 父流程 流程编号 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.GUID);
			attr.setName("GUID"); // 单据编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setMinLen(0);
			attr.setMaxLen(32);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.PrjNo) == false)
		{
			/* 项目编号 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.PrjNo);
			attr.setName("项目编号"); //  项目编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.Insert();
		}
		if (attrs.contains(md.getNo() + "_" + GERptAttr.PrjName) == false)
		{
			/* 项目名称 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.PrjName);
			attr.setName("项目名称"); //  项目名称
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FK_DeptName) == false)
		{
			MapAttr attr = new MapAttr();
			attr.SetValByKey(MapAttrAttr.FK_MapData, md.getNo());
			attr.setEditType(EditType.UnDel);
			attr.SetValByKey(MapAttrAttr.KeyOfEn, "FK_DeptName");
			attr.SetValByKey(MapAttrAttr.Name, "操作员部门名称");
			attr.SetValByKey(MapAttrAttr.MyDataType, DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.SetValByKey(MapAttrAttr.UIVisible, false);
			attr.SetValByKey(MapAttrAttr.UIIsEnable, false);
			attr.setLGType(FieldTypeS.Normal);
			attr.SetValByKey(MapAttrAttr.MinLen, 0);
			attr.SetValByKey(MapAttrAttr.MaxLen, 50);
			attr.Insert();
		}

		///#endregion 补充上流程字段。


		///#region 为流程字段设置分组。
		try
		{
			String flowInfo = "流程信息";
			GroupField flowGF = new GroupField();
			int num = flowGF.Retrieve(GroupFieldAttr.FrmID, fk_mapData, GroupFieldAttr.Lab, "流程信息");
			if (num == 0)
			{
				flowGF = new GroupField();
				flowGF.setLab(flowInfo);
				flowGF.setFrmID(fk_mapData);
				flowGF.setIdx(-1);
				flowGF.Insert();
			}
			sql = "UPDATE Sys_MapAttr SET GroupID='" + flowGF.getOID() + "' WHERE  FK_MapData='" + fk_mapData + "'  AND KeyOfEn IN('" + GERptAttr.PFlowNo + "','" + GERptAttr.PWorkID + "','" + GERptAttr.FK_Dept + "','" + GERptAttr.FK_NY + "','" + GERptAttr.FlowDaySpan + "','" + GERptAttr.FlowEmps + "','" + GERptAttr.FlowEnder + "','" + GERptAttr.FlowEnderRDT + "','" + GERptAttr.FlowEndNode + "','" + GERptAttr.FlowStarter + "','" + GERptAttr.FlowStartRDT + "','" + GERptAttr.WFState + "')";
			DBAccess.RunSQL(sql);
		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteError(ex.getMessage());
		}

		///#endregion 为流程字段设置分组


		///#region 尾后处理.
		GERpt gerpt = this.getHisGERpt();
		gerpt.CheckPhysicsTable(); //让报表重新生成.

		if (SystemConfig.getAppCenterDBType() == DBType.HGDB || DBAccess.getAppCenterDBType() == DBType.UX)
		{
			DBAccess.RunSQL("DELETE FROM Sys_GroupField WHERE FrmID='" + fk_mapData + "' AND  OID NOT IN (SELECT GroupID FROM Sys_MapAttr WHERE FK_MapData = '" + fk_mapData + "')");
		}
		else
		{
			DBAccess.RunSQL("DELETE FROM Sys_GroupField WHERE FrmID='" + fk_mapData + "' AND  OID NOT IN (SELECT GroupID FROM Sys_MapAttr WHERE FK_MapData = '" + fk_mapData + "')");
		}


		DBAccess.RunSQL("UPDATE Sys_MapAttr SET Name='活动时间' WHERE FK_MapData='ND" + flowId + "Rpt' AND KeyOfEn='CDT'");
		DBAccess.RunSQL("UPDATE Sys_MapAttr SET Name='参与者' WHERE FK_MapData='ND" + flowId + "Rpt' AND KeyOfEn='Emps'");

		///#endregion 尾后处理.
		return msg;
	}

	///#endregion 其他公用方法1


	///#region 执行流程事件.
	private bp.wf.FlowEventBase _FDEventEntity = null;
	/**
	 节点实体类，没有就返回为空.
	 */
	public final bp.wf.FlowEventBase getFEventEntity() throws Exception {
		if (_FDEventEntity == null && DataType.IsNullOrEmpty(this.getFlowMark()) == false && DataType.IsNullOrEmpty(this.getFlowEventEntity()) == false)
		{
			_FDEventEntity = bp.wf.Glo.GetFlowEventEntityByEnName(this.getFlowEventEntity());
		}
		return _FDEventEntity;
	}

	///#endregion 执行流程事件.


	///#region 基本属性
	/**
	 是否是MD5加密流程
	 */
	public final boolean getItIsMD5() {
		return this.GetValBooleanByKey(FlowAttr.IsMD5);
	}
	public final void setItIsMD5(boolean value)  {
		this.SetValByKey(FlowAttr.IsMD5, value);
	}
	/**
	 是否有单据
	 */
	public final int getNumOfBill() {
		return this.GetValIntByKey(FlowAttr.NumOfBill);
	}
	public final void setNumOfBill(int value)  {
		this.SetValByKey(FlowAttr.NumOfBill, value);
	}
	/**
	 标题生成规则
	 */
	public final String getTitleRole() throws Exception {
		String str = this.GetValStringByKey(FlowAttr.TitleRole);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			return "@WebUser.FK_DeptName - @WebUser.Name 在 @RDT 发起";
		}
		return str;
	}
	public final void setTitleRole(String value)  {
		this.SetValByKey(FlowAttr.TitleRole, value);
	}
	/**
	 生成title的位置.
	 */
	public final String getTitleRoleNodes() throws Exception {
		String str = this.GetValStringByKey(FlowAttr.TitleRoleNodes);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			return Integer.parseInt(this.getNo()) + "01";
		}
		return str;
	}
	/**
	 明细表
	 */
	public final int getNumOfDtl() {
		return this.GetValIntByKey(FlowAttr.NumOfDtl);
	}
	public final void setNumOfDtl(int value)  {
		this.SetValByKey(FlowAttr.NumOfDtl, value);
	}
	public final int getStartNodeID() {
		return Integer.parseInt(this.getNo() + "01");
	}
	/**
	 add 2013-01-01.
	 业务主表(默认为NDxxRpt)
	 */
	public final String getPTable() throws Exception {
		String s = this.GetValStringByKey(FlowAttr.PTable);
		if (DataType.IsNullOrEmpty(s))
		{
			s = "ND" + Integer.parseInt(this.getNo()) + "Rpt";
		}
		return s.trim();
	}
	public final void setPTable(String value)  {
		this.SetValByKey(FlowAttr.PTable, value);
	}
	/**
	 是否启用？
	 */
	public final GuestFlowRole getGuestFlowRole() {
		return GuestFlowRole.forValue(this.GetValIntByKey(FlowAttr.GuestFlowRole));
	}
	public final void setGuestFlowRole(GuestFlowRole value)  {
		this.SetValByKey(FlowAttr.GuestFlowRole, value);
	}
	/**
	 是否可以独立启动
	 */
	public final boolean getItIsCanStart() {
		return this.GetValBooleanByKey(FlowAttr.IsCanStart);
	}
	public final void setItIsCanStart(boolean value)  {
		this.SetValByKey(FlowAttr.IsCanStart, value);
	}
	/**
	 是否可以批量发起
	 */
	public final boolean getItIsBatchStart() {
		return this.GetValBooleanByKey(FlowAttr.IsBatchStart);
	}
	public final void setItIsBatchStart(boolean value)  {
		this.SetValByKey(FlowAttr.IsBatchStart, value);
	}
	/**
	 是否自动计算未来的处理人
	 */
	public final boolean getItIsFullSA() {
		return this.GetValBooleanByKey(FlowAttr.IsFullSA);
	}
	public final void setItIsFullSA(boolean value)  {
		this.SetValByKey(FlowAttr.IsFullSA, value);
	}
	/**
	 批量发起字段
	 */
	public final String getBatchStartFields() {
		return this.GetValStringByKey(FlowAttr.BatchStartFields);
	}
	public final void setBatchStartFields(String value)  {
		this.SetValByKey(FlowAttr.BatchStartFields, value);
	}
	/**
	 单据格式
	 */
	public final String getBillNoFormat() {

		return this.GetValStringByKey(FlowAttr.BillNoFormat);
	}
	public final void setBillNoFormat(String value)  {
		this.SetValByKey(FlowAttr.BillNoFormat, value);
	}
	/**
	 流程类别
	 */
	public final String getFlowSortNo() {
		return this.GetValStringByKey(FlowAttr.FK_FlowSort);
	}
	public final void setFlowSortNo(String value)  {
		if (DataType.IsNullOrEmpty(value) ==true)
		{
			if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
			{
				String sql = "SELECT No FROM WF_FlowSort WHERE ParentNo='0'";
				String sortNo = DBAccess.RunSQLReturnStringIsNull(sql, "100");
				this.SetValByKey(FlowAttr.FK_FlowSort, sortNo);
				return;
			}
			else
			{
				this.SetValByKey(FlowAttr.FK_FlowSort, WebUser.getOrgNo());
				return;
			}
		}
		this.SetValByKey(FlowAttr.FK_FlowSort, value);
	}
	/**
	 系统类别
	 */
	public final String getSysType() {
		return this.GetValStringByKey(FlowAttr.SysType);
	}
	public final void setSysType(String value)  {
		this.SetValByKey(FlowAttr.SysType, value);
	}
	/**
	 参数
	 */
	public final String getParas() {
		return this.GetValStringByKey(FlowAttr.Paras);
	}
	public final void setParas(String value)  {
		this.SetValByKey(FlowAttr.Paras, value);
	}
	/**
	 版本号
	 */
	public final String getVer() {
		return this.GetValStringByKey(FlowAttr.Ver);
	}
	public final void setVer(String value)  {
		this.SetValByKey(FlowAttr.Ver, value);
	}

	///#endregion


	///#region 计算属性
	/**
	 是否启用数据模版？
	 */
	public final boolean getItIsDBTemplate() {
		return this.GetValBooleanByKey(FlowAttr.IsDBTemplate);
	}
	public final String getNote() throws Exception {
		String s = this.GetValStringByKey("Note");
		if (s.length() == 0)
		{
			return "无";
		}
		return s;
	}
	public final String getNoteHtml() throws Exception {
		if (Objects.equals(this.getNote(), "无") || Objects.equals(this.getNote(), ""))
		{
			return "流程设计人员没有编写此流程的帮助信息，请打开设计器-》打开此流程-》设计画布上点击右键-》流程属性-》填写流程帮助信息。";
		}
		else
		{
			return this.getNote();
		}
	}

	///#endregion


	///#region 扩展属性
	/**
	 是否启用子流程运行结束后，主流程自动运行到下一节点
	 */
	public final boolean getItIsToParentNextNode() {
		return this.GetValBooleanByKey(NodeAttr.IsToParentNextNode);
	}
	public final void setItIsToParentNextNode(boolean value)  {
		this.SetValByKey(NodeAttr.IsToParentNextNode, value);
	}

	public final int getTrackOrderBy() {
		return this.GetValIntByKey(FlowAttr.TrackOrderBy);
	}
	/**
	 节点
	 */
	private Nodes _HisNodes = null;
	/**
	 他的节点集合.
	 */
	public final Nodes getHisNodes() throws Exception {
		_HisNodes = new Nodes(this.getNo());
		return _HisNodes;
	}
	public final void setHisNodes(Nodes value)
	{
		_HisNodes = value;
	}
	/**
	 流程完成条件
	 */
	public final Conds getCondsOfFlowComplete() throws Exception {
		Conds ens = (Conds) this.GetEntitiesAttrFromAutoNumCache(new Conds(), CondAttr.FK_Flow, this.getNo(), CondAttr.CondType, CondType.Flow.getValue(), CondAttr.Idx);
		return ens instanceof Conds ? (Conds)ens : null;
	}
	/**
	 事件:
	 */
	public final FrmEvents getFrmEvents() throws Exception {
		FrmEvents ens = (FrmEvents) this.GetEntitiesAttrFromAutoNumCache(new FrmEvents(), FrmEventAttr.FK_Flow, this.getNo(), null, null, null);
		if(ens.size() == 0){
			ens = (FrmEvents)this.GetEntitiesAttrFromAutoNumCache(new FrmEvents(),
					FrmEventAttr.RefPKVal, this.getNo());
		}
		return ens instanceof FrmEvents ? (FrmEvents)ens : null;
	}
	/**
	 他的 Start 节点
	 */
	public final Node getHisStartNode() throws Exception {

		for (Node nd : this.getHisNodes().ToJavaList())
		{
			if (nd.getItIsStartNode())
			{
				return nd;
			}
		}
		throw new RuntimeException("@没有找到他的开始节点,工作流程[" + this.getName() + "]定义错误.");
	}
	/**
	 他的事务类别
	 */
	public final FlowSort getHisFlowSort() throws Exception {
		return new FlowSort(this.getFlowSortNo());
	}
	/**
	 flow data 数据
	 */
	public final bp.wf.GERpt getHisGERpt() throws Exception {
		try
		{
			bp.wf.GERpt wk = new bp.wf.GERpt("ND" + Integer.parseInt(this.getNo()) + "Rpt");
			return wk;
		}
		catch (java.lang.Exception e)
		{
			this.DoCheck();
			bp.wf.GERpt wk1 = new bp.wf.GERpt("ND" + Integer.parseInt(this.getNo()) + "Rpt");
			return wk1;
		}
	}

	public final boolean getItIsTruckEnable() {
		return this.GetValBooleanByKey(FlowAttr.IsTruckEnable);
	}

	public final boolean getItIsTimeBaseEnable() {
		return this.GetValBooleanByKey(FlowAttr.IsTimeBaseEnable);
	}

	public final boolean getItIsTableEnable() {
		return this.GetValBooleanByKey(FlowAttr.IsTableEnable);
	}


	///#endregion


	///#region 构造方法

	/**
	 流程
	 */
	public Flow() {
		this.setNo("");
	}
	/**
	 流程

	 @param _No 编号
	 */
	public Flow(String _No) throws Exception {
		this.setNo(_No);
		this.Retrieve();
	}
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		//获得事件实体.
		if (DataType.IsNullOrEmpty(this.getFlowMark()) == true)
		{
			this.setFlowEventEntity(bp.wf.Glo.GetFlowEventEntityStringByFlowMark(this.getNo()));
		}
		else
		{
			this.setFlowEventEntity(bp.wf.Glo.GetFlowEventEntityStringByFlowMark(this.getFlowMark()));
		}

		DBAccess.RunSQL("UPDATE WF_Node SET FlowName='" + this.getName() + "' WHERE FK_Flow='" + this.getNo() + "'");
		DBAccess.RunSQL("UPDATE Sys_MapData SET Name='" + this.getName() + "' WHERE No='" + this.getPTable() + "'");
		return super.beforeUpdateInsertAction();
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

		//取消了缓存.
		map.setDepositaryOfEntity(Depositary.Application);
		//map.DepositaryOfEntity= Depositary.Application;
		map.IndexField = FlowAttr.FK_FlowSort;
		map.setCodeStruct( "3");

		map.AddTBStringPK(FlowAttr.No, null, "编号", true, true, 1, 4, 3);
		map.AddTBString(FlowAttr.Name, null, "名称", true, false, 0, 200, 10);

		map.AddTBString(FlowAttr.FK_FlowSort, null, "流程类别", true, false, 0, 35, 10);
		// map.AddDDLEntities(FlowAttr.FK_FlowSort, "01", "流程类别", new FlowSorts(), false);
		map.AddTBString(FlowAttr.SysType, null, "系统类别", true, false, 0, 3, 10);

		map.AddTBInt(FlowAttr.FlowRunWay, 0, "运行方式", false, false);
		map.AddTBInt(FlowAttr.WorkModel, 0, "WorkModel", false, false);

		map.AddTBString(FlowAttr.RunObj, null, "运行内容", true, false, 0, 500, 10);
		map.AddTBString(FlowAttr.Note, null, "备注", true, false, 0, 300, 10);
		map.AddTBString(FlowAttr.RunSQL, null, "流程结束执行后执行的SQL", true, false, 0, 500, 10);

		map.AddTBInt(FlowAttr.NumOfBill, 0, "是否有单据", false, false);
		map.AddTBInt(FlowAttr.NumOfDtl, 0, "NumOfDtl", false, false);
		map.AddTBInt(FlowAttr.FlowAppType, 0, "流程类型", false, false);
		map.AddTBInt(FlowAttr.ChartType, 1, "节点图形类型", false, false);

		//map.AddTBInt(FlowAttr.IsFrmEnable, 0, "是否显示表单", true, true);
		map.AddTBInt(FlowAttr.IsTruckEnable, 1, "是否显示轨迹图", true, true);
		map.AddTBInt(FlowAttr.IsTimeBaseEnable, 1, "是否显示时间轴", true, true);
		map.AddTBInt(FlowAttr.IsTableEnable, 1, "是否显示时间表", true, true);
		//map.AddTBInt(FlowAttr.IsOPEnable, 0, "是否显示操作", true, true);
		map.AddTBInt(FlowAttr.TrackOrderBy, 0, "排序方式", true, true);
		map.AddTBInt(FlowAttr.SubFlowShowType, 0, "子流程轨迹图显示模式", true, true);


		// map.AddBoolean(FlowAttr.IsOK, true, "是否启用", true, true);
		map.AddTBInt(FlowAttr.IsCanStart, 1, "可以独立启动否？", true, true);
		map.AddTBInt(FlowAttr.IsStartInMobile, 1, "是否可以在手机里发起？", true, true);

		//(启用后,ccflow就会为已知道的节点填充处理人到WF_SelectAccper)
		map.AddTBInt(FlowAttr.IsFullSA, 0, "是否自动计算未来的处理人？", false, false);
		map.AddTBInt(FlowAttr.IsMD5, 0, "IsMD5", false, false);

//		map.AddBoolean(FlowAttr.IsWaterMark, false, "表单是否启用水印", true, true,false);
//		//水印展示内容
//		map.AddDDLSysEnum(FlowAttr.WaterMarkText,0, "'水印默认展示'", true, true, "WaterMarkText","@0=系统默认值@1=登录人员账号@2=登录人员名称@3=当前日期(yyyy年MM月dd日)@4=当前日期(yyyy年MM月dd日HH时mm分)@5=登录人员名称+当前日期(yyyy年MM月dd日)@6=登录人员名称+当前日期(yyyy年MM月dd日HH时mm分)");


		map.AddTBInt(FlowAttr.IsEnableDBVer, 0, "是否是启用数据版本控制", false, false);
		map.AddTBInt(FlowAttr.Idx, 0, "显示顺序号(在发起列表中)", true, false);

		map.AddTBInt(FlowAttr.SDTOfFlowRole, 0, "流程计划完成日期计算规则", true, false);
		map.AddTBString(FlowAttr.SDTOfFlowRoleSQL, null, "流程计划完成日期计算规则SQL", false, false, 0, 200, 10);


		// add 2013-01-01.
		map.AddTBString(FlowAttr.PTable, null, "流程数据存储主表", true, false, 0, 50, 10);

		// add 2019.11.07
		map.AddTBInt(FlowAttr.FlowDevModel, 0, "设计模式", true, false);
		map.AddTBString(FlowAttr.FrmUrl, null, "表单信息", true, false, 0, 150, 10, true);



		// 草稿规则 "@0=无(不设草稿)@1=保存到待办@2=保存到草稿箱"
		map.AddTBInt(FlowAttr.Draft, 0, "草稿规则", true, false);

		// add 2013-02-05.
		map.AddTBString(FlowAttr.TitleRole, null, "标题生成规则", true, false, 0, 90, 10, true);
		map.AddTBString(FlowAttr.TitleRoleNodes, null, "生成标题的节点", true, false, 0, 300, 10, true);
		// add 2013-02-14
		map.AddTBString(FlowAttr.FlowMark, null, "流程标记", true, false, 0, 50, 10);
		map.AddTBString(FlowAttr.FlowEventEntity, null, "FlowEventEntity", true, false, 0, 100, 10, true);
		map.AddTBInt(FlowAttr.GuestFlowRole, 0, "是否是客户参与流程？", true, false);
		map.AddTBString(FlowAttr.BillNoFormat, null, "单据编号格式", true, false, 0, 50, 10, true);

		//部门权限控制类型,此属性在报表中控制的.
		map.AddTBInt(FlowAttr.DRCtrlType, 0, "部门查询权限控制方式", true, false);

		//运行主机. 这个流程运行在那个子系统的主机上.
		map.AddTBInt(FlowAttr.IsToParentNextNode, 0, "子流程运行到该节点时，让父流程自动运行到下一步", false, false);


		///#region 流程启动限制
		map.AddTBInt(FlowAttr.StartLimitRole, 0, "启动限制规则", true, false);
		map.AddTBString(FlowAttr.StartLimitPara, null, "规则内容", true, false, 0, 500, 10, true);
		map.AddTBString(FlowAttr.StartLimitAlert, null, "限制提示", true, false, 0, 500, 10, false);
		map.AddTBInt(FlowAttr.StartLimitWhen, 0, "提示时间", true, false);

		///#endregion 流程启动限制


		///#region 导航方式。
		map.AddTBInt(FlowAttr.StartGuideWay, 0, "前置导航方式", false, false);
		map.AddTBString(FlowAttr.StartGuideLink, null, "右侧的连接", true, false, 0, 200, 10, true);
		map.AddTBString(FlowAttr.StartGuideLab, null, "连接标签", true, false, 0, 200, 10, true);

		map.AddTBString(FlowAttr.StartGuidePara1, null, "参数1", true, false, 0, 500, 10, true);
		map.AddTBString(FlowAttr.StartGuidePara2, null, "参数2", true, false, 0, 500, 10, true);
		map.AddTBString(FlowAttr.StartGuidePara3, null, "参数3", true, false, 0, 500, 10, true);
		map.AddTBInt(FlowAttr.IsResetData, 0, "是否启用数据重置按钮？", true, false);
		//    map.AddTBInt(FlowAttr.IsImpHistory, 0, "是否启用导入历史数据按钮？", true, false);
		map.AddTBInt(FlowAttr.IsLoadPriData, 0, "是否导入上一个数据？", true, false);

		///#endregion 导航方式。

		map.AddTBInt(FlowAttr.IsDBTemplate, 0, "是否启用数据模版？", true, false);

		//批量发起 add 2013-12-27.
		map.AddTBInt(FlowAttr.IsBatchStart, 0, "是否可以批量发起", true, false);
		map.AddTBString(FlowAttr.BatchStartFields, null, "批量发起字段(用逗号分开)", true, false, 0, 200, 10, true);
		//map.AddTBInt(FlowAttr.IsAutoSendSubFlowOver, 0, "(当前节点为子流程时)是否检查所有子流程完成后父流程自动发送", true, true);

		map.AddTBString(FlowAttr.Ver, null, "版本号", true, true, 0, 20, 10);

		map.AddTBString(FlowAttr.OrgNo, null, "OrgNo", true, true, 0, 50, 10);

		map.AddTBDateTime(FlowAttr.CreateDate, null, "创建日期", true, true);
		map.AddTBString(FlowAttr.Creater, null, "创建人", true, true, 0, 100, 10, true);

		// 改造参数类型.
		map.AddTBInt(FlowAttr.DevModelType, 0, "设计模式", false, false);
		map.AddTBString(FlowAttr.DevModelPara, null, "设计模式参数", false, false, 0, 50, 10, false);
		//  map.AddTBString(FlowAttr.DomainExt, null, "DomainExt", true, true, 0, 50, 10);
		//参数.
		map.AddTBAtParas(1000);

		///#region 数据同步方案
		//数据同步方式.
		map.AddTBInt(FlowAttr.DTSWay, 0, "同步方式", true, true);
		map.AddTBString(FlowAttr.DTSSpecNodes, null, "同步字段", false, false, 0, 4000, 10);

		//map.AddTBString(FlowAttr.DTSDBSrc, null, "数据源", true, false, 0, 200, 100, false);
		//map.AddTBString(FlowAttr.DTSBTable, null, "业务表名", true, false, 0, 200, 100, false);
		//map.AddTBString(FlowAttr.DTSBTablePK, null, "业务表主键", false, false, 0, 32, 10);
		//   map.AddTBInt(FlowAttr.DTSTime, FlowDTSTime.AllNodeSend.getValue(), "执行同步时间点", true, true);
		//map.AddTBString(FlowAttr.DTSFields, null, "要同步的字段s,中间用逗号分开.", false, false, 0, 900, 100, false);
		map.AddTBString("Icon", null, "Icon", true, true, 0, 50, 10);
		map.AddTBString("TestSysPara", null, "流程系统参数", false, true, 0, 100, 10);
		map.AddTBString("TestFrmPara", null, "表单填充数据", false, true, 0, 100, 10);
		///#endregion 数据同步方案


		RefMethod rm = new RefMethod();
		rm.Title = "设计检查报告"; // "设计检查报告";
		rm.ToolTip = "检查流程设计的问题。";
		rm.Icon = "../../WF/Img/Btn/Confirm.gif";
		rm.ClassMethodName = this.toString() + ".DoCheck";
		rm.GroupName = "流程维护";
		map.AddRefMethod(rm);

		map.AddMyFile("sss");


		this.set_enMap(map);
		return this.get_enMap();
	}

	///#endregion


	///#region  公共方法
	/**
	 创建索引

	 @return
	 */
	public final String CreateIndex() throws Exception {
		// 为track表创建索引.  FID, WorkID
		String ptable = "ND" + Integer.parseInt(this.getPTable()) + "Track";

		// DBAccess.CreatIndex(DBUrlType.AppCenterDSN, ptable, "my");

		return "流程[" + this.getNo() + "." + this.getName() + "]索引创建成功.";
	}
	/**
	 删除数据.

	 @return
	 */
	public final String DoDelData() throws Exception {

		///#region 删除独立表单的数据.
		String mysql = "SELECT OID FROM " + this.getPTable();
		FrmNodes fns = new FrmNodes();
		fns.Retrieve(FrmNodeAttr.FK_Flow, this.getNo(), null);
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
				DBAccess.RunSQL("DELETE FROM " + md.getPTable() + " WHERE OID in (" + mysql + ")");
			}
			catch (java.lang.Exception e)
			{
			}
		}

		///#endregion 删除独立表单的数据.

		String sql = "  WHERE FK_Node in (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";
		String sql1 = " WHERE NodeID in (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";

		// DBAccess.RunSQL("DELETE FROM WF_CHOfFlow WHERE FK_Flow='" + this.getNo() + "'");

		DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE FK_Flow='" + this.getNo() + "'");
		DBAccess.RunSQL("DELETE FROM WF_GenerWorkFlow WHERE FK_Flow='" + this.getNo() + "'");

		DBAccess.RunSQL("DELETE FROM WF_CCList WHERE FlowNo='" + this.getNo() + "'");

		String sqlIn = " WHERE ReturnNode IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";
		DBAccess.RunSQL("DELETE FROM WF_SelectAccper " + sql);
		DBAccess.RunSQL("DELETE FROM WF_TransferCustom " + sql);
		// DBAccess.RunSQL("DELETE FROM WF_FileManager " + sql);
		DBAccess.RunSQL("DELETE FROM WF_RememberMe " + sql);

		if (DBAccess.IsExitsObject("ND" + Integer.parseInt(this.getNo()) + "Track"))
		{
			DBAccess.RunSQL("DELETE FROM ND" + Integer.parseInt(this.getNo()) + "Track ");
		}

		if (DBAccess.IsExitsObject("ND" + Integer.parseInt(this.getNo()) + "Rpt"))
		{
			DBAccess.RunSQL("DELETE FROM ND" + Integer.parseInt(this.getNo()) + "Rpt ");
		}

		DBAccess.RunSQL("DELETE FROM WF_CCList WHERE FlowNo='" + this.getNo() + "'");


		//判断当前的流程是否为主版本，不是主版本不能删除表单数据
		if (DBAccess.IsExitsObject(this.getPTable()) && DataType.IsNullOrEmpty(this.getFlowSortNo())==false)
		{
			DBAccess.RunSQL("DELETE FROM " + this.getPTable());
		}

		if (DBAccess.IsExitsTableCol("WF_CH", "FlowNo"))
			DBAccess.RunSQL("DELETE FROM WF_CH WHERE FK_Flow='" + this.getNo() + "'");

		if (DBAccess.IsExitsTableCol("WF_CH", "FK_Flow"))
			DBAccess.RunSQL("DELETE FROM WF_CH WHERE FK_Flow='" + this.getNo() + "'");
		//DBAccess.RunSQL("DELETE FROM Sys_MapExt WHERE FK_MapData LIKE 'ND"+int.Parse(this.getNo())+"%'" );

		//删除节点数据。
		Nodes nds = new Nodes(this.getNo());
		for (Node nd : nds.ToJavaList())
		{
			try
			{
				Work wk = nd.getHisWork();
				DBAccess.RunSQL("DELETE FROM " + wk.getEnMap().getPhysicsTable());
			}
			catch (java.lang.Exception e2)
			{
			}

			MapDtls dtls = new MapDtls("ND" + nd.getNodeID());
			for (MapDtl dtl : dtls.ToJavaList())
			{
				try
				{
					DBAccess.RunSQL("DELETE FROM " + dtl.getPTable());
				}
				catch (java.lang.Exception e3)
				{
				}
			}
		}
		MapDtls mydtls = new MapDtls("ND" + Integer.parseInt(this.getNo()) + "Rpt");
		for (MapDtl dtl : mydtls.ToJavaList())
		{
			try
			{
				DBAccess.RunSQL("DELETE FROM " + dtl.getPTable());
			}
			catch (java.lang.Exception e4)
			{
			}
		}
		return "删除成功...";
	}

	public final String DoCopy() throws Exception {
		//获取当前流程的模板数据
		String path = this.GenerFlowXmlTemplete();
		;
		bp.wf.Flow flow = TemplateGlo.LoadFlowTemplate(this.getFlowSortNo(), path, ImpFlowTempleteModel.AsNewFlow, null);
		flow.DoCheck(); //要执行一次检查.
		return flow.getNo();
	}

	/**
	 检查报表
	 */
	public final void CheckRpt() throws Exception {
		this.DoCheck_CheckRpt(this.getHisNodes());
	}
	@Override
	protected boolean beforeInsert() throws Exception
	{

		//删除数据库中的垃圾数据
		DoDelData();
		if (bp.wf.Glo.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.setOrgNo(WebUser.getOrgNo());
		}

		//清空WF_Emp中的StartFlow
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			DBAccess.RunSQL("UPDATE  WF_Emp Set StartFlows ='' ");
		}
		else
		{
			DBAccess.RunSQL("UPDATE  WF_Emp Set StartFlows ='' WHERE OrgNo='" + WebUser.getOrgNo() + "'");
		}

		//#region 如果不是节点表单，流程表单: 给表单找一个目录 - 放入根目录下.
		if ( DataType.IsNullOrEmpty(this.getFlowSortNo()) == true)
		{
			String treeNo = "";
			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
				treeNo = WebUser.getOrgNo();
			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
			{
				String sql = "SELECT No FROM WF_FlowSort WHERE OrgNo='" + WebUser.getNo() + "'";
				treeNo = DBAccess.RunSQLReturnStringIsNull(sql, "");
				if (DataType.IsNullOrEmpty(treeNo) == true)
				{
					sql = "SELECT No FROM WF_FlowSort";
					treeNo = DBAccess.RunSQLReturnStringIsNull(sql, "");
				}
			}
			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
			{
				String sql = "SELECT No FROM WF_FlowSort ";
				treeNo = DBAccess.RunSQLReturnStringIsNull(sql, "");
			}
			this.SetValByKey("FK_FlowSort", treeNo);
		}
		this.SetValByKey("Icon", "icon-plane");
        // #endregion 给表单找一个目录.

		return super.beforeInsert();
	}
	/**
	 更新之前做检查

	 @return
	 */
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		//检查设计流程权限,集团模式下，不是自己创建的流程，不能设计流程.
		TemplateGlo.CheckPower(this.getNo());

		//删除缓存数据.
		this.ClearAutoNumCache(false);

		this.setVer(DataType.getCurrentDateTimess());
		return super.beforeUpdate();
	}



	/**
	 更新版本号
	 */
	public static void UpdateVer(String flowNo)
	{
		String sql = "UPDATE WF_Flow SET Ver='" + DataType.getCurrentDateTimess() + "' WHERE No='" + flowNo + "'";
		DBAccess.RunSQL(sql);
	}
	/**
	 删除功能.

	 @return
	 */
	@Override
	protected boolean beforeDelete() throws Exception
	{
		//检查设计流程权限,集团模式下，不是自己创建的流程，不能设计流程.
		TemplateGlo.CheckPower(this.getNo());


		//写入日志.
		bp.sys.base.Glo.WriteUserLog("删除流程：" + this.getName() + " - " + this.getNo(), "通用操作");

		//throw new Exception("err@请反馈给我们，非法的删除操作。 ");
		return super.beforeDelete();
	}
	public final String DoDelete() throws Exception {
		if (DataType.IsNullOrEmpty(this.getNo()) == true)
		{
			throw new RuntimeException("err@流程没有初始化，删除错误.");
		}

		//检查设计流程权限,集团模式下，不是自己创建的流程，不能设计流程.
		TemplateGlo.CheckPower(this.getNo());

		//检查是否有空白状态的数据
		String checkStr="SELECT WorkID FROM  WF_GenerWorkFlow WHERE  WFState=0 AND  FK_Flow='" + this.getNo() + "' ";
		DataTable dtcheck = DBAccess.RunSQLReturnTable(checkStr);
		if (dtcheck.Rows.size() >= 1)
		{
			for(DataRow dr :dtcheck.Rows) {
				long workid = Long.parseLong(dr.getValue(0).toString());
				Dev2Interface.Flow_DoDeleteFlowByReal(workid, true);
			}
		}

		//检查节点下是否有可以删除.
		String sql = "SELECT COUNT(*) AS A FROM WF_GenerWorkerlist WHERE IsPass=0 AND FK_Flow='" + this.getNo() + "' ";
		int num = DBAccess.RunSQLReturnValInt(sql);
		if (num != 0)
		{
			String info= "info@该流程下有未完成的流程【" + num + "】个您不能删除，可以通过如下SQL查询:" + sql + "";
			info += " @如果确实需要删除该流程，请执行删除语句:DELETE FROM WF_GenerWorkerlist WHERE IsPass=0 AND FK_Flow='" + this.getNo() + "' ";
			info += " @或者到：流程属性、流程运维、查询出来流程并删除掉他们。";
			return info;
		}

		//检查流程有没有版本管理？
		if (this.getFlowSortNo().length() > 1)
		{
			String str = "SELECT * FROM WF_Flow WHERE PTable='" + this.getPTable() + "' AND FK_FlowSort='' ";
			DataTable dt = DBAccess.RunSQLReturnTable(str);
			if (dt.Rows.size() >= 1)
			{
				return "err@删除流程出错，该流程下有[" + dt.Rows.size() + "]个子版本您不能删除。";
			}
		}

		sql = "";
		//sql = " DELETE FROM WF_chofflow WHERE FK_Flow='" + this.getNo() + "'";
		//sql += "@ DELETE FROM WF_GenerWorkerlist WHERE FK_Flow='" + this.No + "'";
		//sql += "@ DELETE FROM  WF_GenerWorkFlow WHERE FK_Flow='" + this.No + "'";
		sql += "@ DELETE FROM WF_GenerWorkerlist WHERE FK_Node IN (SELECT NodeID From WF_Node WHERE FK_Flow='" + this.getNo() + "')";
		sql += "@ DELETE FROM WF_GenerWorkFlow WHERE FK_Node IN (SELECT NodeID From WF_Node WHERE FK_Flow='" + this.getNo() + "')";
		sql += "@ DELETE FROM WF_Cond WHERE RefFlowNo='" + this.getNo() + "'";
		sql += "@ DELETE FROM  WF_Cond WHERE FK_Flow='" + this.getNo() + "'";
		//删除节点事件
		sql += "@ DELETE FROM  Sys_FrmEvent WHERE RefFlowNo='" + this.getNo() + "'";

		//删除消息配置.
		sql += "@ DELETE FROM WF_PushMsg WHERE FlowNo='" + this.getNo() + "'";

		// 删除角色节点。
		sql += "@ DELETE FROM WF_NodeStation WHERE FK_Node IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";

		// 删除方向。
		sql += "@ DELETE FROM WF_Direction WHERE FK_Flow='" + this.getNo() + "'";

		//删除节点绑定信息.
		sql += "@ DELETE FROM WF_FrmNode WHERE FK_Node IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";

		sql += "@ DELETE FROM WF_NodeEmp  WHERE   FK_Node IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";

		sql += "@ DELETE FROM WF_NodeReturn WHERE   FK_Node IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";

		sql += "@ DELETE FROM WF_NodeDept WHERE   FK_Node IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";
		sql += "@ DELETE FROM WF_NodeStation WHERE   FK_Node IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";
		sql += "@ DELETE FROM WF_NodeEmp WHERE   FK_Node IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";

		sql += "@ DELETE FROM WF_NodeToolbar WHERE   FK_Node IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";
		sql += "@ DELETE FROM WF_SelectAccper WHERE   FK_Node IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";
		//sql += "@ DELETE FROM WF_TurnTo WHERE   FK_Node IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";

		//删除侦听.
		// sql += "@ DELETE FROM WF_Listen WHERE FK_Node IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";

		// 删除d2d数据.
		//  sql += "@GO DELETE WF_M2M WHERE FK_Node IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";
		//// 删除配置.
		//sql += "@ DELETE FROM WF_FAppSet WHERE NodeID IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";

		Nodes nds = new Nodes(this.getNo());
		for (Node nd : nds.ToJavaList())
		{
			// 删除节点所有相关的东西.
			nd.Delete();
		}

		//// 外部程序设置
		//sql += "@ DELETE FROM WF_FAppSet WHERE  NodeID in (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";

		//删除单据.
		sql += "@ DELETE FROM Sys_FrmPrintTemplate WHERE  NodeID in (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.getNo() + "')";
		//删除权限控制.
		sql += "@ DELETE FROM Sys_FrmSln WHERE FK_Flow='" + this.getNo() + "'";
		//考核表
		//sql += "@ DELETE FROM WF_CH WHERE FK_Flow='" + this.getNo() + "'";
		//删除抄送
		sql += "@ DELETE FROM WF_CCList WHERE FlowNo='" + this.getNo() + "'";

		sql += "@ DELETE FROM WF_CCRole WHERE FlowNo='" + this.getNo() + "'";

		sql += "@ DELETE  FROM WF_Node WHERE FK_Flow='" + this.getNo() + "'";
		sql += "@ DELETE  FROM WF_LabNote WHERE FK_Flow='" + this.getNo() + "'";

		//删除分组信息
		sql += "@ DELETE FROM Sys_GroupField WHERE FrmID NOT IN(SELECT NO FROM Sys_MapData)";

		//删除子流程配置信息
		sql += "@ DELETE FROM WF_NodeSubFlow WHERE FK_Flow='" + this.getNo() + "'";


		///#region 删除流程报表,删除轨迹
		MapData md = new MapData();
		md.setNo("ND" + Integer.parseInt(this.getNo()) + "Rpt");
		md.Delete();

		//删除视图.
		if (DBAccess.IsExitsObject("V_" + this.getNo()))
		{
			DBAccess.RunSQL("DROP VIEW V_" + this.getNo());
		}

		//删除轨迹.
		if (DBAccess.IsExitsObject("ND" + Integer.parseInt(this.getNo()) + "Track") == true)
		{
			DBAccess.RunSQL("DROP TABLE ND" + Integer.parseInt(this.getNo()) + "Track ");
		}

		//删除存储表
		//if (DBAccess.IsExitsObject("ND" + int.Parse(this.getNo()) + "Rpt") == true)
		//    DBAccess.RunSQL("DROP TABLE ND" + int.Parse(this.getNo()) + "Rpt ");


		///#endregion 删除流程报表,删除轨迹.

		// 执行录制的sql scripts.
		DBAccess.RunSQLs(sql);

		//清空WF_Emp中的StartFlow

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			DBAccess.RunSQL("UPDATE  WF_Emp Set StartFlows ='' ");
		}
		else
		{
			DBAccess.RunSQL("UPDATE  WF_Emp Set StartFlows ='' WHERE OrgNo='" + WebUser.getOrgNo() + "'");
		}

		//删除数据的接口.
		DoDelData();

		this.Delete(); //删除需要移除缓存.

		return "执行成功.";
	}
	/**
	 向上移动
	 */
	public final void DoUp() throws Exception {
		this.DoOrderUp(FlowAttr.FK_FlowSort, this.getFlowSortNo(), FlowAttr.Idx);
	}
	/**
	 向下移动
	 */
	public final void DoDown() throws Exception
	{
		this.DoOrderDown(FlowAttr.FK_FlowSort, this.getFlowSortNo(), FlowAttr.Idx);
	}

	///#endregion


	///#region 版本管理.
	/**
	 创建新版本.

	 @return
	 */
	public final String VerCreateNew()
	{
		try
		{
			//生成模版.
			String file = GenerFlowXmlTemplete();

			Flow newFlow = TemplateGlo.LoadFlowTemplate(this.getFlowSortNo(), file, ImpFlowTempleteModel.AsNewFlow);

			newFlow.setPTable(this.getPTable());
			newFlow.setFlowSortNo(""); //不能显示流程树上.
			newFlow.setName(this.getName());
			newFlow.SetValByKey(FlowAttr.CreateDate,DataType.getCurrentDateTime());
			newFlow.setVer(DataType.getCurrentDateTime());
			newFlow.setItIsCanStart(false); //不能发起.
			if(DataType.IsNullOrEmpty(this.getFlowMark())==false)
				newFlow.setFlowMark(this.getFlowMark());
			newFlow.DirectUpdate();
			return newFlow.getNo();
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		//DataSet ds = this.GenerFlowXmlTemplete();
		//return "001";
	}

	/**
	 * 检查流程标记是否重复
	 * @param mark
	 * @param flowNo
	 * @return
	 */
	public String CheckFlowMark(String mark,String flowNo) throws Exception {
		this.setFlowMark(mark);
		this.setNo(flowNo);
		String sql = "";
		if (bp.difference.SystemConfig.getCCBPMRunModel()  == CCBPMRunModel.Single)
			sql = "SELECT No,Name FROM WF_Flow WHERE   FlowMark='" + this.getFlowMark() + "' AND No!='" + this.getNo() + "' AND PTable!='"+this.getPTable()+"'";
		else
			sql = "SELECT No,Name FROM WF_Flow WHERE OrgNo='" + WebUser.getOrgNo() + "' AND FlowMark='" + this.getFlowMark() + "' AND No!='" + this.getNo() + "' AND PTable!='"+this.getPTable()+"'";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		if (dt.Rows.size() >= 1)
		{
			String msg = "";
			for (DataRow dr : dt.Rows)
			{
				msg += " FlowNo=" + dr.getValue("No").toString() + " FlowName=" + dr.getValue("Name").toString() + "";
			}

			return "err@如下流程标记与此重复:" + msg;
		}


		return "info@流程标记无重复.";
	}
	public String ClearStartFlows()
	{
		DBAccess.RunSQL("UPDATE  WF_Emp Set StartFlows =''");
		return "";
	}
	/**
	 设置当前的版本为新版本.

	 @return
	 */
	public final String VerSetCurrentVer() throws Exception {
		String sql = "SELECT FK_FlowSort,No FROM WF_Flow WHERE PTable='" + this.getPTable() + "' AND FK_FlowSort!='' ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			return "err@没有找到主版本,请联系管理员.";
		}
		String flowSort = dt.Rows.get(0).getValue(0).toString();
		String oldFlowNo = dt.Rows.get(0).getValue(1).toString();
		sql = "UPDATE WF_Flow SET FK_FlowSort='',IsCanStart=0 WHERE PTable='" + this.getPTable() + "' ";
		DBAccess.RunSQL(sql);

		sql = "UPDATE WF_Flow SET FK_FlowSort='" + flowSort + "', IsCanStart=1 WHERE No='" + this.getNo() + "' ";
		DBAccess.RunSQL(sql);

		//清缓存
		Cache2019.DeleteRow("BP.WF.Flow", oldFlowNo);
		Cache2019.DeleteRow("BP.WF.Flow", this.getNo());
		Flow flow = new Flow(oldFlowNo);
		flow = new Flow(this.getNo());


		return "info@设置成功.";
	}
	/**
	 获得版本列表.

	 @return
	 */
	public final String VerGenerVerList() throws Exception {
		//if (this.FlowSortNo.equals("") == true)
		//    return "err@当前版本为分支版本，您无法管理，只有主版本才能管理。";

		DataTable dt = new DataTable();
		dt.Columns.Add("Ver"); //版本号
		dt.Columns.Add("No"); //内部编号
		dt.Columns.Add("Name"); //流程名称.
		dt.Columns.Add("IsRel"); //是否发布？
		dt.Columns.Add("NumOfRuning"); //运行中的流程.
		dt.Columns.Add("NumOfOK"); //运行完毕的流程.

		//如果业务表是空的，就更新它.
		String ptable = this.GetValStringByKey(FlowAttr.PTable);
		if (DataType.IsNullOrEmpty(ptable) == true)
		{
			this.SetValByKey(FlowAttr.PTable, this.getPTable());
			this.DirectUpdate();
		}

		//获得所有的版本.
		// String sql = "SELECT No,Name,Ver,FK_FlowSort FROM WF_Flow WHERE PTable='"+this.PTable+"'";
		String sql = "SELECT No  FROM WF_Flow WHERE PTable='" + this.getPTable() + "'";
		Flows fls = new Flows();
		fls.RetrieveInSQL(sql);

		for (Flow item : fls.ToJavaList())
		{
			DataRow dr = dt.NewRow();
			dr.setValue("Ver", item.getVer());
			dr.setValue("No", item.getNo());
			dr.setValue("Name", item.getName());

			if (item.getItIsCanStart()== false)
			{
				dr.setValue("IsRel", "0");
			}
			else
			{
				dr.setValue("IsRel", "1");
			}

			dr.setValue("NumOfRuning", DBAccess.RunSQLReturnValInt("SELECT COUNT(WORKID) FROM WF_GenerWorkFlow WHERE FK_FLOW='" + item.getNo() + "' AND WFState=2"));
			dr.setValue("NumOfOK", DBAccess.RunSQLReturnValInt("SELECT COUNT(WORKID) FROM WF_GenerWorkFlow WHERE FK_FLOW='" + item.getNo() + "' AND WFState=3"));
			dt.Rows.add(dr);
		}

		return bp.tools.Json.ToJson(dt);
	}

	///#endregion 版本管理.


}
