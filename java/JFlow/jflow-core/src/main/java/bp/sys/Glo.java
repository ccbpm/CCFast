package bp.sys;

import bp.sys.base.FormEventBase;
import bp.sys.base.FormEventBaseDtl;
import bp.tools.Cryptos;
import bp.da.*;
import bp.difference.ContextHolderUtils;
import bp.difference.SystemConfig;
import bp.en.*;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import java.math.*;

/**
 * 公用的静态方法.
 */
public class Glo {

    /// <summary>
    /// 获得真实UserNo,如果是SAAS模式.
    /// </summary>
    public static String getUserNo() {

        String empNo = "No";
        if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
            empNo = "UserID as No";
        return empNo;

    }

    public static String DealClassEntityName(String enName) {
        if (SystemConfig.Plant == bp.sys.Plant.CSharp)
            return enName;

        if (enName.contains(".") == false)
            return enName;
        String lastName = enName.substring(enName.lastIndexOf(".") + 1);
        String beginName = enName.substring(0, enName.lastIndexOf("."));
        beginName = beginName.toLowerCase();
        return beginName + "." + lastName;

    }

    /**
     * 清楚设置的缓存.
     * param frmID
     *
     * @throws Exception
     */
    public static void ClearMapDataAutoNum(String frmID) throws Exception {
        //执行清空缓存到的AutoNum.
        MapData md = new MapData();
        md.setNo(frmID);
        if (md.RetrieveFromDBSources() != 0)
            md.ClearAutoNumCache(true); //更新缓存.
    }

    /**
     * 更新SID Or OrgNo 的SQL
     * 用于集成所用
     * 更新被集成的用户的user表
     */
    public static String getUpdateSIDAndOrgNoSQL() {
        return SystemConfig.GetValByKey("UpdateSIDAndOrgNoSQL", null);
    }

    /**
     * 获得真实的数据类型
     * <p>
     * param attrs 属性集合
     * param key key
     * param val 值
     *
     * @return 返回val真实的数据类型.
     */
    public static Object GenerRealType(Attrs attrs, String key, Object val) {
        Attr attr = attrs.GetAttrByKey(key);
        switch (attr.getMyDataType()) {
            case DataType.AppString:
            case DataType.AppDateTime:
            case DataType.AppDate:
                val = val.toString();
                break;
            case DataType.AppInt:
            case DataType.AppBoolean:
                val = Integer.parseInt(val.toString());
                break;
            case DataType.AppFloat:
                val = Float.parseFloat(val.toString());
                break;
            case DataType.AppDouble:

                val = Integer.parseInt(val.toString());
                break;
            case DataType.AppMoney:
                val = new BigDecimal(val.toString());
                break;
            default:
                throw new RuntimeException();
        }
        return val;
    }


    ///业务单元.
    private static Hashtable Htable_BuessUnit = null;

    /**
     * 获得节点事件实体
     * <p>
     * param enName 实例名称
     *
     * @return 获得节点事件实体, 如果没有就返回为空.
     */
    public static BuessUnitBase GetBuessUnitEntityByEnName(String enName) {
        if (Htable_BuessUnit == null || Htable_BuessUnit.isEmpty()) {
            Htable_BuessUnit = new Hashtable();
            ArrayList<BuessUnitBase> al = bp.en.ClassFactory.GetObjects("bp.sys.BuessUnitBase");
            for (BuessUnitBase en : al) {
                Htable_BuessUnit.put(en.toString(), en);
            }
        }

        BuessUnitBase myen = Htable_BuessUnit.get(enName) instanceof BuessUnitBase ? (BuessUnitBase) Htable_BuessUnit.get(enName) : null;
        if (myen == null) {
            bp.da.Log.DefaultLogWriteLineError("@根据类名称获取业务单元实例出现错误:" + enName + ",没有找到该类的实体.");
            return null;
        }
        return myen;
    }

    /**
     * 获得事件实体String，根据编号或者流程标记
     * <p>
     * param flowMark 流程标记
     * param flowNo 流程编号
     *
     * @return null, 或者流程实体.
     */
    public static String GetBuessUnitEntityStringByFlowMark(String flowMark, String flowNo) {
        BuessUnitBase en = GetBuessUnitEntityByFlowMark(flowMark, flowNo);
        if (en == null) {
            return "";
        }
        return en.getClass().getName();
    }

