package bp.wf.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;


/** 
 节点部门
 节点的工作部门有两部分组成.	 
 记录了从一个节点到其他的多个节点.
 也记录了到这个节点的其他的节点.
*/
public class NodeDept extends EntityMyPK
{

		///#region 基本属性
	/** 
	节点
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(NodeDeptAttr.FK_Node);
	}
	public final void setNodeID(int value){
		this.SetValByKey(NodeDeptAttr.FK_Node, value);
	}
	/** 
	 工作部门
	*/
	public final String getDeptNo()  {
		return this.GetValStringByKey(NodeDeptAttr.FK_Dept);
	}
	public final String getDeptName()  {
		return this.GetValRefTextByKey(NodeDeptAttr.FK_Dept);
	}
	public final void setDeptNo(String value){
		this.SetValByKey(NodeDeptAttr.FK_Dept, value);
	}
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenAll();
		return super.getHisUAC();
	}

		///#endregion


		///#region 构造方法
	/** 
	 节点部门
	*/
	public NodeDept()
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

		Map map = new Map("WF_NodeDept", "节点部门");
		map.setDepositaryOfEntity(Depositary.None);
		map.setDepositaryOfMap(Depositary.Application);
		map.IndexField = NodeEmpAttr.FK_Node;

		map.AddMyPK(true);
		map.AddTBInt(NodeStationAttr.FK_Node, 0, "节点", false, false);
		map.AddDDLEntities(NodeDeptAttr.FK_Dept, null, "部门", new bp.port.Depts(), true);

		//            map.AddTBIntPK(NodeStationAttr.FK_Node, 0, "节点", false, false);
		//map.AddDDLEntitiesPK( NodeDeptAttr.FK_Dept,null,"部门",new bp.port.Depts(),true);

		this.set_enMap(map);

		return this.get_enMap();
	}

		///#endregion


	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		this.setMyPK(this.getNodeID() + "_" + this.getDeptNo());
		return super.beforeUpdateInsertAction();
	}

}
