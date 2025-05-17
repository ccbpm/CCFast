package bp.en;

import bp.da.*;
import bp.difference.SystemConfig;

/**
 QueryObject 的摘要说明。
*/
public class QueryObject
{
	private Entity _en = null;
	private Entities _ens = null;
	private String _sql = "";
	private Entity getEn() throws Exception {
		if (this._en == null)
		{
			return this.getEns().getNewEntity();
		}
		else
		{
			return this._en;
		}
	}
	private void setEn(Entity value)
	{this._en = value;
	}
	private Entities getEns()
	{
		return this._ens;
	}
	private void setEns(Entities value)
	{this._ens = value;
	}
	/**
	 处理Order by , group by .
	*/
	private String _groupBy = "";
	/**
	 要得到的查询sql 。
	 * @throws Exception
	*/
	public final String getSQL() throws Exception {
		String sql = "";
		String selecSQL = SqlBuilder.SelectSQL(this.getEn(), this.Top);
		if (this._sql == null || this._sql.length() == 0)
		{
			sql = selecSQL + this._groupBy + this._orderBy;
		}
		else
		{
			if (selecSQL.contains(" WHERE "))
			{
				sql = selecSQL + "  AND (" + this._sql + " ) " + _groupBy + this._orderBy;
			}
			else
			{
				sql = selecSQL + " WHERE   (" + this._sql + " ) " + _groupBy + this._orderBy;
			}
		}


		sql = sql.replace("  ", " ");
		sql = sql.replace("  ", " ");

		sql = sql.replace("AND ( AND )", "AND");
		sql = sql.replace("WHERE(1 = 1) AND ( AND )", "WHERE(1 = 1)");



		sql = sql.replace("WHERE AND", "WHERE");
		sql = sql.replace("WHERE  AND", "WHERE");

		sql = sql.replace("WHERE ORDER", "ORDER");

		return sql;
	}
	public final void setSQL(String value)
	{
		this._sql = this._sql + " " + value;
	}
	public final String getSQLWithOutPara() throws Exception {
		String sql = this.getSQL();
		for (Para en : this.getMyParas())
		{
			sql = sql.replace(SystemConfig.getAppCenterDBVarStr() + en.ParaName, "'" + en.val.toString() + "'");
		}
		return sql;
	}
	public final void AddWhere(String str)
	{
		this._sql = this._sql + " " + str;
	}
	/**
	 修改于2009 -05-12
	*/
	public int Top = -1;

	private Paras _Paras = null;
	public final Paras getMyParas()
	{
		if (_Paras == null)
		{
			_Paras = new Paras();
		}
		return _Paras;
	}
	public final void setMyParas(Paras value)
	{_Paras = value;
	}
	private Paras _ParasR = null;
	public final Paras getMyParasR()
	{
		if (_ParasR == null)
		{
			_ParasR = new Paras();
		}
		return _ParasR;
	}
	public final void AddPara(String key, Object v) throws Exception {
		key = "P" + key;
		this.getMyParas().Add(key, v);
	}
	public QueryObject()throws Exception
	{
	}
	/** DictBase
	*/
	public QueryObject(Entity en) throws Exception {
		this.getMyParas().clear();
		this._en = en;

		this.HisDBType = this._en.getEnMap().getEnDBUrl().getDBType();
		this.HisDBUrlType = this._en.getEnMap().getEnDBUrl().getDBUrlType();
	}
	public QueryObject(Entities ens) throws Exception {
		this.getMyParas().clear();
		ens.clear();
		this._ens = ens;

		Entity en = this._ens.getNewEntity();

		this.HisDBType = en.getEnMap().getEnDBUrl().getDBType();
		this.HisDBUrlType = en.getEnMap().getEnDBUrl().getDBUrlType();
	}
	public bp.da.DBType HisDBType = DBType.MSSQL;
	public bp.da.DBUrlType HisDBUrlType = DBUrlType.AppCenterDSN;

