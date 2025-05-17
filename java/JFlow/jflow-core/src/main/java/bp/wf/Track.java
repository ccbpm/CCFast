package bp.wf;

import bp.da.*;
import bp.en.*;
import bp.tools.SnowflakeIdGenerator;

/**
 轨迹
*/
public class Track extends bp.en.Entity
{

		///#region 基本属性.
	/**
	 表单数据
	*/
	public String FrmDB = null;
	public String WriteDB = null;
	/**
	 主键值
	*/
	public final String getMyPK()  {
		return this.GetValStrByKey(TrackAttr.MyPK);
	}
	public final void setMyPK(String value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.MyPK, value);
	}

	@Override
	public String getPK()  {
		return "MyPK";
	}

	@Override
	public String getPKField() {
		return "MyPK";
	}

		///#endregion 基本属性.


		///#region 字段属性.
	/**
	 节点从
	*/
	public final int getNDFrom()  {
		return this.GetValIntByKey(TrackAttr.NDFrom);
	}
	public final void setNDFrom(int value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.NDFrom, value);
	}
	/**
	 节点到
	*/
	public final int getNDTo()  {
		return this.GetValIntByKey(TrackAttr.NDTo);
	}
	public final void setNDTo(int value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.NDTo, value);
	}
	/**
	 从人员
	*/
	public final String getEmpFrom()  {
		return this.GetValStringByKey(TrackAttr.EmpFrom);
	}
	public final void setEmpFrom(String value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.EmpFrom, value);
	}
	/**
	 到人员
	*/
	public final String getEmpTo()  {
		return this.GetValStringByKey(TrackAttr.EmpTo);
	}
	public final void setEmpTo(String value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.EmpTo, value);
	}
	/**
	 参数数据.
	*/
	public final String getTag()  {
		return this.GetValStringByKey(TrackAttr.Tag);
	}
	public final void setTag(String value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.Tag, value);
	}
	/**
	 记录日期
	*/
	public final String getRDT()  {
		return this.GetValStringByKey(TrackAttr.RDT);
	}
	public final void setRDT(String value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.RDT, value);
	}
	/**
	 fid
	*/
	public final long getFID()  {
		return this.GetValInt64ByKey(TrackAttr.FID);
	}
	public final void setFID(long value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.FID, value);
	}
	/**
	 工作ID
	*/
	public final long getWorkID()  {
		return this.GetValInt64ByKey(TrackAttr.WorkID);
	}
	public final void setWorkID(long value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.WorkID, value);
	}
	/**
	 CWorkID
	*/
	public final long getCWorkID()  {
		return this.GetValInt64ByKey(TrackAttr.CWorkID);
	}
	public final void setCWorkID(long value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.CWorkID, value);
	}
	/**
	 活动类型
	*/
	public final ActionType getHisActionType() {
		return ActionType.forValue(this.GetValIntByKey(TrackAttr.ActionType));
	}
	public final void setHisActionType(ActionType value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.ActionType, value.getValue());
	}

	/**
	 * 排序
	 */
