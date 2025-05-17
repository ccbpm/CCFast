using System;
using System.Collections.Generic;
using System.Collections;
using System.Data;
using System.Text;
using System.Web;
using BP.DA;
using BP.Sys;
using BP.Web;
using BP.Port;
using BP.En;
using BP.WF;
using BP.WF.Template;
using BP.WF.Data;
using BP.WF.HttpHandler;
using BP.CCBill.Template;


namespace BP.CCBill
{
    /// <summary>
    /// 页面功能实体
    /// </summary>
    public class CCBill_Plus : DirectoryPageBase
    {
        #region 构造方法.
        /// <summary>
        /// 构造函数
        /// </summary>
        public CCBill_Plus()
        {
        }
        #endregion 构造方法.

        public string GenerListEn_Init()
        {
            String sql = "SELECT ExpStr FROM Frm_GenerList WHERE No='" + this.RefNo + "'";
            String exp = DBAccess.RunSQLReturnString(sql);

            String sqlDBSrc = "SELECT DBSrc FROM Frm_GenerList WHERE No='" + this.RefNo + "'";
            String dbSrc = DBAccess.RunSQLReturnString(sqlDBSrc);

            DataTable dt;
            exp = BP.WF.Glo.DealSQLExp(exp, null, null);
            if (DataType.IsNullOrEmpty(exp) == false && dbSrc.Equals("local") == false)
            {
                SFDBSrc sfdb = new SFDBSrc(dbSrc);
                dt = sfdb.RunSQLReturnTable(exp);
            }
            else
                dt = DBAccess.RunSQLReturnTable(exp);

            if (BP.Difference.SystemConfig.AppCenterDBFieldCaseModel == FieldCaseModel.UpperCase)
            {
                if (dt.Columns.Contains("NO") == true)
                    dt.Columns["NO"].ColumnName = "No";
                if (dt.Columns.Contains("NAME") == true)
                    dt.Columns["NAME"].ColumnName = "Name";

                //判断是否存在PARENTNO列，避免转换失败
                if (dt.Columns.Contains("PARENTNO") == true)
                    dt.Columns["PARENTNO"].ColumnName = "ParentNo";
            }

            if (BP.Difference.SystemConfig.AppCenterDBFieldCaseModel == FieldCaseModel.Lowercase)
            {
                if (dt.Columns.Contains("no") == true)
                    dt.Columns["no"].ColumnName = "No";
                if (dt.Columns.Contains("name") == true)
                    dt.Columns["name"].ColumnName = "Name";

                //判断是否存在PARENTNO列，避免转换失败
                if (dt.Columns.Contains("parentno") == true)
                    dt.Columns["parentno"].ColumnName = "ParentNo";
            }
            return BP.Tools.Json.ToJson(dt);
        }

    }
}
