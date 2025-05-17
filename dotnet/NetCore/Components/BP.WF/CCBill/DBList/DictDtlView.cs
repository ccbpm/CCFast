using BP.DA;
using BP.En;
using BP.WF;
using BP.Sys;
using System.Data;
using NPOI.SS.Formula.Eval;
using System;
using BP.Difference;
using System.DirectoryServices.Protocols;

namespace BP.CCBill
{
    /// <summary>
    /// 数据源实体
    /// </summary>
    public class DictDtlView : EntityNoName
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
        #endregion

        #region 构造方法
        /// <summary>
        /// 数据源实体
        /// </summary>
        public DictDtlView()
        {
        }
        /// <summary>
        /// 数据源实体
        /// </summary>
        /// <param name="no">映射编号</param>
        public DictDtlView(string no)
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
                Map map = new Map("Sys_MapData", "数据源实体");

                map.CodeStruct = "4";

                #region 基本属性.
                map.AddGroupAttr("基本属性");
                map.AddTBStringPK(MapDataAttr.No, null, "编号", true, true, 1, 190, 20);
                map.AddTBString(MapDataAttr.Name, null, "名称", true, false, 0, 200, 20, true);
                #endregion 基本属性.

                #region 数据源.

                map.AddGroupAttr("SQL数据源");
                map.AddTBString("DictID", null, "实体ID", true, true, 0, 100, 20);
                map.AddTBString("DictName", null, "实体名称", true, true, 0, 100, 20);
                map.AddTBString("DictDtlID", null, "从表ID", true, true, 0, 100, 20);
                map.AddTBString("DictDtlName", null, "从表名称", true, true, 0, 100, 20);

                map.AddTBStringDoc("ExpEn", null, "主表字段", true, false, true);
                map.AddTBStringDoc("DtlFields", null, "从表字段", true, false, true);
                #endregion 数据源.

                //增加参数字段.
                map.AddTBAtParas(4000);

