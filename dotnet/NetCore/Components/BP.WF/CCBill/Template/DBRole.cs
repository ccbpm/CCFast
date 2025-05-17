using BP.En;

namespace BP.CCBill.Template
{
	/// <summary>
	/// 数据权限属性
	/// </summary>
    public class DBRoleAttr:EntityMyPKAttr
    {
        #region 基本属性.
        /// <summary>
        /// 表单ID
        /// </summary>
        public const string FrmID = "FrmID";
        /// <summary>
        /// 子流程编号
        /// </summary>
        public const string DBRole = "DBRole";
        /// <summary>
        /// 标签
        /// </summary>
        public const string MarkID = "MarkID";
        /// <summary>
        /// 是否显示在表格右边
        /// </summary>
        public const string MarkName = "MarkName";

        public const string Docs = "Docs";
        public const string IsEnable = "IsEnable";

        /// <summary>
        /// Idx
        /// </summary>
        public const string Idx = "Idx";
        #endregion 基本属性.
    }
	/// <summary>
	/// 数据权限
	/// </summary>
    public class DBRole : EntityMyPK
    {
        #region 基本属性
        public string FrmID
        {
            get
            {
                return this.GetValStringByKey(DBRoleAttr.FrmID);
            }
            set
            {
                this.SetValByKey(DBRoleAttr.FrmID, value);
            }
        }
        public string DBRole1
        {
            get
            {
                return this.GetValStringByKey(DBRoleAttr.DBRole);
            }
            set
            {
                this.SetValByKey(DBRoleAttr.DBRole, value);
            }
        }
        public string MarkID
        {
            get
            {
                return this.GetValStringByKey(DBRoleAttr.MarkID);
            }
            set
            {
                this.SetValByKey(DBRoleAttr.MarkID, value);
            }
        }
        public string Docs
        {
            get
            {
                return this.GetValStringByKey(DBRoleAttr.Docs);
            }
            set
            {
                this.SetValByKey(DBRoleAttr.Docs, value);
            }
        }
        #endregion

        #region 构造方法
        /// <summary>
        /// 数据权限
        /// </summary>
        public DBRole()
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

                Map map = new Map("Frm_DBRole", "数据权限");

                map.AddMyPK();

                map.AddTBString(DBRoleAttr.FrmID, null, "表单ID", false, false, 0, 300, 10);
                map.AddTBString(DBRoleAttr.DBRole, null, "规则", false, false, 0, 300, 10);
                map.AddTBString(DBRoleAttr.MarkID, null, "权限标记", true, true, 0, 50, 10);
                map.AddTBString(DBRoleAttr.MarkName, null, "权限标记", true, true, 0, 50, 10);
                map.AddTBString(DBRoleAttr.Docs, null, "控制内容", true, true, 0, 50, 10);
                map.AddBoolean(DBRoleAttr.IsEnable, true, "IsEnable", true, true);
                map.AddTBAtParas();

                this._enMap = map;
                return this._enMap;
            }
        }
        #endregion
    }
    /// <summary>
    /// 数据权限
    /// </summary>
    public class DBRoles : EntitiesMyPK
    {
        /// <summary>
        /// 数据权限
        /// </summary>
        public DBRoles() { }
        /// <summary>
        /// 得到它的 Entity 
        /// </summary>
        public override Entity GetNewEntity
        {
            get
            {
                return new DBRole();
            }
        }
        #region 为了适应自动翻译成java的需要,把实体转换成List.
        /// <summary>
        /// 转化成 java list,C#不能调用.
        /// </summary>
        /// <returns>List</returns>
        public System.Collections.Generic.IList<DBRole> ToJavaList()
        {
            return (System.Collections.Generic.IList<DBRole>)this;
        }
        /// <summary>
        /// 转化成list
        /// </summary>
        /// <returns>List</returns>
        public System.Collections.Generic.List<DBRole> Tolist()
        {
            System.Collections.Generic.List<DBRole> list = new System.Collections.Generic.List<DBRole>();
            for (int i = 0; i < this.Count; i++)
            {
                list.Add((DBRole)this[i]);
            }
            return list;
        }
        #endregion 为了适应自动翻译成java的需要,把实体转换成List.
    }
}
