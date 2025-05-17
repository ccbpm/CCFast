package bp.ccbill;

import bp.da.*;
import bp.sys.*;
import bp.wf.httphandler.*;
import bp.difference.*;
import java.net.URLDecoder;
import java.util.*;

/**
 * 页面功能实体
 */
public class WF_CCBill_Admin_DBList extends bp.difference.handler.DirectoryPageBase {

    ///#region 属性.

    /**
     * 模块编号
     */
    public final String getModuleNo() {
        String str = this.GetRequestVal("ModuleNo");
        return str;
    }

    /**
     * 菜单ID.
     */
    public final String getMenuNo() {
        String str = this.GetRequestVal("MenuNo");
        return str;
    }

    public final String getGroupID() {
        String str = this.GetRequestVal("GroupID");
        return str;
    }

    ///#endregion 属性.

    /**
     * 构造函数
     */
    public WF_CCBill_Admin_DBList() {

    }

    /**
     * 映射
     *
     * @return
     */
    public final String FieldsORM_Init() throws Exception {
        try {
            DBList db = new DBList(this.getFrmID());

            String sql = db.getExpEn();
            if (DataType.IsNullOrEmpty(sql) == true) {
                return "";
            }

            sql = sql.replace("~~", "\"");
            sql = sql.replace("~", "'");

            DataTable mydt = new DataTable();
            mydt.Columns.Add("No");
            mydt.Columns.Add("DBType");
            //SQL查询
            if (db.getDBType() == 0) {
                sql = sql.replace("@Key", "1234");
                DataTable dt = null;
                try {
                    //本机数据库查询
                    if (Objects.equals(db.getDBSrc(), "local") || DataType.IsNullOrEmpty(db.getDBSrc()) == true) {
                        dt = DBAccess.RunSQLReturnTable(sql);
                    } else {
                        SFDBSrc dbsrc = new SFDBSrc(db.getDBSrc());
                        dt = dbsrc.RunSQLReturnTable(sql);
                    }
                } catch (RuntimeException ex) {
                    return ex.getMessage();
                }

                for (DataColumn dc : dt.Columns) {
                    DataRow dr = mydt.NewRow();
                    dr.setValue(0, dc.ColumnName);
                    dr.setValue(1, dc.getDataType());
                    mydt.Rows.add(dr);
                }
                return bp.tools.Json.ToJson(mydt);
            }


            //根据URL获取数据源
            if (db.getDBType() == 1) {
                String url = sql;
                url = url.replace("@Key", "");
                if (url.contains("http") == false) {
                    /*如果没有绝对路径 */
                    if (SystemConfig.isBSsystem()) {
                        /*在cs模式下自动获取*/
                        String host = bp.sys.base.Glo.getRequest().getRemoteHost();
                        if (url.contains("@AppPath")) {
                            url = url.replace("@AppPath", "http://" + host + bp.sys.base.Glo.getRequest().getRemoteAddr()); //bp.sys.Glo.Request.ApplicationPath
                        } else {
                            url = "http://" + bp.sys.base.Glo.getRequest().getRemoteHost() + url;
                        }
                    }

                    if (SystemConfig.isBSsystem() == false) {
                        /*在cs模式下它的baseurl 从web.config中获取.*/
                        String cfgBaseUrl = (String) SystemConfig.getAppSettings().get("HostURL");
                        if (DataType.IsNullOrEmpty(cfgBaseUrl)) {
                            String err = "调用url失败:没有在web.config中配置BaseUrl,导致url事件不能被执行.";
                            Log.DebugWriteError(err);
                            throw new RuntimeException(err);
                        }
                        url = cfgBaseUrl + url;
                    }
                }
                String json = DataType.ReadURLContext(url, 8000);
                if (DataType.IsNullOrEmpty(json) == true) {
                    return "err@执行URL没有返回结果值";
                }

                DataTable dt = bp.tools.Json.ToDataTable(json);
                for (DataColumn dc : dt.Columns) {
                    DataRow dr = mydt.NewRow();
                    dr.setValue(0, dc.ColumnName);
                    dr.setValue(1, dc.getDataType());
                    mydt.Rows.add(dr);
                }
                json = bp.tools.Json.ToJson(mydt);

                return json;
            }
            //执行存储过程
            if (db.getDBType() == 2) {
                if (sql.trim().toUpperCase().startsWith("SELECT") == false) {
                    switch (SystemConfig.getAppCenterDBType()) {
                        case MSSQL:
                            sql = "EXEC " + sql + " null";
                            break;
                        case MySQL:
                            sql = "CALL " + sql + "(null)";
                            break;
                        default:
                            throw new RuntimeException("err@其他版本的数据还未解析该功能");
                    }
                }
                DataTable dt = DBAccess.RunSQLReturnTable(sql);
                for (DataColumn dc : dt.Columns) {
                    DataRow dr = mydt.NewRow();
                    dr.setValue(0, dc.ColumnName);
                    dr.setValue(1, dc.getDataType());
                    mydt.Rows.add(dr);
                }
                return bp.tools.Json.ToJson(mydt);

            }
            return "err@没有增加的判断" + db.getDBType();
        } catch (RuntimeException ex) {
            return "err@" + ex.getMessage();
        }
    }

