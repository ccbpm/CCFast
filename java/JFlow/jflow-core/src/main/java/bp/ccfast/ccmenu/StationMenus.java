package bp.ccfast.ccmenu;

import bp.en.*;
import java.util.*;

/** 
 角色菜单s
*/
public class StationMenus extends EntitiesMyPK
{

		///#region 构造
	/** 
	 角色s
	*/
	public StationMenus()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new StationMenu();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<StationMenu> ToJavaList()
	{
		return (java.util.List<StationMenu>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<StationMenu> Tolist()
	{
		ArrayList<StationMenu> list = new ArrayList<StationMenu>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((StationMenu)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
