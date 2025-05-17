package cn.jflow.boot.ClientDevDemo;

import bp.da.DataType;
import bp.tools.HttpConnectionManager;
import net.sf.json.JSONObject;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dev2InterfaceRestfulAPI {
    //BPM服务器地址.
    public static String BPMHost = bp.difference.SystemConfig.getAppSettings().get("HostURL")+"/WF/API";
    //密钥.
    public static String PrivateKey = "DiGuaDiGua,IamCCBPM";

    //拼接访问地址
    static String webPath = "";
    //#endregion 公共变量.

    //#region 组织结构API.
    /// <summary>
    /// 系统登录
    /// </summary>
    /// <param name="privateKey">密钥,默认:DiGuaDiGua,IamCCBPM,存储在:web.config的 PrivateKey 节点里.</param>
    /// <param name="userNo">登录账号: 如果是SAAS模式，则使用短号即可，比如001.</param>
    /// <param name="orgNo">组织编号:对集团，SAAS版有效.</param>
    /// <returns>人员信息</returns>
    public static String Port_Login(String userNo, String orgNo )
    {

        webPath = BPMHost + "/Port_Login";
        String postData = HttpPostConnect(webPath + "?userNo=" + userNo + "&privateKey=" + PrivateKey + "&orgNo=" + orgNo, null,"","");

        return postData;
    }
    public static String Port_LoginReToken(String userNo, String orgNo)
    {
        String data = Port_Login(userNo, orgNo);

        JSONObject jd = JSONObject.fromObject(data);

        String token =  JSONObject.fromObject(jd.get("data").toString()).get("Token").toString();
        return token;
    }
    /// <summary>
    /// 登出
    /// </summary>
    /// <param name="token"></param>
    /// <returns></returns>
    public static String Port_LoginOut(String token)
    {
        webPath = BPMHost + "/Port_LoginOut";
        String postData = HttpPostConnect(webPath + "?token=" + token, null,"","");

        return postData;
    }

    /// <summary>
    /// 保存用户数据, 如果有此数据则修改，无此数据则增加.
    /// 注意:
    /// 1. 该方法允许多次调用.
    /// 2  比如一个人一个部门的时候，就调用一次即可,传入部门编号, 与该部门下的岗位编号集合001,003,004.
    /// 3  比如一个人n个部门的时候，就调用n次,传入部门编号, 与该部门下的岗位编号集合001,002,003.
    /// </summary>
    /// <param name="orgNo">组织编号</param>
    /// <param name="userNo">用户编号,如果是saas版本就是orgNo_userID</param>
    /// <param name="userName">用户名称</param>
    /// <param name="deptNo">部门编号</param>
    /// <param name="kvs">属性值，比如: @Name=张三@Tel=18778882345@Pass=123, 如果是saas模式：就必须有@UserID=xxxx </param>
    /// <param name="stats">角色编号：比如:001,002,003,</param>
    /// <returns>执行信息.</returns>
    public static String Port_Emp_Save(String token, String orgNo, String userNo, String userName, String deptNo, String kvs, String stats)
    {
        webPath = BPMHost + "/Port_Emp_Save";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&orgNo=" + orgNo + "&userNo=" + userNo + "&userName=" + userName + "&deptNo=" + deptNo + "&kvs=" + kvs + "&stats=" + stats, null,"","");
        return postData;
    }
    /// <summary>
    /// 人员删除
    /// </summary>
    /// <param name="token">Token</param>
    /// <param name="userNo">人员编号</param>
    /// <returns>reutrn 1=成功,  其他的标识异常.</returns>
    public static String Port_Emp_Delete(String token, String userNo)
    {
        webPath = BPMHost + "/Port_Emp_Delete";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&userNo=" + userNo, null,"","");
        return postData;
    }
    /// <summary>
    /// 删除兼职部门
    /// </summary>
    /// <param name="token"></param>
    /// <param name="orgNo">组织编号:对于saas模式有效</param>
    /// <param name="userNo">用户编号</param>
    /// <param name="deptNo">删除的兼职部门编号，在该部门下的角色也被删除</param>
    /// <returns>执行结果</returns>
    public static String Port_Emp_DeleteOneDept(String token, String orgNo, String userNo, String deptNo)
    {
        webPath = BPMHost + "/Port_Emp_DeleteOneDept";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&orgNo=" + orgNo + "&userNo=" + userNo + "&deptNo=" + deptNo, null,"","");
        return postData;
    }

    /// <summary>
    /// 角色类型保存
    /// </summary>
    /// <param name="token">token</param>
    /// <param name="orgNo">组织编号：对集团版、SAAS版有效.</param>
    /// <param name="no">编号</param>
    /// <param name="name">名称</param>
    /// <param name="keyVals">属性值，可以为空.</param>
    /// <returns>执行结果</returns>
    public static String Port_StationType_Save(String token, String orgNo, String no, String name, String keyVals)
    {
        webPath = BPMHost + "/Port_StationType_Save";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&orgNo=" + orgNo + "&no=" + no + "&name=" + name + "&keyVals=" + keyVals, null,"","");
        return postData;
    }
    /// <summary>
    /// 角色类型删除
    /// </summary>
    /// <param name="token">token</param>
    /// <param name="orgNo">组织编号：对集团版、SAAS版有效.</param>
    /// <returns>执行结果</returns>
    public static String Port_StationType_Delete(String token, String orgNo, String no)
    {
        webPath = BPMHost + "/Port_StationType_Delete";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&orgNo=" + orgNo + "&no=" + no, null,"","");
        return postData;
    }
    /// <summary>
    /// 保存岗位, 如果有此数据则修改，无此数据则增加.
    /// </summary>
    /// <param name="token">Token</param>
    /// <param name="orgNo">组织编号</param>
    /// <param name="no">岗位编号</param>
    /// <param name="name">岗位名称</param>
    /// <param name="stationTypeNo">岗位类型编号</param>
    /// <param name="keyVals">其他值</param>
    /// <returns></returns>
    public static String Port_Station_Save(String token, String orgNo, String no, String name, String keyVals)
    {
        webPath = BPMHost + "/Port_Station_Save";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&orgNo=" + orgNo + "&no=" + no + "&name=" + name + "&keyVals=" + keyVals, null,"","");
        return postData;
    }
    /// <summary>
    /// 岗位删除
    /// </summary>
    /// <param name="token">Token</param>
    /// <param name="no">删除指定的岗位编号</param>
    /// <returns>执行结果</returns>
    public static String Port_Station_Delete(String token, String no)
    {
        webPath = BPMHost + "/Port_Station_Delete";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&no=" + no, null,"","");
        return postData;
    }
    /// <summary>
    /// 保存部门,如果有此数据则修改,无此数据则增加.
    /// </summary>
    /// <param name="token">Token</param>
    /// <param name="orgNo">组织编号</param>
    /// <param name="no">部门编号</param>
    /// <param name="name">名称</param>
    /// <param name="parentNo">父节点编号</param>
    /// <param name="keyVals">其他的值:@Leaer=zhangsan@Tel=18660153393@Idx=1</param>
    /// <returns></returns>
    public static String Port_Dept_Save(String token, String orgNo, String no, String name, String parentNo, String keyVals)
    {
        webPath = BPMHost + "/Port_Dept_Save";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&orgNo=" + orgNo + "&no=" + no + "&name=" + name + "&parentNo=" + parentNo + "&keyVals=" + keyVals, null,"","");
        return postData;
    }
    /// <summary>
    /// 删除部门
    /// </summary>
    /// <param name="token">Token</param>
    /// <param name="no">要删除的部门编号</param>
    /// <returns></returns>
    public static String Port_Dept_Delete(String token, String no)
    {
        webPath = BPMHost + "/Port_Dept_Delete";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&no=" + no, null,"","");
        return postData;
    }
    //#endregion 组织结构API.

    //#region 流程API.
    public static String DealResult(String postData, String errMsgTitle) throws Exception {
        JSONObject data = JSONObject.fromObject(postData);
        //JsonData data = JsonMapper.ToObject(postData);
        if (data.get("code").toString().equals("200"))
            return  data.get("data").toString();

        throw new Exception(errMsgTitle + ":" + data.get("message").toString());
    }
    /// <summary>
    /// 创建WorkID
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="flowNo">流程编号</param>
    /// <returns>流程实例int64类型的workid</returns>
    public static int Node_CreateBlankWorkID(String token, String flowNo) throws Exception {
        webPath = BPMHost + "/Node_CreateBlankWorkID";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&flowNo=" + flowNo, null,"","");

        String data = DealResult(postData, "创建WorkID错误.");
        return Integer.valueOf(data);
    }
    /// <summary>
    /// 设置草稿
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="workID">流程实例ID</param>
    /// <returns></returns>
    public static String Node_SetDraft(String token, int workID) throws Exception {
        webPath = BPMHost + "/Node_SetDraft";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&workID=" + workID, null,"","");

        return DealResult(postData, "设置草稿错误.");
    }

    /// <summary>
    /// 保存参数到WF_GenerWorkFlow,用于方向条件的判断
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="workID">工作ID</param>
    /// <param name="paras">要保存的参数，用于控制流程转向等的参数变量，注意:数据并没有保存到表单, 格式： @Key1=Val2@Key2=Val2 </param>
    /// <returns>执行结果</returns>
    public static String Flow_SaveParas(String token, int workID, String paras) throws Exception {
        webPath = BPMHost + "/Node_SaveParas";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&workID=" + workID + "&paras=" + paras, null,"","");
        return DealResult(postData, "保存参数错误.");
    }
    /// <summary>
    /// 保存节点表单数据
    /// </summary>
    /// <param name="token"></param>
    /// <param name="workID">工作id</param>
    /// <param name="keyVals">要保存的主表数据，格式： @Key1=Val2@Key2=Val2  @Em=zhangsan@ccflow.org@Tel=18660153393 </param>
    /// <param name="dtlJSON">从表数据,格式： 多个从表的表名按照与从表控件的ID对应，字段与控件的字段对应。 </param>
    /// <param name="athJSON">附件数据,格式： 多个附件: 格式 FileName, FileUrl。一定是 FileName 在前，FileUrl 在后。
    /// 格式： {'Ath1':[{'FileName':'我的附件1.doc','FileUrl':'http://localst:9003:/xxx.docx'},{'FileName':'我的附件2.doc','FileUrl':'http://localst:9003:/xxx.docx'}]} </param>
    /// <returns>执行结果</returns>
    public static String Node_SaveWork(String token, int workID, String keyVals, String dtlJSON , String athJSON )
    {
        webPath = BPMHost + "/Node_SaveWork";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&workID=" + workID + "&keyVals=" + keyVals
                + "&dtlJSON=" + dtlJSON + "&athJSON=" + athJSON, null,"","");
        return postData;
    }

    /// <summary>
    /// 发送接口
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="workID">工作实例WorkID</param>
    /// <param name="toNodeIDStr">到达的下一个节点:默认为0,可以是节点Mark,该标记在节点属性里维护. 0则是按设计器设置的方向条件计算.</param>
    /// <param name="toEmps">下一个节点的接收人:多人用逗号分开比如:zhangsan,lisi. 如果为空按设置的接受人规则计算.</param>
    /// <param name="paras">流程参数:用于控制节点方向或者接收人.数据保存到WF_GenerWorkFlow,用与参数条件,格式: @key1=val1@Key2=Val2</param>
    /// <param name="checkNote">审核意见:需要启用了审核组件，就需要填写审核意见,否则不让发送。</param>
    /// <returns>执行结果,可以直接提示给用户.</returns>
    public static String Node_SendWork(String token, int workID, String toNodeIDStr , String toEmps , String paras , String checkNote) throws Exception {
        webPath = BPMHost + "/Node_SendWork";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&workID=" + workID + "&toNodeIDStr=" + toNodeIDStr
                + "&toEmps=" + toEmps + "&paras=" + paras + "&checkNote=" + checkNote, null,"","");

        return DealResult(postData, "发送错误.");
    }

    /// <summary>
    /// 当前节点执行退回操作
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="workID">工作实例WorkID</param>
    /// <param name="returnToNodeIDStr">退回到的节点,可以是节点的Mark, 如果为空，就是退回上一个节点.</param>
    /// <param name="returnToEmp">退回到的接收人</param>
    /// <param name="msg">退回原因</param>
    /// <param name="isBackToThisNode">是否原路返回到当前节点</param>
    /// <returns></returns>
    public static String Node_ReturnWork(String token, int workID, int returnToNodeIDStr, String returnToEmp, String msg, boolean isBackToThisNode)
    {
        webPath = BPMHost + "/Node_ReturnWork";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&workID=" + workID + "&returnToNodeIDStr=" + returnToNodeIDStr
                + "&returnToEmp=" + returnToEmp + "&msg=" + msg + "&isBackToThisNode=" + isBackToThisNode, null,"","");
        return postData;
    }
    //#endregion 节点API.
    // <summary>
    /// 批量撤回
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="workids">流程ID集合，多个以逗号分隔</param>
    /// <returns></returns>
    public static String Flow_DoUnSend(String token, String workIDs)
    {
        webPath = BPMHost + "/Flow_DoUnSend";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&workIDs=" + workIDs, null,"","");
        return postData;
    }
    /// <summary>
    /// 催办
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="workIDs">工作实例的WorkID集合</param>
    /// <param name="msg">催办信息</param>
    /// <returns></returns>
    public static String Flow_DoPress(String token, String workIDs, String msg) throws Exception {
        webPath = BPMHost + "/Flow_DoPress";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&workIDs=" + workIDs + "&msg=" + msg, null,"","");

        return DealResult(postData, "催办错误.");
    }
    /// <summary>
    /// 设置标题
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="workID"></param>
    /// <param name="title"></param>
    /// <returns></returns>
    public static String Flow_SetTitle(String token, int workID, String title) throws Exception {
        webPath = BPMHost + "/Flow_SetTitle";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&workID=" + workID + "&title=" + title, null,"","");

        return DealResult(postData, "设置标题错误.");
    }
    /// <summary>
    /// 设置父流程信息
    /// </summary>
    /// <param name="subFlowNo">子流程编号</param>
    /// <param name="subFlowWorkID">子流程workid</param>
    /// <param name="parentWorkID">父流程WorkID</param>
    /// <param name="isCopyData">是否要copy父流程的数据</param>
    public static void Flow_SetParentInfo(String token, String subFlowNo, int subFlowWorkID, int parentWorkID, boolean isCopyData)
    {
    }
    /// <summary>
    /// 把当前工作移交给指定的人员
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="workID">流程workid</param>
    /// <param name="toEmpNo">移交给的人员账号</param>
    /// <param name="msg">移交信息</param>
    /// <returns></returns>
    public static String Node_Shift(String token, int workID, String toEmpNo, String msg)
    {
        webPath = BPMHost + "/Node_Shift";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&workID=" + workID + "&toEmpNo=" + toEmpNo + "&msg=" + msg, null,"","");
        return postData;
    }
    /// <summary>
    /// 给当前的工作增加处理人
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="workID">流程workid</param>
    /// <param name="empNo">增加人员的账号</param>
    /// <returns></returns>
    public static String Node_AddTodolist(String token, int workID, String empNo)
    {
        webPath = BPMHost + "/Node_AddTodolist";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&workID=" + workID + "&empNo=" + empNo, null,"","");
        return postData;
    }
    /// <summary>
    /// 批量结束流程
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="workids">流程实例ID集合，以逗号分开</param>
    /// <param name="stopFlowType">结束类型,写入到WF_GenerWorkFlow， AtPara字段 1=正常结束,0=非正常结束</param>
    /// <returns></returns>
    public static String Flow_DoFlowOver(String token, String workIDs, int stopFlowType )
    {
        if(DataType.IsNullOrEmpty(stopFlowType)){
            stopFlowType = 1;
        }
        webPath = BPMHost + "/Flow_DoFlowOver";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&workIDs=" + workIDs + "&stopFlowType=" + stopFlowType, null,"","");
        return postData;
    }
    /// <summary>
    /// 批量删除
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="workids">流程workid集合，以逗号分隔</param>
    /// <returns></returns>
    public static String Batch_Delete(String token, String workIDs)
    {
        webPath = BPMHost + "/Batch_Delete";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&workIDs=" + workIDs, null,"","");
        return postData;
    }
    /// <summary>
    /// 执行抄送
    /// </summary>
    /// <param name="token">token</param>
    /// <param name="fk_node">抄送的节点ID</param>
    /// <param name="workID">工作ID</param>
    /// <param name="toEmpNo">抄送人员编号</param>
    /// <param name="toEmpName">抄送人员人员名称</param>
    /// <param name="msgTitle">标题</param>
    /// <param name="msgDoc">内容</param>
    /// <returns>执行信息</returns>
    public static String Node_CC_WriteTo_CClist(String token, int fk_node, int workID, String toEmpNo, String toEmpName, String msgTitle, String msgDoc) throws Exception {
        webPath = BPMHost + "/Node_CC_WriteTo_CClist";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&fk_node=" + fk_node + "&workID=" + workID
                + "&toEmpNo=" + toEmpNo + "&toEmpName=" + toEmpName + "&msgTitle=" + msgTitle + "&msgDoc=" + msgDoc, null,"","");

        return DealResult(postData, "抄送错误.");
    }

    // <summary>
    /// 可以发起的流程
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="domainExt">流程所属的域</param>
    /// <returns></returns>
    public static String DB_Start(String token, String domainExt) throws Exception {
        webPath = BPMHost + "/DB_Start";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&domainExt=" + domainExt, null,"","");

        return DealResult(postData, "获取可以发起的流程列表错误.");
    }

    /// <summary>
    /// 待办
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="domainExt"></param>
    /// <returns></returns>
    public static String DB_Todolist(String token, String domainExt ) throws Exception {
        webPath = BPMHost + "/DB_Todolist";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&domainExt=" + domainExt, null,"","");

        return DealResult(postData, "获取待办流程列表错误.");
    }

    /// <summary>
    /// 待办(待阅/已阅)
    /// </summary>
    /// <param name="token"></param>
    /// <param name="isRead">0=待阅，1=已阅</param>
    /// <param name="domainExt"></param>
    /// <returns></returns>
    public static String DB_TodolistByIsRead(String token, String isRead, String domainExt) throws Exception {
        webPath = BPMHost + "/DB_TodolistByIsRead";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&isRead=" + isRead + "&domainExt=" + domainExt, null,"","");

        return DealResult(postData, "获取待办(待阅/已阅)流程列表错误.");
    }
    /// <summary>
    /// 在途
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="domainExt"></param>
    /// <returns></returns>
    public static String DB_Runing(String token, String domainExt) throws Exception {
        webPath = BPMHost + "/DB_Runing";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&domainExt=" + domainExt, null,"","");

        return DealResult(postData, "获取在途流程列表错误.");
    }
    /// <summary>
    /// 草稿
    /// </summary>
    /// <param name="token">密钥</param>
    /// <param name="domainExt"></param>
    /// <returns></returns>
    public static String DB_Draft(String token, String domainExt) throws Exception {
        webPath = BPMHost + "/DB_Draft";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&domainExt=" + domainExt, null,"","");

        return DealResult(postData, "获取草稿列表错误.");
    }
    /// <summary>
    /// 根据已读未读参数获取抄送列表数据
    /// </summary>
    /// <param name="token">Token</param>
    /// <param name="isRead">是否已读，0=未读，1=已读</param>
    /// <returns></returns>
    public static String DB_CCListByIsRead(String token, String isRead) throws Exception {
        webPath = BPMHost + "/DB_CCListByIsRead";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&isRead=" + isRead, null,"","");

        return DealResult(postData, "根据已读未读参数获取抄送列表数据错误.");
    }
    /// <summary>
    /// 抄送列表
    /// </summary>
    /// <param name="token">token</param>
    /// <param name="domainExt">流程所属的域, 可以为空</param>
    /// <param name="flowNo">指定的流程，可以为空.</param>
    /// <returns>抄送列表</returns>
    public static String DB_CCList(String token, String domainExt, String flowNo) throws Exception {
        webPath = BPMHost + "/DB_CCList";
        String postData = HttpPostConnect(webPath + "?token=" + token + "&domainExt=" + domainExt + "&flowNo=" + flowNo, null,"","");

        return DealResult(postData, "获取抄送列表错误.");
    }

    public static String HttpPostConnect(String url, Map<String, String> param, String header, String context) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpConnectionManager.getHttpClient();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            if(DataType.IsNullOrEmpty(context)== false)
                httpPost.setHeader(header,context);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (Object key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key.toString(), param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, Consts.UTF_8);

                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            if(response.getStatusLine().getStatusCode()==302){
                return "";
            }

            resultString = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }
}
