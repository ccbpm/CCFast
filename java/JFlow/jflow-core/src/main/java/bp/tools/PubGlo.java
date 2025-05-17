package bp.tools;

import bp.da.*;
import bp.en.*;
import bp.sys.*;
import bp.web.*;
import bp.difference.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

public class PubGlo {

     /**
     * 把一维度的datatable行变列转化未2D的.
     * 比如：列,政治面貌, 性别 , 个数.
     * @param dt
     * @return
     */
    public static DataTable DataTable2D(DataTable dt)
    {

        ///#region 组织字符串.
        String d1 = ","; //维度1 性别
        String d2 = ","; //维度2 政治面貌
        for (DataRow dr : dt.Rows)
        {
            String v1 = dr.getValue(0).toString();
            String v2 = dr.getValue(1).toString();

            if (d1.contains("," + v1 + ",") == false)
                d1 = d1 + v1 + ",";

            if (d2.contains("," + v2) == false)
                d2 = d2 + v2 + ",";
        }
        ///#endregion 组织字符串.

        ///#region 增加列. 男,女.
        DataTable mydt = new DataTable();
        mydt.Columns.Add(dt.Columns.get(0).ColumnName); //维度2列.
        String[] strsD1 = d1.split(",");
        for (String str : strsD1)
        {
            if (DataType.IsNullOrEmpty(str)) continue;
            mydt.Columns.Add(str);
        }
        ///#endregion 增加列.


        //增加行. 政治面貌: 团员,党员,其他.
        String[] strsD2 = d2.split(",");
        for (String myd2str : strsD2)
        {
            if (DataType.IsNullOrEmpty(myd2str)) continue;

            DataRow dr = mydt.NewRow();
            dr.setValue(0, myd2str); //团员

            for (String myd1Str : strsD1)
            {
                if (DataType.IsNullOrEmpty(myd1Str)) continue;

                //从原始数据里，获取 行+列对应的数值..
                String num = " - ";
                for (DataRow dtDR : dt.Rows)
                {
                    String myD0 = dtDR.getValue(0).toString();
                    String myD1 = dtDR.getValue(1).toString();
                    String myNum = dtDR.getValue(2).toString();

                    if (myD0 == null || myD1 == null || myNum == null)
                        continue;

                    if (myD0.equals(myd1Str) == true && myD1.equals(myd2str) == true)
                    {
                        num = myNum;
                        break;
                    }
                }
                dr.setValue(myd1Str, num);
            }
            mydt.Rows.add(dr);
        }
        return mydt;
    }
    ///#region 表达式替换

    /**
     * 表达式替换
     * <p>
     * param exp
     * param en
     *
     * @return
     */
    public static String DealExp(String exp, Entity en) {
        //替换字符
        exp = exp.replace("~", "'");

        if (exp.contains("@") == false) {
            return exp;
        }

        //首先替换加; 的。
        exp = exp.replace("@WebUser.No;", WebUser.getNo());
        exp = exp.replace("@WebUser.Name;", WebUser.getName());
        exp = exp.replace("@WebUser.FK_DeptName;", WebUser.getDeptName());
        exp = exp.replace("@WebUser.FK_Dept;", WebUser.getDeptNo());
        exp = exp.replace("@WebUser.DeptNo;", WebUser.getDeptNo());


        // 替换没有 ; 的 .
        exp = exp.replace("@WebUser.No", WebUser.getNo());
        exp = exp.replace("@WebUser.Name", WebUser.getName());
        exp = exp.replace("@WebUser.FK_DeptName", WebUser.getDeptName());
        exp = exp.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
        exp = exp.replace("@WebUser.DeptNo", WebUser.getDeptNo());

        if (exp.contains("@") == false) {
            return exp;
        }

        //增加对新规则的支持. @MyField; 格式.
        if (en != null) {
            Attrs attrs = en.getEnMap().getAttrs();
            Row row = en.getRow();
            //特殊判断.
            if (row.containsKey("OID") == true) {
                exp = exp.replace("@WorkID", row.get("OID").toString());
            }

            if (exp.contains("@") == false) {
                return exp;
            }

            for (Object key : row.keySet()) {
                //值为空或者null不替换
                if (row.get(key) == null || row.get(key).equals("") == true) {
                    continue;
                }

                if (exp.contains("@" + key)) {
                    Attr attr = attrs.GetAttrByKeyOfEn(String.valueOf(key));
                    //是枚举或者外键替换成文本
                    if (attr.getMyFieldType() == FieldType.Enum || attr.getMyFieldType() == FieldType.PKEnum || attr.getMyFieldType() == FieldType.FK || attr.getMyFieldType() == FieldType.PKFK) {
                        exp = exp.replace("@" + key, row.get(key + "Text").toString());
                    } else {
                        if (attr.getMyDataType() == DataType.AppString && attr.getUIContralType() == UIContralType.DDL && attr.getMyFieldType() == FieldType.Normal) {
                            exp = exp.replace("@" + key, row.get(key + "T").toString());
                        } else {
                            exp = exp.replace("@" + key, row.get(key).toString());
                        }
                    }
                }

                //不包含@则返回SQL语句
                if (exp.contains("@") == false) {
                    return exp;
                }
            }

        }

        if (exp.contains("@") && SystemConfig.isBSsystem() == true) {
            /*如果是bs*/
            for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
                if (DataType.IsNullOrEmpty(key)) {
                    continue;
                }
                exp = exp.replace("@" + key, ContextHolderUtils.getRequest().getParameter(key.toString()));
            }
        }

