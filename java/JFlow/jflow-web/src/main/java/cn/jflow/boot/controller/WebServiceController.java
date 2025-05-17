package cn.jflow.boot.controller;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.port.EmpAttr;
import bp.port.WebUserCopy;
import bp.tools.Encodes;
import bp.tools.HttpClientUtil;
import bp.tools.MD5Utill;
import bp.wf.*;
import bp.wf.template.*;
import bp.port.Emp;
import bp.web.WebUser;
import bp.wf.port.WFEmp;


import com.alibaba.fastjson.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/BPM")
public class WebServiceController {

    /**
     * 计算未来流程运行路线
     * @param param
     * @return
     * @throws Exception
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/GetCondNodes")
    public List<Hashtable> GetPreplaceWokerFreeNodes(@RequestBody Map<String,String> param) throws Exception{

        String FlowNo = param.get("FlowNo");
        long WorkID = Long.parseLong(param.get("WorkID"));
        int NodeID=Integer.parseInt(param.get("NodeID"));
        Node node =new Node(NodeID);
        List<Hashtable> list=new ArrayList<>();
        Hashtable ht=new Hashtable();
        ht.put("NodeId", node.getNodeID());
        ht.put("NodeName",node.getName());
        ht.put("DeliveryWay","none");
        list.add(ht);
        list=NodeSend_GenerNextStepNode_Ext(node,list,FlowNo,WorkID);
        return list;
    }
    public List<Hashtable> NodeSend_GenerNextStepNode_Ext(Node node,List<Hashtable> list,
                                                          String FlowNo,long WorkID) throws Exception{
        Directions dirs = new Directions();
        dirs.Retrieve(DirectionAttr.Node, node.getNodeID(), "Idx");
        if(dirs.size()==1){
            Direction d=(Direction)dirs.get(0);
            Node nd=new Node(d.getToNode());
            Hashtable ht=new Hashtable();
            ht.put("NodeId", nd.getNodeID());
            ht.put("NodeName",nd.getName());
            if(nd.getHisDeliveryWay()==DeliveryWay.PreplaceWokerFree){
                ht.put("DeliveryWay",DeliveryWay.PreplaceWokerFree);
            }
            else{
                ht.put("DeliveryWay","none");
            }
            list.add(ht); //把他加入到里面.
            return list=NodeSend_GenerNextStepNode_Ext(nd,list,FlowNo,WorkID);
        }
        else{
            for (Direction dir: dirs.ToJavaList()){
                //查询出来他的条件.
                Conds conds = new Conds();
                conds.Retrieve(CondAttr.FK_Node, node.getNodeID(), CondAttr.ToNodeID, dir.getToNode(), CondAttr.CondType, CondType.Dir.getValue(), CondAttr.Idx);

                if(conds.size()==0)
                    continue;
                //可以到达的节点.
                if (conds.size() == 1&&node.getHisDeliveryWay()==DeliveryWay.PreplaceWokerFree)
                {
                    Node nd=new Node(dir.getToNode());
                    Hashtable ht=new Hashtable();
                    ht.put("NodeId",dir.getNode());
                    ht.put("NodeName",nd.getName());
                    if(nd.getHisDeliveryWay()==DeliveryWay.PreplaceWokerFree){
                        ht.put("DeliveryWay",DeliveryWay.PreplaceWokerFree);
                    }
                    else{
                        ht.put("DeliveryWay","none");
                    }
                    list.add(ht); //把他加入到里面.
                    continue;
                }

                WebUserCopy webUserCopy=new WebUserCopy();
                webUserCopy.setNo(WebUser.getNo());
                webUserCopy.setName(WebUser.getName());
                webUserCopy.setDeptNo(WebUser.getDeptNo());

                GERpt geRpt=new GERpt("ND"+Integer.parseInt(FlowNo)+"Rpt",WorkID);
                //按条件计算.
                if (conds.GenerResult(geRpt, webUserCopy,node) == true)
                {
                    node = new Node(dir.getToNode());
                    if (node.getHisNodeType() == NodeType.UserNode)
                    {
                        Node nd=new Node(dir.getToNode());
                        Hashtable ht=new Hashtable();
                        ht.put("NodeId",nd.getNodeID());
                        ht.put("NodeName",nd.getName());
                        if(nd.getHisDeliveryWay()==DeliveryWay.PreplaceWokerFree){
                            ht.put("DeliveryWay",DeliveryWay.PreplaceWokerFree);
                        }
                        else{
                            ht.put("DeliveryWay","none");
                        }
                        list.add(ht); //把他加入到里面.
                        return list=NodeSend_GenerNextStepNode_Ext(nd,list,FlowNo,WorkID);
                    }
                }
            }
        }
        return list;
    }
    /**
     * 系统登录
     * @return token
     * @throws Exception
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/SSOLogin")
    public JSONObject SSOLogin(@RequestBody Map<String,String> param)throws Exception{
        JSONObject res = new JSONObject();
        //帐号
        String userNo = param.get("userNo");
        //时间戳
        Long timestamp=Long.parseLong(param.get("timestamp"));
        //当前时间
        Long timesNow=System.currentTimeMillis();
        //获取当前分钟数
        Long mins=(timesNow-timestamp)/(1000*60);
        if(mins>5){
            res.put("code", 500);
            res.put("token",0);
            res.put("message","请求失效.");
            return res;
        }
        //签名
        String sign=param.get("sign");
        String userID="";
        String signStr=userNo+"_"+timestamp;
        String signMD5= Encodes.encodeBase64(signStr);
        //签名判断
        if(!signMD5.equals(sign)){
            res.put("code", 500);
            res.put("token",0);
            res.put("message","签名验证失败.");
            return res;
        }

        try{

            //验证用户信息
            Emp emps=new Emp();
            if(!emps.IsExit(EmpAttr.No,userNo))
            {
                res.put("code", 500);
                res.put("token",0);
                return res;
            }
            else{
                //写入用户信息
                bp.wf.Dev2Interface.Port_Login(userNo);

                res.put("code", 200);
                if(DataType.IsNullOrEmpty(WebUser.getToken())){
                    WFEmp wfEmp=new WFEmp(userNo);
                    if(DataType.IsNullOrEmpty(wfEmp.getToken())){
                        String wfTkoen= DBAccess.GenerGUID();
                        wfEmp.setToken(wfTkoen);
                        wfEmp.Update();
                        res.put("token", wfTkoen);
                    }
                    else
                        res.put("token", wfEmp.getToken());
                }
                else
                    res.put("token", WebUser.getToken());
                res.put("no",WebUser.getNo());
                res.put("fk_dept",WebUser.getDeptNo());
                res.put("name",WebUser.getName());
                res.put("fk_depName",WebUser.getDeptName());
                res.put("SID",WebUser.getSID());
                res.put("orgNo",WebUser.getOrgNo());
            }
        }
        catch(Exception ex){
            res.put("code", 500);
            res.put("token",0);
        }
        return res;
    }
    /**
     * 保存嵌入式表单数据接口
     * usrNo 发起人编号
     * flowNo 流程编号
     * workID 主键
     * maintable  填充表单
     * dtl 从表
     * @return
     */
    @PostMapping("/saveSelfWork")
    public JSONObject saveSelfWork(@RequestBody JSONObject param) throws Exception{
        JSONObject res = new JSONObject();
        //执行发送人员的帐号
        String userNo = param.get("userNo").toString();
        //要保存的流程编号
        String flowNo = param.get("flowNo").toString();
        //流程实例主键
        long workID=Long.parseLong(param.get("workID").toString());
        //主表单数据
        String mainTable = param.get("mainTable").toString();
        //从表数据
        JSONArray dtlsJSON = param.getJSONArray("dtls");
        if(workID==0){
            res.put("code",500);
            res.put("message","workID参数不能为0，请检查参数是否传递正确。");
            return res;
        }

        Hashtable<Object, Object> dataTable = new Hashtable();
        DataSet ds=new DataSet();
        DataTable dt = null;
        try{
            //写入用户信息
            bp.wf.Dev2Interface.Port_Login(userNo);
            GenerWorkFlow gwf=new GenerWorkFlow(workID);
            //表单填充
            if (StringUtils.isNotBlank(mainTable)){
                Map<String, Object> formMap = parseJSON2Map(JSONObject.fromObject(mainTable));
                formMap.forEach((key, value) -> dataTable.put(key,value));
            }
            //从表填充
            //可能有多个从表数据
            for(int i=0;i<dtlsJSON.size();i++){
                JSONObject dtlDatas = (JSONObject) dtlsJSON.get(i);
                //从表编号
                String dtlNo=dtlDatas.get("dtlNo").toString();
                //从表数据
                String dtlArryData=dtlDatas.get("dtl").toString();
                //生成table
                DataTable dtlDt=bp.tools.Json.ToDataTable(dtlArryData);
                dtlDt.TableName=dtlNo;
                ds.Tables.add(dtlDt);
            }

            bp.wf.Dev2Interface.Node_SaveWork(workID,dataTable,ds);
            res.put("code",200);
            res.put("message","保存成功。");
        }
        catch (Exception ex){
            res.put("code",500);
            res.put("message",ex.getMessage());
        }
        return res;
    }
    /**
     * 发送嵌入式表单数据接口
     * usrNo 发起人编号
     * flowNo 流程编号
     * workID 主键
     * maintable  填充表单
     * dtl 从表
     * @return
     */
    @PostMapping("/workSend")
    public JSONObject workSend(@RequestBody JSONObject param) throws Exception{
        //执行发送人员的帐号
        String userNo = param.get("userNo").toString();

        //要保存的流程编号
        String flowNo = param.get("flowNo").toString();
        //流程实例主键
        long workID=Long.parseLong(param.get("workID").toString());
        //下一步节点编号
        int toNode=Integer.parseInt(param.get("toNode").toString());
        //下一步接收人
        String toEmps=param.get("toEmps").toString();
        //主表单数据
        String mainTable = param.get("mainTable").toString();
        //从表数据
        //String dtlJSON = param.get("dtls").toString();
        JSONArray dtlsJSON = param.getJSONArray("dtls");
        //获取审核意见
        JSONArray tracks=param.getJSONArray("tracks");

        Hashtable<Object, Object> dataTable = new Hashtable();
        DataSet ds=new DataSet();
        DataTable dt = null;

        //写入用户信息
        bp.wf.Dev2Interface.Port_Login(userNo);

        if(workID==0)
            workID= Dev2Interface.Node_CreateBlankWork(flowNo,userNo);
        GenerWorkFlow gwf=new GenerWorkFlow(workID);
        Node nd=new Node(gwf.getNodeID());

        JSONObject res = new JSONObject();
        try {


            //表单填充
            if (StringUtils.isNotBlank(mainTable)){
                Map<String, Object> formMap = parseJSON2Map(JSONObject.fromObject(mainTable));
                formMap.forEach((key, value) -> dataTable.put(key,value));
            }
            //如果是开始节点，将单据编号进行赋值
            if(nd.getItIsStartNode()) {
                FrmNodes frmNodes = new FrmNodes();
                frmNodes.Retrieve(FrmNodeAttr.FK_Node,nd.getNodeID(),FrmNodeAttr.FrmSln,2);
                for(FrmNode frmNode:frmNodes.ToJavaList()){
                    if(!DataType.IsNullOrEmpty(frmNode.getBillNoField())){
                        dataTable.put(frmNode.getBillNoField(),gwf.getBillNo());
                        break;
                    }
                }
            }
            //从表填充
            //可能有多个从表数据
            for(int i=0;i<dtlsJSON.size();i++){
                JSONObject dtlDatas = (JSONObject) dtlsJSON.get(i);
                //从表编号
                String dtlNo=dtlDatas.get("dtlNo").toString();
                //从表数据
                String dtlArryData=dtlDatas.get("dtl").toString();
                //生成table
                DataTable dtlDt=bp.tools.Json.ToDataTable(dtlArryData);
                dtlDt.TableName=dtlNo;
                ds.Tables.add(dtlDt);
            }
            Dev2Interface.Node_SendWork(flowNo,workID,dataTable,ds,toNode,toEmps);
            //更新审核意见
            for(int i=0;i<tracks.size();i++){
                JSONObject track = (JSONObject) tracks.get(i);
                int fk_node=Integer.parseInt(track.get("NodeID").toString());
                int actionType=Integer.parseInt(track.get("ActionType").toString());
                String empFrom=track.get("EmpFrom").toString();
                String nodeName="";
                if(DataType.IsNullOrEmpty(nodeName)){
                    Node node=new Node(fk_node);
                    nodeName=node.getName();
                }
                if(fk_node==gwf.getNodeID()&&actionType==22&&empFrom.equals(WebUser.getNo()))
                {

                    bp.wf.Dev2Interface.WriteTrack(flowNo,fk_node,nodeName,workID,0,track.get("Msg").toString(),ActionType.WorkCheck,"","","");
                }
            }

            //执行退出
            Dev2Interface.Port_SigOut();
        } catch (Exception ex) {
            String msg=ex.getMessage();
            if(msg.contains("url@"))
            {
                String urlParams="";
                //如果是需要选择人
                if(ex.getMessage().contains("Accepter.htm")||ex.getMessage().contains("AccepterOfGener.htm")
                        ||ex.getMessage().contains("AccepterOfDept.htm")||ex.getMessage().contains("AccepterOfDeptSation.htm")
                        ||ex.getMessage().contains("AccepterGener.htm")||ex.getMessage().contains("AccepterOfOrg.htm")){
                    //处理参数
                    urlParams=ex.getMessage().split("\\?")[1];
                    String[] params=urlParams.split("&");
                    String nextNode="0";
                    for(int i=0;i<params.length;i++){
                        //获取ToNode与FK_Node
                        String[] keyValue=params[i].split("=");
                        if(keyValue[0].equals("ToNode")){
                            nextNode=keyValue[1];
                        }
                    }
                    //查找人员范围
                    Work wk = nd.getHisWork();
                    wk.setOID(workID);
                    wk.Retrieve();

                    Selector select = new Selector(Integer.parseInt(nextNode));
                    //获得 部门与人员.
                    DataSet dss = select.GenerDataSet(Integer.parseInt(nextNode), wk);
                    DataTable empDt=dss.GetTableByName("Emps");
                    String empJson=bp.tools.Json.ToJson(empDt);
                    res.put("code", "accepter");
                    res.put("msg","success");
                    res.put("workID",workID);
                    res.put("toNode",toNode);
                    res.put("emps",empJson);
                    return res;

                }
                else if(ex.getMessage().contains("ToNodes.htm")){
                    urlParams=ex.getMessage().split("\\?")[1];
                    String[] params=urlParams.split("=");
               }
                else{
                 ex.printStackTrace();
                 res.put("code", 500);
                 res.put("msg", ex.getMessage());
                 return res;
                 }
            }
            else {
                ex.printStackTrace();
                res.put("code", 500);
                res.put("msg", ex.getMessage());
                return res;
            }
        }

        gwf=new GenerWorkFlow(workID);
        String nextEmps="";
        DataTable listDt=bp.da.DBAccess.RunSQLReturnTable("select FK_Emp from WF_EmpWorks where WorkID="+gwf.getWorkID()+"");
        if(listDt.Rows.size()>0){
            for(DataRow row:listDt.Rows){
                nextEmps+=row.get("FK_Emp").toString()+",";
            }
        }
        res.put("code", 200);
        res.put("workID",gwf.getWorkID());
        res.put("fk_node",gwf.getNodeID());
        res.put("WFState",gwf.getWFState());
        res.put("nextEmps",nextEmps);
        res.put("msg","success");
        return res;
    }
    /**
     * 退回
     *
     * @return 退回信息
     * @throws Exception
     */

