package bp.ccfast.ccmenu;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 标签
*/
public class TabDtl extends EntityNoName
{

		///#region 属性
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getIsAdmin() == true)
		{
			uac.IsDelete = true;
			uac.IsUpdate = true;
			uac.IsInsert = false;
			return uac;
		}
		else
		{
			uac.Readonly();
		}
		return uac;
	}

		///#endregion


		///#region 构造方法
	/** 
	 标签
	*/
	public TabDtl()
	{
	}
	@Override
	protected boolean beforeDelete() throws Exception
	{
		return super.beforeDelete();
	}
	/**
	 * EnMap
	 */
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("GPM_MenuDtl", "标签"); // 类的基本属性.
		map.setEnType(EnType.Sys);

		map.AddTBStringPK(TabDtlAttr.No, null, "编号", false, false, 1, 90, 50);
		map.AddTBString(TabDtlAttr.RefMenuNo, null, "菜单编号", false, false, 0, 100, 100);

		map.AddTBString(TabDtlAttr.Icon, null, "Icon", true, false, 0, 50, 50, true);
		map.AddTBString(TabDtlAttr.Name, null, "Tab名称", true, false, 0, 300, 200, true);
		map.AddTBString(TabDtlAttr.UrlExt, null, "链接", true, false, 0, 50, 50, true);

		map.AddTBString(TabDtlAttr.Model, null, "模式", true, false, 0, 50, 50, true);

		map.AddTBInt(TabDtlAttr.Idx, 0, "Idx", true, false);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setNo(DBAccess.GenerGUID(0, null, null));
		return super.beforeInsert();
	}

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		return super.beforeUpdateInsertAction();
	}
}