                this._enMap = map;
                return this._enMap;
            }
        }
        #endregion

        /// <summary>
        /// TS方法调用.
        /// </summary>
        /// <returns></returns>
        public string CheckIt()
        {
            try
            {
                #region 0.变量定义.
                string frmID = this.GetValStringByKey("DictID"); //主表ID.
                string dtlID = this.GetValStringByKey("DictDtlID"); //从表ID.

                MapData md = new MapData(frmID);
                MapAttrs mdAttrs = new MapAttrs(frmID);

                MapDtl dtl = new MapDtl(dtlID);
                MapAttrs dtlAttrs = new MapAttrs(dtlID);
                // 现有的字段.
                MapAttrs attrs = new MapAttrs();
                attrs.Delete("FK_MapData", this.No);

                int grouID = DBAccess.RunSQLReturnValInt("SELECT OID FROM Sys_GroupField WHERE FrmID='" + this.No + "' AND CtrlType='' ", 0);
                if (grouID == 0)
                {
                    GroupField gf = new GroupField();
                    gf.Lab = "基本信息";
                    gf.FrmID = this.No;
                    gf.Insert();
                    grouID = gf.OID;
                }

                string select = "SELECT B.OID,A.Title,A.StarterName,A.Starter,A.FK_Dept,A.OrgNo,A.RDT,A.BillState,A.BillNo,A.AtPara,A.FID";
                string from = " FROM " + md.PTable + " A, " + dtl.PTable + " B ";
                string where = " WHERE A.OID=B.RefPK AND A.BillState >=1  ";
                #endregion 0.变量定义.

                #region 1. 处理公共字段.
                string pubs = ",OID,Title,StarterName,Starter,FK_Dept,OrgNo,RDT,BillState,BillNo,AtPara,FID,";
                string[] strs = pubs.Split(','); //从表ID.
                foreach (string str in strs)
                {
                    if (DataType.IsNullOrEmpty(str) == true)
                        continue;
                    //当前数据源里是否存在
                    MapAttr attr = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, str) as MapAttr;
                    if (attr == null)
                    {
                        //从实体里获取该属性.
                        MapAttr mdattr = mdAttrs.GetEntityByKey(MapAttrAttr.KeyOfEn, str) as MapAttr;
                        if (mdattr == null)
                            continue;

                        mdattr.setFrmID(this.No);
                        mdattr.setMyPK(mdattr.FrmID + "_" + str);
                        mdattr.setGroupID(grouID);
                        mdattr.DirectInsert();
                    }
                }
                #endregion 1. 处理公共字段.

                #region 2. 主表字段..
                string mainTable = "";
                strs = this.GetValStringByKey("ExpEn").Split(','); //从表ID.
                foreach (string str in strs)
                {
                    if (DataType.IsNullOrEmpty(str) == true)
                        continue;

                    //从实体里获取该属性.
                    MapAttr mdattr = mdAttrs.GetEntityByKey(MapAttrAttr.KeyOfEn, str) as MapAttr;
                    //当前数据源里是否存在
                    MapAttr attr = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, str) as MapAttr;
                    //说明 源的Frm已经删除了.
                    if (mdattr == null)
                    {
                        if (attr != null)
                        {
                            attr.Delete(); //如果当前映射里还有,就把他删除掉.
                            continue;
                        }
                        continue;
                    }

                    if (attr == null)
                    {
                        mdattr.setFrmID(this.No);
                        mdattr.setMyPK(mdattr.FrmID + "_" + str);
                        mdattr.setGroupID(grouID);
                        mdattr.Insert();

                        //检查是否是外键字段.
                        if (DataType.IsNullOrEmpty(mdattr.UIBindKey) == false && mdattr.MyDataType == DataType.AppString)
                        {
                            mdattr.setMyPK(mdattr.FrmID + "_" + str + "T");
                            mdattr.UIVisible = false;
                            mdattr.UIContralType = UIContralType.TB;
                            mdattr.setKeyOfEn(str + "T");
                            mdattr.setGroupID(grouID);
                            mdattr.DirectInsert();
                        }
                    }
                    //增加到查询里.
                    mainTable += ",A." + str;
                    //判单是否是外键字段.
                    if (DataType.IsNullOrEmpty(mdattr.UIBindKey) == false && mdattr.UIContralType == UIContralType.DDL && mdattr.MyDataType == DataType.AppString)
                    {
                        where += " AND " + SystemConfig.AppCenterDBLengthStr + "(A." + str + ") >=1 ";
                        where += " AND " + SystemConfig.AppCenterDBLengthStr + "(A." + str + "T) >=1 ";
                    }
                }
                #endregion 2. 主表字段.

                #region 3. 从表字段.
                string dtlTable = "";
                strs = this.GetValStringByKey("DtlFields").Split(','); //从表ID.
                foreach (string str in strs)
                {
                    if (DataType.IsNullOrEmpty(str) == true)
                        continue;

                    //从实体里获取该属性.
                    MapAttr mdattr = dtlAttrs.GetEntityByKey(MapAttrAttr.KeyOfEn, str) as MapAttr;
                    //当前数据源里是否存在
                    MapAttr attr = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, str) as MapAttr;
                    //说明 源的Frm已经删除了.
                    if (mdattr == null)
                    {
                        if (attr != null)
                        {
                            attr.Delete(); //如果当前映射里还有,就把他删除掉.
                            continue;
                        }
                        continue;
                    }

                    if (attr == null)
                    {
                        mdattr.setFrmID(this.No);
                        mdattr.setMyPK(mdattr.FrmID + "_" + str);
                        mdattr.setGroupID(grouID);
                        mdattr.Insert();

                        //检查是否是外键字段.
                        if (DataType.IsNullOrEmpty(mdattr.UIBindKey) == false && mdattr.MyDataType == DataType.AppString)
                        {
                            mdattr.setMyPK(mdattr.FrmID + "_" + str + "T");
                            mdattr.UIVisible = false;
                            mdattr.setKeyOfEn(str + "T"); //增加为影子字段.
                            mdattr.UIBindKey = "";
                            mdattr.UIContralType = UIContralType.TB;
                            mdattr.setGroupID(grouID);
                            mdattr.DirectInsert();
                        }
                    }
                    dtlTable += ",B." + str;
                    //判单是否是外键字段.
                    if (DataType.IsNullOrEmpty(mdattr.UIBindKey) == false && mdattr.UIContralType == UIContralType.DDL && mdattr.MyDataType == DataType.AppString)
                    {
                        where += " AND " + SystemConfig.AppCenterDBLengthStr + "(B." + str + ") >=1 ";
                        where += " AND " + SystemConfig.AppCenterDBLengthStr + "(B." + str + "T) >=1 ";
                    }
                }
                #endregion 2. 主表字段..

                #region 4.重新创建视图.
                if (BP.DA.DBAccess.IsExitsObject(this.No) == true)
                {
                    try
                    {
                        DBAccess.RunSQL("DROP VIEW " + this.No);
                    }
                    catch
                    {
                        DBAccess.RunSQL("DROP TABLE " + this.No);
                    }
                }

                //增加影子字段.
                attrs = new MapAttrs();
                attrs.Retrieve("FK_MapData", this.No);
                string tFields = "";
                foreach (MapAttr attr in attrs)
                {
                    //如果是外键，外部数据源字段.
                    if (attr.MyDataType == DataType.AppString && attr.UIContralType == UIContralType.DDL && DataType.IsNullOrEmpty(attr.UIBindKey) == false)
                    {
                        tFields += "," + attr.KeyOfEn+"T";
                    }
                }

                string exp = select + mainTable + dtlTable + tFields + from + where; //查询表达式.
                try
                {
                    string sql = "CREATE VIEW " + this.No + " AS " + exp;
                    DBAccess.RunSQL(sql);
                }
                catch (Exception ex)
                {
                    return "err@表达式错误：无法创建视图." + ex.Message;
                }
                #endregion 4.重新创建视图..

                #region 5.删除没有的字段.

                string allFields = "," + pubs + this.GetValStringByKey("ExpEn") + "," + this.GetValStringByKey("DtlFields") + ",";
                foreach (MapAttr attr in attrs)
                {
                    if (allFields.Contains("," + attr.KeyOfEn + ",") == true)
                        continue;

                    //如果是影子字段.
                    if (attr.MyDataType == DataType.AppString && attr.UIContralType == UIContralType.TB
                        && attr.UIVisible == false && DataType.IsNullOrEmpty(attr.UIBindKey) == false)
                    {
                        //判断主字段是否存在？ 不存在就删除.
                        string key = attr.KeyOfEn.Substring(0, attr.KeyOfEn.Length - 1);
                        MapAttr myattr1 = attrs.GetEntityByKey("KeyOfEn", key) as MapAttr;
                        if (myattr1 == null)
                            attr.Delete();
                        continue;
                    }
                }
                #endregion 5.删除没有的字段.

                #region 检查.
                // GEEntity ge = new GEEntity(this.No);
                // ge.OID = 1900;
                //  int i = ge.RetrieveFromDBSources();
                #endregion 检查.

                return "检查成功";
            }
            catch (System.Exception ex)
            {
                return "err@" + ex.Message;
            }
        }
    }
}
