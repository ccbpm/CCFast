package cn.jflow.boot.config;

import bp.difference.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    /**
     * 静态资源处理
     **/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if(SystemConfig.isJarRun()==false)
            return;
        String webPath = SystemConfig.getPhysicalPath();

        File file = new File(webPath + "DataUser/Siganture/");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(webPath+"DataUser/UploadFile/");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(webPath+"DataUser/CyclostyleFile");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(webPath+"DataUser/FlowDesc");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(webPath+"DataUser/RichTextFile");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(webPath+"DataUser/Temp");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(webPath+"DataUser/JSLibData");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(webPath+"DataUser/Style/TemplateFoolDevelopDesigner");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(webPath+"DataUser/Bill");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(webPath+"DataUser/HandWritingImg");
        if (!file.exists()) {
            file.mkdirs();
        }
        System.out.println("静态资源处理：" + webPath + "/DataUser/");
        registry.addResourceHandler("/DataUser/Siganture/**").addResourceLocations("file:" + webPath + "/DataUser/Siganture/");
        registry.addResourceHandler("/DataUser/UploadFile/**").addResourceLocations("file:" + webPath + "/DataUser/UploadFile/");
        registry.addResourceHandler("/DataUser/CyclostyleFile/**").addResourceLocations("file:" + webPath + "/DataUser/CyclostyleFile/");
        registry.addResourceHandler("/DataUser/FlowDesc/**").addResourceLocations("file:" + webPath + "/DataUser/FlowDesc/");
        registry.addResourceHandler("/DataUser/InstancePacketOfData/**").addResourceLocations("file:" + webPath + "/DataUser/InstancePacketOfData/");
        registry.addResourceHandler("/DataUser/Temp/**").addResourceLocations("file:" + webPath + "/DataUser/Temp/");
        registry.addResourceHandler("/DataUser/JSLibData/**").addResourceLocations("file:" + webPath + "/DataUser/JSLibData/");
        registry.addResourceHandler("/DataUser/Style/TemplateFoolDevelopDesigner/**").addResourceLocations("file:" + webPath + "/DataUser/Style/TemplateFoolDevelopDesigner/");
        registry.addResourceHandler("/DataUser/Bill/**").addResourceLocations("file:" + webPath + "/DataUser/Bill/");
        registry.addResourceHandler("/DataUser/HandWritingImg/**").addResourceLocations("file:" + webPath + "/DataUser/HandWritingImg/");
    }
}

