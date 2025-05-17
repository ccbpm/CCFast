package bp.wf.port;


/** 
 操作员 属性
*/
public class UserAttr extends bp.en.EntityNoNameAttr
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
	 sid
	*/
	public static final String SID = "Token";
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

		///#endregion


		///#region 组织属性.
	/** 
	 组织结构编码
	*/
	public static final String OrgNo = "OrgNo";
	/** 
	 组织名称
	*/
	public static final String OrgName = "OrgName";
	/** 
	 微信ID
	*/
	public static final String unionid = "unionid";
	public static final String OpenID = "OpenID";
	public static final String OpenID2 = "OpenID2";

		///#endregion 组织属性.


		///#region 权限.
	/** 
	 组织结构编码
	*/
	public static final String IsSaller = "IsSaller";

		///#endregion 组织属性.
}
