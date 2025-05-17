package bp.wf.httphandler;

import bp.da.*;
import bp.port.*;
import bp.*;
import bp.tools.StringHelper;
import bp.wf.*;
import bp.wf.Glo;
import bp.wf.template.*;

import java.util.*;

public class WF_Comm_RefFunc extends bp.difference.handler.DirectoryPageBase
{


	/** 
	 构造函数
	*/
	public WF_Comm_RefFunc()
	{
	}



		///#region Dot2DotTreeDeptEmpModel.htm（部门人员选择）
	/** 
	 保存节点绑定人员信息
	 
	 @return 
	*/
	public final String Dot2DotTreeDeptEmpModel_SaveNodeEmps()throws Exception
	{
		JsonResultInnerData jr = new JsonResultInnerData();
		String nodeid = this.GetRequestVal("nodeid");
		String data = this.GetRequestVal("data");
		String partno = this.GetRequestVal("partno");
		boolean lastpart = false;
		int partidx = 0;
		int partcount = 0;
		int nid = 0;
		String msg = "";
		if (StringHelper.isNullOrEmpty(nodeid))
			throw new RuntimeException("参数nodeid不正确");
		nid = Integer.parseInt(nodeid);

		if (DataType.IsNullOrEmpty(data))
		{
			data = "";
		}

		NodeEmps nemps = new NodeEmps();
		String[] empNos = data.split(",");

		//提交内容过长时，采用分段式提交
		if (DataType.IsNullOrEmpty(partno))
		{
			nemps.Delete(NodeEmpAttr.FK_Node, nid);
		}
		else
		{
			String[] parts = partno.split(java.util.regex.Pattern.quote("/"), -1);

			if (parts.length != 2)
			{
				throw new RuntimeException("参数partno不正确");
			}

			partidx = Integer.parseInt(parts[0]);
			partcount = Integer.parseInt(parts[1]);

			empNos = data.split(",");

			if (partidx == 1)
			{
				nemps.Delete(NodeEmpAttr.FK_Node, nid);
			}

			lastpart = partidx == partcount;
		}

		DataTable dtEmps = DBAccess.RunSQLReturnTable("SELECT No FROM Port_Emp");
		NodeEmp nemp = null;

		for (String empNo : empNos)
		{
			if (dtEmps.Select(String.format("No='%1$s'", empNo)).length + dtEmps.Select(String.format("NO='%1$s'", empNo)).length == 0)
			{
				continue;
			}

			nemp = new NodeEmp();
			nemp.setNodeID(nid);
			nemp.setEmpNo(empNo);
			nemp.Insert();
		}

		if (DataType.IsNullOrEmpty(partno))
		{
			msg = "保存成功";
		}
		else
		{
			class AnonymousType
			{
				public boolean lastpart;
				public int partidx;
				public int partcount;

				public AnonymousType(boolean _lastpart, int _partidx, int _partcount)
				{
					lastpart = _lastpart;
					partidx = _partidx;
					partcount = _partcount;
				}
			}
			jr.setInnerData(new AnonymousType(lastpart, partidx, partcount));

			if (lastpart)
			{
				msg = "保存成功";
			}
			else
			{
				msg = String.format("第{0}/{1}段保存成功", partidx, partcount);
			}
		}

		return transction(bp.tools.Json.ToJson(jr),msg);
	}
	public String transction(String innerData,String msg){
		return "{\"innerData\": "+innerData+",\"msg\":\""+msg+"\"}";
	}


