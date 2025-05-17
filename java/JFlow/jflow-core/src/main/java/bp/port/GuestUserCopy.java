package bp.port;

import bp.en.*; import bp.en.Map;
import bp.da.*;
import bp.web.*;
import bp.*;

/** 
 webUser 的副本
*/
public class GuestUserCopy
{

		///#region 属性.
	public String No = "";
	public String Name = "";

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

	///#endregion 属性.

	/** 
	 GuestUser 的副本
	*/
	public GuestUserCopy()
	{
	}
	/** 
	 按人员初始化数据
	 
	 @param emp
	*/
	public final void LoadEmpEntity(Emp emp) throws Exception {
		this.setNo(emp.getNo());
		this.setName(emp.getName());
	}
	public final void LoadEmpNo(String empNo) throws Exception {
		Emp emp = new Emp(empNo);
		LoadEmpEntity(emp);
	}
	/** 
	 按webUser人员初始化数据
	 
	*/
	public final void LoadWebUser()
	{
		this.setNo(GuestUser.getNo());
		this.setName(GuestUser.getName());
	}
}
