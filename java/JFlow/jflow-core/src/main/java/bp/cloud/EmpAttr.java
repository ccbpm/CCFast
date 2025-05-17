package bp.cloud;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.port.*;
import bp.*;
import java.util.*;

/** 
 操作员 属性
*/
public class EmpAttr extends bp.en.EntityNoNameAttr
{

		///#region 基本属性
	/** 
	 用户ID
	*/
	public static final String UserID = "UserID";
	/** 
	 部门
	*/
	public static final String FK_Dept = "FK_Dept";
	/** 
	 密码
	*/
	public static final String Pass = "Pass";
	/** 
	 电话
	*/
	public static final String Tel = "Tel";
	/** 
	 邮箱
	*/
	public static final String Email = "Email";
	/** 
	 序号
	*/
	public static final String Idx = "Idx";
	/** 
	 拼音
	*/
	public static final String PinYin = "PinYin";
	public static final String Leader = "Leader";

		///#endregion


	/** 
	 组织结构编码
	*/
	public static final String OrgNo = "OrgNo";
	public static final String OrgName = "OrgName";
	/** 
	 连接的ID
	*/
	public static final String unionid = "unionid";
	public static final String EmpSta = "EmpSta";
}
