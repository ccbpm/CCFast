package bp.ccbill.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.wf.template.*;
import bp.sys.*;
import bp.port.*;
import bp.ccbill.*;

/** 
 单据模版
*/
public class FrmTemplate extends EntityNoName
{

		///#region 权限控制.
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getNo().equals("admin") == true)
		{
			uac.IsDelete = false;
			uac.IsUpdate = true;
			return uac;
		}
		uac.Readonly();
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
		return EntityType.forValue(this.GetValIntByKey(FrmTemplateAttr.EntityType));
	}
	public final void setEntityType(EntityType value)  {
		this.SetValByKey(FrmTemplateAttr.EntityType, value.getValue());
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
		return this.GetValIntByKey(FrmTemplateAttr.BtnNewModel);
	}
	public final void setBtnNewModel(int value)  {
		this.SetValByKey(FrmTemplateAttr.BtnNewModel, value);
	}

	/** 
	 单据格式
	*/
	public final String getBillNoFormat() throws Exception {
		String str = this.GetValStrByKey(FrmTemplateAttr.BillNoFormat);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = "{LSH4}";
		}
		return str;
	}
	public final void setBillNoFormat(String value)  {
		this.SetValByKey(FrmTemplateAttr.BillNoFormat, value);
	}
	/** 
	 单据编号生成规则
	*/
	public final String getTitleRole() throws Exception {
		String str = this.GetValStrByKey(FrmTemplateAttr.TitleRole);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = "@WebUser.FK_DeptName @WebUser.Name @RDT";
		}
		return str;
	}
	public final void setTitleRole(String value)  {
		this.SetValByKey(FrmTemplateAttr.BillNoFormat, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 单据模版
	*/
	public FrmTemplate()
	{
	}
	/** 
	 单据模版
	 
	 @param no 映射编号
	*/
	public FrmTemplate(String no) throws Exception
	{
		super(no);
	}
	/**
	 * EnMap
	 */
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("Sys_MapData", "单据模版");

		map.setCodeStruct("4");


			///#region 基本属性.
		map.AddTBStringPK(MapDataAttr.No, null, "表单编号", true, true, 1, 190, 20);
		map.SetHelperAlert(MapDataAttr.No, "也叫表单ID,系统唯一.");

		map.AddDDLSysEnum(MapDataAttr.FrmType, 0, "表单类型", true, true, "BillFrmType", "@0=经典表单@1=自由表单");
		map.AddTBString(MapDataAttr.PTable, null, "存储表", true, false, 0, 500, 20, true);
		map.SetHelperAlert(MapDataAttr.PTable, "存储的表名,如果您修改一个不存在的系统将会自动创建一个表.");

		map.AddTBString(MapDataAttr.Name, null, "表单名称", true, false, 0, 200, 20, true);
		map.AddDDLEntities(MapDataAttr.FK_FormTree, "01", "表单类别", new SysFormTrees(), false);

		map.AddDDLSysEnum(FrmAttr.RowOpenModel, 0, "行记录打开模式", true, true, FrmAttr.RowOpenModel, "@0=新窗口打开@1=弹出窗口打开,关闭后刷新列表@2=弹出窗口打开,关闭后不刷新列表");

			///#endregion 基本属性.


			///#region 单据模版.
		map.AddDDLSysEnum(FrmTemplateAttr.EntityType, 0, "业务类型", true, false, FrmTemplateAttr.EntityType, "@0=独立表单@1=单据@2=编号名称实体@3=树结构实体");
		map.SetHelperAlert(FrmTemplateAttr.EntityType, "该实体的类型,@0=单据@1=编号名称实体@2=树结构实体.");

		map.AddDDLSysEnum(FrmAttr.EntityShowModel, 0, "展示模式", true, true, FrmAttr.EntityShowModel, "@0=表格@1=树干模式");

		map.AddTBString(FrmTemplateAttr.BillNoFormat, null, "实体编号规则", true, false, 0, 100, 20, true);
		map.SetHelperAlert(FrmTemplateAttr.BillNoFormat, "\t\n实体编号规则: \t\n 2标识:01,02,03等, 3标识:001,002,003,等..");

			///#endregion 单据模版.


			///#region 实体属性
		map.AddTBInt(FrmTemplateAttr.EntityEditModel, 0, "编辑模式", true, false);
		//map.AddDDLSysEnum(FrmAttr.EntityEditModel, 0, "编辑模式", true, true, FrmAttr.EntityEditModel, "@0=只读列表模式@1=Table编辑模式");

			///#endregion 实体属性.


			///#region 可以创建的权限.
		//平铺模式.
		map.getAttrsOfOneVSM().AddGroupPanelModel(new StationCreates(), new Stations(), StationCreateAttr.FrmID, StationCreateAttr.FK_Station, "可以创建的角色", StationAttr.FK_StationType, "Name", "No");

		map.getAttrsOfOneVSM().AddGroupListModel(new StationCreates(), new Stations(), StationCreateAttr.FrmID, StationCreateAttr.FK_Station, "可以创建的角色AddGroupListModel", StationAttr.FK_StationType, "Name", "No");

		//节点绑定部门. 节点绑定部门.
		map.getAttrsOfOneVSM().AddBranches(new FrmDeptCreates(), new Depts(), FrmDeptCreateAttr.FrmID, FrmDeptCreateAttr.FK_Dept, "可以创建的部门AddBranches", EmpAttr.Name, EmpAttr.No, "@WebUser.FK_Dept", null);

		//节点绑定人员. 使用树杆与叶子的模式绑定.
		map.getAttrsOfOneVSM().AddBranchesAndLeaf(new EmpCreates(), new Emps(), EmpCreateAttr.FrmID, EmpCreateAttr.FK_Emp, "可以创建的人员", EmpAttr.FK_Dept, EmpAttr.Name, EmpAttr.No, "@WebUser.FK_Dept", null);

			///#endregion 可以创建的权限

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

}
