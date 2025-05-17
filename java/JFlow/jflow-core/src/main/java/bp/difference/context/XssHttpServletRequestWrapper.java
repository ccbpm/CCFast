package bp.difference.context;

import bp.tools.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.regex.Pattern;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public XssHttpServletRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;
    }

    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        if (value == null) {
            return null;
        }
        return cleanXSS(value);
    }

    public String getQueryString() {
        String value = super.getQueryString();
        if (value == null) {
            return null;
        }
        return cleanXSS(value);
    }

    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null)
            return null;
        return cleanXSS(value);
    }

    private String cleanXSS(String value) {
        if (StringUtils.isNotBlank(value)) {
            //删除script标签
            Pattern compile = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = compile.matcher(value).replaceAll("");
            compile = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = compile.matcher(value).replaceAll("");
            compile = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = compile.matcher(value).replaceAll("");
            // 删除单个的 </script> 标签
            compile = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = compile.matcher(value).replaceAll("");
            // 删除单个的<script ...> 标签
            compile = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = compile.matcher(value).replaceAll("");
            // 避免 eval(...) 形式表达式
            compile = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = compile.matcher(value).replaceAll("");
            // 避免 e­xpression(...) 表达式
            compile = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = compile.matcher(value).replaceAll("");
            // 避免 javascript: 表达式
            compile = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = compile.matcher(value).replaceAll("");
            // 避免 vbscript:表达式
            compile = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            value = compile.matcher(value).replaceAll("");
            value = cleanEventAttact(value);
            //替换特殊标签
            value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        }
        return value;
    }

    /**
     * 屏蔽页面注入的所有html事件攻击
     *
     * param value
     * @return
     */
    public String cleanEventAttact(String value) {
        //避免οnclick= 表达式
        Pattern compile = Pattern.compile("onafterprint(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onbeforeprint(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onbeforeunload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onhaschange(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onmessage(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onoffline(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("ononline(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onpagehide(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onpageshow(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onpopstate(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onredo(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onresize(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onstorage(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onundo(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onunload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onblur(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onchange(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("oncontextmenu(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onfocus(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onformchange(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onforminput(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("oninput(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("oninvalid(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onreset(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onselect(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onsubmit(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onkeydown(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onkeypress(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onkeyup(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onclick(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("ondblclick(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("ondrag(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("ondragend(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("ondragenter(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("ondragleave(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("ondragover(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("ondragstart(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("ondrop(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onmousedown(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onmousemove(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onmouseout(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onmouseover(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onmouseenter(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onmouseup(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onmousewheel(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        compile = Pattern.compile("onscroll(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = compile.matcher(value).replaceAll("");
        value = value.replace("document", "");//页面屏蔽document字样
        value = value.replace("alert", "");//页面屏蔽alert字样
        return value;
    }
}
