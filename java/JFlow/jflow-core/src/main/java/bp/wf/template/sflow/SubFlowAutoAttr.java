package bp.wf.template.sflow;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 自动触发子流程属性
*/
public class SubFlowAutoAttr extends SubFlowAttr
{

	/** 
	 当前节点为子流程时，所有子流程完成后启动他的同级子流程自动运行或者结束
	*/
	public static final String IsAutoSendSLSubFlowOver = "IsAutoSendSLSubFlowOver";
}
