package bp.wf.httphandler;

import bp.da.*;
import bp.difference.handler.CommonFileUtils;
import bp.wf.template.*;
import bp.wf.template.frm.*;
import bp.difference.*;
import bp.*;
import bp.wf.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/** 
 页面功能实体
*/
public class WF_Admin_FoolFormDesigner_PrintTemplate extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_Admin_FoolFormDesigner_PrintTemplate()
	{
	}


		///#region  单据模版维护
	/** 
	 @李国文.
	 
	 @return 
	*/
	public final String Bill_Save() throws Exception {
		FrmPrintTemplate bt = new FrmPrintTemplate();
		HttpServletRequest request = getRequest();
		// 上传附件
		MultipartHttpServletRequest mrequest = CommonFileUtils.getMultipartHttpServletRequest(request);
		MultipartFile item = mrequest.getFile("file");
		String fileName = item.getOriginalFilename();
		String filepath = SystemConfig.getPathOfCyclostyleFile() + fileName;
		filepath = filepath.replace("\\", "/");
		File tempFile = new File(filepath);
		MultipartFile multipartFile = mrequest.getFile("File");
		CommonFileUtils.upload(request,"file",tempFile);

		bt.setNodeID(this.getFK_Node());
		bt.setFrmID(this.getFK_MapData());
		bt.setMyPK(this.GetRequestVal("TB_No"));

		if (DataType.IsNullOrEmpty(bt.getMyPK()))
		{
			bt.setMyPK(String.valueOf(DBAccess.GenerOID("Template")));
		}

		bt.setName(this.GetRequestVal("TB_Name"));
		bt.setTempFilePath(fileName); //文件.

		//打印的文件类型.
		bt.setHisPrintFileType(PrintFileType.forValue(this.GetRequestValInt("DDL_BillFileType")));

		//打开模式.
		bt.setPrintOpenModel(PrintOpenModel.forValue(this.GetRequestValInt("DDL_BillOpenModel")));

		//二维码模式.
		bt.setQRModel(QRModel.forValue(this.GetRequestValInt("DDL_QRModel")));

		bt.setTemplateFileModel(TemplateFileModel.forValue(this.GetRequestValInt("TemplateFileModel")));


		bt.Save();

		bt.SaveFileToDB("DBFile", filepath); //把文件保存到数据库里.

		Cache.ClearCache(fileName);
		Cache.ClearCache(fileName+ "Para");

		return "保存成功.";
	}
	private HttpServletResponse response;
	public void setHttpServletResp(HttpServletResponse response) {

		this.response = response;
	}
	/** 
	 下载文件.
	*/
	public final void Bill_Download() throws Exception {
		FrmPrintTemplate en = new FrmPrintTemplate(this.getNo());
		String MyFilePath = en.getTempFilePath();
		String fileName = MyFilePath.substring(MyFilePath.lastIndexOf("/") + 1);

		// 设置响应头和客户端保存文件名
		response.setCharacterEncoding("utf-8");
		response.setContentType("multipart/form-data");
		response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);

		InputStream inputStream = new FileInputStream(MyFilePath);

		OutputStream os = response.getOutputStream();

		long downloadedLength = 0l;
		byte[] b = new byte[2048];
		int length;
		while ((length = inputStream.read(b)) > 0) {
			os.write(b, 0, length);
			downloadedLength += b.length;
		}

		os.close();
		inputStream.close();
	}
		///#endregion

}
