package bp.wf;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.port.*;
import bp.web.*;

/** 
 工作者列表
*/
public class GenerWorkerList extends Entity
{

		///#region 参数属性.
	/** 
	 是否会签
	*/
	public final boolean getItIsHuiQian() {
		return this.GetParaBoolen(GenerWorkerListAttr.IsHuiQian);
	}
	public final void setItIsHuiQian(boolean value)  {
		this.SetPara(GenerWorkerListAttr.IsHuiQian, value);
	}
	/** 
	 分组标记
	*/
	public final String getGroupMark() {
		return this.GetParaString(GenerWorkerListAttr.GroupMark);
	}
	public final void setGroupMark(String value)  {
		this.SetPara(GenerWorkerListAttr.GroupMark, value);
	}
	/** 
	 表单ID(对于动态表单树有效.)
	*/
	public final String getFrmIDs() {
		return this.GetParaString(GenerWorkerListAttr.FrmIDs);
	}
	public final void setFrmIDs(String value)  {
		this.SetPara(GenerWorkerListAttr.FrmIDs, value);
	}

		///#endregion 参数属性.


		///#region 基本属性
	/** 
	 谁来执行它
	*/
	public final int getWhoExeIt()  {
		return this.GetValIntByKey(GenerWorkerListAttr.WhoExeIt);
	}
	public final void setWhoExeIt(int value) throws Exception
	{
		SetValByKey(GenerWorkerListAttr.WhoExeIt, value);
	}
	public final int getPressTimes() {
		return this.GetParaInt(GenerWorkerListAttr.PressTimes, 0);
	}
	public final void setPressTimes(int value) throws Exception {
		SetPara(GenerWorkerListAttr.PressTimes, value);
	}
	/** 
	 优先级
	*/
	public final int getPRI()  {
		return this.GetValIntByKey(GenerWorkerListAttr.PRI);
	}
	public final void setPRI(int value) throws Exception
	{
		SetValByKey(GenerWorkerListAttr.PRI, value);
	}

