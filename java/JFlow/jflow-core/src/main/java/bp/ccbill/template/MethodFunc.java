package bp.ccbill.template;

import bp.da.*;
import bp.en.Map;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;

/** 
 功能执行
*/
public class MethodFunc extends EntityNoName
{

		///#region 基本属性
	/** 
	 表单ID
	*/
	public final String getFrmID() {
		return this.GetValStringByKey(MethodAttr.FrmID);
	}
	public final void setFrmID(String value)  {
		this.SetValByKey(MethodAttr.FrmID, value);
	}
	/** 
	 方法ID
	*/
	public final String getMethodID() {
		return this.GetValStringByKey(MethodAttr.MethodID);
	}
	public final void setMethodID(String value)  {
		this.SetValByKey(MethodAttr.MethodID, value);
	}

	public final String getMsgErr() {
		return this.GetValStringByKey(MethodAttr.MsgErr);
	}
	public final void setMsgErr(String value)  {
		this.SetValByKey(MethodAttr.MsgErr, value);
	}
	public final String getMsgSuccess() {
		return this.GetValStringByKey(MethodAttr.MsgSuccess);
	}
	public final void setMsgSuccess(String value)  {
		this.SetValByKey(MethodAttr.MsgSuccess, value);
	}
	public final String getTag1() {
		return this.GetValStringByKey(MethodAttr.Tag1);
	}
	public final void setTag1(String value)  {
		this.SetValByKey(MethodAttr.Tag1, value);
	}
	public final String getDocs() {
		return this.GetValStringByKey(MethodAttr.Docs);
	}
	public final void setDocs(String value)  {
		this.SetValByKey(MethodAttr.Docs, value);
	}
	public final String getMethodDocUrl() throws Exception {
		String s = this.GetValStringByKey(MethodAttr.MethodDoc_Url);
		if (DataType.IsNullOrEmpty(s) == true)
		{
			s = "http://192.168.0.100/MyPath/xxx.xx";
		}
		return s;
	}
	public final void setMethodDocUrl(String value)  {
		this.SetValByKey(MethodAttr.MethodDoc_Url, value);
	}
	/** 
	 获得或者设置sql脚本.
	*/
	public final String getMethodDocSQL() throws Exception {
		String strs = this.GetBigTextFromDB("SQLScript");
		if (DataType.IsNullOrEmpty(strs) == true)
		{
			return this.getMethodDocSQLDemo(); //返回默认信息.
		}
		return strs;
	}
	public final void setMethodDocSQL(String value) throws Exception {
		this.SaveBigTxtToDB("SQLScript", value);
	}
	/** 
	 获得该实体的demo.
	*/
	public final String getMethodDocTypeScriptDemo() throws Exception {
		String file = bp.difference.SystemConfig.getCCFlowAppPath() + "WF/CCBill/Admin/MethodDoc/MethodDocDemoTS.txt";
		String doc = DataType.ReadTextFile(file); //读取文件.
		doc = doc.replace("/#", "+"); //为什么？
		doc = doc.replace("/$", "-"); //为什么？

		doc = doc.replace("@FrmID", this.getFrmID());

		return doc;
	}
	public final String getMethodDocJavaScriptDemo() throws Exception {
		String file = bp.difference.SystemConfig.getCCFlowAppPath() + "WF/CCBill/Admin/MethodDoc/MethodDocDemoJS.txt";
		String doc = DataType.ReadTextFile(file); //读取文件.
		doc = doc.replace("/#", "+"); //为什么？
		doc = doc.replace("/$", "-"); //为什么？

		doc = doc.replace("@FrmID", this.getFrmID());

		return doc;
	}
	public final String getMethodDocSQLDemo() throws Exception {
		String file = bp.difference.SystemConfig.getCCFlowAppPath() + "WF/CCBill/Admin/MethodDoc/MethodDocDemoSQL.txt";
		String doc = DataType.ReadTextFile(file); //读取文件.
		doc = doc.replace("@FrmID", this.getFrmID());
		return doc;
	}
	/** 
	 获得JS脚本.
	 
	 @return 
	*/
	public final String Gener_MethodDoc_JavaScript() throws Exception {
		return this.getMethodDocJavaScript();
	}

