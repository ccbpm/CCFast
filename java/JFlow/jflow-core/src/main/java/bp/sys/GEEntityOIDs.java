package bp.sys;

import bp.en.EntitiesOID;
import bp.en.Entity;

import java.util.ArrayList;
import java.util.List;

/** 
 通用OID实体s
*/
public class GEEntityOIDs extends EntitiesOID
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
			return new GEEntityOID();
		}
		return new GEEntityOID(this.FrmID);
	}
	/**
	 通用OID实体ID
	*/
	public GEEntityOIDs()
	{
	}
	public GEEntityOIDs(String frmID)
	{
		this.FrmID= frmID;
	}
		///#endregion

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final List<GEEntityOID> ToJavaList()
	{
		return (List<GEEntityOID>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GEEntityOID> Tolist()
	{
		ArrayList<GEEntityOID> list = new ArrayList<GEEntityOID>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GEEntityOID)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
