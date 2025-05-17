package bp.en;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.sys.*;
import bp.sys.xml.XmlEn;
import bp.tools.StringHelper;
import bp.web.WebUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;

/**
 Entity 的摘要说明。
*/
public abstract class Entity extends EnObj implements Serializable
{
	private static final long serialVersionUID = 1L;
	///自动标记获取属性实体方法.
	/**
	 从AutoNum缓存中获取实体s

	 param ens 实体集合
	 param refKey 查询的外键
	 param refVal 外键值
	 @return 返回实体集合
	 * @
	*/

	public final Entities GetEntitiesAttrFromAutoNumCache(Entities ens, String refKey, Object refVal, String refKey2,Object refVal2) throws Exception {
		return GetEntitiesAttrFromAutoNumCache(ens, refKey, refVal, refKey2, refVal2,null);
	}

	public final Entities GetEntitiesAttrFromAutoNumCache(Entities ens, String refKey, Object refVal) throws Exception {
		return GetEntitiesAttrFromAutoNumCache(ens, refKey, refVal, null, null,null);
	}

	public final Entities GetEntitiesAttrFromAutoNumCache(Entities ens, String refKey, Object refVal, String refKey2, Object refVal2,String orderBy) throws Exception {
		//获得段类名.
		String clsName = ens.GetClassIDOfShort();

		//判断内存是否有？
		/*Object tempVar = this.GetRefObject(clsName);
		Entities objs = tempVar instanceof Entities ? (Entities)tempVar : null;
		if (objs != null)
		{
			return objs; //如果缓存有值，就直接返回.
		}

		int count = this.GetParaInt(clsName + "_AutoNum", -1);
		if (count == -1)
		{
			if (refKey2 == null)
			{
				if(DataType.IsNullOrEmpty(orderBy)==false)
					ens.Retrieve(refKey, refVal,orderBy);
				else
					ens.Retrieve(refKey, refVal);
			}
			else
			{
				if(DataType.IsNullOrEmpty(orderBy)==false)
					ens.Retrieve(refKey, refVal, refKey2, refVal2,orderBy);
				else
					ens.Retrieve(refKey, refVal, refKey2, refVal2);
			}

			this.SetPara(clsName + "_AutoNum", ens.size()); //设置他的数量.
			this.DirectUpdate();
			this.SetRefObject(clsName, ens);
			return ens;
		}

		if (count == 0)
		{
			ens.clear();
			this.SetRefObject(clsName, ens);
			return ens;
		}*/

		if (refKey2 == null)
		{
			if (DataType.IsNullOrEmpty(orderBy) == false)
				ens.Retrieve(refKey, refVal, orderBy);
			else
				ens.Retrieve(refKey, refVal);
		}
		else
		{
			if (DataType.IsNullOrEmpty(orderBy) == false)
				ens.Retrieve(refKey, refVal, refKey2, refVal2, orderBy);
			else
				ens.Retrieve(refKey, refVal, refKey2, refVal2);
		}

		if (ens.size() != 0)
		{
			this.SetPara(clsName + "_AutoNum", ens.size()); //设置他的数量.
			this.DirectUpdate();
		}

		this.SetRefObject(clsName, ens);
		return ens;
	}
	/**
	 清除缓存记录
	 把值设置为 -1,执行的时候，让其重新获取.
	 * @
	*/

	public final void ClearAutoNumCache() throws Exception {
		ClearAutoNumCache(true);
	}

	public final void ClearAutoNumCache(boolean isUpdata) throws Exception {
		boolean isHave = false;
		for (Object key : this.getatPara().getHisHT().keySet())
		{
			if (key==null || DataType.IsNullOrEmpty(key) == true)
			{
				continue;
			}

			if (key.toString().endsWith("_AutoNum") == true)
			{
				if (this.GetParaInt(key.toString()) != -1)
				{
					this.SetPara(key.toString(), -1);
					this.SetRefObject(key.toString().replace("_AutoNum", ""),null);
					isHave = true;
				}
			}
		}
		if (isHave == true && isUpdata == true)
		{
			this.DirectUpdate();
		}
	}


