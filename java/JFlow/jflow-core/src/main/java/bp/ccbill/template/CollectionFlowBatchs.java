package bp.ccbill.template;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 流程批量发起流程
*/
public class CollectionFlowBatchs extends EntitiesNoName
{
	/** 
	 流程批量发起流程
	*/
	public CollectionFlowBatchs()
	{
	}
	/** 
	 得到它的 Entity 
	 45f55
	*/
	@Override
	public Entity getNewEntity()
	{
		return new CollectionFlowBatch();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<CollectionFlowBatch> ToJavaList()
	{
		return (java.util.List<CollectionFlowBatch>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<CollectionFlowBatch> Tolist()
	{
		ArrayList<CollectionFlowBatch> list = new ArrayList<CollectionFlowBatch>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((CollectionFlowBatch)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
