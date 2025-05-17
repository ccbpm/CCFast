package bp.App.ShuCai;

import bp.demo.QingJiaAttr;
import bp.en.EntityOID;
import bp.en.Map;

public class ND10Rpt  extends EntityOID {
    private static final long serialVersionUID = 1L;

    public final String getHCTBBH()  {
        return this.GetValStringByKey(ND10RptAttr.HCTBBH);
    }
    public final void setHCTBBH(String value){
        this.SetValByKey(ND10RptAttr.HCTBBH, value);
    }

    public final String getSCJDTBBH()  {
        return this.GetValStringByKey(ND10RptAttr.SCJDTBBH);
    }
    public final void setSCJDTBBH(String value){
        this.SetValByKey(ND10RptAttr.SCJDTBBH, value);
    }

    public final int getBatchNum()  {return this.GetValIntByKey(ND10RptAttr.BatchNum);
    }
    public final void setBatchNum(int value){this.SetValByKey(ND10RptAttr.BatchNum, value);
    }

    public final int getDKSta()  {return this.GetValIntByKey(ND10RptAttr.DKSta);
    }
    public final void setDKSta(int value){this.SetValByKey(ND10RptAttr.DKSta, value);
    }

    public final String getSQL_HeChaQiShu()  {
        return this.GetValStringByKey(ND10RptAttr.SQL_HeChaQiShu);
    }
    public final void setSQL_HeChaQiShu(String value){
        this.SetValByKey(ND10RptAttr.SQL_HeChaQiShu, value);
    }

    public final String getSQL_HeChaQiShuT()  {
        return this.GetValStringByKey(ND10RptAttr.SQL_HeChaQiShuT);
    }
    public final void setSQL_HeChaQiShuT(String value){
        this.SetValByKey(ND10RptAttr.SQL_HeChaQiShuT, value);
    }

    public final String getGEOMETRY()  {
        return this.GetValStringByKey(ND10RptAttr.GEOMETRY);
    }
    public final void setGEOMETRY(String value){
        this.SetValByKey(ND10RptAttr.GEOMETRY, value);
    }

    public final String getSQL_ShiQuXian()  {
        return this.GetValStringByKey(ND10RptAttr.SQL_ShiQuXian);
    }
    public final void setSQL_ShiQuXian(String value){
        this.SetValByKey(ND10RptAttr.SQL_ShiQuXian, value);
    }
    public final String getSQL_ShiQuXianT()  {
        return this.GetValStringByKey(ND10RptAttr.SQL_ShiQuXianT);
    }
    public final void setSQL_ShiQuXianT(String value){
        this.SetValByKey(ND10RptAttr.SQL_ShiQuXianT, value);
    }

    public final String getSQL_XiangZhen()  {
        return this.GetValStringByKey(ND10RptAttr.SQL_XiangZhen);
    }
    public final void setSQL_XiangZhen(String value){
        this.SetValByKey(ND10RptAttr.SQL_XiangZhen, value);
    }
    public final String getSQL_XiangZhenT()  {
        return this.GetValStringByKey(ND10RptAttr.SQL_XiangZhenT);
    }
    public final void setSQL_XiangZhenT(String value){
        this.SetValByKey(ND10RptAttr.SQL_XiangZhenT, value);
    }
    public final String getSHuoShuSX()  {
        return this.GetValStrByKey(BasicInfoAttr.SHuoShuSX);
    }
    public final void setSHuoShuSX(String value){
        this.SetValByKey(BasicInfoAttr.SHuoShuSX, value);
    }
    public final String getJDBH()  {
        return this.GetValStrByKey(ND10RptAttr.JDBH);
    }
    public final void setJDBH(String value){
        this.SetValByKey(ND10RptAttr.JDBH, value);
    }

    public final String getJDMC()  {
        return this.GetValStrByKey(ND10RptAttr.JDMC);
    }
    public final void setJDMC(String value){
        this.SetValByKey(ND10RptAttr.JDMC, value);
    }

    public final String getJDTBMJ()  {
        return this.GetValStrByKey(ND10RptAttr.JDTBMJ);
    }
    public final void setJDTBMJ(String value){
        this.SetValByKey(ND10RptAttr.JDTBMJ, value);
    }

    public final String getHCTBMJ()  {
        return this.GetValStrByKey(ND10RptAttr.HCTBMJ);
    }
    public final void setHCTBMJ(String value){
        this.SetValByKey(ND10RptAttr.HCTBMJ, value);
    }

    /**
     * 重写基类方法
     */
    @Override
    public Map getEnMap()
    {
        if (this.get_enMap() != null)
        {
            return this.get_enMap();
        }

        Map map = new Map("ND10Rpt","蔬菜基地核查表");
        map.AddTBIntPKOID();

        map.AddTBString(ND10RptAttr.HCTBBH, null, "核查图斑编号", true, false, 0, 200, 100);
        map.AddTBInt(ND10RptAttr.BatchNum, 0, "拆分号", true, false);
        map.AddTBInt(ND10RptAttr.ObjectId, 0, ND10RptAttr.ObjectId, true, false);
        map.AddTBString(ND10RptAttr.SCJDTBBH, null, "蔬菜基地图斑编号", true, false, 0, 200, 100);
        map.AddDDLEntities(ND10RptAttr.SQL_HeChaQiShu, null, "核查期数", new HeChaQiShus(), true);
        map.AddTBString(ND10RptAttr.SQL_HeChaQiShuT, null, "核查期数", false, true, 0, 200, 100);
        map.AddTBString(ND10RptAttr.JDBH, null, "基地编号", true, false, 0, 200, 100);
        map.AddTBString(ND10RptAttr.JDMC, null, "基地名称", true, false, 0, 200, 100);
        map.AddTBString(ND10RptAttr.GEOMETRY, null, "基地坐标", true, false, 0, 5000, 100);
        map.AddTBFloat(ND10RptAttr.JDTBMJ, 0, "基地图斑面积(亩)", true, true);
        map.AddTBFloat(ND10RptAttr.HCTBMJ, 0, "核查图斑面积(亩)", true, true);
        map.AddTBString(ND10RptAttr.SQL_ShiQuXian, null, "市区县编号", true, false, 0, 200, 100);
        map.AddTBString(ND10RptAttr.SQL_ShiQuXianT, null, "市区县名称", true, false, 0, 200, 100);
        map.AddTBString(ND10RptAttr.SQL_XiangZhen, null, "乡镇编号", true, false, 0, 200, 100);
        map.AddTBString(ND10RptAttr.SQL_XiangZhenT, null, "乡镇名称", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.SHuoShuSX, null, "所属市县", true, false, 0, 200, 100);
        map.AddDDLSysEnum(ND10RptAttr.DKSta, 0, "图斑状态", true, false, ND10RptAttr.DKSta, "@0=未提交123@1=已提交@2=已审核");
        this.set_enMap(map);
        return this.get_enMap();
    }
}
