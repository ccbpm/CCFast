package bp.ccfast.portal.windowext;

import bp.en.*; import bp.en.Map;
import bp.ccfast.portal.*;
import bp.en.Map;

/** 
 百分比扇形图
*/
public class ChartRate extends EntityNoName
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
	 百分比扇形图
	*/
	public ChartRate()
	{
	}
	/** 
	 百分比扇形图
	 
	 @param no
	*/
	public ChartRate(String no) throws Exception  {
		this.setNo(no);
		this.Retrieve();
	}
	/**
	 * EnMap
	 */
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("GPM_WindowTemplate", "百分比扇形图");


			///#region 基本信息.
		map.AddTBStringPK(WindowTemplateAttr.No, null, "编号", true, true, 1, 40, 200);
		map.AddTBInt(WindowTemplateAttr.ColSpan, 1, "占的列数", true, false);
		map.SetHelperAlert(WindowTemplateAttr.ColSpan, "画布按照4列划分布局，输入的输在在1=4之间.");
		map.AddTBString(WindowTemplateAttr.Name, null, "标题", true, false, 0, 300, 20, true);
		map.AddTBString(WindowTemplateAttr.Icon, null, "Icon", true, false, 0, 100, 20, true);

			///#endregion 基本信息.

		map.AddTBString(WindowTemplateAttr.MoreLab, null, "更多标签", true, false, 0, 300, 20, true);
		map.AddTBString(WindowTemplateAttr.MoreUrl, null, "更多链接", true, false, 0, 300, 20, true);
		map.AddDDLSysEnum(WindowTemplateAttr.MoreLinkModel, 0, "打开方式", true, true, WindowTemplateAttr.MoreLinkModel, "@0=新窗口@1=本窗口@2=覆盖新窗口");


			///#region 数据源.
		map.AddDDLSysEnum(WindowTemplateAttr.DBType, 0, "数据源类型", true, true, "WindowsDBType", "@0=数据库查询SQL@1=执行Url返回Json@2=执行\\DataUser\\JSLab\\Windows.js的函数.");
		map.AddDDLEntities(WindowTemplateAttr.DBSrc, null, "数据源", new bp.sys.SFDBSrcs(), true);


		map.AddTBString(WindowTemplateAttr.LabOfFZ, null, "分子标签", true, false, 0, 100, 20);
		map.AddTBStringDoc(WindowTemplateAttr.SQLOfFZ, null, "分子表达式", true, false, true, 10);

		map.AddTBString(WindowTemplateAttr.LabOfFM, null, "分母标签", true, false, 0, 100, 20);
		map.AddTBStringDoc(WindowTemplateAttr.SQLOfFM, null, "分子表达式", true, false, true, 10);

		map.AddTBString(WindowTemplateAttr.LabOfRate, null, "率标签", true, false, 0, 100, 20);


			///#endregion 数据源.

		this.set_enMap(map);

		return this.get_enMap();
	}

		///#endregion
}
