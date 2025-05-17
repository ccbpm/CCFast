package bp.wf;

import bp.da.DBAccess;
import bp.da.DataType;

import java.util.ArrayList;
import java.util.Hashtable;


/**
 工作发送返回对象集合.
 */
public class SendReturnObjs extends ArrayList<SendReturnObj>
{
	public final boolean getIsStopFlow()
	{
		for (SendReturnObj item : this)
		{
			if (item.MsgFlag.equals(SendReturnMsgFlag.IsStopFlow))
			{
				if (item.MsgOfText.equals("1"))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		//throw new Exception("@没有找到系统变量IsStopFlow");
		return false;
	}

	///#region 获取系统变量.
	public final long getVarWorkID() throws Exception {
		for (SendReturnObj item : this)
		{
			if (item.MsgFlag.equals(SendReturnMsgFlag.VarWorkID))
			{
				return Long.parseLong(item.MsgOfText);
			}
		}
		return 0;
	}
	public final boolean isStopFlow() throws Exception {
		for (SendReturnObj item : this)
		{
			if (item.MsgFlag.equals(SendReturnMsgFlag.IsStopFlow))
			{
				if (item.MsgOfText.equals("1"))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		//throw new Exception("@没有找到系统变量IsStopFlow");
		return false;
	}

	/**
	 到达节点ID
	 */
	public final int getVarToNodeID() throws Exception {
		for (SendReturnObj item : this)
		{
			if (item.MsgFlag.equals(SendReturnMsgFlag.VarToNodeID))
			{
				return Integer.parseInt(item.MsgOfText);
			}
		}
		return 0;
	}
	/**
	 到达节点IDs
	 */
	public final String getVarToNodeIDs() throws Exception {
		for (SendReturnObj item : this)
		{
			if (item.MsgFlag.equals(SendReturnMsgFlag.VarToNodeIDs))
			{
				return item.MsgOfText;
			}
		}
		return null;
	}
	/**
	 到达节点名称
	 */
	public final String getVarToNodeName() throws Exception {
		for (SendReturnObj item : this)
		{
			if (item.MsgFlag.equals(SendReturnMsgFlag.VarToNodeName))
			{
				return item.MsgOfText;
			}
		}
		return "没有找到变量.";
	}
	/**
	 到达的节点名称
	 */
	public final String getVarCurrNodeName() throws Exception {
		for (SendReturnObj item : this)
		{
			if (item.MsgFlag.equals(SendReturnMsgFlag.VarCurrNodeName))
			{
				return item.MsgOfText;
			}
		}
		return null;
	}
	public final int getVarCurrNodeID() throws Exception {
		for (SendReturnObj item : this)
		{
			if (item.MsgFlag.equals(SendReturnMsgFlag.VarCurrNodeID))
			{
				return Integer.parseInt(item.MsgOfText);
			}
		}
		return 0;
	}
	/**
	 接受人
	 */
	public final String getVarAcceptersName() throws Exception {
		for (SendReturnObj item : this)
		{
			if (item.MsgFlag.equals(SendReturnMsgFlag.VarAcceptersName))
			{
				return item.MsgOfText;
			}
		}
		return null;
	}
	/**
	 接受人IDs
	 */
	public final String getVarAcceptersID()  {
		for (SendReturnObj item : this)
		{
			if (item.MsgFlag.equals(SendReturnMsgFlag.VarAcceptersID))
			{
				return item.MsgOfText;
			}
		}
		return null;
	}
	/**
	 文本提示信息.
	 */
	public final String getMsgOfText()  {
		for (SendReturnObj item : this)
		{
			if (item.MsgFlag.equals(SendReturnMsgFlag.MsgOfText))
			{
				return item.MsgOfText;
			}
		}
		return null;
	}

	/**
	 分流向子线程发送时产生的子线程的WorkIDs, 多个有逗号分开.
	 */
	public final String getVarTreadWorkIDs()  {
		for (SendReturnObj item : this)
		{
			if (item.MsgFlag.equals(SendReturnMsgFlag.VarTreadWorkIDs))
			{
				return item.MsgOfText;
			}
		}
		return null;
	}

	///#endregion

	/**
	 构造
	 */
	public SendReturnObjs()  {
	}
	/**
	 根据指定格式的字符串生成一个事例获取相关变量

	 param specText 指定格式的字符串
	 */
	public SendReturnObjs(String specText)
	{
		this.LoadSpecText(specText);
	}
	/**
	 输出text消息
	 */
	public String OutMessageText = null;
	/**
	 输出html信息
	 */
	public String OutMessageHtml = null;
	/**
	 增加消息

	 param msgFlag 消息标记
	 param msg 文本消息
	 param msgOfHtml html消息
	 param type 消息类型
	 */
	public final void AddMsg(String msgFlag, String msg, String msgOfHtml, SendReturnMsgType type)
	{
		SendReturnObj obj = new SendReturnObj();
		obj.MsgFlag = msgFlag;
		obj.MsgOfText = msg;
		obj.MsgOfHtml = msgOfHtml;
		obj.HisSendReturnMsgType = type;
		for (SendReturnObj item : this)
		{
			if (item.MsgFlag.equals(msgFlag))
			{
				item.MsgFlag = msgFlag;
				item.MsgOfText = msg;
				item.MsgOfHtml = msgOfHtml;
				item.HisSendReturnMsgType = type;
				return;
			}
		}
		this.add(obj);
	}
	/**
	 转化成特殊的格式

	 @return
	 */
	public final String ToMsgOfSpecText()  {
		String msg = "";
		for (SendReturnObj item : this)
		{
			if (item.MsgOfText != null)
			{
				msg += "$" + item.MsgFlag + "^" + item.HisSendReturnMsgType.getValue() + "^" + item.MsgOfText;
			}
		}

		//增加上 text信息。
		msg += "$MsgOfText^" + SendReturnMsgType.Info.getValue() + "^" + this.ToMsgOfText();

		msg.replace("@@", "@");
		return msg;
	}
	/**
	 装载指定的文本，生成这个对象。

	 param text 指定格式的文本
	 */
	public final void LoadSpecText(String text)
	{
		String[] strs = text.split("[$]", -1);
		for (String str : strs)
		{

			String[] sp = str.split("[^ ]", -1);
			this.AddMsg(sp[0], sp[2], null, SendReturnMsgType.forValue(Integer.parseInt(sp[1])));
		}
	}
	/**
	 转化成text方式的消息，以方便识别不出来html的设备输出.

	 @return
	 */
	public final String ToMsgOfText()  {
		if (this.OutMessageText != null)
		{
			return this.OutMessageText;
		}

		String msg = "";
		for (SendReturnObj item : this)
		{
			if (item.HisSendReturnMsgType == SendReturnMsgType.SystemMsg)
			{
				continue;
			}

			//特殊判断.
			if (item.MsgFlag.equals(SendReturnMsgFlag.IsStopFlow))
			{
				msg += "@" + item.MsgOfHtml;
				continue;
			}


			if (item.MsgOfText != null)
			{
				if (item.MsgOfText.contains("<"))
				{

///#warning 不应该出现.
					//  Log.DefaultLogWriteLineWarning("@文本信息里面有html标记:" + item.MsgOfText);
					continue;
				}
				msg += "@" + item.MsgOfText;
				continue;
			}

		}
		msg.replace("@@", "@");
		return msg;
	}
	public final String ToJson()  {

		Hashtable ht=new Hashtable();
		for (SendReturnObj item : this)
		{
			String nodeID = null;
			if (item.MsgFlag.equals(SendReturnMsgFlag.VarCurrNodeID) )
				nodeID = item.MsgOfText;
			if (item.MsgFlag.equals(SendReturnMsgFlag.VarToNodeID))
				nodeID = item.MsgOfText;

			if (nodeID != null)
			{
				ht.put(item.MsgFlag, nodeID); //真实的节点ID,是int类型的.

				nodeID = DBAccess.RunSQLReturnStringIsNull("SELECT Mark FROM WF_Node WHERE NodeID=" + nodeID, nodeID);
				ht.put(item.MsgFlag+"NodeMark", nodeID); //节点ID标识.
				continue;
			}

			if (item.MsgFlag.equals(SendReturnMsgFlag.VarToNodeIDs))
			{
				ht.put(item.MsgFlag, item.MsgOfText);
				String ids = "";
				String[] strs = item.MsgOfText.split("[,]", -1);
				for (String str : strs)
				{
					ids += DBAccess.RunSQLReturnStringIsNull("SELECT Mark FROM WF_Node WHERE NodeID=" + nodeID, nodeID) + ",";
				}
				ht.put(item.MsgFlag + "NodeMark", ids);
				continue;
			}

			if(DataType.IsNullOrEmpty(item.MsgOfText))
				continue;

			ht.put(item.MsgFlag,item.MsgOfText);
		}

		return bp.tools.Json.ToJson(ht);
	}
	/**
	 转化成html方式的消息，以方便html的信息输出.

	 @return
	 */
	public final String ToMsgOfHtml()  {
		if (this.OutMessageHtml != null)
		{
			return this.OutMessageHtml;
		}

		String msg = "";
		for (SendReturnObj item : this)
		{
			if (item.HisSendReturnMsgType != SendReturnMsgType.Info)
			{
				continue;
			}

			if (item.MsgOfHtml != null)
			{
				msg += "@" + item.MsgOfHtml;
				continue;
			}

			if (item.MsgOfText != null)
			{
				msg += "@" + item.MsgOfText;
				continue;
			}
		}
		msg = msg.replace("@@", "@");
		msg = msg.replace("@@", "@");
		if (msg.equals("@"))
		{
			return "@流程已经完成.";
		}

		return msg;
	}
}