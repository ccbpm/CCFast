package bp.wf.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;


/** 
 流程轨迹权限
*/
public class TruckViewPower extends EntityNoName
{

		///#region 属性
	/** 
	 发起人可看
	*/
	public final boolean getPStarter()  {
		return this.GetValBooleanByKey(TruckViewPowerAttr.PStarter);
	}
	public final void setPStarter(boolean value){
		this.SetValByKey(TruckViewPowerAttr.PStarter, value);
	}
	/** 
	 参与人可见
	*/
	public final boolean getPWorker()  {
		return this.GetValBooleanByKey(TruckViewPowerAttr.PWorker);
	}
	public final void setPWorker(boolean value){
		this.SetValByKey(TruckViewPowerAttr.PWorker, value);
	}
	/** 
	 被抄送人可见
	*/
	public final boolean getPCCer()  {
		return this.GetValBooleanByKey(TruckViewPowerAttr.PCCer);
	}
	public final void setPCCer(boolean value){
		this.SetValByKey(TruckViewPowerAttr.PCCer, value);
	}
	/** 
	 任何人都可见
	*/
	public final boolean getPAnyOne()  {
		return this.GetValBooleanByKey(TruckViewPowerAttr.PAnyOne);
	}
	public final void setPAnyOne(boolean value){
		this.SetValByKey(TruckViewPowerAttr.PAnyOne, value);
	}
	/** 
	 本部门可见
	*/
	public final boolean getPMyDept()  {
		return this.GetValBooleanByKey(TruckViewPowerAttr.PMyDept);
	}
	public final void setPMyDept(boolean value){
		this.SetValByKey(TruckViewPowerAttr.PMyDept, value);
	}
	/** 
	 直属上级部门可见
	*/
	public final boolean getPPMyDept()  {
		return this.GetValBooleanByKey(TruckViewPowerAttr.PPMyDept);
	}
	public final void setPPMyDept(boolean value){
		this.SetValByKey(TruckViewPowerAttr.PPMyDept, value);
	}
	/** 
	 上级部门可见
	*/
	public final boolean getPPDept()  {
		return this.GetValBooleanByKey(TruckViewPowerAttr.PPDept);
	}
	public final void setPPDept(boolean value){
		this.SetValByKey(TruckViewPowerAttr.PPDept, value);
	}
	/** 
	 平级部门可见
	*/
	public final boolean getPSameDept()  {
		return this.GetValBooleanByKey(TruckViewPowerAttr.PSameDept);
	}
	public final void setPSameDept(boolean value){
		this.SetValByKey(TruckViewPowerAttr.PSameDept, value);
	}
	/** 
	 指定部门可见
	*/
	public final boolean getPSpecDept()  {
		return this.GetValBooleanByKey(TruckViewPowerAttr.PSpecDept);
	}
	public final void setPSpecDept(boolean value){
		this.SetValByKey(TruckViewPowerAttr.PSpecDept, value);
	}
	/** 
	 部门编号
	*/
	public final String getPSpecDeptExt()  {
		return this.GetValStrByKey(TruckViewPowerAttr.PSpecDeptExt);
	}
	public final void setPSpecDeptExt(String value){
		this.SetValByKey(TruckViewPowerAttr.PSpecDeptExt, value);
	}
	/** 
	 指定角色可见
	*/
	public final boolean getPSpecSta()  {
		return this.GetValBooleanByKey(TruckViewPowerAttr.PSpecSta);
	}
	public final void setPSpecSta(boolean value){
		this.SetValByKey(TruckViewPowerAttr.PSpecSta, value);
	}
	/** 
	 角色编号
	*/
	public final String getPSpecStaExt()  {
		return this.GetValStrByKey(TruckViewPowerAttr.PSpecStaExt);
	}
	public final void setPSpecStaExt(String value){
		this.SetValByKey(TruckViewPowerAttr.PSpecStaExt, value);
	}

	/** 
	 权限组
	*/
	public final boolean getPSpecGroup()  {
		return this.GetValBooleanByKey(TruckViewPowerAttr.PSpecGroup);
	}
	public final void setPSpecGroup(boolean value){
		this.SetValByKey(TruckViewPowerAttr.PSpecGroup, value);
	}

