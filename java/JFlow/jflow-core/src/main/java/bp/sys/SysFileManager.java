package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import java.util.*;

/** 
 文件管理者
*/
public class SysFileManager extends EntityOID
{

		///#region 实现基本属性
	/** 
	 
	*/
	public final String getWebPath()  {
		return this.GetValStringByKey(SysFileManagerAttr.WebPath);
	}
	public final void setWebPath(String value){
		this.SetValByKey(SysFileManagerAttr.WebPath, value);
	}
	public final String getAttrFileNo()  {
		return this.GetValStringByKey(SysFileManagerAttr.AttrFileNo);
	}
	public final void setAttrFileNo(String value){
		this.SetValByKey(SysFileManagerAttr.AttrFileNo, value);
	}

	public final String getAttrFileName()  {
		return this.GetValStringByKey(SysFileManagerAttr.AttrFileName);
	}
	public final void setAttrFileName(String value){
		this.SetValByKey(SysFileManagerAttr.AttrFileName, value);
	}

	public final String getMyFileName()  {
		return this.GetValStringByKey(SysFileManagerAttr.MyFileName);
	}
	public final void setMyFileName(String value){
		this.SetValByKey(SysFileManagerAttr.MyFileName, value);
	}
	public final String getMyFileWebUrl() {
		return this.getWebPath();
	}

	public final String getMyFileExt()  {
		return this.GetValStringByKey(SysFileManagerAttr.MyFileExt);
	}
	public final void setMyFileExt(String value){
		this.SetValByKey(SysFileManagerAttr.MyFileExt, value);
	}


	public final String getRec() throws Exception {
		String s = this.GetValStringByKey(SysFileManagerAttr.Rec);
		if (s == null || Objects.equals(s, ""))
		{
			return null;
		}
		return s;
	}
	public final void setRec(String value){
		this.SetValByKey(SysFileManagerAttr.Rec, value);
	}

	public final String getRecText()  {
		return this.GetValRefTextByKey(SysFileManagerAttr.Rec);
	}
	public final String getEnName()  {
		return this.GetValStringByKey(SysFileManagerAttr.EnName);
	}
	public final void setEnName(String value){
		this.SetValByKey(SysFileManagerAttr.EnName, value);
	}
	public final Object getRefVal()  {
		return this.GetValByKey(SysFileManagerAttr.RefVal);
	}
	public final void setRefVal(Object value){
		this.SetValByKey(SysFileManagerAttr.RefVal, value);
	}
	public final String getMyFilePath()  {
		return this.GetValStringByKey(SysFileManagerAttr.MyFilePath);
	}
	public final void setMyFilePath(String value){
		this.SetValByKey(SysFileManagerAttr.MyFilePath, value);
	}
	public final int getMyFileH()  {
		return this.GetValIntByKey(SysFileManagerAttr.MyFileH);
	}
	public final void setMyFileH(int value){
		this.SetValByKey(SysFileManagerAttr.MyFileH, value);
	}
	public final int getMyFileW()  {
		return this.GetValIntByKey(SysFileManagerAttr.MyFileW);
	}
	public final void setMyFileW(int value){
		this.SetValByKey(SysFileManagerAttr.MyFileW, value);
	}
	public final float getMyFileSize()  {
		return this.GetValIntByKey(SysFileManagerAttr.MyFileSize);
	}
	public final void setMyFileSize(float value){
		this.SetValByKey(SysFileManagerAttr.MyFileSize, value);
	}
	public final String getRDT()  {
		return this.GetValStringByKey(SysFileManagerAttr.RDT);
	}
	public final void setRDT(String value){
		this.SetValByKey(SysFileManagerAttr.RDT, value);
	}
	public final String getNote()  {
		return this.GetValStringByKey(SysFileManagerAttr.Note);
	}
	public final void setNote(String value){
		this.SetValByKey(SysFileManagerAttr.Note, value);
	}

		///#endregion


