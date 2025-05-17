package bp.ccfast.portal.windowext;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import bp.ccfast.portal.*;
import java.util.*;

/** 
 变量信息
*/
public class HtmlVarDtl extends EntityMyPK
{

		///#region 属性.
	/** 
	 表达式
	*/
	public final String getRefPK()  {
		return this.GetValStrByKey(DtlAttr.RefPK);
	}
	public final void setRefPK(String value)  {
		this.SetValByKey(DtlAttr.RefPK, value);
	}
	public final String getExp0()  {
		return this.GetValStrByKey(DtlAttr.Exp0);
	}
	public final void setExp0(String value)  {
		this.SetValByKey(DtlAttr.Exp0, value);
	}
	public final int getDBType() {
		return this.GetValIntByKey(DtlAttr.DBType);
	}
	public final void setDBType(int value)  {
		this.SetValByKey(DtlAttr.DBType, value);
	}
	public final String getFontColor()  {
		return this.GetValStrByKey(DtlAttr.FontColor);
	}
	public final void setFontColor(String value)  {
		this.SetValByKey(DtlAttr.FontColor, value);
	}
	public final String getUrlExt()  {
		return this.GetValStrByKey(DtlAttr.UrlExt);
	}
	public final void setUrlExt(String value)  {
		this.SetValByKey(DtlAttr.UrlExt, value);
	}

		///#endregion 属性.


		///#region 权限控制.
	/** 
	 控制权限
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();

		uac.IsInsert = true;
		uac.IsDelete = true;
		uac.IsView = true;
		uac.IsUpdate = true;
		return uac;
	}

		///#endregion 权限控制.


		///#region 属性

		///#endregion 属性


		///#region 构造方法
	/** 
	 变量信息
	*/
	public HtmlVarDtl()
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

		Map map = new Map("GPM_WindowTemplateDtl", "数据项");

		map.AddMyPK(false);
		map.AddTBString(DtlAttr.RefPK, null, "RefPK", false, false, 0, 40, 20, false);
		map.AddTBString(DtlAttr.Name, null, "标签", true, false, 0, 300, 70, true);

		map.AddDDLSysEnum(DtlAttr.DBType, 0, "数据源类型", true, true, "WindowsDBType", "@0=数据库查询SQL@1=执行Url返回Json@2=执行\\DataUser\\JSLab\\Windows.js的函数.");
		map.AddDDLEntities(DtlAttr.DBSrc, null, "数据源", new bp.sys.SFDBSrcs(), true);

		map.AddTBString(DtlAttr.Exp0, null, "表达式", true, false, 0, 300, 700, true);
		map.AddTBString(DtlAttr.FontColor, null, "字体风格", true, false, 0, 300, 100, true);

		map.AddTBString(DtlAttr.UrlExt, "", "链接/函数", true, false, 0, 300, 200, true);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception {
		this.setMyPK(DBAccess.GenerGUID(0, null, null));
		return super.beforeInsert();
	}

}
