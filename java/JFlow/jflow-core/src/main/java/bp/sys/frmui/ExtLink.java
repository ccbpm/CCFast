package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.en.Map;
import bp.sys.*;
import java.util.*;

/** 
 超连接
*/
public class ExtLink extends EntityMyPK
{

		///#region 属性
	/** 
	 目标
	*/
	public final String getTarget()  {
		return this.GetValStringByKey(MapAttrAttr.Tag1);
	}
	public final void setTarget(String value){
		this.SetValByKey(MapAttrAttr.Tag1, value);
	}
	/** 
	 URL
	*/
	public final String getURL()  {
		return this.GetValStringByKey(MapAttrAttr.Tag2).replace("#", "@");
	}
	public final void setURL(String value){
		this.SetValByKey(MapAttrAttr.Tag2, value);
	}
	/** 
	 FK_MapData
	*/
	public final String getFrmID()  {
		return this.GetValStrByKey(MapAttrAttr.FK_MapData);
	}
	public final void setFrmID(String value){
		this.SetValByKey(MapAttrAttr.FK_MapData, value);
	}
	/** 
	 Text
	*/
	public final String getName()  {
		return this.GetValStrByKey(MapAttrAttr.Name);
	}
	public final void setName(String value){
		this.SetValByKey(MapAttrAttr.Name, value);
	}


		///#endregion


		///#region 构造方法
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.Readonly();
		if (bp.web.WebUser.getNo().equals("admin") == true)
		{

			uac.IsUpdate = true;
			uac.IsDelete = true;
		}

		return uac;
	}
	/** 
	 超连接
	*/
	public ExtLink()
	{
	}
	/** 
	 超连接
	 
	 @param mypk
	*/
	public ExtLink(String mypk) throws Exception {
		this.setMyPK(mypk);
		this.Retrieve();
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
		Map map = new Map("Sys_MapAttr", "超连接");
		map.IndexField = MapAttrAttr.FK_MapData;


			///#region 通用的属性.
		map.AddMyPK();
		map.AddTBString(MapAttrAttr.FK_MapData, null, "表单ID", true, true, 1, 100, 20);
		map.AddTBString(MapAttrAttr.KeyOfEn, null, "字段", true, true, 1, 100, 20);
		map.AddDDLSQL(MapAttrAttr.GroupID, 0, "显示的分组", MapAttrString.getSQLOfGroupAttr(), true);

		//单元格数量 2013-07-24 增加
		map.AddDDLSysEnum(MapAttrAttr.ColSpan, 1, "TextBox单元格数量", true, true, "ColSpanAttrDT", "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格@5=跨5个单元格@6=跨6个单元格");
		map.SetHelperAlert(MapAttrAttr.ColSpan, "对于经典表单有效: 标识该字段TextBox横跨的宽度,占的单元格数量.");

		//文本占单元格数量
		map.AddDDLSysEnum(MapAttrAttr.LabelColSpan, 1, "Label单元格数量", true, true, "ColSpanAttrString", "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格@5=跨6个单元格@6=跨6个单元格");
		map.SetHelperAlert(MapAttrAttr.LabelColSpan, "对于经典表单有效: 标识该字段Lable，标签横跨的宽度,占的单元格数量.");

		map.AddTBInt(MapAttrAttr.RowSpan, 1, "行数", true, false);

			///#endregion 通用的属性.


			///#region 个性化属性.
		map.AddTBString(MapAttrAttr.Name, "New Link", "标签", true, false, 0, 500, 20, true);
		map.AddTBString(MapAttrAttr.Tag1, "_blank", "连接目标", true, false, 0, 20, 20);
		map.SetHelperAlert(MapAttrAttr.Tag1, "比如:_blank,_parent,_self");
		map.AddTBString(MapAttrAttr.Tag2, null, "URL", true, false, 0, 500, 20, true);

			///#endregion 个性化属性.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