//	public final int getIdx() {
//		return this.GetValIntByKey(TrackAttr.Idx);
//	}
//	public final void setIdx(int idx)  throws Exception
//	{
//		this.SetValByKey(TrackAttr.Idx, idx);
//	}
	/**
	 获取动作文本

	 param at
	 @return
	*/
	public static String GetActionTypeT(ActionType at)
	{
		switch (at)
		{
			case Forward:
				return "发送";
			case Return:
				return "退回";
			case ReturnAndBackWay:
				return "退回并原路返回";
			case Shift:
				return "移交";
			case UnShift:
				return "撤消移交";
			case Start:
				return "发起";
			case UnSend:
				return "撤消发送";
			case ForwardFL:
				return " -前进(分流点)";
			case ForwardHL:
				return " -向合流点发送";
			case FlowOver:
				return "流程结束";
			case CallChildenFlow:
				return "子流程调用";
			case StartChildenFlow:
				return "子流程发起";
			case SubThreadForward:
				return "线程前进";
			case RebackOverFlow:
				return "恢复已完成的流程";
			case FlowOverByCoercion:
				return "强制结束流程";
			case Hungup:
				return "挂起";
			case UnHungup:
				return "取消挂起";
			case Press:
				return "催办";
			case CC:
				return "抄送";
			case WorkCheck:
				return "审核";
			case AutoWorkCheck:
				return "自动审核";
			case ForwardAskfor:
				return "加签发送";
			case AskforHelp:
				return "加签";
			case Skip:
				return "跳转";
			case HuiQian:
				return "主持人执行会签";
			case DeleteFlowByFlag:
				return "逻辑删除";
			case Order:
				return "队列发送";
			case FlowBBS:
				return "评论";
			case TeampUp:
				return "协作";
			case Adjust:
				return "调整";
			case GovDoc:
				return "公文";
			default:
				return "信息" + at;
		}
	}
	/**
	 活动名称
	*/
	public final String getActionTypeText()  {
		return this.GetValStringByKey(TrackAttr.ActionTypeText);
	}
	public final void setActionTypeText(String value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.ActionTypeText, value);
	}
	/**
	 节点数据
	*/
	public final String getNodeData()  {
		return this.GetValStringByKey(TrackAttr.NodeData);
	}
	public final void setNodeData(String value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.NodeData, value);
	}
	/**
	 实际执行人
	*/
	public final String getExer()  {
		return this.GetValStringByKey(TrackAttr.Exer);
	}
	public final void setExer(String value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.Exer, value);
	}
	/**
	 审核意见
	*/
	public final String getMsg()  {
		return this.GetValStringByKey(TrackAttr.Msg);
	}
	public final void setMsg(String value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.Msg, value);
	}
	/**
	 消息
	*/
	public final String getMsgHtml() throws Exception {
		return this.GetValHtmlStringByKey(TrackAttr.Msg);
	}
	/**
	 人员到
	*/
	public final String getEmpToT()  {
		return this.GetValStringByKey(TrackAttr.EmpToT);
	}
	public final void setEmpToT(String value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.EmpToT, value);
	}
	/**
	 人员从
	*/
	public final String getEmpFromT()  {
		return this.GetValStringByKey(TrackAttr.EmpFromT);
	}
	public final void setEmpFromT(String value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.EmpFromT, value);
	}
	/**
	 节点从
	*/
	public final String getNDFromT()  {
		return this.GetValStringByKey(TrackAttr.NDFromT);
	}
	public final void setNDFromT(String value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.NDFromT, value);
	}
	/**
	 节点到
	*/
	public final String getNDToT()  {
		return this.GetValStringByKey(TrackAttr.NDToT);
	}
	public final void setNDToT(String value)  throws Exception
	 {
		this.SetValByKey(TrackAttr.NDToT, value);
	}
	public String FlowNo = null;
		///#endregion attrs


		///#region 构造.
	public String RptName = null;
	@Override
	public bp.en.Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_Track", "轨迹表");
		// #region 字段
		// 增加一个自动增长的列.
		map.AddTBIntPK(TrackAttr.MyPK, 0, "MyPK", true, false);
		map.AddTBInt(TrackAttr.ActionType, 0, "类型", true, false);
		map.AddTBString(TrackAttr.ActionTypeText, null, "类型(名称)", true, false, 0, 30, 100);
		map.AddTBInt(TrackAttr.FID, 0, "流程ID", true, false);
		map.AddTBInt(TrackAttr.WorkID, 0, "工作ID", true, false);
		//  map.AddTBInt(TrackAttr.CWorkID, 0, "CWorkID", true, false);

		map.AddTBInt(TrackAttr.NDFrom, 0, "从节点", true, false);
		map.AddTBString(TrackAttr.NDFromT, null, "从节点(名称)", true, false, 0, 300, 100);

		map.AddTBInt(TrackAttr.NDTo, 0, "到节点", true, false);
		map.AddTBString(TrackAttr.NDToT, null, "到节点(名称)", true, false, 0, 999, 900);

		map.AddTBString(TrackAttr.EmpFrom, null, "从人员", true, false, 0, 50, 100);
		map.AddTBString(TrackAttr.EmpFromT, null, "从人员(名称)", true, false, 0, 300, 100);

		map.AddTBString(TrackAttr.EmpTo, null, "到人员", true, false, 0, 2000, 100);
		map.AddTBString(TrackAttr.EmpToT, null, "到人员(名称)", true, false, 0, 2000, 100);

		map.AddTBString(TrackAttr.RDT, null, "日期", true, false, 0, 20, 100);
		map.AddTBFloat(TrackAttr.WorkTimeSpan, 0, "时间流程时长(天)", true, false);
		map.AddTBStringDoc(TrackAttr.Msg, null, "消息", true, false);
		map.AddTBStringDoc(TrackAttr.NodeData, null, "节点数据(日志信息)", true, false);
		map.AddTBString(TrackAttr.Tag, null, "参数", true, false, 0, 300, 3000);
		map.AddTBString(TrackAttr.Exer, null, "执行人", true, false, 0, 200, 100);
		///#endregion 字段

		this.set_enMap(map);
		return this.get_enMap();
	}
	/**
	 轨迹
	*/
	public Track()  {
	}
	/**
	 轨迹

	 param flowNo 流程编号
	 param mypk 主键
	*/
	public Track(String flowNo, String mypk)throws Exception
	{
		String sql = "SELECT * FROM ND" + Integer.parseInt(flowNo) + "Track WHERE MyPK='" + mypk + "'";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			throw new RuntimeException("@日志数据丢失.." + sql);
		}
		this.getRow().LoadDataTable(dt, dt.Rows.get(0));
	}
	/**
	 创建track.

	 param fk_flow 流程编号
	*/
	public static void CreateOrRepairTrackTable(String fk_flow) throws Exception {
		String ptable = "ND" + Integer.parseInt(fk_flow) + "Track";
		if (DBAccess.IsExitsObject(ptable))
		{
			return;
		}

		//删除主键.
		DBAccess.DropTablePK(ptable);

		// 删除主键.
		//DBAccess.DropTablePK("WF_Track");

		//创建表.
		Track tk = new Track();
		try
		{
			tk.CheckPhysicsTable();
			// 删除主键.
			DBAccess.DropTablePK("WF_Track");
			// 修改主键为bigint
			DBAccess.AlterColumnToBigInt("WF_Track", "MyPK");
		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteError(ex.getMessage() + " @可以容忍的异常....");
		}

		String sqlRename = "";
		switch (bp.difference.SystemConfig.getAppCenterDBType( ))
		{
			case MSSQL:
				sqlRename = "EXEC SP_RENAME WF_Track, " + ptable;
				break;
			case Informix:
				sqlRename = "RENAME TABLE WF_Track TO " + ptable;
				break;
			default:
				sqlRename = "ALTER TABLE WF_Track RENAME to " + ptable;
				break;
		}

		//重命名.
		DBAccess.RunSQL(sqlRename);

	}

	/**
	 插入

	 param mypk
	*/
	public final void DoInsert(long mypk)throws Exception
	{
		String ptable = "ND" + Integer.parseInt(this.FlowNo) + "Track";
		String dbstr = bp.difference.SystemConfig.getAppCenterDBVarStr();
		String sql = "INSERT INTO " + ptable;
		sql += "(";
		sql += TrackAttr.MyPK + ",";
		sql += TrackAttr.ActionType + ",";
		sql += TrackAttr.ActionTypeText + ",";
		sql += TrackAttr.FID + ",";
		sql += TrackAttr.WorkID + ",";
		sql += TrackAttr.NDFrom + ",";
		sql += TrackAttr.NDFromT + ",";
		sql += TrackAttr.NDTo + ",";
		sql += TrackAttr.NDToT + ",";
		sql += TrackAttr.EmpFrom + ",";
		sql += TrackAttr.EmpFromT + ",";
		sql += TrackAttr.EmpTo + ",";
		sql += TrackAttr.EmpToT + ",";
		sql += TrackAttr.RDT + ",";
		sql += TrackAttr.WorkTimeSpan + ",";
		sql += TrackAttr.Msg + ",";
		sql += TrackAttr.NodeData + ",";
		sql += TrackAttr.Tag + ",";
		sql += TrackAttr.Exer;
		sql += ") VALUES (";
		sql += dbstr + TrackAttr.MyPK + ",";
		sql += dbstr + TrackAttr.ActionType + ",";
		sql += dbstr + TrackAttr.ActionTypeText + ",";
		sql += dbstr + TrackAttr.FID + ",";
		sql += dbstr + TrackAttr.WorkID + ",";
		sql += dbstr + TrackAttr.NDFrom + ",";
		sql += dbstr + TrackAttr.NDFromT + ",";
		sql += dbstr + TrackAttr.NDTo + ",";
		sql += dbstr + TrackAttr.NDToT + ",";
		sql += dbstr + TrackAttr.EmpFrom + ",";
		sql += dbstr + TrackAttr.EmpFromT + ",";
		sql += dbstr + TrackAttr.EmpTo + ",";
		sql += dbstr + TrackAttr.EmpToT + ",";
		sql += dbstr + TrackAttr.RDT + ",";
		sql += dbstr + TrackAttr.WorkTimeSpan + ",";
		sql += dbstr + TrackAttr.Msg + ",";
		sql += dbstr + TrackAttr.NodeData + ",";
		sql += dbstr + TrackAttr.Tag + ",";
		sql += dbstr + TrackAttr.Exer;
		sql += ")";

		//如果这里是空的，就认为它是，从系统里面取出。
		if (DataType.IsNullOrEmpty(this.getActionTypeText()))
		{
			this.setActionTypeText(Track.GetActionTypeT(this.getHisActionType()));
		}

		if (mypk == 0)
		{
			SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0,0);
			long id = idGenerator.nextId();
			this.SetValByKey(TrackAttr.MyPK, id);
		}
		else
		{
			DBAccess.RunSQL("DELETE FROM " + ptable + " WHERE MyPK=" + mypk);
			this.SetValByKey(TrackAttr.MyPK, mypk);
		}

		//HttpWebResponse.
		//if (HttpHandler.)
		this.setTag(this.getTag() + "@SheBei=" + bp.web.WebUser.getSheBei());

