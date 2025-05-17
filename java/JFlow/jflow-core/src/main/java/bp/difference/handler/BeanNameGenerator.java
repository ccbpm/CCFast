package bp.difference.handler;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * Spring Bean 名生成方式
 * @author ThinkGem
 * @version 2016年5月20日
 */
public class BeanNameGenerator extends AnnotationBeanNameGenerator {

	public BeanNameGenerator() {
		super();
	}

	@Override
	protected String buildDefaultBeanName(BeanDefinition definition) {
		return definition.getBeanClassName();
	}

}