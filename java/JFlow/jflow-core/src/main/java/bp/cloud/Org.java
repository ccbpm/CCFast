package bp.cloud;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.port.*;
import java.util.*;

/** 
 组织 的摘要说明。
*/
public class Org extends EntityNoName
{

		///#region 扩展属性
	/** 
	 该人员是否被禁用.
	*/
	public final boolean getItIsEnable() throws Exception {
		if (Objects.equals(this.getNo(), "admin"))
		{
			return true;
		}

		String sql = "SELECT COUNT(FK_Org) FROM Port_DeptOrgStation WHERE FK_Org='" + this.getNo() + "'";
		if (DBAccess.RunSQLReturnValInt(sql, 0) == 0)
		{
			return false;
		}

		sql = "SELECT COUNT(FK_Org) FROM Port_DeptOrg WHERE FK_Org='" + this.getNo() + "'";
		if (DBAccess.RunSQLReturnValInt(sql, 0) == 0)
		{
			return false;
		}

		return true;
	}
	public final String getDB() {
		return this.GetValStringByKey(OrgAttr.DB);
	}
	public final void setDB(String value)  {
		this.SetValByKey(OrgAttr.DB, value);
	}
	/** 
	 网站来源
	*/
	public final String getUrlFrom() {
		return this.GetValStringByKey(OrgAttr.UrlFrom);
	}
	public final void setUrlFrom(String value)  {
		this.SetValByKey(OrgAttr.UrlFrom, value);
	}
	/** 
	 注册来源 0=网站， 1=微信, 2=钉钉.
	*/
	public final int getRegFrom() throws Exception {

		return this.GetValIntByKey(OrgAttr.RegFrom);
	}
	public final void setRegFrom(int value)  {
		this.SetValByKey(OrgAttr.RegFrom, value);
	}
	/** 
	 注册的IP
	*/
	public final String getRegIP() throws Exception {

		return this.GetValStrByKey(OrgAttr.RegIP);
	}
	public final void setRegIP(String value)  {
		this.SetValByKey(OrgAttr.RegIP, value);
	}
	public final String getDTReg() throws Exception {

		return this.GetValStrByKey(OrgAttr.DTReg);
	}
	public final void setDTReg(String value)  {
		this.SetValByKey(OrgAttr.DTReg, value);
	}
	public final String getDTEnd() throws Exception {

		return this.GetValStrByKey(OrgAttr.DTEnd);
	}
	public final void setDTEnd(String value)  {
		this.SetValByKey(OrgAttr.DTEnd, value);
	}
	public final String getAddr() throws Exception {

		return this.GetValStrByKey(OrgAttr.Addr);
	}
	public final void setAddr(String value)  {
		this.SetValByKey(OrgAttr.Addr, value);
	}
	public final String getGUID()  {
		return this.GetValStrByKey(OrgAttr.GUID);
	}

	/** 
	 拼音
	*/
	public final String getAdminer()  {
		return this.GetValStrByKey(OrgAttr.Adminer);
	}
	public final void setAdminer(String value)  {
		this.SetValByKey(OrgAttr.Adminer, value);
	}
	public final String getAdminerName()  {
		return this.GetValStrByKey(OrgAttr.AdminerName);
	}
	public final void setAdminerName(String value)  {
		this.SetValByKey(OrgAttr.AdminerName, value);
	}
	/** 
	 全名
	*/
	public final String getNameFull()  {
		return this.GetValStrByKey(OrgAttr.NameFull);
	}
	public final void setNameFull(String value)  {
		this.SetValByKey(OrgAttr.NameFull, value);
	}
	/** 
	 统计用的JSON
	*/
	public final String getJSONOfTongJi()  {
		return this.GetValStrByKey(OrgAttr.JSONOfTongJi);
	}
	public final void setJSONOfTongJi(String value)  {
		this.SetValByKey(OrgAttr.JSONOfTongJi, value);
	}
	/** 
	 注册年月
	*/
	public final String getFkHy()  {
		return this.GetValStrByKey(OrgAttr.FK_HY);
	}
	public final void setFkHy(String value)  {
		this.SetValByKey(OrgAttr.FK_HY, value);
	}
	/** 
	 拼音
	*/
	public final String getPinYin()  {
		return this.GetValStrByKey(EmpAttr.PinYin);
	}
	public final void setPinYin(String value)  {
		this.SetValByKey(EmpAttr.PinYin, value);
	}
	/** 
	 使用状态0=未安装,1=使用中,2=卸载
	*/
	public final int getWXUseSta() {
		return this.GetValIntByKey(OrgAttr.WXUseSta);
	}
	public final void setWXUseSta(int value)  {
		this.SetValByKey(OrgAttr.WXUseSta, value);
	}
	/** 
	 企业ID
	*/
	public final String getCorpID()  {
		return this.GetValStrByKey(OrgAttr.CorpID);
	}
	public final void setCorpID(String value)  {
		this.SetValByKey(OrgAttr.CorpID, value);
	}
	public final String getRDT()  {
		return this.GetValStrByKey(OrgAttr.RDT);
	}
	public final void setRDT(String value)  {
		this.SetValByKey(OrgAttr.RDT, value);
	}

