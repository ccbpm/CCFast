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
 字段单附件
*/
public class FrmAttachmentSingle extends EntityMyPK
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
	 字段单附件类型
	*/
	public final int getFileType() {
		return this.GetParaInt(FrmAttachmentAttr.FileType);
	}
	public final void setFileType(int value)  {
		this.SetPara(FrmAttachmentAttr.FileType, value);
	}
	/** 
	 使用上传字段单附件的 - 控件类型
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


	public final int getAthSingleRole()  {
		return this.GetValIntByKey(FrmAttachmentAttr.AthSingleRole);
	}

	public final int getAthEditModel()  {
		return this.GetValIntByKey(FrmAttachmentAttr.AthEditModel);
	}

	public final String getSaveTo() {
		return bp.difference.SystemConfig.getPathOfDataUser() + "UploadFile/" + this.getFrmID() + "/";
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
			return "多字段单附件";
		}
		if (this.getUploadType() == AttachmentUploadType.Single)
		{
			return "单字段单附件";
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
	 字段单附件删除方式
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
	 字段单附件名称
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
	 字段单附件标识
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
	 是否是合流汇总多字段单附件？
	*/
	public final boolean getItIsHeLiuHuiZong()  {
		return this.GetValBooleanByKey(FrmAttachmentAttr.IsHeLiuHuiZong);
	}
	public final void setItIsHeLiuHuiZong(boolean value){
		this.SetValByKey(FrmAttachmentAttr.IsHeLiuHuiZong, value);
	}
	/** 
	 该字段单附件是否汇总到合流节点上去？
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
	 字段单附件
	*/
	public FrmAttachmentSingle()
	{
	}
	/** 
	 字段单附件
	 
	 @param mypk 主键
	*/
	public FrmAttachmentSingle(String mypk) throws Exception {
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

		Map map = new Map("Sys_FrmAttachment", "字段单附件");
		map.IndexField = MapAttrAttr.FK_MapData;

		map.AddMyPK();


			///#region 基本属性。
		map.AddTBString(FrmAttachmentAttr.FK_MapData, null, "表单ID", true, true, 1, 100, 20);
		map.AddTBString(FrmAttachmentAttr.NoOfObj, null, "附件标识", true, true, 0, 50, 20);
		map.AddTBInt(FrmAttachmentAttr.FK_Node, 0, "节点控制(对sln有效)", false, false);

		map.AddTBString(FrmAttachmentAttr.Name, null, "附件名称", true, false, 0, 50, 20, false);

		map.AddTBString(FrmAttachmentAttr.Exts, null, "文件格式", true, false, 0, 50, 20, false, null);
		map.SetHelperAlert(FrmAttachmentAttr.Exts, "上传要求,设置模式为: *.*, *.doc, *.docx, *.png,多个中间用逗号分开.\t\n表示仅仅允许上传指定的后缀的文件.");

		map.AddBoolean(FrmAttachmentAttr.NumOfUpload, false, "是否必填？", true, true);
		// map.SetHelperAlert("NumOfUpload", "如果为0则标识必须上传. \t\n用户上传的字段单附件数量低于指定的数量就不让保存.");

		map.AddTBInt(FrmAttachmentAttr.FileMaxSize, 10240, "附件最大限制(KB)", true, false);

		map.AddDDLSysEnum(FrmAttachmentAttr.AthSaveWay, 0, "保存方式", true, true, FrmAttachmentAttr.AthSaveWay, "@0=保存到web服务器@1=保存到数据库@2=ftp服务器@3=保存到对象存储OSS");


		map.AddDDLSysEnum(FrmAttachmentAttr.AthSingleRole, 0, "模板规则", true, true, FrmAttachmentAttr.AthSingleRole, "@0=不使用模板@1=使用上传模板@2=使用上传模板自动加载数据标签");
		map.SetHelperAlert(FrmAttachmentAttr.AthSingleRole, "单附件模板使用规则，如果启用，您需要上传wps/word模板。");

		map.AddDDLSysEnum(FrmAttachmentAttr.AthEditModel, 0, "在线编辑模式", true, true, FrmAttachmentAttr.AthEditModel, "@0=只读@1=可编辑全部区域@2=可编辑非数据标签区域");
		map.SetHelperAlert(FrmAttachmentAttr.AthEditModel, "用于控制附件的在线编辑模式");



			///#endregion 基本属性。


			///#region 权限控制。
		map.AddBoolean(FrmAttachmentAttr.DeleteWay, true, "是否可以删除？", true, true);

		//map.AddDDLSysEnum(FrmAttachmentAttr.DeleteWay, 1, "删除规则", true, true, FrmAttachmentAttr.DeleteWay,
		//    "@0=不能删除@1=删除所有@2=只能删除自己上传的");

		map.AddBoolean(FrmAttachmentAttr.IsUpload, true, "是否可以上传", true, true);
		map.AddBoolean(FrmAttachmentAttr.IsDownload, true, "是否可以下载", true, true);
		map.AddBoolean(FrmAttachmentAttr.IsNote, true, "是否增加备注", true, true);

		map.AddDDLSysEnum(FrmAttachmentAttr.AthUploadWay, 0, "控制上传控制方式", true, true, FrmAttachmentAttr.AthUploadWay, "@0=继承模式@1=协作模式");

		map.AddDDLSysEnum(FrmAttachmentAttr.CtrlWay, 4, "控制呈现控制方式", true, true, "Ath" + FrmAttachmentAttr.CtrlWay, "@0=PK-主键@1=FID-干流程ID@2=PWorkID-父流程ID@3=仅能查看自己上传的字段单附件@4=WorkID-按照WorkID计算(对流程节点表单有效)@5=P2WorkID@6=P3WorkID");

			///#endregion 权限控制。


			///#region 节点相关
		map.AddBoolean(FrmAttachmentAttr.IsToHeLiuHZ, true, "该字段单附件是否要汇总到合流节点上去？(对子线程节点有效)", true, true, true);
		map.AddBoolean(FrmAttachmentAttr.IsHeLiuHuiZong, true, "是否是合流节点的汇总字段单附件组件？(对合流节点有效)", true, true, true);
		map.AddTBString(FrmAttachmentAttr.DataRefNoOfObj, "AttachM1", "对应字段单附件标识", true, false, 0, 150, 20);
		map.SetHelperAlert("DataRefNoOfObj", "对WorkID权限模式有效,用于查询贯穿整个流程的字段单附件标识,与从表的标识一样.");
		map.AddDDLSysEnum(FrmAttachmentAttr.ReadRole, 0, "阅读规则", true, true, FrmAttachmentAttr.ReadRole, "@0=不控制@1=未阅读阻止发送@2=未阅读做记录");

			///#endregion 节点相关


			///#region 其他属性。
		//  map.AddBoolean(FrmAttachmentAttr.IsIdx, false, "是否排序?", true, true);
		map.AddBoolean(FrmAttachmentAttr.IsTurn2Html, false, "是否转换成html(方便手机浏览)", true, true, true);

		//参数属性.
		map.AddTBAtParas(3000);
		//隐藏字段.
		map.AddTBInt(FrmAttachmentAttr.UploadType, 0, "0单附件1多附件", false, false);
		map.AddTBInt(FrmAttachmentAttr.IsVisable, 0, "是否可见?", false, false);


			///#endregion 其他属性。


			///#region 基本配置.
		RefMethod rm = new RefMethod();

		rm = new RefMethod();
		rm.Title = "上传wps模板";
		rm.ClassMethodName = this.toString() + ".DoUploadTemplateWPS";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-fire";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "上传word模板";
		rm.ClassMethodName = this.toString() + ".DoUploadTemplateWord";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-fire";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "测试FTP服务器";
		rm.ClassMethodName = this.toString() + ".DoTestFTPHost";
		rm.refMethodType = RefMethodType.Func;
		rm.Icon = "icon-fire";
		map.AddRefMethod(rm);

			///#endregion 基本配置.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 基本方法.
	public final String DoUploadTemplateWPS() {
		return "../../Admin/FoolFormDesigner/Template/FrmAttachmentSingle/UploadAthTemplateWPS.htm?FrmID=" + this.getFrmID() + "&MyPK=" + this.getMyPK();
	}
	public final String DoUploadTemplateWord() {
		return "../../Admin/FoolFormDesigner/Template/FrmAttachmentSingle/UploadAthTemplateWord.htm?FrmID=" + this.getFrmID() + "&MyPK=" + this.getMyPK();
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

	public boolean ItIsUse = false;

		///#endregion 基本方法.


		///#region 重写的方法.

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

		GroupField gf = new GroupField();
		gf.Delete(GroupFieldAttr.FrmID, this.getFrmID(), GroupFieldAttr.CtrlID, this.getMyPK());

		return super.beforeUpdateInsertAction();
	}
	@Override
	protected boolean beforeInsert() throws Exception
	{
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

		//判断是否是字段字段单附件
		MapAttr mapAttr = new MapAttr();
		mapAttr.setMyPK(this.getMyPK());
		if (mapAttr.RetrieveFromDBSources() != 0 && mapAttr.getName().equals(this.getName()) == false)
		{
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
		//删除.
		MapAttr attr = new MapAttr();
		attr.setMyPK(this.getMyPK());
		attr.Delete();

		//调用frmEditAction, 完成其他的操作.
		CCFormAPI.AfterFrmEditAction(this.getFrmID());
		super.afterDelete();
	}

		///#endregion 重写的方法.

}
