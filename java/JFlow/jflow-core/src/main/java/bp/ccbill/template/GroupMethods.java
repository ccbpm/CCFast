package bp.ccbill.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 分组-集合
*/
public class GroupMethods extends EntitiesNoName
{

		///#region 构造
	/** 
	 分组s
	*/
	public GroupMethods()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new GroupMethod();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<GroupMethod> ToJavaList()
	{
		return (java.util.List<GroupMethod>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GroupMethod> Tolist()
	{
		ArrayList<GroupMethod> list = new ArrayList<GroupMethod>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GroupMethod)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
