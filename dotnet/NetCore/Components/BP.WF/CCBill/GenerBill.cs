using System;
using System.ComponentModel.DataAnnotations;
using BP.En;
using BP.WF;


namespace BP.CCBill
{
    /// <summary>
    /// 实体状态
    /// </summary>
    public enum DictState
    {
        /// <summary>
        /// 空白
        /// </summary>
        None = 0,
        /// <summary>
        /// 草稿
        /// </summary>
        Draft = 1,
        /// <summary>
        /// 编辑中
        /// </summary>
        Editing = 2,
        /// <summary>
        /// 归档
        /// </summary>
        Filing = 3,
        /// <summary>
        /// 删除作废
        /// </summary>
        Delete = 4,

    }
    /// <summary>
    /// 单据状态
    /// </summary>
    public enum BillState
    {
        /// <summary>
        /// 空白(公用)
        /// </summary>
        Blank = 0,
        /// <summary>
        /// 草稿(公用)
        /// </summary>
        Draft = 1,
        /// <summary>
        /// 编辑中(公用)
        /// </summary>
        Editing = 2,
        /// <summary>
        /// 审核中(流程-单据)
        /// </summary>
        Checking = 3,
        /// <summary>
        /// 退回中(流程)
        /// </summary>
        ReturnSta = 5,
        /// <summary>
        /// 单据归档
        /// </summary>
        FrmOver = 100,
        /// <summary>
        /// 流程审核结束
        /// </summary>
        FlowOver = 200

    }
    /// <summary>
    /// 单据控制表 - Attr
    /// </summary>
    public class GenerBillAttr
    {
        #region 基本属性1
        /// <summary>
        /// 工作ID
        /// </summary>
        public const string WorkID = "WorkID";
        /// <summary>
        /// 表单ID
        /// </summary>
        public const string FrmID = "FrmID";
        /// <summary>
        /// 关联的单据号
        /// </summary>
        public const string FrmName = "FrmName";
        #endregion

        #region 基本属性
        /// <summary>
        /// TSpan
        /// </summary>
        public const string TSpan = "TSpan";
        /// <summary>
        /// 单据状态
        /// </summary>
        public const string BillState = "BillState";
        /// <summary>
        /// 标题
        /// </summary>
        public const string Title = "Title";
        /// <summary>
        /// 发起人
        /// </summary>
        public const string Starter = "Starter";
        /// <summary>
        /// 发起人名称
        /// </summary>
        public const string StarterName = "StarterName";
        /// <summary>
        /// 当前的步骤.
        /// </summary>
        public const string CurrIdx = "CurrIdx";
        /// <summary>
        /// 当前的审核人员编号s
        /// </summary>
        public const string CurrCheckerNos = "CurrCheckerNos";
        /// <summary>
        /// 当前的审核人名称.
        /// </summary>
        public const string CurrCheckerNames = "CurrCheckerNames";
        /// <summary>
        /// 产生时间
        /// </summary>
        public const string RDT = "RDT";
        /// <summary>
        /// 完成时间
        /// </summary>
        public const string CDT = "CDT";
        /// <summary>
        /// 当前步骤.
        /// </summary>
        public const string Idx = "Idx";
        /// <summary>
        /// 步骤名称
        /// </summary>
        public const string IdxName = "IdxName";
        /// <summary>
        /// 部门
        /// </summary>
        public const string DeptNo = "DeptNo";
        /// <summary>
        /// 部门名称
        /// </summary>
        public const string DeptName = "DeptName";
        /// <summary>
        /// 年月
        /// </summary>
        public const string FK_NY = "FK_NY";
        /// <summary>
        /// 单据ID
        /// </summary>
        public const string FID = "FID";
      
