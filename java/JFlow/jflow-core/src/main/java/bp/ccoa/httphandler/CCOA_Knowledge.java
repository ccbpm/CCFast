package bp.ccoa.httphandler;

import bp.ccoa.knowledgemanagement.Knowledge;
import bp.da.DBAccess;
import bp.da.DataType;
import bp.difference.ContextHolderUtils;
import bp.difference.SystemConfig;
import bp.difference.handler.CommonFileUtils;
import bp.sys.FrmAttachmentDB;
import bp.tools.Json;
import bp.web.WebUser;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Hashtable;

/**
 * 知识库页面功能实体
 */
public class CCOA_Knowledge extends bp.difference.handler.DirectoryPageBase{

    /**
     * 单附件上传
     * @return
     * @throws Exception
     */
    public final String Knowledge_UploadFile_SingerAth() throws Exception {
        HttpServletRequest request = ContextHolderUtils.getRequest();
        String contentType = request.getContentType();
        String fileName = "";
        String basePath = SystemConfig.getPathOfDataUser();
        if (SystemConfig.isJarRun() == true)
            basePath = SystemConfig.getPhysicalPath() + "DataUser" + File.separator;
        //上传附件
        String filepath = "";
        if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
            MultipartHttpServletRequest multipartRequest = CommonFileUtils.getMultipartHttpServletRequest(request);
            //String parasData = multipartRequest.getParameter("parasData");
            MultipartFile item = multipartRequest.getFile("file");
            if (item == null)
                return "err@获取附件信息有误.";
            filepath = "UploadFile/Knowledge/" + GetRequestVal("No");
            File file = new File(basePath + filepath);
            if (file.exists() == false) {
                file.mkdirs();
            }
            // 获取文件名
            fileName = item.getOriginalFilename();
            if (fileName.indexOf("/") > -1) {
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                fileName = fileName.replace(" ", "");
            }
            filepath = filepath + "/" + DBAccess.GenerGUID() + fileName;
            try {
                CommonFileUtils.upload(request, "file", new File(basePath + filepath));
            } catch (Exception e) {
                e.printStackTrace();
                return "err@执行失败";
            }
        }
        Knowledge knowledge = new Knowledge(GetRequestVal("No"));
        if (DBAccess.IsExitsTableCol("OA_Knowledge", "MyFileName") == false) {
            knowledge.CheckPhysicsTable();
        }
        String fileName1 = fileName.substring(0, fileName.lastIndexOf("."));
        knowledge.SetValByKey("MyFileName", fileName1);
        knowledge.SetValByKey("MyFileSize", DataType.PraseToKB(new File(basePath + filepath).length()));
        knowledge.SetValByKey("MyFilePath", "/DataUser/" + filepath);
        knowledge.SetValByKey("MyFileExt", fileName.replace(fileName1, ""));
        knowledge.Update();
        return "附件保存成功";
    }

    /**
     * 多附件上传
     * @return
     * @throws Exception
     */
    public final String Knowledge_UploadFile_MinAth() throws Exception {
        String refPKVal = GetRequestVal("RefPKVal");
        //Knowledge_MinAth表示知识库的多附件上传类型
        String sort = "Knowledge_MinAth";
        HttpServletRequest request = ContextHolderUtils.getRequest();
        String contentType = request.getContentType();
        String basePath = SystemConfig.getPathOfDataUser();
        //Java翻译时需要加isJarRun判断
        if (SystemConfig.isJarRun() == true)
            basePath = SystemConfig.getPhysicalPath() + "DataUser" + File.separator;
        //上传附件
        if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                // 获取文件名
                String fileName = part.getSubmittedFileName();
                // 获取文件后缀
                String exts = "";
                if (fileName != null && !fileName.isEmpty()) {
                    if(fileName.lastIndexOf(".") != -1){
                        exts = fileName.substring(fileName.lastIndexOf(".") + 1);
                    }else {
                        exts = "无后缀";
                    }
                    String guid = DBAccess.GenerGUID();

                    //获得输入流
                    InputStream fileContent = part.getInputStream();

                    String filepath = "UploadFile/Knowledge/" + refPKVal;
                    File file = new File(basePath + filepath);
                    if (file.exists() == false) {
                        file.mkdirs();
                    }
                    filepath = filepath + "/" + guid + "." + fileName;
                    // 创建输出流
                    OutputStream outputStream = new FileOutputStream(new File(basePath + filepath));
                    try {
                        // 将文件内容写入输出流
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fileContent.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("err@上传文件异常，执行失败。");
                    } finally {
                        // 关闭输入输出流
                        fileContent.close();
                        outputStream.close();
                    }

                    //保存实体
                    FrmAttachmentDB dbUpload = new FrmAttachmentDB();
                    dbUpload.setMyPK(guid);
                    dbUpload.setRefPKVal(refPKVal);

                    dbUpload.setSort(sort);
                    dbUpload.setFileFullName("/DataUser/" + filepath);
                    dbUpload.setFileName(fileName);
                    dbUpload.setFileExts(exts);
                    dbUpload.setFileSize(part.getSize());
                    dbUpload.setRDT(DataType.getCurrentDataTime());
                    dbUpload.setRec(WebUser.getNo());
                    dbUpload.setRecName(WebUser.getName());
                    dbUpload.setDeptNo(WebUser.getDeptNo());
                    dbUpload.setDeptName(WebUser.getDeptName());
                    dbUpload.setUploadGUID(guid);

                    //执行插入
                    dbUpload.Insert();
                }
            }
        }

        return Return_Info(200, "附件上传成功", "");
    }

    private String Return_Info(int code, String msg, String data)
    {
        Hashtable ht = new Hashtable();
        ht.put("code", code);
        ht.put("message", msg);
        ht.put("data", data);
        return Json.ToJson(ht);
    }

}
