package WebService;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * 调用接口的地方
 * @author Administrator
 *
 */
@WebService
public interface PortalInterfaceWSI {
	/**
	 * 发送消息接口
	 * @param sender 发送者
	 * @param sendToEmpNo 接收者
	 * @param title 标题
	 * @param msgInfo 内容
	 * @param OpenUrl 详情URL
	 * @param msgType 消息类型
	 * @return
	 * @throws Exception
	 */
	@WebMethod
	public boolean SendToWebServices(String sender, String sendToEmpNo, String title,String msgInfo, String OpenUrl, String msgType)throws Exception;
	/**
	 * 发送到钉钉
	 * @param sender
	 * @param sendToEmpNo
	 * @param title
	 * @param msgInfo
	 * @param OpenUrl
	 * @param msgType
	 * @return
	 * @throws Exception
	 */
	public boolean SendToDingDing(String sender, String sendToEmpNo, String title,String msgInfo, String OpenUrl, String msgType)throws Exception;
	
	/**
	 * 发送到微信
	 * @param sender
	 * @param sendToEmpNo
	 * @param title
	 * @param msgInfo
	 * @param OpenUrl
	 * @param msgType
	 * @return
	 * @throws Exception
	 */
	public boolean SendToWeiXin(String sender, String sendToEmpNo, String title,String msgInfo, String OpenUrl, String msgType)throws Exception;
	
	/**
	 * 发送到即时通
	 * @param sender
	 * @param sendToEmpNo
	 * @param title
	 * @param msgInfo
	 * @param OpenUrl
	 * @param msgType
	 * @return
	 * @throws Exception
	 */
	public boolean SendToCCIM(String sender, String sendToEmpNo, String title,String msgInfo, String OpenUrl, String msgType)throws Exception;
	
}
