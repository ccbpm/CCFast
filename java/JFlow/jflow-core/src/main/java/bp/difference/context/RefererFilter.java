package bp.difference.context;

import bp.da.DataType;
import bp.da.Log;
import bp.difference.SystemConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
@ConditionalOnProperty(prefix = "security.csrf", name = "enable", havingValue = "true")
@Configuration
public class RefererFilter  implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 不启用或者已忽略的URL不拦截
        boolean enable = SystemConfig.GetValByKeyBoolen("security.csrf.enable",false);
        if (!enable || isExcludeUrl(request.getServletPath())) {
            chain.doFilter(servletRequest, response);
            return;
        }

        String referer = request.getHeader("Referer");
        String serverName = request.getServerName();

        // 判断是否存在外链请求本站
        if (referer == null || !referer.contains(serverName)) {
            Log.DebugWriteError("Referer过滤器 服务器:"+serverName+"当前域名:"+referer);
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        } else {
            chain.doFilter(servletRequest, response);
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * 判断是否为忽略的URL
     * URL路径
     *
     * @return true-忽略，false-过滤
     */
    private boolean isExcludeUrl(String url) {
        String excludes = SystemConfig.GetValByKey("security.csrf.excludes","");
        if (DataType.IsNullOrEmpty(excludes)) {
            return false;
        }
        List<String> urls = Arrays.asList(excludes.trim().split(","));
        for (String uri : urls) {
            // 正则验证
            Pattern p = Pattern.compile("^" + uri);
            if (p.matcher(url).find()) {
                return true;
            }
        }
        return false;
    }

}
