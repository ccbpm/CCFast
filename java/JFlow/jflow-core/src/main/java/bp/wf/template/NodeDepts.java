package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 节点部门
*/
public class NodeDepts extends EntitiesMyPK
{
	/** 
	 节点部门
	*/
	public NodeDepts()
	{
	}
	/** 
	 节点部门
	 
	 @param NodeID 节点ID
	*/
	public NodeDepts(int NodeID) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeDeptAttr.FK_Node, NodeID);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new NodeDept();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<NodeDept> ToJavaList()
	{
		return (java.util.List<NodeDept>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<NodeDept> Tolist()
	{
		ArrayList<NodeDept> list = new ArrayList<NodeDept>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((NodeDept)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

}
