package bp.wf.template;

import bp.en.*;
import bp.en.Map;
import bp.wf.*;

/** 
 节点方向
 节点的方向有两部分组成.
 1, Node.
 2, toNode.
 记录了从一个节点到其他的多个节点.
 也记录了到这个节点的其他的节点.
*/
public class Direction extends EntityMyPK
{

		///#region 基本属性
	/** 
	节点
	*/
	public final int getNode()  {
		return this.GetValIntByKey(DirectionAttr.Node);
	}
	public final void setNode(int value){
		this.SetValByKey(DirectionAttr.Node, value);
	}
	public final String getFlowNo()  {
		return this.GetValStringByKey(DirectionAttr.FK_Flow);
	}
	public final void setFlowNo(String value){
		this.SetValByKey(DirectionAttr.FK_Flow, value);
	}
	/** 
	 转向的节点
	*/
	public final int getToNode()  {
		return this.GetValIntByKey(DirectionAttr.ToNode);
	}
	public final void setToNode(int value){
		this.SetValByKey(DirectionAttr.ToNode, value);
	}
	public final int getIdx()  {
		return this.GetValIntByKey(DirectionAttr.Idx);
	}
	public final void setIdx(int value){
		this.SetValByKey(DirectionAttr.Idx, value);
	}
	public final String getDes()  {
		return this.GetValStringByKey(DirectionAttr.Des);
	}
	public final void setDes(String value){
		this.SetValByKey(DirectionAttr.Des, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 节点方向
	*/
	public Direction()
	{
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
			return this.get_enMap();

		Map map = new Map("WF_Direction", "节点方向信息");
		map.IndexField = DirectionAttr.FK_Flow;
		/*
		 * MyPK 是一个复合主键 是由 Node+'_'+ToNode+'_'+DirType 组合的. 比如: 101_102_1
		 */
		map.AddMyPK(true);
		map.AddTBString(DirectionAttr.FK_Flow, null, "流程", true, true, 0, 4, 0, false);
		map.AddTBInt(DirectionAttr.Node, 0, "从节点", false, true);
		map.AddTBInt(DirectionAttr.ToNode, 0, "到节点", false, true);
		map.AddTBString(DirectionAttr.ToNodeName, null, "到达节点名称", true, true, 0, 300, 300, false);
		map.AddTBInt(DirectionAttr.GateWay, 0, "网关显示?", true, true);
		map.AddTBString(DirectionAttr.Vertices, "", "路径数据", false, true, 0, 200, 200);
		map.AddTBString(DirectionAttr.FromPort, "", "出发点连接桩位置", false, true, 0, 50, 200);
		map.AddTBString(DirectionAttr.ToPort, "", "到达点连接桩位置", false, true, 0, 50, 200);
		map.AddTBInt(DirectionAttr.NodeType, 0, "ToNode类型0=工作,1=路由,2=抄送,3=子流程,4=结束", true, true);

		map.AddTBString(DirectionAttr.Des, null, "描述", true, true, 0, 100, 0, false);
		map.AddTBInt(DirectionAttr.Idx, 0, "计算优先级顺序", true, true);

		////相关功能。
		//map.getAttrsOfOneVSM().Add(new BP.WF.Template.DirectionStations(), new bp.port.Stations(),
		//    NodeStationAttr.FK_Node, NodeStationAttr.FK_Station,
		//    StationAttr.Name, StationAttr.No, "方向条件与角色");
		//map.AddTBInt(DirectionAttr.CondExpModel, 0, "条件计算方式", false, true);
		//map.getAttrsOfOneVSM().Add(new BP.WF.Template.NodeDepts(), new bp.port.Depts(), NodeDeptAttr.FK_Node, NodeDeptAttr.FK_Dept, DeptAttr.Name,
		//DeptAttr.No, "节点部门", Dot2DotModel.TreeDept);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		this.setMyPK(this.getFlowNo() + "_" + this.getNode() + "_" + this.getToNode());
		return super.beforeUpdateInsertAction();
	}
	/** 
	 处理pk 
	 
	 @return 
	*/
	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setMyPK(this.getFlowNo() + "_" + this.getNode() + "_" + this.getToNode());
		return super.beforeInsert();
	}
	@Override
	protected boolean beforeDelete() throws Exception
	{
		this.setMyPK(this.getFlowNo() + "_" + this.getNode() + "_" + this.getToNode());

		//删除条件.
		Conds nds = new Conds();
		nds.Delete(CondAttr.FK_Node, this.getNode(), CondAttr.ToNodeID, this.getToNode());

		return super.beforeDelete();
	}
	/** 
	 上移
	*/
	public final void DoUp() throws Exception {
		this.DoOrderUp(DirectionAttr.Node, String.valueOf(this.getNode()), DirectionAttr.Idx);
		this.UpdateHisToNDs();
	}
	/** 
	 下移
	*/
	public final void DoDown() throws Exception
	{
		this.DoOrderDown(DirectionAttr.Node, String.valueOf(this.getNode()), DirectionAttr.Idx);
		this.UpdateHisToNDs();
	}

	public final void UpdateHisToNDs() throws Exception {
		//获得方向集合处理toNodes
		Directions mydirs = new Directions(this.getNode());

		Node nd = new Node(this.getNode());

		String strs = "";
		for (Direction dir : mydirs.ToJavaList())
		{
			strs += "@" + dir.getToNode();
		}
		nd.setHisToNDs(strs);
		nd.Update();

	}
}
