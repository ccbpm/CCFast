package WebService;

import java.util.Hashtable;
import bp.da.DataTable;

/**
 * 外部接口
 * @author Administrator
 *
 */

public interface LocalWSI {
	/**
	 * 待办
	 * @param userNo 用户编号
	 * @param sysNo 系统编号,为空时返回平台所有数据
	 * @return
	 * @throws Exception 
	 */

	public String DB_Todolist(String userNo, String sysNo) throws Exception;
	
	/**
	 * 获得在途
	 * @param userNo 用户编号
	 * @param sysNo 系统编号,为空时返回平台所有数据
	 * @return
	 * @throws Exception 
	 */

	public String DB_Runing(String userNo, String sysNo) throws Exception;
	
	/**
	 * 我可以发起的流程
	 * @param userNo 用户编号
	 * @param domainExt  系统编号,为空时返回平台所有数据
	 * @return 返回我可以发起的流程列表.
	 * @throws Exception 
	 */

	public String DB_StarFlows(String userNo, String domainExt) throws Exception;
	
	/**
	 * 我发起的流程实例
	 * @param userNo 用户编号
	 * @param sysNo 统编号,为空时返回平台所有数据
	 * @param pageSize
	 * @param pageIdx
	 * @return
	 * @throws Exception 
	 */

	public String DB_MyStartFlowInstance(String userNo, String sysNo, int pageSize, int pageIdx) throws Exception;
	/**
	 * 创建WorkID
	 * @param flowNo 流程编号
	 * @param userNo 工作人员编号
	 * @return 一个长整型的工作流程实例
	 * @throws Exception 
	 */

	public long CreateWorkID(String flowNo, String userNo) throws Exception;
	
	/**
	 * 执行发送
	 * @param flowNo 流的程模版ID
	 * @param workid 工作ID
	 * @param ht 参数，或者表单字段.
	 * @param toNodeID 到达的节点ID.如果让系统自动计算就传入0
	 * @param toEmps 到达的人员IDs,比如:zhangsan,lisi,wangwu. 如果为Null就标识让系统自动计算
	 * @return 发送的结果信息.
	 * @throws Exception 
	 */

	public String SendWork(String flowNo, long workid, Hashtable ht, int toNodeID, String toEmps, String userNo) throws Exception;
	
	/**
	 * 保存参数
	 * @param workid 工作ID
	 * @param paras 用于控制流程运转的参数，比如方向条件. 格式为:@JinE=1000@QingJaiTianShu=100
	 * @throws Exception 
	 */

	public void SaveParas(long workid, String paras, String userNo) throws Exception;
	
	/**
	 * 获得下一个节点信息
	 * @param flowNo 流程编号
	 * @param workid 流程实例
	 * @param paras 方向条件所需要的参数，可以为空。
	 * @return 下一个节点的JSON.
	 * @throws Exception 
	 */

	public String GenerNextStepNode(String flowNo, long workid, String paras, String userNo) throws Exception;
	
	/**
	 * 获得下一步节点的接收人
	 * @param flowNo 流程ID
	 * @param toNodeID 节点ID
	 * @param workid 工作事例ID
	 * @return 返回两个结果集一个是分组的Depts(No,Name)，另外一个是人员的Emps(No, Name, FK_Dept),接受后，用于构造人员选择器.
	 * @throws Exception 
	 */

	public String GenerNextStepNodeEmps(String flowNo, int toNodeID, int workid, String userNo) throws Exception;
	
	/**
	 * 将要达到的节点
	 * @param currNodeID 当前节点ID
	 * @return 返回节点集合的json.
	 * @throws Exception 
	 */

	public String WillToNodes(int currNodeID, String userNo) throws Exception;
	
	/**
	 * 将要退回到的节点
	 * @param workID
	 * @return 返回节点集合的json.
	 * @throws Exception 
	 */

	public String WillReturnToNodes(int workID, String userNo) throws Exception;
	
	/**
	 * 获得当前节点信息.
	 * @param currNodeID  当前节点ID
	 * @return
	 * @throws Exception 
	 */

	public String CurrNodeInfo(int currNodeID, String userNo) throws Exception;
	
	/**
	 * 获得当前流程信息.
	 * @param flowNo 流程ID
	 * @return 当前节点信息
	 * @throws Exception 
	 */

	public String CurrFlowInfo(String flowNo, String userNo) throws Exception;
	
	/**
	 * 获得当前流程信息.
	 * @param workID 流程ID
	 * @return 当前节点信息
	 * @throws Exception 
	 */

	public String CurrGenerWorkFlowInfo(long workID, String userNo) throws Exception;

	

	/**
	 * 退回.
	 * @param workID 流程ID
	 * @param returnToNodeID 流程退回的节点ID
	 * @param returnMsg 退回原因
	 * @return 退回结果信息
	 * @throws Exception 
	 */
	public String Node_ReturnWork(long workID,int returnToNodeID,String returnMsg, String userNo) throws Exception;
	
	/**
	 * 执行流程结束 说明:强制流程结束.
	 * @param workID
	 *            工作ID
	 * @param msg
	 *            流程结束原因
	 * @return 返回成功执行信息
	 * @throws Exception
	 */

	public  String Flow_DoFlowOverQiangZhi(long workID, String msg, String userNo) throws Exception;

	
	/**
	 * 用户登录
	 * @param UserNo
	 * @throws Exception
	 */
	public void Port_Login(String UserNo) throws Exception;
	
