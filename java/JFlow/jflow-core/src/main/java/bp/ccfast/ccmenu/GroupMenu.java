package bp.ccfast.ccmenu;

import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 权限组菜单
*/
public class GroupMenu extends EntityMyPK
{

		///#region 属性
	/** 
	 菜单
	*/
	public final String getFKMenu() {
		return this.GetValStringByKey(GroupMenuAttr.FK_Menu);
	}
	public final void setFKMenu(String value)  {
		this.SetValByKey(GroupMenuAttr.FK_Menu, value);
	}
	/** 
	 权限组
	*/
	public final String getFKGroup() {
		return this.GetValStringByKey(GroupMenuAttr.FK_Group);
	}
	public final void setFKGroup(String value)  {
		this.SetValByKey(GroupMenuAttr.FK_Group, value);
	}
	/** 
	 是否选中
	*/
	public final String isChecked() {
		return this.GetValStringByKey(GroupMenuAttr.IsChecked);
	}
	public final void setChecked(String value)  {
		this.SetValByKey(GroupMenuAttr.IsChecked, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 权限组菜单
	*/
	public GroupMenu()
	{
	}

	/**
	 * 权限组菜单
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("GPM_GroupMenu", "权限组菜单");
		map.setEnType(EnType.Sys);

		map.AddMyPK(true);

		map.AddTBString(GroupMenuAttr.FK_Group, null, "权限组", false, false, 0, 50, 20);
		map.AddTBString(GroupMenuAttr.FK_Menu, null, "菜单", false, false, 0, 50, 20);
		map.AddBoolean(GroupMenuAttr.IsChecked, true, "是否选中", true, true);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
	@Override
	protected boolean beforeInsert() throws Exception
	{
		//@wwh 
		this.setMyPK(this.getFKMenu() + "_" + this.getFKGroup());
		return super.beforeInsert();
	}
}
