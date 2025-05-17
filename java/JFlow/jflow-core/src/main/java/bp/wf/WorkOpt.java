package bp.wf;

import bp.da.*;
import bp.en.*;
import bp.en.Map;

/** 
 退回轨迹
*/
public class WorkOpt extends EntityMyPK
{

		///#region 基本属性
	/** 
	 工作ID
	*/
	public final long getWorkID() {
		return this.GetValInt64ByKey(WorkOptAttr.WorkID);
	}
	public final void setWorkID(long value) throws Exception {
		SetValByKey(WorkOptAttr.WorkID, value);
	}

	/** 
	 退回到节点
	*/
	public final int getNodeID() {
		return this.GetValIntByKey(WorkOptAttr.NodeID);
	}
	public final void setNodeID(int value) throws Exception {
		SetValByKey(WorkOptAttr.NodeID, value);
	}
	public final int getToNodeID() {
		return this.GetValIntByKey(WorkOptAttr.ToNodeID);
	}
	public final void setToNodeID(int value) throws Exception {
		SetValByKey(WorkOptAttr.ToNodeID, value);
	}
	public final String getFlowNo()  {
		return this.GetValStrByKey(WorkOptAttr.FlowNo);
	}
	public final void setFlowNo(String value) throws Exception {
		SetValByKey(WorkOptAttr.FlowNo, value);
	}
	public final String getSendNote()  {
		return this.GetValStrByKey(WorkOptAttr.SendNote);
	}
	public final void setSendNote(String value) throws Exception {
		SetValByKey(WorkOptAttr.SendNote, value);
	}
	public final String getEmpNo()  {
		return this.GetValStrByKey(WorkOptAttr.EmpNo);
	}
	public final void setEmpNo(String value) throws Exception {
		SetValByKey(WorkOptAttr.EmpNo, value);
	}
	/** 
	 退回人
	*/
	public final String getSendEmps() {
		return this.GetValStringByKey(WorkOptAttr.SendEmps);
	}
	public final void setSendEmps(String value) throws Exception {
		SetValByKey(WorkOptAttr.SendEmps, value);
	}
	public final String getSendDepts() {
		return this.GetValStringByKey(WorkOptAttr.SendDepts);
	}
	public final void setSendDepts(String value) throws Exception {
		SetValByKey(WorkOptAttr.SendDepts, value);
	}
	public final String getSendStas() {
		return this.GetValStringByKey(WorkOptAttr.SendStations);
	}
	public final void setSendStas(String value) throws Exception {
		SetValByKey(WorkOptAttr.SendStations, value);
	}
	public final String getCCEmps() {
		return this.GetValStringByKey(WorkOptAttr.CCEmps);
	}
	public final void setCCEmps(String value) throws Exception {
		SetValByKey(WorkOptAttr.CCEmps, value);
	}
	public final String getCCDepts() {
		return this.GetValStringByKey(WorkOptAttr.CCDepts);
	}
	public final void setCCDepts(String value) throws Exception {
		SetValByKey(WorkOptAttr.CCDepts, value);
	}
	public final String getCCStations() {
		return this.GetValStringByKey(WorkOptAttr.CCStations);
	}
	public final void setCCStations(String value) throws Exception {
		SetValByKey(WorkOptAttr.CCStations, value);
	}
	public final String getCCNote() {
		return this.GetValStringByKey(WorkOptAttr.CCNote);
	}
	public final void setCCNote(String value) throws Exception {
		SetValByKey(WorkOptAttr.CCNote, value);
	}

	public final String getSendRDT()  {
		return this.GetValStrByKey(WorkOptAttr.SendRDT);
	}
	public final void setSendRDT(String value) throws Exception {
		SetValByKey(WorkOptAttr.SendRDT, value);
	}

	public final String getStarterName()  {
		return this.GetValStrByKey(WorkOptAttr.StarterName);
	}
	public final void setStarterName(String value) throws Exception {
		SetValByKey(WorkOptAttr.StarterName, value);
	}
	public final String getStartRDT()  {
		return this.GetValStrByKey(WorkOptAttr.StartRDT);
	}
	public final void setStartRDT(String value) throws Exception {
		SetValByKey(WorkOptAttr.StartRDT, value);
	}

		///#endregion


		///#region 构造函数
	public WorkOpt()
	{
	}
	/** 
	 退回轨迹
	*/
	public WorkOpt(String mypk) throws Exception {
		this.setMyPK(mypk);
		this.Retrieve();
	}
	/**
	 * 重写基类方法
	 */
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_WorkOpt", "工作处理器");

		map.AddMyPK(true);

		map.AddTBInt(WorkOptAttr.WorkID, 0, "工作ID", false, true);
		map.AddTBInt(WorkOptAttr.NodeID, 0, "节点ID", false, true);
		map.AddTBInt(WorkOptAttr.ToNodeID, 0, "到达的节点ID", false, true);

		map.AddTBString(WorkOptAttr.EmpNo, null, "操作员", true, true, 0, 100, 10);
		map.AddTBString(WorkOptAttr.FlowNo, null, "FlowNo", false, false, 0, 10, 10);


		map.AddGroupAttr("发送", "");
		map.AddTBString(WorkOptAttr.SendEmps, null, "发送到人员", true, true, 0, 500, 10, true);
		map.AddTBString(WorkOptAttr.SendEmps + "T", null, "发送到人员", false, false, 0, 500, 10, true);

		map.AddTBString(WorkOptAttr.SendDepts, null, "发送到部门", true, true, 0, 500, 10, true);
		map.AddTBString(WorkOptAttr.SendDepts + "T", null, "发送到部门", false, false, 0, 500, 10, true);

