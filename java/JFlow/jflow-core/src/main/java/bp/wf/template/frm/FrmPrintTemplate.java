package bp.wf.template.frm;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;
import java.io.*;

/** 
 打印模板
*/
public class FrmPrintTemplate extends EntityMyPK
{

		///#region  属性
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		return uac;
	}
	/** 
	 编号
	*/
	public final String getMyPK()  {
		String no = this.GetValStrByKey("MyPK");
		no = no.replace("\n", "");
		no = no.replace(" ", "");
		return no;
	}
	public final void setMyPK(String value){
		this.SetValByKey("MyPK", value);
		this.SetValByKey(FrmPrintTemplateAttr.TempFilePath, value);
	}
	/** 
	 生成的单据类型
	*/
	public final PrintFileType getHisPrintFileType() {
		return PrintFileType.forValue(this.GetValIntByKey(FrmPrintTemplateAttr.PrintFileType));
	}
	public final void setHisPrintFileType(PrintFileType value){
		this.SetValByKey(FrmPrintTemplateAttr.PrintFileType, value.getValue());
	}
	/** 
	 二维码生成方式
	*/
	public final QRModel getQRModel() {
		return QRModel.forValue(this.GetValIntByKey(FrmPrintTemplateAttr.QRModel));
	}
	public final void setQRModel(QRModel value){
		this.SetValByKey(FrmPrintTemplateAttr.QRModel, value.getValue());
	}
	public final TemplateFileModel getTemplateFileModel() {
		return TemplateFileModel.forValue(this.GetValIntByKey(FrmPrintTemplateAttr.TemplateFileModel));
	}
	public final void setTemplateFileModel(TemplateFileModel value){
		this.SetValByKey(FrmPrintTemplateAttr.TemplateFileModel, value.getValue());
	}

	/** 
	 生成的单据打开方式
	*/
	public final PrintOpenModel getPrintOpenModel() {
		return PrintOpenModel.forValue(this.GetValIntByKey(FrmPrintTemplateAttr.PrintOpenModel));
	}
	public final void setPrintOpenModel(PrintOpenModel value){
		this.SetValByKey(FrmPrintTemplateAttr.PrintOpenModel, value.getValue());
	}
	/** 
	 打开的连接
	*/
	public final String getTempFilePath() throws Exception {
		String s = this.GetValStrByKey(FrmPrintTemplateAttr.TempFilePath);
		if (DataType.IsNullOrEmpty(s) == true)
		{
			return this.getMyPK();
		}
		return s;
	}
	public final void setTempFilePath(String value){
		this.SetValByKey(FrmPrintTemplateAttr.TempFilePath, value);
	}
	/** 
	 节点名称
	*/
	public final String getNodeName() throws Exception {
		Node nd = new Node(this.getNodeID());
		return nd.getName();
	}
	/** 
	 节点ID
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(FrmPrintTemplateAttr.NodeID);
	}
	public final void setNodeID(int value){
		this.SetValByKey(FrmPrintTemplateAttr.NodeID, value);
	}
	public final String getName()  {
		return this.GetValStringByKey(FrmPrintTemplateAttr.Name);
	}
	public final void setName(String value){
		this.SetValByKey(FrmPrintTemplateAttr.Name, value);
	}
	public final String getFrmID()  {
		return this.GetValStringByKey(FrmPrintTemplateAttr.FrmID);
	}
	public final void setFrmID(String value){
		this.SetValByKey(FrmPrintTemplateAttr.FrmID, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 打印模板
	*/
	public FrmPrintTemplate()
	{
	}
	/** 
	 打印模板
	 
	 @param mypk 主键
	*/
	public FrmPrintTemplate(String mypk) throws Exception
	{
		super(mypk.replace("\n", "").trim());
	}
	/** 
	 获得单据文件流
	 @return 
	*/
	public final byte[] GenerTemplateFile() throws Exception {
		byte[] bytes = DBAccess.GetByteFromDB(this.getEnMap().getPhysicsTable(), "MyPK", this.getMyPK(), "DBFile");
		if (bytes != null)
		{
			return bytes;
		}

		//如果没有找到，就看看默认的文件是否有.
		String tempExcel = bp.difference.SystemConfig.getPathOfDataUser() + "CyclostyleFile/" + this.getMyPK() + ".rtf";
		if ((new File(tempExcel)).isFile() == false)
		{
			tempExcel = bp.difference.SystemConfig.getPathOfDataUser() + "CyclostyleFile/Word单据模版定义演示.docx";
		}

		bytes = DataType.ConvertFileToByte(tempExcel);
		return bytes;
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("Sys_FrmPrintTemplate", "打印模板");
		map.IndexField = FrmPrintTemplateAttr.FrmID;

		map.AddMyPK(true);

		map.AddTBString(FrmPrintTemplateAttr.Name, null, "名称", true, false, 0, 200, 20);
		map.AddTBString(FrmPrintTemplateAttr.TempFilePath, null, "模板路径", true, false, 0, 200, 20);

		map.AddTBInt(FrmPrintTemplateAttr.NodeID, 0, "节点ID", true, false);
		map.AddTBString(FrmPrintTemplateAttr.FlowNo, null, "流程编号", true, false, 0, 200, 20);

		map.AddTBString(FrmPrintTemplateAttr.FrmID, null, "表单ID", false, false, 0, 60, 60);

		map.AddDDLSysEnum(FrmPrintTemplateAttr.TemplateFileModel, 0, "模版模式", true, false, FrmPrintTemplateAttr.TemplateFileModel, "@0=rtf模版@1=VSTO模式的word模版@2=VSTO模式的Excel模版@3=Wps模板");

		map.AddDDLSysEnum(FrmPrintTemplateAttr.PrintFileType, 0, "生成的文件类型", true, false, "PrintFileType", "@0=Word@1=PDF@2=Excel@3=Html");

		map.AddDDLSysEnum(FrmPrintTemplateAttr.PrintOpenModel, 0, "生成的文件打开方式", true, false, "PrintOpenModel", "@0=下载本地@1=在线打开");

		map.AddDDLSysEnum(FrmAttachmentAttr.AthSaveWay, 0, "实例的保存方式", true, true, FrmAttachmentAttr.AthSaveWay, "@0=保存到web服务器@1=保存到数据库@2=ftp服务器@3=保存到对象存储OSS");


		map.AddDDLSysEnum(FrmPrintTemplateAttr.QRModel, 0, "二维码生成方式", true, false, FrmPrintTemplateAttr.QRModel, "@0=不生成@1=生成二维码");

		map.AddTBInt("Idx", 0, "Idx", false, false);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			this.setMyPK(DBAccess.GenerGUID(0, null, null));
		}
		return super.beforeInsert();
	}
}
