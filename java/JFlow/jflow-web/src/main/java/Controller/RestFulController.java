package Controller;

import WebServiceImp.LocalWS;
import bp.da.*;
import bp.difference.SystemConfig;
import bp.difference.handler.CommonFileUtils;
import bp.en.QueryObject;
import bp.port.Emp;
import bp.sys.MapData;
import bp.tools.HttpClientUtil;
import bp.tools.Json;
import bp.tools.StringUtils;
import bp.web.WebUser;
import bp.wf.*;
import bp.wf.data.GERpt;
import bp.wf.port.WFEmp;
import bp.wf.template.Directions;
import bp.wf.template.FlowExt;
import bp.wf.template.Selector;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

@RestController
@RequestMapping(value = "/restful")
@CrossOrigin(origins = "*")
public class RestFulController {
    //ip:port/project
	/**
	 保存文件

	 @return
	 */
	@RequestMapping(value = "/wpssavefile")
	public final String WpsFrm_SaveFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// String fileName = "c:\\xxxx\temp.px";
		String workID = request.getParameter("WorkID");
		String frmID = request.getParameter("FrmID");
		GenerWorkFlow gwf = new GenerWorkFlow(Integer.valueOf(workID));
		if (gwf.getPWorkID() != 0)
		{
			workID = String.valueOf(gwf.getPWorkID());
		}

		String fileName = SystemConfig.getPathOfTemp() + "\\" + workID + "." +frmID + ".docx";

		MapData md = new MapData(frmID);

		File file =null;
		file = new File(fileName);
		//CommonFileUtils.upload(request, "File_Upload", file);
		/*HttpPostedFile file = HttpContextHelper.RequestFiles().get(0); //context.Request.Files;
		file.SaveAs(fileName);*/
		CommonFileUtils.upload(request, "fileField", file);
		//保存文件.
		DBAccess.SaveFileToDB(fileName, md.getPTable(), "OID", String.valueOf(workID), "DBFile");

