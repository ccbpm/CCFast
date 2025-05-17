package bp.ccbill;

import bp.ccbill.template.CollectionAttr;
import bp.ccbill.template.DBRole;
import bp.ccbill.template.DBRoles;
import bp.difference.SystemConfig;
import bp.tools.DateUtils;
import bp.tools.StringHelper;
import bp.wf.*;
import bp.en.*;
import bp.da.*;
import bp.web.*;
import bp.sys.*;
import bp.wf.template.*;
import bp.*;
import io.netty.util.internal.StringUtil;
import net.sf.json.JSON;
import net.sf.json.JSONObject;

import java.util.*;
import java.time.*;
import java.util.Map;

/**
 接口调用
*/
public class Dev2Interface
{
	/**
	 增加日志

	 @param at
	 @param frmID
	 @param at
	 @param msg
	 @return
	*/

	public static void WriteTrack(String frmID, String frmWorkID, String at, String msg, String paras, String flowNo, String flowName, int nodeID, long workIDOfFlow) throws Exception {
		WriteTrack(frmID, frmWorkID, at, msg, paras, flowNo, flowName, nodeID, workIDOfFlow, "");
	}

	public static void WriteTrack(String frmID, String frmWorkID, String at, String msg, String paras, String flowNo, String flowName, int nodeID) throws Exception {
		WriteTrack(frmID, frmWorkID, at, msg, paras, flowNo, flowName, nodeID, 0, "");
	}

	public static void WriteTrack(String frmID, String frmWorkID, String at, String msg, String paras, String flowNo, String flowName) throws Exception {
		WriteTrack(frmID, frmWorkID, at, msg, paras, flowNo, flowName, 0, 0, "");
	}

	public static void WriteTrack(String frmID, String frmWorkID, String at, String msg, String paras, String flowNo) throws Exception {
		WriteTrack(frmID, frmWorkID, at, msg, paras, flowNo, null, 0, 0, "");
	}

	public static void WriteTrack(String frmID, String frmWorkID, String at, String msg, String paras) throws Exception {
		WriteTrack(frmID, frmWorkID, at, msg, paras, null, null, 0, 0, "");
	}

	public static void WriteTrack(String frmID, String frmWorkID, String at, String msg) throws Exception {
		WriteTrack(frmID, frmWorkID, at, msg, null, null, null, 0, 0, "");
	}

	public static void WriteTrack(String frmID, String frmWorkID, String at, String msg, String paras, String funcNo, String funcName, int nodeID, long workIDOfFlow, String frmName) throws Exception {
		bp.ccbill.Track tk = new bp.ccbill.Track();
		tk.setWorkID(frmWorkID);
		tk.setFrmID(frmID);
		tk.setFrmName(frmName);
		tk.setActionType(at);

		switch (at)
		{
			case FrmActionType.BBS:
				tk.setActionTypeText("评论");
				break;
			case FrmActionType.Create:
				tk.setActionTypeText("创建");
				break;
			case FrmActionType.DataVerReback:
				tk.setActionTypeText("数据版本");
				break;
			case FrmActionType.Save:
				tk.setActionTypeText("保存");
				break;
			case FrmActionType.StartFlow:
				tk.setActionTypeText("发起流程");
				break;
			case "Func":
				tk.setActionTypeText("执行功能");
				break;
			case "Info":
				tk.setActionTypeText("信息"); //@gaoxin.
				break;
			default:
				tk.setActionTypeText("其他");
				break;
		}

		tk.setRec(WebUser.getNo());
		tk.setRecName(WebUser.getName());
		tk.setDeptNo(WebUser.getDeptNo());
		tk.setDeptName(WebUser.getDeptName());

		// 流程信息。
		tk.setWorkIDOfFlow(workIDOfFlow);
		tk.setNodeID(nodeID);
		if (funcNo != null)
		{
			tk.setFuncName(funcName);
		}
		if (funcNo != null)
		{
			tk.setFuncNo(funcName);
		}

		//tk.setMyPK(tk.getFrmID() + "_" + tk.WorkID + "_" + tk.Rec + "_" + (int)BP.CCBill.FrmActionType.BBS;
		tk.setMsg(msg);
		tk.setRDT(DataType.getCurrentDateTime());
		if (paras != null)
			tk.SetValByKey("AtPara", paras);

		////流程信息.
		//tk.setNodeID(nodeID;
		//tk.setNodeName(nodeName;
		//tk.setFlowNo(flowNo);
		//tk.FlowName = flowName;
		//tk.setFID(fid;
		tk.Insert();
	}


	/**
	 创建单据的WorkID

	 @param frmID
	 @param userNo
	 @param htParas
	 @return
	*/

	public static long CreateBlankBillID(String frmID, String userNo, java.util.Hashtable htParas, String pDictFrmID) throws Exception {
		return CreateBlankBillID(frmID, userNo, htParas, pDictFrmID, 0);
	}

	public static long CreateBlankBillID(String frmID, String userNo, java.util.Hashtable htParas) throws Exception {
		return CreateBlankBillID(frmID, userNo, htParas, null, 0);
	}

	public static long CreateBlankBillID(String frmID, String userNo) throws Exception {
		return CreateBlankBillID(frmID, userNo, null, null, 0);
	}

	public static long CreateBlankBillID(String frmID) throws Exception {
		return CreateBlankBillID(frmID, null, null, null, 0);
	}
	public static long CreateBlankBillID(String frmID, String userNo, Hashtable htParas, String pDictFrmID, long pDictWorkID) throws Exception {
		if (userNo == null)
		{
			userNo = WebUser.getNo();
		}
		// log
		JSONObject log = new JSONObject();
		log.put("frmID", frmID);
		log.put("userNo", userNo);
		log.put("pDictFrmID", pDictFrmID);
		log.put("pDictWorkID", pDictWorkID);
		log.put("params", JSONObject.fromObject(htParas));
		bp.sys.base.Glo.WriteUserLog(log.toString(), "低代码创建实体");
		// end
		GenerBill gb = new GenerBill();
		int i = gb.Retrieve(GenerBillAttr.FrmID, frmID, GenerBillAttr.Starter, userNo, GenerBillAttr.BillState, 0);
		if (i == 1)
		{
			GERpt rpt1 = new GERpt(frmID);
			rpt1.setOID(gb.getWorkID());
			int count = rpt1.RetrieveFromDBSources();

			if (htParas != null)
			{
				rpt1.Copy(htParas);
			}

			rpt1.SetValByKey("BillState", 0);
			rpt1.SetValByKey("Starter", gb.getStarter());
			rpt1.SetValByKey("StarterName", gb.getStarterName());
			rpt1.SetValByKey("FK_Dept", WebUser.getDeptNo());
			rpt1.SetValByKey("DeptNo", WebUser.getDeptNo());
			rpt1.SetValByKey("RDT", gb.getRDT());
			rpt1.SetValByKey("Title", gb.getTitle());
			rpt1.SetValByKey("BillNo", gb.getBillNo());
			if(SystemConfig.getCCBPMRunModel()!=CCBPMRunModel.Single)
				rpt1.SetValByKey("OrgNo",WebUser.getOrgNo());
			if (pDictFrmID != null)
			{
				rpt1.SetValByKey("PWorkID", pDictWorkID);
				rpt1.SetValByKey("PFrmID", pDictFrmID);
			}
			if (count == 0)
			{
				rpt1.InsertAsOID(gb.getWorkID());
			}
			else
			{
				rpt1.Update();
			}
			return gb.getWorkID();
		}


		FrmBill fb = new FrmBill(frmID);
		gb.setWorkID(DBAccess.GenerOID("WorkID"));
		gb.setBillState(BillState.Blank); //初始化状态.
		gb.setStarter(WebUser.getNo());
		gb.setStarterName(WebUser.getName());
		gb.setFrmName(fb.getName()); //单据名称.
		gb.setFrmID(fb.getNo()); //单据ID

		//if (DataType.IsNullOrEmpty(billNo) == false)
		//    gb.BillNo = billNo; //BillNo
		gb.setDeptNo(WebUser.getDeptNo());
		gb.setDeptName(WebUser.getDeptName());
		gb.setFrmTreeNo(fb.getFormTreeNo()); //单据类别.
		gb.setRDT(DataType.getCurrentDateTime());
		//gb.setIdx(1);
		gb.setIdxName("启动");

		//父字典信息.
		if (pDictFrmID != null)
		{
			gb.setPFrmID(pDictFrmID);
			gb.setPWorkID(pDictWorkID);
		}


		//创建rpt.
		GERpt rpt = new GERpt(frmID);

		//设置标题.
		if (fb.getEntityType() == EntityType.FrmBill)
		{
			gb.setTitle(Dev2Interface.GenerTitle(fb.getTitleRole(), rpt));
			//if (DataType.IsNullOrEmpty(billNo) == false)
			//    gb.BillNo = billNo;
			//else
			gb.setBillNo(bp.ccbill.Dev2Interface.GenerBillNo(fb.getBillNoFormat(), gb.getWorkID(), null, frmID));
		}

		if (fb.getEntityType() == EntityType.EntityTree || fb.getEntityType() == EntityType.FrmDict)
		{
			rpt.getEnMap().setCodeStruct( fb.getEnMap().getCodeStruct());
			gb.setBillNo(rpt.GenerNewNoByKey("BillNo"));
		}

		gb.DirectInsert(); //执行插入.

		//如果.
		if (htParas != null)
		{
			rpt.Copy(htParas);
		}

		//更新基础的数据到表单表.
		// rpt = new BP.WF.GERpt(frmID);
		rpt.SetValByKey("BillState", gb.getBillState().getValue());
		rpt.SetValByKey("Starter", gb.getStarter());
		rpt.SetValByKey("StarterName", gb.getStarterName());
		rpt.SetValByKey("FK_Dept", WebUser.getDeptNo());
		rpt.SetValByKey("DeptNo", WebUser.getDeptNo());
		rpt.SetValByKey("RDT", gb.getRDT());
		rpt.SetValByKey("Title", gb.getTitle());
		rpt.SetValByKey("BillNo", gb.getBillNo());
		if (pDictFrmID != null)
		{
			rpt.SetValByKey("PWorkID", pDictWorkID);
			rpt.SetValByKey("PFrmID", pDictFrmID);
		}

		if(SystemConfig.getCCBPMRunModel()!=CCBPMRunModel.Single)
			rpt.SetValByKey("OrgNo",WebUser.getOrgNo());
		rpt.setOID(gb.getWorkID());
		rpt.InsertAsOID(gb.getWorkID());

		bp.ccbill.Dev2Interface.WriteTrack(frmID, String.valueOf(rpt.getOID()), FrmActionType.Create, "创建记录");

		return gb.getWorkID();
	}

