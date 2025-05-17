package bp.wf.template.sflow;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 手工启动子流程属性
*/
public class SubFlowHandGuideAttr extends SubFlowHandAttr
{
	/** 
	 是否启用子流程发起前置导航.
	*/
	public static final String IsSubFlowGuide = "IsSubFlowGuide";
	/** 
	 SQL 前置导航列表
	*/
	public static final String SubFlowGuideSQL = "SubFlowGuideSQL";
	/** 
	 分组的SQL
	*/
	public static final String SubFlowGuideGroup = "SubFlowGuideGroup";
	/** 
	 编号字段.
	*/
	public static final String SubFlowGuideEnNoFiled = "SubFlowGuideEnNoFiled";
	/** 
	 名称字段
	*/
	public static final String SubFlowGuideEnNameFiled = "SubFlowGuideEnNameFiled";
	/** 
	 是否是树形结构
	*/
	public static final String IsTreeConstruct = "IsTreeConstruct";
	/** 
	 父节点编号
	*/
	public static final String ParentNo = "ParentNo";
}
