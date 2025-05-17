package bp.ccfast.ccmenu;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import java.util.*;

/** 
 部门菜单s
*/
public class DeptMenus extends EntitiesMyPK
{

		///#region 构造
	/** 
	 部门s
	*/
	public DeptMenus()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new DeptMenu();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<DeptMenu> ToJavaList()
	{
		return (java.util.List<DeptMenu>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<DeptMenu> Tolist()
	{
		ArrayList<DeptMenu> list = new ArrayList<DeptMenu>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((DeptMenu)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
