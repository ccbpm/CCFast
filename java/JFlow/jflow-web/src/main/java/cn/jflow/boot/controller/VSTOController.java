package cn.jflow.boot.controller;

import bp.da.DBAccess;
import bp.da.DataType;
import bp.difference.SystemConfig;
import bp.difference.handler.HttpHandlerBase;
import bp.sys.AthSaveWay;
import bp.sys.FrmAttachment;
import bp.sys.FrmAttachmentDB;
import bp.tools.AesEncodeUtil;
import bp.tools.FtpUtil;
import bp.wf.Dev2Interface;
import bp.wf.Node;
import bp.wf.Work;
import bp.wf.httphandler.HttpHandlerGlo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Hashtable;

@RestController
@Api(tags="office和wps插件相关功能接口")
@RequestMapping(value = "/WF/VSTO")
public class VSTOController extends HttpHandlerBase {

    public  final String DBFile ="DBFile";
    public static Object Return_Info(int code, String msg, Object data)
    {
        Hashtable ht = new Hashtable();
        ht.put("code", code);
        ht.put("message", msg);
        ht.put("msg", msg);
        ht.put("data", data);
        return ht;
    }
    @GetMapping(value = "/GenerGongWenTrackByte")
    @ApiOperation("wps获得公文版本文件流")
    @ResponseBody
    public Object GenerGongWenTrackByteWPS(String token, String mypk, int flowNo, HttpServletResponse response, HttpServletRequest request) {
        try {
            //生成文件流
            byte[] bytes = null;
            if (DataType.IsNullOrEmpty(token) || DataType.IsNullOrEmpty(mypk) || flowNo == 0) {
                return Return_Info(500, "参数不能为空", "");

            }

            Dev2Interface.Port_LoginByToken(token);
            String tableTrack = "ND" + flowNo + "Track";
            bytes = DBAccess.GetByteFromDB(tableTrack, "MyPk", mypk, DBFile);

            if (bytes == null || bytes.length == 0) {

                return Return_Info(500, "历史公文不存在", "");
            }
            HttpHandlerGlo.DownLoadFileByBytes(response, bytes, DBAccess.GenerGUID() + ".docx");
        } catch (Exception ex) {
            return Return_Info(500, ex.getMessage(), "");
        }
        return Return_Info(500, "历史公文不存在", "");
    }
    @GetMapping(value = "/GenerGongWenByteWPS")
    @ApiOperation("wps获得公文文件流")
    @ResponseBody
    public Object GenerGongWenByteWPS(String token, int workId, int fkNodeId, String gongWenTemplateFile,HttpServletResponse response, HttpServletRequest request) {
        try {
            //生成文件流
            byte[] bytes = null;
            if (DataType.IsNullOrEmpty(token) || workId == 0 || fkNodeId == 0) {
                return Return_Info(500, "参数不能为空", "");

            }
            Dev2Interface.Port_LoginByToken(token);
            Node nd = new Node(fkNodeId);
            Work wk = nd.getHisWork();
            wk.setOID(workId);
            wk.RetrieveFromDBSources();

            bytes = wk.GetFileFromDB(DBFile, "");

            String fileName = "空白模板.docx";
            if (!DataType.IsNullOrEmpty(gongWenTemplateFile))
            {
                fileName = URLDecoder.decode(gongWenTemplateFile, "UTF-8");
            }
            if (bytes == null || bytes.length == 0) {

                String frmVSTOTemplateFilePath = bp.difference.SystemConfig.getPathOfDataUser() + "DocFlow\\" + fileName;
                File fileInfo = new File(frmVSTOTemplateFilePath);

                if (fileInfo.exists()) {
                    bytes = DataType.ConvertByteByFileFullPath(frmVSTOTemplateFilePath);

                } else {
                    return Return_Info(500, "初始化模板文件不存在", "");
                }
            }
            HttpHandlerGlo.DownLoadFileByBytes(response, bytes, DBAccess.GenerGUID() + ".docx");
        } catch (Exception ex) {
            return Return_Info(500, ex.getMessage(), "");
        }
        return Return_Info(500, "公文不存在", "");
    }