	public final String getHisVarStr()
	{
		switch (this.HisDBType)
		{
			case MSSQL:
			case Access:
				return ":";
			case MySQL:
				return ":";
			case Informix:
				return "?";
			default:
				return ":";
		}
	}
	/**
	 增加函数查寻．

	 param attr 属性
	 param exp 表达格式 大于，等于，小于
	 param len 长度
	 * @throws Exception
	*/
	public final void AddWhereLen(String attr, String exp, int len, bp.da.DBType dbtype) throws Exception
	{
		this.setSQL("(" + SystemConfig.getAppCenterDBLengthStr() + "(" + attr2Field(attr) + " ) " + exp + " '" + String.valueOf(len) + "')");
	}
	/**
	 增加查询条件，条件用 IN 表示．sql必须是一个列的集合．

	 param attr 属性
	 param sql 此sql,必须是有一个列的集合．
	 * @throws Exception
	*/
	public final void AddWhereInSQL(String attr, String sql) throws Exception
	{
		this.AddWhere(attr, " IN ", "(" + sql + " )");
	}
	/**
	 增加查询条件，条件用 IN 表示．sql必须是一个列的集合．

	 param attr 属性
	 param sql 此sql,必须是有一个列的集合．
	 * @throws Exception
	*/
	public final void AddWhereInSQL(String attr, String sql, String orderBy) throws Exception
	{
		this.AddWhere(attr, " IN ", "(" + sql + " )");
		this.addOrderBy(orderBy);
	}
	/**
	 增加查询条件，条件用 IN 表示．sql必须是一个列的集合．

	 param attr 属性
	 param sql 此sql,必须是有一个列的集合．
	 * @throws Exception
	*/
	public final void AddWhereNotInSQL(String attr, String sql) throws Exception
	{
		this.AddWhere(attr, " NOT IN ", " (" + sql + " ) ");
	}
	public final void AddWhereNotIn(String attr, String val) throws Exception
	{
		this.AddWhere(attr, " NOT IN ", " (" + val + " ) ");
	}
	/**
	 增加条件, DataTable 第一列的值．

	 param attr 属性
	 param dt 第一列是要组合的values
	 * @throws Exception
	*/
	public final void AddWhereIn(String attr, DataTable dt) throws Exception
	{
		String strs = "";
		for (DataRow dr : dt.Rows)
		{
			strs += "'"+dr.getValue(0).toString() + "',";
		}
		strs = strs.substring(0, strs.length() - 1);
		this.AddWhereIn(attr, "("+strs+")");
	}
	/**
	 增加条件,vals 必须是sql可以识别的字串．

	 param attr 属性
	 param vals 用 , 分开的．
	 * @throws Exception
	*/
	public final void AddWhereIn(String attr, String vals) throws Exception
	{
		this.AddWhere(attr, " IN ", vals);
	}
	/**


	 param attr
	 param exp
	 param val
	 * @throws Exception
	*/
	public final void AddWhere(String attr, String exp, Object val) throws Exception
	{
		AddWhere(attr, exp, val, null);
	}
	/**
	 增加条件

	 param attr 属性
	 param exp 操作符号（根据不同的数据库）
	 param val 值
	 param paraName 参数名称，可以为null, 如果查询中有多个参数中有相同属性名的需要，分别给他们起一个参数名。
	 * @throws Exception
	*/
	public final void AddWhere(String attr, String exp, Object val, String paraName) throws Exception {
		if (val == null)
		{
			val = "";
		}

		String valStr = String.valueOf(val);

		if (valStr.equals("all"))
		{
			this.setSQL("( 1=1 )");
			return;
		}

		if (exp.toLowerCase().contains("in"))
		{
			this.setSQL("(" + attr2Field(attr) + " " + exp + "  " + valStr + " )");
			return;
		}

		if (exp.toLowerCase().contains("like"))
		{
			if (attr.equals("FK_Dept"))
			{
				valStr = valStr.replace("'", "");
				valStr = valStr.replace("%", "");

				switch (this.HisDBType)
				{
					case Oracle:
					case DM:
					case KingBaseR3:
					case KingBaseR6:
					case GBASE8CByOracle:
						this.setSQL("(" + attr2Field(attr) + " " + exp + " '%'||" + this.getHisVarStr() + "FK_Dept||'%' )");
						this.getMyParas().Add("FK_Dept", valStr);
						break;
					default:
						//this.setSQL("(" + attr2Field(attr) + " " + exp + "  '" + this.HisVarStr + "FK_Dept%' )";
						this.setSQL("(" + attr2Field(attr) + " " + exp + "  '" + valStr + "%' )");
						//this.MyParas.Add("FK_Dept", val);
						break;
				}
			}
			else
			{
				if (valStr.contains(":") || valStr.contains("@"))
				{
					this.setSQL("(" + attr2Field(attr) + " " + exp + "  " + valStr + " )");
				}
				else
				{
					if (valStr.contains("'") == false)
					{
						this.setSQL("(" + attr2Field(attr) + " " + exp + "  '" + valStr + "' )");
					}
					else
					{
						this.setSQL("(" + attr2Field(attr) + " " + exp + "  " + valStr + " )");
					}
				}
			}
			return;
		}
		if (this.getHisVarStr().equals("?"))
		{
			this.setSQL("(" + attr2Field(attr) + " " + exp + "?)");
			this.getMyParas().Add(attr, val);
		}
		else
		{
			if (paraName == null)
			{
				this.setSQL("(" + attr2Field(attr) + " " + exp + this.getHisVarStr() + attr + ")");
				this.getMyParas().Add(attr, val);
			}
			else
			{
				this.setSQL("(" + attr2Field(attr) + " " + exp + this.getHisVarStr() + paraName + ")");
				this.getMyParas().Add(paraName, val);
			}
		}
	}
	public final void AddWhereDept(String val) throws Exception
	{
		String attr = "FK_Dept";
		String exp = "=";

		if (val.contains("'") == false)
		{
			this.setSQL("(" + attr2Field(attr) + " " + exp + "  '" + val + "' )");
		}
		else
		{
			this.setSQL("(" + attr2Field(attr) + " " + exp + "  " + val + " )");
		}
	}
	/**
	 是空的

	 param attr
	 * @throws Exception
	*/
	public final void AddWhereIsNull(String attr) throws Exception
	{
		if(DBAccess.getAppCenterDBType().equals(DBType.KingBaseR3) ||
				DBAccess.getAppCenterDBType().equals(DBType.KingBaseR6)){
			this.setSQL("(" + attr2Field(attr) + "  IS  NULL )");
		}else {
			this.setSQL("(" + attr2Field(attr) + "  IS NULL OR  " + attr2Field(attr) + "='' )");
		}
	}
	public final void AddWhereIsNotNull(String attr) throws Exception
	{
		if(DBAccess.getAppCenterDBType().equals(DBType.KingBaseR3) ||
				DBAccess.getAppCenterDBType().equals(DBType.KingBaseR6)){
			this.setSQL("(" + attr2Field(attr) + "  IS NOT NULL )");
		}else {
			this.setSQL("(" + attr2Field(attr) + "  IS NOT NULL AND  " + attr2Field(attr) + "!='' )");
		}
	}
	public final void AddWhereField(String attr, String exp, String val) throws Exception
	{
		if (val.toString().equals("all"))
		{
			this.setSQL("( 1=1 )");
			return;
		}

		if (exp.toLowerCase().contains("in"))
		{
			this.setSQL("(" + attr + " " + exp + "  " + val + " )");
			return;
		}

		this.setSQL("(" + attr + " " + exp + " :" + attr + " )");
		this.getMyParas().Add(attr, val);
	}
	public final void AddWhereField(String attr, String exp, int val) throws Exception
	{
		if (String.valueOf(val).equals("all"))
		{
			this.setSQL("( 1=1 )");
			return;
		}

		if (exp.toLowerCase().contains("in"))
		{
			this.setSQL("(" + attr + " " + exp + "  " + val + " )");
			return;
		}

		if (attr.equals("RowNum"))
		{
			this.setSQL("(" + attr + " " + exp + "  " + val + " )");
			return;
		}

		if (this.getHisVarStr().equals("?"))
		{
			this.setSQL("(" + attr + " " + exp + "?)");
		}
		else
		{
			this.setSQL("(" + attr + " " + exp + "  " + this.getHisVarStr() + attr + " )");
		}

		this.getMyParas().Add(attr, val);
	}
	/**
	 增加条件

	 param attr 属性
	 param exp 操作符号（根据不同的数据库）
	 param val 值
	 * @throws Exception
	*/
	public final void AddWhere(String attr, String exp, int val) throws Exception
	{
		if (attr.equals("RowNum"))
		{
			this.setSQL("(" + attr2Field(attr) + " " + exp + " " + val + ")");
		}
		else
		{
			if (this.getHisVarStr().equals("?"))
			{
				this.setSQL("(" + attr2Field(attr) + " " + exp + "?)");
			}
			else
			{
				this.setSQL("(" + attr2Field(attr) + " " + exp + this.getHisVarStr() + attr + ")");
			}

			this.getMyParas().Add(attr, val);
		}
	}
	public final void AddHD() throws Exception
	{
		this.setSQL("(  1=1 ) ");
	}
	/**
	 非恒等。
	 * @throws Exception
	*/
	public final void AddHD_Not() throws Exception
	{
		this.setSQL("(  1=2 ) ");
	}
	/**
	 增加条件

	 param attr 属性
	 param exp 操作符号（根据不同的数据库）
	 param val 值
	*/
	public final void AddWhere(String attr, String exp, float val)throws Exception
	{
		this.getMyParas().Add(attr, val);
		if (this.getHisVarStr().equals("?"))
		{
			this.setSQL("(" + attr2Field(attr) + " " + exp + "?)");
		}
		else
		{
			this.setSQL("(" + attr2Field(attr) + " " + exp + " " + this.getHisVarStr() + attr + ")");
		}
	}
	/**
	 增加条件(默认的是= )

	 param attr 属性
	 param val 值
	*/
	public final void AddWhere(String attr, String val) throws Exception {
		this.AddWhere(attr, "=", val);
	}
	/**
	 增加条件(默认的是= )

	 param attr 属性
	 param val 值
	*/
	public final void AddWhere(String attr, int val) throws Exception {
		this.AddWhere(attr, "=", val, null);
	}
	/**
	 增加条件

	 param attr 属性
	 param val 值 true/false
	*/
	public final void AddWhere(String attr, boolean val)throws Exception
	{
		if (val)
		{
			this.AddWhere(attr, "=", 1);
		}
		else
		{
			this.AddWhere(attr, "=", 0);
		}
	}
	public final void AddWhere(String attr, long val)throws Exception
	{
		this.AddWhere(attr, "=", val, null);
	}
	public final void AddWhere(String attr, float val)throws Exception
	{
		this.AddWhere(attr, "=", val);
	}
	public final void AddWhere(String attr, Object val)throws Exception
	{
		if (val == null)
			throw new RuntimeException("Attr=" + attr + ", 值是空 is null");


		if (val.getClass() == Integer.class || val.getClass() == Long.class || val.getClass() == Double.class)
		{
			this.AddWhere(attr, "=", val);
			return;
		}
		this.AddWhere(attr, "=", String.valueOf(val));
	}

