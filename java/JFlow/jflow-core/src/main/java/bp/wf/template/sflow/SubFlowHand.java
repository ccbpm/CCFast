package bp.wf.template.sflow;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 手工启动子流程.
*/
public class SubFlowHand extends EntityMyPK
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
	 主流程编号
	*/
	public final String getFlowNo()  {
		return this.GetValStringByKey(SubFlowAutoAttr.FK_Flow);
	}
	public final void setFlowNo(String value) throws Exception
	{
		SetValByKey(SubFlowAutoAttr.FK_Flow, value);
	}
	/** 
	 流程编号
	*/
	public final String getSubFlowNo()  {
		return this.GetValStringByKey(SubFlowHandAttr.SubFlowNo);
	}
	public final void setSubFlowNo(String value) throws Exception
	{
		SetValByKey(SubFlowHandAttr.SubFlowNo, value);
	}
	/** 
	 流程名称
	*/
	public final String getSubFlowName()  {
		return this.GetValStringByKey(SubFlowHandAttr.SubFlowName);
	}
	/** 
	 条件表达式.
	*/
	public final String getCondExp()  {
		return this.GetValStringByKey(SubFlowHandAttr.CondExp);
	}
	public final void setCondExp(String value) throws Exception
	{
		SetValByKey(SubFlowHandAttr.CondExp, value);
	}
	/** 
	 仅仅可以启动一次?
	*/
	public final boolean getStartOnceOnly()  {
		return this.GetValBooleanByKey(SubFlowYanXuAttr.StartOnceOnly);
	}

	/** 
	 该流程启动的子流程运行结束后才可以再次启动
	*/
	public final boolean getCompleteReStart()  {
		return this.GetValBooleanByKey(SubFlowAutoAttr.CompleteReStart);
	}
	/** 
	 表达式类型
	*/
	public final ConnDataFrom getExpType() {
		return ConnDataFrom.forValue(this.GetValIntByKey(SubFlowHandAttr.ExpType));
	}
	public final void setExpType(ConnDataFrom value) throws Exception
	{
		SetValByKey(SubFlowHandAttr.ExpType, value.getValue());
	}
	public final String getNodeID()  {
		return this.GetValStringByKey(SubFlowHandAttr.FK_Node);
	}
	public final void setNodeID(String value) throws Exception
	{
		SetValByKey(SubFlowHandAttr.FK_Node, value);
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
	 运行类型
	*/
	public final SubFlowModel getSubFlowModel() {
		return SubFlowModel.forValue(this.GetValIntByKey(SubFlowAutoAttr.SubFlowModel));
	}

	public final String getSubFlowLab()  {
		return this.GetValStringByKey(SubFlowHandAttr.SubFlowLab);
	}

	public final int getSubFlowStartModel()  {
		return this.GetValIntByKey(SubFlowAutoAttr.SubFlowStartModel);
	}

	public final FrmSubFlowSta getSubFlowSta() {
		return FrmSubFlowSta.forValue(this.GetValIntByKey(SubFlowYanXuAttr.SubFlowSta));
	}
	/** 
	 指定的流程启动后,才能启动该子流程(请在文本框配置子流程).
	*/
	public final boolean getItIsEnableSpecFlowStart() throws Exception {
		boolean val = this.GetValBooleanByKey(SubFlowAutoAttr.IsEnableSpecFlowStart);
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

		///#endregion


		///#region 构造函数
	/** 
	 手工启动子流程
	*/
	public SubFlowHand()
	{
	}

	public SubFlowHand(String mypk) throws Exception
	{
		this.setMyPK(mypk);
		this.Retrieve();
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

		Map map = new Map("WF_NodeSubFlow", "手动启动子流程");

		map.AddMyPK(true);

		map.AddTBString(SubFlowAttr.FK_Flow, null, "主流程编号", true, true, 0, 5, 100);

		map.AddTBInt(SubFlowHandAttr.FK_Node, 0, "节点", false, true);
		map.AddDDLSysEnum(SubFlowHandAttr.SubFlowType, 0, "子流程类型",true, false, SubFlowHandAttr.SubFlowType, "@0=手动启动子流程@1=触发启动子流程@2=延续子流程");

		map.AddTBString(SubFlowYanXuAttr.SubFlowNo, null, "子流程编号", true, true, 0, 10, 150, false);
		map.AddTBString(SubFlowYanXuAttr.SubFlowName, null, "子流程名称", true, true, 0, 200, 150, false);

		map.AddDDLSysEnum(SubFlowYanXuAttr.SubFlowSta, 1, "状态", true, true, SubFlowYanXuAttr.SubFlowSta, "@0=禁用@1=启用@2=只读");

		map.AddTBString(SubFlowHandAttr.SubFlowLab, null, "启动文字标签", true, false, 0, 20, 150);


		map.AddDDLSysEnum(SubFlowYanXuAttr.SubFlowModel, 0, "子流程模式", true, true, SubFlowYanXuAttr.SubFlowModel, "@0=下级子流程@1=同级子流程");

		map.AddDDLSysEnum(SubFlowAutoAttr.ParentFlowSendNextStepRole, 0, "父流程自动运行到下一步规则", true, true, SubFlowAutoAttr.ParentFlowSendNextStepRole, "@0=不处理@1=该子流程运行结束@2=该子流程运行到指定节点");


		map.AddDDLSysEnum(SubFlowAutoAttr.ParentFlowOverRole, 0, "父流程结束规则", true, true, SubFlowAutoAttr.ParentFlowSendNextStepRole, "@0=不处理@1=该子流程运行结束@2=该子流程运行到指定节点");
		map.AddTBInt(SubFlowAutoAttr.SubFlowNodeID, 0, "指定子流程节点ID", true, false);


		map.AddDDLSysEnum(SubFlowAutoAttr.IsAutoSendSLSubFlowOver, 0, "同级子流程结束规则", true, true, SubFlowAutoAttr.IsAutoSendSLSubFlowOver, "@0=不处理@1=让同级子流程自动运行下一步@2=结束同级子流程");

		map.AddBoolean(SubFlowHandAttr.StartOnceOnly, false, "仅能被调用1次(不能被重复调用).", true, true, true);

		map.AddBoolean(SubFlowHandAttr.CompleteReStart, false, "该子流程运行结束后才可以重新发起.", true, true, true);
		//启动限制规则.
		map.AddBoolean(SubFlowHandAttr.IsEnableSpecFlowStart, false, "指定的流程启动后,才能启动该子流程(请在文本框配置子流程).", true, true, true);
		map.AddTBString(SubFlowHandAttr.SpecFlowStart, null, "子流程编号", true, false, 0, 200, 150, true);
		map.SetHelperAlert(SubFlowHandAttr.SpecFlowStart, "指定的流程启动后，才能启动该子流程，多个子流程用逗号分开. 001,002");
		map.AddTBString(SubFlowHandAttr.SpecFlowStartNote, null, "备注", true, false, 0, 500, 150, true);

		//启动限制规则.
		map.AddBoolean(SubFlowHandAttr.IsEnableSpecFlowOver, false, "指定的流程结束后,才能启动该子流程(请在文本框配置子流程).", true, true, true);
		map.AddTBString(SubFlowHandAttr.SpecFlowOver, null, "子流程编号", true, false, 0, 200, 150, true);
		map.SetHelperAlert(SubFlowHandAttr.SpecFlowOver, "指定的流程结束后，才能启动该子流程，多个子流程用逗号分开. 001,002");
		map.AddTBString(SubFlowHandAttr.SpecFlowOverNote, null, "备注", true, false, 0, 500, 150, true);

		map.AddTBString(SubFlowAttr.SubFlowCopyFields, null, "父流程字段对应子流程字段", true, false, 0, 400, 150, true);

		map.AddDDLSysEnum(SubFlowAttr.BackCopyRole, 0, "子流程结束后数据字段反填规则", true, true, SubFlowAttr.BackCopyRole, "@0=不反填@1=字段自动匹配@2=按照设置的格式@3=混合模式");

		map.AddTBString(SubFlowAttr.ParentFlowCopyFields, null, "子流程字段对应父流程字段", true, false, 0, 400, 150, true);
		map.SetHelperAlert(SubFlowHandAttr.ParentFlowCopyFields, "子流程结束后，按照设置模式:格式为@SubField1=ParentField1@SubField2=ParentField2@SubField3=ParentField3,即子流程字段对应父流程字段，设置成立复制\r\n如果使用签批字段时，请使用按照设置模式");

		map.AddTBInt(SubFlowHandAttr.Idx, 0, "顺序", true, false);


		//@0=单条手工启动, 1=按照简单数据源批量启动. @2=分组数据源批量启动. @3=树形结构批量启动.
		map.AddTBInt(SubFlowHandAttr.SubFlowStartModel, 0, "启动模式", false, false);

		//@0=表格模式, 1=列表模式.
		map.AddTBInt(SubFlowHandAttr.SubFlowShowModel, 0, "展现模式", false, false);

		RefMethod rm = new RefMethod();
	   // rm.Title = "批量发起前置导航";
	  //  rm.ClassMethodName = this.ToString() + ".DoSetGuide";
	//    rm.refMethodType = RefMethodType.RightFrameOpen;
	  //  map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "发起模式";
		rm.ClassMethodName = this.toString() + ".DoStartModel";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "显示模式";
		rm.ClassMethodName = this.toString() + ".DoShowModel";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	/** 
	 发起模式.
	 
	 @return 
	*/
	public final String DoStartModel() {
		return "../../../WF/Admin/AttrNode/SubFlowStartModel/Default.htm?MyPK=" + this.getMyPK();
	}

	public final String DoShowModel() {
		return "../../../WF/Admin/AttrNode/SubFlowShowModel/Default.htm?MyPK=" + this.getMyPK();
	}

	public final String DoSetGuide() {
		return "EnOnly.htm?EnName=BP.WF.Template.SFlow.SubFlowHandGuide&MyPK=" + this.getMyPK();
	}

	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setMyPK(this.getNodeID() + "_" + this.getSubFlowNo() + "_0");
		return super.beforeInsert();
	}


		///#region 移动.
	/** 
	 上移
	 
	 @return 
	*/
	public final String DoUp() throws Exception
	{
		this.DoOrderUp(SubFlowAutoAttr.FK_Node, this.getNodeID().toString(), SubFlowAutoAttr.SubFlowType, "0", SubFlowAutoAttr.Idx);
		return "执行成功";
	}
	/** 
	 下移
	 
	 @return 
	*/
	public final String DoDown() throws Exception
	{
		this.DoOrderDown(SubFlowAutoAttr.FK_Node, this.getNodeID().toString(), SubFlowAutoAttr.SubFlowType, "0", SubFlowAutoAttr.Idx);
		return "执行成功";
	}

		///#endregion 移动.
}
