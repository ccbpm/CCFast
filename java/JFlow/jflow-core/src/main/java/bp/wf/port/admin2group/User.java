package bp.wf.port.admin2group;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import java.util.*;

/** 
 操作员 的摘要说明。
*/
public class User extends EntityNoName
{

		///#region 扩展属性

		public final String getUnionid() throws Exception {
			return this.GetValStrByKey(UserAttr.unionid);
		}
		public final void setUnionid(String value) throws Exception {
			this.SetValByKey(UserAttr.unionid, value);
		}
	public final String getOpenID()  {
		return this.GetValStrByKey(UserAttr.OpenID);
	}
	public final void setOpenID(String value){
		this.SetValByKey(UserAttr.OpenID, value);
	}
	public final String getOpenID2()  {
		return this.GetValStrByKey(UserAttr.OpenID2);
	}
	public final void setOpenID2(String value){
		this.SetValByKey(UserAttr.OpenID2, value);
	}

	public final String getSID()  {
		return this.GetValStrByKey(UserAttr.SID);
	}
	public final void setSID(String value){
		this.SetValByKey(UserAttr.SID, value);
	}
	/** 
	 拼音
	*/
	public final String getPinYin()  {
		return this.GetValStrByKey(UserAttr.PinYin);
	}
	public final void setPinYin(String value){
		this.SetValByKey(UserAttr.PinYin, value);
	}
	/** 
	 主要的部门。
	*/
	public final Dept getHisDept() throws Exception {
		try
		{
			return new Dept(this.getDeptNo());
		}
		catch (Exception ex)
		{
			throw new RuntimeException("@获取操作员" + this.getNo() + "部门[" + this.getDeptNo() + "]出现错误,可能是系统管理员没有给他维护部门.@" + ex.getMessage());
		}
	}
	/** 
	 部门
	*/
	public final String getDeptNo()  {
		return this.GetValStrByKey(UserAttr.FK_Dept);
	}
	public final void setDeptNo(String value){
		this.SetValByKey(UserAttr.FK_Dept, value);
	}
	public final String getDeptNoText()  {
		return this.GetValRefTextByKey(UserAttr.FK_Dept);
	}
	public final String getTel()  {
		return this.GetValStrByKey(UserAttr.Tel);
	}
	public final void setTel(String value){
		this.SetValByKey(UserAttr.Tel, value);
	}
	public final String getEmail()  {
		return this.GetValStrByKey(UserAttr.Email);
	}
	public final void setEmail(String value){
		this.SetValByKey(UserAttr.Email, value);
	}
	/** 
	 密码
	*/
	public final String getPass()  {
		return this.GetValStrByKey(UserAttr.Pass);
	}
	public final void setPass(String value){
		this.SetValByKey(UserAttr.Pass, value);
	}
	/** 
	 顺序号
	*/
	public final int getIdx()  {
		return this.GetValIntByKey(UserAttr.Idx);
	}
	public final void setIdx(int value){
		this.SetValByKey(UserAttr.Idx, value);
	}
	/** 
	 组织结构编码
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(UserAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(UserAttr.OrgNo, value);
	}
	public final String getOrgName()  {
		return this.GetValStrByKey(UserAttr.OrgName);
	}
	public final void setOrgName(String value){
		this.SetValByKey(UserAttr.OrgName, value);
	}

		///#endregion


		///#region 公共方法
	/** 
	 检查密码(可以重写此方法)
	 
	 @param pass 密码
	 @return 是否匹配成功
	*/
	public final boolean CheckPass(String pass) throws Exception {

		/*使用数据库校验.*/
		if (this.getPass().toLowerCase().equals(pass.toLowerCase()) == true)
		{
			return true;
		}
		return false;
	}

		///#endregion 公共方法


