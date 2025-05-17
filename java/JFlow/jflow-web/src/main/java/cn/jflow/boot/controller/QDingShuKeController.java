package cn.jflow.boot.controller;

import bp.da.*;
import bp.difference.handler.HttpHandlerBase;
import bp.en.QueryObject;
import bp.port.Emp;
import bp.wf.Dev2Interface;
import bp.wf.Flows;
import bp.wf.GenerWorkFlow;
import bp.wf.SendReturnObjs;
import bp.wf.template.FlowAttr;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


@RestController
@Api(tags = "千丁数科-API")
@RequestMapping(value = "/bpm-server/api/runtime/process-instances")
public class QDingShuKeController extends HttpHandlerBase {

    private static int CODE_START = 0; // 发起
    private static int CODE_SP = 1; // 审批
    private static int CODE_XS = 2; // 协商
    private static int CODE_BH = 3;// 驳回
    private static int CODE_CH = 4;// 撤回
    private static int CODE_ZZ = 5;// 终止

    public static Map<String, Object> returnInfo(int code, String msg, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("msg", msg);
        response.put("data", data);
        return response;
    }

    /**
     * 处理UserInfo
     *
     * @param userInfoHeaderValue
     * @return 用户信息
     */
    public String extractUserInfo(String userInfoHeaderValue) {
        if (DataType.IsNullOrEmpty(userInfoHeaderValue)) {
            return null;
        }
        byte[] decodedBytes = Base64.getDecoder().decode(userInfoHeaderValue); // 使用Base64的Decoder进行解码
        return new String(decodedBytes); // 将解码后的字节数组转换为字符串
    }

    /**
     * 处理FlowNo（流程标记解析成流程编号）
     *
     * @param processDefName
     * @return 流程编号
     */
    private String extractFlowNo(String processDefName) throws Exception {
        //流程标记
        Flows flows = new Flows();
        QueryObject qo = new QueryObject(flows);
        qo.AddWhere(FlowAttr.FlowMark, processDefName);
        qo.DoQuery();
        if (!flows.isEmpty()) {
            return flows.ToJavaList().get(0).getNo();
        }
        throw new RuntimeException("err@您输入的流程编码不存在，请确认流程编码 processDefName 是否正确。\nCCFLOW提示：" +
                "\n\tFlowMark=[" + processDefName + "],\n\tSQL=[" + qo.getSQL() + "]");
    }

    /**
     * JsonObject转换成一个特定格式的字符串
     *
     * @return 如: @ABC=DEF@taskTitle=xx提交的【xxx】申请，待您处理。...
     */
    public static String convertToCustomString(JsonObject jsonObject) {
        StringBuilder sb = new StringBuilder();
        for (String key : jsonObject.keySet()) {
            if (key.equals("_iframe_url_par")) {
                JsonObject paras = jsonObject.get(key).getAsJsonObject();
                StringBuilder val = new StringBuilder();
                for (String pKey : paras.keySet()) {
                    val.append("&").append(pKey).append(":").append(paras.get(pKey).getAsString());
                }
                sb.append("@").append("IframeUrlParas").append("=").append(val.substring(0));
            } else {
                sb.append("@").append(key).append("=").append(jsonObject.get(key).getAsString()).append("@");
            }
        }
        return sb.toString();
    }

    /**
     * 检查用户
     *
     * @param userID
     * @return
     * @throws Exception
     */
    public boolean checkEmp(String userID) throws Exception {
        //检查人员是否存在
        Emp emp = new Emp();
        emp.setUserID(userID);
        return emp.RetrieveFromDBSources() != 0;
    }

