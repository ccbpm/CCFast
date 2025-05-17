package bp.port;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 部门人员信息 的摘要说明。
*/
public class DeptEmp extends EntityMyPK
{

		///#region 基本属性
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		return uac;
	}
	/** 
	 人员
	*/
	public final String getEmpNo()  {
		return this.GetValStringByKey(DeptEmpAttr.FK_Emp);
	}
	public final void setEmpNo(String value) throws Exception
	{
		SetValByKey(DeptEmpAttr.FK_Emp, value);
		this.setMyPK(this.getDeptNo() + "_" + this.getEmpNo());
	}
	/** 
	 部门
	*/
	public final String getDeptNo()  {
		return this.GetValStringByKey(DeptEmpAttr.FK_Dept);
	}
	public final void setDeptNo(String value) throws Exception
	{
		SetValByKey(DeptEmpAttr.FK_Dept, value);
		this.setMyPK(this.getDeptNo() + "_" + this.getEmpNo());
	}
	public final String getOrgNo()  {
		return this.GetValStringByKey(DeptEmpAttr.OrgNo);
	}
	public final void setOrgNo(String value) throws Exception
	{
		SetValByKey(DeptEmpAttr.OrgNo, value);
	}
	public final String getStationNo()  {
		return this.GetValStringByKey(DeptEmpAttr.StationNo);
	}
	public final void setStationNo(String value) throws Exception
	{
		SetValByKey(DeptEmpAttr.StationNo, value);
	}
	public final String getStationNoT()  {
		return this.GetValStringByKey(DeptEmpAttr.StationNoT);
	}
	public final void setStationNoT(String value) throws Exception
	{
		SetValByKey(DeptEmpAttr.StationNoT, value);
	}
	public final String getDeptName()  {
		return this.GetValStringByKey(DeptEmpAttr.DeptName);
	}
	public final void setDeptName(String value) throws Exception
	{
		SetValByKey(DeptEmpAttr.DeptName, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 工作部门人员信息
	*/
	public DeptEmp()
	{
	}
	/**
	 * 重写基类方法
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
			return this.get_enMap();

		Map map = new Map("Port_DeptEmp", "部门人员信息");
		map.IndexField = DeptEmpAttr.FK_Dept;

		map.AddMyPK();
		map.AddTBString(DeptEmpAttr.FK_Dept, null, "部门", false, false, 1, 50, 1);
		map.AddDDLEntities(DeptEmpAttr.FK_Emp, null, "操作员", new bp.port.Emps(), false);
		map.AddTBString(DeptEmpAttr.OrgNo, null, "组织编码", false, false, 0, 50, 50);

		//For Vue3版本.
		map.AddTBString(DeptEmpAttr.DeptName, null, "部门名称(Vue3)", false, false, 0, 500, 36);
		map.AddTBString(DeptEmpAttr.StationNo, null, "岗位编号(Vue3)", false, false, 0, 500, 36);
		map.AddTBString(DeptEmpAttr.StationNoT, null, "岗位名称(Vue3)", false, false, 0, 500, 36);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeDelete() throws Exception
	{
		bp.sys.base.Glo.WriteUserLog("删除:" + this.ToJson(), "组织数据操作");
		return super.beforeDelete();
	}
	@Override
	protected boolean beforeInsert() throws Exception
	{
		bp.sys.base.Glo.WriteUserLog("新建:" + this.ToJson(), "组织数据操作");
		return super.beforeInsert();
	}

	@Override
	protected void afterDelete() throws Exception
	{
		DeptEmpStations des = new DeptEmpStations();
		des.Delete("FK_Dept", this.GetValByKey("FK_Dept"), "FK_Emp", this.GetValByKey("FK_Emp"));
		super.afterDelete();
	}

	/** 
	 更新前做的事情
	 
	 @return 
	*/
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		if (bp.difference.SystemConfig.getCCBPMRunModel() != bp.sys.CCBPMRunModel.Single && DataType.IsNullOrEmpty(this.getOrgNo()))
		{
			this.setOrgNo(bp.web.WebUser.getOrgNo());
		}
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			if (bp.difference.SystemConfig.getCCBPMRunModel() == bp.sys.CCBPMRunModel.SAAS)
			{
				this.setMyPK(this.getDeptNo() + "_" + this.getEmpNo().replace(this.getOrgNo() + "_",""));
			}
			else
			{
				this.setMyPK(this.getDeptNo() + "_" + this.getEmpNo());
			}

		}

		return super.beforeUpdateInsertAction();
	}
}
