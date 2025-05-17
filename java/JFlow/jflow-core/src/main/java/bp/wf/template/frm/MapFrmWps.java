package bp.wf.template.frm;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.CCFormAPI;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 Wps表单 属性
*/
public class MapFrmWps extends EntityNoName
{

		///#region 属性
	public final String getMyFileExt()  {
		return this.GetValStringByKey("MyFileExt");
	}
	public final String getMyFilePath()  {
		return this.GetValStringByKey("MyFilePath");
	}
	/** 
	 是否是节点表单?
	*/
	public final boolean getItIsNodeFrm() throws Exception {
		if (this.getNo().contains("ND") == false)
		{
			return false;
		}

		if (this.getNo().contains("Rpt") == true)
		{
			return false;
		}

		if (Objects.equals(this.getNo().substring(0, 2), "ND") && this.getNo().contains("Dtl") == false)
		{
			return true;
		}

		return false;
	}
	/** 
	 物理存储表
	*/
	public final String getPTable()  {
		return this.GetValStrByKey(MapDataAttr.PTable);
	}
	public final void setPTable(String value){
		this.SetValByKey(MapDataAttr.PTable, value);
	}
	/** 
	 节点ID.
	*/
	public final int getNodeID() {
		if (this.getNo().indexOf("ND") != 0)
		{
			return 0;
		}
		return Integer.parseInt(this.getNo().replace("ND", ""));
	}

	/** 
	 表格显示的列
	*/
	public final int getTableCol() {
		return 4;
	 
	}
	public final void setTableCol(int value){
		this.SetValByKey(MapDataAttr.TableCol, value);
	}

	public final String getFormTreeNo()  {
		return this.GetValStringByKey(MapDataAttr.FK_FormTree);
	}
	public final void setFormTreeNo(String value){
		this.SetValByKey(MapDataAttr.FK_FormTree, value);
	}

	public final FrmType getHisFrmType() {
		return FrmType.forValue(this.GetValIntByKey(MapDataAttr.FrmType));
	}
	public final void setHisFrmType(FrmType value){
		this.SetValByKey(MapDataAttr.FrmType, value.getValue());
	}


		///#endregion


