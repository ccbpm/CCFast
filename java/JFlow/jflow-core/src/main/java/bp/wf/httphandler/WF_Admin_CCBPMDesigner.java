package bp.wf.httphandler;

import bp.da.*;
import bp.web.*;
import bp.wf.Glo;
import bp.wf.template.*;
import bp.wf.xml.*;
import bp.wf.port.admin2group.*;
import bp.difference.*;
import bp.*;
import bp.wf.*;
import java.util.*;
import java.time.*;

/**
 初始化函数
*/
public class WF_Admin_CCBPMDesigner extends bp.difference.handler.DirectoryPageBase
{
	/**
	 选择器

	 @return
	*/
	public final String SelectFlows_Init() throws Exception {
		String fk_flowsort = this.GetRequestVal("FK_FlowSort").substring(1);

		if (DataType.IsNullOrEmpty(fk_flowsort) == true || fk_flowsort.equals("undefined") == true)
		{
			fk_flowsort = "99";
		}
		DataSet ds = new DataSet();

		String sql = "";
		if (DBAccess.getAppCenterDBType() == DBType.MySQL)
		{
			sql = "SELECT CONCAT('F' , No) as No,Name, CONCAT('F' ,ParentNo) as ParentNo FROM WF_FlowSort WHERE No='" + fk_flowsort + "' OR ParentNo='" + fk_flowsort + "' ORDER BY Idx";
		}
		else
		{
			sql = "SELECT 'F' + No as No,Name, 'F' + ParentNo as ParentNo FROM WF_FlowSort WHERE No='" + fk_flowsort + "' OR ParentNo='" + fk_flowsort + "' ORDER BY Idx";
		}

		DataTable dtFlowSorts = DBAccess.RunSQLReturnTable(sql);
		//if (dtFlowSort.Rows.size() == 0)
		//{
		//    fk_dept = bp.web.WebUser.getDeptNo();
		//    sql = "SELECT No,Name,ParentNo FROM Port_Dept WHERE No='" + fk_dept + "' OR ParentNo='" + fk_dept + "' ORDER BY Idx ";
		//    dtDept = DBAccess.RunSQLReturnTable(sql);
		//}

		dtFlowSorts.TableName = "FlowSorts";
		ds.Tables.add(dtFlowSorts);

		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dtFlowSorts.Columns.get(0).ColumnName = "No";
			dtFlowSorts.Columns.get(1).ColumnName = "Name";
			dtFlowSorts.Columns.get(2).ColumnName = "ParentNo";
		}

		//sql = "SELECT No,Name, FK_Dept FROM Port_Emp WHERE FK_Dept='" + fk_dept + "' ";

		if (DBAccess.getAppCenterDBType() == DBType.MySQL)
		{

			sql = "SELECT  No,CONCAT(NO ,'.',NAME) as Name, CONCAT('F',FK_FlowSort) as ParentNo, Idx FROM WF_Flow where FK_FlowSort='" + fk_flowsort + "' ";
		}
		else
		{
			sql = "SELECT  No,(NO + '.' + NAME) as Name, 'F' + FK_FlowSort as ParentNo, Idx FROM WF_Flow where FK_FlowSort='" + fk_flowsort + "' ";
		}

		sql += " ORDER BY Idx ";

