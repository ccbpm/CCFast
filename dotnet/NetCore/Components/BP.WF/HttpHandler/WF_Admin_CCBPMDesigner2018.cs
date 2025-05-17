using System;
using System.Collections;
using BP.DA;
using BP.Difference;
using BP.Sys;
using BP.WF.Template;

namespace BP.WF.HttpHandler
{
    /// <summary>
    /// 初始化函数
    /// </summary>
    public class WF_Admin_CCBPMDesigner2018 : DirectoryPageBase
    {
        /// <summary>
        /// 构造函数
        /// </summary>
        public WF_Admin_CCBPMDesigner2018()
        {
        }

        #region 节点相关 Nodes
        public string CreateCCNode()
        {
            try
            {
                string x = this.GetRequestVal("X");
                string y = this.GetRequestVal("Y");
                string icon = this.GetRequestVal("icon");

                int iX = 20;
                int iY = 20;

                if (DataType.IsNullOrEmpty(x) == false)
                    iX = (int)double.Parse(x);

                if (DataType.IsNullOrEmpty(y) == false)
                    iY = (int)double.Parse(y);

                Node node = BP.WF.Template.TemplateGlo.NewEtcNode(this.FlowNo, iX, iY, NodeType.CCNode);

                Hashtable ht = new Hashtable();
                ht.Add("NodeID", node.NodeID);
                ht.Add("Name", node.Name);
                ht.Add("NodeType", 2); // 抄送节点.

                return BP.Tools.Json.ToJsonEntityModel(ht);
            }
            catch (Exception ex)
            {
                return "err@" + ex.Message;
            }
        }
        public string CreateSubFlowNode()
        {
            try
            {
                string x = this.GetRequestVal("X");
                string y = this.GetRequestVal("Y");
                string icon = this.GetRequestVal("icon");

                int iX = 20;
                int iY = 20;

                if (DataType.IsNullOrEmpty(x) == false)
                    iX = (int)double.Parse(x);

                if (DataType.IsNullOrEmpty(y) == false)
                    iY = (int)double.Parse(y);

                Node node = BP.WF.Template.TemplateGlo.NewEtcNode(this.FlowNo, iX, iY, NodeType.SubFlowNode);

                Hashtable ht = new Hashtable();
                ht.Add("NodeID", node.NodeID);
                ht.Add("Name", node.Name);
                ht.Add("NodeType", 3); // 子流程节点.

                return BP.Tools.Json.ToJsonEntityModel(ht);
            }
            catch (Exception ex)
            {
                return "err@" + ex.Message;
            }
        }
        /// <summary>
        /// 复制节点:
        /// 1. 把节点复制一个新的节点
        /// 2. 接收人规则复制
        /// </summary>
        /// <returns></returns>
        public string CopyNode()
        {
            Node node = new Node(this.NodeID);

            int idx = DBAccess.RunSQLReturnValInt("SELECT COUNT(NodeID) FROM WF_Node WHERE FK_Flow='" + this.FlowNo + "'", 0);
            string strID = this.FlowNo + idx.ToString().PadLeft(2, '0');
            int nodeIDTo = int.Parse(strID);
            //int nodeIDTo = this.GetRequestValInt("toNodeID");
            Node nodeTo = new Node(nodeIDTo);

            nodeTo.Copy(node);
            nodeTo.NodeID = nodeIDTo;
            nodeTo.HisDeliveryWay= node.HisDeliveryWay;

            //复制相关的属性: 岗位.
            NodeStations stations = new NodeStations();
            stations.Retrieve("FK_Node", this.NodeID);
            string staNos = "";
            string staNames = "";
            foreach (NodeStation item in stations)
            {
                staNos += item.StationNo + ",";
                staNames += item.StationT + ",";

                item.SetValByKey("FK_Node", nodeIDTo);
                item.Save();
            }

            staNos = staNos != "" ? staNos.Substring(0, staNos.LastIndexOf(",")) : staNos;
            staNames = staNames != "" ? staNames.Substring(0, staNames.LastIndexOf(",")) : staNames;

            //复制相关的属性: 部门.
            string deptNos = "";
            string deptNames = "";
            NodeDepts depts = new NodeDepts();
            depts.Retrieve("FK_Node", this.NodeID);
            foreach (NodeDept item in depts)
            {
                item.SetValByKey("FK_Node", nodeIDTo);
                item.Save();

                deptNos += item.DeptNo + ",";
                deptNames += item.DeptName + ",";
            }
            deptNos = deptNos != "" ? deptNos.Substring(0, deptNos.LastIndexOf(",")) : deptNos;
            deptNames = deptNames != "" ? deptNames.Substring(0, deptNames.LastIndexOf(",")) : deptNames;

            //复制相关的属性: 人员.
            NodeEmps emps = new NodeEmps();
            emps.Retrieve("FK_Node", this.NodeID);
            string empNos = "";
            string empNames = "";
            foreach (NodeEmp item in emps)
            {
                item.SetValByKey("FK_Node", nodeIDTo);
                item.Save();

                empNos += item.EmpNo + ",";
                empNames += item.EmpName + ",";
            }
            empNos = empNos != "" ? empNos.Substring(0, empNos.LastIndexOf(",")) : empNos;
            empNames = empNames != "" ? empNames.Substring(0, empNames.LastIndexOf(",")) : empNames;

            nodeTo.Update();

            //更新规则
            DBAccess.RunSQL("UPDATE WF_Node SET DeliveryWay='" + node.GetValIntByKey(NodeAttr.DeliveryWay) + "' WHERE NodeID=" + nodeIDTo);
            //更新岗位名称.
            DBAccess.RunSQL("UPDATE WF_Node SET NodeStations='" + staNos + "',NodeStationsT='" + staNames + "',NodeDepts='"+deptNos+ "',NodeDeptsT='"+deptNames+ "', NodeEmps='"+empNos+ "',NodeEmpsT='"+ empNames+ "' WHERE NodeID=" + nodeIDTo);
            return "执行成功.";
        }
        /// <summary>
        /// 创建流程节点并返回编号
        /// </summary>
        /// <returns></returns>
        public string CreateNode()
        {
            try
            {
                string x = this.GetRequestVal("X");
                string y = this.GetRequestVal("Y");
                string icon = this.GetRequestVal("icon");
                int nodeModel = this.GetRequestValInt("NodeModel");

                int iX = 20;
                int iY = 20;

                if (DataType.IsNullOrEmpty(x) == false)
                    iX = (int)double.Parse(x);

                if (DataType.IsNullOrEmpty(y) == false)
                    iY = (int)double.Parse(y);

                Node node = BP.WF.Template.TemplateGlo.NewNode(this.FlowNo, iX, iY, icon, nodeModel);


                Hashtable ht = new Hashtable();
                ht.Add("NodeID", node.NodeID);
                ht.Add("Name", node.Name);
                ht.Add("RunModel", (int)node.HisRunModel);
                ht.Add("NodeType", 0); //用户节点。

                return BP.Tools.Json.ToJsonEntityModel(ht);
            }
            catch (Exception ex)
            {
                return "err@" + ex.Message;
            }
        }
        /// <summary>
        /// 创建条件
        /// </summary>
        /// <returns></returns>
        public string CreateCond()
        {
            try
            {
                string x = this.GetRequestVal("X");
                string y = this.GetRequestVal("Y");
                string icon = this.GetRequestVal("icon");

                int iX = 20;
                int iY = 20;

                if (DataType.IsNullOrEmpty(x) == false)
                    iX = (int)double.Parse(x);

                if (DataType.IsNullOrEmpty(y) == false)
                    iY = (int)double.Parse(y);

                Node node = BP.WF.Template.TemplateGlo.NewEtcNode(this.FlowNo, iX, iY, NodeType.RouteNode);

                Hashtable ht = new Hashtable();
                ht.Add("NodeID", node.NodeID);
                ht.Add("Name", node.Name);
                ht.Add("NodeType", 1);

                return BP.Tools.Json.ToJsonEntityModel(ht);
            }
            catch (Exception ex)
            {
                return "err@" + ex.Message;
            }
        }
        /// <summary>
        /// 根据节点编号删除流程节点
        /// </summary>
        /// <returns>执行结果</returns>
        public string DeleteNode()
        {
            try
            {
                BP.WF.Node node = new BP.WF.Node();
                node.NodeID = this.NodeID;
                if (node.RetrieveFromDBSources() == 0)
                    return "err@删除失败,没有删除到数据，估计该节点已经别删除了.";

                if (node.ItIsStartNode == true)
                    return "err@开始节点不允许被删除。";

                node.Delete();
                return "删除成功.";
            }
            catch (Exception ex)
            {
                return "err@" + ex.Message;
            }
        }

