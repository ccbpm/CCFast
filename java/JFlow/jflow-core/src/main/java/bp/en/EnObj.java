package bp.en;

import bp.da.*;
import bp.difference.ContextHolderUtils;
import bp.difference.SystemConfig;
import bp.sys.*;
import bp.web.*;
import bp.wf.GenerWorkFlow;
import bp.wf.WFState;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Entity 的摘要说明。
 */
public abstract class EnObj implements Serializable {
    private static final long serialVersionUID = 1L;
    ///访问控制.
    private String _DBVarStr = null;

    public final String getHisDBVarStr() throws Exception {
        if (_DBVarStr != null) {
            return _DBVarStr;
        }

        _DBVarStr = this.getEnMap().getEnDBUrl().getDBVarStr();
        return _DBVarStr;
    }

    /**
     * 他的访问控制.
     */
    protected UAC _HisUAC = null;

    /**
     * 得到 uac 控制.
     *
     * @return
     * @throws Exception
     */
    public UAC getHisUAC() {
        if (_HisUAC == null) {
            _HisUAC = new UAC();
            if (WebUser.getNo().equals("admin")) {
                _HisUAC.IsAdjunct = false;
                _HisUAC.IsDelete = true;
                _HisUAC.IsInsert = true;
                _HisUAC.IsUpdate = true;
                _HisUAC.IsView = true;
                return _HisUAC;
            }
        }
        return _HisUAC;
    }

    ///


    ///取出外部配置的属性信息

    /**
     * 取出Map 的扩展属性。
     * 用于第3方的扩展属性开发。
     * <p>
     * param key 属性Key
     *
     * @return 设置的属性
     */
    public final String GetMapExtAttrByKey(String key) throws Exception {
        Paras ps = new Paras();
        ps.Add("enName", this.toString());
        ps.Add("key", key);

        return (String) DBAccess.RunSQLReturnVal("select attrValue from Sys_ExtMap WHERE className=" + SystemConfig.getAppCenterDBVarStr() + "enName AND attrKey=" + SystemConfig.getAppCenterDBVarStr() + "key", ps);
    }


