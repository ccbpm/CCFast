package bp.da;

import bp.difference.ContextHolderUtils;
import bp.difference.SystemConfig;

import java.util.Hashtable;

public class CacheFrmTemplate {

    ///缓存ht
    private static Hashtable _hts;
    private static Object lockObj = new Object();
    /**
     * 放入表单
     * <p>
     * param frmID 表单ID
     * param ds 表单模版
     *
     * @throws Exception
     */
    public static void Put(String frmID, DataSet ds) throws Exception {
        if (ds == null) return;
        String json = bp.tools.Json.ToJson(ds);
        synchronized (lockObj)
        {
            if (_hts == null)
                _hts = new Hashtable();
            _hts.put(frmID, json);

        }
    }

    /**
     * 移除
     * <p>
     * param frmID 表单ID
     */
    public static void Remove(String frmID) {

        synchronized (lockObj)
        {
            if (_hts == null)
                _hts = new Hashtable();
            _hts.remove(frmID);
        }
    }

    /**
     * 获得表单DataSet模式的模版数据
     * <p>
     * param frmID 表单ID
     *
     * @return 表单模版
     */
    public static DataSet GetFrmDataSetModel(String frmID) throws Exception {
//        if(SystemConfig.getRedisIsEnable()){
//            Object result = ContextHolderUtils.getRedisUtils().get(false, SystemConfig.getRedisCacheKey(frmID));
//            if (result != null) {
//                DataSet ds = bp.tools.Json.ToDataSet(result.toString());
//                return ds;
//            }
//            return null;
//        }
        synchronized (lockObj)
        {
            if (_hts == null)
                _hts = new Hashtable();

            if (_hts.containsKey(frmID) == true)
            {
                String json = _hts.get(frmID) instanceof String ? (String)_hts.get(frmID) : null;
                DataSet ds = bp.tools.Json.ToDataSet(json);
                return ds;
            }
            return null;
        }
    }
}
