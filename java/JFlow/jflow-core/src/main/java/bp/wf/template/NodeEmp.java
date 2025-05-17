package bp.wf.template;

import bp.en.*; import bp.en.Map;
import bp.sys.CCBPMRunModel;

/** 
 节点人员
 节点的到人员有两部分组成.	 
 记录了从一个节点到其他的多个节点.
 也记录了到这个节点的其他的节点.
*/
public class NodeEmp extends EntityMyPK
{

		///#region 基本属性
	/** 
	节点
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(NodeEmpAttr.FK_Node);
	}
	public final void setNodeID(int value){
		this.SetValByKey(NodeEmpAttr.FK_Node, value);
	}
	/** 
	 到人员
	*/
	public final String getEmpNo()  {
		return this.GetValStringByKey(NodeEmpAttr.FK_Emp);
	}
	public final void setEmpNo(String value){
		this.SetValByKey(NodeEmpAttr.FK_Emp, value);
	}
	public final String getEmpName()  {
		return this.GetValRefTextByKey(NodeEmpAttr.FK_Emp);
	}

		///#endregion


		///#region 构造方法
	/** 
	 节点人员
	*/
	public NodeEmp()
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

		Map map = new Map("WF_NodeEmp", "节点人员");
		map.IndexField = NodeEmpAttr.FK_Node;

		map.AddMyPK(true);

		map.AddTBInt(NodeEmpAttr.FK_Node, 0, "节点ID", true, true);
		map.AddDDLEntities(NodeEmpAttr.FK_Emp, null, "到人员", new bp.port.Emps(), true);

		//map.AddTBIntPK(NodeEmpAttr.FK_Node,0,"Node",true,true);
		//map.AddDDLEntitiesPK(NodeEmpAttr.FK_Emp, null, "到人员", new bp.port.Emps(), true);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		this.setMyPK(this.getNodeID() + "_" + this.getEmpNo());
		return super.beforeUpdateInsertAction();
	}


	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			this.setEmpNo(bp.web.WebUser.getOrgNo() + "_" + this.getEmpNo());
		}

		return super.beforeInsert();
	}

}
