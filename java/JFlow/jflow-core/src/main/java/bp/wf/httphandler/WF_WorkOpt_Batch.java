package bp.wf.httphandler;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.en.QueryObject;
import bp.sys.*;
import bp.web.*;
import bp.wf.template.*;
import bp.wf.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 页面功能实体
 */
public class WF_WorkOpt_Batch extends bp.difference.handler.DirectoryPageBase {
    /**
     * 构造函数
     */
    public WF_WorkOpt_Batch() {
    }


    ///#region  界面 .
    public final String WorkCheckModel_Init() throws Exception {
        DataSet ds = new DataSet();

        //获取节点信息
        Node nd = new Node(this.getNodeID());
        Flow fl = nd.getHisFlow();
        ds.Tables.add(nd.ToDataTableField("WF_Node"));

        String sql = "";

        if (nd.getItIsSubThread() == true) {
            sql = "SELECT a.*, b.Starter,b.StarterName,b.ADT,b.WorkID FROM " + fl.getPTable() + " a , WF_EmpWorks b WHERE a.OID=B.FID AND b.WFState Not IN (7) AND b.FK_Node=" + nd.getNodeID() + " AND b.FK_Emp='" + WebUser.getNo() + "'";
        } else {
            sql = "SELECT a.*, b.Starter,b.StarterName,b.ADT,b.WorkID FROM " + fl.getPTable() + " a , WF_EmpWorks b WHERE a.OID=B.WorkID AND b.WFState Not IN (7) AND b.FK_Node=" + nd.getNodeID() + " AND b.FK_Emp='" + WebUser.getNo() + "'";
        }
        //按接受日期降序排列
        sql += " ORDER BY ADT DESC";

        //获取待审批的流程信息集合
        DataTable dt = DBAccess.RunSQLReturnTable(sql);
        //判断大小写，并转换
        if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
        {
            dt.Columns.get("PNODEID").ColumnName = "PNodeID";
            dt.Columns.get("PWORKID").ColumnName = "PWorkID";
            dt.Columns.get("FLOWDAYSPAN").ColumnName = "FlowDaySpan";
            dt.Columns.get("FLOWENDNODE").ColumnName = "FlowEndNode";
            dt.Columns.get("FLOWENDERRDT").ColumnName = "FlowEnderRDT";
            dt.Columns.get("FLOWSTARTRDT").ColumnName = "FlowStartRDT";
            dt.Columns.get("GUID").ColumnName = "GUID";
            dt.Columns.get("PEMP").ColumnName = "PEmp";
            dt.Columns.get("BILLNO").ColumnName = "BillNo";
            dt.Columns.get("ATPARA").ColumnName = "AtPara";
            dt.Columns.get("PRJNO").ColumnName = "PrjNo";
            dt.Columns.get("PRJNAME").ColumnName = "PrjName";
            dt.Columns.get("FLOWEMPS").ColumnName = "FlowEmps";
            dt.Columns.get("PFLOWNO").ColumnName = "PFlowNo";
            dt.Columns.get("FLOWENDER").ColumnName = "FlowEnder";
            dt.Columns.get("WFSTATE").ColumnName = "WFState";
            dt.Columns.get("FLOWSTARTER").ColumnName = "FlowStarter";
            dt.Columns.get("WFSTA").ColumnName = "WFSta";
            dt.Columns.get("OID").ColumnName = "OID";
            dt.Columns.get("FID").ColumnName = "FID";
            dt.Columns.get("RDT").ColumnName = "RDT";
            dt.Columns.get("REC").ColumnName = "Rec";
            dt.Columns.get("EMPS").ColumnName = "Emps";
            dt.Columns.get("FK_DEPT").ColumnName = "FK_Dept";
            dt.Columns.get("FK_DEPTNAME").ColumnName = "FK_DeptName";
            dt.Columns.get("TITLE").ColumnName = "Title";
            dt.Columns.get("FK_NY").ColumnName = "FK_NY";
            dt.Columns.get("SQR").ColumnName = "SQR";
            dt.Columns.get("SQRQ").ColumnName = "SQRQ";
            dt.Columns.get("SQDEPT").ColumnName = "SQDept";
            dt.Columns.get("STARTER").ColumnName = "Starter";
            dt.Columns.get("STARTERNAME").ColumnName = "StarterName";
            dt.Columns.get("ADT").ColumnName = "ADT";
            dt.Columns.get("WORKID").ColumnName = "WorkID";
        }
        if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
        {
            dt.Columns.get("pnodeid").ColumnName = "PNodeID";
            dt.Columns.get("pworkid").ColumnName = "PWorkID";
            dt.Columns.get("flowdayspan").ColumnName = "FlowDaySpan";
            dt.Columns.get("flowendnode").ColumnName = "FlowEndNode";
            dt.Columns.get("flowenderrdt").ColumnName = "FlowEnderRDT";
            dt.Columns.get("flowstartrdt").ColumnName = "FlowStartRDT";
            dt.Columns.get("guid").ColumnName = "GUID";
            dt.Columns.get("pemp").ColumnName = "PEmp";
            dt.Columns.get("billno").ColumnName = "BillNo";
            dt.Columns.get("atpara").ColumnName = "AtPara";
            dt.Columns.get("prjno").ColumnName = "PrjNo";
            dt.Columns.get("prjname").ColumnName = "PrjName";
            dt.Columns.get("flowemps").ColumnName = "FlowEmps";
            dt.Columns.get("pflowno").ColumnName = "PFlowNo";
            dt.Columns.get("flowender").ColumnName = "FlowEnder";
            dt.Columns.get("wfstate").ColumnName = "WFState";
            dt.Columns.get("flowstarter").ColumnName = "FlowStarter";
            dt.Columns.get("wfsta").ColumnName = "WFSta";
            dt.Columns.get("oid").ColumnName = "OID";
            dt.Columns.get("fid").ColumnName = "FID";
            dt.Columns.get("rdt").ColumnName = "RDT";
            dt.Columns.get("rec").ColumnName = "Rec";
            dt.Columns.get("emps").ColumnName = "Emps";
            dt.Columns.get("fk_dept").ColumnName = "FK_Dept";
            dt.Columns.get("fk_deptname").ColumnName = "FK_DeptName";
            dt.Columns.get("title").ColumnName = "Title";
            dt.Columns.get("fk_ny").ColumnName = "FK_NY";
            dt.Columns.get("sqr").ColumnName = "SQR";
            dt.Columns.get("sqrq").ColumnName = "SQRQ";
            dt.Columns.get("sqdept").ColumnName = "SQDept";
            dt.Columns.get("starter").ColumnName = "Starter";
            dt.Columns.get("startername").ColumnName = "StarterName";
            dt.Columns.get("adt").ColumnName = "ADT";
            dt.Columns.get("workid").ColumnName = "WorkID";
        }
        dt.TableName = "Works";
        ds.Tables.add(dt);

        //获取按钮权限
        BtnLab btnLab = new BtnLab(this.getNodeID());
        ds.Tables.add(btnLab.ToDataTableField("Sys_BtnLab"));

        int nodeID = nd.getNodeID();

        //获取字段属性
        MapAttrs attrs = new MapAttrs("ND" + nodeID);

        //获取实际中需要展示的列.
        String batchParas = nd.GetParaString("BatchFields");
        MapAttrs realAttr = new MapAttrs();
        if (DataType.IsNullOrEmpty(batchParas) == false) {
            String[] strs = batchParas.split("[,]", -1);
            for (String str : strs) {
                if (DataType.IsNullOrEmpty(str) || str.contains("@PFlowNo") == true) {
                    continue;
                }

                for (MapAttr attr : attrs.ToJavaList()) {
                    if (!Objects.equals(str, attr.getKeyOfEn())) {
                        continue;
                    }

                    realAttr.AddEntity(attr);
                }
            }
        }

        ds.Tables.add(realAttr.ToDataTableField("Sys_MapAttr"));


        return bp.tools.Json.ToJson(ds);
    }

