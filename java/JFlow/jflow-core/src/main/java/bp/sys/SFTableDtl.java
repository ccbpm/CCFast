package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;

/** 
 系统字典表
*/
public class SFTableDtl extends EntityMyPK
{

		///#region 属性.
	/** 
	 组织编号
	*/
	//public String OrgNo
	//{
	//    get
	//    {
	//        return this.GetValStrByKey(SFTableDtlAttr.OrgNo);
	//    }
	//    set
	//    {
	//        this.SetValByKey(SFTableDtlAttr.OrgNo, value);
	//    }
	//}
	public final String getFKSFTable()  {
		return this.GetValStrByKey(SFTableDtlAttr.FK_SFTable);
	}
	public final void setFKSFTable(String value){
		this.SetValByKey(SFTableDtlAttr.FK_SFTable, value);
	}
	public final String getBH()  {
		return this.GetValStrByKey(SFTableDtlAttr.BH);
	}
	public final void setBH(String value){
		this.SetValByKey(SFTableDtlAttr.BH, value);
	}
	public final String getName()  {
		return this.GetValStrByKey(SFTableDtlAttr.Name);
	}
	public final void setName(String value){
		this.SetValByKey(SFTableDtlAttr.Name, value);
	}
	public final String getParentNo()  {
		return this.GetValStrByKey(SFTableDtlAttr.ParentNo);
	}
	public final void setParentNo(String value){
		this.SetValByKey(SFTableDtlAttr.ParentNo, value);
	}
	public final int getIdx()  {
		return this.GetValIntByKey(SFTableDtlAttr.Idx);
	}
	public final void setIdx(int value){
		this.SetValByKey(SFTableDtlAttr.Idx, value);
	}

		///#endregion 属性.


		///#region 构造方法
	/** 
	 访问权限
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
	 系统字典表
	*/
	public SFTableDtl()
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

		Map map = new Map("Sys_SFTableDtl", "系统字典表");

		//FK_SFTable+"_"+BH
		map.AddMyPK();

		map.AddTBString(SFTableDtlAttr.FK_SFTable, null, "外键表ID", true, false, 0, 200, 20);
		//map.AddTBString(SFTableDtlAttr.TableID, null, "TableID", true, false, 0, 200, 20);
		map.AddTBString(SFTableDtlAttr.BH, null, "BH", true, false, 0, 200, 20);
		map.AddTBString(SFTableDtlAttr.Name, null, "名称", true, false, 0, 200, 20);
		map.AddTBString(SFTableDtlAttr.ParentNo, null, "父节点ID", true, false, 0, 200, 20);

		//用户注销组织的时候，方便删除数据.
		map.AddTBString(SFTableDtlAttr.OrgNo, null, "OrgNo", true, false, 0, 50, 20);
		map.AddTBInt(SFTableDtlAttr.Idx, 0, "顺序号", false, false);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception
	{

		if (DataType.IsNullOrEmpty(this.GetValStringByKey("OrgNo")) == true)
		{
			this.SetValByKey("OrgNo", bp.web.WebUser.getOrgNo());
		}

		if (DataType.IsNullOrEmpty(this.GetValStringByKey("BH")) == true)
		{
			this.SetValByKey("BH", DBAccess.GenerGUID());
		}

		this.setMyPK(this.GetValStringByKey("FK_SFTable") + "_" + this.GetValStringByKey("BH"));

		return super.beforeInsert();
	}
}