	/** 
	 
	*/
	public final String getAccessToken()  {
		return this.GetValStrByKey(OrgAttr.AccessToken);
	}
	public final void setAccessToken(String value)  {
		this.SetValByKey(OrgAttr.AccessToken, value);
	}
	public final String getAccessTokenExpiresIn()  {
		return this.GetValStrByKey(OrgAttr.AccessTokenExpiresIn);
	}
	public final void setAccessTokenExpiresIn(String value)  {
		this.SetValByKey(OrgAttr.AccessTokenExpiresIn, value);
	}

	public final String getPermanentCode()  {
		return this.GetValStrByKey(OrgAttr.PermanentCode);
	}
	public final void setPermanentCode(String value)  {
		this.SetValByKey(OrgAttr.PermanentCode, value);
	}
	public final String getAgentId()  {
		return this.GetValStrByKey(OrgAttr.AgentId);
	}
	public final void setAgentId(String value)  {
		this.SetValByKey(OrgAttr.AgentId, value);
	}
	public final String getAgentName()  {
		return this.GetValStrByKey(OrgAttr.AgentName);
	}
	public final void setAgentName(String value)  {
		this.SetValByKey(OrgAttr.AgentName, value);
	}

	public final String getCorpSquareLogoUrl()  {
		return this.GetValStrByKey(OrgAttr.CorpSquareLogoUrl);
	}
	public final void setCorpSquareLogoUrl(String value)  {
		this.SetValByKey(OrgAttr.CorpSquareLogoUrl, value);
	}

	public final String getCorpRoundLogoUrl()  {
		return this.GetValStrByKey(OrgAttr.CorpRoundLogoUrl);
	}
	public final void setCorpRoundLogoUrl(String value)  {
		this.SetValByKey(OrgAttr.CorpRoundLogoUrl, value);
	}

	public final String getCorpUserMax()  {
		return this.GetValStrByKey(OrgAttr.CorpUserMax);
	}
	public final void setCorpUserMax(String value)  {
		this.SetValByKey(OrgAttr.CorpUserMax, value);
	}

	public final String getCorpAgentMax()  {
		return this.GetValStrByKey(OrgAttr.CorpAgentMax);
	}
	public final void setCorpAgentMax(String value)  {
		this.SetValByKey(OrgAttr.CorpAgentMax, value);
	}

	public final String getCorpFullName()  {
		return this.GetValStrByKey(OrgAttr.CorpFullName);
	}
	public final void setCorpFullName(String value)  {
		this.SetValByKey(OrgAttr.CorpFullName, value);
	}

	public final String getSubjectType()  {
		return this.GetValStrByKey(OrgAttr.SubjectType);
	}
	public final void setSubjectType(String value)  {
		this.SetValByKey(OrgAttr.SubjectType, value);
	}

