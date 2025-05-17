package bp.wf.data;

import bp.en.*;
import bp.*;
import bp.wf.*;
import java.util.*;

/** 
 流程 属性
*/
public class FlowSimpleAttr extends EntityNoNameAttr
{

		///#region 基本属性
	/** 
	 到达人员（可以为空）
	*/
	public static final String ToEmps = "ToEmps";
	public static final String ToEmpOfSQLs = "ToEmpOfSQLs";
	public static final String ToStations = "ToStations";
	public static final String ToDepts = "ToDepts";
	public static final String BeiZhu = "BeiZhu";
	public static final String DTOfExe = "DTOfExe";
	/** 
	 发起时间点
	*/
	public static final String StartDT = "StartDT";
	/** 
	 执行的时间点
	*/
	public static final String Dots = "Dots";

		///#endregion
}