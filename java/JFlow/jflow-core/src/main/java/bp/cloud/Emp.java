package bp.cloud;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.port.*;

import java.util.*;

/** 
 操作员 的摘要说明。
*/
public class Emp extends EntityNoName
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

		String sql = "SELECT COUNT(FK_Emp) FROM Port_DeptEmpStation WHERE FK_Emp='" + this.getNo() + "'";
		if (DBAccess.RunSQLReturnValInt(sql, 0) == 0)
		{
			return false;
		}

		sql = "SELECT COUNT(FK_Emp) FROM Port_DeptEmp WHERE FK_Emp='" + this.getNo() + "'";
		if (DBAccess.RunSQLReturnValInt(sql, 0) == 0)
		{
			return false;
		}

		return true;
	}
	/** 
	 用户ID.
	*/
	public final String getUserID()  {
		return this.GetValStrByKey(EmpAttr.UserID);
	}
	public final void setUserID(String value)  {
		this.SetValByKey(EmpAttr.UserID, value);
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
	 部门
	*/
	public final String getDeptNo()  {
		return this.GetValStrByKey(EmpAttr.FK_Dept);
	}
	public final void setDeptNo(String value)  {
		this.SetValByKey(EmpAttr.FK_Dept, value);
	}
	public final String getDeptText() {
		return this.GetValRefTextByKey(EmpAttr.FK_Dept);
	}
	public final String getTel()  {
		return this.GetValStrByKey(EmpAttr.Tel);
	}
	public final void setTel(String value)  {
		this.SetValByKey(EmpAttr.Tel, value);
	}
	public final String getEmail()  {
		return this.GetValStrByKey(EmpAttr.Email);
	}
	public final void setEmail(String value)  {
		this.SetValByKey(EmpAttr.Email, value);
	}
	/** 
	 直属领导
	*/
	public final String getLeader()  {
		return this.GetValStrByKey(EmpAttr.Leader);
	}
	public final void setLeader(String value)  {
		this.SetValByKey(EmpAttr.Leader, value);
	}
	/** 
	 顺序号
	*/
	public final int getIdx() {
		return this.GetValIntByKey(EmpAttr.Idx);
	}
	public final void setIdx(int value)  {
		this.SetValByKey(EmpAttr.Idx, value);
	}
	/** 
	 组织结构编码
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(EmpAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(EmpAttr.OrgNo, value);
	}
	public final String getOrgName()  {
		return this.GetValStrByKey(EmpAttr.OrgName);
	}
	public final void setOrgName(String value)  {
		this.SetValByKey(EmpAttr.OrgName, value);
	}
	/**
	 * 密码
	 */
	public final String getPass() throws Exception {
		return DBAccess.RunSQLReturnStringIsNull("SELECT Pass FROM Port_Emp WHERE No='" + this.getNo() + "'", null);
	}
	public final void setPass(String value) throws Exception {
		DBAccess.RunSQL("UPDATE Port_Emp SET Pass='" + value + "' WHERE No='" + this.getNo() + "'");
	}
	/**
	 操作员
	*/
	public Emp()
	{
	}
	/** 
	 初始化人员
	 
	 @param orgNo
	 @param userID
	*/
	public Emp(String orgNo, String userID) throws Exception {
		int i = this.Retrieve(EmpAttr.OrgNo, orgNo, EmpAttr.UserID, userID);
		if (i == 0)
		{
			throw new RuntimeException("@组织编号:" + orgNo + " 或者UserID:" + getUserID() + "错误.");
		}
	}
	/** 
	 操作员
	 
	 @param no 编号
	*/
	public Emp(String no) throws Exception  {

			this.setNo(no);
			this.Retrieve();

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
		uac.IsInsert = false;
		return uac;
	}
	/**
	 * 重写基类方法
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Port_Emp", "用户");
		map.setEnType(EnType.App);
		map.IndexField = EmpAttr.FK_Dept;


			///#region 字段
		/*关于字段属性的增加 */
		map.AddTBStringPK(EmpAttr.No, null, "手机号/ID", false, false, 1, 500, 90);
		map.AddTBString(EmpAttr.UserID, null, "登录ID", true, true, 0, 100, 10);
		map.AddTBString(EmpAttr.Name, null, "姓名", true, false, 0, 500, 130);
		map.AddTBString(EmpAttr.FK_Dept, null, "当前登录的部门", true, false, 0, 500, 130);

		//状态. 0=启用，1=禁用.
		map.AddTBInt(EmpAttr.EmpSta, 0, "状态(0=启用，1=禁用)", false, false);
		map.AddTBString(EmpAttr.Leader, null, "部门领导", false, false, 0, 100, 10);
		map.AddTBString(EmpAttr.Tel, null, "电话", true, false, 0, 20, 130, true);
		map.AddTBString(EmpAttr.Email, null, "邮箱", true, false, 0, 100, 132, true);
		map.AddTBString(EmpAttr.PinYin, null, "拼音", false, false, 0, 1000, 132, false);
		map.AddTBString(EmpAttr.OrgNo, null, "OrgNo", true, true, 0, 500, 132, false);

		//map.AddDDLEntities(EmpAttr.OrgNo, null, "组织", new BP.Cloud.Orgs(), false);
		//map.AddTBString(EmpAttr.OrgNo, null, "OrgNo", false, false, 0, 36, 36);
		map.AddTBString(EmpAttr.OrgName, null, "OrgName", false, false, 0, 36, 36);
		map.AddTBInt(EmpAttr.Idx, 0, "序号", true, false);

			///#endregion 字段


			///#region 相关方法.
		RefMethod rm = new RefMethod();
		rm.Title = "设置图片签名";
		rm.ClassMethodName = this.toString() + ".DoSinger";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "部门角色";
		rm.ClassMethodName = this.toString() + ".DoEmpDepts";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		////节点绑定部门. 节点绑定部门.
		//map.getAttrsOfOneVSM().AddBranches(new DeptEmps(), new bp.port.Depts(),
		//   bp.port.DeptEmpAttr.FK_Emp,
		//   bp.port.DeptEmpAttr.FK_Dept, "部门维护", EmpAttr.Name, EmpAttr.No, "@OrgNo");

		rm = new RefMethod();
		rm.Title = "修改密码";
		rm.ClassMethodName = this.toString() + ".DoResetpassword";
		try{
			rm.getHisAttrs().AddTBString("pass1", null, "输入密码", true, false, 0, 100, 100);
			rm.getHisAttrs().AddTBString("pass2", null, "再次输入", true, false, 0, 100, 100);
		}catch(Exception e){

		}
		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "设置部门直属领导";
		//rm.ClassMethodName = this.ToString() + ".DoEditLeader";
		//rm.RefAttrKey = "LeaderName";
		//rm.refMethodType = RefMethodType.LinkModel;
		//map.AddRefMethod(rm);

			///#endregion 相关方法.

		this.set_enMap(map);
		return this.get_enMap();
	}


		///#region 方法执行.
	public final String DoEditMainDept() {
		return "../../../GPM/EmpDeptMainDept.htm?FK_Emp=" + this.getNo() + "&FK_Dept=" + this.getDeptNo();
	}

	public final String DoEditLeader() {
		return "../../../GPM/EmpLeader.htm?FK_Emp=" + this.getNo() + "&FK_Dept=" + this.getDeptNo();
	}

	public final String DoEmpDepts() {
		return "/GPM/EmpDepts.htm?FK_Emp=" + this.getNo();
	}

	public final String DoSinger() throws Exception {
		//路径
		return "../../../GPM/Siganture.htm?EmpNo=" + this.getNo();
	}

		///#endregion 方法执行.

	@Override
	protected boolean beforeInsert() throws Exception
	{

		////当前人员所在的部门.
		//  this.setOrgNo(bp.web.WebUser.getOrgNo();
		////处理主部门的问题.
		//DeptEmp de = new DeptEmp();
		//de.setDeptNo(this.DeptNo;
		//de.setEmpNo(this.No;
		//de.IsMainDept = true;
		//de.setOrgNo(this.OrgNo;
		//de.Save();
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

	/** 
	 保存后修改WF_Emp中的邮箱
	*/
	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		String sql = "Select Count(*) From WF_Emp Where No='" + this.getNo() + "'";
		int count = DBAccess.RunSQLReturnValInt(sql);
		if (count == 0)
		{
			sql = "INSERT INTO WF_Emp (No,Name) VALUES('" + this.getNo() + "','" + this.getName() + "')";
			DBAccess.RunSQL(sql);
		}
		//修改Port_Emp中的缓存                  
		bp.port.Emp emp = new bp.port.Emp();
		emp.setNo( this.getNo());
		if (emp.RetrieveFromDBSources() != 0)
		{
			emp.setDeptNo( this.getDeptNo());
			emp.DirectUpdate();
		}

		super.afterInsertUpdateAction();
	}

	@Override
	protected void afterInsert() throws Exception {
		String pass = this.getPass();
		if (DataType.IsNullOrEmpty(pass) == true)
			pass = SystemConfig.getUserDefaultPass();
		if(DataType.IsNullOrEmpty(SystemConfig.getUserDefaultPass()) == false){
			pass = SystemConfig.getUserDefaultPass();
		}

		if (SystemConfig.getIsEnablePasswordEncryption() == true)
			pass = bp.tools.MD5Utill.MD5Encode(pass, "UTF8");
		this.setPass(pass); //设置密码.

		super.afterInsert();
	}
	@Override
	protected boolean beforeDelete() throws Exception
	{
		if (this.getOrgNo().equals(bp.web.WebUser.getOrgNo()) == false)
		{
			throw new RuntimeException("err@您不能删除别人的数据.");
		}

		DeptEmps ens = new DeptEmps();
		ens.Delete(DeptEmpAttr.FK_Emp, this.getNo());

		DeptEmpStations ensD = new DeptEmpStations();
		ensD.Delete(DeptEmpAttr.FK_Emp, this.getNo());

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

	public static String GenerPinYin(String name)
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
		this.DoOrderUp(EmpAttr.FK_Dept, this.getDeptNo(), EmpAttr.Idx);
		return "执行成功.";
	}
	/** 
	 向下移动
	*/
	public final String DoDown() throws Exception
	{
		this.DoOrderDown(EmpAttr.FK_Dept, this.getDeptNo(), EmpAttr.Idx);
		return "执行成功.";
	}
	/** 
	 重置密码.
	 
	 @param pass1
	 @param pass2
	 @return 
	*/
	public final String DoResetpassword(String pass1, String pass2) throws Exception {
		bp.port.Emp emp = new bp.port.Emp();
		emp.setNo( this.getNo());
		emp.Retrieve();

		return emp.DoResetpassword(pass1,pass2);

	}
	/** 
	 获取集合
	*/
	@Override
	public Entities GetNewEntities()
	{
		return new Emps();
	}

		///#endregion 构造函数
}