	public final void addLeftBracket() throws Exception
	{
		this.setSQL(" (");
	}

	public final void addRightBracket() throws Exception
	{
		this.setSQL(" ) ");
	}

	public final void addAnd()
	{
		this.setSQL(" AND ");
	}

	public final void addOr()
	{
		this.setSQL(" OR ");
	}

	public void addSQL(String sql)
	{
		this.setSQL("("+sql+")");
	}

		///关于endsql
	public final void addGroupBy(String attr)throws Exception
	{
		this._groupBy = " GROUP BY  " + attr2Field(attr);
	}

	public final void addGroupBy(String attr1, String attr2)throws Exception
	{
		this._groupBy = " GROUP BY  " + attr2Field(attr1) + " , " + attr2Field(attr2);
	}

	private String _orderBy = "";
	public final void addOrderBy(String attr)
	{
		if (DataType.IsNullOrEmpty(attr) == true)
			return;

		if (this._orderBy.indexOf("ORDER BY") != -1)
		{
			this._orderBy += " , " + attr;
		}
		else
		{
			this._orderBy = " ORDER BY " + attr;
		}
	}

	/**

	*/
	public final void addOrderByRandom()throws Exception
	{
		if (this._orderBy.indexOf("ORDER BY") != -1)
		{
			this._orderBy = " , NEWID()";
		}
		else
		{
			this._orderBy = " ORDER BY NEWID()";
		}
	}
	/**
	 addOrderByDesc

	 param attr

	*/
	public final void addOrderByDesc(String attr)throws Exception
	{
		if (this._orderBy.indexOf("ORDER BY") != -1)
		{
			this._orderBy += " , " + attr2Field(attr) + " DESC ";
		}
		else
		{
			this._orderBy = " ORDER BY " + attr2Field(attr) + " DESC ";
		}

	}
	public final void addOrderByDesc(String attr1, String attr2)throws Exception
	{
		this._orderBy = " ORDER BY  " + attr2Field(attr1) + " DESC ," + attr2Field(attr2) + " DESC";
	}
	public final void addOrderBy(String attr1, String attr2)throws Exception
	{
		this._orderBy = " ORDER BY  " + attr2Field(attr1) + "," + attr2Field(attr2);
	}

		///

	public final void addHaving()throws Exception
	{
	}
	/** 清除查询条件
	*/
	public final void clear()throws Exception
	{
		this._sql = "";
		this._groupBy = "";
		//this._orderBy = "";
		this.getMyParas().clear();
	}
	private Map _HisMap;
	public final Map getHisMap() throws Exception {
		if (_HisMap == null)
		{
			_HisMap = this.getEn().getEnMap();
		}
		return _HisMap;
	}
	public final void setHisMap(Map value)
	{_HisMap = value;
	}
	/**
	 增加字段.

	 param attrKey
	 @return
	*/
	private String attr2Field(String attrKey) throws Exception {
		Attr attr = this.getHisMap().GetAttrByKey(attrKey);
		if (attr.getItIsRefAttr()  == true)
		{
			//  Entity en = attr.getHisFKEn();
			if (this.HisDBType == DBType.Oracle ||this.HisDBType == DBType.KingBaseR3
					||this.HisDBType == DBType.KingBaseR6 ||this.HisDBType == DBType.DM || this.HisDBType == DBType.GBASE8CByOracle)
			{
				return "T" + attr.getKey().replace("Text", "") + ".Name";
			}
			else
			{
				Entity en = attr.getHisFKEn();
				return en.getEnMap().getPhysicsTable() + "_" + attr.getKey().replace("Text", "") + ".Name";
			}

		}

		return this.getHisMap().getPhysicsTable() + "." + attr.getField();
		// return this.HisMap.getPhysicsTable() + "."+attr;
	}
	public final DataTable DoGroupReturnTable(Entity en, Attrs attrsOfGroupKey, Attr attrGroup, GroupWay gw, OrderWay ow) throws Exception
	{
		switch (en.getEnMap().getEnDBUrl().getDBType())
		{
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				return DoGroupReturnTableOracle(en, attrsOfGroupKey, attrGroup, gw, ow);
			default:
				return DoGroupReturnTableSqlServer(en, attrsOfGroupKey, attrGroup, gw, ow);
		}
	}
	public final DataTable DoGroupReturnTableOracle(Entity en, Attrs attrsOfGroupKey, Attr attrGroup, GroupWay gw, OrderWay ow)throws Exception
	{

			/// 生成要查询的语句
		String fields = "";
		String str = "";
		for (Attr attr : attrsOfGroupKey)
		{
			if (attr.getField() == null)
			{
				continue;
			}

			str = "," + attr.getField();
			fields += str;
		}

		if (attrGroup.getKey().equals("MyNum"))
		{
			switch (gw)
			{
				case BySum:
					fields += ", COUNT(*) AS MyNum";
					break;
				case ByAvg:
					fields += ", AVG(" + attrGroup.getField() + ") AS MyNum";
					break;
				default:
					throw new RuntimeException("no such case:");
			}
		}
		else
		{
			switch (gw)
			{
				case BySum:
					fields += ",SUM(" + attrGroup.getField() + ") AS " + attrGroup.getKey();
					break;
				case ByAvg:
					fields += ",AVG(" + attrGroup.getField() + ") AS " + attrGroup.getKey();
					break;
				default:
					throw new RuntimeException("no such case:");
			}
		}

		String by = "";
		for (Attr attr : attrsOfGroupKey)
		{
			if (attr.getField() == null)
			{
				continue;
			}

			str = "," + attr.getField();
			by += str;
		}
		by = by.substring(1);
		//String sql
		String sql = "SELECT " + fields.substring(1) + " FROM " + this.getEn().getEnMap().getPhysicsTable() + " WHERE " + this._sql + " Group BY " + by;

			///


			///#region
		Map map = new Map();
		map.setPhysicsTable("@VT@");
		map.setAttrs(attrsOfGroupKey);
		map.getAttrs().Add(attrGroup);

			/// .

		String sql1 = SqlBuilder.SelectSQLOfOra(en.toString(), map) + " " + SqlBuilder.GenerFormWhereOfOra(en, map);

		sql1 = sql1.replace("@TopNum", "");
		sql1 = sql1.replace("FROM @VT@", "FROM (" + sql + ") VT");
		sql1 = sql1.replace("@VT@", "VT");
		sql1 = sql1.replace("TOP", "");

		if (ow == OrderWay.OrderByUp)
		{
			sql1 += " ORDER BY " + attrGroup.getKey() + " DESC ";
		}
		else
		{
			sql1 += " ORDER BY " + attrGroup.getKey();
		}

		return this.getEn().RunSQLReturnTable(sql1, this.getMyParas());
	}

