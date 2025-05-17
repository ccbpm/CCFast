package bp.wf.template.frm;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.CCFormAPI;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 自由表单属性
*/
public class MapFrmNode extends EntityNoName
{

		///#region 文件模版属性.


		///#endregion 文件模版属性.


		///#region 属性
	/** 
	 表单事件实体
	*/
	public final String getFromEventEntity()  {
		return this.GetValStrByKey(MapDataAttr.FormEventEntity);
	}
	public final void setFromEventEntity(String value){
		this.SetValByKey(MapDataAttr.FormEventEntity, value);
	}
	/** 
	 是否是节点表单?
	*/
	public final boolean getItIsNodeFrm() throws Exception {
		if (this.getNo().contains("ND") == false)
		{
			return false;
		}

		if (this.getNo().contains("Rpt") == true)
		{
			return false;
		}

		if (Objects.equals(this.getNo().substring(0, 2), "ND"))
		{
			return true;
		}

		return false;
	}
	/** 
	 节点ID.
	*/
	public final int getNodeID() {
		return Integer.parseInt(this.getNo().replace("ND", ""));
	}


	/** 
	 表格显示的列
	*/
	public final int getTableCol() {
		return 4;
	}
	public final void setTableCol(int value){
		this.SetValByKey(MapFrmNodeAttr.TableCol, value);
	}

		///#endregion


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


		///#region 构造方法
	/** 
	 自由表单属性
	*/
	public MapFrmNode()
	{
	}
	/** 
	 自由表单属性
	 
	 @param no 表单ID
	*/
	public MapFrmNode(String no) throws Exception
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

		Map map = new Map("Sys_MapData", "自由表单属性");



			///#region 基本属性.
		map.AddTBStringPK(MapFrmNodeAttr.No, null, "表单编号", true, true, 1, 190, 20);
		map.AddTBString(MapFrmNodeAttr.PTable, null, "存储表", true, false, 0, 100, 20);
		map.AddTBString(MapFrmNodeAttr.Name, null, "表单名称", true, false, 0, 200, 20,true);
		map.AddTBString(MapDataAttr.FormEventEntity, null, "事件实体", true, true, 0, 100, 20, true);

		//数据源.
		map.AddDDLEntities(MapFrmNodeAttr.DBSrc, "local", "数据源", new SFDBSrcs(), true);

		//宽度高度.
		map.AddTBInt(MapFrmNodeAttr.FrmW, 900, "宽度", true, false);

		//表单的运行类型.
		map.AddDDLSysEnum(MapFrmNodeAttr.FrmType, FrmType.FoolForm.getValue(), "表单类型", true, false, MapFrmNodeAttr.FrmType);

			///#endregion 基本属性.


			///#region 设计者信息.
		map.AddTBString(MapFrmNodeAttr.Designer, null, "设计者", true, false, 0, 500, 20);
		map.AddTBString(MapFrmNodeAttr.DesignerContact, null, "联系方式", true, false, 0, 500, 20);
		map.AddTBString(MapFrmNodeAttr.DesignerUnit, null, "单位", true, false, 0, 500, 20, false);
		map.AddTBString(MapFrmNodeAttr.GUID, null, "GUID", true, true, 0, 128, 20, false);
		map.AddTBString(MapFrmNodeAttr.Ver, null, "版本号", true, true, 0, 30, 20);
		map.AddTBStringDoc(MapFrmNodeAttr.Note, null, "备注", true, false, true, 10);

		//增加参数字段.
		map.AddTBAtParas(4000);
		map.AddTBInt(MapFrmNodeAttr.Idx, 100, "顺序号", false, false);

			///#endregion 设计者信息.

		map.AddMyFile("表单模版", null, null);

		//查询条件.
		map.AddSearchAttr(MapFrmNodeAttr.DBSrc, 130);


			///#region 方法 - 基本功能.
		RefMethod rm = new RefMethod();

