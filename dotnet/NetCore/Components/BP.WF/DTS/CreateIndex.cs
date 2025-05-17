using BP.DA;
using BP.En;
using System.Security.Policy;

namespace BP.WF.DTS
{
    /// <summary>
    /// 创建索引
    /// </summary>
    public class CreateIndex : Method
    {
        /// <summary>
        /// 创建索引
        /// </summary>
        public CreateIndex()
        {
            this.Title = "创建索引,优化效率.";
            this.Help = "创建索引字段,调高流程的运行效率.（流程,NDxxxTrack, NDxxRpt. 流程引擎表、组织结构表） 。";
        }
        /// <summary>
        /// 设置执行变量
        /// </summary>
        /// <returns></returns>
        public override void Init()
        {
        }
        /// <summary>
        /// 当前的操纵员是否可以执行这个方法
        /// </summary>
        public override bool IsCanDo
        {
            get
            {
                if (BP.Web.WebUser.No.Equals("admin") == true)
                    return true;
                return false;
            }
        }
        /// <summary>
        /// 执行
        /// </summary>
        /// <returns>返回执行结果</returns>
        public override object Do()
        {
            string info = "开始为Track表创建索引.";

            #region 组织表索引.
            DBAccess.CreatIndex("Port_Emp", "FK_Dept");
            DBAccess.CreatIndex("Port_Emp", "OrgNo");

            DBAccess.CreatIndex("Port_Dept", "ParentNo");
            DBAccess.CreatIndex("Port_Dept", "OrgNo");

            DBAccess.CreatIndex("Port_Station", "FK_StationType");

            DBAccess.CreatIndex("Port_DeptEmp", "FK_Dept");
            DBAccess.CreatIndex("Port_DeptEmp", "FK_Emp");

            DBAccess.CreatIndex("Port_DeptEmpStation", "FK_Emp");
            DBAccess.CreatIndex("Port_DeptEmpStation", "FK_Dept");
            DBAccess.CreatIndex("Port_DeptEmpStation", "FK_Station");

            DBAccess.CreatIndex("Port_DeptEmpStation", "FK_Dept,FK_Station");

            DBAccess.CreatIndex("Port_OrgAdminer", "OrgNo");
            DBAccess.CreatIndex("Port_OrgAdminer", "OrgNo,FK_Emp");
            #endregion 组织表索引.

            #region 流程引擎表.
          //  DBAccess.CreatIndex("WF_GenerWorkFlow", "TodoEmps");
           // DBAccess.CreatIndex("WF_GenerWorkFlow", "Emps");
            DBAccess.CreatIndex("WF_GenerWorkFlow", "Starter");
            DBAccess.CreatIndex("WF_GenerWorkFlow", "FID");

            DBAccess.CreatIndex("WF_GenerWorkerList", "WorkID");
            DBAccess.CreatIndex("WF_GenerWorkerList", "WorkID","FK_Node");
            DBAccess.CreatIndex("WF_GenerWorkerList", "WorkID","FK_Node","FK_Emp");

            DBAccess.CreatIndex("WF_CCList", "WorkID");
            DBAccess.CreatIndex("WF_CCList", "WorkID","NodeID");

            DBAccess.CreatIndex("WF_CCRole", "NodeID");

            DBAccess.CreatIndex("WF_CH", "WorkID");
            DBAccess.CreatIndex("WF_CH", "WorkID","FK_Emp");

            DBAccess.CreatIndex("WF_CHEval", "FK_Node");
            DBAccess.CreatIndex("WF_CHNode", "FK_Node");

            DBAccess.CreatIndex("WF_Cond", "ToNodeID");
            DBAccess.CreatIndex("WF_Cond", "FK_Node");

            DBAccess.CreatIndex("WF_Direction", "Node");
            DBAccess.CreatIndex("WF_Direction", "Node","ToNode");

            DBAccess.CreatIndex("WF_Flow", "FK_FlowSort");
            DBAccess.CreatIndex("WF_Node", "FK_Flow");

            DBAccess.CreatIndex("WF_FrmNode", "FK_Node");
            DBAccess.CreatIndex("WF_FrmNode", "FK_Frm");

            DBAccess.CreatIndex("WF_FrmNodeFieldRemove", "FrmID");
            DBAccess.CreatIndex("WF_FrmNodeFieldRemove", "FrmID","NodeID");
            DBAccess.CreatIndex("WF_FrmNodeFieldRemove", "NodeID");

            DBAccess.CreatIndex("WF_FrmOrg", "FrmID");
            DBAccess.CreatIndex("WF_LabNote", "FK_Flow");

            DBAccess.CreatIndex("WF_NodeDept", "FK_Node");
            DBAccess.CreatIndex("WF_NodeEmp", "FK_Node");

            DBAccess.CreatIndex("WF_NodeReturn", "FK_Node");
            DBAccess.CreatIndex("WF_NodeStation", "FK_Node");
            DBAccess.CreatIndex("WF_NodeSubFlow", "FK_Node");

            DBAccess.CreatIndex("WF_NodeTeam", "FK_Node");
            DBAccess.CreatIndex("WF_NodeToolbar", "FK_Node");

            DBAccess.CreatIndex("WF_Part", "NodeID");

            DBAccess.CreatIndex("WF_PushMsg", "NodeID");
            DBAccess.CreatIndex("WF_PushMsg", "FlowNo");

            DBAccess.CreatIndex("WF_Selectaccper", "WorkID");
            DBAccess.CreatIndex("WF_Selectaccper", "WorkID","FK_Node","FK_Emp");
            #endregion 流程引擎表.

            #region 系统表.
            DBAccess.CreatIndex(BP.Sys.Base.Glo.SysEnum(), "EnumKey");
            DBAccess.CreatIndex(BP.Sys.Base.Glo.SysEnum(), "EnumKey");

            DBAccess.CreatIndex("Sys_Frmattachment", "FK_MapData");
            DBAccess.CreatIndex("Sys_Frmattachment", "FK_Node");

            DBAccess.CreatIndex("Sys_FrmattachmentDB", "FK_MapData","NodeID","WorkID");
            DBAccess.CreatIndex("Sys_FrmattachmentDB", "FK_MapData","WorkID");

            DBAccess.CreatIndex("Sys_FrmEleDB", "FK_MapData","EleID","RefPKVal");

            DBAccess.CreatIndex("Sys_FrmEvent", "FK_Node");
            DBAccess.CreatIndex("Sys_FrmEvent", "FrmID");
            DBAccess.CreatIndex("Sys_FrmEvent", "RefPKVal");

            DBAccess.CreatIndex("Sys_FrmImg", "FK_MapData");
            DBAccess.CreatIndex("Sys_FrmprintTemplate", "NodeID","FrmID");
            DBAccess.CreatIndex("Sys_FrmprintTemplate", "NodeID");

            DBAccess.CreatIndex("Sys_FrmRB", "FK_MapData");
            DBAccess.CreatIndex("Sys_GroupField", "FrmID");
            DBAccess.CreatIndex("Sys_MapAttr", "FK_MapData");

            DBAccess.CreatIndex("Sys_MapAttr", "FK_MapData");
            DBAccess.CreatIndex("Sys_MapDtl", "FK_MapData");

            DBAccess.CreatIndex("Sys_MapExt", "FK_MapData");
            DBAccess.CreatIndex("Sys_SMS", "WorkID");
            DBAccess.CreatIndex("Sys_Userlogt", "EmpNo");

            DBAccess.CreatIndex("Sys_UserRegedit", "FK_Emp");
            DBAccess.CreatIndex("Sys_UserLogT", "EmpNo");
            #endregion 系统表.

            #region 单据系统.
            DBAccess.CreatIndex("Frm_GenerBill", "FrmID");
            DBAccess.CreatIndex("Frm_GroupMethod", "FrmID");
            DBAccess.CreatIndex("Frm_Track", "FrmID");

            DBAccess.CreatIndex("Frm_Track", "WorkID");
            DBAccess.CreatIndex("Frm_Track", "FrmID","WorkID");
            #endregion 系统表.

            #region 权限部分.
            DBAccess.CreatIndex("GPM_Menu", "FrmID");
            DBAccess.CreatIndex("GPM_PowerCenter", "CtrlPKVal");
            #endregion 权限部分.

            #region 轨迹表创建索引.
            Flows fls = new Flows();
            foreach (Flow fl in fls)
            {
                string table = "ND" + int.Parse(fl.No) + "Track";
                DBAccess.CreatIndex(table, "WorkID");
            }
            #endregion 轨迹表创建索引.
            return info;
        }
    }
}
