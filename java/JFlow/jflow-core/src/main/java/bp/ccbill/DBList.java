package bp.ccbill;

import bp.da.DataType;
import bp.en.*;
import bp.sys.*;
import bp.wf.GERptAttr;

/**
 数据源实体
*/
public class DBList extends EntityNoName
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
		return EntityType.forValue(this.GetValIntByKey(DBListAttr.EntityType));
	}
	public final void setEntityType(EntityType value)  {
		this.SetValByKey(DBListAttr.EntityType, value.getValue());
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
		return this.GetValIntByKey(DBListAttr.BtnNewModel);
	}
	public final void setBtnNewModel(int value)  {
		this.SetValByKey(DBListAttr.BtnNewModel, value);
	}
	/**
	 单据格式(流水号4)
	*/
	public final String getBillNoFormat() throws Exception {
		String str = this.GetValStrByKey(DBListAttr.BillNoFormat);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = "{LSH4}";
		}
		return str;
	}
	public final void setBillNoFormat(String value)  {
		this.SetValByKey(DBListAttr.BillNoFormat, value);
	}
	/**
	 单据编号生成规则
	*/
	public final String getTitleRole() throws Exception {
		String str = this.GetValStrByKey(DBListAttr.TitleRole);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = "@WebUser.FK_DeptName @WebUser.Name @RDT";
		}
		return str;
	}
	/**
	 新建标签
	*/
	public final String getBtnNewLable()  {
		return this.GetValStrByKey(DBListAttr.BtnNewLable);
	}
	/**
	 删除标签
	*/
	public final String getBtnDelLable()  {
		return this.GetValStrByKey(DBListAttr.BtnDelLable);
	}
	/**
	 保存标签
	*/
	public final String getBtnSaveLable()  {
		return this.GetValStrByKey(DBListAttr.BtnSaveLable);
	}
	/**
	 提交标签
	*/
	public final String getBtnSubmitLable()  {
		return this.GetValStrByKey(DBListAttr.BtnSubmitLable);
	}
	/**
	 查询标签
	*/
	public final String getBtnSearchLabel()  {
		return this.GetValStrByKey(DBListAttr.BtnSearchLabel);
	}
	/**
	 数据快照
	*/
	public final String getBtnDataVer()  {
		return this.GetValStrByKey(DBListAttr.BtnDataVer);
	}
	/**
	 分组按钮
	*/
	public final boolean getBtnGroupEnable() {
		return this.GetValBooleanByKey(DBListAttr.BtnGroupEnable);
	}
	public final String getBtnGroupLabel()  {
		return this.GetValStrByKey(DBListAttr.BtnGroupLabel);
	}
	/**
	 打印HTML按钮
	*/
	public final boolean getBtnPrintHtmlEnable() {
		return this.GetValBooleanByKey(DBListAttr.BtnPrintHtmlEnable);
	}
	public final String getBtnPrintHtml()  {
		return this.GetValStrByKey(DBListAttr.BtnPrintHtml);
	}
	/**
	 打印PDF按钮
	*/
	public final boolean getBtnPrintPDFEnable() {
		return this.GetValBooleanByKey(DBListAttr.BtnPrintPDFEnable);
	}
	public final String getBtnPrintPDF()  {
		return this.GetValStrByKey(DBListAttr.BtnPrintPDF);
	}
	/**
	 打印RTF按钮
	*/
	public final boolean getBtnPrintRTFEnable() {
		return this.GetValBooleanByKey(DBListAttr.BtnPrintRTFEnable);
	}
	public final String getBtnPrintRTF()  {
		return this.GetValStrByKey(DBListAttr.BtnPrintRTF);
	}
	/**
	 打印CCWord按钮
	*/
	public final boolean getBtnPrintCCWordEnable() {
		return this.GetValBooleanByKey(DBListAttr.BtnPrintCCWordEnable);
	}
	public final String getBtnPrintCCWord()  {
		return this.GetValStrByKey(DBListAttr.BtnPrintCCWord);
	}
	/**
	 数据源类型
	*/
	public final int getDBType() {
		return this.GetValIntByKey(MapDataAttr.DBType);
	}
	public final void  setDBType(int value) {
		this.SetValByKey(MapDataAttr.DBType,value);
	}
	public final String getDBSrc()  {
		return this.GetValStrByKey(MapDataAttr.DBSrc);
	}
	public final void setDBSrc(String value)  {
		this.SetValByKey(MapDataAttr.DBSrc, value);
	}
	public final String getExpEn()  {
		return this.GetValStrByKey(MapDataAttr.ExpEn);
	}
	public final void setExpEn(String value)  {
		this.SetValByKey(MapDataAttr.ExpEn, value);
	}
	public final String getExpList()  {
		return this.GetValStrByKey(MapDataAttr.ExpList);
	}
	public final void setExpList(String value)  {
		this.SetValByKey(MapDataAttr.ExpList, value);
	}
	public final String getExpCount()  {
		return this.GetValStrByKey(MapDataAttr.ExpCount);
	}
	public final void setExpCount(String value)  {
		this.SetValByKey(MapDataAttr.ExpCount, value);
	}
	public final String getMainTable()  {
		return this.GetValStrByKey(DBListAttr.MainTable);
	}
	public final void setMainTable(String value)  {
		this.SetValByKey(DBListAttr.MainTable, value);
	}
	public final String getMainTablePK()  {
		return this.GetValStrByKey(DBListAttr.MainTablePK);
	}
	public final void setMainTablePK(String value)  {
		this.SetValByKey(DBListAttr.MainTablePK, value);
	}

		///#endregion


		///#region 构造方法
	/**
	 数据源实体
	*/
	public DBList()
	{
	}
	/**
	 数据源实体

	 @param no 映射编号
	*/
	public DBList(String no) throws Exception
	{
		super(no);
	}
	/**
	 * EnMap
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
			return this.get_enMap();
		Map map = new Map("Sys_MapData", "数据源实体");
		map.setCodeStruct("4");

		///#region 基本属性.
		map.AddGroupAttr("基本属性", "");
		map.AddTBStringPK(MapDataAttr.No, null, "表单编号", true, true, 1, 190, 20);
		map.SetHelperAlert(MapDataAttr.No, "也叫表单ID,系统唯一.");

		map.AddDDLSysEnum(MapDataAttr.FrmType, 0, "表单类型", true, true, "BillFrmType", "@0=经典表单@1=自由表单@8=开发者表单");
		//  map.AddTBString(MapDataAttr.PTable, null, "存储表", false, false, 0, 500, 20, true);
		// map.SetHelperAlert(MapDataAttr.PTable, "存储的表名,如果您修改一个不存在的系统将会自动创建一个表.");
		map.AddTBString(MapDataAttr.Name, null, "表单名称", true, false, 0, 200, 20, true);
		// map.AddDDLEntities(MapDataAttr.FK_FormTree, "01", "表单类别", new SysFormTrees(), false);
		// #endregion 基本属性.

			///#region 数据源.
		map.AddGroupAttr("数据源", "");
		map.AddTBInt(MapDataAttr.DBType, 0, "数据源类型", true, true);
		map.AddTBString(MapDataAttr.DBSrc, null, "数据源", false, false, 0, 600, 20);

		map.AddTBString(MapDataAttr.ExpEn, null, "实体数据源", false, false, 0, 600, 20, true);
		map.AddTBString(MapDataAttr.ExpList, null, "列表数据源", false, false, 0, 600, 20, true);

		map.AddTBString(DBListAttr.MainTable, null, "列表数据源主表", false, false, 0, 50, 20, false);
		map.AddTBString(DBListAttr.MainTablePK, null, "列表数据源主表主键", false, false, 0, 50, 20, false);
		map.AddTBString(MapDataAttr.ExpCount, null, "列表总数", false, false, 0, 600, 20, true);
		///#endregion 数据源.

			///#region 外观.
		map.AddGroupAttr("外观", "");
		map.AddDDLSysEnum(FrmAttr.RowOpenModel, 2, "行记录打开模式", true, true, "RowOpenMode", "@0=新窗口打开@1=在本窗口打开@2=弹出窗口打开,关闭后不刷新列表@3=弹出窗口打开,关闭后刷新列表");
		String cfg = "@0=MyDictFrameWork.htm 实体与实体相关功能编辑器";
		cfg += "@1=MyDict.htm 实体编辑器";
		cfg += "@2=MyBill.htm 单据编辑器";
		cfg += "@9=自定义URL";
		map.AddDDLSysEnum("SearchDictOpenType", 0, "双击行打开内容", true, true, "SearchDictOpenType", cfg);
		map.AddTBString(EnCfgAttr.UrlExt, null, "要打开的Url", true, false, 0, 500, 60, true);
		map.AddTBInt(FrmAttr.PopHeight, 500, "弹窗高度", true, false);
		map.AddTBInt(FrmAttr.PopWidth, 760, "弹窗宽度", true, false);

		map.AddDDLSysEnum(MapDataAttr.TableCol, 0, "表单显示列数", true, true, "经典表单显示方式", "@0=4列@1=6列@2=上下模式3列");

		map.AddDDLSysEnum(FrmAttr.EntityEditModel, 0, "编辑模式", true, true, FrmAttr.EntityEditModel, "@0=表格@1=行编辑");
		map.SetHelperAlert(FrmAttr.EntityEditModel, "用什么方式打开实体列表进行编辑0=只读查询模式SearchDict.htm,1=行编辑模式SearchEditer.htm");
			///#endregion 外观.

			///#region 数据源实体.
		map.AddDDLSysEnum(DBListAttr.EntityType, 0, "业务类型", true, false, DBListAttr.EntityType, "@0=独立表单@1=单据@2=编号名称实体@3=树结构实体");
		map.SetHelperAlert(DBListAttr.EntityType, "该实体的类型,@0=单据@1=编号名称实体@2=树结构实体.");

		map.AddTBString(DBListAttr.BillNoFormat, null, "实体编号规则", true, false, 0, 100, 20, true);
		map.SetHelperAlert(DBListAttr.BillNoFormat, "\t\n实体编号规则: \t\n 2标识:01,02,03等, 3标识:001,002,003,等..");
		map.AddTBString(FrmBillAttr.SortColumns, null, "排序字段", true, false, 0, 100, 20, true);
		map.AddTBString(FrmBillAttr.ColorSet, null, "颜色设置", true, false, 0, 100, 20, true);
		map.AddTBString(FrmBillAttr.FieldSet, null, "字段求和求平均设置", true, false, 0, 100, 20, true);

		//字段格式化函数.
		map.AddTBString("ForamtFunc", null, "字段格式化函数", true, false, 0, 200, 60, true);
		String msg = "对字段的显示使用函数进行处理";
		msg += "\t\n 1. 对于字段内容需要处理后在输出出来.";
		msg += "\t\n 2. 比如：原字段内容 @zhangsa,张三@lisi,李四 显示的内容为 张三,李四";
		msg += "\t\n 3. 配置格式: 字段名@函数名; 比如:  FlowEmps@DealFlowEmps; ";
		msg += "\t\n 4. 函数写入到 \\DataUser\\JSLibData\\SearchSelf.js";
		map.SetHelperAlert("ForamtFunc", msg);

			///#endregion 数据源实体.
		//增加参数字段.
		map.AddTBAtParas(4000);


			///#region 基本功能.
		RefMethod rm = new RefMethod();

		rm = new RefMethod();
		rm.Title = "步骤1: 设置数据源."; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoDBSrc";
		rm.Icon = "../../WF/Img/Event.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "步骤2: 实体数据"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoExpEn";
		rm.Icon = "../../WF/Img/Event.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "步骤3: 列表数据"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoExpList";
		rm.Icon = "../../WF/Img/Event.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "步骤4: 总数数据"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoExpCount";
		//rm.Icon = "../../WF/Img/Event.png";
		//rm.Visable = true;
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		//map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "步骤4: 测试"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoDBList";
		rm.Icon = "../../WF/Img/Event.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "查询条件"; // "设计表单";
						   //   rm.GroupName = "高级选项";
		rm.ClassMethodName = this.toString() + ".DoSearch";
		rm.Icon = "../../WF/Img/Event.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "视频教程"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoVideo";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);


			///#endregion 基本功能.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	public final String DoVideo()
	{
		return "https://www.bilibili.com/video/BV15P4y1p7Sj";
	}
	public final String DoDBSrc() {
		return "../../Comm/RefFunc/EnOnly.htm?EnName=BP.CCBill.DBListDBSrc&No=" + this.getNo();
	}

	public final String DoExpEn() {
		return "../../CCBill/Admin/DBList/FieldsORM.htm?s=34&FrmID=" + this.getNo() + "&ExtType=PageLoadFull&RefNo=ss3";
	}
	public final String DoExpList() {
		return "../../CCBill/Admin/DBList/ListDBSrc.htm?s=34&FrmID=" + this.getNo() + "&ExtType=PageLoadFull&RefNo=ss3";
	}
	public final String DoExpCount() {
		return "../../CCBill/Admin/DBList/ListDBCount.htm?s=34&FrmID=" + this.getNo() + "&ExtType=PageLoadFull&RefNo=ss3";
	}
	public final String DoDBList() {
		return "../../CCBill/SearchDBList.htm?FrmID=" + this.getNo();
	}
	public final String DoSearch() {
		return "../../CCBill/Admin/Collection/SearchCond.htm?s=34&FrmID=" + this.getNo() + "&ExtType=PageLoadFull&RefNo=ss3";
	}

	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		super.afterInsertUpdateAction();
	}

	/**
	 检查enittyNoName类型的实体
	*/
	public final void CheckEnityTypeAttrsFor_EntityNoName() throws Exception {
		//取出来全部的属性.
		MapAttrs attrs = new MapAttrs(this.getNo());


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
			attr.Insert();
		}

		if (attrs.contains(this.getNo() + "_" + GERptAttr.Title) == false)
		{
			/* 名称 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(this.getNo());
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
			attr.Insert();
		}
		if (attrs.contains(this.getNo() + "_FK_Dept") == false)
		{
			/* 创建人部门 */
			MapAttr attr = new MapAttr();
			attr.setFrmID( this.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn("FK_Dept");
			attr.setName("创建人部门");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
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
			attr.Insert();
		}

			///#endregion 补充上流程字段。

	}
}
