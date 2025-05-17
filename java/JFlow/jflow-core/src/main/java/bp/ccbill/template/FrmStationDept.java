package bp.ccbill.template;

import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 单据查询角色
 单据查询角色有两部分组成.	 
*/
public class FrmStationDept extends EntityMM
{

		///#region 基本属性
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenAll();
		return uac;
	}
	/** 
	单据
	*/
	public final String getFKFrm() {
		return this.GetValStringByKey(FrmStationDeptAttr.FK_Frm);
	}
	public final void setFKFrm(String value)  {
		this.SetValByKey(FrmStationDeptAttr.FK_Frm, value);
	}

	/** 
	 工作角色
	*/
	public final String getFKStation() {
		return this.GetValStringByKey(FrmStationDeptAttr.FK_Station);
	}
	public final void setFKStation(String value)  {
		this.SetValByKey(FrmStationDeptAttr.FK_Station, value);
	}
	public final String getDeptNo() {
		return this.GetValStringByKey(FrmStationDeptAttr.FK_Dept);
	}
	public final void setDeptNo(String value)  {
		this.SetValByKey(FrmStationDeptAttr.FK_Dept, value);
	}


		///#endregion


		///#region 构造方法
	/** 
	 单据工作角色
	*/
	public FrmStationDept()
	{
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

		Map map = new Map("Frm_StationDept", "单据角色部门");

		map.AddTBStringPK(FrmStationDeptAttr.FK_Frm, null, "单据编号", false, false, 1, 190, 20);

		map.AddDDLEntitiesPK(FrmStationDeptAttr.FK_Station, null, "工作角色", new bp.port.Stations(), true);

		map.AddDDLEntitiesPK(FrmStationDeptAttr.FK_Dept, null, "部门", new bp.port.Depts(), true);

		this.set_enMap(map);
		return this.get_enMap();
	}



		///#endregion

}
