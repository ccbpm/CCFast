package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 节点用户组s
*/
public class NodeTeams extends EntitiesMyPK
{
	/** 
	 节点用户组s
	*/
	public NodeTeams()
	{
	}
	/** 
	 节点用户组s
	 
	 @param nodeID 节点ID
	*/
	public NodeTeams(int nodeID) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeTeamAttr.FK_Node, nodeID);
		qo.DoQuery();
	}
	/** 
	 节点用户组
	 
	 @param StationNo StationNo 
	*/
	public NodeTeams(String StationNo) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NodeTeamAttr.FK_Team, StationNo);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new NodeTeam();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<NodeTeam> ToJavaList()
	{
		return (java.util.List<NodeTeam>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<NodeTeam> Tolist()
	{
		ArrayList<NodeTeam> list = new ArrayList<NodeTeam>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((NodeTeam)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
