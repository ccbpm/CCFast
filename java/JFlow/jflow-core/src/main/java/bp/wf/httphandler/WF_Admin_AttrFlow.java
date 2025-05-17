package bp.wf.httphandler;

import bp.difference.handler.CommonFileUtils;
import bp.en.FieldTypeS;
import bp.en.UIContralType;
import bp.port.Emp;
import bp.port.EmpAttr;
import bp.sys.*;
import bp.da.*;
import bp.tools.DateUtils;
import bp.web.WebUser;
import bp.wf.Glo;
import bp.wf.template.*;
import bp.difference.*;
import bp.wf.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.time.*;

import static bp.difference.handler.CommonFileUtils.getMultipartHttpServletRequest;

public class WF_Admin_AttrFlow extends bp.difference.handler.DirectoryPageBase
{
	/**
	 构造函数
	*/
	public WF_Admin_AttrFlow()
	{
	}


		///#region 修改轨迹.
	public final String EditTrackDtl_Init() throws Exception {
		Track tk = new Track(this.getFlowNo(), this.getMyPK());
		return tk.getMsg();
	}
	public final String EditTrackDtl_Save()
	{
		String msg = this.GetRequestVal("Msg");
		String tackTable = "ND" + Integer.parseInt(this.getFlowNo()) + "Track";
		String sql = "UPDATE " + tackTable + " SET Msg='" + msg + "' WHERE MyPK='" + this.getMyPK() + "'";
		DBAccess.RunSQL(sql);
		return "修改成功";
	}
	public final String EditTrackDtl_Delete()
	{
		String tackTable = "ND" + Integer.parseInt(this.getFlowNo()) + "Track";
		String sql = "DELETE FROM  " + tackTable + " WHERE MyPK='" + this.getMyPK() + "'";
		DBAccess.RunSQL(sql);
		return "删除成功.";
	}

		///#endregion



		///#region APICodeFEE_Init.
	/**
	 代码生成器.

	 @return
	*/
	public final String APICodeFEE_Init() throws Exception {
		if (this.getFlowNo() == null || this.getFlowNo().isEmpty())
		{
			return "err@FK_Flow参数不能为空！";
		}

		Flow flow = new Flow(this.getFlowNo());

		String tmpPath = "";

		if (Glo.getPlatform() == Platform.CCFlow)
		{
			tmpPath = SystemConfig.getPathOfWebApp() + "WF/Admin/AttrFlow/APICodeFEE.txt.CCFlow";
		}
		else
		{
			tmpPath = SystemConfig.getPathOfWebApp() + "WF/Admin/AttrFlow/APICodeFEE.txt.JFlow";
		}

		if ((new File(tmpPath)).isFile() == false)
		{
			return String.format("未找到事件编写模板文件“%1$s”，请联系管理员！", tmpPath);
		}

		String Title = flow.getName() + "[" + flow.getNo() + "]";
		String code = DataType.ReadTextFile(tmpPath); //, System.Text.Encoding.UTF8).replace("F001Templepte", string.Format("FEE{0}", flow.getNo())).replace("@FlowName", flow.Name).replace("@FlowNo", flow.getNo());
		code = code.replace("F001Templepte", String.format("FEE%1$s", flow.getNo())).replace("@FlowName", flow.getName()).replace("@FlowNo", flow.getNo());


		//此处将重要行标示出来，根据下面的数组中的项来检索重要行号
		String[] lineStrings = new String[] {"namespace BP.FlowEvent", ": BP.WF.FlowEventBase", "public override String FlowMark", "public override String SendWhen()", "public override String SendSuccess()", "public override String SendError()", "public override String FlowOnCreateWorkID()", "public override String FlowOverBefore()", "public override String FlowOverAfter()", "public override String BeforeFlowDel()", "public override String AfterFlowDel()", "public override String SaveAfter()", "public override String SaveBefore()", "public override String UndoneBefore()", "public override String UndoneAfter()", "public override String ReturnBefore()", "public override String ReturnAfter()", "public override String AskerAfter()", "public override String AskerReAfter()"};



		String msg = "<script type=\"text/javascript\">SyntaxHighlighter.highlight();</script>";
		msg += String.format("<pre type=\"syntaxhighlighter\" class=\"brush: csharp; html-script: false; highlight: [%3$s]\" title=\"%1$s[编号：%2$s] 流程自定义事件代码生成\">", flow.getName(), flow.getNo(), APICodeFEE_Init_GetImportantLinesNumbers(lineStrings, code));
		msg += code.replace("<", "&lt;"); //SyntaxHighlighter中，使用<Pre>包含的代码要将左尖括号改成其转义形式
		msg += "</pre>";

		return msg;
	}
	/**
	 获取重要行的标号连接字符串，如3,6,8

	 @param lineInStrings 重要行中包含的字符串数组，只要行中包含其中的一项字符串，则这行就是重要行
	 @param str 要检索的字符串，使用Environment.NewLine分行
	 @return
	*/
	private String APICodeFEE_Init_GetImportantLinesNumbers(String[] lineInStrings, String str)
	{
		String[] lines = str.replace(System.lineSeparator(), "`").split("[`]", -1);
		String nums = "";

		for (int i = 0; i < lines.length; i++)
		{
			for (String instr : lineInStrings)
			{
				if (lines[i].indexOf(instr) != -1)
				{
					nums += (i + 1) + ",";
					break;
				}
			}
		}

		return StringHelper.trimEnd(nums, ',');
	}

		///#endregion APICodeFEE_Init.


		///#region 节点属性（列表）的操作
	/**
	 初始化节点属性列表.

	 @return
	*/
	public final String NodeAttrs_Init() throws Exception {
		String strFlowId = GetRequestVal("FK_Flow");
		if (DataType.IsNullOrEmpty(strFlowId))
		{
			return "err@参数错误！";
		}

		Nodes nodes = new Nodes();
		nodes.Retrieve("FK_Flow", strFlowId, null);
		//因直接使用nodes.ToJson()无法获取某些字段（e.g.HisFormTypeText,原因：Node没有自己的Attr类）
		//故此处手动创建前台所需的DataTable
		DataTable dt = new DataTable();
		dt.Columns.Add("NodeID"); //节点ID
		dt.Columns.Add("Name"); //节点名称
		dt.Columns.Add("HisFormType"); //表单方案
		dt.Columns.Add("HisFormTypeText");
		dt.Columns.Add("HisRunModel"); //节点类型
		dt.Columns.Add("HisRunModelT");

		dt.Columns.Add("HisDeliveryWay"); //接收方类型
		dt.Columns.Add("HisDeliveryWayText");
		dt.Columns.Add("HisDeliveryWayJsFnPara");
		dt.Columns.Add("HisDeliveryWayCountLabel");
		dt.Columns.Add("HisDeliveryWayCount"); //接收方Count

		dt.Columns.Add("HisCCRole"); //抄送人
		dt.Columns.Add("HisCCRoleText");
		dt.Columns.Add("HisFrmEventsCount"); //消息&事件Count
		dt.Columns.Add("HisFinishCondsCount"); //流程完成条件Count
		DataRow dr;
		for (Node node : nodes.ToJavaList())
		{
			dr = dt.NewRow();
			dr.setValue("NodeID", node.getNodeID());
			dr.setValue("Name", node.getName());
			dr.setValue("HisFormType", node.getHisFormType());
			dr.setValue("HisFormTypeText", node.getHisFormTypeText());
			dr.setValue("HisRunModel", node.getHisRunModel());
			dr.setValue("HisRunModelT", node.getHisRunModelT());
			dr.setValue("HisDeliveryWay", node.getHisDeliveryWay());
			dr.setValue("HisDeliveryWayText", node.getHisDeliveryWayText());

			//接收方数量
			int intHisDeliveryWayCount = 0;
			if (node.getHisDeliveryWay() == DeliveryWay.ByStation)
			{
				dr.setValue("HisDeliveryWayJsFnPara", "ByStation");
				dr.setValue("HisDeliveryWayCountLabel", "角色");
				NodeStations nss = new NodeStations();
				intHisDeliveryWayCount = nss.Retrieve(NodeStationAttr.FK_Node, node.getNodeID(), null);
			}
			else if (node.getHisDeliveryWay() == DeliveryWay.ByDept)
			{
				dr.setValue("HisDeliveryWayJsFnPara", "ByDept");
				dr.setValue("HisDeliveryWayCountLabel", "部门");
				NodeDepts nss = new NodeDepts();
				intHisDeliveryWayCount = nss.Retrieve(NodeDeptAttr.FK_Node, node.getNodeID(), null);
			}
			else if (node.getHisDeliveryWay() == DeliveryWay.ByBindEmp)
			{
				dr.setValue("HisDeliveryWayJsFnPara", "ByDept");
				dr.setValue("HisDeliveryWayCountLabel", "人员");
				NodeEmps nes = new NodeEmps();
				intHisDeliveryWayCount = nes.Retrieve(NodeStationAttr.FK_Node,  node.getNodeID(), null);
			}
			dr.setValue("HisDeliveryWayCount", intHisDeliveryWayCount);

			//抄送
			dr.setValue("HisCCRole", node.getHisCCRole());
			dr.setValue("HisCCRoleText", node.getHisCCRoleText());

			//消息&事件Count
			FrmEvents fes = new FrmEvents();
			dr.setValue("HisFrmEventsCount", fes.Retrieve(FrmEventAttr.FrmID, "ND" + node.getNodeID(), null));

			//流程完成条件Count
			Conds conds = new Conds(CondType.Flow, node.getNodeID());
			dr.setValue("HisFinishCondsCount", conds.size());

			dt.Rows.add(dr);
		}
		return bp.tools.Json.ToJson(dt);
	}

