package bp.wf;

import bp.wf.template.*;
import bp.wf.data.*;
import bp.da.*;
import bp.en.*;
import bp.sys.*;
import bp.wf.template.sflow.*;

import java.util.Objects;


/**
 流程检查类
 1. 创建修复数据表.
 2. 检查流程设计的合法性.

 */
public class FlowCheckError
{
	///#region 构造方法与属性.
	public DataTable dt = null;
	/**
	 流程

	 */
	public Flow flow = null;
	/**
	 节点s

	 */
	public Nodes nds = null;
	/**
	 通用的

	 */
	public final GERpt getHisGERpt() throws Exception {
		return this.flow.getHisGERpt();
	}
	/**
	 流程检查

	 @param fl 流程实体
	 */
	public FlowCheckError(Flow fl) throws Exception {
		this.flow = fl;
		this.nds = new Nodes(fl.getNo());
		//构造消息存储.
		dt = new DataTable();
		dt.Columns.Add("InfoType");
		dt.Columns.Add("ChekOption"); //检查的项目.
		dt.Columns.Add("Msg");
		dt.Columns.Add("NodeID");
		dt.Columns.Add("NodeName");
	}
	/**
	 流程检查

	 @param flNo 流程编号
	 */
	public FlowCheckError(String flNo) throws Exception {
		this.flow = new Flow(flNo);
		this.nds = new Nodes(this.flow.getNo());

		//构造消息存储.
		dt = new DataTable();
		dt.Columns.Add("InfoType");
		dt.Columns.Add("ChekOption"); //检查的项目.
		dt.Columns.Add("Msg");
		dt.Columns.Add("NodeID");
		dt.Columns.Add("NodeName");
	}
	/**
	 信息

	 @param info
	 @param nd
	 */
	private void AddMsgInfo(String checkOption, String info, Node nd)
	{
		AddMsg("信息", checkOption, info, nd);
	}
	/**
	 警告

	 @param info
	 @param nd
	 */
	private void AddMsgWarning(String checkOption, String info, Node nd)
	{
		AddMsg("警告", checkOption, info, nd);
	}
	private void AddMsgError(String checkOption, String info, Node nd)
	{
		AddMsg("错误", checkOption, info, nd);
	}
	/**
	 增加审核信息

	 @param type 类型
	 @param info 消息
	 @param nd 节点
	 @return
	 */
	private void AddMsg(String type, String checkOption, String info, Node nd)
	{
		DataRow dr = this.dt.NewRow();
		dr.setValue(0,type);
		dr.setValue(1,info);
		dr.setValue(2,checkOption);


		if (nd != null)
		{
			dr.setValue(3,nd.getNodeID());
			dr.setValue(4,nd.getName());
		}
		this.dt.Rows.add(dr);
	}
	///#endregion 构造方法与属性.

