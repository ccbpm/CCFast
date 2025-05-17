package bp.ccfast.portal.windowext;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import bp.ccfast.portal.*;
import java.util.*;

/** 
 变量信息
*/
public class Dtl extends EntityMyPK
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
	 变量信息
	*/
	public Dtl()
	{
	}
	/** 
	 EnMap
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("GPM_WindowTemplateDtl", "Dtl变量信息");

		map.AddMyPK(true);
		map.AddTBString(DtlAttr.Name, null, "标签", true, false, 0, 300, 20, true);
		map.AddTBString(DtlAttr.FontColor, null, "颜色", true, false, 0, 300, 20, true);
		map.AddTBString(DtlAttr.Exp0, null, "表达式0", true, false, 0, 300, 20, true);
		map.AddTBString(DtlAttr.Exp1, null, "表达式1", true, false, 0, 300, 20, true);
		map.AddTBString(DtlAttr.DBSrc, null, "数据源", true, false, 0, 100, 20, true);

		map.AddTBInt(DtlAttr.WindowsShowType, 0, "显示类型", true, false);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

}