    /***
     *
     * @return
     */
    @PostMapping("/SaveGongWenWordWPS")
    @ApiOperation("wps保存公文文")
    @ResponseBody
    public Object SaveGongWenWordWPS(@RequestParam("file") MultipartFile file, String token, int workId, int fkNodeId) {
        try {
            if (DataType.IsNullOrEmpty(token) || workId == 0 || fkNodeId == 0) {
                return Return_Info(500, "参数不能为空", "");
            }
            Dev2Interface.Port_LoginByToken(token);
            byte[] bytes = file.getBytes();
            Node nd = new Node(fkNodeId);
            Work wk = nd.getHisWork();
            wk.setOID(workId);
            int i = wk.RetrieveFromDBSources();
            if (i == 0) {
                throw new Exception("该流程的表单数据不存在");

            }
            wk.SaveFileToDB(DBFile, bytes);
            return "File uploaded successfully";
        } catch (Exception ex) {
            return Return_Info(500, ex.getMessage(), "");
        }
    }

    @GetMapping("/GenerFrmAttachmentByte")
    @ApiOperation("wps获得附件")
    @ResponseBody
    public synchronized Object GenerFrmAttachmentByteWPS(String token, String frmAttachmentMyPk, HttpServletResponse response, HttpServletRequest request) {
        try {
            //生成文件流
            byte[] bytes = null;
            if (DataType.IsNullOrEmpty(frmAttachmentMyPk) || DataType.IsNullOrEmpty(token)) {
                return Return_Info(500, "参数不能为空", "");
            }
            Dev2Interface.Port_LoginByToken(token);

            FrmAttachmentDB frmAttachmentDB = new FrmAttachmentDB();

            frmAttachmentDB.setMyPK(frmAttachmentMyPk);
            int count = frmAttachmentDB.Retrieve();

            if (count == 0) {
                return Return_Info(500, "文件不存在", "");
            }
            FrmAttachment frmAttachment = new FrmAttachment();
            frmAttachment.setMyPK(frmAttachmentDB.getFKFrmAttachment());
            frmAttachment.Retrieve();

            //临时文件
            String temFilePath = "";

            //获取配置项是否加密
            boolean fileEncrypt = SystemConfig.isEnableAthEncrypt();
            ;

            //获取此文件是否加密
            boolean isEncrypt = frmAttachmentDB.GetParaBoolen("IsEncrypt");
            if (frmAttachment.getAthSaveWay() == AthSaveWay.IISServer) {

                temFilePath = frmAttachmentDB.getFileFullName() + ".tmp";
                if (fileEncrypt == true && isEncrypt == true) {
                    File fileTemFilePath = new File(temFilePath);
                    if (fileTemFilePath.exists()) {
                        fileTemFilePath.delete();
                    }
                    AesEncodeUtil.decryptFile(frmAttachmentDB.getFileFullName(), temFilePath);
                } else {
                    bytes = DataType.ConvertByteByFileFullPath(frmAttachmentDB.getFileFullName());
                }
            }

            if (frmAttachment.getAthSaveWay() == AthSaveWay.FTPServer) {
                //下载文件到临时位置
                String orignalFile = frmAttachmentDB.GenerTempFile(frmAttachment.getAthSaveWay());
                temFilePath = orignalFile + ".temp";
                if (fileEncrypt == true && isEncrypt == true) {
                    AesEncodeUtil.decryptFile(orignalFile, temFilePath);
                    File file = new File(orignalFile);

                    //将下载的临时文件删除掉
                    if (file.exists() == true)
                        file.delete();
                } else
                    temFilePath = orignalFile;
            }
            if (frmAttachment.getAthSaveWay() == AthSaveWay.DB) {

                //加密文件处理
                if (fileEncrypt == true && isEncrypt == true) {
                    //根据流生成初试源文件
                    String orignalFile = frmAttachmentDB.GenerTempFile(frmAttachment.getAthSaveWay());
                    temFilePath = orignalFile + ".temp";
                    byte[] bytesEncrypt = frmAttachmentDB.GetFileFromDB(DBFile, null);
                    if (bytesEncrypt == null || bytesEncrypt.length == 0) {
                        throw new Exception("文件不存在");
                    }
                    DataType.getFileByBytes(bytesEncrypt, orignalFile);

                    //根据初试源文件生成解密后的文件
                    AesEncodeUtil.decryptFile(orignalFile, temFilePath);
                    File file = new File(orignalFile);
                    if (file.exists() == true)
                        file.delete();
                } else {
                    //不是加密文件直接读取流
                    bytes = frmAttachmentDB.GetFileFromDB(DBFile, null);
                }
            }

            if (bytes == null) {
                bytes = DataType.ConvertByteByFileFullPath(temFilePath);
            }

            HttpHandlerGlo.DownLoadFileByBytes(response, bytes, frmAttachmentDB.getFileName());

        } catch (Exception ex) {
            return Return_Info(500, ex.getMessage(), "");
        }
        return Return_Info(500, "文件不存在", "");

    }

