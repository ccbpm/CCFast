package bp.ccbill;

import bp.en.*; import bp.en.Map;
import bp.wf.*;
import bp.*;
import java.util.*;

/** 
 单据控制表
*/
public class GenerBill extends Entity
{

		///#region 属性
	/** 
	 主键
	*/
	@Override
	public String getPK()
	{
		return GenerBillAttr.WorkID;
	}
	/** 
	 备注
	*/
	public final String getFlowNote()  {
		return this.GetValStrByKey(GenerBillAttr.FlowNote);
	}
	public final void setFlowNote(String value) throws Exception {
		SetValByKey(GenerBillAttr.FlowNote, value);
	}

	/** 
	 BillNo
	*/
	public final String getBillNo()  {
		return this.GetValStrByKey(GenerBillAttr.BillNo);
	}
	public final void setBillNo(String value) throws Exception {
		SetValByKey(GenerBillAttr.BillNo, value);
	}
	/** 
	 单据ID
	*/
	public final String getFrmID()  {
		return this.GetValStrByKey(GenerBillAttr.FrmID);
	}
	public final void setFrmID(String value) throws Exception {
		SetValByKey(GenerBillAttr.FrmID, value);
	}
	/** 
	 单据单据
	*/
	public final String getFrmName()  {
		return this.GetValStrByKey(GenerBillAttr.FrmName);
	}
	public final void setFrmName(String value) throws Exception {
		SetValByKey(GenerBillAttr.FrmName, value);
	}
	/** 
	 优先级
	*/
	public final int getPRI() {
		return this.GetValIntByKey(GenerBillAttr.PRI);
	}
	public final void setPRI(int value) throws Exception {
		SetValByKey(GenerBillAttr.PRI, value);
	}
	/** 
	 待办人员数量
	*/
	public final int getTodoEmpsNum() {
		return this.GetValIntByKey(GenerBillAttr.TodoEmpsNum);
	}
	public final void setTodoEmpsNum(int value) throws Exception {
		SetValByKey(GenerBillAttr.TodoEmpsNum, value);
	}
	/// <summary>
	/// 当前工作到的节点
	/// </summary>
	public final int getCurrIdx() {
		return this.GetValIntByKey(GenerBillAttr.CurrIdx);
	}
	public final void setCurrIdx(int value) throws Exception {
		SetValByKey(GenerBillAttr.CurrIdx, value);
	}
	public final String getCurrCheckerNos()  {
		return this.GetValStrByKey(GenerBillAttr.CurrCheckerNos);
	}
	public final void setCurrCheckerNos(String value) throws Exception {
		SetValByKey(GenerBillAttr.CurrCheckerNos, value);
	}
	public final String getCurrCheckerNames()  {
		return this.GetValStrByKey(GenerBillAttr.CurrCheckerNames);
	}
	public final void setCurrCheckerNames(String value) throws Exception {
		SetValByKey(GenerBillAttr.CurrCheckerNames, value);
	}
	/** 
	 待办人员列表
	*/
	public final String getTodoEmps()  {
		return this.GetValStrByKey(GenerBillAttr.TodoEmps);
	}
	public final void setTodoEmps(String value) throws Exception {
		SetValByKey(GenerBillAttr.TodoEmps, value);
	}
	/** 
	 参与人
	*/
	public final String getEmps()  {
		return this.GetValStrByKey(GenerBillAttr.Emps);
	}
	public final void setEmps(String value) throws Exception {
		SetValByKey(GenerBillAttr.Emps, value);
	}
	/** 
	 状态
	*/
	public final TaskSta getTaskSta() {
		return TaskSta.forValue(this.GetValIntByKey(GenerBillAttr.TaskSta));
	}
	public final void setTaskSta(TaskSta value) throws Exception {
		SetValByKey(GenerBillAttr.TaskSta, value.getValue());
	}
	/** 
	 类别编号
	*/
	public final String getFrmTreeNo()  {
		return this.GetValStrByKey(GenerBillAttr.FK_FrmTree);
	}
	public final void setFrmTreeNo(String value) throws Exception {
		SetValByKey(GenerBillAttr.FK_FrmTree, value);
	}
	/** 
	 部门编号
	*/
	public final String getDeptNo()  {
		return this.GetValStrByKey(GenerBillAttr.DeptNo);
	}
	public final void setDeptNo(String value) throws Exception {
		SetValByKey(GenerBillAttr.DeptNo, value);
	}
	/** 
	 标题
	*/
	public final String getTitle()  {
		return this.GetValStrByKey(GenerBillAttr.Title);
	}
	public final void setTitle(String value) throws Exception {
		SetValByKey(GenerBillAttr.Title, value);
	}
	/** 
	 客户编号
	*/
	public final String getGuestNo()  {
		return this.GetValStrByKey(GenerBillAttr.GuestNo);
	}
	public final void setGuestNo(String value) throws Exception {
		SetValByKey(GenerBillAttr.GuestNo, value);
	}
	/** 
	 客户名称
	*/
	public final String getGuestName()  {
		return this.GetValStrByKey(GenerBillAttr.GuestName);
	}
	public final void setGuestName(String value) throws Exception {
		SetValByKey(GenerBillAttr.GuestName, value);
	}
	/** 
	 产生时间
	*/
	public final String getRDT()  {
		return this.GetValStrByKey(GenerBillAttr.RDT);
	}
	public final void setRDT(String value) throws Exception {
		SetValByKey(GenerBillAttr.RDT, value);
	}
	/** 
	 节点应完成时间
	*/
	public final String getSDTOfNode()  {
		return this.GetValStrByKey(GenerBillAttr.SDTOfNode);
	}
	public final void setSDTOfNode(String value) throws Exception {
		SetValByKey(GenerBillAttr.SDTOfNode, value);
	}
	/** 
	 单据应完成时间
	*/
	public final String getSDTOfFlow()  {
		return this.GetValStrByKey(GenerBillAttr.SDTOfFlow);
	}
	public final void setSDTOfFlow(String value) throws Exception {
		SetValByKey(GenerBillAttr.SDTOfFlow, value);
	}
	/** 
	 单据ID
	*/
	public final long getWorkID() {
		return this.GetValInt64ByKey(GenerBillAttr.WorkID);
	}
	public final void setWorkID(long value) throws Exception {
		SetValByKey(GenerBillAttr.WorkID, value);
	}
	/** 
	 主线程ID
	*/
	public final long getFID() {
		return this.GetValInt64ByKey(GenerBillAttr.FID);
	}
	public final void setFID(long value) throws Exception {
		SetValByKey(GenerBillAttr.FID, value);
	}
	/** 
	 父节点单据编号.
	*/
	public final long getPWorkID() {
		return this.GetValInt64ByKey(GenerBillAttr.PWorkID);
	}
	public final void setPWorkID(long value) throws Exception {
		SetValByKey(GenerBillAttr.PWorkID, value);
	}
	/** 
	 父单据调用的节点
	*/
	public final int getPNodeID() {
		return this.GetValIntByKey(GenerBillAttr.PNodeID);
	}
	public final void setPNodeID(int value) throws Exception {
		SetValByKey(GenerBillAttr.PNodeID, value);
	}
	/** 
	 PFrmID
	*/
	public final String getPFrmID()  {
		return this.GetValStrByKey(GenerBillAttr.PFrmID);
	}
	public final void setPFrmID(String value) throws Exception {
		SetValByKey(GenerBillAttr.PFrmID, value);
	}
	/** 
	 吊起子单据的人员
	*/
	public final String getPEmp()  {
		return this.GetValStrByKey(GenerBillAttr.PEmp);
	}
	public final void setPEmp(String value) throws Exception {
		SetValByKey(GenerBillAttr.PEmp, value);
	}
	/** 
	 发起人
	*/
	public final String getStarter()  {
		return this.GetValStrByKey(GenerBillAttr.Starter);
	}
	public final void setStarter(String value) throws Exception {
		SetValByKey(GenerBillAttr.Starter, value);
	}
	/** 
	 发起人名称
	*/
	public final String getStarterName()  {
		return this.GetValStrByKey(GenerBillAttr.StarterName);
	}
	public final void setStarterName(String value)  {
		this.SetValByKey(GenerBillAttr.StarterName, value);
	}
	/** 
	 发起人部门名称
	*/
	public final String getDeptName()  {
		return this.GetValStrByKey(GenerBillAttr.DeptName);
	}
	public final void setDeptName(String value)  {
		this.SetValByKey(GenerBillAttr.DeptName, value);
	}
	/** 
	 当前节点名称
	*/
	public final String getIdxName()  {
		return this.GetValStrByKey(GenerBillAttr.IdxName);
	}
	public final void setIdxName(String value)  {
		this.SetValByKey(GenerBillAttr.IdxName, value);
	}
	/** 
	 当前工作到的节点
	*/
	public final int getIdx() {
		return this.GetValIntByKey(GenerBillAttr.Idx);
	}
	public final void setIdx(int value) throws Exception {
		SetValByKey(GenerBillAttr.Idx, value);
	}