        /// <summary>
        /// 单据类别
        /// </summary>
        public const string FK_FrmTree = "FK_FrmTree";
        /// <summary>
        /// 优先级
        /// </summary>
        public const string PRI = "PRI";
        /// <summary>
        /// 父单据ID
        /// </summary>
        public const string PWorkID = "PWorkID";
        /// <summary>
        /// 父单据编号
        /// </summary>
        public const string PFrmID = "PFrmID";
        /// <summary>
        /// 父单据节点
        /// </summary>
        public const string PNodeID = "PNodeID";
        /// <summary>
        /// 子单据的调用人.
        /// </summary>
        public const string PEmp = "PEmp";
        /// <summary>
        /// 客户编号(对于客户发起的单据有效)
        /// </summary>
        public const string GuestNo = "GuestNo";
        /// <summary>
        /// 客户名称
        /// </summary>
        public const string GuestName = "GuestName";
        /// <summary>
        /// 单据编号
        /// </summary>
        public const string BillNo = "BillNo";
        /// <summary>
        /// 备注
        /// </summary>
        public const string FlowNote = "FlowNote";
        /// <summary>
        /// 待办人员
        /// </summary>
        public const string TodoEmps = "TodoEmps";
        /// <summary>
        /// 待办人员数量
        /// </summary>
        public const string TodoEmpsNum = "TodoEmpsNum";
        /// <summary>
        /// 任务状态
        /// </summary>
        public const string TaskSta = "TaskSta";
        /// <summary>
        /// 临时存放的参数
        /// </summary>
        public const string AtPara = "AtPara";
        /// <summary>
        /// 参与人
        /// </summary>
        public const string Emps = "Emps";
        /// <summary>
        /// 所有人员
        /// </summary>
        public const string EmpOfAll = "EmpOfAll";
        /// <summary>
        /// GUID
        /// </summary>
        public const string GUID = "GUID";

        public const string Sender = "Sender";
        public const string SendDT = "SendDT";
        /// <summary>
        /// 待办状态
        /// </summary>
        public const string TodoSta = "TodoSta";
        /// <summary>
        /// 节点应完成时间
        /// </summary>
        public const string SDTOfNode = "SDTOfNode";
        /// <summary>
        /// 单据应完成时间
        /// </summary>
        public const string SDTOfFlow = "SDTOfFlow";

