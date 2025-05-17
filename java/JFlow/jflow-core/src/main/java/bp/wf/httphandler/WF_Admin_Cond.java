package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.en.*; import bp.en.Map;
import bp.wf.template.*;
import bp.difference.*;
import bp.*;
import bp.wf.*;

import java.net.URLDecoder;

/**
 页面功能实体
*/
public class WF_Admin_Cond extends bp.difference.handler.DirectoryPageBase
{
	/**
	 构造函数
	*/
	public WF_Admin_Cond()
	{
	}


		///#region 方向优先级.
	/**
	 初始化

	 @return
	*/
	public final String CondPRI_Init() throws Exception {
		Directions dirs = new Directions();
		dirs.Retrieve(DirectionAttr.Node, this.getNodeID(), DirectionAttr.Idx);
		return dirs.ToJson("dt");
		//按照条件的先后计算.
		/*Conds cds = new Conds();
		cds.Retrieve(CondAttr.FK_Node, this.getNodeID(), CondAttr.CondType, 2, CondAttr.Idx);
		for (Cond item : cds.ToJavaList())
		{
			Node nd = new Node(item.getToNodeID());
			item.setNote(nd.getName());
		}

		if (cds.size() <= 1)
		{
			return "info@当前只有[" + cds.size() + "]个条件，无法进行排序.";
		}

		return cds.ToJson("dt");*/
	}
	/**
	 移动.

	 @return
	*/
	public final String CondPRI_Move()
	{
		String[] ens = this.GetRequestVal("MyPKs").split("[,]", -1);
		for (int i = 0; i < ens.length; i++)
		{
			String enNo = ens[i];
			String sql = "UPDATE WF_Direction SET Idx=" + i + " WHERE MyPK='" + enNo + "'";
			DBAccess.RunSQL(sql);
		}
		return "顺序移动成功..";
	}

		///#endregion 方向优先级.

	private Paras ps = new Paras();
	/**
	 初始化Init.

	 @return
	*/
	public final String Condition_Init() throws Exception {
		String toNodeID = this.GetRequestVal("ToNodeID");
		Cond cond = new Cond();
		cond.Retrieve(CondAttr.FK_Node, this.getNodeID(), CondAttr.ToNodeID, toNodeID);
		cond.getRow().SetValByKey("HisDataFrom", cond.getHisDataFrom().toString());

		return cond.ToJson(true);
	}

	/**
	 打开方向条件的初始化.
	 到达的节点.

	 @return
	*/
	public final String ConditionLine_Init()
	{
		ps = new Paras();
		ps.SQL = "SELECT A.NodeID, A.Name FROM WF_Node A,  WF_Direction B WHERE A.NodeID=B.ToNode AND B.Node=" + SystemConfig.getAppCenterDBVarStr() + "Node";
		ps.Add("Node", this.getNodeID());

		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		dt.Columns.get(0).ColumnName = "NodeID";
		dt.Columns.get(1).ColumnName = "Name";
		return bp.tools.Json.ToJson(dt);
	}

		///#region 方向条件-审核组件
	/**
	 保存

	 @return
	*/
	public final String CondByWorkCheck_Save() throws Exception {

		CondType condTypeEnum = CondType.forValue(this.GetRequestValInt("CondType"));
		String sql = this.GetRequestVal("TB_Docs");
		Cond cond = new Cond();
		cond.setHisDataFrom(ConnDataFrom.WorkCheck);
		cond.setNodeID(this.getFKMainNode());
		cond.setToNodeID(this.getToNodeID());
		cond.setFlowNo(this.getFlowNo());
		cond.setOperatorValue(sql);
		cond.setNote(this.GetRequestVal("TB_Note")); //备注.
		cond.setFlowNo(this.getFlowNo());
		cond.setCondType(condTypeEnum);
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			cond.setMyPK(DBAccess.GenerGUID(0, null, null));
			cond.Insert();
		}
		else
		{
			cond.setMyPK(this.getMyPK());
			cond.Update();
		}


