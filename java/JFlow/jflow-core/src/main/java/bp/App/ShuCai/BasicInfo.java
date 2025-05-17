package bp.App.ShuCai;

import bp.en.EntityNoAttr;
import bp.en.EntityNoName;
import bp.en.Map;
import bp.sys.MapAttrAttr;

public class BasicInfo extends EntityNoName {

    public final String getNo()  {
        return this.GetValStrByKey(EntityNoAttr.No);
    }
    public final void setNo(String value){
        this.SetValByKey(EntityNoAttr.No, value);
    }

    public final String getZZZL()  {
        return this.GetValStrByKey(BasicInfoAttr.ZZZL);
    }
    public final void setZZZL(String value){
        this.SetValByKey(BasicInfoAttr.ZZZL, value);
    }

    public final String getHCQS()  {
        return this.GetValStrByKey(BasicInfoAttr.HCQS);
    }
    public final void setHCQS(String value){
        this.SetValByKey(BasicInfoAttr.HCQS, value);
    }

    public final int getISHC()  {
        return this.GetValIntByKey(BasicInfoAttr.ISHC);
    }
    public final void setISHC(int value)  {
        this.SetValByKey(BasicInfoAttr.ISHC, value);
    }

    public final int getDKSta()  {
        return this.GetValIntByKey(BasicInfoAttr.DKSta);
    }
    public final void setDKSta(int value)  {
        this.SetValByKey(BasicInfoAttr.DKSta, value);
    }

    public final String getQuXianNo()  {
        return this.GetValStrByKey(BasicInfoAttr.QuXianNo);
    }
    public final void setQuXianNo(String value){
        this.SetValByKey(BasicInfoAttr.QuXianNo, value);
    }
    public final String getQuXianNoT()  {
        return this.GetValStrByKey(BasicInfoAttr.QuXianNoT);
    }
    public final void setQuXianNoT(String value){
        this.SetValByKey(BasicInfoAttr.QuXianNoT, value);
    }

    public final String getXiangZhenNo()  {
        return this.GetValStrByKey(BasicInfoAttr.XiangZhenNo);
    }
    public final void setXiangZhenNo(String value){
        this.SetValByKey(BasicInfoAttr.XiangZhenNo, value);
    }

    public final String getXiangZhenNoT()  {
        return this.GetValStrByKey(BasicInfoAttr.XiangZhenNoT);
    }
    public final void setXiangZhenNoT(String value){
        this.SetValByKey(BasicInfoAttr.XiangZhenNoT, value);
    }
    public final String getSHuoShuSX()  {
        return this.GetValStrByKey(BasicInfoAttr.SHuoShuSX);
    }
    public final void setSHuoShuSX(String value){
        this.SetValByKey(BasicInfoAttr.SHuoShuSX, value);
    }
    public final String getJDBH()  {
        return this.GetValStrByKey(BasicInfoAttr.JDBH);
    }
    public final void setJDBH(String value){
        this.SetValByKey(BasicInfoAttr.JDBH, value);
    }

    public final String getJDMC()  {
        return this.GetValStrByKey(BasicInfoAttr.JDMC);
    }
    public final void setJDMC(String value){
        this.SetValByKey(BasicInfoAttr.JDMC, value);
    }

    public final String getJDMJ()  {
        return this.GetValStrByKey(BasicInfoAttr.JDMJ);
    }
    public final void setJDMJ(String value){
        this.SetValByKey(BasicInfoAttr.JDMJ, value);
    }
    public final String getTXMJ()  {
        return this.GetValStrByKey(BasicInfoAttr.TXMJ);
    }
    public final void setTXMJ(String value){
        this.SetValByKey(BasicInfoAttr.TXMJ, value);
    }

    public final String getTBBH()  {
        return this.GetValStrByKey(BasicInfoAttr.TBBH);
    }
    public final void setTBBH(String value){
        this.SetValByKey(BasicInfoAttr.TBBH, value);
    }