		rm = new RefMethod();
		rm.Title = "启动经典表单设计器";
		rm.ClassMethodName = this.toString() + ".DoDesignerFool";
		rm.Icon = "../../WF/Img/FileType/xlsx.gif";
		rm.Visable = true;
		rm.Target = "_blank";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "字段维护";
		//rm.ClassMethodName = this.ToString() + ".DoEditFiledsList";
		//rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/field.png";
		//rm.Visable = true;
		//rm.Target = "_blank";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//map.AddRefMethod(rm);


		//rm = new RefMethod();
		//rm.Title = "批量修改字段"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoBatchEditAttr";
		//rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/field.png";
		//rm.Visable = true;
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		//map.AddRefMethod(rm);

		rm = new RefMethod();
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

	   // rm = new RefMethod();
	   // rm.Title = "批量设置验证规则";
	   // rm.Icon = "../../WF/Img/RegularExpression.png";
	   // rm.ClassMethodName = this.ToString() + ".DoRegularExpressionBatch";
	   // rm.refMethodType = RefMethodType.RightFrameOpen;
	   //// map.AddRefMethod(rm);

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
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "Tab顺序键"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoTabIdx";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);
		rm = new RefMethod();
		rm.Title = "模板打印";
		rm.ClassMethodName = this.toString() + ".DoBill";
		rm.Icon = "../../WF/Img/FileType/doc.gif";
		rm.refMethodType = RefMethodType.RightFrameOpen;

		map.AddRefMethod(rm);



		//rm = new RefMethod();
		//rm.Title = "节点表单组件"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoNodeFrmCompent";
		//rm.Visable = true;
		//rm.RefAttrLinkLabel = "节点表单组件";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		//rm.Icon = ../../Img/Components.png";
		//map.AddRefMethod(rm);

			///#endregion 方法 - 基本功能.


			///#region 高级设置.

