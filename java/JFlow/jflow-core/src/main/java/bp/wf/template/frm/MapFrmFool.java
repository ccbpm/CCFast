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
 经典表单属性
*/
public class MapFrmFool extends EntityNoName
{

		///#region 属性
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

		if (Objects.equals(this.getNo().substring(0, 2), "ND") && this.getNo().contains("Dtl") == false)
		{
			return true;
		}

		return false;
	}
	/**
	 物理存储表
	*/
	public final String getPTable()  {
		return this.GetValStrByKey(MapDataAttr.PTable);
	}
	public final void setPTable(String value){
		this.SetValByKey(MapDataAttr.PTable, value);
	}
	/**
	 节点ID.
	*/
	public final int getNodeID()  {
		if (this.getNo().indexOf("ND") != 0)
		{
			return 0;
		}
		return Integer.parseInt(this.getNo().replace("ND", ""));
	}

	/**
	 表格显示的列
	*/
	public final int getTableCol() {
		return 4;
	}
	public final void setTableCol(int value){
		this.SetValByKey(MapDataAttr.TableCol, value);
	}

	public final String getFormTreeNo()  {
		return this.GetValStringByKey(MapDataAttr.FK_FormTree);
	}
	public final void setFormTreeNo(String value){
		this.SetValByKey(MapDataAttr.FK_FormTree, value);
	}

	public final FrmType getHisFrmType() {
		return FrmType.forValue(this.GetValIntByKey(MapDataAttr.FrmType));
	}
	public final void setHisFrmType(FrmType value){
		this.SetValByKey(MapDataAttr.FrmType, value.getValue());
	}


		///#endregion


		///#region 权限控制.
	@Override
	public UAC getHisUAC() {
		UAC uac = new UAC();
		if (bp.web.WebUser.getIsAdmin() == true)
		{
			uac.OpenForAppAdmin(); //2020.6.22 zsy 修改.
			if (this.getNo().startsWith("ND") == true)
			{
				uac.IsDelete = false;
			}
			uac.IsInsert = false;
		}
		else
		{
			throw new RuntimeException("err@非法用户,只有管理员才可以操作.");
		}
		return uac;
	}

		///#endregion 权限控制.


		///#region 构造方法
	/**
	 经典表单属性
	*/
	public MapFrmFool()
	{
	}
	/**
	 经典表单属性

	 @param no 表单ID
	*/
	public MapFrmFool(String no) throws Exception
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

		Map map = new Map("Sys_MapData", "经典表单属性");


			///#region 基本属性.
		map.AddGroupAttr("基本属性", "");
		map.AddTBStringPK(MapDataAttr.No, null, "表单编号", true, true, 1, 190, 20);
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			map.AddTBString(MapDataAttr.PTable, null, "存储表", false, false, 0, 100, 20);
		}
		else
		{
			map.AddTBString(MapDataAttr.PTable, null, "存储表", true, false, 0, 100, 20);
			String msg = "提示:";
			msg += "\t\n1. 该表单把数据存储到那个表里.";
			msg += "\t\n2. 该表必须有一个int64未的OID列作为主键..";
			msg += "\t\n3. 如果指定了一个不存在的表,系统就会自动创建上.";
			map.SetHelperAlert(MapDataAttr.PTable, msg);
		}
		map.AddTBString(MapDataAttr.Name, null, "名称", true, false, 0, 500, 20, true);
		map.AddTBInt(MapDataAttr.TableCol, 0, "显示列数", false, false);
		map.AddTBInt(MapDataAttr.FrmW, 900, "表单宽度", true, false);
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
		}
		else
		{
			map.AddTBString(MapDataAttr.DBSrc, null, "数据源", false, false, 0, 500, 20);
			map.AddDDLEntities(MapDataAttr.FK_FormTree, "01", "表单类别", new SysFormTrees(), true);
		}

		//表单解析 0 普通 1 页签展示
		map.AddDDLSysEnum(MapDataAttr.FrmShowType, 0, "展示方式", true, true, "表单展示方式", "@0=普通方式@1=页签方式");

		//表单的运行类型.
		map.AddDDLSysEnum(MapDataAttr.FrmType, FrmType.FoolForm.getValue(), "表单类型", true, true, MapDataAttr.FrmType);

		map.AddTBString(MapDataAttr.UrlExt, null, "自定义URL", true, false, 0, 300, 20, true);
		map.AddTBString(MapDataAttr.Icon, "icon-doc", "图标", true, false, 0, 100, 100, true);

		map.AddBoolean("IsEnableJs", false, "是否启用自定义js函数？", true, true, true);

			///#endregion 基本属性.


			///#region 设计者信息.
		map.AddGroupAttr("设计者信息", "");
		map.AddTBString(MapDataAttr.Designer, null, "设计者", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerContact, null, "联系方式", true, false, 0, 500, 20);
		map.AddTBString(MapDataAttr.DesignerUnit, null, "单位", true, false, 0, 500, 20, true);
		map.AddTBString(MapDataAttr.GUID, null, "GUID", true, true, 0, 128, 20, false);
		map.AddTBString(MapDataAttr.Ver, null, "版本号", true, true, 0, 30, 20);
		map.AddTBString(MapDataAttr.Note, null, "备注", true, false, 0, 400, 100, true);
		//增加参数字段.
		map.AddTBAtParas(4000);
		map.AddTBInt(MapDataAttr.Idx, 100, "顺序号", false, false);

			///#endregion 设计者信息.


			///#region 基本功能.
		map.AddGroupMethod("基本功能");

		RefMethod rm = new RefMethod();

		rm = new RefMethod();
		rm.Title = "装载填充"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoPageLoadFull";
		// rm.Icon = "../../WF/Img/FullData.png";
		rm.Icon = "icon-reload";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		// rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "表单事件"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoEvent";
		rm.Icon = "../../WF/Img/Event.png";
		rm.Icon = "icon-energy";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "批量修改字段"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoBatchEditAttr";
		rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/field.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-calculator";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "手机端表单";
		rm.Icon = "../../WF/Admin/CCFormDesigner/Img/telephone.png";
		rm.ClassMethodName = this.toString() + ".MobileFrmDesigner";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-screen-smartphone";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "隐藏字段";
		rm.Icon = "../../WF/Admin/CCFormDesigner/Img/telephone.png";
		rm.Icon = "icon-list";
		rm.ClassMethodName = this.toString() + ".FrmHiddenField";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "表单body属性"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoBodyAttr";
		rm.Icon = "../../WF/Img/Script.png";
		rm.Icon = "icon-social-spotify";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "导出模版"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoExp";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "../../WF/Img/Export.png";
		rm.Visable = true;
		rm.RefAttrLinkLabel = "导出到xml";
		rm.Target = "_blank";
		rm.Icon = "icon-social-spotify";
		map.AddRefMethod(rm);

		//带有参数的方法.
		rm = new RefMethod();
		rm.Title = "重命名字段";
		rm.getHisAttrs().AddTBString("FieldOld", null, "旧字段英文名", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("FieldNew", null, "新字段英文名", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("FieldNewName", null, "新字段中文名", true, false, 0, 100, 100);
		rm.ClassMethodName = this.toString() + ".DoChangeFieldName";
		rm.Icon = "../../WF/Img/ReName.png";
		rm.Icon = "icon-refresh";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "表单检查"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoCheckFixFrmForUpdateVer";
		rm.Visable = true;
		rm.RefAttrLinkLabel = "表单检查";
		rm.Icon = "../../WF/Img/Check.png";
		rm.Target = "_blank";
		rm.Icon = "icon-eye";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "Tab顺序键"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoTabIdx";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-list";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "模板打印";
		rm.ClassMethodName = this.toString() + ".DoBill";
		rm.Icon = "../../WF/Img/FileType/doc.gif";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-printer";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "模板打印2019";
		rm.ClassMethodName = this.toString() + ".DoBill2019";
		rm.Icon = "../../WF/Img/FileType/doc.gif";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-printer";
		//  map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "参考面板";
		rm.ClassMethodName = this.toString() + ".DoRefPanel";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-grid";
		map.AddRefMethod(rm);

			///#endregion 方法 - 基本功能.


			///#region 高级功能.
		map.AddGroupMethod("高级功能");
		rm = new RefMethod();
		rm.Title = "版本管理"; // "设计表单";
	   // rm.GroupName = "高级功能";
		rm.ClassMethodName = this.toString() + ".DoMapDataVer";
		//  rm.Icon = "../../WF/Img/Ver.png";
		rm.Icon = "icon-social-dropbox";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "改变表单类型";
	   // rm.GroupName = "高级功能";
		rm.ClassMethodName = this.toString() + ".DoChangeFrmType()";
		rm.getHisAttrs().AddDDLSysEnum("FrmType", 0, "修改表单类型", true, true);
		rm.Icon = "icon-refresh";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "经典表单设计";
	   // rm.GroupName = "高级功能";
		rm.ClassMethodName = this.toString() + ".DoDesignerFool";
		//rm.Icon = "../../WF/Img/FileType/xlsx.gif";
		rm.Icon = "icon-note";
		rm.Visable = true;
		rm.Target = "_blank";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "JS编程"; // "设计表单";
	 //   rm.GroupName = "高级功能";
		rm.ClassMethodName = this.toString() + ".DoInitScript";
		rm.Icon = "../../WF/Img/Script.png";
		rm.Icon = "icon-social-dropbox";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);


		//平铺模式.
		if (bp.wf.Glo.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			map.getAttrsOfOneVSM().AddGroupPanelModel(new FrmOrgs(), new bp.wf.port.admin2group.Orgs(), FrmOrgAttr.FrmID, FrmOrgAttr.OrgNo, "适用组织", null, "Name", "No");
		}

		rm = new RefMethod();
		rm.Title = "特别控件特别用户权限";
		rm.Icon = "../../WF/Admin/CCFormDesigner/Img/SpecUserSpecFields.png";
		rm.ClassMethodName = this.toString() + ".DoSpecFieldsSpecUsers()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-note";
		map.AddRefMethod(rm);

			///#endregion


			///#region 开发接口.
		map.AddGroupMethod("开发接口");
		rm = new RefMethod();
		rm.Title = "调用查询API"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoSearch";
		//rm.Icon = "../../WF/Img/Table.gif";
		rm.Icon = "icon-magnifier";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
	 //   rm.GroupName = "开发接口";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "调用分析API"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoGroup";
		rm.Icon = "icon-chart";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
	 //   rm.GroupName = "开发接口";
		map.AddRefMethod(rm);

			///#endregion 方法 - 开发接口.


			///#region 实验中的功能
		map.AddGroupMethod("实验中的功能");
		//rm = new RefMethod();
		//rm.Title = "批量设置验证规则";
		//rm.GroupName = "实验中的功能";
		////rm.Icon = "../../WF/Img/RegularExpression.png";
		//rm.ClassMethodName = this.ToString() + ".DoRegularExpressionBatch";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Icon = "icon-settings";
		//map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "一键设置表单元素只读";
		rm.Warning = "您确定要设置吗？所有的元素，包括字段、从表、附件以及其它组件都将会被设置为只读的.";
	  //  rm.GroupName = "实验中的功能";
		//rm.Icon = "../../WF/Img/RegularExpression.png";
		rm.ClassMethodName = this.toString() + ".DoOneKeySetReadonly";
		rm.refMethodType = RefMethodType.Func;
		rm.Icon = "icon-settings";
		map.AddRefMethod(rm);


			///#endregion 实验中的功能


		this.set_enMap(map);
		return this.get_enMap();
	}
	/**
	 删除后清缓存
	*/
	@Override
	protected void afterDelete() throws Exception
	{
		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getNo());
		super.afterDelete();
	}

		///#endregion


		///#region 高级设置.
	/**
	 一键设置为只读.

	 @return
	*/
	public final String DoOneKeySetReadonly() throws Exception {
		CCFormAPI.OneKeySetFrmEleReadonly(this.getNo());
		return "设置成功.";
	}
	/**
	 改变表单类型 @李国文 ，需要搬到jflow.并测试.

	 @param val 要改变的类型
	 @return
	*/
	public final String DoChangeFrmType(int val) throws Exception {
		MapData md = new MapData(this.getNo());
		String str = "原来的是:" + md.getHisFrmTypeText() + "类型，";
		md.setHisFrmTypeInt(val);
		str += "现在修改为：" + md.getHisFrmTypeText() + "类型";
		md.Update();

		return str;
	}

		///#endregion 高级设置.

	@Override
	protected boolean beforeUpdate() throws Exception
	{
		//注册事件表单实体.
		//BP.Sys.Base.FormEventBase feb = BP.Sys.Base.Glo.GetFormEventBaseByEnName(this.getNo());
		//if (feb == null)
		//    this.FromEventEntity = "";
		//else
		//    this.FromEventEntity = feb.ToString();

		if (this.getNodeID() != 0)
		{
			this.setFormTreeNo("");
		}

		return super.beforeUpdate();
	}
	@Override
	protected void afterUpdate() throws Exception
	{
		//修改关联明细表
		MapDtl dtl = new MapDtl();
		dtl.setNo(this.getNo());
		if (dtl.RetrieveFromDBSources() == 1)
		{
			dtl.setName(this.getName());
			dtl.setPTable(this.getPTable());
			dtl.DirectUpdate();

			MapData map = new MapData(this.getNo());
			//避免显示在表单库中
			// map.FK_FrmSort = "";
			map.setFormTreeNo("");
			map.DirectUpdate();
		}

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getNo());

		super.afterUpdate();
	}


		///#region 节点表单方法.
	/**
	 版本管理

	 @return
	*/
	public final String DoMapDataVer() {
		return "../../Admin/FoolFormDesigner/MapDataVer.htm?FK_MapData=" + this.getNo() + "&FrmID=" + this.getNo();
	}
	/**
	 顺序

	 @return
	*/
	public final String DoTabIdx() {
		return "../../Admin/FoolFormDesigner/TabIdx.htm?FK_MapData=" + this.getNo();
	}
	/**
	 单据打印

	 @return
	*/
	public final String DoBill() {
		return "../../Admin/FoolFormDesigner/PrintTemplate/Default.htm?FK_MapData=" + this.getNo() + "&FrmID=" + this.getNo() + "&NodeID=" + this.getNodeID() + "&FK_Node=" + this.getNodeID();

	}
	/**
	 隐藏字段.

	 @return
	*/
	public final String FrmHiddenField() {
		return "../../Admin/CCFormDesigner/DialogCtr/FrmHiddenField.htm?FK_MapData=" + this.getNo() + "&NodeID=" + this.getNodeID() + "&FK_Node=" + this.getNodeID();
	}
	/**
	 单据打印

	 @return
	*/
	public final String DoBill2019() {
		return "../../Admin/AttrNode/Bill2019.htm?FK_MapData=" + this.getNo() + "&FrmID=" + this.getNo() + "&NodeID=" + this.getNodeID() + "&FK_Node=" + this.getNodeID();
	}

	/**
	 经典表单设计器

	 @return
	*/
	public final String DoDesignerFool() {
		return "../../Admin/FoolFormDesigner/Designer.htm?FK_MapData=" + this.getNo() + "&MyPK=" + this.getNo() + "&IsFirst=1&IsEditMapData=True";
	}

	/**
	 节点表单组件

	 @return
	*/
	public final String DoNodeFrmCompent() throws Exception {
		if (this.getNo().contains("ND") == true)
		{
			return "../../Comm/EnOnly.htm?EnName=BP.WF.Template.FrmNodeComponent&PK=" + this.getNo().replace("ND", "") + "&t=" + DataType.getCurrentDateTime();
		}
		else
		{
			return "../../Admin/FoolFormDesigner/Do.htm&DoType=FWCShowError";
		}
	}
	/**
	 执行旧版本的兼容性检查.
	*/
	public final String DoCheckFixFrmForUpdateVer() throws Exception {
		// 更新状态.
		DBAccess.RunSQL("UPDATE Sys_GroupField SET CtrlType='' WHERE CtrlType IS NULL");
		DBAccess.RunSQL("UPDATE Sys_GroupField SET CtrlID='' WHERE CtrlID IS NULL");
		DBAccess.RunSQL("UPDATE Sys_GroupField SET CtrlID='' WHERE CtrlID IS NULL");


		//更新GroupFieldID
		String sql = "";
		switch (bp.difference.SystemConfig.getAppCenterDBType())
		{
			case MySQL:
			case GBASE8A:
			case GBASE8CByMySQL:
				sql = "UPDATE Sys_MapDtl, Sys_GroupField B SET Sys_MapDtl.GroupField=B.OID WHERE Sys_MapDtl.No=B.CtrlID AND Sys_MapDtl.GroupField=''";
				break;
			case Oracle:
			case DM:
			case GBASE8CByOracle:
				sql = "UPDATE Sys_MapDtl E SET GroupField=(SELECT U.OID FROM Sys_GroupField U WHERE E.No=U.CtrlID) WHERE EXISTS (SELECT 1 FROM Sys_GroupField U WHERE E.No=U.CtrlID AND E.GroupField='')";
				break;
			case MSSQL:
			default:
				sql = "UPDATE Sys_MapDtl SET GroupField=Sys_GroupField.OID FROM Sys_GroupField WHERE Sys_MapDtl.No=Sys_GroupField.CtrlID AND Sys_MapDtl.GroupField=''";
				break;
		}
		DBAccess.RunSQL(sql);


		//删除重影数据.
		DBAccess.RunSQL("DELETE FROM Sys_GroupField WHERE CtrlType='FWC' and CTRLID is null");

		//一直遇到遇到自动变长的问题, 强制其修复过来.
		DBAccess.RunSQL("UPDATE Sys_Mapattr SET colspan=3 WHERE UIHeight<=38 AND colspan=4");

		String str = "";

		//处理失去分组的字段.
		String sqlOfOID = " CAST(OID as VARCHAR(50)) ";
		if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL)
		{
			sqlOfOID = " CAST(OID as CHAR) ";
		}

		sql = "SELECT MyPK FROM Sys_MapAttr WHERE FK_MapData='" + this.getNo() + "' AND GroupID NOT IN (SELECT OID FROM Sys_GroupField WHERE FrmID='" + this.getNo() + "' AND ( CtrlType='' OR CtrlType IS NULL)  )  OR GroupID IS NULL ";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() != 0)
		{
			GroupField gf = null;
			GroupFields gfs = new GroupFields(this.getNo());
			for (GroupField mygf : gfs.ToJavaList())
			{
				if (Objects.equals(mygf.getCtrlID(), ""))
				{
					gf = mygf;
				}
			}

			if (gf == null)
			{
				gf = new GroupField();
				gf.setLab( "基本信息");
				gf.setFrmID(this.getNo());
				gf.Insert();
			}

			//设置 GroupID
			for (DataRow dr : dt.Rows)
			{
				DBAccess.RunSQL("UPDATE Sys_MapAttr SET GroupID=" + gf.getOID() + " WHERE MyPK='" + dr.getValue(0).toString() + "'");
			}
		}

		//从表.
		MapDtls dtls = new MapDtls(this.getNo());
		for (MapDtl dtl : dtls.ToJavaList())
		{
			GroupField gf = new GroupField();
			int i = gf.Retrieve(GroupFieldAttr.CtrlID, dtl.getNo(), GroupFieldAttr.FrmID, this.getNo());
			if (i == 1)
			{
				continue;
			}

			//GroupField gf = new GroupField();
			//if (gf.IsExit(GroupFieldAttr.CtrlID, dtl.getNo()) == true && !DataType.IsNullOrEmpty(gf.getCtrlType()))
			//    continue;

			gf.setLab(dtl.getName());
			gf.setCtrlID(dtl.getNo());
			gf.setCtrlType("Dtl");
			gf.setFrmID(dtl.getFrmID());
			gf.Save();
			str += "@为从表" + dtl.getName() +" 增加了分组.";
		}

		// 框架.
		MapFrames frams = new MapFrames(this.getNo());
		for (MapFrame fram : frams.ToJavaList())
		{
			GroupField gf = new GroupField();
			int i = gf.Retrieve(GroupFieldAttr.CtrlID, fram.getMyPK(), GroupFieldAttr.FrmID, this.getNo());
			if (i == 1)
			{
				continue;
			}

			gf.setLab(fram.getName());
			gf.setCtrlID(fram.getMyPK());
			gf.setCtrlType("Frame");
			gf.setEnName(fram.getFrmID());
			gf.Insert();

			str += "@为框架 " + fram.getName() +" 增加了分组.";
		}

		// 附件.
		FrmAttachments aths = new FrmAttachments(this.getNo());
		for (FrmAttachment ath : aths.ToJavaList())
		{
			//单附件、不可见的附件，都不需要增加分组.
			if (ath.getItIsVisable() == false || ath.getUploadType() == AttachmentUploadType.Single)
			{
				continue;
			}


			GroupField gf = new GroupField();
			int i = gf.Retrieve(GroupFieldAttr.CtrlID, ath.getMyPK(), GroupFieldAttr.FrmID, this.getNo());
			if (i == 1)
			{
				continue;
			}

			gf.setLab(ath.getName());
			gf.setCtrlID(ath.getMyPK());
			gf.setCtrlType("Ath");
			gf.setFrmID(ath.getFrmID());
			gf.Insert();

			str += "@为附件 " + ath.getName() +" 增加了分组.";
		}

		if (this.getItIsNodeFrm() == true)
		{
			//提高执行效率.
			// FrmNodeComponent conn = new FrmNodeComponent(this.getNodeID());
			//  conn.InitGroupField();
			//conn.Update();
		}


		//删除重复的数据, 比如一个从表显示了多个分组里. 增加此部分.
		if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.DM ||bp.difference.SystemConfig.getAppCenterDBType() == DBType.Oracle || bp.difference.SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.KingBaseR6  || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			sql = "SELECT * FROM (SELECT FrmID,CtrlID,CtrlType, count(*) as Num FROM sys_groupfield WHERE CtrlID!='' GROUP BY FrmID,CtrlID,CtrlType ) WHERE Num > 1";
		}
		else
		{
			sql = "SELECT * FROM (SELECT FrmID,CtrlID,CtrlType, count(*) as Num FROM sys_groupfield WHERE CtrlID!='' GROUP BY FrmID,CtrlID,CtrlType ) AS A WHERE A.Num > 1";
		}

		dt = DBAccess.RunSQLReturnTable(sql);
		for (DataRow dr : dt.Rows)
		{
			String enName = dr.getValue(0).toString();
			String ctrlID = dr.getValue(1).toString();
			String ctrlType = dr.getValue(2).toString();

			GroupFields gfs = new GroupFields();
			gfs.Retrieve(GroupFieldAttr.FrmID, enName, GroupFieldAttr.CtrlID, ctrlID, GroupFieldAttr.CtrlType, ctrlType, null);

			if (gfs.size() <= 1)
			{
				continue;
			}
			for (GroupField gf : gfs.ToJavaList())
			{
				gf.Delete(); //删除其中的一个.
				break;
			}
		}

		if (Objects.equals(str, ""))
		{
			return "检查成功.";
		}

		return str + ", @@@ 检查成功。";
	}

		///#endregion


		///#region 通用方法.
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
		if (this.getNo().startsWith("ND") == false)
		{
			//修改对应的数据库字段名
			DBAccess.RenameTableField(this.getPTable(), fieldOld, newField);
		}
		else
		{
			String strs = this.getNo().replace("ND", "");
			strs = strs.substring(0, strs.length() - 2);
			String rptTable = "ND" + strs + "Rpt";
			MapDatas mds = new MapDatas();
			mds.Retrieve(MapDataAttr.No, rptTable, null);
			if (mds.isEmpty())
			{
				String sql = "UPDATE Sys_MapAttr SET KeyOfEn='" + newField + "',  MyPK='" + rptTable + "_" + newField + "' WHERE KeyOfEn='" + fieldOld + "' AND FK_MapData='" + rptTable + "'";
				DBAccess.RenameTableField(rptTable, fieldOld, newField);
			}

			for (MapData item : mds.ToJavaList())
			{
				String sql = "UPDATE Sys_MapAttr SET KeyOfEn='" + newField + "',  MyPK='" + item.getNo() + "_" + newField + "' WHERE KeyOfEn='" + fieldOld + "' AND FK_MapData='" + item.getNo() + "'";
				DBAccess.RunSQL(sql);
				DBAccess.RenameTableField(item.getPTable(), fieldOld, newField);
			}

		}
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

		//如果是开发者表单需要替换开发者表单的Html
		if (this.getHisFrmType() == FrmType.Develop)
		{
			String devHtml = DBAccess.GetBigTextFromDB("Sys_MapData", "No", this.getNo(), "HtmlTemplateFile");
			if (DataType.IsNullOrEmpty(devHtml) == true)
			{
				return "执行成功";
			}
			String prefix = "TB_";
			//外部数据源、外键、枚举下拉框
			if ((attrNew.getLGType() == FieldTypeS.Normal && attrNew.getMyDataType() == DataType.AppString && attrNew.getUIContralType() == UIContralType.DDL) || (attrNew.getLGType() == FieldTypeS.FK && attrNew.getMyDataType() == DataType.AppString) || (attrNew.getLGType() == FieldTypeS.Enum && attrNew.getUIContralType() == UIContralType.DDL))
			{
				devHtml = devHtml.replace("id=\"DDL_" + attrOld.getKeyOfEn() + "\"", "id=\"DDL_" + attrNew.getKeyOfEn() + "\"").replace("id=\"SS_" + attrOld.getKeyOfEn() + "\"", "id=\"SS_" + attrNew.getKeyOfEn() + "\"").replace("name=\"DDL_" + attrOld.getKeyOfEn() + "\"", "name=\"DDL_" + attrNew.getKeyOfEn() + "\"").replace("data-key=\"" + attrOld.getKeyOfEn() + "\"", "data-key=\"" + attrNew.getKeyOfEn() + "\"").replace(">" + attrOld.getKeyOfEn() + "</option>", ">" + attrNew.getKeyOfEn() + "</option>");

				//保存开发者表单数据
				Dev2Interface.SaveDevelopForm(devHtml, this.getNo());
				return "执行成功";
			}
			//枚举
			if (attrNew.getLGType() == FieldTypeS.Enum)
			{
				if (DataType.IsNullOrEmpty(attrNew.getUIBindKey()) == true)
				{
					throw new RuntimeException("err@" + attrNew.getName() +"枚举字段绑定的枚举为空,请检查该字段信息是否发生变更");
				}
				//根据绑定的枚举获取枚举值
				SysEnums enums = new SysEnums(attrNew.getUIBindKey());
				if (attrNew.getUIContralType() == UIContralType.CheckBok)
				{
					prefix = "CB_";
					devHtml = devHtml.replace("id=\"SC_" + attrOld.getKeyOfEn() + "\"", "id=\"SC_" + attrNew.getKeyOfEn() + "\"");
				}
				if (attrNew.getUIContralType() == UIContralType.RadioBtn)
				{
					prefix = "RB_";
					devHtml = devHtml.replace("id=\"SR_" + attrOld.getKeyOfEn() + "\"", "id=\"SR_" + attrNew.getKeyOfEn() + "\"");
				}
				for (SysEnum item : enums.ToJavaList())
				{
					devHtml = devHtml.replace("id=\"" + prefix + attrOld.getKeyOfEn() + "_" + item.getIntKey() + "\"", "id=\"" + prefix + attrNew.getKeyOfEn() + "_" + item.getIntKey() + "\"").replace("name=\"" + prefix + attrOld.getKeyOfEn() + "\"", "name=\"" + prefix + attrNew.getKeyOfEn() + "\"").replace("data-key=\"" + attrOld.getKeyOfEn() + "\"", "data-key=\"" + attrNew.getKeyOfEn() + "\"");
				}
				//保存开发者表单数据
				Dev2Interface.SaveDevelopForm(devHtml, this.getNo());
				return "执行成功";
			}
			//普通字段
			if (attrNew.getLGType() == FieldTypeS.Normal)
			{
				prefix = "TB_";
				if (attrNew.getMyDataType() == DataType.AppBoolean)
				{
					prefix = "CB_";
				}
				devHtml = devHtml.replace("id=\"" + prefix + attrOld.getKeyOfEn() + "\"", "id=\"" + prefix + attrNew.getKeyOfEn() + "\"").replace("name=\"" + prefix + attrOld.getKeyOfEn() + "\"", "name=\"" + prefix + attrNew.getKeyOfEn() + "\"").replace("data-key=\"" + attrOld.getKeyOfEn() + "\"", "data-key=\"" + attrNew.getKeyOfEn() + "\"").replace("data-name=\"" + attrOld.getName() +"\"", "data-name=\"" + attrNew.getName() +"\"");
			}
			//保存开发者表单数据
			Dev2Interface.SaveDevelopForm(devHtml, this.getNo());
			return "执行成功";
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
		return "../../Admin/FoolFormDesigner/FieldTypeListBatch.htm?FK_MapData=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
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
		String url = "../../Admin/FoolFormDesigner/Designer.htm?FK_MapData=" + this.getNo() + "&UserNo=" + bp.web.WebUser.getNo() + "&Token=" + bp.web.WebUser.getToken() + "&IsFirst=1&AppCenterDBType=" + DBAccess.getAppCenterDBType() + "&CustomerNo=" + bp.difference.SystemConfig.getCustomerNo();
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
	 参考面板

	 @return
	*/
	public final String DoRefPanel() {
		return "../../Comm/RefFunc/EnOnly.htm?EnName=BP.WF.Template.Frm.MapFrmReferencePanel&PKVal=" + this.getNo();
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

	public final String DoSpecFieldsSpecUsers() {
		return "../../Admin/FoolFormDesigner/SepcFiledsSepcUsers.htm?FrmID=" + this.getNo() + "&t=" + DataType.getCurrentDateTime();
	}
	/**
	 经典表单属性.

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
