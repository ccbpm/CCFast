package bp.wf;

import bp.da.*;
import bp.en.*;

/**
 * 退回轨迹
 */
public class ReturnWork extends EntityMyPK {

    ///#region 基本属性

    /**
     * 工作ID
     */
    public final long getWorkID() throws Exception {
        return this.GetValInt64ByKey(ReturnWorkAttr.WorkID);
    }

    public final void setWorkID(long value) throws Exception {
        SetValByKey(ReturnWorkAttr.WorkID, value);
    }

    /**
     * FID
     */
    public final long getFID() throws Exception {
        return this.GetValInt64ByKey(ReturnWorkAttr.FID);
    }

    public final void setFID(long value) throws Exception {
        SetValByKey(ReturnWorkAttr.FID, value);
    }

    /**
     * 退回到节点
     */
    public final int getReturnToNode() throws Exception {
        return this.GetValIntByKey(ReturnWorkAttr.ReturnToNode);
    }

    public final void setReturnToNode(int value) throws Exception {
        SetValByKey(ReturnWorkAttr.ReturnToNode, value);
    }

    /**
     * 退回节点
     */
    public final int getReturnNode() throws Exception {
        return this.GetValIntByKey(ReturnWorkAttr.ReturnNode);
    }

    public final void setReturnNode(int value) throws Exception {
        SetValByKey(ReturnWorkAttr.ReturnNode, value);
    }

    public final String getReturnNodeName() throws Exception {
        return this.GetValStrByKey(ReturnWorkAttr.ReturnNodeName);
    }

    public final void setReturnNodeName(String value) throws Exception {
        SetValByKey(ReturnWorkAttr.ReturnNodeName, value);
    }

    /**
     * 退回人
     */
    public final String getReturner() throws Exception {
        return this.GetValStringByKey(ReturnWorkAttr.Returner);
    }

    public final void setReturner(String value) throws Exception {
        SetValByKey(ReturnWorkAttr.Returner, value);
    }

    public final String getReturnerName() throws Exception {
        return this.GetValStringByKey(ReturnWorkAttr.ReturnerName);
    }

    public final void setReturnerName(String value) throws Exception {
        SetValByKey(ReturnWorkAttr.ReturnerName, value);
    }

    /**
     * 退回给
     */
    public final String getReturnToEmp() throws Exception {
        return this.GetValStringByKey(ReturnWorkAttr.ReturnToEmp);
    }

    public final void setReturnToEmp(String value) throws Exception {
        SetValByKey(ReturnWorkAttr.ReturnToEmp, value);
    }

    public final String getBeiZhu() throws Exception {
        return this.GetValStringByKey(ReturnWorkAttr.BeiZhu);
    }

    public final void setBeiZhu(String value) throws Exception {
        SetValByKey(ReturnWorkAttr.BeiZhu, value);
    }

    public final String getBeiZhuHtml() throws Exception {
        return this.GetValHtmlStringByKey(ReturnWorkAttr.BeiZhu);
    }

    /**
     * 记录日期
     */
    public final String getRDT() throws Exception {
        return this.GetValStringByKey(ReturnWorkAttr.RDT);
    }

    public final void setRDT(String value) throws Exception {
        SetValByKey(ReturnWorkAttr.RDT, value);
    }

    /**
     * 是否要原路返回？
     */
    public final boolean isBackTracking() throws Exception {
        return this.GetValBooleanByKey(ReturnWorkAttr.IsBackTracking);
    }

    public final void setBackTracking(boolean value) throws Exception {
        SetValByKey(ReturnWorkAttr.IsBackTracking, value);
    }

    ///#endregion


    ///#region 构造函数

    /**
     * 退回轨迹
     */
    public ReturnWork() {
    }

    /**
     * 重写基类方法
     */
    @Override
    public bp.en.Map getEnMap() {
        if (this.get_enMap() != null) {
            return this.get_enMap();
        }

        Map map = new Map("WF_ReturnWork", "退回轨迹");

        map.AddMyPK(true);

        map.AddTBInt(ReturnWorkAttr.WorkID, 0, "WorkID", true, true);
        map.AddTBInt(ReturnWorkAttr.FID, 0, "FID", true, true);

        map.AddTBInt(ReturnWorkAttr.ReturnNode, 0, "退回节点", true, true);
        map.AddTBString(ReturnWorkAttr.ReturnNodeName, null, "退回节点名称", true, true, 0, 100, 10);

        map.AddTBString(ReturnWorkAttr.Returner, null, "退回人", true, true, 0, 50, 10);
        map.AddTBString(ReturnWorkAttr.ReturnerName, null, "退回人名称", true, true, 0, 100, 10);

        map.AddTBInt(ReturnWorkAttr.ReturnToNode, 0, "退回到的节点", true, true);
        map.AddTBString(ReturnWorkAttr.ReturnToEmp, null, "退回给", true, true, 0, 4000, 10);

        //如果是frm类型，就是格式化的json数据.
        map.AddTBString(ReturnWorkAttr.BeiZhu, null, "退回原因", true, true, 0, 4000, 10);
        map.AddTBDateTime(ReturnWorkAttr.RDT, null, "退回日期", true, true);

        map.AddTBInt(ReturnWorkAttr.IsBackTracking, 0, "是否要原路返回", true, true);
        //    map.AddTBInt(ReturnWorkAttr.IsBackResetAccepter, 0, "是否要原路返回", true, true);

        // map.AddTBString(ReturnWorkAttr.FrmDB, null, "退回的Frm格式化数据", true, true, 0, 3999, 10);
        // map.AddAttrsFromMapData("BP.WF.ReturnWorks");
        this.set_enMap(map);
        return this.get_enMap();
    }

    ///#endregion

    @Override
    protected boolean beforeInsert() throws Exception {
        this.setReturner(bp.web.WebUser.getNo());
        this.setReturnerName(bp.web.WebUser.getName());

        this.setRDT(DataType.getCurrentDateTime());
        return super.beforeInsert();
    }
}