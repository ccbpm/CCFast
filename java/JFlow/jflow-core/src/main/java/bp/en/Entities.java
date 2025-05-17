package bp.en;

import bp.da.*;
import bp.difference.SystemConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.math.*;

/**
 * 数据实体集合
 */
public abstract class Entities extends ArrayList<Entity> implements Serializable {
    private static final long serialVersionUID = 1L;

    ///查询方法.

    /**
     * 查询全部
     *
     * @return throws Exception
     */
    public int RetrieveAllFromDBSource() throws Exception {
        QueryObject qo = new QueryObject(this);
        return qo.DoQuery();
    }

    public int RetrieveAllFromDBSource(String orderByAttr) throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.addOrderBy(orderByAttr);
        return qo.DoQuery();
    }


    ///过滤
    public final Entity Filter(String key, String val) throws Exception {
        for (Entity en : convertEntities(this)) {
            if (en.GetValStringByKey(key).equals(val)) {
                return en;
            }
        }
        return null;
    }

    public final Entity Filter(String key1, String val1, String key2, String val2) throws Exception {
        for (Entity en : convertEntities(this)) {
            if (en.GetValStringByKey(key1).equals(val1) && en.GetValStringByKey(key2).equals(val2)) {
                return en;
            }
        }
        return null;
    }

    public final Entity Filter(String key1, String val1, String key2, String val2, String key3, String val3) throws Exception {
        for (Entity en : convertEntities(this)) {
            if (en.GetValStringByKey(key1).equals(val1) && en.GetValStringByKey(key2).equals(val2) && en.GetValStringByKey(key3).equals(val3)) {
                return en;
            }
        }
        return null;
    }


    /**
     * 按照属性查询
     * <p>
     * param attr 属性名称
     * param val 值
     *
     * @return 是否查询到
     * @throws Exception
     */
    public final int RetrieveByAttr(String attr, Object val) throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.AddWhere(attr, val);
        return qo.DoQuery();
    }

    public final int RetrieveLikeAttr(String attr, String val) throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.AddWhere(attr, " like ", val);
        return qo.DoQuery();
    }

    ///扩展属性
    /**
     * 是不是分级的字典。
     */
    public final boolean getIsGradeEntities() {
        try {
            Attr attr = null;
            attr = this.getNewEntity().getEnMap().GetAttrByKey("Grade");
            attr = this.getNewEntity().getEnMap().GetAttrByKey("IsDtl");
            return true;
        } catch (java.lang.Exception e) {
            return false;
        }
    }


    /**
     * 写入到xml.
     * <p>
     * param file
     *
     * @return
     */
    public int ExpDataToXml(String file) throws Exception {
        DataTable dt = this.ToDataTableField();
        DataSet ds = new DataSet();
        ds.Tables.add(dt);
        ds.WriteXml(file, XmlWriteMode.WriteSchema, ds);
        return dt.Rows.size();
    }

    /**
     * 通过datatable 转换为实体集合这个Table其中一个列名称是主键
     * <p>
     * param dt Table
     * param fieldName 字段名称，这个字段时包含在table 中的主键
     *
     * @throws Exception
     */
    public final void InitCollectionByTable(DataTable dt, String fieldName) throws Exception {
        Entity en = this.getNewEntity();
        String pk = en.getPK();
        for (DataRow dr : dt.Rows) {
            Entity en1 = this.getNewEntity();
            en1.SetValByKey(pk, dr.getValue(fieldName));
            en1.Retrieve();
            this.AddEntity(en1);
        }
    }

    /**
     * 通过datatable 转换为实体集合.
     * 这个Table 的结构需要与属性结构相同。
     * <p>
     * param dt 转换为Table
     */
    public final void InitCollectionByTable(DataTable dt) throws Exception {
        try {
            for (DataRow dr : dt.Rows) {
                Entity en = this.getNewEntity();
                for (Attr attr : en.getEnMap().getAttrs()) {
                    if (attr.getMyFieldType() == FieldType.RefText) {
                        try {
                            en.getRow().SetValByKey(attr.getKey(), dr.getValue(attr.getKey()));
                        } catch (java.lang.Exception e) {
                        }
                    } else {
                        en.getRow().SetValByKey(attr.getKey(), dr.getValue(attr.getKey()));
                    }
                }
                this.AddEntity(en);
            }
        } catch (RuntimeException ex) {
            throw new RuntimeException("@此表不能向集合转换详细的错误:" + ex.getMessage());
        }
    }

    /**
     * 判断两个实体集合是不是相同.
     * <p>
     * param ens
     *
     * @return
     */
    public final boolean equals(Entities ens) throws Exception {
        if (ens.size() != this.size()) {
            return false;
        }

        for (Entity en : convertEntities(this)) {
            boolean IsExits = false;
            for (Entity en1 : convertEntities(ens)) {
                if (en.getPKVal().equals(en1.getPKVal())) {
                    IsExits = true;
                    break;
                }
            }
            if (IsExits == false) {
                return false;
            }
        }
        return true;
    }


    /**
     * 构造函数
     */
    public Entities() {
    }

    ///


    ///公共方法

    /**
     * 是否存在key= val 的实体。
     * <p>
     * param key
     * param val
     *
     * @return
     */
    public final boolean getIsExits(String key, Object val) throws Exception {
        for (Entity en : convertEntities(this)) {
            if (val.toString().equals(en.GetValStringByKey(key))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建一个该集合的元素的类型的新实例
     *
     * @return
     * @throws Exception
     */
    public abstract Entity getNewEntity() ;

    /**
     * 将对象添加到集合尾处，如果对象已经存在，则不添加
     * <p>
     * param entity 要添加的对象
     *
     * @return 返回添加到的地方
     * @throws Exception
     */
    public int AddEntity(Entity entity) {
        this.add(entity);
        return this.size();
    }

    public int AddEntity(Entity entity, int idx) {
        this.add(idx, entity);
        return idx;
    }

    public void AddEntities(Entities ens) {
        for (Entity en : convertEntities(ens)) {
            this.AddEntity(en);
        }
    }

    /**
     * 增加entities
     * <p>
     * param pks 主键的值，中间用@符号隔开
     *
     * @throws Exception
     */
    public void AddEntities(String pks) throws Exception {
        this.clear();
        if (pks == null || pks.equals("")) {
            return;
        }

        String[] strs = pks.split("[@]", -1);
        for (String str : strs) {
            if (str == null || str.equals("")) {
                continue;
            }

            Entity en = this.getNewEntity();
            en.setPKVal(str);
            if (en.RetrieveFromDBSources() == 0) {
                continue;
            }
            this.AddEntity(en);
        }
    }

    public void Insert(int index, Entity entity) {
        this.add(index, entity);
    }

    /**
     * 判断是不是包含指定的Entity .
     * <p>
     * param en
     *
     * @return
     */
    public final boolean contains(Entity en) throws Exception {
        return this.contains(en.getPKVal());
    }

    /**
     * 是否包含这个集合
     * <p>
     * param ens
     *
     * @return true / false
     */
    public final boolean contains(Entities ens) throws Exception {
        return this.contains(ens, ens.getNewEntity().getPK());
    }

    public final boolean contains(Entities ens, String key) throws Exception {
        if (ens.size() == 0) {
            return false;
        }
        for (Entity en : convertEntities(ens)) {
            if (this.contains(key, en.GetValByKey(key)) == false) {
                return false;
            }
        }
        return true;
    }

    public final boolean contains(Entities ens, String key1, String key2) throws Exception {
        if (ens.size() == 0) {
            return false;
        }
        for (Entity en : convertEntities(ens)) {
            if (this.contains(key1, en.GetValByKey(key1), key2, en.GetValByKey(key2)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是不是包含指定的PK
     * <p>
     * param pkVal
     *
     * @return
     */
    public final boolean contains(Object pkVal) {
        String pk = null;
        try {
            pk = this.getNewEntity().getPK();
            return this.contains(pk, pkVal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 指定的属性里面是否包含指定的值.
     * <p>
     * param attr 指定的属性
     * param pkVal 指定的值
     *
     * @return 返回是否等于
     */
    public final boolean contains(String attr, Object pkVal) throws Exception {
        for (Entity myen : convertEntities(this)) {
            if (myen.GetValByKey(attr).toString().equals(pkVal.toString())) {
                return true;
            }
        }
        return false;
    }

    public final boolean contains(String attr1, Object pkVal1, String attr2, Object pkVal2) throws Exception {
        for (Entity myen : convertEntities(this)) {
            if (myen.GetValByKey(attr1).toString().equals(pkVal1.toString()) && myen.GetValByKey(attr2).toString().equals(pkVal2.toString())) {
                return true;
            }
        }
        return false;
    }

    public final boolean contains(String attr1, Object pkVal1, String attr2, Object pkVal2, String attr3, Object pkVal3) throws Exception {
        for (Entity myen : convertEntities(this)) {
            if (myen.GetValByKey(attr1).toString().equals(pkVal1.toString()) && myen.GetValByKey(attr2).toString().equals(pkVal2.toString()) && myen.GetValByKey(attr3).toString().equals(pkVal3.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 取得当前集合于传过来的集合交集.
     * <p>
     * param ens 一个实体集合
     *
     * @return 比较后的集合
     */
    public final Entities GainIntersection(Entities ens) throws Exception {
        Entities myens = this.CreateInstance();
        String pk = this.getNewEntity().getPK();
        for (Entity en : convertEntities(this)) {
            for (Entity hisen : convertEntities(ens)) {
                if (en.GetValByKey(pk).equals(hisen.GetValByKey(pk))) {
                    myens.AddEntity(en);
                }
            }
        }
        return myens;
    }

    /**
     * 创建立本身的一个实例.
     *
     * @return Entities
     */
    public final Entities CreateInstance() throws Exception {
        return ClassFactory.GetEns(this.toString());
    }

    ///


    /**
     * 获取一个实体
     * <p>
     * param val 值
     *
     * @return
     */
    public final Entity GetEntityByKey(Object val) throws Exception {
        String pk = this.getNewEntity().getPK();
        for (Entity en : convertEntities(this)) {
            if (val.toString().equals(en.GetValStrByKey(pk))) {
                return en;
            }
        }
        return null;
    }

    /**
     * 获取一个实体
     * <p>
     * param attr 属性
     * param val 值
     *
     * @return
     */
    public final Entity GetEntityByKey(String attr, Object val) throws Exception {
        for (Entity en : convertEntities(this)) {
            if (en.GetValByKey(attr).equals(val)) {
                return en;
            }
        }
        return null;
    }

    public final Entity GetEntityByKey(String attr, int val) throws Exception {
        for (Entity en : convertEntities(this)) {
            if (en.GetValIntByKey(attr) == val) {
                return en;
            }
        }
        return null;
    }

    public final Entity GetEntityByKey(String attr1, Object val1, String attr2, Object val2) throws Exception {
        for (Entity en : convertEntities(this)) {
            if (val1.toString().equals(en.GetValStrByKey(attr1)) && val2.toString().equals(en.GetValStrByKey(attr2))) {
                return en;
            }
        }
        return null;
    }

    public final Entity GetEntityByKey(String attr1, Object val1, String attr2, Object val2, String attr3, Object val3) throws Exception {
        for (Entity en : convertEntities(this)) {
            if (en.GetValByKey(attr1).equals(val1) && en.GetValByKey(attr2).equals(val2) && en.GetValByKey(attr3).equals(val3)) {
                return en;
            }
        }
        return null;
    }

    public Entities GetEntitiesByKey(String attr, String val) throws Exception {
        Entities ens = this.getNewEntity().GetNewEntities();
        ens.clear();
        for (Entity en : convertEntities(this)) {
            if (en.GetValStrByKey(attr).equals(val) == false)
                continue;
            ens.AddEntity(en);
        }
        if (ens.size() == 0)
            return null;
        return ens;
    }

    public Entities GetEntitiesByKey(String attr, Object val, String attr2, Object val2) throws Exception {
        Entities ens = this.getNewEntity().GetNewEntities();
        ens.clear();
        for(Entity en : convertEntities(this))
        {
            if (en.GetValStrByKey(attr).equals(val.toString())
                    && en.GetValStrByKey(attr2).equals(val2.toString()))
                ens.AddEntity(en);
        }
        if (ens.size() == 0)
            return null;
        return ens;
    }

    /**
     * 求和
     * <p>
     * param key
     *
     * @return
     */
    public final BigDecimal GetSumDecimalByKey(String key) throws Exception {
        BigDecimal sum = new BigDecimal(0);
        for (Entity en : convertEntities(this)) {
            sum = sum.add(en.GetValDecimalByKey(key, 4));
        }
        return sum;
    }

    public final BigDecimal GetSumDecimalByKey(String key, String attrOfGroup, Object valOfGroup) throws Exception {
        BigDecimal sum = new BigDecimal(0);
        for (Entity en : convertEntities(this)) {
            if (valOfGroup.toString().equals(en.GetValStrByKey(attrOfGroup))) {
                sum = sum.add(en.GetValDecimalByKey(key, 4));
            }
        }
        return sum;
    }

    public final BigDecimal GetAvgDecimalByKey(String key) throws Exception {
        if (this.size() == 0) {
            return new BigDecimal(0);
        }
        BigDecimal sum = new BigDecimal(0);
        for (Entity en : convertEntities(this)) {
            sum = sum.add(en.GetValDecimalByKey(key, 4));
        }
        return sum.divide(new BigDecimal(this.size()));
    }

    public final BigDecimal GetAvgIntByKey(String key) throws Exception {
        if (this.size() == 0) {
            return new BigDecimal(0);
        }
        BigDecimal sum = new BigDecimal(0);
        for (Entity en : convertEntities(this)) {
            sum = sum.add(en.GetValDecimalByKey(key, 4));
        }
        return sum.divide(new BigDecimal(this.size()));
    }

    /**
     * 求和
     * <p>
     * param key
     *
     * @return
     */
    public final int GetSumIntByKey(String key) throws Exception {
        int sum = 0;
        for (Entity en : convertEntities(this)) {
            sum += en.GetValIntByKey(key);
        }
        return sum;
    }

    /**
     * 求和
     * <p>
     * param key
     *
     * @return
     */
    public final float GetSumFloatByKey(String key) throws Exception {
        float sum = 0;
        for (Entity en : convertEntities(this)) {
            sum += en.GetValFloatByKey(key);
        }
        return sum;
    }

    /**
     * 求个数
     * <p>
     * param key
     *
     * @return
     */
    public final int GetCountByKey(String key, String val) throws Exception {
        int sum = 0;
        for (Entity en : convertEntities(this)) {
            if (en.GetValStringByKey(key).equals(val)) {
                sum++;
            }
        }
        return sum;
    }

    public final int GetCountByKey(String key, int val) throws Exception {
        int sum = 0;
        for (Entity en : convertEntities(this)) {
            if (en.GetValIntByKey(key) == val) {
                sum++;
            }
        }
        return sum;
    }

    ///


    ///对集合的操作

    /**
     * 装载到内存
     *
     * @return
     * @throws Exception
     */
    public final int FlodInCache() throws Exception {
        //this.clear();
        QueryObject qo = new QueryObject(this);

        // qo.Top = 2000;
        int num = qo.DoQuery();

        /* 把查询个数加入内存 */
        Entity en = this.getNewEntity();
        CacheEntity.PubEns(en.toString(), this, en.getPK());
        bp.da.Log.DefaultLogWriteLineInfo("成功[" + en.toString() + "-" + num + "]放入缓存。");
        return num;
    }

    /**
     * 执行一次数据检查
     *
     * @throws Exception
     */
    public final String DoDBCheck(DBCheckLevel level) throws Exception {
        return bp.pub.PubClass.DBRpt1(level, this);
    }

    /**
     * 从集合中删除该对象
     * <p>
     * param entity
     */
    public void RemoveEn(Entity entity) {
        this.remove(entity);
    }

    /**
     * 移除
     * <p>
     * param pk
     *
     * @throws Exception
     */
    public void RemoveEn(String pk) throws Exception {
        String key = this.getNewEntity().getPK();
        RemoveEn(key, pk);
    }

    public void RemoveEn(String key, String val) throws Exception {
        for (Entity en : convertEntities(this)) {
            if (en.GetValStringByKey(key).equals(val)) {
                this.RemoveEn(en);
                return;
            }
        }
    }

    public void Remove(String pks) throws Exception {
        //设置
        pks = pks.replace(",", "@");

        String[] mypks = pks.split("[@]", -1);
        String pkAttr = this.getNewEntity().getPK();

        for (String pk : mypks) {
            if (pk == null || pk.length() == 0) {
                continue;
            }

            this.RemoveEn(pkAttr, pk);
        }
    }

    /**
     * 删除table.
     *
     * @return
     */
    public final int ClearTable() throws Exception {
        Entity en = this.getNewEntity();
        return en.RunSQL("DELETE FROM " + en.getEnMap().getPhysicsTable());
    }

    /**
     * 删除集合内的对象
     *
     * @throws Exception
     */
    public final void Delete() throws Exception {
        for (Entity en : convertEntities(this)) {
            if (en.getIsExits()) {
                en.Delete();
            }
        }
        this.clear();
    }

    public final int RunSQL(String sql) throws Exception {
        return this.getNewEntity().RunSQL(sql);
    }

    public int Delete(String key, Object val) throws Exception {
        Entity en = this.getNewEntity();
        Paras ps = new Paras();
        ps.SQL = "DELETE FROM " + en.getEnMap().getPhysicsTable() + " WHERE " + key + "=" + en.getHisDBVarStr() + "p";

        if (val.getClass() != String.class) {
            Attr attr = en.getEnMap().GetAttrByKey(key);
            if (attr.getMyDataType() == DataType.AppString) {
                ps.Add("p", val.toString());
            } else {
                ps.Add("p", val);
            }
        } else {
            Attr attr = en.getEnMap().GetAttrByKey(key);
            if (attr.getMyDataType() == DataType.AppInt) {
                ps.Add("p", Integer.parseInt(val.toString()));
            } else {
                ps.Add("p", val);
            }
        }
        //  ps.Add("p", val);
        return en.RunSQL(ps);
    }

    public final int Delete(String key1, Object val1, String key2, Object val2) throws Exception {
        Entity en = this.getNewEntity();
        Paras ps = new Paras();
        ps.SQL = "DELETE FROM " + en.getEnMap().getPhysicsTable() + " WHERE " + key1 + "=" + en.getHisDBVarStr() + "p1 AND " + key2 + "=" + en.getHisDBVarStr() + "p2";
        if (val1.getClass() != String.class) {
            Attr attr = en.getEnMap().GetAttrByKey(key1);
            if (attr.getMyDataType() == DataType.AppString) {
                ps.Add("p1", val1.toString());
            } else {
                ps.Add("p1", val1);
            }
        } else {
            ps.Add("p1", val1);
        }

        if (val2.getClass() != String.class) {
            Attr attr = en.getEnMap().GetAttrByKey(key2);
            if (attr.getMyDataType() == DataType.AppString) {
                ps.Add("p2", val2.toString());
            } else {
                ps.Add("p2", val2);
            }
        } else {
            ps.Add("p2", val2);
        }


        return en.RunSQL(ps);
    }

    public final int Delete(String key1, Object val1, String key2, Object val2, String key3, Object val3) throws Exception {
        Entity en = this.getNewEntity();
        Paras ps = new Paras();
        ps.SQL = "DELETE FROM " + en.getEnMap().getPhysicsTable() + " WHERE " + key1 + "=" + en.getHisDBVarStr() + "p1 AND " + key2 + "=" + en.getHisDBVarStr() + "p2 AND " + key3 + "=" + en.getHisDBVarStr() + "p3";
        if (val1.getClass() != String.class) {
            Attr attr = en.getEnMap().GetAttrByKey(key1);
            if (attr.getMyDataType() == DataType.AppString) {
                ps.Add("p1", val1.toString());
            } else {
                ps.Add("p1", val1);
            }
        } else {
            ps.Add("p1", val1);
        }

        if (val2.getClass() != String.class) {
            Attr attr = en.getEnMap().GetAttrByKey(key2);
            if (attr.getMyDataType() == DataType.AppString) {
                ps.Add("p2", val2.toString());
            } else {
                ps.Add("p2", val2);
            }
        } else {
            ps.Add("p2", val2);
        }

        if (val3.getClass() != String.class) {
            Attr attr = en.getEnMap().GetAttrByKey(key3);
            if (attr.getMyDataType() == DataType.AppString) {
                ps.Add("p3", val3.toString());
            } else {
                ps.Add("p3", val3);
            }
        } else {
            ps.Add("p3", val3);
        }


        return en.RunSQL(ps);
    }

    public final int Delete(String key1, Object val1, String key2, Object val2, String key3, Object val3, String key4, Object val4) throws Exception {
        Entity en = this.getNewEntity();
        Paras ps = new Paras();
        ps.SQL = "DELETE FROM " + en.getEnMap().getPhysicsTable() + " WHERE " + key1 + "=" + en.getHisDBVarStr() + "p1 AND " + key2 + "=" + en.getHisDBVarStr() + "p2 AND " + key3 + "=" + en.getHisDBVarStr() + "p3 AND " + key4 + "=" + en.getHisDBVarStr() + "p4";
        if (val1.getClass() != String.class) {
            Attr attr = en.getEnMap().GetAttrByKey(key1);
            if (attr.getMyDataType() == DataType.AppString) {
                ps.Add("p1", val1.toString());
            } else {
                ps.Add("p1", val1);
            }
        } else {
            ps.Add("p1", val1);
        }

        if (val2.getClass() != String.class) {
            Attr attr = en.getEnMap().GetAttrByKey(key2);
            if (attr.getMyDataType() == DataType.AppString) {
                ps.Add("p2", val2.toString());
            } else {
                ps.Add("p2", val2);
            }
        } else {
            ps.Add("p2", val2);
        }

        if (val3.getClass() != String.class) {
            Attr attr = en.getEnMap().GetAttrByKey(key3);
            if (attr.getMyDataType() == DataType.AppString) {
                ps.Add("p3", val3.toString());
            } else {
                ps.Add("p3", val3);
            }
        } else {
            ps.Add("p3", val3);
        }

        if (val4.getClass() != String.class) {
            Attr attr = en.getEnMap().GetAttrByKey(key4);
            if (attr.getMyDataType() == DataType.AppString) {
                ps.Add("p4", val4.toString());
            } else {
                ps.Add("p4", val4);
            }
        } else {
            ps.Add("p4", val4);
        }
        return en.RunSQL(ps);
    }

    /**
     * 更新集合内的对象
     *
     * @throws Exception
     */
    public final void Update() throws Exception {
        //String msg="";
        for (Entity en : convertEntities(this)) {
            en.Update();
        }

    }

    /**
     * 保存
     *
     * @throws Exception
     */
    public final void Save() throws Exception {
        for (Entity en : convertEntities(this)) {
            en.Save();
        }
    }

    public final void SaveToXml(String file) throws Exception {
        String dir = "";

        if (file.contains("\\")) {
            dir = file.substring(0, file.lastIndexOf('\\'));
        } else if (file.contains("/")) {
            dir = file.substring(0, file.lastIndexOf("/"));
        }

        if (!dir.equals("")) {
            if ((new File(dir)).isDirectory() == false) {
                (new File(dir)).mkdirs();
            }
        }

        DataTable dt = this.ToDataTableField();
        DataSet ds = new DataSet();
        ds.Tables.add(dt); //  this.ToDataSet();
        //ds.WriteXml(file);
        ds.WriteXml(file, XmlWriteMode.WriteSchema, ds);
    }

    ///


    ///查询方法
    public int RetrieveByKeyNoConnection(String attr, Object val) throws Exception {
        Entity en = this.getNewEntity();
        String pk = en.getPK();

        DataTable dt = DBAccess.RunSQLReturnTable("SELECT " + pk + " FROM " + this.getNewEntity().getEnMap().getPhysicsTable() + " WHERE " + attr + "=" + en.getHisDBVarStr() + "v", "v", val);
        for (DataRow dr : dt.Rows) {
            Entity en1 = this.getNewEntity();
            en1.SetValByKey(pk, dr.getValue(0));
            en1.Retrieve();
            this.AddEntity(en1);
        }
        return dt.Rows.size();
    }

    /**
     * 按照关键字查询。
     * 说明这里是用Attrs接受
     * <p>
     * param key 关键字
     * param attrs
     *
     * @return 返回Table
     * @throws Exception
     */
    public final DataTable RetrieveByKeyReturnTable(String key, Attrs attrs) throws Exception {
        QueryObject qo = new QueryObject(this);

        // 在 Normal 属性里面增加，查询条件。
        Map map = this.getNewEntity().getEnMap();
        qo.addLeftBracket();
        for (Attr en : map.getAttrs()) {
            if (en.getUIContralType() == UIContralType.DDL || en.getUIContralType() == UIContralType.CheckBok) {
                continue;
            }
            qo.addOr();
            qo.AddWhere(en.getKey(), " LIKE ", key);
        }
        qo.addRightBracket();

        //            //
        //			Attrs searchAttrs = map.SearchAttrs;
        //			foreach(Attr attr  in attrs)
        //			{
        //				qo.addAnd();
        //				qo.addLeftBracket();
        //				qo.AddWhere(attr.getKey(), attr.getDefaultVal().ToString() ) ;
        //				qo.addRightBracket();
        //			}
        return qo.DoQueryToTable();
    }

    /**
     * 按照KEY 查找。
     * <p>
     * param keyVal KEY
     *
     * @return 返回朝找出来的个数。
     * @throws Exception
     */
    public int RetrieveByKey(String keyVal) throws Exception {
        keyVal = "%" + keyVal.trim() + "%";
        QueryObject qo = new QueryObject(this);
        Attrs attrs = this.getNewEntity().getEnMap().getAttrs();
        //qo.addLeftBracket();
        String pk = this.getNewEntity().getPK();
        if (!pk.equals("OID")) {
            qo.AddWhere(this.getNewEntity().getPK(), " LIKE ", keyVal);
        }
        for (Attr en : attrs.ToJavaList()) {

            if (en.getUIContralType() == UIContralType.DDL || en.getUIContralType() == UIContralType.CheckBok) {
                continue;
            }

            if (en.getKey().equals(pk)) {
                continue;
            }

            if (en.getMyDataType() != DataType.AppString) {
                continue;
            }

            if (en.getMyFieldType() == FieldType.FK) {
                continue;
            }

            if (en.getMyFieldType() == FieldType.RefText) {
                continue;
            }

            qo.addOr();
            qo.AddWhere(en.getKey(), " LIKE ", keyVal);
        }
        //qo.addRightBracket();
        return qo.DoQuery();
    }

    /**
     * 按LIKE 去查.
     * <p>
     * param key
     * param vals
     *
     * @return
     * @throws Exception
     */
    public int RetrieveByLike(String key, String vals) throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.AddWhere(key, " LIKE ", vals);
        return qo.DoQuery();
    }

    public int Retrieve(String orderBy) throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.addOrderBy(orderBy);
        return qo.DoQuery();
    }

    /**
     * 查询出来，包涵pks 的字串。
     * 比例："001,002,003"
     *
     * @return
     * @throws Exception
     */
    public int RetrieveInPks(String pks) throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.AddWhere(this.getNewEntity().getPK(), " IN ", pks);
        return qo.DoQuery();
    }

    /**
     * 按照IDs查询并且排序
     * 比如: FrmID  IN  '001','002'
     * <p>
     * param key
     * param vals
     *
     * @return
     * @throws Exception
     */
    public final int RetrieveInOrderBy(String key, String vals, String orderByKey) throws Exception {
        QueryObject qo = new QueryObject(this);
        if (vals.contains("(") == false) {
            qo.AddWhere(key, " IN ", "(" + vals + ")");
        } else {
            qo.AddWhere(key, " IN ", vals);
        }
        if (DataType.IsNullOrEmpty(orderByKey) == false) {
            qo.addOrderBy(orderByKey);
        }
        return qo.DoQuery();
    }

    /**
     * 按照IDs查询
     * 比如: FrmID  IN  '001','002'
     * <p>
     * param key
     * param vals
     *
     * @return
     * @throws Exception
     */
    public int RetrieveIn(String key, String vals) throws Exception {
        return RetrieveIn(key, vals, null);
    }

    public int RetrieveIn(String key, String vals, String orderby) throws Exception {
        QueryObject qo = new QueryObject(this);

        if (vals.contains("(") == false)
            qo.AddWhere(key, " IN ", "(" + vals + ")");
        else
            qo.AddWhere(key, " IN ", vals);

        if (DataType.IsNullOrEmpty(orderby) == false)
            qo.addOrderBy(orderby);

        return qo.DoQuery();
    }


    public int RetrieveInSQL(String attr, String sql) throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.AddWhereInSQL(attr, sql);
        return qo.DoQuery();
    }

    public int RetrieveInSQL(String attr, String sql, String orderBy) throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.AddWhereInSQL(attr, sql);
        qo.addOrderBy(orderBy);
        return qo.DoQuery();
    }

    public int RetrieveInSQL(String sql) throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.AddWhereInSQL(this.getNewEntity().getPK(), sql);
        return qo.DoQuery();
    }

    public int RetrieveInSQL_Order(String sql, String orderby) throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.AddWhereInSQL(this.getNewEntity().getPK(), sql);
        qo.addOrderBy(orderby);
        return qo.DoQuery();
    }

    public int Retrieve(String key, boolean val) throws Exception {
        QueryObject qo = new QueryObject(this);
        if (val) {
            qo.AddWhere(key, 1);
        } else {
            qo.AddWhere(key, 0);
        }
        return qo.DoQuery();
    }

    public int Retrieve(String key, Object val) throws Exception {
        return Retrieve(key, val, null);
    }


    public int Retrieve(String key, Object val, String orderby) throws Exception {
        QueryObject qo = new QueryObject(this);

        if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB) {
            qo.AddWhere(key, bp.sys.base.Glo.GenerRealType(this.getNewEntity().getEnMap().getAttrs(), key, val));
        } else {
            qo.AddWhere(key, val);
        }

        if (orderby != null) {
            qo.addOrderBy(orderby); //这个排序方式不要变化，否则会影响其他的地方。
        }
        return qo.DoQuery();
    }

    public int Retrieve(String key, Object val, String key2, Object val2) throws Exception {
        return Retrieve(key, val, key2, val2, null);
    }


    public int Retrieve(String key, Object val, String key2, Object val2, String ordery) throws Exception {
        QueryObject qo = new QueryObject(this);

        if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB) {
            qo.AddWhere(key, bp.sys.base.Glo.GenerRealType(this.getNewEntity().getEnMap().getAttrs(), key, val));
            qo.addAnd();
            qo.AddWhere(key2, bp.sys.base.Glo.GenerRealType(this.getNewEntity().getEnMap().getAttrs(), key2, val2));
        } else {
            qo.AddWhere(key, val);
            qo.addAnd();
            qo.AddWhere(key2, val2);
        }
        if (ordery != null) {
            qo.addOrderBy(ordery);
        }
        return qo.DoQuery();
    }


    public final int Retrieve(String key, Object val, String key2, Object val2, String key3, Object val3, String key4, Object val4) throws Exception {
        return Retrieve(key, val, key2, val2, key3, val3, key4, val4, null);
    }


    public final int Retrieve(String key, Object val, String key2, Object val2, String key3, Object val3, String key4, Object val4, String orderBy) throws Exception {
        QueryObject qo = new QueryObject(this);

        if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB) {
            qo.AddWhere(key, bp.sys.base.Glo.GenerRealType(this.getNewEntity().getEnMap().getAttrs(), key, val));
            qo.addAnd();
            qo.AddWhere(key2, bp.sys.base.Glo.GenerRealType(this.getNewEntity().getEnMap().getAttrs(), key2, val2));
            qo.addAnd();
            qo.AddWhere(key3, bp.sys.base.Glo.GenerRealType(this.getNewEntity().getEnMap().getAttrs(), key3, val3));
            qo.addAnd();
            qo.AddWhere(key4, bp.sys.base.Glo.GenerRealType(this.getNewEntity().getEnMap().getAttrs(), key4, val4));
        } else {
            qo.AddWhere(key, val);
            qo.addAnd();
            qo.AddWhere(key2, val2);
            qo.addAnd();
            qo.AddWhere(key3, val3);
            qo.addAnd();
            qo.AddWhere(key4, val4);
        }

        if (orderBy != null) {
            qo.addOrderBy(orderBy);
        }
        return qo.DoQuery();
    }

    public final int Retrieve(String key, Object val, String key2, Object val2, String key3, Object val3) throws Exception {
        return Retrieve(key, val, key2, val2, key3, val3, null);
    }


    public final int Retrieve(String key, Object val, String key2, Object val2, String key3, Object val3, String orderBy) throws Exception {
        QueryObject qo = new QueryObject(this);

        if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB) {
            qo.AddWhere(key, bp.sys.base.Glo.GenerRealType(this.getNewEntity().getEnMap().getAttrs(), key, val));
            qo.addAnd();
            qo.AddWhere(key2, bp.sys.base.Glo.GenerRealType(this.getNewEntity().getEnMap().getAttrs(), key2, val2));
            qo.addAnd();
            qo.AddWhere(key3, bp.sys.base.Glo.GenerRealType(this.getNewEntity().getEnMap().getAttrs(), key3, val3));
        } else {
            qo.AddWhere(key, val);
            qo.addAnd();
            qo.AddWhere(key2, val2);
            qo.addAnd();
            qo.AddWhere(key3, val3);
        }
        if (orderBy != null) {
            qo.addOrderBy(orderBy);
        }
        return qo.DoQuery();
    }

    /**
     * 查询全部
     *
     * @return
     */
    public int RetrieveAll() throws Exception {
        return this.RetrieveAll(null);
    }

    public int RetrieveAllOrderByRandom() throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.addOrderByRandom();
        return qo.DoQuery();
    }

    public int RetrieveAllOrderByRandom(int topNum) throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.Top = topNum;
        qo.addOrderByRandom();
        return qo.DoQuery();
    }

    public int RetrieveAll(int topNum, String orderby) throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.Top = topNum;
        qo.addOrderBy(orderby);
        return qo.DoQuery();
    }

    public int RetrieveAll(int topNum, String orderby, boolean isDesc) throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.Top = topNum;
        if (isDesc) {
            qo.addOrderByDesc(orderby);
        } else {
            qo.addOrderBy(orderby);
        }
        return qo.DoQuery();
    }

    /**
     * 查询全部
     *
     * @return
     */
    public int RetrieveAll(String orderBy) throws Exception {
        QueryObject qo = new QueryObject(this);
        if (DataType.IsNullOrEmpty(orderBy) == false) {
            qo.addOrderBy(orderBy);
        }
        return qo.DoQuery();
    }

    /**
     * 查询全部。
     *
     * @return
     */
    public int RetrieveAll(String orderBy1, String orderBy2) throws Exception {
        QueryObject qo = new QueryObject(this);
        if (orderBy1 != null) {
            qo.addOrderBy(orderBy1, orderBy2);
        }
        return qo.DoQuery();
    }

    /**
     * 按照最大个数查询。
     * <p>
     * param MaxNum 最大NUM
     *
     * @return
     */
    public final int RetrieveAll(int MaxNum) throws Exception {
        QueryObject qo = new QueryObject(this);
        qo.Top = MaxNum;
        return qo.DoQuery();
    }

    /**
     * 查询全部的结果放到DataTable。
     *
     * @return
     */
    public final DataTable RetrieveAllToTable() throws Exception {
        QueryObject qo = new QueryObject(this);
        return qo.DoQueryToTable();
    }

    private DataTable DealBoolTypeInDataTable(Entity en, DataTable dt) throws Exception {

        for (Attr attr : en.getEnMap().getAttrs()) {
            if (attr.getMyDataType() == DataType.AppBoolean) {
                DataColumn col = new DataColumn();
                col.ColumnName = "tmp" + attr.getKey();
                col.DataType = Boolean.class;
                dt.Columns.Add(col);
                for (DataRow dr : dt.Rows) {
                    if (dr.getValue(attr.getKey()).toString().equals("1")) {
                        dr.setValue("tmp" + attr.getKey(), true);
                    } else {
                        dr.setValue("tmp" + attr.getKey(), false);
                    }
                }
                dt.Columns.remove(attr.getKey());
                dt.Columns.get("tmp" + attr.getKey()).setColumnName(attr.getKey());
                continue;
            }
            if (attr.getMyDataType() == DataType.AppDateTime || attr.getMyDataType() == DataType.AppDate) {
                DataColumn col = new DataColumn();
                col.ColumnName = "tmp" + attr.getKey();
                col.DataType = Date.class;
                dt.Columns.Add(col);
                for (DataRow dr : dt.Rows) {
                    try {
                        dr.setValue("tmp" + attr.getKey(), Date.parse(dr.getValue(attr.getKey()).toString()));
                    } catch (java.lang.Exception e) {
                        if (attr.getDefaultVal().toString().equals("")) {
                            dr.setValue("tmp" + attr.getKey(), new Date());
                        } else {
                            dr.setValue("tmp" + attr.getKey(), Date.parse(attr.getDefaultVal().toString()));
                        }

                    }

                }
                dt.Columns.remove(attr.getKey());
                dt.Columns.get("tmp" + attr.getKey()).setColumnName(attr.getKey());
                continue;
            }
        }
        return dt;
    }


    /**
     * ToJson.
     *
     * @return
     */

    public final String ToJson() throws Exception {
        return ToJson("dt");
    }


    public final String ToJson(String dtName) throws Exception {
        return bp.tools.Json.ToJson(this.ToDataTableField(dtName));
    }

    public final DataTable ToDataTableStringField() throws Exception {
        return ToDataTableStringField("dt");
    }


    public final DataTable ToDataTableStringField(String tableName) throws Exception {
        DataTable dt = this.ToEmptyTableStringField();
        Entity en = this.getNewEntity();
        Attrs attrs = en.getEnMap().getAttrs();

        dt.setTableName(tableName);
        for (Entity myen : convertEntities(this)) {
            DataRow dr = dt.NewRow();
            for (Attr attr : attrs) {
                if (attr.getMyDataType() == DataType.AppBoolean) {
                    if (myen.GetValIntByKey(attr.getKey()) == 1) {
                        dr.setValue(attr.getKey(), "1");
                    } else {
                        dr.setValue(attr.getKey(), "0");
                    }
                    continue;
                }

                /*如果是外键 就要去掉左右空格。
                 *  */
                if (attr.getMyFieldType() == FieldType.FK || attr.getMyFieldType() == FieldType.PKFK) {
                    dr.setValue(attr.getKey(), myen.GetValByKey(attr.getKey()).toString().trim());
                } else {
                    dr.setValue(attr.getKey(), myen.GetValByKey(attr.getKey()));
                }
            }
            dt.Rows.add(dr);
        }
        return dt;
    }

    /**
     * 把当前实体集合的数据库转换成Table。
     *
     * @return DataTable
     */

    public final DataTable ToDataTableField() {
        return ToDataTableField("dt");
    }


    public final DataTable ToDataTableField(String tableName) {
        DataTable dt = this.ToEmptyTableField();

        Entity en = this.getNewEntity();
        Attrs attrs = en.getEnMap().getAttrs();

        dt.setTableName(tableName);

        for (int i = 0; i < this.size(); i++) {
            Entity myen = this.get(i);
            DataRow dr = dt.NewRow();
            for (Attr attr : attrs) {
                if (attr.getMyDataType() == DataType.AppBoolean) {
                    if (myen.GetValIntByKey(attr.getKey()) == 1) {
                        dr.setValue(attr.getKey(), "1");
                    } else {
                        dr.setValue(attr.getKey(), "0");
                    }
                    continue;
                }

                Object val = myen.GetValByKey(attr.getKey());
                if (val == null) {
                    continue;
                }

                /*如果是外键 就要去掉左右空格
                 *  */
                if (attr.getMyFieldType() == FieldType.FK || attr.getMyFieldType() == FieldType.PKFK) {
                    dr.setValue(attr.getKey(), val.toString().trim());
                } else {
                    dr.setValue(attr.getKey(), val);
                }
            }
            dt.Rows.add(dr);
        }
        return dt;
    }

    public final DataTable ToDataTableDesc() throws Exception {
        DataTable dt = this.ToEmptyTableDesc();
        Entity en = this.getNewEntity();

        dt.setTableName(en.getEnMap().getPhysicsTable());
        for (Entity myen : convertEntities(this)) {
            DataRow dr = dt.NewRow();
            for (Attr attr : en.getEnMap().getAttrs()) {

                if (attr.getMyDataType() == DataType.AppBoolean) {
                    if (myen.GetValBooleanByKey(attr.getKey())) {
                        dr.setValue(attr.getDesc(), "是");
                    } else {
                        dr.setValue(attr.getDesc(), "否");
                    }
                    continue;
                }
                dr.setValue(attr.getDesc(), myen.GetValByKey(attr.getKey()));
            }
            dt.Rows.add(dr);
        }
        return dt;
    }

    public final DataTable ToEmptyTableDescField() throws Exception {
        DataTable dt = new DataTable();
        Entity en = this.getNewEntity();
        try {

            for (Attr attr : en.getEnMap().getAttrs()) {

                switch (attr.getMyDataType()) {
                    case DataType.AppString:
                        dt.Columns.Add(new DataColumn(attr.getDesc().trim() + attr.getKey(), String.class));
                        break;
                    case DataType.AppInt:
                        dt.Columns.Add(new DataColumn(attr.getDesc().trim() + attr.getKey(), Integer.class));
                        break;
                    case DataType.AppFloat:
                        dt.Columns.Add(new DataColumn(attr.getDesc().trim() + attr.getKey(), Float.class));
                        break;
                    case DataType.AppBoolean:
                        dt.Columns.Add(new DataColumn(attr.getDesc().trim() + attr.getKey(), String.class));
                        break;
                    case DataType.AppDouble:
                        dt.Columns.Add(new DataColumn(attr.getDesc().trim() + attr.getKey(), Double.class));
                        break;
                    case DataType.AppMoney:
                        dt.Columns.Add(new DataColumn(attr.getDesc().trim() + attr.getKey(), Double.class));
                        break;
                    case DataType.AppDate:
                        dt.Columns.Add(new DataColumn(attr.getDesc().trim() + attr.getKey(), String.class));
                        break;
                    case DataType.AppDateTime:
                        dt.Columns.Add(new DataColumn(attr.getDesc().trim() + attr.getKey(), String.class));
                        break;
                    default:
                        throw new RuntimeException("@bulider insert sql error: 没有这个数据类型");
                }
            }
        } catch (RuntimeException ex) {
            throw new RuntimeException(en.getEnDesc() + " error " + ex.getMessage());

        }
        return dt;
    }

    public final DataTable ToDataTableDescField() throws Exception {
        return ToDataTableDescField("dt");
    }


    public final DataTable ToDataTableDescField(String tableName) throws Exception {
        DataTable dt = this.ToEmptyTableDescField();
        dt.setTableName(tableName);

        Entity en = this.getNewEntity();

        dt.setTableName(en.getEnMap().getPhysicsTable());
        for (Entity myen : convertEntities(this)) {
            DataRow dr = dt.NewRow();
            for (Attr attr : en.getEnMap().getAttrs()) {

                if (attr.getMyDataType() == DataType.AppBoolean) {
                    if (myen.GetValBooleanByKey(attr.getKey())) {
                        dr.setValue(attr.getDesc().trim() + attr.getKey(), "是");
                    } else {
                        dr.setValue(attr.getDesc().trim() + attr.getKey(), "否");
                    }
                    continue;
                }
                dr.setValue(attr.getDesc().trim() + attr.getKey(), myen.GetValByKey(attr.getKey()));
            }
            dt.Rows.add(dr);
        }
        return dt;
    }

    /**
     * 把系统的实体的PK转换为string
     * 比如: "001,002,003,"。
     * <p>
     * param flag 分割符号, 一般来说用 ' ; '
     *
     * @return 转化后的String / 实体集合为空就 return null
     */
    public final String ToStringOfPK(String flag, boolean isCutEndFlag) throws Exception {
        String pk = null;
        for (Entity en : convertEntities(this)) {
            pk += en.getPKVal() + flag;
        }
        if (isCutEndFlag) {
            pk = pk.substring(0, pk.length() - 1);
        }

        return pk;
    }

    /**
     * 把系统的实体的PK转换为 string
     * 比如: "001,002,003,"。
     *
     * @return 转化后的String / 实体集合为空就 return null
     */
    public final String ToStringOfSQLModelByPK() throws Exception {
        if (this.size() == 0) {
            return "''";
        }
        return ToStringOfSQLModelByKey(this.get(0).getPK());
    }

    /**
     * 把系统的实体的PK转换为 string
     * 比如: "001,002,003,"。
     *
     * @return 转化后的String / 实体集合为空就 return "''"
     */
    public final String ToStringOfSQLModelByKey(String key) throws Exception {
        if (this.size() == 0) {
            return "''";
        }

        String pk = null;
        for (Entity en : convertEntities(this)) {
            pk += "'" + en.GetValStringByKey(key) + "',";
        }

        pk = pk.substring(0, pk.length() - 1);

        return pk;
    }

    /**
     * 空的Table
     * 取到一个空表结构。
     *
     * @return
     */

    public final DataTable ToEmptyTableField() {
        return ToEmptyTableField(null);
    }


    public final DataTable ToEmptyTableField(Entity en) {
        DataTable dt = new DataTable();
        if (en == null) {
            en = this.getNewEntity();
        }

        dt.setTableName(en.getEnMap().getPhysicsTable());

        for (Attr attr : en.getEnMap().getAttrs()) {
            switch (attr.getMyDataType()) {
                case DataType.AppString:
                    dt.Columns.Add(new DataColumn(attr.getKey(), String.class));
                    break;
                case DataType.AppInt:
                    dt.Columns.Add(new DataColumn(attr.getKey(), Integer.class));
                    break;
                case DataType.AppFloat:
                    dt.Columns.Add(new DataColumn(attr.getKey(), Float.class));
                    break;
                case DataType.AppBoolean:
                    dt.Columns.Add(new DataColumn(attr.getKey(), String.class));
                    break;
                case DataType.AppDouble:
                    dt.Columns.Add(new DataColumn(attr.getKey(), Double.class));
                    break;
                case DataType.AppMoney:
                    dt.Columns.Add(new DataColumn(attr.getKey(), Double.class));
                    break;
                case DataType.AppDate:
                    dt.Columns.Add(new DataColumn(attr.getKey(), String.class));
                    break;
                case DataType.AppDateTime:
                    dt.Columns.Add(new DataColumn(attr.getKey(), String.class));
                    break;
                default:
                    throw new RuntimeException("@bulider insert sql error: 没有这个数据类型");
            }
        }
        return dt;
    }

    public final DataTable ToEmptyTableStringField() throws Exception {
        DataTable dt = new DataTable();
        Entity en = this.getNewEntity();
        dt.setTableName(en.getEnMap().getPhysicsTable());
        for (Attr attr : en.getEnMap().getAttrs()) {
            dt.Columns.Add(new DataColumn(attr.getKey(), String.class));
        }
        return dt;
    }

    public final DataTable ToEmptyTableDesc() throws Exception {
        DataTable dt = new DataTable();
        Entity en = this.getNewEntity();
        try {

            for (Attr attr : en.getEnMap().getAttrs()) {
                switch (attr.getMyDataType()) {
                    case DataType.AppString:
                        dt.Columns.Add(new DataColumn(attr.getDesc(), String.class));
                        break;
                    case DataType.AppInt:
                        dt.Columns.Add(new DataColumn(attr.getDesc(), Integer.class));
                        break;
                    case DataType.AppFloat:
                        dt.Columns.Add(new DataColumn(attr.getDesc(), Float.class));
                        break;
                    case DataType.AppBoolean:
                        dt.Columns.Add(new DataColumn(attr.getDesc(), String.class));
                        break;
                    case DataType.AppDouble:
                        dt.Columns.Add(new DataColumn(attr.getDesc(), Double.class));
                        break;
                    case DataType.AppMoney:
                        dt.Columns.Add(new DataColumn(attr.getDesc(), Double.class));
                        break;
                    case DataType.AppDate:
                        dt.Columns.Add(new DataColumn(attr.getDesc(), String.class));
                        break;
                    case DataType.AppDateTime:
                        dt.Columns.Add(new DataColumn(attr.getDesc(), String.class));
                        break;
                    default:
                        throw new RuntimeException("@bulider insert sql error: 没有这个数据类型");
                }
            }
        } catch (RuntimeException ex) {
            throw new RuntimeException(en.getEnDesc() + " error " + ex.getMessage());

        }
        return dt;
    }


    /**
     * 缓存查询: 根据 in sql 方式进行。
     * <p>
     * param CacheKey 指定的缓存Key，全局变量不要重复。
     * param inSQL sql 语句
     *
     * @return 返回放在缓存里面的结果集合
     * @throws Exception
     */
    public final int RetrieveFromCacheInSQL(String CacheKey, String inSQL) throws Exception {
        this.clear();
        bp.en.Entities tempVar = Cache.GetEnsDataExt(CacheKey);
        Entities ens = tempVar instanceof Entities ? (Entities) tempVar : null;
        if (ens == null) {
            QueryObject qo = new QueryObject(this);
            qo.AddWhereInSQL(this.getNewEntity().getPK(), inSQL);
            qo.DoQuery();
            Cache.SetEnsDataExt(CacheKey, this);
        } else {
            this.AddEntities(ens);
        }
        return this.size();
    }

    /**
     * 缓存查询: 根据相关的条件
     * <p>
     * param attrKey 属性: 比如 FK_Sort
     * param val 值: 比如:01
     * param top 最大的取值信息
     * param orderBy 排序字段
     * param isDesc
     *
     * @return 返回放在缓存里面的结果集合
     * @throws Exception
     */
    public final int RetrieveFromCache(String attrKey, Object val, int top, String orderBy, boolean isDesc) throws Exception {
        String CacheKey = this.toString() + attrKey + val + top + orderBy + isDesc;
        this.clear();
        Entities ens = Cache.GetEnsDataExt(CacheKey);
        if (ens == null) {
            QueryObject qo = new QueryObject(this);
            qo.Top = top;

            if (attrKey.equals("") || attrKey == null) {
            } else {
                qo.AddWhere(attrKey, val);
            }

            if (orderBy != null) {
                if (isDesc) {
                    qo.addOrderByDesc(orderBy);
                } else {
                    qo.addOrderBy(orderBy);
                }
            }

            qo.DoQuery();
            Cache.SetEnsDataExt(CacheKey, this);
        } else {
            this.AddEntities(ens);
        }
        return this.size();
    }

    /**
     * 缓存查询: 根据相关的条件
     * <p>
     * param attrKey
     * param val
     *
     * @return
     * @throws Exception
     */
    public final int RetrieveFromCache(String attrKey, Object val) throws Exception {
        return RetrieveFromCache(attrKey, val, 99999, null, true);
    }

    /**
     * 缓存查询: 根据相关的条件
     * <p>
     * param attrKey
     * param val
     * param orderby
     *
     * @return
     */
    public final int RetrieveFromCache(String attrKey, Object val, String orderby) throws Exception {
        return RetrieveFromCache(attrKey, val, 99999, orderby, true);
    }

    /**
     * 缓存查询: 根据相关的条件
     * <p>
     * param top
     * param orderBy
     * param isDesc
     *
     * @return
     */
    public final int RetrieveFromCache(String orderBy, boolean isDesc, int top) throws Exception {
        return RetrieveFromCache(null, null, top, orderBy, isDesc);
    }

    ///


    ///包含方法

    /**
     * 是否包含任意一个实体主键编号
     * <p>
     * param keys 多个主键用,符合分开
     *
     * @return true包含任意一个，fale 一个都不包含.
     */
    public final boolean ContainsAnyOnePK(String keys) throws Exception {
        keys = "," + keys + ",";
        for (Entity en : convertEntities(this)) {
            if (keys.contains("," + en.getPKVal() + ",") == true) {
                return true;
            }
        }
        return false;
    }

    /**
     * 包含所有的主键
     * <p>
     * param keys 多个主键用,符合分开
     *
     * @return true全部包含.
     */
    public final boolean ContainsAllPK(String keys) throws Exception {
        keys = "," + keys + ",";
        for (Entity en : convertEntities(this)) {
            if (keys.contains("," + en.getPKVal() + ",") == false) {
                return false;
            }
        }
        return true;
    }

    ///


    ///类名属性.

    /**
     * 类名
     */
    public String getClassID() {
        return this.toString();
    }

    /**
     * 短类名
     */
    public String GetClassIDOfShort() {
        String clsID = this.getClassID();
        return clsID.substring(clsID.lastIndexOf('.') + 1);
    }

    /**
     * 把Object转换成List实体集合
     * param obj
     *
     * @return
     */
    public static ArrayList<Entity> convertEntities(Object obj) {
        return (ArrayList<Entity>) obj;
    }

    public List<Entity> ToJavaListEn() {
        return (List<Entity>) (Object) (Object) this;
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

}