	public final DataTable DoGroupReturnTableSqlServer(Entity en, Attrs attrsOfGroupKey, Attr attrGroup, GroupWay gw, OrderWay ow) throws Exception
	{


			/// 生成要查询的语句
		String fields = "";
		String str = "";
		for (Attr attr : attrsOfGroupKey)
		{
			if (attr.getField() == null)
			{
				continue;
			}
			str = "," + attr.getField();
			fields += str;
		}

		if (attrGroup.getKey().equals("MyNum"))
		{
			switch (gw)
			{
				case BySum:
					fields += ", COUNT(*) AS MyNum";
					break;
				case ByAvg:
					fields += ", AVG(*)   AS MyNum";
					break;
				default:
					throw new RuntimeException("no such case:");
			}
		}
		else
		{
			switch (gw)
			{
				case BySum:
					fields += ",SUM(" + attrGroup.getField() + ") AS " + attrGroup.getKey();
					break;
				case ByAvg:
					fields += ",AVG(" + attrGroup.getField() + ") AS " + attrGroup.getKey();
					break;
				default:
					throw new RuntimeException("no such case:");
			}
		}

		String by = "";
		for (Attr attr : attrsOfGroupKey)
		{
			if (attr.getField() == null)
			{
				continue;
			}

			str = "," + attr.getField();
			by += str;
		}
		by = by.substring(1);
		//String sql
		String sql = "SELECT " + fields.substring(1) + " FROM " + this.getEn().getEnMap().getPhysicsTable() + " WHERE " + this._sql + " Group BY " + by;

			///


			///#region
		Map map = new Map();
		map.setPhysicsTable("@VT@");
		map.setAttrs(attrsOfGroupKey);
		map.getAttrs().Add(attrGroup);

			/// .
		//String sql1=SqlBuilder.SelectSQLOfMS( map )+" "+SqlBuilder.GenerFormWhereOfMS( en,map) + "   AND (" + this._sql+" ) "+_endSql;

		String sql1 = SqlBuilder.SelectSQLOfMS(map) + " " + SqlBuilder.GenerFormWhereOfMS(en, map);

		sql1 = sql1.replace("@TopNum", "");

		sql1 = sql1.replace("FROM @VT@", "FROM (" + sql + ") VT");

		sql1 = sql1.replace("@VT@", "VT");
		sql1 = sql1.replace("TOP", "");
		if (ow == OrderWay.OrderByUp)
		{
			sql1 += " ORDER BY " + attrGroup.getKey() + " DESC ";
		}
		else
		{
			sql1 += " ORDER BY " + attrGroup.getKey();
		}
		return this.getEn().RunSQLReturnTable(sql1, this.getMyParas());
	}
	/**
	 分组查询，返回datatable.

	 param attrsOfGroupKey
	 param attrGroup
	 param gw
	 @return
	 * @throws Exception
	*/
	public final DataTable DoGroupReturnTable1(Entity en, Attrs attrsOfGroupKey, Attr attrGroup, GroupWay gw, OrderWay ow) throws Exception
	{

			/// 生成要查询的语句
		String fields = "";
		String str = "";
		for (Attr attr : attrsOfGroupKey)
		{
			if (attr.getField() == null)
			{
				continue;
			}
			str = "," + attr.getField();
			fields += str;
		}

		if (attrGroup.getKey().equals("MyNum"))
		{
			switch (gw)
			{
				case BySum:
					fields += ", COUNT(*) AS MyNum";
					break;
				case ByAvg:
					fields += ", AVG(*)   AS MyNum";
					break;
				default:
					throw new RuntimeException("no such case:");
			}
		}
		else
		{
			switch (gw)
			{
				case BySum:
					fields += ",SUM(" + attrGroup.getField() + ") AS " + attrGroup.getKey();
					break;
				case ByAvg:
					fields += ",AVG(" + attrGroup.getField() + ") AS " + attrGroup.getKey();
					break;
				default:
					throw new RuntimeException("no such case:");
			}
		}

		String by = "";
		for (Attr attr : attrsOfGroupKey)
		{
			if (attr.getField() == null)
			{
				continue;
			}

			str = "," + attr.getField();
			by += str;
		}
		by = by.substring(1);
		//String sql
		String sql = "SELECT " + fields.substring(1) + " FROM " + this.getEn().getEnMap().getPhysicsTable() + " WHERE " + this._sql + " Group BY " + by;

			///


			///#region
		Map map = new Map();
		map.setPhysicsTable("@VT@");
		map.setAttrs(attrsOfGroupKey);
		map.getAttrs().Add(attrGroup);

			/// .

		//String sql1=SqlBuilder.SelectSQLOfMS( map )+" "+SqlBuilder.GenerFormWhereOfMS( en,map) + "   AND (" + this._sql+" ) "+_endSql;

		String sql1 = SqlBuilder.SelectSQLOfMS(map) + " " + SqlBuilder.GenerFormWhereOfMS(en, map);

		sql1 = sql1.replace("@TopNum", "");
		sql1 = sql1.replace("FROM @VT@", "FROM (" + sql + ") VT");
		sql1 = sql1.replace("@VT@", "VT");
		sql1 = sql1.replace("TOP", "");
		if (ow == OrderWay.OrderByUp)
		{
			sql1 += " ORDER BY " + attrGroup.getKey() + " DESC ";
		}
		else
		{
			sql1 += " ORDER BY " + attrGroup.getKey();
		}
		return this.getEn().RunSQLReturnTable(sql1);
	}
	public String[] FullAttrs = null;
	/**
	 执行查询

	 @return
	 * @throws Exception
	*/
	public final int DoQuery() throws Exception
	{
		try
		{
			if (this._en == null)
			{
				return this.doEntitiesQuery();
			}
			else
			{
				return this.doEntityQuery();
			}
		}
		catch (RuntimeException ex)
		{
			//如果查询失败，1. 做一次检查.  2.在执行一次查询.
			if (this._en == null)
				this._ens.getNewEntity().CheckPhysicsTable();
			else
				this._en.CheckPhysicsTable();

			//执行查询.`
			if (this._en == null)
				return this.doEntitiesQuery();
			else
				return this.doEntityQuery();
		}
	}
	public final int DoQueryBak20111203() throws Exception
	{
		try
		{
			if (this._en == null)
			{
				return this.doEntitiesQuery();
			}
			else
			{
				return this.doEntityQuery();
			}
		}
		catch (RuntimeException ex)
		{
			try
			{
				if (this._en == null)
				{
					this.getEns().getNewEntity().CheckPhysicsTable();
				}
				else
				{
					this._en.CheckPhysicsTable();
				}
			}
			catch (java.lang.Exception e)
			{
			}
			throw ex;
		}
	}
	public final String DealString(String sql) throws Exception
	{
		DataTable dt = this.getEn().RunSQLReturnTable(sql);
		String strs = "";
		for (DataRow dr : dt.Rows)
		{
			strs += ",'" + dr.getValue(0).toString() + "'";
		}
		return strs.substring(1);
	}

