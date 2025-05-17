package bp.App.Handler;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.web.WebUser;

import java.util.Hashtable;

public class DJXXHandler extends bp.difference.handler.DirectoryPageBase{
    /**
     * 获取数据项对应单据信息
     * @return
     */
    public final String getDJTree(){
        try {
            String SJX = this.GetRequestVal("SJX");
            String sql = "select OID, No, Name, sql_czml, ParentNo,FlowNo from v_gk_sjxxx where 1=1 and sql_czml = '"+SJX+"'";
            DataTable dt = bp.da.DBAccess.RunSQLReturnTable(sql);
            dt.Columns.get("no").ColumnName = "No";
            dt.Columns.get("name").ColumnName = "Name";
            dt.Columns.get("oid").ColumnName = "OID";
            dt.Columns.get("parentno").ColumnName = "ParentNo";
            dt.Columns.get("flowno").ColumnName = "FlowNo";
            return bp.tools.Json.ToJson(dt);
        }
        catch (Exception ex){
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "获取数据项单据信息失败：" + ex.getMessage());
            return "";
        }
    }

    /**
     * 删除数据项信息
     * @return
     */
    public String deleteSJXXX(){
        try{
            String ID = this.GetRequestVal("ID");//数据项内码
            String OID = this.GetRequestVal("OID");//单据流程内码
            String SXZL = this.GetRequestVal("SXZL");//事项种类

            //更新12流程表cghzlxno
            DBAccess.RunSQL("update nd12rpt set wfstate = '-1' where oid::text = '"+OID+"'");
            //更新数据项审批状态为删除
            DBAccess.RunSQL("update gk_cghzlx set shzt = -1 where id::text = '"+ID+"'");
            //删除村庄城市关联表数据
            if(SXZL.equals("村庄数据")){
                DBAccess.RunSQL("delete from GK_Cglink where dataitemid::text = '"+ID+"'");
            }
            return "";
        }
        catch (Exception ex){
            bp.da.Log.DefaultLogWriteLine(LogType.Info, "获取所在市县错误："+ ex.getMessage());
            return "";
        }
    }
}

