package bp.App.Handler;

import bp.App.Dto.MenuCh;
import bp.App.Dto.MenuInfo;
import bp.App.Dto.RyMenuDto;
import bp.App.ShuCai.BasicInfo;
import bp.App.ShuCai.BasicInfoAttr;
import bp.ccfast.ccmenu.*;
import bp.da.*;
import bp.difference.ContextHolderUtils;
import bp.difference.SystemConfig;
import bp.difference.handler.CommonFileUtils;
import bp.en.*;
import bp.port.Emp;
import bp.port.EmpAttr;
import bp.sys.*;
import bp.tools.HttpClientUtil;
import bp.tools.Json;
import bp.web.WebUser;
import bp.wf.Dev2Interface;
import bp.wf.GERpt;
import bp.wf.GERptAttr;
import bp.wf.GenerWorkFlow;
import bp.wf.httphandler.HttpHandlerGlo;
import bp.wf.httphandler.WF_Comm;
import bp.wf.xml.AdminMenus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.scene.media.EqualizerBand;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.print.DocFlavor;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map;

public class AppHandler extends bp.difference.handler.DirectoryPageBase{
    //用户数据获取url
    //"https://172.19.144.11:7081/stage-api/system/user/getInfo"  测试
    //"https://59.212.37.116/gateway/system/user/getInfo"  正式
    private static String kg_user_url="https://172.19.144.11:7081/stage-api/system/user/getInfo";

    //用户菜单获取url
    //"https://172.19.144.11:7081/stage-api/system/menu/getRouters"  测试
    //"https://59.212.37.116/gateway/system/menu/getRoute"  正式
    private static String kg_menu_url="https://172.19.144.11:7081/stage-api/system/menu/getRouters";

    //是否测试环境
    private static boolean isTest=true;

