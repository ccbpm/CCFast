package bp.wf.httphandler;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.en.*;
import bp.port.Dept;
import bp.sys.*;
import bp.web.WebUser;
import bp.wf.GERptAttr;
import bp.wf.Node;
import bp.wf.data.NDXRptBaseAttr;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Objects;

/**
 * 页面功能实体
 */
public class WF_Rpt extends bp.difference.handler.DirectoryPageBase {
    ///#region 属性.

    /**
     * 查询类型: 0=我参与的,1=我发起的,2=抄送给我的,3=全部.
     */
    public final int getSearchType() {
        String val = this.GetRequestVal("SearchType");
        if (val == null || Objects.equals(val, "")) {
            return 2;
        }
        return Integer.parseInt(val);
    }

    /**
     * 分析类型: 0=我参与的,1=我发起的,2=抄送给我的,3=全部.
     */
    public final int getGroupType() {
        String val = this.GetRequestVal("GroupType");
        if (val == null || Objects.equals(val, "")) {
            return 2;
        }
        return Integer.parseInt(val);
    }

    /**
     * 状态：0=进行中，1=已经完成，2=其他 ,3=全部.
     */
    public final int getWFSta() {
        String val = this.GetRequestVal("WFSta");
        if (val == null || Objects.equals(val, "")) {
            return 3;
        }
        return Integer.parseInt(val);
    }
    ///#endregion 属性.

    /**
     * 构造函数
     */
    public WF_Rpt() throws Exception {
    }

    /**
     * 初始化
     */
    public final void SearchFlow_InitFields() throws Exception {
        String frmID = "FlowRpt" + this.getFlowNo();
        MapData md = new MapData();
        md.setNo(frmID);
        if (md.RetrieveFromDBSources() == 0) {
            Node nd = new Node(Integer.parseInt(this.getFlowNo() + "01"));
            MapData mymd = new MapData(nd.getNodeFrmID());

            md.SetValByKey("Name", nd.getName());
            md.SetValByKey("PTable", mymd.getPTable());
            md.SetValByKey("EntityType", EntityType.SingleFrm.getValue());
            md.Insert();
        }
        md.ClearCache();
        MapAttr attr = new MapAttr();
        attr.setFrmID(frmID);
        if (attr.IsExit(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.KeyOfEn, NDXRptBaseAttr.OID) == false) {
            attr = new MapAttr();
            attr.setFrmID(frmID);
            attr.SetValByKey(MapAttrAttr.KeyOfEn, NDXRptBaseAttr.OID);
            attr.setName("OID");
            attr.setMyDataType(DataType.AppInt);
            attr.setUIVisible(false);
            attr.setIdx(-100);
            attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
            attr.DirectInsert();
        }
        if (attr.IsExit(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.KeyOfEn, NDXRptBaseAttr.Title) == false) {
            attr = new MapAttr();
            attr.setFrmID(frmID);
            attr.SetValByKey(MapAttrAttr.KeyOfEn, NDXRptBaseAttr.Title);
            attr.setName("标题");
            attr.setIdx(-99);
            attr.setUIVisible(true);
            attr.setMyDataType(DataType.AppString);
            attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
            attr.DirectInsert();
        }
        if (attr.IsExit(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.KeyOfEn, NDXRptBaseAttr.WFState) == false) {
            attr = new MapAttr();
            attr.setFrmID(frmID);
            attr.SetValByKey(MapAttrAttr.KeyOfEn, NDXRptBaseAttr.WFState);
            attr.setName("状态");
            attr.setMyDataType(DataType.AppInt);
            attr.setUIVisible(true);
            attr.setLGType(FieldTypeS.Enum);
            attr.setUIContralType(UIContralType.DDL);
            attr.setUIBindKey(NDXRptBaseAttr.WFState);
            attr.setIdx(-98);
            attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
            attr.DirectInsert();
        }

        if (attr.IsExit(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.KeyOfEn, NDXRptBaseAttr.BillNo) == false) {
            attr = new MapAttr();
            attr.setFrmID(frmID);
            attr.SetValByKey(MapAttrAttr.KeyOfEn, NDXRptBaseAttr.BillNo);
            attr.setName("单号");
            attr.setIdx(-97);
            attr.setUIVisible(true);
            attr.setMyDataType(DataType.AppString);
            attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
            attr.DirectInsert();
        }
        if (attr.IsExit(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.KeyOfEn, NDXRptBaseAttr.FlowEmps) == false) {
            attr = new MapAttr();
            attr.setFrmID(frmID);
            attr.SetValByKey(MapAttrAttr.KeyOfEn, NDXRptBaseAttr.FlowEmps);
            attr.setName("参与人");
            attr.setIdx(-97);
            attr.setUIVisible(false);
            attr.setMyDataType(DataType.AppString);
            attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
            attr.DirectInsert();
        }
        if (attr.IsExit(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.KeyOfEn, NDXRptBaseAttr.FlowStarter) == false) {
            attr = new MapAttr();
            attr.setFrmID(frmID);
            attr.SetValByKey(MapAttrAttr.KeyOfEn, NDXRptBaseAttr.FlowStarter);
            attr.setName("发起人");
            attr.setIdx(-96);
            attr.setUIVisible(true);
            attr.setMyDataType(DataType.AppString);
            attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
            attr.DirectInsert();
        }
        if (attr.IsExit(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.KeyOfEn, NDXRptBaseAttr.FlowStartRDT) == false) {
            attr = new MapAttr();
            attr.setFrmID(frmID);
            attr.SetValByKey(MapAttrAttr.KeyOfEn, NDXRptBaseAttr.FlowStartRDT);
            attr.setName("发起日期");
            attr.setIdx(-95);
            attr.setUIVisible(true);
            attr.setMyDataType(DataType.AppDateTime);
            attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
            attr.DirectInsert();
        }
        //if (attr.IsExit(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.KeyOfEn, NDXRptBaseAttr.TodoEmps) == false)
        //{
        //    attr.SetValByKey(MapAttrAttr.KeyOfEn, NDXRptBaseAttr.TodoEmps);
        //    attr.setName("待办人员");
        //    attr.setIdx(-94);
        //    attr.setUIVisible(true);
        //    attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
        //    attr.DirectInsert();
        //}
    }

