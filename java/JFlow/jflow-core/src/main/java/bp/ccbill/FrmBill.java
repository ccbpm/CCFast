package bp.ccbill;

import bp.ccbill.template.Collection;
import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.wf.*;
import bp.wf.template.*;
import bp.sys.*;
import bp.ccbill.template.*;
import bp.*;
import java.util.*;

/**
 单据属性
*/
public class FrmBill extends EntityNoName
{

		///#region 权限控制.
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForAppAdmin();
		uac.IsDelete = false;
		uac.IsInsert = false;
		return uac;
	}

		///#endregion 权限控制.


		///#region 属性
	/**
	 物理表
	*/
	public final String getPTable() throws Exception {
		String s = this.GetValStrByKey(MapDataAttr.PTable);
		if (DataType.IsNullOrEmpty(s) == true)
		{
			return this.getNo();
		}
		return s;
	}
	public final void setPTable(String value)  {
		this.SetValByKey(MapDataAttr.PTable, value);
	}
	/**
	 实体类型：@0=单据@1=编号名称实体@2=树结构实体
	*/
	public final EntityType getEntityType() {
		return EntityType.forValue(this.GetValIntByKey(FrmBillAttr.EntityType));
	}
	public final void setEntityType(EntityType value)  {
		this.SetValByKey(FrmBillAttr.EntityType, value.getValue());
	}
	/**
	 表单类型 (0=傻瓜，2=自由 ...)
	*/
	public final FrmType getFrmType() {
		return FrmType.forValue(this.GetValIntByKey(MapDataAttr.FrmType));
	}
	public final void setFrmType(FrmType value)  {
		this.SetValByKey(MapDataAttr.FrmType, value.getValue());
	}
	/**
	 表单树
	*/
	public final String getFormTreeNo()  {
		return this.GetValStrByKey(MapDataAttr.FK_FormTree);
	}
	public final void setFormTreeNo(String value)  {
		this.SetValByKey(MapDataAttr.FK_FormTree, value);
	}
	/**
	 单据审核模式
	 */
	public final String getBillCheckModel()  {
		return this.GetValStrByKey("BillCheckModel");
	}
	public final void setBillCheckModel(String value)  {
		this.SetValByKey("BillCheckModel", value);
	}
	/**
	 单据审核模式
	 */
	public final String getBillCheckTag()  {
		return this.GetValStrByKey("BillCheckTag");
	}
	public final void setBillCheckTag(String value)  {
		this.SetValByKey("BillCheckTag", value);
	}
	/**
	 单据格式
	*/
	public final String getBillNoFormat() throws Exception {
		String str = this.GetValStrByKey(FrmBillAttr.BillNoFormat);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = "{LSH4}";
		}
		return str;
	}
	public final void setBillNoFormat(String value)  {
		this.SetValByKey(FrmBillAttr.BillNoFormat, value);
	}
	/**
	 单据编号生成规则
	*/
	public final String getTitleRole() throws Exception {
		String str = this.GetValStrByKey(FrmBillAttr.TitleRole);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = "@WebUser.DeptName @WebUser.Name @RDT";
		}
		return str;
	}
	public final void setTitleRole(String value)  {
		this.SetValByKey(FrmBillAttr.BillNoFormat, value);
	}

	public final String getSortColumns()  {
		return this.GetValStrByKey(FrmBillAttr.SortColumns);
	}
	public final void setSortColumns(String value)  {
		this.SetValByKey(FrmBillAttr.SortColumns, value);
	}

	public final String getFieldSet()  {
		return this.GetValStrByKey(FrmBillAttr.FieldSet);
	}
	public final void setFieldSet(String value)  {
		this.SetValByKey(FrmBillAttr.FieldSet, value);
	}

	public final String getRefDict()  {
		return this.GetValStrByKey(FrmBillAttr.RefDict);
	}
	public final void setRefDict(String value)  {
		this.SetValByKey(FrmBillAttr.RefDict, value);
	}

		///#endregion


		///#region 构造方法
	/**
	 单据属性
	*/
	public FrmBill()
	{
	}
	/**
	 单据属性

	 @param no 映射编号
	*/
	public FrmBill(String no) throws Exception
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

		Map map = new Map("Sys_MapData", "单据属性");



			///#region 基本属性.
		map.AddGroupAttr("基本属性", "");
		map.AddTBStringPK(MapDataAttr.No, null, "表单编号", true, true, 1, 190, 20);
		map.AddDDLSysEnum(MapDataAttr.FrmType, 0, "表单类型", true, true, "BillFrmType", "@0=经典表单@1=自由表单@8=开发者表单");
		//map.AddDDLSysEnum(MapDataAttr.FrmModel, 0, "单据模板", true, true, "BillFrmModel", "@0=系统预置@1=用户新增");
		map.AddTBString(MapDataAttr.PTable, null, "存储表", true, false, 0, 500, 20, true);
		map.AddTBString(MapDataAttr.Name, null, "表单名称", true, false, 0, 500, 20, true);

		if (CCBPMRunModel.SAAS == bp.difference.SystemConfig.getCCBPMRunModel())
		{
			String sql = "SELECT No,Name FROM WF_FlowSort WHERE OrgNo='" + bp.web.WebUser.getOrgNo() + "' AND No!='" + bp.web.WebUser.getOrgNo() + "'";
			map.AddDDLSQL(MapDataAttr.FK_FormTree, null, "表单类别", sql, true);
			//map.AddDDLEntities(MapDataAttr.FK_FormTree, "01", "表单类别", new SysFormTrees(), true);
		}
		else
		{
			map.AddDDLEntities(MapDataAttr.FK_FormTree, "01", "表单类别", new SysFormTrees(), true);
		}

		map.AddDDLSysEnum(MapDataAttr.TableCol, 0, "表单显示列数", true, true, "经典表单显示方式", "@0=4列@1=6列@2=上下模式3列");

		map.AddDDLSysEnum(FrmAttr.RowOpenModel, 0, "行记录打开模式", true, true, "RowOpenMode", "@0=新窗口打开@1=在本窗口打开@2=弹出窗口打开,关闭后不刷新列表@3=弹出窗口打开,关闭后刷新列表");
		String cfg = "@0=MyDictFrameWork.htm 实体与实体相关功能编辑器";
		cfg += "@1=MyDict.htm 实体编辑器";
		cfg += "@2=MyBill.htm 单据编辑器";
		cfg += "@9=自定义URL";
		map.AddDDLSysEnum("SearchDictOpenType", 0, "双击行打开内容", true, true, "SearchDictOpenType", cfg);
		map.AddBoolean(EnCfgAttr.IsSelectMore, true, "是否下拉查询条件多选?", true, true);
		map.AddTBString(EnCfgAttr.UrlExt, null, "要打开的Url", true, false, 0, 500, 60, true);

			///#endregion 基本属性.


			///#region 单据属性.
		map.AddGroupAttr("单据属性", "");

		//map.AddDDLSysEnum(FrmBillAttr.FrmBillWorkModel, 0, "工作模式", true, false, FrmBillAttr.FrmBillWorkModel,
		//    "@0=独立表单@1=单据工作模式");

		map.AddDDLSysEnum(FrmBillAttr.EntityType, 0, "业务类型", true, false, FrmBillAttr.EntityType, "@0=独立表单@1=单据@2=编号名称实体@3=树结构实体");
		map.SetHelperAlert(FrmBillAttr.EntityType, "该实体的类型,@0=单据@1=编号名称实体@2=树结构实体.");

		//map.AddDDLSysEnum(MapDataAttr.FrmType, 0, "表单类型", true, true, "", "@0=独立表单@1=单据工作模式@2=流程工作模式");

		map.AddTBString(FrmBillAttr.BillNoFormat, null, "单号规则", true, false, 0, 100, 20, true);
		map.AddTBString(FrmBillAttr.TitleRole, null, "标题生成规则", true, false, 0, 100, 20, true);
		map.AddTBString(FrmBillAttr.SortColumns, null, "排序字段", true, false, 0, 100, 20, true);
		map.AddTBString(FrmBillAttr.ColorSet, null, "颜色设置", true, false, 0, 100, 20, true);
		String msg = "对字段的颜色处理";
		msg += "\t\n @Age:From=0,To=18,Color=green;From=19,To=30,Color=red";
		map.SetHelperAlert(FrmBillAttr.ColorSet, msg);

		map.AddTBString(FrmBillAttr.RowColorSet, null, "表格行颜色设置", true, false, 0, 100, 20, true);
		map.SetHelperAlert(FrmBillAttr.RowColorSet, "按照指定字段存储的颜色设置表格行的背景色");

		map.AddTBString(FrmBillAttr.FieldSet, null, "字段求和求平均设置", true, false, 0, 100, 20, true);
		map.AddTBString(FrmBillAttr.RefDict, null, "单据关联的实体", false, true, 0, 190, 20, true);
		map.AddTBString("BillCheckTag", null, "BillCheckTag", false, false, 0, 30, 20);

		map.AddTBString("BillCheckModel", null, "单据审核模式", false, false, 0, 30, 20);


			///#endregion 单据属性.


			///#region 按钮权限.
		//map.AddTBString(FrmBillAttr.BtnNewLable, "新建", "新建", true, false, 0, 50, 20);
		//map.AddDDLSysEnum(FrmDictAttr.BtnNewModel, 0, "新建模式", true, true, FrmDictAttr.BtnNewModel,
		//  "@0=表格模式@1=卡片模式@2=不可用", true);


		//map.AddTBString(FrmBillAttr.BtnSaveLable, "保存", "保存", true, false, 0, 50, 20);
		////map.AddBoolean(FrmBillAttr.BtnSaveEnable, true, "是否可用？", true, true);

		//map.AddTBString(FrmBillAttr.BtnSubmitLable, "提交", "提交", true, false, 0, 50, 20);

		//map.AddTBString(FrmBillAttr.BtnDelLable, "删除", "删除", true, false, 0, 50, 20);
		////map.AddBoolean(FrmBillAttr.BtnDelEnable, true, "是否可用？", true, true);

		//map.AddTBString(FrmBillAttr.BtnSearchLabel, "列表", "列表", true, false, 0, 50, 20);
		////map.AddBoolean(FrmBillAttr.BtnSearchEnable, true, "是否可用？", true, true);

		//map.AddTBString(FrmBillAttr.BtnGroupLabel, "分析", "分析", true, false, 0, 50, 20);
		//map.AddBoolean(FrmBillAttr.BtnGroupEnable, false, "是否可用？", true, true);

		//map.AddTBString(FrmBillAttr.BtnPrintHtml, "打印Html", "打印Html", true, false, 0, 50, 20);
		//map.AddBoolean(FrmBillAttr.BtnPrintHtmlEnable, false, "是否可用？", true, true);

		//map.AddTBString(FrmBillAttr.BtnPrintPDF, "打印PDF", "打印PDF", true, false, 0, 50, 20);
		//map.AddBoolean(FrmBillAttr.BtnPrintPDFEnable, false, "是否可用？", true, true);

		//map.AddTBString(FrmBillAttr.BtnPrintRTF, "打印RTF", "打印RTF", true, false, 0, 50, 20);
		//map.AddBoolean(FrmBillAttr.BtnPrintRTFEnable, false, "是否可用？", true, true);

		//map.AddTBString(FrmBillAttr.BtnPrintCCWord, "打印CCWord", "打印CCWord", true, false, 0, 50, 20);
		//map.AddBoolean(FrmBillAttr.BtnPrintCCWordEnable, false, "是否可用？", true, true);

		//map.AddTBString(FrmBillAttr.BtnExpZip, "导出zip文件", "导出zip文件", true, false, 0, 50, 20);
		//map.AddBoolean(FrmBillAttr.BtnExpZipEnable, false, "是否可用？", true, true);


		map.AddTBString(FrmBillAttr.BtnRefBill, "关联单据", "关联单据", true, false, 0, 50, 20);

		map.AddDDLSysEnum(FrmAttr.RefBillRole, 0, "关联单据工作模式", true, true, "RefBillRole", "@0=不启用@1=非必须选择关联单据@2=必须选择关联单据");

		map.AddTBString(FrmBillAttr.RefBill, null, "关联单据ID", true, false, 0, 100, 20, true);
		map.SetHelperAlert(FrmBillAttr.RefBill, "请输入单据编号,多个单据编号用逗号分开.\t\n比如:Bill_Sale,Bill_QingJia");

			///#endregion 按钮权限.


			///#region 查询按钮权限.
		//map.AddTBString(FrmBillAttr.BtnImpExcel, "导入", "导入Excel文件", true, false, 0, 50, 20);
		//map.AddBoolean(FrmBillAttr.BtnImpExcelEnable, true, "是否可用？", true, true);
		//map.AddTBString(FrmBillAttr.BtnExpExcel, "导出", "导出Excel文件", true, false, 0, 50, 20);
		//map.AddBoolean(FrmBillAttr.BtnExpExcelEnable, true, "是否可用？", true, true);
		//map.AddTBString(FrmBillAttr.BtnGroupLabel, "分析", "分析", true, false, 0, 50, 20);
		//map.AddBoolean(FrmBillAttr.BtnGroupEnable, true, "是否可用？", true, true);

			///#endregion 查询按钮权限.


			///#region 设计者信息.
		map.AddGroupAttr("设计者信息", "");

		map.AddTBString(MapDataAttr.Designer, null, "设计者", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerContact, null, "联系方式", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerUnit, null, "单位", true, false, 0, 500, 20, true);
		map.AddTBString(MapDataAttr.GUID, null, "GUID", true, true, 0, 128, 20, false);
		map.AddTBString(MapDataAttr.Ver, null, "版本号", true, true, 0, 30, 20);
		map.AddTBStringDoc(MapDataAttr.Note, null, "备注", true, false, true, 10);
		map.AddTBInt(MapDataAttr.Idx, 100, "顺序号", false, false);
		map.AddTBString("HidenField", null, "隐藏查询条件", false, false, 0, 30, 20);
			///#endregion 设计者信息.


			///#region 扩展参数.
		map.AddTBString(FrmAttr.Tag0, null, "Tag0", false, false, 0, 500, 20);
		map.AddTBString(FrmAttr.Tag1, null, "Tag1", false, false, 0, 4000, 20);
		map.AddTBString(FrmAttr.Tag2, null, "Tag2", false, false, 0, 500, 20);

			///#endregion 扩展参数.

		map.AddTBAtParas(800); //参数属性.


			///#region 基本功能.

		map.AddGroupMethod("基本设置");

		RefMethod rm = new RefMethod();

		rm = new RefMethod();
		rm.Title = "按钮权限"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".ToolbarSetting";
		rm.Icon = "../../WF/Img/Event.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "设计表单"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoDesigner";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
		//rm.GroupName = "开发接口";
		//map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "单据url的API"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoAPI";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		//rm.GroupName = "开发接口";
	   // map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "打开单据数据"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoOpenBill";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
		//rm.GroupName = "开发接口";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "绑定到菜单目录"; // "设计表单";
		try{
			rm.getHisAttrs().AddDDLSQL("MENUNo", null, "选择菜单目录", "SELECT No,Name FROM GPM_Menu WHERE MenuType=3", true);
			rm.getHisAttrs().AddTBString("Name", "@Name", "菜单名称", true, false, 0, 100, 100);
		}catch(Exception e){

		}

		rm.ClassMethodName = this.toString() + ".DoBindMenu";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.Func;
		rm.Target = "_blank";
		//rm.GroupName = "开发接口";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "装载填充"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoPageLoadFull";
		rm.Icon = "../../WF/Img/FullData.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "表单事件"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoEvent";
		rm.Icon = "../../WF/Img/Event.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "执行方法"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoMethod";
		//rm.Icon = "../../WF/Img/Event.png";
		//rm.Visable = true;
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		//map.AddRefMethod(rm);

			///#endregion 基本功能.


			///#region 权限规则.
		map.AddGroupMethod("权限规则");

		rm = new RefMethod();
		rm.Title = "创建规则"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoCreateRole";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkModel;
		rm.RefAttrKey = FrmBillAttr.BtnNewLable;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "保存规则"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoSaveRole";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkModel;
		rm.RefAttrKey = FrmBillAttr.BtnSaveLable;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "提交规则"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoSubmitRole";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkModel;
		rm.RefAttrKey = FrmBillAttr.BtnSubmitLable;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "删除规则"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoDeleteRole";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkModel;
		rm.RefAttrKey = FrmBillAttr.BtnDelLable;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "查询权限"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoSearchRole";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkModel;
		rm.RefAttrKey = FrmBillAttr.BtnSearchLabel;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "数据查询权限规则";
		rm.ClassMethodName = this.toString() + ".DoSearchDataRole()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

			///#endregion


			///#region 报表定义.
		map.AddGroupMethod("报表定义");
		rm = new RefMethod();
		rm.Title = "设置显示的列"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoRpt_ColsChose";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "设置多表头"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoRptMTitle";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "列的顺序"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoRpt_ColsIdxAndLabel";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		//   map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "查询条件"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoRpt_SearchCond";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.GroupName = "报表定义";
		//rm.Title = "页面展示设置"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoRpt_Setting";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		//map.AddRefMethod(rm);


			///#endregion 报表定义.

		this.set_enMap(map);
		return this.get_enMap();
	}
	public final void InsertToolbarBtns() throws Exception {
		//删除数据防止错误.
		DBAccess.RunSQL("DELETE FROM Frm_ToolbarBtn WHERE FrmID='" + this.getNo() + "'");
		//表单的工具栏权限
		ToolbarBtn btn = new ToolbarBtn();
		btn.setFrmID(this.getNo());
		btn.setBtnID("New");
		btn.setBtnLab("新建");
		btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
		btn.SetValByKey("Idx", 0);
		btn.Insert();


		btn = new ToolbarBtn();
		btn.setFrmID(this.getNo());
		btn.setBtnID("Save");
		btn.setBtnLab("保存");
		btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
		btn.SetValByKey("Idx", 1);
		btn.Insert();


		//单据增加提交的功能
		btn = new ToolbarBtn();
		btn.setFrmID(this.getNo());
		btn.setBtnID("Submit");
		btn.setBtnLab("提交");
		btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
		btn.SetValByKey("Idx", 1);
		btn.Insert();


		btn = new ToolbarBtn();
		btn.setFrmID(this.getNo());
		btn.setBtnID("Delete");
		btn.setBtnLab("删除");
		btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
		btn.SetValByKey("Idx", 2);
		btn.Insert();

		btn = new ToolbarBtn();
		btn.setFrmID(this.getNo());
		btn.setBtnID("PrintHtml");
		btn.setBtnLab("打印Html");
		btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
		btn.setItIsEnable(false);
		btn.SetValByKey("Idx", 3);
		btn.Insert();

		btn = new ToolbarBtn();
		btn.setFrmID(this.getNo());
		btn.setBtnID("PrintPDF");
		btn.setBtnLab("打印PDF");
		btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
		btn.setItIsEnable(false);
		btn.SetValByKey("Idx", 4);
		btn.Insert();

		btn = new ToolbarBtn();
		btn.setFrmID(this.getNo());
		btn.setBtnID("PrintRTF");
		btn.setBtnLab("打印RTF");
		btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
		btn.setItIsEnable(false);
		btn.SetValByKey("Idx", 5);
		btn.Insert();

		btn = new ToolbarBtn();
		btn.setFrmID(this.getNo());
		btn.setBtnID("PrintCCWord");
		btn.setBtnLab("打印CCWord");
		btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
		btn.setItIsEnable(false);
		btn.SetValByKey("Idx", 6);
		btn.Insert();

		btn = new ToolbarBtn();
		btn.setFrmID(this.getNo());
		btn.setBtnID("ExpZip");
		btn.setBtnLab("导出Zip包");
		btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
		btn.setItIsEnable(false);
		btn.SetValByKey("Idx", 7);
		btn.Insert();

		//删除已经存在的.
		DBAccess.RunSQL("DELETE FROM Frm_Collection WHERE FrmID='" + this.getNo() + "'");
		//列表权限
		//查询
		Collection collection = new Collection();
		collection.setFrmID(this.getNo());
		collection.setMethodID("Search");
		collection.setName("查询");
		collection.setMethodModel("Search");
		collection.setMark("Search");
		collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
		collection.SetValByKey("Idx", 0);
		collection.Insert();


		//新建
		collection = new Collection();
		collection.setFrmID(this.getNo());
		collection.setMethodID("New");
		collection.setName("新建");
		collection.setMethodModel("New");
		collection.setMark("New");
		collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
		collection.SetValByKey("Idx", 1);
		collection.Insert();

		//删除
		collection = new Collection();
		collection.setFrmID(this.getNo());
		collection.setMethodID("Delete");
		collection.setName("删除");
		collection.setMethodModel("Delete");
		collection.setMark("Delete");
		collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
		collection.SetValByKey("Idx", 2);
		collection.SetPara("EnName","TS.CCBill.CollectionDelete");
		collection.Insert();

		collection = new Collection();
		collection.setFrmID(this.getNo());
		collection.setMethodID("Group");
		collection.setName("分析");
		collection.setMethodModel("Group");
		collection.setMark("Group");
		collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
		collection.SetValByKey("Idx", 3);
		collection.SetValByKey("IsEnable", false);
		collection.Insert();

		//导出
		collection = new Collection();
		collection.setFrmID(this.getNo());
		collection.setMethodID("ExpExcel");
		collection.setName("导出Excel");
		collection.setMethodModel("ExpExcel");
		collection.setMark("ExpExcel");
		collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
		collection.SetValByKey("Idx", 4);
		collection.Insert();

		//导入
		collection = new Collection();
		collection.setFrmID(this.getNo());
		collection.setMethodID("ImpExcel");
		collection.setName("导入Excel");
		collection.setMethodModel("ImpExcel");
		collection.setMark("ImpExcel");
		collection.setNo(collection.getFrmID() + "_" + collection.getMethodID());
		collection.SetValByKey("Idx", 5);
		collection.Insert();
	}

	@Override
	protected void afterInsert() throws Exception
	{
		InsertToolbarBtns();
		CheckEnityTypeAttrsFor_Bill();

		super.afterInsertUpdateAction();
	}

		///#endregion


		///#region 权限控制.
	public final String DoSaveRole() {
		return "../../CCBill/Admin/BillRole.htm?s=34&FrmID=" + this.getNo() + "&CtrlObj=BtnSave";
	}
	/**
	 提交权限规则

	 @return
	*/
	public final String DoSubmitRole() {
		return "../../CCBill/Admin/BillRole.htm?s=34&FrmID=" + this.getNo() + "&CtrlObj=BtnSubmit";
	}

	/**
	 创建权限

	 @return
	*/
	public final String DoCreateRole() {
		return "../../CCBill/Admin/BillRole.htm?s=34&FrmID=" + this.getNo() + "&CtrlObj=BtnNew";
	}
	/**
	 查询权限

	 @return
	*/
	public final String DoSearchRole() {
		return "../../CCBill/Admin/BillRole.htm?s=34&FrmID=" + this.getNo() + "&CtrlObj=BtnSearch";
	}
	/**
	 删除规则.

	 @return
	*/
	public final String DoDeleteRole() {
		return "../../CCBill/Admin/BillRole.htm?s=34&FrmID=" + this.getNo() + "&CtrlObj=BtnDelete";
	}

	/**
	 数据查询权限规则

	 @return
	*/
	public final String DoSearchDataRole() {
		return "../../CCBill/Admin/SearchDataRole.htm?s=34&FrmID=" + this.getNo();
	}

		///#endregion 权限控制.


		///#region 报表定义
	/**
	 选择显示的列

	 @return
	*/
	public final String DoRpt_ColsChose() {
		return "../../CCBill/Admin/ColsChose.htm?FrmID=" + this.getNo();
	}
	/**
	 设置多表头

	 @return
	*/
	public final String DoRptMTitle() {
		return "../../Comm/Sys/MultiTitle.htm?EnsName=" + this.getNo() + "&DoType=Bill";
	}
	/**
	 列的顺序

	 @return
	*/
	public final String DoRpt_ColsIdxAndLabel() {
		return "../../CCBill/Admin/ColsIdxAndLabel.htm?FrmID=" + this.getNo();
	}
	/**
	 查询条件

	 @return
	*/
	public final String DoRpt_SearchCond() {
		return "../../CCBill/Admin/SearchCond.htm?FrmID=" + this.getNo();
	}

	public final String DoRpt_Setting() {
		return "../Sys/SearchSetting.htm?EnsName=" + this.getNo() + "&SettingType=1";
	}

		///#endregion 报表定义.

	public final String ToolbarSetting() {
		return "../../CCBill/Admin/ToolbarSetting.htm?s=34&FrmID=" + this.getNo();
	}
	public final String DoPageLoadFull() {
		return "../../Admin/FoolFormDesigner/MapExt/PageLoadFull.htm?s=34&FK_MapData=" + this.getNo() + "&ExtType=PageLoadFull&RefNo=";
	}
	/**
	 表单事件

	 @return
	*/
	public final String DoEvent() {
		return "../../Admin/CCFormDesigner/Action.htm?FK_MapData=" + this.getNo() + "&T=sd&FK_Node=0";
	}

	/**
	 检查检查实体类型
	*/

	public final void CheckEnityTypeAttrsFor_Bill() throws Exception {
		CheckEnityTypeAttrsFor_Bill(false);
	}

	public final void CheckEnityTypeAttrsFor_Bill(boolean isHavePFrmID) throws Exception {
		//取出来全部的属性.
		MapAttrs attrs = new MapAttrs(this.getNo());

		//为attr找到gfID.
		GroupFields gfs = new GroupFields();
		gfs.Retrieve("FrmID", this.getNo());
		if (gfs.size() == 0)
		{
			GroupField gf = new GroupField();
			gf.setLab("基本信息");
			gf.setFrmID(this.getNo());
			gf.Insert();
			gfs.AddEntity(gf);
		}
		int gfID = gfs.get(0).GetValIntByKey("OID");

			///#region 补充上流程字段到 NDxxxRpt.
		if (attrs.contains(this.getNo() + "_" + GERptAttr.Title) == false)
		{
			/* 标题 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.Title); // "FlowEmps";
			attr.setName("标题"); //   单据模式， ccform的模式.
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(true);
			attr.setMinLen(0);
			attr.setMaxLen(400);
			attr.setIdx(-1);
			attr.setGroupID(gfID); //其他字段都有.
			attr.Insert();
		}

		if (attrs.contains(this.getNo() + "_" + GERptAttr.OID) == false)
		{
			/* WorkID */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setKeyOfEn("OID");
			attr.setName("主键ID");
			attr.setMyDataType(DataType.AppInt);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setDefVal("0");
			attr.setEditType(EditType.Readonly);
			attr.setGroupID(gfID);
			attr.Insert();
		}
		if (attrs.contains(this.getNo() + "_" + GERptAttr.BillNo) == false)
		{
			/* 单据编号 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.BillNo);

			attr.setName("单据编号"); //  单据编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.setGroupID(gfID);
			attr.Insert();
		}

		if (attrs.contains(this.getNo() + "_" + GERptAttr.AtPara) == false)
		{
			/* 参数 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.AtPara);
			attr.setName("参数"); // 单据编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setMinLen(0);
			attr.setMaxLen(4000);
			attr.setIdx(-99);
			attr.setGroupID(gfID);
			attr.Insert();
		}

		if (attrs.contains(this.getNo() + "_BillState") == false)
		{
			/* 单据状态 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn("BillState"); // "FlowEmps";
			attr.setName("单据状态");
			attr.setMyDataType(DataType.AppInt);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(true);
			attr.setMinLen(0);
			attr.setMaxLen(10);
			attr.setIdx(-98);
			attr.setGroupID(gfID);
			attr.Insert();
		}

		if (attrs.contains(this.getNo() + "_Starter") == false)
		{
			/* 发起人 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn("Starter");
			attr.setName("制单人");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.setGroupID(gfID);
			attr.Insert();
		}
		if (attrs.contains(this.getNo() + "_StarterName") == false)
		{
			/* 创建人名称 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn("StarterName");
			attr.setName("制单人名称");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(32);
			attr.setIdx(-1);
			attr.setGroupID(gfID);
			attr.Insert();
		}

		if (attrs.contains(this.getNo() + "_RDT") == false)
		{
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn("RDT");
			attr.setName("制单日期");
			attr.setMyDataType(DataType.AppDateTime);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setIdx(-97);
			attr.setGroupID(gfID);
			attr.Insert();
		}
		if (attrs.contains(this.getNo() + "_DeptNo") == false)
		{
			/* 创建人部门 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn("DeptNo");
			attr.setName("制单人部门");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.setGroupID(gfID);
			attr.Insert();
		}
		if (attrs.contains(this.getNo() + "_OrgNo") == false)
		{
			/* 创建人名称 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn("OrgNo");
			attr.setName("制单人所在的组织");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.setGroupID(gfID);
			attr.Insert();
		}
		if (isHavePFrmID == true)
		{
			if (attrs.contains(this.getNo() + "_PWorkID") == false)
			{
				MapAttr attr = new MapAttr();
				attr.setFrmID( this.getNo());
				attr.setHisEditType(EditType.UnDel);
				attr.setKeyOfEn("PWorkID");
				attr.setName("实体发起的单据");
				attr.setMyDataType(DataType.AppInt);
				attr.setUIContralType(UIContralType.TB);
				attr.setLGType(FieldTypeS.Normal);

				attr.setUIVisible(false);
				attr.setUIIsEnable(false);
				attr.setMinLen(0);
				attr.setMaxLen(50);
				attr.setIdx(-1);
				attr.setGroupID(gfID);
				attr.Insert();
			}

			if (attrs.contains(this.getNo() + "_PFrmID") == false)
			{
				MapAttr attr = new MapAttr();
				attr.setFrmID( this.getNo());
				attr.setHisEditType(EditType.UnDel);
				attr.setKeyOfEn("PFrmID");
				attr.setName("实体名称");
				attr.setMyDataType(DataType.AppString);
				attr.setUIContralType(UIContralType.TB);
				attr.setLGType(FieldTypeS.Normal);

				attr.setUIVisible(false);
				attr.setUIIsEnable(false);
				attr.setMinLen(0);
				attr.setMaxLen(200);
				attr.setIdx(-1);
				attr.setGroupID(gfID);
				attr.Insert();
			}

		}

			///#endregion 补充上流程字段。
	}
	/**
	 绑定菜单树

	 @return 返回执行结果.
	*/
	public final String DoBindMenu(String menumDirNo, String menuName) throws Exception {
		String sql = "SELECT FK_App FROM GPM_Menu WHERE No='" + menumDirNo + "'";
		String app = DBAccess.RunSQLReturnString(sql);

		String guid = DBAccess.GenerGUID(0, null, null);

		String url = "../WF/CCBill/Search.htm?FrmID=" + this.getNo();
		sql = "INSERT INTO GPM_Menu (No, Name, ParentNo, Idx, MenuType, FK_App, Url, OpenWay,Icon,MenuCtrlWay) VALUES ('" + guid + "', '" + menuName + "', '" + menumDirNo + "', 1, 4, '" + app + "', '" + url + "',  0,'',1)";
		DBAccess.RunSQL(sql);
		return "加入成功,如何<a href='En.htm?EnName=BP.GPM.Menu&No=" + guid + "'>控制权限请转GPM.</a>";
	}


		///#region 业务逻辑.
	public final String CreateBlankWorkID() throws Exception {
		return String.valueOf(bp.ccbill.Dev2Interface.CreateBlankBillID(this.getNo(), bp.web.WebUser.getNo(), null));
	}

		///#endregion 业务逻辑.


		///#region 方法操作.
	/**
	 打开单据

	 @return
	*/
	public final String DoOpenBill() {
		return "../../CCBill/SearchBill.htm?FrmID=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
	}
	public final String DoAPI() {
		return "../../Admin/FoolFormDesigner/Bill/API.htm?FrmID=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
	}

		///#endregion 方法操作.
}
