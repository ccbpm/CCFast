package bp.sys;

import bp.da.*;
import bp.difference.*;
import bp.en.*;
import bp.en.Map;
import bp.tools.FtpUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;

import java.io.*;

/** 
 附件数据存储
*/
public class FrmAttachmentDB extends EntityMyPK
{

		///#region 属性
	/** 
	 类别
	*/
	public final String getSort()  {
		return this.GetValStringByKey(FrmAttachmentDBAttr.Sort);
	}
	public final void setSort(String value){
		this.SetValByKey(FrmAttachmentDBAttr.Sort, value);
	}
	/** 
	 记录日期
	*/
	public final String getRDT() throws Exception {
		String str = this.GetValStringByKey(FrmAttachmentDBAttr.RDT);
		return str.substring(5, 16);
	}
	public final void setRDT(String value){
		this.SetValByKey(FrmAttachmentDBAttr.RDT, value);
	}
	/** 
	 文件
	*/
	public final String getFileFullName()  {
		return this.GetValStringByKey(FrmAttachmentDBAttr.FileFullName);
	}
	public final void setFileFullName(String value) throws Exception {
		String str = value;
		str = str.replace("~", "-");
		str = str.replace("'", "-");
		str = str.replace("*", "-");
		this.SetValByKey(FrmAttachmentDBAttr.FileFullName, str);
	}
	/** 
	 上传GUID
	*/
	public final String getUploadGUID()  {
		return this.GetValStringByKey(FrmAttachmentDBAttr.UploadGUID);
	}
	public final void setUploadGUID(String value){
		this.SetValByKey(FrmAttachmentDBAttr.UploadGUID, value);
	}
	/** 
	 附件路径
	*/
	public final String getFilePathName() throws Exception {

		return this.getFileFullName().substring(this.getFileFullName().lastIndexOf('/') + 1);
	}
	/** 
	 附件名称
	*/
	public final String getFileName()  {
		return this.GetValStringByKey(FrmAttachmentDBAttr.FileName);
	}
	public final void setFileName(String value) throws Exception {
		String str = value;
		str = str.replace("~", "-");
		str = str.replace("'", "-");
		str = str.replace("*", "-");

		this.SetValByKey(FrmAttachmentDBAttr.FileName, str);

		String fileExt = str.substring(str.lastIndexOf('.') + 1);
		//后缀名.
		this.SetValByKey(FrmAttachmentDBAttr.FileExts, fileExt);
	}
	/** 
	 附件扩展名
	*/
	public final String getFileExts()  {
		return this.GetValStringByKey(FrmAttachmentDBAttr.FileExts);
	}
	public final void setFileExts(String value)
	{
		String val = value.replace(".", "");
		String[] words = {"asp", "jsp", "do", "php", "msi", "bat", "exe", "sql"};
		val = val.toLowerCase();
		for (String item : words)
		{
			if (val.contains(item) && val.length() == item.length())
			{
				throw new RuntimeException("err@非法的文件格式.");
			}

		}
	}
	/** 
	 相关附件
	*/
	public final String getFKFrmAttachment()  {
		return this.GetValStringByKey(FrmAttachmentDBAttr.FK_FrmAttachment);
	}
	public final void setFKFrmAttachment(String value){
		this.SetValByKey(FrmAttachmentDBAttr.FK_FrmAttachment, value);


		if (DataType.IsNullOrEmpty(this.getFrmID()) == true)
		{
			throw new RuntimeException("err@错误:请首先给FK_MapData赋值..");
		}

		//获取最后"_"的位置
		String val = value.replace(this.getFrmID() + "_", "");
		this.SetValByKey(FrmAttachmentDBAttr.NoOfObj, val);
	}
	/** 
	 主键值
	*/
	public final String getRefPKVal()  {
		return this.GetValStringByKey(FrmAttachmentDBAttr.RefPKVal);
	}
	public final void setRefPKVal(String value){
		this.SetValByKey(FrmAttachmentDBAttr.RefPKVal, value);
	}
	/** 
	 工作ID.
	*/
	public final long getFID()  {
		return this.GetValInt64ByKey(FrmAttachmentDBAttr.FID);
	}
	public final void setFID(long value){
		this.SetValByKey(FrmAttachmentDBAttr.FID, value);
	}
	/** 
	 MyNote
	*/
	public final String getMyNote()  {
		return this.GetValStringByKey(FrmAttachmentDBAttr.MyNote);
	}
	public final void setMyNote(String value){
		this.SetValByKey(FrmAttachmentDBAttr.MyNote, value);
	}
	public final String getNoOfObj()  {
		return this.GetValStringByKey(FrmAttachmentDBAttr.NoOfObj);
	}
	public final void setNoOfObj(String value){
		this.SetValByKey(FrmAttachmentDBAttr.NoOfObj, value);
	}
	/** 
	 记录人
	*/
	public final String getRec()  {
		return this.GetValStringByKey(FrmAttachmentDBAttr.Rec);
	}
	public final void setRec(String value){
		this.SetValByKey(FrmAttachmentDBAttr.Rec, value);
	}
	/** 
	 记录人名称
	*/
	public final String getRecName()  {
		return this.GetValStringByKey(FrmAttachmentDBAttr.RecName);
	}
	public final void setRecName(String value){
		this.SetValByKey(FrmAttachmentDBAttr.RecName, value);
	}

