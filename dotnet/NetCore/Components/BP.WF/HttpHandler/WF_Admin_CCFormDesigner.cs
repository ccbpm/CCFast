using System;
using System.Data;
using BP.Web;
using BP.Sys;
using BP.DA;
using BP.En;
using BP.CCBill;
using BP.Tools;
using Org.BouncyCastle.Asn1.Esf;
using BP.Difference;

namespace BP.WF.HttpHandler
{
    public class WF_Admin_CCFormDesigner : BP.WF.HttpHandler.DirectoryPageBase
    {



        #region 执行父类的重写方法.
        /// <summary>
        /// 构造函数
        /// </summary>
        public WF_Admin_CCFormDesigner()
        {
        }

        /// <summary>
        /// 创建枚举类型字段
        /// </summary>
        /// <returns></returns>
        public string FrmEnumeration_NewEnumField()
        {
            UIContralType ctrl = UIContralType.RadioBtn;
            string ctrlDoType = GetRequestVal("ctrlDoType");
            if (ctrlDoType == "DDL")
                ctrl = UIContralType.DDL;
            else
                ctrl = UIContralType.RadioBtn;

            string fk_mapdata = this.GetRequestVal("FK_MapData");
            string keyOfEn = this.GetRequestVal("KeyOfEn");
            string fieldDesc = this.GetRequestVal("Name");
            string enumKeyOfBind = this.GetRequestVal("UIBindKey"); //要绑定的enumKey.

            BP.Sys.CCFormAPI.NewEnumField(fk_mapdata, keyOfEn, fieldDesc, enumKeyOfBind, ctrl);
            return "绑定成功.";
        }
        /// <summary>
        /// 创建外键字段.
        /// </summary>
        /// <returns></returns>
        public string NewSFTableField()
        {
            try
            {
                string fk_mapdata = this.GetRequestVal("FK_MapData");
                string keyOfEn = this.GetRequestVal("KeyOfEn");
                string fieldDesc = this.GetRequestVal("Name");
                string sftable = this.GetRequestVal("UIBindKey");
                float x = float.Parse(this.GetRequestVal("x"));
                float y = float.Parse(this.GetRequestVal("y"));

                //调用接口,执行保存.
                BP.Sys.CCFormAPI.SaveFieldSFTable(fk_mapdata, keyOfEn, fieldDesc, sftable, x, y);
                return "设置成功";
            }
            catch (Exception ex)
            {
                return "err@" + ex.Message;
            }
        }

        /// <summary>
        /// 转换拼音
        /// </summary>
        /// <returns></returns>
        public string ParseStringToPinyin()
        {
            string name = GetRequestVal("name");
            string flag = GetRequestVal("flag");
            //暂时没发现此方法在哪里有调用，edited by liuxc,2017-9-25
            return BP.Sys.CCFormAPI.ParseStringToPinyinField(name, Equals(flag, "true"), true, 20);
        }

        /// <summary>
        /// 创建隐藏字段.
        /// </summary>
        /// <returns></returns>
        public string NewHidF()
        {
            MapAttr mdHid = new MapAttr();
            mdHid.setMyPK(this.FrmID + "_" + this.KeyOfEn);
            mdHid.setFrmID(this.FrmID);
            mdHid.setKeyOfEn(this.KeyOfEn);
            mdHid.Name = this.Name;
            mdHid.MyDataType = int.Parse(this.GetRequestVal("FieldType"));
            mdHid.setEditType(EditType.Edit);
            mdHid.setMaxLen(100);
            mdHid.setMinLen(0);
            mdHid.setLGType(FieldTypeS.Normal);
            mdHid.setUIVisible(false);
            mdHid.setUIIsEnable(false);
            mdHid.Insert();

            return "创建成功..";
        }
        /// <summary>
        /// 默认执行的方法
        /// </summary>
        /// <returns></returns>
        protected override string DoDefaultMethod()
        {
            string sql = "";
            //返回值
            try
            {
                switch (this.DoType)
                {
                    default:
                        throw new Exception("没有判断的执行标记:" + this.DoType);
                }
            }
            catch (Exception ex)
            {
                return "err@" + this.ToString() + " msg:" + ex.Message;
            }
        }
        #endregion 执行父类的重写方法.

