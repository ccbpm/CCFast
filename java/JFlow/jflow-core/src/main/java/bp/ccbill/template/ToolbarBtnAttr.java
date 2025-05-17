package bp.ccbill.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 实体工具栏按钮属性
*/
public class ToolbarBtnAttr extends EntityMyPKAttr
{

		///#region 基本属性.
	/** 
	 表单ID
	*/
	public static final String FrmID = "FrmID";
	/** 
	 方法ID
	*/
	public static final String BtnID = "BtnID";
	/** 
	 按钮标签
	*/
	public static final String BtnLab = "BtnLab";
	/** 
	 图标
	*/
	public static final String Icon = "Icon";
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

	public static final String IsEnable = "IsEnable";
}
