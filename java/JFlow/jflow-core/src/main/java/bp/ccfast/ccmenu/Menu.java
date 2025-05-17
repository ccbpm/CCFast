package bp.ccfast.ccmenu;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.sys.*;

/** 
 菜单
*/
public class Menu extends EntityNoName
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
	/** 
	 系统编号
	*/
	public final String getSystemNo() {
		return this.GetValStringByKey(MenuAttr.SystemNo);
	}
	public final void setSystemNo(String value)  {
		this.SetValByKey(MenuAttr.SystemNo, value);
	}
	/** 
	 组织编号
	*/
	public final String getOrgNo() {
		return this.GetValStringByKey(MenuAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(MenuAttr.OrgNo, value);
	}
	/** 
	 标记
	*/
	public final String getMark() {
		return this.GetValStringByKey(MenuAttr.Mark);
	}
	public final void setMark(String value)  {
		this.SetValByKey(MenuAttr.Mark, value);
	}
	/** 
	 Tag 1
	*/
	public final String getTag1() {
		return this.GetValStringByKey(MenuAttr.Tag1);
	}
	public final void setTag1(String value)  {
		this.SetValByKey(MenuAttr.Tag1, value);
	}
	//public CtrlWay HisCtrlWay
	//{
	//    get
	//    {
	//        return (CtrlWay)this.GetValIntByKey(MenuAttr.CtrlWay);
	//    }
	//    set
	//    {
	//        this.SetValByKey(MenuAttr.CtrlWay, (int)value);
	//    }
	//}
	/** 
	 功能
	*/
	public final MenuType getHisMenuType() {
		return MenuType.forValue(this.GetValIntByKey(MenuAttr.MenuType));
	}
	public final void setHisMenuType(MenuType value)  {
		this.SetValByKey(MenuAttr.MenuType, value.getValue());
	}

	/** 
	 是否启用
	*/
	public final boolean getItIsEnable() {
		return this.GetValBooleanByKey(MenuAttr.IsEnable);
	}
	public final void setItIsEnable(boolean value)  {
		this.SetValByKey(MenuAttr.IsEnable, value);
	}
	/** 
	 是否是ccSytem
	*/
	//public MenuType MenuType
	//{
	//    get
	//    {
	//        return (MenuType)this.GetValIntByKey(MenuAttr.MenuType);
	//    }
	//    set
	//    {
	//        this.SetValByKey(MenuAttr.MenuType, (int)value);
	//    }
	//}
	/** 
	 类别编号
	*/
	public final String getModuleNo() {
		return this.GetValStringByKey(MenuAttr.ModuleNo);
	}
	public final void setModuleNo(String value)  {
		this.SetValByKey(MenuAttr.ModuleNo, value);
	}
	/** 
	 模式
	*/
	public final String getMenuModel() {
		return this.GetValStringByKey(MenuAttr.MenuModel);
	}
	public final void setMenuModel(String value)  {
		this.SetValByKey(MenuAttr.MenuModel, value);
	}
	public final String getIcon() {
		return this.GetValStringByKey(MenuAttr.Icon);
	}
	public final void setIcon(String value)  {
		this.SetValByKey(MenuAttr.Icon, value);
	}
	/** 
	 菜单工作类型 0=自定义菜单， 1=系统菜单，不可以删除.
	*/
	public final int getWorkType() {
		return this.GetValIntByKey(MenuAttr.WorkType);
	}
	public final void setWorkType(int value)  {
		this.SetValByKey(MenuAttr.WorkType, value);
	}
	public final String getUrlExt() {
		return this.GetValStringByKey(MenuAttr.UrlExt);
	}
	public final void setUrlExt(String value)  {
		this.SetValByKey(MenuAttr.UrlExt, value);
	}
	public final String getMobileUrlExt() {
		return this.GetValStringByKey(MenuAttr.MobileUrlExt);
	}
	public final void setMobileUrlExt(String value)  {
		this.SetValByKey(MenuAttr.MobileUrlExt, value);
	}
	public boolean ItIsCheck = false;
	/** 
	 标记
	*/
	public final int getIdx() {
		return this.GetValIntByKey(MenuAttr.Idx);
	}
	public final void setIdx(int value)  {
		this.SetValByKey(MenuAttr.Idx, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 菜单
	*/
	public Menu()
	{
	}

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
		//如果是数据源列表.
		if (this.getMenuModel().equals("DBList") == true)
		{
			//MapData md = new MapData(this.UrlExt);
			//md.Delete();

			MapAttrs attrs = new MapAttrs();
			attrs.Delete(MapAttrAttr.FK_MapData, this.getMark() + "Bak");
		}

		//删除窗体信息.
		if (this.getMenuModel().equals("Windows") == true)
		{
			bp.ccfast.portal.WindowTemplates ens = new bp.ccfast.portal.WindowTemplates();
			ens.Delete(bp.ccfast.portal.WindowTemplateAttr.PageID, this.getNo());

			// BP.CCFast.Portal.WindowExt.HtmlVarDtls dtls = new Home.WindowExt.HtmlVarDtls();
			// dtls.Delete(BP.CCFast.Portal.WindowTemplateAttr.PageID, this.getNo());
		}


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

		Map map = new Map("GPM_Menu", "菜单"); // 类的基本属性.
		map.setEnType(EnType.Sys);

		map.AddTBStringPK(MenuAttr.No, null, "编号", false, false, 1, 150, 50);
		map.AddTBString(MenuAttr.Name, null, "名称", true, false, 0, 300, 200, true);

		map.AddTBString(MenuAttr.MenuModel, null, "菜单模式", true, true, 0, 50, 50);

		map.AddTBString(MenuAttr.Mark, null, "标记", true, false, 0, 300, 200, false);
		map.AddTBString(MenuAttr.Tag1, null, "Tag1", true, false, 0, 300, 200, false);

		map.AddTBString(MenuAttr.FrmID, null, "FrmID", false, false, 0, 300, 200, false);
		map.AddTBString(MenuAttr.FlowNo, null, "FlowNo", false, false, 0, 300, 200, false);

		// @0=系统根目录@1=系统类别@2=系统.
		map.AddDDLSysEnum(MenuAttr.OpenWay, 1, "打开方式", true, true, MenuAttr.OpenWay, "@0=新窗口@1=本窗口@2=覆盖新窗口");
		// --- 添加vue3路由支持 ---
		// --- 23-11-19
		String PCLinkHelpDocs = "如果是菜单对应的是Vue组件，需要配置此项和Vue文件地址，此项可以自定义，但是需要携带url参数";
		PCLinkHelpDocs += "\n例如/StudentSearch?EnName=TS.Demo.Student";
		PCLinkHelpDocs += "\n也支持url连接，以http/https开头，例如https://www.baidu.com";
		map.AddTBString(MenuAttr.UrlExt, null, "路由配置", true, false, 0, 500, 200, false, PCLinkHelpDocs);
//		map.AddTBString(MenuAttr.UrlExt, null, "PC端连接", true, false, 0, 500, 200, true);
		map.AddTBString(MenuAttr.Alias, null, "别名", false, false, 0, 500, 300);
		map.AddTBString(MenuAttr.path, null, "path", false, false, 0, 50, 50);
		map.AddTBString(MenuAttr.UrlPath, null, "vue文件路径", false, false, 0, 50, 50);
		map.AddDDLStringEnum(MenuAttr.IframeOpenType,"inner","iframe打开方式","@inner=内嵌@outer=外部",true,"iframe打开方式，可以为项目内打开或者新窗口打开,<p style='color:red'>仅当链接为url时有效<p>",false);
		// --- end -----------
		map.AddTBString(MenuAttr.MobileUrlExt, null, "移动端连接", true, false, 0, 500, 200, true);

		map.AddBoolean(MenuAttr.IsEnable, true, "是否启用?", true, true);

		map.AddTBString(MenuAttr.Icon, null, "Icon", true, false, 0, 50, 50, true);
		//  map.AddTBString(MenuAttr.ModuleNo, null, "ModuleNo", false, false, 0, 50, 50);
		map.AddTBString(MenuAttr.SystemNo, null, "SystemNo", false, false, 0, 150, 50);

		//隶属模块，可以让用户编辑。
		map.AddDDLSQL(MenuAttr.ModuleNo, null, "隶属模块编号", "SELECT No,Name FROM GPM_Module WHERE SystemNo='@SystemNo'", true);

		map.AddDDLSysEnum(MenuAttr.ListModel, 0, "列表模式", true, true, MenuAttr.ListModel, "@0=编辑模式@1=视图模式");
		String msg = "提示";
		msg += "\t\n 1. 编辑模式就是可以批量的编辑方式打开数据, 可以批量的表格方式编辑数据.";
		msg += "\t\n 2. 视图模式就是查询的模式打开数据..";
		map.SetHelperAlert(MenuAttr.ListModel, msg);

		map.AddTBString(MenuAttr.OrgNo, null, "OrgNo", true, false, 0, 50, 20);
		map.AddTBInt(MenuAttr.Idx, 0, "顺序号", true, false);
		//@0=不共享@1=按照系统权限设置
		map.AddTBInt("ShareOrgSln", 0, "组织共享方案", true, false);
		map.AddTBAtParas(500);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getModuleNo()) == true)
		{
			throw new RuntimeException("err@模块编号不能为空.");
		}

		//获取他的系统编号.
		Module md = new Module(this.getModuleNo());
		this.setSystemNo(md.getSystemNo());

		if (this.getMenuModel().equals("Dict") || this.getMenuModel().equals("DBList") || this.getMenuModel().equals("Bill") == true)
		{
			this.setMark(DataType.ParseStringForNo(this.getMark(), 100));
			this.setUrlExt(DataType.ParseStringForNo(this.getUrlExt(), 100));
		}
		return super.beforeUpdateInsertAction();
	}

	@Override
	protected void afterDelete() throws Exception
	{
		String sql = "";

			///#region 删除实体。
		if (this.getMenuModel().equals("Dict") == true || this.getMenuModel().equals("EntityNoName") == true  || this.getMenuModel().equals("DBList") == true || this.getMenuModel().equals("Bill") == true)
		{
			String frmID = this.getUrlExt();
			if (frmID.contains("?") || frmID.contains("="))
			{
				frmID = this.GetParaString("EnPKVal");
			}
			//删除实体.
			MapData md = new MapData(frmID);
			md.Delete();

			//删除集合方法.
			sql = "DELETE FROM Frm_Collection WHERE FrmID='" + frmID + "'";
			DBAccess.RunSQL(sql);

			//删除实体组件.
			sql = "DELETE FROM Frm_Method WHERE FrmID='" + frmID + "'";
			DBAccess.RunSQL(sql);

			//删除实体组件.
			sql = "DELETE FROM Frm_ToolbarBtn WHERE FrmID='" + frmID + "'";
			DBAccess.RunSQL(sql);

			//删除实体组件.
			sql = "DELETE FROM GPM_PowerCenter WHERE CtrlObj='Menu' AND CtrlPKVal='" + this.getNo() + "'";
			DBAccess.RunSQL(sql);
		}

			///#endregion 删除实体。

		//删除权限控制..
		sql = "DELETE FROM GPM_PowerCenter WHERE CtrlObj='Menu' AND CtrlPKVal='" + this.getNo() + "'";
		DBAccess.RunSQL(sql);

		super.afterDelete();
	}


		///#region 移动方法.
	/** 
	 向上移动
	*/
	public final void DoUp() throws Exception {
		this.DoOrderUp(MenuAttr.ModuleNo, this.getModuleNo(), ModuleAttr.Idx);
	}
	/** 
	 向下移动
	*/
	public final void DoDown() throws Exception
	{
		this.DoOrderDown(MenuAttr.ModuleNo, this.getModuleNo(), ModuleAttr.Idx);
	}

		///#endregion 移动方法.

}
