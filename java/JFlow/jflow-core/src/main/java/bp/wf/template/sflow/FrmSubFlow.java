package bp.wf.template.sflow;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 父子流程
*/
public class FrmSubFlow extends Entity
{

		///#region 属性
	/** 
	 标签
	*/
	public final String getSFLab()  {
		return this.GetValStringByKey(FrmSubFlowAttr.SFLab);
	}
	/** 
	 编号
	*/
	public final String getNo() {
		return "ND" + this.getNodeID();
	}
	public final void setNo(String value) throws Exception {
		String nodeID = value.replace("ND", "");
		this.setNodeID(Integer.parseInt(nodeID));
	}

	public final AllSubFlowOverRole getAllSubFlowOverRole() {
		return AllSubFlowOverRole.forValue(this.GetValIntByKey(FrmSubFlowAttr.AllSubFlowOverRole));
	}
	/** 
	 节点ID
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(NodeAttr.NodeID);
	}
	public final void setNodeID(int value){
		this.SetValByKey(NodeAttr.NodeID, value);
	}

	/** 
	 字段列
	*/
	public final String getSFFields()  {
		return this.GetValStringByKey(FrmSubFlowAttr.SFFields);
	}
	public final void setSFFields(String value){
		this.SetValByKey(FrmSubFlowAttr.SFFields, value);
	}
	/** 
	 状态
	*/
	public final FrmSubFlowSta getHisFrmSubFlowSta() {
		return FrmSubFlowSta.forValue(this.GetValIntByKey(FrmSubFlowAttr.SFSta));
	}
	public final void setHisFrmSubFlowSta(FrmSubFlowSta value){
		this.SetValByKey(FrmSubFlowAttr.SFSta, value.getValue());
	}
	/** 
	 显示控制方式
	*/
	public final SFShowCtrl getSFShowCtrl() {
		return SFShowCtrl.forValue(this.GetValIntByKey(FrmSubFlowAttr.SFShowCtrl));
	}
	public final void setSFShowCtrl(SFShowCtrl value){
		this.SetValByKey(FrmSubFlowAttr.SFShowCtrl, value.getValue());
	}
	/** 
	 显示格式(0=表格,1=自由.)
	*/
	public final FrmWorkShowModel getHisFrmWorkShowModel() {
		return FrmWorkShowModel.forValue(this.GetValIntByKey(FrmSubFlowAttr.SFShowModel));
	}
	public final void setHisFrmWorkShowModel(FrmWorkShowModel value){
		this.SetValByKey(FrmSubFlowAttr.SFShowModel, value.getValue());
	}
	/** 
	 控件状态
	*/
	public final FrmSubFlowSta getSFSta() {
		return FrmSubFlowSta.forValue(this.GetValIntByKey(FrmSubFlowAttr.SFSta));
	}
	public final void setSFSta(FrmSubFlowSta value){
		this.SetValByKey(FrmSubFlowAttr.SFSta, value.getValue());
	}
	/** 
	 显示方式
	*/
	public final FrmWorkShowModel getSFShowModel() {
		return FrmWorkShowModel.forValue(this.GetValIntByKey(FrmSubFlowAttr.SFShowModel));
	}
	public final void setSFShowModel(FrmWorkShowModel value){
		this.SetValByKey(FrmSubFlowAttr.SFShowModel, value.getValue());
	}

	/** 
	 打开类型
	*/
	public final int getSFOpenType()  {
		return this.GetValIntByKey(FrmSubFlowAttr.SFOpenType);
	}
	public final void setSFOpenType(int value){
		this.SetValByKey(FrmSubFlowAttr.SFOpenType, value);
	}

	/** 
	 H
	*/
	public final float getSfH()  {
		return this.GetValFloatByKey(FrmSubFlowAttr.SF_H);
	}
	public final void setSfH(float value){
		this.SetValByKey(FrmSubFlowAttr.SF_H, value);
	}