	/**
	 * 执行撤销
	 * @param flowNo 流程编码
	 * @param workID 工作ID
	 * @param unSendToNode 撤销到的节点
	 * @param fid 
	 * @return
	 * @throws Exception
	 */
	public String Runing_UnSend(String userNo,String flowNo, long workID, int unSendToNode,long fid) throws Exception;
	
	/**
	 * 流程结束后回滚
	 * @param flowNo 流程编码
	 * @param workId 工作ID
	 * @param backToNodeID 回滚到的节点ID
	 * @param backMsg 回滚原因
	 * @return 回滚信息
	 * @throws Exception 
	 */
	public String DoRebackFlowData(String flowNo,long workId,int backToNodeID,String backMsg, String userNo) throws Exception;

	/// <summary>
    /// 获得当前流程信息.
    /// </summary>
    /// <param name="flowNo">流程ID.</param>
    /// <returns>当前节点信息</returns>
    public String CurrFlowInfo(String flowNo)throws Exception;
  /// <summary>
    /// 获得当前流程信息.
    /// </summary>
    /// <param name="flowNo">流程ID.</param>
    /// <returns>当前节点信息</returns>
    public String CurrGenerWorkFlowInfo(long workID)throws Exception;
    /** 
	 获得工作进度-用于展示流程的进度图
	 
	 @param workID workID
	 @param userNo 用户编号
	 @return 返回待办
	*/
   public String WorkProgressBar(long  workID, String userNo) throws Exception;
   public String WorkProgressBar20(long  workID, String userNo) throws Exception;
   
   /** 
	 查询数据	 
	 @param sqlOfSelect 要查询的sql
	 @param password 用户密码
	 @return 返回查询数据
	*/
   public String DB_RunSQLReturnJSON(String sqlOfSelect, String password) throws Exception;


   /**
	 * 执行抄送
	 * 
	 * @param fk_node
	 *            流程编号
	 * @param workID
	 *            工作ID
	 * @param toEmpNo
	 *            抄送人员编号
	 * @param toEmpName
	 *            抄送人员人员名称
	 * @param msgTitle
	 *            标题
	 * @param msgDoc
	 *            内容
	 * @return 执行信息
	 * @throws Exception
	 */
	public String Node_CC_WriteTo_CClist(int fk_node, long workID, String toEmpNo, String toEmpName,
			String msgTitle, String msgDoc,String userNo) throws Exception;
	/**
	 * 获取指定人员的抄送列表 说明:可以根据这个列表生成指定用户的抄送数据.
	 * 
	 * @param userNo
	 *            人员编号,如果是null,则返回所有的.
	 * @return 返回该人员的所有抄送列表,结构同表WF_CCList.
	 */
	public String DB_CCList(String userNo) throws Exception;



   /** 
 	 是否可以查看该流程	 
 	 @param flowNo 流程编号
 	 @param workid 工作ID
 	 @param userNo 操作员ID
 	 @return 是否可以查看该工作.
 * @throws Exception 
 	*/
   public Boolean Flow_IsCanView(String flowNo, long workid, String userNo) throws Exception;
   
   /** 
	 是否可以查看该流程	 	 
	 @param workid 工作ID
	 @param userNo 操作员ID
	 @return 是否可以执行当前工作.
 * @throws Exception 
	*/
   public Boolean Flow_IsCanDoCurrentWork(long workid, String userNo) throws Exception;
   
   //public int GetNextNodeID(int nodeID, DataTable dirs) throws Exception;

	/** 
                   写入审核信息
    
    <param name="workid">workID</param>
    <param name="msg">审核信息</param>
    * 
    */
   public void Node_WriteWorkCheck(long workid, String msg) throws Exception;
   
   
   /**
    * 获取审核信息
    * @param FK_Flow 流程编号
    * @param FK_Node 节点编号
    * @param workId 流程ID
    * @param fid 干流程ID（针对子线程）
    * @param isReadonly 是否只读
    * @return
    * @throws Exception
    */
   public String DB_WorkCheck(String FK_Flow, int FK_Node,long workId,long fid,boolean isReadonly) throws Exception;

   
   /**
    * 获取流程时间轴数据
    * @param workid
    * @param fid
    * @param fk_flow
    * @throws Exception
    */
   public String Flow_TimeBase(long workid,long fid,String fk_flow) throws Exception;
   
   /**
    * 我参与的
    * @param userNo
    * @return
    * @throws Exception
    */
   public String DB_MyJoinFlows(String userNo) throws Exception;

	/***
	 * 根据流程WorkID、FK_Flow删除流程
	 * @param userNo
	 * @param workid
	 * @param fk_flow
	 * @return
	 * @throws Exception
	 */
   public String DeleteFlow(String userNo,long workid,String fk_flow) throws Exception;

	/**
	 * 上传附件
	 * @param fk_node 节点编号
	 * @param fk_flow 流程编号
	 * @param workid 流程WorkID
	 * @param athNo 附件属性编号
	 * @param fk_mapData 表单属性编号
	 * @param filePath 附件路径
	 * @param fileName 附件名称
	 * @param sort 附件分类
	 * @param fid 干流程ID
	 * @param pworkid 父流程ID
	 * @return
	 * @throws Exception
	 */
   public String CCForm_AddAth(int fk_node, String fk_flow, long workid, String athNo, String fk_mapData,
							   String filePath, String fileName, String sort, int fid, int pworkid) throws  Exception;


}
