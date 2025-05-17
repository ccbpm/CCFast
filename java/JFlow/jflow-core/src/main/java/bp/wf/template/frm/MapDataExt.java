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
public class MapDataExt extends EntityNoName
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


		///#region 自动计算属性.
	/** 
	 左边界.
	*/
	public final float getMaxLeft() {
		return this.GetParaFloat(MapDataAttr.MaxLeft, 0);
	}
	public final void setMaxLeft(float value)  {
		this.SetPara(MapDataAttr.MaxLeft, value);
	}
	/** 
	 右边界
	*/
	public final float getMaxRight() {
		return this.GetParaFloat(MapDataAttr.MaxRight, 0);
	}
	public final void setMaxRight(float value)  {
		this.SetPara(MapDataAttr.MaxRight, value);
	}
	/** 
	 最高top
	*/
	public final float getMaxTop() {
		return this.GetParaFloat(MapDataAttr.MaxTop, 0);
	}
	public final void setMaxTop(float value)  {
		this.SetPara(MapDataAttr.MaxTop, value);
	}
	/** 
	 最低
	*/
	public final float getMaxEnd() {
		return this.GetParaFloat(MapDataAttr.MaxEnd, 0);
	}
	public final void setMaxEnd(float value)  {
		this.SetPara(MapDataAttr.MaxEnd, value);
	}

		///#endregion 自动计算属性.


		///#region 报表属性(参数方式存储).
	/** 
	 是否关键字查询
	*/
	public final boolean getItIsSearchKey() {
		return this.GetParaBoolen(MapDataAttr.IsSearchKey, true);
	}
	public final void setItIsSearchKey(boolean value)  {
		this.SetPara(MapDataAttr.IsSearchKey, value);
	}
	/** 
	 时间段查询方式
	*/
	public final DTSearchWay getDTSearchWay() {
		return DTSearchWay.forValue(this.GetParaInt(MapDataAttr.DTSearchWay, 0));
	}
	public final void setDTSearchWay(DTSearchWay value)  {
		this.SetPara(MapDataAttr.DTSearchWay, value.getValue());
	}
	/** 
	 时间字段
	*/
	public final String getDTSearchKey() {
		return this.GetParaString(MapDataAttr.DTSearchKey);
	}
	public final void setDTSearchKey(String value)  {
		this.SetPara(MapDataAttr.DTSearchKey, value);
	}
	/** 
	 查询外键枚举字段
	*/
	public final String getRptSearchKeys() {
		return this.GetParaString(MapDataAttr.RptSearchKeys, "*");
	}
	public final void setRptSearchKeys(String value)  {
		this.SetPara(MapDataAttr.RptSearchKeys, value);
	}

		///#endregion 报表属性(参数方式存储).


		///#region 外键属性
	public final String getVer()  {
		return this.GetValStringByKey(MapDataAttr.Ver);
	}
	public final void setVer(String value){
		this.SetValByKey(MapDataAttr.Ver, value);
	}
	/** 
	 顺序号
	*/
	public final int getIdx()  {
		return this.GetValIntByKey(MapDataAttr.Idx);
	}
	public final void setIdx(int value){
		this.SetValByKey(MapDataAttr.Idx, value);
	}

		///#endregion


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
	public final DBUrlType getHisDBUrl()
	{
		return DBUrlType.AppCenterDSN;
	}
	public final AppType getHisAppType() {
		return AppType.forValue(this.GetValIntByKey(MapDataAttr.AppType));
	}
	public final void setHisAppType(AppType value){
		this.SetValByKey(MapDataAttr.AppType, value.getValue());
	}
	/** 
	 备注
	*/
	public final String getNote()  {
		return this.GetValStrByKey(MapDataAttr.Note);
	}
	public final void setNote(String value){
		this.SetValByKey(MapDataAttr.Note, value);
	}
	/** 
	 是否有CA.
	*/
	public final boolean getItIsHaveCA() {
		return this.GetParaBoolen("IsHaveCA", false);

	}
	public final void setItIsHaveCA(boolean value)  {
		this.SetPara("IsHaveCA", value);
	}

	/** 
	 类别，可以为空.
	*/
	public final String getFKFormTree()  {
		return this.GetValStrByKey(MapDataAttr.FK_FormTree);
	}
	public final void setFKFormTree(String value){
		this.SetValByKey(MapDataAttr.FK_FormTree, value);
	}
	/** 
	 从表集合.
	*/
	public final String getDtls()  {
		return this.GetValStrByKey(MapDataAttr.Dtls);
	}
	public final void setDtls(String value){
		this.SetValByKey(MapDataAttr.Dtls, value);
	}
	/** 
	 主键
	*/
	public final String getEnPK() throws Exception {
		String s = this.GetValStrByKey(MapDataAttr.EnPK);
		if (DataType.IsNullOrEmpty(s))
		{
			return "OID";
		}
		return s;
	}
	public final void setEnPK(String value){
		this.SetValByKey(MapDataAttr.EnPK, value);
	}
	private Entities _HisEns = null;
	public final Entities getHisEns(){
		if (_HisEns == null)
		{
			_HisEns = ClassFactory.GetEns(this.getNo());
		}
		return _HisEns;
	}
	public final Entity getHisEn() {
		return this.getHisEns().getNewEntity();
	}
	public final float getFrmW()  {
		return this.GetValFloatByKey(MapDataAttr.FrmW);
	}
	public final void setFrmW(float value){
		this.SetValByKey(MapDataAttr.FrmW, value);
	}
	///// <summary>
	///// 表单控制方案
	///// </summary>
	//public String Slns
	//{
	//    get
	//    {
	//        return this.GetValStringByKey(MapDataAttr.Slns);
	//    }
	//    set
	//    {
	//        this.SetValByKey(MapDataAttr.Slns, value);
	//    }
	//}

	/** 
	 表格显示的列
	*/
	public final int getTableCol() throws Exception {
		int i = this.GetValIntByKey(MapDataAttr.TableCol);
		if (i == 0 || i == 1)
		{
			return 4;
		}
		return i;
	}
	public final void setTableCol(int value){
		this.SetValByKey(MapDataAttr.TableCol, value);
	}


		///#endregion


		///#region 构造方法
	/** 
	 表单属性
	*/
	public MapDataExt()
	{
	}
	/** 
	 表单属性
	 
	 @param no 映射编号
	*/
	public MapDataExt(String no) throws Exception
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
		Map map = new Map("Sys_MapData", "表单属性");

		map.setCodeStruct( "4");


			///#region 基本属性.
		map.AddGroupAttr("基本属性", "");
		map.AddTBStringPK(MapDataAttr.No, null, "表单编号", true, false, 1, 190, 20);
		map.AddTBString(MapDataAttr.Name, null, "表单名称", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.PTable, null, "存储表", true, false, 0, 500, 20);

		//表单的运行类型.
		map.AddDDLSysEnum(MapDataAttr.FrmType, FrmType.FoolForm.getValue(), "表单类型",true, true, MapDataAttr.FrmType);

		map.AddTBString(MapDataAttr.UrlExt, null, "URL连接(对嵌入式表单有效)", true, false, 0, 500, 20, true);
		//数据源.
		map.AddDDLEntities(MapDataAttr.DBSrc, "local", "数据源", new SFDBSrcs(), true);

		map.AddDDLEntities(MapDataAttr.FK_FormTree, "01", "表单类别", new SysFormTrees(), true);

			///#endregion 基本属性.


			///#region 设计者信息.
		map.AddGroupAttr("设计者信息", "");
		map.AddTBString(MapDataAttr.Designer, null, "设计者", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerContact, null, "联系方式", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerUnit, null, "单位", true, false, 0, 500, 20,true);
		map.AddTBString(MapDataAttr.GUID, null, "GUID", true, true, 0, 128, 20,false);
		map.AddTBString(MapDataAttr.Ver, null, "版本号", true, true, 0, 30, 20);
		map.AddTBStringDoc(MapDataAttr.Note, null, "备注", true, false, true, 10);

		//增加参数字段.
		map.AddTBAtParas(4000);
		map.AddTBInt(MapDataAttr.Idx, 100, "顺序号", false, false);

			///#endregion 设计者信息.

		//查询条件.
		map.AddSearchAttr(MapDataAttr.DBSrc, 130);

		//RefMethod rm = new RefMethod();
		//rm.Title = "设计自由表单"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoDFrom";
		//rm.Icon = ../../Img/Form.png";
		//rm.Visable = true;
		//rm.Target = "_blank";
		//map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "设计经典表单"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoDFromCol4";
		//rm.Icon = ../../Img/Form.png";
		//rm.Visable = true;
		//rm.Target = "_blank";
		//map.AddRefMethod(rm);


			///#region 方法 - 基本功能.
		map.AddGroupMethod("基本功能");

		RefMethod rm = new RefMethod();
		rm.Title = "装载填充"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoPageLoadFull";
		rm.Icon = "../../WF/Img/FullData.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "表单事件"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoEvent";
		rm.Icon = "../../WF/Img/Event.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "批量设置验证规则";
		rm.Icon = "../../WF/Img/Btn/DTS.gif";
		rm.ClassMethodName = this.toString() + ".DoRegularExpressionBatch";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "批量修改字段"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoBatchEditAttr";
		rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/field.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
	   // map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "经典表单设计器";
		rm.ClassMethodName = this.toString() + ".DoDesignerFool";
		rm.Icon = "../../WF/Img/FileType/xlsx.gif";
		rm.Visable = true;
		rm.Target = "_blank";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "手机端表单";
		rm.Icon = "../../WF/Img/Btn/DTS.gif";
		rm.ClassMethodName = this.toString() + ".MobileFrmDesigner";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "JS编程"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoInitScript";
		rm.Icon = "../../WF/Img/Script.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "表单body属性"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoBodyAttr";
		rm.Icon = "../../WF/Img/Script.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "导出XML表单模版"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoExp";
		rm.Icon = "../../WF/Img/Export.png";
		rm.Visable = true;
		rm.RefAttrLinkLabel = "导出到xml";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);


		//带有参数的方法.

		rm = new RefMethod();
		rm.Title = "重命名字段";
	  //  rm.Warning = "您确定要处理吗？";
		rm.getHisAttrs().AddTBString("FieldOld", null, "旧字段英文名", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("FieldNew", null, "新字段英文名", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("FieldNewName", null, "新字段中文名", true, false, 0, 100, 100);
		rm.ClassMethodName = this.toString() + ".DoChangeFieldName";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "重命表单ID";
		//  rm.GroupName = "高级设置";
		rm.getHisAttrs().AddTBString("NewFrmID1", null, "新表单ID名称", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("NewFrmID2", null, "确认表单ID名称", true, false, 0, 100, 100);
		rm.ClassMethodName = this.toString() + ".DoChangeFrmID";
		rm.Icon = "../../WF/Img/ReName.png";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "检查表单";
		rm.ClassMethodName = this.toString() + ".DoCheckFrm";
		rm.Icon = "../../WF/Img/check.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

			///#endregion 方法 - 基本功能.



			///#region 方法 - 开发接口.
		map.AddGroupMethod("开发接口");
		rm = new RefMethod();
		rm.Title = "调用查询API"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoSearch";
		rm.Icon = "../../WF/Img/Table.gif";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
	  //  rm.GroupName = "开发接口";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "调用分析API"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoGroup";
		rm.Icon = "../../WF/Img/Table.gif";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
	  //  rm.GroupName = "开发接口";
		map.AddRefMethod(rm);

			///#endregion 方法 - 开发接口.


		//rm = new RefMethod();
		//rm.Title = "Word表单属性"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoWordFrm";
		//rm.Icon = ../../Img/Btn/Word.gif";
		//rm.Visable = true;
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		//rm.GroupName = "开发接口";
		//map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "Excel表单属性"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoExcelFrm";
		//rm.Icon = ../../Img/Btn/Excel.gif";
		//rm.Visable = true;
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		//rm.GroupName = "开发接口";
		//map.AddRefMethod(rm);


		//rm = new RefMethod();
		//rm.Title = "数据源管理"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoDBSrc";
		//rm.Icon = "/WF/Img/DB.png";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Visable = true;
		//rm.RefAttrLinkLabel = "数据源管理";
		//rm.Target = "_blank";
		//map.AddRefMethod(rm);


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 基本方法.
	/** 
	 经典表单设计器
	 
	 @return 
	*/
	public final String DoDesignerFool() {
		return "../../Admin/FoolFormDesigner/Designer.htm?FK_MapData=" + this.getNo() + "&IsFirst=1&MyPK=" + this.getNo() + "&IsEditMapData=True";
	}

		///#endregion


		///#region 方法.
	/** 
	 重命名
	 
	 @param frmID1
	 @param frmID2
	 @return 
	*/
	public final String DoChangeFrmID(String frmID1, String frmID2) throws Exception {
		MapData md = new MapData();
		md.setNo(frmID1);
		if (md.getIsExits())
		{
			return "表单ID【" + frmID1 + "】已经存在";
		}

		if (!Objects.equals(frmID1, frmID2))
		{
			return "两次输入的ID不一致.";
		}


		String frmIDOld = this.getNo();

		String sqls = "";
		sqls += "@UPDATE Sys_MapData SET No='" + frmID1 + "' WHERE No='" + frmIDOld + "'";
	   // sqls += "UPDATE Sys_FrmLine SET FK_MapData='" + frmID1 + "' WHERE FK_MapData='" + frmIDOld + "'";
	   // sqls += "UPDATE Sys_FrmLab SET FK_MapData='" + frmID1 + "' WHERE FK_MapData='" + frmIDOld + "'";
		sqls += "UPDATE Sys_FrmBtn SET FK_MapData='" + frmID1 + "' WHERE FK_MapData='" + frmIDOld + "'";
		sqls += "UPDATE Sys_MapAttr SET FK_MapData='" + frmID1 + "' WHERE FK_MapData='" + frmIDOld + "'";
		sqls += "UPDATE Sys_MapExt SET FK_MapData='" + frmID1 + "' WHERE FK_MapData='" + frmIDOld + "'";
		sqls += "UPDATE Sys_FrmImg SET FK_MapData='" + frmID1 + "' WHERE FK_MapData='" + frmIDOld + "'";
		sqls += "UPDATE Sys_FrmImgAth SET FK_MapData='" + frmID1 + "' WHERE FK_MapData='" + frmIDOld + "'";
		sqls += "UPDATE Sys_FrmRB SET FK_MapData='" + frmID1 + "' WHERE FK_MapData='" + frmIDOld + "'";
		sqls += "UPDATE Sys_MapDtl SET FK_MapData='" + frmID1 + "' WHERE FK_MapData='" + frmIDOld + "'";
		sqls += "UPDATE Sys_MapFrame SET FK_MapData='" + frmID1 + "' WHERE FK_MapData='" + frmIDOld + "'";
		sqls += "UPDATE Sys_FrmEle SET FK_MapData='" + frmID1 + "' WHERE FK_MapData='" + frmIDOld + "'";
		sqls += "UPDATE Sys_FrmEvent SET FK_MapData='" + frmID1 + "' WHERE FK_MapData='" + frmIDOld + "'";
		DBAccess.RunSQLs(sqls);

		return "重命名成功，你需要关闭窗口重新刷新。";
	}
	/** 
	 替换名称
	 
	 @param fieldOld 旧名称
	 @param newField 新字段
	 @param newFieldName 新字段名称(可以为空)
	 @return 
	*/
	public final String DoChangeFieldName(String fieldOld, String newField, String newFieldName) throws Exception {
		MapAttr attrOld = new MapAttr();
		attrOld.setKeyOfEn(fieldOld);
		attrOld.setFrmID(this.getNo());
		attrOld.setMyPK(attrOld.getFrmID() + "_" + attrOld.getKeyOfEn());
		if (attrOld.RetrieveFromDBSources() == 0)
		{
			return "@旧字段输入错误[" + attrOld.getKeyOfEn() + "].";
		}

		//检查是否存在该字段？
		MapAttr attrNew = new MapAttr();
		attrNew.setKeyOfEn(newField);
		attrNew.setFrmID(this.getNo());
		attrNew.setMyPK(attrNew.getFrmID() + "_" + attrNew.getKeyOfEn());
		if (attrNew.RetrieveFromDBSources() == 1)
		{
			return "@该字段[" + attrNew.getKeyOfEn() + "]已经存在.";
		}

		//删除旧数据.
		attrOld.Delete();

		//copy这个数据,增加上它.
		attrNew.Copy(attrOld);
		attrNew.setKeyOfEn(newField);
		attrNew.setFrmID(this.getNo());

		if (!Objects.equals(newFieldName, ""))
		{
			attrNew.setName(newFieldName);
		}

		attrNew.Insert();

		//更新处理他的相关业务逻辑.
		MapExts exts = new MapExts(this.getNo());
		for (MapExt item : exts.ToJavaList())
		{
			item.setMyPK(item.getMyPK().replace("_" + fieldOld, "_" + newField));

			if (Objects.equals(item.getAttrOfOper(), fieldOld))
			{
				item.setAttrOfOper(newField);
			}

			if (Objects.equals(item.getAttrsOfActive(), fieldOld))
			{
				item.setAttrsOfActive(newField);
			}

			item.setTag(item.getTag().replace(fieldOld, newField));
			item.setTag1(item.getTag1().replace(fieldOld, newField));
			item.setTag2(item.getTag2().replace(fieldOld, newField));
			item.setTag3(item.getTag3().replace(fieldOld, newField));

			item.setAtPara(item.getAtPara().replace(fieldOld, newField));
			item.setDoc(item.getDoc().replace(fieldOld, newField));
			item.Save();
		}
		return "执行成功";
	}

	/** 
	 检查表单
	 
	 @return 
	*/
	public final String DoCheckFrm() {
		return "../../Admin/AttrNode/CheckFrm.htm?FK_MapData=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
	}


	/** 
	 批量设置正则表达式规则.
	 
	 @return 
	*/
	public final String DoRegularExpressionBatch() {
		return "../../Admin/FoolFormDesigner/MapExt/RegularExpressionBatch.htm?FK_Flow=&FK_MapData=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
	}
	/** 
	 排序字段顺序
	 
	 @return 
	*/
	public final String MobileFrmDesigner() {
		return "../../Admin/MobileFrmDesigner/Default.htm?FK_Flow=&FK_MapData=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
	}
	  /** 
	 设计表单
	 
	 @return 
	  */
	public final String DoDFrom() {
		return "../../Admin/FoolFormDesigner/CCForm/Frm.htm?FK_MapData=" + this.getNo() + "&UserNo=" + bp.web.WebUser.getNo() + "&Token=" + bp.web.WebUser.getToken() + "&AppCenterDBType=" + DBAccess.getAppCenterDBType() + "&CustomerNo=" + bp.difference.SystemConfig.getCustomerNo();
	}
	/** 
	 设计经典表单
	 
	 @return 
	*/
	public final String DoDFromCol4() {
		return "../../Admin/FoolFormDesigner/Designer.htm?FK_MapData=" + this.getNo() + "&IsFirst=1&UserNo=" + bp.web.WebUser.getNo() + "&Token=" + bp.web.WebUser.getToken() + "&AppCenterDBType=" + DBAccess.getAppCenterDBType() + "&CustomerNo=" + bp.difference.SystemConfig.getCustomerNo();
	}
	/** 
	 查询
	 
	 @return 
	*/
	public final String DoSearch() {
		return "../../Comm/Search.htm?s=34&FK_MapData=" + this.getNo() + "&EnsName=" + this.getNo();
	}
	/** 
	 调用分析API
	 
	 @return 
	*/
	public final String DoGroup() {
		return "../../Comm/Group.htm?s=34&FK_MapData=" + this.getNo() + "&EnsName=" + this.getNo();
	}
	/** 
	 数据源管理
	 
	 @return 
	*/
	public final String DoDBSrc() {
		return "../../Comm/Search.htm?s=34&FK_MapData=" + this.getNo() + "&EnsName=BP.Sys.SFDBSrcs";
	}

	public final String DoPageLoadFull() {
		return "../../Admin/FoolFormDesigner/MapExt/PageLoadFull.htm?s=34&FK_MapData=" + this.getNo() + "&ExtType=PageLoadFull&RefNo=";
	}
	public final String DoInitScript() {
		return "../../Admin/FoolFormDesigner/MapExt/InitScript.htm?s=34&FK_MapData=" + this.getNo() + "&ExtType=PageLoadFull&RefNo=";
	}
	/** 
	 表单属性.
	 
	 @return 
	*/
	public final String DoBodyAttr() {
		return "../../Admin/FoolFormDesigner/MapExt/BodyAttr.htm?s=34&FK_MapData=" + this.getNo() + "&ExtType=BodyAttr&RefNo=";
	}
	/** 
	 表单事件
	 
	 @return 
	*/
	public final String DoEvent() {
		return "../../Admin/CCFormDesigner/Action.htm?FK_MapData=" + this.getNo() + "&T=sd&FK_Node=0";
	}

	/** 
	 导出表单
	 
	 @return 
	*/
	public final String DoExp() {
		return "../../Admin/FoolFormDesigner/ImpExp/Exp.htm?FK_MapData=" + this.getNo();
	}

		///#endregion 方法.
}
