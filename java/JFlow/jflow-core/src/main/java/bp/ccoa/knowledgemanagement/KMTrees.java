package bp.ccoa.knowledgemanagement;

import bp.en.*;
import java.util.*;

/** 
 知识树 s
*/
public class KMTrees extends EntitiesTree
{


		///#region 查询.

		///#endregion 重写.


		///#region 重写.
	/** 
	 知识树
	*/
	public KMTrees()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new KMTree();
	}

		///#endregion 重写.



		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<KMTree> ToJavaList()
	{
		return (java.util.List<KMTree>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<KMTree> Tolist()
	{
		ArrayList<KMTree> list = new ArrayList<KMTree>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((KMTree)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
