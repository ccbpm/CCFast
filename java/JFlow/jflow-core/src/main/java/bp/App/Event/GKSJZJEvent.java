package bp.App.Event;
import bp.da.DBAccess;
import bp.da.LogType;
import bp.difference.SystemConfig;
import bp.sys.*;
import bp.tools.HttpClientUtil;
import bp.wf.GenerWorkFlow;
import bp.wf.data.GERpt;
import bp.wf.data.GERptAttr;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSeedValueMDP;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GKSJZJEvent extends bp.sys.base.EventBase{

    //国控临时入库url
    //"http://172.25.172.44:84/webservice.asmx/Linshi_Import_All"  测试
    //"http://172.25.172.44:86/webservice.asmx/Linshi_Import_All"  正式
    private static String gk_rk_test_url="http://172.25.172.44:84/webservice.asmx/Linshi_Import_All";
    //国控临时入库删除url
    //"http://172.25.172.44:84/webservice.asmx/Linshi_Delete"  测试
    //"http://172.25.172.44:86/webservice.asmx/Linshi_Delete"  正式
    private static String gk_rk_test_del_url="http://172.25.172.44:84/webservice.asmx/Linshi_Delete";

    //国控正式入库url
    //"http://172.25.172.44:84/webservice.asmx/Zhengshi_Import"  测试
    //"http://172.25.172.44:86/webservice.asmx/Zhengshi_Import"  正式
    private static String gk_rk_url="http://172.25.172.44:84/webservice.asmx/Zhengshi_Import";


    //村规数据质检url
    //"http://59.212.37.75:88/WebService.asmx/checkLayerMutiple"  测试
    //"http://172.25.172.44:88/WebService.asmx/checkLayerMutiple"  正式
    private static String cg_sjzj_url="http://59.212.37.75:88/WebService.asmx/checkLayerMutiple";

    //专题数据质检url
    //"http://59.212.37.75:88/WebService.asmx/checkLayerMutiple_ZT"  测试
    //"http://172.25.172.44:88/WebService.asmx/checkLayerMutiple_ZT"  正式
    private String zt_sjzj_url="http://59.212.37.75:88/WebService.asmx/checkLayerMutiple_ZT";

    //控规数据质检url
    //"http://59.212.37.75:88/WebService.asmx/checkLayerMutiple_KG"  测试
    //"http://172.25.172.44:88/WebService.asmx/checkLayerMutiple_KG"  正式
    private static String kg_sjzj_url="http://59.212.37.75:88/WebService.asmx/checkLayerMutiple_KG";

    //是否测试环境
    private static boolean isTest=true;
    public GKSJZJEvent() throws Exception{
        this.setTitle("数据质检事件");
    }
    private void urlExt(){
        if(isTest){
            return;
        }
        //国控临时入库url
        gk_rk_test_url="http://172.25.172.44:86/webservice.asmx/Linshi_Import_All";
        //国控临时入库删除url
        gk_rk_test_del_url="http://172.25.172.44:86/webservice.asmx/Linshi_Delete";
        //国控正式入库url
        gk_rk_url="http://172.25.172.44:86/webservice.asmx/Zhengshi_Import";
        //村规数据质检url
        cg_sjzj_url="http://172.25.172.44:88/WebService.asmx/checkLayerMutiple";
        //专题数据质检url
        zt_sjzj_url="http://172.25.172.44:88/WebService.asmx/checkLayerMutiple_ZT";
        //控规数据质检url
        kg_sjzj_url="http://172.25.172.44:88/WebService.asmx/checkLayerMutiple_KG";
    }
    @Override
    public void Do() {
        urlExt();
        try {
            GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "数据质检："+gwf.getNodeID());
            this.SucessInfo ="";
            switch (this.getEventSource()) {
                case EventListNode.SendSuccess://发送成功后
                    DoEventBase(gwf);
                    break;
                case EventListNode.UndoneAfter://撤销发送之后
                    break;
                case EventListNode.SendWhen://发送前
                    DoEventBase(gwf);
                    break;
                case EventListNode.ReturnAfter://退回后
                    break;
                case EventListFlow.FlowOverAfter://流程结束后
                    break;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * 事件
     */
    private void DoEventBase(GenerWorkFlow gwf){
        switch (gwf.getNodeID()) {
            case 101://入临时库
                Cg_ImportData(gwf,false);
                break;
            case 112://村规数据正式入库
                Cg_ImportData(gwf,true);
                break;
            case 102://村规数据质检
                Cg_Checked(gwf);
                break;
            case 202://专题规范数据质检
                ZT_Checked(gwf);
                break;
            case 301://控规数据质检
                Kg_Checked(gwf);
//                KG_ZSRK(gwf);
                break;
            case 310://控规数据质检
                Kg_Checked(gwf);
                break;
            case 324://控规入正式库
                KG_ZSRK(gwf);
                break;
        }
    }
    private static final ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    /**
     * 村规数据质检
     */
    public void Cg_Checked(GenerWorkFlow gwf){
        Instant instant=Instant.now();
        List<String> fileList=new ArrayList<>();
        List<String> fileNameList=new ArrayList<>();
        List<String> fileType=new ArrayList<>();
        try {
            FrmAttachmentDBs frmAttachmentDBs=new FrmAttachmentDBs();
            frmAttachmentDBs.Retrieve(FrmAttachmentDBAttr.RefPKVal,gwf.getWorkID(),FrmAttachmentDBAttr.FK_FrmAttachment,"ND101_AthZJBG");
            if(frmAttachmentDBs.size()<=0) {
                GERpt rpt = new GERpt("ND1Rpt");
                rpt.Retrieve(GERptAttr.OID, gwf.getWorkID());

                FrmAttachmentDBs dbs = new FrmAttachmentDBs();
                dbs.Retrieve(FrmAttachmentDBAttr.RefPKVal, gwf.getWorkID());
                for (FrmAttachmentDB frmAttachmentDB : dbs.ToJavaList()) {
                    if (frmAttachmentDB.getFKFrmAttachment().equals("ND101_ACZGHC")) {
                        fileType.add(frmAttachmentDB.getFileExts());
                        File file=new File(frmAttachmentDB.getFileName());
                        //fileNameList.add(instant.toEpochMilli()+"_"+file.getName());
                        fileNameList.add(file.getName());
                        // 将文件转换为Base64编码的字符串
                        String base64String = encodeFileToBase64(new File(frmAttachmentDB.getFileFullName()));
                        fileList.add(base64String);
                    }
                }

                HashMap<String, Object> hashMap = new HashMap<>();
                //行政区代码
                hashMap.put("XZQDM", empStr(rpt.GetValByKey("SZSX")));
                //行政区名称
                hashMap.put("XZQMC", rpt.GetValByKey("SZSX") + "+" + rpt.GetValByKey("CJXZCDM") + "+" + rpt.GetValByKey("XZCMCT"));
                //方案名称
                hashMap.put("FAMC", empStr(rpt.GetValByKey("FAMC")));
                //备案号
                hashMap.put("BAH", rpt.GetValByKey("BillNo"));
                //申请单位
                hashMap.put("SQDW", empStr(rpt.GetValByKey("SQDept")));
                //经办人
                hashMap.put("JBR",empStr( rpt.GetValByKey("JBR")));
                //入库类型
                SysEnums enums = new SysEnums();
                enums.Retrieve(SysEnumAttr.EnumKey, "RKLX");
                for (SysEnum sysEnum : enums.ToJavaList()) {
                    if (sysEnum.getIntKey() == Integer.parseInt(rpt.GetValByKey("RKLX").toString()))
                        hashMap.put("BALX", sysEnum.getLab());
                }
                //编制主体
                hashMap.put("BZZT", empStr(rpt.GetValByKey("ZZBZZT")));
                //编制单位
                hashMap.put("BZJSDW", empStr(rpt.GetValByKey("BZJSDW")));
                hashMap.put("PFSJ", empStr(rpt.GetValByKey("PFSJ")));

                //耕地指标
                HashMap<String, Object> gdzb = new HashMap<>();
                gdzb.put("XZZB", empStr(rpt.GetValByKey("GDBYLXZZB")));
                gdzb.put("GHZB", empStr(rpt.GetValByKey("GDBYLGHZB")));
                //林地指标
                HashMap<String, Object> ldzb = new HashMap<>();
                ldzb.put("XZZB", empStr(rpt.GetValByKey("LDBYLXZZB")));
                ldzb.put("GHZB", empStr(rpt.GetValByKey("LDBYLGHZB")));
                //建设用地范围
                HashMap<String, Object> jsydfw = new HashMap<>();
                jsydfw.put("XZZB", empStr(rpt.GetValByKey("JSYDZGMXZZB")));
                jsydfw.put("GHZB", empStr(rpt.GetValByKey("JSYDZGMGHZB")));
                jsydfw.put("JDZB", empStr(rpt.GetValByKey("JSYDZGMJDZB")));

                //规划指标
                HashMap<String, Object> ghzb = new HashMap<>();
                ghzb.put("GDBYL", gdzb);
                ghzb.put("LDBYL", ldzb);
                ghzb.put("CZJSYD", jsydfw);

                hashMap.put("GHZB", ghzb);

                //入库成果类型
                enums = new SysEnums();
                enums.Retrieve(SysEnumAttr.EnumKey, "RKCGLX");
                for (SysEnum sysEnum : enums.ToJavaList()) {
                    if (sysEnum.getIntKey() == Integer.parseInt(rpt.GetValByKey("RKCGLX").toString()))
                        hashMap.put("RKCGLX", sysEnum.getLab());
                }
                //附件类型（base64/file）
                hashMap.put("DATATYPE", "base64");
                //所处阶段
                hashMap.put("PERIOD", 3);
                //附件列表
                hashMap.put("FileList", fileList);
                //附件名称列表
                hashMap.put("FILENAMELIST", fileNameList);

                //规划分类标准
                hashMap.put("GHYDFLBZ", empStr(""));
                Map<String, String> map = new HashMap<>();
                map.put("json", bp.tools.Json.ToJson(hashMap));

//                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//                es.submit(() -> {
                try{
//                        RequestContextHolder.setRequestAttributes(requestAttributes);
                    bp.da.Log.DefaultLogWriteLine(LogType.Info, "村规数据质检json：");
//                        bp.da.Log.DefaultLogWriteLine(LogType.Info, map.toString());
                    //测试地址
                    String data = HttpClientUtil.doPost(cg_sjzj_url, map, null, null);
                    bp.da.Log.DefaultLogWriteLine(LogType.Info, "村规数据质检请求结果："+ data);
                    JSONObject jd = JSONObject.parseObject(data);
                    if (jd.get("code").toString().equals("1")) {
                        JSONArray results = JSONArray.parseArray(jd.get("results").toString());
                        String result = results.get(0).toString();
                        try {
                            JSONObject res = JSONObject.parseObject(result);
                            if (res.get("code").toString().equals("0")) {
                                bp.da.Log.DefaultLogWriteLine(LogType.Info, "质检不通过具体提示："+ res.get("msg").toString()+":"+res.get("reportUrl").toString());
                            }
                            String myfilePath = SystemConfig.getPathOfTemp() + DBAccess.GenerGUID() + ".pdf";
                            try {
                                //先删除质检报告
                                FrmAttachmentDBs aths=new FrmAttachmentDBs();
                                aths.Retrieve(FrmAttachmentDBAttr.RefPKVal,gwf.getWorkID(),FrmAttachmentDBAttr.FK_FrmAttachment,"ND101_AthZJBG");
                                aths.Delete();
                                //自动上传质检报告
                                bp.da.DataType.HttpDownloadFile(handleUrl(res.get("reportUrl").toString()), myfilePath);
                                File file = new File(myfilePath);
                                bp.wf.Dev2Interface.CCForm_AddAth(gwf.getNodeID(), gwf.getFlowNo(), gwf.getWorkID(), "ND101_AthZJBG", "ND101", myfilePath, file.getName());
                            } catch (Exception ex) {
                                bp.da.Log.DefaultLogWriteLine(LogType.Info, "村规质检自动上传质检结果异常："+ ex.getMessage());
                                //如果失败的话，让用户手动下载并上传
                                this.SucessInfo = "err@" + "@请手动下载质检报告并上传:" + "@<a href='" + res.get("reportUrl").toString() + "'>质检报告</a>";
                            }
                        } catch (Exception ex) {
                            bp.da.Log.DefaultLogWriteLine(LogType.Info, "数据质检异常："+ ex.getMessage());
                            //质检出错
                            this.SucessInfo = "err@数据质检出现异常:"+ex.getMessage();
                        }
//                            this.SucessInfo = "数据质检上传成功";
                    } else {
                        //执行失败
                        bp.da.Log.DefaultLogWriteLine(LogType.Info, "数据质检不通过："+ jd.get("msg").toString());
                        this.SucessInfo = "err@数据质检不通过，" + jd.get("msg").toString();
                    }
                } catch (Exception ex) {
                    bp.da.Log.DefaultLogWriteLine(LogType.Info, "数据质检异常,返回数据格式不正确："+ ex.getMessage());
                    //质检出错
                    this.SucessInfo = "err@数据质检出现异常:"+ex.getMessage();
                }

//                });
            } else {
                this.SucessInfo ="数据质检审核中";
            }
        }
        catch (Exception ex){
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "数据质检请求异常："+ ex.getMessage());
            this.SucessInfo="err@数据质检出现异常,请检查填写的数据是否正确，或联系管理员";
        }

    }

    /**
     * 村规数据入库
     */
    public void Cg_ImportData(GenerWorkFlow gwf,Boolean isZs){
        try {
            //数据入正式库
            GERpt rpt = new GERpt("ND1Rpt");
            rpt.Retrieve(GERptAttr.OID, gwf.getWorkID());
            Map<String, String> importJsonMap = new HashMap<>();
            //备案号
            importJsonMap.put("BAH", rpt.GetValByKey("BillNo").toString());
            //如果入正式库
            if(isZs){
                //入库号
                importJsonMap.put("RKH", rpt.GetValByKey("BillNo").toString());
            }
            //规划类型
            importJsonMap.put("PlanType", "CZ");
            //数管阶段状态
            importJsonMap.put("sgstage", "");
            //规划分类标准
            importJsonMap.put("GHYDFLBZ", "");
            //调整类型
            if (Integer.parseInt(rpt.GetValByKey("RKLX").toString()) == 1)
                importJsonMap.put("BALX_INDEX", "7");
            if (Integer.parseInt(rpt.GetValByKey("RKLX").toString()) == 4)
                importJsonMap.put("BALX_INDEX", "9");

            //片区名称
            importJsonMap.put("pqmc", "");

            List<String> fileList=new ArrayList<>();
            List<String> fileNameList=new ArrayList<>();
            List<String> fileType=new ArrayList<>();

            //附件集合
            FrmAttachmentDBs dbs = new FrmAttachmentDBs();
            dbs.Retrieve(FrmAttachmentDBAttr.RefPKVal, gwf.getWorkID());
            for (FrmAttachmentDB frmAttachmentDB : dbs.ToJavaList()) {
                if (frmAttachmentDB.getFKFrmAttachment().equals("ND101_ACZGHC")) {
                    if (Integer.parseInt(rpt.GetValByKey("RKLX").toString()) == 1){
                        fileType.add("70106");
                    }
                    if (Integer.parseInt(rpt.GetValByKey("RKLX").toString()) == 4){
                        fileType.add("170106");
                    }
                    fileNameList.add(frmAttachmentDB.getFileName());
                    // 将文件转换为Base64编码的字符串
                    String base64String = encodeFileToBase64(new File(frmAttachmentDB.getFileFullName()));
                    fileList.add(base64String);
                }
            }

            Map<String, String> importMap = new HashMap<>();
            importMap.put("json", bp.tools.Json.ToJson(importJsonMap));
            importMap.put("filetype", bp.tools.Json.ToJson(fileType));
            importMap.put("filebase",  bp.tools.Json.ToJson(fileList));
            importMap.put("filename",  bp.tools.Json.ToJson(fileNameList));

            bp.da.Log.DefaultLogWriteLine(LogType.Info, "村规数据入库请求json：" + bp.tools.Json.ToJson(importJsonMap));
//            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//            es.submit(() -> {
//                RequestContextHolder.setRequestAttributes(requestAttributes);
            //入库
            String importData = "";
            //如果入正式库
            if(isZs){

                importData=HttpClientUtil.doPost(gk_rk_url, importMap, null, null);
                bp.da.Log.DefaultLogWriteLine(LogType.Info, "村规数据正式入库返回结果：" + importData);
            }
            ///入测试库
            else{
                //入临时库之前，先删除原有的数据，避免重复
                String deleteDataUrl=gk_rk_test_del_url;
                Map<String, String> deleteMap = new HashMap<>();
                deleteMap.put("bah", rpt.GetValByKey("BillNo").toString());
                deleteMap.put("plantype", "CZ");
                deleteMap.put("sgstage", "");
                bp.da.Log.DefaultLogWriteLine(LogType.Info, "删除村规数据临时入库参数："+ bp.tools.Json.ToJson(deleteMap));
                String deleteData = HttpClientUtil.doPost(deleteDataUrl, deleteMap, null, null);
                bp.da.Log.DefaultLogWriteLine(LogType.Info, "删除村规数据临时入库返回结果："+ deleteData);
                //入临时库
                importData = HttpClientUtil.doPost(gk_rk_test_url, importMap, null, null);
                bp.da.Log.DefaultLogWriteLine(LogType.Info, "村规数据临时入库返回结果："+ importData);
            }
//            });
            this.SucessInfo ="数据入库中";
        }
        catch (Exception ex){
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "数据入库出现异常："+ ex.getMessage());
            this.SucessInfo="err@数据入库出现异常,请联系管理员";
        }

    }

    /**
     *专题数据质检
     * @param gwf
     */
    public void ZT_Checked(GenerWorkFlow gwf){
        List<String> fileList=new ArrayList<>();
        List<String> fileNameList=new ArrayList<>();
        List<String> fileType=new ArrayList<>();
        try {
            FrmAttachmentDBs frmAttachmentDBs=new FrmAttachmentDBs();
            frmAttachmentDBs.Retrieve(FrmAttachmentDBAttr.RefPKVal,gwf.getWorkID(),FrmAttachmentDBAttr.FK_FrmAttachment,"ND201_AthZJBG");
            if(frmAttachmentDBs.size()<=0) {
                GERpt rpt = new GERpt("ND2Rpt");
                rpt.Retrieve(GERptAttr.OID, gwf.getWorkID());

                FrmAttachmentDBs dbs = new FrmAttachmentDBs();
                dbs.Retrieve(FrmAttachmentDBAttr.RefPKVal, gwf.getWorkID());
                for (FrmAttachmentDB frmAttachmentDB : dbs.ToJavaList()) {
                    if (frmAttachmentDB.getFKFrmAttachment().equals("ND201_AthSLSJ")) {
                        fileType.add(frmAttachmentDB.getFileExts());
                        fileNameList.add(frmAttachmentDB.getFileName());
                        // 将文件转换为Base64编码的字符串
                        String base64String = encodeFileToBase64(new File(frmAttachmentDB.getFileFullName()));
                        fileList.add(base64String);
                    }
                }

                HashMap<String, Object> hashMap = new HashMap<>();
                //行政区代码
                hashMap.put("XZQDM", rpt.GetValByKey("SQL_SX"));
                //行政区名称
//                hashMap.put("XZQMC", rpt.GetValByKey("SQL_SX") + "+" + rpt.GetValByKey("CJXZCDM") + "+" + rpt.GetValByKey("XZCMCT"));
                hashMap.put("XZQMC", rpt.GetValByKey("SQL_SXT"));
                //方案名称
                hashMap.put("FAMC", empStr(rpt.GetValByKey("FAMC")));
                //备案号
                hashMap.put("BAH", rpt.GetValByKey("BillNo"));
                //申请单位
                hashMap.put("SQDW", empStr(rpt.GetValByKey("SQDW")));
                //经办人
                hashMap.put("JBR", empStr(rpt.GetValByKey("JBR")));
                //入库类型
//                SysEnums enums = new SysEnums();
//                enums.Retrieve(SysEnumAttr.EnumKey, "RKLX");
//                for (SysEnum sysEnum : enums.ToJavaList()) {
//                    if (sysEnum.getIntKey() == Integer.parseInt(rpt.GetValByKey("RKLX").toString()))
//                        hashMap.put("BALX", sysEnum.getLab());
//                }
                //附件类型（base64/file）
                hashMap.put("DATATYPE", "base64");
                //所处阶段
                hashMap.put("PERIOD", 3);
                //附件列表
                hashMap.put("FileList", fileList);
                //附件名称列表
                hashMap.put("FILENAMELIST", fileNameList);

                //是否统计住宅用地
                hashMap.put("SFBZXZLZF", "否");
                //是否进行农村存量建设用地审查
                hashMap.put("GHSFYRK", "否");

                Map<String, String> map = new HashMap<>();
                map.put("json", bp.tools.Json.ToJson(hashMap));

                bp.da.Log.DefaultLogWriteLine(LogType.Info, "专题规划json："+ bp.tools.Json.ToJson(hashMap));

                //测试地址
                String data = HttpClientUtil.doPost(zt_sjzj_url, map, null, null);
                bp.da.Log.DefaultLogWriteLine(LogType.Info, "专题规划数据质检请求结果："+ data);
                JSONObject jd = JSONObject.parseObject(data);
                if (jd.get("code").toString().equals("1")) {
                    JSONArray results = JSONArray.parseArray(jd.get("results").toString());
                    String result = results.get(0).toString();
                    JSONObject res = JSONObject.parseObject(result);
                    String myfilePath = SystemConfig.getPathOfTemp() + DBAccess.GenerGUID() + ".pdf";
                    try {
                        //先删除
                        FrmAttachmentDBs aths=new FrmAttachmentDBs();
                        aths.Retrieve(FrmAttachmentDBAttr.RefPKVal,gwf.getWorkID(),FrmAttachmentDBAttr.FK_FrmAttachment,"ND201_AthZJBG");
                        aths.Delete();
                        //自动上传质检报告
                        bp.da.DataType.HttpDownloadFile(handleUrl(res.get("reportUrl").toString()), myfilePath);
                        File file = new File(myfilePath);
                        bp.wf.Dev2Interface.CCForm_AddAth(gwf.getNodeID(), gwf.getFlowNo(), gwf.getWorkID(), "ND201_AthZJBG", "ND201", myfilePath, file.getName());
                    } catch (Exception ex) {
                        bp.da.Log.DefaultLogWriteLine(LogType.Info, "专题规划数据质检异常："+ ex.getMessage());
                        //如果失败的话，让用户手动下载并上传
                        this.SucessInfo = "err@" + "@请手动下载质检报告并上传:" + "@<a href='" + res.get("reportUrl").toString() + "'>质检报告</a>";
                    }

                } else {
                    bp.da.Log.DefaultLogWriteLine(LogType.Info, "专题规划数据质检不通过："+ jd.get("msg").toString());
                    //执行失败
                    this.SucessInfo = "err@专题规划数据质检不通过，" + jd.get("msg").toString();
                }
            }
            else{
                this.SucessInfo ="数据质检审核中";
            }
        }
        catch (Exception ex){
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "专题规划数据质检请求异常："+ ex.getMessage());
            this.SucessInfo="err@数据质检出现异常,请检查填写的数据是否正确，或联系管理员";
        }
    }

    /**
     * 专题数据入库
     * @param gwf
     * @param isZs
     */
    public void ZT_ImportData(GenerWorkFlow gwf,Boolean isZs){
        //暂时没有接口
    }
    //控规质检，临时，正式入库通用数据获取
    private boolean KG_DATA(GenerWorkFlow gwf,GERpt rpt ,List<String> fileList,List<String> fileNameList,List<String> fileTypeIdList ) throws Exception {
        Boolean isAddZJBG=false;
        FrmAttachmentDBs dbs=new FrmAttachmentDBs();
        dbs.Retrieve(FrmAttachmentDBAttr.RefPKVal,gwf.getWorkID());
        for (FrmAttachmentDB frmAttachmentDB : dbs.ToJavaList()) {
            //已经有质检报告
            if (frmAttachmentDB.getFKFrmAttachment().equals("AthZJBG")) {
                isAddZJBG=true;
            }
            //如果是重大调整（地块调整）
            if(rpt.GetValByKey("KGRKLX").toString().equals("3")) {
                //控制性详细规划调整地块项目区矢量层
                Kg_lsrks(frmAttachmentDB,"ND301_AthKZXXXGHDZDKXMQSLC"
                        ,"301","30801",fileTypeIdList,fileNameList,fileList);

                //如果是入库阶段提交
                if(rpt.GetValByKey("JD").toString().equals("2")) {
                    //控制性详细规划调整地块分析矢量层
                    Kg_lsrks(frmAttachmentDB,"ND301_AthKZXXXGHDZDKFXSLT"
                            ,"301","30601",fileTypeIdList,fileNameList,fileList);

                    //控制性详细规划调整成果矢量数据库
                    Kg_lsrks(frmAttachmentDB,"ND301_AthKZXXXGHDZCGSLSJK"
                            ,"301","30701",fileTypeIdList,fileNameList,fileList);

                }
            }
            //如果是重大调整（整体更新）
            if(rpt.GetValByKey("KGRKLX").toString().equals("2")) {
                //控制性详细规划调整地块项目区矢量层
                Kg_lsrks(frmAttachmentDB,"ND301_AthKZXXXGHDZDKXMQSLC"
                        ,"301","150116",fileTypeIdList,fileNameList,fileList);

                //如果是入库阶段提交
                if(rpt.GetValByKey("JD").toString().equals("2")) {
                    //控制性详细规划成果矢量数据库
                    Kg_lsrks(frmAttachmentDB,"ND301_AthKZXXXGHCGSLSJK"
                            ,"301","150102",fileTypeIdList,fileNameList,fileList);

                }
            }
            //如果是一般调整
            if(rpt.GetValByKey("KGRKLX").toString().equals("4")) {
                //控制性详细规划调整地块分析矢量层
                //控制性详细规划调整成果矢量数据库
                Kg_lsrks(frmAttachmentDB,"ND301_AthKZXXXGHDZDKFXSLT"
                        ,"301","40401",fileTypeIdList,fileNameList,fileList);

                //控制性详细规划调整成果矢量数据库
                Kg_lsrks(frmAttachmentDB,"ND301_AthKZXXXGHDZCGSLSJK"
                        ,"301","40501",fileTypeIdList,fileNameList,fileList);

                //控制性详细规划调整地块项目区矢量层
                Kg_lsrks(frmAttachmentDB,"ND301_AthKZXXXGHDZDKXMQSLC"
                        ,"301","40601",fileTypeIdList,fileNameList,fileList);

            }
            //如果是技术修正
            if(rpt.GetValByKey("KGRKLX").toString().equals("5")) {
                //技术修正地块项目区矢量层
                Kg_lsrks(frmAttachmentDB,"ND301_AthJSXZDKXMQSLC"
                        ,"301","50601",fileTypeIdList,fileNameList,fileList);

                //技术修正地块分析矢量层
                Kg_lsrks(frmAttachmentDB,"ND301_AthJSXZDKFXSLC"
                        ,"301","50401",fileTypeIdList,fileNameList,fileList);

                //技术修正成果矢量数据库
                Kg_lsrks(frmAttachmentDB,"ND301_AthJSXZCGSLSJK"
                        ,"301","50501",fileTypeIdList,fileNameList,fileList);

            }
            //如果是新增
            if(rpt.GetValByKey("KGRKLX").toString().equals("1")) {
                //控制性详细规划成果矢量数据库
                Kg_lsrks(frmAttachmentDB,"ND301_AthKZXXXGHCGSLSJK"
                        ,"301","20102",fileTypeIdList,fileNameList,fileList);
            }
            //如果是局部新增
            if(rpt.GetValByKey("KGRKLX").toString().equals("6")) {
                //控制性详细规划调整成果矢量数据库
                Kg_lsrks(frmAttachmentDB,"ND301_AthKZXXXGHDZCGSLSJK"
                        ,"301","140501",fileTypeIdList,fileNameList,fileList);
            }
        }
        return isAddZJBG;
    }
    private boolean Kg_lsrks(FrmAttachmentDB frmAttachmentDB,String FK_FrmAttachment,String nodID,String idType,List<String> fileTypeIdList,List<String> fileNameList,List<String> fileList){
        boolean isChanged=false;
        if (frmAttachmentDB.getFKFrmAttachment().equals(FK_FrmAttachment)) {
            if(frmAttachmentDB.getNodeID()==Integer.parseInt(nodID)
                    &&this.getNodeID()!=Integer.parseInt(nodID))
                isChanged=true;
            fileTypeIdList.add(idType);
            File file=new File(frmAttachmentDB.getFileFullName());
            fileNameList.add(file.getName());
            // 将文件转换为Base64编码的字符串
            String base64String = encodeFileToBase64(new File(frmAttachmentDB.getFileFullName()));
            fileList.add(base64String);
        }
        return isChanged;
    }
    /**
     * 控规数据质检
     */
    public void Kg_Checked(GenerWorkFlow gwf){
        List<String> fileList=new ArrayList<>();
        List<String> fileNameList=new ArrayList<>();
        List<String> fileTypeIdList = new ArrayList<>();
        try {
            GERpt rpt = new GERpt("ND3Rpt");
            rpt.Retrieve(GERptAttr.OID,gwf.getWorkID());
            Boolean isAddZJBG=KG_DATA(gwf,rpt,fileList,fileNameList,fileTypeIdList);
            //没有质检报告，才能数据质检；开始节点不判断
            if(!isAddZJBG) {
                HashMap<String, Object> hashMap = new HashMap<>();
                //行政区代码
                hashMap.put("XZQDM", empStr(rpt.GetValByKey("SQL_SX")));
                //行政区名称
                hashMap.put("XZQMC", rpt.GetValByKey("SQL_SX") + "+" + rpt.GetValByKey("SZXZ") + "+" + rpt.GetValByKey("SZXZT"));
//                hashMap.put("XZQMC", empStr(rpt.GetValByKey("SQL_SX")));
                //方案名称
                hashMap.put("FAMC", empStr(rpt.GetValByKey("GHFAMC")));
                //备案号
                hashMap.put("BAH", rpt.GetValByKey("BillNo"));
                //申请单位
                hashMap.put("SQDW", empStr(rpt.GetValByKey("SQDW")));
                //经办人
                hashMap.put("JBR", empStr(rpt.GetValByKey("JBR")));
                //编制主体
                hashMap.put("BZZT", empStr(rpt.GetValByKey("ZZBZZT")));
                //编制单位
                hashMap.put("BZJSDW", empStr(rpt.GetValByKey("BZJSDW")));
                //批复单位
                hashMap.put("PFDW", empStr(rpt.GetValByKey("PFDW")));
                //批文号
                hashMap.put("PWH", empStr(rpt.GetValByKey("PWH")));
                //批复时间
                hashMap.put("PFSJ", empStr(rpt.GetValByKey("PFSJ")));
                //入库类型
                SysEnums enums = new SysEnums();
                enums.Retrieve(SysEnumAttr.EnumKey, "KGRKLX");
                for (SysEnum sysEnum : enums.ToJavaList()) {
                    if (sysEnum.getIntKey() == Integer.parseInt(rpt.GetValByKey("KGRKLX").toString()))
                        hashMap.put("BALX", sysEnum.getLab());
                }
                //所在片区
                hashMap.put("SZPQ", empStr(rpt.GetValByKey("SZPQT")));
                //是否批复成片开发方案
                enums = new SysEnums();
                enums.Retrieve(SysEnumAttr.EnumKey, "SFSJCPKF");
                for (SysEnum sysEnum : enums.ToJavaList()) {
                    if (sysEnum.getIntKey() == Integer.parseInt(rpt.GetValByKey("SFSJCPKF").toString()))
                        hashMap.put("SFPFCPKFFA", sysEnum.getLab());
                }
                //规划分类标准
                if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 0)
                    hashMap.put("GHYDFLBZ", "1990");
                if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 1)
                    hashMap.put("GHYDFLBZ", "镇规划标准2007");
                if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 2)
                    hashMap.put("GHYDFLBZ", "2011");
                if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 3)
                    hashMap.put("GHYDFLBZ", "用地用海分类");
                if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 4)
                    hashMap.put("GHYDFLBZ", "用地用海分类");

                //入库成果类型
                enums = new SysEnums();
                enums.Retrieve(SysEnumAttr.EnumKey, "KGRKLX");
                for (SysEnum sysEnum : enums.ToJavaList()) {
                    if (sysEnum.getIntKey() == Integer.parseInt(rpt.GetValByKey("KGRKLX").toString()))
                        hashMap.put("RKTYPE", sysEnum.getLab());
                }
                //附件类型（base64/file）
                hashMap.put("DATATYPE", "base64");
                //所处阶段
                hashMap.put("PERIOD", "3");
                //文件类型ID列表
                hashMap.put("FileTypeIdList", fileTypeIdList);
                //附件列表
                hashMap.put("FileList", fileList);
                //附件名称列表
                hashMap.put("FILENAMELIST", fileNameList);

                Map<String, String> map = new HashMap<>();
                map.put("json", bp.tools.Json.ToJson(hashMap));
                bp.da.Log.DefaultLogWriteLine(LogType.Info, "控规json：" + bp.tools.Json.ToJson(hashMap));
                //测试地址
                String data = HttpClientUtil.doPost(kg_sjzj_url, map, null, null);
                bp.da.Log.DefaultLogWriteLine(LogType.Info, "控规数据质检请求结果：" + data);
                JSONObject jd = JSONObject.parseObject(data);
                if (jd.get("code").toString().equals("1")) {
                    JSONArray results = JSONArray.parseArray(jd.get("results").toString());
                    //如果是重大调整（地块调整）
                    if (rpt.GetValByKey("KGRKLX").toString().equals("3")) {
                        //如果是入库阶段提交
                        if (rpt.GetValByKey("JD").toString().equals("2")) {
                            if (JSONObject.parseObject(results.get(0).toString()).get("code").toString().equals("1")
                                    && JSONObject.parseObject(results.get(1).toString()).get("code").toString().equals("1")
                                    && JSONObject.parseObject(results.get(2).toString()).get("code").toString().equals("1")) {
                                Kg_Reports(results,rpt, fileList, fileNameList, fileTypeIdList,gwf);
                            }
                        } else {
                            Kg_Report(results,rpt, fileList, fileNameList, fileTypeIdList,gwf);
                        }
                    }
                    //如果是重大调整（整体更新）
                    if (rpt.GetValByKey("KGRKLX").toString().equals("2")) {
                        if (rpt.GetValByKey("JD").toString().equals("2")) {
                            if (JSONObject.parseObject(results.get(0).toString()).get("code").toString().equals("1")
                                    && JSONObject.parseObject(results.get(1).toString()).get("code").toString().equals("1")) {
                                Kg_Reports(results,rpt, fileList, fileNameList, fileTypeIdList,gwf);
                            }
                        } else {
                            //控制性详细规划成果矢量数据库
                            Kg_Report(results,rpt, fileList, fileNameList, fileTypeIdList,gwf);
                        }
                    }
                    //如果是一般调整
                    if (rpt.GetValByKey("KGRKLX").toString().equals("4")) {
                        //控制性详细规划调整地块分析矢量层
                        //控制性详细规划调整成果矢量数据库
                        //控制性详细规划调整地块项目区矢量层
                        if (JSONObject.parseObject(results.get(0).toString()).get("code").toString().equals("1")
                                && JSONObject.parseObject(results.get(1).toString()).get("code").toString().equals("1")
                                && JSONObject.parseObject(results.get(2).toString()).get("code").toString().equals("1")) {
                            Kg_Reports(results,rpt, fileList, fileNameList, fileTypeIdList,gwf);
                        }
                    }
                    //如果是技术修正
                    if (rpt.GetValByKey("KGRKLX").toString().equals("5")) {
                        //技术修正地块项目区矢量层
                        //技术修正地块分析矢量层
                        //技术修正成果矢量数据库
                        if (JSONObject.parseObject(results.get(0).toString()).get("code").toString().equals("1")
                                && JSONObject.parseObject(results.get(1).toString()).get("code").toString().equals("1")
                                && JSONObject.parseObject(results.get(2).toString()).get("code").toString().equals("1")) {
                            Kg_Reports(results,rpt, fileList, fileNameList, fileTypeIdList,gwf);
                        }

                    }
                    //如果是新增
                    if (rpt.GetValByKey("KGRKLX").toString().equals("1")) {
                        //控制性详细规划成果矢量数据库
                        Kg_Report(results,rpt, fileList, fileNameList, fileTypeIdList,gwf);
                    }
                    //如果是局部新增
                    if (rpt.GetValByKey("KGRKLX").toString().equals("6")) {
                        //控制性详细规划调整成果矢量数据库
                        Kg_Report(results,rpt, fileList, fileNameList, fileTypeIdList,gwf);
                    }
                } else {
                    //执行失败
                    this.SucessInfo = "err@控规数据质检不通过，" + jd.get("msg").toString();
                }
            }
            else{
                this.SucessInfo = "@控规数据质检通过";
            }
        }
        catch (Exception ex){

        }
    }
    private void Kg_Reports(JSONArray results,GERpt rpt,List<String> fileList,List<String> fileNameList,List<String> fileTypeIdList,GenerWorkFlow gwf){
//质检成功
        JSONObject importJD = KG_LSRK(rpt, fileList, fileNameList, fileTypeIdList);
        //如果执行成功，将质检报告自动上传
        if (importJD.get("code").toString().equals("1")) {
            for (int i = 0; i < results.size(); i++) {
                String result = results.get(i).toString();
                JSONObject res = JSONObject.parseObject(result);
                String myfilePath = SystemConfig.getPathOfTemp() + DBAccess.GenerGUID() + ".pdf";
                try {
                    this.SucessInfo += res.get("msg").toString() + "@<a href='" + res.get("reportUrl").toString() + "'>质检报告</a>"
                            + "@" + importJD.get("msg").toString() + "@";
                    //先删除质检报告
                    FrmAttachmentDBs aths = new FrmAttachmentDBs();
                    aths.Retrieve(FrmAttachmentDBAttr.RefPKVal, gwf.getWorkID(), FrmAttachmentDBAttr.NoOfObj, "AthZJBG");
                    aths.Delete();
                    //自动上传质检报告
                    bp.da.DataType.HttpDownloadFile(handleUrl(res.get("reportUrl").toString()), myfilePath);
                    File file = new File(myfilePath);
                    bp.wf.Dev2Interface.CCForm_AddAth(gwf.getNodeID(), gwf.getFlowNo(), gwf.getWorkID(), "ND"+gwf.getNodeID()+"_AthZJBG", "ND"+gwf.getNodeID(), myfilePath, file.getName(),"",res.get("mdbFileId").toString(),gwf.getFID(),gwf.getPWorkID());
                } catch (Exception ex) {
                    bp.da.Log.DefaultLogWriteLine(LogType.Info, "控规数据临时入库异常：" + ex.getMessage());
                    //如果失败的话，让用户手动下载并上传
                    this.SucessInfo = "err@" + "@请手动下载质检报告并上传:" + "@<a href='" + res.get("reportUrl").toString() + "'>质检报告</a>";
                }
            }
        } else {
            this.SucessInfo = "err@" + importJD.get("msg").toString();
        }
    }
    private void Kg_Report(JSONArray results,GERpt rpt,List<String> fileList,List<String> fileNameList,List<String> fileTypeIdList,GenerWorkFlow gwf){
        String result = results.get(0).toString();
        JSONObject res = JSONObject.parseObject(result);
        if (res.get("code").toString().equals("1")) {
            //质检成功
            JSONObject importJD = KG_LSRK(rpt, fileList, fileNameList, fileTypeIdList);
            //如果执行成功，将质检报告自动上传
            if (importJD.get("code").toString().equals("1")) {

                this.SucessInfo = res.get("msg").toString() + "@<a href='" + res.get("reportUrl").toString() + "'>质检报告</a>"
                        + "@" + importJD.get("msg").toString();

                String myfilePath = SystemConfig.getPathOfTemp() + DBAccess.GenerGUID() + ".pdf";
                try {
                    //先删除质检报告
                    FrmAttachmentDBs aths = new FrmAttachmentDBs();
                    aths.Retrieve(FrmAttachmentDBAttr.RefPKVal, gwf.getWorkID(), FrmAttachmentDBAttr.NoOfObj, "AthZJBG");
                    aths.Delete();
                    //自动上传质检报告
                    bp.da.DataType.HttpDownloadFile(handleUrl(res.get("reportUrl").toString()), myfilePath);
                    File file = new File(myfilePath);
                    bp.wf.Dev2Interface.CCForm_AddAth(gwf.getNodeID(), gwf.getFlowNo(), gwf.getWorkID(), "ND"+gwf.getNodeID()+"_AthZJBG", "ND"+gwf.getNodeID(), myfilePath, file.getName(),"",res.get("mdbFileId").toString(),gwf.getFID(),gwf.getPWorkID());
                } catch (Exception ex) {
                    bp.da.Log.DefaultLogWriteLine(LogType.Info, "控规数据临时入库异常：" + ex.getMessage());
                    //如果失败的话，让用户手动下载并上传
                    this.SucessInfo = "err@" + "@请手动下载质检报告并上传:" + "@<a href='" + res.get("reportUrl").toString() + "'>质检报告</a>";
                }
            } else {
                this.SucessInfo = "err@" + importJD.get("msg").toString();
            }
        } else {
            //质检不通过
            this.SucessInfo = "err@" + res.get("msg").toString() + "@<a href='" + res.get("reportUrl").toString() + "'>质检报告</a>";
        }
    }
    /**
     * 控规临时入库
     * @param rpt
     * @return
     */
    public static JSONObject KG_LSRK(GERpt rpt,List<String> fileList,
                                     List<String> fileNameList,
                                     List<String> fileTypeIdList){
        //执行成功后，数据入临时库
        Map<String, String> importJsonMap = new HashMap<>();
        //备案号
        importJsonMap.put("BAH", rpt.GetValByKey("BillNo").toString());
        //规划类型
        importJsonMap.put("PlanType", "KG");
        //数管阶段状态
        importJsonMap.put("sgstage", String.valueOf(Integer.parseInt(rpt.GetValByKey("JD").toString())+1));
        //规划分类标准
        if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 0)
            importJsonMap.put("GHFLBZ", "1990");
        if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 1)
            importJsonMap.put("GHFLBZ", "镇规划标准2007");
        if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 2)
            importJsonMap.put("GHFLBZ", "2011");
        if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 3)
            importJsonMap.put("GHFLBZ", "用地用海分类");
        if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 4)
            importJsonMap.put("GHFLBZ", "用地用海分类");

        //调整类型
        String KGRKLX=rpt.GetValByKey("KGRKLX").toString();
        if(KGRKLX.equals("1")){ //新增
            importJsonMap.put("BALX_INDEX", "2");
        }else if(KGRKLX.equals("2")){ //整体更新
            importJsonMap.put("BALX_INDEX", "15");
        }else if(KGRKLX.equals("3")){ //重大调整
            importJsonMap.put("BALX_INDEX", "3");
        }else if(KGRKLX.equals("4")){ //一般调整
            importJsonMap.put("BALX_INDEX", "4");
        }else if(KGRKLX.equals("5")){ //技术修正
            importJsonMap.put("BALX_INDEX", "5");
        }else if(KGRKLX.equals("6")){ //局部新增
            importJsonMap.put("BALX_INDEX", "14");
        }

        //片区名称
        importJsonMap.put("pqmc", rpt.GetValByKey("SZPQT").toString());

        Map<String, String> importMap = new HashMap<>();
        importMap.put("json", bp.tools.Json.ToJson(importJsonMap));
        importMap.put("filetype",  bp.tools.Json.ToJson(fileTypeIdList));
        importMap.put("filebase",  bp.tools.Json.ToJson(fileList));
        importMap.put("filename",  bp.tools.Json.ToJson(fileNameList));

        //入临时库之前，先删除原有的数据，避免重复
        String deleteDataUrl=gk_rk_test_del_url;
        Map<String, String> deleteMap = new HashMap<>();
        deleteMap.put("bah", rpt.GetValByKey("BillNo").toString());
        deleteMap.put("plantype", "KG");
        deleteMap.put("sgstage", String.valueOf(Integer.parseInt(rpt.GetValByKey("JD").toString())+1));
        String deleteData = HttpClientUtil.doPost(deleteDataUrl, deleteMap, null, null);
        bp.da.Log.DefaultLogWriteLine(LogType.Info, "删除控规数据临时入库参数："+ bp.tools.Json.ToJson(deleteMap));
        bp.da.Log.DefaultLogWriteLine(LogType.Info, "删除控规数据临时入库返回结果："+ deleteData);


        bp.da.Log.DefaultLogWriteLine(LogType.Info, "控规数据临时入库请求json："+ bp.tools.Json.ToJson(importMap));

        //入临时库
        String importData = HttpClientUtil.doPost(gk_rk_test_url, importMap, null, null);
        bp.da.Log.DefaultLogWriteLine(LogType.Info, "控规数据临时入库返回结果："+ importData);
        JSONObject importJD = JSONObject.parseObject(importData);
        return importJD;
    }
    /**
     * 控规正式入库
     * @param gwf
     * @return
     */
    public void KG_ZSRK(GenerWorkFlow gwf){
        try {
            List<String> fileList=new ArrayList<>();
            List<String> fileNameList=new ArrayList<>();
            List<String> fileTypeIdList = new ArrayList<>();
            GERpt rpt = new GERpt("ND3Rpt");
            rpt.Retrieve(GERptAttr.OID,gwf.getWorkID());
            KG_DATA(gwf,rpt,fileList,fileNameList,fileTypeIdList);
            Map<String, String> importJsonMap = new HashMap<>();
            //备案号
            importJsonMap.put("BAH", rpt.GetValByKey("BillNo").toString());
            //入库号
            importJsonMap.put("RKH", rpt.GetValByKey("BillNo").toString());

            //规划类型
            importJsonMap.put("PlanType", "KG");
            //数管阶段状态
            importJsonMap.put("sgstage", String.valueOf(Integer.parseInt(rpt.GetValByKey("JD").toString()) + 1));
            //规划分类标准
            if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 0)
                importJsonMap.put("GHFLBZ", "1990");
            if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 1)
                importJsonMap.put("GHFLBZ", "镇规划标准2007");
            if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 2)
                importJsonMap.put("GHFLBZ", "2011");
            if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 3)
                importJsonMap.put("GHFLBZ", "用地用海分类");
            if (Integer.parseInt(rpt.GetValByKey("GHFLBZ").toString()) == 4)
                importJsonMap.put("GHFLBZ", "用地用海分类");

            //调整类型
            String KGRKLX=rpt.GetValByKey("KGRKLX").toString();
            if(KGRKLX.equals("1")){ //新增
                importJsonMap.put("BALX_INDEX", "2");
            }else if(KGRKLX.equals("2")){ //整体更新
                importJsonMap.put("BALX_INDEX", "15");
            }else if(KGRKLX.equals("3")){ //重大调整
                importJsonMap.put("BALX_INDEX", "3");
            }else if(KGRKLX.equals("4")){ //一般调整
                importJsonMap.put("BALX_INDEX", "4");
            }else if(KGRKLX.equals("5")){ //技术修正
                importJsonMap.put("BALX_INDEX", "5");
            }else if(KGRKLX.equals("6")){ //局部新增
                importJsonMap.put("BALX_INDEX", "14");
            }


            //片区名称
            importJsonMap.put("pqmc", rpt.GetValByKey("SZPQT").toString());

            Map<String, String> importMap = new HashMap<>();
            importMap.put("json", bp.tools.Json.ToJson(importJsonMap));
            importMap.put("filetype",  bp.tools.Json.ToJson(fileTypeIdList));
            importMap.put("filebase",  bp.tools.Json.ToJson(fileList));
            importMap.put("filename",  bp.tools.Json.ToJson(fileNameList));

            bp.da.Log.DefaultLogWriteLine(LogType.Info, "控规数据正式入库请求json：" + bp.tools.Json.ToJson(importMap));

            //入正式库
            String importData = HttpClientUtil.doPost(gk_rk_url, importMap, null, null);
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "控规数据正式入库返回结果：" + importData);
            JSONObject importJD = JSONObject.parseObject(importData);
            if(importJD.get("code").toString().equals("1"))
                this.SucessInfo="入库成功";
        }
        catch (Exception ex){
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "控规数据正式入库异常：" + ex.getMessage());
            this.SucessInfo="err@入库失败，请联系管理员";
        }
    }
    public static String encodeFileToBase64(File file) {
        try{
            FileInputStream fis = new FileInputStream(file);
            byte[] fileBytes = IOUtils.toByteArray(fis);
            String base64EncodedString = org.apache.commons.codec.binary.Base64.encodeBase64String(fileBytes);
            //decodeBase64ToFile(base64EncodedString, "D:/3.3.5.12.1/file.mdb");
            return base64EncodedString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void decodeBase64ToFile(String base64String, String filePath) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        Files.write(new File(filePath).toPath(), decodedBytes);
    }
    private String empStr(Object obj){
        if(obj==null || obj.toString().equals("")){
            return "-";
        }else{
            return obj.toString();
        }
    }

    public static String handleUrl(String url) throws UnsupportedEncodingException {
        int a = 0;
        if(url.contains("专")){
            a = url.indexOf("专");
        }else if(url.contains("村")){
            a = url.indexOf("村");
        }else if(url.contains("控")){
            a = url.indexOf("控");
        }
        String newUrl = url.substring(0,a)+URLEncoder.encode(url.substring(a),"utf-8");
        return newUrl;
    }

//    public static void main(String[] args) throws Exception {
////        String fileName = "控规质检_个个都是_KG_CJXDKDZ202410290002_8c5bdd15-92b7-480b-911d-f1b1b1528104.pdf";
//        String url="http://59.212.37.75:88/tool/pdfReport/控规质检_个个都是_KG_CJXDKDZ202410290002_8c5bdd15-92b7-480b-911d-f1b1b1528104.pdf";
//        url = handleUrl(url);
//        String myfilePath="D:\\Download\\a.pdf";
//        bp.da.DataType.HttpDownloadFile(url, myfilePath);
//    }
}
