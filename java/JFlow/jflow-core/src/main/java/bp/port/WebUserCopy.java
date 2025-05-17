package bp.port;

import bp.web.*;

/** 
 webUser 的副本
*/
public class WebUserCopy
{

		///#region 属性.
	public String No = "";
	public String Name = "";
	public String DeptNo = "";
	public String DeptName = "";
	public String OrgNo = "";
	public String OrgName = "";
	public String Token = "";
	public String Auth = "";
	public String AuthName = "";
	public boolean IsAuthorize = false;

	public String getNo() {
		return No;
	}

	public void setNo(String no) {
		No = no;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getDeptNo() {
		return DeptNo;
	}

	public void setDeptNo(String deptNo) {
		DeptNo = deptNo;
	}

	public String getDeptName() {
		return DeptName;
	}

	public void setDeptName(String deptName) {
		DeptName = deptName;
	}

	public String getOrgNo() {
		return OrgNo;
	}

	public void setOrgNo(String orgNo) {
		OrgNo = orgNo;
	}

	public String getOrgName() {
		return OrgName;
	}

	public void setOrgName(String orgName) {
		OrgName = orgName;
	}

	public String getToken() {
		return Token;
	}

	public void setToken(String token) {
		Token = token;
	}

	public String getAuth() {
		return Auth;
	}

	public void setAuth(String auth) {
		Auth = auth;
	}

	public String getAuthName() {
		return AuthName;
	}

	public void setAuthName(String authName) {
		AuthName = authName;
	}

	public boolean getIsAuthorize() {
		return IsAuthorize;
	}

	public void setIsAuthorize(boolean authorize) {
		IsAuthorize = authorize;
	}

	///#endregion 属性.

	/** 
	 webUser 的副本
	*/
	public WebUserCopy()
	{
	}
	/** 
	 按人员初始化数据
	 
	 @param emp
	*/
	public final void LoadEmpEntity(Emp emp) throws Exception {
		this.setNo(emp.getNo());
		this.setName(emp.getName());
		this.setDeptNo(emp.getDeptNo());
		this.setDeptName(emp.getDeptText());
		this.setOrgNo(emp.getOrgNo());
		this.setOrgName(emp.getOrgNo());
	  //  this.Token = emp.Token;
	}
	public final void LoadEmpNo(String empNo) throws Exception {
	   Emp emp = new Emp(empNo);
		LoadEmpEntity(emp);
	}
	/** 
	 按webUser人员初始化数据
	*/
	public final void LoadWebUser() throws Exception {
		this.setNo(WebUser.getNo());
		this.setName(WebUser.getName());
		this.setDeptNo(WebUser.getDeptNo());
		this.setDeptName(WebUser.getDeptName());
		this.setOrgNo(WebUser.getOrgNo());
		this.setOrgName(WebUser.getOrgName());
		this.setToken(WebUser.getToken());

		this.setIsAuthorize(WebUser.getIsAuthorize());
		this.setAuth(WebUser.getAuth());
		this.setAuthName(WebUser.getAuthName());

	}
}