	public final String Gener_MethodDoc_JavaScript_function() throws Exception {
		String paras = "";
		MapAttrs mattrs = new MapAttrs(this.getNo());
		for (MapAttr item : mattrs.ToJavaList())
		{
			paras += item.getKeyOfEn() + ",";
		}
		if (mattrs.size() > 1)
		{
			paras = paras.substring(0, paras.length() - 1);
		}

		String strs = " function " + this.getMethodID() + "(" + paras + ") {";
		strs += this.getMethodDocJavaScript();
		strs += "}";
		return strs;
	}
	/** 
	 获得SQL脚本
	 
	 @return 
	*/
	public final String Gener_MethodDoc_SQL() throws Exception {
		return this.getMethodDocSQL();
	}

	/**
	 * 获得SQL脚本
	 * @return
	 */
	public String SaveSQLScript(String sql) throws Exception
	{
		this.setMethodDocSQL(sql);
		return "保存成功.";
	}
	/** 
	 获得或者设置js脚本.
	*/
	public final String getMethodDocJavaScript() throws Exception {
		String strs = this.GetBigTextFromDB("JSScript");
		if (DataType.IsNullOrEmpty(strs) == true)
		{
			return this.getMethodDocJavaScriptDemo();
		}

		strs = strs.replace("/#", "+");
		strs = strs.replace("/$", "-");
		return strs;
	}
	public final void setMethodDocJavaScript(String value) throws Exception {

		this.SaveBigTxtToDB("JSScript", value);

	}

	/**
	 获得或者设置ts脚本.
	 */
	public final String getMethodDocTypeScript() throws Exception {
		String strs = this.GetBigTextFromDB("TSScript");
		if (DataType.IsNullOrEmpty(strs) == true)
		{
			return this.getMethodDocTypeScriptDemo();
		}

		strs = strs.replace("/#", "+");
		strs = strs.replace("/$", "-");
		return strs;
	}
	public final void setMethodDocTypeScript(String value) throws Exception {

		this.SaveBigTxtToDB("TSScript", value);

	}

	/** 
	 方法类型：@0=SQL@1=URL@2=JavaScript@3=业务单元
	*/
	public final int getMethodDocTypeOfFunc() {
		return this.GetValIntByKey(MethodAttr.MethodDocTypeOfFunc);
	}
	public final void setMethodDocTypeOfFunc(int value)  {
		this.SetValByKey(MethodAttr.MethodDocTypeOfFunc, value);
	}
	/** 
	 方法类型
	*/
	public final RefMethodType getRefMethodType() {
		return RefMethodType.forValue(this.GetValIntByKey(MethodAttr.RefMethodType));
	}
	public final void setRefMethodType(RefMethodType value)  {
		this.SetValByKey(MethodAttr.RefMethodType, value.getValue());
	}

		///#endregion


		///#region 构造方法
	/** 
	 权限控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (WebUser.getIsAdmin())
		{
			uac.IsUpdate = true;
			return uac;
		}
		return super.getHisUAC();
	}
	/** 
	 功能执行
	*/
	public MethodFunc()
	{
	}
	public MethodFunc(String mypk) throws Exception {
		this.setNo(mypk);
		this.Retrieve();
	}
	/**
	 * 重写基类方法
	 */
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Frm_Method", "功能方法");


		//主键.
		map.AddTBStringPK(MethodAttr.No, null, "编号", true, true, 0, 50, 10);
		map.AddTBString(MethodAttr.Name, null, "方法名", true, false, 0, 300, 10);
		map.AddTBString(MethodAttr.MethodID, null, "方法ID", true, true, 0, 300, 10);
		map.AddTBString(MethodAttr.GroupID, null, "分组ID", true, true, 0, 50, 10);

