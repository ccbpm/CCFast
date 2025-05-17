package bp.wf.httphandler;

import bp.da.*;
import bp.difference.handler.CommonFileUtils;
import bp.difference.handler.CommonUtils;
import bp.sys.*;
import bp.tools.DateUtils;
import bp.tools.Word2PdfUtil;
import bp.web.*;
import bp.port.*;
import bp.en.*;
import bp.wf.Glo;
import bp.wf.data.*;
import bp.wf.template.*;
import bp.wf.dts.*;
import bp.difference.*;
import bp.wf.template.sflow.*;
import bp.wf.template.frm.*;
import bp.wf.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.time.*;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 页面功能实体
 */
public class WF_WorkOpt extends bp.difference.handler.DirectoryPageBase
{

	//#region 冻结.
	/// <summary>
	/// 执行冻结.
	/// </summary>
	/// <returns></returns>
	public String Flow_DoFix() throws Exception {
		return bp.wf.Dev2Interface.Flow_DoFix(this.getWorkID(), false, this.GetRequestVal("Msg"));
	}
	/// <summary>
	/// 执行解冻.
	/// </summary>
	/// <returns></returns>
	public String Flow_DoUnFix() throws Exception {
		return bp.wf.Dev2Interface.Flow_DoUnFix(this.getWorkID(),  this.GetRequestVal("Msg"));
	}
	/// <summary>
	/// 获得冻结列表.
	/// </summary>
	/// <returns></returns>
	public String DB_Fix() throws Exception {
		GenerWorkFlows gwfs = bp.wf.Dev2Interface.DB_Fix();
		return bp.tools.Json.ToJson(gwfs.ToDataTableField());
	}
	//#endregion 冻结
	///#region 公文模板处理.

	/**
	 * 开始节点选择公文模板
	 * @return 返回模板列表或错误信息
	 */
	public String GPN_DocTemplates()
	{
		try
		{
			String flowNo = GetRequestVal("flowNo");
			String nodeId = GetRequestVal("nodeID");
			String workID = GetRequestVal("workID");
			if (DataType.IsNullOrEmpty(flowNo) || DataType.IsNullOrEmpty(nodeId) || DataType.IsNullOrEmpty(workID))
			{
				return "err@参数不能为空";
			}
			DataTable dt = new DataTable();
			dt.Columns.Add("Name");
			dt.Columns.Add("No");

			String frmVSTOFilePath = bp.difference.SystemConfig.getPathOfDataUser() + "DocFlow/";
			File flowDir = new File(frmVSTOFilePath);

			if (!flowDir.exists() || !flowDir.isDirectory())
			{
				return "err@未找到模板";
			}
			File[] files = flowDir.listFiles();
			if (files != null) {
				for (File file : files) {
					//获取文件后缀
					String extension = getExtension(file.getName());
					//只选择word类型=文件
					if ("docx".equals(extension) || "doc".equals(extension)) {
						String fullName = file.getAbsolutePath();
						int index = fullName.indexOf("DocFlow");
						String filePath = fullName.substring(index + 7);
						DataRow drNew = dt.NewRow();
						drNew.setValue("No", URLEncoder.encode(filePath, "utf-8").toLowerCase());
						drNew.setValue("Name", URLEncoder.encode(file.getName(), "utf-8").toLowerCase());
						dt.Rows.add(drNew);
					}
				}
			}
			return bp.tools.Json.ToJson(dt);
		}
		catch (Exception ex)
		{
			Log.DebugWriteError(ex);
			return ex.getMessage();
		}
	}

	private String getExtension(String fileName) {
		int lastIndexOfDot = fileName.lastIndexOf('.');
		if (lastIndexOfDot > -1 && lastIndexOfDot < fileName.length() - 1) {
			return fileName.substring(lastIndexOfDot + 1);
		}
		return "";
	}

	/**
	 * 判断公文是否存在
	 * @return
	 */
	public String IsGongWenByteExist()
	{
		try
		{
			String nodeId = GetRequestVal("nodeID");
			String workID = GetRequestVal("workID");
			if (DataType.IsNullOrEmpty(nodeId) || DataType.IsNullOrEmpty(workID))
			{
				return "err@参数不能为空";
			}
			//生成文件流
			byte[] bytes = null;
			Node nd = new Node(nodeId);
			Work wk = nd.getHisWork();
			wk.SetValByKey("OID", Integer.parseInt(workID));
			wk.RetrieveFromDBSources();

			bytes = wk.GetFileFromDB("DBFile", "");
			if (bytes == null)
			{
				return "公文文件不存在";
			}
			return "公文文件存在";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	///#endregion 公文模板处理.
	/**
	 最新的消息

	 @return
	 */
	public final String GenerMsg_Init()
	{
		//生成消息的ID.
		String sql = "SELECT RDT FROM WF_GenerWorkerlist WHERE IsPass=0 AND FK_Flow !='' AND FK_Emp='" + WebUser.getNo() + "' ORDER BY RDT DESC ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		Hashtable ht = new Hashtable();
		if (dt.Rows.size() == 0)
		{
			ht.put("ID", 0);
		}
		else
		{
			ht.put("ID", dt.Rows.get(0).getValue(0).toString());
		}

		ht.put("Num", dt.Rows.size());
		return bp.tools.Json.ToJson(ht);
	}
	/**
	 * 执行定时触发
	 *
	 * @return
	 * @throws Exception
	 */
	public final String ccbpmServices() throws Exception {
		//自动节点.
		AutoRun_WhoExeIt dts = new AutoRun_WhoExeIt();
		Object tempVar = dts.Do();
		String msg1 = tempVar instanceof String ? (String)tempVar : null;

		//发起流程.
		AutoRunStratFlows en = new AutoRunStratFlows();
		Object tempVar2 = en.Do();
		String msg = tempVar2 instanceof String ? (String)tempVar2 : null;


		ccbpmServices dts3 = new ccbpmServices();
		Object tempVar3 = dts3.Do();
		String msg3 = tempVar3 instanceof String ? (String)tempVar3 : null;

		return "执行完成。 <br>" + msg + "<br>" + msg1 + " <br>" + msg3;
	}

	/**
	 删除子线程

	 @return
	 */
	public final String ThreadDtl_DelSubThread() throws Exception {
		Dev2Interface.Flow_DeleteSubThread(this.getWorkID(), "手工删除");
		//删除已经发向合流点的汇总数据.
		int fNodeID = this.GetRequestValInt("FNodeID");
		if (fNodeID != 0)
		{
			Node nd = new Node(fNodeID);
			MapDtls dtls = new MapDtls(nd.getNodeFrmID());
			for(MapDtl dtl : dtls.ToJavaList())
			{
				/*如果是合流数据*/
				if (dtl.getItIsHLDtl())
					DBAccess.RunSQL("DELETE FROM " + dtl.getPTable() + " WHERE OID=" + this.getWorkID());
			}
		}
		return "删除成功";
	}



	///#region 打印 rtf
	/**
	 初始化

	 @return
	 */
	public final String PrintDoc_Init() throws Exception {
		String billNo = this.GetRequestVal("FK_Bill");
		FrmPrintTemplate templete = new FrmPrintTemplate();
		if (DataType.IsNullOrEmpty(billNo) == false)
		{
			templete.setMyPK(billNo);
			int count = templete.RetrieveFromDBSources();
			if (count == 0)
			{
				return "err@表单模板的编号[" + billNo + "]数据不存在,请查看设计是否正确";
			}
		}
		String frmID = this.getFrmID();
		Node nd = null;
		if (DataType.IsNullOrEmpty(frmID) == true && (this.getNodeID() != 0 && this.getNodeID() != 9999))
		{
			nd = new Node(this.getNodeID());
			if (nd.getHisFormType() == NodeFormType.SDKForm || nd.getHisFormType() == NodeFormType.SelfForm)
			{
				return "err@SDK表单、嵌入式表单暂时不支持打印功能";
			}

			if (nd.getHisFormType() == NodeFormType.SheetTree)
			{
				//获取该节点绑定的表单
				// 所有表单集合.
				MapDatas mds = new MapDatas();
				mds.RetrieveInSQL("SELECT FK_Frm FROM WF_FrmNode WHERE FK_Node=" + this.getNodeID() + " AND FrmEnableRole !=5");
				return "info@" + bp.tools.Json.ToJson(mds.ToDataTableField("dt"));
			}
			frmID = "ND" + this.getNodeID();
			if (nd.getHisFormType() == NodeFormType.FoolForm && DataType.IsNullOrEmpty(frmID) == false)
			{
				FrmPrintTemplates templetes = new FrmPrintTemplates();
				if(nd.getHisFlow().getFlowDevModel()==FlowDevModel.JiJian)
					templetes.Retrieve(FrmPrintTemplateAttr.FrmID, "ND"+Integer.parseInt(this.getFlowNo())+"01", null);
				else
					templetes.Retrieve(FrmPrintTemplateAttr.FrmID, frmID, null);
				if (templetes.size() == 0)
				{
					return "err@当前节点上没有绑定单据模板。";
				}
				if (templetes.size() > 1)
				{
					return templetes.ToJson("dt");
				}
				templete = templetes.get(0) instanceof FrmPrintTemplate ? (FrmPrintTemplate)templetes.get(0) : null;
			}
			if (nd.getHisFormType() == NodeFormType.RefOneFrmTree || nd.getHisFlow().getFlowDevModel() == FlowDevModel.JiJian)
			{
				frmID = nd.getNodeFrmID();
				MapData md = new MapData(frmID);
				FrmPrintTemplates templetes = new FrmPrintTemplates();
				if (md.getHisFrmType() == FrmType.ChapterFrm)
				{
					//如果是章节表单，需要获取当前表单上关联的表单
					String sql = "SELECT CtrlID From Sys_GroupField Where CtrlType='ChapterFrmLinkFrm' AND FrmID='" + frmID + "'";
					DataTable dt = DBAccess.RunSQLReturnTable(sql);
					String val = "'" + frmID + "'";
					for (DataRow dr : dt.Rows)
					{
						val += ",'" + dr.getValue(0) + "'";
					}


					templetes.RetrieveIn(FrmPrintTemplateAttr.FrmID, val, null);
				}
				else
				{
					templetes.Retrieve(FrmPrintTemplateAttr.FrmID, frmID, null);
				}
				if (templetes.size() == 0)
				{
					return "err@当前节点上没有绑定单据模板。";
				}
				if (templetes.size() > 1)
				{
					return templetes.ToJson("dt");
				}
				templete = templetes.get(0) instanceof FrmPrintTemplate ? (FrmPrintTemplate)templetes.get(0) : null;
			}
		}
		//单据的打印
		if (DataType.IsNullOrEmpty(templete.getMyPK()) == true) {
			FrmPrintTemplates templetes = new FrmPrintTemplates();
			templetes.Retrieve(FrmPrintTemplateAttr.FrmID, frmID);
			if (templetes.size() == 0)
				return "err@当前节点上没有绑定单据模板。";
			templete = (FrmPrintTemplate) templetes.get(0);
		}

		String sourceType = this.GetRequestVal("SourceType");
		if (DataType.IsNullOrEmpty(sourceType) == false && sourceType.equals("Bill"))
		{
			return PrintDoc_FormDoneIt(null, this.getWorkID(), this.getFID(), frmID, templete);
		}
		if (DataType.IsNullOrEmpty(sourceType) == false && sourceType.equals("EntityNoName"))
		{
			return PrintDoc_EntityNoNameDoneIt(this.getNo(), frmID, templete);
		}
		if (nd != null && nd.getHisFormType() == NodeFormType.RefOneFrmTree)
		{
			return PrintDoc_FormDoneIt(nd, this.getWorkID(), this.getFID(), templete.getFrmID(), templete);
		}
		return PrintDoc_DoneIt(templete.getMyPK());

	}
	/**
	 执行打印

	 @return
	 */
	public final String PrintDoc_Done() throws Exception {
		String FrmPrintTemplateNo = this.GetRequestVal("FK_Bill");
		return PrintDoc_DoneIt(FrmPrintTemplateNo);
	}

	/**
	 打印pdf.
	 @return
	 */

	public final String PrintDoc_DoneIt() throws Exception {
		return PrintDoc_DoneIt(null);
	}

	public final String PrintDoc_DoneIt(String FrmPrintTemplateNo) throws Exception {
		Node nd = new Node(this.getNodeID());

		if (FrmPrintTemplateNo == null)
		{
			FrmPrintTemplateNo = this.GetRequestVal("FK_Bill");
		}

		FrmPrintTemplate func = new FrmPrintTemplate(FrmPrintTemplateNo);

		//如果不是 FrmPrintTemplateExcel 打印.
		if (func.getTemplateFileModel() == TemplateFileModel.VSTOForExcel)
		{
			return "url@httpccword://-fromccflow,App=FrmPrintTemplateExcel,TemplateNo=" + func.getMyPK() + ",WorkID=" + this.getWorkID() + ",FK_Flow=" + this.getFlowNo() + ",FK_Node=" + this.getNodeID() + ",UserNo=" + WebUser.getNo() + ",Token=" + WebUser.getToken();
		}

		//如果不是 FrmPrintTemplateWord 打印
		if (func.getTemplateFileModel() == TemplateFileModel.VSTOForWord)
		{
			return "url@httpccword://-fromccflow,App=FrmPrintTemplateWord,TemplateNo=" + func.getMyPK() + ",WorkID=" + this.getWorkID() + ",FK_Flow=" + this.getFlowNo() + ",FK_Node=" + this.getNodeID() + ",UserNo=" + WebUser.getNo() + ",Token=" + WebUser.getToken();
		}
		if (func.getTemplateFileModel() == TemplateFileModel.WPS)
		{
			return PrintDoc_WpsWord(nd, this.getWorkID(), this.getFID(), func.getFrmID(), func);
		}


		String billInfo = "";

		String ccformId = this.GetRequestVal("CCFormID");
		if (DataType.IsNullOrEmpty(ccformId) == false)
		{
			return PrintDoc_FormDoneIt(nd, this.getWorkID(), this.getFID(), ccformId, func);
		}

		Work wk = nd.getHisWork();
		wk.setOID(this.getWorkID());
		wk.RetrieveFromDBSources();

		String file = LocalDateTime.now().getYear() + "_" + WebUser.getDeptNo() + "_" + func.getMyPK() + "_" + getWorkID() + ".doc";
		bp.pub.RTFEngine rtf = new bp.pub.RTFEngine();

		String[] paths;
		String path;
		long newWorkID = 0;
		try
		{
			///#region 生成单据
			rtf.getHisEns().clear();
			rtf.getEnsDataDtls().clear();
			if (this.getNodeID() != 0 )
			{
				//把流程主表数据放入里面去.
				GEEntity ndxxRpt = new GEEntity("ND" + Integer.parseInt(nd.getFlowNo()) + "Rpt");
				try
				{
					ndxxRpt.setPKVal(this.getWorkID());
					ndxxRpt.Retrieve();

					newWorkID = this.getWorkID();
				}
				catch (RuntimeException ex)
				{
					if (getFID() > 0)
					{
						ndxxRpt.setPKVal(this.getFID());
						ndxxRpt.Retrieve();

						newWorkID = this.getFID();

						wk = null;
						wk = nd.getHisWork();
						wk.setOID(this.getWorkID());
						wk.RetrieveFromDBSources();
					}
					else
					{
						path = Glo.getFlowFileBill() + LocalDateTime.now().getYear() + "/" + WebUser.getDeptNo() + "/" + func.getMyPK() + "/";
						String msgErr = "@" + String.format("生成单据失败，请让管理员检查目录设置") + "[" + Glo.getFlowFileBill() + "]。@Err：" + ex.getMessage() + " @File=" + file + " @Path:" + path;
						billInfo += "@<font color=red>" + msgErr + "</font>";
						throw new RuntimeException(msgErr + "@其它信息:" + ex.getMessage());
					}
				}
				ndxxRpt.Copy(wk);

				//把数据赋值给wk. 有可能用户还没有执行流程检查，字段没有同步到 NDxxxRpt.
				if (ndxxRpt.getRow().size()> wk.getRow().size())
				{
					wk.setRow(ndxxRpt.getRow());
				}

				rtf.HisGEEntity = wk;

				//加入他的明细表.
				ArrayList<Entities> al = wk.GetDtlsDatasOfList(null);
				for (Entities ens : al)
				{
					rtf.AddDtlEns(ens);
				}

				//增加多附件数据
				FrmAttachments aths = wk.getHisFrmAttachments();
				for (FrmAttachment athDesc : aths.ToJavaList())
				{
					FrmAttachmentDBs athDBs = new FrmAttachmentDBs();
					if (athDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, athDesc.getMyPK(), FrmAttachmentDBAttr.RefPKVal, newWorkID, "RDT") == 0)
					{
						continue;
					}

					rtf.getEnsDataAths().put(athDesc.getNoOfObj(), athDBs);
				}

				//把审核日志表加入里面去.
				Paras ps = new Paras();
				String trackTable = "ND" + Integer.parseInt(nd.getFlowNo()) + "Track";
				boolean isHaveWriteDB = false;
				if (DBAccess.IsExitsTableCol(trackTable, "WriteDB") == true)
				{
					isHaveWriteDB = true;
				}
				if (isHaveWriteDB == true)
				{
					ps.SQL = "SELECT MyPK,NDFrom,ActionType,EmpFrom,EmpFromT,RDT,Msg,WriteDB FROM " + trackTable + " WHERE ActionType=" + SystemConfig.getAppCenterDBVarStr() + "ActionType AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID";
				}
				else
				{
					ps.SQL = "SELECT MyPK,NDFrom,ActionType,EmpFrom,EmpFromT,RDT,Msg FROM " + trackTable + " WHERE ActionType=" + SystemConfig.getAppCenterDBVarStr() + "ActionType AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID";
				}
				ps.Add(TrackAttr.ActionType, ActionType.WorkCheck.getValue());
				ps.Add(TrackAttr.WorkID, newWorkID);
				DataTable dt = DBAccess.RunSQLReturnTable(ps);
				if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
				{
					dt.Columns.get(0).ColumnName = "MyPK";
					dt.Columns.get(1).ColumnName = "NDFrom";
					dt.Columns.get(2).ColumnName = "ActionType";
					dt.Columns.get(3).ColumnName = "EmpFrom";
					dt.Columns.get(4).ColumnName = "EmpFromT";
					dt.Columns.get(5).ColumnName = "RDT";
					dt.Columns.get(6).ColumnName = "Msg";
					if (isHaveWriteDB == true)
					{
						dt.Columns.get(7).ColumnName = "WriteDB";
					}
				}

				if (DBAccess.IsExitsTableCol(trackTable, "WriteDB") == true)
				{
					for (DataRow dr : dt.Rows)
					{
						dr.setValue("WriteDB", DBAccess.GetBigTextFromDB(trackTable, "MyPK", dr.getValue(0).toString(), "WriteDB"));
					}
				}

				rtf.dtTrack = dt;

				//获取启用审核组件的节点
				ps = new Paras();
				ps.SQL = "SELECT NodeID FROM WF_Node WHERE FWCSta=1 AND FK_Flow=" + SystemConfig.getAppCenterDBVarStr() + "FK_Flow";
				ps.Add(NodeAttr.FK_Flow, nd.getFlowNo());
				rtf.wks = DBAccess.RunSQLReturnTable(ps);
			}

			paths = file.split("[_]", -1);
			path = paths[0] + "/" + paths[1] + "/";
			String fileModelT = "rtf";
			if (func.getTemplateFileModel().getValue() == 1)
			{
				fileModelT = "word";
			}
			String billUrl = "file@" + fileModelT + "@" + Glo.getCCFlowAppPath() + "DataUser/Bill/" + path + "/"+func.getMyPK()+"/"+file;

			if (func.getHisPrintFileType() == PrintFileType.PDF)
			{
				billUrl = billUrl.replace(".doc", ".pdf");
			}

			path = Glo.getFlowFileBill() + LocalDateTime.now().getYear() + "/" + WebUser.getDeptNo() + "/" + func.getMyPK() + "/";
			if ((new File(path)).isDirectory() == false)
			{
				(new File(path)).mkdirs();
			}

			String tempFile = func.getTempFilePath();
			if (tempFile.contains(".rtf") == false)
			{
				tempFile = tempFile + ".rtf";
			}

			//用于扫描打印.
			String qrUrl = SystemConfig.getHostURL() + "WF/WorkOpt/PrintDocQRGuide.htm?MyPK=" + func.getMyPK();
			rtf.MakeDoc(tempFile, path, file, qrUrl);

			///#endregion


			///#region 转化成pdf.
			if (func.getHisPrintFileType() == PrintFileType.PDF)
			{
				String rtfPath = path + file;
				String pdfPath = rtfPath.replace(".doc", ".pdf");
				Word2PdfUtil.wordConvertPdfFile(rtfPath,pdfPath);
				return  "file@pdf@"+pdfPath.substring(pdfPath.indexOf("DataUser"));

			}

			///#endregion

			//在线WebOffice打开
			if (func.getPrintOpenModel() == PrintOpenModel.WebOffice)
			{
				return "err@【/WF/WebOffice/PrintOffice.htm】该文件没有重构好,您可以找到旧版本解决，或者自己开发。";
			}
			return billUrl;
		}
		catch (RuntimeException ex)
		{
			path = Glo.getFlowFileBill() + LocalDateTime.now().getYear() + "/" + WebUser.getDeptNo() + "/" + func.getMyPK() + "/";
			String msgErr = "@" + String.format("生成单据失败，请让管理员检查目录设置") + "[" + Glo.getFlowFileBill() + "]。@Err：" + ex.getMessage() + " @File=" + file + " @Path:" + path;
			return "err@<font color=red>" + msgErr + "</font>" + ex.getMessage();
		}
	}

	///#endregion

	public final String PrintDoc_FormDoneIt(Node nd, long workID, long fid, String formID, FrmPrintTemplate func) throws Exception {
		long pkval = workID;
		Work wk = null;
		String billInfo = "";
		MapData mapData = new MapData(formID);
		if (nd != null)
		{
			FrmNode fn = new FrmNode(nd.getNodeID(), formID);
			//先判断解决方案
			if (fn != null && fn.getWhoIsPK() != WhoIsPK.OID)
			{
				if (fn.getWhoIsPK() == WhoIsPK.PWorkID)
				{
					pkval = this.getPWorkID();
				}
				if (fn.getWhoIsPK() == WhoIsPK.FID)
				{
					pkval = fid;
				}
			}
			wk = nd.getHisWork();
			wk.setOID(pkval);
			wk.RetrieveFromDBSources();
		}
		if (mapData.getHisFrmType() == FrmType.ChapterFrm)
		{
			return PrintDoc_ChapterFormDoneIt(nd, workID, pkval, formID, func, wk, mapData);
		}
		String file = LocalDateTime.now().getYear() + "_" + WebUser.getDeptNo() + "_" + func.getMyPK() + "_" + getWorkID() + ".doc";
		String atPara = DBAccess.RunSQLReturnString("SELECT TempalteFile FROM Sys_FrmPrintTemplate WHERE MyPK='" + func.getMyPK() + "'");
		AtPara ap = new AtPara(atPara);
		String tempFile = ap.GetValStrByKey("FileName");
		if ( DataType.IsNullOrEmpty(tempFile) != true)
		{
			if (tempFile.contains(".rtf") == false)
				tempFile = tempFile + ".rtf";
		}
		else{
			tempFile = func.getTempFilePath();
			if (tempFile.contains(".rtf") == false)
				tempFile = tempFile + ".rtf";
		}
		bp.pub.RTFEngine rtf = new bp.pub.RTFEngine(tempFile);

		String[] paths;
		String path;
		long newWorkID = 0;
		try
		{

			///#region 生成单据
			rtf.getHisEns().clear();
			rtf.getEnsDataDtls().clear();
			if (DataType.IsNullOrEmpty(func.getFrmID()) == false)
			{
				//把流程主表数据放入里面去.
				GEEntity ndxxRpt = new GEEntity(formID);
				try
				{
					ndxxRpt.setPKVal(pkval);
					ndxxRpt.Retrieve();

					newWorkID = pkval;
				}
				catch (RuntimeException ex)
				{
					if (getFID() > 0)
					{
						ndxxRpt.setPKVal(this.getFID());
						ndxxRpt.Retrieve();

						newWorkID = this.getFID();

						wk = null;
						wk = nd.getHisWork();
						wk.setOID(this.getWorkID());
						wk.RetrieveFromDBSources();
					}
					else
					{
						path = Glo.getFlowFileBill() + LocalDateTime.now().getYear() + "/" + WebUser.getDeptNo() + "/" + func.getMyPK() + "/";
						String msgErr = "@" + String.format("生成单据失败，请让管理员检查目录设置") + "[" + Glo.getFlowFileBill() + "]。@Err：" + ex.getMessage() + " @File=" + file + " @Path:" + path;
						billInfo += "@<font color=red>" + msgErr + "</font>";
						throw new RuntimeException(msgErr + "@其它信息:" + ex.getMessage());
					}
				}
				//ndxxRpt.Copy(wk);

				//把数据赋值给wk. 有可能用户还没有执行流程检查，字段没有同步到 NDxxxRpt.
				//if (ndxxRpt.Row.size()> wk.Row.size())
				//   wk.Row = ndxxRpt.Row;

				rtf.HisGEEntity = ndxxRpt;

				//加入他的明细表.
				ArrayList<Entities> al = new ArrayList<Entities>();
				MapDtls mapdtls = mapData.getMapDtls();
				mapData.getEnMap().setDtls(null);
				for (MapDtl dtl : mapdtls.ToJavaList())
				{
					GEDtls dtls1 = new GEDtls(dtl.getNo());
					mapData.getEnMap().AddDtl(dtls1, "RefPK");
				}
				al = mapData.GetDtlsDatasOfList(String.valueOf(pkval));

				for (Entities ens : al)
				{
					rtf.AddDtlEns(ens);
				}

				//增加多附件数据
				FrmAttachments aths = mapData.getFrmAttachments();
				for (FrmAttachment athDesc : aths.ToJavaList())
				{
					FrmAttachmentDBs athDBs = new FrmAttachmentDBs();
					if (athDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, athDesc.getMyPK(), FrmAttachmentDBAttr.RefPKVal, newWorkID, "RDT") == 0)
					{
						continue;
					}

					rtf.getEnsDataAths().put(athDesc.getNoOfObj(), athDBs);
				}

				if (nd != null)
				{
					//把审核日志表加入里面去.
					Paras ps = new Paras();
					String trackTable = "ND" + Integer.parseInt(nd.getFlowNo()) + "Track";
					boolean isHaveWriteDB = false;
					if (DBAccess.IsExitsTableCol(trackTable, "WriteDB") == true)
					{
						isHaveWriteDB = true;
					}
					if (isHaveWriteDB == true)
					{
						ps.SQL = "SELECT MyPK,NDFrom,ActionType,EmpFrom,EmpFromT,RDT,Msg,WriteDB FROM " + trackTable + " WHERE ActionType=" + SystemConfig.getAppCenterDBVarStr() + "ActionType AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID";
					}
					else
					{
						ps.SQL = "SELECT MyPK,NDFrom,ActionType,EmpFrom,EmpFromT,RDT,Msg FROM " + trackTable + " WHERE ActionType=" + SystemConfig.getAppCenterDBVarStr() + "ActionType AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID";
					}
					ps.Add(TrackAttr.ActionType, ActionType.WorkCheck.getValue());
					ps.Add(TrackAttr.WorkID, newWorkID);
					DataTable dt = DBAccess.RunSQLReturnTable(ps);
					if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
					{
						dt.Columns.get(0).ColumnName = "MyPK";
						dt.Columns.get(1).ColumnName = "NDFrom";
						dt.Columns.get(2).ColumnName = "ActionType";
						dt.Columns.get(3).ColumnName = "EmpFrom";
						dt.Columns.get(4).ColumnName = "EmpFromT";
						dt.Columns.get(5).ColumnName = "RDT";
						dt.Columns.get(6).ColumnName = "Msg";
						if (isHaveWriteDB == true)
						{
							dt.Columns.get(8).ColumnName = "WriteDB";
						}
					}

					if (DBAccess.IsExitsTableCol(trackTable, "WriteDB") == true)
					{
						for (DataRow dr : dt.Rows)
						{
							dr.setValue("WriteDB", DBAccess.GetBigTextFromDB(trackTable, "MyPK", dr.getValue(0).toString(), "WriteDB"));
						}
					}

					rtf.dtTrack = dt;

					//获取启用审核组件的节点
					ps = new Paras();
					ps.SQL = "SELECT NodeID FROM WF_Node WHERE FWCSta=1 AND FK_Flow=" + SystemConfig.getAppCenterDBVarStr() + "FK_Flow";
					ps.Add(NodeAttr.FK_Flow, nd.getFlowNo());
					rtf.wks = DBAccess.RunSQLReturnTable(ps);
				}
			}

			paths = file.split("[_]", -1);
			path = LocalDateTime.now().getYear() + "/" + WebUser.getDeptNo() + "/" + func.getMyPK() + "/";
			String fileModelT = "rtf";
			if (func.getTemplateFileModel().getValue() == 1)
			{
				fileModelT = "word";
			}

			String billUrl = "file@" + fileModelT + "@" + Glo.getCCFlowAppPath() + "DataUser/Bill/" + path + file;

			if (func.getHisPrintFileType() == PrintFileType.PDF)
			{
				billUrl = billUrl.replace(".doc", ".pdf");
			}

			path = Glo.getFlowFileBill() + LocalDateTime.now().getYear() + "/" + WebUser.getDeptNo() + "/" + func.getMyPK() + "/";
			//  path = Server.MapPath(path);
			if ((new File(path)).isDirectory() == false)
			{
				(new File(path)).mkdirs();
			}

			/*String atPara = DBAccess.RunSQLReturnString("SELECT TempalteFile FROM Sys_FrmPrintTemplate WHERE MyPK='" + func.getMyPK() + "'");
			AtPara ap = new AtPara(atPara);
//			String tempFile = ap.GetValStrByKey("FileName");
			if (tempFile.contains(".rtf") == false)
			{
				tempFile = tempFile + ".rtf";
			}
*/
			//用于扫描打印.
			String qrUrl = SystemConfig.getHostURL() + "/WF/WorkOpt/PrintDocQRGuide.htm?MyPK=" + func.getMyPK();
			rtf.MakeDoc(tempFile, path, file, qrUrl);

			///#endregion


			///#region 转化成pdf.
			if (func.getHisPrintFileType() == PrintFileType.PDF)
			{
				String rtfPath = path + file;
				String pdfPath = rtfPath.replace(".doc", ".pdf");
				Word2PdfUtil.wordConvertPdfFile(rtfPath,pdfPath);
				return  "file@pdf@"+pdfPath.substring(pdfPath.indexOf("DataUser"));
			}

			///#endregion

			//在线WebOffice打开
			if (func.getPrintOpenModel() == PrintOpenModel.WebOffice)
			{
				return "err@【/WF/WebOffice/PrintOffice.htm】该文件没有重构好,您可以找到旧版本解决，或者自己开发。";
			}
			return billUrl;
		}
		catch (RuntimeException ex)
		{
			path = Glo.getFlowFileBill() + LocalDateTime.now().getYear() + "/" + WebUser.getDeptNo() + "/" + func.getMyPK() + "/";
			String msgErr = "@" + String.format("生成单据失败，请让管理员检查目录设置") + "[" + Glo.getFlowFileBill() + "]。err@" + ex.getMessage() + " @File=" + file + " @Path:" + path;
			return "err@<font color=red>" + msgErr + "</font>" + ex.getMessage();
		}
	}

