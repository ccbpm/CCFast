package bp.da;

import java.io.File;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import bp.difference.ContextHolderUtils;
import bp.difference.SystemConfig;
import bp.en.*;
import bp.tools.ConvertTools;
import org.redisson.api.RedissonClient;
import org.redisson.api.RMapCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cache 的摘要说明。
 */
public class Cache  implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger Log = LoggerFactory.getLogger(Cache.class);

    public Cache() {
    }
    private static   RedissonClient redisson = ContextHolderUtils.getRedisson();
    //定义redis存储的key值
    private static String bsCacheKey = SystemConfig.getRedisCacheKey("BSCache");
    // Bill_Cache 单据模板Cache.
    private static String billCacheKey = SystemConfig.getRedisCacheKey("BillCache");
    private static String sqlCacheKey = SystemConfig.getRedisCacheKey("SQLCache");
    private static String ensDataCacheKey = SystemConfig.getRedisCacheKey("EnsDataCache");
    private static String ensCacheExtKey = SystemConfig.getRedisCacheKey("EnsDataCacheExt");
    private static String mapCacheKey = SystemConfig.getRedisCacheKey("MapCache");
    private static final String tsMapCacheKey = SystemConfig.getRedisCacheKey("MapCacheTS");


    //定义redis
    private static RMapCache<Object,Object> _BS_Cache_Redis;
    private static RMapCache<Object,Object> _Bill_Cache_Redis;
    private static RMapCache<Object,Object> _SQL_Cache_Redis;
    private static RMapCache<Object,Object> _EnsData_Cache_Redis;
    private static RMapCache<Object,Object> _EnsData_Cache_Ext_Redis;
    private static RMapCache<Object,Object> _Map_Cache_Redis;
    private static RMapCache _Map_CacheTS_Redis;


    //不启用redis的存储
    private static Hashtable<String, Object> _BS_Cache = new Hashtable<>();
    private static Hashtable<String, Object> _Bill_Cache = new Hashtable<>();
    private static Hashtable<String, Object> _SQL_Cache = new Hashtable<>();
    private static Hashtable<String, Object> _EnsData_Cache = new Hashtable<>();
    private static Hashtable<String, Object> _EnsData_Cache_Ext = new Hashtable<>();
    private static Hashtable<String, Object> _Map_Cache = new Hashtable<String, Object>();
    private static ConcurrentHashMap<String, Map> _Map_CacheTS = null;

    public static void ClearCache() {
        if (SystemConfig.getRedisIsEnable()) {
            getBS_Cache_Redis().clear();
            getBill_Cache_Redis().clear();
            getSQL_Cache_Redis().clear();
            getEnsData_Cache_Redis().clear();
            getEnsData_Cache_Ext_Redis().clear();
            getMap_Cache_Redis().clear();
            getMapCacheRedsTS().clear();
            return;
        }
        if (_BS_Cache != null) {
            _BS_Cache.clear();
        }
        if (_SQL_Cache != null) {
            _SQL_Cache.clear();
        }
        if (_EnsData_Cache != null) {
            _EnsData_Cache.clear();
        }
        if (_Map_Cache != null) {
            _Map_Cache.clear();
        }
        if (_EnsData_Cache_Ext != null) {
            _EnsData_Cache_Ext.clear();
        }
        if (_Bill_Cache != null) {
            _Bill_Cache.clear();
        }
//        if (_Map_CacheTS != null) {
//            _Map_CacheTS.clear();
//        }
    }
    public static void ClearCache(String enName) {
        if(SystemConfig.getRedisIsEnable()){
            getBS_Cache_Redis().remove(enName);
            getBill_Cache_Redis().remove(enName);
            getSQL_Cache_Redis().remove(enName);
            getEnsData_Cache_Redis().remove(enName);
            getEnsData_Cache_Ext_Redis().remove(enName);
            getMap_Cache_Redis().remove(enName);
            getMapCacheRedsTS().remove(enName);
            return;
        }
        if (_BS_Cache != null) {
            if (_BS_Cache.containsKey(enName) == true)
                _BS_Cache.remove(enName);
        }
        if (_SQL_Cache != null) {
            if (_SQL_Cache.containsKey(enName) == true)
                _SQL_Cache.remove(enName);
        }

        if (_EnsData_Cache != null) {
            if (_EnsData_Cache.containsKey(enName) == true)
                _EnsData_Cache.remove(enName);
        }
        if (_Map_Cache != null) {
            if (_Map_Cache.containsKey(enName) == true)
                _Map_Cache.remove(enName);
        }
        if (_EnsData_Cache_Ext != null) {
            if (_EnsData_Cache_Ext.containsKey(enName) == true)
                _EnsData_Cache_Ext.remove(enName);
        }
        if (_Bill_Cache != null) {
            if (_Bill_Cache.containsKey(enName) == true)
                _Bill_Cache.remove(enName);
        }
        if (_Map_CacheTS != null) {
            if (_Map_CacheTS.containsKey(enName) == true)
                _Map_CacheTS.remove(enName);
        }
    }

    // BS_Cache
    public static Hashtable<String, Object> getBS_Cache() {
        if (_BS_Cache == null) {
            _BS_Cache = new Hashtable();
        }
        return _BS_Cache;
    }
    public static RMapCache<Object,Object> getBS_Cache_Redis(){
        if (_BS_Cache_Redis == null) {
            _BS_Cache_Redis = redisson.getMapCache(bsCacheKey);
        }
        return _BS_Cache_Redis;
    }

    //单据的存储
    public static Hashtable<String, Object> getBill_Cache() {
        if (_Bill_Cache == null) {
            _Bill_Cache = new Hashtable();
        }
        return _Bill_Cache;
    }
    public static RMapCache<Object,Object> getBill_Cache_Redis() {
        if (_Bill_Cache_Redis == null) {
            _Bill_Cache_Redis = redisson.getMapCache(billCacheKey);
        }
        return _Bill_Cache_Redis;
    }
    public static Object GetBillCache(String clName){
        if(SystemConfig.getRedisIsEnable())
            return getBill_Cache_Redis().get(clName);
        else
            return getBill_Cache().get(clName);
    }
    public static void setBill_Cache(String clName, Object paras ) {
        if (clName == null)
            return;
        if (paras == null) {
            if(SystemConfig.getRedisIsEnable())
                getBill_Cache_Redis().remove(clName);
            else
                getBill_Cache().remove(clName);
            return;
        }
        if(SystemConfig.getRedisIsEnable())
            getBill_Cache_Redis().put(clName, paras);
        else
            getBill_Cache().put(clName, paras);
    }

    // SQL Cache
    public static Hashtable<String, Object> getSQL_Cache() {

        if (_SQL_Cache == null) {
            _SQL_Cache = new Hashtable<String, Object>();
        }
        return _SQL_Cache;
    }
    public static RMapCache getSQL_Cache_Redis() {
        if (_SQL_Cache_Redis == null) {
            _SQL_Cache_Redis = redisson.getMapCache(sqlCacheKey);
        }
        return _SQL_Cache_Redis;
    }
    public static Hashtable<String, Object> getEnsData_Cache() {
        if (_EnsData_Cache == null) {
            _EnsData_Cache = new Hashtable<String, Object>();
        }
        return _EnsData_Cache;
    }
    public static RMapCache getEnsData_Cache_Redis() {
        if (_EnsData_Cache_Redis == null) {
            _EnsData_Cache_Redis = redisson.getMapCache(ensDataCacheKey);
        }
        return _EnsData_Cache_Redis;
    }

    public static Hashtable<String, Object> getEnsData_Cache_Ext() {
        if (_EnsData_Cache_Ext == null) {
            _EnsData_Cache_Ext = new Hashtable<String, Object>();
        }
        return _EnsData_Cache_Ext;
    }
    public static RMapCache getEnsData_Cache_Ext_Redis() {
        if (_EnsData_Cache_Ext_Redis == null) {
            _EnsData_Cache_Ext_Redis = redisson.getMapCache(ensCacheExtKey);
        }
        return _EnsData_Cache_Ext_Redis;
    }
    public static Hashtable<String, Object> getMap_Cache() {
        if (_Map_Cache == null) {
            _Map_Cache = new Hashtable<String, Object>();
        }
        return _Map_Cache;
    }
    public static RMapCache getMap_Cache_Redis() {
        if (_Map_Cache_Redis == null) {
            _Map_Cache_Redis = redisson.getMapCache(mapCacheKey);
        }
        return _Map_Cache_Redis;
    }
    public static ConcurrentHashMap<String, Map> getMapCacheTS() {
        if (_Map_CacheTS == null)
            _Map_CacheTS = new ConcurrentHashMap<>();
        return _Map_CacheTS;
    }

    public static RMapCache getMapCacheRedsTS() {
        if (SystemConfig.getRedisIsEnable())
            _Map_CacheTS_Redis =redisson.getMapCache(tsMapCacheKey);
        return _Map_CacheTS_Redis;
    }
    public static SQLCache GetSQL(String clName) {
        if(SystemConfig.getRedisIsEnable()){
            SQLCache tempVar = (SQLCache)getSQL_Cache_Redis().get(clName);
            return (SQLCache) ((tempVar instanceof SQLCache) ? tempVar : null);
        }
        SQLCache tempVar = (SQLCache) getSQL_Cache().get(clName);
        return (SQLCache) ((tempVar instanceof SQLCache) ? tempVar : null);
    }

    public static void SetSQL(String clName, SQLCache csh) {
        if (clName == null || csh == null) {
            throw new RuntimeException("clName.  csh 参数有一个为空。");
        }
       if(SystemConfig.getRedisIsEnable())
            getSQL_Cache_Redis().put(clName, csh);
        else
            getSQL_Cache().put(clName, csh);
    }

    public static Entities GetEnsData(String clName) {
        Entities tempVar = (Entities) getEnsData_Cache().get(clName);
        Entities ens = (Entities) ((tempVar instanceof Entities) ? tempVar : null);
        if (ens == null)
            return null;
        if (ens.size() == 0)
            return null;
        return ens;
    }

    public static void EnsDataSet(String clName, Entities obj) {
        if (obj.size() == 0) {
        }
        getEnsData_Cache().put(clName, obj);
        if (SystemConfig.getRedisIsEnable())
            getEnsData_Cache_Redis().put(clName, obj);
    }

    public static void remove(String clName) {
        getEnsData_Cache().remove(clName);
        if (SystemConfig.getRedisIsEnable())
            getEnsData_Cache_Redis().remove(clName);
    }

    // EnsData Cache 扩展 临时的Cache 文件。



    /**
     * 为部分数据做的缓冲处理
     * <p>
     * param clName
     *
     * @return
     */
    public static Entities GetEnsDataExt(String clName) {
        // 判断是否失效了。
        if (SystemConfig.isTempCacheFail()) {
            getEnsData_Cache_Ext().clear();
            if (SystemConfig.getRedisIsEnable())
                getEnsData_Cache_Redis().clear();
            return null;
        }
        try {
            if (SystemConfig.getRedisIsEnable()){
                Entities ens;
                Entities tempVar = (Entities) getEnsData_Cache_Redis().get(clName);
                ens = (Entities) ((tempVar instanceof Entities) ? tempVar : null);
                return ens;
            }
            Entities ens;
            Entities tempVar = (Entities) getEnsData_Cache_Ext().get(clName);
            ens = (Entities) ((tempVar instanceof Entities) ? tempVar : null);
            return ens;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 为部分数据做的缓冲处理
     * <p>
     * param clName
     * param obj
     */
    public static void SetEnsDataExt(String clName, Entities obj) {
        if (clName == null || obj == null) {
            throw new RuntimeException("clName.  obj 参数有一个为空。");
        }
        if(SystemConfig.getRedisIsEnable())
            getEnsData_Cache_Ext_Redis().put(clName, obj);
        else
            getEnsData_Cache_Ext().put(clName, obj);

    }

    public static Map GetMap(String clName) {
        try {
            if(SystemConfig.getRedisIsEnable()){
                Map tempVar = (Map) getMap_Cache_Redis().get(clName);
                return (Map) ((tempVar instanceof Map) ? tempVar : null);
            }
            Map tempVar = (Map) getMap_Cache().get(clName);
            return (Map) ((tempVar instanceof Map) ? tempVar : null);
        } catch (Exception e) {
            return null;
        }
    }

    public static void SetMap(String clName, Map map) {
        if (clName == null)
            return;
        if (map == null) {
            if(SystemConfig.getRedisIsEnable())
                getMap_Cache_Redis().remove(clName);
            else
                getMap_Cache().remove(clName);
            return;
        }
        if(SystemConfig.getRedisIsEnable())
            getMap_Cache_Redis().put(clName, map);
        else
            getMap_Cache().put(clName, map);
    }

    public static Map GetMapTS(String clName) {
        try {
            if(SystemConfig.getRedisIsEnable()){
                Map tempVar = (Map) getMapCacheRedsTS().get(clName);
                return (Map) ((tempVar instanceof Map) ? tempVar : null);
            }
            Map tempVar = (Map) getMapCacheTS().get(clName);
            return (Map) ((tempVar instanceof Map) ? tempVar : null);
        } catch (Exception e) {
            return null;
        }
    }

    public static void SetMapTS(String clName, Map map) {
        if (clName == null)
            return;
        if (map == null) {
            if(SystemConfig.getRedisIsEnable())
                getMapCacheRedsTS().remove(clName);
            else
                getMapCacheTS().remove(clName);
            return;
        }
        if(SystemConfig.getRedisIsEnable())
            getMapCacheRedsTS().put(clName, map);
        else
            getMapCacheTS().put(clName, map);
    }

    /**
     * 是否存map.
     *
     * @param clName
     * @return
     */
    public static boolean IsExitMapTS(String clName) {
        if (clName == null) {
            throw new RuntimeException("clName.不能为空。");
        }
        if(SystemConfig.getRedisIsEnable())
            return getMapCacheRedsTS().containsKey(clName);
        return getMapCacheTS().containsKey(clName);
    }

    // 取出对象

    /**
     * 从 Cache 里面取出对象.
     */
    public static Object GetObj(String key, Depositary where) {
        if (where == Depositary.None) {
            throw new RuntimeException("您没有把(" + key + ")放到session or application 里面不能找出他们.");
        }
        if (where == Depositary.Application) {
            if(SystemConfig.getRedisIsEnable())
                return getBS_Cache_Redis().get(key);
            return getBS_Cache().get(key);
        } else {
            return ContextHolderUtils.getSession().getAttribute(key);
        }

    }

    public static Object GetObjFormApplication(String key, Object isNullAsVal) {
        Object obj;
        if(SystemConfig.getRedisIsEnable())
            obj = getBS_Cache_Redis().get(key);
        else
            obj = getBS_Cache().get(key);
        if (obj == null) {
            return isNullAsVal;
        } else {
            return obj;
        }
    }

    /**
     * RemoveObj
     * <p>
     * param key
     * param where
     */
    public static void RemoveObj(String key, Depositary where) {
        if (!Cache.IsExits(key, where)) {
            return;
        }
        if (where == Depositary.Application) {

        } else {

            ContextHolderUtils.getSession().removeAttribute(key);
        }

    }

    // 放入对象
    public static void RemoveObj(String key) {
        if(SystemConfig.getRedisIsEnable())
            getBS_Cache_Redis().remove(key);
        else
            getBS_Cache().remove(key);
    }
    public static void AddObj(String key, Depositary where, Object obj) {
        if (key == null) {
            throw new RuntimeException("您需要为obj=" + obj.toString() + ",设置为主键值。key");
        }

        if (obj == null) {
            throw new RuntimeException("您需要为obj=null  设置为主键值。key=" + key);
        }

        if (where == Depositary.None) {
            throw new RuntimeException("您没有把(" + key + ")放到 session or application 里面设置他们.");
        }

        if (where == Depositary.Application) {
            if(SystemConfig.getRedisIsEnable())
                getBS_Cache_Redis().put(key, obj);
            else
                getBS_Cache().put(key, obj);
        }
    }

    // 判断对象是不是存在

    /**
     * 判断对象是不是存在
     */
    public static boolean IsExits(String key, Depositary where) {
        if (where == Depositary.Application) {
            return true;
        } else {
            return true;
        }

    }

    /**
     * 读取单据RTF模板文件
     * @param cfile 模板文件全路径
     * @param isCheckCache 是否启用缓存
     * @return  RTF内容字符
     * @throws Exception
     */
    public static String GetBillStr(String cfile, boolean isCheckCache) throws Exception {
        String val = "";
         Object obj = GetBillCache(cfile);
         if(obj == null) val = "";
         else val = obj.toString();
        if (isCheckCache == true) {
            val = null;
        }

        if (DataType.IsNullOrEmpty(val)) {
            String file = null;
            if (cfile.contains(":")) {
                file = cfile;
            } else {
                file = SystemConfig.getPathOfDataUser() + "CyclostyleFile/" + cfile;
            }
            try {
                val = ConvertTools.StreamReaderToStringConvert(file, "us-ascii");
            } catch (Exception ex) {
                throw new RuntimeException("@读取单据模板时出现错误。cfile=" + cfile + " @Ex=" + ex.getMessage());
            }
            setBill_Cache(cfile, val);
        }
        return val.substring(0);
    }

    public static String[] GetBillParas(String cfile, String ensStrs, Entities ens) throws Exception {
        Object obj = GetBillCache(cfile + "Para");
        if(obj!=null){
            return (String[])obj;
        }
      /*  String[] paras = (String[]) ((getBill_Cache().get(cfile + "Para") instanceof String[])
                ? getBill_Cache().get(cfile + "Para") : null);
        if (paras != null) {
            return paras;
        }*/
        Attrs attrs = new Attrs();
        for (Entity en : Entities.convertEntities(ens)) {
            String perKey = en.toString();

            Attrs enAttrs = en.getEnMap().getAttrs();
            for (Attr attr : enAttrs.convertAttrs(enAttrs)) {
                Attr attrN = new Attr();
                attrN.setKey(perKey + "." + attr.getKey());
                if (attr.getItIsRefAttr()) {
                    attrN.setField(perKey + "." + attr.getKey() + "Text");
                }
                attrN.setMyDataType(attr.getMyDataType());
                attrN.setMyFieldType(attr.getMyFieldType());
                attrN.setUIBindKey(attr.getUIBindKey());
                attrN.setField(attr.getField());
                attrs.Add(attrN);
            }
        }

        String [] paras = Cache.GetBillParas_Gener(cfile, attrs);
        setBill_Cache(cfile + "Para", paras);
        return paras;
    }

    public static String[] GetBillParas(String cfile, String ensStrs, Entity en) throws Exception {
        Object obj = GetBillCache(cfile + "Para");
        if(obj!=null){
            return (String[])obj;
        }
        /*String[] paras = (String[]) ((getBill_Cache().get(cfile + "Para") instanceof String[])
                ? getBill_Cache().get(cfile + "Para") : null);
        if (paras != null) {
            return paras;
        }*/
        String[] paras = Cache.GetBillParas_Gener(cfile, en.getEnMap().getAttrs());
        setBill_Cache(cfile + "Para", paras);
        return paras;
    }
    //获取rtf打印中替换的字段
    public static String[] GetBillParas_Gener(String cfile, Attrs attrs) throws Exception {
        String[] paras = new String[300];
        String Billstr = Cache.GetBillStr(cfile, true);
        int i = 0;
        boolean haveError = false;
        String msg = "";
        String []Billstrs=Billstr.split("<");
        for(String str:Billstrs){
            int index=str.indexOf(">");
            if(index!=-1){
                String para=str.substring(0,index);
                // 首先解决空格的问题.
                String real = para.toString();
                if (attrs != null && real.contains(" ")) {
                    real = real.replace(" ", "");
                    Billstr = Billstr.replace(para, real);
                    para = real;
                    haveError = true;
                }
                // 解决特殊符号
                if (attrs != null && real.contains("\\") && real.contains("ND") == false) {
                    haveError = true;
                    String findKey = null;
                    int keyLen = 0;
                    for (Attr attr : attrs) {
                        if (real.contains(attr.getKey())) {
                            if (keyLen <= attr.getKey().length()) {
                                keyLen = attr.getKey().length();
                                findKey = attr.getKey();
                            }
                        }
                    }
                    if (findKey == null) {
                        msg += "@参数:<font color=red><b>(" + real + ")</b></font>可能拼写错误。";
                        continue;
                    }
                    if (real.contains(findKey + ".NYR") == true) {
                        real = findKey + ".NYR";
                    } else if (real.contains(findKey + ".RMB") == true) {
                        real = findKey + ".RMB";
                    } else if (real.contains(findKey + ".RMBDX") == true) {
                        real = findKey + ".RMBDX";
                    } else {
                        real = findKey;
                    }
                    Billstr = Billstr.replace(para, real);
                }
                paras[i] = para;
                i++;
            }
        }
        if (haveError) {
            String myfile = SystemConfig.getPathOfDataUser() + "/CyclostyleFile/" + cfile;
            if (!new File(myfile).exists()) {
                myfile = cfile;
            }
            try {
                ConvertTools.StreamWriteConvert(Billstr, myfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return paras;
    }
}
