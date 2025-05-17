package bp.ccfast.ccmenu;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import java.util.*;

/** 
 权限组菜单s
*/
public class GroupMenus extends EntitiesMyPK
{

		///#region 构造
	/** 
	 权限组s
	*/
	public GroupMenus()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new GroupMenu();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<GroupMenu> ToJavaList()
	{
		return (java.util.List<GroupMenu>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GroupMenu> Tolist()
	{
		ArrayList<GroupMenu> list = new ArrayList<GroupMenu>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GroupMenu)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
