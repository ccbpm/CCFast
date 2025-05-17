package bp.cloud;


/** 
 组织 属性
*/
public class OrgAttr extends bp.en.EntityNoNameAttr
{
	/** 
	 注册的IP
	*/
	public static final String RegIP = "RegIP";
	/** 
	 状态
	*/
	public static final String OrgSta = "OrgSta";


		///#region 基本属性
	/** 
	 微信分配的永久的qiye ID.
	*/
	public static final String CorpID = "CorpID";
	/** 
	 部门
	*/
	public static final String FK_HY = "FK_HY";
	/** 
	 密码
	*/
	public static final String Pass = "Pass";
	/** 
	 Addr
	*/
	public static final String Addr = "Addr";
	/** 
	 简称
	*/
	public static final String NameFull = "NameFull";
	/** 
	 邮箱
	*/
	public static final String Adminer = "Adminer";
	/** 
	 管理员名称
	*/
	public static final String AdminerName = "AdminerName";
	/** 
	 注册来源
	*/
	public static final String UrlFrom = "UrlFrom";
	public static final String DB = "DB";
	/** 
	 序号
	*/
	public static final String Idx = "Idx";
	/** 
	 拼音
	*/
	public static final String PinYin = "PinYin";
	/** 
	 JSONOfTongJi
	*/
	public static final String JSONOfTongJi = "JSONOfTongJi";
	/** 
	 QQ号
	*/
	public static final String QQ = "QQ";
	/** 
	 GUID
	*/
	public static final String GUID = "GUID";
	/** 
	 注册来源
	*/
	public static final String RegFrom = "RegFrom";
	/** 
	 使用状态
	*/
	public static final String UseSta = "UseSta";
	/** 
	 停用日期
	*/
	public static final String DTEnd = "DTEnd";
	/** 
	 启用日期
	*/
	public static final String DTReg = "DTReg";

	/** 
	 
	*/
	public static final String RDT = "RDT";


	/** 
	 授权方（企业）access_token,最长为512字节
	*/
	public static final String AccessToken = "AccessToken";
	/** 
	 授权方（企业）access_token过期时间，有效期2小时
	*/
	public static final String AccessTokenExpiresIn = "AccessTokenExpiresIn";
	/** 
	 企业微信永久授权码
	*/
	public static final String PermanentCode = "PermanentCode";

	/** 
	 授权方企业方形头像
	*/
	public static final String CorpSquareLogoUrl = "CorpSquareLogoUrl";
	/** 
	 授权方企业圆形头像
	*/
	public static final String CorpRoundLogoUrl = "CorpRoundLogoUrl";
	/** 授权方企业用户规模
	*/
	public static final String CorpUserMax = "CorpUserMax";
	/** 
	 授权方企业应用数上限
	*/
	public static final String CorpAgentMax = "CorpAgentMax";
	/** 
	 授权方企业的主体名称
	*/
	public static final String CorpFullName = "CorpFullName";

	/** 
	 企业类型，1. 企业; 2. 政府以及事业单位; 3. 其他组织, 4.团队号
	*/
	public static final String SubjectType = "SubjectType";
	/** 
	 认证到期时间
	*/
	public static final String VerifiedEndTime = "VerifiedEndTime";
	/** 
	 企业规模。当企业未设置该属性时，值为空
	*/
	public static final String CorpScale = "CorpScale";
	/** 
	 企业所属行业。当企业未设置该属性时，值为空
	*/
	public static final String CorpIndustry = "CorpIndustry";
	/** 
	 企业所属子行业。当企业未设置该属性时，值为空
	*/
	public static final String CorpSubIndustry = "CorpSubIndustry";
	/** 
	 企业所在地信息, 为空时表示未知
	*/
	public static final String Location = "Location";
	/** 
	 授权方应用方形头像
	*/
	public static final String SquareLogoUrl = "SquareLogoUrl";
	/** 
	 授权方应用圆形头像
	*/
	public static final String RoundLogoUl = "RoundLogoUl";
	/** 
	 授权方应用id
	*/
	public static final String AgentId = "AgentId";
	/** 
	 授权方应用名字
	*/
	public static final String AgentName = "AgentName";

	/** 
	 微信应用状态
	*/
	public static final String WXUseSta = "WXUseSta";

		///#endregion
}
