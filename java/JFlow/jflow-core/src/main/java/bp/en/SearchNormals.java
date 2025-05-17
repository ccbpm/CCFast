package bp.en;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * SearchKey 集合
 */
public class SearchNormals extends ArrayList<SearchNormal>  implements Serializable {
    private static final long serialVersionUID = 1L;

    ///构造
    public SearchNormals() {
    }


    ///


    ///增加一个查询属性。

    /**
     * 增加一个隐藏的查询属性
     * <p>
     * param refKey 关联key
     * param symbol 操作符号
     * param val 操作的val.
     *
     * @
     */
    public final void AddHidden(String refKey, String symbol, String val) {
        SearchNormal aos = new SearchNormal("K" + this.size(), refKey, refKey, symbol, val, 0, true);
        this.add(aos);
    }

    /**
     * 增加一个查询属性
     * <p>
     * param lab 标签
     * param refKey 实体的属性
     * param defaultvalue 默认值
     *
     * @
     */
    public final void Add(String lab, String refKey, String defaultSymbol, String defaultvalue, int tbWidth) {
        SearchNormal aos = new SearchNormal("K" + this.size(), lab, refKey, defaultSymbol, defaultvalue, tbWidth, false);
        this.add(aos);
    }

    public final void Add(SearchNormal en) {
        this.add(en);
    }

    /**
     * 增加2个属性。
     * <p>
     * param lab 标题
     * param refKey 关联的Key
     * param defaultvalueOfFrom 默认值从
     * param defaultvalueOfTo 默认值从
     * param tbWidth 宽度
     *
     * @
     */
    public final void AddFromTo(String lab, String refKey, String defaultvalueOfFrom, String defaultvalueOfTo, int tbWidth) {
        SearchNormal aos = new SearchNormal("Form_" + refKey, lab + "从", refKey, ">=", defaultvalueOfFrom, tbWidth, false);
        aos.setSymbolEnable(false);
        this.add(aos);

        SearchNormal aos1 = new SearchNormal("To_" + refKey, "到", refKey, "<=", defaultvalueOfTo, tbWidth, false);
        aos1.setSymbolEnable(false);
        this.add(aos1);

    }

    ///
}
