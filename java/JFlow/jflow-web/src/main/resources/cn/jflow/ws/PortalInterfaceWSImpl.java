package cn.jflow.ws;

import javax.jws.WebService;

import bp.da.*;

/**
 * OverrideInterface 的摘要说明
 * 
 */
// 若要允许使用 ASP.NET AJAX 从脚本中调用此 Web 服务，请取消对下行的注释。
// [System.Web.Script.Services.ScriptService]

// [WebService(Namespace = "http://tempuri.org/"), WebServiceBinding(ConformsTo
// = WsiProfiles.BasicProfile1_1), System.ComponentModel.ToolboxItem(false)]
@Deprecated
@WebService(endpointInterface="cn.jflow.ws.PortalInterfaceWS",serviceName="PortalInterfaceWS")
public class PortalInterfaceWSImpl implements PortalInterfaceWS{

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

	// [WebMethod(EnableSession = true)]
	public final boolean SendToWebServices(String msgPK, String sender, String sendToEmpNo, String tel, String msgInfo,
			String tag) {
		String json = "{";
		json += " \"Name\": 'xxxx',";
		json += " \"XB\": 'xxxx',";
		json += " \"Addr\": 'xxxx',";
		json += " \"Tel\": 'xxxx'}";

		// BP.DA.Log.DefaultLogWriteLineInfo("接口调用成功: SendToWebServices " + tel
		// + " msgInfo:" + msgInfo);
		// if (bp.sys.SystemConfig.IsEnableCCIM && sendToEmpNo != null)
		// bp.wf.Glo.SendMessageToCCIM(sender, sendToEmpNo, msgInfo,
		// BP.DA.DataType.getCurrentDate()Time);
		return true;
	}

	// [WebMethod(EnableSession = true)]
	public final boolean SendWhen(String msgPK, String sender, String sendToEmpNo, String tel, String msgInfo) {
		// BP.DA.Log.DefaultLogWriteLineInfo("接口调用成功: SendToWebServices " + tel
		// + " msgInfo:" + msgInfo);
		// if (bp.sys.SystemConfig.IsEnableCCIM && sendToEmpNo != null)
		// bp.wf.Glo.SendMessageToCCIM(sender, sendToEmpNo, msgInfo,
		// BP.DA.DataType.getCurrentDate()Time);
		return true;
	}

	// [WebMethod(EnableSession = true)]
	public final boolean FlowOverBefore(String msgPK, String sender, String sendToEmpNo, String tel, String msgInfo) {
		// BP.DA.Log.DefaultLogWriteLineInfo("接口调用成功: SendToWebServices " + tel
		// + " msgInfo:" + msgInfo);
		// if (bp.sys.SystemConfig.IsEnableCCIM && sendToEmpNo != null)
		// bp.wf.Glo.SendMessageToCCIM(sender, sendToEmpNo, msgInfo,
		// BP.DA.DataType.getCurrentDate()Time);
		return true;
	}

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

	// [WebMethod(EnableSession = true)]
	public final boolean SendToDingDing(String mypk, String sender, String sendToEmpNo, String tel, String msgInfo) {
		// BP.DA.Log.DefaultLogWriteLineInfo("接口调用成功: SendToWebServices MyPK" +
		// mypk +" UserNo:"+userNo+ " Tel:" + tel + " msgInfo:" + msgInfo);

		// if (bp.sys.SystemConfig.IsEnableCCIM && sendToEmpNo != null)
		// bp.wf.Glo.SendMessageToCCIM(sender, sendToEmpNo, msgInfo,
		// BP.DA.DataType.getCurrentDate()Time);
		return true;
	}

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

	// [WebMethod]
	public final boolean SendToWeiXin(String mypk, String sender, String sendToEmpNo, String tel, String msgInfo) {
		// BP.DA.Log.DefaultLogWriteLineInfo("接口调用成功: SendToWeiXin MyPK" + mypk
		// + " UserNo:" + userNo + " Tel:" + tel + " msgInfo:" + msgInfo);

		if (bp.sys.SystemConfig.getIsEnableCCIM() && sendToEmpNo != null) {
			bp.wf.Glo.SendMessageToCCIM(sender, sendToEmpNo, msgInfo, BP.DA.DataType.getCurrentDataTime());
		}
		return true;
	}

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

