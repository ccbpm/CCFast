package bp.wf.template.sflow;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 子流程.
*/
public class SubFlow extends EntityMyPK
{

		///#region 基本属性
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		uac.IsInsert = false;
		return uac;
	}
	/** 
	 子流程编号
	*/
	public final String getSubFlowNo()  {
		return this.GetValStringByKey(SubFlowYanXuAttr.SubFlowNo);
	}
	public final void setSubFlowNo(String value) throws Exception
	{
		SetValByKey(SubFlowYanXuAttr.SubFlowNo, value);
	}
	/** 
	 流程名称
	*/
	public final String getSubFlowName()  {
		return this.GetValStringByKey(SubFlowYanXuAttr.SubFlowName);
	}
	/** 
	 条件表达式.
	*/
	public final String getCondExp()  {
		return this.GetValStringByKey(SubFlowYanXuAttr.CondExp);
	}
	public final void setCondExp(String value) throws Exception
	{
		SetValByKey(SubFlowYanXuAttr.CondExp, value);
	}
	/** 
	 表达式类型
	*/
	public final ConnDataFrom getExpType() {
		return ConnDataFrom.forValue(this.GetValIntByKey(SubFlowYanXuAttr.ExpType));
	}
	public final void setExpType(ConnDataFrom value) throws Exception
	{
		SetValByKey(SubFlowYanXuAttr.ExpType, value.getValue());
	}
	/** 
	 主流程编号
	*/
	public final String getFlowNo()  {
		return this.GetValStringByKey(SubFlowYanXuAttr.FK_Flow);
	}
	public final void setFlowNo(String value) throws Exception
	{
		SetValByKey(SubFlowYanXuAttr.FK_Flow, value);
	}
	/** 
	 主流程NodeID
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(SubFlowYanXuAttr.FK_Node);
	}
	public final void setNodeID(int value) throws Exception
	{
		SetValByKey(SubFlowYanXuAttr.FK_Node, value);
	}
	/** 
	 发送成功后是否隐藏父流程的待办.
	*/
	public final boolean getSubFlowHidTodolist()  {
		return this.GetValBooleanByKey(SubFlowYanXuAttr.SubFlowHidTodolist);
	}
	public final void setSubFlowHidTodolist(boolean value) throws Exception
	{
		SetValByKey(SubFlowYanXuAttr.SubFlowHidTodolist, value);
	}
	public final SubFlowType getSubFlowType() {
		return SubFlowType.forValue(this.GetValIntByKey(SubFlowYanXuAttr.SubFlowType));
	}
	/** 
	 指定的流程启动后,才能启动该子流程(请在文本框配置子流程).
	*/
	public final boolean getItIsEnableSpecFlowStart() throws Exception {
		Boolean val = this.GetValBooleanByKey(SubFlowAutoAttr.IsEnableSpecFlowStart);
		if (val == false)
		{
			return false;
		}

		if (this.getSpecFlowStart().length() > 2)
		{
			return true;
		}
		return false;
	}
	/** 
	 仅仅可以启动一次?
	*/
	public final boolean getStartOnceOnly()  {
		return this.GetValBooleanByKey(SubFlowYanXuAttr.StartOnceOnly);
	}

	/** 
	 指定的流程结束后,才能启动该子流程(请在文本框配置子流程).
	*/
	public final boolean getItIsEnableSpecFlowOver() throws Exception {
		boolean val = this.GetValBooleanByKey(SubFlowAutoAttr.IsEnableSpecFlowOver);
		if (val == false)
		{
			return false;
		}

		if (this.getSpecFlowOver().length() > 2)
		{
			return true;
		}
		return false;
	}
	public final String getSpecFlowOver()  {
		return this.GetValStringByKey(SubFlowYanXuAttr.SpecFlowOver);
	}
	public final String getSpecFlowStart()  {
		return this.GetValStringByKey(SubFlowYanXuAttr.SpecFlowStart);
	}
	/** 
	 自动发起的子流程发送方式
	*/
	public final int getSendModel()  {
		return this.GetValIntByKey(SubFlowAutoAttr.SendModel);
	}

	/** 
	 父流程运行到下一步的规则
	*/
	public final SubFlowRunModel getParentFlowSendNextStepRole() {
		return SubFlowRunModel.forValue(this.GetValIntByKey(SubFlowAttr.ParentFlowSendNextStepRole));
	}
	/** 
	 父流程结束的规则
	*/
	public final SubFlowRunModel getParentFlowOverRole() {
		return SubFlowRunModel.forValue(this.GetValIntByKey(SubFlowAttr.ParentFlowOverRole));
	}

	/** 
	 同级子流程结束规则
	*/
	public final int isAutoSendSLSubFlowOver()  {
		return this.GetValIntByKey(SubFlowAutoAttr.IsAutoSendSLSubFlowOver);
	}

	public final String getSubFlowCopyFields()  {
		return this.GetValStringByKey(SubFlowAttr.SubFlowCopyFields);
	}
	public final BackCopyRole getBackCopyRole() {
		return BackCopyRole.forValue(this.GetValIntByKey(SubFlowAttr.BackCopyRole));
	}

	public final String getParentFlowCopyFields()  {
		return this.GetValStringByKey(SubFlowAttr.ParentFlowCopyFields);
	}

	public final int getSubFlowNodeID()  {
		return this.GetValIntByKey(SubFlowAttr.SubFlowNodeID);
	}

		///#endregion


		///#region 构造函数
	/** 
	 子流程
	*/
	public SubFlow()
	{
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_NodeSubFlow", "子流程(所有类型子流程属性)");
		map.IndexField = SubFlowAttr.FK_Node;

		map.AddMyPK(true);

		map.AddTBString(SubFlowAttr.FK_Flow, null, "主流程编号", true, true, 0, 5, 150);
		map.AddTBInt(SubFlowAttr.FK_Node, 0, "主流程节点", false, true);

		map.AddTBInt(SubFlowAttr.SubFlowType, 0, "子流程类型", false, true);
		map.AddTBInt(SubFlowAttr.SubFlowModel, 0, "子流程模式", false, true);

		map.AddTBInt(SubFlowAutoAttr.ParentFlowSendNextStepRole, 0, "父流程自动运行到下一步规则", false, true);
		map.AddTBInt(SubFlowAutoAttr.ParentFlowOverRole, 0, "父流程结束规则", false, true);
		map.AddTBInt(SubFlowAutoAttr.SubFlowNodeID, 0, "指定子流程节点ID", false, true);
		map.AddTBInt(SubFlowAutoAttr.IsAutoSendSLSubFlowOver, 0, "同级子流程结束规则", false, true);

		map.AddTBString(SubFlowAttr.SubFlowNo, null, "子流程编号", true, true, 0, 10, 150, false);
		map.AddTBString(SubFlowAttr.SubFlowName, null, "子流程名称", true, true, 0, 200, 150, false);

		//启动限制规则0.
		map.AddTBInt(SubFlowAttr.StartOnceOnly, 0, "仅能被调用1次", false, true);

		//启动限制规则1.
		map.AddTBInt(SubFlowAttr.IsEnableSpecFlowStart, 0, "指定的流程启动后,才能启动该子流程(请在文本框配置子流程).", false, true);
		map.AddTBString(SubFlowHandAttr.SpecFlowStart, null, "子流程编号", true, false, 0, 200, 150, true);
		map.AddTBString(SubFlowHandAttr.SpecFlowStartNote, null, "备注", true, false, 0, 500, 150, true);

		//启动限制规则2.
		map.AddTBInt(SubFlowHandAttr.IsEnableSpecFlowOver, 0, "指定的流程结束后,才能启动该子流程(请在文本框配置子流程).", true, true);
		map.AddTBString(SubFlowHandAttr.SpecFlowOver, null, "子流程编号", true, false, 0, 200, 150, true);
		map.AddTBString(SubFlowHandAttr.SpecFlowOverNote, null, "备注", true, false, 0, 500, 150, true);


		map.AddTBInt(SubFlowAttr.ExpType, 0, "表达式类型", false, true);
		map.AddTBString(SubFlowAttr.CondExp, null, "条件表达式", true, false, 0, 500, 150, true);

		map.AddTBInt(SubFlowAttr.YBFlowReturnRole, 0, "退回方式", false, true);

		map.AddTBString(SubFlowAttr.ReturnToNode, null, "要退回的节点", true, true, 0, 200, 150, false);

		map.AddTBInt(SubFlowAttr.SendModel, 0, "自动触发的子流程发送方式", false, true);
		map.AddTBInt(SubFlowAttr.SubFlowStartModel, 0, "启动模式", false, true);

		map.AddTBString(SubFlowAttr.SubFlowCopyFields, null, "父流程字段对应子流程字段", false, false, 0, 400, 150, true);
		map.AddTBInt(SubFlowAttr.BackCopyRole, 0, "子流程结束后数据字段反填规则", false,true);
		;
		map.AddTBString(SubFlowAttr.ParentFlowCopyFields, null, "子流程字段对应父流程字段", true, false, 0, 400, 150, true);

		map.AddDDLSysEnum(SubFlowYanXuAttr.SubFlowSta, 1, "状态", true, true, SubFlowYanXuAttr.SubFlowSta, "@0=禁用@1=启用@2=只读");

		map.AddTBInt(SubFlowAttr.Idx, 0, "顺序", true, false);


		map.AddTBInt(SubFlowAttr.X, 0, "X", true, false);
		map.AddTBInt(SubFlowAttr.Y, 0, "Y", true, false);


		//为中科软增加. 批量发送后，需要隐藏父流程的待办.
		map.AddTBInt(SubFlowAttr.SubFlowHidTodolist, 0, "批量发送后是否隐藏父流程待办", true, false);


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	/** 
	 设置主键
	 
	 @return 
	*/
	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setMyPK(this.getNodeID() + "_" + this.getSubFlowNo() + "_" + this.getSubFlowType().getValue());
		return super.beforeInsert();
	}
}
