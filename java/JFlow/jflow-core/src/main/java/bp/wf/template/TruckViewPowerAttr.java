package bp.wf.template;

import bp.en.*;
/** 
 流程轨迹权限属性
*/
public class TruckViewPowerAttr extends EntityNoNameAttr
{

		///#region 权限组.
	/** 
	 发起人可看
	*/
	public static final String PStarter = "PStarter";
	/** 
	 参与人可看
	*/
	public static final String PWorker = "PWorker";
	/** 
	 被抄送人可看
	*/
	public static final String PCCer = "PCCer";
	/** 
	 任何人可见
	*/
	public static final String PAnyOne = "PAnyOne";
	/** 
	 本部门人可看
	*/
	public static final String PMyDept = "PMyDept";
	/** 
	 部门编号
	*/
	public static final String PSpecDeptExt = "PSpecDeptExt";
	/** 
	 直属上级部门可看
	*/
	public static final String PPMyDept = "PPMyDept";
	/** 
	 上级部门可看
	*/
	public static final String PPDept = "PPDept";
	/** 
	 平级部门可看
	*/
	public static final String PSameDept = "PSameDept";
	/** 
	 指定部门可看
	*/
	public static final String PSpecDept = "PSpecDept";
	/** 
	 指定的角色可看
	*/
	public static final String PSpecSta = "PSpecSta";
	/** 
	 角色编号
	*/
	public static final String PSpecStaExt = "PSpecStaExt";

	/** 
	 指定的权限组可看
	*/
	public static final String PSpecGroup = "PSpecGroup";
	/** 
	 指定的权限组编号
	*/
	public static final String PSpecGroupExt = "PSpecGroupExt";
	/** 
	 指定的人员可看
	*/
	public static final String PSpecEmp = "PSpecEmp";
	/** 
	 人员编号
	*/
	public static final String PSpecEmpExt = "PSpecEmpExt";

		///#endregion 权限组.
}