	/** 
	 保存节点绑定部门信息
	 
	 @return 
	*/
	public final String Dot2DotTreeDeptModel_SaveNodeDepts()throws Exception
	{
		JsonResultInnerData jr = new JsonResultInnerData();
		String nodeid = this.GetRequestVal("nodeid");
		String data = this.GetRequestVal("data");
		String partno = this.GetRequestVal("partno");
		boolean lastpart = false;
		int partidx = 0;
		int partcount = 0;
		int nid = 0;

		try {
			nid = Integer.parseInt(nodeid);
		} catch (Exception e) {
			throw new RuntimeException("参数nodeid不正确");
		}
		if (StringHelper.isNullOrEmpty(nodeid))// ||  int.TryParse(nodeid, out nid) == false
			throw new RuntimeException("参数nodeid不正确");

		if (DataType.IsNullOrEmpty(data))
		{
			data = "";
		}

		NodeDepts ndepts = new NodeDepts();
		String[] deptNos = data.split("\\|");;

		//提交内容过长时，采用分段式提交
		if (DataType.IsNullOrEmpty(partno))
		{
			ndepts.Delete(NodeDeptAttr.FK_Node, nid);
		}
		else
		{
			String[] parts = partno.split(java.util.regex.Pattern.quote("/"), -1);

			if (parts.length != 2)
			{
				throw new RuntimeException("参数partno不正确");
			}

			partidx = Integer.parseInt(parts[0]);
			partcount = Integer.parseInt(parts[1]);

			deptNos = data.split("\\|");

			if (partidx == 1)
			{
				ndepts.Delete(NodeDeptAttr.FK_Node, nid);
			}

			lastpart = partidx == partcount;
		}

		DataTable dtDepts = DBAccess.RunSQLReturnTable("SELECT No FROM Port_Dept");
		NodeDept nemp = null;

		for (String deptNo : deptNos)
		{
			if (dtDepts.Select(String.format("No='%1$s'", deptNo)).length + dtDepts.Select(String.format("NO='%1$s'", deptNo)).length == 0)
			{
				continue;
			}

			nemp = new NodeDept();
			nemp.setNodeID(nid);
			nemp.setDeptNo(deptNo);
			nemp.Insert();
		}

		if (DataType.IsNullOrEmpty(partno))
		{
			jr.setMsg("保存成功");
		}
		else
		{
			class AnonymousType
			{
				public boolean lastpart;
				public int partidx;
				public int partcount;

				public AnonymousType(boolean _lastpart, int _partidx, int _partcount)
				{
					lastpart = _lastpart;
					partidx = _partidx;
					partcount = _partcount;
				}
			}
			jr.setInnerData(new AnonymousType(lastpart, partidx, partcount));

			if (lastpart)
			{
				jr.setMsg("保存成功");
			}
			else
			{
				jr.setMsg(String.format("第%1$s/%2$s段保存成功", partidx, partcount));
			}
		}

		return bp.tools.Json.ToJson(jr);
	}


	/** 
	 获取节点绑定人员信息列表
	 
	 @return 
	*/
	public final String Dot2DotTreeDeptModel_GetNodeDepts()
	{
		JsonResultInnerData jr = new JsonResultInnerData();

		DataTable dt = null;
		String nid = this.GetRequestVal("nodeid");
		String sql = "SELECT pd.No,pd.Name,pd1.No DeptNo,pd1.Name DeptName FROM WF_NodeDept wnd " + "  INNER JOIN Port_Dept pd ON pd.No=wnd.FK_Dept " + "  LEFT JOIN Port_Dept pd1 ON pd1.No=pd.ParentNo " + "WHERE wnd.getNodeID() = " + nid + " ORDER BY pd1.Idx, pd.Name";

		dt = DBAccess.RunSQLReturnTable(sql); //, pagesize, pageidx, "No", "Name", "ASC"
		dt.Columns.Add("Code", String.class);
		dt.Columns.Add("Checked", Boolean.class);

		for (DataRow row : dt.Rows)
		{
			row.setValue("Code", bp.tools.chs2py.ConvertStr2Code(row.get("Name") instanceof String ? (String)row.get("Name") : null));
			row.setValue("Checked", true);
		}

		//对Oracle数据库做兼容性处理
		for (DataColumn col : dt.Columns)
		{
			switch (col.ColumnName.toUpperCase())
			{
				case "NO":
					col.ColumnName = "No";
					break;
				case "NAME":
					col.ColumnName = "Name";
					break;
				case "DEPTNO":
					col.ColumnName = "DeptNo";
					break;
				case "DEPTNAME":
					col.ColumnName = "DeptName";
					break;
			}
		}


		jr.setInnerData(dt);
		String re = bp.tools.Json.ToJson(jr);
		if (Objects.equals(bp.sys.Glo.Plant, Plant.JFlow))
		{
			re = re.replace("\"NO\"", "\"No\"").replace("\"NAME\"", "\"Name\"").replace("\"DEPTNO\"", "\"DeptNo\"").replace("\"DEPTNAME\"", "\"DeptName\"");
		}
		return re;
	}