    /**
     * 创建一个实例
     *
     * @return 自身的实例
     */
    public final Entity CreateInstance() throws Exception {
        Object tempVar = null;
        try {
            tempVar = this.getClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (Entity) ((tempVar instanceof Entity) ? tempVar : null);
    }

    private Entities _GetNewEntities = null;

    ///


    ///方法

    /**
     * 设置默认值.
     *
     * @throws Exception
     */
    public final void ResetDefaultValRowValues() throws Exception {
        if (this.getEnMap() == null) {
            return;
        }

        for (Attr attr : this.getEnMap().getAttrs()) {
            String key = attr.getKey();

            String v = this.GetValStringByKey(key, null); // this._row[key] as string;

            if (v == null || v.indexOf('@') == -1) {
                continue;
            }

            // 设置默认值.
            if (v.equals("@WebUser.No")) {

                this.SetValByKey(attr.getKey(), WebUser.getNo());

                continue;
            } else if (v.equals("@WebUser.Name")) {
                this.SetValByKey(attr.getKey(), WebUser.getName());
                continue;
            } else if (v.equals("@WebUser.FK_Dept")) {
                this.SetValByKey(attr.getKey(), WebUser.getDeptNo());
                continue;
            }else if (v.equals("@WebUser.DeptNo"))
            {
                this.SetValByKey(attr.getKey(),  WebUser.getDeptNo());
                continue;
            } else if (v.equals("@WebUser.FK_DeptName")) {
                this.SetValByKey(attr.getKey(), WebUser.getDeptName());
                continue;
            } else if (v.equals("@WebUser.FK_DeptNameOfFull") || v.equals("@WebUser.FK_DeptFullName")) {
                this.SetValByKey(attr.getKey(), WebUser.getDeptNameOfFull());
                continue;
            } else if (v.equals("@RDT")) {
                String dataFormat = "yyyy-MM-dd";
                switch (attr.getItIsSupperText()) {
                    case 0:
                        break;
                    case 1:
                        dataFormat = "yyyy-MM-dd HH:mm";
                        break;
                    case 2:
                        dataFormat = "yyyy-MM-dd HH:mm:ss";
                        break;
                    case 3:
                        dataFormat = "yyyy-MM";
                        break;
                    case 4:
                        dataFormat = "HH:mm";
                        break;
                    case 5:
                        dataFormat = "HH:mm:ss";
                        break;
                    case 6:
                        dataFormat = "MM-dd";
                    case 7:
                        dataFormat = "yyyy";
                    case 8:
                        dataFormat = "MM";
                        break;
                    default:
                        throw new RuntimeException("没有找到指定的时间类型");
                }
                this.SetValByKey(attr.getKey(), DataType.getCurrentDateByFormart(dataFormat));
                continue;
            }

        }
    }

    /**
     * 重新设置默信息. @yuanlina 这里有问题，需要重构到jflow上去.
     *
     * @throws Exception
     */

    public final void ResetDefaultVal(String fk_mapdata, String fk_flow) throws Exception {
        ResetDefaultVal(fk_mapdata, fk_flow, 0);
    }

    public final void ResetDefaultVal(String fk_mapdata) throws Exception {
        ResetDefaultVal(fk_mapdata, null, 0);
    }

    public final void ResetDefaultVal() throws Exception {
        ResetDefaultVal(null, null, 0);
    }


    public final void ResetDefaultVal(String fk_mapdata, String fk_flow, int fk_node) throws Exception {

        ResetDefaultValRowValues();

        DataTable dt = null;
        if (fk_node != 0 && fk_node != 999999 && fk_flow != null) {
            String sql2 = "SELECT MyPK,DefVal,UIIsEnable FROM Sys_FrmSln WHERE FK_MapData = '" + fk_mapdata + "'  AND FK_Node = " + fk_node;
            dt = DBAccess.RunSQLReturnTable(sql2);
        }


        Attrs attrs = this.getEnMap().getAttrs();
        boolean isReadonly = true;
        for (Attr attr : attrs) {
            if (attr.getItIsRefAttr()) {
                this.SetValRefTextByKey(attr.getKey(), "");
            }

            String tempVar = attr.getDefaultValOfReal();
            String v = tempVar instanceof String ? (String) tempVar : null;
            isReadonly = attr.getUIIsReadonly();
            //先判断是否设置了字段权限
            if (dt != null) {

                String mypk = fk_mapdata + "_" + fk_node + "_" + attr.getKey();
                for (DataRow dr : dt.Rows) {
                    String myp1k = dr.getValue(0).toString();
                    if (myp1k.equals(mypk) == true) {
                        v = dr.getValue(1) instanceof String ? (String) dr.getValue(1) : null;
                        isReadonly = dr.getValue(2).toString().equals("1") == true ? false : true;
                        break;
                    }
                }
            }

            if (v == null || v.contains("@") == false) {
                continue;
            }

            String myval = this.GetValStrByKey(attr.getKey());
            if(DataType.IsNullOrEmpty(myval)==false && myval.contains("@") == false) continue;

            // 设置默认值.
            switch (v) {
                case "@WebUser.No":
                case "@CurrWorker":
                    if (isReadonly == true) {
                        this.SetValByKey(attr.getKey(), WebUser.getNo());
                    } else {
                        if (DataType.IsNullOrEmpty(myval) || v.equals(myval)) {
                            this.SetValByKey(attr.getKey(), WebUser.getNo());
                        }
                    }
                    continue;
                case "@WebUser.Name":
                    if (isReadonly == true) {
                        this.SetValByKey(attr.getKey(), WebUser.getName());
                    } else {
                        if (DataType.IsNullOrEmpty(myval) || v.equals(myval)) {
                            this.SetValByKey(attr.getKey(), WebUser.getName());
                        }
                    }
                    continue;
                case "@WebUser.Tel":
                    if (isReadonly == true) {
                        this.SetValByKey(attr.getKey(), WebUser.getTel());
                    } else {
                        if (DataType.IsNullOrEmpty(myval) || v.equals(myval)) {
                            this.SetValByKey(attr.getKey(), WebUser.getTel());
                        }
                    }
                    continue;
                case "@WebUser.FK_Dept":
                case "@WebUser.DeptNo":
                    if (isReadonly == true) {
                        this.SetValByKey(attr.getKey(), WebUser.getDeptNo());
                    } else {
                        if (DataType.IsNullOrEmpty(myval) || v.equals(myval)) {
                            this.SetValByKey(attr.getKey(), WebUser.getDeptNo());
                        }
                    }
                    continue;
                case "@WebUser.FK_DeptName":
                    if (isReadonly == true) {
                        this.SetValByKey(attr.getKey(), WebUser.getDeptName());
                    } else {
                        if (DataType.IsNullOrEmpty(myval) || v.equals(myval)) {
                            this.SetValByKey(attr.getKey(), WebUser.getDeptName());
                        }
                    }
                    continue;
                case "@WebUser.FK_DeptNameOfFull":
                case "@WebUser.FK_DeptFullName":
                    if (isReadonly == true) {
                        this.SetValByKey(attr.getKey(), WebUser.getDeptNameOfFull());
                    } else {
                        if (DataType.IsNullOrEmpty(myval) || v.equals(myval)) {
                            this.SetValByKey(attr.getKey(), WebUser.getDeptNameOfFull());
                        }
                    }
                    continue;
                case "@WebUser.OrgNo":
                    if (isReadonly == true) {
                        this.SetValByKey(attr.getKey(), WebUser.getOrgNo());
                    } else {
                        if (DataType.IsNullOrEmpty(myval) || v.equals(myval)) {
                            this.SetValByKey(attr.getKey(), WebUser.getOrgNo());
                        }
                    }
                    continue;
                case "@WebUser.OrgName":
                    if (isReadonly == true) {
                        this.SetValByKey(attr.getKey(), WebUser.getOrgName());
                    } else {
                        if (DataType.IsNullOrEmpty(myval) || v.equals(myval)) {
                            this.SetValByKey(attr.getKey(), WebUser.getOrgName());
                        }
                    }
                    continue;
                case "@RDT":
                    String dataFormat = "yyyy-MM-dd";
                    switch (attr.getItIsSupperText()) {
                        case 0:
                            break;
                        case 1:
                            dataFormat = "yyyy-MM-dd HH:mm";
                            break;
                        case 2:
                            dataFormat = "yyyy-MM-dd HH:mm:ss";
                            break;
                        case 3:
                            dataFormat = "yyyy-MM";
                            break;
                        case 4:
                            dataFormat = "HH:mm";
                            break;
                        case 5:
                            dataFormat = "HH:mm:ss";
                            break;
                        case 6:
                            dataFormat = "MM-dd";
                            break;
                        case 7:
                            dataFormat = "yyyy";
                            break;
                        case 8:
                            dataFormat = "MM";
                            break;
                        default:
                            throw new RuntimeException("没有找到指定的时间类型");
                    }
                    if (isReadonly == true) {
                        /** if (myval == v)
                         */
                        //如果是流程第一个节点，一直获取新数据 @CS律师所
                        if(DataType.IsNullOrEmpty(fk_node) == false){
                            if (Integer.toString(fk_node).endsWith("01") == true){
                                this.SetValByKey(attr.getKey(), DataType.getCurrentDateByFormart(dataFormat));
                                continue;
                            }
                        }
                        if (DataType.IsNullOrEmpty(myval) || v.equals(myval)) {
                            this.SetValByKey(attr.getKey(), DataType.getCurrentDateByFormart(dataFormat));
                        }

                    } else {
                        if (DataType.IsNullOrEmpty(myval) || v.equals(myval)) {
                            this.SetValByKey(attr.getKey(), DataType.getCurrentDateByFormart(dataFormat));
                        }
                    }
                    continue;
                case "@FK_ND":
                    if (isReadonly == true) {
                        this.SetValByKey(attr.getKey(), DataType.getCurrentYear());
                    } else {
                        if (DataType.IsNullOrEmpty(myval) || v.equals(myval)) {
                            this.SetValByKey(attr.getKey(), DataType.getCurrentYear());
                        }
                    }
                    continue;
                case "@FK_YF":
                    if (isReadonly == true) {
                        this.SetValByKey(attr.getKey(), DataType.getCurrentMonth());
                    } else {
                        if (DataType.IsNullOrEmpty(myval) || v.equals(myval)) {
                            this.SetValByKey(attr.getKey(), DataType.getCurrentMonth());
                        }
                    }
                    continue;

                case "@yyyy年MM月dd日":
                case "@yyyy年MM月dd日HH时mm分":
                case "@yy年MM月dd日":
                case "@yy年MM月dd日HH时mm分":
                    if (isReadonly == true) {
                        this.SetValByKey(attr.getKey(), DataType.getCurrentDateByFormart(v.replace("@", "")));
                    } else {
                        if (DataType.IsNullOrEmpty(myval) || v.equals(myval)) {
                            this.SetValByKey(attr.getKey(), DataType.getCurrentDateByFormart(v.replace("@", "")));
                        }
                    }
                    continue;
                default:
                    GloVar gloVar = new GloVar();
                    gloVar.setPKVal(v);
                    int count = gloVar.RetrieveFromDBSources();
                    if (count == 1) {
                        //执行SQL获取默认值 @sly
                        String sql = gloVar.getVal();
                        if (DataType.IsNullOrEmpty(sql) == true) {
                            continue;
                        }

                        sql = DealExp(sql, this.getRow(), null);
                        if (sql.toUpperCase().contains("SELECT") == false) {
                            this.SetValByKey(attr.getKey(), sql);
                            continue;
                        }
                        try {
                            //这里有异常就要跑出来
                            String val = DBAccess.RunSQLReturnString(sql);
                            this.SetValByKey(attr.getKey(), val);
                        } catch (RuntimeException ex) {
                            throw new RuntimeException("err@为类：" + this.toString() + ",字段：" + attr.getKey() + ",变量表达式:" + v + ",设置信息:" + gloVar.ToJson() + ",设置默认值解析SQL:" + sql + " ，原始设置SQL:" + gloVar.getVal() + ",执行SQL期间出现错误.");
                        }
                    }
                    continue;
            }
        }
    }

    /**
     * 处理表达式
     * <p>
     * param exp 表达式
     * param row 数据源
     * param errInfo 错误
     *
     * @return
     * @
     */
    private static String DealExp(String exp, Row row, String errInfo) {
        if (exp.contains("@") == false) {
            return exp;
        }

        exp = exp.replace("~", "'");

        //首先替换加; 的。
        exp = exp.replace("@WebUser.No;", WebUser.getNo());
        exp = exp.replace("@WebUser.Name;", WebUser.getName());
        exp = exp.replace("@WebUser.FK_Dept;", WebUser.getDeptNo());
        exp = exp.replace("@WebUser.DeptNo;", WebUser.getDeptNo());
        exp = exp.replace("@WebUser.FK_DeptName;", WebUser.getDeptName());

        // 替换没有 ; 的 .
        exp = exp.replace("@WebUser.No", WebUser.getNo());
        exp = exp.replace("@WebUser.Name", WebUser.getName());
        exp = exp.replace("@WebUser.FK_DeptName", WebUser.getDeptName());
        exp = exp.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
        exp = exp.replace("@WebUser.DeptNo", WebUser.getDeptNo());

        if (exp.contains("@") == false) {
            exp = exp.replace("~", "'");
            return exp;
        }

        //增加对新规则的支持. @MyField; 格式.
        if (row != null) {
            //特殊判断.
            if (row.containsKey("OID") == true) {
                exp = exp.replace("@WorkID", row.get("OID").toString());
            }

            if (exp.contains("@") == false) {
                return exp;
            }

            for (Object key : row.keySet()) {
                if (exp.contains("@" + key + ";")) {
                    exp = exp.replace("@" + key + ";", row.get(key).toString());
                }
            }
            if (exp.contains("@") == false) {
                return exp;
            }


            ///解决排序问题.
            String mystrs = "";
            for (Object key : row.keySet()) {
                mystrs += "@" + key;
            }


            String[] strs = mystrs.split("[@]", -1);
            DataTable dt = new DataTable();
            dt.Columns.Add(new DataColumn("No", String.class));
            for (String str : strs) {
                if (DataType.IsNullOrEmpty(str)) {
                    continue;
                }

                DataRow dr = dt.NewRow();
                dr.setValue(0, str);
                dt.Rows.add(dr);
            }
            //DataView dv = dt.DefaultView;
            //dv.setSort("No DESC";
            //DataTable dtNew = dv.Table;

            ///  解决排序问题.


            ///替换变量.
            for (DataRow dr : dt.Rows) {
                String key = dr.getValue(0).toString();
                boolean isStr = key.toString().contains(",");
                if (isStr == true) {
                    key = key.replace(",", "");
                    exp = exp.replace("@" + key, row.GetValStrByKey(key));
                } else {
                    exp = exp.replace("@" + key, row.GetValStrByKey(key));
                }
            }


            if (exp.contains("@") == false) {
                return exp;
            }

            //替换全部的变量.
            for (Object key : row.keySet()) {
                if (exp.contains("@" + key)) {
                    exp = exp.replace("@" + key, row.get(key).toString());
                }
            }
        }


        if (exp.contains("@") && SystemConfig.isBSsystem() == true) {
            /*如果是bs*/
            Enumeration enu = ContextHolderUtils.getRequest().getParameterNames();
            while (enu.hasMoreElements()) {
                // 判断是否有内容，hasNext()
                String key = (String) enu.nextElement();
                if (DataType.IsNullOrEmpty(key)) {
                    continue;
                }
                exp = exp.replace("@" + key, ContextHolderUtils.getRequest().getParameter(key));
            }

        }

        exp = exp.replace("~", "'");

        return exp;
    }

    /**
     * 把所有的值都设置成默认值，但是主键除外。
     *
     * @
     */
    public final void ResetDefaultValAllAttr() throws Exception {
        Attrs attrs = this.getEnMap().getAttrs();
        for (Attr attr : attrs) {
            if (attr.getUIIsReadonly() == false && attr.getDefaultValOfReal() != null) {
                continue;
            }

            if (attr.getItIsPK()) {
                continue;
            }

            String tempVar = attr.getDefaultValOfReal();
            String v = tempVar instanceof String ? (String) tempVar : null;
            if (v == null) {
                this.SetValByKey(attr.getKey(), "");
                continue;
            }

            if (v.contains("@") == false && v != null) {
                this.SetValByKey(attr.getKey(), v);
                continue;
            }


            // 设置默认值.
            switch (v) {
                case "@WebUser.No":
                    this.SetValByKey(attr.getKey(), WebUser.getNo());
                    continue;
                case "@WebUser.Name":
                    this.SetValByKey(attr.getKey(), WebUser.getName());
                    continue;
                case "@WebUser.FK_Dept":
                case "@WebUser.DeptNo":
                    this.SetValByKey(attr.getKey(), WebUser.getDeptNo());
                    continue;
                case "@WebUser.FK_DeptName":
                    this.SetValByKey(attr.getKey(), WebUser.getDeptName());
                    continue;
                case "@WebUser.FK_DeptNameOfFull":
                    this.SetValByKey(attr.getKey(), WebUser.getDeptNameOfFull());
                    continue;
                case "@RDT":
                    if (attr.getMyDataType() == DataType.AppDate) {
                        this.SetValByKey(attr.getKey(), DataType.getCurrentDate());
                    } else {
                        this.SetValByKey(attr.getKey(), DataType.getCurrentDataTime());
                    }
                    continue;
                default:
                    continue;
            }
        }
    }


    /**
     * 实体
     */
    public EnObj() {
    }

    private Map _tmpEnMap = null;

    /**
     * Map
     */
    protected final Map get_enMap() {
        if (_tmpEnMap == null) {
            _tmpEnMap = Cache.GetMap(this.toString());

        }
        return _tmpEnMap;
    }

    public final void set_enMap(Map value) {
        if (value == null) {
            _tmpEnMap = null;
            return;
        }

        Map mp = (Map) value;
        if (SystemConfig.isDebug()) {

        }

        if (mp == null || mp.getDepositaryOfMap() == Depositary.None) {
            _tmpEnMap = mp;
            return;
        }

        Cache.SetMap(this.toString(), mp);
        _tmpEnMap = mp;
    }

    /**
     * 子类需要继承
     *
     * @
     */
    public abstract Map getEnMap();

    /**
     * 动态的获取map
     *
     * @
     */
    public final Map getEnMapInTime() {
        _tmpEnMap = null;
        Cache.SetMap(this.toString(), null);
        return this.getEnMap();
    }
    ///row 存放实体数据的
    /**
     * 实体的 map 信息。
     */
    private Row _row = null;

    public final Row getRow() {

//        if (SystemConfig.getRedisIsEnable()) {
//            Row tmpRow = local.get();
//            if (tmpRow == null) {
//                if (this._row != null) {
//                    String pkval = this._row.get(this.getPK()) == null ? "" : this._row.get(this.getPK()).toString();
//                    if (!DataType.IsNullOrEmpty(pkval)) {
//                        Row row = bp.da.Cache2019.GetRow(this.toString(), pkval);
//                        if (row != null) {
//                            this._row = row;
//                        }
//                        local.set(this._row);
//                    }
//                    return this._row;
//                } else {
//                    this._row = new Row();
//                    this._row.LoadAttrs(this.getEnMap().getAttrs());
//                    return this._row;
//                }
//            }
//            return tmpRow;
//
//        }
        if (this._row == null) {
            this._row = new Row();
            this._row.LoadAttrs(this.getEnMap().getAttrs());
        }
        return this._row;
    }


    public final void setRow(Row value) throws Exception {
        this._row = value;
        /*if (SystemConfig.getRedisIsEnable()) {
            if (value == null) {
                return;
            }
            Object val = this._row.GetValByKey(this.getPK());
            String pkVal = val == null ? "" : val.toString();
            bp.da.Cache2019.PutRow(this.toString(), pkVal, value);
        }*/
    }

    ///关于属性的操作。


    ///设置值方法

    /**
     * 设置object类型的值
     * <p>
     * param attrKey attrKey
     * param val val
     */
    public final void SetValByKey(String attrKey, String val) {
        if (val == null)
            val = "";
        this.getRow().SetValByKey(attrKey, val);
    }

    public final void SetValByKey(String attrKey, int val) {
        this.getRow().SetValByKey(attrKey, val);
    }

    public final void SetValByKey(String attrKey, long val) {
        this.getRow().SetValByKey(attrKey, val);
    }

    public final void SetValByKey(String attrKey, float val) {
        this.getRow().SetValByKey(attrKey, val);
    }

    public final void SetValByKey(String attrKey, BigDecimal val) {
        this.getRow().SetValByKey(attrKey, val);
    }

    public final void SetValByKey(String attrKey, Object val) {
        this.getRow().SetValByKey(attrKey, val);
    }

    public final void SetValByDesc(String attrDesc, Object val) {
        if (val == null) {
            throw new RuntimeException("@不能设置属性[" + attrDesc + "]null 值。");
        }
        this.getRow().SetValByKey(this.getEnMap().GetAttrByDesc(attrDesc).getKey(), val);
    }

    /**
     * 设置关联类型的值
     * <p>
     * param attrKey attrKey
     * param val val
     */
    public final void SetValRefTextByKey(String attrKey, Object val) {
        this.SetValByKey(attrKey + "Text", val);
    }

    /**
     * 设置bool类型的值
     * <p>
     * param attrKey attrKey
     * param val val
     */
    public final void SetValByKey(String attrKey, boolean val) {
        if (val) {
            this.SetValByKey(attrKey, 1);
        } else {
            this.SetValByKey(attrKey, 0);
        }
    }

    /**
     * 设置默认值
     */
    public final void SetDefaultVals() {
        for (Attr attr : this.getEnMap().getAttrs()) {
            this.SetValByKey(attr.getKey(), attr.getDefaultVal());
        }
    }

    /**
     * 设置日期类型的值
     * <p>
     * param attrKey attrKey
     * param val val
     */
    public final void SetDateValByKey(String attrKey, String val) {
        try {
            this.SetValByKey(attrKey, DataType.StringToDateStr(val));
        } catch (RuntimeException ex) {
            throw new RuntimeException("@不合法的日期数据格式:key=[" + attrKey + "],value=" + val + " " + ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    ///


    ///取值方法

    /**
     * 取得Object
     * <p>
     * param attrKey
     *
     * @return
     */
    public final Object GetValByKey(String attrKey) {
        return this.getRow().GetValByKey(attrKey);


    }

    /**
     * GetValDateTime
     * <p>
     * param attrKey
     *
     * @return
     */

    public final Date GetValDate(String attrKey) {
        try {
            return DataType.ParseSysDateTime2DateTime(this.GetValStringByKey(attrKey));
        } catch (Exception ex) {
            throw new RuntimeException("字段[" + this.getEnMap().GetAttrByKey(attrKey).getDesc() + "],值[" + this.GetValStringByKey(attrKey) + "]，不是一个有效的时间格式.");
        }
    }

    /**
     * 在确定  attrKey 存在 map 的情况下才能使用它
     * <p>
     * param key
     *
     * @return
     */
    public final String GetValStrByKey(String key) {
        if (this.getRow().GetValByKey(key) != null) {
            return this.getRow().GetValByKey(key).toString();
        }
        return "";
    }

    public final String GetValStrByKey(String key, String isNullAs) {
        try {
            String s = this.getRow().GetValByKey(key).toString();
            if (DataType.IsNullOrEmpty(s)) {
                return isNullAs;
            } else {
                return s;
            }
        } catch (java.lang.Exception e) {
            return isNullAs;
        }
    }

    /**
     * 取得String
     * <p>
     * param attrKey
     *
     * @return
     */
    public final String GetValStringByKey(String attrKey) {

        if (this.getRow() == null) {
            throw new RuntimeException("@没有初始化Row.");
        }

        try {
            Object obj = this.getRow().GetValByKey(attrKey);
            if(obj == null) return "";
            return obj.toString();
        } catch (RuntimeException ex) {
            throw new RuntimeException("@获取值期间出现如下异常：" + ex.getMessage() + "  " + attrKey + " 您没有在类增加这个属性，EnsName=" + this.toString());
        }
    }

    public final String GetValStringByKey(String attrKey, String defVal) {
        String val = this.GetValStringByKey(attrKey);
        if (val == null || val.equals("")) {
            return defVal;
        } else {
            return val;
        }
    }

    /**
     * 取出大块文本
     *
     * @return
     */
    public final String GetValDocText() {
        String s = this.GetValStrByKey("Doc");
        if (s.trim().length() != 0) {
            return s;
        }

        s = SysDocFile.GetValTextV2(this.toString(), this.getPKVal().toString());
        this.SetValByKey("Doc", s);
        return s;
    }

    public final String GetValDocHtml() throws Exception {
        String s = this.GetValHtmlStringByKey("Doc");
        if (s.trim().length() != 0) {
            return s;
        }

        s = SysDocFile.GetValHtmlV2(this.toString(), this.getPKVal().toString());
        this.SetValByKey("Doc", s);
        return s;
    }

    /**
     * 取到Html 信息。
     * <p>
     * param attrKey attr
     *
     * @return html.
     */
    public final String GetValHtmlStringByKey(String attrKey) throws Exception {
        return DataType.ParseText2Html(this.GetValStringByKey(attrKey));
    }

    public final String GetValHtmlStringByKey(String attrKey, String defval) throws Exception {
        return DataType.ParseText2Html(this.GetValStringByKey(attrKey, defval));
    }

    /**
     * 取得枚举或者外键的标签
     * 如果是枚举就获取枚举标签.
     * 如果是外键就获取为外键的名称.
     * <p>
     * param attrKey
     *
     * @return
     */
    public final String GetValRefTextByKey(String attrKey) {
        String str = "";
        try {
            Object tempVar = this.getRow().GetValByKey(attrKey + "Text");
            str = tempVar instanceof String ? (String) tempVar : null;
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage() + attrKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (str == null) {
            /*判断是否是外键字段？*/
            if (this.getRow().containsKey(attrKey + "Text") == false) {
                throw new RuntimeException(attrKey + "非外键或者枚举，不能使用:GetValRefTextByKey ，获取数据。");
            }
        }
        return str;
    }

    public final long GetValInt64ByKey(String key) {
        return Long.parseLong(this.GetValStringByKey(key, "0"));
    }

    public final int GetValIntByKey(String key, int isNullAsVal) {
        String str = this.GetValStrByKey(key);
        if (str == null || str.equals("") || str.equals("null")) {
            return isNullAsVal;
        }

        if (DataType.IsNumStr(str) == false) {
            return isNullAsVal;
        }
        return Integer.parseInt(str);
    }

    /**
     * 根据key 得到int val
     * <p>
     * param key
     *
     * @return
     */
    public final int GetValIntByKey(String key) {
        try {
            String str = this.GetValStrByKey(key);
            return DataType.IsNullOrEmpty(str) ? 0 : Integer.parseInt(this.GetValStrByKey(key));
        } catch (RuntimeException ex) {

            String v = this.GetValStrByKey(key).toLowerCase();
            if (v.equals("null")) {
                return 0;
            }

            if (v.equals("true")) {
                this.SetValByKey(key, 1);
                return 1;
            }
            if (v.equals("false")) {
                this.SetValByKey(key, 0);
                return 0;
            }

            if (key.equals("OID")) {
                this.SetValByKey("OID", 0);
                return 0;
            }

            if (this.GetValStrByKey(key).equals("")) {
                Attr attr = this.getEnMap().GetAttrByKey(key);
                if (attr.getIsNull()) {
                    return 567567567;
                } else {
                    return Integer.parseInt(attr.getDefaultVal().toString());
                }
            }

            throw new RuntimeException("@实体类[" + this.toString() + "]@[" + this.getEnMap().GetAttrByKey(key).getDesc() + "]请输入数字，您输入的是[" + this.GetValStrByKey(key) + "],错误信息:" + ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据key 得到 bool val
     * <p>
     * param key
     *
     * @return
     */
    public final boolean GetValBooleanByKey(String key) {
        String s = this.GetValStrByKey(key);
        if (DataType.IsNullOrEmpty(s)) {
            s = this.getEnMap().GetAttrByKey(key).getDefaultVal().toString();
        }

        if (Integer.parseInt(s) < 0 || Integer.parseInt(s) == 0) {
            return false;
        }

        if (s.equals("1")) {
            return true;
        }

        if (s.toUpperCase().equals("FALSE")) {
            return false;
        }
        if (s.toUpperCase().equals("TRUE")) {
            return true;
        }

        if (DataType.IsNullOrEmpty(s)) {
            return false;
        }

        if (Integer.parseInt(s) == 0) {
            return false;
        }

        return true;
    }

    public final boolean GetValBooleanByKey(String key, boolean defval) {
        try {

            if (Integer.parseInt(this.GetValStringByKey(key)) == 0) {
                return false;
            } else {
                return true;
            }
        } catch (java.lang.Exception e) {
            return defval;
        }
    }

    public final String GetValBoolStrByKey(String key) throws Exception {
        if (Integer.parseInt(this.GetValStringByKey(key)) == 0) {
            return "否";
        } else {
            return "是";
        }
    }

    /**
     * 根据key 得到flaot val
     * <p>
     * param key
     *
     * @return
     */
    public final float GetValFloatByKey(String key, int blNum) throws Exception {
        String val = this.getRow().GetValByKey(key).toString();
        if (DataType.IsNullOrEmpty(val)) {
            return 0.00f;
        }

        return Float.parseFloat(val);
    }

    /**
     * 根据key 得到flaot val
     * <p>
     * param key
     *
     * @return
     */
    public final float GetValFloatByKey(String key) {
        try {
            String str = this.getRow().GetValByKey(key).toString();
            return DataType.IsNullOrEmpty(str) ? 0 : Float.parseFloat(str);
        } catch (java.lang.Exception e) {
            if (this.GetValStringByKey(key).equals("")) {
                Attr attr = this.getEnMap().GetAttrByKey(key);
                return Float.parseFloat(attr.getDefaultVal().toString());
            }
            return 0;
        }
    }

    public final BigDecimal GetValMoneyByKey(String key) throws Exception {
        try {
            return this.GetValDecimalByKey(key, 4);
        } catch (java.lang.Exception e) {
            if (this.GetValStringByKey(key).equals("")) {
                Attr attr = this.getEnMap().GetAttrByKey(key);
                if (DataType.IsNullOrEmpty(attr.getDefaultVal()))
                    return new BigDecimal(0);
                return new BigDecimal(Double.parseDouble(attr.getDefaultVal().toString()));

            }
            return new BigDecimal(0);
        }
    }

    /**
     * 根据key 得到flaot val
     * <p>
     * param key
     *
     * @return
     */
    public final BigDecimal GetValDecimalByKey(String key, int scaleN) throws Exception {
        try {
            String str = this.GetValStrByKey(key);
            if (DataType.IsNullOrEmpty(str))
                return new BigDecimal(0);

            BigDecimal bd = new BigDecimal(this.GetValStrByKey(key));
            return bd.setScale(scaleN, BigDecimal.ROUND_HALF_UP);

        } catch (RuntimeException ex) {
            throw new RuntimeException("@表[" + this.getEnDesc() + "]在获取属性[" + key + "]值,出现错误，不能将[" + this.GetValStrByKey(key) + "]转换为float类型.错误信息：" + ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final BigDecimal GetValDecimalByKeyIsNullAsVal(String key, BigDecimal val) {
        try {
            return GetValDecimalByKey(key, 4);
        } catch (RuntimeException ex) {
            return val;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final BigDecimal GetValDecimalByKey(String key, String items) {
        if (DataType.IsNullOrEmpty(items) == true) {
            return new BigDecimal(0);
        }

        if (items.indexOf("@" + key) == -1) {
            return new BigDecimal(0);
        }

        String str = items.substring(items.indexOf("@" + key));
        BigDecimal bd = new BigDecimal(str);
        return bd.setScale(4, BigDecimal.ROUND_HALF_UP);


    }

    public final double GetValDoubleByKey(String key) throws Exception {
        try {
            return new DecimalFormat().parse(this.GetValStrByKey(key)).doubleValue();//Double.parseDouble(this.GetValStrByKey(key));
        } catch (Exception ex) {
            throw new RuntimeException("@表[" + this.getEnDesc() + "]在获取属性[" + key + "]值,出现错误，不能将[" + this.GetValStringByKey(key) + "]转换为double类型.错误信息：" + ex.getMessage());
        }
    }

    public final String GetValAppDateByKey(String key) throws Exception {
        try {
            String str = this.GetValStringByKey(key);
            if (str == null || str.equals("")) {
                return str;
            }
            return DataType.StringToDateStr(str);
        } catch (RuntimeException ex) {
            throw new RuntimeException("@实例：[" + this.getEnMap().getEnDesc() + "]  属性[" + key + "]值[" + this.GetValStringByKey(key).toString() + "]日期格式转换出现错误：" + ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    ///属性

    /**
     * 是否是空白的实体？
     */
    public final boolean getIsBlank() throws Exception {
        if (this._row == null) {
            return true;
        }

        Attrs attrs = this.getEnMap().getAttrs();
        for (Attr attr : attrs) {
            if (attr.getUIIsReadonly() && attr.getItIsFKorEnum() == false) {
                continue;
            }

            //日期类型.  这里需要翻译.
            if (attr.getKey().equals("RDT") || attr.getKey().equals("Rec")) {
                continue;
            }

            if (attr.getDefaultValOfReal() != null && attr.getDefaultValOfReal().contains("@") == true) {
                continue;
            }

            if (attr.getItIsFK()) {
                if (this.GetValByKey(attr.getKey()).equals("") || this.GetValByKey(attr.getKey()) == attr.getDefaultValOfReal()) {
                    continue;
                }
                return false;
            }

            String str = this.GetValStrByKey(attr.getKey());

            //判断是否是数值类型.
            if (attr.getItIsNum() == true) {
                if (this.GetValFloatByKey(attr.getKey()) != Float.parseFloat(attr.getDefaultValOfReal())) {
                    return false;
                }
            }

            if (DataType.IsNullOrEmpty(str) == true) {
                continue;
            }

            if (str.equals(attr.getDefaultVal()) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取或者设置
     * 是不是空的实体.
     */
    public final boolean getIsEmpty() throws Exception {
        if (this._row == null) {
            return true;
        } else {
            if (this.getPKVal() == null || this.getPKVal().toString().equals("0") || this.getPKVal().toString().equals("")) {
                return true;
            }
            return false;
        }
    }

    public final void setIsEmpty(boolean value) {
        this._row = null;
    }

    /**
     * 对这个实体的描述
     *
     * @
     */
    public final String getEnDesc() {
        return this.getEnMap().getEnDesc();
    }

    /**
     * 取到主健值。如果它的主健不唯一，就返回第一个值。
     * 获取或设置
     */
    public final Object getPKVal() {
        return this.GetValByKey(this.getPK());
    }

    public final void setPKVal(Object value) {
        this.SetValByKey(this.getPK(), value);
    }

    /**
     * 如果只有一个主键,就返回PK,如果有多个就返回第一个.PK
     */
    public final int getPKCount() {
        switch (this.getPK()) {
            case "OID":
            case "No":
            case "MyPK":
                return 1;
            default:
                break;
        }

        int i = 0;
        for (Attr attr : this.getEnMap().getAttrs()) {
            if (attr.getMyFieldType() == FieldType.PK || attr.getMyFieldType() == FieldType.PKEnum || attr.getMyFieldType() == FieldType.PKFK) {
                i++;
            }
        }
        if (i == 0) {
            throw new RuntimeException("@没有给【" + this.getEnDesc() + "，" + this.getEnMap().getPhysicsTable() + "】定义主键。");
        } else {
            return i;
        }
    }

    /**
     * 是不是OIDEntity
     */
    public final boolean getItIsOIDEntity() {
        if (this.getPK().equals("OID")) {
            return true;
        }
        return false;
    }

    /**
     * 是不是OIDEntity
     */
    public final boolean getItIsNoEntity() {
        if (this.getPK().equals("No")) {
            return true;
        }
        return false;
    }

    /**
     * 是否是TreeEntity
     */
    public final boolean getItIsTreeEntity() {
        if (this.getClass().getSuperclass().getName().equals("bp.en.EntityTree") == true) {
            return true;
        }
        return false;
    }

    /**
     * 如果只有一个主键,就返回PK,如果有多个就返回第一个.PK
     */
    public String getPK() {
        String pks = "";
        for (Attr attr : this.getEnMap().getAttrs()) {
            switch (attr.getKey()) {
                case "OID":
                    return "OID";
                case "No":
                    return "No";
                case "MyPK":
                    return "MyPK";
                default:
                    break;
            }

            if (attr.getMyFieldType() == FieldType.PK || attr.getMyFieldType() == FieldType.PKEnum || attr.getMyFieldType() == FieldType.PKFK) {
                pks += attr.getKey() + ",";
            }
        }
        if (pks.equals("")) {
            throw new RuntimeException("@没有给【" + this.getEnDesc() + "，" + this.getEnMap().getPhysicsTable() + "】定义主键。");
        }
        pks = pks.substring(0, pks.length() - 1);
        return pks;
    }

   /* public String getPK_Field() throws Exception {
        for (Attr attr : this.getEnMap().getAttrs()) {
            switch (attr.getKey()) {
                case "OID":
                    return attr.getField();
                case "No":
                    return attr.getField();
                case "MyPK":
                    return attr.getField();
                default:
                    break;
            }

            if (attr.getMyFieldType() == FieldType.PK || attr.getMyFieldType() == FieldType.PKEnum || attr.getMyFieldType() == FieldType.PKFK) {
                return attr.getField();
            }
        }

        throw new RuntimeException("@没有给【" + this.getEnDesc() + "】定义主键。");
    }
*/
    public String getPKField() {
        for (Attr attr : this.getEnMap().getAttrs()) {
            switch (attr.getKey()) {
                case "OID":
                    return attr.getField();
                case "No":
                    return attr.getField();
                case "MyPK":
                    return attr.getField();
                default:
                    break;
            }

            if (attr.getMyFieldType() == FieldType.PK || attr.getMyFieldType() == FieldType.PKEnum || attr.getMyFieldType() == FieldType.PKFK) {
                return attr.getField();
            }
        }

        throw new RuntimeException("@没有给【" + this.getEnDesc() + "】定义主键。");
    }

    /**
     * 如果只有一个主键,就返回PK,如果有多个就返回第一个.PK
     */
    public final String[] getPKs() {
        String[] strs1 = new String[this.getPKCount()];
        int i = 0;
        for (Attr attr : this.getEnMap().getAttrs()) {
            if (attr.getMyFieldType() == FieldType.PK || attr.getMyFieldType() == FieldType.PKEnum || attr.getMyFieldType() == FieldType.PKFK) {
                strs1[i] = attr.getKey();
                i++;
            }
        }
        return strs1;
    }

    /**
     * 取到主健值。
     */
    public final Hashtable getPKVals() {
        Hashtable ht = new Hashtable();
        String[] strs = this.getPKs();
        for (String str : strs) {
            ht.put(str, this.GetValStringByKey(str));
        }
        return ht;
    }

}
