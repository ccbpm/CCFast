package bp.ccbill.template;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 功能执行
*/
public class MethodFuncs extends EntitiesNoName
{
	/** 
	 功能执行
	*/
	public MethodFuncs()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MethodFunc();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MethodFunc> ToJavaList()
	{
		return (java.util.List<MethodFunc>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MethodFunc> Tolist()
	{
		ArrayList<MethodFunc> list = new ArrayList<MethodFunc>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MethodFunc)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
