package bp.difference.context;

import bp.da.DataType;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class CORSFilter  implements Filter{
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        String origin = req.getHeader("Origin");
        if(origin == null) {
            origin = req.getHeader("Referer");
        }
        if(DataType.IsNullOrEmpty(origin) || origin.contains(" "))
            origin="*";
        res.addHeader("Access-Control-Allow-Origin", origin);
        res.addHeader("Access-Control-Allow-Credentials", "true");

        res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        res.addHeader("Access-Control-Allow-Headers", "Content-Type,X-CAF-Authorization-Token,sessionToken,X-TOKEN");
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        res.setHeader("Access-Control-Max-Age", "0");
        res.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token,Satoken");
        String sid = request.getParameter("SID");
        //if(DataType.IsNullOrEmpty(sid) || sid.contains(" "))
        //    throw new RuntimeException("err@非法的Token."+sid);
        //res.addHeader("Set-Cookie", "SID="+request.getParameter("SID")+";Path=/;DomainExt=localhost;");

        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Credentials", "true");

        ((HttpServletResponse) response).setHeader("XDomainExtRequestAllowed","1");

        if (((HttpServletRequest) request).getMethod().equals("OPTIONS")) {
            //response.getWriter().println("ok");
            return;
        }
        chain.doFilter(request, response);
    }
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // 1允许任何域名使用
        corsConfiguration.addAllowedHeader("*"); // 2允许任何头
        corsConfiguration.addAllowedMethod("*"); // 3允许任何方法（post、get等）
        corsConfiguration.setAllowCredentials(true);//支持安全证书。跨域携带cookie需要配置这个
        corsConfiguration.setMaxAge(3600L);//预检请求的有效期，单位为秒。设置maxage，可以避免每次都发出预检请求
        return corsConfiguration;
    }
    @Override
    public void destroy() {
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
}
