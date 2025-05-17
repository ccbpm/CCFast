package bp.port.dingtalk.ddsdk;

/**
 * 缓存接口
 */
public interface ICacheProvider {
    /**
     * 获取缓存
     *
     * @param key 缓存key
     * @return 缓存对象或null, 不存在或者过期返回null
     */
    Object GetCache(String key);

    /**
     * 写入缓存
     *
     * @param key   缓存key
     * @param value 缓存值
     */

    void SetCache(String key, Object value);

    void SetCache(String key, Object value, int expire);
}
