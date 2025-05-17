package bp.wf.template;

import bp.da.*;
import bp.sys.*;
import bp.en.*; import bp.en.Map;
import bp.wf.*;

/**
 节点表单的审核组件
*/
public class NodeWorkCheck extends Entity
{

		///#region 属性
	/**
	 节点编号
	*/
	public final String getNo() {
		return "ND" + this.getNodeID();
	}
	public final void setNo(String value) throws Exception {
		String nodeID = value.replace("ND", "");
		this.setNodeID(Integer.parseInt(nodeID));
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
	 状态
	*/
	public final FrmWorkCheckSta getHisFrmWorkCheckSta() {
		return FrmWorkCheckSta.forValue(this.GetValIntByKey(NodeWorkCheckAttr.FWCSta));
	}
	public final void setHisFrmWorkCheckSta(FrmWorkCheckSta value){
		this.SetValByKey(NodeWorkCheckAttr.FWCSta, value.getValue());
	}
	/**
	 显示格式(0=表格,1=自由.)
	*/
	public final FrmWorkShowModel getHisFrmWorkShowModel() {
		return FrmWorkShowModel.forValue(this.GetValIntByKey(NodeWorkCheckAttr.FWCShowModel));
	}
	public final void setHisFrmWorkShowModel(FrmWorkShowModel value){
		this.SetValByKey(NodeWorkCheckAttr.FWCShowModel, value.getValue());
	}
	/**
	 附件类型
	*/
	public final FWCAth getFWCAth() {
		return FWCAth.forValue(this.GetValIntByKey(NodeWorkCheckAttr.FWCAth));
	}
	public final void setFWCAth(FWCAth value){
		this.SetValByKey(NodeWorkCheckAttr.FWCAth, value.getValue());
	}
	/**
	 组件类型
	*/
	public final FWCType getHisFrmWorkCheckType() {
		return FWCType.forValue(this.GetValIntByKey(NodeWorkCheckAttr.FWCType));
	}
	public final void setHisFrmWorkCheckType(FWCType value){
		this.SetValByKey(NodeWorkCheckAttr.FWCType, value.getValue());
	}
	/**
	 标签
	*/
	public final String getFWCLab()  {
		return this.GetValStrByKey(NodeWorkCheckAttr.FWCLab);
	}
	/**
	 组件类型名称
	*/
	public final String getFWCTypeT()  {
		return this.GetValRefTextByKey(NodeWorkCheckAttr.FWCType);
	}

	/**
	 H
	*/
	public final float getFwcH()  {
		return this.GetValFloatByKey(NodeWorkCheckAttr.FWC_H);
	}
	public final void setFwcH(float value){
		this.SetValByKey(NodeWorkCheckAttr.FWC_H, value);
	}
	public final String getFWCHstr() throws Exception {
		if (this.getFwcH() == 0)
		{
			return "100%";
		}
		return this.getFwcH() + "px";
	}
	/**
	 轨迹图是否显示?
	*/
	public final boolean getFWCTrackEnable()  {
		return this.GetValBooleanByKey(NodeWorkCheckAttr.FWCTrackEnable);
	}
	public final void setFWCTrackEnable(boolean value){
		this.SetValByKey(NodeWorkCheckAttr.FWCTrackEnable, value);
	}
	/**
	 历史审核信息是否显示?
	*/
	public final boolean getFWCListEnable()  {
		return this.GetValBooleanByKey(NodeWorkCheckAttr.FWCListEnable);
	}
	public final void setFWCListEnable(boolean value){
		this.SetValByKey(NodeWorkCheckAttr.FWCListEnable, value);
	}
	/**
	 在轨迹表里是否显示所有的步骤？
	*/
	public final boolean getFWCIsShowAllStep()  {
		return this.GetValBooleanByKey(NodeWorkCheckAttr.FWCIsShowAllStep);
	}
	public final void setFWCIsShowAllStep(boolean value){
		this.SetValByKey(NodeWorkCheckAttr.FWCIsShowAllStep, value);
	}
	/**
	 是否显示轨迹在没有走到的节点
	*/
	public final boolean getFWCIsShowTruck()  {
		return this.GetValBooleanByKey(NodeWorkCheckAttr.FWCIsShowTruck);
	}
	public final void setFWCIsShowTruck(boolean value){
		this.SetValByKey(NodeWorkCheckAttr.FWCIsShowTruck, value);
	}
	/**
	 是否显示退回信息？
	*/
	public final int getFWCIsShowReturnMsg()  {
		return this.GetValIntByKey(NodeWorkCheckAttr.FWCIsShowReturnMsg);
	}
	public final void setFWCIsShowReturnMsg(int value){
		this.SetValByKey(NodeWorkCheckAttr.FWCIsShowReturnMsg, value);
	}
	/**
	 如果用户未审核是否按照默认意见填充?
	*/
	public final boolean getFWCIsFullInfo()  {
		return this.GetValBooleanByKey(NodeWorkCheckAttr.FWCIsFullInfo);
	}
	public final void setFWCIsFullInfo(boolean value){
		this.SetValByKey(NodeWorkCheckAttr.FWCIsFullInfo, value);
	}
	/**
	 默认审核信息
	*/
	public final String getFWCDefInfo()  {
		return this.GetValStringByKey(NodeWorkCheckAttr.FWCDefInfo);
	}
	public final void setFWCDefInfo(String value){
		this.SetValByKey(NodeWorkCheckAttr.FWCDefInfo, value);
	}
	/**
	 节点名称.
	*/
	public final String getName()  {
		return this.GetValStringByKey("Name");
	}
	/**
	 节点意见名称，如果为空则取节点名称.
	*/
	public final String getFWCNodeName() throws Exception {
		String str = this.GetValStringByKey(NodeWorkCheckAttr.FWCNodeName);
		if (DataType.IsNullOrEmpty(str))
		{
			return this.getName();
		}
		return str;
	}
	/**
	 操作名词(审核，审定，审阅，批示)
	*/
	public final String getFWCOpLabel()  {
		return this.GetValStringByKey(NodeWorkCheckAttr.FWCOpLabel);
	}
	public final void setFWCOpLabel(String value){
		this.SetValByKey(NodeWorkCheckAttr.FWCOpLabel, value);
	}
	/**
	 操作字段
	*/
	public final String getFWCFields()  {
		return this.GetValStringByKey(NodeWorkCheckAttr.FWCFields);
	}
	public final void setFWCFields(String value){
		this.SetValByKey(NodeWorkCheckAttr.FWCFields, value);
	}
	/**
	 自定义常用短语
	*/
	public final String getFWCNewDuanYu()  {
		return this.GetValStringByKey(NodeWorkCheckAttr.FWCNewDuanYu);
	}
	public final void setFWCNewDuanYu(String value){
		this.SetValByKey(NodeWorkCheckAttr.FWCNewDuanYu, value);
	}
	/**
	 是否显示数字签名？
	*/
	public final int getSigantureEnabel()  {
		return this.GetValIntByKey(NodeWorkCheckAttr.SigantureEnabel);
	}
	public final void setSigantureEnabel(int value){
		this.SetValByKey(NodeWorkCheckAttr.SigantureEnabel, value);
	}

	/**
	 协作模式下操作员显示顺序
	*/
	public final FWCOrderModel getFWCOrderModel() {
		return FWCOrderModel.forValue(this.GetValIntByKey(NodeWorkCheckAttr.FWCOrderModel, 0));
	}
	public final void setFWCOrderModel(FWCOrderModel value){
		this.SetValByKey(NodeWorkCheckAttr.FWCOrderModel, value.getValue());
	}
	/**
	 审核组件状态
	*/
	public final FrmWorkCheckSta getFWCSta() {
		return FrmWorkCheckSta.forValue(this.GetValIntByKey(NodeWorkCheckAttr.FWCSta, 0));
	}
	public final void setFWCSta(FrmWorkCheckSta value){
		this.SetValByKey(NodeWorkCheckAttr.FWCSta, value.getValue());
	}

	public final int getFWCVer()  {
		return this.GetValIntByKey(NodeWorkCheckAttr.FWCVer, 0);
	}
	public final void setFWCVer(int value){
		this.SetValByKey(NodeWorkCheckAttr.FWCVer, value);
	}
	public final String getFWCView()  {
		return this.GetValStringByKey(NodeWorkCheckAttr.FWCView);
	}
	public final void setFWCView(String value){
		this.SetValByKey(NodeWorkCheckAttr.FWCView, value);
	}
	public final String getCheckField()  {
		return this.GetValStringByKey(NodeWorkCheckAttr.CheckField);
	}
	public final void setCheckField(String value){
		this.SetValByKey(NodeWorkCheckAttr.CheckField, value);
	}

	public final int getFWCMsgShow()  {
		return this.GetValIntByKey(NodeWorkCheckAttr.FWCMsgShow);
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
	 审核组件
	*/
	public NodeWorkCheck()
	{
	}
	/**
	 审核组件

	 @param mapData
	*/
	public NodeWorkCheck(String mapData) throws Exception {
		if (mapData.contains("ND") == false)
		{
			this.setHisFrmWorkCheckSta(FrmWorkCheckSta.Disable);
			return;
		}

		String mapdata = mapData.replace("ND", "");
		if (DataType.IsNumStr(mapdata) == false)
		{
			this.setHisFrmWorkCheckSta(FrmWorkCheckSta.Disable);
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
	 审核组件

	 @param nodeID
	*/
	public NodeWorkCheck(int nodeID) throws Exception {
		this.setNodeID(nodeID);
		this.Retrieve();
	}
	/**
	 EnMap
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_Node", "审核组件");


			///#region 基本设置
		map.AddGroupAttr("基本设置", "");
		map.AddTBIntPK(NodeAttr.NodeID, 0, "节点ID", true, true);
		map.AddTBString(NodeAttr.Name, null, "节点名称", true, true, 0, 100, 10);
		map.AddDDLSysEnum(NodeWorkCheckAttr.FWCSta, FrmWorkCheckSta.Disable.getValue(), "审核组件状态", true, true, NodeWorkCheckAttr.FWCSta, "@0=禁用@1=启用@2=只读");

		map.AddTBString(NodeWorkCheckAttr.FWCLab, "审核信息", "显示标签", true, false, 0, 100, 10, true);


		map.AddDDLSysEnum(NodeWorkCheckAttr.FWCType, FWCType.Check.getValue(), "审核组件", true, true, NodeWorkCheckAttr.FWCType, "@0=审核组件@1=日志组件@2=周报组件@3=月报组件");

		//增加.
		map.AddTBInt("FWCShowModel", 2, "工作模式", false, false);
		map.AddTBInt("FWCTimeModel", 0, "时序模式", true, false);
		map.SetHelperAlert("FWCTimeModel", "0为倒序，1为正序。"); //使用alert的方式显示帮助信息.


		map.AddTBString(NodeWorkCheckAttr.FWCNodeName, null, "节点意见名称", true, false, 0, 100, 10);

		map.AddDDLSysEnum(NodeWorkCheckAttr.FWCAth, FWCAth.None.getValue(), "附件上传", true, true, NodeWorkCheckAttr.FWCAth, "@0=不启用@1=多附件@2=单附件(暂不支持)@3=图片附件(暂不支持)");
		map.SetHelperAlert(NodeWorkCheckAttr.FWCAth, "在审核期间，是否启用上传附件？启用什么样的附件？注意：附件的属性在节点表单里配置。"); //使用alert的方式显示帮助信息.

		map.AddTBString(NodeWorkCheckAttr.FWCOpLabel, "审核", "操作名词(审核/审阅/批示)", true, false, 0, 50, 10);
		map.AddTBString(NodeWorkCheckAttr.FWCDefInfo, "", "默认审核信息", true, false, 0, 50, 10);

		map.AddDDLSysEnum(NodeWorkCheckAttr.SigantureEnabel, 0, "签名方式", true, true, NodeWorkCheckAttr.SigantureEnabel, "@0=不签名@1=图片签名@2=写字板@3=电子签名@4=电子盖章@5=电子签名+盖章");
		map.SetHelperUrl(NodeWorkCheckAttr.SigantureEnabel, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=5415110&doc_id=31094");

		map.AddBoolean(NodeWorkCheckAttr.IsChangeFWCTime, false, "是否可以重新编辑审核时间？", true, true, true);
		map.AddBoolean(NodeWorkCheckAttr.FWCIsFullInfo, true, "如果用户未审核是否按照默认意见填充？", true, true, true);
		//map.AddBoolean("WhetherStamp ", false, "是否启用盖章？", true, true, false);

		map.AddTBString(NodeWorkCheckAttr.FWCFields, null, "审批格式字段", true, false, 0, 50, 10, true);
		//map.AddTBString(NodeWorkCheckAttr.FWCNewDuanYu, null, "自定义常用短语(使用@分隔)", true, false, 0, 100, 10, true);


		//增加如下字段是为了查询与排序的需要.
		map.AddTBString(NodeAttr.FK_Flow, null, "流程编号", false, false, 0, 5, 10);
		map.AddTBInt(NodeAttr.Step, 0, "步骤", false, false);


		map.AddDDLSysEnum(NodeWorkCheckAttr.FWCVer, 1, "审核意见保存规则", true, true, NodeWorkCheckAttr.FWCVer, "@0=1个节点1个人保留1个意见@1=保留节点历史意见(默认)");

		//签批字段解决关联字段或者极简模式查询不到结果的修改
		map.AddTBString(NodeAttr.NodeFrmID, null, "节点表单ID", false, true, 0, 50, 10);
		String sql = "SELECT KeyOfEn AS No,Name From Sys_MapAttr Where UIContralType=14 AND (FK_MapData='ND@NodeID' OR FK_MapData='@NodeFrmID' )";
		map.AddDDLSQL(NodeWorkCheckAttr.CheckField, null, "签批字段", sql, true);

	   // map.AddTBString(NodeWorkCheckAttr.CheckField, null, "签批字段", true, false, 0, 50, 10, false);

		map.AddTBString(NodeWorkCheckAttr.FWCView, null, "审核意见立场", true, false, 0, 200, 200,true);
		map.SetHelperAlert(NodeWorkCheckAttr.FWCView, "比如:同意,不同意,酌情处理, 多个立场用逗号分开,此立场可以作为方向条件.");

			///#endregion 基本设置


			///#region 外观设置.
		map.AddGroupAttr("外观设置", "");
		map.AddDDLSysEnum(NodeWorkCheckAttr.FWCShowModel, FrmWorkShowModel.TrackTime.getValue(), "显示方式", true, true, NodeWorkCheckAttr.FWCShowModel, "@0=表格方式@1=自由模式@2=轨迹时间轴模式"); //此属性暂时没有用.
		//协作模式下审核人显示顺序. add for yantai zongheng.
		map.AddDDLSysEnum(NodeWorkCheckAttr.FWCOrderModel, 0, "协作模式下操作员显示顺序", true, true, NodeWorkCheckAttr.FWCOrderModel, "@0=按审批时间先后排序@1=按照接受人员列表先后顺序(官职大小)");
		//for tianye , 多人审核的时候，不让其看到其他人的意见.
		map.AddDDLSysEnum(NodeWorkCheckAttr.FWCMsgShow, 0, "审核意见显示方式", true, true, NodeWorkCheckAttr.FWCMsgShow, "@0=都显示@1=仅显示自己的意见");
		map.AddTBFloat(NodeWorkCheckAttr.FWC_H, 300, "高度(0=100%)", true, false);

		map.AddDDLSysEnum(NodeWorkCheckAttr.FWCIsShowReturnMsg, 0, "退回信息显示规则", true, true, NodeWorkCheckAttr.FWCIsShowReturnMsg, "@0=不显示@1=退回到的节点显示@2=显示全部退回信息");

		map.AddBoolean(NodeWorkCheckAttr.FWCTrackEnable, true, "轨迹图是否显示？", true, true, false);
		map.AddBoolean(NodeWorkCheckAttr.FWCListEnable, true, "历史审核信息是否显示？(否,仅出现意见框)", true, true, true);
		map.AddBoolean(NodeWorkCheckAttr.FWCIsShowAllStep, false, "在轨迹表里是否显示所有的步骤？", true, true);

		map.AddBoolean(NodeWorkCheckAttr.FWCIsShowTruck, false, "是否显示未审核的轨迹？", true, true, true);
		//map.AddBoolean(NodeWorkCheckAttr.FWCIsShowReturnMsg, false, "是否显示退回信息？", true, true, true);

			///#endregion 外观

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		if (this.getFWCAth() == FWCAth.MinAth && this.getFWCSta() != FrmWorkCheckSta.Disable)
		{
			FrmAttachment workCheckAth = new FrmAttachment();
			boolean isHave = workCheckAth.RetrieveByAttr(FrmAttachmentAttr.MyPK, "ND" + this.getNodeID() + "_FrmWorkCheck");
			//不包含审核组件
			if (isHave == false)
			{
				workCheckAth = new FrmAttachment();
				/*如果没有查询到它,就有可能是没有创建.*/
				workCheckAth.setMyPK("ND" + this.getNodeID() + "_FrmWorkCheck");
				workCheckAth.setFrmID("ND" + String.valueOf(this.getNodeID()));
				workCheckAth.setNoOfObj("FrmWorkCheck");
				workCheckAth.setExts("*.*");

				//存储路径.
			   // workCheckAth.SaveTo = "/DataUser/UploadFile/";
				workCheckAth.setItIsNote( false); //不显示note字段.
				workCheckAth.setItIsVisable( false); // 让其在form 上不可见.

				//位置.

				workCheckAth.setH( (float)150);

				//多附件.
				workCheckAth.setUploadType( AttachmentUploadType.Multi);
				workCheckAth.setName("审核组件");
				workCheckAth.SetValByKey("AtPara", "@IsWoEnablePageset=1@IsWoEnablePrint=1@IsWoEnableViewModel=1@IsWoEnableReadonly=0@IsWoEnableSave=1@IsWoEnableWF=1@IsWoEnableProperty=1@IsWoEnableRevise=1@IsWoEnableIntoKeepMarkModel=1@FastKeyIsEnable=0@IsWoEnableViewKeepMark=1@FastKeyGenerRole=@IsWoEnableTemplete=1");
				workCheckAth.Insert();
			}
		}



		return super.beforeUpdateInsertAction();
	}

	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		Node fl = new Node();
		fl.setNodeID(this.getNodeID());
		fl.RetrieveFromDBSources();
		fl.Update();
		GroupField gf = new GroupField();
		if (this.getHisFrmWorkCheckSta() == FrmWorkCheckSta.Disable)
		{
			gf.Delete(GroupFieldAttr.FrmID, this.getNo(), GroupFieldAttr.CtrlType, GroupCtrlType.FWC);
		}
		else
		{
			if (gf.IsExit(GroupFieldAttr.FrmID, this.getNo(), GroupFieldAttr.CtrlType, GroupCtrlType.FWC) == false)
			{
				gf = new GroupField();
				gf.setFrmID("ND" + this.getNodeID());
				gf.setCtrlType(GroupCtrlType.FWC);
				gf.setLab("审核组件");
				gf.setIdx( 0);
				gf.Insert(); //插入.
			}
		}
		super.afterInsertUpdateAction();
	}
}
