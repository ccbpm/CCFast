package bp.wf.template.frm;

import bp.sys.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 表单
*/
public class CCFrm extends EntityNoName
{

		///#region 基本属性
	public FrmNode HisFrmNode = null;
	public final String getPTable()  {
		return this.GetValStringByKey(CCFrmAttr.PTable);
	}
	public final void setPTable(String value){
		this.SetValByKey(CCFrmAttr.PTable, value);
	}
	public final String getURL()  {
		return this.GetValStringByKey(CCFrmAttr.URL);
	}
	public final void setURL(String value){
		this.SetValByKey(CCFrmAttr.URL, value);
	}
	public final FrmType getHisFrmType() {
		return FrmType.forValue(this.GetValIntByKey(CCFrmAttr.FrmType));
	}
	public final void setHisFrmType(FrmType value){
		this.SetValByKey(CCFrmAttr.FrmType, value.getValue());
	}

		///#endregion


		///#region 构造方法
	/** 
	 CCFrm
	*/
	public CCFrm()
	{
	}
	/** 
	 CCFrm
	 
	 @param no
	*/
	public CCFrm(String no) throws Exception
	{
		super(no);

	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_MapData", "表单");
		map.setCodeStruct( "4");

		map.AddTBStringPK(CCFrmAttr.No, null, null, true, true, 1, 200, 4);
		map.AddTBString(CCFrmAttr.Name, null, null, true, false, 0, 50, 10);
		map.AddTBString(CCFrmAttr.FK_Flow, null, "独立表单属性:FK_Flow", true, false, 0, 4, 10);
	 //   map.AddDDLSysEnum(CCFrmAttr.CCFrmType, 0, "独立表单属性:运行类型", true, false, CCFrmAttr.CCFrmType);

		//表单的运行类型.
		map.AddDDLSysEnum(CCFrmAttr.FrmType, FrmType.FoolForm.getValue(), "表单类型",true, false, CCFrmAttr.FrmType);

		map.AddTBString(CCFrmAttr.PTable, null, "物理表", true, false, 0, 50, 10);
		map.AddTBInt(CCFrmAttr.DBURL, 0, "DBURL", true, false);

		// 如果是个嵌入式表单.
		map.AddTBString(CCFrmAttr.URL, null, "Url", true, false, 0, 50, 10);

		//表单类别.
		map.AddTBString(MapDataAttr.FK_FormTree, "01", "表单类别", true, false, 0, 500, 20);

		map.AddTBInt(MapDataAttr.FrmW, 900, "表单宽度", true, false);

		this.set_enMap(map);
		return this.get_enMap();
	}
	public final int getFrmW()  {
		return this.GetValIntByKey(MapDataAttr.FrmW);
	}

		///#endregion
}