		///#endregion


		///#region 与业务表数据同步
	public final String DTSBTable_Init() throws Exception {
		DataSet ds = new DataSet();

		// 把流程信息放入.
		Flow fl = new Flow(this.getFlowNo());
		DataTable dtFlow = fl.ToDataTableField("Flow");
		ds.Tables.add(dtFlow);

		//获得数据源的表.
		SFDBSrc src = new SFDBSrc(fl.getDTSDBSrc());
		DataTable dt = src.GetTables(false);

		if (src.getFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("NO").ColumnName = "No";
			dt.Columns.get("NAME").ColumnName = "Name";
		}
		if (src.getFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("no").ColumnName = "No";
			dt.Columns.get("name").ColumnName = "Name";
		}

		dt.TableName = "Tables";
		ds.Tables.add(dt);


		//把节点信息放入.
		Nodes nds = new Nodes(this.getFlowNo());
		DataTable dtNode = nds.ToDataTableField("Nodes");
		ds.Tables.add(dtNode);



		return bp.tools.Json.ToJson(ds);
	}

	/**
	 与业务表数据同步

	 @return
	*/
	public final String DTSBTable_Save() throws Exception {
		//获取流程属性
		Flow flow = new Flow(this.getFlowNo());
		//获取主键方式
		DataDTSWay dtsWay = DataDTSWay.forValue(this.GetRequestValInt("RB_DTSWay"));

		FlowDTSTime dtsTime = FlowDTSTime.forValue(this.GetRequestValInt("RB_DTSTime"));

		flow.setDTSWay(dtsWay);
		flow.setDTSTime(dtsTime);

		if (flow.getDTSWay() == DataDTSWay.None)
		{
			flow.Update();
			return "保存成功.";
		}

		//保存配置信息
		flow.setDTSDBSrc(this.GetRequestVal("DDL_DBSrc"));
		flow.setDTSBTable( this.GetRequestVal("DDL_Table"));
		flow.setDTSSpecNodes(StringHelper.trimEnd(this.GetRequestVal("CheckBoxIDs"), ','));

		flow.DirectUpdate();
		return "保存成功";
	}

		///#endregion


		///#region 数据同步数据源变化时，关联表的列表发生变化
	public final String DTSBTable_DBSrcChange() throws Exception {
		String dbsrc = this.GetRequestVal("DDL_DBSrc");
		//绑定表.
		SFDBSrc src = new SFDBSrc(dbsrc);
		DataTable dt = src.GetTables(false);
		if (src.getFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("NO").ColumnName = "No";
			dt.Columns.get("NAME").ColumnName = "Name";
		}
		if (src.getFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("no").ColumnName = "No";
			dt.Columns.get("name").ColumnName = "Name";
		}
		return bp.tools.Json.ToJson(dt);
	}

		///#endregion


		///#region 数据调度 - 字段映射.
	public final String DTSBTableExt_Init() throws Exception {
		//定义数据容器.
		DataSet ds = new DataSet();

		//获得数据表列.
		SFDBSrc src = new SFDBSrc(this.GetRequestVal("FK_DBSrc"));

		DataTable dtColms = src.GetColumns(this.GetRequestVal("TableName"));
		dtColms.TableName = "Cols";
		if (src.getFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dtColms.Columns.get("NO").ColumnName = "No";
			dtColms.Columns.get("NAME").ColumnName = "Name";
		}
		if (src.getFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dtColms.Columns.get("no").ColumnName = "No";
			dtColms.Columns.get("name").ColumnName = "Name";
		}

		ds.Tables.add(dtColms); //列名.

		//属性列表.
		MapAttrs attrs = new MapAttrs("ND" + Integer.parseInt(this.getFlowNo()) + "Rpt");
		DataTable dtAttrs = attrs.ToDataTableStringField("Sys_MapAttr");
		ds.Tables.add(dtAttrs);

		//加入流程配置信息
		Flow flow = new Flow(this.getFlowNo());
		DataTable dtFlow = flow.ToDataTableField("Flow");
		ds.Tables.add(dtFlow);

		//转化成json,返回.
		return bp.tools.Json.ToJson(ds);
	}
	public final String DTSBTableExt_Save() throws Exception {
		String rpt = "ND" + Integer.parseInt(this.getFlowNo()) + "Rpt";
		Flow fl = new Flow(this.getFlowNo());
		MapAttrs mattrs = new MapAttrs(rpt);

		String pk = this.GetRequestVal("DDL_OID");
		if (DataType.IsNullOrEmpty(pk) == true)
		{
			return "err@必须设置业务表的主键，否则无法同步。";
		}


		String lcStr = ""; //要同步的流程字段
		String ywStr = ""; //第三方字段
		String err = "";
		for (MapAttr attr : mattrs.ToJavaList())
		{
			int val = this.GetRequestValChecked("CB_" + attr.getKeyOfEn());
			if (val == 0)
			{
				continue;
			}

			String refField = this.GetRequestVal("DDL_" + attr.getKeyOfEn());

			//如果选中的业务字段重复，抛出异常
			if (ywStr.contains("@" + refField + "@"))
			{
				err += "@配置【" + attr.getKeyOfEn() + " - " + attr.getName() +"】错误, 请确保选中业务字段的唯一性，该业务字段已经被其他字段所使用。";
			}
			lcStr += "" + attr.getKeyOfEn() + "=" + refField + "@";
			ywStr += "@" + refField + "@,";
		}

		//    bp.web.Controls.RadioBtn rb = this.Pub1.GetRadioBtnByID("rb_workId");

		int pkModel = this.GetRequestValInt("PKModel");

		String ddl_key = this.GetRequestVal("DDL_OID");
		if (pkModel == 0)
		{
			if (ywStr.contains("@" + ddl_key + "@"))
			{
				err += "@请确保选中业务字段的唯一性，该业务字段【" + ddl_key + "】已经被其他字段所使用。";
			}
			lcStr = "OID=" + ddl_key + "@" + lcStr;
			ywStr = "@" + ddl_key + "@," + ywStr;
		}
		else
		{
			if (ywStr.contains("@" + ddl_key + "@"))
			{
				err += "@请确保选中业务字段的唯一性，该业务字段【" + ddl_key + "】已经被其他字段所使用。";
			}
			lcStr = "GUID=" + ddl_key + "@" + lcStr;
			ywStr = "@" + ddl_key + "@," + ywStr;
		}

		if (!Objects.equals(err, ""))
		{
			return "err@" + err;
		}

		//lcStr = lcStr.replace("@", "");
		ywStr = ywStr.replace("@", "");


		//去除最后一个字符的操作
		if (DataType.IsNullOrEmpty(lcStr) || DataType.IsNullOrEmpty(ywStr))
		{
			return "err@要配置的内容为空...";
		}
		lcStr = lcStr.substring(0, lcStr.length() - 1);
		ywStr = ywStr.substring(0, ywStr.length() - 1);


		//数据存储格式   a,b,c@a_1,b_1,c_1
		fl.setDTSFields(lcStr);
		fl.setDTSBTablePK(pk);
		fl.DirectUpdate();

		return "设置成功.";
	}

		///#endregion


