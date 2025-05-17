package bp.difference.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name="spring.data.redis.repositories.enabled", havingValue="true", matchIfMissing=false)
public class RedisUtils
{
    /**
     * 注入自定义redisTemplate bean
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisTemplate<String, Object> shiroRedisTemplate;

    /**
     * 指定缓存失效时间
     *
     * @param isShiroRedis 是否属于shiroredis缓存
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(Boolean isShiroRedis, String key, long time)
    {
        try
        {
            if (isShiroRedis)
            {
                if (time > 0)
                {

                    shiroRedisTemplate.expire(key, time, TimeUnit.MINUTES);

                }
            }
            else
            {
                if (time > 0)
                {

                    redisTemplate.expire(key, time, TimeUnit.MINUTES);

                }
            }

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public void removeByKey(Boolean isShiroRedis,String key){
        String oldKey = "*"+key + "*";
        // keys()方法 类似于集合里面的contains
        Set<String> keys =isShiroRedis?shiroRedisTemplate.keys(oldKey):redisTemplate.keys(oldKey);
        // 批量删除 直接将keys集合传入即可
        if(isShiroRedis)
            shiroRedisTemplate.delete(keys);
        else
            redisTemplate.delete(keys);
    }
    /**
     * 根据key获取过期时间
     *
     * @param isShiroRedis shiroRedisCache
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(Boolean isShiroRedis, String key)
    {
        return isShiroRedis ? shiroRedisTemplate.getExpire(key, TimeUnit.MINUTES)
                : redisTemplate.getExpire(key, TimeUnit.MINUTES);
    }

    /**
     * 判断key是否存在
     *
     * @param isShiroRedis shiroRedisCache
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(Boolean isShiroRedis, String key)
    {
        try
        {
            return Boolean.TRUE.equals(isShiroRedis ? shiroRedisTemplate.hasKey(key) : redisTemplate.hasKey(key));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(Boolean isShiroRedis, String... key)
    {
        if (isShiroRedis)
        {
            if (key != null && key.length > 0)
            {
                if (key.length == 1)
                {
                    shiroRedisTemplate.delete(key[0]);
                }
                else
                {
                    shiroRedisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
                }
            }
        }
        else
        {

            if (key != null && key.length > 0)
            {
                if (key.length == 1)
                {
                    redisTemplate.delete(key[0]);
                }
                else
                {
                    redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
                }
            }
        }

    }

    /**
     * 批量删除key
     *
     * @param keys
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void del(Boolean isShiroRedis, Collection keys)
    {

        if (isShiroRedis)
        {
            shiroRedisTemplate.delete(keys);
        }
        else
        {
            redisTemplate.delete(keys);
        }

    }

    /**
     * 使用scan命令 查询某些前缀的key
     *
     * @param key
     * @return
     */
  /*  public Set<String> scan(Boolean isShiroRedis, String key)
    {
        if (isShiroRedis)
        {
            Set<String> execute = this.shiroRedisTemplate.execute((RedisCallback<Set<String>>) connection -> {

                Set<String> binaryKeys = new HashSet<>();

                Cursor<byte[]> cursor = connection
                        .scan(new ScanOptions.ScanOptionsBuilder().match(key).count(1000).build());
                while (cursor.hasNext())
                {
                    binaryKeys.add(new String(cursor.next()));
                }
                return binaryKeys;
            });
            return execute;
        }
        else
        {

            Set<String> execute = this.redisTemplate.execute(new RedisCallback<Set<String>>()
            {

                @Override
                public Set<String> doInRedis(RedisConnection connection)
                {

                    Set<String> binaryKeys = new HashSet<>();

                    Cursor<byte[]> cursor = connection
                            .scan(new ScanOptions.ScanOptionsBuilder().match(key).count(1000).build());
                    while (cursor.hasNext())
                    {
                        binaryKeys.add(new String(cursor.next()));
                    }
                    return binaryKeys;
                }
            });
            return execute;
        }

    }

    *//**
     * 使用scan命令 查询某些前缀的key 有多少个 可用来获取当前session数量,也就是在线用户
     *
     * @param key
     * @return
     *//*
    public Long scanSize(Boolean isShiroRedis, String key)
    {
        if (isShiroRedis)
        {
            long dbSize = this.shiroRedisTemplate.execute((RedisCallback<Long>) connection -> {
                long count = 0L;
                Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(key).count(1000).build());
                while (cursor.hasNext())
                {
                    cursor.next();
                    count++;
                }
                return count;
            });
            return dbSize;
        }
        else
        {
            long dbSize = this.redisTemplate.execute(new RedisCallback<Long>()
            {

                @Override
                public Long doInRedis(RedisConnection connection)
                {
                    long count = 0L;
                    Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(key).count(1000).build());
                    while (cursor.hasNext())
                    {
                        cursor.next();
                        count++;
                    }
                    return count;
                }
            });
            return dbSize;
        }
    }*/

