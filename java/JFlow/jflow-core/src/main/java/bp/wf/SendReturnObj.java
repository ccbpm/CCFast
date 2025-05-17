package bp.wf;
/** 
 工作发送返回对象
*/
public class SendReturnObj
{
	/** 
	 消息标记
	*/
	public String MsgFlag = null;
	/** 
	 消息标记描述
	*/
	public final String getMsgFlagDesc()
	{
		if (MsgFlag == null)
		{
			throw new RuntimeException("@没有标记");
		}

		switch (MsgFlag)
		{
			case SendReturnMsgFlag.VarAcceptersID:
				return "接受人ID";
			case SendReturnMsgFlag.VarAcceptersName:
				return "接受人名称";
			case SendReturnMsgFlag.VarAcceptersNID:
				return "接受人ID集合";
			case SendReturnMsgFlag.VarCurrNodeID:
				return "当前节点ID";
			case SendReturnMsgFlag.VarCurrNodeName:
				return "接受人集合的名称(用逗号分开)";
			case SendReturnMsgFlag.VarToNodeID:
				return "到达节点ID";
			case SendReturnMsgFlag.VarToNodeName:
				return "到达节点名称";
			case SendReturnMsgFlag.VarTreadWorkIDs:
				return "子线程的WorkIDs";
			case SendReturnMsgFlag.BillInfo:
				return "单据信息";
			case SendReturnMsgFlag.CCMsg:
				return "抄送信息";
			case SendReturnMsgFlag.CondInfo:
				return "条件信息";
			case SendReturnMsgFlag.CurrWorkOver:
				return "当前的工作已经完成";
			case SendReturnMsgFlag.EditAccepter:
				return "编辑接受者";
			case SendReturnMsgFlag.End:
				return "当前的流程已经结束";
			case SendReturnMsgFlag.FenLiuInfo:
				return "分流信息";
			case SendReturnMsgFlag.FlowOver:
				return "当前流程已经完成";
			case SendReturnMsgFlag.FlowOverByCond:
				return "符合完成条件，流程完成.";
			case SendReturnMsgFlag.HeLiuOver:
				return "分流完成";
			case SendReturnMsgFlag.MacthFlowOver:
				return "符合工作流程完成条件";
			case SendReturnMsgFlag.NewFlowUnSend:
				return "新建流程";
			case SendReturnMsgFlag.OverCurr:
				return "当前流程完成";
			case SendReturnMsgFlag.Rpt:
				return "报表";
			case SendReturnMsgFlag.SendSuccessMsg:
				return "发送成功信息";
			case SendReturnMsgFlag.SendSuccessMsgErr:
				return "发送错误";
			case SendReturnMsgFlag.SendWhen:
				return "发送时";
			case SendReturnMsgFlag.ToEmps:
				return "到达人员";
			case SendReturnMsgFlag.UnSend:
				return "撤消发送";
			case SendReturnMsgFlag.ToEmpExt:
				return "到人员的扩展信息";
			case SendReturnMsgFlag.VarWorkID:
				return "工作ID";
			case SendReturnMsgFlag.IsStopFlow:
				return "流程是否结束";
			case SendReturnMsgFlag.WorkRpt:
				return "工作报告";
			case SendReturnMsgFlag.WorkStartNode:
				return "启动节点";
			case SendReturnMsgFlag.AllotTask:
				return "分配任务";
			default:
				return "信息:" + MsgFlag;
			//  throw new Exception("@没有判断的标记...");
		}
	}
	/** 
	 消息类型
	*/
	public SendReturnMsgType HisSendReturnMsgType = SendReturnMsgType.Info;
	/** 
	 消息内容
	*/
	public String MsgOfText = null;
	/** 
	 消息内容Html
	*/
	public String MsgOfHtml = null;
	/** 
	 发送消息
	*/
	public SendReturnObj()
	{
	}
}
