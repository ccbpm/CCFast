package bp.App;

import bp.App.Dto.MenuCh;
import bp.App.Dto.MenuInfo;
import bp.App.Dto.RyMenuDto;
import bp.App.Handler.ExcelHelperTool;
import bp.App.Handler.TemplateSql;
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
import com.alibaba.fastjson.JSONObject;
import com.aspose.slides.Collections.ArrayList;
import com.aspose.slides.exceptions.IOException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Handler_AD extends bp.difference.handler.DirectoryPageBase{
    /**
     构造函数
     */
//ORIGINAL LINE: public Handler_AD()
    public Handler_AD()
    {
    }

    public final String ImpAD() throws Exception {
        String ext = ".xls";
        HttpServletRequest request = getRequest();
        String contentType = request.getContentType();
        if (contentType == null || contentType.indexOf("multipart/form-data") == -1) {
            throw new RuntimeException("ContentType不符合要求[ContentType=" + contentType + "]");
        }
        File file = null;
        MultipartHttpServletRequest mrequest = CommonFileUtils.getMultipartHttpServletRequest(request);
        MultipartFile requestFile = mrequest.getFile("file");
        if (requestFile == null) {
            return "err@没有获取到文件.";
        }
        String fileNmae = requestFile.getOriginalFilename();
        if(fileNmae.endsWith(".xlsx"))
            ext = ".xlsx";

        //设置文件名
        String fileNewName = DataType.getCurrentDateByFormart("yyyyMMddHHmmssff") + ext;

        //文件存放路径
        String filePath = bp.difference.SystemConfig.getPathOfTemp() + fileNewName;
        file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        try {
            CommonFileUtils.upload(request, "file", file);
        } catch (Exception e) {
            e.printStackTrace();
            return "err@执行失败";
        }

        DataTable dtDept = bp.da.DBLoad.ReadExcelFileToDataTable(filePath);

        String cls = "TS.AD.XS";
        String msg = "";
        for (DataRow dr : dtDept.Rows)
        {
            TSEntityNoName xs = new TSEntityNoName(cls);
            xs.setNo(dr.get(0).toString());
            if (xs.IsExits() == true)
            {
                msg += " err@编号:" + xs.getNo() + "," + xs.getName() + ",已经存在.";
                continue;
            }

            xs.setName(dr.get(1).toString());
            //ORIGINAL LINE: string? wfsc = dr["违法时长"] instanceof String ? (String)dr["违法时长"] : null;
            String wfsc = dr.get("违法时长") instanceof String ? (String)dr.get("违法时长") : null; //违法时长
            xs.SetValByKey("ShiChang", wfsc);

            //ORIGINAL LINE: string? splx = dr["商品类型"] instanceof String ? (String)dr["商品类型"] : null;
            String splx = dr.get("商品类型") instanceof String ? (String)dr.get("商品类型") : null; //违法时长
            String splxNo = DBAccess.RunSQLReturnString("SELECT No FROM AD_SPLX WHERE Name='" + (splx == null ? null : splx.trim()) + "'");
            if (splxNo == null)
            {
                splxNo = "001";
            }
            xs.SetValByKey("SPLX", splxNo);

            //ORIGINAL LINE: string? mtmc = dr["媒体名称"] instanceof String ? (String)dr["媒体名称"] : null;
            String mtmc = dr.get("媒体名称") instanceof String ? (String)dr.get("媒体名称") : null; //违法时长
            String mtmcNo = DBAccess.RunSQLReturnString("SELECT No FROM AD_MeiTi WHERE Name='" + (mtmc == null ? null : mtmc.trim()) + "'");
            if (mtmcNo == null)
            {
                mtmcNo = "3701001";
            }
            xs.SetValByKey("MeiTi", mtmcNo);

            //ORIGINAL LINE: string? diQu = dr["地区"] instanceof String ? (String)dr["地区"] : null;
            String diQu = dr.get("地区") instanceof String ? (String)dr.get("地区") : null; //违法时长
            String diQuNo = DBAccess.RunSQLReturnString("SELECT No FROM cn_diqu WHERE Name='" + (diQu == null ? null : diQu.trim()) + "'");
            if (diQuNo == null)
            {
                diQuNo = "370100";
            }
            xs.SetValByKey("DiQu", diQuNo);

            xs.SetValByKey("QuXian", diQuNo.replace("00","") + "01");
            xs.SetValByKey("XiangZhen", diQuNo.replace("00", "") + "0101");
            xs.Insert();

            msg += "info@数据[" + xs.getNo() + xs.getName() + "]已经导入.";
        }
        return msg;
    }

    public final String ImpZipAD() throws Exception {
        try {
        String ext = ".zip";
        HttpServletRequest request = getRequest();
        String contentType = request.getContentType();
        if (contentType == null || contentType.indexOf("multipart/form-data") == -1) {
            throw new RuntimeException("ContentType不符合要求[ContentType=" + contentType + "]");
        }

        MultipartHttpServletRequest mrequest = CommonFileUtils.getMultipartHttpServletRequest(request);
        MultipartFile requestFile = mrequest.getFile("file");
        if (requestFile == null) {
            return "err@没有获取到文件.";
        }
        String fileNmae = requestFile.getOriginalFilename();
        if(!fileNmae.endsWith(".zip")){
            return "err@文件类型必须是.zip格式";
        }
        //文件存放路径
        String zipFlod =bp.difference.SystemConfig.getPathOfTemp()+"sdjdy"+File.separator+DataType.getCurrentDateByFormart("yyyyMMDDHHddss")+File.separator;
        //zip解压后文件夹
        String zipFlodExt =bp.difference.SystemConfig.getPathOfTemp()+"sdjdy"+File.separator+DataType.getCurrentDateByFormart("yyyyMMDDHHddss")+File.separator+"zip"+File.separator;
        File folder = new File(zipFlodExt);
        // 如果文件夹不存在，则创建文件夹
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("文件夹创建成功：" + zipFlodExt);
            }
        }
        String fname=DataType.getCurrentDateByFormart("yyyyMMDDHHddss") + ext;
        String zipFile=zipFlod+fname;
        File zfile  = new File(zipFile);
        if (zfile.exists()) {
            zfile.delete();
        }

            CommonFileUtils.upload(request, "file", zfile);

//        解开压缩到文件, 到  tempDir
        unzip(zfile,zipFlodExt);
        File file=null;
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null ) {
                for(File f:files){
                    if (!f.isDirectory() && f.getName().endsWith("xlsx")) {
                        file=f;
                    }
                }
            }else{
                return "err@文件不符合约定的格式，没有找到根目录下的文件.";
            }
        }
        if(file==null){
            return "err@文件不符合约定的格式，没有找到根目录下的文件.";
        }
        //广告类别
        DataTable gglblist=DBAccess.RunSQLReturnTable("select intKey No,lab Name from Sys_Enum where EnumKey='advertisingType'");
        //行业类型
