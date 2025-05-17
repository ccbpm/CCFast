package bp.difference;

import java.util.Enumeration;
import java.util.Properties;

import bp.tools.AesEncodeUtil;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class GvtvPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) {
		Enumeration<?> keys = props.propertyNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = props.getProperty(key);
			if (key.endsWith(".encryption") && null != value) {
				props.remove(key);

				key = key.substring(0, key.toString().length() - 11);

				try {
					value = bp.sys.base.Glo.String_JieMi(value.trim());
				} catch (Exception e) {
					e.printStackTrace();
				}
				props.setProperty(key, value);
			}
			value=AesEncodeUtil.decEnvValue(value.toString());
			props.setProperty(key, value);
			System.setProperty(key, value);
		}

		super.processProperties(beanFactoryToProcess, props);
	}

}