	// [WebMethod]
	public final boolean SendToEmail(String mypk, String sender, String sendToEmpNo, String email, String title,
			String maildoc) {
		// BP.DA.Log.DefaultLogWriteLineInfo("接口调用成功: SendToEmail MyPK" + mypk +
		// " email:" + email + " title:" + title + " maildoc:" + maildoc);
		if (bp.sys.SystemConfig.getIsEnableCCIM() && sendToEmpNo != null) {
			bp.wf.Glo.SendMessageToCCIM(sender, sendToEmpNo, title + " \t\n " + maildoc,
					BP.DA.DataType.getCurrentDataTime());
		}
		return true;
	}

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

	// [WebMethod]
	public final boolean SendToCCIM(String mypk, String userNo, String msg, String sourceUserNo, String tag) {
		// BP.DA.Log.DefaultLogWriteLineInfo("接口调用成功: SendToEmail MyPK" + mypk +
		// " userNo:" + userNo + " msg:" + msg);

		if (bp.sys.SystemConfig.getIsEnableCCIM() && userNo != null) {
			bp.wf.Glo.SendMessageToCCIM(bp.web.WebUser.getNo(), userNo, msg, BP.DA.DataType.getCurrentDataTime());
		}
		// BP.CCIM.Glo.SendMsg(userNo, sourceUserNo, msg);
		return true;
	}

	/// #endregion 发送消息接口.

	/// #region 其他的接口.
	/**
	 * 打印文件在处理.
	 * 
	 * @param billFilePath
	 */

	// [WebMethod]
	public final void Print(String billFilePath) {
	}

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

	// [WebMethod]
	public final boolean WriteUserSID(String miyue, String userNo, String sid) {

		/// #region 简单Demo
		try {
			if (!miyue.equals("xxweerwerew")) {
				return false;
			}

			if (userNo.contains(" ") == true) {
				return false;
			}

			if (BP.DA.DBAccess.IsView("Port_Emp") == true) {
				return false;
			}
			String sql = "UPDATE Port_Emp SET SID='" + sid + "' WHERE No='" + userNo + "'";
			BP.DA.DBAccess.RunSQL(sql);
			return true;
		} catch (RuntimeException ex) {
			return false;
		}

		/// #endregion 简单Demo
	}

	/**
	 * 检查用户名密码是否正确
	 * 
	 * @param userNo
	 *            用户名
	 * @param password
	 *            密码
	 * @return 正确返回1，不正确返回0，其他的情况抛出异常。
	 */

