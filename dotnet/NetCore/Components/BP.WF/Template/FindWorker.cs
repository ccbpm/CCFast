using System;
using System.Collections.Generic;
using System.Data;
using System.Text;
using BP.En;
using BP.DA;
using BP.Port;
using BP.Sys;
using System.Collections;
using System.Security.Cryptography.X509Certificates;
using BP.Difference;
using BP.WF.Admin;
using Microsoft.AspNetCore.Server.IIS.Core;

namespace BP.WF.Template
{
    /// <summary>
    /// 找人规则
    /// </summary>
    public class FindWorker
    {
        public FindWorker()
        {
        }

        #region 身份.
        private WebUserCopy _webUserCopy = null;
        public WebUserCopy WebUser
        {
            get
            {
                if (_webUserCopy == null)
                {
                    _webUserCopy = new WebUserCopy();
                    _webUserCopy.LoadWebUser();
                }
                return _webUserCopy;
            }
            set
            {
                _webUserCopy = value;
            }
        }
        #endregion 身份.

        #region 变量
        public WorkNode town = null;
        public WorkNode currWn = null;
        public Flow fl = null;
        string dbStr = BP.Difference.SystemConfig.AppCenterDBVarStr;
        public Paras ps = null;
        string JumpToEmp = null;
        int JumpToNode = 0;
        Int64 WorkID = 0;
        #endregion 变量

        public DataTable FindByWorkFlowModel()
        {
            this.town = town;

            Node toNode = town.HisNode;
            bool isOpenSelecter = toNode.GetParaBoolen("IsOpenSelecter");

            DataTable dt = new DataTable();
            dt.Columns.Add("No", typeof(string));
            string sql;
            string FK_Emp;

            // 如果执行了两次发送，那前一次的轨迹就需要被删除,这里是为了避免错误。
            ps = new Paras();
            ps.Add("WorkID", this.WorkID);
            ps.Add("FK_Node", town.HisNode.NodeID);
            ps.SQL = "DELETE FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "WorkID AND FK_Node =" + dbStr + "FK_Node";
            DBAccess.RunSQL(ps);

            // 如果指定特定的人员处理。
            if (DataType.IsNullOrEmpty(JumpToEmp) == false)
            {
                string[] myEmpStrs = JumpToEmp.Split(',');
                foreach (string emp in myEmpStrs)
                {
                    if (DataType.IsNullOrEmpty(emp))
                        continue;
                    DataRow dr = dt.NewRow();
                    dr[0] = emp;
                    dt.Rows.Add(dr);
                }
                return dt;
            }

            // 按上一节点发送人处理。
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByPreviousNodeEmp)
            {
                DataRow dr = dt.NewRow();
                dr[0] = WebUser.No;
                dt.Rows.Add(dr);
                return dt;
            }

            //首先判断是否配置了获取下一步接受人员的sql.
            if (town.HisNode.HisDeliveryWay == DeliveryWay.BySQL
                || town.HisNode.HisDeliveryWay == DeliveryWay.BySQLTemplate
                || town.HisNode.HisDeliveryWay == DeliveryWay.BySQLAsSubThreadEmpsAndData)
            {

                if (town.HisNode.HisDeliveryWay == DeliveryWay.BySQLTemplate)
                {
                    SQLTemplate st = new SQLTemplate(town.HisNode.DeliveryParas);
                    sql = st.Docs;
                }
                else
                {
                    if (town.HisNode.DeliveryParas.Length < 4)
                        throw new Exception("@您设置的当前节点按照SQL，决定下一步的接受人员，但是你没有设置SQL.");
                    sql = town.HisNode.DeliveryParas;
                    sql = sql.Clone().ToString();
                }


                //特殊的变量.
                sql = sql.Replace("@FK_Node", this.town.HisNode.NodeID.ToString());
                sql = sql.Replace("@NodeID", this.town.HisNode.NodeID.ToString());

                sql = sql.Replace("@WorkID", this.currWn.HisWork.OID.ToString());
                sql = sql.Replace("@FID", this.currWn.HisWork.FID.ToString());
                sql = sql.Replace("@WebUser.DeptNo", WebUser.DeptNo);


                if (this.currWn.HisNode.FormType == NodeFormType.RefOneFrmTree)
                {
                    GEEntity en = new GEEntity(this.currWn.HisNode.NodeFrmID, this.currWn.HisWork.OID);
                    sql = BP.WF.Glo.DealExp(sql, en, null);
                }
                else
                    sql = BP.WF.Glo.DealExp(sql, this.currWn.rptGe, null);


                //如果里面还有变量，就可能是系统变量.
                if (sql.Contains("@") == true)
                {
                    GenerWorkFlow gwf = this.currWn.HisGenerWorkFlow;
                    AtPara ap = gwf.atPara;
                    foreach (string key in ap.HisHT.Keys)
                        sql = sql.Replace("@" + key + "", ap.GetValStrByKey(key));
                }
                if (sql.Contains("@") == true)
                    throw new Exception("err@接受人的表达式设置错误,没有找到变量[" + sql + "].");

                dt = DBAccess.RunSQLReturnTable(sql);
                if (dt.Rows.Count == 0)
                    throw new Exception("@没有找到可接受的工作人员@技术信息：执行的SQL没有发现人员:" + sql);
                return dt;
            }

            #region 按绑定部门计算,该部门一人处理标识该工作结束(子线程)..
            if (town.HisNode.HisDeliveryWay == DeliveryWay.BySetDeptAsSubthread)
            {
                if (this.town.HisNode.ItIsSubThread == false)
                    throw new Exception("@您设置的节点接收人方式为：按绑定部门计算,该部门一人处理标识该工作结束(子线程)，但是当前节点非子线程节点。");

                sql = "SELECT " + BP.Sys.Base.Glo.UserNo + ", Name,FK_Dept AS GroupMark FROM Port_Emp WHERE FK_Dept IN (SELECT FK_Dept FROM WF_NodeDept WHERE FK_Node=" + town.HisNode.NodeID + ")";
                dt = DBAccess.RunSQLReturnTable(sql);
                if (dt.Rows.Count == 0)
                    throw new Exception("@没有找到可接受的工作人员,接受人方式为, ‘按绑定部门计算,该部门一人处理标识该工作结束(子线程)’ @技术信息：执行的SQL没有发现人员:" + sql);
                return dt;
            }
            #endregion 按绑定部门计算,该部门一人处理标识该工作结束(子线程)..

            #region 按照明细表,作为子线程的接收人.
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByDtlAsSubThreadEmps)
            {
                if (this.town.HisNode.ItIsSubThread == false)
                    throw new Exception("@您设置的节点接收人方式为：以分流点表单的明细表数据源确定子线程的接收人，但是当前节点非子线程节点。");

                this.currWn.HisNode.WorkID = this.WorkID; //为获取表单ID ( NodeFrmID )提供参数.
                BP.Sys.MapDtls dtls = new BP.Sys.MapDtls(this.currWn.HisNode.NodeFrmID);
                string msg = null;
                foreach (BP.Sys.MapDtl dtl in dtls)
                {
                    try
                    {
                        string empFild = town.HisNode.DeliveryParas;
                        if (DataType.IsNullOrEmpty(empFild))
                            empFild = " UserNo ";

                        ps = new Paras();
                        ps.SQL = "SELECT " + empFild + ", * FROM " + dtl.PTable + " WHERE RefPK=" + dbStr + "OID ORDER BY OID";
                        if (BP.Difference.SystemConfig.AppCenterDBType == DBType.MySQL)
                            ps.SQL = "SELECT " + empFild + ", A.* FROM " + dtl.PTable + " A WHERE RefPK=" + dbStr + "OID ORDER BY OID";
                        ps.Add("OID", this.WorkID);
                        dt = DBAccess.RunSQLReturnTable(ps);
                        if (dt.Rows.Count == 0)
                            throw new Exception("@没有找到可接受的工作人员,流程设计错误，到达的节点（" + town.HisNode.Name + "）在指定的节点中没有数据，无法找到子线程的工作人员。");
                        return dt;
                    }
                    catch (Exception ex)
                    {
                        msg += ex.Message;
                        //if (dtls.Count == 1)
                        //    throw new Exception("@估计是流程设计错误,没有在分流节点的明细表中设置");
                    }
                }
                throw new Exception("@没有找到分流节点的明细表作为子线程的发起的数据源，流程设计错误，请确认分流节点表单中的明细表是否有UserNo约定的系统字段。" + msg);
            }
            #endregion 按照明细表,作为子线程的接收人.

