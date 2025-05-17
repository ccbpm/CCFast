package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 通用实体s
*/
public class GEEntityWordFrms extends EntitiesOID
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
		//if (this.FrmID == null)
		//    throw new Exception("@没有能 FK_MapData 给值。");

		if (this.FrmID == null)
		{
			return new GEEntity();
		}
		return new GEEntity(this.FrmID);
	}
	/** 
	 通用实体ID
	*/
	public GEEntityWordFrms()
	{
	}
	/** 
	 通用实体ID
	 
	 @param frmID
	*/
	public GEEntityWordFrms(String frmID)
	{
		this.FrmID= frmID;
	}
		///#endregion

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<GEEntityWordFrm> ToJavaList()
	{
		return (java.util.List<GEEntityWordFrm>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GEEntityWordFrm> Tolist()
	{
		ArrayList<GEEntityWordFrm> list = new ArrayList<GEEntityWordFrm>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GEEntityWordFrm)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
