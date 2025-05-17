package bp.da;
import bp.difference.ContextHolderUtils;
import bp.difference.SystemConfig;
import bp.en.*;

import java.util.*;

public class CacheEntity
{

		///Hashtable 属性
	private static Hashtable<String, Object> _Cache = new Hashtable<>();
	private static String dCacheKey = SystemConfig.getRedisCacheKey("DCache");
	public static  Hashtable<String, Object> getDCache()throws Exception
	{
		/*if(SystemConfig.getRedisIsEnable())
			_Cache = ContextHolderUtils.getRedisUtils().hget(false,dCacheKey);*/
		if (_Cache == null)
			_Cache = new Hashtable();
		return _Cache;
	}
	/**
	 把实体放入缓存里面
	 param enName
	 param ens
	 param enPK
	 * @throws Exception
	*/
	public static void PubEns(String enName, Entities ens, String enPK) throws Exception
	{
		Object tempVar = CacheEntity.getDCache().get(enName);
		Hashtable ht = tempVar instanceof Hashtable ? (Hashtable)tempVar : null;
		if (ht == null)
			ht = new Hashtable();

		ht.clear();
		for (Entity en : ens)
		{
			ht.put(en.GetValStrByKey(enPK), en);
		}
		// 把实体集合放入.
		CacheEntity.getDCache().put(enName + "Ens", ens);

	}
	public static Entities GetEns(String enName) throws Exception {
		Object tempVar = CacheEntity.getDCache().get(enName + "Ens");
		Entities ens = tempVar instanceof Entities ? (Entities)tempVar : null;
		return ens;
	}
	/**
	 更新对象

	 param enName
	 param key
	 param en
	*/
	public static void Update(String enName, String key, Entity en) throws Exception {
		Object tempVar = CacheEntity.getDCache().get(enName);
		Hashtable ht = tempVar instanceof Hashtable ? (Hashtable)tempVar : null;
		if (ht == null)
		{
			ht = new Hashtable();
			CacheEntity.getDCache().put(enName, ht);
		}
		ht.put(key, en);
		//清除集合.
		CacheEntity.getDCache().remove(enName + "Ens");

	}
	/**
	 获取一个实体

	 param enName 实体Name
	 param pkVal 主键值
	 @return 返回这个实体
	*/
	public static Entity Select(String enName, String pkVal) throws Exception {
		Object tempVar = CacheEntity.getDCache().get(enName);
		Hashtable ht = tempVar instanceof Hashtable ? (Hashtable)tempVar : null;
		if (ht == null)
		{
			return null;
		}

		return ht.get(pkVal) instanceof Entity ? (Entity)ht.get(pkVal) : null;
	}
	/**
	 删除

	 param enName
	 param pkVal
	*/
	public static void Delete(String enName, String pkVal) throws Exception {
		Object tempVar = CacheEntity.getDCache().get(enName);
		Hashtable ht = tempVar instanceof Hashtable ? (Hashtable)tempVar : null;
		if (ht == null)
		{
			return;
		}

		ht.remove(pkVal);
		//清除集合.
		CacheEntity.getDCache().remove(enName + "Ens");

	}
	/**
	 插入

	 param enName
	 param en
	 param pkVal
	*/
	public static void Insert(String enName, String pkVal, Entity en) throws Exception {
		Object tempVar = CacheEntity.getDCache().get(enName);
		Hashtable ht = tempVar instanceof Hashtable ? (Hashtable)tempVar : null;
		if (ht == null)
		{
			return;
		}

		if (ht.containsKey(pkVal))
		{
			ht.put(pkVal, en);
		}
		else
		{
			ht.put(pkVal, en);
		}

		//清除集合.
		CacheEntity.getDCache().remove(enName + "Ens");

	}
}
