package bp.ccfast.ccmenu;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 模块
*/
public class Module extends EntityNoName
{

		///#region 属性
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(ModuleAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(ModuleAttr.OrgNo, value);
	}
	/** 
	 系统编号
	*/
	public final String getSystemNo()  {
		return this.GetValStrByKey(ModuleAttr.SystemNo);
	}
	public final void setSystemNo(String value)  {
		this.SetValByKey(ModuleAttr.SystemNo, value);
	}
	public final String getIcon()  {
		return this.GetValStrByKey(ModuleAttr.Icon);
	}
	public final void setIcon(String value)  {
		this.SetValByKey(ModuleAttr.Icon, value);
	}
	public final int getIdx() {
		return this.GetValIntByKey(ModuleAttr.Idx);
	}
	public final void setIdx(int value)  {
		this.SetValByKey(ModuleAttr.Idx, value);
	}
	public final boolean getItIsEnable() {
		return this.GetValBooleanByKey(ModuleAttr.IsEnable);
	}
	public final void setItIsEnable(boolean value)  {
		this.SetValByKey(ModuleAttr.IsEnable, value);
	}


		///#endregion


		///#region 按钮权限控制
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
	 模块
	*/
	public Module()
	{
	}

	/** 
	 模块
	 
	 @param no
	*/
	public Module(String no) throws Exception
	{
		this.setNo(no);
		this.Retrieve();
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
		Map map = new Map("GPM_Module", "模块");
		map.setEnType(EnType.App);
		map.setItIsAutoGenerNo(false);

		map.AddTBStringPK(ModuleAttr.No, null, "编号", true, true, 1, 200, 20);
		map.AddTBString(ModuleAttr.Name, null, "名称", true, false, 0, 300, 20);
		map.AddDDLEntities(ModuleAttr.SystemNo, null, "隶属系统", new MySystems(), true);
		map.AddTBString(MenuAttr.Icon, null, "Icon", true, false, 0, 500, 50, true);
		map.AddTBInt(ModuleAttr.Idx, 0, "顺序", true, false);
		map.AddTBInt(ModuleAttr.IsEnable, 1, "IsEnable", true, false);
		map.AddTBString(ModuleAttr.OrgNo, null, "组织编号", true, false, 0, 50, 20);

		map.AddTBInt("ChildDisplayModel", 0, "ChildDisplayModel", true, false);
		//@0=不共享@1=按照系统权限设置
		map.AddTBInt("ShareOrgSln", 0, "组织共享方案", true, false);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 移动方法.
	/** 
	 向上移动
	*/
	public final void DoUp() throws Exception {
		this.DoOrderUp(ModuleAttr.SystemNo, this.getSystemNo(), ModuleAttr.Idx);
	}
	/** 
	 向下移动
	*/
	public final void DoDown() throws Exception
	{
		this.DoOrderDown(ModuleAttr.SystemNo, this.getSystemNo(), ModuleAttr.Idx);
	}

		///#endregion 移动方法.

	/** 
	 业务处理.
	 
	 @return 
	*/
	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getNo()) == true)
		{
			this.setNo(DBAccess.GenerGUID(0, null, null));
		}
		this.setOrgNo(bp.web.WebUser.getOrgNo());
		return super.beforeInsert();
	}
	@Override
	protected boolean beforeDelete() throws Exception
	{
		Menus ens = new Menus();
		ens.Retrieve(MenuAttr.ModuleNo, this.getNo(), null);
		if (ens.size() != 0)
		{
			throw new RuntimeException("err@该模块下有菜单，您不能删除。");
		}

		return super.beforeDelete();
	}

}
