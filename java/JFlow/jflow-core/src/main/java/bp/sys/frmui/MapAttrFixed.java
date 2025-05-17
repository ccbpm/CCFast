package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;

/** 
 系统定位组件
*/
public class MapAttrFixed extends EntityMyPK
{

		///#region 文本字段参数属性.
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
	 实体属性
	*/
	public MapAttrFixed()
	{
	}
	/** 
	 实体属性
	*/
	public MapAttrFixed(String myPK) throws Exception {
		this.setMyPK(myPK);
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

		Map map = new Map("Sys_MapAttr", "系统定位组件");


			///#region 基本字段信息.
		map.AddTBStringPK(MapAttrAttr.MyPK, null, "主键", false, false, 0, 200, 20);
		map.AddTBString(MapAttrAttr.FK_MapData, null, "表单ID", false, false, 1, 100, 20);
		map.AddTBString(MapAttrAttr.Name, null, "字段中文名", true, false, 0, 200, 20, true);

		map.AddTBString(MapAttrAttr.KeyOfEn, null, "字段名", true, true, 1, 200, 20);

		map.AddTBInt(MapAttrAttr.MinLen, 0, "最小长度", true, false);
		map.AddTBInt(MapAttrAttr.MaxLen, 50, "最大长度", true, false);
		map.SetHelperAlert(MapAttrAttr.MaxLen, "定义该字段的字节长度.");


		map.AddTBFloat(MapAttrAttr.UIWidth, 100, "宽度", true, false);
		map.SetHelperAlert(MapAttrAttr.UIWidth, "对自由表单,从表有效,显示文本框的宽度.");
		map.AddBoolean(MapAttrAttr.UIIsEnable, true, "是否启用？", true, true);

		map.AddTBInt(MapAttrAttr.UIContralType, 0, "控件", true, false);

		map.AddDDLSQL(MapAttrAttr.CSSCtrl, "0", "自定义样式", MapAttrString.getSQLOfCSSAttr(), true);

			///#endregion 基本字段信息.


			///#region 经典表单
		//单元格数量 2013-07-24 增加
		map.AddDDLSysEnum(MapAttrAttr.ColSpan, 1, "TextBox单元格数量", true, true, "ColSpanAttrDT", "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格@5=跨5个单元格@6=跨6个单元格");
		map.SetHelperAlert(MapAttrAttr.ColSpan, "对于经典表单有效: 标识该字段TextBox横跨的宽度,占的单元格数量.");

		//文本占单元格数量
		map.AddDDLSysEnum(MapAttrAttr.LabelColSpan, 1, "Label单元格数量", true, true, "ColSpanAttrString", "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格@5=跨6个单元格@6=跨6个单元格");
		map.SetHelperAlert(MapAttrAttr.LabelColSpan, "对于经典表单有效: 标识该字段Lable，标签横跨的宽度,占的单元格数量.");

		//文本跨行
		map.AddTBInt(MapAttrAttr.RowSpan, 1, "行数", true, false);

		//显示的分组.
		map.AddDDLSQL(MapAttrAttr.GroupID, 0, "显示的分组", MapAttrString.getSQLOfGroupAttr(), true);


		map.AddTBInt(MapAttrAttr.Idx, 0, "顺序号", true, false);
		map.SetHelperAlert(MapAttrAttr.Idx, "对经典表单有效:用于调整字段在同一个分组中的顺序.");


			///#endregion 经典表单

		this.set_enMap(map);
		return this.get_enMap();
	}

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		MapAttr attr = new MapAttr();
		attr.setMyPK(this.getMyPK());
		attr.RetrieveFromDBSources();

		//强制设置为评论组件.
		this.SetValByKey(MapAttrAttr.UIContralType, UIContralType.Fixed.getValue());

		if (this.GetValStrByKey("GroupID").equals("无") == true)
		{
			this.SetValByKey("GroupID", "0");
		}

		return super.beforeUpdateInsertAction();
	}



	/** 
	 删除
	*/
	@Override
	protected void afterDelete() throws Exception
	{
		//删除经度纬度的字段
		MapAttr mapAttr = new MapAttr(this.getFrmID() + "_JD");
		mapAttr.Delete();

		mapAttr = new MapAttr(this.getFrmID() + "_WD");
		mapAttr.Delete();

		//删除相对应的rpt表中的字段
		if (this.getFrmID().contains("ND") == true)
		{
			String fk_mapData = this.getFrmID().substring(0, this.getFrmID().length() - 2) + "Rpt";
			String sql = "DELETE FROM Sys_MapAttr WHERE FK_MapData='" + fk_mapData + "' AND( KeyOfEn='" + this.getKeyOfEn() + "' OR KeyOfEn='JD' OR KeyOfEn='WD')";
			DBAccess.RunSQL(sql);
		}

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());

		super.afterDelete();
	}


	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		MapAttr mapAttr = new MapAttr();
		mapAttr.setMyPK(this.getMyPK());
		mapAttr.RetrieveFromDBSources();
		mapAttr.Update();

		//判断表单中是否存在经度、维度字段
		mapAttr = new MapAttr();
		mapAttr.setMyPK(this.getFrmID() + "_" + "JD");
		if (mapAttr.RetrieveFromDBSources() == 0)
		{
			mapAttr.setFrmID(this.getFrmID());
			mapAttr.setKeyOfEn("JD");
			mapAttr.setName("经度");
			mapAttr.setGroupID(1);
			mapAttr.setUIContralType(UIContralType.TB);
			mapAttr.setMyDataType(1);
			mapAttr.setLGType(FieldTypeS.forValue(0));
			mapAttr.setUIVisible(false);
			mapAttr.setUIIsEnable(false);
			mapAttr.setUIIsInput(true);
			mapAttr.setUIWidth(150);
			mapAttr.setUIHeight(23);
			mapAttr.Insert(); //插入字段.
		}

		mapAttr.setMyPK(this.getFrmID() + "_" + "WD");
		if (mapAttr.RetrieveFromDBSources() == 0)
		{
			mapAttr.setFrmID(this.getFrmID());
			mapAttr.setKeyOfEn("WD");
			mapAttr.setName("纬度");
			mapAttr.setGroupID(1);
			mapAttr.setUIContralType(UIContralType.TB);
			mapAttr.setMyDataType(1);
			mapAttr.setLGType(FieldTypeS.forValue(0));
			mapAttr.setUIVisible(false);
			mapAttr.setUIIsEnable(false);
			mapAttr.setUIIsInput(true);
			mapAttr.setUIWidth(150);
			mapAttr.setUIHeight(23);
			mapAttr.Insert(); //插入字段.
		}

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());

		super.afterInsertUpdateAction();
	}

		///#endregion
}
