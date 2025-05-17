package bp.wf;

import bp.difference.StringHelper;
import bp.en.*;
import bp.da.*;
import bp.port.*;
import bp.web.*;
import bp.sys.*;
import bp.wf.template.*;
import bp.tools.*;
import bp.wf.template.sflow.*;
import bp.difference.*;
import net.sf.json.JSONObject;

import java.util.*;
import java.time.*;
import java.util.stream.Collectors;

/**
 WorkNode的附加类: 2020年06月09号
 1， 因为worknode的类方法太多，为了能够更好的减轻代码逻辑.
 2.  一部分方法要移动到这里来.
*/
public class WorkNodePlus
{
	public static void InitWorkerList_Ext(WorkNode currWN, WorkNode town, DataTable dt, int toNodeId, Date dtOfShould, Date dtOfWarning, long nextUsersFID, boolean isFenLiuToSubThread, long nextUsersWorkID) throws Exception {
		/* 如果有多个人员，就要考虑接收人是否记忆属性的问题。 */
		RememberMe rm = currWN.GetHisRememberMe(town.getHisNode());


			///#region 是否需要清空记忆属性.
		// 如果按照选择的人员处理，就设置它的记忆为空。2011-11-06 处理电厂需求 .
		if (rm != null && (town.getHisNode().getHisDeliveryWay() == DeliveryWay.BySelected || town.getHisNode().getItIsRememberMe() == false))
		{
			rm.setObjs("");
		}

		if (rm != null && currWN.getHisNode().getItIsFL())
		{
			rm.setObjs("");
		}

		if (currWN.ItIsHaveSubThreadGroupMark == false && rm != null && !Objects.equals(rm.getObjs(), ""))
		{
			/*检查接收人列表是否发生了变化,如果变化了，就要把有效的接收人清空，让其重新生成.*/
			String emps = "@";
			for (DataRow dr : dt.Rows)
			{
				emps += dr.getValue(0).toString() + "@";
			}

			if (!Objects.equals(rm.getEmps(), emps))
			{
				// 列表发生了变化.
				rm.setEmps(emps);
				rm.setObjs(""); //清空有效的接收人集合.
			}
		}

			///#endregion 是否需要清空记忆属性.

		String myemps = "";
		Emp emp = new Emp();
		int idx = 0;
		String sender = WebUser.getNo() + "," + WebUser.getName();
		String sqls = "";
		long workID = currWN.getWorkID();
		GenerWorkerList wl = new GenerWorkerList();
		String workids = ",";
		for (DataRow dr : dt.Rows)
		{
			String fk_emp = dr.getValue(0).toString();
			if (currWN.ItIsHaveSubThreadGroupMark == true)
			{
				/*如果有分组 Mark ,就不处理重复人员的问题.*/
			}
			else
			{
				// 处理人员重复的，不然会导致generworkerlist的pk错误。
				if (myemps.indexOf("@" + fk_emp + ",") != -1)
				{
					continue;
				}
				myemps += "@" + fk_emp + ",";
			}


				///#region 增加存储的优先级，防止数据库根据FK_Emp的人员编码自动排序，导致查询的结果和接收人的先后顺序不一致
			idx++;
			wl.setIdx(idx);

				///#endregion


				///#region 根据记忆是否设置该操作员可用与否。
			if (rm != null)
			{
				if (Objects.equals(rm.getObjs(), ""))
				{
					/*如果是空的.*/
					wl.setItIsEnable(true);
				}
				else
				{
					if (rm.getObjs().contains("@" + fk_emp + "@") == true)
					{
						wl.setItIsEnable(true); // 如果包含，就说明他已经有了
					}
					else
					{
						wl.setItIsEnable(false);
					}
				}
			}
			else
			{
				wl.setItIsEnable(false);
			}

				///#endregion 根据记忆是否设置该操作员可用与否。

			wl.setNodeID(toNodeId);
			wl.setEmpNo(fk_emp);
			wl.setFlowNo(town.getHisNode().getFlowNo());
			if (dt.Columns.size() == 3 && currWN.ItIsHaveSubThreadGroupMark)
			{
				//设置分组信息.
				Object val = dr.getValue(2);
				if (val == null)
				{
					throw new RuntimeException(bp.wf.Glo.multilingual("@分组标志不能为空。", "WorkNode", "empty_group_tags", new String[0]));
				}

				if (DataType.IsNullOrEmpty(val.toString()))
				{
					throw new RuntimeException(bp.wf.Glo.multilingual("@分组标志不能为空。", "WorkNode", "empty_group_tags", new String[0]));
				}

				wl.setGroupMark(val.toString());
				if (dt.Columns.size()== 4 && town.getHisNode().getHisFormType() == NodeFormType.SheetAutoTree)
				{
					wl.setFrmIDs(dr.getValue(3).toString());
					if (DataType.IsNullOrEmpty(dr.getValue(3).toString()))
					{
						throw new RuntimeException(bp.wf.Glo.multilingual("@接收人数据源不正确,表单IDs不能为空。", "WorkNode", "invalid_receiver_data_source", new String[0]));
					}
				}
			}

			wl.setFID(nextUsersFID);
			if (isFenLiuToSubThread)
			{
				/* 说明这是分流点向下发送
				 *  在这里产生临时的workid.
				 */
				wl.setWorkID(DBAccess.GenerOIDByGUID());
			}
			else
			{
				wl.setWorkID(nextUsersWorkID);
			}
			String IsEnable = "1";
			if (wl.getItIsEnable() == false)
			{
				IsEnable = "0";
			}
		   // sqls += " (" + wl.WorkID + ", '" + wl.EmpNo + "', " + wl.getNodeID() + ", " + wl.Idx + ", '"+wl.GetValByKey("AtPara")+"', "+ IsEnable + "),";
			sqls += "INSERT INTO WF_GenerWorkerlist (WorkID,FK_Emp,FK_Node,Idx,AtPara,IsEnable,WhoExeIt) VALUES(" + wl.getWorkID() + ",'" + wl.getEmpNo() + "'," + wl.getNodeID() + "," + wl.getIdx() + ",'" + wl.GetValByKey("AtPara") + "'," + IsEnable + "," + town.getHisNode().getWhoExeIt() + ") ;";
			if (workids.contains("," + wl.getWorkID() + ",") == false)
			{
				workids += wl.getWorkID() + ",";
			}
			// wl.Insert();
			//currWN.HisWorkerLists.AddEntity(wl);
		} //结束循环.


			///#region 执行更新批量 sql.
		DBAccess.RunSQL(sqls);
		workids = workids.substring(1);
		if (DataType.IsNullOrEmpty(workids) == false)
		{
			workids = workids.substring(0, workids.length() - 1);
		}
		// 执行更新sql.
		String sdt = "无";
		if (town.getHisNode().getHisCHWay() != CHWay.None)
		{
			sdt = DataType.SysDateTimeFormat(dtOfShould);
		}

		//更新基础数据.
		String sql = "UPDATE WF_GenerWorkerlist SET Sender='" + sender + "',DTOfWarning='" + DataType.SysDateTimeFormat(dtOfWarning) + "' ";
		sql += ",SDT='" + sdt + "'";
		sql += ",FK_Flow='" + currWN.getHisFlow().getNo() + "'";
		sql += ",RDT='" + DataType.getCurrentDateTimess() + "'";
		sql += ",FID='" + nextUsersFID + "'";
		sql += ",NodeName='" + town.getHisNode().getName() + "'";
		sql += ",IsEnable=1,IsRead=0,IsPass=0 ";
		sql += " WHERE WorkID IN(" + workids + ")  AND FK_Node=" + toNodeId;
		DBAccess.RunSQL(sql);

		//关联更新人员的名称与部门编号，部门名称。 FK_EmpText,FK_Dept
		String updateSQL = "";
		switch (SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
			case PostgreSQL:
			case HGDB:
				if (SystemConfig.getCCBPMRunModel()==CCBPMRunModel.SAAS) {
					updateSQL = " UPDATE WF_GenerWorkerlist SET FK_Dept = Port_Emp.FK_Dept,EmpName =Port_Emp.Name  FROM Port_Emp ";
					updateSQL += " WHERE Port_Emp.UserID = WF_GenerWorkerlist.FK_Emp AND WorkID IN(" + workids + ") AND FK_Node = " + toNodeId;
				}else
				{
					updateSQL = " UPDATE WF_GenerWorkerlist SET FK_Dept = Port_Emp.FK_Dept,EmpName =Port_Emp.Name  FROM Port_Emp ";
					updateSQL += " WHERE Port_Emp.No = WF_GenerWorkerlist.FK_Emp AND WorkID IN(" + workids + ") AND FK_Node = " + toNodeId;
				}
				break;
			case KingBaseR6:
			case Oracle:
			case DM:
			case GBASE8CByOracle:
				if (SystemConfig.getCCBPMRunModel()==CCBPMRunModel.SAAS) {
					updateSQL = "UPDATE WF_GenerWorkerlist E SET (FK_Dept,EmpName)=(SELECT U.FK_Dept,U.Name FROM Port_Emp U WHERE E.FK_Emp=U.UserID AND WorkID IN(" + workids + ") AND FK_Node =" + toNodeId + ") WHERE WorkID IN(" + workids + ") AND FK_Node =" + toNodeId;
				}else
				{
					updateSQL = "UPDATE WF_GenerWorkerlist E SET (FK_Dept,EmpName)=(SELECT U.FK_Dept,U.Name FROM Port_Emp U WHERE E.FK_Emp=U.No AND WorkID IN(" + workids + ") AND FK_Node =" + toNodeId + ") WHERE WorkID IN(" + workids + ") AND FK_Node =" + toNodeId;
				}
				break;
			default:
				if (SystemConfig.getCCBPMRunModel()==CCBPMRunModel.SAAS) {
					updateSQL = "UPDATE WF_GenerWorkerlist gl LEFT join Port_Emp emp on gl.FK_Emp = emp.UserID set gl.FK_Dept = emp.FK_Dept, gl.EmpName = emp.Name WHERE  WorkID IN(" + workids + ") AND FK_Node = " + toNodeId;
				}else
				{
					updateSQL = "UPDATE WF_GenerWorkerlist gl LEFT join Port_Emp emp on gl.FK_Emp = emp.No set gl.FK_Dept = emp.FK_Dept, gl.EmpName = emp.Name WHERE  WorkID IN(" + workids + ") AND FK_Node = " + toNodeId;
				}
				break;
		}
		DBAccess.RunSQL(updateSQL);

		//// 可以不删除.
		String deleteSQL = "DELETE FROM WF_GenerWorkerlist WHERE FK_Emp IS NULL OR FK_Emp=''";
		DBAccess.RunSQL(deleteSQL);

		//查询出数据.
		GenerWorkerLists gwls = new GenerWorkerLists();
		QueryObject qo = new QueryObject(gwls);
		qo.AddWhereIn("WorkID", "(" + workids + ")");
		qo.addAnd();
		qo.AddWhere("FK_Node", toNodeId);
		qo.DoQuery();

		currWN.HisWorkerLists = gwls;

			///#endregion 执行更新sql.


			///#region 执行对rm的更新。
		if (rm != null)
		{
			String empExts = "";
			String objs = "~"; // 有效的工作人员.
			String objsExt = "@"; // 有效的工作人员.
			for (GenerWorkerList mywl : currWN.HisWorkerLists.ToJavaList())
			{
				if (mywl.getItIsEnable() == false)
				{
					empExts += "<strike><font color=red>" + bp.wf.Glo.DealUserInfoShowModel(mywl.getEmpNo(), mywl.getEmpName()) + "</font></strike>、";
				}
				else
				{
					empExts += "~" + bp.wf.Glo.DealUserInfoShowModel(mywl.getEmpNo(), mywl.getEmpName());
				}

				if (mywl.getItIsEnable() == true)
				{
					objs += mywl.getEmpNo() + "~";
					objsExt += bp.wf.Glo.DealUserInfoShowModel(mywl.getEmpNo(), mywl.getEmpName());
				}
			}
			rm.setEmpsExt(empExts);
			rm.setObjs(objs);
			rm.setObjsExt(objsExt);
			currWN.HisRememberMe = rm;
		}

			///#endregion 执行对rm的更新。

	}
	/**
	 处理数据源.

	 @param fl
	 @param nd
	 @param track
	*/
	public static void AddNodeFrmTrackDB(Flow fl, Node nd, Track track, Work wk) throws Exception {
		//流程是否启用了版本控制
		if (fl.GetValBooleanByKey("IsEnableDBVer") == false)
		{
			return;
		}
		Paras ps = new Paras();
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		int ver = 1;
		//如果是单个表单.
		if (nd.getHisFormType() == NodeFormType.Develop || nd.getHisFormType() == NodeFormType.FoolTruck || nd.getHisFormType() == NodeFormType.ChapterFrm || nd.getHisFormType() == NodeFormType.FoolForm)
		{
			//接点表单保存NDXXRpt
			String frmID = "ND" + Integer.parseInt(fl.getNo()) + "Rpt";
			String dtlJson = AddNodeFrmDtlDB(nd.getNodeID(), track.getWorkID(), nd.getNodeFrmID());
			String athJson = AddNodeFrmAthDB(nd.getNodeID(), track.getWorkID(), nd.getNodeFrmID());
			//获取版本号
			ps.SQL = "SELECT MAX(Ver) From Sys_FrmDBVer WHERE FrmID=" + dbstr + "FrmID AND RefPKVal=" + dbstr + "RefPKVal";
			ps.Add("FrmID", frmID, false);
			ps.Add("RefPKVal", String.valueOf(track.getWorkID()), false);
			ver = DBAccess.RunSQLReturnValInt(ps, 0);
			ver = ver == 0 ? 1 : ver + 1;
			FrmDBVer.AddFrmDBTrack(ver, frmID, String.valueOf(track.getWorkID()), track.getMyPK(), wk.ToJson(true), dtlJson, athJson, false);
			return;
		}

		if (nd.getHisFormType() == NodeFormType.RefOneFrmTree)
		{
			FrmNode fn = new FrmNode(nd.getNodeID(), nd.getNodeFrmID());
			if (fn.getFrmSln() == FrmSln.Readonly)
			{
				return;
			}
			MapData md = nd.getMapData();
			//获取版本号
			ps.SQL = "SELECT MAX(Ver) From Sys_FrmDBVer WHERE FrmID=" + dbstr + "FrmID AND RefPKVal=" + dbstr + "RefPKVal";
			ps.Add("FrmID", md.getNo(), false);
			ps.Add("RefPKVal", String.valueOf(track.getWorkID()), false);
			ver = DBAccess.RunSQLReturnValInt(ps, 0);
			ver = ver == 0 ? 1 : ver + 1;

			if (md.getHisFrmType() == FrmType.ChapterFrm)
			{
				//获取字段
				MapAttrs attrs = md.getMapAttrs();
				for (MapAttr attr : attrs.ToJavaList())
				{
					if (attr.getUIVisible() == false)
					{
						continue;
					}
					FrmDBVer.AddKeyOfEnDBTrack(ver, nd.getNodeFrmID(), String.valueOf(track.getWorkID()), track.getMyPK(), wk.GetValStringByKey(attr.getKeyOfEn()), attr.getKeyOfEn());
				}
				String json = AddNodeFrmDtlDB(nd.getNodeID(), track.getWorkID(), nd.getNodeFrmID());
				String aths = AddNodeFrmAthDB(nd.getNodeID(), track.getWorkID(), nd.getNodeFrmID());
				FrmDBVer.AddFrmDBTrack(ver, nd.getNodeFrmID(), String.valueOf(track.getWorkID()), track.getMyPK(), null, json, aths, true);

				//获取控件类型是ChapterFrmLinkFrm的分组
				GroupFields groups = new GroupFields();
				groups.Retrieve(GroupFieldAttr.FrmID, md.getNo(), GroupFieldAttr.CtrlType, "ChapterFrmLinkFrm", null);
				for (GroupField group : groups.ToJavaList())
				{
					//获取表单数据
					GEEntity en = new GEEntity(group.getCtrlID(), track.getWorkID());
					json = AddNodeFrmDtlDB(nd.getNodeID(), track.getWorkID(), group.getCtrlID());
					aths = AddNodeFrmAthDB(nd.getNodeID(), track.getWorkID(), group.getCtrlID());
					if (en.getRow().containsKey("RDT"))
					{
						en.SetValByKey("RDT", "");
					}
					FrmDBVer.AddFrmDBTrack(ver, group.getCtrlID(), String.valueOf(track.getWorkID()), track.getMyPK(), en.ToJson(true), json, aths, false);
				}

				return;
			}
			String dtlJson = AddNodeFrmDtlDB(nd.getNodeID(), track.getWorkID(), nd.getNodeFrmID());
			String athJson = AddNodeFrmAthDB(nd.getNodeID(), track.getWorkID(), nd.getNodeFrmID());
			FrmDBVer.AddFrmDBTrack(ver, nd.getNodeFrmID(), String.valueOf(track.getWorkID()), track.getMyPK(), wk.ToJson(true), dtlJson, athJson, false);
			return;
		}

		//如果是多个表单.
		if (nd.getHisFormType() == NodeFormType.SheetTree)
		{
			FrmNodes fns = new FrmNodes(fl.getNo(), nd.getNodeID());

			for (FrmNode fn : fns.ToJavaList())
			{
				if (fn.getFrmSln() == FrmSln.Readonly)
				{
					continue;
				}

				//如果是禁用的.
				if (fn.getFrmEnableRole() == FrmEnableRole.Disable)
				{
					continue;
				}

				//如果是由参数启用的.
				if (fn.getFrmEnableRole() == FrmEnableRole.WhenHaveFrmPara)
				{
					continue;
				}

				GEEntityOID ge = new GEEntityOID(fn.getFrmID());
				ge.setOID(track.getWorkID());
				if (ge.RetrieveFromDBSources() == 0)
				{
					continue;
				}
				//获取版本号
				ps = new Paras();
				ps.SQL = "SELECT MAX(Ver) From Sys_FrmDBVer WHERE FrmID=" + dbstr + "FrmID AND RefPKVal=" + dbstr + "RefPKVal";
				ps.Add("FrmID", fn.getFrmID(), false);
				ps.Add("RefPKVal", String.valueOf(track.getWorkID()), false);
				ver = DBAccess.RunSQLReturnValInt(ps, 0);
				ver = ver == 0 ? 1 : ver + 1;

				MapData md = new MapData(fn.getFrmID());
				if (md.getHisFrmType() == FrmType.ChapterFrm)
				{
					//获取字段
					MapAttrs attrs = md.getMapAttrs();
					for (MapAttr attr : attrs.ToJavaList())
					{
						if (attr.getUIVisible() == false)
						{
							continue;
						}
						FrmDBVer.AddKeyOfEnDBTrack(ver, fn.getFrmID(), String.valueOf(track.getWorkID()), track.getMyPK(), ge.GetValStringByKey(attr.getKeyOfEn()), attr.getKeyOfEn());
					}
					String json = AddNodeFrmDtlDB(nd.getNodeID(), track.getWorkID(), fn.getFrmID());
					String aths = AddNodeFrmAthDB(nd.getNodeID(), track.getWorkID(), fn.getFrmID());
					FrmDBVer.AddFrmDBTrack(ver, fn.getFrmID(), String.valueOf(track.getWorkID()), track.getMyPK(), null, json, aths, true);

					//获取控件类型是ChapterFrmLinkFrm的分组
					GroupFields groups = new GroupFields();
					groups.Retrieve(GroupFieldAttr.FrmID, md.getNo(), GroupFieldAttr.CtrlType, "ChapterFrmLinkFrm", null);
					for (GroupField group : groups.ToJavaList())
					{
						//获取表单数据
						GEEntity en = new GEEntity(group.getCtrlID(), track.getWorkID());
						json = AddNodeFrmDtlDB(nd.getNodeID(), track.getWorkID(), group.getCtrlID());
						aths = AddNodeFrmAthDB(nd.getNodeID(), track.getWorkID(), group.getCtrlID());
						if (en.getRow().containsKey("RDT"))
						{
							en.SetValByKey("RDT", "");
						}
						FrmDBVer.AddFrmDBTrack(ver, group.getCtrlID(), String.valueOf(track.getWorkID()), track.getMyPK(), en.ToJson(true), json, aths, false);
					}

					return;
				}

				String dtlJson = AddNodeFrmDtlDB(nd.getNodeID(), track.getWorkID(), fn.getFrmID());
				String athJson = AddNodeFrmAthDB(nd.getNodeID(), track.getWorkID(), fn.getFrmID());
				FrmDBVer.AddFrmDBTrack(ver, fn.getFrmID(), String.valueOf(track.getWorkID()), track.getMyPK(), ge.ToJson(true), dtlJson, athJson, false);
			}
			return;
		}

		//if (fl.HisNodes)
		// FrmDBVer.AddFrmDBTrack()

	}
	/**
	 获取表单从表的数据集合

	 @param nodeId
	 @param workID
	 @param frmID
	 @return
	*/
	public static String AddNodeFrmDtlDB(int nodeId, long workID, String frmID) throws Exception {
		MapData mapData = new MapData(frmID);
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		MapDtls dtls = new MapDtls();
		dtls.Retrieve("FK_MapData", frmID, "FK_Node", 0, null);
		DataSet ds = new DataSet();
		DataTable dt = null;
		for (MapDtl dtl : dtls.ToJavaList())
		{
			String dtlRefPKVal = bp.wf.Dev2Interface.GetDtlRefPKVal(gwf.getWorkID(), gwf.getPWorkID(), gwf.getFID(), nodeId, dtl.getFrmID(), dtl);
			dt = CCFormAPI.GetDtlInfo(dtl, null, dtlRefPKVal);
			dt.TableName = dtl.getPTable();
			ds.Tables.add(dt);
		}
		return Json.ToJson(ds);
	}
	/**
	 获取表单附件的数据集合

	 @param nodeId
	 @param workID
	 @param frmID
	 @return
	*/
	public static String AddNodeFrmAthDB(int nodeId, long workID, String frmID) throws Exception {
		MapData mapData = new MapData(frmID);
		GenerWorkFlow gwf = new GenerWorkFlow(workID);
		FrmAttachments aths = new FrmAttachments();
		aths.Retrieve("FK_MapData", frmID, "FK_Node", 0, null);
		DataSet ds = new DataSet();
		FrmAttachmentDBs dbs = null;
		for (FrmAttachment ath : aths.ToJavaList())
		{
			dbs = bp.wf.CCFormAPI.GenerFrmAttachmentDBs(ath, String.valueOf(gwf.getWorkID()), ath.getMyPK(), gwf.getWorkID(), gwf.getFID(), gwf.getPWorkID(), true, nodeId, frmID);

			ds.Tables.add(dbs.ToDataTableField(ath.getNoOfObj()));
		}
		return Json.ToJson(ds);
	}

