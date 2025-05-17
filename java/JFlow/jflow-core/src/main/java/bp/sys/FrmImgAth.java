package bp.sys;

import bp.en.*;
import bp.en.Map;

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
	public final void setName(boolean val){
		this.SetValByKey(FrmImgAthAttr.Name, val);
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
	public final float getH()  {
		return this.GetValFloatByKey(FrmImgAthAttr.H);
	}
	public final void setH(float value){
		this.SetValByKey(FrmImgAthAttr.H, value);
	}

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
	public final void setIsEdit(boolean val){
		this.SetValByKey(FrmImgAthAttr.IsEdit, val);
	}
	/** 
	 是否必填，2016-11-1
	*/
	public final boolean getItIsRequired()  {
		return this.GetValBooleanByKey(FrmImgAthAttr.IsRequired);
	}
	public final void setIsRequired(boolean val){
		this.SetValByKey(FrmImgAthAttr.IsRequired, val);
	}

		///#endregion


		///#region 构造方法
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
		map.IndexField = FrmImgAttr.FrmID;

		map.AddMyPK();

		map.AddTBString(FrmImgAthAttr.FrmID, null, "表单ID", true, false, 1, 100, 20);
		map.AddTBString(FrmImgAthAttr.CtrlID, null, "控件ID", true, false, 0, 200, 20);
		map.AddTBString(FrmImgAthAttr.Name, null, "中文名称", true, false, 0, 200, 20);

		map.AddTBInt(FrmImgAthAttr.IsEdit, 1, "是否可编辑", true, true);
		map.AddTBInt(FrmImgAthAttr.IsRequired, 0, "是否必填项", true, true);
		map.AddTBString(MapAttrAttr.GUID, null, "GUID", true, false, 0, 128, 20);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		this.setMyPK(this.getFrmID() + "_" + this.getCtrlID());
		MapAttr attr = new MapAttr();
		attr.setMyPK(this.getMyPK());
		if (attr.RetrieveFromDBSources() == 1)
		{
			attr.setName(this.getName());
			attr.Update();
		}

		return super.beforeUpdateInsertAction();
	}
}
