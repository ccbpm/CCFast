package bp.port;

import bp.en.Map;
import bp.en.*; import bp.en.Map;

/** 
 用户组人员
*/
public class TeamEmp extends EntityMM
{

		///#region 属性
	public final String getEmpNo()  {
		return this.GetValStringByKey(TeamEmpAttr.FK_Emp);
	}
	public final void setEmpNo(String value){
		this.SetValByKey(TeamEmpAttr.FK_Emp, value);
	}
	public final String getFKTeam()  {
		return this.GetValStringByKey(TeamEmpAttr.FK_Team);
	}
	public final void setFKTeam(String value){
		this.SetValByKey(TeamEmpAttr.FK_Team, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 用户组人员
	*/
	public TeamEmp()
	{
	}
	/**
	 * 用户组人员
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("Port_TeamEmp", "用户组人员");
		map.setEnType(EnType.App);

		map.AddTBStringPK(TeamEmpAttr.FK_Team, null, "用户组", true, false, 0, 50, 20);
		map.AddDDLEntitiesPK(TeamEmpAttr.FK_Emp, null, "人员", new Emps(), true);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