		///#region 权限控制.
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		//uac.OpenForSysAdmin();
		uac.OpenForAppAdmin(); //2020.6.22zsy修改.
		uac.IsInsert = false;
		return uac;
	}

		///#endregion 权限控制.


		///#region 构造方法
	/** 
	 Wps表单属性
	*/
	public MapFrmWps()
	{
	}
	/** 
	 Wps表单属性
	 
	 @param no 表单ID
	*/
	public MapFrmWps(String no) throws Exception
	{
		super(no);
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

		Map map = new Map("Sys_MapData", "Wps表单属性");


			///#region 基本属性.
		map.AddTBStringPK(MapDataAttr.No, null, "表单编号", true, true, 1, 190, 20);
		map.SetHelperUrl(MapDataAttr.No, "xxxx");

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			map.AddTBString(MapDataAttr.PTable, null, "存储表", false, false, 0, 100, 20);
		}
		else
		{
			map.AddTBString(MapDataAttr.PTable, null, "存储表", true, false, 0, 100, 20);
			String msg = "提示:";
			msg += "\t\n1. 该表单把数据存储到那个表里.";
			msg += "\t\n2. 该表必须有一个int64未的OID列作为主键..";
			msg += "\t\n3. 如果指定了一个不存在的表,系统就会自动创建上.";
			map.SetHelperAlert(MapDataAttr.PTable, msg);
		}

		map.AddTBString(MapDataAttr.Name, null, "表单名称", true, false, 0, 500, 20, true);
		map.AddTBInt(MapDataAttr.TableCol, 0, "显示列数", false, false);

		map.AddTBInt(MapDataAttr.FrmW, 900, "表单宽度", true, false);

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
		}
		else
		{
			map.AddTBString(MapDataAttr.DBSrc, null, "数据源", false, false, 0, 500, 20);
			// map.AddDDLEntities(MapDataAttr.DBSrc, "local", "数据源", new BP.Sys.SFDBSrcs(), true);
			map.AddDDLEntities(MapDataAttr.FK_FormTree, "01", "表单类别", new SysFormTrees(), true);
		}

		//表单的运行类型.
		map.AddDDLSysEnum(MapDataAttr.FrmType, FrmType.FoolForm.getValue(), "表单类型", true, true, MapDataAttr.FrmType);

		//表单解析 0 普通 1 页签展示
		map.AddDDLSysEnum(MapDataAttr.FrmShowType, 0, "表单展示方式", true, true, "表单展示方式", "@0=普通方式@1=页签方式");
		map.AddBoolean("IsEnableJs", false, "是否启用自定义js函数？", true, true, true);

			///#endregion 基本属性.


			///#region 设计者信息.
		map.AddTBString(MapDataAttr.Designer, null, "设计者", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerContact, null, "联系方式", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerUnit, null, "单位", true, false, 0, 500, 20, true);
		map.AddTBString(MapDataAttr.GUID, null, "GUID", true, true, 0, 128, 20, false);
		map.AddTBString(MapDataAttr.Ver, null, "版本号", true, true, 0, 30, 20);
		map.AddTBString(MapDataAttr.Note, null, "备注", true, false, 0, 400, 100, true);
		//增加参数字段.
		map.AddTBAtParas(4000);

			///#endregion 设计者信息.

		map.AddMyFile("wps文件模板", null, bp.difference.SystemConfig.getPathOfDataUser() + "\\CyclostyleFile\\");



			///#region 基本功能.
		RefMethod rm = new RefMethod();

		rm = new RefMethod();
		rm.Title = "装载填充"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoPageLoadFull";
		// rm.Icon = "../../WF/Img/FullData.png";
		rm.Icon = "icon-reload";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		// rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "表单事件"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoEvent";
		rm.Icon = "../../WF/Img/Event.png";
		rm.Icon = "icon-energy";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "批量修改字段"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoBatchEditAttr";
		rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/field.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-calculator";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "手机端表单";
		rm.Icon = "../../WF/Admin/CCFormDesigner/Img/telephone.png";
		rm.ClassMethodName = this.toString() + ".MobileFrmDesigner";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-screen-smartphone";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "隐藏字段";
		rm.Icon = "../../WF/Admin/CCFormDesigner/Img/telephone.png";
		rm.Icon = "icon-list";
		rm.ClassMethodName = this.toString() + ".FrmHiddenField";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "表单body属性"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoBodyAttr";
		rm.Icon = "../../WF/Img/Script.png";
		rm.Icon = "icon-social-spotify";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "导出模版"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoExp";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "../../WF/Img/Export.png";
		rm.Visable = true;
		rm.RefAttrLinkLabel = "导出到xml";
		rm.Target = "_blank";
		rm.Icon = "icon-social-spotify";
		map.AddRefMethod(rm);

		//带有参数的方法.
		rm = new RefMethod();
		rm.Title = "重命名字段";
		rm.getHisAttrs().AddTBString("FieldOld", null, "旧字段英文名", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("FieldNew", null, "新字段英文名", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("FieldNewName", null, "新字段中文名", true, false, 0, 100, 100);
		rm.ClassMethodName = this.toString() + ".DoChangeFieldName";
		rm.Icon = "../../WF/Img/ReName.png";
		rm.Icon = "icon-refresh";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "表单检查"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoCheckFixFrmForUpdateVer";
		rm.Visable = true;
		rm.RefAttrLinkLabel = "表单检查";
		rm.Icon = "../../WF/Img/Check.png";
		rm.Target = "_blank";
		rm.Icon = "icon-eye";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "Tab顺序键"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoTabIdx";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-list";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "模板打印";
		rm.ClassMethodName = this.toString() + ".DoBill";
		rm.Icon = "../../WF/Img/FileType/doc.gif";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-printer";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "模板打印2019";
		rm.ClassMethodName = this.toString() + ".DoBill2019";
		rm.Icon = "../../WF/Img/FileType/doc.gif";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-printer";
		//  map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "参考面板";
		rm.ClassMethodName = this.toString() + ".DoRefPanel";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-grid";
		map.AddRefMethod(rm);

			///#endregion 方法 - 基本功能.


			///#region 高级功能.
		rm = new RefMethod();
		rm.Title = "改变表单类型";
		rm.GroupName = "高级功能";
		rm.ClassMethodName = this.toString() + ".DoChangeFrmType()";
		rm.getHisAttrs().AddDDLSysEnum("FrmType", 0, "修改表单类型", true, true);
		rm.Icon = "icon-refresh";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "Wps表单设计";
		rm.GroupName = "高级功能";
		rm.ClassMethodName = this.toString() + ".DoDesignerFool";
		//rm.Icon = "../../WF/Img/FileType/xlsx.gif";
		rm.Icon = "icon-note";
		rm.Visable = true;
		rm.Target = "_blank";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		map.AddRefMethod(rm);


		//平铺模式.
		if (bp.wf.Glo.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			map.getAttrsOfOneVSM().AddGroupPanelModel(new FrmOrgs(), new bp.wf.port.admin2group.Orgs(), FrmOrgAttr.FrmID, FrmOrgAttr.OrgNo, "适用组织", null, "Name", "No");
		}

			///#endregion


			///#region 实验中的功能
		rm = new RefMethod();
		rm.Title = "一键设置表单元素只读";
		rm.Warning = "您确定要设置吗？所有的元素，包括字段、从表、附件以及其它组件都将会被设置为只读的.";
		rm.GroupName = "实验中的功能";
		//rm.Icon = "../../WF/Img/RegularExpression.png";
		rm.ClassMethodName = this.toString() + ".DoOneKeySetReadonly";
		rm.refMethodType = RefMethodType.Func;
		rm.Icon = "icon-settings";
		map.AddRefMethod(rm);


			///#endregion 实验中的功能

		this.set_enMap(map);
		return this.get_enMap();
	}
	/** 
	 删除后清缓存
	*/
	@Override
	protected void afterDelete() throws Exception
	{
		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getNo());
		super.afterDelete();
	}

		///#endregion


		///#region 高级设置.
	/** 
	 一键设置为只读.
	 
	 @return 
	*/
	public final String DoOneKeySetReadonly() throws Exception {
		CCFormAPI.OneKeySetFrmEleReadonly(this.getNo());
		return "设置成功.";
	}
	/** 
	 改变表单类型 @李国文 ，需要搬到jflow.并测试.
	 
	 @param val 要改变的类型
	 @return 
	*/
	public final String DoChangeFrmType(int val) throws Exception {
		MapData md = new MapData(this.getNo());
		String str = "原来的是:" + md.getHisFrmTypeText() + "类型，";
		md.setHisFrmTypeInt(val);
		str += "现在修改为：" + md.getHisFrmTypeText() + "类型";
		md.Update();

		return str;
	}

		///#endregion 高级设置.

	@Override
	protected boolean beforeUpdate() throws Exception
	{
		if (this.getNodeID() != 0)
		{
			this.setFormTreeNo("");
		}

		return super.beforeUpdate();
	}
	@Override
	protected void afterUpdate() throws Exception
	{
		//修改关联明细表,, 如果是从表.
		MapDtl dtl = new MapDtl();
		dtl.setNo(this.getNo());
		if (dtl.RetrieveFromDBSources() == 1)
		{
			dtl.setName(this.getName());
			dtl.setPTable(this.getPTable());
			dtl.DirectUpdate();

			MapData map = new MapData(this.getNo());
			//避免显示在表单库中
			// map.FK_FrmSort = "";
			map.setFormTreeNo("");
			map.DirectUpdate();
		}

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getNo());

		super.afterUpdate();
	}

		///#region 节点表单方法.
	public final String DoTabIdx() {
		return "../../Admin/FoolFormDesigner/TabIdx.htm?FK_MapData=" + this.getNo();
	}
	/** 
	 单据打印
	 
	 @return 
	*/
	public final String DoBill() {
		return "../../Admin/FoolFormDesigner/PrintTemplate/Default.htm?FK_MapData=" + this.getNo() + "&FrmID=" + this.getNo() + "&NodeID=" + this.getNodeID() + "&FK_Node=" + this.getNodeID();

	   // return "../../Admin/AttrNode/Bill.htm?FK_MapData=" + this.getNo() + "&NodeID=" + this.getNodeID() + "&FK_Node=" + this.getNodeID();
	}
	/** 
	 隐藏字段.
	 
	 @return 
	*/
	public final String FrmHiddenField() {
		return "../../Admin/CCFormDesigner/DialogCtr/FrmHiddenField.htm?FK_MapData=" + this.getNo() + "&NodeID=" + this.getNodeID() + "&FK_Node=" + this.getNodeID();
	}
	/** 
	 单据打印
	 
	 @return 
	*/
	public final String DoBill2019() {
		return "../../Admin/AttrNode/Bill2019.htm?FK_MapData=" + this.getNo() + "&FrmID=" + this.getNo() + "&NodeID=" + this.getNodeID() + "&FK_Node=" + this.getNodeID();
	}

	/** 
	 Wps表单设计器
	 
	 @return 
	*/
	public final String DoDesignerFool() {
		return "../../Admin/FoolFormDesigner/Designer.htm?FK_MapData=" + this.getNo() + "&MyPK=" + this.getNo() + "&IsFirst=1&IsEditMapData=True";
	}

	/** 
	 节点表单组件
	 
	 @return 
	*/
	public final String DoNodeFrmCompent() throws Exception {
		if (this.getNo().contains("ND") == true)
		{
			return "../../Comm/EnOnly.htm?EnName=BP.WF.Template.FrmNodeComponent&PK=" + this.getNo().replace("ND", "") + "&t=" + DataType.getCurrentDateTime();
		}
		else
		{
			return "../../Admin/FoolFormDesigner/Do.htm&DoType=FWCShowError";
		}
	}

		///#endregion


		///#region 通用方法.
	/** 
	 替换名称
	 
	 @param fieldOldName 旧名称
	 @param newField 新字段
	 @param newFieldName 新字段名称(可以为空)
	 @return 
	*/
	public final String DoChangeFieldName(String fieldOld, String newField, String newFieldName) throws Exception {
		MapFrmFool en = new MapFrmFool(this.getNo());
		return en.DoChangeFieldName(fieldOld, newField, newFieldName);
	}
	/** 
	 批量设置正则表达式规则.
	 
	 @return 
	*/
	public final String DoRegularExpressionBatch() {
		return "../../Admin/FoolFormDesigner/MapExt/RegularExpressionBatch.htm?FK_Flow=&FK_MapData=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
	}
	/** 
	 批量修改字段
	 
	 @return 
	*/
	public final String DoBatchEditAttr() {
		return "../../Admin/FoolFormDesigner/FieldTypeListBatch.htm?FK_MapData=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
	}
	/** 
	 排序字段顺序
	 
	 @return 
	*/
	public final String MobileFrmDesigner() {
		return "../../Admin/MobileFrmDesigner/Default.htm?FK_Flow=&FK_MapData=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
	}
	/** 
	 设计表单
	 
	 @return 
	*/
	public final String DoDFrom() throws Exception {
		String url = "../../Admin/FoolFormDesigner/CCForm/Frm.htm?FK_MapData=" + this.getNo() + "&UserNo=" + bp.web.WebUser.getNo() + "&Token=" + bp.web.WebUser.getToken() + "&AppCenterDBType=" + DBAccess.getAppCenterDBType() + "&CustomerNo=" + bp.difference.SystemConfig.getCustomerNo();
		return url;
	}
	/** 
	 设计Wps表单
	 
	 @return 
	*/
	public final String DoDFromCol4() throws Exception {
		String url = "../../Admin/FoolFormDesigner/Designer.htm?FK_MapData=" + this.getNo() + "&UserNo=" + bp.web.WebUser.getNo() + "&Token=" + bp.web.WebUser.getToken() + "&IsFirst=1&AppCenterDBType=" + DBAccess.getAppCenterDBType() + "&CustomerNo=" + bp.difference.SystemConfig.getCustomerNo();
		return url;
	}
	/** 
	 查询
	 
	 @return 
	*/
	public final String DoSearch() {
		return "../../Comm/Search.htm?s=34&FK_MapData=" + this.getNo() + "&EnsName=" + this.getNo();
	}
	/** 
	 参考面板
	 
	 @return 
	*/
	public final String DoRefPanel() {
		return "../../Comm/RefFunc/EnOnly.htm?EnName=BP.WF.Template.Frm.MapFrmReferencePanel&PKVal=" + this.getNo();
	}
	/** 
	 调用分析API
	 
	 @return 
	*/
	public final String DoGroup() {
		return "../../Comm/Group.htm?s=34&FK_MapData=" + this.getNo() + "&EnsName=" + this.getNo();
	}
	/** 
	 数据源管理
	 
	 @return 
	*/
	public final String DoDBSrc() {
		return "../../Comm/Search.htm?s=34&FK_MapData=" + this.getNo() + "&EnsName=BP.Sys.SFDBSrcs";
	}

	public final String DoPageLoadFull() {
		return "../../Admin/FoolFormDesigner/MapExt/PageLoadFull.htm?s=34&FK_MapData=" + this.getNo() + "&ExtType=PageLoadFull&RefNo=";
	}
	public final String DoInitScript() {
		return "../../Admin/FoolFormDesigner/MapExt/InitScript.htm?s=34&FK_MapData=" + this.getNo() + "&ExtType=PageLoadFull&RefNo=";
	}
	/** 
	 Wps表单属性.
	 
	 @return 
	*/
	public final String DoBodyAttr() {
		return "../../Admin/FoolFormDesigner/MapExt/BodyAttr.htm?s=34&FK_MapData=" + this.getNo() + "&ExtType=BodyAttr&RefNo=";
	}
	/** 
	 表单事件
	 
	 @return 
	*/
	public final String DoEvent() {
		return "../../Admin/CCFormDesigner/Action.htm?FK_MapData=" + this.getNo() + "&T=sd&FK_Node=0";
	}

	/** 
	 导出表单
	 
	 @return 
	*/
	public final String DoExp() {
		return "../../Admin/FoolFormDesigner/ImpExp/Exp.htm?FK_MapData=" + this.getNo();
	}

		///#endregion 方法.
}
