package bp.wf.template.frm;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 表单属性
*/
public class MapDataURL extends EntityNoName
{

		///#region 权限控制.
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getNo().equals("admin") == true)
		{
			uac.IsDelete = false;
			uac.IsUpdate = true;
			return uac;
		}
		uac.Readonly();
		return uac;
	}

		///#endregion 权限控制.


		///#region 属性
	/** 
	 物理表
	*/
	public final String getPTable() throws Exception {
		String s = this.GetValStrByKey(MapDataAttr.PTable);
		if (DataType.IsNullOrEmpty(s) == true)
		{
			return this.getNo();
		}
		return s;
	}
	public final void setPTable(String value){
		this.SetValByKey(MapDataAttr.PTable, value);
	}
	/** 
	 URL
	*/
	public final String getUrlExt()  {
		return this.GetValStrByKey(MapDataAttr.UrlExt);
	}
	public final void setUrlExt(String value){
		this.SetValByKey(MapDataAttr.UrlExt, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 表单属性
	*/
	public MapDataURL()
	{
	}
	/** 
	 表单属性
	 
	 @param no 映射编号
	*/
	public MapDataURL(String no) throws Exception
	{
		super(no);
	}
	/** 
	 EnMap
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_MapData", "URL表单属性");


			///#region 基本属性.
		map.AddTBStringPK(MapDataAttr.No, null, "表单编号", true, false, 1, 200, 20);
		map.AddTBString(MapDataAttr.Name, null, "表单名称", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.PTable, null, "存储表", false, false, 0, 500, 20);

		map.AddTBString(MapDataAttr.UrlExt, null, "URL连接", true, false, 0, 500, 20, true);

		//表单的运行类型.
		map.AddDDLSysEnum(MapDataAttr.FrmType, FrmType.FoolForm.getValue(), "表单类型",true, false, MapDataAttr.FrmType);


			///#endregion 基本属性.


			///#region 设计者信息.
		map.AddTBString(MapDataAttr.Designer, null, "设计者", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerContact, null, "联系方式", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerUnit, null, "单位", true, false, 0, 500, 20, true);
		map.AddTBString(MapDataAttr.GUID, null, "GUID", true, true, 0, 128, 20, false);
		map.AddTBString(MapDataAttr.Ver, null, "版本号", true, true, 0, 30, 20);
		map.AddTBStringDoc(MapDataAttr.Note, null, "备注", true, false, true, 10);

		//增加参数字段.
		map.AddTBAtParas(4000);
		map.AddTBInt(MapDataAttr.Idx, 100, "顺序号", false, false);

			///#endregion 设计者信息.


		RefMethod rm = new RefMethod();
		rm.Title = "经典表单设计";
		rm.ClassMethodName = this.toString() + ".DoDesignerFool";
		rm.Icon = "icon-note";
		rm.Visable = true;
		rm.Target = "_blank";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "开发者表单设计器";
		rm.ClassMethodName = this.toString() + ".DoDesignerDev";
		rm.Icon = "icon-note";
		rm.Visable = true;
		rm.Target = "_blank";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		map.AddRefMethod(rm);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	public final String DoDesignerDev() {
		return "../../Admin/FoolFormDesigner/Designer.htm?FK_MapData=" + this.getNo() + "&MyPK=" + this.getNo() + "&IsFirst=1&IsEditMapData=True";
	}
	/** 
	 经典表单设计器
	 
	 @return 
	*/
	public final String DoDesignerFool() {
		return "../../Admin/FoolFormDesigner/Designer.htm?FK_MapData=" + this.getNo() + "&MyPK=" + this.getNo() + "&IsFirst=1&IsEditMapData=True";
	}

	/** 
	 排序字段顺序
	 
	 @return 
	*/
	public final String DoOpenUrl() {
		return this.getUrlExt();
	}
}
