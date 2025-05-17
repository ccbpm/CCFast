package WebServiceImp;

import java.util.HashMap;
import java.util.Map;

import WebService.PortalInterfaceWSI;

/**
 * 调用接口的地方
 * @author Administrator
 *
 */

public class PortalInterfaceWS  implements PortalInterfaceWSI{
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
	@Override

	public boolean SendToWebServices(String sender, String sendToEmpNo, String title,String msgInfo, String OpenUrl, String msgType)throws Exception{
		return true;
	}

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
	@Override
	public boolean SendToDingDing(String sender, String sendToEmpNo, String title, String msgInfo, String OpenUrl,
			String msgType) throws Exception {
		
		//DingDing dingding = new DingDing();
		//String postJson = dingding.ResponseMsg(sendToEmpNo, "", "", "text", msgInfo);
		//boolean flag = dingding.PostDingDingMsg(postJson,sendToEmpNo);
		//if(flag == false)
		//	throw new Exception("发送消息失败");
		return true;
	}


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
	@Override
	public boolean SendToWeiXin(String sender, String sendToEmpNo, String title, String msgInfo, String OpenUrl,
			String msgType) throws Exception {
		/*WeiXin weiXin = new WeiXin();
		String postJson = weiXin.ResponseMsg(sendToEmpNo, "", "", "text", msgInfo);
		boolean flag = new WeiXin().PostWeiXinMsg(postJson);
		if(flag == false)
			throw new Exception("发送消息失败");*/
		return true;
	}

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
	@Override
	public boolean SendToCCIM(String sender, String sendToEmpNo, String title, String msgInfo, String OpenUrl,
			String msgType) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}
}
