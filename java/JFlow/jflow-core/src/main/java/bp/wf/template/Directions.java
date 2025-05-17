package bp.wf.template;

import bp.en.*;
import bp.wf.*;
import java.util.*;

/** 
 节点方向
*/
public class Directions extends Entities
{
	/** 
	 节点方向
	*/
	public Directions()
	{
	}
	/** 
	 方向
	 
	 @param flowNo
	*/
	public Directions(String flowNo) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(DirectionAttr.FK_Flow, flowNo);
		qo.addOrderBy("Node,Idx");
		qo.DoQuery();
	}

	/** 
	 节点方向
	 
	 @param NodeID 节点ID
	*/
	public Directions(int NodeID) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(DirectionAttr.Node, NodeID);
		qo.addAnd();
		qo.AddWhereIn(DirectionAttr.NodeType, "(0,1)"); //普通节点+路由节点

		qo.addOrderBy(DirectionAttr.Idx); //方向条件的优先级.
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Direction();
	}
	/** 
	 此节点的转向方向集合
	 
	 @param nodeID 此节点的ID
	 @param isLifecyle 是不是判断在节点的生存期内		 
	 @return 转向方向集合(ToNodes) 
	*/
	public final Nodes GetHisToNodes(int nodeID, boolean isLifecyle) throws Exception {
		Nodes nds = new Nodes();
		QueryObject qo = new QueryObject(nds);
		qo.AddWhereInSQL(NodeAttr.NodeID, "SELECT ToNode FROM WF_Direction WHERE Node=" + nodeID + " AND (NodeType=0 OR NodeType=1) ");
		qo.DoQuery();
		return nds;
	}
	/** 
	 转向此节点的集合的Nodes
	 
	 @param nodeID 此节点的ID
	 @return 转向此节点的集合的Nodes (FromNodes) 
	*/
	public final Nodes GetHisFromNodes(int nodeID) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(DirectionAttr.ToNode, nodeID);
		qo.DoQuery();
		Nodes ens = new Nodes();
		for (Direction en : this.ToJavaList())
		{
			ens.AddEntity(new Node(en.getNode()));
		}
		return ens;
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Direction> ToJavaList()
	{
		return (java.util.List<Direction>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Direction> Tolist()
	{
		ArrayList<Direction> list = new ArrayList<Direction>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Direction)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

}
