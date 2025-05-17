package bp.sys;

import bp.en.*;
import bp.en.Map;

/** 
 表单元素扩展DB
*/
public class FrmEleDB extends EntityMyPK
{

		///#region 属性
	/** 
	 EleID
	*/
	public final String getEleID()  {
		return this.GetValStrByKey(FrmEleDBAttr.EleID);
	}
	public final void setEleID(String value){
		this.SetValByKey(FrmEleDBAttr.EleID, value);
	}
	/** 
	 Tag1
	*/
	public final String getTag1()  {
		return this.GetValStringByKey(FrmEleDBAttr.Tag1);
	}
	public final void setTag1(String value){
		this.SetValByKey(FrmEleDBAttr.Tag1, value);
	}
	/** 
	 Tag2
	*/
	public final String getTag2()  {
		return this.GetValStringByKey(FrmEleDBAttr.Tag2);
	}
	public final void setTag2(String value){
		this.SetValByKey(FrmEleDBAttr.Tag2, value);
	}
	/** 
	 Tag3
	*/
	public final String getTag3()  {
		return this.GetValStringByKey(FrmEleDBAttr.Tag3);
	}
	public final void setTag3(String value){
		this.SetValByKey(FrmEleDBAttr.Tag3, value);
	}
	/** 
	 Tag4
	*/
	public final String getTag4()  {
		return this.GetValStringByKey(FrmEleDBAttr.Tag4);
	}
	public final void setTag4(String value){
		this.SetValByKey(FrmEleDBAttr.Tag4, value);
	}
	/** 
	 Tag5
	*/
	public final String getTag5()  {
		return this.GetValStringByKey(FrmEleDBAttr.Tag5);
	}
	public final void setTag5(String value){
		this.SetValByKey(FrmEleDBAttr.Tag5, value);
	}
	/** 
	 FK_MapData
	*/
	public final String getFrmID()  {
		return this.GetValStrByKey(FrmEleDBAttr.FK_MapData);
	}
	public final void setFrmID(String value){
		this.SetValByKey(FrmEleDBAttr.FK_MapData, value);
	}
	public final void setFK_MapData(String val){
		this.SetValByKey(FrmEleDBAttr.FK_MapData, val);
	}
	/** 
	 RefPKVal
	*/
	public final String getRefPKVal()  {
		return this.GetValStrByKey(FrmEleDBAttr.RefPKVal);
	}
	public final void setRefPKVal(String value){
		this.SetValByKey(FrmEleDBAttr.RefPKVal, value);
	}
	/** 
	 流程ID
	*/
	public final long getFID()  {
		return this.GetValInt64ByKey(FrmEleDBAttr.FID);
	}
	public final void setFID(long value){
		this.SetValByKey(FrmEleDBAttr.FID, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 表单元素扩展DB
	*/
	public FrmEleDB()
	{
	}
	/** 
	 表单元素扩展DB
	 
	 @param mypk
	*/
	public FrmEleDB(String mypk) throws Exception {
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
		Map map = new Map("Sys_FrmEleDB", "表单元素扩展DB");
		map.IndexField = FrmEleDBAttr.RefPKVal;

		map.AddMyPK();
		map.AddTBString(FrmEleDBAttr.FK_MapData, null, "FK_MapData", true, false, 1, 100, 20);
		map.AddTBString(FrmEleDBAttr.EleID, null, "EleID", true, false, 0, 50, 20);
		map.AddTBString(FrmEleDBAttr.RefPKVal, null, "RefPKVal", true, false, 0, 50, 20);
		map.AddTBInt(FrmEleDBAttr.FID, 0, "FID", false, true);
		map.AddTBString(FrmEleDBAttr.Tag1, null, "Tag1", true, false, 0, 1000, 20);
		map.AddTBString(FrmEleDBAttr.Tag2, null, "Tag2", true, false, 0, 1000, 20);
		map.AddTBString(FrmEleDBAttr.Tag3, null, "Tag3", true, false, 0, 1000, 20);
		map.AddTBString(FrmEleDBAttr.Tag4, null, "Tag4", true, false, 0, 1000, 20);
		map.AddTBString(FrmEleDBAttr.Tag5, null, "Tag5", true, false, 0, 1000, 20);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		//this.setMyPK(this.getFrmID() + "_" + this.EleID + "_" + this.RefPKVal;
	   // this.GenerPKVal();
		return super.beforeUpdateInsertAction();
	}
	public final void GenerPKVal() throws Exception {
		this.setMyPK(this.getFrmID() + "_" + this.getEleID() + "_" + this.getRefPKVal());
	}
}
