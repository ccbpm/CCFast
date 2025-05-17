package bp.wf;

import bp.da.*;
import bp.difference.handler.CommonUtils;
import bp.en.*; import bp.en.Map;
import bp.difference.*;
import bp.web.*;
import bp.sys.*;
import bp.wf.xml.*;
import bp.*;
import java.time.*;
import java.math.*;
import java.util.Date;

/** 
 流程事件基类
 0,集成该基类的子类,可以重写事件的方法与基类交互.
 1,一个子类必须与一个流程模版绑定.
 2,基类里有很多流程运行过程中的变量，这些变量可以辅助开发者在编写复杂的业务逻辑的时候使用.
 3,该基类有一个子类模版，位于:\CCFlow\WF\Admin\AttrFlow\F001Templepte.cs .
*/
public abstract class FlowEventBase
{

		///#region 要求子类强制重写的属性.
	/** 
	 流程编号/流程标记.
	 该参数用于说明要把此事件注册到那一个流程模版上.
	*/
	public abstract String getFlowMark();

		///#endregion 要求子类重写的属性.


		///#region 属性/内部变量(流程在运行的时候触发各类事件，子类可以访问这些属性来获取引擎内部的信息).
	/** 
	 发送对象
	*/
	public SendReturnObjs SendReturnObjs = null;
	/** 
	 实体，一般是工作实体
	*/
	public Entity HisEn = null;
	private Node _hisNode = null;
	/** 
	 当前节点
	*/
	public final Node getHisNode()
	{
		return _hisNode;
	}
	public final void setHisNode(Node value)
	{
		_hisNode = value;
	}
	/** 
	 流程注册表
	*/
	public GenerWorkFlow HisGenerWorkFlow = null;
	/** 
	 参数对象.
	*/
	private Row _SysPara = null;
	/** 
	 参数
	*/
	public final Row getSysPara()
	{
		if (_SysPara == null)
		{
			_SysPara = new Row();
		}
		return _SysPara;
	}
	public final void setSysPara(Row value)
	{
		_SysPara = value;
	}
	/** 
	 成功信息
	*/
	public String SucessInfo = null;

		///#endregion 属性/内部变量(流程在运行的时候触发各类事件，子类可以访问这些属性来获取引擎内部的信息).


		///#region 在发送前的事件里可以改变参数.
	/** 
	 要跳转的节点.(开发人员可以设置该参数,改变发送到的节点转向.)
	*/
	private int _JumpToNodeID = 0;
	public final int getJumpToNodeID()
	{
		return _JumpToNodeID;
	}
	public final void setJumpToNodeID(int value)
	{
		this._JumpToNodeID = value;
	}
	/** 
	 接受人, (开发人员可以设置该参数,改变接受人的范围.)
	*/
	private String _JumpToEmps = null;
	public final String getJumpToEmps()
	{
		return _JumpToEmps;
	}
	public final void setJumpToEmps(String value)
	{
		this._JumpToEmps = value;
	}
	/** 
	 是否停止流程？
	*/
	public boolean ItIsStopFlow = false;

		///#endregion 在发送前的事件里可以改变参数


		///#region 系统参数
	/** 
	 表单ID
	*/
	public final String getFKMapdata()  {
		return this.GetValStr("FK_MapData");
	}

		///#endregion


		///#region 常用属性.
	/** 
	 工作ID
	*/
	public final int getOID()  {
		return this.GetValInt("OID");
	}
	/** 
	 工作ID
	*/
	public final long getWorkID() throws Exception {
		try
		{
			return this.GetValInt64("WorkID"); //有可能开始节点的WorkID=0
		}
		catch (RuntimeException e)
		{
			return this.getOID();
		}
	}
	/** 
	 流程ID
	*/
	public final long getFID()  {
		return this.GetValInt64("FID");
	}
	/** 
	 传过来的WorkIDs集合，子流程.
	*/
	public final String getWorkIDs()  {
		return this.GetValStr("WorkIDs");
	}
	/** 
	 编号集合s
	*/
	public final String getNos()  {
		return this.GetValStr("Nos");
	}
	/** 
	 项目编号
	*/
	public final String getPrjNo()  {
		return this.GetValStr("PrjNo");
	}
	/** 
	 项目名称
	*/
	public final String getPrjName()  {
		return this.GetValStr("PrjName");
	}
	/** 
	 流程标题
	*/
	public final String getTitle()  {
		return this.GetValStr(GERptAttr.Title);
	}

