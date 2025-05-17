package cn.jflow.boot.model;

import lombok.Data;

@Data
public class CallBackModel {
    // 文件类型 docx
    private String filetype;
    // 最新文档下载地址
    private String url;
    // 唯一key值
    private String key;
    // ...
    private String changesurl;
    // token
    private String token;
    // 定义执行强制保存请求时启动器的类型
    private Integer forcesavetype;
    // 当前的操作状态
    private Integer status;
}
