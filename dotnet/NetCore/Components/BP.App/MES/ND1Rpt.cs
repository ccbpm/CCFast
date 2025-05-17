using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using BP.WF.Data;
using BP.En;

namespace BP.MES
{
    /// <summary>
    /// 工单 Attr
    /// </summary>
    public class ND1RptAttr : NDXRptBaseAttr
    {
        #region 基本属性
        /// <summary>
        /// 请假人编号
        /// </summary>
        public const string QingJiaRenNo = "QingJiaRenNo";
        /// <summary>
        /// 请假人名称
        /// </summary>
        public const string QingJiaRenName = "QingJiaRenName";
        /// <summary>
        /// 部门编号
        /// </summary>
        public const string QingJiaRenDeptNo = "QingJiaRenDeptNo";
        /// <summary>
        /// 部门名称
        /// </summary>
        public const string QingJiaRenDeptName = "QingJiaRenDeptName";
        /// <summary>
        /// 请假天数
        /// </summary>
        public const string QingJiaTianShu = "QingJiaTianShu";
        /// <summary>
        /// 请假原因
        /// </summary>
        public const string QingJiaYuanYin = "QingJiaYuanYin";
        /// <summary>
        /// 部门审批意见
        /// </summary>
        public const string NoteBM = "NoteBM";
        /// <summary>
        /// 经理审批意见
        /// </summary>
        public const string NoteZJL = "NoteZJL";
        /// <summary>
        /// 人力资源意见
        /// </summary>
        public const string NoteRL = "NoteRL";
        #endregion
    }
    /// <summary>
    /// 工单
    /// </summary>
    public class ND1Rpt : NDXRptBase
    {
        #region 属性
        /// <summary>
        /// 请假人部门名称
        /// </summary>
        public string QingJiaRenDeptName
        {
            get
            {
                return this.GetValStringByKey(ND1RptAttr.QingJiaRenDeptName);
            }
            set
            {
                this.SetValByKey(ND1RptAttr.QingJiaRenDeptName, value);
            }
        }
        /// <summary>
        /// 请假人编号
        /// </summary>
        public string QingJiaRenNo
        {
            get
            {
                return this.GetValStringByKey(ND1RptAttr.QingJiaRenNo);
            }
            set
            {
                this.SetValByKey(ND1RptAttr.QingJiaRenNo, value);
            }
        }
        /// <summary>
        /// 请假人名称
        /// </summary>
        public string QingJiaRenName
        {
            get
            {
                return this.GetValStringByKey(ND1RptAttr.QingJiaRenName);
            }
            set
            {
                this.SetValByKey(ND1RptAttr.QingJiaRenName, value);
            }
        }
        /// <summary>
        /// 请假人部门编号
        /// </summary>
        public string QingJiaRenDeptNo
        {
            get
            {
                return this.GetValStringByKey(ND1RptAttr.QingJiaRenDeptNo);
            }
            set
            {
                this.SetValByKey(ND1RptAttr.QingJiaRenDeptNo, value);
            }
        }
        /// <summary>
        /// 请假原因
        /// </summary>
        public string QingJiaYuanYin
        {
            get
            {
                return this.GetValStringByKey(ND1RptAttr.QingJiaYuanYin);
            }
            set
            {
                this.SetValByKey(ND1RptAttr.QingJiaYuanYin, value);
            }
        }
        /// <summary>
        /// 请假天数
        /// </summary>
        public float QingJiaTianShu
        {
            get
            {
                return this.GetValIntByKey(ND1RptAttr.QingJiaTianShu);
            }
            set
            {
                this.SetValByKey(ND1RptAttr.QingJiaTianShu, value);
            }
        }
        /// <summary>
        /// 部门审批意见
        /// </summary>
        public string NoteBM
        {
            get
            {
                return this.GetValStringByKey(ND1RptAttr.NoteBM);
            }
            set
            {
                this.SetValByKey(ND1RptAttr.NoteBM, value);
            }
        }
        /// <summary>
        /// 总经理意见
        /// </summary>
        public string NoteZJL
        {
            get
            {
                return this.GetValStringByKey(ND1RptAttr.NoteZJL);
            }
            set
            {
                this.SetValByKey(ND1RptAttr.NoteZJL, value);
            }
        }
        /// <summary>
        /// 人力资源意见
        /// </summary>
        public string NoteRL
        {
            get
            {
                return this.GetValStringByKey(ND1RptAttr.NoteRL);
            }
            set
            {
                this.SetValByKey(ND1RptAttr.NoteRL, value);
            }
        }
        #endregion