		///#endregion 常用属性.


		///#region 获取参数方法
	/** 
	 事件参数
	 
	 @param key 时间字段
	 @return 根据字段返回一个时间,如果为Null,或者不存在就抛出异常.
	*/
	public final Date GetValDateTime(String key)
	{
		try
		{
			String str = this.getSysPara().GetValByKey(key).toString();
			return DataType.ParseSysDateTime2DateTime(str);
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("@流程事件实体在获取参数期间出现错误，请确认字段(" + key + ")是否拼写正确,技术信息:" + ex.getMessage());
		}
	}
	/** 
	 获取字符串参数
	 
	 @param key key
	 @return 如果为Null,或者不存在就抛出异常
	*/
	public final String GetValStr(String key)
	{
		if (this.getSysPara().containsKey(key) == false)
		{
			throw new RuntimeException("@流程事件实体在获取参数期间出现错误，请确认字段(" + key + ")是否拼写正确.");
		}

		return this.getSysPara().GetValByKey(key).toString();
	}
	/** 
	 获取Int64的数值
	 
	 @param key 键值
	 @return 如果为Null,或者不存在就抛出异常
	*/
	public final long GetValInt64(String key)
	{
		return Long.parseLong(this.GetValStr(key));
	}
	/** 
	 获取int的数值
	 
	 @param key 键值
	 @return 如果为Null,或者不存在就抛出异常
	*/
	public final int GetValInt(String key)
	{
		return Integer.parseInt(this.GetValStr(key));
	}
	/** 
	 获取Boolen值
	 
	 @param key 字段
	 @return 如果为Null,或者不存在就抛出异常
	*/
	public final boolean GetValBoolen(String key)
	{
		if (Integer.parseInt(this.GetValStr(key)) == 0)
		{
			return false;
		}
		return true;
	}
	/** 
	 获取decimal的数值
	 
	 @param key 字段
	 @return 如果为Null,或者不存在就抛出异常
	*/
	public final BigDecimal GetValDecimal(String key)
	{
		return new BigDecimal(this.GetValStr(key));
	}

		///#endregion 获取参数方法


		///#region 构造方法
	/** 
	 流程事件基类
	*/
	public FlowEventBase()
	{
	}

		///#endregion 构造方法


		///#region 节点表单事件
	public String FrmLoadAfter()
	{
		return null;
	}
	public String FrmLoadBefore()
	{
		return null;
	}

		///#endregion
	
	//当节点打开后
	public String WhenReadWork()
	{
		return null;
	}
		///#region 要求子类重写的方法(流程事件).
	/** 
	 当创建WorkID的时候
	 
	 @return 创建WorkID所执行的操作
	*/
	public String FlowOnCreateWorkID()
	{
		return null;
	}
	/** 
	 流程完成前
	 
	 @return 返回null，不提示信息，返回string提示结束信息,抛出异常就阻止流程删除.
	*/
	public String FlowOverBefore()
	{
		return null;
	}
	/** 
	 流程结束后
	 
	 @return 返回null，不提示信息，返回string提示结束信息,抛出异常不处理。
	*/
	public String FlowOverAfter()
	{
		return null;
	}
	/** 
	 流程删除前
	 
	 @return 返回null,不提示信息,返回信息，提示删除警告/提示信息, 抛出异常阻止删除操作.
	*/
	public String BeforeFlowDel()
	{
		return null;
	}
	/** 
	 流程删除后
	 
	 @return 返回null,不提示信息,返回信息，提示删除警告/提示信息, 抛出异常不处理.
	*/
	public String AfterFlowDel()
	{
		return null;
	}

