package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;
import java.time.*;

/** 
 Boolen字段
*/
public class MapAttrBoolen extends EntityMyPK
{

		///#region 文本字段参数属性.
	public final String getDefVal()  {
		return this.GetValStringByKey(MapAttrAttr.DefVal);
	}
	public final void setDefVal(String value){
		this.SetValByKey(MapAttrAttr.DefVal, value);
	}
	/** 
	 表单ID
	*/
	public final String getFrmID()  {
		return this.GetValStringByKey(MapAttrAttr.FK_MapData);
	}
	public final void setFrmID(String value){
		this.SetValByKey(MapAttrAttr.FK_MapData, value);
	}
	/** 
	 字段
	*/
	public final String getKeyOfEn()  {
		return this.GetValStringByKey(MapAttrAttr.KeyOfEn);
	}
	public final void setKeyOfEn(String value){
		this.SetValByKey(MapAttrAttr.KeyOfEn, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 控制权限
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsInsert = false;
		uac.IsUpdate = true;
		uac.IsDelete = true;
		return uac;
	}
	/** 
	 Boolen字段
	*/
	public MapAttrBoolen()
	{
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

		Map map = new Map("Sys_MapAttr", "Boolen字段");
		map.IndexField = MapAttrAttr.FK_MapData;


			///#region 基本信息.

		map.AddTBStringPK(MapAttrAttr.MyPK, null, "主键", false, false, 0, 200, 20);
		map.AddTBString(MapAttrAttr.FK_MapData, null, "表单ID", false, false, 1, 100, 20);

		map.AddTBString(MapAttrAttr.Name, null, "字段中文名", true, false, 0, 200, 20, true);
		map.AddTBString(MapAttrAttr.KeyOfEn, null, "字段名", true, true, 1, 200, 20);

		//数据类型.
		map.AddDDLSysEnum(MapAttrAttr.MyDataType, 4, "数据类型", true, false);
		map.AddBoolean(MapAttrAttr.UIVisible, true, "是否可见？", true, true);

		map.AddTBString(MapAttrAttr.DefVal, "0", "默认值(是否选中？0=否,1=是)", true, false, 0, 10, 20);

		map.AddBoolean(MapAttrAttr.UIIsEnable, true, "是否可编辑？", true, true);
		map.AddTBStringDoc(MapAttrAttr.Tip, null, "激活提示", true, false);

			///#endregion 基本信息.


			///#region 经典表单。
		//单元格数量 2013-07-24 增加。
		map.AddDDLSysEnum(MapAttrAttr.ColSpan, 1, "单元格数量", true, true, "ColSpanAttrDT", "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格");

		//文本占单元格数量
		map.AddDDLSysEnum(MapAttrAttr.LabelColSpan, 1, "文本单元格数量", true, true, "ColSpanAttrString", "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格");

		//文本跨行
		map.AddTBInt(MapAttrAttr.RowSpan, 1, "行数", true, false);

		//显示的分组.
		map.AddDDLSQL(MapAttrAttr.GroupID, 0, "显示的分组", MapAttrString.getSQLOfGroupAttr(), true);

		map.AddTBFloat(MapAttrAttr.UIWidth, 100, "宽度", true, false);
		map.AddTBInt(MapAttrAttr.Idx, 0, "顺序号", true, false);

		//CCS样式
		map.AddDDLSQL(MapAttrAttr.CSSCtrl, "0", "自定义样式", MapAttrString.getSQLOfCSSAttr(), true);

			///#endregion 经典表单。


		RefMethod rm = new RefMethod();
		rm.Title = "事件绑函数";
		rm.ClassMethodName = this.toString() + ".BindFunction()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "高级JS设置";
		rm.ClassMethodName = this.toString() + ".DoCheckboxs()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "全局风格定义";
		rm.ClassMethodName = this.toString() + ".DoGloValStyles()";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Icon = "icon-wrench";
		rm.RefAttrKey = MapAttrAttr.CSSCtrl;
		map.AddRefMethod(rm);

		this.set_enMap(map);
		return this.get_enMap();
	}

	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		MapAttr mapAttr = new MapAttr();
		mapAttr.setMyPK(this.getMyPK());
		mapAttr.RetrieveFromDBSources();
		mapAttr.Update();

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());


			///#region 修改默认值.
		//如果没默认值.
		if (DataType.IsNullOrEmpty(this.getDefVal()) == true)
		{
			this.setDefVal("0");
		}
		MapData md = new MapData();
		md.setNo(this.getFrmID());
		if (md.RetrieveFromDBSources() == 1)
		{
			DBAccess.UpdateTableColumnDefaultVal(md.getPTable(), this.getKeyOfEn(), Integer.parseInt(this.getDefVal()));
		}

			///#endregion 修改默认值.

		super.afterInsertUpdateAction();
	}

	/** 
	 删除后清缓存
	*/
	@Override
	protected void afterDelete() throws Exception
	{
		//删除相对应的rpt表中的字段
		if (this.getFrmID().contains("ND") == true)
		{
			String fk_mapData = this.getFrmID().substring(0, this.getFrmID().length() - 2) + "Rpt";
			String sql = "DELETE FROM Sys_MapAttr WHERE FK_MapData='" + fk_mapData + "' AND KeyOfEn='" + this.getKeyOfEn() + "'";
			DBAccess.RunSQL(sql);
		}
		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());
		super.afterDelete();
	}

		///#endregion


		///#region 基本功能.
	public final String DoGloValStyles() {
		return "../../Admin/FoolFormDesigner/StyletDfine/GloValStyles.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=" + this.getMyPK();
	}
	/** 
	 绑定函数
	 
	 @return 
	*/
	public final String BindFunction() {
		return "../../Admin/FoolFormDesigner/MapExt/BindFunction.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&D=" + LocalDateTime.now();
	}
	/** 
	 高级设置
	 
	 @return 
	*/
	public final String DoCheckboxs() throws Exception {

		return "../../Admin/FoolFormDesigner/MapExt/RadioBtns.htm?FK_MapData=" + this.getFrmID() + "&ExtType=AutoFull&KeyOfEn=" + this.getKeyOfEn() + "&RefNo=" + this.getMyPK();
	}

	///#endregion
}
