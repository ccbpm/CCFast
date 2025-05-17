package bp.en;

import bp.da.*;
import bp.sys.MapAttr;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 属性
 */
public class Attr  implements Serializable {

    private static final long serialVersionUID = 1L;
    //属性
    public String GroupName = "基本信息";
    private int _IsKeyEqualField = -1;
    ///属性
    public String HelperUrl = null;
    /**
     * 属性名称
     */
    public String _key = null;
    /**
     * 属性对应的字段
     */
    private String _field = null;
    private int _DefValType = 0;
    /**
     * 字段默认值
     */
    private Object _defaultVal = null;
    /**
     * 是不是主键。
     */
    private FieldType _FieldType = FieldType.Normal;
    /**
     * 数据类型。
     */
    private int _dataType = 0;
    /**
     * 描述。
     */
    private String _desc = null;
    /**
     * 最大长度。
     */
    private int _maxLength = 4000;
    /**
     * 最小长度。
     */
    private int _minLength = 0;
    private float _UIWidth = 80;
    private int _UIHeight = 0;
    private boolean _UIVisible = true;
    /**
     * 是否单行显示
     */
    public boolean UIIsLine = false;
    private boolean _UIIsReadonly = false;
    private UIContralType _UIContralType = UIContralType.TB;
    private String _UIBindKey = null;
    private int _IsSupperText = 0;
    private Entities _HisFKEns = null;
    private String _UIRefKey = null;
    private String _UIRefText = null;

    public Attr(){

    }

    /**
     * 构造函数
     * <p>
     * param key
     * param field
     * param defaultVal
     * param dataType
     * param isPK
     * param desc
     */
    public Attr(String key, String field, Object defaultVal, int dataType, boolean isPK, String desc, int minLength, int maxlength) {
        this._key = key;
        this._field = field;
        this._desc = desc;
        if (isPK) {
            this.setMyFieldType(FieldType.PK);
        }
        this._dataType = dataType;
        this._defaultVal = defaultVal;
        this._minLength = minLength;
        this._maxLength = maxlength;
    }

    public Attr(String key, String field, Object defaultVal, int dataType, boolean isPK, String desc) {
        this._key = key;
        this._field = field;
        this._desc = desc;
        if (isPK) {
            this.setMyFieldType(FieldType.PK);
        }
        this._dataType = dataType;
        this._defaultVal = defaultVal;
    }
    public final MapAttr getToMapAttr() throws Exception {

        MapAttr attr = new MapAttr();
        attr.setKeyOfEn(this.getKey());
        attr.setName(this.getDesc());
        attr.setDefVal(this.getDefaultVal().toString());
        attr.setKeyOfEn(this.getField());
        attr.setMaxLen(this.getMaxLength());
        attr.setMinLen(this.getMinLength());
        attr.setUIBindKey(this.getUIBindKey());
        attr.setUIIsLine(this.UIIsLine);
        attr.setUIHeight(0);
        attr.setDefValType(this.getDefValType());
        if (this.getUIHeight() > 10) {
            if (this.UIIsLine == true)
                attr.setColSpan(4);
            else
                attr.setColSpan(3);
        } else {
            if (this.UIIsLine == true)
                attr.setColSpan(3);
        }
        if (this.getMaxLength() > 3000) {
            attr.setUIHeight(10);
        }

        attr.setUIWidth(this.getUIWidth());
        attr.setMyDataType(this.getMyDataType());

        attr.setUIRefKey(this.getUIRefKeyValue());

        attr.setUIRefKeyText(this.getUIRefKeyText());
        attr.setUIVisible(this.getUIVisible());
        attr.setUIIsEnable(!this.getUIIsReadonly());
        //帮助url.
        attr.SetPara("HelpUrl", this.HelperUrl);
        attr.setUIRefKeyText(this.getUIRefKeyText());
        attr.setUIRefKey(this.getUIRefKeyValue());

        switch (this.getMyFieldType()) {
            case Enum:
            case PKEnum:
                attr.setUIContralType(this.getUIContralType());
                attr.setLGType(FieldTypeS.Enum);
                break;
            case FK:
            case PKFK:
                attr.setUIContralType(this.getUIContralType());
                attr.setLGType(FieldTypeS.FK);
                attr.setUIRefKey("No");
                attr.setUIRefKeyText("Name");
                break;
            default:
                attr.setUIContralType(this.getUIContralType());
                attr.setLGType(FieldTypeS.Normal);

                if (this.getItIsSupperText() == 1)
                    attr.setTextModel(3);
                switch (this.getMyDataType()) {
                    case DataType.AppBoolean:
                        attr.setUIContralType(UIContralType.CheckBok);
                        break;
                    case DataType.AppDate:
                        break;
                    case DataType.AppDateTime:
                        break;
                    default:
                        break;
                }
                break;
        }

        return attr;
    }