        #region 构造函数
        /// <summary>
        /// 请假
        /// </summary>
        public ND1Rpt()
        { 
            
        }
        /// <summary>
        /// 请假
        /// </summary>
        /// <param name="workid">工作ID</param>
        public ND1Rpt(Int64 workid)
        {
            this.OID = workid;
            this.Retrieve();
        }
        /// <summary>
        /// 重写基类方法
        /// </summary>
        public override Map EnMap
        {
            get
            {
                if (this._enMap != null)
                    return this._enMap;

                Map map = new Map("ND18Rpt", "工单");

                #region 流程的基本字段
                map.AddTBIntPKOID();
                map.AddTBString(ND1RptAttr.Title, null, "标题", false, true, 0, 500, 10);
                map.AddTBString(ND1RptAttr.FK_Dept, null, "隶属部门", false, true, 0, 50, 10);
                map.AddTBString(ND1RptAttr.FK_NY, null, "年月", false, true, 0, 50, 10);
                map.AddDDLSysEnum(ND1RptAttr.WFState, 0, "状态", false, true, "WFState");

                map.AddTBInt(ND1RptAttr.FID, 0, "FID", false, true);
                map.AddTBInt(ND1RptAttr.FlowDaySpan, 0, "跨度", false, true);
                map.AddTBInt(ND1RptAttr.FlowEndNode, 0, "结束点", false, true);

                map.AddTBString(ND1RptAttr.FlowEmps, null, "参与人", false, true, 0, 50, 10);
                map.AddTBString(ND1RptAttr.FlowEnder, null, "最后节点处理人", false, true, 0, 50, 10);
                map.AddTBString(ND1RptAttr.FlowEnderRDT, null, "最后处理时间", false, true, 0, 50, 10);
                map.AddTBString(ND1RptAttr.FlowStarter, null, "流程发起人", false, true, 0, 50, 10);
                map.AddTBString(ND1RptAttr.FlowStartRDT, null, "流程发起时间", false, true, 0, 50, 10);
                map.AddTBString(ND1RptAttr.GuestNo, null, "客户编号", false, true, 0, 50, 10);
                map.AddTBString(ND1RptAttr.GuestName, null, "客户名称", false, true, 0, 50, 10);

                map.AddTBString(ND1RptAttr.PFlowNo, null, "父流程编号", false, true, 0, 50, 10);
                map.AddTBInt(ND1RptAttr.PWorkID, 0, "父流程ID", false, true);
                map.AddTBString(ND1RptAttr.BillNo, null, "单据编号", false, true, 0, 100, 10);
                #endregion 流程的基本字段


                #region 业务字段 - 申请单部分.
                map.AddTBString(ND1RptAttr.QingJiaRenNo, null, "请假人编号", false, false, 0, 200, 10);
                map.AddTBString(ND1RptAttr.QingJiaRenName, null, "请假人名称", true, false, 0, 200, 70);
                map.AddTBString(ND1RptAttr.QingJiaRenDeptNo, "", "请假人部门编号", true, false, 0, 200, 50);
                map.AddTBString(ND1RptAttr.QingJiaRenDeptName, null, "请假人部门名称", true, false, 0, 200, 50);
                map.AddTBString(ND1RptAttr.QingJiaYuanYin, null, "请假原因", true, false, 0, 200, 150);
                map.AddTBFloat(ND1RptAttr.QingJiaTianShu, 0, "请假天数", true, false);
                #endregion 业务字段 - 申请单部分.


                // 审核信息.
                map.AddTBString(ND1RptAttr.NoteBM, null, "部门经理意见", true, false, 0, 200, 150);
                map.AddTBString(ND1RptAttr.NoteZJL, null, "总经理意见", true, false, 0, 200, 150);
                map.AddTBString(ND1RptAttr.NoteRL, null, "人力资源意见", true, false, 0, 200, 150);

                this._enMap = map;
                return this._enMap;
            }
        }
        #endregion
    }
    /// <summary>
    /// 工单s
    /// </summary>
    public class ND1Rpts : NDXRptBases
    { 
        #region 方法
        /// <summary>
        /// 得到它的 Entity 
        /// </summary>
        public override Entity GetNewEntity
        {
            get
            {
                return new ND1Rpt();
            }
        }
        /// <summary>
        /// 请假s
        /// </summary>
        public ND1Rpts() { }
        #endregion
    }
}
