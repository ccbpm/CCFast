package bp.wf.httphandler;

import bp.difference.SystemConfig;
import bp.en.*;
import bp.da.*;
import bp.port.WebUserCopy;
import bp.sys.*;
import bp.wf.template.*;
import bp.web.*;
import bp.wf.template.sflow.*;
import bp.wf.*;

import java.text.MessageFormat;
import java.util.*;

/**
 * 页面功能实体
 */
public class WF_WorkOpt_OneWork extends bp.difference.handler.DirectoryPageBase {
    /**
     * 进度图.
     *
     * @return
     */
    public final String JobSchedule_Init() throws Exception {
        DataSet ds = Dev2Interface.DB_JobSchedule(this.getWorkID());
        return bp.tools.Json.ToJson(ds);
    }

    /**
     * 构造函数
     */
    public WF_WorkOpt_OneWork() {
    }

    /**
     * 时间轴
     *
     * @return
     */
    public final String TimeSubThread_Init() throws Exception {
        DataSet ds = new DataSet();
        String mypks = GetRequestVal("MyPKs");
        mypks = "('" + mypks.replace(",", "','") + "')";
        String sql = "SELECT MyPK,ActionType,ActionTypeText,FID,WorkID,NDFrom,NDFromT,NDTo,NDToT,EmpFrom,EmpFromT,EmpTo,EmpToT,RDT,WorkTimeSpan,Msg,NodeData,Exer,Tag FROM ND" + Integer.parseInt(this.getFlowNo()) + "Track Where MyPK IN" + mypks + " ORDER BY RDT ASC ";
        DataTable dt = DBAccess.RunSQLReturnTable(sql);
        dt.TableName = "Track";
        //把列名转化成区分大小写.
        if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase) {
            dt.Columns.get("MYPK").ColumnName = "MyPK";
            dt.Columns.get("ACTIONTYPE").ColumnName = "ActionType";
            dt.Columns.get("ACTIONTYPETEXT").ColumnName = "ActionTypeText";
            dt.Columns.get("FID").ColumnName = "FID";
            dt.Columns.get("WORKID").ColumnName = "WorkID";
            dt.Columns.get("NDFROM").ColumnName = "NDFrom";
            dt.Columns.get("NDFROMT").ColumnName = "NDFromT";
            dt.Columns.get("NDTO").ColumnName = "NDTo";
            dt.Columns.get("NDTOT").ColumnName = "NDToT";
            dt.Columns.get("EMPFROM").ColumnName = "EmpFrom";
            dt.Columns.get("EMPFROMT").ColumnName = "EmpFromT";
            dt.Columns.get("EMPTO").ColumnName = "EmpTo";
            dt.Columns.get("EMPTOT").ColumnName = "EmpToT";
            dt.Columns.get("RDT").ColumnName = "RDT";
            dt.Columns.get("WORKTIMESPAN").ColumnName = "WorkTimeSpan";
            dt.Columns.get("MSG").ColumnName = "Msg";
            dt.Columns.get("NODEDATA").ColumnName = "NodeData";
            dt.Columns.get("EXER").ColumnName = "Exer";
            dt.Columns.get("TAG").ColumnName = "Tag";
        }

