package bp.wf.template;

import bp.en.*;
import bp.wf.*;
import java.util.*;

/** 
 可退回的节点
*/
public class NodeReturns extends EntitiesMM
{
	/** 
	 他的退回到
	*/
	public final Nodes getHisNodes() throws Exception {
		Nodes ens = new Nodes();
		for (NodeReturn ns : this.ToJavaList())
		{
			ens.AddEntity(new Node(ns.getReturnTo()));
		}
		return ens;
	}
	/** 
	 可退回的节点
	*/
	public NodeReturns()
	{
	}
	/** 
	 可退回的节点
	 
	 @param NodeID 节点ID
	*/
	public NodeReturns(int NodeID) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeReturnAttr.FK_Node, NodeID);
		qo.DoQuery();
	}
	/** 
	 可退回的节点
	 
	 @param NodeNo NodeNo 
	*/
	public NodeReturns(String NodeNo) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeReturnAttr.ReturnTo, NodeNo);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new NodeReturn();
	}
	/** 
	 可退回的节点s
	 
	 @param sts 可退回的节点
	 @return 
	*/
	public final Nodes GetHisNodes(Nodes sts) throws Exception {
		Nodes nds = new Nodes();
		Nodes tmp = new Nodes();
		for (Node st : sts.ToJavaList())
		{
			tmp = this.GetHisNodes(st.getNo());
			for (Node nd : tmp.ToJavaList())
			{
				if (nds.contains(nd))
				{
					continue;
				}
				nds.AddEntity(nd);
			}
		}
		return nds;
	}
	/** 
	 可退回的节点
	 
	 @param NodeNo 退回到编号
	 @return 节点s
	*/
	public final Nodes GetHisNodes(String NodeNo) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeReturnAttr.ReturnTo, NodeNo);
		qo.DoQuery();

		Nodes ens = new Nodes();
		for (NodeReturn en : this.ToJavaList())
		{
			ens.AddEntity(new Node(en.getNodeID()));
		}
		return ens;
	}
	/** 
	 转向此节点的集合的Nodes
	 
	 @param nodeID 此节点的ID
	 @return 转向此节点的集合的Nodes (FromNodes) 
	*/
	public final Nodes GetHisNodes(int nodeID) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeReturnAttr.FK_Node, nodeID);
		qo.DoQuery();

		Nodes ens = new Nodes();
		for (NodeReturn en : this.ToJavaList())
		{
			ens.AddEntity(new Node(en.getReturnTo()));
		}
		return ens;
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<NodeReturn> ToJavaList()
	{
		return (java.util.List<NodeReturn>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<NodeReturn> Tolist()
	{
		ArrayList<NodeReturn> list = new ArrayList<NodeReturn>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((NodeReturn)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