		///#endregion 要求子类重写的方法(流程事件).


		///#region 要求子类重写的方法(节点事件).
	/** 
	 保存后
	*/
	public String SaveAfter()
	{
		return null;
	}
	/** 
	 保存前
	*/
	public String SaveBefore()
	{
		return null;
	}
	/** 
	 创建工作ID后的事件
	 
	 @return 
	*/
	public String CreateWorkID()
	{
		return null;
	}
	/** 
	发送前  @Info=xxxx@ToNodeID=xxxx@ToEmps=xxxx
	*/
	public String SendWhen() {
		return null;
	}
	/** 
	 发送成功时
	*/
	public String SendSuccess() throws Exception {
		return null;
	}
	/** 
	 发送失败
	 
	 @return 
	*/
	public String SendError()
	{
		return null;
	}
	/** 
	 退回前
	 
	 @return 
	*/
	public String ReturnBefore()
	{
		return null;
	}
	/** 
	 退后后
	 
	 @return 
	*/
	public String ReturnAfter()
	{
		return null;
	}
	/** 
	 撤销之前
	 
	 @return 
	*/
	public String UndoneBefore()
	{
		return null;
	}
	/** 
	 撤销之后
	 
	 @return 
	*/
	public String UndoneAfter()
	{
		return null;
	}
	/** 
	 移交后
	 
	 @return 
	*/
	public String ShiftAfter()
	{
		return null;
	}
	/** 
	 加签后
	 
	 @return 
	*/
	public String AskerAfter()
	{
		return null;
	}
	/** 
	 加签答复后
	 
	 @return 
	*/
	public String AskerReAfter()
	{
		return null;
	}
	/** 
	 队列节点发送后
	 
	 @return 
	*/
	public String QueueSendAfter()
	{
		return null;
	}
	/** 
	 工作到达的时候
	 
	 @return 
	*/
	public String WorkArrive()
	{
		return null;
	}

		///#endregion 要求子类重写的方法(节点事件).


		///#region 基类方法.
	/** 
	 执行事件
	 
	 @param eventType 事件类型
	 @param en 实体参数
	*/

	public final String DoIt(String eventType, Node currNode, Entity en, String atPara, int jumpToNodeID, String toEmps) throws Exception {
		return DoIt(eventType, currNode, en, atPara, jumpToNodeID, toEmps, null);
	}

	public final String DoIt(String eventType, Node currNode, Entity en, String atPara, int jumpToNodeID) throws Exception {
		return DoIt(eventType, currNode, en, atPara, jumpToNodeID, null, null);
	}

	public final String DoIt(String eventType, Node currNode, Entity en, String atPara) throws Exception {
		return DoIt(eventType, currNode, en, atPara, 0, null, null);
	}

