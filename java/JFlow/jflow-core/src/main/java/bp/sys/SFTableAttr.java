package bp.sys;

import bp.en.*;
/** 
 用户自定义表
*/
public class SFTableAttr extends EntityNoNameAttr
{
	/** 
	 是否可以删除
	*/
	public static final String IsDel = "IsDel";
	/** 
	 字段
	*/
	public static final String FK_Val = "FK_Val";
	/** 
	 数据表描述
	*/
	public static final String TableDesc = "TableDesc";
	/** 
	 默认值
	*/
	public static final String DefVal = "DefVal";
	/** 
	 数据源
	*/
	public static final String DBSrc = "DBSrc";
	/** 
	 是否是树
	*/
	public static final String IsTree = "IsTree";
	/** 
	 表类型
	*/
	public static final String DictSrcType = "DBSrcType";
	/** 
	 字典表类型
	*/
	public static final String CodeStruct = "CodeStruct";
	/** 
	 是否自动生成编号
	*/
	public static final String IsAutoGenerNo = "IsAutoGenerNo";
	/** 
	 编号生成规则
	*/
	public static final String NoGenerModel = "NoGenerModel";


		///#region 链接到其他系统获取数据的属性。
	/** 
	 数据源
	*/
	public static final String FK_SFDBSrc = "FK_SFDBSrc";
	/** 
	 数据源表
	*/
	public static final String SrcTable = "SrcTable";
	/** 
	 显示的值
	*/
	public static final String ColumnValue = "ColumnValue";
	/** 
	 显示的文字
	*/
	public static final String ColumnText = "ColumnText";
	/** 
	 父结点值
	*/
	public static final String ParentValue = "ParentValue";
	/** 
	 查询语句
	*/
	public static final String SelectStatement = "SelectStatement";
	/** 
	 缓存分钟数
	*/
	public static final String CacheMinute = "CacheMinute";
	/** 
	 最近缓存的时间
	*/
	public static final String RootVal = "RootVal";
	/** 
	 加入日期
	*/
	public static final String RDT = "RDT";
	/** 
	 组织编号
	*/
	public static final String OrgNo = "OrgNo";

		///#endregion 链接到其他系统获取数据的属性。
	/** 
	 AtPara
	*/
	public static final String AtPara = "AtPara";
}
