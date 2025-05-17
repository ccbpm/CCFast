package bp.ccfast.ccmenu;

import bp.ccbill.template.Method;
import bp.en.Map;
import bp.sys.*;
import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.ccbill.template.*;
import bp.sys.CCFormAPI;
import bp.wf.*;

import java.io.*;

/**
 系统
*/
public class MySystem extends EntityNoName
{

		///#region 属性
	/**
	 打开方式
	*/
	public final String getOpenWay() throws Exception {
		int openWay = 0;

		switch (openWay)
		{
			case 0:
				return "_blank";
			case 1:
				return this.getNo();
			default:
				return "";
		}
	}
	/**
	 路径
	*/
	public final String getWebPath() {
		return this.GetValStringByKey("WebPath");
	}
	/**
	 是否启用
	*/
	public final boolean getItIsEnable() {
		return this.GetValBooleanByKey(MySystemAttr.IsEnable);
	}
	public final void setItIsEnable(boolean value)  {
		this.SetValByKey(MySystemAttr.IsEnable, value);
	}
	/**
	 顺序
	*/
	public final int getIdx() {
		return this.GetValIntByKey(MySystemAttr.Idx);
	}
	public final void setIdx(int value)  {
		this.SetValByKey(MySystemAttr.Idx, value);
	}
	/**
	 Icon
	*/
	public final String getIcon()  {
		return this.GetValStrByKey(MySystemAttr.Icon);
	}
	public final void setIcon(String value)  {
		this.SetValByKey(MySystemAttr.Icon, value);
	}


	public final String getOrgNo() {
		return this.GetValStringByKey(MySystemAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(MySystemAttr.OrgNo, value);
	}
	public final String getRefMenuNo() {
		return this.GetValStringByKey(MySystemAttr.RefMenuNo);
	}
	public final void setRefMenuNo(String value)  {
		this.SetValByKey(MySystemAttr.RefMenuNo, value);
	}

	/**
	 系统类型
	 */
	public final int getSystemType() {
		return this.GetValIntByKey(MySystemAttr.SystemType);
	}
	public final void setSystemType(int value)  {
		this.SetValByKey(MySystemAttr.SystemType, value);
	}

	/**
	 尾部内容
	 */
	public final String getDocs() {
		return this.GetValStringByKey(MySystemAttr.Docs);
	}
	public final void setDocs(String value)  {
		this.SetValByKey(MySystemAttr.Docs, value);
	}

	/**
	 待办
	 */
	public final int getTodolist_EmpWorks() {
		return this.GetValIntByKey(MySystemAttr.Todolist_EmpWorks);
	}
	public final void setTodolist_EmpWorks(int value)  {
		this.SetValByKey(MySystemAttr.Todolist_EmpWorks, value);
	}

	/**
	 草稿
	 */
	public final int getTodolist_Draft() {
		return this.GetValIntByKey(MySystemAttr.Todolist_Draft);
	}
	public final void setTodolist_Draft(int value)  {
		this.SetValByKey(MySystemAttr.Todolist_Draft, value);
	}

	/**
	 已完成
	 */
	public final int getTodolist_Complete() {
		return this.GetValIntByKey(MySystemAttr.Todolist_Complete);
	}
	public final void setTodolist_Complete(int value)  {
		this.SetValByKey(MySystemAttr.Todolist_Complete, value);
	}

	/**
	 退回
	 */
	public final int getTodolist_ReturnNum() {
		return this.GetValIntByKey(MySystemAttr.Todolist_ReturnNum);
	}
	public final void setTodolist_ReturnNum(int value)  {
		this.SetValByKey(MySystemAttr.Todolist_ReturnNum, value);
	}

	/**
	 抄送
	 */
	public final int getTodolist_CCWorks() {
		return this.GetValIntByKey(MySystemAttr.Todolist_CCWorks);
	}
	public final void setTodolist_CCWorks(int value)  {
		this.SetValByKey(MySystemAttr.Todolist_CCWorks, value);
	}

	/**
	 * 逾期
	 */
	public final int getTodolist_OverWorkNum() {
		return this.GetValIntByKey(MySystemAttr.Todolist_OverWorkNum);
	}
	public final void setTodolist_OverWorkNum(int value)  {
		this.SetValByKey(MySystemAttr.Todolist_OverWorkNum, value);
	}

	/**
	 未阅
	 */
	public final int getTodolist_UnRead() {
		return this.GetValIntByKey(MySystemAttr.Todolist_UnRead);
	}
	public final void setTodolist_UnRead(int value)  {
		this.SetValByKey(MySystemAttr.Todolist_UnRead, value);
	}

	/**
	 挂起
	 */
	public final int getTodolist_HungupNum() {
		return this.GetValIntByKey(MySystemAttr.Todolist_HungupNum);
	}
	public final void setTodolist_HungupNum(int value)  {
		this.SetValByKey(MySystemAttr.Todolist_HungupNum, value);
	}

		///#endregion


		///#region 按钮权限控制
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		uac.IsInsert = false;
		return uac;
	}

		///#endregion


