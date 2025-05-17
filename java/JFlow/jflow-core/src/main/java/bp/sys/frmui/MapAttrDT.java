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
 日期字段
*/
public class MapAttrDT extends EntityMyPK
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
	/** 
	 绑定的枚举ID
	*/
	public final String getUIBindKey()  {
		return this.GetValStringByKey(MapAttrAttr.UIBindKey);
	}
	public final void setUIBindKey(String value){
		this.SetValByKey(MapAttrAttr.UIBindKey, value);
	}
	/** 
	 数据类型
	*/
	public final int getMyDataType()  {
		return this.GetValIntByKey(MapAttrAttr.MyDataType);
	}
	public final void setMyDataType(int value){
		this.SetValByKey(MapAttrAttr.MyDataType, value);
	}
	public final int getFormat()  {
		return this.GetValIntByKey(MapAttrAttr.IsSupperText);
	}
	public final void setFormat(int value){
		this.SetValByKey(MapAttrAttr.IsSupperText, value);
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
	 日期字段
	*/
	public MapAttrDT()
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

		Map map = new Map("Sys_MapAttr", "日期字段");
		map.IndexField = MapAttrAttr.FK_MapData;


			///#region 基本信息.
		map.AddTBStringPK(MapAttrAttr.MyPK, null, "主键", false, false, 0, 200, 20);
		map.AddTBString(MapAttrAttr.FK_MapData, null, "实体标识", false, false, 1, 100, 20);

		map.AddTBString(MapAttrAttr.Name, null, "字段中文名", true, false, 0, 200, 20);
		map.AddTBString(MapAttrAttr.KeyOfEn, null, "字段名", true, true, 1, 200, 20);

		map.AddDDLSysEnum(MapAttrAttr.MyDataType, 6, "数据类型", true, false);

		map.AddTBString(MapAttrAttr.DefVal, null, "默认值(@RDT为当前日期)", true, false, 0, 100, 20);

		map.AddBoolean(MapAttrAttr.UIVisible, true, "是否可见？", true, true);
		map.AddBoolean(MapAttrAttr.UIIsEnable, true, "是否可编辑？", true, true);
		map.AddBoolean(MapAttrAttr.UIIsInput, false, "是否必填项？", true, true);

		map.AddDDLSysEnum(MapAttrAttr.IsSupperText, 1, "格式", true, true, MapAttrAttr.IsSupperText, "@0=yyyy-MM-dd@1=yyyy-MM-dd HH:mm@2=yyyy-MM-dd HH:mm:ss@3=yyyy-MM@4=HH:mm@5=HH:mm:ss@6=MM-dd@7=yyyy@8=MM");

		map.AddTBString(MapAttrAttr.Tip, null, "激活提示", true, false, 0, 400, 20, true);
		//CCS样式
		map.AddDDLSQL(MapAttrAttr.CSSCtrl, "0", "自定义样式", MapAttrString.getSQLOfCSSAttr(), true);


			///#endregion 基本信息.


			///#region 经典表单
		map.AddDDLSysEnum(MapAttrAttr.ColSpan, 1, "单元格数量", true, true, "ColSpanAttrDT", "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格");

		//文本占单元格数量
		map.AddDDLSysEnum(MapAttrAttr.LabelColSpan, 1, "文本单元格数量", true, true, "ColSpanAttrString", "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格");

		//文本跨行
		map.AddTBInt(MapAttrAttr.RowSpan, 1, "行数", true, false);
		//显示的分组.
		map.AddDDLSQL(MapAttrAttr.GroupID, 0, "显示的分组", MapAttrString.getSQLOfGroupAttr(), true);

		map.AddTBInt(MapAttrAttr.Idx, 0, "顺序号", true, false); //@李国文


			///#endregion 经典表单。


			///#region 执行的方法.
		RefMethod rm = new RefMethod();

		//rm = new RefMethod();
		//rm.Title = "自动计算";
		//rm.ClassMethodName = this.ToString() + ".DoAutoFull()";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "正则表达式";
		rm.ClassMethodName = this.toString() + ".DoRegularExpression()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-settings"; //正则表达式
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "事件绑函数";
		rm.ClassMethodName = this.toString() + ".BindFunction()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-puzzle"; //事件绑定函数。
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "日期输入限制";
		rm.ClassMethodName = this.toString() + ".DataFieldInputRole()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-ban";
		map.AddRefMethod(rm);


			///#endregion 执行的方法.

		this.set_enMap(map);
		return this.get_enMap();
	}

	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (this.getFormat() == 0 && this.getMyDataType() == 7)
		{
			this.setFormat(1);
		}

		return super.beforeInsert();
	}

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		//if (this.Format == 0 && this.getMyDataType() == 7)
		//    this.Format = 1;

		//设置时间类型.
		int format = this.getFormat();
		if (format == 0 || format == 3 || format == 6)
		{
			this.setMyDataType(6);
		}
		else
		{
			this.setMyDataType(7);
		}

		return super.beforeUpdateInsertAction();
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


		///#region 方法执行.
	/** 
	 绑定函数
	 
	 @return 
	*/
	public final String BindFunction() {
		return "../../Admin/FoolFormDesigner/MapExt/BindFunction.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&T=" + LocalDateTime.now().toString();
	}
	/** 
	 日期输入限制
	 
	 @return 
	*/
	public final String DataFieldInputRole() {
		return "../../Admin/FoolFormDesigner/MapExt/DataFieldInputRole.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}
	/** 
	 自动计算
	 
	 @return 
	*/
	public final String DoAutoFull() {
		return "../../Admin/FoolFormDesigner/MapExt/AutoFull.htm?FK_MapData=" + this.getFrmID() + "&ExtType=AutoFull&KeyOfEn=" + this.getKeyOfEn();
	}
	/** 
	 正则表达式
	 
	 @return 
	*/
	public final String DoRegularExpression() {
		return "../../Admin/FoolFormDesigner/MapExt/RegularExpression.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=" + this.getMyPK();
	}

		///#endregion 方法执行.
}
