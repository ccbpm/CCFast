package bp.port.dingtalk;

import bp.da.DBAccess;
import bp.da.DataRow;
import bp.da.DataTable;
import bp.da.DataType;
import bp.port.dingtalk.DingModel.*;

import java.util.*;

public class DingFlowMessage
{
	public final DingPostReturnVal Ding_SendWorkMessage(DingMsgType msgType, long WorkID, String sender)
	{


		DataTable dt = DBAccess.RunSQLReturnTable("SELECT * FROM WF_GenerWorkFlow WHERE WorkID=" + WorkID);
		if (dt.Rows.size() == 0)
		{
			return null;
		}

		int wfState = Integer.parseInt(dt.Rows.get(0).getValue("WFState").toString());
		String title = dt.Rows.get(0).getValue("Title").toString();
		String flowNo = dt.Rows.get(0).getValue("FK_Flow").toString();
		String nodeID = dt.Rows.get(0).getValue("FK_Node").toString();
		String fid = dt.Rows.get(0).getValue("FID").toString();
		String flowName = dt.Rows.get(0).getValue("FlowName").toString();
		String nodeName = dt.Rows.get(0).getValue("NodeName").toString();
		String starterName = dt.Rows.get(0).getValue("StarterName").toString();
		String rdt = dt.Rows.get(0).getValue("RDT").toString();


		//结束不发送消息
		if (wfState == 3)
		{
			return null;
		}



		//判断节点类型，分合流等.
		dt = DBAccess.RunSQLReturnTable("SELECT * FROM WF_EmpWorks WHERE WorkID=" + WorkID + " OR FID=" + WorkID);
		if (dt.Rows.size() == 0)
		{
			return null;
		}

		String toUsers = "";
		for (DataRow dr : dt.Rows)
		{
			if (toUsers.length() > 0)
			{
				toUsers += "|";
			}
			toUsers += dr.get("FK_Emp").toString();
		}
		if (toUsers.length() == 0)
		{
			return null;
		}

		switch (msgType)
		{
			case text:
				DingMsgText msgText = new DingMsgText();
				msgText.setAccessToken(DingDing.getAccessToken());
				msgText.setAgentid(bp.difference.SystemConfig.getDing_AgentID());
				msgText.setTouser(toUsers);
				msgText.setContent(title + "\n发送人：" + sender + "\n时间：" + DataType.getCurrentDateTimeCNOfShort());
				return DingTalk_Message.Msg_AgentText_Send(msgText);
			case link:
				DingMsgLink msgLink = new DingMsgLink();
				msgLink.setAccessToken(DingDing.getAccessToken());
				msgLink.setTouser(toUsers);
				msgLink.setAgentid(bp.difference.SystemConfig.getDing_AgentID());
				msgLink.setMessageUrl(bp.difference.SystemConfig.getDing_MessageUrl() + "/CCMobile/login.aspx");
				msgLink.setPicUrl("@lALOACZwe2Rk");
				msgLink.setTitle(title);
				msgLink.setText("发送人：" + sender + "\n时间：" + DataType.getCurrentDateTimeCNOfShort());
				return DingTalk_Message.Msg_AgentLink_Send(msgLink);
			case OA:
				String[] users = toUsers.split("[|]", -1);
				String faildSend = "";
				DingPostReturnVal postVal = null;
				for (String user : users)
				{
					DingMsgOA msgOA = new DingMsgOA();
					msgOA.setAccessToken(DingDing.getAccessToken());
					msgOA.setAgentid(bp.difference.SystemConfig.getDing_AgentID());
					msgOA.setTouser(user);
					msgOA.setMessageUrl(bp.difference.SystemConfig.getDing_MessageUrl() + "/CCMobile/DingAction.aspx?ActionFrom=message&UserID=" + user + "&ActionType=ToDo&FK_Flow=" + flowNo + "&FK_Node=" + nodeID + "&WorkID=" + WorkID + "&FID=" + fid);
					//00是完全透明，ff是完全不透明，比较适中的透明度值是 1e
					msgOA.setHeadBgcolor("FFBBBBBB");
					msgOA.setHeadText("审批");
					msgOA.setBodyTitle(title);
					Hashtable hs = new Hashtable();
					hs.put("流程名",flowName);
					hs.put("当前节点", nodeName);
					hs.put("申请人", starterName);
					hs.put("申请时间",rdt);
					msgOA.setBodyForm(hs);
					msgOA.setBodyAuthor(sender);
					postVal = DingTalk_Message.Msg_OAText_Send(msgOA);
					if (!Objects.equals(postVal.getErrcode(), "0"))
					{
						if (faildSend.length() > 0)
						{
							faildSend += ",";
						}
						faildSend += user;
					}
				}
				//有失败消息
				if (faildSend.length() > 0)
				{
					postVal.setErrcode("500");
					postVal.setErrmsg(faildSend + "消息发送失败");
				}
				return postVal;
		}
		return null;
	}
}