	/**
	 校验流程

	 @return
	 */
	public final String DoCheck() throws Exception {
		///#region 基础操作设置.
		DBAccess.RunSQL("DELETE FROM Sys_MapExt WHERE DoWay='0' or DoWay='None'");
		Cache.ClearCache();
		///#endregion 基础操作设置.

		this.flow.ClearAutoNumCache(true);
		try
		{
			//设置自动计算.
			CheckMode_Auto();

			/**检查独立表单的完整性.
			 */
			CheckMode_Frms();

			//通用检查.
			CheckMode_Gener();

			//检查子线程，数据必须是轨迹模式.
			CheckMode_SpecTable();

			//节点表单字段数据类型检查
			CheckModel_FormFields();

			//检查越轨流程,子流程发起.
			CheckModel_SubFlowYanXus();

			//检查报表.
			String str = this.DoCheck_CheckRpt(this.nds);
			if (DataType.IsNullOrEmpty(str) == false)
			{
				this.AddMsgError("检查Rpt表", "@错误:表单枚举,外键字段UIBindKey信息丢失,请描述该字段的设计过程，反馈给开发人员,并删除错误字段重新在表单上创建。错误字段信息如下:", null);

			}

			//检查焦点字段设置是否还有效.
			CheckMode_FocusField();

			//检查质量考核点.
			CheckMode_EvalModel();

			//检查如果是合流节点必须不能是由上一个节点指定接受人员.
			CheckMode_HeliuAccpterRole();

			// 检查是否是计算未来处理人.
			CheckMode_FullSA();

			// 检查游离态节点, 设置是否正确.
			CheckMode_YouLiTai();
			//如果协作模式的节点，方向条件规则是下拉框的，修改为按线的.
			String sql = "UPDATE WF_Node SET CondModel = 2 WHERE CondModel = 1 AND TodolistModel = 1";
			DBAccess.RunSQL(sql);

			// 检查流程， 处理计算字段.
			Node.CheckFlow(nds, this.flow.getNo());
			for (Node nd : nds.ToJavaList())
			{

				nd.ClearAutoNumCache();
				nd.setRow(null);
				Cache2019.DeleteRow("BP.WF.Node", nd.getNodeID() + "");
			}
			//创建track.
			Track.CreateOrRepairTrackTable(this.flow.getNo());

			//如果是引用的表单库的表单，就要检查该表单是否有FID字段，没有就自动增加.
			CheckMode_Ref();

			return bp.tools.Json.ToJson(dt);
		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteError(ex);
			ex.printStackTrace();
			this.AddMsgError("系统异常", ex.getMessage(),null);
			return bp.tools.Json.ToJson(dt);
		}
	}
	/**
	 检查游离态节点, 设置是否正确.

	 */
	public final void CheckMode_YouLiTai() throws Exception {
		//判断是否启用了 【面板】 功能.
		for (Node nd : nds.ToJavaList())
		{
			if (nd.GetParaBoolen(NodeAttr.IsYouLiTai) == true)
			{
				if (nd.getCondModel() != DirCondModel.ByLineCond)
				{
					nd.setCondModel(DirCondModel.ByLineCond);
					nd.Update();

					this.AddMsgWarning("游离态节点设置", "游离态节点转向规则必须是自动计算,系统帮您自动设置了.", nd);
				}
			}
		}

		//查询出来节点.
		String sql = "SELECT NodeID FROM WF_Node WHERE TCEnable=1 AND FK_Flow='" + this.flow.getNo() + "'";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		for (DataRow dr : dt.Rows)
		{
			int nodeID = Integer.parseInt(dr.getValue(0).toString());
			for (Node nd : nds.ToJavaList())
			{
				if (nd.getNodeID() == nodeID)
				{
					if (nd.getCondModel() != DirCondModel.ByLineCond)
					{
						nd.setCondModel(DirCondModel.ByLineCond);
						nd.Update();
						this.AddMsgWarning("流转自定义设置", "启动流转自定义节点的转向规则必须是自动计算,系统帮您自动设置了.", nd);
					}
				}
			}
		}
	}
	/**
	 通用的检查.

	 */
	public final void CheckMode_Gener() throws Exception {
		//条件集合.
		Conds conds = new Conds(this.flow.getNo());
		//删除垃圾数据.
		String sql = "DELETE FROM WF_Direction  WHERE Node NOT IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.flow.getNo() + "') AND FK_Flow='" + this.flow.getNo() + "' ";
		DBAccess.RunSQL(sql);
		sql = "DELETE FROM WF_Direction  WHERE ToNode NOT IN (SELECT NodeID FROM WF_Node WHERE FK_Flow='" + this.flow.getNo() + "') AND FK_Flow='" + this.flow.getNo() + "' ";
		DBAccess.RunSQL(sql);

		for (Node nd : nds.ToJavaList())
		{
			nd.CleanObject();

			//流程是极简模式，设置每一个节点的NodeFrmID为开始节点表单
			if (this.flow.getFlowDevModel() == FlowDevModel.JiJian)
			{
				nd.SetValByKey(NodeAttr.NodeFrmID, "ND" + Integer.parseInt(this.flow.getNo()) + "01");
			}

			///#region 设置路由节点或者用户节点到路由节点的转向规则为连接线
			//路由节点
			if (nd.getHisNodeType() == NodeType.RouteNode)
			{
				nd.setCondModel(DirCondModel.ByLineCond);
			}
			//到达的节点
			Nodes toNDs = nd.getHisToNodes();
			for (Node toND : toNDs.ToJavaList())
			{
				if (toND.getHisNodeType() == NodeType.RouteNode)
				{
					nd.setCondModel(DirCondModel.ByLineCond);
					break;
				}
			}
			///#endregion 设置路由节点或者用户节点到路由节点的转向规则为连接线

			try
			{
				//设置它的位置类型.
				nd.SetValByKey(NodeAttr.NodePosType, nd.GetHisNodePosType().getValue());
			}
			catch (RuntimeException ex)
			{
				this.AddMsgError("节点位置类型", "节点ID: (" + nd.getNodeID() + ")名称: (" + nd.getName() + ") 到达节点错误：" + ex.getMessage(), nd);
			}

			this.AddMsgInfo("修复表单字段", "修复&检查节点信息", nd);
			nd.RepareMap(this.flow);

			// 从表检查。
			MapDtls dtls = new MapDtls("ND" + nd.getNodeID());
			for (MapDtl dtl : dtls.ToJavaList())
			{
				this.AddMsgInfo("从表自动创建表", "检查明细表" + dtl.getName(), nd);
				dtl.getHisGEDtl().CheckPhysicsTable();
			}

			String mapDataNo = "ND" + nd.getNodeID();
			if (nd.getHisFormType() == NodeFormType.RefOneFrmTree || nd.getHisFormType() == NodeFormType.RefNodeFrm)
				mapDataNo = nd.getNodeFrmID();
			if(nd.getHisFlow().getFlowDevModel() == FlowDevModel.JiJian && nd.getItIsStartNode() == false)
				mapDataNo = nd.getNodeFrmID();
			MapAttrs mattrs = new MapAttrs(mapDataNo);

			///#region 对节点的访问规则进行检查

			//this.AddMsgInfo("开始对节点的访问规则进行检查", nd);
			if(nd.getHisNodeType() == NodeType.UserNode){
				switch (nd.getHisDeliveryWay())
				{
					case ByStation:
					case FindSpecDeptEmpsInStationlist:
						if (nd.getNodeStations().size() == 0)
						{
							this.AddMsgError("接收人规则", "错误:您设置了该节点的访问规则是按角色，但是您没有为节点绑定角色。", nd);
						}
						break;
					case ByDept:
						if (nd.getNodeDepts().size() == 0)
						{
							this.AddMsgError("接收人规则", "设置了该节点的访问规则是按部门，但是您没有为节点绑定部门", nd);
						}

						break;
					case ByBindEmp:
						if (nd.getNodeEmps().size() == 0)
						{
							this.AddMsgError("接收人规则", "您设置了该节点的访问规则是按人员，但是您没有为节点绑定人员。", nd);
						}

						break;
					case BySpecNodeEmp: //按指定的角色计算.
					case BySpecNodeEmpStation: //按指定的角色计算.
						if (nd.getDeliveryParas().trim().length() == 0)
						{
							this.AddMsgError("接收人规则", "您设置了该节点的访问规则是按指定的角色计算，但是您没有设置节点编号。", nd);
						}
						else
						{
							if (DataType.IsNumStr(nd.getDeliveryParas()) == false)
							{
								this.AddMsgError("接收人规则", "您没有设置指定角色的节点编号，目前设置的为{" + nd.getDeliveryParas() + "}", nd);
							}
						}
						break;
					case ByDeptAndStation: //按部门与角色的交集计算.
						String mysql = "";
						//added by liuxc,2015.6.30.
						//区别集成与BPM模式
						mysql = "SELECT pdes.fk_emp AS No" + " FROM   Port_DeptEmpStation pdes" + "        INNER JOIN WF_NodeDept wnd" + "             ON  wnd.fk_dept = pdes.fk_dept" + "             AND wnd.fk_node = " + nd.getNodeID() + "        INNER JOIN WF_NodeStation wns" + "             ON  wns.FK_Station = pdes.fk_station" + "             AND wnd.fk_node =" + nd.getNodeID() + " ORDER BY" + "        pdes.fk_emp";

						DataTable mydt = DBAccess.RunSQLReturnTable(mysql);
						if (mydt.Rows.size() == 0)
						{
							this.AddMsgError("接收人规则", "按照角色与部门的交集计算错误，没有人员集合{" + mysql + "}", nd);
						}
						break;
					case BySQL:
					case BySQLAsSubThreadEmpsAndData:
						if (nd.getDeliveryParas().trim().length() <= 5)
						{
							this.AddMsgError("接收人规则", "您设置了该节点的访问规则是按SQL查询，但是您没有在节点属性里设置查询sql，此sql的要求是查询必须包含No,Name两个列，sql表达式里支持@+字段变量，详细参考开发手册.", nd);
							continue;
						}

						sql = nd.getDeliveryParas();
						for (MapAttr item : mattrs.ToJavaList())
						{
							if (item.getItIsNum())
							{
								sql = sql.replace("@" + item.getKeyOfEn(), "0");
							}
							else
							{
								sql = sql.replace("@" + item.getKeyOfEn(), "'0'");
							}
						}

						sql = sql.replace("@WebUser.No", "'ss'");
						sql = sql.replace("@WebUser.Name", "'ss'");
						sql = sql.replace("@WebUser.FK_DeptName", "'ss'");
						sql = sql.replace("@WebUser.FK_Dept", "'ss'");
						sql = sql.replace("@WebUser.DeptNo", "'ss'");

						sql = sql.replace("''''", "''"); //出现双引号的问题.

						if (sql.contains("@"))
						{
							this.AddMsgError("接收人规则", "您编写的sql变量填写不正确，实际执行中，没有被完全替换下来，确认是否是系统变量.:" + sql, nd);
							continue;
						}

						DataTable testDB = null;
						try
						{
							testDB = DBAccess.RunSQLReturnTable(sql);
						}
						catch (RuntimeException ex)
						{
							//this.AddMsgError("接收人规则", "您设置了该节点的访问规则是按SQL查询,执行此语句错误." + sql + " err:" + ex.getMessage(), nd);
							break;
						}

						if (testDB.Columns.contains("no") == false || testDB.Columns.contains("name") == false)
						{
							this.AddMsgError("接收人规则", "您设置了该节点的访问规则是按SQL查询，设置的sql不符合规则，此sql的要求是查询必须包含No,Name两个列，sql表达式里支持@+字段变量，详细参考开发手册.", nd);
						}

						break;
					case ByPreviousNodeFormEmpsField:
					case ByPreviousNodeFormStationsAI:
					case ByPreviousNodeFormStationsOnly:
					case ByPreviousNodeFormDepts:
						//去rpt表中，查询是否有这个字段
						String str = String.valueOf(nd.getNodeID()).substring(0, String.valueOf(nd.getNodeID()).length() - 2);
						MapAttrs rptAttrs = new MapAttrs();
						rptAttrs.Retrieve(MapAttrAttr.FK_MapData, "ND" + str + "Rpt", MapAttrAttr.KeyOfEn);

						if (rptAttrs.contains(MapAttrAttr.KeyOfEn, nd.getDeliveryParas()) == false)
						{
							//检查节点字段是否有FK_Emp字段
							this.AddMsgError("接收人规则", "您设置了该节点的访问规则是[06.按上一节点表单指定的字段值作为本步骤的接受人]，但是您没有在节点属性的[访问规则设置内容]里设置指定的表单字段，详细参考开发手册.", nd);
						}

						break;
					case BySelected: // 由上一步发送人员选择
						break;
					case ByPreviousNodeEmp: // 与上一个节点人员相同.
						if (nd.getItIsStartNode())
						{
							this.AddMsgError("接收人规则", "节点访问规则设置错误:开始节点，不允许设置与上一节点的工作人员相同.", nd);
							break;
						}
						break;
					default:
						break;
				}
			}

			///#endregion

			///#region 检查节点完成条件，方向条件的定义.
			if (conds.size() != 0)
			{
				//this.AddMsgInfo("方向条件","开始检查(" + nd.Name + ")方向条件:", nd);

				for (Cond cond : conds.ToJavaList())
				{

					Node ndOfCond = new Node();
					ndOfCond.setNodeID(cond.getNodeID());
					if (ndOfCond.RetrieveFromDBSources() == 0)
					{
						continue;
					}

					if (cond.getAttrKey().length() < 2)
					{
						continue;
					}
					String frmID = cond.GetValStringByKey(CondAttr.FrmID);
					if (DataType.IsNullOrEmpty(frmID) == false)
					{
						GEEntity en = new GEEntity(frmID);
						if (en.getEnMap().getAttrs().contains(cond.getAttrKey()) == false)
						{
							this.AddMsgError("方向条件", "属性:" + cond.getAttrKey() + " , " + cond.getAttrName() + " 不存在。", nd);
							continue;
						}
					}
				}
			}
			///#endregion 检查节点完成条件的定义.
		}
	}

