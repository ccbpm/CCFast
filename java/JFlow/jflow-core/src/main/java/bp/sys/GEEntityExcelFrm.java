package bp.sys;

import bp.en.*;
import bp.en.Map;
import java.util.*;

/** 
 excel表单实体
*/
public class GEEntityExcelFrm extends EntityOID
{

		///#region 属性。
	/** 
	 最后修改人
	*/
	public final String getLastEditer()  {
		return this.GetValStringByKey(GEEntityExcelFrmAttr.LastEditer);
	}
	public final void setLastEditer(String value){
		this.SetValByKey(GEEntityExcelFrmAttr.LastEditer, value);
	}
	/** 
	 记录时间
	*/
	public final String getRDT()  {
		return this.GetValStringByKey(GEEntityExcelFrmAttr.RDT);
	}
	public final void setRDT(String value){
		this.SetValByKey(GEEntityExcelFrmAttr.RDT, value);
	}
	/** 
	 文件路径
	*/
	public final String getFilePath()  {
		return this.GetValStringByKey(GEEntityExcelFrmAttr.FilePath);
	}
	public final void setFilePath(String value){
		this.SetValByKey(GEEntityExcelFrmAttr.FilePath, value);
	}

		///#endregion 属性。


		///#region 构造函数
	@Override
	public String getPK()
	{
		return "OID";
	}
	@Override
	public String getPKField()
	{
		return "OID";
	}
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
	 通用实体
	*/
	public GEEntityExcelFrm()
	{
	}
	/** 
	 通用实体
	 
	 @param fk_mapdata 节点ID
	*/
	public GEEntityExcelFrm(String fk_mapdata)
	{
		this.FrmID=fk_mapdata;
	}
	/** 
	 通用实体
	 
	 @param fk_mapdata 节点ID
	 @param oid OID
	*/
	public GEEntityExcelFrm(String fk_mapdata, int oid) throws Exception {
		this.FrmID=fk_mapdata;
		this.setOID(oid);
		int i = this.RetrieveFromDBSources();
	}

		///#endregion


		///#region Map
	/** 
	 重写基类方法=
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
	 GEEntitys
	*/
	@Override
	public Entities GetNewEntities()
	{
		if (this.FrmID == null)
		{
			return new GEEntityExcelFrms();
		}
		return new GEEntityExcelFrms(this.FrmID);
	}

		///#endregion


		///#region 其他属性.
	private ArrayList _Dtls = null;
	public final ArrayList getDtls()
	{
		if (_Dtls == null)
		{
			_Dtls = new ArrayList();
		}
		return _Dtls;
	}

		///#endregion 其他属性.
}
