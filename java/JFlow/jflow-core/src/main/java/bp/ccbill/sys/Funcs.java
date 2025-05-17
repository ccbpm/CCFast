package bp.ccbill.sys;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.ccbill.template.*;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 独立方法s
*/
public class Funcs extends EntitiesNoName
{
	/** 
	 独立方法
	*/
	public Funcs()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Func();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Func> ToJavaList()
	{
		return (java.util.List<Func>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Func> Tolist()
	{
		ArrayList<Func> list = new ArrayList<Func>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Func)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
