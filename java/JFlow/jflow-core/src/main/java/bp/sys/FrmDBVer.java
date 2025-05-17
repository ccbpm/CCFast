package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
/** 
 数据版本
*/
public class FrmDBVer extends EntityMyPK
{

		///#region 基本属性
	/** 
	 权限控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.Readonly();
		uac.IsView = false;
		return uac;
	}
	public final String getTrackID()  {
		return this.GetValStringByKey(FrmDBVerAttr.TrackID);
	}
	public final void setTrackID(String value){
		this.SetValByKey(FrmDBVerAttr.TrackID, value);
	}

	public final String getKeyOfEn()  {
		return this.GetValStringByKey(FrmDBVerAttr.KeyOfEn);
	}
	public final void setKeyOfEn(String value){
		this.SetValByKey(FrmDBVerAttr.KeyOfEn, value);
	}
	public final String getFrmID()  {
		return this.GetValStringByKey(FrmDBVerAttr.FrmID);
	}
	public final void setFrmID(String value){
		this.SetValByKey(FrmDBVerAttr.FrmID, value);
	}
	/** 
	 主键值键
	*/
	public final String getRefPKVal()  {
		return this.GetValStringByKey(FrmDBVerAttr.RefPKVal);
	}
	public final void setRefPKVal(String value){
		this.SetValByKey(FrmDBVerAttr.RefPKVal, value);
	}
	/** 
	 FK_Emp
	*/
	public final String getRecNo()  {
		return this.GetValStringByKey(FrmDBVerAttr.RecNo);
	}
	public final void setRecNo(String value){
		this.SetValByKey(FrmDBVerAttr.RecNo, value);
	}
	public final String getRecName()  {
		return this.GetValStringByKey(FrmDBVerAttr.RecName);
	}
	public final void setRecName(String value){
		this.SetValByKey(FrmDBVerAttr.RecName, value);
	}
	public final String getRDT()  {
		return this.GetValStringByKey(FrmDBVerAttr.RDT);
	}
	public final void setRDT(String value){
		this.SetValByKey(FrmDBVerAttr.RDT, value);
	}
	public final int getVer()  {
		return this.GetValIntByKey(FrmDBVerAttr.Ver);
	}
	public final void setVer(int value){
		this.SetValByKey(FrmDBVerAttr.Ver, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 数据版本
	*/
	public FrmDBVer()
	{
	}
	public FrmDBVer(String mypk) throws Exception {
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
		Map map = new Map("Sys_FrmDBVer", "数据版本");
		map.AddMyPK();

		map.AddTBString(FrmDBVerAttr.FrmID, null, "表单ID", true, true, 0, 100, 20);
		map.AddTBString(FrmDBVerAttr.RefPKVal, null, "主键值", true, true, 0, 40, 20);

		map.AddTBString(FrmDBVerAttr.ChangeFields, null, "修改的字段", true, true, 0, 3900, 20);
		map.AddTBInt(FrmDBVerAttr.ChangeNum, 0, "修改的字段数量", true, true);

		map.AddTBString(FrmDBVerAttr.TrackID, null, "TrackID", true, true, 0, 40, 20);

		map.AddTBString(FrmDBVerAttr.RecNo, null, "记录人", true, true, 0, 30, 20);
		map.AddTBString(FrmDBVerAttr.RecName, null, "用户名", true, true, 0, 30, 20);
		map.AddTBDateTime(FrmDBVerAttr.RDT, null, "记录时间", true, true);
		map.AddTBInt(FrmDBVerAttr.Ver, 0, "版本号", true, true);


		map.AddTBString(FrmDBVerAttr.KeyOfEn, null, "章节字段有效", true, true, 0, 100, 20);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 增加版本.


	/** 
	 增加主表、从表的数据
	 
	 @param frmID 表单ID
	 @param refPKVal 关联值
	 @param trackID
	 @param jsonOfFrmDB
	 @param frmDtlDB
	 @param frmAthDB
	*/

	public static void AddFrmDBTrack(int ver, String frmID, String refPKVal, String trackID, String jsonOfFrmDB, String frmDtlDB, String frmAthDB) throws Exception {
		AddFrmDBTrack(ver, frmID, refPKVal, trackID, jsonOfFrmDB, frmDtlDB, frmAthDB, false);
	}

	public static void AddFrmDBTrack(int ver, String frmID, String refPKVal, String trackID, String jsonOfFrmDB, String frmDtlDB, String frmAthDB, boolean isChartFrm) throws Exception {
		if (jsonOfFrmDB == null && isChartFrm == false)
		{
			return;
		}

		FrmDBVer en = new FrmDBVer();
		en.setFrmID(frmID);
		en.setRefPKVal(refPKVal);
		en.setTrackID(trackID);
		en.setVer(ver);
		en.Insert();

		//保存主表数据.
		if (DataType.IsNullOrEmpty(jsonOfFrmDB) == false)
		{
			DBAccess.SaveBigTextToDB(jsonOfFrmDB, "Sys_FrmDBVer", "MyPK", en.getMyPK(), "FrmDB");
		}

		//保存从表数据
		if (DataType.IsNullOrEmpty(frmDtlDB) == false)
		{
			DBAccess.SaveBigTextToDB(frmDtlDB, "Sys_FrmDBVer", "MyPK", en.getMyPK(), "FrmDtlDB");
		}

		//保存附件数据
		if (DataType.IsNullOrEmpty(frmAthDB) == false)
		{
			DBAccess.SaveBigTextToDB(frmAthDB, "Sys_FrmDBVer", "MyPK", en.getMyPK(), "FrmAthDB");
		}
	}
	/** 
	 保存章节表单的字段数据
	 
	 @param frmID
	 @param refPKVal
	 @param trackID
	 @param chartValue
	 @param keyOfEn
	*/
	public static void AddKeyOfEnDBTrack(int ver, String frmID, String refPKVal, String trackID, String chartValue, String keyOfEn) throws Exception {

		FrmDBVer en = new FrmDBVer();
		en.setFrmID(frmID);
		en.setRefPKVal(refPKVal);
		en.setTrackID(trackID);
		en.setKeyOfEn(keyOfEn);
		en.setVer(ver);
		en.Insert();

		//保存章节表单字段的数据.
		DBAccess.SaveBigTextToDB(chartValue, "Sys_FrmDBVer", "MyPK", en.getMyPK(), "FrmDB");
	}

	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setMyPK(DBAccess.GenerGUID());
		this.SetValByKey(FrmDBVerAttr.RDT, DataType.getCurrentDateTime());

		if (DataType.IsNullOrEmpty(this.getRecNo()) == true)
		{
			this.setRecNo(bp.web.WebUser.getNo());
			this.setRecName(bp.web.WebUser.getName());
		}

		return super.beforeInsert();
	}

		///#endregion 增加版本.


		///#region 重写
	@Override
	public Entities GetNewEntities()
	{
		return new FrmDBVers();
	}

		///#endregion 重写
}
