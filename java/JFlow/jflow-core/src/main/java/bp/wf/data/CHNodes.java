package bp.wf.data;

import bp.en.*;

import java.util.*;

/** 
 节点时限s
*/
public class CHNodes extends Entities
{

		///#region 构造方法属性
	/** 
	 节点时限s
	*/
	public CHNodes()throws Exception
	{
	}

	public CHNodes(long WorkID) throws Exception {
		this.Retrieve(CHNodeAttr.WorkID, WorkID, null);
		return;
	}

		///#endregion


		///#region 属性
	/** 
	 节点时限
	*/
	@Override
	public Entity getNewEntity()
	{
		return new CHNode();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<CHNode> ToJavaList()throws Exception
	{
		return (java.util.List<CHNode>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<CHNode> Tolist()throws Exception
	{
		ArrayList<CHNode> list = new ArrayList<CHNode>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((CHNode)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}