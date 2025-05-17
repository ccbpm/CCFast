/**
 * Project Name:app-engine
 * File Name:Constant4Comm.java
 * Package Name:com.bwda.engine.common
 * Date:2017年10月19日
 * Copyright (c) 2017, 江苏保旺达软件有限公司 All Rights Reserved.
 *
 */
package bp.tools;
/**
 * 共通常量定义
 *
 * @author fanchengliang
 * @date 2017年10月19日 
 */
public class Constant4Comm {
    
    
    /**
     * 符号.
     */
    public static final String STR_DOT = ".";
    
    /**
     * 符号,
     */
    public static final String STR_COMMA = ",";
    
    /**
     * 符号!
     */
    public static final String STR_IMPORTAMT = "!";
    
    /**
     * 符号*
     */
    public static final String STR_STAR = "*";
    
    /**
     * 符号\\
     */
    public static final String STR_BACK_SLASH = "\\";
    
    /**
     * 符号/
     */
    public static final String STR_SLASH = "/";
    
    /**
     * 符号?
     */
    public static final String STR_QUESTION = "?";
    
    /**
     * 符号&
     */
    public static final String STR_AND = "&";
    
    /**
     * 字符串true
     */
    public static final String STR_TRUE = "true";
    
    /**
     * 字符串false
     */
    public static final String STR_FALSE = "false";
    
    /**
     * 字符串"unknown"
     */
    public static final String STR_UNKNOWN = "unknown";
    
    /**
     * 字符串"null"
     */
    public static final String STR_NULL = "null";
    
    /**
     * 字符串"exception"
     */
    public static final String STR_EXCEPTION = "exception";
    
    /**
     * 字符串"linux"
     */
    public static final String OS_LINUX = "linux";
    
    /**
     * 字符串"windows"
     */
    public static final String OS_WIN = "windows";
    
    /**
     * 字符\
     */
    public static final char CHAR_BACK_SLASH = '\\';
    
    /**
     * 字符-
     */
    public static final char CHAR_MINUS = '-';
    
    /**
     * SQL关键字-"select"
     */
    public static final String SQL_KEY_SELECT= "select";
    
    /**
     * SQL关键字-"select distinct"
     */
    public static final String SQL_KEY_SELECT_DIST= "select distinct";
    
    /**
     * SQL方法名-执行查询.
     */
    public static final String SQL_FUNC_QUERY= "executeQuery";
    
    /**
     * SQL方法名-设置为Null.
     */
    public static final String SQL_FUNC_SETNULL= "setNull";
    
    /**
     * SQL方法名-获得结果集.
     */
    public static final String SQL_FUNC_GET_RESULTSET= "getResultSet";
    
    /**
     * SQL关键字metaParameters
     */
    public static final String SQL_WORD_PARAMS = "metaParameters";
    
    /**
     * SQL方法名-获得更新计数.
     */
    public static final String SQL_FUNC_GET_UPDATE_COUNT= "getUpdateCount";
    
    /**
     * 配置常量-日志是否显示SQL参数
     */
    public static final String CONF_SHOW_SQL_PARAMS_1 = "1";
    
    /**
     * 配置常量-是否允许多账号同时登录
     */
    public static final String CONF_MULTIACCOUNT_LOGIN = "user.multiAccountLogin";
    
    /**
     * 配置常量-初始密码
     */
    public static final String CONF_DEFAULT_PASS4USER = "defaultPass4user";
    
    /**
     * 配置常量-'WEB视图前缀
     */
    public static final String CONF_VIEW_PREFIX = "web.view.prefix";
    
    /**
     * 配置常量-WEB视图后缀
     */
    public static final String CONF_VIEW_SUFFIX = "web.view.suffix";
    
    /**
     * 配置常量-是否允许刷新首页
     */
    public static final String CONF_NOT_ALLOW_REFRESH_INDEX = "notAllowRefreshIndex";
    
    /**
     * 配置常量-#系统是否跳过crsf检查  true：跳过 | false 需要crsf检查
     */
    public static final String CONF_NO_CRSF_CHK = "noCrsfCheck";
    
    /**
     * nginx集群配置，反向代理名(与nginx.conf中proxy_pass(upstream)配置部分)保持一致
     */
    public static final String NGINX_PROXY_PASS = "nginx_proxy_pass";
    
    /**
     * 请求头串
     */
    public static final String XMLREQ_HEAD = "XMLHttpRequest";
    
    /**
     * 请求头cookie
     */
    public static final String REQ_HEAD_COOKIE = "__cookie";
    
    /**
     * 请求头Range
     */
    public static final String REQ_HEAD_RANGE = "Range";
    
    /**
     * 请求头Range
     */
    public static final String REQ_XML_HTTP_REQ = "XMLHttpRequest";
    
    /**
     * 请求方法POST
     */
    public static final String REQ_POST = "POST";
    
    /**
     * 请求方法GET
     */
    public static final String REQ_GET = "GET";
    
    /**
     * 本机请求IP "0:0:0:0:0:0:0:1".
     */
    public static final String REQ_LOCAL_IP = "0:0:0:0:0:0:0:1";
    
    /**
     * 异常信息前缀
     */
    public static final String PREF4EX_MSG = "msg:";
    
    /**
     * JAVA关键词-java包前缀
     */
    public static final String PREF_JAVA = "java.";
    
    /**
     * JAVA关键词-javax包前缀
     */
    public static final String PREF_JAVAX = "javax.";
    
    /**
     * 参数正则类型-INT
     */
    public static final String REG_NAME_INT = "int";
    
    /**
     * 参数正则类型-yesorno
     */
    public static final String REG_NAME_YES_NO = "yesorno";
    
    /**
     * 参数正则类型-year
     */
    public static final String REG_NAME_YEAR = "year";
    
    /**
     * 参数正则类型-date
     */
    public static final String REG_NAME_DATE = "date";
    
    /**
     * 参数正则类型-url
     */
    public static final String REG_NAME_URL = "url";
    
    /**
     * 数据库类型-"oracle"
     */
    public static final String DB_TYPE_ORA = "oracle";
    
    /**
     * SSO模式1--引入整体框架
     */
    public static final String SSO_MODE_ALL = "1";
    
    /**
     * SSO模式0--引入指定页面
     */
    public static final String SSO_MODE_PAGE = "0";
    
}

