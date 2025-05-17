package bp.wf.rpt;

import bp.da.*;
import bp.port.*;
import bp.en.*;
import bp.en.Map;
import bp.sys.*;

/**
 * 报表设计
 */
public class MapRpt extends EntityNoName {

    ///#region 报表权限控制方式

    /**
     * 报表查看权限控制.
     */
    public final RightViewWay getRightViewWay() throws Exception {
        return RightViewWay.forValue(this.GetValIntByKey(MapRptAttr.RightViewWay));
    }

    public final void setRightViewWay(RightViewWay value) throws Exception {
        this.SetValByKey(MapRptAttr.RightViewWay, value.getValue());
    }

    /**
     * 报表查看权限控制-数据
     */
    public final String getRightViewTag() throws Exception {
        return this.GetValStringByKey(MapRptAttr.RightViewTag);
    }

    public final void setRightViewTag(String value) throws Exception {
        this.SetValByKey(MapRptAttr.RightViewTag, value);
    }

    /**
     * 报表部门权限控制.
     */
    public final RightDeptWay getRightDeptWay() throws Exception {
        return RightDeptWay.forValue(this.GetValIntByKey(MapRptAttr.RightDeptWay));
    }

    public final void setRightDeptWay(RightDeptWay value) throws Exception {
        this.SetValByKey(MapRptAttr.RightDeptWay, value.getValue());
    }

    /**
     * 报表部门权限控制-数据
     */
    public final String getRightDeptTag() throws Exception {
        return this.GetValStringByKey(MapRptAttr.RightDeptTag);
    }

    public final void setRightDeptTag(String value) throws Exception {
        this.SetValByKey(MapRptAttr.RightDeptTag, value);
    }

    ///#endregion 报表权限控制方式


    ///#region 外键属性

    /**
     * 框架
     */
    public final MapFrames getMapFrames() throws Exception {
        Object tempVar = this.GetRefObject("MapFrames");
        MapFrames obj = tempVar instanceof MapFrames ? (MapFrames) tempVar : null;
        if (obj == null) {
            obj = new MapFrames(this.getNo());
            this.SetRefObject("MapFrames", obj);
        }
        return obj;
    }


    /**
     * 图片
     */
    public final FrmImgs getFrmImgs() throws Exception {
        Object tempVar = this.GetRefObject("FrmLabs");
        FrmImgs obj = tempVar instanceof FrmImgs ? (FrmImgs) tempVar : null;
        if (obj == null) {
            obj = new FrmImgs(this.getNo());
            this.SetRefObject("FrmLabs", obj);
        }
        return obj;
    }

    /**
     * 附件
     */
    public final FrmAttachments getFrmAttachments() throws Exception {
        Object tempVar = this.GetRefObject("FrmAttachments");
        FrmAttachments obj = tempVar instanceof FrmAttachments ? (FrmAttachments) tempVar : null;
        if (obj == null) {
            obj = new FrmAttachments(this.getNo());
            this.SetRefObject("FrmAttachments", obj);
        }
        return obj;
    }

    /**
     * 图片附件
     */
    public final FrmImgAths getFrmImgAths() throws Exception {
        Object tempVar = this.GetRefObject("FrmImgAths");
        FrmImgAths obj = tempVar instanceof FrmImgAths ? (FrmImgAths) tempVar : null;
        if (obj == null) {
            obj = new FrmImgAths(this.getNo());
            this.SetRefObject("FrmImgAths", obj);
        }
        return obj;
    }

    /**
     * 单选按钮
     */
    public final FrmRBs getFrmRBs() throws Exception {
        Object tempVar = this.GetRefObject("FrmRBs");
        FrmRBs obj = tempVar instanceof FrmRBs ? (FrmRBs) tempVar : null;
        if (obj == null) {
            obj = new FrmRBs(this.getNo());
            this.SetRefObject("FrmRBs", obj);
        }
        return obj;
    }

    /**
     * 属性
     */
    public final MapAttrs getMapAttrs() throws Exception {
        Object tempVar = this.GetRefObject("MapAttrs");
        MapAttrs obj = tempVar instanceof MapAttrs ? (MapAttrs) tempVar : null;
        if (obj == null) {
            obj = new MapAttrs(this.getNo());
            this.SetRefObject("MapAttrs", obj);
        }
        return obj;
    }

    ///#endregion


    ///#region 属性

    /**
     * 流程编号
     */
    public final String getFlowNo() throws Exception {
        String str = this.GetValStrByKey(MapRptAttr.FK_Flow);
        return str;
    }

    public final void setFlowNo(String value) throws Exception {
        this.SetValByKey(MapRptAttr.FK_Flow, value);
    }

    /**
     * 物理表
     */
    public final String getPTable() throws Exception {
        String s = this.GetValStrByKey(MapRptAttr.PTable);
        if (DataType.IsNullOrEmpty(s) == true) {
            return this.getNo();
        }
        return s;
    }

