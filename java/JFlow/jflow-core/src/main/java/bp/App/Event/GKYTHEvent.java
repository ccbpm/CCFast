package bp.App.Event;

import bp.da.DataTable;
import bp.da.DataType;
import bp.da.LogType;
import bp.da.Paras;
import bp.difference.SystemConfig;
import bp.sys.*;
import bp.tools.HttpClientUtil;
import bp.wf.GenerWorkFlow;
import bp.wf.Track;
import bp.wf.Tracks;
import bp.wf.WorkCheck;
import bp.wf.data.GERpt;
import bp.wf.data.GERptAttr;
import bp.wf.template.FrmWorkChecks;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GKYTHEvent extends bp.sys.base.EventBase{
    //省一体化
    //"http://172.25.116.26:9009"  测试
    //"http://172.25.116.26:8087"  正式
    private static String yt_sheng_url="http://172.25.116.26:9009";
    //市县一体化
    //http://172.25.116.26:10891 测试
    //http://172.25.116.26:10899  正式
    private static String yt_sx_url="http://172.25.116.26:10891";

    //是否测试环境
    private static boolean isTest=true;
    private void urlExt() {
        if (isTest) {
            return;
        }
        //省一体化
        yt_sheng_url="http://172.25.116.26:8087";
        //市县一体化
        yt_sx_url="http://172.25.116.26:10899";
    }

    public GKYTHEvent() throws Exception{
        this.setTitle("一体化数据推送事件");
    }

    @Override
    public void Do() {
        urlExt();
        try {
            GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
            this.SucessInfo ="";
            switch (this.getEventSource()) {
                case EventListNode.SendSuccess://发送成功后
//                    DoEventBase(gwf);
                    break;
                case EventListNode.UndoneAfter://撤销发送之后
                    break;
                case EventListNode.SendWhen://发送前
//                    DoEventBase(gwf);
                    break;
                case EventListNode.ReturnAfter://退回后
                    break;
                case EventListFlow.FlowOverAfter://流程结束后
                    break;
            }
        }
        catch (Exception ex){
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "一体化推送开始异常："+ ex.getMessage());
            this.SucessInfo="err@@获取工作失败";
        }
    }

    /**
     * 获取一体化平台token
     * @return
     */
    public String getYTHToken(GERpt rpt){
        try{
            //测试路径
            String url=yt_sheng_url+"/kgsp/getToken/accept";
            //如果是县级
            if(rpt.GetValByKey("SFSSJ").toString().equals("0")){
                url=yt_sx_url+"/kgsp/getToken/accept";
            }
            String data=bp.tools.HttpClientUtil.doPost(url,new HashMap<>(),"operateType","accept");
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "一体化token："+ data);
            JSONObject jsonObject= JSON.parseObject(data);
            String token=jsonObject.getString("data");
            return token;
        }catch (Exception ex){
            return "";
        }
    }
    /**
     * 事件
     */
    private void DoEventBase(GenerWorkFlow gwf){
        try{
            switch (gwf.getFlowNo()) {
                case "001"://村规数据推送至一体化平台
                    Cg_DataPost(gwf);
                    break;
                case "003"://控规数据推送至一体化平台
                    Kg_DataPost(gwf);
                    break;
            }
        }
        catch (Exception ex){

        }
    }
    /**
     * 村规数据推送
     */
    public void Cg_DataPost(GenerWorkFlow gwf){
        List<HashMap<String,Object>> fileList=new ArrayList<>();
        try {
            GERpt rpt = new GERpt("ND1Rpt");
            rpt.Retrieve(GERptAttr.OID,gwf.getWorkID());

            //获取token
            String token=getYTHToken(rpt);
            //获取附件
            FrmAttachments frmAttachments=new FrmAttachments();
            frmAttachments.Retrieve(FrmAttachmentAttr.FK_MapData,"ND"+gwf.getNodeID());

            FrmAttachmentDBs dbs=new FrmAttachmentDBs();
            dbs.Retrieve(FrmAttachmentDBAttr.RefPKVal,gwf.getWorkID());
            for (FrmAttachmentDB frmAttachmentDB: dbs.ToJavaList()){
                //新增
                if(rpt.GetValByKey("RKLX").equals("1")){
                    HashMap<String,Object> athFile=new HashMap<>();
                    //附件名称
                    athFile.put("fileName",frmAttachmentDB.getFileName());
                    //附件下载路径
                    athFile.put("filePath",frmAttachmentDB.getFilePathName());
                    //附件大小
                    athFile.put("fileSize",frmAttachmentDB.getFileSize());
                    //附件格式
                    athFile.put("fileFormat",frmAttachmentDB.getFileExts());
                    //附件处理
                    if(frmAttachmentDB.getNoOfObj().equals("AthZJBG")){
                        //id
                        athFile.put("subDirId",this.getWorkID());
                        //目录id
                        athFile.put("dirId","70106");
                        //目录名称
                        athFile.put("dirName","村庄规划成果矢量数据库");
                        //机器质检结论
                        athFile.put("jqzjyj","机器质检结论");
                        //机器质检时间
                        athFile.put("jqzjsj",frmAttachmentDB.getRDT());
                        //附件key
                        athFile.put("fieldKey","jqzjyj");
                    }
                    else if(frmAttachmentDB.getFKFrmAttachment().equals("ND101_ACZGHC")){
                        athFile.put("subDirId", this.getWorkID());
                        //目录id
                        athFile.put("dirId","70106");
                        //目录名称
                        athFile.put("dirName","村庄规划成果矢量数据库");
                    }
                    else{
                        for (FrmAttachment frmAttachment:frmAttachments.ToJavaList()) {
                            if(frmAttachment.getNoOfObj().equals(frmAttachmentDB.getNoOfObj())){
                                //目录id
                                athFile.put("dirId", frmAttachment.getMyPK());
                                //目录名称
                                athFile.put("dirName", frmAttachment.getNoOfObj());
                            }
                        }
                    }
                    //创建时间
                    athFile.put("createTime",frmAttachmentDB.getFileName());
                    //创建人
                    athFile.put("createBy",frmAttachmentDB.getFileName());
                    fileList.add(athFile);
                }
                //局部新增
                if(rpt.GetValByKey("RKLX").equals("4")){
                    HashMap<String,Object> athFile=new HashMap<>();
                    //附件名称
                    athFile.put("fileName",frmAttachmentDB.getFileName());
                    //附件下载路径
                    athFile.put("filePath",frmAttachmentDB.getFilePathName());
                    //附件大小
                    athFile.put("fileSize",frmAttachmentDB.getFileSize());
                    //附件格式
                    athFile.put("fileFormat",frmAttachmentDB.getFileExts());
                    //附件处理
                    if(frmAttachmentDB.getNoOfObj().equals("AthZJBG")){
                        //id
                        athFile.put("subDirId",this.getWorkID());
                        //目录id
                        athFile.put("dirId","170106");
                        //目录名称
                        athFile.put("dirName","村庄规划成果矢量数据库");
                        //机器质检结论
                        athFile.put("jqzjyj","机器质检结论");
                        //机器质检时间
                        athFile.put("jqzjsj",frmAttachmentDB.getRDT());
                        //附件key
                        athFile.put("fieldKey","jqzjyj");
                    }
                    else if(frmAttachmentDB.getFKFrmAttachment().equals("ND101_ACZGHC")){
                        athFile.put("subDirId", this.getWorkID());
                        //目录id
                        athFile.put("dirId","170106");
                        //目录名称
                        athFile.put("dirName","村庄规划成果矢量数据库");
                    }
                    else{
                        for (FrmAttachment frmAttachment:frmAttachments.ToJavaList()) {
                            if(frmAttachment.getNoOfObj().equals(frmAttachmentDB.getNoOfObj())){
                                //目录id
                                athFile.put("dirId", frmAttachment.getMyPK());
                                //目录名称
                                athFile.put("dirName", frmAttachment.getNoOfObj());
                            }
                        }
                    }
                    //创建时间
                    athFile.put("createTime",frmAttachmentDB.getFileName());
                    //创建人
                    athFile.put("createBy",frmAttachmentDB.getFileName());
                    fileList.add(athFile);
                }
            }

            HashMap<String,Object> jsonData=new HashMap<>();
            //备案类型
            SysEnums enums=new SysEnums();
            enums.Retrieve(SysEnumAttr.EnumKey,"RKLX");
            for (SysEnum sysEnum:enums.ToJavaList()){
                if(sysEnum.getIntKey()==Integer.parseInt(rpt.GetValByKey("RKLX").toString()))
                    jsonData.put("balx",sysEnum.getLab());
            }
            //所在市县
            jsonData.put("szsx",rpt.GetValByKey("SZSX"));
            //所在乡镇
            jsonData.put("szxz",rpt.GetValByKey("SZXZT"));
            //方案名称
            jsonData.put("famc",rpt.GetValByKey("FAMC"));
            //行政村名称
            jsonData.put("xzcmc",rpt.GetValByKey("XZCMCT"));
            //村级行政区代码
            jsonData.put("cjxzqdm",rpt.GetValByKey("CJXZCDM"));
            //编制主体
            jsonData.put("zzbzzt",rpt.GetValByKey("ZZBZZT"));
            //编制单位
            jsonData.put("bzjsdw",rpt.GetValByKey("BZJSDW"));
            //批复文件
            jsonData.put("pfwj",rpt.GetValByKey("PFWJ"));
            //批复单位
            jsonData.put("pfdw",rpt.GetValByKey("PFDW"));
            //批复文件时间
            jsonData.put("pfsj",rpt.GetValByKey("PFSJ"));
            //批文号
            jsonData.put("pwh",rpt.GetValByKey("PWH"));
            //经办人
            jsonData.put("jbr",rpt.GetValByKey("JBR"));
            //联系电话
            jsonData.put("lxdh",rpt.GetValByKey("LXDH"));
            //电子邮箱
            jsonData.put("dzyx",rpt.GetValByKey("DZYX"));
            //申请单位
            jsonData.put("sqdw",rpt.GetValByKey("SQDept"));
            //耕地现状数据
            jsonData.put("gdxzsj",rpt.GetValByKey("GDBYLXZZB"));
            //耕地规划数据
            jsonData.put("gdghzb",rpt.GetValByKey("GDBYLGHZB"));
            //林地现状数据
            jsonData.put("ldxzsj",rpt.GetValByKey("LDBYLXZZB"));
            //林地规划数据
            jsonData.put("ldghzb",rpt.GetValByKey("LDBYLGHZB"));
            //建设用地现状数据
            jsonData.put("jsydxzsj",rpt.GetValByKey("JSYDZGMXZZB"));
            //建设用地规划指标
            jsonData.put("jsydghzb",rpt.GetValByKey("JSYDZGMGHZB"));
            //项目概况
            jsonData.put("xmgk",rpt.GetValByKey("XMGK"));
            //数据坐标系
            enums=new SysEnums();
            enums.Retrieve(SysEnumAttr.EnumKey,"SJZBX");
            for (SysEnum sysEnum:enums.ToJavaList()){
                if(sysEnum.getIntKey()==Integer.parseInt(rpt.GetValByKey("SJZBX").toString()))
                    jsonData.put("sjzbx",sysEnum.getLab());
            }

            //村庄名录
            jsonData.put("czml",rpt.GetValByKey("SQL_CZMLT"));

            //processInfo
            HashMap<String,Object> hashMap=new HashMap<>();
            //表单数据
            hashMap.put("jsonData",jsonData);
            //附件列表
            hashMap.put("fileInfoList",fileList);

            //推送的整体数据
            HashMap<String,Object> map=new HashMap<>();
            map.put("type","CZ");
            map.put("operateType","accept");
            map.put("bah",rpt.GetValByKey("BillNo"));
            map.put("processInfo",hashMap);

            Map<String,String> headerMap=new HashMap<>();
            headerMap.put("token",token);

            String postUrl="";
            //县级推送一体化测试接口,省正式8087,测试9009,市县正式10899,测试10891
            if(rpt.GetValByKey("SFSSJ").toString().equals("0")){
                postUrl=yt_sx_url+"/kgsp/handleProcessAndFiles";
            }
            else{
                postUrl=yt_sheng_url+"/kgsp/handleProcessAndFiles";
            }

            bp.da.Log.DefaultLogWriteLine(LogType.Info, "村规数据一体化推送json："+ bp.tools.Json.ToJson(map));

            String data= HttpClientUtil.doPost(postUrl,map.toString(),headerMap);
            JSONObject jd=JSONObject.parseObject(data);
            if(Boolean.parseBoolean(jd.get("success").toString())){
                bp.da.Log.DefaultLogWriteLine(LogType.Info, "村规数据一体化推送成功："+ map.toString());
                //执行成功
                this.SucessInfo="推送成功";
            }
            else{
                //执行失败
                bp.da.Log.DefaultLogWriteLine(LogType.Info, "村规数据一体化推送失败："+ data);
                this.SucessInfo="err@@村规数据一体化推送失败：@"+data;
            }
        }
        catch (Exception ex){
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "村规数据一体化推送异常："+ ex.getMessage());
            this.SucessInfo="err@@村规数据一体化推送异常,请联系管理员";
        }
    }
    /**
     * 控规数据推送
     */
    public void Kg_DataPost(GenerWorkFlow gwf){
        List<HashMap<String,Object>> fileList=new ArrayList<>();
        try {
            GERpt rpt = new GERpt("ND3Rpt");
            rpt.Retrieve(GERptAttr.OID,gwf.getWorkID());

            //备案类型
            String lx=rpt.GetValByKey("KGRKLX").toString();

            //审核意见
            String token=getYTHToken(rpt);

            WorkCheck workCheck=new WorkCheck("003",gwf.getNodeID(),gwf.getWorkID(),gwf.getFID(),0);
            Tracks tracks=workCheck.getHisWorkChecks();

            //获取附件组件
            FrmAttachments frmAttachments=new FrmAttachments();
            frmAttachments.Retrieve(FrmAttachmentAttr.FK_MapData,"ND"+gwf.getNodeID());

            FrmAttachmentDBs dbs=new FrmAttachmentDBs();
            dbs.Retrieve(FrmAttachmentDBAttr.RefPKVal,gwf.getWorkID());

            for (FrmAttachmentDB frmAttachmentDB: dbs.ToJavaList()){
                //局部新增，只有一个质检报告
                if(lx=="6") {
                    HashMap<String, Object> athFile = new HashMap<>();
                    //附件名称
                    athFile.put("fileName", frmAttachmentDB.getFileName());
                    //附件下载路径
                    athFile.put("filePath", frmAttachmentDB.getFilePathName());
                    //附件大小
                    athFile.put("fileSize", frmAttachmentDB.getFileSize());
                    //附件格式
                    athFile.put("fileFormat", frmAttachmentDB.getFileExts());

                    //附件处理
                    //质检报告
                    if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthZJBG")) {
                        //id
                        athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                        //目录id
                        athFile.put("dirId", "140501");
                        //目录名称
                        athFile.put("dirName", "控制性详细规划调整成果矢量数据库");
                        //机器质检结论
                        athFile.put("jqzjyj", "机器质检结论");
                        //机器质检时间
                        athFile.put("jqzjsj", frmAttachmentDB.getRDT());
                        //附件key
                        athFile.put("fieldKey", "jqzjyj");
                    }
                    else if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthKZXXXGHDZCGSLSJK")) {
                        //id
                        athFile.put("subDirId", frmAttachmentDB.getMyNote());
                        //目录id
                        athFile.put("dirId", "140501");
                        //目录名称
                        athFile.put("dirName", "控制性详细规划调整成果矢量数据库");
                    }
                    //技术审查意见
                    else if (frmAttachmentDB.getFKFrmAttachment().equals("ND310_FrmWorkCheck")) {
                        athFile=getJSSCYJ(athFile,tracks,frmAttachmentDB,310);
                    }//市级，图数一致性审查意见、压覆审核意见
                    else if (frmAttachmentDB.getFKFrmAttachment().equals("ND311_FrmWorkCheck")) {
                        athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,311,false);
                    }
                    //省级，图数一致性审查意见、压覆审核意见
                    else if (frmAttachmentDB.getFKFrmAttachment().equals("ND319_FrmWorkCheck")) {
                        athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,311,true);
                    }
                    else{
                        for (FrmAttachment frmAttachment:frmAttachments.ToJavaList()) {
                            if(frmAttachment.getNoOfObj().equals(frmAttachmentDB.getNoOfObj())){
                                //目录id
                                athFile.put("dirId", frmAttachment.getMyPK());
                                //目录名称
                                athFile.put("dirName", frmAttachment.getNoOfObj());
                            }
                        }
                    }
                    //创建时间
                    athFile.put("createTime", frmAttachmentDB.getRDT());
                    //创建人
                    athFile.put("createBy", frmAttachmentDB.getRecName());
                    fileList.add(athFile);
                }
                //新增，只有一个质检报告
                else if(lx=="1") {
                    HashMap<String, Object> athFile = new HashMap<>();
                    //附件名称
                    athFile.put("fileName", frmAttachmentDB.getFileName());
                    //附件下载路径
                    athFile.put("filePath", frmAttachmentDB.getFilePathName());
                    //附件大小
                    athFile.put("fileSize", frmAttachmentDB.getFileSize());
                    //附件格式
                    athFile.put("fileFormat", frmAttachmentDB.getFileExts());

                    //附件处理
                    //质检报告
                    if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthZJBG")) {
                        //id
                        athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                        //目录id
                        athFile.put("dirId", "20102");
                        //目录名称
                        athFile.put("dirName", "控制性详细规划成果矢量数据库");
                        //机器质检结论
                        athFile.put("jqzjyj", "机器质检结论");
                        //机器质检时间
                        athFile.put("jqzjsj", frmAttachmentDB.getRDT());
                        //附件key
                        athFile.put("fieldKey", "jqzjyj");
                    }
                    else if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthKZXXXGHCGSLSJK")) {
                        //id
                        athFile.put("subDirId", frmAttachmentDB.getMyNote());
                        //目录id
                        athFile.put("dirId", "20102");
                        //目录名称
                        athFile.put("dirName", "控制性详细规划成果矢量数据库");
                    }
                    //技术审查意见
                    else if (frmAttachmentDB.getFKFrmAttachment().equals("ND310_FrmWorkCheck")) {
                        athFile=getJSSCYJ(athFile,tracks,frmAttachmentDB,310);
                    }//图数一致性审查意见、压覆审核意见
                    else if (frmAttachmentDB.getFKFrmAttachment().equals("ND311_FrmWorkCheck")) {
                        athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,311,false);
                    }
                    //省级，图数一致性审查意见、压覆审核意见
                    else if (frmAttachmentDB.getFKFrmAttachment().equals("ND319_FrmWorkCheck")) {
                        athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,319,true);
                    }
                    else{
                        for (FrmAttachment frmAttachment:frmAttachments.ToJavaList()) {
                            if(frmAttachment.getNoOfObj().equals(frmAttachmentDB.getNoOfObj())){
                                //目录id
                                athFile.put("dirId", frmAttachment.getMyPK());
                                //目录名称
                                athFile.put("dirName", frmAttachment.getNoOfObj());
                            }
                        }
                    }
                    //创建时间
                    athFile.put("createTime", frmAttachmentDB.getRDT());
                    //创建人
                    athFile.put("createBy", frmAttachmentDB.getRecName());
                    fileList.add(athFile);
                }
                //整体更新
                else if(lx=="2") {
                    //启动申请阶段
                    if (rpt.GetValByKey("JD").equals("0")) {
                        HashMap<String, Object> athFile = new HashMap<>();
                        //附件名称
                        athFile.put("fileName", frmAttachmentDB.getFileName());
                        //附件下载路径
                        athFile.put("filePath", frmAttachmentDB.getFilePathName());
                        //附件大小
                        athFile.put("fileSize", frmAttachmentDB.getFileSize());
                        //附件格式
                        athFile.put("fileFormat", frmAttachmentDB.getFileExts());

                        //附件处理
                        //质检报告
                        if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthZJBG")) {
                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "150116");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                            //机器质检结论
                            athFile.put("jqzjyj", "机器质检结论");
                            //机器质检时间
                            athFile.put("jqzjsj", frmAttachmentDB.getRDT());
                            //附件key
                            athFile.put("fieldKey", "jqzjyj");
                        } else if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthKZXXXGHDZDKXMQSLC")) {
                            //id
                            athFile.put("subDirId", frmAttachmentDB.getMyNote());
                            //目录id
                            athFile.put("dirId", "150116");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                        }
                        //启动阶段技术审查意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND302_FrmWorkCheck")) {
                            athFile=getJSSCYJ(athFile,tracks,frmAttachmentDB,302);
                        }
                        else {
                            for (FrmAttachment frmAttachment : frmAttachments.ToJavaList()) {
                                if (frmAttachment.getNoOfObj().equals(frmAttachmentDB.getNoOfObj())) {
                                    //目录id
                                    athFile.put("dirId", frmAttachment.getMyPK());
                                    //目录名称
                                    athFile.put("dirName", frmAttachment.getNoOfObj());
                                }
                            }
                        }
                        //创建时间
                        athFile.put("createTime", frmAttachmentDB.getRDT());
                        //创建人
                        athFile.put("createBy", frmAttachmentDB.getRecName());
                        fileList.add(athFile);
                    }
                    //审查审批阶段
                    if (rpt.GetValByKey("JD").equals("1")) {
                        HashMap<String, Object> athFile = new HashMap<>();
                        //附件名称
                        athFile.put("fileName", frmAttachmentDB.getFileName());
                        //附件下载路径
                        athFile.put("filePath", frmAttachmentDB.getFilePathName());
                        //附件大小
                        athFile.put("fileSize", frmAttachmentDB.getFileSize());
                        //附件格式
                        athFile.put("fileFormat", frmAttachmentDB.getFileExts());

                        //附件处理
                        //质检报告
                        if (frmAttachmentDB.getFKFrmAttachment().equals("ND307_AthKZXXXGHDZDKXMQSLC")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND307_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "150116");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "150116");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                        }
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND307_AthKZXXXGHCGSLSJK")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND307_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "150102");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划成果矢量数据库");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "150102");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划成果矢量数据库");
                        }
                        //审查阶段技术审查意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND310_FrmWorkCheck")) {
                            athFile=getJSSCYJ(athFile,tracks,frmAttachmentDB,310);
                        }//图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND311_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,311,false);
                        }
                        //省级，图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND320_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,320,true);
                        }
                        else {
                            for (FrmAttachment frmAttachment : frmAttachments.ToJavaList()) {
                                if (frmAttachment.getNoOfObj().equals(frmAttachmentDB.getNoOfObj())) {
                                    //目录id
                                    athFile.put("dirId", frmAttachment.getMyPK());
                                    //目录名称
                                    athFile.put("dirName", frmAttachment.getNoOfObj());
                                }
                            }
                        }
                        //创建时间
                        athFile.put("createTime", frmAttachmentDB.getRDT());
                        //创建人
                        athFile.put("createBy", frmAttachmentDB.getRecName());
                        fileList.add(athFile);
                    }
                    //入库阶段
                    if (rpt.GetValByKey("JD").equals("2")) {
                        HashMap<String, Object> athFile = new HashMap<>();
                        //附件名称
                        athFile.put("fileName", frmAttachmentDB.getFileName());
                        //附件下载路径
                        athFile.put("filePath", frmAttachmentDB.getFilePathName());
                        //附件大小
                        athFile.put("fileSize", frmAttachmentDB.getFileSize());
                        //附件格式
                        athFile.put("fileFormat", frmAttachmentDB.getFileExts());

                        //附件处理
                        //质检报告
                        if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthKZXXXGHDZDKXMQSLC")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND301_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "150116");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "150116");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                        }
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthKZXXXGHCGSLSJK")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND301_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "150102");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划成果矢量数据库");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "150102");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划成果矢量数据库");
                        }
                        //入库阶段技术审查意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND310_FrmWorkCheck")) {
                            athFile=getJSSCYJ(athFile,tracks,frmAttachmentDB,310);
                        }//图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND311_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,311,false);
                        }
                        //省级，图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND319_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,319,true);
                        }
                        else {
                            for (FrmAttachment frmAttachment : frmAttachments.ToJavaList()) {
                                if (frmAttachment.getNoOfObj().equals(frmAttachmentDB.getNoOfObj())) {
                                    //目录id
                                    athFile.put("dirId", frmAttachment.getMyPK());
                                    //目录名称
                                    athFile.put("dirName", frmAttachment.getNoOfObj());
                                }
                            }
                        }
                        //创建时间
                        athFile.put("createTime", frmAttachmentDB.getRDT());
                        //创建人
                        athFile.put("createBy", frmAttachmentDB.getRecName());
                        fileList.add(athFile);
                    }
                }
                //地块调整
                else if(lx=="3") {
                    //启动申请阶段
                    if (rpt.GetValByKey("JD").equals("0")) {
                        HashMap<String, Object> athFile = new HashMap<>();
                        //附件名称
                        athFile.put("fileName", frmAttachmentDB.getFileName());
                        //附件下载路径
                        athFile.put("filePath", frmAttachmentDB.getFilePathName());
                        //附件大小
                        athFile.put("fileSize", frmAttachmentDB.getFileSize());
                        //附件格式
                        athFile.put("fileFormat", frmAttachmentDB.getFileExts());

                        //附件处理
                        //质检报告
                        if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthZJBG")) {
                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "30801");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                            //机器质检结论
                            athFile.put("jqzjyj", "机器质检结论");
                            //机器质检时间
                            athFile.put("jqzjsj", frmAttachmentDB.getRDT());
                            //附件key
                            athFile.put("fieldKey", "jqzjyj");
                        } else if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthKZXXXGHDZDKXMQSLC")) {
                            //id
                            athFile.put("subDirId", frmAttachmentDB.getMyNote());
                            //目录id
                            athFile.put("dirId", "30801");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                        }
                        //启动阶段技术审查意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND302_FrmWorkCheck")) {
                            athFile=getJSSCYJ(athFile,tracks,frmAttachmentDB,302);
                        }
                        else {
                            for (FrmAttachment frmAttachment : frmAttachments.ToJavaList()) {
                                if (frmAttachment.getNoOfObj().equals(frmAttachmentDB.getNoOfObj())) {
                                    //目录id
                                    athFile.put("dirId", frmAttachment.getMyPK());
                                    //目录名称
                                    athFile.put("dirName", frmAttachment.getNoOfObj());
                                }
                            }
                        }
                        //创建时间
                        athFile.put("createTime", frmAttachmentDB.getRDT());
                        //创建人
                        athFile.put("createBy", frmAttachmentDB.getRecName());
                        fileList.add(athFile);
                    }
                    //审查审批阶段
                    if (rpt.GetValByKey("JD").equals("1")) {
                        HashMap<String, Object> athFile = new HashMap<>();
                        //附件名称
                        athFile.put("fileName", frmAttachmentDB.getFileName());
                        //附件下载路径
                        athFile.put("filePath", frmAttachmentDB.getFilePathName());
                        //附件大小
                        athFile.put("fileSize", frmAttachmentDB.getFileSize());
                        //附件格式
                        athFile.put("fileFormat", frmAttachmentDB.getFileExts());

                        //附件处理
                        //质检报告

                        //控制性详细规划调整地块分析矢量层(上传gdb格式的zip文件)
                        if (frmAttachmentDB.getFKFrmAttachment().equals("ND307_AthKZXXXGHDZDKFXSLT")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND307_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "30601");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划调整地块分析矢量层");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "30601");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整地块分析矢量层");
                        }
                        //控制性详细规划调整成果矢量数据库(上传gdb格式的zip文件)
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND307_AthKZXXXGHDZCGSLSJK")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND307_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "30701");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划调整成果矢量数据库");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "30701");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整成果矢量数据库");
                        }
                        //控制性详细规划调整地块项目区矢量层(上传gdb格式的zip)
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND307_AthKZXXXGHDZDKXMQSLC")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND307_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "30801");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "30801");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                        }
                        //审查阶段技术审查意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND310_FrmWorkCheck")) {
                            athFile=getJSSCYJ(athFile,tracks,frmAttachmentDB,310);
                        }//图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND311_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,311,false);
                        }
                        //省级，图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND320_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,320,true);
                        }
                        else {
                            for (FrmAttachment frmAttachment : frmAttachments.ToJavaList()) {
                                if (frmAttachment.getNoOfObj().equals(frmAttachmentDB.getNoOfObj())) {
                                    //目录id
                                    athFile.put("dirId", frmAttachment.getMyPK());
                                    //目录名称
                                    athFile.put("dirName", frmAttachment.getNoOfObj());
                                }
                            }
                        }
                        //创建时间
                        athFile.put("createTime", frmAttachmentDB.getRDT());
                        //创建人
                        athFile.put("createBy", frmAttachmentDB.getRecName());
                        fileList.add(athFile);
                    }
                    //入库阶段
                    if (rpt.GetValByKey("JD").equals("2")) {
                        HashMap<String, Object> athFile = new HashMap<>();
                        //附件名称
                        athFile.put("fileName", frmAttachmentDB.getFileName());
                        //附件下载路径
                        athFile.put("filePath", frmAttachmentDB.getFilePathName());
                        //附件大小
                        athFile.put("fileSize", frmAttachmentDB.getFileSize());
                        //附件格式
                        athFile.put("fileFormat", frmAttachmentDB.getFileExts());

                        //附件处理
                        //质检报告
                        //控制性详细规划调整地块分析矢量层(上传gdb格式的zip文件)
                        if (frmAttachmentDB.getFKFrmAttachment().equals("ND307_AthKZXXXGHDZDKFXSLT")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND307_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "30601");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划调整地块分析矢量层");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "30601");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整地块分析矢量层");
                        }
                        //控制性详细规划调整成果矢量数据库(上传gdb格式的zip文件)
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND307_AthKZXXXGHDZCGSLSJK")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND307_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "30701");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划调整成果矢量数据库");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "30701");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整成果矢量数据库");
                        }
                        //控制性详细规划调整地块项目区矢量层(上传gdb格式的zip)
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND307_AthKZXXXGHDZDKXMQSLC")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND307_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "30801");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "30801");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                        }
                        //入库阶段技术审查意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND310_FrmWorkCheck")) {
                            athFile=getJSSCYJ(athFile,tracks,frmAttachmentDB,310);
                        }//图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND311_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,311,false);
                        }
                        //省级，图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND319_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,319,true);
                        }
                        else {
                            for (FrmAttachment frmAttachment : frmAttachments.ToJavaList()) {
                                if (frmAttachment.getNoOfObj().equals(frmAttachmentDB.getNoOfObj())) {
                                    //目录id
                                    athFile.put("dirId", frmAttachment.getMyPK());
                                    //目录名称
                                    athFile.put("dirName", frmAttachment.getNoOfObj());
                                }
                            }
                        }
                        //创建时间
                        athFile.put("createTime", frmAttachmentDB.getRDT());
                        //创建人
                        athFile.put("createBy", frmAttachmentDB.getRecName());
                        fileList.add(athFile);
                    }
                }
                //一般调整
                else if(lx=="4") {
                    //审查审批阶段
                    if (rpt.GetValByKey("JD").equals("1")) {
                        HashMap<String, Object> athFile = new HashMap<>();
                        //附件名称
                        athFile.put("fileName", frmAttachmentDB.getFileName());
                        //附件下载路径
                        athFile.put("filePath", frmAttachmentDB.getFilePathName());
                        //附件大小
                        athFile.put("fileSize", frmAttachmentDB.getFileSize());
                        //附件格式
                        athFile.put("fileFormat", frmAttachmentDB.getFileExts());

                        //附件处理
                        //质检报告

                        //控制性详细规划调整地块分析矢量层(上传gdb格式的zip文件)
                        if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthKZXXXGHDZDKFXSLT")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND301_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "40401");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划调整地块分析矢量层");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "40401");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整地块分析矢量层");
                        }
                        //控制性详细规划调整成果矢量数据库(上传gdb格式的zip文件)
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthKZXXXGHDZCGSLSJK")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND301_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "40501");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划调整成果矢量数据库");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "40501");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整成果矢量数据库");
                        }
                        //控制性详细规划调整地块项目区矢量层(上传gdb格式的zip)
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthKZXXXGHDZDKXMQSLC")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND301_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "40601");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "40601");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                        }
                        //审查阶段技术审查意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND310_FrmWorkCheck")) {
                            athFile=getJSSCYJ(athFile,tracks,frmAttachmentDB,310);
                        }//图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND311_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,311,false);
                        }
                        //省级，图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND320_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,320,true);
                        }
                        else {
                            for (FrmAttachment frmAttachment : frmAttachments.ToJavaList()) {
                                if (frmAttachment.getNoOfObj().equals(frmAttachmentDB.getNoOfObj())) {
                                    //目录id
                                    athFile.put("dirId", frmAttachment.getMyPK());
                                    //目录名称
                                    athFile.put("dirName", frmAttachment.getNoOfObj());
                                }
                            }
                        }
                        //创建时间
                        athFile.put("createTime", frmAttachmentDB.getRDT());
                        //创建人
                        athFile.put("createBy", frmAttachmentDB.getRecName());
                        fileList.add(athFile);
                    }
                    //入库阶段
                    if (rpt.GetValByKey("JD").equals("2")) {
                        HashMap<String, Object> athFile = new HashMap<>();
                        //附件名称
                        athFile.put("fileName", frmAttachmentDB.getFileName());
                        //附件下载路径
                        athFile.put("filePath", frmAttachmentDB.getFilePathName());
                        //附件大小
                        athFile.put("fileSize", frmAttachmentDB.getFileSize());
                        //附件格式
                        athFile.put("fileFormat", frmAttachmentDB.getFileExts());

                        //附件处理
                        //质检报告
                        //控制性详细规划调整地块分析矢量层(上传gdb格式的zip文件)
                        if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthKZXXXGHDZDKFXSLT")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND301_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "40401");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划调整地块分析矢量层");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "40401");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整地块分析矢量层");
                        }
                        //控制性详细规划调整成果矢量数据库(上传gdb格式的zip文件)
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthKZXXXGHDZCGSLSJK")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND301_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "40501");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划调整成果矢量数据库");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "40501");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整成果矢量数据库");
                        }
                        //控制性详细规划调整地块项目区矢量层(上传gdb格式的zip)
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthKZXXXGHDZDKXMQSLC")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND301_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "40601");
                                //目录名称
                                subFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "40601");
                            //目录名称
                            athFile.put("dirName", "控制性详细规划调整地块项目区矢量层");
                        }
                        //入库阶段技术审查意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND310_FrmWorkCheck")) {
                            athFile=getJSSCYJ(athFile,tracks,frmAttachmentDB,310);
                        }//图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND311_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,311,false);
                        }
                        //省级，图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND319_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,319,true);
                        }
                        else {
                            for (FrmAttachment frmAttachment : frmAttachments.ToJavaList()) {
                                if (frmAttachment.getNoOfObj().equals(frmAttachmentDB.getNoOfObj())) {
                                    //目录id
                                    athFile.put("dirId", frmAttachment.getMyPK());
                                    //目录名称
                                    athFile.put("dirName", frmAttachment.getNoOfObj());
                                }
                            }
                        }
                        //创建时间
                        athFile.put("createTime", frmAttachmentDB.getRDT());
                        //创建人
                        athFile.put("createBy", frmAttachmentDB.getRecName());
                        fileList.add(athFile);
                    }
                }
                //技术修正
                else if(lx=="5") {
                    //审查审批阶段
                    if (rpt.GetValByKey("JD").equals("1")) {
                        HashMap<String, Object> athFile = new HashMap<>();
                        //附件名称
                        athFile.put("fileName", frmAttachmentDB.getFileName());
                        //附件下载路径
                        athFile.put("filePath", frmAttachmentDB.getFilePathName());
                        //附件大小
                        athFile.put("fileSize", frmAttachmentDB.getFileSize());
                        //附件格式
                        athFile.put("fileFormat", frmAttachmentDB.getFileExts());

                        //附件处理
                        //质检报告

                        //技术修正地块项目区矢量层(上传gdb格式的zip文件)
                        if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthJSXZDKXMQSLC")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND301_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "50601");
                                //目录名称
                                subFile.put("dirName", "技术修正地块项目区矢量层");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "50601");
                            //目录名称
                            athFile.put("dirName", "技术修正地块项目区矢量层");
                        }
                        //技术修正地块分析矢量层(上传gdb格式的zip文件)
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthJSXZDKFXSLC")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND301_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "50401");
                                //目录名称
                                subFile.put("dirName", "技术修正地块分析矢量层");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "50401");
                            //目录名称
                            athFile.put("dirName", "技术修正地块分析矢量层");
                        }
                        //控制性详细规划调整地块项目区矢量层(上传gdb格式的zip)
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthJSXZCGSLSJK")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND301_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "50501");
                                //目录名称
                                subFile.put("dirName", "技术修正成果矢量数据库");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "50501");
                            //目录名称
                            athFile.put("dirName", "技术修正成果矢量数据库");
                        }
                        //审查阶段技术审查意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND310_FrmWorkCheck")) {
                            athFile=getJSSCYJ(athFile,tracks,frmAttachmentDB,310);
                        }//图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND311_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,311,false);
                        }
                        //省级，图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND320_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,320,true);
                        }
                        else {
                            for (FrmAttachment frmAttachment : frmAttachments.ToJavaList()) {
                                if (frmAttachment.getNoOfObj().equals(frmAttachmentDB.getNoOfObj())) {
                                    //目录id
                                    athFile.put("dirId", frmAttachment.getMyPK());
                                    //目录名称
                                    athFile.put("dirName", frmAttachment.getNoOfObj());
                                }
                            }
                        }
                        //创建时间
                        athFile.put("createTime", frmAttachmentDB.getRDT());
                        //创建人
                        athFile.put("createBy", frmAttachmentDB.getRecName());
                        fileList.add(athFile);
                    }
                    //入库阶段
                    if (rpt.GetValByKey("JD").equals("2")) {
                        HashMap<String, Object> athFile = new HashMap<>();
                        //附件名称
                        athFile.put("fileName", frmAttachmentDB.getFileName());
                        //附件下载路径
                        athFile.put("filePath", frmAttachmentDB.getFilePathName());
                        //附件大小
                        athFile.put("fileSize", frmAttachmentDB.getFileSize());
                        //附件格式
                        athFile.put("fileFormat", frmAttachmentDB.getFileExts());

                        //附件处理
                        //质检报告
                        //技术修正地块项目区矢量层(上传gdb格式的zip文件)
                        if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthJSXZDKXMQSLC")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND301_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "50601");
                                //目录名称
                                subFile.put("dirName", "技术修正地块项目区矢量层");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "50601");
                            //目录名称
                            athFile.put("dirName", "技术修正地块项目区矢量层");
                        }
                        //技术修正地块分析矢量层(上传gdb格式的zip文件)
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthJSXZDKFXSLC")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND301_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "50401");
                                //目录名称
                                subFile.put("dirName", "技术修正地块分析矢量层");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "50401");
                            //目录名称
                            athFile.put("dirName", "技术修正地块分析矢量层");
                        }
                        //控制性详细规划调整地块项目区矢量层(上传gdb格式的zip)
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND301_AthJSXZCGSLSJK")) {
                            FrmAttachmentDBs attachmentDBs=new FrmAttachmentDBs();
                            attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment,"ND301_AthZJBG",FrmAttachmentDBAttr.UploadGUID,frmAttachmentDB.getUploadGUID());
                            if (attachmentDBs.size()>0){
                                FrmAttachmentDB attachmentDB=(FrmAttachmentDB)attachmentDBs.get(0);
                                HashMap<String, Object> subFile = new HashMap<>();
                                //附件名称
                                subFile.put("fileName", attachmentDB.getFileName());
                                //附件下载路径
                                subFile.put("filePath", attachmentDB.getFilePathName());
                                //附件大小
                                subFile.put("fileSize", attachmentDB.getFileSize());
                                //附件格式
                                subFile.put("fileFormat", attachmentDB.getFileExts());
                                //id
                                subFile.put("subDirId", attachmentDB.getMyNote());
                                //目录id
                                subFile.put("dirId", "50501");
                                //目录名称
                                subFile.put("dirName", "技术修正成果矢量数据库");
                                //机器质检结论
                                subFile.put("jqzjyj", "机器质检结论");
                                //机器质检时间
                                subFile.put("jqzjsj", attachmentDB.getRDT());
                                //附件key
                                subFile.put("fieldKey", "jqzjyj");
                                //创建时间
                                subFile.put("createTime", attachmentDB.getRDT());
                                //创建人
                                subFile.put("createBy", attachmentDB.getRecName());
                                fileList.add(subFile);
                            }

                            //id
                            athFile.put("subDirId", frmAttachmentDB.getUploadGUID());
                            //目录id
                            athFile.put("dirId", "50501");
                            //目录名称
                            athFile.put("dirName", "技术修正成果矢量数据库");
                        }
                        //入库阶段技术审查意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND310_FrmWorkCheck")) {
                            athFile=getJSSCYJ(athFile,tracks,frmAttachmentDB,310);
                        }//图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND311_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,311,false);
                        }
                        //省级，图数一致性审查意见、压覆审核意见
                        else if (frmAttachmentDB.getFKFrmAttachment().equals("ND319_FrmWorkCheck")) {
                            athFile=getTSSCYJ(athFile,tracks,frmAttachmentDB,319,true);
                        }
                        else {
                            for (FrmAttachment frmAttachment : frmAttachments.ToJavaList()) {
                                if (frmAttachment.getNoOfObj().equals(frmAttachmentDB.getNoOfObj())) {
                                    //目录id
                                    athFile.put("dirId", frmAttachment.getMyPK());
                                    //目录名称
                                    athFile.put("dirName", frmAttachment.getNoOfObj());
                                }
                            }
                        }
                        //创建时间
                        athFile.put("createTime", frmAttachmentDB.getRDT());
                        //创建人
                        athFile.put("createBy", frmAttachmentDB.getRecName());
                        fileList.add(athFile);
                    }
                }
            }

            HashMap<String,Object> hashMap=new HashMap<>();
            //方案名称
            hashMap.put("ghfamc",rpt.GetValByKey("GHFAMC"));
            //备案类型
            SysEnums enums=new SysEnums();
            enums.Retrieve(SysEnumAttr.EnumKey,"KGRKLX");
            for (SysEnum sysEnum:enums.ToJavaList()){
                if(sysEnum.getIntKey()==Integer.parseInt(rpt.GetValByKey("KGRKLX").toString()))
                    hashMap.put("balx",sysEnum.getLab());
            }
            //所在片区
            hashMap.put("szpq",rpt.GetValByKey("SZPQT"));
            //是否属于重要规划控制区
            hashMap.put("sfsyzyghkzq",rpt.GetValByKey("Dict_SFSYZYGHKZQT"));
            //所在市县
            hashMap.put("szsx",rpt.GetValByKey("SQL_SXT"));
            //所在地区
            hashMap.put("szdq",rpt.GetValByKey("SZXZT"));
            //行政区代码
            hashMap.put("xzqdm",rpt.GetValByKey("SQL_SX"));
            //是否委托审批
            enums=new SysEnums();
            enums.Retrieve(SysEnumAttr.EnumKey,"SFWTSP");
            for (SysEnum sysEnum:enums.ToJavaList()){
                if(sysEnum.getIntKey()==Integer.parseInt(rpt.GetValByKey("SFWTSP").toString()))
                    hashMap.put("sfwtsp",sysEnum.getLab());
            }
            //编制主体
            hashMap.put("zzbzzt",rpt.GetValByKey("ZZBZZT"));
            //编制单位
            hashMap.put("bzjsdw",rpt.GetValByKey("BZJSDW"));
            //批复文件
            hashMap.put("pfwj",rpt.GetValByKey("PFWJ"));
            //批复单位
            hashMap.put("pfdw",rpt.GetValByKey("PFDW"));
            //批复时间
            hashMap.put("pfsj",rpt.GetValByKey("PFSJ"));
            //批文号
            hashMap.put("pwh",rpt.GetValByKey("PWH"));
            //申请单位
            hashMap.put("sqdw",rpt.GetValByKey("SQDW"));
            //经办人
            hashMap.put("jbr",rpt.GetValByKey("JBR"));
            //联系电话
            hashMap.put("lxdh",rpt.GetValByKey("LXDH"));
            //电子邮箱
            hashMap.put("dzyx",rpt.GetValByKey("DZYX"));
            if(lx.equals("6")) {
                //地块类型
                hashMap.put("dklx", rpt.GetValByKey("Dict_DKLXT"));
            }
            //项目概况
            hashMap.put("xmgk",rpt.GetValByKey("XMGK"));
            //开发边界
            hashMap.put("kfbj",rpt.GetValByKey("KFBJ"));
            //开发边界内建设用地规模
            hashMap.put("kfbjnjsydgm",rpt.GetValByKey("KFBJNJSYDGM"));
            //申请说明
            hashMap.put("sqsm",rpt.GetValByKey("SQSM"));
            //数据坐标系
            enums=new SysEnums();
            enums.Retrieve(SysEnumAttr.EnumKey,"SJZBX");
            for (SysEnum sysEnum:enums.ToJavaList()){
                if(sysEnum.getIntKey()==Integer.parseInt(rpt.GetValByKey("SJZBX").toString()))
                    hashMap.put("sjzbx",sysEnum.getLab());
            }


            HashMap<String,Object> processInfo=new HashMap<>();
            //表单数据
            processInfo.put("jsonData",hashMap);
            //附件列表
            processInfo.put("fileInfoList",fileList);

            //推送的整体数据
            HashMap<String,Object> map=new HashMap<>();
            //新增
            if(lx.equals("1"))
                map.put("type","KG_CREATE");
            //整体更新
            else if(lx.equals("2")){
                Boolean isQdJd=false;
                for (Track track:tracks.ToJavaList()){
                    if(track.getNDFrom()==302)
                        isQdJd=true;
                }
                if(isQdJd)//启动阶段
                    map.put("type","KG_ZT_CREATE_NEW");
                else//入库阶段
                    map.put("type","KG_CREATE");
            }
            //地块调整
            else if(lx.equals("3")){
                Boolean isQdJd=false;
                for (Track track:tracks.ToJavaList()){
                    if(track.getNDFrom()==302)
                        isQdJd=true;
                }
                if(isQdJd)//启动阶段
                    map.put("type","KG_UPDATE_NEW");
                else//入库阶段
                    map.put("type","KG_UPDATE");
            }
            //一般调整
            else if(lx.equals("4")){
                Boolean isQdJd=false;
                for (Track track:tracks.ToJavaList()){
                    if(track.getNDFrom()==309)
                        isQdJd=true;
                }
                if(isQdJd)//审查审批阶段
                    map.put("type","KG_CREATE_NEW");
                else//入库阶段
                    map.put("type","KG_UPDATE");
            }
            //技术修正
            else if(lx.equals("5")){
                Boolean isQdJd=false;
                for (Track track:tracks.ToJavaList()){
                    if(track.getNDFrom()==309)
                        isQdJd=true;
                }
                if(isQdJd)//审查审批阶段
                    map.put("type","KG_CREATE_NEW");
                else//入库阶段
                    map.put("type","KG_UPDATE");
            }
            //局部新增
            else if(lx.equals("6"))
                map.put("type","KG_JBXZ");
            map.put("operateType","accept");
            map.put("bah",rpt.GetValByKey("BillNo"));
            map.put("processInfo",processInfo);

            Map<String,String> headerMap=new HashMap<>();
            headerMap.put("token",token);

            String postUrl="";
            //县级推送一体化测试接口,省正式8087,测试9009,市县正式10899,测试10891
            if(rpt.GetValByKey("SFSSJ").toString().equals("0")){
                postUrl=yt_sx_url+"/kgsp/handleProcessAndFiles";
            }
            else{
                postUrl=yt_sheng_url+"/kgsp/handleProcessAndFiles";
            }

            bp.da.Log.DefaultLogWriteLine(LogType.Info, "控规数据一体化推送json："+ bp.tools.Json.ToJson(map));

            String data= HttpClientUtil.doPost(postUrl,map.toString(),headerMap);
            JSONObject jd=JSONObject.parseObject(data);
            if(Boolean.parseBoolean(jd.get("success").toString())){
                bp.da.Log.DefaultLogWriteLine(LogType.Info, "控规数据一体化推送成功："+ map.toString());
                //执行成功
                this.SucessInfo="推送成功";
            }
            else{
                //执行失败
                bp.da.Log.DefaultLogWriteLine(LogType.Info, "控规数据一体化推送失败："+ data);
                this.SucessInfo="err@@控规数据一体化推送失败：@"+data;
            }
        }
        catch (Exception ex){
            //执行失败
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "控规数据一体化推送异常："+ ex.getMessage());
            this.SucessInfo="err@@控规数据一体化推送异常：@"+ex.getMessage();
        }
    }

    /**
     * 判断是否是图数审核员
     * @param no
     * @return
     */
    public Boolean isTSStation(String no){
        String sql="select count(*) from port_deptemp where fk_emp='"+no+"' and StationNo='1564-41405-9274'";
        Integer num=bp.da.DBAccess.RunSQLReturnValInt(sql);
        if(num>0)
            return true;
        else
            return false;
    }

    /**
     * 获取技术审查意见
     * @return
     */
    public HashMap<String,Object> getJSSCYJ(HashMap<String,Object> athFile,Tracks tracks
            ,FrmAttachmentDB frmAttachmentDB,Integer nodeId){
        try {
            //目录id
            athFile.put("dirId", this.getWorkID());
            //目录名称
            athFile.put("dirName", "数据审查意见");
            //附件key
            athFile.put("fieldKey", "sjscyj");
            //技术审查人
            athFile.put("shr", frmAttachmentDB.getRecName());
            //最新的技术审查意见mypk
            String tackMyPK = "";
            //历史审核意见
            String lssjscyj = "";
            for (Track track : tracks.ToJavaList()) {
                if (track.getNDFrom() == nodeId) {
                    tackMyPK = track.getMyPK();
                    //技术审查意见
                    athFile.put("shyj", track.getMsg());
                    //技术审查时间
                    athFile.put("shsj", track.getRDT());
                    break;
                }
            }
            //获取历史审核意见
            for (Track track : tracks.ToJavaList()) {
                if (track.getNDFrom() == nodeId && track.getMyPK().equals(tackMyPK) == false) {
                    lssjscyj += track.getEmpFromT() + "(" + track.getRDT() + ")：" + track.getMsg() + ";";
                }
            }
            if (!DataType.IsNullOrEmpty(lssjscyj))
                athFile.put("lssjscyj", lssjscyj);

            return athFile;
        }
        catch (Exception ex){
            return athFile;
        }
    }
    /**
     * 获取图数、压覆审查意见
     * @param athFile
     * @param tracks
     * @param frmAttachmentDB
     * @param nodeId
     * @param sfssj
     * @return
     */
    public HashMap<String,Object> getTSSCYJ(HashMap<String,Object> athFile,Tracks tracks
            ,FrmAttachmentDB frmAttachmentDB,Integer nodeId,Boolean sfssj){
        try {
            //目录id
            athFile.put("dirId", this.getWorkID());
            //目录名称
            athFile.put("dirName", "数据审查意见");
            //最新的图数一致性审查意见mypk
            String tsTackMyPK="";
            //最新的压覆审核意见mypk
            String yfTackMyPK="";
            //历史图数一致性审查意见
            String lstsyzxscyj="";
            //历史压覆审核意见
            String lsyfscyi="";
            for (Track track : tracks.ToJavaList()) {
                //图数一致性审查意见
                if (track.getNDFrom() == nodeId){
                    //判断是否是图数审核员
                    if (isTSStation(frmAttachmentDB.getRec())) {
                        if(DataType.IsNullOrEmpty(tsTackMyPK)) {
                            //附件key
                            if(sfssj)
                                athFile.put("fieldKey", "tsyzxscyj1");
                            else
                                athFile.put("fieldKey", "tsyzxscyj");
                            //技术审查人
                            athFile.put("shr", frmAttachmentDB.getRecName());
                            //技术审查意见
                            athFile.put("shyj", track.getMsg());
                            //技术审查时间
                            athFile.put("shsj", track.getRDT());
                            tsTackMyPK=track.getMyPK();
                        }
                    }
                    //压覆审核意见
                    else {
                        if(DataType.IsNullOrEmpty(yfTackMyPK)) {
                            //附件key
                            if(sfssj)
                                athFile.put("fieldKey", "yfscyi1");
                            else
                                athFile.put("fieldKey", "yfscyi");
                            //技术审查人
                            athFile.put("shr", frmAttachmentDB.getRecName());
                            //技术审查意见
                            athFile.put("shyj", track.getMsg());
                            //技术审查时间
                            athFile.put("shsj", track.getRDT());
                            yfTackMyPK=track.getMyPK();
                        }
                    }
                }
            }
            //获取历史审核意见
            for (Track track : tracks.ToJavaList()) {
                //图数一致性审查意见
                if (track.getNDFrom() == nodeId){
                    //判断是否是图数审核员
                    if (isTSStation(frmAttachmentDB.getRec())) {
                        if(track.getMyPK().equals(tsTackMyPK)==false) {
                            lstsyzxscyj+=track.getEmpFromT()+"("+track.getRDT()+")："+track.getMsg()+";";
                        }
                    }
                    //压覆审核意见
                    else {
                        if(track.getMyPK().equals(yfTackMyPK)==false) {
                            lsyfscyi+=track.getEmpFromT()+"("+track.getRDT()+")："+track.getMsg()+";";
                        }
                    }
                }
            }
            if(sfssj){
                if (!DataType.IsNullOrEmpty(lstsyzxscyj))
                    athFile.put("lstsyzxscyj1", lstsyzxscyj);
                if (!DataType.IsNullOrEmpty(lsyfscyi))
                    athFile.put("lsyfscyi1", lsyfscyi);
            }
            else {
                if (!DataType.IsNullOrEmpty(lstsyzxscyj))
                    athFile.put("lstsyzxscyj", lstsyzxscyj);
                if (!DataType.IsNullOrEmpty(lsyfscyi))
                    athFile.put("lsyfscyi", lsyfscyi);
            }
            return athFile;
        }
        catch (Exception ex){
            return athFile;
        }
    }
}
