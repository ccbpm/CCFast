package WebServiceImp;
import java.util.Hashtable;

import bp.da.DBAccess;
import bp.da.DataRow;
import bp.da.DataSet;
import bp.da.DataTable;
import bp.da.DataType;
import bp.tools.Json;
import bp.wf.Dev2Interface;
import bp.wf.Flow;
import bp.wf.GenerWorkFlow;
import bp.wf.GenerWorkerListAttr;
import bp.wf.GenerWorkerLists;
import bp.wf.Node;
import bp.wf.Nodes;
import bp.wf.SendReturnObjs;
import bp.wf.WFState;
import bp.wf.data.GERpt;

import bp.wf.template.Directions;
import bp.wf.template.FlowExt;
import bp.wf.template.NodeWorkCheck;
import bp.wf.template.Selector;

import WebService.LocalWSI;

public class LocalWS implements LocalWSI {

	/**
	 * 待办
	 *
	 * @param userNo
	 *            用户编号
	 * @param sysNo
	 *            系统编号,为空时返回平台所有数据
	 * @return
	 * @throws Exception
	 */

	@Override
	public String DB_Todolist(String userNo, String sysNo) throws Exception {
		try {
			Dev2Interface.Port_LoginByToken(userNo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "";
		if (DataType.IsNullOrEmpty(sysNo) == true)
			sql = "SELECT * FROM WF_EmpWorks WHERE FK_Emp='" + userNo + "'";
		else
			sql = "SELECT * FROM WF_EmpWorks WHERE DomainExt='" + sysNo + "' AND FK_Emp='" + userNo + "'";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return Json.ToJson(dt);
	}

	/**
	 * 获得在途
	 *
	 * @param userNo
	 *            用户编号
	 * @param sysNo
	 *            系统编号,为空时返回平台所有数据
	 * @return
	 * @throws Exception
	 */
	@Override
	public String DB_Runing(String userNo, String sysNo) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(userNo);
		DataTable dt = Dev2Interface.DB_GenerRuning(userNo, null, false);
		return Json.ToJson(dt);
	}

	/**
	 * 我可以发起的流程
	 *
	 * @param userNo
	 *            用户编号
	 * @param domainExt
	 *            系统编号,为空时返回平台所有数据
	 * @return 返回我可以发起的流程列表.
	 * @throws Exception
	 */

	@Override
	public String DB_StarFlows(String userNo, String domainExt) throws Exception {
		Dev2Interface.Port_LoginByToken(userNo);
		DataTable dt = Dev2Interface.DB_StarFlows(userNo, domainExt);
		return Json.ToJson(dt);
	}

	/**
	 * 我发起的流程实例
	 *
	 * @param userNo
	 *            用户编号
	 * @param domainExt
	 *            统编号,为空时返回平台所有数据
	 * @param pageSize
	 * @param pageIdx
	 * @return
	 */
	@Override
	public String DB_MyStartFlowInstance(String userNo, String domainExt, int pageSize, int pageIdx)throws Exception {
		try {
			Dev2Interface.Port_LoginByToken(userNo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "";
		if (domainExt == null)
			sql = "SELECT * FROM WF_GenerWorkFlow WHERE Starter='" + userNo + "'";
		else
			sql = "SELECT * FROM WF_GenerWorkFlow WHERE DomainExt='" + domainExt + "' AND Starter='" + userNo + "'";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return Json.ToJson(dt);
	}

	/**
	 * 创建WorkID
	 *
	 * @param flowNo
	 *            流程编号
	 * @param userNo
	 *            工作人员编号
	 * @return 一个长整型的工作流程实例
	 * @throws Exception
	 */

	@Override
	public long CreateWorkID(String flowNo, String userNo) throws Exception {
		Dev2Interface.Port_LoginByToken(userNo);
		return Dev2Interface.Node_CreateBlankWork(flowNo, userNo);
	}

	/**
	 * 执行发送
	 *
	 * @param flowNo
	 *            流的程模版ID
	 * @param workid
	 *            工作ID
	 * @param ht
	 *            参数，或者表单字段.
	 * @param toNodeID
	 *            到达的节点ID.如果让系统自动计算就传入0
	 * @param toEmps
	 *            到达的人员IDs,比如:zhangsan,lisi,wangwu. 如果为Null就标识让系统自动计算
	 * @return 发送的结果信息.
	 * @throws Exception
	 */

	@Override
	public String SendWork(String flowNo, long workid, Hashtable ht, int toNodeID, String toEmps, String userNo)
			throws Exception {
		Dev2Interface.Port_LoginByToken(userNo);
		SendReturnObjs objs = Dev2Interface.Node_SendWork(flowNo, workid, ht, toNodeID, toEmps);

		String msg = objs.ToMsgOfText();
        System.out.println(msg);
		Hashtable myht = new Hashtable();
		myht.put("Message", msg);
		myht.put("IsStopFlow", objs.getIsStopFlow());

		if (objs.getIsStopFlow() == false) {
			myht.put("VarAcceptersID", objs.getVarAcceptersID() == null ? "" : objs.getVarAcceptersID());
			myht.put("VarAcceptersName", objs.getVarAcceptersName() == null ? "" : objs.getVarAcceptersName());
			myht.put("VarToNodeID", objs.getVarToNodeID());
			myht.put("VarToNodeName", objs.getVarToNodeName() == null ? "" : objs.getVarToNodeName());
		}
		return Json.ToJson(myht);
	}

	/**
	 * 保存参数
	 *
	 * @param workid
	 *            工作ID
	 * @param paras
	 *            用于控制流程运转的参数，比如方向条件. 格式为:@JinE=1000@QingJaiTianShu=100
	 * @throws Exception
	 */

	@Override
	public void SaveParas(long workid, String paras, String userNo) throws Exception {
		Dev2Interface.Port_LoginByToken(userNo);
		Dev2Interface.Flow_SaveParas(workid, paras);

	}

	/**
	 * 获得下一个节点信息
	 *
	 * @param flowNo
	 *            流程编号
	 * @param workid
	 *            流程实例
	 * @param paras
	 *            方向条件所需要的参数，可以为空。
	 * @return 下一个节点的JSON.
	 * @throws Exception
	 */

	@Override
	public String GenerNextStepNode(String flowNo, long workid, String paras, String token) throws Exception {

		Dev2Interface.Port_LoginByToken(token);
		if (paras != null)
			Dev2Interface.Flow_SaveParas(workid, paras);

		int nodeID = Dev2Interface.Node_GetNextStepNode(flowNo, workid);
		Node nd = new Node(nodeID);

		// 如果字段 DeliveryWay = 4 就表示到达的接点是由当前节点发送人选择接收人.
		// 自定义参数的字段是 SelfParas, DeliveryWay
		// CondModel = 方向条件计算规则.
		return nd.ToJson();
	}

	/**
	 * 获得下一步节点的接收人
	 *
	 * @param flowNo
	 *            流程ID
	 * @param toNodeID
	 *            节点ID
	 * @param workid
	 *            工作事例ID
	 * @return 返回两个结果集一个是分组的Depts(No,Name)，另外一个是人员的Emps(No, Name,
	 *         FK_Dept),接受后，用于构造人员选择器.
	 * @throws Exception
	 */

	@Override
	public String GenerNextStepNodeEmps(String flowNo, int toNodeID, int workid, String token) throws Exception {

		Dev2Interface.Port_LoginByToken(token);

		Selector select = new Selector(toNodeID);
		Node nd = new Node(toNodeID);

		GERpt rpt = new GERpt("ND" + Integer.parseInt(flowNo) + "Rpt", workid);
		DataSet ds = select.GenerDataSet(toNodeID, rpt);
		return Json.ToJson(ds);
	}

	/**
	 * 将要退回到的节点
	 *
	 * @param workID
	 * @return 返回节点集合的json.
	 * @throws Exception
	 */

	@Override
	public String WillReturnToNodes(int workID, String token) throws Exception {

		try {

			Dev2Interface.Port_LoginByToken(token);


			GenerWorkFlow gwf = new GenerWorkFlow(workID);

			DataTable dt = Dev2Interface.DB_GenerWillReturnNodes(workID);
			return Json.ToJson(dt);
		} catch (Exception ex) {
			return "err@" + ex.getMessage();
		}
	}

	/**
	 * 将要达到的节点
	 *
	 * @param currNodeID
	 *            当前节点ID
	 * @return 返回节点集合的json.
	 * @throws Exception
	 */

	@Override
	public String WillToNodes(int currNodeID, String token) throws Exception {

		Dev2Interface.Port_LoginByToken(token);

		try {
			Node nd = new Node(currNodeID);

			Directions dirs = new Directions();
			Nodes nds = dirs.GetHisToNodes(currNodeID, false);
			return nds.ToJson();
		} catch (Exception ex) {
			return "err@" + ex.getMessage();
		}
	}

	/**
	 * 获得当前节点信息.
	 *
	 * @param currNodeID
	 *            当前节点ID
	 * @return
	 * @throws Exception
	 */

	@Override
	public String CurrNodeInfo(int currNodeID, String token) throws Exception {
		Dev2Interface.Port_LoginByToken(token);
		Node nd = new Node(currNodeID);
		return nd.ToJson();
	}

	/**
	 * 获得当前流程信息.
	 *
	 * @param flowNo
	 *            流程ID
	 * @return 当前节点信息
	 * @throws Exception
	 */

	@Override
	public String CurrFlowInfo(String flowNo, String token) throws Exception {
		Dev2Interface.Port_LoginByToken(token);
		Flow fl = new Flow(flowNo);
		return fl.ToJson();
	}

	/**
	 * 获得当前流程信息.
	 *
	 * @param workID
	 *            流程ID
	 * @return 当前节点信息
	 * @throws Exception
	 */
	@Override
	public String CurrGenerWorkFlowInfo(long workID, String token) throws Exception {
		Dev2Interface.Port_LoginByToken(token);
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		return gwf.ToJson();
	}

	/**
	 * 退回.
	 *
	 * @param workID
	 *            流程ID
	 * @param returnToNodeID
	 *            流程退回的节点ID
	 * @param returnMsg
	 *            退回原因
	 * @return 退回结果信息
	 * @throws Exception
	 */
	@Override
	public String Node_ReturnWork(long workID, int returnToNodeID, String returnMsg, String token) throws Exception {

		Dev2Interface.Port_LoginByToken(token);

		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		return Dev2Interface.Node_ReturnWork(gwf.getFlowNo(), workID, gwf.getFID(), gwf.getNodeID(),
				returnToNodeID,  returnMsg, false);

	}

	/**
	 * 执行流程结束 说明:强制流程结束.
	 * @param workID
	 *            工作ID
	 * @param msg
	 *            流程结束原因
	 * @return 返回成功执行信息
	 * @throws Exception
	 */

	@Override
	public String Flow_DoFlowOverQiangZhi(long workID, String msg, String token) throws Exception {
		Dev2Interface.Port_LoginByToken(token);
		return Dev2Interface.Flow_DoFlowOver( workID, msg,WFState.CompleteByNode);
	}

	@Override
	public void Port_Login(String UserNo) throws Exception {

	}


	/**
	 * 执行撤销
	 *
	 * @param flowNo
	 *            流程编码
	 * @param workID
	 *            工作ID
	 * @param unSendToNode
	 *            撤销到的节点
	 * @param fid
	 * @return
	 * @throws Exception
	 */
	@Override
	public String Runing_UnSend(String userNo, String flowNo, long workID, int unSendToNode, long fid)
			throws Exception {

		Dev2Interface.Port_LoginByToken(userNo);

		return Dev2Interface.Flow_DoUnSend(flowNo, workID, unSendToNode, fid);
	}

	/**
	 * 流程结束后回滚
	 *
	 * @param flowNo
	 *            流程编码
	 * @param workId
	 *            工作ID
	 * @param backToNodeID
	 *            回滚到的节点ID
	 * @param backMsg
	 *            回滚原因
	 * @return 回滚信息
	 * @throws Exception
	 */
	@Override
	public String DoRebackFlowData(String flowNo, long workId, int backToNodeID, String backMsg, String userNo)
			throws Exception {
		Dev2Interface.Port_LoginByToken(userNo);
		FlowExt flow = new FlowExt(flowNo);
		return flow.DoRebackFlowData(workId, backToNodeID, backMsg);
	}

	/**
	 * 获得当前流程信息.
	 *
	 * @param flowNo
	 *            流程ID.
	 * @return 当前节点信息
	 */
	@Override
	public String CurrFlowInfo(String flowNo) throws Exception {
		Flow fl = new Flow(flowNo);
		return fl.ToJson();
	}

	/**
	 * 获得当前流程信息.
	 *
	 * @param workID
	 *            流程ID.
	 * @return 当前节点信息
	 */
	@Override
	public String CurrGenerWorkFlowInfo(long workID) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		return gwf.ToJson();
	}

	/**
	 * 获得工作进度-用于展示流程的进度图
	 *
	 * @param workID
	 *            workID
	 * @param userNo
	 *            用户编号
	 * @return 返回待办
	 */
	@Override
	public String WorkProgressBar(long workID, String userNo) throws Exception {
		DataSet ds = Dev2Interface.DB_JobSchedule(workID);
		return Json.ToJson(ds);
	}

	/**
	 * 获得工作进度-用于展示流程的进度图 - for zhongkeshuguang.
	 *
	 * @param workID
	 *            workID
	 * @param userNo
	 *            用户编号
	 * @return 返回待办
	 */
	@Override
	public String WorkProgressBar20(long  workID, String userNo) throws Exception
    {

		Dev2Interface.Port_LoginByToken(userNo);
		DataSet ds = Dev2Interface.DB_JobSchedule(workID);
		return bp.tools.Json.ToJson(ds);

    }

	@Override
	public String DB_RunSQLReturnJSON(String sqlOfSelect, String password) throws Exception {
		return null;
	}

	/**
	 * 执行抄送
	 *
	 * @param fk_node
	 *            节点编号
	 * @param workID
	 *            工作ID
	 * @param toEmpNo
	 *            抄送人员编号
	 * @param toEmpName
	 *            抄送人员人员名称
	 * @param msgTitle
	 *            标题
	 * @param msgDoc
	 *            内容
	 * @return 执行信息
	 * @throws Exception
	 */
	@Override
	public String Node_CC_WriteTo_CClist(int fk_node, long workID, String toEmpNo, String toEmpName, String msgTitle,
			String msgDoc, String userNo) throws Exception {
		Dev2Interface.Port_LoginByToken(userNo);
		return Dev2Interface.Node_CC_WriteTo_CClist(fk_node, workID, toEmpNo, toEmpName, msgTitle, msgDoc);
	}


	   /**
	 	 是否可以查看该流程
	 	 @param flowNo 流程编号
	 	 @param workid 工作ID
	 	 @return 是否可以查看该工作.
	 * @throws Exception
	 	*/
	@Override
    public Boolean Flow_IsCanView(String flowNo, long workid, String userNo) throws Exception
    {
        return Dev2Interface.Flow_IsCanViewTruck(flowNo, workid,userNo);
    }

    /**
	 是否可以查看该流程
	 @param userNo
	 @param workid 用户密码
	 @return 是否可以查看该工作.
* @throws Exception
	*/
	@Override
    public Boolean Flow_IsCanDoCurrentWork(long workid, String userNo) throws Exception
    {
        return Dev2Interface.Flow_IsCanDoCurrentWork(workid, userNo);
    }



	/**
	 * 获取指定人员的抄送列表 说明:可以根据这个列表生成指定用户的抄送数据.
	 *
	 * @param userNo
	 *            人员编号,如果是null,则返回所有的.
	 * @return 返回该人员的所有抄送列表,结构同表WF_CCList.
	 */
	@Override
	public String DB_CCList(String userNo) throws Exception {
		Dev2Interface.Port_LoginByToken(userNo);

		DataTable dt = Dev2Interface.DB_CCList(userNo);
		return Json.ToJson(dt);
	}

	/**
                    写入审核信息

     <param name="workid">workID</param>
     <param name="msg">审核信息</param>
     *
     */
	@Override
    public void Node_WriteWorkCheck(long workid, String msg) throws Exception
    {
        GenerWorkFlow gwf = new GenerWorkFlow(workid);
          Dev2Interface.Node_WriteWorkCheck(gwf.getWorkID(), msg,"",null);
    }

	/**
    * 获取审核信息
    * @param FK_Flow 流程编号
    * @param FK_Node 节点编号
    * @param workId 流程ID
    * @param fid 干流程ID（针对子线程）
    * * @param isReadonly是否只读
    * @return
    * @throws Exception
    */
	@Override
	public String DB_WorkCheck(String FK_Flow, int FK_Node, long workId, long fid,boolean isReadonly) throws Exception {
		return FK_Flow;
	}

	/**
    * 获取流程时间轴数据
    * @param workid
    * @param fid
    * @param fk_flow
    * @throws Exception
    */
	@Override
	public String Flow_TimeBase(long workid, long fid, String fk_flow) throws Exception {
		DataSet ds = new DataSet();

		//获取track.
		DataTable dt = Dev2Interface.DB_GenerTrackTable(fk_flow, workid, fid);
		ds.Tables.add(dt);
		//获取 WF_GenerWorkFlow
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID( workid) ;
		gwf.RetrieveFromDBSources();
		ds.Tables.add(gwf.ToDataTableField("WF_GenerWorkFlow"));

		if (gwf.getWFState() != WFState.CompleteEnd)
		{
			GenerWorkerLists gwls = new GenerWorkerLists();
			gwls.Retrieve(GenerWorkerListAttr.WorkID, workid);

			ds.Tables.add(gwls.ToDataTableField("WF_GenerWorkerList"));
		}

	    //把节点审核配置信息.
		NodeWorkCheck fwc = new NodeWorkCheck(gwf.getNodeID());
		ds.Tables.add(fwc.ToDataTableField("FrmWorkCheck"));

		//返回结果.
		return Json.ToJson(ds);

	}

	/**
	 * 我参与的
	 */
	@Override
	public String DB_MyJoinFlows(String userNo) throws Exception {
		Dev2Interface.Port_LoginByToken(userNo);
		return userNo;
	}

	/***
	 * 根据流程WorkID、FK_Flow删除流程
	 * @param userNo
	 * @param workid
	 * @param fk_flow
	 * @return
	 * @throws Exception
	 */
	@Override
	public String DeleteFlow(String userNo,long workid,String fk_flow) throws Exception{
		Dev2Interface.Port_LoginByToken(userNo);
		return Dev2Interface.Flow_DoDeleteFlowByReal( workid, true);
	}

	/**
	 * 附件上传
	 * @param fk_node 节点编号
	 * @param fk_flow 流程编号
	 * @param workid 流程WorkID
	 * @param athNo 附件属性编号
	 * @param fk_mapData 表单属性编号
	 * @param filePath 附件路径
	 * @param fileName 附件名称
	 * @param sort 附件分类
	 * @param fid 干流程ID
	 * @param pworkid 父流程ID
	 * @return
	 * @throws Exception
	 */
	@Override
	public String CCForm_AddAth(int fk_node, String fk_flow, long workid, String athNo, String fk_mapData,
								String filePath, String fileName, String sort, int fid, int pworkid) throws Exception{
		Dev2Interface.CCForm_AddAth(fk_node,fk_flow,workid,athNo,fk_mapData,filePath,fileName,sort,"",fid,pworkid);
		return "附件传递成功";
	}
}
