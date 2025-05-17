using BP.DA;
using BP.En;
using BP.WF;
using BP.Sys;
using System.Data;
using NPOI.SS.Formula.Eval;
using System;

namespace BP.CCBill
{
    /// <summary>
    /// 数据源实体
    /// </summary>
    public class DBEntity : EntityNoName
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
        public DBEntity()
        {
        }
        /// <summary>
        /// 数据源实体
        /// </summary>
        /// <param name="no">映射编号</param>
        public DBEntity(string no)
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
                map.AddDDLSysEnum(MapDataAttr.FrmType, 0, "表单类型", true, true, "BillFrmType", "@0=经典表单@1=自由表单@8=开发者表单");
                #endregion 基本属性.

                #region 数据源.
                map.AddGroupAttr("数据源");
                map.AddTBString(MapDataAttr.DBSrc, null, "数据源", false, false, 0, 600, 20);
                map.AddTBString(MapDataAttr.ExpEn, null, "SQL", false, false, 0, 600, 20, true);
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
                #region 1.重新创建视图.
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

                string sql = "";
                string exp = this.GetValStrByKey("ExpEn").Replace("~", "'");
                exp = exp.Replace("~", "'");
                exp = exp.Replace("~", "'");
                exp = exp.Replace("~", "'");
                exp = exp.Replace("~", "'");
                exp = exp.Replace("~", "'");
                try
                {
                    sql = "CREATE VIEW " + this.No + " AS " + exp;
                    DBAccess.RunSQL(sql);
                }
                catch (Exception ex)
                {
                    return "err@表达式错误：无法创建视图." + ex.Message;
                }
                #endregion 重新创建视图.

                #region 2.检查列是否完整.
                sql = "SELECT * FROM " + this.No + " WHERE 1=1 ";
                DataTable dt = DBAccess.RunSQLReturnTable(sql);
                string msg = "";
                string cols = ",OID,Title,StarterName,Starter,FK_Dept,OrgNo,RDT,BillState,BillNo,AtPara,FID,";
                string[] strs = cols.Split(',');
                foreach (string str in strs)
                {
                    if (DataType.IsNullOrEmpty(str) == true)
                        continue;
                    if (dt.Columns.Contains(str) == false)
                        msg += "" + str + ",";
                }
                if (DataType.IsNullOrEmpty(msg) == false)
                    return "err@请检查SQL查询语句不符合要求，如下列不存在:" + msg;
                #endregion 检查列是否完整.

                #region 3.创建新字段.
                foreach (DataColumn dc in dt.Columns)
                {
                    MapAttr attr = new MapAttr();
                    attr.MyPK = this.No + "_" + dc.ColumnName;
                    if (attr.RetrieveFromDBSources() == 1)
                        continue;

                    attr.setKeyOfEn(this.No);
                    attr.setKeyOfEn(dc.ColumnName);

                    //获得列的中文名字.
                    string name = DBAccess.RunSQLReturnStringIsNull("SELECT Name FROM Sys_MapAttr WHERE KeyOfEn='" + dc.ColumnName + "'", dc.ColumnName);
                    attr.setName(name);
                    attr.setFrmID(this.No); //

                    if (dc.DataType == typeof(int))
                        attr.setMyDataType(DataType.AppInt);

                    if (dc.DataType == typeof(float))
                        attr.setMyDataType(DataType.AppFloat);

                    if (dc.DataType == typeof(decimal))
                        attr.setMyDataType(DataType.AppDouble);

                    switch (dc.ColumnName)
                    {
                        case "OID":
                        case "Rec":
                        case "AtPara":
                        case "OrgNo":
                        case "RDT":
                        case "BillState":
                        case "FID":
                        case "FK_Dept":
                            attr.setUIVisible(false);
                            attr.setEditType(EditType.UnDel);
                            attr.setUIVisible(false);
                            break;
                        case "Title":
                            attr.setEditType(EditType.UnDel);
                            break;
                        default:
                            break;
                    }
                    //设置不能删除.
                    if (cols.Contains("," + attr.KeyOfEn + ",") == true)
                        attr.setEditType(EditType.UnDel);
                    attr.Insert();
                    msg += "增加字段:" + dc.ColumnName;
                }
                #endregion 创建字段.

                #region 4.删除没有的字段.
                MapAttrs attrs = new MapAttrs();
                attrs.Retrieve("FK_MapData", this.No);
                foreach (MapAttr attr in attrs)
                {
                    if (dt.Columns.Contains(attr.KeyOfEn) == true) continue;
                    attr.Delete(); //删除他.
                    msg += "@字段：" + attr.KeyOfEn + " - " + attr.Name + "被删除.";
                }
                #endregion 创建字段.

                return "检查成功:" + msg;
            }
            catch (System.Exception ex)
            {
                return "err@" + ex.Message;
            }
        }
    }
}
