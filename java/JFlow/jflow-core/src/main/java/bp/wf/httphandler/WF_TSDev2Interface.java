package bp.wf.httphandler;

import bp.da.*;
import bp.difference.handler.CommonFileUtils;
import bp.sys.*;
import bp.tools.StringUtils;
import bp.web.*;
import bp.port.*;
import bp.en.*; import bp.en.Map;
import bp.wf.*;
import bp.wf.template.*;
import bp.difference.*;
import bp.*;
import bp.wf.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

/** 
 页面功能实体
*/
public class WF_TSDev2Interface extends bp.difference.handler.DirectoryPageBase
{

		///#region 参数.
	public final String getParas()
	{
		return this.GetRequestVal("Paras");
	}

		///#endregion
	/** 
	 构造函数
	*/
	public WF_TSDev2Interface()
	{
	}
	public final String Flow_Start() throws Exception {
		bp.wf.httphandler.WF_MyFlow hand = new WF_MyFlow();
		return hand.MyFlow_Init();
	}
	/** 
	 创建空白的WorkID.
	 
	 @return 
	*/
	public final String Node_CreateBlankWork() throws Exception {
		//var en = new TSEntityMyPK();
		//en.getClassID() = "TS.ZH.ND2001Dtl1";
		//en.setMyPK("xxxx";
		//en.Retrieve();
		//String addr = en.GetValByKey("Tel");

		String strs = this.getParas();
		AtPara ap = new AtPara(strs);
		long workid = Dev2Interface.Node_CreateBlankWork(this.getFlowNo(), ap.getHisHT());
		return String.valueOf(workid);
	}

	public String Node_SetDraft() throws Exception {
		Dev2Interface.Node_SetDraft(this.getWorkID());
		return "执行成功";
	}
	/** 
	 执行发送动作.
	 
	 @return 
	*/
	public final String Node_SendWork() throws Exception {
		String toEmps = this.GetRequestVal("ToEmps");
		return Dev2Interface.Node_SendWork(this.getFlowNo(), this.getWorkID(), this.getToNodeID(), toEmps).ToMsgOfText();
	}
	public final String Flow_DeleteFlow()
	{
		return Dev2Interface.Flow_DoDeleteFlowByReal(this.getWorkID(), false);
	}

	/** 
	 删除草稿
	 
	 @return 
	*/
	public final String Flow_DoDeleteDraft() throws Exception {
		return Dev2Interface.Flow_DoDeleteDraft(this.getFlowNo(), this.getWorkID(), false);
	}
	/** 
	 执行退回操作
	 @return
	*/
	public final String Node_ReturnWork() throws Exception {
		String msg = this.GetRequestVal("Msg");
		return Dev2Interface.Node_ReturnWork(this.getWorkID(), this.getToNodeID(), msg, false);
	}
	/// <summary>
	/// 
	/// </summary>
	/// <returns></returns>
	public String Port_SendMsg() throws Exception {
		String title = this.GetRequestVal("Title");
		String docs = this.GetRequestVal("Docs");
		String toEmpNos = this.GetRequestVal("ToEmpNos");

		Dev2Interface.Port_SendMsg(toEmpNos, title, docs, "Msg", "Msg", "", 0, 0, 0, null);
		return "执行成功.";
	}
	public String Port_SendMsgByBillInfo() throws Exception {
		String title = this.GetRequestVal("Title");
		String docs = this.GetRequestVal("Docs");
		String toEmpNos = this.GetRequestVal("ToEmpNos");
		String frmID = this.GetRequestVal("FrmID");
		long workID = this.GetRequestValInt64("WorkID");

		Dev2Interface.Port_SendMsg(toEmpNos, title, docs, "FrmBill"+workID+frmID, "FrmBill", frmID, workID, 0, 0, null);
		return "执行成功.";
	}
	public String Port_SendMsgByFlowInfo() throws Exception {
		String title = this.GetRequestVal("Title");
		String docs = this.GetRequestVal("Docs");
		String toEmpNos = this.GetRequestVal("ToEmpNos");
		long workID = this.GetRequestValInt64("WorkID");

		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		Dev2Interface.Port_SendMsg(toEmpNos, title, docs, "FlowInfo"+workID, "FlowInfo", gwf.getFlowNo(), workID, 0, 0, null);
		return "执行成功.";
	}
	public final String UploadFile()
	{

		try
		{
			String fileName = this.GetRequestVal("fileName");
			HttpServletRequest request = ContextHolderUtils.getRequest();

			if (CommonFileUtils.getFilesSize(request,"file") == 0)
			{
				return "err@请选择要上传的文件。";
			}

			String fileType = this.GetRequestVal("fileType");
			String path = SystemConfig.getPathOfDataUser() + "UploadFile";
			if(StringUtils.isNotEmpty(fileType)) {
				path = path + "/" + fileType;
			}
			if (!(new File(path)).isDirectory())
			{
				(new File(path)).mkdirs();
			}

			String filePath = path + "/" + fileName;
			String refFileName = fileName;
			if(StringUtils.isNotEmpty(fileType)) {
				refFileName = fileType + "/" + refFileName;
			}
			String relativePath = "/DataUser/UploadFile/" + refFileName;
			File file = new File(filePath);
			if (file.isFile())
			{
				file.delete();
			}
			//这里使用绝对路径来索引
			CommonFileUtils.upload(request,"file",file);
			return relativePath;
		}
		catch (Exception ex)
		{
			return ex.toString();
		}

	}
}
