package bp.wf.template;

import bp.en.*; import bp.en.Map;

/** 
 可撤销的节点
 节点的撤销到有两部分组成.	 
 记录了从一个节点到其他的多个节点.
 也记录了到这个节点的其他的节点.
*/
public class NodeCancel extends EntityMM
{

		///#region 基本属性
	/** 
	撤销到
	*/
	public final int getCancelTo()  {
		return this.GetValIntByKey(NodeCancelAttr.CancelTo);
	}
	public final void setCancelTo(int value){
		this.SetValByKey(NodeCancelAttr.CancelTo, value);
	}
	/** 
	 工作流程
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(NodeCancelAttr.FK_Node);
	}
	public final void setNodeID(int value){
		this.SetValByKey(NodeCancelAttr.FK_Node, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 可撤销的节点
	*/
	public NodeCancel()
	{
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_NodeCancel", "可撤销的节点");
		map.IndexField = NodeEmpAttr.FK_Node;



		map.AddTBIntPK(NodeCancelAttr.FK_Node, 0, "节点", true, true);
		map.AddTBIntPK(NodeCancelAttr.CancelTo, 0, "撤销到", true, true);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
