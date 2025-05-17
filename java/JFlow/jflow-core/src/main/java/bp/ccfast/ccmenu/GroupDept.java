package bp.ccfast.ccmenu;

import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.port.*;

/** 
 权限组部门
*/
public class GroupDept extends EntityMyPK
{

		///#region 属性
	public final String getDeptNo() {
		return this.GetValStringByKey(GroupDeptAttr.FK_Dept);
	}
	public final void setDeptNo(String value)  {
		this.SetValByKey(GroupDeptAttr.FK_Dept, value);
	}
	public final String getGroupNo() {
		return this.GetValStringByKey(GroupDeptAttr.FK_Group);
	}
	public final void setGroupNo(String value)  {
		this.SetValByKey(GroupDeptAttr.FK_Group, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 权限组部门
	*/
	public GroupDept()
	{
	}

	/**
	 * 权限组Dept
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("GPM_GroupDept", "权限组部门");
		map.AddMyPK(true);

		map.AddTBString(GroupDeptAttr.FK_Group, null, "权限组", false, false, 0, 50, 20);
		map.AddDDLEntities(GroupDeptAttr.FK_Dept, null, "部门", new Depts(), true);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
	@Override
	protected boolean beforeInsert() throws Exception
	{
		//
		this.setMyPK(this.getGroupNo() + "_" + this.getDeptNo());

		return super.beforeInsert();
	}
}
