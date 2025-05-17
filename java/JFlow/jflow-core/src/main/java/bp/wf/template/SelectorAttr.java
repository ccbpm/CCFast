package bp.wf.template;


import bp.en.*;

/** 
 Selector属性
*/
public class SelectorAttr extends EntityNoNameAttr
{
	/** 
	 流程
	*/
	public static final String NodeID = "NodeID";
	/** 
	 接受模式
	*/
	public static final String SelectorModel = "SelectorModel";
	/** 
	 选择人分组
	*/
	public static final String SelectorP1 = "SelectorP1";
	/** 
	 操作员
	*/
	public static final String SelectorP2 = "SelectorP2";
	/** 
	 默认选择的数据源
	*/
	public static final String SelectorP3 = "SelectorP3";
	/** 
	 强制选择的数据源
	*/
	public static final String SelectorP4 = "SelectorP4";
	/** 
	 数据显示方式(表格与树)
	*/
	public static final String FK_SQLTemplate = "FK_SQLTemplate";
	/** 
	 是否自动装载上一笔加载的数据
	*/
	public static final String IsAutoLoadEmps = "IsAutoLoadEmps";
	/** 
	 是否单项选择？
	*/
	public static final String IsSimpleSelector = "IsSimpleSelector";
	/** 
	 是否启用部门搜索范围限定
	*/
	public static final String IsEnableDeptRange = "IsEnableDeptRange";
	/** 
	 是否启用角色搜索范围限定
	*/
	public static final String IsEnableStaRange = "IsEnableStaRange";
}
