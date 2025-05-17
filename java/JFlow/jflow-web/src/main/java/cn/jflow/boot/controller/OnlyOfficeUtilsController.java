package cn.jflow.boot.controller;

import bp.da.DBAccess;
import bp.da.DataType;
import bp.da.Log;
import bp.da.LogType;
import bp.web.WebUser;
import bp.wf.Dev2Interface;
import bp.wf.Node;
import bp.wf.Work;
import bp.wf.httphandler.HttpHandlerGlo;
import cn.jflow.boot.Utils.HttpUtil;
import cn.jflow.boot.model.*;
import cn.jflow.boot.service.ConfigService;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLDecoder;
import java.util.Hashtable;

@RestController
@Api(tags="onlyoffice相关接口")
@RequestMapping(value = "/WF/OnlyOfficeUtils")
public class OnlyOfficeUtilsController {
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

    /***
     *
     * @param data
     * @return
     */
    public static Object Return_InfoForOnlyOffice(String data)
    {
        Hashtable ht = new Hashtable();
        ht.put("error", data);
        return ht;
    }

    @GetMapping("/downloadFile")
    @ApiOperation("onlyoffice获得公文文件流")
    @ResponseBody
    public  Object downloadFile(String token, int WorkID, int NodeID, String gongWenTemplateFile,HttpServletResponse response) {
        try {
            Log.DefaultLogWriteLine(LogType.Info,"onlyoffice获得公文文件流接口参数token:"+token+"WorkID:"+WorkID+"NodeID:"+NodeID);
            //生成文件流
            byte[] bytes = null;
            if (DataType.IsNullOrEmpty(token) || WorkID == 0 || NodeID == 0) {
                return Return_Info(500, "参数不能为空", "");

            }
            Dev2Interface.Port_LoginByToken(token);
            Node nd = new Node(NodeID);
            Work wk = nd.getHisWork();
            wk.setOID(WorkID);
            wk.RetrieveFromDBSources();

            bytes = wk.GetFileFromDB(DBFile, "");

            String fileName = "/EmptyTemplate.docx";
            if (!DataType.IsNullOrEmpty(gongWenTemplateFile))
            {
                fileName = URLDecoder.decode(gongWenTemplateFile, "UTF-8");
            }
            if (bytes == null || bytes.length == 0) {

                String frmVSTOTemplateFilePath = bp.difference.SystemConfig.getPathOfDataUser() + "DocFlow" + fileName;
                File fileInfo = new File(frmVSTOTemplateFilePath);

                if (fileInfo.exists()) {
                    bytes = DataType.ConvertByteByFileFullPath(frmVSTOTemplateFilePath);

                } else {
                    return Return_Info(500, "初始化模板文件不存在", "");
                }
            }
            HttpHandlerGlo.DownLoadFileByBytes(response, bytes, DBAccess.GenerGUID() + ".docx");
        } catch (Exception ex) {
            System.out.println(ex);
            Log.DefaultLogWriteLine(LogType.Error, ex.getMessage());
            return Return_Info(500, ex.getMessage(), "");
        }
        return Return_Info(200, "", "");
    }

