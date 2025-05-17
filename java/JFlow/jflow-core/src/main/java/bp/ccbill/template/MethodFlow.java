package bp.ccbill.template;

import bp.da.*;
import bp.en.Map;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;

/** 
 执行流程
*/
public class MethodFlow extends EntityNoName
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
		MapAttrs attrs = new MapAttrs(this.getNo());
		for (MapAttr item : attrs.ToJavaList())
		{
			paras += item.getKeyOfEn() + ",";
		}
		if (attrs.size() > 1)
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
	 执行流程
	*/
	public MethodFlow()
	{
	}
	public MethodFlow(String mypk) throws Exception {
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
		map.AddTBString(MethodAttr.Mark, null, "Mark", true, true, 0, 300, 10);


		map.AddTBString(MethodAttr.FrmID, null, "表单ID", true, true, 0, 300, 10);
		map.AddTBString(MethodAttr.FlowNo, null, "流程编号", true, true, 0, 10, 10);


		map.AddTBString(MethodAttr.Icon, null, "图标", true, false, 0, 50, 10, true);

		//  map.AddTBString(MethodAttr.WarningMsg, null, "执行流程警告信息", true, false, 0, 300, 10, true);
		map.AddDDLSysEnum(MethodAttr.ShowModel, 0, "显示方式", true, true, MethodAttr.ShowModel, "@0=按钮@1=超链接");

		// map.AddDDLSysEnum(MethodAttr.MethodDocTypeOfFunc, 0, "内容类型", true, false, "MethodDocTypeOfFunc",
		//"@0=SQL@1=URL@2=JavaScript@3=业务单元");
		// map.AddTBString(MethodAttr.MethodDoc_Url, null, "URL执行内容", false, false, 0, 300, 10);
		//   map.AddTBString(MethodAttr.MsgSuccess, null, "成功提示信息", true, false, 0, 300, 10, true);
		//    map.AddTBString(MethodAttr.MsgErr, null, "失败提示信息", true, false, 0, 300, 10, true);


			///#region 外观.
		map.AddTBInt(MethodAttr.PopHeight, 100, "弹窗高度", true, false);
		map.AddTBInt(MethodAttr.PopWidth, 260, "弹窗宽度", true, false);

			///#endregion 外观.


			///#region 显示位置控制.
		map.AddBoolean(MethodAttr.IsMyBillToolBar, true, "是否显示在MyBill.htm工具栏上", true, true, true);
		map.AddBoolean(MethodAttr.IsMyBillToolExt, false, "是否显示在MyBill.htm工具栏右边的更多按钮里", true, true, true);
		map.AddBoolean(MethodAttr.IsSearchBar, false, "是否显示在Search.htm工具栏上(用于批处理)", true, true, true);

			///#endregion 显示位置控制.


			///#region 相同字段数据同步方式.
		map.AddDDLSysEnum(MethodAttr.DTSDataWay, 0, "同步相同字段数据方式", true, true, MethodAttr.DTSDataWay, "@0=不同步@1=同步全部的相同字段的数据@2=同步指定字段的数据");

		map.AddTBString(MethodAttr.DTSSpecFiels, null, "要同步的字段", true, false, 0, 300, 10,true);

		map.AddBoolean(MethodAttr.DTSWhenFlowOver, false, "流程结束后同步？", true, true, true);
		map.AddBoolean(MethodAttr.DTSWhenNodeOver, false, "节点发送成功后同步？", true, true, true);

			///#endregion 相同字段数据同步方式.


		RefMethod rm = new RefMethod();
		rm.Title = "设计流程"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoAlert";
		rm.Warning = "";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.Func;
		//rm.GroupName = "开发接口";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "重新导入实体字段"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".ReSetFrm";
		rm.Warning = "现有的表单字段将会被清除，重新导入的字段会被增加上去，数据不会变化，导入需慎重。";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.Func;
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
	public final String DoAlert()
	{
		return "您需要转入流程设计器去设计流程.";
		// return "../../CCBill/Admin/MethodParas.htm?No=" + this.No;
	}
	/** 
	 重新导入实体字段
	 
	 @return 
	*/
	public final String ReSetFrm() throws Exception {
		//如果是发起流程的方法，就要表单的字段复制到，流程的表单上去.
		bp.wf.httphandler.WF_Admin_FoolFormDesigner_ImpExp handler = new bp.wf.httphandler.WF_Admin_FoolFormDesigner_ImpExp();
		//   handler.AddPara
		handler.Imp_CopyFrm("ND" + Integer.parseInt(this.getMethodID() + "01"), this.getFrmID());
		return "执行成功，您需要转入流程设计器查看表单.";

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
