package bp.wf.httphandler;

import bp.da.Log;
import bp.tools.HttpClientUtil;
import bp.tools.StringUtils;
import net.sf.json.JSONObject;

public class chatGPTUtil {

    //ONLY for test flowable microservice
    private static final String CCFLOWAI_URL = "http://localhost:3200";

    private static final String SQL_BY_AI = "/generate/sql/";
    private static final String TABLE_BY_PROMPT = "/generate/table";

    //haowz 20240510
    public static String generateAttrs( String prompt)
    {
        String jsonBody = "{ "+
            "\"prompt\": \"设计一个车辆基本信息VehicleInfo表，只要一个表设计，不需要描述解释，只要纯json格式代码。字段数组为columns，每个字段属性为name，cnname，datatype，length\", "+
            "\"parentMessageId\": \"\", "+
            "\"persona_id\": \"\"," +
            "\"options\": {" +
            "\"model\": \"gpt-4\"," +
            "\"temperature\": 0.8," +
            "\"presence_penalty\": 0,"+
            "\"frequency_penalty\": 0,"+
            "\"max_tokens\": 1888"+
            "}"+
        "}";

        JSONObject jsonData = JSONObject.fromObject(jsonBody);
        jsonData.put("prompt", prompt + " 只要一个表设计，不需要描述解释，只要纯json格式代码。字段数组为columns，每个字段属性为name，cnname，datatype，length");

        String url = CCFLOWAI_URL + TABLE_BY_PROMPT;
        String resp =  HttpClientUtil.doPostJson(url, jsonData.toString());
        Log.DebugWriteInfo(resp);

        if (!verifyHttpStatus(resp)) {
            return "err@" + resp;
        }

        if (StringUtils.isNotEmpty(resp) )
        {
            return resp;
        }
        return "err@";
    }

    private static boolean verifyHttpStatus( String resp)
    {
        try {
            JSONObject o1 = JSONObject.fromObject(resp);
            if (o1 != null && o1.containsKey("status")) {
                String status = String.format("%s", o1.get("status"));
                if (StringUtils.isNotEmpty(status) && !status.equals("200")) {
                    Log.DebugWriteError(resp);
                    return false;
                }
            }
        }
        catch ( Exception e)
        {
            Log.DebugWriteError(e);
            return false;
        }
        return true;
    }
}
