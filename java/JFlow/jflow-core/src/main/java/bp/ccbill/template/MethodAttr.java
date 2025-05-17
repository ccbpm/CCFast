package bp.ccbill.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 实体方法属性
*/
public class MethodAttr extends EntityNoNameAttr
{

		///#region 基本属性.
	/** 
	 表单ID
	*/
	public static final String FrmID = "FrmID";

	/** 
	 分组ID
	*/
	public static final String GroupID = "GroupID";
	/** 
	 方法ID
	*/
	public static final String MethodID = "MethodID";
	/** 
	 图标
	*/
	public static final String Icon = "Icon";
	/** 
	 方法类型
	*/
	public static final String RefMethodType = "RefMethodType";
	/** 
	 方法打开模式
	*/
	public static final String MethodModel = "MethodModel";
	/** 
	 标记
	*/
	public static final String Mark = "Mark";
	/** 
	 tag
	*/
	public static final String Tag1 = "Tag1";
	/** 
	 显示方式.
	*/
	public static final String ShowModel = "ShowModel";
	/** 
	 处理内容
	*/
	public static final String MethodDoc_Url = "MethodDoc_Url";
	/** 
	 内容类型
	*/
	public static final String MethodDocTypeOfFunc = "MethodDocTypeOfFunc";
	/** 
	 处理内容s
	*/
	public static final String Docs = "Docs";

	/** 
	 执行警告信息-对功能方法有效
	*/
	public static final String WarningMsg = "WarningMsg";
	/** 
	 成功提示信息
	*/
	public static final String MsgSuccess = "MsgSuccess";
	/** 
	 失败提示信息
	*/
	public static final String MsgErr = "MsgErr";
	/** 
	 执行完毕后干啥？
	*/
	public static final String WhatAreYouTodo = "WhatAreYouTodo";
	/** 
	 Idx
	*/
	public static final String Idx = "Idx";

		///#endregion 基本属性.


		///#region 外观.
	/** 
	 宽度.
	*/
	public static final String PopWidth = "PopWidth";
	/** 
	 高度
	*/
	public static final String PopHeight = "PopHeight";

		///#endregion 外观.


		///#region 显示位置
	/** 
	 是否显示myToolBar工具栏上.
	*/
	public static final String IsMyBillToolBar = "IsMyBillToolBar";
	/** 
	 显示在工具栏更多按钮里.
	*/
	public static final String IsMyBillToolExt = "IsMyBillToolExt";
	/** 
	 显示在查询列表工具栏目上，用于执行批处理.
	*/
	public static final String IsSearchBar = "IsSearchBar";

		///#endregion 显示位置


		///#region 流程方法相关.
	/** 
	 流程结束后是否同步字段?
	*/
	public static final String DTSDataWay = "DTSDataWay";
	public static final String DTSSpecFiels = "DTSSpecFiels";
	public static final String DTSWhenFlowOver = "DTSWhenFlowOver";
	public static final String DTSWhenNodeOver = "DTSWhenNodeOver";
	public static final String FlowNo = "FlowNo";

		///#endregion 流程方法相关.

	public static final String IsEnable = "IsEnable";
	/** 
	 是否显示在列表？
	*/
	public static final String IsList = "IsList";
	/** 
	 是否含有参数
	*/
	public static final String IsHavePara = "IsHavePara";
}