	/** 
	 Idx
	*/
	public final int getIdx()  {
		return this.GetValIntByKey(GenerWorkerListAttr.Idx);
	}
	public final void setIdx(int value) throws Exception
	{
		SetValByKey(GenerWorkerListAttr.Idx, value);
	}
	/** 
	 WorkID
	*/
	@Override
	public String getPK()
	{
		return "WorkID,FK_Emp,FK_Node";
	}
	/** 
	 是否可用(在分配工作时有效)
	*/
	public final boolean getItIsEnable()  {
		return this.GetValBooleanByKey(GenerWorkerListAttr.IsEnable);
	}
	public final void setItIsEnable(boolean value){
		this.SetValByKey(GenerWorkerListAttr.IsEnable, value);
	}
	/** 
	 是否通过(对审核的会签节点有效)
	*/
	public final boolean getItIsPass()  {
		return this.GetValBooleanByKey(GenerWorkerListAttr.IsPass);
	}
	public final void setItIsPass(boolean value){
		this.SetValByKey(GenerWorkerListAttr.IsPass, value);
	}
	/** 
	 0=未处理.
	 1=已经通过.
	 -2=  标志该节点是干流程人员处理的节点，目的为了让分流节点的人员可以看到待办.
	*/
	public final int getPassInt()  {
		return this.GetValIntByKey(GenerWorkerListAttr.IsPass);
	}
	public final void setPassInt(int value){
		this.SetValByKey(GenerWorkerListAttr.IsPass, value);
	}
	public final boolean getItIsRead()  {
		return this.GetValBooleanByKey(GenerWorkerListAttr.IsRead);
	}
	public final void setItIsRead(boolean value){
		this.SetValByKey(GenerWorkerListAttr.IsRead, value);
	}
	/** 
	 WorkID
	*/
	public final long getWorkID()  {
		return this.GetValInt64ByKey(GenerWorkerListAttr.WorkID);
	}
	public final void setWorkID(long value){
		this.SetValByKey(GenerWorkerListAttr.WorkID, value);
	}
	/** 
	 Node
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(GenerWorkerListAttr.FK_Node);
	}
	public final void setNodeID(int value){
		this.SetValByKey(GenerWorkerListAttr.FK_Node, value);
	}
	/** 
	 部门名称
	*/
	public final String getDeptName() {
		return this.GetParaString(GenerWorkerListAttr.DeptName);
	}
	public final void setDeptName(String value)  {
		this.SetPara(GenerWorkerListAttr.DeptName, value);
	}
	public final String getStaName() {
		return this.GetParaString("StaName");
	}
	public final void setStaName(String value)  {
		this.SetPara("StaName", value);
	}
	public final String getDeptNo() {

		return this.GetValStrByKey(GenerWorkerListAttr.FK_Dept);
	}
	public final void setDeptNo(String value){
		this.SetValByKey(GenerWorkerListAttr.FK_Dept, value);
	}
	/** 
	 发送人
	*/
	public final String getSender()  {
		return this.GetValStrByKey(GenerWorkerListAttr.Sender);
	}
	public final void setSender(String value){
		this.SetValByKey(GenerWorkerListAttr.Sender, value);
	}
	/** 
	 节点名称
	*/
	public final String getNodeName()  {
		return this.GetValStrByKey(GenerWorkerListAttr.NodeName);
	}
	public final void setNodeName(String value){
		this.SetValByKey(GenerWorkerListAttr.NodeName, value);
	}
	/** 
	 流程ID
	*/
	public final long getFID()  {
		return this.GetValInt64ByKey(GenerWorkerListAttr.FID);
	}
	public final void setFID(long value){
		this.SetValByKey(GenerWorkerListAttr.FID, value);
	}
	/** 
	 工作人员
	*/
	public final Emp getHisEmp() throws Exception {
		return new Emp(this.getEmpNo());
	}
	/** 
	 发送日期
	*/
	public final String getRDT()  {
		return this.GetValStringByKey(GenerWorkerListAttr.RDT);
	}
	public final void setRDT(String value) throws Exception {
		//this.SetValByKey(GenerWorkerListAttr.RDT, value);
		this.SetValByKey(GenerWorkerListAttr.RDT, DataType.getCurrentDateTimess());

	}
	/** 
	 完成时间
	*/
	public final String getCDT()  {
		return this.GetValStringByKey(GenerWorkerListAttr.CDT);
	}
	public final void setCDT(String value){
		this.SetValByKey(GenerWorkerListAttr.CDT, value);
	}
	/** 
	 应该完成日期
	*/
	public final String getSDT()  {
		return this.GetValStringByKey(GenerWorkerListAttr.SDT);
	}
	public final void setSDT(String value){
		this.SetValByKey(GenerWorkerListAttr.SDT, value);
	}
	/** 
	 警告日期
	*/
	public final String getDTOfWarning()  {
		return this.GetValStringByKey(GenerWorkerListAttr.DTOfWarning);
	}
	public final void setDTOfWarning(String value){
		this.SetValByKey(GenerWorkerListAttr.DTOfWarning, value);
	}
	/** 
	 人员
	*/
	public final String getEmpNo()  {
		return this.GetValStringByKey(GenerWorkerListAttr.FK_Emp);
	}
	public final void setEmpNo(String value){
		this.SetValByKey(GenerWorkerListAttr.FK_Emp, value);
	}
	/** 
	 人员名称
	*/
	public final String getEmpName()  {
		return this.GetValStrByKey(GenerWorkerListAttr.EmpName);
	}
	public final void setEmpName(String value){
		this.SetValByKey(GenerWorkerListAttr.EmpName, value);
	}
	/** 
	 流程编号
	*/
	public final String getFlowNo()  {
		return this.GetValStringByKey(GenerWorkerListAttr.FK_Flow);
	}
	public final void setFlowNo(String value){
		this.SetValByKey(GenerWorkerListAttr.FK_Flow, value);
	}

	public final String getGuestNo()  {
		return this.GetValStringByKey(GenerWorkerListAttr.GuestNo);
	}
	public final void setGuestNo(String value){
		this.SetValByKey(GenerWorkerListAttr.GuestNo, value);
	}
	public final String getGuestName()  {
		return this.GetValStringByKey(GenerWorkerListAttr.GuestName);
	}
	public final void setGuestName(String value){
		this.SetValByKey(GenerWorkerListAttr.GuestName, value);
	}

		///#endregion