		map.AddTBString(WorkOptAttr.SendStations, null, "发送到角色", true, true, 0, 100, 10, true);
		map.AddTBString(WorkOptAttr.SendStations + "T", null, "发送到角色", false, false, 0, 500, 10, true);
		map.AddTBString(WorkOptAttr.SendDeptLeaders, null, "到部门领导", true, false, 0, 500, 10, true);
		map.AddTBString(WorkOptAttr.SendDeptLeadersT, null, "到部门领导T", false, false, 0, 500, 10, true);
		map.AddTBStringDoc(WorkOptAttr.SendNote, null, "小纸条", true, true, true, 10);

		map.AddGroupAttr("抄送", "");
		map.AddTBString(WorkOptAttr.CCEmps, null, "抄送到人员", true, true, 0, 100, 10, true);
		map.AddTBString(WorkOptAttr.CCEmps + "T", null, "抄送到人员", false, false, 0, 500, 10, true);

		map.AddTBString(WorkOptAttr.CCDepts, null, "抄送到部门", true, true, 0, 100, 10, true);
		map.AddTBString(WorkOptAttr.CCDepts + "T", null, "抄送到部门", false, false, 0, 500, 10, true);

		map.AddTBString(WorkOptAttr.CCStations, null, "抄送到角色", true, true, 0, 100, 10, true);
		map.AddTBString(WorkOptAttr.CCStations + "T", null, "抄送到部门", false, false, 0, 500, 10, true);
		map.AddTBStringDoc(WorkOptAttr.CCNote, null, "抄送说明", true, true, true, 10);

		map.AddGroupAttr("工作信息", "");
		map.AddTBString(WorkOptAttr.Title, null, "标题", true, true, 0, 200, 10, true);
		map.AddTBString(WorkOptAttr.NodeName, null, "当前节点", true, true, 0, 500, 10, false, null);
		map.AddTBString(WorkOptAttr.ToNodeName, null, "到达节点", true, true, 0, 500, 10, false, null);

		map.AddTBString(WorkOptAttr.StarterName, null, "发起人", true, true, 0, 200, 10, false, null);
		map.AddTBDateTime(WorkOptAttr.StartRDT, null, "发起日期", true, true);

		map.AddTBInt(WorkOptAttr.ToNodeID, 0, "到达节点ID", false, false);

		map.AddTBString(WorkOptAttr.TodoEmps, null, "当前处理人", true, true, 0, 200, 10, true);
		map.AddTBString(WorkOptAttr.SenderName, null, "发送人", true, true, 0, 100, 10);

		map.AddTBString(WorkOptAttr.SendRDT, null, "发送日期", true, true, 0, 100, 10);
		map.AddTBString(WorkOptAttr.SendSDT, null, "限期", true, true, 0, 100, 10);
		map.AddTBInt(WorkOptAttr.WorkID, 0, "工作ID", true, true);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getStartRDT()) == true)
		{
			this.setStarterName(bp.web.WebUser.getDeptName() + "\\" + bp.web.WebUser.getName());
			this.setStartRDT(DataType.getCurrentDateTime());
		}
		return super.beforeUpdateInsertAction();
	}
	/** 
	 获取手动抄送时抄送人信息
	 
	 @return 
	*/
	public final DataTable GenerCCers() throws Exception {
		DataTable dt = new DataTable();
		dt.Columns.Add(new DataColumn("No", String.class));
		dt.Columns.Add(new DataColumn("Name", String.class));

		DataTable mydt = new DataTable();
		DataRow dr = null;
		String sql = "";
		//抄送到人员
		if (DataType.IsNullOrEmpty(this.getCCEmps()) == false)
		{
			String[] empNos = this.getCCEmps().split("[,]", -1);
			String[] empNames = this.GetValStrByKey(WorkOptAttr.CCEmps + "T").split("[,]", -1);
			for (int i = 0; i < empNos.length; i++)
			{
				dr = dt.NewRow();
				dr.setValue("No", empNos[i]);
				dr.setValue("Name", empNames[i]);
				dt.Rows.add(dr);
			}
		}
		//抄送到部门
		if (DataType.IsNullOrEmpty(this.getCCDepts()) == false)
		{
			sql = "SELECT " + bp.sys.base.Glo.getUserNo() + ",Name FROM Port_Emp A, Port_DeptEmp B  WHERE A." + bp.sys.base.Glo.getUserNoWhitOutAS() + "= B.FK_Emp AND B.FK_Dept IN(" + bp.port.Glo.GenerWhereInSQL(this.getCCDepts()) + ")";
			mydt = DBAccess.RunSQLReturnTable(sql);
			for (DataRow mydr : mydt.Rows)
			{
				dr = dt.NewRow();
				dr.setValue("No", mydr.getValue("No"));
				dr.setValue("Name", mydr.getValue("Name"));
				dt.Rows.add(dr);
			}
		}
		//抄送到岗位
		if (DataType.IsNullOrEmpty(this.getCCStations()) == false)
		{
			sql = "SELECT " + bp.sys.base.Glo.getUserNo() + ",Name FROM Port_Emp A, Port_DeptEmpStation B  WHERE A.No= B.FK_Emp AND B.FK_Station IN(" + bp.port.Glo.GenerWhereInSQL(this.getCCStations()) + ")";
			mydt = DBAccess.RunSQLReturnTable(sql);
			for (DataRow mydr : mydt.Rows)
			{
				dr = dt.NewRow();
				dr.setValue("No", mydr.getValue("No"));
				dr.setValue("Name", mydr.getValue("Name"));
				dt.Rows.add(dr);
			}
		}
		//将dt中的重复数据过滤掉  
//		DataView myDataView = new DataView(dt);
//		//此处可加任意数据项组合
//		String[] strComuns = {"No", "Name"};
//		return myDataView.ToTable(true, strComuns);
		return dt;
	}
}
