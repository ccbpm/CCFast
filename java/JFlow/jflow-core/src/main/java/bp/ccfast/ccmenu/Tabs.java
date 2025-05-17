package bp.ccfast.ccmenu;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import java.util.*;

/** 
 标签容器s
*/
public class Tabs extends EntitiesNoName
{

		///#region 构造
	/** 
	 标签容器s
	*/
	public Tabs()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Tab();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Tab> ToJavaList()
	{
		return (java.util.List<Tab>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Tab> Tolist()
	{
		ArrayList<Tab> list = new ArrayList<Tab>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Tab)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
