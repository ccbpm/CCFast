package cn.jflow.ws;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;

import bp.da.DataTable;
 
@WebService
public interface PortalInterfaceWS {
	/// #region 发送消息接口. 需要与web.config中 ShortMessageWriteTo 配置才能起作用。
	/**
	 * 发送短信接口(二次开发需要重写这个接口) 
	 * 
	 * @param msgPK
	 *            消息主键，是对应的Sys_SMS的MyPK。
	 * @param sender
	 *            发送人(内部帐号，可以为空.)
	 * @param sendToEmpNo
	 *            发送给(内部帐号，可以为空.)
	 * @param tel
	 *            手机号码
	 * @param msgInfo
	 *            短消息
	 * @return 是否发送成功
	 */

	@WebMethod(operationName = "SendToWebServices")
	@WebResult(name = "result")
	public boolean SendToWebServices(String msgPK, String sender, String sendToEmpNo, String tel, String msgInfo,
			String tag);

	@WebMethod(operationName = "SendWhen")
	@WebResult(name = "result")
	public boolean SendWhen(String msgPK, String sender, String sendToEmpNo, String tel, String msgInfo);

	@WebMethod(operationName = "FlowOverBefore")
	@WebResult(name = "result")
	public boolean FlowOverBefore(String msgPK, String sender, String sendToEmpNo, String tel, String msgInfo);

	/**
	 * 发送丁丁的接口
	 * 
	 * @param msgPK
	 *            消息主键，是对应的Sys_SMS的MyPK。
	 * @param sender
	 *            发送人(内部帐号，可以为空.)
	 * @param sendToEmpNo
	 *            发送给(内部帐号，可以为空.)
	 * @param tel
	 *            电话
	 * @param msgInfo
	 *            消息内容
	 * @return 是否发送成功
	 */

	@WebMethod(operationName = "SendToDingDing")
	@WebResult(name = "result")
	public boolean SendToDingDing(String mypk, String sender, String sendToEmpNo, String tel, String msgInfo);

	/**
	 * 发送微信的接口
	 * 
	 * @param mypk
	 *            消息主键，是对应的Sys_SMS的MyPK。
	 * @param sender
	 *            发送人(内部帐号，可以为空.)
	 * @param sendToEmpNo
	 *            发送给(内部帐号，可以为空.)
	 * @param tel
	 * @param msgInfo
	 * @return 是否发送成功
	 */

	@WebMethod(operationName = "SendToWeiXin")
	@WebResult(name = "result")
	public boolean SendToWeiXin(String mypk, String sender, String sendToEmpNo, String tel, String msgInfo);

	/**
	 * 发送邮件接口
	 * 
	 * @param mypk
	 *            消息主键，是对应的Sys_SMS的MyPK。
	 * @param sender
	 *            发送人(内部帐号，可以为空.)
	 * @param sendToEmpNo
	 *            发送给(内部帐号，可以为空.)
	 * @param email
	 *            邮件地址
	 * @param title
	 *            标题
	 * @param maildoc
	 *            内容
	 * @param sendToEmpNo
	 *            接收人编号
	 * @return 是否发送成功
	 */

	@WebMethod(operationName = "SendToEmail")
	@WebResult(name = "result")
	public boolean SendToEmail(String mypk, String sender, String sendToEmpNo, String email, String title,
			String maildoc);

	/**
	 * 发送到CCIM即时通讯
	 * 
	 * @param mypk
	 *            主键
	 * @param email
	 *            邮件
	 * @param title
	 *            标题
	 * @param maildoc
	 *            内容
	 * @return 返回发送结果
	 */
	// C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted
	// to Java:
	// ORIGINAL LINE: public bool SendToCCIM(string mypk, string userNo, string
	// msg, string sourceUserNo, string tag = null)

	@WebMethod(operationName = "SendToCCIM")
	@WebResult(name = "result")
	public boolean SendToCCIM(String mypk, String userNo, String msg, String sourceUserNo, String tag);

	/// #endregion 发送消息接口.

	/// #region 其他的接口.
	/**
	 * 打印文件在处理.
	 * 
	 * @param billFilePath
	 */

	@WebMethod(operationName = "Print")
	@WebResult(name = "result")
	public void Print(String billFilePath);

	/// #endregion

	/// #region 组织结构.
	/**
	 * 用于单点登录的写入SID
	 * 
	 * @param miyue
	 *            配置在web.config中的密码，用于两个系统的握手.
	 * @param userNo
	 *            用户ID , 对应Port_Emp的No列.
	 * @param sid
	 *            用户SID , 对应Port_Emp的SID列.
	 * @return
	 */

	@WebMethod(operationName = "WriteUserSID")
	@WebResult(name = "result")
	public boolean WriteUserSID(String miyue, String userNo, String sid);

	/**
	 * 检查用户名密码是否正确
	 * 
	 * @param userNo
	 *            用户名
	 * @param password
	 *            密码
	 * @return 正确返回1，不正确返回0，其他的情况抛出异常。
	 */

	@WebMethod(operationName = "CheckUserNoPassWord")
	@WebResult(name = "result")
	public int CheckUserNoPassWord(String userNo, String password);

	/**
	 * 获得部门信息
	 * 
	 * @return 返回No,Name,ParentNo至少三个列的部门信息
	 */

