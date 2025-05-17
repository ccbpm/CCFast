package cn.jflow.boot.model;

import cn.jflow.boot.Utils.FileUtils;
import cn.jflow.boot.model.Document;
import cn.jflow.boot.model.DocumentType;
import cn.jflow.boot.model.EditConfig;

public class Config {

    // 文档配置项目
    private Document document;
    // 编辑类型
    private String documentType;

    public String getDocumentType() {
        return documentType;
    }

    //文档服务地址
    private String api;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    // 文档编辑器定制化配置项
    private EditConfig editorConfig;
    //显示类型
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EditConfig getEditorConfig() {
        return editorConfig;
    }

    public void setEditorConfig(EditConfig editorConfig) {
        this.editorConfig = editorConfig;
    }

    public static Config build() {
        Config config = new Config();

        EditConfig editorConfig = new EditConfig();
        Document document = new Document();
        EditConfig.Customization customization = new EditConfig.Customization();
        EditConfig.Customization.Logo logo = new EditConfig.Customization.Logo();
        customization.setLogo(logo);
        editorConfig.setCustomization(customization);
        config.setEditorConfig(editorConfig);
        config.setDocument(document);
        return config;
    }

}