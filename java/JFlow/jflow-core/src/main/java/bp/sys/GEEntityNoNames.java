package bp.sys;

import bp.en.EntitiesOID;
import bp.en.Entity;

import java.util.ArrayList;
import java.util.List;

/** 
 通用OID实体s
*/
public class GEEntityNoNames extends EntitiesOID
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
	public Entity getNewEntity()  {
		if (this.FrmID == null)
		{
			return new GEEntityNoName();
		}
		return new GEEntityNoName(this.FrmID);
	}
	/**
	 通用OID实体ID
	*/
	public GEEntityNoNames()
	{
	}
	public GEEntityNoNames(String frmID)
	{
		this.FrmID= frmID;
	}
		///#endregion

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final List<GEEntityNoName> ToJavaList()
	{
		return (List<GEEntityNoName>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GEEntityNoName> Tolist()
	{
		ArrayList<GEEntityNoName> list = new ArrayList<GEEntityNoName>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GEEntityNoName)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
