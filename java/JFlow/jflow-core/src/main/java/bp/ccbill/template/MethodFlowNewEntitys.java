package bp.ccbill.template;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 新建实体流程
*/
public class MethodFlowNewEntitys extends EntitiesNoName
{
	/** 
	 新建实体流程
	*/
	public MethodFlowNewEntitys()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MethodFlowNewEntity();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MethodFlowNewEntity> ToJavaList()
	{
		return (java.util.List<MethodFlowNewEntity>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MethodFlowNewEntity> Tolist()
	{
		ArrayList<MethodFlowNewEntity> list = new ArrayList<MethodFlowNewEntity>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MethodFlowNewEntity)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
