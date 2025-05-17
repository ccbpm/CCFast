package bp.wf.rpt;

import bp.en.*; import bp.en.Map;
import bp.port.*;


/** 
 RptStation 的摘要说明。
*/
public class RptStation extends Entity
{
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getNo().equals("admin") == true)
		{
			uac.IsView = true;
			uac.IsDelete = true;
			uac.IsInsert = true;
			uac.IsUpdate = true;
			uac.IsAdjunct = true;
		}
		return uac;
	}


		///#region 基本属性
	/** 
	 报表ID
	*/
	public final String getFKRpt()  {
		return this.GetValStringByKey(RptStationAttr.FK_Rpt);
	}
	public final void setFKRpt(String value) throws Exception
	{
		SetValByKey(RptStationAttr.FK_Rpt, value);
	}
	public final String getFKStationT()  {
		return this.GetValRefTextByKey(RptStationAttr.FK_Station);
	}
	/** 
	角色
	*/
	public final String getFKStation()  {
		return this.GetValStringByKey(RptStationAttr.FK_Station);
	}
	public final void setFKStation(String value) throws Exception
	{
		SetValByKey(RptStationAttr.FK_Station, value);
	}

		///#endregion


		///#region 扩展属性


		///#endregion


		///#region 构造函数
	/** 
	 报表角色
	*/
	public RptStation()
	{
	}
	/** 
	 报表角色对应
	 
	 @param _empoid 报表ID
	 @param wsNo 角色编号 	
	*/
	public RptStation(String _empoid, String wsNo) throws Exception {
		this.setFKRpt(_empoid);
		this.setFKStation(wsNo);
		if (this.Retrieve() == 0)
		{
			this.Insert();
		}
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()  {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_RptStation", "报表角色对应信息");

		map.setEnType(EnType.Dot2Dot);

		map.AddTBStringPK(RptStationAttr.FK_Rpt, null, "报表", false, false, 1, 15, 1);
		map.AddDDLEntitiesPK(RptStationAttr.FK_Station, null, "角色", new Stations(), true);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
