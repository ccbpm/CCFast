package bp.difference.handler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bp.da.DataType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import bp.da.Log;
import bp.tools.qiyeweixin.aes.WXBizMsgCrypt;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import bp.difference.SystemConfig;


@Controller
@RequestMapping("/WF/WXZFH")
@Scope("request")
public class WXGZHController {
    /**
     * 接收消息服务器配置 URL
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "Start", method = RequestMethod.GET)
    public void start(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Log.DebugWriteInfo("开始签名校验");

        String sToken = SystemConfig.getWX_WeiXinToken();
        String sEncodingAESKey = SystemConfig.getWX_EncodingAESKey();
        String sCorpID = SystemConfig.getWX_CorpID();
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);

        Log.DebugWriteInfo("All Parameter: " + request.getParameterMap().toString());
        //得到服务器传过来的4个参数
        String sVerifyMsgSig = request.getParameter("msg_signature");
        Log.DebugWriteInfo("sVerifyMsgSig: " + sVerifyMsgSig);

        String sVerifyTimeStamp = request.getParameter("timestamp");
        Log.DebugWriteInfo("sVerifyTimeStamp: " + sVerifyTimeStamp);

        String sVerifyNonce = request.getParameter("nonce");
        Log.DebugWriteInfo("sVerifyNonce: " + sVerifyNonce);

        String sVerifyEchoStr = request.getParameter("echostr");
        Log.DebugWriteInfo("sVerifyEchoStr: " + sVerifyEchoStr);

        String sEchoStr = verifyURL(wxcpt, sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, sVerifyEchoStr);
        if (DataType.IsNullOrEmpty(sEchoStr)) {
            response.getWriter().println("校验失败");
        } else {
            response.getWriter().println(sEchoStr);
        }
    }

    /**
     * ------------使用示例一：验证回调URL---------------
     * *企业开启回调模式时，企业微信会向验证url发送一个get请求
     * 假设点击验证时，企业收到类似请求：
     * * GET /cgi-bin/wxpush?msg_signature=5c45ff5e21c57e6ad56bac8758b79b1d9ac89fd3&timestamp=1409659589&nonce=263014780&echostr=P9nAzCzyDtyTWESHep1vC5X9xho%2FqYX3Zpb4yKa9SKld1DsH3Iyt3tP3zNdtp%2B4RPcs8TgAE7OaBO%2BFZXvnaqQ%3D%3D
     * * HTTP/1.1 Host: qy.weixin.qq.com
     * <p>
     * 接收到该请求时，企业应		1.解析出Get请求的参数，包括消息体签名(msg_signature)，时间戳(timestamp)，随机数字串(nonce)以及企业微信推送过来的随机加密字符串(echostr),
     * 这一步注意作URL解码。
     * 2.验证消息体签名的正确性
     * 3. 解密出echostr原文，将原文当作Get请求的response，返回给企业微信
     * 第2，3步可以用企业微信提供的库函数VerifyURL来实现。
     *
     * @param wxcpt
     * @param sVerifyMsgSig
     * @param sVerifyTimeStamp
     * @param sVerifyNonce
     * @param sVerifyEchoStr
     * @return 输出echostr原文
     */
    public String verifyURL(WXBizMsgCrypt wxcpt, String sVerifyMsgSig, String sVerifyTimeStamp, String sVerifyNonce, String sVerifyEchoStr) {

        // 解析出url上的参数值如下：

        String sEchoStr; //需要返回的明文
        try {
            sEchoStr = wxcpt.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp,
                    sVerifyNonce, sVerifyEchoStr);
            System.out.println("verifyurl echostr: " + sEchoStr);
            // 验证URL成功，将sEchoStr返回
            // HttpUtils.SetResponse(sEchoStr);
            return sEchoStr;
        } catch (Exception e) {
            //验证URL失败，错误原因请查看异常
            Log.DebugWriteError(e);
            return null;
        }
    }

    /**
     * ------------使用示例二：对用户回复的消息解密---------------
     * 用户回复消息或者点击事件响应时，企业会收到回调消息，此消息是经过企业微信加密之后的密文以post形式发送给企业，密文格式请参考官方文档
     * 假设企业收到企业微信的回调消息如下：
     * POST /cgi-bin/wxpush? msg_signature=477715d11cdb4164915debcba66cb864d751f3e6&timestamp=1409659813&nonce=1372623149 HTTP/1.1
     * Host: qy.weixin.qq.com
     * Content-Length: 613
     * <xml>		<ToUserName><![CDATA[wx5823bf96d3bd56c7]]></ToUserName><Encrypt><![CDATA[RypEvHKD8QQKFhvQ6QleEB4J58tiPdvo+rtK1I9qca6aM/wvqnLSV5zEPeusUiX5L5X/0lWfrf0QADHHhGd3QczcdCUpj911L3vg3W/sYYvuJTs3TUUkSUXxaccAS0qhxchrRYt66wiSpGLYL42aM6A8dTT+6k4aSknmPj48kzJs8qLjvd4Xgpue06DOdnLxAUHzM6+kDZ+HMZfJYuR+LtwGc2hgf5gsijff0ekUNXZiqATP7PF5mZxZ3Izoun1s4zG4LUMnvw2r+KqCKIw+3IQH03v+BCA9nMELNqbSf6tiWSrXJB3LAVGUcallcrw8V2t9EL4EhzJWrQUax5wLVMNS0+rUPA3k22Ncx4XXZS9o0MBH27Bo6BpNelZpS+/uh9KsNlY6bHCmJU9p8g7m3fVKn28H3KDYA5Pl/T8Z1ptDAVe0lXdQ2YoyyH2uyPIGHBZZIs2pDBS8R07+qN+E7Q==]]></Encrypt>
     * <AgentID><![CDATA[218]]></AgentID>
     * </xml>
     * <p>
     * 企业收到post请求之后应该		1.解析出url上的参数，包括消息体签名(msg_signature)，时间戳(timestamp)以及随机数字串(nonce)
     * 2.验证消息体签名的正确性。
     * 3.将post请求的数据进行xml解析，并将<Encrypt>标签的内容进行解密，解密出来的明文即是用户回复消息的明文，明文格式请参考官方文档
     * 第2，3步可以用企业微信提供的库函数DecryptMsg来实现。
     *
     * @param wxcpt
     * @param sReqMsgSig
     * @param sReqTimeStamp
     * @param sReqNonce
     * @param sReqData
     */
    public void decryptMsg(WXBizMsgCrypt wxcpt, String sReqMsgSig, String sReqTimeStamp, String sReqNonce, String sReqData) {

        // String sReqMsgSig = HttpUtils.ParseUrl("msg_signature");
        //String sReqMsgSig = "477715d11cdb4164915debcba66cb864d751f3e6";
        // String sReqTimeStamp = HttpUtils.ParseUrl("timestamp");
        //String sReqTimeStamp = "1409659813";
        // String sReqNonce = HttpUtils.ParseUrl("nonce");
        //String sReqNonce = "1372623149";
        // post请求的密文数据
        // sReqData = HttpUtils.PostData();
        //String sReqData = "<xml><ToUserName><![CDATA[wx5823bf96d3bd56c7]]></ToUserName><Encrypt><![CDATA[RypEvHKD8QQKFhvQ6QleEB4J58tiPdvo+rtK1I9qca6aM/wvqnLSV5zEPeusUiX5L5X/0lWfrf0QADHHhGd3QczcdCUpj911L3vg3W/sYYvuJTs3TUUkSUXxaccAS0qhxchrRYt66wiSpGLYL42aM6A8dTT+6k4aSknmPj48kzJs8qLjvd4Xgpue06DOdnLxAUHzM6+kDZ+HMZfJYuR+LtwGc2hgf5gsijff0ekUNXZiqATP7PF5mZxZ3Izoun1s4zG4LUMnvw2r+KqCKIw+3IQH03v+BCA9nMELNqbSf6tiWSrXJB3LAVGUcallcrw8V2t9EL4EhzJWrQUax5wLVMNS0+rUPA3k22Ncx4XXZS9o0MBH27Bo6BpNelZpS+/uh9KsNlY6bHCmJU9p8g7m3fVKn28H3KDYA5Pl/T8Z1ptDAVe0lXdQ2YoyyH2uyPIGHBZZIs2pDBS8R07+qN+E7Q==]]></Encrypt><AgentID><![CDATA[218]]></AgentID></xml>";

        try {
            String sMsg = wxcpt.DecryptMsg(sReqMsgSig, sReqTimeStamp, sReqNonce, sReqData);
            System.out.println("after decrypt msg: " + sMsg);
            // TODO: 解析出明文xml标签的内容进行处理
            // For example:
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(sMsg);
            InputSource is = new InputSource(sr);
            Document document = db.parse(is);

            Element root = document.getDocumentElement();
            NodeList nodelist1 = root.getElementsByTagName("Content");
            String Content = nodelist1.item(0).getTextContent();
            System.out.println("Content：" + Content);

        } catch (Exception e) {
            // TODO
            // 解密失败，失败原因请查看异常
            e.printStackTrace();
        }
    }

    /**
     * ------------使用示例三：企业回复用户消息的加密---------------
     * 企业被动回复用户的消息也需要进行加密，并且拼接成密文格式的xml串。
     * 假设企业需要回复用户的明文如下：
     * <xml>
     * <ToUserName><![CDATA[mycreate]]></ToUserName>
     * <FromUserName><![CDATA[wx5823bf96d3bd56c7]]></FromUserName>
     * <CreateTime>1348831860</CreateTime>
     * <MsgType><![CDATA[text]]></MsgType>
     * <Content><![CDATA[this is a test]]></Content>
     * <MsgId>1234567890123456</MsgId>
     * <AgentID>128</AgentID>
     * </xml>
     * <p>
     * 为了将此段明文回复给用户，企业应：			1.自己生成时间时间戳(timestamp),随机数字串(nonce)以便生成消息体签名，也可以直接用从企业微信的post url上解析出的对应值。
     * 2.将明文加密得到密文。	3.用密文，步骤1生成的timestamp,nonce和企业在企业微信设定的token生成消息体签名。			4.将密文，消息体签名，时间戳，随机数字串拼接成xml格式的字符串，发送给企业。
     * 以上2，3，4步可以用企业微信提供的库函数EncryptMsg来实现。
     *
     * @param wxcpt
     * @param sRespData
     * @param sReqTimeStamp
     * @param sReqNonce
     */
    public void encryptMsg(WXBizMsgCrypt wxcpt, String sRespData, String sReqTimeStamp, String sReqNonce) {

        //String sRespData = "<xml><ToUserName><![CDATA[mycreate]]></ToUserName><FromUserName><![CDATA[wx5823bf96d3bd56c7]]></FromUserName><CreateTime>1348831860</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[this is a test]]></Content><MsgId>1234567890123456</MsgId><AgentID>128</AgentID></xml>";
        try {
            String sEncryptMsg = wxcpt.EncryptMsg(sRespData, sReqTimeStamp, sReqNonce);
            System.out.println("after encrypt sEncrytMsg: " + sEncryptMsg);
            // 加密成功
            // TODO:
            // HttpUtils.SetResponse(sEncryptMsg);
        } catch (Exception e) {
            e.printStackTrace();
            // 加密失败
        }
    }

}