        #region 创建表单.
        public string NewFrmGuide_GenerPinYin()
        {
            string isQuanPin = this.GetRequestVal("IsQuanPin");
            string name = this.GetRequestVal("TB_Name");


            //表单No长度最大100，因有前缀CCFrm_，因此此处设置最大94，added by liuxc,2017-9-25
            string str = BP.Sys.CCFormAPI.ParseStringToPinyinField(name, Equals(isQuanPin, "1"), true, 94);

            MapData md = new MapData();
            md.No = str;
            if (BP.Difference.SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
                md.No = str + "_" + WebUser.OrgNo;
            if (md.RetrieveFromDBSources() == 0)
                return str;

            return "err@表单ID:" + str + "已经被使用.";
        }
        /// <summary>
        /// 获得系统的表
        /// </summary>
        /// <returns></returns>
        public string NewFrmGuide_Init()
        {
            DataSet ds = new DataSet();

            SFDBSrc src = new SFDBSrc("local");
            ds.Tables.Add(src.ToDataTableField("SFDBSrc"));

            DataTable tables = src.GetTables(true);
            tables.TableName = "Tables";
            ds.Tables.Add(tables);
            return BP.Tools.Json.ToJson(ds);

        }
        /// <summary>
        /// 创建一个DBList.
        /// </summary>
        /// <returns></returns>
        public string NewFrmGuide_Create_DBList()
        {
            BP.Sys.MapData md = new BP.Sys.MapData();
            md.Name = this.GetRequestVal("TB_Name");

            md.No = DataType.ParseStringForNo(this.GetRequestVal("TB_No"), 100);
            //表单类型.
            md.HisFrmTypeInt = this.GetRequestValInt("DDL_FrmType");
            md.EntityType = EntityType.DBList;
            string sort = this.GetRequestVal("FK_FrmSort");
            if (DataType.IsNullOrEmpty(sort) == true)
                sort = this.GetRequestVal("DDL_FrmTree");

            md.FormTreeNo = sort;

            //类型.
            md.AppType = "100";//独立表单.
            md.DBSrc = this.GetRequestVal("DDL_DBSrc");
            if (md.IsExits == true)
                return "err@表单ID:" + md.No + "已经存在.";

            //没有设置表，保存不上.
            md.PTable = this.No;
            md.Insert();

            //增加上OID字段.
            BP.Sys.CCFormAPI.RepareCCForm(md.No);

            //数据源实体，修改OID的属性为字符型
            MapAttr mapAttr = new MapAttr(md.No + "_OID");
            mapAttr.setMyDataType(DataType.AppString);
            mapAttr.Update();

            BP.CCBill.FrmDict entityDict = new FrmDict(md.No);
            entityDict.CheckEnityTypeAttrsFor_Dict();
            entityDict.InsertToolbarBtns();

            #region 初始化数据.
            DBList db = new DBList(md.No);
            db.MainTable = "A";
            db.MainTablePK = "No";

            if (BP.Difference.SystemConfig.CCBPMRunModel == CCBPMRunModel.Single)
            {
                db.ExpEn = "SELECT A.No as OID, A.No as BillNo, a.Name AS Title, A.Tel,A.Email , A.FK_Dept as DeptNo, B.Name AS DeptT FROM Port_Emp A, Port_Dept B WHERE A.FK_Dept=B.No AND A.No='@Key' ";
                db.ExpList = "SELECT A.No as OID, A.No as BillNo, a.Name AS Title, A.Tel, A.PinYin,A.Email, B.No as DeptNo, B.Name AS DeptT FROM Port_Emp A, Port_Dept B WHERE A.FK_Dept=B.No ";
                db.ExpCount = "SELECT  count(a.No) as Num FROM Port_Emp A, Port_Dept B WHERE A.FK_Dept=B.No ";
            }
            else
            {
                db.ExpEn = "SELECT A.No as OID, A.No as BillNo, a.Name AS Title, A.Tel ,A.Email , A.FK_Dept as DeptNo, B.Name AS DeptT FROM Port_Emp A, Port_Dept B WHERE A.FK_Dept=B.No AND A.No='@Key' AND A.OrgNo='@WebUser.OrgNo' ";
                db.ExpList = "SELECT A.No as OID, A.No as BillNo, a.Name AS Title, A.Tel, A.PinYin,A.Email, B.No as DeptNo, B.Name AS DeptT FROM Port_Emp A, Port_Dept B WHERE A.FK_Dept=B.No AND A.OrgNo='@WebUser.OrgNo'  ";
                db.ExpCount = "SELECT  count(a.No) as Num FROM Port_Emp A, Port_Dept B WHERE A.FK_Dept=B.No AND A.OrgNo='@WebUser.OrgNo'  ";
            }

            db.Update();
            #endregion  初始化数据.

            return "创建成功...";
        }
        /// <summary>
        /// 创建表单
        /// </summary>
        /// <returns></returns>
        public string NewFrmGuide_Create()
        {
            MapData md = new MapData();

            string no = this.GetRequestVal("TB_No");
            string name = this.GetRequestVal("TB_Name");
            if (DataType.IsNullOrEmpty(no) == true)
                no = DataType.ParseStringForNo(no, 100);
            try
            {
                md.setName(name);
                md.SetValByKey("No", no);
                md.HisFrmTypeInt = this.GetRequestValInt("DDL_FrmType");
                string ptable = this.GetRequestVal("TB_PTable");

                md.PTable = ptable;
                //   md.PTable = DataType.ParseStringForNo(this.GetRequestVal("TB_PTable"), 100);
                //数据表模式。  需要翻译.
                md.PTableModel = this.GetRequestValInt("DDL_PTableModel");

                string sort = this.GetRequestVal("FK_FrmSort");
                if (DataType.IsNullOrEmpty(sort) == true)
                    sort = this.GetRequestVal("DDL_FrmTree");

                if (DataType.IsNullOrEmpty(sort) == true)
                {
                    if (SystemConfig.CCBPMRunModel == CCBPMRunModel.Single)
                    {
                        sort = DBAccess.RunSQLReturnStringIsNull("SELECT No FROM Sys_FormTree WHERE ParentNo='0'", "100");
                    }
                    else
                    {
                        sort = WebUser.OrgNo; 
                    }
                    //return "err@没有采集到表单存放目录.";
                }

                //    md.FK_FrmSort = sort;
                md.FormTreeNo = sort;
                md.SetValByKey("Ver", DataType.CurrentDateTime);  //创建日期.

                md.AppType = "0";//独立表单
                md.DBSrc = this.GetRequestVal("DDL_DBSrc");

                if (md.IsExits == true)
                    return "err@表单ID:" + md.No + "已经存在.";

                switch (md.HisFrmType)
                {
                    //自由，傻瓜，SL表单不做判断
                    case BP.Sys.FrmType.FoolForm:
                    case BP.Sys.FrmType.Develop:
                    case BP.Sys.FrmType.ChapterFrm:
                        break;
                    case BP.Sys.FrmType.Url:
                    case BP.Sys.FrmType.Entity:
                        md.UrlExt = md.PTable;
                        md.PTable = "";
                        break;
                    //如果是以下情况，导入模式
                    case BP.Sys.FrmType.WordFrm:
                    case BP.Sys.FrmType.WPSFrm:
                    case BP.Sys.FrmType.ExcelFrm:
                    case BP.Sys.FrmType.VSTOForExcel:
                        break;
                    default:
                        throw new Exception("err@未知表单类型." + md.HisFrmType.ToString());
                }
                md.Insert();

                BP.Sys.EntityType entityType = (EntityType)this.GetRequestValInt("EntityType");
                md.EntityType = entityType;  //设置类型.

                #region 如果是单据.
                if (entityType == EntityType.FrmBill)
                {
                    //增加上OID字段.
                    BP.Sys.CCFormAPI.RepareCCForm(md);

                    BP.CCBill.FrmBill bill = new FrmBill(md.No);
                    bill.EntityType = entityType;
                    bill.BillNoFormat = "ccbpm{yyyy}-{MM}-{LSH4}";

                    //设置默认的查询条件.
                    bill.SetPara("IsSearchKey", 1);
                    bill.SetPara("DTSearchWay", 0);

                    bill.Update();
                    bool isHavePFrmID = false;
                    if (DataType.IsNullOrEmpty(this.GetRequestVal("FrmID")) == false)
                        isHavePFrmID = true;
                    bill.CheckEnityTypeAttrsFor_Bill();
                    bill.InsertToolbarBtns();

                    #region 检查是否有关联的 attrs. @gx.
                    //检查是否传来相关的dictFrmID.
                    string dictFrmID = this.GetRequestVal("DictFrmID");
                    if (DataType.IsNullOrEmpty(dictFrmID) == false)
                    {
                        MapData mdDict = new MapData(dictFrmID);

                        GroupField groupField = new GroupField();
                        groupField.Retrieve("FrmID", md.No);

                        string attrs = this.GetRequestVal("SelectAttrs");
                        string[] strs = attrs.Split(',');
                        MapAttr ma = new MapAttr();
                        int idx = 2;
                        string selectSQL = "";
                        foreach (string str in strs)
                        {
                            if (DataType.IsNullOrEmpty(str) == true)
                                continue;

                            ma.setMyPK(str);
                            ma.RetrieveFromDBSources();
                            ma.setFrmID(md.No);
                            ma.setIdx(idx);
                            ma.setGroupID(groupField.OID);
                            ma.Insert();
                            idx++;
                            selectSQL += ma.KeyOfEn + ",";
                        }

                        selectSQL = " Title as " + dictFrmID + "Name, " + selectSQL.Substring(0, selectSQL.Length - 1);

                        //插入编号，名称字段.
                        ma.setMyPK(md.No + "_Title");
                        ma.RetrieveFromDBSources();
                        ma.setUIIsEnable(true);
                        ma.setUIVisible(true);
                        ma.setKeyOfEn(dictFrmID + "No");
                        ma.setName(mdDict.Name + "编号");
                        ma.setIdx(idx);
                        ma.setFrmID(md.No);
                        ma.setGroupID(groupField.OID);
                        ma.Insert();

                        ma.setKeyOfEn(dictFrmID + "Name");
                        ma.setName(mdDict.Name + "名称");
                        ma.setFrmID(md.No);
                        ma.setGroupID(groupField.OID);
                        ma.setIdx(idx);
                        ma.Insert();

                        string ctrlID = dictFrmID + "No";

                        // 设置MapExt. TBFullCtrl 模式.
                        MapExt mapExt = new MapExt();
                        mapExt.setMyPK(ma.FrmID + "_" + ctrlID + "_TBFullCtrl");
                        mapExt.FrmID = dictFrmID;

                        mapExt.SetValByKey("ExtModel", "TBFullCtrl");
                        mapExt.SetValByKey("ExtType", "TBFullCtrl");

                        mapExt.SetValByKey("DoWay", "Simple");
                        mapExt.SetValByKey("AttrOfOper", ctrlID);
                        mapExt.SetValByKey("Tag4", "SELECT BillNo as No, Title as Name FROM " + mdDict.PTable + " WHERE BillNo like ~%@Key%~ OR Title like ~%@Key%~");
                        mapExt.SetValByKey("Tag5", "Self");
                        mapExt.SetValByKey("Tag6", "SELECT " + selectSQL + "  FROM " + mdDict.PTable + " WHERE BillNo=~@Key~");
                        mapExt.SetValByKey("DBType", "0");
                        mapExt.SetValByKey("DBSrcNo", "local");
                        mapExt.Insert();
                    }
                    #endregion 检查是否有关联的 attrs.
                }
                #endregion 如果是单据.

                #region 如果是实体 FrmDict,FrmAsk
                if (entityType == EntityType.FrmDict || entityType == EntityType.FrmAsk)
                {
                    //增加上OID字段.
                    BP.Sys.CCFormAPI.RepareCCForm(md);

                    BP.CCBill.FrmDict entityDict = new FrmDict(md.No);
                    entityDict.BillNoFormat = "3"; //编码格式.001,002,003.
                    entityDict.BtnNewModel = 0;

                    //设置默认的查询条件.
                    entityDict.SetPara("IsSearchKey", 1);
                    entityDict.SetPara("DTSearchWay", 0);

                    entityDict.EntityType = entityType;
                    entityDict.Update();
                    entityDict.CheckEnityTypeAttrsFor_Dict();
                    entityDict.InsertToolbarBtns();
                }
                #endregion 如果是实体 FrmDict,FrmAsk .

                #region 如果是实体 FrmEntityNoName
                if (entityType == EntityType.FrmEntityNoName)
                {
                    BP.CCBill.FrmEntityNoName entityDict = new FrmEntityNoName(md.No);
                    entityDict.BillNoFormat = "3"; //编码格式.001,002,003.
                    //entityDict.BtnNewModel = 0;

                    //设置默认的查询条件.
                    entityDict.SetPara("IsSearchKey", 1);
                    entityDict.SetPara("DTSearchWay", 0);

                    entityDict.EntityType = entityType;
                    entityDict.SetValByKey("EntityType", 5);

                    entityDict.Update();
                    entityDict.CheckEnityTypeAttrsFor_EntityNoName();

                    //生成按钮.
                    BP.CCBill.FrmDict frmDict = new FrmDict(md.No);
                    frmDict.InsertToolbarBtns();

                    md.SetValByKey("EnPK", "No");
                    md.Update();

                    //  entityDict.InsertToolbarBtns();

                    return "info@实体EntityNoName创建成功.";
                }
                #endregion 如果是实体  FrmEntityNoName

                //增加上OID字段.
                BP.Sys.CCFormAPI.RepareCCForm(md);

                //创建表与字段.
                GEEntity en = new GEEntity(md.No);
                en.CheckPhysicsTable();

                if (md.HisFrmType == BP.Sys.FrmType.WPSFrm || md.HisFrmType == BP.Sys.FrmType.WordFrm || md.HisFrmType == BP.Sys.FrmType.ExcelFrm)
                {
                    /*把表单模版存储到数据库里 */
                    return "url@../../Comm/RefFunc/En.htm?EnName=BP.WF.Template.Frm.MapFrmExcel&PKVal=" + md.No;
                }

                if (md.HisFrmType == BP.Sys.FrmType.Entity)
                    return "url@../../Comm/Ens.htm?EnsName=" + md.PTable;

                if (md.HisFrmType == BP.Sys.FrmType.Develop)
                    return "url@../DevelopDesigner/Designer.htm?FK_MapData=" + md.No + "&FrmID=" + md.No + "&EntityType=" + this.GetRequestVal("EntityType");

                return "url@../FoolFormDesigner/Designer.htm?IsFirst=1&FK_MapData=" + md.No + "&EntityType=" + this.GetRequestVal("EntityType");
            }
            catch (Exception ex)
            {
                md.Delete();
                return "err@" + ex.Message;
            }
        }
        #endregion 创建表单.

        #region 表格处理.
        public string Tables_Init()
        {
            BP.Sys.SFTables tabs = new BP.Sys.SFTables();
            tabs.RetrieveAll();
            DataTable dt = tabs.ToDataTableField();
            dt.Columns.Add("RefNum", typeof(int));

            foreach (DataRow dr in dt.Rows)
            {
                //求引用数量.
                int refNum = DBAccess.RunSQLReturnValInt("SELECT COUNT(KeyOfEn) FROM Sys_MapAttr WHERE UIBindKey='" + dr["No"] + "'", 0);
                dr["RefNum"] = refNum;
            }
            return BP.Tools.Json.ToJson(dt);
        }
        public string Tables_Delete()
        {
            try
            {
                BP.Sys.SFTable tab = new BP.Sys.SFTable();
                tab.No = this.No;
                tab.Delete();
                return "删除成功.";
            }
            catch (Exception ex)
            {
                return "err@" + ex.Message;
            }
        }

        public string TableRef_Init()
        {
            BP.Sys.MapAttrs mapAttrs = new BP.Sys.MapAttrs();
            mapAttrs.RetrieveByAttr(BP.Sys.MapAttrAttr.UIBindKey, this.FK_SFTable);

            DataTable dt = mapAttrs.ToDataTableField();
            return BP.Tools.Json.ToJson(dt);
        }


        #endregion

        #region 复制表单

        public void DoCopyFrm()
        {
            string fromFrmID = GetRequestVal("FromFrmID");
            string toFrmID = GetRequestVal("ToFrmID");
            string toFrmName = GetRequestVal("ToFrmName");
            MapData toMapData = new MapData(fromFrmID);
            toMapData.No = toFrmID;
            toMapData.Name = toFrmName;
            toMapData.Insert();
            //导入表单信息
            MapData.ImpMapData(toFrmID, BP.Sys.CCFormAPI.GenerHisDataSet_AllEleInfo(fromFrmID));

            if (toMapData.HisEntityType == (int)EntityType.FrmBill)
            {
                FrmBill frmBill = new FrmBill(fromFrmID);
                frmBill.No = toFrmID;
                frmBill.Name = toFrmName;
                frmBill.Update();
            }
            if (toMapData.HisEntityType == (int)EntityType.FrmDict)
            {
                FrmDict frmDict = new FrmDict(fromFrmID);
                frmDict.No = toFrmID;
                frmDict.Name = toFrmName;
                frmDict.Update();
            }

            //清空缓存

            toMapData.RepairMap();
            BP.Difference.SystemConfig.DoClearCache();


        }
        #endregion 复制表单

    }
}
