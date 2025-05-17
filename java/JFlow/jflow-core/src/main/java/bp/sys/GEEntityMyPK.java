package bp.sys;

import bp.en.*;
import bp.en.Map;

/** 
 通用OID实体
*/
public class GEEntityMyPK extends Entity
{

		///#region 构造函数
	/** 
	 设置或者获取主键值.
	*/
	public final String getMyPK()  {
		return this.GetValStrByKey("MyPK");
	}
	public final void setMyPK(String value){
		this.SetValByKey("MyPK", value);
	}
	/**
	 主键值
	*/
	@Override
	public String getPK()
	{
		return "MyPK";
	}
	/** 
	  主键字段
	*/
	@Override
	public String getPKField()
	{
		return "MyPK";
	}
	/** 
	 转化为类.
	 
	 @return 
	*/
	@Override
	public String toString()
	{
		return this.FrmID;
	}
	@Override
	public String getClassID()
	{
		return this.FrmID;
	}
	/** 
	 主键
	*/
	public String FrmID = null;
	/** 
	 通用OID实体
	*/
	public GEEntityMyPK()
	{
	}
	/** 
	 通用OID实体
	 
	 @param frmID 节点ID
	*/
	public GEEntityMyPK(String frmID)
	{
		this.FrmID= frmID;
	}
	public GEEntityMyPK(String frmID, String pk) throws Exception {
		this.FrmID= frmID;
		this.setPKVal(pk);
		this.Retrieve();
	}

		///#endregion


		///#region 构造映射.
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

		if (this.FrmID == null)
		{
			throw new RuntimeException("没有给" + this.FrmID + "值，您不能获取它的Map。");
		}

		try {
			this.set_enMap(MapData.GenerHisMap(this.FrmID));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this.get_enMap();
	}
	/** 
	 GEEntityMyPKs
	*/
	@Override
	public Entities GetNewEntities()
	{
		if (this.FrmID == null)
		{
			return new GEEntityMyPKs();
		}
		return new GEEntityMyPKs(this.FrmID);
	}

		///#endregion

}
