using System;
using System.Data;
using BP.DA;
using BP.En;
using BP.WF;
using BP.Port;

namespace BP.WF.Template
{
    /// <summary>
    /// 流程数据同步 属性
    /// </summary>
    public class SyncDataAttr : EntityMyPKAttr
    {
        ///流程编号
        public const string FlowNo = "FlowNo";
        //同步类型.
        public const string SyncType = "SyncType";
        //数据库链接URL
        public const string DBSrc = "DBSrc";
        //API链接URL
        public const string APIUrl = "APIUrl";
        //备注.
        public const string Note = "Note";
        //表
        public const string PTable = "PTable";
       
        //查询表
        public const string FrmID = "FrmID";
        //查询字段.
        public const string FrmName = "FrmName";
    }
    /// <summary>
    /// 流程数据同步
    /// </summary>
    public class SyncData : EntityMyPK
    {
        #region 属性
        /// <summary>
        /// 流程编号
        /// </summary>
        public string FlowNo
        {
            get
            {
                return this.GetValStringByKey(SyncDataAttr.FlowNo);
            }
            set
            {
                this.SetValByKey(SyncDataAttr.FlowNo, value);
            }
        }

        /// <summary>
        /// 字段
        /// </summary>
        public string FrmName
        {
            get
            {
                return this.GetValStringByKey(SyncDataAttr.FrmName);
            }
            set
            {
                this.SetValByKey(SyncDataAttr.FrmName, value);
            }
        }
        /// <summary>
        /// 表集合
        /// </summary>
        public string FrmID
        {
            get
            {
                return this.GetValStringByKey(SyncDataAttr.FrmID);
            }
            set
            {
                this.SetValByKey(SyncDataAttr.FrmID, value);
            }
        }
        /// <summary>
        /// 表
        /// </summary>
        public string PTable
        {
            get
            {
                return this.GetValStringByKey(SyncDataAttr.PTable);
            }
            set
            {
                this.SetValByKey(SyncDataAttr.PTable, value);
            }
        }
        public string TablePKType
        {
            get
            {
                return this.GetValStringByKey("TablePKType");
            }
            set
            {
                this.SetValByKey("TablePKType", value);
            }
        }
        public string TablePKName
        {
            get
            {
                return this.GetValStringByKey("TablePKName");
            }
            set
            {
                this.SetValByKey("TablePKName", value);
            }
        }
        public string DBSrcT
        {
            get
            {
                return this.GetValStringByKey("DBSrcT");
            }
            set
            {
                this.SetValByKey("DBSrcT", value);
            }
        }
        /// <summary>
        /// 备注
        /// </summary>
        public string Note
        {
            get
            {
                return this.GetValStringByKey(SyncDataAttr.Note);
            }
            set
            {
                this.SetValByKey(SyncDataAttr.Note, value);
            }
        }
        /// <summary>
        /// URL 
        /// </summary>
        public string APIUrl
        {
            get
            {
                return this.GetValStringByKey(SyncDataAttr.APIUrl);
            }
            set
            {
                this.SetValByKey(SyncDataAttr.APIUrl, value);
            }
        }
        /// <summary>
        /// 数据源
        /// </summary>
        public string DBSrc
        {
            get
            {
                return this.GetValStringByKey(SyncDataAttr.DBSrc);
            }
            set
            {
                this.SetValByKey(SyncDataAttr.DBSrc, value);
            }
        }
        /// <summary>
        /// 类型
        /// </summary>
        public string SyncType
        {
            get
            {
                return this.GetValStringByKey(SyncDataAttr.SyncType);
            }
            set
            {
                this.SetValByKey(SyncDataAttr.SyncType, value);
            }
        }

        #endregion

        #region 构造函数
        /// <summary>
        /// SyncData
        /// </summary>
        public SyncData()
        {
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
                Map map = new Map("WF_SyncData", "流程数据同步");

                map.AddMyPK();
                map.AddTBString(SyncDataAttr.FlowNo, null, "流程编号", false, false, 0, 10, 50, true);
                string val = "@DBSrc=按数据源同步@API=按API同步";
                map.AddDDLStringEnum(SyncDataAttr.SyncType, "DBSrc", "同步类型", val, true, "", false);

                map.AddTBString("Note", null, "备注/说明", false, false, 0, 300, 50, true);

                map.AddTBString("APIUrl", null, "APIUrl", false, false, 0, 300, 50, true);
                map.AddTBString("DBSrc", null, "数据源ID", false, false, 0, 300, 50, true);
                map.AddTBString("DBSrcT", null, "数据源", false, false, 0, 200, 50, true);

                map.AddTBString("PTable", null, "数据表", true, true, 0, 100, 50);
                map.AddTBString("PTableName", null, "表名", true, true, 0, 100, 50);

                map.AddTBString("TablePKName", null, "主键", true, true, 0, 300, 50);
                map.AddTBString("TablePKType", null, "主键类型", true, true, 0, 300, 50);

                map.AddTBString("FrmID", null, "源表单ID", true, true, 0, 300, 50);
                map.AddTBString("FrmName", null, "名称", true, true, 0, 300, 50);

                // map.AddTBString(SyncDataAttr.SQLTables, null, "查询表的集合SQL", false, false, 0, 300, 50);
                // map.AddTBString(SyncDataAttr.SQLFields, null, "查询表字段的SQL", false, false, 0, 300, 50);

                map.AddTBAtParas();
                this._enMap = map;
                return this._enMap;
            }
        }
        #endregion

        #region 公共方法.
        /// <summary>
        /// 同步数据库字段.
        /// </summary>
        /// <returns></returns>
        public string Init_DtlFields()
        {
            if (this.SyncType.Equals("DBSrc") == false)
                return "当前数据源不是DBSrc类型,.";

            if (DataType.IsNullOrEmpty(this.PTable) == false)
                return "err@请输入table名称.";

            SyncDataField fs = new SyncDataField();
            return "";
        }
        #endregion 公共方法.

    }
    /// <summary>
    /// 流程数据同步
    /// </summary>
    public class SyncDatas : EntitiesMyPK
    {
        #region 方法
        /// <summary>
        /// 得到它的 Entity 
        /// </summary>
        public override Entity GetNewEntity
        {
            get
            {
                return new SyncData();
            }
        }
        /// <summary>
        /// 流程数据同步
        /// </summary>
        public SyncDatas() { }

        public SyncDatas(string _No) {
            this.Retrieve(SyncDataAttr.FlowNo, _No);
        }
        #endregion

        #region 为了适应自动翻译成java的需要,把实体转换成List.
        /// <summary>
        /// 转化成 java list,C#不能调用.
        /// </summary>
        /// <returns>List</returns>
        public System.Collections.Generic.IList<SyncData> ToJavaList()
        {
            return (System.Collections.Generic.IList<SyncData>)this;
        }
        /// <summary>
        /// 转化成list
        /// </summary>
        /// <returns>List</returns>
        public System.Collections.Generic.List<SyncData> Tolist()
        {
            System.Collections.Generic.List<SyncData> list = new System.Collections.Generic.List<SyncData>();
            for (int i = 0; i < this.Count; i++)
            {
                list.Add((SyncData)this[i]);
            }
            return list;
        }
        #endregion 为了适应自动翻译成java的需要,把实体转换成List.
    }
}
