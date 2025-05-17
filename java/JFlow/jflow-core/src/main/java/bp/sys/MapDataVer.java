package bp.sys;

import bp.da.*;
import bp.en.Map;
import bp.en.*;

/** 
 表单模板版本管理
*/
public class MapDataVer extends EntityMyPK
{

		///#region 属性
	public final int getVer()  {
		return this.GetValIntByKey(MapDataVerAttr.Ver);
	}
	public final void setVer(int value){
		this.SetValByKey(MapDataVerAttr.Ver, value);
	}
	public final int getItIsRel()  {
		return this.GetValIntByKey(MapDataVerAttr.IsRel);
	}
	public final void setItIsRel(int value){
		this.SetValByKey(MapDataVerAttr.IsRel, value);
	}
	public final int getRowNum()  {
		return this.GetValIntByKey(MapDataVerAttr.RowNum);
	}
	public final void setRowNum(int value){
		this.SetValByKey(MapDataVerAttr.RowNum, value);
	}
	public final String getFrmID()  {
		return this.GetValStringByKey(MapDataVerAttr.FrmID);
	}
	public final void setFrmID(String value){
		this.SetValByKey(MapDataVerAttr.FrmID, value);
	}
	public final String getRec()  {
		return this.GetValStringByKey(MapDataVerAttr.Rec);
	}
	public final void setRec(String value){
		this.SetValByKey(MapDataVerAttr.Rec, value);
	}
	public final String getRecName()  {
		return this.GetValStringByKey(MapDataVerAttr.RecName);
	}
	public final void setRecName(String value){
		this.SetValByKey(MapDataVerAttr.RecName, value);
	}
	public final String getRecNote()  {
		return this.GetValStringByKey(MapDataVerAttr.RecNote);
	}
	public final void setRecNote(String value){
		this.SetValByKey(MapDataVerAttr.RecNote, value);
	}
	public final void setRDT(String value){
		this.SetValByKey(MapDataVerAttr.RDT, value);
	}
	public final void setAttrsNum(int value){
		this.SetValByKey(MapDataVerAttr.AttrsNum, value);
	}
	public final void setDtlsNum(int value){
		this.SetValByKey(MapDataVerAttr.DtlsNum, value);
	}
	public final void setAthsNum(int value){
		this.SetValByKey(MapDataVerAttr.AthsNum, value);
	}
	public final void setExtsNum(int value){
		this.SetValByKey(MapDataVerAttr.ExtsNum, value);
	}


		///#endregion


		///#region 构造方法
	/** 
	 模板版本管理
	*/
	public MapDataVer()
	{
	}
	public MapDataVer(String mypk) throws Exception {
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

		Map map = new Map("Sys_MapDataVer", "表单模板版本管理");

		map.AddMyPK();
		map.AddTBInt(MapDataVerAttr.Ver, 0, "版本号", true, false);
		map.AddTBInt(MapDataVerAttr.IsRel, 0, "是否主版本?", true, false);
		map.AddTBInt(MapDataVerAttr.RowNum, 0, "行数", true, false);

		map.AddTBString(MapDataVerAttr.FrmID, null, "表单ID", true, false, 0, 50, 20);


		map.AddTBInt(MapDataVerAttr.AttrsNum, 0, "字段数", true, true);
		map.AddTBInt(MapDataVerAttr.DtlsNum, 0, "从表数", true, true);
		map.AddTBInt(MapDataVerAttr.AthsNum, 0, "附件数", true, true);
		map.AddTBInt(MapDataVerAttr.ExtsNum, 0, "逻辑数", true, true);



		map.AddTBString(MapDataVerAttr.Rec, null, "记录人ID", true, false, 0, 50, 20);
		map.AddTBString(MapDataVerAttr.RecName, null, "记录人名称", true, false, 0, 50, 20);
		map.AddTBString(MapDataVerAttr.RecNote, null, "备注", true, false, 0, 500, 20);

		map.AddTBDateTime(MapDataVerAttr.RDT, null, "记录时间", true, false);


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception{
		this.SetValByKey("RDT", DataType.getCurrentDateTime());
		return super.beforeInsert();
	}
	@Override
	protected boolean beforeDelete() throws Exception
	{
		//如果改版本的数据已经在存储表中使用，则不能删除
		MapData md = new MapData(this.getFrmID());
		int count = DBAccess.RunSQLReturnValInt("SELECT Count(*) From " + md.getPTable() + " WHERE AtPara like '%@FrmVer=" + this.getVer() + "%'");
		if (count > 0)
		{
			throw new RuntimeException("表单" + md.getName() + "版本" + this.getVer() + "已经被使用，不能删除");
		}

		return super.beforeDelete();
	}

	@Override
	protected void afterDelete() throws Exception
	{
		//删除版本需要删除相关的表单信息
		MapData md = new MapData(this.getFrmID() + "." + this.getVer());
		md.Delete();
		super.afterDelete();
	}


}
