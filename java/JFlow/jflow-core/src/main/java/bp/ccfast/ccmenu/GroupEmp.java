package bp.ccfast.ccmenu;

import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.port.*;

/** 
 权限组人员
*/
public class GroupEmp extends EntityMM
{

		///#region 属性
	public final String getEmpNo() {
		return this.GetValStringByKey(GroupEmpAttr.FK_Emp);
	}
	public final void setEmpNo(String value)  {
		this.SetValByKey(GroupEmpAttr.FK_Emp, value);
	}
	public final String getFKGroup() {
		return this.GetValStringByKey(GroupEmpAttr.FK_Group);
	}
	public final void setFKGroup(String value)  {
		this.SetValByKey(GroupEmpAttr.FK_Group, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 权限组人员
	*/
	public GroupEmp()
	{
	}

	/**
	 * 权限组人员
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("GPM_GroupEmp", "权限组人员");
		map.setEnType(EnType.App);

		map.AddTBStringPK(GroupEmpAttr.FK_Group, null, "权限组", true, false, 0, 50, 20);
		map.AddDDLEntitiesPK(GroupEmpAttr.FK_Emp, null, "人员", new Emps(), true);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
