package bp.ccfast.ccmenu;

import bp.en.*; import bp.en.Map;
import bp.port.*;
import bp.*;
import bp.ccfast.*;
import java.util.*;

/** 
 权限组人员s
*/
public class GroupEmps extends EntitiesMM
{

		///#region 构造
	/** 
	 权限组s
	*/
	public GroupEmps()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new GroupEmp();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<GroupEmp> ToJavaList()
	{
		return (java.util.List<GroupEmp>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GroupEmp> Tolist()
	{
		ArrayList<GroupEmp> list = new ArrayList<GroupEmp>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GroupEmp)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
