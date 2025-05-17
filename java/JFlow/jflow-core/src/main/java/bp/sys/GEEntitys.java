package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 通用OID实体s
*/
public class GEEntitys extends EntitiesOID
{

		///#region 重载基类方法
	@Override
	public String toString()
	{
		//if (this.FrmID == null)
		//    throw new Exception("@没有能 FK_MapData 给值。");
		return this.FrmID;
	}
	/** 
	 主键
	*/
	public String FrmID = null;

		///#endregion


		///#region 方法
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		if (this.FrmID == null)
		{
			return new GEEntity();
		}
		return new GEEntity(this.FrmID);
	}
	/** 
	 通用OID实体ID
	*/
	public GEEntitys()
	{
	}
	public GEEntitys(String frmID)
	{
		this.FrmID= frmID;
	}
		///#endregion

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<GEEntity> ToJavaList()
	{
		return (java.util.List<GEEntity>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GEEntity> Tolist()
	{
		ArrayList<GEEntity> list = new ArrayList<GEEntity>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GEEntity)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
