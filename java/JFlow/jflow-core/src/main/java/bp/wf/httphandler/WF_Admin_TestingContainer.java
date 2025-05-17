package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.web.*;
import bp.port.*;
import bp.en.*; import bp.en.Map;
import bp.wf.template.*;
import bp.difference.*;
import bp.*;
import bp.wf.*;

/**
 页面功能实体
*/
public class WF_Admin_TestingContainer extends bp.difference.handler.DirectoryPageBase
{
	/**
	 构造函数
	*/
	public WF_Admin_TestingContainer()
	{
	}

	/**
	 测试页面初始化

	 @return
	*/
	public final String Default_Init() throws Exception {
		String testerNo = this.GetRequestVal("TesterNo");
		if (WebUser.getNo().equals(testerNo) == false)
		{
			WebUser.setToken("");
			if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
			{
				Dev2Interface.Port_Login(testerNo);
			}
			else
			{
				Dev2Interface.Port_Login(testerNo, WebUser.getOrgNo());
			}
		}

		long workid = Dev2Interface.Node_CreateBlankWork(this.getFlowNo(), testerNo);
		Flow flow = new Flow(this.getFlowNo());
		String testSysPara = flow.GetValStringByKey("TestSysPara","");
		String testFrmPara = flow.GetValStringByKey("TestFrmPara","");
		if(DataType.IsNullOrEmpty(testSysPara)==false){
			testSysPara = testSysPara.replace(" ","");
			Dev2Interface.Flow_SaveParas(workid,testSysPara);
		}
		if(DataType.IsNullOrEmpty(testFrmPara)==false){
			testFrmPara = testFrmPara.replace(" ","");
			AtPara atPara = new AtPara(testFrmPara);
			Dev2Interface.Node_SaveWork(workid,atPara.getHisHT());
		}
		return String.valueOf(workid);
	}
	/**
	 数据库信息

	 @return
	*/
	public final String DBInfo_Init() throws Exception {
		//数据容器，用于盛放数据，并返回json.
		DataSet ds = new DataSet();

		//流程引擎控制表.
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		ds.Tables.add(gwf.ToDataTableField("WF_GenerWorkFlow"));

		//流程引擎人员列表.
		GenerWorkerLists gwls = new GenerWorkerLists(this.getWorkID());
		gwls.Retrieve(GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.RDT);
		ds.Tables.add(gwls.ToDataTableField("WF_GenerWorkerlist"));


		//获得Track数据.
		String table = "ND" + Integer.parseInt(this.getFlowNo()) + "Track";
		String sql = "SELECT * FROM " + table + " WHERE WorkID=" + this.getWorkID();
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Track";
		//把列大写转化为小写.
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			Track tk = new Track();
			for (Attr attr : tk.getEnMap().getAttrs())
			{
				if (dt.Columns.contains(attr.getKey().toUpperCase()) == true)
				{
					dt.Columns.get(attr.getKey().toUpperCase()).ColumnName = attr.getKey();

				}
			}
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			Track tk = new Track();
			for (Attr attr : tk.getEnMap().getAttrs())
			{
				if (dt.Columns.contains(attr.getKey().toLowerCase()) == true)
				{
					dt.Columns.get(attr.getKey().toLowerCase()).ColumnName = attr.getKey();

				}
			}
		}
		ds.Tables.add(dt);

		//获得NDRpt表
		String rpt = "ND" + Integer.parseInt(this.getFlowNo()) + "Rpt";
		GEEntity en = new GEEntity(rpt);
		en.setPKVal(this.getWorkID());
		en.RetrieveFromDBSources();
		ds.Tables.add(en.ToDataTableField("NDRpt"));

