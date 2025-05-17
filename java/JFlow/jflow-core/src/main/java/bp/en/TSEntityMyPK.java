package bp.en;

import bp.da.*;
import bp.*;
import java.util.*;

/** 
 通用编号名称实体
*/
public class TSEntityMyPK extends EntityMyPK
{

		///#region 构造函数
	/** 
	 主键
	*/
	@Override
	public String getPK()
	{
		return "MyPK";
	}
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
		return this._TSclassID;
	}
	/** 
	 主键ID
	*/
	@Override
	public String getClassID()
	{
		return this._TSclassID;
	}
	public TSEntityMyPK()
	{
		//构造.
		bp.port.StationType en = new bp.port.StationType();
		this.set_enMap(en.get_enMap());
	}
	/** 
	 主键
	*/
	public String _TSclassID = null;

	/** 
	 通用编号名称实体
	 
	 @param _TSclassID 类ID
	*/
	public TSEntityMyPK(String _TSclassID)
	{
		this._TSclassID = _TSclassID;
		this.set_enMap(bp.ents.Glo.GenerMap(_TSclassID));
		// this._enMap = map as Map;
		// this._enMap = BP.EnTS.Glo.GenerMap(_TSclassID);
	}
	/** 
	 通用编号名称实体
	 
	 @param classID 表单ID
	 @param pk
	*/
	public TSEntityMyPK(String classID, String pk) throws Exception {
		this._TSclassID = classID;
		this.set_enMap(bp.ents.Glo.GenerMap(_TSclassID));
		this.setMyPK(pk);
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

		if (this._TSclassID == null)
		{
			throw new RuntimeException("没有给 _TSclassID 值，您不能获取它的Map。");
		}

		this.set_enMap(bp.ents.Glo.GenerMap(this._TSclassID));
		return this.get_enMap();
	}
	/** 
	 TSEntityMyPKs
	*/
	@Override
	public Entities GetNewEntities()
	{
		if (this._TSclassID == null)
		{
			throw new RuntimeException("没有给 _TSclassID 值，您不能获取它的 Entities。");
		}
		return new TSEntitiesMyPK(this._TSclassID);
	}

		///#endregion
}