	public final String getVerifiedEndTime()  {
		return this.GetValStrByKey(OrgAttr.VerifiedEndTime);
	}
	public final void setVerifiedEndTime(String value)  {
		this.SetValByKey(OrgAttr.VerifiedEndTime, value);
	}

	public final String getCorpScale()  {
		return this.GetValStrByKey(OrgAttr.CorpScale);
	}
	public final void setCorpScale(String value)  {
		this.SetValByKey(OrgAttr.CorpScale, value);
	}

	public final String getCorpIndustry()  {
		return this.GetValStrByKey(OrgAttr.CorpIndustry);
	}
	public final void setCorpIndustry(String value)  {
		this.SetValByKey(OrgAttr.CorpIndustry, value);
	}

	public final String getCorpSubIndustry()  {
		return this.GetValStrByKey(OrgAttr.CorpSubIndustry);
	}
	public final void setCorpSubIndustry(String value)  {
		this.SetValByKey(OrgAttr.CorpSubIndustry, value);
	}

	public final String getLocation()  {
		return this.GetValStrByKey(OrgAttr.Location);
	}
	public final void setLocation(String value)  {
		this.SetValByKey(OrgAttr.Location, value);
	}

	public final String getSquareLogoUrl()  {
		return this.GetValStrByKey(OrgAttr.SquareLogoUrl);
	}
	public final void setSquareLogoUrl(String value)  {
		this.SetValByKey(OrgAttr.SquareLogoUrl, value);
	}
	public final String getRoundLogoUl()  {
		return this.GetValStrByKey(OrgAttr.RoundLogoUl);
	}
	public final void setRoundLogoUl(String value)  {
		this.SetValByKey(OrgAttr.RoundLogoUl, value);
	}

		///#endregion


		///#region 公共方法
	/** 
	 检查密码(可以重写此方法)
	 
	 @param pass 密码
	 @return 是否匹配成功
	*/
	public final boolean CheckPass(String pass)
	{
		//if (this.Pass == pass)
		//    return true;
		return false;
	}

		///#endregion 公共方法


		///#region 构造函数
	/** 
	 组织
	*/
	public Org()
	{
	}
	public Org(String no) throws Exception  {
		this.setNo(no);
		this.Retrieve();
	}
	/** 
	 权限
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForAppAdmin();
		return uac;
	}
	/**
	 * 重写基类方法
	 */
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Port_Org", "组织");
		map.setEnType(EnType.App);
		map.IndexField = OrgAttr.FK_HY;


			///#region 字段
		/*关于字段属性的增加 */
		map.AddTBStringPK(OrgAttr.No, null, "账号OrgNo", true, false, 1, 50, 90);

		map.AddTBString(OrgAttr.CorpID, null, "CorpID", true, false, 1, 50, 90);

		map.AddTBString(OrgAttr.Name, null, "简称", true, false, 0, 200, 130);
		map.AddTBString(OrgAttr.NameFull, null, "全称", true, false, 0, 300, 400);

		map.AddTBString(OrgAttr.Adminer, null, "管理员帐号", true, true, 0, 300, 400);
		map.AddTBString(OrgAttr.AdminerName, null, "管理员", true, true, 0, 300, 400);

		map.AddTBString(OrgAttr.Addr, null, "地址", true, false, 0, 300, 36);
		map.AddTBString(OrgAttr.GUID, null, "GUID", true, false, 0, 32, 36);

		map.AddTBDateTime(OrgAttr.DTReg, null, "起始日期", true, false);
		map.AddTBDateTime(OrgAttr.DTEnd, null, "停用日期", true, false);
		map.AddTBString(OrgAttr.RegIP, null, "注册的IP", true, false, 0, 30, 20);
		map.AddTBDateTime(OrgAttr.RDT, null, "注册日期", true, false);

		//@0=网站注册@1=企业微信@2=钉钉@3=微信小程序
		map.AddTBInt(OrgAttr.RegFrom, 0, "注册来源", true, false);
		map.AddTBInt(OrgAttr.WXUseSta, 0, "状态@0=未安装1=使用中@2=卸载.", true, false);

