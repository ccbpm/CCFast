package cn.jflow.boot.service;

import cn.jflow.boot.Utils.FileUtils;
import cn.jflow.boot.model.*;

import java.util.HashMap;

public class ConfigService {
    public static void structureConfig(Config config, DefaultFileBuilder wrapper) {

        config.setApi(wrapper.getOnlyOfficeFile().getApi());

        DocumentType documentType = FileUtils.getDocumentType(wrapper.getOnlyOfficeFile().getSuffix());
        config.setDocumentType(documentType.name());
        config.setType(wrapper.getType());
        // TODO 根据用户设置权限
        Document.Permission userPermissions22 = new Document.Permission();
        String fileName = wrapper.getFileName();

        // 设置文件名称
        config.getDocument().setTitle(fileName);
        // 设置访问地址
        config.getDocument().setUrl(wrapper.getOnlyOfficeFile().getUrl());
        config.getDocument().setFileType(wrapper.getOnlyOfficeFile().getSuffix());
        config.getDocument().setKey(wrapper.getOnlyOfficeFile().getFileKey());
        config.getDocument().setPermissions(userPermissions22);

        //设置editConfig
         EditConfig editConfig=config.getEditorConfig();

        // 设置回调接口
        editConfig.setCallbackUrl(wrapper.getOnlyOfficeFile().getCallbackUr());
        editConfig.setLang("zh"); // 语言，写死不允许修改

        Boolean canEdit = wrapper.getOnlyOfficeFile().getCanEdit();
        ActionEnum action = wrapper.getAction();
        editConfig.setCoEditing(action.equals(ActionEnum.view) ? new HashMap<String, Object>() {{
            put("mode", "strict");
            put("change", false);
        }} : new HashMap<String, Object>() {{
            put("mode", "fast");
            put("change", true);
        }});

        // 设置Model
        editConfig.setMode(canEdit && !action.equals(ActionEnum.view) ? EditConfig.Mode.edit.name(): EditConfig.Mode.view.name());
        editConfig.setUser(wrapper.getUser());
    }
}
