package cn.jflow.boot.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class EditConfig {
    /**
     * 定义文档服务器接收到数据后，处理后进行回调接口【系统交换接口】
     * 。。。。
     */
    private String callbackUrl;
    // 文档共同编辑功能
    private HashMap<String, Object> coEditing = null;
    // 文档新建默认模板，搭配模板使用
    private String createUrl;
    // 文档编辑器自定义界面
    private Customization customization;

    // 文档语言，默认中文即可无需跟改
    private String lang = "zh";
    // 文档打开模式
    private String mode;
    //文档服务地址
    private String api;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    // 文档操作用户
    private EditConfig.User user;
    public enum Mode {
        edit,
        view
    }
    @Data
    public static class Customization {
        // 文档编辑器logo
        private Logo logo;
        // 文档编辑器自动保存功能
        private Boolean autosave = false;
        // 文档编辑器Comments菜单按钮
        private Boolean comments = true;
        // 文档编辑器菜单显示在最上放
        private Boolean compactHeader = false;
        // 文档编辑器toolbar显示模式
        private Boolean compactToolbar = false;
        // 文档编辑器使用OOXML格式兼容的功能，开启会缺失注释功能
        private Boolean compatibleFeatures = false;
        // 文档编辑器中保存文档时即调用回调接口
        private Boolean forcesave = false;
        // 文档编辑器显示帮助功能
        private Boolean help = false;
        // 文档编辑器第一次进行隐藏右边菜单
        private Boolean hideRightMenu = false;
        // 文档编辑器隐藏尺子
        private Boolean hideRulers = false;
        // 文档编辑器隐藏Submit按钮
        private Boolean submitForm = false;
        private Boolean about = true;
        private Boolean feedback = true;

        @Data
        public static class Logo {
            // 用户图片
            private String image;
            // 用户图片
            private String imageEmbedded;
            // 点击时的跳转地址
            private String url;
        }

    }
    @Data
    public static class User {
        private String id;
        private String name;
        private String group;
        private Boolean favorite;

        public void configure(int idParam, String nameParam, String groupParam) {
            this.id = "uid-" + idParam;
            this.name = nameParam;
            this.group = groupParam;
        }
    }
}