		map.AddTBString(OrgAttr.UrlFrom, null, "网站来源", true, true, 0, 30, 20);
		map.AddTBString(OrgAttr.DB, null, "DB", true, false, 0, 32, 36);

			///#endregion 字段


			///#region 微信信息
		map.AddTBString(OrgAttr.CorpID, null, "授权方企业微信id", true, false, 0, 200, 36);
		map.AddTBString(OrgAttr.AgentName, null, "授权方企业名称，即企业简称", true, false, 0, 200, 36);
		map.AddTBString(OrgAttr.AgentId, null, "授权方应用id", true, false, 0, 200, 36);
		map.AddTBString(OrgAttr.PermanentCode, null, "企业微信永久授权码", true, false, 0, 200, 36);
		map.AddTBString(OrgAttr.AccessToken, null, "授权方（企业）access_token", true, false, 0, 200, 36);

		map.AddTBDateTime(OrgAttr.AccessTokenExpiresIn, null, "授权方（企业）access_token失效时间", true, false);

		//map.AddTBString(OrgAttr.CorpSquareLogoUrl, null, "授权方企业方形头像", true, false, 0, 4000, 36);
		//map.AddTBString(OrgAttr.CorpRoundLogoUrl, null, "授权方企业圆形头像", true, false, 0, 4000, 36);
		map.AddTBString(OrgAttr.CorpUserMax, null, "授权方企业用户规模", true, false, 0, 4000, 36);
		map.AddTBString(OrgAttr.CorpAgentMax, null, "授权方企业应用数上限", true, false, 0, 4000, 36);
		map.AddTBString(OrgAttr.CorpFullName, null, "授权方企业的主体名称", true, false, 0, 4000, 36);
		map.AddTBString(OrgAttr.SubjectType, null, "企业类型，1. 企业; 2. 政府以及事业单位; 3. 其他组织, 4.团队号", true, false, 0, 4000, 36);
		map.AddTBString(OrgAttr.VerifiedEndTime, null, "认证到期时间", true, false, 0, 4000, 36);
		map.AddTBString(OrgAttr.CorpScale, null, "企业规模。当企业未设置该属性时，值为空", true, false, 0, 4000, 36);
		map.AddTBString(OrgAttr.CorpIndustry, null, "企业所属行业。当企业未设置该属性时，值为空", true, false, 0, 4000, 36);
		map.AddTBString(OrgAttr.CorpSubIndustry, null, "企业所属子行业。当企业未设置该属性时，值为空", true, false, 0, 4000, 36);
		map.AddTBString(OrgAttr.Location, null, "企业所在地信息, 为空时表示未知", true, false, 0, 4000, 36);
		// map.AddTBString(OrgAttr.SquareLogoUrl, null, "授权方应用方形头像", true, false, 0, 4000, 36);
		//map.AddTBString(OrgAttr.RoundLogoUl, null, "授权方应用圆形头像", true, false, 0, 4000, 36);
		map.AddTBInt(OrgAttr.OrgSta, 1, "组织状态", true, false);
			///#endregion 微信信息

