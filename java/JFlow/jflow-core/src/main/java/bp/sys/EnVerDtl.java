package bp.sys;
import bp.en.*;
import bp.en.Map;

/** 
 部门角色对应 的摘要说明。
*/
public class EnVerDtl extends EntityMyPK
{

		///#region 基本属性
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{

		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		return uac;

	}
	public final String getRefPK()  {
		return this.GetValStringByKey(EnVerDtlAttr.RefPK);
	}
	public final void setRefPK(String value) throws Exception
	{
		SetValByKey(EnVerDtlAttr.RefPK, value);
	}
	/** 
	 实体名称
	*/
	public final String getFrmID()  {
		return this.GetValStringByKey(EnVerDtlAttr.FrmID);
	}
	public final void setFrmID(String value) throws Exception
	{
		SetValByKey(EnVerDtlAttr.FrmID, value);
	}
	public final String getEnPKValue()  {
		return this.GetValStringByKey(EnVerDtlAttr.EnPKValue);
	}
	public final void setEnPKValue(String value) throws Exception
	{
		SetValByKey(EnVerDtlAttr.EnPKValue, value);
	}
	/** 
	 字段
	*/
	public final String getAttrKey()  {
		return this.GetValStringByKey(EnVerDtlAttr.AttrKey);
	}

	public final void setAttrKey(String value) throws Exception
	{
		SetValByKey(EnVerDtlAttr.AttrKey, value);
	}
	/** 
	 版本主表PK
	*/
	public final String getBindKey()  {
		return this.GetValStringByKey(EnVerDtlAttr.BindKey);
	}
	public final void setBindKey(String value) throws Exception
	{
		SetValByKey(EnVerDtlAttr.BindKey, value);
	}
	/** 
	字段名
	*/
	public final String getAttrName()  {
		return this.GetValStringByKey(EnVerDtlAttr.AttrName);
	}
	public final void setAttrName(String value) throws Exception
	{
		SetValByKey(EnVerDtlAttr.AttrName, value);
	}
	public final int getLGType()  {
		return this.GetValIntByKey(EnVerDtlAttr.LGType);
	}
	public final void setLGType(int value) throws Exception
	{
		SetValByKey(EnVerDtlAttr.LGType, value);
	}
	/** 
	 旧值
	*/
	public final String getMyVal()  {
		return this.GetValStringByKey(EnVerDtlAttr.MyVal);
	}
	public final void setMyVal(String value) throws Exception {
		if (value == null)
		{
			SetValByKey(EnVerDtlAttr.MyVal, "");
		}
		else
		{
			SetValByKey(EnVerDtlAttr.MyVal, value);
		}


	}

		///#endregion


		///#region 扩展属性

		///#endregion


		///#region 构造函数
	/** 
	 工作部门角色对应
	*/
	public EnVerDtl()
	{
	}

	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_EnVerDtl", "版本明细");
		map.setEnType(EnType.Dot2Dot); //实体类型，admin 系统管理员表，PowerAble 权限管理表,也是用户表,你要想把它加入权限管理里面请在这里设置。。
		map.IndexField = EnVerDtlAttr.FrmID;

		map.AddMyPK();
		map.AddTBString(EnVerDtlAttr.RefPK, null, "关联版本主键", true, false, 0, 50, 30);
		map.AddTBString(EnVerDtlAttr.FrmID, null, "FrmID", false, false, 0, 200, 1);
		map.AddTBString(EnVerDtlAttr.EnPKValue, null, "EnPKValue", true, false, 0, 50, 30);

		map.AddTBString(EnVerDtlAttr.AttrKey, null, "字段", false, false, 0, 200, 1);
		map.AddTBString(EnVerDtlAttr.AttrName, null, "字段名", true, false, 0, 200, 30);
		map.AddTBInt(EnVerDtlAttr.LGType, 0, "逻辑类型", true, false);
		map.AddTBString(EnVerDtlAttr.BindKey, null, "外部数据源", true, false, 0, 200, 30);

		map.AddTBString(EnVerDtlAttr.MyVal, null, "数据值", true, false, 0, 4000, 30);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
