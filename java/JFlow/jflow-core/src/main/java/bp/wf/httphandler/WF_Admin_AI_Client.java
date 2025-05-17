package bp.wf.httphandler;


import bp.da.*;
import bp.sys.MapAttr;
import bp.sys.MapAttrs;
import bp.tools.HttpClientUtil;
import bp.tools.OkHttpUtil;
import bp.tools.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 页面功能实体
 */
public class WF_Admin_AI_Client {
    //#region 公共变量.
    //BPM服务器地址.
    public static String BPMHost_Local = bp.difference.SystemConfig.getAppSettings().get("HostURLAI").toString();
    //public static string BPMHost = "http://shengchan.ccflow.org";
    public static String BPMHost = "http://101.43.32.218:8616";
    // + "/WF/API";
    //密钥.
    public static String PrivateKey = "DiGuaDiGua,IamCCBPM";

    //拼接访问地址
    static String webPath = "";
    //#endregion 公共变量.
    //C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
    ///#region AI接口.
    /**
     通用问答

     @param words
     @return
     */

    public static String GenerAsk(String words, String names, boolean debugMode)
    {
        return GenerAsk(words, names, debugMode, false);
    }

    public static String GenerAsk(String words, String names)
    {
        return GenerAsk(words, names, false, false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string GenerAsk(string words, string names, Boolean debugMode = false, Boolean localMode = false)
    public static String GenerAsk(String words, String names, boolean debugMode, boolean localMode)
    {
        //string subPath = "/generate/attrs";
        //webPath = BPMHost + subPath;
        //String postData = HttpClientUtil.doPost(webPath + "?token=xxx&Words=" + words + "&names=" + names + debugCmd, "");
        //return postData;
        return "此部分尚未完成.";
    }

    public static String CopilotCmd(String words, String names, boolean debugMode)
    {
        return CopilotCmd(words, names, debugMode, false);
    }

    public static String CopilotCmd(String words, String names)
    {
        return CopilotCmd(words, names, false, false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string CopilotCmd(string words, string names, Boolean debugMode = false, Boolean localMode = false)
    public static String CopilotCmd(String words, String names, boolean debugMode, boolean localMode)
    {
        String subPath = "/copilot/cmd";
        webPath = BPMHost + subPath;
        if (localMode && !StringUtils.isEmpty(BPMHost_Local))
        {
            webPath = BPMHost_Local + subPath;
        }
        String debugCmd = debugMode ? "&debug=1" : "";
        String postData = HttpClientUtil.doPost(webPath + "?token=xxx&Words=" + words + "&names=" + names + debugCmd, null,null,"");
        return postData;
    }
    /**
     根据模板生成代码

     @param words 模板
     @param ddl 表字段
     @return
     */

    public static String AIFrm_GenerCode(String words, String ddl, String codefomat, boolean debugMode) throws Exception {
        return AIFrm_GenerCode(words, ddl, codefomat, debugMode, false);
    }

    public static String AIFrm_GenerCode(String words, String ddl, String codefomat) throws Exception {
        return AIFrm_GenerCode(words, ddl, codefomat, false, false);
    }

    public static String AIFrm_GenerCode(String words, String ddl) throws Exception {
        return AIFrm_GenerCode(words, ddl, "javascript", false, false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string AIFrm_GenerCode(string words, string ddl, string codefomat= "javascript", Boolean debugMode = false, Boolean localMode = false)
    public static String AIFrm_GenerCode(String words, String ddl, String codefomat, boolean debugMode, boolean localMode) throws Exception {
        String subPath = "/generate/code";
        webPath = BPMHost + subPath;
        if (localMode && !StringUtils.isEmpty(BPMHost_Local))
        {
            webPath = BPMHost_Local + subPath;
        }

        String debugCmd = debugMode ? "&debug=1" : "";
        //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
        String postData = "Words=" + words + "&ddl=" + ddl;
        //String postData = "Words=word1&ddl=ddl1";
        String responseData = HttpClientUtil.doPost(webPath + "?token=xxx&code=" + codefomat + debugCmd, postData,null);
        return responseData;
    }


    public static String AIFrm_GenerCode_0(String words, String ddl, String codefomat, boolean debugMode) throws Exception {
        return AIFrm_GenerCode_0(words, ddl, codefomat, debugMode, false);
    }

    public static String AIFrm_GenerCode_0(String words, String ddl, String codefomat) throws Exception {
        return AIFrm_GenerCode_0(words, ddl, codefomat, false, false);
    }

    public static String AIFrm_GenerCode_0(String words, String ddl) throws Exception {
        return AIFrm_GenerCode_0(words, ddl, "javascript", false, false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string AIFrm_GenerCode_0(string words, string ddl, string codefomat = "javascript", Boolean debugMode = false, Boolean localMode = false)
    public static String AIFrm_GenerCode_0(String words, String ddl, String codefomat, boolean debugMode, boolean localMode) throws Exception {
        String subPath = "/generate/code";
        webPath = BPMHost + subPath;
        if (localMode && !StringUtils.isEmpty(BPMHost_Local))
        {
            webPath = BPMHost_Local + subPath;
        }

        //String postData = HttpClientUtil.doPost(webPath + "?token=xxx", json, "POST",true);
        String debugCmd = debugMode ? "&debug=1" : "";
        //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
        String postData = HttpClientUtil.doPost(webPath + "?token=xxx&Words=" + words + "&code=" + codefomat + "&ddl=" + ddl + debugCmd, "",null);
        return postData;
    }
    /**
     获得分析数据

     @param frmID 表单ID
     @param words 提示词
     @return
     */

    public static String AIFrm_GenerFX(String frmID, String words, boolean debugMode) throws Exception {
        return AIFrm_GenerFX(frmID, words, debugMode, false);
    }

    public static String AIFrm_GenerFX(String frmID, String words) throws Exception {
        return AIFrm_GenerFX(frmID, words, false, false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string AIFrm_GenerFX(string frmID, string words, Boolean debugMode = false, Boolean localMode = false)
    public static String AIFrm_GenerFX(String frmID, String words, boolean debugMode, boolean localMode) throws Exception {
        String subPath = "/find/rules";
        webPath = BPMHost + subPath;
        if (localMode && !StringUtils.isEmpty(BPMHost_Local))
        {
            webPath = BPMHost_Local + subPath;
        }

        //String postData = HttpClientUtil.doPost(webPath + "?token=xxx", json, "POST",true);
        String debugCmd = debugMode ? "&debug=1" : "";
        //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
        String postData = HttpClientUtil.doPost(webPath + "?token=xxx&Words=" + words + debugCmd, "",null);
        return postData;
    }
    /**
     通过AI获得字段.

     @param words 提示词
     @param names 已经有的字段名称
     @param debugMode 是否启动debug模式，debug
     debug=1，在AIHost端，不调用大模型，直接返回一个预置的json，用于调试json格式目的。
     debug=0，则调用大模型，返回值是实际的数据
     本地模式就是指在本地建立AIHost服务进行调用。
     本地模式后，仍需设置0实际访问模式或者Debug模式

     @return 返回结果dataSet模式的.
     */

    public static String AIFrm_GenerAttrs_ByWords(String frmID, String words, String names, boolean debugMode) throws Exception {
        return AIFrm_GenerAttrs_ByWords(frmID, words, names, debugMode, false);
    }

    public static String AIFrm_GenerAttrs_ByWords(String frmID, String words, String names) throws Exception {
        return AIFrm_GenerAttrs_ByWords(frmID, words, names, false, false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string AIFrm_GenerAttrs_ByWords(String frmID, string words, string names, Boolean debugMode = false, Boolean localMode = false)
    public static String AIFrm_GenerAttrs_ByWords(String frmID, String words, String names, boolean debugMode, boolean localMode) throws Exception {
        String subPath = "/generate/attrs";
        webPath = BPMHost + subPath;
        if (localMode && !StringUtils.isEmpty(BPMHost_Local))
        {
            webPath = BPMHost_Local + subPath;
        }

        //String postData = HttpClientUtil.doPost(webPath + "?token=xxx", json, "POST",true);
        String debugCmd = debugMode ? "&debug=1" : "";
        //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
        String postData = HttpClientUtil.doPost(webPath + "?token=xxx&Words=" + words + "&names=" + names + debugCmd, "",null);
        return postData;
    }

    /**
     通过AI获得从表

     @param words 提示词
     @param names 已经有的从表名称
     @return
     */

    public static String AIFrm_GenerDetails_ByWord(String words, String names, boolean debugMode) throws Exception {
        return AIFrm_GenerDetails_ByWord(words, names, debugMode, false);
    }

    public static String AIFrm_GenerDetails_ByWord(String words, String names) throws Exception {
        return AIFrm_GenerDetails_ByWord(words, names, false, false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string AIFrm_GenerDetails_ByWord(string words, string names, Boolean debugMode = false, Boolean localMode = false)
    public static String AIFrm_GenerDetails_ByWord(String words, String names, boolean debugMode, boolean localMode) throws Exception {
        String subPath = "/generate/details";
        webPath = BPMHost + subPath;
        if (localMode && !StringUtils.isEmpty(BPMHost_Local))
        {
            webPath = BPMHost_Local + subPath;
        }

        //String postData = HttpClientUtil.doPost(webPath + "?token=xxx", json, "POST",true);
        String debugCmd = debugMode ? "&debug=1" : "";
        //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
        String postData = HttpClientUtil.doPost(webPath + "?token=xxx&Words=" + words + "&names=" + names + debugCmd, "",null);
        return postData;
    }
    /**
     通过AI获得节点

     @param words 提示词
     @param existnodes 已经加入的节点名集合
     @param debugMode 是否启动debug模式，debug
     debug=1，在AIHost端，不调用大模型，直接返回一个预置的json，用于调试json格式目的。
     debug=0，则调用大模型，返回值是实际的数据
     本地模式就是指在本地建立AIHost服务进行调用。
     本地模式也有实际访问大模型，以及Debug模式

     @return 退回节点信息，json格式
     */

    public static DataTable AIFlow_GenerFlow_ByWords(String words, String existnodes, boolean debugMode) throws Exception {
        return AIFlow_GenerFlow_ByWords(words, existnodes, debugMode, false);
    }

    public static DataTable AIFlow_GenerFlow_ByWords(String words, String existnodes) throws Exception {
        return AIFlow_GenerFlow_ByWords(words, existnodes, false, false);
    }

    public static DataTable AIFlow_GenerFlow_ByWords(String words) throws Exception {
        return AIFlow_GenerFlow_ByWords(words, "", false, false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static DataTable AIFlow_GenerFlow_ByWords(string words, string existnodes = "", Boolean debugMode = false, Boolean localMode = false)
    public static DataTable AIFlow_GenerFlow_ByWords(String words, String existnodes, boolean debugMode, boolean localMode) throws Exception {
        String subPath = "/generate/nodes";
        webPath = BPMHost + subPath;
        if (localMode && !StringUtils.isEmpty(BPMHost_Local))
        {
            webPath = BPMHost_Local + subPath;
        }

        words = words.trim();
        //String postData = HttpClientUtil.doPost(webPath + "?token=xxx", json, "POST",true);
        String debugCmd = debugMode ? "&debug=1" : "";
        //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
        String postData = HttpClientUtil.doPost(webPath + "?token=xxx&Words=" + words + "&existnodes=" + existnodes + debugCmd, "",null);

        DataTable dt = new DataTable();
        if (StringUtils.isEmpty(postData) || Objects.equals(postData, "[]"))
        {
            dt.Columns.Add(new DataColumn("nodeNo"));
            dt.Columns.Add(new DataColumn("nodeName"));
            dt.Columns.Add(new DataColumn("nodeRole"));
            dt.Columns.Add(new DataColumn("nodeDesc"));
            dt.Columns.Add(new DataColumn("flowName"));
        }
        else
        {
            dt = bp.tools.Json.ToDataTable(postData);
        }

        //   dt.TableName = "我的流程";
        dt.Columns.get("nodeNo").ColumnName = "No";
        dt.Columns.get("nodeName").ColumnName = "Name";
        dt.Columns.get("nodeRole").ColumnName = "Stations";
        dt.Columns.get("nodeDesc").ColumnName = "Desc";
        dt.Columns.get("flowName").ColumnName = "AI生成的流程";
        return dt;
    }

    public static String App_ByWords(String words, boolean debugModel) throws Exception {
        return App_ByWords(words, debugModel, false);
    }

    public static String App_ByWords(String words) throws Exception {
        return App_ByWords(words, false, false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string App_ByWords(string words, Boolean debugModel = false, Boolean localMode = false)
    public static String App_ByWords(String words, boolean debugModel, boolean localMode) throws Exception {
        return AIFlow_GenerApp_ByWords(words, "", debugModel, localMode);
    }
    /**
     通过AI获得应用
     模块，菜单，操作

     @param words 提示词
     @param existmenus 已经加入的menu集合
     @param debugMode 是否启动debug模式，debug
     debug=1，在AIHost端，不调用大模型，直接返回一个预置的json，用于调试json格式目的。
     debug=0，则调用大模型，返回值是实际的数据
     本地模式就是指在本地建立AIHost服务进行调用。
     本地模式也有实际访问大模型，以及Debug模式

     @return 退回节点信息，json格式
     */

    public static String AIFlow_GenerApp_ByWords(String words, String existmenus, boolean debugMode) throws Exception {
        return AIFlow_GenerApp_ByWords(words, existmenus, debugMode, false);
    }

    public static String AIFlow_GenerApp_ByWords(String words, String existmenus) throws Exception {
        return AIFlow_GenerApp_ByWords(words, existmenus, false, false);
    }

    public static String AIFlow_GenerApp_ByWords(String words) throws Exception {
        return AIFlow_GenerApp_ByWords(words, "", false, false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string AIFlow_GenerApp_ByWords(string words, string existmenus = "", Boolean debugMode = false, Boolean localMode = false)
    public static String AIFlow_GenerApp_ByWords(String words, String existmenus, boolean debugMode, boolean localMode) throws Exception {
        String subPath = "/generate/menus";
        webPath = BPMHost + subPath;
        if (localMode && !StringUtils.isEmpty(BPMHost_Local))
        {
            webPath = BPMHost_Local + subPath;
        }

        words = words.trim();
        //String postData = HttpClientUtil.doPost(webPath + "?token=xxx", json, "POST",true);
        String debugCmd = debugMode ? "&debug=1" : "";
        //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
        String postData = HttpClientUtil.doPost(webPath + "?token=xxx&Words=" + words + "&existnodes=" + existmenus + debugCmd, "",null);

        //DataTable dt = new DataTable();
        DataSet ds = new DataSet();
        if (StringUtils.isEmpty(postData) || Objects.equals(postData, "[]"))
        {
        }
        else
        {
            //ds = BP.Tools.Json.ToDataSet(postData);
        }

        return postData;
    }
    public static String Text_App_ByWords(String words) throws Exception {
        return Text_App_ByWords(words, false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string Text_App_ByWords(string words, Boolean Debug = false)
    public static String Text_App_ByWords(String words, boolean Debug) throws Exception {
        DataSet ds = new DataSet();

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
        ///#region 0. 基础信息. - 系统名称.
        DataTable dt = new DataTable();
        dt.TableName = "BaseInfo";
        dt.Columns.Add("SystemNo");
        dt.Columns.Add("SystemName");

        DataRow dr = dt.NewRow();
        dr.setValue(0, "car");
        dr.setValue(1, "车辆管理");
        dt.Rows.add(dr);
        ds.Tables.add(dt);
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
        ///#endregion 0. 基础信息.

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
        ///#region 1.生成模块.
        dt = new DataTable();
        dt.TableName = "Moduel";
        dt.Columns.Add("No");
        dt.Columns.Add("Name");

        dr = dt.NewRow();
        dr.setValue(0,"001");//模块编号
        dr.setValue(1, "车辆台账管理");
        dt.Rows.add(dr);

        dr = dt.NewRow();
        dr.setValue(0, "002"); //模块编号
        dr.setValue(1, "加油信息管理");
        dt.Rows.add(dr);

        dr = dt.NewRow();
        dr.setValue(0, "003"); //模块编号
        dr.setValue(1, "报告与统计");
        dt.Rows.add(dr);
        ds.Tables.add(dt); //增加一个dataset.
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
        ///#endregion 1. 生成模块

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
        ///#region 2.生成菜单.
        dt = new DataTable();
        dt.TableName = "Menu";
        dt.Columns.Add("No");
        dt.Columns.Add("Name");
        dt.Columns.Add("MenuType");
        dt.Columns.Add("ModuelNo"); //模块的编号.

        dr = dt.NewRow();
        dr.set("No", "001");
        dr.set("Name", "车辆台账");
        dr.set("MenuType", "Dict");
        dr.set("ModuelNo", "001"); //模块编号.
        dt.Rows.add(dr);

        dr = dt.NewRow();
        dr.set("No", "002");
        dr.set("Name", "加油记录列表");
        dr.set("MenuType", "Bill");
        dr.set("ModuelNo", "002"); //模块编号.
        dt.Rows.add(dr);

        dr = dt.NewRow();
        dr.set("No", "005");
        dr.set("Name", "车辆使用报告");
        dr.set("MenuType", "Rpt");
        dr.set("ModuelNo", "003"); //模块编号.

        dr = dt.NewRow();
        dr.set("No", "005");
        dr.set("Name", "加油费用统计");
        dr.set("MenuType", "Rpt");
        dr.set("ModuelNo", "003"); //模块编号.
        dt.Rows.add(dr);
        ds.Tables.add(dt);
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
        ///#endregion 2.生成菜单.

        String str = bp.tools.Json.ToJson(ds);
        return str;
    }
    public static String Test_AIFlow_GenerFlow_ByWords(String words, String existnodes)
    {
        return Test_AIFlow_GenerFlow_ByWords(words, existnodes, false);
    }

    public static String Test_AIFlow_GenerFlow_ByWords(String words)
    {
        return Test_AIFlow_GenerFlow_ByWords(words, "", false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string Test_AIFlow_GenerFlow_ByWords(string words, string existnodes = "", Boolean Debug = false)
    public static String Test_AIFlow_GenerFlow_ByWords(String words, String existnodes, boolean Debug)
    {
        DataSet ds = new DataSet();

        DataTable dt = new DataTable();
        dt.Columns.Add("No");
        dt.Columns.Add("Name");
        dt.Columns.Add("X");
        dt.Columns.Add("Y");

        DataRow dr = dt.NewRow();
        dr.setValue(0, "001");
        dr.setValue(1, "填写申请");
        dr.setValue(2, "100");
        dr.setValue(3, "200");
        dt.Rows.add(dr);

        dr = dt.NewRow();
        dr.setValue(0, "002");
        dr.setValue(1, "部门审批");
        dr.setValue(2, "100");
        dr.setValue(3, "200");
        dt.Rows.add(dr);

        dr = dt.NewRow();
        dr.setValue(0, "003");
        dr.setValue(1, "人力资源审批");
        dr.setValue(2, "100");
        dr.setValue(3, "200");
        dt.Rows.add(dr);

        dr = dt.NewRow();
        dr.setValue(0, "004");
        dr.setValue(1, "反馈给申请人");
        dr.setValue(2, "100");
        dr.setValue(3, "200");
        dt.Rows.add(dr);

        ds.Tables.add(dt); //增加一个dataset.
        return bp.tools.Json.ToJson(dt);
    }
    public static String AIFlow_GenerNodesAndFrm_ByFile(String words)
    {
        return null;
    }
    /**
     通过AI获得App应用

     @param words
     @param names
     @return
     */
    public static String AIApp_GenerApp(String words, String names) throws Exception {
        webPath = BPMHost + "/generate/table";
        String postData = HttpClientUtil.doPost(webPath + "?token=XXX&Words=" + words + "&names=" + names, "",null);
        return postData;
    }

    /**
     通过AI获得图标关联.

     @param words 提示词
     @param debugMode 是否启动debug模式，debug
     debug=1，在AIHost端，不调用大模型，直接返回一个预置的json，用于调试json格式目的。
     debug=0，则调用大模型，返回值是实际的数据
     本地模式就是指在本地建立AIHost服务进行调用。
     本地模式后，仍需设置0实际访问模式或者Debug模式

     @return 返回结果dataSet模式的.
     */

    public static String AIFrm_MatchIcons_ByWords(String words, boolean debugMode) throws Exception {
        return AIFrm_MatchIcons_ByWords(words, debugMode, false);
    }

    public static String AIFrm_MatchIcons_ByWords(String words) throws Exception {
        return AIFrm_MatchIcons_ByWords(words, false, false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string AIFrm_MatchIcons_ByWords(string words, Boolean debugMode = false, Boolean localMode = false)
    public static String AIFrm_MatchIcons_ByWords(String words, boolean debugMode, boolean localMode) throws Exception {
        String subPath = "/match/icons";
        webPath = BPMHost + subPath;
        if (localMode && !StringUtils.isEmpty(BPMHost_Local))
        {
            webPath = BPMHost_Local + subPath;
        }

        //String postData = HttpClientUtil.doPost(webPath + "?token=xxx", json, "POST",true);
        String debugCmd = debugMode ? "&debug=1" : "";
        //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
        String postData = HttpClientUtil.doPost(webPath + "?token=xxx&Words=" + words + "&names=" + debugCmd, "",null);
        return postData;
    }

    /**
     生成测试数据

     @param words 提示词
     @return
     */

    public static String AiFrm_GenerTestDB_ByWords(String words, boolean debugMode) throws Exception {
        return AiFrm_GenerTestDB_ByWords(words, debugMode, false);
    }

    public static String AiFrm_GenerTestDB_ByWords(String words) throws Exception {
        return AiFrm_GenerTestDB_ByWords(words, false, false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string AiFrm_GenerTestDB_ByWords(string words, Boolean debugMode = false, Boolean localMode = false)
    public static String AiFrm_GenerTestDB_ByWords(String words, boolean debugMode, boolean localMode) throws Exception {
        String subPath = "/generate/testdata";
        webPath = BPMHost + subPath;
        if (localMode && !StringUtils.isEmpty(BPMHost_Local))
        {
            webPath = BPMHost_Local + subPath;
        }

        //String postData = HttpClientUtil.doPost(webPath + "?token=xxx", json, "POST",true);
        String debugCmd = debugMode ? "&debug=1" : "";
        //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
        String postData = HttpClientUtil.doPost(webPath + "?token=xxx&Words=" + words + debugCmd, "",null);
        return postData;
    }


    /**
     生成大屏分析

     @param words 提示词
     @return
     */

    public static String AiFrm_GenerWindows_ByWords(String words, boolean debugMode) throws Exception {
        return AiFrm_GenerWindows_ByWords(words, debugMode, false);
    }

    public static String AiFrm_GenerWindows_ByWords(String words) throws Exception {
        return AiFrm_GenerWindows_ByWords(words, false, false);
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string AiFrm_GenerWindows_ByWords(string words, Boolean debugMode = false, Boolean localMode = false)
    public static String AiFrm_GenerWindows_ByWords(String words, boolean debugMode, boolean localMode) throws Exception {
        String subPath = "/generate/chartsql";
        webPath = BPMHost + subPath;
        if (localMode && !StringUtils.isEmpty(BPMHost_Local))
        {
            webPath = BPMHost_Local + subPath;
        }

        //String postData = HttpPostConnect(webPath + "?token=xxx", json, "POST",true);
        String debugCmd = debugMode ? "&debug=1" : "";
        //如果debug=1，则AIHost不调用大模型，直接返回一个预置的json。用于调试json格式目的，如果调试业务请debug=0
        String postData = HttpClientUtil.doPost(webPath + "?token=xxx&Words=" + words + debugCmd, "",null);
        return postData;
    }

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
    ///#endregion AI接口.


  /*  public static String HttpPostConnect(String serverUrl, String postData, String requestMethod)
    {
        return HttpPostConnect(serverUrl, postData, requestMethod, false);
    }

    public static String HttpPostConnect(String serverUrl, String postData)
    {
        return HttpPostConnect(serverUrl, postData, "POST", false);
    }
*/
    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string HttpPostConnect(string serverUrl, string postData, string requestMethod = "POST", bool isJsonModel = false)
   /* public static String HttpPostConnect(String serverUrl, String postData, String requestMethod, boolean isJsonModel)
    {

        var dataArray = postData.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        //创建请求
        var request = (HttpWebRequest)HttpWebRequest.Create(serverUrl);
        request.Method = requestMethod;
        request.ContentLength = dataArray.Length;
        //设置上传服务的数据格式  设置之后不好使
        //request.ContentType = "application/x-www-form-urlencoded";
        //请求的身份验证信息为默认
        request.Credentials = CredentialCache.DefaultCredentials;

        if (isJsonModel == true)
        {
            request.ContentType = "application/json";
        }
        else
        {
            request.ContentType = "application/x-www-form-urlencoded";
        }

        ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12;
        //请求超时时间
        request.Timeout = 1000 * 120;
        //创建输入流
        OutputStream dataStream;
        if (requestMethod.toLowerCase().equals("get") == false)
        {
            try
            {
                dataStream = request.GetRequestStream();
                //发送请求
                dataStream.write(dataArray, 0, dataArray.Length);
                dataStream.close();
            }
            catch (RuntimeException e)
            {
                return "0"; //连接服务器失败
            }
        }

        HttpWebResponse res;
        try
        {
            res = (HttpWebResponse)request.GetResponse();
        }
        catch (WebException ex)
        {
            res = (HttpWebResponse)ex.Response;
        }
        InputStreamReader sr = new InputStreamReader(res.GetResponseStream(), java.nio.charset.StandardCharsets.UTF_8);
        //读取返回消息
        String data = sr.ReadToEnd();
        sr.close();
        return data;
    }*/

}