    public final String SearchFlow_Init() throws Exception {
        String frmID = "FlowRpt" + this.getFlowNo();

        MapData md = new MapData();
        md.setNo(frmID);
        if (md.RetrieveFromDBSources() == 0) {
            SearchFlow_InitFields();
            md.RetrieveFromDBSources();
        }


        String dtFrom = this.GetRequestVal("DTFrom");
        String dtTo = this.GetRequestVal("DTTo");
        String keyWord = this.GetRequestVal("KeyWords");

        //数据.
        GEEntitys ges = new GEEntitys(frmID);
        Attrs attrs = ges.getNewEntity().getEnMap().getAttrs();
        if(attrs.contains("WFState")==false)
            SearchFlow_InitFields();


        //设置查询条件.
        QueryObject qo = new QueryObject(ges);


        ///#region 控制状态.
        //已经完成的.
        if (this.getWFSta() == 1) {
            qo.AddWhere(NDXRptBaseAttr.WFState, "=", 3); //必须的条件.
        }

        //进行中的.
        if (this.getWFSta() == 0) {
            qo.AddWhereNotIn(NDXRptBaseAttr.WFState, "3,7,11"); //必须的条件.
            qo.addAnd();
            qo.AddWhere(NDXRptBaseAttr.WFState, ">=", 1); //必须的条件.
        }

        if (this.getWFSta() == 2) {
            qo.addLeftBracket();
            qo.AddWhere(NDXRptBaseAttr.WFState, ">", 3); //必须的条件.
            qo.addOr();
            qo.addSQL(NDXRptBaseAttr.WFState+"=1");
            qo.addRightBracket();
        }

        //全部的.
        if (this.getWFSta() == 3) {
            qo.AddWhere(NDXRptBaseAttr.WFState, ">=", 1); //必须的条件.
        }
        ///#endregion 控制状态.

        ///#region 时间范围.
        if (DataType.IsNullOrEmpty(dtTo) == false) {
            qo.addAnd();
            qo.addLeftBracket();
            qo.AddWhere(NDXRptBaseAttr.FlowStartRDT, ">=", dtFrom);
            qo.addAnd();
            //    qo.AddWhere(NDXRptBaseAttr.FlowStartRDT, "<=", dtTo);
            qo.setSQL("(  FlowStartRDT <=  '" + dtTo + "' )");
            qo.addRightBracket();
        }
        ///#endregion 时间范围.

        ///#region 权限范围..
        //我参与的
        if (this.getSearchType() == 0) {
            qo.addAnd();
            qo.AddWhere(bp.wf.GERptAttr.FlowEmps, " LIKE ", "%" + WebUser.getNo() + "%");
        }

        //我发起的
        if (this.getSearchType() == 1) {
            qo.addAnd();
            qo.AddWhere(bp.wf.GERptAttr.FlowStarter, WebUser.getNo());
        }
        ///#endregion 权限范围..

        ///#region 关键字..
        if (DataType.IsNullOrEmpty(keyWord) == false) {
            qo.addAnd();
            qo.AddWhere(bp.wf.GERptAttr.Title, " LIKE ", "%" + keyWord + "%");
            qo.addOr();
            qo.AddWhere(GERptAttr.BillNo, " LIKE ", "%" + keyWord + "%");
        }
        ///#endregion 关键字..

        //增加关键字查询.
        // if (this.SearchType == 2) { if (isHaveAnd == true)
        qo.addOrderByDesc("FlowStartRDT");
        DataTable dt = qo.DoQueryToTable();
        dt.TableName = "dt";

        if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
        {
            dt.Columns.get("oid").ColumnName = "OID";
            dt.Columns.get("title").ColumnName = "Title";
            dt.Columns.get("wfstate").ColumnName = "WFState";
            dt.Columns.get("flowemps").ColumnName = "No";
            dt.Columns.get("billno").ColumnName = "BillNo";
            dt.Columns.get("flowstarter").ColumnName = "FlowStarter";
            dt.Columns.get("flowstartrdt").ColumnName = "FlowStartRDT";
        }

        return bp.tools.Json.ToJson(dt);
    }