	/// 创建一个空白的实体.
	/// </summary>
	/// <param name="frmID"></param>
	/// <param name="userNo"></param>
	/// <param name="htParas"></param>
	/// <returns></returns>
	public static String CreateBlankEntityNoName(String frmID, String userNo, Hashtable htParas) throws Exception {
		if (userNo == null)
			userNo = WebUser.getNo();

		// 创建一个实体, 先检查一下是否有空白的数据.
		GEEntityNoName rpt = new GEEntityNoName(frmID);
		int i = rpt.Retrieve("RecNo", userNo, "EntityState", 0);
		if (i >= 1)
		{
			if (htParas != null)
				rpt.Copy(htParas);

			rpt.SetValByKey("RDT", DataType.getCurrentDate());
			if(SystemConfig.getCCBPMRunModel()!=CCBPMRunModel.Single)
				rpt.SetValByKey("OrgNo",WebUser.getOrgNo());
			rpt.Update();
			return rpt.getNo(); //如果有空白的数据，就返回给他.
		}


		//执行copy数据.
		if (htParas != null)
			rpt.Copy(htParas);

		FrmEntityNoName fb = new FrmEntityNoName(frmID);

		//更新基础的数据到表单表.
		rpt.SetValByKey("EntityState", 0);
		rpt.SetValByKey("RecNo", WebUser.getNo());
		rpt.SetValByKey("RecName", WebUser.getName());
		rpt.SetValByKey("DeptNo", WebUser.getDeptNo());
		rpt.SetValByKey("OrgNo", WebUser.getOrgNo());
		rpt.SetValByKey("RDT", DataType.getCurrentDate());

		//设置编号生成规则.
		rpt.getEnMap().setCodeStruct(fb.getBillNoFormat()); // "4";//  fb.BillNoFormat;

		//rpt.SetValByKey("Title", gb.Title);
		rpt.SetValByKey("No", rpt.GenerNewNoByKey("No"));
		rpt.ResetDefaultVal();
		if(SystemConfig.getCCBPMRunModel()!=CCBPMRunModel.Single)
			rpt.SetValByKey("OrgNo",WebUser.getOrgNo());
		rpt.Insert();

		//  rpt.InsertAsNo(rpt.OID);
		Dev2Interface.WriteTrack(frmID, rpt.getNo(), FrmActionType.Create, "创建记录");
		return rpt.getNo();
	}
	public static String Bill_Send(String frmID, long workID ){
		return Bill_Send(frmID, workID,  null);
	}
	/// <summary>
	/// 执行发送
	/// </summary>
	/// <param name="frmID">节点ID</param>
	/// <param name="workID">工作ID</param>
	/// <param name="toEmpNo">到达的人员编号,如果为空就按照默认的来设置。</param>
	/// <returns>返回执行结果</returns>
	public static String Bill_Send(String frmID, long workID, String toEmpNo)
	{
         //   #region 处理将要发送到的人员.
		if (DataType.IsNullOrEmpty(toEmpNo) == true)
		{
			String sql = "SELECT EmpNo FROM Frm_GenerBillWorker WHERE WorkID=" + workID + " ORDER BY Idx";
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
			{
			}
		}
		//    #endregion 处理将要发送到的人员.
		return "";
	}
	/// <summary>
	/// 设置审核模式: 启动单据后设置审核模式.
	/// </summary>
	/// <param name="frmID">表单信息</param>
	/// <param name="workID">工作ID</param>
	/// <param name="toEmpNo">设置内容</param>
	/// <returns></returns>
	public static String Bill_InitBillCheckModel(String frmID, long workID) throws Exception {
		FrmBill frmBill = new FrmBill(frmID);

		//如果没有设置审核模式.
		if (frmBill.getBillCheckModel().equals("None") || frmBill.getBillCheckModel().equals("0") || DataType.IsNullOrEmpty(frmBill.getBillCheckModel()))
		{
			return "info@当前没有设置审核模式.";
		}
		//按照固定人员审核.
		if (frmBill.getBillCheckModel().equals("ByEmpNos") == true)
		{
			String empNos = frmBill.getBillCheckTag();
			return Bill_CheckerGoToOrder(frmID, workID, empNos);
		}

		//按照SQL固定人员审核.
		if (frmBill.getBillCheckModel().equals("BySQL") == true)
		{
			String sql = frmBill.getBillCheckTag();
			GEEntity en = new GEEntity(frmID, workID);

			sql = bp.wf.Glo.DealSQLExp(sql, en, null);
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			String empNos = "";
			for (DataRow dr : dt.Rows)
			{
				empNos += dr.get(0).toString() + ",";
			}
			return Bill_CheckerGoToOrder(frmID, workID, empNos);
		}

		//按照：流程审核.
		if (frmBill.getBillCheckModel().equals("ByFlowNo") == true)
		{
			String flowNo = frmBill.getBillCheckTag();
			GEEntity en = new GEEntity(frmID, workID);

			long flowWorkID = en.GetParaInt("WorkID", 0);

			//创建一个空白的workid.
			if (flowWorkID == 0)
				flowWorkID = bp.wf.Dev2Interface.Node_CreateBlankWork(flowNo);

			//设置参数.
			bp.wf.Dev2Interface.Node_SaveWork(flowWorkID, en.getRow());
			//设置草稿.
			bp.wf.Dev2Interface.Node_SetDraft(flowWorkID);

			//返回流程信息,调用流程.
			return "@FlowNo=" + flowNo + "@WorkID=" + flowWorkID;
		}
		return "";
	}
	/// <summary>
	/// 审核人预置 preplace
	/// </summary>
	/// <param name="workID">工作ID</param>
	/// <param name="empNosOfChecker"></param>
	/// <returns>执行结果</returns>
	public static String Bill_PreplaceChecker(long workID, String empNosOfChecker) throws Exception {
		GenerBill gb = new GenerBill(workID);
		if (gb.getBillState() == BillState.FlowOver)
			throw new Exception("err@该单据已经完成，您不能设置审核人员.");
		gb.SetPara("PreplaceChecker", empNosOfChecker);
		if (gb.getBillState() == BillState.Blank)
			gb.setBillState(BillState.Editing);
		gb.Update();
		return "审核人预置成功，在用户提交的时候,就显示这些处理人.";
	}
	/// <summary>
	/// 预置审核人员列表（使用接口为人员设置路径）
	/// 1. 仅仅是预置处理人，如果需要启动审批，则要调用审批流程。
	/// </summary>
	/// <param name="frmID">表单ID</param>
	/// <param name="workID">工作ID</param>
	/// <param name="empNosOfChecker">审核人员,格式为:zhangsan,lisi,wangwu</param>
	/// <returns>返回执行结果</returns>
	/// <exception cref="Exception"></exception>
	public static String Bill_CheckerInit( long workID, String empNosOfChecker) throws Exception {
		empNosOfChecker = empNosOfChecker.trim();
		GenerBill gb = new GenerBill(workID);
		if (gb.getBillState() == BillState.FlowOver)
			throw new Exception("err@该单据已经完成，您不能设置审核人员.");

		try
		{
			//删除现在已经有的人员.
			DBAccess.RunSQL("DELETE FROM Frm_GenerWorker WHERE WorkID=" + workID);
		}catch(Exception ex)
		{
			GenerWorker lis2t = new GenerWorker();
			lis2t.CheckPhysicsTable();
		}

		//检查人员是否正确？
		bp.port.Emps emps = new bp.port.Emps();
		String[] strs = empNosOfChecker.split(",");
		String err = "";
		for (String empNo : strs)
		{
			if (DataType.IsNullOrEmpty(empNo) == true)
				continue;
			bp.port.Emp emp = new bp.port.Emp();
			emp.setNo (empNo);
			if (emp.RetrieveFromDBSources() == 0)
			{
				err += " 人员账号错误:[" + empNo + "],不存在.";
			}
			emps.AddEntity(emp); //增加进去.
		}
		if (DataType.IsNullOrEmpty(err) == false)
			throw new Exception("设置审核人员错误:" + err);

		GenerWorker list = new GenerWorker();
		list.SetValByKey("FrmID", gb.getFrmID());
		list.SetValByKey("WorkID", workID);

		//审核人员.
		int idx = 0;
		String names = "";
		for (bp.port.Emp emp : emps.ToJavaList())
		{
			idx++;

			String mypk = workID + "_" + emp.getNo() + "_" + idx;
			list.setMyPK(mypk);
			list.SetValByKey("EmpNo", emp.getNo());

			if (idx == 1)
				list.SetValByKey("PassSta", 1); //设置第一个待办.
			else
				list.SetValByKey("PassSta", 0);
			list.Insert(); //设置审批人员.

			if (idx == 1)
				names += "" +emp.getNo()+","+ emp.getName();
			else
				names += "、" +emp.getNo()+","+ emp.getName();
		}
		String msg = "启动简易审核:下达给" + names;
		bp.ccbill.Dev2Interface.WriteTrack(gb.getFrmID(), String.valueOf(workID), "StartFlow", msg);
		return "启动成功.";
	}
	/// <summary>
	/// 设置审核人进行审核,进入审核队列.
	/// 1.发起审批.
	/// </summary>
	/// <param name="frmID">表单ID</param>
	/// <param name="workID">oid</param>
	/// <param name="empNosOfChecker">审核人,多个审核人用逗号分开，比如:zhangsan,lisi,wangwu</param>
	/// <returns>返回设置结果</returns>
	public static String Bill_CheckerGoToOrder(String frmID, long workID, String empNosOfChecker) throws Exception {
		empNosOfChecker = empNosOfChecker.trim();
		empNosOfChecker = empNosOfChecker.replace(",,", ",");
		empNosOfChecker = empNosOfChecker.replace(",,", ",");
		empNosOfChecker = empNosOfChecker.replace(",,", ",");

		GenerBill gb = new GenerBill(workID);
		if (gb.getBillState() == BillState.FlowOver)
			throw new Exception("err@该单据已经完成，您不能设置审核人员.");
		try
		{
			//删除现在已经有的人员.
			DBAccess.RunSQL("DELETE FROM Frm_GenerWorker WHERE WorkID=" + workID);
		}
		catch (Exception ex)
		{
			GenerWorker lis2t = new GenerWorker();
			lis2t.CheckPhysicsTable();
		}

		//检查人员是否正确？
		bp.port.Emps emps = new bp.port.Emps();
		String[] strs = empNosOfChecker.split(",");
		String err = "";
		for (String empNo : strs)
		{
			if (DataType.IsNullOrEmpty(empNo) == true)
				continue;
			bp.port.Emp emp = new bp.port.Emp();
			emp.setNo(empNo);
			if (emp.RetrieveFromDBSources() == 0)
			{
				err += "\t\n人员账号错误:[" + empNo + "],不存在.";
			}
			emps.AddEntity(emp); //增加进去.
		}
		if (DataType.IsNullOrEmpty(err) == false)
			throw new Exception("err@设置审核人员错误:" + err);

		GenerWorker list = new GenerWorker();
		list.SetValByKey("FrmID", frmID);
		list.SetValByKey("WorkID", workID);
		list.SetValByKey("EmpNo", WebUser.getNo());
		list.SetValByKey("EmpName", WebUser.getName()); //设置人员.
		list.SetValByKey("DeptNo", WebUser.getDeptNo());
		list.SetValByKey("DeptName", WebUser.getDeptName());
		list.SetValByKey("Idx", 0); //设置顺序号.
		list.SetValByKey("PassSta", 2); //审核通过.
		list.SetValByKey("CheckerNote", "发起审核"); //审核通过.
		list.SetValByKey("RDT", DataType.getCurrentDateTime()); //审核通过.
		list.SetValByKey("SendDT", DataType.getCurrentDateTime()); //审核日期.
		list.SetValByKey("MyPK", workID + "_" + WebUser.getNo() + "_0"); //设置主键.
		list.Insert();

		list.SetValByKey("SendDT", "无"); //审核日期.

		//审核人员.
		int idx = 0;
		String names = "";
		String currNos = "";
		String currNames = "";
		String currNoNames = "";
		for (bp.port.Emp emp : emps.ToJavaList())
		{
			idx++;
			String mypk = workID + "_" + emp.getNo() + "_" + idx;
			list.setMyPK(mypk);
			list.SetValByKey("EmpNo", emp.getNo());
			list.SetValByKey("EmpName", emp.getName()); //设置人员.
			list.SetValByKey("DeptNo", emp.getDeptNo());
			list.SetValByKey("DeptName", emp.getDeptText());
			list.SetValByKey("Idx", idx); //设置顺序号.

			if (idx == 1)
				list.SetValByKey("PassSta", 0); //设置第一个待办.
			else
				list.SetValByKey("PassSta", 1); //等待审核.
			list.Insert(); //设置审批人员.

			if (idx == 1)
			{
				currNos = emp.getNo();
				currNames = emp.getName();
				names += "" + emp.getName();
				currNoNames += emp.getNo() + "," + emp.getName() + ";";
			}
			else {
				names += "、" + emp.getName();
				currNoNames += emp.getNo() + "," + emp.getName() + ";";
			}
		}

		gb.setBillState(BillState.Checking);
		//设置当前审核人的信息.
		gb.SetValByKey(GenerBillAttr.CurrIdx, 1);
		gb.SetValByKey(GenerBillAttr.CurrCheckerNos, currNos);
		gb.SetValByKey(GenerBillAttr.CurrCheckerNames, currNames);
		gb.SetValByKey(GenerBillAttr.Emps, WebUser.getNo()+","+WebUser.getName()+";");
		gb.Update();  //设置审核状态.

		String msg = "启动简易审核:下达给:" + currNoNames;
		bp.ccbill.Dev2Interface.WriteTrack(frmID, String.valueOf(workID), "StartFlow", msg);
		return  msg;
	}
	/**
	 创建一个实体ID

	 @param frmID 实体ID
	 @param userNo 用户编号
	 @param htParas 参数
	 @return 一个实例的workid
	*/
	public static long CreateBlankDictID(String frmID, String userNo, Hashtable htParas) throws Exception {
		if (userNo == null)
		{
			userNo = WebUser.getNo();
		}
		// log
		JSONObject log = new JSONObject();
		log.put("frmID", frmID);
		log.put("userNo", userNo);
		log.put("params", JSONObject.fromObject(htParas));
		bp.sys.base.Glo.WriteUserLog(log.toString(), "低代码创建实体");
		// end
		// 创建一个实体, 先检查一下是否有空白的数据.
		GERpt rpt = new GERpt(frmID);
		int i = rpt.Retrieve("Starter", userNo, "BillState", 0);
		if (i >= 1)
		{
			if (htParas != null)
			{
				rpt.Copy(htParas);
			}

			rpt.SetValByKey("RDT", DataType.getCurrentDate());
			if(SystemConfig.getCCBPMRunModel()!=CCBPMRunModel.Single)
				rpt.SetValByKey("OrgNo",WebUser.getOrgNo());
			rpt.setFID(0);
			rpt.Update();
			return rpt.getOID(); //如果有空白的数据，就返回给他.
		}


		//执行copy数据.
		if (htParas != null)
		{
			rpt.Copy(htParas);
		}

		FrmDict fb = new FrmDict(frmID);

		//更新基础的数据到表单表.
		rpt.SetValByKey("BillState", 0);
		rpt.SetValByKey("Starter", WebUser.getNo());
		rpt.SetValByKey("StarterName", WebUser.getName());
		rpt.SetValByKey("DeptNo", WebUser.getDeptNo());
		rpt.SetValByKey("DeptName", WebUser.getDeptName());
		rpt.SetValByKey("RDT", DataType.getCurrentDate());
		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
			rpt.SetValByKey("OrgNo", WebUser.getOrgNo());
		//设置编号生成规则.
		//rpt.getEnMap().setCodeStruct(fb.getBillNoFormat());

		//rpt.SetValByKey("Title", gb.Title);
		String title = Dev2Interface.GenerTitle(fb.getTitleRole(), rpt);
		rpt.SetValByKey("Title", title);
		//rpt.SetValByKey("BillNo", rpt.GenerNewNoByKey("BillNo", null));
		rpt.setOID(DBAccess.GenerOID("WorkID"));
		rpt.ResetDefaultVal(null, null, 0);
		rpt.setFID(0);
		rpt.InsertAsOID(rpt.getOID());

		//生成单据编号.
		String billNo = Dev2Interface.GenerBillNo(fb.getBillNoFormat(), rpt.getOID(), rpt, fb.getNo());
		rpt.SetValByKey("BillNo", billNo);
		rpt.Update(); //更新.

		GenerBill gb = new GenerBill();
		gb.setTitle(title);
		gb.setBillNo(billNo);
		gb.setWorkID(rpt.getOID());
		gb.setStarter(WebUser.getNo());
		gb.setStarterName(WebUser.getName());
		gb.setBillState(BillState.Blank);
		gb.setDeptName(WebUser.getDeptName());
		gb.setDeptNo(WebUser.getNo());
		gb.setFrmID(fb.getNo());
		gb.setFrmName(fb.getName());
		gb.Insert();

		bp.ccbill.Dev2Interface.WriteTrack(frmID, String.valueOf(rpt.getOID()), FrmActionType.Create, "创建记录");

		return rpt.getOID();
	}
	/**
	 保存实体数据

	 @param frmID 表单ID
	 @param workid 工作ID
	 @param htParas 参数数据
	 @return
	*/
	public static void SaveDictWork(String frmID, long workid, Hashtable htParas) throws Exception {
		// 创建一个实体, 先检查一下是否有空白的数据.
		GERpt rpt = new GERpt(frmID);
		rpt.setOID(workid);
		if (rpt.RetrieveFromDBSources() == 0)
		{
			if (htParas != null)
			{
				rpt.Copy(htParas);
			}

			//设置编号生成规则.
			FrmBill fb = new FrmBill(frmID);
			rpt.getEnMap().setCodeStruct(fb.getBillNoFormat());
			rpt.SetValByKey("BillNo", rpt.GenerNewNoByKey("BillNo", null));
			rpt.InsertAsOID(workid);
		}
		else
		{
			//执行copy数据.
			if (htParas != null)
			{
				rpt.Copy(htParas);
			}
		}

		//更新基础的数据到表单表.
		rpt.SetValByKey("BillState", 100);
		rpt.SetValByKey("Starter", WebUser.getNo());
		rpt.SetValByKey("StarterName", WebUser.getName());
		rpt.SetValByKey("FK_Dept", WebUser.getDeptNo());
		rpt.SetValByKey("RDT", DataType.getCurrentDate());
		rpt.Update();

		bp.ccbill.Dev2Interface.WriteTrack(frmID, String.valueOf(workid), FrmActionType.Save, "执行保存");

	}

