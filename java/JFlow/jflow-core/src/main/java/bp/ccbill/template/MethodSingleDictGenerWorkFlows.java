package bp.ccbill.template;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 单实体流程查询
*/
public class MethodSingleDictGenerWorkFlows extends EntitiesMyPK
{
	/** 
	 单实体流程查询
	*/
	public MethodSingleDictGenerWorkFlows()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MethodSingleDictGenerWorkFlow();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MethodSingleDictGenerWorkFlow> ToJavaList()
	{
		return (java.util.List<MethodSingleDictGenerWorkFlow>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MethodSingleDictGenerWorkFlow> Tolist()
	{
		ArrayList<MethodSingleDictGenerWorkFlow> list = new ArrayList<MethodSingleDictGenerWorkFlow>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MethodSingleDictGenerWorkFlow)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