	public final String DoIt(String eventType, Node currNode, Entity en, String atPara, int jumpToNodeID, String toEmps, GenerWorkFlow hisGWF) throws Exception {
		this.HisEn = en;
		this.setHisNode(currNode);
		this.HisGenerWorkFlow = hisGWF;
		//  this.WorkID = en.GetValInt64ByKey("OID");
		this.setJumpToEmps(toEmps);
		this.setJumpToNodeID(jumpToNodeID);
		this.setSysPara(null);
		this.ItIsStopFlow = false;


			///#region 处理参数.
		Row r = en.getRow();

		//系统参数.
		if (r.containsKey("FK_MapData") == true)
		{
			r.SetValByKey("FK_MapData", en.getClassID());
		}
		else
		{
			r.put("FK_MapData", en.getClassID());
		}

		if (atPara != null)
		{
			AtPara ap = new AtPara(atPara);
			for (String s : ap.getHisHT().keySet())
			{
				try
				{
					r.put(s, ap.GetValStrByKey(s));
				}
				catch (java.lang.Exception e)
				{
					r.SetValByKey(s, ap.GetValStrByKey(s));
				}
			}
		}

		if (SystemConfig.isBSsystem() == true)
		{
			/*如果是bs系统, 就加入外部url的变量.*/
			for (String key : CommonUtils.getRequest().getParameterMap().keySet())
			{
				if (key == null)
				{
					continue;
				}

				switch (key)
				{
					case "WorkID":
					case "NodeID":
					case "FrmID":
					case "IsReadonly":
					case "__VIEWSTATE":
					case "__EVENTVALIDATION":
					case "OID":
						continue;
					default:
						break;
				}


				String val = ContextHolderUtils.getRequest().getParameter(key); //BP.Sys.Base.Glo.Request.QueryString[key];

				if (r.containsKey(key) == true)
				{
					r.SetValByKey(key, val);
				}
				else
				{
					r.put(key, val);
				}
			}
		}
		this.setSysPara(r);

			///#endregion 处理参数.


			///#region 执行事件.
		switch (eventType)
		{
			case EventListFlow.FlowOnCreateWorkID: // 节点表单事件。
				return this.CreateWorkID();
			case EventListNode.FrmLoadAfter: // 节点表单事件。
				return this.FrmLoadAfter();
			case EventListNode.FrmLoadBefore: // 节点表单事件。
				return this.FrmLoadBefore();
			case EventListNode.WhenReadWork: //  节点打开后.。
				return this.WhenReadWork();
			case EventListNode.NodeFrmSaveAfter: // 节点事件 保存后。
				return this.SaveAfter();
			case EventListNode.NodeFrmSaveBefore: // 节点事件 - 保存前.。
				return this.SaveBefore();
			case EventListNode.SendWhen: // 节点事件 - 发送前。

				this.ItIsStopFlow = false;

				String str = this.SendWhen();

				if (this.ItIsStopFlow == true)
				{
					return "@Info=" + str + "@IsStopFlow=1";
				}

				if (this.getJumpToNodeID() == 0 && this.getJumpToEmps() == null)
				{
					return str;
				}

				//返回这个格式, NodeSend 来解析.
				return "@Info=" + str + "@ToNodeID=" + this.getJumpToNodeID() + "@ToEmps=" + this.getJumpToEmps();

			case EventListNode.SendSuccess: // 节点事件 - 发送成功时。
				return this.SendSuccess();
			case EventListNode.SendError: // 节点事件 - 发送失败。
				return this.SendError();
			case EventListNode.ReturnBefore: // 节点事件 - 退回前。
				return this.ReturnBefore();
			case EventListNode.ReturnAfter: // 节点事件 - 退回后。
				return this.ReturnAfter();
			case EventListNode.UndoneBefore: // 节点事件 - 撤销前。
				return this.UndoneBefore();
			case EventListNode.UndoneAfter: // 节点事件 - 撤销后。
				return this.UndoneAfter();
			case EventListNode.ShitAfter: // 节点事件-移交后
				return this.ShiftAfter();
			case EventListNode.AskerAfter: //节点事件 加签后
				return this.AskerAfter();
			case EventListNode.AskerReAfter: //节点事件加签回复后
				return this.FlowOverBefore();
			case EventListNode.QueueSendAfter: //队列节点发送后
				return this.AskerReAfter();

			case EventListFlow.FlowOverBefore: // 流程结束前.。
				return this.FlowOverBefore();
			case EventListFlow.FlowOverAfter: // 流程结束后。
				return this.FlowOverAfter();
			case EventListFlow.BeforeFlowDel: // 流程删除前。
				return this.BeforeFlowDel();
			case EventListFlow.AfterFlowDel: // 删除后.
				return this.AfterFlowDel();
			case EventListNode.WorkArrive: // 工作到达时.
				return this.WorkArrive();
			default:
				throw new RuntimeException("@没有判断的事件类型:" + eventType);
		}

			///#endregion 执行事件.
	}

		///#endregion 基类方法.
}
