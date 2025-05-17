package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 节点集合
*/
public class NodeConds extends Entities
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new NodeCond();
	}

		///#endregion


		///#region 构造方法
	/** 
	 节点集合
	*/
	public NodeConds()
	{
	}
	/** 
	 节点集合.
	 @param fk_flow
	*/
	public NodeConds(String fk_flow) throws Exception {
		this.Retrieve(NodeAttr.FK_Flow, fk_flow, NodeAttr.Step);
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<NodeCond> ToJavaList()
	{
		return (java.util.List<NodeCond>)(Object)this;
	}
	/** 
	 转化成list 为了翻译成java的需要
	 
	 @return List
	*/
	public final ArrayList<NodeCond> Tolist()
	{
		ArrayList<NodeCond> list = new ArrayList<NodeCond>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((NodeCond)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

}
