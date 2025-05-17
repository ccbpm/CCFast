package bp.ccbill.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;

/**
 实体方法
*/
public class Method extends EntityNoName
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
	 方法分组ID
	*/
	public final String getGroupID() {
		return this.GetValStringByKey(MethodAttr.GroupID);
	}
	public final void setGroupID(String value)  {
		this.SetValByKey(MethodAttr.GroupID, value);
	}
	public final String getIcon() {
		return this.GetValStringByKey(MethodAttr.Icon);
	}
	public final void setIcon(String value)  {
		this.SetValByKey(MethodAttr.Icon, value);
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

	public final String getFlowNo() {
		return this.GetValStringByKey(MethodAttr.FlowNo);
	}
	public final void setFlowNo(String value)  {
		this.SetValByKey(MethodAttr.FlowNo, value);
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
	/**
	 模式
	*/
	public final String getMethodModel() {
		return this.GetValStringByKey(MethodAttr.MethodModel);
	}
	public final void setMethodModel(String value)  {
		this.SetValByKey(MethodAttr.MethodModel, value);
	}
	/**
	 标记
	*/
	public final String getMark() {
		return this.GetValStringByKey(MethodAttr.Mark);
	}
	public final void setMark(String value)  {
		this.SetValByKey(MethodAttr.Mark, value);
	}
	/**
	 tag1
	*/
	public final String getTag1() {
		return this.GetValStringByKey(MethodAttr.Tag1);
	}
	public final void setTag1(String value)  {
		this.SetValByKey(MethodAttr.Tag1, value);
	}

		///#endregion


		///#region 构造方法
	/**
	 实体方法
	*/
	public Method()
	{
	}
	/**
	 实体方法

	 @param no
	*/
	public Method(String no) throws Exception  {
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

		Map map = new Map("Frm_Method", "实体方法");

		//主键.
		map.AddGroupAttr("基本属性", "");
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

		////批处理的方法，显示到集合上.
		//map.AddBoolean(MethodAttr.IsCanBatch, false, "是否可以批处理?", true, false);

		//临时存储.
		map.AddTBString(MethodAttr.Docs, null, "方法内容", true, false, 0, 300, 10);

		map.AddDDLSysEnum(MethodAttr.RefMethodType, 0, "方法类型", true, false, MethodAttr.RefMethodType, "@0=功能@1=模态窗口打开@2=新窗口打开@3=右侧窗口打开@4=转到新页面");


			///#region 显示位置控制.
		map.AddGroupAttr("显示位置控制", "");

		map.AddBoolean(MethodAttr.IsMyBillToolBar, true, "是否显示在MyBill.htm工具栏上", true, true, true);
		map.AddBoolean(MethodAttr.IsMyBillToolExt, false, "是否显示在MyBill.htm工具栏右边的更多按钮里", true, true, true);
		map.AddBoolean(MethodAttr.IsSearchBar, false, "是否显示在Search.htm工具栏上(用于批处理)", true, true, true);

			///#endregion 显示位置控制.


			///#region 外观.
		map.AddGroupAttr("外观", "");

		map.AddTBInt(MethodAttr.PopHeight, 0, "弹窗高度", true, false);
		map.AddTBInt(MethodAttr.PopWidth, 0, "弹窗宽度", true, false);

			///#endregion 外观.


			///#region 对功能有效
		map.AddGroupAttr("对功能有效", "");

		//对功能有效.
		map.AddTBString(MethodAttr.WarningMsg, null, "功能执行警告信息", true, false, 0, 300, 10);
		map.AddTBString(MethodAttr.MsgSuccess, null, "成功提示信息", true, false, 0, 300, 10, true);
		map.AddTBString(MethodAttr.MsgErr, null, "失败提示信息", true, false, 0, 300, 10, true);
		map.AddDDLSysEnum(MethodAttr.WhatAreYouTodo, 0, "执行完毕后干啥？", true, true, MethodAttr.WhatAreYouTodo, "@0=关闭提示窗口@1=关闭提示窗口并刷新@2=转入到Search.htm页面上去");
		map.AddDDLSysEnum(MethodAttr.MethodDocTypeOfFunc, 0, "内容类型", true, false, "MethodDocTypeOfFunc", "@0=SQL@1=URL@2=JavaScript@3=业务单元");

			///#endregion 对功能有效


			///#region (流程)相同字段数据同步方式.
		map.AddGroupAttr("相同字段数据同步方式", "");
		map.AddDDLSysEnum(MethodAttr.DTSDataWay, 0, "同步相同字段数据方式", true, true, MethodAttr.DTSDataWay, "@0=不同步@1=同步全部的相同字段的数据@2=同步指定字段的数据");

		map.AddTBString(MethodAttr.DTSSpecFiels, null, "要同步的字段", true, false, 0, 300, 10, true);

		map.AddBoolean(MethodAttr.DTSWhenFlowOver, false, "流程结束后同步？", true, true, true);
		map.AddBoolean(MethodAttr.DTSWhenNodeOver, false, "节点发送成功后同步？", true, true, true);

			///#endregion (流程)相同字段数据同步方式.

		//是否启用？
		map.AddTBInt(MethodAttr.IsEnable, 1, "是否启用？", true, true);
		map.AddTBInt(MethodAttr.IsList, 0, "是否显示在列表?", true, false);
		map.AddTBInt(MethodAttr.IsHavePara, 0, "是否含有参数?", true, false);
		map.AddTBInt(MethodAttr.Idx, 0, "Idx", true, false);
		map.AddTBInt("PrintFileType",0,"打印方式",false,false);
		try{
			map.AddMyFile();
		}catch(Exception e){

		}

		map.AddTBAtParas();
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 移动.
	public final void DoUp() throws Exception {
		this.DoOrderUp(MethodAttr.FrmID, this.getFrmID(), MethodAttr.Idx);
	}
	public final void DoDown() throws Exception
	{
		this.DoOrderDown(MethodAttr.FrmID, this.getFrmID(), MethodAttr.Idx);
	}

		///#endregion 移动.

	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getNo()) == true)
		{
			this.setNo(DBAccess.GenerGUID(0, null, null));
		}
		return super.beforeInsert();
	}
}
