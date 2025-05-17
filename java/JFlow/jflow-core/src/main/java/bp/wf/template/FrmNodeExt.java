package bp.wf.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.wf.Glo;
import bp.wf.template.sflow.*;
import bp.wf.*;

/** 
 节点表单
 节点的工作节点有两部分组成.	 
 记录了从一个节点到其他的多个节点.
 也记录了到这个节点的其他的节点.
*/
public class FrmNodeExt extends EntityMyPK
{

		///#region 属性.
	public final String getFKFrm()  {
		return this.GetValStrByKey(FrmNodeAttr.FK_Frm);
	}
	public final int getNodeID()  {
		return this.GetValIntByKey(FrmNodeAttr.FK_Node);
	}
	/** 
	 @李国文 
	*/
	public final String getFlowNo()  {
		return this.GetValStringByKey(FrmNodeAttr.FK_Flow);
	}

	/** 
	 是否启用节点组件?
	*/
	public final FrmWorkCheckSta isEnableFWC() {
		return FrmWorkCheckSta.forValue(this.GetValIntByKey(FrmNodeAttr.IsEnableFWC));
	}
	public final void setEnableFWC(FrmWorkCheckSta value){
		this.SetValByKey(FrmNodeAttr.IsEnableFWC, value.getValue());
	}

	/** 
	 签批字段
	*/
	public final String getCheckField()  {
		return this.GetValStringByKey(NodeWorkCheckAttr.CheckField);
	}

	public final FrmSubFlowSta getSFSta() {
		return FrmSubFlowSta.forValue(this.GetValIntByKey(FrmSubFlowAttr.SFSta));
	}
	public final void setSFSta(FrmSubFlowSta value){
		this.SetValByKey(FrmSubFlowAttr.SFSta, value.getValue());
	}

		///#endregion


		///#region 基本属性
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();