		//功能标记.
		map.AddTBString(MethodAttr.MethodModel, null, "方法模式", true, true, 0, 300, 10);
		map.AddTBString(MethodAttr.Tag1, null, "Tag1", true, true, 0, 300, 10);
	   // map.AddTBString(MethodAttr.Mark, null, "Mark", true, true, 0, 300, 10);
		map.AddTBString(MethodAttr.FrmID, null, "表单ID", true, true, 0, 300, 10);

		map.AddTBString(MethodAttr.Icon, null, "图标", true, false, 0, 50, 10, true);

		map.AddTBString(MethodAttr.Mark, null, "功能说明", true, false, 0, 900, 10, true);
		map.SetHelperAlert(MethodAttr.Mark, "对于该功能的描述.");


		//是否显示到列表.
		map.AddBoolean(MethodAttr.IsList, false, "是否显示在列表?", true, true);


		// map.AddTBStringDoc(MethodAttr.Docs, null, "功能说明", true, false, true);
		//    map.AddDDLSysEnum(MethodAttr.WhatAreYouTodo, 0, "执行完毕后干啥？", true, true, MethodAttr.WhatAreYouTodo,
		//   "@0=关闭提示窗口@1=关闭提示窗口并刷新@2=转入到Search.htm页面上去");

		map.AddTBString(MethodAttr.WarningMsg, null, "功能执行警告信息", true, false, 0, 300, 10, true);
		//map.AddDDLSysEnum(MethodAttr.ShowModel, 0, "显示方式", true, true, MethodAttr.ShowModel,
		//  "@0=按钮@1=超链接");

		map.AddDDLSysEnum(MethodAttr.MethodDocTypeOfFunc, 0, "内容类型", true, false, "MethodDocTypeOfFunc", "@0=SQL@1=URL@2=JavaScript@3=业务单元");
		map.AddTBString(MethodAttr.Docs, null, "执行内容", true, false, 0, 300, 10, true);

		map.AddTBString(MethodAttr.MsgSuccess, null, "成功提示信息", true, false, 0, 300, 10, true);
		map.AddTBString(MethodAttr.MsgErr, null, "失败提示信息", true, false, 0, 300, 10, true);


			///#region 外观.
		//  map.AddTBInt(MethodAttr.PopHeight, 100, "弹窗高度", true, false);
		//    map.AddTBInt(MethodAttr.PopWidth, 260, "弹窗宽度", true, false);

			///#endregion 外观.


			///#region 显示位置控制.
		// map.AddBoolean(MethodAttr.IsMyBillToolBar, true, "是否显示在MyBill.htm工具栏上", true, true, true);
		//  map.AddBoolean(MethodAttr.IsMyBillToolExt, false, "是否显示在MyBill.htm工具栏右边的更多按钮里", true, true, true);
		//  map.AddBoolean(MethodAttr.IsSearchBar, false, "是否显示在Search.htm工具栏上(用于批处理)", true, true, true);

			///#endregion 显示位置控制.

		RefMethod rm = new RefMethod();
		//rm.Title = "方法参数"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoParas";
		//rm.Visable = true;
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		//rm.GroupName = "开发接口";
		//  map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "方法内容"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoDocs";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		//rm.GroupName = "开发接口";
		map.AddRefMethod(rm);


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 执行方法.
	/** 
	 方法参数
	 
	 @return 
	*/
	public final String DoParas() {
		return "../../CCBill/Admin/MethodParas.htm?No=" + this.getNo();
	}
	/** 
	 方法内容
	 
	 @return 
	*/
	public final String DoDocs() {
		return "../../CCBill/Admin/MethodDoc/Default.htm?No=" + this.getNo();
	}
	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getNo()) == true)
		{
			this.setNo(DBAccess.GenerGUID(0, null, null));
		}
		return super.beforeInsert();
	}

		///#endregion 执行方法.

}
