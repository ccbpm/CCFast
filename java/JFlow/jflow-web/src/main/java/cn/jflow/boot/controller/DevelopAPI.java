package cn.jflow.boot.controller;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import bp.da.*;
import bp.en.Entities;
import bp.en.Row;
import bp.en.Rows;
import bp.sys.*;
import bp.tools.StringUtils;
import bp.wf.*;
import bp.wf.data.GERpt;
import bp.wf.httphandler.WF_WorkOpt_OneWork;
import bp.wf.template.*;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import bp.ccfast.third.goview.GoviewProject;
import bp.ccfast.third.goview.GoviewProjectData;
import bp.ccfast.third.goview.GoviewProjectDataVo;
import bp.ccfast.third.goview.GoviewProjectVo;
import bp.ccfast.third.goview.SysFileVo;
import bp.ccfast.third.goview.SysUser;
import bp.ccfast.third.goview.Token;
import bp.difference.ContextHolderUtils;
import bp.difference.SystemConfig;
import bp.difference.handler.HttpHandlerBase;
import bp.en.QueryObject;
import bp.tools.Json;
import bp.web.WebUser;
import bp.wf.httphandler.WF;
import bp.wf.httphandler.WF_CCForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags="工具包接口")
@RequestMapping(value = "/WF/API")
public class DevelopAPI extends HttpHandlerBase {
    public static Object Return_Info(int code, String msg, Object data)
    {
        Hashtable ht = new Hashtable();
        ht.put("code", code);
        ht.put("message", msg);
        ht.put("msg", msg);
        ht.put("data", data);
        return ht;
    }

    @RequestMapping(value = "/Node_ShiftUn")
    @ApiOperation("撤销移交")
    @ApiImplicitParams({
            @ApiImplicitParam(name="token",value="验证码",paramType = "query",required = true),
            @ApiImplicitParam(name="workID",value="实例ID",dataType = "Long" ,required = true),
    })
    public final Object Node_ShiftUn(String token, long workID) {
        if(DataType.IsNullOrEmpty(token) == true){
            return Return_Info(500,"没有获取到Token","");
        }

        try{
            Dev2Interface.Port_LoginByToken(token);

            String info = Dev2Interface.Node_ShiftUn(workID);
            return Return_Info(200, "撤销移交成功", info);

        }catch(Exception e){
            return Return_Info(500,"撤销移交失败",e.getMessage());
        }
    }

