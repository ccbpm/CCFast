package bp.wf;

import bp.en.*;
import bp.da.*;
import bp.port.*;
import bp.web.WebUser;
import bp.wf.template.*;
import bp.difference.*;
import java.util.*;

/**
 抄送工作逻辑
*/
public class WorkCC
{

		///#region 身份.
	private WebUserCopy _webUserCopy = null;
	public final WebUserCopy getWebUser() throws Exception {
		if (_webUserCopy == null)
		{
			_webUserCopy = new WebUserCopy();
			_webUserCopy.LoadWebUser();
		}
		return _webUserCopy;
	}
	public final void setWebUser(WebUserCopy value)
	{
		_webUserCopy = value;
	}

		///#endregion 身份.

	public WorkNode currWN = null;
	public long WorkID = 0;
	/**
	 构造WorkCC
	 @param webUser
	*/
	public WorkCC(WorkNode userWN, WebUserCopy webUser)
	{
		this.currWN = userWN;
		this.WorkID =this.currWN.getWorkID();
		this.setWebUser(webUser);
	}
	/**
	 执行抄送.
	*/
	public final void DoCC(String type) throws Exception {
		Node nd = currWN.getHisNode();
		Node curND = currWN.getSkipNode();
		if(curND!=null && nd.getNodeID()!=curND.getNodeID())
			nd = curND;
		//查询出来到达的抄送节点.
		Directions dirs = new Directions();
		dirs.Retrieve(DirectionAttr.Node, nd.getNodeID(), DirectionAttr.NodeType, NodeType.CCNode.getValue(), DirectionAttr.Idx);
		if (dirs.size() == 0)
		{
			return; //如果没有抄送节点，就不处理.
		}

		//定义容器集合,获得可以抄送的节点.
		Nodes ccNodes = new Nodes();
		for (Direction dir : dirs.ToJavaList())
		{
			Conds conds = new Conds();
			int i = conds.Retrieve(CondAttr.FK_Node, nd.getNodeID(), CondAttr.ToNodeID, dir.getToNode(), CondAttr.CondType, CondType.Dir.getValue(), CondAttr.Idx);

			//判断是否通过.
			if (i == 0 || conds.GenerResult(currWN.rptGe, this.getWebUser()) == true)
			{
				Node ccNode = new Node();
				ccNode.setNodeID(dir.getToNode());
				ccNode.Retrieve();
				ccNodes.AddEntity(ccNode); //加入到集合.
				continue;
			}
		}
		if (ccNodes.size() == 0)
		{
			return;
		}

		//定义容器:需要多个规则把他们合并.
		DataTable dtCCers = new DataTable();
		dtCCers.Columns.Add("EmpNo");
		dtCCers.Columns.Add("EmpName");
		dtCCers.Columns.Add("CCNodeID");
		dtCCers.Columns.Add("CCNodeName");
		dtCCers.Columns.Add("NodeID"); //抄送节点的ID.
		dtCCers.Columns.Add("InEmpWorks");
		String empNames="";
		//执行cc
		for (Node myCCNode : ccNodes.ToJavaList())
		{
			//获得抄送规则集合.
			CCRoles rols = new CCRoles();
			rols.Retrieve(CCRoleAttr.NodeID, myCCNode.getNodeID(), "Idx");

			String emps = "";
			String cctoEmps = "";
			//遍历岗位规则集合.
			for (CCRole rol : rols.ToJavaList())
			{
				//获得抄送人的集合.
				DataTable dt = GenerCCers(rol, this.currWN.rptGe, this.WorkID);

				//把数据放进去.
				for (DataRow dr : dt.Rows)
				{
					String empNo = dr.getValue(0).toString();
					if (DataType.IsNullOrEmpty(empNo) == true)
					{
						continue;
					}

					if (emps.contains(empNo + ",") == false)
					{
						emps += empNo + ",";
						//把他加入到集合里.
						DataRow mydr = dtCCers.NewRow();
						mydr.setValue(0, empNo); //EmpNo
						if (dt.Columns.size()== 2)
						{
							mydr.setValue(1, dr.getValue(1).toString()); //EmpName
							//empNames+=mydr.getValue(1)+",";
							cctoEmps+=mydr.getValue(1)+",";
						}

						//节点ID.
						mydr.setValue(2, rol.getNodeID()); //抄送的节点.
						mydr.setValue(3, myCCNode.getName()); //抄送的节点.
						mydr.setValue(4, myCCNode.getNodeID()); //抄送的节点.
						mydr.setValue(5, myCCNode.getCCWriteTo() ==CCWriteTo.CCList?0:1); //抄送的节点.
						dtCCers.Rows.add(mydr);
					}
				}
			}

			if(type.equals("WorkNode") && DataType.IsNullOrEmpty(emps) == false)
			{
				emps = emps.substring(0,emps.length()-1);
				emps = "'"+emps.replace(",","','")+"'";
				String sql = "SELECT GROUP_CONCAT(Name) FROM Port_Emp WHERE No IN("+emps+")";
				if(SystemConfig.getAppCenterDBType() == DBType.MSSQL){
					sql=" SELECT Names=STUFF((Select ','+Name FROM Port_Emp WHERE No In("+emps+") FOR xml path('')),1,1,'' )";
				}
				if(SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.DM  ||  SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle){
					sql=" SELECT WM_CONCAT(TO_CHAR(Name) ) FROM Port_Emp WHERE No In("+emps+")";
				}
				if(SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB){
					sql="SELECT string_agg(name,',') FROM Port_Emp WHERE No IN("+emps+")";
				}
				cctoEmps = DBAccess.RunSQLReturnString(sql);
				empNames+=cctoEmps;
				Glo.AddToTrack(ActionType.CC, nd.getFlowNo(), this.WorkID, 0, currWN.getHisNode().getNodeID(), currWN.getHisNode().getName(), WebUser.getNo(), WebUser.getName(), myCCNode.getNodeID(), myCCNode.getName(), emps, cctoEmps, "来自" + WebUser.getName() + "的抄送", null);
			}
		}

		//写入数据：dtCCers
		long fid = this.currWN.getHisGenerWorkFlow().getFID();
		String flowNo = this.currWN.getHisGenerWorkFlow().getFlowNo();
		String flowName = this.currWN.getHisGenerWorkFlow().getFlowName();

		String title = this.currWN.getHisGenerWorkFlow().getTitle();
		long workID = this.currWN.getWorkID();
		int nodeIDMain = nd.getNodeID(); //节点ID.
		String nodeMainName = nd.getName(); //节点ID.
		String sqls = "";

		DBAccess.RunSQL("DELETE FROM WF_CCList WHERE WorkID=" + workID + " AND NodeIDWork=" + nodeIDMain);

		for (DataRow mydr : dtCCers.Rows)
		{
			String empNo = mydr.getValue(0).toString();
			String nodeID = mydr.getValue(4).toString();
			String nodeName = mydr.getValue(3).toString();
			int InEmpWorks = mydr.getValue(5)==null?0:Integer.parseInt(mydr.getValue(5).toString());
			//String nodeName = mydr[3).toString();
			String mypk = workID + "_" + nodeIDMain + "_" + empNo;
			//判断MyPK是否已经存在....
			if (DBAccess.RunSQLReturnValInt("SELECT COUNT(*) FROM WF_CCList WHERE MyPK = '" + mypk + "'") > 0)
				continue;
			sqls += "@ INSERT INTO WF_CCList (MyPK,WorkID,FID,CCTo,NodeIDWork,FlowNo,Title,RDT,NodeIDCC,NodeIDCCName,InEmpWorks) ";
			sqls += " VALUES ('" + mypk + "'," + workID + ","+fid+",'" + empNo + "'," + nodeIDMain + ",'" + flowNo + "','" + title + "','" + DataType.getCurrentDateTime() + "',"+ nodeID+",'"+nodeName+"',"+InEmpWorks+")";


			Dev2Interface.Port_SendMsg(empNo, "抄送:"+title, "CC" +nd.getNodeID() + "_" + workID + "_", SMSMsgType.CC, "CC", nd.getFlowNo(), nd.getNodeID(), workID, 0, null);


		}
		sqls += "";
		DBAccess.RunSQLs(sqls);
		if(type.equals("FullSA")){
			String sql = "UPDATE WF_CCList SET Sta=-1, RecEmpNo='" + getWebUser().getNo() + "',RecEmpName='" + getWebUser().getName() +"',NodeName='" + nodeMainName + "',FlowName='" + flowName + "' WHERE WorkID=" + this.WorkID + " AND NodeIDWork=" + nodeIDMain;
			DBAccess.RunSQL(sql);
		}
		if(type.equals("WorkNode")){
			String sql = "UPDATE WF_CCList SET Sta=0, RecEmpNo='" + getWebUser().getNo() + "',RecEmpName='" + getWebUser().getName() +"',NodeName='" + nodeMainName + "',FlowName='" + flowName + "' WHERE WorkID=" + this.WorkID + " AND NodeIDWork=" + nodeIDMain;
			DBAccess.RunSQL(sql);
		}
		//CCList en = new CCList();
		//en.CheckPhysicsTable();

		// 更新其他字段 DeptName, 等字段》
		String updateSQL = "";
		switch (SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
			case HGDB:
				updateSQL = " UPDATE WF_CCList cc SET cc.CCToName=emp.Name,cc.DeptNo = emp.FK_Dept,cc.DeptName =dept.Name  FROM Port_Emp emp,Port_Dept dept ";
				updateSQL += " WHERE emp.No = cc.CCTo AND dept.No = emp.FK_Dept AND cc.WorkID = " + workID;
				break;
			case PostgreSQL:
				updateSQL = " UPDATE WF_CCList SET CCToName=emp.Name,DeptNo = emp.FK_Dept,DeptName =dept.Name  FROM Port_Emp emp,Port_Dept dept ";
				updateSQL += " WHERE emp.No = CCTo AND dept.No = emp.FK_Dept AND wf_cclist.WorkID = " + workID;
				break;
			case Oracle:
			case DM:
			case GBASE8CByOracle:
				updateSQL = "UPDATE WF_CCList E SET (CCToName,DeptNo,DeptName)=(SELECT U.Name,U.FK_Dept,D.Name FROM Port_Emp U,Port_Dept D WHERE E.CCTo=U.No AND U.FK_Dept=D.No) WHERE E.WorkID = " + workID;
				break;
			default:
				updateSQL = "UPDATE WF_CCList cc LEFT JOIN Port_Emp emp ON cc.CCTo = emp.No LEFT JOIN Port_Dept dept ON emp.FK_Dept = dept.No SET cc.CCToName = emp.Name, cc.DeptNo = emp.FK_Dept, cc.DeptName = dept.Name WHERE  WorkID =" + workID;
				break;
		}
		DBAccess.RunSQL(updateSQL);
		if(type.equals("WorkNode") ){
			this.currWN.addMsg("CC", bp.wf.Glo.multilingual("@自动抄送给:{0}.", "WorkNode", "cc", empNames));
		}

	}
	/**
	 获得人员

	 @param rol
	 @param rpt
	 @param workid
	 @return
	 @exception Exception
	*/
	public final DataTable GenerCCers(CCRole rol, Entity rpt, long workid) throws Exception {
		DataTable dt = new DataTable();
		dt.Columns.Add(new DataColumn("No", String.class));
		dt.Columns.Add(new DataColumn("Name", String.class));

		DataTable mydt = new DataTable();

		/**按照接受人规则计算.
		 */
		if (rol.getCCRoleExcType() == CCRoleExcType.ByDeliveryWay) {
			Node toNode = new Node(rol.getNodeID());
			WorkNode toWn = new WorkNode(this.currWN.getHisWork(), toNode);

			FindWorker fw = new FindWorker();
			try{
				DataTable mydt1 = fw.DoIt(this.currWN.getHisFlow(), this.currWN, toWn);
				return mydt1;
			}catch(Exception e){
				return mydt;
			}
		}

		String sql = "";
		if (rol.getCCRoleExcType() == CCRoleExcType.ByDepts) {
			/*如果抄送到部门. */
			//  sql = "SELECT A." + BP.Sys.Base.Glo.UserNo + ", A.Name FROM Port_Emp A, WF_CCDept B  WHERE  B.FK_Dept=A.FK_Dept AND B.FK_Node=" + this.getNodeID();
			sql = "SELECT A." + bp.sys.base.Glo.getUserNo() + ", A.Name FROM Port_Emp A  WHERE  A.FK_Dept IN (" + rol.getEnIDs() + ") ";

			mydt = DBAccess.RunSQLReturnTable(sql);
			for (DataRow mydr : mydt.Rows) {
				DataRow dr = dt.NewRow();
				dr.setValue("No", mydr.getValue("No"));
				dr.setValue("Name", mydr.getValue("Name"));
				dt.Rows.add(dr);
			}
			return dt;
		}

		if (rol.getCCRoleExcType() == CCRoleExcType.ByEmps) {
			/*如果抄送到人员. */
			//  sql = "SELECT A." + BP.Sys.Base.Glo.UserNo + ", A.Name FROM Port_Emp A, WF_CCEmp B WHERE A." + BP.Sys.Base.Glo.UserNoWhitOutAS + "=B.FK_Emp AND B.FK_Node=" + rol.NodeID;
			sql = "SELECT A." + bp.sys.base.Glo.getUserNo() + ", A.Name FROM Port_Emp A  WHERE  " + bp.sys.base.Glo.getUserNoWhitOutAS() + " IN (" + rol.getEnIDs() + ") ";

			mydt = DBAccess.RunSQLReturnTable(sql);
			for (DataRow mydr : mydt.Rows) {
				DataRow dr = dt.NewRow();
				dr.setValue("No", mydr.getValue("No"));
				dr.setValue("Name", mydr.getValue("Name"));
				dt.Rows.add(dr);
			}
			return dt;
		}

		if (rol.getCCRoleExcType() == CCRoleExcType.ByStations) {
			if (rol.getCCStaWay() == bp.wf.CCStaWay.StationOnly) {
				sql = "SELECT " + bp.sys.base.Glo.getUserNo() + ",Name FROM Port_Emp A, Port_DeptEmpStation B  WHERE A.No= B.FK_Emp AND B.FK_Station IN (" + rol.getEnIDs() + ")";
				mydt = DBAccess.RunSQLReturnTable(sql);
				for (DataRow mydr : mydt.Rows) {
					DataRow dr = dt.NewRow();
					dr.setValue("No", mydr.getValue("No"));
					dr.setValue("Name", mydr.getValue("Name"));
					dt.Rows.add(dr);
				}
				return dt;
			}

			if (rol.getCCStaWay() == bp.wf.CCStaWay.StationSmartCurrNodeWorker || rol.getCCStaWay() == bp.wf.CCStaWay.StationSmartNextNodeWorker) {
				/*按角色智能计算*/
				String deptNo = "";
				if (rol.getCCStaWay() == bp.wf.CCStaWay.StationSmartCurrNodeWorker) {
					deptNo = bp.web.WebUser.getDeptNo();
				} else {
					deptNo = DBAccess.RunSQLReturnStringIsNull("SELECT FK_Dept FROM WF_GenerWorkerlist WHERE WorkID=" + workid + " AND IsEnable=1 AND IsPass=0", bp.web.WebUser.getDeptNo());
				}

				sql = "SELECT " + bp.sys.base.Glo.getUserNo() + ",Name FROM Port_Emp A, Port_DeptEmpStation B WHERE A." + bp.sys.base.Glo.getUserNoWhitOutAS() + "=B.FK_Emp AND B.FK_Station IN (" + rol.getEnIDs() + ") AND B.FK_Dept='" + deptNo + "'";
				mydt = DBAccess.RunSQLReturnTable(sql);
				for (DataRow mydr : mydt.Rows) {
					DataRow dr = dt.NewRow();
					dr.setValue("No", mydr.getValue("No"));
					dr.setValue("Name", mydr.getValue("Name"));
					dt.Rows.add(dr);
				}
				return dt;
			}

			if (rol.getCCStaWay() == bp.wf.CCStaWay.StationAndDept) {
				if(DataType.IsNullOrEmpty(rol.getEnIDs()))
					throw new RuntimeException("err@自动抄送，按照角色与部门的交集计算，没有配置角色信息");
				if(DataType.IsNullOrEmpty(rol.getEnDeptIDs()))
					throw new RuntimeException("err@自动抄送，按照角色与部门的交集计算，没有配置部门信息");
				sql = "SELECT " + bp.sys.base.Glo.getUserNo() + ",Name FROM Port_Emp A, Port_DeptEmpStation B  WHERE A.No= B.FK_Emp AND B.FK_Station IN (" + rol.getEnIDs() + ") AND B.FK_Dept IN(" + rol.getEnDeptIDs() + ")";
				mydt = DBAccess.RunSQLReturnTable(sql);
				for (DataRow mydr : mydt.Rows) {
					DataRow dr = dt.NewRow();
					dr.setValue("No", mydr.getValue("No"));
					dr.setValue("Name", mydr.getValue("Name"));
					dt.Rows.add(dr);
				}
				return dt;
			}

			if (rol.getCCStaWay() == CCStaWay.StationDeptUpLevelCurrNodeWorker || rol.getCCStaWay() == CCStaWay.StationDeptUpLevelNextNodeWorker) {
				// 求当事人的部门编号.
				String deptNo = "";

				if (rol.getCCStaWay() == CCStaWay.StationDeptUpLevelCurrNodeWorker) {
					deptNo = bp.web.WebUser.getDeptNo();
				}

				if (rol.getCCStaWay() == CCStaWay.StationDeptUpLevelNextNodeWorker) {
					deptNo = DBAccess.RunSQLReturnStringIsNull("SELECT FK_Dept FROM WF_GenerWorkerlist WHERE WorkID=" + workid + " AND IsEnable=1 AND IsPass=0", getWebUser().DeptNo);
				}

				while (true) {
					Dept dept = new Dept(deptNo);

					//sql = "SELECT " + BP.Sys.Base.Glo.UserNo + ",Name FROM Port_Emp A, Port_DeptEmpStation B, WF_CCStation C WHERE A." + BP.Sys.Base.Glo.UserNoWhitOutAS + "=B.FK_Emp AND B.FK_Station=C.FK_Station  AND C.FK_Node=" + this.getNodeID() + " AND B.FK_Dept='" + deptNo + "'";

					sql = "SELECT " + bp.sys.base.Glo.getUserNo() + ",Name FROM Port_Emp A, Port_DeptEmpStation B WHERE A.No=B.FK_Emp AND B.FK_Station IN (" + rol.getEnIDs() + ") AND B.FK_Dept='" + deptNo + "'";

					mydt = DBAccess.RunSQLReturnTable(sql);
					for (DataRow mydr : mydt.Rows) {
						DataRow dr = dt.NewRow();
						dr.setValue("No", mydr.getValue("No"));
						dr.setValue("Name", mydr.getValue("Name"));
						dt.Rows.add(dr);
					}

					if (Objects.equals(dept.getParentNo(), "0")) {
						break;
					}

					deptNo = dept.getParentNo();
				}
				return dt;
			}
		}

		if (rol.getCCRoleExcType() == CCRoleExcType.BySQLs) {
			Object tempVar = rol.getEnIDs().replace("'", "");
			sql = tempVar instanceof String ? (String) tempVar : null;
			if (sql.contains("@") == true) {
				sql = bp.wf.Glo.DealExp(sql, rpt, null);
			}

			/*按照SQL抄送. */
			mydt = DBAccess.RunSQLReturnTable(sql);
			for (DataRow mydr : mydt.Rows) {
				DataRow dr = dt.NewRow();
				dr.setValue("No", mydr.getValue("No"));
				dr.setValue("Name", mydr.getValue("Name"));
				dt.Rows.add(dr);
			}
			return dt;
		}
		/**按照表单字段抄送*/
		if (rol.getCCRoleExcType() == CCRoleExcType.ByFrmField) {
			Node toNode = new Node(rol.getNodeID());
			WorkNode toWn = new WorkNode(this.currWN.getHisWork(), toNode);

			if (DataType.IsNullOrEmpty(rol.getEnIDs()) == true) {
				throw new RuntimeException("抄送规则自动抄送选择按照表单字段抄送没有设置抄送人员字段");
			}

			String[] attrs = rol.getEnIDs().replace("'", "").split("[,]", -1);
			String ccemps = ",";
			for (String attr : attrs) {
				String emps1 = rpt.GetValStrByKey(attr);
				if(DataType.IsNullOrEmpty(emps1)==false){
					sql = "SELECT No,Name FROM Port_Emp WHERE No IN (" + bp.da.DataType.DealFromatSQLWhereIn(emps1) + ")";
					DataTable dtVals = DBAccess.RunSQLReturnTable(sql);
					if (dtVals.Rows.size() == 0)
						dtVals = CCFormAPI.GenerPopData2022(String.valueOf(this.WorkID), emps1);
					String empNo = "";
					for(DataRow drr : dtVals.Rows){
						empNo = drr.getValue(0).toString();
						if (ccemps.contains("," + empNo + ","))
							continue;
						ccemps += empNo + ",";
						DataRow dr = dt.NewRow();
						dr.setValue("No",empNo);
						dr.setValue("Name",drr.getValue(1).toString());
						dt.Rows.add(dr);
					}
				}

			}
			return dt;
		}
		throw new RuntimeException("err@没有解析的设置." + rol.getCCRoleExcType());
	}
}
