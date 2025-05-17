package bp.port;

import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 部门角色人员对应 的摘要说明。
*/
public class DeptEmpStation extends EntityMyPK
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
	public final String getOrgNo()  {
		return this.GetValStringByKey(DeptEmpStationAttr.OrgNo);
	}
	public final void setOrgNo(String value) throws Exception
	{
		SetValByKey(DeptEmpStationAttr.OrgNo, value);
	}
	/** 
	 人员
	*/
	public final String getEmpNo()  {
		return this.GetValStringByKey(DeptEmpStationAttr.FK_Emp);
	}
	public final void setEmpNo(String value) throws Exception
	{
		SetValByKey(DeptEmpStationAttr.FK_Emp, value);
		this.setMyPK(this.getDeptNo() + "_" + this.getEmpNo() + "_" + this.getStationNo());
	}
	/** 
	 部门
	*/
	public final String getDeptNo()  {
		return this.GetValStringByKey(DeptEmpStationAttr.FK_Dept);
	}
	public final void setDeptNo(String value) throws Exception
	{
		SetValByKey(DeptEmpStationAttr.FK_Dept, value);
		this.setMyPK(this.getDeptNo() + "_" + this.getEmpNo() + "_" + this.getStationNo());
	}
	public final String getStationT()  {
		return this.GetValStringByKey(DeptEmpStationAttr.FK_Station);
	}
	/** 
	角色
	*/
	public final String getStationNo()  {
		return this.GetValStringByKey(DeptEmpStationAttr.FK_Station);
	}
	public final void setStationNo(String value) throws Exception
	{
		SetValByKey(DeptEmpStationAttr.FK_Station, value);
		this.setMyPK(this.getDeptNo() + "_" + this.getEmpNo() + "_" + this.getStationNo());
	}

		///#endregion


		///#region 构造函数
	/** 
	 工作部门角色人员对应
	*/
	public DeptEmpStation()
	{
	}
	/**
	 * 重写基类方法
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Port_DeptEmpStation", "部门角色人员对应");

		map.AddTBStringPK("MyPK", null, "主键MyPK", false, true, 1, 150, 10);
		map.AddTBString(DeptEmpStationAttr.FK_Dept, null, "部门", true, true, 1, 100, 1);
		map.AddTBString(DeptEmpStationAttr.FK_Station, null, "角色", true, true, 1, 50, 1);
		map.AddTBString(DeptEmpStationAttr.FK_Emp, null, "操作员", true, true, 1, 100, 1);
		map.AddTBString(DeptEmpAttr.OrgNo, null, "组织编码", true, true, 0, 50, 50);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	/** 
	 更新删除前做的事情
	 
	 @return 
	*/
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		if (bp.difference.SystemConfig.getCCBPMRunModel() == bp.sys.CCBPMRunModel.SAAS)
		{
			this.setMyPK(this.getDeptNo() + "_" + this.getEmpNo().replace(this.getOrgNo() + "_","") + "_" + this.getStationNo());
		}
		else
		{
			this.setMyPK(this.getDeptNo() + "_" + this.getEmpNo() + "_" + this.getStationNo());
		}
		return super.beforeUpdateInsertAction();
	}
}