    public final void setPTable(String value) throws Exception {
        this.SetValByKey(MapRptAttr.PTable, value);
    }

    /**
     * 备注
     */
    public final String getNote() throws Exception {
        return this.GetValStrByKey(MapRptAttr.Note);
    }

    public final void setNote(String value) throws Exception {
        this.SetValByKey(MapRptAttr.Note, value);
    }

    private Entities _HisEns = null;

    public final Entities getHisEns() throws Exception {
        if (_HisEns == null) {
            _HisEns = ClassFactory.GetEns(this.getNo());
        }
        return _HisEns;
    }

    public final Entity getHisEn() throws Exception {
        return this.getHisEns().getNewEntity();
    }

    ///#endregion


    ///#region 构造方法
    private GEEntity _HisEn = null;

    public final GEEntity getHisGEEn() throws Exception {
        if (this._HisEn == null) {
            _HisEn = new GEEntity(this.getNo());
        }
        return _HisEn;
    }

    /**
     * 报表设计
     */
    public MapRpt() {
    }

    /**
     * 报表设计
     *
     * @param no 映射编号
     */
    public MapRpt(String no, String flowNo) throws Exception {
        this.setNo(no);
        this.Retrieve();
        this.setFlowNo(flowNo);
    }

    /**
     * EnMap
     */
    @Override
    public Map getEnMap() {
        if (this.get_enMap() != null) {
            return this.get_enMap();
        }

        Map map = new Map("Sys_MapData", "报表设计");

        map.setDepositaryOfEntity(Depositary.Application);
        map.setCodeStruct("4");

        map.AddTBStringPK(MapRptAttr.No, null, "编号", true, false, 1, 200, 20);
        map.AddTBString(MapRptAttr.Name, null, "描述", true, false, 0, 500, 20);
        map.AddTBString(MapRptAttr.PTable, null, "物理表", true, false, 0, 500, 20);
        map.AddTBString(MapRptAttr.FK_Flow, null, "流程编号", true, false, 0, 4, 3);

        //Tag
        //map.AddTBString(MapRptAttr.Tag, null, "Tag", true, false, 0, 500, 20);
        //时间查询:用于报表查询.
        //  map.AddTBInt(MapRptAttr.IsSearchKey, 0, "是否需要关键字查询", true, false);
        //   map.AddTBInt(MapRptAttr.DTSearchWay, 0, "时间查询方式", true, false);
        //   map.AddTBString(MapRptAttr.DTSearchKey, null, "时间查询字段", true, false, 0, 200, 20);
        map.AddTBString(MapRptAttr.Note, null, "备注", true, false, 0, 500, 20);


        ///#region 权限控制. 2014-12-18
        map.AddTBInt(MapRptAttr.RightViewWay, 0, "报表查看权限控制方式", true, false);
        map.AddTBString(MapRptAttr.RightViewTag, null, "报表查看权限控制Tag", true, false, 0, 4000, 20);
        map.AddTBInt(MapRptAttr.RightDeptWay, 0, "部门数据查看控制方式", true, false);
        map.AddTBString(MapRptAttr.RightDeptTag, null, "部门数据查看控制Tag", true, false, 0, 4000, 20);

        map.getAttrsOfOneVSM().Add(new RptStations(), new Stations(), RptStationAttr.FK_Rpt, RptStationAttr.FK_Station, DeptAttr.Name, DeptAttr.No, "角色权限", Dot2DotModel.Default, null, null);
        map.getAttrsOfOneVSM().Add(new RptDepts(), new Depts(), RptDeptAttr.FK_Rpt, RptDeptAttr.FK_Dept, DeptAttr.Name, DeptAttr.No, "部门权限", Dot2DotModel.Default, null, null);
        map.getAttrsOfOneVSM().Add(new RptEmps(), new Emps(), RptEmpAttr.FK_Rpt, RptEmpAttr.FK_Emp, DeptAttr.Name, DeptAttr.No, "人员权限", Dot2DotModel.Default, null, null);

        ///#endregion 权限控制.

        //增加参数字段.
        map.AddTBAtParas(1000);

        this.set_enMap(map);
        return this.get_enMap();
    }

    ///#endregion


    ///#region 其他方法.

    /**
     * 显示的列.
     */
    public final MapAttrs getHisShowColsAttrs() throws Exception {
        MapAttrs mattrs = new MapAttrs(this.getNo());
        return mattrs;
    }

    /**
     * 删除之前.
     *
     * @return
     */
    @Override
    protected boolean beforeDelete() throws Exception {
        MapAttrs attrs = new MapAttrs();
        attrs.Delete(MapAttrAttr.FK_MapData, this.getNo());
        return super.beforeDelete();
    }

    ///#endregion 其他方法.
}
