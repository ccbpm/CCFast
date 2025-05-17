package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 审核组件s
*/
public class NodeWorkChecks extends Entities
{

		///#region 构造
	/** 
	 审核组件s
	*/
	public NodeWorkChecks()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new NodeWorkCheck();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<NodeWorkCheck> ToJavaList()
	{
		return (java.util.List<NodeWorkCheck>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<NodeWorkCheck> Tolist()
	{
		ArrayList<NodeWorkCheck> list = new ArrayList<NodeWorkCheck>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((NodeWorkCheck)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
