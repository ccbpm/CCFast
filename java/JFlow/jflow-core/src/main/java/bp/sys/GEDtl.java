package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import java.util.*;
import java.math.*;

/**
 通用从表
*/
public class GEDtl extends EntityOID
{
		///#region 构造函数
	@Override
	public String toString()
	{
		return this.MapDtlNo;
	}
	@Override
	public String getClassID()
	{
		return this.MapDtlNo;
	}
	public final String getRDT()  {
		return this.GetValStringByKey(GEDtlAttr.RDT);
	}
	public final void setRDT(String value){
		this.SetValByKey(GEDtlAttr.RDT, value);
	}
	public final String getRec()  {
		return this.GetValStringByKey(GEDtlAttr.Rec);
	}
	public final void setRec(String value){
		this.SetValByKey(GEDtlAttr.Rec, value);
	}
	/**
	 关联的PK值
	*/
	public final String getRefPK()  {
		return this.GetValStringByKey(GEDtlAttr.RefPK);
	}
	public final void setRefPK(String value){
		this.SetValByKey(GEDtlAttr.RefPK, value);
	}
	public final long getRefPKInt64()  {
		return this.GetValInt64ByKey(GEDtlAttr.RefPK);
	}
	public final void setRefPKInt64(long value){
		this.SetValByKey(GEDtlAttr.RefPK, value);
	}
	/**
	 行是否锁定
	*/
	public final boolean getItIsRowLock()  {
		return this.GetValBooleanByKey(GEDtlAttr.IsRowLock);
	}
	public final void setItIsRowLock(boolean value){
		this.SetValByKey(GEDtlAttr.IsRowLock, value);
	}
	/**
	 关联的PKint
	*/
	public final int getRefPKInt()  {
		return this.GetValIntByKey(GEDtlAttr.RefPK);
	}
	public final void setRefPKInt(int value){
		this.SetValByKey(GEDtlAttr.RefPK, value);
	}
	public final long getFID()  {
		return this.GetValInt64ByKey(GEDtlAttr.FID);
	}
	public final void setFID(long value){
		this.SetValByKey(GEDtlAttr.FID, value);
	}
	/**
	 主键
	*/
	public String MapDtlNo = null;
	/**
	 通用从表
	*/
	public GEDtl()
	{
	}
	/**
	 通用从表

	 @param fk_mapdtl 节点ID
	*/
	public GEDtl(String fk_mapdtl)
	{
		this.MapDtlNo = fk_mapdtl;
	}
	/**
	 通用从表

	 @param fk_mapdtl 节点ID
	 @param _oid OID
	*/
	public GEDtl(String fk_mapdtl, int _oid) throws Exception {
		this.MapDtlNo = fk_mapdtl;
		this.setOID(_oid);
	}

	/**
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		if (this.MapDtlNo == null)
		{
			throw new RuntimeException("没有给" + this.MapDtlNo + "值，您不能获取它的Map。");
		}

		MapDtl md = null;
		try {
			md = new MapDtl(this.MapDtlNo);
			this.set_enMap(md.GenerMap());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this.get_enMap();
	}
	/**
	 GEDtls
	*/
	@Override
	public Entities GetNewEntities()
	{
		if (this.MapDtlNo == null)
		{
			return new GEDtls();
		}

		return new GEDtls(this.MapDtlNo);
	}
	public final boolean ItIsChange(GEDtl dtl) throws Exception {
		Attrs attrs = dtl.getEnMap().getAttrs();
		for (Attr attr : attrs)
		{
			if (this.GetValByKey(attr.getKey()) == dtl.GetValByKey(attr.getKey()))
			{
				continue;
			}
			else
			{
				return true;
			}
		}
		return false;
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		return super.beforeUpdate();
	}
	/**
	 记录人

	 @return
	*/
	@Override
	protected boolean beforeInsert() throws Exception
	{
		// 判断是否有变化的项目，决定是否执行储存。
		MapAttrs mattrs = new MapAttrs(this.MapDtlNo);
		boolean isChange = false;
		for (MapAttr mattr : mattrs.ToJavaList())
		{
			if (isChange)
			{
				break;
			}
			switch (mattr.getKeyOfEn())
			{
				case "Rec":
				case "RDT":
			   // case "RefPK":
				case "FID":
					break;
				default:
					if (mattr.getItIsNum())
					{
						String s = this.GetValStrByKey(mattr.getKeyOfEn());
						if (DataType.IsNullOrEmpty(s))
						{
							this.SetValByKey(mattr.getKeyOfEn(), mattr.getDefVal());
							s = mattr.getDefVal().toString();
							continue;
						}
						s = s.replace("￥", "");
						s = s.replace(",", "");

						if (mattr.getDefValDecimal().equals(new BigDecimal(s)))
						{
							continue;
						}
						isChange = true;
						break;
					}
					else
					{
						if (Objects.equals(this.GetValStrByKey(mattr.getKeyOfEn()), mattr.getDefVal()))
						{
							continue;
						}
						isChange = true;
						break;
					}
			}
		}
		if (isChange == false)
		{
			return false;
		}

		this.setRec(bp.web.WebUser.getNo());
		this.setRDT(DataType.getCurrentDateTime());

		return super.beforeInsert();
	}

		///#endregion
}