        public string EditNodePosition()
        {
            try
            {
                string FK_Node = this.GetValFromFrmByKey("NodeID");
                string x = this.GetValFromFrmByKey("X");
                string y = this.GetValFromFrmByKey("Y");
                BP.WF.Node node = new BP.WF.Node();
                node.NodeID = int.Parse(FK_Node);
                int left = DataType.IsNullOrEmpty(x) ? 20 : int.Parse(x);
                int top = DataType.IsNullOrEmpty(x) ? 20 : int.Parse(y);
                if (left <= 0) left = 20;
                if (top <= 0) top = 20;

                int iResult = node.RetrieveFromDBSources();
                if (iResult > 0)
                {
                    node.X = left;
                    node.Y = top;
                    node.Update();
                    return "修改成功.";
                }

                return "err@修改节点失败，请确认该节点是否存在？";
            }
            catch (Exception ex)
            {
                return "err@" + ex.Message;
            }
        }
        /// <summary>
        /// 修改节点名称
        /// </summary>
        /// <returns></returns>
        public string Node_EditNodeName()
        {
            string FK_Node = this.GetValFromFrmByKey("NodeID");
            string NodeName = HttpContextHelper.UrlDecode(this.GetValFromFrmByKey("NodeName"));

            BP.WF.Node node = new BP.WF.Node();
            node.NodeID = int.Parse(FK_Node);
            int iResult = node.RetrieveFromDBSources();
            if (iResult > 0)
            {
                node.Name = NodeName;
                node.Update();
                return "@修改成功.";
            }

            return "err@修改节点失败，请确认该节点是否存在？";
        }
        #endregion end Node