	/**
	 保存

	 @param frmID 表单ID
	 @param workID 工作ID
	 @return 返回保存结果
	*/
	public static String SaveBillWork(String frmID, long workID) throws Exception {
		FrmBill fb = new FrmBill(frmID);

		GenerBill gb = new GenerBill();
		gb.setWorkID(workID);
		int i = gb.RetrieveFromDBSources();
		if (i == 0)
		{
			return "";
		}
		if (DataType.IsNullOrEmpty(fb.getBillCheckModel()) == true || fb.getBillCheckModel().equals("None") || gb.getBillState() == BillState.Blank
				|| gb.getBillState() == BillState.Draft)
			gb.setBillState(BillState.Editing);

		//创建rpt.
		GERpt rpt = new GERpt(gb.getFrmID(), workID);

		if (fb.getEntityType() == EntityType.EntityTree || fb.getEntityType() == EntityType.FrmDict)
		{

			gb.setTitle(rpt.getTitle());
			gb.Update();
			return "保存成功...";
		}

		//单据编号.
		if (DataType.IsNullOrEmpty(gb.getBillNo()) == true && !(fb.getEntityType() == EntityType.EntityTree || fb.getEntityType() == EntityType.FrmDict))
		{
			gb.setBillNo(bp.ccbill.Dev2Interface.GenerBillNo(fb.getBillNoFormat(), workID, null, fb.getPTable()));
			//更新单据里面的billNo字段.
			if (DBAccess.IsExitsTableCol(fb.getPTable(), "BillNo") == true)
			{
				DBAccess.RunSQL("UPDATE " + fb.getPTable() + " SET BillNo='" + gb.getBillNo() + "' WHERE OID=" + workID);
			}
		}

		//标题.
		if (DataType.IsNullOrEmpty(gb.getTitle()) == true && !(fb.getEntityType() == EntityType.EntityTree || fb.getEntityType() == EntityType.FrmDict))
		{
			gb.setTitle(Dev2Interface.GenerTitle(fb.getTitleRole(), rpt));
			//更新单据里面的 Title 字段.
			if (DBAccess.IsExitsTableCol(fb.getPTable(), "Title") == true)
			{
				DBAccess.RunSQL("UPDATE " + fb.getPTable() + " SET Title='" + gb.getTitle() + "' WHERE OID=" + workID);
			}
		}

		gb.Update();

		//把通用的字段更新到数据库.
		rpt.setTitle(gb.getTitle());
		rpt.setBillNo(gb.getBillNo());
		rpt.SetValByKey("BillState", gb.getBillState().getValue());
		rpt.Update();

		bp.ccbill.Dev2Interface.WriteTrack(frmID, String.valueOf(rpt.getOID()), FrmActionType.Save, "保存");

		return "保存成功...";
	}
	/// <summary>
	/// 退回给指定的人
	/// </summary>
	/// <param name="workID">工作ID</param>
	/// <param name="toIdx">退回到节点</param>
	/// <param name="msg">退回信息</param>
	/// <returns>执行结果.</returns>
	public static String MyBill_ReturnWork(long workID, int toIdx, String msg) throws Exception {
		GenerBill gb = new GenerBill(workID);
		GenerWorkers wks = new GenerWorkers();
		if (toIdx == 0)
		{
			//如果退回到开始节点.
			gb.setCurrIdx( 0);
			gb.SetValByKey("CurrCheckerNos", gb.getStarter());
			gb.SetValByKey("CurrCheckerNames", gb.getStarterName());
			gb.SetValByKey("SendDT", DataType.getCurrentDateTime());
			gb.setBillState(BillState.ReturnSta);
			String info2 = "退回成功,退回发起人[" + gb.getStarter() + "," + gb.getStarterName() + "] \t\n 退回原因:" + msg + "\t\n 退回人:" + WebUser.getNo() + "," + WebUser.getName() + " 日期:" + DataType.getCurrentDateTime();
			gb.SetValByKey("Msg", info2);
			gb.Update();

			//@0=待办@1=未开始@2=已通过@3=退回
			String sql = "UPDATE FRM_GenerWorker SET PassSta=1 WHERE WorkID=" + workID + " AND Idx > 0";
			DBAccess.RunSQL(sql);

			//设置待办.
			sql = "UPDATE FRM_GenerWorker SET PassSta=0 WHERE WorkID=" + workID + " AND Idx = 0";
			DBAccess.RunSQL(sql);

			bp.ccbill.Dev2Interface.WriteTrack(gb.getFrmID(), String.valueOf(workID), "ReturnWork", "退回", info2);
			return info2;
		}

		wks.Retrieve("WorkID", workID, "Idx", gb.getCurrIdx());//查询出来当前的Idx.
		if (wks.size() == 0)
			return "err@退回错误，数据错误，没有找到当前的数据.";

		GenerWorker fromWK = (GenerWorker)wks.get(0);
		fromWK.setPassSta(PassSta.UnPass.getValue());
		fromWK.setCheckerNote(msg);
		fromWK.Update(); //把退回的.

		wks.Retrieve("WorkID", workID, "Idx", toIdx);
		if (wks.size() == 0)
			return "err@退回错误，数据错误，没有找到要退回到的节点数据.";
		GenerWorker toWK = (GenerWorker)wks.get(0);
		toWK.setPassSta(PassSta.ReturnSta.getValue());
		//   toWK.CheckerNote = msg;
		toWK.Update();

		String info = "退回信息:\t\n" + msg + "\t\n退回人:" + fromWK.getEmpNo() + "," + fromWK.getEmpName() + " 时间:" + DataType.getCurrentDateTime();
		gb.setCurrIdx(toIdx);
		gb.SetValByKey("CurrCheckerNos", toWK.getEmpNo());
		gb.SetValByKey("CurrCheckerNames", toWK.getEmpName());
		gb.SetValByKey("SendDT", DataType.getCurrentDateTime());
		gb.SetValByKey("Msg", info);
		gb.setBillState(BillState.ReturnSta); //退回中.
		gb.Update();
		bp.ccbill.Dev2Interface.WriteTrack(gb.getFrmID(), String.valueOf(workID), "ReturnWork", "退回", info);
		return "退回成功,退回到节点[" + toWK.getIdx() + "],退回给[" + toWK.getEmpNo() + "," + toWK.getEmpName() + "]";
	}
	/// <summary>
	/// 执行发送
	/// </summary>
	/// <param name="frmID"></param>
	/// <param name="workID"></param>
	/// <param name="msg"></param>
	/// <returns></returns>
	public static String MyBill_Send(String frmID, long workID, String msg) throws Exception {
		GenerBill gb = new GenerBill(workID);
		String mypk = gb.getWorkID() + "_" + WebUser.getNo() + "_" + gb.getCurrIdx();
		GenerWorker wk = new GenerWorker(mypk);
		wk.setMyPK(mypk);
		if (wk.RetrieveFromDBSources() == 0)
			return "err@当前表单已经到达:"+gb.getCurrCheckerNames()+",您不能在审核.";

		wk.setPassSta(PassSta.Passed.getValue());
		wk.setSendDT(DataType.getCurrentDateTime());
		wk.Update();

		//写入审核意见.
		bp.ccbill.Dev2Interface.WriteTrack(frmID, String.valueOf(workID), "Check", msg);
		GenerWorkers lists = new GenerWorkers();
		lists.Retrieve("WorkID", workID, "Idx", gb.getCurrIdx() + 1, "PassSta", PassSta.UnPass.getValue());
		if (lists.size() != 0)
		{
			String checkerNo = "";
			String checkerNames = "";
			String toEmps = "";
			for (GenerWorker item : lists.ToJavaList())
			{
				item.setPassSta(PassSta.Checking.getValue());
				item.setRDT(DataType.getCurrentDateTime()); //设置当前的日期.
				item.Update();
				checkerNo += item.getEmpNo() + ",";
				checkerNames += item.getEmpName();
				toEmps += item.getEmpNo() + "," + item.getEmpName() + ";";
			}
			gb.setBillState(BillState.Checking);
			gb.SetValByKey("CurrIdx", gb.getCurrIdx() + 1);
			gb.SetValByKey("CurrCheckerNos", checkerNo);
			gb.SetValByKey("CurrCheckerNames", checkerNames);
			gb.SetValByKey("SendDT", DataType.getCurrentDateTime());
			gb.SetValByKey("Sender", WebUser.getName());

			if (gb.getEmps().contains(WebUser.getNo() + ",") == false)
				gb.setEmps(gb.getEmps() +WebUser.getNo() + "," + WebUser.getName() + ";");
			gb.Update();
			bp.ccbill.Dev2Interface.WriteTrack(frmID, String.valueOf(workID), "Send", "单据审核发送,发送给" + checkerNames, checkerNo);
			return "您已经审核完成,单据流转给:" + toEmps;
		}
		//说明到了最后一个节点了.通知发起人,让其有一个待办.
		if (lists.size() == 0)
		{
			//删除所有的数据, 保留：不然回滚以后找不到人.
			//DBAccess.RunSQL("DELETE FROM FRM_GenerWorker WHERE WorkID=" + workID);

			wk.setEmpNo(gb.getStarter());
			wk.setEmpName(gb.getStarterName());
			wk.setPassSta(PassSta.Checking.getValue()); //设置审核状态.

			mypk = gb.getWorkID() + "_" + gb.getStarter() + "_200";
			wk.SetValByKey("MyPK", mypk); //设置主键.
			wk.setWorkID(workID);
			wk.setIdx(200);
			wk.Insert(); //最后一个节点结束.

			gb.SetValByKey("Msg", "单据审核完毕通知.");
		}
		if (gb.getEmps().contains(WebUser.getNo() + ",") == false)
			gb.setEmps(gb.getEmps() +WebUser.getNo() + "," + WebUser.getName() + ";");
		gb.setCurrIdx( 0);
		gb.SetValByKey("CurrCheckerNos", ""); //设置当前处理人为空.
		gb.SetValByKey("CurrCheckerNames", "");
		gb.setBillState(BillState.FlowOver);
		gb.SetValByKey("SendDT", DataType.getCurrentDateTime());
		gb.Update(); //更新

		//调用事件.
		MapData md = new MapData(frmID);
		GEEntity en = new GEEntity(frmID, workID);

		//执行单据审核结束.
		String frmEvent = ExecEvent.DoFrm(md, EventListFrm.CheckOver, en);

		bp.ccbill.Dev2Interface.WriteTrack(frmID, String.valueOf(workID), EventListFrm.CheckOver, "审核完毕.");
		if (DataType.IsNullOrEmpty(frmEvent) == true)
			return "该单据已经归档,审核完毕,通知给发起人:[" + gb.getStarter() + "," + gb.getStarterName() + "]";
		else
			return "该单据已经归档,审核完毕,通知给发起人:[" + gb.getStarter() + "," + gb.getStarterName() + "]，事件消息:" + frmEvent;
	}
	/// <summary>
	/// 撤销提交
	/// </summary>
	/// <param name="frmID"></param>
	/// <param name="workID"></param>
	/// <returns></returns>
	public static String Bill_UnSubmit(String frmID, long workID)
	{
		////如果是单据的创始人.
		//GenerBill gb = new GenerBill(workID);
		//gb.BillState = BillState.Editing;
		//gb.Emps = WebUser.No + "," + WebUser.Name + ";";
		//gb.TodoEmps = "";
		//gb.TodoEmpsNum = 0;
		//gb.CurrIdx = 0;
		//gb.CurrCheckerNos = "";
		//gb.CurrCheckerNames = "";
		//gb.Update();
		//DBAccess.RunSQL("DELETE FROM FRM_GenerWorker WHERE WorkID=" + workID);

		////执行更新单据状态.
		//BP.WF.GERpt myrpt = new BP.WF.GERpt(gb.FrmID, workID);
		//myrpt.SetValByKey("BillState", (int)BillState.Editing);
		//myrpt.Update();

		//BP.CCBill.Dev2Interface.WriteTrack(frmID, workID.ToString(), FrmActionType.UnSubmit, "撤销提交，重新编辑。");
		return "撤销成功，可以继续编辑，然后提交。";
	}
	/**
	 设置单据只读(审核)

	 @param workID 实体ID
	 */
	public static void MyBill_SetChecking(long workID) throws Exception {
		GenerBill gb = new GenerBill(workID);
		gb.setBillState (BillState.Checking);
		gb.Update();

		GEEntityOID ge = new GEEntityOID(gb.getFrmID(), gb.getWorkID());
		ge.SetValByKey("BillState", BillState.Checking.getValue());
		ge.Update();
	}
	/**
	 设置单据流程完成

	 @param workID
	 */
	public static void MyBill_SetFrmOver(long workID) throws Exception {
		GenerBill gb = new GenerBill(workID);
		gb.setBillState (BillState.FrmOver);
		gb.Update();

		GEEntityOID ge = new GEEntityOID(gb.getFrmID(), gb.getWorkID());
		ge.SetValByKey("BillState", BillState.FrmOver.getValue());
		ge.Update();
	}
	public static void MyBill_SetEditing(long workID) throws Exception {
		GenerBill gb = new GenerBill(workID);
		gb.setBillState (BillState.Editing);
		gb.Update();

		GEEntityOID ge = new GEEntityOID(gb.getFrmID(), gb.getWorkID());
		ge.SetValByKey("BillState", BillState.Editing.getValue());
		ge.Update();
	}