	/**
	 流程属性的预先计算与基础的更新

	 */
	public final void CheckMode_Auto()
	{
		// 设置流程名称.
		DBAccess.RunSQL("UPDATE WF_Node SET FlowName = (SELECT Name FROM WF_Flow WHERE NO=WF_Node.FK_Flow)");

		//设置单据编号只读格式.
		DBAccess.RunSQL("UPDATE Sys_MapAttr SET UIIsEnable=0 WHERE KeyOfEn='BillNo' AND UIIsEnable=1");

		//开始节点不能有会签.
		DBAccess.RunSQL("UPDATE WF_Node SET HuiQianRole=0 WHERE NodePosType=0 AND HuiQianRole !=0");

		//开始节点不能有退回.
		//DBAccess.RunSQL("UPDATE WF_Node SET ReturnRole=0 WHERE NodePosType=0 AND ReturnRole !=0");

		//删除垃圾,非法数据.
		String sqls = "DELETE FROM Sys_FrmSln WHERE FK_MapData NOT IN (SELECT No from Sys_MapData)";
		sqls += "@ DELETE FROM WF_Direction WHERE Node=ToNode";
		DBAccess.RunSQLs(sqls);

		//更新计算数据.
		//this.flow.NumOfBill = DBAccess.RunSQLReturnValInt("SELECT count(*) FROM Sys_FrmPrintTemplate WHERE NodeID IN (SELECT NodeID FROM WF_Flow WHERE No='" + this.flow.getNo() + "')");
		//this.flow.NumOfDtl = DBAccess.RunSQLReturnValInt("SELECT count(*) FROM Sys_MapDtl WHERE FK_MapData='ND" + int.Parse(this.flow.getNo()) + "Rpt'");
		//this.flow.DirectUpdate();

		//一直没有找到设置3列，自动回到四列的情况.
		//DBAccess.RunSQL("UPDATE Sys_MapAttr SET ColSpan=3 WHERE  UIHeight<=23 AND ColSpan=4");
	}
	/**
	 检查独立表单的完整性.

	 */
	public final void CheckMode_Frms() throws Exception {
		FrmNodes fns = new FrmNodes();
		fns.Retrieve(FrmNodeAttr.FK_Flow, this.flow.getNo());
		String frms = "";
		String err = "";
		for (FrmNode item : fns.ToJavaList())
		{
			if (DataType.IsNullOrEmpty(item.getFrmID()) == true)
			{
				item.Delete();
				continue;
			}
			if (frms.contains(item.getFrmID() + ","))
			{
				continue;
			}
			Node nd  = new Node(item.getNodeID());
			//删除垃圾数据
			if(nd.getHisFormType() == NodeFormType.RefOneFrmTree && nd.getNodeFrmID().equals(item.getFrmID())==false){
				item.Delete();
				continue;
			}
			frms += item.getFrmID() + ",";


			if(nd.getHisFormType() != NodeFormType.EntityTS){
				MapData md = new MapData();
				md.setNo(item.getFrmID());
				if (md.RetrieveFromDBSources() == 0)
				{
					this.AddMsgError("绑定表单库的表单", "节点绑定的表单ID=" + item.getFrmID() + "，但该表单已经不存在.", new Node(item.getNodeID()));
					continue;
				}
				md.ClearCache();
				md.RepairMap();
				GEEntity en = new GEEntity(md.getNo());
				en.CheckPhysicsTable();
			}

		}
	}
	/**
	 如果是引用的表单库的表单，就要检查该表单是否有FID字段，没有就自动增加.

	 */
	public final void CheckMode_Ref() throws Exception {
		for (Node nd : nds.ToJavaList())
		{
			if (nd.getHisFormType() == NodeFormType.RefOneFrmTree)
			{
				MapAttr mattr = new MapAttr();
				mattr.setMyPK(nd.getNodeFrmID() + "_FID");
				if (mattr.RetrieveFromDBSources() == 0)
				{
					mattr.SetValByKey(MapAttrAttr.KeyOfEn, "FID");
					mattr.SetValByKey(MapAttrAttr.FK_MapData, nd.getNodeFrmID());
					mattr.SetValByKey(MapAttrAttr.MyDataType, DataType.AppInt);
					mattr.SetValByKey(MapAttrAttr.UIVisible, false);
					mattr.SetValByKey(MapAttrAttr.Name, "FID(自动增加)");


					mattr.Insert();

					GEEntity en = new GEEntity(nd.getNodeFrmID());
					en.CheckPhysicsTable();
				}
			}
		}
	}
	/**
	 子线城，子线程的表单必须是轨迹模式

	 */
	public final void CheckMode_SpecTable() throws Exception {
		for (Node nd : nds.ToJavaList())
		{
			if (nd.getItIsSubThread() == false)
			{
				continue;
			}
			MapData md = new MapData();
			md.setNo("ND" + nd.getNodeID());
			if (md.RetrieveFromDBSources() == 1)
			{
				if (nd.getItIsSubThread() == false && md.getPTable().equals(this.flow.getPTable())==false)
				{
					md.setPTable(this.flow.getPTable());
					md.Update();
					md.ClearCache();
				}
				if(nd.getItIsSubThread() == true){
					md.setPTable("ND" + nd.getNodeID());
					md.Update();
					md.ClearCache();
				}
			}
			//检查数据表.
			GEEntity geEn = new GEEntity(md.getNo());
			geEn.CheckPhysicsTable();
		}
	}
	/**
	 检查越轨流程,子流程发起.

	 */
	public final void CheckModel_SubFlowYanXus() throws Exception {
		String msg = "";
		SubFlowYanXus yanxuFlows = new SubFlowYanXus();
		yanxuFlows.Retrieve(SubFlowYanXuAttr.SubFlowNo, this.flow.getNo());

		for (SubFlowYanXu flow : yanxuFlows.ToJavaList())
		{
			Flow fl = new Flow(flow.getSubFlowNo());
		}
	}

