using System;
using BP.Difference;
using BP.En;
using BP.Web;
using BP.WF;


namespace BP.CCBill
{
    /// <summary>
    /// 状态
    /// </summary>
    public enum PassSta
    {
        /// <summary>
        /// 审核中
        /// </summary>
        Checking = 0,
        /// <summary>
        /// 未审核.
        /// </summary>
        UnPass=1,
        /// <summary>
        /// 审核通过
        /// </summary>
        Passed=2,
        /// <summary>
        /// 退回
        /// </summary>
        ReturnSta=3,
    }
    /// <summary>
    /// 单据控制表
    /// </summary>
    public class GenerWorker : EntityMyPK
    {
        #region 属性
        public PassSta PassSta
        {
            get
            {
                return (PassSta)this.GetValIntByKey("PassSta");
            }
            set
            {
                SetValByKey("PassSta", (int)value);
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
        /// 产生时间
        /// </summary>
        public string RDT
        {
            get
            {
                return this.GetValStrByKey("RDT");
            }
            set
            {
                SetValByKey("RDT", value);
            }
        }
        public string SendDT
        {
            get
            {
                return this.GetValStrByKey("SendDT");
            }
            set
            {
                SetValByKey("SendDT", value);
            }
        }
        /// <summary>
        /// 单据ID
        /// </summary>
        public Int64 WorkID
        {
            get
            {
                return this.GetValInt64ByKey("WorkID");
            }
            set
            {
                SetValByKey("WorkID", value);
            }
        }
        public string EmpNo
        {
            get
            {
                return this.GetValStrByKey("EmpNo");
            }
            set
            {
                SetValByKey("EmpNo", value);
            }
        }
        public string EmpName
        {
            get
            {
                return this.GetValStrByKey("EmpName");
            }
            set
            {
                SetValByKey("EmpName", value);
            }
        }
        public int Idx
        {
            get
            {
                return this.GetValIntByKey("Idx");
            }
            set
            {
                SetValByKey("Idx", value);
            }
        }
        /// <summary>
        /// 审核意见
        /// </summary>
        public string CheckerNote
        {
            get
            {
                return this.GetValStrByKey("CheckerNote");
            }
            set
            {
                SetValByKey("CheckerNote", value);
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
        public GenerWorker()
        {
        }
        /// <summary>
        /// 单据控制表
        /// </summary>
        /// <param name="workID">workID</param>
        public GenerWorker(string mypk)
        {
            this.setMyPK(mypk); 
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
                Map map = new Map("Frm_GenerWorker", "工作人员表");

                map.AddMyPK();
                map.AddTBInt("Idx", 0, "节点步骤", true, true);
                map.AddTBInt("WorkID", 0, "WorkID", true, true);
                map.AddTBString("EmpNo", null, "处理人编号", true, false, 0, 100, 10);
                map.AddTBString("EmpName", null, "处理人名称", true, false, 0, 100, 10);

                map.AddTBInt("PassSta", 0, "状态", true, false); //@0=待办@1=未开始@2=已通过@3=退回.
                map.AddTBDateTime("RDT",null, "记录日期", true, true); 
                map.AddTBDateTime("SendDT",null, "审核日期", true, true);

                map.AddTBString("DeptNo", null, "部门No", true, false, 0, 100, 10);
                map.AddTBString("DeptName", null, "部门名称", true, false, 0, 100, 10);
                map.AddTBString("FrmID", null, "表单ID", true, false, 0, 100, 10);

                map.AddTBString("CheckerNote", null, "审核意见", true, false, 0, 500, 10);

                //参数.
                map.AddTBString(GenerBillAttr.AtPara, null, "参数", true, false, 0, 2000, 10);

                map.AddTBString("OrgNo", null, "OrgNo", true, false, 0, 500, 10);
                this._enMap = map;
                return this._enMap;
            }
        }
        #endregion

        #region 方法操作.
        protected override bool beforeInsert()
        {
            if (SystemConfig.CCBPMRunModel != BP.Sys.CCBPMRunModel.Single)
                this.SetValByKey("OrgNo", WebUser.OrgNo);

            return base.beforeInsert();
        }
        #endregion 方法操作.
    }
    /// <summary>
    /// 单据控制表s
    /// </summary>
    public class GenerWorkers : EntitiesMyPK
    {
        #region 构造
        /// <summary>
        /// 单据控制表s
        /// </summary>
        public GenerWorkers()
        {
        }
        /// <summary>
        /// 得到它的 Entity
        /// </summary>
        public override Entity GetNewEntity
        {
            get
            {
                return new GenerWorker();
            }
        }
        #endregion

        #region 为了适应自动翻译成java的需要,把实体转换成List.
        /// <summary>
        /// 转化成 java list,C#不能调用.
        /// </summary>
        /// <returns>List</returns>
        public System.Collections.Generic.IList<GenerWorker> ToJavaList()
        {
            return (System.Collections.Generic.IList<GenerWorker>)this;
        }
        /// <summary>
        /// 转化成list
        /// </summary>
        /// <returns>List</returns>
        public System.Collections.Generic.List<GenerWorker> Tolist()
        {
            System.Collections.Generic.List<GenerWorker> list = new System.Collections.Generic.List<GenerWorker>();
            for (int i = 0; i < this.Count; i++)
            {
                list.Add((GenerWorker)this[i]);
            }
            return list;
        }
        #endregion 为了适应自动翻译成java的需要,把实体转换成List.
    }
}
