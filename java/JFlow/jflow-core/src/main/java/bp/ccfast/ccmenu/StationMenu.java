package bp.ccfast.ccmenu;

import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 角色菜单
*/
public class StationMenu extends EntityMyPK
{

		///#region 属性
	/** 
	 菜单
	*/
	public final String getFKMenu() {
		return this.GetValStringByKey(StationMenuAttr.FK_Menu);
	}
	public final void setFKMenu(String value)  {
		this.SetValByKey(StationMenuAttr.FK_Menu, value);
	}
	/** 
	 角色
	*/
	public final String getFKStation() {
		return this.GetValStringByKey(StationMenuAttr.FK_Station);
	}
	public final void setFKStation(String value)  {
		this.SetValByKey(StationMenuAttr.FK_Station, value);
	}
	/** 
	 是否选中
	*/
	public final String isChecked() {
		return this.GetValStringByKey(StationMenuAttr.IsChecked);
	}
	public final void setChecked(String value)  {
		this.SetValByKey(StationMenuAttr.IsChecked, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 角色菜单
	*/
	public StationMenu()
	{
	}

	/**
	 * 角色菜单
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("GPM_StationMenu", "角色菜单");
		map.setEnType(EnType.Sys);

		map.AddMyPK(true);
		map.AddTBString(StationMenuAttr.FK_Menu, null, "菜单", false, false, 0, 50, 20);
		map.AddTBString(StationMenuAttr.FK_Station, null, "角色", false, false, 0, 50, 20);

		//map.AddDDLEntities(StationMenuAttr.FK_Station, null, "角色", new bp.port.Stations(), true);
		map.AddBoolean(StationMenuAttr.IsChecked, true, "是否选中", true, true);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setMyPK(this.getFKMenu() + "_" + this.getFKStation());
		return super.beforeInsert();
	}
}