		//转化为json ,返回出去.
		return bp.tools.Json.ToJson(ds);
	}
	/**
	 SelectOneUser_Init

	 @return
	*/
	public final String SelectOneUser_Init() throws Exception {
		GenerWorkerLists ens = new GenerWorkerLists();
		QueryObject qo = new QueryObject(ens);
		qo.AddWhere("IsEnable",1);
		qo.addAnd();
		qo.addLeftBracket();
		qo.AddWhere("WorkID", this.getWorkID());
		qo.addOr();
		qo.AddWhere("FID", this.getWorkID());
		qo.addRightBracket();
		qo.addOrderBy(" RDT,CDT,FK_Node ");
		qo.DoQuery();

		return ens.ToJson("dt");
	}

	/**
	 让adminer登录.

	 @return
	*/
	public final String Default_LetAdminerLogin() throws Exception {
		try
		{
			String token = this.GetRequestVal("Token");
			String userNo = Dev2Interface.Port_LoginByToken(token);
			//@lyc
		   // Dev2Interface.Port_GenerToken();
			return userNo;
		}
		catch (RuntimeException ex)
		{
			//@ 多人用同一个账号登录，就需要加上如下代码.
			if (DataType.IsNullOrEmpty(this.getUserNo()) == false)
			{
				Dev2Interface.Port_Login(this.getUserNo(), this.getOrgNo());
				//
				//Dev2Interface.Port_GenerToken();
				return this.getUserNo();
			}
			return ex.getMessage();
		}
	}
	/**
	 切换用户

	 @return
	*/
	public final String SelectOneUser_ChangUser()
	{

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			String adminer = this.GetRequestVal("Adminer");
			String SID = this.GetRequestVal("Token");
			try
			{
				Dev2Interface.Port_Login(this.getEmpNo());
				String token = Dev2Interface.Port_GenerToken();
				return token;
			}
			catch (Exception ex)
			{
				return "err@" + ex.getMessage();
			}
		}

		try
		{
			Dev2Interface.Port_Login(this.getEmpNo(), this.getOrgNo());
			String token = Dev2Interface.Port_GenerToken();
			return token;
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}

	}


		///#region TestFlow2020_Init
	/**
	 发起.

	 @return
	*/
	public final String TestFlow2020_StartIt() throws Exception {
		//此SID是管理员的SID.
		String testerNo = this.GetRequestVal("TesterNo");
		String orgNo = this.GetRequestVal("OrgNo");

		FlowExt fl = new FlowExt(this.getFlowNo());
		fl.setTester(testerNo);
		fl.Update();
		//选择的人员登录
		Dev2Interface.Port_Login(testerNo, orgNo);
		String token = Dev2Interface.Port_GenerToken(testerNo);

		int model = SystemConfig.getCCBPMRunModel().getValue();

		//组织url发起该流程.
		String url = "Default.html?RunModel=" + model + "&FK_Flow=" + this.getFlowNo() + "&TesterNo=" + testerNo;
		url += "&OrgNo=" + WebUser.getOrgNo();
		url += "&UserNo=" + this.GetRequestVal("TesterNo");
		url += "&Token=" + token;
		return url;
	}
	/**
	 初始化

	 @return
	*/
	public final String TestFlow2020_Init() throws Exception {
		//清除缓存.
		SystemConfig.DoClearCache();

		if (WebUser.getIsAdmin() == false)
		{
			return "err@您不是管理员，无法执行该操作.";
		}

		FlowExt fl = new FlowExt(this.getFlowNo());


			///#region 检查.
		int nodeid = Integer.parseInt(this.getFlowNo() + "01");
		DataTable dt = null;
		Node nd = new Node(nodeid);

			///#endregion 测试人员.


			///#region 从配置里获取-测试人员.
		/* 检查是否设置了测试人员，如果设置了就按照测试人员身份进入
		 * 设置测试人员的目的是太多了人员影响测试效率.
		 * */
		String tester = fl.getTester();
		if (tester != null && tester.length() > 2)
		{
			// 构造人员表.
			DataTable dtEmps = new DataTable();
			dtEmps.Columns.Add("No");
			dtEmps.Columns.Add("Name");
			dtEmps.Columns.Add("FK_DeptText");
			dtEmps.Columns.Add("DeptFullName");
			Emps emps = new Emps();
			if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			{
				emps.RetrieveIn(EmpAttr.UserID, "'" + tester.replace(",", "','") + "'", null);
			}
			else
			{
				emps.RetrieveIn(EmpAttr.No, "'" + tester.replace(",", "','") + "'", null);
			}

			if (emps.size() > 0)
			{
				for (Emp emp : emps.ToJavaList())
				{
					if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS && emp.getOrgNo().equals(WebUser.getOrgNo()) == false)
					{
						continue;
					}
					DataRow dr = dtEmps.NewRow();
					dr.setValue("No", emp.getUserID());
					dr.setValue("Name", emp.getName());
					dr.setValue("FK_DeptText", emp.getDeptText());
					dtEmps.Rows.add(dr);
				}
				return bp.tools.Json.ToJson(dtEmps);
			}

		}

			///#endregion 测试人员.

		//fl.DoCheck();
		try
		{

				///#region 从设置里获取-测试人员.
			String sql = "SELECT";
			if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
			{
				sql = "SELECT Top 500 ";
			}
			/**#region 从设置里获取-测试人员.
			*/
			switch (nd.getHisDeliveryWay())
			{
				case ByStation:
				case ByStationOnly:
					if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
					{
						sql += " Port_Emp.No AS No,Port_Emp.Name AS Name,Port_Dept.Name AS FK_DeptText  FROM Port_Emp LEFT JOIN Port_Dept ON  Port_Emp.FK_Dept=Port_Dept.No  join Port_DeptEmpStation ON (fk_emp=Port_Emp.No) join WF_NodeStation on (WF_NodeStation.fk_station=Port_DeptEmpStation.fk_station) WHERE (1=1) AND  FK_Node=" + nd.getNodeID();
					}
					if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
					{
						// 查询当前组织下所有的该岗位的人员.
						sql += " A. No,A.Name AS Name,D.Name AS FK_DeptText FROM Port_Emp A, Port_DeptEmpStation B, WF_NodeStation C,Port_Dept D ";
						sql += " WHERE A.OrgNo='" + WebUser.getOrgNo() + "' AND C.FK_Node=" + nd.getNodeID();
						sql += " AND A.No=B.FK_Emp AND B.FK_Station=C.FK_Station AND A.FK_Dept=D.No AND B.FK_Dept=D.No";
					}
					if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.SAAS)
					{
						sql += " A.UserID as No,A.Name AS Name,D.Name AS FK_DeptText FROM Port_Emp A, Port_DeptEmpStation B, WF_NodeStation C,Port_Dept D ";
						sql += " WHERE A.OrgNo='" + WebUser.getOrgNo() + "' AND C.FK_Node=" + nd.getNodeID();
						sql += " AND A.UserID=B.FK_Emp AND B.FK_Station=C.FK_Station AND A.FK_Dept=D.No AND B.FK_Dept=D.No";
					}
					break;
				case ByTeamOrgOnly: //按照组织智能计算。
				case ByTeamDeptOnly: //按照组织智能计算。
					sql += "  A." + bp.sys.base.Glo.getUserNo() + ",A.Name,D.Name AS FK_DeptText FROM Port_Emp A, WF_NodeTeam B, Port_TeamEmp C,Port_Dept D ";
					sql += " WHERE A." + bp.sys.base.Glo.getUserNoWhitOutAS() + "=C.FK_Emp AND B.FK_Team=C.FK_Team AND B.FK_Node=" + nd.getNodeID() + " AND A.OrgNo='" + WebUser.getOrgNo() + "' AND A.FK_Dept=D.No";
					break;
				case ByTeamOnly: //仅按用户组计算.

					sql += "  A." + bp.sys.base.Glo.getUserNo() + ",A.Name,D.Name AS FK_DeptText FROM Port_Emp A, WF_NodeTeam B, Port_TeamEmp C ,Port_Dept D";
					sql += " WHERE A.No=C.FK_Emp AND B.FK_Team=C.FK_Team AND B.FK_Node=" + nd.getNodeID() + " AND A.FK_Dept=D.No";
					break;
				case ByDept:
					sql += "  A." + bp.sys.base.Glo.getUserNo() + ",A.Name,C.Name AS FK_DeptText FROM Port_Emp A, WF_NodeDept B,Port_Dept C,Port_DeptEmp D WHERE D.FK_Emp=A.No AND D.FK_Dept=C.No  AND B.FK_Node=" + nodeid + "  AND B.FK_Dept=C.No";
					break;
				case ByBindEmp:
					if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
					{
						sql += "  A." + bp.sys.base.Glo.getUserNo() + ",A.Name,B.Name AS FK_DeptText FROM Port_Emp A,Port_Dept B WHERE A.FK_Dept=B.No AND A." + bp.sys.base.Glo.getUserNo() + " IN (SELECT FK_Emp from WF_NodeEmp where FK_Node='" + nodeid + "') AND OrgNo='" + WebUser.getOrgNo() + "'";
					}
					else
					{
						sql += "  A." + bp.sys.base.Glo.getUserNo() + ",A.Name,B.Name AS FK_DeptText FROM Port_Emp A,Port_Dept B  WHERE A.FK_Dept=B.No AND A." + bp.sys.base.Glo.getUserNo() + " in (SELECT FK_Emp from WF_NodeEmp where FK_Node='" + nodeid + "') ";
					}
					break;
				case ByDeptAndStation:
					sql += "  pdes.FK_Emp AS No,A.Name AS Name,B.Name AS FK_DeptText  FROM  Port_DeptEmpStation pdes, WF_NodeDept wnd,WF_NodeStation wns,Port_Emp A,Port_Dept B WHERE pdes.FK_Emp=A.No AND pdes.FK_Dept=B.No AND A.FK_Dept=B.NO AND  wnd.FK_Dept = pdes.FK_Dept  AND wnd.NodeID = " + nodeid + " AND  wns.FK_Station = pdes.StationNo  AND wnd.NodeID =" + nodeid + " ORDER BY" + "  pdes.FK_Emp";
					break;
				case BySelected: //所有的人员多可以启动, 2016年11月开始约定此规则.
					if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
					{
						sql += "  A.No, A.Name, B.Name as FK_DeptText FROM  Port_Emp A, Port_Dept B WHERE A.FK_Dept=B.No";
					}
					else
					{
						sql += "  A." + bp.sys.base.Glo.getUserNo() + ", A.Name, B.Name as FK_DeptText FROM  Port_Emp A, Port_Dept B WHERE A.FK_Dept=B.No ";
						sql += " AND A.OrgNo='" + WebUser.getOrgNo() + "' ";
						sql += " AND B.OrgNo='" + WebUser.getOrgNo() + "' ";
					}
					break;
				case BySelectedOrgs: //按照设置的组织计算: 20202年3月开始约定此规则.
					if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
					{
						return "err@非集团版本，不能设置启用此模式.";
					}
					sql += "  A." + bp.sys.base.Glo.getUserNo() + ",A.Name,C.Name as FK_DeptText FROM Port_Emp A, WF_FlowOrg B, Port_Dept C ";
					sql += " WHERE A.OrgNo = B.OrgNo AND B.FlowNo = '" + this.getFlowNo() + "' AND A.FK_Dept=c.No ";
					break;
				case BySQL:
					if (DataType.IsNullOrEmpty(nd.getDeliveryParas()))
					{
						return "err@您设置的按SQL访问开始节点，但是您没有设置sql.";
					}
					break;
				default:
					break;
			}
			if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB)
			{
				sql += " Limit 500";
			}
			dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
			{
				return "err@您按照:[" + nd.getHisDeliveryWay() + "]的方式设置的开始节点的访问规则，但是开始节点没有人员.";
			}

			if (dt.Rows.size() > 500)
			{
				return "err@可以发起开始节点的人员太多，会导致系统崩溃变慢，请<a href='javascript:SetTester()' >设置测试发起人</a>。";
			}

			// 构造人员表.
			DataTable dtMyEmps = new DataTable();
			dtMyEmps.Columns.Add("No");
			dtMyEmps.Columns.Add("Name");
			dtMyEmps.Columns.Add("FK_DeptText");

			//处理发起人数据.
			String emps = "";
			for (DataRow dr : dt.Rows)
			{
				String myemp = dr.getValue(0).toString();
				if (emps.contains("," + myemp + ",") == true)
				{
					continue;
				}

				emps += "," + myemp + ",";


				DataRow drNew = dtMyEmps.NewRow();

				drNew.setValue("No", myemp);
				drNew.setValue("Name", dr.getValue(1).toString());
				drNew.setValue("FK_DeptText", dr.getValue(2).toString());

				dtMyEmps.Rows.add(drNew);
			}
			//返回数据源.
			return bp.tools.Json.ToJson(dtMyEmps);

				///#endregion 从设置里获取-测试人员.

		}
		catch (RuntimeException ex)
		{
			return "err@您没有正确的设置开始节点的访问规则，这样导致没有可启动的人员，方法：TestFlow2020_Init。系统错误提示:" + ex.getMessage();
		}
	}

		///#endregion

}
