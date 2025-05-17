package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import bp.web.*;
import bp.difference.*;

/** 
 EnCfgs
*/
public class EnCfg extends EntityNoName
{

		///#region UI设置.
	public final String getUI()  {
		return this.GetValStringByKey(EnCfgAttr.UI);
	}
	public final void setUI(String value){
		this.SetValByKey(EnCfgAttr.UI, value);
	}

		///#endregion UI设置.


		///#region 基本属性
	/** 
	 数据分析方式
	*/
	public final String getDatan()  {
		return this.GetValStringByKey(EnCfgAttr.Datan);
	}
	public final void setDatan(String value){
		this.SetValByKey(EnCfgAttr.Datan, value);
	}
	/** 
	 附件路径
	*/
	public final String getFJSavePath() throws Exception {
		String str = this.GetValStringByKey(EnCfgAttr.FJSavePath);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			return SystemConfig.getPathOfDataUser() + this.getNo() + "/";
		}
		return str;
	}
	public final void setFJSavePath(String value){
		this.SetValByKey(EnCfgAttr.FJSavePath, value);
	}
	/** 
	 附件存储位置.
	*/
	public final String getFJWebPath() throws Exception {
		String str = this.GetValStringByKey(EnCfgAttr.FJWebPath);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = bp.sys.base.Glo.getRequest().getRemoteHost() + "/DataUser/" + this.getNo() + "/";
		}
		str = str.replace("\\", "/");
		if (!str.endsWith("/"))
		{
			str += "/";
		}
		return str;
	}
	public final void setFJWebPath(String value){
		this.SetValByKey(EnCfgAttr.FJWebPath, value);
	}

		///#endregion


		///#region 参数属性.
	/** 
	 批处理-设置页面大小
	*/
	public final int getPageSizeOfBatch() {
		return this.GetParaInt("PageSizeOfBatch", 600);
	}
	public final void setPageSizeOfBatch(int value)  {
		this.SetPara("PageSizeOfBatch", value);
	}
	/** 
	 批处理-设置页面大小
	*/
	public final int getPageSizeOfSearch() {
		return this.GetParaInt("PageSizeOfSearch", 15);
	}
	public final void setPageSizeOfSearch(int value)  {
		this.SetPara("PageSizeOfSearch", value);
	}

	public final String getFieldSet()  {
		return this.GetValStringByKey(EnCfgAttr.FieldSet);
	}
	public final void setFieldSet(String value){
		this.SetValByKey(EnCfgAttr.FieldSet, value);
	}

		///#endregion 参数属性.


		///#region 构造方法
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsDelete = false;
		if (WebUser.getIsAdmin()  == true)
		{
			uac.IsInsert = false;
			uac.IsDelete = false;
			uac.IsUpdate = true;
		}
		else
		{
			uac.Readonly();
			uac.IsView = false;
		}
		return uac;
	}
	/** 
	 系统实体
	*/
	public EnCfg()
	{
	}
	/** 
	 系统实体
	 
	 @param enName
	*/
	public EnCfg(String enName) throws Exception {
		this.setNo(enName);
		this.RetrieveFromDBSources();
	}
	@Override
	public int Retrieve() throws Exception {
		return super.RetrieveFromDBSources();
	}
	/** 
	 map
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("Sys_EnCfg", "实体配置");
		map.AddGroupAttr("基础信息");
		map.AddTBStringPK(EnCfgAttr.No, null, "实体名称", true, false, 1, 100, 60);


			///#region 基本信息设置.
		//String cfg1 = "@0=En.htm 实体与实体相关功能编辑器";
		map.AddDDLSysEnum("UIRowStyleGlo", 0, "表格数据行风格(应用全局)", true, true);
		map.AddBoolean("IsEnableDouclickGlo", true, "是否启动双击打开(应用全局)?", true, true);

		map.AddBoolean("IsEnableFocusField", false, "是否启用焦点字段?", true, true);
		map.AddTBString("FocusField", null, "焦点字段", true, false, 0, 30, 60, true);
		map.SetHelperAlert("FocusField", "用于显示点击打开的列，比如:Name, Title列.");

		map.AddBoolean("IsEnableRefFunc", false, "是否启用相关功能列?", true, true);
		map.AddBoolean("IsEnableOpenICON", false, "是否启用打开图标?", true, true);


		map.AddDDLSysEnum("PosRefFuncMobile", 0, "(移动端)相关功能显示位置", true, true, "PosRefFuncMobile", "@0=不显示@1=显示底部@2=显示左");
		map.SetHelperAlert("PosRefFuncMobile", "移动端列表页，相关功能显示的位置?");

		//数据加密存储.
		map.AddBoolean(EnCfgAttr.IsJM, false, "是否是加密存储?", true, true);
		map.AddBoolean(EnCfgAttr.IsSelectMore, false, "是否下拉查询条件多选?", true, true);

		map.AddDDLSysEnum("MoveToShowWay", 0, "移动到显示方式", true, true);

		map.AddDDLSysEnum("TableCol", 0, "实体表单显示列数", true, true, "实体表单显示列数", "@0=4列@1=6列");
		map.AddBoolean("IsShowIcon", false, "是否显示项目图标", true, true);
		map.AddTBString("KeyLabel", null, "关键字Label", true, false, 0, 30, 60, true);
		map.SetHelperAlert("KeyLabel", "(默认为:关键字:)");
		map.AddTBString("KeyPlaceholder", null, "关键字提示", true, false, 0, 300, 60, true);

		map.AddTBInt("PageSize", 10, "页面显示的条数", true, true);
		map.SetHelperAlert("PageSize", "(默认:10)");

		map.AddTBInt("FontSize", 14, "页面字体大小", true, true);
		map.SetHelperAlert("FontSize", "(默认:14px)");
		map.AddDDLSysEnum("EditerType", 0, "大块文本编辑器", true, true);

		map.AddBoolean("IsCond", false, "退出后是否清空查询条件?", true, true);
		map.SetHelperAlert("IsCond", "在查询组件中，是不是每次进入前都要清空以前的查询条件？默认不清空。");

			///#endregion 基本信息设置.


			///#region 查询排序.
		map.AddGroupAttr("查询排序");
		map.AddTBString("OrderBy", null, "查询排序字段", true, false, 0, 100, 60);
		map.AddBoolean("IsDeSc", true, "是否降序排序?", true, true);

			///#endregion 查询排序.


			///#region 附件保存路径.
		map.AddTBString(EnCfgAttr.FJSavePath, null, "附加保存路径", true, false, 0, 100, 60);
		map.AddTBString(EnCfgAttr.FJWebPath, null, "附件Web路径", true, false, 0, 100, 60);

			///#endregion 附件保存路径.


			///#region 其他高级设置.
		map.AddTBString(EnCfgAttr.Datan, null, "字段数据分析方式", true, false, 0, 200, 60);
		map.AddTBString(EnCfgAttr.UI, null, "UI设置", true, false, 0, 2000, 60);

		//字段颜色设置.
		map.AddTBString(EnCfgAttr.ColorSet, null, "颜色设置", true, false, 0, 500, 60, true);
		String msg = "对字段的颜色处理";
		msg += "\t\n@Age:From=0,To=18,Color=green;From=19,To=30,Color=red";
		map.SetHelperAlert(EnCfgAttr.ColorSet, msg);
		//对字段求总和平均.
		map.AddTBString(EnCfgAttr.FieldSet, null, "字段求和/平均设置", true, false, 0, 500, 60, true);

		//字段格式化函数.
		map.AddTBString("ForamtFunc", null, "字段格式化函数", true, false, 0, 200, 60, true);
		msg = "对字段的显示使用函数进行处理";
		msg += "\t\n 1. 对于字段内容需要处理后在输出出来.";
		msg += "\t\n 2. 比如：原字段内容 @zhangsa,张三@lisi,李四 显示的内容为 张三,李四";
		msg += "\t\n 3. 配置格式: 字段名@函数名; 比如:  FlowEmps@DealFlowEmps; ";
		msg += "\t\n 4. 函数写入到 \\DataUser\\JSLibData\\SearchSelf.js";
		map.SetHelperAlert("ForamtFunc", msg);


		map.AddTBString(EnCfgAttr.Drill, null, "数据钻取", true, false, 0, 200, 60, true);
		msg = "显示钻取链接的字段";
		msg += "\t\n 格式: @Age@JinE@ShouYi";
		msg += "\t\n 显示在Search, En组件里面.";
		map.SetHelperAlert(EnCfgAttr.Drill, msg);

		map.AddDDLSysEnum(EnCfgAttr.MobileFieldShowModel, 0, "移动端列表字段显示方式", true, true, EnCfgAttr.MobileFieldShowModel, "@0=默认设置@1=设置显示字段@2=设置模板");


		map.AddTBStringDoc(EnCfgAttr.MobileShowContent, EnCfgAttr.MobileShowContent, null, "移动端列表字段设置", true, false, 0, 500, 10, true);

		String help = "格式1: Key1,Key2, 格式2: @Key1@Key2@Key3@";
		map.SetHelperAlert(EnCfgAttr.MobileShowContent, help);

			///#endregion 其他高级设置.


			///#region  Search.工具栏按钮.
		map.AddGroupAttr("工具栏按钮");
		map.AddBoolean("BtnsShowLeft", false, "按钮显示到左边?", true, true, false);
		msg = "配置的按钮显示位置.";
		msg += "\t\n1.默认配置的按钮显示在右边位置. ";
		msg += "\t\n1.这些按钮包括自定义按钮，新建，导入，导出，分组。";
		map.SetHelperAlert("BtnsShowLeft", msg);

		//导入功能.
		map.AddBoolean("IsImp", true, "是否显示导入?", true, true, false);
		map.AddTBString(EnCfgAttr.ImpFuncUrl, null, "导入功能Url", true, false, 0, 500, 60, true);
		map.SetHelperAlert(EnCfgAttr.ImpFuncUrl, "如果为空，则使用通用的导入功能.");

		map.AddBoolean("IsExp", false, "是否显示导出", true, true, true);
		map.AddBoolean(EnCfgAttr.IsGroup, true, "是否显示分析按钮（在查询工具栏里）?", true, true, true);
		map.AddBoolean("IsEnableLazyload", true, "是否启用懒加载？（对树结构实体有效）?", true, true, true);

		map.AddTBString("BtnLab1", null, "集合:自定义按钮标签1", true, false, 0, 70, 60, false);
		map.SetHelperAlert("BtnLab1", "自定义按钮与标签,函数可以写入到/DataUser/JSLabData/SearchSelf.js里面.");
		map.AddTBString("BtnJS1", null, "集合:Url/Javasccript", true, false, 0, 300, 60, false);

		map.AddTBString("BtnLab2", null, "集合:自定义按钮标签2", true, false, 0, 70, 60, false);
		map.AddTBString("BtnJS2", null, "集合:Url/Javasccript", true, false, 0, 300, 60, false);

		map.AddTBString("BtnLab3", null, "集合:自定义按钮标签3", true, false, 0, 70, 60, false);
		map.AddTBString("BtnJS3", null, "集合:Url/Javasccript", true, false, 0, 300, 60, false);

			///#endregion 按钮配置信息 - 自定义按钮.


			///#region  EnOnly.按钮配置信息.

		map.AddTBString("EnBtnLab1", null, "实体:自定义按钮标签1", true, false, 0, 70, 60, false);
		map.SetHelperAlert("EnBtnLab1", "实体:自定义按钮与标签,函数可以写入到/DataUser/JSLabData/SearchSelf.js里面.");
		map.AddTBString("EnBtnJS1", null, "实体:Url/Javasccript", true, false, 0, 300, 60, false);

		map.AddTBString("EnBtnLab2", null, "实体:自定义按钮标签2", true, false, 0, 70, 60, false);
		map.AddTBString("EnBtnJS2", null, "实体:Url/Javasccript", true, false, 0, 300, 60, false);

			///#endregion 按钮配置信息 - 自定义按钮.


			///#region 双击/单击行的配置.
		map.AddGroupAttr("双击/单击行的配置");
		String cfg = "@0=En.htm 实体与实体相关功能编辑器";
		cfg += "@1=EnOnly.htm 实体编辑器";
		cfg += "@2=/CCForm/FrmGener.htm 经典表单解析器";
		cfg += "@3=/CCForm/FrmGener.htm 自由表单解析器";
		cfg += "@9=自定义URL";
		map.AddDDLSysEnum("SearchUrlOpenType", 0, "双击/单击行打开内容", true, true, "SearchUrlOpenType", cfg);
		map.AddBoolean("IsRefreshParentPage", true, "关闭后是否刷新本页面", true, true);

		map.AddTBString(EnCfgAttr.UrlExt, null, "要打开的Url", true, false, 0, 500, 60, true);
		map.AddDDLSysEnum("DoubleOrClickModel", 0, "双击/单击行弹窗模式", true, true, "DoubleOrClickModel", "@0=双击行弹窗@1=单击行弹窗");
		map.AddDDLSysEnum("OpenModel", 0, "打开方式", true, true, "OpenModel", "@0=弹窗-强制关闭@1=新窗口打开-winopen模式@2=弹窗-非强制关闭@3=执行指定的方法.@4=流程设计器打开模式");
		map.AddTBString("OpenModelFunc", null, "弹窗方法", true, false, 0, 300, 60, false);

		msg = "首先在写一个函数，放入到:/DataUser/JSLab/SearchSelf.js里面 ";
		msg += "\t\n 该函数里 OpenIt(传入一个已经计算好的url);";
		msg += "\t\n 比如您写一个方法: OpenItMyUrl(url);";
		map.SetHelperAlert("OpenModelFunc", msg);

		// map.AddTBInt("WinCardW", 1000, "窗体宽度", false, false);
		//  map.AddTBInt("WinCardH", 600, "高度", false, false);
		map.AddDDLSysEnum("WinCardW", 0, "宽度", true, true, "WinCardW", "@0=75%@1=50%@2=100%@3=25%");
		map.AddDDLSysEnum("WinCardH", 2, "高度", true, true, "WinCardH", "@0=75%@1=50%@2=100%@3=85%@4=25%");


			///#endregion

		map.AddTBAtParas(3000); //参数属性.



			///#region 执行的方法 - 基本功能
		map.AddGroupMethod("基本功能");
		RefMethod rm = new RefMethod();
		rm = new RefMethod();
		rm.Title = "设置显示的列";
		rm.ClassMethodName = this.toString() + ".SearchSettingCols()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "多表头设置";
		rm.ClassMethodName = this.toString() + ".MultiTitle()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "启用经典表单设计器";
		rm.ClassMethodName = this.toString() + ".DesignerFool()";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "导入数据";
		rm.ClassMethodName = this.toString() + ".ImpData()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "字段颜色范围设置";
		rm.ClassMethodName = this.toString() + ".FieldColors()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "字段求和/平均设置";
		rm.ClassMethodName = this.toString() + ".FieldAvgSum()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "清除设计内容";
		rm.ClassMethodName = this.toString() + ".ClearData()";
		rm.refMethodType = RefMethodType.Func;
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "移动端Search";
		rm.ClassMethodName = this.toString() + ".ToMSearch()";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		map.AddRefMethod(rm);


			///#endregion 执行的方法.


			///#region 执行的方法 - 数据库信息
		rm = new RefMethod();
		rm.Title = "表结构";
		rm.ClassMethodName = this.toString() + ".FieldDesc()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "字段UI";
		rm.ClassMethodName = this.toString() + ".FieldDescUI()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

			///#endregion 执行的方法.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	public final String FieldDescUI() {
		return "../../Comm/Sys/SystemClassFieldUI.htm?EnsName=" + this.getNo();
	}

	public final String FieldDesc() {
		return "../../Comm/Sys/SystemClassField.htm?EnsName=" + this.getNo();
	}

	public final String ToMSearch() {
		return "../../../CCMobile/Comm/Search.htm?EnsName=" + this.getNo();
	}

	/** 
	 字段颜色设置
	 
	 @return 
	*/
	public final String FieldColors() {
		return "../../Comm/Sys/FieldColors.htm?EnsName=" + this.getNo();
	}
	/** 
	 字段求和/平均设置
	 
	 @return 
	*/
	public final String FieldAvgSum() {
		return "../../Comm/Sys/FieldAvgSum.htm?EnsName=" + this.getNo();
	}
	/** 
	 清除数据.
	 
	 @return 
	*/
	public final String ClearData() throws Exception {
		MapData md = new MapData(this.getNo());
		md.setNo(this.getNo());
		if (md.RetrieveFromDBSources() == 0)
		{
			return "err@没有设计的数据要清除.";
		}
		md.Delete();


		return "清除成功.";
	}
	public final String ImpData() {
		return "../../Comm/Sys/ImpData.htm?EnsName=" + this.getNo();
	}
	public final String DesignerFool() {
		return "../../Admin/FoolFormDesigner/Designer.htm?FK_MapData=" + this.getNo();
	}

	public final String SearchSettingCols() {
		return "../../Comm/Sys/SearchSettingCols.htm?EnsName=" + this.getNo();
	}

	public final String SearchSetting() {
		return "../../Comm/Sys/SearchSetting.htm?EnsName=" + this.getNo();
	}
	/** 
	 多表头
	 
	 @return 
	*/
	public final String MultiTitle() {
		return "../../Comm/Sys/MultiTitle.htm?EnsName=" + this.getNo() + "&DoType=Search";
	}
	/** 
	 生成他的Attrs
	 
	 @return 
	*/
	public final String GenerAttrs() throws Exception {
		Entities ens = ClassFactory.GetEns(this.getNo());
		if (ens == null)
		{
			return "err@" + this.getNo() + ",类名错误.";
		}
		MapAttrs attrs = new MapAttrs();
		MapData md = new MapData();
		md.setNo(this.getNo());
		int count = md.RetrieveFromDBSources();
		if (count == 0)
		{
			attrs = ens.getNewEntity().getEnMap().getAttrs().ToMapAttrs();
		}
		else
		{
			attrs.Retrieve(MapAttrAttr.FK_MapData, this.getNo(), MapAttrAttr.Idx);
		}

		return attrs.ToJson();
	}
}