	/**
	 检查焦点字段设置是否还有效

	 */
	public final void CheckMode_FocusField() throws Exception {
		String msg = "";
		//获得gerpt字段.
		GERpt rpt = this.flow.getHisGERpt();
		for (Attr attr : rpt.getEnMap().getAttrs())
		{
			rpt.SetValByKey(attr.getKey(), "0");
		}
		for (Node nd : nds.ToJavaList())
		{
			if (nd.getFocusField().trim().equals(""))
			{
				Work wk = nd.getHisWork();
				String attrKey = "";
				for (Attr attr : wk.getEnMap().getAttrs())
				{
					if (attr.getUIVisible() == true && attr.getUIIsDoc() && attr.getUIIsReadonly() == false)
					{
						attrKey = attr.getDesc() + ":@" + attr.getKey();
					}
				}

				if (attrKey.equals(""))
				{
					msg = "节点ID:" + nd.getNodeID() + " 名称:" + nd.getName() + "属性里没有设置焦点字段，会导致信息写入轨迹表空白，为了能够保证流程轨迹是可读的请设置焦点字段.";
					this.AddMsgWarning("焦点字段", msg, nd);
				}
//				else
//				{
//					msg = "节点ID:" + nd.getNodeID() + " 名称:" + nd.getName() + "属性里没有设置焦点字段，会导致信息写入轨迹表空白，为了能够保证流程轨迹是可读的系统自动设置了焦点字段为" + attrKey + ".";
//					this.AddMsgInfo("焦点字段", msg, nd);
//
//					nd.setFocusField(attrKey);
//					nd.DirectUpdate();
//				}
				continue;
			}

			Object tempVar = nd.getFocusField();
			String strs = (String)((tempVar instanceof String) ? tempVar : null);
			strs = Glo.DealExp(strs, rpt, "err");
			if (strs.contains("@") == true)
			{
				msg = "焦点字段（" + nd.getFocusField() + "）在节点(step:" + nd.getStep() + " 名称:" + nd.getName() + ")属性里的设置已无效，表单里不存在该字段.";
				this.AddMsgWarning("焦点字段", msg, nd);
			}

			if (this.flow.getItIsMD5())
			{
				if (nd.getHisWork().getEnMap().getAttrs().contains(WorkAttr.MD5) == false)
				{
					nd.RepareMap(this.flow);
				}
			}
		}
	}
	/**
	 检查质量考核点

	 */
	public final void CheckMode_EvalModel()
	{
		String msg = "";
		for (Node nd : nds.ToJavaList())
		{
			if (nd.getItIsEval())
			{
				//如果是质量考核点，检查节点表单是否具别质量考核的特别字段？
				String sql = "SELECT COUNT(*) FROM Sys_MapAttr WHERE FK_MapData='ND" + nd.getNodeID() + "' AND KeyOfEn IN ('EvalEmpNo','EvalEmpName','EvalEmpCent')";
				if (DBAccess.RunSQLReturnValInt(sql) != 3)
				{
					this.AddMsgInfo("质量考核", "@信息:您设置了节点(" + nd.getNodeID() + "," + nd.getName() + ")为质量考核节点，但是您没有在该节点表单中设置必要的节点考核字段.", nd);
				}
			}
		}
	}
	/**
	 是否是自动计算未来处理人?

	 */
	public final void CheckMode_FullSA() throws Exception {
		//是否是自动计算未来处理人.
		if (this.flow.getItIsFullSA() == false)
		{
			return;
		}

		String msg = "";
		for (Node nd : nds.ToJavaList())
		{
			//方向条件转向规则设置为，自动计算的.
			if (nd.getCondModel() != DirCondModel.ByLineCond)
			{
				nd.setCondModel(DirCondModel.ByLineCond);
				nd.Update();
				this.AddMsgInfo("计算未来处理人", "计算未来接收人的流程，转向规则必须是按照条件计算，系统已经自动为您修复。", nd);
			}
			if (nd.getItIsStartNode() == false && nd.getHisNodeType() == NodeType.UserNode)
			{
				if (nd.getHisDeliveryWay() == DeliveryWay.BySelected || nd.getHisDeliveryWay() == DeliveryWay.BySelected_2 || nd.getHisDeliveryWay() == DeliveryWay.BySelected_2)
				{
					this.AddMsgError("计算未来处理人", "计算未来处理人的流程，接收人规则不能是主观选择的，请在节点右键设置接收人规则.", nd);
				}
			}
		}
	}
	/**
	 检查如果是合流节点必须不能是由上一个节点指定接受人员.

	 @return
	 */
	public final void CheckMode_HeliuAccpterRole()
	{
		String msg = "";
		for (Node nd : nds.ToJavaList())
		{
			//如果是合流节点.
			if (nd.getHisNodeWorkType() == NodeWorkType.WorkHL || nd.getHisNodeWorkType() == NodeWorkType.WorkFHL)
			{
				if (nd.getHisDeliveryWay() == DeliveryWay.BySelected)
				{
					msg = "节点ID:" + nd.getNodeID() + " 名称:" + nd.getName() + "是合流或者分合流节点，但是该节点设置的接收人规则为由上一步指定，这是错误的，应该为自动计算而非每个子线程人为的选择.";
					this.AddMsgError("合流节点接收人设置", msg, nd);
				}
			}
		}
	}