		RefMethod rm = new RefMethod();
		rm.Title = "设置图片签名";
		rm.ClassMethodName = this.toString() + ".DoICON";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		this.set_enMap(map);
		return this.get_enMap();
	}

	public final String DoICON() {
		return "../../../GPM/OrgDepts.htm?FK_Org=" + this.getNo();
	}

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		//增加拼音，以方便查找.
		if (DataType.IsNullOrEmpty(this.getName()) == true)
		{
			throw new RuntimeException("err@名称不能为空.");
		}

		String pinyinQP = DataType.ParseStringToPinyin(this.getName()).toLowerCase();
		String pinyinJX = DataType.ParseStringToPinyinJianXie(this.getName()).toLowerCase();
		this.setPinYin("," + pinyinQP + "," + pinyinJX + ",");

		return super.beforeUpdateInsertAction();
	}

	/** 
	 获取集合
	*/
	@Override
	public Entities GetNewEntities()
	{
		return new Orgs();
	}

		///#endregion 构造函数

	/** 
	 初始化数据.
	*/
	public final void Init_OrgDatas() throws Exception {

			///#region 初始化 - 流程树.
		bp.wf.template.FlowSort fs = new bp.wf.template.FlowSort();
		fs.setNo(this.getNo()); //公司编号
		fs.setName("流程树");
		fs.setOrgNo(this.getNo());
		fs.setParentNo("100"); //这里固定死了,必须是100.
		fs.DirectInsert();

		fs.setNo(DBAccess.GenerGUID(5, "Port_Dept", "No"));
		fs.setParentNo(this.getNo()); //帐号信息.
		fs.setName("日常办公");
		fs.setOrgNo(this.getNo());
		fs.DirectInsert();

		fs.setNo(DBAccess.GenerGUID(0, null, null));
		fs.setParentNo(this.getNo()); //帐号信息.
		fs.setName("财务类");
		fs.setOrgNo(this.getNo());
		fs.DirectInsert();

		fs.setNo(DBAccess.GenerGUID(0, null, null));
		fs.setParentNo(this.getNo());
		fs.setName("人力资源类");
		fs.setOrgNo(this.getNo());
		fs.DirectInsert();

			///#endregion 开始流程树.


			///#region  加载流程模版.
		//类别.
		bp.wf.template.FlowSorts fss = new bp.wf.template.FlowSorts();
		fss.Retrieve("OrgNo", this.getNo(), null);
		fs = fss.get(0) instanceof bp.wf.template.FlowSort ? (bp.wf.template.FlowSort)fss.get(0) : null;

			///#endregion


			///#region 表单树.
		bp.wf.template.SysFormTree ft = new bp.wf.template.SysFormTree();
		ft.setNo(this.getNo()); //公司编号
		ft.setName("表单树");
		ft.setOrgNo(this.getNo());
		ft.setParentNo("100"); //这里固定死了必须是100.
		ft.DirectInsert();

		ft.setNo(DBAccess.GenerGUID(0, null, null));
		ft.setParentNo(this.getNo()); //帐号信息.
		ft.setName("日常办公");
		ft.setOrgNo(this.getNo());
		ft.DirectInsert();

		ft.setNo(DBAccess.GenerGUID(0, null, null));
		ft.setParentNo(this.getNo()); //帐号信息.
		ft.setName("财务类");
		ft.setOrgNo(this.getNo());
		ft.DirectInsert();

		ft.setNo(DBAccess.GenerGUID(0, null, null));
		ft.setParentNo(this.getNo());
		ft.setName("人力资源类");
		ft.setOrgNo(this.getNo());
		ft.DirectInsert();

			///#endregion 表单树.

		//如果是web注册 =0，就去掉. 
		if (this.getRegFrom() != 0)
		{
			return;
		}


			///#region 初始化部门.
		//根目录.
		Dept dept = new Dept();
		dept.setNo(this.getNo());
		dept.setParentNo("100");
		dept.setName(this.getName());
		dept.setOrgNo(this.getNo());
		dept.setAdminer(this.getAdminer());
		dept.DirectInsert();

		Emp ep = new Emp(this.getNo() + "_" + this.getAdminer());
		ep.setDeptNo(this.getNo());
		ep.setOrgNo(this.getNo()); //所在公司.

		ep.Update();

		//写入Port_OrgAdminer表中，供查询是否是管理员.
		bp.wf.port.admin2group.OrgAdminer orgAdminer = new bp.wf.port.admin2group.OrgAdminer();
		orgAdminer.setEmpNo(ep.getUserID());
		orgAdminer.setOrgNo(this.getNo());
		orgAdminer.DirectInsert();

		//管理员.
		this.setAdminer(ep.getUserID());
		this.setAdminerName(ep.getName());

		//把这个人员放入到根目录下.
		DeptEmp de = new DeptEmp();
		de.setOrgNo( ep.getOrgNo());
		de.setDeptNo(this.getNo());
		de.setEmpNo(ep.getUserID());
		de.setMyPK(de.getDeptNo() + "_" + de.getEmpNo());
		de.DirectInsert();


			///#region 开始创建下级部门.

		dept.setNo(DBAccess.GenerGUID(0, null, null)); // (deptNoLen, "Port_Dept", "No");
		dept.setParentNo(this.getNo()); //帐号信息.
		dept.setName("总经理部");
		dept.setOrgNo(this.getNo());
		dept.setAdminer(null);
		dept.DirectInsert();

		dept.setNo(DBAccess.GenerGUID(0, null, null)); // (deptNoLen, "Port_Dept", "No");
		dept.setParentNo(this.getNo()); //帐号信息.
		dept.setName("信息部");
		dept.setOrgNo(this.getNo());
		dept.setAdminer(null);
		dept.DirectInsert();

		//把当前的人员放入到信息部里面去.
		DeptEmp myde = new DeptEmp();
		//设置主键.
		myde.setMyPK(dept.getNo() + "_" + this.getAdminer());

		myde.setDeptNo(dept.getNo());
		myde.setEmpNo(this.getAdminer());

	   // myde.setEmpNo(this.getNo() + "_" + this.Adminer;

		myde.setOrgNo(this.getNo());
		myde.Insert();


		dept.setNo(DBAccess.GenerGUID(0, null, null)); // (deptNoLen, "Port_Dept", "No");
		dept.setParentNo(this.getNo()); //帐号信息.
		dept.setName("财务部");
		dept.setOrgNo(this.getNo());
		dept.setAdminer(null);
		dept.DirectInsert();

		dept.setNo(DBAccess.GenerGUID(0, null, null)); // (deptNoLen, "Port_Dept", "No");
		dept.setParentNo(this.getNo());
		dept.setName("人力资源部");
		dept.setAdminer(null);
		dept.setOrgNo(this.getNo());
		dept.DirectInsert();

			///#endregion 开始创建下级部门.


			///#endregion 初始化部门.


			///#region Init 角色类别.


			///#region 高层角色.
		StationType st = new StationType();
		st.setNo(DBAccess.GenerGUID(0, null, null));
		st.setName("高层");
		st.setOrgNo(this.getNo());
		st.DirectInsert();

		Station sta = new Station();
		sta.setNo(DBAccess.GenerGUID(0, null, null));
		sta.setName("总经理岗");
		sta.setOrgNo(this.getNo());
		sta.setFKStationType(st.getNo());
		sta.DirectInsert();

		sta = new Station();
		sta.setNo(DBAccess.GenerGUID(0, null, null));
		sta.setName("副总经理岗");
		sta.setOrgNo(this.getNo());
		sta.setFKStationType(st.getNo());
		sta.DirectInsert();

			///#endregion 高层角色.


			///#region 中层角色
		st = new StationType();
		st.setNo(DBAccess.GenerGUID(0, null, null));
		st.setName("中层");
		st.setOrgNo(this.getNo());
		st.DirectInsert();

		sta = new Station();
		sta.setNo(DBAccess.GenerGUID(0, null, null));
		sta.setName("信息部部长");
		sta.setOrgNo(this.getNo());
		sta.setFKStationType(st.getNo());
		sta.DirectInsert();

		//把当前的人员放入到信息部里面去.
		DeptEmpStation des = new DeptEmpStation();
		des.setDeptNo(myde.getDeptNo());
		des.setEmpNo(this.getAdminer());
		des.setStationNo(sta.getNo()); //信息部部长.
		des.setOrgNo(this.getNo());
		des.DirectInsert();

		sta = new Station();
		sta.setNo(DBAccess.GenerGUID(0, null, null));
		sta.setName("财务部经理岗");
		sta.setOrgNo(this.getNo());
		sta.setFKStationType(st.getNo());
		sta.DirectInsert();

		sta = new Station();
		sta.setNo(DBAccess.GenerGUID(0, null, null));
		sta.setName("人力资源部经理岗");
		sta.setOrgNo(this.getNo());
		sta.setFKStationType(st.getNo());
		sta.DirectInsert();

			///#endregion 中层角色


			///#region 基层
		st = new StationType();
		st.setNo(DBAccess.GenerGUID(0, null, null));
		st.setName("基层");
		st.setOrgNo(this.getNo());
		st.DirectInsert();

		sta = new Station();
		sta.setNo(DBAccess.GenerGUID(0, null, null));
		sta.setName("程序员岗");
		sta.setOrgNo(this.getNo());
		sta.setFKStationType(st.getNo());
		sta.DirectInsert();

		sta = new Station();
		sta.setNo(DBAccess.GenerGUID(0, null, null));
		sta.setName("出纳岗");
		sta.setOrgNo(this.getNo());
		sta.setFKStationType(st.getNo());
		sta.DirectInsert();

		sta = new Station();
		sta.setNo(DBAccess.GenerGUID(0, null, null));
		sta.setName("人力资源助理岗");
		sta.setOrgNo(this.getNo());
		sta.setFKStationType(st.getNo());
		sta.DirectInsert();

			///#endregion 基层


			///#endregion Init 角色类别.

		//return "url@/Admin/Portal/Default.htm?Token=" + ep.SID + "&UserNo=" + ep.No;
	}
	/** 
	 生成一个唯一的GUID》
	 
	 @return 
	*/
	public static String GenerNewOrgNo()
	{
		while (true)
		{
			String chars = DBAccess.GenerGUID();// System.Guid.NewGuid().toString().substring(0, 4);
			String str = chars.substring(0, 1);
			if (DataType.IsNumStr(str) == true)
			{
				continue;
			}

			String sql = "SELECT count(No) as Num FROM Port_Org WHERE No='" + chars + "'";
			if (DBAccess.RunSQLReturnValInt(sql) >= 1)
			{
				continue;
			}
			return chars;
		}
	}
	/** 
	 执行删除.
	 
	 @return 
	*/
	@Override
	protected boolean beforeDelete() throws Exception
	{
		throw new RuntimeException("不允许删除.");
	}
	public final void DoDelete() throws Exception {
		//删除流程，删除数据.
		 bp.wf.Flows fls = new bp.wf.Flows();
		 fls.Retrieve(bp.wf.template.FlowAttr.OrgNo, this.getNo(), null);
		 for (bp.wf.Flow item : fls.ToJavaList())
		 {
			 item.DoDelData();
			 item.DoDelete();
		 }

		//删除组织数据.
		DBAccess.RunSQL("DELETE FROM Port_Emp WHERE OrgNo='" + this.getNo() + "'");
		DBAccess.RunSQL("DELETE FROM Port_Dept WHERE OrgNo='" + this.getNo() + "'");
		DBAccess.RunSQL("DELETE FROM Port_DeptEmp WHERE OrgNo='" + this.getNo() + "'");
		DBAccess.RunSQL("DELETE FROM Port_DeptEmpStation WHERE OrgNo='" + this.getNo() + "'");

		//删除管理员.
		DBAccess.RunSQL("DELETE FROM Port_OrgAdminer WHERE OrgNo='" + this.getNo() + "'");


		//删除类别.
		DBAccess.RunSQL("DELETE FROM WF_FlowSort WHERE OrgNo='" + this.getNo() + "'");
		DBAccess.RunSQL("DELETE FROM Sys_FormTree WHERE OrgNo='" + this.getNo() + "'");

		this.DirectDelete();
	}
	public final void DoDeletePassPort() throws Exception {
		//删除组织数据.
		DBAccess.RunSQL("DELETE FROM Port_Emp WHERE OrgNo='" + this.getNo() + "'");
		DBAccess.RunSQL("DELETE FROM Port_Dept WHERE OrgNo='" + this.getNo() + "'");
		DBAccess.RunSQL("DELETE FROM Port_DeptEmp WHERE OrgNo='" + this.getNo() + "'");

		//删除管理员.
		DBAccess.RunSQL("DELETE FROM Port_OrgAdminer WHERE OrgNo='" + this.getNo() + "'");

		this.DirectDelete();
	}
}
