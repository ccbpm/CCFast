package cn.jflow.boot.model;

import lombok.Data;

import java.util.List;

public class Document {

    // 文档是否被编辑和下载的权限
    private Permission permissions;



    public Permission getPermissions() {
        return permissions;
    }

    public void setPermissions(Permission permissions) {
        this.permissions = permissions;
    }
    // 文档服务器唯一标识
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // 文件类型 docx ppt
    private String fileType;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    // 文件名称
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // 文档服务器下载文件的地址
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static class Permission {
        // 文档注释功能
        private Boolean comment = true;
        // 文档copy功能
        private Boolean copy = true;
        // 文档下载功能
        private Boolean download = true;
        // 文档编辑功能（在model为view时这个设置不起作用）
        private Boolean edit = true;
        // 文档打印功能
        private Boolean print = true;
        // 文档填写表单功能
        private Boolean fillForms = true;
        // 文档过滤器功能
        private Boolean modifyFilter = true;
        // 文档更改内容控件设置功能
        private Boolean modifyContentControl = true;
        // 文档审阅模式功能
        private Boolean review = true;
        // 文档聊天功能
        private Boolean chat = true;

        // 定义用户可以接受/拒绝其更改的组
        private List<String> reviewGroups;
        private List<String> userInfoGroups;
    }
}