	/**
	 发送草稿实例 2020.10.27 fro 铁路局.
	*/
	public static void SendDraftSubFlow(WorkNode wn) throws Exception {
		//如果不允许发送草稿子流程.
		if (wn.getHisNode().getItIsSendDraftSubFlow() == false)
		{
			return;
		}

		//查询出来该流程实例下的所有草稿子流程.
		GenerWorkFlows gwfs = new GenerWorkFlows();
		gwfs.Retrieve(GenerWorkFlowAttr.PWorkID, wn.getWorkID(), GenerWorkFlowAttr.WFState, 1, null);

		//子流程配置信息.
		SubFlowHandGuide sf = null;

		//开始发送子流程.
		for (GenerWorkFlow gwfSubFlow : gwfs.ToJavaList())
		{
			//获得配置信息.
			if (sf == null || !Objects.equals(sf.getFlowNo(), gwfSubFlow.getFlowNo()))
			{
				String pkval = wn.getHisGenerWorkFlow().getFlowNo() + "_" + gwfSubFlow.getFlowNo() + "_0";
				sf = new SubFlowHandGuide();
				sf.setMyPK(pkval);
				sf.RetrieveFromDBSources();
			}

			//把草稿移交给当前人员. - 更新控制表.
			gwfSubFlow.setStarter(WebUser.getNo());
			gwfSubFlow.setStarterName(WebUser.getName());
			gwfSubFlow.Update();
			//把草稿移交给当前人员. - 更新工作人员列表.
			DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET FK_Emp='" + WebUser.getNo() + "',EmpName='" + WebUser.getName() + "' WHERE WorkID=" + gwfSubFlow.getWorkID());
			//更新track表.
			//DBAccess.RunSQL("UPDATE ND"+int.Parse(gwfSubFlow.FK_Flow) +"Track SET FK_Emp='" + WebUser.getNo() + "',FK_EmpText='" + WebUser.getName() + "' WHERE WorkID=" + gwfSubFlow.WorkID);

			//启动子流程. 并把两个字段，写入子流程.
			bp.wf.Dev2Interface.Node_SendWork(gwfSubFlow.getFlowNo(), gwfSubFlow.getWorkID(), null, null);
		}
	}
	/**
	 当要发送是检查流程是否可以允许发起.

	 @param flow 流程
	 @param wk 开始节点工作
	 @return
	*/
	public static boolean CheckIsCanStartFlow_SendStartFlow(Flow flow, Work wk) throws Exception {
		StartLimitRole role = flow.getStartLimitRole();
		if (role == StartLimitRole.None)
		{
			return true;
		}

		String sql = "";
		String ptable = flow.getPTable();

		if (role == StartLimitRole.ColNotExit)
		{
			/* 指定的列名集合不存在，才可以发起流程。*/
			if (DataType.IsNullOrEmpty(flow.getStartLimitPara()) == true)
			{
				throw new RuntimeException("err@流程发起限制规则出现错误,StartLimitRole.ColNotExit , 没有配置参数. ");
			}

			//求出原来的值.
			String[] strs = flow.getStartLimitPara().split("[,]", -1);
			String val = "";
			int paraLength = 0;
			for (String str : strs)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}
				try
				{
					val += wk.GetValStringByKey(str);
					paraLength++;
				}
				catch (RuntimeException ex)
				{
					throw new RuntimeException("@流程设计错误,您配置的检查参数(" + flow.getStartLimitPara() + "),中的列(" + str + ")已经不存在表单里.");
				}
			}


