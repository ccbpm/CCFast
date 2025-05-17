package bp.ccfast.ccmenu;

import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 人员菜单功能
*/
public class EmpMenu extends EntityMyPK
{

		///#region 属性
	/** 
	 人员
	*/
	public final String getEmpNo() {
		return this.GetValStringByKey(EmpMenuAttr.FK_Emp);
	}
	public final void setEmpNo(String value)  {
		this.SetValByKey(EmpMenuAttr.FK_Emp, value);
	}
	/** 
	 菜单
	*/
	public final String getFKMenu() {
		return this.GetValStringByKey(EmpMenuAttr.FK_Menu);
	}
	public final void setFKMenu(String value)  {
		this.SetValByKey(EmpMenuAttr.FK_Menu, value);
	}
	/** 
	 是否选中
	*/
	public final boolean getItIsChecked() {
		return this.GetValBooleanByKey(EmpMenuAttr.IsChecked);
	}
	public final void setItIsChecked(boolean value)  {
		this.SetValByKey(EmpMenuAttr.IsChecked, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 人员菜单功能
	*/
	public EmpMenu()
	{
	}
	/**
	 * 人员菜单功能
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("GPM_EmpMenu", "人员菜单对应");

		map.AddMyPK(true);

		// map.AddTBStringPK(EmpMenuAttr.FK_Emp, null, "操作员", true, false, 0, 3900, 20);
		map.AddTBString(EmpMenuAttr.FK_Menu, null, "菜单", false, false, 0, 50, 20);
		map.AddDDLEntities(EmpMenuAttr.FK_Emp, null, "人员", new bp.port.Emps(), true);
		map.AddTBString(EmpMenuAttr.FK_App, null, "系统编号", false, false, 0, 50, 20);
		map.AddBoolean(EmpMenuAttr.IsChecked, true, "是否选中", true, true);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception
	{
		//@wwh,代码转换.
		this.setMyPK(this.getFKMenu() + "_" + this.getEmpNo());
		return super.beforeInsert();
	}
}
