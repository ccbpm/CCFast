package bp.ccfast.ccmenu;

import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 标签容器
*/
public class Tab extends EntityNoName
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
	 标签容器
	*/
	public Tab()
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

		Map map = new Map("GPM_Menu", "标签容器"); // 类的基本属性.
		map.setEnType(EnType.Sys);

		map.AddTBStringPK(MenuAttr.No, null, "编号", false, false, 1, 90, 50);
		map.AddTBString(MenuAttr.Name, null, "名称", true, false, 0, 300, 200, true);
		map.AddTBString(MenuAttr.Icon, null, "Icon", true, false, 0, 50, 50, true);

		//从表明细.
		map.AddDtl(new TabDtls(), TabDtlAttr.RefMenuNo);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		return super.beforeUpdateInsertAction();
	}
}