    @CrossOrigin(origins = "*")
    @PostMapping("/returnWork")
    public JSONObject returnWork(@RequestBody JSONObject param)throws Exception {
        //声明参数类
        Paras ps = new Paras();
        //设置字符
        String dbstr = SystemConfig.getAppCenterDBVarStr();
        JSONObject res = new JSONObject();
        //执行人员的帐号
        String userNo = param.get("userNo").toString();

        //业务ID
        String workid = param.get("workID").toString();
        //退回到的节点
        String toNode = param.get("toNode").toString();
        //退回信息
        String msg = param.get("returnMsg").toString();
        //是否原路返回
        Boolean isBackToThisNode=Boolean.parseBoolean(param.get("isBackToThisNode").toString());

        try {
            //写入用户信息
            bp.wf.Dev2Interface.Port_Login(userNo);
            //执行退回
            String returnMsg=bp.wf.Dev2Interface.Node_ReturnWork(Long.parseLong(workid), Integer.parseInt(toNode), msg, isBackToThisNode);
            res.put("code", 200);
            res.put("message", returnMsg);
            //下一步节点名称
            res.put("nodeName", toNode);

            bp.da.Log.DefaultLogWriteLine(LogType.Info,"退回成功:"+workid);
        }
        catch (Exception ex){
            //执行退出
            Dev2Interface.Port_SigOut();
            bp.da.Log.DefaultLogWriteLine(LogType.Info,"退回失败:"+ex.getMessage().toString());
            res.put("code", 500);
            res.put("message", ex.getMessage());
            //下一步节点
            res.put("nodeName", "");
        }
        //执行退出
        Dev2Interface.Port_SigOut();
        return res;
    }
    /**
     * 结束流程
     *
     * @return 退回信息
     * @throws Exception
     */