	public final String GenerPKsByTableWithParaMySQL(String pk, String sql, int from, int to)throws Exception
	{
		DataTable dt = DBAccess.RunSQLReturnTable(sql, this.getMyParas());
		String pks = "";
		int paraI = 0;

		String dbStr = SystemConfig.getAppCenterDBVarStr();
		for (DataRow dr : dt.Rows)
		{
			paraI++;
			if (dbStr.equals("?"))
			{
				pks += "?,";
			}
			else
			{
				pks += SystemConfig.getAppCenterDBVarStr() + "R" + paraI + ",";
			}

			if (pk.equals("OID") || pk.equals("WorkID") || pk.equals("NodeID"))
			{
				this.getMyParasR().Add("R" + paraI, Integer.parseInt(dr.getValue(0).toString()));
			}
			else
			{
				this.getMyParasR().Add("R" + paraI, dr.getValue(0).toString());
			}
		}

		if (pks.equals(""))
			return null;
		return pks.substring(0, pks.length() - 1);

	}
	public final String GenerPKsByTableWithPara(String pk, String sql, int from, int to)throws Exception
	{
		DataTable dt = DBAccess.RunSQLReturnTable(sql, this.getMyParas());
		String pks = "";
		int i = 0;
		int paraI = 0;

		String dbStr = SystemConfig.getAppCenterDBVarStr();
		for (DataRow dr : dt.Rows)
		{
			i++;
			if (i > from)
			{
				paraI++;
				//pks += "'" + dr[0).toString() + "'";
				if (dbStr.equals("?"))
				{
					pks += "?,";
				}
				else
				{
					pks += SystemConfig.getAppCenterDBVarStr() + "R" + paraI + ",";
				}

				if (pk.equals("OID") || pk.equals("WorkID") || pk.equals("NodeID"))
				{
					this.getMyParasR().Add("R" + paraI, Integer.parseInt(dr.getValue(0).toString()));
				}
				else
				{
					this.getMyParasR().Add("R" + paraI, dr.getValue(0).toString());
				}


				if (i >= to)
				{
					return pks.substring(0, pks.length() - 1);
				}
			}
		}
		if (pks.equals(""))
			return null;

		return pks.substring(0, pks.length() - 1);
	}
	public final String GenerPKsByTable(String sql, int from, int to) throws Exception
	{
		//Log.DefaultLogWriteLineWarning(" ***************************** From= " + from + "  T0" + to);
		DataTable dt = this.getEn().RunSQLReturnTable(sql, this.getMyParas());
		String pks = "";
		int i = 0;
		for (DataRow dr : dt.Rows)
		{
			i++;
			if (i > from)
			{
				if (i >= to)
				{
					pks += "'" + dr.getValue(0).toString() + "'";
					return pks;
				}
				else
				{
					pks += "'" + dr.getValue(0).toString() + "',";
				}
			}
		}
		if (pks.equals(""))
		{
			return "  '11111111' ";
		}
		return pks.substring(0, pks.length() - 1);
	}
	/**
	 删除当前查询的排序字段，然后可以再次增加其他的排序字段
	 <p>added by liuxc,2015.3.18,为解决默认增加的是主键字段排序，但此排序字段未提供删除方法的问题</p>
	*/
	public final void ClearOrderBy()throws Exception
	{
		this._orderBy = "";
	}
	/**


	 param pk
	 param pageSize
	 param pageIdx
	 @return
	 * @throws Exception
	*/
	public final int DoQuery(String pk, int pageSize, int pageIdx) throws Exception
	{
		if (pk.equals("OID") || pk.equals("WorkID"))
		{
			return DoQuery(pk, pageSize, pageIdx, pk, true);
		}
		else
		{
			return DoQuery(pk, pageSize, pageIdx, pk, false);
		}
	}
	/**
	 分页查询方法

	 param pk 主键
	 param pageSize 页面大小
	 param pageIdx 第x页
	 param orderBy 排序
	 param orderWay 排序方式: 两种情况 Down UP
	 @return 查询结果
	 * @throws Exception
	*/
	public final int DoQuery(String pk, int pageSize, int pageIdx, String orderBy, String orderWay) throws Exception
	{
		if (orderWay.toLowerCase().trim().equals("up") || orderWay.toLowerCase().trim().equals("asc"))
		{
			return DoQuery(pk, pageSize, pageIdx, orderBy, false);
		}
		else
		{
			return DoQuery(pk, pageSize, pageIdx, orderBy, true);
		}
	}
	/**
	 分页查询方法

	 param pk 主键
	 param pageSize 页面大小
	 param pageIdx 第x页
	 @return 查询结果
	 * @throws Exception
	*/
	public final int DoQuery(String pk, int pageSize, int pageIdx, boolean isDesc) throws Exception
	{
		return DoQuery(pk, pageSize, pageIdx, pk, isDesc);
	}
	/**
	 分页查询方法

	 param pk 主键
	 param pageSize 页面大小
	 param pageIdx 第x页
	 param orderBy 排序
	 @return 查询结果
	 * @throws Exception
	*/
	public final int DoQuery(String pk, int pageSize, int pageIdx, String orderBy, boolean isDesc) throws Exception
	{
		int pageNum = 0;

		//如果没有加入排序字段，使用主键
		if (DataType.IsNullOrEmpty(this._orderBy))
		{
			String isDescStr = "";
			if (isDesc)
			{
				isDescStr = " DESC ";
			}

			if (DataType.IsNullOrEmpty(orderBy))
			{
				orderBy = pk;
			}

			this._orderBy = attr2Field(orderBy) + isDescStr;
		}

		if (this._orderBy.contains("ORDER BY") == false)
		{
			_orderBy = " ORDER BY " + this._orderBy;
		}

		try
		{
			if (this._en == null)
			{
				int recordConut = 0;
				recordConut = this.GetCount(); // 获取 它的数量。

				if (recordConut == 0)
				{
					this._ens.clear();
					return 0;
				}

				// xx!5555 提出的错误.
				if (pageSize == 0)
				{
					pageSize = 12;
				}

				pageNum = Math.round(recordConut / pageSize); // 页面个数。

				pageNum++;
				int top = pageSize * (pageIdx - 1);

				int myleftCount = recordConut - (pageNum * pageSize);

				pageNum++;

				String sql = "";
				Entity en = this._ens.getNewEntity();
				Map map = en.getEnMap();
				int toIdx = 0;
				String pks = "";
				switch (map.getEnDBUrl().getDBType())
				{
					case Oracle:
					case DM:
					case KingBaseR3:
					case KingBaseR6:
					case GBASE8CByOracle:
						toIdx = top + pageSize;
						if (DataType.IsNullOrEmpty(this._sql)==true)
						{
							if (top == 0)
							{
								sql = "SELECT * FROM ( SELECT  " + pk + " FROM " + map.getPhysicsTable() + " " + this._orderBy + "   ) WHERE ROWNUM <=" + pageSize;
							}
							else
							{
								sql = "SELECT * FROM ( SELECT  " + pk + " FROM " + map.getPhysicsTable() + " " + this._orderBy + ") ";
							}
						}
						else
						{
							String mysql = this.getSQL();
							mysql = mysql.substring(mysql.indexOf("FROM "));

							if (top == 0)
							{
								sql = "SELECT * FROM ( SELECT " + map.getPhysicsTable() + "." + pk + " " + mysql + " )  WHERE ROWNUM <=" + pageSize;
							}
							else
							{
								sql = "SELECT * FROM ( SELECT " + map.getPhysicsTable() + "." + pk + " " + mysql + " ) ";
							}
							//sql = "SELECT * FROM ( SELECT  " + pk + " FROM " + map.getPhysicsTable() + " WHERE " + this._sql + " " + this._orderBy + "   ) ";
						}

						sql = sql.replace("AND ( ( 1=1 ) )", " ");

						pks = this.GenerPKsByTableWithPara(pk, sql, top, toIdx);
						this.clear();
						this.setMyParas(this.getMyParasR());
						if (pks != null)
						{
							this.AddWhereIn(pk, "(" + pks + ")");
						}
						else
						{
							this.AddHD();
						}

						this.Top= pageSize;
						return this.doEntitiesQuery();
					case Informix:
						toIdx = top + pageSize;
						if (DataType.IsNullOrEmpty(this._sql)==true)
						{
							if (top == 0)
							{
								sql = " SELECT first " + pageSize + "  " + this.getEn().getPKField() + " FROM " + map.getPhysicsTable() + " " + this._orderBy;
							}
							else
							{
								sql = " SELECT  " + this.getEn().getPKField() + " FROM " + map.getPhysicsTable() + " " + this._orderBy;
							}
						}
						else
						{
							String mysql = this.getSQL();
							mysql = mysql.substring(mysql.indexOf("FROM "));
							if (top == 0)
							{
								sql = "SELECT first " + pageSize + " " + this.getEn().getPKField() + "  " + mysql;
							}
							else
							{
								sql = "SELECT  " + this.getEn().getPKField() + " " + mysql;
							}
						}

						sql = sql.replace("AND ( ( 1=1 ) )", " ");

						pks = this.GenerPKsByTableWithPara(pk, sql, top, toIdx);
						this.clear();
						this.setMyParas(this.getMyParasR());

						if (pks == null)
						{
							this.AddHD_Not();
						}
						else
						{
							this.AddWhereIn(pk, "(" + pks + ")");
						}

						this.Top= pageSize;
						return this.doEntitiesQuery();
					case MySQL:
					case HGDB:
					case GBASE8CByMySQL:
					case GBASE8A:
						toIdx = top + pageSize;
						if (DataType.IsNullOrEmpty(this._sql)==true)
						{
							if (top == 0)
							{
								sql = " SELECT  " + this.getEn().getPKField() + " FROM " + map.getPhysicsTable() + " " + this._orderBy + " LIMIT " + pageSize;
							}
							else
							{
								sql = " SELECT  " + this.getEn().getPKField() + " FROM " + map.getPhysicsTable() + " " + this._orderBy +" LIMIT "+top+", "+pageSize;
							}
						}
						else
						{
							String mysql = this.getSQL();
							mysql = mysql.substring(mysql.indexOf("FROM "));

							if (top == 0)
							{
								sql = "SELECT " + map.getPhysicsTable() + "." + this.getEn().getPKField() + " " + mysql + " LIMIT " + pageSize;
							}
							else
							{
								sql = "SELECT " + map.getPhysicsTable() + "." + this.getEn().getPKField() + " " + mysql +" LIMIT "+top+", "+pageSize;
							}
						}

						sql = sql.replace("AND ( ( 1=1 ) )", " ");

						pks = this.GenerPKsByTableWithParaMySQL(pk, sql, top, toIdx);
						this.clear();
						this.setMyParas(this.getMyParasR());

						if (pks == null)
						{
							this.AddHD_Not();
						}
						else
						{
							this.AddWhereIn(pk, "(" + pks + ")");
						}
						this.Top=pageSize;
						return this.doEntitiesQuery();
					case PostgreSQL:
						toIdx = top + pageSize;
						if (DataType.IsNullOrEmpty(this._sql)==true)
						{
							if (top == 0)
							{
								sql = " SELECT  " + this.getEn().getPKField() + " FROM " + map.getPhysicsTable() + " " + this._orderBy + " LIMIT " + pageSize;
							}
							else
							{
								sql = " SELECT  " + this.getEn().getPKField() + " FROM " + map.getPhysicsTable() + " " + this._orderBy +" LIMIT "+pageSize+" offset "+top;
							}
						}
						else
						{
							String mysql = this.getSQL();
							mysql = mysql.substring(mysql.indexOf("FROM "));

							if (top == 0)
							{
								sql = "SELECT " + map.getPhysicsTable() + "." + this.getEn().getPKField() + " " + mysql + " LIMIT " + pageSize;
							}
							else
							{
								sql = "SELECT " + map.getPhysicsTable() + "." + this.getEn().getPKField() + " " + mysql +" LIMIT "+ pageSize +" offset "+top;
							}
						}

						sql = sql.replace("AND ( ( 1=1 ) )", " ");

						pks = this.GenerPKsByTableWithParaMySQL(pk, sql, top, toIdx);
						this.clear();
						this.setMyParas(this.getMyParasR());

						if (pks == null)
						{
							this.AddHD_Not();
						}
						else
						{
							this.AddWhereIn(pk, "(" + pks + ")");
						}
						this.Top=pageSize;
						return this.doEntitiesQuery();
					case MSSQL:
					default:
						toIdx = top + pageSize;
						if (DataType.IsNullOrEmpty(this._sql)==true)
						{
							//此处去掉原有的第1页时用top pagesize的写法，会导致第1页数据查询出来的不准确，统一都用下面的写法，edited by liuxc,2017-8-30
							//此处查询数据，除第1页外，有可能会造排序不正确，但每一页的数据是准确的，限于原有写法，没法改动此处逻辑解决这个问题
							sql = " SELECT  [" + this.getEn().getPKField() + "] FROM " + map.getPhysicsTable() + " " + this._orderBy;
						}
						else
						{
							String mysql = this.getSQL();
							mysql = mysql.substring(mysql.indexOf("FROM "));
							sql = "SELECT " + map.getPhysicsTable() + "." + this.getEn().getPKField() + " as  [" + this.getEn().getPKField() + "]  " + mysql;
						}

						sql = sql.replace("AND ( ( 1=1 ) )", " ");

						pks = this.GenerPKsByTableWithPara(pk, sql, top, toIdx);
						this.clear();
						this.setMyParas(this.getMyParasR());

						if (pks == null)
						{
							this.AddHD_Not();
						}
						else
						{
							this.AddWhereIn(pk, "(" + pks + ")");
						}

						this.Top=pageSize;
						return this.doEntitiesQuery();
				}
			}
			else
			{
				return this.doEntityQuery();
			}
		}
		catch (RuntimeException ex)
		{
			try
			{
				if (this._en == null)
				{
					this.getEns().getNewEntity().CheckPhysicsTable();
				}
				else
				{
					this._en.CheckPhysicsTable();
				}
			}
			catch (java.lang.Exception e)
			{
			}
			throw ex;
		}
	}
	/**
	 按照

	 @return
	 * @throws Exception
	*/
	public final DataTable DoQueryToTable() throws Exception
	{
		try
		{
			String sql = this.getSQL();
			sql = sql.replace("WHERE (1=1) AND ( AND ( ( ( 1=1 ) ) AND ( ( 1=1 ) ) ) )", "");

			return DBAccess.RunSQLReturnTable(sql, this.getMyParas());
		}
		catch (RuntimeException ex)
		{
			if (this._en == null)
			{
				this.getEns().getNewEntity().CheckPhysicsTable();
			}
			else
			{
				this._en.CheckPhysicsTable();
			}
			throw ex;
		}
	}
	private boolean checkedTable = false;
	/**
	 得到返回的数量

	 @return 得到返回的数量
	 * @throws Exception
	*/
	public final int GetCount() throws Exception
	{
		String sql = this.getSQL();
		String ptable = this.getEn().getEnMap().getPhysicsTable();
		String pk = this.getEn().getPKField();

		switch (this.getEn().getEnMap().getEnDBUrl().getDBType())
		{
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				if (DataType.IsNullOrEmpty(this._sql)==true)
				{
					sql = "SELECT COUNT(" + ptable + "." + pk + ") as C FROM " + ptable;
				}
				else
				{
					sql = "SELECT COUNT(" + ptable + "." + pk + ") as C " + sql.substring(sql.indexOf("FROM "));
				}
				break;
			default:
				if (DataType.IsNullOrEmpty(this._sql)==true)
				{
					sql = "SELECT COUNT(" + ptable + "." + pk + ") as C FROM " + ptable;
				}
				else
				{
					sql = sql.substring(sql.indexOf("FROM "));
					if (sql.indexOf("ORDER BY") >= 0)
					{
						sql = sql.substring(0, sql.indexOf("ORDER BY") - 1);
					}
					sql = "SELECT COUNT(" + ptable + "." + pk + ") as C " + sql;
				}
				//sql="SELECT COUNT(*) as C "+this._endSql  +sql.Substring(  sql.IndexOf("FROM ") ) ;
				//sql="SELECT COUNT(*) as C FROM "+ this._ens.getNewEntity().getEnMap().getPhysicsTable()+ "  " +sql.Substring(sql.IndexOf("WHERE") ) ;
				//int i = sql.IndexOf("ORDER BY") ;
				//if (i!=-1)
				//	sql=sql.Substring(0,i);
				break;
		}
		try
		{
			int i = this.getEn().RunSQLReturnValInt(sql, this.getMyParas());
			if (this.Top == -1)
			{
				return i;
			}

			if (this.Top >= i)
			{
				return i;
			}
			else
			{
				return this.Top;
			}
		}
		catch (RuntimeException ex)
		{
			this.getEn().CheckPhysicsTable();
			if(checkedTable == false)
			{
				checkedTable = true;
				return this.GetCount();
			}
			throw ex;
		}
	}
	public final DataTable GetSumOrAvg(String oper) throws Exception
	{
		String sql = this.getSQL();
		String ptable = this.getEn().getEnMap().getPhysicsTable();
		String pk = this.getEn().getPKField();

		switch (this.getEn().getEnMap().getEnDBUrl().getDBType())
		{
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				if (DataType.IsNullOrEmpty(this._sql)==true)
				{
					sql = "SELECT " + oper + " FROM " + ptable;
				}
				else
				{
					sql = "SELECT " + oper + sql.substring(sql.indexOf("FROM "));
				}
				break;
			default:
				if (DataType.IsNullOrEmpty(this._sql)==true)
				{
					sql = "SELECT  " + oper + "  FROM " + ptable;
				}
				else
				{
					sql = sql.substring(sql.indexOf("FROM "));
					if (sql.indexOf("ORDER BY") >= 0)
					{
						sql = sql.substring(0, sql.indexOf("ORDER BY") - 1);
					}
					sql = "SELECT " + oper + " " + sql;
				}

				break;
		}
		try
		{

			return this.getEn().RunSQLReturnTable(sql, this.getMyParas());

		}
		catch (RuntimeException ex)
		{
			this.getEn().CheckPhysicsTable();
			throw ex;
		}
	}