//		Date d = new Date();
//		Date tempOut_d = new Date();
//		if (DataType.IsNullOrEmpty(getRDT()) || Date.TryParse(this.getRDT(), tempOut_d) == false)
//		{
//		d = tempOut_d.outArgValue;
//			this.setRDT(DataType.getCurrentDateTimess());
//		}
//	else
//	{
//		d = tempOut_d.outArgValue;
//	}


			///#region 执行保存
		try
		{
			Paras ps = SqlBuilder.GenerParas(this, null);
			ps.SQL = sql;

			switch (bp.difference.SystemConfig.getAppCenterDBType( ))
			{
				case MSSQL:
					this.RunSQL(ps);
					break;
				case Access:
					this.RunSQL(ps);
					break;
				case MySQL:
				case Informix:
				default:
					ps.SQL = ps.SQL.replace("[", "").replace("]", "");
					this.RunSQL(ps); // 运行sql.
					//  this.RunSQL(sql.Replace("[", "").Replace("]", ""), SqlBuilder.GenerParas(this, null));
					break;
			}
		}
		catch (RuntimeException ex)
		{
			// 写入日志.
			Log.DebugWriteError(ex.getMessage());

			//创建track.
			//Track.CreateOrRepairTrackTable(this.FK_Flow);
			throw ex;
		}


			///#region 增加,日志.
		//把frm日志写入到数据里.
		if (this.FrmDB != null)
		{
			if (this.getHisActionType() == ActionType.SubThreadForward || this.getHisActionType() == ActionType.StartChildenFlow || this.getHisActionType() == ActionType.Start || this.getHisActionType() == ActionType.Forward || this.getHisActionType() == ActionType.SubThreadForward || this.getHisActionType() == ActionType.ForwardHL || this.getHisActionType() == ActionType.FlowOver)
			{

				Flow fl = new Flow(this.FlowNo);
				Node nd = new Node(this.getNDFrom());

				Work wk = nd.getHisWork();
				wk.setOID(this.getWorkID());
				wk.RetrieveFromDBSources();

				WorkNodePlus.AddNodeFrmTrackDB(fl, nd, this, wk);
				//t.FrmDB = this.HisWork.ToJson();
			}

			DBAccess.SaveBigTextToDB(this.FrmDB, ptable, "MyPK", String.valueOf(mypk), "FrmDB");
		}

			///#endregion 增加.


		if (!DataType.IsNullOrEmpty(this.WriteDB)) //&& DBAccess.IsExitsTableCol(ptable, "WriteDB") == true)
		{
			if (this.getHisActionType() == ActionType.WorkCheck)
			{
				DBAccess.SaveBigTextToDB(this.WriteDB, ptable, "MyPK", this.getMyPK(), "WriteDB");
			}
			else
			{
				if(DBAccess.IsExitsTableCol(ptable, "WriteDB")){
					sql = "SELECT WriteDB From " + ptable + " WHERE MyPK='" + this.WriteDB + "'";
					DBAccess.SaveBigTextToDB(DBAccess.RunSQLReturnStringIsNull(sql, ""), ptable, "MyPK", this.getMyPK(), "WriteDB");
				}
			}


		}
			///#endregion 执行保存
		//解决流程的开始日期计算错误的问题.
		if (this.getHisActionType() == ActionType.Start || this.getHisActionType() == ActionType.StartChildenFlow)
		{
			Paras ps = new Paras();
			ps.SQL = "UPDATE WF_GenerWorkerlist SET RDT=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "RDT WHERE WorkID=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "WorkID ";
			ps.Add("RDT", this.getRDT(), false);
			ps.Add("WorkID", this.getWorkID());
			DBAccess.RunSQL(ps);

			ps = new Paras();
			ps.SQL = "UPDATE WF_GenerWorkFlow SET RDT=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "RDT WHERE WorkID=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "WorkID ";
			ps.Add("RDT", this.getRDT(), false);
			ps.Add("WorkID", this.getWorkID());
			DBAccess.RunSQL(ps);
		}
	}
	/**
	 增加授权人

	 @return
	*/
	@Override
	protected boolean beforeInsert() throws Exception {
		if (bp.web.WebUser.getNo().equals("Guest"))
		{
			this.setExer(bp.web.GuestUser.getNo() + "," + bp.web.GuestUser.getName());
		}
		else
		{
			if (bp.web.WebUser.getIsAuthorize())
			{
				this.setExer(bp.wf.Glo.DealUserInfoShowModel(bp.web.WebUser.getAuth(), bp.web.WebUser.getAuthName()));
			}
			else
			{
				this.setExer(bp.wf.Glo.DealUserInfoShowModel(bp.web.WebUser.getNo(), bp.web.WebUser.getName()));
			}
		}
		this.DoInsert(0);
		return false;
	}

		///#endregion 构造.
}