		return "保存成功..";
	}


		///#endregion


		///#region 方向条件URL
	/**
	 保存

	 @return
	*/
	public final String CondByUrl_Save() throws Exception {

		String toNodeID = this.GetRequestVal("ToNodeID");
		CondType condTypeEnum = CondType.forValue(this.GetRequestValInt("CondType"));

		String sql = this.GetRequestVal("TB_Docs");
		Cond cond = new Cond();
		cond.setHisDataFrom(ConnDataFrom.Url);

		cond.setNodeID(this.getFKMainNode());
		cond.setToNodeID(this.getToNodeID());

		cond.setFlowNo(this.getFlowNo());
		cond.setOperatorValue(sql);
		cond.setNote(this.GetRequestVal("TB_Note")); //备注.

		cond.setFlowNo(this.getFlowNo());
		cond.setCondType(condTypeEnum);
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			cond.setMyPK(DBAccess.GenerGUID(0, null, null));
			cond.Insert();
		}
		else
		{
			cond.setMyPK(this.getMyPK());
			cond.Update();
		}

		return "保存成功..";
	}

		///#endregion


		///#region WebApi

	/**
	 保存

	 @return
	*/
	public final String CondByWebApi_Save() throws Exception {

		CondType condTypeEnum = CondType.forValue(this.GetRequestValInt("CondType"));

		String sql = this.GetRequestVal("TB_Docs");
		String atParas = this.GetRequestVal("TB_AtParas");

		Cond cond = new Cond();
		cond.setHisDataFrom(ConnDataFrom.WebApi);

		if (this.GetRequestValInt("FK_MainNode") == 0)
		{
			cond.setNodeID(this.getNodeID());
		}
		else
		{
			cond.setNodeID(this.GetRequestValInt("FK_MainNode"));
		}
		if (this.getToNodeID() == 0)
		{
			cond.setToNodeID(this.getNodeID());
		}
		else
		{
			cond.setToNodeID(this.getToNodeID());
		}

		cond.setFlowNo(this.getFlowNo());
		cond.setOperatorValue(sql);
		cond.setNote(this.GetRequestVal("TB_Note")); //备注.
		cond.setFlowNo(this.getFlowNo());
		cond.setCondType(condTypeEnum);
		// cond.OperatorValue = atParas; //在存储一遍.
		cond.SetPara("OperatorValue", atParas);
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			cond.setMyPK(DBAccess.GenerGUID(0, null, null));
			cond.Insert();
		}
		else
		{
			cond.setMyPK(this.getMyPK());
			cond.Update();
		}

		return "保存成功..";
	}


		///#endregion WebApi


		///#region 方向条件 Frm 模版
	/**
	 初始化

	 @return
	*/
	public final String CondByFrm_Init() throws Exception {
		DataSet ds = new DataSet();

		String toNodeID = this.GetRequestVal("ToNodeID");
		Node nd = new Node(this.getNodeID());

		String frmID = this.getFrmID();
		if (DataType.IsNullOrEmpty(frmID) == true)
		{
			frmID = "ND" + Integer.parseInt(nd.getFlowNo()) + "Rpt";
		}

		CondType condTypeEnum = CondType.forValue(this.GetRequestValInt("CondType"));

		//增加条件.
		if (DataType.IsNullOrEmpty(this.getMyPK()) == false)
		{
			Cond cond = new Cond(this.getMyPK());
			ds.Tables.add(cond.ToDataTableField("WF_Cond"));
		}

		String noteIn = "'FID','PRI','PNodeID','PrjNo', 'PrjName', 'FK_NY','FlowDaySpan', 'Rec','CDT','RDT','AtPara','WFSta','FlowNote','FlowStartRDT','FlowEnderRDT','FlowEnder','FlowSpanDays','WFState','OID','PWorkID','PFlowNo','PEmp','FlowEndNode','GUID'";

		//增加字段集合.
		String sql = "";
		if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			sql = "SELECT KeyOfEn as No, KeyOfEn||' - '||Name as Name FROM Sys_MapAttr WHERE FK_MapData='" + frmID + "'";
			sql += " AND KeyOfEn Not IN (" + noteIn + ") ";
			sql += " AND MyDataType NOT IN (6,7) ";
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.MySQL|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			sql = "SELECT KeyOfEn as No, CONCAT(KeyOfEn,' - ', Name ) as Name FROM Sys_MapAttr WHERE FK_MapData='" + frmID + "'";
			sql += " AND KeyOfEn Not IN (" + noteIn + ") ";
			sql += " AND MyDataType NOT IN (6,7) ";
		}
		else
		{
			sql = "SELECT KeyOfEn as No, KeyOfEn+' - '+Name as Name FROM Sys_MapAttr WHERE FK_MapData='" + frmID + "'";
			sql += " AND KeyOfEn Not IN (" + noteIn + ") ";
			sql += " AND MyDataType NOT IN (6,7) ";
		}


		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Sys_MapAttr";
		dt.Columns.get(0).ColumnName = "No";
		dt.Columns.get(1).ColumnName = "Name";

		DataRow dr = dt.NewRow();
		dr.setValue(0, "all");
		dr.setValue(1, "请选择表单字段");
		dt.Rows.add(dr);
		ds.Tables.add(dt);

		return bp.tools.Json.ToJson(ds); // cond.ToJson();
	}
	public final String CondByFrm_InitField() throws Exception {
		//字段属性.
		MapAttr attr = new MapAttr();
		attr.setMyPK("ND" + Integer.parseInt(this.getFlowNo()) + "Rpt_" + this.getKeyOfEn());
		attr.Retrieve();
		return AttrCond(attr);
	}
	/**
	 保存

	 @return
	*/
	public final String CondByFrm_Save() throws Exception {
		//定义变量.
		String field = this.GetRequestVal("DDL_Fields");
		field = "ND" + Integer.parseInt(this.getFlowNo()) + "Rpt_" + field;

		MapAttr attr = new MapAttr(field);

		int toNodeID = this.getToNodeID();
		String oper = this.GetRequestVal("DDL_Operator");

		String operVal = this.GetRequestVal("OperVal");
		String operValT = this.GetRequestVal("OperValText");

		CondType condTypeEnum = CondType.forValue(this.GetRequestValInt("CondType"));

		Cond cond = new Cond();
		cond.setHisDataFrom(ConnDataFrom.NodeForm);
		cond.setDataFromText("表单字段");

		cond.setToNodeID(toNodeID);

		cond.setNodeID(this.getNodeID());
		cond.setOperatorNo(oper);
		cond.setOperatorValue(operVal); //操作值.
		cond.setOperatorValueT(operValT);

		cond.setAttrNo(field); //字段属性.

		cond.setFlowNo(this.getFlowNo());
		cond.setCondType(condTypeEnum);

		cond.setNote("表单[" + attr.getFrmID() + "]字段:[" + attr.getKeyOfEn() + "," + attr.getName() +"][" + oper + "][" + operValT + "]");

		///#region 方向条件，全部更新.
		//Conds conds = new Conds();
		//QueryObject qo = new QueryObject(conds);
		//qo.AddWhere(CondAttr.FK_Node, this.getNodeID());
		//qo.addAnd();
		//qo.AddWhere(CondAttr.DataFrom, (int)ConnDataFrom.NodeForm);
		//qo.addAnd();
		//qo.AddWhere(CondAttr.CondType, (int)condTypeEnum);
		//if (toNodeID != 0)
		//{
		//    qo.addAnd();
		//    qo.AddWhere(CondAttr.ToNodeID, toNodeID);
		//}
		//int num = qo.DoQuery();
		///#endregion

		switch (condTypeEnum)
		{
			case Flow:
			case Node:

				break;
			case Dir:
			case SubFlow: //启动子流程.
				cond.setToNodeID(toNodeID);
				break;
			default:
				throw new RuntimeException("未设计的情况。" + condTypeEnum.toString());
		}
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			cond.setMyPK(DBAccess.GenerGUID(0, null, null));
			cond.Insert();
		}
		else
		{
			cond.setMyPK(this.getMyPK());
			cond.Update();
		}
		return "保存成功!!";
	}

		///#endregion 方向条件 Frm 模版


		///#region 独立表单的方向条件.
	/**
	 初始化

	 @return
	*/
	public final String StandAloneFrm_Init() throws Exception {
		ps = new Paras();
		ps.SQL = "SELECT m.No, m.Name, n.FK_Node, n.FK_Flow FROM WF_FrmNode n INNER JOIN Sys_MapData m ON n.FK_Frm=m.No WHERE n.FrmEnableRole!=5 AND n.FK_Node=" + SystemConfig.getAppCenterDBVarStr() + "FK_Node";
		ps.Add("FK_Node", this.getNodeID());
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		dt.TableName = "Frms";
		dt.Columns.get(0).ColumnName = "No";
		dt.Columns.get(1).ColumnName = "Name";

		//@gaoxin.
		DataRow dr = dt.NewRow();
		dr.setValue(0, "ND" + Integer.parseInt(this.getFlowNo()) + "Rpt");
		dr.setValue(1, "节点表单(内置表单)");
		dt.Rows.add(dr);

		DataSet ds = new DataSet();
		ds.Tables.add(dt);

		//增加条件集合.
		String toNodeID = this.GetRequestVal("ToNodeID");

		// 增加条件.
		if (DataType.IsNullOrEmpty(this.getMyPK()) == false)
		{
			Cond cond = new Cond(this.getMyPK());
			ds.Tables.add(cond.ToDataTableField("WF_Cond"));
		}

		return bp.tools.Json.ToJson(ds);
	}
	/**
	 获得一个表单的字段.

	 @return
	*/
	public final String StandAloneFrm_InitFrmAttr() throws Exception {
		String frmID = this.GetRequestVal("FrmID");
		MapAttrs attrs = new MapAttrs(frmID);
		return attrs.ToJson("dt");
	}
	public final String StandAloneFrm_Save() throws Exception {
		String frmID = this.GetRequestVal("FrmID");

		//定义变量.
		String field = this.GetRequestVal("DDL_Fields");
		field = frmID + "_" + field;

		int toNodeID = this.getToNodeID();
		String oper = this.GetRequestVal("DDL_Operator");
		String operVal = this.GetRequestVal("OperVal");

		//节点,子线城,还是其他
		CondType condTypeEnum = CondType.forValue(this.GetRequestValInt("CondType"));

		Cond cond = new Cond();
		cond.setHisDataFrom(ConnDataFrom.StandAloneFrm);
		cond.setToNodeID(toNodeID);

		cond.setNodeID(this.getNodeID());
		cond.setOperatorNo(oper);
		cond.setOperatorValue(operVal); //操作值.

		cond.setAttrNo(field); //字段属性.

		cond.setFlowNo(this.getFlowNo());
		cond.setCondType(condTypeEnum);

		///#region 方向条件，全部更新.
		//Conds conds = new Conds();
		//QueryObject qo = new QueryObject(conds);
		//qo.AddWhere(CondAttr.FK_Node, this.getNodeID());
		//qo.addAnd();
		//qo.AddWhere(CondAttr.DataFrom, (int)ConnDataFrom.StandAloneFrm);
		//qo.addAnd();
		//qo.AddWhere(CondAttr.CondType, (int)condTypeEnum);
		//if (toNodeID != 0)
		//{
		//    qo.addAnd();
		//    qo.AddWhere(CondAttr.ToNodeID, toNodeID);
		//}
		//int num = qo.DoQuery();
		///#endregion

		/* 执行同步*/

		switch (condTypeEnum)
		{
			case Flow:
			case Node:
				break;
			case Dir:
			case SubFlow:
				cond.setToNodeID(toNodeID);
				break;
			default:
				throw new RuntimeException("未设计的情况。" + condTypeEnum.toString());
		}
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			cond.setMyPK(DBAccess.GenerGUID(0, null, null));
			cond.Insert();
		}
		else
		{
			cond.setMyPK(this.getMyPK());
			cond.Update();
		}
		return "保存成功!!";
	}

	public final String StandAloneFrm_InitField() throws Exception {
		//字段属性.
		MapAttr attr = new MapAttr();
		attr.setMyPK(this.getFrmID() + "_" + this.getKeyOfEn());
		attr.Retrieve();
		return AttrCond(attr);
	}
	private String AttrCond(MapAttr attr) throws Exception {
		//定义数据容器.
		DataSet ds = new DataSet();

		ds.Tables.add(attr.ToDataTableField("Sys_MapAttr"));

		if (attr.getLGType() == FieldTypeS.Enum)
		{
			SysEnums ses = new SysEnums(attr.getUIBindKey());
			ds.Tables.add(ses.ToDataTableField("Enums"));
		}


			///#region 增加操作符 number.
		if (attr.getItIsNum())
		{
			DataTable dtOperNumber = new DataTable();
			dtOperNumber.TableName = "Opers";
			dtOperNumber.Columns.Add("No", String.class);
			dtOperNumber.Columns.Add("Name", String.class);

			if (attr.getMyDataType() == DataType.AppBoolean)
			{
				DataRow dr = dtOperNumber.NewRow();
				dr.setValue("No", "dengyu");
				dr.setValue("Name", "= 等于");
				dtOperNumber.Rows.add(dr);
			}
			else
			{
				DataRow dr = dtOperNumber.NewRow();
				dr.setValue("No", "dengyu");
				dr.setValue("Name", "= 等于");
				dtOperNumber.Rows.add(dr);

				dr = dtOperNumber.NewRow();
				dr.setValue("No", "dayu");
				dr.setValue("Name", " > 大于");
				dtOperNumber.Rows.add(dr);

				dr = dtOperNumber.NewRow();
				dr.setValue("No", "dayudengyu");
				dr.setValue("Name", " >= 大于等于");
				dtOperNumber.Rows.add(dr);

				dr = dtOperNumber.NewRow();
				dr.setValue("No", "xiaoyu");
				dr.setValue("Name", " < 小于");
				dtOperNumber.Rows.add(dr);

				dr = dtOperNumber.NewRow();
				dr.setValue("No", "xiaoyudengyu");
				dr.setValue("Name", " <= 小于等于");
				dtOperNumber.Rows.add(dr);

				dr = dtOperNumber.NewRow();
				dr.setValue("No", "budengyu");
				dr.setValue("Name", " != 不等于");
				dtOperNumber.Rows.add(dr);
			}

			ds.Tables.add(dtOperNumber);
		}
		else
		{

				///#region 增加操作符 string.
			DataTable dtOper = new DataTable();
			dtOper.TableName = "Opers";
			dtOper.Columns.Add("No", String.class);
			dtOper.Columns.Add("Name", String.class);

			DataRow dr = dtOper.NewRow();
			dr.setValue("No", "dengyu");
			dr.setValue("Name", "= 等于");
			dtOper.Rows.add(dr);

			dr = dtOper.NewRow();
			dr.setValue("No", "like");
			dr.setValue("Name", " LIKE 包含");
			dtOper.Rows.add(dr);

			dr = dtOper.NewRow();
			dr.setValue("No", "dayu");
			dr.setValue("Name", ">大于");
			dtOper.Rows.add(dr);

			dr = dtOper.NewRow();
			dr.setValue("No", "xiaoyudengyu");
			dr.setValue("Name", " <= 小于等于");
			dtOper.Rows.add(dr);

			dr = dtOper.NewRow();
			dr.setValue("No", "dayudengyu");
			dr.setValue("Name", " >= 大于等于");
			dtOper.Rows.add(dr);

			dr = dtOper.NewRow();
			dr.setValue("No", "xiaoyu");
			dr.setValue("Name", "<小于");
			dtOper.Rows.add(dr);



			dr = dtOper.NewRow();
			dr.setValue("No", "budengyu");
			dr.setValue("Name", " != 不等于");
			dtOper.Rows.add(dr);
			ds.Tables.add(dtOper);

				///#endregion 增加操作符 string.
		}

			///#endregion 增加操作符 number.

		return bp.tools.Json.ToJson(ds); // cond.ToJson();
	}

		///#endregion


		///#region 方向条件SQL 模版
	/**
	 保存

	 @return
	*/
	public final String CondBySQLTemplate_Save() throws Exception {

		CondType condTypeEnum = CondType.forValue(this.GetRequestValInt("CondType"));
		String sql = this.GetRequestVal("TB_Docs");
		String sqlT = this.GetRequestVal("SqlValT");
		Cond cond = new Cond();
		cond.setHisDataFrom(ConnDataFrom.SQLTemplate);

		cond.setNodeID(this.getFKMainNode());
		cond.setToNodeID(this.getToNodeID());

		cond.setFlowNo(this.getFlowNo());
		cond.setOperatorValue(sql);
		cond.setOperatorValueT(sqlT);
		cond.setNote(this.GetRequestVal("TB_Note")); //备注.

		cond.setFlowNo(this.getFlowNo());
		cond.setCondType(condTypeEnum);
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			cond.setMyPK(DBAccess.GenerGUID(0, null, null));
			cond.Insert();
		}
		else
		{
			cond.setMyPK(this.getMyPK());
			cond.Update();
		}
		return "保存成功..";
	}


		///#endregion 方向条件SQL 模版


		///#region 方向条件SQL

	/** 保存

	 @return
	*/
	public final String CondBySQL_Save() throws Exception {
		CondType condTypeEnum = CondType.forValue(this.GetRequestValInt("CondType"));

		String sql = this.GetRequestVal("TB_Docs");
		String FK_DBSrc = this.GetRequestVal("FK_DBSrc");

		Cond cond = new Cond();
		cond.setHisDataFrom(ConnDataFrom.SQL);
		cond.setNodeID(this.getFKMainNode());
		cond.setToNodeID(this.getToNodeID());

		cond.setFlowNo(this.getFlowNo());
		cond.setOperatorValue(sql);
		cond.setDBSrcNo(FK_DBSrc);
		cond.setNote(this.GetRequestVal("TB_Note")); //备注.

		cond.setFlowNo(this.getFlowNo());
		cond.setCondType(condTypeEnum);
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			cond.setMyPK(DBAccess.GenerGUID(0, null, null));
			cond.Insert();
		}
		else
		{
			cond.setMyPK(this.getMyPK());
			cond.Update();
		}

		return "保存成功..";
	}


		///#endregion


		///#region 方向条件角色

	/**
	 保存

	 @return
	*/
	public final String CondByStation_Save() throws Exception {

		int ToNodeID = this.getToNodeID();

		Cond cond = new Cond();

		String val = this.GetRequestVal("Stations").replace(",", "@");
		String valT = this.GetRequestVal("StationNames");
		cond.setOperatorValue(val);
		cond.setOperatorValueT(valT);
		cond.setSpecOperWay(SpecOperWay.forValue(this.GetRequestValInt("DDL_SpecOperWay")));

		if (cond.getSpecOperWay() != SpecOperWay.CurrOper)
		{
			cond.setSpecOperPara(this.GetRequestVal("TB_SpecOperPara"));
		}
		else
		{
			cond.setSpecOperPara("");
		}

		cond.setHisDataFrom(ConnDataFrom.Stas);
		cond.setFlowNo(this.getFlowNo());
		cond.setNodeID(getFKMainNode());

		cond.setToNodeID(ToNodeID);
		cond.setCondType(CondType.forValue(this.GetRequestValInt("CondType"))); //条件类型. Dir,Node,Flow

		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			cond.setMyPK(DBAccess.GenerGUID(0, null, null));
			cond.Insert();
		}
		else
		{
			cond.setMyPK(this.getMyPK());
			cond.Update();
		}

		return "保存成功..";
	}


		///#endregion


		///#region 按照部门条件计算CondByDept_Delete
	public final String CondByDept_Save() throws Exception {

		CondType condType = CondType.forValue(this.getCondType());
		Cond cond = new Cond();
		cond.setHisDataFrom(ConnDataFrom.Depts);
		cond.setNodeID(this.getFKMainNode());
		cond.setRefFlowNo(this.getFlowNo());
		cond.setFlowNo(this.getFlowNo());
		cond.setToNodeID(this.getToNodeID());
		cond.setCondTypeInt(this.getCondType());

		String val = this.GetRequestVal("depts").replace(",", "@");
		String valT = this.GetRequestVal("deptNames");
		cond.setOperatorValue(val);
		cond.setOperatorValueT(valT);
		cond.setSpecOperWay(SpecOperWay.forValue(this.GetRequestValInt("DDL_SpecOperWay")));
		if (cond.getSpecOperWay() != SpecOperWay.CurrOper)
		{
			cond.setSpecOperPara(this.GetRequestVal("TB_SpecOperPara"));
		}
		else
		{
			cond.setSpecOperPara("");
		}
		cond.setHisDataFrom(ConnDataFrom.Depts);
		cond.setDataFromText("部门条件");

		cond.setFlowNo(this.getFlowNo());
		cond.setCondTypeInt(this.getCondType());
		cond.setNodeID(this.getFKMainNode());

		cond.setToNodeID(this.getToNodeID());
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			cond.setMyPK(DBAccess.GenerGUID(0, null, null));
			cond.Insert();
		}
		else
		{
			cond.setMyPK(this.getMyPK());
			cond.Update();
		}

		return "保存成功!!";
	}


		///#endregion

	public final int getFKMainNode()
	{
		int fk_mainNode = this.GetRequestValInt("FK_MainNode");
		if (fk_mainNode == 0)
		{
			fk_mainNode = this.GetRequestValInt("FK_Node");
		}
		return fk_mainNode;
	}
	/**
	 类型
	*/
	public final int getCondType()
	{
		int val = this.GetRequestValInt("CondType");
		return val;
	}
		///#region 方向条件Para

	/**
	 保存

	 @return
	*/
	public final String CondByPara_Save() throws Exception {

		String toNodeID = this.GetRequestVal("ToNodeID");
		CondType condTypeEnum = CondType.forValue(this.GetRequestValInt("CondType"));
		String sql = this.GetRequestVal("TB_Docs");

		Cond cond = new Cond();

		cond.setHisDataFrom(ConnDataFrom.Paras);

		cond.setNodeID(this.getFKMainNode());
		cond.setToNodeID(this.getToNodeID());

		cond.setFlowNo(this.getFlowNo());
		cond.setOperatorValue(sql);
		cond.setNote(this.GetRequestVal("TB_Note")); //备注.

		cond.setFlowNo(this.getFlowNo());
		cond.setCondType(condTypeEnum);
		if (DataType.IsNullOrEmpty(this.getMyPK()) == true)
		{
			cond.setMyPK(DBAccess.GenerGUID(0, null, null));
			cond.Insert();
		}
		else
		{
			cond.setMyPK(this.getMyPK());
			cond.Update();
		}

		return "保存成功..";
	}


		///#endregion


}
