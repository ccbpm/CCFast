package bp.wf.template;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.sys.*;
import bp.en.*; import bp.en.Map;
import bp.port.*;
import bp.web.*;
import bp.wf.*;
import java.util.*;

/**
 选择器
*/
public class Selector extends Entity
{

		///#region 基本属性
	@Override
	public String getPK()
	{
		return "NodeID";
	}
	/**
	 选择模式
	*/
	public final SelectorModel getSelectorModel() {
		return SelectorModel.forValue(this.GetValIntByKey(SelectorAttr.SelectorModel));
	}
	public final void setSelectorModel(SelectorModel value){
		this.SetValByKey(SelectorAttr.SelectorModel, value.getValue());
	}
	/**
	 分组数据源
	*/
	public final String getSelectorP1() throws Exception {
		String s = this.GetValStringByKey(SelectorAttr.SelectorP1);
		s = s.replace("~", "'");
		return s;
	}
	public final void setSelectorP1(String value){
		this.SetValByKey(SelectorAttr.SelectorP1, value);
	}
	/**
	 实体数据源
	*/
	public final String getSelectorP2() throws Exception {
		String s = this.GetValStringByKey(SelectorAttr.SelectorP2);
		s = s.replace("~", "'");
		return s;
	}
	public final void setSelectorP2(String value){
		this.SetValByKey(SelectorAttr.SelectorP2, value);
	}
	/**
	 默认选择数据源
	*/
	public final String getSelectorP3() throws Exception {
		String s = this.GetValStringByKey(SelectorAttr.SelectorP3);
		s = s.replace("~", "'");
		return s;
	}
	public final void setSelectorP3(String value){
		this.SetValByKey(SelectorAttr.SelectorP3, value);
	}
	/**
	 强制选择数据源
	*/
	public final String getSelectorP4() throws Exception {
		String s = this.GetValStringByKey(SelectorAttr.SelectorP4);
		s = s.replace("~", "'");
		return s;
	}
	public final void setSelectorP4(String value){
		this.SetValByKey(SelectorAttr.SelectorP3, value);
	}
	/**
	 是否自动装载上一笔加载的数据
	*/
	public final boolean getItIsAutoLoadEmps()  {
		return this.GetValBooleanByKey(SelectorAttr.IsAutoLoadEmps);
	}
	public final void setItIsAutoLoadEmps(boolean value){
		this.SetValByKey(SelectorAttr.IsAutoLoadEmps, value);
	}
	/**
	 是否单选？
	*/
	public final boolean getItIsSimpleSelector()  {
		return this.GetValBooleanByKey(SelectorAttr.IsSimpleSelector);
	}
	public final void setItIsSimpleSelector(boolean value){
		this.SetValByKey(SelectorAttr.IsSimpleSelector, value);
	}
	/**
	 是否启用部门搜索范围限定
	*/
	public final boolean getItIsEnableDeptRange()  {
		return this.GetValBooleanByKey(SelectorAttr.IsEnableDeptRange);
	}
	public final void setItIsEnableDeptRange(boolean value){
		this.SetValByKey(SelectorAttr.IsEnableDeptRange, value);
	}
	/**
	 是否启用角色搜索范围限定
	*/
	public final boolean getItIsEnableStaRange()  {
		return this.GetValBooleanByKey(SelectorAttr.IsEnableStaRange);
	}
	public final void setItIsEnableStaRange(boolean value){
		this.SetValByKey(SelectorAttr.IsEnableStaRange, value);
	}
	/**
	 节点ID
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(SelectorAttr.NodeID);
	}
	public final void setNodeID(int value){
		this.SetValByKey(SelectorAttr.NodeID, value);
	}
	/**
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsDelete = false;
		uac.IsInsert = false;
		if (WebUser.getNo().equals("admin") == true)
		{
			uac.IsUpdate = true;
			uac.IsView = true;
		}

		uac.IsUpdate = true;

		return uac;
	}

		///#endregion


		///#region 构造方法
	/**
	 接受人选择器
	*/
	public Selector()
	{
	}
	/**
	 接受人选择器

	 @param nodeid
	*/
	public Selector(int nodeid) throws Exception {
		this.setNodeID(nodeid);
		this.Retrieve();
	}
	/**
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}


			///#region 字段.
		Map map = new Map("WF_Node", "选择器");

		map.setDepositaryOfEntity(Depositary.Application);

		map.AddTBIntPK(SelectorAttr.NodeID, 0, "NodeID", true, true);
		map.AddTBString(SelectorAttr.Name, null, "节点名称", true, true, 0, 100, 100);

		map.AddTBInt(SelectorAttr.SelectorModel, 0, "显示方式", true, true);
		//map.AddDDLSysEnum(SelectorAttr.SelectorModel, 5, "显示方式", true, true, SelectorAttr.SelectorModel,
		//    "@0=按角色@1=按部门@2=按人员@3=按SQL@4=按SQL模版计算@5=使用通用人员选择器@6=部门与角色的交集@7=自定义Url@8=使用通用部门角色人员选择器@9=按角色智能计算(操作员所在部门)");

		map.AddDDLSQL(SelectorAttr.FK_SQLTemplate, null, "SQL模版", "SELECT No,Name FROM WF_SQLTemplate WHERE SQLType=5", true);

		map.AddBoolean(SelectorAttr.IsAutoLoadEmps, true, "是否自动加载上一次选择的人员？", true, true);
		map.AddBoolean(SelectorAttr.IsSimpleSelector, false, "是否单项选择(只能选择一个人)？", true, true);
		map.AddBoolean(SelectorAttr.IsEnableDeptRange, false, "是否启用部门搜索范围限定(对使用通用人员选择器有效)？", true, true, true);
		map.AddBoolean(SelectorAttr.IsEnableStaRange, false, "是否启用角色搜索范围限定(对使用通用人员选择器有效)？", true, true, true);


		// map.AddDDLSysEnum(SelectorAttr.IsMinuesAutoLoadEmps, 5, "接收人选择方式", true, true, SelectorAttr.SelectorModel,
		// "@0=按角色@1=按部门@2=按人员@3=按SQL@4=按SQL模版计算@5=使用通用人员选择器@6=部门与角色的交集@7=自定义Url");

		map.AddTBStringDoc(SelectorAttr.SelectorP1, null, "分组参数:可以为空,比如:SELECT No,Name,ParentNo FROM  Port_Dept", true, false, 0, 300, 3);
		map.AddTBStringDoc(SelectorAttr.SelectorP2, null, "操作员数据源:比如:SELECT No,Name,FK_Dept FROM  Port_Emp", true, false, 0, 300, 3);

		map.AddTBStringDoc(SelectorAttr.SelectorP3, null, "默认选择的数据源:比如:SELECT FK_Emp FROM  WF_GenerWorkerList WHERE FK_Node=102 AND WorkID=@WorkID", true, false, 0, 300, 3);
		map.AddTBStringDoc(SelectorAttr.SelectorP4, null, "强制选择的数据源:比如:SELECT FK_Emp FROM  WF_GenerWorkerList WHERE FK_Node=102 AND WorkID=@WorkID", true, false, 0, 300, 3);
		map.AddTBString(NodeAttr.DeliveryParas, null, "访问规则设置", true, false, 0, 300, 10);

			///#endregion


			///#region 对应关系
		//平铺模式.
		map.getAttrsOfOneVSM().AddGroupPanelModel(new bp.wf.template.NodeStations(), new Stations(), bp.wf.template.NodeStationAttr.FK_Node, bp.wf.template.NodeStationAttr.FK_Station, "绑定角色(平铺)", StationAttr.FK_StationType, "Name", "No");

		map.getAttrsOfOneVSM().AddGroupListModel(new bp.wf.template.NodeStations(), new Stations(), bp.wf.template.NodeStationAttr.FK_Node, bp.wf.template.NodeStationAttr.FK_Station, "绑定角色(树)", StationAttr.FK_StationType, "Name", "No");

		//节点绑定部门. 节点绑定部门.
		//map.getAttrsOfOneVSM().AddBranches(new bp.wf.template.NodeDepts(), new Depts(), bp.wf.template.NodeDeptAttr.FK_Node, bp.wf.template.NodeDeptAttr.FK_Dept, "绑定部门", EmpAttr.Name, EmpAttr.No, "@WebUser.FK_Dept", null);
		map.getAttrsOfOneVSM().AddBranches(new bp.wf.template.NodeDepts(), new Depts(), bp.wf.template.NodeDeptAttr.FK_Node, bp.wf.template.NodeDeptAttr.FK_Dept, "绑定部门", EmpAttr.Name, EmpAttr.No, "@WebUser.DeptNo");

		//节点绑定人员. 使用树杆与叶子的模式绑定.
		map.getAttrsOfOneVSM().AddBranchesAndLeaf(new bp.wf.template.NodeEmps(), new Emps(), bp.wf.template.NodeEmpAttr.FK_Node, bp.wf.template.NodeEmpAttr.FK_Emp, "绑定接受人", EmpAttr.FK_Dept, EmpAttr.Name, EmpAttr.No, "@WebUser.FK_Dept", null);

			///#endregion


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	/**
	 产生数据.

	 @return
	*/
	public final DataSet GenerDataSet(int nodeid, Entity en) throws Exception {
		DataSet ds = null;
		switch (this.getSelectorModel())
		{
			case Dept:
				ds = ByDept(nodeid, en);
				break;
			case TeamOrgOnly:
				ds = ByTeam(nodeid, en, this.getSelectorModel());
				break;
			case TeamOnly:
				ds = ByTeam(nodeid, en, this.getSelectorModel());
				break;
			case TeamDeptOnly:
				ds = ByTeam(nodeid, en, this.getSelectorModel());
				break;
			case Emp:
				ds = ByEmp(nodeid);
				break;
			case Station:
				ds = ByStation(nodeid, en);
				break;
			case ByStationAI:
				ds = ByStationAI(nodeid, en);

				break;
			case DeptAndStation:
				ds = DeptAndStation(nodeid);
				break;
			case SQL:
				ds = BySQL(nodeid, en);
				break;
			case SQLTemplate:
				ds = SQLTemplate(nodeid, en);
				break;
			case GenerUserSelecter:
				ds = ByGenerUserSelecter();
				break;
			case AccepterOfDeptStationOfCurrentOper: //按角色智能计算.
				ds = AccepterOfDeptStationOfCurrentOper(nodeid, en);
				break;
			case ByWebAPI:
				ds = ByWebAPI(en);
				break;
			case ByMyDeptEmps:
				ds = ByMyDeptEmps();
				break;
			default:
				throw new RuntimeException("@错误:没有判断的选择类型:" + this.getSelectorModel());
				//break;
		}

		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			for (DataTable dt : ds.Tables)
			{
				for (int i = 0; i < dt.Columns.size(); i++)
				{
					if (Objects.equals(dt.Columns.get(i).ColumnName.toUpperCase(), "NO"))
					{
						dt.Columns.get(i).ColumnName = "No";
					}

					if (Objects.equals(dt.Columns.get(i).ColumnName.toUpperCase(), "NAME"))
					{
						dt.Columns.get(i).ColumnName = "Name";
					}

					if (Objects.equals(dt.Columns.get(i).ColumnName.toUpperCase(), "PARENTNO"))
					{
						dt.Columns.get(i).ColumnName = "ParentNo";
					}

					if (Objects.equals(dt.Columns.get(i).ColumnName.toUpperCase(), "FK_DEPT"))
					{
						dt.Columns.get(i).ColumnName = "FK_Dept";
					}
				}
			}
		}