	/** 
	 工作单据状态
	*/
	public final BillState getBillState() {
		return BillState.forValue(this.GetValIntByKey(GenerBillAttr.BillState));
	}
	public final void setBillState(BillState value) throws Exception {
		//if (value == BillState.)
		//    SetValByKey(GenerBillAttr.BillSta, (int)BillSta.Complete);
		//else if (value == bp.wf.BillState.Delete)
		//    SetValByKey(GenerBillAttr.BillSta, (int)BillSta.Etc);
		//else
		//    SetValByKey(GenerBillAttr.BillSta, (int)BillSta.Runing);

		SetValByKey(GenerBillAttr.BillState, value.getValue());
	}
	/** 
	 单据状态
	*/
	public final String getBillStateText() {
		return this.GetValRefTextByKey(GenerBillAttr.BillState);
	}
	/** 
	 GUID
	*/
	public final String getGUID()  {
		return this.GetValStrByKey(GenerBillAttr.GUID);
	}
	public final void setGUID(String value) throws Exception {
		SetValByKey(GenerBillAttr.GUID, value);
	}

		///#endregion


		///#region 权限控制.
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getNo().equals("admin") == true)
		{
			uac.IsDelete = false;
			uac.IsUpdate = true;
			return uac;
		}
		uac.Readonly();
		return uac;
	}

		///#endregion 权限控制.


		///#region 构造方法
	/** 
	 单据控制表
	*/
	public GenerBill()
	{
	}
	/** 
	 单据控制表
	 
	 @param workid workID
	*/
	public GenerBill(long workid) throws Exception {
		this.setWorkID(workid);
		this.Retrieve();
	}
	/** 
	 EnMap
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("Frm_GenerBill", "单据控制表");

		map.AddTBIntPK(GenerBillAttr.WorkID, 0, "WorkID", true, true);

		map.AddTBString(GenerBillAttr.FK_FrmTree, null, "单据类别", true, false, 0, 100, 10);
		map.AddTBString(GenerBillAttr.FrmID, null, "单据ID", true, false, 0, 100, 10);
		map.AddTBString(GenerBillAttr.FrmName, null, "单据名称", true, false, 0, 200, 10);

		map.AddTBString(GenerBillAttr.BillNo, null, "单据编号", true, false, 0, 100, 10);
		map.AddTBString(GenerBillAttr.Title, null, "标题", true, false, 0, 1000, 10);
		map.AddDDLSysEnum(GenerBillAttr.BillSta, 0, "状态(简)", true, false, GenerBillAttr.BillSta, "@0=运行中@1=已完成@2=其他");
		map.AddDDLSysEnum(GenerBillAttr.BillState, 0, "单据状态", true, false, GenerBillAttr.BillState, "@0=空白@1=草稿@2=编辑中@100=归档");

		map.AddTBString(GenerBillAttr.Starter, null, "创建人", true, false, 0, 200, 10);
		map.AddTBString(GenerBillAttr.StarterName, null, "创建人名称", true, false, 0, 200, 10);
		map.AddTBString(GenerBillAttr.Sender, null, "发送人", true, false, 0, 200, 10);

		map.AddTBString(GenerBillAttr.CurrCheckerNos, null, "当前审核人编号s", true, false, 0, 200, 10);
		map.AddTBString(GenerBillAttr.CurrCheckerNames, null, "当前审核人名称s", true, false, 0, 200, 10);
		map.AddTBInt(GenerBillAttr.CurrIdx, 0, "当前审核人步骤", true, false);

		map.AddTBString("Msg", null, "信息", true, false, 0, 300, 10);

		map.AddTBDateTime(GenerBillAttr.RDT, "记录日期", true, true);
		map.AddTBDateTime(GenerBillAttr.SendDT, "单据活动时间", true, true);
		map.AddTBString(GenerBillAttr.DeptNo, null, "部门", true, false, 0, 100, 10);
		map.AddTBString(GenerBillAttr.DeptName, null, "部门名称", true, false, 0, 100, 10);
		map.AddTBInt(GenerBillAttr.PRI, 1, "优先级", true, true);

		map.AddTBDateTime(GenerBillAttr.SDTOfNode, "节点应完成时间", true, true);
		map.AddTBDateTime(GenerBillAttr.SDTOfFlow, "单据应完成时间", true, true);

		//父子单据信息.
		map.AddTBString(GenerBillAttr.PFrmID, null, "父单据编号", true, false, 0, 3, 10);
		map.AddTBInt(GenerBillAttr.PWorkID, 0, "父单据ID", true, true);
		map.AddDDLSysEnum(GenerBillAttr.TSpan, 0, "时间段", true, false, GenerBillAttr.TSpan, "@0=本周@1=上周@2=上上周@3=更早");

		//参数.
		map.AddTBString(GenerBillAttr.AtPara, null, "参数(单据运行设置临时存储的参数)", true, false, 0, 2000, 10);
		map.AddTBString(GenerBillAttr.Emps, null, "参与人", true, false, 0, 4000, 10);
		map.AddTBString(GenerBillAttr.EmpOfAll, null, "所有人员", true, false, 0, 4000, 10);
		map.AddTBString(GenerBillAttr.GUID, null, "GUID", false, false, 0, 36, 10);
		map.AddTBString(GenerBillAttr.FK_NY, null, "年月", false, false, 0, 7, 7);

		//设置参数.
		map.AddTBAtParas(3000);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

		///#region 方法操作.
		@Override
		protected  boolean beforeUpdateInsertAction() throws Exception {
			this.SetValByKey(GenerBillAttr.SendDT, bp.da.DataType.getCurrentDateTime());
			return super.beforeUpdateInsertAction();
		}
		///#endregion 方法操作.
}