    public final String FieldsORM_SaveKeysName() throws Exception {
        MapAttrs mapAttrs = new MapAttrs(this.getFrmID() + "Bak");
        //改变类型先保存字段集合的名称
        for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
            if (key == null) {
                continue;
            }
            if (key.indexOf("TB_") == -1 || key.equals("TB_Doc") == true) {
                continue;
            }
            String myKey = key;
            String val = ContextHolderUtils.getRequest().getParameter(key);
            myKey = myKey.replace("TB_", "");

            val = URLDecoder.decode(val, "UTF-8");

            Object tempVar = mapAttrs.GetEntityByKey(this.getFrmID() + "Bak_" + myKey);
            MapAttr attr = tempVar instanceof MapAttr ? (MapAttr) tempVar : null;
            if (attr != null) {
                boolean uiVisible = this.GetRequestValBoolen("CB_" + attr.getKeyOfEn());
                attr.setUIVisible(uiVisible);
                attr.setName(val);
                attr.DirectUpdate();
            }


        }
        return "保存成功";
    }

    /**
     * 执行应用
     *
     * @return
     */
    public final String FieldsORM_App() throws Exception {
        String ver = this.GetRequestVal("Ver");
        if(DataType.IsNullOrEmpty(ver) == true)
            ver ="H5";
        // 删除当前的字段.
        //系统字段
        String systemKeys = "BillState,RDT,Starter,StarterName,OrgNo,AtPara";
        String sql = "DELETE FROM Sys_MapAttr Where FK_MapData='" + this.getFrmID() + "' AND KeyOfEn NOT IN('" + systemKeys.replace(",", "','") + "')";
        DBAccess.RunSQL(sql);
        // 按照顺序从 bak里copy过来.
        systemKeys = systemKeys + ",";
        //保存当前字段
        MapAttrs mapAttrs = new MapAttrs(this.getFrmID() + "Bak");
        if(ver.equals("H5")){
            //改变类型先保存字段集合的名称
            for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet()) {
                if (key == null) {
                    continue;
                }
                if (key.indexOf("TB_") == -1 || key.equals("TB_DBSrc") == true) {
                    continue;
                }
                String myKey = key;
                String val = ContextHolderUtils.getRequest().getParameter(key);
                myKey = myKey.replace("TB_", "");

                val = URLDecoder.decode(val, "UTF-8");

                Object tempVar = mapAttrs.GetEntityByKey(this.getFrmID() + "Bak_" + myKey);
                MapAttr attr = tempVar instanceof MapAttr ? (MapAttr) tempVar : null;
                if (attr != null) {
                    boolean uiVisible = this.GetRequestValBoolen("CB_" + attr.getKeyOfEn());
                    attr.setName(val);
                    attr.setUIVisible(uiVisible);
                    attr.DirectUpdate();
                    if (systemKeys.indexOf(attr.getKeyOfEn() + ",") == -1) {
                        attr.setFrmID(this.getFrmID());
                        attr.setMyPK(this.getFrmID() + "_" + attr.getKeyOfEn());
                        attr.setGroupID(1);
                        attr.Insert();
                    }

                }


            }
        }
        if(ver.equals("Vue3")){
            for(MapAttr attr : mapAttrs.ToJavaList()){
                if (systemKeys.indexOf(attr.getKeyOfEn() + ",") == -1) {
                    attr.setFrmID(this.getFrmID());
                    attr.setMyPK(this.getFrmID() + "_" + attr.getKeyOfEn());
                    attr.setGroupID(1);
                    attr.Insert();
                }
            }
        }
        MapData mapData = new MapData(this.getFrmID());
        mapData.ClearCache();
        return "执行成功";
    }

}