    public final String BatchToolBar_Init() throws Exception {
        DataSet ds = new DataSet();
        Node nd = new Node(this.getNodeID());
        //获取按钮权限
        BtnLab btnLab = new BtnLab(this.getNodeID());
        DataTable dt = new DataTable("ToolBar");
        dt.Columns.Add("No");
        dt.Columns.Add("Name");
        dt.Columns.Add("Oper");
        dt.Columns.Add("Role", Integer.class);
        dt.Columns.Add("Icon");
        DataRow dr = dt.NewRow();
        //发送
        dr.setValue("No", "Send");
        dr.setValue("Name", btnLab.getSendLab());
        dr.setValue("Oper", "");
        dt.Rows.add(dr);
        if (btnLab.getReturnEnable()) {
            /*退回*/
            dr = dt.NewRow();
            dr.setValue("No", "Return");
            dr.setValue("Name", btnLab.getReturnLab());
            dr.setValue("Oper", "");
            dt.Rows.add(dr);
        }
        if (btnLab.getDeleteEnable() != 0) {
            dr = dt.NewRow();
            dr.setValue("No", "DeleteFlow");
            dr.setValue("Name", btnLab.getDeleteLab());
            dr.setValue("Oper", "");
            dt.Rows.add(dr);

        }

        if (btnLab.getEndFlowEnable() && nd.getItIsStartNode() == false) {
            dr = dt.NewRow();
            dr.setValue("No", "EndFlow");
            dr.setValue("Name", btnLab.getEndFlowLab());
            dr.setValue("Oper", "");
            dt.Rows.add(dr);

        }
	   /* int checkModel = nd.GetParaInt("BatchCheckNoteModel");
	    if (nd.FrmWorkCheckSta == FrmWorkCheckSta.Enable && checkModel == 0) {
	        *//*增加审核意见*//*
			dr = dt.NewRow();
			dr.setValue("No", "WorkCheckMsg");
			dr.setValue("Name", "填写审核意见");
			dr.setValue("Oper", "");
			dt.Rows.add(dr);
	}
		*/
        ds.Tables.add(dt);
        GenerWorkFlow gwf = new GenerWorkFlow();
        gwf.setTodoEmps(WebUser.getNo() + ",");
        DataTable dtNodes = Dev2Interface.Node_GenerDTOfToNodes(gwf, nd);
        if (dtNodes != null) {
            ds.Tables.add(dtNodes);
        }

        ds.Tables.add(nd.ToDataTableField("WF_Node"));
        return bp.tools.Json.ToJson(ds);

    }