		///#region 构造方法
	/**
	 系统
	*/
	public MySystem()
	{
	}
	/**
	 系统

	 @param no
	*/
	public MySystem(String no) throws Exception
	{
		this.setNo(no);
		this.Retrieve();
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
		Map map = new Map("GPM_System", "系统");

		map.AddTBStringPK(MySystemAttr.No, null, "编号", true, false, 2, 100, 100);
		map.AddTBString(MySystemAttr.Name, null, "名称", true, false, 0, 300, 150, true);
		map.AddBoolean(MySystemAttr.IsEnable, true, "启用?", true, true);
		map.AddTBString(MySystemAttr.Icon, null, "图标", true, false, 0, 50, 150, true);

		map.AddTBString(MenuAttr.OrgNo, null, "组织编号", true, false, 0, 50, 20);
		map.AddTBInt(MySystemAttr.Idx, 0, "顺序", true, false);

		map.AddTBInt(MySystemAttr.SystemType, 0, "系统类型", true, false);
		map.AddTBString(MySystemAttr.Docs, null, "尾部内容", true, false, 0, 50, 20);
		map.AddTBInt(MySystemAttr.Todolist_EmpWorks, 0, "待办", true, false);
		map.AddTBInt(MySystemAttr.Todolist_Draft, 0, "草稿", true, false);
		map.AddTBInt(MySystemAttr.Todolist_Complete, 0, "已完成", true, false);
		map.AddTBInt(MySystemAttr.Todolist_ReturnNum, 0, "退回", true, false);
		map.AddTBInt(MySystemAttr.Todolist_CCWorks, 0, "抄送", true, false);
		map.AddTBInt(MySystemAttr.Todolist_OverWorkNum, 0, "逾期", true, false);
		map.AddTBInt(MySystemAttr.Todolist_UnRead, 0, "未阅", true, false);
		map.AddTBInt(MySystemAttr.Todolist_HungupNum, 0, "挂起", true, false);
		//@0=不共享@1=按照系统权限设置
		map.AddTBInt("ShareOrgSln", 0, "组织共享方案", true, false);
		map.AddTBString(MySystemAttr.FK_Stations, null, "角色ids", false, true, 0, 200, 30, true);
		map.AddTBString("AtPara", null, "AtPara", false, true, 0, 200, 30, true);

		RefMethod rm = new RefMethod();
		rm.Title = "导出应用模板";
		rm.ClassMethodName = this.toString() + ".DoExpAppModel";
		//rm.refMethodType = RefMethodType.LinkeWinOpen;
		map.AddRefMethod(rm);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	/**
	 导出

	 @return
	*/
	public final String DoExp() throws Exception {
		String path = bp.difference.SystemConfig.getPathOfWebApp() + "CCFast/SystemTemplete/" + this.getName() + "/";
		if ((new File(path)).isDirectory() == false)
		{
			(new File(path)).mkdirs();
		}

		//系统属性.
		DataSet ds = new DataSet();
		ds.Tables.add(this.ToDataTableField("MySystem"));

		//模块.
		Modules ens = new Modules();
		ens.Retrieve(ModuleAttr.SystemNo, this.getNo(), null);
		ds.Tables.add(ens.ToDataTableField("Modules"));

		//菜单.
		Menus menus = new Menus();
		menus.Retrieve(MenuAttr.SystemNo, this.getNo(), null);
		ds.Tables.add(menus.ToDataTableField("Menus"));

		String file = path + "Menus.xml"; //默认的页面.
		ds.WriteXml(file, XmlWriteMode.WriteSchema, ds);

		//遍历菜单.
		for (Menu en : menus.ToJavaList())
		{
			////常规的功能，不需要备份.
			//if (en.Mark.equals("WorkRec") == true
			//    || en.Mark.equals("Calendar") == true
			//    || en.Mark.equals("Notepad") == true)
			//    continue;
			switch (en.getMenuModel())
			{
				case "WorkRec":
				case "Calendar":
				case "Notepad":
				case "Task":
				case "KnowledgeManagement":
					break;
				case "Dict": //如果是实体.
					Dict(en, path);
					break;
				case "DictTable": //如果是字典.
					DictTable(en, path);
					break;
				default:
					//    throw new Exception("err@没有判断的应用类型:" + en.Mark);
					break;
			}
		}

		return "执行成功. 导出到：" + path;
	}
	public final String DictTable(Menu en, String path) throws Exception {
		DataSet ds = new DataSet();

		SFTable sf = new SFTable(en.getUrlExt());

		ds.Tables.add(sf.ToDataTableField("SFTable"));

		DataTable dt = sf.GenerHisDataTable(null);
		dt.setTableName("Data");
		ds.Tables.add(dt);

		ds.WriteXml(path + en.getUrlExt() + ".xml", XmlWriteMode.WriteSchema, ds);
		return "";
	}
	/**
	 导出字典.

	 @return
	*/
	public final String Dict(Menu en, String path) throws Exception {
		//获得表单的ID.
		String frmID = en.getUrlExt();

		DataSet ds = CCFormAPI.GenerHisDataSet_AllEleInfo(frmID);
		String file = path + "/" + frmID + ".xml"; //实体方法.
		ds.WriteXml(file, XmlWriteMode.WriteSchema, ds);


			///#region 导出实体的方法 .
		//获得方法分组
		GroupMethods ensGroup = new GroupMethods();
		ensGroup.Retrieve(MethodAttr.FrmID, frmID, null);

		//获得方法.
		Methods ens = new Methods();
		ens.Retrieve(MethodAttr.FrmID, frmID, null);

		//保存方法.
		ds = new DataSet();
		ds.Tables.add(ensGroup.ToDataTableField("GroupMethods"));
		ds.Tables.add(ens.ToDataTableField("Methods"));

		file = path + frmID + "_GroupMethods.xml"; //实体方法.
		ds.WriteXml(file, XmlWriteMode.WriteSchema, ds);

		//循环单实体方法集合.
		for (Method method : ens.ToJavaList())
		{
			switch (method.getMethodModel())
			{
				case "FlowEtc": //流程
					Flow f2l1 = new Flow(method.getMethodID());
					f2l1.DoExpFlowXmlTemplete(path + method.getMethodID() + "_Flow");
					break;
				case "FlowBaseData": //流程
					Flow fl1 = new Flow(method.getMethodID());
					fl1.DoExpFlowXmlTemplete(path + method.getMethodID() + "_Flow");
					break;
				case "Func": //功能导出？
					break;
				default:
					break;
			}
		}

			///#endregion 导出实体的方法 .


			///#region 导出集合 .
		//获得方法分组
		bp.ccbill.template.Collections ensCollts = new Collections();
		ensCollts.Retrieve(CollectionAttr.FrmID, frmID, null);

		//保存方法.
		ds = new DataSet();
		ds.Tables.add(ensCollts.ToDataTableField("Collections"));

		file = path + "/" + frmID + "_Collections.xml"; //实体方法.
		ds.WriteXml(file, XmlWriteMode.WriteSchema, ds);

		//循环单实体方法集合.
		for (Collection method : ensCollts.ToJavaList())
		{
			switch (method.getMethodModel())
			{
				case "FlowEntityBatchStart": //流程
					Flow fC1 = new Flow(method.getFlowNo());
					fC1.DoExpFlowXmlTemplete(path + method.getFlowNo() + "_Flow");
					break;
				case "FlowNewEntity": //流程
					Flow fc2 = new Flow(method.getFlowNo());
					fc2.DoExpFlowXmlTemplete(path + method.getFlowNo() + "_Flow");
					break;
				default:
					break;
			}
		}

			///#endregion 导出实体的方法 .

		return "实体导出成功";
	}
	/**
	 导出应用模板

	 @return
	*/
	public final String DoExpAppModel() {
		return "../../GPM/PowerCenter.htm?CtrlObj=System&CtrlPKVal=" + this.getNo() + "&CtrlGroup=System";
	}

	/**
	 业务处理.

	 @return
	*/
	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getNo()) == true)
		{
			this.setNo(DBAccess.GenerGUID(10, null, null));
		}

		this.setOrgNo(bp.web.WebUser.getOrgNo());
		return super.beforeInsert();
	}

	@Override
	protected boolean beforeDelete() throws Exception
	{
		Modules ens = new Modules();
		ens.Retrieve(ModuleAttr.SystemNo, this.getNo(), null);
		if (ens.size() != 0)
		{
			throw new RuntimeException("err@该系统下有子模块，您不能删除。");
		}

		//看看这个类别下是否有表单，如果有就删除掉.
		String sql = "SELECT COUNT(No) AS No FROM Sys_MapData WHERE FK_FormTree='" + this.getNo() + "'";
		if (DBAccess.RunSQLReturnValInt(sql) == 0)
		{
			DBAccess.RunSQL("DELETE FROM Sys_FormTree WHERE No='" + this.getNo() + "' ");
		}

		//看看这个类别下是否有流程，如果有就删除掉.
		sql = "SELECT COUNT(No) AS No FROM WF_Flow WHERE FK_FlowSort='" + this.getNo() + "'";
		if (DBAccess.RunSQLReturnValInt(sql) == 0)
		{
			DBAccess.RunSQL("DELETE FROM WF_FlowSort WHERE No='" + this.getNo() + "' ");
		}

		return super.beforeDelete();
	}


		///#region 移动方法.
	/**
	 向上移动
	*/
	public final void DoUp() throws Exception {
		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.DoOrderUp(MySystemAttr.OrgNo, this.getOrgNo(), MySystemAttr.Idx);
		}
		else
		{
			this.DoOrderUp(MySystemAttr.Idx);
		}
	}
	/**
	 向下移动
	*/
	public final void DoDown() throws Exception
	{
		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.DoOrderDown(MySystemAttr.OrgNo, this.getOrgNo(), MySystemAttr.Idx);
		}
		else
		{
			this.DoOrderDown(MySystemAttr.Idx);
		}
	}

		///#endregion 移动方法.

}
