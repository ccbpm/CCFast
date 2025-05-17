package bp.ccbill.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 集合方法
*/
public class Collection extends EntityNoName
{

		///#region 基本属性
	/** 
	 表单ID
	*/
	public final String getFrmID() {
		return this.GetValStringByKey(CollectionAttr.FrmID);
	}
	public final void setFrmID(String value)  {
		this.SetValByKey(CollectionAttr.FrmID, value);
	}
	public final String getIcon() {
		return this.GetValStringByKey(CollectionAttr.Icon);
	}
	public final void setIcon(String value)  {
		this.SetValByKey(CollectionAttr.Icon, value);
	}

	/** 
	 方法ID
	*/
	public final String getMethodID() {
		return this.GetValStringByKey(CollectionAttr.MethodID);
	}
	public final void setMethodID(String value)  {
		this.SetValByKey(CollectionAttr.MethodID, value);
	}

	public final String getFlowNo() {
		return this.GetValStringByKey(CollectionAttr.FlowNo);
	}
	public final void setFlowNo(String value)  {
		this.SetValByKey(CollectionAttr.FlowNo, value);
	}
	/** 
	 模式
	*/
	public final String getMethodModel() {
		return this.GetValStringByKey(CollectionAttr.MethodModel);
	}
	public final void setMethodModel(String value)  {
		this.SetValByKey(CollectionAttr.MethodModel, value);
	}
	/** 
	 标记
	*/
	public final String getMark() {
		return this.GetValStringByKey(CollectionAttr.Mark);
	}
	public final void setMark(String value)  {
		this.SetValByKey(CollectionAttr.Mark, value);
	}
	/** 
	 tag1
	*/
	public final String getTag1() {
		return this.GetValStringByKey(CollectionAttr.Tag1);
	}
	public final void setTag1(String value)  {
		this.SetValByKey(CollectionAttr.Tag1, value);
	}
	public final int getIdx() {
		return this.GetValIntByKey(CollectionAttr.Idx);
	}
	public final void setIdx(int value)  {
		this.SetValByKey(CollectionAttr.Idx, value);
	}


		///#endregion


		///#region 构造方法
	/** 
	 集合方法
	*/
	public Collection()
	{
	}
	/** 
	 集合方法
	 
	 @param no
	*/
	public Collection(String no) throws Exception  {
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

		Map map = new Map("Frm_Collection", "集合方法");

		map.AddGroupAttr("基本属性", "");
		//主键.
		map.AddTBStringPK(CollectionAttr.No, null, "编号", true, true, 0, 50, 10);
		map.AddTBString(CollectionAttr.Name, null, "方法名", true, false, 0, 300, 10);
		map.AddTBString(CollectionAttr.MethodID, null, "方法ID", true, true, 0, 300, 10);

		//功能标记. 
		map.AddTBString(CollectionAttr.MethodModel, null, "方法模式", true, true, 0, 300, 10);
		map.AddTBString(CollectionAttr.Tag1, null, "Tag1", true, true, 0, 300, 10);
		map.AddTBString(CollectionAttr.Mark, null, "Mark", true, true, 0, 300, 10);
		map.AddTBString(CollectionAttr.FrmID, null, "表单ID", true, true, 0, 300, 10);
		map.AddTBString(CollectionAttr.FlowNo, null, "流程编号", true, true, 0, 10, 10);
		map.AddTBString(CollectionAttr.Icon, null, "图标", true, false, 0, 50, 10, true);

		//临时存储.
		map.AddTBString(CollectionAttr.Docs, null, "方法内容", true, false, 0, 300, 10);

		//是否启用？
		map.AddBoolean(CollectionAttr.IsEnable, true, "是否启用？", true, true, true);
		map.AddTBInt(CollectionAttr.Idx, 0, "Idx", true, false);

		map.AddDDLSysEnum(MethodAttr.RefMethodType, 0, "页面打开方式", true, true, "RefMethodTypeLink", "@0=模态窗口打开@1=新窗口打开@2=右侧窗口打开@4=转到新页面");


			///#region 外观.
		map.AddGroupAttr("外观", "");
		map.AddTBInt(CollectionAttr.PopHeight, 0, "弹窗高度", true, false);
		map.AddTBInt(CollectionAttr.PopWidth, 0, "弹窗宽度", true, false);

			///#endregion 外观.


			///#region 对功能有效
		map.AddGroupAttr("对功能有效", "");
		//对功能有效.
		map.AddTBString(CollectionAttr.WarningMsg, null, "功能执行警告信息", true, false, 0, 300, 10);
		map.AddTBString(CollectionAttr.MsgSuccess, null, "成功提示信息", true, false, 0, 300, 10, true);
		map.AddTBString(CollectionAttr.MsgErr, null, "失败提示信息", true, false, 0, 300, 10, true);
		map.AddDDLSysEnum(CollectionAttr.WhatAreYouTodo, 0, "执行完毕后干啥？", true, true, CollectionAttr.WhatAreYouTodo, "@0=关闭提示窗口@1=关闭提示窗口并刷新@2=转入到Search.htm页面上去");

			///#endregion 对功能有效

		//增加参数字段.
		map.AddTBAtParas(4000);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 移动.
	public final void DoUp() throws Exception {
		this.DoOrderUp(CollectionAttr.FrmID, this.getFrmID(), CollectionAttr.Idx);
	}
	public final void DoDown() throws Exception
	{
		this.DoOrderDown(CollectionAttr.FrmID, this.getFrmID(), CollectionAttr.Idx);
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
