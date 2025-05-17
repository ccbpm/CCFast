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
public class CollectionFlowNewEntitys extends EntitiesNoName
{
	/** 
	 新建实体流程
	*/
	public CollectionFlowNewEntitys()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new CollectionFlowNewEntity();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<CollectionFlowNewEntity> ToJavaList()
	{
		return (java.util.List<CollectionFlowNewEntity>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<CollectionFlowNewEntity> Tolist()
	{
		ArrayList<CollectionFlowNewEntity> list = new ArrayList<CollectionFlowNewEntity>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((CollectionFlowNewEntity)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
