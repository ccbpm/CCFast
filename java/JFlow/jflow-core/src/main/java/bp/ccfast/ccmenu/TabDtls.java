package bp.ccfast.ccmenu;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import java.util.*;

/** 
 标签s
*/
public class TabDtls extends EntitiesNoName
{

		///#region 构造
	/** 
	 标签s
	*/
	public TabDtls()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new TabDtl();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<TabDtl> ToJavaList()
	{
		return (java.util.List<TabDtl>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<TabDtl> Tolist()
	{
		ArrayList<TabDtl> list = new ArrayList<TabDtl>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((TabDtl)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