		///#region 构造函数
	/** 
	 操作员
	*/
	public User()
	{
	}
	/** 
	 操作员
	 
	 @param no 编号
	*/
	public User(String no) throws Exception
	{
		try
		{
			this.setNo(no);
			this.Retrieve();
		}
		catch (RuntimeException ex)
		{
			int i = this.RetrieveFromDBSources();
			if (i == 0)
			{
				throw new RuntimeException("err@用户账号[" + this.getNo() + "]错误:" + ex.getMessage());
			}
			throw ex;
		}
	}

	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getIsAdmin() == true)
		{
			uac.OpenAll();
		}
		else
		{
			uac.Readonly();
		}
		return uac;
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Port_User", "用户");
		map.setEnType(EnType.App);
		map.IndexField = UserAttr.FK_Dept;


			///#region 字段。
		/*关于字段属性的增加 */
		map.AddTBStringPK(UserAttr.No, null, "手机号/ID", true, false, 1, 150, 90);
		map.AddTBString(UserAttr.Name, null, "姓名", true, false, 0, 500, 130);
		map.AddTBString(UserAttr.Pass, null, "密码", false, false, 0, 100, 10);
		map.AddTBString(UserAttr.FK_Dept, null, "部门", false, false, 0, 100, 10);
		map.AddTBString(UserAttr.SID, null, "Token", false, false, 0, 36, 36);
		map.AddTBString(UserAttr.Tel, null, "电话", true, false, 0, 20, 130);
		map.AddTBString(UserAttr.Email, null, "邮箱", true, false, 0, 100, 132, true);
		map.AddTBString(UserAttr.PinYin, null, "拼音", true, false, 0, 1000, 132, true);

		map.AddTBString(UserAttr.OrgNo, null, "OrgNo", true, false, 0, 500, 132, true);
		map.AddTBString(UserAttr.OrgName, null, "OrgName", true, false, 0, 500, 132, true);
		map.AddTBString(UserAttr.unionid, null, "unionid", true, false, 0, 500, 132, true);
		map.AddTBString(UserAttr.OpenID, null, "小程序的OpenID", true, false, 0, 500, 132, true);
		map.AddTBString(UserAttr.OpenID2, null, "公众号的OpenID", true, false, 0, 500, 132, true);


		map.AddTBInt(UserAttr.Idx, 0, "序号", true, false);

			///#endregion 字段

		this.set_enMap(map);
		return this.get_enMap();
	}


	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (bp.difference.SystemConfig.getIsEnablePasswordEncryption() == true)
		{
			if (Objects.equals(this.getPass(), ""))
			{
				this.setPass("123");
			}
			this.setPass(bp.tools.MD5Utill.MD5Encode(this.getPass(), "UTF8"));
		}
		return super.beforeInsert();
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
	@Override
	protected boolean beforeDelete() throws Exception
	{
		if (!Objects.equals(this.getOrgNo(), bp.web.WebUser.getOrgNo()))
		{
			throw new RuntimeException("err@您不能删除别人的数据.");
		}

		return super.beforeDelete();
	}
	/** 
	 删除之后要做的事情
	*/
	@Override
	protected void afterDelete() throws Exception
	{
		super.afterDelete();
	}

	public static String GenerPinYin(String no, String name)
	{
		//增加拼音，以方便查找.
		String pinyinQP = DataType.ParseStringToPinyin(name).toLowerCase();
		String pinyinJX = DataType.ParseStringToPinyinJianXie(name).toLowerCase();
		String py = "," + pinyinQP + "," + pinyinJX + ",";
		return py;
	}

	/** 
	 向上移动
	*/
	public final String DoUp() throws Exception
	{
		this.DoOrderUp(UserAttr.FK_Dept, this.getDeptNo(), UserAttr.Idx);
		return "执行成功.";
	}
	/** 
	 向下移动
	*/
	public final String DoDown() throws Exception
	{
		this.DoOrderDown(UserAttr.FK_Dept, this.getDeptNo(), UserAttr.Idx);
		return "执行成功.";
	}

	public final String DoResetpassword(String pass1, String pass2) throws Exception {
		if (pass1.equals(pass2) == false)
		{
			return "两次密码不一致";
		}

		if (bp.difference.SystemConfig.getIsEnablePasswordEncryption() == true)
		{
			pass1 = bp.tools.MD5Utill.MD5Encode(pass1, "UTF8");
		}

		this.setPass(pass1);

		this.Update();
		return "密码设置成功";
	}
	/** 
	 获取集合
	*/
	@Override
	public Entities GetNewEntities()
	{
		return new Users();
	}

		///#endregion 构造函数
}