	// [WebMethod]
	public final int CheckUserNoPassWord(String userNo, String password) {

		/// #region 简单Demo
		try {
			String sql = "SELECT Pass FROM Port_Emp WHERE No='" + userNo + "'";
			Object tempVar = BP.DA.DBAccess.RunSQLReturnVal(sql);
			String pass = (String) ((tempVar instanceof String) ? tempVar : null);
			if (password.equals(pass)) {
				return 1; // 成功返回1.
			}
			return 0; // 失败返回0.
		} catch (RuntimeException ex) {
			throw new RuntimeException("@校验出现错误:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单Demo
	}

	/**
	 * 获得部门信息
	 * 
	 * @return 返回No,Name,ParentNo至少三个列的部门信息
	 */

	// [WebMethod]
	public final DataTable GetDept(String deptNo) {

		/// #region 简单 Demo
		try {
			return BP.DA.DBAccess.RunSQLReturnTable("SELECT No,Name,ParentNo FROM Port_Dept WHERE No='" + deptNo + "'");
		} catch (RuntimeException ex) {
			throw new RuntimeException("@获得部门出现错误:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

	/**
	 * 获得部门信息
	 * 
	 * @return 返回No,Name,ParentNo至少三个列的部门信息
	 */

	// [WebMethod]
	public final DataTable GetDepts() {

		/// #region 简单 Demo
		try {
			return BP.DA.DBAccess.RunSQLReturnTable("SELECT No,Name,ParentNo FROM Port_Dept ORDER BY ParentNo,No");
		} catch (RuntimeException ex) {
			throw new RuntimeException("@获得部门出现错误:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

	/**
	 * 获得部门信息
	 * 
	 * @return 返回No,Name,ParentNo至少三个列的部门信息
	 */

	// [WebMethod]
	public final DataTable GetDeptsByParentNo(String parentDeptNo) {

		/// #region 简单 Demo
		try {
			return BP.DA.DBAccess.RunSQLReturnTable("SELECT No,Name,ParentNo FROM Port_Dept WHERE ParentNo='"
					+ parentDeptNo + "' ORDER BY ParentNo,No");
		} catch (RuntimeException ex) {
			throw new RuntimeException("@获得部门出现错误:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

	/**
	 * 获得岗位信息
	 * 
	 * @return 返回No,Name,FK_StationType 至少三个列的岗位信息
	 */

	// [WebMethod]
	public final DataTable GetStations() {

		/// #region 简单 Demo
		try {
			return BP.DA.DBAccess
					.RunSQLReturnTable("SELECT No,Name,FK_StationType FROM Port_Station ORDER BY FK_StationType,No");
		} catch (RuntimeException ex) {
			throw new RuntimeException("@获得岗位出现错误:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

	/**
	 * 获得岗位信息
	 * 
	 * @return 返回No,Name,FK_StationType至少三个列的岗位信息
	 */

	// [WebMethod]
	public final DataTable GetStation(String stationNo) {

		/// #region 简单 Demo
		try {
			return BP.DA.DBAccess
					.RunSQLReturnTable("SELECT No,Name,FK_StationType FROM Port_Station WHERE No='" + stationNo + "'");
		} catch (RuntimeException ex) {
			throw new RuntimeException("@获得岗位出现错误:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

	/**
	 * 获得人员信息(一人多部门)
	 * 
	 * @return 返回No,Name,FK_Dept至少三个列的部门、人员、岗位信息
	 */

	// [WebMethod]
	public final DataTable GetEmps() {

		/// #region 简单 Demo
		try {
			return BP.DA.DBAccess.RunSQLReturnTable(
					"SELECT a.No,a.Name,a.FK_Dept,b.Name as FK_DeptText FROM Port_Emp a, Port_Dept b WHERE (a.FK_Dept=b.No) ");
		} catch (RuntimeException ex) {
			throw new RuntimeException("@获得人员信息:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

	/**
	 * 获得人员信息(一人多部门)
	 * 
	 * @return 返回No,Name,FK_Dept至少三个列的部门、人员、岗位信息
	 */

	// [WebMethod]
	public final DataTable GetEmpsByDeptNo(String deptNo) {

		/// #region 简单 Demo
		try {
			return BP.DA.DBAccess.RunSQLReturnTable(
					"SELECT a.No,a.Name,a.FK_Dept,b.Name as FK_DeptText FROM Port_Emp a, Port_Dept b WHERE a.FK_Dept=b.No AND A.FK_Dept='"
							+ deptNo + "' ");
		} catch (RuntimeException ex) {
			throw new RuntimeException("@获得人员信息:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

	/**
	 * 获得人员信息(一人多部门)
	 * 
	 * @return 返回No,Name,FK_Dept至少三个列的部门、人员、岗位信息
	 */

	// [WebMethod]
	public final DataTable GetEmp(String no) {

		/// #region 简单 Demo
		try {
			return BP.DA.DBAccess.RunSQLReturnTable(
					"SELECT a.No,a.Name,a.FK_Dept,b.Name as FK_DeptText FROM Port_Emp a, Port_Dept b WHERE (a.No='" + no
							+ "') AND (a.FK_Dept=b.No) ");
		} catch (RuntimeException ex) {
			throw new RuntimeException("@获得人员信息:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

	/**
	 * 获得部门人员信息(一人多部门)
	 * 
	 * @return 返回FK_Dept,FK_Emp至少三个列的部门、人员、岗位信息
	 */

	// [WebMethod]
	public final DataTable GetDeptEmp() {

		/// #region 简单 Demo
		try {

			return BP.DA.DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Emp FROM Port_DeptEmp");
		} catch (RuntimeException ex) {
			throw new RuntimeException("@获得部门人员信息:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

	/**
	 * 获得人员他的部门实体信息集合.
	 * 
	 * @return 返回No,Name,ParentNo部门信息
	 */

	// [WebMethod]
	public final DataTable GetEmpHisDepts(String empNo) {

		/// #region 简单 Demo
		try {
			String sql = "SELECT No,Name,ParentNo FROM Port_Dept WHERE No IN(SELECT FK_Dept FROM Port_DeptEmp WHERE FK_Emp='"
					+ empNo + "')";
			return BP.DA.DBAccess.RunSQLReturnTable(sql);
		} catch (RuntimeException ex) {
			throw new RuntimeException("@获得人员他的部门实体信息:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

	/**
	 * 获得人员他的岗位实体信息集合.
	 * 
	 * @return 返回No,Name,FK_StationType岗位信息
	 */

	// [WebMethod]
	public final DataTable GetEmpHisStations(String empNo) {

		/// #region 简单 Demo
		try {
			String sql = "SELECT No,Name,FK_StationType FROM Port_Station WHERE No IN(SELECT FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='"
					+ empNo + "')";
			return BP.DA.DBAccess.RunSQLReturnTable(sql);
		} catch (RuntimeException ex) {
			throw new RuntimeException("@获得人员他的岗位实体信息:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

	/**
	 * 获得部门人员岗位对应信息
	 * 
	 * @return 返回FK_Dept,FK_Emp,FK_Station至少三个列的部门、人员、岗位信息
	 */

	// [WebMethod]
	public final DataTable GetDeptEmpStations() {

		/// #region 简单 Demo
		try {
			return BP.DA.DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Emp,FK_Station FROM Port_DeptEmpStation");
		} catch (RuntimeException ex) {
			throw new RuntimeException("@获得部门人员岗位对应信息:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

	/// #endregion 组织结构.

	/// #region 特殊的查询
	/**
	 * 通过一组岗位编号获得他的人员集合
	 * 
	 * @param stationNos
	 *            用逗号隔开的岗位集合比如: '01','02'
	 * @return 返回No,Name,FK_Dept三个列.
	 */

	// [WebMethod]
	public final DataTable GenerEmpsByStations(String stationNos) {

		/// #region 简单 Demo
		try {
			return BP.DA.DBAccess.RunSQLReturnTable(
					"SELECT a.No,a.Name,a.FK_Dept FROM Port_Emp A, Port_DeptEmpStation B WHERE A.No=B.FK_Emp AND B.FK_Station IN ("
							+ stationNos + ")");
		} catch (RuntimeException ex) {
			throw new RuntimeException("@根据岗位集合，获得人员集合错误:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

	/**
	 * 通过一组部门编号获得他的人员集合
	 * 
	 * @param deptNos
	 *            用逗号隔开的部门集合比如: '01','02'
	 * @return 返回No,Name,FK_Dept三个列.
	 */

	// [WebMethod]
	public final DataTable GenerEmpsByDepts(String deptNos) {

		/// #region 简单 Demo
		try {
			return BP.DA.DBAccess.RunSQLReturnTable(
					"SELECT a.No,a.Name,a.FK_Dept FROM Port_Emp A, Port_DeptEmp B WHERE A.No=B.No AND B.FK_Dept IN ("
							+ deptNos + ")");
		} catch (RuntimeException ex) {
			throw new RuntimeException("@根据部门集合，获得人员集合错误:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

	/**
	 * 指定部门与一个岗位集合，获得他们的人员。
	 * 
	 * @param deptNo
	 *            部门编号
	 * @param stations
	 *            岗位编号s
	 * @return No,Name,FK_Dept三个列的人员信息
	 */

	// [WebMethod]
	public final DataTable GenerEmpsBySpecDeptAndStats(String deptNo, String stations) {

		/// #region 简单 Demo
		try {
			String sql = "SELECT a.No,a.Name,a.FK_Dept FROM Port_Emp A, Port_DeptEmpStation B WHERE A.No=B.FK_Emp AND B.FK_Station IN ("
					+ stations + ") AND A.FK_Dept='" + deptNo + "'";
			return BP.DA.DBAccess.RunSQLReturnTable(sql);
		} catch (RuntimeException ex) {
			throw new RuntimeException("@ 指定部门与一个岗位集合，获得他们的人员:" + ex.getMessage()); // 连接错误，直接抛出异常.
		}

		/// #endregion 简单 Demo
	}

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

	// [WebMethod]
	public final String SendSuccess(String flowNo, int nodeID, long workid, String userNo, String userName) {
		return null;
	}

	/// #endregion 事件接口.

}