		//带有参数的方法.
		rm = new RefMethod();
		rm.Title = "重命名字段";
	 //   rm.GroupName = "高级设置";
		rm.getHisAttrs().AddTBString("FieldOld", null, "旧字段英文名", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("FieldNew", null, "新字段英文名", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("FieldNewName", null, "新字段中文名", true, false, 0, 100, 100);
		rm.ClassMethodName = this.toString() + ".DoChangeFieldName";
		rm.Icon = "../../WF/Img/ReName.png";
		rm.GroupName = "高级设置";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "重命表单ID";
	  //  rm.GroupName = "高级设置";
		rm.getHisAttrs().AddTBString("NewFrmID1", null, "新表单ID名称", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("NewFrmID2", null, "确认表单ID名称", true, false, 0, 100, 100);
		rm.ClassMethodName = this.toString() + ".DoChangeFrmID";
		rm.Icon = "../../WF/Img/ReName.png";
		rm.GroupName = "高级设置";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "复制表单";
		//  rm.GroupName = "高级设置";
		rm.getHisAttrs().AddTBString("FrmID", null, "要复制新表单ID", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("FrmName", null, "表单名称", true, false, 0, 100, 100);
		rm.getHisAttrs().AddDDLEntities("FrmTree", null, "复制到表单目录", new SysFormTrees(),true);

		rm.ClassMethodName = this.toString() + ".DoCopyFrm";
		rm.Icon = "../../WF/Img/Btn/Copy.GIF";
		rm.GroupName = "高级设置";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "手机端表单";
		rm.GroupName = "高级设置";
		rm.Icon = "../../WF/Admin/CCFormDesigner/Img/telephone.png";
		rm.ClassMethodName = this.toString() + ".MobileFrmDesigner";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		//map.AddRefMethod(rm);

			///#endregion 高级设置.


			///#region 方法 - 开发接口.
		rm = new RefMethod();
		rm.Title = "调用查询API"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoSearch";
		rm.Icon = "../../WF/Img/Table.gif";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
		rm.GroupName = "开发接口";
		//map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "调用分析API"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoGroup";
		rm.Icon = "../../WF/Img/Table.gif";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
		rm.GroupName = "开发接口";
		map.AddRefMethod(rm);

			///#endregion 方法 - 开发接口.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


	/** 
	 单据打印
	 
	 @return 
	*/
	public final String DoBill() {
		return "../../Admin/FoolFormDesigner/PrintTemplate/Default.htm?FK_MapData=" + this.getNo() + "&FrmID=" + this.getNo() + "&NodeID=" + this.getNodeID() + "&FK_Node=" + this.getNodeID();

	   // return "../../Admin/AttrNode/Bill.htm?FK_MapData=" + this.getNo() + "&NodeID=" + this.getNodeID() + "&FK_Node=" + this.getNodeID();
	}


	@Override
	protected boolean beforeUpdate() throws Exception
	{
		//注册事件表单实体.
		bp.sys.base.FormEventBase feb = bp.sys.base.Glo.GetFormEventBaseByEnName(this.getNo());
		if (feb == null)
		{
			this.setFromEventEntity("");
		}
		else
		{

			this.setFromEventEntity(feb.toString());
		}

		return super.beforeUpdate();
	}
	public final String DoTabIdx() {
		return "../../Admin/FoolFormDesigner/TabIdx.htm?FK_MapData=" + this.getNo();
	}

	/** 
	 复制表单
	 
	 @param frmID
	 @param frmName
	 @param fk_frmTree
	 @return 
	*/
	public final String DoCopyFrm(String frmID, String frmName, String fk_frmTree) throws Exception {
		return CCFormAPI.CopyFrm(this.getNo(), frmID, frmName, fk_frmTree);
	}


		///#region 节点表单方法.

	/** 
	 经典表单设计器
	 
	 @return 
	*/
	public final String DoDesignerFool() {
		return "../../Admin/FoolFormDesigner/Designer.htm?FK_MapData=" + this.getNo() + "&MyPK=" + this.getNo() + "&IsEditMapData=True&DoDesignerFool&IsFirst=1";
	}
	/** 
	 编辑excel模版.
	 
	 @return 
	*/
	public final String DoEditExcelTemplate() {
		return "../../Admin/CCFormDesigner/ExcelFrmDesigner/Designer.htm?FK_MapData=" + this.getNo();
	}
	/** 
	 表单字段.
	 
	 @return 
	*/
	public final String DoEditFiledsList() {
		return "../../Admin/FoolFormDesigner/BatchEdit.htm?FK_MapData=" + this.getNo();
	}

		///#endregion


		///#region 通用方法.
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
	  //  sqls += "UPDATE Sys_FrmLine SET FK_MapData='" + frmID1 + "' WHERE FK_MapData='" + frmIDOld + "'";
	  //  sqls += "UPDATE Sys_FrmLab SET FK_MapData='" + frmID1 + "' WHERE FK_MapData='" + frmIDOld + "'";
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
	 
	 @param fieldOldName 旧名称
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
	 批量设置正则表达式规则.
	 
	 @return 
	*/
	public final String DoRegularExpressionBatch() {
		return "../../Admin/FoolFormDesigner/MapExt/RegularExpressionBatch.htm?FK_Flow=&FK_MapData=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
	}
	/** 
	 批量修改字段
	 
	 @return 
	*/
	public final String DoBatchEditAttr() {
		return "../../Admin/FoolFormDesigner/BatchEdit.htm?FK_MapData=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
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
	public final String DoDFrom() throws Exception {
		String url = "../../Admin/FoolFormDesigner/CCForm/Frm.htm?FK_MapData=" + this.getNo() + "&UserNo=" + bp.web.WebUser.getNo() + "&Token=" + bp.web.WebUser.getToken() + "&AppCenterDBType=" + DBAccess.getAppCenterDBType() + "&CustomerNo=" + bp.difference.SystemConfig.getCustomerNo();
		return url;
	}
	/** 
	 设计经典表单
	 
	 @return 
	*/
	public final String DoDFromCol4() throws Exception {
		String url = "../../Admin/FoolFormDesigner/Designer.htm?FK_MapData=" + this.getNo() + "&UserNo=" + bp.web.WebUser.getNo() + "&Token=" + bp.web.WebUser.getToken() + "&AppCenterDBType=" + DBAccess.getAppCenterDBType() + "&IsFirst=1&CustomerNo=" + bp.difference.SystemConfig.getCustomerNo();
		return url;
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
	 自由表单属性.
	 
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