		return "上传成功.";
	}
    @RequestMapping(value = "/test")
    public String queryPrestoDemo(HttpServletRequest request, HttpServletResponse response) {
		JSONObject res = new JSONObject();
        String userNo = request.getParameter("userNo");
        //请销假
        String domainExt = request.getParameter("domainExt");

        LocalWS localWS = new LocalWS();

        String result = null;
        try {
            result = localWS.DB_StarFlows(userNo,domainExt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
	/**
	 * 统一身份登录JY
	 *
	 * @return token
	 * @throws Exception
	 */
	@RequestMapping("/SSOLogin")
	public String SSOLogin(String code) throws Exception {
		//获取token
		String appId = SystemConfig.getAppSettings().get("AppID").toString();
		String  appSecret= SystemConfig.getAppSettings().get("AppSecret").toString();
		java.util.Map<String, String> headerMap = new Hashtable<String, String>();
		//Base64 base64 = new Base64();
		headerMap.put("Content-Type", "application/json");

		String info = "";
		info += "{";
		info += "\"client_id\":\"" + appId + "\",";
		info += "\"client_secret\":\"" + appSecret + "\",";
		info += "\"code\":\"" + code + "\",";
		info += "\"scope\":\"uid+cn+userIdCode \",";
		info += "\"redirect_uri\":\"http://10.1.0.21/restful/SSOLogin\",";
		info += "\"grant_type\":\"authorization_code\",";
		info += "}";
		String tokenUrl ="http://10.1.0.187:8088/am/oauth2/access_token";
		String gettokenData = HttpClientUtil.doPost(tokenUrl,info,headerMap);
		net.sf.json.JSONObject j = net.sf.json.JSONObject.fromObject(gettokenData);
		String tokenData = j.get("access_token").toString();
		//请求失败，
		if (!j.get("code").toString().equals("200")) {
			bp.da.Log.DefaultLogWriteLine(LogType.Error, "访问令牌失败:" + j.toString());
			String msg = "该模块访问令牌失效，请联系管理员。";
			throw new RuntimeException(msg);
		}


		Date date =  new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
		String url = "http://10.1.0.187:8088/am/oauth2/tokeninfo?access_token="+tokenData+"&s="+dateFormat.format(date);
		String getData = HttpClientUtil.doGet(url);
		net.sf.json.JSONObject j2 = net.sf.json.JSONObject.fromObject(getData);
		String uid = j.get("uid").toString();
		String tokenId = j.get("tokenId").toString();
		String cn = j.get("cn").toString();
		//请求失败，
		if (!j.get("code").toString().equals("200")) {
			bp.da.Log.DefaultLogWriteLine(LogType.Error, "请求用户信息失败:" + j.toString());
			String msg = "该模块请求用户信息失败，请联系管理员。";
			throw new RuntimeException(msg);
		}

		Hashtable rs = new Hashtable();
		try {
			//验证用户信息

				Emp myEmp = new Emp();
				int i = myEmp.Retrieve("No", uid);
				if (i == 0)
					return "err@模块内部获取不到用户信息.";

				//设置他的组织，信息.
				WebUser.setNo(myEmp.getNo()); //登录帐号.
				WebUser.setName(myEmp.getName());//登录人名称
				WebUser.setDeptNo(myEmp.getDeptNo());//部门编号
				WebUser.setDeptName(myEmp.getDeptText());//部门名称
				//WebUser.setOrgNo(myEmp.getOrgNo());//组织编号

				//写入用户信息
				Dev2Interface.Port_Login(uid);
				//生成SID
				//String SID = DBAccess.GenerGUID();
				WFEmp wfEmp = new WFEmp();
				if (!wfEmp.IsExit("No", uid)) {
					wfEmp.setNo(myEmp.getNo());
					wfEmp.setName(myEmp.getName());
					wfEmp.setDeptNo(myEmp.getDeptNo());
					String sql = "UPDATE WF_Emp SET AtPara = REPLACE(AtPara, '@Token_PC=" + WebUser.getToken() + "', '@Token_PC=" + tokenId + "') WHERE No = '" +uid + "'";
					DBAccess.RunSQL(sql);
					wfEmp.DirectInsert();
				} else {
					wfEmp = new WFEmp(uid);
					String sql = "UPDATE WF_Emp SET AtPara = REPLACE(AtPara, '@Token_PC=" + WebUser.getToken() + "', '@Token_PC=" + tokenId + "') WHERE No = '" +uid + "'";
					DBAccess.RunSQL(sql);
					wfEmp.DirectUpdate();
				}
				rs.put("code", 200);
				rs.put("token", tokenId);
				rs.put("msg", "成功");

		} catch (Exception ex) {
			rs.put("code", 500);
			rs.put("token", 0);
			rs.put("msg", ex.getMessage());
		}
		return bp.tools.Json.ToJson(rs);
	}

	/**
	 * 待办
	 * @param domainExt 系统编号,为空时返回平台所有数据
	 * @param token
	 * @return
     * @throws Exception
	 */

    @RequestMapping(value = "/DB_Todolist")
	public String DB_Todolist(String domainExt,String token) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		DataTable dt = bp.wf.Dev2Interface.DB_GenerEmpWorksOfDataTable(WebUser.getNo(), 0, null, domainExt, null);
         return bp.tools.Json.ToJson(dt);
	}

	/**
	 * 获得在途
	 * @param token
	 * @return
	 * @throws Exception
	 */
    @RequestMapping(value = "/DB_Runing")
	public String DB_Runing(String token,String domainExt) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		 DataTable dt = bp.wf.Dev2Interface.DB_GenerRuning(WebUser.getNo(), false, domainExt);
         return bp.tools.Json.ToJson(dt);
	}

	/**
	 * 草稿
	 * @param token
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/DB_Draft")
	public String DB_Draft(String token,String domainExt) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		DataTable dt = bp.wf.Dev2Interface.DB_GenerDraftDataTable(null, domainExt);
		return bp.tools.Json.ToJson(dt);
	}
	/**
	 * 我可以发起的流程
	 * @param domainExt  系统编号,为空时返回平台所有数据
	 * @return 返回我可以发起的流程列表.
	 * @throws Exception
	 */

    @RequestMapping(value = "/DB_StarFlows")
	public String DB_StarFlows(String domainExt,String token) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		DataTable dt= bp.wf.Dev2Interface.DB_StarFlows(WebUser.getNo(),domainExt);
        return bp.tools.Json.ToJson(dt);
	}

	/**
	 * 打开的表单
	 * @param token
	 * @param workid
	 * @param flowNo
	 * @param nodeID
	 * @param fid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/GenerFrmUrl")
	public String GenerFrmUrl(String token, long workid,String flowNo,int nodeID,long fid) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		/*
		 * 发起的url需要在该流程的开始节点的表单方案中，使用SDK表单，并把表单的url设置到里面去.
		 * 设置步骤:
		 * 1. 打开流程设计器.
		 * 2. 在开始节点上右键，选择表单方案.
		 * 3. 选择SDK表单，把url配置到文本框里去.
		 * 比如: /App/F027QingJia.htm
		 */
		if (nodeID == 0)
			nodeID = Integer.parseInt(flowNo + "01");

		if (workid == 0)
			workid = Dev2Interface.Node_CreateBlankWork(flowNo, WebUser.getNo());

		String url = "";
		Node nd = new Node(nodeID);
		if (nd.getFormType() == NodeFormType.SDKForm || nd.getFormType() == NodeFormType.SelfForm)
		{
			//.
			url = nd.getFormUrl();
			if (url.contains("?") == true)
				url += "&FK_Flow=" + flowNo + "&FK_Node=" + nodeID + "&WorkID=" + workid + "&Token=" + token + "&UserNo=" +WebUser.getNo();
			else
				url += "?FK_Flow=" + flowNo + "&FK_Node=" + nodeID + "&WorkID=" + workid + "&Token=" + token + "&UserNo=" + WebUser.getNo();
		}
		else
		{
			url = "/WF/MyFlow.htm?FK_Flow=" + flowNo + "&FK_Node=" + nodeID + "&WorkID=" + workid+ "&Token=" + token;
		}
		return url;
	}

	/**
	 * 我发起的流程实例
	 * @param domainExt 统编号,为空时返回平台所有数据
	 * @return
	 * @throws Exception
	 */
		@RequestMapping(value = "/DB_MyStartFlowInstance")
	public String DB_MyStartFlowInstance(String domainExt,String token) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		String sql = "";
        if (StringUtils.isEmpty(domainExt))
            sql = "SELECT * FROM WF_GenerWorkFlow WHERE Starter='" + WebUser.getNo() + "'";
        else
            sql = "SELECT * FROM WF_GenerWorkFlow WHERE DomainExt='" + domainExt + "' AND Starter='" +  WebUser.getNo() + "'";

        DataTable dt = bp.da.DBAccess.RunSQLReturnTable(sql);
        return bp.tools.Json.ToJson(dt);
	}