    static {
        if(!isTest){
            //用户数据获取url
            kg_user_url="https://59.212.37.116/gateway/system/user/getInfo";

            //用户菜单获取url
            kg_menu_url="https://59.212.37.116/gateway/system/menu/getRoute";
        }
    }
    /**
     * 构造函数
     */
    public AppHandler() {
    }
    /**
     比较两个字符串是否有交集

     @param ids1
     @param ids2
     @return
     */
    public final boolean IsHaveIt(String ids1, String ids2)
    {
        if (DataType.IsNullOrEmpty(ids1) == true)
        {
            return false;
        }
        if (DataType.IsNullOrEmpty(ids2) == true)
        {
            return false;
        }

        String[] str1s = ids1.split("[,]", -1);
        String[] str2s = ids2.split("[,]", -1);

        for (String str1 : str1s)
        {
            if (Objects.equals(str1, "") || str1 == null)
            {
                continue;
            }

            for (String str2 : str2s)
            {
                if (Objects.equals(str2, "") || str2 == null)
                {
                    continue;
                }

                if (str2.equals(str1) == true)
                {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 关键信息加密
     * @return
     */
    public String encryptVal() throws Exception{
        Hashtable map=new Hashtable();
        String val=this.GetRequestVal("encryptVal");
        map.put("code","200");
        map.put("msg","加密成功");
        map.put("data","b6R1NYWN7SEadU4e0c+rxA==");
        return bp.tools.Json.ToJson(map,false);
//        try{
//            String url="http://59.212.146.96:85/encrypt/encryptVal";
//            bp.da.Log.DefaultLogWriteLine(LogType.Info, "加密请求信息："+ url+";ry-token:"+url);
//            Map<String,String> bodyMap=new HashMap<>();
//            bodyMap.put("val",val);
//            String data= HttpClientUtil.doGetSSL(url,null,bodyMap);
//            bp.da.Log.DefaultLogWriteLine(LogType.Info, "加密返回信息："+ data);
//            JSONObject dataJson= JSON.parseObject(data);
//            if(dataJson.getString("code").equals("200")) {
//                map.put("code",dataJson.getString("code"));
//                map.put("msg",dataJson.getString("msg"));
//                map.put("data",dataJson.getString("data"));
//            }
//            else {
//                map.put("code",dataJson.getString("code"));
//                map.put("msg","加密失败");
//                map.put("data",val);
//            }
//            return bp.tools.Json.ToJson(map,false);
//        }
//        catch (Exception ex){
//            bp.da.Log.DefaultLogWriteLine(LogType.Info, "加密失败："+ ex.getMessage());
//            return "@err加密失败";
//        }
    }
    /**
     * 关键信息解密
     * @return
     */
    public String decryptVal() throws Exception{
        Hashtable map=new Hashtable();
        String val=this.GetRequestVal("encryptVal");
        map.put("code","200");
        map.put("msg","解密成功");
        map.put("data","17789861131");
        return bp.tools.Json.ToJson(map,false);
//        try{
//            String url="http://59.212.146.96:85/encrypt/decryptVal";
//            bp.da.Log.DefaultLogWriteLine(LogType.Info, "解密请求信息："+ url+";ry-token:"+url);
//            Map<String,String> bodyMap=new HashMap<>();
//            bodyMap.put("val",val);
//            String data= HttpClientUtil.doGetSSL(url,null,bodyMap);
//            bp.da.Log.DefaultLogWriteLine(LogType.Info, "解密返回信息："+ data);
//            JSONObject dataJson= JSON.parseObject(data);
//            if(dataJson.getString("code").equals("200")) {
//                map.put("code",dataJson.getString("code"));
//                map.put("msg",dataJson.getString("msg"));
//                map.put("data",dataJson.getString("data"));
//            }
//            else {
//                map.put("code",dataJson.getString("code"));
//                map.put("msg","解密失败");
//                map.put("data",val);
//            }
//            return bp.tools.Json.ToJson(map,false);
//        }
//        catch (Exception ex){
//            bp.da.Log.DefaultLogWriteLine(LogType.Info, "解密失败："+ ex.getMessage());
//            return "@err加密失败";
//        }
    }
    /**
     * 获取所在市县的行政代码信息
     * @return
     */
    public String getCityCodeByUserName(){
        try{
            Paras ps=new Paras();
            Hashtable map=new Hashtable();
            String str= SystemConfig.getAppCenterDBVarStr();
            ps.SQL="select a.No,a.Name as shortname,b.city_name as name,b.City_Jc,a.City_Code as \"City_Code\",a.County_Code,a.Street_Code,"
                +"a.Village_Code,a.City_Level as \"City_Level\" from GK_DeptCity a left join sys_city b on a.City_Code=b.city_code where a.No="+str+"No";
            ps.Add("No", WebUser.getDeptNo());
            DataTable dt=bp.da.DBAccess.RunSQLReturnTable(ps);
            if(dt.Rows.size()>0){
                if(Integer.parseInt(dt.Rows.get(0).get("City_Level").toString())>0){
                    map.put("No",dt.Rows.get(0).get("City_Code").toString());
                    map.put("Name",dt.Rows.get(0).get("name").toString());
                    map.put("Level",dt.Rows.get(0).get("City_Level").toString());
                    map.put("Jc",dt.Rows.get(0).get("city_jc").toString());
                }
                else{
                    map.put("No",dt.Rows.get(0).get("no").toString());
                    map.put("Name",dt.Rows.get(0).get("shortname").toString());
                    map.put("Level",dt.Rows.get(0).get("City_Level").toString());
                    map.put("Jc",dt.Rows.get(0).get("city_jc").toString());
                }
            }
            return bp.tools.Json.ToJson(map,false);
        }
        catch (Exception ex){
            bp.da.Log.DefaultLogWriteLine(LogType.Error, "获取所在市县错误："+ ex.getMessage());
            return "";
        }
    }

    /***
     * 蔬菜基地核查工作进展统计表
     * @return
     */
    public  String getHCSituation(){

//        String hcqs=this.GetRequestVal("hcqs");
//        if(DataType.IsNullOrEmpty(hcqs)){
//            return "err@参数核查期数不能为空";
//        }
        String sql="select row_number() over(ORDER BY city.city_code) as index, COALESCE(A.jdgs,0) as jdgs,COALESCE(round(C.jdmj, 2),0.00) as jdmj ,COALESCE(D.yhctbgs,0) as yhctbgs,COALESCE(round(F.yhctbmj, 2),0.00) as yhctbmj,\n" +
                "CASE WHEN COALESCE(D.yhctbgs,0)> 0 THEN FLOOR(COALESCE(D.yhctbgs,0)* 10000 /COALESCE(A.jdgs,0))/ 100 ELSE 0 END AS yhcbl,\n" +
                "RANK() OVER (ORDER BY " +
                "CASE " +
                "WHEN COALESCE(D.yhctbgs,0)> 0 THEN " +
                " FLOOR( " +
                "\t\tCOALESCE(D.yhctbgs,0)* 10000 /COALESCE(A.jdgs,0))/ 100 ELSE 0\n" +
                "\tEND DESC \n" +
                "\t) AS yhcpm, /*,TO_DATE(TO_CHAR(stat_time,'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd hh24:mi:ss')*/\n" +
                "\tG.yshtbgs,\n" +
                "\t--核查图斑个数\n" +
                "\tI.hctbgs,\n" +
                "\t--已审核比例\n" +
                "CASE\n" +
                "\t\tWHEN COALESCE(G.yshtbgs,0)> 0 THEN\n" +
                "\t\tFLOOR(\n" +
                "\t\tCOALESCE(G.yshtbgs,0)* 10000 /COALESCE(I.hctbgs,0))/ 100\n" +
                "\t\t ELSE 0 \n" +
                "\tEND AS yshbl,\n" +
                "\t--已审核排名\n" +
                "\tRANK() OVER (\n" +
                "\t\t\n" +
                "\tORDER BY\n" +
                "CASE\n" +
                "\t\t\n" +
                "\t\tWHEN COALESCE(G.yshtbgs,0)> 0 THEN\n" +
                "\t\tFLOOR(\n" +
                "\t\tCOALESCE(G.yshtbgs,0)* 10000 /COALESCE(I.hctbgs,0))/ 100\n" +
                "\t\t ELSE 0 \n" +
                "\tEND DESC \n" +
                "\t) AS yshpm,\n" +
                "\t\t\t\n" +
                "city.city_code, city.city_name  as cityname from  sys_city city  \n" +
                "  --基地个数\n" +
                "\tLEFT JOIN ( SELECT shuoshusx, COUNT( jdbh ) AS jdgs FROM ( SELECT DISTINCT shuoshusx, jdbh FROM gis_basicinfo where hcqs='SC23' ) SCJD_JBXX GROUP BY shuoshusx ) A ON A.shuoshusx = city.city_code\n" +
                "\t--基地面积\n" +
                "\t LEFT JOIN ( SELECT shuoshusx,sum(COALESCE ( jdmj, 0 )) AS jdmj  FROM gis_basicinfo where hcqs='SC23'   GROUP BY shuoshusx ) C ON C.shuoshusx = city.city_code\n" +
                "\t \n" +
                "\t --已核查图斑数\n" +
                "\t  LEFT JOIN ( SELECT shuoshusx, COUNT( jdbh ) AS yhctbgs FROM ( SELECT DISTINCT shuoshusx, jdbh FROM gis_basicinfo where hcqs='SC23' and ishc=1 ) SCJD_JBXX GROUP BY shuoshusx  ) D ON D.shuoshusx = city.city_code\n" +
                "\t\t\n" +
                "\t\t--已核查图斑面积\n" +
                "\t\tLEFT JOIN ( \tSELECT shuoshusx,sum(COALESCE ( jdmj, 0 )) AS yhctbmj  FROM gis_basicinfo where hcqs='SC23' and ishc=1   GROUP BY shuoshusx ) F ON F.shuoshusx = city.city_code\n" +
                "\t\t\n" +
                "\t\t---已审核图斑个数\n" +
                "\t  LEFT JOIN ( SELECT shuoshusx, COUNT( jdbh ) AS yshtbgs FROM ( SELECT shuoshusx, jdbh FROM nd10rpt where sql_hechaqishu='SC23' and dksta=2 ) SCJD_HCXX GROUP BY shuoshusx  ) G ON G.shuoshusx = city.city_code\n" +
                "\t\t\n" +
                "\t\t--已审核图斑面积\n" +
                "\tLEFT JOIN ( \tSELECT shuoshusx,sum(COALESCE ( jdtbmj, 0 )) AS yshjdmj  FROM nd10rpt where hcqs='SC23' and dksta=2  GROUP BY shuoshusx ) H ON H.shuoshusx = city.city_code\n" +
                "\t\t--摸排核查信息核查图斑个数\n" +
                "\t\t\n" +
                "LEFT JOIN ( SELECT shuoshusx, COUNT( jdbh ) AS hctbgs FROM ( SELECT  shuoshusx, jdbh FROM nd10rpt where   sql_hechaqishu='SC23'  ) SCJD_HCXX GROUP BY shuoshusx  )\n" +
                " I ON I.shuoshusx = city.city_code\n" +
                "\t\tWHERE city.city_level='1'\n";
        DataTable dt=DBAccess.RunSQLReturnTable(sql);
        return bp.tools.Json.ToJson(dt);
    }

    /**
     * 获取系统信息
     * @return
     */
    public final String getSystemName(){
        try{
            String systemNo=this.GetRequestVal("appNo");
            MySystem system=new MySystem(systemNo);
            Hashtable map=new Hashtable();
            map.put("No",system.getNo());
            map.put("Name",system.getName());
            return bp.tools.Json.ToJson(map,false);
        }
        catch (Exception ex){
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "获取系统名称错误："+ ex.getMessage());
            return "";
        }
    }

    /**
     * 获取材料树，可根据表单配置，动态获取当前节点可以显示的附件字段
     * @return
     */
    public final String getAthTree() {
        try {

            String sql = "";
            String OID = this.GetRequestVal("OID");
            //先获取流程数据
            GenerWorkFlow gwf = new GenerWorkFlow(Long.parseLong(OID));
            if (gwf.getFlowNo().equals("002")) {
                sql = "select '001' as \"No\",'汇交材料' as \"Name\",'0' as \"ParentNo\",'ment' as \"AthType\" union "
                        + "select mypk as \"No\",filename as \"Name\",fk_frmattachment as \"ParentNo\",'mentdb' as \"AthType\" from sys_frmattachmentdb  where refpkval='" + OID + "'"
                        + " union select mypk as \"No\", name as \"Name\",'001' as \"ParentNo\",'ment' as \"AthType\" from sys_frmattachment where fk_mapdata in"
                        + "(select fk_mapdata from sys_frmattachmentdb  where refpkval='" + OID + "')";
            } else {
                //获取设置的表单字段联动控制数据
                FrmRBs frmRBs = new FrmRBs();
                frmRBs.Retrieve(FrmRBAttr.FrmID, "ND" + gwf.getNodeID());
                //获取rpt数据
                GERpt rpt = new GERpt("ND" + Integer.parseInt(gwf.getFlowNo()) + "Rpt");
                rpt.Retrieve(GERptAttr.OID, Long.parseLong(OID));

                //循环判断值，以及需要隐藏的附件字段
                String athHiddens = "";
                if (frmRBs.size() > 0) {
                    for (FrmRB rb : frmRBs.ToJavaList()) {
                        //值相等，再获取
                        if (!rpt.GetValByKey(rb.getKeyOfEn()).toString().equals(rb.getIntKey()))
                            continue;
                        String fields = rb.getFieldsCfg();
                        //关联字段如果不为空，继续
                        if (DataType.IsNullOrEmpty(fields))
                            continue;

                        //取出附件字段
                        String[] fieldsCfgs = fields.split("@");
                        for (int i = 0; i < fieldsCfgs.length; i++) {
                            if (DataType.IsNullOrEmpty(fieldsCfgs[i]))
                                continue;
                            String[] fd = fieldsCfgs[i].split("=");
                            if (fd[1].equals("4"))
                                athHiddens += "'" + fd[0] + "',";
                        }
                        //AtPara atPara=new AtPara(fields);
                        //atPara.GetValStrByKey()
                    }
                }
                if (!DataType.IsNullOrEmpty(athHiddens))
                    athHiddens = athHiddens.substring(0, athHiddens.length() - 1);

                sql = "select '001' as \"No\",'汇交材料' as \"Name\",'0' as \"ParentNo\",'ment' as \"AthType\" union "
                        + "select mypk as \"No\",filename as \"Name\",fk_frmattachment as \"ParentNo\",'mentdb' as \"AthType\" from sys_frmattachmentdb  where refpkval='" + OID + "' and noofobj not in(" + athHiddens + ")"
                        + " union select mypk as \"No\", name as \"Name\",'001' as \"ParentNo\",'ment' as \"AthType\" from sys_frmattachment where fk_mapdata in"
                        + "(select fk_mapdata from sys_frmattachmentdb  where refpkval='" + OID + "') and noofobj not in(" + athHiddens + ")";
            }

            DataTable dt = bp.da.DBAccess.RunSQLReturnTable(sql);
            return bp.tools.Json.ToJson(dt);
        } catch (Exception ex) {
            bp.da.Log.DefaultLogWriteLine(LogType.Error, "获取菜单失败：" + ex.getMessage());
            return "err@" + ex.getMessage();
        }
    }

    /**
     * 获取材料树，可根据表单配置，动态获取当前节点可以显示的附件字段FORz专题流程
     * @return
     */
    public final String getAthTreeForZT(){
        try {
            String OID = this.GetRequestVal("OID");

            String sql = "select '001' as \"No\",'汇交材料' as \"Name\",'0' as \"ParentNo\",'ment' as \"AthType\" union "
                    + "select mypk as \"No\",filename as \"Name\",fk_frmattachment as \"ParentNo\",'mentdb' as \"AthType\" from sys_frmattachmentdb  where refpkval='" + OID + "'"
                    + " union select mypk as \"No\", name as \"Name\",'001' as \"ParentNo\",'ment' as \"AthType\" from sys_frmattachment where fk_mapdata in"
                    + "(select fk_mapdata from sys_frmattachmentdb  where refpkval='" + OID + "')";
            DataTable dt = bp.da.DBAccess.RunSQLReturnTable(sql);
            return bp.tools.Json.ToJson(dt);
        }
        catch (Exception ex){
            bp.da.Log.DefaultLogWriteLine(LogType.Error, "获取菜单失败：" + ex.getMessage());
            return "err@"+ex.getMessage();
        }
    }
    /**
     * 获取当前登录系统的人员
     * @return
     */
    public String autoLoginWithRy(){
        //根据token获取用户登录信息
        String ry_token=this.GetRequestVal("ry-token");
        String url=kg_user_url;
        bp.da.Log.DefaultLogWriteLine(LogType.Info, "若依getInfo请求信息："+ url+";ry-token:"+ry_token);
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("Authorization",ry_token);
        String data= HttpClientUtil.doGetSSL(url,headerMap,null);
        bp.da.Log.DefaultLogWriteLine(LogType.Info, "若依getInfo请求结果："+ data);

        JSONObject dataJson= JSON.parseObject(data);
        if(dataJson.getString("code").equals("200")){
            //获取用户信息
            JSONObject userInfo=dataJson.getJSONObject("user");
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "若依userInfo信息："+ dataJson.getString("user"));
            //获取用户id
            String userId=userInfo.getString("userName");
            try{
                //判断系统中是否存在该用户
                Emp emp=new Emp();
                if(emp.IsExit(EmpAttr.No,userId)){
                    //先登录再获取Token.
                    Dev2Interface.Port_Login(emp.getUserID());
                    String token = Dev2Interface.Port_GenerToken("PC","");
                    return token;
                }
                else{
                    return "err@此用户不存在"+userId;
                }
            }
            catch (Exception ex){
                return "err@认证失败";
            }
        }
        else{
            return "err@认证失败";
        }
    }
    /**
     * 自定义获取菜单
     * @return
     * @throws Exception
     */
    public final String Default_InitRy() throws Exception {
        try{
            String moduleNos="";
            String menuNos="";
            String ry_token = this.GetRequestVal("ry-token");
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "ry_token：" + ry_token);
            String appNo = this.GetRequestVal("appNo");
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "appNo：" + appNo);
            String isVue3 = this.GetRequestVal("isVue3");

            if(!DataType.IsNullOrEmpty(ry_token)){
                if(DataType.IsNullOrEmpty(appNo)){
                    return "err@appNo参数不能为空";
                }
                if (WebUser.getIsAdmin() == false && SystemConfig.getCCBPMRunModel() != CCBPMRunModel.SAAS) {
                    String url = kg_menu_url;
                    bp.da.Log.DefaultLogWriteLine(LogType.Info, "若依菜单请求信息：" + url + ";ry-token:" + ry_token);
                    //设置header
                    Map<String, String> headerMap = new HashMap<>();
                    headerMap.put("Authorization", ry_token);
                    headerMap.put("X-Appno", appNo);
                    //设置params
                    Map<String, String> params = new HashMap<>();
                    String data = HttpClientUtil.doGetSSL(url, headerMap, params);
                    bp.da.Log.DefaultLogWriteLine(LogType.Info, "若依菜单请求结果：" + data);
//                String data="{\n" +
//                        "    \"msg\": \"操作成功\",\n" +
//                        "    \"code\": 200,\n" +
//                        "    \"data\": [\n" +
//                        "        {\n" +
//                        "            \"name\": \"813712-45415-1011314\",\n" +
//                        "            \"path\": \"/sjhj\",\n" +
//                        "            \"hidden\": false,\n" +
//                        "            \"component\": \"Layout\",\n" +
//                        "            \"meta\": {\n" +
//                        "                \"title\": \"数据汇交\",\n" +
//                        "                \"icon\": \"build\",\n" +
//                        "                \"noCache\": false,\n" +
//                        "                \"link\": null\n" +
//                        "            }\n" +
//                        "        },\n" +
//                        "        {\n" +
//                        "            \"name\": \"14935-413119-111811\",\n" +
//                        "            \"path\": \"/ml1\",\n" +
//                        "            \"hidden\": false,\n" +
//                        "            \"redirect\": \"noRedirect\",\n" +
//                        "            \"component\": \"Layout\",\n" +
//                        "            \"alwaysShow\": true,\n" +
//                        "            \"meta\": {\n" +
//                        "                \"title\": \"目录1\",\n" +
//                        "                \"icon\": \"abnormal\",\n" +
//                        "                \"noCache\": false,\n" +
//                        "                \"link\": null\n" +
//                        "            },\n" +
//                        "            \"children\": [\n" +
//                        "                {\n" +
//                        "                    \"name\": \"144314-45015-10474\",\n" +
//                        "                    \"path\": \"cg\",\n" +
//                        "                    \"hidden\": false,\n" +
//                        "                    \"component\": \"Layout\",\n" +
//                        "                    \"meta\": {\n" +
//                        "                        \"title\": \"村规\",\n" +
//                        "                        \"icon\": \"build\",\n" +
//                        "                        \"noCache\": false,\n" +
//                        "                        \"link\": null\n" +
//                        "                    }\n" +
//                        "                }\n" +
//                        "            ]\n" +
//                        "        },\n" +
//                        "        {\n" +
//                        "            \"name\": \"12896-461013-8511\",\n" +
//                        "            \"path\": \"/ml2\",\n" +
//                        "            \"hidden\": false,\n" +
//                        "            \"redirect\": \"noRedirect\",\n" +
//                        "            \"component\": \"Layout\",\n" +
//                        "            \"alwaysShow\": true,\n" +
//                        "            \"meta\": {\n" +
//                        "                \"title\": \"目录2\",\n" +
//                        "                \"icon\": \"abnormal\",\n" +
//                        "                \"noCache\": false,\n" +
//                        "                \"link\": null\n" +
//                        "            },\n" +
//                        "            \"children\": [\n" +
//                        "                {\n" +
//                        "                    \"name\": \"810615-410149-101455\",\n" +
//                        "                    \"path\": \"kg\",\n" +
//                        "                    \"hidden\": false,\n" +
//                        "                    \"component\": \"Layout\",\n" +
//                        "                    \"meta\": {\n" +
//                        "                        \"title\": \"控规\",\n" +
//                        "                        \"icon\": \"build\",\n" +
//                        "                        \"noCache\": false,\n" +
//                        "                        \"link\": null\n" +
//                        "                    }\n" +
//                        "                }\n" +
//                        "            ]\n" +
//                        "        }\n" +
//                        "    ]\n" +
//                        "}";
                    RyMenuDto ryMenuDto = JSON.parseObject(data, RyMenuDto.class);
                    if (ryMenuDto.getCode() == 200) {
                        List<MenuInfo> menuInfoList = ryMenuDto.getData();
                        if (menuInfoList.size() > 0) {
                            //获取模块Nos
                            for (MenuInfo module : menuInfoList) {
                                MenuInfo menuInfo = module;
                                //说明有二级目录判断
                                if (menuInfo.getChildren() != null) {
                                    moduleNos += "'" + menuInfo.getName() + "',";
                                    List<MenuCh> children = menuInfo.getChildren();
                                    for (MenuCh menuCh : children) {
                                        menuNos += "'" + menuCh.getName() + "',";
                                    }
                                } else {
                                    menuNos += "'" + menuInfo.getName() + "',";
                                }

                            }
                        }
                        else{
                            return "err@菜单信息为空";
                        }
                    } else {
                        bp.da.Log.DefaultLogWriteLine(LogType.Info, "获取菜单失败：" + ryMenuDto.getMsg());
                        return "err@" + ryMenuDto.getMsg();
                    }

                }
                //appNo = "135412-4471-114313";
                DataSet myds = new DataSet();
                ///#region 构造数据容器.
                //系统表.
                MySystems systems = new MySystems();
                //systems.RetrieveAll();
                if (WebUser.getIsAdmin() == true && SystemConfig.getCCBPMRunModel() != CCBPMRunModel.SAAS)
                    systems.RetrieveAll();
                else
                    systems.Retrieve(MySystemAttr.No,appNo);
                DataTable dtSys = systems.ToDataTableField("System");
                dtSys.Columns.Add("IsOpen");

                //模块.
                Modules modules = new Modules();
                //modules.RetrieveAll();
                if (WebUser.getIsAdmin() == true && SystemConfig.getCCBPMRunModel() != CCBPMRunModel.SAAS){
                    modules.RetrieveAll();
                }
                else{
                    QueryObject qo=new QueryObject(modules);
                    qo.AddWhere(ModuleAttr.No,"in","("+moduleNos.substring(0,moduleNos.length()-1)+")");
                    qo.DoQuery();
                }
                DataTable dtModule = modules.ToDataTableField("Module");
                dtModule.Columns.Add("IsOpen");

                //菜单.
                Menus menus = new Menus();
                //menus.RetrieveAll();
                if (WebUser.getIsAdmin() == true && SystemConfig.getCCBPMRunModel() != CCBPMRunModel.SAAS) {
                    menus.RetrieveAll();
                }
                else{
                    QueryObject qo = new QueryObject(menus);
                    qo.AddWhere(MenuAttr.No, "in", "(" + menuNos.substring(0, menuNos.length() - 1) + ")");
                    qo.DoQuery();
                }
                DataTable dtMenu = menus.ToDataTableField("Menu");
                dtMenu.Columns.get("UrlExt").ColumnName = "Url";

                ///#endregion 构造数据容器.

                ///#region 把数据加入里面去.
                myds.Tables.add(dtSys);
                myds.Tables.add(dtModule);
                myds.Tables.add(dtMenu);

                ///#endregion 把数据加入里面去.
                ///#region 如果是admin.
                if (WebUser.getIsAdmin() == true && this.getItIsMobile() == false && SystemConfig.getCCBPMRunModel() != CCBPMRunModel.SAAS)
                {

                    if(isVue3 == null) {
                        ///#region 增加默认的系统.
                        DataRow dr = dtSys.NewRow();
                        dr.setValue("No", "Flows");
                        dr.setValue("Name", "流程设计");
                        dr.setValue("Icon", "");
                        dtSys.Rows.add(dr);

                        dr = dtSys.NewRow();
                        dr.setValue("No", "Frms");
                        dr.setValue("Name", "表单设计");
                        dr.setValue("Icon", "");
                        dtSys.Rows.add(dr);

                        dr = dtSys.NewRow();
                        dr.setValue("No", "System");
                        dr.setValue("Name", "系统管理");
                        dr.setValue("Icon", "");
                        dtSys.Rows.add(dr);
                    }
                    ///#endregion 增加默认的系统.

                    String sqlWhere = "";
                    if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
                    {
                        sqlWhere = " AND OrgNo='" + WebUser.getOrgNo() + "'";
                    }

                    DataSet dsAdminMenus = new DataSet();
                    AdminMenus mymenus = new AdminMenus();
                    dsAdminMenus.readXml(mymenus.getFile());
                    //增加模块.
                    DataTable dtGroup = dsAdminMenus.GetTableByName("Group");
                    for (DataRow dtRow : dtGroup.Rows)
                    {
                        DataRow drModel = dtModule.NewRow();
                        drModel.setValue("No", dtRow.get("No"));
                        drModel.setValue("Name", dtRow.get("Name"));
                        drModel.setValue("SystemNo", "System");
                        drModel.setValue("Icon", dtRow.get("Icon"));
                        dtModule.Rows.add(drModel);
                    }

                    //增加菜单.
                    DataTable dtItem = dsAdminMenus.GetTableByName("Item");
                    for (DataRow dtRow : dtItem.Rows)
                    {
                        DataRow drMenu = dtMenu.NewRow();
                        drMenu.setValue("No", dtRow.get("No"));
                        drMenu.setValue("Name", dtRow.get("Name"));
                        drMenu.setValue("ModuleNo", dtRow.get("GroupNo"));
                        drMenu.setValue("Url", dtRow.get("Url"));
                        drMenu.setValue("Icon", dtRow.get("Icon"));
                        drMenu.setValue("SystemNo", "System");
                        dtMenu.Rows.add(drMenu);
                    }
                }
                ///#endregion 如果是admin.

                //   myds.WriteXml("c:/11.xml");

                ///#region 让第一个系统的第1个模块的默认打开的.
                if (myds.GetTableByName("System").Rows.size() != 0)
                {
                    //让第一个打开.
                    myds.GetTableByName("System").Rows.get(0).setValue("IsOpen" , "true");
                    String systemNo = myds.GetTableByName("System").Rows.get(0).getValue("No").toString();
                    for (DataRow dr : myds.GetTableByName("Module").Rows)
                    {
                        if (dr.getValue("SystemNo").toString().equals(systemNo) == false)
                        {
                            continue;
                        }

                        dr.setValue("IsOpen", "true");
                        break;
                    }
                }
                ///#endregion 让第一个系统的第1个模块的第一个菜单打开.
                return Json.ToJson(myds);
            } else {

                DataSet myds = new DataSet();

                ///#region 构造数据容器.
                //系统表.
                MySystems systems = new MySystems();
                systems.RetrieveAll();
                //systems.Retrieve(MySystemAttr.No,appNo);
                DataTable dtSys = systems.ToDataTableField("System");
                dtSys.Columns.Add("IsOpen");

                //模块.
                Modules modules = new Modules();
                modules.RetrieveAll();

                DataTable dtModule = modules.ToDataTableField("Module");
                dtModule.Columns.Add("IsOpen");
                //菜单.
                Menus menus = new Menus();
                menus.RetrieveAll();
                DataTable dtMenu = menus.ToDataTableField("Menu");
                dtMenu.Columns.get("UrlExt").ColumnName = "Url";

                ///#region 把数据加入里面去.
                myds.Tables.add(dtSys);
                myds.Tables.add(dtModule);
                myds.Tables.add(dtMenu);

                ///#endregion 把数据加入里面去.

                ///#region 如果是admin.

                if (isVue3 == null) {
                    ///#region 增加默认的系统.
                    DataRow dr = dtSys.NewRow();
                    dr.setValue("No", "Flows");
                    dr.setValue("Name", "流程设计");
                    dr.setValue("Icon", "");
                    dtSys.Rows.add(dr);

                    dr = dtSys.NewRow();
                    dr.setValue("No", "Frms");
                    dr.setValue("Name", "表单设计");
                    dr.setValue("Icon", "");
                    dtSys.Rows.add(dr);

                    dr = dtSys.NewRow();
                    dr.setValue("No", "System");
                    dr.setValue("Name", "系统管理");
                    dr.setValue("Icon", "");
                    dtSys.Rows.add(dr);
                }
                ///#endregion 增加默认的系统.

                String sqlWhere = "";
                if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single) {
                    sqlWhere = " AND OrgNo='" + WebUser.getOrgNo() + "'";
                }

                DataSet dsAdminMenus = new DataSet();
                AdminMenus mymenus = new AdminMenus();
                dsAdminMenus.readXml(mymenus.getFile());
                //增加模块.
                DataTable dtGroup = dsAdminMenus.GetTableByName("Group");
                for (DataRow dtRow : dtGroup.Rows) {
                    DataRow drModel = dtModule.NewRow();
                    drModel.setValue("No", dtRow.get("No"));
                    drModel.setValue("Name", dtRow.get("Name"));
                    drModel.setValue("SystemNo", "System");
                    drModel.setValue("Icon", dtRow.get("Icon"));
                    dtModule.Rows.add(drModel);
                }
                //增加菜单.
                DataTable dtItem = dsAdminMenus.GetTableByName("Item");
                for (DataRow dtRow : dtItem.Rows) {
                    DataRow drMenu = dtMenu.NewRow();
                    drMenu.setValue("No", dtRow.get("No"));
                    drMenu.setValue("Name", dtRow.get("Name"));
                    drMenu.setValue("ModuleNo", dtRow.get("GroupNo"));
                    drMenu.setValue("Url", dtRow.get("Url"));
                    drMenu.setValue("Icon", dtRow.get("Icon"));
                    drMenu.setValue("SystemNo", "System");
                    dtMenu.Rows.add(drMenu);
                }
                ///#endregion 如果是admin.

                //   myds.WriteXml("c:/11.xml");


                ///#region 让第一个系统的第1个模块的默认打开的.
                if (myds.GetTableByName("System").Rows.size() != 0) {
                    //让第一个打开.
                    myds.GetTableByName("System").Rows.get(0).setValue("IsOpen", "true");
                    String systemNo = myds.GetTableByName("System").Rows.get(0).getValue("No").toString();
                    for (DataRow dr : myds.GetTableByName("Module").Rows) {
                        if (dr.getValue("SystemNo").toString().equals(systemNo) == false) {
                            continue;
                        }

                        dr.setValue("IsOpen", "true");
                        break;
                    }
                }
                ///#endregion 让第一个系统的第1个模块的第一个菜单打开.


                return Json.ToJson(myds);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            bp.da.Log.DefaultLogWriteLine(LogType.Error, ex.getMessage());
            return "err@"+ex.getMessage();
        }
    }
    public static String PJTBMJKEY = "[{\"key\":\"HKSPJTBMJ\"}," +
            "{\"key\":\"WZSSPJTBMJ\"},{\"key\":\"QHSPJTBMJ\"},"+
            "{\"key\":\"DZSPJTBMJ\"},{\"key\":\"WCSPJTBMJ\"},"+
            "{\"key\":\"WNSPJTBMJ\"},{\"key\":\"DFSPJTBMJ\"},"+
            "{\"key\":\"DAXPJTBMJ\"},{\"key\":\"TCXPJTBMJ\"},"+
            "{\"key\":\"CMXPJTBMJ\"},{\"key\":\"LGXPJTBMJ\"},"+
            "{\"key\":\"BSXPJTBMJ\"},{\"key\":\"CJXPJTBMJ\"},"+
            "{\"key\":\"LDXPJTBMJ\"},{\"key\":\"LSXPJTBMJ\"},"+
            "{\"key\":\"BTXPJTBMJ\"},{\"key\":\"QZXPJTBMJ\"},"+
            "{\"key\":\"HJPJTBMJ\"},"+
            "{\"key\":\"SYSPJTBMJ\"}]";
    //超大图斑数
    public  static  int CDTBS=2;
    //超小图斑数
    /***
     * 导出数据到excel
     * @return
     * @throws Exception
     */
    public Object exportDataByExcel() throws Exception {


        String sheetName="明细";
        try {
            //取出来查询条件.
            UserRegedit ur = new UserRegedit();
            ur.setMyPK(WebUser.getNo() + "_" + this.getEnsName() + "_SearchAttrs");
            ur.RetrieveFromDBSources();
            Entities ens = ClassFactory.GetEns(this.getEnsName());
            Entity en = ens.getNewEntity();

            WF_Comm wfComm = new WF_Comm();
            QueryObject qo = wfComm.Search_Data(ens, en, en.getEnMap(), ur, "CommSearch");
            EnCfg encfg = new EnCfg();
            encfg.setNo(this.getEnsName());
            //增加排序
            if (encfg.RetrieveFromDBSources() != 0) {
                String orderBy = encfg.GetValStrByKey("OrderBy");
                boolean isDesc = encfg.GetValBooleanByKey("IsDeSc");

                if (DataType.IsNullOrEmpty(orderBy) == false) {
                    try {
                        if (isDesc) {
                            qo.addOrderByDesc(orderBy);
                        } else {
                            qo.addOrderBy(orderBy);
                        }
                    } catch (RuntimeException ex) {
                        encfg.SetValByKey("OrderBy", orderBy);
                    }
                }
            }
            if (encfg.RetrieveFromDBSources() != 0) {
                qo.addOrderByDesc(en.getPK());
            }
            qo.DoQuery();

            DataTable dt = ens.ToDataTableField();

            XSSFWorkbook xssfWorkbook = null;
            FileInputStream file = new FileInputStream(SystemConfig.getPathOfDataUser() + "/TempleteOfImp/ExportDataTemplate.xlsx");

            xssfWorkbook = new XSSFWorkbook(file);
            file.close();

            XSSFSheet sheetMX=xssfWorkbook.getSheet("明细");
            //后面使用它来执行计算公式 核心代码
            FormulaEvaluator formulaEvaluator = new XSSFFormulaEvaluator((xssfWorkbook));

            DataTable  dtSum=DBAccess.RunSQLReturnTable(TemplateSql.RWMJSQL);

            for (DataRow dr: dtSum.Rows){
                String shuoshusx=String.valueOf(dr.getValue("shuoshusx"));
                String sum=String.valueOf(dr.getValue("sum"));
                int tbs=0;
                double sumScale2=0;
                try{
                     tbs=Integer.parseInt(dr.getValue("tbs").toString());
                     BigDecimal bigDecimal=new BigDecimal(sum);
                     sumScale2 =bigDecimal.doubleValue();
                }catch (Exception ex){
                    ex.printStackTrace();
                    Log.DebugWriteError(ex);
                }

                if(TemplateSql.HKS.equals(shuoshusx)){
                    //海口市任务面积
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.HKSRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.HKSTBS,tbs);
                     DataTable hkqxtbs=DBAccess.RunSQLReturnTable(TemplateSql.HKSQXTBS);
                     for(DataRow drHKQX : hkqxtbs.Rows){
                         String quxianno=String.valueOf(drHKQX.getValue("quxianno"));
                         String tbsHK=String.valueOf(drHKQX.getValue("tbs"));
                         int tbsHKINT=0;
                         try {
                             tbsHKINT=Integer.parseInt(tbsHK);
                         }catch (Exception ex){
                             ex.printStackTrace();
                             Log.DebugWriteError(ex);
                         }
                         if(TemplateSql.HKSLHQ.equals(quxianno)){
                             ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.HKSLHQTBS,tbsHKINT);
                         }
                         if(TemplateSql.HKSMLQ.equals(quxianno)){
                             ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.HKSMLQTBS,tbsHKINT);
                         }
                         if(TemplateSql.HKSQSQ.equals(quxianno)){
                             ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.HKSQSQTBS,tbsHKINT);
                         }
                         if(TemplateSql.HKSXYQ.equals(quxianno)){
                             ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.HKSXYQTBS,tbsHKINT);
                         }
                     }
                }
                if(TemplateSql.SYS.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.SYSRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.SYSTBS,tbs);
                    DataTable syqxtbs=DBAccess.RunSQLReturnTable(TemplateSql.SYSQXTBSSQL);
                    for (DataRow sydr:syqxtbs.Rows){

                        String quxianno=String.valueOf(sydr.getValue("quxianno"));
                        String tbsSY=String.valueOf(sydr.getValue("tbs"));
                        int tbsSYINT=0;
                        try {
                            tbsSYINT=Integer.parseInt(tbsSY);
                        }catch (Exception ex){
                            ex.printStackTrace();
                            Log.DebugWriteError(ex);
                        }
                        if(TemplateSql.SYSHTQ.equals(quxianno)){
                            ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.SYSHTQTBS,tbsSYINT);
                        }
                        if(TemplateSql.SYSJYQ.equals(quxianno)){
                            ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.SYSJYQTBS,tbsSYINT);

                        }
                        if(TemplateSql.SYSTYQ.equals(quxianno)){
                            ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.SYSTYQTBS,tbsSYINT);

                        }
                        if(TemplateSql.SYSYCSTQ.equals(quxianno)){
                            ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.SYSYCSTQTBS,tbsSYINT);

                        }
                        if(TemplateSql.SYSYZQ.equals(quxianno)){
                            ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.SYSYZQTBS,tbsSYINT);

                        }
                    }
                }
                if(TemplateSql.WZSS.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.WZSSRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.WZSSTBS,tbs);
                }
                if(TemplateSql.QHS.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.QHSRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.QHSTBS,tbs);
                }
                if(TemplateSql.DZS.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.DZSRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.DZSTBS,tbs);
                }
                if(TemplateSql.WCS.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.WCSRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.WCSTBS,tbs);
                }
                if(TemplateSql.WNS.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.WNSRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.WNSTBS,tbs);
                }
                if(TemplateSql.DFS.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.DFSRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.DFSTBS,tbs);
                }
                if(TemplateSql.DAX.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.DAXRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.DAXTBS,tbs);
                }
                if(TemplateSql.TCX.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.TCXRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.TCXTBS,tbs);
                }
                if(TemplateSql.CMX.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.CMXRWUMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.CMXTBS,tbs);
                }
                if(TemplateSql.LGX.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.LGXRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.LGXTBS,tbs);
                }
                if(TemplateSql.BSX.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.BSXRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.BSXTBS,tbs);
                }
                if(TemplateSql.CJX.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.CJXRWUMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.CJXTBS,tbs);
                }
                if(TemplateSql.LDX.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.LDXRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.LDXTBS,tbs);
                }
                if(TemplateSql.LSX.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.LSXRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.LSXTBS,tbs);
                }
                if(TemplateSql.BTX.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.BTXRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.BTXTBS,tbs);
                }
                if(TemplateSql.QZX.equals(shuoshusx)){
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.QZXRWMJ,sumScale2);
                    ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.QZXTBS,tbs);
                }
            }

            //平均图斑面积
            DataTable dtPJTBMJ=Json.ToDataTable(PJTBMJKEY);
            for(DataRow drPJTBMJ:dtPJTBMJ.Rows){
                String key=String.valueOf(drPJTBMJ.getValue("key"));
                Cell cellPJTBMJ=ExcelHelperTool.GetICell(xssfWorkbook,sheetName,key,CellType.NUMERIC);
                ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,key,formulaEvaluator.evaluate(cellPJTBMJ).getNumberValue());
            }
            //计算超大图斑数
            DataTable dtCDTBS=DBAccess.RunSQLReturnTable(String.format(TemplateSql.CDTBSSQL,CDTBS));
            for(DataRow drCDTBS:dtCDTBS.Rows){
                String key="CDTBS"+drCDTBS.getValue("quxianno");
                int count=Integer.valueOf(String.valueOf(drCDTBS.getValue("count")));
                ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,key,count);
            }

            //计算超小图斑书
            DataTable dtCXTBS=DBAccess.RunSQLReturnTable(String.format(TemplateSql.CXTBSSQL,CDTBS));
            for(DataRow drCXTB:dtCXTBS.Rows){
                String key="CXTBS"+drCXTB.getValue("quxianno");
                int count=Integer.valueOf(String.valueOf(drCXTB.getValue("count")));
                ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,key,count);
            }

            //计算公式  任务面积合计
            Cell cellHJ=ExcelHelperTool.GetICell(xssfWorkbook,sheetName,TemplateSql.HJRWMJ,CellType.NUMERIC);
            //执行公式，此处cell的值就是公式
            ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.HJRWMJ,formulaEvaluator.evaluate(cellHJ).getNumberValue());
            //计算合计图斑数量
            Cell cellHJTBS=ExcelHelperTool.GetICell(xssfWorkbook,sheetName,TemplateSql.HJTBS,CellType.NUMERIC);
            ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.HJTBS,formulaEvaluator.evaluate(cellHJTBS).getNumberValue());

            //计算合计超大图斑书
            Cell cellHJCDTBS=ExcelHelperTool.GetICell(xssfWorkbook,sheetName,TemplateSql.HJCDTBS,CellType.NUMERIC);
            ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.HJCDTBS,formulaEvaluator.evaluate(cellHJCDTBS).getNumberValue());

            //计算合计超小图斑数
            Cell cellHJCXTBS=ExcelHelperTool.GetICell(xssfWorkbook,sheetName,TemplateSql.HJCXTBS,CellType.NUMERIC);
            ExcelHelperTool.SetNumericVal(xssfWorkbook,sheetName,TemplateSql.HJCXTBS,formulaEvaluator.evaluate(cellHJCXTBS).getNumberValue());

            XSSFSheet sheet = xssfWorkbook.getSheet("核查表");
            // 创建单元格风格样式
            XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
            // 设置样式-单元格边框
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            int rowI = 7;
            for (DataRow dr : dt.Rows) {
                int cellNum = 1;
                if(sheet.getRow(rowI)!=null){
                    int lastRowNum=sheet.getLastRowNum();
                    sheet.shiftRows(rowI,lastRowNum,1);
                }
                XSSFRow xssfRow = sheet.createRow(rowI);
                for (int i = 0; i < 46; i++) {
                    String val="";
                    if(i==0){
                        val=String.valueOf(dr.getValue(dr.columns.get("HCTBBH")));
                    }
                    if(i==1){
                        val=String.valueOf(dr.getValue(dr.columns.get("SCJDTBBH")));
                    }
                    if(i==2){
                        val=String.valueOf(dr.getValue(dr.columns.get("SQL_HeChaQiShuT")));
                    }
                    if(i==3){
                        val=String.valueOf(dr.getValue(dr.columns.get("DKStaText")));
                    }
                    if(i==4){
                        val=String.valueOf(dr.getValue(dr.columns.get("SQL_ShiQuXianT")));
                    }
                    if(i==5){
                        val=String.valueOf(dr.getValue(dr.columns.get("SQL_XiangZhenT")));
                    }
                    if(i==6){
                        val=String.valueOf(dr.getValue(dr.columns.get("JDBH")));
                    }
                    if(i==7){
                        val=String.valueOf(dr.getValue(dr.columns.get("JDMC")));
                    }
                    if(i==8){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("JDTBMJ"))));
                    }
                    if(i==9){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("HCTBMJ"))));
                    }
                    if(i==10){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("YC_MJ"))));
                    }
                    if(i==11){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("YC_CL"))));
                    }
                    if(i==12){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("GL_MJ"))));
                    }
                    if(i==13){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("GL_CL"))));
                    }

                    if(i==14){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("GQ_MJ"))));
                    }
                    if(i==15){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("GQ_CL"))));
                    }
                    if(i==16){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("QG_MJ"))));
                    }
                    if(i==17){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("QG_CL"))));
                    }

                    if(i==18){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("DL_MJ"))));
                    }
                    if(i==19){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("DL_CL"))));
                    }

                    if(i==20){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("QTSC_MJ"))));
                    }
                    if(i==21){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("QTSC_CL"))));
                    }
                    //销售去向1
                    if(i==22){
                        val=String.valueOf(dr.getValue(dr.columns.get("SQL_SX1T")));
                    }
                    if(i==23){
                        val=String.valueOf(dr.getValue(dr.columns.get("SQL_XZ1T")));
                    }
                    if(i==24){
                        val=String.valueOf(dr.getValue(dr.columns.get("ZZZLText")));
                    }
                    if(i==25){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("ShuLiang1"))));
                    }

                    if(i==26){
                        val=String.valueOf(dr.getValue(dr.columns.get("SQL_SX2T")));
                    }
                    if(i==27){
                        val=String.valueOf(dr.getValue(dr.columns.get("SQL_XZ2T")));
                    }
                    if(i==28){
                        val=String.valueOf(dr.getValue(dr.columns.get("ZZZL2Text")));
                    }
                    if(i==29){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("ShuLiang2"))));
                    }

                    if(i==30){
                        val=String.valueOf(dr.getValue(dr.columns.get("SQL_SX3T")));
                    }
                    if(i==31){
                        val=String.valueOf(dr.getValue(dr.columns.get("SQL_XZ3T")));
                    }
                    if(i==32){
                        val=String.valueOf(dr.getValue(dr.columns.get("ZZZL3Text")));
                    }
                    if(i==33){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("ShuLiang3"))));
                    }

                    if(i==34){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("LSL_MJ"))));
                    }
                    if(i==35){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("SGL_MJ"))));
                    }
                    if(i==36){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("QTNZW_MJ"))));
                    }
                    if(i==37){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("LSPH_MJ"))));
                    }

                    if(i==38){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("WSDP_MJ"))));
                    }
                    if(i==39){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("JYDP_MJ"))));
                    }
                    if(i==40){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("FCWBZYP_MJ"))));
                    }
                    if(i==41){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("XGPFJSGZ_MJ"))));
                    }
                    if(i==42){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("LDSC_MJ"))));
                    }
                    if(i==43){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("SSDPZZMJ"))));
                    }
                    if(i==44){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("DPZZZMJ"))));
                    }
                    if(i==45){
                        val=isZero(String.valueOf(dr.getValue(dr.columns.get("DPZZZCL"))));
                    }
                    if(rowI==7){
                        sheet.setColumnWidth(cellNum,getColumnWidth(val));
                    }
                    Cell cell = xssfRow.createCell(cellNum++);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(val);
                }
                rowI++;
            }
            String fileName ="摸排核查信息.xlsx";
            String savePath = SystemConfig.getPathOfDataUser() + "Temp/" + fileName;
            FileOutputStream saveFile = new FileOutputStream(savePath);
            //FileStream saveFile = new FileStream(savePath, FileMode.CreateNew, FileAccess.Write);
            xssfWorkbook.write(saveFile);
            saveFile.close();
            HttpHandlerGlo.DownloadFile(savePath, fileName);
        } catch (Exception ex) {
            throw  ex;
        }
        return "info@导出成功";
    }

    /***
     * 导入方法
     * @return
     * @throws Exception
     */
    public final String ImpEntityData_Done() throws Exception {

        String token = this.GetRequestVal("token");
        Dev2Interface.Port_LoginByToken(token);
        HttpServletRequest request = ContextHolderUtils.getRequest();
        String contentType = request.getContentType();
        MultipartHttpServletRequest multipartRequest = null;
        if (contentType != null && contentType.indexOf("multipart/form-data") != -1)
            multipartRequest = CommonFileUtils.getMultipartHttpServletRequest(request);
        if (multipartRequest == null)
            return "err@请求出错";
        List<MultipartFile> items = multipartRequest.getFiles("file");
        MultipartFile file = items.get(0);
        //文件存放路径
        String filePath = SystemConfig.getPathOfTemp() + "/" + DBAccess.GenerGUID() + ".xls";

        File fileFullName = new File(filePath);

        DataType.getFileByBytes(file.getBytes(), filePath);

        //从excel里面获得数据表.
        DataTable dt = DBLoad.ReadExcelFileToDataTable(filePath, 0);
        if (fileFullName.exists() == true)
            fileFullName.delete();

        try {
            String errInfo = "";
            BasicInfo basicInfo = new BasicInfo();
            for (DataRow dr : dt.Rows) {
                String no = String.valueOf(dr.getValue("图斑编号"));
                basicInfo.setNo(no);

                int count = basicInfo.Retrieve(EntityNoAttr.No, no);

                String hcqs = String.valueOf(dr.getValue("核查期数"));
                basicInfo.setHCQS(hcqs);
                basicInfo.setISHC(0);
                basicInfo.SetValByKey(BasicInfoAttr.ISHCT, "否");
                basicInfo.setDKSta(0);
                basicInfo.SetValByKey(BasicInfoAttr.DKStaT, "未提交");

                String shuoshuSX = String.valueOf(dr.getValue("所属市县"));

                String quXianNo = String.valueOf(dr.getValue("行政区代码"));
                basicInfo.SetValByKey(BasicInfoAttr.QuXianNo, quXianNo);
                basicInfo.SetValByKey(BasicInfoAttr.SHuoShuSX, shuoshuSX);
                if (DataType.IsNullOrEmpty(quXianNo)) {
                    errInfo += "err@图斑编号[" + no + "]行政区代码不允许为空";
                    continue;
                }

                //0 省  1 市县 2 区 3 乡镇  citylevel
                if (DataType.IsNullOrEmpty(quXianNo)) {
                    errInfo += "err@图斑编号[" + no + "]查询不到此行政区代码数据";
                    continue;
                }
                String quXianNoT = String.valueOf(dr.getValue("行政区名称"));
                basicInfo.SetValByKey(BasicInfoAttr.QuXianNoT, quXianNoT);

                //String DBAccess.RunSQLReturnString("select  * from  sys_city where city_code ='460108'");
                String xiangZhenNo = String.valueOf(dr.getValue("乡镇代码"));
                basicInfo.SetValByKey(BasicInfoAttr.XiangZhenNo, xiangZhenNo);
                if (DataType.IsNullOrEmpty(xiangZhenNo)) {
                    errInfo += "err@图斑编号[" + no + "]乡镇编码不允许为空";
                    continue;
                }

                String xiangZhenNoT = String.valueOf(dr.getValue("乡镇名称"));
                basicInfo.SetValByKey(BasicInfoAttr.XiangZhenNoT, xiangZhenNoT);

                String sjly = String.valueOf(dr.getValue("数据来源"));
                basicInfo.SetValByKey(BasicInfoAttr.SJLY, sjly);

                String zzzl = String.valueOf(dr.getValue("种植种类"));
                basicInfo.SetValByKey(BasicInfoAttr.ZZZL, zzzl);

                String txmj = String.valueOf(dr.getValue("图形面积"));
                basicInfo.SetValByKey(BasicInfoAttr.TXMJ, txmj);

                float jdmj = toFloatInternal(String.valueOf(dr.getValue("基地面积")));
                basicInfo.SetValByKey(BasicInfoAttr.JDMJ, jdmj);

                float zzmjF = toFloatInternal(String.valueOf(dr.getValue("种植面积")));
                basicInfo.SetValByKey(BasicInfoAttr.ZHMJ, zzmjF);

                String jdmc = String.valueOf(dr.getValue("基地名称"));
                basicInfo.SetValByKey(BasicInfoAttr.JDMC, jdmc);

                String jdfzr = String.valueOf(dr.getValue("基地负责人"));
                basicInfo.SetValByKey(BasicInfoAttr.JDFZE, jdfzr);

                String lxfs = String.valueOf(dr.getValue("联系方式"));
                basicInfo.SetValByKey(BasicInfoAttr.LXFS, lxfs);

                String jdzb = String.valueOf(dr.getValue("基地坐标"));
                basicInfo.SetValByKey(BasicInfoAttr.JDZB, jdzb);

                String tdsyqrjdh = String.valueOf(dr.getValue("土地所有权人及电话"));
                basicInfo.SetValByKey(BasicInfoAttr.TDWYQRJDH, tdsyqrjdh);

                String zgqrjdh = String.valueOf(dr.getValue("资格权人及电话"));
                basicInfo.SetValByKey(BasicInfoAttr.ZGQRJDH, zgqrjdh);

                String jyqrjdh = String.valueOf(dr.getValue("经营权人及电话"));
                basicInfo.SetValByKey(BasicInfoAttr.JYQRJDH, jyqrjdh);

                String zzrjdh = String.valueOf(dr.getValue("种植人及电话"));
                basicInfo.SetValByKey(BasicInfoAttr.ZZRJDH, zzrjdh);

                float cljh = toFloatInternal(String.valueOf(dr.getValue("产量计划")));
                basicInfo.SetValByKey(BasicInfoAttr.CLJH, cljh);


                if (count > 0) {
                    basicInfo.Update();
                } else {
                    basicInfo.Insert();
                }

            }
            return "@info导入成功";
        } catch (Exception ex) {
            Log.DebugWriteError(ex);
            return "err@" + ex.getMessage();
        }
    }


    public String isZero(String val) {
        BigDecimal rewardPercent = new BigDecimal("0.00000000");
        if (rewardPercent.toString().equals(val)) {
            return   "0.00";
        } else {
            rewardPercent=  new  BigDecimal(val).setScale(2, RoundingMode.HALF_UP);

            return rewardPercent.toString();
        }
    }

    public int getColumnWidth(String val) {
        if(DataType.IsNullOrEmpty(val)){
            return 5*256;
        }
        if(val.length()<5){
            return 5*256;
        }

        int intCeiling = (int) Math.ceil(val.length()*1.25*256);
        return intCeiling;
    }

    public float toFloatInternal(String str) {
        if (str == null || str.trim().isEmpty()) {
            return 0.0f;
        }
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }
}
