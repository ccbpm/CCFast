package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 节点人员
*/
public class NodeEmps extends EntitiesMyPK
{
	/** 
	 节点人员
	*/
	public NodeEmps()
	{
	}
	/** 
	 节点人员
	 
	 @param NodeID 节点ID
	*/
	public NodeEmps(int NodeID) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeEmpAttr.FK_Node, NodeID);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new NodeEmp();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<NodeEmp> ToJavaList()
	{
		return (java.util.List<NodeEmp>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<NodeEmp> Tolist()
	{
		ArrayList<NodeEmp> list = new ArrayList<NodeEmp>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((NodeEmp)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
