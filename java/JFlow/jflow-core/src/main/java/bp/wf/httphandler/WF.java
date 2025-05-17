package bp.wf.httphandler;

import bp.da.*;
import bp.difference.StringHelper;
import bp.difference.handler.CommonUtils;
import bp.en.UIContralType;
import bp.sys.*;
import bp.sys.CCFormAPI;
import bp.web.*;
import bp.difference.*;
import bp.wf.Glo;
import bp.wf.port.*;
import bp.wf.template.*;
import bp.tools.*;
import bp.wf.*;
import net.sf.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;

public class WF extends bp.difference.handler.DirectoryPageBase
{

	///#region 确认&未确认工作.

	/**
	 * 已经确认的工作. GL_TodolistConfimTrue
	 * @return
	 */
	public String Confim_True_Init() throws Exception {
		return bp.tools.Json.ToJson(Dev2Interface.DB_Todolist_Confim(true));
	}

	/**
	 * 未确认的工作:GL_TodolistConfimFalse
	 * @return
	 */
	public String Confim_False_Init() throws Exception {
		return bp.tools.Json.ToJson(Dev2Interface.DB_Todolist_Confim(false));
	}

	/**
	 * 执行批量移交
	 * @return
	 * @throws Exception
	 */
	public String Batch_Shift() throws Exception {
		String fromOneUserNo = this.GetRequestVal("FromOneUserNo");
		String workIDs = this.GetRequestVal("WorkIDs");
		String toOneUserNo = this.GetRequestVal("ToOneUserNo");
		return Dev2Interface.Flow_Batch_Shift(fromOneUserNo, workIDs, toOneUserNo);
	}
    ///#endregion 确认&未确认工作.

	public final String DealErrInfo_Save()
	{
		return "";
	}