		///#endregion Dot2DotTreeDeptModel.htm（部门选择）


		///#region Dot2DotStationModel.htm（角色选择）

	/** 
	 保存节点绑定角色信息
	 
	 @return 
	*/
	public final String Dot2DotStationModel_SaveNodeStations()throws Exception
	{
		JsonResultInnerData jr = new JsonResultInnerData();
		String nodeid = this.GetRequestVal("nodeid");
		String data = this.GetRequestVal("data");
		String partno = this.GetRequestVal("partno");
		boolean lastpart = false;
		int partidx = 0;
		int partcount = 0;
		int nid = 0;

		try {
			nid = Integer.parseInt(nodeid);
		} catch (Exception e) {
			throw new RuntimeException("参数nodeid不正确");
		}
		if (StringHelper.isNullOrEmpty(nodeid))
			throw new RuntimeException("参数nodeid不正确");

		if (DataType.IsNullOrEmpty(data))
		{
			data = "";
		}

		NodeStations nsts = new NodeStations();
		String[] stNos = data.split("\\|");

		//提交内容过长时，采用分段式提交
		if (DataType.IsNullOrEmpty(partno))
		{
			nsts.Delete(NodeStationAttr.FK_Node, nid);
		}
		else
		{
			String[] parts = partno.split(java.util.regex.Pattern.quote("/"), -1);

			if (parts.length != 2)
			{
				throw new RuntimeException("参数partno不正确");
			}

			partidx = Integer.parseInt(parts[0]);
			partcount = Integer.parseInt(parts[1]);

			stNos = data.split("\\|");

			if (partidx == 1)
			{
				nsts.Delete(NodeStationAttr.FK_Node, nid);
			}

			lastpart = partidx == partcount;
		}

		DataTable dtSts = DBAccess.RunSQLReturnTable("SELECT No FROM Port_Station");
		NodeStation nst = null;

		for (String stNo : stNos)
		{
			if (dtSts.Select(String.format("No='%1$s'", stNo)).length + dtSts.Select(String.format("NO='%1$s'", stNo)).length == 0)
			{
				continue;
			}

			nst = new NodeStation();
			nst.setNodeID(nid);
			nst.setStationNo(stNo);
			nst.Insert();
		}

		if (DataType.IsNullOrEmpty(partno))
		{
			jr.setMsg("保存成功");
		}
		else
		{
			class AnonymousType
			{
				public boolean lastpart;
				public int partidx;
				public int partcount;

				public AnonymousType(boolean _lastpart, int _partidx, int _partcount)
				{
					lastpart = _lastpart;
					partidx = _partidx;
					partcount = _partcount;
				}
			}
			jr.setInnerData(new AnonymousType(lastpart, partidx, partcount));

			if (lastpart)
			{
				jr.setMsg("保存成功");
			}
			else
			{
				jr.setMsg(String.format("第%1$s/%2$s段保存成功", partidx, partcount));
			}
		}

		return bp.tools.Json.ToJson(jr);
	}
	/** 
	 获取部门树根结点
	 
	 @return 
	*/
	public final String Dot2DotStationModel_GetStructureTreeRoot() throws Exception {
		JsonResultInnerData jr = new JsonResultInnerData();

		EasyuiTreeNode node, subnode;
		ArrayList<EasyuiTreeNode> d = new ArrayList<EasyuiTreeNode>();
		String parentrootid = this.GetRequestVal("parentrootid");
		String sql = null;
		DataTable dt = null;

		if (parentrootid == null || parentrootid.isEmpty())
		{
			throw new RuntimeException("参数parentrootid不能为空");
		}

		CheckStationTypeIdxExists();
		boolean isUnitModel = DBAccess.IsExitsTableCol("Port_Dept", "IsUnit");

		if (isUnitModel)
		{
			boolean isValid = DBAccess.IsExitsTableCol("Port_Station", "FK_Unit");

			if (!isValid)
			{
				isUnitModel = false;
			}
		}

		if (isUnitModel)
		{
			sql = String.format("SELECT No,Name,ParentNo FROM Port_Dept WHERE IsUnit = 1 AND ParentNo = '%1$s'", parentrootid);
			dt = DBAccess.RunSQLReturnTable(sql);

			if (dt.Rows.size() == 0)
			{
				dt.Rows.AddDatas("-1", "无单位数据", parentrootid);
			}

			node = new EasyuiTreeNode();
			node.setId("UNITROOT_" + dt.Rows.get(0).getValue("No"));
			node.setText(dt.Rows.get(0).getValue("Name") instanceof String ? (String)dt.Rows.get(0).getValue("Name") : null);
			node.setIconCls("icon-department");
			node.setAttributes(new EasyuiTreeNodeAttributes());
			node.getAttributes().setNo(dt.Rows.get(0).getValue("No") instanceof String ? (String)dt.Rows.get(0).getValue("No") : null);
			node.getAttributes().setName(dt.Rows.get(0).getValue("Name") instanceof String ? (String)dt.Rows.get(0).getValue("Name"): null);
			node.getAttributes().setParentNo(parentrootid);
			node.getAttributes().setTType("UNITROOT");
			node.setState("closed");

			if (!Objects.equals(node.getText(), "无单位数据"))
			{
				node.setChildren(new ArrayList<EasyuiTreeNode>());
				node.getChildren().add(new EasyuiTreeNode());
				node.getChildren().get(0).setText("loading...");
			}

			d.add(node);
		}
		else
		{
			sql = "SELECT No,Name FROM Port_StationType";
			dt = DBAccess.RunSQLReturnTable(sql);

			node = new EasyuiTreeNode();
			node.setId("STROOT_-1");
			node.setText("角色类型");
			node.setIconCls("icon-department");
			node.setAttributes(new EasyuiTreeNodeAttributes());
			node.getAttributes().setNo("-1");
			node.getAttributes().setName("角色类型");
			node.getAttributes().setParentNo(parentrootid);
			node.getAttributes().setTType("STROOT");
			node.setState("closed");

			if (dt.Rows.size() > 0)
			{
				node.setChildren(new ArrayList<EasyuiTreeNode>());
				node.getChildren().add(new EasyuiTreeNode());
				node.getChildren().get(0).setText("loading...");
			}

			d.add(node);
		}

		jr.setInnerData(d);
		jr.setMsg(String.valueOf(isUnitModel).toLowerCase());

		return  bp.tools.Json.ToJson(jr);
	}