	///与缓存有关的操作
	private Entities _GetNewEntities = null;
	public Entities GetNewEntities() throws Exception {
		if (_GetNewEntities == null) {
			String str = this.toString();
			String ensName = str + "s";

			_GetNewEntities = ClassFactory.GetEns(ensName);
			if (_GetNewEntities != null){
				try {
					return _GetNewEntities.getClass().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}

			ArrayList al = ClassFactory.GetObjects("bp.en.Entities");
			for (Object o : al) {
				Entities ens = (Entities) ((o instanceof Entities) ? o : null);

				if (ens == null) {
					continue;
				}
				if (ens.getNewEntity().toString() != null && ens.getNewEntity().toString().equals(str)) {
					_GetNewEntities = ens;
					try {
						return _GetNewEntities.getClass().newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
			throw new RuntimeException("@no ens" + this.toString());
		}
		try {
			return _GetNewEntities.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 类名
	 * @
	*/
	public String getClassID() {
		return this.toString();
	}
	/**
	 短类名
	*/
	public String GetClassIDOfShort() throws Exception {
		String clsID = this.getClassID();
		return clsID.substring(clsID.lastIndexOf('.') + 1);
	}


	protected SQLCache _SQLCache = null;
	@JsonIgnore
	public SQLCache getSQLCache() throws Exception {
		if (_SQLCache == null)
		{
			_SQLCache = bp.da.Cache.GetSQL(this.toString());
			if (_SQLCache == null)
			{
				_SQLCache = new SQLCache(this);
				bp.da.Cache.SetSQL(this.toString(), _SQLCache);
			}
		}
		return _SQLCache;
	}
	public void setSQLCache(SQLCache value)
	{
		_SQLCache = value;
	}
	/**
	 转化成

	 @return
	 * @
	*/
	public final String ToStringAtParas() throws Exception {
		String str = "";
		for (Attr attr : this.getEnMap().getAttrs())
		{
			str += "@" + attr.getKey() + "=" + this.GetValByKey(attr.getKey());
		}
		return str;
	}
	public final bp.da.KeyVals ToKeyVals() throws Exception {
		KeyVals kvs = new KeyVals();
		for (Attr attr : this.getEnMap().getAttrs())
		{
			//kvs.Add(attr.getKey(), this.GetValByKey(attr.getKey()), );
		}
		return kvs;
	}

	/**
	 把一个实体转化成Json.
	 @return 返回该实体的单个json
	 * @
	*/

	public final String ToJson() {
		return ToJson(true);
	}


	public final String ToJson(boolean isInParaFields)  {
		Hashtable ht = this.getRow();
		//如果不包含参数字段.
		if (isInParaFields == false || ht.containsKey("AtPara") == false )
			return bp.tools.Json.ToJsonEntityModel(ht);
		try
		{
			/*如果包含这个字段  @FK_BanJi=01 */
			AtPara ap = this.getatPara();
			for (Object key : ap.getHisHT().keySet())
			{
				if (ht.containsKey(key) == true)
				{
					continue;
				}
				ht.put(key, ap.getHisHT().get(key));
			}

			//把参数属性移除.
			ht.remove("_ATObj_");
		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteWarning("@ ToJson " + ex.getMessage());
		}

		return bp.tools.Json.ToJson(ht);
	}

	/**
	 转化成json字符串，包含外键与枚举，主表使用Main命名。
	 外键使用外键表命名，枚举值使用枚举值命名。

	 @return
	 * @
	*/
	public final String ToJsonIncludeEnumAndForeignKey() throws Exception {
		DataSet ds = new DataSet();

		ds.Tables.add(this.ToDataTableField()); //把主表数据加入进去.

		Attrs attrs = this.getEnMap().getAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getUIBindKey().equals(""))
			{
				continue;
			}

			if (attr.getItIsEnum())
			{
				if (ds.GetTableByName(attr.getUIBindKey())!=null)
				{
					continue;
				}
				SysEnums ses = new SysEnums(attr.getUIBindKey());

				DataTable dt = ses.ToDataTableField(attr.getUIBindKey()); //把枚举加入进去.
				ds.Tables.add(dt); //把这个枚举值加入进去..
				continue;
			}

			if (attr.getItIsFK())
			{
				if (ds.GetTableByName(attr.getUIBindKey())!=null)
				{
					continue;
				}

				Entities ens = attr.getHisFKEns();
				ens.RetrieveAll();

				DataTable dt = ens.ToDataTableField(attr.getUIBindKey()); //把外键表加入进去.
				ds.Tables.add(dt); //把这个枚举值加入进去..
				continue;
			}
		}
		return bp.tools.Json.ToJson(ds);
	}
	/**
	 创建一个空的表

	 param
	 @return
	*/

	public final DataTable ToEmptyTableField() {
		return ToEmptyTableField(null);
	}


	public final DataTable ToEmptyTableField(Entity en)  {
		DataTable dt = new DataTable();
		if (en == null)
		{
			en = this;
		}

		dt.TableName = en.getEnMap().getPhysicsTable();

		for (Attr attr : en.getEnMap().getAttrs())
		{
			switch (attr.getMyDataType())
			{
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

	public final DataTable ToDataTableField() {
		return ToDataTableField("Main");
	}


	public final DataTable ToDataTableField(String tableName)  {
		DataTable dt = this.ToEmptyTableField(this);
		dt.TableName = tableName;

		//增加参数列.
		if (this.getRow().containsKey("AtPara") == true)
		{
			/*如果包含这个字段,就说明他有参数,把参数也要弄成一个列.*/
			AtPara ap = this.getatPara();
			for (Object key : ap.getHisHT().keySet())
			{
				if (key == null || dt.Columns.contains(key) == true)
				{
					continue;
				}

				dt.Columns.Add(key.toString(), String.class);
			}
		}

		DataRow dr = dt.NewRow();
		for (Attr attr : this.getEnMap().getAttrs())
		{
			if (attr.getMyDataType() == DataType.AppBoolean)
			{
				if (this.GetValIntByKey(attr.getKey()) == 1)
				{
					dr.setValue(attr.getKey(), "1");
				}
				else
				{
					dr.setValue(attr.getKey(), "0");
				}
				continue;
			}

			/*如果是外键 就要去掉左右空格。
			 *  */
			if (attr.getMyFieldType() == FieldType.FK || attr.getMyFieldType() == FieldType.PKFK)
			{
				dr.setValue(attr.getKey(), this.GetValByKey(attr.getKey()).toString().trim());
			}
			else
			{
				Object obj = this.GetValByKey(attr.getKey());
				if (obj == null && attr.getItIsNum())
				{
					dr.setValue(attr.getKey(), (Object)null);
					continue;
				}

				if (attr.getItIsNum() == true && DataType.IsNumStr(obj.toString()) == false)
				{
					dr.setValue(attr.getKey(), (Object)null);
				}
				else
				{
					dr.setValue(attr.getKey(), obj);
				}
			}
		}

		if (this.getRow().containsKey("AtPara") == true)
		{
			/*如果包含这个字段*/
			AtPara ap = this.getatPara();
			for (Object key : ap.getHisHT().keySet())
			{
				if(key == null)
					continue;
				if (DataType.IsNullOrEmpty(dr.getValue(key.toString()).toString()) == true)
				{
					dr.setValue(key.toString(), ap.getHisHT().get(key));
				}
			}
		}

		dt.Rows.add(dr);
		return dt;
	}

		///


		///关于database 操作
	public  int RunSQL(String sql) throws Exception {
		Paras ps = new Paras();
		ps.SQL=sql;
		return this.RunSQL(ps);
	}
	/**
	 在此实体是运行sql 返回结果集合

	 param ps 要运行的sql
	 @return 执行的结果
	*/
	public final int RunSQL(Paras ps) throws Exception {
		switch (this.getEnMap().getEnDBUrl().getDBUrlType())
		{
			case AppCenterDSN:
				return DBAccess.RunSQL(ps);

			case DBSrc:
				return this.getEnMap().getEnDBUrl().getHisDBSrc().RunSQL(ps.SQL, ps);
			default:
				throw new RuntimeException("@没有设置类型。");
		}
	}
	public final int RunSQL(String sql, Paras paras) throws Exception {
		switch (this.getEnMap().getEnDBUrl().getDBUrlType())
		{
			case AppCenterDSN:
				return DBAccess.RunSQL(sql, paras);

			case DBSrc:
				return this.getEnMap().getEnDBUrl().getHisDBSrc().RunSQL(sql, paras);
			default:
				throw new RuntimeException("@没有设置类型。");
		}
	}
	/**
	 在此实体是运行sql 返回结果集合

	 param sql 要运行的 select sql
	 @return 执行的查询结果
	*/

	public  DataTable RunSQLReturnTable(String sql) throws Exception {
		return RunSQLReturnTable(sql, null);
	}



	public  DataTable RunSQLReturnTable(String sql, Paras paras) throws Exception {
		switch (this.getEnMap().getEnDBUrl().getDBUrlType())
		{
			case AppCenterDSN:
				return DBAccess.RunSQLReturnTable(sql, paras);

			case DBSrc:
				return this.getEnMap().getEnDBUrl().getHisDBSrc().RunSQLReturnTable(sql);
			default:
				throw new RuntimeException("@没有设置类型。");
		}
	}

	/**
	 查询SQL返回int

	 param sql
	 @return
	*/

	public final int RunSQLReturnValInt(String sql) throws Exception {
		return RunSQLReturnValInt(sql, null);
	}


	public final int RunSQLReturnValInt(String sql, Paras paras) throws Exception {
		if (paras == null)
		{
			paras = new Paras();
		}

		paras.SQL=sql;
		switch (this.getEnMap().getEnDBUrl().getDBUrlType())
		{
			case AppCenterDSN:
				return DBAccess.RunSQLReturnValInt(paras, 0);

			case DBSrc:
				return this.getEnMap().getEnDBUrl().getHisDBSrc().RunSQLReturnValInt(sql);
			default:
				throw new RuntimeException("@没有设置类型。");
		}
	}


		///


		///关于明细的操作
	public final Entities GetEnsDaOfOneVSM(AttrOfOneVSM attr) throws Exception {
		Entities ensOfMM = attr.getEnsOfMM();
		Entities ensOfM = attr.getEnsOfM();
		ensOfM.clear();

		QueryObject qo = new QueryObject(ensOfMM);
		qo.AddWhere(attr.getAttrOfOneInMM(), this.getPKVal().toString());
		qo.DoQuery();

		for (Entity en : ensOfMM)
		{
			Entity enOfM = ensOfM.getNewEntity();
			enOfM.setPKVal(en.GetValStringByKey(attr.getAttrOfMInMM()));
			enOfM.Retrieve();
			ensOfM.AddEntity(enOfM);
		}
		return ensOfM;
	}
	/**
	 取得实体集合多对多的实体集合.

	 param ensOfMMclassName 实体集合的类名称
	 @return 数据实体
	 * @
	*/
	public final Entities GetEnsDaOfOneVSM(String ensOfMMclassName) throws Exception {
		AttrOfOneVSM attr = this.getEnMap().GetAttrOfOneVSM(ensOfMMclassName);

		return GetEnsDaOfOneVSM(attr);
	}
	public final Entities GetEnsDaOfOneVSMFirst() throws Exception {
		AttrOfOneVSM attr = this.getEnMap().getAttrsOfOneVSM().get(0);
		return this.GetEnsDaOfOneVSM(attr);
	}



		///关于明细的操作
	/**
	 得到他的数据实体

	 param EnsName 类名称
	 @return
	 * @
	*/
	public final Entities GetDtlEnsDa(String EnsName) throws Exception {
		Entities ens = ClassFactory.GetEns(EnsName);
		return GetDtlEnsDa(ens);

	}
	/**
	 取出他的数据实体

	 param ens 集合
	 @return 执行后的实体信息
	 * @
	*/
	public final Entities GetDtlEnsDa(Entities ens) throws Exception {
		for (EnDtl dtl : this.getEnMap().getDtls())
		{
			if (dtl.getEns().getClass() == ens.getClass())
			{
				QueryObject qo = new QueryObject(dtl.getEns());
				qo.AddWhere(dtl.getRefKey(), this.getPKVal().toString());
				qo.DoQuery();
				return dtl.getEns();
			}
		}
		throw new RuntimeException("@在取[" + this.getEnDesc() + "]的明细时出现错误。[" + ens.getNewEntity().getEnDesc() + "],不在他的集合内。");
	}


	public final Entities GetDtlEnsDa(EnDtl dtl) throws Exception {
		return GetDtlEnsDa(dtl, null);
	}


	public final Entities GetDtlEnsDa(EnDtl dtl, String pkval) throws Exception {
		try
		{
			if (pkval == null)
			{
				pkval = this.getPKVal().toString();
			}
			QueryObject qo = new QueryObject(dtl.getEns());
			MapDtl md = new MapDtl();
			md.setNo(dtl.getEns().getNewEntity().getClassID());
			if (md.RetrieveFromDBSources() == 0)
			{
				qo.AddWhere(dtl.getRefKey(), pkval);
				qo.DoQuery();
				return dtl.getEns();
			}

			//如果是freefrm 就考虑他的权限控制问题.
			switch (md.getDtlOpenType())
			{
				case ForEmp: // 按人员来控制.
					qo.AddWhere(GEDtlAttr.RefPK, pkval);
					qo.addAnd();
					qo.AddWhere(GEDtlAttr.Rec, WebUser.getNo());
					break;
				case ForWorkID: // 按工作ID来控制
					qo.AddWhere(GEDtlAttr.RefPK, pkval);
					break;
				case ForFID: // 按流程ID来控制.这里不允许修改，如需修改则加新case.

					qo.AddWhere(GEDtlAttr.FID, pkval);
					break;
			}

			if (!md.getFilterSQLExp().equals(""))
			{
				String[] strs = md.getFilterSQLExp().split("[=]", -1);
				qo.addAnd();
				qo.AddWhere(strs[0], strs[1]);
			}

			qo.DoQuery();

		}
		catch (RuntimeException e)
		{
			throw new RuntimeException("@在取[" + this.getEnDesc() + "]的明细时出现错误。[" + dtl.getDesc() + "],不在他的集合内。");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dtl.getEns();
	}


	/**
	 取出他的明细集合。

	 @return
	 * @
	*/
	public final ArrayList GetDtlsDatasOfArrayList() throws Exception {
		ArrayList al = new ArrayList();
		for (EnDtl dtl : this.getEnMap().getDtls())
		{
			al.add(this.GetDtlEnsDa(dtl.getEns()));
		}
		return al;
	}


	public final java.util.ArrayList<Entities> GetDtlsDatasOfList() throws Exception {
		return GetDtlsDatasOfList(null);
	}


	public final ArrayList<Entities> GetDtlsDatasOfList(String pkval) throws Exception {
		ArrayList<Entities> al = new ArrayList<Entities>();
		for (EnDtl dtl : this.getEnMap().getDtls())
		{
			al.add(this.GetDtlEnsDa(dtl, pkval));
		}
		return al;
	}



		///检查一个属性值是否存在于实体集合中
	/**
	 检查一个属性值是否存在于实体集合中
	 这个方法经常用到在beforeinsert中。

	 param key 要检查的key.
	 param val 要检查的key.对应的val
	 @return
	*/
	protected final int ExitsValueNum(String key, String val) throws Exception {
		String field = this.getEnMap().GetFieldByKey(key);
		Paras ps = new Paras();
		ps.Add("p", val);

		String sql = "SELECT COUNT(" + key + " ) FROM " + this.getEnMap().getPhysicsTable() + " WHERE " + key + "=" + this.getHisDBVarStr() + "p";
		return Integer.parseInt(DBAccess.RunSQLReturnVal(sql, ps).toString());
	}

		///


		///于编号有关系的处理。
	/**
	 这个方法是为不分级字典，生成一个编号。根据制订的 属性.

	 param attrKey 属性
	 @return 产生的序号
	 * @
	*/

	public final String GenerNewNoByKey(String attrKey) throws Exception {
		return GenerNewNoByKey(attrKey, null);
	}


	public final String GenerNewNoByKey(String attrKey, Attr attr) throws Exception {
		try
		{
			String sql = null;
			if (attr == null)
			{
				attr = this.getEnMap().GetAttrByKey(attrKey);
			}
			//    if (attr.getUIIsReadonly() == false)
			//      throw new Exception("@需要自动生成编号的列(" + attr.getKey() + ")必须为只读。");

			String field = this.getEnMap().GetFieldByKey(attrKey);
			switch (this.getEnMap().getEnDBUrl().getDBType())
			{
				case MSSQL:
					sql = "SELECT CONVERT(INT, MAX(CAST(" + field + " as int)) )+1 AS No FROM " + this.getEnMap().getPhysicsTable();
					break;
				case PostgreSQL:
				case HGDB:
					sql = "SELECT to_number( MAX(" + field + ") ,'99999999')+1   FROM " + this.getEnMap().getPhysicsTable();
					break;
				case Oracle:
				case KingBaseR3:
				case KingBaseR6:
				case DM:
				case GBASE8CByOracle:
					sql = "SELECT MAX(" + field + ") +1 AS No FROM " + this.getEnMap().getPhysicsTable();
					break;
				case MySQL:
				case GBASE8CByMySQL:
				case GBASE8A:
					sql = "SELECT CONVERT(MAX(CAST(" + field + " AS SIGNED INTEGER)),SIGNED) +1 AS No FROM " + this.getEnMap().getPhysicsTable();
					break;
				case Informix:
					sql = "SELECT MAX(" + field + ") +1 AS No FROM " + this.getEnMap().getPhysicsTable();
					break;
				case Access:
					sql = "SELECT MAX( [" + field + "]) +1 AS  No FROM " + this.getEnMap().getPhysicsTable();
					break;
				default:
					throw new RuntimeException("error");
			}
			String str = String.valueOf(DBAccess.RunSQLReturnValLong(sql, 1));
			if (str.equals("0") || str.equals(""))
			{
				str = "1";
			}
			return StringHelper.padLeft(str, Integer.parseInt(this.getEnMap().getCodeStruct()), '0');
		}
		catch (RuntimeException ex)
		{
			this.CheckPhysicsTable();
			throw ex;
		}
	}
	/**
	 按照一列产生顺序号码。

	 param attrKey 要产生的列
	 param attrGroupKey 分组的列名
	 param attrGroupVal 分组的主键
	 @return
	*/
	public final String GenerNewNoByKey(int nolength, String attrKey, String attrGroupKey, String attrGroupVal) throws Exception {
		if (attrGroupKey == null || attrGroupVal == null)
		{
			throw new RuntimeException("@分组字段attrGroupKey attrGroupVal 不能为空");
		}

		Paras ps = new Paras();
		ps.Add("groupKey", attrGroupKey);
		ps.Add("groupVal", attrGroupVal);

		String sql = "";
		String field = this.getEnMap().GetFieldByKey(attrKey);
		ps.Add("f", attrKey);

		switch (this.getEnMap().getEnDBUrl().getDBType())
		{
			case MSSQL:
				sql = "SELECT CONVERT(bigint, MAX([" + field + "]))+1 AS Num FROM " + this.getEnMap().getPhysicsTable() + " WHERE " + attrGroupKey + "='" + attrGroupVal + "'";
				break;
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case DM:
			case Informix:
			case GBASE8CByOracle:
				sql = "SELECT MAX( :f )+1 AS No FROM " + this.getEnMap().getPhysicsTable() + " WHERE " + this.getHisDBVarStr() + "groupKey=" + this.getHisDBVarStr() + "groupVal ";
				break;
			case MySQL:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql = "SELECT MAX(" + field + ") +1 AS Num FROM " + this.getEnMap().getPhysicsTable() + " WHERE " + attrGroupKey + "='" + attrGroupVal + "'";
				break;
			case Access:
				sql = "SELECT MAX([" + field + "]) +1 AS Num FROM " + this.getEnMap().getPhysicsTable() + " WHERE " + attrGroupKey + "='" + attrGroupVal + "'";
				break;
			default:
				throw new RuntimeException("error");
		}

		DataTable dt = DBAccess.RunSQLReturnTable(sql, ps);
		String str = "1";
		if (dt.Rows.size() != 0)
		{
			if (dt.Rows.get(0).getValue(0)==null)
			{
				str = "1";
			}
			else
			{
				str = dt.Rows.get(0).getValue(0).toString();
			}
		}
		return StringHelper.padLeft(str, nolength, '0');
	}
	public final String GenerNewNoByKey(String attrKey, String attrGroupKey, String attrGroupVal) throws Exception {
		return this.GenerNewNoByKey(Integer.parseInt(this.getEnMap().getCodeStruct()), attrKey, attrGroupKey, attrGroupVal);
	}
	/**
	 按照两列查生顺序号码。

	 param attrKey
	 param attrGroupKey1
	 param attrGroupKey2
	 param attrGroupVal1
	 param attrGroupVal2
	 @return
	 * @
	*/
	public final String GenerNewNoByKey(String attrKey, String attrGroupKey1, String attrGroupKey2, Object attrGroupVal1, Object attrGroupVal2) throws Exception {
		String f = this.getEnMap().GetFieldByKey(attrKey);
		Paras ps = new Paras();
		//   ps.Add("f", f);

		String sql = "";
		switch (this.getEnMap().getEnDBUrl().getDBType())
		{
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case DM:
			case Informix:
			case GBASE8CByOracle:
				sql = "SELECT   MAX(" + f + ") +1 AS No FROM " + this.getEnMap().getPhysicsTable();
				break;
			case MSSQL:
				sql = "SELECT CONVERT(INT, MAX(" + this.getEnMap().GetFieldByKey(attrKey) + ") )+1 AS No FROM " + this.getEnMap().getPhysicsTable() + " WHERE " + this.getEnMap().GetFieldByKey(attrGroupKey1) + "='" + attrGroupVal1 + "' AND " + this.getEnMap().GetFieldByKey(attrGroupKey2) + "='" + attrGroupVal2 + "'";
				break;
			case Access:
				sql = "SELECT CONVERT(INT, MAX(" + this.getEnMap().GetFieldByKey(attrKey) + ") )+1 AS No FROM " + this.getEnMap().getPhysicsTable() + " WHERE " + this.getEnMap().GetFieldByKey(attrGroupKey1) + "='" + attrGroupVal1 + "' AND " + this.getEnMap().GetFieldByKey(attrGroupKey2) + "='" + attrGroupVal2 + "'";
				break;
			default:
				break;
		}

		DataTable dt = DBAccess.RunSQLReturnTable(sql, ps);
		String str = "1";
		if (dt.Rows.size() != 0)
		{
			str = dt.Rows.get(0).getValue(0).toString();
		}
		return StringHelper.padLeft(str, Integer.parseInt(this.getEnMap().getCodeStruct()), '0');
	}
	/**
	 按照两列查生顺序号码。

	 param attrKey 字段
	 param attrGroupKey1
	 param attrGroupKey2
	 param attrGroupKey3
	 param attrGroupVal1
	 param attrGroupVal2
	 param attrGroupVal3
	 @return
	 * @
	*/
	public final String GenerNewNoByKey(String attrKey, String attrGroupKey1, String attrGroupKey2, String attrGroupKey3, Object attrGroupVal1, Object attrGroupVal2, Object attrGroupVal3) throws Exception {
		String f = this.getEnMap().GetFieldByKey(attrKey);
		Paras ps = new Paras();
		//   ps.Add("f", f);

		String sql = "";
		switch (this.getEnMap().getEnDBUrl().getDBType())
		{
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case DM:
			case Informix:
			case GBASE8CByOracle:
				sql = "SELECT   MAX(" + f + ") +1 AS No FROM " + this.getEnMap().getPhysicsTable();
				break;
			case MSSQL:
				sql = "SELECT CONVERT(INT, MAX(" + this.getEnMap().GetFieldByKey(attrKey) + ") )+1 AS No FROM " + this.getEnMap().getPhysicsTable() + " WHERE " + this.getEnMap().GetFieldByKey(attrGroupKey1) + "='" + attrGroupVal1 + "' AND " + this.getEnMap().GetFieldByKey(attrGroupKey2) + "='" + attrGroupVal2 + "'  AND " + this.getEnMap().GetFieldByKey(attrGroupKey3) + "='" + attrGroupVal3 + "'";
				break;
			case Access:
				sql = "SELECT CONVERT(INT, MAX(" + this.getEnMap().GetFieldByKey(attrKey) + ") )+1 AS No FROM " + this.getEnMap().getPhysicsTable() + " WHERE " + this.getEnMap().GetFieldByKey(attrGroupKey1) + "='" + attrGroupVal1 + "' AND " + this.getEnMap().GetFieldByKey(attrGroupKey2) + "='" + attrGroupVal2 + "'  AND " + this.getEnMap().GetFieldByKey(attrGroupKey3) + "='" + attrGroupVal3 + "'";
				break;
			default:
				break;
		}

		DataTable dt = DBAccess.RunSQLReturnTable(sql, ps);
		String str = "1";
		if (dt.Rows.size() != 0)
		{
			str = dt.Rows.get(0).getValue(0).toString();
		}
		return StringHelper.padLeft(str, Integer.parseInt(this.getEnMap().getCodeStruct()), '0');
	}

		///


		///构造方法
	public Entity()
	{
	}

		///


		///排序操作
	protected final void DoOrderUp(String idxAttr) throws Exception
	{
		//  String pkval = this.PKVal as string;
		String pkval = this.getPKVal().toString();
		String pk = this.getPK();
		String table = this.getEnMap().getPhysicsTable();

		String sql = "SELECT " + pk + "," + idxAttr + " FROM " + table + "  ORDER BY " + idxAttr;
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		int idx = 0;
		String beforeNo = "";
		String myNo = "";
		boolean isMeet = false;

		for (DataRow dr : dt.Rows)
		{
			idx++;
			myNo = dr.getValue(pk).toString();
			if (pkval.equals(myNo))
			{
				isMeet = true;
			}

			if (isMeet == false)
			{
				beforeNo = myNo;
			}
			DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idx + " WHERE " + pk + "='" + myNo + "'");
		}
		DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idxAttr + "+1 WHERE " + pk + "='" + beforeNo + "'");
		DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idxAttr + "-1 WHERE " + pk + "='" + pkval + "'");
	}
	protected final void DoOrderUp(String groupKeyAttr, String groupKeyVal, String idxAttr) throws Exception {
		//  String pkval = this.PKVal as string;
		String pkval = this.getPKVal().toString();
		String pk = this.getPK();
		String table = this.getEnMap().getPhysicsTable();

		String sql = "SELECT " + pk + "," + idxAttr + " FROM " + table + " WHERE " + groupKeyAttr + "='" + groupKeyVal + "' ORDER BY " + idxAttr;
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		int idx = 0;
		String beforeNo = "";
		String myNo = "";
		boolean isMeet = false;

		for (DataRow dr : dt.Rows)
		{
			idx++;
			myNo = dr.getValue(pk).toString();
			if (pkval.equals(myNo))
			{
				isMeet = true;
			}

			if (isMeet == false)
			{
				beforeNo = myNo;
			}
			DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idx + " WHERE " + pk + "='" + myNo + "'");
		}
		DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idxAttr + "+1 WHERE " + pk + "='" + beforeNo + "'");
		DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idxAttr + "-1 WHERE " + pk + "='" + pkval + "'");
	}
	protected final void DoOrderUp(String groupKeyAttr, Object gVal1, String gKey2, Object gVal2, String idxAttr) throws Exception {
		//  String pkval = this.PKVal as string;
		String pkval = this.getPKVal().toString();
		String pk = this.getPK();
		String table = this.getEnMap().getPhysicsTable();

		String sql = "SELECT " + pk + "," + idxAttr + " FROM " + table + " WHERE (" + groupKeyAttr + "='" + gVal1 + "' AND " + gKey2 + "='" + gVal2 + "') ORDER BY " + idxAttr;
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		int idx = 0;
		String beforeNo = "";
		String myNo = "";
		boolean isMeet = false;

		for (DataRow dr : dt.Rows)
		{
			idx++;
			myNo = dr.getValue(pk).toString();

			if (myNo.equals(pkval) == true)
			{
				isMeet = true;
			}

			if (isMeet == false)
			{
				beforeNo = myNo;
			}

			DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idx + " WHERE " + pk + "='" + myNo + "'");
		}
		DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idxAttr + "+1 WHERE " + pk + "='" + beforeNo + "'");
		DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idxAttr + "-1 WHERE " + pk + "='" + pkval + "'");
	}
	/**
	 上移

	 param groupKeyAttr
	 param gVal1
	 param gKey2
	 param gVal2
	 param gKey3
	 param gVal3
	 param idxAttr
	 * @
	*/
	protected final void DoOrderUp(String groupKeyAttr, Object gVal1, String gKey2, Object gVal2, String gKey3, Object gVal3, String idxAttr) throws Exception {
		//  String pkval = this.PKVal as string;
		String pkval = this.getPKVal().toString();
		String pk = this.getPK();
		String table = this.getEnMap().getPhysicsTable();

		String sql = "SELECT " + pk + "," + idxAttr + " FROM " + table + " WHERE (" + groupKeyAttr + "='" + gVal1 + "' AND " + gKey2 + "='" + gVal2 + "' AND " + gKey3 + "='" + gVal3 + "') ORDER BY " + idxAttr;
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		int idx = 0;
		String beforeNo = "";
		String myNo = "";
		boolean isMeet = false;

		for (DataRow dr : dt.Rows)
		{
			idx++;
			myNo = dr.getValue(pk).toString();

			if (myNo.equals(pkval) == true)
			{
				isMeet = true;
			}

			if (isMeet == false)
			{
				beforeNo = myNo;
			}

			DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idx + " WHERE " + pk + "='" + myNo + "'");
		}
		DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idxAttr + "+1 WHERE " + pk + "='" + beforeNo + "'");
		DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idxAttr + "-1 WHERE " + pk + "='" + pkval + "'");
	}

	protected final void DoOrderUp(String groupKeyAttr, Object gVal1, String gKey2, Object gVal2, String gKey3, Object gVal3,  String gKey4, Object gVal4,String idxAttr) throws Exception {
		//  String pkval = this.PKVal as string;
		String pkval = this.getPKVal().toString();
		String pk = this.getPK();
		String table = this.getEnMap().getPhysicsTable();

		String sql = "SELECT " + pk + "," + idxAttr + " FROM " + table + " WHERE (" + groupKeyAttr + "='" + gVal1 + "' AND " + gKey2 + "='" + gVal2 + "' AND " + gKey3 + "='" + gVal3 + "' AND " + gKey4 + "='" + gVal4 + "') ORDER BY " + idxAttr;
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		int idx = 0;
		String beforeNo = "";
		String myNo = "";
		boolean isMeet = false;

		for (DataRow dr : dt.Rows)
		{
			idx++;
			myNo = dr.getValue(pk).toString();

			if (myNo.equals(pkval) == true)
			{
				isMeet = true;
			}

			if (isMeet == false)
			{
				beforeNo = myNo;
			}

			DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idx + " WHERE " + pk + "='" + myNo + "'");
		}
		DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idxAttr + "+1 WHERE " + pk + "='" + beforeNo + "'");
		DBAccess.RunSQL("UPDATE " + table + " SET " + idxAttr + "=" + idxAttr + "-1 WHERE " + pk + "='" + pkval + "'");
	}
	/**
	 插队

	 param idxAttr Idx列
	 param entityPKVal 要插入的指定实体主键值
	 param groupKey 列名
	 param
	 * @
	*/
	protected final void DoOrderInsertTo(String idxAttr, Object entityPKVal, String groupKey) throws Exception {
		String ptable = this.getEnMap().getPhysicsTable(); // Sys_MapAttr
		String pk = this.getPK(); //MyPK
		int idx = this.GetValIntByKey(idxAttr); // 当前实体的idx. 10
												//   String groupVal = this.GetValStringByKey(groupKey); //分组的val.   101

		//求出来要被插队的 idx.
		String sql = "SELECT " + idxAttr + "," + groupKey + " FROM " + ptable + " WHERE " + pk + "='" + entityPKVal + "'";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		int idxFirst = Integer.parseInt(dt.Rows.get(0).getValue(0).toString());
		String groupValFirst = dt.Rows.get(0).getValue(1).toString();

		sql = "UPDATE " + ptable + " SET " + idxAttr + "=" + idxFirst + "-1, " + groupKey + "='" + groupValFirst + "' WHERE " + this.getPK() + "='" + this.getPKVal() + "'";
		DBAccess.RunSQL(sql);
	}
	/**
	 排序

	 param idxAttr
	 * @
	*/
	protected final void DoOrderDown(String idxAttr) throws Exception {
		String pkval = this.getPKVal().toString();
		String pk = this.getPK();
		String table = this.getEnMap().getPhysicsTable();

		String sql = "SELECT " + pk + " ," + idxAttr + " FROM " + table + "  order by " + idxAttr;
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		int idx = 0;
		String nextNo = "";
		String myNo = "";
		boolean isMeet = false;

		String sqls = "";
		for (DataRow dr : dt.Rows)
		{
			myNo = dr.getValue(pk).toString();
			if (isMeet == true)
			{
				nextNo = myNo;
				isMeet = false;
			}
			idx++;

			if (pkval.equals(myNo))
			{
				isMeet = true;
			}

			sqls += "@ UPDATE " + table + " SET " + idxAttr + "=" + idx + " WHERE " + pk + "='" + myNo + "'";
		}

		sqls += "@ UPDATE  " + table + " SET " + idxAttr + "=" + idxAttr + "-1 WHERE " + pk + "='" + nextNo + "'";
		sqls += "@ UPDATE  " + table + " SET " + idxAttr + "=" + idxAttr + "+1 WHERE " + pk + "='" + pkval + "'";

		DBAccess.RunSQLs(sqls);
	}
	protected final void DoOrderDown(String groupKeyAttr, String groupKeyVal, String idxAttr) throws Exception {
		String pkval = this.getPKVal().toString();
		String pk = this.getPK();
		String table = this.getEnMap().getPhysicsTable();

		String sql = "SELECT " + pk + " ," + idxAttr + " FROM " + table + " WHERE " + groupKeyAttr + "='" + groupKeyVal + "' order by " + idxAttr;
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		int idx = 0;
		String nextNo = "";
		String myNo = "";
		boolean isMeet = false;

		String sqls = "";
		for (DataRow dr : dt.Rows)
		{
			myNo = dr.getValue(pk).toString();
			if (isMeet == true)
			{
				nextNo = myNo;
				isMeet = false;
			}
			idx++;

			if (pkval.equals(myNo))
			{
				isMeet = true;
			}

			sqls += "@ UPDATE " + table + " SET " + idxAttr + "=" + idx + " WHERE " + pk + "='" + myNo + "'";
		}

		sqls += "@ UPDATE  " + table + " SET " + idxAttr + "=" + idxAttr + "-1 WHERE " + pk + "='" + nextNo + "'";
		sqls += "@ UPDATE  " + table + " SET " + idxAttr + "=" + idxAttr + "+1 WHERE " + pk + "='" + pkval + "'";

		DBAccess.RunSQLs(sqls);
	}
	/**
	 下移

	 param groupKeyAttr 分组字段1
	 param val1 值1
	 param gKeyAttr2 字段2
	 param gKeyVal2 值2
	 param idxAttr 排序字段
	 * @
	*/
	protected final void DoOrderDown(String groupKeyAttr, Object val1, String gKeyAttr2, Object gKeyVal2, String idxAttr) throws Exception {
		String pkval = this.getPKVal().toString();
		String pk = this.getPK();
		String table = this.getEnMap().getPhysicsTable();

		String sql = "SELECT " + pk + " ," + idxAttr + " FROM " + table + " WHERE (" + groupKeyAttr + "='" + val1 + "' AND " + gKeyAttr2 + "='" + gKeyVal2 + "' ) order by " + idxAttr;
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		int idx = 0;
		String nextNo = "";
		String myNo = "";
		boolean isMeet = false;

		String sqls = "";
		for (DataRow dr : dt.Rows)
		{
			myNo = dr.getValue(pk).toString();
			if (isMeet == true)
			{
				nextNo = myNo;
				isMeet = false;
			}
			idx++;

			if (pkval.equals(myNo))
			{
				isMeet = true;
			}

			sqls += "@UPDATE " + table + " SET " + idxAttr + "=" + idx + " WHERE " + pk + "='" + myNo + "' AND  (" + groupKeyAttr + "='" + val1 + "' AND " + gKeyAttr2 + "='" + gKeyVal2 + "' ) ";
		}

		sqls += "@ UPDATE  " + table + " SET " + idxAttr + "=" + idxAttr + "-1 WHERE " + pk + "='" + nextNo + "' AND (" + groupKeyAttr + "='" + val1 + "' AND " + gKeyAttr2 + "='" + gKeyVal2 + "' )";
		sqls += "@ UPDATE  " + table + " SET " + idxAttr + "=" + idxAttr + "+1 WHERE " + pk + "='" + pkval + "' AND (" + groupKeyAttr + "='" + val1 + "' AND " + gKeyAttr2 + "='" + gKeyVal2 + "' )";

		DBAccess.RunSQLs(sqls);
	}
	/**
	 下移

	 param groupKeyAttr 字段1
	 param val1 值1
	 param gKeyAttr2 字段2
	 param gKeyVal2 值2
	 param gKeyAttr3 字段3
	 param gKeyVal3 值3
	 param idxAttr 排序字段
	 * @
	*/
	protected final void DoOrderDown(String groupKeyAttr, Object val1, String gKeyAttr2, Object gKeyVal2, String gKeyAttr3, Object gKeyVal3, String idxAttr) throws Exception {
		String pkval = this.getPKVal().toString();
		String pk = this.getPK();
		String table = this.getEnMap().getPhysicsTable();

		String sql = "SELECT " + pk + " ," + idxAttr + " FROM " + table + " WHERE (" + groupKeyAttr + "='" + val1 + "' AND " + gKeyAttr2 + "='" + gKeyVal2 + "' AND " + gKeyAttr3 + "='" + gKeyVal3 + "' ) order by " + idxAttr;
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		int idx = 0;
		String nextNo = "";
		String myNo = "";
		boolean isMeet = false;

		String sqls = "";
		for (DataRow dr : dt.Rows)
		{
			myNo = dr.getValue(pk).toString();
			if (isMeet == true)
			{
				nextNo = myNo;
				isMeet = false;
			}
			idx++;

			if (pkval.equals(myNo))
			{
				isMeet = true;
			}

			sqls += "@UPDATE " + table + " SET " + idxAttr + "=" + idx + " WHERE " + pk + "='" + myNo + "' AND  (" + groupKeyAttr + "='" + val1 + "' AND " + gKeyAttr2 + "='" + gKeyVal2 + "' ) ";
		}

		sqls += "@ UPDATE  " + table + " SET " + idxAttr + "=" + idxAttr + "-1 WHERE " + pk + "='" + nextNo + "' AND (" + groupKeyAttr + "='" + val1 + "' AND " + gKeyAttr2 + "='" + gKeyVal2 + "'  AND " + gKeyAttr3 + "='" + gKeyVal3 + "' )";
		sqls += "@ UPDATE  " + table + " SET " + idxAttr + "=" + idxAttr + "+1 WHERE " + pk + "='" + pkval + "' AND (" + groupKeyAttr + "='" + val1 + "' AND " + gKeyAttr2 + "='" + gKeyVal2 + "'  AND " + gKeyAttr3 + "='" + gKeyVal3 + "' )";

		DBAccess.RunSQLs(sqls);
	}
	protected final void DoOrderDown(String groupKeyAttr, Object val1, String gKeyAttr2, Object gKeyVal2, String gKeyAttr3, Object gKeyVal3,String gKeyAttr4, Object gKeyVal4,  String idxAttr) throws Exception {
		String pkval = this.getPKVal().toString();
		String pk = this.getPK();
		String table = this.getEnMap().getPhysicsTable();

		String sql = "SELECT " + pk + " ," + idxAttr + " FROM " + table + " WHERE (" + groupKeyAttr + "='" + val1 + "' AND " + gKeyAttr2 + "='" + gKeyVal2 + "' AND " + gKeyAttr3 + "='" + gKeyVal3 + "'  AND " + gKeyAttr4 + "='" + gKeyVal4 + "' ) order by " + idxAttr;
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		int idx = 0;
		String nextNo = "";
		String myNo = "";
		boolean isMeet = false;

		String sqls = "";
		for (DataRow dr : dt.Rows)
		{
			myNo = dr.getValue(pk).toString();
			if (isMeet == true)
			{
				nextNo = myNo;
				isMeet = false;
			}
			idx++;

			if (pkval.equals(myNo))
			{
				isMeet = true;
			}

			sqls += "@UPDATE " + table + " SET " + idxAttr + "=" + idx + " WHERE " + pk + "='" + myNo + "' AND  (" + groupKeyAttr + "='" + val1 + "' AND " + gKeyAttr2 + "='" + gKeyVal2 + "' ) ";
		}

		sqls += "@ UPDATE  " + table + " SET " + idxAttr + "=" + idxAttr + "-1 WHERE " + pk + "='" + nextNo + "' AND (" + groupKeyAttr + "='" + val1 + "' AND " + gKeyAttr2 + "='" + gKeyVal2 + "'  AND " + gKeyAttr3 + "='" + gKeyVal3 + "'  AND " + gKeyAttr4 + "='" + gKeyVal4 + "')";
		sqls += "@ UPDATE  " + table + " SET " + idxAttr + "=" + idxAttr + "+1 WHERE " + pk + "='" + pkval + "' AND (" + groupKeyAttr + "='" + val1 + "' AND " + gKeyAttr2 + "='" + gKeyVal2 + "'  AND " + gKeyAttr3 + "='" + gKeyVal3 + "'  AND " + gKeyAttr4 + "='" + gKeyVal4 + "' )";

		DBAccess.RunSQLs(sqls);
	}

		/// 排序操作


		///直接操作
	/**
	 直接更新
	 * @
	*/
	public final int DirectUpdate() throws Exception
	{
		try
		{
			return EntityDBAccess.Update(this, null);

		}
		catch (RuntimeException ex)
		{
			if (ex.getMessage().contains("列名") || ex.getMessage().contains("将截断字符串") || ex.getMessage().contains("缺少") || ex.getMessage().contains("的值太大"))
			{
				/*说明字符串长度有问题.*/
				this.CheckPhysicsTable();

				//执行字段扩充检查.
				boolean isCheck = CheckPhysicsTableAutoExtFieldLength(ex);
				if (isCheck == true)
				{
					return this.DirectUpdate();
				}
			}
			throw ex;
		}
	}
	/**
	 直接的Insert
	 * @throws Exception
	*/
	public int DirectInsert() throws Exception
	{
		try
		{
			switch (SystemConfig.getAppCenterDBType())
			{
				case MSSQL:
					return this.RunSQL(this.getSQLCache().Insert, SqlBuilder.GenerParas(this, null));
				case Access:
					return this.RunSQL(this.getSQLCache().Insert, SqlBuilder.GenerParas(this, null));
				case MySQL:
				case Informix:
				default:
					return this.RunSQL(this.getSQLCache().Insert.replace("[", "").replace("]", ""), SqlBuilder.GenerParas(this, null));
			}
		}
		catch (RuntimeException ex)
		{

			this.CheckPhysicsTable();

			//执行字段扩充检查.
			boolean isCheck = CheckPhysicsTableAutoExtFieldLength(ex);
			if (isCheck == true)
			{
				return this.Insert();
			}

			this.roll();
			throw ex;
		}
	}
	/**
	 直接的Delete
	 * @throws Exception
	*/
	public final void DirectDelete() throws Exception
	{
		EntityDBAccess.Delete(this);
	}
	public  void DirectSave() throws Exception
	{
		if (this.getIsExits())
		{
			this.DirectUpdate();
		}
		else
		{
			this.DirectInsert();
		}
	}



		///Retrieve
	/**
	 按照属性查询

	 param attr1 属性名称
	 param val1 值
	 @return 是否查询到
	 * @throws Exception
	*/
	public final boolean RetrieveByAttrAnd(String attr1, Object val1, String attr2, Object val2) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(attr1, val1);
		qo.addAnd();
		qo.AddWhere(attr2, val2);

		if (qo.DoQuery() >= 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 按照属性查询

	 param attr1 属性名称
	 param val1 值
	 @return 是否查询到
	 * @throws Exception
	*/
	public final boolean RetrieveByAttrOr(String attr1, Object val1, String attr2, Object val2) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(attr1, val1);
		qo.addOr();
		qo.AddWhere(attr2, val2);

		if (qo.DoQuery() == 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 按照属性查询

	 param attr 属性名称
	 param val 值
	 @return 是否查询到
	 * @throws Exception
	*/
	public final boolean RetrieveByAttr(String attr, Object val) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(attr, val);

		if (qo.DoQuery() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 从DBSources直接查询

	 @return 查询的个数
	 * @throws Exception
	*/
	public int RetrieveFromDBSources() throws Exception
	{
		try
		{
			int i =  EntityDBAccess.Retrieve(this, this.getSQLCache().Select, SqlBuilder.GenerParasPK(this));
			if (this.getEnMap().getDepositaryOfEntity() == Depositary.Application)
			{
				Cache2019.UpdateRow(this.toString(), this.getPKVal().toString(), this.getRow());
			}
			return i;
		}
		catch (java.lang.Exception e)
		{
			this.CheckPhysicsTable();
			return EntityDBAccess.Retrieve(this, this.getSQLCache().Select, SqlBuilder.GenerParasPK(this));
		}
	}
	/**
	 查询

	 param key
	 param val
	 @return
	 * @throws Exception
	*/
	public final int Retrieve(String key, Object val) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(key, val);
		return qo.DoQuery();
	}

	public final int Retrieve(String key1, Object val1, String key2, Object val2) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(key1, val1);
		qo.addAnd();
		qo.AddWhere(key2, val2);
		return qo.DoQuery();
	}
	public final int Retrieve(String key1, Object val1, String key2, Object val2, String key3, Object val3) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(key1, val1);
		qo.addAnd();
		qo.AddWhere(key2, val2);
		qo.addAnd();
		qo.AddWhere(key3, val3);
		return qo.DoQuery();
	}
	/**
	 按主键查询，返回查询出来的个数。
	 如果查询出来的是多个实体，那把第一个实体给值。

	 @return 查询出来的个数
	 * @throws Exception
	*/
	public int Retrieve() throws Exception {
		/*如果是没有放入缓存的实体. @wangyanyan */
//        if (this.getEnMap().getDepositaryOfEntity() == Depositary.Application)
//        {
//        	Row row = bp.da.Cache2019.GetRow(this.toString(), this.getPKVal().toString());
//            if (row != null)
//            {
//                this.setRow(row);
//                return 1;
//            }
//        }

		// 如果是没有放入缓存的实体.
		try {

			int i = EntityDBAccess.Retrieve(this,this.getSQLCache().Select, SqlBuilder.GenerParasPK(this));
			if (i > 0){
                //@wangyanyan 放入缓存.
                if (this.getEnMap().getDepositaryOfEntity() == Depositary.Application)
                {
                    bp.da.Cache2019.PutRow(this.toString(), this.getPKVal().toString(), this.getRow());
                }
				return i;
			}

		} catch (RuntimeException ex) {

			String msg = ex.getMessage() == null ? "" : ex.getMessage();
			if (msg.contains("does not exist")
					|| (msg.contains("不存在") && msg.contains("记录[枚举注册  Sys_EnumMain,") == false)
					|| msg.contains("doesn't exist") //@wwh.
					|| msg.contains("无效")
					|| msg.contains("field list")) {
				try {

					this.CheckPhysicsTable();
					if (bp.da.DBAccess.IsExits(this.getEnMap().getPhysicsTable()) == true)
						return this.RetrieveFromDBSources();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			throw new RuntimeException(msg + "@在Entity(" + this.toString() + ")查询期间出现错误@" + ex.getStackTrace());
		} catch (Exception e) {
			e.printStackTrace();
		}

		String msg = "";
		String pk = this.getPK();

		if (pk.equals("OID")) {
			msg += "[ 主键=OID 值=" + this.GetValStrByKey("OID") + " ]";
		} else if (pk.equals("No")) {
			msg += "[ 主键=No 值=" + this.GetValStrByKey("No") + " ]";
		} else if (pk.equals("MyPK")) {
			msg += "[ 主键=MyPK 值=" + this.GetValStrByKey("MyPK") + " ]";
		} else if (pk.equals("NodeID")) {
			msg += "[ 主键=NodeID 值=" + this.GetValStrByKey("NodeID") + " ]";
		} else if (pk.equals("WorkID")) {
			msg += "[ 主键=WorkID 值=" + this.GetValStrByKey("WorkID") + " ]";
		} else {
			Hashtable ht = this.getPKVals(); /*
									 * warning for (Object key : ht.keySet())
									 */
			Set<String> keys = ht.keySet();
			for (Object key : keys) {
				msg += "[ 主键=" + key + " 值=" + ht.get(key) + " ]";
			}

			throw new RuntimeException("@没有[" + this.getEnMap().getEnDesc() + "  " + this.getEnMap().getPhysicsTable()
					+ ", 类[" + this.toString() + "], 物理表[" + this.getEnMap().getPhysicsTable() + "] 实例。" + msg);

		}

		Log.DefaultLogWriteLine(LogType.Error, "@没有[" + this.getEnMap().getEnDesc() + "  " + this.getEnMap().getPhysicsTable() + ", 类[" + this.toString() + "], 物理表[" + this.getEnMap().getPhysicsTable() + "] 实例。PK = " + this.GetValByKey(this.getPK()));
		throw new RuntimeException("@记录[" + this.getEnMap().getEnDesc() + "  " + this.getEnMap().getPhysicsTable() + ", " + msg + "不存在.");
	}
	public boolean IsExits() throws Exception {
		return	getIsExits();
	}
	/**
	 判断是不是存在的方法.

	 @return
	 * @throws Exception
	*/
	public boolean getIsExits() throws Exception
	{
		try
		{
			if (this.getPKField().contains(","))
			{
				Attrs attrs = this.getEnMap().getAttrs();

					/*说明多个主键*/
				QueryObject qo = new QueryObject(this);
				String[] pks = this.getPKField().split("[,]", -1);

				boolean isNeedAddAnd = false;
				for (String pk : pks)
				{
					if (DataType.IsNullOrEmpty(pk))
					{
						continue;
					}

					if (isNeedAddAnd == true)
					{
						qo.addAnd();
					}
					else
					{
						isNeedAddAnd = true;
					}

					Attr attr = attrs.GetAttrByKey(pk);
					switch (attr.getMyDataType())
					{
						case DataType.AppBoolean:
						case DataType.AppInt:
							qo.AddWhere(pk, this.GetValIntByKey(attr.getKey()));
							break;
						case DataType.AppDouble:
						case DataType.AppMoney:
							qo.AddWhere(pk, this.GetValDecimalByKey(attr.getKey(),4));
							break;
						default:
							qo.AddWhere(pk, this.GetValStringByKey(attr.getKey()));
							break;
					}

				}

				if (qo.DoQueryToTable().Rows.size() == 0)
				{
					return false;
				}

				return true;
			}

			Object obj = this.getPKVal();
			if (obj == null || obj.toString().equals(""))
			{
				return false;
			}

			if (this.getItIsOIDEntity())
			{
				if (obj.toString().equals("0"))
				{
					return false;
				}
			}

				// 生成数据库判断语句。
			String selectSQL = "SELECT " + this.getPKField() + " FROM " + this.getEnMap().getPhysicsTable() + " WHERE ";
			switch (this.getEnMap().getEnDBUrl().getDBType())
			{
				case MSSQL:
					selectSQL += SqlBuilder.GetKeyConditionOfMS(this);
					break;
				case Oracle:
				case KingBaseR3:
				case KingBaseR6:
				case DM:
				case PostgreSQL:
				case HGDB:
				case GBASE8CByOracle:
					selectSQL += SqlBuilder.GetKeyConditionOfOraForPara(this);
					break;
				case Informix:
					selectSQL += SqlBuilder.GetKeyConditionOfInformixForPara(this);
					break;
				case MySQL:
				case GBASE8CByMySQL:
				case GBASE8A:
					selectSQL += SqlBuilder.GetKeyConditionOfMS(this);
					break;
				case Access:
					selectSQL += SqlBuilder.GetKeyConditionOfOLE(this);
					break;
				default:
					throw new RuntimeException("@没有设计到。" + this.getEnMap().getEnDBUrl().getDBUrlType());
			}

				// 从数据库里面查询，判断有没有。
			switch (this.getEnMap().getEnDBUrl().getDBUrlType())
			{
				case AppCenterDSN:
					return DBAccess.IsExits(selectSQL, SqlBuilder.GenerParasPK(this));
					//case DBUrlType.DBAccessOfMSSQL1:
					//    return DBAccessOfMSSQL1.IsExits(selectSQL);
					//case DBUrlType.DBAccessOfMSSQL2:
					//    return DBAccessOfMSSQL2.IsExits(selectSQL);
					//case DBUrlType.DBAccessOfOracle1:
					//    return DBAccessOfOracle1.IsExits(selectSQL);
					//case DBUrlType.DBAccessOfOracle2:
					//    return DBAccessOfOracle2.IsExits(selectSQL);
				default:
					throw new RuntimeException("@没有设计到的DBUrl。" + this.getEnMap().getEnDBUrl().getDBUrlType());
			}

		}
		catch (RuntimeException ex)
		{
			this.CheckPhysicsTable();
			throw ex;
		}
	}
	/**
	 按照主键查询，查询出来的结果不赋给当前的实体。

	 @return 查询出来的个数
	 * @throws Exception
	*/
	public final DataTable RetrieveNotSetValues() throws Exception
	{
		Paras ps = new Paras();
        ps.SQL=SqlBuilder.Retrieve(this);
        ps.Add(this.getPK(), this.getPKVal());

        return this.RunSQLReturnTable(ps.SQL,ps);
	}
	/**
	 这个表里是否存在

	 param pk
	 param val
	 @return
	 * @throws Exception
	*/
	public final boolean IsExit(String pk, Object val) throws Exception
	{
		if (pk.equals("OID"))
		{
			if (Integer.parseInt(val.toString()) == 0)
			{
				return false;
			}
			else
			{
				return true;
			}
		}

		QueryObject qo = new QueryObject(this);
		qo.AddWhere(pk, val);
		if (qo.DoQuery() == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	public final boolean IsExit(String pk1, Object val1, String pk2, Object val2) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(pk1, val1);
		qo.addAnd();
		qo.AddWhere(pk2, val2);

		if (qo.DoQuery() == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public final boolean IsExit(String pk1, Object val1, String pk2, Object val2, String pk3, Object val3) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(pk1, val1);
		qo.addAnd();
		qo.AddWhere(pk2, val2);
		qo.addAnd();
		qo.AddWhere(pk3, val3);

		if (qo.DoQuery() == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

		///


		///删除.
	private boolean CheckDB()
	{

			///检查数据.
		//CheckDatas  ens=new CheckDatas(this.getEnMap().getPhysicsTable());
		//foreach(CheckData en in ens)
		//{
		//    String sql="DELETE  FROM "+en.RefTBName+"   WHERE  "+en.RefTBFK+" ='"+this.GetValByKey(en.MainTBPK) +"' ";
		//    DBAccess.RunSQL(sql);
		//}

			///


			///判断是否有明细
		for (bp.en.EnDtl dtl : this.getEnMap().getDtls())
		{
			//String sql = "DELETE  FROM  " + dtl.getEns().getNewEntity().getEnMap().getPhysicsTable() + "   WHERE  " + dtl.getRefKey() + " ='" + this.getPKVal().toString() + "' ";
			//DBAccess.RunSQL(sql);
			/*
			//String sql="SELECT "+dtl.RefKey+" FROM  "+dtl.getEns().getNewEntity().getEnMap().getPhysicsTable()+"   WHERE  "+dtl.RefKey+" ='"+this.PKVal.ToString() +"' ";
			DataTable dt= DBAccess.RunSQLReturnTable(sql);
			if(dt.Rows.size()==0)
			    continue;
			else
			    throw new Exception("@["+this.EnDesc+"],删除期间出现错误，它有["+dt.Rows.size()+"]个明细存在,不能删除！");
			    */
		}

			///

		return true;
	}
	/**
	 删除之前要做的工作

	 @return
	 * @throws Exception
	*/
	protected boolean beforeDelete() throws Exception {
		this.CheckDB();
		return true;
	}
	/**
	 删除它关连的实体．
	 * @throws Exception
	*/
	public final void DeleteHisRefEns() throws Exception
	{

			///检查数据.
		//			CheckDatas  ens=new CheckDatas(this.getEnMap().getPhysicsTable());
		//			foreach(CheckData en in ens)
		//			{
		//				String sql="DELETE  FROM "+en.RefTBName+"   WHERE  "+en.RefTBFK+" ='"+this.GetValByKey(en.MainTBPK) +"' ";
		//				DBAccess.RunSQL(sql);
		//			}

			///


			///判断是否有明细
		for (bp.en.EnDtl dtl : this.getEnMap().getDtls())
		{
			String sql = "DELETE FROM " + dtl.getEns().getNewEntity().getEnMap().getPhysicsTable() + "   WHERE  " + dtl.getRefKey() + " ='" + this.getPKVal().toString() + "' ";
			DBAccess.RunSQL(sql);
		}

			///


			///判断是否有一对对的关系.
		for (bp.en.AttrOfOneVSM dtl : this.getEnMap().getAttrsOfOneVSM())
		{
			String sql = "DELETE  FROM " + dtl.getEnsOfMM().getNewEntity().getEnMap().getPhysicsTable() + "   WHERE  " + dtl.getAttrOfOneInMM() + " ='" + this.getPKVal().toString() + "' ";
			DBAccess.RunSQL(sql);
		}

			///
	}
	/**
	 把缓存删除
	 * @throws Exception
	*/
	public final void DeleteDataAndCache() throws Exception
	{
		this.Delete();
		this.DeleteFromCache();
	}
	public final void DeleteFromCache() throws Exception
	{
		//删除缓存.
		CacheEntity.Delete(this.toString(), this.getPKVal().toString());
		// 删除数据.
		this.getRow().clear();
	}
	public final int Delete() throws Exception
	{
		if (this.beforeDelete() == false)
		{
			return 0;
		}

		int i = 0;
		try
		{
			i = EntityDBAccess.Delete(this);
		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteInfo(ex.getMessage());
			throw ex;
		}

		//更新缓存.  @wangyanyan
		if (this.getEnMap().getDepositaryOfEntity() == Depositary.Application)
		{
			Cache2019.DeleteRow(this.toString(), this.getPKVal().toString());
		}

		this.afterDelete();
		return i;
	}
	/**
	 直接删除指定的

	 param pk
	 * @throws Exception
	*/
	public final int Delete(Object pk) throws Exception
	{
		Paras ps = new Paras();
		ps.Add(this.getPK(), pk);
		switch (this.getEnMap().getEnDBUrl().getDBType())
		{
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case DM:
			case MSSQL:
			case MySQL:
			case GBASE8CByOracle:
			case GBASE8CByMySQL:
			case GBASE8A:
				return DBAccess.RunSQL("DELETE FROM " + this.getEnMap().getPhysicsTable() + " WHERE " + this.getPK() + " =" + this.getHisDBVarStr() + pk);
			default:
				throw new RuntimeException("没有涉及到的类型。");
		}
	}
	/**
	 删除指定的数据

	 param attr
	 param val
	 * @throws Exception
	*/
	public final int Delete(String attr, Object val) throws Exception
	{
		Paras ps = new Paras();
		//ps.Add(attr, val);
		if (SystemConfig.getAppCenterDBFieldIsParaDBType() == true)
		{
			ps.Add(attr, bp.sys.base.Glo.GenerRealType(this.getEnMap().getAttrs(), attr, val));
		}
		else
		{
			ps.Add(attr, val);
		}

		return DBAccess.RunSQL("DELETE FROM " + this.getEnMap().getPhysicsTable() + " WHERE " + this.getEnMap().GetAttrByKey(attr).getField() + " =" + this.getHisDBVarStr() + attr, ps);
	}
	public final int Delete(String attr1, Object val1, String attr2, Object val2) throws Exception
	{
		Paras ps = new Paras();

		if (SystemConfig.getAppCenterDBFieldIsParaDBType() == true)
		{
			ps.Add(attr1, bp.sys.base.Glo.GenerRealType(this.getEnMap().getAttrs(), attr1, val1));
			ps.Add(attr2, bp.sys.base.Glo.GenerRealType(this.getEnMap().getAttrs(), attr2, val2));

		}
		else
		{
			ps.Add(attr1, val1);
			ps.Add(attr2, val2);
		}

		return DBAccess.RunSQL("DELETE FROM " + this.getEnMap().getPhysicsTable() + " WHERE " + this.getEnMap().GetAttrByKey(attr1).getField() + " =" + this.getHisDBVarStr() + attr1 + " AND " + this.getEnMap().GetAttrByKey(attr2).getField() + " =" + this.getHisDBVarStr() + attr2, ps);
	}
	public final int Delete(String attr1, Object val1, String attr2, Object val2, String attr3, Object val3) throws Exception
	{
		Paras ps = new Paras();

		if (SystemConfig.getAppCenterDBFieldIsParaDBType() == true)
		{
			ps.Add(attr1, bp.sys.base.Glo.GenerRealType(this.getEnMap().getAttrs(), attr1, val1));
			ps.Add(attr2, bp.sys.base.Glo.GenerRealType(this.getEnMap().getAttrs(), attr2, val2));
			ps.Add(attr3, bp.sys.base.Glo.GenerRealType(this.getEnMap().getAttrs(), attr3, val3));
		}
		else
		{
			ps.Add(attr1, val1);
			ps.Add(attr2, val2);
			ps.Add(attr3, val3);
		}

		return DBAccess.RunSQL("DELETE FROM " + this.getEnMap().getPhysicsTable() + " WHERE " + this.getEnMap().GetAttrByKey(attr1).getField() + " =" + this.getHisDBVarStr() + attr1 + " AND " + this.getEnMap().GetAttrByKey(attr2).getField() + " =" + this.getHisDBVarStr() + attr2 + " AND " + this.getEnMap().GetAttrByKey(attr3).getField() + " =" + this.getHisDBVarStr() + attr3, ps);
	}
	public final int Delete(String attr1, Object val1, String attr2, Object val2, String attr3, Object val3, String attr4, Object val4) throws Exception
	{
		Paras ps = new Paras();

		if (SystemConfig.getAppCenterDBFieldIsParaDBType() == true)
		{
			ps.Add(attr1, bp.sys.base.Glo.GenerRealType(this.getEnMap().getAttrs(), attr1, val1));
			ps.Add(attr2, bp.sys.base.Glo.GenerRealType(this.getEnMap().getAttrs(), attr2, val2));
			ps.Add(attr3, bp.sys.base.Glo.GenerRealType(this.getEnMap().getAttrs(), attr3, val3));
			ps.Add(attr4, bp.sys.base.Glo.GenerRealType(this.getEnMap().getAttrs(), attr4, val4));

		}
		else
		{
			ps.Add(attr1, val1);
			ps.Add(attr2, val2);
			ps.Add(attr3, val3);
			ps.Add(attr4, val4);
		}

		return DBAccess.RunSQL("DELETE FROM " + this.getEnMap().getPhysicsTable() + " WHERE " + this.getEnMap().GetAttrByKey(attr1).getField() + " =" + this.getHisDBVarStr() + attr1 + " AND " + this.getEnMap().GetAttrByKey(attr2).getField() + " =" + this.getHisDBVarStr() + attr2 + " AND " + this.getEnMap().GetAttrByKey(attr3).getField() + " =" + this.getHisDBVarStr() + attr3 + " AND " + this.getEnMap().GetAttrByKey(attr4).getField() + " =" + this.getHisDBVarStr() + attr4, ps);
	}
	protected void afterDelete() throws Exception
	{
		if (this.getEnMap().getDepositaryOfEntity() != Depositary.Application)
		{
			return;
		}
		/**删除缓存。
		*/
		bp.da.CacheEntity.Delete(this.toString(), this.getPKVal().toString());
		return;
	}

		///参数字段
	public final AtPara getatPara()  {
		Object tempVar = this.getRow().GetValByKey("_ATObj_");
		AtPara at = tempVar instanceof AtPara ? (AtPara)tempVar : null;
		if (at != null)
			return at;
		try
		{
			String atParaStr = this.GetValStringByKey("AtPara");
			if (DataType.IsNullOrEmpty(atParaStr) == false)
			{
				at = new AtPara(atParaStr);
				this.SetValByKey("_ATObj_", at);
				return at;
			}
			at = new AtPara(atParaStr);
			this.SetValByKey("_ATObj_", at);
			return at;
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("@获取参数AtPara时出现异常" + ex.getMessage() + "，可能是您没有加入约定的参数字段AtPara. " + ex.getMessage());
		}
	}

	/**
	 获取参数

	 param key
	 @return
	 * @
	*/
	public final String GetParaString(String key)  {
		removeATObj(key);
		return getatPara().GetValStrByKey(key);
	}
	/**
	 获取参数

	 param key
	 param isNullAsVal
	 @return
	 * @
	*/
	public final String GetParaString(String key, String isNullAsVal)  {
		removeATObj(key);
		String str = getatPara().GetValStrByKey(key);
		if (DataType.IsNullOrEmpty(str))
		{
			return isNullAsVal;
		}
		return str;
	}
	/**
	 获取参数Init值

	 param key
	 @return
	 * @
	*/

	public final int GetParaInt(String key)  {
		return GetParaInt(key, 0);
	}


	public final int GetParaInt(String key, int isNullAsVal) {
		removeATObj(key);
		return getatPara().GetValIntByKey(key, isNullAsVal);
	}

	public final float GetParaFloat(String key) throws Exception {
		return GetParaFloat(key, 0);
	}


	public final float GetParaFloat(String key, float isNullAsVal) {
		removeATObj(key);
		return getatPara().GetValFloatByKey(key, isNullAsVal);
	}
	/**
	 获取参数boolen值

	 param key
	 @return
	 * @
	*/
	public final boolean GetParaBoolen(String key) {
		removeATObj(key);
		return getatPara().GetValBoolenByKey(key);
	}
	/**
	 获取参数boolen值

	 param key
	 param IsNullAsVal
	 @return
	 * @
	*/
	public final boolean GetParaBoolen(String key, boolean IsNullAsVal) {
		removeATObj(key);
		return getatPara().GetValBoolenByKey(key, IsNullAsVal);
	}
	private  void removeATObj(String key)  {
		AtPara para = getatPara();
		if(para.getHisHT().contains(key)==false)
			this.getRow().remove("_ATObj_");
	}
	/**
	 设置参数

	 param key
	 param obj
	 * @
	*/
	public final void SetPara(String key, String obj)  {
		if (getatPara() != null)
		{
			this.getRow().remove("_ATObj_");
		}

		String atParaStr = this.GetValStringByKey("AtPara");
		if (atParaStr.contains("@" + key + "=") == false)
		{
			atParaStr += "@" + key + "=" + obj;
			this.SetValByKey("AtPara", atParaStr);
		}
		else
		{
			AtPara at = new AtPara(atParaStr);
			at.SetVal(key, obj);
			this.SetValByKey("AtPara", at.GenerAtParaStrs());
		}
	}
	public final void SetPara(String key, int obj)  {
		SetPara(key, String.valueOf(obj));
	}
	public final void SetPara(String key, float obj) {
		SetPara(key, String.valueOf(obj));
	}
	public final void SetPara(String key, boolean obj) {
		if (obj == false)
		{
			SetPara(key, "0");
		}
		else
		{
			SetPara(key, "1");
		}
	}

		///


		///通用方法
	/**
	 获取实体

	 param key
	 * @
	*/
	public final Object GetRefObject(String key) {
		return this.getRow().get("_" + key);
		//object obj = this.Row[key];
		//if (obj == null)
		//{
		//    if (this.getRow().containsKey(key) == false)
		//        return null;
		//    obj = this.Row[key];
		//}
		//return obj;
	}
	/**
	 设置实体

	 param key
	 param obj
	 * @
	*/
	public final void SetRefObject(String key, Object obj)  {
		if (obj == null || obj.equals("")  )
		{
			this.getRow().put("_" + key, "");
			return;
		}
		this.getRow().put("_" + key, "");

		/*if (obj.getClass() == Enum.class)
		{
			this.getRow().put("_" + key, ((Integer) obj).intValue());
		} else
		{
			this.getRow().put("_" + key, obj);
		}
		this.getRow().put("_" + key, obj);*/
	}
	/**
	 在插入之前要做的工作。

	 @return
	 * @
	*/
	protected boolean beforeInsert() throws Exception{
		return true;
	}
	protected boolean roll()
	{
		return true;
	}
	public void InsertWithOutPara() throws Exception
	{
		this.RunSQL(SqlBuilder.Insert(this));
	}
	/**
	 Insert .
	 * @throws Exception
	*/
	public int Insert() throws Exception {
		if (!this.beforeInsert())
		{
			return 0;
		}

		if (!this.beforeUpdateInsertAction())
		{
			return 0;
		}

		int i = 0;
		try
		{
			i = this.DirectInsert();
		}
		catch (RuntimeException ex)
		{
			this.CheckPhysicsTable();
			if(ex.getMessage().contains("Parameter not found")){
				this.setSQLCache(null);
				return this.Insert();
			}

			//执行字段扩充检查.
			boolean isCheck = CheckPhysicsTableAutoExtFieldLength(ex);
			if (isCheck)
			{
				return this.Insert();
			}


			throw ex;
		}

		// 开始更新内存数据。 @wangyanyan
		if (this.getEnMap().getDepositaryOfEntity() == Depositary.Application)
		{
			Cache2019.PutRow(this.toString(), this.getPKVal().toString(), this.getRow());
		}

		this.afterInsert();
		this.afterInsertUpdateAction();

		return i;
	}
	protected void afterInsert() throws Exception
	{
		//added by liuxc,2016-02-19,新建时，新增一个版本记录
		/*if (this.getEnMap().ItIsEnableVer)
		{
			//增加版本为1的版本历史记录
			String enName = this.toString();
			String rdt = bp.da.DataType.getCurrentDataTime();

			//edited by liuxc,2017-03-24,增加判断，如果相同主键的数据曾被删除掉，再次被增加时，会延续被删除时的版本，原有逻辑报错
			EnVer ver = new EnVer();
			ver.setMyPK(enName + "_" + this.getPKVal());

			if (ver.RetrieveFromDBSources() == 0)
			{
				ver.setFrmID(enName);
				ver.setPKValue(this.getPKVal().toString());
				ver.setName(this.getEnMap().getEnDesc());
			}
			else
			{
			}

			ver.setRDT(rdt);
			ver.setRecNo(WebUser.getName());
			ver.Save();

			// 保存字段数据.
			Attrs attrs = this.getEnMap().getAttrs();
			for (Attr attr : attrs)
			{
				if (attr.getItIsRefAttr())
				{
					continue;
				}
				*//*EnVerDtl dtl = new EnVerDtl();
				dtl.setEnVerPK(ver.getMyPK());
				dtl.setEnVer(ver.getEVer());
				dtl.setEnName(ver.getNo());
				dtl.setAttrKey(attr.getKey());
				dtl.setAttrName(attr.getDesc());
				//dtl.OldVal = this.GetValStrByKey(attr.getKey());   //第一个版本时，旧值没有
				dtl.setRDT(rdt);
				dtl.setRec(WebUser.getName());
				dtl.setNewVal(this.GetValStrByKey(attr.getKey()));
				dtl.setMyPK(ver.getMyPK() + "_" + attr.getKey() + "_" + dtl.getEnVer());
				dtl.Insert();*//*
			}
		}*/

		return;
	}
	/**
	 在更新与插入之后要做的工作.
	 * @throws Exception
	*/
	protected void afterInsertUpdateAction() throws Exception
	{
		if (this.getEnMap().getHisFKEnumAttrs().size() > 0)
		{
			this.RetrieveFromDBSources();
		}

		if (this.getEnMap().IsAddRefName)
		{
			this.ReSetNameAttrVal();
			this.DirectUpdate();
		}
		return;
	}
	/**
	 从一个副本上copy.
	 用于两个数性基本相近的 实体 copy.

	 param fromEn
	 * @throws Exception
	*/
	public void Copy(Entity fromEn) throws Exception
	{
		for (Attr attr : this.getEnMap().getAttrs())
		{
			Object obj = fromEn.GetValByKey(attr.getKey());
			if (obj == null)
			{
				continue;
			}

			this.SetValByKey(attr.getKey(), obj);
		}
	}
	/**
	 从一个副本上

	 param fromRow
	 * @throws Exception
	*/
	public void Copy(Row fromRow) throws Exception
	{
		Attrs attrs = this.getEnMap().getAttrs();
		for (Attr attr : attrs)
		{
			try
			{
				this.SetValByKey(attr.getKey(), fromRow.GetValByKey(attr.getKey()));
			}
			catch (java.lang.Exception e)
			{
			}
		}
	}
	public void Copy(XmlEn xmlen) throws Exception
	{
		Attrs attrs = this.getEnMap().getAttrs();
		for (Attr attr : attrs)
		{
			Object obj = null;
			try
			{
				obj = xmlen.GetValByKey(attr.getKey());
			}
			catch (java.lang.Exception e)
			{
				continue;
			}

			if (obj == null || obj.toString().equals(""))
			{
				continue;
			}
			this.SetValByKey(attr.getKey(), xmlen.GetValByKey(attr.getKey()));
		}
	}
	/**
	 复制 Hashtable

	 param ht
	 * @throws Exception
	*/
	public void Copy(Hashtable ht) throws Exception
	{
		for (Object k : ht.keySet())
		{
			Object obj = null;
			try
			{
				obj = ht.get(k);
			}
			catch (java.lang.Exception e)
			{
				continue;
			}

			if (obj == null || obj.toString().equals(""))
			{
				continue;
			}
			this.SetValByKey(k.toString(), obj);
		}
	}
	public void Copy(DataRow dr) throws Exception
	{
		for (Attr attr : this.getEnMap().getAttrs())
		{
			try
			{
				this.SetValByKey(attr.getKey(), dr.getValue(attr.getKey()));
			}
			catch (java.lang.Exception e)
			{
			}
		}
	}
	public final String Copy(String refDoc) throws Exception
	{
		for (Attr attr : this.getEnMap().getAttrs())
		{
			refDoc = refDoc.replace("@" + attr.getKey(), this.GetValStrByKey(attr.getKey()));
		}
		return refDoc;
	}


	public final void Copy() throws Exception
	{
		for (Attr attr : this.getEnMap().getAttrs())
		{
			if (attr.getItIsPK() == false)
			{
				continue;
			}

			if (attr.getMyDataType() == DataType.AppInt)
			{
				this.SetValByKey(attr.getKey(), 0);
			}
			else
			{
				this.SetValByKey(attr.getKey(), "");
			}
		}

		try
		{
			this.SetValByKey("No", "");
		}
		catch (java.lang.Exception e)
		{
		}
	}

	/**
	 校验数据

	 @return
	*/
	public final boolean verifyData()throws Exception
	{
		return true;

	}



		///更新，插入之前的工作。
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		switch (this.getEnMap().getEnType())
		{
			case View:
			case XML:
			case Ext:
				return false;
			default:
				break;
		}

		this.verifyData();
		return true;
	}

	/**
	 更新
	*/

	/** @return
	 * @throws Exception
	*/
	public int Update() throws Exception
	{
		return this.Update(null);
	}
	/**
	 仅仅更新一个属性

	 param key1 key1
	 param val1 val1
	 @return 更新的个数
	 * @throws Exception
	*/
	public final int Update(String key1, Object val1) throws Exception {
		this.SetValByKey(key1, val1);

		String sql = "";

		if (val1.getClass() == Integer.class || val1.getClass() == Float.class || val1.getClass() == Long.class || val1.getClass() == BigDecimal.class)
		{
			sql = "UPDATE " + this.getEnMap().getPhysicsTable() + " SET " + key1 + " =" + val1 + " WHERE " + this.getPK() + "='" + this.getPKVal() + "'";
		}
		if (val1.getClass() == String.class)
		{
			sql = "UPDATE " + this.getEnMap().getPhysicsTable() + " SET " + key1 + " ='" + val1 + "' WHERE " + this.getPK() + "='" + this.getPKVal() + "'";
		}

		return this.RunSQL(sql);
	}
	public final int Update(String key1, Object val1, String key2, Object val2) throws Exception {
		this.SetValByKey(key1, val1);
		this.SetValByKey(key2, val2);

		key1 = key1 + "," + key2;
		return this.Update(key1.split("[,]", -1));
	}
	public final int Update(String key1, Object val1, String key2, Object val2, String key3, Object val3) throws Exception {
		this.SetValByKey(key1, val1);
		this.SetValByKey(key2, val2);
		this.SetValByKey(key3, val3);

		key1 = key1 + "," + key2 + "," + key3;
		return this.Update(key1.split("[,]", -1));
	}
	public final int Update(String key1, Object val1, String key2, Object val2, String key3, Object val3, String key4, Object val4) throws Exception {
		this.SetValByKey(key1, val1);
		this.SetValByKey(key2, val2);
		this.SetValByKey(key3, val3);
		this.SetValByKey(key4, val4);
		key1 = key1 + "," + key2 + "," + key3 + "," + key4;
		return this.Update(key1.split("[,]", -1));
	}
	public final int Update(String key1, Object val1, String key2, Object val2, String key3, Object val3, String key4, Object val4, String key5, Object val5) throws Exception {
		this.SetValByKey(key1, val1);
		this.SetValByKey(key2, val2);
		this.SetValByKey(key3, val3);
		this.SetValByKey(key4, val4);
		this.SetValByKey(key5, val5);

		key1 = key1 + "," + key2 + "," + key3 + "," + key4 + "," + key5;
		return this.Update(key1.split("[,]", -1));
	}
	public final int Update(String key1, Object val1, String key2, Object val2, String key3, Object val3, String key4, Object val4, String key5, Object val5, String key6, Object val6) throws Exception {
		this.SetValByKey(key1, val1);
		this.SetValByKey(key2, val2);
		this.SetValByKey(key3, val3);
		this.SetValByKey(key4, val4);
		this.SetValByKey(key5, val5);
		this.SetValByKey(key6, val6);
		key1 = key1 + "," + key2 + "," + key3 + "," + key4 + "," + key5 + "," + key6;
		return this.Update(key1.split("[,]", -1));
	}
	protected boolean beforeUpdate() throws Exception
	{
		return true;
	}

	// 限制重试次数，防止递归
	protected ThreadLocal<Integer> GetRetryCount(){
		return ThreadLocal.withInitial(() -> 0);
	}

	/**
	 更新实体
	 * @throws Exception
	*/
	public final int Update(String[] keys) throws Exception
	{
		String str = "";
		try
		{
			str = "@更新之前出现错误 ";
			if (this.beforeUpdate() == false)
				return 0;
			str = "@更新插入之前出现错误";
			if (this.beforeUpdateInsertAction() == false)
				return 0;
			// 判断是否有参数字段.
			if (this.get_enMap().ParaFields != null)
			{
				String[] strs = this.get_enMap().ParaFields.split(",");
				for (String key : strs)
				{
					if (DataType.IsNullOrEmpty(key) == true)
						continue;
					if (this.getRow().containsKey(key) == false){
						continue;
					}
					String val = this.getRow().get(key).toString(); // as string;
					this.SetPara(key, val);
				}
			}
			str = "@更新时出现错误";
			int i = EntityDBAccess.Update(this, keys);
			str = "@更新之后出现错误";

			// 开始更新内存数据。
			switch (this.getEnMap().getDepositaryOfEntity())
			{
				case Application:
					CacheEntity.Update(this.toString(), this.getPKVal().toString(), this);
					break;
				case None:
					break;
			}
			//更新缓存. @wangyanyan
			if (this.getEnMap().getDepositaryOfEntity() == Depositary.Application)
			{
				Cache2019.UpdateRow(this.toString(), this.getPKVal().toString(), this.getRow());
			}

			this.afterUpdate();
			str = "@更新插入之后出现错误";
			this.afterInsertUpdateAction();
			return i;
		}
		catch (RuntimeException ex)
		{

			if (ex.getMessage().contains("列名") || ex.getMessage().contains("将截断字符串")
					|| ex.getMessage().contains("缺少") || ex.getMessage().contains("的值太大")
					|| ex.getMessage().contains("Parameter not found"))
			{
				if(GetRetryCount().get() >= 2)
				{
					GetRetryCount().set(0);
					throw ex;
				}
				GetRetryCount().set(GetRetryCount().get() + 1);
				/*说明字符串长度有问题.*/
				this.CheckPhysicsTable();
				if(ex.getMessage().contains("Parameter not found")==true){
					this.setSQLCache(null);
					return this.Update();
				}
				//执行字段扩充检查.
				boolean isCheck = CheckPhysicsTableAutoExtFieldLength(ex);
				if (isCheck == true)
					return this.Update();
			}

			Log.DefaultLogWriteLine(LogType.Error, ex.getMessage());
			if (SystemConfig.isDebug())
			{
				throw new RuntimeException("@[" + this.getEnDesc() + "]更新期间出现错误:" + str + ex.getMessage());
			}
			else
			{
				throw ex;
			}
		}
	}
	private int UpdateOfDebug(String[] keys) throws Exception
	{
		String str = "";
		try
		{
			str = "@在更新之前出现错误";
			if (this.beforeUpdate() == false)
			{
				return 0;
			}
			str = "@在beforeUpdateInsertAction出现错误";
			if (this.beforeUpdateInsertAction() == false)
			{
				return 0;
			}
			int i = EntityDBAccess.Update(this, keys);
			str = "@在afterUpdate出现错误";
			this.afterUpdate();
			str = "@在afterInsertUpdateAction出现错误";
			this.afterInsertUpdateAction();
			//this.UpdateMemory();
			return i;
		}
		catch (RuntimeException ex)
		{
			String msg = "@[" + this.getEnDesc() + "]UpdateOfDebug更新期间出现错误:" + str + ex.getMessage();
			Log.DefaultLogWriteLine(LogType.Error, msg);

			if (SystemConfig.isDebug())
			{
				throw new RuntimeException(msg);
			}
			else
			{
				throw ex;
			}
		}
	}

	protected void afterUpdate() throws Exception
	{
		if (this.getEnMap().ItIsEnableVer)
		{
			/*处理版本号管理.*/
			// 取出来原来最后的版本数据.

			String enName = this.toString();
			String rdt = DataType.getCurrentDataTime();

			EnVer ver = new EnVer();
			ver.Retrieve(EnVerAttr.MyPK, enName + "_" + this.getPKVal());

			EnVerDtl dtl = null;
			EnVerDtl dtlTemp = null;

			if (ver.RetrieveFromDBSources() == 0)
			{

				return;
			}


		}
		return;
	}


		///对文件的处理.
	public final void SaveBigTxtToDB(String saveToField, String bigTxt) throws Exception {

		DBAccess.SaveBigTextToDB(bigTxt, this.getEnMap().getPhysicsTable(), this.getPK(), this.getPKVal().toString(), saveToField);

	}
	/**
	 保存文件到数据库

	 param saveToField 要保存的字段
	 param bytesOfFile 文件流
	 * @throws Exception
	*/
	public final void SaveFileToDB(String saveToField, byte[] bytesOfFile) throws Exception
	{
		try
		{
			DBAccess.SaveBytesToDB(bytesOfFile,this.getEnMap().getPhysicsTable(), this.getPK(), this.getPKVal().toString(), saveToField);
		}
		catch (RuntimeException ex)
		{
			/* 为了防止任何可能出现的数据丢失问题，您应该先仔细检查此脚本，然后再在数据库设计器的上下文之外运行此脚本。*/
			String sql = "";
			if (DBAccess.getAppCenterDBType() == DBType.MSSQL)
			{
				sql = "ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + saveToField + " Image NULL ";
			}

			if (DBAccess.getAppCenterDBType() == DBType.Oracle
					|| SystemConfig.getAppCenterDBType() == DBType.DM
					|| SystemConfig.getAppCenterDBType() == bp.da.DBType.KingBaseR3
					|| SystemConfig.getAppCenterDBType() == bp.da.DBType.KingBaseR6
					|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
			{
				sql = "ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + saveToField + " Image NULL ";
			}

			if (DBAccess.getAppCenterDBType() == DBType.MySQL || DBAccess.getAppCenterDBType() == DBType.GBASE8CByMySQL || DBAccess.getAppCenterDBType() == DBType.GBASE8A)
			{
				sql = "ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + saveToField + " Image NULL ";
			}

			DBAccess.RunSQL(sql);

			throw new RuntimeException("@保存文件期间出现错误，有可能该字段没有被自动创建，现在已经执行创建修复数据表，请重新执行一次." + ex.getMessage());
		}
	}

	/**
	 保存文件到数据库

	 param saveToField 要保存的字段
	 param fileFullName 文件路径
	 * @throws Exception
	*/
	public final void SaveFileToDB(String saveToField, String fileFullName) throws Exception
	{
		try
		{
			DBAccess.SaveFileToDB(fileFullName, this.getEnMap().getPhysicsTable(), this.getPK(), this.getPKVal().toString(), saveToField);
		}
		catch (RuntimeException ex)
		{
			/* 为了防止任何可能出现的数据丢失问题，您应该先仔细检查此脚本，然后再在数据库设计器的上下文之外运行此脚本。*/
			String sql = "";
			if (DBAccess.getAppCenterDBType() == DBType.MSSQL)
			{
				sql = "ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + saveToField + " Image NULL ";
			}

			if (DBAccess.getAppCenterDBType() == DBType.Oracle
					|| SystemConfig.getAppCenterDBType() == DBType.DM
					|| SystemConfig.getAppCenterDBType() == bp.da.DBType.KingBaseR3
					|| SystemConfig.getAppCenterDBType() == bp.da.DBType.KingBaseR6
					|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
			{
				sql = "ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + saveToField + " Blob NULL ";
			}

			if (DBAccess.getAppCenterDBType() == DBType.MySQL || DBAccess.getAppCenterDBType() == DBType.GBASE8CByMySQL  || DBAccess.getAppCenterDBType() == DBType.GBASE8A)
			{
				sql = "ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + saveToField + " MediumBlob NULL ";
			}

			DBAccess.RunSQL(sql);

			throw new RuntimeException("@保存文件期间出现错误，有可能该字段没有被自动创建，现在已经执行创建修复数据表，请重新执行一次." + ex.getMessage());
		}
	}
	/**
	 从表的字段里读取文件

	 param saveToField 字段
	 param filefullName 文件路径,如果为空怎不保存直接返回文件流，如果不为空则创建文件。
	 @return 返回文件流
	 * @throws Exception
	*/
	public final byte[] GetFileFromDB(String saveToField, String filefullName) throws Exception
	{
		return DBAccess.GetByteFromDB(this.getEnMap().getPhysicsTable(), this.getPK(), this.getPKVal().toString(), saveToField);
	}
	/**
	 从表的字段里读取string

	 param imgFieldName 字段名
	 @return 大文本数据
	 * @throws Exception
	*/
	public final String GetBigTextFromDB(String imgFieldName) throws Exception {
		return DBAccess.GetBigTextFromDB(this.getEnMap().getPhysicsTable(), this.getPK(), this.getPKVal().toString(), imgFieldName);
	}

		/// 对文件的处理.

	/**
	 执行保存

	 @return
	 * @throws Exception
	*/
	public int Save() throws Exception
	{
		switch (this.getPK())
		{
			case "OID":
				if (this.GetValIntByKey("OID") == 0)
				{
					this.Insert();
					return 1;
				}
				else
				{
					this.Update();
					return 1;
				}
			case "MyPK":
			case "No":
			case "ID":
				//自动生成的MYPK，插入前获取主键
				this.beforeUpdateInsertAction();
				String pk = this.GetValStrByKey(this.getPK());

				if (pk.equals("") || pk == null)
				{
					this.Insert();
					return 1;
				}
				else
				{
					int i = this.Update();
					if (i == 0)
					{
						this.Insert();
						i = 1;
					}
					return i;
				}
			default:
				if (this.Update() == 0)
				{
					this.Insert();
				}
				return 1;
		}
	}

		///


		///关于数据库的处理
	/**
	 检查是否是日期
	 * @throws Exception
	*/
	protected final void CheckDateAttr() throws Exception
	{
		Attrs attrs = this.getEnMap().getAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getMyDataType() == DataType.AppDate || attr.getMyDataType() == DataType.AppDateTime)
			{
				Date dt = this.GetValDate(attr.getKey());
			}
		}
	}
	/**
	 创建物理表
	 * @throws Exception
	*/
	protected final void CreatePhysicsTable() throws Exception
	{
		if (this.getEnMap().getEnDBUrl().getDBUrlType() == DBUrlType.AppCenterDSN)
		{
			String sql = "";
			switch (DBAccess.getAppCenterDBType())
			{
				case Oracle:
				case KingBaseR3:
				case KingBaseR6:
				case DM:
				case GBASE8CByOracle:
					sql = SqlBuilder.GenerCreateTableSQLOfOra(this);
					break;
				case Informix:
					sql = SqlBuilder.GenerCreateTableSQLOfInfoMix(this);
					break;
				case PostgreSQL:
				case HGDB:
					sql = SqlBuilder.GenerCreateTableSQLOfPostgreSQL(this);
					break;
				case MSSQL:
					sql = SqlBuilder.GenerCreateTableSQLOfMS(this);
					break;
				case MySQL:
				case GBASE8CByMySQL:
				case GBASE8A:
					sql = SqlBuilder.GenerCreateTableSQLOfMySQL(this);
					break;
				case Access:
					sql = SqlBuilder.GenerCreateTableSQLOf_OLE(this);
					break;
				default:
					throw new RuntimeException("@未判断的数据库类型。");
			}
			if (SystemConfig.getAppCenterDBType() == DBType.Oracle){
				sql = sql.replace("5000","4000");
			}
			DBAccess.RunSQL(sql);
			this.CreateIndexAndPK();
			return;
		}

	}
	private void CreateIndexAndPK() throws Exception
	{
		///建立主键
		if (DBAccess.IsExitsTabPK(this.getEnMap().getPhysicsTable()) == false)
		{
			int pkconut = this.getPKCount();
			if (pkconut == 1)
			{
				DBAccess.CreatePK(this.getEnMap().getPhysicsTable(), this.getPKField(), this.getEnMap().getEnDBUrl().getDBType());
				DBAccess.CreatIndex(this.getEnMap().getPhysicsTable(), this.getPKField(),this.getEnMap().getEnDBUrl().getDBType());
			}
			else if (pkconut == 2)
			{

				String pk0 = this.getPKs()[0];
				String pk1 = this.getPKs()[1];
				DBAccess.CreatePK(this.getEnMap().getPhysicsTable(), pk0, pk1, this.getEnMap().getEnDBUrl().getDBType());
				DBAccess.CreatIndex(this.getEnMap().getPhysicsTable(), pk0, pk1);
			}
			else if (pkconut == 3)
			{
				String pk0 = this.getPKs()[0];
				String pk1 = this.getPKs()[1];
				String pk2 = this.getPKs()[2];
				DBAccess.CreatePK(this.getEnMap().getPhysicsTable(), pk0, pk1, pk2, this.getEnMap().getEnDBUrl().getDBType());
				DBAccess.CreatIndex(this.getEnMap().getPhysicsTable(), pk0, pk1, pk2);
			}
		}


	}
	/**
	 如果一个属性是外键，并且它还有一个字段存储它的名称。
	 设置这个外键名称的属性。
	 * @throws Exception
	*/
	protected final void ReSetNameAttrVal() throws Exception
	{
		Attrs attrs = this.getEnMap().getAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getItIsFKorEnum() == false)
			{
				continue;
			}

			String s = this.GetValRefTextByKey(attr.getKey());
			this.SetValByKey(attr.getKey() + "Name", s);
		}
	}
	private void CheckPhysicsTable_SQL() throws Exception
	{
		String table = this.getEnMap().getPhysicsTable();
		DBType dbtype = this.getEnMap().getEnDBUrl().getDBType();
		String sqlFields = "";
		String sqlYueShu = "";


		DataTable dtAttr = DBAccess.RunSQLReturnTable(DBAccess.SQLOfTableFieldDesc(table));
		DataTable dtYueShu = DBAccess.RunSQLReturnTable(DBAccess.SQLOfTableFieldYueShu(table));


			///修复表字段。
		Attrs attrs = this.getEnMap().getAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getItIsRefAttr() || attr.getItIsPK())
			{
				continue;
			}

			String FType = "";
			String Flen = "";


				///判断是否存在.
			boolean isHave = false;
			for (DataRow dr : dtAttr.Rows)
			{
				if (dr.getValue("FName").toString().toLowerCase().equals(attr.getField().toLowerCase()))
				{
					isHave = true;
					FType = dr.getValue("FType") instanceof String ? (String)dr.getValue("FType") : null;
					Flen = dr.getValue("FLen").toString();
					break;
				}
			}
			if (isHave == false)
			{
				/*不存在此列 , 就增加此列。*/
				switch (attr.getMyDataType())
				{
					case DataType.AppString:
					case DataType.AppDate:
					case DataType.AppDateTime:
						int len = attr.getMaxLength();
						if (len == 0)
						{
							len = 200;
						}
						//throw new Exception("属性的最小长度不能为0。");
						if (len > 4000)
						{
							if (dbtype == DBType.Access && len >= 254)
							{
								DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + "  Memo DEFAULT '" + attr.getDefaultVal() + "' NULL");
							}
							else
							{
								DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " NVARCHAR(MAX) DEFAULT '" + attr.getDefaultVal() + "' NULL");
							}
						}
						else
						{
							if (dbtype == DBType.Access && len >= 254)
							{
								DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + "  Memo DEFAULT '" + attr.getDefaultVal() + "' NULL");
							}
							else
							{
								DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " NVARCHAR(" + len + ") DEFAULT '" + attr.getDefaultVal() + "' NULL");
							}
						}
						continue;
					case DataType.AppInt:
						if(attr.getDefaultVal() == null)
							DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT NULL");
						else
							DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT '" + attr.getDefaultVal() + "' NULL");
						continue;
					case DataType.AppBoolean:
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT '" + attr.getDefaultVal() + "' NULL");
						continue;
					case DataType.AppFloat:
					case DataType.AppMoney:
					case DataType.AppDouble:
						if(attr.getDefaultVal() == null)
							DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " FLOAT DEFAULT  NULL");
						else
							DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " FLOAT DEFAULT '" + attr.getDefaultVal() + "' NULL");
						continue;
					default:
						throw new RuntimeException("error MyFieldType= " + attr.getMyFieldType() + " key=" + attr.getKey());
				}
			}

				///


				///检查类型是否匹配.
			switch (attr.getMyDataType())
			{
				case DataType.AppString:
				case DataType.AppDate:
				case DataType.AppDateTime:
					if (FType.toLowerCase().contains("char"))
					{
						/*类型正确，检查长度*/
						if (attr.getItIsPK())
						{
							continue;
						}

						if (Flen == null)
						{
							throw new RuntimeException("" + attr.getKey() + " -" + sqlFields);
						}
						int len = Integer.parseInt(Flen);


						if (len == -1)
						{
							continue; //有可能是 nvarchar(MAX)
						}

						if (len < attr.getMaxLength())
						{
							try
							{
								DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ALTER column " + attr.getKey() + " VARCHAR(" + attr.getMaxLength() + ")");
							}
							catch (java.lang.Exception e)
							{
								/*如果类型不匹配，就删除它在重新建, 先删除约束，在删除列，在重建。*/
								for (DataRow dr : dtYueShu.Rows)
								{
									if (dr.getValue("FName").toString().toLowerCase().equals(attr.getKey().toLowerCase()))
									{
										DBAccess.RunSQL("ALTER TABLE " + table + " drop constraint " + dr.getValue(0).toString());
									}
								}

								// 在执行一遍.
								if (attr.getMaxLength() >= 4000)
								{
									DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ALTER column " + attr.getKey() + " VARCHAR(4000)");
								}
								else
								{
									DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ALTER column " + attr.getKey() + " VARCHAR(" + attr.getMaxLength() + ")");
								}
							}
						}
					}
					else
					{
						String err = "err@字段类型不匹配,表[" + this.getEnMap().getPhysicsTable() + "]字段[" + attr.getKey() + "]名称[" + attr.getDesc() + "]映射类型为[" + attr.getMyDataTypeStr() + "],数据类型为[" + FType + "]";
						bp.da.Log.DebugWriteWarning(err);

						// throw new Exception();

						/***如果类型不匹配，就删除它在重新建, 先删除约束，在删除列，在重建。*/
						//foreach (DataRow dr in dtYueShu.Rows)
						//{
						//    if (dr["FName").toString().ToLower() == attr.getKey().ToLower())
						//        DBAccess.RunSQL("ALTER TABLE " + table + " drop constraint " + dr[0).toString());
						//}

						//DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " drop column " + attr.Field);
						//DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " NVARCHAR(" + attr.getMaxLength() + ") DEFAULT '" + attr.getDefaultVal() + "' NULL");
						continue;
					}
					break;
				case DataType.AppInt:
				case DataType.AppBoolean:
					if (FType.contains("int") == false)
					{
						String err = "err@字段类型不匹配,表[" + this.getEnMap().getPhysicsTable() + "]字段[" + attr.getKey() + "]名称[" + attr.getDesc() + "]映射类型为[" + attr.getMyDataTypeStr() + "],数据类型为[" + FType + "]";
						bp.da.Log.DebugWriteWarning(err);
						/***如果类型不匹配，就删除它在重新建, 先删除约束，在删除列，在重建。*/
						//foreach (DataRow dr in dtYueShu.Rows)
						//{
						//    if (dr["FName").toString().ToLower() == attr.getKey().ToLower())
						//        DBAccess.RunSQL("alter table " + table + " drop constraint " + dr[0).toString());
						//}
						//DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " drop column " + attr.Field);
						//DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT '" + attr.getDefaultVal() + "' NULL");
						//continue;
					}
					break;
				case DataType.AppFloat:
				case DataType.AppMoney:
				case 9:
					if (FType.contains("float") == false)
					{
						String err = "err@字段类型不匹配,表[" + this.getEnMap().getPhysicsTable() + "]字段[" + attr.getKey() + "]名称[" + attr.getDesc() + "]映射类型为[" + attr.getMyDataTypeStr() + "],数据类型为[" + FType + "]";
						bp.da.Log.DebugWriteWarning(err);

						/***如果类型不匹配，就删除它在重新建, 先删除约束，在删除列，在重建。*/
						//foreach (DataRow dr in dtYueShu.Rows)
						//{
						//    if (dr["FName").toString().ToLower() == attr.getKey().ToLower())
						//        DBAccess.RunSQL("alter table " + table + " drop constraint " + dr[0).toString());
						//}
						//DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " drop column " + attr.Field);
						//DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " FLOAT DEFAULT '" + attr.getDefaultVal() + "' NULL");
						//continue;
					}
					break;
				default:
					//  throw new Exception("error MyFieldType= " + attr.getMyFieldType() + " key=" + attr.getKey());
					break;
			}

				///
		}

			/// 修复表字段。


			///检查枚举类型是否存在.
		attrs = this.getEnMap().getHisEnumAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getMyDataType() != DataType.AppInt)
			{
				continue;
			}

			if (attr.UITag == null)
			{
				continue;
			}

			try
			{
				SysEnums ses = new SysEnums(attr.getUIBindKey(), attr.UITag);
				continue;
			}
			catch (java.lang.Exception e2)
			{
			}

			try
			{
				String[] strs = attr.UITag.split("[@]", -1);
				SysEnums ens = new SysEnums();
				ens.Delete(SysEnumAttr.EnumKey, attr.getUIBindKey());
				for (String s : strs)
				{
					if (DataType.IsNullOrEmpty(s)==true)
					{
						continue;
					}

					String[] vk = s.split("[=]", -1);
					SysEnum se = new SysEnum();
					se.setIntKey(Integer.parseInt(vk[0]));
					se.setLab(vk[1]);
					se.setEnumKey(attr.getUIBindKey());
					se.Insert();
				}
			}
			catch (RuntimeException ex)
			{
				throw new RuntimeException("@自动增加枚举时出现错误，请确定您的格式是否正确。" + ex.getMessage() + "attr.getUIBindKey()=" + attr.getUIBindKey());
			}

		}

			///


			///建立主键
		if (DBAccess.IsExitsTabPK(this.getEnMap().getPhysicsTable()) == false)
		{
			int pkconut = this.getPKCount();
			if (pkconut == 1)
			{
				DBAccess.CreatePK(this.getEnMap().getPhysicsTable(), this.getPKField(), this.getEnMap().getEnDBUrl().getDBType());
				DBAccess.CreatIndex(this.getEnMap().getPhysicsTable(), this.getPKField(), this.getEnMap().getEnDBUrl().getDBType());
			}
			else if (pkconut == 2)
			{
				String pk0 = this.getPKs()[0];
				String pk1 = this.getPKs()[1];
				DBAccess.CreatePK(this.getEnMap().getPhysicsTable(), pk0, pk1, this.getEnMap().getEnDBUrl().getDBType());
				DBAccess.CreatIndex(this.getEnMap().getPhysicsTable(), pk0, pk1);
			}
			else if (pkconut == 3)
			{
				String pk0 = this.getPKs()[0];
				String pk1 = this.getPKs()[1];
				String pk2 = this.getPKs()[2];
				DBAccess.CreatePK(this.getEnMap().getPhysicsTable(), pk0, pk1, pk2, this.getEnMap().getEnDBUrl().getDBType());
				DBAccess.CreatIndex(this.getEnMap().getPhysicsTable(), pk0, pk1, pk2);
			}
		}

			///


			///重命名表名字段名.

		String ptable = this.getEnMap().getPhysicsTable();

		String sql = "exec sp_rename '" + this.getEnMap().getPhysicsTable() + "','" + this.getEnMap().getPhysicsTable() + "'";
		DBAccess.RunSQL(sql);

		for (Attr item : this.getEnMap().getAttrs())
		{
			if (item.getItIsRefAttr() == true)
			{
				continue;
			}

			boolean isHave = false;
			for (DataRow dr : dtAttr.Rows)
			{
				String fName = dr.getValue("FName").toString();
				if (fName.equals(item.getKey()) == true)
				{
					isHave = true;
					break;
				}
			}

			//如果字段有大小写的变化,就修正过来.
			if (isHave == false)
			{
				try{
					sql = "exec sp_rename '" + ptable + ".[" + item.getKey() + "]','" + item.getKey() + "','column';";
					DBAccess.RunSQL(sql);
				}catch(Exception e){

				}

			}

		}

			/// 重命名表名字段名.

	}
	/**
	 PostgreSQL 检查.
	 * @throws Exception
	*/
	private void CheckPhysicsTable_PostgreSQL() throws Exception
	{
		String table = this.getEnMap().getPhysicsTable();
		DBType dbtype = this.getEnMap().getEnDBUrl().getDBType();
		String sqlFields = "";
		String sqlYueShu = "";

		//字段信息: 名称fname, 类型ftype, 长度flen.
		sqlFields = "SELECT a.attname as fname, format_type(a.atttypid,a.atttypmod) as type,  0 as FLen, 'xxxxxxx' as FType,";
		sqlFields += " a.attnotnull as notnull FROM pg_class as c,pg_attribute as a  ";
		sqlFields += " where c.relname = '" + this.getEnMap().getPhysicsTable().toLowerCase() + "' and a.attrelid = c.oid and a.attnum>0  ";

		//约束信息.
		//sqlYueShu = "SELECT b.name, a.name FName from sysobjects b join syscolumns a on b.id = a.cdefault where a.id = object_id('" + this.getEnMap().getPhysicsTable() + "') ";

		DataTable dtAttr = DBAccess.RunSQLReturnTable(sqlFields);

		for (DataRow dr : dtAttr.Rows)
		{
			String type = dr.getValue("type").toString();
			if (type.contains("char"))
			{
				dr.setValue("ftype", "char");

				int start = type.indexOf('(') + 1;
				int end = type.indexOf(')');
				String len = type.substring(start, end);
				dr.setValue("flen", Integer.parseInt(len));
			}
			else
			{
				dr.setValue("ftype", type);
			}

		}


			///修复表字段。
		Attrs attrs = this.getEnMap().getAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getItIsRefAttr()  || attr.getItIsPK())
			{
				continue;
			}

			String FType = "";
			String Flen = "";


				///判断是否存在.
			boolean isHave = false;
			for (DataRow dr : dtAttr.Rows)
			{
				if (dr.getValue("FName").toString().toLowerCase().equals(attr.getField().toLowerCase()))
				{
					isHave = true;
					FType = dr.getValue("FType") instanceof String ? (String)dr.getValue("FType") : null;
					Flen = dr.getValue("FLen").toString();
					break;
				}
			}
			if (isHave == false)
			{
				/*不存在此列 , 就增加此列。*/
				switch (attr.getMyDataType())
				{
					case DataType.AppString:
					case DataType.AppDate:
					case DataType.AppDateTime:
						int len = attr.getMaxLength();
						if (len == 0)
						{
							len = 200;
						}
						if (len > 4000)
						{
							if (dbtype == DBType.Access && len >= 254)
							{
								DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + "  Memo DEFAULT '" + attr.getDefaultVal() + "' NULL");
							}
							else
							{
								DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " VARCHAR(" + len + ") DEFAULT '" + attr.getDefaultVal() + "' NULL");
							}
						}
						else
						{
							if (dbtype == DBType.Access && len >= 254)
							{
								DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + "  Memo DEFAULT '" + attr.getDefaultVal() + "' NULL");
							}
							else
							{
								DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " VARCHAR(" + len + ") DEFAULT '" + attr.getDefaultVal() + "' NULL");
							}
						}
						continue;
					case DataType.AppInt:
						if(attr.getDefaultVal() == null)
							DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT  NULL");
						else
							DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT '" + attr.getDefaultVal() + "' NULL");
						continue;
					case DataType.AppBoolean:
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT '" + attr.getDefaultVal() + "' NULL");
						continue;
					case DataType.AppFloat:
					case DataType.AppMoney:
					case DataType.AppDouble:
						if(attr.getDefaultVal() == null)
							DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " FLOAT DEFAULT  NULL");
						else
							DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " FLOAT DEFAULT '" + attr.getDefaultVal() + "' NULL");
						continue;
					default:
						throw new RuntimeException("err@MyFieldType= " + attr.getMyFieldType() + " key=" + attr.getKey());
				}
			}



				///检查类型是否匹配.
			switch (attr.getMyDataType())
			{
				case DataType.AppString:
				case DataType.AppDate:
				case DataType.AppDateTime:
					if (FType.toLowerCase().contains("char"))
					{
						/*类型正确，检查长度*/
						if (attr.getItIsPK())
						{
							continue;
						}

						if (Flen == null)
						{
							throw new RuntimeException("" + attr.getKey() + " -" + sqlFields);
						}
						int len = Integer.parseInt(Flen);


						if (len == -1)
						{
							continue; //有可能是 nvarchar(MAX)
						}

						if (len < attr.getMaxLength())
						{
							try
							{
								DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ALTER column " + attr.getKey() + " type character varying(" + attr.getMaxLength() + ")");
							}
							catch (java.lang.Exception e)
							{
								/*如果类型不匹配，就删除它在重新建, 先删除约束，在删除列，在重建。*/
								//foreach (DataRow dr in dtYueShu.Rows)
								//{
								//    if (dr["FName").toString().ToLower() == attr.getKey().ToLower())
								//        DBAccess.RunSQL("ALTER TABLE " + table + " drop constraint " + dr[0).toString());
								//}

								DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ALTER column " + attr.getKey() + " type character varying(" + attr.getMaxLength() + ")");
							}
						}
					}
					else if (FType.toLowerCase().contains("text"))
					{

					}
					else
					{
						String err = "err@字段类型不匹配,表[" + this.getEnMap().getPhysicsTable() + "]字段[" + attr.getKey() + "]名称[" + attr.getDesc() + "]映射类型为[" + attr.getMyDataTypeStr() + "],数据类型为[" + FType + "]";
						bp.da.Log.DebugWriteWarning(err);

						// throw new Exception();

						/***如果类型不匹配，就删除它在重新建, 先删除约束，在删除列，在重建。*/
						String atlerSql="ALTER TABLE "+this.getEnMap().getPhysicsTable()+" ALTER COLUMN "+attr.getField()+"  TYPE varchar("+attr.getMaxLength()+");";
						String alterSqlDefault="ALTER TABLE "+this.getEnMap().getPhysicsTable()+" ALTER COLUMN "+attr.getField()+" set DEFAULT '" + attr.getDefaultVal() + "'";
						DBAccess.RunSQL(atlerSql+alterSqlDefault);
						continue;
					}
					break;
				case DataType.AppInt:
				case DataType.AppBoolean:

					break;
				case DataType.AppFloat:
				case DataType.AppMoney:
				case 9:

					break;
				default:
					//  throw new Exception("error MyFieldType= " + attr.getMyFieldType() + " key=" + attr.getKey());
					break;
			}

		}

			/// 修复表字段。


			///检查枚举类型是否存在.
		attrs = this.getEnMap().getHisEnumAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getMyDataType() != DataType.AppInt)
			{
				continue;
			}

			if (attr.UITag == null)
			{
				continue;
			}

			try
			{
				SysEnums ses = new SysEnums(attr.getUIBindKey(), attr.UITag);
				continue;
			}
			catch (java.lang.Exception e2)
			{
			}

			try
			{
				String[] strs = attr.UITag.split("[@]", -1);
				SysEnums ens = new SysEnums();
				ens.Delete(SysEnumAttr.EnumKey, attr.getUIBindKey());
				for (String s : strs)
				{
					if (DataType.IsNullOrEmpty(s)==true)
					{
						continue;
					}

					String[] vk = s.split("[=]", -1);
					SysEnum se = new SysEnum();
					se.setIntKey(Integer.parseInt(vk[0]));
					se.setLab(vk[1]);
					se.setEnumKey(attr.getUIBindKey());
					se.Insert();
				}
			}
			catch (RuntimeException ex)
			{
				throw new RuntimeException("@自动增加枚举时出现错误，请确定您的格式是否正确。" + ex.getMessage() + "attr.getUIBindKey()=" + attr.getUIBindKey());
			}

		}



			///建立主键
		if (DBAccess.IsExitsTabPK(this.getEnMap().getPhysicsTable()) == false)
		{
			int pkconut = this.getPKCount();
			if (pkconut == 1)
			{
				DBAccess.CreatePK(this.getEnMap().getPhysicsTable(), this.getPKField(), this.getEnMap().getEnDBUrl().getDBType());
				DBAccess.CreatIndex(this.getEnMap().getPhysicsTable(), this.getPKField(), this.getEnMap().getEnDBUrl().getDBType());
			}
			else if (pkconut == 2)
			{
				String pk0 = this.getPKs()[0];
				String pk1 = this.getPKs()[1];
				DBAccess.CreatePK(this.getEnMap().getPhysicsTable(), pk0, pk1, this.getEnMap().getEnDBUrl().getDBType());
				DBAccess.CreatIndex(this.getEnMap().getPhysicsTable(), pk0, pk1);
			}
			else if (pkconut == 3)
			{
				String pk0 = this.getPKs()[0];
				String pk1 = this.getPKs()[1];
				String pk2 = this.getPKs()[2];
				DBAccess.CreatePK(this.getEnMap().getPhysicsTable(), pk0, pk1, pk2, this.getEnMap().getEnDBUrl().getDBType());
				DBAccess.CreatIndex(this.getEnMap().getPhysicsTable(), pk0, pk1, pk2);
			}
		}



			///创建索引.
		if (this.getEnMap().IndexField != null)
		{
			DBAccess.CreatIndex(this.getEnMap().getPhysicsTable(), this.getEnMap().IndexField, this.getEnMap().getEnDBUrl().getDBType());
		}

		int pkconut22 = this.getPKCount();
		if (pkconut22 == 1)
		{
			DBAccess.CreatIndex(this.getEnMap().getPhysicsTable(), this.getPKField(), this.getEnMap().getEnDBUrl().getDBType());
		}
		else if (pkconut22 == 2)
		{
			String pk0 = this.getPKs()[0];
			String pk1 = this.getPKs()[1];
			DBAccess.CreatIndex(this.getEnMap().getPhysicsTable(), pk0, pk1);
		}
		else if (pkconut22 == 3)
		{
			String pk0 = this.getPKs()[0];
			String pk1 = this.getPKs()[1];
			String pk2 = this.getPKs()[2];
			DBAccess.CreatIndex(this.getEnMap().getPhysicsTable(), pk0, pk1, pk2);
		}

	}
	/**
	 自动扩展长度
	 * @throws Exception
	*/
	public final boolean CheckPhysicsTableAutoExtFieldLength(RuntimeException ex) throws Exception
	{
		this.set_enMap(this.getEnMap());

		if (this.getEnMap().getEnType() == EnType.View || this.getEnMap().getEnType() == EnType.XML || this.getEnMap().getEnType() == EnType.ThirdPartApp || this.getEnMap().getEnType() == EnType.Ext)
		{
			return false;
		}



		switch (SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
				return CheckPhysicsTableAutoExtFieldLength_SQL();
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case DM:
			case GBASE8CByOracle:
				break;
			case MySQL:
			case GBASE8CByMySQL:
			case GBASE8A:
				break;
			case Informix:
				break;
			case PostgreSQL:
			case HGDB:
				break;
			default:
				throw new RuntimeException("@没有涉及到的数据库类型");
		}

		return false;
	}

	private boolean CheckPhysicsTableAutoExtFieldLength_SQL() throws Exception
	{
		String sqlFields = "SELECT column_name as FName,data_type as FType,CHARACTER_MAXIMUM_LENGTH as FLen from information_schema.columns where table_name='" + this.getEnMap().getPhysicsTable() + "'";
		//原始的
		DataTable dtAttr = DBAccess.RunSQLReturnTable(sqlFields);

		//是否有? 没有check就返回.
		boolean isCheckIt = false;

		//遍历属性.
		Attrs attrs = this.getEnMap().getAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getMyDataType() != DataType.AppString)
			{
				continue;
			}

			int dbLen = 0;
			for (DataRow dr : dtAttr.Rows)
			{
				if (dr.getValue("FName").toString().equals(attr.getKey()) == true)
				{
					dbLen = Integer.parseInt(dr.getValue("FLen").toString());
					break;
				}
			}

			//如果是负数，就是maxvarchar 的类型.
			if (dbLen <= 0)
			{
				continue;
			}

			//获得长度.
			String val = this.GetValStrByKey(attr.getKey());
			if (val.length() <= dbLen)
			{
				continue;
			}

			//字段长度.
			String sql = "";
			if (val.length() >= 4000)
			{
				sql = "ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ALTER column " + attr.getKey() + " nvarchar(MAX)";
			}
			else
			{
				sql = "ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ALTER column " + attr.getKey() + " VARCHAR(" + val.length() + ")";
			}

			try
			{
				DBAccess.RunSQL(sql);
				isCheckIt = true;
			}
			catch (RuntimeException ex)
			{
				int valNum = DBAccess.DropConstraintOfSQL(this.getEnMap().getPhysicsTable(), attr.getKey());
				if (valNum > 0)
				{
					DBAccess.RunSQL(sql);
					isCheckIt = true; //设置是否更新到.
				}
			}
		}
		//返回是否检测到。
		return isCheckIt;
	}
	/**
	 检查物理表
	 * @throws Exception
	*/
	public final void CheckPhysicsTable() throws Exception
	{
		Map enMap  = this.getEnMap();
		this.set_enMap(enMap);
		if (enMap.getEnType() == EnType.View || enMap.getEnType() == EnType.XML || enMap.getEnType() == EnType.ThirdPartApp || enMap.getEnType() == EnType.Ext)
		{
			return;
		}

		if (DBAccess.IsExitsObject(enMap.getEnDBUrl(), enMap.getPhysicsTable()) == false)
		{
			/* 如果物理表不存在就新建立一个物理表。*/
			this.CreatePhysicsTable();
			return;
		}
		if (enMap.getIsView())
		{
			return;
		}

		//检查是否有对应的主键.
		String pk = this.getPK();
		if (pk.contains(",") == false && enMap.getAttrs().contains(pk) == false)
		{
			if (this.toString().contains(".") == true)
			{
				throw new RuntimeException("err@Entity " + this.toString() + "," + enMap.getEnDesc() + "的Map设置错误主键为【" + pk + "】但是没有这个字段，请检查Map。");
			}
			else
			{
				if (pk.equals("OID") == true)
				{
					MapAttr attr = new MapAttr();
					attr.setFrmID(this.toString());
					attr.setKeyOfEn("OID");
					attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
					attr.setMyDataType(DataType.AppInt);
					attr.setEditType(EditType.UnDel);
					attr.setName("OID");
					attr.setUIVisible(false);
					attr.Insert();
				}
				else if (pk.equals("No") == true)
				{
					MapAttr attr = new MapAttr();
					attr.setFrmID(this.toString());
					attr.setKeyOfEn("No");
					attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
					attr.setMyDataType(DataType.AppString);
					attr.setEditType(EditType.UnDel);
					attr.setName("编号");
					attr.setUIVisible(false);
					attr.Insert();
				}
				else
				{
					throw new RuntimeException("err@Entity " + this.toString() + "," + enMap.getEnDesc() + "的Map设置错误主键为【" + pk + "】但是没有这个字段，请检查:  SELECT * FROM Sys_MapAttr WHERE FK_MapData='" + this.toString() + "'");
				}
			}
		}

		//需要清空一下缓存
		//Cache.DelSQL(this.toString());
		// 如果不是主应用程序的数据库就不让执行检查. 考虑第三方的系统的安全问题.
		if (enMap.getEnDBUrl().getDBUrlType() != DBUrlType.AppCenterDSN)
		{
			return;
		}
		switch (SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
				this.CheckPhysicsTable_SQL();
				break;
			case Oracle:
			case DM:
				this.CheckPhysicsTable_Ora();
				break;
			case MySQL:
			case GBASE8CByMySQL:
			case GBASE8A:
				this.CheckPhysicsTable_MySQL();
				break;
			case Informix:
				this.CheckPhysicsTable_Informix();
				break;
			case PostgreSQL:
			case HGDB:
			case GBASE8CByOracle:
				this.CheckPhysicsTable_PostgreSQL();
				break;
			case KingBaseR3:
			case KingBaseR6:
				this.CheckPhysicsTable_King();
				break;
			default:
				throw new RuntimeException("@没有涉及到的数据库类型");
		}

	}
	private void CheckPhysicsTable_Informix() throws Exception
	{

			///检查字段是否存在
		String sql = "SELECT *  FROM " + this.getEnMap().getPhysicsTable() + " WHERE 1=2";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		//如果不存在.
		for (Attr attr : this.getEnMap().getAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}

			if (attr.getItIsPK())
			{
				continue;
			}

			if (dt.Columns.contains(attr.getKey()) == true)
			{
				continue;
			}

			if (attr.getKey().equals("AID"))
			{
				/* 自动增长列 */
				DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT  Identity(1,1)");
				continue;
			}

			/*不存在此列 , 就增加此列。*/
			switch (attr.getMyDataType())
			{
				case DataType.AppString:
				case DataType.AppDate:
				case DataType.AppDateTime:
					int len = attr.getMaxLength();
					if (len == 0)
					{
						len = 200;
					}

					if (len >= 254)
					{
						DBAccess.RunSQL("alter table " + this.getEnMap().getPhysicsTable() + " add " + attr.getField() + " lvarchar(" + len + ") default '" + attr.getDefaultVal() + "'");
					}
					else
					{
						DBAccess.RunSQL("alter table " + this.getEnMap().getPhysicsTable() + " add " + attr.getField() + " varchar(" + len + ") default '" + attr.getDefaultVal() + "'");
					}
					break;
				case DataType.AppInt:
				case DataType.AppBoolean:
					DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT8 DEFAULT " + attr.getDefaultVal() + " ");
					break;
				case DataType.AppFloat:
				case DataType.AppMoney:
				case DataType.AppDouble:
					DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " FLOAT DEFAULT  " + attr.getDefaultVal() + " ");
					break;
				default:
					throw new RuntimeException("error MyFieldType= " + attr.getMyFieldType() + " key=" + attr.getKey());
			}
		}



			///检查字段长度是否符合最低要求
		for (Attr attr : this.getEnMap().getAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}
			if (attr.getMyDataType() == DataType.AppDouble || attr.getMyDataType() == DataType.AppFloat || attr.getMyDataType() == DataType.AppInt || attr.getMyDataType() == DataType.AppMoney || attr.getMyDataType() == DataType.AppBoolean)
			{
				continue;
			}

			int maxLen = attr.getMaxLength();
			dt = new DataTable();
			sql = "select c.*  from syscolumns c inner join systables t on c.tabid = t.tabid where t.tabname = lower('" + this.getEnMap().getPhysicsTable().toLowerCase() + "') and c.colname = lower('" + attr.getKey().toLowerCase() + "') and c.collength < " + attr.getMaxLength();
			dt = this.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
			{
				continue;
			}
			for (DataRow dr : dt.Rows)
			{
				try
				{
					if (attr.getMaxLength() >= 255)
					{
						this.RunSQL("alter table " + dr.getValue("owner") + "." + this.getEnMap().getPhysicsTableExt() + " modify " + attr.getField() + " lvarchar(" + attr.getMaxLength() + ")");
					}
					else
					{
						this.RunSQL("alter table " + dr.getValue("owner") + "." + this.getEnMap().getPhysicsTableExt() + " modify " + attr.getField() + " varchar(" + attr.getMaxLength() + ")");
					}
				}
				catch (RuntimeException ex)
				{
					bp.da.Log.DebugWriteWarning(ex.getMessage());
				}
			}
		}

			///


			///检查枚举类型字段是否是INT 类型
		Attrs attrs = this.getEnMap().getHisEnumAttrs();

		attrs = this.getEnMap().getHisEnumAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getMyDataType() != DataType.AppInt)
			{
				continue;
			}
			if (attr.UITag == null)
			{
				continue;
			}
			try
			{
				SysEnums ses = new SysEnums(attr.getUIBindKey(), attr.UITag);
				continue;
			}
			catch (java.lang.Exception e)
			{
			}
			String[] strs = attr.UITag.split("[@]", -1);
			SysEnums ens = new SysEnums();
			ens.Delete(SysEnumAttr.EnumKey, attr.getUIBindKey());
			for (String s : strs)
			{
				if (DataType.IsNullOrEmpty(s)==true)
				{
					continue;
				}

				String[] vk = s.split("[=]", -1);
				SysEnum se = new SysEnum();
				se.setIntKey(Integer.parseInt(vk[0]));
				se.setLab(vk[1]);
				se.setEnumKey(attr.getUIBindKey());
				se.Insert();
			}
		}

			///

		this.CreateIndexAndPK();
	}
	private void CheckPhysicsTable_MySQL() throws Exception
	{

			///检查字段是否存在
		String sql = "SELECT *  FROM " + this.getEnMap().getPhysicsTable() + " WHERE 1=2";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		//如果不存在.
		for (Attr attr : this.getEnMap().getAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}

			if (attr.getItIsPK())
			{
				continue;
			}

			if (dt.Columns.contains(attr.getKey()) == true)
			{
				continue; //不存在此列.
			}

			if (attr.getKey().equals("AID"))
			{
				/* 自动增长列 */
				DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT  Identity(1,1) COMMENT '" + attr.getDesc() + "'");
				continue;
			}

			/*不存在此列 , 就增加此列。*/
			switch (attr.getMyDataType())
			{
				case DataType.AppString:
					int len = attr.getMaxLength();
					if (len == 0)
					{
						len = 200;
					}
					if (len > 3000)
					{
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " TEXT COMMENT '" + attr.getDesc() + "'");
					}
					else
					{
						if(SystemConfig.getAppCenterDBType() == DBType.GBASE8A && (attr.getField().equals("Sort") ||attr.getField().equals("Target")))
							DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD \"" + attr.getField() + "\" NVARCHAR(" + len + ") DEFAULT '" + attr.getDefaultVal() + "' NULL COMMENT '" + attr.getDesc() + "'");
						else
							DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " NVARCHAR(" + len + ") DEFAULT '" + attr.getDefaultVal() + "' NULL COMMENT '" + attr.getDesc() + "'");
					}
					break;
				case DataType.AppDate:
				case DataType.AppDateTime:
					DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " NVARCHAR(20) DEFAULT '" + attr.getDefaultVal() + "' NULL COMMENT '" + attr.getDesc() + "'");
					break;
				case DataType.AppInt:
				case DataType.AppBoolean:
					if( attr.getDefaultVal() == null)
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT  NULL COMMENT '" + attr.getDesc() + "'");
					else
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT '" + attr.getDefaultVal() + "' NULL COMMENT '" + attr.getDesc() + "'");
					break;
				case DataType.AppFloat:
					if( attr.getDefaultVal() == null)
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " FLOAT (11,2) DEFAULT  NULL COMMENT '" + attr.getDesc() + "'");
					else
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " FLOAT (11,2) DEFAULT '" + attr.getDefaultVal() + "' NULL COMMENT '" + attr.getDesc() + "'");
					break;
				case DataType.AppMoney:
					if( attr.getDefaultVal() == null)
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " DECIMAL (20,"+attr.getDefaultDecimalLen()+") DEFAULT  NULL COMMENT '" + attr.getDesc() + "'");
					else
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " DECIMAL (20,"+attr.getDefaultDecimalLen()+") DEFAULT '" + attr.getDefaultVal() + "' NULL COMMENT '" + attr.getDesc() + "'");
					break;
				case DataType.AppDouble:
					if( attr.getDefaultVal() == null)
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " DOUBLE DEFAULT  NULL COMMENT '" + attr.getDesc() + "'");
					else
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " DOUBLE DEFAULT '" + attr.getDefaultVal() + "' NULL COMMENT '" + attr.getDesc() + "'");
					break;
				default:
					throw new RuntimeException("error MyFieldType= " + attr.getMyFieldType() + " key=" + attr.getKey());
			}
		}

		//字段类型不一致
		for (Attr attr : this.getEnMap().getAttrs()){
			if (attr.getMyFieldType() == FieldType.RefText)
				continue;
			DataColumn dc = dt.Columns.get(attr.getField());
			if(dc==null)
				continue;
			if(SystemConfig.getAppCenterDBType() == DBType.GBASE8A){
				if(attr.getMyDataType()==DataType.AppString && dc.DataType == String.class)
					continue;
				if((attr.getMyDataType()==DataType.AppInt || attr.getMyDataType()==DataType.AppBoolean) && dc.DataType == Integer.class)
					continue;
				if((attr.getMyDataType()==DataType.AppFloat || attr.getMyDataType()==DataType.AppDouble || attr.getMyDataType()==DataType.AppMoney)&& dc.DataType == Float.class)
					continue;
				//不能直接改类型，需要删除后重新创建
				//暂时不能修改类型
				continue;
			}
			//数字类型转字符类型
			if(attr.getMyDataType()==DataType.AppString) {
				if (dc.DataType == Integer.class || dc.DataType == Float.class || dc.DataType == Double.class) {
					//修改字段类型
					int len = attr.getMaxLength();
					if (len == 0) {
						len = 200;
					}
					if (len > 3000) {
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " MODIFY " + attr.getField() + " TEXT COMMENT '" + attr.getDesc() + "'");
					} else {
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " MODIFY " + attr.getField() + " NVARCHAR(" + len + ") DEFAULT '" + attr.getDefaultVal() + "' NULL COMMENT '" + attr.getDesc() + "'");
					}
					break;
				}
				continue;
			}
			//字符类型转数字
			if(attr.getMyDataType() == DataType.AppInt || attr.getMyDataType() == DataType.AppBoolean){
				if(dc.DataType != Integer.class){
					if(attr.getDefaultVal() == null)
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " MODIFY " + attr.getField() + " INT DEFAULT  NULL COMMENT '" + attr.getDesc() + "'");
					else
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " MODIFY " + attr.getField() + " INT DEFAULT '" + attr.getDefaultVal() + "' NULL COMMENT '" + attr.getDesc() + "'");
				}
				continue;
			}
			if (attr.getMyDataType() == DataType.AppFloat) {
				if(attr.getDefaultVal() == null)
					DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " MODIFY " + attr.getField() + " FLOAT (11,2) DEFAULT  NULL COMMENT '" + attr.getDesc() + "'");
				else
					DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " MODIFY " + attr.getField() + " FLOAT (11,2) DEFAULT '" + attr.getDefaultVal() + "' NULL COMMENT '" + attr.getDesc() + "'");
				continue;
			}
			if(attr.getMyDataType() == DataType.AppDouble ){
				if(dc.DataType != Float.class){
					if(attr.getDefaultVal() == null)
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " MODIFY " + attr.getField() + " DOUBLE DEFAULT  NULL COMMENT '" + attr.getDesc() + "'");
					else
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " MODIFY " + attr.getField() + " DOUBLE DEFAULT '" + attr.getDefaultVal() + "' NULL COMMENT '" + attr.getDesc() + "'");
				}
				continue;
			}
			if(attr.getMyDataType() == DataType.AppMoney ){
				if(dc.DataType != Float.class){
					if(attr.getDefaultVal() == null)
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " MODIFY " + attr.getField() + " DECIMAL (20,"+attr.getDefaultDecimalLen()+") DEFAULT  NULL COMMENT '" + attr.getDesc() + "'");
					else
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " MODIFY " + attr.getField() + " DECIMAL (20,"+attr.getDefaultDecimalLen()+") DEFAULT '" + attr.getDefaultVal() + "' NULL COMMENT '" + attr.getDesc() + "'");
				}
				continue;
			}
		}


			///检查字段长度是否符合最低要求
		for (Attr attr : this.getEnMap().getAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}
			if (attr.getMyDataType() == DataType.AppDouble || attr.getMyDataType() == DataType.AppFloat || attr.getMyDataType() == DataType.AppInt || attr.getMyDataType() == DataType.AppMoney || attr.getMyDataType() == DataType.AppBoolean)
			{
				continue;
			}

			int maxLen = attr.getMaxLength();
			dt = new DataTable();
			sql = "select character_maximum_length as Len, table_schema as OWNER FROM information_schema.columns WHERE TABLE_SCHEMA='" + SystemConfig.getAppCenterDBDatabase() + "' AND table_name ='" + this.getEnMap().getPhysicsTable() + "' and column_Name='" + attr.getField() + "' AND character_maximum_length < " + attr.getMaxLength();
			dt = this.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
			{
				continue;
			}
			for (DataRow dr : dt.Rows)
			{
				try
				{
					if (attr.getMaxLength() < 3000)
					{
						this.RunSQL("alter table " + dr.getValue("OWNER") + "." + this.getEnMap().getPhysicsTableExt() + " modify " + attr.getField() + " NVARCHAR(" + attr.getMaxLength() + ")");
					}
					else
					{
						this.RunSQL("alter table " + dr.getValue("OWNER") + "." + this.getEnMap().getPhysicsTableExt() + " modify " + attr.getField() + " text");
					}
				}
				catch (RuntimeException ex)
				{
					bp.da.Log.DebugWriteWarning(ex.getMessage());
				}
			}
		}

			///


			///检查枚举类型字段是否是INT 类型
		Attrs attrs = this.getEnMap().getHisEnumAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getMyDataType() != DataType.AppInt)
			{
				continue;
			}

			sql = "SELECT DATA_TYPE FROM information_schema.columns WHERE table_name='" + this.getEnMap().getPhysicsTable() + "' AND COLUMN_NAME='" + attr.getField() + "' and table_schema='" + SystemConfig.getAppCenterDBDatabase() + "'";
			String val = DBAccess.RunSQLReturnString(sql);
			if (val == null)
			{
				Log.DefaultLogWriteLineError("@没有检测到字段eunm" + attr.getKey());
			}

			if (val.indexOf("CHAR") != -1)
			{
				/*如果它是 varchar 字段*/
				sql = "SELECT table_schema as OWNER FROM information_schema.columns WHERE  table_name='" + this.getEnMap().getPhysicsTableExt() + "' AND COLUMN_NAME='" + attr.getField() + "' and table_schema='" + SystemConfig.getAppCenterDBDatabase() + "'";
				String OWNER = DBAccess.RunSQLReturnString(sql);
				try
				{
					this.RunSQL("alter table  " + this.getEnMap().getPhysicsTableExt() + " modify " + attr.getField() + " NUMBER ");
				}
				catch (RuntimeException ex)
				{
					Log.DefaultLogWriteLineError("运行sql 失败:alter table  " + this.getEnMap().getPhysicsTableExt() + " modify " + attr.getField() + " NUMBER " + ex.getMessage());
				}
			}
		}

			///


			///检查枚举类型是否存在.
		attrs = this.getEnMap().getHisEnumAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getMyDataType() != DataType.AppInt)
			{
				continue;
			}
			if (attr.UITag == null)
			{
				continue;
			}
			try
			{
				SysEnums ses = new SysEnums(attr.getUIBindKey(), attr.UITag);
				continue;
			}
			catch (java.lang.Exception e)
			{
			}
			String[] strs = attr.UITag.split("[@]", -1);
			SysEnums ens = new SysEnums();
			ens.Delete(SysEnumAttr.EnumKey, attr.getUIBindKey());
			for (String s : strs)
			{
				if (DataType.IsNullOrEmpty(s)==true)
				{
					continue;
				}

				String[] vk = s.split("[=]", -1);
				SysEnum se = new SysEnum();
				se.setIntKey(Integer.parseInt(vk[0]));
				se.setLab(vk[1]);
				se.setEnumKey(attr.getUIBindKey());
				se.Insert();
			}
		}


		this.CreateIndexAndPK();
	}

	private void CheckPhysicsTable_Ora() throws Exception
	{
		DataTable dt = new DataTable();
		String sql = "SELECT LISTAGG(DISTINCT(column_name),',') AS Column_Name  FROM all_tab_cols WHERE table_name = '" + this.getEnMap().getPhysicsTable().toUpperCase() + "' AND owner='" +  SystemConfig.getUser().toUpperCase() + "'";
		try{
			if(SystemConfig.getAppCenterDBType() != DBType.Oracle){
				sql = "SELECT WMSYS.WM_CONCAT(DISTINCT(column_name)) AS Column_Name  FROM all_tab_cols WHERE table_name = '" + this.getEnMap().getPhysicsTable().toUpperCase() + "' AND owner='" +  SystemConfig.getUser().toUpperCase() + "'";
			}
			dt = DBAccess.RunSQLReturnTable(sql);
		}catch(Exception e){
			Log.DebugWriteError("err@ORACLE执行SQL出错，SQL=[" + sql + "]");
		}
		if (dt.Rows.size() == 0)
		{
			return;
		}

		String fields = "," + dt.Rows.get(0).getValue(0).toString() + ",";
		fields = fields.toUpperCase();
		//如果不存在.
		for (Attr attr : this.getEnMap().getAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}

			if (attr.getItIsPK())
			{
				continue;
			}

			if (fields.contains("," + attr.getKey().toUpperCase() + ",") == true)
			{
				continue;
			}

			//if (fields.contains(attr.getKey().ToUpper() + ",") == true)
			//    continue;

			//if (fields.contains(","+attr.getKey().ToUpper()) == true)
			//    continue;

			if (attr.getKey().equals("AID"))
			{
				/* 自动增长列 */
				DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT  Identity(1,1)");
				continue;
			}

			/*不存在此列 , 就增加此列。*/
			switch (attr.getMyDataType())
			{
				case DataType.AppString:
				case DataType.AppDate:
				case DataType.AppDateTime:
					int len = attr.getMaxLength();
					if (len == 0)
					{
						len = 200;
					}
					DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " VARCHAR(" + len + ") DEFAULT '" + attr.getDefaultVal() + "' NULL");
					break;
				case DataType.AppInt:
				case DataType.AppBoolean:
					if (attr.getItIsPK() == true)
					{
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT '" + attr.getDefaultVal() + "' NOT NULL");
					}
					else
					{
						if(attr.getDefaultVal() == null)
							DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT   NULL");
						else
							DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT '" + attr.getDefaultVal() + "'   NULL");
					}
					break;
				case DataType.AppFloat:
				case DataType.AppMoney:
				case DataType.AppDouble:
					if(attr.getDefaultVal() == null)
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " FLOAT DEFAULT NULL");
					else
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " FLOAT DEFAULT '" + attr.getDefaultVal() + "' NULL");
					break;
				default:
					throw new RuntimeException("error MyFieldType= " + attr.getMyFieldType() + " Key=" + attr.getKey());
			}
		}

			///


			///检查字段长度是否符合最低要求
		for (Attr attr : this.getEnMap().getAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}
			if (attr.getMyDataType() == DataType.AppDouble || attr.getMyDataType() == DataType.AppFloat || attr.getMyDataType() == DataType.AppInt || attr.getMyDataType() == DataType.AppMoney || attr.getMyDataType() == DataType.AppBoolean)
			{
				continue;
			}

			int maxLen = attr.getMaxLength();
			dt = new DataTable();
			//sql = "SELECT DATA_LENGTH AS LEN, OWNER FROM ALL_TAB_COLUMNS WHERE upper(TABLE_NAME)='" + this.getEnMap().getPhysicsTable()Ext.ToUpper()
			//    + "' AND UPPER(COLUMN_NAME)='" + attr.Field.ToUpper() + "' AND DATA_LENGTH < " + attr.getMaxLength();

			//update dgq 2016-5-24 不取所有用户的表列名，只要取自己的就可以了
			sql = "SELECT DATA_LENGTH AS LEN FROM USER_TAB_COLUMNS WHERE upper(TABLE_NAME)='" + this.getEnMap().getPhysicsTableExt().toUpperCase() + "' AND UPPER(COLUMN_NAME)='" + attr.getField().toUpperCase() + "' AND DATA_LENGTH < " + attr.getMaxLength();
			dt = this.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
			{
				continue;
			}

			for (DataRow dr : dt.Rows)
			{
				//this.RunSQL("alter table " + dr["OWNER") + "." + this.getEnMap().getPhysicsTable()Ext + " modify " + attr.getField() + " varchar2(" + attr.getMaxLength() + ")");

				this.RunSQL("alter table " + this.getEnMap().getPhysicsTableExt() + " modify " + attr.getField() + " varchar2(" + attr.getMaxLength() + ")");

			}
		}

			///


			///检查枚举类型字段是否是INT 类型
		Attrs attrs = this.getEnMap().getHisEnumAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getMyDataType() != DataType.AppInt)
			{
				continue;
			}
			sql = "SELECT DATA_TYPE FROM ALL_TAB_COLUMNS WHERE upper(TABLE_NAME)='" + this.getEnMap().getPhysicsTableExt().toUpperCase() + "' AND UPPER(COLUMN_NAME)='" + attr.getField().toUpperCase() + "' ";
			String val = DBAccess.RunSQLReturnString(sql);
			if (val == null)
			{
				Log.DefaultLogWriteLineError("@没有检测到字段eunm" + attr.getKey());
			}
			if (val.indexOf("CHAR") != -1)
			{
				/*如果它是 varchar 字段*/
				sql = "SELECT OWNER FROM ALL_TAB_COLUMNS WHERE upper(TABLE_NAME)='" + this.getEnMap().getPhysicsTableExt().toUpperCase() + "' AND UPPER(COLUMN_NAME)='" + attr.getField().toUpperCase() + "' ";
				String OWNER = DBAccess.RunSQLReturnString(sql);
				try
				{
					this.RunSQL("alter table  " + this.getEnMap().getPhysicsTableExt() + " modify " + attr.getField() + " NUMBER ");
				}
				catch (RuntimeException ex)
				{
					Log.DefaultLogWriteLineError("运行sql 失败:alter table  " + this.getEnMap().getPhysicsTableExt() + " modify " + attr.getField() + " NUMBER " + ex.getMessage());
				}
			}
		}

			///


			///检查枚举类型是否存在.
		attrs = this.getEnMap().getHisEnumAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getMyDataType() != DataType.AppInt)
			{
				continue;
			}
			if (attr.UITag == null)
			{
				continue;
			}
			try
			{
				SysEnums ses = new SysEnums(attr.getUIBindKey(), attr.UITag);
				continue;
			}
			catch (java.lang.Exception e)
			{
			}
			String[] strs = attr.UITag.split("[@]", -1);
			SysEnums ens = new SysEnums();
			ens.Delete(SysEnumAttr.EnumKey, attr.getUIBindKey());
			for (String s : strs)
			{
				if (DataType.IsNullOrEmpty(s)==true)
				{
					continue;
				}

				String[] vk = s.split("[=]", -1);
				SysEnum se = new SysEnum();
				se.setIntKey(Integer.parseInt(vk[0]));
				se.setLab(vk[1]);
				se.setEnumKey(attr.getUIBindKey());
				se.Insert();
			}
		}

			///
		this.CreateIndexAndPK();
	}
	/*
	 * 人大金仓检查表字段
	 */
	private void CheckPhysicsTable_King() throws Exception
	{

		//检查字段是否存在
		//String sql = "SELECT * FROM " + this.getEnMap().getPhysicsTable() + " WHERE 1=2 ";
		String sql = "SELECT STRING_AGG(column_name,',') AS Column_Name  FROM ALL_TAB_COLUMNS WHERE upper(TABLE_NAME) = '" + this.getEnMap().getPhysicsTable().toUpperCase() + "'";
		// AND owner='" + SystemConfig.getAppCenterDBDatabase().toUpperCase() + "'";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			return;
		}

		String fields = "," + dt.Rows.get(0).getValue(0).toString() + ",";
		fields = fields.toUpperCase();
		//如果不存在.
		for (Attr attr : this.getEnMap().getAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}

			if (attr.getItIsPK())
			{
				continue;
			}

			if (fields.contains("," + attr.getKey().toUpperCase() + ",") == true)
			{
				continue;
			}

			//if (fields.contains(attr.getKey().ToUpper() + ",") == true)
			//    continue;

			//if (fields.contains(","+attr.getKey().ToUpper()) == true)
			//    continue;

			if (attr.getKey().equals("AID"))
			{
				/* 自动增长列 */
				DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT  Identity(1,1)");
				continue;
			}

			/*不存在此列 , 就增加此列。*/
			switch (attr.getMyDataType())
			{
				case DataType.AppString:
				case DataType.AppDate:
				case DataType.AppDateTime:
					int len = attr.getMaxLength();
					if (len == 0)
					{
						len = 200;
					}
					DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " VARCHAR(" + len + ") DEFAULT '" + attr.getDefaultVal() + "' NULL");
					break;
				case DataType.AppInt:
				case DataType.AppBoolean:
					if (attr.getItIsPK() == true)
					{
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT '" + attr.getDefaultVal() + "' NOT NULL");
					}
					else
					{
						if(attr.getDefaultVal() == null)
							DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT    NULL");
						else
							DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " INT DEFAULT '" + attr.getDefaultVal() + "'   NULL");
					}
					break;
				case DataType.AppFloat:
				case DataType.AppMoney:
				case DataType.AppDouble:
					if(attr.getDefaultVal() == null)
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " FLOAT DEFAULT  NULL");
					else
						DBAccess.RunSQL("ALTER TABLE " + this.getEnMap().getPhysicsTable() + " ADD " + attr.getField() + " FLOAT DEFAULT '" + attr.getDefaultVal() + "' NULL");
					break;
				default:
					throw new RuntimeException("error MyFieldType= " + attr.getMyFieldType() + " Key=" + attr.getKey());
			}
		}

		///检查字段长度是否符合最低要求
		for (Attr attr : this.getEnMap().getAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}
			if (attr.getMyDataType() == DataType.AppDouble || attr.getMyDataType() == DataType.AppFloat || attr.getMyDataType() == DataType.AppInt || attr.getMyDataType() == DataType.AppMoney || attr.getMyDataType() == DataType.AppBoolean)
			{
				continue;
			}

			int maxLen = attr.getMaxLength();
			dt = new DataTable();
			//sql = "SELECT DATA_LENGTH AS LEN, OWNER FROM ALL_TAB_COLUMNS WHERE upper(TABLE_NAME)='" + this.getEnMap().getPhysicsTable()Ext.ToUpper()
			//    + "' AND UPPER(COLUMN_NAME)='" + attr.Field.ToUpper() + "' AND DATA_LENGTH < " + attr.getMaxLength();

			//update dgq 2016-5-24 不取所有用户的表列名，只要取自己的就可以了
			sql = "SELECT DATA_LENGTH AS LEN FROM ALL_TAB_COLUMNS WHERE upper(TABLE_NAME)='" + this.getEnMap().getPhysicsTableExt().toUpperCase() + "' AND UPPER(COLUMN_NAME)='" + attr.getField().toUpperCase() + "' AND DATA_LENGTH < " + attr.getMaxLength();
			dt = this.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
			{
				continue;
			}

			for (DataRow dr : dt.Rows)
			{
				this.RunSQL("alter table " + this.getEnMap().getPhysicsTableExt() + "  ALTER COLUMN " + attr.getField() + " TYPE varchar2(" + attr.getMaxLength() + ")");
			}
		}

		///检查枚举类型字段是否是INT 类型
		Attrs attrs = this.getEnMap().getHisEnumAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getMyDataType() != DataType.AppInt)
			{
				continue;
			}
			sql = "SELECT DATA_TYPE FROM ALL_TAB_COLUMNS WHERE upper(TABLE_NAME)='" + this.getEnMap().getPhysicsTableExt().toUpperCase() + "' AND UPPER(COLUMN_NAME)='" + attr.getField().toUpperCase() + "' ";
			String val = DBAccess.RunSQLReturnString(sql);
			if (val == null)
			{
				Log.DefaultLogWriteLineError("@没有检测到字段eunm" + attr.getKey());
			}
			if (val.indexOf("CHAR") != -1)
			{
				/*如果它是 varchar 字段*/
				sql = "SELECT OWNER FROM ALL_TAB_COLUMNS WHERE upper(TABLE_NAME)='" + this.getEnMap().getPhysicsTableExt().toUpperCase() + "' AND UPPER(COLUMN_NAME)='" + attr.getField().toUpperCase() + "' ";
				String OWNER = DBAccess.RunSQLReturnString(sql);
				try
				{
					this.RunSQL("alter table  " + this.getEnMap().getPhysicsTableExt() + " ALTER COLUMN " + attr.getField() + " TYPE NUMBER ");
				}
				catch (RuntimeException ex)
				{
					Log.DefaultLogWriteLineError("运行sql 失败:alter table  " + this.getEnMap().getPhysicsTableExt() + " ALTER COLUMN " + attr.getField() + " NUMBER " + ex.getMessage());
				}
			}
		}

		///检查枚举类型是否存在.
		attrs = this.getEnMap().getHisEnumAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getMyDataType() != DataType.AppInt)
			{
				continue;
			}
			if (attr.UITag == null)
			{
				continue;
			}
			try
			{
				SysEnums ses = new SysEnums(attr.getUIBindKey(), attr.UITag);
				continue;
			}
			catch (java.lang.Exception e)
			{
			}
			String[] strs = attr.UITag.split("[@]", -1);
			SysEnums ens = new SysEnums();
			ens.Delete(SysEnumAttr.EnumKey, attr.getUIBindKey());
			for (String s : strs)
			{
				if (DataType.IsNullOrEmpty(s)==true)
				{
					continue;
				}

				String[] vk = s.split("[=]", -1);
				SysEnum se = new SysEnum();
				se.setIntKey(Integer.parseInt(vk[0]));
				se.setLab(vk[1]);
				se.setEnumKey(attr.getUIBindKey());
				se.Insert();
			}
		}

		this.CreateIndexAndPK();
	}


	/**
	 把entity的实体属性调度到en里面去.
	 * @throws Exception
	*/

	public final MapData DTSMapToSys_MapData() throws Exception
	{
		return DTSMapToSys_MapData(null);
	}


	public final MapData DTSMapToSys_MapData(String fk_mapdata) throws Exception
	{
		if (fk_mapdata == null)
		{

			//为了适应配置vsto系统的需要，这里需要全称.
			fk_mapdata = this.toString();
		}

		Map map = this.get_enMap();

		//获得短的类名称.


			///更新主表信息.
		MapData md = new MapData();
		md.setNo(fk_mapdata);
		if (md.RetrieveFromDBSources() == 0)
		{
			md.Insert();
		}

		md.setEnPK(this.getPK()); //主键
		md.setEnsName(fk_mapdata); //类名.
		md.setName(map.getEnDesc());
		md.setPTable(map.getPhysicsTable());
		md.Update();

			/// 更新主表信息.

		//删除.
		MapAttrs attrs = new MapAttrs();
		attrs.Delete(MapAttrAttr.FK_MapData, fk_mapdata);

		//同步属性 mapattr.
		DTSMapToSys_MapData_InitMapAttr(map.getAttrs(), fk_mapdata);


			///同步从表.
		//同步从表.
		EnDtls dtls = map.getDtls();
		for (EnDtl dtl : dtls)
		{
			MapDtl mdtl = new MapDtl();

			Entity enDtl = dtl.getEns().getNewEntity();

			mdtl.setNo(enDtl.getClassID());
			if (mdtl.RetrieveFromDBSources() == 0)
			{
				mdtl.Insert();
			}

			mdtl.setName(enDtl.getEnDesc());
			mdtl.setFrmID(fk_mapdata);
			mdtl.setPTable(enDtl.getEnMap().getPhysicsTable());

			mdtl.setRefPK(dtl.getRefKey()); //关联的主键.

			mdtl.Update();

			//同步字段.
			DTSMapToSys_MapData_InitMapAttr(enDtl.getEnMap().getAttrs(), enDtl.GetClassIDOfShort());
		}

			/// 同步从表.

		return md;
	}
	/**
	 同步字段属性

	 param attrs
	 param fk_mapdata
	 * @throws Exception
	*/
	private void DTSMapToSys_MapData_InitMapAttr(Attrs attrs, String frmID) throws Exception
	{
		for (Attr attr : attrs)
		{
			if (attr.getItIsRefAttr() )
			{
				continue;
			}

			MapAttr mattr = new MapAttr();
			mattr.setKeyOfEn(attr.getKey());
			mattr.setFrmID(frmID);
			mattr.setMyPK(mattr.getFrmID() + "_" + mattr.getKeyOfEn());
			mattr.RetrieveFromDBSources();

			mattr.setName(attr.getDesc());
			mattr.setDefVal(attr.getDefaultVal().toString());
			mattr.setKeyOfEn(attr.getField());
			mattr.setMaxLen(attr.getMaxLength());
			mattr.setMinLen(attr.getMinLength());
			mattr.setUIBindKey(attr.getUIBindKey());
			mattr.setUIIsLine(attr.UIIsLine);
			mattr.setUIHeight(0);

			if (attr.getMaxLength() > 3000)
			{
				mattr.setUIHeight(10);
			}

			mattr.setUIWidth(attr.getUIWidth());
			mattr.setMyDataType(attr.getMyDataType());

			mattr.setUIRefKey(attr.getUIRefKeyValue());

			mattr.setUIRefKeyText(attr.getUIRefKeyText());
			mattr.setUIVisible(attr.getUIVisible());
			if (attr.getItIsSupperText() == 1)
				mattr.setTextModel(3);
			//设置显示与隐藏，按照默认值.
			if (mattr.GetParaString("SearchVisable").equals(""))
			{
				if (mattr.getUIVisible() == true)
				{
					mattr.SetPara("SearchVisable", 1);
				}
				else
				{
					mattr.SetPara("SearchVisable", 0);
				}
			}


			switch (attr.getMyFieldType())
			{
				case Enum:
				case PKEnum:
					mattr.setUIContralType(attr.getUIContralType());
					mattr.setLGType(FieldTypeS.Enum);
					mattr.setUIIsEnable(attr.getUIIsReadonly());
					break;
				case FK:
				case PKFK:
					mattr.setUIContralType(attr.getUIContralType());
					mattr.setLGType(FieldTypeS.FK);
					//attr.setMyDataType((int)FieldType.FK;
					mattr.setUIRefKey("No");
					mattr.setUIRefKeyText("Name");
					mattr.setUIIsEnable(attr.getUIIsReadonly());
					break;
				default:
					mattr.setUIContralType(UIContralType.TB);
					mattr.setLGType(FieldTypeS.Normal);
					mattr.setUIIsEnable(!attr.getUIIsReadonly());
					switch (attr.getMyDataType())
					{
						case DataType.AppBoolean:
							mattr.setUIContralType(UIContralType.CheckBok);
							mattr.setUIIsEnable(attr.getUIIsReadonly());
							break;
						case DataType.AppDate:
							//if (this.Tag == "1")
							//    attr.getDefaultVal() = DataType.getCurrentDate();
							break;
						case DataType.AppDateTime:
							//if (this.Tag == "1")
							//    attr.getDefaultVal() = DataType.getCurrentDate();
							break;
						default:
							break;
					}
					break;
			}
			if (mattr.Update() == 0)
			{
				mattr.Insert();
			}
		}
	}
	@Override
	public String toString() {
		return this.getClass().getName();
	}
}