        /// <summary>
        /// 删除连接线
        /// </summary>
        /// <returns></returns>
        public string Direction_Delete()
        {
            try
            {
                Directions di = new Directions();
                di.Retrieve(DirectionAttr.FK_Flow, this.FlowNo, DirectionAttr.Node, this.NodeID, DirectionAttr.ToNode, this.GetValFromFrmByKey("ToNode"));
                foreach (Direction direct in di)
                {
                    direct.Delete();
                }
                return "@删除成功！";
            }
            catch (Exception ex)
            {
                return "@err:" + ex.Message;
            }
        }
        public string Direction_Init()
        {
            try
            {
                string pk = this.FlowNo + "_" + this.NodeID + "_" + this.GetValFromFrmByKey("ToNode");

                Direction dir = new Direction();
                dir.setMyPK(pk);

                if (dir.RetrieveFromDBSources() > 0)
                {
                    return dir.Des;
                }

                return "";
            }
            catch (Exception ex)
            {
                return "@err:" + ex.Message;
            }
        }
        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public string Direction_Save()
        {
            try
            {
                string pk = this.FlowNo + "_" + this.NodeID + "_" + this.GetValFromFrmByKey("ToNode");

                Direction dir = new Direction();
                dir.setMyPK(pk);

                if (dir.RetrieveFromDBSources() > 0)
                {
                    dir.Des = this.GetValFromFrmByKey("Des");
                    dir.DirectUpdate();
                }
                return "@保存成功！";
            }
            catch (Exception ex)
            {
                return "@err:" + ex.Message;
            }
        }
        /// <summary>
        /// 添加标签
        /// </summary>
        /// <returns></returns>
        public string CreatLabNote()
        {
            try
            {
                LabNote lb = new LabNote();

                //获取当前流程已经存在的数量
                LabNotes labNotes = new LabNotes();
                int num = labNotes.Retrieve(LabNoteAttr.FK_Flow, this.FlowNo);

                string Name = this.GetValFromFrmByKey("LabName");
                int x = int.Parse(this.GetValFromFrmByKey("X"));
                int y = int.Parse(this.GetValFromFrmByKey("Y"));

                lb.setMyPK(this.FlowNo + "_" + x + "_" + y + "_" + (num + 1));
                lb.Name = Name;
                lb.FlowNo = this.FlowNo;
                lb.X = x;
                lb.Y = y;

                lb.DirectInsert();

                Hashtable ht = new Hashtable();
                ht.Add("MyPK", this.FlowNo + "_" + x + "_" + y + "_" + (num + 1));
                ht.Add("FK_Flow", this.FlowNo);

                return BP.Tools.Json.ToJsonEntityModel(ht);
            }
            catch (Exception ex)
            {
                return "@err:" + ex.Message;
            }
        }

        public void CheckBillFrm()
        {
            GEEntity en = new GEEntity(this.EnsName);
            en.CheckPhysicsTable();
        }
    }
}
