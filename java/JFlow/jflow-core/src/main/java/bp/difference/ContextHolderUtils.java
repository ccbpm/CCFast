package bp.difference;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import bp.da.DataType;
import bp.difference.rabbitMQ.RabbitUtils;
import bp.pub.PubClass;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import bp.difference.handler.CommonUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.*;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JFlow上下文工具类
 * @version 2016-5-9
 */
public class ContextHolderUtils implements ApplicationContextAware, DisposableBean {

	private static ContextHolderUtils contextHolder;
	private static ApplicationContext springContext;

	// 数据源设置
	private DataSource dataSource;

	private static RedissonClient redisson;

	// 第三方系统session中的用户编码，设置后将不用再调用登录方法，直接获取当前session进行登录。
	private static String userNoSessionKey;

	public synchronized static ContextHolderUtils getInstance() {
		if (contextHolder == null) {
			if(springContext == null){
				ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring-context.xml");
				springContext = ctx;
			}

			contextHolder = springContext.getBean(ContextHolderUtils.class);
		}
		return contextHolder;
	}

	/**
	 * SpringMvc下获取request
	 *
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		HttpServletRequest request = null;
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes != null) {
			ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
			request = servletRequestAttributes.getRequest();
		}
		if (request == null)
			request = CommonUtils.getRequest();
		return request;
	}

	public static HttpServletResponse getResponse() {
        return CommonUtils.getResponse();
	}

	/**
	 * SpringMvc下获取session
	 * @return
	 */
	public static HttpSession getSession() {
		HttpSession session = getRequest().getSession();
		return session;
	}

	public static void addCookie(String name, int expiry, String value) {

		if(getRequest()!= null){
			Cookie cookies[] = getRequest().getCookies() ;
			if (cookies == null) {
				Cookie cookie = new Cookie(name, value);
				cookie.setMaxAge(expiry);
				getResponse().addCookie(cookie);
				getRequest().setAttribute(name,value);
			} else {
				for (Cookie cookie : cookies) {
					if (name.equals(cookie.getName())) {
						cookie.setValue(value);
						cookie.setMaxAge(expiry);
						cookie.setPath("/");
						getResponse().addCookie(cookie);
						return;
					}
				}
				Cookie cookie = new Cookie(name, value);
				cookie.setMaxAge(expiry);
				cookie.setPath("/");
				getResponse().addCookie(cookie);
			}
		}
	}

	public static void addCookie(String name, String value)  {
		int expiry = 60 * 60 * 24 * 8;
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(value);
		if(m.find()) {
			value = "base64:" + Base64.getEncoder().encodeToString(value.getBytes());
		}
		if(getRequest()!= null){
			Cookie[] cookies = getRequest().getCookies() ;
			if (cookies == null) {
				Cookie cookie = new Cookie(name, value);
				cookie.setPath("/");
				cookie.setMaxAge(expiry);
				getResponse().addCookie(cookie);
				getRequest().setAttribute(name,value);
			} else {
				for (Cookie cookie : cookies) {
					if (name.equals(cookie.getName())) {
						cookie.setValue(value);
						cookie.setMaxAge(expiry);
						cookie.setPath("/");
						getResponse().addCookie(cookie);
						return;
					}
				}
				Cookie cookie = new Cookie(name, value);
				cookie.setMaxAge(expiry);
				cookie.setPath("/");
				getResponse().addCookie(cookie);
			}
		}
	}

	public static Cookie getCookie(String name) {
		Cookie cookies[] = getRequest().getCookies();
		if (cookies == null || name == null || name.isEmpty())
			return null;
		for (Cookie cookie : cookies) {
			if (name.equals(cookie.getName())) {
				String val = cookie.getValue();
				if(val.startsWith("base64:")) {
					byte[] result = Base64.getDecoder().decode(val.replace("base64:", ""));
					val = new String(result);
					return new Cookie(cookie.getName(), val);
				}
				return cookie;
			}
		}
		return null;
	}

	public static void deleteCookie(String name) {
		Cookie cookies[] = getRequest().getCookies();
		if (cookies == null || name == null || name.length() == 0)
			return;
		for (Cookie cookie : cookies) {
			if (name.equals(cookie.getName())) {
				cookie.setValue("");
				cookie.setMaxAge(0);
				getResponse().addCookie(cookie);
			}
		}
	}

	public static void clearCookie() {
		Cookie[] cookies = getRequest().getCookies();
		if (null == cookies)
			return;
		for (Cookie cookie : cookies) {
			cookie.setValue("");
			cookie.setMaxAge(0);
			getResponse().addCookie(cookie);

		}
	}

	/**
	 * 获取数据源
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * 获取数据源
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}



	public void setRedisson(RedissonClient redission){
		this.redisson = redission;
	}
	public static RedissonClient getRedisson() {
		return redisson;
	}
	/**
	 * 获取第三方系统session中的用户编码
	 * @return
	 */
	public String getUserNoSessionKey() {
		return userNoSessionKey;
	}

	/**
	 * 设置第三方系统session中的用户编码
	 * param userNoSessionKey
	 */
	public void   setUserNoSessionKey(String userNoSessionKey) {
		this.userNoSessionKey = userNoSessionKey;
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) springContext.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return springContext.getBean(requiredType);
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		ContextHolderUtils.springContext = arg0;
	}
	public ApplicationContext getApplicationContext(){
		return ContextHolderUtils.springContext;
	}
	@Override
	public void destroy() throws Exception {
		ContextHolderUtils.springContext = null;
	}

	public static void ResponseWriteFile(byte[] fileData, String fileName) throws IOException {
		ResponseWriteFile(fileData,fileName, "application/octet-stream");
	}
	public static void ResponseWriteFile(byte[] fileData, String fileName, String contentType) throws IOException {
		HttpServletResponse response = getResponse();
		response.setContentType(contentType+";charset=utf8");
		response.setHeader("Content-Disposition",
				"attachment;filename=" + PubClass.toUtf8String(getRequest(),fileName));
		response.setHeader("Connection", "close");
		response.setHeader("Code", "OK");
		// 读取目标文件，通过response将目标文件写到客户端
		// 读取文件
		OutputStream out = response.getOutputStream();
		out.write(fileData);
		out.close();

	}
	public static void ResponseWriteFile(String filePath, String fileName, String contentType) throws IOException {
		HttpServletResponse response = getResponse();
		response.setContentType(contentType+";charset=utf8");
		String name = PubClass.toUtf8String(getRequest(),fileName);
		if(DataType.IsNullOrEmpty(name) || name.contains(" "))
			throw new RuntimeException("文件名字不合法，含有空字符");
		response.setHeader("Content-Disposition",
				"attachment;filename=" + PubClass.toUtf8String(getRequest(),fileName));
		response.setHeader("Connection", "close");
		response.setHeader("Code", "OK");
		InputStream in = new FileInputStream(new File(filePath));
		OutputStream out = response.getOutputStream();
		// 写文件
		int b;
		while ((b = in.read()) != -1) {
			out.write(b);
		}
		in.close();
		out.close();

	}

	private static RabbitUtils rabbitUtils;
	public void setRabbitUtils(RabbitUtils rabbitUtils) {
		this.rabbitUtils = rabbitUtils;
	}
	public static RabbitUtils getRabbitUtils(){
		return rabbitUtils;
	}
}
