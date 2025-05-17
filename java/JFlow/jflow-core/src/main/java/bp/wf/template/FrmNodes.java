package bp.wf.template;

import bp.en.*;
import bp.wf.*;
import java.util.*;

/** 
 节点表单s
*/
public class FrmNodes extends EntitiesMyPK
{

		///#region 属性.
	/** 
	 他的工作节点
	*/
	public final Nodes getHisNodes() throws Exception {
		Nodes ens = new Nodes();
		for (FrmNode ns : this.ToJavaList())
		{
			ens.AddEntity(new Node(ns.getNodeID()));
		}
		return ens;
	}

		///#endregion 属性.


		///#region 构造方法..
	/** 
	 节点表单
	*/
	public FrmNodes()
	{
	}
	/** 
	 节点表单
	 @param fk_flow
	 @param nodeID 节点ID
	*/
	public FrmNodes(String fk_flow, int nodeID) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(FrmNodeAttr.FK_Flow, fk_flow);
		qo.addAnd();
		qo.AddWhere(FrmNodeAttr.FK_Node, nodeID);

		qo.addOrderBy(FrmNodeAttr.Idx);
		qo.DoQuery();
	}
	/** 
	 节点表单
	 @param nodeID nodeID
	*/
	public FrmNodes(int nodeID) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(FrmNodeAttr.FK_Node, nodeID);
		qo.addOrderBy(FrmNodeAttr.Idx);
		qo.DoQuery();
	}

		///#endregion 构造方法..


		///#region 公共方法.
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmNode();
	}
	/** 
	 节点表单s
	 
	 @param sts 节点表单
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
	 节点表单
	 
	 @param NodeNo 工作节点编号
	 @return 节点s
	*/
	public final Nodes GetHisNodes(String NodeNo) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(FrmNodeAttr.FK_Node, NodeNo);
		qo.DoQuery();

		Nodes ens = new Nodes();
		for (FrmNode en : this.ToJavaList())
		{
			ens.AddEntity(new Node(en.getFrmID()));
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
		qo.AddWhere(FrmNodeAttr.FK_Frm, nodeID);
		qo.DoQuery();

		Nodes ens = new Nodes();
		for (FrmNode en : this.ToJavaList())
		{
			ens.AddEntity(new Node(en.getNodeID()));
		}
		return ens;
	}

		///#endregion 公共方法.


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmNode> ToJavaList()
	{
		return (java.util.List<FrmNode>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmNode> Tolist()
	{
		ArrayList<FrmNode> list = new ArrayList<FrmNode>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmNode)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

}