        if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase) {
            dt.Columns.get("mypk").ColumnName = "MyPK";
            dt.Columns.get("actiontype").ColumnName = "ActionType";
            dt.Columns.get("actiontypetext").ColumnName = "ActionTypeText";
            dt.Columns.get("fid").ColumnName = "FID";
            dt.Columns.get("workid").ColumnName = "WorkID";
            dt.Columns.get("ndfrom").ColumnName = "NDFrom";
            dt.Columns.get("ndfromt").ColumnName = "NDFromT";
            dt.Columns.get("ndto").ColumnName = "NDTo";
            dt.Columns.get("ndtot").ColumnName = "NDToT";
            dt.Columns.get("empfrom").ColumnName = "EmpFrom";
            dt.Columns.get("empfromt").ColumnName = "EmpFromT";
            dt.Columns.get("empto").ColumnName = "EmpTo";
            dt.Columns.get("emptot").ColumnName = "EmpToT";
            dt.Columns.get("rdt").ColumnName = "RDT";
            dt.Columns.get("worktimespan").ColumnName = "WorkTimeSpan";
            dt.Columns.get("msg").ColumnName = "Msg";
            dt.Columns.get("nodedata").ColumnName = "NodeData";
            dt.Columns.get("exer").ColumnName = "Exer";
            dt.Columns.get("tag").ColumnName = "Tag";
        }
        //获取track.
        ds.Tables.add(dt);


        ///#region  父子流程数据存储到这里.
        Hashtable ht = new Hashtable();
        for (DataRow dr : dt.Rows) {
            ActionType at = ActionType.forValue(Integer.parseInt(dr.getValue(TrackAttr.ActionType).toString()));

            String tag = dr.getValue(TrackAttr.Tag).toString(); //标识.
            String mypk = dr.getValue(TrackAttr.MyPK).toString(); //主键.

            String msg = "";
            if (at == ActionType.CallChildenFlow) {
                //被调用父流程吊起。
                if (DataType.IsNullOrEmpty(tag) == false) {
                    AtPara ap = new AtPara(tag);
                    GenerWorkFlow mygwf = new GenerWorkFlow();
                    mygwf.setWorkID(ap.GetValInt64ByKey("PWorkID"));
                    if (mygwf.RetrieveFromDBSources() == 1) {
                        msg = "<p>操作员:{" + dr.getValue(TrackAttr.EmpFromT).toString() + "}在当前节点上，被父流程{" + mygwf.getFlowName() + "},<a target=b" + ap.GetValStrByKey("PWorkID") + " href='Track.htm?WorkID=" + ap.GetValStrByKey("PWorkID") + "&FK_Flow=" + ap.GetValStrByKey("PFlowNo") + "' >" + msg + "</a></p>";
                    } else {
                        msg = "<p>操作员:{" + dr.getValue(TrackAttr.EmpFromT).toString() + "}在当前节点上，被父流程调用{" + mygwf.getFlowName() + "}，但是该流程被删除了.</p>" + tag;
                    }

                    msg = "<a target=b" + ap.GetValStrByKey("PWorkID") + " href='Track.htm?WorkID=" + ap.GetValStrByKey("PWorkID") + "&FK_Flow=" + ap.GetValStrByKey("PFlowNo") + "' >" + msg + "</a>";
                }

                //放入到ht里面.
                ht.put(mypk, msg);
            }

            if (at == ActionType.StartChildenFlow) {
                if (DataType.IsNullOrEmpty(tag) == false) {
                    if (tag.contains("Sub")) {
                        tag = tag.replace("Sub", "C");
                    }

                    AtPara ap = new AtPara(tag);
                    GenerWorkFlow mygwf = new GenerWorkFlow();
                    mygwf.setWorkID(ap.GetValInt64ByKey("CWorkID"));
                    if (mygwf.RetrieveFromDBSources() == 1) {
                        msg = "<p>操作员:{" + dr.getValue(TrackAttr.EmpFromT).toString() + "}在当前节点上调用了子流程{" + mygwf.getFlowName() + "}, <a target=b" + ap.GetValStrByKey("CWorkID") + " href='Track.htm?WorkID=" + ap.GetValStrByKey("CWorkID") + "&FK_Flow=" + ap.GetValStrByKey("CFlowNo") + "' >" + msg + "</a></p>";
                        msg += "<p>当前子流程状态：{" + mygwf.getWFStateText() + "}，运转到:{" + mygwf.getNodeName() + "}，最后处理人{" + mygwf.getTodoEmps() + "}，最后处理时间{" + mygwf.getRDT() + "}。</p>";
                    } else {
                        msg = "<p>操作员:{" + dr.getValue(TrackAttr.EmpFromT).toString() + "}在当前节点上调用了子流程{" + mygwf.getFlowName() + "}，但是该流程被删除了.</p>" + tag;
                    }

                }

                //放入到ht里面.
                ht.put(mypk, msg);
            }
        }

        ///#endregion


        //把节点审核配置信息.
        NodeWorkCheck fwc = new NodeWorkCheck(this.getNodeID());
        ds.Tables.add(fwc.ToDataTableField("FrmWorkCheck"));

        //返回结果.
        return bp.tools.Json.ToJson(ds);
    }

    /**
     * 时间轴
     *
     * @return
     */
    public final String TimeBase_Init() throws Exception {
        DataSet ds = new DataSet();

        //获取干流程和子线程中的Track信息
        DataTable dt = Dev2Interface.DB_GenerTrackTable(this.getFlowNo(), this.getWorkID(), this.getFID(), false);
        ds.Tables.add(dt);


		/*#region  父子流程数据存储到这里.
		Hashtable ht = new Hashtable();
		foreach (DataRow dr in dt.Rows)
		{
		    ActionType at = (ActionType)int.Parse(dr[TrackAttr.ActionType).toString());

		    String tag = dr[TrackAttr.Tag).toString(); //标识.
		    String mypk = dr[TrackAttr.MyPK).toString(); //主键.

		    String msg = "";
		    if (at == ActionType.CallChildenFlow)
		    {
		        //被调用父流程吊起。
		        if (DataType.IsNullOrEmpty(tag) == false)
		        {
		            AtPara ap = new AtPara(tag);
		            GenerWorkFlow mygwf = new GenerWorkFlow();
		            mygwf.WorkID = ap.GetValInt64ByKey("PWorkID");
		            if (mygwf.RetrieveFromDBSources() == 1)
		                msg = "<p>操作员:{" + dr[TrackAttr.EmpFromT).toString() + "}在当前节点上，被父流程{" + mygwf.getFlowName() + "},<a target=b" + ap.GetValStrByKey("PWorkID") + " href='Track.htm?WorkID=" + ap.GetValStrByKey("PWorkID") + "&FK_Flow=" + ap.GetValStrByKey("PFlowNo") + "' >" + msg + "</a></p>";
		            else
		                msg = "<p>操作员:{" + dr[TrackAttr.EmpFromT).toString() + "}在当前节点上，被父流程调用{" + mygwf.getFlowName() + "}，但是该流程被删除了.</p>" + tag;

		            msg = "<a target=b" + ap.GetValStrByKey("PWorkID") + " href='Track.htm?WorkID=" + ap.GetValStrByKey("PWorkID") + "&FK_Flow=" + ap.GetValStrByKey("PFlowNo") + "' >" + msg + "</a>";
		        }

		        //放入到ht里面.
		        ht.Add(mypk, msg);
		    }

		    if (at == ActionType.StartChildenFlow)
		    {
		        if (DataType.IsNullOrEmpty(tag) == false)
		        {
		            if (tag.contains("Sub"))
		                tag = tag.replace("Sub", "C");

		            AtPara ap = new AtPara(tag);
		            GenerWorkFlow mygwf = new GenerWorkFlow();
		            mygwf.WorkID = ap.GetValInt64ByKey("CWorkID");
		            if (mygwf.RetrieveFromDBSources() == 1)
		            {
		                msg = "<p>操作员:{" + dr[TrackAttr.EmpFromT).toString() + "}在当前节点上调用了子流程{" + mygwf.getFlowName() + "}, <a target=b" + ap.GetValStrByKey("CWorkID") + " href='Track.htm?WorkID=" + ap.GetValStrByKey("CWorkID") + "&FK_Flow=" + ap.GetValStrByKey("CFlowNo") + "' >" + msg + "</a></p>";
		                msg += "<p>当前子流程状态：{" + mygwf.WFStateText + "}，运转到:{" + mygwf.NodeName + "}，最后处理人{" + mygwf.TodoEmps + "}，最后处理时间{" + mygwf.RDT + "}。</p>";
		            }
		            else
		                msg = "<p>操作员:{" + dr[TrackAttr.EmpFromT).toString() + "}在当前节点上调用了子流程{" + mygwf.getFlowName() + "}，但是该流程被删除了.</p>" + tag;

		        }

		        //放入到ht里面.
		        ht.Add(mypk, msg);
		    }
		}
		#endregion*/

        //获取当前流程的待办信息 WF_GenerWorkFlow
        GenerWorkFlow gwf = new GenerWorkFlow();
        gwf.setWorkID(this.getWorkID());
        gwf.RetrieveFromDBSources();
        ds.Tables.add(gwf.ToDataTableField("WF_GenerWorkFlow"));

        if (gwf.getIsOver()==false) {
            GenerWorkerLists gwls = new GenerWorkerLists();
            gwls.Retrieve(GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.Idx);

            //warning 补偿式的更新.  做特殊的判断，当会签过了以后仍然能够看isPass=90的错误数据.
            for (GenerWorkerList item : gwls.ToJavaList()) {
                if (item.getPassInt() == 90 && gwf.getNodeID() != item.getNodeID()) {
                    item.setPassInt(0);
                    item.Update();
                }
            }
            Node nd = new Node(gwf.getNodeID());
            if (nd.getHisRunModel() == RunModel.FL || nd.getHisRunModel() == RunModel.FHL) {
                //获取是否存在退回的分合流点
                GenerWorkerLists tgwls = new GenerWorkerLists();
                tgwls.Retrieve(GenerWorkerListAttr.FID, this.getWorkID(), GenerWorkerListAttr.FK_Node, gwf.getNodeID(), GenerWorkerListAttr.IsPass, 0, GenerWorkerListAttr.Idx);
                for (GenerWorkerList gwl : tgwls.ToJavaList()) {
                    gwls.AddEntity(gwl);
                }
            }
            ds.Tables.add(gwls.ToDataTableField("WF_GenerWorkerlist"));
        }

        //把节点审核配置信息.
        NodeWorkCheck fwc = new NodeWorkCheck(gwf.getNodeID());
        ds.Tables.add(fwc.ToDataTableField("FrmWorkCheck"));

        //获取启动的子流程信息
        SubFlows subFlows = new SubFlows(this.getFlowNo());
        ds.Tables.add(subFlows.ToDataTableField("WF_SubFlow"));

        //返回结果.
        return bp.tools.Json.ToJson(ds);
    }


    ///#region 执行父类的重写方法.


    ///#endregion 执行父类的重写方法.


    ///#region 属性.
    public final String getMsg() {
        String str = this.GetRequestVal("TB_Msg");
        if (str == null || Objects.equals(str, "") || Objects.equals(str, "null")) {
            return null;
        }
        return str;
    }

    public final String getUserName() {
        String str = this.GetRequestVal("UserName");
        if (str == null || Objects.equals(str, "") || Objects.equals(str, "null")) {
            return null;
        }
        return str;
    }

    public final String getTitle() {
        String str = this.GetRequestVal("Title");
        if (str == null || Objects.equals(str, "") || Objects.equals(str, "null")) {
            return null;
        }
        return str;
    }

    /**
     * 字典表
     */
    public final String getFKSFTable() {
        String str = this.GetRequestVal("FK_SFTable");
        if (str == null || Objects.equals(str, "") || Objects.equals(str, "null")) {
            return null;
        }
        return str;

    }

    public final String getEnumKey() {
        String str = this.GetRequestVal("EnumKey");
        if (str == null || Objects.equals(str, "") || Objects.equals(str, "null")) {
            return null;
        }
        return str;

    }


    public final String getName() {
        String str = WebUser.getName();
        if (str == null || Objects.equals(str, "") || Objects.equals(str, "null")) {
            return null;
        }
        return str;
    }

    ///#endregion 属性.

    public final String FlowBBS_Delete() {
        return Dev2Interface.Flow_BBSDelete(this.getFlowNo(), this.getMyPK(), WebUser.getNo());
    }

    /**
     * 执行撤销
     *
     * @return
     */
    public final String OP_UnSend() {
        //获取用户当前所在的节点
        String currNode = "";
        switch (DBAccess.getAppCenterDBType()) {
            case Oracle:
            case DM:
            case KingBaseR3:
            case KingBaseR6:
            case GBASE8CByOracle:
                currNode = "SELECT FK_Node FROM (SELECT  FK_Node FROM WF_GenerWorkerlist WHERE FK_Emp='" + WebUser.getNo() + "' AND WorkID=" + this.getWorkID() + "  Order by RDT DESC ) WHERE rownum=1";
                break;
            case MySQL:
            case PostgreSQL:
            case UX:
            case HGDB:
            case GBASE8CByMySQL:
            case GBASE8A:
                currNode = "SELECT  FK_Node FROM WF_GenerWorkerlist WHERE FK_Emp='" + WebUser.getNo() + "' AND WorkID=" + this.getWorkID() + "  Order by RDT DESC LIMIT 1";
                break;
            case MSSQL:
                currNode = "SELECT TOP 1 FK_Node FROM WF_GenerWorkerlist WHERE FK_Emp='" + WebUser.getNo() + "' AND WorkID=" + this.getWorkID() + " Order by RDT DESC";
                break;
            default:
                break;
        }
        String unSendToNode = DBAccess.RunSQLReturnString(currNode);
        try {
            return Dev2Interface.Flow_DoUnSend(this.getFlowNo(), this.getWorkID(), Integer.parseInt(unSendToNode), this.getFID());
        } catch (Exception ex) {
            return "err@" + ex.getMessage();
        }
    }

    @Override
    protected String DoDefaultMethod() {
        return "err@没有判断的执行类型：" + this.getDoType() + " @类 " + this.toString();
    }

    public final String OP_ComeBack() throws Exception {
        WorkFlow wf3 = new WorkFlow(getWorkID());
        wf3.DoComeBackWorkFlow("无");
        return "流程已经被重新启用.";
    }

    public final String OP_UnHungup() throws Exception {
        WorkFlow wf2 = new WorkFlow(getWorkID());
        //  wf2.DoUnHungup();
        return "流程已经被解除挂起.";
    }

    public final String OP_Hungup() throws Exception {
        WorkFlow wf1 = new WorkFlow(getWorkID());
        //wf1.DoHungup()
        return "流程已经被挂起.";
    }

    public final String OP_DelFlow() throws Exception {
        WorkFlow wf = new WorkFlow(getWorkID());
        wf.DoDeleteWorkFlowByReal(true);
        return "流程已经被删除！";
    }

    /**
     * 获取可操作状态信息
     *
     * @return
     */
    public final String OP_GetStatus() throws Exception {
        GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
        Hashtable ht = new Hashtable();

        boolean CanPackUp = true; //是否可以打包下载.


        ///#region  PowerModel权限的解析
        String psql = "SELECT A.PowerFlag,A.EmpNo,A.EmpName FROM WF_PowerModel A WHERE PowerCtrlType =1" + " UNION " + "SELECT A.PowerFlag,B.No,B.Name FROM WF_PowerModel A, Port_Emp B, Port_DeptEmpStation C WHERE A.PowerCtrlType = 0 AND B.No=C.FK_Emp AND A.StaNo = C.FK_Station";
        psql = "SELECT PowerFlag From(" + psql + ")D WHERE  D.EmpNo='" + WebUser.getNo() + "'";

        String powers = DBAccess.RunSQLReturnStringIsNull(psql, "");


        ///#endregion PowerModel权限的解析


        ///#region 文件打印的权限判断，这里为天业集团做的特殊判断，现实的应用中，都可以打印.
        if (Objects.equals(bp.difference.SystemConfig.getCustomerNo(), "TianYe") && !Objects.equals(WebUser.getNo(), "admin")) {
            CanPackUp = IsCanPrintSpecForTianYe(gwf);
        }

        ///#endregion 文件打印的权限判断，这里为天业集团做的特殊判断，现实的应用中，都可以打印.
        if (CanPackUp == true) {
            ht.put("CanPackUp", 1);
        } else {
            ht.put("CanPackUp", 0);
        }

        //获取打印的方式PDF/RDF,节点打印方式
        Node nd = new Node(this.getNodeID());
        if (nd.getHisPrintDocEnable() == true) {
            ht.put("PrintType", 1);
        } else {
            ht.put("PrintType", 0);
        }


        //是否可以打印.
        switch (gwf.getWFState()) {
            case Runing: // 运行时
                /*删除流程.*/
                if (Dev2Interface.Flow_IsCanDeleteFlowInstance(this.getFlowNo(), this.getWorkID(), WebUser.getNo()) == true) {
                    ht.put("IsCanDelete", 1);
                } else {
                    ht.put("IsCanDelete", 0);
                }

                if (powers.contains("FlowDataDelete") == true) {
                    //存在移除这个键值
                    if (ht.containsKey("IsCanDelete") == true) {
                        ht.remove("IsCanDelete");
                    }
                    ht.put("IsCanDelete", 1);
                }


                /*取回审批*/
                String para = "";
                String sql = "SELECT NodeID FROM WF_Node WHERE CheckNodes LIKE '%" + gwf.getNodeID() + "%'";
                int myNode = DBAccess.RunSQLReturnValInt(sql, 0);
                if (myNode != 0) {
                    //GetTask gt = new GetTask(myNode);
                    //if (gt.Can_I_Do_It())
                    //{
                    //    ht.Add("TackBackFromNode", gwf.getNodeID());
                    //    ht.Add("TackBackToNode", myNode);
                    //    ht.Add("CanTackBack", 1);
                    //}
                }

                if (Objects.equals(bp.difference.SystemConfig.getCustomerNo(), "TianYe")) {
                    ht.put("CanUnSend", 1);

                } else {
                    /*撤销发送*/
                    GenerWorkerLists workerlists = new GenerWorkerLists();
                    QueryObject info = new QueryObject(workerlists);
                    info.AddWhere(GenerWorkerListAttr.FK_Emp, WebUser.getNo());
                    info.addAnd();
                    info.AddWhere(GenerWorkerListAttr.IsPass, "1");
                    info.addAnd();
                    info.AddWhere(GenerWorkerListAttr.IsEnable, "1");
                    info.addAnd();
                    info.AddWhere(GenerWorkerListAttr.WorkID, this.getWorkID());

                    if (info.DoQuery() > 0) {
                        ht.put("CanUnSend", 1);
                    } else {
                        ht.put("CanUnSend", 0);
                    }

                    if (powers.contains("FlowDataUnSend") == true) {
                        //存在移除这个键值
                        if (ht.containsKey("CanUnSend") == true) {
                            ht.remove("CanUnSend");
                        }
                        ht.put("CanUnSend", 1);
                    }

                }


                //流程结束
                if (powers.contains("FlowDataOver") == true) {
                    ht.put("CanFlowOver", 1);
                }

                //催办
                if (powers.contains("FlowDataPress") == true) {
                    ht.put("CanFlowPress", 1);
                }


                //是否可以调整工时
                sql = "SELECT CHRole \"CHRole\" From WF_GenerWorkerlist G,WF_Node N Where G.FK_Node=N.NodeID AND N.CHRole!=0 AND WorkID=" + this.getWorkID() + " AND FK_Emp='" + WebUser.getNo() + "'";
                DataTable dt = DBAccess.RunSQLReturnTable(sql);
                if (dt.Rows.size() > 0) {
                    for (DataRow dr : dt.Rows) {
                        if (Integer.parseInt(dr.getValue("CHRole").toString()) == 1 || Integer.parseInt(dr.getValue("CHRole").toString()) == 3) {
                            ht.put("CanChangCHRole", 1);
                            break;
                        } else {
                            ht.put("CanChangCHRole", 2);
                        }

                    }


                }


                break;
            case CompleteEnd: // 完成.
            case CompleteByNode: // 完成.
            case Delete: // 逻辑删除..
                /*恢复使用流程*/
                if (WebUser.getNo().equals("admin") == true) {
                    ht.put("CanRollBack", 1);
                } else {
                    ht.put("CanRollBack", 0);
                }

                if (powers.contains("FlowDataRollback") == true) {
                    //存在移除这个键值
                    if (ht.containsKey("CanRollBack") == true) {
                        ht.remove("CanRollBack");
                    }
                    ht.put("CanRollBack", 1);
                }

                if (nd.getCHRole() != 0) //0禁用 1 启用 2 只读 3 启用并可以调整流程应完成时间
                {
                    ht.put("CanChangCHRole", 2);

                }


                //判断是否可以打印.
                break;
            case Hungup: // 挂起.
                /*撤销挂起*/
                if (Dev2Interface.Flow_IsCanDoCurrentWork(getWorkID(), WebUser.getNo()) == false) {
                    ht.put("CanUnHungup", 0);
                } else {
                    ht.put("CanUnHungup", 1);
                }
                break;
            default:
                break;
        }

        return bp.tools.Json.ToJson(ht);

        //return json + "}";
    }

    /**
     * 是否可以打印.
     *
     * @param gwf
     * @return
     */
    private boolean IsCanPrintSpecForTianYe(GenerWorkFlow gwf) throws Exception {
        //如果已经完成了，并且节点不是最后一个节点就不能打印.
        if (gwf.getIsOver()) {
            Node nd = new Node(gwf.getNodeID());
            if (nd.getItIsEndNode() == false) {
                return false;
            }
        }

        // 判断是否可以打印.
        String sql = "SELECT Distinct NDFrom, EmpFrom FROM ND" + Integer.parseInt(this.getFlowNo()) + "Track WHERE WorkID=" + this.getWorkID();
        DataTable dt = DBAccess.RunSQLReturnTable(sql);
        for (DataRow dr : dt.Rows) {
            //判断节点是否启用了按钮?
            int nodeid = Integer.parseInt(dr.getValue(0).toString());
            BtnLab btn = new BtnLab(nodeid);
            if (btn.getPrintPDFEnable() == true || btn.getPrintZipEnable() == true) {
                String empFrom = dr.getValue(1).toString();
                if (gwf.getIsOver() && (Objects.equals(WebUser.getNo(), empFrom) || Objects.equals(gwf.getStarter(), WebUser.getNo()))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取OneWork页面的tabs集合
     *
     * @return
     */
    public final String OneWork_GetTabs() throws Exception {
        DataTable dt = new DataTable();
        dt.Columns.Add("No", String.class);
        dt.Columns.Add("Name", String.class);
        dt.Columns.Add("Url", String.class);
        dt.Columns.Add("IsDefault", Integer.class);
        Flow flow = new Flow(this.getFlowNo());
        int nodeID = this.getNodeID();
        if (nodeID == 0) {
            GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
            nodeID = gwf.getNodeID();
        }
        DataRow dr = null;

        String paras = String.format("FK_Node=%s&WorkID=%d&FK_Flow=%s&FID=%d&FromWorkOpt=1&CCSta=" + this.GetRequestValInt("CCSta"), String.valueOf(nodeID), this.getWorkID(), this.getFlowNo(), this.getFID());
        String url = "";
		/*if (flow.IsFrmEnable == true)
		{

		    Node nd = new Node(nodeID);
		    url = "../../MyView.htm?" + paras;
		    if ((nd.getHisFormType() == NodeFormType.SDKForm || nd.getHisFormType() == NodeFormType.SelfForm))
		    {
		        if (nd.FormUrl.contains("?"))
		            url = "@url=" + nd.FormUrl + "&IsReadonly=1&" + paras;
		        else
		            url = "@url=" + nd.FormUrl + "?IsReadonly=1&" + paras;
		    }
		    dr = dt.NewRow();
		    dr["No"] = "Frm";
		    dr["Name"] = "表单";
		    dr["Url"] = url;
		    dr["IsDefault"] = 0;
		    dt.Rows.add(dr);
		}*/

        if (flow.getItIsTruckEnable() == true) {
            dr = dt.NewRow();
            dr.setValue("No", "Truck");
            dr.setValue("Name", "轨迹图");
            dr.setValue("Url", "Chart.htm?" + paras);
            dr.setValue("IsDefault", 0);
            dt.Rows.add(dr);
        }

        if (flow.getItIsTimeBaseEnable() == true) {
            dr = dt.NewRow();
            dr.setValue("No", "TimeBase");
            dr.setValue("Name", "时间轴");
            dr.setValue("Url", "TimeBase.htm?" + paras);
            dr.setValue("IsDefault", 0);
            dt.Rows.add(dr);
        }

        if (flow.getItIsTableEnable() == true) {
            dr = dt.NewRow();
            dr.setValue("No", "Table");
            dr.setValue("Name", "时间表");
            dr.setValue("Url", "Table.htm?" + paras);
            dr.setValue("IsDefault", 0);
            dt.Rows.add(dr);
        }


        return bp.tools.Json.ToJson(dt);
    }

    /**
     * 获取流程的JSON数据，以供显示工作轨迹/流程设计
     *
     * @return
     */
    public final String Chart_Init2020() {
        //参数.
        String fk_flow = this.getFlowNo();
        long workid = this.getWorkID();
        long fid = this.getFID();

        DataSet ds = new DataSet();
        DataTable dt = null;
        String json = "";
        try {
            //流程信息
            String sql = "SELECT No as \"No\", Name as \"Name\", ChartType as \"ChartType\" FROM WF_Flow WHERE No='" + fk_flow + "'";
            dt = DBAccess.RunSQLReturnTable(sql);
            dt.TableName = "WF_Flow";
            ds.Tables.add(dt);

            //节点信息 ，
            // NodePosType=0，开始节点， 1中间节点,2=结束节点.
            // RunModel= select * FROM sys_enums where Enumkey='RunModel'
            // TodolistModel= select * FROM sys_enums where Enumkey='TodolistModel' ;
            sql = "SELECT NodeID as \"ID\", Name as \"Name\", ICON as \"Icon\", X as \"X\", Y as \"Y\", NodePosType as \"NodePosType\",RunModel as \"RunModel\",HisToNDs as \"HisToNDs\",TodolistModel as \"TodolistModel\" FROM WF_Node WHERE FK_Flow='" + fk_flow + "' ORDER BY Step";
            dt = DBAccess.RunSQLReturnTable(sql);
            dt.TableName = "WF_Node";
            ds.Tables.add(dt);

            //标签信息
            sql = "SELECT MyPK as \"MyPK\", Name  as\"Name\", X as \"X\", Y as \"Y\" FROM WF_LabNote WHERE FK_Flow='" + fk_flow + "'";
            dt = DBAccess.RunSQLReturnTable(sql);
            dt.TableName = "WF_LabNote";
            ds.Tables.add(dt);

            //线段方向信息
            sql = "SELECT Node as \"Node\", ToNode as \"ToNode\", 0 as  \"DirType\", 0 as \"IsCanBack\" FROM WF_Direction WHERE FK_Flow='" + fk_flow + "'";
            dt = DBAccess.RunSQLReturnTable(sql);
            dt.TableName = "WF_Direction";
            ds.Tables.add(dt);

            //如果workid=0就仅仅返回流程图数据.
            if (workid == 0) {
                return bp.tools.Json.ToJson(ds);
            }


            //流程信息.
            GenerWorkFlow gwf = new GenerWorkFlow(workid);
            dt = gwf.ToDataTableField("Main"); // DBAccess.RunSQLReturnTable(string.Format(sql, workid));
            dt.TableName = "WF_GenerWorkFlow";
            ds.Tables.add(dt);

            //把节点审核配置信息.
            NodeWorkCheck fwc = new NodeWorkCheck(gwf.getNodeID());
            ds.Tables.add(fwc.ToDataTableField("FrmWorkCheck"));


            //获取工作轨迹信息
            String trackTable = "ND" + Integer.parseInt(fk_flow) + "Track";
            sql = "SELECT FID as \"FID\",NDFrom as \"NDFrom\",NDFromT as \"NDFromT\",NDTo as \"NDTo\", NDToT as \"NDToT\", ActionType as \"ActionType\",ActionTypeText as \"ActionTypeText\",Msg as \"Msg\",RDT as \"RDT\",EmpFrom as \"EmpFrom\",EmpFromT as \"EmpFromT\", EmpToT as \"EmpToT\",EmpTo as \"EmpTo\" FROM " + trackTable + " WHERE WorkID=" + workid + (fid == 0 ? (" OR FID=" + workid) : (" OR WorkID=" + fid + " OR FID=" + fid)) + " ORDER BY RDT DESC";
            dt = DBAccess.RunSQLReturnTable(sql);

            DataTable newdt = new DataTable();
            newdt = dt.clone();


            ///#region 判断轨迹数据中，最后一步是否是撤销或退回状态的，如果是，则删除最后2条数据
            if (dt.Rows.size() > 0) {
                int actionType = Integer.parseInt(dt.Rows.get(0).getValue("ActionType").toString());
                if (actionType == ActionType.Return.getValue() || actionType == ActionType.UnSend.getValue()) {
                    if (dt.Rows.size() > 1) {
                        dt.Rows.remove(1);
                        dt.Rows.remove(0);
                    } else {
                        dt.Rows.remove(0);
                    }
                    newdt = dt;
                } else if (dt.Rows.size() > 1 && (Integer.parseInt(dt.Rows.get(1).getValue("ActionType").toString()) == ActionType.Return.getValue() || Integer.parseInt(dt.Rows.get(1).getValue("ActionType").toString()) == ActionType.UnSend.getValue())) {
                    //删除已发送的节点，
                    if (dt.Rows.size() > 3) {
                        dt.Rows.remove(1);
                        dt.Rows.remove(1);
                    } else {
                        dt.Rows.remove(1);
                    }

                    String fk_node = "";
                    if (dt.Rows.get(0).getValue("NDFrom").equals(dt.Rows.get(0).getValue("NDTo"))) {
                        fk_node = dt.Rows.get(0).getValue("NDFrom").toString();
                    }
                    if (DataType.IsNullOrEmpty(fk_node) == false) {
                        //如果是跳转页面，则需要删除中间跳转的节点
                        for (DataRow dr : dt.Rows) {
                            if (Objects.equals(dr.getValue("ACTIONTYPE"), ActionType.Skip.getValue()) && dr.getValue("NDFrom").toString().equals(fk_node)) {
                                continue;
                            }
                            DataRow newdr = newdt.NewRow();
                            newdr.ItemArray = dr.ItemArray;
                            newdt.Rows.add(newdr);
                        }
                    } else {
                        newdt = dt.copy();
                    }
                } else {
                    newdt = dt.copy();
                }
            }
            newdt.TableName = "Track";
            ds.Tables.add(newdt);

            ///#endregion


            ///#region 如果流程没有完成,就把工作人员列表返回过去.
            if (gwf.getIsOver()==false) {
                //加入工作人员列表.
                GenerWorkerLists gwls = new GenerWorkerLists();
                long id = this.getFID();
                if (id == 0) {
                    id = this.getWorkID();
                }

                QueryObject qo = new QueryObject(gwls);
                qo.AddWhere(GenerWorkerListAttr.FID, id);
                qo.addOr();
                qo.AddWhere(GenerWorkerListAttr.WorkID, id);
                qo.DoQuery();

                DataTable dtGwls = gwls.ToDataTableField("WF_GenerWorkerlist");
                ds.Tables.add(dtGwls);
            }

            ///#endregion 如果流程没有完成,就把工作人员列表返回过去.

            String str = bp.tools.Json.ToJson(ds);
            //DataType.WriteFile("c:\\GetFlowTrackJsonData_CCflow.txt", str);
            return str;
        } catch (Exception ex) {
            return "err@" + ex.getMessage();
        }
    }

    /**
     * 获得最后一个人的审批意见
     *
     * @return
     */
    public final String SubFlowGuid_GenerLastOneCheckNote() {
        String table = "ND" + Integer.parseInt(this.getFlowNo()) + "Track";
        String sql = "SELECT Msg, WriteDB FROM " + table + " WHERE WorkID=" + this.getWorkID() + " AND ActionType=1 ORDER BY RDT DESC ";

        DataTable dt = DBAccess.RunSQLReturnTable(sql);

        String info = dt.Rows.get(0).getValue(0).toString();
        if (info.contains("WorkCheck@") == true) {
            info = info.substring(info.indexOf("WorkCheck@") + 10);
        }

        Hashtable ht = new Hashtable();
        ht.put("Msg", info);
        ht.put("WriteDB", dt.Rows.get(0).getValue(1));

        return bp.tools.Json.ToJson(ht);

    }

    public final String Chart_Init() {
        String fk_flow = this.getFlowNo();
        long workid = this.getWorkID();
        long fid = this.getFID();

        DataSet ds = new DataSet();
        DataTable dt = null;
        String json = "";
        try {
            //获取流程信息
            String sql = "SELECT No as \"No\", Name as \"Name\",  ChartType as \"ChartType\" FROM WF_Flow WHERE No='" + fk_flow + "'";
            dt = DBAccess.RunSQLReturnTable(sql);
            dt.TableName = "WF_Flow";
            ds.Tables.add(dt);

            //获取流程中的节点信息
            sql = "SELECT NodeID as \"ID\", Name as \"Name\", ICON as \"Icon\", X as \"X\", Y as \"Y\", NodePosType as \"NodePosType\",RunModel as \"RunModel\",HisToNDs as \"HisToNDs\",TodolistModel as \"TodolistModel\", NodeType as \"NodeType\",Step as \"Step\" FROM WF_Node WHERE FK_Flow='" + fk_flow + "' ORDER BY Step";
            dt = DBAccess.RunSQLReturnTable(sql);
            dt.TableName = "WF_Node";
            ds.Tables.add(dt);

            //获取流程中的标签信息
            sql = "SELECT MyPK as \"MyPK\", Name as \"Name\", X as \"X\", Y as \"Y\" FROM WF_LabNote WHERE FK_Flow='" + fk_flow + "'";
            dt = DBAccess.RunSQLReturnTable(sql);
            dt.TableName = "WF_LabNote";
            ds.Tables.add(dt);

            //获取流程中的线段方向信息
            sql = "SELECT Node as \"Node\", ToNode as \"ToNode\", 0 as  \"DirType\", 0 as \"IsCanBack\" FROM WF_Direction WHERE FK_Flow='" + fk_flow + "'";
            dt = DBAccess.RunSQLReturnTable(sql);
            dt.TableName = "WF_Direction";
            ds.Tables.add(dt);

            if (workid != 0) {
                GenerWorkFlow gwf = new GenerWorkFlow(workid);
                dt = gwf.ToDataTableField();
                dt.TableName = "FlowInfo";
                ds.Tables.add(dt);

                //获得流程状态.
                WFState wfState = gwf.getWFState();

                String fk_Node = String.valueOf(gwf.getNodeID());

                //把节点审核配置信息.
                NodeWorkCheck fwc = new NodeWorkCheck(fk_Node);
                ds.Tables.add(fwc.ToDataTableField("FrmWorkCheck"));


                //获取工作轨迹信息
                String trackTable = "ND" + Integer.parseInt(fk_flow) + "Track";
                switch (SystemConfig.getAppCenterDBType())
                {   case PostgreSQL:
                    case Oracle:
                    case DM:
                    case GBASE8CByOracle:
                        sql = "SELECT FID ," +
                                "NDFrom ," +
                                "NDFromT ," +
                                "NDTo , " +
                                "NDToT , " +
                                "ActionType ," +
                                "ActionTypeText ," +
                                "Msg ," +
                                "RDT ," +
                                "EmpFrom ," +
                                "EmpFromT , " +
                                "EmpToT ," +
                                "EmpTo , " +
                                "NodeData  " +
                                "FROM " + trackTable + " " +
                                "WHERE WorkID=" + workid + (fid == 0 ? (" " +
                                "OR FID=" + workid) : (" " +
                                "OR WorkID=" + fid + " OR FID=" + fid)) + " " +
                                "ORDER BY RDT DESC";
                        break;
                    case KingBaseR3:
                    case KingBaseR6:
                    case HGDB:
                        sql = "SELECT FID as \"FID\"," +
                                "NDFrom as \"NDFrom\"," +
                                "NDFromT as \"NDFromT\"," +
                                "NDTo as \"NDTo\", " +
                                "NDToT as \"NDToT\", " +
                                "ActionType as \"ActionType\"," +
                                "ActionTypeText as \"ActionTypeText\"," +
                                "Msg as \"Msg\"," +
                                "RDT as \"RDT\"," +
                                "EmpFrom as \"EmpFrom\"," +
                                "EmpFromT as \"EmpFromT\", " +
                                "EmpToT as \"EmpToT\"," +
                                "EmpTo as \"EmpTo\", " +
                                "NodeData as \"NodeData\" " +
                                "FROM " + trackTable + " " +
                                "WHERE WorkID=" + workid + (fid == 0 ? (" " +
                                "OR FID=" + workid) : (" " +
                                "OR WorkID=" + fid + " OR FID=" + fid)) + " " +
                                "ORDER BY RDT DESC";
                        break;
                    default:
                        sql = "SELECT FID as 'FID'," +
                                "NDFrom as 'NDFrom'," +
                                "NDFromT as 'NDFromT'," +
                                "NDTo as 'NDTo', " +
                                "NDToT as 'NDToT', " +
                                "ActionType as 'ActionType'," +
                                "ActionTypeText as 'ActionTypeText'," +
                                "Msg as 'Msg'," +
                                "RDT as 'RDT'," +
                                "EmpFrom as 'EmpFrom'," +
                                "EmpFromT as 'EmpFromT', " +
                                "EmpToT as 'EmpToT'," +
                                "EmpTo as 'EmpTo', " +
                                "NodeData as 'NodeData' " +
                                "FROM " + trackTable + " " +
                                "WHERE WorkID=" + workid + (fid == 0 ? (" " +
                                "OR FID=" + workid) : (" " +
                                "OR WorkID=" + fid + " OR FID=" + fid)) + " " +
                                "ORDER BY RDT DESC";
                        break;
              }


                dt = DBAccess.RunSQLReturnTable(sql);
                //把列名转化成区分大小写.
                if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
                {
                    dt.Columns.get("ACTIONTYPE").ColumnName = "ActionType";
                    dt.Columns.get("ACTIONTYPETEXT").ColumnName = "ActionTypeText";
                    dt.Columns.get("FID").ColumnName = "FID";
                    dt.Columns.get("NDFROM").ColumnName = "NDFrom";
                    dt.Columns.get("NDFROMT").ColumnName = "NDFromT";
                    dt.Columns.get("NDTO").ColumnName = "NDTo";
                    dt.Columns.get("NDTOT").ColumnName = "NDToT";
                    dt.Columns.get("EMPFROM").ColumnName = "EmpFrom";
                    dt.Columns.get("EMPFROMT").ColumnName = "EmpFromT";
                    dt.Columns.get("EMPTO").ColumnName = "EmpTo";
                    dt.Columns.get("EMPTOT").ColumnName = "EmpToT";
                    dt.Columns.get("RDT").ColumnName = "RDT";
                    dt.Columns.get("WORKTIMESPAN").ColumnName = "WorkTimeSpan";
                    dt.Columns.get("MSG").ColumnName = "Msg";
                    dt.Columns.get("NODEDATA").ColumnName = "NodeData";
                }
                DataTable newdt = new DataTable();
                newdt = dt.clone();

                //现在轨迹中有路由节点撤销节点的处理
                List<DataRow> selectRow = dt.select("ActionType=5");
                if(!selectRow.isEmpty()){
                    for(int k=0;k<selectRow.size();k++){
                        DataTable curDt = dt.copy();
                        int idx = 0;
                        for(DataRow dr : dt.Rows){
                            ActionType actionType = ActionType.forValue(Integer.parseInt(dr.getValue("ActionType").toString()));
                            if(actionType== ActionType.UnSend){
                                //获取退回到的节点
                                String ndT = dr.getValue("NDTo").toString();
                                String ndF = dr.getValue("NDFrom").toString();
                                //循环要移除的数据
                                boolean isBreak = false;
                                for(int dIdx=idx+1;dIdx<dt.Rows.size(); dIdx++){
                                    DataRow ddr = dt.Rows.get(idx+1);
                                    actionType = ActionType.forValue(Integer.parseInt(ddr.getValue("ActionType").toString()));
                                    String ndF1 = ddr.getValue("NDFrom").toString();
                                    String ndT1 = ddr.getValue("NDTo").toString();
                                    if(actionType == ActionType.Forward || actionType == ActionType.Skip || actionType == ActionType.Order){
                                        if(ndF1.equals(ndT)==true && ndT1.equals(ndF)==true){
                                            dt.Rows.remove(idx+1);
                                            break;
                                        }
                                        //如果出现跳转，NDF是撤销到的节点也break
                                        if(ndF1.equals(ndT)==true){
                                            dt.Rows.remove(idx+1);
                                            break;
                                        }
                                    }
                                    dt.Rows.remove(idx+1);

                                }
                                //移除当前的节点
                                dt.Rows.remove(idx);
                                break;
                            }
                            idx++;
                        }
                    }
                }

                //判断轨迹数据中，最后一步是否是撤销或退回状态的，如果是，则删除最后2条数据
                if (dt.Rows.size() > 0) {
                    int actionType = Integer.parseInt(dt.Rows.get(0).getValue("ActionType").toString());
                    if (actionType == ActionType.Return.getValue() || actionType == ActionType.UnSend.getValue()) {
                        if (dt.Rows.size() > 1) {
                            dt.Rows.remove(1);
                            dt.Rows.remove(0);
                        } else {
                            dt.Rows.remove(0);
                        }

                        newdt = dt;
                    } else if (dt.Rows.size() > 1 && (Integer.parseInt(dt.Rows.get(1).getValue("ActionType").toString()) == ActionType.Return.getValue() || Integer.parseInt(dt.Rows.get(1).getValue("ActionType").toString()) == ActionType.UnSend.getValue())) {
                        //删除已发送的节点，
                        if (dt.Rows.size() > 3) {
                            dt.Rows.remove(1);
                            dt.Rows.remove(1);
                        } else {
                            dt.Rows.remove(1);
                        }

                        String fk_node = "";
                        if (dt.Rows.get(0).getValue("NDFrom").equals(dt.Rows.get(0).getValue("NDTo"))) {
                            fk_node = dt.Rows.get(0).getValue("NDFrom").toString();
                        }
                        if (DataType.IsNullOrEmpty(fk_node) == false) {
                            //如果是跳转页面，则需要删除中间跳转的节点
                            for (DataRow dr : dt.Rows) {
                                if (Objects.equals(dr.getValue("ACTIONTYPE"), ActionType.Skip.getValue()) && dr.getValue("NDFrom").toString().equals(fk_node)) {
                                    continue;
                                }
                                DataRow newdr = newdt.NewRow();
                                //修复轨迹图不显示
                                for(int i=0;i<newdr.columns.stream().count();i++){
                                    newdr.setValue(i,dr.getValue(i));
                                }
                                newdt.Rows.add(newdr);
                            }
                        } else {
                            newdt = dt.copy();
                        }
                    } else {
                        newdt = dt.copy();
                    }
                }

                newdt.TableName = "Track";
                ds.Tables.add(newdt);

                //获取预先计算的节点处理人，以及处理时间,added by liuxc,2016-4-15
                sql = "SELECT wsa.FK_Node as \"FK_Node\",wsa.FK_Emp as \"FK_Emp\",wsa.EmpName as \"EmpName\",wsa.TimeLimit as \"TimeLimit\",wsa.TSpanHour as \"TSpanHour\",wsa.ADT as \"ADT\",wsa.SDT as \"SDT\" FROM WF_SelectAccper wsa WHERE wsa.WorkID = " + workid;
                dt = DBAccess.RunSQLReturnTable(sql);
                // dt.TableName = "POSSIBLE";
                dt.TableName = "Possible";
                ds.Tables.add(dt);

                //获取节点处理人数据，及处理/查看信息
                sql = "SELECT wgw.FK_Emp as \"FK_Emp\",wgw.FK_Node as \"FK_Node\",wgw.EmpName as \"FK_EmpText\",wgw.RDT as \"RDT\",wgw.IsRead as \"IsRead\",wgw.IsPass as \"IsPass\" FROM WF_GenerWorkerlist wgw WHERE wgw.WorkID = " + workid + (fid == 0 ? (" OR FID=" + workid) : (" OR WorkID=" + fid + " OR FID=" + fid));
                dt = DBAccess.RunSQLReturnTable(sql);
                dt.TableName = "DISPOSE";
                ds.Tables.add(dt);


                //如果流程没有完成.
                if (wfState != WFState.CompleteEnd && wfState != WFState.CompleteByNode) {
                    GenerWorkerLists gwls = new GenerWorkerLists();
                    long id = this.getFID();
                    if (id == 0) {
                        id = this.getWorkID();
                    }

                    QueryObject qo = new QueryObject(gwls);
                    qo.AddWhere(GenerWorkerListAttr.FID, id);
                    qo.addOr();
                    qo.AddWhere(GenerWorkerListAttr.WorkID, id);
                    qo.addOrderBy(GenerWorkerListAttr.Idx);
                    qo.DoQuery();


                    DataTable dtGwls = gwls.ToDataTableField("WF_GenerWorkerlist");
                    ds.Tables.add(dtGwls);
                }

            } else {
                String trackTable = "ND" + Integer.parseInt(fk_flow) + "Track";
                sql = "SELECT NDFrom as \"NDFrom\", NDTo as \"NDTo\",ActionType as \"ActionType\",ActionTypeText as \"ActionTypeText\",Msg as \"Msg\",RDT as \"RDT\",EmpFrom as \"EmpFrom\",EmpFromT as \"EmpFromT\",EmpToT as \"EmpToT\",EmpTo as \"EmpTo\" FROM " + trackTable + " WHERE WorkID=0 ORDER BY RDT ASC";
                dt = DBAccess.RunSQLReturnTable(sql);
                dt.TableName = "TRACK";
                ds.Tables.add(dt);
            }

            //for (int i = 0; i < ds.Tables.Count; i++)
            //{
            //    dt = ds.Tables[i];
            //    dt.TableName = dt.TableName.ToUpper();
            //    for (int j = 0; j < dt.Columns.Count; j++)
            //    {
            //        dt.Columns.get(j).ColumnName = dt.Columns.get(j].ColumnName.ToUpper();
            //    }
            //}

            //获取子流程
            SubFlows subFlows = new SubFlows(this.getFlowNo());
            ds.Tables.add(subFlows.ToDataTableField("WF_NodeSubFlow"));

            //获取发起的子流程
            GenerWorkFlows gwfs = new GenerWorkFlows();
            gwfs.Retrieve(GenerWorkFlowAttr.PWorkID, this.getWorkID(), "WorkID");
            ds.Tables.add(gwfs.ToDataTableField("WF_GenerWorkFlow"));
            String str = bp.tools.Json.ToJson(ds);
            //  DataType.WriteFile("c:\\GetFlowTrackJsonData_CCflow.txt", str);
            return str;
        } catch (Exception ex) {
            return "err@" + ex.getMessage();
        }
    }

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

    /**
     * 获取未审批的节点
     * @return
     * @throws Exception
     */
    public String GetFutureNodeNotContainsEmps() throws Exception {

        String nodeIDs = "";
        GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
        if (gwf.getIsOver()==true)
            return nodeIDs;
        int currNodeID = gwf.getNodeID();
        //获取未来处理人的节点
        Node nd = new Node(currNodeID);
        if(nd.getItIsStartNode()){
            //String sql="SELECT Count(*) From WF_SelectAccper WHERE WorkID="+this.getWorkID();
            //if(DBAccess.RunSQLReturnValInt(sql)==0){
            Work wk = nd.getHisWork();
            wk.setOID(this.getWorkID());
            wk.Retrieve();
            WorkNode wn = new WorkNode(wk, nd);
            wn.rptGe = nd.getHisFlow().getHisGERpt();
            wn.rptGe.setOID(this.getWorkID());
            //执行计算未来处理人.
            FullSA fsa = new FullSA();
            fsa.DoIt2023(wn);
            //}
        }
        GERpt rptGe = nd.getHisFlow().getHisGERpt();
        rptGe.setOID(this.getWorkID());
        rptGe.RetrieveFromDBSources();
        Node hisNode = nd;
        //获取当前节点的抄送节点
        nodeIDs+=GetFutureCCNodes(nd,rptGe,nodeIDs);
        while (nd.getItIsEndNode()==false)
        {
            nd = NodeSend_GenerNextStepNode_Ext(nd, rptGe, this.getWebUser(), hisNode);
            if (nd == null)
                break;
            if(nd.getHisNodeType() == NodeType.RouteNode) continue;
            nodeIDs+=nd.getNodeID()+",";
            nodeIDs+=GetFutureCCNodes(nd,rptGe,nodeIDs);
            if (nd.getHisNodeType() == NodeType.UserNode)
                hisNode = nd;

        }
        return nodeIDs;
    }
    private String GetFutureCCNodes(Node nd,GERpt rptGe,String nodeIDs) throws Exception {
        String nodes="";
        //查询出来到达的抄送节点.
        Directions dirs = new Directions();
        dirs.Retrieve(DirectionAttr.Node, nd.getNodeID(), DirectionAttr.NodeType, NodeType.CCNode.getValue(), DirectionAttr.Idx);
        if (dirs.size() == 0)
        {
            return nodes; //如果没有抄送节点，就不处理.
        }

        //定义容器集合,获得可以抄送的节点.
        for (Direction dir : dirs.ToJavaList())
        {
            Conds conds = new Conds();
            int i = conds.Retrieve(CondAttr.FK_Node, nd.getNodeID(), CondAttr.ToNodeID, dir.getToNode(), CondAttr.CondType, CondType.Dir.getValue(), CondAttr.Idx);

            //判断是否通过.
            if (i == 0 || conds.GenerResult(rptGe, this.getWebUser()) == true)
            {
                if(nodeIDs.contains(dir.getToNode()+",") == false)
                    nodes+=dir.getToNode()+",";
            }
        }
        return nodes;
    }
    /**
     * 获取未审批的节点及审批人
     * @return
     * @throws Exception
     */
    public String GetFutureNodes() throws Exception {
        DataSet ds = new DataSet();
        GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
        if (gwf.getIsOver()==true)
            return bp.tools.Json.ToJson(ds);

        SelectAccpers accpers = new SelectAccpers();
        accpers.Retrieve(SelectAccperAttr.WorkID, this.getWorkID());
        SelectAccpers selectAccpers = new SelectAccpers();
        int currNodeID = gwf.getNodeID();
        //获取未来处理人的节点
        Node nd = new Node(currNodeID);
        GERpt rptGe = nd.getHisFlow().getHisGERpt();
        rptGe.setOID(this.getWorkID());
        rptGe.RetrieveFromDBSources();
        SelectAccpers curaccpers = null;
        SelectAccper accper = null;
        Node hisNode = nd;
        Nodes nds = new Nodes();
        while (nd.getItIsEndNode()==false)
        {
            nd = NodeSend_GenerNextStepNode_Ext(nd, rptGe, this.getWebUser(), hisNode);
            if (nd == null)
                break;
            nds.AddEntity(nd);
            curaccpers = (SelectAccpers)accpers.GetEntitiesByKey(SelectAccperAttr.WorkID, this.getWorkID(), SelectAccperAttr.FK_Node, nd.getNodeID());
            if (curaccpers != null)
            {
                selectAccpers.AddEntities(curaccpers);
                accper = (SelectAccper)curaccpers.get(0);
                this.getWebUser().setNo(accper.getEmpNo());
                this.getWebUser().setName(accper.getEmpName());
                this.getWebUser().setDeptNo(accper.getDeptNo());
                this.getWebUser().setDeptName(accper.getDeptName());
            }
            if (nd.getHisNodeType() == NodeType.UserNode)
                hisNode = nd;
            if (nd.getHisNodeType() == NodeType.RouteNode && accper == null)
            {
                accper = new SelectAccper();
                accper.setNodeID(nd.getNodeID());
                accper.setWorkID(this.getWorkID());
                selectAccpers.AddEntity(accper);
            }
        }
        ds.Tables.add(nds.ToDataTableField("Nodes"));
        ds.Tables.add(selectAccpers.ToDataTableField("SelectAccpers"));
        return bp.tools.Json.ToJson(ds);
    }

    private Node NodeSend_GenerNextStepNode_Ext(Node currNode,GERpt rptGe,WebUserCopy webUser,Node hisNode) throws Exception {
        Nodes nds = currNode.getHisToNodes();
        if (nds.size() == 1)
        {
            Node toND = (Node)nds.get(0);
            return toND;
        }
        if (nds.size() == 0)
            return null;
        //获得所有的方向,按照优先级, 按照条件处理方向，如果成立就返回.
        Directions dirs = new Directions(currNode.getNodeID());
        Node nd = null;
        if (dirs.size() == 1)
        {
            nd = new Node(dirs.get(0).GetValIntByKey(DirectionAttr.ToNode));
            return nd;
        }
        //定义没有条件的节点集合.
        Directions dirs0Cond = new Directions();

        for(Direction dir : dirs.ToJavaList())
        {
            //查询出来他的条件.
            Conds conds = new Conds();
            conds.Retrieve(CondAttr.FK_Node, currNode.getNodeID(),
                    CondAttr.ToNodeID, dir.getToNode(), CondAttr.CondType,
                    CondType.Dir.getValue(),
                    CondAttr.Idx);

            //可以到达的节点.
            if (conds.size() == 0)
            {
                dirs0Cond.AddEntity(dir); //把他加入到里面.
                continue;
            }

            //按条件计算.
            if (conds.GenerResult(rptGe, webUser, hisNode) == true)
            {
                nd = new Node(dir.getToNode());
                return nd;
            }
        }

        if (dirs0Cond.size() == 0)
            return null;

        if (dirs0Cond.size() != 1)
            return null;

        int toNodeID = dirs0Cond.get(0).GetValIntByKey(DirectionAttr.ToNode);
        nd = new Node(toNodeID);
        return nd;
    }

    /**
     * 获取流程的JSON数据，以供显示工作轨迹/流程设计
     *
     * @return
     */
    public final String GetFlowTrackJsonData() {
        String fk_flow = this.getFlowNo();
        long workid = this.getWorkID();
        long fid = this.getFID();


        DataSet ds = new DataSet();
        DataTable dt = null;
        try {
            //获取流程信息
            String sql = "SELECT No \"No\", Name \"Name\",  ChartType \"ChartType\" FROM WF_Flow WHERE No='" + fk_flow + "'";
            dt = DBAccess.RunSQLReturnTable(sql);
            dt.TableName = "WF_FLOW";
            ds.Tables.add(dt);

            //获取流程中的节点信息
            sql = "SELECT NodeID \"ID\", Name \"Name\", ICON \"Icon\", X \"X\", Y \"Y\", NodePosType \"NodePosType\", RunModel \"RunModel\",HisToNDs \"HisToNDs\",TodolistModel \"TodolistModel\" FROM WF_Node WHERE FK_Flow='" + fk_flow + "' ORDER BY Step";
            dt = DBAccess.RunSQLReturnTable(sql);
            dt.TableName = "WF_NODE";
            ds.Tables.add(dt);

            //获取流程中的标签信息
            sql = "SELECT MyPK \"MyPK\", Name \"Name\", X \"X\", Y \"Y\" FROM WF_LabNote WHERE FK_Flow='" + fk_flow + "'";
            dt = DBAccess.RunSQLReturnTable(sql);
            dt.TableName = "WF_LABNOTE";
            ds.Tables.add(dt);

            //获取流程中的线段方向信息
            sql = "SELECT Node \"Node\", ToNode \"ToNode\", 0 as  \"DirType\", 0 as \"IsCanBack\" FROM WF_Direction WHERE FK_Flow='" + fk_flow + "'";
            dt = DBAccess.RunSQLReturnTable(sql);
            dt.TableName = "WF_DIRECTION";
            ds.Tables.add(dt);

            if (workid != 0) {
                //获取流程信息，added by liuxc,2016-10-26
                //sql =
                //    "SELECT wgwf.Starter,wgwf.StarterName,wgwf.RDT,wgwf.WFSta,wgwf.WFState FROM WF_GenerWorkFlow wgwf WHERE wgwf.WorkID = " +
                //    workid;
                sql = "SELECT wgwf.Starter as \"Starter\"," + "        wgwf.StarterName as \"StarterName\"," + "        wgwf.RDT as \"RDT\"," + "        wgwf.WFSta as \"WFSta\"," + "        se.getLab()  as   \"WFStaText\"," + "        wgwf.WFState as \"WFState\"," + "        wgwf.FID as \"FID\"," + "        wgwf.PWorkID as \"PWorkID\"," + "        wgwf.PFlowNo as \"PFlowNo\"," + "        wgwf.PNodeID as \"PNodeID\"," + "        wgwf.FK_Flow as \"FK_Flow\"," + "        wgwf.NodeID as \"FK_Node\"," + "        wgwf.Title as \"Title\"," + "        wgwf.WorkID as \"WorkID\"," + "        wgwf.NodeName as \"NodeName\"," + "        wf.Name  as   \"FlowName\"" + " FROM   WF_GenerWorkFlow wgwf" + "        INNER JOIN WF_Flow wf" + "             ON  wf.No=wgwf.FK_Flow" + "        INNER JOIN Sys_Enum se" + "             ON  se.getIntKey() = wgwf.WFSta" + "             AND se.EnumKey = 'WFSta'" + " WHERE  wgwf.WorkID = " + workid  + "        OR  wgwf.FID = " + workid  + "        OR  wgwf.PWorkID = " + workid  + " ORDER BY" + "        wgwf.RDT DESC";

                dt = DBAccess.RunSQLReturnTable(sql);
                dt.TableName = "FLOWINFO";
                ds.Tables.add(dt);

                //获取工作轨迹信息
                String trackTable = "ND" + Integer.parseInt(fk_flow) + "Track";
                sql = "SELECT NDFrom \"NDFrom\",NDFromT \"NDFromT\",NDTo  \"NDTo\", NDToT \"NDToT\", ActionType \"ActionType\",ActionTypeText \"ActionTypeText\",Msg \"Msg\",RDT \"RDT\",EmpFrom \"EmpFrom\",EmpFromT \"EmpFromT\", EmpToT \"EmpToT\",EmpTo \"EmpTo\" FROM " + trackTable + " WHERE WorkID=" + workid + (fid == 0 ? (" OR FID=" + workid) : (" OR WorkID=" + fid + " OR FID=" + fid)) + " ORDER BY RDT ASC";

                dt = DBAccess.RunSQLReturnTable(sql);

                //判断轨迹数据中，最后一步是否是撤销或退回状态的，如果是，则删除最后2条数据
                if (dt.Rows.size() > 0) {
                    int actionType = Integer.parseInt(dt.Rows.get(0).getValue("ACTIONTYPE").toString());
                    if (actionType == ActionType.Return.getValue() || actionType == ActionType.UnSend.getValue()) {
                        if (dt.Rows.size() > 1) {
                            dt.Rows.remove(0);
                            dt.Rows.remove(0);
                        } else {
                            dt.Rows.remove(0);
                        }
                    }
                }

                dt.TableName = "TRACK";
                ds.Tables.add(dt);

                //获取预先计算的节点处理人，以及处理时间,added by liuxc,2016-4-15
                sql = "SELECT wsa.FK_Node as \"FK_Node\",wsa.FK_Emp as \"FK_Emp\",wsa.EmpName as \"EmpName\",wsa.TimeLimit as \"TimeLimit\",wsa.TSpanHour as \"TSpanHour\",wsa.ADT as \"ADT\",wsa.SDT as \"SDT\" FROM WF_SelectAccper wsa WHERE wsa.WorkID = " + workid;
                dt = DBAccess.RunSQLReturnTable(sql);
                dt.TableName = "POSSIBLE";
                ds.Tables.add(dt);

                //获取节点处理人数据，及处理/查看信息
                sql = "SELECT wgw.FK_Emp as \"FK_Emp\",wgw.FK_Node as \"FK_Node\",wgw.EmpName as \"EmpName\",wgw.RDT as \"RDT\",wgw.IsRead as \"IsRead\",wgw.IsPass as \"IsPass\" FROM WF_GenerWorkerlist wgw WHERE wgw.WorkID = " + workid + (fid == 0 ? (" OR FID=" + workid) : (" OR WorkID=" + fid + " OR FID=" + fid));
                dt = DBAccess.RunSQLReturnTable(sql);
                dt.TableName = "DISPOSE";
                ds.Tables.add(dt);
            } else {
                String trackTable = "ND" + Integer.parseInt(fk_flow) + "Track";
                sql = "SELECT NDFrom \"NDFrom\", NDTo \"NDTo\",ActionType \"ActionType\",ActionTypeText \"ActionTypeText\",Msg \"Msg\",RDT \"RDT\",EmpFrom \"EmpFrom\",EmpFromT \"EmpFromT\",EmpToT \"EmpToT\",EmpTo \"EmpTo\" FROM " + trackTable + " WHERE WorkID=0 ORDER BY RDT ASC";
                dt = DBAccess.RunSQLReturnTable(sql);
                dt.TableName = "TRACK";
                ds.Tables.add(dt);
            }

            String str = bp.tools.Json.ToJson(ds);
            //  DataType.WriteFile("c:\\GetFlowTrackJsonData_CCflow.txt", str);
            return str;
        } catch (Exception ex) {
            return "err@" + ex.getMessage();
        }
    }

    /**
     * 获得发起的BBS评论.
     *
     * @return
     */
    public final String FlowBBSList() throws Exception {
        bp.ccbill.Tracks tracks = new bp.ccbill.Tracks();
        QueryObject qo = new QueryObject(tracks);
        qo.AddWhere(TrackAttr.ActionType, bp.ccbill.FrmActionType.BBS);
        qo.addAnd();
        qo.addLeftBracket();

        if (this.getFID() != 0) {
            qo.AddWhere(TrackAttr.WorkID, this.getFID());
            qo.addOr();
            qo.AddWhere(TrackAttr.FID, this.getFID());
        } else {
            qo.AddWhere(TrackAttr.WorkID, this.getWorkID());

            if (this.getWorkID() != 0) {
                qo.addOr();
                qo.AddWhere(TrackAttr.FID, this.getWorkID());
            }
        }
        qo.addRightBracket();
        qo.addOrderBy(TrackAttr.RDT);
        qo.DoQuery();
        //转化成json
        return bp.tools.Json.ToJson(tracks.ToDataTableField("dt"));
    }

    /**
     * 查看某一用户的评论.
     */
    public final String FlowBBS_Check() {
        Paras pss = new Paras();
        pss.SQL = "SELECT * FROM ND" + Integer.parseInt(this.getFlowNo()) + "Track WHERE ActionType=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "ActionType AND WorkID=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "WorkID AND  EMPFROMT='" + this.getUserName() + "'";
        pss.Add("ActionType", ActionType.FlowBBS.getValue());
        pss.Add("WorkID", this.getWorkID());

        return bp.tools.Json.ToJson(DBAccess.RunSQLReturnTable(pss));
    }

    /**
     * 提交评论.
     *
     * @return
     */
    public final String FlowBBS_Save() throws Exception {
        String msg = this.GetValFromFrmByKey("FlowBBS_Doc");
        String fk_mapData = this.GetRequestVal("FK_MapData");
        Node nd = new Node(this.getNodeID());
        if (DataType.IsNullOrEmpty(fk_mapData) == true) {
            fk_mapData = nd.getNodeFrmID();
        }
        MapData mapData = new MapData(fk_mapData);
        Dev2Interface.Track_WriteBBS(fk_mapData, mapData.getName(), this.getWorkID(), msg, this.getFID(), this.getFlowNo(), null, this.getNodeID(), nd.getName());
        return "评论信息保存成功";
    }

    /**
     * 回复评论.
     *
     * @return
     */
    public final String FlowBBS_Replay() throws Exception {
        SMS sms = new SMS();
        sms.RetrieveByAttr(SMSAttr.MyPK, getMyPK());
        sms.setMyPK(DBAccess.GenerGUID(0, null, null));
        sms.setRDT(DataType.getCurrentDateTime());
        sms.setSendToEmpNo(this.getUserName());
        sms.setSender(WebUser.getNo());
        sms.setTitle(this.getTitle());
        sms.setDocOfEmail(this.getMsg());
        sms.Insert();
        return null;
    }

    /**
     * 统计评论条数.
     *
     * @return
     */
    public final String FlowBBS_Count() {
        Paras ps = new Paras();
        ps.SQL = "SELECT COUNT(ActionType) FROM ND" + Integer.parseInt(this.getFlowNo()) + "Track WHERE ActionType=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "ActionType AND WorkID=" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "WorkID";
        ps.Add("ActionType", ActionType.FlowBBS.getValue());
        ps.Add("WorkID", this.getWorkID());
        String count = String.valueOf(DBAccess.RunSQLReturnValInt(ps));
        return count;
    }


}