	/**
	 节点表单字段数据类型检查，名字相同的字段出现类型不同的处理方法：依照不同于NDxxRpt表中同名字段类型为基准

	 @return 检查结果
	 */
	private String CheckModel_FormFields()
	{
		StringBuilder errorAppend = new StringBuilder();
		errorAppend.append("@信息: -------- 流程节点表单的字段类型检查: ------ ");
		try
		{
			Nodes nds = new Nodes(this.flow.getNo());
			String fk_mapdatas = "'ND" + Integer.parseInt(this.flow.getNo()) + "Rpt'";
			for (Node nd : nds.ToJavaList())
			{
				fk_mapdatas += ",'ND" + nd.getNodeID() + "'";
			}

			//筛选出类型不同的字段.
			String checkSQL = "SELECT   AA.KEYOFEN, COUNT(*) AS MYNUM FROM (" + "  SELECT A.KEYOFEN,  MYDATATYPE,  COUNT(*) AS MYNUM " + "  FROM SYS_MAPATTR A WHERE FK_MAPDATA IN (" + fk_mapdatas + ") GROUP BY KEYOFEN, MYDATATYPE" + ")  AA GROUP BY  AA.KEYOFEN HAVING COUNT(*) > 1";
			DataTable dt_Fields = DBAccess.RunSQLReturnTable(checkSQL);
			for (DataRow row : dt_Fields.Rows)
			{
				String keyOfEn = row.get("KEYOFEN").toString();
				String myNum = row.get("MYNUM").toString();
				int iMyNum = 0;
				if (DataType.IsNullOrEmpty(myNum) == false)
					iMyNum = Integer.parseInt(myNum);
				//存在2种以上数据类型，有手动进行调整
				if (iMyNum > 2)
				{
					errorAppend.append("@错误：字段名" + keyOfEn + "在此流程表(" + fk_mapdatas + ")中存在2种以上数据类型(如：int，float,varchar,datetime)，请手动修改。");
					return errorAppend.toString();
				}

				//存在2种数据类型，以不同于NDxxRpt字段类型为主
				MapAttr baseMapAttr = new MapAttr();
				MapAttr rptMapAttr = new MapAttr("ND" + Integer.parseInt(this.flow.getNo()) + "Rpt", keyOfEn);

				//Rpt表中不存在此字段
				if (rptMapAttr == null || Objects.equals(rptMapAttr.getMyPK(), ""))
				{
					this.DoCheck_CheckRpt(this.flow.getHisNodes());
					rptMapAttr = new MapAttr("ND" + Integer.parseInt(this.flow.getNo()) + "Rpt", keyOfEn);
					this.getHisGERpt().CheckPhysicsTable();
				}

				//Rpt表中不存在此字段,直接结束
				if (rptMapAttr == null || Objects.equals(rptMapAttr.getMyPK(), ""))
				{
					continue;
				}

				for (Node nd : nds.ToJavaList())
				{
					MapAttr ndMapAttr = new MapAttr("ND" + nd.getNodeID(), keyOfEn);
					if (ndMapAttr == null || Objects.equals(ndMapAttr.getMyPK(), ""))
					{
						continue;
					}

					//找出与NDxxRpt表中字段数据类型不同的表单
					if (rptMapAttr.getMyDataType() != ndMapAttr.getMyDataType())
					{
						baseMapAttr = ndMapAttr;
						break;
					}
				}
				errorAppend.append("@基础表" + baseMapAttr.getFrmID() + "，字段" + keyOfEn + "数据类型为：" + baseMapAttr.getMyDataTypeStr());
				//根据基础属性类修改数据类型不同的表单
				for (Node nd : nds.ToJavaList())
				{
					MapAttr ndMapAttr = new MapAttr("ND" + nd.getNodeID(), keyOfEn);
					//不包含此字段的进行返回,类型相同的进行返回
					if (ndMapAttr == null || Objects.equals(ndMapAttr.getMyPK(), "") || Objects.equals(baseMapAttr.getMyPK(), ndMapAttr.getMyPK()) || baseMapAttr.getMyDataType() == ndMapAttr.getMyDataType())
					{
						continue;
					}

					ndMapAttr.setName(baseMapAttr.getName());
					ndMapAttr.setMyDataType(baseMapAttr.getMyDataType());
					ndMapAttr.setUIWidth(baseMapAttr.getUIWidth());
					ndMapAttr.setUIHeight(baseMapAttr.getUIHeight());
					ndMapAttr.setMinLen(baseMapAttr.getMinLen());
					ndMapAttr.setMaxLen(baseMapAttr.getMaxLen());
					if (ndMapAttr.Update() > 0)
					{
						errorAppend.append("@修改了" + "ND" + nd.getNodeID() + " 表，字段" + keyOfEn + "修改为：" + baseMapAttr.getMyDataTypeStr());
					}
					else
					{
						errorAppend.append("@错误:修改" + "ND" + nd.getNodeID() + " 表，字段" + keyOfEn + "修改为：" + baseMapAttr.getMyDataTypeStr() + "失败。");
					}
				}
				//修改NDxxRpt
				rptMapAttr.setName(baseMapAttr.getName());
				rptMapAttr.setMyDataType(baseMapAttr.getMyDataType());
				rptMapAttr.setUIWidth(baseMapAttr.getUIWidth());
				rptMapAttr.setUIHeight(baseMapAttr.getUIHeight());
				rptMapAttr.setMinLen(baseMapAttr.getMinLen());
				rptMapAttr.setMaxLen(baseMapAttr.getMaxLen());
				if (rptMapAttr.Update() > 0)
				{
					errorAppend.append("@修改了" + "ND" + Integer.parseInt(this.flow.getNo()) + "Rpt 表，字段" + keyOfEn + "修改为：" + baseMapAttr.getMyDataTypeStr());
				}
				else
				{
					errorAppend.append("@错误:修改" + "ND" + Integer.parseInt(this.flow.getNo()) + "Rpt 表，字段" + keyOfEn + "修改为：" + baseMapAttr.getMyDataTypeStr() + "失败。");
				}
			}

			//筛选出类型不同的字段.
			checkSQL = "SELECT   AA.KEYOFEN, COUNT(*) AS MYNUM FROM (" + "  SELECT A.KEYOFEN,  MaxLen,  COUNT(*) AS MYNUM " + "  FROM SYS_MAPATTR A WHERE FK_MAPDATA IN (" + fk_mapdatas + ") GROUP BY KEYOFEN, MaxLen" + ")  AA GROUP BY  AA.KEYOFEN HAVING COUNT(*) > 1";
			dt_Fields = DBAccess.RunSQLReturnTable(checkSQL);
			for (DataRow row : dt_Fields.Rows)
			{
				String keyOfEn = row.get("KEYOFEN").toString();
				String myNum = row.get("MYNUM").toString();
				int iMyNum = 0;
				if (DataType.IsNullOrEmpty(myNum) == false)
					iMyNum = Integer.parseInt(myNum);

				//获取最大的长度
				String frmID = fk_mapdatas.replace("'ND" + Integer.parseInt(this.flow.getNo()) + "Rpt',","");
				checkSQL = "SELECT MyPK From Sys_MapAttr WHERE MaxLen = (SELECT Max(MaxLen) FROM Sys_MapAttr WHERE FK_MapData IN("+frmID+") AND KeyOfEn='"+keyOfEn+"') AND FK_MapData IN("+frmID+") AND KeyOfEn='"+keyOfEn+"'";
				DataTable dt = DBAccess.RunSQLReturnTable(checkSQL);
				if(dt.Rows.size() == 0)
					continue;
				String mypk = dt.Rows.get(0).getValue(0).toString();
				//存在2种数据类型，以不同于NDxxRpt字段类型为主
				MapAttr baseMapAttr = new MapAttr(mypk);
				MapAttr rptMapAttr = new MapAttr("ND" + Integer.parseInt(this.flow.getNo()) + "Rpt", keyOfEn);

				//Rpt表中不存在此字段
				if (rptMapAttr == null || Objects.equals(rptMapAttr.getMyPK(), ""))
				{
					this.DoCheck_CheckRpt(this.flow.getHisNodes());
					rptMapAttr = new MapAttr("ND" + Integer.parseInt(this.flow.getNo()) + "Rpt", keyOfEn);
					this.getHisGERpt().CheckPhysicsTable();
				}

				//Rpt表中不存在此字段,直接结束
				if (rptMapAttr == null || Objects.equals(rptMapAttr.getMyPK(), ""))
				{
					continue;
				}
				rptMapAttr.setMaxLen(baseMapAttr.getMaxLen());
				rptMapAttr.Update();
			}
		}
		catch (Exception ex)
		{
			errorAppend.append("@错误:" + ex.getMessage());
		}
		return errorAppend.toString();
	}
	/**
	 检查数据报表.

	 @param nds
	 */
	private String DoCheck_CheckRpt(Nodes nds) throws Exception {
		String msg = "";
		String fk_mapData = "ND" + Integer.parseInt(this.flow.getNo()) + "Rpt";
		String flowId = String.valueOf(Integer.parseInt(this.flow.getNo()));

		//生成该节点的 nds 比如  "'ND101','ND102','ND103'"
		String ndsstrs = "";
		for (Node nd : nds.ToJavaList())
		{
			ndsstrs += "'ND" + nd.getNodeID() + "',";
		}
		if(ndsstrs.equals("")==false){
			ndsstrs = ndsstrs.substring(0, ndsstrs.length() - 1);
		}


		///#region 插入字段。
		String sql = "SELECT distinct KeyOfEn FROM Sys_MapAttr WHERE FK_MapData IN (" + ndsstrs + ")";
		if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL)
		{
			sql = "SELECT A.* FROM (" + sql + ") AS A ";
			String sql3 = "DELETE FROM Sys_MapAttr WHERE KeyOfEn NOT IN (" + sql + ") AND FK_MapData='" + fk_mapData + "' ";
			DBAccess.RunSQL(sql3); // 删除不存在的字段.
		}
		else
		{
			String sql2 = "DELETE FROM Sys_MapAttr WHERE KeyOfEn NOT IN (" + sql + ") AND FK_MapData='" + fk_mapData + "' ";
			DBAccess.RunSQL(sql2); // 删除不存在的字段.
		}