		///#region 挂起属性
	/** 
	 挂起时间
	*/
	public final String getDTOfHungup()  {
		return this.GetValStringByKey(GenerWorkerListAttr.DTOfHungup);
	}
	public final void setDTOfHungup(String value){
		this.SetValByKey(GenerWorkerListAttr.DTOfHungup, value);
	}
	/** 
	 解除挂起时间
	*/
	public final String getDTOfUnHungup()  {
		return this.GetValStringByKey(GenerWorkerListAttr.DTOfUnHungup);
	}
	public final void setDTOfUnHungup(String value){
		this.SetValByKey(GenerWorkerListAttr.DTOfUnHungup, value);
	}
	/** 
	 挂起次数
	*/
	public final int getHungupTimes() {
		return this.GetParaInt(GenerWorkerListAttr.HungupTimes, 0);
	}
	public final void setHungupTimes(int value)  {
		this.SetPara(GenerWorkerListAttr.HungupTimes, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 主键
	*/
	@Override
	public String getPKField()
	{
		return "WorkID,FK_Emp,FK_Node";
	}
	/** 
	 工作者
	*/
	public GenerWorkerList()
	{
	}
	public GenerWorkerList(long workid, int nodeID, String empNo) throws Exception {
		this.setWorkID(workid);
		this.setNodeID(nodeID);
		this.setEmpNo(empNo);
		this.Retrieve();
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_GenerWorkerlist", "工作者");

		map.IndexField = GenerWorkerListAttr.WorkID;

		//三个主键: WorkID，FK_Emp，FK_Node
		map.AddTBIntPK(GenerWorkerListAttr.WorkID, 0, "工作ID", true, true);
		map.AddTBStringPK(GenerWorkerListAttr.FK_Emp, null, "人员", true, false, 0, 100, 100);
		map.AddTBIntPK(GenerWorkerListAttr.FK_Node, 0, "节点ID", true, false);

		//干流程WorkID.
		map.AddTBInt(GenerWorkerListAttr.FID, 0, "流程ID", true, false);

		map.AddTBString(GenerWorkerListAttr.EmpName, null, "人员名称", true, false, 0, 30, 100);
		map.AddTBString(GenerWorkerListAttr.NodeName, null, "节点名称", true, false, 0, 100, 100);

		map.AddTBString(GenerWorkerListAttr.FK_Flow, null, "流程", true, false, 0, 5, 100);
		//为tianyu集团，处理工作的岗位编号.
		map.AddTBString(GenerWorkerListAttr.StaNo, null, "岗位编号", true, false, 0, 100, 100);
		//为tianyu集团，处理工作的部门编号.
		map.AddTBString(GenerWorkerListAttr.FK_Dept, null, "部门", true, false, 0, 100, 100);

		//如果是流程属性来控制的就按流程属性来计算。
		map.AddTBDateTime(GenerWorkerListAttr.SDT, "应完成日期", false, false);
		map.AddTBDateTime(GenerWorkerListAttr.DTOfWarning, "警告日期", false, false);
		map.AddTBDateTime(GenerWorkerListAttr.RDT, "记录时间(接受工作日期)", false, false);

		//完成日期.
		map.AddTBDateTime(GenerWorkerListAttr.CDT, "完成时间", false, false);

		//用于分配工作.
		map.AddTBInt(GenerWorkerListAttr.IsEnable, 1, "是否可用", true, true);

		//add for 上海 2012-11-30 
		map.AddTBInt(GenerWorkerListAttr.IsRead, 0, "是否读取", true, true);

		//是否通过？ 0=未通过 1=已经完成 , xxx标识其他状态.
		map.AddTBInt(GenerWorkerListAttr.IsPass, 0, "是否通过(对合流节点有效)", false, false);

		// 谁执行它？ 0=手工执行，1=机器执行。2=混合执行.
		map.AddTBInt(GenerWorkerListAttr.WhoExeIt, 0, "谁执行它", false, false);

		//发送人. 2011-11-12 为天津用户增加。
		map.AddTBString(GenerWorkerListAttr.Sender, null, "发送人", true, false, 0, 200, 100);

		//优先级，2012-06-15 为青岛用户增加。
		map.AddTBInt(GenerWorkFlowAttr.PRI, 1, "优先级", true, true);

		// 挂起
		map.AddTBDateTime(GenerWorkerListAttr.DTOfHungup,null, "挂起时间", false, false);
		map.AddTBDateTime(GenerWorkerListAttr.DTOfUnHungup,null, "预计解除挂起时间", false, false);

		//外部用户. 203-08-30
		map.AddTBString(GenerWorkerListAttr.GuestNo, null, "外部用户编号", true, false, 0, 30, 100);
		map.AddTBString(GenerWorkerListAttr.GuestName, null, "外部用户名称", true, false, 0, 100, 100);

		map.AddTBInt(GenerWorkerListAttr.Idx, 1, "Idx", true, true);

		//参数标记 2014-04-05.
		map.AddTBAtParas(4000);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 重写基类的方法.
	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (this.getFID() != 0)
		{
			if (this.getFID() == this.getWorkID())
			{
				this.setFID(0);
			}
		}

		if (this.getEmpNo().equals("Guest"))
		{
			this.setEmpName(GuestUser.getName());
			this.setGuestName(this.getEmpName());
			this.setGuestNo(GuestUser.getNo());
		}

		if (DataType.IsNullOrEmpty(this.getDeptNo()))
		{
			this.setDeptNo(WebUser.getDeptNo());
		}

		if (DataType.IsNullOrEmpty(this.getDeptName()))
		{
			this.setDeptName(WebUser.getDeptName());
		}

		//增加记录日期.
		this.SetValByKey(GenerWorkerListAttr.RDT, DataType.getCurrentDateTimess());

		return super.beforeInsert();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getDeptNo()))
		{
			this.setDeptNo(WebUser.getDeptNo());
		}

		if (DataType.IsNullOrEmpty(this.getDeptName()))
		{
			this.setDeptName(WebUser.getDeptName());
		}

		return super.beforeUpdate();

	}

		///#endregion 重写基类的方法.

}
