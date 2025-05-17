package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 系统字典表s
*/
public class SFTableDicts extends EntitiesNoName
{

		///#region 构造
	/** 
	 系统字典表s
	*/
	public SFTableDicts()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SFTableDict();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SFTableDict> ToJavaList()
	{
		return (java.util.List<SFTableDict>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SFTableDict> Tolist()
	{
		ArrayList<SFTableDict> list = new ArrayList<SFTableDict>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SFTableDict)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
