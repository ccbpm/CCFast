package bp.ccbill.template;

import bp.da.*;
import bp.en.Map;
import bp.web.*;
import bp.en.*; import bp.en.Map;

/** 
 新建实体流程
*/
public class MethodFlowNewEntity extends EntityNoName
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
	public final String getFlowNo() {
		return this.GetValStringByKey(MethodAttr.FlowNo);
	}
	public final void setFlowNo(String value)  {
		this.SetValByKey(MethodAttr.FlowNo, value);
	}
	public final String getUrlExt() {
		return this.GetValStringByKey("UrlExt");
	}
	public final void setUrlExt(String value)  {
		this.SetValByKey("UrlExt", value);
	}
	/** 
	 是否在流程结束后同步？
	*/
	public final boolean getDTSWhenFlowOver() {
		return this.GetValBooleanByKey(MethodAttr.DTSWhenFlowOver);
	}
	public final void setDTSWhenFlowOver(boolean value)  {
		this.SetValByKey(MethodAttr.DTSWhenFlowOver, value);
	}
	/** 
	 同步的方式
	*/
	public final int getDTSDataWay() {
		return this.GetValIntByKey(MethodAttr.DTSDataWay);
	}
	public final void setDTSDataWay(int value)  {
		this.SetValByKey(MethodAttr.DTSDataWay, value);
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
	 新建实体流程
	*/
	public MethodFlowNewEntity()
	{
	}
	public MethodFlowNewEntity(String no) throws Exception  {
		this.setNo(no);
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

		Map map = new Map("GPM_Menu", "新建实体");

		map.AddTBStringPK(bp.ccfast.ccmenu.MenuAttr.No, null, "编号", true, true, 0, 150, 10);
		map.AddTBString(MethodAttr.FrmID, null, "表单ID", true, true, 0, 300, 10);
		map.AddTBString("FlowNo", null, "流程编号", true, true, 0, 300, 10, false);
		map.AddTBString(MethodAttr.Icon, null, "图标", true, false, 0, 50, 10, true);

		//
		map.AddTBString("UrlExt", null, "链接", false, false, 0, 300, 10, true);


			///#region 显示位置控制.
		map.AddBoolean(MethodAttr.IsMyBillToolBar, true, "是否显示在MyDict.htm工具栏上", true, true, true);
		map.AddBoolean(MethodAttr.IsMyBillToolExt, false, "是否显示在MyDict.htm工具栏右边的更多按钮里", true, true, true);
		map.AddBoolean(MethodAttr.IsSearchBar, false, "是否显示在 SearchDict.htm工具栏上(用于批处理)", true, true, true);

			///#endregion 显示位置控制.


			///#region 相同字段数据同步方式.
		map.AddDDLSysEnum(MethodAttr.DTSDataWay, 0, "同步相同字段数据方式", true, true, MethodAttr.DTSDataWay, "@0=不同步@1=同步全部的相同字段的数据@2=同步指定字段的数据");

		map.AddTBString(MethodAttr.DTSSpecFiels, null, "要同步的字段", true, false, 0, 300, 10, true);

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
		//  map.AddRefMethod(rm);

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

	public final String CreateWorkID() throws Exception {
		long workid = bp.wf.Dev2Interface.Node_CreateBlankWork(this.getFlowNo());

		//给当前的流程实例做标记.
		bp.wf.GenerWorkFlow gwf = new bp.wf.GenerWorkFlow(workid);
		gwf.setPFlowNo(this.getFrmID());
		gwf.SetPara("FlowNewEntity", "1"); //设置标记，等到流程结束后，自动写入到Dict一笔记录.
		gwf.SetPara("MenuNo", this.getNo()); //菜单编号.
		gwf.setPWorkID(gwf.getWorkID()); //实体保存的ID 与 流程ID一致。
		gwf.Update();

		return String.valueOf(workid);
	}


		///#region 执行方法.
	/** 
	 方法参数
	 
	 @return 
	*/
	public final String DoAlert()
	{
		return "您需要转入流程设计器去设计流程.";
		// return "../../CCBill/Admin/MethodParas.htm?No=" + this.MyPK;
	}
	/** 
	 重新导入实体字段
	 
	 @return 
	*/
	public final String ReSetFrm() throws Exception {
		//如果是发起流程的方法，就要表单的字段复制到，流程的表单上去.
		bp.wf.httphandler.WF_Admin_FoolFormDesigner_ImpExp handler = new bp.wf.httphandler.WF_Admin_FoolFormDesigner_ImpExp();
		//   handler.AddPara
		handler.Imp_CopyFrm("ND" + Integer.parseInt(this.getFlowNo() + "01"), this.getFrmID());
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