	/** 
	 所在部门
	*/
	public final String getDeptNo()  {
		return this.GetValStringByKey(FrmAttachmentDBAttr.FK_Dept);
	}
	public final void setDeptNo(String value){
		this.SetValByKey(FrmAttachmentDBAttr.FK_Dept, value);
	}
	/** 
	 所在部门名称
	*/
	public final String getDeptName()  {
		return this.GetValStringByKey(FrmAttachmentDBAttr.FK_DeptName);
	}
	public final void setDeptName(String value){
		this.SetValByKey(FrmAttachmentDBAttr.FK_DeptName, value);
	}
	/** 
	 附件编号
	*/
	public final String getFrmID()  {
		return this.GetValStringByKey(FrmAttachmentDBAttr.FK_MapData);
	}
	public final void setFrmID(String value){
		this.SetValByKey(FrmAttachmentDBAttr.FK_MapData, value);
	}
	/** 
	 文件大小
	*/
	public final float getFileSize()  {
		return this.GetValFloatByKey(FrmAttachmentDBAttr.FileSize);
	}
	public final void setFileSize(float value){
		this.SetValByKey(FrmAttachmentDBAttr.FileSize, Float.parseFloat(String.format("%.2f", value/1024)));
	}
	/** 
	 是否锁定行?
	*/
	public final boolean getItIsRowLock()  {
		return this.GetValBooleanByKey(FrmAttachmentDBAttr.IsRowLock);
	}
	public final void setItIsRowLock(boolean value){
		this.SetValByKey(FrmAttachmentDBAttr.IsRowLock, value);
	}
	/** 
	 显示顺序
	*/
	public final int getIdx()  {
		return this.GetValIntByKey(FrmAttachmentDBAttr.Idx);
	}
	public final void setIdx(int value){
		this.SetValByKey(FrmAttachmentDBAttr.Idx, value);
	}
	/** 
	 附件扩展名
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(FrmAttachmentDBAttr.NodeID);
	}
	public final void setNodeID(int value){
		this.SetValByKey(FrmAttachmentDBAttr.NodeID, value);
	}
	/** 
	 附件类型
	*/
	public final AttachmentUploadType getHisAttachmentUploadType() throws Exception {
		if (this.getMyPK().contains("_") && this.getMyPK().length() < 32)
		{
			return AttachmentUploadType.Single;
		}
		else
		{
			return AttachmentUploadType.Multi;
		}
	}

		///#endregion