		///#region 单表单查看.
	/**
	 流程单表单查看

	 @return
	*/
	public final String FrmView_Init() throws Exception {
		Node nd = new Node(this.getNodeID());
		nd.WorkID=this.getWorkID(); //为获取表单ID ( NodeFrmID )提供参数.

		MapData md = new MapData();
		md.setNo(nd.getNodeFrmID());
		if (md.RetrieveFromDBSources() == 0)
		{
			throw new RuntimeException("装载错误，该表单ID=" + md.getNo() + "丢失，请修复一次流程重新加载一次.");
		}

		//获得表单模版.
		DataSet myds = CCFormAPI.GenerHisDataSet(md.getNo(), null, null);


			///#region 把主从表数据放入里面.
		//.工作数据放里面去, 放进去前执行一次装载前填充事件.
		Work wk = nd.getHisWork();
		wk.setOID(this.getWorkID());
		wk.RetrieveFromDBSources();

		//重设默认值.
		//wk.ResetDefaultVal();

		DataTable mainTable = wk.ToDataTableField("MainTable");
		mainTable.TableName = "MainTable";
		myds.Tables.add(mainTable);

			///#endregion

		//加入WF_Node.
		DataTable WF_Node = nd.ToDataTableField("WF_Node");
		myds.Tables.add(WF_Node);


			///#region 加入组件的状态信息, 在解析表单的时候使用.
		FrmNodeComponent fnc = new FrmNodeComponent(nd.getNodeID());
		myds.Tables.add(fnc.ToDataTableField("WF_FrmNodeComponent").copy());

			///#endregion 加入组件的状态信息, 在解析表单的时候使用.


			///#region 把外键表加入DataSet
		DataTable dtMapAttr = myds.GetTableByName("Sys_MapAttr");
		DataTable dt = new DataTable();
		MapExts mes = md.getMapExts();
		MapExt me = new MapExt();
		DataTable ddlTable = new DataTable();
		ddlTable.Columns.Add("No");
		for (DataRow dr : dtMapAttr.Rows)
		{
			String lgType = dr.getValue("LGType").toString();
			String uiBindKey = dr.getValue("UIBindKey").toString();

			if (DataType.IsNullOrEmpty(uiBindKey) == true)
			{
				continue; //为空就continue.
			}

			if (lgType.equals("1") == true)
			{
				continue; //枚举值就continue;
			}

			String uiIsEnable = dr.getValue("UIIsEnable").toString();
			if (uiIsEnable.equals("0") == true && lgType.equals("1") == true)
			{
				continue; //如果是外键，并且是不可以编辑的状态.
			}

			if (uiIsEnable.equals("1") == true && lgType.equals("0") == true)
			{
				continue; //如果是外部数据源，并且是不可以编辑的状态.
			}



			// 检查是否有下拉框自动填充。
			String keyOfEn = dr.getValue("KeyOfEn").toString();
			String fk_mapData = dr.getValue("FK_MapData").toString();


				///#region 处理下拉框数据范围. for 小杨.
			Object tempVar = mes.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLL, MapExtAttr.AttrOfOper, keyOfEn);
			me = tempVar instanceof MapExt ? (MapExt)tempVar : null;
			if (me != null)
			{
				Object tempVar2 = me.getDoc();
				String fullSQL = tempVar2 instanceof String ? (String)tempVar2 : null;
				fullSQL = fullSQL.replace("~", ",");
				fullSQL = bp.wf.Glo.DealExp(fullSQL, wk, null);
				dt = DBAccess.RunSQLReturnTable(fullSQL);
				//重构新表
				DataTable dt_FK_Dll = new DataTable();
				dt_FK_Dll.TableName = keyOfEn; //可能存在隐患，如果多个字段，绑定同一个表，就存在这样的问题.
				dt_FK_Dll.Columns.Add("No", String.class);
				dt_FK_Dll.Columns.Add("Name", String.class);
				for (DataRow dllRow : dt.Rows)
				{
					DataRow drDll = dt_FK_Dll.NewRow();
					drDll.setValue("No", dllRow.get("No"));
					drDll.setValue("Name", dllRow.get("Name"));
					dt_FK_Dll.Rows.add(drDll);
				}
				myds.Tables.add(dt_FK_Dll);
				continue;
			}

				///#endregion 处理下拉框数据范围.

			// 判断是否存在.
			if (myds.contains(uiBindKey) == true)
			{
				continue;
			}

			DataTable mydt = bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey, null);
			if (mydt == null)
			{
				DataRow ddldr = ddlTable.NewRow();
				ddldr.setValue("No", uiBindKey);
				ddlTable.Rows.add(ddldr);
			}
			else
			{
				myds.Tables.add(mydt);
			}
		}
		ddlTable.TableName = "UIBindKey";
		myds.Tables.add(ddlTable);

			///#endregion End把外键表加入DataSet


		return Json.ToJson(myds);
	}

		///#endregion


		///#region 综合查询
	/**
	 综合查询

	 @return
	*/
	public final String SearchZongHe_Init()
	{
		String key = this.GetRequestVal("Key");
		String dtFrom = this.GetRequestVal("DTFrom");
		String dtTo = this.GetRequestVal("DTTo");
		String flowNo = this.GetRequestVal("FlowNo");
		String wfState = this.GetRequestVal("WFState");
		Paras ps = new Paras();
		ps.SQL = "SELECT WorkID,FlowName,NodeName,StarterName,RDT,SendDT,WFState,Title,SDTOfNode FROM WF_GenerWorkFlow ";
		String dbstr = SystemConfig.getAppCenterDBVarStr();

		ps.SQL += "  WHERE (Starter=" + dbstr + "Starter OR ";
		if (Objects.equals(SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "?"))
		{
			if (SystemConfig.getAppCenterDBType() == DBType.MySQL)
			{
				ps.SQL += " Emps like CONCAT('%'," + dbstr + "Emps" + ",'%'))";
			}
			else
			{
				ps.SQL += " Emps like '%' +" + dbstr + "Emps" + "+'%')";
			}
		}
		else
		{
			ps.SQL += "  Emps like '%'||" + dbstr + "Emps" + "||'%')";
		}

		ps.Add("Starter", WebUser.getNo(), false);
		ps.Add("Emps", WebUser.getNo(), false);
		//如果关键字key.
		if (DataType.IsNullOrEmpty(key) == false)
		{
			if (Objects.equals(SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "?"))
			{
				if (SystemConfig.getAppCenterDBType() == DBType.MySQL)
				{
					ps.SQL += " AND Title like CONCAT('%'," + dbstr + "Title" + ",'%')";
				}
				else
				{
					ps.SQL += "  AND Title like '%' +" + dbstr + "Title" + "+'%'";
				}
			}
			else
			{
				ps.SQL += " AND Title like '%'||" + dbstr + "Title" + "||'%'";
			}
			ps.Add("Title", key, false);
		}

		//如果有日期从到.
		if (DataType.IsNullOrEmpty(dtFrom) == false)
		{
			ps.SQL += " AND ( RDT >= " + dbstr + "DtFrom AND RDT <=" + dbstr + "DtTo ) ";
			ps.Add("DtFrom", dtFrom, false);
			ps.Add("DtTo", dtTo, false);
		}


		//如果有流程编号.
		if (DataType.IsNullOrEmpty(flowNo) == false)
		{
			ps.SQL += " AND  FK_Flow= " + dbstr + "FK_Flow ";
			ps.Add("FK_Flow", flowNo, false);
		}

		//如果有流程状态.
		if (DataType.IsNullOrEmpty(wfState) == false && wfState.equals("all") == false)
		{
			ps.SQL += " AND  WFState= " + dbstr + "WFState ";
			ps.Add("WFState", wfState, false);
		}
		else
		{
			ps.SQL += " AND  WFState >1 ";
		}

		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "WorkID";
			dt.Columns.get(1).ColumnName = "FlowName";
			dt.Columns.get(2).ColumnName = "NodeName";
			dt.Columns.get(3).ColumnName = "StarterName";
			dt.Columns.get(4).ColumnName = "RDT";
			dt.Columns.get(5).ColumnName = "SendDT";
			dt.Columns.get(6).ColumnName = "WFState";
			dt.Columns.get(7).ColumnName = "Title";
			dt.Columns.get(8).ColumnName = "SDTOfNode";
		}
		return Json.ToJson(dt);
	}

		///#endregion

	/**
	 流程数据

	 @return
	*/
	public final String Watchdog_Init()
	{
		String sql = " SELECT FK_Flow,FlowName, COUNT(workid) as Num FROM V_MyFlowData WHERE MyEmpNo='" + WebUser.getNo() + "' ";
		sql += " GROUP BY  FK_Flow,FlowName ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Group";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "FK_Flow";
			dt.Columns.get(1).ColumnName = "FlowName";
			dt.Columns.get(2).ColumnName = "Num";
		}
		return Json.ToJson(dt);
	}
	/**
	 流程数据初始化

	 @return
	*/
	public final String Watchdog_InitFlows()
	{
		String sql = " SELECT *  FROM V_MyFlowData WHERE MyEmpNo='" + WebUser.getNo() + "' AND FK_Flow='" + this.getFlowNo() + "'";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Flows";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			String columnName = "";
			for (DataColumn col : dt.Columns)
			{
				columnName = col.ColumnName.toUpperCase();
				switch (columnName)
				{
					case "WORKID":
						col.ColumnName = "WorkID";
						break;
					case "FID":
						col.ColumnName = "FID";
						break;
					case "FK_FLOWSORT":
						col.ColumnName = "FK_FlowSort";
						break;
					case "SYSTYPE":
						col.ColumnName = "SysType";
						break;
					case "FK_FLOW":
						col.ColumnName = "FK_Flow";
						break;
					case "FLOWNAME":
						col.ColumnName = "FlowName";
						break;
					case "TITLE":
						col.ColumnName = "Title";
						break;
					case "WFSTA":
						col.ColumnName = "WFSta";
						break;
					case "WFSTATE":
						col.ColumnName = "WFState";
						break;
					case "STARTER":
						col.ColumnName = "Starter";
						break;
					case "STARTERNAME":
						col.ColumnName = "StarterName";
						break;
					case "RDT":
						col.ColumnName = "RDT";
						break;
					case "PFLOWNO":
						col.ColumnName = "PFlowNo";
						break;
					case "PWORKID":
						col.ColumnName = "PWorkID";
						break;
					case "PNODEID":
						col.ColumnName = "PNodeID";
						break;
					case "TODOEMPS":
						col.ColumnName = "TodoEmps";
						break;
					case "EMPS":
						col.ColumnName = "Emps";
						break;
					case "BILLNO":
						col.ColumnName = "BillNo";
						break;
					case "SENDDT":
						col.ColumnName = "SendDT";
						break;
					case "FK_NODE":
						col.ColumnName = "FK_Node";
						break;
					case "NODENAME":
						col.ColumnName = "NodeName";
						break;
					case "FK_DEPT":
						col.ColumnName = "FK_Dept";
						break;
					case "DEPTNAME":
						col.ColumnName = "DeptName";
						break;
					case "PRI":
						col.ColumnName = "PRI";
						break;
					case "SDTOFNODE":
						col.ColumnName = "SDTOfNode";
						break;

				}

			}
		}
		return Json.ToJson(dt);
	}
	/**
	 构造函数
	*/
	public WF()
	{

	}
	/**
	 为宁海特殊处理

	 @return
	*/
	public final String GoToMyFlow_Init() throws Exception {
		String userNo = this.GetRequestVal("UserNo");
		Dev2Interface.Port_Login(userNo);
		return "登录成功";
	}
	@Override
	protected String DoDefaultMethod() throws Exception {
		return super.DoDefaultMethod();
	}
	public final String HasSealPic() throws Exception {
		String no = GetRequestVal("No");
		if ((no == null || no.isEmpty()))
		{
			return "";
		}

		String path = "/DataUser/Siganture/" + no + ".jpg";
		//如果文件存在

		if ((new File(SystemConfig.getPathOfWebApp() + (path))).isFile() == false)
		{
			path = "/DataUser/Siganture/" + no + ".JPG";
			if ((new File(SystemConfig.getPathOfWebApp() + (path))).isFile() == true)
			{
				return "";
			}

			//如果不存在，就返回名称
			bp.port.Emp emp = new bp.port.Emp(no);
			return emp.getName();
		}
		return "";
	}
	/**
	 执行的方法.

	 @return
	*/
	public final String Do_Init()
	{
		String at = this.GetRequestVal("ActionType");
		if (DataType.IsNullOrEmpty(at))
		{
			at = this.GetRequestVal("DoType");
		}

		if (DataType.IsNullOrEmpty(at) && this.getSID() != null)
		{
			at = "Track";
		}
		String sid = this.getSID();
		try
		{
			switch (at)
			{

				case "Focus": //把任务放入任务池.
					Dev2Interface.Flow_Focus(this.getWorkID());
					return "info@Close";
				case "PutOne": //把任务放入任务池.
					Dev2Interface.Node_TaskPoolPutOne(this.getWorkID());
					return "info@Close";
				case "DoAppTask": // 申请任务.
					Dev2Interface.Node_TaskPoolTakebackOne(this.getWorkID());
					return "info@Close";
				case "DoOpenCC":
				case "WFRpt":
					String Sta = this.GetRequestVal("Sta");
					if (Objects.equals(Sta, "0"))
					{
						CCList cc1 = new CCList();
						cc1.setMyPK(this.getMyPK());
						cc1.Retrieve();
						cc1.setHisSta(CCSta.Read);
						cc1.Update();
					}

					if (DataType.IsNullOrEmpty(sid) == false)
					{
						String[] strss = sid.split("[_]", -1);
						GenerWorkFlow gwfl = new GenerWorkFlow(Long.parseLong(strss[1]));
						return "url@./MyCC.htm?WorkID=" + gwfl.getWorkID() + "&FK_Node=" + gwfl.getNodeID() + "&FK_Flow=" + gwfl.getFlowNo() + "&FID=" + gwfl.getFID() + "&CCSta=1";
					}
					return "url@./MyCC.htm?WorkID=" + this.getWorkID() + "&FK_Node=" + this.getNodeID() + "&FK_Flow=" + this.getFlowNo() + "&FID=" + this.getFID() + "&CCSta=1";
				case "DelCC": //删除抄送.
					CCList cc = new CCList();
					cc.setMyPK(this.getMyPK());
					cc.Retrieve();
					cc.setHisSta(CCSta.Del);
					cc.Update();
					return "info@Close";
				case "DelSubFlow": //删除进程。
					try
					{
						Dev2Interface.Flow_DeleteSubThread(this.getWorkID(), "手工删除");
						return "info@Close";
					}
					catch (RuntimeException ex)
					{
						return "err@" + ex.getMessage();
					}
				case "DelDtl":
					GEDtls dtls = new GEDtls(this.getEnsName());
					GEDtl dtl = (GEDtl)dtls.getNewEntity();
					dtl.setOID(this.getRefOID());
					if (dtl.RetrieveFromDBSources() == 0)
					{
						return "info@Close";
					}
					FrmEvents fes = new FrmEvents(this.getEnsName()); //获得事件.

					// 处理删除前事件.
					try
					{
						fes.DoEventNode(EventListFrm.DtlRowDelBefore, dtl);
					}
					catch (RuntimeException ex)
					{
						return "err@" + ex.getMessage();
					}
					dtl.Delete();

					// 处理删除后事件.
					try
					{
						fes.DoEventNode(EventListFrm.DtlRowDelAfter, dtl);
					}
					catch (RuntimeException ex)
					{
						return "err@" + ex.getMessage();
					}
					return "info@Close";
				case "EmpDoUp":
					WFEmp ep = new WFEmp(this.GetRequestVal("RefNo"));
					ep.DoUp();

					WFEmps emps111 = new WFEmps();
					//  emps111.RemoveCache();
					emps111.RetrieveAll();
					return "info@Close";
				case "EmpDoDown":
					WFEmp ep1 = new WFEmp(this.GetRequestVal("RefNo"));
					ep1.DoDown();

					WFEmps emps11441 = new WFEmps();
					//  emps11441.RemoveCache();
					emps11441.RetrieveAll();
					return "info@Close";
				case "Track": //通过一个串来打开一个工作.
					String mySid = this.getSID(); // this.Request.QueryString["Token");
					String[] mystrs = mySid.split("[_]", -1);

					long myWorkID = Integer.parseInt(mystrs[1]);
					String fk_emp = mystrs[0];
					int fk_node = Integer.parseInt(mystrs[2]);
					Node mynd = new Node();
					mynd.setNodeID(fk_node);
					mynd.RetrieveFromDBSources();

					String fk_flow = mynd.getFlowNo();
					String myurl = "./WorkOpt/OneWork/OneWork.htm?CurrTab=Track&FK_Node=" + mynd.getNodeID()+ "&WorkID=" + myWorkID + "&FK_Flow=" + fk_flow;
					WebUser.SignInOfGener(new bp.port.Emp(fk_emp), "CH", false, false, null, null);

					return "url@" + myurl;
				case "OF": //通过一个串来打开一个工作.
					//sid 格式为: guid+"_"+workid+"_"+empNo+"_"+nodeID;
					String[] strs = sid.split("[_]", -1);

					String workID = strs[1];
					String empNo = strs[2];

					GenerWorkerList wl = new GenerWorkerList();
					int i = wl.Retrieve(GenerWorkerListAttr.FK_Emp, empNo, GenerWorkerListAttr.WorkID, workID, GenerWorkerListAttr.IsPass, 0);

					if (i == 0)
					{
						return "info@此工作已经被别人处理或者此流程已删除";
					}

					GenerWorkFlow gwf = new GenerWorkFlow(wl.getWorkID());

					//设置他的组织.
					WebUser.setOrgNo(gwf.getOrgNo());

					bp.port.Emp empOF = new bp.port.Emp(wl.getEmpNo());
					WebUser.SignInOfGener(empOF, "CH", false, false, null, null);
					String u = "MyFlow.htm?FK_Flow=" + wl.getFlowNo() + "&WorkID=" + wl.getWorkID() + "&FK_Node=" + wl.getNodeID() + "&FID=" + wl.getFID() + "&PWorkID=" + gwf.getPWorkID();

					return "url@" + u;
				case "ExitAuth":
					bp.port.Emp emp = new bp.port.Emp(this.getEmpNo());
					//首先退出，再进行登录
					WebUser.Exit();
					WebUser.SignInOfGener(emp, WebUser.getSysLang(), false, false, null, null);
					return "info@Close";

				case "UnSend": //执行撤消发送。
					String url = "./WorkOpt/UnSend.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo();
					return "url@" + url;
				case "SetBillState":
					break;
				case "WorkRpt":
					break;
				case "PrintBill":
					break;
				//删除流程中第一个节点的数据，包括待办工作
				case "DeleteFlow":
					//调用DoDeleteWorkFlowByReal方法
					WorkFlow wf = new WorkFlow(new Flow(this.getFlowNo()), this.getWorkID());
					wf.DoDeleteWorkFlowByReal(true);
					return "流程删除成功";
				case "DownFlowSearchExcel": //下载流程查询结果，转到下面的逻辑，不放在此try..catch..中
					break;
				case "DownFlowSearchToTmpExcel": //导出到模板
					break;
				default:
					throw new RuntimeException("没有判断的at标记:" + at);
			}
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
		//此处之所以再加一个switch，是因为在下载文件逻辑中，调用Response.End()方法，如果此方法放在try..catch..中，会报线程中止异常
		switch (at)
		{
			case "DownFlowSearchExcel":
				break;
			case "DownFlowSearchToTmpExcel": //导出到模板
				break;
		}
		return "";
	}

	/**
	 获取设置的PC端和移动端URL

	 @return
	*/
	public final String Do_PCAndMobileUrl()
	{
		Hashtable ht = new Hashtable();
		ht.put("PCUrl", SystemConfig.getHostURL());
		ht.put("MobileUrl", SystemConfig.getMobileURL());
		return Json.ToJson(ht);
	}

	/**
	 页面调整移动端OR手机端

	 @return
	*/
	public final String Do_Direct()
	{
		//获取地址
		//获取地址
		String baseUrl = this.GetRequestVal("DirectUrl");
		//移动端打开
		String userAgent = CommonUtils.getRequest().getHeader("user-agent");
		if (userAgent.indexOf("Android") != -1) {
			return SystemConfig.getMobileURL() + baseUrl;
		} else if (userAgent.indexOf("iPhone") != -1 || userAgent.indexOf("iPad") != -1) {
			return SystemConfig.getMobileURL() + baseUrl;
		} else {
			return SystemConfig.getHostURL() + baseUrl;
		}
	}


		///#region 我的关注流程.
	/**
	 我的关注流程

	 @return
	*/
	public final String Focus_Init() throws Exception {
		String flowNo = this.GetRequestVal("FK_Flow");
		String domainExt = this.GetRequestVal("DomainExt");

		int idx = 0;
		//获得关注的数据.
		DataTable dt = Dev2Interface.DB_Focus(flowNo, WebUser.getNo(), domainExt);
		SysEnums stas = new SysEnums("WFSta");
		String[] tempArr;
		for (DataRow dr : dt.Rows)
		{
			int wfsta = Integer.parseInt(dr.getValue("WFSta").toString());
			//edit by liuxc,2016-10-22,修复状态显示不正确问题
			Object tempVar = stas.GetEntityByKey(SysEnumAttr.IntKey, wfsta);
			String wfstaT = (tempVar instanceof SysEnum ? (SysEnum)tempVar : null).getLab();
			String currEmp = "";

			if (wfsta != WFSta.Complete.getValue())
			{
				//edit by liuxc,2016-10-24,未完成时，处理当前处理人，只显示处理人姓名
				for (String emp : dr.getValue("ToDoEmps").toString().split("[;]", -1))
				{
					tempArr = emp.split("[,]", -1);

					currEmp += tempArr.length > 1 ? tempArr[1] : tempArr[0] + ",";
				}

				currEmp = StringHelper.trimEnd(currEmp, ',');

				//currEmp = dr["ToDoEmps").toString();
				//currEmp = currEmp.TrimEnd(';');
			}
			dr.setValue("ToDoEmps", currEmp);
			dr.setValue("FlowNote", wfstaT);
			dr.setValue("AtPara", (wfsta == WFSta.Complete.getValue() ? StringHelper.trimEnd(StringHelper.trimStart(dr.getValue("Sender").toString(), '('), ')').split("[,]", -1)[1] : ""));
		}
		return Json.ToJson(dt);
	}
	/**
	 取消关注

	 @return
	*/
	public final String Focus_Delete() throws Exception {
		Dev2Interface.Flow_Focus(this.getWorkID());
		return "执行成功";
	}

		///#endregion 我的关注.

	/**
	 方法

	 @return
	*/
	public final String HandlerMapExt() throws Exception {
		WF_CCForm wf = new WF_CCForm();
		return wf.HandlerMapExt();
	}
	/**
	 最近发起的流程.

	 @return
	*/
	public final String StartEaryer_Init() throws Exception {
		//定义容器.
		DataSet ds = new DataSet();

		//获得能否发起的流程.
		String sql = "SELECT FK_Flow as No,FlowName as Name, FK_FlowSort,B.Name as FK_FlowSortText,B.DomainExt, COUNT(WorkID) as Num ";
		sql += " FROM WF_GenerWorkFlow A, WF_FlowSort B  ";
		sql += " WHERE Starter='" + WebUser.getNo() + "'  AND A.FK_FlowSort=B.No  ";
		if (DataType.IsNullOrEmpty(this.getFlowNo()) == false)
		{
			sql += " AND A.FK_Flow='" + this.getFlowNo() + "'";
		}
		sql += " GROUP BY FK_Flow, FlowName, FK_FlowSort, B.Name,B.DomainExt ";

		DataTable dtStart = DBAccess.RunSQLReturnTable(sql);
		dtStart.TableName = "Start";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dtStart.Columns.get(0).ColumnName = "No";
			dtStart.Columns.get(1).ColumnName = "Name";
			dtStart.Columns.get(2).ColumnName = "FK_FlowSort";
			dtStart.Columns.get(3).ColumnName = "FK_FlowSortText";
			dtStart.Columns.get(4).ColumnName = "DomainExt";
			dtStart.Columns.get(5).ColumnName = "Num";
		}
		ds.Tables.add(dtStart);

		DataTable dtSort = new DataTable("Sort");
		dtSort.Columns.Add("No", String.class);
		dtSort.Columns.Add("Name", String.class);
		dtSort.Columns.Add("DomainExt", String.class);

		String nos = "";
		for (DataRow dr : dtStart.Rows)
		{
			String no = dr.getValue("FK_FlowSort").toString();
			if (nos.contains(no) == true)
			{
				continue;
			}

			String name = dr.getValue("FK_FlowSortText").toString();
			String domainExt = dr.getValue("DomainExt").toString();

			nos += "," + no;

			DataRow mydr = dtSort.NewRow();
			mydr.setValue(0, no);
			mydr.setValue(1, name);
			mydr.setValue(2, domainExt);
			dtSort.Rows.add(mydr);
		}

		dtSort.TableName = "Sort";
		ds.Tables.add(dtSort);

		return Json.ToJson(ds);
	}
	public final String Start_CopyAsWorkID() throws Exception {
		long workid = Dev2Interface.Node_CreateBlankWork(this.getFlowNo(), null, this.getWorkID());
		return String.valueOf(workid);
	}
	/**
	 获得发起列表

	 @return
	*/
	public final String Start_Init() throws Exception {
		String json = "";

		WFEmp em = new WFEmp();
		String userNo = WebUser.getNo();
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			userNo = WebUser.getOrgNo() + "_" + WebUser.getNo();
		}
		em.setNo(userNo);
		if (DataType.IsNullOrEmpty(em.getNo()) == true)
		{
			return "err@登录信息丢失,请重新登录.";
		}

		if (em.RetrieveFromDBSources() == 0)
		{
			em.setDeptNo(WebUser.getDeptNo());
			em.setName(WebUser.getName());
			if(DataType.IsNullOrEmpty(WebUser.getOrgNo()) == false){
				em.setOrgNo(WebUser.getOrgNo());
			}
			em.Insert();
		}

		//json = DBAccess.GetBigTextFromDB("WF_Emp", "No", userNo, "StartFlows");
		if (DataType.IsNullOrEmpty(json) == false)
		{
			return json;
		}

		//定义容器.
		DataSet ds = new DataSet();

		//获得能否发起的流程.
		DataTable dtStart = Dev2Interface.DB_StarFlows(userNo);
		dtStart.TableName = "Start";
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dtStart.Columns.get("No").ColumnName = "No";
			dtStart.Columns.get("NAME").ColumnName = "Name";
			dtStart.Columns.get("ISBATCHSTART").ColumnName = "IsBatchStart";
			dtStart.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
			dtStart.Columns.get("FK_FLOWSORTTEXT").ColumnName = "FK_FlowSortText";
			dtStart.Columns.get("doMainExt").ColumnName = "DomainExt";
			dtStart.Columns.get("ISSTARTINMOBILE").ColumnName = "IsStartInMobile";
			dtStart.Columns.get("IDX").ColumnName = "Idx";
			dtStart.Columns.get("WORKMODEL").ColumnName = "WorkModel";

		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dtStart.Columns.get("No").ColumnName = "No";
			dtStart.Columns.get("NAME").ColumnName = "Name";
			dtStart.Columns.get("ISBATCHSTART").ColumnName = "IsBatchStart";
			dtStart.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
			dtStart.Columns.get("FK_FLOWSORTTEXT").ColumnName = "FK_FlowSortText";
			dtStart.Columns.get("doMainExt").ColumnName = "DomainExt";
			dtStart.Columns.get("ISSTARTINMOBILE").ColumnName = "IsStartInMobile";
			dtStart.Columns.get("IDX").ColumnName = "Idx";
			dtStart.Columns.get("WORKMODEL").ColumnName = "WorkModel";
		}
		ds.Tables.add(dtStart);


			///#region 动态构造 流程类别.
		DataTable dtSort = new DataTable("Sort");
		dtSort.Columns.Add("No", String.class);
		dtSort.Columns.Add("Name", String.class);
		dtSort.Columns.Add("DomainExt", String.class);
		dtSort.Columns.Add("MobileColorType", Integer.class);

		String nos = "";
		for (DataRow dr : dtStart.Rows)
		{
			String no = dr.getValue("FK_FlowSort").toString();
			if (nos.contains(no) == true)
			{
				continue;
			}

			String name = dr.getValue("FK_FlowSortText").toString();
			String domainExt = dr.getValue("DomainExt").toString();
			String mobileColorType = dr.getValue("MobileColorType").toString();

			nos += "," + no;

			DataRow mydr = dtSort.NewRow();
			mydr.setValue(0, no);
			mydr.setValue(1, name);
			mydr.setValue(2, domainExt);
			mydr.setValue(3, DataType.IsNullOrEmpty(mobileColorType)== true?0:Integer.parseInt(mobileColorType));
			dtSort.Rows.add(mydr);
		}

		dtSort.TableName = "Sort";
		ds.Tables.add(dtSort);

			///#endregion 动态构造 流程类别.

		//返回组合
		json = Json.ToJson(ds);

		//把json存入数据表，避免下一次再取.
		if (dtStart.Rows.size() > 0)
		{
			DBAccess.SaveBigTextToDB(json, "WF_Emp", "No", userNo, "StartFlows");
		}

		//返回组合
		return json;
	}
	/**
	 获得发起列表

	 @return
	*/
	public final String FlowSearch_Init() throws Exception {
		DataSet ds = new DataSet();

		//流程类别.
		FlowSorts fss = new FlowSorts();
		fss.RetrieveAll();

		DataTable dtSort = fss.ToDataTableField("Sort");
		dtSort.TableName = "Sort";
		ds.Tables.add(dtSort);

		//获得能否发起的流程.
		DataTable dtStart = DBAccess.RunSQLReturnTable("SELECT No,Name, FK_FlowSort FROM WF_Flow ORDER BY FK_FlowSort,Idx");
		dtStart.TableName = "Start";

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dtStart.Columns.get("NO").ColumnName = "No";
			dtStart.Columns.get("NAME").ColumnName = "Name";
			dtStart.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dtStart.Columns.get("no").ColumnName = "No";
			dtStart.Columns.get("name").ColumnName = "Name";
			dtStart.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";
		}
		ds.Tables.add(dtStart);
		//返回组合
		return Json.ToJson(ds);
	}

		///#region 获得列表.
	/**
	 运行
	 @return 运行中的流程
	*/
	public final String Runing_Init() throws Exception {
		DataTable dt = null;
		boolean isContainFuture = this.GetRequestValBoolen("IsContainFuture");
		dt = Dev2Interface.DB_GenerRuning(WebUser.getNo(), this.getFlowNo(), false, this.getDomainExt(), isContainFuture); //获得指定域的在途.
		return Json.ToJson(dt);
	}
	//近期工作
	public final String RecentWork_Init()
	{
		/* 近期工作. */
		String sql = "";
		String empNo = WebUser.getNo();
		sql += "SELECT  * FROM WF_GenerWorkFlow  WHERE ";
		sql += " (Emps LIKE '%@" + empNo + "@%' OR Emps LIKE '%@" + empNo + ",%' OR Emps LIKE '%," + empNo + "@%')";
		// sql += " AND Starter!='" + empNo + "'"; //不能是我发起的.

		if (DataType.IsNullOrEmpty(this.getFlowNo()) == false)
		{
			sql += " AND FK_Flow='" + this.getFlowNo() + "'"; //如果有流程编号,就按他过滤.
		}

		sql += " AND WFState >1 ORDER BY RDT DESC ";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		//添加oracle的处理
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("PRI").ColumnName = "PRI";
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("FID").ColumnName = "FID";
			dt.Columns.get("WFSTATE").ColumnName = "WFState";
			dt.Columns.get("WFSTA").ColumnName = "WFSta";
			dt.Columns.get("WEEKNUM").ColumnName = "WeekNum";
			dt.Columns.get("TSPAN").ColumnName = "TSpan";
			dt.Columns.get("TODOSTA").ColumnName = "TodoSta";
			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dt.Columns.get("TODOEMPSNUM").ColumnName = "TodoEmpsNum";
			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("TASKSTA").ColumnName = "TaskSta";
			dt.Columns.get("SYSTYPE").ColumnName = "SysType";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("SENDER").ColumnName = "Sender";
			dt.Columns.get("SENDDT").ColumnName = "SendDT";
			dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";
			dt.Columns.get("SDTOFFLOW").ColumnName = "SDTOfFlow";
			dt.Columns.get("RDT").ColumnName = "RDT";
			dt.Columns.get("PWORKID").ColumnName = "PWorkID";
			dt.Columns.get("PFLOWNO").ColumnName = "PFlowNo";
			dt.Columns.get("PFID").ColumnName = "PFID";
			dt.Columns.get("PEMP").ColumnName = "PEmp";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";

			dt.Columns.get("GUID").ColumnName = "Guid";
			dt.Columns.get("GUESTNO").ColumnName = "GuestNo";
			dt.Columns.get("GUESTNAME").ColumnName = "GuestName";
			dt.Columns.get("FLOWNOTE").ColumnName = "FlowNote";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("FK_NY").ColumnName = "FK_NY";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FK_DEPT").ColumnName = "FK_Dept";
			dt.Columns.get("EMPS").ColumnName = "Emps";
			if(dt.Columns.contains("DOMAIN"))
				dt.Columns.get("DOMAIN").ColumnName = "Domain";
			if (dt.Columns.contains("DOMAINEXT"))
				dt.Columns.get("DOMAINEXT").ColumnName = "DomainExt";
			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dt.Columns.get("BILLNO").ColumnName = "BillNo";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("pri").ColumnName = "PRI";
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("fid").ColumnName = "FID";
			dt.Columns.get("wfstate").ColumnName = "WFState";
			dt.Columns.get("wfsta").ColumnName = "WFSta";
			dt.Columns.get("weeknum").ColumnName = "WeekNum";
			dt.Columns.get("tspan").ColumnName = "TSpan";
			dt.Columns.get("todosta").ColumnName = "TodoSta";
			dt.Columns.get("deptname").ColumnName = "DeptName";
			dt.Columns.get("todoempsnum").ColumnName = "TodoEmpsNum";
			dt.Columns.get("todoemps").ColumnName = "TodoEmps";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("tasksta").ColumnName = "TaskSta";
			dt.Columns.get("systype").ColumnName = "SysType";
			dt.Columns.get("startername").ColumnName = "StarterName";
			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("sender").ColumnName = "Sender";
			dt.Columns.get("senddt").ColumnName = "SendDT";
			dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";
			dt.Columns.get("sdtofflow").ColumnName = "SDTOfFlow";
			dt.Columns.get("rdt").ColumnName = "RDT";
			dt.Columns.get("pworkid").ColumnName = "PWorkID";
			dt.Columns.get("pflowno").ColumnName = "PFlowNo";
			dt.Columns.get("pfid").ColumnName = "PFID";
			dt.Columns.get("pemp").ColumnName = "PEmp";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("guid").ColumnName = "Guid";
			dt.Columns.get("guestno").ColumnName = "GuestNo";
			dt.Columns.get("guestname").ColumnName = "GuestName";
			dt.Columns.get("flownote").ColumnName = "FlowNote";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("fk_ny").ColumnName = "FK_NY";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			dt.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("fk_dept").ColumnName = "FK_Dept";
			dt.Columns.get("emps").ColumnName = "Emps";
			if (dt.Columns.contains("domain"))
				dt.Columns.get("domain").ColumnName = "Domain";
			if (dt.Columns.contains("domainext"))
				dt.Columns.get("domainext").ColumnName = "DomainExt";
			dt.Columns.get("deptname").ColumnName = "DeptName";
			dt.Columns.get("billno").ColumnName = "BillNo";
		}
		return Json.ToJson(dt);
	}

	/**
	 我参与的已经完成的工作.

	 @return
	*/
	public final String Complete_Init()
	{
		/* 如果不是删除流程注册表. */
		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		String flowNo = this.GetRequestVal("FK_Flow");
		if(DataType.IsNullOrEmpty(flowNo) == false)
		{
			//如果存在flowNo表示中间件模式的分流程查询接口
			ps.SQL = "SELECT  * FROM WF_GenerWorkFlow  WHERE (Emps LIKE '%@" + WebUser.getNo() + "@%' OR Emps LIKE '%@" + WebUser.getNo() + ",%' OR Emps LIKE '%," + WebUser.getNo() + "@%') AND (WFState=3 OR WFState=11) AND FK_Flow=" + dbstr + "FK_Flow ORDER BY  RDT DESC";
			ps.Add("FK_Flow", flowNo);
		}
		else
		{
			ps.SQL = "SELECT  * FROM WF_GenerWorkFlow  WHERE (Emps LIKE '%@" + WebUser.getNo() + "@%' OR Emps LIKE '%@" + WebUser.getNo() + ",%' OR Emps LIKE '%," + WebUser.getNo() + "@%') AND (WFState=3 OR WFState=11) ORDER BY  RDT DESC";
		}
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		//添加oracle的处理
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("PRI").ColumnName = "PRI";
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("FID").ColumnName = "FID";
			dt.Columns.get("WFSTATE").ColumnName = "WFState";
			dt.Columns.get("WFSTA").ColumnName = "WFSta";
			dt.Columns.get("WEEKNUM").ColumnName = "WeekNum";
			dt.Columns.get("TSPAN").ColumnName = "TSpan";
			dt.Columns.get("TODOSTA").ColumnName = "TodoSta";
			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dt.Columns.get("TODOEMPSNUM").ColumnName = "TodoEmpsNum";
			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("TASKSTA").ColumnName = "TaskSta";
			dt.Columns.get("SYSTYPE").ColumnName = "SysType";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("SENDER").ColumnName = "Sender";
			dt.Columns.get("SENDDT").ColumnName = "SendDT";
			dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";
			dt.Columns.get("SDTOFFLOW").ColumnName = "SDTOfFlow";
			dt.Columns.get("RDT").ColumnName = "RDT";
			dt.Columns.get("PWORKID").ColumnName = "PWorkID";
			dt.Columns.get("PFLOWNO").ColumnName = "PFlowNo";
			dt.Columns.get("PFID").ColumnName = "PFID";
			dt.Columns.get("PEMP").ColumnName = "PEmp";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";


			dt.Columns.get("GUID").ColumnName = "Guid";
			dt.Columns.get("GUESTNO").ColumnName = "GuestNo";
			dt.Columns.get("GUESTNAME").ColumnName = "GuestName";
			dt.Columns.get("FLOWNOTE").ColumnName = "FlowNote";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
			dt.Columns.get("FK_NY").ColumnName = "FK_NY";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FK_DEPT").ColumnName = "FK_Dept";
			dt.Columns.get("EMPS").ColumnName = "Emps";
			if(dt.Columns.contains("DOMAIN"))
				dt.Columns.get("DOMAIN").ColumnName = "Domain";
			if (dt.Columns.contains("DOMAINEXT"))
				dt.Columns.get("DOMAINEXT").ColumnName = "DomainExt";
			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dt.Columns.get("BILLNO").ColumnName = "BillNo";
			dt.Columns.get("ATPARA").ColumnName = "AtPara";
			dt.Columns.get("HUNGUPTIME").ColumnName = "HungupTime";
			dt.Columns.get("ORGNO").ColumnName = "OrgNo";
			dt.Columns.get("PNODEID").ColumnName = "PNodeID";
			dt.Columns.get("PRJNAME").ColumnName = "PrjName";
			dt.Columns.get("PRJNO").ColumnName = "PrjNo";
			dt.Columns.get("SDTOFFLOWWARNING").ColumnName = "SDTOfFlowWarning";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("pri").ColumnName = "PRI";
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("fid").ColumnName = "FID";
			dt.Columns.get("wfstate").ColumnName = "WFState";
			dt.Columns.get("wfsta").ColumnName = "WFSta";
			dt.Columns.get("weeknum").ColumnName = "WeekNum";
			dt.Columns.get("tspan").ColumnName = "TSpan";
			dt.Columns.get("todosta").ColumnName = "TodoSta";
			dt.Columns.get("deptname").ColumnName = "DeptName";
			dt.Columns.get("todoempsnum").ColumnName = "TodoEmpsNum";
			dt.Columns.get("todoemps").ColumnName = "TodoEmps";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("tasksta").ColumnName = "TaskSta";
			dt.Columns.get("systype").ColumnName = "SysType";
			dt.Columns.get("startername").ColumnName = "StarterName";
			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("sender").ColumnName = "Sender";
			dt.Columns.get("senddt").ColumnName = "SendDT";
			dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";
			dt.Columns.get("sdtofflow").ColumnName = "SDTOfFlow";
			dt.Columns.get("rdt").ColumnName = "RDT";
			dt.Columns.get("pworkid").ColumnName = "PWorkID";
			dt.Columns.get("pflowno").ColumnName = "PFlowNo";
			dt.Columns.get("pfid").ColumnName = "PFID";
			dt.Columns.get("pemp").ColumnName = "PEmp";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("guid").ColumnName = "Guid";
			dt.Columns.get("guestno").ColumnName = "GuestNo";
			dt.Columns.get("guestname").ColumnName = "GuestName";
			dt.Columns.get("flownote").ColumnName = "FlowNote";
			dt.Columns.get("flowname").ColumnName = "FlowName";
			dt.Columns.get("fk_ny").ColumnName = "FK_NY";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			dt.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("fk_dept").ColumnName = "FK_Dept";
			dt.Columns.get("emps").ColumnName = "Emps";
			if (dt.Columns.contains("domain"))
				dt.Columns.get("domain").ColumnName = "Domain";
			if (dt.Columns.contains("domainext"))
				dt.Columns.get("domainext").ColumnName = "DomainExt";
			dt.Columns.get("deptname").ColumnName = "DeptName";
			dt.Columns.get("billno").ColumnName = "BillNo";
			dt.Columns.get("atpara").ColumnName = "AtPara";
			dt.Columns.get("hunguptime").ColumnName = "HungupTime";
			dt.Columns.get("orgno").ColumnName = "OrgNo";
			dt.Columns.get("pnodeid").ColumnName = "PNodeID";
			dt.Columns.get("prjname").ColumnName = "PrjName";
			dt.Columns.get("prjno").ColumnName = "PrjNo";
			dt.Columns.get("sdtofflowwarning").ColumnName = "SDTOfFlowWarning";

		}

		return Json.ToJson(dt);
	}

	/**
	 执行撤销

	 @return
	*/
	public final String Runing_UnSend()
	{
		try
		{
			//获取撤销到的节点
			int unSendToNode = this.GetRequestValInt("UnSendToNode");
			return Dev2Interface.Flow_DoUnSend(this.getFlowNo(), this.getWorkID(), unSendToNode, this.getFID());
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/**
	 撤销发送

	 @return
	*/
	public final String Runing_UnSendCC() throws Exception {
		String checkboxs = GetRequestVal("CCPKs");
		CCLists ccs = new CCLists();
		ccs.RetrieveIn("MyPK", "'" + checkboxs.replace(",", "','") + "'", null);
		ccs.Delete();
		return "撤销抄送成功";
	}
	/**
	 执行催办

	 @return
	*/
	public final String Runing_Press()
	{
		try
		{
			return Dev2Interface.Flow_DoPress(this.getWorkID(), this.GetRequestVal("Msg"), false);
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/**
	 打开表单

	 @return
	*/
	public final String Runing_OpenFrm() throws Exception {
		int nodeID = this.getNodeID();
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		if (nodeID == 0)
		{
			gwf = new GenerWorkFlow(this.getWorkID());
			nodeID = gwf.getNodeID();
		}

		String appPath = Glo.getCCFlowAppPath();
		Node nd = null;
		Track tk = new Track();
		tk.FlowNo=this.getFlowNo();


		tk.setWorkID(this.getWorkID());
		if (this.getMyPK() != null)
		{
			tk = new Track(this.getFlowNo(), this.getMyPK());
			nd = new Node(tk.getNDFrom());
		}
		else
		{
			nd = new Node(nodeID);
		}

		Flow fl = new Flow(this.getFlowNo());
		long workid = 0;
		if (nd.getItIsSubThread() == true)
		{
			if (tk.getFID() == 0)
			{
				if (gwf == null)
				{
					gwf = new GenerWorkFlow(this.getWorkID());
				}

				workid = gwf.getFID();
			}
			else
			{
				workid = tk.getFID();
			}
		}
		else
		{
			workid = tk.getWorkID();
		}

		long fid = this.getFID();
		if (this.getFID() == 0)
		{
			fid = tk.getFID();
		}

		if (fid > 0)
		{
			workid = fid;
		}

		if (workid == 0)
		{
			workid = this.getWorkID();
		}

		String urlExt = "";

		// gwf.atPara.getHisHT()

		DataTable ndrpt = DBAccess.RunSQLReturnTable("SELECT PFlowNo,PWorkID FROM " + fl.getPTable() + " WHERE OID=" + workid);
		if (ndrpt.Rows.size() == 0)
		{
			urlExt = "&PFlowNo=0&PWorkID=0&IsToobar=0&IsHidden=true&CCSta=" + this.GetRequestValInt("CCSta");
		}
		else
		{
			urlExt = "&PFlowNo=" + ndrpt.Rows.get(0).getValue("PFlowNo") + "&PWorkID=" + ndrpt.Rows.get(0).getValue("PWorkID") + "&IsToobar=0&IsHidden=true&CCSta=" + this.GetRequestValInt("CCSta");
		}

		urlExt += "&From=CCFlow&TruckKey=" + tk.GetValStrByKey("MyPK") + "&DoType=" + this.getDoType() + "&UserNo=" + WebUser.getNo() != null ? WebUser.getNo() : "" + "&Token=" + WebUser.getToken() != null ? WebUser.getToken() : "";

		urlExt = urlExt.replace("PFlowNo=null", "");
		urlExt = urlExt.replace("PWorkID=null", "");

		if (!gwf.getatPara().getHisHT().isEmpty())
		{
			for (String item : gwf.getatPara().getHisHT().keySet())
			{
				urlExt += "&" + item + "=" + gwf.getatPara().getHisHT().get(item);
			}
		}


		if (nd.getHisFormType() == NodeFormType.SDKForm || nd.getHisFormType() == NodeFormType.SelfForm)
		{
			//added by liuxc,2016-01-25
			if (nd.getFormUrl().contains("?"))
			{
				return "urlForm@" + nd.getFormUrl() + "&IsReadonly=1&WorkID=" + workid + "&FK_Node=" + nd.getNodeID() + "&FK_Flow=" + nd.getFlowNo() + "&FID=" + fid + urlExt;
			}

			return "urlForm@" + nd.getFormUrl() + "?IsReadonly=1&WorkID=" + workid + "&FK_Node=" + nd.getNodeID() + "&FK_Flow=" + nd.getFlowNo() + "&FID=" + fid + urlExt;
		}

		if (nd.getHisFormType() == NodeFormType.SheetTree || nd.getHisFormType() == NodeFormType.SheetAutoTree)
		{
			if (Glo.getPlatform() == Platform.CCFlow)
			{
				return "url@/WF/MyViewTree.htm?3=4&WorkID=" + this.getWorkID() + "&FID=" + this.getFID() + "&OID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + nd.getNodeID() + "&PK=OID&PKVal=" + this.getWorkID() + "&IsEdit=0&IsLoadData=0&IsReadonly=1" + urlExt;
			}
			else
			{
				return "url@/jflow-web/MyViewTree.htm?3=4&WorkID=" + this.getWorkID() + "&FID=" + this.getFID() + "&OID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + nd.getNodeID() + "&PK=OID&PKVal=" + this.getWorkID() + "&IsEdit=0&IsLoadData=0&IsReadonly=1" + urlExt;
			}
		}

		Work wk = nd.getHisWork();
		wk.setOID(workid);
		if (wk.RetrieveFromDBSources() == 0)
		{
			GERpt rtp = nd.getHisFlow().getHisGERpt();
			rtp.setOID(workid);
			if (rtp.RetrieveFromDBSources() == 0)
			{
				String info = "打开(" + nd.getName() + ")错误";
				info += "当前的节点数据已经被删除！！！<br> 造成此问题出现的原因如下。";
				info += "1、当前节点数据被非法删除。";
				info += "2、节点数据是退回人与被退回人中间的节点，这部分节点数据查看不支持。";
				info += "技术信息:表" + wk.getEnMap().getPhysicsTable() + " WorkID=" + workid;
				return "err@" + info;
			}
			wk.setRow(rtp.getRow());
		}

		if (nd.getHisFlow().getItIsMD5() && wk.ItIsPassCheckMD5() == false)
		{
			String err = "打开(" + nd.getName() + ")错误";
			err += "当前的节点数据已经被篡改，请报告管理员。";
			return "err@" + err;
		}
		nd.WorkID=this.getWorkID(); //为获取表单ID ( NodeFrmID )提供参数.
		if (nd.getHisFormType() == NodeFormType.Develop)
		{
			MapData md = new MapData(nd.getNodeFrmID());
			if (md.getHisFrmType() != FrmType.Develop)
			{
				md.setHisFrmType(FrmType.Develop);
				md.Update();
			}
		}
		else if (nd.getHisFormType() == NodeFormType.FoolForm)
		{
			nd.WorkID=this.getWorkID(); //为获取表单ID ( NodeFrmID )提供参数.
			MapData md = new MapData(nd.getNodeFrmID());
			if (md.getHisFrmType() != FrmType.FoolForm)
			{
				md.setHisFrmType(FrmType.FoolForm);
				md.Update();
			}
		}
		String endUrl = "";

		if (!gwf.getatPara().getHisHT().isEmpty())
		{
			for (String item : gwf.getatPara().getHisHT().keySet())
			{
				endUrl += "&" + item + "=" + gwf.getatPara().getHisHT().get(item);
			}
		}

		//加入是累加表单的标志，目的是让附件可以看到.

		if (nd.getHisFormType() == NodeFormType.FoolTruck)
		{
			endUrl = "&FormType=10&FromWorkOpt=" + this.GetRequestVal("FromWorkOpt");
		}

		//return "url@./CCForm/Frm.htm?FK_MapData=" + nd.NodeFrmID + "&OID=" + wk.OID + "&FK_Flow=" + this.FlowNo + "&FK_Node=" + nd.getNodeID() + "&PK=OID&PKVal=" + wk.OID + "&IsEdit=0&IsLoadData=0&IsReadonly=1" + endUrl+"&CCSta="+this.GetRequestValInt("CCSta");

		if (Glo.getPlatform() == Platform.CCFlow)
		{
			return "url@/WF/MyView.htm?FK_MapData=" + nd.getNodeFrmID() + "&OID=" + wk.getOID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + nd.getNodeID() + "&PK=OID&PKVal=" + wk.getOID() + "&IsEdit=0&IsLoadData=0&IsReadonly=1" + endUrl + "&CCSta=" + this.GetRequestValInt("CCSta");
		}
		else
		{
			return "url@/jflow-web/WF/MyView.htm?FK_MapData=" + nd.getNodeFrmID()+ "&OID=" + wk.getOID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + nd.getNodeID() + "&PK=OID&PKVal=" + wk.getOID() + "&IsEdit=0&IsLoadData=0&IsReadonly=1" + endUrl + "&CCSta=" + this.GetRequestValInt("CCSta");
		}
	}
	/**
	 草稿

	 @return
	*/
	public final String Draft_Init()
	{
		DataTable dt = null;
		String domainExt = this.GetRequestVal("DomainExt");
		String flowNo = this.GetRequestVal("FK_Flow");

		dt = Dev2Interface.DB_GenerDraftDataTable(flowNo, domainExt);
		return Json.ToJson(dt);
	}
	/**
	 删除草稿.

	 @return
	*/
	public final String Draft_Delete() throws Exception {
		return Dev2Interface.Flow_DoDeleteDraft(this.getFlowNo(), this.getWorkID(), false);
	}
	/**
	 获得会签列表

	 @return
	*/
	public final String HuiQianList_Init()
	{
		String sql = "SELECT A.WorkID, A.Title,A.FK_Flow, A.FlowName, A.Starter, A.StarterName, A.Sender, A.Sender,A.FK_Node,A.NodeName,A.SDTOfNode,A.TodoEmps";
		sql += " FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B ";
		sql += " WHERE A.WorkID=B.WorkID and a.FK_Node=b.FK_Node ";
		sql += " AND (B.IsPass=90 OR A.AtPara LIKE '%HuiQianZhuChiRen=" + WebUser.getNo() + "%') ";
		sql += " AND B.FK_Emp=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp";

		Paras ps = new Paras();
		ps.Add("FK_Emp", WebUser.getNo(), false);
		ps.SQL = sql;
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";

			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";

			dt.Columns.get("SENDER").ColumnName = "Sender";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";
			dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";
			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";

			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("startername").ColumnName = "StarterName";

			dt.Columns.get("sender").ColumnName = "Sender";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";
			dt.Columns.get("todoemps").ColumnName = "TodoEmps";
		}
		return Json.ToJson(dt);
	}
	/**
	 协作模式待办

	 @return
	*/
	public final String TeamupList_Init()
	{
		String sql = "SELECT A.WorkID, A.Title,A.FK_Flow, A.FlowName, A.Starter, A.StarterName, A.Sender, A.Sender,A.FK_Node,A.NodeName,A.SDTOfNode,A.TodoEmps";
		sql += " FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B, WF_Node C ";
		sql += " WHERE A.WorkID=B.WorkID and a.FK_Node=b.FK_Node AND A.FK_Node=C.NodeID AND C.TodolistModel=1 ";
		sql += " AND B.IsPass=0 AND B.FK_Emp=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp";
		//   sql += " AND B.IsPass=0 AND B.FK_Emp=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "FK_Emp";

		Paras ps = new Paras();
		ps.Add("FK_Emp", WebUser.getNo(), false);
		ps.SQL = sql;
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";

			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";

			dt.Columns.get("SENDER").ColumnName = "Sender";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";
			dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";
			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";

			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("startername").ColumnName = "StarterName";

			dt.Columns.get("sender").ColumnName = "Sender";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";
			dt.Columns.get("todoemps").ColumnName = "TodoEmps";
		}
		return Json.ToJson(dt);
	}
	/**
	 获得加签人的待办
	 @LQ

	 @return
	*/
	public final String HuiQianAdderList_Init()
	{
		String sql = "SELECT A.WorkID, A.Title,A.FK_Flow, A.FlowName, A.Starter, A.StarterName, A.Sender, A.Sender,A.FK_Node,A.NodeName,A.SDTOfNode,A.TodoEmps";
		sql += " FROM WF_GenerWorkFlow A, WF_GenerWorkerlist B, WF_Node C ";
		sql += " WHERE A.WorkID=B.WorkID and a.FK_Node=b.FK_Node AND B.IsPass=0 AND B.FK_Emp=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp";
		sql += " AND B.AtPara LIKE '%IsHuiQian=1%' ";
		sql += " AND A.FK_Node=C.NodeID ";
		sql += " AND C.TodolistModel= 4";

		Paras ps = new Paras();
		ps.Add("FK_Emp", WebUser.getNo(), false);
		ps.SQL = sql;
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
			dt.Columns.get("FLOWNAME").ColumnName = "FlowName";

			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";

			dt.Columns.get("SENDER").ColumnName = "Sender";
			dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
			dt.Columns.get("NODENAME").ColumnName = "NodeName";
			dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";
			dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
			dt.Columns.get("flowname").ColumnName = "FlowName";

			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("startername").ColumnName = "StarterName";

			dt.Columns.get("sender").ColumnName = "Sender";
			dt.Columns.get("fk_node").ColumnName = "FK_Node";
			dt.Columns.get("nodename").ColumnName = "NodeName";
			dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";
			dt.Columns.get("todoemps").ColumnName = "TodoEmps";
		}
		return Json.ToJson(dt);
	}
	/**
	 初始化待办.

	 @return
	*/
	public final String Todolist_Init() throws Exception {
		String wfState = this.GetRequestVal("ShowWhat"); //比如：WFSTate=1,状态.
		String orderBy = this.GetRequestVal("OrderBy");
		String isRead = this.GetRequestVal("IsRead");
		DataTable dt = Dev2Interface.DB_GenerEmpWorksOfDataTable(WebUser.getNo(), this.getNodeID(), wfState, this.getDomainExt(), this.getFlowNo(), isRead, orderBy);
		return Json.ToJson(dt);
	}
	public final String Todolist_ABC()
	{
		// BP.WF.Dev2Interface.
		return "xxxx";
	}
	/**
	 逾期工作

	 @return
	*/
	public final String Timeout_Init()
	{
		//String wfState = this.GetRequestVal("ShowWhat"); //比如：WFSTate=1,状态.
		//String orderBy = this.GetRequestVal("OrderBy");
		DataTable dt = Dev2Interface.DB_Timeout();
		return Json.ToJson(dt);
	}
	/**
	 近期发起

	 @return
	*/
	public final String RecentStart_Init() throws Exception {
		DataTable dt = Dev2Interface.DB_RecentStart(this.getFlowNo());
		return Json.ToJson(dt);
	}
	/**
	 获得授权人的待办.

	 @return
	*/
	public final String Todolist_Author() throws Exception {
		DataTable dt = null;
		dt = Dev2Interface.DB_GenerEmpWorksOfDataTable(this.getNo(), this.getNodeID());

		//转化大写的toJson.
		return Json.ToJson(dt);
	}
	/**
	 初始化

	 @return
	*/
	public final String TodolistOfAuth_Init()
	{
		return "err@尚未重构完成.";
	}
	/**
	 获得挂起列表

	 @return
	*/
	public final String HungupList_Init() throws Exception {
		return Dev2Interface.DB_GenerHungupList();
	}
	/**
	 同意挂起

	 @return
	*/
	public final String HungupList_Agree() throws Exception {
		return Dev2Interface.Node_HungupWorkAgree(this.getWorkID());
	}
	/**
	 拒绝挂起

	 @return
	*/
	public final String HungupList_Reject() throws Exception {
		return Dev2Interface.Node_HungupWorkReject(this.getWorkID(), this.GetRequestVal("Msg"));
	}

	public final String FutureTodolist_Init()
	{
		DataTable dt = null;
		dt = Dev2Interface.DB_FutureTodolist();

		//转化大写的toJson.
		return Json.ToJson(dt);
	}



		///#endregion 获得列表.



		///#region 共享任务池.
	/**
	 初始化共享任务

	 @return
	*/
	public final String TaskPoolSharing_Init()
	{
		DataTable dt = Dev2Interface.DB_TaskPool();

		return Json.ToJson(dt);
	}
	/**
	 申请任务.

	 @return
	*/
	public final String TaskPoolSharing_Apply() throws Exception {
		boolean b = Dev2Interface.Node_TaskPoolTakebackOne(this.getWorkID());
		if (b == true)
		{
			return "申请成功.";
		}
		else
		{
			return "err@申请失败...";
		}
	}
	/**
	 我申请下来的任务

	 @return
	*/
	public final String TaskPoolApply_Init()
	{
		DataTable dt = Dev2Interface.DB_TaskPoolOfMyApply();

		return Json.ToJson(dt);
	}
	public final String TaskPoolApply_PutOne() throws Exception {
		Dev2Interface.Node_TaskPoolPutOne(this.getWorkID());
		return "放入成功,其他的同事可以看到这件工作.您可以在任务池里看到它并重新申请下来.";
	}

		///#endregion


		///#region 登录相关.
	/**
	 返回当前会话信息.
	 1. token 用于ccflow内部访问.
	 2. sid 用于集成.

	 @return
	*/
	public final String Login_Init() throws Exception {

			///#region 检查一下是否有 token、sid ? 如果有就直接登录.
		String token = this.GetRequestVal("Token");
		if (DataType.IsNullOrEmpty(token) == false)
		{
			Dev2Interface.Port_LoginByToken(token);
			return "url@Home.htm?Token=" + token;
		}

			///#endregion 检查一下是否有token ?

		Hashtable ht = new Hashtable();
		if (WebUser.getNoOfRel() == null)
		{
			ht.put("UserNo", "");
		}
		else
		{
			ht.put("UserNo", WebUser.getNo());
		}
		return Json.ToJson(ht);
	}
	/**
	 执行登录.

	 @return
	*/
	public final String LoginSubmit() throws Exception {
		bp.port.Emp emp = new bp.port.Emp();
		emp.setUserID(this.GetValFromFrmByKey("TB_No"));

		if (emp.RetrieveFromDBSources() == 0)
		{
			return "err@用户名或密码错误.";
		}

		String pass = this.GetValFromFrmByKey("TB_PW");
		if (emp.getPass().equals(pass) == false)
		{
			return "err@用户名或密码错误.";
		}

		//让其登录.
		Dev2Interface.Port_Login(emp.getUserID());

		return "登录成功.";
	}
	/**
	 执行授权登录

	 @return
	*/
	public final String AuthorList_LoginAs() throws Exception {
		WFEmp wfemp = new WFEmp(this.getNo());

		//if (wfemp.AuthorIsOK == false)
		//   return "err@授权登录失败！";

		bp.port.Emp emp1 = new bp.port.Emp(this.getNo());
		WebUser.SignInOfGener(emp1, "CH", false, false, WebUser.getNo(), WebUser.getName());

		return "授权登录成功！";
	}
	/**
	 批处理审批

	 @return
	*/
	public final String Batch_Init()
	{
		String sql = "SELECT a.NodeID, a.Name,a.FlowName, a.BatchRole, COUNT(WorkID) AS Num  FROM  WF_Node a, WF_EmpWorks b ";
		sql += " WHERE a.NodeID=b.FK_Node AND b.FK_Emp='" + WebUser.getNo() + "' AND a.BatchRole!=0 ";
		sql += " AND b.WFState!=7 GROUP BY a.NodeID,a.Name,a.FlowName,a.BatchRole ";
		//sql += " AND b.WFState NOT IN (7) AND a.BatchRole!=0 GROUP BY A.NodeID, a.Name,a.FlowName,a.BatchRole ";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "NodeID";
			dt.Columns.get(1).ColumnName = "Name";
			dt.Columns.get(2).ColumnName = "FlowName";
			dt.Columns.get(3).ColumnName = "BatchRole";
			dt.Columns.get(4).ColumnName = "Num";
		}
		return Json.ToJson(dt);
	}
	/**
	 授权列表

	 @return
	*/
	public final String AuthorTodolist_Init() throws Exception {
		String refNo = this.GetRequestVal("RefNo");
		if(DataType.IsNullOrEmpty(refNo)){
			return "";
		}
		DataTable dt = null;
		Auth auth = new Auth()	;
		auth.setMyPK(refNo);
		if(auth.RetrieveFromDBSources()==0)
			return "err@获取授权信息失败："+refNo+"信息丢失";
		int authType = auth.GetValIntByKey(AuthAttr.AuthType,0);

		String todata = auth.getTakeBackDT().replace("-", "");
		if (DataType.IsNullOrEmpty(auth.getTakeBackDT()) == false) {
			int mydt = Integer.parseInt(todata);
			int nodt = Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
			if (mydt < nodt)
				return "err@授权已经到期不能再查看待办";
		}

		if(authType == 0){
			//获取指定人的所有待办
			DataTable dtt = Dev2Interface.DB_GenerEmpWorksOfDataTable(auth.getAuther());
			List<DataRow> drr = dtt.select("WFState>1");
			dt = dtt.clone();
			for(DataRow dr : drr){
				dt.Rows.AddRow(dr);
			}
 		}
		//获取指定人的指订流程的待办
		if(authType == 1){
			String flowNo=auth.getFlowNo();
			if(DataType.IsNullOrEmpty(flowNo)){
				dt = Dev2Interface.DB_GenerEmpWorksOfDataTable(auth.getAuther());
			}else{
				if(flowNo.endsWith(","))
					flowNo = flowNo.substring(0,flowNo.length()-1);
				flowNo="'"+flowNo.replace(",","','")+"'";
				String sql="SELECT * FROM WF_EmpWorks WHERE  FK_Emp='" + auth.getAuther() + "' AND  FK_Flow IN( " + flowNo + ") AND WFState >1";
				dt = DBAccess.RunSQLReturnTable(sql);
				if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
				{
					dt.Columns.get("PRI").ColumnName = "PRI";
					dt.Columns.get("WORKID").ColumnName = "WorkID";
					dt.Columns.get("ISREAD").ColumnName = "IsRead";
					dt.Columns.get("STARTER").ColumnName = "Starter";
					dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
					dt.Columns.get("WFSTATE").ColumnName = "WFState";
					dt.Columns.get("FK_DEPT").ColumnName = "FK_Dept";
					dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
					dt.Columns.get("FK_FLOW").ColumnName = "FK_Flow";
					dt.Columns.get("FLOWNAME").ColumnName = "FlowName";
					dt.Columns.get("PWORKID").ColumnName = "PWorkID";
					dt.Columns.get("PFLOWNO").ColumnName = "PFlowNo";
					dt.Columns.get("FK_NODE").ColumnName = "FK_Node";
					dt.Columns.get("NODENAME").ColumnName = "NodeName";
					//dt.Columns.get("WORKERDEPT").ColumnName = "WorkerDept";
					dt.Columns.get("TITLE").ColumnName = "Title";
					dt.Columns.get("RDT").ColumnName = "RDT";
					dt.Columns.get("ADT").ColumnName = "ADT";
					dt.Columns.get("SDT").ColumnName = "SDT";
					dt.Columns.get("FK_EMP").ColumnName = "FK_Emp";
					dt.Columns.get("FID").ColumnName = "FID";
					dt.Columns.get("FK_FLOWSORT").ColumnName = "FK_FlowSort";
					dt.Columns.get("SYSTYPE").ColumnName = "SysType";
					dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";
					dt.Columns.get("SENDDT").ColumnName = "SendDT";
					//dt.Columns.get("PRESSTIMES").ColumnName = "PressTimes";
					dt.Columns.get("GUESTNO").ColumnName = "GuestNo";
					dt.Columns.get("GUESTNAME").ColumnName = "GuestName";
					dt.Columns.get("BILLNO").ColumnName = "BillNo";
					//dt.Columns.get("FLOWNOTE").ColumnName = "FlowNote";
					dt.Columns.get("TODOEMPS").ColumnName = "TodoEmps";
					dt.Columns.get("TODOEMPSNUM").ColumnName = "TodoEmpsNum";
					dt.Columns.get("TODOSTA").ColumnName = "TodoSta";
					dt.Columns.get("TASKSTA").ColumnName = "TaskSta";
					dt.Columns.get("LISTTYPE").ColumnName = "ListType";
					dt.Columns.get("SENDER").ColumnName = "Sender";
					dt.Columns.get("ATPARA").ColumnName = "AtPara";
				}

				if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
				{
					dt.Columns.get("pri").ColumnName = "PRI";
					dt.Columns.get("workid").ColumnName = "WorkID";
					dt.Columns.get("isread").ColumnName = "IsRead";
					dt.Columns.get("starter").ColumnName = "Starter";
					dt.Columns.get("startername").ColumnName = "StarterName";
					dt.Columns.get("wfstate").ColumnName = "WFState";
					dt.Columns.get("fk_dept").ColumnName = "FK_Dept";
					dt.Columns.get("deptname").ColumnName = "DeptName";
					dt.Columns.get("fk_flow").ColumnName = "FK_Flow";
					dt.Columns.get("flowname").ColumnName = "FlowName";
					dt.Columns.get("pworkid").ColumnName = "PWorkID";
					dt.Columns.get("pflowno").ColumnName = "PFlowNo";
					dt.Columns.get("fk_node").ColumnName = "FK_Node";
					dt.Columns.get("nodename").ColumnName = "NodeName";
					//  dt.Columns.get("workerdept").ColumnName = "WorkerDept";
					dt.Columns.get("title").ColumnName = "Title";
					dt.Columns.get("rdt").ColumnName = "RDT";
					dt.Columns.get("adt").ColumnName = "ADT";
					dt.Columns.get("sdt").ColumnName = "SDT";
					dt.Columns.get("fk_emp").ColumnName = "FK_Emp";
					dt.Columns.get("fid").ColumnName = "FID";
					dt.Columns.get("fk_flowsort").ColumnName = "FK_FlowSort";
					dt.Columns.get("systype").ColumnName = "SysType";
					dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";
					dt.Columns.get("senddt").ColumnName = "SendDT";
					//dt.Columns.get("presstimes").ColumnName = "PressTimes";
					dt.Columns.get("guestno").ColumnName = "GuestNo";
					dt.Columns.get("guestname").ColumnName = "GuestName";
					dt.Columns.get("billno").ColumnName = "BillNo";
					//dt.Columns.get("flownote").ColumnName = "FlowNote";
					dt.Columns.get("todoemps").ColumnName = "TodoEmps";
					dt.Columns.get("todoempsnum").ColumnName = "TodoEmpsNum";
					dt.Columns.get("todosta").ColumnName = "TodoSta";
					dt.Columns.get("tasksta").ColumnName = "TaskSta";
					dt.Columns.get("listtype").ColumnName = "ListType";
					dt.Columns.get("sender").ColumnName = "Sender";
					dt.Columns.get("atpara").ColumnName = "AtPara";

				}

			}
		}
		return bp.tools.Json.ToJson(dt);
	}

	/**
	 授权列表

	 @return
	*/
	public final String AuthorTodolist_Todolist()
	{

		return "";
	}
	/**
	 退出登录

	 @param UserNo
	 @param Author
	 @return
	*/
	public final String AuthExitAndLogin(String UserNo, String Author)
	{
		String msg = "suess@退出成功！";
		try
		{
			bp.port.Emp emp = new bp.port.Emp(UserNo);
			//首先退出
			WebUser.Exit();
			//再进行登录
			bp.port.Emp emp1 = new bp.port.Emp(Author);
			WebUser.SignInOfGener(emp1, "CH", false, false, null, null);
		}
		catch (Exception ex)
		{
			msg = "err@退出时发生错误:" + ex.getMessage();
		}
		return msg;
	}
	/**
	 获取授权人列表

	 @return
	*/
	public final String AuthorList_Init() throws Exception {
		try
		{
			Auths ens = new Auths();
			ens.Retrieve(AuthAttr.AutherToEmpNo, WebUser.getNo(), null);
			return ens.ToJson("dt");

			//Paras ps = new Paras();
			//ps.SQL = "SELECT No,Name,AuthorDate FROM WF_Emp WHERE Author=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "Author";
			//ps.Add("Author", bp.web.WebUser.getNo());
			//DataTable dt = DBAccess.RunSQLReturnTable(ps);

			//if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
			//{
			//    dt.Columns.get("NO").ColumnName = "No";
			//    dt.Columns.get("NAME").ColumnName = "Name";
			//    dt.Columns.get("AUTHORDATE").ColumnName = "AuthorDate";
			//}
			//if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
			//{
			//    dt.Columns.get("no").ColumnName = "No";
			//    dt.Columns.get("name").ColumnName = "Name";
			//    dt.Columns.get("authordate").ColumnName = "AuthorDate";
			//}
			//return BP.Tools.Json.ToJson(dt);
		}
		catch (Exception ex)
		{
			WFEmp en = new WFEmp();
			en.CheckPhysicsTable();
			throw new RuntimeException("err@系统异常，请在执行一次:" + ex.getMessage());
		}
	}

	public final String AuthorListMy_Init() throws Exception {
		try
		{
			Auths ens = new Auths();
			ens.Retrieve(AuthAttr.Auther, WebUser.getNo(), null);
			return ens.ToJson("dt");
		}
		catch (Exception ex)
		{
			WFEmp en = new WFEmp();
			en.CheckPhysicsTable();
			throw new RuntimeException("err@系统异常，请在执行一次:" + ex.getMessage());
		}
	}
	/**
	 当前登录人是否有授权

	 @return
	*/
	public final String IsHaveAuthor() throws Exception {
		Auths ens = new Auths();
		ens.Retrieve(AuthAttr.Auther, WebUser.getNo(), null);
		if (ens.size() > 0)
		{
			return "suess@有授权";
		}
		return "err@没有授权";
	}
	/**
	 退出.

	 @return
	*/
	public final String LoginExit() throws Exception {
		Dev2Interface.Port_SigOut();
		return null;
	}
	/**
	 授权退出.

	 @return
	*/
	public final String AuthExit() throws Exception {
		return this.AuthExitAndLogin(this.getNo(), WebUser.getAuth());
	}

		///#endregion 登录相关.

	/**
	 获得抄送列表

	 @return
	*/
	public final String CC_Init()
	{
		String sta = this.GetRequestVal("Sta");
		if (sta == null || Objects.equals(sta, ""))
		{
			sta = "-1";
		}

		int pageSize = 6; // int.Parse(pageSizeStr);

		String pageIdxStr = this.GetRequestVal("PageIdx");
		if (pageIdxStr == null)
		{
			pageIdxStr = "1";
		}

		int pageIdx = Integer.parseInt(pageIdxStr);

		//实体查询.
		//BP.WF.SMSs ss = new BP.WF.SMSs();
		//BP.En.QueryObject qo = new BP.En.QueryObject(ss);

		DataTable dt = null;
		if (sta.equals("-1"))
		{
			dt = Dev2Interface.DB_CCList();
		}

		if (Objects.equals(sta, "0"))
		{
			dt = Dev2Interface.DB_CCList_UnRead(WebUser.getNo());
		}

		if (Objects.equals(sta, "1"))
		{
			dt = Dev2Interface.DB_CCList_Read();
		}

		if (Objects.equals(sta, "2"))
		{
			dt = Dev2Interface.DB_CCList_Delete(WebUser.getNo());
		}

		//int allNum = qo.GetCount();
		//qo.DoQuery(BP.WF.SMSAttr.MyPK, pageSize, pageIdx);

		return Json.ToJson(dt);
	}

	/**
	 我的流程的查询条件

	 @return
	*/
	public final String Search_Conds() throws Exception {
		if (WebUser.getNo() == null)
		{
			throw new RuntimeException("err@登录信息丢失.");
		}
		DataSet ds = new DataSet();
		String tSpan = this.GetRequestVal("TSpan");
		String keyWord = this.GetRequestVal("KeyWord");


			///#region 1、获取时间段枚举/总数.
		SysEnums ses = new SysEnums("TSpan");
		DataTable dtTSpan = ses.ToDataTableField("dt");
		dtTSpan.TableName = "TSpan";
		ds.Tables.add(dtTSpan);
		String sqlWhere = "";
		if (DataType.IsNullOrEmpty(keyWord) == false)
		{
			sqlWhere += " AND Title like '%" + keyWord + "%' ";
		}
		if (DataType.IsNullOrEmpty(this.getFlowNo()) == false)
		{
			sqlWhere += " AND FK_Flow='" + this.getFlowNo() + "'";
		}
		String sql = " SELECT  TSpan as No, COUNT(WorkID) as Num FROM WF_GenerWorkFlow WHERE (Emps LIKE '%" + WebUser.getNo() + "%' OR Starter='" + WebUser.getNo() + "') AND FID = 0 AND WFState > 1 " + sqlWhere + " GROUP BY TSpan ";

		DataTable dtTSpanNum = DBAccess.RunSQLReturnTable(sql);
		for (DataRow drEnum : dtTSpan.Rows)
		{
			String no = drEnum.getValue("IntKey").toString();
			for (DataRow dr : dtTSpanNum.Rows)
			{
				if (Objects.equals(dr.getValue("No").toString(), no))
				{
					drEnum.setValue("Lab", drEnum.get("Lab").toString() + "(" + dr.getValue("Num") + ")");
					break;
				}
			}
		}

			///#endregion


			///#region 2、处理流程类别列表.
		sqlWhere = "";
		if (DataType.IsNullOrEmpty(keyWord) == false)
		{
			sqlWhere += " AND Title like '%" + keyWord + "%' ";
		}
		if (!Objects.equals(tSpan, "-1"))
		{
			sqlWhere += " AND TSpan=" + tSpan;
		}

		sql = "SELECT  FK_Flow as No, FlowName as Name, COUNT(WorkID) as Num FROM WF_GenerWorkFlow WHERE (Emps LIKE '%" + WebUser.getNo() + "%' OR TodoEmps LIKE '%" + WebUser.getNo() + ",%' OR Starter='" + WebUser.getNo() + "')  AND WFState > 1 AND FID = 0 " + sqlWhere + " GROUP BY FK_Flow, FlowName";

		DataTable dtFlows = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dtFlows.Columns.get(0).setColumnName("No");
			dtFlows.Columns.get(1).setColumnName("Name");
			dtFlows.Columns.get(2).setColumnName("Num");
		}


		dtFlows.TableName = "Flows";
		ds.Tables.add(dtFlows);

			///#endregion
		return Json.ToJson(ds);
	}
	/**
	 查询总条数

	 @return
	*/
	public final String Search_Count()
	{
		if (WebUser.getNo() == null)
		{
			return "";
		}
		String tSpan = this.GetRequestVal("TSpan");
		if (DataType.IsNullOrEmpty(tSpan) == true)
		{
			tSpan = null;
		}
		//查询关键字
		String keyWord = this.GetRequestVal("KeyWord");
		if (DataType.IsNullOrEmpty(keyWord) == true)
		{
			keyWord = null;
		}

		String sqlWhere = "(Emps LIKE '%" + WebUser.getNo() + "%' OR TodoEmps LIKE '%" + WebUser.getNo() + "%' OR Starter = '" + WebUser.getNo() + "') AND FID = 0 AND WFState > 1 ";
		if (!Objects.equals(tSpan, "-1"))
		{
			sqlWhere += "AND TSpan = '" + tSpan + "' ";
		}
		if (keyWord != null)
		{
			sqlWhere += "AND Title like '%" + keyWord + "%' ";
		}
		if (this.getFlowNo() != null)
		{
			sqlWhere += "AND FK_Flow = '" + this.getFlowNo() + "' ";
		}

		//获取总条数
		String totalNumSql = "SELECT count(*) from WF_GenerWorkFlow where " + sqlWhere;
		int totalNum = DBAccess.RunSQLReturnValInt(totalNumSql);
		return String.valueOf(totalNum);
	}
	/**
	 初始化

	 @return
	*/
	public final String Search_Data() throws Exception {
		if (WebUser.getNo() == null)
		{
			return "";
		}
		String sql = "";

		String tSpan = this.GetRequestVal("TSpan");
		if (DataType.IsNullOrEmpty(tSpan) == true)
		{
			tSpan = null;
		}
		//查询关键字
		String keyWord = this.GetRequestVal("KeyWord");
		if (DataType.IsNullOrEmpty(keyWord) == true)
		{
			keyWord = null;
		}



			///#region 处理查询
		//当前页
		int pageIdx = this.getPageIdx();
		//每页条数
		int pageSize = this.getPageSize();

		int startIndex = (pageIdx - 1) * pageSize;
		int num = pageSize * (pageIdx - 1);
		String sqlWhere = "(Emps LIKE '%" + WebUser.getNo() + "%' OR TodoEmps LIKE '%" + WebUser.getNo() + "%' OR Starter = '" + WebUser.getNo() + "') AND FID = 0 AND WFState > 1 ";
		if (!Objects.equals(tSpan, "-1"))
		{
			sqlWhere += "AND TSpan = '" + tSpan + "' ";
		}
		if (keyWord != null)
		{
			sqlWhere += "AND Title like '%" + keyWord + "%' ";
		}
		if (this.getFlowNo() != null)
		{
			sqlWhere += "AND FK_Flow = '" + this.getFlowNo() + "' ";
		}

		sqlWhere += " ORDER BY RDT DESC ";
		if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
			sql = "SELECT NVL(WorkID, 0) WorkID,NVL(FID, 0) FID ,FK_Flow,FlowName,Title, NVL(WFSta, 0) WFSta,WFState,  Starter, StarterName,Sender,NVL(RDT, '2018-05-04 19:29') RDT,NVL(FK_Node, 0) FK_Node,NodeName, TodoEmps " + "FROM (select A.*, rownum r from (select * from WF_GenerWorkFlow where " + sqlWhere + ") A) where r between " + (pageIdx * pageSize - pageSize + 1) + " and " + (pageIdx * pageSize);
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
		{
			sql = "SELECT  TOP " + pageSize + " ISNULL(WorkID, 0) WorkID,ISNULL(FID, 0) FID ,FK_Flow,FlowName,Title, ISNULL(WFSta, 0) WFSta,WFState,  Starter, StarterName,Sender,ISNULL(RDT, '2018-05-04 19:29') RDT,ISNULL(FK_Node, 0) FK_Node,NodeName, TodoEmps FROM WF_GenerWorkFlow " + "where WorkID not in (select top(" + num + ") WorkID from WF_GenerWorkFlow where " + sqlWhere + ") AND" + sqlWhere;
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.MySQL  || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL  || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{
			sql = "SELECT IFNULL(WorkID, 0) WorkID,IFNULL(FID, 0) FID ,FK_Flow,FlowName,Title, IFNULL(WFSta, 0) WFSta,WFState,  Starter, StarterName,Sender,IFNULL(RDT, '2018-05-04 19:29') RDT,IFNULL(FK_Node, 0) FK_Node,NodeName, TodoEmps FROM WF_GenerWorkFlow where (1=1) AND " + sqlWhere + " LIMIT " + startIndex + "," + pageSize;
		}
		else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX)
		{
			sql = "SELECT COALESCE(WorkID, 0) WorkID,COALESCE(FID, 0) FID ,FK_Flow,FlowName,Title, COALESCE(WFSta, 0) WFSta,WFState,  Starter, StarterName,Sender,COALESCE(RDT, '2018-05-04 19:29') RDT,COALESCE(FK_Node, 0) FK_Node,NodeName, TodoEmps FROM WF_GenerWorkFlow where (1=1) AND " + sqlWhere + " LIMIT " + pageSize + " offset " + startIndex;
		}
		DataTable mydt = DBAccess.RunSQLReturnTable(sql);

		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			mydt.Columns.get(0).ColumnName = "WorkID";
			mydt.Columns.get(1).ColumnName = "FID";
			mydt.Columns.get(2).ColumnName = "FK_Flow";
			mydt.Columns.get(3).ColumnName = "FlowName";
			mydt.Columns.get(4).ColumnName = "Title";
			mydt.Columns.get(5).ColumnName = "WFSta";
			mydt.Columns.get(6).ColumnName = "WFState";
			mydt.Columns.get(7).ColumnName = "Starter";
			mydt.Columns.get(8).ColumnName = "StarterName";
			mydt.Columns.get(9).ColumnName = "Sender";
			mydt.Columns.get(10).ColumnName = "RDT";
			mydt.Columns.get(11).ColumnName = "FK_Node";
			mydt.Columns.get(12).ColumnName = "NodeName";
			mydt.Columns.get(13).ColumnName = "TodoEmps";


		}
		mydt.TableName = "WF_GenerWorkFlow";
		if (mydt != null)
		{
			mydt.Columns.Add("TDTime");
			for (DataRow dr : mydt.Rows)
			{
				dr.setValue("TDTime", GetTraceNewTime(dr.getValue("FK_Flow").toString(), Integer.parseInt(dr.getValue("WorkID").toString()), Integer.parseInt(dr.getValue("FID").toString())));
			}
		}

			///#endregion

		return Json.ToJson(mydt);
	}
	public static String GetTraceNewTime(String fk_flow, long workid, long fid) throws Exception {

			///#region 获取track数据.
		String sqlOfWhere2 = "";
		String sqlOfWhere1 = "";
		String dbStr = SystemConfig.getAppCenterDBVarStr();
		Paras ps = new Paras();
		if (fid == 0)
		{
			sqlOfWhere1 = " WHERE (FID=" + dbStr + "WorkID11 OR WorkID=" + dbStr + "WorkID12 )  ";
			ps.Add("WorkID11", workid);
			ps.Add("WorkID12", workid);
		}
		else
		{
			sqlOfWhere1 = " WHERE (FID=" + dbStr + "FID11 OR WorkID=" + dbStr + "FID12 ) ";
			ps.Add("FID11", fid);
			ps.Add("FID12", fid);
		}

		String sql = "";
		sql = "SELECT MAX(RDT) FROM ND" + Integer.parseInt(fk_flow) + "Track " + sqlOfWhere1;
		sql = "SELECT RDT FROM  ND" + Integer.parseInt(fk_flow) + "Track  WHERE RDT=(" + sql + ")";
		ps.SQL = sql;

		try
		{
			return DBAccess.RunSQLReturnString(ps);
		}
		catch (java.lang.Exception e)
		{
			// 处理track表.
			Track.CreateOrRepairTrackTable(fk_flow);
			return DBAccess.RunSQLReturnString(ps);
		}

			///#endregion 获取track数据.
	}

		///#region 处理page接口.
	/**
	 执行的内容
	*/
	public final String getDoWhat()
	{
		String str = this.GetRequestVal("DoWhat");
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = this.GetRequestVal("DoType");
		}
		return str;
	}
	/**
	 当前的用户
	*/
	public final String getUserNo()
	{

		String str = this.GetRequestVal("UserNo");
		if (DataType.IsNullOrEmpty(str) == true)
		{
			return this.GetRequestVal("UserID");
		}
		return str;
	}
	/**
	 用户的安全校验码(请参考集成章节)
	*/
	public final String getSID()
	{
		String str = this.GetRequestVal("Token");
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = this.GetRequestVal("Token");
		}
		return str;
	}
	public final long GenerWorkIDByEntityPK() throws Exception {
		if (this.getWorkID() != 0)
		{
			return this.getWorkID();
		}

		String entityPK = this.GetRequestVal("EntityPK");
		if (DataType.IsNullOrEmpty(entityPK) == true)
		{
			return 0;
		}

		String entityPKVal = this.GetRequestVal(entityPK);
		if (DataType.IsNullOrEmpty(entityPK) == true)
		{
			throw new RuntimeException("err@参数值[" + entityPK + "]没有传递过来,无法获取实体流程的数据.");
		}

		GERpt rpt = new GERpt("ND" + Integer.parseInt(this.getFlowNo()) + "Rpt");

		if (rpt.getEnMap().getAttrs().contains(entityPK) == false)
		{
			String frmID = "ND" + Integer.parseInt(this.getFlowNo()) + "Rpt";
			MapAttr attr = new MapAttr();
			attr.setMyPK(frmID + "_" + entityPK);
			attr.setKeyOfEn(entityPK);
			attr.setFrmID( frmID);
			attr.setName(entityPK);
			attr.setMyDataType(DataType.AppString);
			attr.setMaxLen(50);
			attr.setUIVisible(false);
			attr.Save();

			//节点表单
			frmID = "ND" + Integer.parseInt(this.getFlowNo()) + "01";
			attr.setMyPK(frmID + "_" + entityPK);
			attr.setKeyOfEn(entityPK);
			attr.setFrmID( frmID);
			attr.setMyDataType(DataType.AppString);
			attr.setMaxLen(50);
			attr.setUIVisible(false);
			attr.Save();

			//从新查询.
			rpt = new GERpt("ND" + Integer.parseInt(this.getFlowNo()) + "Rpt");
		}
		int i = rpt.Retrieve(entityPK, entityPKVal);

		if (rpt.getOID() == 0)
		{
			rpt.setOID(Dev2Interface.Node_CreateBlankWork(this.getFlowNo(), WebUser.getNo()));
			rpt.Retrieve();

			rpt.setPrjNo(entityPK);
			rpt.setPrjName(entityPKVal);
			rpt.SetValByKey(entityPK, entityPKVal);
			rpt.Update();

			GenerWorkFlow gwf = new GenerWorkFlow(rpt.getOID());
			gwf.setPrjNo(entityPK);
			gwf.setPrjName(entityPKVal);
			gwf.SetPara("EntityPK", entityPK);
			gwf.SetPara(entityPK, entityPKVal);
			gwf.Update();
		}

		return rpt.getOID();
	}
	/**
	 用户登录.

	 @return
	*/
	public final String Port_Login() throws Exception {

		String userNo = this.getUserNo();
		if (DataType.IsNullOrEmpty(userNo) == false)
		{
			Dev2Interface.Port_Login(userNo);
			return Dev2Interface.Port_GenerToken();
		}

		String token = this.GetRequestVal("Token");
		return Dev2Interface.Port_LoginByToken(token);
	}
	public final String getDevType()
	{
		String val = this.GetRequestVal("DevType");
		if (DataType.IsNullOrEmpty(val) == true || Objects.equals(val, "PC"))
		{
			return "WF";
		}
		else
		{
			return "CCMobile";
		}
	}
	public final String PortSaaS_Init() throws Exception {
		if (this.getDoWhat() == null)
		{
			return "err@必要的参数没有传入，请参考接口规则。DoWhat";
		}

		String token = "";


			///#region 根据用户的token, 调用配置的ssoURL ，获得用户名，并登录.
		String userNo = "ccs";
		bp.wf.port.admingroup.Org org = new bp.wf.port.admingroup.Org();
		org.setNo(this.getOrgNo());
		if (org.RetrieveFromDBSources() == 0)
		{
			return "err@组织编号为" + this.getOrgNo() + "在数据库中不存在";
		}
		String url = org.GetValStringByKey("SSOUrl", ""); // 格式: https:/xxxx.xxx.xxx.xx/xx.do?xxxxx={$Token}
		if (DataType.IsNullOrEmpty(url) == true || url.contains("{$Token}") == false)
		{
			return "err@组织信息配置错误,没有配置回调访问验证的token的服务地址，请让组织管理员登录系统在=》组织管理=》组织属性=》SSOUrl进行设置。";
		}
		String ticket = this.GetRequestVal("ticket");
		//根据配置的地址，获得token.
		url = url.replace("{$ticket}", ticket).replace("{$Token}", ticket);
		String result = DataType.ReadURLContext(url, 9999); //执行回调：url返回用户编号.
		if (DataType.IsNullOrEmpty(result) == true)
		{
			return "err@执行URL没有返回结果值";
		}
		//数据序列化
		JSONObject jsonData =JSONObject.fromObject(result);
		//code=200，表示请求成功，否则失败
		if (!jsonData.get("code").toString().equals("0000"))
		{
			return "err@执行SSOUrl回调URL返回结果失败";
		}
		if(jsonData.get("result") == null)
			return "err@执行SSOUrl回调URL返回结果result获取为null";
		//获取返回的数据
		JSONObject data = JSONObject.fromObject(jsonData.get("result").toString());
		//
		userNo = data.get("mobilePhone") != null ? data.get("mobilePhone").toString() : "";
		if (DataType.IsNullOrEmpty(userNo) == true)
		{
			return "err@读取url错误:" + userNo;
		}

		String empID = this.getOrgNo() + "_" + userNo;
		bp.port.Emp emp = new bp.port.Emp();
		emp.setNo(empID);
		if (emp.RetrieveFromDBSources() == 0)
		{
			return "err@用户错误:" + emp.getNo();
		}

		//执行登录.
		Dev2Interface.Port_Login(userNo, this.getOrgNo());
		token = Dev2Interface.Port_GenerToken();
			///#endregion 获得token.

		if (this.getDoWhat().equals("PortLogin") == true)
		{
			return "登录成功";
		}


			///#region 生成参数串.
		String paras = "";
		for (String str : ContextHolderUtils.getRequest().getParameterMap().keySet())
		{
			String val = this.GetRequestVal(str);
			if (val.indexOf('@') != -1)
			{
				return "err@您没有能参数: [ " + str + " ," + val + " ] 给值 ，URL 将不能被执行。";
			}
			switch (str)
			{
				case DoWhatList.DoNode:
				case DoWhatList.Emps:
				case DoWhatList.EmpWorks:
				case DoWhatList.FlowSearch:
				case DoWhatList.Login:
				case DoWhatList.MyFlow:
				case DoWhatList.MyWork:
				case DoWhatList.Start:
				case DoWhatList.Start5:
				case DoWhatList.StartSimple:
				case DoWhatList.FlowFX:
				case DoWhatList.DealWork:
				case "StartFlow":
				case "FK_Flow":
				case "WorkID":
				case "FK_Node":
				case "Token":
				case "DoType":
				case "DoMethod":
				case "HttpHandlerName":
				case "t":
				case "FrmID":
				case "FK_MapData":
				case "MyFrm":
				case "MyView":
				case "DoWhat":
				case "EntityPK":
					break;
				default:
					paras += "&" + str + "=" + val;
					break;
			}
		}
		paras += "&Token=" + token;
		String nodeID = String.valueOf(Integer.parseInt(this.getFlowNo() + "01"));

			///#endregion 生成参数串.

		//发起流程.
		if (this.getDoWhat().equals("StartClassic") == true)
		{
			if (this.getFlowNo() == null)
			{
				return "url@./AppClassic/Home.htm";
			}
			else
			{
				return "url@./AppClassic/Home.htm?FK_Flow=" + this.getFlowNo() + paras + "&FK_Node=" + nodeID;
			}
		}

		//打开工作轨迹。
		if (this.getDoWhat().equals(DoWhatList.OneWork) == true)
		{
			if (this.getFlowNo() == null || this.getWorkID() == 0)
			{
				throw new RuntimeException("@参数 FK_Flow 或者 WorkID 为 Null 。");
			}
			return "url@/" + this.getDevType() + "/MyView.htm?FK_Flow=" + this.getFlowNo() + "&WorkID=" + this.getWorkID() + "&o2=1" + paras;
		}

		//查看表单不需要FK_Node参数。
		if (this.getDoWhat().equals("MyView") == true)
		{
			//if (this.getNodeID() != 0)
			//    return "err@执行MyView不需要NodeID/FK_Node参数.";
			long workID = this.GenerWorkIDByEntityPK();
			if (workID == 0)
			{
				return "err@执行MyView不需要NodeID/FK_Node参数.";
			}

			if (this.getNodeID() == 0)
			{
				GenerWorkFlow gwf = new GenerWorkFlow(workID);
				paras += "&FK_Node=" + gwf.getNodeID();
			}

			return "url@/" + this.getDevType() + "/MyView.htm?FK_Flow=" + this.getFlowNo() + "&WorkID=" + workID + "&o2=1" + paras;
		}

		//查看指定的节点表单需要FK_Node参数。
		if (this.getDoWhat().equals("MyFrm") == true)
		{
			if (this.getNodeID() == 0)
			{
				return "err@执行 MyFrm 需要NodeID/FK_Node参数.";
			}

			long workID = this.GenerWorkIDByEntityPK();
			if (workID == 0)
			{
				return "err@执行MyView不需要NodeID/FK_Node参数.";
			}

			return "url@/" + this.getDevType() + "/MyFrm.htm?FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID() + "&WorkID=" + workID + "&o2=1" + paras;
		}

		//发起页面,或者调用发起流程.
		if (this.getDoWhat().equals(DoWhatList.Start) == true || this.getDoWhat().equals("StartFlow"))
		{
			if (this.getFlowNo() == null)
			{
				return "url@Start.htm";
			}

			//实体编号,启动指定实体编号隶属的工作流程ID.
			long workID = this.GenerWorkIDByEntityPK();
			if (workID == 0)
			{
				return "url@MyFlow.htm?FK_Flow=" + this.getFlowNo() + paras + "&FK_Node=" + nodeID;
			}

			// 是否可以执行当前的工作.
			if (Dev2Interface.Flow_IsCanDoCurrentWork(workID, WebUser.getNo()) == true)
			{
				return "url@/" + this.getDevType() + "/MyFlow.htm?FK_Flow=" + this.getFlowNo() + paras + "&WorkID=" + workID;
			}

			return "url@/" + this.getDevType() + "/MyView.htm?FK_Flow=" + this.getFlowNo() + paras + "&WorkID=" + workID;
		}

		//发起表单.
		if (this.getDoWhat().equals("StartFrm"))
		{
			long oid = this.getOID();
			if (oid == 0)
			{
				GEEntityOID gn = new GEEntityOID(this.getFrmID());
				oid = DBAccess.GenerOIDByGUID();
				try
				{
					gn.SaveAsOID(oid);
				}
				catch (RuntimeException ex)
				{
					gn.CheckPhysicsTable();
					gn.SaveAsOID(oid);
				}
			}
			return "url@/" + this.getDevType() + "/CCForm/Frm.htm?FrmID=" + this.getFrmID() + "&OID=" + oid + "" + paras;
		}


		//处理工作.
		if (this.getDoWhat().equals(DoWhatList.DealWork) == true || this.getDoWhat().equals(DoWhatList.MyFlow) == true)
		{
			long workid = this.getWorkID();
			if (workid == 0){
				return "err@参数 WorkID 为Null 。";
			}
			String flowNo = this.getFlowNo();
			if (DataType.IsNullOrEmpty(flowNo)){
				// 如果没有FlowNo，计算FlowNo
				GenerWorkFlow gwf = new GenerWorkFlow();
				gwf.setWorkID(workid);
				int i = gwf.RetrieveFromDBSources();
				if (i == 0){
					return "err@计算FlowNo出错，没有GenerWorkFlow数据。";
				}
				flowNo = gwf.getFlowNo();
			}
			return "url@MyFlow.htm?FK_Flow=" + flowNo + "&WorkID=" + workid + "&o2=1" + paras;
		}
		//我的抄送.
		if (this.getDoWhat().equals(DoWhatList.MyCC) == true)
		{
			if (DataType.IsNullOrEmpty(this.getFlowNo()) || this.getWorkID() == 0)
				return "err@参数 FK_Flow 或者 WorkID 为Null 。";
			return "url@MyCC.htm?FK_Flow=" + this.getFlowNo() + "&WorkID=" + this.getWorkID() + "&o2=1" + paras + "&FK_Node=" + nodeID;
		}
		//流程设计器
		if (this.getDoWhat().equals(DoWhatList.Flows) == true)
		{
			return "url@Portal/Flows.htm";
		}
		//表单设计器
		if (this.getDoWhat().equals(DoWhatList.Frms) == true)
		{
			return "url@Portal/Frms.htm";
		}

		//请求在途.
		if (this.getDoWhat().equals(DoWhatList.Runing) == true)
		{
			return "url@/" + this.getDevType() + "/Runing.htm?FK_Flow=" + this.getFlowNo();
		}

		//请求首页.
		if (this.getDoWhat().equals("Home") == true)
		{
			if (this.getDevType().equals("CCMobile"))
			{
				return "url@/CCMobilePortal/SaaS/Home.htm?FK_Flow=" + this.getFlowNo();
			}
			return "url@/Portal/Standard/Default.htm?FK_Flow=" + this.getFlowNo();
		}

		//请求待办。
		if (this.getDoWhat().equals(DoWhatList.EmpWorks) == true || this.getDoWhat().equals("Todolist") == true)
		{
			if (DataType.IsNullOrEmpty(this.getFlowNo()))
			{
				return "url@/" + this.getDevType() + "/Todolist.htm";
			}
			else
			{
				return "url@/" + this.getDevType() + "/Todolist.htm?FK_Flow=" + this.getFlowNo();
			}
		}
		//草稿
		if (this.getDoWhat().equals(DoWhatList.Draft) == true)
		{
			return "url@Draft.htm?FK_Flow=" + this.getFlowNo();
		}
		//请求流程查询。
		if (this.getDoWhat().equals(DoWhatList.FlowSearch) == true)
		{
			if (DataType.IsNullOrEmpty(this.getFlowNo()))
			{
				return "url@./RptSearch/Default.htm";
			}
			else
			{
				return "url@./RptDfine/Search.htm?2=1&FK_Flow=001&EnsName=ND" + Integer.parseInt(this.getFlowNo()) + "Rpt" + paras;
			}
		}

		//流程查询小页面.
		if (this.getDoWhat().equals(DoWhatList.FlowSearchSmall) == true)
		{
			if (this.getFlowNo() == null)
			{
				return "url@./RptSearch/Default.htm";
			}
			else
			{
				return "url./Comm/Search.htm?EnsName=ND" + Integer.parseInt(this.getFlowNo()) + "Rpt" + paras;
			}
		}

		//打开消息.
		if (this.getDoWhat().equals(DoWhatList.DealMsg) == true)
		{
			String guid = this.GetRequestVal("GUID");
			SMS sms = new SMS();
			sms.setMyPK(guid);
			sms.Retrieve();

			//判断当前的登录人员.
			if (!Objects.equals(WebUser.getNo(), sms.getSendToEmpNo()))
			{
				Dev2Interface.Port_Login(sms.getSendToEmpNo());
			}

			AtPara ap = new AtPara(sms.getAtPara());
			switch (sms.getMsgType())
			{
				case SMSMsgType.SendSuccess: // 发送成功的提示.

					if (Dev2Interface.Flow_IsCanDoCurrentWork(ap.GetValInt64ByKey("WorkID"), WebUser.getNo()) == true)
					{
						return "url@/" + this.getDevType() + "/MyFlow.htm?FK_Flow=" + ap.GetValStrByKey("FK_Flow") + "&WorkID=" + ap.GetValStrByKey("WorkID") + "&o2=1" + paras;
					}
					else
					{
						return "url@/" + this.getDevType() + "/MyView.htm?FK_Flow=" + ap.GetValStrByKey("FK_Flow") + "&WorkID=" + ap.GetValStrByKey("WorkID") + "&o2=1" + paras;
					}

				default: //其他的情况都是查看工作报告.
					return "url@/" + this.getDevType() + "/MyView.htm?FK_Flow=" + ap.GetValStrByKey("FK_Flow") + "&WorkID=" + ap.GetValStrByKey("WorkID") + "&o2=1" + paras;
			}
		}
		return "url@/" + this.getDevType() + "/AppClassic/Home.htm?Token=" + paras;
		//  return "err@没有判断的标记.";
		//  return "warning@没有约定的标记:DoWhat=" + this.DoWhat;
	}

	/**
	 调用页面入口
	 @return
	*/
	public final String Port_Init() throws Exception {
		if (this.getDoWhat() == null)
			return "err@必要的参数没有传入，请参考接口规则, DoWhat";

			///#region 安全性校验. SID 模式.
		String token = this.GetRequestVal("Token");
		if (DataType.IsNullOrEmpty(token) == false)
		{
			Dev2Interface.Port_LoginByToken(token);
		}
		else if (DataType.IsNullOrEmpty(this.getUserNo()) == false)
		{
			Dev2Interface.Port_Login(this.getUserNo());
			//token = Dev2Interface.Port_GenerToken("PC");
		}

			///#endregion 安全性校验. SID 模式.

		if (this.getDoWhat().equals("PortLogin") == true)
		{
			return "登录成功";
		}


			///#region 生成参数串.
		String paras = "";
		for (String str : ContextHolderUtils.getRequest().getParameterMap().keySet())
		{
			String val = this.GetRequestVal(str);
			if (val.indexOf('@') != -1)
			{
				return "err@您没有能参数: [ " + str + " ," + val + " ] 给值 ，URL 将不能被执行。";
			}
			switch (str)
			{
				case DoWhatList.DoNode:
				case DoWhatList.Emps:
				case DoWhatList.EmpWorks:
				case DoWhatList.FlowSearch:
				case DoWhatList.Login:
				case DoWhatList.MyFlow:
				case DoWhatList.MyWork:
				case DoWhatList.Start:
				case DoWhatList.Start5:
				case DoWhatList.StartSimple:
				case DoWhatList.FlowFX:
				case DoWhatList.DealWork:
				case "StartFlow":
				case "FK_Flow":
				case "WorkID":
				case "FK_Node":
				case "Token":
				case "DoType":
				case "DoMethod":
				case "HttpHandlerName":
				case "t":
				case "FrmID":
				case "FK_MapData":
				case "MyFrm":
				case "MyView":
				case "DoWhat":
				case "EntityPK":
					break;
				default:
					paras += "&" + str + "=" + val;
					break;
			}
		}
		paras += "&Token=" + token;

		String nodeID = "0";
		if(DataType.IsNullOrEmpty(this.getFlowNo())==false)
			nodeID = String.valueOf(Integer.parseInt(this.getFlowNo() + "01"));

			///#endregion 生成参数串.

		//发起流程.
		if (this.getDoWhat().equals("StartClassic") == true)
		{
			if (this.getFlowNo() == null)
			{
				return "url@./AppClassic/Home.htm";
			}
			else
			{
				return "url@./AppClassic/Home.htm?FK_Flow=" + this.getFlowNo() + paras + "&FK_Node=" + nodeID;
			}
		}

		//请求待办。
		if (this.getDoWhat().equals(DoWhatList.EmpWorks) == true || this.getDoWhat().equals("Todolist") == true)
		{
			if (DataType.IsNullOrEmpty(this.getFlowNo()))
			{
				return "url@Todolist.htm";
			}
			else
			{
				return "url@Todolist.htm?FK_Flow=" + this.getFlowNo() + "&Token=" + token;
			}
		}

		//请求流程查询。
		if (this.getDoWhat().equals(DoWhatList.FlowSearch) == true)
		{
			if (DataType.IsNullOrEmpty(this.getFlowNo()))
			{
				return "url@./RptSearch/Default.htm";
			}
			else
			{
				return "url@./RptDfine/Search.htm?2=1&FK_Flow=" + this.getFlowNo() + "&EnsName=ND" + Integer.parseInt(this.getFlowNo()) + "Rpt" + paras;
			}
		}

		//流程查询小页面.
		if (this.getDoWhat().equals(DoWhatList.FlowSearchSmall) == true)
		{
			if (this.getFlowNo() == null)
			{
				return "url@./RptSearch/Default.htm";
			}
			else
			{
				return "url@./Comm/Search.htm?EnsName=ND" + Integer.parseInt(this.getFlowNo()) + "Rpt" + paras;
			}
		}

		//以下接口中如果没有FlowNo会自动计算FlowNo
		long workid = this.getWorkID();
		if (workid == 0){
			return "err@参数 WorkID 为Null 。";
		}
		String flowNo = this.getFlowNo();
		if (DataType.IsNullOrEmpty(flowNo)){
			// 如果没有FlowNo，计算FlowNo
			GenerWorkFlow gwf = new GenerWorkFlow();
			gwf.setWorkID(workid);
			int i = gwf.RetrieveFromDBSources();
			if (i == 0){
				return "err@计算FlowNo出错，没有GenerWorkFlow数据。";
			}
			flowNo = gwf.getFlowNo();
		}

		//打开工作轨迹。
		if (this.getDoWhat().equals(DoWhatList.OneWork) == true)
		{
			return "url@MyView.htm?FK_Flow=" + flowNo +"&WorkID=" + workid + "&o2=1" + paras;
		}
		// 近期处理
		if (this.getDoWhat().equals(DoWhatList.RecentWork) == true)
			return "url@RecentWork.htm?Token=" + token +"&FK_Flow=" + flowNo;
		//查看表单不需要FK_Node参数。
		if (this.getDoWhat().equals("MyView") == true)
		{
			long workID = this.GenerWorkIDByEntityPK();
			if (workID == 0)
			{
				return "err@执行MyView不需要NodeID/FK_Node参数.";
			}

			if (this.getNodeID() == 0)
			{
				GenerWorkFlow gwf = new GenerWorkFlow(workID);
				paras += "&FK_Node=" + gwf.getNodeID();
				paras += "&FK_Flow=" + gwf.getFlowNo();
			}

			return "url@MyView.htm?WorkID=" + workID + "&o2=1" + paras;
		}

		//查看指定的节点表单需要FK_Node参数。
		if (this.getDoWhat().equals("MyFrm") == true)
		{
			if (this.getNodeID() == 0)
			{
				return "err@执行 MyFrm 需要NodeID/FK_Node参数.";
			}

			long workID = this.GenerWorkIDByEntityPK();
			if (workID == 0)
			{
				return "err@执行MyView不需要NodeID/FK_Node参数.";
			}

			return "url@MyFrm.htm?FK_Flow=" + flowNo + "&FK_Node=" + this.getNodeID() + "&WorkID=" + workID + "&o2=1" + paras;
		}

		//发起页面,或者调用发起流程.
		if (this.getDoWhat().equals(DoWhatList.Start) == true || this.getDoWhat().equals("StartFlow"))
		{
			if (this.getFlowNo() == null)
			{
				return "url@Start.htm?Token=" + token;
			}

			//实体编号,启动指定实体编号隶属的工作流程ID.
			long workID = this.GenerWorkIDByEntityPK();
			if (workID == 0)
			{
				return "url@MyFlow.htm?FK_Flow=" + flowNo + paras + "&FK_Node=" + nodeID;
			}

			// 是否可以执行当前的工作.
			if (Dev2Interface.Flow_IsCanDoCurrentWork(workID, WebUser.getNo()) == true)
			{
				return "url@MyFlow.htm?FK_Flow=" + flowNo + paras + "&WorkID=" + workID;
			}

			return "url@MyView.htm?FK_Flow=" + flowNo + paras + "&WorkID=" + workID;
		}

		//发起表单.
		if (this.getDoWhat().equals("StartFrm"))
		{
			long oid = this.getOID();
			if (oid == 0)
			{
				GEEntityOID gn = new GEEntityOID(this.getFrmID());
				oid = DBAccess.GenerOIDByGUID();
				try
				{
					gn.SaveAsOID(oid);
				}
				catch (RuntimeException ex)
				{
					gn.CheckPhysicsTable();
					gn.SaveAsOID(oid);
				}
			}
			return "url@/WF/CCForm/Frm.htm?FrmID=" + this.getFrmID() + "&OID=" + oid + paras  + "&UserNo=" + getUserNo();
		}

		//vue移动端-处理工作
		if (this.getDoWhat().equals(DoWhatList.CCMobileDealWork) == true)
		{
			if (DataType.IsNullOrEmpty(this.getFlowNo()) || this.getWorkID() == 0)
			{
				return "err@参数 FK_Flow 或者 WorkID 为Null 。";
			}
			return "url@CCMobileMyFlow.htm?FK_Flow=" + flowNo + "&WorkID=" + workid + "&o2=1" + paras;
		}

		//处理工作.
		if (this.getDoWhat().equals(DoWhatList.DealWork) == true || this.getDoWhat().equals(DoWhatList.MyFlow) == true)
		{
			return "url@MyFlow.htm?FK_Flow=" + flowNo + "&WorkID=" + workid + "&o2=1" + paras;
		}
		//我的抄送.
		if (this.getDoWhat().equals(DoWhatList.MyCC) == true)
		{
			return "url@MyCC.htm?FK_Flow=" + flowNo + "&WorkID=" + workid + "&o2=1" + paras + "&FK_Node=" + nodeID;
		}

		// 抄送列表
		if (this.getDoWhat().equals(DoWhatList.CCList) == true)
			return "url@CC.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

		//已完成
		if (this.getDoWhat().equals(DoWhatList.Complete) == true)
			return "url@Complete.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

		// 最近发起
		if (this.getDoWhat().equals(DoWhatList.StartEarlyer) == true)
			return "url@StartEarlyer.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

		//监控
		if (this.getDoWhat().equals(DoWhatList.Watchdog) == true)
		{
			if (DataType.IsNullOrEmpty(this.getEnsName()))
				return "url@Watchdog.htm?Token=" + token + "&UserNo=" + WebUser.getNo();
			else
				return "url@Watchdog.htm?" + paras.substring(1);
		}

		//流程设计器
		if (this.getDoWhat().equals(DoWhatList.Flows) == true)
		{
			return "url@Portal/FlowTree.htm?Token=" + token + "&UserNo=" + WebUser.getNo();
		}
		//表单设计器
		if (this.getDoWhat().equals(DoWhatList.Frms) == true)
		{
			return "url@Portal/FrmTree.htm?Token=" + token + "&UserNo=" + WebUser.getNo();
		}

		//请求在途.
		if (this.getDoWhat().equals(DoWhatList.Runing) == true)
		{
			return "url@Runing.htm?FK_Flow=" + flowNo;
		}

		//请求首页.
		if (this.getDoWhat().equals("Home") == true)
		{
			return "url@Home.htm?FK_Flow=" + flowNo;
		}


		//批处理。
		if (this.getDoWhat().equals("Batch") == true)
		{
			return "url@Batch.htm?Token=" + token;
		}
		//草稿
		if (this.getDoWhat().equals(DoWhatList.Draft) == true)
		{
			return "url@Draft.htm?FK_Flow=" + flowNo;
		}


        //我发起的
        if (this.getDoWhat().equals(DoWhatList.MyStartFlows) == true)
            return "url@./Comm/Search.htm?EnsName=BP.WF.Data.MyStartFlows&Token=" + token;

		//我发起的ForVue3
		if (this.getDoWhat().equals(DoWhatList.MyStartFlowsForVue3) == true)
			return "url@./MyStartFlows?Token=" + token;

        //我审批的（当前登录人处理的所有流程）。
        if (this.getDoWhat().equals(DoWhatList.MyJoinFlows) == true)
            return "url@./Comm/Search.htm?EnsName=BP.WF.Data.MyJoinFlows&WFSta=all&Token=" + token;

        //我的流程分布
        if (this.getDoWhat().equals(DoWhatList.DistributedOfMy) == true)
            return "url@./RptSearch/DistributedOfMy.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

        //我的流程
        if (this.getDoWhat().equals(DoWhatList.SearchDataGrid) == true)
            return "url@SearchDataGrid.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

        //逾期流程
        if (this.getDoWhat().equals(DoWhatList.SearchDelays) == true)
            return "url@./Comm/Search.htm?EnsName=BP.WF.Data.Delays&Token=" + token;

        //会签主持人待办
        if (this.getDoWhat().equals(DoWhatList.HuiQianList) == true)
            return "url@HuiQianList.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

        //协作待办
        if (this.getDoWhat().equals(DoWhatList.TeamupList) == true)
            return "url@TeamupList.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

        //授权待办
        if (this.getDoWhat().equals(DoWhatList.AuthorList) == true)
            return "url@AuthorList.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

        //工作委托2019
        if (this.getDoWhat().equals(DoWhatList.AuthorTodolist2019) == true)
            return "url@AuthorTodolist2019.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

        //共享任务
        if (this.getDoWhat().equals(DoWhatList.TaskPoolSharing) == true)
            return "url@TaskPoolSharing.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

        //申请下来的任务
        if (this.getDoWhat().equals(DoWhatList.TaskPoolApply) == true)
            return "url@TaskPoolApply.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

        //未来任务
        if (this.getDoWhat().equals(DoWhatList.FutureTodolist) == true)
            return "url@FutureTodolist.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

		//我的设置
		if (this.getDoWhat().equals(DoWhatList.MySetting) == true)
			return "url@./Comm/RefFunc/En.htm?EnName=BP.Port.Emp&No=" + WebUser.getNo() + "&Token=" + token;

		//我的关注
		if (this.getDoWhat().equals(DoWhatList.MyFocus) == true)
			return "url@Focus.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

		if (this.getDoWhat().equals(DoWhatList.Message) == true)
		{
			return "url@Portal/Message.htm?Token=" + token;
		}

		//挂起的工作
		if (this.getDoWhat().equals(DoWhatList.HungupList) == true)
			return "url@HungupList.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

		//任务
		if (this.getDoWhat().equals(DoWhatList.Task) == true)
			return "url@/CCFast/Task/Task.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

		//知识库
		if (this.getDoWhat().equals(DoWhatList.Knowledge) == true)
			return "url@/CCFast/KnowledgeManagement/Default.htm?Token=" + token + "&UserNo=" + WebUser.getNo();

        //admin查看处理的所有流程。
        if (this.getDoWhat().equals(DoWhatList.GenerWorkFlowViews) == true)
            return "url@./Comm/Search.htm?EnsName=BP.WF.Data.GenerWorkFlowViews&WFSta=all&Token=" + token;



		//打开消息.
		if (this.getDoWhat().equals(DoWhatList.DealMsg) == true)
		{
			String guid = this.GetRequestVal("GUID");
			SMS sms = new SMS();
			sms.setMyPK(guid);
			sms.Retrieve();

			//判断当前的登录人员.
			if (!Objects.equals(WebUser.getNo(), sms.getSendToEmpNo()))
			{
				Dev2Interface.Port_Login(sms.getSendToEmpNo());
			}

			AtPara ap = new AtPara(sms.getAtPara());
			switch (sms.getMsgType())
			{
				case SMSMsgType.SendSuccess: // 发送成功的提示.

					if (Dev2Interface.Flow_IsCanDoCurrentWork(ap.GetValInt64ByKey("WorkID"), WebUser.getNo()) == true)
					{
						return "url@MyFlow.htm?FK_Flow=" + ap.GetValStrByKey("FK_Flow") + "&WorkID=" + ap.GetValStrByKey("WorkID") + "&o2=1" + paras;
					}
					else
					{
						return "url@MyView.htm?FK_Flow=" + ap.GetValStrByKey("FK_Flow") + "&WorkID=" + ap.GetValStrByKey("WorkID") + "&o2=1" + paras;
					}

				default: //其他的情况都是查看工作报告.
					return "url@MyView.htm?FK_Flow=" + ap.GetValStrByKey("FK_Flow") + "&WorkID=" + ap.GetValStrByKey("WorkID") + "&o2=1" + paras;
			}
		}
		if (this.getDoWhat().equals(DoWhatList.CCMobileHome) == true)
		{
			return "url@/FastMobilePortal/Default.htm";
		}
		if (this.getDoWhat().equals(DoWhatList.CCMobileStart) == true)
		{
			return "url@/CCMobile/MyFlow.htm?From=Start&FK_Flow=" + flowNo;
		}
		if (this.getDoWhat().equals(DoWhatList.Vue3Track) == true)
		{
			return "url@/Track.htm?FK_Flow=" + flowNo + "&WorkID=" + workid + "&Token=" + token;
		}
		return "warning@没有约定的标记:DoWhat=" + this.getDoWhat();
	}

		///#endregion 处理page接口.
	/**
	Author_InitLeft
	@return
	*/
	public final String Author_InitLeft()
	{
		String sql = "SELECT Auther,AuthName, FK_Flow,FlowName, COUNT(FK_Flow) as Num   FROM V_WF_AuthTodolist ";
		sql += "WHERE AutherToEmpNo = '" + WebUser.getNo() + "'";
		sql += " AND TakeBackDT >= '" + this.GetRequestVal("nowDate") + "'";
		sql += "     GROUP BY Auther, AuthName, FK_Flow, FlowName  ";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "Auther";
			dt.Columns.get(1).ColumnName = "AuthName";
			dt.Columns.get(2).ColumnName = "FK_Flow";
			dt.Columns.get(3).ColumnName = "FlowName";
			dt.Columns.get(4).ColumnName = "Num";
		}
		return Json.ToJson(dt);
	}
	/**
	 Author_InitDocs
	 @return
	 */
	public final String Author_InitDocs()
	{
		String sql = "SELECT *  FROM V_WF_AuthTodolist ";
		sql += "WHERE AutherToEmpNo = '" + WebUser.getNo() + "' and Auther ='" + this.GetRequestVal("author") + "' and FK_Flow ='" + this.GetRequestVal("flowNo") + "'";
		sql += "  AND TakeBackDT >= '" + this.GetRequestVal("nowDate") + "'";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			String columnName = "";
			for (DataColumn col : dt.Columns)
			{
				columnName = col.ColumnName.toUpperCase();
				switch (columnName)
				{
					case "AUTHER":
						col.ColumnName = "Auther";
						break;
					case "AUTHNAME":
						col.ColumnName = "AutherName";
						break;
					case "PWORKID":
						col.ColumnName = "PWorkID";
						break;
					case "FK_NODE":
						col.ColumnName = "FK_Node";
						break;
					case "FID":
						col.ColumnName = "FID";
						break;
					case "WORKID":
						col.ColumnName = "WorkID";
						break;
					case "AUTHERTOEMPNO":
						col.ColumnName = "AutherToEmpNo";
						break;
					case "TAKEBACKDT":
						col.ColumnName = "TakeBackDT";
						break;
					case "FK_FLOW":
						col.ColumnName = "FK_Flow";
						break;
					case "FLOWNAME":
						col.ColumnName = "FlowName";
						break;
					case "TITLE":
						col.ColumnName = "Title";
						break;
				}
			}
		}
		return Json.ToJson(dt);
	}

	/**
	 * 检查当前人员是否有权限处理当前工作
	 * @return
	 */
	public String Flow_IsCanDo() throws Exception {
		long worid = this.GetRequestValInt64("WorkID");
		boolean isCan = bp.wf.Dev2Interface.Flow_IsCanDoCurrentWork(worid, WebUser.getNo());
		bp.da.Log.DefaultLogWriteLine(LogType.Info, "Flow_IsCanDo:" + isCan);
		Hashtable ht = new Hashtable();
		ht.put("isCanDo", isCan);
		return bp.tools.Json.ToJson(ht);
	}
	public String getLinkFlowInfo() throws Exception{
		String billNo=this.GetRequestVal("BillNo");
		GenerWorkFlow gwf=new GenerWorkFlow();
		gwf.Retrieve(GenerWorkFlowAttr.BillNo,billNo);
		Hashtable ht=new Hashtable();
		ht.put("FlowNo",gwf.getFlowNo());
		ht.put("WorkID",gwf.getWorkID());
		return bp.tools.Json.ToJson(ht,false);
	}
}