    /**
     * 获得业务单元.
     * <p>
     * param flowMark 流程标记
     * param flowNo 流程编号
     *
     * @return null, 或者流程实体.
     */
    public static BuessUnitBase GetBuessUnitEntityByFlowMark(String flowMark, String flowNo) {

        if (Htable_BuessUnit == null || Htable_BuessUnit.isEmpty()) {
            Htable_BuessUnit = new Hashtable();
            ArrayList<BuessUnitBase> al = bp.en.ClassFactory.GetObjects("bp.sys.BuessUnitBase");
            Htable_BuessUnit.clear();
            for (BuessUnitBase en : al) {
                Htable_BuessUnit.put(en.toString(), en);
            }
        }

        for (Object key : Htable_BuessUnit.keySet()) {
            BuessUnitBase fee = Htable_BuessUnit.get(key) instanceof BuessUnitBase ? (BuessUnitBase) Htable_BuessUnit.get(key) : null;
            if (fee.toString().equals(flowMark) || fee.toString().contains("," + flowNo + ",") == true) {
                return fee;
            }
        }
        return null;
    }

    /// 业务单元.


    ///与 表单 事件实体相关.
    private static Hashtable Htable_FormFEE = null;

    /**
     * 获得节点事件实体
     * <p>
     * param enName 实例名称
     *
     * @return 获得节点事件实体, 如果没有就返回为空.
     */
    public static FormEventBase GetFormEventBaseByEnName(String enName) {
        if (Htable_FormFEE == null) {
            Htable_FormFEE = new Hashtable();

            ArrayList<FormEventBase> al = bp.en.ClassFactory.GetObjects("bp.sys.FormEventBase");
            Htable_FormFEE.clear();

            for (FormEventBase en : al) {
                Htable_FormFEE.put(en.getFormMark(), en);
            }
        }

        for (Object key : Htable_FormFEE.keySet()) {
            FormEventBase fee = Htable_FormFEE.get(key) instanceof FormEventBase ? (FormEventBase) Htable_FormFEE.get(key) : null;
            if (key == null)
                continue;
            if (key.toString().contains(",")) {
                if (key.toString().indexOf(enName + ",") >= 0 || key.toString().length() == key.toString().indexOf("," + enName) + enName.length() + 1) {
                    return fee;
                }
            }

            if (enName.equals(key)) {
                return fee;
            }
        }

        return null;

    }

    /// 与 表单 事件实体相关.


    ///与 表单从表 事件实体相关.
    private static Hashtable Htable_FormFEEDtl = null;

    /**
     * 获得节点事件实体
     * <p>
     * param enName 实例名称
     *
     * @return 获得节点事件实体, 如果没有就返回为空.
     */
    public static FormEventBaseDtl GetFormDtlEventBaseByEnName(String dtlEnName) {
        if (Htable_FormFEEDtl == null || Htable_FormFEEDtl.isEmpty()) {
            Htable_FormFEEDtl = new Hashtable();
            ArrayList<FormEventBaseDtl> al = bp.en.ClassFactory.GetObjects("bp.sys.FormEventBaseDtl");
            Htable_FormFEEDtl.clear();
            for (FormEventBaseDtl en : al) {
                Htable_FormFEEDtl.put(en.getFormDtlMark(), en);
            }
        }

        for (Object key : Htable_FormFEEDtl.keySet()) {
            FormEventBaseDtl fee = Htable_FormFEEDtl.get(key) instanceof FormEventBaseDtl ? (FormEventBaseDtl) Htable_FormFEEDtl.get(key) : null;
            if (fee.getFormDtlMark().indexOf(dtlEnName) >= 0 || fee.getFormDtlMark().equals(dtlEnName)) {
                return fee;
            }
        }
        return null;
    }

    /// 与 表单 事件实体相关.


    ///公共变量.
    public static String Plant = "CCFlow";

    /**
     * 部门版本号
     *
     * @throws Exception
     */
    public static String getDeptsVersion() throws Exception {
        GloVar en = new GloVar();
        en.setNo("DeptsVersion");
        if (en.RetrieveFromDBSources() == 0) {
            en.setName("部门版本号");
            en.setVal(bp.da.DataType.getCurrentDataTime());
            en.setGroupKey("Glo");
            en.Insert();
        }
        return en.getVal();
    }

    /**
     * 部门数量 - 用于显示ccim的下载进度.
     */
    public static int getDeptsCount() {
        return DBAccess.RunSQLReturnValInt("SELECT COUNT(No) as Num FROM Port_Dept");
    }

