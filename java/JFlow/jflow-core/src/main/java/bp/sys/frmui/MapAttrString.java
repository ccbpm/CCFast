package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;
import java.time.*;

/**
 实体属性
*/
public class MapAttrString extends EntityMyPK
{

		///#region 文本字段参数属性.
	public final boolean getItIsSupperText()  {
		return this.GetValBooleanByKey(MapAttrAttr.IsSupperText);
	}
	public final void setItIsSupperText(boolean value){
		this.SetValByKey(MapAttrAttr.IsSupperText, value);
	}
	public final int getTextModel()  {
		return this.GetValIntByKey(MapAttrAttr.TextModel);
	}
	public final void setTextModel(int value){
		this.SetValByKey(MapAttrAttr.TextModel, value);
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
	 * 名称
	 */
	public final String getName()  {
		return this.GetValStringByKey(MapAttrAttr.Name);
	}

	public final void setName(String value){
		this.SetValByKey(MapAttrAttr.Name, value);
	}
	public final void setFK_MapData(String val){
		this.SetValByKey(MapAttrAttr.FK_MapData, val);
	}
	/**
	 最大长度
	*/
	public final int getMaxLen()  {
		return this.GetValIntByKey(MapAttrAttr.MaxLen);
	}
	public final void setMaxLen(int value){
		this.SetValByKey(MapAttrAttr.MaxLen, value);
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
	 控件类型
	*/
	public final UIContralType getUIContralType() {
		return UIContralType.forValue(this.GetValIntByKey(MapAttrAttr.UIContralType));
	}
	public final void setUIContralType(UIContralType value){
		this.SetValByKey(MapAttrAttr.UIContralType, value.getValue());
	}
	/**
	 是否可见
	*/
	public final boolean getUIVisible()  {
		return this.GetValBooleanByKey(MapAttrAttr.UIVisible);
	}
	public final void setUIVisible(boolean value){
		this.SetValByKey(MapAttrAttr.UIVisible, value);
	}
	/**
	 是否可编辑
	*/
	public final boolean getUIIsEnable()  {
		return this.GetValBooleanByKey(MapAttrAttr.UIIsEnable);
	}
	public final void setUIIsEnable(boolean val){
		this.SetValByKey(MapAttrAttr.UIIsEnable, val);
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
	 实体属性
	*/
	public MapAttrString()
	{
	}
	/**
	 实体属性
	*/
	public MapAttrString(String myPK) throws Exception {
		this.setMyPK(myPK);
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

		Map map = new Map("Sys_MapAttr", "文本字段");


			///#region 基本字段信息.
		map.AddGroupAttr("基本属性");
		map.AddTBStringPK(MapAttrAttr.MyPK, null, "主键", false, false, 0, 200, 20);

		map.AddTBString(MapAttrAttr.FK_MapData, null, "表单ID", false, false, 1, 100, 20);
		map.AddTBString(MapAttrAttr.Name, null, "字段中文名", true, false, 0, 200, 20, true);
		map.AddTBString(MapAttrAttr.KeyOfEn, null, "字段名", true, true, 1, 200, 20);

		//默认值.
		String sql = "SELECT No,Name FROM Sys_GloVar WHERE GroupKey='DefVal'";
		//显示的分组.
		map.AddDDLSQL("ExtDefVal", "0", "系统默认值", sql, true);

		map.AddTBString(MapAttrAttr.DefVal, null, "默认值表达式", true, false, 0, 400, 20, true);
		map.SetHelperAlert(MapAttrAttr.DefVal, "可以编辑的字段设置默认值后，保存时候按照编辑字段计算.");

		map.AddTBInt(MapAttrAttr.MinLen, 0, "最小长度", true, false);
		map.AddTBInt(MapAttrAttr.MaxLen, 50, "最大长度", true, false);
		map.SetHelperAlert(MapAttrAttr.MaxLen, "定义该字段的字节长度.");

		map.AddTBFloat(MapAttrAttr.UIWidth, 100, "宽度", true, false);
		map.SetHelperAlert(MapAttrAttr.UIWidth, "对自由表单,从表有效,显示文本框的宽度.");

		map.AddTBFloat(MapAttrAttr.UIHeight, 23, "高度", true, false);
		map.AddTBInt(MapAttrAttr.UIContralType, 0, "控件", false, false);
		//map.AddDDLSysEnum(MapAttrAttr.UIContralType, 0, "控件", false, false, MapAttrAttr.UIContralType);
		//map.AddTBFloat("ExtRows", 1, "文本框行数(决定高度)", true, false);

		map.AddBoolean(MapAttrAttr.UIVisible, true, "是否可见？", true, true);
		map.SetHelperAlert(MapAttrAttr.UIVisible, "对于不可见的字段可以在隐藏功能的栏目里找到这些字段进行编辑或者删除.");

		map.AddBoolean(MapAttrAttr.UIIsEnable, true, "是否可编辑？", true, true);
		map.SetHelperAlert(MapAttrAttr.UIIsEnable, "不可编辑,让该字段设置为只读.");

		map.AddBoolean(MapAttrAttr.UIIsInput, false, "是否必填项？", true, true);
	  //  map.AddBoolean(MapAttrAttr.IsRichText, false, "是否富文本？", true, true);
	   // map.SetHelperAlert(MapAttrAttr.IsRichText, "以html编辑器呈现或者编写字段.");
	 //   map.AddBoolean(MapAttrAttr.IsSecret, false, "是否保密？", true, true);

		map.AddDDLSysEnum(MapAttrAttr.TextModel, 0, "文本类型", true, true, "TextModel", "@0=普通文本@1=密码框@2=大文本@3=富文本");

		//map.AddBoolean(MapAttrAttr.IsSupperText, false, "是否大块文本？(是否该字段存放的超长字节字段)", true, true, true);
		//map.SetHelperAlert(MapAttrAttr.IsSupperText, "大块文本存储字节比较长，超过4000个字符.");

			///#endregion 基本字段信息.


			///#region 经典表单
		map.AddGroupAttr("经典表单");

		//单元格数量 2013-07-24 增加
		map.AddDDLSysEnum(MapAttrAttr.ColSpan, 1, "TextBox单元格数量", true, true, "ColSpanAttrDT", "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格@5=跨5个单元格@6=跨6个单元格");
		map.SetHelperAlert(MapAttrAttr.ColSpan, "对于经典表单有效: 标识该字段TextBox横跨的宽度,占的单元格数量.");

		//文本占单元格数量
		map.AddDDLSysEnum(MapAttrAttr.LabelColSpan, 1, "Label单元格数量", true, true, "ColSpanAttrString", "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格@5=跨6个单元格@6=跨6个单元格");
		map.SetHelperAlert(MapAttrAttr.LabelColSpan, "对于经典表单有效: 标识该字段Lable，标签横跨的宽度,占的单元格数量.");

		//文本跨行
		map.AddTBInt(MapAttrAttr.RowSpan, 1, "行数", true, false);

		//显示的分组.
		map.AddDDLSQL(MapAttrAttr.GroupID, 0, "显示的分组", MapAttrString.getSQLOfGroupAttr(), true);

		map.AddTBInt(MapAttrAttr.Idx, 0, "顺序号", true, false);
		map.SetHelperAlert(MapAttrAttr.Idx, "对经典表单有效:用于调整字段在同一个分组中的顺序.");

		map.AddTBString(MapAttrAttr.ICON, null, "图标", true, false, 0, 50, 20, true);

		map.AddTBString(MapAttrAttr.Tip, null, "激活提示", true, false, 0, 400, 20, true);
		map.SetHelperAlert(MapAttrAttr.Tip, "在文本框输入的时候显示在文本框背景的提示文字,也就是文本框的 placeholder 的值.");


		map.AddDDLSysEnum(MapAttrAttr.IsSigan, 0, "签名模式", true, true, MapAttrAttr.IsSigan, "@0=无@1=图片签名@2=山东CA@3=广东CA@4=图片盖章");
		map.SetHelperAlert(MapAttrAttr.IsSigan, "图片签名,需要的是当前是只读的并且默认值为@WebUser.No,其他签名需要个性化的编写数字签章的集成代码.");

		//map.AddTBString("CSSLabel", null, "标签样式css", true, false, 0, 400, 20, true);
		//map.AddTBString("CSSCtrl", null, "控件样式css", true, false, 0, 400, 20, true);
		//CCS样式
		//map.AddDDLSQL(MapAttrAttr.CSS, "0", "控件样式", MapAttrString.SQLOfCSSAttr, true);

		map.AddDDLSQL("CSSLabel", "0", "标签样式", MapAttrString.getSQLOfCSSAttr(), true);
		map.AddDDLSQL(MapAttrAttr.CSSCtrl, "0", "控件样式", MapAttrString.getSQLOfCSSAttr(), true);

			///#endregion 经典表单.


			///#region 基本功能.
		map.AddGroupMethod("基本功能");
		RefMethod rm = new RefMethod();

		rm = new RefMethod();
		rm.Title = "全局样式定义";
		rm.ClassMethodName = this.toString() + ".DoStyleEditer()";
		rm.refMethodType = RefMethodType.LinkModel;
		rm.RefAttrKey = "CSSCtrl";
		rm.GroupName = "高级设置";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "文本框自动完成";
		rm.ClassMethodName = this.toString() + ".DoTBFullCtrl2019()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-energy";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "快速录入";
		rm.ClassMethodName = this.toString() + ".DoFastEnter()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-docs";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "全局默认值";
		rm.ClassMethodName = this.toString() + ".DoDefVal()";
		rm.refMethodType = RefMethodType.RightFrameOpen;

		//  map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "Pop返回值";
		rm.ClassMethodName = this.toString() + ".DoPop2019()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-magnifier-add";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "事件绑函数";
		rm.ClassMethodName = this.toString() + ".BindFunction()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-puzzle";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "正则表达式";
		rm.ClassMethodName = this.toString() + ".DoRegularExpression()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-settings";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "字段名链接";
		rm.ClassMethodName = this.toString() + ".DoFieldNameLink()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-settings";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "只读字段名链接";
		rm.ClassMethodName = this.toString() + ".DoReadOnlyLink()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-settings";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "字符拼接";
		rm.ClassMethodName = this.toString() + ".StringJoint()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-settings";
		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "常用字段";
		//rm.ClassMethodName = this.ToString() + ".DoGeneralField()";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//map.AddRefMethod(rm);

			///#endregion 基本功能.


			///#region 输入多选.
		map.AddGroupMethod("输入内容多选");
		rm = new RefMethod();
		//rm.GroupName = "输入内容多选";
		rm.Title = "小范围多选(combox)";
		rm.ClassMethodName = this.toString() + ".DoMultipleChoiceSmall()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-equalizer";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		//rm.GroupName = "输入内容多选";
		rm.Title = "小范围单选(select)";
		rm.ClassMethodName = this.toString() + ".DSingleChoiceSmall()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-equalizer";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		//rm.GroupName = "输入内容多选";
		rm.Title = "搜索选择";
		rm.ClassMethodName = this.toString() + ".DoMultipleChoiceSearch()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-magnifier";

		map.AddRefMethod(rm);

		rm = new RefMethod();
		//rm.GroupName = "输入内容多选";
		rm.Title = "高级快速录入";
		rm.ClassMethodName = this.toString() + ".DoFastInput()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-magnifier";
		map.AddRefMethod(rm);

	  //  map.AddGroupMethod("移动端扫码录入");
		rm = new RefMethod();
		rm.Title = "移动端扫码录入";
		rm.ClassMethodName = this.toString() + ".DoQRCode()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-energy";
		map.AddRefMethod(rm);


			///#endregion 输入多选


			///#region 高级设置.
		map.AddGroupMethod("高级设置");
		rm = new RefMethod();
	  //  rm.GroupName = "高级设置";
		rm.Title = "字段重命名";
		rm.ClassMethodName = this.toString() + ".DoRenameField()";
		rm.getHisAttrs().AddTBString("key1", "@KeyOfEn", "字段重命名为?", true, false, 0, 100, 100);
	   // rm.getHisAttrs().AddTBString("k33ey1", "@KeyOfEn", "字段重33命名为?", true, false, 0, 100, 100);

		rm.refMethodType = RefMethodType.Func;
		rm.Warning = "如果是节点表单，系统就会把该流程上的所有同名的字段都会重命名，包括NDxxxRpt表单。";
		rm.Icon = "icon-refresh";
		map.AddRefMethod(rm);

		rm = new RefMethod();
	  //  rm.GroupName = "高级设置";
		rm.Title = "字段类型转换";
		rm.ClassMethodName = this.toString() + ".DoTurnFieldType()";
		rm.getHisAttrs().AddTBString("key1", "int", "输入类型，格式:int,float,double,date,datetime,boolean", true, false, 0, 100, 100);
		rm.refMethodType = RefMethodType.Func;
		rm.Warning = "string类型转化为枚举，请先转为int,由int转枚举.";
		rm.Icon = "icon-refresh";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "批处理";
		rm.ClassMethodName = this.toString() + ".DoEleBatch()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
	  //  rm.GroupName = "高级设置";
		rm.Icon = "icon-calculator";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "转化为签批组件";
		rm.ClassMethodName = this.toString() + ".DoSetCheck()";
		rm.Warning = "您确定要设置为签批组件吗？";
		//rm.GroupName = "高级设置";
		rm.Icon = "icon-magic-wand";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "全局风格定义";
		rm.ClassMethodName = this.toString() + ".DoGloValStyles()";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Icon = "icon-wrench";
		rm.RefAttrKey = MapAttrAttr.CSSCtrl;
		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "转化为评论组件";
		//rm.ClassMethodName = this.ToString() + ".DoSetFlowBBS()";
		//rm.Warning = "您确定要设置为评论组件吗？";
		// rm.GroupName = "高级设置";
		//map.AddRefMethod(rm);


			///#endregion 执行的方法.

		this.set_enMap(map);
		return this.get_enMap();
	}
	/**
	 移动端扫码录入

	 @return
	*/
	public final String DoQRCode() {
		return "../../Admin/FoolFormDesigner/MapExt/QRCode.htm?FrmID=" + this.getFrmID() + "&MyPK=" + this.getMyPK();
	}
	public final String DoGloValStyles() {
		return "../../Admin/FoolFormDesigner/StyletDfine/GloValStyles.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=" + this.getMyPK();
	}
	/**
	 字段分组查询语句
	*/
	public static String getSQLOfGroupAttr()
	{
		return "SELECT OID as No, Lab as Name FROM Sys_GroupField WHERE FrmID='@FK_MapData'  AND (CtrlType IS NULL OR CtrlType='')  ";
	}
	/**
	 字段自定义样式查询
	*/
	public static String getSQLOfCSSAttr()
	{
		String sql = "SELECT ";
		switch (bp.difference.SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
				sql = "SELECT '' AS No, '默认风格' as Name ";
				break;
			case Oracle:
			case DM:
			case UX:
			case KingBaseR3:
			case KingBaseR6:
			case HGDB:
			case GBASE8CByOracle:
				sql = "SELECT '' AS No, '默认风格' as Name FROM DUAL ";
				break;
			default:
				sql = "SELECT '' AS No, '默认风格' as Name ";
				break;
		}

		String mysql = sql + " UNION ";
		mysql += " SELECT No,Name FROM Sys_GloVar WHERE GroupKey='CSS' OR GroupKey='css' ";
		return mysql;
	}
	/**
	 删除
	*/
	@Override
	protected void afterDelete() throws Exception
	{
		//如果是附件字段删除附件属性
		//MapAttr attr = new MapAttr(this.MyPK);
		if (this.getUIContralType() == UIContralType.AthShow)
		{
			FrmAttachment ath = new FrmAttachment();
			ath.Delete(FrmAttachmentAttr.MyPK, this.getMyPK());
		}
		//删除可能存在的关联属性.
		String sql = "DELETE FROM Sys_MapAttr WHERE FK_MapData='" + this.getFrmID() + "' AND KeyOfEn='" + this.getKeyOfEn() + "T'";
		DBAccess.RunSQL(sql);

		//删除相关的图片信息.
		if (DBAccess.IsExitsTableCol("Sys_FrmImg", "KeyOfEn") == true)
		{
			sql = "DELETE FROM Sys_FrmImg WHERE FK_MapData='" + this.getFrmID() + "' AND KeyOfEn='" + this.getKeyOfEn() + "T'";
		}
		DBAccess.RunSQL(sql);

		//删除相对应的rpt表中的字段
		if (this.getFrmID().contains("ND") == true)
		{
			String fk_mapData = this.getFrmID().substring(0, this.getFrmID().length() - 2) + "Rpt";
			sql = "DELETE FROM Sys_MapAttr WHERE FK_MapData='" + fk_mapData + "' AND( KeyOfEn='" + this.getKeyOfEn() + "T' OR KeyOfEn='" + this.getKeyOfEn() + "')";
			DBAccess.RunSQL(sql);
		}

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());

		super.afterDelete();
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


		///#endregion


		///#region 基本功能.
	/**
	 全局样式定义工具

	 @return
	*/
	public final String DoStyleEditer() {
		return "../../Admin/FoolFormDesigner/StyletDfine/GloValStyles.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}
	public final String DoTurnFieldType(String type) throws Exception {
		if (type.toLowerCase().equals("int") == true)
		{
			this.SetValByKey(MapAttrAttr.MyDataType, DataType.AppInt);
			this.SetValByKey(MapAttrAttr.LGType, 0); //设置成普通类型的.
			this.Update();
			return "转换为 int 类型执行成功，请您手工修改字段类型，以防止cc不能自动转换过来.";
		}
		if (type.toLowerCase().equals("float") == true)
		{
			this.SetValByKey(MapAttrAttr.MyDataType, DataType.AppFloat);
			this.SetValByKey(MapAttrAttr.LGType, 0); //设置成普通类型的.
			this.Update();
			return "转换为 float 类型执行成功，请您手工修改字段类型，以防止cc不能自动转换过来.";
		}

		if (type.toLowerCase().equals("double") == true)
		{
			this.SetValByKey(MapAttrAttr.MyDataType, DataType.AppDouble);
			this.SetValByKey(MapAttrAttr.LGType, 0); //设置成普通类型的.
			this.Update();
			return "转换为 double 类型执行成功，请您手工修改字段类型，以防止cc不能自动转换过来.";
		}


		if (type.toLowerCase().equals("date") == true)
		{
			this.SetValByKey(MapAttrAttr.MyDataType, DataType.AppDate);
			this.SetValByKey(MapAttrAttr.LGType, 0); //设置成普通类型的.
			this.Update();
			return "转换为 date 类型执行成功.";
		}

		if (type.toLowerCase().equals("datetime") == true)
		{
			this.SetValByKey(MapAttrAttr.MyDataType, DataType.AppDateTime);
			this.SetValByKey(MapAttrAttr.LGType, 0); //设置成普通类型的.
			this.Update();
			return "转换为 AppDateTime 类型执行成功.";
		}

		if (type.toLowerCase().equals("boolean") == true)
		{
			this.SetValByKey(MapAttrAttr.MyDataType, DataType.AppBoolean);
			this.SetValByKey(MapAttrAttr.LGType, 0); //设置成普通类型的.
			this.Update();
			return "转换为 boolean 类型执行成功，请您手工修改字段类型int，以防止cc不能自动转换过来.";
		}

		return "err@输入的类型错误:" + type;
	}

	/**
	 * 修改字段中文名
	 * @param newField
	 * @return
	 */
	public String DoRenameCH(String newField) throws Exception {

		this. setName(newField);
		this.Update();

		//判断是否是节点表单.
		if (this.getFrmID().indexOf("ND") < 0)
			return "更新成功.";

		String nodeID = this.getFrmID().replace("ND", "");
		String flowNo = DBAccess.RunSQLReturnStringIsNull("SELECT FK_Flow FROM WF_Node WHERE NodeID=" + nodeID, null);
		if (flowNo == null)
			return "更新失败";

		String frmID = "ND" + Integer.parseInt(flowNo) + "Rpt";

		DBAccess.RunSQL("UPDATE Sys_MapAttr SET Name='" + this.getName() + "' WHERE MyPK='" + frmID + "_" + this.getKeyOfEn() + "'");
		return "更新成功";
	}
	/**
	 字段重命名

	 @param newField
	 @return
	*/
	public final String DoRenameField(String newField) throws Exception {

		if (this.getKeyOfEn().equals(newField) == true)
		{
			return "err@与现在的名字相同.";
		}

		MapData md = new MapData(this.getFrmID());

		if (DBAccess.IsExitsTableCol(md.getPTable(), newField) == true)
		{
			return "err@该字段已经存在数据表:" + md.getPTable() + ",您不能重命名.";
		}


		String sql = "SELECT COUNT(MyPK) as Num FROM Sys_MapAttr WHERE MyPK='" + this.getFrmID() + "_" + newField + "'";
		if (DBAccess.RunSQLReturnValInt(sql) >= 1)
		{
			return "err@该字段已经存在[" + newField + "].";
		}

		//修改字段名.
		sql = "UPDATE Sys_MapAttr SET KeyOfEn='" + newField + "', MyPK='" + this.getFrmID() + "_" + newField + "'  WHERE KeyOfEn='" + this.getKeyOfEn() + "' AND FK_MapData='" + this.getFrmID() + "'";
		DBAccess.RunSQL(sql);

		//更新处理他的相关业务逻辑.
		MapExts exts = new MapExts(this.getFrmID());
		for (MapExt item : exts.ToJavaList())
		{
			item.setMyPK(item.getMyPK().replace("_" + this.getKeyOfEn(), "_" + newField));

			if (item.getAttrOfOper().equals(this.getKeyOfEn()))
			{
				item.setAttrOfOper(newField);
			}

			if (item.getAttrsOfActive().equals(this.getKeyOfEn()))
			{
				item.setAttrsOfActive(newField);
			}

			item.setTag(item.getTag().replace(this.getKeyOfEn(), newField));
			item.setTag1(item.getTag1().replace(this.getKeyOfEn(), newField));
			item.setTag2(item.getTag2().replace(this.getKeyOfEn(), newField));
			item.setTag3(item.getTag3().replace(this.getKeyOfEn(), newField));

			item.setAtPara(item.getAtPara().replace(this.getKeyOfEn(), newField));
			item.setDoc(item.getDoc().replace(this.getKeyOfEn(), newField));
			item.Save();
		}

		//如果是表单库的表单，需要把数据库中的字段重命名
		if (DataType.IsNullOrEmpty(md.getFormTreeNo()) == false && this.getFrmID().startsWith("ND") == false)
		{
			//对数据库中的字段重命名
			DBAccess.RenameTableField(md.getPTable(), this.getKeyOfEn(), newField);
			return "重名成功,关闭设置页面重新查看表单设计器中字段属性";
		}



		//如果是节点表单，就修改其他的字段.
		if (this.getFrmID().indexOf("ND") != 0)
		{
			return "重名称成功,如果是自由表单，请关闭表单设计器重新打开.";
		}

		String strs = this.getFrmID().replace("ND", "");
		strs = strs.substring(0, strs.length() - 2);
		String rptTable = "ND" + strs + "Rpt";
		MapDatas mds = new MapDatas();
		mds.Retrieve(MapDataAttr.No, rptTable);
		if (mds.size() == 0)
		{
			sql = "UPDATE Sys_MapAttr SET KeyOfEn='" + newField + "',  MyPK='" + rptTable + "_" + newField + "' WHERE KeyOfEn='" + this.getKeyOfEn() + "' AND FK_MapData='" + rptTable + "'";
			DBAccess.RenameTableField(rptTable, this.getKeyOfEn(), newField);
		}

		for (MapData item : mds.ToJavaList())
		{
			sql = "UPDATE Sys_MapAttr SET KeyOfEn='" + newField + "',  MyPK='" + item.getNo() + "_" + newField + "' WHERE KeyOfEn='" + this.getKeyOfEn() + "' AND FK_MapData='" + item.getNo() + "'";
			DBAccess.RunSQL(sql);
			DBAccess.RenameTableField(item.getPTable(), this.getKeyOfEn(), newField);
		}


		//自由表单模板的替换
		return "重名称成功,如果是自由表单，请关闭表单设计器重新打开.";
	}
	/**
	 绑定函数

	 @return
	*/
	public final String BindFunction() {
		return "../../Admin/FoolFormDesigner/MapExt/BindFunction.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&T=" + LocalDateTime.now().toString();
	}
	/**
	 快速录入

	 @return
	*/
	public final String DoFastEnter() {
		return "../../Admin/FoolFormDesigner/MapExt/FastInput.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}
	public final String DoFieldNameLink() {
		return "../../Admin/FoolFormDesigner/MapExt/FieldNameLink.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}
	public final String DoReadOnlyLink() {
		return "../../Admin/FoolFormDesigner/MapExt/ReadOnlyLink.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();

	}
	/**
	 快速录入

	 @return
	*/
	public final String DoPop2019() {
		return "../../Admin/FoolFormDesigner/Pop/Default.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}
	/**
	 设置常用字段

	 @return
	*/
	public final String DoGeneralField() {
		return "../../Admin/FoolFormDesigner/General/GeneralField.htm?FrmID=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}
	/**
	 全局默认值

	 @return
	*/
	public final String DoDefVal() {
		return "../../Admin/FoolFormDesigner/DefVal.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}

		///#endregion


		///#region 方法执行 Pop自动完成.
	/**
	 自动计算

	 @return
	*/
	public final String StringJoint() {
		return "../../Admin/FoolFormDesigner/MapExt/StringJoint.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}
	/**
	 简单列表模式

	 @return
	*/
	public final String DoPopFullCtrl() {
		return "../../Admin/FoolFormDesigner/MapExt/PopFullCtrl.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=TBFullCtrl_" + this.getMyPK();
	}
	/**
	 多条件查询列表模式

	 @return
	*/
	public final String DoPopFullCtrlAdv() {
		return "../../Admin/FoolFormDesigner/MapExt/PopFullCtrl.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=TBFullCtrl_" + this.getMyPK();
	}

		///#endregion 方法执行 Pop填充自动完成.


		///#region 方法执行.
	/**
	 设置签批组件

	 @return 执行结果
	*/
	public final String DoSetCheck() throws Exception {
		this.setUIContralType(UIContralType.SignCheck);
		this.setUIIsEnable(false);
		this.setUIVisible(true);
		this.Update();
		return "设置成功,当前文本框已经是签批组件了,请关闭掉当前的窗口.";
	}

	public final String DoSetFlowBBS() throws Exception {
		MapAttrs mapAttrs = new MapAttrs();
		mapAttrs.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), MapAttrAttr.UIContralType, UIContralType.FlowBBS.getValue());
		if (mapAttrs.size() == 0)
		{
			this.setUIContralType(UIContralType.FlowBBS);
			this.setUIIsEnable(false);
			this.setUIVisible(false);
			this.Update();
			return "设置成功,当前文本框已经是评论组件了,请关闭掉当前的窗口.";
		}

		return "表单中只能存在一个评论组件，表单" + this.getFrmID() + "已经存在评论组件不能再增加";
	}
	/**
	 批处理

	 @return
	*/
	public final String DoEleBatch() throws Exception {
		//return "../../Admin/FoolFormDesigner/EleBatch.aspx?EleType=MapAttr&KeyOfEn=" + this.getKeyOfEn() + "&FType=1&MyPK=" + this.MyPK + "&FK_MapData=" + this.FrmID;
		return "../../Admin/FoolFormDesigner/EleBatch.htm?EleType=MapAttr&KeyOfEn=" + this.getKeyOfEn() + "&FType=1&MyPK=" + this.getMyPK() + "&FK_MapData=" + this.getFrmID();
	}
	/**
	 小范围多选

	 @return
	*/
	public final String DoMultipleChoiceSmall() {
		return "../../Admin/FoolFormDesigner/MapExt/MultipleChoiceSmall/Default.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&m=s";
	}


	public final String DSingleChoiceSmall() {
		return "../../Admin/FoolFormDesigner/MapExt/SingleChoiceSmall/Default.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&m=s";

	}
	/**
	 大范围多选

	 @return
	*/
	public final String DoMultipleChoiceSearch() {
		return "../../Admin/FoolFormDesigner/MapExt/MultipleChoiceSearch.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&m=s";
	}

	public final String DoFastInput() {
		return "../../Admin/FoolFormDesigner/MapExt/MultipleInputSearch.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&m=s";

	}
	/**
	 超链接

	 @return
	*/
	public final String DoLink() {
		return "../../Admin/FoolFormDesigner/MapExt/Link.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=" + this.getMyPK() + "&FK_MapExt=Link_" + this.getFrmID() + "_" + this.getKeyOfEn();
	}
	/**
	 设置开窗返回值

	 @return
	*/
	public final String DoPopVal() {
		return "../../Admin/FoolFormDesigner/MapExt/PopVal.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=" + this.getMyPK() + "&FK_MapExt=PopVal_" + this.getFrmID() + "_" + this.getKeyOfEn();
	}
	/**
	 正则表达式

	 @return
	*/
	public final String DoRegularExpression() {
		return "../../Admin/FoolFormDesigner/MapExt/RegularExpression.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=" + this.getMyPK();
	}

	/**
	 文本框自动完成

	 @return
	*/
	public final String DoTBFullCtrl2019() {
		return "../../Admin/FoolFormDesigner/TBFullCtrl/Default.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=TBFullCtrl_" + this.getMyPK();
	}

	/**
	 设置级联

	 @return
	*/
	public final String DoInputCheck() {
		return "../../Admin/FoolFormDesigner/MapExt/InputCheck.htm?FK_MapData=" + this.getFrmID() + "&OperAttrKey=" + this.getKeyOfEn() + "&RefNo=" + this.getMyPK() + "&DoType=New&ExtType=InputCheck";
	}
	/**
	 扩展控件

	 @return
	*/
	public final String DoEditFExtContral() {
		return "../../Admin/FoolFormDesigner/EditFExtContral.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=" + this.getMyPK();
	}
	/**
	 扩展控件2019

	 @return
	*/
	public final String DoEditFExtContral2019() {
		return "../../Admin/FoolFormDesigner/EditFExtContral/Default.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=" + this.getMyPK();
	}

		///#endregion 方法执行.


		///#region 重载.
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		MapAttr attr = new MapAttr();
		attr.setMyPK(this.getMyPK());
		attr.RetrieveFromDBSources();

		//高度.
		//  attr.UIHeightInt = this.GetValIntByKey("ExtRows") * 23;

		//attr.IsRichText = this.GetValBooleanByKey(MapAttrAttr.IsRichText); //是否是富文本？
		//attr.IsSupperText = this.GetValIntByKey(MapAttrAttr.IsSupperText); //是否是大块文本？

		if (this.getTextModel() == 2 || this.getTextModel() == 3)
		{
			//attr.setMaxLen(4000);
			this.SetValByKey(MapAttrAttr.MaxLen, 4000);
		}



			///#region 自动扩展字段长度. 需要翻译.
		if (attr.getMaxLen() < this.getMaxLen())
		{
			attr.setMaxLen(this.getMaxLen());

			String sql = "";
			MapData md = new MapData();
			md.setNo(this.getFrmID());
			if (md.RetrieveFromDBSources() == 1)
			{
				if (DBAccess.IsExitsTableCol(md.getPTable(), this.getKeyOfEn()) == true && DBAccess.IsView(md.getPTable())==false)
				{
					switch (bp.difference.SystemConfig.getAppCenterDBType())
					{
						case MSSQL:
							sql = "ALTER TABLE " + md.getPTable() + " ALTER column " + attr.getField() + " NVARCHAR(" + attr.getMaxLen() + ")";
							break;
						case MySQL:
						case GBASE8CByMySQL:
						case GBASE8A:
							sql = "ALTER table " + md.getPTable() + " modify " + attr.getField() + " NVARCHAR(" + attr.getMaxLen() + ")";
							break;
						case Oracle:
						case DM:
						case GBASE8CByOracle:
							sql = "ALTER table " + md.getPTable() + " modify " + attr.getField() + " VARCHAR2(" + attr.getMaxLen() + ")";
							break;
						case KingBaseR3:
						case KingBaseR6:
							sql = "ALTER table " + md.getPTable() + " ALTER COLUMN " + attr.getField() + " Type VARCHAR2(" + attr.getMaxLen() + ")";
							break;
						case PostgreSQL:
						case UX:
						case HGDB:
							sql = "ALTER table " + md.getPTable() + " alter " + attr.getField() + " type character varying(" + attr.getMaxLen() + ")";
							break;
						default:
							throw new RuntimeException("err@没有判断的数据库类型.");
					}
					DBAccess.RunSQL(sql); //如果是oracle如果有nvarchar与varchar类型，就会出错.
				}
			}
		}

		//设置默认值.

			///#endregion 自动扩展字段长度.


			///#region 设置默认值.
		MapData mymd = new MapData();
		mymd.setNo(this.getFrmID());
		if (mymd.RetrieveFromDBSources() == 1 && attr.getDefVal().equals(this.GetValStrByKey("DefVal")) == false)
		{
			DBAccess.UpdateTableColumnDefaultVal(mymd.getPTable(), attr.getField(), this.GetValStrByKey("DefVal"));
		}

			///#endregion 设置默认值.

		//默认值.
		String defval = this.GetValStrByKey("ExtDefVal");
		if (Objects.equals(defval, "") || Objects.equals(defval, "0"))
		{
			String defVal = this.GetValStrByKey("DefVal");
			if (defval.contains("@") == true)
			{
				this.SetValByKey("DefVal", "");
			}
		}
		else
		{
			this.SetValByKey("DefVal", this.GetValByKey("ExtDefVal"));
		}

		//执行保存.
		attr.Save();

		if (Objects.equals(this.GetValStrByKey("GroupID"), "无"))
		{
			this.SetValByKey("GroupID", "0");
		}


		return super.beforeUpdateInsertAction();
	}

		///#endregion
}