    public final int getOBJECTID()  {
        return this.GetValIntByKey(BasicInfoAttr.OBJECTID);
    }
    public final void setOBJECTID(int value){
        this.SetValByKey(BasicInfoAttr.OBJECTID, value);
    }
    public BasicInfo() {

    }
    public BasicInfo(String no) throws Exception {
        this.setNo(no);
        this.Retrieve();
    }
    @Override
    public Map getEnMap() {
        if (this.get_enMap() != null)
        {
            return this.get_enMap();
        }
        Map map = new Map("GIS_BasicInfo", "摸排基本信息");
        map.setItIsAutoGenerNo(true);

        map.AddTBStringPK(EntityNoAttr.No, null, "图斑编号", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.TBBH, null, "图斑编号", true, false, 0, 200, 100);
        map.AddTBInt(BasicInfoAttr.OBJECTID, 0, "objectid", true, false);
        map.AddDDLEntities(BasicInfoAttr.HCQS, null, "核查期数", new HeChaQiShus(), true);
        map.AddDDLSysEnum(BasicInfoAttr.ISHC, 0, "是否核查", true, true, BasicInfoAttr.ISHC, "@0=未核查123@1=已核查");
        //map.AddDDLEntities(BasicInfoAttr.QuXianNo, null, "市县", new QuXians(), true);
       // map.AddDDLStringEnum(BasicInfoAttr.DKSta, "0", "图斑状态", "@0=未提交@1=已提交@2=已审核", true);
        //map.AddDDLStringEnum(BasicInfoAttr.DKSta, "0", "图斑状态", "@0=未提交@1=已提交@2=已审核", true);
        map.AddTBString(BasicInfoAttr.DKStaT, null, "图斑状态", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.QuXianNo, null, "市区县", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.QuXianNoT, null, "市区县名称", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.XiangZhenNo, null, "乡镇", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.XiangZhenNoT, null, "乡镇名称", true, false, 0, 200, 100);
        // String sql="select No,Name,ParentNo from (select '460000' as No,'海南省' as Name, '0' as ParentNo from  Sys_City union select City_Code as No,City_Name as Name,Parent_Code as ParentNo from Sys_City where City_Level='1' or City_Level='2' ) order by No";
        //map.AddDDLSQL(BasicInfoAttr.QuXianNo,null,"市县",sql,true);
        //map.AddDDLEntities(BasicInfoAttr.XiangZhenNo, null, "乡镇", new XiangZhens(), true);
       // map.AddDDLSQL(BasicInfoAttr.XiangZhenNo, null, "乡镇", "`select City_Code as No,City_Name as Name from Sys_City where City_Level='3' `", true);
        map.AddTBString(BasicInfoAttr.SJLY, null, "数据来源", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.ZZZL, null, "种类", true, false, 0, 200, 100);
        map.AddTBFloat(BasicInfoAttr.TXMJ, 0, "图形面积(亩)", true, false);
        map.AddTBFloat(BasicInfoAttr.JDMJ, 0, "基地面积(亩)", true, false);
        map.AddTBFloat(BasicInfoAttr.ZHMJ, 0, "种植(亩)", true, false);
        map.AddTBString(BasicInfoAttr.JDMC, null, "基地名称", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.JDBH , null, "基地编号", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.JDFZE, null, "基地负责人", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.LXFS, null, "联系方式", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.JDZB, null, "基地坐标", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.TDWYQRJDH, null, "土地所有权人及电话", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.ZGQRJDH, null, "资格权人及电话", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.JYQRJDH, null, "经营权人及电话", true, false, 0, 200, 100);
        map.AddTBString(BasicInfoAttr.ZZRJDH, null, "种植人及电话", true, false, 0, 200, 100);
        map.AddTBFloat(BasicInfoAttr.CLJH, 0, "产量计划", true, false);
        map.AddTBString(BasicInfoAttr.SHuoShuSX, null, "所属市县", true, false, 0, 5000, 100);


        this.set_enMap(map);
        return this.get_enMap();

    }
}
