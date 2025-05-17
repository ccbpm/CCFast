package bp.wf.httphandler;

import bp.difference.handler.CommonFileUtils;
import bp.sys.*;
import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.base.EventBase;
import bp.wf.template.*;
import bp.difference.*;
import bp.tools.*;
import bp.*;
import bp.wf.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.*;

public class WF_Admin_AttrNode extends bp.difference.handler.DirectoryPageBase
{
	/**
	 构造函数
	*/
	public WF_Admin_AttrNode()
	{
	}


		///#region 事件基类.
	/**
	 事件类型
	*/
	public final String getShowType()
	{
		if (this.getNodeID() != 0)
		{
			return "Node";
		}

		if (this.getNodeID() == 0 && DataType.IsNullOrEmpty(this.getFlowNo()) == false && this.getFlowNo().length() >= 3)
		{
			return "Flow";
		}

		if (this.getNodeID() == 0 && DataType.IsNullOrEmpty(this.getFrmID()) == false)
		{
			return "Frm";
		}

		return "Node";
	}
	/**
	 获得该节点下已经绑定该类型的实体.

	 @return
	*/
	public final String ActionDtl_Init()
	{
		//业务单元集合.
		DataTable dtBuess = new DataTable();
		dtBuess.Columns.Add("No", String.class);
		dtBuess.Columns.Add("Name", String.class);
		dtBuess.TableName = "BuessUnits";
		ArrayList<BuessUnitBase> al = ClassFactory.GetObjects("BP.Sys.BuessUnitBase");
		for (BuessUnitBase en : al)
		{
			DataRow dr = dtBuess.NewRow();
			dr.setValue("No", en.toString());
			dr.setValue("Name", en.getTitle());
			dtBuess.Rows.add(dr);
		}

		return Json.ToJson(dtBuess);
	}

	/**
	 * 获取eventbase类
	 * @return
	 */
	public final String ActionEventBase_Init()
	{
		//业务单元集合.
		DataTable dtBuess = new DataTable();
		dtBuess.Columns.Add("No", String.class);
		dtBuess.Columns.Add("Name", String.class);
		dtBuess.TableName = "EventBase";
		ArrayList<EventBase> al = ClassFactory.GetObjects("BP.Sys.Base.EventBase");
		for (EventBase en : al)
		{
			DataRow dr = dtBuess.NewRow();
			dr.setValue("No", en.toString());
			dr.setValue("Name", en.getTitle());
			dtBuess.Rows.add(dr);
		}

		return Json.ToJson(dtBuess);
	}

		///#endregion 事件基类.


		///#region   公文维护
	/**
	 选择一个模版

	 @return
	*/
	public final String SelectDocTemp_Save() throws Exception {
		String docTempNo = this.GetRequestVal("no");

		DocTemplate docTemplate = new DocTemplate(docTempNo);

		if ((new File(docTemplate.getFilePath())).isFile() == false)
		{
			return "err@选择的模版文件不存在.";
		}

		//获得模版的流.
		byte[] bytes = DataType.ConvertFileToByte(docTemplate.getFilePath());

		//保存到数据库里.
		Flow fl = new Flow(this.getFlowNo());
		DBAccess.SaveBytesToDB(bytes, fl.getPTable(), "OID", String.valueOf( this.getWorkID()), "WordFile");

		////模板与业务的绑定.
		//DocTempFlow dtf = new DocTempFlow();
		//dtf.CheckPhysicsTable();

		//if (dtf.IsExit(DocTempFlowAttr.WorkID, workId))
		//{
		//    dtf.Delete();
		//}
		//dtf.WorkID = workId;
		//dtf.TempNo = docTempNo;
		//dtf.setMyPK(workId + "_" + docTempNo;
		//dtf.Insert();

		return "模板导入成功.";
	}

