using BP.DA;
using BP.En;
using BP.Sys;


namespace BP.CCBill
{
    /// <summary>
    /// 实体表单
    /// </summary>
    public class FrmEntityNoName : EntityNoName
    {
        #region 权限控制.
        public override UAC HisUAC
        {
            get
            {
                UAC uac = new UAC();
                uac.OpenForAppAdmin();
                uac.IsDelete = false;
                uac.IsInsert = false;
                return uac;
            }
        }
        #endregion 权限控制.

        #region 属性
        /// <summary>
        /// 物理表
        /// </summary>
        public string PTable
        {
            get
            {
                string s = this.GetValStrByKey(MapDataAttr.PTable);
                if (DataType.IsNullOrEmpty(s) == true)
                    return this.No;
                return s;
            }
            set
            {
                this.SetValByKey(MapDataAttr.PTable, value);
            }
        }
        /// <summary>
        /// 实体类型：@0=单据@1=编号名称实体@2=树结构实体
        /// </summary>
        public EntityType EntityType
        {
            get
            {
                return (EntityType)this.GetValIntByKey("EntityType");
            }
            set
            {
                this.SetValByKey("EntityType", (int)value);
            }
        }
        /// <summary>
        /// 表单类型 (0=傻瓜，2=自由 ...)
        /// </summary>
        public FrmType FrmType
        {
            get
            {
                return (FrmType)this.GetValIntByKey(MapDataAttr.FrmType);
            }
            set
            {
                this.SetValByKey(MapDataAttr.FrmType, (int)value);
            }
        }
        /// <summary>
        /// 表单树
        /// </summary>
        public string FK_FormTree
        {
            get
            {
                return this.GetValStrByKey(MapDataAttr.FK_FormTree);
            }
            set
            {
                this.SetValByKey(MapDataAttr.FK_FormTree, value);
            }
        }
        /// <summary>
        /// 单据格式(流水号4)
        /// </summary>
        public string BillNoFormat
        {
            get
            {
                string str = this.GetValStrByKey("BillNoFormat");
                if (DataType.IsNullOrEmpty(str) == true)
                    str = "{LSH4}";
                return str;
            }
            set
            {
                this.SetValByKey("BillNoFormat", value);
            }
        }
        /// <summary>
        /// 单据编号生成规则
        /// </summary>
        public string TitleRole
        {
            get
            {
                string str = this.GetValStrByKey("TitleRole");
                if (DataType.IsNullOrEmpty(str) == true)
                    str = "@WebUser.FK_DeptName @WebUser.Name @RDT";
                return str;
            }
            set
            {
                this.SetValByKey("BillNoFormat", value);
            }
        }
        #endregion

        #region 构造方法
        /// <summary>
        /// 实体表单
        /// </summary>
        public FrmEntityNoName()
        {
        }
        /// <summary>
        /// 实体表单
        /// </summary>
        /// <param name="no">映射编号</param>
        public FrmEntityNoName(string no)
            : base(no)
        {
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
                Map map = new Map("Sys_MapData", "实体表单");
                map.CodeStruct = "4";

                #region 基本属性.
                map.AddGroupAttr("基本信息");
                map.AddTBStringPK(MapDataAttr.No, null, "表单编号", true, true, 1, 190, 20);
                map.SetHelperAlert(MapDataAttr.No, "也叫表单ID,系统唯一.");
                map.AddTBString(MapDataAttr.PTable, null, "存储表", true, false, 0, 500, 20, true);
                map.SetHelperAlert(MapDataAttr.PTable, "存储的表名,如果您修改一个不存在的系统将会自动创建一个表.");

                map.AddTBString(MapDataAttr.Name, null, "表单名称", true, false, 0, 200, 20, true);
                map.AddTBString("FK_FormTree", null, "表单类别", true, false, 0, 200, 20, true);

               // map.AddDDLEntities(MapDataAttr.FK_FormTree, "01", "表单类别", new SysFormTrees(), false);
                #endregion 基本属性.

                #region 实体表单.
                map.AddGroupAttr("实体表单");
                map.AddTBInt("EntityType", 0, "实体类型", false, false);

                map.AddTBString("BillNoFormat", null, "实体编号规则", true, false, 0, 100, 20, true);
                map.AddTBString("SortColumns", null, "排序字段", true, false, 0, 100, 20, true);
                map.AddTBString("ColorSet", null, "表格列颜色设置", true, false, 0, 100, 20, true);
                #endregion 实体表单.

                #region 设计者信息.
                map.AddGroupAttr("设计者信息");

                map.AddTBString(MapDataAttr.Designer, null, "设计者", true, false, 0, 500, 20);
                map.AddTBString(MapDataAttr.DesignerContact, null, "联系方式", true, false, 0, 500, 20);
                map.AddTBString(MapDataAttr.DesignerUnit, null, "单位", true, false, 0, 500, 20, true);
                map.AddTBString(MapDataAttr.GUID, null, "GUID", true, true, 0, 128, 20, false);
                map.AddTBString(MapDataAttr.Ver, null, "版本号", true, true, 0, 30, 20);
                map.AddTBStringDoc(MapDataAttr.Note, null, "备注", true, false, true);
                map.AddTBInt(MapDataAttr.Idx, 100, "顺序号", false, false);
                #endregion 设计者信息.

                this._enMap = map;
                return this._enMap;
            }
        }
        #endregion