	public final DataTable DoGroupQueryToTable(String selectSQl, String groupBy, String orderBy) throws Exception
	{
		String sql = this.getSQL();
		String ptable = this.getEn().getEnMap().getPhysicsTable();
		String pk = this.getEn().getPKField();

		switch (this.getEn().getEnMap().getEnDBUrl().getDBType())
		{
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				if (DataType.IsNullOrEmpty(this._sql)==true)
				{
					sql = selectSQl + " FROM " + ptable + "WHERE " + groupBy + orderBy;
				}
				else
				{
					sql = selectSQl + sql.substring(sql.indexOf(" FROM ")) + groupBy + orderBy;
				}
				break;
			default:
				if (DataType.IsNullOrEmpty(this._sql)==true)
				{
					sql = selectSQl + " FROM " + ptable + "WHERE " + groupBy + orderBy;
				}
				else
				{
					sql = sql.substring(sql.indexOf(" FROM "));
					if (sql.indexOf("ORDER BY") >= 0)
					{
						sql = sql.substring(0, sql.indexOf("ORDER BY") - 1);
					}
					sql = selectSQl + sql + groupBy + orderBy;
				}
				//sql="SELECT COUNT(*) as C "+this._endSql  +sql.Substring(  sql.IndexOf("FROM ") ) ;
				//sql="SELECT COUNT(*) as C FROM "+ this._ens.getNewEntity().getEnMap().getPhysicsTable()+ "  " +sql.Substring(sql.IndexOf("WHERE") ) ;
				//int i = sql.IndexOf("ORDER BY") ;
				//if (i!=-1)
				//	sql=sql.Substring(0,i);
				break;
		}
		return this.getEn().RunSQLReturnTable(sql, this.getMyParas());

	}
	/**
	 最大的数量

	 param topNum 最大的数量
	 @return 要查询的信息
	 * @throws Exception
	*/
	public final DataTable DoQueryToTable(int topNum) throws Exception
	{
		this.Top = topNum;
		return this.getEn().RunSQLReturnTable(this.getSQL(), this.getMyParas());
	}

