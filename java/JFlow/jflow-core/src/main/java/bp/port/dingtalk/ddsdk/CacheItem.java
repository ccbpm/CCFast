package bp.port.dingtalk.ddsdk;

/**
 * 缓存项
 */
public class CacheItem {

    ///#region 属性
    private Object _value;

    public final Object getValue() {
        return _value;
    }

    public final void setValue(Object value) {
        _value = value;
    }

    private String _key;

    public final String getKey() {
        return _key;
    }

    public final void setKey(String value) {
        _key = value;
    }
    ///#endregion

    ///#region 内部变量
    /**
     * 插入时间
     */
    private long _insertTime;
    /**
     * 过期时间
     */
    private int _expire;
    ///#endregion

    ///#region 构造函数

    /**
     * 构造函数
     *
     * @param key    缓存的KEY
     * @param value  缓存的VALUE
     * @param expire 缓存的过期时间
     */
    public CacheItem(String key, Object value, int expire) {
        this._key = key;
        this._value = value;
        this._expire = expire;
        this._insertTime = TimeStamp.Now();
    }
    ///#endregion

    ///#region Expired

    /**
     * 是否过期
     *
     * @return
     */
    public final boolean Expired() {
        return TimeStamp.Now() > this._insertTime + _expire;
    }
    ///#endregion
}
