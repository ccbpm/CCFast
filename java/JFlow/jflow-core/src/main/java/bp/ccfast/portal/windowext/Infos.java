package bp.ccfast.portal.windowext;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import bp.ccfast.portal.*;
import java.util.*;

/** 
 通知公告s
*/
public class Infos extends EntitiesNoName
{

		///#region 构造
	/** 
	 通知公告s
	*/
	public Infos()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Info();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Info> ToJavaList()
	{
		return (java.util.List<Info>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Info> Tolist()
	{
		ArrayList<Info> list = new ArrayList<Info>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Info)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