	/** 
	 权限组编号
	*/
	public final String getPSpecGroupExt()  {
		return this.GetValStrByKey(TruckViewPowerAttr.PSpecGroupExt);
	}
	public final void setPSpecGroupExt(String value){
		this.SetValByKey(TruckViewPowerAttr.PSpecGroupExt, value);
	}

	/** 
	 指定的人员
	*/
	public final boolean getPSpecEmp()  {
		return this.GetValBooleanByKey(TruckViewPowerAttr.PSpecEmp);
	}
	public final void setPSpecEmp(boolean value){
		this.SetValByKey(TruckViewPowerAttr.PSpecEmp, value);
	}
	/** 
	 指定编号
	*/
	public final String getPSpecEmpExt()  {
		return this.GetValStrByKey(TruckViewPowerAttr.PSpecEmpExt);
	}
	public final void setPSpecEmpExt(String value){
		this.SetValByKey(TruckViewPowerAttr.PSpecEmpExt, value);
	}









		///#endregion



		///#region 构造方法
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForAppAdmin();
		uac.IsDelete = false;
		uac.IsInsert = false;
		return uac;
	}

	/** 
	 流程轨迹权限
	*/
	public TruckViewPower()
	{
	}

	public TruckViewPower(String no) throws Exception
	{
		this.setNo(no);
		this.Retrieve();
	}
	/** 
	 map
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_Flow", "流程模版主表");
		map.setDepositaryOfEntity(Depositary.Application);

		map.AddTBStringPK(TruckViewPowerAttr.No, null, "编号", true, true, 1, 10, 3);
		map.AddTBString(TruckViewPowerAttr.Name, null, "名称", true, false, 0, 50, 10, true);


			///#region 基本控制.
		map.AddBoolean(TruckViewPowerAttr.PStarter, true, "发起人可看(必选)", true, false, false);
		map.AddBoolean(TruckViewPowerAttr.PWorker, true, "参与人可看(必选)", true, false, false);
		map.AddBoolean(TruckViewPowerAttr.PCCer, true, "被抄送人可看(必选)", true, false, false);
		map.AddBoolean(TruckViewPowerAttr.PAnyOne, false, "任何人可见", true, false, true);

			///#endregion 基本控制.


			///#region 按部门控制.
		map.AddBoolean(TruckViewPowerAttr.PMyDept, true, "本部门人可看", true, true, false);
		map.AddBoolean(TruckViewPowerAttr.PPMyDept, true, "直属上级部门可看", true, true, false);

		map.AddBoolean(TruckViewPowerAttr.PPDept, true, "上级部门可看", true, true, false);
		map.AddBoolean(TruckViewPowerAttr.PSameDept, true, "平级部门可看", true, true, false);

		map.AddBoolean(TruckViewPowerAttr.PSpecDept, true, "指定部门可看", true, true, false);
		map.AddTBString(TruckViewPowerAttr.PSpecDeptExt, null, "部门编号", true, false, 0, 200, 100, false);

			///#endregion 按部门控制.


			///#region 其他权限控制.
		map.AddBoolean(TruckViewPowerAttr.PSpecSta, true, "指定的角色可看", true, true, false);
		map.AddTBString(TruckViewPowerAttr.PSpecStaExt, null, "角色编号", true, false, 0, 200, 100, false);

		map.AddBoolean(TruckViewPowerAttr.PSpecGroup, true, "指定的权限组可看", true, true, false);
		map.AddTBString(TruckViewPowerAttr.PSpecGroupExt, null, "权限组", true, false, 0, 200, 100, false);

		map.AddBoolean(TruckViewPowerAttr.PSpecEmp, true, "指定的人员可看", true, true, false);
		map.AddTBString(TruckViewPowerAttr.PSpecEmpExt, null, "指定的人员编号", true, false, 0, 200, 100, false);

			///#endregion 其他权限控制.


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 公用方法.
	/** 
	 检查指定的人员是否可以产看该轨迹图.
	 
	 @param workid 流程ID
	 @param userNo 操作员
	 @return 
	*/
	public final boolean CheckICanView(long workid, String userNo)
	{
		if (userNo == null)
		{
			userNo = bp.web.WebUser.getNo();
		}
		return true;
	}

		///#endregion
}