    public final String GroupFlow_Init() throws Exception {
        DataSet ds = new DataSet();
        String frmID = "FlowRpt" + this.getFlowNo();
        MapData md = new MapData();
        md.setNo(frmID);
        if (md.RetrieveFromDBSources() == 0) {
            SearchFlow_InitFields();
            md.RetrieveFromDBSources();
        }
        //表单中的字段属性
        MapAttrs attrs = new MapAttrs();
        attrs.Retrieve(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.Idx);
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
        ///#region 分组条件
        String groupBy = this.GetRequestVal("GroupBy");
        if (DataType.IsNullOrEmpty(groupBy) == true) {
            groupBy = "";
        }
        DataTable dt = new DataTable("Group_MapAttr");
        dt.Columns.Add("Field", String.class);
        dt.Columns.Add("Name", String.class);
        dt.Columns.Add("Checked", Boolean.class);
        for (MapAttr attr : attrs.ToJavaList()) {
            if (attr.getMyDataType() == DataType.AppBoolean || attr.getUIContralType() == UIContralType.DDL || attr.getUIContralType() == UIContralType.RadioBtn) {
                DataRow dr = dt.NewRow();
                dr.setValue("Field", attr.getKeyOfEn());
                dr.setValue("Name", attr.getHisAttr().getDesc());

                // 根据状态 设置信息.
                if (groupBy.indexOf(attr.getKeyOfEn()) != -1) {
                    dr.setValue("Checked", true);
                } else {
                    dr.setValue("Checked", false);
                }
                dt.Rows.add(dr);
            }
        }
        ds.Tables.add(dt);
        ///#endregion 分组条件
        ///#region 分析项目
        String searchField = this.GetRequestVal("SearchField");
        if (DataType.IsNullOrEmpty(searchField) == true) {
            searchField = "";
        }
        dt = new DataTable("Analysis_MapAttr");
        dt.Columns.Add("Field", String.class);
        dt.Columns.Add("Name", String.class);
        dt.Columns.Add("Checked", Boolean.class);

        //如果不存在分析项手动添加一个分析项
        DataRow dtr = dt.NewRow();
        dtr.setValue("Field", "Group_Number");
        dtr.setValue("Name", "数量");
        dtr.setValue("Checked", "true");
        dt.Rows.add(dtr);

        DataTable ddlDt = new DataTable();
        ddlDt.TableName = "Group_Number";
        ddlDt.Columns.Add("No");
        ddlDt.Columns.Add("Name");
        ddlDt.Columns.Add("Selected", Boolean.class);
        DataRow ddlDr = ddlDt.NewRow();
        ddlDr.setValue("No", "SUM");
        ddlDr.setValue("Name", "求和");
        ddlDr.setValue("Selected", true);
        ddlDt.Rows.add(ddlDr);
        ds.Tables.add(ddlDt);
        for (MapAttr attr : attrs.ToJavaList()) {
            if (attr.getItIsPK() || attr.getItIsNum() == false) {
                continue;
            }
            if (attr.getUIContralType() != UIContralType.TB) {
                continue;
            }
            if (attr.getUIVisible() == false) {
                continue;
            }
            if (attr.getHisAttr().getMyFieldType() == FieldType.FK) {
                continue;
            }
            if (attr.getHisAttr().getMyFieldType() == FieldType.Enum) {
                continue;
            }
            if(attr.getMyDataType() == DataType.AppBoolean)
                continue;
            if (attr.getKeyOfEn().equals("OID") || attr.getKeyOfEn().equals("WorkID") || attr.getKeyOfEn().equals("MID")
                    || attr.getKeyOfEn().equals("PWorkID") || attr.getKeyOfEn().equals("PNodeID") || attr.getKeyOfEn().equals("FID")) {
                continue;
            }

            dtr = dt.NewRow();
            dtr.setValue("Field", attr.getKeyOfEn());
            dtr.setValue("Name", attr.getHisAttr().getDesc());


            // 根据状态 设置信息.
            if (searchField.indexOf(attr.getKeyOfEn()) != -1) {
                dtr.setValue("Checked", true);
            } else {
                dtr.setValue("Checked", false);
            }
            dt.Rows.add(dtr);

            ddlDt = new DataTable();
            ddlDt.Columns.Add("No");
            ddlDt.Columns.Add("Name");
            ddlDt.Columns.Add("Selected");
            ddlDt.TableName = attr.getKeyOfEn();

            ddlDr = ddlDt.NewRow();
            ddlDr.setValue("No", "SUM");
            ddlDr.setValue("Name", "求和");
            if (searchField.indexOf("@" + attr.getKeyOfEn() + "=SUM") != -1) {
                ddlDr.setValue("Selected", "true");
            }
            ddlDt.Rows.add(ddlDr);

            ddlDr = ddlDt.NewRow();
            ddlDr.setValue("No", "AVG");
            ddlDr.setValue("Name", "求平均");
            if (searchField.indexOf("@" + attr.getKeyOfEn() + "=AVG") != -1) {
                ddlDr.setValue("Selected", "true");
            }
            ddlDt.Rows.add(ddlDr);
            ds.Tables.add(ddlDt);

        }
        ds.Tables.add(dt);
        ///#endregion 分析项目
        return bp.tools.Json.ToJson(ds);
    }