		///#region 构造方法
	public SysFileManager()
	{
	}
	/** 
	 文件管理者
	 
	 <param MyFileName="_OID">
	*/
	public SysFileManager(int _OID) throws Exception {
		super(_OID);
	}
	/** 
	 map
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("Sys_FileManager", "文件管理者");

		map.AddTBIntPKOID();
		map.AddTBString(SysFileManagerAttr.AttrFileName, null, "指定名称", true, false, 0, 50, 20);
		map.AddTBString(SysFileManagerAttr.AttrFileNo, null, "指定编号", true, false, 0, 50, 20);

		map.AddTBString(SysFileManagerAttr.EnName, null, "关联的表", false, true, 1, 50, 20);
		map.AddTBString(SysFileManagerAttr.RefVal, null, "主键值", false, true, 1, 50, 10);
		map.AddTBString(SysFileManagerAttr.WebPath, null, "Web路径", false, true, 0, 100, 30);

		map.AddMyFile("文件名称");

		//map.AddTBString(SysFileManagerAttr.MyFileName, null, "文件名称", true, false, 1, 50, 20);
		//map.AddTBInt(SysFileManagerAttr.MyFileSize, 0, "文件大小", true, true);
		//map.AddTBInt(SysFileManagerAttr.MyFileH, 0, "Img高度", true, true);
		//map.AddTBInt(SysFileManagerAttr.MyFileW, 0, "Img宽度", true, true);
		//map.AddTBString(SysFileManagerAttr.MyFileExt, null, "文件类型", true, true, 0, 50, 20);

		map.AddTBString(SysFileManagerAttr.RDT, null, "上传时间", true, true, 1, 50, 20);
		map.AddTBString(SysFileManagerAttr.Rec, null, "上传人", true, true, 0, 50, 20);
		map.AddTBStringDoc();
		this.set_enMap(map);
		return this.get_enMap();
	}
	@Override
	protected boolean beforeInsert() throws Exception
	{
	   this.setRec(bp.web.WebUser.getNo());
	   this.setRDT(DataType.getCurrentDateTime());
		return super.beforeInsert();
	}
	@Override
	protected boolean beforeDelete() throws Exception
	{
		if (Objects.equals(this.getRec(), bp.web.WebUser.getNo()))
		{
			return super.beforeDelete();
		}
		return super.beforeDelete();
	}

		///#endregion


		///#region　共用方法
	public final void UpdateLoadFileOfAccess(String MyFilePath)
	{
		//FileInfo fi = new FileInfo(MyFilePath);// Replace with your file MyFileName
		//if (fi.Exists == false)
		//    throw new Exception("文件已经不存在。");

		//this.MyFileSize =int.Parse( fi.length().ToString());
		//this.MyFilePath = fi.FullMyFileName;
		//this.MyFileName = fi.MyFileName;
		//this.Insert();

		//byte[] bData = null;
		////int nNewFileID = 0;
		//// Read file data into buffer
		//using (FileStream fs = fi.OpenRead())
		//{
		//    bData = new byte[fi.length()];
		//    int nReadLength = fs.Read(bData, 0, (int)(fi.length()));
		//}

		////			// Add file info into DB
		////			String strQuery = "INSERT INTO FileInfo " 
		////				+ " ( FileMyFileName, FullMyFileName, FileData ) "
		////				+ " VALUES "
		////				+ " ( @FileMyFileName, @FullMyFileName, @FileData ) "
		////				+ " SELECT @@IDENTITY AS 'Identity'";

		//String strQuery = "UPDATE Sys_FileManager SET FileData=@FileData WHERE OID=" + this.OID;
		//OleDbConnection conn = (OleDbConnection)BP.DA.DBAccess.GetAppCenterDBConn;
		//conn.Open();

		//OleDbCommand sqlComm = new OleDbCommand(strQuery,
		//    conn);

		////sqlComm.Parameters.Add( "@FileMyFileName", fi.MyFileName );
		////sqlComm.Parameters.Add( "@FullMyFileName", fi.FullMyFileName );
		//sqlComm.Parameters.AddWithValue("@FileData", bData);
		//sqlComm.ExecuteNonQuery();

		//// Get new file ID
		////	SqlDataReader sqlReader = sqlComm.ExecuteReader(); 
		////			if( sqlReader.Read() )
		////			{
		////				nNewFileID = int.Parse(sqlReader.GetValue(0).ToString());
		////			}
		////
		////			sqlReader.Close();
		////			sqlComm.Dispose();
		////
		////			if( nNewFileID > 0 )
		////			{
		////				// Add new item in list view
		////				//ListViewItem itmNew = lsvFileInfo.Items.Add( fi.MyFileName );
		////				//itmNew.Tag = nNewFileID;
		////			}
	}

		///#endregion
}