	/** 
	 轨迹图是否显示?
	*/
	public final boolean getSFTrackEnable()  {
		return this.GetValBooleanByKey(FrmSubFlowAttr.SFTrackEnable);
	}
	public final void setSFTrackEnable(boolean value){
		this.SetValByKey(FrmSubFlowAttr.SFTrackEnable, value);
	}
	/** 
	 历史审核信息是否显示?
	*/
	public final boolean getSFListEnable()  {
		return this.GetValBooleanByKey(FrmSubFlowAttr.SFListEnable);
	}
	public final void setSFListEnable(boolean value){
		this.SetValByKey(FrmSubFlowAttr.SFListEnable, value);
	}
	/** 
	 在轨迹表里是否显示所有的步骤？
	*/
	public final boolean getSFIsShowAllStep()  {
		return this.GetValBooleanByKey(FrmSubFlowAttr.SFIsShowAllStep);
	}
	public final void setSFIsShowAllStep(boolean value){
		this.SetValByKey(FrmSubFlowAttr.SFIsShowAllStep, value);
	}
	/** 
	 如果用户未审核是否按照默认意见填充?
	*/
	public final boolean getSFIsFullInfo()  {
		return this.GetValBooleanByKey(FrmSubFlowAttr.SFIsFullInfo);
	}
	public final void setSFIsFullInfo(boolean value){
		this.SetValByKey(FrmSubFlowAttr.SFIsFullInfo, value);
	}
	/** 
	 默认审核信息
	*/
	public final String getSFDefInfo()  {
		return this.GetValStringByKey(FrmSubFlowAttr.SFDefInfo);
	}
	public final void setSFDefInfo(String value){
		this.SetValByKey(FrmSubFlowAttr.SFDefInfo, value);
	}
	/** 
	 节点名称.
	*/
	public final String getName()  {
		return this.GetValStringByKey("Name");
	}
	/** 
	 标题，如果为空则取节点名称.
	*/
	public final String getSFCaption() throws Exception {
		String str = this.GetValStringByKey(FrmSubFlowAttr.SFCaption);
		if (Objects.equals(str, ""))
		{
			str = "启动子流程";
		}
		return str;
	}
	public final void setSFCaption(String value){
		this.SetValByKey(FrmSubFlowAttr.SFCaption, value);
	}
	/** 
	 操作名词(审核，审定，审阅，批示)
	*/
	public final String getSFOpLabel()  {
		return this.GetValStringByKey(FrmSubFlowAttr.SFOpLabel);
	}
	public final void setSFOpLabel(String value){
		this.SetValByKey(FrmSubFlowAttr.SFOpLabel, value);
	}
	/** 
	 是否显示数字签名？
	*/
	public final boolean getSigantureEnabel()  {
		return this.GetValBooleanByKey(FrmSubFlowAttr.SigantureEnabel);
	}
	public final void setSigantureEnabel(boolean value){
		this.SetValByKey(FrmSubFlowAttr.SigantureEnabel, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		uac.IsDelete = false;
		uac.IsInsert = false;
		return uac;
	}
	/** 
	 重写主键
	*/
	@Override
	public String getPK()
	{
		return "NodeID";
	}
	/** 
	 父子流程
	*/
	public FrmSubFlow()
	{
	}
	/** 
	 父子流程
	 @param mapData
	*/
	public FrmSubFlow(String mapData) throws Exception {
		if (mapData.contains("ND") == false)
		{
			this.setHisFrmSubFlowSta(FrmSubFlowSta.Disable);
			return;
		}

		String mapdata = mapData.replace("ND", "");
		if (DataType.IsNumStr(mapdata) == false)
		{
			this.setHisFrmSubFlowSta(FrmSubFlowSta.Disable);
			return;
		}

		try
		{
			this.setNodeID(Integer.parseInt(mapdata));
		}
		catch (java.lang.Exception e)
		{
			return;
		}
		this.Retrieve();
	}
	/** 
	 父子流程
	 @param nodeID
	*/
	public FrmSubFlow(int nodeID) throws Exception {
		this.setNodeID(nodeID);
		this.Retrieve();
	}
	/** 
	 EnMap
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_Node", "父子流程");

		map.AddGroupAttr("父子流程", "");
		map.AddTBIntPK(NodeAttr.NodeID, 0, "节点ID", true, true);
		map.AddTBString(NodeAttr.Name, null, "节点名称", true, true, 0, 100, 10);
		map.AddTBString(FrmSubFlowAttr.SFLab, "子流程", "显示标签", true, false, 0, 200, 10, true);

		map.AddDDLSysEnum(FrmSubFlowAttr.SFSta,0, "组件状态", true, true, FrmSubFlowAttr.SFSta, "@0=禁用@1=启用@2=只读");
		map.SetHelperUrl(FrmSubFlowAttr.SFSta, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3982372&doc_id=31094");

		map.AddDDLSysEnum(FrmSubFlowAttr.SFShowModel, 0, "显示方式", true, true, FrmSubFlowAttr.SFShowModel, "@0=表格方式@1=自由模式"); //此属性暂时没有用.

		map.AddDDLSysEnum(FrmSubFlowAttr.SFShowCtrl, 0, "显示控制方式", true, true, FrmSubFlowAttr.SFShowCtrl, "@0=可以看所有的子流程@1=仅仅可以看自己发起的子流程"); //此属性暂时没有用.
		map.SetHelperAlert(FrmSubFlowAttr.SFShowCtrl, "是对当前节点，一个流程实例下启动的所有子流程的数据权限查看控制。");

		map.AddDDLSysEnum(FrmSubFlowAttr.AllSubFlowOverRole, 0, "所有子流程结束规则", true, true, FrmSubFlowAttr.AllSubFlowOverRole, "@0=不处理@1=当前流程自动运行下一步@2=结束当前流程");

		map.AddTBString(FrmSubFlowAttr.SFCaption, "启动子流程", "连接标题", true, false, 0, 100, 10, true);

		map.AddTBString(FrmSubFlowAttr.SFDefInfo, null, "可启动的子流程编号(多个用逗号分开)", false, false, 0, 50, 10, true);

		map.AddTBString(FrmSubFlowAttr.SFFields, null, "审批格式字段", true, false, 0, 50, 10, true);

		map.AddDDLSysEnum(FrmSubFlowAttr.SFOpenType, 0, "打开子流程显示", true, true, FrmSubFlowAttr.SFOpenType, "@0=工作查看器@1=流程轨迹"); //此属性暂时没有用.
		map.SetHelperAlert(FrmSubFlowAttr.SFOpenType, "点击子流程（一个子流程实例）的时候要打开的页面。\t\n1.工作查看器可以看到表单. \t\n2.流程轨迹看到流程运行图，时间轴. ");
		map.AddTBFloat(FrmSubFlowAttr.SF_H, 300, "高度", true, false);


		RefMethod rm = new RefMethod();
		rm.Title = "手动启动子流程";
		rm.ClassMethodName = this.toString() + ".DoSubFlowHand";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "自动触发子流程";
		rm.ClassMethodName = this.toString() + ".DoSubFlowAuto";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "延续子流程";
		rm.ClassMethodName = this.toString() + ".DoSubFlowYanXu";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 子流程。
	/** 
	 自动触发
	 
	 @return 
	*/
	public final String DoSubFlowAuto() {
		return "../../Admin/AttrNode/SubFlow/SubFlowAuto.htm?FK_Node=" + this.getNodeID() + "&tk=" + (new Random()).nextDouble();
	}
	/** 
	 手动启动子流程
	 
	 @return 
	*/
	public final String DoSubFlowHand() {
		return "../../Admin/AttrNode/SubFlow/SubFlowHand.htm?FK_Node=" + this.getNodeID() + "&tk=" + (new Random()).nextDouble();
	}
	/** 
	 延续子流程
	 
	 @return 
	*/
	public final String DoSubFlowYanXu() {
		return "../../Admin/AttrNode/SubFlow/SubFlowYanXu.htm?FK_Node=" + this.getNodeID() + "&tk=" + (new Random()).nextDouble();
	}

		///#endregion 子流程。


		///#region 重写方法.
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		return super.beforeUpdateInsertAction();
	}
	@Override
	protected void afterUpdate() throws Exception
	{
		//清空缓存，重新查数据
		Node nd = new Node(this.getNodeID());
		nd.RetrieveFromDBSources();
		Cache2019.UpdateRow(nd.toString(), String.valueOf(this.getNodeID()), nd.getRow());

		GroupField gf = new GroupField();
		if (this.getSFSta() == FrmSubFlowSta.Disable)
		{
			gf.Delete(GroupFieldAttr.CtrlID, "SubFlow" + this.getNo());
		}
		else
		{
			if (gf.IsExit(GroupFieldAttr.CtrlID, "SubFlow" + this.getNo()) == false)
			{
				gf = new GroupField();
				gf.setFrmID("ND" + this.getNodeID());
				gf.setCtrlID("SubFlow" + this.getNo());
				gf.setCtrlType(GroupCtrlType.SubFlow);
				gf.setLab("父子流程组件");
				gf.setIdx(0);
				gf.Insert(); //插入.
			}
		}

		super.afterUpdate();
	}

		///#endregion 重写方法.
}
