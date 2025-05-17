package bp.ccbill.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 实体工具栏按钮
*/
public class ToolbarBtn extends EntityMyPK
{

		///#region 基本属性
	/** 
	 表单ID
	*/
	public final String getFrmID() {
		return this.GetValStringByKey(ToolbarBtnAttr.FrmID);
	}
	public final void setFrmID(String value)  {
		this.SetValByKey(ToolbarBtnAttr.FrmID, value);
	}
	public final String getIcon() {
		return this.GetValStringByKey(ToolbarBtnAttr.Icon);
	}
	public final void setIcon(String value)  {
		this.SetValByKey(ToolbarBtnAttr.Icon, value);
	}

	/** 
	 方法ID
	*/
	public final String getBtnID() {
		return this.GetValStringByKey(ToolbarBtnAttr.BtnID);
	}
	public final void setBtnID(String value)  {
		this.SetValByKey(ToolbarBtnAttr.BtnID, value);
	}
	public final String getBtnLab() {
		return this.GetValStringByKey(ToolbarBtnAttr.BtnLab);
	}
	public final void setBtnLab(String value)  {
		this.SetValByKey(ToolbarBtnAttr.BtnLab, value);
	}
	public final boolean getItIsEnable() {
		return this.GetValBooleanByKey(ToolbarBtnAttr.IsEnable);
	}
	public final void setItIsEnable(boolean value)  {
		this.SetValByKey(ToolbarBtnAttr.IsEnable, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 实体工具栏按钮
	*/
	public ToolbarBtn()
	{
	}
	/** 
	 实体工具栏按钮
	 
	 @param mypk
	*/
	public ToolbarBtn(String mypk) throws Exception {
		this.setMyPK(mypk);
		this.Retrieve();
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

		Map map = new Map("Frm_ToolbarBtn", "实体工具栏按钮");

		map.AddMyPK(true);

		//主键.
		map.AddTBString(ToolbarBtnAttr.BtnID, null, "按钮ID", true, true, 0, 300, 10);
		map.AddTBString(ToolbarBtnAttr.BtnLab, null, "标签", true, false, 0, 300, 10);

		//功能标记. 
		map.AddTBString(ToolbarBtnAttr.FrmID, null, "表单ID", true, true, 0, 300, 10);

		map.AddTBString(ToolbarBtnAttr.Icon, null, "图标", true, false, 0, 50, 10, true);


			///#region 外观.
		map.AddTBInt(ToolbarBtnAttr.PopHeight, 0, "弹窗高度", true, false);
		map.AddTBInt(ToolbarBtnAttr.PopWidth, 0, "弹窗宽度", true, false);

			///#endregion 外观.

		//是否启用？
		map.AddBoolean(ToolbarBtnAttr.IsEnable, true, "是否启用？", true, true, true);
		map.AddTBInt(ToolbarBtnAttr.Idx, 0, "Idx", true, false);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 移动.
	public final void DoUp() throws Exception {
		this.DoOrderUp(ToolbarBtnAttr.FrmID, this.getFrmID(), ToolbarBtnAttr.Idx);
	}
	public final void DoDown() throws Exception
	{
		this.DoOrderDown(ToolbarBtnAttr.FrmID, this.getFrmID(), ToolbarBtnAttr.Idx);
	}

		///#endregion 移动.

	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			this.setMyPK(DBAccess.GenerGUID(0, null, null));
		}
		return super.beforeInsert();
	}

}