	/** 
	 获取指定部门下一级子部门及人员列表
	 
	 @return 
	*/
	public final String Dot2DotStationModel_GetSubUnits() throws Exception {
		String parentid = this.GetRequestVal("parentid");
		String nid = this.GetRequestVal("nodeid");
		String tp = this.GetRequestVal("stype"); //ST,UNIT
		String ttype = this.GetRequestVal("ttype"); //STROOT,UNITROOT,ST,CST,S

		if (parentid == null || parentid.isEmpty())
		{
			throw new RuntimeException("参数parentid不能为空");
		}
		if (nid == null || nid.isEmpty())
		{
			throw new RuntimeException("参数nodeid不能为空");
		}

		EasyuiTreeNode node = null;
		ArrayList<EasyuiTreeNode> d = new ArrayList<EasyuiTreeNode>();
		String sql = "";
		DataTable dt = null;
		bp.wf.template.NodeStations sts = new bp.wf.template.NodeStations();
		String sortField = CheckStationTypeIdxExists() ? "Idx" : "No";

		sts.Retrieve(bp.wf.template.NodeStationAttr.FK_Node, Integer.parseInt(nid), null);

		if (Objects.equals(tp, "ST"))
		{
			if (Objects.equals(ttype, "STROOT"))
			{
				sql = "SELECT No,Name FROM Port_StationType ORDER BY " + sortField + " ASC";
				dt = DBAccess.RunSQLReturnTable(sql);

				for (DataRow row : dt.Rows)
				{
					node = new EasyuiTreeNode();
					node.setId("ST_" + row.get("No"));
					node.setText(row.get("Name") instanceof String ? (String)row.get("Name") : null);
					node.setIconCls("icon-department");
					node.setAttributes(new EasyuiTreeNodeAttributes());
					node.getAttributes().setNo(row.get("No") instanceof String ? (String)row.get("No") : null);
					node.getAttributes().setName(row.get("Name") instanceof String ? (String)row.get("Name") : null);
					node.getAttributes().setParentNo("-1");
					node.getAttributes().setTType("ST");
					node.setState("closed");
					node.setChildren(new ArrayList<EasyuiTreeNode>());
					node.getChildren().add(new EasyuiTreeNode());
					node.getChildren().get(0).setText("loading...");

					d.add(node);
				}
			}
			else
			{
				sql = String.format("SELECT ps.No,ps.Name,ps.FK_StationType,pst.Name FK_StationTypeName FROM Port_Station ps" + " INNER JOIN Port_StationType pst ON pst.setNo(ps.FK_StationType" + " WHERE ps.FK_StationType = '%1$s' ORDER BY ps.Name ASC", parentid);
				dt = DBAccess.RunSQLReturnTable(sql);

				for (DataRow row : dt.Rows)
				{
					node = new EasyuiTreeNode();
					node.setId("S_" + parentid + "_" + row.get("No"));
					node.setText(row.get("Name") instanceof String ? (String)row.get("Name") : null);
					node.setIconCls("icon-user");
					node.setChecked(sts.GetEntityByKey(bp.wf.template.NodeStationAttr.FK_Station, row.get("No")) != null);
					node.setAttributes(new EasyuiTreeNodeAttributes());
					node.getAttributes().setNo(row.get("No") instanceof String ? (String)row.get("No") : null);
					node.getAttributes().setName(row.get("Name") instanceof String ? (String)row.get("Name") : null);
					node.getAttributes().setParentNo(row.get("FK_StationType") instanceof String ? (String)row.get("FK_StationType") : null);
					node.getAttributes().setParentName(row.get("FK_StationTypeName") instanceof String ? (String)row.get("FK_StationTypeName") : null);
					node.getAttributes().setTType("S");
					node.getAttributes().setCode(bp.tools.chs2py.ConvertStr2Code(row.get("Name") instanceof String ? (String)row.get("Name") : null));

					d.add(node);
				}
			}
		}
		else
		{
			//角色所属单位UNIT
			dt = DBAccess.RunSQLReturnTable(String.format("SELECT * FROM Port_Dept WHERE IsUnit = 1 AND ParentNo='%1$s' ORDER BY Name ASC", parentid));

			for (DataRow dept : dt.Rows)
			{
				node = new EasyuiTreeNode();
				node.setId("UNIT_" + dept.get("No"));
				node.setText(dept.get("Name") instanceof String ? (String)dept.get("Name") : null);
				node.setIconCls("icon-department");
				node.setAttributes(new EasyuiTreeNodeAttributes());
				node.getAttributes().setNo(dept.get("No") instanceof String ? (String)dept.get("No") : null);
				node.getAttributes().setName(dept.get("Name") instanceof String ? (String)dept.get("Name") : null);
				node.getAttributes().setParentNo(dept.get("ParentNo") instanceof String ? (String)dept.get("ParentNo") : null);
				node.getAttributes().setTType("UNIT");
				node.getAttributes().setCode(bp.tools.chs2py.ConvertStr2Code(dept.get("Name") instanceof String ? (String)dept.get("Name") : null));
				node.setState("closed");
				node.setChildren(new ArrayList<EasyuiTreeNode>());
				node.getChildren().add(new EasyuiTreeNode());
				node.getChildren().get(0).setText("loading...");

				d.add(node);
			}

			dt = DBAccess.RunSQLReturnTable(String.format("SELECT ps.No,ps.Name,pst.No FK_StationType, pst.Name FK_StationTypeName,ps.FK_Unit,pd.Name FK_UnitName FROM Port_Station ps" + " INNER JOIN Port_StationType pst ON pst.setNo(ps.FK_StationType" + " INNER JOIN Port_Dept pd ON pd.No=ps.FK_Unit" + " WHERE ps.FK_Unit = '%1$s' ORDER BY pst.%2$s ASC,ps.Name ASC", parentid, sortField));

			//增加角色
			for (DataRow st : dt.Rows)
			{
				node = new EasyuiTreeNode();
				node.setId("S_" + st.get("FK_Unit") + "_" + st.get("No"));
				node.setText(st.get("Name") + "[" + st.get("FK_StationTypeName") + "]");
				node.setIconCls("icon-user");
				node.setChecked(sts.GetEntityByKey(bp.wf.template.NodeStationAttr.FK_Station, st.get("No")) != null);
				node.setAttributes(new EasyuiTreeNodeAttributes());
				node.getAttributes().setNo(st.get("No") instanceof String ? (String)st.get("No") : null);
				node.getAttributes().setName(st.get("Name") instanceof String ? (String)st.get("Name") : null);
				node.getAttributes().setParentNo(st.get("FK_Unit") instanceof String ? (String)st.get("FK_Unit") : null);
				node.getAttributes().setParentName(st.get("FK_UnitName") instanceof String ? (String)st.get("FK_UnitName") : null);
				node.getAttributes().setTType("S");
				node.getAttributes().setCode(bp.tools.chs2py.ConvertStr2Code(st.get("Name") instanceof String ? (String)st.get("Name") : null));

				d.add(node);
			}
		}

		return  bp.tools.Json.ToJson(d);
	}

