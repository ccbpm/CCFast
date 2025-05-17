package bp.port.dingtalk.ddsdk;

import java.util.*;

/**
 * 简易缓存
 */
public class SimpleCacheProvider implements ICacheProvider {
    private static SimpleCacheProvider _instance = null;

    ///#region GetInstance

    /**
     * 获取缓存实例
     *
     * @return
     */
    public static SimpleCacheProvider GetInstance() {
        if (_instance == null) {
            _instance = new SimpleCacheProvider();
        }
        return _instance;
    }
    ///#endregion

    private HashMap<String, CacheItem> _caches;

    private SimpleCacheProvider() {
        this._caches = new HashMap<String, CacheItem>();
    }

    ///#region GetCache

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @return 缓存对象
     */
    public Object GetCache(String key) {
        if (_caches.containsKey(key)) {
            CacheItem item = _caches.get(key);
            if (!item.Expired()) {
                return item.getValue();
            }
        }
        return null;
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @param <T> 缓存对象类型
     * @return 缓存对象
     */
    public <T> T GetCache(String key, Class<T> clazz) {
        Object obj = GetCache(key);
        if (obj == null) {
            return null;
        }
        return clazz.cast(obj);
    }
    ///#endregion

    ///#region SetCache

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     */

    public final void SetCache(String key, Object value) {
        SetCache(key, value, 300);
    }

    /**
     * 设置缓存
     *
     * @param key    键
     * @param value  值
     * @param expire 有效期，默认300
     */
    public final void SetCache(String key, Object value, int expire) {
        this._caches.put(key, new CacheItem(key, value, expire));
    }
    ///#endregion
}
