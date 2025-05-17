package bp.ccfast.ccmenu;

import bp.en.*; import bp.en.Map;
import bp.port.*;
import bp.*;
import bp.ccfast.*;
import java.util.*;

/** 
 权限组s
*/
public class Groups extends EntitiesNoName
{

		///#region 构造
	/** 
	 权限组s
	*/
	public Groups()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Group();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Group> ToJavaList()
	{
		return (java.util.List<Group>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Group> Tolist()
	{
		ArrayList<Group> list = new ArrayList<Group>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Group)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
