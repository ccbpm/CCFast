package bp.difference.handler;

import java.util.Locale;

import org.springframework.core.io.Resource;

/**
 * 自定义Jstl视图，实现资源有效验证，如果不存在则交给下一个视图解析器解析。
 * @author ThinkGem
 * @version 2014-8-27
 */
public class JFlowJstlView extends org.springframework.web.servlet.view.JstlView {

	@Override
	public boolean checkResource(Locale locale) throws Exception {
		Resource resource = null; 
        try {
        	resource = getApplicationContext().getResource(this.getUrl());
        	if (resource.exists()){
        		return true;
        	}
            logger.debug("View not exists [" + this.getUrl() + "], to access the default view. ");
        } catch (Exception e) { 
            // 什么也不做
        }
        // 返回 false, 以便被下一个 resolver处理
		return false;
	}
	
}