		//权限控制.
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			uac.OpenForSysAdmin();
		}
		else
		{
			uac.OpenAll();
		}

		uac.IsInsert = false;

		return uac;
	}


		///#endregion


		///#region 构造方法
	/** 
	 节点表单
	*/
	public FrmNodeExt()
	{
	}
	/** 
	 节点表单
	 
	 @param mypk
	*/
	public FrmNodeExt(String mypk) throws Exception
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

		Map map = new Map("WF_FrmNode", "节点表单");


			///#region 基本信息.
		map.AddGroupAttr("基本信息", "");
		map.AddMyPK(true);
		//map.AddTBString(FrmNodeAttr.FK_Frm, null, "表单", true, true, 0, 300, 20);
		map.AddDDLEntities(FrmNodeAttr.FK_Frm, null, "表单", new MapDatas(), false);
		map.AddTBInt(FrmNodeAttr.FK_Node, 0, "节点ID", true, true);

		map.AddBoolean(FrmNodeAttr.IsPrint, false, "是否可以打印", true, true);
		map.AddBoolean(FrmNodeAttr.IsEnableLoadData, false, "是否启用装载填充事件", true, true);

		map.AddBoolean(FrmNodeAttr.IsCloseEtcFrm, false, "打开时是否关闭其它的页面？", true, true, true);
		map.SetHelperAlert(FrmNodeAttr.IsCloseEtcFrm, "默认为不关闭,当该表单以tab标签也打开时,是否关闭其它的tab页?");

		map.AddDDLSysEnum(FrmNodeAttr.WhoIsPK, 0, "谁是主键?", true, true);
		map.SetHelperAlert(FrmNodeAttr.WhoIsPK, "用来控制谁是表单事例的主键的方案，对于父子流程如果子流程需要在看到父流程的表单，就需要设置ParentID是主键。");

		map.AddDDLSysEnum(FrmNodeAttr.FrmSln, 0, "控制方案", true, true, FrmNodeAttr.FrmSln, "@0=默认方案@1=只读方案@2=自定义方案");
		map.SetHelperAlert(FrmNodeAttr.FrmSln, "控制该表单数据元素权限的方案，如果是自定义方案，就要设置每个表单元素的权限.");

		//map.AddBoolean(FrmNodeAttr.IsEnableFWC, false, "是否启用审核组件？", true, true, true);

		//单据编号对应字段
		map.AddDDLSQL(NodeWorkCheckAttr.BillNoField, null, "单据编号对应字段", Glo.getSQLOfBillNo(), true);


		map.AddTBString(FrmNodeAttr.FrmNameShow, null, "表单显示名字", true, false, 0, 100, 20);
		map.SetHelperAlert(FrmNodeAttr.FrmNameShow, "显示在表单树上的名字,默认为空,表示与表单的实际名字相同.多用于节点表单的名字在表单树上显示.");

		//map.AddDDLSysEnum(BP.WF.Template.FrmWorkCheckAttr.FWCSta, 0, "审核组件(是否启用审核组件？)", true, true);

		//add 2016.3.25.
		map.AddBoolean(FrmNodeAttr.Is1ToN, false, "是否1变N？(分流节点有效)", true, true, true);
		map.AddTBString(FrmNodeAttr.HuiZong, null, "汇总的数据表名", true, false, 0, 300, 20);
		map.SetHelperAlert(FrmNodeAttr.HuiZong, "子线程要汇总的数据表，对当前节点是子线程节点有效。");

		//模版文件，对于office表单有效.
		map.AddTBString(FrmNodeAttr.TempleteFile, null, "模版文件", true, false, 0, 500, 20);

		//是否显示.
		//  map.AddTBString(FrmNodeAttr.GuanJianZiDuan, null, "关键字段", true, false, 0, 20, 20);

		//显示的
		map.AddTBInt(FrmNodeAttr.Idx, 0, "顺序号", true, false);
		map.SetHelperAlert(FrmNodeAttr.Idx, "在表单树上显示的顺序,可以通过列表调整.");


			///#endregion 基本信息.


			///#region 组件属性.
		map.AddDDLSysEnum(FrmNodeAttr.IsEnableFWC, FrmWorkCheckSta.Disable.getValue(), "审核组件状态", true, true, NodeWorkCheckAttr.FWCSta, "@0=禁用@1=启用@2=只读");
		map.SetHelperAlert(FrmNodeAttr.IsEnableFWC, "控制该表单是否启用审核组件？如果启用了就显示在该表单上;");

		//签批字段
		map.AddDDLSQL(NodeWorkCheckAttr.CheckField, null, "签批字段", Glo.getSQLOfCheckField(), true);

		map.AddDDLSysEnum(FrmSubFlowAttr.SFSta, FrmSubFlowSta.Disable.getValue(), "父子流程组件状态", true, true, FrmSubFlowAttr.SFSta, "@0=禁用@1=启用@2=只读");

			///#endregion


			///#region 隐藏字段.
		//@0=始终启用@1=有数据时启用@2=有参数时启用@3=按表单的字段表达式@4=按SQL表达式@5=不启用@6=按角色@7=按部门.
		map.AddTBInt(FrmNodeAttr.FrmEnableRole, 0, "启用规则", false, false);
		map.AddTBString(FrmNodeAttr.FrmEnableExp, null, "启用的表达式", true, false, 0, 900, 20);
		map.AddTBString(FrmNodeAttr.FK_Flow, null, "流程编号", false, false, 0, 4, 20);

			///#endregion 隐藏字段.


			///#region 相关功能..
		map.AddGroupMethod("基本功能");
		RefMethod rm = new RefMethod();
		rm.Title = "设计表单";
		rm.ClassMethodName = this.toString() + ".DoDFrm()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-settings"; //正则表达式
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "启用规则";
		rm.ClassMethodName = this.toString() + ".DoEnableRole()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-settings"; //正则表达式
		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "表单启用规则";
		//rm.ClassMethodName = this.ToString() + ".DoFrmEnableRole()";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "审核组件设置";
		//rm.Icon = ../../Img/Mobile.png";
		rm.ClassMethodName = this.toString() + ".DoFrmNodeWorkCheck";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		// rm.GroupName = "表单组件";
		rm.Icon = "icon-settings"; //正则表达式
		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "表单列表";
		//rm.ClassMethodName = this.ToString() + ".DoBindFrms()";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "批量设置";
		rm.ClassMethodName = this.toString() + ".DoBatchSetting()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-settings"; //正则表达式
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "改变表单类型";
		rm.ClassMethodName = this.toString() + ".DoChangeFrmType()";
		rm.getHisAttrs().AddDDLSysEnum("FrmType", 0, "修改表单类型", true, true);
		rm.Icon = "icon-settings"; //正则表达式
		map.AddRefMethod(rm);

			///#endregion 基本设置.


			///#region 表单元素权限.
		map.AddGroupMethod("表单元素权限");
		rm = new RefMethod();
		rm.Title = "字段权限";
		rm.ClassMethodName = this.toString() + ".DoFields()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "从表权限";
		rm.ClassMethodName = this.toString() + ".DoDtls()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "附件权限";
		rm.ClassMethodName = this.toString() + ".DoAths()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "图片附件权限";
		rm.ClassMethodName = this.toString() + ".DoImgAths()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "从其他节点Copy权限设置";
		rm.ClassMethodName = this.toString() + ".DoCopyFromNode()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-settings"; //正则表达式
		map.AddRefMethod(rm);

			///#endregion 表单元素权限.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


	public final String DoBatchSetting() throws Exception {
		//return "../../Admin/Sln/BindFrms.htm?FK_Node=" + this.getNodeID() + "&FK_Flow=" + this.FlowNo;

		return "../../Admin/AttrNode/FrmSln/BatchEditSln.htm?NodeID=" + this.getNodeID() + "&MyPK=" + this.getMyPK() + "&FrmID=" + this.getFKFrm();

	}
	/** 
	 设计表单
	 
	 @return 
	*/
	public final String DoDFrm() {
		return "../../Admin/Sln/BindFrms.htm?FK_Node=" + this.getNodeID() + "&FK_Flow=" + this.getFlowNo();
	}
	/** 
	 打开绑定表单
	 
	 @return 
	*/
	public final String DoBindFrms() {
		return "../../Admin/Sln/BindFrms.htm?FK_Node=" + this.getNodeID() + "&FK_Flow=" + this.getFlowNo();
	}

	/** 
	 审核组件
	 
	 @return 
	*/
	public final String DoFrmNodeWorkCheck() {
		return "../../Comm/EnOnly.htm?EnName=BP.WF.Template.FrmWorkCheck&PKVal=" + this.getNodeID() + "&CheckField=" + this.getCheckField() + "&FK_Frm=" + this.getFKFrm() + "&t=" + DataType.getCurrentDateTime();
	}

	/** 
	 改变表单类型
	 
	 @param val 要改变的类型
	 @return 
	*/
	public final String DoChangeFrmType(int val) throws Exception {
		MapData md = new MapData(this.getFKFrm());
		String str = "原来的是:" + md.getHisFrmTypeText() + "类型，";
		md.setHisFrmTypeInt(val);
		str += "现在修改为：" + md.getHisFrmTypeText() + "类型";
		md.Update();

		return str;
	}

	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		Node node = new Node();
		node.setNodeID(this.getNodeID());
		int i = node.RetrieveFromDBSources();
		if (i != 0 && (node.getHisFormType() == NodeFormType.RefOneFrmTree || node.getHisFormType() == NodeFormType.FoolTruck))
		{
			node.setFrmWorkCheckSta(this.isEnableFWC());
			node.SetValByKey("CheckField", this.getCheckField());
			node.Update();
		}
		FrmSubFlow frmSubFlow = new FrmSubFlow(this.getNodeID());
		frmSubFlow.setSFSta(this.getSFSta());
		super.afterInsertUpdateAction();
	}


		///#region 表单元素权限.
	public final String DoDtls() {
		return "../../Admin/Sln/Dtls.htm?FK_MapData=" + this.getFKFrm() + "&FK_Node=" + this.getNodeID() + "&FK_Flow=" + this.getFlowNo() + "&DoType=Field";
	}
	public final String DoFields() {
		return "../../Admin/Sln/Fields.htm?FK_MapData=" + this.getFKFrm() + "&FK_Node=" + this.getNodeID() + "&FK_Flow=" + this.getFlowNo() + "&DoType=Field";
	}

	public final String DoComponents() {
		return "../../Admin/Sln/Components.htm?FK_MapData=" + this.getFKFrm() + "&FK_Node=" + this.getNodeID() + "&FK_Flow=" + this.getFlowNo() + "&DoType=Field";
	}
	public final String DoAths() {
		return "../../Admin/Sln/Aths.htm?FK_MapData=" + this.getFKFrm() + "&FK_Node=" + this.getNodeID() + "&FK_Flow=" + this.getFlowNo() + "&DoType=Field";
	}

	public final String DoImgAths() {
		return "../../Admin/Sln/ImgAths.htm?FK_MapData=" + this.getFKFrm() + "&FK_Node=" + this.getNodeID() + "&FK_Flow=" + this.getFlowNo() + "&DoType=Field";
	}

	public final String DoCopyFromNode() {
		return "../../Admin/Sln/Aths.htm?FK_MapData=" + this.getFKFrm() + "&FK_Node=" + this.getNodeID() + "&FK_Flow=" + this.getFlowNo() + "&DoType=Field";
	}
	public final String DoEnableRole() {
		return "../../Admin/AttrNode/BindFrmsNodeEnableRole.htm?MyPK=" + this.getMyPK();
	}

		///#endregion 表单元素权限.

}