    /**
     * 配置onlyoffice编辑或预览参数
     * @param token
     * @param mode
     * @param WorkID
     * @param NodeID
     * @param gongWenTemplateFile
     * @return
     */
    @GetMapping("/editOnlyOffice")
    public Object editOnlyOffice(String token,String mode, int WorkID,int NodeID,String gongWenTemplateFile){

      try{
          if (DataType.IsNullOrEmpty(token) || WorkID==0 || NodeID == 0 || DataType.IsNullOrEmpty(mode)) {
              return Return_Info(500, "参数不能为空", "");
          }
          Dev2Interface.Port_LoginByToken(token);

          OnlyOfficeFile onlyOfficeFile = new OnlyOfficeFile();
          String guid=DBAccess.GenerGUID();
          onlyOfficeFile.setId(guid);
          onlyOfficeFile.setSuffix("docx");
          onlyOfficeFile.setTitle(guid+".docx");
          onlyOfficeFile.setFileKey(guid);
          onlyOfficeFile.setCanEdit(false);
          onlyOfficeFile.setApi(bp.difference.SystemConfig.getAppSettings().get("onlyoffice_api").toString());

          String url=bp.difference.SystemConfig.getAppSettings().get("onlyoffice_ip").toString()+"/WF/OnlyOfficeUtils/downloadFile?token="+token+"&mode="+mode+"&NodeID="+NodeID+"&WorkID="+WorkID+"&gongWenTemplateFile="+gongWenTemplateFile;
          onlyOfficeFile.setUrl(url);

          String callbackUrl=bp.difference.SystemConfig.getAppSettings().get("onlyoffice_ip").toString()+"/WF/OnlyOfficeUtils/callbackForOnlyOffice?token="+token+"&mode="+mode+"&NodeID="+NodeID+"&WorkID="+WorkID;;
          onlyOfficeFile.setCallbackUr(callbackUrl);

          onlyOfficeFile.setApi(bp.difference.SystemConfig.getAppSettings().get("onlyoffice_api").toString());

          ActionEnum action = mode.equals("view") ? ActionEnum.view : ActionEnum.edit;

          EditConfig.User user = new EditConfig.User();
          user.setFavorite(true);
          user.setId(WebUser.getUserID());
          user.setName(WebUser.getName());
          user.setGroup("default");

          DefaultFileBuilder  defaultFileBuilder=DefaultFileBuilder.builder()
                  .fileName(onlyOfficeFile.getTitle())
                  .onlyOfficeFile(onlyOfficeFile)
                  .action(action)
                  .user(user)
                  .type(onlyOfficeFile.getType())
                  .build();

          Config config=Config.build();
          ConfigService.structureConfig(config,defaultFileBuilder);
          return Return_Info(200,"", JSON.toJSON(config));
      }catch (Exception ex){
          System.out.println(ex);
          Log.DefaultLogWriteLine(LogType.Error, ex.getMessage());
          return Return_Info(500,ex.getMessage(),"");
      }

    }

    /**
     * onlyoffice回调参数
     * @param request
     * @param token
     * @param WorkID
     * @param NodeID
     * @param body
     * @return
     * @throws Exception
     */
    @PostMapping("/callbackForOnlyOffice")
    @ResponseBody
    public Object callbackForOnlyOffice(HttpServletRequest request, String token, int WorkID, int NodeID, @RequestBody CallBackModel body) throws Exception {
        Log.DefaultLogWriteLine(LogType.Info, "============callbackForOnlyOffice回调接口参数body:{}" + body);
        //1 - 正在编辑文档，2 - 文档已准备好保存，3 - 发生文档保存错误4 - 文档已关闭，没有任何更改，6 - 正在编辑文档，但保存了当前文档状态，7 - 强制保存文档时发生错误。
        try {

            if (body.getStatus() == 1) {
                return Return_InfoForOnlyOffice("0");
            }
            if (body.getStatus() == 2) {
                if (DataType.IsNullOrEmpty(token) || WorkID == 0 || NodeID == 0) {
                    return Return_InfoForOnlyOffice("参数不能为空");
                }
                byte[] bytes = HttpUtil.downloadFile(body.getUrl(), null);
                Dev2Interface.Port_LoginByToken(token);

                Node nd = new Node(NodeID);
                Work wk = nd.getHisWork();
                wk.setOID(WorkID);
                int i = wk.RetrieveFromDBSources();
                if (i == 0) {
                    throw new Exception("该流程的表单数据不存在");

                }
                wk.SaveFileToDB(DBFile, bytes);
            }
            return Return_InfoForOnlyOffice("0");
        } catch (Exception ex) {
            System.out.println(ex);
            Log.DefaultLogWriteLine(LogType.Error, ex.getMessage());
            return Return_InfoForOnlyOffice(ex.getMessage());
        }


    }
}