	/** 
	 获取节点绑定人员信息列表
	 
	 @return 
	*/
	public final String Dot2DotStationModel_GetNodeStations()throws Exception {
		JsonResultInnerData jr = new JsonResultInnerData();

		DataTable dt = null;
		String nid = this.GetRequestVal("nodeid");
		int pagesize = Integer.parseInt(this.GetRequestVal("pagesize"));
		int pageidx = Integer.parseInt(this.GetRequestVal("pageidx"));
		String st = this.GetRequestVal("stype");
		String sql = "";
		String sortField = CheckStationTypeIdxExists() ? "Idx" : "No";

		if (st.equals("UNIT")) {
			sql = "SELECT ps.No,ps.Name,pd.No UnitNo,pd.Name UnitName FROM WF_NodeStation wns " + "  INNER JOIN Port_Station ps ON ps.setNo(wns.FK_Station " + "  INNER JOIN Port_Dept pd ON pd.setNo(ps.FK_Unit " + "WHERE wns.FK_Node = " + nid + " ORDER BY ps.Name ASC";
		} else {
			sql = "SELECT ps.No,ps.Name,pst.No UnitNo,pst.Name UnitName FROM WF_NodeStation wns " + "  INNER JOIN Port_Station ps ON ps.setNo(wns.FK_Station " + "  INNER JOIN Port_StationType pst ON pst.setNo(ps.FK_StationType " + "WHERE wns.FK_Node = " + nid + " ORDER BY pst." + sortField + " ASC,ps.Name ASC";
		}

		dt = DBAccess.RunSQLReturnTable(sql); //, pagesize, pageidx, "No", "Name", "ASC"
		dt.Columns.Add("Code", String.class);
		dt.Columns.Add("Checked", Boolean.class);

		for (DataRow row : dt.Rows) {
			row.setValue("Code", bp.tools.chs2py.ConvertStr2Code(row.getValue("Name") instanceof String ? (String) row.getValue("Name") : null));
			row.setValue("Checked", true);
		}

		//对Oracle数据库做兼容性处理
		if (DBAccess.getAppCenterDBType() == DBType.DM ||DBAccess.getAppCenterDBType() == DBType.Oracle) {
			for (DataColumn col : dt.Columns) {
				switch (col.ColumnName) {
					case "NO":
						col.ColumnName = "No";
						break;
					case "NAME":
						col.ColumnName = "Name";
						break;
					case "UNITNO":
						col.ColumnName = "DeptNo";
						break;
					case "UNITNAME":
						col.ColumnName = "DeptName";
						break;
				}
			}
		}

		jr.setInnerData(dt);
		jr.setMsg("");
		String re = bp.tools.Json.ToJson(jr);
		if (Glo.Plant.equals(Plant.JFlow)) {
			re = re.replace("\"NO\"", "\"No\"").replace("\"NAME\"", "\"Name\"").replace("\"UNITNO\"", "\"UnitNo\"").replace("\"UNITNAME\"", "\"UnitName\"");
		}
		return bp.tools.Json.ToJson(re);
	}

