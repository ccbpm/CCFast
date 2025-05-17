package cn.jflow.boot.config;

import java.util.Hashtable;
import javax.sql.DataSource;
import bp.difference.rabbitMQ.RabbitUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import bp.difference.ContextHolderUtils;
import bp.difference.GvtvPropertyPlaceholderConfigurer;
import bp.difference.SystemConfig;

/**
 * JFlow配置
 * @author Bryce Han
 *
 */
@Configuration
@ComponentScan(basePackages = {"bp.nbport","bp.difference", "Controller", "cn.jflow.boot"})
public class JFlowConfig {

	private static final Logger logger = LoggerFactory.getLogger(JFlowConfig.class);

	private static ApplicationContext applicationContext;

	@Autowired
	Environment env;
	////@Autowired
	//private RedissonClient redisson;


	/**
	 * 属性文件jflow.properties配置
	 * @return
	 */
	@Bean
	public static GvtvPropertyPlaceholderConfigurer propertyConfigurer() {
		GvtvPropertyPlaceholderConfigurer propertyConfigurer = new GvtvPropertyPlaceholderConfigurer();
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("classpath:jflow.properties");
		propertyConfigurer.setLocation(resource);
		return propertyConfigurer;
	}
	/**
	 * 配置JFlow数据库属性
	 */
	public void loadJFlowDatabaseConfig() {
		//配置jflow属性
		Hashtable<String, Object> props = SystemConfig.getCS_AppSettings();

		String url = env.getProperty("spring.datasource.url");
		/*Hashtable<String, Object> dbProps = configDatabaseParams(url);
		props.putAll(dbProps);*/

		String platform = env.getProperty("spring.datasource.platform");
		String schema = env.getProperty("spring.datasource.name");
		String username = env.getProperty("spring.datasource.username");
		String password = env.getProperty("spring.datasource.password");
		String testQuery = env.getProperty("spring.datasource.hikari.connection-test-query");
		props.put("AppCenterDSN", url);
		props.put("AppCenterDBType", platform);
		props.put("AppCenterDBDatabase", schema);
		props.put("JflowUser", username);
		props.put("JflowPassword", password);
		props.put("JflowTestSql", testQuery);
		props.put("RedisIsEnable",env.getProperty("spring.data.redis.repositories.enabled").equals("true")?1:0);
	}


	/**
	 * JFlow集成上下文工具类
	 * @param dataSource 数据源
	 * @return
	 */
	@Primary
	@Bean
	public ContextHolderUtils jflowContextHolderUtils(DataSource dataSource) {
		loadJFlowDatabaseConfig();
		ContextHolderUtils contextHolderUtils = new ContextHolderUtils();
		contextHolderUtils.setDataSource(dataSource);
		if(applicationContext != null) {
			contextHolderUtils.setApplicationContext(applicationContext);
		}

		return contextHolderUtils;
	}
	@Bean
	@ConditionalOnProperty(name="spring.data.redis.repositories.enabled", havingValue="true", matchIfMissing=false)
	public ContextHolderUtils jflowRedissionContextHolderUtils(RedissonClient redisson) {
		ContextHolderUtils contextHolderUtils = new ContextHolderUtils();
		contextHolderUtils.setRedisson(redisson);
		if(applicationContext != null) {
			contextHolderUtils.setApplicationContext(applicationContext);
		}
		return contextHolderUtils;
	}
	@Bean
	@ConditionalOnProperty(name="spring.rabbitmq.enable", havingValue = "true", matchIfMissing = false)
	public ContextHolderUtils jflowRabbitContextHolderUtils(RabbitUtils rabbitUtils){
		ContextHolderUtils contextHolderUtils = new ContextHolderUtils();
		contextHolderUtils.setRabbitUtils(rabbitUtils);
		if(applicationContext != null) {
			contextHolderUtils.setApplicationContext(applicationContext);
		}
		return contextHolderUtils;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		JFlowConfig.applicationContext = applicationContext;
	}

}