			//找出已经发起的全部流程.
			sql = "SELECT " + flow.getStartLimitPara() + " FROM " + ptable + " WHERE  WFState NOT IN(0,1) AND FlowStarter='" + WebUser.getNo() + "'";
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			for (DataRow dr : dt.Rows)
			{
				String v = "";
				for (int i = 0; i < paraLength; i++)
				{
					v += dr.getValue(i);
				}
				if (v.equals(val))
				{
					return false;
				}
			}
			return true;
		}

		// 配置的sql,执行后,返回结果是 0 .
		if (role == StartLimitRole.ResultIsZero)
		{
			sql = bp.wf.Glo.DealExp(flow.getStartLimitPara(), wk, null);
			if (DBAccess.RunSQLReturnValInt(sql, 0) == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}

		// 配置的sql,执行后,返回结果是 <> 0 .
		if (role == StartLimitRole.ResultIsNotZero)
		{
			sql = bp.wf.Glo.DealExp(flow.getStartLimitPara(), wk, null);
			if (DBAccess.RunSQLReturnValInt(sql, 0) != 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}

		//为子流程的时候，该子流程只能被调用一次.
		if (role == StartLimitRole.OnlyOneSubFlow)
		{
			sql = "SELECT PWorkID FROM WF_GenerWorkFlow WHERE WorkID=" + wk.getOID();
			String pWorkidStr = DBAccess.RunSQLReturnStringIsNull(sql, "0");
			if (Objects.equals(pWorkidStr, "0"))
			{
				return true;
			}

			sql = "SELECT Starter, RDT FROM WF_GenerWorkFlow WHERE PWorkID=" + pWorkidStr + " AND FK_Flow='" + flow.getNo() + "'";
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0 || dt.Rows.size() == 1)
			{
				return true;
			}

			String starter = dt.Rows.get(0).getValue("Starter").toString();
			String rdt = dt.Rows.get(0).getValue("RDT").toString();

			throw new RuntimeException(flow.getStartLimitAlert() + "@该子流程已经被[" + starter + "], 在[" + rdt + "]发起，系统只允许发起一次。");
		}

		return true;
	}
	/**
	 开始执行数据同步,在流程运动的过程中，
	 数据需要同步到不同的表里去.

	 @param fl 流程
	 @param gwf 待办数据
	 @param rpt 业务数据
	 @param currNode 当前节点
	 @param isStopFlow 是否流程结束
	*/
	public static String DTSData(Flow fl, GenerWorkFlow gwf, GERpt rpt, Node currNode, boolean isStopFlow) throws Exception {
		String msg = "";

		//判断同步类型.
		if (fl.getDTSWay() == DataDTSWay.None)
			return msg;

		//流程结束时候同步，但是流程没有结束.
		if (fl.getDTSWay() == DataDTSWay.WhenStopFlow && isStopFlow)
			return msg;

		//按照指定的节点同步.
		if (fl.getDTSWay() == DataDTSWay.AfterSpecifiedNode && fl.getDTSSpecNodes().equals(String.valueOf(currNode.getNodeID())) == false)
			return msg;

		SyncDatas ens = new SyncDatas();
		ens.Retrieve("FlowNo", fl.getNo());

		for (SyncData item : ens.ToJavaList())
		{
			if (item.getSyncType().equals("DBSrc") == true) { // 数据源
				msg += DTSData_DBSrc(item, fl, gwf, rpt, currNode, isStopFlow);
			}
			if (item.getSyncType().equals("APIOfSelf") == true) // 同步到API接口
			{
				msg += DTSData_APIOfSelf(item, fl, gwf, rpt, currNode, isStopFlow);
			}
		}
		return msg;
	}

	public static String DTSData_APIOfSelf(SyncData sd, Flow fl, GenerWorkFlow gwf, GERpt rpt, Node currNode, boolean isStopFlow) throws Exception {
		long workID = gwf.getWorkID();
		String nodeFrmID = currNode.getNodeFrmID();
		//推送的数据
		String info = "{";
		//推送的主表数据
		String mainTable = "";
		mainTable += "\"mainTable\":";
		mainTable += "{";
		MapAttrs mattrs = new MapAttrs(nodeFrmID);
		for (MapAttr attr : mattrs.ToJavaList())
		{
			String keyOfEn = attr.getKeyOfEn();
			if (keyOfEn.equals("Title") || keyOfEn.equals("BillNo"))
			{
				continue;
			}
			if (keyOfEn.equals("AtPara") || keyOfEn.equals("BillState"))
			{
				continue;
			}
			if (keyOfEn.equals("RDT") || keyOfEn.equals("OrgNo"))
			{
				continue;
			}
			if (keyOfEn.equals("FK_Dept") || keyOfEn.equals("FID"))
			{
				continue;
			}
			if (keyOfEn.equals("Starter") || keyOfEn.equals("StarterName"))
			{
				continue;
			}
			if (keyOfEn.equals("OID") || keyOfEn.equals("Rec"))
			{
				continue;
			}
			mainTable += "\"" + keyOfEn + "\":\"" + rpt.GetValStrByKey(keyOfEn) + "\",";
		}
		mainTable += "\"oid\":\"" + workID + "\"";
		mainTable += "}";

		//推送的从表数据
		String dtls = "[";
		String dtlData = "";

		MapDtls mapDtls = new MapDtls();
		mapDtls.Retrieve(MapDtlAttr.FK_MapData, nodeFrmID, null);
		for (MapDtl dtl : mapDtls.ToJavaList())
		{
			String dtlNo = dtl.getNo();
			dtlData += "{";
			dtlData += "\"dtlNo\":\"" + dtlNo + "\",";
			//多个从表的数据
			String dtlList = "[";
			//每一行数据
			String dtlOne = "";
			//每一行的字段数据
			String dtlKeys = "";
			//从表附件
			String dtlAths = "[";
			String dtlAth = "";

			MapAttrs dtlAttrs = new MapAttrs(dtlNo);
			GEDtls geDtls = new GEDtls(dtlNo);
			geDtls.Retrieve(GEDtlAttr.RefPK, workID, null);
			for (GEDtl geDtl : geDtls.ToJavaList())
			{
				dtlKeys = "{";
				for (MapAttr attr : dtlAttrs.ToJavaList())
				{
					String keyOfEn = attr.getKeyOfEn();
					if (keyOfEn.equals("OID") || keyOfEn.equals("RefPK"))
					{
						continue;
					}
					if (keyOfEn.equals("FID") || keyOfEn.equals("RDT"))
					{
						continue;
					}
					if (keyOfEn.equals("Rec") || keyOfEn.equals("AthNum"))
					{
						continue;
					}
					dtlKeys += "\"" + keyOfEn + "\":\"" + geDtl.GetValByKey(keyOfEn) + "\",";
				}
				if (!DataType.IsNullOrEmpty(dtlKeys))
				{
					dtlKeys = dtlKeys.substring(0, dtlKeys.length() - 1);
				}
				dtlKeys += "}";

				FrmAttachmentDBs attachmentDBs = new FrmAttachmentDBs();
				attachmentDBs.Retrieve(FrmAttachmentDBAttr.FK_MapData, dtlNo, FrmAttachmentDBAttr.RefPKVal, geDtl.getOID(), null);
				for (FrmAttachmentDB frmAttachmentDB : attachmentDBs.ToJavaList())
				{
					dtlAth += "{";
					dtlAth += "\"fileFullName\":\"" + frmAttachmentDB.getFileFullName() + "\",";
					dtlAth += "\"fileName\":\"" + frmAttachmentDB.getFileName() + "\",";
					dtlAth += "\"sort\":\"" + frmAttachmentDB.getSort() + "\",";
					dtlAth += "\"fileExts\":\"" + frmAttachmentDB.getFileExts() + "\",";
					dtlAth += "\"rdt\":\"" + frmAttachmentDB.getRDT() + "\",";
					dtlAth += "\"rec\":\"" + frmAttachmentDB.getRec() + "\",";
					dtlAth += "\"myPK\":\"" + frmAttachmentDB.getMyPK() + "\",";
					dtlAth += "\"recName\":\"" + frmAttachmentDB.getRecName() + "\",";
					dtlAth += "\"fk_dept\":\"" + frmAttachmentDB.getDeptNo() + "\",";
					dtlAth += "\"fk_deptName\":\"" + frmAttachmentDB.getDeptName() + "\"";
					dtlAth += "},";
				}
				if (!DataType.IsNullOrEmpty(dtlAth))
				{
					dtlAth = dtlAth.substring(0, dtlAth.length() - 1);
				}
				dtlAth += "]";
				dtlOne += "{";
				dtlOne += "\"dtlData\":" + dtlKeys + ",";
				dtlOne += "\"dtlAths\":[" + dtlAth + "";
				dtlOne += "},";
			}
			if (!DataType.IsNullOrEmpty(dtlOne))
			{
				dtlOne = dtlOne.substring(0, dtlOne.length() - 1);
			}
			dtlList += dtlOne;
			dtlList += "]";

			dtlData += "\"dtl\":" + dtlList + "";
			dtlData += "},";
		}
		if (!DataType.IsNullOrEmpty(dtlData))
		{
			dtlData = dtlData.substring(0, dtlData.length() - 1);
		}
		dtls += dtlData;
		dtls += "]";

		//附件数据
		String aths = "[";
		String ath = "";

		FrmAttachments attachments = new FrmAttachments();
		attachments.Retrieve(FrmAttachmentAttr.FK_MapData, nodeFrmID, FrmAttachmentAttr.FK_Node, 0, null);
		for (FrmAttachment attachment : attachments.ToJavaList())
		{
			FrmAttachmentDBs dbs = new FrmAttachmentDBs();
			dbs.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, attachment.getMyPK(), FrmAttachmentDBAttr.FK_MapData, nodeFrmID, FrmAttachmentDBAttr.RefPKVal, workID, null);
			if (dbs.size() <= 0)
			{
				continue;
			}
			ath += "{";
			ath += "\"attachmentid\":\"" + attachment.getMyPK() + "\",";

			String athdb = "";
			for (FrmAttachmentDB db : dbs.ToJavaList())
			{
				athdb += "{";
				athdb += "\"fileFullName\":\"" + db.getFileFullName() + "\",";
				athdb += "\"fileName\":\"" + db.getFileName() + "\",";
				athdb += "\"sort\":\"" + db.getSort() + "\",";
				athdb += "\"fileExts\":\"" + db.getFileExts() + "\",";
				athdb += "\"rdt\":\"" + db.getRDT() + "\",";
				athdb += "\"myPK\":\"" + db.getMyPK() + "\",";
				athdb += "\"refPKVal\":\"" + db.getRefPKVal() + "\",";
				athdb += "\"rec\":\"" + db.getRec() + "\",";
				athdb += "\"recName\":\"" + db.getRecName() + "\",";
				athdb += "\"fk_dept\":\"" + db.getDeptNo() + "\",";
				athdb += "\"fk_deptName\":\"" + db.getDeptName() + "\"";
				athdb += "},";
			}
			if (!DataType.IsNullOrEmpty(athdb))
			{
				athdb = athdb.substring(0, athdb.length() - 1);
			}
			ath += "\"athdb\":" + athdb + "";
			ath += "},";
		}
		if (!DataType.IsNullOrEmpty(ath))
		{
			ath = ath.substring(0, ath.length() - 1);
		}
		aths += ath;
		aths += "]";

		info += mainTable;
		info += ",\"dtls\":" + dtls;
		info += ",\"aths\":" + aths;
		info += "}";

		String apiUrl = sd.getAPIUrl();
		Hashtable headerMap = new Hashtable();

		//设置返回值格式
		headerMap.put("Content-Type", "application/json");
		//设置token，用于接口校验
		String token = WebUser.getToken();
		if (DataType.IsNullOrEmpty(token) == true || token.contains(" "))
			throw new Exception("err@当前Token无效，请重新登录。");

		if (token.length() <= 6)
			throw new Exception("err@非法的Token. [Token:" + token + "]");

		headerMap.put("Authorization", token);
		//执行POST
		String postData = PubGlo.HttpPostConnect(apiUrl, info, "POST", true);
		JSONObject res = JSONObject.fromObject(postData);
		if (!res.get("code").toString().equals("200"))
		{
			Log.DebugWriteInfo("同步失败:WebAPI请求地址：" + apiUrl + ",请求内容：" + postData);
			Log.DebugWriteInfo("失败数据:" + info);
			throw new Exception("err@执行API接口失败，接口URL[" + apiUrl + "]，请求方式[POST]，请求数据[" + info + "]，响应数据[" + postData + "]");
		}

		return "同步流程数据到API接口[" + apiUrl + "]成功.";

	}

	public static String DTSData_DBSrc(SyncData sd, Flow fl, GenerWorkFlow gwf, GERpt rpt, Node currNode, boolean isStopFlow) throws Exception {
		//获取同步的字段.
		SyncDataFields sfs = new SyncDataFields();
		sfs.Retrieve("RefPKVal", sd.getMyPK());

		String ptbale = sd.getPTable();
		String tablePKName = sd.getTablePKName();
		long workID = gwf.getWorkID();

		String sql = "SELECT count(*) as Num  FROM " + ptbale + " WHERE " + tablePKName + "='" + workID + "'";
		SFDBSrc src = new SFDBSrc(sd.getDBSrc());
		int num = src.RunSQLReturnValInt(sql);
		if (num == 0)
		{
			if (sd.getTablePKType().toLowerCase().contains("char"))
				sql = "INSERT INTO " + ptbale + " (" + tablePKName + ") VALUES('" + workID + "')";
			else
				sql = "INSERT INTO " + ptbale + " (" + tablePKName + ") VALUES(" + workID + ")";
			src.RunSQL(sql);
		}
		Entity frmData = null;
		if (sd.getFrmID().equals(rpt.getClassID()) == true)
			frmData = rpt;

		sql = "UPDATE " + ptbale + " SET ";
		for (SyncDataField sf : sfs.ToJavaList())
		{
			String toFieldKey = sf.getToFieldKey();
			String attrKey = sf.getAttrKey();
			if (DataType.IsNullOrEmpty(toFieldKey) == true)
				continue;
			//获得数据.
			String str = frmData.GetValStringByKey(toFieldKey);
			if (sf.GetValStringByKey("AttrType").equals("1") == true)
				sql += attrKey + "= '" + str + "',"; //string类型的.
			else
			{
				if (DataType.IsNullOrEmpty(str) == true)
					continue;
				sql += attrKey + "= " + str + ",";
			}
		}
		
		sql = sql.substring(0, sql.length() - 1);
		if (sd.getTablePKType().contains("char") == true || sd.getTablePKType().equals("1") == true)
			sql += " WHERE " + sd.GetValStrByKey("TablePKName") + " ='" + workID + "'";
		else
			sql += " WHERE " + sd.GetValStrByKey("TablePKName") + "=" + workID;
		src.RunSQL(sql);
		return "同步流程数据到:" + sd.getNote() + ",成功.";
	}
	/**
	 处理协作模式下的删除规则

	 @param nd 节点
	 @param gwf
	*/
	public static String GenerWorkerListDelRole(Node nd, GenerWorkFlow gwf) throws Exception {
		String emps="";
		if (nd.getGenerWorkerListDelRole() == 0)
		{
			return emps;
		}

		//按照部门删除,同部门下的人员+兼职部门.
		if (nd.getGenerWorkerListDelRole() == 1 || nd.getGenerWorkerListDelRole() == 3 || nd.getGenerWorkerListDelRole() == 4)
		{
			//定义本部门的人员. 主部门+兼职部门.
			String sqlUnion = "";
			if (nd.getGenerWorkerListDelRole() == 1)
			{
				sqlUnion += " SELECT " + bp.sys.base.Glo.getUserNoWhitOutAS() + " as FK_Emp FROM Port_Emp WHERE FK_Dept='" + WebUser.getDeptNo() + "' ";
				sqlUnion += " UNION ";
				sqlUnion += " SELECT FK_Emp FROM Port_DeptEmp WHERE FK_Dept='" + WebUser.getDeptNo() + "'";
			}

			//主部门的人员.
			if (nd.getGenerWorkerListDelRole() == 3)
			{
				sqlUnion += " SELECT " + bp.sys.base.Glo.getUserNo() + " FROM Port_Emp WHERE FK_Dept='" + WebUser.getDeptNo() + "' ";
			}

			//兼职部门的人员.
			if (nd.getGenerWorkerListDelRole() == 4)
			{
				sqlUnion += " SELECT FK_Dept FROM Port_DeptEmp WHERE FK_Dept='" + WebUser.getDeptNo() + "'";
			}

			//获得要删除的人员.
			String sql = " SELECT FK_Emp,EmpName FROM WF_GenerWorkerlist WHERE ";
			sql += " WorkID=" + gwf.getWorkID() + " AND FK_Node=" + gwf.getNodeID() + " AND IsPass=0 ";
			sql += " AND FK_Emp IN (" + sqlUnion + ")";

			//获得要删除的数据.
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			for (int i = 0; i < dt.Rows.size(); i++)
			{
				String empNo = dt.Rows.get(i).getValue(0).toString();
				if (empNo.equals(WebUser.getNo()) == true)
				{
					continue;
				}
				String empName = dt.Rows.get(i).getValue(1).toString();
				emps+=empNo+","+empName+"@";
				sql = "UPDATE WF_GenerWorkerlist SET IsPass=1 WHERE WorkID=" + gwf.getWorkID() + " AND FK_Node=" + gwf.getNodeID() + " AND FK_Emp='" + empNo + "'";
				DBAccess.RunSQL(sql);
			}
		}

		//按照角色删除,同角色的人员.
		if (nd.getGenerWorkerListDelRole() == 2)
		{
			//1. 求出来: 当前人员的角色集合与节点角色集合的交集， 表示：当前人员用这些角色做了这个节点的事情.
			//获得当前节点使用的岗位, 首先从临时的变量里找（动态的获取的）,没有就到 NodeStation 里找.
			String temp = gwf.GetParaString("NodeStas" + gwf.getNodeID(), ""); //这个变量是上一个节点通过字段选择出来的.
			String stasSQLIn = "";
			if (DataType.IsNullOrEmpty(temp) == false)
			{
				stasSQLIn = DataType.DealFromatSQLWhereIn(temp);
			}
			else
			{
				DataTable dtStas = DBAccess.RunSQLReturnTable("SELECT FK_Station FROM WF_NodeStation WHERE FK_Node=" + nd.getNodeID());
				if (dtStas.Rows.size() == 0)
				{
					throw new RuntimeException("err@执行按照岗位删除人员待办出现错误，没有找到节点" + nd.getNodeID() + "-" + nd.getName() + ",绑定的岗位.");
				}

				String strs = "";
				for (DataRow dr : dtStas.Rows)
				{
					strs += ",'" + dr.getValue(0).toString() + "'";
				}
				stasSQLIn = strs.substring(1);
			}
			if (DataType.IsNullOrEmpty(stasSQLIn) == true)
			{
				throw new RuntimeException("err@没有找到当前节点使用的岗位集合.");
			}


			//求出来我使用的岗位集合.
			String sqlGroupMy = ""; //我使用岗位.
			sqlGroupMy = "SELECT FK_Station FROM Port_DeptEmpStation  WHERE FK_Station IN (" + stasSQLIn + ") AND FK_Emp='" + WebUser.getNo() + "'";

			DataTable dtGroupMy = DBAccess.RunSQLReturnTable(sqlGroupMy);
			if (dtGroupMy.Rows.isEmpty()) {
				throw new RuntimeException("err@发送失败，当前人员 [" + WebUser.getNo() +  "]， 没有对应岗位 [" + stasSQLIn + "]，请检查配置");
			}
			String stasGroupMy = ""; //我使用的岗位.
			for (DataRow dr : dtGroupMy.Rows)
			{
				stasGroupMy += ",'" + dr.getValue(0).toString() + "'";
			}

			stasGroupMy = stasGroupMy.substring(1);


			//2. 遍历: 当前的操作员，一个个的判断是否可以删除.
			GenerWorkerLists gwls = new GenerWorkerLists();
			gwls.Retrieve(GenerWorkerListAttr.WorkID, gwf.getWorkID(), GenerWorkerListAttr.FK_Node, nd.getNodeID(), null);

			for (GenerWorkerList item : gwls.ToJavaList())
			{
				if (item.getItIsEnable() == false)
				{
					continue;
				}
				if (item.getEmpNo().equals(WebUser.getNo()) == true)
				{
					continue; //要把自己排除在外.
				}

				String sqlGroupUser = "SELECT FK_Station FROM Port_DeptEmpStation WHERE FK_Station IN (" + stasSQLIn + ") AND FK_Emp='" + item.getEmpNo() + "'";
				DataTable dtGroupUser = DBAccess.RunSQLReturnTable(sqlGroupUser);

				// 判断  sqlGroupMy  >= sqlGroupUser  是否包含,如果包含，就是删除对象.
				boolean isCanDel = true;
				for (DataRow dr : dtGroupUser.Rows)
				{
					String staNo = "'" + dr.getValue(0).toString() + "'";
					if (stasGroupMy.contains(staNo) == false)
					{
						isCanDel = false;
					}
				}

				//符合删除的条件.
				if (isCanDel == true)
				{
					//item.setItIsEnable(false);
					item.setPassInt(1);
					item.Update();
					emps+=item.getEmpNo()+","+item.getEmpName()+"@";
				}
			}

			// 地瓜土豆问题.

				///#region 3 检查同角色的人员是否有交集: 潘茄的人,马铃薯的人，都分别审批了，需要删除 潘茄+马铃薯角色的人.
			// 3.1 找出来处理人中，用到人角色集合， 就是说已经消耗掉的角色集合.
			String sql = "SELECT B.FK_Station,A.FK_Emp FROM WF_GenerWorkerlist A, Port_DeptEmpStation B ";
			sql += " WHERE A.FK_Emp=B.FK_Emp AND B.FK_Station IN (" + stasSQLIn + ") AND A.WorkID=" + gwf.getWorkID() + " AND A.FK_Node=" + nd.getNodeID();
			sql += " AND (A.IsPass=1 OR A.FK_Emp='" + WebUser.getNo() + "') ";

			DataTable dtStationsUsed = DBAccess.RunSQLReturnTable(sql);
			String stasUseed = "";
			for (DataRow dr : dtStationsUsed.Rows)
			{
				stasUseed += ",'" + dr.getValue(0).toString() + "'";
			}

			// 3.2 扫描剩余的待办人员，这些待办的人员的使用的本节点的角色集合 是否 在消耗掉的角色集合中，如果有，就删除他的待办.
			gwls = new GenerWorkerLists();
			gwls.Retrieve("WorkID", gwf.getWorkID(), "IsPass", 0, null);
			for (GenerWorkerList item : gwls.ToJavaList())
			{
				if (item.getEmpNo().equals(WebUser.getNo()) == true)
				{
					continue;
				}

				//未处理的人的角色集合.
				String sqlGroupUser = "SELECT FK_Station FROM Port_DeptEmpStation A  WHERE  FK_Station IN (" + stasSQLIn + ") AND FK_Emp='" + item.getEmpNo() + "'";
				DataTable dtGroupUser = DBAccess.RunSQLReturnTable(sqlGroupUser);

				// 判断  sqlGroupMy  >= sqlGroupUser  是否包含,如果包含，就是删除对象.
				boolean isCanDel = true;
				for (DataRow dr : dtGroupUser.Rows)
				{
					String staNo = "'" + dr.getValue(0).toString() + "'";
					if (stasUseed.contains(staNo) == false)
					{
						isCanDel = false;
					}
				}

				//符合删除的条件.
				if (isCanDel == true)
				{
					//item.setItIsEnable(false);
					item.setPassInt(1);
					item.Update();
					emps+=item.getEmpNo()+","+item.getEmpName()+"@";
				}
			}

				///#endregion  检查同角色的人员是否有交集: 潘茄的人,马铃薯的人，都分别审批了，需要删除 潘茄+马铃薯角色的人.

		}
		return emps;
	}
	/**
	 处理发送返回，断头路节点.
	*/
	public static WorkNode IsSendBackNode(WorkNode wn) throws Exception {
		if (wn.getHisNode().getItIsSendBackNode() == false)
		{
			return wn; //如果不是断头路节点，就让其返回.
		}
		if (wn.getHisGenerWorkFlow().getWFState() == WFState.ReturnSta)
		{
			//是退回状态且原路返回的情况
			String sql = "SELECT NDFrom, EmpFrom, EmpFromT ";
			sql += " FROM ND" + Integer.parseInt(wn.getHisGenerWorkFlow().getFlowNo()) + "Track";
			sql += " WHERE WorkID=" + wn.getWorkID() + " AND (ActionType=2 OR ActionType=201) ORDER BY RDT DESC";
			DataTable mydt = DBAccess.RunSQLReturnTable(sql);
			if (mydt.Rows.size() != 0 && wn.getHisGenerWorkFlow().GetParaBoolen("IsBackTracking") == true)
			{
				wn.JumpToNode = new Node(Integer.parseInt(mydt.Rows.get(0).getValue(0).toString()));
				wn.JumpToEmp = mydt.Rows.get(0).getValue(1).toString();
				return wn;
			}

		}
		if (wn.getHisNode().getHisToNDNum() != 0)
		{
			throw new RuntimeException("err@流程设计错误:当前节点是发送自动返回节点，但是当前节点不能有到达的节点.");
		}

		if (wn.getHisNode().getHisRunModel() != RunModel.Ordinary)
		{
			throw new RuntimeException("err@流程设计错误:只能是线性节点才能设置[发送并返回]属性,当前节点是[" + wn.getHisNode().getHisRunModel().toString() + "]");
		}

		//判断是否是最后一个人？
		boolean isLastOne = false;
		GenerWorkerLists gwls = new GenerWorkerLists();
		gwls.Retrieve(GenerWorkerListAttr.WorkID, wn.getWorkID(), GenerWorkerListAttr.FK_Node, wn.getHisNode().getNodeID(), GenerWorkerListAttr.IsPass, 0, null);
		if (gwls.size() == 1)
		{
			isLastOne = true; //如果只有一个，本人就是lastOne.
		}

		//WorkNode wn= this.GetPreviousWorkNode();
		//this.JumpToEmp = wn.getHisWork().Rec; //对于绑定的表单有问题.
		//this.JumpToNode = wn.getHisNode();

		if (isLastOne == true || wn.getHisNode().getTodolistModel() == TodolistModel.QiangBan)
		{
			String ptable = "ND" + Integer.parseInt(wn.getHisFlow().getNo()) + "Track";

			String mysql = "";
			if (wn.getHisNode().getItIsSubThread() == true)
			{
				mysql = "SELECT NDFrom,EmpFrom FROM " + ptable + " WHERE (WorkID =" + wn.getWorkID() + " AND FID=" + wn.getHisGenerWorkFlow().getFID() + ") AND ActionType!= " + ActionType.UnSend.getValue() + " AND NDTo = " + wn.getHisNode().getNodeID() + " AND(NDTo != NDFrom) AND NDFrom In(Select Node From WF_Direction Where ToNode=" + wn.getHisNode().getNodeID() + " AND FK_Flow='" + wn.getHisFlow().getNo() + "') ORDER BY RDT DESC";
			}
			else
			{
				mysql = "SELECT NDFrom,EmpFrom FROM " + ptable + " WHERE WorkID =" + wn.getWorkID() + " AND ActionType!= " + ActionType.UnSend.getValue() + " AND NDTo = " + wn.getHisNode().getNodeID() + " AND(NDTo != NDFrom) AND NDFrom In(Select Node From WF_Direction Where ToNode=" + wn.getHisNode().getNodeID() + " AND FK_Flow='" + wn.getHisFlow().getNo() + "') ORDER BY RDT DESC";
			}

			DataTable mydt = DBAccess.RunSQLReturnTable(mysql);
			if (mydt.Rows.size() == 0)
			{
				throw new RuntimeException("系统错误，没有找到上一个节点.");
			}

			wn.JumpToEmp = mydt.Rows.get(0).getValue(1).toString();
			int priNodeID = Integer.parseInt(mydt.Rows.get(0).getValue(0).toString());
			wn.JumpToNode = new Node(priNodeID);

			//清除选择，防止在自动发送到该节点上来.
			wn.getHisGenerWorkFlow().setParasToNodes("");
			wn.getHisGenerWorkFlow().DirectUpdate();

			//清除上次发送的选择,不然下次还会自动发送到当前的节点上来.
			mysql = "DELETE FROM WF_SelectAccper WHERE FK_Node=" + wn.JumpToNode.getNodeID() + " AND WorkID=" + wn.getWorkID();
			DBAccess.RunSQL(mysql);
		}
		return wn;
	}
	/**
	 处理 askfor 状态

	 @param wn
	*/
	public static SendReturnObjs DealAskForState(WorkNode wn) throws Exception {
		/*如果是加签状态, 就判断加签后，是否要返回给执行加签人.*/
		GenerWorkerLists gwls = new GenerWorkerLists();
		gwls.Retrieve(GenerWorkerListAttr.FK_Node, wn.getHisNode().getNodeID(), GenerWorkerListAttr.WorkID, wn.getWorkID(), null);

		boolean isDeal = false;
		AskforHelpSta askForSta = AskforHelpSta.AfterDealSend;
		for (GenerWorkerList item : gwls.ToJavaList())
		{
			if (item.getPassInt() == AskforHelpSta.AfterDealSend.getValue())
			{
				/*如果是加签后，直接发送就不处理了。*/
				isDeal = true;
				askForSta = AskforHelpSta.AfterDealSend;

				// 更新workerlist, 设置所有人员的状态为已经处理的状态,让它走到下一步骤去.
				DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=1 WHERE FK_Node=" + wn.getHisNode().getNodeID() + " AND WorkID=" + wn.getWorkID());

				//写入日志.
				wn.AddToTrack(ActionType.ForwardAskfor, item.getEmpNo(), item.getEmpName(), wn.getHisNode().getNodeID(), wn.getHisNode().getName(), bp.wf.Glo.multilingual("加签后向下发送，直接发送给下一步处理人。", "WorkNode", "send_to_next"));

			}

			if (item.getPassInt() == AskforHelpSta.AfterDealSendByWorker.getValue())
			{
				/*如果是加签后，在由我直接发送。*/
				item.setPassInt(0);

				isDeal = true;
				askForSta = AskforHelpSta.AfterDealSendByWorker;

				// 更新workerlist, 设置所有人员的状态为已经处理的状态.
				DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=1 WHERE FK_Node=" + wn.getHisNode().getNodeID() + " AND WorkID=" + wn.getWorkID());

				// 把发起加签人员的状态更新过来，让他可见待办工作.
				item.setPassInt(0);
				item.Update();

				// 更新流程状态.
				wn.getHisGenerWorkFlow().setWFState(WFState.AskForReplay);
				wn.getHisGenerWorkFlow().Update();

				//让加签人，设置成工作未读。
				bp.wf.Dev2Interface.Node_SetWorkUnRead(wn.getWorkID(), item.getEmpNo());

				// 从临时变量里获取回复加签意见.
				String replyInfo = wn.getHisGenerWorkFlow().getParasAskForReply();

				////写入日志.
				//this.AddToTrack(ActionType.ForwardAskfor, item.FK_Emp, item.FK_EmpText,
				//    this.getHisNode().getNodeID(), this.getHisNode().Name,
				//    "加签后向下发送，并转向加签人发起人（" + item.EmpNo + "，" + item.EmpName + "）。<br>意见:" + replyInfo);

				//写入日志.
				wn.AddToTrack(ActionType.ForwardAskfor, item.getEmpNo(), item.getEmpName(), wn.getHisNode().getNodeID(), wn.getHisNode().getName(), bp.wf.Glo.multilingual("回复意见:{0}.", "WorkNode", "reply_comments", replyInfo));

				//加入系统变量。
				wn.addMsg(SendReturnMsgFlag.VarToNodeID, String.valueOf(wn.getHisNode().getNodeID()), SendReturnMsgType.SystemMsg);
				wn.addMsg(SendReturnMsgFlag.VarToNodeName, wn.getHisNode().getName(), SendReturnMsgType.SystemMsg);
				wn.addMsg(SendReturnMsgFlag.VarAcceptersID, item.getEmpNo(), SendReturnMsgType.SystemMsg);
				wn.addMsg(SendReturnMsgFlag.VarAcceptersName, item.getEmpName(), SendReturnMsgType.SystemMsg);

				//加入提示信息.
				wn.addMsg(SendReturnMsgFlag.SendSuccessMsg, bp.wf.Glo.multilingual("已经转给加签的发起人({0},{1})", "WorkNode", "send_to_the_operator", item.getEmpNo().toString(), item.getEmpName()), SendReturnMsgType.Info);

				//删除当前操作员临时增加的工作列表记录, 如果不删除就会导致第二次加签失败.
				GenerWorkerList gwl = new GenerWorkerList();
				gwl.Delete(GenerWorkerListAttr.FK_Node, wn.getHisNode().getNodeID(), GenerWorkerListAttr.WorkID, wn.getWorkID(), GenerWorkerListAttr.FK_Emp, wn.getExecer());

				//调用发送成功事件.
				String sendSuccess = ExecEvent.DoNode(EventListNode.SendSuccess, wn);
				wn.HisMsgObjs.AddMsg("info21", sendSuccess, sendSuccess, SendReturnMsgType.Info);

				//执行时效考核.
				Glo.InitCH(wn.getHisFlow(), wn.getHisNode(), wn.getWorkID(), 0, wn.getHisGenerWorkFlow().getTitle());

				//返回发送对象.
				return wn.HisMsgObjs;
			}
		}

		if (isDeal == false)
		{
			throw new RuntimeException(bp.wf.Glo.multilingual("@流程引擎错误，不应该找不到加签的状态.", "WorkNode", "wf_eng_error_1"));
		}

		return null;
	}
	/**
	 执行分河流状态

	 @param wn
	*/
	public static void DealHeLiuState(WorkNode wn) throws Exception {
		/*   如果是合流点 检查当前是否是合流点如果是，则检查分流上的子线程是否完成。*/
		/*检查是否有子线程没有结束*/
		Paras ps = new Paras();
		ps.SQL = "SELECT WorkID,FK_Emp,EmpName,NodeName FROM WF_GenerWorkerlist WHERE FID=" + ps.getDBStr() + "FID AND IsPass=0 AND IsEnable=1";
		ps.Add(WorkAttr.FID, wn.getWorkID());

		DataTable dtWL = DBAccess.RunSQLReturnTable(ps);
		String infoErr = "";
		if (dtWL.Rows.size() != 0)
		{
			if (wn.getHisNode().getThreadKillRole() == ThreadKillRole.None || wn.getHisNode().getThreadKillRole() == ThreadKillRole.ByHand)
			{
				infoErr += bp.wf.Glo.multilingual("@您不能向下发送，有如下子线程没有完成。", "WorkNode", "cannot_send_to_next_1");

				for (DataRow dr : dtWL.Rows)
				{
					String op = bp.wf.Glo.multilingual("@操作员编号:{0},{1}", "WorkNode", "current_operator", dr.getValue("FK_Emp").toString(), dr.getValue("EmpName").toString());
					String nd = bp.wf.Glo.multilingual("停留节点:{0}.", "WorkNode", "current_node", dr.getValue("NodeName").toString());
					//infoErr += "@操作员编号:" + dr["FK_Emp"] + "," + dr["FK_EmpText"] + ",停留节点:" + dr["NodeName");
					infoErr += op + ";" + nd;
				}

				if (wn.getHisNode().getThreadKillRole() == ThreadKillRole.ByHand)
				{
					infoErr += bp.wf.Glo.multilingual("@请通知他们处理完成,或者强制删除子流程您才能向下发送.", "WorkNode", "cannot_send_to_next_2");
				}

				else
				{
					infoErr += bp.wf.Glo.multilingual("@请通知他们处理完成,您才能向下发送.", "WorkNode", "cannot_send_to_next_3");
				}

				//抛出异常阻止它向下运动。
				throw new RuntimeException(infoErr);
			}

			if (wn.getHisNode().getThreadKillRole() == ThreadKillRole.ByAuto)
			{
				//删除每个子线程，然后向下运动。
				for (DataRow dr : dtWL.Rows)
				{
					bp.wf.Dev2Interface.Flow_DeleteSubThread(Long.parseLong(dr.getValue(0).toString()), bp.wf.Glo.multilingual("合流点发送时自动删除", "WorkNode", "auto_delete"));
				}
			}
		}
	}

	/**
	 子流程运行结束后

	 @param wn
	*/
	public static String SubFlowEvent(WorkNode wn) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow(wn.getWorkID());
		//判断是否是子流程
		if (gwf.getPWorkID() == 0)
		{
			return "";
		}
		String msg = "";
		//子流程运行到指定节点后父流程自动运行到下一个节点
		if (gwf.getIsOver()==false)
		{
			SubFlows subFlows = new SubFlows();
			subFlows.Retrieve(SubFlowAttr.FK_Node, gwf.getPNodeID(), SubFlowAttr.SubFlowNo, wn.getHisFlow().getNo(), null);
			if (subFlows.size() == 0)
			{
				return "";
			}
			SubFlow subFlow = subFlows.get(0) instanceof SubFlow ? (SubFlow)subFlows.get(0) : null;

			if (subFlow.getSubFlowNodeID() == 0 || wn.getHisNode().getNodeID() != subFlow.getSubFlowNodeID())
			{
				return "";
			}

			if (subFlow.getParentFlowSendNextStepRole() == SubFlowRunModel.SpecifiedNodes)
			{
				//获取父流程实例信息
				GenerWorkFlow pgwf = new GenerWorkFlow(gwf.getPWorkID());
				if (pgwf.getNodeID() == gwf.getPNodeID())
				{
					SendReturnObjs returnObjs = bp.wf.Dev2Interface.Node_SendWork(gwf.getPFlowNo(), gwf.getPWorkID());
					msg = "父流程自动运行到下一个节点，" + returnObjs.ToMsgOfHtml();

				}
				return msg;
			}
			return "";
		}

		//子流程结束后父流程/同级子流程的处理
		if (gwf.getIsOver()==true)
		{
			//先判断当前流程是下级子流程还是同级子流程
			long slworkid = gwf.GetParaInt("SLWorkID", 0);
			SubFlows subFlows = new SubFlows();
			SubFlow subFlow = null;
			//下级子流程
			if (slworkid == 0)
			{
				//判断子流程中的设置关系
				subFlows.Retrieve(SubFlowAttr.FK_Node, gwf.getPNodeID(), SubFlowAttr.SubFlowNo, wn.getHisFlow().getNo(), null);
				if (subFlows.size() == 0)
				{
					return "";
				}
				subFlow = subFlows.get(0) instanceof SubFlow ? (SubFlow)subFlows.get(0) : null;

				//把子流程的数据反填到父流程中
				SubFlowOver_CopyDataToParantFlow(subFlow, wn.getHisGenerWorkFlow().getPWorkID(), wn);

				int pnodeId = gwf.getPNodeID();
				FrmSubFlow nd = new FrmSubFlow(pnodeId);
				switch (nd.getAllSubFlowOverRole())
				{
					case None: //父节点不设置所有子流程结束规则
												  //发送成功后显示父流程的待办
						if (subFlow.getSubFlowHidTodolist() == true)
						{
							GenerWorkFlow pgwf = new GenerWorkFlow(gwf.getPWorkID());
							String mysql = "SELECT COUNT(WorkID) as Num FROM WF_GenerWorkFlow WHERE PWorkID=" + gwf.getPWorkID() + " AND FK_Flow='" + wn.getHisFlow().getNo() + "' AND WFState !=3 ";
							if (DBAccess.RunSQLReturnValInt(mysql, 0) == 0)
							{
								DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=0 Where WorkID=" + pgwf.getWorkID() + " AND FK_Node=" + pgwf.getNodeID() + " AND IsPass=100");

							}
						}

						//单个子流程控制父流程运行到下一个节点
						if (subFlow.getParentFlowSendNextStepRole() == SubFlowRunModel.FlowOver)
						{
							msg = SubFlowOver_ParentFlowAutoSendNextSetp(false, wn.getHisGenerWorkFlow().getPWorkID(), wn.getHisGenerWorkFlow());
						}
						//单个子流程控制父流程结束
						if (subFlow.getParentFlowOverRole() == SubFlowRunModel.FlowOver)
						{
							msg = SubFlowOver_ParentFlowOver(false, wn.getHisGenerWorkFlow().getPWorkID());
						}
						break;
					case SendParentFlowToNextStep: //父流程设置所有子流程结束后，父流程运行到下一个节点
						msg = SubFlowOver_ParentFlowAutoSendNextSetp(true, wn.getHisGenerWorkFlow().getPWorkID(), wn.getHisGenerWorkFlow());
						break;
					case OverParentFlow: //父流程设置所有子流程结束后，父流程结束
						msg = SubFlowOver_ParentFlowOver(true, wn.getHisGenerWorkFlow().getPWorkID());
						break;
					default:
						break;

				}

				return msg;
			}

			//同级子流程
			String slFlowNo = gwf.GetParaString("SLFlowNo");
			int slNodeID = gwf.GetParaInt("SLNodeID", 0);
			subFlows.Retrieve(SubFlowAttr.FK_Node, slNodeID, SubFlowAttr.SubFlowNo, wn.getHisFlow().getNo(), null);
			if (subFlows.size() == 0)
			{
				return "";
			}
			subFlow = subFlows.get(0) instanceof SubFlow ? (SubFlow)subFlows.get(0) : null;
			//子流程运行结束后父流程是否自动往下运行一步
			msg = SubFlowOver_DealSLSubFlow(slworkid, subFlow, wn.getHisGenerWorkFlow());

			return msg;
		}
		return "";

	}
	/**
	 子流程运行结束后把数据反填到父流程中

	 @param subFlow
	 @param pworkid
	 @param wn
	*/
	private static void SubFlowOver_CopyDataToParantFlow(SubFlow subFlow, long pworkid, WorkNode wn) throws Exception {
		//子流程结束后，数据不反填到父流程中
		if (subFlow.getBackCopyRole() == BackCopyRole.None)
		{
			return;
		}
		Node nd = new Node(subFlow.getNodeID());
		Work pwk = nd.getHisWork();
		pwk.setOID(pworkid);
		pwk.RetrieveFromDBSources();

		GERpt prpt = new bp.wf.GERpt("ND" + Integer.parseInt(subFlow.getFlowNo()) + "Rpt");
		prpt.setOID(pworkid);
		prpt.RetrieveFromDBSources();


		//判断是否启用了数据字段反填规则
		if (subFlow.getBackCopyRole() == BackCopyRole.AutoFieldMatch || subFlow.getBackCopyRole() == BackCopyRole.MixedMode)
		{
			//子流程数据拷贝到父流程中
			pwk.Copy(wn.getHisWork());
			prpt.Copy(wn.getHisWork());
		}
		// 子流程数据拷贝到父流程中
		if ((subFlow.getBackCopyRole() == BackCopyRole.FollowSetFormat || subFlow.getBackCopyRole() == BackCopyRole.MixedMode) && DataType.IsNullOrEmpty(subFlow.getParentFlowCopyFields()) == false)
		{
			Work wk = wn.getHisWork();
			Attrs attrs = wk.getEnMap().getAttrs();
			//获取子流程的签批字段
			String keyOfEns = "";
			String keyVals = ""; //签批字段存储的值
			for (Attr attr : attrs)
			{
				if (attr.getUIContralType() == UIContralType.SignCheck)
				{
					keyOfEns += attr.getField() + ",";
					continue;
				}

			}

			//父流程把子流程不同字段进行匹配赋值
			AtPara ap = new AtPara(subFlow.getParentFlowCopyFields());
			for (String str : ap.getHisHT().keySet())
			{
				Object val = ap.GetValStrByKey(str);
				if (DataType.IsNullOrEmpty(val.toString()) == true)
				{
					continue;
				}
				pwk.SetValByKey(val.toString(), wk.GetValByKey(str));
				prpt.SetValByKey(val.toString(), wk.GetValByKey(str));
				if (keyOfEns.contains(str + ",") == true)
				{
					keyVals += wk.GetValByKey(str);
				}
			}
			if (DataType.IsNullOrEmpty(keyVals) == false)
			{
				String trackPTable = "ND" + Integer.parseInt(wn.getHisFlow().getNo()) + "Track";
				//把子流程的签批字段对应的审核信息拷贝到父流程中
				keyVals = keyVals.substring(1);
				String sql = "SELECT * FROM " + trackPTable + " WHERE ActionType=22 AND WorkID=" + wn.getWorkID() + " AND NDFrom IN(" + keyVals + ")";
				DataTable dt = DBAccess.RunSQLReturnTable(sql);
				Tracks tracks = new Tracks();
				QueryObject.InitEntitiesByDataTable(tracks, dt, null);
				for (Track t : tracks.ToJavaList())
				{

					t.setWorkID(pwk.getOID());
					t.setFID(pwk.getFID());
					t.setHisActionType(ActionType.WorkCheck);
					t.setMyPK(String.valueOf(DBAccess.GenerOIDByGUID()));
					t.Insert();
				}
			}

		}
		pwk.Update();
		prpt.Update();

	}
	/**
	 子流程运行结束后，父流程自动运行到下一个节点

	 @param isAllSubFlowOver
	 @param pworkid
	 @param gwf
	 @return
	 @exception Exception
	*/
	private static String SubFlowOver_ParentFlowAutoSendNextSetp(boolean isAllSubFlowOver, long pworkid, GenerWorkFlow gwf) throws Exception {
		//所有子流程结束后，父流程自动运行到下一个节点
		if (isAllSubFlowOver == true)
		{
			if (bp.wf.Dev2Interface.Flow_NumOfSubFlowRuning(pworkid) != 0)
			{
				return "";
			}
		}


			///#region 检查父流程是否符合自动运行到下一个节点的条件
		GenerWorkFlow pgwf = new GenerWorkFlow();
		pgwf.setWorkID(pworkid);
		if (pgwf.RetrieveFromDBSources() == 0)
		{
			return ""; // 父流程被删除了也不能执行。
		}

		if (pgwf.getIsOver())
		{
			return ""; //父流程已经完成也不能执行.
		}

		//检查父流程的当前停留的节点是否还是发起子流程的节点？
		if (gwf.getPNodeID() != pgwf.getNodeID())
		{
			return "";
		}

			///#endregion 检查父流程是否符合自动运行到下一个节点的条件

		//获得父流程.
		String[] strs = pgwf.getTodoEmps().split("[;]", -1);
		strs = strs[0].split("[,]", -1);
		String empNo = strs[0];
		if (DataType.IsNullOrEmpty(empNo) == true)
		{
			throw new RuntimeException("err@没有找到父流程的处理人.");
		}

		//当前登录用户.
		String currUserNo = WebUser.getNo();
		try
		{
			Emp emp = new Emp(empNo);
			//让父流程的userNo登录.
			bp.wf.Dev2Interface.Port_Login(emp.getNo());
			SendReturnObjs objs = null;
			try
			{
				objs = bp.wf.Dev2Interface.Node_SendWork(pgwf.getFlowNo(), pgwf.getWorkID());
				//切换到当前流程节点.
				bp.wf.Dev2Interface.Port_Login(currUserNo);
			}
			catch (RuntimeException ex)
			{
				bp.wf.Dev2Interface.Port_Login(currUserNo);
				throw new RuntimeException(ex.getMessage());
			}

			return "@成功让父流程运行到下一个节点." + objs.ToMsgOfHtml();
		}
		catch (RuntimeException ex)
		{
			//切换到当前流程节点.
			bp.wf.Dev2Interface.Port_Login(currUserNo);
			String info = "这个错误";
			if (ex.getMessage().contains("WorkOpt/") == true)
			{
				info += "@流程设计错误:自动运行到的下一个节点的接收人规则是由上一个人员来选择的,导致到不能自动运行到下一步骤.";
				return info;
			}

			return "@在最后一个子流程完成后，让父流程的节点自动发送时，出现错误:" + ex.getMessage();
		}
	}
	/**
	 子流程结束后，父流程自动结束

	 @param isAllSubFlowOver
	 @param pworkid
	 @return
	*/
	public static String SubFlowOver_ParentFlowOver(boolean isAllSubFlowOver, long pworkid) throws Exception {
		//所有子流程结束后，父流程自动结束
		if (isAllSubFlowOver == true)
		{
			if (bp.wf.Dev2Interface.Flow_NumOfSubFlowRuning(pworkid) == 0)
			{
				return "";
			}
		}
		GenerWorkFlow gwf = new GenerWorkFlow(pworkid);
		if (gwf.getIsOver()==true)
		{
			return "";
		}

		return bp.wf.Dev2Interface.Flow_DoFlowOver(gwf.getWorkID(), "父流程[" + gwf.getFlowName() + "],标题为[" + gwf.getTitle() + "]成功结束");
	}
	/**
	 子流程结束后，处理同级子流程

	 @param slWorkID
	 @param subFlow
	 @param gwf
	 @return
	*/
	private static String SubFlowOver_DealSLSubFlow(long slWorkID, SubFlow subFlow, GenerWorkFlow gwf) throws Exception {
		if (subFlow.isAutoSendSLSubFlowOver() == 0)
		{
			return "";
		}
		String slFlowNo = gwf.GetParaString("SLFlowNo");
		int slNodeID = gwf.GetParaInt("SLNodeID", 0);
		Flow fl = new Flow(slFlowNo);
		GenerWorkFlow subgwf = new GenerWorkFlow(slWorkID);
		if (subFlow.isAutoSendSLSubFlowOver() == 1)
		{
			if (subgwf.getNodeID() != slNodeID)
			{
				return "";
			}

			//主流程自动运行到一下节点
			SendReturnObjs returnObjs = bp.wf.Dev2Interface.Node_SendWork(slFlowNo, slWorkID);
			String sendSuccess = "同级子流程[" + fl.getName() + "]程自动运行到下一个节点，发送过程如下：\n @接收人" + returnObjs.getVarAcceptersName() + "\n @下一步[" + returnObjs.getVarCurrNodeName() + "]启动";
			return sendSuccess;
		}
		//结束父流程
		if (subFlow.isAutoSendSLSubFlowOver() == 2)
		{
			if (subgwf.getIsOver())
			{
				return "";
			}
			return bp.wf.Dev2Interface.Flow_DoFlowOver(slWorkID, "同级子流程流程[" + fl.getName() + "],WorkID为[" + slWorkID + "]成功结束");
		}
		return "";
	}
}