		///#region 前置导航save
	/**
	 前置导航save

	 @return
	*/
	public final String StartGuide_Save()
	{
		try
		{
			//Flow en = new Flow();
			//en.setNo(this.FlowNo;
			//en.Retrieve();

			//int val = this.GetRequestValInt("RB_StartGuideWay");

			//en.SetValByKey(BP.WF.Template.FlowAttr.StartGuideWay, val);

			//if (en.StartGuideWay == StartGuideWay.None)
			//{
			//    en.StartGuideWay = BP.WF.Template.StartGuideWay.None;
			//}

			//if (en.StartGuideWay == StartGuideWay.ByHistoryUrl)
			//{
			//    en.StartGuidePara1 = this.GetRequestVal("TB_ByHistoryUrl");
			//    en.StartGuidePara2 = "";
			//    en.StartGuideWay = BP.WF.Template.StartGuideWay.ByHistoryUrl;
			//}

			//if (en.StartGuideWay == StartGuideWay.BySelfUrl)
			//{
			//    en.StartGuidePara1 = this.GetRequestVal("TB_SelfURL");
			//    en.StartGuidePara2 = "";
			//    en.StartGuideWay = BP.WF.Template.StartGuideWay.BySelfUrl;
			//}

			////单条模式.
			//if (en.StartGuideWay == StartGuideWay.BySQLOne)
			//{
			//    en.StartGuidePara1 = this.GetRequestVal("TB_BySQLOne1");  //查询语句.
			//    en.StartGuidePara2 = this.GetRequestVal("TB_BySQLOne2");  //列表语句.

			//    //@李国文.
			//    en.StartGuidePara3 = this.GetRequestVal("TB_BySQLOne3");  //单行赋值语句.
			//    en.StartGuideWay = BP.WF.Template.StartGuideWay.BySQLOne;
			//}
			////多条模式
			//if (en.StartGuideWay == StartGuideWay.BySQLMulti)
			//{
			//    en.StartGuidePara1 = this.GetRequestVal("TB_BySQLMulti1");  //查询语句.
			//    en.StartGuidePara2 = this.GetRequestVal("TB_BySQLMulti2");  //列表语句.
			//    en.StartGuideWay = BP.WF.Template.StartGuideWay.BySQLMulti;
			//}
			////多条-子父流程-合卷审批.
			//if (en.StartGuideWay == StartGuideWay.SubFlowGuide)
			//{
			//    en.StartGuidePara1 = this.GetRequestVal("TB_SubFlow1");  //查询语句.
			//    en.StartGuidePara2 = this.GetRequestVal("TB_SubFlow2");  //列表语句.
			//    en.StartGuideWay = BP.WF.Template.StartGuideWay.SubFlowGuide;
			//}

			//BP.WF.Template.FrmNodes fns = new BP.WF.Template.FrmNodes(int.Parse(this.FlowNo + "01"));
			//if (fns.size()>= 2)
			//{
			//    if (en.StartGuideWay == StartGuideWay.ByFrms)
			//        en.StartGuideWay = BP.WF.Template.StartGuideWay.ByFrms;
			//}

			////右侧的超链接.
			//en.StartGuideLink = this.GetRequestVal("TB_GuideLink");
			//en.StartGuideLab = this.GetRequestVal("TB_GuideLab");

			// en.Update();
			return "保存成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}

		///#endregion


		///#region 流程轨迹查看权限
	/**
	 流程轨迹查看权限

	 @return
	*/
	public final String TruckViewPower_Save()
	{
		try
		{
			TruckViewPower en = new TruckViewPower(this.getFlowNo());
			en.Retrieve();

			Object tempVar = bp.pub.PubClass.CopyFromRequestByPost(en);
			en = tempVar instanceof TruckViewPower ? (TruckViewPower)tempVar : null;
			en.Save(); //执行保存.
			return "保存成功";
		}
		catch (java.lang.Exception e)
		{
			return "err@保存失败";
		}

	}

		///#endregion 流程轨迹查看权限save


		///#region 数据导入.
	/**
	 导入bpmn2.0

	 @return
	*/
	public final String Imp_DoneBPMN() throws Exception {

		File xmlFile = null;
		String fileName = UUID.randomUUID().toString();
		try {
			xmlFile = File.createTempFile(fileName, ".xml");
		} catch (IOException e1) {
			xmlFile = new File(System.getProperty("java.io.tmpdir"), fileName + ".xml");
		}
		xmlFile.deleteOnExit();
		HttpServletRequest request = ContextHolderUtils.getRequest();
		try{
			CommonFileUtils.upload(request,"File_UploadBPMN", xmlFile);
		}catch(Exception e){
			e.printStackTrace();
			return "err@执行失败";
		}
		String filePath = xmlFile.getAbsolutePath();

		String flowNo = this.getFlowNo();
		String FK_FlowSort = this.GetRequestVal("FK_Sort");

		//检查流程编号
		if (DataType.IsNullOrEmpty(flowNo) == false)
		{
			Flow fl = new Flow(flowNo);
			FK_FlowSort = fl.getFlowSortNo();
		}

		//检查流程类别编号
		if (DataType.IsNullOrEmpty(FK_FlowSort) == true)
		{
			if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
			{
				FK_FlowSort = bp.web.WebUser.getOrgNo();
			}
			else
			{
				return "err@所选流程类别编号不存在。";
			}
		}

		//执行导入
		Flow flow = TemplateGlo.NewFlowByBPMN(FK_FlowSort, filePath);
		flow.setPTable("ND"+Integer.parseInt(flow.getNo())+"Rpt");
		flow.Update();
		flow.DoCheck(); //要执行一次检查.

		Hashtable ht = new Hashtable();
		ht.put("FK_Flow", flow.getNo());
		ht.put("FlowName", flow.getName());
		ht.put("FK_FlowSort", flow.getFlowSortNo());
		ht.put("Msg", "导入成功,流程编号为:" + flow.getNo() + "名称为:" + flow.getName());
		return bp.tools.Json.ToJson(ht);
	}
	public String DingDing_ImpAths() throws Exception
	{
		try {
			String FlowNo = this.GetRequestVal("FlowNo");
			String athName = this.GetRequestVal("AthName");
			bp.da.Log.DefaultLogWriteLine(LogType.Info, "FlowNo:" + FlowNo);
			bp.da.Log.DefaultLogWriteLine(LogType.Info, "AthName:" + athName);
			HttpServletRequest request = getRequest();
			MultipartHttpServletRequest mrequest = getMultipartHttpServletRequest(request);
			List<MultipartFile> multipartFiles = mrequest.getFiles("file");
			if (multipartFiles.size() <= 0)
				return "";
			else {
				for (MultipartFile file : multipartFiles) {
					String fileName = file.getOriginalFilename();
					bp.da.Log.DefaultLogWriteLine(LogType.Info, "fileName:" + fileName);
					fileName=fileName.split("/")[1];
					String[] strs = fileName.split("_");
					String BillNo = strs[0];
					bp.da.Log.DefaultLogWriteLine(LogType.Info, "BillNo:" + BillNo);
					GenerWorkFlow gwf = new GenerWorkFlow();
					gwf.Retrieve(GenerWorkFlowAttr.BillNo, BillNo);
					if(gwf.getWorkID()==0)
						continue;

					FrmAttachments aths = new FrmAttachments();
					aths.Retrieve(FrmAttachmentAttr.FK_MapData, "ND" + Integer.parseInt(FlowNo) + "01");
					if (aths.size() > 0) {
						for (FrmAttachment ath : aths.ToJavaList()) {
							if (!ath.getName().equals(athName))
								continue;

							String myfilePath = ath.getSaveTo() + gwf.getWorkID() + "/";
							myfilePath = myfilePath.replace("\\\\", "/");
							if ((new File(myfilePath)).isDirectory() == false)
							{
								(new File(myfilePath)).mkdirs();
							}

							file.transferTo(new File(myfilePath + "/" + fileName));

							FrmAttachmentDB dbUpload = new FrmAttachmentDB();
							String guid = DBAccess.GenerGUID();
							dbUpload.setMyPK(guid); // athDesc.getFrmID() + oid.ToString();
							dbUpload.setNodeID(Integer.parseInt(gwf.getFlowNo() + "01"));
							//dbUpload.setSort(sort);
							dbUpload.setFrmID(ath.getFrmID());
							dbUpload.setFKFrmAttachment(ath.getMyPK());

							dbUpload.setFileExts(fileName.substring(fileName.lastIndexOf(".") + 1));
							dbUpload.setFID(gwf.getFID());
							dbUpload.setFileFullName(myfilePath + "/" + fileName);
							dbUpload.setFileName(fileName);
							dbUpload.setFileSize(file.getSize());
							dbUpload.setRDT(gwf.getRDT());
							dbUpload.setRec(gwf.getStarter());
							dbUpload.setRecName(gwf.getStarterName());
							dbUpload.setDeptNo(gwf.getDeptNo());
							dbUpload.setDeptName(gwf.getDeptName());
							dbUpload.setRefPKVal(String.valueOf(gwf.getWorkID()));

							dbUpload.setUploadGUID(guid);
							dbUpload.Insert();
						}
					}
				}
			}
		}
		catch (Exception ex){
			String msg=ex.getMessage();
			bp.da.Log.DefaultLogWriteLine(LogType.Info, "附件上传错误:" + msg);
		}
		return "";
	}
	public String DingDing_ImpDataTrack(DataTable dt,String flowNo) throws Exception{
		String adminer = WebUser.getNo();
		try {
			String billNos="";
			for (DataRow dr : dt.Rows) {
				String billNo = dr.get("审批编号").toString();
				if(billNos.contains(billNo+"@")){
					continue;
				}
				billNos+=billNo+"@";
				GenerWorkFlow gwf = new GenerWorkFlow();
				gwf.Retrieve(GenerWorkFlowAttr.BillNo, billNo, GenerWorkFlowAttr.FK_Flow, flowNo);
				if(gwf!=null)
				{
					String checkLog = dr.get("审批记录(含处理人UserID)").toString();
					if (DataType.IsNullOrEmpty(checkLog) == true)
						return "err@excel模板错误:您需要将[审批记录(含处理人UserID)] 列设置为文本类型,导入不进来.";

					String[] strs = checkLog.split(";");
					if (DataType.IsNullOrEmpty(strs[0]) == true)
						continue;
					for (String str : strs){
						if (DataType.IsNullOrEmpty(str) == true)
							continue;

						String[] mystrs = str.split("[|]");
						if (str.contains("抄送") == true)
						{
							if (str.contains("已离职")){
								if(mystrs[0].contains("已离职")){
									if(mystrs[0].split(",").length<=0)
										DealEmpID(mystrs[1], mystrs[0]);
								}
							}
							//抄送人
							String pl_UserID = mystrs[1];
							String pl_Label = "抄送";
							String pl_Date = mystrs[3];
							String pl_Oper = mystrs[4];
							String pl_note = "";
							//String pl_note = pl_Oper;
							//抄送人执行登录
							bp.wf.Dev2Interface.Port_Login(pl_UserID);
							String empNames=mystrs[0]+",";
							String[] emps=empNames.split(",");
							for (String emp:emps){
								if(DataType.IsNullOrEmpty(emp))
									continue;
								Emp emp1=new Emp();
								if(emp1.IsExit(EmpAttr.Name,emp.replaceAll("\n", ""))){
									CCList list = new CCList();
									//list.setMyPK(DBAccess.GenerOIDByGUID().ToString(); // workID + "_" + fk_node + "_" + empNo;
									list.setMyPK(gwf.getWorkID() + "_" + gwf.getNodeID() + "_" + emp1.getNo());
									list.setFID(gwf.getFID());
									list.setFlowNo(gwf.getFlowNo());
									list.setFlowName(gwf.getFlowName());
									list.setNodeIDWork(gwf.getNodeID());
									list.setNodeName(gwf.getNodeName());
									list.setTitle(gwf.getTitle());
									list.setDoc("");
									list.setCCTo(emp1.getNo());
									list.setCCToName(emp1.getName());
									list.setInEmpWorks(false); //added by liuxc,2015.7.6
									list.setHisSta(CCSta.Read);
									list.setRDT(pl_Date); //抄送日期.
									list.setRecEmpNo(WebUser.getNo());
									list.setRecEmpName(WebUser.getName());
									list.setWorkID(gwf.getWorkID());
									list.setDomainExt(gwf.getDomainExt());
									list.setOrgNo(gwf.getOrgNo()); //设置组织编号.

									try
									{
										list.Insert();
									}
									catch (java.lang.Exception e)
									{
										// list.CheckPhysicsTable();
										try {
											list.Update();
										} catch (Exception ex) {
											throw new RuntimeException(ex);
										}
									}
									pl_note = "抄送给:"+emp.replaceAll("\n", "");
									if(str.contains("抄送或签"))
										pl_Label="抄送或签";
									else
										pl_Label="抄送";
									String pk_mypk = bp.wf.Dev2Interface.WriteTrackInfo(flowNo, gwf.getNodeID(), pl_Label, gwf.getWorkID(), 0, pl_note, pl_Oper);

									String pl_sql = "@UPDATE ND" + Integer.parseInt(flowNo) + "Track SET RDT='" + pl_Date + "' ,EmpTo='"+emp1.getNo()+"',EmpToT='"+emp1.getName()+"' WHERE MyPK='" + pk_mypk + "'";
									DBAccess.RunSQLs(pl_sql);
								}
								else{
									pl_note = "抄送给:"+emp.replaceAll("\n", "");
									if(str.contains("抄送或签"))
										pl_Label="抄送或签";
									else
										pl_Label="抄送";
									String pk_mypk = bp.wf.Dev2Interface.WriteTrackInfo(flowNo, gwf.getNodeID(), pl_Label, gwf.getWorkID(), 0, pl_note, pl_Oper);
									String pl_sql = "@UPDATE ND" + Integer.parseInt(flowNo) + "Track SET RDT='" + pl_Date + "',EmpToT='"+emp+"' WHERE MyPK='" + pk_mypk + "'";
									DBAccess.RunSQLs(pl_sql);
								}
							}
						}
					}
				}
			}
			bp.wf.Dev2Interface.Port_Login(adminer);
		}
		catch (Exception ex){
			bp.wf.Dev2Interface.Port_Login(adminer);
		}

		return "";
	}
	/**
	 * 导入钉钉历史数据
	 * @return
	 */
	public String DingDing_ImpDataFile()
	{
		String adminer = WebUser.getNo();
		try {
			HttpServletRequest request = getRequest();
			if (CommonFileUtils.getFilesSize(request, "file") == 0) {
				return "err@请选择要导入的数据信息。";
			}

			String fileName = CommonFileUtils.getOriginalFilename(request, "file");
			String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
			if (!prefix.equals("xls") && !prefix.equals("xlsx")) {

				return "err@上传的文件必须是Excel文件.";
			}

			String errInfo = "";
			String ext = ".xls";
			if (fileName.contains(".xlsx")) {
				ext = ".xlsx";
			}


			//设置文件名
			String fileNewName = DateUtils.format(new Date(), "yyyyMMddHHmmss");
			File file = null;
			try {
				file = File.createTempFile(fileNewName, ext);
			} catch (IOException e1) {
				file = new File(System.getProperty("java.io.tmpdir"), fileNewName + ext);
			}
			file.deleteOnExit();
			try {
				CommonFileUtils.upload(request, "file", file);
			} catch (Exception e) {
				e.printStackTrace();
				return "err@执行失败";
			}
			DataTable dt = DBLoad.GetTableByExt(file.getAbsolutePath());
			String flowNo = this.getFlowNo();

			//return DingDing_ImpDataTrack(dt,flowNo);


			int node01 = Integer.parseInt(flowNo + "01");
			Node startNode = new Node(node01);

			MapAttrs attrs=new MapAttrs();
			attrs.Retrieve(MapAttrAttr.FK_MapData,"ND"+Integer.parseInt(flowNo) + "Rpt");

			String msg = "导人数据信息如下:";
			GEEntityOID ndrpt = new GEEntityOID("ND" + Integer.parseInt(flowNo) + "Rpt");
			String billNos="";
			for (DataRow dr: dt.Rows)
			{
				String title = dr.get("标题").toString();
				String billNo = dr.get("审批编号").toString();
				String endRDT=dr.get("完成时间").toString();
				String startRDT=dr.get("发起时间").toString();
				String mapDtl="";

				if(billNos.contains(billNo+"@")){
					GenerWorkFlow gwf=new GenerWorkFlow();
					gwf.Retrieve(GenerWorkFlowAttr.BillNo,billNo);
					if(gwf!=null)
					{
						for (DataColumn dc : dt.Columns) {
							String colName = dc.ColumnName;
							if (colName.contains("Dtl@")) {
								String[] dtlStr = colName.split("@");
								String[] colStr = dtlStr[1].split("[.]");
								//从表FK_MapData
								mapDtl = colStr[0];
								break;
							}
						}
						//执行从表数据载入
						Imp_DingDingDtl(mapDtl,dr,dt.Columns,gwf.getWorkID(),WebUser.getNo(),flowNo);
					}
					continue;
				}
				else
					billNos+=billNo+"@";

				//处理用户信息.
				String starterID = dr.get("发起人UserID").toString();
				String checkLog = dr.get("审批记录(含处理人UserID)").toString();

				if (DataType.IsNullOrEmpty(checkLog) == true)
					return "err@excel模板错误:您需要将[审批记录(含处理人UserID)] 列设置为文本类型,导入不进来.";

				String[] strs = checkLog.split(";");
				if (DataType.IsNullOrEmpty(strs[0]) == true)
					continue;

				//判断申请人是否已经离职
				String[] starterstrs = strs[0].split("[|]");
				if (starterstrs[0].contains("已离职"))
					DealEmpID(starterstrs[1], starterstrs[0]);
				//用发起人登录.
				bp.wf.Dev2Interface.Port_Login(starterID);

				//创建workid.
				long workid = bp.wf.Dev2Interface.Node_CreateBlankWork(flowNo, WebUser.getNo());
				ndrpt.setOID(workid);
				ndrpt.RetrieveFromDBSources();

				for (DataColumn dc : dt.Columns)
				{
					String colName = dc.ColumnName;
					if (DataType.IsNullOrEmpty(colName) == true)
						continue;


					if (colName.contains("Dtl@")==false&(colName.contains("附件") == true||colName.contains("图片") == true))
					{
						FrmAttachments aths=new FrmAttachments();
						aths.Retrieve(FrmAttachmentAttr.FK_MapData,"ND"+node01);
						if(aths.size()>0){
							for (FrmAttachment ath:aths.ToJavaList()){
								try{
									if(!ath.getName().equals(colName))
										continue;
									//获得附件内容.
									String docs = dr.get(colName).toString();
									if (DataType.IsNullOrEmpty(docs) == true)
										continue;

									if (docs.contains("http") == false)
										continue;

									String[] fjs=docs.split(";");
									for (String item:fjs){
										if(DataType.IsNullOrEmpty(item))
											continue;
										//获得附件ID.
										String fjID = ath.getNoOfObj();
										//获得附件的后缀.
										String fileExt = item.substring(item.lastIndexOf('.'));
										String myfilePath = SystemConfig.getPathOfTemp() + DBAccess.GenerGUID() + fileExt;
										try
										{
											bp.da.DataType.HttpDownloadFile(item, myfilePath);
										}
										catch (Exception ex)
										{
											msg += "@获取远程数据错误:[" + item + "],技术信息:" + ex.getMessage();
											continue;
										}
										String frmID = ath.getFrmID();
										File file1=new File(myfilePath);
										bp.wf.Dev2Interface.CCForm_AddAth(Integer.parseInt(flowNo + "01"), flowNo, workid, ath.getMyPK(), frmID, myfilePath, file1.getName());
									}
								}
								catch (Exception ex){}

							}
						}
						continue;
					}

					if(colName.contains("Dtl@")){
						String [] dtlStr=colName.split("@");
						String[] colStr=dtlStr[1].split("[.]");
						//从表FK_MapData
						mapDtl=colStr[0];
						continue;
					}

					if(colName.equals("审批编号")||colName.equals("标题")||colName.equals("审批状态")
					||colName.equals("审批结果")||colName.equals("耗时(时:分:秒)")||colName.equals("历史审批人姓名"))
						continue;
					if(colName.equals("完成时间")) {
						ndrpt.SetValByKey("FlowEnderRDT", dr.get(colName).toString());
						continue;
					}
					if(colName.equals("发起时间")) {
						ndrpt.SetValByKey("RDT", dr.get(colName).toString());
						ndrpt.SetValByKey("FlowStartRDT", dr.get(colName).toString());
						continue;
					}
					for (MapAttr attr:attrs.ToJavaList()){
						if(attr.getKeyOfEn().equals("WorkID")
						||attr.getKeyOfEn().equals("OID")||attr.getKeyOfEn().equals("AtPara")
								||attr.getKeyOfEn().equals("FlowEmps")
								||attr.getKeyOfEn().equals("GUID")||attr.getKeyOfEn().equals("PEmp")
								||attr.getKeyOfEn().equals("PFlowNo")||attr.getKeyOfEn().equals("PNodeID")
								||attr.getKeyOfEn().equals("PrjName")||attr.getKeyOfEn().equals("PrjNo")
								||attr.getKeyOfEn().equals("PWorkID")||attr.getKeyOfEn().equals("Rec")
								||attr.getKeyOfEn().equals("Title")||attr.getKeyOfEn().equals("WFSta"))
						{
							continue;
						}

						if(!colName.equals(attr.getName()))
							continue;

						try
						{
							//枚举
							if((attr.getUIContralType()== UIContralType.DDL
								|| attr.getUIContralType()==UIContralType.RadioBtn
									|| attr.getUIContralType()==UIContralType.CheckBok)
									&&attr.getMyDataType()==DataType.AppInt
									&&attr.getLGType()== FieldTypeS.Enum){
								if(DataType.IsNullOrEmpty(dr.get(colName).toString().replaceAll("\n", "")))
									ndrpt.SetValByKey(attr.getKeyOfEn(), "-1");
								else {
									SysEnums sysEnums = new SysEnums();
									sysEnums.Retrieve(SysEnumAttr.EnumKey, attr.getUIBindKey());
									for (SysEnum sysEnum : sysEnums.ToJavaList()) {
										if (sysEnum.getLab().equals(dr.get(colName).toString()))
											ndrpt.SetValByKey(attr.getKeyOfEn(), sysEnum.getIntKey());
									}
								}
							}//外键
							else if(attr.getUIContralType()== UIContralType.DDL
									&&attr.getMyDataType()==DataType.AppString
									&&attr.getLGType()== FieldTypeS.Normal){
								if(DataType.IsNullOrEmpty(dr.get(colName).toString()))
									ndrpt.SetValByKey(attr.getKeyOfEn(), "-1");
								else{
									SFTable sfTable=new SFTable(attr.getUIBindKey());
									DataTable sdDt=sfTable.GenerHisDataTable();
									for (DataRow row: sdDt.Rows){
										if(row.get("Name").toString().equals(dr.get(colName).toString().replaceAll("\n", ""))) {
											ndrpt.SetValByKey(attr.getKeyOfEn(), row.get("No").toString());
											ndrpt.SetValByKey(attr.getKeyOfEn()+"T", row.get("Name").toString());
										}
									}
								}
							}
							else{
								ndrpt.SetValByKey(attr.getKeyOfEn(), dr.get(colName).toString());
							}
						}
						catch (Exception ex)
						{
							msg=ex.getMessage();
						}
					}
				}
				//发起年月
				SimpleDateFormat sdft = new SimpleDateFormat("yyyy-MM"); // 定义日期格式
				Date s = sdft.parse(startRDT);
				ndrpt.SetValByKey("SDTOfNode",sdft.format(s));
				ndrpt.Update(); //执行更新

				//执行从表数据载入
				Imp_DingDingDtl(mapDtl,dr,dt.Columns,workid,WebUser.getNo(),flowNo);
				// 处理审批记录.

				int idx = -1; //执行步骤.
				String endTime="";
				String endEmp="";
				for (String str : strs)
				{
					if (DataType.IsNullOrEmpty(str) == true)
						continue;

					String[] mystrs = str.split("[|]");

					if (str.contains("提交申请") == true)
					{
						if (str.contains("已离职"))
							DealEmpID(mystrs[1], mystrs[0]);

						String rdt = str.split("[|]")[3]; //流程的发起日期.

						//发起年月
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM"); // 定义日期格式
						Date date = sdf.parse(rdt);

						//用发起人登录.
						bp.wf.Dev2Interface.Port_Login(starterID);

						endEmp=starterID;

						bp.wf.Dev2Interface.WriteTrackInfo(flowNo, node01, startNode.getName(), workid, 0, "提交申请", "发起流程");

						//更新处理日期.
						String sqls = "@UPDATE WF_GenerWorkFlow SET RDT='" + rdt + "',FK_NY='"+sdf.format(date)+"',SDTOfNode='"+endRDT+"' WHERE WorkID=" + workid;
						sqls += "@UPDATE ND" + Integer.parseInt(flowNo) + "Track SET RDT='" + rdt + "' WHERE WorkID=" + workid;
						DBAccess.RunSQLs(sqls);
						continue;
					}
					else if (str.contains("评论") == true
							||str.contains("转交") == true
							||str.contains("退回") == true)
					{
						String pl_UserID = mystrs[1];
						String pl_Label = mystrs[2];
						String pl_Date = mystrs[3];
						String pl_Oper = mystrs[4];
						String pl_note ="";
						try{
							pl_note=pl_Oper + ":" + mystrs[5];
						}catch (Exception ex){}

						if (str.contains("已离职"))
							DealEmpID(mystrs[1], mystrs[0]);


						bp.wf.Dev2Interface.Port_Login(pl_UserID);

						String pk_mypk = bp.wf.Dev2Interface.WriteTrackInfo(flowNo, node01, pl_Label, workid, 0, pl_note, pl_Oper);

						String pl_sql = "@UPDATE ND" + Integer.parseInt(flowNo) + "Track SET RDT='" + pl_Date + "' WHERE MyPK='" + pk_mypk + "'";
						DBAccess.RunSQLs(pl_sql);
					}
					else if (str.contains("抄送") == true)
					{
						GenerWorkFlow gwf=new GenerWorkFlow(workid);
						String pl_UserID = mystrs[1];
						String pl_Label = "抄送";
						String pl_Date = mystrs[3];
						String pl_Oper = mystrs[4];
						String pl_note = "";
						//String pl_note = pl_Oper;
						//抄送人执行登录
						bp.wf.Dev2Interface.Port_Login(pl_UserID);
						String empNames=mystrs[0]+",";
						String[] emps=empNames.split(",");
						for (String emp:emps){
							if(DataType.IsNullOrEmpty(emp))
								continue;
							Emp emp1=new Emp();
							if(emp1.IsExit(EmpAttr.Name,emp.replaceAll("\n", ""))){
								CCList list = new CCList();
								//list.setMyPK(DBAccess.GenerOIDByGUID().ToString(); // workID + "_" + fk_node + "_" + empNo;
								list.setMyPK(gwf.getWorkID() + "_" + gwf.getNodeID() + "_" + emp1.getNo());
								list.setFID(gwf.getFID());
								list.setFlowNo(gwf.getFlowNo());
								list.setFlowName(gwf.getFlowName());
								list.setNodeIDWork(gwf.getNodeID());
								list.setNodeName(gwf.getNodeName());
								list.setTitle(gwf.getTitle());
								list.setDoc("");
								list.setCCTo(emp1.getNo());
								list.setCCToName(emp1.getName());
								list.setInEmpWorks(false); //added by liuxc,2015.7.6
								list.setHisSta(CCSta.Read);
								list.setRDT(pl_Date); //抄送日期.
								list.setRecEmpNo(WebUser.getNo());
								list.setRecEmpName(WebUser.getName());
								list.setWorkID(gwf.getWorkID());
								list.setDomainExt(gwf.getDomainExt());
								list.setOrgNo(gwf.getOrgNo()); //设置组织编号.

								try
								{
									list.Insert();
								}
								catch (java.lang.Exception e)
								{
									// list.CheckPhysicsTable();
									try {
										list.Update();
									} catch (Exception ex) {
										throw new RuntimeException(ex);
									}
								}
								pl_note = "抄送给:"+emp.replaceAll("\n", "");
								if(str.contains("抄送或签"))
									pl_Label="抄送或签";
								else
									pl_Label="抄送";
								String pk_mypk = bp.wf.Dev2Interface.WriteTrackInfo(flowNo, gwf.getNodeID(), pl_Label, gwf.getWorkID(), 0, pl_note, pl_Oper);

								String pl_sql = "@UPDATE ND" + Integer.parseInt(flowNo) + "Track SET RDT='" + pl_Date + "' ,EmpTo='"+emp1.getNo()+"',EmpToT='"+emp1.getName()+"' WHERE MyPK='" + pk_mypk + "'";
								DBAccess.RunSQLs(pl_sql);
							}
							else{
								pl_note = "抄送给:"+emp.replaceAll("\n", "");
								if(str.contains("抄送或签"))
									pl_Label="抄送或签";
								else
									pl_Label="抄送";
								String pk_mypk = bp.wf.Dev2Interface.WriteTrackInfo(flowNo, gwf.getNodeID(), pl_Label, gwf.getWorkID(), 0, pl_note, pl_Oper);
								String pl_sql = "@UPDATE ND" + Integer.parseInt(flowNo) + "Track SET RDT='" + pl_Date + "',EmpToT='"+emp+"' WHERE MyPK='" + pk_mypk + "'";
								DBAccess.RunSQLs(pl_sql);
							}
						}
//						String pk_mypk = bp.wf.Dev2Interface.WriteTrackInfo(flowNo, node01, pl_Label, workid, 0, pl_note, pl_Oper);
//
//						String pl_sql = "@UPDATE ND" + Integer.parseInt(flowNo) + "Track SET RDT='" + pl_Date + "',EmpToT='"+emp+"' WHERE MyPK='" + pk_mypk + "'";
//						DBAccess.RunSQLs(pl_sql);
					}
					else {
						//处理普通发送节点.
						String userId = mystrs[1];
						String nodeName = mystrs[2];
						String dtTime = mystrs[3];
						String note = "";
						if (str.contains("终止流程实例")) {
							dtTime = mystrs[2];
							nodeName = "终止流程实例";
							note = "终止流程实例";
						}else if (str.contains("拒绝")) {
							dtTime = mystrs[3];
							nodeName = "拒绝";
							try{
								note = mystrs[5];
							}
							catch (Exception ex){
								note = "";
							}

						} else
							note = mystrs[4];

						if (str.contains("已离职"))
							DealEmpID(mystrs[1], mystrs[0]);

						//用人员等.
						bp.wf.Dev2Interface.Port_Login(userId);
						endEmp=starterID;

						//记录信息.
						String send_mypk = bp.wf.Dev2Interface.WriteTrackInfo(flowNo, node01, nodeName, workid, 0, note, "信息");
						String sql = "UPDATE ND" + Integer.parseInt(flowNo) + "Track SET RDT='" + dtTime + "' WHERE MyPK='" + send_mypk + "'";
						DBAccess.RunSQLs(sql);
						endTime = dtTime;
					}
				}

				//bp.wf.Nodes nds = new Nodes();
				//nds.Retrieve("FK_Flow", flowNo);
//				for (Node nd : nds.ToJavaList())
//				{
//					DBAccess.RunSQL("UPDATE ND" + Integer.parseInt(flowNo) + "Track SET NDFrom='" + nd.getNodeID() + "',ActionType=1 WHERE WorkID=" + workid + " AND NDFromT='" + nd.getName() + "'");
//					DBAccess.RunSQL("UPDATE ND" + Integer.parseInt(flowNo) + "Track SET NDTo='" + nd.getNodeID() + "',ActionType=1 WHERE WorkID=" + workid + " AND NDToT='" + nd.getName() + "'");
//				}

				//更新发送.
				//DBAccess.RunSQL("UPDATE ND" + Integer.parseInt(flowNo) + "Track SET ActionType=1 WHERE WorkID=" + workid + " AND ActionType=100");

				bp.wf.Dev2Interface.Port_Login(endEmp);

				//设置单据编号..
				bp.wf.Dev2Interface.SetBillNo(flowNo, workid, billNo);

				//设置标题.
				bp.wf.Dev2Interface.Flow_SetFlowTitle(flowNo, workid, title);

				//设置流程结束.
				bp.wf.Dev2Interface.Flow_DoFlowOver(workid, "流程结束.", WFState.CompleteEnd);

				DBAccess.RunSQL("UPDATE ND" + Integer.parseInt(flowNo) + "Track SET NDFromT='流程结束' ,NDToT='流程结束',RDT='"+endTime+"' WHERE WorkID=" + workid + " AND ActionType='8'");
				msg += " @标题：[" + title + "] - 成功导入, WorkID=" + workid;
			}

			bp.wf.Dev2Interface.Port_Login(adminer);
			//return "info@" + msg;
			return "成功";
		}
		catch (Exception ex){
			return ex.getMessage();
		}
	}
	public final String Imp_DingDingDtl(String mapData,DataRow dr,DataColumnCollection cols
			,Long refPK,String Rec,String fk_flow) throws Exception{

		if(DataType.IsNullOrEmpty(mapData))
			return "";
		boolean isHaveDtl=false;
		String fqsj=dr.get("发起时间").toString();
		GEDtl dtl = new GEDtl(mapData);
		dtl.setOID(DBAccess.GenerOID("Dtl"));
		dtl.setRec(Rec);
		dtl.setRDT(fqsj);

		for (DataColumn dc : cols)
		{
			String colName = dc.ColumnName;
			if (DataType.IsNullOrEmpty(colName) == true)
				continue;


			if (colName.contains("Dtl@")==false)
				continue;

			if(colName.contains("Dtl@")){
				String [] dtlStr=colName.split("@");
				String[] colStr=dtlStr[1].split("[.]");
				//从表FK_MapData
				String mapDtl=colStr[0];
				//字段名称
				String attrName=colStr[1];

				if (attrName.contains("附件") == true||attrName.contains("图片") == true)
				{
					FrmAttachments aths=new FrmAttachments();
					aths.Retrieve(FrmAttachmentAttr.FK_MapData,mapDtl);
					if(aths.size()>0){
						for (FrmAttachment ath:aths.ToJavaList()){
							if(!ath.getName().equals(attrName))
								continue;
							//获得附件内容.
							String docs = dr.get(dc.ColumnName).toString();
							if (DataType.IsNullOrEmpty(docs) == true)
								continue;

							if (docs.contains("http") == false)
								continue;

							String[] fjs=docs.split(";");
							for (String item:fjs){
								if(DataType.IsNullOrEmpty(item))
									continue;
								//获得附件ID.
								String fjID = ath.getNoOfObj();
								//获得附件的后缀.
								String fileExt = item.substring(item.lastIndexOf('.'));
								String myfilePath = SystemConfig.getPathOfTemp() + DBAccess.GenerGUID() + fileExt;
								String filePath="";
								try
								{
									filePath=bp.da.DataType.HttpDownloadFile(item, myfilePath);
								}
								catch (Exception ex)
								{
									continue;
								}
								File file=new File(filePath);
								isHaveDtl=true;
								bp.wf.Dev2Interface.CCForm_AddAth(Integer.parseInt(fk_flow + "01"), fk_flow, refPK, ath.getMyPK(), ath.getFrmID(), myfilePath, file.getName());
							}

						}
					}

				}else {
					MapAttrs dtlAttrs = new MapAttrs();
					dtlAttrs.Retrieve(MapAttrAttr.FK_MapData, mapDtl);
					for (MapAttr mapAttr : dtlAttrs.ToJavaList()) {
						if (mapAttr.getKeyOfEn().equals("OID")
								|| mapAttr.getKeyOfEn().equals("RDT")
								|| mapAttr.getKeyOfEn().equals("Rec")
								|| mapAttr.getKeyOfEn().equals("RefPK")) {
							continue;
						}
						if (mapAttr.getName().equals(attrName)) {
							//枚举
							if(mapAttr.getUIContralType()== UIContralType.DDL
									&&mapAttr.getMyDataType()==DataType.AppInt
									&&mapAttr.getLGType()== FieldTypeS.Enum){
								if(DataType.IsNullOrEmpty(dr.get(colName).toString()))
									dtl.SetValByKey(mapAttr.getKeyOfEn(), "-1");
								else {
									SysEnums sysEnums = new SysEnums();
									sysEnums.Retrieve(SysEnumAttr.EnumKey, mapAttr.getUIBindKey());
									for (SysEnum sysEnum : sysEnums.ToJavaList()) {
										if (sysEnum.getLab().equals(dr.get(colName).toString().replaceAll("\n", ""))) {
											dtl.SetValByKey(mapAttr.getKeyOfEn(), sysEnum.getIntKey());

										}
									}
								}
							}//外键
							else if(mapAttr.getUIContralType()== UIContralType.DDL
									&&mapAttr.getMyDataType()==DataType.AppString
									&&mapAttr.getLGType()== FieldTypeS.Normal){
								if(DataType.IsNullOrEmpty(dr.get(colName).toString()))
									dtl.SetValByKey(mapAttr.getKeyOfEn(), "-1");
								else{
									SFTable sfTable=new SFTable(mapAttr.getUIBindKey());
									DataTable sdDt=sfTable.GenerHisDataTable();
									for (DataRow row: sdDt.Rows){
										if(row.get("Name").toString().equals(dr.get(colName).toString().replaceAll("\n", ""))) {
											dtl.SetValByKey(mapAttr.getKeyOfEn(), row.get("No").toString());
											dtl.SetValByKey(mapAttr.getKeyOfEn() + "T", row.get("Name").toString());
										}
									}
								}
							}
							else{
								dtl.SetValByKey(mapAttr.getKeyOfEn(), dr.get(colName).toString());
								isHaveDtl=true;
							}
						}
					}
				}
			}
		}
		try{
			if(isHaveDtl){
				dtl.setRefPK(String.valueOf(refPK));
				dtl.DirectInsert();
			}
		}
		catch (Exception ex){
			String msg=ex.getMessage();
		}
		return "";
	}
	public void DealEmpID(String empID, String empName) throws Exception
	{
		Emp emp = new Emp();
		emp.setNo(empID);
		if (emp.RetrieveFromDBSources() == 0)
		{
			emp.setName(empName.replaceAll("\n", ""));
			emp.setDeptNo(WebUser.getDeptNo());
			bp.wf.Dev2Interface.Port_Login("admin");
			emp.DirectInsert();
		}

	}
	/**
	 流程模版导入.

	 @return
	*/
	public final String Imp_Done() throws Exception {
		File xmlFile = null;
		String fileName = UUID.randomUUID().toString();
		try {
			xmlFile = File.createTempFile(fileName, ".xml");
		} catch (IOException e1) {
			xmlFile = new File(System.getProperty("java.io.tmpdir"), fileName + ".xml");
		}
		xmlFile.deleteOnExit();
		HttpServletRequest request = ContextHolderUtils.getRequest();
		try{
			CommonFileUtils.upload(request,"file", xmlFile);
		}catch(Exception e){
			try{
				CommonFileUtils.upload(request,"File_Upload", xmlFile);
			}catch(Exception ex){
                ex.printStackTrace();
				return "err@执行失败";
			}
		}

		String flowNo = this.getFlowNo();
		String FK_FlowSort = this.GetRequestVal("FK_Sort");

		//检查流程编号
		if (DataType.IsNullOrEmpty(flowNo) == false)
		{
			Flow fl = new Flow(flowNo);
			FK_FlowSort = fl.getFlowSortNo();
		}

		//检查流程类别编号
		if (DataType.IsNullOrEmpty(FK_FlowSort) == true)
		{
			if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
			{
				FK_FlowSort = bp.web.WebUser.getOrgNo();
			}
			else
			{
				return "err@所选流程类别编号不存在。";
			}
		}

		//导入模式
		ImpFlowTempleteModel model = ImpFlowTempleteModel.forValue(this.GetRequestValInt("ImpWay"));
		if (model == ImpFlowTempleteModel.AsSpecFlowNo)
		{
			flowNo = this.GetRequestVal("SpecFlowNo");
		}

		//执行导入
		Flow flow = TemplateGlo.LoadFlowTemplate(FK_FlowSort, xmlFile.getAbsolutePath(), model, flowNo);
		flow.setPTable("ND"+Integer.parseInt(flow.getNo())+"Rpt");
		flow.SetValByKey(FlowAttr.CreateDate,DataType.getCurrentDateTime());
		flow.setVer(DataType.getCurrentDateTime());
		flow.Update();
		flow.DoCheck(); //要执行一次检查.

		Hashtable ht = new Hashtable();
		ht.put("FK_Flow", flow.getNo());
		ht.put("FlowName", flow.getName());
		ht.put("FK_FlowSort", flow.getFlowSortNo());
		ht.put("Msg", "导入成功,流程编号为:" + flow.getNo() + "名称为:" + flow.getName());
		return bp.tools.Json.ToJson(ht);
	}

		///#endregion 数据导入.


		///#region 修改node Icon.
	/**
	 修改节点ICON

	 @return
	*/
	public final String NodesIcon_Init() throws Exception {
		DataSet ds = new DataSet();
		Nodes nds = new Nodes(this.getFlowNo());
		DataTable dt = nds.ToDataTableField("Nodes");
		ds.Tables.add(dt);

		//把文件放入ds.
		String path = SystemConfig.getPathOfWebApp() + "WF/Admin/ClientBin/NodeIcon/";
		String[] strs = bp.tools.BaseFileUtils.getFiles(path);
		DataTable dtIcon = new DataTable();
		dtIcon.Columns.Add("No");
		dtIcon.Columns.Add("Name");
		for (String str : strs)
		{
			String fileName = str.substring(str.lastIndexOf("\\") + 1);
			fileName = fileName.substring(0, fileName.lastIndexOf("."));

			DataRow dr = dtIcon.NewRow();
			dr.setValue(0, fileName);
			dr.setValue(1, fileName);
			dtIcon.Rows.add(dr);
		}

		dtIcon.TableName = "ICONs";
		ds.Tables.add(dtIcon);

		return bp.tools.Json.ToJson(ds);
	}

		///#endregion 修改node Icon.

	/**
	 流程时限消息设置

	 @return
	*/
	public final String PushMsgEntity_Init() throws Exception {
		DataSet ds = new DataSet();

		//流程上的字段
		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, "ND" + Integer.parseInt(this.getFlowNo()) + "rpt", "LGType", 0, "MyDataType", 1, null);
		ds.Tables.add(attrs.ToDataTableField("FrmFields"));

		//节点
		Nodes nds = new Nodes(this.getFlowNo());
		ds.Tables.add(nds.ToDataTableField("Nodes"));

		//mypk
		PushMsg msg = new PushMsg();
		msg.setMyPK(this.getMyPK());
		msg.RetrieveFromDBSources();
		ds.Tables.add(msg.ToDataTableField("PushMsgEntity"));

		return bp.tools.Json.ToJson(ds);
	}

	/**
	 流程时限消息设置

	 @return
	*/
	public final String PushMsg_Save() throws Exception {
		PushMsg msg = new PushMsg();
		msg.setMyPK(this.getMyPK());
		msg.RetrieveFromDBSources();

		msg.setEventNo(this.getEventNo()); //流程时限规则
		msg.setFlowNo(this.getFlowNo());

		Nodes nds = new Nodes(this.getFlowNo());
		int rbSMS = this.GetRequestValInt("RB_SMS");
		if (rbSMS == 1)
			msg.setPushWayNo("TodoEmps");
		if (rbSMS == 2)
			msg.setPushWayNo("Field");
		if (rbSMS == 3)
			msg.setPushWayNo("NodeWorker");
		if (rbSMS == 4)
			msg.setPushWayNo("BySQL");
		if (rbSMS == 5)
			msg.setPushWayNo("SpecEmpNo");
		if (rbSMS == 6)
			msg.setPushWayNo("Starter");
		msg.SetPara("SMSPushWay", rbSMS);

			///#region 其他节点的处理人方式（求选择的节点）
		String nodesOfSMS = "";
		for (Node mynd : nds.ToJavaList())
		{
			for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet())
			{
				if (key.contains("CB_SMS_" + mynd.getNodeID()) && nodesOfSMS.contains(mynd.getNodeID() + "") == false)
				{
					nodesOfSMS += mynd.getNodeID() + ",";
				}


			}
		}
		msg.setSMSNodes(nodesOfSMS);

			///#endregion 其他节点的处理人方式（求选择的节点）

		//发给指定的人员
		msg.setByEmps(ContextHolderUtils.getRequest().getParameter("TB_Emps"));

		//短消息发送设备
		msg.setSMSPushModel(this.GetRequestVal("PushModel"));

		//邮件标题
		msg.setMailTitleReal(ContextHolderUtils.getRequest().getParameter("TB_title"));

		//短信内容模版.
		msg.setSMSDocReal(ContextHolderUtils.getRequest().getParameter("TB_SMS"));

		//保存.
		if (DataType.IsNullOrEmpty(msg.getMyPK()) == true)
		{
			msg.setMyPK(DBAccess.GenerGUID(0, null, null));
			msg.Insert();
		}
		else
		{
			msg.Update();
		}

		return "保存成功..";
	}


