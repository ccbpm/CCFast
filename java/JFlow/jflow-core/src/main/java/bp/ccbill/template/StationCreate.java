package bp.ccbill.template;

import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 单据可创建的工作角色
 单据的工作角色有两部分组成.	 
 记录了从一个单据到其他的多个单据.
 也记录了到这个单据的其他的单据.
*/
public class StationCreate extends EntityMM
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
	public final int getFrmID() {
		return this.GetValIntByKey(StationCreateAttr.FrmID);
	}
	public final void setFrmID(int value)  {
		this.SetValByKey(StationCreateAttr.FrmID, value);
	}
	public final String getFKStationT() {
		return this.GetValRefTextByKey(StationCreateAttr.FK_Station);
	}
	/** 
	 工作角色
	*/
	public final String getFKStation() {
		return this.GetValStringByKey(StationCreateAttr.FK_Station);
	}
	public final void setFKStation(String value)  {
		this.SetValByKey(StationCreateAttr.FK_Station, value);
	}
		///#endregion
		///#region 构造方法
	/** 
	 单据可创建的工作角色
	*/
	public StationCreate()
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

		Map map = new Map("Frm_StationCreate", "单据角色");

		map.AddTBStringPK(StationCreateAttr.FrmID, null, "表单", true, true, 1, 100, 100);
		map.AddDDLEntitiesPK(StationCreateAttr.FK_Station, null, "可以创建角色", new bp.port.Stations(), true);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

}
