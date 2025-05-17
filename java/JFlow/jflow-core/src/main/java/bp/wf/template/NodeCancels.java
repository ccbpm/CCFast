package bp.wf.template;

import bp.en.*;
import bp.wf.*;
import java.util.*;

/** 
 可撤销的节点
*/
public class NodeCancels extends EntitiesMM
{

		///#region 构造与属性.
	/** 
	 他的撤销到
	*/
	public final Nodes getHisNodes() throws Exception {
		Nodes ens = new Nodes();
		for (NodeCancel ns : this.ToJavaList())
		{
			ens.AddEntity(new Node(ns.getCancelTo()));
		}
		return ens;
	}
	/** 
	 可撤销的节点
	*/
	public NodeCancels()
	{
	}
	/** 
	 可撤销的节点
	 
	 @param NodeID 节点ID
	*/
	public NodeCancels(int NodeID) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeCancelAttr.FK_Node, NodeID);
		qo.DoQuery();
	}
	/** 
	 可撤销的节点
	 
	 @param NodeNo NodeNo 
	*/
	public NodeCancels(String NodeNo) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeCancelAttr.CancelTo, NodeNo);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new NodeCancel();
	}

		///#endregion 构造与属性.


		///#region 公共方法.
	/** 
	 可撤销的节点s
	 
	 @param sts 可撤销的节点
	 <Cancels></Cancels>
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
	 可撤销的节点
	 
	 @param NodeNo 撤销到编号
	 <Cancels>节点s</Cancels>
	*/
	public final Nodes GetHisNodes(String NodeNo) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeCancelAttr.CancelTo, NodeNo);
		qo.DoQuery();

		Nodes ens = new Nodes();
		for (NodeCancel en : this.ToJavaList())
		{
			ens.AddEntity(new Node(en.getNodeID()));
		}
		return ens;
	}
	/** 
	 转向此节点的集合的Nodes
	 
	 @param nodeID 此节点的ID
	 <Cancels>转向此节点的集合的Nodes (FromNodes)</Cancels> 
	*/
	public final Nodes GetHisNodes(int nodeID) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeCancelAttr.FK_Node, nodeID);
		qo.DoQuery();

		Nodes ens = new Nodes();
		for (NodeCancel en : this.ToJavaList())
		{
			ens.AddEntity(new Node(en.getCancelTo()));
		}
		return ens;
	}

		///#endregion 公共方法.


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<NodeCancel> ToJavaList()
	{
		return (java.util.List<NodeCancel>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<NodeCancel> Tolist()
	{
		ArrayList<NodeCancel> list = new ArrayList<NodeCancel>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((NodeCancel)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