/**
	 * 创建WorkID
	 * @param flowNo 流程编号
	 * @return 一个长整型的工作流程实例
	 * @throws Exception
	 */

    @RequestMapping(value = {"/CreateWorkID","/Node_CreateBlankWorkID"})
	public long CreateWorkID(String flowNo,String token) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		return bp.wf.Dev2Interface.Node_CreateBlankWork(flowNo, WebUser.getNo());
	}

	/**
	 * 执行发送
	 * @param flowNo 流的程模版ID
	 * @param workid 工作ID
	 * @param jsonString 参数，或者表单字段.
	 * @param toNodeID 到达的节点ID.如果让系统自动计算就传入0
	 * @param toEmps 到达的人员IDs,比如:zhangsan,lisi,wangwu. 如果为Null就标识让系统自动计算
	 * @param fid 子线程的主干流程ID.没有就传入0
	 * @param pworkid 子流程的父流程ID.没有就传入0
	 * @return 发送的结果信息.
	 * @throws Exception
	 */

    @RequestMapping(value = "/SendWork")
	public String SendWork(String flowNo, long workid, String jsonString, int toNodeID, String toEmps,String token,long fid,long pworkid) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);

		Hashtable ht = Json.JsonToHashtable("{"+jsonString+"}");
		//bp.wf.SendReturnObjs objs = bp.wf.Dev2Interface.Node_SendWork(flowNo, workid, ht, toNodeID, toEmps);
		bp.wf.SendReturnObjs objs = bp.wf.Dev2Interface.Node_SendWork(flowNo, workid, ht, null, toNodeID, toEmps, WebUser.getNo(), WebUser.getName() , WebUser.getDeptNo(), WebUser.getDeptName(), null, fid, pworkid);
        String msg = objs.ToMsgOfText();

        Hashtable myht = new Hashtable();
        myht.put("Message", msg);
        myht.put("IsStopFlow", objs.getIsStopFlow());

        if (objs.getIsStopFlow()==false)
        {
        	myht.put("VarAcceptersID", objs.getVarAcceptersID()==null?"":objs.getVarAcceptersID());
	        myht.put("VarAcceptersName", objs.getVarAcceptersName() == null ?"":objs.getVarAcceptersName());
	        myht.put("VarToNodeID", objs.getVarToNodeID());
	        myht.put("VarToNodeName", objs.getVarToNodeName()==null?"":objs.getVarToNodeName());
        }
        return bp.tools.Json.ToJson(myht);
	}

	/**
	 * 节点发送数据
	 * @param token
	 * @param flowNo 流程编号
	 * @param nodeID 当前节点ID
	 * @param workid 工作实例WorkID
	 * @param toNodeID 下一个节点
	 * @param toEmps 下个节点的接收人
	 * @param paras 表单的参数保存到WF_GenerWorkFlow,作为方向条件等使用
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Node_SendWork")
	public String Node_SendWork(String token, String flowNo, int nodeID,long workid,int toNodeID,String toEmps,String paras) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		if(DataType.IsNullOrEmpty(paras)==false)
			Dev2Interface.Flow_SaveParas(workid, paras);
		//执行发送.
		Hashtable ht = new Hashtable();
		ht.put("FK_Flow", flowNo);
		ht.put("FK_NodeID", nodeID);
		ht.put("WorkID", workid);
		//执行发送.
		SendReturnObjs objs = Dev2Interface.Node_SendWork(flowNo, workid, ht, null, toNodeID, toEmps);
		String msg = objs.ToMsgOfText();
		return msg;

	}
    @RequestMapping(value = "/SendWorkZHZG")
	public String SendWorkZHZG(long workid, int toNodeID, String toEmps,String token) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		String sql="select FK_Flow from wf_generworkflow where workid="+workid;
		String flowNo=bp.da.DBAccess.RunSQLReturnString(sql);
		bp.wf.SendReturnObjs objs = bp.wf.Dev2Interface.Node_SendWork(flowNo, workid, null, toNodeID, toEmps);

        String msg = objs.ToMsgOfText();

        Hashtable myht = new Hashtable();
        myht.put("Message", msg);
        myht.put("IsStopFlow", objs.getIsStopFlow());
		Hashtable xy = new Hashtable();
        if (objs.getIsStopFlow()==false)
        {
        	myht.put("VarAcceptersID", objs.getVarAcceptersID()==null?"":objs.getVarAcceptersID());
	        myht.put("VarAcceptersName", objs.getVarAcceptersName() == null ?"":objs.getVarAcceptersName());
	        myht.put("VarToNodeID", objs.getVarToNodeID());
	        myht.put("VarToNodeName", objs.getVarToNodeName()==null?"":objs.getVarToNodeName());
        }
        //返回待办信息
		GenerWorkFlow gwf=new GenerWorkFlow(workid);
		String	sql2 = "SELECT workid,fid,fk_node,fk_emp FROM WF_EmpWorks WHERE workid='" + workid + "' or fid='" + workid + "' or (workid='" + gwf.getFID() + "' and fid = 0)" ;

		DataTable dt = bp.da.DBAccess.RunSQLReturnTable(sql2);
		myht.put("VarToDoList", dt.Rows);
        return bp.tools.Json.ToJson(myht);
	}

	/**
	 * 保存参数
	 * @param workid 工作ID
	 * @param paras 用于控制流程运转的参数，比如方向条件. 格式为:@JinE=1000@QingJaiTianShu=100
	 * @throws Exception
	 */

    @RequestMapping(value = "/SaveParas")
	public void SaveParas(long workid, String paras,String token) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		bp.wf.Dev2Interface.Flow_SaveParas(workid, paras);

	}

	/**
	 * 获得下一个节点信息
	 * @param flowNo 流程编号
	 * @param workid 流程实例
	 * @param paras 方向条件所需要的参数，可以为空。
	 * @return 下一个节点的JSON.
	 * @throws Exception
	 */

    @RequestMapping(value = "/GenerNextStepNode")
	public String GenerNextStepNode(String flowNo, long workid, String paras, String token) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		if (paras != null)
            bp.wf.Dev2Interface.Flow_SaveParas(workid, paras);

        int nodeID = bp.wf.Dev2Interface.Node_GetNextStepNode(flowNo, workid);
        bp.wf.Node nd = new bp.wf.Node(nodeID);

       //如果字段 DeliveryWay = 4 就表示到达的接点是由当前节点发送人选择接收人.
		//自定义参数的字段是 SelfParas, DeliveryWay
		// CondModel = 方向条件计算规则.
        return bp.tools.Json.ToJson(nd.ToDataTableField());
	}

	/**
	 * 获得下一步节点的接收人
	 * @param flowNo 流程ID
	 * @param toNodeID 节点ID
	 * @param workid 工作事例ID
	 * @return 返回两个结果集一个是分组的Depts(No,Name)，另外一个是人员的Emps(No, Name, FK_Dept),接受后，用于构造人员选择器.
	 * @throws Exception
	 */

    @RequestMapping(value = "/GenerNextStepNodeEmps")
	public String GenerNextStepNodeEmps(String flowNo, int toNodeID, int workid, String token) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		Selector select = new Selector(toNodeID);
        Node nd = new Node(toNodeID);

        GERpt rpt = new GERpt("ND" + Integer.parseInt(flowNo) + "Rpt", workid);
        DataSet ds = select.GenerDataSet(toNodeID, rpt);
        return bp.tools.Json.ToJson(ds);
	}

	/**
	 * 将要退回到的节点
	 * @param workid
	 * @return 返回节点集合的json.
	 * @throws Exception
	 */

    @RequestMapping(value = {"/WillReturnToNodes","/DB_GenerWillReturnNodes"})
	public String WillReturnToNodes(long workid,String token) throws Exception {

		try
		{

		bp.wf.Dev2Interface.Port_LoginByToken(token);

		GenerWorkFlow gwf=new GenerWorkFlow(workid);

		DataTable dt=bp.wf.Dev2Interface.DB_GenerWillReturnNodes(workid);
        return bp.tools.Json.ToJson(dt);
		}catch(Exception ex)
		{
		  return "err@"+ex.getMessage();
		}
	}

	/**
	 * 将要达到的节点
	 * @param currNodeID 当前节点ID
	 * @return 返回节点集合的json.
	 * @throws Exception
	 */

    @RequestMapping(value = "/WillToNodes")
	public String WillToNodes(int currNodeID,String token) throws Exception {

		try
		{
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		Node nd = new Node(currNodeID);

        Directions dirs = new Directions();
        Nodes nds = dirs.GetHisToNodes(currNodeID, false);
        return bp.tools.Json.ToJson(nds.ToDataTableField());
		}catch(Exception ex)
		{
		  return "err@"+ex.getMessage();
		}
	}

	/**
	 * 获得当前节点信息.
	 * @param currNodeID  当前节点ID
	 * @return
	 * @throws Exception
	 */
    @RequestMapping(value = "/CurrNodeInfo")
	public String CurrNodeInfo(int currNodeID,String token) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		Node nd = new Node(currNodeID);
         return bp.tools.Json.ToJson(nd.ToDataTableField());
	}

	/**
	 * 获得当前流程信息.
	 * @param flowNo 流程ID
	 * @return 当前节点信息
	 * @throws Exception
	 */
    @RequestMapping(value = "/CurrFlowInfo")
	public String CurrFlowInfo(String flowNo,String token) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		Flow fl = new Flow(flowNo);
          return fl.ToJson();
	}

	/**
	 * 获得当前流程信息.
	 * @param workID 流程ID
	 * @return 当前节点信息
	 * @throws Exception
	 */
    @RequestMapping(value = "/CurrGenerWorkFlowInfo")
	public String CurrGenerWorkFlowInfo(long workID, String token) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
         return gwf.ToJson();
	}

	/**
	 * 退回.
	 * @param workid 流程ID
	 * @param returnToNodeID 流程退回的节点ID
	 * @param returnMsg 退回原因
	 * @return 退回结果信息
	 * @throws Exception
	 */
    @RequestMapping(value = "/Node_ReturnWork")
	   public String Node_ReturnWork(long workid, int returnToNodeID, String returnMsg, String token) throws Exception {
	  bp.wf.Dev2Interface.Port_LoginByToken(token);
	  GenerWorkFlow gwf=new GenerWorkFlow(workid);
	  try{
		  return bp.wf.Dev2Interface.Node_ReturnWork(gwf.getFlowNo(), workid, gwf.getFID(), gwf.getNodeID(), returnToNodeID, returnMsg,false);
	  }catch(Exception ex){
		  return "err@"+ex.getMessage();
	  }
   }

	/**
	 * 执行流程结束 说明:强制流程结束.
	 * @param workID 工作ID
	 * @param msg 流程结束原因
	 * @return 返回成功执行信息
	 * @throws Exception
	 */
    @RequestMapping(value = "/Flow_DoFlowOverQiangZhi")
	public  String Flow_DoFlowOverQiangZhi(long workID, String msg, String token) throws Exception {
	  bp.wf.Dev2Interface.Port_LoginByToken(token);

	  return Dev2Interface.Flow_DoFlowOver( workID, msg,WFState.CompleteByNode);
  }


	/**
	 * 登录接口，返回默认页面，单组织返回default页面，集团模式返回default或者selectOrg页面
	 * @param userNo
	 * @return
	 * @throws Exception
	 */
   /* @RequestMapping(value = "/Port_Login_Default")
	public String Port_Login_Default(String userNo) throws Exception{

		//获得当前管理员管理的组织数量.
		OrgAdminers adminers = null;

		bp.port.Emp emp = new bp.port.Emp();
		emp.setUserID(userNo);
		if (emp.RetrieveFromDBSources() == 0)
		{
			return "err@用户名错误.";
		}

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			//调用登录方法.
			Dev2Interface.Port_Login(emp.getUserID());
			if (DBAccess.IsExitsTableCol("Port_Emp", "EmpSta") == true)
			{
				String sql = "SELECT EmpSta FROM Port_Emp WHERE No='" + emp.getNo() + "'";
				if (DBAccess.RunSQLReturnValInt(sql, 1) == 1)
				{
					return "err@该用户已经被禁用.";
				}
			}
			return "url@Default.htm?Token=" + Dev2Interface.Port_GenerToken("PC") + "&UserNo=" + emp.getUserID();
		}
		//查询他管理多少组织.
		adminers = new OrgAdminers();
		adminers.Retrieve(OrgAdminerAttr.FK_Emp, userNo, null);
		if (adminers.size() == 0)
		{
			Orgs orgs = new Orgs();
			int i = orgs.Retrieve("Adminer", userNo, null);
			if (i == 0)
			{
				//调用登录方法.
				Dev2Interface.Port_Login(emp.getUserID(), emp.getOrgNo());
				return "url@Default.htm?Token=" + Dev2Interface.Port_GenerToken("PC") + "&UserNo=" + userNo + "&OrgNo=";
			}

			for (Org org : orgs.ToJavaList())
			{
				OrgAdminer oa = new OrgAdminer();
				oa.setFK_Emp(WebUser.getNo());
				oa.setOrgNo(org.getNo());
				oa.Save();
			}
			adminers.Retrieve(OrgAdminerAttr.FK_Emp, emp.getUserID(), null);
		}

		//设置他的组织，信息.
		WebUser.setNo(emp.getUserID()); //登录帐号.
		WebUser.setFK_Dept(emp.getFK_Dept());
		WebUser.setFK_DeptName(emp.getFK_DeptText());

		//执行登录.
		bp.wf.Dev2Interface.Port_Login(emp.getUserID(), null, emp.getOrgNo());

		//设置SID.
		WebUser.setSID(DBAccess.GenerGUID()); //设置SID.
		emp.setSID(WebUser.getSID()); //设置SID.
		bp.wf.Dev2Interface.Port_SetSID(emp.getUserID(), WebUser.getSID());

		//执行更新到用户表信息.
		// WebUser.UpdateSIDAndOrgNoSQL();
		//判断是否是多个组织的情况.
		if (adminers.size() == 1)
		{
			return "url@Portal/Standard/Default.htm?SID=" + emp.getSID() + "&UserNo=" + emp.getUserID() + "&OrgNo=" + emp.getOrgNo();
		}

		return "url@Portal/Standard/SelectOneOrg.htm?SID=" + emp.getSID() + "&UserNo=" + emp.getUserID() + "&OrgNo=" + emp.getOrgNo();

	}
	*/
	/**
	 * 执行撤销
	 * @param flowNo 流程编码
	 * @param workID 工作ID
	 * @param unSendToNode 撤销到的节点
	 * @param fid
	 * @return
	 * @throws Exception
	 */
    @RequestMapping(value = "/Runing_UnSend")
		public String Runing_UnSend(String token,String flowNo, long workID, int unSendToNode,long fid) throws Exception{

		bp.wf.Dev2Interface.Port_LoginByToken(token);

		return bp.wf.Dev2Interface.Flow_DoUnSend(flowNo, workID,unSendToNode,fid);
	}

	/**
	 * 流程结束后回滚
	 * @param flowNo 流程编码
	 * @param workId 工作ID
	 * @param backToNodeID 回滚到的节点ID
	 * @param backMsg 回滚原因
	 * @return 回滚信息
	 * @throws Exception
	 */
    @RequestMapping(value = "/DoRebackFlowData")
	public String DoRebackFlowData(String flowNo,long workId,int backToNodeID,String backMsg, String token) throws Exception{
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		FlowExt flow = new FlowExt(flowNo);
		return flow.DoRebackFlowData(workId, backToNodeID, backMsg);
	}
	/**
	 获得工作进度-用于展示流程的进度图
	 @param workID workID
	 @param token 用户token
	 @return 返回待办
	*/
    @RequestMapping(value = "/WorkProgressBar")
    public String WorkProgressBar(long  workID, String token) throws Exception
    {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
        DataSet ds = bp.wf.Dev2Interface.DB_JobSchedule(workID);
        return bp.tools.Json.ToJson(ds);
    }

    /**
	 查询数据
	 @param sqlOfSelect 要查询的sql
	 @param password 用户密码
	 @return 返回查询数据
     * @throws Exception
	*/
    @RequestMapping(value = "/DB_RunSQLReturnJSON")
    public String DB_RunSQLReturnJSON(String sqlOfSelect, String password) throws Exception
    {
        if ( password.equals(password) == false)
            return "err@密码错误";

        DataTable dt = bp.da.DBAccess.RunSQLReturnTable(sqlOfSelect);
        return bp.tools.Json.ToJson(dt);
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
    @RequestMapping(value = "/Node_CC_WriteTo_CClist")
	public String Node_CC_WriteTo_CClist(int fk_node, long workID, String toEmpNo, String toEmpName,
			String msgTitle, String msgDoc,String token) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		return bp.wf.Dev2Interface.Node_CC_WriteTo_CClist(fk_node, workID,toEmpNo,toEmpName,msgTitle,msgDoc);
	}
	/**
	 * 是否可以查看该流程
	 * @param flowNo 流程编号
	 * @param workid 工作ID
	 * @return 是否可以查看该工作.
	 * @throws Exception
	 */
    @RequestMapping(value = "/Flow_IsCanView")
    public Boolean Flow_IsCanView(String flowNo, long workid, String userNo) throws Exception
    {
        return bp.wf.Dev2Interface.Flow_IsCanViewTruck(flowNo, workid,userNo);
    }

    /**
	 是否可以处理该流程
	 @param userNo 要查询的 sql
	 @param workid 用户密码
	 @return 是否可以查看该工作.
* @throws Exception
	*/
    @RequestMapping(value = "/Flow_IsCanDoCurrentWork")
    public Boolean Flow_IsCanDoCurrentWork(long workid, String userNo) throws Exception
    {
        return bp.wf.Dev2Interface.Flow_IsCanDoCurrentWork(workid, userNo);
    }

	/**
	 * 获取指定人员的抄送列表 说明:可以根据这个列表生成指定用户的抄送数据.
	 *
	 * @param userNo
	 *            人员编号,如果是null,则返回所有的.
	 * @return 返回该人员的所有抄送列表,结构同表WF_CCList.
	 */
    @RequestMapping(value = "/DB_CCList")
	public String DB_CCList(String userNo,String token) throws Exception{
		bp.wf.Dev2Interface.Port_LoginByToken(token);

		DataTable dt = bp.wf.Dev2Interface.DB_CCList(userNo);
		return bp.tools.Json.ToJson(dt);
	}


	// 根据当前节点获得下一个节点.
    @RequestMapping(value = "/GetNextNodeID")
	public int GetNextNodeID(int nodeID, DataTable dirs)
    {
        int toNodeID = 0;
        for (DataRow dir : dirs.Rows)
        {
            if ( Integer.parseInt(dir.getValue("Node").toString()) == nodeID)
            {
                toNodeID = Integer.parseInt( dir.getValue("ToNode").toString());
                break;
            }
        }

        int toNodeID2 = 0;

        for (DataRow dir11 : dirs.Rows)
        {
            if (Integer.parseInt(dir11.getValue("Node").toString()) ==nodeID )
            {
                toNodeID2 = Integer.parseInt(dir11.getValue("ToNode").toString());
            }
        }

        //两次去的不一致，就有分支，有分支就reutrn 0 .
        if (toNodeID2 == toNodeID)
            return toNodeID;
        return  0 ;
    }

    /**
    写入审核信息

<param name="workid">workID</param>
<param name="msg">审核信息</param>
*
*/
    @RequestMapping(value = "/Node_WriteWorkCheck")
   public void Node_WriteWorkCheck(long workid, String msg) throws Exception
   {
        GenerWorkFlow gwf = new GenerWorkFlow(workid);
        bp.wf.Dev2Interface.Node_WriteWorkCheck( gwf.getWorkID(), msg,"",null);
   }
    /// 关于组织结构的接口.
	/**
	 登录成功后返回的token.

	 @param userNo
	 @param password
	 @param orgNo
	 @return
	 */
	@RequestMapping(value = "/Port_Login")
	public final String Port_Login(String userNo, String password, String orgNo) throws Exception {
		bp.port.Emp emp = new bp.port.Emp();
		emp.setNo(userNo);
		if (emp.RetrieveFromDBSources() == 0)
		{
			return "err@密码或者用户名错误.";
		}

		if (emp.CheckPass(password) == false)
		{
			return "err@密码或者用户名错误.";
		}

		if (DataType.IsNullOrEmpty(orgNo) == true)
		{
			orgNo = emp.getOrgNo();
		}

		String token = bp.wf.Dev2Interface.Port_GenerToken();
		//执行登录，返回token.
		bp.wf.Dev2Interface.Port_Login(userNo,orgNo);
		return token;
	}


	///关于组织的接口.
	@RequestMapping(value="/Flow_DoPress")
	public String Flow_DoPress(String token, String workids,String msg) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		if (DataType.IsNullOrEmpty(workids))
			return "err@执行批量催办的WorkIDs不能为空";
		String[] strs = workids.split(",");

		if (msg == null)
			msg = "需要您处理待办工作.";
		try
		{
			String info = "";
			for(String workidStr : strs)
			{
				if (bp.da.DataType.IsNullOrEmpty(workidStr) == true)
					continue;

				info += "@" + Dev2Interface.Flow_DoPress(Integer.parseInt(workidStr), msg, true);
			}
			return info;
		}
		catch (Exception ex)
		{
			return "err@"+ex.getMessage();
		}

	}
	/// <summary>
	/// 批量审核
	/// </summary>
	/// <param name="token"></param>
	/// <param name="workids"></param>
	/// <returns></returns>
	@RequestMapping(value="/CC_BatchCheckOver")
	public String CC_BatchCheckOver(String token, String workids) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		if (DataType.IsNullOrEmpty(workids))
			return "err@执行批量审批的WorkIDs不能为空";

		try
		{
			return Dev2Interface.Node_CC_SetCheckOverBatch(workids);
		}
		catch (Exception ex)
		{
			return "err@"+ex.getMessage();
		}

	}
	/// <summary>
	/// 批量删除流程
	/// </summary>
	/// <param name="token"></param>
	/// <param name="workids"></param>
	/// <returns></returns>
	@RequestMapping(value="/Flow_BatchDeleteByFlag")
	public String Flow_BatchDeleteByFlag(String token, String workids) throws Exception {
		//根据token登录
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		if (DataType.IsNullOrEmpty(workids))
			return "err@批量删除的WorkIDs不能为空";
		try
		{
			String[] strs = workids.split(",");
			for(String workidStr : strs)
			{
				if (bp.da.DataType.IsNullOrEmpty(workidStr) == true)
					continue;

				String st1r = Dev2Interface.Flow_DoDeleteFlowByFlag(Long.parseLong(workidStr), "删除", true);
			}
			return "删除成功";
		}
		catch (Exception ex)
		{
			return "err@"+ex.getMessage();
		}

	}
	/// <summary>
	/// 批量删除
	/// </summary>
	/// <param name="token"></param>
	/// <param name="workids">工作实例WorkIDs</param>
	/// <param name="isDeleteSubFlows">是否删除子流程</param>
	/// <returns></returns>
	@RequestMapping(value="/Flow_BatchDeleteByReal")
	public String Flow_BatchDeleteByReal(String token, String workids,int isDeleteSubFlows) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		if (DataType.IsNullOrEmpty(workids))
			return "err@批量删除的WorkIDs不能为空";
		try
		{
			String[] strs = workids.split(",");

			for(String workidStr : strs)
			{
				if (bp.da.DataType.IsNullOrEmpty(workidStr) == true)
					continue;

				String st1r = Dev2Interface.Flow_DoDeleteFlowByReal(Long.parseLong(workidStr), isDeleteSubFlows == 1 ? true : false);
			}
			return  "删除成功";
		}
		catch (Exception ex)
		{
			return "err@"+ex.getMessage();
		}

	}
	/// <summary>
	/// 批量恢复逻辑删除的流程
	/// </summary>
	/// <param name="token"></param>
	/// <param name="workids"></param>
	/// <returns></returns>
	@RequestMapping(value="/Flow_BatchDeleteByFlagAndUnDone")
	public String Flow_BatchDeleteByFlagAndUnDone(String token, String workids) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		if (DataType.IsNullOrEmpty(workids))
			return "err@批量撤销删除的WorkIDs不能为空";
		try
		{
			String[] strs = workids.split(",");

			for(String workidStr : strs)
			{
				if (bp.da.DataType.IsNullOrEmpty(workidStr) == true)
					continue;

				Dev2Interface.Flow_DoUnDeleteFlowByFlag(null, Long.parseLong(workidStr), "删除");
			}
			return "撤销删除成功";
		}
		catch (Exception ex)
		{
			return "err@"+ex.getMessage();
		}

	}
	/// <summary>
	/// 批量撤回
	/// </summary>
	/// <param name="token"></param>
	/// <param name="workids"></param>
	/// <returns></returns>
	@RequestMapping(value="/Flow_DoUnSend")
	public String Flow_DoUnSend(String token, String workids) throws Exception {
		//根据token登录
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		if (DataType.IsNullOrEmpty(workids))
			return "err@批量撤销的WorkIDs不能为空";
		try
		{
			String info = "";
			String[] strs = workids.split(",");
			for(String workidStr : strs)
			{
				if (bp.da.DataType.IsNullOrEmpty(workidStr) == true)
					continue;

				info += Dev2Interface.Flow_DoUnSend(null, Long.parseLong(workidStr), 0, 0);
			}
			return info;
		}
		catch (Exception ex)
		{
			return "err@"+ex.getMessage();
		}

	}
	/// <summary>
	/// 批量删除草稿
	/// </summary>
	/// <param name="token"></param>
	/// <param name="workids"></param>
	/// <returns></returns>
	@RequestMapping(value="/Flow_DeleteDraft")
	public String Flow_DeleteDraft(String token, String workids) throws Exception {
		//根据token登录
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		if (DataType.IsNullOrEmpty(workids))
			return "err@批量删除草稿的WorkIDs不能为空";
		try
		{
			String[] strs = workids.split(",");
			for(String workidStr : strs)
			{
				if (bp.da.DataType.IsNullOrEmpty(workidStr) == true)
					continue;

				Dev2Interface.Node_DeleteDraft(Long.parseLong(workidStr));
			}
			return "删除成功";
		}
		catch (Exception ex)
		{
			return "err@"+ex.getMessage();
		}

	}
	/// <summary>
	/// 批量结束流程
	/// </summary>
	/// <param name="token"></param>
	/// <param name="workids"></param>
	/// <returns></returns>
	@RequestMapping(value="/Flow_DoFlowOver")
	public String Flow_DoFlowOver(String token, String workids) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		if (DataType.IsNullOrEmpty(workids))
			return "err@批量结束流程的WorkIDs不能为空";

		String[] strs = workids.split(",");
		try
		{
			for(String workidStr : strs)
			{
				if (bp.da.DataType.IsNullOrEmpty(workidStr) == true)
					continue;

				Dev2Interface.Flow_DoFlowOver(Long.parseLong(workidStr), "批量结束", WFState.CompleteEnd);
			}

			return "执行成功";
		}
		catch (Exception ex)
		{
			return "err@"+ex.getMessage();
		}

	}
	/// <summary>
	/// 查询
	/// </summary>
	/// <param name="key">关键字</param>
	/// <param name="dtFrom">日期从</param>
	/// <param name="dtTo">日期到</param>
	/// <param name="scop">范围</param>
	/// <param name="pageIdx">分页</param>
	/// <returns></returns>
     @RequestMapping(value="/Search_Init")
	public String Search_Init(String token,String key,String dtFrom,String dtTo,String scop,int pageIdx) throws Exception {
		bp.wf.Dev2Interface.Port_LoginByToken(token);
		try
		{
			GenerWorkFlows gwfs = new GenerWorkFlows();
			//创建查询对象.
			QueryObject qo = new QueryObject(gwfs);
			if (DataType.IsNullOrEmpty(key) == false)
			{
				qo.AddWhere(GenerWorkFlowAttr.Title, " LIKE ", "%" + key + "%");
				qo.addAnd();
			}

			//我参与的.
			if (scop.equals("0") == true)
				qo.AddWhere(GenerWorkFlowAttr.Emps, "LIKE", "%@" + WebUser.getNo() + ",%");

			//我发起的.
			if (scop.equals("1") == true)
				qo.AddWhere(GenerWorkFlowAttr.Starter, "=",  WebUser.getNo());

			//我部门发起的.
			if (scop.equals("2") == true)
				qo.AddWhere(GenerWorkFlowAttr.FK_Dept, "=", WebUser.getDeptNo());


			//任何一个为空.
			if (DataType.IsNullOrEmpty(dtFrom) == true || DataType.IsNullOrEmpty(dtTo) == true)
			{

			}
			else
			{
				qo.addAnd();
				qo.AddWhere(GenerWorkFlowAttr.RDT, ">=", dtFrom);
				qo.addAnd();
				qo.AddWhere(GenerWorkFlowAttr.RDT, "<=", dtTo);
			}

			int count = qo.GetCount(); //获得总数.

			qo.DoQuery("WorkID", 20, pageIdx);


			DataTable dt = gwfs.ToDataTableField("gwls");

			//创建容器.
			DataSet ds = new DataSet();
			ds.Tables.add(dt); //增加查询对象.

			//增加数量.
			DataTable mydt = new DataTable();
			mydt.TableName = "count";
			mydt.Columns.Add("CC");
			DataRow dr = mydt.NewRow();
			dr.setValue(0,String.valueOf(count)); //把数量加进去.
			mydt.Rows.add(dr);
			ds.Tables.add(mydt);

			return bp.tools.Json.ToJson(ds);

		}
		catch (Exception ex)
		{
			return "err@"+ex.getMessage();
		}

	}

	@RequestMapping(value = "/Portal_Login",method={RequestMethod.GET,RequestMethod.POST})
	public String Portal_Login(String privateKey,String userNo) throws Exception {
		if (DataType.IsNullOrEmpty(userNo) == true)
			return "err@账号不能为空";
		String localKey = bp.difference.SystemConfig.GetValByKey("PrivateKey", "");
		if (DataType.IsNullOrEmpty(localKey) == true)
			localKey = "DiGuaDiGua,IamCCBPM";
		if (localKey.equals(privateKey) == false)
		{
			return  "err@私约错误，请检查全局文件中配置 PrivateKey";
		}

		//执行本地登录.
		bp.wf.Dev2Interface.Port_Login(userNo);
		String token = bp.wf.Dev2Interface.Port_GenerToken();
		return token;
	}
}