	public final String PrintDoc_EntityNoNameDoneIt(String pkval, String formID, FrmPrintTemplate func) throws Exception {
		GEEntityNoName en = new GEEntityNoName(formID,pkval);
		String billInfo = "";
		MapData mapData = new MapData(formID);

		String file = LocalDateTime.now().getYear() + "_" + WebUser.getDeptNo() + "_" + func.getMyPK() + "_" + pkval + ".doc";
		String atPara = DBAccess.RunSQLReturnString("SELECT TempalteFile FROM Sys_FrmPrintTemplate WHERE MyPK='" + func.getMyPK() + "'");
		AtPara ap = new AtPara(atPara);
		String tempFile = ap.GetValStrByKey("FileName");
		if ( DataType.IsNullOrEmpty(tempFile) != true)
		{
			if (tempFile.contains(".rtf") == false)
				tempFile = tempFile + ".rtf";
		}
		else{
			tempFile = func.getTempFilePath();
			if (tempFile.contains(".rtf") == false)
				tempFile = tempFile + ".rtf";
		}
		bp.pub.RTFEngine rtf = new bp.pub.RTFEngine(tempFile);

		String[] paths;
		String path;
		try
		{

			///#region 生成单据
			rtf.getHisEns().clear();
			rtf.getEnsDataDtls().clear();
			rtf.HisGEEntity = en;

			//加入他的明细表.
			ArrayList<Entities> al = new ArrayList<Entities>();
			MapDtls mapdtls = mapData.getMapDtls();
			mapData.getEnMap().setDtls(null);
			for (MapDtl dtl : mapdtls.ToJavaList())
			{
				GEDtls dtls1 = new GEDtls(dtl.getNo());
				mapData.getEnMap().AddDtl(dtls1, "RefPK");
			}
			al = mapData.GetDtlsDatasOfList(String.valueOf(pkval));

			for (Entities ens : al)
			{
				rtf.AddDtlEns(ens);
			}

			//增加多附件数据
			FrmAttachments aths = mapData.getFrmAttachments();
			for (FrmAttachment athDesc : aths.ToJavaList())
			{
				FrmAttachmentDBs athDBs = new FrmAttachmentDBs();
				if (athDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, athDesc.getMyPK(), FrmAttachmentDBAttr.RefPKVal, pkval, "RDT") == 0)
				{
					continue;
				}

				rtf.getEnsDataAths().put(athDesc.getNoOfObj(), athDBs);
			}

			paths = file.split("[_]", -1);
			path = LocalDateTime.now().getYear() + "/" + WebUser.getDeptNo() + "/" + func.getMyPK() + "/";
			String fileModelT = "rtf";
			if (func.getTemplateFileModel().getValue() == 1)
			{
				fileModelT = "word";
			}

			String billUrl = "file@" + fileModelT + "@" + Glo.getCCFlowAppPath() + "DataUser/Bill/" + path + file;

			if (func.getHisPrintFileType() == PrintFileType.PDF)
			{
				billUrl = billUrl.replace(".doc", ".pdf");
			}
			path = Glo.getFlowFileBill() + LocalDateTime.now().getYear() + "/" + WebUser.getDeptNo() + "/" + func.getMyPK() + "/";
			if ((new File(path)).isDirectory() == false)
			{
				(new File(path)).mkdirs();
			}

			//用于扫描打印.
			String qrUrl = SystemConfig.getHostURL() + "/WF/WorkOpt/PrintDocQRGuide.htm?MyPK=" + func.getMyPK();
			rtf.MakeDoc(tempFile, path, file, qrUrl);

			if (func.getHisPrintFileType() == PrintFileType.PDF)
			{
				String rtfPath = path + file;
				String pdfPath = rtfPath.replace(".doc", ".pdf");
				Word2PdfUtil.wordConvertPdfFile(rtfPath,pdfPath);
				return  "file@pdf@"+pdfPath.substring(pdfPath.indexOf("DataUser"));
			}

			if (func.getPrintOpenModel() == PrintOpenModel.WebOffice)
			{
				return "err@【/WF/WebOffice/PrintOffice.htm】该文件没有重构好,您可以找到旧版本解决，或者自己开发。";
			}
			return billUrl;
		}
		catch (RuntimeException ex)
		{
			path = Glo.getFlowFileBill() + LocalDateTime.now().getYear() + "/" + WebUser.getDeptNo() + "/" + func.getMyPK() + "/";
			String msgErr = "@" + String.format("生成单据失败，请让管理员检查目录设置") + "[" + Glo.getFlowFileBill() + "]。err@" + ex.getMessage() + " @File=" + file + " @Path:" + path;
			return "err@<font color=red>" + msgErr + "</font>" + ex.getMessage();
		}
	}
	public final String PrintDoc_ChapterFormDoneIt(Node nd, long workID, long pkval, String formID, FrmPrintTemplate func, Work wk, MapData mapData) throws Exception {
		String billInfo = "";
		String file = LocalDateTime.now().getYear() + "_" + WebUser.getDeptNo() + "_" + func.getMyPK() + "_" + getWorkID() + ".doc";
		String atPara = DBAccess.RunSQLReturnString("SELECT TempalteFile FROM Sys_FrmPrintTemplate WHERE MyPK='" + func.getMyPK() + "'");
		AtPara ap = new AtPara(atPara);
		String tempFile = ap.GetValStrByKey("FileName");
		if ( DataType.IsNullOrEmpty(tempFile) != true)
		{
			if (tempFile.contains(".rtf") == false)
				tempFile = tempFile + ".rtf";
		}
		else{
			tempFile = func.getTempFilePath();
			if (tempFile.contains(".rtf") == false)
				tempFile = tempFile + ".rtf";
		}
		bp.pub.RTFEngine rtf = new bp.pub.RTFEngine(tempFile);
		String rtfStr = rtf._rtfStr;
		String hidegroup = wk.GetParaString("HideGroup", "");
		String hideAttrs = wk.GetParaString("HideAttrs", "");
		//章节表单且根据当前流程筛选的可显示的章节打印输出
		GroupFields gfs = mapData.getGroupFields();
		MapAttrs mapAttrs = mapData.getMapAttrs();
		hidegroup = hidegroup + ",";
		hideAttrs = hideAttrs + ",";
		//按照分组处理
		//递归层数
		int recursionCount = 1;
		int idx = 0;
		HashMap<Integer, String> numberMap = new HashMap<Integer, String>();
		numberMap.put(1, "一");
		numberMap.put(2, "二");
		numberMap.put(3, "三");
		numberMap.put(4, "四");
		numberMap.put(5, "五");
		numberMap.put(6, "六");
		numberMap.put(7, "七");
		numberMap.put(8, "八");
		numberMap.put(9, "九");
		numberMap.put(10, "十");
		numberMap.put(11, "十一");
		numberMap.put(12, "十二");
		numberMap.put(13, "十三");
		for (GroupField gf : gfs.ToJavaList())
		{
			if (DataType.IsNullOrEmpty(gf.GetValStringByKey("ParentOID")) == true)
			{
				if (hidegroup.contains(gf.getOID() + ",") == false)
				{
					if (gf.getLab().equals("封面") == false)
					{
						idx++;
					}
					if (rtfStr.contains("<Group." + gf.getOID() + ">") == true)
					{
						rtfStr = rtfStr.replace("<Group." + gf.getOID() + ">", String.valueOf(idx) + rtf.GetCode(gf.getLab()));
						//递归获取下面的子级
						rtfStr = GetChildNodes(gf.getOID(), gfs, rtfStr, hidegroup, String.valueOf(idx), rtf);
					}
				}
				else
				{
					if (rtfStr.contains("<Group." + gf.getOID() + ">") == true)
					{
						rtfStr = GetRtfStr(rtfStr, "<Group." + gf.getOID() + ">");
					}
					//递归获取下面的子级
					rtfStr = GetChildNodes(gf.getOID(), gfs, rtfStr, hidegroup, String.valueOf(idx), rtf);
				}
			}
		}
		rtfStr = GetAttrChildNodes(mapAttrs, rtfStr, hideAttrs, String.valueOf(idx), rtf);
		rtf._rtfStr = rtfStr;
		String[] paths;
		String path;
		long newWorkID = 0;
		try
		{

			///#region 生成单据
			rtf.getHisEns().clear();
			rtf.getEnsDataDtls().clear();
			if (DataType.IsNullOrEmpty(func.getFrmID()) == false)
			{
				//把流程主表数据放入里面去.
				GEEntity ndxxRpt = new GEEntity(formID);
				try
				{
					ndxxRpt.setPKVal(pkval);
					ndxxRpt.Retrieve();
					newWorkID = pkval;
				}
				catch (RuntimeException ex)
				{
					if (getFID() > 0)
					{
						ndxxRpt.setPKVal(this.getFID());
						ndxxRpt.Retrieve();
						newWorkID = this.getFID();

						wk = null;
						wk = nd.getHisWork();
						wk.setOID(this.getWorkID());
						wk.RetrieveFromDBSources();
					}
					else
					{
						path = Glo.getFlowFileBill() + LocalDateTime.now().getYear() + "/" + WebUser.getDeptNo() + "/" + func.getMyPK() + "/";
						String msgErr = "@" + String.format("生成单据失败，请让管理员检查目录设置") + "[" + Glo.getFlowFileBill() + "]。@Err：" + ex.getMessage() + " @File=" + file + " @Path:" + path;
						billInfo += "@<font color=red>" + msgErr + "</font>";
						throw new RuntimeException(msgErr + "@其它信息:" + ex.getMessage());
					}
				}
				rtf.HisGEEntity = ndxxRpt;
				//加入他的明细表.
				ArrayList<Entities> al = new ArrayList<Entities>();
				MapDtls mapdtls = mapData.getMapDtls();
				for (MapDtl dtl : mapdtls.ToJavaList())
				{
					GEDtls dtls1 = new GEDtls(dtl.getNo());
					mapData.getEnMap().AddDtl(dtls1, "RefPK");
				}
				al = mapData.GetDtlsDatasOfList(String.valueOf(pkval));

				for (Entities ens : al)
				{
					rtf.AddDtlEns(ens);
				}

				//增加多附件数据
				FrmAttachments aths = mapData.getFrmAttachments();
				for (FrmAttachment athDesc : aths.ToJavaList())
				{
					FrmAttachmentDBs athDBs = new FrmAttachmentDBs();
					if (athDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, athDesc.getMyPK(), FrmAttachmentDBAttr.RefPKVal, newWorkID, "RDT") == 0)
					{
						continue;
					}

					rtf.getEnsDataAths().put(athDesc.getNoOfObj(), athDBs);
				}

				if (nd != null)
				{
					//把审核日志表加入里面去.
					Paras ps = new Paras();
					String trackTable = "ND" + Integer.parseInt(nd.getFlowNo()) + "Track";
					boolean isHaveWriteDB = false;
					if (DBAccess.IsExitsTableCol(trackTable, "WriteDB") == true)
					{
						isHaveWriteDB = true;
					}
					if (isHaveWriteDB == true)
					{
						ps.SQL = "SELECT MyPK,NDFrom,ActionType,EmpFrom,EmpFromT,RDT,Msg,WriteDB FROM " + trackTable + " WHERE ActionType=" + SystemConfig.getAppCenterDBVarStr() + "ActionType AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID";
					}
					else
					{
						ps.SQL = "SELECT MyPK,NDFrom,ActionType,EmpFrom,EmpFromT,RDT,Msg FROM " + trackTable + " WHERE ActionType=" + SystemConfig.getAppCenterDBVarStr() + "ActionType AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID";
					}
					ps.Add(TrackAttr.ActionType, ActionType.WorkCheck.getValue());
					ps.Add(TrackAttr.WorkID, newWorkID);
					DataTable dt = DBAccess.RunSQLReturnTable(ps);
					if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
					{
						dt.Columns.get(0).ColumnName = "MyPK";
						dt.Columns.get(1).ColumnName = "NDFrom";
						dt.Columns.get(2).ColumnName = "ActionType";
						dt.Columns.get(3).ColumnName = "EmpFrom";
						dt.Columns.get(4).ColumnName = "EmpFromT";
						dt.Columns.get(5).ColumnName = "RDT";
						dt.Columns.get(6).ColumnName = "Msg";
						if (isHaveWriteDB == true)
						{
							dt.Columns.get(8).ColumnName = "WriteDB";
						}
					}

					if (DBAccess.IsExitsTableCol(trackTable, "WriteDB") == true)
					{
						for (DataRow dr : dt.Rows)
						{
							dr.setValue("WriteDB", DBAccess.GetBigTextFromDB(trackTable, "MyPK", dr.getValue(0).toString(), "WriteDB"));
						}
					}

					rtf.dtTrack = dt;

					//获取启用审核组件的节点
					ps = new Paras();
					ps.SQL = "SELECT NodeID FROM WF_Node WHERE FWCSta=1 AND FK_Flow=" + SystemConfig.getAppCenterDBVarStr() + "FK_Flow";
					ps.Add(NodeAttr.FK_Flow, nd.getFlowNo());
					rtf.wks = DBAccess.RunSQLReturnTable(ps);
				}
			}

			paths = file.split("[_]", -1);
			path = paths[0] + "/" + paths[1] + "/" + paths[2] + "/";
			String fileModelT = "rtf";
			if (func.getTemplateFileModel().getValue() == 1)
			{
				fileModelT = "word";
			}

			String billUrl = "url@" + fileModelT + "@" + Glo.getCCFlowAppPath() + "DataUser/Bill/" + path + file;

			if (func.getHisPrintFileType() == PrintFileType.PDF)
			{
				billUrl = billUrl.replace(".doc", ".pdf");
			}

			path = Glo.getFlowFileBill() + LocalDateTime.now().getYear() + "/" + WebUser.getDeptNo() + "/" + func.getMyPK() + "/";
			if ((new File(path)).isDirectory() == false)
			{
				(new File(path)).mkdirs();
			}



			//用于扫描打印.
			String qrUrl = SystemConfig.getHostURL() + "WF/WorkOpt/PrintDocQRGuide.htm?MyPK=" + func.getMyPK();
			rtf.MakeDoc(tempFile, path, file, qrUrl);

			///#endregion


			///#region 转化成pdf.
			if (func.getHisPrintFileType() == PrintFileType.PDF)
			{
				String rtfPath = path + file;
				String pdfPath = rtfPath.replace(".doc", ".pdf");
				try
				{
					// BP.WF.Glo.Rtf2PDF(rtfPath, pdfPath);
				}
				catch (RuntimeException ex)
				{
					return "err@" + ex.getMessage();
				}
			}

			///#endregion

			//在线WebOffice打开
			if (func.getPrintOpenModel() == PrintOpenModel.WebOffice)
			{
				return "err@【/WF/WebOffice/PrintOffice.htm】该文件没有重构好,您可以找到旧版本解决，或者自己开发。";
			}
			return billUrl;
		}
		catch (RuntimeException ex)
		{
			path = Glo.getFlowFileBill() + LocalDateTime.now().getYear() + "/" + WebUser.getDeptNo() + "/" + func.getMyPK() + "/";
			String msgErr = "@" + String.format("生成单据失败，请让管理员检查目录设置") + "[" + Glo.getFlowFileBill() + "]。err@" + ex.getMessage() + " @File=" + file + " @Path:" + path;
			return "err@<font color=red>" + msgErr + "</font>" + ex.getMessage();
		}
	}
	/**
	 Wps打印Word文档

	 @param nd
	 @param workID
	 @param fid
	 @param formID
	 @param func
	 @return
	 */
	public final String PrintDoc_WpsWord(Node nd, long workID, long fid, String formID, FrmPrintTemplate func) throws Exception {
		DataSet ds = new DataSet();
		DataTable dt = func.ToDataTableField("Sys_FrmPrintTemplate");
		ds.Tables.add(dt);
		long pkval = workID;
		Work wk = null;
		String billInfo = "";
		if (nd != null)
		{
			FrmNode fn = new FrmNode(nd.getNodeID(), formID);
			//先判断解决方案
			if (fn != null && fn.getWhoIsPK() != WhoIsPK.OID)
			{
				if (fn.getWhoIsPK() == WhoIsPK.PWorkID)
				{
					pkval = this.getPWorkID();
				}
				if (fn.getWhoIsPK() == WhoIsPK.FID)
				{
					pkval = fid;
				}
			}
			wk = nd.getHisWork();
			wk.setOID(this.getWorkID());
			wk.RetrieveFromDBSources();
		}
		MapData mapData = new MapData(formID);
		long newWorkID = 0;

		if (DataType.IsNullOrEmpty(func.getFrmID()) == false)
		{
			//把流程主表数据放入里面去.
			GEEntity ndxxRpt = new GEEntity(formID);
			try
			{
				ndxxRpt.setPKVal(pkval);
				ndxxRpt.Retrieve();

				newWorkID = pkval;
			}
			catch (RuntimeException ex)
			{
				if (getFID() > 0)
				{
					ndxxRpt.setPKVal(this.getFID());
					ndxxRpt.Retrieve();

					newWorkID = this.getFID();

					wk = null;
					wk = nd.getHisWork();
					wk.setOID(this.getWorkID());
					wk.RetrieveFromDBSources();
				}
			}
			//加入主表信息
			dt = ndxxRpt.ToDataTableField("MainTable");
			ds.Tables.add(dt);
			dt = mapData.getMapAttrs().ToDataTableField("Sys_MapAttr");
			ds.Tables.add(dt);

			//加入他的明细表.
			MapDtls dtls = mapData.getMapDtls();
			dt = dtls.ToDataTableField("Sys_MapDtl");
			ds.Tables.add(dt);
			for (MapDtl dtl : dtls.ToJavaList())
			{
				GEDtls dtls1 = new GEDtls(dtl.getNo());
				EnDtl endtl = new EnDtl();
				endtl.Ens = dtls1;
				endtl.RefKey = "RefPK";
				dt = dtl.GetDtlEnsDa(endtl, String.valueOf(pkval)).ToDataTableField(dtl.getNo());
				ds.Tables.add(dt);
			}

			//增加多附件数据
			FrmAttachments aths = mapData.getFrmAttachments();
			DataTable athDt = aths.ToDataTableField("Sys_FrmAttachment");
			ds.Tables.add(athDt);
			for (FrmAttachment athDesc : aths.ToJavaList())
			{
				FrmAttachmentDBs athDBs = new FrmAttachmentDBs();
				if (athDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, athDesc.getMyPK(), FrmAttachmentDBAttr.RefPKVal, newWorkID, "RDT") == 0)
				{
					continue;
				}
				dt = athDBs.ToDataTableField(athDesc.getMyPK());
				ds.Tables.add(dt);
			}

			if (nd != null)
			{
				//把审核日志表加入里面去.
				Paras ps = new Paras();
				ps.SQL = "SELECT * FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track WHERE ActionType=" + SystemConfig.getAppCenterDBVarStr() + "ActionType AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID";
				ps.Add(TrackAttr.ActionType, ActionType.WorkCheck.getValue());
				ps.Add(TrackAttr.WorkID, newWorkID);

				dt = DBAccess.RunSQLReturnTable(ps);
				dt.TableName = "WF_Track";
				ds.Tables.add(dt);
			}
		}
		return bp.tools.Json.ToJson(ds);
	}
	private String GetChildNodes(long parentID, GroupFields gfs, String rtfStr, String hidegroup, String parentIdx, bp.pub.RTFEngine rtf) throws Exception {
		int idx = 0;
		for (GroupField gf : gfs.ToJavaList())
		{
			if (gf.GetValIntByKey("ParentOID") == parentID)
			{

				if (hidegroup.contains(gf.getOID() + ",") == false)
				{
					idx++;
					if (rtfStr.contains("<Group." + gf.getOID() + ">") == true)
					{
						rtfStr = rtfStr.replace("<Group." + gf.getOID() + ">", parentIdx + "." + String.valueOf(idx) + rtf.GetCode(gf.getLab()));
						//处理
						rtfStr = GetChildNodes(gf.getOID(), gfs, rtfStr, hidegroup, parentIdx + "." + String.valueOf(idx), rtf);
					}
				}
				else
				{
					//移除这个分组标签
					if (rtfStr.contains("<Group." + gf.getOID() + ">") == true)
					{
						rtfStr = GetRtfStr(rtfStr, "<Group." + gf.getOID() + ">");
					}
					//递归获取下面的子级
					rtfStr = GetChildNodes(gf.getOID(), gfs, rtfStr, hidegroup, parentIdx + "." + idx, rtf);
				}

				//移除从表
				if (gf.getCtrlType().equals("Dtl") == true && rtfStr.contains("<" + gf.getCtrlID() + ".Begin>") == true)
				{
					if (hidegroup.contains(gf.getOID() + ",") == true)
					{
						int pos_rowKey = rtfStr.indexOf("<" + gf.getCtrlID() + ".Begin>") + 1;
						int row_start = -1, row_end = -1;
						row_start = rtfStr.substring(0, pos_rowKey).lastIndexOf("\\pard");
						pos_rowKey = rtfStr.indexOf("<" + gf.getCtrlID() + ".End>") + 1;
						row_end = rtfStr.substring(pos_rowKey).indexOf("\\par") + 6;
						if (row_start != -1 && row_end != -1)
						{
							rtfStr = rtfStr.substring(0, row_start) + rtfStr.substring(pos_rowKey).substring(row_end);
						}
					}
					else
					{
						//移除这个分组标签
						rtfStr = GetRtfStr(rtfStr, "<" + gf.getCtrlID() + ".Begin>");
						rtfStr = GetRtfStr(rtfStr, "<" + gf.getCtrlID() + ".End>");
					}


				}
				//移除附件
				if (gf.getCtrlType().equals("Ath") == true && rtfStr.contains("<" + gf.getCtrlID() + ".>") == true && hidegroup.contains(gf.getOID() + ",") == true)
				{

				}
			}
		}
		return rtfStr;
	}
	private String GetRtfStr(String rtfStr, String tag)
	{
		//移除这个分组标签
		int pos_rowKey = rtfStr.indexOf(tag) + 1;
		int row_start = -1, row_end = -1;
		if (pos_rowKey > 0)
		{
			row_start = rtfStr.substring(0, pos_rowKey).lastIndexOf("\\pard");
			row_end = rtfStr.substring(pos_rowKey).indexOf("\\par") + 6;
		}
		if (row_start != -1 && row_end != -1)
		{
			rtfStr = rtfStr.substring(0, row_start) + rtfStr.substring(pos_rowKey).substring(row_end);
		}
		return rtfStr;
	}
	private String GetAttrChildNodes(MapAttrs mapAttrs, String rtfStr, String hideAttrs, String parentIdx, bp.pub.RTFEngine rtf) throws Exception {
		for (MapAttr mapAttr : mapAttrs.ToJavaList())
		{
			if (rtfStr.contains("<" + mapAttr.getKeyOfEn() + ">") == true)
			{
				if (hideAttrs.contains(mapAttr.getKeyOfEn() + ",") == true)
				{
					//移除这个分组标签
					rtfStr = GetRtfStr(rtfStr, "<" + mapAttr.getKeyOfEn() + ">");
				}
			}
		}
		return rtfStr;
	}
	/**
	 执行挂起

	 @return
	 */
	public final String Hungup_Save() throws Exception {
		int delayWay = this.GetRequestValInt("DelayWay");
		String strRelDate = this.GetRequestVal("RelDate");
		String relDate;

		if (delayWay == 1)
		{
			if (strRelDate == null || strRelDate.isEmpty())
			{
				return "err@截止时间不可以为空";
			}

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date dtimeRelDate = formatter.parse(strRelDate);
			if (dtimeRelDate.compareTo(new Date()) < 0)
			{
				return "err@截止时间不可以小于当前时间";
			}

			relDate = DataType.getDateByFormart(dtimeRelDate,DataType.getSysDateTimeFormat());
		}
		else
		{
			relDate = "";
		}

		Dev2Interface.Node_HungupWork(this.getWorkID(), delayWay, relDate, this.GetRequestVal("Doc"));
		return "挂起成功.";
	}

	/**
	 构造函数
	 */
	public WF_WorkOpt()
	{
	}
	/**
	 打包下载

	 @return
	 */
	public final String Packup_Init()
	{
		try
		{
			String basePath = this.GetRequestVal("BasePath");
			String sourceType = this.GetRequestVal("SourceType");
			//打印单据实体、单据表单
			if (DataType.IsNullOrEmpty(sourceType) == false && sourceType.equals("Bill"))
			{
				return MakeForm2Html.MakeBillToPDF(this.GetRequestVal("FrmID"), this.getWorkID(), null, null, basePath);
			}
			if (DataType.IsNullOrEmpty(sourceType) == false && sourceType.equals("EntityNoName"))
			{
				return MakeForm2Html.MakeEntityNoNameToPDF(this.GetRequestVal("FrmID"), this.getNo(), null, null, basePath);
			}
			int nodeID = this.getNodeID();
			if (this.getNodeID() == 0)
			{
				GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
				nodeID = gwf.getNodeID();
			}

			Node nd = new Node(nodeID);
			//树形表单方案单独打印
			if ((nd.getHisFormType() == NodeFormType.SheetTree && nd.getHisPrintPDFModle() == 1))
			{
				//获取该节点绑定的表单
				// 所有表单集合.
				MapDatas mds = new MapDatas();
				mds.RetrieveInSQL("SELECT FK_Frm FROM WF_FrmNode WHERE FK_Node=" + this.getNodeID() + " AND FrmEnableRole != 5");
				return "info@" + bp.tools.Json.ToJson(mds.ToDataTableField("dt"));
			}
			return MakeForm2Html.MakeCCFormToPDF(nd, this.getWorkID(), this.getFlowNo(), null, null, basePath);
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}

	/**
	 批量pdf打包下载
	 //树形表单方案 未处理
	 @return
	 */
	public final String BatchPdfPackup()
	{
		try
		{
			String workIds = this.GetRequestVal("workids");
			String flowNo = this.GetRequestVal("FlowNo");

			return MakeForm2Html.MakeBatchPDFZip(workIds,flowNo);


		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}

	/**
	 * 独立表单PDF打印
	 * @return
	 * @throws Exception
	 */
	public final String Packup_FromInit()
	{
		try
		{
			int nodeID = this.getNodeID();
			if (this.getNodeID() == 0)
			{
				GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
				nodeID = gwf.getNodeID();
			}
			Node nd = new Node(nodeID);
			return MakeForm2Html.MakeFormToPDF(this.GetRequestVal("FrmID"), this.GetRequestVal("FrmName"), nd, this.getWorkID(), this.getFlowNo(), null, false, this.GetRequestVal("BasePath"));
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/**
	 扫描二维码获得文件.

	 @return
	 */
	public final String PrintDocQRGuide_Init()
	{
		try
		{
			String basePath = this.GetRequestVal("BasePath");
			int nodeID = this.getNodeID();
			if (this.getNodeID() == 0)
			{
				GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
				nodeID = gwf.getNodeID();
			}

			Node nd = new Node(nodeID);
			Work wk = nd.getHisWork();
			return MakeForm2Html.MakeCCFormToPDF(nd, this.getWorkID(), this.getFlowNo(), null, null, basePath);
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/**
	 选择表单,发起前置导航.

	 @return
	 */
	public final String StartGuideFrms_Init() throws Exception {
		String sql = "SELECT No,Name From Sys_MapData A,WF_FrmNode B WHERE A.No=B.FK_Frm AND B.FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node AND FrmEnableRole=" + FrmEnableRole.WhenHaveFrmPara.getValue();
		Paras ps = new Paras();
		ps.SQL = sql;
		ps.Add(FrmNodeAttr.FK_Node, Integer.parseInt(this.getFlowNo() + "01"));
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		dt.TableName = "Frms";
		DataSet ds = new DataSet();
		ds.Tables.add(dt);
		Flow flow = new Flow(this.getFlowNo());
		ds.Tables.add(flow.ToDataTableField("WF_Flow"));
		return bp.tools.Json.ToJson(ds);
	}


	///#region 公文处理.
	/**
	 直接下载

	 @return
	 */
	public final String DocWord_OpenByHttp() throws Exception {
		String DocName = this.GetRequestVal("DocName"); //获取上传的公文模板名称
		//生成文件.
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		Flow fl = new Flow(this.getFlowNo());

		String file = SystemConfig.getPathOfTemp() + "/" + DocName;
		DBAccess.GetFileFromDB(file, fl.getPTable(), "OID", String.valueOf(this.getWorkID()), "DocWordFile");
		return "../../DataUser/Temp/" + DocName;

	}
	/**
	 重置公文文件.

	 @return
	 */
	public final String DocWord_ResetFile() throws Exception {
		Flow fl = new Flow(this.getFlowNo());
		String sql = "UPDATE " + fl.getPTable() + " SET DocWordFile=NULL WHERE OID=" + this.getWorkID();
		DBAccess.RunSQL(sql);
		return "重新生成模版成功.";
	}
	/**
	 生成文件模版

	 @return
	 */
	public final String DocWord_Init() throws Exception {
		BtnLab lab = new BtnLab(this.getNodeID());
		if (lab.getOfficeBtnEnableInt() == 0)
		{
			return "err@当前节点没有启用公文.";
		}

		//首先判断是否生成公文文件？ todo.
		Flow fl = new Flow(this.getFlowNo());
		byte[] val = DBAccess.GetByteFromDB(fl.getPTable(), "OID", String.valueOf(this.getWorkID()), "DocWordFile");
		if (val != null)
		{
			return "info@OfficeBtnEnable=" + String.valueOf(lab.getOfficeBtnEnableInt()) + ";请下载文件"; //如果已经有这个模版了.
		}

		DocTemplate en = new DocTemplate();
		//求出要生成的模版.
		DocTemplates ens = new DocTemplates();
		ens.Retrieve(DocTemplateAttr.FK_Node, this.getNodeID(), null);
		if (ens.size() > 1)
		{
			return "url@DocWordSelectDocTemp.htm";
		}

		//如果没有模版就给他一个默认的模版.
		if (ens.size() == 0)
		{
			en.setFilePath(SystemConfig.getPathOfDataUser() + "DocTemplete/Default.docx");
		}

		if (ens.size() == 1)
		{
			en = ens.get(0) instanceof DocTemplate ? (DocTemplate)ens.get(0) : null;
		}


///#warning 替换变量. todo.

		DBAccess.SaveBytesToDB(en.getFileBytes(), fl.getPTable(), "OID", this.getWorkIDStr(), "DocWordFile");
		return "info@OfficeBtnEnable=" + String.valueOf(lab.getOfficeBtnEnableInt()) + ";请下载文件"; //如果已经有这个模版了.
	}
	/**
	 上传

	 @return
	 */
	public final String DocWord_Upload() throws Exception {

		String path = SystemConfig.getPathOfTemp() + DBAccess.GenerGUID() + ".docx";
		HttpServletRequest request = getRequest();
		String contentType = request.getContentType();
		if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
			if (new File(path).exists() == false)
				new File(path).mkdirs();

			try {
				CommonFileUtils.upload(request, "file", new File(path));
			} catch (Exception e) {
				e.printStackTrace();
				return "err@请上传模版";
			}
		}
		//插入模版.
		DocTemplate dt = new DocTemplate();
		dt.setNodeID(this.getNodeID());
		dt.setNo(DBAccess.GenerGUID(0, null, null));
		dt.setName(new File(path).getName());
		dt.setFilePath(path); //路径
		dt.setNodeID(this.getNodeID());
		dt.Insert();

		Flow fl = new Flow(this.getFlowNo());
		DBAccess.SaveFileToDB(path, fl.getPTable(), "OID", String.valueOf(this.getWorkID()), "DocWordFile");

		return "上传成功.";
	}

	/**
	 选择一个模版

	 @return
	 */
	public final String DocWordSelectDocTemp_Imp() throws Exception {
		Node node = new Node(this.getNodeID());
		if (node.getItIsStartNode() == false)
		{
			return "err@不是开始节点不可以执行模板导入.";
		}

		DocTemplate docTemplate = new DocTemplate(this.getNo());
		if ((new File(docTemplate.getFilePath())).isFile() == false)
		{
			return "err@选择的模版文件不存在,请联系管理员.";
		}

		byte[] bytes = DataType.ConvertFileToByte(docTemplate.getFilePath());
		Flow fl = new Flow(this.getFlowNo());
		DBAccess.SaveBytesToDB(bytes, fl.getPTable(), "OID", this.getWorkIDStr(), "DocWordFile");
		return "模板导入成功.";
	}

	///#endregion


	///#region 通用人员选择器.
	public final String AccepterOfGener_Init() throws Exception {
		SelectAccpers ens = AccepterOfGener_Init_Ext();
		return ens.ToJson("dt");
	}
	public final String AccepterOfGener_Init_TS() throws Exception {
		SelectAccpers ens = AccepterOfGener_Init_Ext();

		return ens.ToJson("dt");
	}
	/**
	 通用人员选择器Init

	 @return
	 */
	public final SelectAccpers AccepterOfGener_Init_Ext() throws Exception {
		/* 获得上一次发送的人员列表. */
		int toNodeID = this.GetRequestValInt("ToNode");
		Selector selector = new Selector(toNodeID);


		//查询出来,已经选择的人员.
		SelectAccpers sas = new SelectAccpers();
		int i = sas.Retrieve(SelectAccperAttr.FK_Node, toNodeID, SelectAccperAttr.WorkID, this.getWorkID(), SelectAccperAttr.Idx);
		if (selector.getItIsAutoLoadEmps() == false)
		{
			return sas;
		}
		if (i == 0)
		{
			//获得最近的一个workid.
			String trackTable = "ND" + Integer.parseInt(this.getFlowNo()) + "Track";
			Paras ps = new Paras();
			if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
			{
				ps.SQL = "SELECT TOP 1 Tag,EmpTo FROM " + trackTable + " WHERE NDTo=" + SystemConfig.getAppCenterDBVarStr() + "NDTo AND (ActionType=0 OR ActionType=1) AND EmpFrom=" + SystemConfig.getAppCenterDBVarStr() + "EmpFrom ORDER BY WorkID desc  ";
				ps.Add("NDTo", toNodeID);
				ps.Add("EmpFrom", WebUser.getNo(), false);
			}
			else if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
			{
				ps.SQL = "SELECT * FROM (SELECT  Tag,EmpTo,WorkID FROM " + trackTable + " A WHERE A.EmpFrom=" + SystemConfig.getAppCenterDBVarStr() + "EmpFrom AND A.NDFrom=" + SystemConfig.getAppCenterDBVarStr() + "NDFrom AND A.NDTo=" + SystemConfig.getAppCenterDBVarStr() + "NDTo AND (ActionType=0 OR ActionType=1) AND EmpFrom=" + SystemConfig.getAppCenterDBVarStr() + "EmpFrom ORDER BY WorkID DESC ) WHERE ROWNUM =1";
				ps.Add("EmpFrom", WebUser.getNo(), false);
				ps.Add("NDFrom", this.getNodeID());
				ps.Add("NDTo", toNodeID);
			}
			else if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
			{
				ps.SQL = "SELECT Tag,EmpTo FROM " + trackTable + " A WHERE A.NDFrom=" + SystemConfig.getAppCenterDBVarStr() + "NDFrom AND A.NDTo=" + SystemConfig.getAppCenterDBVarStr() + "NDTo AND (ActionType=0 OR ActionType=1) AND EmpFrom=" + SystemConfig.getAppCenterDBVarStr() + "EmpFrom ORDER BY WorkID  DESC limit 1,1 ";
				ps.Add("NDFrom", this.getNodeID());
				ps.Add("NDTo", toNodeID);
				ps.Add("EmpFrom", WebUser.getNo(), false);
			}
			else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX)
			{
				ps.SQL = "SELECT Tag,EmpTo FROM " + trackTable + " A WHERE A.NDFrom=:NDFrom AND A.NDTo=:NDTo AND (ActionType=0 OR ActionType=1) AND EmpFrom=:EmpFrom ORDER BY WorkID  DESC limit 1 ";
				ps.Add("NDFrom", this.getNodeID());
				ps.Add("NDTo", toNodeID);
				ps.Add("EmpFrom", WebUser.getNo(), false);
			}
			else
			{
				//ps.SQL = "SELECT Tag, EmpTo FROM " + trackTable + " WHERE NDTo=" + SystemConfig.getAppCenterDBVarStr() + "NDTo AND (ActionType=0 OR ActionType=1) AND EmpFrom=" + SystemConfig.getAppCenterDBVarStr() + "EmpFrom ORDER BY WorkID DESC  ";
				//ps.Add("NDTo", toNodeID);
				//ps.Add("EmpFrom", WebUser.getNo());
				throw new RuntimeException("err@没有判断的数据库类型....");
			}

			DataTable dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() != 0)
			{
				String tag = dt.Rows.get(0).getValue("Tag").toString();
				String emps = dt.Rows.get(0).getValue("EmpTo").toString();
				if (tag != null && tag.contains("EmpsAccepter=") == true)
				{
					String[] strs = tag.split("[@]", -1);
					for (String str : strs)
					{
						if (str == null || Objects.equals(str, "") || str.contains("EmpsAccepter=") == false)
						{
							continue;
						}
						String[] mystr = str.split("[=]", -1);
						if (mystr.length == 2)
						{
							emps = mystr[1];
						}
					}
				}

				if (emps.contains(",") == true)
				{
					if (emps.contains("'") == true)
					{
						String[] strs = emps.split("[;]", -1);
						for (String str : strs)
						{
							String[] emp = str.split("[,]", -1);
							String empNo = emp[0];
							Dev2Interface.Node_AddNextStepAccepters(this.getWorkID(), toNodeID, empNo, false);
						}
					}
				}
				else
				{
					Dev2Interface.Node_AddNextStepAccepters(this.getWorkID(), toNodeID, emps, false);
				}
			}

			if (dt.Rows.size() != 0)
			{
				sas.Retrieve(SelectAccperAttr.FK_Node, toNodeID, SelectAccperAttr.WorkID, this.getWorkID(), null);
			}
		}
		//判断人员是否已经删除
		//if (sas.size()!= 0)
		//{
		//    for (int k = sas.size()- 1; k >= 0; k--)
		//    {
		//        SelectAccper sa = sas[k] as SelectAccper;
		//        Emp emp = new Emp(sa.FK_Emp);
		//        if (emp.getDeptNo() == "")
		//        {
		//            sas.RemoveEn(sa);
		//            sa.Delete();
		//        }

		//    }
		//}
		ArrayList<String> deleteItems = new ArrayList<String>();
		if (sas.size() > 0)
		{
			for (int k = sas.size() - 1; k >= 0; k--)
			{
				SelectAccper sa = sas.get(k) instanceof SelectAccper ? (SelectAccper)sas.get(k) : null;
				if (Objects.equals(sa.getDeptNo(), ""))
				{
					sas.RemoveEn(sa);
					deleteItems.add(sa.getDeptNo());
				}
			}
		}
		if (!deleteItems.isEmpty())
		{
//C# TO JAVA CONVERTER TASK: Local functions are not converted by C# to Java Converter:
//			new System.Threading.Thread()
//				{
//				void run()
//				{
//						SelectAccpers accepters = new SelectAccpers();
//						accepters.Delete("FK_Dept", "");
//				}
//				}.Start();
		}
		return sas;
	}
	/**
	 增加接收人.

	 @return
	 */
	public final String AccepterOfGener_AddEmps()
	{
		try
		{
			//到达的节点ID.
			int toNodeID = this.GetRequestValInt("ToNode");
			String emps = this.GetRequestVal("AddEmps");

			//增加到里面去.
			Dev2Interface.Node_AddNextStepAccepters(this.getWorkID(), toNodeID, emps, false);

			//查询出来,已经选择的人员.
			SelectAccpers sas = new SelectAccpers();
			sas.Retrieve(SelectAccperAttr.FK_Node, toNodeID, SelectAccperAttr.WorkID, this.getWorkID(), SelectAccperAttr.Idx);

			return sas.ToJson("dt");
		}
		catch (Exception ex)
		{
			if (ex.getMessage().contains("INSERT") == true)
			{
				return "err@人员名称重复,导致部分人员插入失败.";
			}

			return "err@" + ex.getMessage();
		}
	}
	/**
	 执行发送.

	 @return
	 */
	public final String AccepterOfGener_Send()
	{
		try
		{
			int toNodeID = this.GetRequestValInt("ToNode");
			if (toNodeID != 0) //排除协作模式下的会签
			{
				Node nd = new Node(toNodeID);
				if (nd.getHisDeliveryWay() == DeliveryWay.BySelected)
				{
					/* 仅仅设置一个,检查压入的人员个数.*/
					Paras ps = new Paras();
					ps.SQL = "SELECT count(WorkID) as Num FROM WF_SelectAccper WHERE FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND AccType=0";
					ps.Add("FK_Node", toNodeID);
					ps.Add("WorkID", this.getWorkID());
					int num = DBAccess.RunSQLReturnValInt(ps, 0);
					if (num == 0)
					{
						return "err@请指定下一步工作的处理人.";
					}
					Selector sr = new Selector(toNodeID);
					if (sr.getItIsSimpleSelector() == true)
					{
						if (num != 1)
						{
							return "err@您只能选择一个接受人,请移除其他的接受人然后执行发送.";
						}
					}
				}
			}

			SendReturnObjs objs = Dev2Interface.Node_SendWork(this.getFlowNo(), this.getWorkID(), toNodeID, null);
			String strs = objs.ToMsgOfHtml();
			strs = strs.replace("@", "<br>@");

			///#region 处理发送后转向.
			//当前节点.
			Node currNode = new Node(this.getNodeID());
			/*处理转向问题.*/
			switch (currNode.getHisTurnToDeal())
			{
				case SpecUrl:
					String myurl = currNode.getTurnToDealDoc();
					if (myurl.contains("?") == false)
					{
						myurl += "?1=1";
					}
					Attrs myattrs = currNode.getHisWork().getEnMap().getAttrs();
					Work hisWK = currNode.getHisWork();
					for (Attr attr : myattrs)
					{
						if (myurl.contains("@") == false)
						{
							break;
						}
						myurl = myurl.replace("@" + attr.getKey(), hisWK.GetValStrByKey(attr.getKey()));
					}
					myurl = myurl.replace("@WebUser.No", WebUser.getNo());
					myurl = myurl.replace("@WebUser.Name", WebUser.getName());
					myurl = myurl.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
					myurl = myurl.replace("@WebUser.DeptNo", WebUser.getDeptNo());

					if (myurl.contains("@"))
					{
						Dev2Interface.Port_SendMsg("admin", currNode.getName() + "在" + currNode.getName() + "节点处，出现错误", "流程设计错误，在节点转向url中参数没有被替换下来。Url:" + myurl, "Err" + currNode.getNo() + "_" + this.getWorkID(), SMSMsgType.Err, this.getFlowNo(), this.getNodeID(), this.getWorkID(), this.getFID());
						throw new RuntimeException("流程设计错误，在节点转向url中参数没有被替换下来。Url:" + myurl);
					}

					if (myurl.contains("PWorkID") == false)
					{
						myurl += "&PWorkID=" + this.getWorkID();
					}

					myurl += "&FromFlow=" + this.getFlowNo() + "&FromNode=" + this.getNodeID() + "&UserNo=" + WebUser.getNo() + "&Token=" + WebUser.getToken();
					return "TurnUrl@" + myurl;

				case TurnToByCond:

					return strs;
				default:
					strs = strs.replace("@WebUser.No", WebUser.getNo());
					strs = strs.replace("@WebUser.Name", WebUser.getName());
					strs = strs.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
					strs = strs.replace("@WebUser.DeptNo", WebUser.getDeptNo());
					return strs;
			}
		}
		catch (Exception ex)
		{

			return "err@" + ex.getMessage();
		}
	}

	///#endregion

	// 查询select集合
	public final String AccepterOfGener_SelectEmps() throws Exception {
		String sql = "";
		String emp = this.GetRequestVal("TB_Emps");


		///#region 保障查询语句的安全.
		emp = emp.toLowerCase();
		emp = emp.replace("'", "");
		emp = emp.replace("&", "&amp");
		emp = emp.replace("<", "&lt");
		emp = emp.replace(">", "&gt");
		emp = emp.replace("delete", "");
		emp = emp.replace("update", "");
		emp = emp.replace("insert", "");

		///#endregion 保障查询语句的安全.

		boolean isPinYin = DBAccess.IsExitsTableCol("Port_Emp", "PinYin");
		if (isPinYin == true)
		{
			//标识结束，不要like名字了.
			if (emp.contains("/"))
			{
				//if (SystemConfig.getCustomerNo() == "TianYe") // 只改了oracle的
				//{
				//    //String endSql = "";
				//    //if (bp.web.WebUser.getDeptNo().IndexOf("18099") == 0)
				//    //    endSql = " AND B.No LIKE '18099%' ";
				//    //else
				//    //    endSql = " AND B.No NOT LIKE '18099%' ";

				//    String specFlowNos = SystemConfig.getAppSettings()["SpecFlowNosForAccpter");
				//    if (DataType.IsNullOrEmpty(specFlowNos) == true)
				//        specFlowNos = ",001,";

				//    String specEmpNos = "";
				//    if (specFlowNos.contains(this.getNodeID().ToString() + ",") == false)
				//        specEmpNos = " AND a.No!='00000001' ";

				//    sql = "SELECT a." + Glo.UserNo + ",a.Name || '/' || b.FullName as Name FROM Port_Emp a, Port_Dept b WHERE  (a.fk_dept=b.no) and (a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%'  OR a.PinYin LIKE '%," + emp.ToLower() + "%') AND rownum<=12 " + specEmpNos;
				//}
				//else
				//{
				if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
				{
					if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
					{
						sql = "SELECT TOP 12 a.No as No,a.Name +'/'+b.Name as Name FROM Port_Emp a,Port_Dept b  WHERE a.OrgNo=" + WebUser.getOrgNo() + " AND (a.fk_dept=b.no) AND (a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%'  OR a.PinYin LIKE '%," + emp.toLowerCase() + "%') ";
					}
					else
					{
						sql = "SELECT TOP 12 a.No,a.Name +'/'+b.Name as Name FROM Port_Emp a,Port_Dept b  WHERE  (a.FK_Dept=b.getNo()) and (a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%'  OR a.PinYin LIKE '%," + emp.toLowerCase() + "%') ";
					}
				}
				if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
				{
					sql = "SELECT a.No,a.Name || '/' || b.name as Name FROM Port_Emp a,Port_Dept b  WHERE  (a.fk_dept=b.no) and (a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%'  OR a.PinYin LIKE '%," + emp.toLowerCase() + "%') AND rownum<=12 ";
				}
				if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
				{
					sql = "SELECT a.No,CONCAT(a.Name,'/',b.name) as Name FROM Port_Emp a,Port_Dept b  WHERE  (a.fk_dept=b.no) and (a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%'  OR a.PinYin LIKE '%," + emp.toLowerCase() + "%') LIMIT 12";
				}

			}
			else
			{
				if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
				{
					if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
					{
						sql = "SELECT TOP 12 a.No as No,a.Name +'/'+b.name as Name FROM Port_Emp a,Port_Dept b  WHERE A.OrgNo='" + WebUser.getOrgNo() + "' AND (a.fk_dept=b.no) and ( a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%'  OR a.PinYin LIKE '%," + emp.toLowerCase() + "%')";
					}
					else
					{
						sql = "SELECT TOP 12 a.No,a.Name +'/'+b.name as Name FROM Port_Emp a,Port_Dept b  WHERE  (a.fk_dept=b.no) and (a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%'  OR  a.PinYin LIKE '%," + emp.toLowerCase() + "%')";
					}
				}
				if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
				{
					sql = "SELECT a.No,a.Name || '/' || b.name as Name FROM Port_Emp a,Port_Dept b  WHERE  (a.fk_dept=b.no) and ( a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%'  OR  a.PinYin LIKE '%," + emp.toLowerCase() + "%') AND rownum<=12 ";
				}
				if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
				{
					if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
					{
						sql = "SELECT a.No,CONCAT(a.Name,'/',b.name) as Name FROM Port_Emp a,Port_Dept b  WHERE  A.OrgNo='" + WebUser.getOrgNo() + "' AND (a.fk_dept=b.no) and ( a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%'  OR  a.PinYin LIKE '%," + emp.toLowerCase() + "%' ) LIMIT 12";
					}
					else
					{
						sql = "SELECT a.No,CONCAT(a.Name,'/',b.name) as Name FROM Port_Emp a,Port_Dept b  WHERE  (a.fk_dept=b.no) and ( a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%'  OR  a.PinYin LIKE '%," + emp.toLowerCase() + "%' ) LIMIT 12";
					}
				}

			}
		}
		else
		{
			if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
			{
				if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
				{
					sql = "SELECT TOP 12 a.No,a.Name +'/'+b.Name as Name FROM Port_Emp a,Port_Dept b  WHERE  a.OrgNo='" + WebUser.getOrgNo() + "'  AND (a.fk_dept=b.no) and (a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%')";
				}
				else
				{
					sql = "SELECT TOP 12 a.No,a.Name +'/'+b.Name as Name FROM Port_Emp a,Port_Dept b  WHERE  (a.fk_dept=b.no) and (a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%')";
				}
			}
			if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
			{
				sql = "SELECT a.No,a.Name || '/' || b.name as Name FROM Port_Emp a,Port_Dept b  WHERE  (a.fk_dept=b.no) and (a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%') and rownum<=12 ";
			}
			if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
			{
				//@lyc
				if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
				{
					sql = "SELECT a.No,CONCAT(a.Name,'/',b.name) as Name FROM Port_Emp a,Port_Dept b  WHERE  (a.fk_dept=b.no) and (a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%') LIMIT 12";
				}
				else
				{
					sql = "SELECT a.No,CONCAT(a.Name,'/',b.name) as Name FROM Port_Emp a,Port_Dept b  WHERE  (a.fk_dept=b.no) and (a.No like '%" + emp + "%' OR a.NAME  LIKE '%" + emp + "%')  AND a.OrgNo='" + WebUser.getOrgNo() + "' LIMIT 12 ";
				}
			}
		}

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.Columns.get(0).ColumnName = "No";
		dt.Columns.get(1).ColumnName = "Name";

		return bp.tools.Json.ToJson(dt);
	}
	/**
	 获取本组织之外的联络人员

	 @return
	 */
	public final String AccepterOfOfficer_Init()
	{
		Paras paras = new Paras();
		paras.SQL = "SELECT A.No,A.Name,FK_Dept, B.NameOfPath As DeptName,A.OrgNo From Port_Emp A,Port_Dept B WHERE A.setDeptNo(B.No AND IsOfficer=1 AND A.OrgNo!=" + SystemConfig.getAppCenterDBVarStr() + "OrgNo";
		paras.Add("OrgNo", WebUser.getOrgNo(), false);
		DataTable dt = DBAccess.RunSQLReturnTable(paras);
		for (DataRow dr : dt.Rows)
		{
			String deptNo = dr.getValue(2) instanceof String ? (String)dr.getValue(2) : null;
			String deptName = dr.getValue(3) instanceof String ? (String)dr.getValue(3) : null;
			if (deptName.contains("/") == false)
			{
				String name = Dev2Interface.GetParentNameByCurrNo(deptNo, "Port_Dept", dr.getValue(4) instanceof String ? (String)dr.getValue(4) : null);
				if (deptName.equals(name) == false)
				{
					DBAccess.RunSQL("UPDATE Port_Dept SET NameOfPath='" + name + "' WHERE No='" + deptNo + "'");
					dr.setValue(3, name);
				}

			}
		}

		return bp.tools.Json.ToJson(dt);
	}

	///#region 会签.
	/**
	 会签

	 @return
	 */
	public final String HuiQian_Init() throws Exception {

		DataSet ds = Dev2Interface.Node_HuiQian_Init(this.getWorkID());
		return bp.tools.Json.ToJson(ds);
	}
	/**
	 移除

	 @return
	 */
	public final String HuiQian_Delete() throws Exception {
		Dev2Interface.Node_HuiQian_Delete(this.getWorkID(), this.GetRequestVal("FK_Emp"));
		return HuiQian_Init();
	}
	/**
	 增加审核人员

	 @return
	 */
	public final String HuiQian_AddEmps() throws Exception {
		return Dev2Interface.Node_HuiQian_AddEmps(this.getWorkID(), this.GetRequestVal("HuiQianType"), this.GetRequestVal("AddEmps"));
	}

	///#endregion


	///#region 与会签相关的.
	// 查询select集合
	public final String HuiQian_SelectEmps() throws Exception {
		return AccepterOfGener_SelectEmps();
	}
	/**
	 增加主持人

	 @return
	 */
	public final String HuiQian_AddLeader() throws Exception {
		return Dev2Interface.Node_HuiQian_AddLeader(this.getWorkID());
	}
	/**
	 保存并关闭

	 @return
	 */
	public final String HuiQian_SaveAndClose() throws Exception {
		return Dev2Interface.Node_HuiQianDone(this.getWorkID(), this.getToNodeID());

	}

	///#endregion


	///#region 审核组件.
	/**
	 校验密码

	 @return
	 */
	public final String WorkCheck_CheckPass() throws Exception {
		String sPass = this.GetRequestVal("SPass");
		bp.wf.port.WFEmp emp = new bp.wf.port.WFEmp(WebUser.getNo());
		if (Objects.equals(emp.getSPass(), sPass))
		{
			return "签名成功";
		}
		return "err@密码错误";
	}
	/**
	 修改密码

	 @return
	 */
	public final String WorkCheck_ChangePass() throws Exception {
		String sPass = this.GetRequestVal("SPass");
		String sPass1 = this.GetRequestVal("SPass1");
		String sPass2 = this.GetRequestVal("SPass2");

		bp.wf.port.WFEmp emp = new bp.wf.port.WFEmp(WebUser.getNo());
		if (Objects.equals(emp.getSPass(), sPass))
		{
			return "旧密码错误";
		}

		if (sPass1.equals(sPass2) == false)
		{
			return "err@两次输入的密码不一致";
		}
		emp.setSPass(sPass2);
		emp.Update();
		return "密码修改成功";
	}

	/**
	 初始化审核组件数据.

	 @return
	 */
	public final String WorkCheck_Init() throws Exception {
		if (WebUser.getNo() == null)
		{
			return "err@登录信息丢失,请重新登录.";
		}

		//表单库审核组件流程编号为null的异常处理
		if (DataType.IsNullOrEmpty(this.getFlowNo()) == true)
		{
			return "err@流程信息丢失,请联系管理员.";
		}

		DataSet ds = new DataSet();

		GenerWorkFlow gwf = new GenerWorkFlow();
		if (this.getWorkID() != 0)
		{
			gwf.setWorkID(this.getWorkID());
			gwf.Retrieve();
		}
		int nodeID = this.getNodeID();
		if(nodeID == 0)
			nodeID= gwf.getNodeID();
		///#region 定义变量.

		//查询审核意见的表名
		String trackTable = "ND" + Integer.parseInt(this.getFlowNo()) + "Track";

		//当前节点的审核信息
		NodeWorkCheck wcDesc = new NodeWorkCheck(nodeID);

		NodeWorkCheck frmWorkCheck = null;
		FrmAttachmentDBs athDBs = null; //上传附件集合
		Nodes nds = new Nodes(this.getFlowNo());

		Node nd = null;


		Track tkDoc = null;
		String nodes = ""; //可以审核的节点.

		boolean isExitTb_doc = true;

		DataRow row = null;

		//是不是只读?
		boolean isReadonly = this.GetRequestVal("IsReadonly") != null && this.GetRequestVal("IsReadonly").equals("1") ? true : false;

		//是否可以编辑审核意见
		boolean isCanDo = false;
		if (isReadonly == true)
		{
			isCanDo = false;
		}
		else
		{
			isCanDo = Dev2Interface.Flow_IsCanDoCurrentWork(this.getWorkID(), WebUser.getNo());
		}


		NodeWorkCheck fwc = null;
		DataTable dt = null;

		//当前流程的审核组件的HuiQian
		NodeWorkChecks fwcs = new NodeWorkChecks();
		fwcs.Retrieve(NodeAttr.FK_Flow, this.getFlowNo(), NodeAttr.Step);
		ds.Tables.add(wcDesc.ToDataTableField("WF_FrmWorkCheck"));


		//审核意见存储集合
		DataTable tkDt = new DataTable("Tracks");
		tkDt.Columns.Add("MyPK", String.class);
		tkDt.Columns.Add("NodeID", Integer.class);
		tkDt.Columns.Add("NodeName", String.class);
		tkDt.Columns.Add("Msg", String.class);
		tkDt.Columns.Add("EmpFrom", String.class);
		tkDt.Columns.Add("EmpFromT", String.class);
		tkDt.Columns.Add("DeptName", String.class);
		tkDt.Columns.Add("RDT", String.class);
		tkDt.Columns.Add("IsDoc", Boolean.class);
		tkDt.Columns.Add("ParentNode", Integer.class);
		tkDt.Columns.Add("ActionType", Integer.class);
		tkDt.Columns.Add("Tag", String.class);
		tkDt.Columns.Add("FWCView", String.class);
		tkDt.Columns.Add("WritImg", String.class);
		tkDt.Columns.Add("WriteStamp", String.class);
		tkDt.Columns.Add("SigantureEnabel", Integer.class);//签名方式
		tkDt.Columns.Add("FWCAth", Integer.class);//附件上传
		tkDt.Columns.Add("Exer", String.class);//执行人
		//流程附件集合.
		DataTable athDt = new DataTable("Aths");
		athDt.Columns.Add("NodeID", Integer.class);
		athDt.Columns.Add("MyPK", String.class);
		athDt.Columns.Add("FK_FrmAttachment", String.class);
		athDt.Columns.Add("FK_MapData", String.class);
		athDt.Columns.Add("FileName", String.class);
		athDt.Columns.Add("FileExts", String.class);
		athDt.Columns.Add("CanDelete", Boolean.class);

		//当前节点的流程数据
		FrmAttachmentDBs frmathdbs = new FrmAttachmentDBs();
		frmathdbs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, "ND" + this.getNodeID() + "_FrmWorkCheck", FrmAttachmentDBAttr.RefPKVal, String.valueOf(this.getWorkID()), FrmAttachmentDBAttr.Rec, WebUser.getNo(), FrmAttachmentDBAttr.RDT);

		for (FrmAttachmentDB athDB : frmathdbs.ToJavaList())
		{
			row = athDt.NewRow();
			row.setValue("NodeID", this.getNodeID());
			row.setValue("MyPK", athDB.getMyPK());
			row.setValue("FK_FrmAttachment", athDB.getFKFrmAttachment());
			row.setValue("FK_MapData", athDB.getFrmID());
			row.setValue("FileName", athDB.getFileName());
			row.setValue("FileExts", athDB.getFileExts());
			row.setValue("CanDelete", Objects.equals(athDB.getRec(), WebUser.getNo()) && isReadonly == false ? 1 : 0);
			athDt.Rows.add(row);
		}
		ds.Tables.add(athDt);

		//审核组件的定义
		WorkCheck wc = null;
		if (this.getFID() != 0)
		{
			wc = new WorkCheck(this.getFlowNo(), this.getNodeID(), this.getFID(), 0,wcDesc.GetValIntByKey("FWCTimeModel",0));
		}
		else
		{
			wc = new WorkCheck(this.getFlowNo(), this.getNodeID(), this.getWorkID(), this.getFID(),wcDesc.GetValIntByKey("FWCTimeModel",0));
		}


		//是否屏蔽正在审批的节点的审批意见(在在途，已完成等查看页面使用)
		boolean isShowCurrNodeInfo = true;


		if (isReadonly == true && gwf.getWFState() == WFState.Runing && gwf.getNodeID() == this.getNodeID())
		{
			isShowCurrNodeInfo = false;
		}

		/*
		 * 获得当前节点已经审核通过的人员.
		 * 比如：多人处理规则中的已经审核同意的人员，会签人员,组合成成一个字符串。
		 * 格式为: ,zhangsan,lisi,
		 * 用于处理在审核列表中屏蔽临时的保存的审核信息.
		 * 12 为芒果增加一个非正常完成状态.
		 * */
		String checkerPassed = ",";
		if (gwf.getIsOver()==false)
		{
			Paras ps = new Paras();
			ps.SQL = "SELECT FK_Emp FROM WF_GenerWorkerlist WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND IsPass=1 AND FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node Order By RDT,CDT";
			ps.Add("WorkID", this.getWorkID());
			ps.Add("FK_Node", this.getNodeID());
			DataTable checkerPassedDt = DBAccess.RunSQLReturnTable(ps);
			for (DataRow dr : checkerPassedDt.Rows)
			{
				checkerPassed += dr.getValue("FK_Emp") + ",";
			}
		}


		///#endregion 定义变量.


		///#region 判断是否显示 - 历史审核信息显示
		boolean isDoc = false;
		Tracks tks = null;
		if (wcDesc.getFWCListEnable() == true)
		{
			tks = wc.getHisWorkChecks();

			for (Track tk : tks.ToJavaList())
			{
				//评论组件
				if (tk.getHisActionType() == ActionType.FlowBBS)
				{
					continue;
				}

				//不是审核状态、启动子流程、退回状态就跳出循环
				if ((tk.getHisActionType() != ActionType.WorkCheck && tk.getHisActionType() != ActionType.AutoWorkCheck) && tk.getHisActionType() != ActionType.StartChildenFlow && tk.getHisActionType() != ActionType.Return)
				{
					continue;
				}

				//当前节点只显示自己审核的意见时
				if (wcDesc.getFWCMsgShow() == 1 && tk.getEmpFrom().equals(WebUser.getNo()) == false)
				{
					continue;
				}

				//退回
				if (tk.getHisActionType() == ActionType.Return || tk.getHisActionType() == ActionType.ReturnAndBackWay)
				{
					//1.不显示退回意见 2.显示退回意见但是不是退回给本节点的意见
					if (wcDesc.getFWCIsShowReturnMsg() == 0)
					{
						continue;
					}
					if (wcDesc.getFWCIsShowReturnMsg() == 1 && tk.getNDTo() != this.getNodeID())
					{
						continue;
					}
				}


				///#region 多人处理（协作，会签，队列）
				//如果是多人处理，流程未运行完成，就让其显示已经审核过的意见.
				if (tk.getNDFrom() == this.getNodeID() && checkerPassed.contains("," + tk.getEmpFrom()  + ",") == false && (gwf.getIsOver()==false  && gwf.getWFState().getValue() != 12))
				{
					continue;
				}

				if (gwf.getNodeID() == tk.getNDFrom() && checkerPassed.contains("," + tk.getEmpFrom()  + ",") == false && (gwf.getIsOver()==false && gwf.getWFState().getValue() != 12))
				{
					continue;
				}

				/* if (tk.getNDFrom() == this.getNodeID() && gwf.HuiQianTaskSta != HuiQianTaskSta.None)
				 {
				     //判断会签, 去掉正在审批的节点.
				     if (tk.getNDFrom() == this.getNodeID() && isShowCurrNodeInfo == false)
				         continue;
				 }
 */


				///#endregion 多人处理（协作，会签，队列）
				//为false可能为子流程数据
				if (String.valueOf(tk.getNDFrom()).startsWith(Integer.parseInt(this.getFlowNo()) + "") == true)
				{
					//当前节点在后面设计被删除时也需要屏蔽
					bp.en.Entity tempVar = nds.GetEntityByKey(tk.getNDFrom());
					nd = tempVar instanceof Node ? (Node)tempVar : null;
					if (nd == null)
					{
						continue;
					}
				}
				row = tkDt.NewRow();
				row.setValue("MyPK", tk.getMyPK());
				row.setValue("NodeID", tk.getNDFrom());
				row.setValue("NodeName", tk.getNDFromT());
				bp.en.Entity tempVar2 = fwcs.GetEntityByKey(tk.getNDFrom());
				fwc = tempVar2 instanceof NodeWorkCheck ? (NodeWorkCheck)tempVar2 : null;

				// zhoupeng 增加了判断，在会签的时候最后会签人发送前不能填写意见.
				if (tk.getNDFrom() == this.getNodeID() && tk.getEmpFrom().equals(WebUser.getNo()) && isCanDo && isDoc == false)
				{
					isDoc = true;
					row.setValue("IsDoc", true);
				}
				else
				{
					row.setValue("IsDoc", false);
				}


				row.setValue("ParentNode", 0);
				row.setValue("RDT", DataType.IsNullOrEmpty(tk.getRDT()) ? "" : tk.getNDFrom() == tk.getNDTo() && DataType.IsNullOrEmpty(tk.getMsg()) ? "" : tk.getRDT());

				if (isReadonly == false && tk.getEmpFrom().equals(WebUser.getNo()) && this.getNodeID() == tk.getNDFrom() && isExitTb_doc && (wcDesc.getHisFrmWorkCheckType() == FWCType.Check || ((wcDesc.getHisFrmWorkCheckType() == FWCType.DailyLog || wcDesc.getHisFrmWorkCheckType() == FWCType.WeekLog) && DataType.getDateByFormart(new Date(tk.getRDT()),"yyyy-MM-dd").equals(DataType.getCurrentDate())) || (wcDesc.getHisFrmWorkCheckType() == FWCType.MonthLog && DataType.getDateByFormart(new Date(tk.getRDT()),"yyyy-MM").equals(DataType.getCurrentDateByFormart("yyyy-MM")))))
				{
					boolean isLast = true;
					for (Track tk1 : tks.ToJavaList())
					{
						if (tk1.getHisActionType() == tk.getHisActionType() && tk1.getNDFrom() == tk.getNDFrom() && tk1.getRDT().compareTo(tk.getRDT()) > 0)
						{
							isLast = false;
							break;
						}
					}

					if (isLast && isDoc == false && gwf.getIsOver()==false)
					{
						isExitTb_doc = false;
						row.setValue("IsDoc", true);
						isDoc = true;
						row.setValue("Msg", Dev2Interface.GetCheckInfo(this.getFlowNo(), this.getWorkID(), this.getNodeID(), wcDesc.getFWCDefInfo()));
						tkDoc = tk;
					}
					else
					{
						row.setValue("Msg", tk.getMsgHtml());
					}
				}
				else
				{
					row.setValue("Msg", tk.getMsgHtml());
				}

				row.setValue("EmpFrom", tk.getEmpFrom() );
				row.setValue("EmpFromT", tk.getEmpFromT() );
				//获取部门
				String DeptName = "";
				String[] Arrays = tk.getNodeData().split("[@]", -1);
				for (String i : Arrays)
				{
					if (i.contains("DeptName="))
					{
						DeptName = i.split("[=]", -1)[1];
					}
				}
				row.setValue("DeptName", DeptName);
				row.setValue("ActionType", tk.getHisActionType().getValue());
				row.setValue("Tag", tk.getTag());
				row.setValue("FWCView", fwc != null ? fwc.getFWCView() : "");

				//获取每个节点的签名方式
				String sql = "SELECT SigantureEnabel FROM WF_Node WHERE NodeID=" + tk.getNDFrom();
				String sigantureEnabel = DBAccess.RunSQLReturnString(sql);
				if (DataType.IsNullOrEmpty(sigantureEnabel) == true)
				{
					sigantureEnabel = "0";
				}
				row.setValue("SigantureEnabel", sigantureEnabel);

				//获取每个节点的附件上传
				sql = "SELECT FWCAth FROM WF_Node WHERE NodeID=" + tk.getNDFrom();
				String fwcAth = DBAccess.RunSQLReturnString(sql);
				if (DataType.IsNullOrEmpty(fwcAth) == true)
				{
					fwcAth = "0";
				}

				//添加执行人，前端须查询执行人的附件
				row.setValue("Exer", tk.getExer());

				row.setValue("FWCAth", fwcAth);

				row.setValue("WritImg", "");
				row.setValue("WriteStamp", "");

				//@0=不签名@1=图片签名@2=写字板@3=电子签名@4=电子盖章@5=电子签名+盖章

				if ("2".equals(sigantureEnabel) == true)  //写字板
					row.setValue("WritImg", DBAccess.GetBigTextFromDB(trackTable, "MyPK", tk.getMyPK(), "WriteDB"));

				if ("4".equals(sigantureEnabel) == true && "5".equals(sigantureEnabel) == true)  //电子盖章  电子签名+盖章
					row.setValue("WriteStamp", DBAccess.GetBigTextFromDB(trackTable, "MyPK", tk.getMyPK(), "FrmDB"));

				tkDt.Rows.add(row);


				///#region //审核组件附件数据
				athDBs = new FrmAttachmentDBs();
				QueryObject obj_Ath = new QueryObject(athDBs);
				obj_Ath.AddWhere(FrmAttachmentDBAttr.FK_FrmAttachment, "ND" + tk.getNDFrom() + "_FrmWorkCheck");
				obj_Ath.addAnd();
				obj_Ath.AddWhere(FrmAttachmentDBAttr.RefPKVal, String.valueOf(this.getWorkID()));
				obj_Ath.addAnd();
				obj_Ath.AddWhere(FrmAttachmentDBAttr.Rec, tk.getEmpFrom() );
				obj_Ath.addOrderBy(FrmAttachmentDBAttr.RDT);
				obj_Ath.DoQuery();

				for (FrmAttachmentDB athDB : athDBs.ToJavaList())
				{
					row = athDt.NewRow();
					row.setValue("NodeID", tk.getNDFrom());
					row.setValue("MyPK", athDB.getMyPK());
					row.setValue("FK_FrmAttachment", athDB.getFKFrmAttachment());
					row.setValue("FK_MapData", athDB.getFrmID());
					row.setValue("FileName", athDB.getFileName());
					row.setValue("FileExts", athDB.getFileExts());
					row.setValue("CanDelete", Objects.equals(athDB.getFrmID(), String.valueOf(this.getNodeID())) && Objects.equals(athDB.getRec(), WebUser.getNo()) && isReadonly == false);
					athDt.Rows.add(row);
				}

				///#endregion


				///#region //子流程的审核组件数据
				if (tk.getFID() != 0 && tk.getHisActionType() == ActionType.StartChildenFlow && tkDt.Select("ParentNode=" + tk.getNDFrom()).length == 0)
				{
					String[] paras = tk.getTag().split("[@]", -1);
					String[] p1 = paras[1].split("[=]", -1);
					String fk_flow = p1[1]; //子流程编号

					String[] p2 = paras[2].split("[=]", -1);
					String workId = p2[1]; //子流程ID.
					int biaoji = 0;

					WorkCheck subwc = new WorkCheck(fk_flow, Integer.parseInt(fk_flow + "01"), Long.parseLong(workId), 0,wcDesc.GetValIntByKey("FWCTimeModel",0));
					String subtrackTable = "ND" + Integer.parseInt(fk_flow) + "Track";
					Tracks subtks = subwc.getHisWorkChecks();
					//取出来子流程的所有的节点。
					Nodes subNds = new Nodes(fk_flow);
					for (Node item : subNds.ToJavaList()) //主要按顺序显示
					{
						for (Track mysubtk : subtks.ToJavaList())
						{
							if (item.getNodeID() != mysubtk.getNDFrom())
							{
								continue;
							}

							/*输出该子流程的审核信息，应该考虑子流程的子流程信息, 就不考虑那样复杂了.*/
							if (mysubtk.getHisActionType() == ActionType.WorkCheck)
							{
								// 发起多个子流程时，发起人只显示一次
								if (mysubtk.getNDFrom() == Integer.parseInt(fk_flow + "01") && biaoji == 1)
								{
									continue;
								}
								NodeWorkCheck subFrmCheck = new NodeWorkCheck("ND" + mysubtk.getNDFrom());
								row = tkDt.NewRow();
								row.setValue("NodeID", mysubtk.getNDFrom());
								row.setValue("NodeName", String.format("(子流程)%1$s", mysubtk.getNDFromT()));
								row.setValue("Msg", mysubtk.getMsgHtml());
								row.setValue("EmpFrom", mysubtk.getEmpFrom() );
								row.setValue("EmpFromT", mysubtk.getEmpFromT() );
								//获取部门
								DeptName = "";
								Arrays = tk.getNodeData().split("[@]", -1);
								for (String i : Arrays)
								{
									if (i.contains("DeptName="))
									{
										DeptName = i.split("[=]", -1)[1];
									}
								}
								row.setValue("DeptName", DeptName);
								row.setValue("RDT", mysubtk.getRDT());
								row.setValue("IsDoc", false);
								row.setValue("ParentNode", tk.getNDFrom());
								row.setValue("ActionType", mysubtk.getHisActionType().getValue());
								row.setValue("Tag", mysubtk.getTag());
								row.setValue("FWCView", subFrmCheck.getFWCView());
								if (subFrmCheck.getSigantureEnabel() != 2 && subFrmCheck.getSigantureEnabel() != 3 && subFrmCheck.getSigantureEnabel() != 5)
								{
									row.setValue("WritImg", "");
								}
								else
								{
									row.setValue("WritImg", DBAccess.GetBigTextFromDB(subtrackTable, "MyPK", mysubtk.getMyPK(), "WriteDB"));
								}
								if (subFrmCheck.getSigantureEnabel() != 4 && subFrmCheck.getSigantureEnabel() != 5)
								{
									row.setValue("WriteStamp", "");
								}
								else
								{
									row.setValue("WriteStamp", DBAccess.GetBigTextFromDB(subtrackTable, "MyPK", mysubtk.getMyPK(), "FrmDB"));
								}

								tkDt.Rows.add(row);

								if (mysubtk.getNDFrom() == Integer.parseInt(fk_flow + "01"))
								{
									biaoji = 1;
								}
							}
						}
					}
				}

				///#endregion

			}
		}

		///#endregion 判断是否显示 - 历史审核信息显示


		///#region 审核意见默认填写

		//首先判断当前是否有此意见? 如果是退回的该信息已经存在了.
		boolean isHaveMyInfo = false;
		for (DataRow dr : tkDt.Rows)
		{
			String fk_node = dr.getValue("NodeID").toString();
			String empFrom = dr.getValue("EmpFrom").toString();
			if (Integer.parseInt(fk_node) == this.getNodeID() && Objects.equals(empFrom, bp.web.WebUser.getNo()))
			{
				isHaveMyInfo = true;
			}
		}

		// 增加默认的审核意见.
		if (isExitTb_doc && wcDesc.getHisFrmWorkCheckSta() == FrmWorkCheckSta.Enable && isCanDo && isReadonly == false && isHaveMyInfo == false)
		{
			DataRow[] rows = null;
			bp.en.Entity tempVar3 = nds.GetEntityByKey(this.getNodeID());
			nd = tempVar3 instanceof Node ? (Node)tempVar3 : null;
			if (wcDesc.getFWCOrderModel() == FWCOrderModel.SqlAccepter)
			{
				rows = tkDt.Select("NodeID=" + this.getNodeID() + " AND Msg='' AND EmpFrom='" + WebUser.getNo() + "'");

				if (rows.length == 0)
				{
					rows = tkDt.Select("NodeID=" + this.getNodeID() + " AND EmpFrom='" + WebUser.getNo() + "'");
				}

				if (rows.length > 0)
				{
					row = rows[0];
					row.setValue("IsDoc", true);

					row.setValue("Msg", Dev2Interface.GetCheckInfo(this.getFlowNo(), this.getWorkID(), this.getNodeID(), wcDesc.getFWCDefInfo()));
					if (row.getValue("Msg").toString().equals(""))
					{
						row.setValue("RDT", Dev2Interface.GetCheckRDT(this.getFlowNo(), this.getWorkID(), this.getNodeID(), WebUser.getNo()));
					}

				}
				else
				{
					row = tkDt.NewRow();
					row.setValue("NodeID", this.getNodeID());
					row.setValue("NodeName", nd.getFWCNodeName());
					row.setValue("IsDoc", true);
					row.setValue("ParentNode", 0);
					row.setValue("RDT", Dev2Interface.GetCheckRDT(this.getFlowNo(), this.getWorkID(), this.getNodeID(), WebUser.getNo()));

					row.setValue("Msg", Dev2Interface.GetCheckInfo(this.getFlowNo(), this.getWorkID(), this.getNodeID(), wcDesc.getFWCDefInfo()));
					row.setValue("EmpFrom", WebUser.getNo());
					row.setValue("EmpFromT", WebUser.getName());
					row.setValue("DeptName", WebUser.getDeptName());
					row.setValue("ActionType", ActionType.Forward.getValue());
					row.setValue("Tag", Dev2Interface.GetCheckTag(this.getFlowNo(), this.getWorkID(), this.getNodeID(), WebUser.getNo()));
					tkDt.Rows.add(row);
				}
			}
			else
			{
				row = tkDt.NewRow();
				row.setValue("NodeID", this.getNodeID());
				row.setValue("NodeName", nd.getFWCNodeName());
				row.setValue("IsDoc", true);
				row.setValue("ParentNode", 0);
				row.setValue("RDT", Dev2Interface.GetCheckRDT(this.getFlowNo(), this.getWorkID(), this.getNodeID(), WebUser.getNo()));

				row.setValue("Msg", Dev2Interface.GetCheckInfo(this.getFlowNo(), this.getWorkID(), this.getNodeID(), wcDesc.getFWCDefInfo()));
				row.setValue("EmpFrom", WebUser.getNo());
				row.setValue("EmpFromT", WebUser.getName());
				row.setValue("DeptName", WebUser.getDeptName());
				row.setValue("ActionType", ActionType.Forward.getValue());
				row.setValue("Tag", Dev2Interface.GetCheckTag(this.getFlowNo(), this.getWorkID(), this.getNodeID(), WebUser.getNo()));
				if (wcDesc.getSigantureEnabel() == 5)
				{
					String sql = "Select MyPK From " + trackTable + "  WHERE ActionType=" + ActionType.WorkCheck.getValue() + " AND  NDFrom=" + this.getNodeID() + " AND WorkID=" + this.getWorkID() + " AND EmpFrom = '" + WebUser.getNo() + "'";
					String mypk = DBAccess.RunSQLReturnStringIsNull(sql, "");
					if (DataType.IsNullOrEmpty(mypk) == false)
					{
						row.setValue("WritImg", DBAccess.GetBigTextFromDB(trackTable, "MyPK", mypk, "WriteDB"));
						row.setValue("WriteStamp", DBAccess.GetBigTextFromDB(trackTable, "MyPK", mypk, "FrmDB"));
					}
					else
					{
						row.setValue("WritImg", "");
						row.setValue("WriteStamp", "");

					}
				}
				if (wcDesc.getSigantureEnabel() == 2 || wcDesc.getSigantureEnabel() == 3)
				{
					String sql = "Select MyPK From " + trackTable + "  WHERE ActionType=" + ActionType.WorkCheck.getValue() + " AND  NDFrom=" + this.getNodeID() + " AND WorkID=" + this.getWorkID() + " AND EmpFrom = '" + WebUser.getNo() + "'";
					String mypk = DBAccess.RunSQLReturnStringIsNull(sql, "");
					if (DataType.IsNullOrEmpty(mypk) == false)
					{
						row.setValue("WritImg", DBAccess.GetBigTextFromDB(trackTable, "MyPK", mypk, "WriteDB"));
					}
					else
					{
						row.setValue("WritImg", "");
					}
					row.setValue("WriteStamp", "");
				}
				if (wcDesc.getSigantureEnabel() == 4)
				{
					String sql = "Select MyPK From " + trackTable + "  WHERE ActionType=" + ActionType.WorkCheck.getValue() + " AND  NDFrom=" + this.getNodeID() + " AND WorkID=" + this.getWorkID() + " AND EmpFrom = '" + WebUser.getNo() + "'";
					String mypk = DBAccess.RunSQLReturnStringIsNull(sql, "");
					if (DataType.IsNullOrEmpty(mypk) == false)
					{
						row.setValue("WriteStamp", DBAccess.GetBigTextFromDB(trackTable, "MyPK", mypk, "FrmDB"));
					}
					else
					{
						row.setValue("WriteStamp", "");
					}
					row.setValue("WritImg", "");
				}
				if (wcDesc.getSigantureEnabel() == 0 || wcDesc.getSigantureEnabel() == 1)
				{
					row.setValue("WritImg", "");
					row.setValue("WriteStamp", "");
				}
				tkDt.Rows.add(row);
			}
		}

		///#endregion


		///#region 显示有审核组件，但还未审核的节点. 包括退回后的.
		if (tks == null)
		{
			tks = wc.getHisWorkChecks();
		}
		if(gwf.getWFState()!=WFState.CompleteEnd && gwf.getWFState()!=WFState.CompleteByNode && wcDesc.getFWCIsShowTruck() == true){
			WF_WorkOpt_OneWork oneWork = new WF_WorkOpt_OneWork();
			String nodeIDs = oneWork.GetFutureNodeNotContainsEmps();
			for (NodeWorkCheck item : fwcs.ToJavaList())
			{
				if (item.getFWCIsShowTruck() == false)
				{
					continue; //不需要显示历史记录.
				}
				if(nodeIDs.contains(item.getNodeID()+",")==false)
					continue;
				//是否已审核.
				boolean isHave = false;
				for (Track tk : tks.ToJavaList())
				{
					//翻译.
					if (tk.getNDFrom() == item.getNodeID() && tk.getHisActionType() == ActionType.WorkCheck)
					{
						isHave = true; //已经有了
						break;
					}
				}

				if (isHave == true)
				{
					continue;
				}

				row = tkDt.NewRow();
				row.setValue("NodeID", item.getNodeID());

				Node mynd = (Node)nds.GetEntityByKey(item.getNodeID());
				row.setValue("NodeName", mynd.getFWCNodeName());
				row.setValue("IsDoc", false);
				row.setValue("ParentNode", 0);
				row.setValue("RDT", "");
				row.setValue("Msg", "未审批");
				row.setValue("EmpFrom", "");
				row.setValue("EmpFromT", "");
				row.setValue("DeptName", "");
				row.setValue("WritImg", "");
				row.setValue("WriteStamp", "");
				tkDt.Rows.add(row);
			}

		}

		///#endregion 增加空白.

		ds.Tables.add(tkDt);


		//如果有 SignType 列就获得签名信息.
		if (Objects.equals(SystemConfig.getCustomerNo(), "TianYe"))
		{
			String tTable = "ND" + Integer.parseInt(this.getFlowNo()) + "Track";
			String sql = "SELECT distinct a.No, a.SignType, a.EleID FROM Port_Emp a, " + tTable + " b WHERE (A.No='" + WebUser.getNo() + "') OR B.ActionType=22 AND a.No=b.EmpFrom AND B.WorkID=" + this.getWorkID();

			DataTable dtTrack = DBAccess.RunSQLReturnTable(sql);
			dtTrack.TableName = "SignType";

			dtTrack.Columns.get("NO").ColumnName = "No";
			dtTrack.Columns.get("SIGNTYPE").ColumnName = "SignType";
			dtTrack.Columns.get("ELEID").ColumnName = "EleID";

			ds.Tables.add(dtTrack);
		}

		String str = bp.tools.Json.ToJson(ds);

		return str;
	}
	/**
	 初始化审核组件数据.

	 @return
	 */
	public final String WorkCheck_Init2019() throws Exception {
		if (WebUser.getNo() == null)
		{
			return "err@登录信息丢失,请重新登录.";
		}
		GenerWorkFlow gwf = new GenerWorkFlow();
		if (this.getWorkID() != 0)
		{
			gwf.setWorkID(this.getWorkID());
			gwf.Retrieve();
		}

		///#region 定义变量.
		String trackTable = "ND" + Integer.parseInt(this.getFlowNo()) + "Track";
		int nodeID = this.getNodeID();
		if(nodeID == 0)
			nodeID= gwf.getNodeID();
		NodeWorkCheck wcDesc = new NodeWorkCheck(nodeID); // 当前节点的审核组件
		NodeWorkCheck frmWorkCheck = null;
		FrmAttachmentDBs athDBs = null; //附件数据
		Nodes nds = new Nodes(this.getFlowNo()); //该流程的所有节点
		NodeWorkChecks fwcs = new NodeWorkChecks();
		Node nd = null;
		WorkCheck wc = null;
		Tracks tks = null;
		Track tkDoc = null;
		String nodes = ""; //已经审核过的节点.
		boolean isCanDo = false; //是否可以审批
		boolean isExitTb_doc = true;
		DataSet ds = new DataSet();
		DataRow row = null;

		//是不是只读?
		boolean isReadonly = false;
		if (this.GetRequestVal("IsReadonly") != null && this.GetRequestVal("IsReadonly").equals("1"))
		{
			isReadonly = true;
		}

		DataTable nodeEmps = new DataTable();
		NodeWorkCheck fwc = null;
		DataTable dt = null;
		int idx = 0;
		int noneEmpIdx = 0;

		fwcs.Retrieve(NodeAttr.FK_Flow, this.getFlowNo(), NodeAttr.Step);
		ds.Tables.add(wcDesc.ToDataTableField("WF_FrmWorkCheck")); //当前的节点审核组件定义，放入ds.

		//审核意见的存储集合
		DataTable tkDt = new DataTable("Tracks");
		tkDt.Columns.Add("MyPK", String.class);
		tkDt.Columns.Add("NodeID", Integer.class);
		tkDt.Columns.Add("NodeName", String.class);
		tkDt.Columns.Add("Msg", String.class);
		tkDt.Columns.Add("EmpFrom", String.class);
		tkDt.Columns.Add("EmpFromT", String.class);
		tkDt.Columns.Add("DeptName", String.class);
		tkDt.Columns.Add("RDT", String.class);
		tkDt.Columns.Add("IsDoc", Boolean.class);
		tkDt.Columns.Add("ParentNode", Integer.class);
		tkDt.Columns.Add("ActionType", Integer.class);
		tkDt.Columns.Add("Tag", String.class);
		tkDt.Columns.Add("FWCView", String.class);
		tkDt.Columns.Add("WritImg", String.class);
		tkDt.Columns.Add("WriteStamp", String.class);
		tkDt.Columns.Add("SigantureEnabel", Integer.class);
		tkDt.Columns.Add("FWCAth", Integer.class);
		tkDt.Columns.Add("Exer", String.class);
		//流程附件.
		DataTable athDt = new DataTable("Aths");
		athDt.Columns.Add("NodeID", Integer.class);
		athDt.Columns.Add("MyPK", String.class);
		athDt.Columns.Add("FK_FrmAttachment", String.class);
		athDt.Columns.Add("FK_MapData", String.class);
		athDt.Columns.Add("FileName", String.class);
		athDt.Columns.Add("FileExts", String.class);
		athDt.Columns.Add("CanDelete", Boolean.class);
		//当前节点的流程数据
		FrmAttachmentDBs frmathdbs = new FrmAttachmentDBs();
		frmathdbs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, "ND" + this.getNodeID() + "_FrmWorkCheck", FrmAttachmentDBAttr.RefPKVal, String.valueOf(this.getWorkID()), "Rec", WebUser.getNo(), FrmAttachmentDBAttr.RDT);

		for (FrmAttachmentDB athDB : frmathdbs.ToJavaList())
		{
			row = athDt.NewRow();
			row.setValue("NodeID", this.getNodeID());
			row.setValue("MyPK", athDB.getMyPK());
			row.setValue("FK_FrmAttachment", athDB.getFKFrmAttachment());
			row.setValue("FK_MapData", athDB.getFrmID());
			row.setValue("FileName", athDB.getFileName());
			row.setValue("FileExts", athDB.getFileExts());
			row.setValue("CanDelete", Objects.equals(athDB.getRec(), WebUser.getNo()) && isReadonly == false);
			athDt.Rows.add(row);
		}
		ds.Tables.add(athDt);

		if (this.getFID() != 0)
		{
			wc = new WorkCheck(this.getFlowNo(), this.getNodeID(), this.getWorkID(), this.getFID(),wcDesc.GetValIntByKey("FWCTimeModel",0));
		}
		else
		{
			wc = new WorkCheck(this.getFlowNo(), this.getNodeID(), this.getWorkID(), 0,wcDesc.GetValIntByKey("FWCTimeModel",0));
		}

		//是否只读？
		if (isReadonly == true)
		{
			isCanDo = false;
		}
		else
		{
			isCanDo = Dev2Interface.Flow_IsCanDoCurrentWork(this.getWorkID(), WebUser.getNo());
		}

		//如果是查看状态, 为了屏蔽掉正在审批的节点, 在查看审批意见中.
		boolean isShowCurrNodeInfo = true;


		if (isCanDo == false && isReadonly == true)
		{
			if (gwf.getWFState() == WFState.Runing && gwf.getNodeID() == this.getNodeID())
			{
				isShowCurrNodeInfo = false;
			}
		}

		/*
		 * 获得当前节点已经审核通过的人员.
		 * 比如：多人处理规则中的已经审核同意的人员，会签人员,组合成成一个字符串。
		 * 格式为: ,zhangsan,lisi,
		 * 用于处理在审核列表中屏蔽临时的保存的审核信息.
		 * 12 为芒果增加一个非正常完成状态.
		 * */
		String checkerPassed = ",";
		if (gwf.getIsOver()==false  && gwf.getWFState().getValue() != 12)
		{
			//Paras ps = new Paras();
			//ps.SQL = "SELECT FK_Emp FROM WF_GenerWorkerlist WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND IsPass=1 AND FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node  Order By RDT,CDT";
			//ps.Add("WorkID", this.WorkID);
			//ps.Add("FK_Node", this.getNodeID());

			String sql = "SELECT EmpFrom as FK_Emp  FROM ND" + Integer.parseInt(this.getFlowNo()) + "Track WHERE WorkID =" + this.getWorkID() + "  AND NDFrom = " + this.getNodeID();
			DataTable checkerPassedDt = DBAccess.RunSQLReturnTable(sql);
			for (DataRow dr : checkerPassedDt.Rows)
			{
				checkerPassed += dr.getValue("FK_Emp") + ",";
			}
		}

		Tracks tracks = new Tracks();
		///#endregion 定义变量.

		///#region 判断是否显示 - 历史审核信息显示
		boolean isDoc = false;
		if (wcDesc.getFWCListEnable() == true)
		{
			tks = wc.getHisWorkChecks();

			for (Track tk : tks.ToJavaList())
			{
				if(tk.getHisActionType()!=ActionType.WorkCheck && tk.getHisActionType()!=ActionType.AutoWorkCheck)
					tracks.add(tk);
				if (tk.getHisActionType() == ActionType.FlowBBS)
				{
					continue;
				}

				if (wcDesc.getFWCMsgShow() == 1 && tk.getEmpFrom().equals(WebUser.getNo()) == false)
				{
					continue;
				}

				switch (tk.getHisActionType())
				{
					case WorkCheck:
					case AutoWorkCheck:
					case Forward:
					case StartChildenFlow:
					case ForwardHL:
					case SubThreadForward:
						if (nodes.contains(tk.getNDFrom() + ",") == false)
						{
							nodes += tk.getNDFrom() + ",";
						}
						break;
					case Return:
					case ReturnAndBackWay:
						if (wcDesc.getFWCIsShowReturnMsg() == 1 && tk.getNDTo() == this.getNodeID())
						{
							if (nodes.contains(tk.getNDFrom() + ",") == false)
							{
								nodes += tk.getNDFrom() + ",";
							}
						}
						if (wcDesc.getFWCIsShowReturnMsg() == 2)
						{
							if (nodes.contains(tk.getNDFrom() + ",") == false)
							{
								nodes += tk.getNDFrom() + ",";
							}
						}
						break;
					default:
						continue;
				}
			}
			int trackIdx = 0;

			for (Track tk : tracks.ToJavaList())
			{
				//倒叙
				if(wc.FWCTimeModel == 0){
					if((trackIdx-1)>=0){
						Track track = (Track)tracks.get(trackIdx-1) ;
						if(track.getHisActionType()==ActionType.UnSend){
							trackIdx++;
							continue;
						}
					}
				}else{
					if(tracks.size()>(trackIdx+1)){
						Track track = (Track)tracks.get(trackIdx+1) ;
						if(track.getHisActionType()==ActionType.UnSend){
							trackIdx++;
							continue;
						}
					}
				}

				trackIdx++;
				if (nodes.contains(tk.getNDFrom() + ",") == false)
				{
					continue;
				}

				//退回
				if (tk.getHisActionType() == ActionType.Return || tk.getHisActionType() == ActionType.ReturnAndBackWay)
				{
					//1.不显示退回意见 2.显示退回意见但是不是退回给本节点的意见
					if (wcDesc.getFWCIsShowReturnMsg() == 0)
					{
						continue;
					}
					if (wcDesc.getFWCIsShowReturnMsg() == 1 && tk.getNDTo() != this.getNodeID())
					{
						continue;
					}
				}

				//  此部分被zhoupeng注释, 在会签的时候显示不到意见。
				// 如果是当前的节点.当前人员可以处理, 已经审批通过的人员.
				if (tk.getNDFrom() == this.getNodeID() && isCanDo == true && tk.getEmpFrom() .equals(WebUser.getNo()) == false && checkerPassed.contains("," + tk.getEmpFrom()  + ",") == false)
				{
					continue;
				}

				if (tk.getNDFrom() == this.getNodeID() && gwf.getHuiQianTaskSta() != HuiQianTaskSta.None)
				{
					//判断会签, 去掉正在审批的节点.
					if (tk.getNDFrom() == this.getNodeID() && isShowCurrNodeInfo == false)
					{
						continue;
					}
				}
				//如果是多人处理，就让其显示已经审核过的意见.
				//if (tk.getNDFrom() == this.getNodeID()&& checkerPassed.IndexOf("," + tk.getEmpFrom()  + ",") < 0 && (gwf.WFState != WFState.CompleteEnd && (int)gwf.WFState != 12))
				//    continue;

				bp.en.Entity tempVar = fwcs.GetEntityByKey(tk.getNDFrom());
				fwc = tempVar instanceof NodeWorkCheck ? (NodeWorkCheck)tempVar : null;
				if (fwc == null || fwc.getFWCSta() != FrmWorkCheckSta.Enable)
				{
					continue;
				}

				//历史审核信息现在存放在流程前进的节点中
				switch (tk.getHisActionType())
				{
					case Forward:
					case ForwardAskfor:
					case Start:
						//case UnSend:
					case ForwardFL:
						//case ForwardHL:
					case SubThreadForward:
					case TeampUp:
					case Return:
					case ReturnAndBackWay:
					case StartChildenFlow:
					case FlowOver:
					case Skip:
					case Order:
						row = tkDt.NewRow();
						row.setValue("MyPK", tk.getMyPK());
						row.setValue("NodeID", tk.getNDFrom());
						row.setValue("NodeName", tk.getNDFromT());
						// zhoupeng 增加了判断，在会签的时候最后会签人发送前不能填写意见.
						if (tk.getNDFrom() == this.getNodeID() && tk.getEmpFrom().equals(WebUser.getNo()) && isCanDo && isDoc == false)
						{
							//修改测试
							isDoc = true;

						}

						row.setValue("IsDoc", false);

						row.setValue("ParentNode", 0);
						row.setValue("RDT", DataType.IsNullOrEmpty(tk.getRDT()) ? "" : tk.getNDFrom() == tk.getNDTo() && DataType.IsNullOrEmpty(tk.getMsg()) ? "" : tk.getRDT());
						//row["T_NodeIndex"] = tk.Row["T_NodeIndex");
						//row["T_CheckIndex"] = tk.Row["T_CheckIndex");

						row.setValue("Msg", tk.getMsgHtml());


						row.setValue("EmpFrom", tk.getEmpFrom() );
						row.setValue("EmpFromT", tk.getEmpFromT() );
						//获取部门
						String DeptName = "";
						String[] Arrays = tk.getNodeData().split("[@]", -1);
						for (String i : Arrays)
						{
							if (i.contains("DeptName="))
							{
								DeptName = i.split("[=]", -1)[1];
							}
						}
						row.setValue("DeptName", DeptName);
						row.setValue("ActionType", tk.getHisActionType().getValue());
						row.setValue("Tag", tk.getTag());

						//获取每个节点的签名方式
						String sql = "SELECT SigantureEnabel FROM WF_Node WHERE NodeID=" + tk.getNDFrom();
						String sigantureEnabel = DBAccess.RunSQLReturnString(sql);
						if (DataType.IsNullOrEmpty(sigantureEnabel) == true)
						{
							sigantureEnabel = "0";
						}
						row.setValue("SigantureEnabel", sigantureEnabel);

						//获取每个节点的附件上传
						sql = "SELECT FWCAth FROM WF_Node WHERE NodeID=" + tk.getNDFrom();
						String fwcAth = DBAccess.RunSQLReturnString(sql);
						if (DataType.IsNullOrEmpty(fwcAth) == true)
						{
							fwcAth = "0";
						}

						//添加执行人，前端须查询执行人的附件
						row.setValue("Exer", tk.getExer());

						row.setValue("FWCAth", fwcAth);

						row.setValue("WritImg", "");
						row.setValue("WriteStamp", "");

						//@0=不签名@1=图片签名@2=写字板@3=电子签名@4=电子盖章@5=电子签名+盖章

						if ("2".equals(sigantureEnabel) == true)  //写字板
							row.setValue("WritImg", DBAccess.GetBigTextFromDB(trackTable, "MyPK", tk.getMyPK(), "WriteDB"));

						if ("4".equals(sigantureEnabel) == true && "5".equals(sigantureEnabel) == true)  //电子盖章  电子签名+盖章
							row.setValue("WriteStamp", DBAccess.GetBigTextFromDB(trackTable, "MyPK", tk.getMyPK(), "FrmDB"));

						tkDt.Rows.add(row);


						///#region 审核组件附件数据

						//athDBs = new FrmAttachmentDBs();
						//QueryObject obj_Ath = new QueryObject(athDBs);
						//obj_Ath.AddWhere(FrmAttachmentDBAttr.FK_FrmAttachment, "ND" + tk.getNDFrom() + "_FrmWorkCheck");
						//obj_Ath.addAnd();
						//obj_Ath.AddWhere(FrmAttachmentDBAttr.RefPKVal, this.WorkID.ToString());
						//obj_Ath.addAnd();
						//obj_Ath.AddWhere(FrmAttachmentDBAttr.Rec, tk.getEmpFrom() );
						//obj_Ath.addOrderBy(FrmAttachmentDBAttr.RDT);
						//obj_Ath.DoQuery();

						//for (FrmAttachmentDB athDB : athDBs)
						//{
						//    row = athDt.NewRow();
						//    row["NodeID"] = tk.getNDFrom();
						//    row["MyPK"] = athDB.getMyPK();
						//    row["FK_FrmAttachment"] = athDB.getFKFrmAttachment();
						//    row["FK_MapData"] = athDB.getFrmID();
						//    row["FileName"] = athDB.getFileName();
						//    row["FileExts"] = athDB.getFileExts();
						//    row["CanDelete"] = athDB.getFrmID() == this.getNodeID().ToString() && athDB.getRec() == WebUser.No && isReadonly == false;
						//    athDt.Rows.add(row);
						//}

						///#endregion


						///#region //子流程的审核组件数据
						if (tk.getFID() != 0 && tk.getHisActionType() == ActionType.StartChildenFlow && tkDt.Select("ParentNode=" + tk.getNDFrom()).length == 0)
						{
							String[] paras = tk.getTag().split("[@]", -1);
							String[] p1 = paras[1].split("[=]", -1);
							String fk_flow = p1[1]; //子流程编号

							String[] p2 = paras[2].split("[=]", -1);
							String workId = p2[1]; //子流程ID.
							int biaoji = 0;

							WorkCheck subwc = new WorkCheck(fk_flow, Integer.parseInt(fk_flow + "01"), Long.parseLong(workId), 0,wcDesc.GetValIntByKey("FWCTimeModel",0));
							String subtrackTable = "ND" + Integer.parseInt(fk_flow) + "Track";
							Tracks subtks = subwc.getHisWorkChecks();
							//取出来子流程的所有的节点。
							Nodes subNds = new Nodes(fk_flow);
							for (Node item : subNds.ToJavaList()) //主要按顺序显示
							{
								for (Track mysubtk : subtks.ToJavaList())
								{
									if (item.getNodeID() != mysubtk.getNDFrom())
									{
										continue;
									}

									/*输出该子流程的审核信息，应该考虑子流程的子流程信息, 就不考虑那样复杂了.*/
									if (mysubtk.getHisActionType() == ActionType.WorkCheck)
									{
										// 发起多个子流程时，发起人只显示一次
										if (mysubtk.getNDFrom() == Integer.parseInt(fk_flow + "01") && biaoji == 1)
										{
											continue;
										}
										NodeWorkCheck subFrmCheck = new NodeWorkCheck("ND" + mysubtk.getNDFrom());
										row = tkDt.NewRow();
										row.setValue("NodeID", mysubtk.getNDFrom());
										row.setValue("NodeName", String.format("(子流程)%1$s", mysubtk.getNDFromT()));
										row.setValue("Msg", mysubtk.getMsgHtml());
										row.setValue("EmpFrom", mysubtk.getEmpFrom() );
										row.setValue("EmpFromT", mysubtk.getEmpFromT() );

										//获取部门
										DeptName = "";
										Arrays = mysubtk.getNodeData().split("[@]", -1);
										for (String i : Arrays)
										{
											if (i.contains("DeptName="))
											{
												DeptName = i.split("[=]", -1)[1];
											}
										}
										row.setValue("DeptName", DeptName);
										row.setValue("RDT", mysubtk.getRDT());
										row.setValue("IsDoc", false);
										row.setValue("ParentNode", tk.getNDFrom());
										// row["T_NodeIndex"] = idx++;
										// row["T_CheckIndex"] = noneEmpIdx++;
										row.setValue("ActionType", mysubtk.getHisActionType().getValue());
										row.setValue("Tag", mysubtk.getTag());
										if (subFrmCheck.getSigantureEnabel() != 2 && subFrmCheck.getSigantureEnabel() != 3 && subFrmCheck.getSigantureEnabel() != 5)
										{
											row.setValue("WritImg", "");
										}
										else
										{
											row.setValue("WritImg", DBAccess.GetBigTextFromDB(subtrackTable, "MyPK", mysubtk.getMyPK(), "WriteDB"));
										}
										if (subFrmCheck.getSigantureEnabel() != 4 && subFrmCheck.getSigantureEnabel() != 5)
										{
											row.setValue("WriteStamp", "");
										}
										else
										{
											row.setValue("WriteStamp", DBAccess.GetBigTextFromDB(subtrackTable, "MyPK", mysubtk.getMyPK(), "FrmDB"));
										}

										tkDt.Rows.add(row);

										if (mysubtk.getNDFrom() == Integer.parseInt(fk_flow + "01"))
										{
											biaoji = 1;
										}
									}
								}
							}
						}

						///#endregion
						break;
					default:
						break;
				}
			}
		}

		///#endregion 判断是否显示 - 历史审核信息显示


		///#region 审核意见默认填写

		//首先判断当前是否有此意见? 如果是退回的该信息已经存在了.
		boolean isHaveMyInfo = false;
		//for (DataRow dr : tkDt.Rows)
		//{
		//    String fk_node = dr["NodeID").toString();
		//    String empFrom = dr["EmpFrom").toString();
		//    if (int.Parse(fk_node) == this.getNodeID() && empFrom == Web.WebUser.getNo())
		//        isHaveMyInfo = true;
		//}

		// 增加默认的审核意见.
		if (isExitTb_doc && wcDesc.getHisFrmWorkCheckSta() == FrmWorkCheckSta.Enable && isCanDo && isReadonly == false && isHaveMyInfo == false)
		{
			DataRow[] rows = null;
			bp.en.Entity tempVar2 = nds.GetEntityByKey(this.getNodeID());
			nd = tempVar2 instanceof Node ? (Node)tempVar2 : null;
			if (wcDesc.getFWCOrderModel() == FWCOrderModel.SqlAccepter)
			{
				rows = tkDt.Select("NodeID=" + this.getFK_Node() + " AND Msg='' AND EmpFrom='" + WebUser.getNo() + "'");
				if (rows.length == 0)
				{
					rows = tkDt.Select("NodeID=" + this.getNodeID() + " AND EmpFrom='" + WebUser.getNo() + "'");
				}

				if (rows.length > 0)
				{
					row = rows[0];
					row.setValue("IsDoc", true);

					row.setValue("Msg", Dev2Interface.GetCheckInfo(this.getFlowNo(), this.getWorkID(), this.getNodeID(), wcDesc.getFWCDefInfo()));
					if (row.getValue("Msg").toString().equals(""))
					{
						row.setValue("RDT", "");
					}

				}
				else
				{
					row = tkDt.NewRow();
					row.setValue("NodeID", this.getNodeID());
					row.setValue("NodeName", nd.getFWCNodeName());
					row.setValue("IsDoc", true);
					row.setValue("ParentNode", 0);
					row.setValue("RDT", Dev2Interface.GetCheckRDT(this.getFlowNo(), this.getWorkID(), this.getNodeID(), WebUser.getNo()));

					row.setValue("Msg", Dev2Interface.GetCheckInfo(this.getFlowNo(), this.getWorkID(), this.getNodeID(), wcDesc.getFWCDefInfo()));
					row.setValue("EmpFrom", WebUser.getNo());
					row.setValue("EmpFromT", WebUser.getName());
					row.setValue("DeptName", WebUser.getDeptName());
					//row["T_NodeIndex"] = ++idx;
					//row["T_CheckIndex"] = ++noneEmpIdx;
					row.setValue("ActionType", ActionType.Forward.getValue());
					row.setValue("Tag", Dev2Interface.GetCheckTag(this.getFlowNo(), this.getWorkID(), this.getNodeID(), WebUser.getNo()));
					if (wcDesc.getSigantureEnabel() == 5)
					{
						String sql = "Select MyPK From " + trackTable + "  WHERE ActionType=" + ActionType.WorkCheck.getValue() + " AND  NDFrom=" + this.getNodeID() + " AND  NDTo=" + this.getNodeID() + " AND WorkID=" + this.getWorkID() + " AND EmpFrom = '" + WebUser.getNo() + "'";
						String mypk = DBAccess.RunSQLReturnStringIsNull(sql, "");
						if (DataType.IsNullOrEmpty(mypk) == false)
						{
							row.setValue("WritImg", DBAccess.GetBigTextFromDB(trackTable, "MyPK", mypk, "WriteDB"));
							row.setValue("WriteStamp", DBAccess.GetBigTextFromDB(trackTable, "MyPK", mypk, "FrmDB"));
						}
						else
						{
							row.setValue("WritImg", "");
							row.setValue("WriteStamp", "");

						}
					}
					if (wcDesc.getSigantureEnabel() == 2 || wcDesc.getSigantureEnabel() == 3)
					{
						String sql = "Select MyPK From " + trackTable + "  WHERE ActionType=" + ActionType.WorkCheck.getValue() + " AND  NDFrom=" + this.getNodeID() + " AND  NDTo=" + this.getNodeID() + " AND WorkID=" + this.getWorkID() + " AND EmpFrom = '" + WebUser.getNo() + "'";
						String mypk = DBAccess.RunSQLReturnStringIsNull(sql, "");
						if (DataType.IsNullOrEmpty(mypk) == false)
						{
							row.setValue("WritImg", DBAccess.GetBigTextFromDB(trackTable, "MyPK", mypk, "WriteDB"));
						}
						else
						{
							row.setValue("WritImg", "");
						}
						row.setValue("WriteStamp", "");
					}
					if (wcDesc.getSigantureEnabel() == 4)
					{
						String sql = "Select MyPK From " + trackTable + "  WHERE ActionType=" + ActionType.WorkCheck.getValue() + " AND  NDFrom=" + this.getNodeID() + " AND  NDTo=" + this.getNodeID() + " AND WorkID=" + this.getWorkID() + " AND EmpFrom = '" + WebUser.getNo() + "'";
						String mypk = DBAccess.RunSQLReturnStringIsNull(sql, "");
						if (DataType.IsNullOrEmpty(mypk) == false)
						{
							row.setValue("WriteStamp", DBAccess.GetBigTextFromDB(trackTable, "MyPK", mypk, "FrmDB"));
						}
						else
						{
							row.setValue("WriteStamp", "");
						}
						row.setValue("WritImg", "");
					}
					if (wcDesc.getSigantureEnabel() == 0 || wcDesc.getSigantureEnabel() == 1)
					{
						row.setValue("WritImg", "");
						row.setValue("WriteStamp", "");
					}
					tkDt.Rows.add(row);
				}
			}
			else
			{
				row = tkDt.NewRow();
				row.setValue("NodeID", this.getNodeID());
				row.setValue("NodeName", nd.getFWCNodeName());
				row.setValue("IsDoc", true);
				row.setValue("ParentNode", 0);
				row.setValue("RDT", Dev2Interface.GetCheckRDT(this.getFlowNo(), this.getWorkID(), this.getNodeID(), WebUser.getNo()));

				row.setValue("Msg", Dev2Interface.GetCheckInfo(this.getFlowNo(), this.getWorkID(), this.getNodeID(), wcDesc.getFWCDefInfo()));
				row.setValue("EmpFrom", WebUser.getNo());
				row.setValue("EmpFromT", WebUser.getName());
				row.setValue("DeptName", WebUser.getDeptName());
				//row["T_NodeIndex"] = ++idx;
				//row["T_CheckIndex"] = ++noneEmpIdx;
				row.setValue("ActionType", ActionType.Forward.getValue());
				row.setValue("Tag", Dev2Interface.GetCheckTag(this.getFlowNo(), this.getWorkID(), this.getNodeID(), WebUser.getNo()));
				if (wcDesc.getSigantureEnabel() != 2)
				{
					row.setValue("WritImg", "");
				}
				else
				{
					String sql = "Select MyPK From " + trackTable + "  WHERE ActionType=" + ActionType.WorkCheck.getValue() + " AND  NDFrom=" + this.getNodeID() + " AND  NDTo=" + this.getNodeID() + " AND WorkID=" + this.getWorkID() + " AND EmpFrom = '" + WebUser.getNo() + "'";
					row.setValue("WritImg", DBAccess.GetBigTextFromDB(trackTable, "MyPK", DBAccess.RunSQLReturnVal(sql) == null ? null : DBAccess.RunSQLReturnVal(sql).toString(), "WriteDB"));
				}
				tkDt.Rows.add(row);
			}
		}

		///#endregion


		///#region 显示有审核组件，但还未审核的节点. 包括退回后的.
		if (tks == null)
		{
			tks = wc.getHisWorkChecks();
		}
		if(wcDesc.getHisFrmWorkCheckSta() == FrmWorkCheckSta.Enable && isCanDo && isReadonly == false){
			WF_WorkOpt_OneWork oneWork = new WF_WorkOpt_OneWork();
			String nodeIDs = oneWork.GetFutureNodeNotContainsEmps();
			for (NodeWorkCheck item : fwcs.ToJavaList())
			{
				if (item.getFWCIsShowTruck() == false)
				{
					continue; //不需要显示历史记录.
				}
				if(nodeIDs.contains(item.getNodeID()+",")==false)
					continue;
				//是否已审核.
				boolean isHave = false;
				for (Track tk : tks.ToJavaList())
				{
					//翻译.
					if (tk.getNDFrom() == this.getNodeID() && tk.getHisActionType() == ActionType.WorkCheck)
					{
						isHave = true; //已经有了
						break;
					}
				}

				if (isHave == true)
				{
					continue;
				}

				row = tkDt.NewRow();
				row.setValue("NodeID", item.getNodeID());

				Node mynd = (Node)nds.GetEntityByKey(item.getNodeID());
				row.setValue("NodeName", mynd.getFWCNodeName());
				row.setValue("IsDoc", false);
				row.setValue("ParentNode", 0);
				row.setValue("RDT", "");
				row.setValue("Msg", "未审批");
				row.setValue("EmpFrom", "");
				row.setValue("EmpFromT", "");
				row.setValue("DeptName", "");
				//row["T_NodeIndex"] = ++idx;
				//row["T_CheckIndex"] = ++noneEmpIdx;

				tkDt.Rows.add(row);
			}
		}


		///#endregion 增加空白.

		ds.Tables.add(tkDt);


		//如果有 SignType 列就获得签名信息.
		if (Objects.equals(SystemConfig.getCustomerNo(), "TianYe"))
		{
			String tTable = "ND" + Integer.parseInt(this.getFlowNo()) + "Track";
			String sql = "SELECT distinct a.No, a.SignType, a.EleID FROM Port_Emp a, " + tTable + " b WHERE (A.No='" + WebUser.getNo() + "') OR B.ActionType=22 AND a.No=b.EmpFrom AND B.WorkID=" + this.getWorkID();

			DataTable dtTrack = DBAccess.RunSQLReturnTable(sql);
			dtTrack.TableName = "SignType";

			dtTrack.Columns.get("NO").ColumnName = "No";
			dtTrack.Columns.get("SIGNTYPE").ColumnName = "SignType";
			dtTrack.Columns.get("ELEID").ColumnName = "EleID";

			ds.Tables.add(dtTrack);
		}

		String str = bp.tools.Json.ToJson(ds);
		//用于jflow数据输出格式对比.
		//  DataType.WriteFile("c:/WorkCheck_Init_ccflow.txt", str);
		return str;
	}
	public final String WorkTrack_Init() throws Exception {
		if (DataType.IsNullOrEmpty(WebUser.getNo()))
			return "err@登录信息丢失,请重新登录.";

		DataSet ds = new DataSet();
		//流程实例信息
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		int curNodeID = this.GetRequestValInt("CCNodeID");
		if(curNodeID ==0)
			curNodeID = this.getNodeID();
		if(curNodeID ==0)
			curNodeID = gwf.getNodeID();
		//审核组件表
		String trackTable = "ND" + Integer.parseInt(this.getFlowNo()) + "Track";
		//当前节点的审核组件
		NodeWorkCheck wcDesc = new NodeWorkCheck(curNodeID);
		ds.Tables.add(wcDesc.ToDataTableField("WF_FrmWorkCheck"));
		//已经运行过的节点
		String runNodes = "";
		if(gwf.getIsOver()==false){
			String sql="SELECT DISTINCT(FK_Node) From(SELECT FK_Node From WF_GenerWorkerList WHERE WorkID="+this.getWorkID()+") A";
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			for(DataRow dr : dt.Rows){
				runNodes+=dr.getValue(0).toString()+",";
			}
		}

		//获取当前节点是否可以审批
		//是不是只读?
		boolean isReadonly = this.GetRequestVal("IsReadonly") != null && this.GetRequestVal("IsReadonly").equals("1") ? true : false;
		//是否可以编辑审核意见
		boolean isCanDo = false;
		if (isReadonly == true)
			isCanDo = false;
		else
			isCanDo = Dev2Interface.Flow_IsCanDoCurrentWork(this.getWorkID(), WebUser.getNo());
		//是否屏蔽正在审批的节点的审批意见(在在途，已完成等查看页面使用)
		boolean isShowCurrNodeInfo = true;
		if (isReadonly == true && gwf.getWFState() == WFState.Runing && gwf.getNodeID() == this.getNodeID())
			isShowCurrNodeInfo = false;

		/*
		 * 获得当前节点已经审核通过的人员.
		 * 比如：多人处理规则中的已经审核同意的人员，会签人员,组合成成一个字符串。
		 * 格式为: ,zhangsan,lisi,
		 * 用于处理在审核列表中屏蔽临时的保存的审核信息.
		 * */
		String checkerPassed = ",";
		if (gwf.getIsOver()==false)
		{
			Paras ps = new Paras();
			ps.SQL = "SELECT FK_Emp FROM WF_GenerWorkerlist WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND IsPass=1 AND FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node Order By RDT,CDT";
			ps.Add("WorkID", this.getWorkID());
			ps.Add("FK_Node", this.getNodeID());
			DataTable checkerPassedDt = DBAccess.RunSQLReturnTable(ps);
			for (DataRow dr : checkerPassedDt.Rows)
			{
				checkerPassed += dr.getValue("FK_Emp") + ",";
			}
		}


		DataRow row = null;
		DataTable dt = null;

		//审核意见的存储集合
		DataTable tkDt = new DataTable("Tracks");
		tkDt.Columns.Add("MyPK", String.class);
		tkDt.Columns.Add("NodeID", Integer.class);
		//从节点来
		tkDt.Columns.Add("NDFrom", Integer.class);
		//到某节点
		tkDt.Columns.Add("NDTo", Integer.class);
		tkDt.Columns.Add("NodeName", String.class);
		tkDt.Columns.Add("Msg", String.class);
		tkDt.Columns.Add("EmpFrom", String.class);
		tkDt.Columns.Add("EmpFromT", String.class);
		tkDt.Columns.Add("EmpTo", String.class);
		tkDt.Columns.Add("EmpToT", String.class);
		tkDt.Columns.Add("DeptName", String.class);
		tkDt.Columns.Add("RDT", String.class);
		tkDt.Columns.Add("ParentNode", Integer.class);
		tkDt.Columns.Add("ActionType", Integer.class);
		tkDt.Columns.Add("Tag", String.class);
		tkDt.Columns.Add("FWCView", String.class);
		tkDt.Columns.Add("WritImg", String.class);
		tkDt.Columns.Add("WriteStamp", String.class);
		tkDt.Columns.Add("SigantureEnabel", Integer.class); //签名方式
		tkDt.Columns.Add("FWCAth", Integer.class); //签名方式
		tkDt.Columns.Add("Exer", String.class); //执行人
		tkDt.Columns.Add("Step", Integer.class);	// 步骤
		//流程附件.
		DataTable athDt = new DataTable("Aths");
		athDt.Columns.Add("NodeID", Integer.class);
		athDt.Columns.Add("MyPK", String.class);
		athDt.Columns.Add("FK_FrmAttachment", String.class);
		athDt.Columns.Add("FK_MapData", String.class);
		athDt.Columns.Add("FileName", String.class);
		athDt.Columns.Add("FileExts", String.class);
		athDt.Columns.Add("CanDelete", Boolean.class);
		//当前节点的流程数据
		FrmAttachmentDBs frmathdbs = new FrmAttachmentDBs();
		frmathdbs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, "ND" + this.getNodeID() + "_FrmWorkCheck", FrmAttachmentDBAttr.RefPKVal, String.valueOf(this.getWorkID()), "Rec", WebUser.getNo(), FrmAttachmentDBAttr.RDT);

		for (FrmAttachmentDB athDB : frmathdbs.ToJavaList())
		{
			row = athDt.NewRow();
			row.setValue("NodeID", this.getNodeID());
			row.setValue("MyPK", athDB.getMyPK());
			row.setValue("FK_FrmAttachment", athDB.getFKFrmAttachment());
			row.setValue("FK_MapData", athDB.getFrmID());
			row.setValue("FileName", athDB.getFileName());
			row.setValue("FileExts", athDB.getFileExts());
			row.setValue("CanDelete", Objects.equals(athDB.getRec(), WebUser.getNo()) && isReadonly == false);
			athDt.Rows.add(row);
		}
		ds.Tables.add(athDt);
		//审核组件的定义
		WorkCheck wc = null;
		if (this.getFID() != 0)
			wc = new WorkCheck(this.getFlowNo(), this.getNodeID(), this.getWorkID(), this.getFID(),wcDesc.GetValIntByKey("FWCTimeModel",0));
		else
			wc = new WorkCheck(this.getFlowNo(), this.getNodeID(), this.getWorkID(), 0,wcDesc.GetValIntByKey("FWCTimeModel",0));

		//获取运行过的所有历史审核信息
		StringBuilder nodes = new StringBuilder();
		Tracks tks = wc.getHisWorkChecks();
		Tracks tracks = new Tracks();
		for (Track tk : tks.ToJavaList()) {
			ActionType actionType = tk.getHisActionType();
			if(actionType != ActionType.WorkCheck && actionType != ActionType.AutoWorkCheck)
				tracks.add(tk);
			if (actionType == ActionType.FlowBBS)
				continue;
			if (wcDesc.getFWCMsgShow() == 1 && !tk.getEmpFrom().equals(WebUser.getNo()))
				continue;
			switch (actionType) {
				case WorkCheck:
				case AutoWorkCheck:
				case Forward:
				case StartChildenFlow:
				case ForwardHL:
				case Return:
				case ReturnAndBackWay:
				case CC:
				case Skip:
				case FlowOver:
					if(actionType == ActionType.CC && tk.getNDFrom()!=tk.getNDTo()){
						if (!nodes.toString().contains(tk.getNDTo() + ","))
							nodes.append(tk.getNDTo()).append(",");
					}
					if (!nodes.toString().contains(tk.getNDFrom() + ","))
						nodes.append(tk.getNDFrom()).append(",");
					break;
			}
		}

		int trackIdx = 0;
		String curTrackEmpNos =",";
		for (Track tk : tracks.ToJavaList())
		{
			ActionType actionType = tk.getHisActionType();
			//倒叙
			if(wc.FWCTimeModel == 0){
				if((trackIdx-1)>=0){
					Track track = (Track)tracks.get(trackIdx-1) ;
					if(track.getHisActionType()==ActionType.UnSend){
						trackIdx++;
						continue;
					}
				}
			} else {
				if(tracks.size()>(trackIdx+1)){
					Track track = (Track)tracks.get(trackIdx+1) ;
					if(track.getHisActionType()==ActionType.UnSend){
						trackIdx++;
						continue;
					}
				}
			}

			trackIdx++;
			if (!nodes.toString().contains(tk.getNDFrom() + ","))
				continue;
			//退回
//			if (actionType == ActionType.Return || actionType == ActionType.ReturnAndBackWay)
//			{
//				//0.不显示退回意见
//				if (wcDesc.getFWCIsShowReturnMsg() == 0)
//					continue;
//				//1.退回到的节点显示
//				//zsy5.23修改为无论任何节点都正常展示
//				if (wcDesc.getFWCIsShowReturnMsg() == 1 && tk.getNDTo() != this.getNodeID())
//					continue;
//			}

			// 如果是当前的节点.当前人员可以处理, 已经审批通过的人员.
			if (actionType !=ActionType.Shift && actionType !=ActionType.Return && actionType !=ActionType.ReturnAndBackWay && tk.getNDFrom() == this.getNodeID() && isCanDo == true && tk.getEmpFrom() .equals(WebUser.getNo()) == false && checkerPassed.contains("," + tk.getEmpFrom()  + ",") == false)
				continue;

			if (tk.getNDFrom() == this.getNodeID() && gwf.getHuiQianTaskSta() != HuiQianTaskSta.None)
			{
				//判断会签, 去掉正在审批的节点.
				if (tk.getNDFrom() == this.getNodeID() && isShowCurrNodeInfo == false)
					continue;
			}

			//手动抄送也需要展示track信息
//			if(tk.getHisActionType() == ActionType.CC && tk.getNDFrom()==tk.getNDTo())
//				continue;
			//历史审核信息现在存放在流程前进的节点中
			switch (actionType)
			{
				case AutoWorkCheck:
				case Forward:
				case ForwardAskfor:
				case Start:
				case ForwardHL:
				case SubThreadForward:
				case TeampUp:
				case Return:
				case ReturnAndBackWay:
				case StartChildenFlow:
				case FlowOver:
				case Order:
				case CC:
				case Skip:
				case Shift:
				case RebackOverFlow:
				case Adjust:
					if(tk.getNDFrom() == curNodeID && actionType != ActionType.Return && actionType != ActionType.ReturnAndBackWay)
						curTrackEmpNos += tk.getEmpTo()+",";
					row = tkDt.NewRow();
					row.setValue("MyPK", tk.getMyPK());
					row.setValue("NDFrom",tk.getNDFrom());
					row.setValue("NDTo",tk.getNDTo());
					if(tk.getHisActionType() == ActionType.CC){
						row.setValue("NodeID", tk.getNDTo());
						row.setValue("NodeName", tk.getNDToT());
						row.setValue("EmpFrom", tk.getEmpTo() );
						row.setValue("EmpFromT", tk.getEmpToT() );
						row.setValue("EmpTo", tk.getEmpTo() );
						row.setValue("EmpToT", tk.getEmpToT() );
					}else{
						row.setValue("NodeID", tk.getNDFrom());
						row.setValue("NodeName", tk.getNDFromT());
						row.setValue("EmpFrom", tk.getEmpFrom() );
						row.setValue("EmpFromT", tk.getEmpFromT() );
						row.setValue("EmpTo", tk.getEmpTo() );
						row.setValue("EmpToT", tk.getEmpToT() );
					}
					row.setValue("ParentNode", 0);
					row.setValue("RDT", DataType.IsNullOrEmpty(tk.getRDT()) ? "" : tk.getNDFrom() == tk.getNDTo() && DataType.IsNullOrEmpty(tk.getMsg()) ? "" : tk.getRDT());
					row.setValue("Msg", tk.getMsgHtml());

					//获取部门
					String DeptName = "";
					String[] Arrays = tk.getNodeData().split("[@]", -1);
					for (String i : Arrays)
					{
						if (i.contains("DeptName="))
						{
							DeptName = i.split("[=]", -1)[1];
						}
					}
					row.setValue("DeptName", DeptName);
					row.setValue("ActionType", tk.getHisActionType().getValue());
					row.setValue("Tag", tk.getTag());


					//获取每个节点的签名方式
					String sql = "SELECT SigantureEnabel FROM WF_Node WHERE NodeID=" + tk.getNDFrom();
					String sigantureEnabel = DBAccess.RunSQLReturnString(sql);
					if (DataType.IsNullOrEmpty(sigantureEnabel) == true)
					{
						sigantureEnabel = "0";
					}
					row.setValue("SigantureEnabel", sigantureEnabel);

					//获取每个节点的附件上传
					sql = "SELECT FWCAth FROM WF_Node WHERE NodeID=" + tk.getNDFrom();
					String fwcAth = DBAccess.RunSQLReturnString(sql);
					if (DataType.IsNullOrEmpty(fwcAth) == true)
					{
						fwcAth = "0";
					}

					//添加执行人，前端须查询执行人的附件
					row.setValue("Exer", tk.getExer());

					row.setValue("FWCAth", fwcAth);

					row.setValue("WritImg", "");
					row.setValue("WriteStamp", "");

					//@0=不签名@1=图片签名@2=写字板@3=电子签名@4=电子盖章@5=电子签名+盖章

					if ("2".equals(sigantureEnabel))  //写字板
						row.setValue("WritImg", DBAccess.GetBigTextFromDB(trackTable, "MyPK", tk.getMyPK(), "WriteDB"));

					if ("4".equals(sigantureEnabel) || "5".equals(sigantureEnabel))  //电子盖章  电子签名+盖章
						row.setValue("WriteStamp", DBAccess.GetBigTextFromDB(trackTable, "MyPK", tk.getMyPK(), "FrmDB"));
					if(tk.getHisActionType() == ActionType.Skip){
						//如果是跳转，当前节点的功能是发送成功，下一个节点被跳转
						row.setValue("ActionType",ActionType.Forward.getValue());

						DataRow nextrow = tkDt.NewRow();
						for(DataColumn col :tkDt.Columns){
							nextrow.setValue(col.ColumnName,row.getValue(col.ColumnName));
						}
						row.setValue("ActionType", ActionType.Skip.getValue());
						row.setValue("NDFrom",tk.getNDTo());
						row.setValue("NDTo",tk.getNDTo());
						row.setValue("NodeID", tk.getNDTo());
						row.setValue("NodeName", tk.getNDToT());
						row.setValue("EmpFrom", tk.getEmpTo() );
						row.setValue("EmpFromT", tk.getEmpToT() );
						row.setValue("EmpTo", tk.getEmpTo() );
						row.setValue("EmpToT", tk.getEmpToT() );
						tkDt.Rows.add(nextrow);
						tkDt.Rows.add(row);
						continue;
					}


					tkDt.Rows.add(row);

					///#region //子流程的审核组件数据
					if (tk.getFID() != 0 && tk.getHisActionType() == ActionType.StartChildenFlow && tkDt.Select("ParentNode=" + tk.getNDFrom()).length == 0)
					{
						String[] paras = tk.getTag().split("[@]", -1);
						String[] p1 = paras[1].split("[=]", -1);
						String fk_flow = p1[1]; //子流程编号

						String[] p2 = paras[2].split("[=]", -1);
						String workId = p2[1]; //子流程ID.
						int biaoji = 0;

						WorkCheck subwc = new WorkCheck(fk_flow, Integer.parseInt(fk_flow + "01"), Long.parseLong(workId), 0,wcDesc.GetValIntByKey("FWCTimeModel",0));
						String subtrackTable = "ND" + Integer.parseInt(fk_flow) + "Track";
						Tracks subtks = subwc.getHisWorkChecks();
						//取出来子流程的所有的节点。
						Nodes subNds = new Nodes(fk_flow);
						for (Node item : subNds.ToJavaList()) //主要按顺序显示
						{
							for (Track mysubtk : subtks.ToJavaList())
							{
								if (item.getNodeID() != mysubtk.getNDFrom())
									continue;

								/*输出该子流程的审核信息，应该考虑子流程的子流程信息, 就不考虑那样复杂了.*/
								if (mysubtk.getHisActionType() == ActionType.WorkCheck)
								{
									// 发起多个子流程时，发起人只显示一次
									if (mysubtk.getNDFrom() == Integer.parseInt(fk_flow + "01") && biaoji == 1)
									{
										continue;
									}
									NodeWorkCheck subFrmCheck = new NodeWorkCheck("ND" + mysubtk.getNDFrom());
									row = tkDt.NewRow();
									row.setValue("NodeID", mysubtk.getNDFrom());
									row.setValue("NodeName", String.format("(子流程)%1$s", mysubtk.getNDFromT()));
									row.setValue("Msg", mysubtk.getMsgHtml());
									row.setValue("EmpFrom", mysubtk.getEmpFrom() );
									row.setValue("EmpFromT", mysubtk.getEmpFromT() );
									row.setValue("EmpTo", mysubtk.getEmpTo() );
									row.setValue("EmpToT", mysubtk.getEmpToT() );

									//获取部门
									DeptName = "";
									Arrays = mysubtk.getNodeData().split("[@]", -1);
									for (String i : Arrays)
									{
										if (i.contains("DeptName="))
										{
											DeptName = i.split("[=]", -1)[1];
										}
									}
									row.setValue("DeptName", DeptName);
									row.setValue("RDT", mysubtk.getRDT());
									row.setValue("ParentNode", tk.getNDFrom());
									row.setValue("ActionType", mysubtk.getHisActionType().getValue());
									row.setValue("Tag", mysubtk.getTag());
									if (subFrmCheck.getSigantureEnabel() != 2 && subFrmCheck.getSigantureEnabel() != 3 && subFrmCheck.getSigantureEnabel() != 5)
									{
										row.setValue("WritImg", "");
									}
									else
									{
										row.setValue("WritImg", DBAccess.GetBigTextFromDB(subtrackTable, "MyPK", mysubtk.getMyPK(), "WriteDB"));
									}
									if (subFrmCheck.getSigantureEnabel() != 4 && subFrmCheck.getSigantureEnabel() != 5)
									{
										row.setValue("WriteStamp", "");
									}
									else
									{
										row.setValue("WriteStamp", DBAccess.GetBigTextFromDB(subtrackTable, "MyPK", mysubtk.getMyPK(), "FrmDB"));
									}

									tkDt.Rows.add(row);

									if (mysubtk.getNDFrom() == Integer.parseInt(fk_flow + "01"))
									{
										biaoji = 1;
									}
								}
							}
						}
					}
					break;
				default:
					break;
			}
		}
		Node nodeInfo = new Node(curNodeID);
		String empNos = "";
		String empNames = "";
		//增加当前审核的节点
		if(gwf.getIsOver()==false){

			//if(wcDesc.getHisFrmWorkCheckSta() == FrmWorkCheckSta.Enable && isCanDo && isReadonly == false){
			row = tkDt.NewRow();
			row.setValue("NodeID", nodeInfo.getNodeID());
			row.setValue("NDFrom",  "0");
			row.setValue("NodeName", nodeInfo.getFWCNodeName());
			row.setValue("ParentNode", 0);
			row.setValue("RDT", "");
			row.setValue("Msg", "审批中");
			String todoEmps = gwf.getTodoEmps();
			if(todoEmps.contains(";")==false){
				String[] strs = todoEmps.split(",");
				empNos = strs[0];
				if(strs.length>1) empNames=strs[1];

			}else{
				String regex = "(.*?),(.*?)(?=;)";

				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(todoEmps);

				while (matcher.find()) {
					if(curTrackEmpNos.contains(","+matcher.group(1)+",") == false){
						empNos+= matcher.group(1)+",";
						empNames+= matcher.group(2)+",";
					}
				}
				if(empNos.endsWith(","))  empNos = empNos.substring(0,empNos.length()-1);
				if(empNames.endsWith(","))  empNames = empNames.substring(0,empNames.length()-1);
			}
			row.setValue("EmpFrom",empNos);
			row.setValue("EmpFromT", empNames);
			row.setValue("EmpTo", empNos);
			row.setValue("EmpToT", empNames);
			row.setValue("DeptName", "");
			if(nodeInfo.getItIsStartNode())
				row.setValue("ActionType", ActionType.Forward.getValue());
			if(wcDesc.GetValIntByKey("FWCTimeModel",0)==0)
				tkDt.Rows.add(0,row);
			else
				tkDt.Rows.add(row);
		}

		//获取未来运算到的节点
		if(gwf.getIsOver()==false && wcDesc.getFWCIsShowTruck() == true){
			Nodes nds = new Nodes(this.getFlowNo());
			WF_WorkOpt_OneWork oneWork = new WF_WorkOpt_OneWork();
			String nodeIDs = oneWork.GetFutureNodeNotContainsEmps();
			SelectAccpers sas = new SelectAccpers();
			sas.Retrieve(SelectAccperAttr.WorkID,this.getWorkID());

			List<String> idsList = new ArrayList<>(Arrays.asList(nodeIDs.split(",")));
			List<String> runIds = new ArrayList<>(Arrays.asList(runNodes.split(",")));
			//MyView增加当前节点
			if(isReadonly){
				String nd = String.valueOf(this.getNodeID());
				if(!idsList.contains(nd)){
					idsList.add(nd);
				}
				if(runIds.contains(nd)){
					runIds.remove(nd);
				}
				//未来节点集合排序
				//Collections.sort(idsList);
			}

			SelectAccpers csa = null;
			for(String nodeID : idsList){
				if(nodeID.equals(String.valueOf(nodeInfo.getNodeID()))){
					continue;
				}
				if(DataType.IsNullOrEmpty(nodeID) == true)
					continue;
				//是否已审核.
				if(runIds.contains(nodeID) == true)
					continue;
				//获取处理人
				Entities ens = sas.GetEntitiesByKey(SelectAccperAttr.FK_Node,nodeID);
				empNos="";
				empNames="";
				if(ens!=null){
					csa = (SelectAccpers) ens;
					for(SelectAccper sa :csa.ToJavaList()){
						empNos +=sa.getEmpNo()+",";
						empNames +=sa.getEmpName()+",";
					}
					empNos = empNos.substring(0, empNos.length()-1);
					empNames = empNames.substring(0, empNames.length()-1);
				}
				if(DataType.IsNullOrEmpty(empNos)) {
					empNos ="手工选择处理人";
					empNames="手工选择处理人";
				}
				row = tkDt.NewRow();
				row.setValue("NodeID", nodeID);
				row.setValue("NDFrom",  "0");
				Node mynd = (Node)nds.GetEntityByKey(nodeID);
				row.setValue("NodeName", mynd.getFWCNodeName());
				row.setValue("ParentNode", 0);
				row.setValue("RDT", "");
				row.setValue("Msg", "未审批");
				row.setValue("EmpFrom", empNos);
				row.setValue("EmpFromT", empNames);
				row.setValue("EmpTo", "");
				row.setValue("EmpToT", "");
				row.setValue("DeptName", "");
				if(mynd.getHisNodeType()==NodeType.CCNode)
					row.setValue("ActionType", ActionType.CC.getValue());
				if(wcDesc.GetValIntByKey("FWCTimeModel",0)==0)
					tkDt.Rows.add(0,row);
				else
					tkDt.Rows.add(row);
			}
		}
		ds.Tables.add(tkDt);

		//判断当前节点的是否可以审批
		if(wcDesc.getHisFrmWorkCheckSta() == FrmWorkCheckSta.Enable && isCanDo && isReadonly == false){
			DataTable dtt = tkDt.clone();
			row = dtt.NewRow();
			row.setValue("NodeID", this.getNodeID());
			row.setValue("NodeName", nodeInfo.getFWCNodeName());
			row.setValue("IsDoc", true);
			row.setValue("ParentNode", 0);
			row.setValue("RDT", "");

			row.setValue("Msg", Dev2Interface.GetCheckInfo(this.getFlowNo(), this.getWorkID(), this.getNodeID(), wcDesc.getFWCDefInfo()));
			row.setValue("EmpFrom", WebUser.getNo());
			row.setValue("EmpFromT", WebUser.getName());
			row.setValue("EmpTo", WebUser.getNo());
			row.setValue("EmpToT", WebUser.getName());
			row.setValue("DeptName", WebUser.getDeptName());
			row.setValue("ActionType", ActionType.Forward.getValue());
			row.setValue("Tag", Dev2Interface.GetCheckTag(this.getFlowNo(), this.getWorkID(), this.getNodeID(), WebUser.getNo()));
			if (wcDesc.getSigantureEnabel() == 5)
			{
				String sql = "Select MyPK From " + trackTable + "  WHERE ActionType=" + ActionType.WorkCheck.getValue() + " AND  NDFrom=" + this.getNodeID() + " AND WorkID=" + this.getWorkID() + " AND EmpFrom = '" + WebUser.getNo() + "'";
				String mypk = DBAccess.RunSQLReturnStringIsNull(sql, "");
				if (DataType.IsNullOrEmpty(mypk) == false)
				{
					row.setValue("WritImg", DBAccess.GetBigTextFromDB(trackTable, "MyPK", mypk, "WriteDB"));
					row.setValue("WriteStamp", DBAccess.GetBigTextFromDB(trackTable, "MyPK", mypk, "FrmDB"));
				}
				else
				{
					row.setValue("WritImg", "");
					row.setValue("WriteStamp", "");

				}
			}
			if (wcDesc.getSigantureEnabel() == 2 || wcDesc.getSigantureEnabel() == 3)
			{
				String sql = "Select MyPK From " + trackTable + "  WHERE ActionType=" + ActionType.WorkCheck.getValue() + " AND  NDFrom=" + this.getNodeID() + " AND WorkID=" + this.getWorkID() + " AND EmpFrom = '" + WebUser.getNo() + "'";
				String mypk = DBAccess.RunSQLReturnStringIsNull(sql, "");
				if (DataType.IsNullOrEmpty(mypk) == false)
				{
					row.setValue("WritImg", DBAccess.GetBigTextFromDB(trackTable, "MyPK", mypk, "WriteDB"));
				}
				else
				{
					row.setValue("WritImg", "");
				}
				row.setValue("WriteStamp", "");
			}
			if (wcDesc.getSigantureEnabel() == 4)
			{
				String sql = "Select MyPK From " + trackTable + "  WHERE ActionType=" + ActionType.WorkCheck.getValue() + " AND  NDFrom=" + this.getNodeID() + " AND WorkID=" + this.getWorkID() + " AND EmpFrom = '" + WebUser.getNo() + "'";
				String mypk = DBAccess.RunSQLReturnStringIsNull(sql, "");
				if (DataType.IsNullOrEmpty(mypk) == false)
				{
					row.setValue("WriteStamp", DBAccess.GetBigTextFromDB(trackTable, "MyPK", mypk, "FrmDB"));
				}
				else
				{
					row.setValue("WriteStamp", "");
				}
				row.setValue("WritImg", "");
			}
			if (wcDesc.getSigantureEnabel() == 0 || wcDesc.getSigantureEnabel() == 1)
			{
				row.setValue("WritImg", "");
				row.setValue("WriteStamp", "");
			}
			dtt.Rows.add(row);
			dtt.TableName="MyTrack";
			ds.Tables.add(dtt);
		}

		DataRowCollection rowList = tkDt.Rows;
		Map<Object,Integer> nodeSort = new HashMap<>();
		int _idx = 0;
		List<Integer> filterActionType= new ArrayList<>();
		filterActionType.add(2);
		filterActionType.add(21);
		filterActionType.add(201);
		for(DataRow dataRow : rowList){
			Object actionType = dataRow.getValue("ActionType");
			if(filterActionType.contains(actionType)){
				continue;
			}
			Object nodeId = dataRow.getValue("NodeID");
			Integer idx = nodeSort.get(nodeId);
			if(idx != null) {
				dataRow.setValue("Step", idx);
			} else {
				nodeSort.put(nodeId, _idx);
				dataRow.setValue("Step", _idx);
				_idx++;
			}
		}

		String str = bp.tools.Json.ToJson(ds);
		return str;
	}
	/**
	 获取审核组件中刚上传的附件列表信息
	 @return
	 */
	public final String WorkCheck_GetNewUploadedAths() throws Exception {
		DataRow row = null;
		String athNames = GetRequestVal("Names");
		String attachPK = GetRequestVal("AttachPK");

		DataTable athDt = new DataTable("Aths");
		athDt.Columns.Add("NodeID", Integer.class);
		athDt.Columns.Add("MyPK", String.class);
		athDt.Columns.Add("FK_FrmAttachment", String.class);
		athDt.Columns.Add("FK_MapData", String.class);
		athDt.Columns.Add("FileName", String.class);
		athDt.Columns.Add("FileExts", String.class);
		athDt.Columns.Add("CanDelete", String.class);
		athDt.Columns.Add("Rec", String.class);
		FrmAttachmentDBs athDBs = new FrmAttachmentDBs();
		QueryObject obj_Ath = new QueryObject(athDBs);
		obj_Ath.AddWhere(FrmAttachmentDBAttr.FK_FrmAttachment, "ND" + this.getNodeID() + "_FrmWorkCheck");
		obj_Ath.addAnd();
		obj_Ath.AddWhere(FrmAttachmentDBAttr.RefPKVal, String.valueOf(this.getWorkID()));
		String exer = this.GetRequestVal("Exer");
		if(DataType.IsNullOrEmpty(exer) == false){
			obj_Ath.addAnd();
			obj_Ath.AddWhere(FrmAttachmentDBAttr.Rec, this.GetRequestVal("Exer"));
		}
		obj_Ath.addOrderBy(FrmAttachmentDBAttr.RDT);
		obj_Ath.DoQuery();

		for (FrmAttachmentDB athDB : athDBs.ToJavaList())
		{
			if (athNames != null && athNames.toLowerCase().indexOf("|" + athDB.getFileName().toLowerCase() + "|") == -1)
			{
				continue;
			}

			row = athDt.NewRow();

			row.setValue("NodeID", this.getNodeID());
			row.setValue("MyPK", athDB.getMyPK());
			row.setValue("FK_FrmAttachment", athDB.getFKFrmAttachment());
			row.setValue("FK_MapData", athDB.getFrmID());
			row.setValue("FileName", athDB.getFileName());
			row.setValue("FileExts", athDB.getFileExts());
			row.setValue("CanDelete", Objects.equals(athDB.getRec(), WebUser.getNo()) ? 1 : 0);
			row.setValue("Rec", athDB.getRec());
			athDt.Rows.add(row);
		}

		return bp.tools.Json.ToJson(athDt);
	}
	/**
	 获取附件链接

	 @param athDB
	 @return
	 */
	private String GetFileAction(FrmAttachmentDB athDB) throws Exception {
		if (athDB == null || Objects.equals(athDB.getFileExts(), ""))
		{
			return "#";
		}

		FrmAttachment athDesc = new FrmAttachment(athDB.getFKFrmAttachment());
		switch (athDB.getFileExts())
		{
			case "doc":
			case "docx":
			case "xls":
			case "xlsx":
				return "javascript:AthOpenOfiice('" + athDB.getFKFrmAttachment() + "','" + this.getWorkID() + "','" + athDB.getMyPK() + "','" + athDB.getFrmID() + "','" + athDB.getFKFrmAttachment() + "','" + this.getNodeID() + "')";
			case "txt":
			case "jpg":
			case "jpeg":
			case "gif":
			case "png":
			case "bmp":
			case "ceb":
				return "javascript:AthOpenView('" + athDB.getRefPKVal() + "','" + athDB.getMyPK() + "','" + athDB.getFKFrmAttachment() + "','" + athDB.getFileExts() + "','" + this.getFlowNo() + "','" + athDB.getFrmID() + "','" + this.getWorkID() + "','false')";
			case "pdf":
				return athDesc.getSaveTo() + this.getWorkID() + "/" + athDB.getMyPK() + "." + athDB.getFileName();
		}

		return "javascript:AthDown('" + athDB.getFKFrmAttachment() + "','" + this.getWorkID() + "','" + athDB.getMyPK() + "','" + athDB.getFrmID() + "','" + this.getFlowNo() + "','" + athDB.getFKFrmAttachment() + "')";
	}

	/**
	 审核信息保存.

	 @return
	 */
	public final String WorkCheck_Save() throws Exception {
		//设计的时候,workid=0,不让其存储.
		if (this.getWorkID() == 0)
		{
			return "";
		}

		// 审核信息.
		String msg = "";
		String writeImg = GetRequestVal("WriteImg");
		String handlerName = GetRequestVal("HandlerName");
		//if (DataType.IsNullOrEmpty(writeImg) == false)
		//   writeImg = HttpUtility.UrlDecode(writeImg, Encoding.UTF8);//writeImg.replace('~', '+');
		String writeStamp = GetRequestVal("WriteStamp");
		String dotype = GetRequestVal("ShowType");
		String doc = GetRequestVal("Doc");
		boolean isCC = Objects.equals(GetRequestVal("IsCC"), "1");
		String checkTime = DataType.getCurrentDateTimess();
		if (DataType.IsNullOrEmpty(checkTime) == true) checkTime = "";

		String fwcView = null;
		if (DataType.IsNullOrEmpty(GetRequestVal("FWCView")) == false)
		{
			fwcView = "@FWCView=" + GetRequestVal("FWCView").trim();
		}
		//查看时取消保存
		if (dotype != null && Objects.equals(dotype, "View"))
		{
			return "";
		}

		//内容为空，取消保存，20170727取消此处限制
		//if (DataType.IsNullOrEmpty(doc.Trim()))
		//    return "";

		String val = "";
		NodeWorkCheck wcDesc = new NodeWorkCheck(this.getNodeID());
		if (DataType.IsNullOrEmpty(wcDesc.getFWCFields()) == false)
		{
			//循环属性获取值
			Attrs fwcAttrs = new Attrs();
			// Attrs fwcAttrs = new Attrs(wcDesc.FWCFields);

			for (Attr attr : fwcAttrs)
			{
				if (attr.getUIContralType() == UIContralType.TB)
				{
					val = GetRequestVal("TB_" + attr.getKey());

					msg += attr.getKey() + "=" + val + ";";
				}
				else if (attr.getUIContralType() == UIContralType.CheckBok)
				{
					val = GetRequestVal("CB_" + attr.getKey());

					msg += attr.getKey() + "=" + Integer.parseInt(val) + ";";
				}
				else if (attr.getUIContralType() == UIContralType.DDL)
				{
					val = GetRequestVal("DDL_" + attr.getKey());

					msg += attr.getKey() + "=" + val + ";";
				}
			}
		}
		else
		{
			// 加入审核信息.
			msg = doc;
		}

		//在审核人打开后，申请人撤销，就不不能让其保存.(在途页面不做判断)
		String sql = "SELECT FK_Node FROM WF_GenerWorkFlow WHERE WorkID=" + this.getWorkID();
		if (DBAccess.RunSQLReturnValInt(sql) != this.getNodeID() && handlerName.indexOf("WF_MyView") == -1)
		{
			return "err@当前工作已经被撤销或者已经移动到下一个节点您不能在执行审核.";
		}

		// 处理人大的需求，需要把审核意见写入到FlowNote里面去.
		sql = "UPDATE WF_GenerWorkFlow SET FlowNote='" + msg + "' WHERE WorkID=" + this.getWorkID();
		DBAccess.RunSQL(sql);

		// 判断是否是抄送?
		if (isCC)
		{
			// 写入审核信息，有可能是update数据。
			Dev2Interface.Node_WriteWorkCheck(this.getWorkID(), msg, wcDesc.getFWCOpLabel(), wcDesc.getSigantureEnabel() == 2 ? writeImg : "",null, checkTime);

			//设置抄送状态 - 已经审核完毕.
			Dev2Interface.Node_CC_SetSta(this.getNodeID(), this.getWorkID(), WebUser.getNo(), CCSta.CheckOver);

			sql = "SELECT MyPK,RDT FROM ND" + Integer.parseInt(this.getFlowNo()) + "Track WHERE WorkID=" + this.getWorkID() + " AND NDFrom = " + this.getNodeID() + " AND ActionType = " + ActionType.WorkCheck.getValue() + " AND EmpFrom = '" + WebUser.getNo() + "'";
			DataTable dtt = DBAccess.RunSQLReturnTable(sql, 1, 1, "MyPK", "RDT", "DESC");
			//判断是不是签章
			if (DataType.IsNullOrEmpty(writeStamp) == false)
			{
				//writeStamp = HttpUtility.UrlDecode(writeStamp, Encoding.UTF8);// writeStamp.replace('~', '+');
				String mypk = dtt.Rows.size() > 0 ? dtt.Rows.get(0).getValue("MyPK").toString() : "";
				if (DataType.IsNullOrEmpty(mypk) == false)
				{
					DBAccess.SaveBigTextToDB(writeStamp, "ND" + Integer.parseInt(this.getFlowNo()) + "Track", "MyPK", mypk, "FrmDB");
				}
			}
			return dtt.Rows.size() > 0 ? dtt.Rows.get(0).getValue("RDT").toString() : "";
		}


		///#region 根据类型写入数据  qin
		if (wcDesc.getHisFrmWorkCheckType() == FWCType.Check) //审核组件
		{
			//判断是否审核组件中"协作模式下操作员显示顺序”设置为"按照接受人员列表先后顺序(官职大小)"，删除原有的空审核信息
			if (wcDesc.getFWCOrderModel() == FWCOrderModel.SqlAccepter)
			{
				sql = "DELETE FROM ND" + Integer.parseInt(this.getFlowNo()) + "Track WHERE WorkID = " + this.getWorkID() + " AND ActionType = " + ActionType.WorkCheck.getValue() + " AND NDFrom = " + this.getNodeID() + " AND NDTo = " + this.getNodeID() + " AND EmpFrom = '" + WebUser.getNo() + "'";
				DBAccess.RunSQL(sql);
			}

			Dev2Interface.Node_WriteWorkCheck(this.getWorkID(), msg, wcDesc.getFWCOpLabel(), wcDesc.getSigantureEnabel() == 2 || wcDesc.getSigantureEnabel() == 3 || wcDesc.getSigantureEnabel() == 5 ? writeImg : "", fwcView, checkTime);
		}

		if (wcDesc.getHisFrmWorkCheckType() == FWCType.DailyLog) //日志组件
		{
			Dev2Interface.WriteTrackDailyLog(this.getFlowNo(), this.getNodeID(), wcDesc.getName(), this.getWorkID(), this.getFID(), msg, wcDesc.getFWCOpLabel());
		}
		if (wcDesc.getHisFrmWorkCheckType() == FWCType.WeekLog) //周报
		{
			Dev2Interface.WriteTrackWeekLog(this.getFlowNo(), this.getNodeID(), wcDesc.getName(), this.getWorkID(), this.getFID(), msg, wcDesc.getFWCOpLabel());
		}
		if (wcDesc.getHisFrmWorkCheckType() == FWCType.MonthLog) //月报
		{
			Dev2Interface.WriteTrackMonthLog(this.getFlowNo(), this.getNodeID(), wcDesc.getName(), this.getWorkID(), this.getFID(), msg, wcDesc.getFWCOpLabel());
		}

		///#endregion

		sql = "SELECT MyPK,RDT FROM ND" + Integer.parseInt(this.getFlowNo()) + "Track WHERE WorkID=" + this.getWorkID() + " AND NDFrom = " + this.getNodeID() + " AND ActionType = " + ActionType.WorkCheck.getValue() + " AND EmpFrom = '" + WebUser.getNo() + "'";
		DataTable dt = DBAccess.RunSQLReturnTable(sql, 1, 1, "MyPK", "RDT", "DESC");
		//判断是不是签章
		if (DataType.IsNullOrEmpty(writeStamp) == false)
		{
			//writeStamp = HttpUtility.UrlDecode(writeStamp, Encoding.UTF8);// writeStamp.replace('~', '+');
			String mypk = dt.Rows.size() > 0 ? dt.Rows.get(0).getValue("MyPK").toString() : "";
			if (DataType.IsNullOrEmpty(mypk) == false)
			{
				DBAccess.SaveBigTextToDB(writeStamp, "ND" + Integer.parseInt(this.getFlowNo()) + "Track", "MyPK", mypk, "FrmDB");
			}
		}

		return dt.Rows.size() > 0 ? dt.Rows.get(0).getValue("RDT").toString() : "";
	}

	///#endregion


	///#region 工作分配.
	/**
	 分配工作

	 @return
	 */
	public final String AllotTask_Init() throws Exception {
		GenerWorkerLists wls = new GenerWorkerLists(this.getWorkID(), this.getNodeID(), true);
		return wls.ToJson("dt");
	}
	/**
	 分配工作

	 @return
	 */
	public final String AllotTask_Save()
	{
		return "";
	}

	///#endregion


	///#region 执行跳转.
	/**
	 返回可以跳转的节点.

	 @return
	 */
	public final String FlowSkip_Init() throws Exception {
		Node nd = new Node(this.getNodeID());
		BtnLab lab = new BtnLab(this.getNodeID());

		String sql = "SELECT NodeID,Name FROM WF_Node WHERE FK_Flow='" + this.getFlowNo() + "'";
		switch (lab.getJumpWayEnum())
		{
			case Previous:
				sql = "SELECT NodeID,Name FROM WF_Node WHERE NodeID IN (SELECT FK_Node FROM WF_GenerWorkerlist WHERE WorkID=" + this.getWorkID() + " )";
				break;
			case Next:
				sql = "SELECT NodeID,Name FROM WF_Node WHERE NodeID NOT IN (SELECT FK_Node FROM WF_GenerWorkerlist WHERE WorkID=" + this.getWorkID() + " ) AND FK_Flow='" + this.getFlowNo() + "'";
				break;
			case AnyNode:
				sql = "SELECT NodeID,Name FROM WF_Node WHERE FK_Flow='" + this.getFlowNo() + "' ORDER BY STEP";
				break;
			case JumpSpecifiedNodes:
				sql = nd.getJumpToNodes();
				sql = sql.replace("@WebUser.No", WebUser.getNo());
				sql = sql.replace("@WebUser.Name", WebUser.getName());
				sql = sql.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
				sql = sql.replace("@WebUser.DeptNo", WebUser.getDeptNo());
				if (sql.contains("@"))
				{
					Work wk = nd.getHisWork();
					wk.setOID(this.getWorkID());
					wk.RetrieveFromDBSources();
					for (Attr attr : wk.getEnMap().getAttrs())
					{
						if (sql.contains("@") == false)
						{
							break;
						}
						sql = sql.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
					}
				}
				break;
			case CanNotJump:
				return "err@此节点不允许跳转.";
			default:
				return "err@未判断";
		}

		sql = sql.replace("~", "'");
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		//如果是oracle,就转成小写.
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("NODEID").ColumnName = "NodeID";
			dt.Columns.get("NAME").ColumnName = "Name";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("nodeid").ColumnName = "NodeID";
			dt.Columns.get("name").ColumnName = "Name";
		}
		return bp.tools.Json.ToJson(dt);
	}
	/**
	 执行跳转

	 @return
	 */
	public final String FlowSkip_Do()
	{
		try
		{
			Node ndJump = new Node(this.GetRequestValInt("GoNode"));
			WorkNode wn = new WorkNode(this.getWorkID(), this.getNodeID());
			String msg = wn.NodeSend(ndJump, null).ToMsgOfHtml();
			return msg;
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}

	///#endregion 执行跳转.


	///#region 执行父类的重写方法.
	/**
	 默认执行的方法

	 @return
	 */
	@Override
	protected String DoDefaultMethod()
	{
		switch (this.getDoType())
		{

			case "DtlFieldUp": //字段上移
				return "执行成功.";
			default:
				break;
		}

		//找不不到标记就抛出异常.
		throw new RuntimeException("@标记[" + this.getDoType() + "]，没有找到.");
	}

	///#endregion 执行父类的重写方法.


	///#region 抄送Adv.
	/**
	 选择权限组

	 @return
	 */
	public final String CCAdv_SelectGroups()
	{
		String sql = "SELECT NO,NAME FROM GPM_Group ORDER BY IDX";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}
	/**
	 抄送初始化.

	 @return
	 */
	public final String CCAdv_Init() throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		Hashtable ht = new Hashtable();
		ht.put("Title", gwf.getTitle());

		//计算出来曾经抄送过的人.
		Paras ps = new Paras();
		ps.SQL = "SELECT CCToName FROM WF_CCList WHERE FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID";
		ps.Add("FK_Node", this.getNodeID());
		ps.Add("WorkID", this.getWorkID());
		DataTable mydt = DBAccess.RunSQLReturnTable(ps);
		String toAllEmps = "";
		for (DataRow dr : mydt.Rows)
		{
			toAllEmps += dr.getValue(0).toString() + ",";
		}

		ht.put("CCTo", toAllEmps);

		// 根据他判断是否显示权限组。
		if (DBAccess.IsExitsObject("GPM_Group") == true)
		{
			ht.put("IsGroup", "1");
		}
		else
		{
			ht.put("IsGroup", "0");
		}

		//返回流程标题.
		return bp.tools.Json.ToJsonEntityModel(ht);
	}
	/**
	 选择部门呈现信息.

	 @return
	 */
	public final String CCAdv_SelectDepts() throws Exception {
		Depts depts = new Depts();
		depts.RetrieveAll();
		return depts.ToJson("dt");
	}
	/**
	 选择部门呈现信息.

	 @return
	 */
	public final String CCAdv_SelectStations() throws Exception {
		//角色类型.
		String sql = "SELECT NO,NAME FROM Port_StationType ORDER BY NO";
		DataSet ds = new DataSet();
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Port_StationType";
		ds.Tables.add(dt);

		//角色.
		String sqlStas = "SELECT NO,NAME,FK_STATIONTYPE FROM Port_Station ORDER BY FK_STATIONTYPE,NO";
		DataTable dtSta = DBAccess.RunSQLReturnTable(sqlStas);
		dtSta.TableName = "Port_Station";
		ds.Tables.add(dtSta);
		return bp.tools.Json.ToJson(ds);
	}
	/**
	 抄送发送.

	 @return
	 */
	public final String CCAdv_Send() throws Exception {
		//人员信息. 格式 zhangsan,张三;lisi,李四;
		String emps = this.GetRequestVal("Emps");

		//角色信息. 格式:  001,002,003,
		String stations = this.GetRequestVal("Stations");
		stations = stations.replace(";", ",");

		//权限组. 格式:  001,002,003,
		String groups = this.GetRequestVal("Groups");
		if (groups == null)
		{
			groups = "";
		}
		groups = groups.replace(";", ",");

		//部门信息.  格式: 001,002,003,
		String depts = this.GetRequestVal("Depts");
		//标题.
		String title = this.GetRequestVal("TB_Title");
		//内容.
		String doc = this.GetRequestVal("TB_Doc");

		//调用抄送接口执行抄送.
		String ccRec = Dev2Interface.Node_CC_WriteTo_CClist(this.getNodeID(), this.getWorkID(), title, doc, emps, depts, stations, groups);

		if (Objects.equals(ccRec, ""))
		{
			return "没有抄送到任何人。";
		}
		else
		{
			return "本次抄送给如下人员：" + ccRec;
		}
		//return "执行抄送成功.emps=(" + emps + ")  depts=(" + depts + ") stas=(" + stations + ") 标题:" + title + " ,抄送内容:" + doc;
	}

	///#endregion 抄送Adv.


	///#region 抄送普通的抄送.
	public final String CC_AddEmps() throws Exception {
		String toEmpStrs = this.GetRequestVal("AddEmps");
		toEmpStrs = toEmpStrs.replace(",", ";");
		String[] toEmps = toEmpStrs.split("[;]", -1);
		String infos = "";
		for (String empStr : toEmps)
		{
			if (DataType.IsNullOrEmpty(empStr) == true)
			{
				continue;
			}

			Emp emp = new Emp(empStr);

			CCList cc = new CCList();
			cc.setFlowNo(this.getFlowNo());
			cc.setNodeIDWork(this.getNodeID());
			cc.setWorkID(this.getWorkID());
			cc.setRecEmpNo(emp.getNo());
			cc.setRecEmpName(emp.getName());

			cc.Insert();
		}

		return "";
	}

	/**
	 抄送发送.

	 @return
	 */
	public final String CC_Send() throws Exception {
		//人员信息. 格式 zhangsan,张三;lisi,李四;
		String emps = this.GetRequestVal("Emps");

		//角色信息. 格式:  001,002,003,
		String stations = this.GetRequestVal("Stations");
		if (DataType.IsNullOrEmpty(stations) == false)
		{
			stations = stations.replace(";", ",");
		}

		//权限组. 格式:  001,002,003,
		String groups = this.GetRequestVal("Groups");
		if (groups == null)
		{
			groups = "";
		}
		groups = groups.replace(";", ",");

		//部门信息.  格式: 001,002,003,
		String depts = this.GetRequestVal("Depts");
		//标题.
		String title = this.GetRequestVal("TB_Title");
		//内容.
		String doc = this.GetRequestVal("TB_Doc");

		//调用抄送接口执行抄送.
		String ccRec = Dev2Interface.Node_CC_WriteTo_CClist(this.getNodeID(), this.getWorkID(), title, doc, emps, depts, stations, groups);

		//该节点上设置为未启动.
		DBAccess.RunSQL("UPDATE WF_CCList SET Sta=-1 WHERE WorkID=" + this.getWorkID() + " AND FK_Node=" + this.getNodeID());

		if (Objects.equals(ccRec, ""))
		{
			return "没有抄送到任何人。";
		}
		else
		{
			return "本次抄送给如下人员：" + ccRec;
		}
		//return "执行抄送成功.emps=(" + emps + ")  depts=(" + depts + ") stas=(" + stations + ") 标题:" + title + " ,抄送内容:" + doc;
	}

	///#endregion 抄送普通的抄送.


	public final String DeleteFlowInstance_Init() throws Exception {
		if (Dev2Interface.Flow_IsCanDeleteFlowInstance(this.getFlowNo(), this.getWorkID(), WebUser.getNo()) == false)
		{
			return "err@您没有删除该流程的权限";
		}
		//获取节点中配置的流程删除规则
		if (this.getNodeID() != 0)
		{
			Paras ps = new Paras();
			ps.SQL = "SELECT wn.DelEnable FROM WF_Node wn WHERE wn.NodeID = " + SystemConfig.getAppCenterDBVarStr() + "NodeID";
			ps.Add("NodeID", this.getNodeID());
			return DBAccess.RunSQLReturnValInt(ps) + "";
		}

		return "删除成功.";
	}

	public final String DeleteFlowInstance_DoDelete() throws Exception {
		if (Dev2Interface.Flow_IsCanDeleteFlowInstance(this.getFlowNo(), this.getWorkID(), WebUser.getNo()) == false)
		{
			return "err@您没有删除该流程的权限.";
		}

		String deleteWay = this.GetRequestVal("RB_DeleteWay");
		String doc = this.GetRequestVal("TB_Doc");

		//是否要删除子流程？ 这里注意变量的获取方式，你可以自己定义.
		String isDeleteSubFlow = this.GetRequestVal("CB_IsDeleteSubFlow");

		boolean isDelSubFlow = false;
		if (Objects.equals(isDeleteSubFlow, "1"))
		{
			isDelSubFlow = true;
		}

		//按照标记删除.
		if (Objects.equals(deleteWay, "1"))
		{
			Dev2Interface.Flow_DoDeleteFlowByFlag(this.getWorkID(), doc, isDelSubFlow);
		}

		//彻底删除.
		if (Objects.equals(deleteWay, "3"))
		{
			Dev2Interface.Flow_DoDeleteFlowByReal(this.getWorkID(), isDelSubFlow);
		}

		//彻底并放入到删除轨迹里.
		if (Objects.equals(deleteWay, "2"))
		{
			Dev2Interface.Flow_DoDeleteFlowByWriteLog(this.getFlowNo(), this.getWorkID(), doc, isDelSubFlow);
		}

		return "流程删除成功.";
	}
	/**
	 获得节点表单数据.

	 @return
	 */
	//public String ViewWorkNodeFrm()
	//{
	//    Node nd = new Node(this.getNodeID());
	//    nd.setWorkID(this.WorkID; //为获取表单ID ( NodeFrmID )提供参数.

	//    Hashtable ht = new Hashtable();
	//    ht.Add("FormType", nd.getFormType().ToString());
	//    ht.Add("Url", nd.FormUrl + "&WorkID=" + this.WorkID + "&FK_Flow=" + this.FlowNo + "&FK_Node=" + this.getNodeID());

	//    if (nd.getFormType() == NodeFormType.SDKForm)
	//        return bp.tools.Json.ToJsonEntityModel(ht);

	//    if (nd.getFormType() == NodeFormType.SelfForm)
	//        return bp.tools.Json.ToJsonEntityModel(ht);

	//    //表单模版.
	//    DataSet myds = BP.Sys.CCFormAPI.GenerHisDataSet(nd.getNodeFrmID());
	//    String json = BP.WF.Dev2Interface.CCFrom_GetFrmDBJson(this.FlowNo, this.MyPK);
	//    DataTable mainTable = bp.tools.Json.ToDataTableOneRow(json);
	//    mainTable.TableName = "MainTable";
	//    myds.Tables.add(mainTable);

	//    //MapExts exts = new MapExts(nd.getHisWork().ToString());
	//    //DataTable dtMapExt = exts.ToDataTableDescField();
	//    //dtMapExt.TableName = "Sys_MapExt";
	//    //myds.Tables.add(dtMapExt);

	//    return bp.tools.Json.ToJson(myds);
	//}

	/**
	 回复加签信息.

	 @return
	 */
	public final String AskForRe() throws Exception {
		String note = this.GetRequestVal("Note"); //原因.
		return Dev2Interface.Node_AskforReply(this.getWorkID(), note);
	}
	/**
	 执行加签

	 @return 执行信息
	 */
	public final String Askfor() throws Exception {
		long workID = Integer.parseInt(this.GetRequestVal("WorkID")); //工作ID
		String toEmp = this.GetRequestVal("ToEmp"); //让谁加签?
		String note = this.GetRequestVal("Note"); //原因.
		String model = this.GetRequestVal("Model"); //模式.

		AskforHelpSta sta = AskforHelpSta.AfterDealSend;
		if (Objects.equals(model, "1"))
		{
			sta = AskforHelpSta.AfterDealSendByWorker;
		}

		return Dev2Interface.Node_Askfor(workID, sta, toEmp, note);
	}
	/**
	 人员选择器

	 @return
	 */
	public final String SelectEmps_Init() throws Exception {
		String fk_dept = this.getDeptNo();
		if (DataType.IsNullOrEmpty(fk_dept) == true || fk_dept.equals("undefined") == true)
		{
			fk_dept = WebUser.getDeptNo();
		}

		DataSet ds = new DataSet();

		String sql = "";
		sql = "SELECT No,Name,ParentNo FROM Port_Dept WHERE No='" + fk_dept + "' OR ParentNo='" + fk_dept + "' ORDER BY Idx";

		//如果是节水公司的.
		//if (SystemConfig.getCustomerNo() == "TianYe" && WebUser.FK_Dept.IndexOf("18099") == -1)
		//    sql = "SELECT No,Name,ParentNo FROM Port_Dept WHERE  No='" + fk_dept + "'  OR (ParentNo='" + fk_dept + "' AND No!='18099') ORDER BY Idx ";

		DataTable dtDept = DBAccess.RunSQLReturnTable(sql);
		if (dtDept.Rows.size() == 0)
		{
			fk_dept = WebUser.getDeptNo();
			sql = "SELECT No,Name,ParentNo FROM Port_Dept WHERE No='" + fk_dept + "' OR ParentNo='" + fk_dept + "' ORDER BY Idx ";
			dtDept = DBAccess.RunSQLReturnTable(sql);
		}

		dtDept.TableName = "Depts";
		ds.Tables.add(dtDept);

		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dtDept.Columns.get(0).ColumnName = "No";
			dtDept.Columns.get(1).ColumnName = "Name";
			dtDept.Columns.get(2).ColumnName = "ParentNo";
		}


		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			sql = "SELECT distinct CONCAT('Emp_',A.No ) AS No, A.Name, '" + fk_dept + "' as FK_Dept, a.Idx FROM Port_Emp A LEFT JOIN Port_DeptEmp B  ON A.No=B.FK_Emp WHERE A.FK_Dept='" + fk_dept + "' OR B.FK_Dept='" + fk_dept + "' ";
			if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
			{
				sql = "SELECT distinct 'Emp_'+A.No AS No, A.Name, '" + fk_dept + "' as FK_Dept, a.Idx FROM Port_Emp A LEFT JOIN Port_DeptEmp B  ON A.No=B.FK_Emp WHERE A.FK_Dept='" + fk_dept + "' OR B.FK_Dept='" + fk_dept + "' ";
			}
			sql += " ORDER BY A.Idx ";
		}
		else
		{
			// 原来的SQL sql = "SELECT distinct CONCAT('Emp_', A.getNo()) AS No,A.Name, '" + fk_dept + "' as FK_Dept, a.Idx FROM Port_Emp A LEFT JOIN Port_DeptEmp B  ON A.No=B.FK_Emp WHERE A.FK_Dept='" + fk_dept + "' OR B.FK_Dept='" + fk_dept + "' ";

			sql = "SELECT CONCAT('Emp_',No) AS No, Name, FK_Dept,Idx FROM Port_Emp WHERE FK_Dept='" + fk_dept + "'";
			sql += " UNION ";
			sql += "SELECT CONCAT('Emp_',A.No) AS No,A.Name, A.FK_Dept,A.Idx FROM Port_Emp A, Port_DeptEmp B WHERE A.No=B.FK_Emp AND B.FK_Dept='" + fk_dept + "'";

			if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
			{
				sql = "SELECT  'Emp_'+ No AS No, Name, FK_Dept,Idx FROM Port_Emp WHERE FK_Dept='" + fk_dept + "'";
				sql += " UNION ";
				sql += "SELECT  'Emp_'+ A.No AS No,A.Name, '" + fk_dept + "' as FK_Dept, a.Idx FROM Port_Emp A ,Port_DeptEmp B  WHERE A.No=B.FK_Emp AND B.FK_Dept='" + fk_dept + "' ";
			}
			sql = "SELECT Distinct * FROM (" + sql + ")A Order By Idx";
		}

		DataTable dtEmps = DBAccess.RunSQLReturnTable(sql);
		dtEmps.TableName = "Emps";
		ds.Tables.add(dtEmps);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dtEmps.Columns.get(0).ColumnName = "No";
			dtEmps.Columns.get(1).ColumnName = "Name";
			dtEmps.Columns.get(2).ColumnName = "FK_Dept";
		}

		//转化为 json
		return bp.tools.Json.ToJson(ds);
	}
	/**
	 处理发送初始化已经选择的人员信息.

	 @return
	 */
	public final String SendWorkOpt_Init() throws Exception {
		String sql = "";
		switch (SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
				sql = "SELECT top 1 MyPK FROM WF_WorkOpt WHERE NodeID=" + this.getNodeID() + " AND ToNodeID=" + this.getToNodeID() + " AND EmpNo='" + WebUser.getNo() + "' AND WorkID!=" + this.getWorkID() + " ORDER BY StartRDT DESC ";
				break;
			case MySQL:
			case UX:
			case PostgreSQL:
			case HGDB:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql = "SELECT MyPK FROM WF_WorkOpt WHERE NodeID=" + this.getNodeID() + " AND ToNodeID=" + this.getToNodeID() + " AND EmpNo='" + WebUser.getNo() + "' AND WorkID!=" + this.getWorkID() + " ORDER BY StartRDT DESC  limit 1";
				break;
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				sql = "SELECT MyPK FROM WF_WorkOpt WHERE NodeID=" + this.getNodeID() + " AND ToNodeID=" + this.getToNodeID() + " AND EmpNo='" + WebUser.getNo() + "' AND WorkID!=" + this.getWorkID() + " AND  ROWNUM = 1 ORDER BY StartRDT DESC";
				break;
			default:
				throw new RuntimeException("err@没有判断的数据库类型");
		}

		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		NodeSimple toND = new NodeSimple(this.getToNodeID());
		WorkOpt en = new WorkOpt();

		String val = DBAccess.RunSQLReturnStringIsNull(sql, null);
		if (val == null)
		{
			en.setWorkID(this.getWorkID());
			en.setFlowNo(this.getFlowNo());

			String[] strs = this.getMyPK().split("[_]", -1);
			en.setEmpNo(WebUser.getNo());
			en.setNodeID(this.getNodeID());
			en.setToNodeID(this.getToNodeID());
			en.setFlowNo(this.getFlowNo());
			en.setWorkID(this.getWorkID());
			en.setMyPK(this.getMyPK());

			//流程信息.
			en.SetValByKey(WorkOptAttr.SendRDT, gwf.getSendDT()); //发送日期.
			en.SetValByKey(WorkOptAttr.SendSDT, gwf.getSDTOfNode()); //节点应完成时间.
			en.SetValByKey(WorkOptAttr.SenderName, gwf.getSender()); //发送人.
			en.SetValByKey(WorkOptAttr.Title, gwf.getTitle());

			en.SetValByKey(WorkOptAttr.NodeName, gwf.getNodeName());
			en.SetValByKey(WorkOptAttr.ToNodeName, toND.getName());

			en.SetValByKey(WorkOptAttr.ToNodeName, toND.getName()); //到达的节点名称.

			//如果是开始节点.
			if (String.valueOf(en.getNodeID()).endsWith("01") == true)
			{
				en.SetValByKey(WorkOptAttr.SendSDT, "无"); //节点应完成时间.
				en.SetValByKey(WorkOptAttr.SendRDT, "无"); //发送日期.
			}

			en.Save();
			return "info@执行成功.";
		}

		//加载上一笔数据，让其可以自动记录上一次的处理人。
		WorkOpt opt = new WorkOpt(val);

		en = new WorkOpt(this.getMyPK());
		en.Copy(opt);
		en.setWorkID(this.getWorkID());
		en.setFlowNo(this.getFlowNo());
		en.setMyPK(this.getMyPK());

		//流程信息.
		en.SetValByKey(WorkOptAttr.StartRDT, gwf.getRDT());
		en.SetValByKey(WorkOptAttr.SendRDT, gwf.getSendDT());
		en.SetValByKey(WorkOptAttr.SendSDT, gwf.getSDTOfNode());
		en.SetValByKey(WorkOptAttr.SenderName, gwf.getSender());
		en.SetValByKey(WorkOptAttr.Title, gwf.getTitle());

		en.SetValByKey(WorkOptAttr.NodeName, gwf.getNodeName());
		en.SetValByKey(WorkOptAttr.ToNodeName, toND.getName());

		//如果是开始节点.
		if (String.valueOf(en.getNodeID()).endsWith("01") == true)
		{
			en.SetValByKey(WorkOptAttr.SendSDT, "无"); //节点应完成时间.
			en.SetValByKey(WorkOptAttr.SendRDT, "无"); //发送日期.
		}

		en.Update();
		return "info@执行成功.";
	}


	///#region 选择接受人.
	/**
	 初始化接受人.

	 @return
	 */
	public final String Accepter_Init() throws Exception {
		/*如果是协作模式, 就要检查当前是否主持人, 当前是否是会签模式. */
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		if (gwf.getNodeID() != this.getNodeID() && this.getNodeID() != 0)
		{
			return "err@当前流程已经运动到[" + gwf.getNodeName() + "]上,当前处理人员为[" + gwf.getTodoEmps() + "]，this.getNodeID()=" + this.getNodeID();
		}

		//当前节点ID.
		Node nd = new Node(gwf.getNodeID());

		//判断当前是否是协作模式.
		if (nd.getTodolistModel() == TodolistModel.Teamup && nd.getItIsStartNode() == false)
		{
			if (gwf.getTodoEmps().contains(WebUser.getNo() + ","))
			{
				/* 说明我是主持人之一, 我就可以选择接受人,发送到下一个节点上去. */
			}
			else
			{
				//  String err= "err@流程配置逻辑错误，当前节点是协作模式，当前节点的方向条件不允许[发送按钮旁下拉框选择(默认模式)].";
				//  err += "，如果需要手工选择，请使用[节点属性]-[设置方向条件]-[按照用户执行发送后手工选择计算]模式计算.";
				//  return err;

				/* 不是主持人就执行发送，返回发送结果. */

				//判断是否有不发送标记？
				if (this.GetRequestValBoolen("IsSend") == true)
				{
					SendReturnObjs objs = Dev2Interface.Node_SendWork(gwf.getFlowNo(), this.getWorkID());
					return "info@" + objs.ToMsgOfHtml();
				}
			}
		}

		int toNodeID = this.GetRequestValInt("ToNode");
		if (toNodeID == 0)
		{
			Nodes nds = nd.getHisToNodes();
			if (nds.size() == 1)
			{
				toNodeID = nds.get(0).GetValIntByKey("NodeID");
			}
			else
			{
				return "err@参数错误,必须传递来到达的节点ID ToNode .";
			}
		}

		Work wk = nd.getHisWork();
		try{
			wk.setOID(this.getWorkID());
			wk.Retrieve();
		}catch(Exception e){
			wk.setOID(this.getPWorkID());
			wk.Retrieve();
		}


		Selector select = new Selector();
		select.setNodeID(toNodeID);
		select.RetrieveFromDBSources();

		if (select.getSelectorModel() == SelectorModel.GenerUserSelecter)
		{
			return "url@AccepterOfGener.htm?WorkID=" + this.getWorkID() + "&FK_Node=" + gwf.getNodeID() + "&FK_Flow=" + nd.getFlowNo() + "&ToNode=" + toNodeID + "&PWorkID=" + gwf.getPWorkID() + "&PageName=AccepterOfGener";
		}

		if (select.getSelectorModel() == SelectorModel.AccepterOfDeptStationEmp)
		{
			return "url@AccepterOfDeptStationEmp.htm?WorkID=" + this.getWorkID() + "&FK_Node=" + gwf.getNodeID() + "&FK_Flow=" + nd.getFlowNo() + "&ToNode=" + toNodeID + "&PWorkID=" + gwf.getPWorkID() + "&PageName=AccepterOfDeptStationEmp";
		}

		if (select.getSelectorModel() == SelectorModel.Url)
		{
			return "BySelfUrl@" + select.getSelectorP1() + "?WorkID=" + this.getWorkID() + "&FK_Node=" + gwf.getNodeID() + "&FK_Flow=" + nd.getFlowNo() + "&ToNode=" + toNodeID + "&PWorkID=" + gwf.getPWorkID() + "&PageName=" + select.getSelectorP1();
		}

		//获得 部门与人员.
		DataSet ds = select.GenerDataSet(toNodeID, wk);

		if (Objects.equals(SystemConfig.getCustomerNo(), "TianYe")) //天业集团，去掉00000001董事长
		{
		}

		//增加判断.
		if (ds.GetTableByName("Emps").Rows.size() == 0)
		{
			return "err@配置接受人范围为空,请联系管理员.";
		}

		////只有一个人，就让其发送下去.
		//if (ds.GetTableByName("Emps"].Rows.size() == 1)
		//{
		//    String emp = ds.GetTableByName("Emps"].Rows[0][0).toString();
		//    SendReturnObjs objs= BP.WF.Dev2Interface.Node_SendWork(this.FlowNo, this.WorkID, toNodeID, emp);
		//    return  "info@"+objs.ToMsgOfText();
		//}


		///#region 计算上一次选择的结果, 并把结果返回过去.
		String sql = "";
		DataTable dt = new DataTable();
		dt.Columns.Add("No", String.class);
		dt.Columns.Add("Name", String.class);
		dt.TableName = "Selected";
		if (select.getItIsAutoLoadEmps() == true)
		{
			//  WorkOpt en = new WorkOpt();
			//   en.GetValFloatByKey
			//   en.Insert();

			//获取当前节点的SelectAccper的值
			SelectAccpers selectAccpers = new SelectAccpers();
			selectAccpers.Retrieve(SelectAccperAttr.WorkID, this.getWorkID(), SelectAccperAttr.FK_Node, toNodeID, null);
			if (selectAccpers.size() != 0)
			{
				for (SelectAccper sa : selectAccpers.ToJavaList())
				{
					DataRow dr = dt.NewRow();
					dr.setValue(0, sa.getEmpNo());
					dt.Rows.add(dr);
				}
			}
			else
			{
				if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
				{
					sql = "SELECT  top 1 Tag,EmpTo FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track A WHERE A.NDFrom=" + this.getNodeID() + " AND A.NDTo=" + toNodeID + " AND ActionType=1 ORDER BY WorkID DESC";
				}
				else if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
				{
					sql = "SELECT * FROM (SELECT  Tag,EmpTo,WorkID FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track A WHERE A.NDFrom=" + this.getNodeID() + " AND A.NDTo=" + toNodeID + " AND ActionType=1 ORDER BY WorkID DESC ) WHERE ROWNUM =1";
				}
				else if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
				{
					sql = "SELECT  Tag,EmpTo FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track A WHERE A.NDFrom=" + this.getNodeID() + " AND A.NDTo=" + toNodeID + " AND ActionType=1 ORDER BY WorkID  DESC limit 1,1 ";
				}
				else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX)
				{
					sql = "SELECT  Tag,EmpTo FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track A WHERE A.NDFrom=" + this.getNodeID() + " AND A.NDTo=" + toNodeID + " AND ActionType=1 ORDER BY WorkID  DESC limit 1 ";
				}

				DataTable mydt = DBAccess.RunSQLReturnTable(sql);
				String emps = "";
				if (mydt.Rows.size() != 0)
				{
					emps = mydt.Rows.get(0).getValue("Tag").toString();
					if (Objects.equals(emps, "") || emps == null)
					{
						emps = mydt.Rows.get(0).getValue("EmpTo").toString();
						emps = emps + "," + emps;
					}
				}

				String[] strs = emps.split("[;]", -1);
				for (String str : strs)
				{
					if (DataType.IsNullOrEmpty(str) == true)
					{
						continue;
					}

					String[] emp = str.split("[,]", -1);
					if (emp.length != 2)
					{
						continue;
					}

					DataRow dr = dt.NewRow();
					dr.setValue(0, emp[0]);
					dr.setValue(1, DBAccess.RunSQLReturnString("SELECT Name FROM Port_Emp WHERE No='" + emp[0] + "'"));
					dt.Rows.add(dr);
				}
			}
		}

		//增加一个table.
		ds.Tables.add(dt);

		///#endregion 计算上一次选择的结果, 并把结果返回过去.

		//返回json.
		return bp.tools.Json.ToJson(ds);
	}
	/**
	 保存.

	 @return
	 */
	public final String Accepter_Save()
	{
		try
		{
			//求到达的节点.
			int toNodeID = 0;
			if (!Objects.equals(this.GetRequestVal("ToNode"), "0"))
			{
				toNodeID = Integer.parseInt(this.GetRequestVal("ToNode"));
			}

			if (toNodeID == 0)
			{ //没有就获得第一个节点.
				Node nd = new Node(this.getNodeID());
				Nodes nds = nd.getHisToNodes();
				toNodeID = nds.get(0).GetValIntByKey("NodeID");
			}

			//求发送到的人员.
			// String selectEmps = this.GetValFromFrmByKey("SelectEmps");
			String selectEmps = this.GetRequestVal("SelectEmps");
			selectEmps = selectEmps.replace(";", ",");

			//保存接受人.
			Dev2Interface.Node_AddNextStepAccepters(this.getWorkID(), toNodeID, selectEmps, true);
			return "SaveOK@" + selectEmps;
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	public final String Accepter_Send()
	{
		//求到达的节点.
		int toNodeID = 0;
		if (!Objects.equals(this.GetRequestVal("ToNode"), "0"))
		{
			toNodeID = Integer.parseInt(this.GetRequestVal("ToNode"));
		}

		String emps = this.GetRequestVal("SelectEmps");
		String ahther = this.GetRequestVal("Auther");

		return Accepter_SendExt(this.getWorkID(), toNodeID, emps, ahther);
	}
	/**
	 执行保存并发送.

	 @return 返回发送的结果.
	 */
	private String Accepter_SendExt(long workid, int toNodeID, String selectEmps, String Auther)
	{
		try
		{
			GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
			int currNodeID = gwf.getNodeID();

			if (toNodeID == 0)
			{ //没有就获得第一个节点.
				Node nd = new Node(gwf.getNodeID());
				Nodes nds = nd.getHisToNodes();
				toNodeID = nds.get(0).GetValIntByKey("NodeID");
			}
			selectEmps = selectEmps.replace(";", ",");


			///#region 处理授权人.
			//授权人
			String auther = this.GetRequestVal("Auther");
			if (DataType.IsNullOrEmpty(auther) == false)
			{
				//  bp.web.WebUser.getIsAuthorize() = true;
				WebUser.setAuth(auther);
				WebUser.setAuthName(DBAccess.RunSQLReturnString("SELECT Name FROM Port_Emp WHERE No='" + auther + "'"));
			}
			else
			{
				// bp.web.WebUser.getIsAuthorize() = true;
				WebUser.setAuth("");
				WebUser.setAuthName(""); // BP.DA.DBAccess.RunSQLReturnString("SELECT Name FROM Port_Emp WHERE No='" + auther + "'");
			}

			///#endregion 处理授权人.


			//执行发送.
			SendReturnObjs objs = Dev2Interface.Node_SendWork(gwf.getFlowNo(), workid, toNodeID, selectEmps);

			String msg = objs.ToMsgOfHtml();

			///#region 处理授权
			gwf = new GenerWorkFlow(workid);
			if (DataType.IsNullOrEmpty(auther) == false)
			{
				gwf.SetPara("Auth", WebUser.getAuthName() + "授权");
				gwf.Update();
			}

			///#endregion 处理授权

			//当前节点.
			Node currNode = new Node(currNodeID);
			if (currNode.getHisFormType() != NodeFormType.SDKForm && currNode.getHisCancelRole() != CancelRole.None)
				msg="@IsCanUnSend=1"+msg;
			if (DataType.IsNullOrEmpty(objs.getVarAcceptersID())==false && objs.getVarAcceptersID().split(",").length >= 2 && currNode.getItIsTask())
				msg="@IsCanTask=1"+msg;

			///#region 处理发送后转向.
			/*处理转向问题.*/
			switch (currNode.getHisTurnToDeal())
			{
				case SpecUrl:
					String myurl = currNode.getTurnToDealDoc();
					if (myurl.contains("?") == false)
					{
						myurl += "?1=1";
					}
					Attrs myattrs = currNode.getHisWork().getEnMap().getAttrs();
					Work hisWK = currNode.getHisWork();
					for (Attr attr : myattrs)
					{
						if (myurl.contains("@") == false)
						{
							break;
						}
						myurl = myurl.replace("@" + attr.getKey(), hisWK.GetValStrByKey(attr.getKey()));
					}
					myurl = myurl.replace("@WebUser.No", WebUser.getNo());
					myurl = myurl.replace("@WebUser.Name", WebUser.getName());
					myurl = myurl.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
					myurl = myurl.replace("@WebUser.DeptNo", WebUser.getDeptNo());
					if (myurl.contains("@"))
					{
						Dev2Interface.Port_SendMsg("admin", currNode.getFlowName() + "在" + currNode.getName() + "节点处，出现错误", "流程设计错误，在节点转向url中参数没有被替换下来。Url:" + myurl, "Err" + currNode.getNo() + "_" + gwf.getWorkID(), SMSMsgType.Err, gwf.getFlowNo(), gwf.getNodeID(), gwf.getWorkID(), gwf.getFID());
						throw new RuntimeException("流程设计错误，在节点转向url中参数没有被替换下来。Url:" + myurl);
					}

					if (myurl.contains("PWorkID") == false)
					{
						myurl += "&PWorkID=" + gwf.getPWorkID();
					}

					if (myurl.contains("WorkID") == false)
					{
						myurl += "&WorkID=" + gwf.getWorkID();
					}

					myurl += "&FromFlow=" + gwf.getFlowNo() + "&FromNode=" + gwf.getNodeID() + "&UserNo=" + WebUser.getNo() + "&Token=" + WebUser.getToken();
					return "TurnUrl@" + myurl;

				case TurnToByCond:

					return msg;
				default:
					msg = msg.replace("@WebUser.No", WebUser.getNo());
					msg = msg.replace("@WebUser.Name", WebUser.getName());
					msg = msg.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
					msg = msg.replace("@WebUser.DeptNo", WebUser.getDeptNo());
					return msg;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "err@" + ex.getMessage();
		}
	}

	///#endregion


	///#region 回滚.
	/**
	 回滚操作.

	 @return
	 */
	public final String Rollback_Init()
	{
		String andsql = " ";
		andsql += "  ActionType=" + ActionType.Start.getValue();
		andsql += " OR ActionType=" + ActionType.TeampUp.getValue();
		andsql += " OR ActionType=" + ActionType.Forward.getValue();
		andsql += " OR ActionType=" + ActionType.HuiQian.getValue();
		andsql += " OR ActionType=" + ActionType.ForwardFL.getValue();
		andsql += " OR ActionType=" + ActionType.FlowOver.getValue();
		andsql += " OR ActionType=" + ActionType.Skip.getValue();

		String sql = "SELECT RDT,NDFrom, NDFromT,EmpFrom,EmpFromT,ActionTypeText  FROM ND" + Integer.parseInt(this.getFlowNo()) + "Track WHERE WorkID=" + this.getWorkID() + " AND(" + andsql + ") Order By RDT DESC";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "RDT";
			dt.Columns.get(1).ColumnName = "NDFrom";
			dt.Columns.get(2).ColumnName = "NDFromT";
			dt.Columns.get(3).ColumnName = "EmpFrom";
			dt.Columns.get(4).ColumnName = "EmpFromT";
			dt.Columns.get(5).ColumnName = "ActionTypeText";
		}


		return bp.tools.Json.ToJson(dt);
	}

	/**
	 * 设置节点处理人（主观选择、预设置）
	 * @return
	 * @throws Exception
	 */
	public final String Node_AddNextStepAccepters() throws Exception{
		String emps=this.GetRequestVal("toEmps");
		return bp.wf.Dev2Interface.Node_AddNextStepAccepters(this.getWorkID(),this.getNodeID(),emps,"");
	}
	/**
	 执行回滚操作

	 @return
	 */
	public final String Rollback_Done() throws Exception {
		FlowExt flow = new FlowExt(this.getFlowNo());
		return flow.DoRebackFlowData(this.getWorkID(), this.getNodeID(), this.GetRequestVal("Msg"));
	}

	///#endregion 回滚.


	///#region 工作退回.
	/**
	 获得可以退回的节点.

	 @return 退回信息
	 */
	public final String Return_Init()
	{
		try
		{

			DataTable dt = Dev2Interface.DB_GenerWillReturnNodes(this.getWorkID());

			//如果只有一个退回节点，就需要判断是否启用了单节点退回规则.
			if (dt.Rows.size() == 1)
			{
				Node nd = new Node(this.getNodeID());
				if (nd.getReturnOneNodeRole() != 0)
				{
					/* 如果:启用了单节点退回规则.
					 */
					String returnMsg = "";
					if (nd.getReturnOneNodeRole() == 1 && DataType.IsNullOrEmpty(nd.getReturnField()) == false)
					{
						/*从表单字段里取意见.*/
						Work wk = nd.getHisWork();
						wk.setOID(this.getWorkID());
						wk.RetrieveFromDBSources();
						if (wk.getEnMap().getAttrs().contains(nd.getReturnField()) == false)
						{
							return "err@系统设置错误，您设置的单节点退回，退回信息字段拼写错误或者不存在" + nd.getReturnField();
						}
						returnMsg = wk.GetValStrByKey(nd.getReturnField());
						// DBAccess.RunSQLReturnStringIsNull(sql, "未填写意见");
					}

					if (nd.getReturnOneNodeRole() == 2)
					{
						/*从审核组件里取意见.*/
						String sql = "SELECT Msg FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track WHERE WorkID=" + this.getWorkID() + " AND NDFrom=" + this.getNodeID() + " AND EmpFrom='" + WebUser.getNo() + "' AND ActionType=" + ActionType.WorkCheck.getValue();
						returnMsg = DBAccess.RunSQLReturnStringIsNull(sql, "未填写意见");
					}

					int toNodeID = Integer.parseInt(dt.Rows.get(0).getValue(0).toString());

					String info = Dev2Interface.Node_ReturnWork(this.getFlowNo(), this.getWorkID(), 0, this.getNodeID(), toNodeID, returnMsg, false);
					return "info@" + info;
				}
			}
			return bp.tools.Json.ToJson(dt);
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/**
	 执行退回,返回退回信息.

	 @return
	 */
	public final String DoReturnWork() throws Exception {
		String returnToNode = this.GetRequestVal("ReturnToNode");
		String[] vals = returnToNode.split("=>");
		if(vals.length < 2)
			vals = this.GetRequestVal("ReturnToNode").split("[@]", -1);

		int toNodeID = Integer.parseInt(vals[0]);

		String toEmp = vals[1];
		String reMesage = this.GetRequestVal("ReturnInfo");

		boolean isBackBoolen = false;
		if (this.GetRequestVal("IsBack").equals("1") == true)
		{
			isBackBoolen = true;
		}

		boolean isKill = false; //是否全部退回.
		String isKillEtcThread = this.GetRequestVal("IsKillEtcThread");
		if (DataType.IsNullOrEmpty(isKillEtcThread) == false && isKillEtcThread.equals("1") == true)
		{
			isKill = true;
		}

		String pageData = this.GetRequestVal("PageData");

		return Dev2Interface.Node_ReturnWork(this.getWorkID(), toNodeID, toEmp, reMesage, isBackBoolen, pageData, isKill);
	}

	///#endregion

	/**
	 执行移交.

	 @return
	 */
	public final String Shift_Save() throws Exception {
		String msg = this.GetRequestVal("Message");
		String toEmp = this.GetRequestVal("ToEmp");
		return Dev2Interface.Node_Shift(this.getWorkID(), toEmp, msg);
	}
	/**
	 撤销移交

	 @return
	 */
	public final String UnShift() throws Exception {
		return Dev2Interface.Node_ShiftUn(this.getWorkID());
	}
	/**
	 执行催办

	 @return
	 */
	public final String Press() throws Exception {
		String msg = this.GetRequestVal("Msg");
		//调用API.
		return Dev2Interface.Flow_DoPress(this.getWorkID(), msg, true);
	}


	///#region 流程数据模版. for 浙商银行 by zhoupeng.
	/**
	 流程数据模版

	 @return
	 */
	public final String DBTemplate_Init() throws Exception {
		DataSet ds = new DataSet();

		//获取模版.
		Paras ps = new Paras();
		ps.SQL = "SELECT WorkID,Title,AtPara FROM WF_GenerWorkFlow WHERE FK_Flow=" + SystemConfig.getAppCenterDBVarStr() + "FK_Flow AND WFState=3 AND Starter=" + SystemConfig.getAppCenterDBVarStr() + "Starter AND ATPARA LIKE '%@DBTemplate=1%'";
		ps.Add("FK_Flow", this.getFlowNo(), false);
		ps.Add("Starter", WebUser.getNo(), false);
		DataTable dtTemplate = DBAccess.RunSQLReturnTable(ps);
		dtTemplate.TableName = "DBTemplate";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dtTemplate.Columns.get(0).ColumnName = "WorkID";
			dtTemplate.Columns.get(1).ColumnName = "Title";
		}

		//把模版名称替换 title.
		for (DataRow dr : dtTemplate.Rows)
		{
			String str = dr.getValue(2).toString();
			AtPara ap = new AtPara(str);
			String dbtemplateName = ap.GetValStrByKey("DBTemplateName");
			if (DataType.IsNullOrEmpty(dbtemplateName) == false)
			{
				dr.setValue("Title", dbtemplateName);
			}
		}

		ds.Tables.add(dtTemplate);

		// 获取历史发起数据.
		ps = new Paras();
		if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
		{
			ps.SQL = "SELECT TOP 30 WorkID,Title FROM WF_GenerWorkFlow WHERE FK_Flow=" + SystemConfig.getAppCenterDBVarStr() + "FK_Flow AND WFState=3 AND Starter=" + SystemConfig.getAppCenterDBVarStr() + "Starter AND ATPARA NOT LIKE '%@DBTemplate=1%' ORDER BY RDT ";
			ps.Add("FK_Flow", this.getFlowNo(), false);
			ps.Add("Starter", WebUser.getNo(), false);
		}
		if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			ps.SQL = "SELECT WorkID,Title FROM WF_GenerWorkFlow WHERE FK_Flow=" + SystemConfig.getAppCenterDBVarStr() + "FK_Flow AND WFState=3 AND Starter=" + SystemConfig.getAppCenterDBVarStr() + "Starter AND ATPARA NOT LIKE '%@DBTemplate=1%' AND rownum<=30 ORDER BY RDT ";
			ps.Add("FK_Flow", this.getFlowNo(), false);
			ps.Add("Starter", WebUser.getNo(), false);
		}
		if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			ps.SQL = "SELECT WorkID,Title FROM WF_GenerWorkFlow WHERE FK_Flow=" + SystemConfig.getAppCenterDBVarStr() + "FK_Flow AND WFState=3 AND Starter=" + SystemConfig.getAppCenterDBVarStr() + "Starter AND ATPARA NOT LIKE '%@DBTemplate=1%' ORDER BY RDT LIMIT 30";
			ps.Add("FK_Flow", this.getFlowNo(), false);
			ps.Add("Starter", WebUser.getNo(), false);
		}
		DataTable dtHistroy = DBAccess.RunSQLReturnTable(ps);
		dtHistroy.TableName = "History";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dtHistroy.Columns.get(0).ColumnName = "WorkID";
			dtHistroy.Columns.get(1).ColumnName = "Title";
		}
		ds.Tables.add(dtHistroy);

		//转化为 json.
		return bp.tools.Json.ToJson(ds);
	}

	public final String DBTemplate_SaveAsDBTemplate() throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		gwf.setParasDBTemplate(true);
		gwf.setParasDBTemplateName(this.GetRequestVal("Title")); //this.GetRequestVal("Title");
		gwf.Update();
		return "设置成功";
	}

	public final String DBTemplate_DeleteDBTemplate() throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		gwf.setParasDBTemplate(false);
		gwf.Update();

		return "设置成功";
	}

	public final String DBTemplate_StartFlowAsWorkID()
	{
		return "设置成功";
	}

	///#endregion 流程数据模版.


	///#region tonodes
	/**
	 初始化.

	 @return
	 */
	public final String ToNodes_Init() throws Exception {
		//获取到下一个节点的节点Nodes

		//获得当前节点到达的节点.
		Nodes nds = new Nodes();
		String toNodes = this.GetRequestVal("ToNodes");
		if (DataType.IsNullOrEmpty(toNodes) == false)
		{
			/*解决跳转问题.*/
			String[] mytoNodes = toNodes.split("[,]", -1);
			for (String str : mytoNodes)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}
				nds.AddEntity(new Node(Integer.parseInt(str)));
			}
		}
		else
		{
			nds = Dev2Interface.WorkOpt_GetToNodes(this.getFlowNo(), this.getNodeID(), this.getWorkID(), this.getFID());
		}

		//获得上次默认选择的节点
		int lastSelectNodeID = Dev2Interface.WorkOpt_ToNodes_GetLasterSelectNodeID(this.getFlowNo(), this.getNodeID());
		if (lastSelectNodeID == 0 && nds.size() != 0)
		{
			lastSelectNodeID = Integer.parseInt(nds.get(0).getPKVal().toString());
		}


		DataSet ds = new DataSet();
		ds.Tables.add(nds.ToDataTableField("Nodes"));
		DataTable dt = new DataTable("SelectNode");
		dt.Columns.Add("NodeID");
		DataRow dr = dt.NewRow();
		dr.setValue("NodeID", lastSelectNodeID);
		dt.Rows.add(dr);
		ds.Tables.add(dt);
		return bp.tools.Json.ToJson(ds);
	}

	/**
	 发送

	 @return
	 */
	public final String ToNodes_Send() throws Exception {
		String toNodes = this.GetRequestVal("ToNodes");
		// 执行发送.
		String msg = "";
		Node nd = new Node(this.getNodeID());
		Work wk = nd.getHisWork();
		wk.setOID(this.getWorkID());
		wk.Retrieve();

		try
		{
			String toNodeStr = Integer.parseInt(this.getFlowNo()) + "01";
			//如果为开始节点
			if (Objects.equals(toNodeStr, toNodes))
			{
				//把参数更新到数据库里面.
				GenerWorkFlow gwf = new GenerWorkFlow();
				gwf.setWorkID(this.getWorkID());
				gwf.RetrieveFromDBSources();
				gwf.setParasToNodes(toNodes);
				gwf.Save();

				WorkNode firstwn = new WorkNode(wk, nd);

				Node toNode = new Node(toNodeStr);
				msg = firstwn.NodeSend(toNode, gwf.getStarter()).ToMsgOfHtml();
			}
			else
			{
				msg = Dev2Interface.WorkOpt_SendToNodes(this.getFlowNo(), this.getNodeID(), this.getWorkID(), this.getFID(), toNodes).ToMsgOfHtml();
			}
		}
		catch (RuntimeException ex)
		{

			return ex.getMessage();
		}

		GenerWorkFlow gwfw = new GenerWorkFlow();
		gwfw.setWorkID(this.getWorkID());
		gwfw.RetrieveFromDBSources();
		if (nd.getItIsRememberMe() == true)
		{
			gwfw.setParasToNodes(toNodes);
		}
		else
		{
			gwfw.setParasToNodes("");
		}
		gwfw.Save();

		//当前节点.
		Node currNode = new Node(this.getNodeID());
		Flow currFlow = new Flow(this.getFlowNo());


		///#region 处理发送后转向.
		try
		{
			/*处理转向问题.*/
			switch (currNode.getHisTurnToDeal())
			{
				case SpecUrl:
					String myurl = currNode.getTurnToDealDoc();
					if (myurl.contains("?") == false)
					{
						myurl += "?1=1";
					}
					Attrs myattrs = currNode.getHisWork().getEnMap().getAttrs();
					Work hisWK = currNode.getHisWork();
					for (Attr attr : myattrs)
					{
						if (myurl.contains("@") == false)
						{
							break;
						}
						myurl = myurl.replace("@" + attr.getKey(), hisWK.GetValStrByKey(attr.getKey()));
					}
					myurl = myurl.replace("@WebUser.No", WebUser.getNo());
					myurl = myurl.replace("@WebUser.Name", WebUser.getName());
					myurl = myurl.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
					myurl = myurl.replace("@WebUser.DeptNo", WebUser.getDeptNo());

					if (myurl.contains("@"))
					{
						Dev2Interface.Port_SendMsg("admin", currFlow.getName() + "在" + currNode.getName() + "节点处，出现错误", "流程设计错误，在节点转向url中参数没有被替换下来。Url:" + myurl, "Err" + currNode.getNo() + "_" + this.getWorkID(), SMSMsgType.Err, this.getFlowNo(), this.getNodeID(), this.getWorkID(), this.getFID());
						throw new RuntimeException("流程设计错误，在节点转向url中参数没有被替换下来。Url:" + myurl);
					}

					if (myurl.contains("PWorkID") == false)
					{
						myurl += "&PWorkID=" + this.getWorkID();
					}

					myurl += "&FromFlow=" + this.getFlowNo() + "&FromNode=" + this.getNodeID() + "&UserNo=" + WebUser.getNo() + "&Token=" + WebUser.getToken();
					return "TurnUrl@" + myurl;
				case TurnToByCond:

					return msg;
				default:
					msg = msg.replace("@WebUser.No", WebUser.getNo());
					msg = msg.replace("@WebUser.Name", WebUser.getName());
					msg = msg.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
					msg = msg.replace("@WebUser.DeptNo", WebUser.getDeptNo());
					return msg;
			}

			///#endregion

		}
		catch (RuntimeException ex)
		{
			if (ex.getMessage().contains("请选择下一步骤工作") == true || ex.getMessage().contains("用户没有选择发送到的节点") == true)
			{
				if (currNode.getCondModel() == DirCondModel.ByLineCond)
				{
					return "url@./WorkOpt/ToNodes.htm?FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID() + "&WorkID=" + this.getWorkID() + "&FID=" + this.getFID();
				}

				return "err@下一个节点的接收人规则是，当前节点选择来选择，在当前节点属性里您没有启动接受人按钮，系统自动帮助您启动了，请关闭窗口重新打开。" + ex.getMessage();
			}

			GenerWorkFlow HisGenerWorkFlow = new GenerWorkFlow(this.getWorkID());
			//防止发送失败丢失接受人，导致不能出现下拉方向选择框. @杜.
			if (HisGenerWorkFlow != null)
			{
				//如果是会签状态.
				if (HisGenerWorkFlow.getHuiQianTaskSta() == HuiQianTaskSta.HuiQianing)
				{
					//如果是主持人.
					if (Objects.equals(HisGenerWorkFlow.getHuiQianZhuChiRen(), WebUser.getNo()))
					{
						String empStrSepc = WebUser.getNo() + "," + WebUser.getName() + ";";
						if (HisGenerWorkFlow.getTodoEmps().contains(empStrSepc) == false)
						{
							HisGenerWorkFlow.setTodoEmps(HisGenerWorkFlow.getTodoEmps() + empStrSepc);
							HisGenerWorkFlow.Update();
						}
					}
					else
					{
						//非主持人.
						String empStrSepc = WebUser.getNo() + "," + WebUser.getName() + ";";
						if (HisGenerWorkFlow.getTodoEmps().contains(empStrSepc) == false)
						{
							HisGenerWorkFlow.setTodoEmps(HisGenerWorkFlow.getTodoEmps() + empStrSepc);
							HisGenerWorkFlow.Update();
						}
					}
				}


				if (HisGenerWorkFlow.getHuiQianTaskSta() != HuiQianTaskSta.HuiQianing)
				{
					if (HisGenerWorkFlow.getTodoEmps().contains(WebUser.getNo() + ",") == false)
					{
						HisGenerWorkFlow.setTodoEmps(HisGenerWorkFlow.getTodoEmps() + WebUser.getNo() + "," + WebUser.getName() + ";");
						HisGenerWorkFlow.Update();
					}
				}
			}
			return ex.getMessage();

		}
	}

	///#endregion tonodes


	///#region 自定义.
	public String TZWorkerRole_Init() throws Exception {
		DataSet ds = new DataSet();

		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		/*if (gwf.getTransferCustomType() != TransferCustomType.ByWorkerSet)
		{
			gwf.setTransferCustomType(TransferCustomType.ByWorkerSet);
			gwf.Update();
		}
		*/
		ds.Tables.add(gwf.ToDataTableField("WF_GenerWorkFlow"));

		//当前运行到的节点
		Node currNode = new Node(gwf.getNodeID());

		//所有的节点s.
		BtnLabs nds = new BtnLabs();
		nds.Retrieve("FK_Flow", this.getFlowNo(), "NodeID");
		ds.Tables.add(nds.ToDataTableField("WF_Node"));

		//工作人员列表.已经走完的节点与人员.
		GenerWorkerLists gwls = new GenerWorkerLists(this.getWorkID());
		GenerWorkerList gwln = (GenerWorkerList)gwls.GetEntityByKey(GenerWorkerListAttr.FK_Node, this.getNodeID());
		if (gwln == null)
		{
			gwln = new GenerWorkerList();
			gwln.setNodeID(currNode.getNodeID());
			gwln.setNodeName(currNode.getName());
			gwln.setEmpNo(WebUser.getNo());
			gwln.setEmpName(WebUser.getName());
			gwls.AddEntity(gwln);
		}
		ds.Tables.add(gwls.ToDataTableField("WF_GenerWorkerlist"));

		SelectAccpers accpers = new SelectAccpers();
		accpers.Retrieve("WorkID", this.getWorkID(), "FK_Node");

		ds.Tables.add(accpers.ToDataTableField("WF_SelectAccpers"));

		DataTable dtsa = ds.get("WF_SelectAccpers");
		if (dtsa.Rows.size() > 0)
		{
			// 创建一个用于存储匹配行的列表
			List<DataRow> matchedRows = new ArrayList<>();

			// 遍历第一个DataTable的行
			DataTable dtgwf = ds.get("WF_GenerWorkFlow");
			for (DataRow row1 : dtgwf.Rows)
			{
				String kf_node_row1 = row1.getValue("FK_Node") == null ? "0" : row1.getValue("FK_Node").toString();
				long fkNodeValue1 = Long.parseLong(kf_node_row1);

				// 遍历第二个DataTable的行
				for (DataRow row2 : dtsa.Rows)
				{
					String kf_node_row2 = row2.getValue("FK_Node") == null ? "0" : row2.getValue("FK_Node").toString();
					long fkNodeValue2 = Long.parseLong(kf_node_row2);

					// 检查FK_Node字段值是否相同
					if (fkNodeValue1 != fkNodeValue2)
					{
						// 将匹配的行添加到列表中
						matchedRows.add(row2);
					}
				}
			}
			if (matchedRows.size() > 0)
			{
				return bp.tools.Json.ToJson(ds);
			}
		}

        ///#region 执行计算未来处理人.

		Work wk = currNode.getHisWork();
		wk.setOID(this.getWorkID());
		wk.Retrieve();
		WorkNode wn = new WorkNode(wk, currNode);
		boolean isFullSA = wn.getHisFlow().getItIsFullSA();
		wn.getHisFlow().setItIsFullSA(true);

		//执行计算未来处理人.
		FullSA fsa = new FullSA();
		fsa.DoIt2023(wn);
        ///#endregion 执行计算未来处理人
		if(isFullSA == false)
		{
			wn.getHisFlow().setItIsFullSA(false);
		}

		return bp.tools.Json.ToJson(ds);
	}
	/// <summary>
	/// 重置未来计算人
	/// </summary>
	/// <returns></returns>
	public String TZWorkerRole_Reset() throws Exception {
		DataSet ds = new DataSet();

		long workID = this.getWorkID();
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		if (gwf.getTransferCustomType() != TransferCustomType.ByWorkerSet)
		{
			gwf.setTransferCustomType(TransferCustomType.ByWorkerSet);
			gwf.Update();
		}

		ds.Tables.add(gwf.ToDataTableField("WF_GenerWorkFlow"));

		//当前运行到的节点
		Node currNode = new Node(gwf.getNodeID());

		//所有的节点s.
		BtnLabs nds = new BtnLabs();
		nds.Retrieve("FK_Flow", this.getFlowNo(), "NodeID");
		ds.Tables.add(nds.ToDataTableField("WF_Node"));

		//工作人员列表.已经走完的节点与人员.
		GenerWorkerLists gwls = new GenerWorkerLists(workID);
		GenerWorkerList gwln = (GenerWorkerList)gwls.GetEntityByKey(GenerWorkerListAttr.FK_Node, this.getNodeID());
		if (gwln == null)
		{
			gwln = new GenerWorkerList();
			gwln.setNodeID(currNode.getNodeID());
			gwln.setNodeName(currNode.getName());
			gwln.setEmpNo(WebUser.getNo());
			gwln.setEmpName(WebUser.getName());
			gwls.AddEntity(gwln);
		}
		ds.Tables.add(gwls.ToDataTableField("WF_GenerWorkerlist"));
        ///#region 执行计算未来处理人.

		Work wk = currNode.getHisWork();
		wk.setOID(workID);
		wk.Retrieve();
		WorkNode wn = new WorkNode(wk, currNode);
		wn.getHisFlow().setItIsFullSA(true);

		//执行计算未来处理人.
		FullSA fsa = new FullSA();
		fsa.DoIt2023(wn);
        ///#endregion 执行计算未来处理人.

		SelectAccpers accpers = new SelectAccpers();
		accpers.Retrieve("WorkID", workID, "FK_Node");

		ds.Tables.add(accpers.ToDataTableField("WF_SelectAccpers"));

		return bp.tools.Json.ToJson(ds);
	}
	/**
	 初始化

	 @return
	 */
	public final String TransferCustom_Init() throws Exception {
		DataSet ds = new DataSet();

		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		if (gwf.getTransferCustomType() != TransferCustomType.ByWorkerSet)
		{
			gwf.setTransferCustomType(TransferCustomType.ByWorkerSet);
			gwf.Update();
		}

		ds.Tables.add(gwf.ToDataTableField("WF_GenerWorkFlow"));

		//当前运行到的节点
		Node currNode = new Node(gwf.getNodeID());

		//所有的节点s.
		Nodes nds = new Nodes(this.getFlowNo());
		//ds.Tables.add(nds.ToDataTableField("WF_Node"));

		//工作人员列表.已经走完的节点与人员.
		GenerWorkerLists gwls = new GenerWorkerLists(this.getWorkID());
		GenerWorkerList gwln = (GenerWorkerList)gwls.GetEntityByKey(GenerWorkerListAttr.FK_Node, this.getNodeID());
		if (gwln == null)
		{
			gwln = new GenerWorkerList();
			gwln.setNodeID(currNode.getNodeID());
			gwln.setNodeName(currNode.getName());
			gwln.setEmpNo(WebUser.getNo());
			gwln.setEmpName(WebUser.getName());
			gwls.AddEntity(gwln);
		}
		ds.Tables.add(gwls.ToDataTableField("WF_GenerWorkerlist"));

		//设置的手工运行的流转信息.
		TransferCustoms tcs = new TransferCustoms(this.getWorkID());
		if (tcs.size() == 0)
		{

			///#region 执行计算未来处理人.

			Work wk = currNode.getHisWork();
			wk.setOID(this.getWorkID());
			wk.Retrieve();
			WorkNode wn = new WorkNode(wk, currNode);
			wn.getHisFlow().setItIsFullSA(true);

			//执行计算未来处理人.
			FullSA fsa = new FullSA();
			fsa.DoIt2023(wn);

			///#endregion 执行计算未来处理人.

			for (Node nd : nds.ToJavaList())
			{
				if (nd.getNodeID()== this.getNodeID())
				{
					continue;
				}
				if (nd.GetParaBoolen(NodeAttr.IsYouLiTai) == false)
				{
					continue;
				}
				GenerWorkerList gwl = (GenerWorkerList)gwls.GetEntityByKey(GenerWorkerListAttr.FK_Node, nd.getNodeID());
				if (gwl == null)
				{
					/*说明没有 */
					TransferCustom tc = new TransferCustom();
					tc.setWorkID(this.getWorkID());
					tc.setNodeID(nd.getNodeID());
					tc.setNodeName(nd.getName());


					///#region 计算出来当前节点的工作人员.
					SelectAccpers sas = new SelectAccpers();
					sas.Retrieve(SelectAccperAttr.WorkID, this.getWorkID(), SelectAccperAttr.FK_Node, nd.getNodeID(), null);

					String workerID = "";
					String workerName = "";
					for (SelectAccper sa : sas.ToJavaList())
					{
						workerID += sa.getEmpNo() + ",";
						workerName += sa.getEmpName() + ",";
					}

					///#endregion 计算出来当前节点的工作人员.

					tc.setWorker(workerID);
					tc.setWorkerName(workerName);
					tc.setIdx(nd.getStep());
					tc.setItIsEnable(true);
					if (nd.getHisCHWay() == CHWay.ByTime && nd.GetParaInt("CHWayOfTimeRole", 0) == 2)
					{
						tc.setPlanDT(DateUtils.format(DateUtils.addDay(new Date(), 1), DataType.getSysDateTimeFormat()));
					}
					tc.Insert();
				}
			}
			tcs = new TransferCustoms(this.getWorkID());
		}

		ds.Tables.add(tcs.ToDataTableField("WF_TransferCustoms"));

		return bp.tools.Json.ToJson(ds);
	}

	///#endregion 自定义.


	///#region 时限初始化数据
	public final String CH_Init() throws Exception {
		DataSet ds = new DataSet();

		//获取处理信息的列表
		GenerWorkerLists gwls = new GenerWorkerLists();
		gwls.Retrieve(GenerWorkerListAttr.FK_Flow, this.getFlowNo(), GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.RDT);
		DataTable dt = gwls.ToDataTableField("WF_GenerWorkerlist");
		ds.Tables.add(dt);

		//获取流程信息
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		ds.Tables.add(gwf.ToDataTableField("WF_GenerWorkFlow"));

		Flow flow = new Flow(this.getFlowNo());
		ds.Tables.add(flow.ToDataTableField("WF_Flow"));

		//获取流程流转自定义的数据
		String sql = "SELECT FK_Node AS NodeID,NodeName AS Name From WF_TransferCustom WHERE WorkID=" + getWorkID() + " AND IsEnable=1 Order By Idx";
		DataTable dtYL = DBAccess.RunSQLReturnTable(sql);

		//删除不启用的游离态节点时限设置
		sql = "DELETE FROM WF_CHNode WHERE WorkID=" + getWorkID() + " AND FK_Node IN(SELECT FK_Node FROM WF_TransferCustom WHERE WorkID=" + getWorkID() + " AND IsEnable=0 )";
		DBAccess.RunSQL(sql);

		//节点时限表
		CHNodes chNodes = new CHNodes(this.getWorkID());
		///#region 获取流程节点信息的列表
		Nodes nds = new Nodes(this.getFlowNo());
		//如果是游离态的节点有可能调整顺序
		dt = new DataTable();
		dt.TableName = "WF_Node";
		dt.Columns.Add("NodeID");
		dt.Columns.Add("Name");
		dt.Columns.Add("SDTOfNode"); //节点应完成时间
		dt.Columns.Add("PlantStartDt"); //节点计划开始时间
		dt.Columns.Add("GS"); //工时

		DataRow dr;
		boolean isFirstY = true;
		//上一个节点的时间
		String beforeSDTOfNode = "";
		//先排序运行过的节点
		CHNode chNode = null;
		for (GenerWorkerList gwl : gwls.ToJavaList())
		{
			bp.en.Entity tempVar = chNodes.GetEntityByKey(CHNodeAttr.FK_Node, gwl.getNodeID());
			chNode = tempVar instanceof CHNode ? (CHNode)tempVar : null;
			if (chNode != null)
			{
				chNode.SetPara("RDT", gwl.getRDT());
				chNode.SetPara("CDT", gwl.getCDT());
				chNode.SetPara("IsPass", gwl.getItIsPass());
				chNode.setStartDT(gwl.getRDT());
				chNode.setEndDT(gwl.getCDT());
				chNode.setEmpNo(gwl.getEmpNo());
				chNode.setEmpT(gwl.getEmpName());
				continue;

			}
			chNode = new CHNode();
			chNode.setWorkID(this.getWorkID());
			chNode.setNodeID(gwl.getNodeID());
			chNode.setNodeName(gwl.getNodeName());
			chNode.setStartDT(gwl.getRDT());
			chNode.setEndDT(gwl.getCDT());
			chNode.setEmpNo(gwl.getEmpNo());
			chNode.setEmpT(gwl.getEmpName());
			chNode.SetPara("RDT", gwl.getRDT());
			chNode.SetPara("CDT", gwl.getCDT());
			chNode.SetPara("IsPass", gwl.getItIsPass());
			chNodes.AddEntity(chNode);
			beforeSDTOfNode = gwl.getCDT();
		}
		for (Node node : nds.ToJavaList())
		{
			bp.en.Entity tempVar2 = gwls.GetEntityByKey(GenerWorkerListAttr.FK_Node, node.getNodeID());
			GenerWorkerList gwl = tempVar2 instanceof GenerWorkerList ? (GenerWorkerList)tempVar2 : null;
			if (gwl != null)
			{
				continue;
			}

			//已经设定
			bp.en.Entity tempVar3 = chNodes.GetEntityByKey(CHNodeAttr.FK_Node, node.getNodeID());
			chNode = tempVar3 instanceof CHNode ? (CHNode)tempVar3 : null;
			if (chNode != null)
			{
				continue;
			}

			String sdtOfNode = "";
			String plantStartDt = "";
			if (node.GetParaBoolen("IsYouLiTai") == true)
			{
				if (isFirstY == true)
				{
					for (DataRow drYL : dtYL.Rows)
					{
						bp.en.Entity tempVar4 = chNodes.GetEntityByKey(CHNodeAttr.FK_Node, Integer.parseInt(drYL.get("NodeID").toString()));
						chNode = tempVar4 instanceof CHNode ? (CHNode)tempVar4 : null;
						if (chNode != null)
						{
							continue;
						}
						chNode = new CHNode();
						chNode.setWorkID(this.getWorkID());
						chNode.setNodeID(Integer.parseInt(drYL.get("NodeID").toString()));
						chNode.setNodeName(drYL.get("Name").toString());
						//计划开始时间
						plantStartDt = beforeSDTOfNode;
						chNode.setStartDT(plantStartDt);
						//计划完成时间
						sdtOfNode = getSDTOfNode(node, beforeSDTOfNode, gwf);
						chNode.setEndDT(sdtOfNode);
						//工时
						int gty = 0;
						if (DataType.IsNullOrEmpty(plantStartDt) == false && DataType.IsNullOrEmpty(sdtOfNode) == false)
						{
							gty = DataType.GetSpanDays(plantStartDt, sdtOfNode);
						}

						chNode.setGT(gty);

						beforeSDTOfNode = sdtOfNode;
						chNodes.AddEntity(chNode);
					}
					isFirstY = false;
				}
				continue;
			}
			chNode = new CHNode();
			chNode.setWorkID(this.getWorkID());
			chNode.setNodeID(node.getNodeID());
			chNode.setNodeName(node.getName());

			//计划开始时间
			plantStartDt = beforeSDTOfNode;
			chNode.setStartDT(plantStartDt);

			//计划完成时间
			sdtOfNode = getSDTOfNode(node, beforeSDTOfNode, gwf);
			chNode.setEndDT(sdtOfNode);

			//计算初始值工天
			int gs = 0;
			if (DataType.IsNullOrEmpty(plantStartDt) == false && DataType.IsNullOrEmpty(sdtOfNode) == false)
			{
				gs = DataType.GetSpanDays(plantStartDt, sdtOfNode);
			}
			chNode.setGT(gs);
			beforeSDTOfNode = sdtOfNode;
			chNodes.AddEntity(chNode);

		}

		///#endregion 流程节点信息

		ds.Tables.add(chNodes.ToDataTableField("WF_CHNode"));
		//获取当前节点信息
		Node nd = new Node(this.getNodeID());
		ds.Tables.add(nd.ToDataTableField("WF_CurrNode"));

		///#region 获取剩余天数
		Part part = new Part();
		part.setMyPK(nd.getFlowNo() + "_0_DeadLineRole");
		int count = part.RetrieveFromDBSources();
		int day = 0; //含假期的天数
		Date dateT = new Date();
		if (count > 0)
		{
			//判断是否包含假期
			if (Integer.parseInt(part.getTag4()) == 0)
			{
				String holidays = GloVar.getHolidays();
				while (true)
				{
					if (dateT.compareTo(DataType.ParseSysDate2DateTime(gwf.getSDTOfFlow())) >= 0)
					{
						break;
					}

					if (holidays.contains(DataType.getCurrentDateByFormart("MM-dd")))
					{
						dateT = DataType.AddDays(dateT,1);
						day++;
						continue;
					}
					dateT = DataType.AddDays(dateT,1);
				}

			}

		}
		String spanTime = GetSpanTime(new Date(), DataType.ParseSysDate2DateTime(gwf.getSDTOfFlow()), day);
		dt = new DataTable();
		dt.TableName = "SpanTime";
		dt.Columns.Add("SpanTime");
		dr = dt.NewRow();
		dr.setValue("SpanTime", spanTime);
		dt.Rows.add(dr);
		ds.Tables.add(dt);

		///#endregion 获取剩余天数

		return bp.tools.Json.ToJson(ds);
	}


	private String getSDTOfNode(Node node, String beforeSDTOfNode, GenerWorkFlow gwf) throws Exception {
		Date SDTOfNode = new Date();
		if (DataType.IsNullOrEmpty(beforeSDTOfNode))
		{
			beforeSDTOfNode = gwf.getSDTOfNode();
		}
		//按天、小时考核
		if (node.GetParaInt("CHWayOfTimeRole", 0) == 0)
		{
			//增加天数. 考虑到了节假日.
			int timeLimit = node.getTimeLimit();
			SDTOfNode = Glo.AddDayHoursSpan(DateUtils.parse(beforeSDTOfNode), node.getTimeLimit(),
					node.getTimeLimitHH(), node.getTimeLimitMM(), node.getTWay());
		}
		//按照节点字段设置
		if (node.GetParaInt("CHWayOfTimeRole", 0) == 1)
		{
			//获取设置的字段、
			String keyOfEn = node.GetParaString("CHWayOfTimeRoleField");
			if (DataType.IsNullOrEmpty(keyOfEn) == true)
			{
				node.setHisCHWay(CHWay.None);
			}
			else
			{
				SDTOfNode = DataType.ParseSysDateTime2DateTime(node.getHisWork().GetValByKey(keyOfEn).toString());
			}

		}
		return DateUtils.format(SDTOfNode, DataType.getSysDateTimeFormat());
	}

	///#endregion 时限初始化数据


	///#region 节点时限重新设置
	public final String CH_Save() throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		//获取流程应完成时间
		String sdtOfFow = this.GetRequestVal("GWF");
		if (DataType.IsNullOrEmpty(sdtOfFow) == false && !Objects.equals(gwf.getSDTOfFlow(), sdtOfFow))
		{
			gwf.setSDTOfFlow(sdtOfFow);
		}

		//获取节点的时限设置
		Nodes nds = new Nodes(this.getFlowNo());
		CHNode chNode = null;
		for (Node nd : nds.ToJavaList())
		{
			chNode = new CHNode();
			String startDT = this.GetRequestVal("StartDT_" + nd.getNodeID());
			String endDT = this.GetRequestVal("EndDT_" + nd.getNodeID());
			int gt = this.GetRequestValInt("GT_" + nd.getNodeID());
			float scale = this.GetRequestValFloat("Scale_" + nd.getNodeID());
			float totalScale = this.GetRequestValFloat("TotalScale_" + nd.getNodeID());

			chNode.setWorkID(this.getWorkID());
			chNode.setNodeID(nd.getNodeID());
			chNode.setNodeName(nd.getName());
			if (DataType.IsNullOrEmpty(startDT) == false)
			{
				chNode.setStartDT(startDT);
			}
			if (DataType.IsNullOrEmpty(endDT) == false)
			{
				chNode.setEndDT(endDT);
			}

			chNode.setGT(gt);
			chNode.setScale(scale);
			chNode.setTotalScale(totalScale);
			chNode.Save();
		}
		gwf.Update();
		return "保存成功";
	}

	///#endregion 节点时限重新设置


	///#region 节点备注的设置
	public final String Note_Init()
	{
		Paras ps = new Paras();
		ps.SQL = "SELECT * FROM ND" + Integer.parseInt(this.getFlowNo()) + "Track WHERE ActionType=" + SystemConfig.getAppCenterDBVarStr() + "ActionType AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID";
		ps.Add("ActionType", ActionType.FlowBBS.getValue());
		ps.Add("WorkID", this.getWorkID());

		//转化成json
		return bp.tools.Json.ToJson(DBAccess.RunSQLReturnTable(ps));
	}

	/**
	 * 公文版本
	 * @return
	 */
	public final String OfficeTrack_Init(){
		int flowNo=Integer.parseInt(this.getFlowNo());
		Long workID=this.getWorkID();
		String tableTrack= "ND" + flowNo + "Track";

		Paras ps = new Paras();
		ps.SQL = "SELECT MyPK,NDFrom,NDFromT,Msg,EmpFrom,EmpFromT,RDT,Msg FROM  "+tableTrack+" WHERE ActionType=" + SystemConfig.getAppCenterDBVarStr() + "ActionType AND WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID ORDER BY RDT ";
		ps.Add("ActionType", ActionType.GovDoc.getValue());
		ps.Add("WorkID", workID);

		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		//转化成json
		return bp.tools.Json.ToJson(DBAccess.RunSQLReturnTable(ps));
	}

	/**
	 保存备注.

	 @return
	 */
	public final String Note_Save() throws Exception {
		String msg = this.GetRequestVal("Msg");
		//需要删除track表中的数据是否存在备注
		String sql = "DELETE From ND" + Integer.parseInt(this.getFlowNo()) + "Track WHERE WorkID=" + getWorkID() + " AND NDFrom=" + this.getNodeID() + " AND EmpFrom='" + WebUser.getNo() + "' And ActionType=" + ActionType.FlowBBS.getValue();
		DBAccess.RunSQL(sql);
		//增加track
		Node nd = new Node(this.getNodeID());
		Glo.AddToTrack(ActionType.FlowBBS, this.getFlowNo(), this.getWorkID(), this.getFID(), nd.getNodeID(), nd.getName(), WebUser.getNo(), WebUser.getName(), nd.getNodeID(), nd.getName(), WebUser.getNo(), WebUser.getName(), msg, null);

		//发送消息
		String empsStrs = DBAccess.RunSQLReturnStringIsNull("SELECT Emps FROM WF_GenerWorkFlow WHERE WorkID=" + this.getWorkID(), "");
		String[] myEmpStrs = empsStrs.split("[@]", -1);
		//标题
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		String title = "流程名称为" + gwf.getFlowName() + "标题为" + gwf.getTitle() + "在节点增加备注说明" + msg;

		for (String emp : myEmpStrs)
		{
			if (DataType.IsNullOrEmpty(emp))
			{
				continue;
			}
			String[] emps = emp.split("[,]", -1);
			String empNo = emps[0];

			//获得当前人的邮件.
			bp.wf.port.WFEmp empEn = new bp.wf.port.WFEmp(empNo);

			Dev2Interface.Port_SendMsg(empEn.getNo(), title, msg, null, "NoteMessage", this.getFlowNo(), this.getNodeID(), this.getWorkID(), this.getFID());

		}
		return "保存成功";
	}


	///#endregion 节点备注的设置

	private static String GetSpanTime(Date t1, Date t2, int day) {
		Date span = new Date(t2.getTime() - t1.getTime());

		int days = DateUtils.getDay(span);

		int hours = DateUtils.getHour(span);

		int minutes = DateUtils.getMinute(span);

		if (days == 0 && hours == 0 && minutes == 0) {
			minutes = DateUtils.getSecond(span) > 0 ? 1 : 0;
		}

		String spanStr = "";

		if (days > 0) {
			spanStr += (days - day) + "天";
		}

		if (hours > 0) {
			spanStr += hours + "时";
		}

		if (minutes > 0) {
			spanStr += minutes + "分";
		}

		if (spanStr.length() == 0) {
			spanStr = "0分";
		}

		return spanStr;
	}
	/**
	 流程的常用语

	 @return
	 */
	public final String UsefulExpresFlow_Init() throws Exception {
		//AttrKey =WorkCheck, FlowBBS, WorkReturn
		String attrKey = this.GetRequestVal("AttrKey");

		FastInputs ens = new FastInputs();
		ens.Retrieve(FastInputAttr.CfgKey, "Flow", FastInputAttr.EnsName, "Flow", FastInputAttr.AttrKey, attrKey, FastInputAttr.FK_Emp, WebUser.getNo(), null);

		if (!ens.isEmpty())
		{
			return ens.ToJson("dt");
		}

		if (attrKey.equals("Comment"))
		{
			FastInput en = new FastInput();
			en.setMyPK(DBAccess.GenerGUID(0, null, null));
			en.setEnsName("Flow");
			en.setCfgKey("Flow");
			en.setAttrKey(attrKey);
			en.setVals("已阅");
			en.setEmpNo(WebUser.getNo());
			en.Insert();
		}
		if (attrKey.equals("CYY"))
		{
			FastInput en = new FastInput();
			en.setMyPK(DBAccess.GenerGUID(0, null, null));
			en.setEnsName("Flow");
			en.setCfgKey("Flow");
			en.setAttrKey(attrKey);
			en.setVals("同意");
			en.setEmpNo(WebUser.getNo());
			en.Insert();

			en = new FastInput();
			en.setMyPK(DBAccess.GenerGUID(0, null, null));
			en.setEnsName("Flow");
			en.setCfgKey("Flow");
			en.setAttrKey(attrKey);
			en.setVals("不同意");
			en.setEmpNo(WebUser.getNo());
			en.Insert();

			en = new FastInput();
			en.setMyPK(DBAccess.GenerGUID(0, null, null));
			en.setEnsName("Flow");
			en.setCfgKey("Flow");
			en.setAttrKey(attrKey);
			en.setVals("同意，请领导批示");
			en.setEmpNo(WebUser.getNo());
			en.Insert();

			en = new FastInput();
			en.setMyPK(DBAccess.GenerGUID(0, null, null));
			en.setEnsName("Flow");
			en.setCfgKey("Flow");
			en.setAttrKey(attrKey);
			en.setVals("同意办理");
			en.setEmpNo(WebUser.getNo());
			en.Insert();

			en = new FastInput();
			en.setMyPK(DBAccess.GenerGUID(0, null, null));
			en.setEnsName("Flow");
			en.setCfgKey("Flow");
			en.setAttrKey(attrKey);
			en.setVals("情况属实报领导批准");
			en.setEmpNo(WebUser.getNo());
			en.Insert();
		}

		ens.Retrieve(FastInputAttr.CfgKey, "Flow", FastInputAttr.EnsName, "Flow", FastInputAttr.AttrKey, attrKey, FastInputAttr.FK_Emp, WebUser.getNo(), null);
		return ens.ToJson("dt");
	}


	///#region
	/**
	 批量发起子流程.

	 @return
	 */
	public final String SubFlowGuid_Send() throws Exception {
		//获得选择的实体信息. 格式为: 001@运输司,002@法制司
		String selectNos = GetRequestVal("SelectNos");
		if (DataType.IsNullOrEmpty(selectNos) == true)
			return "err@没有选择需要启动子流程的信息";
		String subFlowMyPK = GetRequestVal("SubFlowMyPK");
		//前置导航的子流程的配置.
		SubFlowHandGuide subFlow = new SubFlowHandGuide(subFlowMyPK);
		SubFlowHand subFlowH = new SubFlowHand(subFlowMyPK);
		Hashtable ht=this.GenerBSParas();
		//选择的编号. selectNos格式为 001@开发司,002@运输司,
		String[] strs = selectNos.split("[,]", -1);
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		try{
			//启用线程的时候设置持有上下文的Request容器
			ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			RequestContextHolder.setRequestAttributes(servletRequestAttributes,true);
			final int POOL_SIZE = strs.length;
			ThreadPoolExecutor executor = new ThreadPoolExecutor(
					POOL_SIZE,
					POOL_SIZE,
					POOL_SIZE, TimeUnit.SECONDS,
					new ArrayBlockingQueue<>(POOL_SIZE),
					new ThreadPoolExecutor.CallerRunsPolicy());
			List<CompletableFuture<Void>> futures = new ArrayList<CompletableFuture<Void>>();
			for (String str : strs)
			{
				if (DataType.IsNullOrEmpty(str) == true)
					continue;
				SystemConfig.setIsBSsystem(false);
				CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
					try {
						bp.da.Log.DebugWriteInfo("str=" + str + "开始,Time=" + DataType.getCurrentDateTimeCNOfLong());
						// str的格式为:002@运输司
						String[] enNoName = str.split("[@]", -1);
						if (enNoName.length < 2)
							throw new Exception("err@" + enNoName[0] + "不存在名称");
						//获得实体的名字,编号.
						//String enNo = enNoName[0];
						//String enName = enNoName[1];
						//发送单个子流程信息
						SendSingleSubFlow(subFlowH,ht,gwf);

					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}, executor);
				futures.add(future);

			}
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
			executor.shutdown();
			//SystemConfig.setIsBSsystem(true);
		}catch(Exception ex){
			Log.DebugWriteError("发送子流程部分失败:"+ex.getMessage());
			return "err@发送子流程部分失败";
		}
		if (subFlow.getSubFlowHidTodolist() == true)
		{
			//发送子流程后不显示父流程待办，设置父流程已经的待办已经处理 100
			DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=100 Where WorkID=" + this.getWorkID() + " AND FK_Node=" + this.getFK_Node());

		}
		return "发起子流程成功";
	}
	private final void SendSingleSubFlow(SubFlowHand subFlowH,Hashtable ht,GenerWorkFlow gwf) throws Exception {
		long workidSubFlow = 0;
		if (subFlowH.getSubFlowModel() == SubFlowModel.SubLevel)
		{
			workidSubFlow = bp.wf.Dev2Interface.Node_CreateBlankWork(subFlowH.getSubFlowNo(), ht, null, WebUser.getNo(), null, gwf.getWorkID(), gwf.getFID(), gwf.getFlowNo(), gwf.getNodeID());
		}
		if (subFlowH.getSubFlowModel() == SubFlowModel.SameLevel)
		{
			workidSubFlow = bp.wf.Dev2Interface.Node_CreateBlankWork(subFlowH.getSubFlowNo(), ht, null, WebUser.getNo(), null, gwf.getPWorkID(), gwf.getPFID(), gwf.getPFlowNo(), gwf.getPNodeID());
		}

		//设置父子关系
		if (subFlowH.getSubFlowModel() == SubFlowModel.SubLevel)
		{
			//Dev2Interface.SetParentInfo(subFlowH.getSubFlowNo(), workidSubFlow, this.getWorkID(), WebUser.getNo(), this.getFK_Node());
		}
		if (subFlowH.getSubFlowModel() == SubFlowModel.SameLevel)
		{
			//Dev2Interface.SetParentInfo(subFlowH.getSubFlowNo(), workidSubFlow, gwf.getPWorkID(), WebUser.getNo(), gwf.getPNodeID()); //父子关系
			//设置同级关系
			GenerWorkFlow subGWF = new GenerWorkFlow(workidSubFlow);
			subGWF.SetPara("SLFlowNo", gwf.getFlowNo());
			subGWF.SetPara("SLNodeID", gwf.getNodeID());
			subGWF.SetPara("SLWorkID", gwf.getWorkID());
			subGWF.SetPara("SLEmp", WebUser.getNo());
			subGWF.Update();
		}
		DBAccess.RunSQL("update wf_generWorkFlow SET WFState=1 where workid="+workidSubFlow);
		Dev2Interface.Node_SendWork(subFlowH.getSubFlowNo(), workidSubFlow);
	}
	private   Hashtable GenerBSParas()
	{

		Hashtable ht=new Hashtable();
		if (SystemConfig.isBSsystem() == true)
		{
			String expKeys = ",OID,DoType,HttpHandlerName,DoMethod,t,PWorkID,WorkID,";
			for (String k : CommonUtils.getRequest().getParameterMap().keySet())
			{
				if (k == null )	continue;
				if (expKeys.contains(','+k+',')) continue;

				if (ht.containsKey(k))
				{
					ht.put(k, bp.sys.Glo.getRequest().getParameter(k));
				}
				else
				{
					ht.put(k, bp.sys.Glo.getRequest().getParameter(k));
				}
			}
		}

		return ht;
	}
	/**
	 会签子流程-删除草稿

	 @return
	 */
	public final String SubFlowGuid_DeleteSubFlowDraf() throws Exception {
		Node nd = new Node(Integer.parseInt(this.getFlowNo() + "01"));
		String sql = "SELECT PTable FROM Sys_MapData WHERE No='" + nd.getNodeFrmID() + "'";
		String pTableOfSubFlow = DBAccess.RunSQLReturnString(sql);

		DBAccess.RunSQL("DELETE FROM WF_GenerWorkFlow WHERE WorkID=" + this.getWorkID());
		DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + this.getWorkID());
		DBAccess.RunSQL("DELETE FROM " + pTableOfSubFlow + " WHERE OID=" + this.getWorkID());
		return "删除成功";
	}

	///#endregion 会签.


	public final String JumpWay_Init() throws Exception {
		Node node = new Node(this.getNodeID());
		String sql = "";
		switch (node.getJumpWay())
		{
			case   CanNotJump: //不跳转
				break;
			case   Previous: //向前跳转
				sql = "SELECT NodeID,Name FROM WF_Node WHERE NodeID IN (SELECT FK_Node FROM WF_GenerWorkerlist WHERE WorkID=" + this.getWorkID() + " )";
				break;
			case   Next: //向后跳转
				sql = "SELECT NodeID,Name FROM WF_Node WHERE NodeID NOT IN (SELECT FK_Node FROM WF_GenerWorkerlist WHERE WorkID=" + this.getWorkID() + " ) AND FK_Flow='" + this.getFlowNo() + "'";
				break;
			case   AnyNode: //任意节点
				sql = "SELECT NodeID,Name FROM WF_Node WHERE FK_Flow='" + this.getFlowNo() + "' ORDER BY STEP";
				break;
			case   JumpSpecifiedNodes: //指定节点
				sql = node.getJumpToNodes();
				sql = sql.replace("@WebUser.No", WebUser.getNo());
				sql = sql.replace("@WebUser.Name", WebUser.getName());
				sql = sql.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
				sql = sql.replace("@WebUser.DeptNo", WebUser.getDeptNo());
				if (sql.contains("@"))
				{
					Work wk = node.getHisWork();
					wk.setOID(this.getWorkID());
					wk.RetrieveFromDBSources();
					for (Attr attr : wk.getEnMap().getAttrs())
					{
						if (sql.contains("@") == false)
						{
							break;
						}
						sql = sql.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
					}
				}
				break;
			default:
				throw new RuntimeException(node.getJumpWay() + "还未增加改类型的判断.");
		}
		sql = sql.replace("~", "'");
		if (DataType.IsNullOrEmpty(sql) == false)
		{
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase || SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
			{
				for (DataColumn col : dt.Columns)
				{
					String colName = col.ColumnName.toLowerCase();
					switch (colName)
					{
						case "nodeid":
							col.ColumnName = "NodeID";
							break;
						case "name":
							col.ColumnName = "Name";
							break;
						default:
							break;
					}
				}
			}
			return bp.tools.Json.ToJson(dt);
		}
		return "";
	}


	public final String JumpWay_Send()
	{
		try
		{
			int toNodeID = this.GetRequestValInt("ToNode");

			SendReturnObjs objs = Dev2Interface.Node_SendWork(this.getFlowNo(), this.getWorkID(), toNodeID, null);
			String strs = objs.ToMsgOfHtml();
			strs = strs.replace("@", "<br>@");


			///#region 处理发送后转向.
			//当前节点.
			Node currNode = new Node(this.getNodeID());
			/*处理转向问题.*/
			switch (currNode.getHisTurnToDeal())
			{
				case SpecUrl:
					String myurl = currNode.getTurnToDealDoc();
					if (myurl.contains("?") == false)
					{
						myurl += "?1=1";
					}
					Attrs myattrs = currNode.getHisWork().getEnMap().getAttrs();
					Work hisWK = currNode.getHisWork();
					for (Attr attr : myattrs)
					{
						if (myurl.contains("@") == false)
						{
							break;
						}
						myurl = myurl.replace("@" + attr.getKey(), hisWK.GetValStrByKey(attr.getKey()));
					}
					myurl = myurl.replace("@WebUser.No", WebUser.getNo());
					myurl = myurl.replace("@WebUser.Name", WebUser.getName());
					myurl = myurl.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
					myurl = myurl.replace("@WebUser.DeptNo", WebUser.getDeptNo());
					if (myurl.contains("@"))
					{
						Dev2Interface.Port_SendMsg("admin", currNode.getName() + "在" + currNode.getName() + "节点处，出现错误", "流程设计错误，在节点转向url中参数没有被替换下来。Url:" + myurl, "Err" + currNode.getNo() + "_" + this.getWorkID(), SMSMsgType.Err, this.getFlowNo(), this.getNodeID(), this.getWorkID(), this.getFID());
						throw new RuntimeException("流程设计错误，在节点转向url中参数没有被替换下来。Url:" + myurl);
					}

					if (myurl.contains("PWorkID") == false)
					{
						myurl += "&PWorkID=" + this.getWorkID();
					}

					myurl += "&FromFlow=" + this.getFlowNo() + "&FromNode=" + this.getNodeID() + "&UserNo=" + WebUser.getNo() + "&Token=" + WebUser.getToken();
					return "TurnUrl@" + myurl;
				case TurnToByCond:

					return strs;
				default:
					strs = strs.replace("@WebUser.No", WebUser.getNo());
					strs = strs.replace("@WebUser.Name", WebUser.getName());
					strs = strs.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
					strs = strs.replace("@WebUser.DeptNo", WebUser.getDeptNo());
					return strs;
			}
		}
		catch (Exception ex)
		{

			if (ex.getMessage().indexOf("url@") != -1)
			{
				return ex.getMessage().replace("/WorkOpt/", "/");
			}
			return "err@" + ex.getMessage();
		}
	}
	/**
	 发送抄送审核处理

	 @return
	 */
	public final String TS_Send() throws Exception {
		//操作类型 发送，抄送（不做处理），发送+审核，发送+抄送+审核
		String operType = this.GetRequestVal("OperType");
		WorkOpt workOpt = new WorkOpt(this.getMyPK());
		if (DataType.IsNullOrEmpty(operType) == true)
		{
			return "err@不可能存在的错误,没有获取到参数:" + operType;
		}

		///#region 执行发送.
		//组合要发送到的人员编号：把部门人员，三者相加进来.
		String emps = workOpt.getSendEmps();
		if(DataType.IsNullOrEmpty(emps)==false && emps.startsWith(","))
			emps = emps.substring(1);
		//判断是否按交集计算.
		if (this.GetRequestValBoolen("IsJiaoJi") == false)
		{
			emps += ","+bp.port.Glo.GenerEmpNosByDeptNos(workOpt.getSendDepts());
			emps += bp.port.Glo.GenerEmpNosByStationNos(workOpt.getSendStas());
		}
		else
		{
			if(!DataType.IsNullOrEmpty(workOpt.getSendDepts()) && !DataType.IsNullOrEmpty(workOpt.getSendStas())){
				String sql = "SELECT FK_Emp FROM Port_DeptEmpStation WHERE FK_Dept IN (" + bp.port.Glo.GenerWhereInSQL(workOpt.getSendDepts()) + ") AND FK_Station  IN (" + bp.port.Glo.GenerWhereInSQL(workOpt.getSendStas()) + ") ";
				DataTable dt = DBAccess.RunSQLReturnTable(sql);
				String strs = "";
				for (DataRow dr : dt.Rows)
					strs += dr.getValue(0) + ",";
				emps += "," + strs;
			}

		}

		emps += bp.port.Glo.GenerEmpNosByDeptLeadersNos(workOpt.GetValStringByKey("SendDeptLeaders")); //获得部门领导.

		if (DataType.IsNullOrEmpty(emps) == true)
		{
			return "err@接受人不能为空.";
		}

		//执行发送:
		String sendStr = this.Accepter_SendExt(workOpt.getWorkID(), workOpt.getToNodeID(), emps, "");

		//写入小纸条.
		if (DataType.IsNullOrEmpty(workOpt.getSendNote()) == false)
		{
			GenerWorkFlow gwf = new GenerWorkFlow(workOpt.getWorkID());
			gwf.SetPara("ScripNodeID", workOpt.getNodeID());
			gwf.SetPara("ScripMsg", workOpt.getSendNote());
			gwf.Update();
		}

		///#endregion 执行发送.


		///#region 执行抄送.
		/*//组合要发送到的人员编号：把部门人员，三者相加进来.
		emps = workOpt.getCCEmps();
		emps += bp.port.Glo.GenerEmpNosByDeptNos(workOpt.getCCDepts());
		emps += bp.port.Glo.GenerEmpNosByStationNos(workOpt.getCCStations());
		String ccMsg = Dev2Interface.Node_CCToEmps(workOpt.getNodeID(), workOpt.getWorkID(), emps, "来自" + WebUser.getName() + "的抄送", workOpt.getCCNote());
		*/
		///#endregion 执行抄送.

		return sendStr;
	}

	public final String TS_CC() throws Exception {
		WorkOpt workOpt = new WorkOpt();
		workOpt.setMyPK(WebUser.getNo()+"_"+this.getNodeID()+"_"+this.getWorkID());
		if(workOpt.RetrieveFromDBSources()==1){
			String emps = workOpt.getCCEmps();
			emps +=","+ bp.port.Glo.GenerEmpNosByDeptNos(workOpt.getCCDepts());
			emps +=","+ bp.port.Glo.GenerEmpNosByStationNos(workOpt.getCCStations());
			return Dev2Interface.Node_CCToEmps(workOpt.getNodeID(), workOpt.getWorkID(), emps, "来自" + WebUser.getName() + "的抄送", workOpt.getCCNote());
		}
		return "err@没有配置接收抄送人";
	}


	private void SaveWorkCheck(WorkOpt workOpt)
	{
		//暂时未处理
	}
	private void SendWork(WorkOpt workOpt)
	{
		//执行发送.

	}

}