		DataTable dtFlows = DBAccess.RunSQLReturnTable(sql);
		dtFlows.TableName = "Flows";
		ds.Tables.add(dtFlows);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dtFlows.Columns.get(0).ColumnName = "No";
			dtFlows.Columns.get(1).ColumnName = "Name";
			dtFlows.Columns.get(2).ColumnName = "FK_FlowSort";
		}

		//转化为 json
		return bp.tools.Json.ToJson(ds);
	}

	/**
	 按照管理员登录.
	 @return 登录信息
	*/
	public final String AdminerChang_LoginAs() throws Exception {
		String orgNo = this.GetRequestVal("OrgNo");
		WebUser.setOrgNo(this.getOrgNo());
		return "info@登录成功, 如果系统不能自动刷新，请手工刷新。";
	}

	public final String Flows_Init() throws Exception {
		DataTable dt = new DataTable();

		dt.Columns.Add("FlowNo");
		dt.Columns.Add("FlowName");

		dt.Columns.Add("NumOfRuning"); //运行中的.
		dt.Columns.Add("NumOfOK"); //已经完成的.
		dt.Columns.Add("NumOfEtc"); //其他.

		Flows fls = new Flows();
		fls.RetrieveAll();

		for (Flow fl : fls.ToJavaList())
		{
			DataRow dr = dt.NewRow();
			dr.setValue("FlowNo", fl.getNo());
			dr.setValue("FlowName", fl.getName());
			dr.setValue("NumOfRuning", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) FROM  WF_GenerWorkFlow WHERE FK_Flow='" + fl.getNo() + "' AND WFState in (2,5)", 0));
			dr.setValue("NumOfOK", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) FROM  WF_GenerWorkFlow WHERE FK_Flow='" + fl.getNo() + "' AND WFState = 3 ", 0));
			dr.setValue("NumOfEtc", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) FROM  WF_GenerWorkFlow WHERE FK_Flow='" + fl.getNo() + "' AND WFState in (4,5,6,7,8) ", 0));

			dt.Rows.add(dr);
		}
		return bp.tools.Json.ToJson(dt);
	}

	/**
	 构造函数
	*/
	public WF_Admin_CCBPMDesigner()
	{
	}
	/**
	 执行流程设计图的保存.

	 @return
	*/
	public final String Designer_Save() throws Exception {

		if (WebUser.getIsAdmin() == false)
		{
			return "err@当前您【" + WebUser.getNo() + "," + WebUser.getName() + "】不是管理员,请重新登录.造成这种原因是您在测试容器没有正常退回造成的.";
		}

		String sql = "";
		try
		{
			Flow flow = new Flow(this.getFlowNo());

			StringBuilder sBuilder = new StringBuilder();

			//保存方向.
			sBuilder = new StringBuilder();
			String[] dirs = this.GetRequestVal("Dirs").split("[@]", -1);

			Direction mydir = new Direction();
			for (String item : dirs)
			{
				if (Objects.equals(item, "") || item == null)
				{
					continue;
				}
				String[] strs = item.split("[,]", -1);
				mydir.setMyPK(strs[0]);
				if (mydir.getIsExits() == true)
				{
					continue;
				}

				sBuilder.append("DELETE FROM WF_Direction WHERE MyPK='" + strs[0] + "';");

				sBuilder.append("INSERT INTO WF_Direction (MyPK,FK_Flow,Node,ToNode) VALUES ('" + strs[0] + "','" + strs[1] + "','" + strs[2] + "','" + strs[3] + "');");
			}
			DBAccess.RunSQLs(sBuilder.toString());

			//保存label位置.
			sBuilder = new StringBuilder();
			String[] labs = this.GetRequestVal("Labs").split("[@]", -1);
			for (String item : labs)
			{
				if (DataType.IsNullOrEmpty(item) == true)
				{
					continue;
				}
				String[] strs = item.split("[,]", -1);

				sBuilder.append("UPDATE WF_LabNote SET X=" + strs[1] + ",Y=" + strs[2] + " WHERE MyPK='" + strs[0] + "';");
			}

			String sqls = sBuilder.toString();
			DBAccess.RunSQLs(sqls);

			//更新节点 HisToNDs，不然就需要检查一遍.
			Nodes nds = new Nodes();
			nds.Retrieve(NodeAttr.FK_Flow, this.getFlowNo(), null);

			//获得方向集合处理toNodes
			Directions mydirs = new Directions(this.getFlowNo());

			String mystrs = "";
			for (Node item : nds.ToJavaList())
			{
				String strs = "";
				for (Direction dir : mydirs.ToJavaList())
				{
					if (dir.getNode() != item.getNodeID())
					{
						continue;
					}

					strs += "@" + dir.getToNode();
				}

				int nodePosType = 0;
				if (item.getItIsStartNode() == true)
				{
					nodePosType = 0;
				}
				else if (DataType.IsNullOrEmpty(strs) == true)
				{
					nodePosType = 2;
				}
				else
				{
					nodePosType = 1;
				}

				DBAccess.RunSQL("UPDATE WF_Node SET HisToNDs='" + strs + "',NodePosType=" + nodePosType + "  WHERE NodeID=" + item.getNodeID());

				DBAccess.RunSQL("UPDATE Sys_MapData SET Name='" + item.getName() + "' WHERE No='ND" + item.getNodeID() + "'");
			}

			//获取所有子流程
			String subs = this.GetRequestVal("SubFlows");
			int subFlowShowType = flow.GetValIntByKey(FlowAttr.SubFlowShowType);
			if (DataType.IsNullOrEmpty(subs) == false && subFlowShowType == 0)
			{
				String[] subFlows = subs.split("[@]", -1);
				for (String item : subFlows)
				{
					if (DataType.IsNullOrEmpty(item) == true)
					{
						continue;
					}
					String[] strs = item.split("[,]", -1);
					sBuilder.append("UPDATE WF_NodeSubFlow SET X=" + strs[1] + ",Y=" + strs[2] + " WHERE MyPK='" + strs[0] + "';");
				}
			}
			//保存节点位置. @101,2,30@102,3,1
			String[] nodes = this.GetRequestVal("Nodes").split("[@]", -1);
			for (String item : nodes)
			{
				if (DataType.IsNullOrEmpty(item) == true)
				{
					continue;
				}

				String[] strs = item.split("[,]", -1);
				String nodeID = strs[0]; //获得nodeID.
				if (subFlowShowType == 1 && subs.indexOf(nodeID) != -1)
				{
					String sub = subs.substring(subs.indexOf("@\"" + nodeID) + 1);
					if (sub.contains("@") == true)
					{
						sub = sub.substring(0, sub.indexOf("@"));
					}
					String[] subInfo = sub.split("[,]", -1);
					sBuilder.append("UPDATE WF_Node SET X=" + strs[1] + ",Y=" + strs[2] + ",Name='" + strs[3] + "',SubFlowX=" + subInfo[1] + ", SubFlowY=" + subInfo[2] + " WHERE NodeID=" + strs[0] + ";");
				}
				else
				{
					sBuilder.append("UPDATE WF_Node SET X=" + strs[1] + ",Y=" + strs[2] + ",Name='" + strs[3] + "' WHERE NodeID=" + strs[0] + ";");
				}
			}

			DBAccess.RunSQLs(sBuilder.toString());

			// DBAccess.RunSQL("update WF_Direction set ToNodeName=WF_Node.Name from WF_Node where //WF_Direction.ToNode=WF_node.getNodeID() AND WF_Direction.FK_FlOW='" + this.FlowNo+"'");


				///#region 更新节点名称.
			switch (SystemConfig.getAppCenterDBType())
			{
				case MSSQL:
				case KingBaseR3:
				case KingBaseR6:
				case PostgreSQL:
				case HGDB:
					sql = " UPDATE WF_Direction SET ToNodeName = WF_Node.Name,NodeType=WF_Node.NodeType FROM WF_Node  ";
					sql += " WHERE WF_Direction.ToNode = WF_Node.NodeID AND WF_Direction.FK_Flow='" + this.getFlowNo() + "'";
					break;
				case Oracle:
				case DM:
				case GBASE8CByOracle:
					 sql = "UPDATE WF_Direction E SET ToNodeName=(SELECT U.Name FROM WF_Node U WHERE E.ToNode=U.NodeID AND U.FK_Flow='" + this.getFlowNo() + "'), NodeType=(SELECT U.NodeType FROM WF_Node U WHERE E.ToNode=U.NodeID AND U.FK_Flow='" + this.getFlowNo() + "') WHERE EXISTS (SELECT 1 FROM WF_Node U WHERE E.ToNode=U.NodeID  AND U.FK_Flow='" + this.getFlowNo() + "')";
					break;
				default:
					sql = "UPDATE WF_Direction A, WF_Node B SET A.ToNodeName=B.Name,A.NodeType=B.NodeType WHERE A.ToNode=B.NodeID AND A.FK_Flow='" + this.getFlowNo() + "' ";
					break;
			}
			DBAccess.RunSQL(sql);

				///#endregion 更新节点名称.


			//清楚缓存.
		   Cache.ClearCache();
			// Node nd = new Node(102);
			// throw new Exception(nd.Name);

			return "保存成功.";

		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}

	/**
	 下载流程模版

	 @return
	*/
	public final String ExpFlowTemplete() throws Exception {
		Flow flow = new Flow(this.getFlowNo());
		String fileXml = flow.GenerFlowXmlTemplete();
		String docs = DataType.ReadTextFile(fileXml);
		return docs;
	}
	/**
	 返回临时文件.

	 @return
	*/
	public final String DownFormTemplete() throws Exception {
		DataSet ds = bp.sys.CCFormAPI.GenerHisDataSet_AllEleInfo(this.getFrmID());

		String file = SystemConfig.getPathOfTemp() + this.getFrmID() + ".xml";
		ds.WriteXml(file, XmlWriteMode.WriteSchema, ds);
		String docs = DataType.ReadTextFile(file);
		return docs;
	}

	/**
	 使管理员登录使管理员登录    ///
	 @return
	*/
	public final String LetLogin() throws Exception {
		LetAdminLogin(this.GetRequestVal("UserNo"), true);
		return "登录成功.";
	}
	/**
	 获得枚举列表的JSON.

	 @return
	*/
	public final String Logout() throws Exception {
		Dev2Interface.Port_SigOut();
		return "您已经安全退出,欢迎使用ccbpm.";
	}




		///#region 主页.
	/**
	 初始化登录界面.

	 @return
	*/
	public final String Default_Init()
	{
		try
		{
			//如果登录信息丢失了,就让其重新登录一次.
			if (DataType.IsNullOrEmpty(WebUser.getNoOfRel()) == true)
			{
				String userNo = this.GetRequestVal("UserNo");
				String sid = this.GetRequestVal("Token");
				Dev2Interface.Port_LoginByToken(sid);
			}

			if (WebUser.getIsAdmin() == false)
			{
				return "url@Login.htm?DoType=Logout&Err=NoAdminUsers";
			}

			//如果没有流程表，就执行安装.
			if (DBAccess.IsExitsObject("WF_Flow") == false)
			{
				return "url@../DBInstall.htm";
			}

			Hashtable ht = new Hashtable();

			ht.put("OSModel", "1");

			//把系统信息加入里面去.
			ht.put("SysNo", SystemConfig.getSysNo());
			ht.put("SysName", SystemConfig.getSysName());

			ht.put("CustomerNo", SystemConfig.getCustomerNo());
			ht.put("CustomerName", SystemConfig.getCustomerName());

			//集成的平台.
			ht.put("RunOnPlant", SystemConfig.getRunOnPlant());

			try
			{
				// 执行升级
				String str = Glo.UpdataCCFlowVer();
				if (str == null)
				{
					str = "";
				}
				ht.put("Msg", str);
			}
			catch (RuntimeException ex)
			{
				return "err@" + ex.getMessage();
			}

			//生成Json.
			return bp.tools.Json.ToJsonEntityModel(ht);
		}
		catch (Exception ex)
		{
			return "err@初始化界面期间出现如下错误:" + ex.getMessage();
		}
	}

	public final String Login_InitInfo()
	{
		Hashtable ht = new Hashtable();
		ht.put("SysNo", SystemConfig.getSysNo());
		ht.put("SysName", SystemConfig.getSysName());

		return bp.tools.Json.ToJson(ht);
	}
	/**
	 初始化登录界面.

	 @return
	*/
	public final String Login_Init() throws Exception {
		//检查数据库连接.
		try
		{
			DBAccess.TestIsConnection();
		}
		catch (RuntimeException ex)
		{
			return "err@异常信息:" + ex.getMessage();
		}

		//检查是否缺少Port_Emp 表，如果没有就是没有安装.
		if (DBAccess.IsExitsObject("Port_Emp") == false && DBAccess.IsExitsObject("WF_Flow") == false)
		{
			return "url@../DBInstall.htm";
		}

		////让admin登录
		//if (DataType.IsNullOrEmpty(bp.web.WebUser.getNo()) || bp.web.WebUser.getIsAdmin() == false)
		//    return "url@Login.htm?DoType=Logout";

		//如果没有流程表，就执行安装.
		if (DBAccess.IsExitsObject("WF_Flow") == false)
		{
			return "url@../DBInstall.htm";
		}

		//是否需要自动登录。 这里都把cookeis的数据获取来了.
		String userNo = this.GetRequestVal("UserNo");
		String sid = this.GetRequestVal("Token");

		if (DataType.IsNullOrEmpty(sid) == false && DataType.IsNullOrEmpty(userNo) == false)
		{
			/*  如果都有值，就需要他登录。 */
			try
			{
				String str = Glo.UpdataCCFlowVer();
				Dev2Interface.Port_LoginByToken(sid);
				if (this.getFlowNo() == null)
				{
					return "url@Default.htm?UserNo=" + userNo + "&OrgNo=" + WebUser.getOrgNo() + "&Key=" + new Date() + "&Token=" + sid;
				}
				else
				{
					return "url@Designer.htm?UserNo=" + userNo + "&OrgNo=" + WebUser.getOrgNo() + "&FK_Flow=" + this.getFlowNo() + "&Key=" + new Date() + "&Token=" + sid;
				}
			}
			catch (RuntimeException ex)
			{
				return "err@登录失败" + ex.getMessage();
			}
		}

		try
		{
			// 执行升级
			String str = Glo.UpdataCCFlowVer();
			if (str == null)
			{
				str = "准备完毕,欢迎登录,当前小版本号为:" + Glo.Ver;
			}
			return str;
		}
		catch (RuntimeException ex)
		{
			String msg = "err@升级失败(ccbpm有自动修复功能,您可以刷新一下系统会自动创建字段,刷新多次扔解决不了问题,请反馈给我们)";
			msg += "@系统信息:" + ex.getMessage();
			return msg;
		}
	}
	//流程设计器登录前台，转向规则，判断是否为天业BPM
	public final String Login_Redirect()
	{
		if (Objects.equals(SystemConfig.getCustomerNo(), "TianYe"))
		{
			return "url@../../../BPM/pages/login.html";
		}

		return "url@../../AppClassic/Login.htm?DoType=Logout";
	}
	/**
	初始化当前登录人的下的所有组织

	 @return
	*/
	public final String SelectOneOrg_Init() throws Exception {
	   Orgs orgs = new Orgs();
		orgs.RetrieveInSQL("SELECT OrgNo FROM Port_OrgAdminer WHERE FK_Emp='" + WebUser.getNo() + "'");
		return orgs.ToJson("dt");
	}
	/**
	选择一个组织

	 @return
	*/
	public final String SelectOneOrg_Selected() throws Exception {
		WebUser.setOrgNo(this.getOrgNo());

		//找到管理员所在的部门.
		String sql = "SELECT a.No FROM Port_Dept A,Port_DeptEmp B WHERE A.No=B.FK_Dept AND B.FK_Emp='" + WebUser.getNo() + "'  AND A.OrgNo='" + this.getOrgNo() + "'";
		String deptNo = DBAccess.RunSQLReturnStringIsNull(sql, this.getOrgNo());

		WebUser.setDeptNo(deptNo);

		//执行更新到用户表信息.
		WebUser.UpdateSIDAndOrgNoSQL();

		return "url@Default.htm?Token=" + WebUser.getToken() + "&UserNo=" + WebUser.getNo() + "&OrgNo=" + WebUser.getOrgNo();
		// return "登录成功.";
	}


	public final String DeleteNode() throws Exception {
		try
		{
			Node node = new Node();
			node.setNodeID(this.getNodeID());
			if (node.RetrieveFromDBSources() == 0)
			{
				return "err@删除失败,没有删除到数据，估计该节点已经别删除了.";
			}

			if (node.getItIsStartNode() == true)
			{
				return "err@开始节点不允许被删除。";
			}

			node.Delete();
			return "删除成功.";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}

	/**
	 让admin登录

	 @return
	*/
	public final String LetAdminLoginByToken()
	{
		try
		{
			String userNo = this.GetRequestVal("UserNo");
			String sid = this.GetRequestVal("Token");

			Dev2Interface.Port_LoginByToken(sid);

			return "info@登录成功";
		}
		catch (RuntimeException ex)
		{
			return "err@登录失败:" + ex.getMessage();
		}
	}
	/**
	 让admin 登录
	 @param empNo 当前的语言
	 @return 成功则为空，有异常时返回异常信息
	*/
	public final String LetAdminLogin(String empNo, boolean islogin) throws Exception {
		try
		{
			if (islogin)
			{
				bp.port.Emp emp = new bp.port.Emp(empNo);
				WebUser.SignInOfGener(emp, "CH", false, false, null, null);
			}
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
		return "@登录成功.";
	}

	public final String AdminerChange()
	{

		String mysql = "SELECT ";
		mysql += "No as \"No\", ";
		mysql += "Name as \"Name\", ";
		mysql += "UseSta as \"UseSta\", ";
		mysql += "RootOfDept as \"RootOfDept\" ";
		mysql += " FROM  WF_Emp WHERE No LIKE '" + this.GetRequestVal("UserNo") + "@%' ";
		DataTable dt = DBAccess.RunSQLReturnTable(mysql);
		return bp.tools.Json.ToJson(dt);
	}

	///#endregion

}
