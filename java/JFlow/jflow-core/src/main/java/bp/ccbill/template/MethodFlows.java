package bp.ccbill.template;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 执行流程
*/
public class MethodFlows extends EntitiesNoName
{
	/** 
	 执行流程
	*/
	public MethodFlows()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MethodFlow();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MethodFlow> ToJavaList()
	{
		return (java.util.List<MethodFlow>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MethodFlow> Tolist()
	{
		ArrayList<MethodFlow> list = new ArrayList<MethodFlow>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MethodFlow)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