    /**
     * 人员数量 - 用于显示ccim的下载进度.
     */
    public static int getEmpsCount() {
        return DBAccess.RunSQLReturnValInt("SELECT COUNT(a.No()) as Num FROM Port_Emp a, Port_Dept b WHERE A.FK_Dept=B.No AND A.No NOT IN ('admin','Guest')");
    }

    /**
     * 人员版本号
     *
     * @throws Exception
     */
    public static String getUsersVersion() throws Exception {
        GloVar en = new GloVar();
        en.setNo("UsersVersion");
        if (en.RetrieveFromDBSources() == 0) {
            en.setName("人员版本号");
            en.setVal(bp.da.DataType.getCurrentDataTime());
            en.setGroupKey("Glo");
            en.Insert();
        }
        return en.getVal();
    }

    /// 公共变量.


    ///写入系统日志(写入的文件:\DataUser\Log\*.*)

    /**
     * 写入一条消息
     * <p>
     * param msg 消息
     */
    public static void WriteLineInfo(String msg) {
        bp.da.Log.DefaultLogWriteLineInfo(msg);
    }

    /**
     * 写入一条警告
     * <p>
     * param msg 消息
     */
    public static void WriteLineWarning(String msg) {
        bp.da.Log.DefaultLogWriteLineWarning(msg);
    }

    /**
     * 写入一条错误
     * <p>
     * param msg 消息
     */
    public static void WriteLineError(String msg) {
        bp.da.Log.DefaultLogWriteLineError(msg);
    }

    /// 写入系统日志


    ///写入用户日志(写入的用户表:Sys_UserLog).

    /**
     * 写入用户日志
     * <p>
     * param logType 类型
     * param empNo 操作员编号
     * param msg 信息
     * param ip IP
     *
     * @throws Exception
     */
    public static void WriteUserLog(String logType, String empNo, String msg, String ip) throws Exception {
        UserLog ul = new UserLog();
        ul.setMyPK(DBAccess.GenerGUID());
        ul.setEmpNo(empNo);
        ul.setLogFlag(logType);
        ul.setDocs(msg);
        ul.setIP(ip);
        ul.setRDT(DataType.getCurrentDataTime());
        ul.Insert();
    }

    /**
     * 写入用户日志
     * <p>
     * param logType 日志类型
     * param empNo 操作员编号
     * param msg 消息
     */
    public static void WriteUserLog(String logType, String empNo, String msg) {
        //为了提高运行效率，把这个地方屏蔽了。
        return;

    }

    /**
     * 产生消息,senderEmpNo是为了保证写入消息的唯一性，receiveid才是真正的接收者.
     * 如果插入失败.
     * <p>
     * param fromEmpNo 发送人
     * param now 发送时间
     * param msg 消息内容
     * param sendToEmpNo 接受人
     */
    public static void SendMessageToCCIM(String fromEmpNo, String sendToEmpNo, String msg, String now) {
        //暂停对ccim消息提醒的支持.
        return;
    }

    /**
     * 处理生成提示信息,不友好的提示.
     * <p>
     * param alertInfo 从系统里抛出的错误信息.
     *
     * @return 返回的友好提示信息.
     */
    public static String GenerFriendlyAlertHtmlInfo(String alertInfo) {
        // 格式为: err@错误中文提示信息. tech@info 数据库错误,查询sqL为.
        return alertInfo;
    }


    ///加密解密文件.
    public static void File_JiaMi(String fileFullPath) {
        //南京宝旺达.
        if (SystemConfig.getCustomerNo().equals("BWDA")) {

        }
    }

    public static void File_JieMi(String fileFullPath) {
        //南京宝旺达.
        if (SystemConfig.getCustomerNo().equals("BWDA")) {

        }
    }

    /**
     * 字符串的解密
     * <p>
     * param str 加密的字符串
     *
     * @return 返回解密后的字符串
     */
    public static String String_JieMi(String str) {
        //南京宝旺达.
        if (SystemConfig.getCustomerNo().equals("BWDA")) {
            return str;
        }

        return str;
    }

    public static String String_JieMi_FTP(String str) throws Exception {

        //南京宝旺达.
        if (SystemConfig.getCustomerNo().equals("BWDA"))
            return Cryptos.aesDecrypt(str);

        return str;
    }

    public static HttpServletRequest getRequest() {
        return ContextHolderUtils.getRequest();
    }

}