	public final String FlowDocInit() throws Exception {
		MethodReturnMessage<String> msg = new MethodReturnMessage<String>();
		msg.setSuccess(true);

		try
		{
			int nodeId = Integer.parseInt(this.GetRequestVal("nodeId"));
			int workId = Integer.parseInt(this.GetRequestVal("workId"));
			String flowNo = this.GetRequestVal("fk_flow");
			String tableName = "ND" + Integer.parseInt(flowNo) + "Rpt";

			String str = "WordFile";
			if (DBAccess.IsExitsTableCol(tableName, str) == false)
			{
				/*如果没有此列，就自动创建此列.*/
				String sql = "ALTER TABLE " + tableName + " ADD  " + str + " image ";

				if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
				{
					sql = "ALTER TABLE " + tableName + " ADD  " + str + " image ";
				}

				DBAccess.RunSQL(sql);
			}

			byte[] bytes = DBAccess.GetByteFromDB(tableName, "OID", String.valueOf(workId), "WordFile");
			Node node = new Node(nodeId);

			if (!node.getItIsStartNode())
			{
				if (bytes == null)
				{
					msg.setMessage("{\"IsStartNode\":0,\"IsExistFlowData\":0,\"IsExistTempData\":0}");
				}
				else
				{
					msg.setMessage("{\"IsStartNode\":0,\"IsExistFlowData\":1,\"IsExistTempData\":0}");
				}
			}
			else //开始节点
			{
				DocTemplates dts = new DocTemplates();
				int count = dts.Retrieve(DocTemplateAttr.FK_Node, nodeId, null);

				if (bytes == null)
				{
					if (count == 0)
					{
						msg.setMessage("{\"IsStartNode\":1,\"IsExistFlowData\":0,\"IsExistTempData\":0}");
						msg.setData(null);
					}
					else
					{
						msg.setMessage("{\"IsStartNode\":1,\"IsExistFlowData\":0,\"IsExistTempData\":" + count + "}");
						msg.setData(dts.ToJson("dt"));
					}
				}
				else
				{
					if (count == 0)
					{
						msg.setMessage("{\"IsStartNode\":1,\"IsExistFlowData\":1,\"IsExistTempData\":0}");
						msg.setData(null);
					}
					else
					{
						msg.setMessage("{\"IsStartNode\":1,\"IsExistFlowData\":1,\"IsExistTempData\":" + count + "}");
						msg.setData(dts.ToJson("dt"));
					}
				}
			}
		}
		catch (RuntimeException ex)
		{
			msg.setSuccess(false);
			msg.setMessage(ex.getMessage());
		}

		return bp.tools.Json.ToJson(msg);
	}

	/**
	 删除

	 @return
	*/
	public final String DocTemp_Del() throws Exception {
		int no = Integer.parseInt(this.GetRequestVal("no"));

		DocTemplate dt = new DocTemplate();
		dt.Retrieve(DocTemplateAttr.No, no);
		dt.Delete();

		return "操作成功";
	}
	/**
	 模版文件上传
	 @return
	*/
	public final String DocTemp_Upload() throws Exception {

		Node nd = new Node(this.getFK_Node());

		HttpServletRequest request = getRequest();
		String contentType = request.getContentType();
		String fileName="";
		String fileFullPath="";
		if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
			fileName = CommonFileUtils.getOriginalFilename(request, "file");
			String path = SystemConfig.getPathOfDataUser() + "DocTemplate/" + nd.getFlowNo();
			fileFullPath = path + "/" + fileName;

			//上传文件.
			if ((new File(path)).isDirectory() == false)
			{
				(new File(path)).mkdirs();
			}

			try {
				CommonFileUtils.upload(request, "file", new File(fileFullPath));
			} catch (Exception e) {
				e.printStackTrace();
				return "err@执行失败";
			}
		}



		//插入模版.
		DocTemplate dt = new DocTemplate();
		dt.setNodeID(getFK_Node());
		dt.setNo(DBAccess.GenerGUID());
		dt.setName( fileName);
		dt.setFilePath(fileFullPath); //路径
		dt.Insert();

