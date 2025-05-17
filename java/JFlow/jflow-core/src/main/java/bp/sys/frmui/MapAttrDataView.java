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
 数据视图
*/
public class MapAttrDataView extends EntityMyPK
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
	 绑定的ID
	*/
	public final String getUIBindKey()  {
		return this.GetValStringByKey(MapAttrAttr.UIBindKey);
	}
	public final void setUIBindKey(String value){
		this.SetValByKey(MapAttrAttr.UIBindKey, value);
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
		if (bp.web.WebUser.getIsAdmin()  == true)
		{
			uac.IsInsert = false;
			uac.IsUpdate = true;
			uac.IsDelete = true;
		}
		return uac;
	}
	/** 
	 数据视图
	*/
	public MapAttrDataView()
	{
	}
	public MapAttrDataView(String mypk) throws Exception {
		this.setMyPK(mypk);
		this.Retrieve();
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

		Map map = new Map("Sys_MapAttr", "数据视图");
		map.IndexField = MapAttrAttr.FK_MapData;


			///#region 基本信息.
		map.AddTBStringPK(MapAttrAttr.MyPK, null, "主键", false, false, 0, 200, 20);
		map.AddTBString(MapAttrAttr.FK_MapData, null, "表单ID", false, false, 1, 100, 20);
		map.AddTBString(MapAttrAttr.Name, null, "名称", true, false, 0, 200, 20);
		map.AddTBString(MapAttrAttr.KeyOfEn, null, "ID", true, true, 1, 200, 20);

		//默认值.
		map.AddTBStringDoc(MapAttrAttr.DefVal, null, "SQL语句", true, false, true);
		map.SetHelperAlert(MapAttrAttr.DefVal, "设置查询语句比如:SELECT No,Name,Addr,Email FROM WF_Emp WHERE FK_Dept=@WebUser.FK_Dept ");

		map.AddTBString(MapAttrAttr.UIBindKey, null, "中文对应", true, false, 0, 100, 20, true);
		map.SetHelperAlert(MapAttrAttr.UIBindKey, "@No=编号@Name=名称@Addr=地址@Email=邮件");

		map.AddTBFloat(MapAttrAttr.UIWidth, 100, "宽度", true, false);

	  //  map.AddBoolean(MapAttrAttr.UIVisible, true, "可见", true, true);
		map.AddDDLSQL(MapAttrAttr.GroupID, 0, "显示的分组", MapAttrString.getSQLOfGroupAttr(), true);
		map.AddTBInt(MapAttrAttr.Idx, 0, "顺序号", true, false);

			///#endregion 基本信息.

		///#region 经典表单。
		////map.AddDDLSysEnum(MapAttrAttr.ColSpan, 1, "单元格数量", true, true, "ColSpanAttrDT",
		////   "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格");
		//////文本占单元格数量
		////map.AddDDLSysEnum(MapAttrAttr.LabelColSpan, 1, "文本单元格数量", true, true, "ColSpanAttrString",
		////    "@1=跨1个单元格@2=跨2个单元格@3=跨3个单元格@4=跨4个单元格");
		//////文本跨行
		////map.AddTBInt(MapAttrAttr.RowSpan, 1, "行数", true, false);
		////显示的分组.
		///#endregion 经典表单。


			///#region 执行的方法.
		RefMethod rm = new RefMethod();
		//rm = new RefMethod();
		//rm.Title = "设置联动";
		//rm.ClassMethodName = this.ToString() + ".DoActiveDDL()";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "设置显示过滤";
		//rm.ClassMethodName = this.ToString() + ".DoAutoFullDLL()";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//map.AddRefMethod(rm);


		//rm = new RefMethod();
		//rm.Title = "填充其他控件";
		//rm.ClassMethodName = this.ToString() + ".DoDDLFullCtrl2019()";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "外键表属性";
		//rm.ClassMethodName = this.ToString() + ".DoSFTable()";
		//rm.refMethodType = RefMethodType.LinkeWinOpen;
		//rm.GroupName = "高级";
		//map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "高级JS设置";
		//rm.ClassMethodName = this.ToString() + ".DoRadioBtns()";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.GroupName = "高级";
		//map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "事件绑函数";
		//rm.ClassMethodName = this.ToString() + ".BindFunction()";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//map.AddRefMethod(rm);


			///#endregion 执行的方法.

		this.set_enMap(map);
		return this.get_enMap();
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
	 外键表属性
	 
	 @return 
	*/
	public final String DoSFTable() {
		return "../../Admin/FoolFormDesigner/GuideSFTableAttr.htm?FK_SFTable=" + this.getUIBindKey();
	}
	/** 
	 高级设置
	 
	 @return 
	*/
	public final String DoRadioBtns() {
		return "../../Admin/FoolFormDesigner/MapExt/RadioBtns.htm?FK_MapData=" + this.getFrmID() + "&ExtType=AutoFull&KeyOfEn=" + this.getKeyOfEn() + "&RefNo=" + this.getMyPK();
	}
	/** 
	 设置填充其他下拉框
	 
	 @return 
	*/
	public final String DoDDLFullCtrl() {
		return "../../Admin/FoolFormDesigner/MapExt/DDLFullCtrl.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn() + "&MyPK=DDLFullCtrl_" + this.getFrmID() + "_" + this.getKeyOfEn();
	}
	public final String DoDDLFullCtrl2019() {
		return "../../Admin/FoolFormDesigner/MapExt/DDLFullCtrl2019.htm?FK_MapData=" + this.getFrmID() + "&ExtType=AutoFull&KeyOfEn=" + this.getKeyOfEn() + "&RefNo=" + this.getMyPK();
	}
	/** 
	 设置下拉框显示过滤
	 
	 @return 
	*/
	public final String DoAutoFullDLL() {
		return "../../Admin/FoolFormDesigner/MapExt/AutoFullDLL.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}
	/** 
	 设置级联
	 
	 @return 
	*/
	public final String DoActiveDDL() {
		return "../../Admin/FoolFormDesigner/MapExt/ActiveDDL.htm?FK_MapData=" + this.getFrmID() + "&KeyOfEn=" + this.getKeyOfEn();
	}

		///#endregion 方法执行.


		///#region 重写的方法.
	/** 
	 删除，把影子字段也要删除.
	*/
	@Override
	protected void afterDelete() throws Exception
	{
		MapAttr attr = new MapAttr();
		attr.setMyPK(this.getFrmID() + "_" + this.getKeyOfEn() + "T");
		attr.Delete();

		//删除相对应的rpt表中的字段
		if (this.getFrmID().contains("ND") == true)
		{
			String fk_mapData = this.getFrmID().substring(0, this.getFrmID().length() - 2) + "Rpt";
			String sql = "DELETE FROM Sys_MapAttr WHERE FK_MapData='" + fk_mapData + "' AND( KeyOfEn='" + this.getKeyOfEn() + "T' OR KeyOfEn='" + this.getKeyOfEn() + "')";
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

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());

		super.afterInsertUpdateAction();
	}

		///#endregion 重写的方法.

}