    /**
     * 处理异常
     *
     * @param e
     * @return
     */
    private ResponseEntity<Object> handleException(Exception e) {
        return new ResponseEntity<>(returnInfo(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), ""), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 创建流程实例
     *
     * @return 流程实例编号
     */
    @PostMapping
    @ApiOperation("创建流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jsonStr", value = "JSON数据", paramType = "body", required = true)
    })
    public ResponseEntity<Object> createProcessInstance(@RequestBody String jsonStr, HttpServletRequest request) {
        Log.DebugWriteInfo("Body => " + jsonStr);
        try {
            String userID = extractUserInfo(request.getHeader("X-ER-User-Info"));
            Log.DebugWriteInfo("Headers[X-ER-User-Info ] " + userID);
            if (!checkEmp(userID)) {
                return new ResponseEntity<>(returnInfo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "err@人员[" + userID + "]不存在", ""), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
            String processDefName = jsonObject.get("processDefName").getAsString();
            String flowNo = extractFlowNo(processDefName); // 根据流程标记获取流程编号
            Map<String, Object> resultMap = createWorkflow(jsonObject, flowNo, userID); // 执行创建流程的逻辑
            long workid = (long) resultMap.get("workid");
            String msg = (String) resultMap.get("msg");
            Log.DebugWriteInfo("流程实例编号workid = " + workid);
            return new ResponseEntity<>(returnInfo(HttpStatus.CREATED.value(), DataType.IsNullOrEmpty(msg) ? "创建流程实例成功" : "创建流程实例成功" + msg, String.valueOf(workid)), HttpStatus.CREATED);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private Map<String, Object> createWorkflow(JsonObject jsonObject, String flowNo, String userID) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "");
        long workid;
        bp.wf.Dev2Interface.Port_Login(userID);
        workid = Dev2Interface.Node_CreateBlankWork(flowNo, userID);
        map.put("workid", workid);
        JsonElement spje = jsonObject.get("startParam");
        if (!DataType.IsNullOrEmpty(spje)) {
            JsonObject startParam = spje.getAsJsonObject();
            JsonObject variables = startParam.getAsJsonObject("variables");
            String atPara = convertToCustomString(variables);
            try {
                //执行保存参数接口
                bp.wf.Dev2Interface.Flow_SaveParas(workid, atPara);
            } catch (Exception ex) {
                Log.DebugWriteError("保存参数失败[Flow_SaveParas] ，" + ex.getMessage());
            }
            //设置发起标题
            setTitle(workid, CODE_START);
            //完成第一个工作(finishFirstWorkItem)
            JsonElement ffwje = startParam.get("finishFirstWorkItem");
            if (!DataType.IsNullOrEmpty(ffwje) && "true".equals(ffwje.getAsString())) {
                // finishFirstWorkItem=true 发送到下个节点
                SendReturnObjs returnObjs = Dev2Interface.Node_SendWork(flowNo, workid);
                //设置审批标题
                setTitle(workid, CODE_SP);
                map.put("msg", "并完成第一个工作项，返回信息：" + bp.tools.Json.ToJson(returnObjs));
            }
        }

        return map;
    }

    /**
     * 根据状态码更新标题
     *
     * @param workid 流程实例编号
     * @param code   状态码
     */
    private void setTitle(long workid, int code) {
        // 获取待办数据
        GenerWorkFlow gwf = new GenerWorkFlow();
        int i = 0;
        try {
            gwf.setWorkID(workid);
            i = gwf.RetrieveFromDBSources();
        } catch (Exception ex) {
            Log.DebugWriteError(ex);
        }

        // 如果存在待办数据就执行下面的逻辑
        if (i > 0) {
            // 获取参数
            AtPara atPara = gwf.getatPara();

            String taskTitle = atPara.GetValStrByKey("taskTitle");
            String title = "";
            String gwftitle = gwf.getTitle();
            if (!DataType.IsNullOrEmpty(taskTitle)) { // 存在taskTitle值时，将标题设置为taskTitle
                title = taskTitle;
            } else {
                title = gwftitle;
            }

            //判断状态码，根据状态码追加标题状态
            switch (code) {
                case 1:
                    title = "【审批】 " + title;
                    break;
                case 2:
                    title = "【协商】 " + title;
                    break;
                case 3:
                    title = "【驳回】 " + title;
                    break;
                case 4:
                    title = "【撤回】 " + title;
                    break;
                case 5:
                    title = "【终止】 " + title;
                    break;
                default:
                    break;
            }

            //执行更新标题逻辑
            try {
                gwf.setTitle(title);
                gwf.DirectUpdate();
            } catch (Exception ex) {
                Log.DebugWriteError(ex);
            }
        }
    }

    /**
     * 启动流程实例
     *
     * @param processInstId
     * @return
     */
    @PutMapping("/{processInstId}/actions/start")
    @ApiOperation("启动流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processInstId", value = "流程实例编号", paramType = "path", required = true)
    })
    public ResponseEntity<Object> startProcessInstance(@PathVariable Long processInstId, @RequestBody String jsonStr, HttpServletRequest request) {
        try {
            Log.DebugWriteInfo("流程实例编号processInstId = " + processInstId);
            String gaiaApiKey = request.getHeader("X-Gaia-Api-Key"); // 网关令牌 -- 暂时用不到
            String userInfo = request.getHeader("X-ER-User-Info"); // 操作人员登录名
            String systemInfo = request.getHeader("X-ER-system-Info"); // 系统编号 -- 暂时用不到
            String userID = extractUserInfo(userInfo);
            Log.DebugWriteInfo("Headers[X-ER-User-Info ] " + userInfo + " => " + userID);

            //检查人员是否存在
            if (!checkEmp(userID)) {
                return new ResponseEntity<>(returnInfo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "err@人员[" + userID + "]不存在", ""), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            bp.wf.Dev2Interface.Port_Login(userID); //执行CCFLOW登录

            GenerWorkFlow gwf = new GenerWorkFlow();
            gwf.setWorkID(processInstId);
            int i = gwf.RetrieveFromDBSources();
            if (i == 0) {
                return new ResponseEntity<>(returnInfo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "err@流程实例为[" + processInstId + "]不存在", ""), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            JsonElement je = JsonParser.parseString(jsonStr);
            JsonObject jsonObject = je.getAsJsonObject(); // 将JsonElement转换为JsonObject

            JsonObject startParam = jsonObject.getAsJsonObject("startParam");

            if (!DataType.IsNullOrEmpty(startParam)) {
                JsonObject variables = startParam.getAsJsonObject("variables");
                String atPara = convertToCustomString(variables); // 生成参数字段的值
                boolean b = bp.wf.Dev2Interface.Flow_SaveParas(processInstId, atPara); //执行CCFLOW保存参数接口
                if (!b) {
                    return new ResponseEntity<>(returnInfo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "err@参数保存失败，", ""), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            SendReturnObjs returnObjs = Dev2Interface.Node_SendWork(gwf.getFlowNo(), processInstId);//执行CCFLOW流程发送接口

            //设置审批标题
            setTitle(processInstId, CODE_SP);

            return new ResponseEntity<>(returnInfo(HttpStatus.OK.value(), bp.tools.Json.ToJson(returnObjs), ""), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 流程信息暂存
     *
     * @param processInstId
     * @return
     */
    @PutMapping("/{processInstId}/actions/save")
    @ApiOperation("流程信息暂存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processInstId", value = "流程实例编号", paramType = "path", required = true)
    })
    public ResponseEntity<Object> saveProcessInstance(@PathVariable Long processInstId, HttpServletRequest request) {
        try {
            Log.DebugWriteInfo("流程实例编号processInstId = " + processInstId);
            String gaiaApiKey = request.getHeader("X-Gaia-Api-Key"); // 网关令牌 -- 暂时用不到
            String userInfo = request.getHeader("X-ER-User-Info"); // 操作人员登录名
            String systemInfo = request.getHeader("X-ER-system-Info"); // 系统编号 -- 暂时用不到
            String userID = extractUserInfo(userInfo);
            Log.DebugWriteInfo("Headers[X-ER-User-Info ] " + userInfo + " => " + userID);

            //检查人员是否存在
            if (!checkEmp(userID)) {
                return new ResponseEntity<>(returnInfo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "err@人员[" + userID + "]不存在", ""), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            bp.wf.Dev2Interface.Port_Login(userID); //执行CCFLOW登录

            String msg = Dev2Interface.Node_SaveWork(processInstId, new Hashtable<>());

            return new ResponseEntity<>(returnInfo(HttpStatus.OK.value(), msg, ""), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 根据流程实例id撤回流程
     *
     * @param processInstId
     * @return
     */
    @PutMapping("{processInstId}/actions/revocation")
    @ApiOperation("根据流程实例id撤回流程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processInstId", value = "流程实例编号", paramType = "path", required = true)
    })
    public ResponseEntity<Object> revocationProcessInstance(@PathVariable Long processInstId, HttpServletRequest request) {
        try {
            Log.DebugWriteInfo("流程实例编号processInstId = " + processInstId);
            String gaiaApiKey = request.getHeader("X-Gaia-Api-Key"); // 网关令牌 -- 暂时用不到
            String userInfo = request.getHeader("X-ER-User-Info"); // 操作人员登录名
            String systemInfo = request.getHeader("X-ER-system-Info"); // 系统编号 -- 暂时用不到
            String userID = extractUserInfo(userInfo);
            Log.DebugWriteInfo("Headers[X-ER-User-Info ] " + userInfo + " => " + userID);

            //检查人员是否存在
            if (!checkEmp(userID)) {
                return new ResponseEntity<>(returnInfo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "err@人员[" + userID + "]不存在", ""), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            bp.wf.Dev2Interface.Port_Login(userID); // 执行CCFLOW登录

            String msg = Dev2Interface.Flow_DoUnSend(null, processInstId, 0); // 执行CCFLOW撤销发送操作

            //设置撤回标题
            setTitle(processInstId, CODE_CH);

            return new ResponseEntity<>(returnInfo(HttpStatus.OK.value(), msg, ""), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /**
     * 终止流程实例
     *
     * @param processInstId
     * @return
     */
    @PutMapping("{processInstId}/actions/terminate")
    @ApiOperation("终止流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processInstId", value = "流程实例编号", paramType = "path", required = true)
    })
    public ResponseEntity<Object> terminateProcessInstance(@PathVariable Long processInstId, HttpServletRequest request) {
        try {
            Log.DebugWriteInfo("流程实例编号processInstId = " + processInstId);
            String gaiaApiKey = request.getHeader("X-Gaia-Api-Key"); // 网关令牌 -- 暂时用不到
            String userInfo = request.getHeader("X-ER-User-Info"); // 操作人员登录名www
            String systemInfo = request.getHeader("X-ER-system-Info"); // 系统编号 -- 暂时用不到
            String userID = extractUserInfo(userInfo);
            Log.DebugWriteInfo("Headers[X-ER-User-Info ] " + userInfo + " => " + userID);

            //检查人员是否存在
            if (!checkEmp(userID)) {
                return new ResponseEntity<>(returnInfo(HttpStatus.INTERNAL_SERVER_ERROR.value(), "err@人员[" + userID + "]不存在", ""), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            bp.wf.Dev2Interface.Port_Login(userID); //执行CCFLOW登录

            String msg = Dev2Interface.Flow_DoFlowOver(processInstId, "用户[" + userInfo + "]终止了流程，流程实例编号[" + processInstId + "]");

            //设置终止标题
            setTitle(processInstId, CODE_ZZ);

            return new ResponseEntity<>(returnInfo(HttpStatus.OK.value(), msg, ""), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @Override
    public Class getCtrlType() {
        return QDingShuKeController.class;
    }
}