//        DataTable hylxlist=DBAccess.RunSQLReturnTable("SELECT No,Name,BeiZhu  from ad_splx order by NO");
        //媒介类型
        DataTable mjlblist=DBAccess.RunSQLReturnTable("select intKey No,lab Name from Sys_Enum where EnumKey='MeiJieLeiBie'");
        //地区市
        DataTable dqlist=DBAccess.RunSQLReturnTable("select * from cn_diqu");
        //县
        DataTable xlist=DBAccess.RunSQLReturnTable("select * from cn_quxian");
        //违规代码
        DataTable wglist=DBAccess.RunSQLReturnTable("select No,Name,YiJu from ad_weigui");

        //文件存放路径.
        DataTable dtDept = bp.da.DBLoad.ReadExcelFileToDataTable(file.getAbsolutePath(),0);
        List<Map<String,String>>drlist=new ArrayList();
        for (DataRow dr : dtDept.Rows) {
            String idx = dr.get("序号").toString();
            String ggmtmc = dr.get("广告媒体名称").toString();
            String ggmtdz = dr.get("广告媒体地址").toString();
            String ggzmc = dr.get("广告主名称").toString();
            String ggzdz = dr.get("广告主地址").toString();
            String ggmc = dr.get("广告名称").toString();
            String ggsc = dr.get("广告时长").toString();
            String fbsj = dr.get("发布时间").toString();
            String wfmsxx = dr.get("违法描述信息").toString();

            String gglbT = dr.get("广告类别").toString();
            String mjlbT = dr.get("媒介类别").toString();
            String lsdsjT = dr.get("隶属地市局").toString();
            String qxT = dr.get("区县").toString();
            String wfdm = dr.get("违法代码").toString();
            if (!idx.equals("")) {
                Map<String, String> m = new HashMap<>();
                m.put("idx", idx);
                m.put("ggmtmc", ggmtmc);
                m.put("ggmtdz", ggmtdz);
                m.put("ggzmc", ggzmc);
                m.put("ggzdz", ggzdz);
                m.put("ggmc", ggmc);
                m.put("ggsc", ggsc);
                m.put("fbsj", fbsj);
                m.put("wfmsxx", wfmsxx);
                m.put("gglbT", gglbT);
                m.put("mjlbT", mjlbT);
                m.put("lsdsjT", lsdsjT);
                m.put("qxT", qxT);
                m.put("wfdm", wfdm);
                drlist.add(m);
            } else {
                String wfdmm = drlist.get(drlist.size() - 1).get("wfdm");
                if (!wfdm.equals("")) {
                    wfdmm += "," + wfdm;
                    drlist.get(drlist.size() - 1).put("wfdm", wfdmm);
                }
            }
        }
        for(Map<String,String>m:drlist){
            String idx = m.get("idx");
            String ggmtmc = m.get("ggmtmc");
            String ggmtdz = m.get("ggmtdz");
            String ggzmc = m.get("ggzmc");
            String ggzdz = m.get("ggzdz");
            String ggmc = m.get("ggmc");
            String ggsc = m.get("ggsc");
            String fbsj = m.get("fbsj");
            String wfmsxx = m.get("wfmsxx");
            String gglbT = m.get("gglbT");
            String mjlbT = m.get("mjlbT");
            String lsdsjT = m.get("lsdsjT");
            String qxT = m.get("qxT");
            String wfdm = m.get("wfdm");
            long workid = Dev2Interface.Node_CreateBlankWork("005");
            //主表字段处理
            GEEntityOID en = new GEEntityOID("ND501", workid);
            en.SetValByKey("GuangGaoMeiTiMingChe", ggmtmc);
            en.SetValByKey("GuangGaoZhuMingCheng", ggzmc);
            en.SetValByKey("GuangGaoMeiTiDiZhi", ggmtdz);
            en.SetValByKey("GuangGaoZhuDiZhi", ggzdz);
            en.SetValByKey("GuangGaoMingCheng", ggmc);
            en.SetValByKey("WeiFaShiZhang", ggsc);
            en.SetValByKey("SQRQ", fbsj);
            en.SetValByKey("WeiFaMiaoShuXinXi", wfmsxx);
            //广告类别
            for(DataRow dr :gglblist.Rows){
                if(dr.get("Name").toString().equals(gglbT))    {
                    en.SetValByKey("advertisingType", dr.get("No"));
                    en.SetValByKey("advertisingTypeT", dr.get("Name"));
                    break;
                }
            }
            //媒介类别
            for(DataRow dr :mjlblist.Rows){
                if(dr.get("Name").toString().equals(mjlbT))    {
                    en.SetValByKey("MeiJieLeiBie", Integer.parseInt(dr.get("No").toString()));
                    en.SetValByKey("MeiJieLeiBieT", dr.get("Name"));
                    break;
                }
            }
            //隶属地市局
            for(DataRow dr :dqlist.Rows){
                if(dr.get("Name").toString().equals(lsdsjT))    {
                    en.SetValByKey("cn_diqu", dr.get("No"));
                    en.SetValByKey("cn_diquT", dr.get("Name"));
                    break;
                }
            }
            //区县
            for(DataRow dr :xlist.Rows){
                if(dr.get("Name").toString().equals(qxT))    {
                    en.SetValByKey("cn_quxian", dr.get("No"));
                    en.SetValByKey("cn_quxianT", dr.get("Name"));
                    break;
                }
            }
            en.setOID(workid);
            en.Update();

            //从表处理

            for(String k:wfdm.split(",")){
                GEDtl dtls = new GEDtl("ND501WeiFaXingWei");
                dtls.setRefPK(String.valueOf(workid));
                for(DataRow dr :wglist.Rows){
                    if(dr.get("No").toString().equals(k))    {
                        dtls.SetValByKey("WeiGui", dr.get("No"));
                        dtls.SetValByKey("WeiGuiT", dr.get("Name"));
                        dtls.SetValByKey("YiJu", dr.get("YiJu"));
                        break;
                    }
                }
                dtls.Insert();
            }


            //附件上传
            File[] files = folder.listFiles();
            for(File f:files) {
                if (f.isDirectory() && f.getName().startsWith(idx+".")) {
                    File[] files2 = f.listFiles();
                    for(File f2:files2){
                        if(!f2.isDirectory()){
                            bp.wf.Dev2Interface.CCForm_AddAth(501, "005", workid, "ND501_ShiPinTuPian", "ND501", f2.getAbsolutePath(), f2.getName());
                        }
                    }
                }
            }
            //设置待办.
            bp.wf.Dev2Interface.Node_SetDraft2Todolist(workid);
            //执行发送.
            bp.wf.Dev2Interface.Node_SendWork("005", workid);
        }
        } catch (Exception e) {
            e.printStackTrace();
            return "err@执行失败："+e.getMessage();
        }
        return "info@导入成功";
    }
    public static void unzip(File zipFile, String descDir) {
        try (ZipArchiveInputStream inputStream = getZipFile(zipFile)) {
            File pathFile = new File(descDir);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            ZipArchiveEntry entry = null;
            while ((entry = inputStream.getNextZipEntry()) != null) {
                if (entry.isDirectory()) {
                    File directory = new File(descDir, entry.getName());
                    directory.mkdirs();
                } else {
                    OutputStream os = null;
                    try {
                        os = new BufferedOutputStream(new FileOutputStream(new File(descDir, entry.getName())));
                        //输出文件路径信息
                        IOUtils.copy(inputStream, os);
                    } finally {
                        IOUtils.closeQuietly(os);
                    }
                }
            }
            final File[] files = pathFile.listFiles();
            if (files != null && files.length == 1 && files[0].isDirectory()) {
                // 说明只有一个文件夹
                FileUtils.copyDirectory(files[0], pathFile);
                //免得删除错误， 删除的文件必须在/data/demand/目录下。
                boolean isValid = files[0].getPath().contains("/data/www/");
                if (isValid) {
                    FileUtils.forceDelete(files[0]);
                }
            }

        } catch (Exception e) {
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "[unzip] 解压zip文件出错"+ e.getMessage());
        }
    }
    private static ZipArchiveInputStream getZipFile(File zipFile) throws Exception {
        return new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
    }
}
