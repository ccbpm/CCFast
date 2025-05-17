package bp.wf.template;

import bp.en.*;

/** 
 流程数据同步 属性
*/
public class SyncDataAttr extends EntityMyPKAttr
{
	/**流程编号
	*/
	public static final String FlowNo = "FlowNo";
	//同步类型.
	public static final String SyncType = "SyncType";
	//数据源ID
	public static final String DBSrc = "DBSrc";
	//数据源
	public static final String DBSrcT = "DBSrcT";
	//API链接URL
	public static final String APIUrl = "APIUrl";
	//备注.
	public static final String Note = "Note";
	//数据表
	public static final String PTable = "PTable";
	//表名
	public static final String PTableName = "PTableName";
	//主键
	public static final String TablePKName = "TablePKName";
	//主键类型
	public static final String TablePKType = "TablePKType";
	//源表单ID
	public static final String FrmID = "FrmID";
	//名称
	public static final String FrmName = "FrmName";
	//查询表
	public static final String SQLTables = "SQLTables";
	//查询字段.
	public static final String SQLFields = "SQLFields";
}
