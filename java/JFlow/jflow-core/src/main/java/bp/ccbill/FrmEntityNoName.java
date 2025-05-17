package bp.ccbill;

import bp.ccbill.template.Collection;
import bp.ccbill.template.ToolbarBtn;
import bp.da.DBAccess;
import bp.da.DataType;
import bp.en.*;
import bp.sys.*;
import bp.wf.GERptAttr;
import bp.wf.template.SysFormTrees;

/**
 单据属性
*/
public class FrmEntityNoName extends EntityNoName
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
	 新建模式 @0=表格模式@1=卡片模式@2=不可用
	 */
	public final String getBtnNewModel()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnNewModel);
	}
	public final void setBtnNewModel(int value)  {
		this.SetValByKey(FrmDictAttr.BtnNewModel, value);
	}
	/**
	 单据格式(流水号4)
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
			str = "@WebUser.FK_DeptName @WebUser.Name @RDT";
		}
		return str;
	}
	public final void setTitleRole(String value)  {
		this.SetValByKey(FrmBillAttr.BillNoFormat, value);
	}
	/**
	 新建标签
	 */
	public final String getBtnNewLable()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnNewLable);
	}
	/**
	 删除标签
	 */
	public final String getBtnDelLable()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnDelLable);
	}
	/**
	 保存标签
	 */
	public final String getBtnSaveLable()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnSaveLable);
	}
	/**
	 提交标签
	 */
	public final String getBtnSubmitLable()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnSubmitLable);
	}
	/**
	 查询标签
	 */
	public final String getBtnSearchLabel()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnSearchLabel);
	}
	/**
	 数据快照
	 */
	public final String getBtnDataVer()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnDataVer);
	}
	/**
	 分组按钮
	 */
	public final String getBtnGroupEnable()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnGroupEnable);
	}
	public final String getBtnGroupLabel()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnGroupLabel);
	}
	/**
	 打印HTML按钮
	 */
	public final String getBtnPrintHtmlEnable()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnPrintHtmlEnable);
	}
	public final String getBtnPrintHtml()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnPrintHtml);
	}
	/**
	 打印PDF按钮
	 */
	public final String getBtnPrintPDFEnable()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnPrintPDFEnable);
	}
	public final String getBtnPrintPDF()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnPrintPDF);
	}
	/**
	 打印RTF按钮
	 */
	public final String getBtnPrintRTFEnable()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnPrintRTFEnable);
	}
	public final String getBtnPrintRTF()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnPrintRTF);
	}
	/**
	 打印CCWord按钮
	 */
	public final String getBtnPrintCCWordEnable()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnPrintCCWordEnable);
	}
	public final String getBtnPrintCCWord()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnPrintCCWord);
	}

	/**
	 打印ZIP按钮
	 */
	public final String getBtnExpZipEnable()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnExpZipEnable);
	}
	public final String getBtnExpZip()  {
		return this.GetValStrByKey(FrmEntityNoNameAttr.BtnExpZip);
	}
		///#endregion


		///#region 构造方法
	/**
	 单据属性
	*/
	public FrmEntityNoName()
	{
	}
	/**
	 单据属性

	 @param no 映射编号
	*/
	public FrmEntityNoName(String no) throws Exception
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
		map.setCodeStruct("4");

		// #region 基本属性.
		map.AddGroupAttr("基本信息");
		map.AddTBStringPK(MapDataAttr.No, null, "表单编号", true, true, 1, 190, 20);
		map.SetHelperAlert(MapDataAttr.No, "也叫表单ID,系统唯一.");
		map.AddTBString(MapDataAttr.PTable, null, "存储表", true, false, 0, 500, 20, true);
		map.SetHelperAlert(MapDataAttr.PTable, "存储的表名,如果您修改一个不存在的系统将会自动创建一个表.");

		map.AddTBString(MapDataAttr.Name, null, "表单名称", true, false, 0, 200, 20, true);
		map.AddDDLEntities(MapDataAttr.FK_FormTree, "01", "表单类别", new SysFormTrees(), false);
 		//#endregion 基本属性.


 		//#region 实体表单.
		map.AddGroupAttr("实体表单");
		map.AddTBInt("EntityType", 0, "实体类型", false, false);

		map.AddTBString(FrmEntityNoNameAttr.BillNoFormat, null, "实体编号规则", true, false, 0, 100, 20, true);
		map.AddTBString(FrmBillAttr.SortColumns, null, "排序字段", true, false, 0, 100, 20, true);
		map.AddTBString(FrmBillAttr.ColorSet, null, "表格列颜色设置", true, false, 0, 100, 20, true);
 		//#endregion 实体表单.

 		//#region 设计者信息.
		map.AddGroupAttr("设计者信息");

		map.AddTBString(MapDataAttr.Designer, null, "设计者", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerContact, null, "联系方式", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerUnit, null, "单位", true, false, 0, 500, 20, true);
		map.AddTBString(MapDataAttr.GUID, null, "GUID", true, true, 0, 128, 20, false);
		map.AddTBString(MapDataAttr.Ver, null, "版本号", true, true, 0, 30, 20);
		map.AddTBStringDoc(MapDataAttr.Note, null, "备注", true, false, true);
		map.AddTBInt(MapDataAttr.Idx, 100, "顺序号", false, false);
 		//#endregion 设计者信息.

		this.set_enMap(map);
		return this.get_enMap();
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
		super.afterInsertUpdateAction();
	}
	/// <summary>
	/// 检查enittyNoName类型的实体
	/// </summary>
	public void CheckEnityTypeAttrsFor_EntityNoName() throws Exception {
		//取出来全部的属性.
		MapAttrs attrs = new MapAttrs(this.getNo());

		//创建表单ID。
		int groupID = DBAccess.RunSQLReturnValInt("SELECT MAX(GroupID) FROM Sys_MapAttr WHERE FK_MapData='" + this.getNo() + "'", 0);
		if (groupID == 0)
		{
			GroupField gf = new GroupField();
			gf.setLab( "基本信息");
			gf.setFrmID(this.getNo());
			gf.Insert();
			groupID = (int) gf.getOID();
		}

		int idx = 0;

    // #region 补充上流程字段到 NDxxxRpt.
		if (attrs.contains(this.getNo() + "_No") == false)
		{
			/* 单据编号 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(this.getNo());
			attr.setEditType(EditType.UnDel);
			attr.setKeyOfEn("No");
			attr.setName("编号"); //  单据编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(idx++);
			attr.setGroupID(groupID);
			attr.Insert();
		}

		if (attrs.contains(this.getNo() + "_Name") == false)
		{
			/* 名称 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(this.getNo());
			attr.setEditType(EditType.UnDel);
			attr.setKeyOfEn("Name"); // "FlowEmps";
			attr.setName("名称"); //   单据模式， ccform的模式.
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(true);
			attr.setUIIsLine(false);
			attr.setMinLen(0);
			attr.setMaxLen(400);
			attr.setIdx(-idx++);
			attr.setGroupID(groupID);
			attr.Insert();
		}
		if (attrs.contains(this.getNo() + "_EntityState") == false)
		{
			/* 单据状态 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(this.getNo());
			attr.setEditType(EditType.UnDel);
			attr.setKeyOfEn("EntityState"); // "FlowEmps";
			attr.setName("实体状态"); //
			attr.setMyDataType(DataType.AppInt);
			attr.setUIContralType(UIContralType.TB);
			attr.setUIContralType(UIContralType.TB);

			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(true);
			attr.setMinLen(0);
			attr.setMaxLen(10);
			attr.setIdx(-idx++);
			attr.setGroupID(groupID);
			attr.Insert();
		}

		if (attrs.contains(this.getNo() + "_RecNo") == false)
		{
			/* 发起人 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(this.getNo());
			attr.setEditType(EditType.UnDel);
			attr.setKeyOfEn("RecNo");
			attr.setName("创建人"); //
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-idx++);
			attr.setGroupID(groupID);
			attr.Insert();
		}
		if (attrs.contains(this.getNo() + "_RecName") == false)
		{
			/* 创建人名称 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(this.getNo());
			attr.setEditType(EditType.UnDel);
			attr.setKeyOfEn("RecName");
			attr.setName("创建人名称"); //
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(32);
			attr.setIdx(idx++);
			attr.setGroupID(groupID);
			attr.Insert();
		}

		if (attrs.contains(this.getNo() + "_RDT") == false)
		{
			MapAttr attr = new MapAttr();
			attr.setFrmID(this.getNo());
			attr.setEditType(EditType.UnDel);
			attr.setKeyOfEn("RDT");
			attr.setName("创建时间");
			attr.setMyDataType(DataType.AppDateTime);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(false);
			attr.setIdx(idx++);
			attr.setGroupID(groupID);
			attr.Insert();
		}
		if (attrs.contains(this.getNo() + "_DeptNo") == false)
		{
			/* 创建人部门 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(this.getNo());
			attr.setEditType(EditType.UnDel);
			attr.setKeyOfEn("DeptNo");
			attr.setName("创建人部门"); //
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-idx++);
			attr.setGroupID(groupID);
			attr.Insert();
		}
		if (attrs.contains(this.getNo() + "_OrgNo") == false)
		{
			/* 创建人名称 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(this.getNo());
			attr.setEditType(EditType.UnDel);
			attr.setKeyOfEn("OrgNo");
			attr.setName("隶属组织"); //
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(idx++);
			attr.setGroupID(groupID);
			attr.Insert();
		}
		if (attrs.contains(this.getNo() + "_" + GERptAttr.AtPara) == false)
		{
			/* 参数 */
			MapAttr attr = new MapAttr();
			attr.setFrmID(this.getNo());
			attr.setEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.AtPara);
			attr.setName("参数"); // 单据编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(false);
			attr.setMinLen(0);
			attr.setMaxLen(4000);
			attr.setIdx(idx++);
			attr.setGroupID(groupID);
			attr.Insert();
		}
    // #endregion 补充上流程字段。

    // #region 注册到外键表.
		SFTable sf = new SFTable();
		sf.SetValByKey("No", this.getNo());
		if (sf.getIsExits() == false)
		{
			sf.setName(this.getName());
			sf.setSrcType(DictSrcType.SQL);
			sf.setSrcTable(this.getPTable());
			sf.setColumnValue("No");
			sf.setColumnText("Name");
			sf.setSelectStatement("SELECT No, Name FROM  " + this.getPTable());
			sf.SetPara("EnName", "TS.FrmUI.SFTableSQLNoName");

			sf.Insert();
		}
     //#endregion 注册到外键表
	}
}