		ds.Tables.add(this.ToDataTableField("Selector"));

		return ds;
	}
	/**
	 通用

	 @return
	*/
	private DataSet ByGenerUserSelecter()
	{
		DataSet ds = new DataSet();

		////排序.
		//String orderByDept = "";
		//if (DBAccess.IsExitsTableCol("Port_Dept", "Idx"))
		//    orderByDept = " ORDER BY Port_Dept.Idx";

		//String orderByEmp = "";
		//if (DBAccess.IsExitsTableCol("Port_Emp", "Idx"))
		//    orderByDept = " ORDER BY Port_Emp.Dept, Port_Emp.Idx";

		//部门
		String sql = "SELECT distinct No,Name, ParentNo FROM Port_Dept  ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.setTableName("Depts");
		ds.Tables.add(dt);

		//人员.
		sql = "SELECT  No, Name, FK_Dept FROM Port_Emp ";
		DataTable dtEmp = DBAccess.RunSQLReturnTable(sql);
		dtEmp.setTableName("Emps");
		ds.Tables.add(dtEmp);

		return ds;
	}
	/**
	 按照模版

	 @param nodeID 节点ID
	 @param en
	 @return
	*/
	private DataSet SQLTemplate(int nodeID, Entity en) throws Exception {
		//设置他的模版.
		//Node nd = new Node(nodeID);

		SQLTemplate sql = new SQLTemplate(this.getSelectorP1());
		this.setSelectorP2(sql.getDocs());

		return BySQL(nodeID, en);
	}

	/**
	 按照SQL计算.

	 @param nodeID 节点ID
	 @return 返回值
	*/
	private DataSet BySQL(int nodeID, Entity en) throws Exception {
		// 定义数据容器.
		DataSet ds = new DataSet();


		//求部门.
		String sqlGroup = this.getSelectorP1(); // @sly
		String sqlDB = this.getSelectorP2(); //人员

		if(DataType.IsNullOrEmpty(sqlGroup) && DataType.IsNullOrEmpty(sqlDB)){
			String sql = this.GetValStringByKey(NodeAttr.DeliveryParas);
			if(DataType.IsNullOrEmpty(sql))
				throw new RuntimeException("接收人配置的固定范围选择按SQL计算时为空");
			//设置空的部门
			DataTable dtDept = new DataTable();
			dtDept.Columns.Add("No");
			dtDept.Columns.Add("Name");
			dtDept.setTableName("Depts");
			ds.Tables.add(dtDept);
			sql = bp.wf.Glo.DealSQLExp(sql,en,null);
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			dt.setTableName("Emps");
			ds.Tables.add(dt);
			//转换大小写
			for (DataColumn col : dt.Columns)
			{
				String colName = col.ColumnName;
				switch (colName)
				{
					case "no":
					case "NO":
						col.ColumnName = "No";
						break;
					case "name":
					case "NAME":
						col.ColumnName = "Name";
						break;
					case "fk_dept":
					case "FK_DEPT":
						col.ColumnName = "FK_Dept";
						break;
					default:
						break;
				}
			}
			return ds;
		}
		if (DataType.IsNullOrEmpty(sqlGroup) == false && sqlGroup.length() > 6)
		{
			sqlGroup = bp.wf.Glo.DealExp(sqlGroup, en, null); //@祝梦娟
			DataTable dt = DBAccess.RunSQLReturnTable(sqlGroup);
			dt.setTableName("Depts");
			//转换大小写
			for (DataColumn col : dt.Columns)
			{
				String colName = col.ColumnName.toLowerCase();
				switch (colName)
				{
					case "no":
						col.ColumnName = "No";
						break;
					case "name":
						col.ColumnName = "Name";
						break;
					default:
						break;
				}
			}
			ds.Tables.add(dt);
		}

		//求人员范围.

		sqlDB = bp.wf.Glo.DealExp(sqlDB, en, null); //@祝梦娟

		DataTable dtEmp = DBAccess.RunSQLReturnTable(sqlDB);
		dtEmp.setTableName("Emps");
		//转换大小写
		for (DataColumn col : dtEmp.Columns)
		{
			String colName = col.ColumnName.toLowerCase();
			switch (colName)
			{
				case "no":
					col.ColumnName = "No";
					break;
				case "name":
					col.ColumnName = "Name";
					break;
				case "fk_dept":
					col.ColumnName = "FK_Dept";
					break;
				default:
					break;
			}
		}
		ds.Tables.add(dtEmp);

		//求默认选择的数据.
		if (!Objects.equals(this.getSelectorP3(), ""))
		{
			sqlDB = this.getSelectorP3();
			sqlDB = bp.wf.Glo.DealExp(sqlDB, en, null); //@祝梦娟

			DataTable dtDef = DBAccess.RunSQLReturnTable(sqlDB);
			dtDef.setTableName("DefaultSelected");
			for (DataColumn col : dtDef.Columns)
			{
				String colName = col.ColumnName.toLowerCase();
				switch (colName)
				{
					case "no":
						col.ColumnName = "No";
						break;
					case "name":
						col.ColumnName = "Name";
						break;
					default:
						break;
				}
			}

			ds.Tables.add(dtDef);
		}


		//求强制选择的数据源.
		if (!Objects.equals(this.getSelectorP4(), ""))
		{
			sqlDB = this.getSelectorP4();

			sqlDB = sqlDB.replace("@WebUser.No", WebUser.getNo());
			sqlDB = sqlDB.replace("@WebUser.Name", WebUser.getName());
			sqlDB = sqlDB.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
			sqlDB = sqlDB.replace("@WebUser.DeptNo", WebUser.getDeptNo());
			sqlDB = sqlDB.replace("@WorkID", en.GetValStringByKey("OID"));
			sqlDB = sqlDB.replace("@OID", en.GetValStringByKey("OID"));

			if (sqlDB.contains("@"))
			{
				sqlDB = bp.wf.Glo.DealExp(sqlDB, en, null);
			}

			DataTable dtForce = DBAccess.RunSQLReturnTable(sqlDB);
			for (DataColumn col : dtForce.Columns)
			{
				String colName = col.ColumnName.toLowerCase();
				switch (colName)
				{
					case "no":
						col.ColumnName = "No";
						break;
					case "name":
						col.ColumnName = "Name";
						break;
					default:
						break;
				}
			}
			dtForce.setTableName("ForceSelected");
			ds.Tables.add(dtForce);
		}

		return ds;
	}

	/**
	 按照部门获取部门人员树.

	 @param nodeID 节点ID
	 @return 返回数据源dataset
	*/
	private DataSet ByDept(int nodeID, Entity en) throws Exception {
		// 定义数据容器.
		DataSet ds = new DataSet();
		String sql = null;
		DataTable dt = null;
		DataTable dtEmp = null;

		Node nd = new Node(nodeID);
		if (nd.getHisDeliveryWay() == DeliveryWay.BySelectedForPrj)
		{
			//部门.
			sql = "SELECT distinct a.No,a.Name, a.ParentNo FROM Port_Dept a,  WF_NodeDept b, WF_PrjEmp C,Port_DeptEmp D WHERE A.No=B.FK_Dept AND B.FK_Node=" + nodeID + " AND C.FK_Prj='" + en.GetValStrByKey("PrjNo") + "' ORDER BY a.Idx ";
			sql += "  AND C.FK_Emp=D.FK_Emp ";


			dt = DBAccess.RunSQLReturnTable(sql);
			dt.setTableName("Depts");
			ds.Tables.add(dt);

			//人员.
			sql = "SELECT distinct a." + bp.sys.base.Glo.getUserNo() + ", a.Name, a.FK_Dept FROM Port_Emp a, WF_NodeDept b, WF_PrjEmp C WHERE a.FK_Dept=b.FK_Dept  AND A.No=C.FK_Emp  AND B.FK_Node=" + nodeID + " AND C.FK_Prj='" + en.GetValStrByKey("PrjNo") + "'  ORDER BY a.Idx ";
			dtEmp = DBAccess.RunSQLReturnTable(sql);
			ds.Tables.add(dtEmp);
			dtEmp.setTableName("Emps");
			return ds;
		}


		//部门.
		sql = "SELECT distinct a.No,a.Name, a.ParentNo,a.Idx FROM Port_Dept a,WF_NodeDept b WHERE a.No=b.FK_Dept AND B.FK_Node=" + nodeID + "   ORDER BY a.Idx ";
		dt = DBAccess.RunSQLReturnTable(sql);
		dt.setTableName("Depts");
		ds.Tables.add(dt);

		//人员.
		sql = "SELECT distinct a." + bp.sys.base.Glo.getUserNo() + ", a.Name, d.FK_Dept ,a.Idx FROM Port_Emp a, WF_NodeDept b,Port_DeptEmp d WHERE d.FK_Dept=b.FK_Dept AND a.No=d.FK_Emp AND B.FK_Node=" + nodeID + "  ORDER BY a.Idx";
		dtEmp = DBAccess.RunSQLReturnTable(sql);
		ds.Tables.add(dtEmp);
		dtEmp.setTableName("Emps");
		return ds;
	}
	/**
	 按照Emp获取部门人员树.

	 @param nodeID 节点ID
	 @return 返回数据源dataset
	*/
	private DataSet ByEmp(int nodeID)
	{
		// 定义数据容器.
		DataSet ds = new DataSet();


		//部门.
		String sql = "SELECT distinct a.No,a.Name, a.ParentNo FROM Port_Dept a, WF_NodeEmp b, Port_Emp c WHERE b.FK_Emp=c." + bp.sys.base.Glo.getUserNoWhitOutAS() + " AND a.No=c.FK_Dept AND B.FK_Node=" + nodeID + " ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.setTableName("Depts");
		ds.Tables.add(dt);

		//人员.
		sql = "SELECT distinct a." + bp.sys.base.Glo.getUserNo() + ",a.Name, a.FK_Dept FROM Port_Emp a, WF_NodeEmp b WHERE a." + bp.sys.base.Glo.getUserNoWhitOutAS() + "=b.FK_Emp AND b.FK_Node=" + nodeID;

		DataTable dtEmp = DBAccess.RunSQLReturnTable(sql);
		dtEmp.setTableName("Emps");
		ds.Tables.add(dtEmp);
		return ds;
	}
	/**
	 按照Emp获取部门人员树.
	 @return 返回数据源dataset
	*/
	private DataSet ByMyDeptEmps()
	{
		// 定义数据容器.
		DataSet ds = new DataSet();


		//部门.
		String sql = "SELECT No,Name FROM Port_Dept  WHERE No='" + WebUser.getDeptNo() + "' ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.setTableName("Depts");
		ds.Tables.add(dt);

		//人员.
		sql = " SELECT No,Name,FK_Dept FROM Port_Emp  WHERE FK_Dept='" + WebUser.getDeptNo() + "'";

		DataTable dtEmp = DBAccess.RunSQLReturnTable(sql);
		dtEmp.setTableName("Emps");
		ds.Tables.add(dtEmp);
		return ds;
	}
	/**


	 @param nodeID
	 @param en
	 @return
	*/
	private DataSet AccepterOfDeptStationOfCurrentOper(int nodeID, Entity en) throws Exception {
		// 定义数据容器.
		DataSet ds = new DataSet();

		//部门.
		String sql = "";
		sql = "SELECT d.No,d.Name,d.ParentNo  FROM  Port_DeptEmp  de, Port_Dept as d WHERE de.FK_Dept = d.No and de.setEmpNo('" + WebUser.getNo() + "'";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		//人员.
		if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.DM || bp.difference.SystemConfig.getAppCenterDBType() == DBType.Oracle || bp.difference.SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.KingBaseR6  || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			sql = "SELECT * FROM (SELECT distinct a.No,a.Name, a.FK_Dept FROM Port_Emp a,  WF_NodeStation b, Port_DeptEmpStation c WHERE a.No=c.FK_Emp AND B.FK_Station=C.FK_Station AND C.FK_Dept='" + WebUser.getDeptNo() + "' AND b.FK_Node=" + nodeID + ")  ORDER BY A.Idx ";
		}
		else
		{
			sql = "SELECT distinct a." + bp.sys.base.Glo.getUserNo() + ",a.Name, a.FK_Dept FROM Port_Emp a,  WF_NodeStation b, Port_DeptEmpStation c WHERE a.No=c.FK_Emp AND C.FK_Dept='" + WebUser.getDeptNo() + "' AND B.FK_Station=C.FK_Station AND b.FK_Node=" + nodeID + "  ORDER BY A.Idx";
		}

		DataTable dtEmp = DBAccess.RunSQLReturnTable(sql);
		if (dtEmp.Rows.size() > 0)
		{
			dt.setTableName("Depts");
			ds.Tables.add(dt);

			dtEmp.setTableName("Emps");
			ds.Tables.add(dtEmp);
		}
		else //如果没人，就查询父级
		{
			//查询当前节点的workdID
			long workID = Long.parseLong(en.GetValStringByKey("OID"));
			WorkNode node = new WorkNode(workID, nodeID);

			sql = " SELECT No,Name, ParentNo FROM Port_Dept WHERE no  in (  SELECT  ParentNo FROM Port_Dept WHERE No  IN " + "( SELECT FK_Dept FROM WF_GenerWorkerlist WHERE WorkID ='" + workID + "' ))";
			dt = DBAccess.RunSQLReturnTable(sql);
			dt.setTableName("Depts");
			ds.Tables.add(dt);

			// 如果当前的节点不是开始节点， 从轨迹里面查询。
			sql = "SELECT DISTINCT b." + bp.sys.base.Glo.getUserNo() + ",b.Name,b.FK_Dept   FROM Port_DeptEmpStation a," +
					"Port_Emp b  WHERE FK_Station IN " + "( SELECT FK_Station FROM WF_NodeStation WHERE FK_Node=" + nodeID + ") " +
					"AND a.FK_Dept IN (SELECT ParentNo FROM Port_Dept WHERE No IN (SELECT FK_DEPT FROM WF_GenerWorkerlist WHERE WorkID=" +
					workID + "))" + " AND a.FK_Emp = b." + bp.sys.base.Glo.getUserNoWhitOutAS() + " ";
			sql += " ORDER BY b.No ";

			dtEmp = DBAccess.RunSQLReturnTable(sql);
			dtEmp.setTableName("Emps");
			ds.Tables.add(dtEmp);
		}
		return ds;
	}
	/**
	 部门于角色的交集 @zkr.

	 @param nodeID
	 @return
	*/
	private DataSet DeptAndStation(int nodeID)
	{
		// 定义数据容器.
		DataSet ds = new DataSet();

		//部门.
		String sql = "";

		sql = "SELECT B.No,B.Name,B.ParentNo FROM WF_NodeDept A, Port_Dept B WHERE A.FK_Dept=B.No AND FK_Node=" + nodeID;
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.setTableName("Depts");
		ds.Tables.add(dt);

		//@zkr.
		sql = "SELECT distinct a." + bp.sys.base.Glo.getUserNo() + ",a.Name, a.FK_Dept,a.Idx FROM Port_Emp A,  WF_NodeStation b, Port_DeptEmpStation c,WF_NodeDept D WHERE a." + bp.sys.base.Glo.getUserNoWhitOutAS() + "=c.FK_Emp AND B.FK_Station=C.FK_Station AND b.FK_Node=" + nodeID + " AND D.FK_Dept=A.FK_Dept AND D.FK_Node=" + nodeID + "  ORDER BY A.Idx ";
		DataTable dtEmp = DBAccess.RunSQLReturnTable(sql);
		dtEmp.setTableName("Emps");
		ds.Tables.add(dtEmp);
		return ds;
	}

	/**
	 按用户组计算

	 @param nodeID
	 @param en
	 @return
	*/
	private DataSet ByTeam(int nodeID, Entity en, SelectorModel sm) throws Exception {
		// 定义数据容器.
		DataSet ds = new DataSet();
		String sql = null;
		DataTable dt = null;
		DataTable dtEmp = null;
		Node nd = new Node(nodeID);
		if (sm == SelectorModel.TeamDeptOnly)
		{
			sql = "SELECT  No,Name FROM Port_Dept WHERE No='" + WebUser.getDeptNo() + "'";
		}
		if (sm == SelectorModel.TeamOnly)
		{
			sql = "SELECT DISTINCT a.No, a.Name, a.ParentNo,a.Idx FROM Port_Dept a, WF_NodeTeam b, Port_TeamEmp c, Port_Emp d WHERE a.No=d.FK_Dept AND b.FK_Team=c.FK_Team AND C.FK_Emp=D.No AND B.FK_Node=" + nodeID + "   ORDER BY A.No,A.Idx";
		}
		if (sm == SelectorModel.TeamOrgOnly)
		{
			sql = "SELECT DISTINCT a.No, a.Name, a.ParentNo,a.Idx FROM Port_Dept a, WF_NodeTeam b, Port_TeamEmp c, Port_Emp d WHERE a.No=d.FK_Dept AND b.FK_Team=c.FK_Team AND C.FK_Emp=D.No AND B.FK_Node=" + nodeID + " AND D.OrgNo='" + WebUser.getOrgNo() + "' ORDER BY A.No,A.Idx";
		}

		dt = DBAccess.RunSQLReturnTable(sql);
		dt.setTableName("Depts");
		ds.Tables.add(dt);

		//人员.
		if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.DM ||bp.difference.SystemConfig.getAppCenterDBType() == DBType.Oracle || bp.difference.SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || bp.difference.SystemConfig.getAppCenterDBType() == DBType.HGDB || DBAccess.getAppCenterDBType() == DBType.UX  || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			if (sm == SelectorModel.TeamDeptOnly)
			{
				sql = "SELECT * FROM (SELECT DISTINCT a.No,a.Name, a.FK_Dept,a.Idx FROM Port_Emp a,  WF_NodeTeam b, Port_TeamEmp c WHERE a.No=c.FK_Emp AND B.FK_Team=C.FK_Team AND B.FK_Node=" + nodeID + " AND A.FK_Dept='" + WebUser.getDeptNo() + "') ORDER BY FK_Dept,Idx,No";
			}
			if (sm == SelectorModel.TeamOrgOnly)
			{
				sql = "SELECT * FROM (SELECT DISTINCT a.No,a.Name, a.FK_Dept,a.Idx FROM Port_Emp a,  WF_NodeTeam b, Port_TeamEmp c WHERE a.No=c.FK_Emp AND B.FK_Team=C.FK_Team AND B.FK_Node=" + nodeID + " AND A.OrgNo='" + WebUser.getOrgNo() + "') ORDER BY FK_Dept,Idx,No";
			}
			if (sm == SelectorModel.TeamOnly)
			{
				sql = "SELECT * FROM (SELECT DISTINCT a.No,a.Name, a.FK_Dept,a.Idx FROM Port_Emp a,  WF_NodeTeam b, Port_TeamEmp c WHERE a.No=c.FK_Emp AND B.FK_Team=C.FK_Team AND B.FK_Node=" + nodeID + " ) ORDER BY FK_Dept,Idx,No";
			}
		}
		else
		{
			if (sm == SelectorModel.TeamDeptOnly)
			{
				sql = "SELECT DISTINCT a." + bp.sys.base.Glo.getUserNo() + ",a.Name, a.FK_Dept,a.Idx FROM Port_Emp A,  WF_NodeTeam B, Port_TeamEmp C WHERE a." + bp.sys.base.Glo.getUserNoWhitOutAS() + "=c.FK_Emp AND B.FK_Team=C.FK_Team AND B.FK_Node=" + nodeID + " AND A.FK_Dept='" + WebUser.getDeptNo() + "'  ORDER BY A.Idx";
			}
			if (sm == SelectorModel.TeamOrgOnly)
			{
				sql = "SELECT DISTINCT a." + bp.sys.base.Glo.getUserNo() + ",a.Name, a.FK_Dept,a.Idx FROM Port_Emp A,  WF_NodeTeam B, Port_TeamEmp C WHERE a." + bp.sys.base.Glo.getUserNoWhitOutAS() + "=c.FK_Emp AND B.FK_Team=C.FK_Team AND B.FK_Node=" + nodeID + " AND A.OrgNo='" + WebUser.getOrgNo() + "'  ORDER BY A.Idx";
			}
			if (sm == SelectorModel.TeamOnly)
			{
				sql = "SELECT DISTINCT a." + bp.sys.base.Glo.getUserNo() + ",a.Name, a.FK_Dept,a.Idx FROM Port_Emp A,  WF_NodeTeam B, Port_TeamEmp C WHERE a." + bp.sys.base.Glo.getUserNoWhitOutAS() + "=c.FK_Emp AND B.FK_Team=C.FK_Team AND B.FK_Node=" + nodeID + "  ORDER BY A.Idx";
			}
		}

		dtEmp = DBAccess.RunSQLReturnTable(sql);
		dtEmp.setTableName("Emps");
		ds.Tables.add(dtEmp);
		return ds;
	}
	private DataSet ByGroupOnly(int nodeID, Entity en) throws Exception {
		// 定义数据容器.
		DataSet ds = new DataSet();
		String sql = null;
		DataTable dt = null;
		DataTable dtEmp = null;

		Node nd = new Node(nodeID);

		//部门.
		sql = "SELECT distinct a.No, a.Name, a.ParentNo,a.Idx FROM Port_Dept a, WF_NodeTeam b, Port_TeamEmp c, Port_Emp d WHERE a.No=d.FK_Dept AND b.FK_Group=c.FK_Group AND C.FK_Emp=D.No AND B.FK_Node=" + nodeID + " ORDER BY A.No,A.Idx";
		dt = DBAccess.RunSQLReturnTable(sql);
		dt.setTableName("Depts");
		ds.Tables.add(dt);

		//人员.
		if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.DM ||bp.difference.SystemConfig.getAppCenterDBType() == DBType.Oracle || bp.difference.SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || bp.difference.SystemConfig.getAppCenterDBType() == DBType.HGDB || DBAccess.getAppCenterDBType() == DBType.UX  || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			if (DBAccess.IsExitsTableCol("Port_Emp", "Idx") == true)
			{
				sql = "SELECT * FROM (SELECT distinct a.No,a.Name, a.FK_Dept,a.Idx FROM Port_Emp a,  WF_NodeTeam b, Port_TeamEmp c WHERE a.No=c.FK_Emp AND B.FK_Group=C.FK_Group AND b.FK_Node=" + nodeID + ") ORDER BY FK_Dept,Idx,No";
			}
			else
			{
				sql = "SELECT distinct a.No,a.Name, a.FK_Dept,a.Idx FROM Port_Emp a,  WF_NodeTeam b, Port_TeamEmp c WHERE a.No=c.FK_Emp AND B.FK_Group=C.FK_Group AND b.FK_Node=" + nodeID + " ";
			}
		}
		else
		{
			sql = "SELECT distinct a." + bp.sys.base.Glo.getUserNo() + ",a.Name, a.FK_Dept,a.Idx FROM Port_Emp a,  WF_NodeTeam b, Port_TeamEmp c WHERE a." + bp.sys.base.Glo.getUserNoWhitOutAS() + "=c.FK_Emp AND B.FK_Group=C.FK_Group AND b.FK_Node=" + nodeID + "  ORDER BY A.Idx";
		}

		dtEmp = DBAccess.RunSQLReturnTable(sql);
		dtEmp.setTableName("Emps");
		ds.Tables.add(dtEmp);
		return ds;
	}

	private DataSet ByStationAI(int nodeID, Entity en) throws Exception {
		Node nd = new Node(nodeID);

		int ShenFenModel = nd.GetParaInt("ShenFenModel", 0);

		//如果按照上一个节点的操作员身份计算.
		if (ShenFenModel == 0)
		{
			return ByStationAI(en, WebUser.getDeptNo(), WebUser.getNo(),nd);
		}

		//如果按照指定节点的操作员身份计算.
		if (ShenFenModel == 1)
		{
			int specNodeID = nd.GetParaInt("ShenFenVal", 0);

			int workID = en.GetValIntByKey("OID");

			String sql = "SELECT FK_Emp,FK_Dept FROM WF_GenerWorkerList WHERE FK_Node=" + specNodeID + " AND WorkID=" + workID;
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			String empNo = "", deptNo = "";
			if (dt.Rows.size() == 0)
			{
				Node ndSpec = new Node(specNodeID);
				if (ndSpec.getItIsStartNode() == false)
				{
					throw new RuntimeException("err@没有找到上一步节点，参数信息: NodeID=" + specNodeID + ",WorkID=" + workID + ", 不应该出现的异常，请联系管理员, 有可能您配置了没有路过的节点，作为指定节点的身份计算了。");
				}
				empNo = WebUser.getNo();
				deptNo = WebUser.getDeptNo();
			}
			else
			{
				//获得指定节点的人员编号.
				empNo = dt.Rows.get(0).getValue(0).toString();
				deptNo = dt.Rows.get(0).getValue(1).toString();
			}

			return ByStationAI(en, deptNo, empNo,nd);
		}

		//如果按指定字段的身份计算.
		if (ShenFenModel == 2)
		{
			String empNo = nd.GetParaString("ShenFenVal");
			Emp emp = new Emp(empNo);
			return ByStationAI(en, emp.getDeptNo(), emp.getNo(),nd);
		}

		throw new RuntimeException("err@没有判断的身份模式." + ShenFenModel);
	}

	private DataSet ByStationAI(Entity en, String deptNo, String userID, Node nd) throws Exception {
		///#region 按角色智能计算, 切片模式. 需要对每个角色都要找到接受人，然后把这些接受人累加起来.
		if (nd.getDeliveryStationReqEmpsWay() == 1 || nd.getDeliveryStationReqEmpsWay() == 2)
		{
			DataSet ds = new DataSet();
			DataTable dt = new DataTable();
			dt.Columns.Add("No");
			dt.Columns.Add("Name");
			dt.TableName="Depts";

			DataTable edt = new DataTable();
			edt.TableName="Emps";
			edt.Columns.Add("No");
			edt.Columns.Add("Name");
			edt.Columns.Add("FK_Dept");
			DataTable staDT = DBAccess.RunSQLReturnTable("SELECT FK_Station From WF_NodeStation WHERE FK_Node="+nd.getNodeID());
			String stas="";
			for(DataRow dr : staDT.Rows){
				stas+=dr.getValue(0)+",";
			}
			String[] temps = stas.split("[,]", -1);
			for (String str : temps)
			{
				if(DataType.IsNullOrEmpty(str))
					continue;
				//求一个角色下的人员.
				DataTable mydt1 = FindWorker_GetEmpsByDeptAI(str, deptNo,dt);

				//如果是严谨模式.
				if (nd.getDeliveryStationReqEmpsWay() == 1 && mydt1.Rows.size() == 0)
				{
					Station st = new Station(str);
					throw new RuntimeException("@角色[" + st.getName() +"]下，没有找到人不能发送下去，请检查组织结构是否完整。");
				}
				//累加.
				for (DataRow dr : mydt1.Rows)
				{
					DataRow mydr = edt.NewRow();
					mydr.setValue(0, dr.getValue(0).toString());
					mydr.setValue(1, dr.getValue(1).toString());
					mydr.setValue(2, dr.getValue(2).toString());
					edt.Rows.add(mydr);
				}
			}
			ds.Tables.add(dt);
			ds.Tables.add(edt);
			return ds;
		}
		//第一次计算.
		DataSet ds = ByStationAI_Ext(en, deptNo, userID);

		if (ds.Tables.get(1).Rows.size() == 0)
		{
			//如果在本部门找不到，就到父部门去找.
			Dept mydept = new Dept(deptNo);
			ds = ByStationAI_Ext(en, mydept.getParentNo(), userID);

			if (ds.Tables.get(1).Rows.size() == 0)
			{
				//如果父部门找不到，就到父父部门去找, 在找不到就不找了。
				if (mydept.getParentNo().equals("0") == false)
				{
					Dept myParentDept = new Dept(mydept.getParentNo());
					ds = ByStationAI_Ext(en, myParentDept.getParentNo(), userID);
					if (ds.Tables.get(1).Rows.size() != 0)
					{
						return ds;
					}
				}

				if (ds.Tables.get(1).Rows.size() == 0)
				{
					//如果爷爷部门也找不到，就到于父亲同一级的部门去找.
					Depts pDepts = new Depts();
					pDepts.Retrieve(DeptAttr.ParentNo, mydept.getParentNo(), null);

					for (Dept item : pDepts.ToJavaList())
					{
						ds = ByStationAI_Ext(en, item.getNo(), userID);
						if (ds.Tables.get(1).Rows.size() >= 1)
						{
							return ds;
						}
					}
				}
			}
		}

		//如果实在找不到了，就仅按角色计算.
		if (ds.Tables.get(1).Rows.size() == 0)
		{
			ds = ByStation(this.getNodeID(), en);
		}

		return ds;

	}
	/**
	 指定部门下的，角色人员的数据。
	 @param en
	 @param deptNo
	 @param userNo
	 @return
	*/
	private DataSet ByStationAI_Ext(Entity en, String deptNo, String userNo) throws Exception {
		// 定义数据容器.
		DataSet ds = new DataSet();
		String sql = null;
		DataTable dt = null;
		DataTable dtEmp = null;

		//部门. @zkr
		sql = "";
		sql += "SELECT No, Name FROM Port_Dept WHERE No = '" + deptNo + "'";
		sql += " UNION ";
		sql += "SELECT  No, Name FROM Port_Dept A, Port_DeptEmp B WHERE A.No = B.FK_Dept AND B.FK_Emp = '" + userNo + "'";

		dt = DBAccess.RunSQLReturnTable(sql);
		dt.setTableName("Depts");
		ds.Tables.add(dt);

		//查询人员.
		sql = "SELECT A.No,A.Name, A.FK_Dept FROM Port_Emp A, Port_DeptEmpStation B, WF_NodeStation C WHERE C.FK_Node = " + this.getNodeID() + " AND B.FK_Dept = '" + deptNo + "' AND A.FK_Dept = B.FK_Dept AND B.FK_Station=C.FK_Station AND A.No=b.FK_Emp  ORDER BY A.Idx";
		dtEmp = DBAccess.RunSQLReturnTable(sql);
		dtEmp.setTableName("Emps");
		ds.Tables.add(dtEmp);
		return ds;
	}
	private  DataTable FindWorker_GetEmpsByDeptAI(String stas, String deptNo,DataTable dtt) throws Exception {
		DataTable dt = FindWorker_GetEmpsByStationsAndDepts(stas, deptNo);
		if (dt.Rows.size() == 0)
		{
			//本部门的父级.
			Dept deptMy = new Dept(deptNo);
			dt = FindWorker_GetEmpsByStationsAndDepts(stas, deptMy.getParentNo());

			//本级部门的祖父级,不在向上判断了.
			if (dt.Rows.size() == 0 && deptMy.getParentNo().equals("0") == false)
			{
				Dept deptParent = new Dept(deptMy.getParentNo());
				dt = FindWorker_GetEmpsByStationsAndDepts(stas, deptParent.getParentNo());
				if (dt.Rows.size() != 0){
					DataRow dr = dtt.NewRow();
					Dept dept = new Dept(deptParent.getParentNo());
					dr.setValue("No",dept.getNo());
					dr.setValue("Name",dept.getName());
					dtt.Rows.add(dr);
				}
			}else{
				DataRow dr = dtt.NewRow();
				Dept dept = new Dept(deptMy.getParentNo());
				dr.setValue("No",dept.getNo());
				dr.setValue("Name",dept.getName());
				dtt.Rows.add(dr);
			}

			//扫描评级部门.
			/*if (dt.Rows.size() == 0)
			{
				String deptNos = "";
				Depts depts = new Depts();
				depts.Retrieve(DeptAttr.ParentNo, deptMy.getParentNo());
				for (Dept mydept : depts.ToJavaList())
					deptNos += "," + mydept.getNo();

				dt = FindWorker_GetEmpsByStationsAndDepts(stas, deptNos);
			}*/
			return dt;
		}
		DataRow dr = dtt.NewRow();
		Dept dept = new Dept(deptNo);
		dr.setValue("No",dept.getNo());
		dr.setValue("Name",dept.getName());
		dtt.Rows.add(dr);
		return dt;
	}


	/**
	 获取部门与角色的交集.

	 @param stas 角色集合s
	 @param depts 部门集合s
	 @return
	 */
	private  DataTable FindWorker_GetEmpsByStationsAndDepts(String stas, String depts)
	{
		String sqlEnd = "";
		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
			sqlEnd = " AND OrgNo='" + bp.web.WebUser.getOrgNo() + "'";

		//是单个的.
		if (stas.contains(",") == false && depts.contains(",") == false)
		{
			String sql1 = "SELECT B.No AS No,B.Name AS Name,A.FK_Dept FROM Port_DeptEmpStation A,Port_Emp B WHERE A.FK_Emp=B.No AND A.FK_Station='" + stas + "' AND A.FK_Dept='" + depts + "' "; // + sqlEnd;
			return DBAccess.RunSQLReturnTable(sql1);
		}

		//处理合法的 in 字段.
		if (stas.contains("'") == false)
		{
			String[] temps = stas.split("[,]",-1);
			String mystrs = "";
			for (String temp : temps)
				mystrs += ",'" + temp + "'";

			mystrs = mystrs.substring(1);
			stas = mystrs;
		}

		//处理合法的in 字段.
		if (depts.contains("'") == false)
		{
			String[] temps = depts.split("[,]",-1);
			String mystrs = "";
			for (String temp : temps)
				mystrs += ",'" + temp + "'";

			mystrs = mystrs.substring(1);
			depts = mystrs;
		}

		String sql = "SELECT B.No AS No,B.Name AS Name,A.FK_Dept FROM Port_DeptEmpStation A, Port_Emp B WHERE A.FK_Emp AND B.No AND A.FK_Station IN(" + stas + ") AND A.FK_Dept IN (" + depts + ") "; // + sqlEnd;
		return DBAccess.RunSQLReturnTable(sql);
	}
	/**
	 按照Station获取部门人员树.

	 @param nodeID 节点ID
	 @return 返回数据源dataset
	*/
	private DataSet ByStation(int nodeID, Entity en) throws Exception {
		// 定义数据容器.
		DataSet ds = new DataSet();
		String sql = null;
		DataTable dt = null;
		DataTable dtEmp = null;

		Node nd = new Node(nodeID);
		if (nd.getHisDeliveryWay() == DeliveryWay.BySelectedForPrj)
		{
			//部门.
			sql = "SELECT distinct a.No, a.Name, a.ParentNo,a.Idx FROM Port_Dept a, WF_NodeStation b, Port_DeptEmpStation c, Port_Emp d, WF_PrjEmp E WHERE a.No=d.FK_Dept AND b.FK_Station=c.FK_Station AND C.FK_Emp=D.No AND d." + bp.sys.base.Glo.getUserNoWhitOutAS() + "=e.FK_Emp And C.FK_Emp=E.FK_Emp  AND B.FK_Node=" + nodeID + " AND E.FK_Prj='" + en.GetValStrByKey("PrjNo") + "' ORDER BY A.No,A.Idx";
			dt = DBAccess.RunSQLReturnTable(sql);
			dt.setTableName("Depts");
			ds.Tables.add(dt);

			//人员.
			if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.DM ||bp.difference.SystemConfig.getAppCenterDBType() == DBType.Oracle || bp.difference.SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || bp.difference.SystemConfig.getAppCenterDBType() == DBType.HGDB || DBAccess.getAppCenterDBType() == DBType.UX  || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
			{
				if (DBAccess.IsExitsTableCol("Port_Emp", "Idx") == true)
				{
					sql = "SELECT * FROM (SELECT distinct a.No,a.Name, a.FK_Dept,a.Idx FROM Port_Emp a,  WF_NodeStation b, Port_DeptEmpStation c, WF_PrjEmp d  WHERE a.No=c.FK_Emp AND B.FK_Station=C.FK_Station And a.No=d.FK_Emp And C.FK_Emp=d.FK_Emp AND b.FK_Node=" + nodeID + " AND D.FK_Prj='" + en.GetValStrByKey("PrjNo") + "') ORDER BY FK_Dept,Idx,No";
				}
				else
				{
					sql = "SELECT distinct a.No,a.Name, a.FK_Dept,A.Idx FROM Port_Emp a,  WF_NodeStation b, Port_DeptEmpStation c, WF_PrjEmp d  WHERE a.No=c.FK_Emp AND B.FK_Station=C.FK_Station And a.No=d.FK_Emp And C.FK_Emp=d.FK_Emp AND b.FK_Node=" + nodeID + " AND D.FK_Prj='" + en.GetValStrByKey("PrjNo") + "'  ORDER BY A.Idx ";
				}
			}
			else
			{
				sql = "SELECT distinct a." + bp.sys.base.Glo.getUserNo() + ",a.Name, a.FK_Dept,A.Idx FROM Port_Emp a,  WF_NodeStation b, Port_DeptEmpStation c, WF_PrjEmp d WHERE a." + bp.sys.base.Glo.getUserNoWhitOutAS() + "=c.FK_Emp AND B.FK_Station=C.FK_Station And a." + bp.sys.base.Glo.getUserNoWhitOutAS() + "=d.FK_Emp And C.FK_Emp=d.FK_Emp AND b.FK_Node=" + nodeID + " AND D.FK_Prj='" + en.GetValStrByKey("PrjNo") + "'  ORDER BY A.Idx ";
			}

			dtEmp = DBAccess.RunSQLReturnTable(sql);
			ds.Tables.add(dtEmp);
			dtEmp.setTableName("Emps");
			return ds;
		}


		//部门.
		sql = "SELECT distinct a.No, a.Name, a.ParentNo,a.Idx FROM Port_Dept a, WF_NodeStation b, Port_DeptEmpStation c, Port_Emp d WHERE a.No=d.FK_Dept AND b.FK_Station=c.FK_Station AND C.FK_Emp=D." + bp.sys.base.Glo.getUserNoWhitOutAS() + " AND B.FK_Node=" + nodeID + " ORDER BY A.No,A.Idx";
		dt = DBAccess.RunSQLReturnTable(sql);
		dt.setTableName("Depts");
		ds.Tables.add(dt);

		//人员.
		if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.DM ||bp.difference.SystemConfig.getAppCenterDBType() == DBType.Oracle || bp.difference.SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || bp.difference.SystemConfig.getAppCenterDBType() == DBType.HGDB || DBAccess.getAppCenterDBType() == DBType.UX  || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			if (DBAccess.IsExitsTableCol("Port_Emp", "Idx") == true)
			{
				sql = "SELECT * FROM (SELECT distinct a.No,a.Name, a.FK_Dept,a.Idx FROM Port_Emp a,  WF_NodeStation b, Port_DeptEmpStation c WHERE a.No=c.FK_Emp AND B.FK_Station=C.FK_Station AND b.FK_Node=" + nodeID + ") ORDER BY FK_Dept,Idx,No";
			}
			else
			{
				sql = "SELECT distinct a.No,a.Name, a.FK_Dept,a.Idx FROM Port_Emp a,  WF_NodeStation b, Port_DeptEmpStation c WHERE a.No=c.FK_Emp AND B.FK_Station=C.FK_Station AND b.FK_Node=" + nodeID + "  ORDER BY A.Idx";
			}
		}
		else {
			if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
				sql = "SELECT distinct a.UserID as No,a.Name, a.FK_Dept,a.Idx FROM Port_Emp a,  WF_NodeStation b, Port_DeptEmpStation c WHERE a.No=c.FK_Emp AND B.FK_Station=C.FK_Station AND b.FK_Node=" + nodeID + "  ORDER BY A.Idx";
			else
				sql = "SELECT distinct a.No,a.Name, a.FK_Dept,a.Idx FROM Port_Emp a,  WF_NodeStation b, Port_DeptEmpStation c WHERE a.No=c.FK_Emp AND B.FK_Station=C.FK_Station AND b.FK_Node=" + nodeID + "  ORDER BY A.Idx";
		}

		dtEmp = DBAccess.RunSQLReturnTable(sql);
		dtEmp.setTableName("Emps");
		ds.Tables.add(dtEmp);

		return ds;
	}
	private DataSet ByWebAPI(Entity en) throws Exception {
		DataSet ds = new DataSet();
		//返回值
		String postData = "";
		//配置的api地址
		String apiUrl = this.getSelectorP1();
		if (apiUrl.contains("@WebApiHost")) //可以替换配置文件中配置的webapi地址
		{
			apiUrl = apiUrl.replace("@WebApiHost", bp.difference.SystemConfig.getAppSettings().get("WebApiHost").toString());
		}

		//增加header参数
		Hashtable headerMap = new Hashtable();


		//saas模式，需要传入systemNo
		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			//获取系统编号
			//String systemNo = BP.DA.DBAccess.RunSQLReturnStringIsNull("select No from port_domainExt where No=(select domainExt from port_org where No=(select orgNo from port_emp where No='" + WebUser.getNo() + "'))", "");
			//headerMap.Add("systemNo", systemNo);
			//headerMap.Add("orgNo", WebUser.getOrgNo());
		}
		//集团模式，传入域编号
		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
		{
			//传入域
			headerMap.put("OrgNo", WebUser.getOrgNo());
		}

		//加入token
		headerMap.put("Content-Type", "application/json");
		String token = WebUser.getToken();
		if (DataType.IsNullOrEmpty(token) == true || token.contains(" "))
			throw new RuntimeException("err@非法的Token.");
		if (token.length() <= 6)
			throw new Exception("err@非法的Token." + token);
		headerMap.put("Authorization", token);



		apiUrl = bp.wf.Glo.DealExp(apiUrl, en, null);
		//执行POST
		postData = bp.tools.PubGlo.HttpPostConnect(apiUrl, headerMap, "");

		DataTable dt = bp.tools.Json.ToDataTable(postData);
		dt.setTableName("Emps");
		ds.Tables.add(dt);

		//部门
		//String sql = "SELECT distinct No,Name, ParentNo FROM Port_Dept where No='null'";
		//DataTable dtDept = DBAccess.RunSQLReturnTable(sql);
		//dtDept.setTableName("Depts";
		//ds.Tables.add(dtDept);

		return ds;
	}
}