    @CrossOrigin(origins = "*")
    @PostMapping("/setEndWork")
    public JSONObject setEndWork(@RequestBody JSONObject param)throws Exception {
        //声明参数类
        Paras ps = new Paras();
        //设置字符
        String dbstr = SystemConfig.getAppCenterDBVarStr();
        JSONObject res = new JSONObject();
        //执行人员的帐号
        String userNo = param.get("userNo").toString();

        //业务ID
        String workid = param.get("workID").toString();


        try {
            //写入用户信息
            bp.wf.Dev2Interface.Port_Login(userNo);
            //执行退回
            String returnMsg=bp.wf.Dev2Interface.Flow_DoFlowOver(Long.parseLong(workid),"审核结束");
            res.put("code", 200);
            res.put("message", "流程已结束");

        }
        catch (Exception ex){
            //执行退出
            Dev2Interface.Port_SigOut();

            res.put("code", 500);
            res.put("message", ex.getMessage());

        }
        //执行退出
        Dev2Interface.Port_SigOut();
        return res;
    }
    /**
     * 获取某个人可以发起的流程
     *
     * @param userNo
     *            工作人员编号
     *            工作人员SID
     * @return 返回信息
     * @throws Exception
     */
    @RequestMapping(value = "/Start_Init",method=RequestMethod.GET)
    public JSONObject Start_Init(String userNo,String doMainExt) throws Exception{
        JSONObject res = new JSONObject();

        try{
            //写入用户信息
            bp.wf.Dev2Interface.Port_Login(userNo);
            //执行
            String json = "";
            json = DBAccess.GetBigTextFromDB("WF_Emp", "No", userNo, "StartFlows");
            if (DataType.IsNullOrEmpty(json) == false)
            {
                res.put("code", 200);
                res.put("message","获取成功");
                res.put("result",json);
                return  res;
            }

            //定义容器.
            DataSet ds = new DataSet();

            //获得能否发起的流程.
            DataTable dtStart = Dev2Interface.DB_StarFlows(WebUser.getNo(),doMainExt);
            dtStart.TableName = "Start";
            ds.Tables.add(dtStart);


            ///动态构造 流程类别.
            DataTable dtSort = new DataTable("Sort");
            dtSort.Columns.Add("No", String.class);
            dtSort.Columns.Add("Name", String.class);
            dtSort.Columns.Add("DomainExt", String.class);

            String nos = "";
            for (DataRow dr : dtStart.Rows)
            {
                String no = dr.getValue("FK_FlowSort").toString();
                if (nos.contains(no) == true)
                {
                    continue;
                }

                String name = dr.getValue("FK_FlowSortText").toString();
                String domainExt = dr.getValue("DomainExt").toString();

                nos += "," + no;

                DataRow mydr = dtSort.NewRow();
                mydr.setValue(0, no);
                mydr.setValue(1, name);
                mydr.setValue(2, domainExt);
                dtSort.Rows.add(mydr);
            }

            dtSort.TableName = "Sort";
            ds.Tables.add(dtSort);

            /// 动态构造 流程类别.

            //返回组合
            json = bp.tools.Json.ToJson(ds);

            //把json存入数据表，避免下一次再取.
            if (dtStart.Rows.size() > 0)
            {
                DBAccess.SaveBigTextToDB(json, "WF_Emp", "No", WebUser.getNo(), "StartFlows");
            }
            res.put("code", 200);
            res.put("message","获取成功");
            res.put("result",json);
        }
        catch(Exception ex){
            res.put("code", 500);
            res.put("message",ex.getMessage());
            res.put("result","[]");
        }
        //执行退出
        Dev2Interface.Port_SigOut();
        return res;
    }
    /**
     * 通用获取待办
     *
     * @param userNo
     *            工作人员编号
     *            工作人员SID
     * @param FK_Flow
     *            流程编号
     * @return 返回信息
     * @throws Exception
     */
    @RequestMapping(value = "/workAssignment",method=RequestMethod.GET)
    public JSONObject Todo_Init(String userNo,String FK_Flow,String doMainExt){
        JSONObject res = new JSONObject();

        try{
            DataSet ds = new DataSet();
            //待办列表
            DataTable dt = bp.wf.Dev2Interface.DB_Todolist(userNo);
            dt.TableName = "Todolist";
            ds.Tables.add(dt);
            //获取在途列表
            dt = new DataTable();
            dt = bp.wf.Dev2Interface.DB_GenerRuning(userNo,FK_Flow,false,doMainExt,false);
            dt.TableName = "Running";
            ds.Tables.add(dt);
            //获取已完成列表
            dt = new DataTable();
            dt = bp.wf.Dev2Interface.DB_FlowComplete(userNo);
            dt.TableName = "Complete";
            ds.Tables.add(dt);

            Paras ps = new Paras();
            String str = SystemConfig.getAppCenterDBVarStr();
            ps.SQL="select * from wf_returnwork where workid in(select workid from wf_empworks where fk_emp="+str+"fk_emp and domainExt="+str+"domainExt)";
            ps.Add("fk_emp",userNo);
            ps.Add("domainExt",doMainExt);
            dt=new DataTable();
            dt=bp.da.DBAccess.RunSQLReturnTable(ps);
            dt.TableName="ReturnWorks";
            ds.Tables.add(dt);

            res.put("code", 200);
            res.put("message","获取成功");
            res.put("result",bp.tools.Json.ToJson(ds));
        }
        catch(Exception ex){
            res.put("code", 500);
            res.put("message",ex.getMessage());
            res.put("result","[]");
        }
        return res;
    }
    public static Map<String, Object> parseJSON2Map(JSONObject json) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        // 最外层解析
        for (Object k : json.keySet()) {
            Object v = json.get(k);
            // 如果内层还是json数组的话，继续解析
            if (v instanceof JSONArray) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                Iterator<JSONObject> it = ((JSONArray) v).iterator();
                while (it.hasNext()) {
                    JSONObject json2 = it.next();
                    list.add(parseJSON2Map(json2));
                }
                map.put(k.toString(), list);
            } else if (v instanceof JSONObject) {
                // 如果内层是json对象的话，继续解析
                map.put(k.toString(), parseJSON2Map((JSONObject) v));
            } else {
                // 如果内层是普通对象的话，直接放入map中
                map.put(k.toString(), v);
            }
        }
        return map;
    }
}