		///#region 构造方法
	/** 
	 附件数据存储
	*/
	public FrmAttachmentDB()
	{
	}
	/** 
	 附件数据存储
	 
	 @param mypk
	*/
	public FrmAttachmentDB(String mypk) throws Exception {
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
		Map map = new Map("Sys_FrmAttachmentDB", "附件数据存储");

		map.IndexField = FrmAttachmentDBAttr.RefPKVal;

		map.AddMyPK();

		map.AddTBString(FrmAttachmentDBAttr.FK_MapData, null, "FK_MapData", true, false, 1, 100, 20);
		map.AddTBString(FrmAttachmentDBAttr.FK_FrmAttachment, null, "附件主键", true, false, 1, 500, 20);
		map.AddTBString(FrmAttachmentDBAttr.NoOfObj, null, "附件标识", true, false, 0, 50, 20);

		map.AddTBString(FrmAttachmentDBAttr.RefPKVal, null, "实体主键", true, false, 0, 50, 20);
		map.AddTBInt(FrmAttachmentDBAttr.FID, 0, "FID", true, false);
		map.AddTBInt(FrmAttachmentDBAttr.NodeID, 0, "节点ID", true, false);


		map.AddTBString(FrmAttachmentDBAttr.Sort, null, "类别", true, false, 0, 200, 20);
		map.AddTBString(FrmAttachmentDBAttr.FileFullName, null, "文件路径", true, false, 0, 700, 20);
		map.AddTBString(FrmAttachmentDBAttr.FileName, null, "名称", true, false, 0, 500, 20);
		map.AddTBString(FrmAttachmentDBAttr.FileExts, null, "扩展", true, false, 0, 50, 20);
		map.AddTBFloat(FrmAttachmentDBAttr.FileSize, 0, "文件大小", true, false);

		map.AddTBDateTime(FrmAttachmentDBAttr.RDT, null, "记录日期", true, false);
		map.AddTBString(FrmAttachmentDBAttr.Rec, null, "记录人", true, false, 0, 50, 20);
		map.AddTBString(FrmAttachmentDBAttr.RecName, null, "记录人名字", true, false, 0, 50, 20);
		map.AddTBString(FrmAttachmentDBAttr.FK_Dept, null, "所在部门", true, false, 0, 50, 20);
		map.AddTBString(FrmAttachmentDBAttr.FK_DeptName, null, "所在部门名称", true, false, 0, 50, 20);
		map.AddTBStringDoc(FrmAttachmentDBAttr.MyNote, null, "备注", true, false);

		map.AddTBInt(FrmAttachmentDBAttr.IsRowLock, 0, "是否锁定行", true, false);

		//顺序.
		map.AddTBInt(FrmAttachmentDBAttr.Idx, 0, "排序", true, false);

		//这个值在上传时候产生.
		map.AddTBString(FrmAttachmentDBAttr.UploadGUID, null, "上传GUID", true, false, 0, 500, 20);

		map.AddTBAtParas(3000); //增加参数属性.

		this.set_enMap(map);
		return this.get_enMap();
	}

	/**
	 * 从OSS下载文件到临时文件中
	 * @return 下载的文件
	 * @throws Exception
	 */
	private String MakeFullFileFromOSS() throws Exception {
		String pathOfTemp = SystemConfig.getPathOfTemp();
		if ((new File(pathOfTemp)).isDirectory() == false)
		{
			(new File(pathOfTemp)).mkdirs();
		}

		String tempFile = pathOfTemp + DBAccess.GenerGUID() + "." + this.getFileExts();
		if ((new File(tempFile)).isFile() == true)
		{
			(new File(tempFile)).delete();
		}

		// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(bp.difference.SystemConfig.getOSSEndpoint()
				, bp.difference.SystemConfig.getOSSAccessKeyId(), bp.difference.SystemConfig.getOSSAccessKeySecret());
		try {
			//转换路径
			this.setFileFullName(this.getFileFullName().replace("//","/"));
			// ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
			OSSObject ossObject = ossClient.getObject(bp.difference.SystemConfig.getOSSBucketName(), this.getFileFullName());

			// 读取文件内容。
			//System.out.println("Object content:");
			BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			while (true) {
				String line = reader.readLine();
				if (line == null) break;

				writer.write(line);
				writer.newLine(); // 写入换行符
				//System.out.println("\n" + line);
			}
			// 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
			reader.close();
			writer.close();
			// ossObject对象使用完毕后必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
			ossObject.close();

		} catch (OSSException oe) {
			String err = "捕获到OSSException，这意味着您的请求发送到OSS，但由于某种原因被错误响应拒绝。";
			err += "\nError Message:" + oe.getErrorMessage();
			err += "\nError Code:" + oe.getErrorCode();
			err += "\nRequest ID:" + oe.getRequestId();
			err += "\nHost ID:" + oe.getHostId();
			Log.DebugWriteError(err);
			throw new RuntimeException(err);
		} catch (Throwable ce) {
			String err = "捕获到ClientException，这意味着客户端在尝试与OSS通信时遇到了严重的内部问题，例如无法访问网络。";
			err += "\nError Message:" + ce.getMessage();
			Log.DebugWriteError(err);
			throw new RuntimeException(err);
		} finally {
			if (ossClient != null) {
				ossClient.shutdown();
			}
		}

