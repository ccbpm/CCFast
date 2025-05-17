package bp.ccfast.ccmenu;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 权限中心
*/
public class PowerCenter extends EntityMyPK
{

		///#region 属性
	/** 
	 控制主键
	*/
	public final String getCtrlPKVal()  {
		return this.GetValStrByKey(PowerCenterAttr.CtrlPKVal);
	}
	public final String getIDNames()  {
		return this.GetValStrByKey(PowerCenterAttr.IDNames);
	}
	public final String getIDs()  {
		return this.GetValStrByKey(PowerCenterAttr.IDs);
	}
	public final String getCtrlModel()  {
		return this.GetValStrByKey(PowerCenterAttr.CtrlModel);
	}
	public final String getCtrlGroup()  {
		return this.GetValStrByKey(PowerCenterAttr.CtrlGroup);
	}
	public final String getCtrlObj()  {
		return this.GetValStrByKey(PowerCenterAttr.CtrlObj);
	}

		///#endregion


		///#region 按钮权限控制
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForAppAdmin();
		return uac;
	}

		///#endregion


		///#region 构造方法
	/** 
	 权限中心
	*/
	public PowerCenter()
	{
	}
	/** 
	 权限中心
	 
	 @param mypk
	*/
	public PowerCenter(String mypk) throws Exception {
		this.setMyPK(mypk);
		this.Retrieve();
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

		Map map = new Map("GPM_PowerCenter", "权限中心");

		map.AddMyPK(true);

		// System,Module,Menus
		map.AddTBString(PowerCenterAttr.CtrlObj, null, "控制对象(SystemMenus)", true, false, 0, 300, 20);
		map.AddTBString(PowerCenterAttr.CtrlPKVal, null, "控制对象ID", true, false, 0, 300, 20);
		//Menus, Frm 
		map.AddTBString(PowerCenterAttr.CtrlGroup, null, "隶属分组(可为空)", true, false, 0, 300, 20);

		//AnyOne,Adminer,Depts
		map.AddTBString(PowerCenterAttr.CtrlModel, null, "控制模式", true, false, 0, 300, 20);

		map.AddTBStringDoc(PowerCenterAttr.IDs, null, "主键s(Stas,Depts等)", true, false);
		map.AddTBStringDoc(PowerCenterAttr.IDNames, null, "IDNames", true, false);

		map.AddTBString(PowerCenterAttr.OrgNo, null, "编号", true, false, 0, 100, 20);
		map.AddTBString(PowerCenterAttr.Idx, null, "Idx", true, false, 0, 100, 20);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		if (bp.web.WebUser.getIsAdmin() == false)
		{
			throw new RuntimeException("err@非管理员不能操作...");
		}

		return super.beforeUpdateInsertAction();
	}

	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setMyPK(DBAccess.GenerGUID(0, null, null));
		return super.beforeInsert();
	}
}
