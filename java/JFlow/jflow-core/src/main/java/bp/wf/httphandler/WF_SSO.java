package bp.wf.httphandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import bp.da.*;
import bp.web.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

/**
 页面功能实体
 */
public class WF_SSO extends bp.difference.handler.DirectoryPageBase
{
    /**
     构造函数
     */
    public WF_SSO()
    {
    }

    //C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
    ///#region  界面 .
    public final String SSO_Init()
    {
        if (DataType.IsNullOrEmpty(WebUser.getNo()))
        {
            Hashtable ht = new Hashtable();
            ht.put("code", 200);
            ht.put("SSOPath", bp.difference.SystemConfig.GetValByKey("SSOPath", ""));
            ht.put("JumpSSOServicePath", bp.difference.SystemConfig.GetValByKey("JumpSSOServicePath", ""));
            return bp.tools.Json.ToJson(ht);
        }
        return "info@已经登陆.";
    }
    public final String SSO_Callback() throws ParserConfigurationException {

        String ticket = this.GetRequestVal("Ticket");
        return SSO_LoginByST(ticket);
    }

    private String SSO_LoginByST(String ticket)
    {
        Hashtable ht = new Hashtable();
        //獲取验证ST路徑
        String SSOPathST = bp.difference.SystemConfig.GetValByKey("SSOPathST", "");
        String ssoRedirectPath = bp.difference.SystemConfig.GetValByKey("SSORedirectPath", "");
        if (DataType.IsNullOrEmpty(SSOPathST))
        {
            return "err@沒有配置ST验证路径";
        }
        //验证ST
        String apiUrl = SSOPathST + "?ticket=" + ticket + "&service=" + bp.difference.SystemConfig.GetValByKey("JumpSSOServicePath",   "");
        String xmlString = bp.tools.PubGlo.HttpGet(apiUrl);

        try
        {
            // 创建xml解析对象
            SAXReader reader = new SAXReader();
            // 定义一个文档
            Document document = null;
            // 将字符串转换为
            document = reader.read(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
            Element root = document.getRootElement();

            if(root.elements("authenticationSuccess") != null){
                List<Element> childElements = root.elements("authenticationSuccess");
                Element child = childElements.get(0);
                String userNo = child.elementText("user");
                //执行登录
                bp.wf.Dev2Interface.Port_Login(userNo);
                String token = bp.wf.Dev2Interface.Port_GenerToken("PC");
                //登录成功返回的参数
                ht.put("code", 200);
                ht.put("Token", token);
                ht.put("SSORedirectPath", ssoRedirectPath);
                return bp.tools.Json.ToJson(ht);
            } else
            {
                //登录失败，返回错误提示信息
                ht.put("code", 500);
                ht.put("msg", "ST无效，" + root.elements("authenticationFailure").get(0));
                return bp.tools.Json.ToJson(ht);
            }
        }
        catch (RuntimeException e)
        {
            //返回异常
            ht.put("code", 500);
            ht.put("msg", e.getMessage());
            return bp.tools.Json.ToJson(ht);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
    ///#endregion 界面方法.
    public final String SSO_Logout() throws Exception {
        WebUser.Exit();
        //登出sso路径
        String SSOPath = bp.difference.SystemConfig.GetValByKey("SSOPath", "");
        String logoutUrl = SSOPath + "/logout?service=" + bp.difference.SystemConfig.GetValByKey("JumpSSOServicePath",   "");
        //bp.tools.PubGlo.HttpGet(apiUrl);
        Hashtable ht = new Hashtable();
        ht.put("code", 200);
        ht.put("LogoutUrl", logoutUrl);
        return bp.tools.Json.ToJson(ht);
    }
}