    /**
     * 审核组件模式：批量发送
     *
     * @return
     */
    public String WorkCheckModel_Send() throws Exception {
        //审核批量发送.
        bp.wf.Node nd = new bp.wf.Node(this.getNodeID());

        //获取要批处理数据
        String sql = String.format("SELECT WorkID, FID,Title FROM WF_EmpWorks WHERE FK_Emp='%1$s' and FK_Node='%2$s'", WebUser.getNo(), this.getFK_Node());
        DataTable dt = DBAccess.RunSQLReturnTable(sql);
        int idx = -1;
        String msg = "";

        //判断是否有传递来的参数.
        int toNode = this.GetRequestValInt("ToNode");
        String toEmps = this.GetRequestVal("ToEmps");

        String editFiles = nd.GetParaString("EditFields");
        //多表单的签批组件的修改
        FrmNode frmNode = null;
        if (nd.getHisFormType() == NodeFormType.SheetTree || nd.getHisFormType() == NodeFormType.RefOneFrmTree) {
            FrmNodes frmNodes = new FrmNodes();
            QueryObject qo = new QueryObject(frmNodes);
            qo.AddWhere(FrmNodeAttr.FK_Node, nd.getNodeID());
            qo.addAnd();
            qo.AddWhere(FrmNodeAttr.IsEnableFWC, 1);
            qo.addAnd();
            qo.AddWhereIsNotNull(NodeWorkCheckAttr.CheckField);
            qo.DoQuery();
            if (frmNodes.size() != 0)
                frmNode = (FrmNode) frmNodes.get(0);
        }
        FrmNode myFrmNode = frmNode;
        int count = 0;
        int successCout = 0;
        int errorCount = 0;
        String successMsg = "";
        String errorMsg = "";
        try {
            //启用线程的时候设置持有上下文的Request容器
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            RequestContextHolder.setRequestAttributes(servletRequestAttributes, true);
            final int POOL_SIZE = dt.Rows.size();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    POOL_SIZE,
                    POOL_SIZE,
                    POOL_SIZE, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(POOL_SIZE),
                    new ThreadPoolExecutor.CallerRunsPolicy());
            List<CompletableFuture<Hashtable>> futures = new ArrayList<CompletableFuture<Hashtable>>();
            for (DataRow dr : dt.Rows) {
                long workid = Long.parseLong(dr.getValue(0).toString());
                String cb = this.GetValFromFrmByKey("CB_" + workid, "0");
                if (cb.equals("on") == false)
                    continue;
                count++;
                SystemConfig.setIsBSsystem(false);
                CompletableFuture<Hashtable> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        //是否启用了审核组件？
                        if (nd.getFrmWorkCheckSta() == FrmWorkCheckSta.Enable) {
                            //绑定多表单，获取启用审核组件的表单
                            if (myFrmNode != null) {
                                GEEntity en = new GEEntity(myFrmNode.getFrmID(), workid);
                                en.SetValByKey(myFrmNode.getCheckField(), en.GetValStrByKey(myFrmNode.getCheckField()) + "," + nd.getNodeID());
                                en.Update();
                            } else {
                                NodeWorkCheck workCheck = new NodeWorkCheck(nd.getNodeID());
                                if (DataType.IsNullOrEmpty(workCheck.getCheckField()) == false) {
                                    GEEntity en = new GEEntity(nd.getNodeFrmID(), workid);
                                    en.SetValByKey(workCheck.getCheckField(), en.GetValStrByKey(workCheck.getCheckField()) + "," + nd.getNodeID());
                                    en.Update();
                                }
                            }

                            //获取审核意见的值
                            String checkNote = "";

                            //选择的多条记录一个意见框.
                            int model = nd.GetParaInt("BatchCheckNoteModel", 0);

                            //多条记录一个意见.
                            if (model == 0)
                                checkNote = this.GetRequestVal("CheckNote");

                            //每条记录都有自己的意见.
                            if (model == 1)
                                checkNote = this.GetValFromFrmByKey("TB_" + workid + "_WorkCheck_Doc", null);

                            if (model == 2)
                                checkNote = " ";

                            //写入审核意见.
                            if (DataType.IsNullOrEmpty(checkNote) == false)
                                bp.wf.Dev2Interface.Node_WriteWorkCheck(workid, checkNote, null, null);
                        }
                        //设置字段的默认值.
                        Work wk = nd.getHisWork();
                        wk.setOID(workid);
                        wk.Retrieve();
                        wk.ResetDefaultVal();
                        if (DataType.IsNullOrEmpty(editFiles) == false) {
                            String[] files = editFiles.split(",");
                            String val = "";
                            for (String key : files) {
                                if (DataType.IsNullOrEmpty(key) == true)
                                    continue;
                                val = this.GetRequestVal("TB_" + workid + "_" + key);
                                wk.SetValByKey(key, val);
                            }
                        }

                        wk.Update();
                        bp.wf.SendReturnObjs objs = bp.wf.Dev2Interface.Node_SendWork(nd.getFlowNo(), workid, toNode, toEmps);
                        Hashtable ht = new Hashtable();
                        ht.put("type", 1);
                        ht.put("msg", "@对工作(" + dr.getValue("Title") + ")处理情况如下" + objs.ToMsgOfHtml() + "<br/>");
                        return ht;
                    } catch (Exception ex) {
                        Hashtable ht = new Hashtable();
                        ht.put("type", 0);
                        ht.put("msg", "@工作(" + dr.getValue("Title") + ")发送出现错误:" + ex.getMessage() + "<br/>");
                        return ht;
                    }
                }, executor);
                Hashtable ht = future.get();
                if (ht.get("type").toString().equals("1")) {
                    successMsg += ht.get("msg");
                    successCout++;
                } else {
                    errorMsg += ht.get("msg");
                    errorCount++;
                }
                futures.add(future);

            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
            executor.shutdown();
            //SystemConfig.setIsBSsystem(true);
        } catch (Exception ex) {
            Log.DebugWriteError("发送子流程部分失败:" + ex.getMessage());
            return "err@发送子流程部分失败";
        }

        if (successCout != 0)
            msg += "@发送成功" + successCout + "条";
        if (errorCount != 0)
            msg += "@发送失败" + errorCount + "条";
        if (successCout != 0)
            msg += "@发送成功信息如下:" + (((successMsg == null ||successMsg.equals("null")))? "":successMsg);
        if (errorCount != 0)
            msg += "@发送失败信息如下:" + errorMsg;

        if (msg == "")
            msg = "没有选择需要处理的工作";

        return msg;
    }

    public String WorkCheckModelVue_Send() throws Exception {
        //审核批量发送.
        Node nd = new Node(this.getNodeID());
        String workIds = this.GetRequestVal("WorkIDs");
        String checkMsg = this.GetRequestVal("CheckMsg");
        String selectItems = this.GetRequestVal("SelectItems");
        JSONArray json = null;
        if (DataType.IsNullOrEmpty(selectItems) == false) {
            json = JSONArray.fromObject(selectItems);
        }
        //获取要批处理数据
        String sql = String.format("SELECT WorkID, FID,Title FROM WF_EmpWorks WHERE FK_Emp='%1$s' and FK_Node='%2$s' and WorkID IN('" + workIds.replace(",", "','") + "')", WebUser.getNo(), this.getNodeID());
        DataTable dt = DBAccess.RunSQLReturnTable(sql);
        int idx = -1;
        String msg = "";

        //判断是否有传递来的参数.
        int toNode = this.GetRequestValInt("ToNode");
        String toEmps = this.GetRequestVal("ToEmps");

        String editFiles = nd.GetParaString("EditFields");
        //多表单的签批组件的修改
        FrmNode frmNode = null;
        if (nd.getHisFormType() == NodeFormType.SheetTree || nd.getHisFormType() == NodeFormType.RefOneFrmTree) {
            FrmNodes frmNodes = new FrmNodes();
            QueryObject qo = new QueryObject(frmNodes);
            qo.AddWhere(FrmNodeAttr.FK_Node, nd.getNodeID());
            qo.addAnd();
            qo.AddWhere(FrmNodeAttr.IsEnableFWC, 1);
            qo.addAnd();
            qo.AddWhereIsNotNull(NodeWorkCheckAttr.CheckField);
            qo.DoQuery();
            if (frmNodes.size() != 0)
                frmNode = (FrmNode) frmNodes.get(0);
        }
        FrmNode myFrmNode = frmNode;
        int count = 0;
        int successCout = 0;
        int errorCount = 0;
        String successMsg = "";
        String errorMsg = "";
        try {
            //启用线程的时候设置持有上下文的Request容器
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            RequestContextHolder.setRequestAttributes(servletRequestAttributes, true);
            final int POOL_SIZE = dt.Rows.size();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    POOL_SIZE,
                    POOL_SIZE,
                    POOL_SIZE, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(POOL_SIZE),
                    new ThreadPoolExecutor.CallerRunsPolicy());
            List<CompletableFuture<Hashtable>> futures = new ArrayList<CompletableFuture<Hashtable>>();
            for (DataRow dr : dt.Rows) {
                long workid = Long.parseLong(dr.getValue(0).toString());
                JSONObject obj = GetObject(workid, json);
                count++;
                SystemConfig.setIsBSsystem(false);
                CompletableFuture<Hashtable> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        //是否启用了审核组件？
                        if (nd.getFrmWorkCheckSta() == FrmWorkCheckSta.Enable) {
                            //绑定多表单，获取启用审核组件的表单
                            if (myFrmNode != null) {
                                GEEntity en = new GEEntity(myFrmNode.getFrmID(), workid);
                                en.SetValByKey(myFrmNode.getCheckField(), en.GetValStrByKey(myFrmNode.getCheckField()) + "," + nd.getNodeID());
                                en.Update();
                            } else {
                                NodeWorkCheck workCheck = new NodeWorkCheck(nd.getNodeID());
                                if (DataType.IsNullOrEmpty(workCheck.getCheckField()) == false) {
                                    GEEntity en = new GEEntity(nd.getNodeFrmID(), workid);
                                    en.SetValByKey(workCheck.getCheckField(), en.GetValStrByKey(workCheck.getCheckField()) + "," + nd.getNodeID());
                                    en.Update();
                                }
                            }

                            //获取审核意见的值
                            String checkNote = "";

                            //选择的多条记录一个意见框.
                            int model = nd.GetParaInt("BatchCheckNoteModel", 0);

                            //多条记录一个意见.
                            if (model == 0)
                                checkNote = checkMsg;

                            //每条记录都有自己的意见.
                            if (model == 1 && obj != null) {
                                Object result = obj.get("CheckMsg");
                                checkNote = result == null ? "" : result.toString();

                            }
                            if (model == 2)
                                checkNote = " ";
                            //写入审核意见.
                            if (DataType.IsNullOrEmpty(checkNote) == false)
                                bp.wf.Dev2Interface.Node_WriteWorkCheck(workid, checkNote, null, null);
                        }
                        //设置字段的默认值.
                        Work wk = nd.getHisWork();
                        wk.setOID(workid);
                        wk.Retrieve();
                        wk.ResetDefaultVal();
                        if (DataType.IsNullOrEmpty(editFiles) == false) {
                            String[] files = editFiles.split(",");
                            String val = "";
                            for (String key : files) {
                                if (DataType.IsNullOrEmpty(key) == true)
                                    continue;
                                if (obj != null) {
                                    Object result = obj.get(key);
                                    wk.SetValByKey(key, result == null ? "" : result.toString());
                                }
                            }
                        }

                        wk.Update();

                        //执行工作发送.
                        bp.wf.SendReturnObjs objs = bp.wf.Dev2Interface.Node_SendWork(nd.getFlowNo(), workid, toNode, toEmps);
                        Hashtable ht = new Hashtable();
                        ht.put("type", 1);
                        ht.put("msg", "@对工作(" + dr.getValue("Title") + ")处理情况如下" + objs.ToMsgOfHtml() + "<br/>");
                        return ht;
                    } catch (Exception ex) {
                        Hashtable ht = new Hashtable();
                        ht.put("type", 0);
                        ht.put("msg", "@工作(" + dr.getValue("Title") + ")发送出现错误:" + ex.getMessage() + "<br/>");
                        return ht;
                    }

                }, executor);
                Hashtable ht = future.get();
                if (ht.get("type").toString().equals("1")) {
                    successMsg += ht.get(msg);
                    successCout++;
                } else {
                    errorMsg += ht.get(msg);
                    errorCount++;
                }
                futures.add(future);

            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
            executor.shutdown();
            //SystemConfig.setIsBSsystem(true);
        } catch (Exception ex) {
            Log.DebugWriteError("发送子流程部分失败:" + ex.getMessage());
            return "err@发送子流程部分失败";
        }
        if (successCout != 0)
            msg += "@发送成功信息如下:<br/>" + successMsg;
        if (errorCount != 0)
            msg += "@发送失败信息如下:<br/>" + errorMsg;

        if (msg == "")
            return "没有选择需要处理的工作";

        return "本次批量发送成功" + successCout + "件，失败" + errorCount + "件。<br>" + msg;
    }

    private JSONObject GetObject(long compareWorkid, JSONArray json) {
        if (json == null)
            return null;
        for (int i = 0; i < json.size(); i++) {
            JSONObject obj = json.getJSONObject(i);
            long workid = obj.getLong("WorkID");
            if (workid == compareWorkid)
                return obj;
        }
        return null;
    }

    public String BatchList_Init() throws Exception {
        DataSet ds = new DataSet();

        String FK_Node = GetRequestVal("FK_Node");

        //获取节点信息
        Node nd = new Node(this.getNodeID());
        Flow fl = nd.getHisFlow();
        ds.Tables.add(nd.ToDataTableField("WF_Node"));

        String sql = "";

        if (nd.getItIsSubThread() == true) {
            sql = "SELECT a.*, b.Starter,b.ADT,b.WorkID FROM " + fl.getPTable() + " a , WF_EmpWorks b WHERE a.OID=B.FID AND b.WFState Not IN (7) AND b.FK_Node=" + nd.getNodeID() + " AND b.FK_Emp='" + WebUser.getNo() + "'";
        } else {
            sql = "SELECT a.*, b.Starter,b.ADT,b.WorkID FROM " + fl.getPTable() + " a , WF_EmpWorks b WHERE a.OID=B.WorkID AND b.WFState Not IN (7) AND b.FK_Node=" + nd.getNodeID() + " AND b.FK_Emp='" + WebUser.getNo() + "'";
        }

        //获取待审批的流程信息集合
        DataTable dt = DBAccess.RunSQLReturnTable(sql);
        dt.TableName = "Batch_List";
        ds.Tables.add(dt);

        //获取按钮权限
        BtnLab btnLab = new BtnLab(this.getNodeID());

        ds.Tables.add(btnLab.ToDataTableField("Sys_BtnLab"));

        //获取报表数据
        String inSQL = "SELECT WorkID FROM WF_EmpWorks WHERE FK_Emp='" + WebUser.getNo() + "' AND WFState!=7 AND FK_Node=" + this.getNodeID();
        Works wks = nd.getHisWorks();
        wks.RetrieveInSQL(inSQL);

        ds.Tables.add(wks.ToDataTableField("WF_Work"));

        //获取字段属性
        MapAttrs attrs = new MapAttrs("ND" + this.getNodeID());

        //获取实际中需要展示的列
        String batchParas = nd.getBatchParas();
        MapAttrs realAttr = new MapAttrs();
        if (DataType.IsNullOrEmpty(batchParas) == false) {
            String[] strs = batchParas.split(",");
            for (String str : strs) {
                if (DataType.IsNullOrEmpty(str) || str.contains("@PFlowNo") == true)
                    continue;

                for (MapAttr attr : attrs.ToJavaList()) {
                    if (str.equals(attr.getKeyOfEn()) == false)
                        continue;

                    realAttr.AddEntity(attr);
                }
            }
        }

        ds.Tables.add(realAttr.ToDataTableField("Sys_MapAttr"));

        return bp.tools.Json.ToJson(ds);
    }


    ///#endregion 界面方法.


    ///#region 通用方法.

    /**
     * 批量删除
     *
     * @return
     */
    public String Batch_Delete() throws Exception {
        //BP.WF.Node nd = new BP.WF.Node(this.getNodeID());
        String workIDs = this.GetRequestVal("WorkIDs");
        if (DataType.IsNullOrEmpty(workIDs) == true)
            return "err@没有选择需要处理的工作";
        String msg = "";
        GenerWorkFlows gwfs = new GenerWorkFlows();
        gwfs.RetrieveIn("WorkID", workIDs);
        for (GenerWorkFlow gwf : gwfs.ToJavaList()) {
            msg += "@对工作(" + gwf.getTitle() + ")处理情况如下。<br>";
            String mes = bp.wf.Dev2Interface.Flow_DoDeleteFlowByFlag(gwf.getWorkID(), "批量删除", true);
            msg += mes;
            msg += "<hr>";
        }
        return "批量删除成功" + msg;

    }

    public String Batch_StopFlow() throws Exception {
        String workIDs = this.GetRequestVal("WorkIDs");
        if (DataType.IsNullOrEmpty(workIDs) == true)
            return "err@没有选择需要处理的工作";
        String msg = "";
        GenerWorkFlows gwfs = new GenerWorkFlows();
        gwfs.RetrieveIn("WorkID", workIDs);
        for (GenerWorkFlow gwf : gwfs.ToJavaList()) {
            msg += "@对工作(" + gwf.getTitle() + ")处理情况如下。<br>";
            String mes = bp.wf.Dev2Interface.Flow_DoFlowOver(gwf.getWorkID(), "批量结束流程");
            msg += mes;
            msg += "<hr>";
        }
        return "批量结束成功" + msg;

    }

    /**
     * 批量退回 待定
     *
     * @return
     */
    public String Batch_Return() throws Exception {
        Node nd = new Node(this.getNodeID());
        String workIDs = this.GetRequestVal("WorkIDs");
        if (DataType.IsNullOrEmpty(workIDs) == true)
            workIDs = String.valueOf(this.getWorkID());

        //return "err@没有选择需要处理的工作";
        String msg = "";

        String returnToNode = this.GetRequestVal("ReturnToNode");
        String[] vals = returnToNode.split("=>");
        if(vals.length < 2)
            vals = this.GetRequestVal("ReturnToNode").split("[@]", -1);

        int toNodeID = Integer.parseInt(vals[0]);

        String toEmp = vals[1];
        String reMesage = this.GetRequestVal("ReturnInfo");

        boolean isBackBoolen = false;
        if (this.GetRequestVal("IsBack").equals("1") == true)
            isBackBoolen = true;

        boolean isKill = false; //是否全部退回.
        String isKillEtcThread = this.GetRequestVal("IsKillEtcThread");
        if (DataType.IsNullOrEmpty(isKillEtcThread) == false && isKillEtcThread.equals("1") == true)
            isKill = true;

        String pageData = this.GetRequestVal("PageData");
        GenerWorkFlows gwfs = new GenerWorkFlows();
        gwfs.RetrieveIn("WorkID", workIDs);
        for (GenerWorkFlow gwf : gwfs.ToJavaList()) {
            msg += "@对工作(" + gwf.getTitle() + ")处理情况如下。<br>";
            msg += bp.wf.Dev2Interface.Node_ReturnWork(gwf.getWorkID(), toNodeID, toEmp, reMesage, isBackBoolen, pageData, isKill);
            msg += "<hr>";

        }
        return msg;
    }


    ///#endregion

}
