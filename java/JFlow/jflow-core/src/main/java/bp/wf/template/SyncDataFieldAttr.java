package bp.wf.template;

import bp.en.*;

/** 
 流程数据同步 属性
*/
public class SyncDataFieldAttr extends EntityMyPKAttr
{
	/**流程编号
	*/
	public static final String FlowNo = "FlowNo";
	//同步类型.
	public static final String RefPKVal = "RefPKVal";
	//数据源
	public static final String AttrKey = "AttrKey";
	public static final String AttrName = "AttrName";
	public static final String AttrType = "AttrType";

	//备注.
	public static final String ToFieldKey = "ToFieldKey";
	public static final String ToFieldName = "ToFieldName";
	public static final String ToFieldType = "ToFieldType";

	//使用的转换函数
	public static final String TurnFunc = "TurnFunc";
	//是否同步?
	public static final String IsSync = "IsSync";
}
