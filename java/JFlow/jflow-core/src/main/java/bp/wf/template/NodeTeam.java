package bp.wf.template;

import bp.en.*; import bp.en.Map;
import bp.wf.*;

/** 
 节点用户组
 节点的用户组有两部分组成.	 
 记录了从一个节点到其他的多个节点.
 也记录了到这个节点的其他的节点.
*/
public class NodeTeam extends EntityMyPK
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
		return this.GetValIntByKey(NodeTeamAttr.FK_Node);
	}
	public final void setNodeID(int value){
		this.SetValByKey(NodeTeamAttr.FK_Node, value);
	}
	public final String getFKTeamT()  {
		return this.GetValRefTextByKey(NodeTeamAttr.FK_Team);
	}
	/** 
	 用户组
	*/
	public final String getFKTeam()  {
		return this.GetValStringByKey(NodeTeamAttr.FK_Team);
	}
	public final void setFKTeam(String value){
		this.SetValByKey(NodeTeamAttr.FK_Team, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 节点用户组
	*/
	public NodeTeam()
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

		Map map = new Map("WF_NodeTeam", "节点权限组");


		map.AddMyPK(true);

		map.AddTBInt(NodeTeamAttr.FK_Node, 0, "节点", false, false);
		map.AddDDLEntities(NodeTeamAttr.FK_Team, null, "用户组", new bp.port.Teams(), true);

		this.set_enMap(map);
		return this.get_enMap();
	}

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		this.setMyPK(this.getNodeID() + "_" + this.getFKTeam());
		return super.beforeUpdateInsertAction();
	}

	/** 
	 节点权限组发生变化，删除该节点记忆的接收人员。
	 
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