		///#endregion Dot2DotStationModel.htm（角色选择）


		///#region Methods
	/** 
	 判断Port_StationType表中是否含有Idx字段
	 
	 @return 
	*/
	public final boolean CheckStationTypeIdxExists() throws Exception {
		if (DBAccess.IsExitsTableCol("Port_StationType", "Idx") == false)
		{
			if (DBAccess.IsView("Port_StationType", bp.difference.SystemConfig.getAppCenterDBType()) == false)
			{
				StationType st = new StationType();
				st.CheckPhysicsTable();

				DBAccess.RunSQL("UPDATE Port_StationType SET Idx = 1");
				return true;
			}
		}
		else
		{
			return true;
		}
		return false;
	}

		///#endregion


		///#region 辅助实体定义
	/** 
	 Eayui tree node对象
	 <p>主要用于数据的JSON化组织</p>
	*/
	public static class EasyuiTreeNode
	{
		private String id;
		public final String getId()
		{
			return id;
		}
		public final void setId(String value)
		{
			id = value;
		}
		private String text;
		public final String getText()
		{
			return text;
		}
		public final void setText(String value)
		{
			text = value;
		}
		private String state;
		public final String getState()
		{
			return state;
		}
		public final void setState(String value)
		{
			state = value;
		}
		private boolean checked;
		public final boolean getChecked()
		{
			return checked;
		}
		public final void setChecked(boolean value)
		{
			checked = value;
		}
		private String iconCls;
		public final String getIconCls()
		{
			return iconCls;
		}
		public final void setIconCls(String value)
		{
			iconCls = value;
		}
		private EasyuiTreeNodeAttributes attributes;
		public final EasyuiTreeNodeAttributes getAttributes()
		{
			return attributes;
		}
		public final void setAttributes(EasyuiTreeNodeAttributes value)
		{
			attributes = value;
		}
		private ArrayList<EasyuiTreeNode> children;
		public final ArrayList<EasyuiTreeNode> getChildren()
		{
			return children;
		}
		public final void setChildren(ArrayList<EasyuiTreeNode> value)
		{
			children = value;
		}
	}

	public static class EasyuiTreeNodeAttributes
	{
		private String No;
		public final String getNo()
		{
			return No;
		}
		public final void setNo(String value)
		{
			No = value;
		}
		private String Name;
		public final String getName()
		{
			return Name;
		}
		public final void setName(String value)
		{
			Name = value;
		}
		private String ParentNo;
		public final String getParentNo()
		{
			return ParentNo;
		}
		public final void setParentNo(String value)
		{
			ParentNo = value;
		}
		private String ParentName;
		public final String getParentName()
		{
			return ParentName;
		}
		public final void setParentName(String value)
		{
			ParentName = value;
		}
		private String TType;
		public final String getTType()
		{
			return TType;
		}
		public final void setTType(String value)
		{
			TType = value;
		}
		private String Code;
		public final String getCode()
		{
			return Code;
		}
		public final void setCode(String value)
		{
			Code = value;
		}
	}

		///#endregion 辅助实体定义
}