		//所有节点表单字段的合集.
		sql = "SELECT MyPK, KeyOfEn,DefVal,Name,LGType,MyDataType,UIContralType,UIBindKey,FK_MapData FROM Sys_MapAttr WHERE FK_MapData IN (" + ndsstrs + ")";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		//求已经存在的字段集合。
		sql = "SELECT KeyOfEn FROM Sys_MapAttr WHERE FK_MapData='ND" + flowId + "Rpt'";
		DataTable dtExits = DBAccess.RunSQLReturnTable(sql);
		String pks = "@";
		for (DataRow dr : dtExits.Rows)
		{
			pks += dr.getValue(0) + "@";
		}

		//查询出来已经有的映射.
		MapAttrs attrs = new MapAttrs(fk_mapData);

		//遍历 - 所有节点表单字段的合集
		for (DataRow dr : dt.Rows)
		{
			//如果是枚举，外键字段，判断是否判定了对应的枚举和外键
			int lgType = Integer.parseInt(dr.getValue("LGType").toString());
			int contralType = Integer.parseInt(dr.getValue("UIContralType").toString());
			String defval = dr.getValue("DefVal") == null?"":dr.getValue("DefVal").toString();

			if ((lgType == 2 && contralType == 1) || (lgType == 0 && contralType == 1 && Integer.parseInt(dr.getValue("MyDataType").toString()) == 1))
			{
				if (dr.getValue("UIBindKey") == null || DataType.IsNullOrEmpty(dr.getValue("UIBindKey").toString()) == true)
				{
					msg += "表单" + dr.getValue("FK_MapData").toString() + "中,外键/外部数据源字段:" + dr.getValue("Name").toString() + "(" + dr.getValue("KeyOfEn").toString() + ");";
				}
			}
			if (lgType == 1 && (dr.getValue("UIBindKey") == null || DataType.IsNullOrEmpty(dr.getValue("UIBindKey").toString()) == true))
			{
				msg += "表单" + dr.getValue("FK_MapData").toString() + "中,枚举字段:" + dr.getValue("Name").toString() + "(" + dr.getValue("KeyOfEn").toString() + ");";
			}

			if (pks.contains("@" + dr.getValue("KeyOfEn").toString() + "@") == true)
			{
				continue;
			}

			String mypk = dr.getValue("MyPK").toString();

			pks += dr.getValue("KeyOfEn").toString() + "@";

			//找到这个属性.
			MapAttr ma = new MapAttr(mypk);
			ma.setMyPK("ND" + flowId + "Rpt_" + ma.getKeyOfEn());
			ma.setFrmID("ND" + flowId + "Rpt");
			ma.setUIIsEnable(false);

			if (ma.getDefValReal().contains("@"))
			{
				//如果是一个有变量的参数.
				ma.setDefVal("");
			}

			//如果包含他,就说已经存在.
			if (attrs.contains("MyPK", ma.getMyPK()) == true)
			{
				if(ma.getDefVal().equals(defval) == false){
					ma.setDefVal(defval);
					ma.Update();
				}
				continue;
			}
			// 如果不存在.
			ma.Insert();
		}

