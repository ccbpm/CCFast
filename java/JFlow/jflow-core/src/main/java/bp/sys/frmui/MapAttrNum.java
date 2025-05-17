package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;
import java.time.*;
import java.math.*;

/**
 数值字段
*/
public class MapAttrNum extends EntityMyPK
{

		///#region 属性.
	/**
	 默认值
	*/
	public final String getDefVal() throws Exception {
		String str = this.GetValStrByKey(MapAttrAttr.DefVal);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			return "0";
		}
		return str;
	}
	public final void setDefVal(String value){
		this.SetValByKey(MapAttrAttr.DefVal, value);
	}
	public final int getDefValType()  {
		return this.GetValIntByKey(MapAttrAttr.DefValType);
	}
	public final void setDefValType(int value){
		this.SetValByKey(MapAttrAttr.DefValType, value);
	}
	/**
	 表单ID
	*/
	public final String getFrmID()  {
		return this.GetValStringByKey(MapAttrAttr.FK_MapData);
	}
	public final void setFrmID(String value){
		this.SetValByKey(MapAttrAttr.FK_MapData, value);
	}
	/**
	 字段
	*/
	public final String getKeyOfEn()  {
		return this.GetValStringByKey(MapAttrAttr.KeyOfEn);
	}
	public final void setKeyOfEn(String value){
		this.SetValByKey(MapAttrAttr.KeyOfEn, value);
	}
	/**
	 绑定的枚举ID
	*/
	public final String getUIBindKey()  {
		return this.GetValStringByKey(MapAttrAttr.UIBindKey);
	}
	public final void setUIBindKey(String value){
		this.SetValByKey(MapAttrAttr.UIBindKey, value);
	}
	/**
	 数据类型
	*/
	public final int getMyDataType()  {
		return this.GetValIntByKey(MapAttrAttr.MyDataType);
	}
	public final void setMyDataType(int value){
		this.SetValByKey(MapAttrAttr.MyDataType, value);
	}

		///#endregion


		///#region 构造方法
	/**
	 控制权限
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsInsert = false;
		uac.IsUpdate = true;
		uac.IsDelete = true;
		return uac;
	}
	/**
	 数值字段
	*/
	public MapAttrNum()
	{
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

		Map map = new Map("Sys_MapAttr", "数值字段");
		map.IndexField = MapAttrAttr.FK_MapData;



			///#region 基本信息.
		map.AddGroupAttr("基本属性");
		map.AddMyPK();
		map.AddTBString(MapAttrAttr.FK_MapData, null, "实体标识", false, false, 1, 100, 20);
		map.AddTBString(MapAttrAttr.Name, null, "字段中文名", true, false, 0, 200, 20);
		map.AddTBString(MapAttrAttr.KeyOfEn, null, "字段名", true, true, 1, 200, 20);

		map.AddDDLSysEnum(MapAttrAttr.MyDataType, 2, "数据类型", true, false);

		map.AddTBString(MapAttrAttr.DefVal, MapAttrAttr.DefaultVal, "默认值/小数位数", true, false, 0, 200, 20);

		map.AddDDLSysEnum(MapAttrAttr.DefValType, 1, "默认值选择方式", true, true, "DefValType", "@0=默认值为空@1=按照设置的默认值设置", false);
		String help = "给该字段设置默认值:\t\r";

		help += "\t\r 1. 如果是整形就设置一个整形的数字作为默认值.";
		help += "\t\r 2. 对于float,decimal数据类型，如果设置0.0000就是标识要保留4位小数,如果是1.0000 标识保留4位小数,默认值为1.";
		map.SetHelperAlert("DefVal", help);

		map.AddBoolean(MapAttrAttr.UIVisible, true, "是否可见？", true, true);
		map.AddBoolean(MapAttrAttr.UIIsEnable, true, "是否可编辑？", true, true);
		map.AddBoolean(MapAttrAttr.UIIsInput, false, "是否必填项？", true, true);
		//map.AddBoolean(MapAttrAttr.IsSecret, false, "是否保密？", true, true);
		map.AddBoolean("ExtIsSum", false, "是否显示合计(对从表有效)", true, true);
		map.SetHelperAlert("ExtIsSum", "如果是从表，就需要显示该从表的合计,在从表的底部.");
		map.AddTBString(MapAttrAttr.Tip, null, "激活提示", true, false, 0, 400, 20, true);
		map.AddTBString("NumMin", null, "最小值", true, false, 0, 400, 20);
		map.AddTBString("NumMax", null, "最大值", true, false, 0, 400, 20);
		map.AddTBFloat("NumStepLength", 1, "步长", true, false);
		map.AddTBInt("NumScale", 2, "小数位数", false, false);
		map.AddTBAtParas(4000);

			///#endregion 基本信息.


			///#region 经典表单
		map.AddGroupAttr("经典表单");

		map.AddDDLSysEnum(MapAttrAttr.ColSpan, 1, "TextBox单元格数", true, true, "ColSpanAttrDT", "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格");

		//文本占单元格数量
		map.AddDDLSysEnum(MapAttrAttr.LabelColSpan, 1, "Label文本单元格数", true, true, "ColSpanAttrString", "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格");
		map.AddTBFloat(MapAttrAttr.UIWidth, 80, "宽度", true, false);
		map.AddTBFloat(MapAttrAttr.UIHeight, 23, "高度", true, true);

		//文本跨行
		map.AddTBInt(MapAttrAttr.RowSpan, 1, "行数", true, false);
		//显示的分组.
		map.AddDDLSQL(MapAttrAttr.GroupID, 0, "显示的分组", MapAttrString.getSQLOfGroupAttr(), true);
		map.AddTBInt(MapAttrAttr.Idx, 0, "顺序号", true, false); //@李国文

		map.AddDDLSQL(MapAttrAttr.CSSCtrl, "0", "自定义样式", MapAttrString.getSQLOfCSSAttr(), true);

			///#endregion 经典表单

		//参数字段.
		map.ParaFields = ",NumMin,NumMax,NumStepLength,NumScale,";


			///#region 执行的方法.
		map.AddGroupMethod("基本功能");
		RefMethod rm = new RefMethod();
		rm = new RefMethod();
		rm.Title = "正则表达式";
		rm.ClassMethodName = this.toString() + ".DoRegularExpression()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-settings"; //正则表达式
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "事件绑函数";
		rm.ClassMethodName = this.toString() + ".BindFunction()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-puzzle"; //事件绑定函数。
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "字段计算";
		rm.ClassMethodName = this.toString() + ".DoAutoFull()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-energy"; //取多个字段计算结果.
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "对从表列计算";
		rm.ClassMethodName = this.toString() + ".DoAutoFullDtlField()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-energy"; //取多个字段计算结果.
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "求两个日期天数";
		rm.ClassMethodName = this.toString() + ".DoReqDays()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-energy"; //取多个字段计算结果.
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "设置文本框RMB大写";
		rm.ClassMethodName = this.toString() + ".DoRMBDaXie()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-wrench";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "输入范围限制";
		rm.ClassMethodName = this.toString() + ".DoLimit()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-wrench";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "全局风格定义";
		rm.ClassMethodName = this.toString() + ".DoGloValStyles()";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Icon = "icon-wrench";
		rm.RefAttrKey = MapAttrAttr.CSSCtrl;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "字段名链接";
		rm.ClassMethodName = this.toString() + ".DoFieldNameLink()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-settings";
		map.AddRefMethod(rm);


			///#endregion 执行的方法.

		this.set_enMap(map);
		return this.get_enMap();
	}
	public final String DoFieldNameLink() {
		return "../../Admin/FoolFormDesigner/MapExt/FieldNameLink.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{

			///#region 修改默认值.
		//如果没默认值.
		if (DataType.IsNullOrEmpty(this.getDefVal()) && this.getDefValType() == 0)
		{
			this.setDefVal(MapAttrAttr.DefaultVal);
		}

		MapData md = new MapData();
		md.setNo(this.getFrmID());

		if (md.RetrieveFromDBSources() == 1)
		{
			//修改默认值.
			if (this.getMyDataType() == DataType.AppInt)
			{
				DBAccess.UpdateTableColumnDefaultVal(md.getPTable(), this.getKeyOfEn(), Integer.parseInt(this.getDefVal()));
			}
			if (this.getMyDataType() == DataType.AppDouble)
			{
				DBAccess.UpdateTableColumnDefaultVal(md.getPTable(), this.getKeyOfEn(), Double.parseDouble(this.getDefVal()));
			}
			if (this.getMyDataType() == DataType.AppFloat)
			{
				DBAccess.UpdateTableColumnDefaultVal(md.getPTable(), this.getKeyOfEn(), Float.parseFloat(this.getDefVal()));
			}
			if (this.getMyDataType() == DataType.AppMoney)
			{
				DBAccess.UpdateTableColumnDefaultVal(md.getPTable(), this.getKeyOfEn(), new BigDecimal(this.getDefVal()));
			}
			//清空对应的表单的row值
			GEEntity en = new GEEntity(this.getFrmID());
			en.setRow(null);
		}

			///#endregion 修改默认值.

		MapAttr attr = new MapAttr();
		attr.setMyPK(this.getMyPK());
		attr.RetrieveFromDBSources();

		//是否显示合计
		attr.setItIsSum(this.GetValBooleanByKey("ExtIsSum"));
		attr.Update();

		return super.beforeUpdateInsertAction();
	}

	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		MapAttr mapAttr = new MapAttr();
		mapAttr.setMyPK(this.getMyPK());
		mapAttr.RetrieveFromDBSources();
		mapAttr.Update();

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());

		super.afterInsertUpdateAction();
	}

	/**
	 删除后清缓存
	*/
	@Override
	protected void afterDelete() throws Exception
	{
		//删除相对应的rpt表中的字段
		if (this.getFrmID().contains("ND") == true)
		{
			String fk_mapData = this.getFrmID().substring(0, this.getFrmID().length() - 2) + "Rpt";
			String sql = "DELETE FROM Sys_MapAttr WHERE FK_MapData='" + fk_mapData + "' AND KeyOfEn='" + this.getKeyOfEn() + "'";
			DBAccess.RunSQL(sql);
		}

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());
		super.afterDelete();
	}

		///#endregion


		///#region 基本功能.
	/**
	 人民币大写

	 @return
	*/
	public final String DoRMBDaXie() {
		return "../../Admin/FoolFormDesigner/MapExt/RMBDaXie.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}
	/**
	 求天数

	 @return
	*/
	public final String DoReqDays() {
		return "../../Admin/FoolFormDesigner/MapExt/ReqDays.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}
	/**
	 绑定函数

	 @return
	*/
	public final String BindFunction() {
		return "../../Admin/FoolFormDesigner/MapExt/BindFunction.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&T=" + LocalDateTime.now().toString();
	}

		///#endregion


		///#region 方法执行.

	public final String DoLimit() {
		return "../../Admin/FoolFormDesigner/MapExt/NumEnterLimit.htm?&MyPK=" + this.getMyPK() + "&FrmID=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}

	public final String DoAutoFullDtlField() {
		return "../../Admin/FoolFormDesigner/MapExt/AutoFullDtlField.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}
	/**
	 自动计算

	 @return
	*/
	public final String DoAutoFull() {
		return "../../Admin/FoolFormDesigner/MapExt/AutoFull.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}
	/**
	 设置开窗返回值

	 @return
	*/
	public final String DoPopVal() {
		return "../../Admin/FoolFormDesigner/MapExt/PopVal.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=" + this.getMyPK();
	}
	/**
	 正则表达式

	 @return
	*/
	public final String DoRegularExpression() {
		return "../../Admin/FoolFormDesigner/MapExt/RegularExpressionNum.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=" + this.getMyPK();
	}
	/**
	 文本框自动完成

	 @return
	*/
	public final String DoTBFullCtrl() {
		return "../../Admin/FoolFormDesigner/MapExt/TBFullCtrl.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=" + this.getMyPK();
	}
	/**
	 扩展控件

	 @return
	*/
	public final String DoEditFExtContral() {
		return "../../Admin/FoolFormDesigner/EditFExtContral.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=" + this.getMyPK();
	}
	public final String DoGloValStyles() {
		return "../../Admin/FoolFormDesigner/StyletDfine/GloValStyles.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=" + this.getMyPK();
	}

		///#endregion 方法执行.
}
