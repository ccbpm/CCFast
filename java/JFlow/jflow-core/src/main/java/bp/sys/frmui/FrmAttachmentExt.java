package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import bp.tools.FtpUtil;

import java.util.*;

/**
 附件
*/
public class FrmAttachmentExt extends EntityMyPK
{
	/**
	 访问权限.
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsView = true;
		uac.IsInsert = false;
		if (bp.web.WebUser.getNo().equals("admin") || bp.web.WebUser.getIsAdmin()  == true)
		{
			uac.IsUpdate = true;
			uac.IsDelete = true;
			return uac;
		}
		return uac;
	}


		///#region 参数属性.
	/**
	 是否可见？
	*/
	public final boolean getItIsVisable() {
		return this.GetParaBoolen(FrmAttachmentAttr.IsVisable, true);
	}
	public final void setItIsVisable(boolean value)  {
		this.SetPara(FrmAttachmentAttr.IsVisable, value);
	}
	/**
	 附件类型
	*/
	public final int getFileType() {
		return this.GetParaInt(FrmAttachmentAttr.FileType);
	}
	public final void setFileType(int value)  {
		this.SetPara(FrmAttachmentAttr.FileType, value);
	}
	/**
	 使用上传附件的 - 控件类型
	 0=批量.
	 1=单个。
	*/
	public final int getUploadCtrl() {
		return this.GetParaInt(FrmAttachmentAttr.UploadCtrl);
	}
	public final void setUploadCtrl(int value)  {
		this.SetPara(FrmAttachmentAttr.UploadCtrl, value);
	}
	/**
	 上传校验
	 0=不校验.
	 1=不能为空.
	 2=每个类别下不能为空.
	*/
	public final UploadFileNumCheck getUploadFileNumCheck() {
		return UploadFileNumCheck.forValue(this.GetValIntByKey(FrmAttachmentAttr.UploadFileNumCheck));
	}
	public final void setUploadFileNumCheck(UploadFileNumCheck value)  {
		this.SetPara(FrmAttachmentAttr.UploadFileNumCheck, value.getValue());
	}


		///#endregion 参数属性.


		///#region 属性
	/**
	 节点编号
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(FrmAttachmentAttr.FK_Node);
	}
	public final void setNodeID(int value){
		this.SetValByKey(FrmAttachmentAttr.FK_Node, value);
	}
	/**
	 上传类型（单个的，多个，指定的）
	*/
	public final AttachmentUploadType getUploadType() {
		return AttachmentUploadType.forValue(this.GetValIntByKey(FrmAttachmentAttr.UploadType));
	}
	public final void setUploadType(AttachmentUploadType value){
		this.SetValByKey(FrmAttachmentAttr.UploadType, value.getValue());
	}
	/**
	 类型名称
	*/
	public final String getUploadTypeT() throws Exception {
		if (this.getUploadType() == AttachmentUploadType.Multi)
		{
			return "多附件";
		}
		if (this.getUploadType() == AttachmentUploadType.Single)
		{
			return "单附件";
		}
		if (this.getUploadType() == AttachmentUploadType.Specifically)
		{
			return "指定的";
		}
		return "XXXXX";
	}
	/**
	 是否可以上传
	*/
	public final boolean getItIsUpload()  {
		return this.GetValBooleanByKey(FrmAttachmentAttr.IsUpload);
	}
	public final void setItIsUpload(boolean value){
		this.SetValByKey(FrmAttachmentAttr.IsUpload, value);
	}
	/**
	 是否可以下载
	*/
	public final boolean getItIsDownload()  {
		return this.GetValBooleanByKey(FrmAttachmentAttr.IsDownload);
	}
	public final void setItIsDownload(boolean value){
		this.SetValByKey(FrmAttachmentAttr.IsDownload, value);
	}
	/**
	 附件删除方式
	*/
	public final AthDeleteWay getHisDeleteWay() {
		return AthDeleteWay.forValue(this.GetValIntByKey(FrmAttachmentAttr.DeleteWay));
	}
	public final void setHisDeleteWay(AthDeleteWay value){
		this.SetValByKey(FrmAttachmentAttr.DeleteWay, value.getValue());
	}


