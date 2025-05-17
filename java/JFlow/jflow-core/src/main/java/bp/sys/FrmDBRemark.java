package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;

/** 
 数据批阅
*/
public class FrmDBRemark extends EntityMyPK
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
	public final String getRemark()  {
		return this.GetValStringByKey(FrmDBRemarkAttr.Remark);
	}
	public final void setRemark(String value){
		this.SetValByKey(FrmDBRemarkAttr.Remark, value);
	}
	public final String getFrmID()  {
		return this.GetValStringByKey(FrmDBRemarkAttr.FrmID);
	}
	public final void setFrmID(String value){
		this.SetValByKey(FrmDBRemarkAttr.FrmID, value);
	}
	/** 
	 FK_Emp
	*/
	public final String getRecNo()  {
		return this.GetValStringByKey(FrmDBRemarkAttr.RecNo);
	}
	public final void setRecNo(String value){
		this.SetValByKey(FrmDBRemarkAttr.RecNo, value);
	}
	public final String getRecName()  {
		return this.GetValStringByKey(FrmDBRemarkAttr.RecName);
	}
	public final void setRecName(String value){
		this.SetValByKey(FrmDBRemarkAttr.RecName, value);
	}
	public final String getRDT()  {
		return this.GetValStringByKey(FrmDBRemarkAttr.RDT);
	}
	public final void setRDT(String value){
		this.SetValByKey(FrmDBRemarkAttr.RDT, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 数据批阅
	*/
	public FrmDBRemark()
	{
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
		Map map = new Map("Sys_FrmDBRemark", "数据批阅");
		map.AddMyPK();

		map.AddTBString(FrmDBRemarkAttr.FrmID, null, "表单ID", true, true, 0, 100, 20);
		map.AddTBString(FrmDBRemarkAttr.RefPKVal, null, "PKVal", true, true, 0, 40, 20);
		map.AddTBString(FrmDBRemarkAttr.Field, null, "字段", true, true, 0, 60, 20);


		map.AddTBString(FrmDBRemarkAttr.Remark, null, "备注", true, true, 0, 500, 20);

		map.AddTBString(FrmDBRemarkAttr.RecNo, null, "记录人", true, true, 0, 50, 20);
		map.AddTBString(FrmDBRemarkAttr.RecName, null, "字段", true, true, 0, 60, 20);
		map.AddTBDateTime(FrmDBRemarkAttr.RDT, null, "记录时间", true, true);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setMyPK(DBAccess.GenerGUID());
		this.setRDT(DataType.getCurrentDateTime());
		if (DataType.IsNullOrEmpty(this.getRecNo()) == true)
		{
			this.setRecNo(bp.web.WebUser.getNo());
			this.setRecName(bp.web.WebUser.getName());
		}
		return super.beforeInsert();
	}


		///#region 重写
	@Override
	public Entities GetNewEntities()
	{
		return new FrmDBRemarks();
	}

		///#endregion 重写
}