		///#region 欢迎页面初始化.
	/**
	 欢迎页面初始化-获得数量.

	 @return
	*/
	public final String GraphicalAnalysis_Init()
	{
		Hashtable ht = new Hashtable();
		String fk_flow = GetRequestVal("FK_Flow");
		//所有的实例数量.
		ht.put("FlowInstaceNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState >1 AND Fk_flow = '" + fk_flow + "'")); //实例数.

		//所有的待办数量.
		ht.put("TodolistNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState=2 AND Fk_flow = '" + fk_flow + "'"));

		//所有的运行中的数量.
		ht.put("RunNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFSta!=1 AND WFState!=3 AND Fk_flow = '" + fk_flow + "'"));

		//退回数.
		ht.put("ReturnNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(WorkID) FROM WF_GenerWorkFlow WHERE WFState=5 AND Fk_flow = '" + fk_flow + "'"));

		//说有逾期的数量.
		if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			ht.put("OverTimeNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) FROM WF_EMPWORKS where STR_TO_DATE(SDT,'%Y-%m-%d %H:%i') < now() AND Fk_flow = '" + fk_flow + "'"));

		}
		else if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			String sql = "SELECT COUNT(*) from (SELECT *  FROM WF_EMPWORKS WHERE  REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}') AND(sysdate - TO_DATE(SDT, 'yyyy-mm-dd hh24:mi:ss')) > 0 AND Fk_flow = '" + fk_flow + "'";

			sql += "UNION SELECT* FROM WF_EMPWORKS WHERE  REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2}$') AND (sysdate - TO_DATE(SDT, 'yyyy-mm-dd')) > 0 AND Fk_flow = '" + fk_flow + "')";

			ht.put("OverTimeNum", DBAccess.RunSQLReturnValInt(sql));
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB)
		{
			ht.put("OverTimeNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) FROM WF_EMPWORKS where to_timestamp(CASE WHEN SDT='无' THEN '' ELSE SDT END, 'yyyy-mm-dd hh24:MI:SS') < NOW() AND Fk_flow = '" + fk_flow + "'"));
		}
		else
		{
			ht.put("OverTimeNum", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) FROM WF_EMPWORKS where convert(varchar(100),SDT,120) < CONVERT(varchar(100), GETDATE(), 120) AND Fk_flow = '" + fk_flow + "'"));
		}

		return bp.tools.Json.ToJson(ht);
	}
	/**
	 获得数量  流程饼图，部门柱状图，月份折线图.

	 @return
	*/
	public final String GraphicalAnalysis_DataSet() throws Exception {
		DataSet ds = new DataSet();
		String fk_flow = GetRequestVal("FK_Flow");

			///#region  实例分析
		//月份分组.
		String sql = "SELECT FK_NY, count(WorkID) as Num FROM WF_GenerWorkFlow WHERE WFState >1 AND Fk_flow = '" + fk_flow + "' GROUP BY FK_NY";
		DataTable FlowsByNY = DBAccess.RunSQLReturnTable(sql);
		FlowsByNY.TableName = "FlowsByNY";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			FlowsByNY.Columns.get(0).ColumnName = "FK_NY";
			FlowsByNY.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(FlowsByNY);

		//部门分组.
		sql = "SELECT DeptName, count(WorkID) as Num FROM WF_GenerWorkFlow WHERE WFState >1 AND Fk_flow = '" + fk_flow + "' GROUP BY DeptName ";
		DataTable FlowsByDept = DBAccess.RunSQLReturnTable(sql);
		FlowsByDept.TableName = "FlowsByDept";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			FlowsByDept.Columns.get(0).ColumnName = "DeptName";
			FlowsByDept.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(FlowsByDept);

			///#endregion 实例分析。



			///#region 待办 分析
		//待办 - 部门分组.
		sql = "SELECT DeptName, count(WorkID) as Num FROM WF_EmpWorks WHERE WFState >1 AND Fk_flow = '" + fk_flow + "' GROUP BY DeptName";
		DataTable TodolistByDept = DBAccess.RunSQLReturnTable(sql);
		TodolistByDept.TableName = "TodolistByDept";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			TodolistByDept.Columns.get(0).ColumnName = "DeptName";
			TodolistByDept.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(TodolistByDept);

		//逾期的 - 人员分组.
		if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			sql = "SELECT  p.name,COUNT (w.WorkID) AS Num from Port_Emp p,WF_EmpWorks w  WHERE p. NO = w.FK_Emp AND WFState >1 and STR_TO_DATE(SDT,'%Y-%m-%d %H:%i') < now() AND Fk_flow = '" + fk_flow + "' GROUP BY p.name,w.FK_Emp";

		}
		else if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			sql = "SELECT  p.name,COUNT (w.WorkID) AS Num from Port_Emp p,WF_EmpWorks w  WHERE p. NO = w.FK_Emp AND WFState >1 and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}') AND(sysdate - TO_DATE(SDT, 'yyyy-mm-dd hh24:mi:ss')) > 0 AND Fk_flow = '" + fk_flow + "' GROUP BY p.name,w.FK_Emp ";
			sql += "UNION SELECT  p.name,COUNT (w.WorkID) AS Num from Port_Emp p,WF_EmpWorks w  WHERE p. NO = w.FK_Emp AND WFState >1 and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2}$') AND (sysdate - TO_DATE(SDT, 'yyyy-mm-dd')) > 0 AND Fk_flow = '" + fk_flow + "' GROUP BY p.name,w.FK_Emp";
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB)
		{
			sql = "SELECT  p.name,COUNT (w.WorkID) AS Num from Port_Emp p,WF_EmpWorks w  WHERE p. NO = w.FK_Emp AND WFState >1 to_timestamp(CASE WHEN SDT='无' THEN '' ELSE SDT END, 'yyyy-mm-dd hh24:MI:SS') < NOW() AND Fk_flow = '" + fk_flow + "' GROUP BY p.name,w.FK_Emp";
		}
		else
		{
			sql = "SELECT  p.name,COUNT (w.WorkID) AS Num from Port_Emp p,WF_EmpWorks w  WHERE p. NO = w.FK_Emp AND WFState >1 and convert(varchar(100),SDT,120) < CONVERT(varchar(100), GETDATE(), 120) AND Fk_flow = '" + fk_flow + "' GROUP BY p.name,w.FK_Emp";
		}
		DataTable OverTimeByEmp = DBAccess.RunSQLReturnTable(sql);
		OverTimeByEmp.TableName = "OverTimeByEmp";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			OverTimeByEmp.Columns.get(0).ColumnName = "Name";
			OverTimeByEmp.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(OverTimeByEmp);
		//逾期的 - 部门分组.
		if (SystemConfig.getAppCenterDBType() == DBType.MySQL|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			sql = "SELECT DeptName, count(WorkID) as Num FROM WF_EmpWorks WHERE WFState >1 and STR_TO_DATE(SDT,'%Y-%m-%d %H:%i') < now() AND Fk_flow = '" + fk_flow + "' GROUP BY DeptName";

		}
		else if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			sql = "SELECT DeptName, count(WorkID) as Num FROM WF_EmpWorks WHERE WFState >1 and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}') AND(sysdate - TO_DATE(SDT, 'yyyy-mm-dd hh24:mi:ss')) > 0 AND Fk_flow = '" + fk_flow + "' GROUP BY DeptName ";
			sql += "UNION SELECT DeptName, count(WorkID) as Num FROM WF_EmpWorks WHERE WFState >1 and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2}$') AND (sysdate - TO_DATE(SDT, 'yyyy-mm-dd')) > 0 AND Fk_flow = '" + fk_flow + "' GROUP BY DeptName";
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB)
		{
			sql = "SELECT DeptName, count(WorkID) as Num FROM WF_EmpWorks WHERE WFState >1 and to_timestamp(CASE WHEN SDT='无' THEN '' ELSE SDT END, 'yyyy-mm-dd hh24:MI:SS') < NOW(), GETDATE(), 120) AND Fk_flow = '" + fk_flow + "' GROUP BY DeptName";
		}
		else
		{
			sql = "SELECT DeptName, count(WorkID) as Num FROM WF_EmpWorks WHERE WFState >1 and convert(varchar(100),SDT,120) < CONVERT(varchar(100), GETDATE(), 120) AND Fk_flow = '" + fk_flow + "' GROUP BY DeptName";
		}
		DataTable OverTimeByDept = DBAccess.RunSQLReturnTable(sql);
		OverTimeByDept.TableName = "OverTimeByDept";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			OverTimeByDept.Columns.get(0).ColumnName = "DeptName";
			OverTimeByDept.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(OverTimeByDept);
		//逾期的 - 节点分组.
		if (SystemConfig.getAppCenterDBType() == DBType.MySQL|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			sql = "Select NodeName,count(*) as Num from WF_EmpWorks WHERE WFState >1 and STR_TO_DATE(SDT,'%Y-%m-%d %H:%i') < now() AND Fk_flow = '" + fk_flow + "' GROUP BY NodeName";

		}
		else if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			sql = "Select NodeName,count(*) as Num from WF_EmpWorks WHERE WFState >1 and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}') AND(sysdate - TO_DATE(SDT, 'yyyy-mm-dd hh24:mi:ss')) > 0 AND Fk_flow = '" + fk_flow + "' GROUP BY NodeName ";
			sql += "UNION Select NodeName,count(*) as Num from WF_EmpWorks WHERE WFState >1 and REGEXP_LIKE(SDT, '^[0-9]{4}-[0-9]{2}-[0-9]{2}$') AND (sysdate - TO_DATE(SDT, 'yyyy-mm-dd')) > 0 AND Fk_flow = '" + fk_flow + "' GROUP BY NodeName";
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB)
		{
			sql = "Select NodeName,count(*) as Num from WF_EmpWorks WHERE WFState >1 and to_timestamp(CASE WHEN SDT='无' THEN '' ELSE SDT END, 'yyyy-mm-dd hh24:MI:SS') < NOW() AND Fk_flow = '" + fk_flow + "' GROUP BY NodeName";
		}
		else
		{
			sql = "Select NodeName,count(*) as Num from WF_EmpWorks WHERE WFState >1 and convert(varchar(100),SDT,120) < CONVERT(varchar(100), GETDATE(), 120) AND Fk_flow = '" + fk_flow + "' GROUP BY NodeName";
		}
		DataTable OverTimeByNode = DBAccess.RunSQLReturnTable(sql);
		OverTimeByNode.TableName = "OverTimeByNode";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			OverTimeByNode.Columns.get(0).ColumnName = "NodeName";
			OverTimeByNode.Columns.get(1).ColumnName = "Num";
		}
		ds.Tables.add(OverTimeByNode);

			///#endregion 逾期。


		return bp.tools.Json.ToJson(ds);
	}

		///#endregion 欢迎页面初始化.


}