	/**
	 自动控制大小
	*/
	public final boolean getItIsAutoSize()  {
		return this.GetValBooleanByKey(FrmAttachmentAttr.IsAutoSize);
	}
	public final void setItIsAutoSize(boolean value){
		this.SetValByKey(FrmAttachmentAttr.IsAutoSize, value);
	}
	/**
	 IsShowTitle
	*/
	public final boolean getItIsShowTitle()  {
		return this.GetValBooleanByKey(FrmAttachmentAttr.IsShowTitle);
	}
	public final void setItIsShowTitle(boolean value){
		this.SetValByKey(FrmAttachmentAttr.IsShowTitle, value);
	}
	/**
	 是否是节点表单.
	*/
	public final boolean getItIsNodeSheet() throws Exception {
		if (this.getFrmID().startsWith("ND") == true)
		{
			return true;
		}
		return false;
	}
	/**
	 备注列
	*/
	public final boolean getItIsNote()  {
		return this.GetValBooleanByKey(FrmAttachmentAttr.IsNote);
	}
	public final void setItIsNote(boolean value){
		this.SetValByKey(FrmAttachmentAttr.IsNote, value);
	}
	/**
	 附件名称
	*/
	public final String getName() throws Exception {
		String str = this.GetValStringByKey(FrmAttachmentAttr.Name);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = "未命名";
		}
		return str;
	}
	public final void setName(String value){
		this.SetValByKey(FrmAttachmentAttr.Name, value);
	}
	/**
	 类别
	*/
	public final String getSort()  {
		return this.GetValStringByKey(FrmAttachmentAttr.Sort);
	}
	public final void setSort(String value){
		this.SetValByKey(FrmAttachmentAttr.Sort, value);
	}
	/**
	 要求的格式
	*/
	public final String getExts()  {
		return this.GetValStringByKey(FrmAttachmentAttr.Exts);
	}
	public final void setExts(String value){
		this.SetValByKey(FrmAttachmentAttr.Exts, value);
	}

	/**
	 附件标识
	*/
	public final String getNoOfObj()  {
		return this.GetValStringByKey(FrmAttachmentAttr.NoOfObj);
	}


	/**
	 H
	*/
	public final float getH()  {
		return this.GetValFloatByKey(FrmAttachmentAttr.H);
	}
	public final void setH(float value){
		this.SetValByKey(FrmAttachmentAttr.H, value);
	}
	/**
	 数据控制方式
	*/
	public final AthCtrlWay getHisCtrlWay() {
		return AthCtrlWay.forValue(this.GetValIntByKey(FrmAttachmentAttr.CtrlWay));
	}
	public final void setHisCtrlWay(AthCtrlWay value){
		this.SetValByKey(FrmAttachmentAttr.CtrlWay, value.getValue());
	}
	/**
	 是否是合流汇总多附件？
	*/
	public final boolean getItIsHeLiuHuiZong()  {
		return this.GetValBooleanByKey(FrmAttachmentAttr.IsHeLiuHuiZong);
	}
	public final void setItIsHeLiuHuiZong(boolean value){
		this.SetValByKey(FrmAttachmentAttr.IsHeLiuHuiZong, value);
	}
	/**
	 该附件是否汇总到合流节点上去？
	*/
	public final boolean getItIsToHeLiuHZ()  {
		return this.GetValBooleanByKey(FrmAttachmentAttr.IsToHeLiuHZ);
	}
	public final void setItIsToHeLiuHZ(boolean value){
		this.SetValByKey(FrmAttachmentAttr.IsToHeLiuHZ, value);
	}
	/**
	 文件展现方式
	*/
	public final FileShowWay getFileShowWay() {
		return FileShowWay.forValue(this.GetParaInt(FrmAttachmentAttr.FileShowWay));
	}
	public final void setFileShowWay(FileShowWay value)  {
		this.SetPara(FrmAttachmentAttr.FileShowWay, value.getValue());
	}
	/**
	 上传方式（对于父子流程有效）
	*/
	public final AthUploadWay getAthUploadWay() {
		return AthUploadWay.forValue(this.GetValIntByKey(FrmAttachmentAttr.AthUploadWay));
	}
	public final void setAthUploadWay(AthUploadWay value){
		this.SetValByKey(FrmAttachmentAttr.AthUploadWay, value.getValue());
	}
	/**
	 FK_MapData
	*/
	public final String getFrmID()  {
		return this.GetValStrByKey(FrmAttachmentAttr.FK_MapData);
	}
	public final void setFrmID(String value){
		this.SetValByKey(FrmAttachmentAttr.FK_MapData, value);
	}

	/**
	 是否要转换成html
	*/
	public final boolean getItIsTurn2Html()  {
		return this.GetValBooleanByKey(FrmAttachmentAttr.IsTurn2Html);
	}

		///#endregion


		///#region 快捷键
	/**
	 是否启用快捷键
	*/
	public final boolean getFastKeyIsEnable() {
		return this.GetParaBoolen(FrmAttachmentAttr.FastKeyIsEnable);
	}
	public final void setFastKeyIsEnable(boolean value)  {
		this.SetPara(FrmAttachmentAttr.FastKeyIsEnable, value);
	}
	/**
	 启用规则
	*/
	public final String getFastKeyGenerRole() {
		return this.GetParaString(FrmAttachmentAttr.FastKeyGenerRole);
	}
	public final void setFastKeyGenerRole(String value)  {
		this.SetPara(FrmAttachmentAttr.FastKeyGenerRole, value);
	}

		///#endregion 快捷键


		///#region 构造方法
	/**
	 附件
	*/
	public FrmAttachmentExt()
	{
	}
	/**
	 附件

	 @param mypk 主键
	*/
	public FrmAttachmentExt(String mypk) throws Exception {
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

		Map map = new Map("Sys_FrmAttachment", "附件");
		map.IndexField = MapAttrAttr.FK_MapData;
		map.AddGroupAttr("基础信息");
		map.AddMyPK();


			///#region 基本属性。
		map.AddTBString(FrmAttachmentAttr.FK_MapData, null, "表单ID", true, true, 1, 100, 20);
		map.AddTBString(FrmAttachmentAttr.NoOfObj, null, "附件标识", true, true, 0, 50, 20);
		map.AddTBInt(FrmAttachmentAttr.FK_Node, 0, "节点控制(对sln有效)", false, false);

		//for渔业厅增加.
		map.AddDDLSysEnum(FrmAttachmentAttr.AthRunModel, 0, "运行模式", true, true, FrmAttachmentAttr.AthRunModel, "@0=流水模式@1=固定模式@2=自定义页面");

		map.AddTBString(FrmAttachmentAttr.Name, null, "附件名称", true, false, 0, 50, 20, true);

		map.AddTBString(FrmAttachmentAttr.Exts, null, "文件格式", true, false, 0, 50, 20, true, null);
		map.SetHelperAlert(FrmAttachmentAttr.Exts, "上传要求,设置模式为: *.*, *.doc, *.docx, *.png,多个中间用逗号分开.\t\n表示仅仅允许上传指定的后缀的文件.");

		map.AddTBInt(FrmAttachmentAttr.NumOfUpload, 0, "最小上传数量", true, false);
		map.SetHelperAlert("NumOfUpload", "如果为0则标识必须上传. \t\n用户上传的附件数量低于指定的数量就不让保存.");

		map.AddTBInt(FrmAttachmentAttr.TopNumOfUpload, 99, "最大上传数量", true, false);
		map.AddTBInt(FrmAttachmentAttr.FileMaxSize, 10240, "附件最大限制(KB)", true, false);
		map.AddDDLSysEnum(FrmAttachmentAttr.UploadFileNumCheck, 0, "上传校验方式", true, true, FrmAttachmentAttr.UploadFileNumCheck, "@0=不用校验@1=不能为空@2=每个类别下不能为空");

		map.AddDDLSysEnum(FrmAttachmentAttr.AthSaveWay, 0, "保存方式", true, true, FrmAttachmentAttr.AthSaveWay, "@0=保存到web服务器@1=保存到数据库@2=ftp服务器@3=保存到对象存储OSS");


		map.AddBoolean(FrmAttachmentAttr.IsIdx, false, "是否排序?", true, true);
		map.AddTBString("OpenDesc", null, "上传说明", false, false, 0, 500, 20, true, null);



		map.AddTBString(FrmAttachmentAttr.Sort, null, "类别", true, false, 0, 500, 20, true, null);
		map.SetHelperAlert(FrmAttachmentAttr.Sort, "设置格式:生产类,文件类,其他，也可以设置一个SQL，比如select Name FROM Port_Dept  \t\n目前已经支持了扩展列,可以使用扩展列定义更多的字段，该设置将要被取消.");

		map.AddBoolean(FrmAttachmentAttr.IsTurn2Html, false, "是否转换成html(方便手机浏览)", true, true, true);

		//附件是否显示
		map.AddBoolean(FrmAttachmentAttr.IsVisable, true, "是否显示附件分组", true, true, true);

		map.AddDDLSysEnum(FrmAttachmentAttr.FileType, 0, "附件类型", true, true, FrmAttachmentAttr.FileType, "@0=普通附件@1=图片文件");

		map.AddDDLSysEnum(FrmAttachmentAttr.PicUploadType, 0, "图片附件上传方式", true, true, FrmAttachmentAttr.PicUploadType, "@0=拍照上传或者相册上传@1=只能拍照上传");
		map.SetHelperAlert(FrmAttachmentAttr.PicUploadType, "该功能只使用于移动端图片文件上传的方式.");

		map.AddBoolean(FrmAttachmentAttr.IsEnableTemplate, true, "是否启用模板下载？", true, true, true);
		map.AddTBFloat(FrmAttachmentAttr.H, 150, "高度", true, false);
		// @wwh.
		map.AddMyFileS("附件模板");


			///#endregion 基本属性。


			///#region 权限控制。
		//hzm新增列
		// map.AddTBInt(FrmAttachmentAttr.DeleteWay, 0, "附件删除规则(0=不能删除1=删除所有2=只能删除自己上传的", false, false);
		map.AddGroupAttr("权限控制");
		map.AddDDLSysEnum(FrmAttachmentAttr.DeleteWay, 1, "附件删除规则", true, true, FrmAttachmentAttr.DeleteWay, "@0=不能删除@1=删除所有@2=只能删除自己上传的");

		map.AddBoolean(FrmAttachmentAttr.IsUpload, true, "是否可以上传", true, true);
		map.AddBoolean(FrmAttachmentAttr.IsDownload, true, "是否可以下载", true, true);

		map.AddBoolean(FrmAttachmentAttr.IsAutoSize, true, "自动控制大小", true, true);
		map.AddBoolean(FrmAttachmentAttr.IsNote, true, "是否增加备注", true, true);
		map.AddBoolean(FrmAttachmentAttr.IsExpCol, true, "是否启用扩展列", true, true);

		map.AddBoolean(FrmAttachmentAttr.IsShowTitle, true, "是否显示标题列", true, true);

		map.AddDDLSysEnum(FrmAttachmentAttr.UploadType, 0, "上传类型", true, false, FrmAttachmentAttr.CtrlWay, "@0=单个@1=多个@2=指定");
		map.SetHelperAlert(FrmAttachmentAttr.UploadType, "单附件：请使用字段单附件组件。");


		map.AddDDLSysEnum(FrmAttachmentAttr.AthUploadWay, 0, "控制上传控制方式", true, true, FrmAttachmentAttr.AthUploadWay, "@0=继承模式@1=协作模式");

		map.AddDDLSysEnum(FrmAttachmentAttr.CtrlWay, 4, "控制呈现控制方式", true, true, "Ath" + FrmAttachmentAttr.CtrlWay, "@0=PK-主键@1=FID-干流程ID@2=PWorkID-父流程ID@3=仅能查看自己上传的附件@4=WorkID-按照WorkID计算(对流程节点表单有效)@5=P2WorkID@6=P3WorkID");


		//map.AddDDLSysEnum(FrmAttachmentAttr.DataRef, 0, "数据引用", true, true, FrmAttachmentAttr.DataRef,
		//    "@0=当前组件ID@1=指定的组件ID");

			///#endregion 权限控制。


			///#region 流程相关
		map.AddGroupAttr("流程相关");
		//map.AddDDLSysEnum(FrmAttachmentAttr.DtlOpenType, 0, "附件删除规则", true, true, FrmAttachmentAttr.DeleteWay,
		//    "@0=不能删除@1=删除所有@2=只能删除自己上传的");
		map.AddBoolean(FrmAttachmentAttr.IsToHeLiuHZ, true, "该附件是否要汇总到合流节点上去？(对子线程节点有效)", true, true, true);
		map.AddBoolean(FrmAttachmentAttr.IsHeLiuHuiZong, true, "是否是合流节点的汇总附件组件？(对合流节点有效)", true, true, true);
		map.AddTBString(FrmAttachmentAttr.DataRefNoOfObj, "AttachM1", "对应附件标识", true, false, 0, 150, 20);
		map.SetHelperAlert("DataRefNoOfObj", "对WorkID权限模式有效,用于查询贯穿整个流程的附件标识,与从表的标识一样.");

		map.AddDDLSysEnum(FrmAttachmentAttr.ReadRole, 0, "阅读规则", true, true, FrmAttachmentAttr.ReadRole, "@0=不控制@1=未阅读阻止发送@2=未阅读做记录");

			///#endregion 流程相关


			///#region 其他属性。
		//参数属性.
		map.AddTBAtParas(3000);

			///#endregion 其他属性。


			///#region 基本功能.
		map.AddGroupMethod("基本功能");
		RefMethod rm = new RefMethod();
		//  rm.Icon = "/WF/Admin/CCFormDesigner/Img/Menu/CC.png";
		//rm.ClassMethodName = this.ToString() + ".DoAdv";
		// rm.refMethodType = RefMethodType.RightFrameOpen;
		//  map.AddRefMethod(rm);
		rm = new RefMethod();
		rm.Title = "测试FTP服务器";
		rm.ClassMethodName = this.toString() + ".DoTestFTPHost";
		rm.refMethodType = RefMethodType.Func;
		rm.Icon = "icon-fire";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "重命名标记";
		rm.ClassMethodName = this.toString() + ".ResetAthName";
		rm.getHisAttrs().AddTBString("F", null, "命名后的标记", true, false, 0, 100, 50);
		rm.refMethodType = RefMethodType.Func;
		rm.Icon = "icon-note";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Icon = "icon-credit-card";
		rm.Title = "分组属性"; // "设计表单";
		rm.ClassMethodName = this.toString() + ".DoGroup";
		// rm.Icon = "../Img/AttachmentM.png";
		rm.Visable = true;
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Target = "_blank";
		map.AddRefMethod(rm);

		String msg = "说明：";
		msg += "\t\n 1. 每个附件都有一个标记比如，Ath1,Ath2, FJ. ";
		msg += "\t\n 2. 这个标记在一个表单中不能重复，这个标记也叫附件的小名。";
		msg += "\t\n 3. 在父子流程，或者多表单流程中，这个标记可以用于继承附件的展示。";
		msg += "\t\n 4. 比如：一个父流程的附件组件的标记为Ath1, 一个子流程的表单的附件表单要看到这个附件信息，就需要把两个小名保持一致。";
		rm.Warning = msg;
		map.AddRefMethod(rm);


			///#endregion 基本功能.


			///#region 高级设置.
		map.AddGroupMethod("实验中功能");
		rm = new RefMethod();

		rm.Title = "类别设置";
		rm.ClassMethodName = this.toString() + ".DoSettingSort";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		map.AddRefMethod(rm);

		rm = new RefMethod();

		rm.Title = "设置扩展列";
		rm.ClassMethodName = this.toString() + ".DtlOfAth";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		// map.AddRefMethod(rm);

			///#endregion 高级设置.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	/**
	 重命名表单标记.

	 @param fname
	 @return
	*/
	public final String ResetAthName(String fname) throws Exception {
		//检查一下是否有重名的？
		FrmAttachments ens = new FrmAttachments();
		ens.Retrieve(FrmAttachmentAttr.FK_MapData, this.getFrmID());
		for (FrmAttachment item : ens.ToJavaList())
		{
			if (item.getNoOfObj().equals(fname) == true)
			{
				return "err@修改失败，该表单中已经存在该标记了.";
			}
		}

		//修改模版.
		String myPKNew = this.getFrmID() + "_" + fname;
		String sql = "UPDATE Sys_FrmAttachment SET MyPK='" + myPKNew + "', NoOfObj='" + fname + "' WHERE MyPK='" + this.getMyPK() + "' ";
		DBAccess.RunSQL(sql);

		//修改分组信息，不然就丢失了.
		sql = "UPDATE Sys_GroupField SET CtrlID='" + myPKNew + "'  WHERE CtrlID='" + this.getMyPK() + "' ";
		DBAccess.RunSQL(sql);

		//修改：数据库.
		sql = "UPDATE Sys_FrmAttachmentDB SET NoOfObj='" + fname + "',FK_FrmAttachment='" + myPKNew + "'  WHERE FK_FrmAttachment='" + this.getMyPK() + "'";
		DBAccess.RunSQL(sql);

		return "执行成功: 您需要关闭表单设计器，然后重新进入。";
	}

	public final String DtlOfAth() throws Exception {
		String url = "../../Admin/FoolFormDesigner/MapDefDtlFreeFrm.htm?FK_MapDtl=" + this.getMyPK() + "&For=" + this.getMyPK();
		return url;
	}


	/**
	 编辑分组属性

	 @return
	*/
	public final String DoGroup() throws Exception {
		GroupField gf = new GroupField();
		int i = gf.Retrieve(GroupFieldAttr.FrmID, this.getFrmID(), GroupFieldAttr.CtrlID, this.getMyPK());
		if (i == 0)
		{
			gf.setLab(this.getName());
			gf.setFrmID(this.getFrmID());
			gf.setCtrlType("Dtl");
			gf.setCtrlID(this.getMyPK());
			gf.setIdx(10);
			gf.Insert();
		}
		String url = "../../Comm/EnOnly.htm?EnName=BP.Sys.GroupField&PKVal=" + gf.getOID() + "&Token=" + bp.web.WebUser.getToken() + "&AppCenterDBType=" + DBAccess.getAppCenterDBType() + "&CustomerNo=" + bp.difference.SystemConfig.getCustomerNo();
		return url;
	}

	/**
	 测试连接

	 @return
	*/
	public final String DoTestFTPHost() throws Exception {
		FtpUtil ftpUtil;
		try {
			ftpUtil = bp.sys.base.Glo.getFtpUtil();
		} catch (Exception e) {

			e.printStackTrace();
			return "err@" + e.getMessage();
		}
		return ftpUtil.openConnection();
	}
	/**
	 固定模式类别设置

	 @return
	*/
	public final String DoSettingSort() {
		return "../../Admin/FoolFormDesigner/AttachmentSortSetting.htm?FK_MapData=" + this.getFrmID() + "&MyPK=" + this.getMyPK() + "&Ath=" + this.getNoOfObj();
	}
	/**
	 执行高级设置.

	 @return
	*/
	public final String DoAdv() {
		return "/WF/Admin/FoolFormDesigner/Attachment.aspx?FK_MapData=" + this.getFrmID() + "&MyPK=" + this.getMyPK() + "&Ath=" + this.getNoOfObj();
	}

	public boolean ItIsUse = false;
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		if (this.getNodeID() == 0)
		{
			//适应设计器新的规则 by dgq
			if (!DataType.IsNullOrEmpty(this.getNoOfObj()) && this.getNoOfObj().contains(this.getFrmID()))
			{
				this.setMyPK(this.getNoOfObj());
			}
			else
			{
				this.setMyPK(this.getFrmID() + "_" + this.getNoOfObj());
			}
		}

		if (this.getNodeID() != 0)
		{
			/*工作流程模式.*/
			if (this.getHisCtrlWay() == AthCtrlWay.PK)
			{
				this.setHisCtrlWay(AthCtrlWay.WorkID);
			}
			this.setMyPK(this.getFrmID() + "_" + this.getNoOfObj() + "_" + this.getNodeID());
		}

		if (this.getHisCtrlWay() != AthCtrlWay.WorkID)
		{
			this.setAthUploadWay(AthUploadWay.Interwork);
		}


		//如果是pworkid. 就不让其删除或者上传.
		if (this.getHisCtrlWay() == AthCtrlWay.PWorkID || this.getHisCtrlWay() == AthCtrlWay.PWorkID || this.getHisCtrlWay() == AthCtrlWay.P2WorkID || this.getHisCtrlWay() == AthCtrlWay.P3WorkID || this.getHisCtrlWay() == AthCtrlWay.RootFlowWorkID)
		{
			this.SetValByKey(FrmAttachmentAttr.DeleteWay, 0);
			this.SetValByKey(FrmAttachmentAttr.IsUpload, 0);
		}



			///#region 处理分组.
		//更新相关的分组信息.
		if (this.getItIsVisable() == true && this.getNodeID() == 0)
		{
			GroupField gf = new GroupField();
			int i = gf.Retrieve(GroupFieldAttr.FrmID, this.getFrmID(), GroupFieldAttr.CtrlID, this.getMyPK());
			if (i == 0)
			{
				gf.setLab(this.getName());
				gf.setFrmID(this.getFrmID());
				gf.setCtrlType("Ath");
				//gf.CtrlID = this.MyPK;
				gf.Insert();
			}
			else
			{
				gf.setLab(this.getName());
				gf.setFrmID(this.getFrmID());
				gf.setCtrlType("Ath");
				//gf.CtrlID = this.MyPK;
				gf.Update();
			}
		}

		//如果不显示.
		if (this.getItIsVisable() == false)
		{
			GroupField gf = new GroupField();
			gf.Delete(GroupFieldAttr.FrmID, this.getFrmID(), GroupFieldAttr.CtrlID, this.getMyPK());
		}

			///#endregion 处理分组.


		return super.beforeUpdateInsertAction();
	}
	@Override
	protected boolean beforeInsert() throws Exception {
		//在属性实体集合插入前，clear父实体的缓存.
		bp.sys.base.Glo.ClearMapDataAutoNum(this.getFrmID());


		if (this.getNodeID() == 0)
		{
			this.setMyPK(this.getFrmID() + "_" + this.getNoOfObj());
		}
		else
		{
			this.setMyPK(this.getFrmID() + "_" + this.getNoOfObj() + "_" + this.getNodeID());
		}

		return super.beforeInsert();
	}
	/**
	 插入之后
	*/
	@Override
	protected void afterInsert() throws Exception
	{
		GroupField gf = new GroupField();
		if (this.getNodeID() == 0 && gf.IsExit(GroupFieldAttr.CtrlID, this.getMyPK()) == false)
		{
			gf.setFrmID(this.getFrmID());
			gf.setCtrlID(this.getMyPK());
			gf.setCtrlType("Ath");
			gf.setLab(this.getName());
			gf.setIdx(0);
			gf.Insert(); //插入.

		}
		super.afterInsert();
	}

	@Override
	protected void afterInsertUpdateAction() throws Exception
	{
		FrmAttachment ath = new FrmAttachment();
		ath.setMyPK(this.getMyPK());
		ath.RetrieveFromDBSources();
		ath.setItIsToHeLiuHZ(this.getItIsToHeLiuHZ());
		ath.setItIsHeLiuHuiZong(this.getItIsHeLiuHuiZong());

		//强制设置,保存到ftp服务器上.
		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			ath.setAthSaveWay(AthSaveWay.FTPServer);
		}

		ath.Update();


		//判断是否是字段附件
		MapAttr mapAttr = new MapAttr();
		mapAttr.setMyPK(this.getMyPK());
		if (mapAttr.RetrieveFromDBSources() != 0)
		{
			mapAttr.setUIHeight(this.getH());
			mapAttr.setName(this.getName());
			mapAttr.Update();
		}

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());

		super.afterInsertUpdateAction();
	}

	/**
	 删除之后.
	*/
	@Override
	protected void afterDelete() throws Exception
	{
		GroupFields gfs = new GroupFields();
		//   gfs.RetrieveByLike(GroupFieldAttr.CtrlID, this.MyPK + "%");
		//  gfs.Delete();
		gfs.Delete(GroupFieldAttr.CtrlID, this.getMyPK());

		//把相关的字段也要删除.
		MapAttrString attr = new MapAttrString();
		attr.setMyPK(this.getMyPK());
		if (attr.RetrieveFromDBSources() != 0)
		{
			attr.Delete();
		}

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());

		super.afterDelete();
	}
}