    public final String GroupFlow_Search() throws Exception {
        DataSet ds = new DataSet();
        String frmID = "FlowRpt" + this.getFlowNo();
        GEEntitys ges = new GEEntitys(frmID);

        //表单中的字段属性
        MapAttrs attrs = new MapAttrs();
        attrs.Retrieve(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.Idx);


        String dtFrom = this.GetRequestVal("DTFrom");
        String dtTo = this.GetRequestVal("DTTo");
        String keyWord = this.GetRequestVal("KeyWords");

        MapAttrs AttrsOfNum = new MapAttrs(); //列
        String groupKey = "";
        String Condition = ""; //处理特殊字段的条件问题。

        //根据注册表信息获取里面的分组信息
        String StateNumKey = this.GetRequestVal("StateNumKey");
        String[] statNumKeys = StateNumKey.split("[@]", -1);
        for (String ct : statNumKeys) {
            if (ct.split("[=]", -1).length != 2) {
                continue;
            }
            String[] paras = ct.split("[=]", -1);

            //判断paras[0]的类型
            int dataType = 2;
            if (paras[0].equals("Group_Number")) {
                AttrsOfNum.AddEntity((new Attr("Group_Number", "Group_Number", 1, DataType.AppInt, false, "数量")).getToMapAttr());
            } else {
                Object tempVar = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, paras[0]);
                MapAttr attr = tempVar instanceof MapAttr ? (MapAttr) tempVar : null;
                AttrsOfNum.AddEntity(attr);
                dataType = attr.getMyDataType();
            }

            if (paras[0].equals("Group_Number")) {
                groupKey += " count(*) \"" + paras[0] + "\",";
            } else {
                switch (paras[1]) {
                    case "SUM":
                        if (dataType == 2) {
                            groupKey += " SUM(" + paras[0] + ") \"" + paras[0] + "\",";
                        } else {
                            groupKey += " round ( SUM(" + paras[0] + "), 4) \"" + paras[0] + "\",";
                        }
                        break;
                    case "AVG":
                        groupKey += " round (AVG(" + paras[0] + "), 4)  \"" + paras[0] + "\",";
                        break;
                    case "AMOUNT":
                        if (dataType == 2) {
                            groupKey += " SUM(" + paras[0] + ") \"" + paras[0] + "\",";
                        } else {
                            groupKey += " round ( SUM(" + paras[0] + "), 4) \"" + paras[0] + "\",";
                        }
                        break;
                    default:
                        throw new RuntimeException("没有判断的情况.");
                }

            }

        }
        boolean isHaveLJ = false; // 是否有累计字段。
        if (StateNumKey.indexOf("AMOUNT@") != -1) {
            isHaveLJ = true;
        }

