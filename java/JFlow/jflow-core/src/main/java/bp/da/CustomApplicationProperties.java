package bp.da;

import bp.tools.AesEncodeUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description 项目启动时动态修改配置文件属性  ，
 * 属性value以#CC#开头，加解密工具类：AesEncodeUtil   加密方法：encryptAESHex  解密方法：decryptAESHex  使用默认加密盐和偏移量
 * 需要在 resources目录下建 /META-INF/spring.factories 并在里面配置  org.springframework.boot.env.EnvironmentPostProcessor=bp.da.CustomApplicationProperties
 * @Author 董士明
 **/

public class CustomApplicationProperties implements EnvironmentPostProcessor  {
    private static String env = "";
    private static String mainEnv = "";
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        queryEvn(propertySources);
        execEnv(propertySources,mainEnv);
        execEnv(propertySources,env);
    }
    //获取主和激活的属性文件
    private void queryEvn(MutablePropertySources propertySources){
        Iterator<PropertySource<?>> iterator = propertySources.iterator();
        while(iterator.hasNext()){
            PropertySource<?> next = iterator.next();
            String configFileName = next.getName();
            if(configFileName.contains("application-")){ // spring只会加载当前环境配置
                env = configFileName;
            }
            if(configFileName.contains("application.")){ // 主配置文件 格式： applicationConfig: [classpath:/application.properties]
                mainEnv = configFileName;
            }
        }
    }
    //获取主属性文件
    private void execEnv(MutablePropertySources propertySources,String envStr){
        if(envStr.equals("")){
            return;
        }
          // 获取主配置文件
        String mainName =envStr;
//        String mainName = "Config resource 'class path resource ["+mainEnv+"]' via location 'optional:classpath:/'";
        MapPropertySource propertySource = (MapPropertySource) propertySources.get(mainName);
        Map<String, Object> source = propertySource.getSource();
        Map<String, Object> newConfigMap = new HashMap<>();
        source.forEach((k,v) -> {
            v=AesEncodeUtil.decEnvValue(v.toString());
            newConfigMap.put(k, v.toString());
        });
        changeEnv(propertySources,mainName,newConfigMap);
    }
    private void changeEnv(MutablePropertySources propertySources,String activeName,Map<String, Object> newConfigMap ){
       if(newConfigMap==null || newConfigMap.size()==0){
           return;
       }
        propertySources.replace(activeName, new MapPropertySource(activeName , newConfigMap));
    }
}
