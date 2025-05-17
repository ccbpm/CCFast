package bp.ccbill;

import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import java.util.*;

/** 
 数据源实体s
*/
public class DBListDBSrcs extends EntitiesNoName
{

		///#region 构造
	/** 
	 数据源实体s
	*/
	public DBListDBSrcs()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new DBListDBSrc();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<DBListDBSrc> ToJavaList()
	{
		return (java.util.List<DBListDBSrc>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<DBListDBSrc> Tolist()
	{
		ArrayList<DBListDBSrc> list = new ArrayList<DBListDBSrc>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((DBListDBSrc)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
