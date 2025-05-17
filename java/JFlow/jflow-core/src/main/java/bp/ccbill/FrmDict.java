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
 实体表单
*/
public class FrmDict extends EntityNoName
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
		return EntityType.forValue(this.GetValIntByKey(FrmDictAttr.EntityType));
	}
	public final void setEntityType(EntityType value)  {
		this.SetValByKey(FrmDictAttr.EntityType, value.getValue());
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
	public final String getFKFormTree()  {
		return this.GetValStrByKey(MapDataAttr.FK_FormTree);
	}
	public final void setFKFormTree(String value)  {
		this.SetValByKey(MapDataAttr.FK_FormTree, value);
	}
	/**
	 新建模式 @0=表格模式@1=卡片模式@2=不可用
	*/
	public final int getBtnNewModel() {
		return this.GetValIntByKey(FrmDictAttr.BtnNewModel);
	}
	public final void setBtnNewModel(int value)  {
		this.SetValByKey(FrmDictAttr.BtnNewModel, value);
	}
	/**
	 单据格式(流水号4)
	*/
	public final String getBillNoFormat() throws Exception {
		return "4";
		/*String str = this.GetValStrByKey(FrmDictAttr.BillNoFormat);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = "{LSH4}";
		}
		return str;*/
	}
	public final void setBillNoFormat(String value)  {
		this.SetValByKey(FrmDictAttr.BillNoFormat, value);
	}
	/**
	 单据编号生成规则
	*/
	public final String getTitleRole() throws Exception {
		String str = this.GetValStrByKey(FrmDictAttr.TitleRole);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = "@WebUser.DeptName @WebUser.Name @RDT";
		}
		return str;
	}
	public final void setTitleRole(String value)  {
		this.SetValByKey(FrmDictAttr.BillNoFormat, value);
	}
	/**
	 新建标签
	*/
	public final String getBtnNewLable()  {
		return this.GetValStrByKey(FrmDictAttr.BtnNewLable);
	}
	/**
	 删除标签
	*/
	public final String getBtnDelLable()  {
		return this.GetValStrByKey(FrmDictAttr.BtnDelLable);
	}
	/**
	 保存标签
	*/
	public final String getBtnSaveLable()  {
		return this.GetValStrByKey(FrmDictAttr.BtnSaveLable);
	}
	/**
	 提交标签
	*/
	public final String getBtnSubmitLable()  {
		return this.GetValStrByKey(FrmDictAttr.BtnSubmitLable);
	}
	/**
	 查询标签
	*/
	public final String getBtnSearchLabel()  {
		return this.GetValStrByKey(FrmDictAttr.BtnSearchLabel);
	}
	/**
	 数据快照
	*/
	public final String getBtnDataVer()  {
		return this.GetValStrByKey(FrmDictAttr.BtnDataVer);
	}
	/**
	 分组按钮
	*/
	public final boolean getBtnGroupEnable() {
		return this.GetValBooleanByKey(FrmDictAttr.BtnGroupEnable);
	}
	public final String getBtnGroupLabel()  {
		return this.GetValStrByKey(FrmDictAttr.BtnGroupLabel);
	}
	/**
	 打印HTML按钮
	*/
	public final boolean getBtnPrintHtmlEnable() {
		return this.GetValBooleanByKey(FrmDictAttr.BtnPrintHtmlEnable);
	}
	public final String getBtnPrintHtml()  {
		return this.GetValStrByKey(FrmDictAttr.BtnPrintHtml);
	}
	/**
	 打印PDF按钮
	*/
	public final boolean getBtnPrintPDFEnable() {
		return this.GetValBooleanByKey(FrmDictAttr.BtnPrintPDFEnable);
	}
	public final String getBtnPrintPDF()  {
		return this.GetValStrByKey(FrmDictAttr.BtnPrintPDF);
	}
	/**
	 打印RTF按钮
	*/
	public final boolean getBtnPrintRTFEnable() {
		return this.GetValBooleanByKey(FrmDictAttr.BtnPrintRTFEnable);
	}
	public final String getBtnPrintRTF()  {
		return this.GetValStrByKey(FrmDictAttr.BtnPrintRTF);
	}
	/**
	 打印CCWord按钮
	*/
	public final boolean getBtnPrintCCWordEnable() {
		return this.GetValBooleanByKey(FrmDictAttr.BtnPrintCCWordEnable);
	}
	public final String getBtnPrintCCWord()  {
		return this.GetValStrByKey(FrmDictAttr.BtnPrintCCWord);
	}
	/**
	 打印ZIP按钮
	*/
	public final boolean getBtnExpZipEnable() {
		return this.GetValBooleanByKey(FrmDictAttr.BtnExpZipEnable);
	}
	public final String getBtnExpZip()  {
		return this.GetValStrByKey(FrmDictAttr.BtnExpZip);
	}


		///#endregion


		///#region 构造方法
	/**
	 实体表单
	*/
	public FrmDict()
	{
	}
	/**
	 实体表单

	 @param no 映射编号
	*/
	public FrmDict(String no) throws Exception
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
		Map map = new Map("Sys_MapData", "实体表单");
		map.setCodeStruct( "4");


			///#region 基本属性.
		map.AddGroupAttr("基本信息", "");
		map.AddTBStringPK(MapDataAttr.No, null, "表单编号", true, true, 1, 190, 20);
		map.SetHelperAlert(MapDataAttr.No, "也叫表单ID,系统唯一.");

		map.AddDDLSysEnum(MapDataAttr.FrmType, 0, "表单类型", true, true, "BillFrmType", "@0=经典表单@1=自由表单@8=开发者表单");
		map.AddTBString(MapDataAttr.PTable, null, "存储表", true, false, 0, 500, 20, true);
		map.SetHelperAlert(MapDataAttr.PTable, "存储的表名,如果您修改一个不存在的系统将会自动创建一个表.");

		map.AddTBString(MapDataAttr.Name, null, "表单名称", true, false, 0, 200, 20, true);
		map.AddDDLEntities(MapDataAttr.FK_FormTree, "01", "表单类别", new SysFormTrees(), false);

			///#endregion 基本属性.


			///#region 外观.
		map.AddGroupAttr("外观", "");
		map.AddDDLSysEnum(FrmAttr.RowOpenModel, 2, "行记录打开模式", true, true, "RowOpenMode", "@0=新窗口打开@1=在本窗口打开@2=弹出窗口打开,关闭后不刷新列表@3=弹出窗口打开,关闭后刷新列表");
		String cfg = "@0=MyDictFrameWork.htm 实体与实体相关功能编辑器";
		cfg += "@1=MyDict.htm 实体编辑器";
		cfg += "@2=MyBill.htm 单据编辑器";
		cfg += "@9=自定义URL";
		map.AddDDLSysEnum("SearchDictOpenType", 0, "双击行打开内容", true, true, "SearchDictOpenType", cfg);
		map.AddBoolean(EnCfgAttr.IsSelectMore, true, "是否下拉查询条件多选?", true, true);
		map.AddTBString(EnCfgAttr.UrlExt, null, "要打开的Url", true, false, 0, 500, 60, true);
		map.AddTBInt(FrmAttr.PopHeight, 500, "弹窗高度", true, false);
		map.AddTBInt(FrmAttr.PopWidth, 760, "弹窗宽度", true, false);

		map.AddDDLSysEnum(MapDataAttr.TableCol, 0, "表单显示列数", true, true, "经典表单显示方式", "@0=4列@1=6列@2=上下模式3列");

		map.AddDDLSysEnum(FrmAttr.EntityEditModel, 0, "编辑模式", true, true, FrmAttr.EntityEditModel, "@0=表格@1=行编辑");
		map.SetHelperAlert(FrmAttr.EntityEditModel, "用什么方式打开实体列表进行编辑0=只读查询模式SearchDict.htm,1=行编辑模式SearchEditer.htm");

			///#endregion 外观.


			///#region 实体表单.
		map.AddGroupAttr("实体表单", "");

		map.AddDDLSysEnum(FrmDictAttr.EntityType, 0, "业务类型", true, false, FrmDictAttr.EntityType, "@0=独立表单@1=单据@2=编号名称实体@3=树结构实体");
		map.SetHelperAlert(FrmDictAttr.EntityType, "该实体的类型,@0=单据@1=编号名称实体@2=树结构实体.");

		map.AddTBString(FrmDictAttr.BillNoFormat, null, "实体编号规则", true, false, 0, 100, 20, true);
		map.SetHelperAlert(FrmDictAttr.BillNoFormat, "\t\n实体编号规则: \t\n 2标识:01,02,03等, 3标识:001,002,003,等..");
		map.AddTBString(FrmBillAttr.SortColumns, null, "排序字段", true, false, 0, 100, 20, true);
		map.AddTBString(FrmBillAttr.ColorSet, null, "表格列颜色设置", true, false, 0, 100, 20, true);

		String msg = "对字段的颜色处理";
		msg += "\t\n @Age:From=0,To=18,Color=green;From=19,To=30,Color=red";
		map.SetHelperAlert(FrmBillAttr.ColorSet, msg);

		map.AddTBString(FrmBillAttr.RowColorSet, null, "表格行颜色设置", true, false, 0, 100, 20, true);
		map.SetHelperAlert(FrmBillAttr.RowColorSet, "按照指定字段存储的颜色设置表格行的背景色");

		map.AddTBString(FrmBillAttr.FieldSet, null, "字段求和求平均设置", true, false, 0, 100, 20, true);
		//字段格式化函数.
		map.AddTBString("ForamtFunc", null, "字段格式化函数", true, false, 0, 200, 60, true);
		  msg = "对字段的显示使用函数进行处理";
		msg += "\t\n 1. 对于字段内容需要处理后在输出出来.";
		msg += "\t\n 2. 比如：原字段内容 @zhangsa,张三@lisi,李四 显示的内容为 张三,李四";
		msg += "\t\n 3. 配置格式: 字段名@函数名; 比如:  FlowEmps@DealFlowEmps; ";
		msg += "\t\n 4. 函数写入到 /DataUser/JSLibData/SearchSelf.js";
		map.SetHelperAlert("ForamtFunc", msg);

		map.AddTBInt(FrmBillAttr.TableStyle, 0, "表格内容展示类型", false, false);
			///#endregion 实体表单.

		///#region MyBill - 按钮权限.
		//map.AddTBString(FrmDictAttr.BtnNewLable, "新建", "新建", true, false, 0, 50, 20);
		//map.AddDDLSysEnum(FrmDictAttr.BtnNewModel, 0, "新建模式", true, true, FrmDictAttr.BtnNewModel,
		//  "@0=表格模式@1=卡片模式@2=不可用", true);

		//map.AddTBString(FrmDictAttr.BtnSaveLable, "保存", "保存", true, false, 0, 50, 20);
		//map.AddBoolean(FrmDictAttr.BtnSaveEnable, true, "是否可用？", true, true);

		//  map.AddTBString(FrmDictAttr.BtnSubmitLable, "提交", "提交", true, false, 0, 50, 20);
		//map.AddBoolean(FrmDictAttr.BtnSubmitEnable, true, "是否可用？", true, true);

		//删除.
		//map.AddTBString(FrmDictAttr.BtnDelLable, "删除", "删除", true, false, 0, 50, 20);

		//数据版本.
		// map.AddTBString(FrmDictAttr.BtnDataVer, "数据快照", "数据快照", true, false, 0, 50, 20);

		//map.AddBoolean(FrmDictAttr.BtnDelEnable, true, "是否可用？", true, true);

		//map.AddTBString(FrmDictAttr.BtnSearchLabel, "列表", "列表", true, false, 0, 50, 20);
		//map.AddBoolean(FrmDictAttr.BtnSearchEnable, true, "是否可用？", true, true);

		//map.AddTBString(FrmDictAttr.BtnGroupLabel, "分析", "分析", true, false, 0, 50, 20);
		//map.AddBoolean(FrmDictAttr.BtnGroupEnable, true, "是否可用？", true, true);

		//map.AddTBString(FrmDictAttr.BtnPrintHtml, "打印Html", "打印Html", true, false, 0, 50, 20);
		//map.AddBoolean(FrmDictAttr.BtnPrintHtmlEnable, false, "是否可用？", true, true);

		//map.AddTBString(FrmDictAttr.BtnPrintPDF, "打印PDF", "打印PDF", true, false, 0, 50, 20);
		//map.AddBoolean(FrmDictAttr.BtnPrintPDFEnable, false, "是否可用？", true, true);

		//map.AddTBString(FrmDictAttr.BtnPrintRTF, "打印RTF", "打印RTF", true, false, 0, 50, 20);
		//map.AddBoolean(FrmDictAttr.BtnPrintRTFEnable, false, "是否可用？", true, true);

		//map.AddTBString(FrmDictAttr.BtnPrintCCWord, "打印CCWord", "打印CCWord", true, false, 0, 50, 20);
		//map.AddBoolean(FrmDictAttr.BtnPrintCCWordEnable, false, "是否可用？", true, true);

		//map.AddTBString(FrmDictAttr.BtnExpZip, "导出zip文件", "导出zip文件", true, false, 0, 50, 20);
		//map.AddBoolean(FrmDictAttr.BtnExpZipEnable, false, "是否可用？", true, true);
		///#endregion 按钮权限.

		// #region 查询按钮权限.
		// map.AddTBString(FrmDictAttr.BtnImpExcel, "导入", "导入Excel文件", true, false, 0, 50, 20);
		//map.AddBoolean(FrmDictAttr.BtnImpExcelEnable, true, "是否可用？", true, true);

		//map.AddTBString(FrmDictAttr.BtnExpExcel, "导出", "导出Excel文件", true, false, 0, 50, 20);
		//map.AddBoolean(FrmDictAttr.BtnExpExcelEnable, true, "是否可用？", true, true);

		// map.AddTBString(FrmDictAttr.BtnGroupLabel, "分析", "分析", true, false, 0, 50, 20);
		//map.AddBoolean(FrmDictAttr.BtnGroupEnable, true, "是否可用？", true, true);
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

			///#endregion 设计者信息.


			///#region 扩展参数.
		map.AddTBAtParas(3000); //参数属性.
		map.AddTBString(FrmDictAttr.Tag0, null, "Tag0", false, false, 0, 500, 20);
		map.AddTBString(FrmDictAttr.Tag1, null, "Tag1", false, false, 0, 4000, 20);
		map.AddTBString(FrmDictAttr.Tag2, null, "Tag2", false, false, 0, 500, 20);
		map.AddTBInt("ShowColModel", 0, "显示列", false, false);

		///#endregion 扩展参数.


			///#region 基本功能.

		map.AddGroupMethod("基本功能");

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
		rm.Title = "表单事件"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoEvent";
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
		//  map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "单据url的API"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoAPI";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		//rm.GroupName = "开发接口";
	   // map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "打开数据(表格)"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoOpenBillDict";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "打开数据(行编辑)"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoOpenBillEditer";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);


		//rm = new RefMethod();
		//rm.Title = "绑定到菜单目录"; // "设计表单";
		//rm.getHisAttrs().AddDDLSQL("MENUNo", null, "选择菜单目录", "SELECT No,Name FROM GPM_Menu WHERE MenuType=3");
		//rm.getHisAttrs().AddTBString("Name", "@Name", "菜单名称", true, false, 0, 100, 100);
		//rm.ClassMethodName = this.ToString() + ".DoBindMenu";
		//rm.Visable = true;
		//rm.refMethodType = RefMethodType.Func;
		//rm.Target = "_blank";
		////rm.GroupName = "开发接口";
		//map.AddRefMethod(rm);



			///#endregion 基本功能.


			///#region 查询定义.
		map.AddGroupMethod("查询定义");
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

			///#endregion 报表定义.


			///#region 权限规则.
		map.AddGroupMethod("权限规则");
		rm = new RefMethod();
		rm.Title = "创建规则"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoCreateRole";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkModel;
		rm.RefAttrKey = FrmDictAttr.BtnNewLable;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "保存规则"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoSaveRole";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkModel;
		rm.RefAttrKey = FrmDictAttr.BtnSaveLable;
		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "提交规则"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoSubmitRole";
		//rm.Visable = true;
		//rm.refMethodType = RefMethodType.LinkModel;
		//rm.RefAttrKey = FrmDictAttr.BtnSubmitLable;
		//rm.GroupName = "权限规则";
		//map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "删除规则"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoDeleteRole";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkModel;
		rm.RefAttrKey = FrmDictAttr.BtnDelLable;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "数据版本权限规则"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoBtnDataVer";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkModel;
		rm.RefAttrKey = FrmDictAttr.BtnDataVer;
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "查询权限"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoSearchRole";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkModel;
		rm.RefAttrKey = FrmDictAttr.BtnSearchLabel;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "数据查询权限规则";
		rm.ClassMethodName = this.toString() + ".DoSearchDataRole()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

			///#endregion

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	/**
	 执行模板复制

	 @param newFrmID
	 @param frmName
	 @param ptable
	 @return
	*/
	public final String DoCopyTemplate(String newFrmID, String frmName, String ptable) throws Exception {
		boolean isSetReadonly = true;
		boolean isCopyMethod = false;

		MapData md = new MapData(this.getNo());
		md.DoCopy(newFrmID, frmName);

		MapData mdTo = new MapData(newFrmID);
		if (ptable == null)
		{
			mdTo.setPTable(ptable);
		}
		mdTo.setName(frmName);
		mdTo.Update();


			///#region 如果要设置为只读的.
		if (isSetReadonly == true)
		{
			MapAttrs mattrs = new MapAttrs();
			mattrs.Retrieve(MapAttrAttr.FK_MapData, newFrmID, null);

			for (MapAttr item : mattrs.ToJavaList())
			{
				if (item.getUIIsEnable() == false)
				{
					continue;
				}

				item.setUIIsEnable(false);
				item.Update();
			}
		}

			///#endregion 如果要设置为只读的.


		return "执行成功.";
	}

	public final void InsertToolbarBtns() throws Exception {
		//删除已经存在的.
		DBAccess.RunSQL("DELETE FROM Frm_Collection WHERE FrmID='" + this.getNo() + "'");
		DBAccess.RunSQL("DELETE FROM Frm_ToolbarBtn WHERE FrmID='" + this.getNo() + "'");
		//表单的工具栏权限
		ToolbarBtn btn = new ToolbarBtn();
		if (this.getEntityType() == EntityType.FrmDict || this.getEntityType() == EntityType.FrmEntityNoName)
		{
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


			btn = new ToolbarBtn();
			btn.setFrmID(this.getNo());
			btn.setBtnID("Delete");
			btn.setBtnLab("删除");
			btn.setMyPK(btn.getFrmID() + "_" + btn.getBtnID());
			btn.SetValByKey("Idx", 2);
			btn.Insert();
		}


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

		if (this.getEntityType() == EntityType.FrmDict || this.getEntityType() == EntityType.FrmEntityNoName)
		{
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



	}


	@Override
	protected void afterInsert() throws Exception
	{
		InsertToolbarBtns();
		CheckEnityTypeAttrsFor_Dict();

		super.afterInsertUpdateAction();
	}

	/**
	 检查enittyNoName类型的实体
	*/
	public final void CheckEnityTypeAttrsFor_Dict() throws Exception {
		//取出来全部的属性.
		MapAttrs attrs = new MapAttrs(this.getNo());

		//创建表单ID。
		int groupID = DBAccess.RunSQLReturnValInt("SELECT MAX(GroupID) FROM Sys_MapAttr WHERE FK_MapData='" + this.getNo() + "'", 0);
		if (groupID == 0)
		{
			GroupField gf = new GroupField();
			gf.setLab("基本信息");
			gf.setFrmID(this.getNo());
			gf.Insert();
			groupID = (int) gf.getOID();
		}
			///#region 补充上流程字段到 NDxxxRpt.
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
			attr.setGroupID(groupID);
			attr.Insert();
		}
		if (attrs.contains(this.getNo() + "_" + GERptAttr.BillNo) == false)
		{
			/* 单据编号 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.BillNo);
			attr.setName("编号"); //  单据编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.setGroupID(groupID);
			attr.Insert();
		}

		if (attrs.contains(this.getNo() + "_" + GERptAttr.Title) == false)
		{
			/* 名称 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.Title); // "FlowEmps";
			attr.setName("名称"); //   单据模式， ccform的模式.
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(true);
			attr.setUIIsLine(true);
			attr.setMinLen(0);
			attr.setMaxLen(400);
			attr.setIdx(-90);
			attr.setGroupID(groupID);
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
			attr.setUIContralType(UIContralType.TB);

			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(true);
			attr.setMinLen(0);
			attr.setMaxLen(10);
			attr.setIdx(-98);
			attr.setGroupID(groupID);
			attr.Insert();
		}

		if (attrs.contains(this.getNo() + "_Starter") == false)
		{
			/* 发起人 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn("Starter");
			attr.setName("创建人");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.setGroupID(groupID);
			attr.Insert();
		}
		if (attrs.contains(this.getNo() + "_StarterName") == false)
		{
			/* 创建人名称 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn("StarterName");
			attr.setName("创建人名称");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(32);
			attr.setIdx(-1);
			attr.setGroupID(groupID);
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
			attr.setGroupID(groupID);
			attr.Insert();
		}

		if (attrs.contains(this.getNo() + "_RDT") == false)
		{
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn("RDT");
			attr.setName("创建时间");
			attr.setMyDataType(DataType.AppDateTime);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setUIIsLine (false);
			attr.setIdx(-97);
			attr.setGroupID(groupID);
			attr.Insert();
		}
		if (attrs.contains(this.getNo() + "_DeptNo") == false)
		{
			/* 创建人部门 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn("_DeptNo");
			attr.setName("创建人部门");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.setGroupID(groupID);
			attr.Insert();
		}
		if (attrs.contains(this.getNo() + "_OrgNo") == false)
		{
			/* 创建人名称 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn("OrgNo");
			attr.setName("创建人所在的组织");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.setGroupID(groupID);
			attr.Insert();
		}

			///#endregion 补充上流程字段。


			///#region 注册到外键表.
		SFTable sf = new SFTable();
		sf.setNo(this.getNo());
		if (sf.RetrieveFromDBSources() == 0)
		{
			sf.setName(this.getName());
			sf.setSrcType(DictSrcType.SQL);
			sf.setSrcTable(this.getPTable());
			sf.setColumnValue("BillNo");
			sf.setColumnText("Title");
			sf.setSelectStatement("SELECT BillNo AS No, Title as Name FROM " + this.getPTable());
			sf.SetPara("EnName", "TS.FrmUI.SFTableSQLNoName");
			sf.Insert();
		}

			///#endregion 注册到外键表
	}


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
	public final String DoRpt_SearchCond() throws Exception {
		//  http://localhost:2296/WF/CCBill/Admin/Collection/SearchCond.htm?FrmID=Dict_XueShengTaiZhang&ModuleNo=0d5b6b0b-9051-44cf-b989-7e9d802f01af
		return "../../CCBill/Admin/Collection/SearchCond.htm?FrmID=" + this.getNo();
	}

		///#endregion 报表定义.


		///#region 权限控制.
	/**
	 保存权限规则

	 @return
	*/
	public final String DoSaveRole() {
		return "../../CCBill/Admin/BillRole.htm?s=34&FrmID=" + this.getNo() + "&CtrlObj=BtnSave";
	}
	/**
	 数据版本权限规则.

	 @return
	*/
	public final String DoBtnDataVer() {
		return "../../CCBill/Admin/DataVerRole.htm?s=34&FrmID=" + this.getNo() + "&CtrlObj=BtnSave";
	}

	/**
	 提交权限规则

	 @return
	*/
	public final String DoSubmitRole() {
		return "../../CCBill/Admin/BillRole.htm?s=34&FrmID=" + this.getNo() + "&CtrlObj=BtnSubmit";
	}
	/**
	 新增权限规则

	 @return
	*/
	public final String DoCreateRole() {
		return "../../CCBill/Admin/BillRole.htm?s=34&FrmID=" + this.getNo() + "&CtrlObj=BtnNew";
	}
	/**
	 删除权限规则

	 @return
	*/
	public final String DoDeleteRole() {
		return "../../CCBill/Admin/BillRole.htm?s=34&FrmID=" + this.getNo() + "&CtrlObj=BtnDelete";
	}
	/**
	 查询权限

	 @return
	*/
	public final String DoSearchRole() {
		return "../../CCBill/Admin/BillRole.htm?s=34&FrmID=" + this.getNo() + "&CtrlObj=BtnSearch";
	}
	/**
	 数据查询权限规则

	 @return
	*/
	public final String DoSearchDataRole() {
		return "../../CCBill/Admin/SearchDataRole.htm?s=34&FrmID=" + this.getNo();
	}

		///#endregion 权限控制.

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
	public final String CreateBlankWorkID() {
		try {
			return String.valueOf(Dev2Interface.CreateBlankDictID(this.getNo(), null, null));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

		///#endregion 业务逻辑.


		///#region 方法操作.
	/**
	 打开单据

	 @return
	*/
	public final String DoOpenBillDict() {
		return "../../CCBill/SearchDict.htm?FrmID=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
	}
	public final String DoOpenBillEditer() {
		return "../../CCBill/SearchEditer.htm?FrmID=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
	}
	public final String DoAPI() {
		return "../../Admin/FoolFormDesigner/Bill/API.htm?FrmID=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
	}

		///#endregion 方法操作.

}
