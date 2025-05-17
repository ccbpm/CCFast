package bp.ccbill.template;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 连接方法
*/
public class MethodLinks extends EntitiesNoName
{
	/** 
	 连接方法
	*/
	public MethodLinks()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MethodLink();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MethodLink> ToJavaList()
	{
		return (java.util.List<MethodLink>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MethodLink> Tolist()
	{
		ArrayList<MethodLink> list = new ArrayList<MethodLink>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MethodLink)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
