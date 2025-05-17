package bp.sys;

import bp.en.*;

/** 
 映射基础
*/
public class MapDataAttr extends EntityNoNameAttr
{
	/** 
	 表单事件实体类
	*/
	public static final String FormEventEntity = "FormEventEntity";
	/** 
	 存储表
	*/
	public static final String PTable = "PTable";
	/** 
	 表存储格式0=自定义表,1=指定表,可以修改字段2=执行表不可以修改字段.
	*/
	public static final String PTableModel = "PTableModel";
	/** 
	 从表数量
	*/
	public static final String Dtls = "Dtls";
	/** 
	 实体主键
	*/
	public static final String EnPK = "EnPK";
	/** 
	 宽度
	*/
	public static final String FrmW = "FrmW";
	/** 
	 表格列(对经典表单有效)
	*/
	public static final String TableCol = "TableCol";
	/** 
	 来源
	*/
	public static final String FrmFrom = "FrmFrom";
	/** 
	 设计者
	*/
	public static final String Designer = "Designer";
	/** 
	 设计者单位
	*/
	public static final String DesignerUnit = "DesignerUnit";
	/** 
	 设计者联系方式
	*/
	public static final String DesignerContact = "DesignerContact";
	/** 
	 设计器
	*/
	public static final String DesignerTool11 = "DesignerTool";
	/** 
	 表单树类别
	*/
	public static final String FK_FormTree = "FK_FormTree";
	/** 
	 表单类型
	*/
	public static final String FrmType = "FrmType";
	/** 
	 业务类型
	*/
	public static final String EntityType = "EntityType";
	/** 
	 表单展示方式
	*/
	public static final String FrmShowType = "FrmShowType";
	/** 
	 单据模板
	*/
	public static final String FrmModel = "FrmModel";
	/** 
	 Url(对于嵌入式表单有效)
	*/
	public static final String UrlExt = "UrlExt";
	/** 
	 Tag
	*/
	public static final String Tag = "Tag";
	/** 
	 备注
	*/
	public static final String Note = "Note";
	/** 
	 Idx
	*/
	public static final String Idx = "Idx";
	/** 
	 GUID
	*/
	public static final String GUID = "GUID";
	/** 
	 版本号
	*/
	public static final String Ver = "Ver";
	/** 
	 应用类型
	*/
	public static final String AppType = "AppType";
	/** 
	 表单body属性.
	*/
	public static final String BodyAttr = "BodyAttr";
	/** 
	 流程控件
	*/
	public static final String FlowCtrls = "FlowCtrls";
	/** 
	组织结构.
	*/
	public static final String OrgNo = "OrgNo";
	/** 
	 Icon.
	*/
	public static final String Icon = "Icon";


		///#region DBList类型的实体.
	/** 
	 数据源
	*/
	public static final String DBSrc = "DBSrc";
	/** 
	 数据源类型
	*/
	public static final String DBType = "DBType";
	/** 
	 单行
	*/
	public static final String ExpEn = "ExpEn";
	/** 
	 列表
	*/
	public static final String ExpList = "ExpList";
	/** 
	 表达式
	*/
	public static final String ExpCount = "ExpCount";
	/** 
	 分页的模式
	*/
	public static final String ExpListPageModel = "ExpListPageModel";

		///#endregion DBList类型的实体.


		///#region 报表属性(参数的方式存储).
	/** 
	 是否关键字查询
	*/
	public static final String IsSearchKey = "IsSearchKey";
	/** 
	 时间段查询方式
	*/
	public static final String DTSearchWay = "DTSearchWay";
	/** 
	 时间字段
	*/
	public static final String DTSearchKey = "DTSearchKey";
	/** 
	 查询外键枚举字段
	*/
	public static final String RptSearchKeys = "RptSearchKeys";

		///#endregion 报表属性(参数的方式存储).


		///#region 其他计算属性，参数存储.
	/** 
	 最左边的值
	*/
	public static final String MaxLeft = "MaxLeft";
	/** 
	 最右边的值
	*/
	public static final String MaxRight = "MaxRight";
	/** 
	 最头部的值
	*/
	public static final String MaxTop = "MaxTop";
	/** 
	 最底部的值
	*/
	public static final String MaxEnd = "MaxEnd";

		///#endregion 其他计算属性，参数存储.


		///#region 参数属性.
	public static final String EnsName = "EnsName";
	/** 
	 是否是加密
	*/
	public static final String IsJM = "IsJM";

		///#endregion 参数属性.
}