        #endregion
    }
    /// <summary>
    /// 单据控制表
    /// </summary>
    public class GenerBill : Entity
    {
        #region 属性
        /// <summary>
        /// 主键
        /// </summary>
        public override string PK
        {
            get
            {
                return GenerBillAttr.WorkID;
            }
        }
        /// <summary>
        /// 备注
        /// </summary>
        public string FlowNote
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.FlowNote);
            }
            set
            {
                SetValByKey(GenerBillAttr.FlowNote, value);
            }
        }

        /// <summary>
        /// BillNo
        /// </summary>
        public string BillNo
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.BillNo);
            }
            set
            {
                SetValByKey(GenerBillAttr.BillNo, value);
            }
        }
        /// <summary>
        /// 单据ID
        /// </summary>
        public string FrmID
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.FrmID);
            }
            set
            {
                SetValByKey(GenerBillAttr.FrmID, value);
            }
        }
        /// <summary>
        /// 单据单据
        /// </summary>
        public string FrmName
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.FrmName);
            }
            set
            {
                SetValByKey(GenerBillAttr.FrmName, value);
            }
        }
        /// <summary>
        /// 优先级
        /// </summary>
        public int PRI
        {
            get
            {
                return this.GetValIntByKey(GenerBillAttr.PRI);
            }
            set
            {
                SetValByKey(GenerBillAttr.PRI, value);
            }
        }
        /// <summary>
        /// 待办人员数量
        /// </summary>
        public int TodoEmpsNum
        {
            get
            {
                return this.GetValIntByKey(GenerBillAttr.TodoEmpsNum);
            }
            set
            {
                SetValByKey(GenerBillAttr.TodoEmpsNum, value);
            }
        }
        /// <summary>
        /// 待办人员列表
        /// </summary>
        public string TodoEmps
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.TodoEmps);
            }
            set
            {
                SetValByKey(GenerBillAttr.TodoEmps, value);
            }
        }
        /// <summary>
        /// 参与人
        /// </summary>
        public string Emps
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.Emps);
            }
            set
            {
                SetValByKey(GenerBillAttr.Emps, value);
            }
        }
        /// <summary>
        /// 状态
        /// </summary>
        public TaskSta TaskSta
        {
            get
            {
                return (TaskSta)this.GetValIntByKey(GenerBillAttr.TaskSta);
            }
            set
            {
                SetValByKey(GenerBillAttr.TaskSta, (int)value);
            }
        }
        /// <summary>
        /// 类别编号
        /// </summary>
        public string FrmTreeNo
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.FK_FrmTree);
            }
            set
            {
                SetValByKey(GenerBillAttr.FK_FrmTree, value);
            }
        }
        /// <summary>
        /// 部门编号
        /// </summary>
        public string DeptNo
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.DeptNo);
            }
            set
            {
                SetValByKey(GenerBillAttr.DeptNo, value);
            }
        }
        public string DeptName
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.DeptName);
            }
            set
            {
                SetValByKey(GenerBillAttr.DeptName, value);
            }
        }
        /// <summary>
        /// 标题
        /// </summary>
        public string Title
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.Title);
            }
            set
            {
                SetValByKey(GenerBillAttr.Title, value);
            }
        }
        /// <summary>
        /// 客户编号
        /// </summary>
        public string GuestNo
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.GuestNo);
            }
            set
            {
                SetValByKey(GenerBillAttr.GuestNo, value);
            }
        }
        /// <summary>
        /// 客户名称
        /// </summary>
        public string GuestName
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.GuestName);
            }
            set
            {
                SetValByKey(GenerBillAttr.GuestName, value);
            }
        }
        /// <summary>
        /// 产生时间
        /// </summary>
        public string RDT
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.RDT);
            }
            set
            {
                SetValByKey(GenerBillAttr.RDT, value);
            }
        }
        /// <summary>
        /// 单据ID
        /// </summary>
        public Int64 WorkID
        {
            get
            {
                return this.GetValInt64ByKey(GenerBillAttr.WorkID);
            }
            set
            {
                SetValByKey(GenerBillAttr.WorkID, value);
            }
        }
        /// <summary>
        /// 主线程ID
        /// </summary>
        public Int64 FID
        {
            get
            {
                return this.GetValInt64ByKey(GenerBillAttr.FID);
            }
            set
            {
                SetValByKey(GenerBillAttr.FID, value);
            }
        }
        /// <summary>
        /// 父节点单据编号.
        /// </summary>
        public Int64 PWorkID
        {
            get
            {
                return this.GetValInt64ByKey(GenerBillAttr.PWorkID);
            }
            set
            {
                SetValByKey(GenerBillAttr.PWorkID, value);
            }
        }
        /// <summary>
        /// 父单据调用的节点
        /// </summary>
        public int PNodeID
        {
            get
            {
                return this.GetValIntByKey(GenerBillAttr.PNodeID);
            }
            set
            {
                SetValByKey(GenerBillAttr.PNodeID, value);
            }
        }
        /// <summary>
        /// PFrmID
        /// </summary>
        public string PFrmID
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.PFrmID);
            }
            set
            {
                SetValByKey(GenerBillAttr.PFrmID, value);
            }
        }
        /// <summary>
        /// 吊起子单据的人员
        /// </summary>
        public string PEmp
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.PEmp);
            }
            set
            {
                SetValByKey(GenerBillAttr.PEmp, value);
            }
        }
        /// <summary>
        /// 发起人
        /// </summary>
        public string Starter
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.Starter);
            }
            set
            {
                SetValByKey(GenerBillAttr.Starter, value);
            }
        }
        /// <summary>
        /// 发起人名称
        /// </summary>
        public string StarterName
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.StarterName);
            }
            set
            {
                this.SetValByKey(GenerBillAttr.StarterName, value);
            }
        }
        
        /// <summary>
        /// 当前节点名称
        /// </summary>
        public string IdxName
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.IdxName);
            }
            set
            {
                this.SetValByKey(GenerBillAttr.IdxName, value);
            }
        }
        /// <summary>
        /// 当前工作到的节点
        /// </summary>
        public int CurrIdx
        {
            get
            {
                return this.GetValIntByKey(GenerBillAttr.CurrIdx);
            }
            set
            {
                SetValByKey(GenerBillAttr.CurrIdx, value);
            }
        }
        public string CurrCheckerNos
        {
            get
            {
                return this.GetValStringByKey(GenerBillAttr.CurrCheckerNos);
            }
            set
            {
                SetValByKey(GenerBillAttr.CurrCheckerNos, value);
            }
        }
        public string CurrCheckerNames
        {
            get
            {
                return this.GetValStringByKey(GenerBillAttr.CurrCheckerNames);
            }
            set
            {
                SetValByKey(GenerBillAttr.CurrCheckerNames, value);
            }
        }
        /// <summary>
        /// 工作单据状态
        /// </summary>
        public BillState BillState
        {
            get
            {
                return (BillState)this.GetValIntByKey(GenerBillAttr.BillState);
            }
            set
            {
                //if (value == BillState.)
                //    SetValByKey(GenerBillAttr.BillSta, (int)BillSta.Complete);
                //else if (value == BP.WF.BillState.Delete)
                //    SetValByKey(GenerBillAttr.BillSta, (int)BillSta.Etc);
                //else
                //    SetValByKey(GenerBillAttr.BillSta, (int)BillSta.Runing);

                SetValByKey(GenerBillAttr.BillState, (int)value);
            }
        }
        /// <summary>
        /// 单据状态
        /// </summary>
        public string BillStateText
        {
            get
            {
                return this.GetValRefTextByKey(GenerBillAttr.BillState);
            }
        }
        /// <summary>
        /// GUID
        /// </summary>
        public string GUID
        {
            get
            {
                return this.GetValStrByKey(GenerBillAttr.GUID);
            }
            set
            {
                SetValByKey(GenerBillAttr.GUID, value);
            }
        }
        #endregion

        #region 权限控制.
        public override UAC HisUAC
        {
            get
            {
                UAC uac = new UAC();
                if (BP.Web.WebUser.No.Equals("admin")==true)
                {
                    uac.IsDelete = false;
                    uac.IsUpdate = true;
                    return uac;
                }
                uac.Readonly();
                return uac;
            }
        }
        #endregion 权限控制.

        #region 构造方法
        /// <summary>
        /// 单据控制表
        /// </summary>
        public GenerBill()
        {
        }
        /// <summary>
        /// 单据控制表
        /// </summary>
        /// <param name="workID">workID</param>
        public GenerBill(Int64 workid)
        {
            this.WorkID = workid;
            this.Retrieve();
        }
        /// <summary>
        /// EnMap
        /// </summary>
        public override Map EnMap
        {
            get
            {
                if (this._enMap != null)
                    return this._enMap;
                Map map = new Map("Frm_GenerBill", "单据控制表");

                map.AddTBIntPK(GenerBillAttr.WorkID, 0, "WorkID", true, true);

                map.AddTBString(GenerBillAttr.FK_FrmTree, null, "单据类别", true, false, 0, 100, 10);
                map.AddTBString(GenerBillAttr.FrmID, null, "单据ID", true, false, 0, 100, 10);
                map.AddTBString(GenerBillAttr.FrmName, null, "单据名称", true, false, 0, 200, 10);

                map.AddTBString(GenerBillAttr.BillNo, null, "单据编号", true, false, 0, 100, 10);
                map.AddTBString(GenerBillAttr.Title, null, "标题", true, false, 0, 1000, 10);
                //map.AddDDLSysEnum(GenerBillAttr.BillSta, 0, "状态(简)", true, false, GenerBillAttr.BillSta, "@0=运行中@1=已完成@2=其他");
                map.AddDDLSysEnum(GenerBillAttr.BillState, 0, "单据状态", true, false, GenerBillAttr.BillState, "@0=空白@1=草稿@2=编辑中@3=审核中@5=退回@100=归档");

                map.AddTBString(GenerBillAttr.Starter, null, "创建人", true, false, 0, 200, 10);
                map.AddTBString(GenerBillAttr.StarterName, null, "创建人名称", true, false, 0, 200, 10);
                map.AddTBString(GenerBillAttr.Sender, null, "发送人", true, false, 0, 200, 10);

                map.AddTBString(GenerBillAttr.CurrCheckerNos, null, "当前审核人编号s", true, false, 0, 200, 10);
                map.AddTBString(GenerBillAttr.CurrCheckerNames, null, "当前审核人名称s", true, false, 0, 200, 10);
                map.AddTBInt(GenerBillAttr.CurrIdx, 0, "当前审核人步骤", true, false);

                map.AddTBString("Msg", null, "信息", true, false, 0, 300, 10);

                map.AddTBDateTime(GenerBillAttr.RDT, "记录日期", true, true);
                map.AddTBDateTime(GenerBillAttr.SendDT, "单据活动时间", true, true);


                map.AddTBString(GenerBillAttr.DeptNo, null, "部门", true, false, 0, 100, 10);
                map.AddTBString(GenerBillAttr.DeptName, null, "部门名称", true, false, 0, 100, 10);
                map.AddTBInt(GenerBillAttr.PRI, 1, "优先级", true, true);

                map.AddTBDateTime(GenerBillAttr.SDTOfNode, "节点应完成时间", true, true);
                map.AddTBDateTime(GenerBillAttr.SDTOfFlow, "单据应完成时间", true, true);
                //父子单据信息.
                map.AddTBString(GenerBillAttr.PFrmID, null, "父单据编号", true, false, 0, 3, 10);
                map.AddTBInt(GenerBillAttr.PWorkID, 0, "父单据ID", true, true);
                map.AddDDLSysEnum(GenerBillAttr.TSpan, 0, "时间段", true, false, GenerBillAttr.TSpan, "@0=本周@1=上周@2=上上周@3=更早");
                 
                //参数.
                map.AddTBString(GenerBillAttr.AtPara, null, "参数(单据运行设置临时存储的参数)", true, false, 0, 2000, 10);
                map.AddTBString(GenerBillAttr.Emps, null, "参与人", true, false, 0, 4000, 10);
                map.AddTBString(GenerBillAttr.EmpOfAll, null, "所有人员", true, false, 0, 4000, 10);
                map.AddTBString(GenerBillAttr.GUID, null, "GUID", false, false, 0, 36, 10);
                map.AddTBString(GenerBillAttr.FK_NY, null, "年月", false, false, 0, 7, 7);

                //设置参数.
                map.AddTBAtParas(3000);
                this._enMap = map;
                return this._enMap;
            }
        }
        #endregion

        #region 方法操作.
        protected override bool beforeUpdateInsertAction()
        {
            this.SetValByKey(GenerBillAttr.SendDT, BP.DA.DataType.CurrentDateTime);
            return base.beforeUpdateInsertAction();
        }
        #endregion 方法操作.
    }
    /// <summary>
    /// 单据控制表s
    /// </summary>
    public class GenerBills : Entities
    {
        #region 构造
        /// <summary>
        /// 单据控制表s
        /// </summary>
        public GenerBills()
        {
        }
        /// <summary>
        /// 得到它的 Entity
        /// </summary>
        public override Entity GetNewEntity
        {
            get
            {
                return new GenerBill();
            }
        }
        #endregion

        #region 为了适应自动翻译成java的需要,把实体转换成List.
        /// <summary>
        /// 转化成 java list,C#不能调用.
        /// </summary>
        /// <returns>List</returns>
        public System.Collections.Generic.IList<GenerBill> ToJavaList()
        {
            return (System.Collections.Generic.IList<GenerBill>)this;
        }
        /// <summary>
        /// 转化成list
        /// </summary>
        /// <returns>List</returns>
        public System.Collections.Generic.List<GenerBill> Tolist()
        {
            System.Collections.Generic.List<GenerBill> list = new System.Collections.Generic.List<GenerBill>();
            for (int i = 0; i < this.Count; i++)
            {
                list.Add((GenerBill)this[i]);
            }
            return list;
        }
        #endregion 为了适应自动翻译成java的需要,把实体转换成List.
    }
}
