package bp.wf.httphandler;

import bp.da.*;
import bp.difference.*;
import bp.difference.handler.CommonFileUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/** 
 页面功能实体
*/
public class WF_Admin_FoolFormDesigner_Template_FrmAttachmentSingle extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_Admin_FoolFormDesigner_Template_FrmAttachmentSingle()
	{

	}


		///#region  界面 .
	/** 
	 生成模板文件
	 
	 @return 
	*/
	public final String UploadAthTemplateWPS_Init() throws Exception {
		bp.sys.frmui.FrmAttachmentSingle ath = new bp.sys.frmui.FrmAttachmentSingle(this.getMyPK());
		String file = SystemConfig.getPathOfTemp() + "/" + this.getMyPK() + ".wps";
		DBAccess.GetFileFromDB(file, ath.getEnMap().getPhysicsTable(), "MyPK", this.getMyPK(), "TemplateFile");
		return file;
	}
	/** 
	 保存模板文件
	 
	 @return 
	*/
	public final String UploadAthTemplateWPS_Save() throws Exception {
		HttpServletRequest request = ContextHolderUtils.getRequest();

		if (CommonFileUtils.getFilesSize(request,"file") == 0)
		{
			return "err@请上传文件模板";
		}
		//保存文件到临时目录
		String fileName = CommonFileUtils.getOriginalFilename(request,"file");
		if(fileName.indexOf("\\")>-1){
			fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
		}
		if(fileName.indexOf("/")>-1){
			fileName = fileName.substring(fileName.lastIndexOf("/")+1);
		}
		String path = SystemConfig.getPathOfTemp() + fileName;
		CommonFileUtils.upload(request,"file",new File(path));
		bp.sys.frmui.FrmAttachmentSingle ath = new bp.sys.frmui.FrmAttachmentSingle(this.getMyPK());
		//存储到模板库里。
		DBAccess.SaveFileToDB(path, ath.getEnMap().getPhysicsTable(), "MyPK", this.getMyPK(), "TemplateFile");
		return "保存成功";
	}

		///#endregion 界面.

}
