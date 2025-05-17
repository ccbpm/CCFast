package cn.jflow.boot.model;


public class OnlyOfficeFile {
    private static final long serialVersionUID = 1L;
    /**
     * 文件id
     */
    private String id;

    /**
     * 文件唯一标识
     */
    private String fileKey;

    /**
     * 文件名称
     */
    private String title;

    private  String suffix;

    private String type="desktop";

    /**
     * 回调地址
     */
    private String callbackUr;

    /***
     * api
     */
    private String api;

    /***
     * 文件下载地址
     */
    private String url;

    private  Boolean canEdit;

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getCallbackUr() {
        return callbackUr;
    }

    public void setCallbackUr(String callbackUr) {
        this.callbackUr = callbackUr;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileKey() {
        return fileKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

}
