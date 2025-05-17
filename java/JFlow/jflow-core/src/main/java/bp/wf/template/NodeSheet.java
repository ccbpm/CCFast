package bp.wf.template;

import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.port.*;

/** 
 节点属性.
*/
public class NodeSheet extends Entity
{

		///#region 属性.
	public final int getNodeID()  {
		return this.GetValIntByKey(NodeAttr.NodeID);
	}
	public final void setNodeID(int value){
		this.SetValByKey(NodeAttr.NodeID, value);
	}
	/** 
	 节点名称
	*/
	public final String getName()  {
		return this.GetValStringByKey(NodeAttr.Name);
	}
	public final void setName(String value){
		this.SetValByKey(NodeAttr.Name, value);
	}

		///#endregion 属性.


		///#region 构造函数
	/** 
	 节点
	*/
	public NodeSheet()
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

		//map 的基 础信息.
		Map map = new Map("WF_Node", "节点");


			///#region  基础属性
		map.AddTBIntPK(NodeAttr.NodeID, 0, "节点ID", true, true);
		map.SetHelperUrl(NodeAttr.NodeID, "http://ccbpm.mydoc.io/?v=5404&t=17901");
		map.AddTBInt(NodeAttr.Step, 0, "步骤(无计算意义)", true, false);
		map.SetHelperUrl(NodeAttr.Step, "http://ccbpm.mydoc.io/?v=5404&t=17902");
		//map.SetHelperAlert(NodeAttr.Step, "它用于节点的排序，正确的设置步骤可以让流程容易读写."); //使用alert的方式显示帮助信息.
		map.AddTBString(NodeAttr.FK_Flow, null, "流程编号", false, false, 0, 5, 10, false, "http://ccbpm.mydoc.io/?v=5404&t=17023");
		map.AddTBString(NodeAttr.Name, null, "名称", true, true, 0, 100, 10, false, "http://ccbpm.mydoc.io/?v=5404&t=17903");

			///#endregion  基础属性


			///#region 对应关系用户组。
		//平铺模式.
		map.getAttrsOfOneVSM().AddGroupPanelModel(new bp.wf.template.NodeTeams(), new Teams(), bp.wf.template.NodeTeamAttr.FK_Node, bp.wf.template.NodeTeamAttr.FK_Team, "节点绑定用户组", null, "Name", "No");

		//deptAndEmp列表模式.
		map.getAttrsOfOneVSM().AddGroupListModel(new bp.wf.template.NodeTeams(), new Teams(), bp.wf.template.NodeTeamAttr.FK_Node, bp.wf.template.NodeTeamAttr.FK_Team, "节点绑定用户组AddTeamListModel", TeamAttr.FK_TeamType, "Name", "No");

			///#endregion  对应关系用户组


			///#region 对角色.
		//平铺模式.
		map.getAttrsOfOneVSM().AddGroupPanelModel(new bp.wf.template.NodeStations(), new Stations(), bp.wf.template.NodeStationAttr.FK_Node, bp.wf.template.NodeStationAttr.FK_Station, "节点绑定角色", StationAttr.FK_StationType, "Name", "No");

		//列表模式.
		map.getAttrsOfOneVSM().AddGroupListModel(new bp.wf.template.NodeStations(), new Stations(), bp.wf.template.NodeStationAttr.FK_Node, bp.wf.template.NodeStationAttr.FK_Station, "节点绑定角色AddTeamListModel", StationAttr.FK_StationType, "Name", "No");

			///#endregion 对角色.


			///#region 节点绑定人员.
		//平铺模式.
		map.getAttrsOfOneVSM().AddGroupPanelModel(new TeamEmps(), new Teams(), TeamEmpAttr.FK_Team, TeamEmpAttr.FK_Emp, "节点绑定人员", TeamAttr.FK_TeamType, "Name", "No");

		//列表模式.
		map.getAttrsOfOneVSM().AddGroupListModel(new TeamEmps(), new Teams(), TeamEmpAttr.FK_Team, TeamEmpAttr.FK_Emp, "节点绑定人员", TeamAttr.FK_TeamType, "Name", "No");

		//节点绑定部门. 节点绑定部门.
		String defDeptVal = "@WebUser.FK_Dept";
		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			defDeptVal = "@WebUser.OrgNo";
		}

		//绑定部门的.
		map.getAttrsOfOneVSM().AddBranches(new bp.wf.template.NodeDepts(), new Depts(), bp.wf.template.NodeDeptAttr.FK_Node, bp.wf.template.NodeDeptAttr.FK_Dept, "节点绑定部门AddBranches", EmpAttr.Name, EmpAttr.No, defDeptVal, "@No=编号@Name=名称");


		//节点绑定人员. 使用树杆与叶子的模式绑定.
		map.getAttrsOfOneVSM().AddBranchesAndLeaf(new bp.wf.template.NodeEmps(), new Emps(), bp.wf.template.NodeEmpAttr.FK_Node, bp.wf.template.NodeEmpAttr.FK_Emp, "节点绑定接受人", EmpAttr.FK_Dept, EmpAttr.Name, EmpAttr.No, defDeptVal, "@No=编号@Name=名称@FK_Dept=部门");


		map.AddDtl(new NodeToolbars(), NodeToolbarAttr.FK_Node);

		// 经典表单可以调用的子流程. 2014.10.19 去掉.
		//map.getAttrsOfOneVSM().Add(new BP.WF.NodeFlows(), new Flows(), NodeFlowAttr.FK_Node, NodeFlowAttr.FK_Flow, DeptAttr.Name, DeptAttr.No,
		//    "经典表单可调用的子流程");

			///#endregion

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