	/// <summary>
	/// 撤销发送:简易审核的撤销发送.
	/// </summary>
	/// <param name="frmID">表单ID</param>
	/// <param name="workID">工作ID</param>
	/// <returns>执行结果</returns>
	public static String Bill_UnSend(String frmID, long workID) throws Exception {
		GenerBill gb = new GenerBill(workID);
		String emps = "," + gb.getEmps() + ",";
		if (emps.contains(WebUser.getNo() + ",") == false)
			return "err@您不是单据审核的参与人，您不能撤销.";

		//如果是单据的创始人.
		if (gb.getStarter().equals(WebUser.getNo()) == true)
		{
			gb.setBillState(BillState.Editing);
			gb.setEmps(WebUser.getNo() + "," + WebUser.getName() + ";");
			gb.setTodoEmps("");
			gb.setTodoEmpsNum( 0);
			gb.setCurrIdx(0);
			gb.setCurrCheckerNos("");
			gb.setCurrCheckerNames( "");
			gb.Update();
			DBAccess.RunSQL("DELETE FROM FRM_GenerWorker WHERE WorkID=" + workID);

			//执行更新单据状态.
			bp.wf.GERpt myrpt = new bp.wf.GERpt(gb.getFrmID(), workID);
			myrpt.SetValByKey("BillState", BillState.Editing);
			myrpt.Update();

			//执行撤销审核.
			MapData md1 = new MapData(frmID);
			GEEntity en1 = new GEEntity(frmID, workID);
			String msg1 = ExecEvent.DoFrm(md1, "UnSend", en1);
			bp.ccbill.Dev2Interface.WriteTrack(frmID, String.valueOf(workID), FrmActionType.UnSubmit, "发起人撤销提交，重新编辑。");
			if (DataType.IsNullOrEmpty(msg1) == true)
				return "撤销成功，您是发起人可以继续编辑，然后提交。" + msg1;
			return "撤销成功，您是发起人可以继续编辑，然后提交。其他:" + msg1;
		}

		//@0=待办@1=未开始@2=已通过@3=退回

		//获取当前的人员的节点ID.
		String sql = "SELECT Idx FROM Frm_GenerWorker WHERE WorkID=" + workID + " AND EmpNo='" + WebUser.getNo() + "' AND PassSta=2 ";
		int myIdx = DBAccess.RunSQLReturnValInt(sql, 0);
		if (myIdx == 0)
			return "err@系统错误，没有找到您的审核节点.";
		if (myIdx == 0)
			return "err@系统错误，不应该是0.";

		GenerWorkers gws = new GenerWorkers();
		gws.Retrieve("WorkID", workID, "Idx");


		//把中间的节点更新为未开始.
		boolean isGotoMyIdx = false;
		boolean isGotoCurrIdx = false;
		for (GenerWorker item : gws.ToJavaList())
		{
			if (item.getIdx() == myIdx)
				isGotoMyIdx = true;

			if (item.getIdx() == gb.getCurrIdx())
				isGotoCurrIdx = true;

			if (isGotoMyIdx && isGotoCurrIdx)
				DBAccess.RunSQL("UPDATE Frm_GenerWorker SET PassSta=1  WHERE  Idx=" + item.getIdx() + " AND WorkID=" + workID);
		}

		//设置未开始.
		DBAccess.RunSQL("UPDATE Frm_GenerWorker SET PassSta=1  WHERE  Idx=" + gb.getCurrIdx() + " AND WorkID=" + workID);

		//设置他是审核状态.
		DBAccess.RunSQL("UPDATE Frm_GenerWorker SET PassSta=0  WHERE  Idx=" + myIdx + " AND WorkID=" + workID);
		//  PassSta.
		//设置为归档状态.
		gb.setBillState(BillState.Checking);
		gb.setCurrIdx(myIdx);
		gb.setCurrCheckerNos(WebUser.getNo());
		gb.setCurrCheckerNames( WebUser.getName());
		gb.setEmps(gb.getEmps().replace(WebUser.getNo() + "," + WebUser.getName() + ";", "")); // 1.0去掉审核人.
		gb.Update();
		//执行撤销审核.
		MapData md = new MapData(frmID);
		GEEntity en = new GEEntity(frmID, workID);
		String msg = ExecEvent.DoFrm(md, "UnSend", en);

		bp.ccbill.Dev2Interface.WriteTrack(frmID, String.valueOf(workID), FrmActionType.UnSubmit, "撤销提交");
		if (DataType.IsNullOrEmpty(msg) == true)
			return "撤销成功" + msg;
		else
			return "撤销成功:" + msg;
	}
	public static String UnSubmitWork(String frmID, long workID) throws Exception {
		FrmBill fb = new FrmBill(frmID);

		GenerBill gb = new GenerBill();

		//设置为归档状态.
		gb.setBillState(BillState.Editing);

		//创建rpt.
		bp.wf.GERpt rpt = new bp.wf.GERpt(gb.getFrmID(), workID);

		if (fb.getEntityType() == EntityType.EntityTree || fb.getEntityType() == EntityType.FrmDict)
		{
			gb.setTitle(rpt.getTitle());
			gb.Update();
			return "提交成功...";
		}

		//单据编号.
		if (DataType.IsNullOrEmpty(gb.getBillNo()) == true && !(fb.getEntityType() == EntityType.EntityTree || fb.getEntityType() == EntityType.FrmDict))
		{
			gb.setBillNo(bp.ccbill.Dev2Interface.GenerBillNo(fb.getBillNoFormat(), workID, null, fb.getPTable()));
			//更新单据里面的billNo字段.
			if (DBAccess.IsExitsTableCol(fb.getPTable(), "BillNo") == true)
				DBAccess.RunSQL("UPDATE " + fb.getPTable() + " SET BillNo='" + gb.getBillNo() + "' WHERE OID=" + workID);
		}

		//标题.
		if (DataType.IsNullOrEmpty(gb.getTitle()) == true && !(fb.getEntityType() == EntityType.EntityTree || fb.getEntityType() == EntityType.FrmDict))
		{
			gb.setTitle(Dev2Interface.GenerTitle(fb.getTitleRole(), rpt));
			//更新单据里面的 Title 字段.
			if (DBAccess.IsExitsTableCol(fb.getPTable(), "Title") == true)
				DBAccess.RunSQL("UPDATE " + fb.getPTable() + " SET Title='" + gb.getTitle() + "' WHERE OID=" + workID);
		}

		gb.Update();

		//把通用的字段更新到数据库.
		rpt.setTitle(gb.getTitle());
		rpt.setBillNo(gb.getBillNo());
		rpt.Update();

		//增加到日志.
		bp.ccbill.Dev2Interface.WriteTrack(frmID, String.valueOf(workID), FrmActionType.UnSubmit, "撤销提交.");
		return "提交成功...";
	}
	/**
	 提交

	 @param frmID 表单ID
	 @param workID 工作ID
	 @return 返回保存结果
	*/
	public static String SubmitWork(String frmID, long workID) throws Exception {
		FrmBill fb = new FrmBill(frmID);

		GenerBill gb = new GenerBill();
		gb.setWorkID(workID);
		int i = gb.RetrieveFromDBSources();
		if (i == 0)
		{
			return "";
		}

		//设置为归档状态.
		gb.setBillState(BillState.FlowOver);

		//创建rpt.
		GERpt rpt = new GERpt(gb.getFrmID(), workID);

		if (fb.getEntityType() == EntityType.EntityTree || fb.getEntityType() == EntityType.FrmDict)
		{
			gb.setTitle(rpt.getTitle());
			gb.Update();
			return "提交成功...";
		}

		//单据编号.
		if (DataType.IsNullOrEmpty(gb.getBillNo()) == true && !(fb.getEntityType() == EntityType.EntityTree || fb.getEntityType() == EntityType.FrmDict))
		{
			gb.setBillNo(bp.ccbill.Dev2Interface.GenerBillNo(fb.getBillNoFormat(), workID, null, fb.getPTable()));
			//更新单据里面的billNo字段.
			if (DBAccess.IsExitsTableCol(fb.getPTable(), "BillNo") == true)
			{
				DBAccess.RunSQL("UPDATE " + fb.getPTable() + " SET BillNo='" + gb.getBillNo() + "' WHERE OID=" + workID);
			}
		}

		//标题.
		if (DataType.IsNullOrEmpty(gb.getTitle()) == true && !(fb.getEntityType() == EntityType.EntityTree || fb.getEntityType() == EntityType.FrmDict))
		{
			gb.setTitle(Dev2Interface.GenerTitle(fb.getTitleRole(), rpt));
			//更新单据里面的 Title 字段.
			if (DBAccess.IsExitsTableCol(fb.getPTable(), "Title") == true)
			{
				DBAccess.RunSQL("UPDATE " + fb.getPTable() + " SET Title='" + gb.getTitle() + "' WHERE OID=" + workID);
			}
		}

		gb.Update();

		//把通用的字段更新到数据库.
		rpt.setTitle(gb.getTitle());
		rpt.setBillNo(gb.getBillNo());
		rpt.Update();

		bp.ccbill.Dev2Interface.WriteTrack(frmID, String.valueOf(workID), FrmActionType.Submit, "执行提交.");


		return "提交成功...";
	}
	/**
	 保存

	 @param frmID 表单ID
	 @param workID 工作ID
	 @return 返回保存结果
	*/
	public static String SaveAsDraft(String frmID, long workID) throws Exception {
		GenerBill gb = new GenerBill(workID);
		if (gb.getBillState() != BillState.Blank)
		{
			return "err@只有在None的模式下才能保存草稿。";
		}

		if (gb.getBillState() != BillState.Editing)
		{
			gb.setBillState(BillState.Editing);
			gb.Update();
		}
		return "保存成功...";
	}
	/// <summary>
	/// 设置审核人员
	/// </summary>
	/// <param name="workID">工作ID</param>
	/// <param name="empNos">人员编号，按照顺序执行.</param>
	/// <returns>返回执行结果</returns>
	public static String MyBill_GenerWorkerByEmpNos(long workID, String empNos) throws Exception {
		GenerBill gb = new GenerBill(workID);
		if (gb.getBillState() == BillState.FlowOver)
			throw new Exception("err@当前是归档状态，您不能设置工作人员。");

		//删除现在的数据.
		DBAccess.RunSQL("DELETE FROM Frm_GenerWorker WHERE WorkID=" + workID);
		GenerWorker worker = new GenerWorker();
		String[] emps = empNos.split(",");
		for (int i = 0; i < emps.length; i++)
		{
			String empNo = emps[i].trim();
			bp.port.Emp emp = new bp.port.Emp(empNo);

			String mypk = i + "_" + workID + "_" + emp.getNo();
			worker.SetValByKey("Idx", i); //设置步骤.
			worker.SetValByKey("WorkID", gb.getWorkID()); //设置工作ID.
			worker.SetValByKey("EmpNo", empNo); //设置人员信息.
			worker.SetValByKey("EmpName", emp.getName());

			//设置状态.
			if (i == 0)
			{
				worker.SetValByKey("PassSta", 0);
				gb.SetValByKey("CurrCheckerNos", emp.getNo());
				gb.SetValByKey("CurrCheckerNames", emp.getName());
				gb.SetValByKey("CurrIdx", 1);
			}
			else
				worker.SetValByKey("PassSta", 1); //为未执行的状态.
			worker.setMyPK(mypk); //设置主键.
			worker.Insert();
		}

		//gb.setIdx(0);
		gb.setBillState(BillState.Checking); //审核中.
		//设置人员.
		gb.setEmps(WebUser.getNo() + "," + WebUser.getName() + ";");
		gb.Update(); //更新.

		return "设置成功.";
	}
	/// <summary>
	/// 删除单据
	/// </summary>
	/// <param name="frmID"></param>
	/// <param name="workID"></param>
	/// <returns>执行结果</returns>
	public static String MyBill_Delete(String frmID, long workID) throws Exception {

		//整理参数.
		DBRoles rols = new DBRoles();
		rols.Retrieve("FrmID", frmID, "DBRole", "RecDelete"); //获得删除规则.
		String mydepts = "" + WebUser.getDeptNo() + ","; //我的部门.
		String mystas = ""; //我的角色.
		DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.getUserID() + "'");
		for (DataRow dr : mydeptsDT.Rows)
		{
			mydepts += dr.get(0).toString() + ",";
			mystas += dr.get(1).toString() + ",";
		}