        protected override void afterInsert()
        {
            base.afterInsertUpdateAction();
        }
        /// <summary>
        /// 检查enittyNoName类型的实体
        /// </summary>
        public void CheckEnityTypeAttrsFor_EntityNoName()
        {
            //取出来全部的属性.
            MapAttrs attrs = new MapAttrs(this.No);

            //创建表单ID。
            int groupID = DBAccess.RunSQLReturnValInt("SELECT MAX(GroupID) FROM Sys_MapAttr WHERE FK_MapData='" + this.No + "'", 0);
            if (groupID == 0)
            {
                GroupField gf = new GroupField();
                gf.Lab = "基本信息";
                gf.FrmID = this.No;
                gf.Insert();
                groupID = gf.OID;
            }

            int idx = 0;

            #region 补充上流程字段到 NDxxxRpt.
            if (attrs.Contains(this.No + "_No") == false)
            {
                /* 单据编号 */
                MapAttr attr = new MapAttr();
                attr.setFrmID(this.No);
                attr.setEditType(EditType.UnDel);
                attr.setKeyOfEn("No");
                attr.setName("编号"); //  单据编号
                attr.setMyDataType(DataType.AppString);
                attr.setUIContralType(UIContralType.TB);
                attr.setLGType(FieldTypeS.Normal);
                attr.setUIVisible(true);
                attr.setUIIsEnable(false);
                attr.setUIIsLine(false);
                attr.setMinLen(0);
                attr.setMaxLen(100);
                attr.setIdx(idx++);
                attr.setGroupID(groupID);
                attr.Insert();
            }

            if (attrs.Contains(this.No + "_Name") == false)
            {
                /* 名称 */
                MapAttr attr = new MapAttr();
                attr.setFrmID(this.No);
                attr.setEditType(EditType.UnDel);
                attr.setKeyOfEn("Name"); // "FlowEmps";
                attr.setName("名称"); //   单据模式， ccform的模式.
                attr.setMyDataType(DataType.AppString);
                attr.setUIContralType(UIContralType.TB);
                attr.setLGType(FieldTypeS.Normal);
                attr.setUIVisible(true);
                attr.setUIIsEnable(true);
                attr.UIIsLine = false;
                attr.setMinLen(0);
                attr.setMaxLen(400);
                attr.setIdx(-idx++);
                attr.setGroupID(groupID);
                attr.Insert();
            }
            if (attrs.Contains(this.No + "_EntityState") == false)
            {
                /* 单据状态 */
                MapAttr attr = new MapAttr();
                attr.setFrmID(this.No);
                attr.setEditType(EditType.UnDel);
                attr.setKeyOfEn("EntityState"); // "FlowEmps";
                attr.setName("实体状态"); //  
                attr.setMyDataType(DataType.AppInt);
                attr.setUIContralType(UIContralType.TB);
                attr.setUIContralType(UIContralType.TB);

                attr.setLGType(FieldTypeS.Normal);
                attr.setUIVisible(false);
                attr.setUIIsEnable(false);
                attr.UIIsLine = true;
                attr.setMinLen(0);
                attr.setMaxLen(10);
                attr.setIdx(-idx++);
                attr.setGroupID(groupID);
                attr.Insert();
            }

            if (attrs.Contains(this.No + "_RecNo") == false)
            {
                /* 发起人 */
                MapAttr attr = new MapAttr();
                attr.setFrmID(this.No);
                attr.setEditType(EditType.UnDel);
                attr.setKeyOfEn("RecNo");
                attr.setName("创建人"); //  
                attr.setMyDataType(DataType.AppString);
                attr.setUIContralType(UIContralType.TB);
                attr.setLGType(FieldTypeS.Normal);

                attr.setUIVisible(false);
                attr.setUIIsEnable(false);
                attr.setMinLen(0);
                attr.setMaxLen(100);
                attr.setIdx(-idx++);
                attr.setGroupID(groupID);
                attr.Insert();
            }
            if (attrs.Contains(this.No + "_RecName") == false)
            {
                /* 创建人名称 */
                MapAttr attr = new MapAttr();
                attr.setFrmID(this.No);
                attr.setEditType(EditType.UnDel);
                attr.setKeyOfEn("RecName");
                attr.setName("创建人名称"); //  
                attr.setMyDataType(DataType.AppString);
                attr.setUIContralType(UIContralType.TB);
                attr.setLGType(FieldTypeS.Normal);

                attr.setUIVisible(false);
                attr.setUIIsEnable(false);
                attr.setMinLen(0);
                attr.setMaxLen(32);
                attr.setIdx(idx++);
                attr.setGroupID(groupID);
                attr.Insert();
            }

            if (attrs.Contains(this.No + "_RDT") == false)
            {
                MapAttr attr = new MapAttr();
                attr.setFrmID(this.No);
                attr.setEditType(EditType.UnDel);
                attr.setKeyOfEn("RDT");
                attr.setName("创建时间");
                attr.setMyDataType(DataType.AppDateTime);
                attr.setUIContralType(UIContralType.TB);
                attr.setLGType(FieldTypeS.Normal);
                attr.setUIVisible(false);
                attr.setUIIsEnable(false);
                attr.UIIsLine = false;
                attr.setIdx(idx++);
                attr.setGroupID(groupID);
                attr.Insert();
            }
            if (attrs.Contains(this.No + "_DeptNo") == false)
            {
                /* 创建人部门 */
                MapAttr attr = new MapAttr();
                attr.setFrmID(this.No);
                attr.setEditType(EditType.UnDel);
                attr.setKeyOfEn("DeptNo");
                attr.setName("创建人部门"); //  
                attr.setMyDataType(DataType.AppString);
                attr.setUIContralType(UIContralType.TB);
                attr.setLGType(FieldTypeS.Normal);

                attr.setUIVisible(false);
                attr.setUIIsEnable(false);
                attr.setMinLen(0);
                attr.setMaxLen(100);
                attr.setIdx(-idx++);
                attr.setGroupID(groupID);
                attr.Insert();
            }
            if (attrs.Contains(this.No + "_OrgNo") == false)
            {
                /* 创建人名称 */
                MapAttr attr = new MapAttr();
                attr.setFrmID(this.No);
                attr.setEditType(EditType.UnDel);
                attr.setKeyOfEn("OrgNo");
                attr.setName("隶属组织"); //  
                attr.setMyDataType(DataType.AppString);
                attr.setUIContralType(UIContralType.TB);
                attr.setLGType(FieldTypeS.Normal);

                attr.setUIVisible(false);
                attr.setUIIsEnable(false);
                attr.setMinLen(0);
                attr.setMaxLen(100);
                attr.setIdx(idx++);
                attr.setGroupID(groupID);
                attr.Insert();
            }
            if (attrs.Contains(this.No + "_AtPara") == false)
            {
                /* 参数 */
                MapAttr attr = new MapAttr();
                attr.setFrmID(this.No);
                attr.setEditType(EditType.UnDel);
                attr.setKeyOfEn("AtPara");
                attr.setName("参数"); // 单据编号
                attr.setMyDataType(DataType.AppString);
                attr.setUIContralType(UIContralType.TB);
                attr.setLGType(FieldTypeS.Normal);
                attr.setUIVisible(false);
                attr.setUIIsEnable(false);
                attr.UIIsLine = false;
                attr.setMinLen(0);
                attr.setMaxLen(4000);
                attr.setIdx(idx++);
                attr.setGroupID(groupID);
                attr.Insert();
            }
            #endregion 补充上流程字段。

            #region 注册到外键表.
            SFTable sf = new SFTable();
            sf.SetValByKey("No", this.No);
            if (sf.IsExits == false)
            {
                sf.Name = this.Name;
                sf.SrcType = DictSrcType.SQL;
                sf.SrcTable = this.PTable;
                sf.ColumnValue = "No";
                sf.ColumnText = "Name";
                sf.SelectStatement = "SELECT No, Name FROM " + this.PTable;
                sf.SetPara("EnName", "TS.FrmUI.SFTableSQLNoName");
                sf.Insert();
            }
            #endregion 注册到外键表
        }
    }
    /// <summary>
    /// 实体表单s
    /// </summary>
    public class FrmEntityNoNames : EntitiesNoName
    {
        #region 构造
        /// <summary>
        /// 实体表单s
        /// </summary>
        public FrmEntityNoNames()
        {
        }
        /// <summary>
        /// 得到它的 Entity
        /// </summary>
        public override Entity GetNewEntity
        {
            get
            {
                return new FrmEntityNoName();
            }
        }
        #endregion

        #region 为了适应自动翻译成java的需要,把实体转换成List.
        /// <summary>
        /// 转化成 java list,C#不能调用.
        /// </summary>
        /// <returns>List</returns>
        public System.Collections.Generic.IList<FrmEntityNoName> ToJavaList()
        {
            return (System.Collections.Generic.IList<FrmEntityNoName>)this;
        }
        /// <summary>
        /// 转化成list
        /// </summary>
        /// <returns>List</returns>
        public System.Collections.Generic.List<FrmEntityNoName> Tolist()
        {
            System.Collections.Generic.List<FrmEntityNoName> list = new System.Collections.Generic.List<FrmEntityNoName>();
            for (int i = 0; i < this.Count; i++)
            {
                list.Add((FrmEntityNoName)this[i]);
            }
            return list;
        }
        #endregion 为了适应自动翻译成java的需要,把实体转换成List.
    }
}
