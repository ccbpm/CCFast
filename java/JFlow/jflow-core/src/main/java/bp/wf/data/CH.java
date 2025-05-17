package bp.wf.data;

import bp.en.*;
import bp.en.Map;

/** 
 时效考核
*/
public class CH extends EntityMyPK
{

		///#region 基本属性
	/** 
	 发送人
	*/
	public final String getSender()  {
		return this.GetValStringByKey(CHAttr.Sender);
	}
	public final void setSender(String value){
		this.SetValByKey(CHAttr.Sender, value);
	}
	/** 
	 发送人名称
	*/
	public final String getSenderT()  {
		return this.GetValStringByKey(CHAttr.SenderT);
	}
	public final void setSenderT(String value){
		this.SetValByKey(CHAttr.SenderT, value);
	}
	/** 
	 考核状态
	*/
	public final CHSta getCHSta() {
		return CHSta.forValue(this.GetValIntByKey(CHAttr.CHSta));
	}
	public final void setCHSta(CHSta value){
		this.SetValByKey(CHAttr.CHSta, value.getValue());
	}
	/** 
	 时间到
	*/
	public final String getDTTo()  {
		return this.GetValStringByKey(CHAttr.DTTo);
	}
	public final void setDTTo(String value){
		this.SetValByKey(CHAttr.DTTo, value);
	}
	/** 
	 时间从
	*/
	public final String getDTFrom()  {
		return this.GetValStringByKey(CHAttr.DTFrom);
	}
	public final void setDTFrom(String value){
		this.SetValByKey(CHAttr.DTFrom, value);
	}
	/** 
	 应完成日期
	*/
	public final String getSDT()  {
		return this.GetValStringByKey(CHAttr.SDT);
	}
	public final void setSDT(String value){
		this.SetValByKey(CHAttr.SDT, value);
	}
	/** 
	 流程标题
	*/
	public final String getTitle()  {
		return this.GetValStringByKey(CHAttr.Title);
	}
	public final void setTitle(String value){
		this.SetValByKey(CHAttr.Title, value);
	}
	/** 
	 流程编号
	*/
	public final String getFK_Flow()  {
		return this.GetValStringByKey(CHAttr.FK_Flow);
	}
	public final void setFK_Flow(String value){
		this.SetValByKey(CHAttr.FK_Flow, value);
	}
	/** 
	 流程
	*/
	public final String getFK_FlowT()  {
		return this.GetValStringByKey(CHAttr.FK_FlowT);
	}
	public final void setFK_FlowT(String value){
		this.SetValByKey(CHAttr.FK_FlowT, value);
	}
	/** 
	 限期
	*/
	public final int getTimeLimit()  {
		return this.GetValIntByKey(CHAttr.TimeLimit);
	}
	public final void setTimeLimit(int value){
		this.SetValByKey(CHAttr.TimeLimit, value);
	}
	/** 
	 实际完成用时.
	*/
	public final float getUseDays()  {
		return this.GetValFloatByKey(CHAttr.UseDays);
	}
	public final void setUseDays(float value){
		this.SetValByKey(CHAttr.UseDays, value);
	}
	/** 
	 逾期时间
	*/
	public final float getOverDays()  {
		return this.GetValFloatByKey(CHAttr.OverDays);
	}
	public final void setOverDays(float value){
		this.SetValByKey(CHAttr.OverDays, value);
	}
	/** 
	 用时（分钟）
	*/
	public final float getUseMinutes()  {
		return this.GetValFloatByKey(CHAttr.UseMinutes);
	}
	public final void setUseMinutes(float value){
		this.SetValByKey(CHAttr.UseMinutes, value);
	}
	/** 
	 超时（分钟）
	*/
	public final float getOverMinutes()  {
		return this.GetValFloatByKey(CHAttr.OverMinutes);
	}
	public final void setOverMinutes(float value){
		this.SetValByKey(CHAttr.OverMinutes, value);
	}
	/** 
	 操作人员
	*/
	public final String getFK_Emp()  {
		return this.GetValStringByKey(CHAttr.FK_Emp);
	}
	public final void setFK_Emp(String value){
		this.SetValByKey(CHAttr.FK_Emp, value);
	}
	/** 
	 人员
	*/
	public final String getFK_EmpT()  {
		return this.GetValStringByKey(CHAttr.FK_EmpT);
	}
	public final void setFK_EmpT(String value){
		this.SetValByKey(CHAttr.FK_EmpT, value);
	}
	/** 
	 相关当事人
	*/
	public final String getGroupEmps()  {
		return this.GetValStringByKey(CHAttr.GroupEmps);
	}
	public final void setGroupEmps(String value){
		this.SetValByKey(CHAttr.GroupEmps, value);
	}
	/** 
	 相关当事人名称
	*/
	public final String getGroupEmpsNames()  {
		return this.GetValStringByKey(CHAttr.GroupEmpsNames);
	}
	public final void setGroupEmpsNames(String value){
		this.SetValByKey(CHAttr.GroupEmpsNames, value);
	}
	/** 
	 相关当事人数量
	*/
	public final int getGroupEmpsNum()  {
		return this.GetValIntByKey(CHAttr.GroupEmpsNum);
	}
	public final void setGroupEmpsNum(int value){
		this.SetValByKey(CHAttr.GroupEmpsNum, value);
	}
	/** 
	 部门
	*/
	public final String getFK_Dept()  {
		return this.GetValStrByKey(CHAttr.FK_Dept);
	}
	public final void setFK_Dept(String value){
		this.SetValByKey(CHAttr.FK_Dept, value);
	}
	/** 
	 部门名称
	*/
	public final String getFK_DeptT()  {
		return this.GetValStrByKey(CHAttr.FK_DeptT);
	}
	public final void setFK_DeptT(String value){
		this.SetValByKey(CHAttr.FK_DeptT, value);
	}
	/** 
	 年月
	*/
	public final String getFK_NY()  {
		return this.GetValStrByKey(CHAttr.FK_NY);
	}
	public final void setFK_NY(String value){
		this.SetValByKey(CHAttr.FK_NY, value);
	}
	/** 
	 考核方式
	*/
	public final int getDTSWay()  {
		return this.GetValIntByKey(CHAttr.DTSWay);
	}
	public final void setDTSWay(int value){
		this.SetValByKey(CHAttr.DTSWay, value);
	}
	/** 
	 周
	*/
	public final int getWeekNum()  {
		return this.GetValIntByKey(CHAttr.WeekNum);
	}
	public final void setWeekNum(int value){
		this.SetValByKey(CHAttr.WeekNum, value);
	}
	/** 
	 工作ID
	*/
	public final long getWorkID()  {
		return this.GetValInt64ByKey(CHAttr.WorkID);
	}
	public final void setWorkID(long value){
		this.SetValByKey(CHAttr.WorkID, value);
	}
	/** 
	 流程ID
	*/
	public final long getFID()  {
		return this.GetValInt64ByKey(CHAttr.FID);
	}
	public final void setFID(long value){
		this.SetValByKey(CHAttr.FID, value);
	}
	/** 
	 节点ID
	*/
	public final int getFK_Node()  {
		return this.GetValIntByKey(CHAttr.FK_Node);
	}
	public final void setNodeID(int value){
		this.SetValByKey(CHAttr.FK_Node, value);
	}
	/** 
	 节点名称
	*/
	public final String getFK_NodeT()  {
		return this.GetValStrByKey(CHAttr.FK_NodeT);
	}
	public final void setFK_NodeT(String value){
		this.SetValByKey(CHAttr.FK_NodeT, value);
	}
	/** 
	 总扣分
	*/
	public final float getPoints()  {
		return this.GetValFloatByKey(CHAttr.Points);
	}
	public final void setPoints(float value){
		this.SetValByKey(CHAttr.Points, value);
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
	 时效考核
	*/
	public CH()
	{
	}
	/** 
	 
	 
	 param pk
	*/
	public CH(String pk) throws Exception 
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

		Map map = new Map("WF_CH", "时效考核");

		map.AddMyPK(true);


			///#region 基本属性.
		map.AddTBInt(CHAttr.WorkID, 0, "工作ID", false, true);
		map.AddTBInt(CHAttr.FID, 0, "FID", false, true);
		map.AddTBString(CHAttr.Title, null, "标题", false, false, 0, 900, 5);

		map.AddTBString(CHAttr.FK_Flow, null, "流程", false, false, 0, 4, 3);
		map.AddTBString(CHAttr.FK_FlowT, null, "流程名称", true, true, 0, 200, 5);

		map.AddTBInt(CHAttr.FK_Node, 0, "节点", false, false);
		map.AddTBString(CHAttr.FK_NodeT, null, "节点名称", true, true, 0, 200, 5);


		map.AddTBString(CHAttr.Sender, null, "发送人", false, false, 0, 200, 3);
		map.AddTBString(CHAttr.SenderT, null, "发送人名称", true, true, 0, 200, 5);


		map.AddTBString(CHAttr.FK_Emp, null, "当事人", true, true, 0, 100, 3);
		map.AddTBString(CHAttr.FK_EmpT, null, "当事人名称", true, true, 0, 200, 5);

			//为邓州增加的属性. 解决多人处理一个节点的工作的问题.
		map.AddTBString(CHAttr.GroupEmps, null, "相关当事人", true, true, 0, 400, 3);
		map.AddTBString(CHAttr.GroupEmpsNames, null, "相关当事人名称", true, true, 0, 900, 3);
		map.AddTBInt(CHAttr.GroupEmpsNum, 1, "相关当事人数量", false, false);


		map.AddTBString(CHAttr.DTFrom, null, "任务下达时间", true, true, 0, 50, 5);
		map.AddTBString(CHAttr.DTTo, null, "任务处理时间", true, true, 0, 50, 5);
		map.AddTBString(CHAttr.SDT, null, "应完成日期", true, true, 0, 50, 5);

		map.AddTBString(CHAttr.FK_Dept, null, "隶属部门", true, true, 0, 50, 5);
		map.AddTBString(CHAttr.FK_DeptT, null, "部门名称", true, true, 0, 500, 5);
		map.AddTBString(CHAttr.FK_NY, null, "隶属月份", true, true, 0, 10, 10);
		map.AddDDLSysEnum(CHAttr.DTSWay, 0, "考核方式", true, true, CHAttr.DTSWay, "@0=不考核@1=按照时效考核@2=按照工作量考核");

			///#endregion 基本属性.


			///#region 计算属性.
		map.AddTBString(CHAttr.TimeLimit, null, "规定限期", true, true, 0, 50, 5);
		map.AddTBFloat(CHAttr.OverMinutes, 0, "逾期分钟", false, true);
		map.AddTBFloat(CHAttr.UseDays, 0, "实际使用天", false, true);
		map.AddTBFloat(CHAttr.OverDays, 0, "逾期天", false, true);
		map.AddTBInt(CHAttr.CHSta, 0, "状态", true, true);
		map.AddTBInt(CHAttr.WeekNum, 0, "第几周", false, true);
		map.AddTBFloat(CHAttr.Points, 0, "总扣分", true, true);

			///#endregion 计算属性.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

}