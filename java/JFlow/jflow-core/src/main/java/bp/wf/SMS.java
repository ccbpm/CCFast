package bp.wf;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.en.*; import bp.en.Map;
import bp.web.*;
import bp.sys.*;
import bp.wf.template.PushMsg;
import bp.wf.template.PushMsgAttr;
import bp.wf.template.PushMsgs;

/** 
 消息
*/
public class SMS extends EntityMyPK
{

		///#region 新方法 2013
	/** 
	 发送消息
	 
	 @param userNo 接受人
	 @param msgTitle 标题
	 @param msgDoc 内容
	 @param msgFlag 标记
	 @param msgType 类型
	 @param paras 扩展参数
	*/

	public static void SendMsg(String userNo, String msgTitle, String msgDoc, String msgFlag, String msgType, String paras, long workid, String pushModel) throws Exception {
		SendMsg(userNo, msgTitle, msgDoc, msgFlag, msgType, paras, workid, pushModel, null);
	}

	public static void SendMsg(String userNo, String msgTitle, String msgDoc, String msgFlag, String msgType, String paras, long workid) throws Exception {
		SendMsg(userNo, msgTitle, msgDoc, msgFlag, msgType, paras, workid, null, null);
	}

	public static void SendMsg(String userNo, String msgTitle, String msgDoc, String msgFlag, String msgType, String paras, long workid, String pushModel, String openUrl) throws Exception {

		SMS sms = new SMS();
		sms.setMyPK(DBAccess.GenerGUID(0, null, null));
		sms.setHisEmailSta(MsgSta.UnRun);

		sms.setSender(WebUser.getNo());
		sms.setSendToEmpNo(userNo);

		sms.setTitle(msgTitle);
		sms.setDocOfEmail(msgDoc);
		sms.setMobileInfo(msgDoc);

		sms.setSender(WebUser.getNo());
		sms.setRDT(DataType.getCurrentDateTime());

		sms.setMsgFlag(msgFlag); // 消息标志.
		sms.setMsgType(msgType); // 消息类型.'

		sms.setAtPara(paras);

		sms.setWorkID(workid);

		/**如果没有设置模式，就设置邮件.
		*/
		if (pushModel == null)
		{
			pushModel = "Email";
		}

		if (DataType.IsNullOrEmpty(openUrl) == false)
		{
			sms.SetPara("OpenUrl", openUrl);
		}
		if (DataType.IsNullOrEmpty(pushModel) == false)
		{
			sms.SetPara("PushModel", pushModel);
		}

		sms.Insert();
	}

		///#endregion 新方法


		///#region 手机短信属性
	/** 
	 手机号码
	*/
	public final String getMobile() {
		return this.GetValStringByKey(SMSAttr.Mobile);
	}
	public final void setMobile(String value) throws Exception {
		SetValByKey(SMSAttr.Mobile, value);
	}
	/** 
	 手机状态
	*/
	public final MsgSta getHisMobileSta() {
		return MsgSta.forValue(this.GetValIntByKey(SMSAttr.MobileSta));
	}
	public final void setHisMobileSta(MsgSta value) throws Exception {
		SetValByKey(SMSAttr.MobileSta, value.getValue());
	}
	/** 
	 手机信息
	*/
	public final String getMobileInfo() {
		return this.GetValStringByKey(SMSAttr.MobileInfo);
	}
	public final void setMobileInfo(String value) throws Exception {
		SetValByKey(SMSAttr.MobileInfo, value);
	}

		///#endregion