    @PostMapping("/SaveFrmAttachment")
    @ApiOperation("wps保存附件")
    @ResponseBody
    public synchronized Object SaveFrmAttachmentWPS(@RequestParam("file") MultipartFile multipartFile, String token, String frmAttachmentMyPk) {
        try {
            if (multipartFile == null || DataType.IsNullOrEmpty(token) || DataType.IsNullOrEmpty(frmAttachmentMyPk)) {
                return Return_Info(500, "参数不能为空", "");
            }
            Dev2Interface.Port_LoginByToken(token);

            FrmAttachmentDB frmAttachmentDB = new FrmAttachmentDB();

            frmAttachmentDB.setMyPK(frmAttachmentMyPk);
            int count = frmAttachmentDB.Retrieve();
            if (count == 0) {
                return Return_Info(500, "文件不存在", "");
            }
            FrmAttachment frmAttachment = new FrmAttachment();
            frmAttachment.setMyPK(frmAttachmentDB.getFKFrmAttachment());
            frmAttachment.Retrieve();

            //获取配置项是否加密
            boolean fileEncrypt = SystemConfig.isEnableAthEncrypt();
            String tempFile = "";
            //获取此文件是否加密
            boolean isEncrypt = frmAttachmentDB.GetParaBoolen("IsEncrypt");

            if (frmAttachment.getAthSaveWay() == AthSaveWay.IISServer) {
                if (fileEncrypt == true && isEncrypt == true) {
                    //现将上传的文件转存为临时文件
                    tempFile = frmAttachmentDB.MakeTempFileFullPath() + ".tmp";
                    File filetempFile = new File(tempFile);
                    if (filetempFile.exists()) {
                        filetempFile.delete();
                    }

                    //将上传的文件转为临时文件
                    DataType.getFileByBytes(multipartFile.getBytes(), tempFile);

                    File fileFullName = new File(frmAttachmentDB.getFileFullName());
                    //将原始文件删除掉
                    if (fileFullName.exists() == true)
                        fileFullName.delete();

                    //对临时文件进行加密并输出成新文件
                    AesEncodeUtil.encryptFile(tempFile, frmAttachmentDB.getFileFullName());
                    //删除掉临时文件
                    filetempFile.delete();
                } else {
                    //将原始文件删除掉然后保存新文件
                    File fileFullName = new File(frmAttachmentDB.getFileFullName());
                    if (fileFullName.exists() == true)
                        fileFullName.delete();
                    DataType.getFileByBytes(multipartFile.getBytes(), frmAttachmentDB.getFileFullName());
                }
            }

            //数据库存储
            if (frmAttachment.getAthSaveWay() == AthSaveWay.DB) {
                if (fileEncrypt == true && isEncrypt == true) {
                    //将上传的文件存储为临时文件
                    tempFile = frmAttachmentDB.MakeTempFileFullPath() + ".tmp";
                    File filetempFile = new File(tempFile);
                    if (filetempFile.exists()) {
                        filetempFile.delete();//删除临时文件

                    }
                    //将将上传的文件存为临时文件
                    DataType.getFileByBytes(multipartFile.getBytes(), tempFile);

                    //声明加密的文件路径
                    String tempEncryptFile = tempFile + ".encryptTemp";
                    File fileTempEncryptFile = new File(tempEncryptFile);
                    if (fileTempEncryptFile.exists()) {
                        fileTempEncryptFile.delete();
                    }

                    //根据初试源文件生成加密后的文件
                    AesEncodeUtil.encryptFile(tempFile, tempEncryptFile);

                    //保存流到数据库
                    frmAttachmentDB.SaveFileToDB(DBFile, tempEncryptFile);

                    //删除掉临时加密后的文件
                    if (fileTempEncryptFile.exists()) {
                        fileTempEncryptFile.delete();
                    }
                    if (filetempFile.exists()) {
                        filetempFile.delete();
                    }

                } else {
                    frmAttachmentDB.SaveFileToDB(DBFile, multipartFile.getBytes());
                }
            }

            if (frmAttachment.getAthSaveWay() == AthSaveWay.FTPServer) {
                //建立ftp链接
                String[] stringList = frmAttachmentDB.getFileFullName().split("/");
                String fileName = stringList[stringList.length - 1];

                FtpUtil ftpUtil = bp.wf.Glo.getFtpUtil();
                ftpUtil.openConnection();
                //将上传的文件先转为临时文件
                tempFile = frmAttachmentDB.MakeTempFileFullPath() + ".tmp";
                File filetempFile = new File(tempFile);
                if (filetempFile.exists()) {
                    filetempFile.delete();
                }
                DataType.getFileByBytes(multipartFile.getBytes(), tempFile);
                if (fileEncrypt == true && isEncrypt == true) {

                    //声明加密的文件路径
                    String tempEncryptFile = tempFile + ".encryptTemp";
                    File fileTempEncryptFile = new File(tempEncryptFile);
                    if (fileTempEncryptFile.exists()) {
                        fileTempEncryptFile.delete();
                    }

                    //根据初试源文件生成加密后的文件
                    AesEncodeUtil.encryptFile(tempFile, tempEncryptFile);
                    ftpUtil.changeWorkingDirectory(frmAttachmentDB.getFileFullName().replace(fileName, ""), true);
                    ftpUtil.uploadFile(fileName, tempEncryptFile);

                    if (filetempFile.exists()) {
                        filetempFile.delete();
                    }
                    if (fileTempEncryptFile.exists()) {
                        fileTempEncryptFile.delete();
                    }

                } else {

                    //修改ftp工作目录
                    ftpUtil.changeWorkingDirectory(frmAttachmentDB.getFileFullName().replace(fileName, ""), true);
                    ftpUtil.uploadFile(fileName, tempFile);
                    if (filetempFile.exists()) {
                        filetempFile.delete();
                    }
                }
            }

            return "File uploaded successfully";

        } catch (Exception ex) {
            return Return_Info(500, ex.getMessage(), "");
        }
    }
    @GetMapping("/GetVstoExtensionVersion")
    @ApiOperation("获取vsto版本")
    @ResponseBody
    public Object GetVstoExtensionVersion()
    {
        return Return_Info(200, "执行成功", SystemConfig.getVstoExtensionVersion());
    }

    /**
     * 以下为服务器端判断客户端浏览器类型的方法
     */
    private String getBrowser(HttpServletRequest request) {

        String userAgent = request.getHeader("USER-AGENT").toLowerCase();
        if (userAgent != null) {
            if (userAgent.indexOf("chrome") >= 0)
                return "CE";
            if (userAgent.indexOf("msie") >= 0)
                return "IE";
            if (userAgent.indexOf("firefox") >= 0)
                return "FF";
            if (userAgent.indexOf("safari") >= 0)
                return "SF";
        }
        return null;
    }
    @Override
    public Class getCtrlType() {
        return null;
    }
}