        if (Objects.equals(groupKey, "")) {
            return null;
        }

        /* 如果包含累计数据，那它一定需要一个月份字段。业务逻辑错误。*/
        groupKey = groupKey.substring(0, groupKey.length() - 1);
        Paras ps = new Paras();
        // 生成 sql.
        String selectSQL = "SELECT ";
        String groupBy = " GROUP BY ";
        MapAttrs AttrsOfGroup = new MapAttrs();

        String SelectedGroupKey = this.GetRequestVal("SelectedGroupKey"); // 为保存操作状态的需要。
        MapAttr mapAttr = null;
        if (!DataType.IsNullOrEmpty(SelectedGroupKey)) {
            String[] SelectedGroupKeys = SelectedGroupKey.split("[@]", -1);
            for (String key : SelectedGroupKeys) {
                if (key.contains("=") == true) {
                    continue;
                }
                selectSQL += key + " \"" + key + "\",";
                groupBy += key + ",";
                // 加入组里面。
                Object tempVar2 = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, key);
                mapAttr = tempVar2 instanceof MapAttr ? (MapAttr) tempVar2 : null;
                AttrsOfGroup.AddEntity(mapAttr);
                //布尔类型
                if (mapAttr.getMyDataType() == DataType.AppBoolean)
                {
                    selectSQL += " CASE " + ges.getNewEntity().getEnMap().getPhysicsTable() + "." + mapAttr.getKeyOfEn();
                    selectSQL += " WHEN 0 THEN '否'";
                    selectSQL += " WHEN 1 THEN '是'";
                    selectSQL += " END \"" + mapAttr.getKeyOfEn() + "T\",";
                    continue;
                }
                //外键，枚举
                if (mapAttr.getLGType() == FieldTypeS.FK || mapAttr.getLGType() == FieldTypeS.Enum)
                    continue;
                //外部数据源
                if (mapAttr.getUIBindKey().contains(".") == false)
                {
                    selectSQL += key + "T" + " \"" + key + "T\",";
                    groupBy += key+"T" + ",";
                }
            }
        }


        groupBy = groupBy.substring(0, groupBy.length() - 1);

        if (groupBy.equals(" GROUP BY")) {
            return null;
        }


        String orderByReq = this.GetRequestVal("OrderBy");

        String orderby = "";

        if (orderByReq != null && (selectSQL.contains(orderByReq) || groupKey.contains(orderByReq))) {
            orderby = " ORDER BY " + orderByReq;
            String orderWay = this.GetRequestVal("OrderWay");
            if (!DataType.IsNullOrEmpty(orderWay) && !orderWay.equals("Up")) {
                orderby += " DESC ";
            }
        }

        //查询语句
        QueryObject qo = new QueryObject(ges);

        ///#region 控制状态.
        //已经完成的.
        if (this.getWFSta() == 1) {
            qo.AddWhere(NDXRptBaseAttr.WFState, "=", 3); //必须的条件.
        }

        //进行中的.
        if (this.getWFSta() == 0) {
            qo.addSQL(NDXRptBaseAttr.WFState + "!=3");
            qo.addAnd();
            qo.addSQL(NDXRptBaseAttr.WFState + ">=1");
        }

        //全部的.
        if (this.getWFSta() == 2 || this.getWFSta() == 3) {
            qo.AddWhere(NDXRptBaseAttr.WFState, ">=", 1); //必须的条件.
        }
        ///#endregion 控制状态.

        ///#region 时间范围.
        if (DataType.IsNullOrEmpty(dtTo) == false) {
            qo.addAnd();
            qo.addLeftBracket();
            qo.AddWhere(NDXRptBaseAttr.FlowStartRDT, ">=", dtFrom);
            qo.addAnd();
            qo.setSQL("(  FlowStartRDT <=  '" + dtTo + "' )");
            qo.addRightBracket();
        }
        ///#endregion 时间范围.

        ///#region 权限范围..
        //我参与的
        if (this.getSearchType() == 0) {
            qo.addAnd();
            qo.AddWhere(bp.wf.GERptAttr.FlowEmps, " LIKE ", "%" + WebUser.getNo() + "%");
        }

        //我发起的
        if (this.getSearchType() == 1) {
            qo.addAnd();
            qo.AddWhere(bp.wf.GERptAttr.FlowStarter, WebUser.getNo());
        }
        ///#endregion 权限范围..

        ///#region 关键字..
        if (DataType.IsNullOrEmpty(keyWord) == false) {
            qo.addAnd();
            qo.AddWhere(bp.wf.GERptAttr.Title, " LIKE ", "%" + keyWord + "%");
        }
        ///#endregion 关键字..
        //执行查询
        DataTable dt2 = qo.DoGroupQueryToTable(selectSQL + groupKey, groupBy, orderby);

        DataTable dt1 = dt2.clone();

        dt1.Columns.Add("IDX", Integer.class);

        ///#region 对他进行分页面

        int myIdx = 0;
        for (DataRow dr : dt2.Rows) {
            myIdx++;
            DataRow mydr = dt1.NewRow();
            mydr.setValue("IDX", myIdx);
            for (DataColumn dc : dt2.Columns) {
                mydr.setValue(dc.ColumnName, dr.getValue(dc.ColumnName));
            }
            dt1.Rows.add(mydr);
        }
        ///#endregion

        ///#region 处理 Int 类型的分组列。
        DataTable dt = dt1.clone();
        dt.TableName = "GroupSearch";
        dt.Rows.clear();
        for (MapAttr attr : AttrsOfGroup.ToJavaList()) {
            dt.Columns.get(attr.getKeyOfEn()).DataType = String.class;
        }
        DataRow dr1=null;
		for (DataRow dr : dt1.Rows)
		{
            dr1 = dt1.NewRow();
            for (DataColumn dc : dt2.Columns) {
                dr1.setValue(dc.ColumnName, dr.getValue(dc.ColumnName));
            }
            dt.Rows.add(dr1);
		}
        ///#endregion

        // 处理这个物理表 , 如果有累计字段, 就扩展它的列。
        if (isHaveLJ) {
            // 首先扩充列.
            for (MapAttr attr : AttrsOfNum.ToJavaList()) {
                if (StateNumKey.indexOf(attr.getKeyOfEn() + "=AMOUNT") == -1) {
                    continue;
                }

                switch (attr.getMyDataType()) {
                    case DataType.AppInt:
                        dt.Columns.Add(attr.getKeyOfEn() + "Amount", Integer.class);
                        break;
                    default:
                        dt.Columns.Add(attr.getKeyOfEn() + "Amount", BigDecimal.class);
                        break;
                }
            }

            String sql = "";
            String whereOFLJ = "";
            AtPara ap = new AtPara("");
            /** #region 获得查询数据.
             */
            for (String str : ap.getHisHT().keySet()) {
                Object val = ap.GetValStrByKey(str);
                if (val.equals("all")) {
                    continue;
                }
                if (!Objects.equals(str, "FK_NY")) {
                    whereOFLJ += " " + str + " =" + SystemConfig.getAppCenterDBVarStr() + str + "   AND ";
                }

            }

            // 添加累计汇总数据.
            for (DataRow dr : dt.Rows) {
                for (MapAttr attr : AttrsOfNum.ToJavaList()) {
                    if (StateNumKey.indexOf(attr.getKeyOfEn() + "=AMOUNT") == -1) {
                        continue;
                    }

                    //形成查询sql.
                    if (whereOFLJ.length() > 0) {
                        sql = "SELECT SUM(" + attr.getKeyOfEn() + ") FROM " + ges.getNewEntity().getEnMap().getPhysicsTable() + whereOFLJ + " AND ";
                    } else {
                        sql = "SELECT SUM(" + attr.getKeyOfEn() + ") FROM " + ges.getNewEntity().getEnMap().getPhysicsTable() + " WHERE ";
                    }

                    for (MapAttr attr1 : AttrsOfGroup.ToJavaList()) {
                        switch (attr1.getKeyOfEn()) {
                            case "FK_NY":
                                sql += " FK_NY <= '" + dr.getValue("FK_NY") + "' AND FK_ND='" + dr.getValue("FK_NY").toString().substring(0, 4) + "' AND ";
                                break;
                            case "FK_Dept":
                                sql += attr1.getKeyOfEn() + "='" + dr.getValue(attr1.getKeyOfEn()) + "' AND ";
                                break;
                            case "FK_SJ":
                            case "FK_XJ":
                                sql += attr1.getKeyOfEn() + " LIKE '" + dr.getValue(attr1.getKeyOfEn()) + "%' AND ";
                                break;
                            default:
                                sql += attr1.getKeyOfEn() + "='" + dr.getValue(attr1.getKeyOfEn()) + "' AND ";
                                break;
                        }
                    }

                    sql = sql.substring(0, sql.length() - "AND ".length());
                    if (attr.getMyDataType() == DataType.AppInt) {
                        dr.setValue(attr.getKeyOfEn() + "Amount", DBAccess.RunSQLReturnValInt(sql, 0));
                    } else {
                        dr.setValue(attr.getKeyOfEn() + "Amount", DBAccess.RunSQLReturnValDecimal(sql, new BigDecimal("0"), 2));
                    }
                }
            }
        }

        // 为表扩充外键
        for (MapAttr attr : AttrsOfGroup.ToJavaList()) {
            //外键，枚举
            if (attr.getLGType() == FieldTypeS.FK || attr.getLGType() == FieldTypeS.Enum || attr.getKeyOfEn().equals("WFState"))
            {
                dt.Columns.Add(attr.getKeyOfEn() + "T", String.class);
                continue;
            }
            continue;

        }
        for (MapAttr attr : AttrsOfGroup.ToJavaList()) {
            if (attr.getKeyOfEn().equals("WFState") == true) {
                attr.SetValByKey("UIBindKey", "WFState");
            }
            /* 说明它是枚举类型 */
            if(attr.getLGType() == FieldTypeS.Enum || attr.getKeyOfEn().equals("WFState") == true) {

                SysEnums ses = new SysEnums(attr.getUIBindKey());
                for (DataRow dr : dt.Rows) {
                    int val = 0;
                    try {
                        val = Integer.parseInt(dr.getValue(attr.getKeyOfEn()).toString());
                    } catch (Exception e) {
                        dr.setValue(attr.getKeyOfEn() + "T", " ");
                        continue;
                    }

                    for (SysEnum se : ses.ToJavaList()) {
                        if (se.getIntKey() == val) {
                            dr.setValue(attr.getKeyOfEn() + "T", se.getLab());
                        }
                    }
                }
                continue;
            }
            if(attr.getLGType() == FieldTypeS.FK) {
                for (DataRow dr : dt.Rows) {
                    Entity myen = attr.getHisAttr().getHisFKEn();
                    String val = dr.getValue(attr.getKeyOfEn()).toString();
                    myen.SetValByKey(attr.getKeyOfEn() + "Text", val);
                    try {
                        myen.Retrieve();
                        dr.setValue(attr.getKeyOfEn() + "T", myen.GetValStrByKey(attr.getUIRefKeyText()));
                    } catch (Exception e) {
                        if (val == null || val.length() <= 1) {
                            dr.setValue(attr.getKeyOfEn() + "T", val);
                        } else if (Objects.equals(val.substring(0, 2), "63")) {
                            try {
                                Dept Dept = new Dept(val);
                                dr.setValue(attr.getKeyOfEn() + "T", Dept.getName());
                            } catch (Exception e2) {
                                dr.setValue(attr.getKeyOfEn() + "T", val);
                            }
                        } else {
                            dr.setValue(attr.getKeyOfEn() + "T", val);
                        }
                    }
                }
            }
        }
        ds.Tables.add(dt);
        ds.Tables.add(AttrsOfNum.ToDataTableField("AttrsOfNum"));
        ds.Tables.add(AttrsOfGroup.ToDataTableField("AttrsOfGroup"));
        return bp.tools.Json.ToJson(ds);
    }

    public String ContrastDtlFlow_Init() throws Exception {
        String frmID = "FlowRpt" + this.getFlowNo();

        MapData md = new MapData();
        md.setNo(frmID);
        if (md.RetrieveFromDBSources() == 0) {
            SearchFlow_InitFields();
            md.RetrieveFromDBSources();
        }

        String dtFrom = this.GetRequestVal("DTFrom");
        String dtTo = this.GetRequestVal("DTTo");
        String keyWord = this.GetRequestVal("KeyWords");

        //数据.
        GEEntitys ges = new GEEntitys(frmID);

        //设置查询条件.
        QueryObject qo = new QueryObject(ges);

        ///#region 控制状态.
        //已经完成的.
        if (this.getWFSta() == 1)
            qo.addSQL(NDXRptBaseAttr.WFState + "=3"); //必须的条件.

        //进行中的.
        if (this.getWFSta() == 0) {
            qo.addSQL(NDXRptBaseAttr.WFState + "!=3");
            qo.addAnd();
            qo.addSQL(NDXRptBaseAttr.WFState + ">=1");
        }

        //全部的.
        if (this.getWFSta() == 2)
            qo.addSQL(NDXRptBaseAttr.WFState + ">=1"); //必须的条件.
        ///#endregion 控制状态.

        ///#region 时间范围.
        if (DataType.IsNullOrEmpty(dtTo) == false) {
            qo.addAnd();
            qo.addLeftBracket();
            qo.AddWhere(NDXRptBaseAttr.FlowStartRDT, ">=", dtFrom);
            qo.addAnd();
            qo.setSQL("(  FlowStartRDT <=  '" + dtTo + "' )");
            qo.addRightBracket();
        }
        ///#endregion 时间范围.

        ///#region 权限范围..
        //我参与的
        if (this.getSearchType() == 0) {
            qo.addAnd();
            qo.AddWhere(bp.wf.GERptAttr.FlowEmps, " LIKE ", "%" + WebUser.getNo() + "%");
        }

        //我发起的
        if (this.getSearchType() == 1) {
            qo.addAnd();
            qo.AddWhere(bp.wf.GERptAttr.FlowStarter, WebUser.getNo());
        }
        ///#endregion 权限范围..

        ///#region 关键字..
        if (DataType.IsNullOrEmpty(keyWord) == false) {
            qo.addAnd();
            qo.AddWhere(bp.wf.GERptAttr.Title, " LIKE ", "%" + keyWord + "%");
        }
        ///#endregion 关键字..

        ///#region 其他条件
        Enumeration<String> paramNames = getRequest().getParameterNames();
        while (paramNames.hasMoreElements()){
            String key = paramNames.nextElement();
            if (key.equals("FlowNo") || key.equals("DTFrom") || key.equals("DTTo") || key.equals("KeyWords") || key.equals("WFSta") || key.equals("SearchType"))
                continue;
            if(key.equals("DoType") || key.equals("DoMethod") || key.equals("HttpHandlerName") || key.equals("t") || key.equals("Token"))
                continue;
            String val = this.GetRequestVal(key);
            qo.addAnd();
            qo.AddWhere(key, val);
        }

        ///#endregion 其他条件

        DataTable dt = qo.DoQueryToTable();
        dt.TableName = "dt";
        return bp.tools.Json.ToJson(dt);
    }
}