    public final boolean getItIsFK() {
        if (this.getMyFieldType() == FieldType.FK || this.getMyFieldType() == FieldType.PKFK) {
            return true;
        } else {
            return false;
        }
    }

    public final boolean getItIsFKorEnum() {
        if (this.getMyFieldType() == FieldType.Enum || this.getMyFieldType() == FieldType.PKEnum || this.getMyFieldType() == FieldType.FK || this.getMyFieldType() == FieldType.PKFK) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是不是能使用默认值。
     */

    public final boolean getItIsNum() {
        if (getMyDataType() == DataType.AppBoolean || getMyDataType() == DataType.AppDouble || getMyDataType() == DataType.AppFloat || getMyDataType() == DataType.AppInt || getMyDataType() == DataType.AppMoney) {
            return true;
        } else {
            return false;
        }
    }

    public final boolean getItIsEnum() {
        if (getMyFieldType() == FieldType.Enum || getMyFieldType() == FieldType.PKEnum) {
            return true;
        } else {
            return false;
        }
    }

    public final boolean getItIsRefAttr() {
        if (this.getMyFieldType() == FieldType.RefText) {
            return true;
        }
        return false;
    }

    /**
     * 计算属性是不是PK
     */
    public final boolean getItIsPK() {
        if (getMyFieldType() == FieldType.PK || getMyFieldType() == FieldType.PKFK || getMyFieldType() == FieldType.PKEnum) {
            return true;
        } else {
            return false;
        }
    }



    public final boolean getItIsKeyEqualField() {
        if (_IsKeyEqualField == -1) {
            if (this.getKey().equals(this.getField())) {
                _IsKeyEqualField = 1;
            } else {
                _IsKeyEqualField = 0;
            }
        }

        if (_IsKeyEqualField == 1) {
            return true;
        }
        return false;
    }

    /**
     * 输入描述
     */
    /*public final String getEnterDesc() {
        if (this.getUIContralType() == UIContralType.TB) {
            if (this.getUIIsReadonly() || this.getUIVisible() == false) {
                return "此字段只读";
            } else {
                if (this.getMyDataType() == DataType.AppDate) {
                    return "输入日期类型" + DataType.getSysDateFormat();
                } else if (this.getMyDataType() == DataType.AppDateTime) {
                    return "输入日期时间类型" + DataType.getSysDateTimeFormat();
                } else if (this.getMyDataType() == DataType.AppString) {
                    return "输入要求最小长度" + this.getMinLength() + "字符，最大长度" + this.getMaxLength() + "字符";
                } else if (this.getMyDataType() == DataType.AppMoney) {
                    return "金额类型 0.00";
                } else {
                    return "输入数值类型";
                }
            }

        } else if (this.getUIContralType() == UIContralType.DDL || this.getUIContralType() == UIContralType.CheckBok) {
            if (this.getUIIsReadonly()) {
                return "此字段只读";
            } else {
                if (this.getMyDataType() == DataType.AppBoolean) {
                    return "是/否";
                } else {
                    return "列表选择";
                }
            }
        }

        return "";
    }*/


    ///

    /**
     * 属性名称
     */
    public final String getKey() {
        return this._key;
    }

    public final void setKey(String value) {
        if (value != null) {
            this._key = value.trim();
        }
    }



    /**
     * 属性对应的字段
     *
     * @return
     */
    public final String getField() {
        return this._field;
    }

    public final void setField(String value) {
        if (value != null) {
            this._field = value.trim();
        }
    }



    public final int getDefValType() {
        return this._DefValType;
    }

    public final void setDefValType(int value) {
        if (value != 0) {
            this._DefValType = value;
        }
    }



    public final String getDefaultValOfReal() {
        if (_defaultVal == null) {
            return null;
        }
        return _defaultVal.toString();
    }

    public final void setDefaultValOfReal(String value) {
        _defaultVal = value;
    }

    /**
     * 字段默认值
     */
    public final Object getDefaultVal() {
        switch (this.getMyDataType()) {
            case DataType.AppString:
            case DataType.AppDate:
            case DataType.AppDateTime:
                if (this._defaultVal == null) {
                    return "";
                }
                break;
            case DataType.AppInt:
                if (this._defaultVal == null) {
                    if(this.getMyFieldType() == FieldType.Enum || this.getMyFieldType() == FieldType.PKEnum)
                        return 0;
                    return null;
                }
                try {
                    return Integer.parseInt(this._defaultVal.toString());
                } catch (java.lang.Exception e) {
                    return null;
                    //throw new Exception("@设置["+this.Key+"]默认值出现错误，["+_defaultVal.ToString()+"]不能向 int 转换。");
                }
            case DataType.AppMoney:
                if (this._defaultVal == null) {
                    return null;
                }
                try {
                    return new BigDecimal(this._defaultVal.toString());
                } catch (java.lang.Exception e2) {
                    return null;
                    //	throw new Exception("@设置["+this.Key+"]默认值出现错误，["+_defaultVal.ToString()+"]不能向 AppMoney 转换。");
                }
            case DataType.AppFloat:
                if (this._defaultVal == null) {
                    return null;
                }
                try {
                    return Float.parseFloat(this._defaultVal.toString());
                } catch (java.lang.Exception e3) {
                    return null;
                    //	throw new Exception("@设置["+this.Key+"]默认值出现错误，["+_defaultVal.ToString()+"]不能向 float 转换。");
                }

            case DataType.AppBoolean:
                if (this._defaultVal == null || this._defaultVal.toString().equals("")) {
                    return 0;
                }
                try {
                    if (DataType.StringToBoolean(this._defaultVal.toString())) {
                        return 1;
                    } else {
                        return 0;
                    }
                } catch (java.lang.Exception e4) {
                    //throw new RuntimeException("@设置[" + this.getKey() + "]默认值出现错误，[" + this._defaultVal.toString() + "]不能向 bool 转换，请设置0/1。");
                    return 0;
                }

            case 5:
                if (this._defaultVal == null) {
                    return null;
                }
                try {
                    return Double.parseDouble(this._defaultVal.toString());
                } catch (java.lang.Exception e5) {
                    //throw new RuntimeException("@设置[" + this.getKey() + "]默认值出现错误，[" + _defaultVal.toString() + "]不能向 double 转换。");
                    return null;
                }
            default:
                throw new RuntimeException("@bulider insert sql error: 没有这个数据类型，字段名称:" + this.getDesc() + " 英文:" + this.getKey());
        }
        return this._defaultVal;
    }

    public final void setDefaultVal(Object value) {
        this._defaultVal = value;
    }

    public final int getDefaultDecimalLen(){
        Object defval = this.getDefaultVal();
        if(defval == null)
            return 2;
        String defValS = defval.toString();
        if(DataType.IsNullOrEmpty(defValS))
            return 2;
        if(defValS.contains(".") == false)
            return 2;
        return defValS.split("[.]")[1].length();
    }



    /**
     * 数据类型。
     */
    public final int getMyDataType() {
        return this._dataType;
    }

    public final void setMyDataType(int value) {
        this._dataType = value;
    }

    public final String getMyDataTypeStr() {
        return DataType.GetDataTypeDese(this.getMyDataType());
    }



    /**
     * 是不是主键
     *
     * @return yes / no
     */
    public final FieldType getMyFieldType() {
        return this._FieldType;
    }

    public final void setMyFieldType(FieldType value) {
        this._FieldType = value;
    }


    public final String getDesc() {
        return this._desc;
    }

    public final void setDesc(String value) {
        this._desc = value;
    }

    /**
     * 在线帮助
     */
    public final String getDescHelper() {
        if (this.HelperUrl == null) {
            return this._desc;
        }

        if (this.HelperUrl.contains("script")) {
            return "<a href=\"" + this.HelperUrl + "\"  ><img src='../../Img/Help.png'  height='20px' border=0/>" + this._desc + "</a>";
        } else {
            return "<a href=\"" + this.HelperUrl + "\" target=_blank ><img src='../../Img/Help.png'  height='20px' border=0/>" + this._desc + "</a>";
        }
    }

    public final String getDescHelperIcon() {
        if (this.HelperUrl == null) {
            return this._desc;
        }
        return "<a href=\"" + this.HelperUrl + "\" ><img src='../../Img/Help.png' height='20px' border=0/></a>";
    }



    /**
     * 最大长度。
     */
    public final int getMaxLength() {
        switch (this.getMyDataType()) {
            case DataType.AppDate:
                return 50;
            case DataType.AppDateTime:
                return 50;
            case DataType.AppString:
                if (this.getItIsFK()) {
                    return 100;
                } else {
                    if (this._maxLength == 0) {
                        return 50;
                    }
                    return this._maxLength;
                }
            default:
                if (this.getItIsFK()) {
                    return 100;
                } else {
                    return this._maxLength;
                }
        }
    }

    public final void setMaxLength(int value) {
        this._maxLength = value;
    }



    /**
     * 最小长度。
     */
    public final int getMinLength() {
        return this._minLength;
    }

    public final void setMinLength(int value) {
        this._minLength = value;
    }

    /**
     * 是否可以为空, 对数值类型的数据有效.
     */
    public final boolean getIsNull() {
        if (this.getMinLength() == 0) {
            return false;
        } else {
            return true;
        }
    }

    ///


    ///UI 的扩展属性
    public final int getUIWidthInt() {
        return (int) this.getUIWidth();
    }



    /**
     * 宽度
     */
    public final float getUIWidth() {
        if (this._UIWidth <= 10) {
            return 15;
        } else {
            return this._UIWidth;
        }
    }

    public final void setUIWidth(float value) {
        this._UIWidth = value;
    }



    /**
     * 高度
     */
    public final int getUIHeight() {
        return this._UIHeight * 10;
    }

    public final void setUIHeight(int value) {
        this._UIHeight = value;
    }



    /**
     * 是不是可见
     */
    public final boolean getUIVisible() {
        return this._UIVisible;
    }

    public final void setUIVisible(boolean value) {
        this._UIVisible = value;
    }


    /**
     * 是不是只读
     */
    public final boolean getUIIsReadonly() {
        return this._UIIsReadonly;
    }

    public final void setUIIsReadonly(boolean value) {
        this._UIIsReadonly = value;
    }



    /**
     * 控件类型。
     */
    public final UIContralType getUIContralType() {
        return this._UIContralType;
    }

    public final void setUIContralType(UIContralType value) {
        this._UIContralType = value;
    }



    /**
     * 要Bind 的Key.
     * 在TB 里面就是 DataHelpKey
     * 在DDL 里面是  SelfBindKey.
     */
    public final String getUIBindKey() {
        return this._UIBindKey;
    }

    public final void setUIBindKey(String value) {
        this._UIBindKey = value;
    }
    public final int getItIsSupperText() {
        return this._IsSupperText;
    }
    public final void setItIsSupperText(int value) {
        this._IsSupperText = value;
    }
    public final boolean getUIIsDoc() {
        if (this.getUIHeight() != 0 && this.getUIContralType() == UIContralType.TB) {
            return true;
        } else {
            return false;
        }
    }

    public final Entity getHisFKEn() throws Exception {
        Entities hisFKEns = this.getHisFKEns();
        if (hisFKEns != null) {
            return hisFKEns.getNewEntity();
        }
        return null;
    }

    /**
     * 它关联的ens.这个只有在,这个属性是fk, 时有效。
     */
    public final Entities getHisFKEns() {
        if (_HisFKEns == null) {

            if (this.getMyFieldType() == FieldType.Enum || this.getMyFieldType() == FieldType.PKEnum) {
                return null;
            } else if (this.getMyFieldType() == FieldType.FK || this.getMyFieldType() == FieldType.PKFK) {
                if (this.getUIBindKey().contains(",TS."))
                {
                    int idx = this.getUIBindKey().lastIndexOf(",TS.");
                    String className = this.getUIBindKey().substring(idx + 1);

                    TSEntitiesNoName ens = new TSEntitiesNoName(className);
                    if (ens == null)
                        throw new RuntimeException("err@ClassID=" + className + " 没有注册.");
                    _HisFKEns = ens; // BP.EnTS ClassFactory.GetEns(className);
                }else if (this.getUIBindKey().contains(".")) {
                    _HisFKEns = ClassFactory.GetEns(this.getUIBindKey());
                } else {
                    _HisFKEns = new GENoNames(this.getUIBindKey(), this.getDesc()); // ClassFactory.GetEns(this.getUIBindKey());
                }
            } else if (this.getMyFieldType() == FieldType.Normal && this.getUIContralType() == UIContralType.DDL && DataType.IsNullOrEmpty(this.getUIBindKey()) == false && this.getUIBindKey().toUpperCase().startsWith("SELECT")) {
                String sqlBindKey = bp.tools.PubGlo.DealExp(this.getUIBindKey(), null);
                DataTable dt = DBAccess.RunSQLReturnTable(sqlBindKey);
                //转换成GENoNames
                try {
                    GENoNames noNames = new GENoNames();
                    GENoName noName = null;
                    for (DataRow dr : dt.Rows) {
                        noName = new GENoName(this.getField(), this.getDesc());
                        noName.setNo(dr.getValue(0).toString());
                        noName.setName(dr.getValue(1).toString());
                        noNames.add(noName);
                    }
                    _HisFKEns = noNames;
                } catch (Exception e) {
                    return null;
                }

            } else {
                return null;
            }
        }
        return _HisFKEns;
    }

    public final void setHisFKEns(Entities value) {
        _HisFKEns = value;
    }



    /**
     * 要Bind 的Key. 在TB 里面就是 DataHelpKey
     * 在DDL 里面是SelfBindKey.
     */
    public final String getUIRefKeyValue() {
        return this._UIRefKey;
    }

    public final void setUIRefKeyValue(String value) {
        this._UIRefKey = value;
    }



    /**
     * 关联的实体valkey
     */
    public final String getUIRefKeyText() {
        return this._UIRefText;
    }

    public final void setUIRefKeyText(String value) {
        this._UIRefText = value;
    }

    public String UITag = null;

    ///
}
