package bp.en;

import bp.web.*;

import java.io.Serializable;
import java.util.Enumeration;


import bp.da.DataType;

/**
 * SearchKey 的摘要说明。
 * 用来处理一条记录的存放，问题。
 */
public class SearchNormal implements Serializable {

    private static final long serialVersionUID = 1L;
    ///基本属性
    /**
     * 是否隐藏
     */
    private boolean _IsHidden = false;

    /**
     * 是否隐藏
     */
    public final boolean getItIsHidden() {
        return _IsHidden;
    }

    public final void setItIsHidden(boolean value) {
        _IsHidden = value;
    }

    /**
     * 操作是否可用
     */
    private boolean _SymbolEnable = true;

    /**
     * 操作是否可用
     */
    public final boolean getSymbolEnable() {
        return _SymbolEnable;
    }

    public final void setSymbolEnable(boolean value) {
        _SymbolEnable = value;
    }

    /**
     * 标签
     */
    private String _Lab = "";

    /**
     * 标签
     */
    public final String getLab() {
        return _Lab;
    }

    public final void setLab(String value) {
        _Lab = value;
    }

    /**
     * 查询默认值
     */
    private String _DefaultVal = "";

    /**
     * OperatorKey
     */
    public final String getDefaultVal() {
        return _DefaultVal;
    }

    public final void setDefaultVal(String value) {
        _DefaultVal = value;
    }

    /**
     * 默认值
     *
     * @
     */
    public final String getDefaultValRun() throws Exception {
        if (_DefaultVal == null) {
            return null;
        }

        if (_DefaultVal.contains("@")) {
            if (_DefaultVal.contains("@WebUser.No")) {
                return _DefaultVal.replace("@WebUser.No", WebUser.getNo());
            }

            if (_DefaultVal.contains("@WebUser.Name")) {
                return _DefaultVal.replace("@WebUser.Name", WebUser.getName());
            }

            if (_DefaultVal.contains("@WebUser.FK_Dept")) {
                return _DefaultVal.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
            }

            if (_DefaultVal.contains("@WebUser.DeptNo"))
                return _DefaultVal.replace("@WebUser.DeptNo", WebUser.getDeptNo());

            if (_DefaultVal.contains("@WebUser.DeptNo")) {
                return _DefaultVal.replace("@WebUser.DeptNo", WebUser.getDeptNo());
            }


            if (_DefaultVal.contains("@WebUser.DeptParentNo")) {
                return _DefaultVal.replace("@WebUser.DeptParentNo", WebUser.getDeptParentNo());
            }

            if (_DefaultVal.contains("@WebUser.FK_DeptName")) {
                return _DefaultVal.replace("@WebUser.FK_DeptName", WebUser.getDeptName());
            }

            if (_DefaultVal.contains("@WebUser.OrgNo")) {
                return _DefaultVal.replace("@WebUser.OrgNo", WebUser.getOrgNo());
            }


            if (_DefaultVal.contains("@WebUser.FK_DeptNameOfFull")) {
                return _DefaultVal.replace("@WebUser.FK_DeptNameOfFull", WebUser.getDeptNameOfFull());
            }

            // 处理传递过来的参数。
            Enumeration enu = bp.sys.base.Glo.getRequest().getParameterNames();
            while (enu.hasMoreElements()) {
                // 判断是否有内容，hasNext()
                String k = (String) enu.nextElement();
                if (bp.da.DataType.IsNullOrEmpty(k)) {
                    continue;
                }
                return _DefaultVal.replace("@" + k, bp.sys.base.Glo.getRequest().getParameter(k));
            }


        }
        return _DefaultVal;
    }

    /**
     * 默认的操作符号.
     */
    private String _defaultSymbol = "=";

    /**
     * 操作符号
     */
    public final String getDefaultSymbol() {
        return _defaultSymbol;
    }

    public final void setDefaultSymbol(String value) {
        _defaultSymbol = value;
    }

    /**
     * 对应的属性
     */
    private String _RefAttr = "";

    /**
     * 对应的属性
     */
    public final String getRefAttrKey() {
        return _RefAttr;
    }

    public final void setRefAttrKey(String value) {
        _RefAttr = value;
    }

    /**
     * Key
     */
    private String _Key = "";

    /**
     * Key
     */
    public final String getKey() {
        return _Key;
    }

    public final void setKey(String value) {
        _Key = value;
    }

    /**
     * TB 宽度
     */
    private int _TBWidth = 10;

    /**
     * TBWidth
     */
    public final int getTBWidth() {
        return _TBWidth;
    }

    public final void setTBWidth(int value) {
        _TBWidth = value;
    }

    ///


    ///构造方法
    public SearchNormal()
    {
    }
    /**
     * 构造一个普通的查询属性
     *
     * @
     */
    public SearchNormal(String key, String lab, String refAttr, String DefaultSymbol, String defaultValue, int tbwidth, boolean isHidden) {
        this.setKey(key);
        this.setLab(lab);
        this.setRefAttrKey(refAttr);
        this.setDefaultSymbol(DefaultSymbol);
        this.setDefaultVal(defaultValue);
        this.setTBWidth(tbwidth);
        this.setItIsHidden(isHidden);
    }

    ///
}