		return tempFile;
	}

	/** 
	 生成文件.
	 
	 @return 
	*/
	private String MakeFullFileFromFtp() throws Exception {
		String pathOfTemp = SystemConfig.getPathOfTemp();
		if ((new File(pathOfTemp)).isDirectory() == false)
		{
			(new File(pathOfTemp)).mkdirs();
		}
		String tempFile = pathOfTemp + DBAccess.GenerGUID() + "." + this.getFileExts();
		if ((new File(tempFile)).isFile() == true)
		{
			(new File(tempFile)).delete();
		}

		return tempFile;
	}
	public final String DoDown() throws Exception
	{

		return "执行成功.";
	}

	/** 
	 重写
	 
	 @return 
	*/
	@Override
	protected boolean beforeInsert() throws Exception
	{
		return super.beforeInsert();
	}

	@Override
	protected void afterDelete() throws Exception
	{
		//判断删除excel数据提取的数据
		if ((this.getFKFrmAttachment() == null || DataType.IsNullOrEmpty(this.getFKFrmAttachment())))
		{
			return;
		}

		FrmAttachment ath = new FrmAttachment(this.getFKFrmAttachment());
		try
		{
			// @于庆海需要翻译.
			if (ath.getAthSaveWay() == bp.sys.AthSaveWay.IISServer)
			{
				(new File(this.getFileFullName())).delete();
			}

			if (ath.getAthSaveWay() == bp.sys.AthSaveWay.FTPServer)
			{
				FtpUtil ftpUtil = bp.sys.base.Glo.getFtpUtil();
				String fullName = this.getFileFullName();
				ftpUtil.deleteFile(fullName);
			}
		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteError(ex.getMessage());
		}

		super.afterDelete();
	}



		///#endregion

	/** 
	 获得临时文件
	 
	 @return 
	*/

	public final String GenerTempFile() throws Exception {
		return GenerTempFile(bp.sys.AthSaveWay.IISServer);
	}

	/***
	 * 返回临时文件绝对路径
	 * @return
	 * @throws Exception
	 */
	public String MakeTempFileFullPath() throws Exception {
		return this.MakeFullFileFromFtp();
	}
	public final String GenerTempFile(AthSaveWay saveWay) throws Exception {
		if (saveWay == bp.sys.AthSaveWay.IISServer)
		{
			return this.getFileFullName();
		}

		if (saveWay == bp.sys.AthSaveWay.FTPServer)
		{
			String tempFile=this.MakeFullFileFromFtp();
			FtpUtil ftpUtil = bp.wf.Glo.getFtpUtil();
			ftpUtil.openConnection();
			ftpUtil.downloadFile(this.getFileFullName(), tempFile);
			return tempFile;

		}
		if(saveWay == AthSaveWay.Minio){
			String tempFile=this.MakeFullFileFromFtp();
			return  tempFile;
		}

		if (saveWay == bp.sys.AthSaveWay.OSS)
		{
			return this.MakeFullFileFromOSS();
		}

		if (saveWay == bp.sys.AthSaveWay.DB)
		{
			String tempFile=this.MakeFullFileFromFtp();
			return tempFile;


		}

		throw new RuntimeException("@尚未处理存储到db里面的文件.");
	}

	/** 
	 向下移動
	*/
	public final void DoDownTabIdx() throws Exception {
		this.DoOrderDown(FrmAttachmentDBAttr.RefPKVal, this.getRefPKVal(), FrmAttachmentDBAttr.FK_FrmAttachment, this.getFKFrmAttachment(), FrmAttachmentDBAttr.Idx);
	}
	/** 
	 向上移動
	*/
	public final void DoUpTabIdx() throws Exception {
		this.DoOrderUp(FrmAttachmentDBAttr.RefPKVal, this.getRefPKVal(), FrmAttachmentDBAttr.FK_FrmAttachment, this.getFKFrmAttachment(), FrmAttachmentDBAttr.Idx);

		//  this.DoOrderUp(FrmAttachmentDBAttr.FK_MapData, this.FrmID, FrmAttachmentDBAttr.Idx);
	}
}
