package bp.ccbill;

import bp.da.DBAccess;
import bp.da.DataTable;
import bp.da.DataType;
import bp.da.FieldCaseModel;
import bp.difference.handler.DirectoryPageBase;
import bp.sys.SFDBSrc;
import net.sf.json.JSONObject;

/**
 * 页面功能实体
 *
 * @author: scott
 * @date: 2024年07月25日 10:30
 */
public class CCBill_Plus extends DirectoryPageBase {
    ///#region 构造方法.
    /**
     * 构造函数
     */
    public CCBill_Plus() {
    }
    ///#endregion 构造方法.

    public String GenerListEn_Init() throws Exception {
        String sql = "SELECT ExpStr FROM Frm_GenerList WHERE No='" + this.getRefNo() + "'";
        String exp = DBAccess.RunSQLReturnString(sql);

        String sqlDBSrc = "SELECT DBSrc FROM Frm_GenerList WHERE No='" + this.getRefNo() + "'";
        String dbSrc = DBAccess.RunSQLReturnString(sqlDBSrc);

        DataTable dt;
        exp = bp.wf.Glo.DealSQLExp(exp, null, null);
        if (DataType.IsNullOrEmpty(exp) == false  && dbSrc.equals("local") == false)
        {
            SFDBSrc sfdb = new SFDBSrc(dbSrc);
            dt = sfdb.RunSQLReturnTable(exp);
        }
        else
            dt = DBAccess.RunSQLReturnTable(exp);

        if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
        {
            dt.Columns.get("NO").ColumnName = "No";
            dt.Columns.get("NAME").ColumnName = "Name";

            //判断是否存在PARENTNO列，避免转换失败
            if (dt.Columns.contains("PARENTNO") == true)
                dt.Columns.get("PARENTNO").ColumnName = "ParentNo";
        }

        if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
        {
            dt.Columns.get("no").ColumnName = "No";
            dt.Columns.get("name").ColumnName = "Name";

            //判断是否存在PARENTNO列，避免转换失败
            if (dt.Columns.contains("parentno") == true)
                dt.Columns.get("parentno").ColumnName = "ParentNo";
        }

        return bp.tools.Json.ToJson(dt);
    }

}
