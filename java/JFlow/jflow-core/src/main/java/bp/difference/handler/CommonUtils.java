package bp.difference.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

/**
 * 工具类的父类。封装框架的特定实现,SpringMvc的实现
 * @author asus
 *
 */
public class CommonUtils {
	/**
	 * SpringMvc下获取request
	 * 
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		try {
			RequestAttributes ra = RequestContextHolder.currentRequestAttributes();
			if (ra instanceof ServletRequestAttributes){
				HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
				//如果是文件上传的Request，需要强制转换为DefaultMultipartHttpServletRequest
				if(request.getAttribute("multipartRequest") != null){
					return (DefaultMultipartHttpServletRequest)(request.getAttribute("multipartRequest"));
				}else{
					return ((ServletRequestAttributes) ra).getRequest();
				}
			}
			else if (ra instanceof bp.difference.context.ServletRequestAttributes){
				HttpServletRequest request = ((bp.difference.context.ServletRequestAttributes)ra).getRequest();
				//如果是文件上传的Request，需要强制转换为DefaultMultipartHttpServletRequest
				if(request.getAttribute("multipartRequest") != null){
					return (DefaultMultipartHttpServletRequest)(request.getAttribute("multipartRequest"));
				}else{
					return ((bp.difference.context.ServletRequestAttributes)ra).getRequest();
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static HttpServletResponse getResponse() {
		try {
			RequestAttributes ra = RequestContextHolder.currentRequestAttributes();
			if (ra instanceof ServletRequestAttributes){
				return (HttpServletResponse)ra.getClass().getMethod("getResponse").invoke(ra);
			}
			else if (ra instanceof bp.difference.context.ServletRequestAttributes){
				return ((bp.difference.context.ServletRequestAttributes)ra).getResponse();
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