        exp = exp.replace("~", "'");
        return exp;
    }

    ///#endregion 表达式替换


    ///#region http请求

    /**
     * Http Get请求
     * <p>
     * param url
     *
     * @return
     */
    public static String HttpGet(String url) {
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder responseText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseText.append(line);
            }
            reader.close();
            connection.disconnect();
            return responseText.toString();
        } catch (IOException ex) {
            //url请求失败
            return ex.getMessage();
        }
    }

    public static String HttpPostConnect_Data(String dbSrNo, String exp, Hashtable ht, String requestMethod) throws Exception { //返回值
        //用户输入的webAPI地址
        String apiUrl = exp;
        if (apiUrl.contains("@WebApiHost")) //可以替换配置文件中配置的webapi地址
        {
            apiUrl = apiUrl.replace("@WebApiHost", bp.difference.SystemConfig.getAppSettings().get("WebApiHost").toString());
        }

        SFDBSrc mysrc = new SFDBSrc(dbSrNo);
        //拼接路径.
        apiUrl = mysrc.getConnString() + apiUrl;

        ///#region 解析路径变量 /{xxx}/{yyy} ? xxx=xxx
        if (apiUrl.contains("{") == true) {
            if (ht != null) {
                for (Object key : ht.keySet()) {
                    apiUrl = apiUrl.replace("{" + key + "}", ht.get(key).toString());
                }
            }
        }
        ///#endregion

        String[] strs = apiUrl.split("[?]", -1);
        //api接口地址
        String apiHost = strs[0];
        //api参数
        String apiParams = "";
        if (strs.length == 2) {
            apiParams = strs[1];
        }
        return bp.tools.PubGlo.HttpPostConnect(apiHost, apiParams, requestMethod);
    }

    /**
     * httppost方式发送数据
     * <p>
     * param url 要提交的url
     * param postDataStr
     * param timeOut 超时时间
     * param encode text code.
     *
     * @return 成功：返回读取内容；失败：0
     */
    public static String HttpPostConnect(String serverUrl, String postData, String requestMethod) throws Exception {
        return HttpPostConnect(serverUrl, postData, requestMethod, false);
    }

    public static String HttpPostConnect(String serverUrl, String postData) throws Exception {
        return HttpPostConnect(serverUrl, postData, "POST", false);
    }

    public static String HttpPostConnect(String serverUrl, String postData, String requestMethod, boolean isJsonModel) throws Exception {
        byte[] dataArray = postData.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        //创建请求
        URL requestUrl = new URL(serverUrl);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod(requestMethod);
        connection.setFixedLengthStreamingMode(dataArray.length);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        //设置上传服务的数据格式  设置之后不好使
        //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        //请求的身份验证信息为默认
        //request.Credentials = CredentialCache.DefaultCredentials;

        if (isJsonModel == true) {
            connection.setRequestProperty("Content-Type", "application/json");
        } else {
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        }

        //ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12;
        //请求超时时间
        connection.setConnectTimeout(10000);
        //创建输入流
        OutputStream dataStream;
        if (requestMethod.toLowerCase().equals("get") == false) {
            try {
                dataStream = connection.getOutputStream() ;
                //发送请求
                dataStream.write(dataArray, 0, dataArray.length);
                dataStream.close();
            } catch (RuntimeException e) {
                return "0"; //连接服务器失败
            }
        }

        InputStreamReader sr = new InputStreamReader(connection.getInputStream(), java.nio.charset.StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(sr);
        //读取返回消息
        int responseCode = connection.getResponseCode();
        StringBuilder responseText = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseText.append(line);
        }
        reader.close();
        connection.disconnect();
        return responseText.toString();
    }

    public static String HttpPostConnect(String serverUrl, Hashtable headerMap, String postData) throws Exception {
        byte[] dataArray = postData.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        //创建请求
        URL requestUrl = new URL(serverUrl);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setFixedLengthStreamingMode(dataArray.length);
        //设置上传服务的数据格式  设置之后不好使
        //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        //请求的身份验证信息为默认
        //request.Credentials = CredentialCache.DefaultCredentials;
        // request.ContentType = "application/x-www-form-urlencoded";
        connection.setRequestProperty("Content-Type", "application/json");
        //  request.ContentType = "application/x-www-form-urlencoded";

        //设置请求头
        if (!headerMap.isEmpty()) {
            for (Object key : headerMap.keySet()) {
                connection.setRequestProperty((String) key, headerMap.get(key).toString());
            }
        }

        //请求超时时间
        connection.setConnectTimeout(10000);
        //创建输入流
        OutputStream dataStream;
        try {
            dataStream = connection.getOutputStream();
        } catch (RuntimeException e) {
            return "0"; //连接服务器失败
        }
        //发送请求
        dataStream.write(dataArray, 0, dataArray.length);
        dataStream.close();

        InputStreamReader sr = new InputStreamReader(connection.getInputStream(), java.nio.charset.StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(sr);
        //读取返回消息
        int responseCode = connection.getResponseCode();
        StringBuilder responseText = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseText.append(line);
        }
        reader.close();
        connection.disconnect();
        return responseText.toString();
    }
}

///#endregion http请求
