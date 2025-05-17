package bp.en;

import bp.da.*;
import bp.*;
import java.util.*;

/** 
 通用WorkID实体s
*/
public class TSEntitiesWorkID extends Entities
{

		///#region 重载基类方法
	@Override
	public String toString()
	{
		return this._TSclassID;
	}
	/** 
	 类名
	*/
	public String _TSclassID = null;

		///#endregion


		///#region 方法
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		if (this._TSclassID == null)
		{
			return new bp.port.StationType();
		}
		//throw new Exception("没有给 _TSclassID 值，您不能获取它的 Entities。");
		return new TSEntityWorkID(this._TSclassID);
	}
	public TSEntitiesWorkID()
	{

	}
	public TSEntitiesWorkID(String tsClassID)
	{
		this._TSclassID = tsClassID;
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<TSEntityWorkID> ToJavaList()
	{
		return (java.util.List<TSEntityWorkID>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<TSEntityWorkID> Tolist()
	{
		ArrayList<TSEntityWorkID> list = new ArrayList<TSEntityWorkID>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((TSEntityWorkID)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
