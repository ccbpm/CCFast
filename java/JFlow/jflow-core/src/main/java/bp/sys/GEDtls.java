package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 通用从表s
*/
public class GEDtls extends EntitiesOID
{

		///#region 重载基类方法
	/** 
	 节点ID
	*/
	public String MapDtlNo = null;

		///#endregion


		///#region 方法
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		if (this.MapDtlNo == null)
		{
			return new GEDtl();
		}
		return new GEDtl(this.MapDtlNo);
	}
	/** 
	 通用从表ID
	*/
	public GEDtls()
	{
	}
	/** 
	 通用从表ID
	 
	 @param fk_mapdtl
	*/
	public GEDtls(String fk_mapdtl)
	{
		this.MapDtlNo = fk_mapdtl;
	}
	public GEDtls(String fk_mapdtl, Object pkval) throws Exception {
		this.MapDtlNo = fk_mapdtl;
		this.Retrieve("RefPK", pkval);
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<GEDtl> ToJavaList()
	{
		return (java.util.List<GEDtl>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GEDtl> Tolist()
	{
		ArrayList<GEDtl> list = new ArrayList<GEDtl>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GEDtl)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