		FrmBill fb = new FrmBill(frmID);

		GEEntityOID billEn = new GEEntityOID(frmID, workID);
		//执行删除.
		return MyBill_DeleteExt(fb, rols, billEn, mydepts, mystas);
	}
	/**
	 删除单据

	 @param fb 权限实体
	 @param dbRols 权限实体
	 @param dbRols 权限实体
	 @param billEn 单据实体
	 @param mydepts 部门编号
	 @param mystas 岗位编号
	 @return
	 */
	private static String MyBill_DeleteExt(FrmBill fb, DBRoles dbRols, GEEntityOID billEn, String mydepts, String mystas) throws Exception {
		if (dbRols.size() == 0)
		{
			billEn.Delete();
			return "单据:" + billEn.GetValStringByKey("BillNo") + ",标题:" + billEn.GetValStringByKey("Title") + ",删除成功.";
		}

		String starter = billEn.GetValStringByKey("Starter"); //发起人.
		String deptNo = billEn.GetValStringByKey("DeptNo");
		String orgNo = billEn.GetValStringByKey("OrgNo");
		String billNo = billEn.GetValStringByKey("BillNo");
		String title = billEn.GetValStringByKey("Title");

		//是否可以删除?
		boolean isDelete = false;
		for (DBRole item : dbRols.ToJavaList())
		{
			if (item.getMarkID().equals("None") == true)
			{
				isDelete = true;
				break;
			}
			if (item.getMarkID().equals("SelfOnly") == true && starter.equals(WebUser.getNo()) == true)
			{
				isDelete = true;
				break;
			}
			if (item.getMarkID().equals("DeptLeader") == true)
			{
				String deptLeader = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Dept WHERE No='" + deptNo + "'");
				if (deptLeader != null && deptLeader.equals(WebUser.getNo()) == true)
				{
					isDelete = true;
					break;
				}
			}

			if (item.getMarkID().equals("ByStations") == true && DataType.IsHaveIt(item.getDocs(), mystas) == true)
			{
				isDelete = true;
				break;
			}
			if (item.getMarkID().equals("ByDepts") == true && DataType.IsHaveIt(item.getDocs(), mydepts) == true)
			{
				isDelete = true;
				break;
			}
			if (item.getMarkID().equals("ByEmps") == true && DataType.IsHaveIt(item.getDocs(), "," + WebUser.getNo() + ",") == true)
			{
				isDelete = true;
				break;
			}
			if (item.getMarkID().equals("Adminer") == true && WebUser.getNo().equals("admin") == true)
			{
				isDelete = true;
				break;
			}
			if (item.getMarkID().equals("Admin2") == true && WebUser.getIsAdmin() == true)
			{
				isDelete = true;
				break;
			}
			if (item.getMarkID().equals("SQL") == true)
			{
				String sql = bp.wf.Glo.DealExp(item.getDocs(), null, "");
				if (DBAccess.RunSQLReturnValFloat(sql) > 0)
				{
					isDelete = true;
					break;
				}
			}
		}
		if (isDelete == false)
		{
			return "您没有权限删除单号[" + billNo + "]标题为[" + title + "]的数据.";
		}

		//检查是否是逻辑删除.
		if (dbRols.GetEntityByKey("MarkID", "DeleteByFlag") == null)
		{
			String sqls = "DELETE FROM Frm_GenerBill WHERE WorkID=" + billEn.getOID();
			sqls += "@DELETE FROM Frm_GenerWorker WHERE WorkID=" + billEn.getOID();
			sqls += "@DELETE FROM " + fb.getPTable() + " WHERE OID=" + billEn.getOID();
			DBAccess.RunSQLs(sqls);
		}
		else
		{
			String sqls = "DELETE FROM Frm_GenerBill WHERE WorkID=" + billEn.getOID();
			sqls += "@DELETE FROM Frm_GenerWorker WHERE WorkID=" + billEn.getOID();
			sqls += "@UPDATE " + fb.getPTable() + " SET BillState=-1  WHERE OID=" + billEn.getOID();
			DBAccess.RunSQLs(sqls);
		}
		return "单号[" + billNo + "]删除成功.";
	}

	public static String MyBill_DeleteBills(String frmID, String workIds) throws Exception {
		//1. 整理参数.
		DBRoles rols = new DBRoles();
		rols.Retrieve("FrmID", frmID, "DBRole", "RecDelete");
		String mydepts = "" + WebUser.getDeptNo() + ","; //我的部门.
		String mystas = ""; //我的角色.
		DataTable mydeptsDT = DBAccess.RunSQLReturnTable("SELECT FK_Dept,FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.getUserID() + "'");
		for (DataRow dr : mydeptsDT.Rows)
		{
			mydepts += dr.get(0).toString() + ",";
			mystas += dr.get(1).toString() + ",";
		}
		FrmBill fb = new FrmBill(frmID);

		String[] workids = workIds.split("[,]", -1);

		String msg = "";
		int idx = 0;
		for (String workID : workids)
		{
			if (DataType.IsNullOrEmpty(workID) == true)
			{
				continue;
			}

			GEEntityOID billEn = new GEEntityOID(frmID, Long.parseLong(workID));

			//执行删除消息
			idx++;
			msg += "第"+idx+"条,删除消息:" + MyBill_DeleteExt(fb, rols, billEn, mydepts, mystas);
		}
		return msg;
	}

	/**
	 删除实体

	 @param frmID
	 @param workID
	 @return
	*/
	public static String MyDict_Delete(String frmID, long workID) throws Exception {
		FrmBill fb = new FrmBill(frmID);
		String sql = "@DELETE FROM " + fb.getPTable() + " WHERE OID=" + workID;
		DBAccess.RunSQLs(sql);
		// log
		JSONObject log = new JSONObject();
		log.put("frmID", frmID);
		log.put("workID", workID);
		log.put("sql", sql);
		bp.sys.base.Glo.WriteUserLog(log.toString(), "低代码实体删除");
		// end
		return "删除成功.";
	}
	/// <summary>
	/// 删除
	/// </summary>
	/// <param name="frmID"></param>
	/// <param name="nos"></param>
	/// <returns></returns>
	public static String MyEntityNoName_Deletes(String frmID, String nos) throws Exception {
		bp.ccbill.template.Collection collection = new bp.ccbill.template.Collection();
		collection.setNo( frmID + "_Delete");
		collection.Retrieve();
		int deleteRole = collection.GetValIntByKey(CollectionAttr.IsEnable);
		MapData fb = new MapData(frmID);

		String[] strs = nos.split(",");

		for (String no : strs)
		{
			if (StringUtil.isNullOrEmpty(no) == true)
				continue;

			if (deleteRole == 1)
			{
				String sql = "DELETE FROM " + fb.getPTable() + " WHERE No='"+ no + "'";
				DBAccess.RunSQLs(sql);
			}
			if (deleteRole == 2)
			{
				DBAccess.RunSQLs("UPDATE  " + fb.getPTable() + " SET EntityState=-1 WHERE No='"+no+"'");
			}
		}
		return "删除成功.";
	}

	/**
	 删除实体单据

	 @param frmID
	 @param workIds
	 @return
	*/
	public static String MyDict_DeleteDicts(String frmID, String workIds) throws Exception {
		bp.ccbill.template.Collection collection = new bp.ccbill.template.Collection() ;
		collection.setNo(frmID+"_Delete");
		collection.Retrieve();
		int deleteRole = collection.GetValIntByKey(CollectionAttr.IsEnable);
		FrmBill fb = new FrmBill(frmID);
		String sql = "";
		if(deleteRole == 1){
			//物理删除
			sql = "DELETE FROM " + fb.getPTable() + " WHERE OID in (" + workIds + ")";
			DBAccess.RunSQLs(sql);
			// log
			JSONObject log = new JSONObject();
			log.put("frmID", frmID);
			log.put("workID", workIds);
			log.put("sql", sql);
			bp.sys.base.Glo.WriteUserLog(log.toString(), "低代码实体批量物理删除");
		}
		if(deleteRole == 2){
			sql = "UPDATE " + fb.getPTable() + " SET BillState=-1 WHERE OID in (" + workIds + ")";
			DBAccess.RunSQLs(sql);
			JSONObject log = new JSONObject();
			log.put("frmID", frmID);
			log.put("workID", workIds);
			log.put("sql", sql);
			bp.sys.base.Glo.WriteUserLog(log.toString(), "低代码实体批量逻辑删除");
		}
		// end
		return "删除成功.";
	}
	/**
	 删除树形结构的实体表单
	 @param frmID
	 @param billNo
	 @return
	*/
	public static String MyEntityTree_Delete(String frmID, String billNo) throws Exception {
		FrmBill fb = new FrmBill(frmID);
		String sql = "DELETE FROM " + fb.getPTable() + " WHERE BillNo='" + billNo + "' OR ParentNo='" + billNo + "'";
		DBAccess.RunSQLs(sql);
		return "删除成功.";
	}
	/// <summary>
	/// 表单数据复制
	/// </summary>
	/// <param name="frmID">表单ID</param>
	/// <param name="workid">WorkID</param>
	/// <returns>复制的主表数据</returns>
	public static String FlowFrm_Copy(String frmID, int workid) throws Exception {
		//旧的WorkID
		int oldWorkID = workid;
		//复制主表数据（包括二进制）
		GEEntityOID en = new GEEntityOID(frmID, oldWorkID);
		en.SaveAsNew();
		//新的WorkID
		long newWorkID = en.getOID();

		//判断是否有从表，并进行复制
		MapDtls mds = new MapDtls(frmID);
		if(mds != null && mds.size() > 0)
		{
			//遍历从表s
			for(MapDtl mdtl : mds.ToJavaList())
			{
				//获取旧的从表数据
				GEDtls dtls = new GEDtls(mdtl.getNo());
				dtls.Retrieve(GEDtlAttr.RefPK, oldWorkID);

				//遍历旧的从表数据
				for(GEDtl gedtl : dtls.ToJavaList())
				{
					//替换新的RefPK
					gedtl.setRefPK(String.valueOf(newWorkID));
					//执行插入，开始复制从表数据
					gedtl.SaveAsNew();
				}
			}
		}

		return en.ToJson();
	}
	/**
	 复制单据数据

	 @param frmID
	 @param workID
	 @return
	*/
	public static String MyBill_Copy(String frmID, long workID) throws Exception {
		//获取单据的属性
		FrmBill fb = new FrmBill(frmID);

		GenerBill gb = new GenerBill();
		gb.setWorkID(DBAccess.GenerOID("WorkID"));
		gb.setBillState(BillState.Editing); //初始化状态.
		gb.setStarter(WebUser.getNo());
		gb.setStarterName(WebUser.getName());
		gb.setFrmName(fb.getName()); //单据名称.
		gb.setFrmID(fb.getNo()); //单据ID

		gb.setFrmTreeNo(fb.getFormTreeNo()); //单据类别.
		gb.setRDT(DataType.getCurrentDateTime());
		//gb.setIdx(1);
		gb.setIdxName("启动");

		//创建rpt.
		GERpt rpt = new GERpt(frmID, workID);

		//设置标题.
		gb.setTitle(Dev2Interface.GenerTitle(fb.getTitleRole(), rpt));
		gb.setBillNo(bp.ccbill.Dev2Interface.GenerBillNo(fb.getBillNoFormat(), gb.getWorkID(), null, frmID));

		gb.DirectInsert(); //执行插入.

		//更新基础的数据到表单表.
		rpt.SetValByKey("BillState", gb.getBillState().getValue());
		rpt.SetValByKey("Starter", gb.getStarter());
		rpt.SetValByKey("StarterName", gb.getStarterName());
		rpt.SetValByKey("RDT", gb.getRDT());
		rpt.SetValByKey("Title", gb.getTitle());
		rpt.SetValByKey("BillNo", gb.getBillNo());
		rpt.setOID(gb.getWorkID());
		rpt.InsertAsOID(gb.getWorkID());

			///#region 复制其他数据.

		//复制明细。
		MapDtls dtls = new MapDtls(frmID);
		if (!dtls.isEmpty())
		{
			for (MapDtl dtl : dtls.ToJavaList())
			{
				if (dtl.getItIsCopyNDData() == false)
				{
					continue;
				}

				//new 一个实例.
				GEDtl dtlData = new GEDtl(dtl.getNo());

				GEDtls dtlsFromData = new GEDtls(dtl.getNo());
				dtlsFromData.Retrieve(GEDtlAttr.RefPK, workID, null);
				for (GEDtl geDtlFromData : dtlsFromData.ToJavaList())
				{
					//是否启用多附件
					FrmAttachmentDBs dbs = null;
					if (dtl.getItIsEnableAthM() == true)
					{
						//根据从表的OID 获取附件信息
						dbs = new FrmAttachmentDBs();
						dbs.Retrieve(FrmAttachmentDBAttr.RefPKVal, geDtlFromData.getOID(), null);
					}

					dtlData.Copy(geDtlFromData);
					dtlData.setRefPK(String.valueOf(rpt.getOID()));
					dtlData.InsertAsNew();
					if (dbs != null && !dbs.isEmpty())
					{
						//复制附件信息
						FrmAttachmentDB newDB = new FrmAttachmentDB();
						for (FrmAttachmentDB db : dbs.ToJavaList())
						{
							newDB.Copy(db);
							newDB.setRefPKVal( String.valueOf(dtlData.getOID()));
							newDB.setFID( dtlData.getOID());
							newDB.setMyPK(DBAccess.GenerGUID(0, null, null));
							newDB.Insert();
						}
					}

				}
			}

		}

		//获取附件组件、
		FrmAttachments athDecs = new FrmAttachments(frmID);
		//复制附件数据。
		if (!athDecs.isEmpty())
		{
			for (FrmAttachment athDec : athDecs.ToJavaList())
			{
				FrmAttachmentDBs aths = new FrmAttachmentDBs();
				aths.Retrieve(FrmAttachmentDBAttr.FK_FrmAttachment, athDec.getMyPK(), FrmAttachmentDBAttr.RefPKVal, workID, null);
				for (FrmAttachmentDB athDB : aths.ToJavaList())
				{
					FrmAttachmentDB athDB_N = new FrmAttachmentDB();
					athDB_N.Copy(athDB);
					athDB_N.setRefPKVal( String.valueOf(rpt.getOID()));
					athDB_N.setMyPK(DBAccess.GenerGUID(0, null, null));
					athDB_N.Insert();
				}
			}
		}

		///#endregion 复制表单其他数据.
		bp.ccbill.Dev2Interface.WriteTrack(frmID, String.valueOf(workID), "复制", "执行复制");

		return "复制成功.";
	}

	public static DataTable DB_StartDicts()
	{
		String sql = "SELECT A.No,A.Name,B.Name AS SortName,A.Icon FROM Sys_MapData A, Sys_FormTree B ";
		sql += "  WHERE  A.FK_FormTree =B.No and A.EntityType =2  AND A.OrgNo='" + bp.web.WebUser.getOrgNo() + "' ";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "No";
			dt.Columns.get(1).ColumnName = "Name";
			dt.Columns.get(2).ColumnName = "SortName";
			dt.Columns.get(3).ColumnName = "Icon";
		}
		return dt;
	}

	/**
	 获得发起列表

	 @param empID
	 @return
	*/
	public static DataTable DB_StartBills(String empID) throws Exception {
		String sql = "SELECT A.No,A.Name,B.Name AS SortName,A.Icon FROM Sys_MapData A, Sys_FormTree B ";
		sql += "  WHERE  A.FK_FormTree =B.No and A.EntityType =1 ";

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			sql += " AND A.OrgNo='" + bp.web.WebUser.getOrgNo() + "' ";
		}

		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "No";
			dt.Columns.get(1).ColumnName = "Name";
			dt.Columns.get(2).ColumnName = "SortName";
			dt.Columns.get(3).ColumnName = "Icon";
		}
		return dt;
	}

	/**
	 * 近期发起的单据
	 * @param empID
	 * @return
	 */
	public static DataTable DB_Recent(String empID) throws Exception {
		GenerBills gbs = new GenerBills();
		QueryObject qo = new QueryObject(gbs);

		qo.addLeftBracket();
		qo.AddWhere(GenerBillAttr.CurrCheckerNos, " NOT LIKE ", "%" + WebUser.getNo() + "%");
		qo.addAnd();
		qo.AddWhere(GenerBillAttr.Emps, " LIKE ", "%" + WebUser.getNo() + "%");
		qo.addRightBracket();

		qo.addAnd();

		qo.AddWhere(GenerBillAttr.BillState, " >= ",1);
		qo.addOrderBy("RDT");
		qo.Top = 200;
		qo.DoQuery();
		return gbs.ToDataTableField();
	}

	public static DataTable DB_Todolist() throws Exception {

		return DB_Todolist(WebUser.getNo(),null);
	}
	/**
	 获得待办列表

	 @param empID
	 @return
	*/
	public static DataTable DB_Todolist(String empID) throws Exception {
		GenerBills gbs = new GenerBills();
		gbs.RetrieveAll();
		return gbs.ToDataTableField();
	}
	/// <summary>
	/// 近期发起的单据
	/// </summary>
	/// <param name="empID"></param>
	/// <returns></returns>
	public static DataTable DB_Todolist(String empNo, String frmID) throws Exception {
		if (empNo == null)
			empNo = WebUser.getNo();

		String sql = "";
		sql = "SELECT  A.BillNo,A.WorkID,A.Title,A.FrmID,A.FrmName,A.Starter,A.StarterName,A.RDT,B.EmpNo,B.EmpName,B.DeptNo,B.DeptName,B.Idx,A.BillState,A.AtPara, ";
		sql += " A.RDT,A.Sender,A.SendDT,A.SDTOfNode,B.RDT AS ADT ";
		sql += " FROM Frm_GenerBill A, FRM_GenerWorker B ";
		sql += " WHERE a.WorkID=B.WorkID AND (B.PassSta=3 OR B.PassSta=5 OR B.PassSta=0 )  ";
		sql += " AND B.EmpNo='" + empNo + "'";//5=退回，3=审核中,0=待办审核中.
		if (DataType.IsNullOrEmpty(frmID) == false)
			sql += " AND B.FrmID='" + frmID + "'";

		DataTable dt = null;
		try
		{
			dt = DBAccess.RunSQLReturnTable(sql);
		}
		catch(Exception ex)
		{
			GenerBill gb = new GenerBill();
			gb.CheckPhysicsTable();
			GenerWorker gw = new GenerWorker();
			gw.CheckPhysicsTable();
			dt = DBAccess.RunSQLReturnTable(sql);
		}
		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("WORKID").ColumnName = "WorkID";
			dt.Columns.get("TITLE").ColumnName = "Title";
			dt.Columns.get("FRMID").ColumnName = "FrmID";
			dt.Columns.get("BILLNO").ColumnName = "BillNo";
			dt.Columns.get("ATPARA").ColumnName = "AtPara";

			dt.Columns.get("RDT").ColumnName = "RDT";
			dt.Columns.get("SENDER").ColumnName = "Sender";
			dt.Columns.get("SENDDT").ColumnName = "SendDT";
			dt.Columns.get("SDTOFNODE").ColumnName = "SDTOfNode";

			dt.Columns.get("FRMNAME").ColumnName = "FrmName";
			dt.Columns.get("STARTER").ColumnName = "Starter";
			dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
			dt.Columns.get("RDT").ColumnName = "RDT";
			dt.Columns.get("EMPNO").ColumnName = "EmpNo";
			dt.Columns.get("EMPNAME").ColumnName = "EmpName";
			dt.Columns.get("DEPTNO").ColumnName = "DeptNo";
			dt.Columns.get("DEPTNAME").ColumnName = "DeptName";
			dt.Columns.get("IDX").ColumnName = "Idx";
		}

		if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("workid").ColumnName = "WorkID";
			dt.Columns.get("title").ColumnName = "Title";
			dt.Columns.get("billno").ColumnName = "BillNo";
			dt.Columns.get("atpara").ColumnName = "AtPara";

			dt.Columns.get("frmid").ColumnName = "FrmID";
			dt.Columns.get("frmname").ColumnName = "FrmName";
			dt.Columns.get("starter").ColumnName = "Starter";
			dt.Columns.get("startername").ColumnName = "StarterName";
			dt.Columns.get("rdt").ColumnName = "RDT";
			dt.Columns.get("sender").ColumnName = "Sender";
			dt.Columns.get("senddt").ColumnName = "SendDT";
			dt.Columns.get("sdtofnode").ColumnName = "SDTOfNode";

			dt.Columns.get("rdt").ColumnName = "RDT";
			dt.Columns.get("empno").ColumnName = "EmpNo";
			dt.Columns.get("empname").ColumnName = "EmpName";
			dt.Columns.get("deptno").ColumnName = "DeptNo";
			dt.Columns.get("deptname").ColumnName = "DeptName";
			dt.Columns.get("idx").ColumnName = "Idx";
		}
		return dt;
	}
	/**
	 草稿列表

	 @param frmID 单据ID
	 @param empID 操作员
	 @return
	*/
	public static DataTable DB_Draft(String frmID, String empID) throws Exception {
		if (DataType.IsNullOrEmpty(empID) == true)
		{
			empID = WebUser.getNo();
		}

		GenerBills bills = new GenerBills();
		bills.Retrieve(GenerBillAttr.FrmID, frmID, GenerBillAttr.Starter, empID, null);

		return bills.ToDataTableField("dt");
	}

	public static String GenerTitle(String titleRole, Entity wk) throws Exception {
		if (DataType.IsNullOrEmpty(titleRole))
		{
			// 为了保持与ccflow4.5的兼容,从开始节点属性里获取.
			Attr myattr = wk.getEnMap().getAttrs().GetAttrByKey("Title");
			if (myattr == null)
			{
				myattr = wk.getEnMap().getAttrs().GetAttrByKey("Title");
			}

			if (myattr != null)
			{
				titleRole = myattr.getDefaultVal().toString();
			}

			if (DataType.IsNullOrEmpty(titleRole) || titleRole.contains("@") == false)
			{
				titleRole = "@WebUser.DeptName-@WebUser.No,@WebUser.Name在@RDT发起.";
			}
		}

		if (Objects.equals(titleRole, "@OutPara") || DataType.IsNullOrEmpty(titleRole) == true)
		{
			titleRole = "@WebUser.DeptName-@WebUser.No,@WebUser.Name在@RDT发起.";
		}


		titleRole = titleRole.replace("@WebUser.No", WebUser.getNo());
		titleRole = titleRole.replace("@WebUser.Name", WebUser.getName());
		titleRole = titleRole.replace("@WebUser.DeptNameOfFull", WebUser.getDeptNameOfFull());
		titleRole = titleRole.replace("@WebUser.DeptName", WebUser.getDeptName());
		titleRole = titleRole.replace("@WebUser.DeptNo", WebUser.getDeptNo());
		titleRole = titleRole.replace("@RDT", DataType.getCurrentDateByFormart("yy年MM月dd日HH时mm分"));
		if (titleRole.contains("@"))
		{
			Attrs attrs = wk.getEnMap().getAttrs();

			// 优先考虑外键的替换,因为外键文本的字段的长度相对较长。
			for (Attr attr : attrs)
			{
				if (titleRole.contains("@") == false)
				{
					break;
				}
				if (attr.getItIsRefAttr()  == false)
				{
					continue;
				}
				titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
			}

			//在考虑其它的字段替换.
			for (Attr attr : attrs)
			{
				if (titleRole.contains("@") == false)
				{
					break;
				}

				if (attr.getItIsRefAttr()  == true)
				{
					continue;
				}
				titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
			}
		}
		titleRole = titleRole.replace('~', '-');
		titleRole = titleRole.replace("'", "”");

		// 为当前的工作设置title.
		wk.SetValByKey("Title", titleRole);
		return titleRole;
	}
	/**
	 生成单据编号

	 @param billNo 单据编号规则
	 @param workid 工作ID
	 @param en 实体类
	 @param frmID 表单ID
	 @return 生成的单据编号
	*/
	public static String GenerBillNo(String billNo, long workid, Entity en, String frmID)
	{
		if (DataType.IsNullOrEmpty(billNo))
		{
			billNo = "3";
		}

		if (billNo.contains("@"))
		{
			billNo = bp.wf.Glo.DealExp(billNo, en, null);
		}

		/*如果，Bill 有规则 */
		billNo = billNo.replace("{YYYY}", DataType.getCurrentDateByFormart("yyyy"));
		billNo = billNo.replace("{yyyy}", DataType.getCurrentDateByFormart("yyyy"));

		billNo = billNo.replace("{yy}", DataType.getCurrentDateByFormart("yy"));
		billNo = billNo.replace("{YY}", DataType.getCurrentDateByFormart("yy"));

		billNo = billNo.replace("{MM}", DataType.getCurrentDateByFormart("MM"));
		billNo = billNo.replace("{mm}", DataType.getCurrentDateByFormart("MM"));

		billNo = billNo.replace("{DD}", DateUtils.format(new Date(), "DD"));
		billNo = billNo.replace("{dd}", DateUtils.format(new Date(), "dd"));
		billNo = billNo.replace("{HH}", DateUtils.format(new Date(), "HH"));
		billNo = billNo.replace("{hh}", DateUtils.format(new Date(), "hh"));

		billNo = billNo.replace("{LSH}", String.valueOf(workid));
		billNo = billNo.replace("{WorkID}", String.valueOf(workid));
		billNo = billNo.replace("{OID}", String.valueOf(workid));

		if (billNo.contains("@WebUser.DeptZi"))
		{
			String val = DBAccess.RunSQLReturnStringIsNull("SELECT Zi FROM Port_Dept WHERE No='" + WebUser.getDeptNo() + "'", "");
			billNo = billNo.replace("@WebUser.DeptZi", val.toString());
		}

		String sql = "";
		int num = 0;
		String supposeBillNo = billNo; //假设单据号，长度与真实单据号一致
		ArrayList<java.util.Map.Entry<Integer, Integer>> loc = new java.util.ArrayList<java.util.Map.Entry<Integer, Integer>>(); //流水号位置，流水号位数
		String lsh; //流水号设置码
		int lshIdx = -1; //流水号设置码所在位置
		java.util.Map<Integer, Integer>  map = new HashMap<Integer, Integer>();
		for (int i = 2; i < 9; i++)
		{
			lsh = "{LSH" + i + "}";

			if (!supposeBillNo.contains(lsh))
			{
				continue;
			}

			while (supposeBillNo.contains(lsh))
			{
				//查找流水号所在位置
				lshIdx = supposeBillNo.indexOf(lsh);
				//将找到的流水号码替换成假设的流水号
				supposeBillNo = (lshIdx == 0 ? "" : supposeBillNo.substring(0, lshIdx)) + bp.tools.StringHelper.padLeft("", i, '_') + (lshIdx + 6 < supposeBillNo.length() ? supposeBillNo.substring(lshIdx + 6) : "");
				//保存当前流程号所处位置，及流程号长度，以便之后使用替换成正确的流水号
				map.put(lshIdx, i);
			}
		}
		Iterator<java.util.Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			java.util.Map.Entry<Integer, Integer> entry = iterator.next();
			loc.add(entry);
		}
		//数据库中查找符合的单据号集合,NOTE:此处需要注意，在LIKE中带有左广方括号时，要使用一对广播号将其转义
		sql = "SELECT BillNo FROM Frm_GenerBill WHERE BillNo LIKE '" + supposeBillNo.replace("[", "[[]") + "'" + " AND WorkID <> " + workid + " AND FrmID ='" + frmID + "' " + " ORDER BY BillNo DESC ";

		String maxBillNo = DBAccess.RunSQLReturnString(sql);
		int ilsh = 0;

		if (DataType.IsNullOrEmpty(maxBillNo))
		{
			//没有数据，则所有流水号都从1开始
			for (java.util.Map.Entry<Integer, Integer> kv : loc)
			{
				supposeBillNo = (kv.getKey() == 0 ? "" : supposeBillNo.substring(0, kv.getKey())) + bp.tools.StringHelper.padLeft("1", kv.getValue(), '0') + (kv.getKey() + kv.getValue() < supposeBillNo.length() ? supposeBillNo.substring(kv.getKey() + kv.getValue()) : "");
			}
		}
		else
		{
			//有数据，则从右向左开始判断流水号，当右侧的流水号达到最大值，则左侧的流水号自动加1
			HashMap<Integer, Integer> mlsh = new HashMap<Integer, Integer>();
			int plus1idx = -1;

			for (int i = loc.size() - 1; i >= 0; i--)
			{
				//获取单据号中当前位的流水码数
				ilsh = Integer.parseInt(bp.tools.StringHelper.substring(maxBillNo, loc.get(i).getKey(), loc.get(i).getValue()));

				if (plus1idx >= 0)
				{
					//如果当前码位被置为+1，则+1，同时将标识置为-1
					ilsh++;
					plus1idx = -1;
				}
				else
				{
					mlsh.put(loc.get(i).getKey(), i == loc.size() - 1 ? ilsh + 1 : ilsh);
					continue;
				}

				if (ilsh >= Integer.parseInt(bp.tools.StringHelper.padLeft("", loc.get(i).getValue(), '9')))
				{
					//右侧已经达到最大值
					if (i > 0)
					{
						//记录前位的码
						mlsh.put(loc.get(i).getKey(), 1);
					}
					else
					{
						supposeBillNo = "单据号超出范围";
						break;
					}

					//则将前一个流水码位，标记为+1
					plus1idx = i - 1;
				}
				else
				{
					mlsh.put(loc.get(i).getKey(), ilsh + 1);
				}
			}

			if (supposeBillNo.equals("单据号超出范围"))
			{
				return supposeBillNo;
			}

			//拼接单据号
			for (Map.Entry<Integer, Integer> kv : loc)
			{
				supposeBillNo = (kv.getKey() == 0 ? "" : supposeBillNo.substring(0, kv.getKey())) + StringHelper.padLeft(mlsh.get(kv.getKey()).toString(), kv.getValue(), '0') + (kv.getKey() + kv.getValue() < supposeBillNo.length() ? supposeBillNo.substring(kv.getKey() + kv.getValue()) : "");
			}
		}

		billNo = supposeBillNo;
		return billNo;
	}
}
