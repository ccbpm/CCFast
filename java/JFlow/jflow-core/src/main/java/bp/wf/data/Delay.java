package bp.wf.data;

import bp.en.*;
import bp.en.Map;
import bp.wf.*;

/** 
 逾期流程
*/
public class Delay extends EntityMyPK
{

		///#region 基本属性
	/** 
	 workid
	*/
	public final long getWorkID()  {
		return this.GetValInt64ByKey(DelayAttr.WorkID);
	}
	public final void setWorkID(long value){
		this.SetValByKey(DelayAttr.WorkID, value);
	}
	/** 
	 发起人
	*/
	public final String getStarter()  {
		return this.GetValStringByKey(DelayAttr.Starter);
	}
	public final void setStarter(String value){
		this.SetValByKey(DelayAttr.Starter, value);
	}
	/** 
	 发起人名称
	*/
	public final String getStarterName()  {
		return this.GetValStringByKey(DelayAttr.StarterName);
	}
	public final void setStarterName(String value){
		this.SetValByKey(DelayAttr.StarterName, value);
	}
	/** 
	 流程状态
	*/
	public final int getWFStateInt()  {
		return this.GetValIntByKey(DelayAttr.WFState);
	}
	public final void setWFStateInt(int value){
		this.SetValByKey(DelayAttr.WFState, value);
	}
	/** 
	 流程状态
	*/
	public final WFState getWFState() {
		return WFState.forValue(this.GetValIntByKey(DelayAttr.WFState));
	}
	public final void setWFState(WFState value){
		this.SetValByKey(DelayAttr.WFState, value.getValue());
	}
	/** 
	 部门编号
	*/
	public final String getFK_Dept()  {
		return this.GetValStringByKey(DelayAttr.FK_Dept);
	}
	public final void setFK_Dept(String value){
		this.SetValByKey(DelayAttr.FK_Dept, value);
	}
	/** 
	 部门名称
	*/
	public final String getDeptName()  {
		return this.GetValStringByKey(DelayAttr.DeptName);
	}
	public final void setDeptName(String value){
		this.SetValByKey(DelayAttr.DeptName, value);
	}
	/** 
	 流程编号
	*/
	public final String getFK_Flow()  {
		return this.GetValStringByKey(DelayAttr.FK_Flow);
	}
	public final void setFK_Flow(String value){
		this.SetValByKey(DelayAttr.FK_Flow, value);
	}
	/** 
	 流程名称
	*/
	public final String getFlowName()  {
		return this.GetValStringByKey(DelayAttr.FlowName);
	}
	public final void setFlowName(String value){
		this.SetValByKey(DelayAttr.FlowName, value);
	}
	/** 
	 当前节点
	*/
	public final int getFK_Node()  {
		return this.GetValIntByKey(DelayAttr.FK_Node);
	}
	public final void setNodeID(int value){
		this.SetValByKey(DelayAttr.FK_Node, value);
	}
	/** 
	 节点名称
	*/
	public final String getNodeName()  {
		return this.GetValStringByKey(DelayAttr.NodeName);
	}
	public final void setNodeName(String value){
		this.SetValByKey(DelayAttr.NodeName, value);
	}
	/** 
	 标题
	*/
	public final String getTitle()  {
		return this.GetValStringByKey(DelayAttr.Title);
	}
	public final void setTitle(String value){
		this.SetValByKey(DelayAttr.Title, value);
	}
	/** 
	 记录日期
	*/
	public final String getRDT()  {
		return this.GetValStringByKey(DelayAttr.RDT);
	}
	public final void setRDT(String value){
		this.SetValByKey(DelayAttr.RDT, value);
	}
	/** 
	 应完成日期
	*/
	public final String getSDT()  {
		return this.GetValStringByKey(DelayAttr.SDT);
	}
	public final void setSDT(String value){
		this.SetValByKey(DelayAttr.SDT, value);
	}
	/** 
	 人员编号
	*/
	public final String getFK_Emp()  {
		return this.GetValStringByKey(DelayAttr.FK_Emp);
	}
	public final void setFK_Emp(String value){
		this.SetValByKey(DelayAttr.FK_Emp, value);
	}
	/** 
	 fid
	*/
	public final long getFID()  {
		return this.GetValInt64ByKey(DelayAttr.FID);
	}
	public final void setFID(long value){
		this.SetValByKey(DelayAttr.FID, value);
	}
	/** 
	 流程类别
	*/
	public final String getFK_FlowSort()  {
		return this.GetValStringByKey(DelayAttr.FK_FlowSort);
	}
	public final void setFK_FlowSort(String value){
		this.SetValByKey(DelayAttr.FK_FlowSort, value);
	}
	/** 
	 系统类型
	*/
	public final String getSysType()  {
		return this.GetValStringByKey(DelayAttr.SysType);
	}
	public final void setSysType(String value){
		this.SetValByKey(DelayAttr.SysType, value);
	}
	/** 
	 应完成日期
	*/
	public final String getSDTOfNode()  {
		return this.GetValStringByKey(DelayAttr.SDTOfNode);
	}
	public final void setSDTOfNode(String value){
		this.SetValByKey(DelayAttr.SDTOfNode, value);
	}
	/** 
	 催办次数
	*/
	public final int getPressTimes()  {
		return this.GetValIntByKey(DelayAttr.PressTimes);
	}
	public final void setPressTimes(int value){
		this.SetValByKey(DelayAttr.PressTimes, value);
	}
	/** 
	 单据编号
	*/
	public final String getBillNo()  {
		return this.GetValStringByKey(DelayAttr.BillNo);
	}
	public final void setBillNo(String value){
		this.SetValByKey(DelayAttr.BillNo, value);
	}
	/** 
	 备注
	*/
	public final String getFlowNote()  {
		return this.GetValStringByKey(DelayAttr.FlowNote);
	}
	public final void setFlowNote(String value){
		this.SetValByKey(DelayAttr.FlowNote, value);
	}
	/** 
	 待办处理人
	*/
	public final String getTodoEmps()  {
		return this.GetValStringByKey(DelayAttr.TodoEmps);
	}
	public final void setTodoEmps(String value){
		this.SetValByKey(DelayAttr.TodoEmps, value);
	}
	/** 
	 发送人
	*/
	public final String getSender()  {
		return this.GetValStringByKey(DelayAttr.Sender);
	}
	public final void setSender(String value){
		this.SetValByKey(DelayAttr.Sender, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsDelete = false;
		uac.IsInsert = false;
		uac.IsUpdate=false;
		uac.IsView = true;
		return uac;
	}
	/** 
	 逾期流程
	*/
	public Delay()
	{
	}
	/** 
	 
	 
	 param pk
	*/
	public Delay(String pk) throws Exception
	{
		super(pk);
	}

		///#endregion


		///#region Map
	/** 
	 EnMap
	*/
	@Override
	public bp.en.Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("V_WF_Delay", "逾期流程");
		map.setEnType(EnType.View);

		map.AddMyPK(true);

		map.AddTBInt(DelayAttr.WorkID, 0, "工作ID", true, true);
		map.AddTBString(DelayAttr.FK_Emp, null, "待办人", true, true, 0, 100, 40);
		map.AddTBInt(DelayAttr.FK_Node, 0, "节点", false, true);

		map.AddTBString(DelayAttr.Title, null, "标题", true, true, 0, 50, 100);
		map.AddTBString(DelayAttr.Starter, null, "Starter", false, false, 0, 100, 5);
		map.AddTBString(DelayAttr.StarterName, null, "发起人", true, true, 0, 50, 30);

		map.AddDDLSysEnum(DelayAttr.WFState, 0, "状态", true, true, DelayAttr.WFState);

		map.AddTBString(DelayAttr.FK_Dept, null, "隶属部门", false, true,0,50,50);
		map.AddTBString(DelayAttr.DeptName, null, "隶属部门", true, true, 0, 50, 40);
		map.AddDDLEntities(DelayAttr.FK_Flow, null, "流程", new Flows(), false);
		  //  map.AddTBString(DelayAttr.FlowName, null, "流程名称", true, true, 0, 50, 40);
		map.AddTBString(DelayAttr.NodeName, null, "节点名称", true, true, 0, 50, 40);

		 //   map.AddTBInt(DelayAttr.WorkerDept, 0, "工作人员部门", false, true);
		map.AddTBString(DelayAttr.RDT, null, "接受日期", true, true, 0, 50, 30);
		map.AddTBString(DelayAttr.SDT, null, "应完成日期", true, true, 0, 50, 50);
		map.AddTBInt(DelayAttr.FID, 0, "FID", false, false);
		map.AddTBString(DelayAttr.FK_FlowSort, null, "流程类别", false, true,0,50,50);
		map.AddTBString(DelayAttr.SysType, null, "SysType", false, true, 0, 50, 5);
		   // map.AddTBString(DelayAttr.SDTOfNode, null, "节点应完成日期", true, true, 0, 50, 70);
		map.AddTBString(DelayAttr.PressTimes, null, "催办次数", false, true, 0, 50, 5);
		map.AddTBString(DelayAttr.BillNo, null, "单据号", true, true, 0, 50, 5);
		map.AddTBString(DelayAttr.FlowNote, null, "FlowNote", false, true, 0, 50, 5);
		map.AddTBString(DelayAttr.TodoEmps, null, "待办人员", true, true, 0, 50, 5);
		map.AddTBString(DelayAttr.Sender, null, "发送者", true, true, 0, 50, 50);

			//查询条件.
		//map.AddSearchAttr(DelayAttr.WFState, 130);
		map.AddSearchAttr(DelayAttr.FK_Flow, 130);


		RefMethod rm = new RefMethod();
		rm.Title = "打开轨迹";
		rm.ClassMethodName = this.toString() + ".DoOpenTrack";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		map.AddRefMethod(rm);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	public final String DoOpenTrack() {
		return "/WF/WFRpt.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFK_Flow();
	}
}