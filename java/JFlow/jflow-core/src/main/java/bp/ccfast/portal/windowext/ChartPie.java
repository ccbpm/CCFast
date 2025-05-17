package bp.ccfast.portal.windowext;

import bp.en.*; import bp.en.Map;
import bp.en.Map;

/** 
 饼图
*/
public class ChartPie extends EntityNoName
{

		///#region 权限控制.
	/** 
	 控制权限
	*/
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
			uac.IsView = false;
		}

		uac.IsInsert = false;
		uac.IsDelete = false;
		return uac;
	}

		///#endregion 权限控制.


		///#region 属性

		///#endregion 属性


		///#region 构造方法
	/** 
	 饼图
	*/
	public ChartPie()
	{
	}
	/** 
	 饼图
	 
	 @param no
	*/
	public ChartPie(String no) throws Exception  {
		this.setNo(no);
		this.Retrieve();
	}
	/**
	 * EnMap
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}


		this.set_enMap(Glo.StationDBSrcMap("饼图"));

		return this.get_enMap();
	}

		///#endregion
}