	private int doEntityQuery() throws Exception
	{
		return EntityDBAccess.Retrieve(this.getEn(), this.getSQL(), this.getMyParas());
	}
	private int doEntitiesQuery() throws Exception {
		switch (this.HisDBType)
		{
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				if (this.Top != -1)
				{
					this.addAnd();
					this.AddWhereField("RowNum", "<=", this.Top);
				}
				break;
			case MSSQL:
			case MySQL:
			case GBASE8CByMySQL:
			case GBASE8A:
			default:
				break;
		}
		return EntityDBAccess.Retrieve(this.getEns(), this.getSQL(), this.getMyParas(), this.FullAttrs);
	}
	/**
	 根据data初始化entiies.

	 param ens 实体s
	 param dt 数据表
	 param fullAttrs 要填充的树形
	 @return 初始化后的ens
	 * @throws Exception
	*/
	public static Entities InitEntitiesByDataTable(Entities ens, DataTable dt, String[] fullAttrs) throws Exception
	{
		FieldCaseModel caseModel =SystemConfig.getAppCenterDBFieldCaseModel();
		if (fullAttrs == null) {
			Map enMap = ens.getNewEntity().getEnMap();
			Attrs attrs = enMap.getAttrs();
			try {
				Row row = null;
				for (DataRow dr : dt.Rows) {
					Entity en = ens.getNewEntity();
					row = new Row();
					for (Attr attr : attrs) {
						if (caseModel == FieldCaseModel.UpperCase){
							if(attr.getMyFieldType() == FieldType.RefText && dt.Columns.containsMatchCase(attr.getKey()))
								row.SetValByKey(attr.getKey(), dr.getValue(attr.getKey()));
							else
								row.SetValByKey(attr.getKey(), dr.getValue(attr.getKey().toUpperCase()));
						}else if(caseModel == FieldCaseModel.Lowercase) {
							if(attr.getMyFieldType() == FieldType.RefText && dt.Columns.containsMatchCase(attr.getKey()))
								row.SetValByKey(attr.getKey(), dr.getValue(attr.getKey()));
							else
							{
								//人大金仓数据库，有时字段命名不统一,小写与大小写混在一起
								if(SystemConfig.getAppCenterDBType()==DBType.KingBaseR6){
									if(dt.Columns.containsMatchCase(attr.getKey()))
										row.SetValByKey(attr.getKey(), dr.getValue(attr.getKey()));
									else if (dt.Columns.containsMatchCase(attr.getKey().toLowerCase())) {
										row.SetValByKey(attr.getKey(), dr.getValue(attr.getKey().toLowerCase()));
									}
									else if (dt.Columns.containsMatchCase(attr.getKey().toUpperCase())) {
										row.SetValByKey(attr.getKey(), dr.getValue(attr.getKey().toUpperCase()));
									}
									else
										row.SetValByKey(attr.getKey(), dr.getValue(attr.getKey().toLowerCase()));
								}
								else
									row.SetValByKey(attr.getKey(), dr.getValue(attr.getKey().toLowerCase()));
							}

						}
						else
							row.SetValByKey(attr.getKey(), dr.getValue(attr.getKey()));
					}
					en.setRow(row);
					ens.AddEntity(en);
				}
			} catch (RuntimeException ex) {
				// warning 不应该出现的错误. 2011-12-03 add
				String cols = "";
				for (DataColumn dc : dt.Columns) {
					cols += " , " + dc.ColumnName;
				}
				throw new RuntimeException("Columns=" + cols + "@Ens=" + ens.toString() + " @异常信息:" + ex.getMessage());
			}

			return ens;
		}
		Row row = null;
		for (DataRow dr : dt.Rows) {
			Entity en = ens.getNewEntity();
			row = new Row();
			for (String str : fullAttrs) {
				if (caseModel== FieldCaseModel.UpperCase){
					if(SystemConfig.getAppCenterDBType() == DBType.KingBaseR3
						&& dt.Columns.containsMatchCase(str)==true)
						row.SetValByKey(str, dr.getValue(str));
					else
						row.SetValByKey(str, dr.getValue(str.toUpperCase()));
				}else if(caseModel == FieldCaseModel.Lowercase) {
					if(dt.Columns.containsMatchCase(str)==true)
						row.SetValByKey(str, dr.getValue(str));
					else
						row.SetValByKey(str, dr.getValue(str.toLowerCase()));
				}
				else
					row.SetValByKey(str, dr.getValue(str));

			}
			en.setRow(row);
			ens.AddEntity(en);
		}

		return ens;
	}
}