		//保存文件.
		DBAccess.SaveFileToDB(fileFullPath, dt.getEnMap().getPhysicsTable(), "No", dt.getNo(), "FileTemplate");
		return dt.ToJson();
	}

	///#endregion




		///#region  节点消息
	public final String PushMsg_Init() throws Exception {
		//增加上单据模版集合.
		int nodeID = this.GetRequestValInt("FK_Node");
		PushMsgs ens = new PushMsgs(nodeID);
		return ens.ToJson("dt");
	}
	public final String PushMsg_Save() throws Exception {
		PushMsg msg = new PushMsg();
		msg.setMyPK(this.getMyPK());
		msg.RetrieveFromDBSources();

		msg.setEventNo(this.getEventNo());
		msg.setNodeID(this.getNodeID());

		Node nd = new Node(this.getNodeID());
		Nodes nds = new Nodes(nd.getFlowNo());
		msg.setFlowNo(nd.getFlowNo());

		int rbSMS = this.GetRequestValInt("RB_SMS");
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

		//推送方式。
		msg.setIsEnableSMS(Integer.parseInt(ContextHolderUtils.getRequest().getParameter("RB_SMS").replace("RB_SMS_", "")));

		//表单字段作为接收人.
		msg.setSMSField(ContextHolderUtils.getRequest().getParameter("DDL_SMS_Fields"));


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

		//按照SQL
		msg.setBySQL(ContextHolderUtils.getRequest().getParameter("TB_SQL"));

		//发给指定的人员
		msg.setByEmps(ContextHolderUtils.getRequest().getParameter("TB_Emps"));

		//短消息发送设备
		msg.setSMSPushModel(this.GetRequestVal("PushModel"));

		//邮件标题
		msg.setMailTitleReal(ContextHolderUtils.getRequest().getParameter("TB_title"));

		//短信内容模版.
		msg.setSMSDocReal(ContextHolderUtils.getRequest().getParameter("TB_SMS"));

		//节点预警
		if (Objects.equals(this.getEventNo(), EventListNode.NodeWarning))
		{
			int noticeType = Integer.parseInt(ContextHolderUtils.getRequest().getParameter("RB_NoticeType").replace("RB_NoticeType", ""));
			msg.SetPara("NoticeType", noticeType);
			int hour = Integer.parseInt(ContextHolderUtils.getRequest().getParameter("TB_NoticeHour"));
			msg.SetPara("NoticeHour", hour);
		}

		//节点逾期
		if (Objects.equals(this.getEventNo(), EventListNode.NodeOverDue))
		{
			int noticeType = Integer.parseInt(ContextHolderUtils.getRequest().getParameter("RB_NoticeType").replace("RB_NoticeType", ""));
			msg.SetPara("NoticeType", noticeType);
			int day = Integer.parseInt(ContextHolderUtils.getRequest().getParameter("TB_NoticeDay"));
			msg.SetPara("NoticeDay", day);
		}

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

	public final String PushMsgEntity_Init() throws Exception {
		DataSet ds = new DataSet();

		//字段下拉框.
		//select * from Sys_MapAttr where FK_MapData='ND102' and LGType = 0 AND MyDataType =1

		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, "ND" + this.getNodeID(), "LGType", 0, "MyDataType", 1, null);
		ds.Tables.add(attrs.ToDataTableField("FrmFields"));

		//节点
		//TODO 数据太多优化一下
		Node nd = new Node(this.getNodeID());
		Nodes nds = new Nodes(nd.getFlowNo());
		ds.Tables.add(nds.ToDataTableField("Nodes"));

		//mypk
		PushMsg msg = new PushMsg();
		msg.setMyPK(this.getMyPK());
		msg.RetrieveFromDBSources();
		ds.Tables.add(msg.ToDataTableField("PushMsgEntity"));

		return Json.ToJson(ds);
	}



		///#endregion


		///#region 表单模式
	/**
	 表单模式

	 @return
	*/
	public final String NodeFromWorkModel_Init() throws Exception {
		//数据容器.
		DataSet ds = new DataSet();

		// 当前节点信息.
		Node nd = new Node(this.getNodeID());

		nd.WorkID= this.getWorkID(); //为获取表单ID ( NodeFrmID )提供参数.
		nd.setNodeFrmID( nd.getNodeFrmID());
		// nd.setFormUrl(nd.getFormUrl();

		DataTable mydt = nd.ToDataTableField("WF_Node");
		ds.Tables.add(mydt);

		BtnLab btn = new BtnLab(this.getNodeID());
		DataTable dtBtn = btn.ToDataTableField("WF_BtnLab");
		ds.Tables.add(dtBtn);

		//节点s
		Nodes nds = new Nodes(nd.getFlowNo());

		//节点s
		ds.Tables.add(nds.ToDataTableField("Nodes"));

		return Json.ToJson(ds);
	}
	/**
	 表单模式

	 @return
	*/
	public final String NodeFromWorkModel_Save() throws Exception {
		Node nd = new Node(this.getNodeID());

		MapData md = new MapData("ND" + this.getNodeID());

		//用户选择的表单类型.
		String selectFModel = this.GetValFromFrmByKey("FrmS");

		//使用ccbpm内置的节点表单
		if (Objects.equals(selectFModel, "DefFrm"))
		{
			//呈现风格
			String frmModel = this.GetValFromFrmByKey("RB_Frm");
			if (Objects.equals(frmModel, "0"))
			{
				//自由表单
				nd.setFormType(NodeFormType.Develop);
				nd.DirectUpdate();

				md.setHisFrmType(FrmType.Develop);
				md.Update();
			}
			else
			{
				//经典表单
				nd.setFormType(NodeFormType.FoolForm);
				nd.DirectUpdate();

				md.setHisFrmType(FrmType.FoolForm);
				md.Update();
			}
			//表单引用
			String refFrm = this.GetValFromFrmByKey("RefFrm");
			//当前节点表单
			if (Objects.equals(refFrm, "0"))
			{
				nd.setNodeFrmID( "");
				nd.DirectUpdate();
			}
			//其他节点表单
			if (Objects.equals(refFrm, "1"))
			{
				nd.setNodeFrmID( "ND" + this.GetValFromFrmByKey("DDL_Frm"));
				nd.DirectUpdate();
			}
		}

		//使用傻瓜轨迹表单模式.
		if (Objects.equals(selectFModel, "FoolTruck"))
		{
			nd.setFormType(NodeFormType.FoolTruck);
			nd.DirectUpdate();

			md.setHisFrmType(FrmType.FoolForm); //同时更新表单表住表.
			md.Update();
		}

		//使用嵌入式表单
		if (Objects.equals(selectFModel, "SelfForm"))
		{
			nd.setFormType(NodeFormType.SelfForm);
			nd.setFormUrl(this.GetValFromFrmByKey("TB_CustomURL"));
			nd.DirectUpdate();

			md.setHisFrmType(FrmType.Url); //同时更新表单表住表.
			md.setUrlExt(this.GetValFromFrmByKey("TB_CustomURL"));
			md.Update();

		}
		//使用SDK表单
		if (Objects.equals(selectFModel, "SDKForm"))
		{
			nd.setFormType(NodeFormType.SDKForm);
			nd.setFormUrl(this.GetValFromFrmByKey("TB_FormURL"));
			nd.DirectUpdate();

			md.setHisFrmType(FrmType.Url);
			md.setUrlExt(this.GetValFromFrmByKey("TB_FormURL"));
			md.Update();

		}
		//绑定多表单
		if (Objects.equals(selectFModel, "SheetTree"))
		{

			String sheetTreeModel = this.GetValFromFrmByKey("SheetTreeModel");

			if (Objects.equals(sheetTreeModel, "0"))
			{
				nd.setFormType(NodeFormType.SheetTree);
				nd.DirectUpdate();

				md.setHisFrmType(FrmType.FoolForm); //同时更新表单表住表.
				md.Update();
			}
			else
			{
				nd.setFormType(NodeFormType.DisableIt);
				nd.DirectUpdate();

				md.setHisFrmType(FrmType.FoolForm); //同时更新表单表住表.
				md.Update();
			}
		}

		return "保存成功...";
	}

		///#endregion 表单模式


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
				intHisDeliveryWayCount = nes.Retrieve(NodeStationAttr.FK_Node, node.getNodeID(), null);
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
		return Json.ToJson(dt);
	}

		///#endregion


		///#region 特别控件特别用户权限
	public final String SepcFiledsSepcUsers_Init()
	{

		/*String fk_mapdata = this.GetRequestVal("FK_MapData");
		if (DataType.IsNullOrEmpty(fk_mapdata))
		    fk_mapdata = "ND101";

		String fk_node = this.GetRequestVal("FK_Node");
		if (DataType.IsNullOrEmpty(fk_node))
		    fk_mapdata = "101";


		BP.Sys.MapAttrs attrs = new BP.Sys.MapAttrs(fk_mapdata);

		BP.Sys.FrmImgs imgs = new BP.Sys.FrmImgs(fk_mapdata);

		BP.Sys.MapExts exts = new BP.Sys.MapExts();
		int mecount = exts.Retrieve(BP.Sys.MapExtAttr.FK_MapData, fk_mapdata,
		    BP.Sys.MapExtAttr.Tag, this.GetRequestVal("FK_Node"),
		    BP.Sys.MapExtAttr.ExtType, "SepcFiledsSepcUsers");

		BP.Sys.FrmAttachments aths = new BP.Sys.FrmAttachments(fk_mapdata);

		exts = new BP.Sys.MapExts();
		exts.Retrieve(BP.Sys.MapExtAttr.FK_MapData, fk_mapdata,
		    BP.Sys.MapExtAttr.Tag, this.GetRequestVal("FK_Node"),
		    BP.Sys.MapExtAttr.ExtType, "SepcAthSepcUsers");
		*/
		return ""; //toJson
	}

		///#endregion


		///#region 批量发起规则设置
	public final String BatchStartFields_Init() throws Exception {
		int nodeID = Integer.parseInt(String.valueOf(this.getNodeID()));
		//获取节点字段集合
		MapAttrs attrs = new MapAttrs("ND" + nodeID);
		//获取节点对象
		Node nd = new Node(nodeID);
		//获取批量发起设置规则
		SysEnums ses = new SysEnums(NodeAttr.BatchRole);
		//获取当前节点设置的批处理规则
		String srole = "";
		if (nd.getHisBatchRole() == BatchRole.None)
		{
			srole = "0";
		}
		else if (nd.getHisBatchRole() == BatchRole.WorkCheckModel)
		{
			srole = "1";
		}
		else
		{
			srole = "2";
		}
		return "{\"nd\":" + nd.ToJson(true) + ",\"ses\":" + ses.ToJson("dt") + ",\"attrs\":" + attrs.ToJson("dt") + ",\"BatchRole\":" + srole + "}";
	}

		///#endregion


		///#region 发送阻塞模式
	public final String BlockModel_Save() throws Exception {
		Node nd = new Node(this.getNodeID());

		nd.setBlockAlert(this.GetRequestVal("TB_Alert")); //提示信息.

		int val = this.GetRequestValInt("RB_BlockModel");
		nd.SetValByKey(NodeAttr.BlockModel, val);
		if (nd.getBlockModel() == BlockModel.None)
		{
			nd.setBlockModel(BlockModel.None);
		}

		if (nd.getBlockModel() == BlockModel.CurrNodeAll)
		{
			nd.setBlockModel(BlockModel.CurrNodeAll);
		}

		if (nd.getBlockModel() == BlockModel.SpecSubFlow)
		{
			nd.setBlockModel(BlockModel.SpecSubFlow);
			nd.setBlockExp(this.GetRequestVal("TB_SpecSubFlow"));
		}

		if (nd.getBlockModel() == BlockModel.BySQL)
		{
			nd.setBlockModel(BlockModel.BySQL);
			nd.setBlockExp(this.GetRequestVal("TB_SQL"));
		}

		if (nd.getBlockModel() == BlockModel.ByExp)
		{
			nd.setBlockModel(BlockModel.ByExp);
			nd.setBlockExp(this.GetRequestVal("TB_Exp"));
		}

		if (nd.getBlockModel() == BlockModel.SpecSubFlowNode)
		{
			nd.setBlockModel(BlockModel.SpecSubFlowNode);
			nd.setBlockExp(this.GetRequestVal("TB_SpecSubFlowNode"));
		}
		if (nd.getBlockModel() == BlockModel.SameLevelSubFlow)
		{
			nd.setBlockModel(BlockModel.SameLevelSubFlow);
			nd.setBlockExp(this.GetRequestVal("TB_SameLevelSubFlow"));
		}

		nd.setBlockAlert(this.GetRequestVal("TB_Alert"));
		nd.Update();

		return "保存成功.";
	}

		///#endregion


		///#region 可以撤销的节点
	public final String CanCancelNodes_Save() throws Exception {
		NodeCancels rnds = new NodeCancels();
		rnds.Delete(NodeCancelAttr.FK_Node, this.getNodeID());

		Nodes nds = new Nodes();
		nds.Retrieve(NodeAttr.FK_Flow, this.getFlowNo(), null);

		int i = 0;
		for (Node nd : nds.ToJavaList())
		{
			String cb = this.GetRequestVal("CB_" + nd.getNodeID());
			if (cb == null || Objects.equals(cb, ""))
			{
				continue;
			}

			NodeCancel nr = new NodeCancel();
			nr.setNodeID(this.getNodeID());
			nr.setCancelTo(nd.getNodeID());
			nr.Insert();
			i++;
		}
		if (i == 0)
		{
			return "请您选择要撤销的节点。";
		}

		return "设置成功.";
	}

		///#endregion


		///#region 表单检查(CheckFrm.htm)
	public final String CheckFrm_Check() throws Exception {
		if (!Objects.equals(bp.web.WebUser.getNo(), "admin"))
		{
			return "err@只有管理员有权限进行此项操作！";
		}

		if ((this.getFrmID() == null || this.getFrmID().isEmpty()))
		{
			return "err@参数FK_MapData不能为空！";
		}

		String msg = "";

		//1.检查字段扩展设置
		MapExts mes = new MapExts(this.getFrmID());
		MapAttrs attrs = new MapAttrs(this.getFrmID());
		MapDtls dtls = new MapDtls(this.getFrmID());
		Entity en = null;
		String fieldMsg = "";

		//1.1主表
		for (MapExt me : mes.ToJavaList())
		{
			if (!(me.getAttrOfOper() == null || me.getAttrOfOper().isEmpty()))
			{
				en = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, me.getAttrOfOper());

				if (en != null && !(me.getAttrsOfActive() == null || me.getAttrsOfActive().isEmpty()))
				{
					en = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, me.getAttrsOfActive());
				}
			}

			if (en == null)
			{
				me.DirectDelete();
				msg += "删除扩展设置中MyPK=" + me.getPKVal() + "的设置项；<br />";
			}
		}

		//1.2明细表
		for (MapDtl dtl : dtls.ToJavaList())
		{
			mes = new MapExts(dtl.getNo());
			attrs = new MapAttrs(dtl.getNo());

			for (MapExt me : mes.ToJavaList())
			{
				if (!(me.getAttrOfOper() == null || me.getAttrOfOper().isEmpty()))
				{
					en = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, me.getAttrOfOper());

					if (en != null && !(me.getAttrsOfActive() == null || me.getAttrsOfActive().isEmpty()))
					{
						en = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, me.getAttrsOfActive());
					}
				}

				if (en == null)
				{
					me.DirectDelete();
					msg += "删除扩展设置中MyPK=" + me.getPKVal() + "的设置项；<br />";
				}
			}
		}

		//2.检查字段权限
		FrmFields ffs = new FrmFields();
		ffs.Retrieve(FrmFieldAttr.FrmID, this.getFrmID(), null);

		//2.1主表
		for (FrmField ff : ffs.ToJavaList())
		{
			en = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, ff.getKeyOfEn());

			if (en == null)
			{
				ff.DirectDelete();
				msg += "删除字段权限中MyPK=" + ff.getPKVal() + "的设置项；<br />";
			}
		}

		//2.2明细表
		for (MapDtl dtl : dtls.ToJavaList())
		{
			ffs = new FrmFields();
			ffs.Retrieve(FrmFieldAttr.FrmID, dtl.getNo(), null);
			attrs = new MapAttrs(dtl.getNo());

			for (FrmField ff : ffs.ToJavaList())
			{
				en = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, ff.getKeyOfEn());

				if (en == null)
				{
					ff.DirectDelete();
					msg += "删除字段权限中MyPK=" + ff.getPKVal() + "的设置项；<br />";
				}
			}
		}

		msg += "检查完成！";

		return msg;
	}

		///#endregion


	public final String NodeStationGroup_Init()
	{
		String sql = "select No as \"No\", Name as \"Name\" FROM port_StationType where No in " + "(select Fk_StationType from Port_Station where OrgNo ='" + this.GetRequestVal("orgNo") + "') group by No,Name";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return Json.ToJson(dt);
	}
	/**
	 删除,该组织下已经保存的岗位.
	 @return
	 */
	public final void NodeStationGroup_Dele()
	{
		String sql = "DELETE FROM WF_NodeStation WHERE FK_Station IN (SELECT No FROM Port_Station WHERE OrgNo='" + this.GetRequestVal("orgNo") + "') AND FK_Node=" + this.GetRequestVal("nodeID");
		DBAccess.RunSQL(sql);
	}
	/**
	 删除,该组织下已经保存的岗位.
	 @return
	 */
	public final void NodeDept_Dele()
	{
		String sql = "DELETE FROM WF_NodeDept WHERE FK_Node=" + this.GetRequestVal("nodeID");
		DBAccess.RunSQL(sql);
	}
	/**
	 删除,该组织下已经保存的岗位.
	 @return
	 */
	public final void NodeDeptGroup_Dele()
	{
		String sql = "DELETE FROM WF_NodeDept WHERE FK_Node=" + this.GetRequestVal("nodeID") + " AND FK_Dept IN (SELECT No FROM Port_Dept WHERE OrgNo='" + this.GetRequestVal("orgNo") + "')";
		DBAccess.RunSQL(sql);
	}

	/**
	 WF_Node_Up
	 @return
	 */
	public final void WF_Node_Up()
	{
		String sql = "UPDATE WF_Node SET NodeAppType=" + this.GetRequestVal("appType") + " WHERE NodeID=" + this.GetRequestVal("nodeID");
		DBAccess.RunSQL(sql);
	}
	/**
	 NodeStationGroup_init
	 @return
	 */
	public final String NodeAppType()
	{
		String sql = "SELECT NodeAppType FROM WF_Node WHERE NodeID=" + this.GetRequestVal("FK_Node");
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "NodeAppType";
		}
		return Json.ToJson(dt);
	}
}
