package bp.wf.template;

import bp.en.*; import bp.en.Map;
import bp.wf.*;

/** 
 节点工作角色
*/
public class NodeStation extends EntityMyPK
{

		///#region 基本属性
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenAll();
		return uac;
	}
	/** 
	节点
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(NodeStationAttr.FK_Node);
	}
	public final void setNodeID(int value){
		this.SetValByKey(NodeStationAttr.FK_Node, value);
	}
	public final String getStationT()  {
		return this.GetValRefTextByKey(NodeStationAttr.FK_Station);
	}
	/** 
	 工作角色
	*/
	public final String getStationNo()  {
		return this.GetValStringByKey(NodeStationAttr.FK_Station);
	}
	public final void setStationNo(String value){
		this.SetValByKey(NodeStationAttr.FK_Station, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 节点工作角色
	*/
	public NodeStation()
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

		Map map = new Map("WF_NodeStation", "节点角色");

		map.AddMyPK(true);

		map.AddTBInt(NodeStationAttr.FK_Node, 0, "节点", false, false);
		map.AddDDLEntities(NodeStationAttr.FK_Station, null, "工作角色", new bp.port.Stations(), true);
		//map.AddTBString("FK_Station", null, "工作角色", false, false, 0, 100, 100);
		map.AddTBString("OrgNo", null, "OrgNo", false, false, 0, 100, 100);

		//map.AddTBIntPK(NodeStationAttr.FK_Node, 0, "节点", false, false);
		//// #warning ,这里为了方便用户选择，让分组都统一采用了枚举类型. edit zhoupeng. 2015.04.28. 注意jflow也要修改.
		//map.AddDDLEntitiesPK(NodeStationAttr.FK_Station, null, "工作角色",
		//   new bp.port.Stations(), true);

		this.set_enMap(map);
		return this.get_enMap();
	}
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		this.setMyPK(this.getNodeID() + "_" + this.getStationNo());
		return super.beforeUpdateInsertAction();
	}

	/** 
	 节点角色发生变化，删除该节点记忆的接收人员。
	 
	 @return 
	*/
	@Override
	protected boolean beforeInsert() throws Exception
	{
		RememberMe remeberMe = new RememberMe();
		remeberMe.Delete(RememberMeAttr.FK_Node, this.getNodeID());
		return super.beforeInsert();
	}

		///#endregion
}
