package bp.ccfast.ccmenu;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 查询条件
*/
public class SearchAttr extends EntityNoName
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
			uac.IsInsert = true;
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
	 查询条件
	*/
	public SearchAttr()
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
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("GPM_MenuDtl", "查询条件"); // 类的基本属性.
		map.setEnType(EnType.Sys);

		map.AddTBStringPK(SearchAttrAttr.No, null, "编号", false, false, 1, 90, 50);
		map.AddTBString(SearchAttrAttr.RefMenuNo, null, "菜单编号", false, false, 0, 100, 100);

		map.AddTBString(SearchAttrAttr.Name, null, "条件标签", true, false, 0, 300, 100, true);
		map.AddTBString(SearchAttrAttr.Tag1, null, "标识", true, false, 0, 500, 100, true);
		map.AddTBString(SearchAttrAttr.UrlExt, null, "SQL/标记/枚举值", true, false, 0, 500, 500, true);

		map.AddTBInt(SearchAttrAttr.Idx, 0, "Idx", true, false);
		map.AddDtl(new SearchAttrs(), SearchAttrAttr.RefMenuNo);

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
