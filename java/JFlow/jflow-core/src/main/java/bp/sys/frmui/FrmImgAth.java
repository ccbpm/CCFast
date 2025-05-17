package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.sys.*;
import java.util.*;

/** 
 图片附件
*/
public class FrmImgAth extends EntityMyPK
{

		///#region 属性
	/** 
	 名称
	*/
	public final String getName()  {
		return this.GetValStringByKey(FrmImgAthAttr.Name);
	}
	public final void setName(String value){
		this.SetValByKey(FrmImgAthAttr.Name, value);
	}
	/** 
	 控件ID
	*/
	public final String getCtrlID()  {
		return this.GetValStringByKey(FrmImgAthAttr.CtrlID);
	}
	public final void setCtrlID(String value){
		this.SetValByKey(FrmImgAthAttr.CtrlID, value);
	}

	/** 
	 H
	*/
	public final float getH()  {
		return this.GetValFloatByKey(FrmImgAthAttr.H);
	}
	public final void setH(float value){
		this.SetValByKey(FrmImgAthAttr.H, value);
	}
	/** 
	 W
	*/
	public final float getW()  {
		return this.GetValFloatByKey(FrmImgAthAttr.W);
	}
	public final void setW(float value){
		this.SetValByKey(FrmImgAthAttr.W, value);
	}
	/** 
	 FK_MapData
	*/
	public final String getFrmID()  {
		return this.GetValStrByKey(FrmImgAthAttr.FrmID);
	}
	public final void setFrmID(String value){
		this.SetValByKey(FrmImgAthAttr.FrmID, value);
	}
	/** 
	 是否可编辑
	*/
	public final boolean getItIsEdit()  {
		return this.GetValBooleanByKey(FrmImgAthAttr.IsEdit);
	}
	public final void setItIsEdit(boolean value){
		this.SetValByKey(FrmImgAthAttr.IsEdit, value);
	}
	/** 
	 是否必填，2016-11-1
	*/
	public final boolean getItIsRequired()  {
		return this.GetValBooleanByKey(FrmImgAthAttr.IsRequired);
	}
	public final void setItIsRequired(boolean value){
		this.SetValByKey(FrmImgAthAttr.IsRequired, value);
	}
	/** 
	 所在的分组
	*/
	public final int getGroupID() throws Exception {
		String str = this.GetValStringByKey(FrmImgAthAttr.GroupID);
		if (Objects.equals(str, "无") || Objects.equals(str, ""))
		{
			return 1;
		}
		return Integer.parseInt(str);
	}
	public final void setGroupID(int value){
		this.SetValByKey(FrmImgAthAttr.GroupID, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	*/

	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsInsert = false;
		uac.IsUpdate = true;
		uac.IsDelete = true;
		return uac;
	}
	/** 
	 图片附件
	*/
	public FrmImgAth()
	{
	}
	/** 
	 图片附件
	 
	 @param mypk
	*/
	public FrmImgAth(String mypk) throws Exception {
		this.setMyPK(mypk);
		this.Retrieve();
	}
	/** 
	 EnMap
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("Sys_FrmImgAth", "图片附件");
		map.IndexField = MapAttrAttr.FK_MapData;

		map.AddMyPK();

		map.AddTBString(FrmImgAthAttr.FrmID, null, "表单ID", true, true, 1, 100, 20);
		map.AddTBString(FrmImgAthAttr.CtrlID, null, "控件ID", true, true, 0, 200, 20);
		map.AddTBString(FrmImgAthAttr.Name, null, "中文名称", true, false, 0, 200, 20);


		map.AddTBFloat(FrmImgAthAttr.H, 200, "H", true, false);
		map.AddTBFloat(FrmImgAthAttr.W, 160, "W", false, false);

		map.AddBoolean(FrmImgAthAttr.IsEdit, true, "是否可编辑", true, true);
		//map.AddTBInt(FrmImgAthAttr.IsEdit, 1, "是否可编辑", true, true);
		map.AddBoolean(FrmImgAthAttr.IsRequired, false, "是否必填项", true, true);
		//显示的分组.
		map.AddDDLSQL(FrmImgAthAttr.GroupID, 0, "显示的分组", MapAttrString.getSQLOfGroupAttr(), true);
		map.AddTBInt(MapAttrAttr.ColSpan, 0, "单元格数量", false, true);

		//跨单元格
		map.AddDDLSysEnum(MapAttrAttr.LabelColSpan, 1, "文本单元格数量", true, true, "ColSpanAttrString", "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格");
		//跨行
		map.AddDDLSysEnum(MapAttrAttr.RowSpan, 1, "行数", true, true, "RowSpanAttrString", "@1=跨1个行@2=跨2行@3=跨3行");
		//map.AddTBInt(FrmImgAthAttr.IsRequired, 0, "是否必填项", true, true);
		//map.AddTBString(FrmBtnAttr.GUID, null, "GUID", true, true, 0, 128, 20);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		//this.setMyPK(this.getFrmID() + "_" + this.CtrlID;
		return super.beforeUpdateInsertAction();
	}

	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		//在属性实体集合插入前，clear父实体的缓存.
		bp.sys.base.Glo.ClearMapDataAutoNum(this.getFrmID());

		bp.sys.FrmImgAth imgAth = new bp.sys.FrmImgAth();
		imgAth.setMyPK(this.getMyPK());
		imgAth.RetrieveFromDBSources();
		imgAth.Update();

		//调用frmEditAction, 完成其他的操作.
		bp.sys.CCFormAPI.AfterFrmEditAction(this.getFrmID());

		MapAttr attr = new MapAttr();
		attr.setMyPK(this.getFrmID() + "_" + imgAth.getCtrlID());
		if (attr.RetrieveFromDBSources() == 0)
		{
			attr.setFrmID(this.getFrmID());
			attr.setName(this.getName());
			attr.setKeyOfEn(imgAth.getCtrlID());
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.FrmImgAth);
			attr.setGroupID(this.getGroupID());
			attr.setItIsEnableInAPP(true);
			attr.setUIVisible(true);
			attr.DirectInsert();
		}
		else
		{
			attr.setGroupID(this.getGroupID());
			attr.DirectUpdate();
		}


		super.afterInsertUpdateAction();
	}

	/** 
	 删除后清缓存
	*/
	@Override
	protected void afterDelete() throws Exception
	{
		//把相关的字段也要删除.
		MapAttrString attr = new MapAttrString();
		attr.setMyPK(this.getMyPK());
		attr.setFrmID(this.getFrmID());
		attr.Delete();
		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());
		super.afterDelete();
	}

}
