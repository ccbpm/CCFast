package bp.port;

import bp.da.*;
import bp.difference.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.web.*;
import bp.*;
import java.util.*;
import java.io.*;

/** 
 操作员属性
*/
public class EmpAttr extends EntityNoNameAttr
{

		///#region 基本属性
	/** 
	 UserID
	*/
	public static final String UserID = "UserID";
	/** 
	 组织编号
	*/
	public static final String OrgNo = "OrgNo";
	/** 
	 部门
	*/
	public static final String FK_Dept = "FK_Dept";
	/** 
	 手机号
	*/
	public static final String Tel = "Tel";
	/** 
	 邮箱
	*/
	public static final String Email = "Email";
	/** 
	 直属部门领导
	*/
	public static final String Leader = "Leader";
	/** 
	 部门领导编号
	*/
	public static final String LeaderName = "LeaderName";
	/** 
	 排序
	*/
	public static final String Idx = "Idx";
	/** 
	 检索拼音
	*/
	public static final String PinYin = "PinYin";

	public static final String EmpSta = "EmpSta";
	/** 
	 是否是联络员
	*/
	//public const String IsOfficer = "IsOfficer";


		///#endregion
}
