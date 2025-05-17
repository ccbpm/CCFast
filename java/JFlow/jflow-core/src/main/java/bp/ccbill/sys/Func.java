package bp.ccbill.sys;

import bp.da.*;
import bp.en.Map;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.ccbill.template.*;

/** 
 独立方法
*/
public class Func extends EntityNoName
{

		///#region 基本属性

	/** 
	 方法ID - 不是主键
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
	public final String getMethodDocJavaScriptDemo()
	{
		String file = bp.difference.SystemConfig.getCCFlowAppPath() + "WF/CCBill/Admin/MethodDoc/MethodDocDemoJS.txt";
		String doc = DataType.ReadTextFile(file); //读取文件.
		doc = doc.replace("/#", "+"); //为什么？
		doc = doc.replace("/$", "-"); //为什么？

		//  doc = doc.replace("@FrmID", this.FrmID);

		return doc;
	}
	public final String getMethodDocSQLDemo()
	{
		String file = bp.difference.SystemConfig.getCCFlowAppPath() + "WF/CCBill/Admin/MethodDoc/MethodDocDemoSQL.txt";
		String doc = DataType.ReadTextFile(file); //读取文件.
												  //  doc = doc.replace("@FrmID", this.FrmID);
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
	 独立方法
	*/
	public Func()
	{
	}
	public Func(String no) throws Exception  {
		this.setNo(no);
		this.Retrieve();
	}
	/**
	 * 重写基类方法
	 */
	@Override
	public Map getEnMap(){
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Frm_Func", "功能");

		map.AddTBStringPK(FuncAttr.No, null, "编号", true, true, 0, 50, 10);
		map.AddTBString(FuncAttr.Name, null, "方法名", true, false, 0, 300, 10, true);

		map.AddTBString(FuncAttr.FuncID, null, "方法ID", true, false, 0, 300, 10, true);
		map.AddTBString(FuncAttr.Icon, null, "图标", true, false, 0, 50, 10, true);

		map.AddDDLSysEnum(FuncAttr.FuncSrc, 0, "功能来源", true, false, "FuncSrc", "@0=自定义@1=系统内置");
		map.AddTBString(FuncAttr.DTSName, null, "功能内容", true, false, 0, 300, 10, true);



		map.AddTBStringDoc(FuncAttr.Docs, null, "功能说明", true, false, true, 10);
		map.SetHelperAlert(FuncAttr.Docs, "对于该功能的描述.");

		map.AddTBString(FuncAttr.WarningMsg, null, "独立方法警告信息", true, false, 0, 300, 10, true);
		map.AddDDLSysEnum(FuncAttr.MethodDocTypeOfFunc, 0, "内容类型", true, false, "MethodDocTypeOfFunc", "@0=SQL@1=URL@2=JavaScript@3=业务单元");

		map.AddTBString(FuncAttr.MethodDoc_Url, null, "URL执行内容", false, false, 0, 300, 10);
		map.AddTBString(FuncAttr.MsgSuccess, null, "成功提示信息", true, false, 0, 300, 10, true);
		map.AddTBString(FuncAttr.MsgErr, null, "失败提示信息", true, false, 0, 300, 10, true);
		map.AddTBInt(FuncAttr.IsHavePara, 0, "是否含有参数?", true, false);

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
	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setNo(DBAccess.GenerGUID(0, null, null));
		return super.beforeInsert();
	}
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
		return "../../CCBill/Admin/MethodDocSys/Default.htm?No=" + this.getNo();
	}

		///#endregion 执行方法.
}