            #region 按节点绑定的人员处理.
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByBindEmp)
            {
                sql = "SELECT A.No,A.Name FROM Port_Emp A, WF_NodeEmp B WHERE FK_Node=" + town.HisNode.NodeID + " AND A.No=B.FK_Emp ";
                if (SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
                    sql = "SELECT A.UserID No,A.Name  FROM Port_Emp A, WF_NodeEmp B WHERE FK_Node=" + town.HisNode.NodeID + " AND A.UserID=B.FK_Emp AND A.OrgNo='" + WebUser.OrgNo + "' ";

                dt = DBAccess.RunSQLReturnTable(sql);
                if (dt.Rows.Count == 0)
                    throw new Exception("@流程设计错误:下一个节点(" + town.HisNode.Name + ")没有绑定工作人员 . " + sql);
                return dt;
            }
            #endregion 按节点绑定的人员处理.

            string empNo = WebUser.No;
            string empDept = WebUser.DeptNo;


            #region 找指定节点的人员直属领导 .
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByEmpLeader)
            {
                //查找指定节点的人员， 如果没有节点，就是当前的节点.
                string para = town.HisNode.DeliveryParas;
                if (DataType.IsNullOrEmpty(para) == true)
                    para = this.currWn.HisNode.NodeID.ToString();

                //throw new Exception("err@配置错误，当前节点是找指定节点的直属领导，但是您没有设置指定的节点ID.");

                string[] strs = para.Split(',');
                foreach (string str in strs)
                {
                    if (DataType.IsNullOrEmpty(str) == true)
                        continue;

                    ps = new Paras();
                    ps.SQL = "SELECT FK_Emp FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node AND (IsPass=1 OR FK_Emp=" + dbStr + "FK_Emp)";
                    ps.Add("OID", this.WorkID);
                    ps.Add("FK_Node", int.Parse(str));
                    ps.Add("FK_Emp", WebUser.No);
                    DataTable dtt = DBAccess.RunSQLReturnTable(ps);
                    if (dtt.Rows.Count == 0)
                        continue;
                    foreach (DataRow dr in dtt.Rows)
                    {
                        empNo = dr[0].ToString();
                        //查找人员的直属leader
                        sql = "";
                        if (BP.Difference.SystemConfig.CCBPMRunModel == CCBPMRunModel.Single)
                            sql = "SELECT Leader,FK_Dept FROM Port_Emp WHERE No='" + empNo + "'";
                        if (BP.Difference.SystemConfig.CCBPMRunModel == CCBPMRunModel.GroupInc)
                            sql = "SELECT Leader,FK_Dept FROM Port_Emp WHERE No='" + empNo + "' And OrgNo = '" + WebUser.OrgNo + "'";
                        if (BP.Difference.SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
                            sql = "SELECT Leader,FK_Dept FROM Port_Emp WHERE No='" + WebUser.OrgNo + "_" + empNo + "'";

                        DataTable dtEmp = DBAccess.RunSQLReturnTable(sql);

                        //查找他的leader, 如果没有就找部门领导.
                        string leader = dtEmp.Rows[0][0] as string;
                        string deptNo = dtEmp.Rows[0][1] as string;
                        if (DataType.IsNullOrEmpty(leader) == true)
                        {
                            sql = "SELECT Leader FROM Port_Dept WHERE No='" + deptNo + "'";
                            leader = DBAccess.RunSQLReturnStringIsNull(sql, null);
                            if (leader == null)
                                throw new Exception("@没有找到可接受的工作人员,流程设计错误:下一个节点(" + town.HisNode.Name + ")设置的按照直属领导计算，没有维护(" + WebUser.No + "," + WebUser.Name + ")的直属领导 . ");

                        }
                        if (DataType.IsNullOrEmpty(leader) == false)
                        {
                            DataRow drr = dt.NewRow();
                            drr[0] = leader;
                            dt.Rows.Add(drr);
                        }
                    }
                }
                if (dt.Rows.Count == 0)
                    throw new Exception("@没有找到可接受的工作人员。@技术信息：找指定节点的人员直属领导:" + para);
                return dt;
            }
            #endregion .按照部门负责人计算

            #region 按照部门负责人计算. 
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByDeptLeader)
                return ByDeptLeader();
            #endregion .按照部门负责人计算


            #region 发送给分管领导 
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByDeptSpecer)
                return ByDeptSpecer();
            #endregion .发送给分管领导


            #region 按照部门分管领导计算.
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByDeptShipLeader)
                return ByDeptShipLeader();
            #endregion .按照部门负责人计算

            #region 按照选择的人员处理。
            if (town.HisNode.HisDeliveryWay == DeliveryWay.BySelected
                || town.HisNode.HisDeliveryWay == DeliveryWay.BySelectedForPrj
                || town.HisNode.HisDeliveryWay == DeliveryWay.ByFEE
                || town.HisNode.HisDeliveryWay == DeliveryWay.PreplaceWokerFix
                || town.HisNode.HisDeliveryWay == DeliveryWay.PreplaceWokerFree
                || town.HisNode.HisDeliveryWay == DeliveryWay.BySelected_2)
            {
                ps = new Paras();
                ps.Add("FK_Node", this.town.HisNode.NodeID);
                ps.Add("WorkID", this.currWn.HisWork.OID);
                ps.SQL = "SELECT FK_Emp FROM WF_SelectAccper WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND AccType=0 ORDER BY IDX";
                dt = DBAccess.RunSQLReturnTable(ps);
                if (dt.Rows.Count == 0)
                {
                    /*从上次发送设置的地方查询. */
                    SelectAccpers sas = new SelectAccpers();
                    int i = sas.QueryAccepterPriSetting(this.town.HisNode.NodeID);
                    if (i == 0)
                    {
                        if (town.HisNode.HisDeliveryWay == DeliveryWay.BySelected || town.HisNode.HisDeliveryWay == DeliveryWay.BySelected_2
                            || town.HisNode.HisDeliveryWay == DeliveryWay.BySelectedForPrj)
                        {
                            Node currNode = this.currWn.HisNode;
                            if (toNode.ItIsResetAccepter == false && toNode.HisToNDs.Contains("@" + currNode.NodeID) == true && currNode.HisToNDs.Contains("@" + toNode.NodeID) == true)
                            {
                                sql = "SELECT EmpFrom From ND" + Int32.Parse(toNode.FlowNo) + "Track WHERE WorkID=" + this.WorkID + " AND NDFrom=" + toNode.NodeID + " AND NDTo=" + currNode.NodeID + " AND ActionType IN(0,1,6,7,11)";
                                DataTable dtt = DBAccess.RunSQLReturnTable(sql);
                                if (dtt.Rows.Count > 0)
                                {
                                    foreach (DataRow drr in dtt.Rows)
                                    {
                                        DataRow dr = dt.NewRow();
                                        dr[0] = drr[0].ToString();
                                        dt.Rows.Add(dr);
                                    }
                                    return dt;
                                }
                            }
                            if (town.HisNode.HisDeliveryWay == DeliveryWay.BySelected_2)
                                throw new Exception("url@./WorkOpt/Accepter.htm?FK_Flow=" + toNode.FlowNo + "&FK_Node=" + this.currWn.HisNode.NodeID + "&ToNode=" + toNode.NodeID + "&WorkID=" + this.WorkID + "PageName=Accepter");
                            Selector select = new Selector(toNode.NodeID);
                            if (select.SelectorModel == SelectorModel.GenerUserSelecter)
                                throw new Exception("url@./WorkOpt/AccepterOfGener.htm?FK_Flow=" + toNode.FlowNo + "&FK_Node=" + this.currWn.HisNode.NodeID + "&ToNode=" + toNode.NodeID + "&WorkID=" + this.WorkID + "&PageName=AccepterOfGener");
                            else
                                throw new Exception("url@./WorkOpt/Accepter.htm?FK_Flow=" + toNode.FlowNo + "&FK_Node=" + this.currWn.HisNode.NodeID + "&ToNode=" + toNode.NodeID + "&WorkID=" + this.WorkID + "PageName=Accepter");
                        }
                        else
                        {
                            throw new Exception("@流程设计错误，请重写FEE，然后为节点(" + town.HisNode.Name + ")设置接受人员，详细请参考cc流程设计手册。");
                        }
                    }

                    //插入里面.
                    foreach (SelectAccper item in sas)
                    {
                        DataRow dr = dt.NewRow();
                        dr[0] = item.EmpNo;
                        dt.Rows.Add(dr);
                    }
                    return dt;
                }
                return dt;
            }
            #endregion 按照选择的人员处理。

            #region 按照指定节点的处理人计算。
            if (town.HisNode.HisDeliveryWay == DeliveryWay.BySpecNodeEmp
                || town.HisNode.HisDeliveryWay == DeliveryWay.ByStarter)
            {
                /* 按指定节点的人员计算 */
                string strs = town.HisNode.DeliveryParas;
                if (town.HisNode.HisDeliveryWay == DeliveryWay.ByStarter)
                {
                    Int64 myworkid = this.currWn.WorkID;
                    if (this.currWn.HisWork.FID != 0)
                        myworkid = this.currWn.HisWork.FID;
                    dt = DBAccess.RunSQLReturnTable("SELECT Starter as No, StarterName as Name FROM WF_GenerWorkFlow WHERE WorkID=" + myworkid);
                    if (dt.Rows.Count == 1)
                        return dt;

                    /* 有可能当前节点就是第一个节点，那个时间还没有初始化数据，就返回当前人. */
                    if (this.currWn.HisNode.ItIsStartNode)
                    {
                        DataRow dr = dt.NewRow();
                        dr[0] = WebUser.No;
                        dt.Rows.Add(dr);
                        return dt;
                    }

                    if (dt.Rows.Count == 0)
                        throw new Exception("@没有找到可接受的工作人员,流程设计错误，到达的节点（" + town.HisNode.Name + "）无法找到开始节点的工作人员。");
                    else
                        return dt;

                }

                // 首先从本流程里去找。
                strs = strs.Replace(";", ",");
                string[] ndStrs = strs.Split(',');
                foreach (string nd in ndStrs)
                {
                    if (DataType.IsNullOrEmpty(nd))
                        continue;

                    if (DataType.IsNumStr(nd) == false)
                        throw new Exception("流程设计错误:您设置的节点(" + town.HisNode.Name + ")的接收方式为按指定的节点角色投递，但是您没有在访问规则设置中设置节点编号。");

                    ps = new Paras();
                    string workSQL = "";
                    //获取指定节点的信息
                    Node specNode = new Node(int.Parse(nd));
                    //指定节点是子线程
                    if (specNode.ItIsSubThread == true)
                    {
                        if (this.currWn.HisNode.ItIsSubThread == true)
                            workSQL = "FID=" + this.currWn.HisWork.FID;
                        else
                            workSQL = "FID=" + this.WorkID;
                    }
                    else
                    {
                        if (this.currWn.HisNode.ItIsSubThread == true)
                            workSQL = "WorkID=" + this.currWn.HisWork.FID;
                        else
                            workSQL = "WorkID=" + this.WorkID;

                    }

                    ps.SQL = "SELECT DISTINCT(FK_Emp) FROM WF_GenerWorkerlist WHERE " + workSQL + " AND FK_Node=" + dbStr + "FK_Node AND IsEnable=1 ";
                    ps.Add("FK_Node", int.Parse(nd));

                    DataTable dt_ND = DBAccess.RunSQLReturnTable(ps);
                    //添加到结果表
                    if (dt_ND.Rows.Count != 0)
                    {
                        foreach (DataRow row in dt_ND.Rows)
                        {
                            DataRow dr = dt.NewRow();
                            dr[0] = row[0].ToString();
                            dt.Rows.Add(dr);
                        }
                        //此节点已找到数据则不向下找，继续下个节点
                        continue;
                    }

                    //就要到轨迹表里查,因为有可能是跳过的节点.
                    ps = new Paras();
                    ps.SQL = "SELECT DISTINCT(" + TrackAttr.EmpFrom + ") FROM ND" + int.Parse(fl.No) + "Track WHERE"
                        + " (ActionType=" + dbStr + "ActionType1 OR ActionType=" + dbStr + "ActionType2 OR ActionType=" + dbStr + "ActionType3"
                        + "  OR ActionType=" + dbStr + "ActionType4 OR ActionType=" + dbStr + "ActionType5 OR ActionType=" + dbStr + "ActionType6)"
                        + "   AND NDFrom=" + dbStr + "NDFrom AND " + workSQL;
                    ps.Add("ActionType1", (int)ActionType.Skip);
                    ps.Add("ActionType2", (int)ActionType.Forward);
                    ps.Add("ActionType3", (int)ActionType.ForwardFL);
                    ps.Add("ActionType4", (int)ActionType.ForwardHL);
                    ps.Add("ActionType5", (int)ActionType.SubThreadForward);
                    ps.Add("ActionType6", (int)ActionType.Start);
                    ps.Add("NDFrom", int.Parse(nd));

                    dt_ND = DBAccess.RunSQLReturnTable(ps);
                    if (dt_ND.Rows.Count != 0)
                    {
                        foreach (DataRow row in dt_ND.Rows)
                        {
                            DataRow dr = dt.NewRow();
                            dr[0] = row[0].ToString();
                            dt.Rows.Add(dr);
                        }
                        continue;
                    }

                    //从Selector中查找
                    ps = new Paras();
                    ps.SQL = "SELECT DISTINCT(FK_Emp) FROM WF_SelectAccper WHERE FK_Node=" + dbStr + "FK_Node AND " + workSQL;
                    ps.Add("FK_Node", int.Parse(nd));


                    dt_ND = DBAccess.RunSQLReturnTable(ps);
                    //添加到结果表
                    if (dt_ND.Rows.Count != 0)
                    {
                        foreach (DataRow row in dt_ND.Rows)
                        {
                            DataRow dr = dt.NewRow();
                            dr[0] = row[0].ToString();
                            dt.Rows.Add(dr);
                        }
                        //此节点已找到数据则不向下找，继续下个节点
                        continue;
                    }


                }

                //本流程里没有有可能该节点是配置的父流程节点,也就是说子流程的一个节点与父流程指定的节点的工作人员一致.
                GenerWorkFlow gwf = new GenerWorkFlow(this.WorkID);
                if (gwf.PWorkID != 0)
                {
                    foreach (string pnodeiD in ndStrs)
                    {
                        if (DataType.IsNullOrEmpty(pnodeiD))
                            continue;

                        Node nd = new Node(int.Parse(pnodeiD));
                        if (nd.FlowNo != gwf.PFlowNo)
                            continue; // 如果不是父流程的节点，就不执行.

                        ps = new Paras();
                        ps.SQL = "SELECT FK_Emp FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node AND IsPass=1 AND IsEnable=1 ";
                        ps.Add("FK_Node", nd.NodeID);
                        if (this.currWn.HisNode.ItIsSubThread == true)
                            ps.Add("OID", gwf.PFID);
                        else
                            ps.Add("OID", gwf.PWorkID);

                        DataTable dt_PWork = DBAccess.RunSQLReturnTable(ps);
                        if (dt_PWork.Rows.Count != 0)
                        {
                            foreach (DataRow row in dt_PWork.Rows)
                            {
                                DataRow dr = dt.NewRow();
                                dr[0] = row[0].ToString();
                                dt.Rows.Add(dr);
                            }
                            //此节点已找到数据则不向下找，继续下个节点
                            continue;
                        }

                        //就要到轨迹表里查,因为有可能是跳过的节点.
                        ps = new Paras();
                        ps.SQL = "SELECT " + TrackAttr.EmpFrom + " FROM ND" + int.Parse(fl.No) + "Track WHERE (ActionType=" + dbStr + "ActionType1 OR ActionType=" + dbStr + "ActionType2 OR ActionType=" + dbStr + "ActionType3 OR ActionType=" + dbStr + "ActionType4 OR ActionType=" + dbStr + "ActionType5) AND NDFrom=" + dbStr + "NDFrom AND WorkID=" + dbStr + "WorkID";
                        ps.Add("ActionType1", (int)ActionType.Start);
                        ps.Add("ActionType2", (int)ActionType.Forward);
                        ps.Add("ActionType3", (int)ActionType.ForwardFL);
                        ps.Add("ActionType4", (int)ActionType.ForwardHL);
                        ps.Add("ActionType5", (int)ActionType.Skip);

                        ps.Add("NDFrom", nd.NodeID);

                        if (this.currWn.HisNode.ItIsSubThread == true)
                            ps.Add("WorkID", gwf.PFID);
                        else
                            ps.Add("WorkID", gwf.PWorkID);

                        dt_PWork = DBAccess.RunSQLReturnTable(ps);
                        if (dt_PWork.Rows.Count != 0)
                        {
                            foreach (DataRow row in dt_PWork.Rows)
                            {
                                DataRow dr = dt.NewRow();
                                dr[0] = row[0].ToString();
                                dt.Rows.Add(dr);
                            }
                        }
                    }
                }
                //返回指定节点的处理人
                if (dt.Rows.Count != 0)
                    return dt;

                throw new Exception("@流程设计错误，到达的节点（" + town.HisNode.Name + "）在指定的节点(" + strs + ")中没有数据，无法找到工作的人员。 @技术信息如下: 投递方式:BySpecNodeEmp sql=" + ps.SQLNoPara);
            }
            #endregion 按照节点绑定的人员处理。

            #region 按照上一个节点表单指定字段的人员处理。
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByPreviousNodeFormEmpsField)
            {
                // 为河南安防增加接受人规则按从表获取. 
                int A5DataFrom = town.HisNode.GetParaInt("A5DataFrom");
                // 检查接受人员规则,是否符合设计要求.
                string specEmpFields = town.HisNode.DeliveryParas;
                if (DataType.IsNullOrEmpty(specEmpFields))
                    specEmpFields = "SysSendEmps";

                string emps = "";
                DataTable dtVals = null;

                #region 0.按主表的字段计算.
                if (A5DataFrom == 0)
                {
                    if (this.currWn.rptGe.EnMap.Attrs.Contains(specEmpFields) == false)
                        throw new Exception("@您设置的接受人规则是按照表单指定的字段，决定下一步的接受人员，该字段{" + specEmpFields + "}已经删除或者丢失。");

                    //
                    string ptable = this.currWn.rptGe.EnMap.PhysicsTable;
                    string emps1 = this.currWn.rptGe.GetValStringByKey(specEmpFields);
                    sql = "SELECT No,Name FROM Port_Emp WHERE No IN (" + BP.DA.DataType.DealFromatSQLWhereIn(emps1) + ")";
                    dt = DBAccess.RunSQLReturnTable(sql);
                    if (dt.Rows.Count == 0)
                    {
                        //获得数据.
                        dt = BP.WF.CCFormAPI.GenerPopData2022(this.WorkID.ToString(), specEmpFields);
                        if (dt.Rows.Count == 0)
                            throw new Exception("@没有找到可接受的工作人员,没有在字段[ " + specEmpFields + " ]中指定接受人，工作无法向下发送。");
                    }
                    return dt;
                }
                #endregion 按主表的字段计算.

                #region 1.按从表的字段计算.
                string dtlID = this.town.HisNode.GetParaString("A5DataDtl");
                if (dtlID != null)
                    throw new Exception("@到达节点[" + this.town.HisNode.Name + "]的接受人规则是按照从表的字段计算，但是您没有配置从表的字段");

                MapDtl dtl = new MapDtl();
                dtl.No = dtlID;
                if (dtl.RetrieveFromDBSources() == 0)
                    throw new Exception("@到达节点[" + this.town.HisNode.Name + "]的接受人规则是按照从表的字段计算，从表[" + dtlID + "]被删除.");


                MapAttrs dtlAttrs = new MapAttrs();
                dtlAttrs.Retrieve("FK_MapData", dtlID);

                if (dtlAttrs.GetCountByKey("KeyOfEn", specEmpFields) == 0)
                    throw new Exception("@到达节点[" + this.town.HisNode.Name + "]的接受人规则是按照从表的字段计算，从表[" + dtlID + "]的字段[" + specEmpFields + "]，被删除.");

                //获得数据.
                dt = BP.WF.CCFormAPI.GenerPopData2022(this.WorkID.ToString(), specEmpFields);
                if (dt.Rows.Count == 0)
                    throw new Exception("@没有找到可接受的工作人员@没有在字段[ " + specEmpFields + " ]中指定接受人，工作无法向下发送。");
                #endregion 按从表的字段计算.
            }
            #endregion 按照上一个节点表单指定字段的人员处理。
            #region 按照上一个节点表单从表指定字段的人员处理。
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByPreviousNodeFormEmpsFrmDtl)
            {
                ///#region 1.按从表的字段计算.
                String dtlID = this.town.HisNode.DeliveryParas;
                if (DataType.IsNullOrEmpty(dtlID) == true)
                {
                    throw new Exception("@到达节点[" + this.town.HisNode.Name + "]的接受人规则是按照从表的字段计算，但是您没有配置从表的字段");
                }

                MapDtl dtl = new MapDtl();
                dtl.No = dtlID;
                if (dtl.RetrieveFromDBSources() == 0)
                {
                    throw new Exception("@到达节点[" + this.town.HisNode.Name + "]的接受人规则是按照从表的字段计算，从表[" + dtlID + "]被删除.");
                }


                MapAttrs dtlAttrs = new MapAttrs();
                dtlAttrs.Retrieve("FK_MapData", dtlID, null);
                String specEmpFields = this.town.HisNode.GetParaString("DtlField");
                if (dtlAttrs.GetCountByKey("KeyOfEn", specEmpFields) == 0)
                {
                    throw new Exception("@到达节点[" + this.town.HisNode.Name + "]的接受人规则是按照从表的字段计算，从表[" + dtlID + "]的字段[" + specEmpFields + "]，被删除.");
                }
                String dtlRefPKVal = Dev2Interface.GetDtlRefPKVal(this.WorkID, this.currWn.HisGenerWorkFlow.PWorkID, this.currWn.HisGenerWorkFlow.FID, this.currWn.HisGenerWorkFlow.NodeID, dtl.FrmID, dtl);
                DataTable dtt = DBAccess.RunSQLReturnTable("SELECT " + specEmpFields + " From " + dtl.PTable + " WHERE RefPK='" + dtlRefPKVal + "'");
                if (dtt.Rows.Count == 0)
                {
                    throw new Exception("@没有找到可接受的工作人员@没有在字段[ " + specEmpFields + " ]中指定接受人，工作无法向下发送。");
                }
                string str = "";
                string empNos = ",";
                DataRow drr = null;
                foreach (DataRow dr in dtt.Rows)
                {
                    str = dr[0].ToString();
                    if (str.Contains(",") == true)
                    {
                        string[] strs = str.Split(",");
                        foreach (string no in strs)
                        {
                            if (empNos.Contains("," + no + ",") == false)
                            {
                                empNos += no + ",";
                                drr = dt.NewRow();
                                drr[0] = no;
                                dt.Rows.Add(drr);
                            }
                        }
                    }
                    else
                    {
                        if (empNos.Contains("," + str + ",") == false)
                        {
                            empNos += str + ",";
                            drr = dt.NewRow();
                            drr[0] = str;
                            dt.Rows.Add(drr);
                        }
                    }
                }
                return dt;
            }
            #endregion 按照上一个节点表单从表指定字段的人员处理。

            #region 字段是部门编号。
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByPreviousNodeFormDepts)
            {
                // 检查接受人员规则,是否符合设计要求.
                string specEmpFields = town.HisNode.DeliveryParas;
                if (DataType.IsNullOrEmpty(specEmpFields))
                    specEmpFields = "SysSendEmps";

                string emps = "";
                DataTable dtVals = null;

                if (this.currWn.rptGe.EnMap.Attrs.Contains(specEmpFields) == false)
                    throw new Exception("@您设置的接受人规则是按照表单指定的字段作为部门的编号，决定下一步的接受人员，该字段{" + specEmpFields + "}已经删除或者丢失。");

                //获得数据.
                dt = BP.WF.CCFormAPI.GenerPopData2022(this.WorkID.ToString(), specEmpFields);
                if (dt.Rows.Count == 0)
                    throw new Exception("@没有找到可接受的工作人员@没有在字段[ " + specEmpFields + " ]中指定接受人（部门字段），工作无法向下发送。");

                foreach (DataRow dr in dt.Rows)
                {
                    string deptNo = dr[0].ToString();
                    dr[0] = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Dept WHERE No='" + deptNo + "'");
                }
                return dt;

            }
            #endregion 字段是部门编号。

            #region 字段是权限组: 
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByPreviousNodeFormEmpsTeam)
            {

                // 检查接受人员规则,是否符合设计要求.
                string specEmpFields = town.HisNode.DeliveryParas;
                if (DataType.IsNullOrEmpty(specEmpFields))
                    specEmpFields = "SysSendEmps";

                string emps = "";
                DataTable dtVals = null;

                if (this.currWn.rptGe.EnMap.Attrs.Contains(specEmpFields) == false)
                    throw new Exception("@您设置的接受人规则是按照表单指定的字段作为部门的编号，决定下一步的接受人员，该字段{" + specEmpFields + "}已经删除或者丢失。");

                //获得数据.
                dt = BP.WF.CCFormAPI.GenerPopData2022(this.WorkID.ToString(), specEmpFields);
                if (dt.Rows.Count == 0)
                    throw new Exception("@没有找到可接受的工作人员@没有在字段[ " + specEmpFields + " ]中指定接受人（部门字段），工作无法向下发送。");

                DataTable mydt1 = new DataTable();
                mydt1.Columns.Add("No");
                mydt1.Columns.Add("Name");

                foreach (DataRow dr in dt.Rows)
                {
                    string deptNo = dr[0].ToString();
                    DataTable dtTemp = DBAccess.RunSQLReturnTable("SELECT FK_Emp FROM Port_TeamEmp WHERE FK_Teamp='" + deptNo + "'");
                }
                return dt;
            }
            #endregion 字段是部门编号。

            #region 绑定字典表。
            if (town.HisNode.HisDeliveryWay == DeliveryWay.BySFTable)
            {
                String pkval = town.HisNode.DeliveryParas;
                SFTable table = new SFTable(pkval);
                DataTable mydtTable = table.GenerHisDataTable(this.currWn.rptGe.Row);
                return mydtTable;
            }
            #endregion 绑定字典表。


            #region 按照上一个节点表单指定字段的 【部门】处理。
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByPreviousNodeFormDepts)
            {
                // 检查接受人员规则,是否符合设计要求.
                String specEmpFields = town.HisNode.DeliveryParas;
                if (DataType.IsNullOrEmpty(specEmpFields) == true)
                    throw new Exception("@您设置的接受人规则是按照表单指定的字段是部门，但是没有选择表单字段");
                if (this.currWn.rptGe.EnMap.Attrs.Contains(specEmpFields) == false)
                    throw new Exception("@您设置的接受人规则是按照表单指定的部门字段，决定下一步的接受人员，该字段{" + specEmpFields + "}已经删除或者丢失。");

                //判断该字段是否启用了pop返回值？
                sql = "SELECT  Tag1 AS VAL FROM Sys_FrmEleDB WHERE RefPKVal=" + this.WorkID + " AND EleID='" + specEmpFields + "'";
                String depts = "";
                DataTable dtVals = DBAccess.RunSQLReturnTable(sql);

                //获取接受人并格式化接受人,
                if (dtVals.Rows.Count > 0)
                {
                    foreach (DataRow dr in dtVals.Rows)
                    {
                        depts += dr[0].ToString() + ",";
                    }
                }
                else
                {
                    depts = this.currWn.rptGe.GetValStringByKey(specEmpFields);
                }


                depts = depts.Replace(" ", ""); //去掉空格.
                if (depts.EndsWith(","))
                    depts = depts.Substring(0, depts.Length - 1);
                if (DataType.IsNullOrEmpty(depts) == false)
                {
                    depts = "'" + depts.Replace(",", "','") + "'";
                }

                if (DataType.IsNullOrEmpty(depts) == true)
                    throw new Exception("@您设置的接受人规则是按照表单指定的部门字段，没有选择部门");
                //获取人员
                sql = "SELECT DISTINCT(FK_Emp) From Port_DeptEmp WHERE FK_Dept IN(" + depts + ")";
                DataTable dtt = DBAccess.RunSQLReturnTable(sql);
                if (dtt.Rows.Count == 0)
                    throw new Exception("@您设置的接受人规则是按照表单指定的部门字段，填写的部门中不存在人员");
                return dtt;
            }
            #endregion 按照上一个节点表单指定字段的 【部门】处理。

            #region 按照上一个节点表单指定字段的 【角色】处理。
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByPreviousNodeFormStationsAI
            || town.HisNode.HisDeliveryWay == DeliveryWay.ByPreviousNodeFormStationsOnly)
            {
                // 检查接受人员规则,是否符合设计要求.
                string specEmpFields = town.HisNode.DeliveryParas;
                if (DataType.IsNullOrEmpty(specEmpFields))
                    specEmpFields = "SysSendEmps";

                if (this.currWn.rptGe.EnMap.Attrs.Contains(specEmpFields) == false)
                    throw new Exception("@您设置的接受人规则是按照表单指定的角色字段，决定下一步的接受人员，该字段{" + specEmpFields + "}已经删除或者丢失。");

                //判断该字段是否启用了pop返回值？
                sql = "SELECT  Tag1 AS VAL FROM Sys_FrmEleDB WHERE RefPKVal=" + this.WorkID + " AND EleID='" + specEmpFields + "'";
                string emps = "";
                DataTable dtVals = DBAccess.RunSQLReturnTable(sql);

                //获得角色信息.
                string stas = "";

                //获取接受人并格式化接受人, 
                if (dtVals.Rows.Count > 0)
                {
                    foreach (DataRow dr in dtVals.Rows)
                        emps += dr[0].ToString() + ",";
                }
                else
                {
                    emps = this.currWn.rptGe.GetValStringByKey(specEmpFields);
                }
                emps = emps.Replace(" ", ""); //去掉空格.
                if (emps.Contains(",") && emps.Contains(";"))
                {
                    /*如果包含,; 例如 zhangsan,张三;lisi,李四;*/
                    string[] myemps1 = emps.Split(';');
                    foreach (string str in myemps1)
                    {
                        if (DataType.IsNullOrEmpty(str))
                            continue;

                        string[] ss = str.Split(',');
                        stas += "," + ss[0];

                        //DataRow dr = dt.NewRow();
                        // = ss[0];
                        //dt.Rows.Add(dr);
                    }
                    if (dt.Rows.Count == 0)
                        throw new Exception("@没有找到可接受的工作人员@输入的接受人员角色信息错误;[" + emps + "]。");
                    else
                        return dt;
                }
                else
                {
                    emps = emps.Replace(";", ",");
                    emps = emps.Replace("；", ",");
                    emps = emps.Replace("，", ",");
                    emps = emps.Replace("、", ",");
                    emps = emps.Replace("@", ",");

                    if (DataType.IsNullOrEmpty(emps))
                        throw new Exception("@没有找到可接受的工作人员@没有在字段[" + this.currWn.HisWork.EnMap.Attrs.GetAttrByKey(specEmpFields).Desc + "]中指定接受人，工作无法向下发送。");

                    // 把它加入接受人员列表中.
                    string[] myemps = emps.Split(',');
                    int nodeID = town.HisNode.NodeID;
                    foreach (string s in myemps)
                    {
                        if (DataType.IsNullOrEmpty(s))
                            continue;
                        stas += "," + s;
                    }
                }

                if (DataType.IsNullOrEmpty(stas) == true)
                    throw new Exception("err@按照上一个节点表单指定字段的,没有找到选择的岗位信息.");

                //根据角色：集合获取信息.
                stas = stas.Substring(1);

                //把这次的岗位s存储到临时变量,以方便用到下一个节点多人处理规则，按岗位删除时用到。
                this.currWn.HisGenerWorkFlow.SetPara("NodeStas" + town.HisNode.NodeID, stas);


                // 仅按角色计算.  以下都有要重写.
                if (toNode.HisDeliveryWay == DeliveryWay.ByPreviousNodeFormStationsOnly)
                {
                    dt = WorkFlowBuessRole.FindWorker_GetEmpsByStations(stas);
                    if (dt.Rows.Count == 0)
                        throw new Exception("err@没有找到可接受的工作人员@按照字段角色(仅按角色计算)找接受人错误,当前部门下没有您选择的角色人员.");

                    return dt;
                }

                #region 按角色智能计算, 还是集合模式.
                if (toNode.DeliveryStationReqEmpsWay == 0)
                {
                    string deptNo = WebUser.DeptNo;
                    dt = WorkFlowBuessRole.FindWorker_GetEmpsByDeptAI(stas, deptNo);
                    if (dt.Rows.Count == 0)
                        throw new Exception("err@没有找到可接受的工作人员@按照字段角色(智能)找接受人错误,当前部门与父级部门下没有您选择的角色人员.");
                    return dt;
                }
                #endregion 按角色智能计算, 要判断切片模式,还是集合模式.

                #region 按角色智能计算, 切片模式. 需要对每个角色都要找到接受人，然后把这些接受人累加起来.
                if (toNode.DeliveryStationReqEmpsWay == 1 || toNode.DeliveryStationReqEmpsWay == 2)
                {
                    string deptNo = WebUser.DeptNo;
                    string[] temps = stas.Split(',');
                    foreach (string str in temps)
                    {
                        //求一个角色下的人员.
                        DataTable mydt1 = WorkFlowBuessRole.FindWorker_GetEmpsByDeptAI(str, deptNo);

                        //如果是严谨模式.
                        if (toNode.DeliveryStationReqEmpsWay == 1 && mydt1.Rows.Count == 0)
                        {
                            Station st = new Station(str);
                            throw new Exception("@角色[" + st.Name + "]下，没有找到人不能发送下去，请检查组织结构是否完整。");
                        }

                        //累加.
                        foreach (DataRow dr in mydt1.Rows)
                        {
                            DataRow mydr = dt.NewRow();
                            mydr[0] = dr[0].ToString();
                            dt.Rows.Add(mydr);
                        }
                    }
                }
                #endregion 按角色智能计算, 切片模式.

                return dt;
            }
            #endregion 按照上一个节点表单指定字段的[角色]人员处理.

            #region 为省立医院增加，按照指定的部门范围内的角色计算..
            if (town.HisNode.HisDeliveryWay == DeliveryWay.FindSpecDeptEmpsInStationlist)
            {
                sql = "SELECT A.FK_Emp FROM Port_DeptEmpStation A WHERE A.FK_DEPT ='" + WebUser.DeptNo + "' AND A.FK_Station in(";
                sql += "select FK_Station from WF_NodeStation where FK_node=" + town.HisNode.NodeID + ")";

                dt = DBAccess.RunSQLReturnTable(sql);

                if (dt.Rows.Count > 0)
                    return dt;

                throw new Exception("@没有找到可接受的工作人员@节点访问规则(" + town.HisNode.HisDeliveryWay.ToString() + ")错误:节点(" + town.HisNode.NodeID + "," + town.HisNode.Name + "), 按照角色与部门的交集确定接受人的范围错误，没有找到人员:SQL=" + sql);

            }
            #endregion 按部门与角色的交集计算.

            #region 按部门与角色的交集计算. - 不包含子部门.
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByDeptAndStation && town.HisNode.GetParaBoolen("AccepterRoleSubDepts") == false)
            {
                //added by liuxc,2015.6.29.

                sql = "SELECT pdes.fk_emp AS No"
                    + " FROM   Port_DeptEmpStation pdes"
                    + "        INNER JOIN WF_NodeDept wnd "
                    + "             ON  wnd.fk_dept = pdes.fk_dept"
                    + "             AND wnd.fk_node = " + town.HisNode.NodeID
                    + "        INNER JOIN WF_NodeStation wns"
                    + "             ON  wns.FK_Station = pdes.fk_station"
                    + "             AND wns.fk_node =" + town.HisNode.NodeID
                    + " ORDER BY"
                    + "        pdes.fk_emp";

                dt = DBAccess.RunSQLReturnTable(sql);

                if (dt.Rows.Count > 0)
                    return dt;

                throw new Exception("@没有找到可接受的工作人员@节点访问规则(" + town.HisNode.HisDeliveryWay.ToString() + ")错误:节点(" + town.HisNode.NodeID + "," + town.HisNode.Name + "), 按照角色与部门的交集确定接受人的范围错误，没有找到人员:SQL=" + sql);

            }
            #endregion 按部门与角色的交集计算.

            #region 按部门与角色的交集计算. - 包含子部门.
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByDeptAndStation && town.HisNode.GetParaBoolen("AccepterRoleSubDepts") == true)
            {
                NodeDepts depts = new NodeDepts();
                depts.Retrieve("FK_Node", town.HisNode.NodeID);

                //遍历部门集合.
                foreach (NodeDept dept in depts)
                    dt = ByDeptAndStationExt(dt, town.HisNode, dept.DeptNo);

                if (dt.Rows.Count > 0)
                    return dt;

                throw new Exception("@没有找到可接受的工作人员@节点访问规则(" + town.HisNode.HisDeliveryWay.ToString() + ")错误:节点(" + town.HisNode.NodeID + "," + town.HisNode.Name + "), 按照角色与部门的交集确定接受人的范围错误，没有找到人员");

            }
            #endregion 按部门与角色的交集计算.

            #region 判断节点部门里面是否设置了部门，如果设置了就按照它的部门处理。
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByDept)
            {
                ps = new Paras();
                ps.Add("FK_Node", this.town.HisNode.NodeID);
                ps.SQL = "SELECT A.No,A.Name FROM Port_Emp A,Port_DeptEmp B WHERE A.No=B.FK_Emp AND B.FK_Dept IN(SELECT FK_dept FROM WF_NodeDept WHERE FK_Node =" + dbStr + "FK_Node)";
                dt = DBAccess.RunSQLReturnTable(ps);
                if (dt.Rows.Count == 0)
                    throw new Exception("err@没有找到可接受的工作人员@按照 [按绑定的部门计算] 计算接收人的时候出现错误，没有找到人，请检查节点绑定的部门下的人员.");
                return dt;
            }
            #endregion 判断节点部门里面是否设置了部门，如果设置了，就按照它的部门处理。

            #region 用户组 计算 
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByTeamOnly)
            {
                ps = new Paras();
                sql = "SELECT A.FK_Emp FROM Port_TeamEmp A, WF_NodeTeam B WHERE A.FK_Team=B.FK_Team AND B.FK_Node=" + town.HisNode.NodeID;
                dt = DBAccess.RunSQLReturnTable(sql);
                if (dt.Rows.Count > 0)
                    return dt;
                throw new Exception("@没有找到可接受的工作人员@节点访问规则错误:节点(" + town.HisNode.NodeID + "," + town.HisNode.Name + "), 仅按用户组计算，没有找到人员:SQL=" + sql);

            }
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByTeamOrgOnly)
            {
                sql = "SELECT DISTINCT A.FK_Emp FROM Port_TeamEmp A, WF_NodeTeam B  WHERE A.FK_Team=B.FK_Team AND B.FK_Node=" + toNode.NodeID;
                dt = DBAccess.RunSQLReturnTable(sql);
                if (dt.Rows.Count > 0)
                    return dt;

                throw new Exception("@没有找到可接受的工作人员@节点访问规则错误:节点(" + town.HisNode.NodeID + "," + town.HisNode.Name + "), 仅按用户组计算，没有找到人员:SQL=" + sql);
            }

            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByTeamDeptOnly)
            {
                sql = "SELECT DISTINCT A.FK_Emp FROM Port_TeamEmp A, WF_NodeTeam B, Port_DeptEmp C WHERE A.FK_Emp=C.FK_Emp AND A.FK_Team=B.FK_Team AND B.FK_Node=" + toNode.NodeID + " AND C.FK_Dept='" + WebUser.DeptNo + "'";
                dt = DBAccess.RunSQLReturnTable(sql);
                if (dt.Rows.Count > 0)
                    return dt;

                throw new Exception("@没有找到可接受的工作人员@节点访问规则错误 ByTeamDeptOnly :节点(" + town.HisNode.NodeID + "," + town.HisNode.Name + "), 仅按用户组计算，没有找到人员:SQL=" + sql);

            }
            #endregion

            #region 56.按照指定的部门集合与节点设置角色交集计算.
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByStationSpecDepts)
            {
                Node nd = town.HisNode;
                string sqlDepts = nd.ARDeptModelDeptsSQL(this.town.WorkID); // 获得部门的sqls.
                DataTable dtDepts = DBAccess.RunSQLReturnTable(sqlDepts);
                int dgModel = nd.GetParaInt("DGModel56");
                if (dtDepts.Rows.Count == 1)
                {
                    //如果只有一个部门.
                    string deptNo = dtDepts.Rows[0][0].ToString();

                    #region 判断递归模式. 0=递归并累加,递归到根节点,并把找到的人累加起来.
                    if (dgModel == 0)
                    {
                        DataTable dtEmps = new DataTable(); //定义容器.
                        dtEmps.Columns.Add("EmpNo");
                        string empNos = "";
                        string curEmpNo = "";
                        while (true)
                        {
                            string mysql = " SELECT FK_Emp FROM Port_DeptEmpStation A,WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + nd.NodeID + " AND A.FK_Dept='" + deptNo + "'";
                            dt = DBAccess.RunSQLReturnTable(mysql);
                            //插入里面去.
                            foreach (DataRow dr in dt.Rows)
                            {
                                curEmpNo = dr[0].ToString();
                                if (empNos.Contains(curEmpNo + ",") == false)
                                {
                                    DataRow mydr = dtEmps.NewRow();
                                    mydr[0] = dr[0];
                                    dtEmps.Rows.Add(mydr);
                                    empNos += curEmpNo + ",";
                                }
                            }
                            //找到上一级部门.
                            deptNo = DBAccess.RunSQLReturnStringIsNull("SELECT ParentNo FROM Port_Dept WHERE No='" + deptNo + "'", null).ToString();
                            if (deptNo == null || deptNo.Equals("0") == true)
                                break;
                            continue;
                        }

                        //判断是否有此数据.
                        if (dtEmps.Rows.Count == 0)
                            throw new Exception("err@到达节点[" + this.town.HisNode.Name + "],接受人规则是:按照指定的部门集合与节点设置角色交集计算,没有获得接受人,技术信息:sqlDepts:[" + sqlDepts + "]dgModel:[" + dgModel + "]");
                        return dtEmps;
                    }
                    #endregion 判断递归模式. 0=递归并累加,递归到根节点,并把找到的人累加起来.

                    #region 判断递归模式: 1=递归不累加,向根节点递归,如果找到人,就不在递归了.
                    if (dgModel == 1)
                    {
                        while (true)
                        {
                            string mysql = " SELECT FK_Emp FROM Port_DeptEmpStation A,WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + nd.NodeID + " AND A.FK_Dept='" + deptNo + "'";
                            dt = DBAccess.RunSQLReturnTable(mysql);
                            if (dt.Rows.Count == 0)
                            {
                                deptNo = DBAccess.RunSQLReturnStringIsNull("SELECT ParentNo FROM Port_Dept WHERE No='" + deptNo + "'", null).ToString();
                                if (deptNo == null || deptNo.Equals("0") == true)
                                    throw new Exception("err@到达节点[" + this.town.HisNode.Name + "],接受人规则是“按照指定的部门集合与节点设置角色交集计算,没有获得接受人”,技术信息：dgModel=1。");
                                continue;
                            }
                            return dt;
                        }
                    }
                    #endregion 判断递归模式.1=递归不累加,向根节点递归,如果找到人,就不在递归了.

                    #region 判断递归模式. 2=不递归, 仅仅按照指定的部门寻找.
                    if (dgModel == 2)
                    {
                        string mysql = " SELECT FK_Emp FROM Port_DeptEmpStation A,WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + nd.NodeID + " AND A.FK_Dept='" + deptNo + "'";
                        dt = DBAccess.RunSQLReturnTable(mysql);
                        if (dt.Rows.Count == 0)
                            throw new Exception("err@到达节点[" + this.town.HisNode.Name + "],接受人规则是[按照指定的部门集合与节点设置角色交集计算],没有获得接受人.");
                        return dt;
                    }
                    #endregion 判断递归模式.2=不递归, 仅仅按照指定的部门寻找.
                }

                string sqlStations = "SELECT FK_Station FROM WF_NodeStation WHERE FK_Node=" + nd.NodeID;
                //获得两个的交集.
                string mysql1 = " SELECT FK_Emp FROM Port_DeptEmpStation WHERE FK_Station IN(" + sqlStations + ") AND FK_Dept IN (" + sqlDepts + ")";
                dt = DBAccess.RunSQLReturnTable(mysql1);
                if (dt.Rows.Count == 0)
                    throw new Exception("err@没有找到可接受的工作人员@到达节点[" + this.town.HisNode.Name + "],接受人规则是[按照指定的部门集合与节点设置角色交集计算],没有获得接受人.");
                return dt;
            }
            #endregion 按照指定的角色集合与部门的交集计算


            #region 57. 按照指定的角色集合与节点设置部门交集计算.
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByStationSpecStas)
            {
                Node nd = town.HisNode;
                string sqlStations = nd.ARStaModelStasSQL(this.town.WorkID); //获得部门的sqls.
                string sqlDepts = "SELECT FK_Dept FROM WF_NodeDept WHERE FK_Node=" + nd.NodeID;
                DataTable dtDepts = DBAccess.RunSQLReturnTable(sqlDepts);
                if (dtDepts.Rows.Count == 1)
                {
                    //如果只有一个部门.
                    string deptNo = dtDepts.Rows[0][0].ToString();
                    if (DataType.IsNullOrEmpty(deptNo) == true)
                        throw new Exception("err@寻找接收人的时候出现错误，【按照指定的角色集合与节点设置部门交集计算】，节点[" + nd.NodeID + "," + nd.Name + "]维护的节点绑定部门有问题，请重新维护。");
                    while (true)
                    {
                        string mysql = "SELECT FK_Emp FROM Port_DeptEmpStation A WHERE FK_Station IN (" + sqlStations + ") AND  FK_Dept='" + deptNo + "'";
                        dt = DBAccess.RunSQLReturnTable(mysql);
                        if (dt.Rows.Count == 0)
                        {
                            deptNo = DBAccess.RunSQLReturnStringIsNull("SELECT ParentNo FROM Port_Dept WHERE No='" + deptNo + "'", null).ToString();
                            if (deptNo == null || deptNo.Equals("0") == true)
                                throw new Exception("err@到达节点[" + this.town.HisNode.Name + "],接受人规则是[按照指定的角色集合与节点设置部门交集计算],没有获得接受人.");
                            continue;
                        }
                        return dt;
                    }
                }

                //获得两个的交集.
                string mysql1 = " SELECT FK_Emp FROM Port_DeptEmpStation WHERE FK_Station IN(" + sqlStations + ") AND FK_Dept IN (" + sqlDepts + ")";
                dt = DBAccess.RunSQLReturnTable(mysql1);
                if (dt.Rows.Count == 0)
                    throw new Exception("err@没有找到可接受的工作人员@到达节点[" + this.town.HisNode.Name + "],接受人规则是[按照指定的角色集合与节点设置部门交集计算],没有获得接受人.");
                return dt;
            }
            #endregion 按照指定的部门集合与岗位的交集计算


            #region 仅按角色计算
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByStationOnly)
            {
                ps = new Paras();
                if (BP.Difference.SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
                {
                    //2020-4-25 按照角色倒序排序 修改原因队列模式时，下级角色处理后发给上级角色， 角色越高数值越小
                    sql = "SELECT A.FK_Emp FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND A.OrgNo=" + dbStr + "OrgNo AND B.FK_Node=" + dbStr + "FK_Node ORDER BY A.FK_Station desc";
                    ps.Add("OrgNo", WebUser.OrgNo);
                    ps.Add("FK_Node", town.HisNode.NodeID);
                    ps.SQL = sql;
                    dt = DBAccess.RunSQLReturnTable(ps);
                }
                else
                {
                    //2020-4-25 按照角色倒序排序 修改原因队列模式时，下级角色处理后发给上级角色， 角色越高数值越小
                    sql = "SELECT A.FK_Emp FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node ORDER BY A.FK_Station desc";
                    ps.Add("FK_Node", town.HisNode.NodeID);
                    ps.SQL = sql;
                    dt = DBAccess.RunSQLReturnTable(ps);
                }
                if (dt.Rows.Count > 0)
                    return dt;

                throw new Exception("@没有找到可接受的工作人员@节点访问规则错误:流程[" + town.HisNode.FlowName + "]节点[" + town.HisNode.NodeID + "," + town.HisNode.Name + "], 仅按角色计算，没有找到人员。");
            }
            #endregion

            #region 路由计算: 人员2人员,格式: @zhangsan,lisi@wang,zhaoliu 解释为: 张三发给李四，王五发给赵六.
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByFromEmpToEmp)
            {
                string[] fromto = town.HisNode.DeliveryParas.Split('@');
                string defUser = "";
                foreach (string str in fromto)
                {
                    string[] kv = str.Split(',');
                    if (kv[0].Equals("Defalut") == true)
                    {
                        defUser = kv[1];
                        continue;
                    }

                    //如果碰到了当前的人员.
                    if (kv[0].Equals(WebUser.No) == true)
                    {
                        string empTo = kv[1];
                        //BP.Port.Emp emp = new BP.Port.Emp(empTo);
                        DataRow dr = dt.NewRow();
                        dr[0] = empTo;
                        //  dr[1] = emp.Name;
                        dt.Rows.Add(dr);
                        return dt;
                    }
                }

                if (DataType.IsNullOrEmpty(defUser) == false)
                {
                    string empTo = defUser;
                    DataRow dr = dt.NewRow();
                    dr[0] = empTo;
                    dt.Rows.Add(dr);
                    return dt;
                }
                throw new Exception("@接收人规则是按照人员路由表设置的，但是系统管理员没有为您配置路由,当前节点;" + town.HisNode.Name);
            }
            #endregion


            #region 路由计算: 部门2人员,格式: @1001,lisi@1002,zhaoliu 解释为: 财务部发给李四，开发部发给赵六.
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByFromDeptToEmp)
            {
                string[] fromto = town.HisNode.DeliveryParas.Split('@');
                string defUser = "";
                foreach (string str in fromto)
                {
                    if (DataType.IsNullOrEmpty(str))
                        continue;
                    string tempStr = str.Replace("\n", "");
                    string[] kv = tempStr.Split(',');
                    if (kv[0].Equals("Defalut") == true)
                    {
                        defUser = kv[1];
                        continue;
                    }

                    //如果碰到了当前的人员部门.
                    if (kv[0].Equals(WebUser.DeptNo) == true)
                    {
                        string empTo = kv[1];
                        //BP.Port.Emp emp = new BP.Port.Emp(empTo);
                        DataRow dr = dt.NewRow();
                        dr[0] = empTo;
                        //  dr[1] = emp.Name;
                        dt.Rows.Add(dr);
                        return dt;
                    }
                }

                if (DataType.IsNullOrEmpty(defUser) == false)
                {
                    string empTo = defUser;
                    DataRow dr = dt.NewRow();
                    dr[0] = empTo;
                    dt.Rows.Add(dr);
                    return dt;
                }
                throw new Exception("@接收人规则是按照人员路由表设置的，但是系统管理员没有为您配置路由,当前节点;" + town.HisNode.Name);
            }
            #endregion

            #region 路由计算: 字段2人员,格式: @001,lisi@002,zhaoliu 解释为: 公章发给李四，财务章发给赵六.
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByFromAttrToEmp)
            {
                string attrKey = town.HisNode.GetParaString("RouteAttr2Emp");
                if (DataType.IsNullOrEmpty(attrKey) == true)
                    throw new Exception("err@节点[" + town.HisNode.Name + "]接收人规则配置[ByFromAttrToEmp]错误:没有配置字段.");

                if (this.currWn.rptGe.EnMap.Attrs.Contains(attrKey) == false)
                    throw new Exception("err@节点[" + town.HisNode.Name + "]接收人规则配置[ByFromAttrToEmp]错误:配置字段[" + attrKey + "],在表单里不存在.");

                string attrVal = this.currWn.rptGe.GetValStringByKey(attrKey);
                if (DataType.IsNullOrEmpty(attrVal) == true)
                    throw new Exception("err@节点[" + town.HisNode.Name + "]接收人规则配置[ByFromAttrToEmp]错误:配置字段[" + attrKey + "]值为空.");

                string[] fromto = town.HisNode.DeliveryParas.Split(',');
                string defUser = "";
                foreach (string str in fromto)
                {
                    string[] kv = str.Split(',');
                    if (kv[0].Equals("Defalut") == true)
                    {
                        defUser = kv[1];
                        continue;
                    }

                    if (kv[0].Equals(attrVal) == true)
                    {
                        string empTo = kv[1];
                        //BP.Port.Emp emp = new BP.Port.Emp(empTo);
                        DataRow dr = dt.NewRow();
                        dr[0] = empTo;
                        //  dr[1] = emp.Name;
                        dt.Rows.Add(dr);
                        return dt;
                    }
                }

                if (DataType.IsNullOrEmpty(defUser) == false)
                {
                    string empTo = defUser;
                    DataRow dr = dt.NewRow();
                    dr[0] = empTo;
                    dt.Rows.Add(dr);
                    return dt;
                }
                throw new Exception("@接收人规则是按照人员路由表设置的，但是系统管理员没有为您配置路由,当前节点;" + town.HisNode.Name);
            }
            #endregion

            #region 按照自定义的URL来计算 
            if (town.HisNode.HisDeliveryWay == DeliveryWay.BySelfUrl)
            {
                ps = new Paras();
                ps.Add("FK_Node", this.town.HisNode.NodeID);
                ps.Add("WorkID", this.currWn.HisWork.OID);
                ps.SQL = "SELECT FK_Emp FROM WF_SelectAccper WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND AccType=0 ORDER BY IDX";
                dt = DBAccess.RunSQLReturnTable(ps);
                if (dt.Rows.Count == 0)
                {
                    /*从上次发送设置的地方查询. */
                    SelectAccpers sas = new SelectAccpers();
                    int i = sas.QueryAccepterPriSetting(this.town.HisNode.NodeID);
                    if (i == 0 && town.HisNode.HisWhenNoWorker == false)
                    {
                        GenerWorkFlow gwf = new GenerWorkFlow(this.WorkID);
                        if (DataType.IsNullOrEmpty(toNode.DeliveryParas) == true)
                            throw new Exception("节点" + toNode.NodeID + "_" + toNode.Name + "设置的接收人规则是自定义的URL,现在未获取到设置的信息");
                        else
                            throw new Exception("BySelfUrl@" + toNode.DeliveryParas + "?FK_Flow=" + toNode.FlowNo + "&FK_Node=" + this.currWn.HisNode.NodeID + "&ToNode=" + toNode.NodeID + "&WorkID=" + this.WorkID + "&PWorkID=" + gwf.PWorkID + "&FID=" + gwf.FID);
                    }

                    //插入里面.
                    foreach (SelectAccper item in sas)
                    {
                        DataRow dr = dt.NewRow();
                        dr[0] = item.EmpNo;
                        dt.Rows.Add(dr);
                    }
                    return dt;
                }
                return dt;
            }
            #endregion 按照自定义的URL来计算

            #region 直接上级 for oppein.
            if ((int)town.HisNode.HisDeliveryWay == 501)
            {
                //配置变量.
                //string sqlSetting = "SELECT AR501DeptNo,AR501StationType FROM WF_Node WHERE NodeID="+town.HisNode.NodeID;
                //DataTable mydtSetting = DBAccess.RunSQLReturnTable(sqlSetting);
                //string AR501DeptNo = mydtSetting.Rows[0]["AR501DeptNo"].ToString();
                //string AR501StationType = mydtSetting.Rows[0]["AR501StationType"].ToString();

                string AR501DeptNo = town.HisNode.GetParaString("AR501DeptNo", "xxx");
                string AR501StationType = town.HisNode.GetParaString("AR501StationType", "xxx");
                string exp = town.HisNode.GetParaString("Exp501", "xxx");

                //处理表达式.
                string myExp = BP.WF.Glo.DealExp(exp, town.HisWork);
                //  Row row = this.currWn.rptGe.Row;

                //从表模板.
                MapDtls mapDtls = new MapDtls();
                mapDtls.Retrieve("FK_MapData", "ND" + currWn.HisNode.NodeID);

                //求从表数据:
                GEDtls dtls = new GEDtls("ND101Dtl1", this.WorkID);
                foreach (GEDtl item in dtls)
                {
                    Row rod = item.Row;
                }

                //系统变量.
                Int64 workid = this.WorkID;
                int nodeID = town.HisNode.NodeID;
                string userNo = WebUser.No;
            }
            #endregion 直接上级


            #region 按照设置的WebAPI接口获取的数据计算 - 新版本.
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByAPIUrl)
            {
                //获取对应的WebAPI;
                string nodeWebAPI = this.town.HisNode.GetParaString("NodeWebAPI");
                Hashtable htt = null;
                if (DataType.IsNullOrEmpty(nodeWebAPI) == true)
                {
                    throw new Exception("err@接受人规则按照WebAPI计算,请选择配置的WebAPI的字典");
                }
                if (this.town.HisNode.FormType == NodeFormType.RefOneFrmTree)
                {
                    GEEntity en = new GEEntity(this.town.HisNode.NodeFrmID, this.currWn.HisWork.OID);
                    htt = en.Row;
                }
                else
                    htt = this.currWn.rptGe.Row;
                htt["WorkID"] = this.WorkID;
                htt["OID"] = this.WorkID;
                htt["FrmID"] = this.town.HisNode.NodeFrmID;
                htt["NodeID"] = this.town.HisNode.NodeID;
                htt["FlowNo"] = this.town.HisNode.FlowNo;
                //增加GenerWorkFlow中AtPara的参数
                AtPara atPara = this.town.HisGenerWorkFlow.atPara;
                foreach (string key in atPara.HisHT.Keys)
                {
                    htt[key] = atPara.GetValStrByKey(key);
                }
                SFTable sfTable = new SFTable(nodeWebAPI);
                dt = sfTable.GenerHisDataTable(htt);
                /*//组织参数.
                string paras = "@WorkID=" + this.WorkID + "@OID=" + this.WorkID;

                Part part = new Part("AR" + this.town.HisNode.NodeID);
                string strs = part.ARWebApi(paras);
                if (strs.Equals("err@") == true)
                    throw new Exception(strs);

                strs = strs.Replace("，", ",");
                strs = strs.Replace(";", ",");
                strs = strs.Replace("；", ",");

                string[] mystars = strs.Split(',');

                foreach (string str in mystars)
                {
                    DataRow dr = dt.NewRow();
                    dr["No"] = str;
                    dt.Rows.Add(dr);
                }*/
                return dt;
            }
            #endregion 按照设置的WebAPI接口获取的数据计算

            #region 按照设置的WebAPI接口获取的数据计算(旧版本)
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByAPIUrl && 1 == 2)
            {
                //返回值
                string postData = "";
                //用户输入的webAPI地址
                string apiUrl = town.HisNode.DeliveryParas;
                if (apiUrl.Contains("@WebApiHost"))//可以替换配置文件中配置的webapi地址
                    apiUrl = apiUrl.Replace("@WebApiHost", BP.Difference.SystemConfig.AppSettings["WebApiHost"]);
                //如果有参数
                if (apiUrl.Contains("?"))
                {
                    //api接口地址
                    string apiHost = apiUrl.Split('?')[0];
                    //api参数
                    string apiParams = apiUrl.Split('?')[1];
                    //参数替换
                    apiParams = BP.WF.Glo.DealExp(apiParams, town.HisWork);
                    Hashtable bodyJson = new Hashtable();
                    if (apiParams.Contains("&"))
                    {
                        String[] bodyParams = apiParams.Split('&');
                        foreach (String item in bodyParams)
                        {
                            String[] keyVals = item.Split('=');
                            bodyJson.Add(keyVals[0], keyVals[1]);
                        }
                    }
                    else
                    {
                        String[] keyVals = apiParams.Split('=');
                        bodyJson.Add(keyVals[0], keyVals[1]);
                    }

                    //执行POST
                    postData = BP.Tools.PubGlo.HttpPostConnect(apiHost, BP.Tools.Json.ToJson(bodyJson), "POST", true);

                    if ((postData == "[]" || postData == "" || postData == null) && town.HisNode.HisWhenNoWorker == false)
                        throw new Exception("节点" + town.HisNode.NodeID + "_" + town.HisNode.Name + "设置的WebAPI接口返回的数据出错，请检查接口返回值。");

                    dt = BP.Tools.Json.ToDataTable(postData);
                    return dt;
                }
                else
                {//如果没有参数
                    postData = BP.Tools.PubGlo.HttpPostConnect(apiUrl, "", "GET");
                    if ((postData == "[]" || postData == "" || postData == null) && town.HisNode.HisWhenNoWorker == false)
                        throw new Exception("节点" + town.HisNode.NodeID + "_" + town.HisNode.Name + "设置的WebAPI接口返回的数据出错，请检查接口返回值。");

                    dt = BP.Tools.Json.ToDataTable(postData);
                    return dt;
                }
            }
            #endregion 按照设置的WebAPI接口获取的数据计算

            #region 按照组织模式人员选择器
            if (town.HisNode.HisDeliveryWay == DeliveryWay.BySelectedEmpsOrgModel)
            {
                ps = new Paras();
                ps.Add("FK_Node", this.town.HisNode.NodeID);
                ps.Add("WorkID", this.currWn.HisWork.OID);
                ps.SQL = "SELECT FK_Emp FROM WF_SelectAccper WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND AccType=0 ORDER BY IDX";
                dt = DBAccess.RunSQLReturnTable(ps);
                if (dt.Rows.Count == 0)
                {
                    /*从上次发送设置的地方查询. */
                    SelectAccpers sas = new SelectAccpers();
                    int i = sas.QueryAccepterPriSetting(this.town.HisNode.NodeID);
                    if (i == 0)
                    {
                        throw new Exception("url@./WorkOpt/AccepterOfOrg.htm?FK_Flow=" + toNode.FlowNo + "&FK_Node=" + this.currWn.HisNode.NodeID + "&ToNode=" + toNode.NodeID + "&WorkID=" + this.WorkID);
                    }

                    //插入里面.
                    foreach (SelectAccper item in sas)
                    {
                        DataRow dr = dt.NewRow();
                        dr[0] = item.EmpName;
                        dt.Rows.Add(dr);
                    }
                    return dt;
                }
                return dt;
            }
            #endregion 按照组织模式人员选择器
            #region 选择其他组织的联络员
            if (town.HisNode.HisDeliveryWay == DeliveryWay.BySelectEmpByOfficer)
            {
                ps = new Paras();
                ps.Add("FK_Node", this.town.HisNode.NodeID);
                ps.Add("WorkID", this.currWn.HisWork.OID);
                ps.SQL = "SELECT FK_Emp FROM WF_SelectAccper WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND AccType=0 ORDER BY IDX";
                dt = DBAccess.RunSQLReturnTable(ps);
                if (dt.Rows.Count == 0)
                {
                    /*从上次发送设置的地方查询. */
                    SelectAccpers sas = new SelectAccpers();
                    int i = sas.QueryAccepterPriSetting(this.town.HisNode.NodeID);
                    if (i == 0)
                    {
                        throw new Exception("url@./WorkOpt/AccepterOfOfficer.htm?FK_Flow=" + toNode.FlowNo + "&FK_Node=" + this.currWn.HisNode.NodeID + "&ToNode=" + toNode.NodeID + "&WorkID=" + this.WorkID);
                    }

                    //插入里面.
                    foreach (SelectAccper item in sas)
                    {
                        DataRow dr = dt.NewRow();
                        dr[0] = item.EmpName;
                        dt.Rows.Add(dr);
                    }
                    return dt;
                }
                return dt;
            }
            #endregion 选择其他组织的联络员

            #region 发送人的上级部门的负责人: 2022.2.20 benjing. by zhoupeng  
            if (town.HisNode.HisDeliveryWay == DeliveryWay.BySenderParentDeptLeader)
            {
                Dept dept = new Dept(WebUser.DeptNo);
                string deptNo = dept.ParentNo;
                sql = "SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + deptNo + "'";
                dt = DBAccess.RunSQLReturnTable(sql);
                string leaderNo = null;
                if (dt.Rows.Count == 1)
                {
                    leaderNo = dt.Rows[0][0] as string;
                    //如果领导是当前操作员，就让其找上一级的部门领导。
                    if (leaderNo != null && WebUser.No.Equals(leaderNo) == true)
                        leaderNo = null;
                }

                if (dt.Rows.Count == 0 || BP.DA.DataType.IsNullOrEmpty(leaderNo) == true)
                {
                    //如果没有找到,就到父节点去找.
                    BP.Port.Dept pDept = new BP.Port.Dept(deptNo);
                    sql = "SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + pDept.No + "'";
                    dt = DBAccess.RunSQLReturnTable(sql);
                    return dt;
                    // throw new Exception("err@按照 [发送人的上级部门的负责人] 计算接收人的时候出现错误，您没有维护部门[" + pDept.Name + "]的部门负责人.");
                }
                return dt;
            }
            #endregion 发送人的上级部门的负责人 2022.2.20 benjing.

            #region 发送人上级部门指定的角色 2022.2.20 beijing. by zhoupeng  
            if (town.HisNode.HisDeliveryWay == DeliveryWay.BySenderParentDeptStations)
            {
                //当前人员身份 sf
                Hashtable sf = GetEmpDeptBySFModel();
                empDept = sf["DeptNo"].ToString();
                empNo = sf["EmpNo"].ToString();

                BP.Port.Dept dept = new BP.Port.Dept(empDept);
                string deptNo = dept.ParentNo;

                sql = "SELECT A.FK_Emp,FK_Dept FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + town.HisNode.NodeID + " AND A.FK_Dept='" + deptNo + "'";
                dt = DBAccess.RunSQLReturnTable(sql);
                /*if (dt.Rows.Count == 0)
                {
                    BP.Port.Dept pDept = new BP.Port.Dept(deptNo);
                    throw new Exception("err@按照 [发送人上级部门指定的角色] 计算接收人的时候出现错误，没有找到人，请检查节点绑定的角色以及该部门【" + pDept.Name + "】下的人员设置的角色.");
                }*/
                return dt;
            }
            #endregion 发送人的上级部门的负责人 2022.2.20 beijing.  

            #region 连续多级主管 - 指定角色模式 模式分为两种情况，先实现审批终点是角色的情况,另外一种通讯录模式没有实现
            if (town.HisNode.HisDeliveryWay == DeliveryWay.ByMLeader0)
            {
                return this.ByMLeader0();

            }
            #endregion

            #region 最后判断 - 按照角色来执行。
            /* 如果执行节点 与 接受节点角色集合一致 */
            string currGroupStaNDs = this.currWn.HisNode.GroupStaNDs;
            string toNodeTeamStaNDs = town.HisNode.GroupStaNDs;

            if (DataType.IsNullOrEmpty(currGroupStaNDs) == false && currGroupStaNDs.Equals(toNodeTeamStaNDs) == true && this.currWn.HisNode.GetParaInt("ShenFenModel") == 0 && town.HisNode.GetParaInt("ShenFenModel") == 0)
            {
                /* 说明，就把当前人员做为下一个节点处理人。*/
                DataRow dr = dt.NewRow();
                if (dt.Columns.Count == 0)
                    dt.Columns.Add("No");

                dr[0] = WebUser.No;
                dt.Rows.Add(dr);
                return dt;
            }

            //获取当前人员信息的
            Hashtable ht = GetEmpDeptBySFModel();
            empDept = ht["DeptNo"].ToString();
            empNo = ht["EmpNo"].ToString();
            //按角色智能计算, 要判断切片模式,还是集合模式.
            ///#region 按角色智能计算, 切片模式. 需要对每个角色都要找到接受人，然后把这些接受人累加起来.
            if (toNode.DeliveryStationReqEmpsWay == 1 || toNode.DeliveryStationReqEmpsWay == 2)
            {
                DataTable staDT = DBAccess.RunSQLReturnTable("SELECT FK_Station From WF_NodeStation WHERE FK_Node=" + toNode.NodeID);
                string stas = "";
                foreach (DataRow dr in staDT.Rows)
                {
                    stas += dr[0] + ",";
                }
                string deptNo = empDept;
                string[] temps = stas.Split(",");
                foreach (string str in temps)
                {
                    if (DataType.IsNullOrEmpty(str))
                        continue;
                    //求一个角色下的人员.
                    DataTable mydt1 = WorkFlowBuessRole.FindWorker_GetEmpsByDeptAI(str, deptNo);

                    //如果是严谨模式.
                    if (toNode.DeliveryStationReqEmpsWay == 1 && mydt1.Rows.Count == 0)
                    {
                        Station st = new Station(str);
                        throw new Exception("@角色[" + st.Name + "]下，没有找到人不能发送下去，请检查组织结构是否完整。");
                    }

                    //累加.
                    foreach (DataRow dr in mydt1.Rows)
                    {
                        DataRow mydr = dt.NewRow();
                        mydr[0] = dr[0].ToString();
                        dt.Rows.Add(mydr);
                    }
                }
                return dt;
            }
            /* 如果执行节点 与 接受节点角色集合不一致 */
            if ((DataType.IsNullOrEmpty(toNodeTeamStaNDs) == true && DataType.IsNullOrEmpty(currGroupStaNDs) == true)
                || currGroupStaNDs.Equals(toNodeTeamStaNDs) == false)
            {
                /* 没有查询到的情况下, 先按照本部门计算。添加FK_Dept*/


                sql = "SELECT FK_Emp as No,FK_Dept FROM Port_DeptEmpStation A, WF_NodeStation B         WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node AND A.FK_Dept=" + dbStr + "FK_Dept";
                ps = new Paras();
                ps.SQL = sql;
                ps.Add("FK_Node", town.HisNode.NodeID);
                ps.Add("FK_Dept", empDept);

                dt = DBAccess.RunSQLReturnTable(ps);
                if (dt.Rows.Count == 0)
                {
                    NodeStations nextStations = town.HisNode.NodeStations;
                    if (nextStations.Count == 0)
                        throw new Exception("@没有找到可接受的工作人员@节点没有角色:" + town.HisNode.NodeID + "  " + town.HisNode.Name);
                }
                else
                {
                    bool isInit = false;
                    foreach (DataRow dr in dt.Rows)
                    {
                        if (dr[0].ToString() == WebUser.No)
                        {
                            /* 如果角色分组不一样，并且结果集合里还有当前的人员，就说明了出现了当前操作员，拥有本节点上的角色也拥有下一个节点的工作角色
                             导致：节点的分组不同，传递到同一个人身上。 */
                            isInit = true;
                        }
                    }
#warning edit by peng, 用来确定不同角色集合的传递包含同一个人的处理方式。
                    return dt;
                }
            }

            /*这里去掉了向下级别寻找的算法. */
            /* 没有查询到的情况下, 按照最大匹配数 提高一个级别计算，递归算法未完成。
             * 因为:以上已经做的角色的判断，就没有必要在判断其它类型的节点处理了。
             * */

            string nowDeptID = empDept.Clone() as string;

            //第1步:直线父级寻找.
            while (true)
            {
                BP.Port.Dept myDept = new BP.Port.Dept(nowDeptID);
                nowDeptID = myDept.ParentNo;
                if (nowDeptID == "-1" || nowDeptID.ToString() == "0")
                {
                    break; /*一直找到了最高级仍然没有发现，就跳出来循环从当前操作员人部门向下找。*/
                    throw new Exception("@按角色计算没有找到(" + town.HisNode.Name + ")接受人.");
                }

                //检查指定的父部门下面是否有该人员.
                DataTable mydtTemp = this.Func_GenerWorkerList_SpecDept(nowDeptID, empNo);
                if (mydtTemp.Rows.Count != 0)
                    return mydtTemp;

                continue;
            }

            //第2步：父级的平级.如果是0查找，1不查找父级的平级
            int StationFindWay = town.HisNode.GetParaInt("StationFindWay");
            if (StationFindWay == 0)
            {
                nowDeptID = empDept.Clone() as string;
                while (true)
                {
                    BP.Port.Dept myDept = new BP.Port.Dept(nowDeptID);
                    nowDeptID = myDept.ParentNo;
                    if (nowDeptID == "-1" || nowDeptID.ToString() == "0")
                    {
                        break; /*一直找到了最高级仍然没有发现，就跳出来循环从当前操作员人部门向下找。*/
                        throw new Exception("@按角色计算没有找到(" + town.HisNode.Name + ")接受人.");
                    }

                    //该部门下的所有子部门是否有人员.
                    DataTable mydtTemp = Func_GenerWorkerList_SpecDept_SameLevel(nowDeptID, empNo);
                    if (mydtTemp.Rows.Count != 0)
                        return mydtTemp;
                    continue;
                }
            }

            //向下级寻找.
            if (StationFindWay == 2)
            {
                nowDeptID = empDept.Clone() as string;

                //找下级1，2,3 级.
                Depts subDepts = new Depts();
                subDepts.Retrieve("ParentNo", nowDeptID); //1级子部门集合.
                foreach (Dept sbDept in subDepts)
                {
                    //该部门下的所有子部门是否有人员.
                    DataTable mydtTemp = Func_GenerWorkerList_SpecDept_SameLevel(sbDept.No, empNo);
                    if (mydtTemp.Rows.Count != 0)
                        return mydtTemp;

                    //找下级1，2级.
                    Depts subSubDepts = new Depts();
                    subSubDepts.Retrieve("ParentNo", sbDept.No);
                    foreach (Dept subSubDept in subSubDepts)  //2级子部门集合.
                    {
                        //该部门下的所有子部门是否有人员.
                        DataTable mydtTemp1 = Func_GenerWorkerList_SpecDept_SameLevel(subSubDept.No, empNo);
                        if (mydtTemp1.Rows.Count != 0)
                            return mydtTemp1;

                        //找下级1，2级.
                        Depts sub3Depts = new Depts();
                        sub3Depts.Retrieve("ParentNo", subSubDept.No);
                        foreach (Dept sub3Dept in sub3Depts)  //3级子部门集合.
                        {
                            //该部门下的所有子部门是否有人员.
                            DataTable mydtTemp2 = Func_GenerWorkerList_SpecDept_SameLevel(sub3Dept.No, empNo);
                            if (mydtTemp2.Rows.Count != 0)
                                return mydtTemp2;
                        }
                    }
                }
            }

            /*如果向上找没有找到，就考虑从本级部门上向下找。只找一级下级的平级 */
            nowDeptID = empDept.Clone() as string;

            //递归出来子部门下有该角色的人员 返回数据添加FK_Dept
            DataTable mydt = Func_GenerWorkerList_SpecDept_SameLevel(nowDeptID, empNo);

            if (mydt == null || mydt.Rows.Count == 0)
            {
                //如果递归没有找到人,就全局搜索角色.
                sql = "SELECT A.FK_Emp,FK_Dept FROM  Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node ORDER BY A.FK_Emp";
                ps = new Paras();
                ps.Add("FK_Node", town.HisNode.NodeID);
                ps.SQL = sql;
                dt = DBAccess.RunSQLReturnTable(ps);

                if (dt.Rows.Count > 0)
                    return dt;
                throw new Exception("@没有找到可接受的工作人员@按角色智能计算没有找到(" + town.HisNode.Name + ")接受人 @当前工作人员:" + WebUser.No + ",名称:" + WebUser.Name + " , 部门编号:" + WebUser.DeptNo + " 部门名称：" + WebUser.DeptName);
                /*
                                if (dt.Rows.Count == 0)
                                {
                                    mydt = new DataTable();
                                    mydt.Columns.Add(new DataColumn("No", typeof(string)));
                                    mydt.Columns.Add(new DataColumn("Name", typeof(string)));
                                }*/
            }

            return mydt;
            #endregion  按照角色来执行。
        }
        /// <summary>
        /// 根据部门编号获得
        /// </summary>
        /// <param name="dt"></param>
        /// <param name="nd"></param>
        /// <param name="deptNo"></param>
        /// <returns></returns>
        private DataTable ByDeptAndStationExt(DataTable dt, Node nd, string deptNo)
        {
            string sql = "SELECT A.No,A.Name FROM Port_Emp A, Port_DeptEmpStation B, WF_NodeStation C ";
            sql += " WHERE A.No=B.FK_Emp AND B.FK_Station=C.FK_Station AND C.FK_Node='" + nd.NodeID + "' AND B.FK_Dept='" + deptNo + "'";
            DataTable mydt = DBAccess.RunSQLReturnTable(sql);

            //合并数据.
            foreach (DataRow mydr in mydt.Rows)
            {
                string myNo = mydr[0].ToString();
                string myName = mydr[1].ToString();

                bool isHave = false;
                foreach (DataRow dr in dt.Rows)
                {
                    string no = dr[0].ToString();
                    if (myNo.Contains(no) == true)
                    {
                        isHave = true;
                        break;
                    }
                }
                if (isHave == true)
                    continue;
                DataRow dataRow = dt.NewRow();
                dataRow[0] = myNo;
                dt.Rows.Add(dataRow);
            }

            Depts depts = new Depts();
            depts.Retrieve("ParentNo", deptNo);
            foreach (Dept dept in depts)
            {
                dt = ByDeptAndStationExt(dt, nd, dept.No);
            }
            return dt;
        }

        private DataTable ByMLeader0()
        {
            DataTable dt = new DataTable();
            dt.Columns.Add(new DataColumn("No", typeof(string)));
            dt.Columns.Add(new DataColumn("Name", typeof(string)));

            Node toNode = this.town.HisNode;

            //如果是多人处理规则，必须配置队列模式，这里强制修改。
            if (toNode.TodolistModel != TodolistModel.Order)
            {
                toNode.TodolistModel = TodolistModel.Order;
                toNode.Update();
                // throw new Exception("err@节点["+toNode.Name+"]连续多级主管审批，配置的接收人规则必须是");
            }

            //获取当前节点绑定的岗位.
            NodeStations nds = new NodeStations();
            nds.Retrieve("FK_Node", toNode.NodeID);

            //假定身份是当前人员.
            string fromUserNo = WebUser.No;

            #region  判断0: 没有任何配置,就直接找他的领导,一直找到头
            if (toNode.GetParaInt("JiBieModel") == 0 && nds.Count == 0)
            {

                Emp emp = new Emp(fromUserNo);

                for (int i = 0; i < 100; i++)
                {
                    string leader = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Emp WHERE No='" + fromUserNo + "'");
                    //没有直属领导就到，部门领导去找.
                    if (DataType.IsNullOrEmpty(leader) == true)
                        leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + emp.DeptNo + "' AND B.Leader IS  Not null  ");

                    if (leader == null)
                    {
                        throw new Exception("err@组织结构配置不完善,没有为[" + emp.No + "," + emp.Name + "] 配置他的直属领导或部门领导.");
                    }

                    if (fromUserNo.Equals(leader)) break;

                    Emp empLeader = new Emp(leader);

                    //增加到里面.
                    DataRow dr = dt.NewRow();
                    dr[0] = empLeader.No;
                    dr[1] = empLeader.Name;
                    dt.Rows.Add(dr);
                    fromUserNo = leader;  //设置当前的人员.

                    //判断是否是最高部门？
                    Dept dept = new Dept(empLeader.DeptNo);
                    if (SystemConfig.CCBPMRunModel == CCBPMRunModel.Single)
                    {
                        if (dept.ParentNo.Equals("0") == true)
                            break;
                    }
                    else
                    {
                        if (dept.No.Equals(WebUser.OrgNo) == true)
                            break;
                    }
                }
            }
            #endregion 判断0: 没有任何配置,就直接找他的领导,一直找到头

            #region  判断1: 仅仅配置岗位的情况.
            if (toNode.GetParaInt("JiBieModel") == 0 && nds.Count != 0)
            {
                Emp emp = new Emp(fromUserNo);

                for (int i = 0; i < 100; i++)
                {
                    string leader = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Emp WHERE No='" + fromUserNo + "'");
                    //没有直属领导就到，部门领导去找.
                    if (DataType.IsNullOrEmpty(leader) == true)
                        leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + emp.DeptNo + "' AND B.Leader IS  Not null  ");

                    if (leader == null)
                    {
                        throw new Exception("err@组织结构配置不完善,没有为[" + emp.No + "," + emp.Name + "] 配置他的直属领导或部门领导.");
                    }

                    if (fromUserNo.Equals(leader)) break;

                    Emp empLeader = new Emp(leader);

                    //增加到里面.
                    DataRow dr = dt.NewRow();
                    dr[0] = empLeader.No;
                    dr[1] = empLeader.Name;
                    dt.Rows.Add(dr);
                    fromUserNo = leader;  //设置当前的人员.

                    //判断是否是最高部门？
                    Dept dept = new Dept(empLeader.DeptNo);
                    if (SystemConfig.CCBPMRunModel == CCBPMRunModel.Single)
                    {
                        if (dept.ParentNo.Equals("0") == true)
                            break;
                    }
                    else
                    {
                        if (dept.No.Equals(WebUser.OrgNo) == true)
                            break;
                    }


                    //判断当前人员是否拥有岗位？
                    string sql = "SELECT * FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND A.FK_Emp='" + leader + "' AND B.FK_Node=" + toNode.NodeID;
                    if (DBAccess.RunSQLReturnTable(sql).Rows.Count != 0)
                        break;
                }
            }
            #endregion 判断1: 仅仅配置岗位的情况

            #region  判断2: 仅仅配置级别的情况.
            if (toNode.GetParaInt("JiBieModel") == 1 && nds.Count == 0)
            {
                Emp emp = new Emp(fromUserNo);
                for (int i = 0; i <= toNode.GetParaInt("JiBieLevel"); i++)
                {
                    string leader = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Emp WHERE No='" + fromUserNo + "'");
                    //没有直属领导就到，部门领导去找.
                    if (DataType.IsNullOrEmpty(leader) == true)
                        leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + emp.DeptNo + "' AND B.Leader IS  Not null  ");

                    if (leader == null)
                    {
                        throw new Exception("err@组织结构配置不完善,没有为[" + emp.No + "," + emp.Name + "] 配置他的直属领导或部门领导.");
                    }

                    if (fromUserNo.Equals(leader)) break;

                    Emp empLeader = new Emp(leader);
                    //增加到里面.
                    DataRow dr = dt.NewRow();
                    dr[0] = empLeader.No;
                    dr[1] = empLeader.Name;
                    dt.Rows.Add(dr);
                    fromUserNo = leader;  //设置当前的人员.


                    //判断是否是最高部门？
                    Dept dept = new Dept(empLeader.DeptNo);
                    if (SystemConfig.CCBPMRunModel == CCBPMRunModel.Single)
                    {
                        if (dept.ParentNo.Equals("0") == true)
                            break;
                    }
                    else
                    {
                        if (dept.No.Equals(WebUser.OrgNo) == true)
                            break;
                    }
                }
            }
            #endregion 判断2: 仅仅配置级别的情况

            #region  判断3: 配置级别+岗位.
            if (toNode.GetParaInt("JiBieModel") == 1 && nds.Count != 0)
            {
                Emp emp = new Emp(fromUserNo);
                for (int i = 0; i <= toNode.GetParaInt("JiBieLevel"); i++)
                {
                    string leader = DBAccess.RunSQLReturnString("SELECT Leader FROM Port_Emp WHERE No='" + fromUserNo + "'");
                    //没有直属领导就到，部门领导去找.
                    if (DataType.IsNullOrEmpty(leader) == true)
                        leader = DBAccess.RunSQLReturnString("SELECT A.No,A.Name FROM Port_Emp A, Port_Dept B WHERE A.No=B.Leader AND B.No='" + emp.DeptNo + "' AND B.Leader IS  Not null  ");

                    if (leader == null)
                    {
                        throw new Exception("err@组织结构配置不完善,没有为[" + emp.No + "," + emp.Name + "] 配置他的直属领导或部门领导.");
                    }

                    if (fromUserNo.Equals(leader)) break;

                    Emp empLeader = new Emp(leader);
                    //增加到里面.
                    DataRow dr = dt.NewRow();
                    dr[0] = empLeader.No;
                    dr[1] = empLeader.Name;
                    dt.Rows.Add(dr);
                    fromUserNo = leader;  //设置当前的人员.


                    //判断是否是最高部门？
                    Dept dept = new Dept(empLeader.DeptNo);
                    if (SystemConfig.CCBPMRunModel == CCBPMRunModel.Single)
                    {
                        if (dept.ParentNo.Equals("0") == true)
                            break;
                    }
                    else
                    {
                        if (dept.No.Equals(WebUser.OrgNo) == true)
                            break;
                    }

                    //判断当前人员是否拥有岗位？
                    string sql = "SELECT * FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND A.FK_Emp='" + leader + "' AND B.FK_Node=" + toNode.NodeID;
                    if (DBAccess.RunSQLReturnTable(sql).Rows.Count != 0)
                        break;

                }
            }
            #endregion 判断2: 仅仅配置级别的情况

            return dt;
        }

        private Hashtable GetEmpDeptBySFModel()
        {
            Node nd = town.HisNode;
            Hashtable ht = new Hashtable();
            //身份模式.
            int sfModel = nd.GetParaInt("ShenFenModel");

            //身份参数.
            string sfVal = nd.GetParaString("ShenFenVal");

            //按照当前节点的身份计算
            if (sfModel == 0)
            {
                ht.Add("EmpNo", WebUser.No);
                ht.Add("DeptNo", WebUser.DeptNo);
                return ht;
            }
            //按照指定节点的身份计算.
            if (sfModel == 1)
            {
                if (DataType.IsNullOrEmpty(sfVal))
                    sfVal = currWn.HisNode.NodeID.ToString();

                Paras ps = new Paras();
                ps.SQL = "SELECT FK_Emp,FK_Dept FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node Order By RDT DESC";
                ps.Add("OID", this.WorkID);
                ps.Add("FK_Node", int.Parse(sfVal));

                DataTable dt = DBAccess.RunSQLReturnTable(ps);
                if (dt.Rows.Count == 0)
                {
                    //如果启用了计算未来处理人
                    if (currWn.HisNode.HisFlow.ItIsFullSA == true)
                    {
                        ps.SQL = "SELECT FK_Emp,FK_Dept FROM WF_SelectAccper WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "OID";
                        dt = DBAccess.RunSQLReturnTable(ps);
                        if (dt.Rows.Count == 0)
                        {
                            throw new Exception("err@未找到接收人，到达节点[" + this.town.HisNode.NodeID + "," + town.HisNode.Name + "]，" +
                                    "流程编号[" + currWn.HisNode.HisFlow.No + "]，WorkID[" + this.WorkID + "]，" +
                                    "身份模式[" + sfModel + "]，身份参数[" + sfVal + "]，请查询数据表WF_GenerWorkerlist、WF_SelectAccper");
                        }
                    }
                    else
                    {
                        throw new Exception("err@未找到接收人，到达节点[" + this.town.HisNode.NodeID + "," + town.HisNode.Name + "]，" +
                                "流程编号[" + currWn.HisNode.HisFlow.No + "]，WorkID[" + this.WorkID + "]，" +
                                "身份模式[" + sfModel + "]，身份参数[" + sfVal + "]，请查询数据表WF_GenerWorkerlist、WF_SelectAccper");
                    }
                }
                ht.Add("EmpNo", dt.Rows[0][0].ToString());
                ht.Add("DeptNo", dt.Rows[0][1].ToString());
            }

            //按照 字段的值的人员编号作为身份计算.
            if (sfModel == 2)
            {
                if (DataType.IsNullOrEmpty(sfVal) == true)
                    throw new Exception("err@流程模板配置错误，到达节点[" + this.town.HisNode.NodeID + "," + town.HisNode.Name + "]根据字段值作为人员编号，没有配置字段值:" + sfVal);
                //获得字段的值.
                string empNo = "";
                if (currWn.HisNode.HisFormType == NodeFormType.RefOneFrmTree)
                    empNo = currWn.HisWork.GetValStrByKey(sfVal);
                else
                    empNo = currWn.rptGe.GetValStrByKey(sfVal);
                BP.Port.Emp emp = new BP.Port.Emp();
                emp.UserID = empNo;
                if (emp.RetrieveFromDBSources() == 0)
                    throw new Exception("err@根据字段值:" + sfVal + "在Port_Emp中没有找到人员信息");
                ht.Add("EmpNo", emp.No);
                ht.Add("DeptNo", emp.DeptNo);
            }
            //按照字段的值作为部门编号
            if (sfModel == 3)
            {
                if (DataType.IsNullOrEmpty(sfVal) == true)
                    throw new Exception("err@流程模板配置错误，到达节点[" + this.town.HisNode.NodeID + "," + town.HisNode.Name + "]根据字段值作为人员编号，没有配置字段值:" + sfVal);
                //获得字段的值.
                String deptNo = "";
                if (currWn.HisNode.HisFormType == NodeFormType.RefOneFrmTree)
                    deptNo = currWn.HisWork.GetValStrByKey(sfVal);
                else
                    deptNo = currWn.rptGe.GetValStrByKey(sfVal);
                ht.Add("DeptNo", deptNo);
                ht.Add("EmpNo", WebUser.No);
            }
            //按照WF_GenerWorkFlow中AtPara中的字段值作为部门编号
            if (sfModel == 4)
            {
                if (DataType.IsNullOrEmpty(sfVal) == true)
                    throw new Exception("err@流程模板配置错误，到达节点[" + this.town.HisNode.NodeID + "," + town.HisNode.Name + "]根据字段值作为人员编号，没有配置字段值:" + sfVal);
                AtPara atPara = currWn.HisGenerWorkFlow.atPara;
                bool isHaveVal = false;
                foreach (string key in atPara.HisHT.Keys)
                {
                    if (DataType.IsNullOrEmpty(key))
                        continue;
                    if (key.Equals(sfVal) == true)
                    {
                        isHaveVal = true;
                        ht.Add("DeptNo", atPara.GetValStrByKey(key));
                        ht.Add("EmpNo", WebUser.No);
                        break;
                    }

                }
                if (isHaveVal == false)
                    throw new Exception("err@人员身份按系统参数(部门编号)作为人员身份获取错误，到达节点[" + this.town.HisNode.NodeID + "," + town.HisNode.Name + "]根据WF_GenerWorkFlow中AtPara没有获取到:" + sfVal + "的值");
            }
            return ht;
        }
        /// <summary>
        /// 找部门的领导
        /// </summary>
        /// <returns></returns>
        private DataTable ByDeptLeader()
        {
            string sqlDepts = this.town.HisNode.ARDeptModelDeptsSQL(this.town.WorkID); // 获得部门的sqls.
            DataTable dtDepts = DBAccess.RunSQLReturnTable(sqlDepts);

            DataTable dt = new DataTable();
            dt.Columns.Add("No");
            dt.Columns.Add("Name");

            string depts = "";
            foreach (DataRow dr in dtDepts.Rows)
            {
                depts += dr[0].ToString() + ",";
            }
            depts = BP.Port.Glo.GenerWhereInSQL(depts);

            string sql = "";
            if (SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
                sql = "SELECT a.UserID as No,a.Name FROM  Port_Emp a, Port_Dept b  WHERE a.UserID=b.Leader AND B.No in (" + depts + ") ";
            else
                sql = "SELECT a.No,a.Name FROM  Port_Emp a, Port_Dept b  WHERE a.No=b.Leader AND B.No in (" + depts + ") ";
            return DBAccess.RunSQLReturnTable(sql);
        }
        /// <summary>
        /// 按照分管领导计算 2025-05
        /// </summary>
        /// <returns></returns>
        /// <exception cref="Exception"></exception>
        private DataTable ByDeptSpecer()
        {
            string sqlDepts = this.town.HisNode.ARDeptModelDeptsSQL(this.town.WorkID); // 获得部门的sqls.
            DataTable dtDepts = DBAccess.RunSQLReturnTable(sqlDepts);

            DataTable dt = new DataTable();
            dt.Columns.Add("No");
            dt.Columns.Add("Name");

            string depts = "";
            foreach (DataRow dr in dtDepts.Rows)
            {
                depts += dr[0].ToString() + ",";
            }
            depts = BP.Port.Glo.GenerWhereInSQL(depts);

            string sql = "";
            if (SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
                sql = "SELECT a.UserID as No,a.Name FROM  Port_Emp a, Port_Dept b  WHERE a.UserID=b.Specer AND B.No IN (" + depts + ") ";
            else
                sql = "SELECT a.No,a.Name FROM  Port_Emp a, Port_Dept b  WHERE a.No=b.Specer AND B.No IN (" + depts + ") ";

            return DBAccess.RunSQLReturnTable(sql);
        }
        /// <summary>
        /// 找部门的分管领导
        /// </summary>
        /// <returns></returns>
        private DataTable ByDeptShipLeader()
        {

            Node nd = town.HisNode;

            //身份模式.
            int sfModel = nd.GetParaInt("ShenFenModel");

            //身份参数.
            string sfVal = nd.GetParaString("ShenFenVal");

            //按照当前节点的身份计算
            if (sfModel == 0)
                return ByDeptShipLeader_Nodes(currWn.HisNode.NodeID.ToString());

            //按照指定节点的身份计算.
            if (sfModel == 1)
                return ByDeptShipLeader_Nodes(sfVal);

            //按照 字段的值的人员编号作为身份计算.
            if (sfModel == 2)
            {
                //获得字段的值.
                string empNo = "";
                if (currWn.HisNode.HisFormType == NodeFormType.RefOneFrmTree)
                    empNo = currWn.HisWork.GetValStrByKey(sfVal);
                else
                    empNo = currWn.rptGe.GetValStrByKey(sfVal);
                BP.Port.Emp emp = new BP.Port.Emp();
                emp.UserID = empNo;
                if (emp.RetrieveFromDBSources() == 0)
                {
                    throw new Exception("err@根据字段值:" + sfVal + "在Port_Emp中没有找到人员信息");
                }
                return ByDeptShipLeader_Fields(emp.No, emp.DeptNo);
            }

            throw new Exception("err@没有判断的身份模式.");
        }
        private DataTable ByDeptLeader_Nodes(string nodes)
        {
            DataTable dt = null;
            //查找指定节点的人员， 如果没有节点，就是当前的节点.
            if (DataType.IsNullOrEmpty(nodes) == true)
                nodes = this.currWn.HisNode.NodeID.ToString();

            Paras ps = new Paras();
            ps.SQL = "SELECT FK_Emp,FK_Dept FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node Order By RDT DESC";
            ps.Add("OID", this.WorkID);
            ps.Add("FK_Node", int.Parse(nodes));

            dt = DBAccess.RunSQLReturnTable(ps);
            if (dt.Rows.Count == 0)
                throw new Exception("err@不符合常理，没有找到数据");
            string empNo = dt.Rows[0][0].ToString();
            string deptNo = dt.Rows[0][1].ToString();
            return ByDeptLeader_Fields(empNo, deptNo);
        }
        private DataTable ByDeptShipLeader_Nodes(string nodes)
        {
            DataTable dt = null;
            //查找指定节点的人员， 如果没有节点，就是当前的节点.
            if (DataType.IsNullOrEmpty(nodes) == true)
                nodes = this.currWn.HisNode.NodeID.ToString();

            Paras ps = new Paras();
            ps.SQL = "SELECT FK_Emp,FK_Dept FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node Order By RDT DESC";
            ps.Add("OID", this.WorkID);
            ps.Add("FK_Node", int.Parse(nodes));

            dt = DBAccess.RunSQLReturnTable(ps);
            if (dt.Rows.Count == 0)
                throw new Exception("err@不符合常理，没有找到数据");
            string empNo = dt.Rows[0][0].ToString();
            string deptNo = dt.Rows[0][1].ToString();
            return ByDeptShipLeader_Fields(empNo, deptNo);
        }
        private DataTable ByDeptLeader_Fields(string empNo, string empDept)
        {
            string sql = "SELECT Leader FROM Port_Dept WHERE No='" + empDept + "'";
            string myEmpNo = DBAccess.RunSQLReturnStringIsNull(sql, null);

            if (DataType.IsNullOrEmpty(myEmpNo) == true)
            {
                //如果部门的负责人为空，则查找Port_Emp中的Learder信息
                if (BP.Difference.SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
                    sql = "SELECT Leader FROM Port_Emp WHERE UserID='" + empNo + "' AND OrgNo='" + WebUser.OrgNo + "'";
                else
                    sql = "SELECT Leader FROM Port_Emp WHERE No='" + empNo + "'";

                myEmpNo = DBAccess.RunSQLReturnStringIsNull(sql, null);
                if (DataType.IsNullOrEmpty(myEmpNo) == true)
                {
                    Dept mydept = new Dept(empDept);
                    throw new Exception("@流程设计错误:下一个节点(" + town.HisNode.Name + ")设置的按照部门负责人计算，当前您的部门(" + mydept.No + "," + mydept.Name + ")没有维护负责人 . ");
                }
            }

            //如果有这个人,并且是当前人员，说明他本身就是经理或者部门负责人.
            if (myEmpNo.Equals(empNo) == true)
            {
                sql = "SELECT Leader FROM Port_Dept WHERE No=(SELECT PARENTNO FROM PORT_DEPT WHERE NO='" + empDept + "')";
                myEmpNo = DBAccess.RunSQLReturnStringIsNull(sql, null);
                if (DataType.IsNullOrEmpty(myEmpNo) == true)
                {
                    Dept mydept = new Dept(empDept);
                    throw new Exception("@流程设计错误:下一个节点(" + town.HisNode.Name + ")设置的按照部门负责人计算，当前您的部门(" + mydept.Name + ")上级没有维护负责人 . ");
                }
            }
            return DBAccess.RunSQLReturnTable(sql);
        }
        private DataTable ByDeptShipLeader_Fields(string empNo, string empDept)
        {
            BP.Port.Dept mydept = new BP.Port.Dept(empDept);
            Paras ps = new Paras();
            ps.Add("No", empDept);
            ps.SQL = "SELECT ShipLeader FROM Port_Dept WHERE No='" + empDept + "'";

            DataTable dt = DBAccess.RunSQLReturnTable(ps);
            if (dt.Rows.Count != 0 && dt.Rows[0][0] != null && DataType.IsNullOrEmpty(dt.Rows[0][0].ToString()) == true)
            {
                //如果部门的负责人为空，则查找Port_Emp中的Learder信息
                ps.Clear();
                if (BP.Difference.SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
                    ps.SQL = "SELECT ShipLeader FROM Port_Emp WHERE UserID='" + empNo + "' AND OrgNo='" + WebUser.OrgNo + "'";
                else
                    ps.SQL = "SELECT ShipLeader FROM Port_Emp WHERE No='" + empNo + "'";

                dt = DBAccess.RunSQLReturnTable(ps);
                if (dt.Rows.Count != 0 && dt.Rows[0][0] != null && DataType.IsNullOrEmpty(dt.Rows[0][0].ToString()) == true)
                    throw new Exception("@流程设计错误:下一个节点(" + town.HisNode.Name + ")设置的按照部门负责人计算，当前您的部门(" + mydept.No + "," + mydept.Name + ")没有维护负责人 . ");
            }

            //如果有这个人,并且是当前人员，说明他本身就是经理或者部门负责人.
            if (dt.Rows[0][0].ToString().Equals(empNo) == true)
            {
                ps.SQL = "SELECT ShipLeader FROM Port_Dept WHERE No=(SELECT PARENTNO FROM PORT_DEPT WHERE NO='" + empDept + "')";
                dt = DBAccess.RunSQLReturnTable(ps);
                if (dt.Rows.Count == 0)
                    throw new Exception("@流程设计错误:下一个节点(" + town.HisNode.Name + ")设置的按照部门负责人计算，当前您的部门(" + mydept.Name + ")上级没有维护负责人 . ");
            }
            return dt;
        }
        /// <summary>
        /// 获得指定部门下是否有该角色的人员.
        /// </summary>
        /// <param name="deptNo">部门编号</param>
        /// <param name="empNo">人员编号</param>
        /// <returns></returns>
        public DataTable Func_GenerWorkerList_SpecDept(string deptNo, string empNo)
        {
            string sql;

            Paras ps = new Paras();
            if (this.town.HisNode.ItIsExpSender == true)
            {
                /* 不允许包含当前处理人. */
                sql = "SELECT FK_Emp as No FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node AND A.FK_Dept=" + dbStr + "FK_Dept AND A.FK_Emp!=" + dbStr + "FK_Emp";

                ps.SQL = sql;
                ps.Add("FK_Node", town.HisNode.NodeID);
                ps.Add("FK_Dept", deptNo);
                ps.Add("FK_Emp", empNo);
            }
            else
            {
                sql = "SELECT FK_Emp as No FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node AND A.FK_Dept=" + dbStr + "FK_Dept";

                ps.SQL = sql;
                ps.Add("FK_Node", town.HisNode.NodeID);
                ps.Add("FK_Dept", deptNo);
            }

            DataTable dt = DBAccess.RunSQLReturnTable(ps);
            return dt;
        }
        /// <summary>
        /// 获得本部门的人员
        /// </summary>
        /// <param name="deptNo"></param>
        /// <param name="emp1"></param>
        /// <returns></returns>
        public DataTable Func_GenerWorkerList_SpecDept_SameLevel(string deptNo, string empNo)
        {
            string sql;

            Paras ps = new Paras();
            if (this.town.HisNode.ItIsExpSender == true)
            {
                /* 不允许包含当前处理人. */
                sql = "SELECT FK_Emp as No FROM Port_DeptEmpStation A, WF_NodeStation B, Port_Dept C WHERE A.FK_Dept=C.No AND A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node AND C.ParentNo=" + dbStr + "FK_Dept AND A.FK_Emp!=" + dbStr + "FK_Emp";

                ps.SQL = sql;
                ps.Add("FK_Node", town.HisNode.NodeID);
                ps.Add("FK_Dept", deptNo);
                ps.Add("FK_Emp", empNo);
            }
            else
            {
                sql = "SELECT FK_Emp as No FROM Port_DeptEmpStation A, WF_NodeStation B, Port_Dept C  WHERE A.FK_Dept=C.No AND A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node AND C.ParentNo=" + dbStr + "FK_Dept";
                ps.SQL = sql;
                ps.Add("FK_Node", town.HisNode.NodeID);
                ps.Add("FK_Dept", deptNo);
            }

            DataTable dt = DBAccess.RunSQLReturnTable(ps);
            return dt;
        }
        /// <summary>
        /// 执行找人
        /// </summary>
        /// <returns></returns>
        public DataTable DoIt(Flow fl, WorkNode currWn, WorkNode toWn, bool isFromFullSA = false)
        {
            // 给变量赋值.
            this.fl = fl;
            this.currWn = currWn;
            this.town = toWn;
            this.WorkID = currWn.WorkID;

            if (this.town.HisNode.ItIsGuestNode)
            {
                /*到达的节点是客户参与的节点. add by zhoupeng 2016.5.11*/
                DataTable mydt = new DataTable();
                mydt.Columns.Add("No", typeof(string));
                mydt.Columns.Add("Name", typeof(string));

                DataRow dr = mydt.NewRow();
                dr["No"] = "Guest";
                dr["Name"] = "外部用户";
                mydt.Rows.Add(dr);
                return mydt;
            }

            //如果到达的节点是按照workflow的模式。
            if (toWn.HisNode.HisDeliveryWay != DeliveryWay.ByCCFlowBPM)
            {
                DataTable re_dt = null;
                bool isOpenSelecter = toWn.HisNode.GetParaBoolen("IsOpenSelecter");
                try
                {
                    re_dt = this.FindByWorkFlowModel();
                }
                catch (Exception ex)
                {
                    string msg = ex.Message;
                    string backMsg = msg.Replace("@没有找到可接受的工作人员@", "@");
                    if (isFromFullSA)
                    {
                        if (msg.Contains("@没有找到可接受的工作人员") == true && town.HisNode.HisWhenNoWorker == true)
                        {
                            re_dt = new DataTable();
                            return re_dt;
                        }
                        throw new Exception(backMsg);
                    }


                    if (msg.Contains("@没有找到可接受的工作人员") == true)
                    {
                        if (isOpenSelecter == true)
                            throw new Exception("url@./WorkOpt/AccepterOfGener.htm?FK_Flow=" + toWn.HisNode.FlowNo + "&FK_Node=" + this.currWn.HisNode.NodeID + "&ToNode=" + toWn.HisNode.NodeID + "&WorkID=" + this.WorkID + "&PageName=AccepterOfGener");
                        if (town.HisNode.HisWhenNoWorker == true)
                        {
                            re_dt = new DataTable();
                            return re_dt;
                        }
                    }
                    throw new Exception(backMsg);
                }

                if (re_dt.Rows.Count == 1)
                    return re_dt; //如果只有一个人，就直接返回，就不处理了。

                #region 根据配置追加接收人 by dgq 2015.5.18

                string paras = this.town.HisNode.DeliveryParas;
                if (paras.Contains("@Spec"))
                {
                    //如果返回null ,则创建表
                    if (re_dt == null)
                    {
                        re_dt = new DataTable();
                        re_dt.Columns.Add("No", typeof(string));
                    }

                    //获取配置规则
                    string[] reWays = this.town.HisNode.DeliveryParas.Split('@');
                    foreach (string reWay in reWays)
                    {
                        if (DataType.IsNullOrEmpty(reWay))
                            continue;
                        string[] specItems = reWay.Split('=');
                        //配置规则错误
                        if (specItems.Length != 2)
                            continue;
                        //规则名称，SpecStations、SpecEmps
                        string specName = specItems[0];
                        //规则内容
                        string specContent = specItems[1];
                        switch (specName)
                        {
                            case "SpecStations"://按角色
                                string[] stations = specContent.Split(',');
                                foreach (string station in stations)
                                {
                                    if (DataType.IsNullOrEmpty(station))
                                        continue;

                                    //获取角色下的人员
                                    string sql = "";
                                    if (BP.Difference.SystemConfig.CCBPMRunModel == CCBPMRunModel.Single)
                                        sql = "SELECT FK_Emp FROM Port_DeptEmpStation WHERE FK_Station='" + station + "'";
                                    else
                                    {
                                        sql = "SELECT FK_Emp FROM Port_DeptEmpStation WHERE FK_Station='" + station + "' AND OrgNo='" + WebUser.OrgNo + "'";
                                    }

                                    DataTable dt_Emps = DBAccess.RunSQLReturnTable(sql);
                                    foreach (DataRow empRow in dt_Emps.Rows)
                                    {
                                        //排除为空编号
                                        if (empRow[0] == null || DataType.IsNullOrEmpty(empRow[0].ToString()))
                                            continue;

                                        DataRow dr = re_dt.NewRow();
                                        dr[0] = empRow[0];
                                        re_dt.Rows.Add(dr);
                                    }
                                }
                                break;
                            case "SpecEmps"://按人员编号
                                string[] myEmpStrs = specContent.Split(',');
                                foreach (string emp in myEmpStrs)
                                {
                                    //排除为空编号
                                    if (DataType.IsNullOrEmpty(emp))
                                        continue;

                                    DataRow dr = re_dt.NewRow();
                                    dr[0] = emp;
                                    re_dt.Rows.Add(dr);
                                }
                                break;
                        }
                    }
                }
                #endregion

                //本节点接收人不允许包含上一步发送人 。
                if (this.town.HisNode.ItIsExpSender == true && re_dt.Rows.Count >= 2)
                {
                    /*
                     * 排除了接受人分组的情况, 因为如果有了分组，就破坏了分组的结构了.
                     * 
                     */
                    //复制表结构
                    DataTable dt = re_dt.Clone();
                    foreach (DataRow row in re_dt.Rows)
                    {
                        //排除当前登录人
                        if (row[0].ToString() == WebUser.No)
                            continue;

                        DataRow dr = dt.NewRow();
                        dr[0] = row[0];

                        if (row.Table.Columns.Count == 2)
                            dr[1] = row[1];

                        dt.Rows.Add(dr);
                    }
                    return dt;
                }
                return re_dt;
            }

            //没有找到人的情况，就返回空.
            return null;
        }


    }
}
