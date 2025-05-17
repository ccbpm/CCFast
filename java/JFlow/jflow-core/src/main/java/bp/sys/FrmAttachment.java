package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;

import java.io.File;
import java.util.*;

/**
 附件
*/
public class FrmAttachment extends EntityMyPK
{

		///#region 参数属性.
	/**
	 是否可见？
	*/
	public final boolean getItIsVisable()  {
		return this.GetValBooleanByKey(FrmAttachmentAttr.IsVisable, true);
	}
	public final void setItIsVisable(boolean value){
		this.SetValByKey(FrmAttachmentAttr.IsVisable, value);
	}
	public final int getDeleteWay()  {
		return this.GetValIntByKey(FrmAttachmentAttr.DeleteWay, 0);
	}
	public final void setDeleteWay(int value){
		this.SetValByKey(FrmAttachmentAttr.DeleteWay, value);
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
	 最低上传数量
	*/
	public final int getNumOfUpload()  {
		return this.GetValIntByKey(FrmAttachmentAttr.NumOfUpload);
	}
	public final void setNumOfUpload(int value){
		this.SetValByKey(FrmAttachmentAttr.NumOfUpload, value);
	}
	/**
	 最大上传数量
	*/
	public final int getTopNumOfUpload()  {
		return this.GetValIntByKey(FrmAttachmentAttr.TopNumOfUpload);
	}
	public final void setTopNumOfUpload(int value){
		this.SetValByKey(FrmAttachmentAttr.TopNumOfUpload, value);
	}
	/**
	 附件最大限制
	*/
	public final int getFileMaxSize()  {
		return this.GetValIntByKey(FrmAttachmentAttr.FileMaxSize);
	}
	public final void setFileMaxSize(int value){
		this.SetValByKey(FrmAttachmentAttr.FileMaxSize, value);
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
	/**
	 保存方式
	 0 =文件方式保存。
	 1 = 保存到数据库.
	 2 = ftp服务器.
	*/
	public final AthSaveWay getAthSaveWay() {
		return AthSaveWay.forValue(this.GetValIntByKey(FrmAttachmentAttr.AthSaveWay));
	}
	public final void setAthSaveWay(AthSaveWay value)  {
		this.SetPara(FrmAttachmentAttr.AthSaveWay, value.getValue());
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
	 运行模式？
	*/
	public final AthRunModel getAthRunModel() {
		return AthRunModel.forValue(this.GetValIntByKey(FrmAttachmentAttr.AthRunModel));
	}
	public final void setAthRunModel(AthRunModel value){
		this.SetValByKey(FrmAttachmentAttr.AthRunModel, value.getValue());
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
	 备注列
	*/
	public final boolean getItIsNote()  {
		return this.GetValBooleanByKey(FrmAttachmentAttr.IsNote);
	}
	public final void setItIsNote(boolean value){
		this.SetValByKey(FrmAttachmentAttr.IsNote, value);
	}

	/**
	 是否启用扩张列
	*/
	public final boolean getItIsExpCol()  {
		return this.GetValBooleanByKey(FrmAttachmentAttr.IsExpCol);
	}
	public final void setItIsExpCol(boolean value){
		this.SetValByKey(FrmAttachmentAttr.IsExpCol, value);
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

	public final int getFileType()  {
		return this.GetValIntByKey(FrmAttachmentAttr.FileType);
	}
	/**
	 保存到
	*/
	public final String getSaveTo() throws Exception {
		String path = this.getFrmID();

		if (this.getAthSaveWay() == bp.sys.AthSaveWay.IISServer)
		{
			path = bp.difference.SystemConfig.getPathOfDataUser() + "/UploadFile/" + this.getFrmID() + "/";
		}

		if (this.getAthSaveWay() == bp.sys.AthSaveWay.FTPServer)
		{
			path = "//" + this.getFrmID() + "//";
		}

		path = path.replace("/", File.separator).replace("\\", File.separator);

		return path;
	}
	/**
	 数据关联组件ID
	*/
	public final String getDataRefNoOfObj() throws Exception {
		String str = this.GetValStringByKey(FrmAttachmentAttr.DataRefNoOfObj);
		if (Objects.equals(str, ""))
		{
			str = this.getNoOfObj();
		}
		return str;
	}
	public final void setDataRefNoOfObj(String value){
		this.SetValByKey(FrmAttachmentAttr.DataRefNoOfObj, value);
	}
	/**
	 附件编号
	*/
	public final String getNoOfObj()  {
		return this.GetValStringByKey(FrmAttachmentAttr.NoOfObj);
	}
	public final void setNoOfObj(String value){
		this.SetValByKey(FrmAttachmentAttr.NoOfObj, value);
	}


	/**
	 H
	*/
	public final float getH()  {
		return this.GetValFloatByKey(FrmAttachmentAttr.H);
	}
	public final void setH(float value)  {
		this.SetValByKey(FrmAttachmentAttr.H, value);
	}
	public final int getGroupID()  {
		return this.GetValIntByKey(FrmAttachmentAttr.GroupID);
	}
	public final void setGroupID(long value)  {
		this.SetValByKey(FrmAttachmentAttr.GroupID, value);
	}
	/**
	 阅读规则:@0=不控制@1=未阅读阻止发送@2=未阅读做记录
	*/
	public final int getReadRole()  {
		return this.GetValIntByKey(FrmAttachmentAttr.ReadRole);
	}
	public final void setReadRole(int value)  {
		this.SetValByKey(FrmAttachmentAttr.ReadRole, value);
	}


	public final int getRowIdx()  {
		return this.GetValIntByKey(FrmAttachmentAttr.RowIdx);
	}
	public final void setRowIdx(int value)  {
		this.SetValByKey(FrmAttachmentAttr.RowIdx, value);
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
	public final boolean getItIsHeLiuHuiZong() {
		return this.GetParaBoolen(FrmAttachmentAttr.IsHeLiuHuiZong);
	}
	public final void setItIsHeLiuHuiZong(boolean value)  {
		this.SetPara(FrmAttachmentAttr.IsHeLiuHuiZong, value);
	}
	/**
	 该附件是否汇总到合流节点上去？
	*/
	public final boolean getItIsToHeLiuHZ() {
		return this.GetParaBoolen(FrmAttachmentAttr.IsToHeLiuHZ);
	}
	public final void setItIsToHeLiuHZ(boolean value)  {
		this.SetPara(FrmAttachmentAttr.IsToHeLiuHZ, value);
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
	public FrmAttachment()
	{
	}
	/**
	 附件

	 @param mypk
	*/
	public FrmAttachment(String mypk) throws Exception {
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

		Map map = new Map("Sys_FrmAttachment", "附件");
		map.IndexField = FrmAttachmentAttr.FK_MapData;
		map.AddMyPK();

		map.AddTBString(FrmAttachmentAttr.FK_MapData, null, "表单ID", true, false, 1, 100, 20);
		map.AddTBString(FrmAttachmentAttr.NoOfObj, null, "附件编号", true, false, 0, 50, 20);
		map.AddTBInt(FrmAttachmentAttr.FK_Node, 0, "节点控制(对sln有效)", false, false);

		//for渔业厅增加.
		map.AddTBInt(FrmAttachmentAttr.AthRunModel, 0, "运行模式", false, false);
		map.AddTBInt(FrmAttachmentAttr.AthSaveWay, 0, "保存方式", false, false);

		map.AddTBString(FrmAttachmentAttr.Name, null, "名称", true, false, 0, 50, 20);
		map.AddTBString(FrmAttachmentAttr.Exts, null, "要求上传的格式", true, false, 0, 200, 20);
		map.AddTBInt(FrmAttachmentAttr.NumOfUpload, 0, "最小上传数量", true, false);
		map.AddTBInt(FrmAttachmentAttr.TopNumOfUpload, 99, "最大上传数量", true, false);
		map.AddTBInt(FrmAttachmentAttr.FileMaxSize, 10240, "附件最大限制(KB)", true, false);
		map.AddTBInt(FrmAttachmentAttr.UploadFileNumCheck, 0, "上传校验方式", true, false);

		map.AddTBString(FrmAttachmentAttr.Sort, null, "类别(可为空)", true, false, 0, 500, 20);

		map.AddTBFloat(FrmAttachmentAttr.H, 150, "H", false, false);

		map.AddBoolean(FrmAttachmentAttr.IsUpload, true, "是否可以上传", false, false);
		map.AddBoolean(FrmAttachmentAttr.IsVisable, true, "是否可见", false, false);
		//  map.AddTBInt(FrmAttachmentAttr.IsDelete, 1, "附件删除规则(0=不能删除1=删除所有2=只能删除自己上传的)", false, false);
		map.AddTBInt(FrmAttachmentAttr.FileType, 0, "附件类型", false, false);
		map.AddTBInt(FrmAttachmentAttr.ReadRole, 0, "阅读规则", true, true);
		map.AddTBInt(FrmAttachmentAttr.PicUploadType, 0, "图片附件上传方式", true, true);

		//hzm新增列
		map.AddTBInt(FrmAttachmentAttr.DeleteWay, 1, "附件删除规则(0=不能删除1=删除所有2=只能删除自己上传的", false, false);
		map.AddBoolean(FrmAttachmentAttr.IsDownload, true, "是否可以下载", false, false);


		map.AddBoolean(FrmAttachmentAttr.IsAutoSize, true, "自动控制大小", false, false);
		map.AddBoolean(FrmAttachmentAttr.IsNote, true, "是否增加备注", false, false);
		map.AddBoolean(FrmAttachmentAttr.IsExpCol, false, "是否启用扩展列", false, false);

		map.AddBoolean(FrmAttachmentAttr.IsShowTitle, true, "是否显示标题列", false, false);
		map.AddTBInt(FrmAttachmentAttr.UploadType, 0, "上传类型0单个1多个2指定", false, false);

		map.AddTBInt(FrmAttachmentAttr.IsIdx, 0, "是否排序", false, false);


		// map.AddBoolean(FrmAttachmentAttr.IsIdx, false, "是否排序?", true, true);


			///#region 流程属性.
		//对于父子流程有效.
		map.AddTBInt(FrmAttachmentAttr.CtrlWay, 4, "控制呈现控制方式0=PK,1=FID,2=ParentID", false, false);
		map.AddTBInt(FrmAttachmentAttr.AthUploadWay, 0, "控制上传控制方式0=继承模式,1=协作模式.", false, false);
		map.AddTBInt(FrmAttachmentAttr.ReadRole, 0, "阅读规则", true, true);

		//数据引用，如果为空就引用当前的.
		map.AddTBString(FrmAttachmentAttr.DataRefNoOfObj, null, "数据引用组件ID", true, false, 0, 150, 20, true, null);
		map.AddDDLSysEnum(FrmAttachmentAttr.AthEditType, 0, "在线编辑方式", true, true, FrmAttachmentAttr.AthEditType,
				"@0=Office插件@1=WPS插件", true);
			///#endregion 流程属性.


		//参数属性.
		map.AddTBAtParas(3000);

		//  map.AddTBInt(FrmAttachmentAttr.RowIdx, 0, "RowIdx", false, false);
		map.AddTBInt(FrmAttachmentAttr.GroupID, 0, "GroupID", false, false);
		map.AddTBString(FrmAttachmentAttr.GUID, null, "GUID", true, false, 0, 128, 20);

		map.AddTBInt(FrmAttachmentAttr.IsEnableTemplate, 0, "是否启用模板下载?", false, false);
		map.AddTBString("OpenDesc", null, "上传说明", false, false, 0, 500, 20, true, null);


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

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
		else
		{
			this.setMyPK(this.getFrmID() + "_" + this.getNoOfObj() + "_" + this.getNodeID());
		}

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

		//对于流程类的多附件，默认按照WorkID控制. add 2017.08.03  by zhoupeng.
		if (this.getNodeID() != 0 && this.getHisCtrlWay() == AthCtrlWay.PK)
		{
			this.setHisCtrlWay(AthCtrlWay.WorkID);
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
		if (this.getNodeID() == 0)
		{
			//判断是否是字段附件
			MapAttr attr = new MapAttr();
			attr.setMyPK(this.getMyPK());
			if(attr.RetrieveFromDBSources()== 0 && gf.IsExit(GroupFieldAttr.CtrlID, this.getMyPK()) == false){
				if (this.GetParaBoolen("IsFieldAth") == true)
				{
					gf.SetPara("IsFieldAth", 1);
				}
				gf.setFrmID(this.getFrmID());
				gf.setCtrlID(this.getMyPK());
				gf.setCtrlType("Ath");
				gf.setLab(this.getName());
				gf.setIdx(0);
				gf.Insert(); //插入.
				DBAccess.RunSQL("UPDATE Sys_FrmAttachment SET GroupID="+gf.getOID()+" WHERE MyPK='"+this.getMyPK()+"'");
			}

		}
		super.afterInsert();
	}

	/**
	 删除之后.
	*/
	@Override
	protected void afterDelete() throws Exception
	{
		GroupField gf = new GroupField();
		gf.Delete(GroupFieldAttr.CtrlID, this.getMyPK());

		super.afterDelete();
	}
}
