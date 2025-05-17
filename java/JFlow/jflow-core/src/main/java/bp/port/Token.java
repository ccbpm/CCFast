package bp.port;

import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
  登录记录
*/
public class Token extends EntityMyPK
{

		///#region 属性
	/** 
	 组织编码
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(TokenAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(TokenAttr.OrgNo, value);
	}
	public final String getOrgName()  {
		return this.GetValStrByKey(TokenAttr.OrgName);
	}
	public final void setOrgName(String value){
		this.SetValByKey(TokenAttr.OrgName, value);
	}


	public final String getEmpNo()  {
		return this.GetValStrByKey(TokenAttr.EmpNo);
	}
	public final void setEmpNo(String value){
		this.SetValByKey(TokenAttr.EmpNo, value);
	}

	public final String getEmpName()  {
		return this.GetValStrByKey(TokenAttr.EmpName);
	}
	public final void setEmpName(String value){
		this.SetValByKey(TokenAttr.EmpName, value);
	}
	public final String getDeptNo()  {
		return this.GetValStrByKey(TokenAttr.DeptNo);
	}
	public final void setDeptNo(String value){
		this.SetValByKey(TokenAttr.DeptNo, value);
	}
	public final String getDeptName()  {
		return this.GetValStrByKey(TokenAttr.DeptName);
	}
	public final void setDeptName(String value){
		this.SetValByKey(TokenAttr.DeptName, value);
	}
	public final String getRDT()  {
		return this.GetValStrByKey(TokenAttr.RDT);
	}
	public final void setRDT(String value){
		this.SetValByKey(TokenAttr.RDT, value);
	}
	public final int getSheBei()  {
		return this.GetValIntByKey(TokenAttr.SheBei);
	}
	public final void setSheBei(int value){
		this.SetValByKey(TokenAttr.SheBei, value);
	}

		///#endregion


		///#region 实现基本的方方法
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		return uac;
	}

		///#endregion


		///#region 构造方法
	/** 
	 登录记录
	*/
	public Token()
	{
	}
	/** 
	 登录记录
	 
	 @param pkval
	*/
	public Token(String pkval) throws Exception {
		super(pkval);
	}

		///#endregion

	/**
	 * 登录记录Map
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("Port_Token", "登录记录");
		map.setCodeStruct("2");

		map.AddMyPK();

		map.AddTBString(TokenAttr.EmpNo, null, "人员编号", true, false, 0, 100, 20);
		map.AddTBString(TokenAttr.EmpName, null, "人员名称", true, false, 0, 100, 20);

		map.AddTBString(TokenAttr.DeptNo, null, "部门编号", true, false, 0, 100, 20);
		map.AddTBString(TokenAttr.DeptName, null, "部门名称", true, false, 0, 100, 20);

		map.AddTBString(TokenAttr.OrgNo, null, "组织编号", true, false, 0, 100, 20);
		map.AddTBString(TokenAttr.OrgName, null, "组织名称", true, false, 0, 100, 20);

		map.AddTBDateTime(TokenAttr.RDT, null, "记录日期", true, false);


		map.AddTBInt(TokenAttr.SheBei, 0, "0=PC,1=移动", true, false);

		this.set_enMap(map);
		return this.get_enMap();
	}

}