	@WebMethod(operationName = "GetDept")
	@WebResult(name = "result")
	public DataTable GetDept(String deptNo);

	/**
	 * 获得部门信息
	 * 
	 * @return 返回No,Name,ParentNo至少三个列的部门信息
	 */

	@WebMethod(operationName = "GetDepts")
	@WebResult(name = "result")
	public DataTable GetDepts();

	/**
	 * 获得部门信息
	 * 
	 * @return 返回No,Name,ParentNo至少三个列的部门信息
	 */

	@WebMethod(operationName = "GetDeptsByParentNo")
	@WebResult(name = "result")
	public DataTable GetDeptsByParentNo(String parentDeptNo);

	/**
	 * 获得岗位信息
	 * 
	 * @return 返回No,Name,FK_StationType 至少三个列的岗位信息
	 */

	@WebMethod(operationName = "GetStations")
	@WebResult(name = "result")
	public DataTable GetStations();

	/**
	 * 获得岗位信息
	 * 
	 * @return 返回No,Name,FK_StationType至少三个列的岗位信息
	 */

	@WebMethod(operationName = "GetStation")
	@WebResult(name = "result")
	public DataTable GetStation(String stationNo);

	/**
	 * 获得人员信息(一人多部门)
	 * 
	 * @return 返回No,Name,FK_Dept至少三个列的部门、人员、岗位信息
	 */

	@WebMethod(operationName = "GetEmps")
	@WebResult(name = "result")
	public DataTable GetEmps();

	/**
	 * 获得人员信息(一人多部门)
	 * 
	 * @return 返回No,Name,FK_Dept至少三个列的部门、人员、岗位信息
	 */

	@WebMethod(operationName = "GetEmpsByDeptNo")
	@WebResult(name = "result")
	public DataTable GetEmpsByDeptNo(String deptNo);

	/**
	 * 获得人员信息(一人多部门)
	 * 
	 * @return 返回No,Name,FK_Dept至少三个列的部门、人员、岗位信息
	 */

	@WebMethod(operationName = "GetEmp")
	@WebResult(name = "result")
	public DataTable GetEmp(String no);

	/**
	 * 获得部门人员信息(一人多部门)
	 * 
	 * @return 返回FK_Dept,FK_Emp至少三个列的部门、人员、岗位信息
	 */

	@WebMethod(operationName = "GetDeptEmp")
	@WebResult(name = "result")
	public DataTable GetDeptEmp();

	/**
	 * 获得人员他的部门实体信息集合.
	 * 
	 * @return 返回No,Name,ParentNo部门信息
	 */

	@WebMethod(operationName = "GetEmpHisDepts")
	@WebResult(name = "result")
	public DataTable GetEmpHisDepts(String empNo);

	/**
	 * 获得人员他的岗位实体信息集合.
	 * 
	 * @return 返回No,Name,FK_StationType岗位信息
	 */

	@WebMethod(operationName = "GetEmpHisStations")
	@WebResult(name = "result")
	public DataTable GetEmpHisStations(String empNo);

	/**
	 * 获得部门人员岗位对应信息
	 * 
	 * @return 返回FK_Dept,FK_Emp,FK_Station至少三个列的部门、人员、岗位信息
	 */

	@WebMethod(operationName = "GetDeptEmpStations")
	@WebResult(name = "result")
	public DataTable GetDeptEmpStations();

	/// #endregion 组织结构.

	/// #region 特殊的查询
	/**
	 * 通过一组岗位编号获得他的人员集合
	 * 
	 * @param stationNos
	 *            用逗号隔开的岗位集合比如: '01','02'
	 * @return 返回No,Name,FK_Dept三个列.
	 */

	@WebMethod(operationName = "GenerEmpsByStations")
	@WebResult(name = "result")
	public DataTable GenerEmpsByStations(String stationNos);

	/**
	 * 通过一组部门编号获得他的人员集合
	 * 
	 * @param deptNos
	 *            用逗号隔开的部门集合比如: '01','02'
	 * @return 返回No,Name,FK_Dept三个列.
	 */

	@WebMethod(operationName = "GenerEmpsByDepts")
	@WebResult(name = "result")
	public DataTable GenerEmpsByDepts(String deptNos);

	/**
	 * 指定部门与一个岗位集合，获得他们的人员。
	 * 
	 * @param deptNo
	 *            部门编号
	 * @param stations
	 *            岗位编号s
	 * @return No,Name,FK_Dept三个列的人员信息
	 */

	@WebMethod(operationName = "GenerEmpsBySpecDeptAndStats")
	@WebResult(name = "result")
	public DataTable GenerEmpsBySpecDeptAndStats(String deptNo, String stations);

	/// #endregion

	/// #region 事件接口.
	/**
	 * 发送成功要执行的事件
	 * 
	 * @param flowNo
	 *            流程编号
	 * @param nodeID
	 *            节点ID
	 * @param workid
	 *            工作ID
	 * @param userNo
	 *            用户编号
	 * @param userName
	 *            用户名称
	 * @return 执行结果
	 */

	@WebMethod(operationName = "SendSuccess")
	@WebResult(name = "result")
	public String SendSuccess(String flowNo, int nodeID, long workid, String userNo, String userName);
	/// #endregion 事件接口.
}