		// 创建mapData.
		MapData md = new MapData();
		md.setNo("ND" + flowId + "Rpt");
		if (md.RetrieveFromDBSources() == 0)
		{
			md.setName(this.flow.getName());
			md.setPTable(this.flow.getPTable());
			md.Insert();
		}
		else
		{
			if (md.getName().equals(this.flow.getName()) == false || md.getPTable().equals(this.flow.getPTable()) == false)
			{
				md.setName(this.flow.getName());
				md.setPTable(this.flow.getPTable());
				md.Update();
			}

		}
		///#endregion 插入字段。

		///#region 补充上流程字段到NDxxxRpt.
		int groupID = 0;
		for (MapAttr attr : attrs.ToJavaList())
		{
			switch (attr.getKeyOfEn())
			{
				case GERptAttr.FK_Dept:
					attr.setUIContralType(UIContralType.TB);
					attr.setLGType(FieldTypeS.Normal);
					attr.setUIVisible(true);
					attr.setGroupID(groupID); // gfs[0].GetValIntByKey("OID");
					attr.setUIIsEnable(false);
					attr.setDefVal("");
					attr.setMaxLen(100);
					attr.Update();
					break;

				case "FK_NY":
					//  attr.UIBindKey = "BP.Pub.NYs";
					attr.setUIContralType(UIContralType.TB);
					attr.setLGType(FieldTypeS.Normal);
					attr.setUIVisible(true);
					attr.setUIIsEnable(false);
					attr.setGroupID(groupID);
					attr.Update();
					break;
				case "FK_Emp":
					break;
				default:
					break;
			}
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.Title) == false)
		{
			// 标题
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.Title); // "FlowEmps";
			attr.setName("标题");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(true);
			attr.setMinLen(0);
			attr.setMaxLen(400);
			attr.setIdx(-100);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.OID) == false)
		{
			// WorkID
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setKeyOfEn("OID");
			attr.setName("WorkID");
			attr.setMyDataType(DataType.AppInt);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setDefVal("0");
			attr.setHisEditType(EditType.Readonly);
			attr.Insert();
		}


		if (attrs.contains(md.getNo() + "_" + GERptAttr.FID) == false)
		{
			// WorkID
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setKeyOfEn("FID");
			attr.setName("FID");
			attr.setMyDataType(DataType.AppInt);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setDefVal("0");
			attr.setHisEditType(EditType.Readonly);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.WFState) == false)
		{
			// 流程状态
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.WFState);
			attr.setName("流程状态");
			attr.setMyDataType(DataType.AppInt);
			attr.setUIBindKey(GERptAttr.WFState);
			attr.setUIContralType(UIContralType.DDL);
			attr.setLGType(FieldTypeS.Enum);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(1000);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.WFSta) == false)
		{
			// 流程状态Ext
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.WFSta);
			attr.setName("状态");
			attr.setMyDataType(DataType.AppInt);
			attr.setUIBindKey(GERptAttr.WFSta);
			attr.setUIContralType(UIContralType.DDL);
			attr.setLGType(FieldTypeS.Enum);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(1000);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FlowEmps) == false)
		{
			// 参与人
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.FlowEmps); // "FlowEmps";
			attr.setName("参与人");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(true);
			attr.setMinLen(0);
			attr.setMaxLen(1000);
			attr.setIdx(-100);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FlowStarter) == false)
		{
			// 发起人
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.FlowStarter);
			attr.setName("发起人");
			attr.setMyDataType(DataType.AppString);

			//attr.UIBindKey = "BP.Port.Emps";
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);

			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FlowStartRDT) == false)
		{
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.FlowStartRDT); // "FlowStartRDT";
			attr.setName("发起时间");
			attr.setMyDataType(DataType.AppDateTime);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(false);
			attr.setIdx(-101);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FlowEnder) == false)
		{
			// 发起人
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.FlowEnder);
			attr.setName("结束人");
			attr.setMyDataType(DataType.AppString);
			// attr.UIBindKey = "BP.Port.Emps";
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-1);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FlowEnderRDT) == false)
		{
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.FlowEnderRDT); // "FlowStartRDT";
			attr.setName("结束时间");
			attr.setMyDataType(DataType.AppDateTime);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(false);
			attr.setIdx(-101);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FlowEndNode) == false)
		{
			// 结束节点
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.FlowEndNode);
			attr.setName("结束节点");
			attr.setMyDataType(DataType.AppInt);
			attr.setDefVal("0");
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(false);
			attr.setHisEditType(EditType.UnDel);
			attr.setIdx(-101);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FlowDaySpan) == false)
		{
			// FlowDaySpan
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.FlowDaySpan); // "FlowStartRDT";
			attr.setName("流程时长(天)");
			attr.setMyDataType(DataType.AppFloat);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(true);
			attr.setUIIsLine(false);
			attr.setIdx(-101);
			attr.setDefVal("0");
			attr.Insert();
		}

		if (attrs.contains(md.getNo()+ "_" + GERptAttr.PFlowNo) == false)
		{
			// 父流程 流程编号
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.PFlowNo);
			attr.setName("父流程编号"); // 父流程流程编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(true);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-100);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.PNodeID) == false)
		{
			// 父流程WorkID
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.PNodeID);
			attr.setName("父流程启动的节点");
			attr.setMyDataType(DataType.AppInt);
			attr.setDefVal("0");
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(false);
			attr.setHisEditType(EditType.UnDel);
			attr.setIdx(-101);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.PWorkID) == false)
		{
			// 父流程WorkID
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.PWorkID);
			attr.setName("父流程WorkID");
			attr.setMyDataType(DataType.AppInt);
			attr.setDefVal("0");
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(false);
			attr.setHisEditType(EditType.UnDel);
			attr.setIdx(-101);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.PEmp) == false)
		{
			// 调起子流程的人员
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.PEmp);
			attr.setName("调起子流程的人员");
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(true);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-100);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.BillNo) == false)
		{
			// 父流程 流程编号
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.BillNo);
			attr.setName("单据编号"); // 单据编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-100);
			attr.Insert();
		}




		if (attrs.contains(md.getNo() + "_" + GERptAttr.AtPara) == false)
		{
			// 父流程 流程编号
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.AtPara);
			attr.setName("参数"); // 单据编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(false);
			attr.setMinLen(0);
			attr.setMaxLen(4000);
			attr.setIdx(-100);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.GUID) == false)
		{
			// 父流程 流程编号
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.GUID);
			attr.setName("GUID"); // 单据编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(false);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(false);
			attr.setMinLen(0);
			attr.setMaxLen(32);
			attr.setIdx(-100);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.PrjNo) == false)
		{
			// 项目编号
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.PrjNo);
			attr.setName("项目编号"); // 项目编号
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-100);
			attr.Insert();
		}
		if (attrs.contains(md.getNo() + "_" + GERptAttr.PrjName) == false)
		{
			// 项目名称
			MapAttr attr = new MapAttr();
			attr.setFrmID( md.getNo());
			attr.setHisEditType(EditType.UnDel);
			attr.setKeyOfEn(GERptAttr.PrjName);
			attr.setName("项目名称"); // 项目名称
			attr.setMyDataType(DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.setLGType(FieldTypeS.Normal);
			attr.setUIVisible(true);
			attr.setUIIsEnable(false);
			attr.setUIIsLine(false);
			attr.setMinLen(0);
			attr.setMaxLen(100);
			attr.setIdx(-100);
			attr.Insert();
		}

		if (attrs.contains(md.getNo() + "_" + GERptAttr.FK_DeptName) == false)
		{
			MapAttr attr = new MapAttr();
			attr.SetValByKey(MapAttrAttr.FK_MapData, md.getNo());
			attr.setEditType(EditType.UnDel);
			attr.SetValByKey(MapAttrAttr.KeyOfEn, "FK_DeptName");
			attr.SetValByKey(MapAttrAttr.Name, "操作员部门名称");
			attr.SetValByKey(MapAttrAttr.MyDataType, DataType.AppString);
			attr.setUIContralType(UIContralType.TB);
			attr.SetValByKey(MapAttrAttr.UIVisible, false);
			attr.SetValByKey(MapAttrAttr.UIIsEnable, false);
			attr.setLGType(FieldTypeS.Normal);
			attr.SetValByKey(MapAttrAttr.MinLen, 0);
			attr.SetValByKey(MapAttrAttr.MaxLen, 50);
			attr.Insert();
		}
		///#endregion 补充上流程字段。

		///#region 为流程字段设置分组。
		try
		{
			String flowInfo = "流程信息";
			GroupField flowGF = new GroupField();
			int num = flowGF.Retrieve(GroupFieldAttr.FrmID, fk_mapData, GroupFieldAttr.Lab, "流程信息");
			if (num == 0)
			{
				flowGF = new GroupField();
				flowGF.setLab(flowInfo);
				flowGF.setFrmID(fk_mapData);
				flowGF.setIdx(-1);
				flowGF.Insert();
			}
			sql = "UPDATE Sys_MapAttr SET GroupID='" + flowGF.getOID() + "' WHERE  FK_MapData='" + fk_mapData + "'  AND KeyOfEn IN('" + GERptAttr.PFlowNo + "','" + GERptAttr.PWorkID + "','" + GERptAttr.FK_Dept + "','" + GERptAttr.FK_NY + "','" + GERptAttr.FlowDaySpan + "','" + GERptAttr.FlowEmps + "','" + GERptAttr.FlowEnder + "','" + GERptAttr.FlowEnderRDT + "','" + GERptAttr.FlowEndNode + "','" + GERptAttr.FlowStarter + "','" + GERptAttr.FlowStartRDT + "','" + GERptAttr.WFState + "')";
			DBAccess.RunSQL(sql);
		}
		catch (RuntimeException ex)
		{
			AddMsgError("检查数据表",ex.getMessage(),null);
		}
		///#endregion 为流程字段设置分组

		///#region 尾后处理.
		GERpt gerpt = this.getHisGERpt();
		gerpt.CheckPhysicsTable(); //让报表重新生成.

		if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.HGDB || DBAccess.getAppCenterDBType() == DBType.UX)
		{
			DBAccess.RunSQL("DELETE FROM Sys_GroupField WHERE FrmID='" + fk_mapData + "' AND  OID NOT IN (SELECT GroupID FROM Sys_MapAttr WHERE FK_MapData = '" + fk_mapData + "')");
		}
		else
		{
			DBAccess.RunSQL("DELETE FROM Sys_GroupField WHERE FrmID='" + fk_mapData + "' AND  OID NOT IN (SELECT GroupID FROM Sys_MapAttr WHERE FK_MapData = '" + fk_mapData + "')");
		}


		DBAccess.RunSQL("UPDATE Sys_MapAttr SET Name='活动时间' WHERE FK_MapData='ND" + flowId + "Rpt' AND KeyOfEn='CDT'");
		DBAccess.RunSQL("UPDATE Sys_MapAttr SET Name='参与者' WHERE FK_MapData='ND" + flowId + "Rpt' AND KeyOfEn='Emps'");
		///#endregion 尾后处理.
		return msg;
	}
}