		///#region  邮件属性
	public final String getPushModel() {
		return this.GetParaString(SMSAttr.PushModel);
	}
	/** 
	 参数
	*/
	public final String getAtPara()  {
		return this.GetValStrByKey("AtPara", "");
	}
	public final void setAtPara(String value)  {
		this.SetValByKey("AtPara", value);
	}
	/** 
	 邮件状态
	*/
	public final MsgSta getHisEmailSta() {
		return MsgSta.forValue(this.GetValIntByKey(SMSAttr.EmailSta));
	}
	public final void setHisEmailSta(MsgSta value)  {
		this.SetValByKey(SMSAttr.EmailSta, value.getValue());
	}
	/** 
	 Email
	*/
	public final String getEmail() {
		return this.GetValStringByKey(SMSAttr.Email);
	}
	public final void setEmail(String value) throws Exception {
		SetValByKey(SMSAttr.Email, value);
	}
	/** 
	 发送给
	*/
	public final String getSendToEmpNo() {
		return this.GetValStringByKey(SMSAttr.SendTo);
	}
	public final void setSendToEmpNo(String value) throws Exception {
		SetValByKey(SMSAttr.SendTo, value);
	}
	public final int getItIsRead() {
		return this.GetValIntByKey(SMSAttr.IsRead);
	}
	public final void setItIsRead(int value)  {
		this.SetValByKey(SMSAttr.IsRead, (int)value);
	}
	public final int getItIsAlert() {
		return this.GetValIntByKey(SMSAttr.IsAlert);
	}
	public final void setItIsAlert(int value)  {
		this.SetValByKey(SMSAttr.IsAlert, (int)value);
	}
	/** 
	 消息标记(可以用它来避免发送重复)
	*/
	public final String getMsgFlag() {
		return this.GetValStringByKey(SMSAttr.MsgFlag);
	}
	public final void setMsgFlag(String value) throws Exception {
		SetValByKey(SMSAttr.MsgFlag, value);
	}
	/** 
	 类型
	*/
	public final String getMsgType() {
		return this.GetValStringByKey(SMSAttr.MsgType);
	}
	public final void setMsgType(String value) throws Exception {
		SetValByKey(SMSAttr.MsgType, value);
	}
	/** 
	 工作ID
	*/
	public final long getWorkID() {
		return this.GetValInt64ByKey(SMSAttr.WorkID);
	}
	public final void setWorkID(long value) throws Exception {
		SetValByKey(SMSAttr.WorkID, value);
	}
	/** 
	 发送人
	*/
	public final String getSender() {
		return this.GetValStringByKey(SMSAttr.Sender);
	}
	public final void setSender(String value) throws Exception {
		SetValByKey(SMSAttr.Sender, value);
	}
	/** 
	 记录日期
	*/
	public final String getRDT() {
		return this.GetValStringByKey(SMSAttr.RDT);
	}
	public final void setRDT(String value)  {
		this.SetValByKey(SMSAttr.RDT, value);
	}
	/** 
	 标题
	*/
	public final String getTitle() {
		return this.GetValStringByKey(SMSAttr.EmailTitle);
	}
	public final void setTitle(String value) throws Exception {
		SetValByKey(SMSAttr.EmailTitle, value);
	}
	/** 
	 邮件内容
	*/
	public final String getDocOfEmail() throws Exception {
		String doc = this.GetValStringByKey(SMSAttr.EmailDoc);
		if (DataType.IsNullOrEmpty(doc))
		{
			return this.getTitle();
		}

		return doc.replace('~', '\'');
	}
	public final void setDocOfEmail(String value) throws Exception {
		SetValByKey(SMSAttr.EmailDoc, value);
	}
	/** 
	 邮件内容.
	*/
	public final String getDoc() throws Exception {
		String doc = this.GetValStringByKey(SMSAttr.EmailDoc);
		if (DataType.IsNullOrEmpty(doc))
		{
			return this.getTitle();
		}
		return doc.replace('~', '\'');

	}
	public final void setDoc(String value) throws Exception {
		SetValByKey(SMSAttr.EmailDoc, value);
	}
	/** 
	 打开的连接
	*/
	public final String getOpenURL() {
		return this.GetParaString(SMSAttr.OpenUrl);
	}
	public final void setOpenURL(String value)  {
		this.SetPara(SMSAttr.OpenUrl, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenAll();
		return uac;
	}
	/** 
	 消息
	*/
	public SMS()
	{
	}
	/** 
	 Map
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_SMS", "消息");

		map.AddMyPK(true);

		map.AddTBString(SMSAttr.Sender, null, "发送人(可以为空)", false, true, 0, 200, 20);
		map.AddTBString(SMSAttr.SendTo, null, "发送给(可以为空)", false, true, 0, 1000, 20);
		map.AddTBDateTime(SMSAttr.RDT, "写入时间", true, false);

		map.AddTBString(SMSAttr.Mobile, null, "手机号(可以为空)", false, true, 0, 50, 20);
		map.AddTBInt(SMSAttr.MobileSta, MsgSta.UnRun.getValue(), "消息状态", true, true);
		map.AddTBString(SMSAttr.MobileInfo, null, "短信信息", false, true, 0, 1000, 20);

		map.AddTBString(SMSAttr.Email, null, "Email(可以为空)", false, true, 0, 200, 20);
		map.AddTBInt(SMSAttr.EmailSta, MsgSta.UnRun.getValue(), "EmaiSta消息状态", true, true);
		map.AddTBString(SMSAttr.EmailTitle, null, "标题", false, true, 0, 3000, 20);
		map.AddTBStringDoc(SMSAttr.EmailDoc, null, "内容", false, true);
		map.AddTBDateTime(SMSAttr.SendDT, null, "发送时间", false, false);

		map.AddTBInt(SMSAttr.IsRead, 0, "是否读取?", true, true);
		map.AddTBInt(SMSAttr.IsAlert, 0, "是否提示?", true, true);

		//用于获得数据.
		map.AddTBInt(SMSAttr.WorkID, 0, "WorkID", true, true);

		//消息主键.
		map.AddTBString(SMSAttr.MsgFlag, null, "消息标记(用于防止发送重复)", false, true, 0, 200, 20);
		map.AddTBString(SMSAttr.MsgType, null, "消息类型(CC抄送,Todolist待办,Return退回,Etc其他消息...)", false, true, 0, 200, 20);

		//其他参数.
		map.AddTBAtParas(500);

		//map.ItIsShowSearchKey = false;
		//map.SearchFields="Tel,Addr,"

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	/** 
	 发送邮件
	 
	 @param mail
	 @param mailTitle
	 @param mailDoc
	 @return 
	*/
	public static boolean SendEmailNowAsync(String mail, String mailTitle, String mailDoc)
	{
//		try
//		{
//			System.Net.Mail.MailMessage myEmail = new System.Net.Mail.MailMessage();
//
//			//邮件地址.
//			String emailAddr = bp.difference.SystemConfig.GetValByKey("SendEmailAddress", null);
//			if (emailAddr == null)
//			{
//				return false;
//				//emailAddr = "ccbpmtester@tom.com";
//			}
//
//			String emailPassword = bp.difference.SystemConfig.GetValByKey("SendEmailPass", null);
//			if (emailPassword == null)
//			{
//				return false;
//				emailPassword = "ccbpm123";
//			}
//
//			mailDoc = DataType.ParseText2Html(mailDoc);
//
//			String displayName = bp.difference.SystemConfig.GetValByKey("SendEmailDisplayName", "驰骋BPM");
//			myEmail.From = new System.Net.Mail.MailAddress(emailAddr, displayName, System.Text.Encoding.UTF8);
//
//			myEmail.To.Add(mail);
//			myEmail.setSubject(mailTitle);
//			myEmail.setSubjectEncoding(System.Text.Encoding.UTF8); //邮件标题编码
//			myEmail.setIsBodyHtml(true);
//			myEmail.setBody(mailDoc);
//			myEmail.setBodyEncoding(System.Text.Encoding.UTF8); //邮件内容编码
//			myEmail.setIsBodyHtml(true); //是否是HTML邮件
//			myEmail.setPriority(MailPriority.High); // 邮件优先级
//
//			SmtpClient client = new SmtpClient();
//			client.setUseDefaultCredentials(true);
//			if (bp.difference.SystemConfig.GetValByKeyInt("SendEmailEnableSsl", 1) == 1)
//			{
//				client.setEnableSsl(true); //经过ssl加密.
//			}
//			else
//			{
//				client.setEnableSsl(false);
//			}
//
//			client.Credentials = new System.Net.NetworkCredential(emailAddr, emailPassword);
//			client.Port = bp.difference.SystemConfig.GetValByKeyInt("SendEmailPort", 587); //使用的端口
//			client.Host = bp.difference.SystemConfig.GetValByKey("SendEmailHost", "smtp.gmail.com");
//
//			Object userState = myEmail;
//			//调用自带的异步方法
//			client.Send(myEmail);
//		   /* client.SendMailAsync(myEmail);
//		    client.SendAsync(myEmail, userState);*/
//		}
//		catch (RuntimeException e)
//		{
//			Log.DebugWriteError(e.getMessage());
//			return false;
//		}
		return true;
}
	/** 
	 SAAS发送.
	*/
	public final void SendMsgToSAAS() throws Exception {
		//获取设置.
		String messageUrl = bp.difference.SystemConfig.getAppSettings().get("HandlerOfMessage").toString();
		if (DataType.IsNullOrEmpty(messageUrl) == true)
		{
			return;
		}

		String httpUrl = messageUrl;

		String json = "";
		if (bp.difference.SystemConfig.getCustomerNo().equals("YuTong") == true)
		{
			json = "{";
			json += " \"token\": '34c45c2b30512e8a8e10467cee45d7ed',";
			json += " \"Sender\": \"" + WebUser.getNo() + "\",";
			json += " \"OrgNo\": \"" + WebUser.getOrgNo() + "\",";
			json += " \"userid\": \"" + this.getSendToEmpNo() + "\",";
			json += " \"Tel\": \"" + this.getMobile() + "\",";
			json += " \"title\":\"" + this.getTitle() + "\",";
			json += " \"MsgFlg\":\"" + this.getMsgFlag() + "\",";
			json += " \"MobileInfo\":\"" + this.getMobileInfo() + " \",";
			json += " \"contents\":\"" + this.getDoc() + " \",";
			json += " \"wx\":'true',";
			json += " \"isEmail\":'true',";
			json += " \"url\":\"" + this.getOpenURL() + " \"}";
		}
		else
		{
			json = "{";
			json += " \"Sender\": \"" + WebUser.getNo() + "\",";
			json += " \"OrgNo\": \"" + WebUser.getOrgNo() + "\",";
			json += " \"SendTo\": \"" + this.getSendToEmpNo() + "\",";
			json += " \"Tel\": \"" + this.getMobile() + "\",";
			json += " \"Title\":\"" + this.getTitle() + "\",";
			json += " \"MsgFlg\":\"" + this.getMsgFlag() + "\",";
			json += " \"MobileInfo\":\"" + this.getMobileInfo() + " \",";
			json += " \"Doc\":\"" + this.getDoc() + " \",";
			json += " \"Url\":\"" + this.getOpenURL() + " \"}";
		}
		{
		//微信
			//注册到url里面去.
			bp.tools.HttpClientUtil.doPost(httpUrl, json,null);
		}
	}
	/** 
	 插入之后执行的方法.
	*/
	@Override
	protected void afterInsert() throws Exception
	{
		try
		{
			bp.da.Log.DefaultLogWriteLine(LogType.Info, "发送消息");

			// 检查是否为SAAS模式并提前返回
			if (isSaasMode()) {
				bp.da.Log.DebugWriteInfo("SAAS模式不发送消息。");
				return;
			}

			if (this.getHisEmailSta() != MsgSta.UnRun){
				return;

			}

			// 解析参数并获取节点信息
			String fkFlow = extractParameter("FK_Flow");
			int nodeId = Integer.parseInt(extractParameter("NodeID"));

			// 获取节点消息配置
			PushMsg pm = getPushMsgConfig(fkFlow, nodeId);
			if (pm == null) {
				return; // 或抛出异常，取决于业务需求
			}

			try {
				// 发送消息
				sendMessage(pm);

				// 记录日志
				bp.da.Log.DebugWriteInfo("消息发送成功");
			}catch (Exception ex){
				bp.da.Log.DebugWriteError(ex);
			}

		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteError("@消息机制没有配置成功." + ex.getMessage());
		}
		super.afterInsert();
	}

	private boolean isSaasMode() {
		return bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS;
	}

	private String extractParameter(String key) {
		if(DataType.IsNullOrEmpty(this.getAtPara())){
			return "";
		}
		String[] atPara = this.getAtPara().split("@");
		for (String str : atPara) {
			if (str.startsWith(key + "=")) {
				return str.split("=")[1];
			}
		}
		return "";
	}

	private PushMsg getPushMsgConfig(String fkFlow, int nodeId) throws Exception {
		PushMsgs pms = new PushMsgs();
		// 判断是否存在
		if (pms.Retrieve(PushMsgAttr.FlowNo, fkFlow, PushMsgAttr.NodeID, nodeId, PushMsgAttr.EventNo, "SendSuccess") != 0) {
			return pms.ToJavaList().get(0);
		}
		return null;
	}

	private void sendMessage(PushMsg pm) throws Exception {
		// 邮件
		if (pm.getIsEnableEmail() == 1) {
			bp.wf.OverrideEvent.SendToEmail(this);
		}
		// 短信
		if (pm.getIsEnableSMS() == 1) {
			bp.wf.OverrideEvent.SendToMobile(this);
		}
		// 钉钉
		if (bp.difference.SystemConfig.getMessageIsEnableDingDing()) {
			bp.wf.OverrideEvent.SendToDingDing(this);
		}
		// 企业微信
		if (bp.difference.SystemConfig.getMessageIsEnableWeiXin()) {
			bp.wf.OverrideEvent.SendToWeiXin(this);
		}
		// 自定义
		if (bp.difference.SystemConfig.getMessageIsEnableSelf()) {
			bp.wf.OverrideEvent.MessageIsEnableSelf(this);
		}
		// 发送RabbitMQ消息
		if (bp.difference.SystemConfig.getMessageIsEnableRabbitMQ() == true){
			bp.wf.OverrideEvent.SendToRabbitMQ(this);
		}
	}

	/** 
	 设置已读
	*/
	public final void DoRead() throws Exception {

		this.setItIsRead(1);
		this.Update();
	}

}