    // ============================String(字符串)=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(Boolean isShiroRedis, String key)
    {
        return isShiroRedis ? (key == null ? null : shiroRedisTemplate.opsForValue().get(key))
                : (key == null ? null : redisTemplate.opsForValue().get(key));
    }

    /**
     * 普通缓存放入
     *
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(Boolean isShiroRedis, String key, Object value)
    {
        try
        {

            if (isShiroRedis)
            {

                shiroRedisTemplate.opsForValue().set(key, value);
            }
            else
            {
                redisTemplate.opsForValue().set(key, value);
            }

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key 键
     * @param value 值
     * @param time 时间(分钟) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(Boolean isShiroRedis, String key, Object value, long time)
    {
        try
        {
            if (isShiroRedis)
            {
                if (time > 0)
                {
                    shiroRedisTemplate.opsForValue().set(key, value, time, TimeUnit.MINUTES);
                }
                else
                {
                    set(isShiroRedis, key, value);
                }
            }
            else
            {

                if (time > 0)
                {
                    redisTemplate.opsForValue().set(key, value, time, TimeUnit.MINUTES);
                }
                else
                {
                    set(isShiroRedis, key, value);
                }
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(Boolean isShiroRedis, String key, long delta)
    {
        if (delta < 0)
        {
            throw new RuntimeException("递增因子必须大于0");
        }
        return isShiroRedis ? shiroRedisTemplate.opsForValue().increment(key, delta)
                : redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key 键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(Boolean isShiroRedis, String key, long delta)
    {
        if (delta < 0)
        {
            throw new RuntimeException("递减因子必须大于0");
        }
        return isShiroRedis ? shiroRedisTemplate.opsForValue().increment(key, -delta)
                : redisTemplate.opsForValue().increment(key, -delta);
    }
    // ================================Hash(哈希)=================================
    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Hashtable<String, Object> hget(Boolean isShiroRedis,String key) {
        Map map =  isShiroRedis ? shiroRedisTemplate.opsForHash().entries(key):redisTemplate.opsForHash().entries(key);
        return CovertFromMap(map);
    }
    /**
     * HashGet
     *
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(Boolean isShiroRedis, String key, String item)
    {
        return isShiroRedis ? shiroRedisTemplate.opsForHash().get(key, item)
                : redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(Boolean isShiroRedis, String key)
    {
        return isShiroRedis ? shiroRedisTemplate.opsForHash().entries(key) : redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(Boolean isShiroRedis, String key, Map<String, Object> map)
    {
        try
        {

            if (isShiroRedis)
            {

                shiroRedisTemplate.opsForHash().putAll(key, map);
            }
            else
            {
                redisTemplate.opsForHash().putAll(key, map);
            }

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param isShiroRedis 是否为shiroredis缓存
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(Boolean isShiroRedis, String key, Map<String, Object> map, long time)
    {
        try
        {
            if (isShiroRedis)
            {
                shiroRedisTemplate.opsForHash().putAll(key, map);
                if (time > 0)
                {
                    expire(isShiroRedis, key, time);
                }
            }
            else
            {
                redisTemplate.opsForHash().putAll(key, map);
                if (time > 0)
                {
                    expire(isShiroRedis, key, time);
                }
            }

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(Boolean isShiroRedis, String key, String item, Object value)
    {
        try
        {
            if (isShiroRedis)
            {
                shiroRedisTemplate.opsForHash().put(key, item, value);
            }
            else
            {
                redisTemplate.opsForHash().put(key, item, value);
            }

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(Boolean isShiroRedis, String key, String item, Object value, long time)
    {
        try
        {
            if (isShiroRedis)
            {
                shiroRedisTemplate.opsForHash().put(key, item, value);
                if (time > 0)
                {
                    expire(isShiroRedis, key, time);
                }
            }
            else
            {
                redisTemplate.opsForHash().put(key, item, value);
                if (time > 0)
                {
                    expire(isShiroRedis, key, time);
                }
            }

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(Boolean isShiroRedis, String key, Object... item)
    {

        if (isShiroRedis)
        {
            shiroRedisTemplate.opsForHash().delete(key, item);
        }
        else
        {
            redisTemplate.opsForHash().delete(key, item);
        }

    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item)
    {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by)
    {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by)
    {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }
    // ============================Set(集合)=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key)
    {
        try
        {
            return redisTemplate.opsForSet().members(key);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value)
    {
        try
        {
            return redisTemplate.opsForSet().isMember(key, value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values)
    {
        try
        {
            return redisTemplate.opsForSet().add(key, values);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(Boolean isShiroRedis, String key, long time, Object... values)
    {
        try
        {

            if (isShiroRedis)
            {
                Long count = shiroRedisTemplate.opsForSet().add(key, values);
                if (time > 0)
                {
                    expire(isShiroRedis, key, time);
                }
                return count;
            }
            else
            {
                Long count = redisTemplate.opsForSet().add(key, values);
                if (time > 0)
                {
                    expire(isShiroRedis, key, time);
                }
                return count;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key)
    {
        try
        {
            return redisTemplate.opsForSet().size(key);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values)
    {
        try
        {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }
    // ===============================List(列表)=================================

    /**
     * 获取list缓存的内容
     *
     * @param key 键
     * @param start 开始
     * @param end 结束 0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end)
    {
        try
        {
            return redisTemplate.opsForList().range(key, start, end);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key)
    {
        try
        {
            return redisTemplate.opsForList().size(key);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key 键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index)
    {
        try
        {
            return redisTemplate.opsForList().index(key, index);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value)
    {
        try
        {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param isShiroRedis 是否shiroredis缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean lSet(Boolean isShiroRedis, String key, Object value, long time)
    {
        try
        {
            if (isShiroRedis)
            {
                shiroRedisTemplate.opsForList().rightPush(key, value);
                if (time > 0)
                {
                    expire(isShiroRedis, key, time);
                }

            }
            else
            {
                redisTemplate.opsForList().rightPush(key, value);
                if (time > 0)
                {
                    expire(isShiroRedis, key, time);
                }
            }

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value)
    {
        try
        {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param isShiroRedis shiroredisCache
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean lSet(Boolean isShiroRedis, String key, List<Object> value, long time)
    {
        try
        {

            if (isShiroRedis)
            {
                shiroRedisTemplate.opsForList().rightPushAll(key, value);
                if (time > 0)
                {
                    expire(isShiroRedis, key, time);
                }
            }
            else
            {
                redisTemplate.opsForList().rightPushAll(key, value);
                if (time > 0)
                {
                    expire(isShiroRedis, key, time);
                }
            }

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value)
    {
        try
        {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value)
    {
        try
        {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将Map转成Hashtable
     * @param map
     * @return
     */
    private Hashtable CovertFromMap(Map<Object,Object> map){
        Hashtable ht = new Hashtable();
        if(map==null)
            return ht;
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            if(entry.getKey()!=null)
                ht.put( entry.getKey(),entry.getValue());
        }
        return ht;
    }
}