    /**
     * 获取流程时间轴，包含了分合流流程，父子流程，延续子流程
     * @param token
     * @param fk_flow
     * @param workID
     * @param fid
     * @return
     */
    @RequestMapping(value = "/Flow_TrackInfo")
    @ApiOperation("获得流程流转信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="token",value="token",paramType = "query",required = true),
            @ApiImplicitParam(name="fk_flow",value="流程编号",dataType = "String" ,required = true),
            @ApiImplicitParam(name="workID",value="流程实例ID",dataType = "Long" ,required = true),
            @ApiImplicitParam(name="fid",value="传0",dataType = "Long" ,required = true)
    })
    public final Object  Flow_TrackInfo(String token, String fk_flow, long workID, long fid) {
        if(DataType.IsNullOrEmpty(token) == true){
            return Return_Info(500,"没有获取到Token","");
        }
        if(DataType.IsNullOrEmpty(fk_flow) == true){
            return Return_Info(500,"没有获取到流程ID","");
        }
        if(DataType.IsNullOrEmpty(workID) == true){
            return Return_Info(500,"没有获取到workID实例ID","");
        }
        if(DataType.IsNullOrEmpty(fid) == true){
            return Return_Info(500,"没有获取到fid，传0","");
        }
        DataSet ds = new DataSet();
        try{
            Dev2Interface.Port_LoginByToken(token);

            return Return_Info(200, "获得整体流程信息成功", Json.ToJson(Dev2Interface.DB_GenerTrackTable( fk_flow,  workID, fid)));

        }catch(Exception e){
            return Return_Info(500,"获得整体流程信息失败",e.getMessage());
        }
    }
    /**
     * 根据父流程ID获取子流程ID
     * @param token
     * @return
     */
    @RequestMapping(value = "/Flow_SubGenerWorkFlows")
    @ApiOperation("根据父流程ID获取子流程ID")
    @ApiImplicitParams({
            @ApiImplicitParam(name="token",value="验证码",paramType = "query",required = true),
            @ApiImplicitParam(name="workID",value="父流程实例workID",dataType = "Long" ,required = true),
    })
    public final Object Flow_SubGenerWorkFlows(String token,  long pworkID) {
        if (DataType.IsNullOrEmpty(token) == true) {
            return Return_Info(500, "没有获取到Token", "");
        }
        if (DataType.IsNullOrEmpty(pworkID) == true) {
            return Return_Info(500, "没有获取到父流程实例ID", "");
        }

        return Return_Info(200, "获取子流程ID信息成功", Json.ToJson(getSubWrokId(pworkID)));
    }

    public DataTable getSubWrokId(long pworkID){

        String subWorkId="";
        String sql="select  workid,pworkid,pnodeid,fk_flow from wf_generworkflow where pworkid=";
        DataTable dtRe=new DataTable();
        dtRe.Columns.Add("workid", String.class);
        dtRe.Columns.Add("pworkid", String.class);
        dtRe.Columns.Add("pnodeid", String.class);
        dtRe.Columns.Add("fk_flow", String.class);

        DataTable dt = DBAccess.RunSQLReturnTable(sql+pworkID);

        for(DataRow dr : dt.Rows){
            DataRow reRow = dtRe.NewRow();
            reRow.setValue("workid", dr.getValue("workid"));
            reRow.setValue("pworkid", dr.getValue("pworkid"));
            reRow.setValue("pnodeid", dr.getValue("pnodeid"));
            reRow.setValue("fk_flow", dr.getValue("fk_flow"));
            dtRe.Rows.add(reRow);
            //dtRe.Rows.AddRow(dr);
            DataTable dt2 = DBAccess.RunSQLReturnTable(sql+dr.getValue(0));
            for(DataRow dr2 : dt2.Rows) {
                DataRow reRow2 = dtRe.NewRow();
                reRow2.setValue("workid", dr2.getValue("workid"));
                reRow2.setValue("pworkid", dr2.getValue("pworkid"));
                reRow2.setValue("pnodeid", dr2.getValue("pnodeid"));
                reRow2.setValue("fk_flow", dr2.getValue("fk_flow"));
                dtRe.Rows.add(reRow2);
            }
        }
        return dtRe;
    }
    /**
     * 获得整体流程信息
     * @param token
     * @param workID
     * @return
     */
    @RequestMapping(value = "/Flow_WorkInfo")
    @ApiOperation("获得整体流程信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="token",value="验证码",paramType = "query",required = true),
            @ApiImplicitParam(name="workID",value="实例ID",dataType = "Long" ,required = true),
    })
    public final Object Flow_WorkInfo(String token, long workID) {
        if(DataType.IsNullOrEmpty(token) == true){
            return Return_Info(500,"没有获取到Token","");
        }

        DataSet ds = new DataSet();
        try{
            Dev2Interface.Port_LoginByToken(token);

            //处理主表.
            GenerWorkFlow gwf = new GenerWorkFlow(workID);
            if (gwf.getFID() != 0)
            {
                workID = gwf.getFID();
                gwf.setWorkID(workID);
                gwf.Retrieve();
            }
            DataTable dt = gwf.ToDataTableField("WF_GenerWorkFlow");
            dt.Columns.Add("CurrNodeMark"); //增加一个字段.

            DataTable dtMark = Flow_NodesMarkExt(gwf.getFlowNo());
            dt.Rows.get(0).setValue("CurrNodeMark",Flow_NodeMarkExt(gwf.getNodeID(),dtMark));
            ds.Tables.add(dt); //增加到数据源.

            //处理从表.
            GenerWorkerLists ens = new GenerWorkerLists();
            QueryObject qo = new QueryObject(ens);
            qo.AddWhere("WorkID", workID);
            qo.addOr();
            qo.AddWhere("FID", workID);
            qo.addOrderBy("RDT");
            qo.DoQuery();

            ens.Retrieve("WorkID", workID, "RDT");
            DataTable dtGwls = ens.ToDataTableField("WF_GenerWorkerList");
            dtGwls.Columns.Add("NodeMark"); //增加一个字段.
            //转换NodeI.
            for (DataRow item : dtGwls.Rows)
            {
                int nodeID = Integer.parseInt( String.valueOf(item.get("FK_Node")));
                item.setValue("NodeMark",Flow_NodeMarkExt(nodeID, dtMark));
            }
            ds.Tables.add(dtGwls); //增加到数据源.

            String info= Json.ToJson(ds);
            return Return_Info(200, "获取成功", info);

        }catch(Exception e){
            return Return_Info(500,"创建WorkID失败",e.getMessage());
        }
    }
    /**
     * 获得流程中每个节点的相关时间
     * 第一个节点要有创建时间（与接收时间同字段），发送时间
     * 其它节点要有接收，发送时间
     * 结束节点还要提供办结时间（与发送时间同字段）
     * @param token
     * @param flowID
     * @param workID
     * @return[{
     *    "NodeID":""  ,                 节点id
     * 	  "ActionType": "" ,             节点状态
     * 	  "ActionTypeText":"" ,          节点状态描述
     * 	  "ReceiveTime":"" ,             节点接收时间（第一个节点为创建时间）
     * 	  "RDT": ""                      节点发送时间(ActionType==8时，RDT即为完结时间)
     *  }]
     */
    @RequestMapping(value = "/Work_NodeInfo")
    @ApiOperation("获得整体流程信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="token",value="验证码",paramType = "query",required = true),
            @ApiImplicitParam(name="flowID",value="流程ID",dataType = "String" ,required = true),
            @ApiImplicitParam(name="workID",value="实例ID",dataType = "long" ,required = true),
//            @ApiImplicitParam(name="NodeID",value="节点ID",dataType = "String" ,required = false),
    })
    public final Object Work_NodeInfo(String token,String flowID, long workID ) {
        if(DataType.IsNullOrEmpty(token) == true){
            return Return_Info(500,"没有获取到Token","");
        }
        String NodeID="";
        DataSet ds = new DataSet();
        try{
            Dev2Interface.Port_LoginByToken(token);
            GenerWorkFlow gwf = new GenerWorkFlow(workID);
            String atpara=gwf.getRow().get("AtPara").toString();
            //第一个节点创建时间
            String createTime="";
            if(atpara.contains("@CreateRDT")){
                String [] arr=atpara.split("@");
                for(String str:arr){
                    if(str.contains("CreateRDT")){
                        createTime=str.split("=")[1];
                    }
                }
            }
            String tableName = "ND" + flowID + "track";
            Paras pens = new Paras();
            //轨迹表
            StringBuilder sql=new StringBuilder("");
//            sql.append(" select ActionType,NDfrom,NDfromT,NdTo,NdToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,'' ReceiveTime from ")
            sql.append(" select NDfrom NodeID,ActionType,ActionTypeText,'' ReceiveTime ,RDT from ")
                    .append(tableName).append(" where ActionType not in(21,22,26,32) ");  //21:抄送  22:审核 26:跳转  32:信息Route
            sql.append(" and workID =").append(SystemConfig.getAppCenterDBVarStr()).append("v");
            pens.Add("v",workID);
            if(StringUtils.isNotEmpty(NodeID)){
                sql.append(" and NDfrom =").append(SystemConfig.getAppCenterDBVarStr()).append("v1");
                pens.Add("v1",NodeID);
            }
            pens.SQL=sql.toString();
            DataTable dt= DBAccess.RunSQLReturnTable(pens);
            for(int i=0;i<dt.Rows.size();i++){
                DataRow dr= dt.Rows.get(i);
               if(i==0 && StringUtils.isEmpty(NodeID)){
                   if(StringUtils.isEmpty(createTime)){
                       createTime=dr.getValue("RDT").toString();
                   }
               }else{
                   createTime=dt.Rows.get(i-1).getValue("rdt").toString();
               }
                dr.put("ReceiveTime",createTime);
            }
            return Return_Info(200, "获取成功", Json.ToJson(dt));

        }catch(Exception e){
            return Return_Info(500,"获取失败",e.getMessage());
        }
    }
    private String Flow_NodeMarkExt(int nodeID)
    {
        //获得节点的Mark.
        Paras pens = new Paras();
        pens.SQL= "SELECT NodeMark FROM WF_Node WHERE NodeID=" +SystemConfig.getAppCenterDBVarStr() + "v";
        pens.Add("v",nodeID);
        return DBAccess.RunSQLReturnString(pens);
    }
    private String Flow_NodeMarkExt(int nodeID, DataTable dt) throws Exception {
        for (DataRow dr : dt.Rows)
        {
            if (Integer.parseInt(dr.getValue(0).toString()) == nodeID)
            {
                String str  = dr.getValue(1).toString();
                if (DataType.IsNullOrEmpty(str)==true)
                    return String.valueOf(nodeID);
                return str;
            }
        }
        throw new Exception("err@没有找到NodeID="+nodeID+"的mark");
    }
    private DataTable Flow_NodesMarkExt(String flowNo)
    {
        //获得节点的Mark.
        Paras pens = new Paras();
        pens.SQL="SELECT NodeID,Mark FROM WF_Node WHERE FK_Flow=" +SystemConfig.getAppCenterDBVarStr() + "v";
        pens.Add("v",flowNo);
        return DBAccess.RunSQLReturnTable(pens);
    }

    /**
     * 登录
     * @param privateKey 密钥
     * @param userNo 用户编号
     * @param orgNo 租户/组织编号（集团版和SaaS版需要填写）
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/Port_Login")
    @ApiOperation("根据密钥和用户名登录,返回用户登录信息其中有Token")
    @ApiImplicitParams({
            @ApiImplicitParam(name="privateKey",value="密钥",paramType = "query",required = true),
            @ApiImplicitParam(name="userNo",value="用户编号",required = true),
            @ApiImplicitParam(name="orgNo",value="租户/组织编号（SaaS版需要填写）",required = false),
    })
    public final Object Port_Login_Submit(String privateKey, String userNo, String orgNo) throws Exception {
        if(DataType.IsNullOrEmpty(privateKey) == true){
            return Return_Info(500,"登录失败","参数privateKey不能为空");
        }
        if(DataType.IsNullOrEmpty(userNo) == true){
            return Return_Info(500,"登录失败","参数userNo不能为空");
        }
        String localKey = SystemConfig.GetValByKey("PrivateKey", "DiGuaDiGua,IamCCBPM");
        if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
        {
            if(DataType.IsNullOrEmpty(orgNo) == true){
                return Return_Info(500,"登录失败","当前运行模式为SaaS模式，参数orgNo不能为空");
            }
            bp.wf.port.admin2group.Org org = new bp.wf.port.admin2group.Org(orgNo);
            String key = org.GetValStrByKey("PrivateKey");
            if (DataType.IsNullOrEmpty(key) == false)
                localKey = key;
        }
        if (DataType.IsNullOrEmpty(localKey) == true)
            localKey = "DiGuaDiGua,IamCCBPM";
        if (localKey.equals(privateKey) == false)
            return Return_Info(500,"登录失败","私钥错误，请检查全局文件中配置 PrivateKey");

        try{
            String Token = "";
            if(SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc && DataType.IsNullOrEmpty(orgNo) == true){
                String sql = "SELECT No,OrgNo FROM port_Emp WHERE no = " + userNo + "'";
                DataTable dt = DBAccess.RunSQLReturnTable(sql);
                if (dt.Rows.size() != 1)
                {
                    throw new RuntimeException("err@用户名不存在." );
                }
                orgNo = dt.Rows.get(0).getValue(1) != null ? dt.Rows.get(0).getValue(1).toString() : "";
            }
           // if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS){
            Dev2Interface.Port_Login(userNo,orgNo);
            Token = Dev2Interface.Port_GenerToken("PC");
            /*}else{
                Token = Dev2Interface.Port_GenerToken(userNo);
                Dev2Interface.Port_Login(userNo,orgNo);
            }*/
            Hashtable ht = new Hashtable();
            ht.put("No", WebUser.getNo());
            ht.put("Name", WebUser.getName());
            ht.put("FK_Dept", WebUser.getDeptNo());
            ht.put("FK_DeptName", WebUser.getDeptName());
            ht.put("OrgNo", WebUser.getOrgNo());
            ht.put("OrgName", WebUser.getOrgName());
            ht.put("Token", Token);
            return Return_Info(200,"登录成功", Json.ToJson(ht));
        }catch(Exception e){
            return Return_Info(500,"登录失败","登录失败:"+e.getMessage());
        }
    }

    /**
     * 人员信息保存
     * @param token
     * @param orgNo
     * @param userNo
     * @param userName
     * @param deptNo
     * @param kvs
     * @param stats
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/Port_Emp_Save")
    @ApiOperation("人员信息保存")
    @ApiImplicitParams({
            @ApiImplicitParam(name="token",value="Token",paramType = "query",required = true),
            @ApiImplicitParam(name="orgNo",value="组织编号,如：Test",required = true),
            @ApiImplicitParam(name="userNo",value="用户编号(如果是saas模式就填orgNo_userID),如：Test_1033",required = true),
            @ApiImplicitParam(name="userName",value="用户名称,如：张三",required = true),
            @ApiImplicitParam(name="deptNo",value="部门编号",required = true),
            @ApiImplicitParam(name="kvs",value="属性值(如果是saas模式就必须有@UserID=xxxx),如: @Name=张三@Tel=18778882345@Pass=123@UserID=1033",required = false),
            @ApiImplicitParam(name="stats",value="岗位/角色集合,比如:001,002,003,",required = false),
    })
    public Object Port_Emp_Save(String token, String orgNo, String userNo, String userName, String deptNo, String kvs, String stats) throws Exception {
        Dev2Interface.Port_LoginByToken(token);
        if (WebUser.getIsAdmin() == false)
            return Return_Info(500,"[" + WebUser.getName() + "]不是管理员不能操作人员信息","0");
        String msg = bp.port.OrganizationAPI.Port_Emp_Save(orgNo, userNo, userName, deptNo, kvs, stats);
        if(msg.contains("err@")){
            return Return_Info(500, msg, "0");
        }else {
            return Return_Info(200,"人员信息保存成功", msg);
        }
    }

    /**
     * 人员删除
     * @param token
     * @param orgNo
     * @param userNo
     * @return 1=成功,  其他的标识异常.
     * @throws Exception
     */
    @RequestMapping(value = "/Port_Emp_Delete")
    @ApiOperation("人员删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="token",value="Token",paramType = "query",required = true),
            @ApiImplicitParam(name="orgNo",value="组织编号",required = false),
            @ApiImplicitParam(name="userNo",value="用户编号",required = true),
    })
    public Object Port_Emp_Delete(String token,String orgNo, String userNo) throws Exception {
        Dev2Interface.Port_LoginByToken(token);
        if (WebUser.getIsAdmin() == false)
            return Return_Info(500,"[" + WebUser.getName() + "]不是管理员不能操作人员信息","0");
        String msg = bp.port.OrganizationAPI.Port_Emp_Delete(orgNo, userNo);
        if(msg.contains("err@")){
            return Return_Info(500, msg, "0");
        }else {
            return Return_Info(200,"人员删除成功", msg);
        }
    }

    /**
     * 保存部门组织
     * @param token
     * @param orgNo
     * @param name
     * @param adminer
     * @param adminerName
     * @param keyVals
     * @return 1 增加成功，其他的增加失败.
     * @throws Exception
     */
    @RequestMapping(value = "/Port_Org_Save")
    @ApiOperation("集团模式下同步组织以及管理员信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "orgNo", value = "组织编号", required = true),
            @ApiImplicitParam(name = "name", value = "组织名称", required = true),
            @ApiImplicitParam(name = "adminer", value = "管理员账号", required = true),
            @ApiImplicitParam(name = "adminerName", value = "管理员名字", required = true),
            @ApiImplicitParam(name = "keyVals", value = "其他的值:@Leaer=zhangsan@Tel=12233333@Idx=1", required = false)
    })
    public Object Port_Org_Save(String token,String orgNo,  String name, String adminer, String adminerName, String keyVals) throws Exception {
        Dev2Interface.Port_LoginByToken(token);
        if (WebUser.getIsAdmin() == false)
            return Return_Info(500,"[" + WebUser.getName() + "]不是管理员不能操作组织信息","0");
        return Return_Info(200,"集团模式下同步组织以及管理员信息成功",bp.port.OrganizationAPI.Port_Org_Save(orgNo, name, adminer, adminerName,keyVals));
    }

    /**
     * 保存部门
     * @param token
     * @param orgNo
     * @param no
     * @param name
     * @param parentNo
     * @param keyVals
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/Port_Dept_Save")
    @ApiOperation("保存部门,如果有此数据则修改,无此数据则增加.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "orgNo", value = "组织编号", required = false),
            @ApiImplicitParam(name = "no", value = "部门编号", required = true),
            @ApiImplicitParam(name = "name", value = "名称", required = true),
            @ApiImplicitParam(name = "parentNo", value = "父节点编号", required = true),
            @ApiImplicitParam(name = "keyVals", value = "其他的值:@Leaer=zhangsan@Tel=18660153393@Idx=1", required = false)
    })
    public Object Port_Dept_Save(String token,String orgNo, String no, String name, String parentNo, String keyVals) throws Exception {
        Dev2Interface.Port_LoginByToken(token);
        if (WebUser.getIsAdmin() == false)
            return Return_Info(500,"[" + WebUser.getName() + "]不是管理员不能删保存部门信息","0");
        return Return_Info(200,"保存部门信息成功",bp.port.OrganizationAPI.Port_Dept_Save(orgNo, no, name, parentNo, keyVals));

    }
    @RequestMapping(value = "/Port_Dept_ST_Save")
    @ApiOperation("保存部门,如果有此数据则修改,无此数据则增加.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "orgNo", value = "组织编号", required = false),
            @ApiImplicitParam(name = "no", value = "部门编号", required = true),
            @ApiImplicitParam(name = "name", value = "名称", required = true),
            @ApiImplicitParam(name = "parentNo", value = "父节点编号", required = true),
            @ApiImplicitParam(name = "keyVals", value = "其他的值:@Leaer=zhangsan@Tel=18660153393@Idx=1", required = false),
            @ApiImplicitParam(name = "stations", value = "部门下的岗位值:001,002,003", required = false)
    })
    public Object Port_Dept_ST_Save(String token,String orgNo, String no, String name, String parentNo, String keyVals,String stations) throws Exception {
        Dev2Interface.Port_LoginByToken(token);
        if (WebUser.getIsAdmin() == false)
            return Return_Info(500,"[" + WebUser.getName() + "]不是管理员不能删保存部门信息","0");
        return Return_Info(200,"保存部门信息成功",bp.port.OrganizationAPI.Port_Dept_ST_Save(orgNo, no, name, parentNo, keyVals,stations));

    }
    @RequestMapping(value = "/Port_Dept_Delete")
    @ApiOperation("删除部门.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "no", value = "要删除的部门编号", required = true)
    })
    public Object Port_Dept_Delete(String token,String no) throws Exception {
        Dev2Interface.Port_LoginByToken(token);
        if (WebUser.getIsAdmin() == false)
            return Return_Info(500,"[" + WebUser.getName() + "]不是管理员不能删除部门信息","0");
        return Return_Info(200,"删除部门成功",bp.port.OrganizationAPI.Port_Dept_Delete(no));
    }
    /// <summary>
    /// 保存岗位, 如果有此数据则修改，无此数据则增加.
    /// </summary>
    /// <param name="orgNo">组织编号</param>
    /// <param name="no">编号</param>
    /// <param name="name">名称</param>
    /// <returns>return 1 增加成功，其他的增加失败.</returns>
    @RequestMapping(value = "/Port_Station_Save")
    @ApiOperation("保存岗位, 如果有此数据则修改，无此数据则增加..")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "orgNo", value = "组织编号", required = true),
            @ApiImplicitParam(name = "no", value = "岗位编号", required = true),
            @ApiImplicitParam(name = "name", value = "岗位名称", required = true),
            @ApiImplicitParam(name = "stationTypeNo", value = "岗位类型编号", required = false),
            @ApiImplicitParam(name = "keyVals", value = "其他值（扩展入参，如果岗位中又其他字段需要保存，可以放到keyVals中，书写格式为\"@字段名=值@字段名=值\"）", required = false)
    })
    public Object Port_Station_Save(String token,String orgNo, String no,  String name,String stationTypeNo,String keyVals) throws Exception {
        Dev2Interface.Port_LoginByToken(token);
        if (WebUser.getIsAdmin() == false)
            return Return_Info(500,"[" + WebUser.getName() + "]不是管理员不能保存岗位信息","0");
        String msg = bp.port.OrganizationAPI.Port_Station_Save(orgNo, no, name,stationTypeNo, keyVals);
        if(msg.contains("err@")){
            return Return_Info(500, msg, "0");
        }else {
            return Return_Info(200,"保存岗位成功", msg);
        }
    }
    /// <summary>
    /// 删除岗位.
    /// </summary>
    /// <param name="no">删除指定的岗位编号</param>
    /// <returns></returns>
    @RequestMapping(value = "/Port_Station_Delete")
    @ApiOperation("删除岗位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "no", value = "要删除的岗位编号", required = true)
    })
    public Object Port_Station_Delete(String token,String no) throws Exception {
        Dev2Interface.Port_LoginByToken(token);
        if (WebUser.getIsAdmin() == false)
            return Return_Info(500,"[" + WebUser.getName() + "]不是管理员不能删除岗位信息","0");
        return Return_Info(200,"删除岗位成功",bp.port.OrganizationAPI.Port_Station_Delete(no, WebUser.getOrgNo()));
    }
    /**
     保存用户组, 如果有此数据则修改，无此数据则增加.
     @param token Token
     @param orgNo 组织编号
     @param no 用户组编号
     @param name 用户组名称
     @param keyVals 其他值
     @return
     */
    @RequestMapping(value = "/Port_Team_Save")
    @ApiOperation("保存用户组, 如果有此数据则修改，无此数据则增加")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "orgNo", value = "组织编号", required = true),
            @ApiImplicitParam(name = "typeno", value = "用户组类型编号", required = true),
            @ApiImplicitParam(name = "no", value = "用户组编号", required = true),
            @ApiImplicitParam(name = "name", value = "用户组名称", required = true),
            @ApiImplicitParam(name = "keyVals", value = "其他值", required = false)
    })
   public final Object Port_Team_Save(String token, String typeno,String orgNo, String no, String name, String keyVals) throws Exception {
        Dev2Interface.Port_LoginByToken(token);

        if (WebUser.getIsAdmin() == false)
            return Return_Info(500,"[" + WebUser.getName() + "]不是管理员不能维护用户组信息","0");

        return Return_Info(200,"保存用户组成功",bp.port.OrganizationAPI.Port_Team_Save(orgNo, typeno,no, name, keyVals));
    }
    /**
     删除用户组.
     @param no 删除指定的用户组编号
     @return
     */
    @RequestMapping(value = "/Port_Team_Delete")
    @ApiOperation("删除用户组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "no", value = "删除指定的用户组编号", required = true)
    })
    public final Object Port_Team_Delete(String token, String no) throws Exception {
        //根据token登录
        Dev2Interface.Port_LoginByToken(token);

        if (WebUser.getIsAdmin() == false)
            return Return_Info(500,"[" + WebUser.getName() + "]不是管理员不能删除用户组信息","0");

        return  Return_Info(200,"删除用户组成功",bp.port.OrganizationAPI.Port_Team_Delete(no));
    }
    /**
     保存用户组类型, 如果有此数据则修改，无此数据则增加.

     @param token Token
     @param orgNo 组织编号
     @param no 用户组编号
     @param name 用户组名称
     @param keyVals 其他值
     @return
     */
    @RequestMapping(value = "/Port_TeamType_Save")
    @ApiOperation("保存用户组类型, 如果有此数据则修改，无此数据则增加")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "orgNo", value = "组织编号", required = true),
            @ApiImplicitParam(name = "no", value = "用户组类型编号", required = true),
            @ApiImplicitParam(name = "name", value = "用户组类型名称", required = true),
            @ApiImplicitParam(name = "keyVals", value = "其他值", required = false)
    })
   public final Object Port_TeamType_Save(String token, String orgNo, String no, String name, String keyVals) throws Exception {
        Dev2Interface.Port_LoginByToken(token);

        if (WebUser.getIsAdmin() == false)
            return Return_Info(500,"[" + WebUser.getName() + "]不是管理员不能维护用户组信息","0");

        return Return_Info(200,"保存用户组类型成功",bp.port.OrganizationAPI.Port_TeamType_Save(orgNo, no, name, keyVals));
    }
    /**
     * 删除用户组类型.
     *
     * @param no 删除指定的用户组编号
     * @return
     */
    @RequestMapping(value = "/Port_TeamType_Delete")
    @ApiOperation("删除用户组类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "no", value = "删除指定的用户组类型编号", required = true)
    })
    public final Object Port_TeamType_Delete(String token, String no) throws Exception {
        //根据token登录
        Dev2Interface.Port_LoginByToken(token);

        if (WebUser.getIsAdmin() == false)
            return Return_Info(500,"[" + WebUser.getName() + "]不是管理员不能删除用户组类型信息","0");

        return Return_Info(200,"删除用户组类型成功",bp.port.OrganizationAPI.Port_TeamType_Delete(no));
    }

    /**
     * 保存岗位类型, 如果有此数据则修改，无此数据则增加.
     *
     * @param token   Token
     * @param orgNo   组织编号
     * @param no      岗位类型编号
     * @param name    岗位类型名称
     * @param keyVals 其他值
     * @return
     */
    @RequestMapping(value = "/Port_StationType_Save")
    @ApiOperation("保存岗位类型, 如果有此数据则修改，无此数据则增加")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "orgNo", value = "组织编号", required = true),
            @ApiImplicitParam(name = "no", value = "岗位类型编号", required = true),
            @ApiImplicitParam(name = "name", value = "岗位类型名称", required = true),
            @ApiImplicitParam(name = "keyVals", value = "其他值", required = false)
    })
   public final Object Port_StationType_Save(String token, String orgNo, String no, String name, String keyVals) throws Exception {
        Dev2Interface.Port_LoginByToken(token);

        if (WebUser.getIsAdmin() == false)
            return Return_Info(500,"[" + WebUser.getName() + "]不是管理员不能维护岗位类型信息","0");

        return Return_Info(200,"保存岗位类型成功",bp.port.OrganizationAPI.Port_StationType_Save(orgNo, no, name, keyVals));
    }
    /**
     * 删除岗位类型.
     *
     * @param no 删除指定的岗位类型编号
     * @return
     */
    @RequestMapping(value = "/Port_StationType_Delete")
    @ApiOperation("删除岗位类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "no", value = "删除指定的岗位类型编号", required = true)
    })
    public final Object Port_StationType_Delete(String token, String no) throws Exception {
        //根据token登录
        Dev2Interface.Port_LoginByToken(token);

        if (WebUser.getIsAdmin() == false)
            return Return_Info(500,"[" + WebUser.getName() + "]不是管理员不能删除岗位类型信息","0");

        return Return_Info(200,"删除岗位类型成功",bp.port.OrganizationAPI.Port_StationType_Delete(no,WebUser.getOrgNo()));
    }

    /**
     * 创建根据流程编号WorkID
     * @param token
     * @param flowNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/Node_CreateBlankWorkID")
    @ApiOperation("根据流程编号创建流程实例,返回WorkID为单据的主键.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "flowNo", value = "流程模板编号", required = true)
    })
    public final Object Node_CreateBlankWorkID(String token,String flowNo) throws Exception {
        Dev2Interface.Port_LoginByToken(token);
        try{
            long WorkID = Dev2Interface.Node_CreateBlankWork(flowNo, WebUser.getUserID());
            return Return_Info(200,"创建根据流程编号WorkID成功",String.valueOf(WorkID));
        }catch(Exception e){
            return Return_Info(500,"流程创建WorkID失败","流程创建WorkID失败:"+e.getMessage());
        }
    }

    @RequestMapping(value = "/Node_SetDraft")
    @ApiOperation("设置草稿.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例WorkID", dataType = "Long", required = true)
    })
    public final Object Node_SetDraft(String token,Long workID) throws Exception {

        Dev2Interface.Port_LoginByToken(token);
        try{
            Dev2Interface.Node_SetDraft( workID);
            return Return_Info(200,"流程实例设置草稿成功","");
        }catch(Exception e){
            return Return_Info(500,"流程设置草稿失败","流程设置草稿失败:"+e.getMessage());
        }
    }
    /**
     * 指定给特定的移交人
     * @param token
     * @param workIDs
     * @param toEmpNo
     * @param msg
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/Node_Shift")
    @ApiOperation("移交")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workIDs", value = "流程实例WorkIDs(可填写多个WorkID使用\",\"拼接)", required = true),
            @ApiImplicitParam(name = "toEmpNo", value = "人员编号", required = true),
            @ApiImplicitParam(name = "msg", value = "移交原因", required = true)
    })
    public final Object Node_Shift(String token,String workIDs,String toEmpNo,String msg) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"移交失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(workIDs) == true)
            return Return_Info(500,"移交失败","流程实例WorkID值不能为空");
        if(DataType.IsNullOrEmpty(toEmpNo) == true)
            return Return_Info(500,"移交失败","指定的移交人不能为空");
        Dev2Interface.Port_LoginByToken(token);
        try{
            String[] splitWorkID = workIDs.split("[,]");
            String info = "";
            for(String workID : splitWorkID){
                info += Dev2Interface.Node_Shift(Long.parseLong(workID), toEmpNo, msg);
            }
            return  Return_Info(200,"移交成功", info);
        }catch(Exception e){
            return Return_Info(500,"设置工作移交失败","设置工作移交失败:"+e.getMessage());
        }
    }
    /**
     * 给指定的流程实例增加指定的待办人员
     * @param token
     * @param workID
     * @param empNo
     * @return 执行结果,
     * @throws Exception
     */
    @RequestMapping(value = "/Node_AddTodolist")
    @ApiOperation("给指定的流程实例增加指定的待办人员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例WorkID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "empNo", value = "要增加的人员编号", required = true)
    })
    public final Object Node_AddTodolist(String token,Long workID,String empNo) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"给指定的流程实例增加指定的待办人员失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(workID) == true)
            return Return_Info(500,"给指定的流程实例增加指定的待办人员失败","流程实例WorkID值不能为空");
        if(DataType.IsNullOrEmpty(empNo) == true)
            return Return_Info(500,"给指定的流程实例增加指定的待办人员失败","给指定的人增加待办，人员编号不能为空");
        Dev2Interface.Port_LoginByToken(token);
        try{

            Dev2Interface.Node_AddTodolist(workID,empNo );
            return  Return_Info(200,"给指定的人增加待办成功","");

        }catch(Exception e){
            return Return_Info(500,"给指定的人增加待办失败","给指定的人增加待办失败:"+e.getMessage());
        }
    }

    /**
     *
     * @param token
     * @param workID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/Flow_GenerWorkFlow")
    @ApiOperation("获取流程实例信息,节点状态,发起人,停留节点,当前人待办等.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例workID", dataType = "Long", required = true)
    })
    public final Object Flow_GenerWorkFlow(String token,Long workID) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"获取流程实例信息失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(workID) == true)
            return Return_Info(500,"获取流程实例信息失败","流程实例WorkID值不能为空");

        Dev2Interface.Port_LoginByToken(token);
        try{
            GenerWorkFlow gwf = new GenerWorkFlow(workID);

            return  Return_Info(200,"获取流程实例信息成功",gwf.ToJson());
        }catch(Exception e){
            return Return_Info(500,"获取流程实例信息失败","获取流程实例信息失败:"+e.getMessage());
        }
    }

    @RequestMapping(value = "/Node_SaveParas")
    @ApiOperation("保存参数:可以作为方向条件,接受人规则等参数.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例workID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "paras", value = "参数,格式@Key1=Val1@Key2=Val2比如,@Tel=18660153393@Addr=山东.济南@Age=35", required = true)
    })
    public final Object Node_SaveParas(String token,Long workID,String paras) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"保存参数失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(workID) == true)
            return Return_Info(500,"保存参数失败","流程实例WorkID值不能为空");
        if(DataType.IsNullOrEmpty(paras) == true)
            return Return_Info(500,"保存参数失败","参数Paras值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        try{
            Dev2Interface.Flow_SaveParas(workID, paras);
            return  Return_Info(200,"参数值保存成功","");
        }catch(Exception e){
            return Return_Info(500,"保存参数失败","参数值保存失败:"+e.getMessage());
        }
    }
    @RequestMapping(value = "/Node_SaveParasByMap")
    @ApiOperation("保存参数:可以作为方向条件,接受人规则等参数.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例workID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "paras", value = "参数,格式@Key1=Val1@Key2=Val2比如,@Tel=18660153393@Addr=山东.济南@Age=35", required = true)
    })
    public final Object Node_SaveParasByMap(@RequestBody Map<String, Object> map) throws Exception {
        String token = (String) map.get("token");
        Long workID = Long.valueOf((Integer) map.get("workId"));
        String paras = (String) map.get("paras");
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"保存参数失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(workID) == true)
            return Return_Info(500,"保存参数失败","流程实例WorkID值不能为空");
        if(DataType.IsNullOrEmpty(paras) == true)
            return Return_Info(500,"保存参数失败","参数Paras值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        try{
            Dev2Interface.Flow_SaveParas(workID, paras);
            return  Return_Info(200,"参数值保存成功","");
        }catch(Exception e){
            return Return_Info(500,"保存参数失败","参数值保存失败:"+e.getMessage());
        }
    }

    @RequestMapping(value = "/Node_SaveWorkByMap")
    @ApiOperation("保存到表单数据:可以作为方向条件,接受人规则等参数,用于发起流程或者中间节点对表单数据进行存储.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例workID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "keyVals", value = "参数,格式@Key1=Val1@Key2=Val2比如,@Tel=18660153393@Addr=山东.济南@Age=35", required = true),
            @ApiImplicitParam(name = "dtlJSON", value = "从表格式为一个json可以转化为DataSet模式,每个表名要与从表的ID对应才能保存.", required = false),
            @ApiImplicitParam(name = "dsAths", value = "附件格式为一个json可以转化为DataSet模式,FileName,FileUrl  文件名字,全路径文件路径. http://xxx.xxx.xxx/myfile.doc;.<br>" +
                    "例：{\"Ath1\":[{\"FileName\":\"我的附件1.doc\",\"FileUrl\":\"http://localst:9003:/xxx.docx\"},{\"FileName\":\"我的附件2.doc\",\"FileUrl\":\"http://localst:9003:/xxx.docx\"}],\"Ath2\":[{\"FileName\":\"我的附件3.doc\",\"FileUrl\":\"http://localst:9003:/xxx.docx\"},{\"FileName\":\"我的附件4.doc\",\"FileUrl\":\"http://localst:9003:/xxx.docx\"}]}", required = false)
    })
    public final Object Node_SaveWorkByMap(@RequestBody Map<String, Object> map) throws Exception {
        String token = (String) map.get("token");
        Long workID = Long.valueOf((String) map.get("workID"));
//    	Hashtable<String, String> keyVals = (Hashtable<String, String>) map.get("keyVals");

        Object docNoObj = map.get("keyVals");
        ObjectMapper mapper=new ObjectMapper();
        Hashtable<String, String> keyVals = mapper.convertValue(docNoObj, Hashtable.class);

        String dtlJSON = (String) map.get("dtlJSON");
        if(DataType.IsNullOrEmpty(token))
            return Return_Info(500,"保存工作失败","用户的 Token 值不能为空");
        if(DataType.IsNullOrEmpty(workID))
            return Return_Info(500,"保存工作失败","流程实例 WorkID值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        try{
//            AtPara at=new AtPara(keyVals);
            if (dtlJSON==null) {
                Dev2Interface.Node_SaveWork(workID, keyVals);
                return Return_Info(200, "保存工作成功", "");
            }

            DataSet ds= Json.ToDataSet(dtlJSON);//附件:FileName,FileUrl  文件名字,全路径文件路径. http://xxx.xxx.xxx/myfile.doc;
            String athsObj = (String) map.get("dsAths");
            if (DataType.IsNullOrEmpty(athsObj)) {
                Dev2Interface.Node_SaveWork(workID, keyVals,ds);
            } else {
                DataSet dsAths = Json.ToDataSet(athsObj);
                Dev2Interface.Node_SaveWork(workID, keyVals, ds, dsAths);
            }
            //bp.wf.Dev2Interface.Node_SaveWork(workID, keyVals,ds);
            return Return_Info(200, "保存工作成功", "");
        }catch(Exception e){
            return Return_Info(500,"保存工作失败","保存工作失败:"+e.getMessage());
        }
    }
    @RequestMapping(value = "/Node_SaveWork")
    @ApiOperation("保存到表单数据:可以作为方向条件,接受人规则等参数,用于发起流程或者中间节点对表单数据进行存储.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例workID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "keyVals", value = "参数,格式@Key1=Val1@Key2=Val2比如,@Tel=18660153393@Addr=山东.济南@Age=35", required = true),
            @ApiImplicitParam(name = "dtlJSON", value = "从表数据：从表的名称与表名对应，列名与从表的字段对应。", required = false),
            @ApiImplicitParam(name = "athJSON", value = "附件数据: 表名与附件的ID(短号NoOfObj标记)，列表格式：FileName(文件名), FileUrl(下载地址), 顺序不能变化, 文件名(包含文件后缀),下载地址(http:/xxxx/xxx.docx) ." +
                    "{\"Ath1\":[{\"FileName\":\"我的附件1.doc\",\"FileUrl\":\"http://localst:9003:/xxx.docx\"},{\"FileName\":\"我的附件2.doc\",\"FileUrl\":\"http://localst:9003:/xxx.docx\"}],\"Ath2\":[{\"FileName\":\"我的附件3.doc\",\"FileUrl\":\"http://localst:9003:/xxx.docx\"},{\"FileName\":\"我的附件4.doc\",\"FileUrl\":\"http://localst:9003:/xxx.docx\"}]}", required = false),

    })
    public final Object Node_SaveWork(String token,Long workID,String keyVals,String dtlJSON,String athJSON) throws Exception {
        if(DataType.IsNullOrEmpty(token))
            return Return_Info(500,"保存工作失败","用户的 Token 值不能为空");
        if(DataType.IsNullOrEmpty(workID))
            return Return_Info(500,"保存工作失败","流程实例 WorkID值不能为空");

        Dev2Interface.Port_LoginByToken(token);
        try{
            AtPara at=new AtPara(keyVals);

            DataSet dsDtls = null;
            if (dtlJSON != null)
                dsDtls = Json.ToDataSet(dtlJSON);

            DataSet dsAths = null;
            if (athJSON != null)
                dsAths = Json.ToDataSet(athJSON);
            if(DataType.IsNullOrEmpty(dsAths)){
                Dev2Interface.Node_SaveWork(workID, at.getHisHT(),dsDtls);
            }else {
                Dev2Interface.Node_SaveWork(workID, at.getHisHT(), dsDtls,dsAths);
            }
            return Return_Info(200, "保存工作成功", "");
        }catch(Exception e){
            return Return_Info(500,"保存工作失败","保存工作失败:"+e.getMessage());
        }
    }
    @RequestMapping(value = "/Node_OnlySaveAth")
    @ApiOperation("补充上传节点附件数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例workID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "athJSON", value = "附件数据: 表名与附件的ID(短号NoOfObj标记)，列表格式：FileName(文件名), FileUrl(下载地址), 顺序不能变化, 文件名(包含文件后缀),下载地址(http:/xxxx/xxx.docx) ." +
                    "{\"Ath1\":[{\"FileName\":\"我的附件1.doc\",\"FileUrl\":\"http://localst:9003:/xxx.docx\"},{\"FileName\":\"我的附件2.doc\",\"FileUrl\":\"http://localst:9003:/xxx.docx\"}],\"Ath2\":[{\"FileName\":\"我的附件3.doc\",\"FileUrl\":\"http://localst:9003:/xxx.docx\"},{\"FileName\":\"我的附件4.doc\",\"FileUrl\":\"http://localst:9003:/xxx.docx\"}]}", required = true),

    })
    public final Object Node_OnlySaveAth(String token,Long workID,String athJSON) throws Exception {
        if(DataType.IsNullOrEmpty(token))
            return Return_Info(500,"保存工作失败","用户的 Token 值不能为空");
        if(DataType.IsNullOrEmpty(workID))
            return Return_Info(500,"保存工作失败","流程实例 WorkID值不能为空");

        Dev2Interface.Port_LoginByToken(token);
        try{
            DataSet dsAths = null;
            if (athJSON != null)
                dsAths = Json.ToDataSet(athJSON);

            GenerWorkFlow gwf = new GenerWorkFlow(workID);
            Node nd = new Node(gwf.getNodeID());
            Work wk = nd.getHisWork();

            if (dsAths != null)
            {
                //定义对象.
                FrmAttachmentDBs dbs = new FrmAttachmentDBs();
                //保存从表
                for(DataTable dt : dsAths.Tables)
                {
                    for (FrmAttachment ath : wk.getHisFrmAttachments().ToJavaList())
                    {
                        if (dt.TableName.equals(ath.getNoOfObj()) == false)
                            continue;

                        //临时路径
                        String rootTempPath = SystemConfig.getPathOfTemp() + workID;
                        if ((new File(rootTempPath)).isDirectory() == false)
                        {
                            (new File(rootTempPath)).mkdirs();
                        }

                        String pkval = String.valueOf(gwf.getWorkID());
                        if(ath.getAthSaveWay() == AthSaveWay.SaveLink)
                        {
                            //求主键. 如果该表单挂接到流程上.
                            if (gwf.getNodeID() != 0)
                            {
                                //判断表单方案。
                                FrmNode fn = new FrmNode(gwf.getNodeID(), ath.getFrmID());
                                if (fn.getFrmSln() == FrmSln.Readonly)
                                    return "err@"+ath.getName()+"不允许上传附件.";
                                //是默认的方案的时候.
                                if (fn.getFrmSln() == FrmSln.Default)
                                {
                                    //判断当前方案设置的whoIsPk ，让附件集成 whoIsPK 的设置。
                                    if (fn.getWhoIsPK() == WhoIsPK.FID)
                                        pkval = String.valueOf(gwf.getFID());

                                    if (fn.getWhoIsPK() == WhoIsPK.PWorkID)
                                        pkval = String.valueOf(gwf.getPWorkID());
                                }
                                //自定义方案.
                                if (fn.getFrmSln() == FrmSln.Self)
                                {
                                    FrmAttachment athDesc = new FrmAttachment(ath.getMyPK() + "_" + gwf.getNodeID());
                                    if (athDesc.getHisCtrlWay() == AthCtrlWay.FID)
                                        pkval = String.valueOf(gwf.getFID());

                                    if (athDesc.getHisCtrlWay() == AthCtrlWay.PWorkID)
                                        pkval =  String.valueOf(gwf.getPWorkID());
                                }
                            }
                        }

                        // 复制数据.
                        for(DataRow dr : dt.Rows)
                        {
                            String fileName = dr.getValue(0)==null?"" :dr.getValue(0).toString(); //文件名称.
                            String fileUrl = dr.getValue(1)==null?"":dr.getValue(1).toString();  //文件路径. http://xxx.xxx.xxx/myfile.doc;
                            if (DataType.IsNullOrEmpty(fileUrl) == true || DataType.IsNullOrEmpty(fileName)==true)
                                return "err@附件的存储的URL或者附件名称不能为空";
                            try
                            {
                                //1.下载文件到本机.

                                String tempPath = rootTempPath + "/" + fileName;
                                java.net.URL url = new java.net.URL(fileUrl);
                                java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
                                con.setRequestMethod(con.getRequestMethod());
                                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                                con.setRequestProperty("User-Agent", "Mozilla/4.76");
                                con.setRequestProperty("connection", "keep-alive");
                                con.setDoOutput(true);
                                BufferedInputStream out = new BufferedInputStream(con.getInputStream());
                                File file = new File(tempPath);
                                file.createNewFile();
                                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempPath));
                                try {
                                    int bytes = 0;
                                    byte[] bufferOut = new byte[1024];
                                    while ((bytes = out.read(bufferOut)) != -1) {
                                        bos.write(bufferOut, 0, bytes);
                                    }
                                } catch (Exception e) {
                                    throw e;
                                } finally {
                                    out.close();
                                    bos.close();
                                }
                                //保存数据.
                                if(ath.getAthSaveWay() == AthSaveWay.SaveLink)
                                {
                                    FrmAttachmentDB athDB = new FrmAttachmentDB();
                                    athDB.setMyPK(DBAccess.GenerGUID());
                                    athDB.setSort("");
                                    athDB.setNodeID(gwf.getNodeID());
                                    athDB.setRefPKVal(pkval);
                                    athDB.setFrmID(ath.getFrmID());
                                    athDB.setFKFrmAttachment(ath.getMyPK());
                                    athDB.setFileName(fileName);
                                    athDB.setFileExts(fileName.substring(fileName.lastIndexOf(".")));
                                    File info = new File(tempPath);
                                    athDB.setFileSize((float)info.length());
                                    athDB.setFileFullName(fileUrl);
                                    athDB.setRDT(DataType.getCurrentDateTimess());
                                    athDB.setRec(bp.web.WebUser.getNo());
                                    athDB.setRecName(bp.web.WebUser.getName());
                                    athDB.setDeptNo(WebUser.getDeptNo());
                                    athDB.setDeptName(WebUser.getDeptName());
                                    athDB.Insert();
                                    info.delete();
                                    continue;
                                }
                                //保存附件到服务器
                                bp.wf.Dev2Interface.CCForm_AddAth(gwf.getNodeID(), gwf.getFlowNo(), workID, ath.getMyPK(), ath.getFrmID(), tempPath, fileName, null,"", gwf.getFID(), gwf.getPWorkID());

                            }
                            catch (Exception ex)
                            {
                            }
                        }
                    }
                }
            }
            //#endregion 保存附件.
            return Return_Info(200, "保存工作成功", "");
        }catch(Exception e){
            return Return_Info(500,"保存工作失败","保存工作失败:"+e.getMessage());
        }
    }
    @RequestMapping(value = "/Flow_SetTitle")
    @ApiOperation("设置标题:流程实例的标题,也可以使用流程属性的标题生成规则生成.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例workID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "title", value = "要设置的标题", required = true),
            @ApiImplicitParam(name = "flowNo", value = "流程编号、标记", required = false)
    })
    public final Object Flow_SetTitle(String token,Long workID,String title,String flowNo) throws Exception {

        Dev2Interface.Port_LoginByToken(token);
        try{
            Dev2Interface.Flow_SetFlowTitle(flowNo, workID, title);
            return  Return_Info(200,"标题设置成功","");
        }catch(Exception e){
            return Return_Info(500,"设置标题失败","标题设置失败:"+e.getMessage());
        }
    }

    @RequestMapping(value = "/Node_SendWork")
    @ApiOperation("执行发送:从一个节点运动到下一个节点.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例workID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "ht", value = "主表单数据，JSON格式", required = false),
            @ApiImplicitParam(name = "dtls", value = "从表数据json格式，多个从表,没有可为null", required = false),
            @ApiImplicitParam(name = "paras", value = "参数，保存到WF_GenerWorkFlow,用与参数条件", required = false),
            @ApiImplicitParam(name = "toEmps", value = "接受人:设置空表示,根据到达的节点的接受人规则计算接收人,多个接受人用逗号分开,比如:zhangsan,lisi", required = false),
            @ApiImplicitParam(name = "toNodeIDStr", value = "到达的下一个节点,默认为0设置0表示让ccbpm根据方向条件判断方向,可以是节点Mark", required = false),
            @ApiImplicitParam(name = "checkNote", value = "审核意见:启用了审核组件，就需要填写审核意见，在节点属性=》表单=》审核组件设置。", required = false)
    })
    public final Object Node_SendWork(String token,Long workID,String ht,String dtls, String toEmps,String paras,String toNodeIDStr, String checkNote) throws Exception {
        if(DataType.IsNullOrEmpty(token))
            return Return_Info(500,"执行发送失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(workID))
            return Return_Info(500,"执行发送失败","流程实例WorkID值不能为空");
        if(!DataType.IsNullOrEmpty(ht) && !DataType.IsJson(ht))
            return Return_Info(500,"执行发送失败","输入的ht参数不是JSON格式");

        try{
            Dev2Interface.Port_LoginByToken(token);
            //保存参数.
            if (!DataType.IsNullOrEmpty(paras))
                Dev2Interface.Flow_SaveParas(workID, paras);

            //写入审核意见.
            if (!DataType.IsNullOrEmpty(checkNote))
                Dev2Interface.Node_WriteWorkCheck(workID, checkNote, null, null, null);
            Paras pens = new Paras();
            pens.SQL="SELECT FK_Flow FROM WF_GenerWorkFlow WHERE workID=" +SystemConfig.getAppCenterDBVarStr() + "v";
            pens.Add("v",workID);
            String  flowNo = DBAccess.RunSQLReturnString(pens);
            int toNodeID = DealNodeIDStr(toNodeIDStr, flowNo);
            //执行发送.
            SendReturnObjs objs = Dev2Interface.Node_SendWork(flowNo, workID, DataType.IsNullOrEmpty(ht) == true ? null : DataType.ToHashtable(ht), null, toNodeID, toEmps);

            return  Return_Info(200,"执行发送成功",objs.ToMsgOfText());
        }catch(Exception e){
            return Return_Info(500,"执行发送失败",e.getMessage());
        }
    }
    @RequestMapping(value = "/Node_SendWork_ReJson")
    @ApiOperation("发送接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例workID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "flowNo", value = "流程编号", required = false),
            @ApiImplicitParam(name = "toNodeID", value = "到达节点ID,空时传0", dataType = "Int", required = false),
            @ApiImplicitParam(name = "toEmps", value = "到达人员编号，多人以,隔开", required = false)
    })
    public final Object Node_SendWork_ReJson(String token,Long workID,String flowNo,Integer toNodeID,String toEmps) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"执行发送失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(workID) == true)
            return Return_Info(500,"执行发送失败","流程实例WorkID值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        //执行发送.
        Hashtable ht = new Hashtable();
        Enumeration enu = ContextHolderUtils.getRequest().getParameterNames();
        while (enu.hasMoreElements()) {
            String str = (String) enu.nextElement();
            if (DataType.IsNullOrEmpty(str) == true) {
                continue;
            }
            String val = this.GetValByKey(str);
            if (val != null)
                ht.put(str, val);
        }
        try{
            String fk_flow = flowNo;
            if (DataType.IsNullOrEmpty(fk_flow) == true){
                Paras pens = new Paras();
                pens.SQL="SELECT FK_Flow FROM WF_GenerWorkFlow WHERE workID=" +SystemConfig.getAppCenterDBVarStr() + "v";
                pens.Add("v",workID);
                fk_flow = DBAccess.RunSQLReturnString(pens);
            }

            //执行发送.
            SendReturnObjs objs = Dev2Interface.Node_SendWork(fk_flow, workID, ht, null, toNodeID, toEmps);

            return  Return_Info(200,"执行发送成功",objs.ToJson());
        }catch(Exception e){
            return Return_Info(500,"执行发送失败",e.getMessage());
        }
    }
    @RequestMapping(value = "/DB_GenerWillReturnNodes")
    @ApiOperation("退回到的节点:执行退回的时候,首先要选择可以退回的节点,放入到下拉框让操作员选择.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例workID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "fid", value = "流程ID", dataType = "Long", required = false),
            @ApiImplicitParam(name = "nodeID", value = "节点ID", dataType = "Int", required = true)
    })
    public final Object DB_GenerWillReturnNodes(String token,Long workID,Long fid,Integer nodeID) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"获取可以退回的节点失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(workID) == true)
            return Return_Info(500,"获取可以退回的节点失败","流程实例WorkID值不能为空");
        if(DataType.IsNullOrEmpty(nodeID) == true || nodeID.intValue() == 0)
            return Return_Info(500,"获取可以退回的节点失败","节点编号FK_Node值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        try{
            //获得可以退回的节点.
            DataTable dt = Dev2Interface.DB_GenerWillReturnNodes(workID);
            return  Return_Info(200,"获取可以退回的节点成功", Json.ToJson(dt));
        }catch(Exception e){
            return Return_Info(500,"获取可以退回的节点失败","获取可以退回的节点失败:"+e.getMessage());
        }
    }

    @RequestMapping(value = "/Node_ReturnWork")
    @ApiOperation("执行退回:退回与发送是相反的操作,在ccbpm没有同意不同意概念,只有前进后退.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例workID", required = true),
            @ApiImplicitParam(name = "returnToNodeIDStr", value = "退回到节点ID，空时传0", required = false),
            @ApiImplicitParam(name = "returnToEmp", value = "退回到人员编号,标记:退回当前节点的所有人：AllEmps", required = false),
            @ApiImplicitParam(name = "msg", value = "退回原因", required = true),
            @ApiImplicitParam(name = "isBackToThisNode", value = "退回到某个节点后再次发送时是否直接发送给退回的节点, true/false", dataType = "Boolean", required = false)
    })
    public final Object Node_ReturnWork(String token, long workID, String returnToNodeIDStr, String returnToEmp, String msg, boolean isBackToThisNode) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"执行退回失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(workID) == true)
            return Return_Info(500,"执行退回失败","流程实例WorkID值不能为空");
        if(DataType.IsNullOrEmpty(msg) == true)
            return Return_Info(500,"执行退回失败","退回原因Msg值不能为空");

        Dev2Interface.Port_LoginByToken(token);
        try{
            Paras pens = new Paras();
            pens.SQL="SELECT FK_Flow FROM WF_GenerWorkFlow WHERE workID=" +SystemConfig.getAppCenterDBVarStr() + "v";
            pens.Add("v",workID);
            String  flowNo = DBAccess.RunSQLReturnString(pens);
            int returnToNodeID = DealNodeIDStr(returnToNodeIDStr, flowNo);

            if(returnToNodeID == 0)
            {
                String empstr ="";
                DataTable dt = Dev2Interface.DB_GenerWillReturnNodes(workID);
                if (dt.Rows.size() == 1)
                {
                    returnToNodeID = Integer.parseInt(dt.Rows.get(0).getValue("No").toString());
                    returnToEmp = dt.Rows.get(0).getValue("Rec").toString();
                }
                else {
                    returnToNodeID = Integer.parseInt(dt.Rows.get(0).getValue("No").toString());
                }
            }

            return  Return_Info(200,"执行退回成功",
                    Dev2Interface.Node_ReturnWork(workID,  returnToNodeID, returnToEmp, msg, isBackToThisNode));
        }catch(Exception e){
            e.printStackTrace();
            return Return_Info(500,"执行退回失败",e.getMessage());
        }
    }

    @RequestMapping(value = "/Search_Init")
    @ApiOperation("流程实例查询:对流程注册表的查询,WF_GenerWorkFlow请参考表结构.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "scop", value = "流程实例workID", required = false),
            @ApiImplicitParam(name = "key", value = "0:我参与的，1:我发起的，2:我部门发起的", required = false),
            @ApiImplicitParam(name = "dtFrom", value = "日期从", required = false),
            @ApiImplicitParam(name = "dtTo", value = "日期到", required = false),
            @ApiImplicitParam(name = "pageIdx", value = "页码", dataType = "Int", required = false)
    })
    public final Object Search_Init(String token,String scop,String key,String dtFrom,String dtTo,Integer pageIdx) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"流程实例查询失败","用户的Token值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        try{
            return  Return_Info(200,"流程实例查询成功",Search_Init(scop,key,dtFrom,dtTo,pageIdx));
        }catch(Exception e){
            return Return_Info(500,"流程实例查询失败",e.getMessage());
        }
    }

     @RequestMapping(value = "/GenerFrmUrl")
     @ApiOperation("获得表单URL:对流程注册表的查询,WF_GenerWorkFlow请参考表结构.")
     @ApiImplicitParams({
             @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
             @ApiImplicitParam(name = "workID", value = "流程实例workID",dataType = "Long",required = true),
             @ApiImplicitParam(name = "FK_Flow", value = "流程编号", required = true),
             @ApiImplicitParam(name = "FK_Node", value = "当前节点编号", dataType = "Int",required = false)
     })
     public final Object GenerFrmUrl(@RequestParam("token") String token,@RequestParam("workID") long workID, @RequestParam("flowNo")String FK_Flow, @RequestParam("nodeID")Integer FK_Node) throws Exception {
         if(DataType.IsNullOrEmpty(token) == true)
             return "err@用户的Token值不能为空";
         if(DataType.IsNullOrEmpty(workID) == true)
             return "err@流程实例workID值不能为空";
         if(DataType.IsNullOrEmpty(FK_Flow) == true)
             return "err@流程编号FK_Flow值不能为空";
         Dev2Interface.Port_LoginByToken(token);
         try{
             return GenerFrmUrl(workID,FK_Flow,FK_Node==null?0:FK_Node.intValue(),token);
         }catch(Exception e){
             return "err@获取可以发起的流程列表失败:"+e.getMessage();
         }
     }

    @RequestMapping(value = "/DB_Start")
    @ApiOperation("获得可发起的流程:一个可以发起的流程由他的身份决定的,在开始节点的右键属性里设置.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "domainExt", value = "域:可传空,比如:CRM,OA,ERP等,配置在流程目录属性上.", required = false)
    })
    public final Object DB_Start(String token,String domainExt) throws Exception {

        Dev2Interface.Port_LoginByToken(token);
        try{
            String userNo = WebUser.getNo();
            if(SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS){
                userNo = WebUser.getOrgNo() + "_" + WebUser.getNo();;
            }
            DataTable dtStrat = Dev2Interface.DB_StarFlows(userNo,domainExt);
            return  Return_Info(200,"流程实例查询成功", Json.ToJson(dtStrat));
        }catch(Exception e){
            return Return_Info(500,"获取可以发起的流程列表失败",e.getMessage());
        }
    }

    @RequestMapping(value = "/DB_Draft")
    @ApiOperation("草稿:暂存的表单,请参考流程属性草稿规则.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "flowNo", value = "流程模板编号", required = false),
            @ApiImplicitParam(name = "domainExt", value = "域:可传空,比如:CRM,OA,ERP等,配置在流程目录属性上.", required = false)
    })
    public final Object DB_Draft(String token,String flowNo,String domainExt) throws Exception {

        Dev2Interface.Port_LoginByToken(token);
        try{
            DataTable dtDraft = Dev2Interface.DB_GenerDraftDataTable(flowNo,domainExt);
            return  Return_Info(200,"获取草稿列表成功", Json.ToJson(dtDraft));
        }catch(Exception e){
            return Return_Info(500,"获取草稿列表失败",e.getMessage());
        }
    }

    @RequestMapping(value = "/DB_Todolist")
    @ApiOperation("待办:等待我解决的问题,包括发送、转发、移交、撤销的工作.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "flowNo", value = "流程模板编号:要取得指定流程的待办就传入.", required = false),
            @ApiImplicitParam(name = "nodeID", value = "节点ID:要取得指定节点的待办就传入,默认为0.", dataType = "Int", required = false),
            @ApiImplicitParam(name = "domainExt", value = "域:可传空,比如:CRM,OA,ERP等,配置在流程目录属性上.", required = false)
    })

    public final Object DB_Todolist(String token,String domainExt ,String flowNo,Integer nodeID) throws Exception {

        Dev2Interface.Port_LoginByToken(token);
        try{
            DataTable dtTodolist = Dev2Interface.DB_GenerEmpWorksOfDataTable(WebUser.getNo(), 0,null, domainExt,flowNo,null);
            return  Return_Info(200,"获取待办列表成功",  Json.ToJson(dtTodolist));
        }catch(Exception e){
            return Return_Info(500,"获取待办列表失败",e.getMessage());

        }
    }

    @RequestMapping(value = "/DB_TodolistByIsRead")
    @ApiOperation("待办(待阅/已阅)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "isRead", value = "0=待阅，1=已阅", required = true),
            @ApiImplicitParam(name = "domainExt", value = "域:可传空,比如:CRM,OA,ERP等,配置在流程目录属性上.", required = false)
    })
    public final Object DB_TodolistByIsRead(String token, String isRead, String domainExt ) throws Exception {

        Dev2Interface.Port_LoginByToken(token);
        try{
            DataTable dtTodolist = Dev2Interface.DB_GenerEmpWorksOfDataTable(WebUser.getNo(), 0,null, domainExt, null, isRead, null);
            return  Return_Info(200,"获取待办列表成功",  Json.ToJson(dtTodolist));
        }catch(Exception e){
            return Return_Info(500,"获取待办列表失败",e.getMessage());

        }
    }

    @RequestMapping(value = "/DB_Runing")
    @ApiOperation("在途:我参与的流程但是该流程没有走完,可以执行催办与撤销操作.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "domainExt", value = "域:可传空,比如:CRM,OA,ERP等,配置在流程目录属性上.", required = false)
    })
    public final Object DB_Runing(String token,String domainExt) throws Exception {

        Dev2Interface.Port_LoginByToken(token);
        try{
            DataTable dtRuing = Dev2Interface.DB_GenerRuning(WebUser.getNo(), false, domainExt);
            return  Return_Info(200,"获取在途列表成功", Json.ToJson(dtRuing));
        }catch(Exception e){
            return Return_Info(500,"获取在途列表失败",e.getMessage());
        }
    }


    @RequestMapping(value="Flow_RecentWorkInit")
    @ApiOperation("近期:近期发起的流程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
    })
    public final Object Flow_RecentWorkInit(String token){
        Dev2Interface.Port_LoginByToken(token);
        try {
            return Return_Info(200,"获取近期列表成功", Dev2Interface.Flow_RecentWorkInit());
        }catch(Exception e) {
            return Return_Info(500,"获取近期列表失败",e.getMessage());
        }
    }

    @RequestMapping(value="Batch_Init")
    @ApiOperation("批处理:批量处理流程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
    })
    public final Object Batch_Init(String token){
        Dev2Interface.Port_LoginByToken(token);
        try {
            return Return_Info(200,"获取批处理列表成功", new WF().Batch_Init());
        }catch(Exception e) {
            return Return_Info(500,"获取批处理列表失败",e.getMessage());
        }
    }

    @RequestMapping(value = "/DB_CCListByIsRead")
    @ApiOperation("根据已读未读参数获取抄送列表数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "isRead", value = "是否已读，0=未读，1=已读", required = true)
    })
    public final Object DB_CCListByIsRead(String token,String isRead) throws Exception {

        try{
            //根据token登录
            Dev2Interface.Port_LoginByToken(token);

            DataTable dt = new DataTable();
            String msg = "";
            if ("0".equals(isRead))
            {
                dt = Dev2Interface.DB_CCList_UnRead(WebUser.getNo());
                msg = "获取未读抄送列表数据成功";
            }

            if ("1".equals(isRead))
            {
                dt = Dev2Interface.DB_CCList_Read();
                msg = "获取已读抄送列表数据成功";
            }

            return Return_Info(200, msg, Json.ToJson(dt));
        }catch(Exception e){
            String err = "接口[DB_CCListByIsRead]获取数据失败，参数Token=" + token + " isRead=" + isRead;
            return Return_Info(500, err, e.getMessage());
        }
    }

    @RequestMapping(value = "/DB_CCList")
    @ApiOperation("抄送:发送给别人,知会给我的工作.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "domainExt", value = "域:可传空,比如:CRM,OA,ERP等,配置在流程目录属性上.", required = false)
    })
    public final Object DB_CCList(String token,String domainExt) throws Exception {

        Dev2Interface.Port_LoginByToken(token);
        try{
            DataTable dtCC = Dev2Interface.DB_CCList(domainExt);
            return  Return_Info(200,"获取抄送列表成功", Json.ToJson(dtCC));
        }catch(Exception e){
            return Return_Info(500,"获取抄送列表失败",e.getMessage());
        }
    }

    @RequestMapping(value = "/DB_CCListBySta")
    @ApiOperation("抄送:发送给别人,知会给我的工作.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "sta", value = "状态,枚举类型:0 未读;1 已读;2 已回复;3 删除;", dataType = "Int", required = false),
            @ApiImplicitParam(name = "domainExt", value = "域:可传空,比如:CRM,OA,ERP等,配置在流程目录属性上.", required = false)
    })
    public final Object DB_CCListBySta(String token,Integer sta, String domainExt) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"根据抄送状态获取抄送列表失败","用户的Token值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        try{
            DataTable dtCC = Dev2Interface.DB_CCList(CCSta.forValue(sta==null?0:sta.intValue()),domainExt);

            return  Return_Info(200,"根据抄送状态获取抄送列表成功", Json.ToJson(dtCC));
        }catch(Exception e){
            return Return_Info(500,"根据抄送状态获取抄送列表失败",e.getMessage());
        }
    }

    @RequestMapping(value = "/Node_CC_WriteTo_CClist")
    @ApiOperation("执行抄送:写入抄送的方法")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例workID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "nodeID", value = "当前节点ID", dataType = "Int", required = true),
            @ApiImplicitParam(name = "title", value = "标题", required = true),
            @ApiImplicitParam(name = "doc", value = "内容", required = true),
            @ApiImplicitParam(name = "emps", value = "人员编号,多人以;隔开 例如： zhanghaicheng,张海成;lisi,李四;", required = false),
            @ApiImplicitParam(name = "depts", value = "部门编号,多个以,隔开", required = false),
            @ApiImplicitParam(name = "stations", value = "岗位编号,多个以,隔开", required = false)
    })
    public final Object Node_CC_WriteTo_CClist(String token,Long workID,Integer nodeID,String title, String doc,String emps,String depts,String stations) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"执行抄送失败","用户的Token值不能为空");
        if(nodeID==null || nodeID.intValue() == 0)
            return Return_Info(500,"执行抄送失败","当前节点编号FK_Node值不能为空或者0");
        Dev2Interface.Port_LoginByToken(token);
        try{
            //抄送给指定的人 格式：zhangsan,张三;lisi,李四；，部门 格式：1001;10002。岗位  格式 1001;1002
            if(DataType.IsNullOrEmpty(title)==true)
                title="来自"+ WebUser.getNo()+"的抄送信息.";
            if(DataType.IsNullOrEmpty(doc)==true)
                doc="来自"+ WebUser.getNo()+"的抄送信息.";

            String ccRec = Dev2Interface.Node_CC_WriteTo_CClist(nodeID==null?0:nodeID.intValue(), workID, title, doc,  emps,
                    depts ,stations, null);
            if(DataType.IsNullOrEmpty(ccRec)==true)
                return Return_Info(500,"没有设置可以抄送的人员","");

            return Return_Info(200,"执行抄送成功",ccRec);
        }catch(Exception e){
            return Return_Info(500,"执行抄送失败",e.getMessage());
        }
    }

    @RequestMapping(value = "/Flow_DoPress")
    @ApiOperation("催办:提醒当前流程的节点处理人,及时处理工作.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workIDs", value = "流程实例workID集合:多个以,隔开", required = true),
            @ApiImplicitParam(name = "msg", value = "催办信息", required = false)
    })
    public final Object Flow_DoPress(String token,String workIDs,String msg) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"催办失败","用户的Token值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        try{
            String[] strs = workIDs.split(",");
        if (DataType.IsNullOrEmpty(msg))
            msg = "需要您处理待办工作.";
        String info = "";
        for (String workIDStr : strs)
        {
            if (DataType.IsNullOrEmpty(workIDStr) == true)
                continue;
            info += "@" + Dev2Interface.Flow_DoPress(Integer.parseInt(workIDStr), msg, true);
        }
            return Return_Info(200,"催办成功",info);
        }catch(Exception e){
            return Return_Info(500,"催办失败",e.getMessage());
        }
    }

    /**
     * 批量抄送审核.
     * @param token
     * @param workIDs
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/CC_BatchCheckOver")
    @ApiOperation("抄送批量阅知:批量阅知已读.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workIDs", value = "流程实例workID集合:多个以,隔开", required = true)
    })
    public final Object CC_BatchCheckOver(String token,String workIDs) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"抄送批量阅知失败","用户的Token值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        if(DataType.IsNullOrEmpty(workIDs) == true)
            return Return_Info(500,"抄送批量阅知失败","批量阅读完毕的workIDs不能为空");
        try{
            return Return_Info(200,"抄送批量阅知成功",this.CC_BatchCheckOver(workIDs));
        }catch(Exception e){
            return Return_Info(500,"抄送批量阅知失败",e.getMessage());
        }
    }

    @RequestMapping(value = "/Flow_BatchDeleteByFlag")
    @ApiOperation("批量逻辑删除流程实例:对流程实例在WF_GenerWorkFlow的WFState设置删除标记=7.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workIDs", value = "流程实例workID集合:多个以,隔开", required = true)
    })
    public final Object Flow_BatchDeleteByFlag(String token,String workIDs) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"批量逻辑删除流程实例失败","用户的Token值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        if(DataType.IsNullOrEmpty(workIDs) == true)
            return Return_Info(500,"批量逻辑删除流程实例失败","批删除workIDs值不能为空");
        try{
              String[] strs = workIDs.split(",");
        for (String workIDStr : strs)
        {
            if (DataType.IsNullOrEmpty(workIDStr) == true)
                continue;
            Dev2Interface.Flow_DoDeleteFlowByFlag(Long.parseLong(workIDStr), "删除", true);
        }
        return Return_Info(200,"删除成功","");
        }catch(Exception e){
            return Return_Info(500,"批量逻辑删除流程实例失败",e.getMessage());
        }
    }

    @RequestMapping(value = "/Flow_BatchDeleteByReal")
    @ApiOperation("批量物理删除流程实例:对流程实例在WF_GenerWorkFlow里直接删除,并删除相关的轨迹,工作人员,业务表数据.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workIDs", value = "流程实例workID集合,多个以,隔开", required = true)
    })
    public final Object Flow_BatchDeleteByReal(String token,String workIDs) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"批量物理删除流程实例失败","用户的Token值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        if(DataType.IsNullOrEmpty(workIDs) == true)
            return Return_Info(500,"批量物理删除流程实例失败","批删除workIDs值不能为空");
        try{

              String[] strs = workIDs.split(",");
        for (String workIDStr : strs)
        {
            if (DataType.IsNullOrEmpty(workIDStr) == true)
                continue;
            Dev2Interface.Flow_DoDeleteFlowByReal(Long.parseLong(workIDStr), true);
        }
        return Return_Info(200,"删除成功","");
        }catch(Exception e){
            return Return_Info(500,"批量物理删除流程实例失败",e.getMessage());
        }
    }

    @RequestMapping(value = "/Flow_BatchDeleteByFlagAndUnDone")
    @ApiOperation("批量撤销删除的流程:对已经逻辑删除流程实例在恢复正常状态.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workIDs", value = "流程实例workID集合:多个以,隔开", required = true)
    })
    public final Object Flow_BatchDeleteByFlagAndUnDone(String token, String  workIDs) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"批量撤销删除的流程失败","用户的Token值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        if(DataType.IsNullOrEmpty(workIDs) == true)
            return Return_Info(500,"批量撤销删除的流程失败","批删除workIDs值不能为空");
        try{
            String[] strs = workIDs.split(",");
            for (String workIDStr : strs)
            {
                if (DataType.IsNullOrEmpty(workIDStr) == true)
                    continue;
                Dev2Interface.Flow_DoUnDeleteFlowByFlag(null, Integer.parseInt(workIDStr), "删除");
            }
            return Return_Info(200,"恢复成功","");
        }catch(Exception e){
            return Return_Info(500,"批量撤销删除的流程失败",e.getMessage());
        }
    }
    @RequestMapping(value = "/Flow_DoUnSend")
    @ApiOperation("撤销流程实例:撤销后当前工作返回自己的待办列表里.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workIDs", value = "流程实例workID集合:多个以,隔开", required = true)
    })
    public final Object Flow_DoUnSend(String token, String workIDs) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"撤销流程实例失败","用户的Token值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        if(DataType.IsNullOrEmpty(workIDs) == true)
            return Return_Info(500,"撤销流程实例失败","批撤销workIDs值不能为空");
        try{

            String[] strs = workIDs.split(",");

            String info = "";
            for (String workIDStr : strs)
            {
                if (DataType.IsNullOrEmpty(workIDStr) == true)
                {
                    continue;
                }

                info += Dev2Interface.Flow_DoUnSend(null, Long.parseLong(workIDStr), 0, 0);
            }
            return Return_Info(200,"撤销流程实例成功",info);
        }catch(Exception e){
            return Return_Info(500,"批量撤销流程失败",e.getMessage());
        }
    }


    @RequestMapping(value = "/Flow_DeleteDraft")
    @ApiOperation("删除草稿:从草稿箱里执行批量删除操作,请参考流程属性,草稿规则.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workIDs", value = "流程实例workID集合:多个以,隔开", required = true)
    })
    public final Object Flow_DeleteDraft(String token, String  workIDs) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"批量删除草稿失败","用户的Token值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        if(DataType.IsNullOrEmpty(workIDs) == true)
            return Return_Info(500,"批量删除草稿失败","批量删除草稿workIDs值不能为空");
        try{
            return Return_Info(200,"批量删除草稿成功", this.Flow_DeleteDraft(workIDs));
        }catch(Exception e){
            return Return_Info(500,"批量删除草稿失败",e.getMessage());
        }
    }
    @RequestMapping(value = "/Flow_DoFlowOver")
    @ApiOperation("执行流程结束:对流程注册表WF_GenerWorkFlow的WFState设置为结束状态.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workIDs", value = "流程实例workID集合:多个以,隔开", required = true)
    })
    public final Object Flow_DoFlowOver(String token, String workIDs) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"批量删除草稿失败","用户的Token值不能为空");
        Dev2Interface.Port_LoginByToken(token);
        try{

            String[] strs = workIDs.split(",");

            String info = "";
            for (String workIDStr : strs)
            {
                if (DataType.IsNullOrEmpty(workIDStr) == true)
                    continue;
                Dev2Interface.Flow_DoFlowOver(Long.parseLong(workIDStr), "批量结束",WFState.CompleteEnd);
            }
            return Return_Info(200,"执行成功","执行成功");
        }catch(Exception e){
            return Return_Info(500,"批量结束流程实例失败",e.getMessage());
        }
    }

    @RequestMapping(value = "/Node_GetNextStepNodesByNodeID")
    @ApiOperation("根据节点编号获得到达节点的集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "nodeID", value = "节点ID", dataType = "Int", required = true)
    })
    public final Object Node_GetNextStepNodesByNodeID(String token,Integer nodeID) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"获得到达节点的集合失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(nodeID) == true || nodeID == 0)
            return Return_Info(500,"获得到达节点的集合失败","节点编号nodeID不能为空");

        Dev2Interface.Port_LoginByToken(token);
        try{
            //获得可以退回的节点.
            Directions dirs = Dev2Interface.Node_GetNextStepNodesByNodeID(nodeID);
            return Return_Info(200,"获得到达节点的执行成功",dirs.ToJson());
        }catch(Exception e){
            return Return_Info(500,"获得到达节点的集合失败",e.getMessage());
        }
    }
    @RequestMapping(value = "/Node_GetNextStepEmpsByNodeID")
    @ApiOperation("获得到指定节点上的工作人员集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "nodeID", value = "节点ID", dataType = "Int", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例ID,可为0，系统自动查询", dataType = "Int", required = true)
    })
    public final Object Node_GetNextStepEmpsByNodeID(String token,Integer nodeID, Integer workID) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"获得到指定节点上的工作人员集合失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(nodeID) == true || nodeID == 0)
            return Return_Info(500,"获得到指定节点上的工作人员集合失败","节点编号nodeID不能为空");
        if(DataType.IsNullOrEmpty(workID) == true)
            return Return_Info(500,"获得到指定节点上的工作人员集合失败","流程实例workID不能为空，可为0");
        Dev2Interface.Port_LoginByToken(token);
        try{
            //获得可以退回的节点.
            DataTable dt = Dev2Interface.Node_GetNextStepEmpsByNodeID(nodeID,workID);
            return Return_Info(200,"获得到指定节点上的工作人员集合成功", Json.ToJson(dt));
        }catch(Exception e){
            return Return_Info(500,"获得到指定节点上的工作人员集合失败",e.getMessage());
        }
    }
    @RequestMapping(value = "/Node_GetNextStepNodesByWorkID")
    @ApiOperation("根据流程编号获得到达节点的集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例ID", dataType = "Int", required = true)
    })
    public final Object  Node_GetNextStepNodesByWorkID(String token,Integer workID) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"根据流程编号获得到达节点的集合失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(workID) == true)
            return Return_Info(500,"根据流程编号获得到达节点的集合失败","流程实例IDworkID不能为空");

        Dev2Interface.Port_LoginByToken(token);
        try{
            //获得可以退回的节点.
            Directions dirs = Dev2Interface.Node_GetNextStepNodesByWorkID(workID);
            return Return_Info(200,"根据流程编号获得到达节点的集合成功",  dirs.ToJson());
        }catch(Exception e){
            return Return_Info(500,"根据流程编号获得到达节点的集合失败",e.getMessage());
        }
    }

    /**
     * 会签初始化，获取主持人，加签人及处理的当前流程的状态
     * @param workID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/HuiQian_Init")
    @ApiOperation("会签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例ID", dataType = "Long", required = true)
    })
    public final Object  HuiQian_Init(String token,Long workID) throws Exception {
        //根据token执行登录
        Dev2Interface.Port_LoginByToken(token);

        if(DataType.IsNullOrEmpty(workID) == true)
            return Return_Info(500,"获取会签初始化数据失败","流程实例workID不能为空");
        try{
            DataSet ds = Dev2Interface.Node_HuiQian_Init(workID);

            return Return_Info(200,"获取会签初始化数据成功",  Json.ToJson(ds));
        }catch(Exception e){
            return Return_Info(500,"获取会签初始化数据失败",e.getMessage());
        }
    }


    @RequestMapping(value = "/HuiQian_Done")
    @ApiOperation("会签:执行发送")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "toNodeIDStr", value = "到达节点ID，为空时传0", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例ID", dataType = "Long", required = true)
    })
    public final Object  HuiQian_Done(String token,Long workID,String toNodeIDStr ) throws Exception {
       //根据token执行登录
        Dev2Interface.Port_LoginByToken(token);
        GenerWorkFlow gwf = new GenerWorkFlow(workID);
        int toNodeID = DealNodeIDStr(toNodeIDStr, gwf.getFlowNo());

        try
        {
            Dev2Interface.Node_HuiQianDone(workID, toNodeID);
            return Return_Info(200, "会签执行发送成功", "执行成功");
        }
        catch (Exception e)
        {
            return Return_Info(500, "会签执行发送失败", e.getMessage());
        }
    }

    @RequestMapping(value = "/HuiQian_AddEmps")
    @ApiOperation("增加会签人")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例ID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "huiQianType", value = "会签类型，huiQianType=AddLeader增加组长,可为空", required = false),
            @ApiImplicitParam(name = "empStrs", value = "选择人员,多人使用,隔开", required = true)
    })
    public final Object  HuiQian_AddEmps(String token,Long workID, String huiQianType, String empStrs) throws Exception {
        //根据token执行登录
        Dev2Interface.Port_LoginByToken(token);

        if(DataType.IsNullOrEmpty(workID) == true)
            return Return_Info(500,"增加会签人失败","流程实例workID不能为空");
        if (DataType.IsNullOrEmpty(empStrs) == true)
            return Return_Info(500,"增加会签人失败","您没有选择人员");
        try{
            return Return_Info(200,"增加会签人成功",  Dev2Interface.Node_HuiQian_AddEmps(workID,huiQianType, empStrs));
        }catch(Exception e){
            return Return_Info(500,"增加会签人失败",e.getMessage());
        }
    }
    @RequestMapping(value = "/HuiQian_DeleteEmps")
    @ApiOperation("删除会签人员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例ID", dataType = "Long", required = true),
            @ApiImplicitParam(name = "empStrs", value = "选择人员,多人使用,隔开", required = true)
    })
    public final Object  HuiQian_DeleteEmps(String token,Long workID, String empStrs) throws Exception {
        //根据token执行登录
        Dev2Interface.Port_LoginByToken(token);

        if(DataType.IsNullOrEmpty(workID) == true)
            return Return_Info(500,"删除会签人员失败","流程实例workID不能为空");
        if (DataType.IsNullOrEmpty(empStrs) == true)
            return Return_Info(500,"删除会签人员失败","您没有选择人员");
        try{
            Dev2Interface.Node_HuiQian_Delete(workID, empStrs);
            return Return_Info(200,"删除成功",  "");
        }catch(Exception e){
            return Return_Info(500,"删除会签人员失败",e.getMessage());
        }
    }

    @RequestMapping(value = "/WorkFlow_State")
    @ApiOperation("根据WorkID获取流程实例状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "workID", value = "流程实例ID", dataType = "Long", required = true)
    })
    public final Object  WorkFlow_State(String token,Long workID) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"根据WorkID获取流程实例状态失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(workID) == true)
            return Return_Info(500,"根据WorkID获取流程实例状态失败","流程实例workID不能为空");

        Dev2Interface.Port_LoginByToken(token);
        //判断是否有查看权限
        if (Dev2Interface.Flow_IsCanDoCurrentWork(workID, WebUser.getNo()) == false){
            return Return_Info(500,"根据WorkID获取流程实例状态失败","当前用户没有查看权限");
        }

        GenerWorkFlow gwf = new GenerWorkFlow(workID);
        return Return_Info(200,"获取成功",  gwf.ToJson());

    }

    @RequestMapping(value = "/WorkTrack")
    @ApiOperation("根据WorkID获取全流程轨迹(配置计算未来接收人)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "WorkID", value = "流程实例ID", dataType = "Long", required = true)
    })
    public final Object  WorkTrack(String token,Long WorkID) throws Exception {
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"根据WorkID获取流程轨迹失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(WorkID) == true)
            return Return_Info(500,"根据WorkID获取流程轨迹失败","流程实例workID不能为空");

        Dev2Interface.Port_LoginByToken(token);

        GenerWorkFlow gwf = new GenerWorkFlow(WorkID);
        Nodes nds = new Nodes(gwf.getFlowNo());
        int curNodeID =gwf.getNodeID();
        String checkerPassed = ",";
        String trackTable = "ND" + Integer.parseInt(gwf.getFlowNo()) + "Track";
        boolean isShowCurrNodeInfo = true;

        if ((gwf.getWFState() != WFState.CompleteEnd && gwf.getWFState() != WFState.CompleteByNode )&& gwf.getWFState().getValue() != 12)
        {
            Paras ps = new Paras();
            ps.SQL = "SELECT FK_Emp FROM WF_GenerWorkerlist WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID AND IsPass=1 AND FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node Order By RDT,CDT";
            ps.Add("WorkID", this.getWorkID());
            ps.Add("FK_Node", gwf.getNodeID());
            DataTable checkerPassedDt = DBAccess.RunSQLReturnTable(ps);
            for (DataRow dr : checkerPassedDt.Rows)
            {
                checkerPassed += dr.getValue("FK_Emp") + ",";
            }
        }
        //已经运行过的节点
        String runNodes = "";
        if(gwf.getWFState() != WFState.CompleteEnd && gwf.getWFState() != WFState.CompleteByNode){
            String sql="SELECT DISTINCT(FK_Node) From(SELECT FK_Node From WF_GenerWorkerList WHERE WorkID="+gwf.getWorkID()+"  ORDER BY RDT) A";
            DataTable dt = DBAccess.RunSQLReturnTable(sql);
            for(DataRow dr : dt.Rows){
                runNodes+=dr.getValue(0).toString()+",";
            }
        }
        //是否可以编辑审核意见
        boolean isCanDo = Dev2Interface.Flow_IsCanDoCurrentWork(this.getWorkID(), WebUser.getNo());

        DataSet ds = new DataSet();
        DataRow row = null;

        DataTable tkDt = new DataTable("Tracks");
        tkDt.Columns.Add("MyPk", String.class);
        tkDt.Columns.Add("NodeID", Integer.class);
        //从节点来
        tkDt.Columns.Add("NDFrom", String.class);
        tkDt.Columns.Add("NDFromT", String.class);

        //到某节点
        tkDt.Columns.Add("NDTo", String.class);
        tkDt.Columns.Add("NDToT", String.class);

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
        //流程附件.
        DataTable athDt = new DataTable("Aths");
        athDt.Columns.Add("NodeID", Integer.class);
        athDt.Columns.Add("MyPK", String.class);
        athDt.Columns.Add("FK_FrmAttachment", String.class);
        athDt.Columns.Add("FK_MapData", String.class);
        athDt.Columns.Add("FileName", String.class);
        athDt.Columns.Add("FileExts", String.class);
        athDt.Columns.Add("CanDelete", Boolean.class);


        DataTable historyTracks = new DataTable("historyTracks");
        historyTracks.Columns.Add("MyPk", String.class);
        historyTracks.Columns.Add("NodeID", Integer.class);
        //从节点来
        historyTracks.Columns.Add("NDFrom", String.class);
        historyTracks.Columns.Add("NDFromT", String.class);

        //到某节点
        historyTracks.Columns.Add("NDTo", String.class);
        historyTracks.Columns.Add("NDToT", String.class);

        historyTracks.Columns.Add("NodeName", String.class);
        historyTracks.Columns.Add("Msg", String.class);
        historyTracks.Columns.Add("EmpFrom", String.class);
        historyTracks.Columns.Add("EmpFromT", String.class);
        historyTracks.Columns.Add("EmpTo", String.class);
        historyTracks.Columns.Add("EmpToT", String.class);
        historyTracks.Columns.Add("DeptName", String.class);
        historyTracks.Columns.Add("RDT", String.class);
        historyTracks.Columns.Add("ParentNode", Integer.class);
        historyTracks.Columns.Add("ActionType", Integer.class);
        historyTracks.Columns.Add("Tag", String.class);
        historyTracks.Columns.Add("FWCView", String.class);
        historyTracks.Columns.Add("WritImg", String.class);
        historyTracks.Columns.Add("WriteStamp", String.class);
        historyTracks.Columns.Add("SigantureEnabel", Integer.class); //签名方式
        historyTracks.Columns.Add("FWCAth", Integer.class); //签名方式
        historyTracks.Columns.Add("Exer", String.class); //执行人

        //审核组件的定义
        WorkCheck wc = null;
        NodeWorkCheck wcDesc = new NodeWorkCheck(curNodeID);
        if (gwf.getFID() != 0)
            wc = new WorkCheck(gwf.getFlowNo(), gwf.getNodeID(), gwf.getWorkID(), gwf.getFID(),wcDesc.GetValIntByKey("FWCTimeModel",0));
        else
            wc = new WorkCheck(gwf.getFlowNo(), gwf.getNodeID(), gwf.getWorkID(), 0,wcDesc.GetValIntByKey("FWCTimeModel",0));
        //获取运行过的所有历史审核信息
        String nodes = "";
        Tracks tks = wc.getHisWorkChecks();
        Tracks tracks = new Tracks();
        for (Track tk : tks.ToJavaList()) {
            if(tk.getHisActionType()!=ActionType.WorkCheck && tk.getHisActionType()!=ActionType.AutoWorkCheck)
                tracks.add(tk);

            if (tk.getHisActionType() == ActionType.FlowBBS)
                continue;

            if (wcDesc.getFWCMsgShow() == 1 && tk.getEmpFrom().equals(WebUser.getNo()) == false)
                continue;
            switch (tk.getHisActionType()) {
                case WorkCheck:
                case AutoWorkCheck:
                case Forward:
                case StartChildenFlow:
                case ForwardHL:
                case Return:
                case ReturnAndBackWay:
                case CC:
                    if(tk.getHisActionType() == ActionType.CC && tk.getNDFrom()!=tk.getNDTo()){
                        if (nodes.contains(tk.getNDTo() + ",") == false)
                            nodes += tk.getNDTo() + ",";
                    }
                    if (nodes.contains(tk.getNDFrom() + ",") == false)
                        nodes += tk.getNDFrom() + ",";
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
                continue;

            //退回
            if (tk.getHisActionType() == ActionType.Return || tk.getHisActionType() == ActionType.ReturnAndBackWay)
            {
                //0.不显示退回意见
                if (wcDesc.getFWCIsShowReturnMsg() == 0)
                    continue;
                //1.退回到的节点显示
                //zsy5.23修改为无论任何节点都正常展示
                if (wcDesc.getFWCIsShowReturnMsg() == 1 && tk.getNDTo() != gwf.getNodeID())
                    continue;
            }

            // 如果是当前的节点.当前人员可以处理, 已经审批通过的人员.
            if (tk.getHisActionType() !=ActionType.Shift&&tk.getNDFrom() == gwf.getNodeID() && isCanDo == true && tk.getEmpFrom() .equals(WebUser.getNo()) == false && checkerPassed.contains("," + tk.getEmpFrom()  + ",") == false)
                continue;

            if (tk.getNDFrom() == gwf.getNodeID() && gwf.getHuiQianTaskSta() != HuiQianTaskSta.None)
            {
                //判断会签, 去掉正在审批的节点.
            }

            //手动抄送也需要展示track信息
//			if(tk.getHisActionType() == ActionType.CC && tk.getNDFrom()==tk.getNDTo())
//				continue;
            //历史审核信息现在存放在流程前进的节点中
            switch (tk.getHisActionType())
            {
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
                    row = historyTracks.NewRow();
                    row.setValue("MyPk", tk.getMyPK());
                    row.setValue("NDFrom",tk.getNDFrom());
                    row.setValue("NDTo",tk.getNDTo());
                    row.setValue("NDFromT",tk.getNDFromT());
                    row.setValue("NDToT",tk.getNDToT());
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

                    if ("2".equals(sigantureEnabel) == true)  //写字板
                        row.setValue("WritImg", DBAccess.GetBigTextFromDB(trackTable, "MyPK", tk.getMyPK(), "WriteDB"));

                    if ("4".equals(sigantureEnabel) == true && "5".equals(sigantureEnabel) == true)  //电子盖章  电子签名+盖章
                        row.setValue("WriteStamp", DBAccess.GetBigTextFromDB(trackTable, "MyPK", tk.getMyPK(), "FrmDB"));

                    historyTracks.Rows.add(row);

                    ///#region //子流程的审核组件数据
                    if (tk.getFID() != 0 && tk.getHisActionType() == ActionType.StartChildenFlow && historyTracks.Select("ParentNode=" + tk.getNDFrom()).length == 0)
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
                                    row = historyTracks.NewRow();
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

                                    historyTracks.Rows.add(row);

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
        ds.Tables.add(historyTracks);

        WF_WorkOpt_OneWork oneWork = new WF_WorkOpt_OneWork();
        String nodeIDs = oneWork.GetFutureNodeNotContainsEmps();
        SelectAccpers sas = new SelectAccpers();
        sas.Retrieve(SelectAccperAttr.WorkID,this.getWorkID());

        SelectAccpers csa = null;
        for(String nodeID : nodeIDs.split(",")){
            if(DataType.IsNullOrEmpty(nodeID) == true)
                continue;
            //是否已审核.
            if(runNodes.contains(nodeID+",") == true)
                continue;
            //获取处理人
            Entities ens = sas.GetEntitiesByKey(SelectAccperAttr.FK_Node,nodeID);
            String empNos="";
            String empNames="";
            if(ens!=null){
                csa = (SelectAccpers) ens;
                for(SelectAccper sa :csa.ToJavaList()){
                    empNos +=sa.getEmpNo()+",";
                    empNames +=sa.getEmpName()+",";
                }
                empNos = empNos.substring(0, empNos.length()-1);
                empNames = empNames.substring(0, empNames.length()-1);
            }
            row = tkDt.NewRow();
            row.setValue("NodeID", nodeID);
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
        ds.Tables.add(tkDt);

        //判断当前节点的是否可以审批
        if(wcDesc.getHisFrmWorkCheckSta() == FrmWorkCheckSta.Enable ){
            Node nd = new Node(gwf.getNodeID());
            DataTable dtt = tkDt.clone();
            row = dtt.NewRow();
            row.setValue("NodeID", gwf.getNodeID());
            row.setValue("NodeName", nd.getFWCNodeName());
            row.setValue("IsDoc", true);
            row.setValue("ParentNode", 0);
            row.setValue("RDT", "");

            row.setValue("Msg", Dev2Interface.GetCheckInfo(gwf.getFlowNo(), this.getWorkID(), gwf.getNodeID(), wcDesc.getFWCDefInfo()));
            row.setValue("EmpFrom", WebUser.getNo());
            row.setValue("EmpFromT", WebUser.getName());
            row.setValue("EmpTo", WebUser.getNo());
            row.setValue("EmpToT", WebUser.getName());
            row.setValue("DeptName", WebUser.getDeptName());
            row.setValue("ActionType", ActionType.Forward.getValue());
            row.setValue("Tag", Dev2Interface.GetCheckTag(gwf.getFlowNo(), this.getWorkID(), gwf.getNodeID(), WebUser.getNo()));

            dtt.Rows.add(row);
            dtt.TableName="MyTrack";
            ds.Tables.add(dtt);
        }

        String str = bp.tools.Json.ToJson(ds);
        return Return_Info(200,"获取成功", str);

    }

    public String Search_Init(String scop,String key,String dtFrom,String dtTo,Integer pageIdx) throws Exception {
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
            qo.AddWhere(GenerWorkFlowAttr.Starter, "=", WebUser.getNo());

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

        qo.DoQuery("workID", 20, pageIdx);
        //   qo.DoQuery(); // "workID", 20, pageIdx);


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
        return Json.ToJson(ds);
    }

    /**
     批量设置抄送查看完毕
     @return
     */
    public final String CC_BatchCheckOver(String workIDs) throws Exception
    {
        return Dev2Interface.Node_CC_SetCheckOverBatch(workIDs);
    }


    /**
     获得发起的url.

     */
    public final Object GenerFrmUrl(long workID,String fk_flow,Integer fk_node,String Token) throws Exception
    {
//
//             * 发起的url需要在该流程的开始节点的表单方案中，使用SDK表单，并把表单的url设置到里面去.
//             * 设置步骤:
//             * 1. 打开流程设计器.
//             * 2. 在开始节点上右键，选择表单方案.
//             * 3. 选择SDK表单，把url配置到文本框里去.
//             * 比如: /App/F027QingJia.htm
//

        try
        {
            int nodeID = fk_node!=null?fk_node.intValue():0;
            if (nodeID == 0)
                nodeID = Integer.parseInt(fk_flow + "01");
            if (workID == 0)
                workID = Dev2Interface.Node_CreateBlankWork(fk_flow, WebUser.getNo());

            String url = "";
            bp.wf.Node nd = new bp.wf.Node(nodeID);
            if (nd.getFormType() == NodeFormType.SDKForm || nd.getFormType() == NodeFormType.SelfForm)
            {
                url = nd.getFormUrl();
                if (url.contains("?") == true)
                {
                    url += "&FK_Flow=" + fk_flow + "&FK_Node=" + nodeID + "&workID=" + workID + "&Token=" +Token + "&UserNo=" + WebUser.getNo();
                }
                else
                {
                    url += "?FK_Flow=" + fk_flow + "&FK_Node=" + nodeID + "&workID=" + workID + "&Token=" + Token + "&UserNo=" + WebUser.getNo();
                }
            }
            else
            {
                url = "/WF/MyFlow.htm?FK_Flow=" + fk_flow + "&FK_Node=" + nodeID + "&workID=" + workID + "&Token=" + Token;
            }
            return Return_Info(200, "获取打开的表单成功", url);
        }
        catch (RuntimeException ex)
        {
            //输出url.
            return "err@" + ex.getMessage();
        }
    }
    ///#region 通用方法.
    public final String GetValByKey(String key)
    {
        String str = ContextHolderUtils.getRequest().getParameter(key);
        if (DataType.IsNullOrEmpty(str))
        {
            return null;
        }
        return str;
    }
    public final int GetValIntByKey(String key)
    {
        String val = GetValByKey(key);
        if (val == null)
        {
            return 0;
        }
        return Integer.parseInt(val);
    }
    public final boolean GetValBoolenByKey(String key)
    {
        String val = GetValByKey(key);
        if (val == null)
        {
            return false;
        }
        if (val.equals("0") == true)
        {
            return false;
        }
        return true;
    }
    /*
    删除草稿

     */
    public final String Flow_DeleteDraft(String workIDs)throws Exception
    {
        String[] strs = workIDs.split(",");

        String info = "";
        for (String workIDStr : strs)
        {
            if (DataType.IsNullOrEmpty(workIDStr) == true)
            {
                continue;
            }

            Dev2Interface.Node_DeleteDraft(Long.parseLong(workIDStr));
        }
        return "删除成功.";
    }
    @Override
    public Class getCtrlType() {
        return null;
    }

    private int DealNodeIDStr(String nodeIDStr, String flowNo)
    {
        int returnToNodeID = 0;
        if(DataType.IsNullOrEmpty(nodeIDStr)==true)
            return 0;
        if (DataType.IsNumStr(nodeIDStr) == true)
            returnToNodeID = Integer.parseInt(nodeIDStr);
        else
            returnToNodeID = DBAccess.RunSQLReturnValInt("SELECT NodeID FROM WF_Node WHERE FK_Flow='" + flowNo + "' AND Mark='" + nodeIDStr + "'");
        return returnToNodeID;
    }

    @RequestMapping(value = "/DB_StartByOrg")
    @ApiOperation("获取指定组织下所有流程")
    @ApiImplicitParams({
            @ApiImplicitParam(name="token",value="Token",paramType = "query",required = true),
            @ApiImplicitParam(name="orgNo",value="组织编号",required = true)
    })
    public Object DB_StartByOrg(String token, String orgNo) throws Exception {
        Dev2Interface.Port_LoginByToken(token);
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"根据WorkID获取流程实例状态失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(orgNo) == true)
            return Return_Info(500,"根据WorkID获取流程实例状态失败","组织IDorgNo不能为空");
        return Return_Info(200,"获取指定组织下所有流程成功", Json.ToJson(Dev2Interface.DB_StartByOrg(orgNo)));
    }
    /**
     * 获得指定流程的节点信息.
     * @param flowNo  流程ID
     * @param token  token
     * @param orgNo  组织ID
     * @return Object
     * @throws Exception
     */
    @RequestMapping(value = "/Flow_InfoByOrg")
    @ApiOperation("根据OrgNo获得指定流程的节点信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flowNo", value = "流程ID",required = true),
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "orgNo", value = "组织ID", required = true)
    })
    public Object Flow_InfoByOrg(String flowNo,String token, String orgNo) throws Exception {
        Dev2Interface.Port_LoginByToken(token);
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"根据OrgNo获得指定流程的节点信息失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(orgNo) == true)
            return Return_Info(500,"根据OrgNo获得指定流程的节点信息失败","组织IDorgNo不能为空");
        if(DataType.IsNullOrEmpty(flowNo) == true)
            return Return_Info(500,"根据OrgNo获得指定流程的节点信息失败","流程编号flowNo不能为空");

        return Return_Info(200,"根据OrgNo获得指定流程的节点信息成功", Json.ToJson(Dev2Interface.Flow_InfoByOrg(flowNo,orgNo)));

    }
    /**
     * 根据节点ID获得该节点可退回的节点信息.
     * @param nodeId  节点ID
     * @param token  token
     * @param flowNo  流程No
     * @return Object
     * @throws Exception
     */
    @RequestMapping(value = "/Node_ReturnInfo")
    @ApiOperation("根据节点ID获得该节点可退回的节点信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nodeId", value = "节点ID",required = true),
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "flowNo", value = "流程No", paramType = "query", required = true)
    })
    public Object Node_ReturnInfo(String nodeId,String token,String flowNo) throws Exception {
        Dev2Interface.Port_LoginByToken(token);
        if(DataType.IsNullOrEmpty(token) == true)
            return Return_Info(500,"根据节点ID获得该节点可退回的节点信息失败","用户的Token值不能为空");
        if(DataType.IsNullOrEmpty(nodeId) == true)
            return Return_Info(500,"根据节点ID获得该节点可退回的节点信息失败","节点ID不能为空");
        int toNodeID = DealNodeIDStr(nodeId, flowNo);
        return Return_Info(200,"根据节点ID获得该节点可退回的节点信息成功",Json.ToJson(Dev2Interface.Node_ReturnInfo(toNodeID)));

    }
    @RequestMapping(value = "/batchSendWork")
    @ApiOperation("批量发送")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "根据token登录"),
            @ApiImplicitParam(name = "workIds", value = "workIds"),
            @ApiImplicitParam(name = "flowNo", value = "流程No")
    })
    public String batchSendWork(String token,String workIds,String flowNo) throws Exception {
        //启用线程的时候设置持有上下文的Request容器
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(servletRequestAttributes,true);
        Dev2Interface.Port_LoginByToken(token);
        String[] strs = workIds.split(",");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                30,
                50,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(60000),
                new ThreadPoolExecutor.DiscardPolicy()
        );
        for(String sendWorkID :strs){
            threadPoolExecutor.execute(() -> {
                try {
                    Dev2Interface.Node_SendWork(flowNo,Long.parseLong(sendWorkID));

                } catch (Exception e) {
                    // 异常不做处理;
                    e.printStackTrace();
                }
            });
        }
       return "";
    }

    @GetMapping(value = "/Frm_Init")
    @ApiOperation("获得表单的数据[入参说明：token是登录凭证、frmID是表单ID（流程所使用的表单）、oid是WorkID流程实例编号]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "frmID", value = "表单ID", paramType = "query", required = true),
            @ApiImplicitParam(name = "oid", value = "实例数据ID", paramType = "query", required = true)
    })
    public Object Frm_Init(String token, String frmID, String oid)
    {
        try
        {
            //根据token登录
            Dev2Interface.Port_LoginByToken(token);
            WF_CCForm handle = new WF_CCForm();
            handle.AddPara("FrmID", frmID);
            handle.AddPara("OID", oid);
            handle.AddPara("WorkID", oid);

            String str = handle.FrmGener_Init();
            return Return_Info(200, "执行成功", str);
        }
        catch (Exception ex)
        {
            return Return_Info(500, "执行失败", ex.getMessage());
        }
    }
    @GetMapping(value = "/Frm_InitByFlowNo")
    @ApiOperation("获得表单的数据[入参说明：token是登录凭证、FlowNo是流程ID（流程所使用的表单）、WFState是流程实例状态(0为空白，2为运行中，3为已办结)]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", paramType = "query", required = true),
            @ApiImplicitParam(name = "flowNo", value = "流程ID", paramType = "query", required = true),
            @ApiImplicitParam(name = "WFState", value = "流程实例状态", dataType = "Int", required = true)
    })
    public Object Frm_InitByFlowNo(String token, String flowNo, Integer WFState)
    {
        try
        {
            String str = "";
            JSONObject jsonNew = new JSONObject();
            Flow fl = new Flow(flowNo);
            String sql = "SELECT OID FROM " + fl.getPTable() + " WHERE WFState =" + WFState;
            DataTable dt = DBAccess.RunSQLReturnTable(sql);
            for (DataRow dr : dt.Rows)
            {
                Integer workid = Integer.parseInt(dr.getValue("OID").toString());
                WF_CCForm handle = new WF_CCForm();
                handle.AddPara("FrmID", fl.getPTable());
                handle.AddPara("OID", workid.toString());
                handle.AddPara("WorkID", workid.toString());

                str = handle.FrmGener_Init();
                jsonNew.putAll(JSONObject.parseObject(str));
            }
            return Return_Info(200, "执行成功", jsonNew);
        }
        catch (Exception ex)
        {
            return Return_Info(500, "执行失败", ex.getMessage());
        }
    }


    /**
     * 蓝色大屏-登陆
     * @param user 用户vo
     * @param token
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "蓝色大屏-登陆", notes = "蓝色大屏-登陆")
    @PostMapping("/goView/login")
    public bp.tools.AjaxResult Port_LoginByPassword(@RequestBody SysUser user,String token, HttpServletRequest request) throws Exception {
        bp.tools.AjaxResult json = new bp.tools.AjaxResult();
        try{

            if (token != null) {
                Dev2Interface.Port_LoginByToken(token);
            }

            bp.port.Emp emp= new bp.port.Emp();
            emp.setNo(user.getUsername()) ;
            if ( emp.RetrieveFromDBSources()==0) {
                json.put("code", 500);
                json.put("msg","用户名或者密码错误");
                return json;
            }

            if (emp.CheckPass(user.getPassword()) == false) {
                json.put("code", 500);
                json.put("msg","用户名或者密码错误");
                return json;
            }

            //先登录再获取Token.
            Dev2Interface.Port_Login(emp.getUserID());
            token = Dev2Interface.Port_GenerToken(emp.getNo(),"PC");

            user.setUsername(WebUser.getName());
            user.setNickname(WebUser.getName());
            user.setPassword(null);
            user.setId(WebUser.getNo());

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userinfo", user);
        	Token token2 = new Token();
        	token2.setTokenName("satoken");
        	token2.setTokenValue(token);
            map.put("token", token2);

            json.put("code", 200);
            json.put("data", map);
            json.put("msg","登录成功！");
            return json;
        }catch(Exception e){
            json.put("code", 500);
            json.put("msg","登录有错误！");
            return json;
        }
    }
    /**
     * 蓝色大屏-退出登录
    */
    @RequestMapping(value = "/goView/Port_LoginOut")
    @ApiOperation("退出登录:清除当前的会话,注销cookies.")
    public final Object Portal_LoginOut() throws Exception {
        try{
            Dev2Interface.Port_SigOut();
            return Return_Info(200,"退出成功","退出成功");
        }catch(Exception e){
            return Return_Info(500,"退出成功失败",e.getMessage());
        }
    }


    /**
     * 蓝色大屏-获取oss地址
          */
	@ApiOperation(value = "蓝色大屏-获取oss地址", notes = "蓝色大屏-获取oss地址")
	@GetMapping("/goView/getOssInfo")
	public Object getOssInfo() {
		HttpServletRequest request = ContextHolderUtils.getRequest();
		String fullUrl = request.getRequestURL().toString();
		String prefix = fullUrl.substring(0, fullUrl.indexOf("/", 8));

		 prefix = prefix+"/DataUser/";
	     return  Return_Info(200,"获取成功",prefix);

	}
    @GetMapping(value = "/Search_EmpByToken")
    @ApiOperation("根据token查询人员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Token", value = "Token", paramType = "query", required = true),
    })
    public Object Search_EmpByToken(String Token) throws Exception {
        bp.port.Token tk = new bp.port.Token();
        tk.setMyPK(Token);
        if (tk.RetrieveFromDBSources() == 0)
        {
            throw new RuntimeException("err@ token 过期或失效.");
        }
        DataTable dt = tk.ToDataTableField();
        return Return_Info(200,"根据Token获取人员信息成功", Json.ToJson(dt));
    }
    /**
     *蓝色大屏- 获取项目列表
     */
    @GetMapping(value = "/goView/projectList")
    @ApiImplicitParams({
        @ApiImplicitParam(name="token",value="token",required = true),
    })
    @ApiOperation("蓝色大屏-项目列表")
	public Object projectList(String token){

		if (token != null) {
            Dev2Interface.Port_LoginByToken(token);
		}
		//String userName = WebUser.getName();
		String userName  = WebUser.getUserID();

		String execSql = "select mypk as id , ProjectName as projectName,CreateTime as createTime,"
				+ "CreateUserId as createUserId,State as state,isDelete,indexImage,Remarks from goview_projects t "
				+ " where 1=1 and createUserId = '"+userName+"'  and isTemplate=-1 ";
		DataTable dt = DBAccess.RunSQLReturnTable(execSql);
		int count = dt.Rows.size();


        Hashtable ht = new Hashtable();
        ht.put("code", 200);
        ht.put("message", "获取成功");
        ht.put("data", Json.ToJson(dt));
        ht.put("count", count);
        return ht;
	}
    /**
     * 蓝色大屏-获取项目模板列表
     */
    @GetMapping(value = "/goView/projectTemplateListApi")
    @ApiImplicitParams({
        @ApiImplicitParam(name="token",value="token",required = true),
    })
    @ApiOperation("蓝色大屏-模板列表")
	public Object projectTemplateListApi(String token){

		if (token != null) {
            Dev2Interface.Port_LoginByToken(token);
		}
		String userName = WebUser.getUserID();

		String execSql = "select mypk as id , ProjectName as projectName,CreateTime as createTime,"
				+ "CreateUserId as createUserId,State as state,isDelete,indexImage,Remarks from goview_projects t "
				+ " where 1=1 and createUserId = '"+userName+"' and isTemplate=1 ";
		DataTable dt = DBAccess.RunSQLReturnTable(execSql);
		int count = dt.Rows.size();


        Hashtable ht = new Hashtable();
        ht.put("code", 200);
        ht.put("message", "获取成功");
        ht.put("data", Json.ToJson(dt));
        ht.put("count", count);
        return ht;
	}

	/**
     * 蓝色大屏-新增保存项目信息
     * @param
     * @return
     */
	@ApiOperation(value = "蓝色大屏-新增保存项目", notes = "蓝色大屏-新增保存项目")
	@PostMapping("/goView/create")
	public Object createProject( @RequestBody GoviewProjectVo goviewProjectVo ){
		GoviewProject goviewProject = new GoviewProject();
		goviewProject.setProjectname(goviewProjectVo.getProjectName());
		goviewProject.setMyPK(goviewProjectVo.getProjectName());
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
		goviewProject.setCreatetime(dateString);
		goviewProject.setCreateuserid(goviewProjectVo.getCreateUserId());
		goviewProject.setState(-1);
		goviewProject.setIsTemplate(-1);

		try {
			int b = goviewProject.Insert();
			if(b>0){
				goviewProjectVo.setState(-1);
				goviewProjectVo.setCreateTime(dateString);
                return Return_Info(200,"创建成功",goviewProjectVo);
			}else{
			     return Return_Info(500,"创建失败","");
			}
		} catch (Exception e) {
			e.printStackTrace();
		     return  Return_Info(500,"创建失败","");
		}


	}
	/**
     * 蓝色大屏-通过模板创建项目
     * @param
     * @return
     */
	@ApiOperation(value = "蓝色大屏-通过模板创建项目", notes = "蓝色大屏-通过模板创建项目")

	@PostMapping("/goView/createByTemplate")
	public Object createByTemplateProject( @RequestBody GoviewProjectVo goviewProjectVo ){
		GoviewProject goviewProject = new GoviewProject();
		goviewProject.setProjectname(goviewProjectVo.getProjectName());
		goviewProject.setMyPK(goviewProjectVo.getProjectName());
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
		goviewProject.setCreatetime(dateString);
		goviewProject.setCreateuserid(goviewProjectVo.getCreateUserId());
		goviewProject.setState(-1);
		goviewProject.setIsTemplate(-1);

		try {
			int b = goviewProject.Insert();
			if(b>0){
				goviewProjectVo.setState(-1);
				goviewProjectVo.setCreateTime(dateString);

				//GoviewProjectData goviewProjectData= new GoviewProjectData();
				String execSql = "select mypk as id , projectid ,Content from goview_projectdatas where projectid = '"+goviewProjectVo.getTemplateID()+"'";
				DataTable dt = DBAccess.RunSQLReturnTable(execSql);
				int count = dt.Rows.size();
				GoviewProjectData goviewProjectData = new GoviewProjectData();
				if(count>0) {
					//String content = (String) dt.getValue(0, "Content");
					//goviewProjectData.
					String ids = (String) dt.getValue(0, "id");
					String content = (String) dt.getValue(0, "Content");

					 goviewProjectData.setCreateTime(dateString);
					 goviewProjectData.setProjectId(goviewProjectVo.getProjectName());
					 goviewProjectData.setContent(content);

					goviewProjectData.SaveBigTxtToDB("Content", content);
					goviewProjectData.Save();
					//goviewProjectData.Update();
				}else {
					 goviewProjectData.setProjectId(goviewProjectVo.getProjectName());
					 //goviewProjectData.setContent(content);
					 goviewProjectData.setCreateTime(dateString);
					 goviewProjectData.Save();
				}


                return Return_Info(200,"创建成功",goviewProjectVo);
			}else{
			     return Return_Info(500,"创建失败","");
			}
		} catch (Exception e) {
			e.printStackTrace();
		     return  Return_Info(500,"创建失败","");
		}


	}


	/**
	 * 蓝色大屏-项目表删除
	 * @param ids
	 * @return
	 */
	@ApiOperation(value = "蓝色大屏-项目表删除", notes = "蓝色大屏-项目表删除")
	@DeleteMapping("/goView/delete")
	public Object remove(String ids){
		try {
			GoviewProject goviewProject = new GoviewProject(ids);
			//goviewProject.setMyPK(ids);
			int t = goviewProject.Delete();
		} catch (Exception e) {
			e.printStackTrace();
		     return  Return_Info(500,"删除失败","");
		 }
	     return Return_Info(200,"删除成功","");
	 }


	@ApiOperation(value = "蓝色大屏-修改项目", notes = "蓝色大屏-修改项目")
    @ApiImplicitParams({
        @ApiImplicitParam(name="id",value="项目ID",required = false),
        @ApiImplicitParam(name="projectName",value="项目名称",required = false),
    })
    @PostMapping("/goView/edit")
    public Object editSave(@RequestBody GoviewProjectVo goviewProjectVo)
    {

		try {
			//goviewProject.Save();
			GoviewProject goviewProject = new GoviewProject(goviewProjectVo.getId());
			//goviewProject.setMyPK(goviewProjectVo.getId());

			if(goviewProjectVo.getProjectName()!=null) {
				goviewProject.setProjectname(goviewProjectVo.getProjectName());
			}
			if(goviewProjectVo.getIndexImage()!=null) {
				goviewProject.setIndeximage(goviewProjectVo.getIndexImage());
			}

			//goviewProject.
			goviewProject.Update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	     return Return_Info(200,"获取成功","");

    }


	@ApiOperation(value = "蓝色大屏-项目重命名", notes = "蓝色大屏-项目重命名")
    @PostMapping("/goView/rename")
    @ResponseBody
    public Object rename(@RequestBody GoviewProjectVo goviewProjectVo)
    {

		GoviewProject goviewProject = new GoviewProject();
		goviewProject.setMyPK(goviewProjectVo.getId());
		goviewProject.setProjectname(goviewProjectVo.getProjectName());
		try {
			//goviewProject.Save();
			goviewProject.Update();
		} catch (Exception e) {
			e.printStackTrace();
	       return Return_Info(500,"重命名失败","");
		}
       return Return_Info(200,"重命名成功","");
    }
//

	//发布/取消项目状态
    @PutMapping("/goView/publish")
    @ApiOperation(value = "蓝色大屏-发布项目", notes = "蓝色大屏-发布项目")
	@ResponseBody
    public Object updateVisible(@RequestBody GoviewProjectVo goviewProjectVo) {
    	if(goviewProjectVo.getState()==-1||goviewProjectVo.getState()==1) {
    		try {
        		GoviewProject goviewProject = new GoviewProject(goviewProjectVo.getId());
        		goviewProject.setMyPK(goviewProjectVo.getId());
        		goviewProject.setState(goviewProjectVo.getState());
        		goviewProject.Update();
    		} catch (Exception e) {
    			e.printStackTrace();
    		    return Return_Info(500,"发布失败","");
    		}
    	}
	      return Return_Info(200,"发布成功","");
	}
	//设置模板
    @PutMapping("/goView/setTemplate")
    @ApiOperation(value = "蓝色大屏-设置项目为模板", notes = "蓝色大屏-设置项目为模板")
	@ResponseBody
    public Object setTemplate(@RequestBody GoviewProjectVo goviewProjectVo) {
    	if(goviewProjectVo.getIsTemplate()==-1||goviewProjectVo.getIsTemplate()==1) {
    		try {
        		GoviewProject goviewProject = new GoviewProject(goviewProjectVo.getId());
        		goviewProject.setMyPK(goviewProjectVo.getId());
        		goviewProject.setIsTemplate(goviewProjectVo.getIsTemplate());
        		goviewProject.Update();
    		} catch (Exception e) {
    			e.printStackTrace();
    		    return Return_Info(500,"设置失败","");
    		}
    	}
	      return Return_Info(200,"设置模板成功","");
	}



    @ApiOperation(value = "蓝色大屏-获取项目数据", notes = "蓝色大屏-获取项目数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name="projectId",value="项目ID",required = true),
    })
	@GetMapping("/goView/getData")
    public Object getData(String projectId)
    {

    	GoviewProjectVo goviewProjectVo=new GoviewProjectVo();


		try {
			GoviewProject goviewProject = new GoviewProject(projectId);
	    	goviewProjectVo.setId(projectId);
	    	goviewProjectVo.setProjectName(goviewProject.getProjectname());
	    	goviewProjectVo.setIndexImage(goviewProject.getIndeximage());
	    	goviewProjectVo.setRemarks(goviewProject.getRemarks());
	    	goviewProjectVo.setState(goviewProject.getState());

            Paras pens = new Paras();
            pens.SQL="select mypk as id , projectid ,Content from goview_projectdatas where projectid = "+SystemConfig.getAppCenterDBVarStr() + "v";
            pens.Add("v",projectId);
            DataTable dt = DBAccess.RunSQLReturnTable(pens);
			int count = dt.Rows.size();
			if(count>0) {
				String content = (String) dt.getValue(0, "Content");
		    	goviewProjectVo.setContent(content);
		    	return Return_Info(200,"获取成功",goviewProjectVo);
			}else {
				return Return_Info(200,"无数据","null");
			}

		} catch (Exception e) {
	        return Return_Info(500,"无数据","");
		}


       // return Return_Info(200,"获取成功",bp.tools.Json.ToJson(goviewProjectVo));

    }


	@ApiOperation(value = "蓝色大屏-保存设计信息", notes = "蓝色大屏-保存设计信息")
	@PostMapping("/goView/save/data")
	public Object saveData(GoviewProjectDataVo data) {
		bp.tools.AjaxResult json = new bp.tools.AjaxResult();
		GoviewProjectData goviewProjectData = new GoviewProjectData();

		try {
			GoviewProject goviewProject = new GoviewProject(data.getProjectId());
			if(goviewProject==null) {
		        return Return_Info(500,"没有项目信息","");
		     }
			//GoviewProjectData goviewProjectData= new GoviewProjectData();
			String execSql = "select mypk as id , projectid ,Content from goview_projectdatas where projectid = '"+data.getProjectId()+"'";
			DataTable dt = DBAccess.RunSQLReturnTable(execSql);
			int count = dt.Rows.size();

			if(count>0) {
				//String content = (String) dt.getValue(0, "Content");
				//goviewProjectData.
				String ids = (String) dt.getValue(0, "id");
				String projectid = (String) dt.getValue(0, "projectid");
				 Date currentTime = new Date();
			        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			        String dateString = formatter.format(currentTime);
					 goviewProjectData.setCreateTime(dateString);
				 goviewProjectData.setMyPK(ids);
				 goviewProjectData.setProjectId(projectid);
				goviewProjectData.setContent(data.getContent());

				goviewProjectData.SaveBigTxtToDB("Content", data.getContent());
				goviewProjectData.Save();
				//goviewProjectData.Update();
			}else {
				goviewProjectData.setProjectId(data.getProjectId());
				goviewProjectData.setContent(data.getContent());
				 Date currentTime = new Date();
		        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        String dateString = formatter.format(currentTime);
				 goviewProjectData.setCreateTime(dateString);
				goviewProjectData.Save();
			}

		} catch (Exception e) {
			e.printStackTrace();
	        return Return_Info(500,"保存数据失败","");
	    }

        json.put("msg", "保存数据成功");
        json.put("code", 200);
        return Return_Info(200,"保存数据成功","");
	}

    /**
     * 蓝色大屏-通过Token登陆
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "蓝色大屏-通过Token登陆", notes = "蓝色大屏-通过Token登陆")
    @ApiImplicitParams({
        @ApiImplicitParam(name="token",value="token",required = true),
    })
	@GetMapping("/goView/loginByToken")
	@ResponseBody
   // public final Object Port_LoginByPassword(String userNo, String password, String orgNo) throws Exception {
	public final Object loginByToken(String token) throws Exception {
		bp.tools.AjaxResult json = new bp.tools.AjaxResult();
		try{
			if(DataType.IsNullOrEmpty(token) == true){
			   	json.put("code", 500);
		        json.put("data", "");
		        json.put("msg","Token为空");
	            return json;
			}else {
	            Dev2Interface.Port_LoginByToken(token);
			}
        	SysUser user = new SysUser();

        	user.setUsername(WebUser.getName());
        	user.setNickname(WebUser.getName());
        	user.setPassword(null);
        	user.setId(WebUser.getNo());

        	Token token2 = new Token();
        	token2.setTokenName("satoken");
        	//token2.setIsLogin(bp.web.WebUser.);
        	token2.setTokenValue(WebUser.getToken());

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userinfo", user);
			map.put("token", token2);

            return Return_Info(200,"登录成功",map);
        }catch(Exception e){
        	 return Return_Info(500,"登录有错误！","");
        }
    }
    @ApiOperation(value = "蓝色大屏-上传文件", notes = "蓝色大屏-上传文件")
	@PostMapping("/goView/upload")
	public Object uploadFile(@RequestBody MultipartFile object) throws IOException{

       // HttpServletRequest request = ContextHolderUtils.getRequest();
       // String contentType = request.getContentType();

        String fileName = "";
        String basePath = SystemConfig.getPathOfDataUser();
        if (SystemConfig.isJarRun() == true)
            basePath = SystemConfig.getPhysicalPath() + "DataUser" + File.separator;
        //上传附件
        String filepath = "";

        if (object == null)
        	return Return_Info(500,"获取附件信息有误", "失败");
        filepath = "UploadFile/goview";
        File file = new File(basePath + filepath);
        if (file.exists() == false) {
            file.mkdirs();
        }
        // 获取文件名
        fileName = object.getOriginalFilename();
        if (fileName.indexOf("/") > -1) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            fileName = fileName.replace(" ", "");
        }
        filepath = filepath + "/" + DBAccess.GenerGUID() + fileName;



        try {
            //CommonFileUtils.upload(request, "file", new File(basePath + filepath));
        	File desc = new File(basePath + filepath);
        	object.transferTo(desc);

        } catch (Exception e) {
            e.printStackTrace();
            return Return_Info(500, e.getMessage(), "失败");
        }

		//GoviewProjectData

		try {
			String fileName1 = fileName.substring(0, fileName.lastIndexOf("."));
//			GoviewProject goviewProject = new GoviewProject(projectId);
//	        String fileName1 = fileName.substring(0, fileName.lastIndexOf("."));
//	        // knowledge.SetValByKey("MyFileName", fileName1);
//	         goviewProject.setIndeximage("/DataUser/" + filepath);
//	         goviewProject.Update();

	         SysFileVo sysFileVo= new SysFileVo();
	         sysFileVo.setFileName(fileName1);
	         sysFileVo.setFileurl(filepath);
	         sysFileVo.setFileSize(String.valueOf(object.getSize()));

		     return Return_Info(200,"创建成功",sysFileVo);

		} catch (Exception e) {
			e.printStackTrace();
            return Return_Info(500,"创建失败","");
		}

	}
    /**
     * 蓝色大屏-动态数据测试1
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/goView/test1")
    public final Object goView_test1(String key) {
        try{
            System.out.println("test1:"+key);
            String p="";
            String rstr="默认文本";
            if(key!=null && !key.equals("")){
                System.out.println("key:"+key);
                p=key;
            }
            if(p.equals("1")){
                rstr="选项1";
            }else if(p.equals("2")){
                rstr="选项2";
            }
            return Return_Info(0,"",rstr);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 蓝色大屏-动态数据测试2
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/goView/test2")
    public final Object goView_test2(String key) {
        try{
            System.out.println("test2："+key);
            String p="";
            String rstr="{\"dimensions\":[\"product\",\"data1\"],\"source\":[{\"product\":\"Sat\",\"data1\":110},{\"product\":\"Sun\",\"data1\":130},{\"product\":\"Wed\",\"data1\":150},{\"product\":\"Thu\",\"data1\":80}]}";
            if(key!=null && !key.equals("")){
                System.out.println("key:"+key);
                p=key;
            }
            if(p.equals("1")){
                rstr="{\"dimensions\":[\"product\",\"data1\"],\"source\":[{\"product\":\"Mon\",\"data1\":120},{\"product\":\"Tue\",\"data1\":200},{\"product\":\"Wed\",\"data1\":150}]}";
            }else if(p.equals("2")){
                rstr="{\"dimensions\":[\"product\",\"data1\"],\"source\":[{\"product\":\"Thu\",\"data1\":80},{\"product\":\"Fri\",\"data1\":70}]}";
            }
            return Return_Info(0,"", JSONObject.parseObject(rstr));
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 蓝色大屏-动态数据测试3
     * sql传输
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/goView/sql")
    public final Object goView_test3(String key,  @RequestBody String sql) {
        try{
            System.out.println("test3："+key);
            System.out.println("sql:"+sql);
            String p="";
            String rstr="sql默认文本";
            if(key!=null && !key.equals("")){
                System.out.println("key:"+key);
                p=key;
            }

            if(p.equals("1")){
                rstr="sql选项1";
            }else if(p.equals("2")){
                rstr="sql选项2";
            }
            return Return_Info(0,"",rstr);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    //获取部门列表
    @RequestMapping(value = "/queryDeptByWebApi")
    @ApiOperation("获取部门列表")
    public final Object queryDeptByWebApi() {
        try{
            Paras pens = new Paras();
            pens.SQL="select No,Name,ParentNo from port_dept ";
            DataTable dt= DBAccess.RunSQLReturnTable(pens);
            return dt.Rows;
        }catch(Exception e){
            return Return_Info(500,"获取部门信息失败",e.getMessage());
        }
    }
    //获取部门列表
    @RequestMapping(value = "/queryDeptByWebApiParam/{parentNo}")
    @ApiOperation("获取部门列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="parentNo",value="父节点ID",required = false),
    })
    public final Object queryDeptByWebApiParam(@PathVariable String parentNo) {
        try{
            Paras pens = new Paras();
            pens.SQL="select No,Name,ParentNo from port_dept where ParentNo="+pens.getDBStr()+"v";
            pens.Add("v",parentNo);
            DataTable dt= DBAccess.RunSQLReturnTable(pens);
            return dt.Rows;
        }catch(Exception e){
            return Return_Info(500,"获取部门信息失败",e.getMessage());
        }
    }
    //获取部门列表
    @RequestMapping(value = "/queryDeptTreeByWebApi")
    @ApiOperation("获取部门树列表")
    public final Object queryDeptTreeByWebApi() {
        try{
            Paras pens = new Paras();
            pens.SQL="select No,Name,ParentNo from port_dept ";
            DataTable dt= DBAccess.RunSQLReturnTable(pens);
            return dt.Rows;
        }catch(Exception e){
            return Return_Info(500,"获取部门树信息失败",e.getMessage());
        }
    }
}
