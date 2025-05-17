package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 用户自定义表s
*/
public class SFTableSQLs extends EntitiesNoName
{

		///#region 构造
	/** 
	 用户自定义表s
	*/
	public SFTableSQLs()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SFTableSQL();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SFTableSQL> ToJavaList()
	{
		return (java.util.List<SFTableSQL>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SFTableSQL> Tolist()
	{
		ArrayList<SFTableSQL> list = new ArrayList<SFTableSQL>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SFTableSQL)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
