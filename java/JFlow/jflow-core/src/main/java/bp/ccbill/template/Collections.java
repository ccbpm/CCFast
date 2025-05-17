package bp.ccbill.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 集合方法
*/
public class Collections extends EntitiesNoName
{
	/** 
	 集合方法
	*/
	public Collections()
	{
	}
	/** 
	 集合方法
	 
	 @param nodeid 方法IDID
	*/
	public Collections(int nodeid) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(CollectionAttr.MethodID, nodeid);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Collection();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Collection> ToJavaList()
	{
		return (java.util.List<Collection>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Collection> Tolist()
	{
		ArrayList<Collection> list = new ArrayList<Collection>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Collection)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
