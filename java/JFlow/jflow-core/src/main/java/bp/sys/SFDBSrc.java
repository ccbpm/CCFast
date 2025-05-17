package bp.sys;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.en.*;
import bp.en.Map;
import bp.tools.StringUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import static bp.da.DBType.*;
import static bp.sys.DBSrcType.Oracle;

/**
 数据源
*/
public class SFDBSrc extends EntityNoName
{

		///#region 属性
	public final FieldCaseModel getFieldCaseModel() throws Exception {
		switch (this.getDBSrcType())
		{
			case Oracle:
			case bp.sys.DBSrcType.DM:
			case DBSrcType.GBASE8CByOracle:
				return FieldCaseModel.UpperCase;
			case bp.sys.DBSrcType.PostgreSQL:
			case bp.sys.DBSrcType.UX:
			case bp.sys.DBSrcType.HGDB:
				return FieldCaseModel.Lowercase;
			case bp.sys.DBSrcType.KingBaseR6:
				return FieldCaseModel.UpperCase;
			case bp.sys.DBSrcType.KingBaseR3:
				String sql = "show case_sensitive;";
				String caseSen = "";
				try
				{
					caseSen = this.RunSQLReturnString(sql);
				}
				catch (RuntimeException ex)
				{
					sql = "show enable_ci;";
					caseSen = this.RunSQLReturnString(sql);
					if ("on".equals(caseSen))
					{
						return FieldCaseModel.None;
					}
					else
					{
						return FieldCaseModel.UpperCase;
					}
				}
				if ("on".equals(caseSen))
				{
					return FieldCaseModel.UpperCase;
				}
				else
				{
					return FieldCaseModel.None;
				}
			default:
				return FieldCaseModel.None;
		}
	}
	/**
	 数据库类型
	*/
	public final String getDBSrcType()  {
		return this.GetValStringByKey(SFDBSrcAttr.DBSrcType);
	}
	public final void setDBSrcType(String value){
		this.SetValByKey(SFDBSrcAttr.DBSrcType, value);
	}

	public final String getDBName()  {
		return this.GetValStringByKey(SFDBSrcAttr.DBName);
	}
	public final void setDBName(String value){
		this.SetValByKey(SFDBSrcAttr.DBName, value);
	}
	public final String getIP()  {
		return this.GetValStringByKey(SFDBSrcAttr.IP);
	}
	public final void setIP(String value){
		this.SetValByKey(SFDBSrcAttr.IP, value);
	}

	/**
	 数据库类型
	*/
	public final DBType getHisDBType()  {
		switch (this.getDBSrcType())
		{
			case bp.sys.DBSrcType.local:
				return bp.difference.SystemConfig.getAppCenterDBType();
			case bp.sys.DBSrcType.MSSQL:
				return MSSQL;
			case Oracle:
				return DBType.Oracle;
			case bp.sys.DBSrcType.DM:
				return DM;
			case bp.sys.DBSrcType.KingBaseR3:
				return KingBaseR3;
			case bp.sys.DBSrcType.KingBaseR6:
				return KingBaseR6;
			case bp.sys.DBSrcType.MySQL:
				return MySQL;
			case bp.sys.DBSrcType.Informix:
				return Informix;
			case bp.sys.DBSrcType.PostgreSQL:
				return PostgreSQL;
			case bp.sys.DBSrcType.HGDB:
				return HGDB;
			case DBSrcType.GBASE8CByOracle:
				return GBASE8CByOracle;
			case DBSrcType.GBASE8CByMySQL:
				return GBASE8CByMySQL;
			case DBSrcType.GBASE8A:
				return GBASE8A;
			default:
				throw new RuntimeException("err@HisDBType没有判断的数据库类型.");
		}
	}

		///#endregion


		///#region 数据库访问方法
	/**
	 运行SQL返回数值

	 @param sql 一行一列的SQL
	 @param isNullAsVal 如果为空，就返回制定的值.
	 @return 要返回的值
	*/
	public final int RunSQLReturnInt(String sql, int isNullAsVal) throws Exception {
		DataTable dt = this.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			return isNullAsVal;
		}
		return Integer.parseInt(dt.Rows.get(0).getValue(0).toString());
	}


	public final Entities DoQuery(Entities ens, String sql, String expPageSize, String pk, Attrs attrs, int count, int pageSize, int pageIdx, String orderBy) throws Exception {
		return DoQuery(ens, sql, expPageSize, pk, attrs, count, pageSize, pageIdx, orderBy, false);
	}

	public final Entities DoQuery(Entities ens, String sql, String expPageSize, String pk, Attrs attrs, int count, int pageSize, int pageIdx, String orderBy, boolean isDesc) throws Exception {
		DataTable dt = new DataTable();
		if (count == 0)
		{
			return null;
		}
		int pageNum = 0;
		String orderBySQL = "";
		//如果没有加入排序字段，使用主键
		if (DataType.IsNullOrEmpty(orderBy) == false)
		{
			orderBy = pk;
			String isDescStr = "";
			if (isDesc)
			{
				isDescStr = " DESC ";
			}
			orderBySQL = orderBy + isDescStr;
		}
		sql = sql + " " + orderBySQL;
		try
		{
			if (pageSize == 0)
			{
				pageSize = 10;
			}
			if (pageIdx == 0)
			{
				pageIdx = 1;
			}
			int top = pageSize * (pageIdx - 1) + 1;
			int max = pageSize * pageIdx;
			int myleftCount = count - (pageNum * pageSize);
			String mysql = "";
			switch (this.getDBSrcType())
			{
				case Oracle:
				case bp.sys.DBSrcType.DM:
				case bp.sys.DBSrcType.KingBaseR3:
				case bp.sys.DBSrcType.KingBaseR6:
				case DBSrcType.GBASE8CByOracle:
					mysql = "SELECT * FROM (" + sql + " AND ROWNUM<=" + max + ") temp WHERE temp.rn>=" + top;
					break;
				case bp.sys.DBSrcType.MySQL:
				case DBSrcType.GBASE8A:
				case DBSrcType.GBASE8CByMySQL:
					mysql = sql + " LIMIT " + pageSize * (pageIdx - 1) + "," + pageSize;
					break;
				case bp.sys.DBSrcType.PostgreSQL:
				case bp.sys.DBSrcType.HGDB:
				case bp.sys.DBSrcType.UX:
				case bp.sys.DBSrcType.MSSQL:
				default:
					//获取主键的类型
					Attr attr = attrs.GetAttrByKeyOfEn(pk);

					//mysql = countSql;
					//mysql = mysql.Substring(mysql.ToUpper().IndexOf("FROM "));
					// mysql = "SELECT  "+ mainTable+pk + " "  + mysql;
					String pks = this.GenerPKsByTableWithPara(pk, attr.getItIsNum(), expPageSize, pageSize * (pageIdx - 1), max, null);

					if (pks == null)
					{
						mysql = sql + " AND 1=2 ";
					}
					else
					{
						mysql = sql + " AND OID in(" + pks + ")";
					}
					break;
			}
			dt = this.RunSQLReturnTable(mysql);
			return InitEntitiesByDataTable(ens, dt, null);

		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("err@数据源执行分页SQL出现错误：" + sql + "错误原因:" + ex.getMessage());
		}
	}

	public final Entities InitEntitiesByDataTable(Entities ens, DataTable dt, String[] fullAttrs) throws Exception {
		if (fullAttrs == null)
		{
			Map enMap = ens.getNewEntity().getEnMap();
			Attrs attrs = enMap.getAttrs();
			try
			{

				for (DataRow dr : dt.Rows)
				{
					Entity en = ens.getNewEntity();
					for (Attr attr : attrs)
					{
						if (dt.Columns.contains(attr.getKey()) == false && dt.Columns.contains(attr.getKey().toUpperCase()) == false)
						{
							continue;
						}
						if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
						{
							if (attr.getMyFieldType() == FieldType.RefText)
							{
								en.SetValByKey(attr.getKey(), dr.getValue(attr.getKey()));
							}
							else
							{
								en.SetValByKey(attr.getKey(), dr.getValue(attr.getKey().toUpperCase()));
							}
						}
						else if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
						{
							if (attr.getMyFieldType() == FieldType.RefText)
							{
								en.SetValByKey(attr.getKey(), dr.getValue(attr.getKey()));
							}
							else
							{
								en.SetValByKey(attr.getKey(), dr.getValue(attr.getKey().toLowerCase()));
							}
						}
						else
						{
							en.SetValByKey(attr.getKey(), dr.getValue(attr.getKey()));
						}
					}
					ens.AddEntity(en);
				}
			}
			catch (RuntimeException ex)
			{
				// warning 不应该出现的错误. 2011-12-03 add
				String cols = "";
				for (DataColumn dc : dt.Columns)
				{
					cols += " , " + dc.ColumnName;
				}
				throw new RuntimeException("Columns=" + cols + "@Ens=" + ens.toString() + " @异常信息:" + ex.getMessage());
			}

			return ens;
		}

		for (DataRow dr : dt.Rows)
		{
			Entity en = ens.getNewEntity();
			for (String str : fullAttrs)
			{
				if (dt.Columns.contains(str) == false && dt.Columns.contains(str.toUpperCase()) == false)
				{
					continue;
				}
				if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
				{
					if (dt.Columns.contains(str) == true)
					{
						en.SetValByKey(str, dr.getValue(str));
					}
					else
					{
						en.SetValByKey(str, dr.getValue(str.toUpperCase()));
					}
				}
				else if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
				{
					if (dt.Columns.contains(str) == true)
					{
						en.SetValByKey(str, dr.getValue(str));
					}
					else
					{
						en.SetValByKey(str, dr.getValue(str.toLowerCase()));
					}
				}

				else
				{
					en.SetValByKey(str, dr.getValue(str));
				}

			}
			ens.AddEntity(en);
		}

		return ens;

	}

	public final String GenerPKsByTableWithPara(String pk, boolean isNum, String sql, int from, int to, Paras paras) throws Exception {
		DataTable dt = this.RunSQLReturnTable(sql, paras);
		String pks = "";
		int i = 0;
		int paraI = 0;

		String dbStr = bp.difference.SystemConfig.getAppCenterDBVarStr();
		for (DataRow dr : dt.Rows)
		{
			i++;
			if (i > from)
			{


				if (isNum == true)
				{
					pks += Integer.parseInt(dr.getValue(pk).toString()) + ",";
				}
				else
				{
					pks += "'" + dr.getValue(pk).toString() + "',";
				}
				if (i >= to)
				{
					return pks.substring(0, pks.length() - 1);
				}
			}
		}
		if (Objects.equals(pks, ""))
		{
			return null;
		}
		return pks.substring(0, pks.length() - 1);
	}
	/**
	 运行SQL

	 @param sql
	 @return
	*/
	public final int RunSQL(String sql) throws Exception {
		int i =0;
		Connection conn = null;
		Statement stmt = null;
		switch(this.getDBSrcType()){
			case bp.sys.DBSrcType.local:
				return DBAccess.RunSQL(sql);
			case Oracle:
			case bp.sys.DBSrcType.DM:
			case bp.sys.DBSrcType.KingBaseR3:
			case bp.sys.DBSrcType.KingBaseR6:
			case bp.sys.DBSrcType.MySQL:
			case bp.sys.DBSrcType.MSSQL:
			case DBSrcType.GBASE8CByOracle:
			case DBSrcType.GBASE8CByMySQL:
			case DBSrcType.GBASE8A:
				conn =this.getConnection();
				try{
					stmt = conn.createStatement();// 创建用于执行静态sql语句的Statement对象，st属局部变量
					i = stmt.executeUpdate(sql);
					return i;
				}catch (Exception ex) {
					String msg = "@运行外部数据"+this.getDBName()+"SQL报错。\n  @SQL: " + sql + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
					Log.DefaultLogWriteLineError(msg);
					throw new RuntimeException(msg, ex);
				}finally {
					closeAll(conn,stmt,null);
				}
			default:
				throw new Exception("err@没有处理数据库"+this.getDBSrcType()+"类型");
		}
	}

	/**
	 * 解析连接字符
	 * @return
	 * @throws Exception
	 */
	public final Hashtable getPairs() throws Exception {
		Hashtable ht = new Hashtable();
		if(bp.sys.DBSrcType.local.equals(this.getDBSrcType())){
			return ht;
		}
		String connString = this.getConnString();
		if(DataType.IsNullOrEmpty(connString) == true){
			return ht;
		}
		//字符串格式 IP=127.0.0.1;Port=3306;DBName=jflow;Username=root;Password=ccflow@123;
		String[] pairs  = connString.split(";");
		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			if (keyValue.length == 2) {
				String key = keyValue[0];
				String value = keyValue[1];
				ht.put(key,value);
			}
		}
		return ht;
	}
	/**
	 连接字符串.
	 * @throws Exception
	 */
	public final Connection getConnection() throws Exception
	{
		String connString = this.getConnString();
		if(DataType.IsNullOrEmpty(connString) == true)
			throw new Exception("err@请配置连接的字符串");
		Hashtable ht = getPairs();
		String ip = ht.get("IP") == null ? ht.get("Data Source").toString() : ht.get("IP").toString();
		String port = ht.get("Port").toString();
		String dbname = ht.get("DBName") == null ? ht.get("Initial Catalog").toString() : ht.get("DBName").toString();
		String username = ht.get("Username") == null ? ht.get("User ID").toString() : ht.get("Username").toString();
		String password = ht.get("Password").toString();
		String url = "";//数据库连接的url
		Connection con = null;
		switch (this.getDBSrcType())
		{
			case bp.sys.DBSrcType.MSSQL:
				try{
					//加载MySql的驱动类
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver") ;
				}catch(ClassNotFoundException e){
					System.out.println("找不到驱动程序类 ，加载驱动失败！");
					e.printStackTrace() ;
				}
				url="jdbc:sqlserver://" + ip+":"+port+"/"+dbname+";useLOBs=false";
				break;
			case bp.sys.DBSrcType.Oracle:
				try{
					//加载MySql的驱动类
					Class.forName("oracle.jdbc.driver.OracleDriver") ;
				}catch(ClassNotFoundException e){
					System.out.println("找不到驱动程序类 ，加载驱动失败！");
					e.printStackTrace() ;
				}
				url="jdbc:oracle:thin:@" + ip+":"+port+"/"+dbname;
				break;
			case bp.sys.DBSrcType.MySQL:
				try{
					//加载MySql的驱动类
					Class.forName("com.mysql.jdbc.Driver") ;
				}catch(ClassNotFoundException e){
					System.out.println("找不到驱动程序类 ，加载驱动失败！");
					e.printStackTrace() ;
				}
				url="jdbc:mysql://"+ip+":"+port+"/"+dbname+"?useUnicode=true&characterEncoding=utf-8&useOldAliasMetadataBehavior=true&allowMultiQueries=true";
				break;
			case DBSrcType.GBASE8CByOracle:
			case DBSrcType.GBASE8CByMySQL:
				try{
					//加载GBASE8C的驱动类
					Class.forName("org.opengauss.Driver") ;
				}catch(ClassNotFoundException e){
					System.out.println("找不到驱动程序类 ，加载驱动失败！");
					e.printStackTrace() ;
				}
				url="jdbc.opengauss://" + ip+":"+port+"/"+dbname;
				break;
			case DBSrcType.GBASE8A:
				try{
					//加载GBASE8C的驱动类
					Class.forName("com.gbase.jdbc.Driver") ;
				}catch(ClassNotFoundException e){
					System.out.println("找不到驱动程序类 ，加载驱动失败！");
					e.printStackTrace() ;
				}
				url="jdbc.gbase://" + ip+":"+port+"/"+dbname;
				break;
			case bp.sys.DBSrcType.KingBaseR3:
			case bp.sys.DBSrcType.KingBaseR6:
				try{
					//加载MySql的驱动类
					Class.forName("com.kingbase8.Driver") ;
				}catch(ClassNotFoundException e){
					System.out.println("找不到驱动程序类 ，加载驱动失败！");
					e.printStackTrace() ;
				}
				url="jdbc:kingbase8://"+ip+":"+port+"/"+dbname;
				break;

			case DBSrcType.HGDB:
				try{
					Class.forName("com.highgo.jdbc.Driver") ;
				}catch(ClassNotFoundException e){
					System.out.println("找不到驱动程序类 ，加载驱动失败！");
					e.printStackTrace() ;
				}
				url="jdbc:highgo://"+ip+":"+port+"/"+dbname;
				break;
			case DBSrcType.PostgreSQL:
				try{
					Class.forName("org.postgresql.Driver") ;
				}catch(ClassNotFoundException e){
					System.out.println("找不到驱动程序类 ，加载驱动失败！");
					e.printStackTrace() ;
				}
				url="jdbc:postgresql://"+ip+":"+port+"/"+dbname+"?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&useSSL=false&allowMultiQueries=true";
				break;
			default:
				throw new RuntimeException("@没有判断的类型.");
		}
		con = DriverManager.getConnection(url,username,password);
		return con;
	}
	private  void closeAll(Connection conn, Statement st, ResultSet rs){
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(st!=null){
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public final void RunSQLs(String sql) throws Exception {
		if (DataType.IsNullOrEmpty(sql))
		{
			return;
		}

		//sql = DealSQL(sql);//去掉注释.

		sql = sql.replace("@GO", "~");
		sql = sql.replace("@", "~");

		if (sql.contains("';'") == false)
		{
			sql = sql.replace(";", "~");
		}

		sql = sql.replace("UPDATE", "~UPDATE");
		sql = sql.replace("DELETE", "~DELETE");
		sql = sql.replace("INSERT", "~INSERT");

		String[] strs = sql.split("[~]", -1);
		for (String str : strs)
		{
			if (DataType.IsNullOrEmpty(str))
			{
				continue;
			}

			if (str.contains("--") || str.contains("/*"))
			{
				continue;
			}

			RunSQL(str);
		}
	}

	private static final Object _lock = new Object();
	/**
	 运行SQL

	 @param runObj
	 @return
	*/
	public final DataTable RunSQLReturnTable(String runObj) throws Exception {
		DataTable dt = RunSQLReturnTable(runObj, new Paras());
		return dt;

	}


	public final String RunSQLReturnString(String runObj) throws Exception {
		return RunSQLReturnString(runObj, null);
	}

	public final String RunSQLReturnString(String runObj, String isNullasVal) throws Exception {

		DataTable dt = RunSQLReturnTable(runObj);
		if (dt.Rows.size() == 0)
		{
			return isNullasVal;
		}

		return dt.Rows.get(0).getValue(0).toString();
	}


	/**
	 运行SQL返回datatable

	 @param runObj
	 @return
	*/
	public final DataTable RunSQLReturnTable(String runObj, Paras ps) throws Exception {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		switch(this.getDBSrcType()){
			case DBSrcType.local:
				return DBAccess.RunSQLReturnTable(runObj,ps);
			case DBSrcType.Oracle:
			case DBSrcType.KingBaseR3:
			case DBSrcType.KingBaseR6:
			case DBSrcType.MySQL:
			case DBSrcType.MSSQL:
			case DBSrcType.GBASE8CByOracle:
			case DBSrcType.GBASE8CByMySQL:
			case DBSrcType.GBASE8A:
				conn =this.getConnection();
				try{
					DataTable oratb = new DataTable("otb");
					if (null != ps && ps.size() > 0) {
						pstmt = new NamedParameterStatement(conn, runObj);
						PrepareCommand(pstmt, ps);
						// pstmt.setString(1, "李思");
						rs = pstmt.executeQuery();
						ResultSetMetaData rsmd = rs.getMetaData();
						int size = rsmd.getColumnCount();
						for (int i = 0; i < size; i++) {
							oratb.Columns.Add(rsmd.getColumnName(i + 1), Para.getDAType(rsmd.getColumnType(i + 1)));
						}
						while (rs.next()) {
							DataRow dr = oratb.NewRow();// 產生一列DataRow
							for (int i = 0; i < size; i++) {
								Object val = rs.getObject(i + 1);
								if (dr != null && dr.columns.size() > 0 && dr.columns.get(i).DataType != null) {
									if (dr.columns.get(i).DataType.toString().contains("Integer")
											|| dr.columns.get(i).DataType.toString().contains("Double")) {
										if (val == null) {
											dr.setValue(i, 0);
										} else {
											dr.setValue(i, val);
										}

									} else {
										dr.setValue(i, val);
									}
								} else {
									dr.setValue(i, val);
								}
								// dr.setDataType(i, Para.getDAType(val));
							}
							oratb.Rows.add(dr);// DataTable加入此DataRow
						}
					} else {
						stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
						rs = stmt.executeQuery(runObj);
						ResultSetMetaData rsmd = rs.getMetaData();
						int size = rsmd.getColumnCount();
						for (int i = 0; i < size; i++) {
							oratb.Columns.Add(rsmd.getColumnName(i + 1), Para.getDAType(rsmd.getColumnType(i + 1)));
						}
						while (rs.next()) {
							DataRow dr = oratb.NewRow();// 產生一列DataRow
							for (int i = 0; i < size; i++) {
								Object val = rs.getObject(i + 1);
								if (dr != null && dr.columns.size() > 0 && dr.columns.get(i).DataType != null) {
									if (dr.columns.get(i).DataType.toString().contains("Integer")
											|| dr.columns.get(i).DataType.toString().contains("Double")) {
										if (val == null) {
											dr.setValue(i, 0);
										} else {
											dr.setValue(i, val);
										}

									} else {
										dr.setValue(i, val);
									}
								} else {
									dr.setValue(i, val);
								}
							}
							oratb.Rows.add(dr);// DataTable加入此DataRow
						}
					}
					if (Log.isLoggerDebugEnabled()) {
						Log.DefaultLogWriteLineDebug("SQL: " + runObj);
						Log.DefaultLogWriteLineDebug("Param: " + ps.getDebugInfo() + ", Result: Rows=" + oratb.Rows.size());
					}
					return oratb;
				}catch (Exception ex) {
					String msg = "@运行外部数据"+this.getDBName()+"SQL报错。\n  @SQL: " + runObj + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
					Log.DefaultLogWriteLineError(msg);
					throw new RuntimeException(msg, ex);
				}finally {
					closeAll(conn,stmt,null);
				}
			case DBSrcType.WebApi:
				//执行Url
				try
				{
					String host = this.getConnString();
					String json = bp.tools.PubGlo.HttpGet(host + runObj);
					if (DataType.IsNullOrEmpty(json))
						return new DataTable();
					return bp.tools.Json.ToDataTable(json);
				}
				catch(Exception ex)
				{
					throw new RuntimeException(ex.getMessage());
				}
			default:
				throw new Exception("err@没有处理数据库"+this.getDBSrcType()+"类型");
		}

	}
	private static void PrepareCommand(NamedParameterStatement ps, Paras params) throws Exception {
		if (null == params || params.size() <= 0) {
			return;
		}
		try {
			for (Para para : params) {
				if (para.DAType == String.class) {
					try {
						if (para.val != null && !para.val.equals("")) {
							ps.setString(para.ParaName, String.valueOf(para.val));
						} else {
							ps.setString(para.ParaName, "");
						}
					} catch (Exception e) {
						ps.setString(para.ParaName, "");
					}
				} else if (para.DAType == Long.class) {
					ps.setLong(para.ParaName, (Long) para.val);
				} else if (para.DAType == Integer.class) {
					// if ("".equals(para.val))
					// para.val = "0";
					ps.setInt(para.ParaName,
							para.val instanceof String ? Integer.parseInt("" + para.val) : (Integer) para.val);
				} else if (para.DAType == Float.class) {
					ps.setFloat(para.ParaName, (Float) para.val);
				} else if (para.DAType == Double.class) {
					ps.setDouble(para.ParaName, (Double) para.val);
				} else if (para.DAType == BigDecimal.class) {
					ps.setBigDecimal(para.ParaName, (BigDecimal) para.val);
				} else if (para.DAType == java.util.Date.class) {
					java.util.Date date = (java.util.Date) para.val;
					if (date == null) {
						ps.setDate(para.ParaName, null);
					} else {
						ps.setDate(para.ParaName, new java.sql.Date(date.getTime()));
					}
				} else if (para.DAType == Boolean.class) {
					ps.setBoolean(para.ParaName, (Boolean) para.val);
				}
			}
		} catch (SQLException ex) {
			String msg = "@运行查询在(PrepareCommand)出错  @异常信息：" + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg, ex);
		}
	}
	public final DataTable RunSQLReturnTable(String sql, int startRecord, int recordCount) throws Exception {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		switch(this.getDBSrcType()){
			case DBSrcType.local:
				return DBAccess.RunSQLReturnTable(sql);
			case DBSrcType.Oracle:
			case DBSrcType.KingBaseR3:
			case DBSrcType.KingBaseR6:
			case DBSrcType.MySQL:
			case DBSrcType.MSSQL:
			case DBSrcType.GBASE8CByOracle:
			case DBSrcType.GBASE8CByMySQL:
			case DBSrcType.GBASE8A:
				conn =this.getConnection();
				try{
					DataTable oratb = new DataTable("otb");
					stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					rs = stmt.executeQuery(sql);
					ResultSetMetaData rsmd = rs.getMetaData();
					int size = rsmd.getColumnCount();
					for (int i = 0; i < size; i++) {
						oratb.Columns.Add(rsmd.getColumnName(i + 1), Para.getDAType(rsmd.getColumnType(i + 1)));
					}
					int count = 1;
					while (rs.next()) {
						if(count<startRecord)
							continue;
						if(count>recordCount)
							continue;
						count++;
						DataRow dr = oratb.NewRow();// 產生一列DataRow
						for (int i = 0; i < size; i++) {
							Object val = rs.getObject(i + 1);
							if (dr != null && dr.columns.size() > 0 && dr.columns.get(i).DataType != null) {
								if (dr.columns.get(i).DataType.toString().contains("Integer")
										|| dr.columns.get(i).DataType.toString().contains("Double")) {
									if (val == null) {
										dr.setValue(i, 0);
									} else {
										dr.setValue(i, val);
									}

								} else {
									dr.setValue(i, val);
								}
							} else {
								dr.setValue(i, val);
							}
						}
						oratb.Rows.add(dr);// DataTable加入此DataRow
					}

					if (Log.isLoggerDebugEnabled()) {
						Log.DefaultLogWriteLineDebug("SQL: " + sql);
					}
					return oratb;
				}catch (Exception ex) {
					String msg = "@运行外部数据"+this.getDBName()+"SQL报错。\n  @SQL: " + sql + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
					Log.DefaultLogWriteLineError(msg);
					throw new RuntimeException(msg, ex);
				}finally {
					closeAll(conn,stmt,null);
				}
			default:
				throw new Exception("err@没有处理数据库"+this.getDBSrcType()+"类型");
		}
	}
	/**
	 判断数据源所在库中是否已经存在指定名称的对象【表/视图】

	 @param objName 表/视图 名称
	 @return 如果不存在，返回null，否则返回对象的类型：TABLE(表)、VIEW(视图)、PROCEDURE(存储过程，判断不完善)、OTHER(其他类型)
	*/
	public final String IsExistsObj(String objName) throws Exception {
		String sql = "";
		DataTable dt = null;

		switch (this.getDBSrcType())
		{
			case bp.sys.DBSrcType.local:
				sql = GetIsExitsSQL(DBAccess.getAppCenterDBType(), objName, SystemConfig.getAppCenterDBDatabase() .toString());
				dt = DBAccess.RunSQLReturnTable(sql);
				break;
			case bp.sys.DBSrcType.MSSQL:
				sql = GetIsExitsSQL(MSSQL, objName, this.getDBName());
				dt = RunSQLReturnTable(sql);
				break;
			case Oracle:
			case bp.sys.DBSrcType.DM:
				sql = GetIsExitsSQL(DBType.Oracle, objName, this.getDBName());
				dt = RunSQLReturnTable(sql);
				break;
			case bp.sys.DBSrcType.KingBaseR3:
			case bp.sys.DBSrcType.KingBaseR6:
				sql = GetIsExitsSQL(KingBaseR3, objName, this.getDBName());
				dt = RunSQLReturnTable(sql);
				break;
			case bp.sys.DBSrcType.MySQL:
				sql = GetIsExitsSQL(MySQL, objName, this.getDBName());
				dt = RunSQLReturnTable(sql);
				break;
			case bp.sys.DBSrcType.Informix:
				sql = GetIsExitsSQL(Informix, objName, this.getDBName());
				dt = RunSQLReturnTable(sql);
				break;
			case DBSrcType.GBASE8CByOracle:
				sql = GetIsExitsSQL(GBASE8CByOracle, objName, this.getDBName());
				dt = RunSQLReturnTable(sql);
				break;
			case DBSrcType.GBASE8CByMySQL:
				sql = GetIsExitsSQL(GBASE8CByMySQL, objName, this.getDBName());
				dt = RunSQLReturnTable(sql);
				break;
			case DBSrcType.GBASE8A:
				sql = GetIsExitsSQL(GBASE8A, objName, this.getDBName());
				dt = RunSQLReturnTable(sql);
				break;
			default:
				throw new RuntimeException("@未涉及的数据库类型。");
		}

		return dt.Rows.size() == 0 ? null : dt.Rows.get(0).getValue(0).toString();
	}

	/**
	 获取判断数据库中是否存在指定名称的表/视图SQL语句

	 @param dbType 数据库类型
	 @param objName 表/视图名称
	 @param dbName 数据库名称
	 @return
	*/
	public final String GetIsExitsSQL(DBType dbType, String objName, String dbName)
	{
		switch (dbType)
		{
			case MSSQL:
			case PostgreSQL:
			case UX:
			case HGDB:
				return String.format("SELECT (CASE s.xtype WHEN 'U' THEN 'TABLE' WHEN 'V' THEN 'VIEW' WHEN 'P' THEN 'PROCEDURE' ELSE 'OTHER' END) OTYPE FROM sysobjects s WHERE s.name = '%1$s'", objName);
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				return String.format("SELECT uo.OBJECT_TYPE OTYPE FROM user_objects uo WHERE uo.OBJECT_NAME = '%1$s'", objName.toUpperCase());
			case MySQL:
			case GBASE8CByMySQL:
			case GBASE8A:
				return String.format("SELECT (CASE t.TABLE_TYPE WHEN 'BASE TABLE' THEN 'TABLE' ELSE 'VIEW' END) OTYPE FROM information_schema.tables t WHERE t.TABLE_SCHEMA = '%2$s' AND t.TABLE_NAME = '%1$s'", objName, dbName);
			case Informix:
				return String.format("SELECT (CASE s.tabtype WHEN 'T' THEN 'TABLE' WHEN 'V' THEN 'VIEW' ELSE 'OTHER' END) OTYPE FROM systables s WHERE s.tabname = '%1$s'", objName);
			case DB2:
				return String.format("");
			case Access:
				return String.format("");
			default:
				throw new RuntimeException("@没有涉及的数据库类型。");
		}
	}

		///#endregion


		///#region 构造方法
	/**
	 编辑类型
	*/
	public final int getEditType() {
		return this.GetParaInt("EditType", 0);
	}
	public final void setEditType(int value)  {
		this.SetPara("EditType", value);
	}
	/**
	 数据源
	*/
	public SFDBSrc()
	{
	}
	public SFDBSrc(String no) throws Exception {
		this.setNo(no);
		try
		{
			this.Retrieve();
		}
		catch (Exception ex)
		{
			this.CheckPhysicsTable();
			if (no.equals("local") == true)
			{
				this.setName(no);
				this.setDBSrcType(no);
				this.setDBName(no);
				this.Insert();
				return;
			}
			throw ex;
		}
	}
	/**
	 EnMap
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_SFDBSrc", "数据源");

		map.AddTBStringPK(SFDBSrcAttr.No, null, "编号", true, false, 1, 20, 20);
		map.AddTBString(SFDBSrcAttr.Name, null, "名称", true, false, 0, 30, 20);
		//String cfg = "@0=应用系统主数据库(默认)@1=SQLServer数据库@2=Oracle数据库@3=MySQL数据库@4=Informix数据库@50=Dubbo服务@100=WebService数据源";
		//map.AddDDLSysEnum(SFDBSrcAttr.DBSrcType, 0, "数据源类型", true, true,
		//  SFDBSrcAttr.DBSrcType,cfg);

		String cfg1 = "@local=应用系统数据库(默认)@MSSQL=SQLServer数据库@Oracle=Oracle数据库@MySQL=MySQL数据库@Informix=Informix数据库@KindingBase3=人大金仓库R3@KindingBase6=人大金仓库R6@UX=优漩@Dubbo=Dubbo服务@WS=WebService数据源@WebApi=WebApi@CCFromRef.js";

		map.AddDDLStringEnum(SFDBSrcAttr.DBSrcType, "local", "类型", cfg1, true, null, false);
		map.AddTBString(SFDBSrcAttr.DBName, null, "数据库名称/Oracle保持为空", true, false, 0, 30, 20);
		map.AddTBString(SFDBSrcAttr.ConnString, null, "连接串/URL", true, false, 0, 200, 20, true);
		map.AddTBAtParas(200);

		RefMethod rm = new RefMethod();

		rm = new RefMethod();
		rm.Title = "测试连接";
		rm.ClassMethodName = this.toString() + ".DoConn";
		rm.refMethodType = RefMethodType.Func; // 仅仅是一个功能.
		map.AddRefMethod(rm);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 方法.
	/**
	 连接字符串.
	*/
	public final String getConnString() throws Exception {
		switch (this.getDBSrcType())
		{
			case bp.sys.DBSrcType.local:
				return bp.difference.SystemConfig.getAppCenterDSN();
			default:
				return this.GetValStringByKey(SFDBSrcAttr.ConnString);
		}
	}
	/**
	 执行连接

	 @return
	*/
	public final String DoConn() throws Exception {
		if (this.getNo().equals("local"))
			return "本地连接不需要测试.";
		if (this.getDBSrcType().equals(bp.sys.DBSrcType.local))
			return "@在该系统中只能有一个本地连接.";
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		closeAll(conn,stmt,null);
		return "恭喜您，该(" + this.getName() + ")连接配置成功。";

	}
	/**
	 获取所有数据表，不包括视图

	 @return
	*/
	public final DataTable GetAllTablesWithoutViews() throws Exception {
		StringBuilder sql = new StringBuilder();
		String dbType = this.getDBSrcType();
		if (Objects.equals(dbType, bp.sys.DBSrcType.local))
		{
			switch (bp.difference.SystemConfig.getAppCenterDBType())
			{
				case MSSQL:
					dbType = bp.sys.DBSrcType.MSSQL;
					break;
				case Oracle:
					dbType = Oracle;
					break;
				case DM:
					dbType = bp.sys.DBSrcType.DM;
					break;
				case MySQL:
					dbType = bp.sys.DBSrcType.MySQL;
					break;
				case Informix:
					dbType = bp.sys.DBSrcType.Informix;
					break;
				case PostgreSQL:
					dbType = bp.sys.DBSrcType.PostgreSQL;
					break;
				case UX:
					dbType = bp.sys.DBSrcType.UX;
					break;
				case KingBaseR3:
					dbType = bp.sys.DBSrcType.KingBaseR3;
					break;
				case KingBaseR6:
					dbType = bp.sys.DBSrcType.KingBaseR6;
					break;
				case HGDB:
					dbType = bp.sys.DBSrcType.HGDB;
					break;
				case GBASE8CByOracle:
					dbType = bp.sys.DBSrcType.GBASE8CByOracle;
					break;
				case GBASE8CByMySQL:
					dbType = bp.sys.DBSrcType.GBASE8CByMySQL;
					break;
				case GBASE8A:
					dbType = bp.sys.DBSrcType.GBASE8A;
					break;
				default:
					throw new RuntimeException("没有涉及到的连接测试类型...");
			}
		}

		switch (dbType)
		{
			case bp.sys.DBSrcType.MSSQL:
				sql.append("SELECT NAME AS No," + "\r\n");
				sql.append("       NAME" + "\r\n");
				sql.append("FROM   sysobjects" + "\r\n");
				sql.append("WHERE  xtype = 'U'" + "\r\n");
				sql.append("ORDER BY" + "\r\n");
				sql.append("       Name" + "\r\n");
				break;
			case Oracle:
			case bp.sys.DBSrcType.DM:
			case bp.sys.DBSrcType.KingBaseR3:
			case bp.sys.DBSrcType.KingBaseR6:
			case DBSrcType.GBASE8CByOracle:
				sql.append("SELECT uo.OBJECT_NAME No," + "\r\n");
				sql.append("       uo.OBJECT_NAME Name" + "\r\n");
				sql.append("  FROM user_objects uo" + "\r\n");
				sql.append(" WHERE uo.OBJECT_TYPE = 'TABLE'" + "\r\n");
				sql.append(" ORDER BY uo.OBJECT_NAME" + "\r\n");
				break;
			case bp.sys.DBSrcType.MySQL:
			case DBSrcType.GBASE8CByMySQL:
			case DBSrcType.GBASE8A:
				sql.append("SELECT " + "\r\n");
				sql.append("    table_name No," + "\r\n");
				sql.append("    table_name Name" + "\r\n");
				sql.append("FROM" + "\r\n");
				sql.append("    information_schema.tables" + "\r\n");
				sql.append("WHERE" + "\r\n");
				sql.append(String.format("    table_schema = '%1$s'", Objects.equals(this.getDBSrcType(), bp.sys.DBSrcType.local) ? SystemConfig.getAppCenterDBDatabase()  :this.getDBName()) + "\r\n");
				sql.append("        AND table_type = 'BASE TABLE'" + "\r\n");
				sql.append("ORDER BY table_name;" + "\r\n");
				break;
			case bp.sys.DBSrcType.Informix:
				sql.append("" + "\r\n");
				break;
			case bp.sys.DBSrcType.PostgreSQL:
			case bp.sys.DBSrcType.UX:
			case bp.sys.DBSrcType.HGDB:
				sql.append("SELECT " + "\r\n");
				sql.append("    table_name No," + "\r\n");
				sql.append("    table_name Name" + "\r\n");
				sql.append("FROM" + "\r\n");
				sql.append("    information_schema.tables" + "\r\n");
				sql.append("WHERE" + "\r\n");
				sql.append(String.format("    table_schema = '%1$s'", Objects.equals(this.getDBSrcType(), bp.sys.DBSrcType.local) ? SystemConfig.getAppCenterDBDatabase()  :this.getDBName()) + "\r\n");
				sql.append("        AND table_type = 'BASE TABLE'" + "\r\n");
				sql.append("ORDER BY table_name;" + "\r\n");
				break;
			default:
				break;
		}

		DataTable allTables = null;
		if (Objects.equals(this.getNo(), "local"))
		{
			allTables = DBAccess.RunSQLReturnTable(sql.toString());
		}
		else
		{
			allTables = RunSQLReturnTable(sql.toString());
		}

		return allTables;
	}
	/**
	 获得数据列表.

	 @return
	*/

	public final DataTable GetTables() throws Exception {
		return GetTables(false);
	}

	public final DataTable GetTables(boolean isCutFlowTables) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(String.format("SELECT ss.SrcTable FROM Sys_SFTable ss WHERE ss.FK_SFDBSrc = '%1$s'", this.getNo()));

		DataTable allTablesExist = DBAccess.RunSQLReturnTable(sql.toString());

		sql.setLength(0);

		String dbType = this.getDBSrcType();
		if (Objects.equals(dbType, bp.sys.DBSrcType.local))
		{
			switch (bp.difference.SystemConfig.getAppCenterDBType())
			{
				case MSSQL:
					dbType = bp.sys.DBSrcType.MSSQL;
					break;
				case Oracle:
					dbType = Oracle;
					break;
				case DM:
					dbType = bp.sys.DBSrcType.DM;
					break;
				case MySQL:
					dbType = bp.sys.DBSrcType.MySQL;
					break;
				case Informix:
					dbType = bp.sys.DBSrcType.Informix;
					break;
				case PostgreSQL:
					dbType = bp.sys.DBSrcType.PostgreSQL;
					break;
				case UX:
					dbType = bp.sys.DBSrcType.UX;
					break;
				case KingBaseR3:
					dbType = bp.sys.DBSrcType.KingBaseR3;
					break;
				case KingBaseR6:
					dbType = bp.sys.DBSrcType.KingBaseR6;
					break;
				case HGDB:
					dbType = bp.sys.DBSrcType.HGDB;
					break;
				case GBASE8CByOracle:
					dbType = bp.sys.DBSrcType.GBASE8CByOracle;
					break;
				case GBASE8CByMySQL:
					dbType = bp.sys.DBSrcType.GBASE8CByMySQL;
					break;
				case GBASE8A:
					dbType = bp.sys.DBSrcType.GBASE8A;
					break;
				default:
					throw new RuntimeException("没有涉及到的连接测试类型...");
			}
		}

		switch (dbType)
		{
			case bp.sys.DBSrcType.MSSQL:
				sql.append("SELECT NAME AS No," + "\r\n");
				sql.append("       [Name] = '[' + (CASE xtype WHEN 'U' THEN '表' ELSE '视图' END) + '] ' + " + "\r\n");
				sql.append("       NAME," + "\r\n");
				sql.append("       xtype" + "\r\n");
				sql.append("FROM   sysobjects" + "\r\n");
				sql.append("WHERE  (xtype = 'U' OR xtype = 'V')" + "\r\n");
				//   sql.AppendLine("       AND (NAME NOT LIKE 'ND%')");
				sql.append("       AND (NAME NOT LIKE 'Demo_%')" + "\r\n");
				sql.append("       AND (NAME NOT LIKE 'Sys_%')" + "\r\n");
				sql.append("       AND (NAME NOT LIKE 'WF_%')" + "\r\n");
				sql.append("       AND (NAME NOT LIKE 'GPM_%')" + "\r\n");
				sql.append("ORDER BY" + "\r\n");
				sql.append("       xtype," + "\r\n");
				sql.append("       NAME" + "\r\n");
				break;
			case Oracle:
			case bp.sys.DBSrcType.DM:
			case bp.sys.DBSrcType.KingBaseR3:
			case bp.sys.DBSrcType.KingBaseR6:
			case DBSrcType.GBASE8CByOracle:
				sql.append("SELECT uo.OBJECT_NAME AS No," + "\r\n");
				sql.append("       '[' || (CASE uo.OBJECT_TYPE" + "\r\n");
				sql.append("         WHEN 'TABLE' THEN" + "\r\n");
				sql.append("          '表'" + "\r\n");
				sql.append("         ELSE" + "\r\n");
				sql.append("          '视图'" + "\r\n");
				sql.append("       END) || '] ' || uo.OBJECT_NAME AS Name," + "\r\n");
				sql.append("       CASE uo.OBJECT_TYPE" + "\r\n");
				sql.append("         WHEN 'TABLE' THEN" + "\r\n");
				sql.append("          'U'" + "\r\n");
				sql.append("         ELSE" + "\r\n");
				sql.append("          'V'" + "\r\n");
				sql.append("       END AS xtype" + "\r\n");
				sql.append("  FROM user_objects uo" + "\r\n");
				sql.append(" WHERE (uo.OBJECT_TYPE = 'TABLE' OR uo.OBJECT_TYPE = 'VIEW')" + "\r\n");
				//sql.AppendLine("   AND uo.OBJECT_NAME NOT LIKE 'ND%'");
				//sql.AppendLine("   AND uo.OBJECT_NAME NOT LIKE 'Demo_%'");
				//sql.AppendLine("   AND uo.OBJECT_NAME NOT LIKE 'Sys_%'");
				//sql.AppendLine("   AND uo.OBJECT_NAME NOT LIKE 'WF_%'");
				//sql.AppendLine("   AND uo.OBJECT_NAME NOT LIKE 'GPM_%'");
				sql.append(" ORDER BY uo.OBJECT_TYPE, uo.OBJECT_NAME" + "\r\n");
				break;
			case bp.sys.DBSrcType.MySQL:
			case DBSrcType.GBASE8CByMySQL:
			case DBSrcType.GBASE8A:
				sql.append("SELECT " + "\r\n");
				sql.append("    table_name AS No," + "\r\n");
				sql.append("    CONCAT('['," + "\r\n");
				sql.append("            CASE table_type" + "\r\n");
				sql.append("                WHEN 'BASE TABLE' THEN '表'" + "\r\n");
				sql.append("                ELSE '视图'" + "\r\n");
				sql.append("            END," + "\r\n");
				sql.append("            '] '," + "\r\n");
				sql.append("            table_name) AS Name," + "\r\n");
				sql.append("    CASE table_type" + "\r\n");
				sql.append("        WHEN 'BASE TABLE' THEN 'U'" + "\r\n");
				sql.append("        ELSE 'V'" + "\r\n");
				sql.append("    END AS xtype" + "\r\n");
				sql.append("FROM" + "\r\n");
				sql.append("    information_schema.tables" + "\r\n");
				sql.append("WHERE" + "\r\n");
				sql.append(String.format("    table_schema = '%1$s'", GetTableSchema()) + "\r\n");
				sql.append("        AND (table_type = 'BASE TABLE'" + "\r\n");
				sql.append("        OR table_type = 'VIEW')" + "\r\n");
				//   sql.AppendLine("       AND (table_name NOT LIKE 'ND%'");
				sql.append("        AND table_name NOT LIKE 'Demo_%'" + "\r\n");
				sql.append("        AND table_name NOT LIKE 'Sys_%'" + "\r\n");
				sql.append("        AND table_name NOT LIKE 'WF_%'" + "\r\n");
				sql.append("        AND table_name NOT LIKE 'GPM_%'" + "\r\n");
				sql.append("ORDER BY table_type , table_name;" + "\r\n");
				break;
			case bp.sys.DBSrcType.Informix:
				sql.append("" + "\r\n");
				break;
			case bp.sys.DBSrcType.PostgreSQL:
			case bp.sys.DBSrcType.UX:
			case bp.sys.DBSrcType.HGDB:
				sql.append("SELECT table_name AS No,CONCAT('[',CASE table_type WHEN 'BASE TABLE' THEN '表'ELSE '视图'END,'] ',table_name) " +
						"AS Name,CASE table_type WHEN 'BASE TABLE' THEN 'U'ELSE 'V' END AS xtype");
				sql.append(" FROM information_schema.tables");
				sql.append(String.format("  WHERE  table_catalog = '%1$s'", GetTableSchema()));
				sql.append(" AND (table_type = 'BASE TABLE'OR table_type = 'VIEW')AND table_name NOT LIKE 'Demo_%'AND table_name NOT LIKE 'Sys_%'");
				sql.append(" AND table_name NOT LIKE 'WF_%' AND table_name NOT LIKE 'GPM_%' AND TABLE_NAME NOT LIKE'port_%' and  table_schema NOT IN ('pg_catalog', 'information_schema')AND table_name NOT LIKE 'pg_%' AND table_name NOT LIKE 'sql_%' GROUP BY no ORDER BY table_type , table_name;");
				break;
			default:
				break;
		}

		DataTable allTables = null;
		if (Objects.equals(this.getNo(), "local"))
		{
			allTables = DBAccess.RunSQLReturnTable(sql.toString());


				///#region 把tables 的英文名称替换为中文.
			//把tables 的英文名称替换为中文.
			String mapDT = "SELECT PTable,Name FROM Sys_MapData ";
			DataTable myDT = DBAccess.RunSQLReturnTable(mapDT);
			for (DataRow myDR : allTables.Rows)
			{
				String no = myDR.getValue("No").toString();

				String name = null;
				for (DataRow dr : myDT.Rows)
				{
					String pTable = dr.getValue("PTable").toString();
					if (pTable.equals(no) == false)
					{
						continue;
					}

					name = dr.getValue("Name").toString();
					break;
				}
				if (name != null)
				{
					myDR.setValue("Name", myDR.getValue("Name").toString() + "-" + name);
				}
			}

				///#endregion 把tables 的英文名称替换为中文.


		}
		else
		{
			allTables = RunSQLReturnTable(sql.toString());
		}

		//去除已经使用的表
		String filter = "";
		for (DataRow dr : allTablesExist.Rows)
		{
			filter += String.format("No='%1$s' OR ", dr.getValue(0));
		}

		if (!Objects.equals(filter, ""))
		{
			List<DataRow>  deletedRows = allTables.select(rtrim(filter," OR "));
			for (DataRow dr : deletedRows)
			{
				allTables.Rows.remove(dr);
			}
		}

		//去掉系统表.
		if (isCutFlowTables == true)
		{
			DataTable dt = new DataTable();
			dt.Columns.Add("No", String.class);
			dt.Columns.Add("Name", String.class);

			for (DataRow dr : allTables.Rows)
			{
				String no = dr.getValue("No").toString();

				if (no.contains("WF_") || no.contains("Track") || no.contains("Sys_") || no.contains("Demo_"))
				{
					continue;
				}

				DataRow mydr = dt.NewRow();
				mydr.setValue("No", dr.getValue("No"));
				mydr.setValue("Name", dr.getValue("Name"));
				dt.Rows.add(mydr);
			}

			return dt;
		}

		return allTables;
	}
	public static String rtrim(String str,String substr){
		int j=str.length()-1;
		for(;j>-1;j--){
			if(substr.indexOf(str.charAt(j))==-1){
				break;
			}
		}
		return str.substring(0, j+1);
	}

	public final String GetTablesJSON() throws Exception {
		DataTable dt = this.GetTables(true);
		return bp.tools.Json.ToJson(dt);
	}

	/**
	 修改表/视图/列名称（不完善）

	 @param objType 修改对象的类型，TABLE(表)、VIEW(视图)、COLUMN(列)
	 @param oldName 旧名称
	 @param newName 新名称
	*/
	public final void Rename(String objType, String oldName, String newName) throws Exception {
		Rename(objType, oldName, newName, null);
	}
	public final void Rename(String objType, String oldName, String newName, String tableName) throws Exception {
		String dbType = this.getDBSrcType();
		if (Objects.equals(dbType, bp.sys.DBSrcType.local))
		{
			switch (bp.difference.SystemConfig.getAppCenterDBType())
			{
				case MSSQL:
					dbType = bp.sys.DBSrcType.MSSQL;
					break;
				case Oracle:
					dbType = Oracle;
					break;
				case DM:
					dbType = bp.sys.DBSrcType.DM;
					break;
				case MySQL:
					dbType = bp.sys.DBSrcType.MySQL;
					break;
				case Informix:
					dbType = bp.sys.DBSrcType.Informix;
					break;
				case KingBaseR3:
					dbType = bp.sys.DBSrcType.KingBaseR3;
					break;
				case KingBaseR6:
					dbType = bp.sys.DBSrcType.KingBaseR6;
					break;
				case GBASE8CByOracle:
					dbType = bp.sys.DBSrcType.GBASE8CByOracle;
					break;
				case GBASE8CByMySQL:
					dbType = bp.sys.DBSrcType.GBASE8CByMySQL;
					break;
				case GBASE8A:
					dbType = bp.sys.DBSrcType.GBASE8A;
					break;
				default:
					throw new RuntimeException("@没有涉及到的连接测试类型。");
			}
		}

		switch (dbType)
		{
			case bp.sys.DBSrcType.MSSQL:
				if (Objects.equals(objType.toLowerCase(), "column"))
				{
					RunSQL(String.format("EXEC SP_RENAME '%1$s', '%2$s', 'COLUMN'", oldName, newName));
				}
				else
				{
					RunSQL(String.format("EXEC SP_RENAME '%1$s', '%2$s'", oldName, newName));
				}
				break;
			case Oracle:
			case bp.sys.DBSrcType.DM:
			case bp.sys.DBSrcType.KingBaseR3:
			case bp.sys.DBSrcType.KingBaseR6:
			case DBSrcType.GBASE8CByOracle:
				if (Objects.equals(objType.toLowerCase(), "column"))
				{
					RunSQL(String.format("ALTER TABLE %1$s RENAME COLUMN %2$s TO %3$s", tableName, oldName, newName));
				}
				else if (Objects.equals(objType.toLowerCase(), "table"))
				{
					RunSQL(String.format("ALTER TABLE %1$s RENAME TO %2$s", oldName, newName));
				}
				else if (Objects.equals(objType.toLowerCase(), "view"))
				{
					RunSQL(String.format("RENAME %1$s TO %2$s", oldName, newName));
				}
				else
				{
					throw new RuntimeException("@未涉及到的Oracle数据库改名逻辑。");
				}
				break;
			case bp.sys.DBSrcType.MySQL:
			case DBSrcType.GBASE8CByMySQL:
			case DBSrcType.GBASE8A:
				if (Objects.equals(objType.toLowerCase(), "column"))
				{
					String sql = String.format("SELECT c.COLUMN_TYPE FROM information_schema.columns c WHERE c.TABLE_SCHEMA = '%1$s' AND c.TABLE_NAME = '%2$s' AND c.COLUMN_NAME = '%3$s'", GetTableSchema(), tableName, oldName);

					DataTable dt = RunSQLReturnTable(sql);
					if (dt.Rows.size() > 0)
					{
						RunSQL(String.format("ALTER TABLE %1$s CHANGE COLUMN %2$s %3$s %4$s", tableName, oldName, newName, dt.Rows.get(0).getValue(0)));
					}
				}
				else if (Objects.equals(objType.toLowerCase(), "table"))
				{
					RunSQL(String.format("ALTER TABLE `%1$s`.`%2$s` RENAME `%1$s`.`%3$s`", this.getDBName(), oldName, newName));
				}
				else if (Objects.equals(objType.toLowerCase(), "view"))
				{
					String sql = String.format("SELECT t.VIEW_DEFINITION FROM information_schema.views t WHERE t.TABLE_SCHEMA = '%1$s' AND t.TABLE_NAME = '%2$s'", GetTableSchema(), oldName);

					DataTable dt = RunSQLReturnTable(sql);
					if (dt.Rows.size() == 0)
					{
						RunSQL("DROP VIEW " + oldName);
					}
					else
					{
						RunSQL(String.format("CREATE VIEW %1$s AS %2$s", newName, dt.Rows.get(0).getValue(0)));
						RunSQL("DROP VIEW " + oldName);
					}
				}
				else
				{
					throw new RuntimeException("@未涉及到的Oracle数据库改名逻辑。");
				}
				break;
			case bp.sys.DBSrcType.Informix:

				break;
			default:
				throw new RuntimeException("@没有涉及到的数据库类型。");
		}
	}
	/**
	 获取判断指定表达式如果为空，则返回指定值的SQL表达式
	 <p>注：目前只对MSSQL/ORACLE/MYSQL三种数据库做兼容</p>
	 <p>added by liuxc,2017-03-07</p>

	 @param expression 要判断的表达式，在SQL中的写法
	 @param isNullBack 判断的表达式为NULL，返回值的表达式，在SQL中的写法
	 @return
	*/
	public final String GetIsNullInSQL(String expression, String isNullBack) throws Exception {
		String dbType = this.getDBSrcType();
		if (Objects.equals(dbType, bp.sys.DBSrcType.local))
		{
			switch (bp.difference.SystemConfig.getAppCenterDBType())
			{
				case MSSQL:
					dbType = bp.sys.DBSrcType.MSSQL;
					break;
				case Oracle:
					dbType = Oracle;
					break;
				case DM:
					dbType = bp.sys.DBSrcType.DM;
					break;
				case MySQL:
					dbType = bp.sys.DBSrcType.MySQL;
					break;
				case Informix:
					dbType = bp.sys.DBSrcType.Informix;
					break;
				case KingBaseR3:
					dbType = bp.sys.DBSrcType.KingBaseR3;
					break;
				case KingBaseR6:
					dbType = bp.sys.DBSrcType.KingBaseR6;
					break;
				case GBASE8CByOracle:
					dbType = bp.sys.DBSrcType.GBASE8CByOracle;
					break;
				case GBASE8CByMySQL:
					dbType = bp.sys.DBSrcType.GBASE8CByMySQL;
					break;
				case GBASE8A:
					dbType = bp.sys.DBSrcType.GBASE8A;
					break;
				default:
					throw new RuntimeException("没有涉及到的连接测试类型...");
			}
		}
		switch (dbType)
		{
			case bp.sys.DBSrcType.MSSQL:
				return " ISNULL(" + expression + "," + isNullBack + ")";
			case Oracle:
			case bp.sys.DBSrcType.DM:
			case DBSrcType.GBASE8CByOracle:
				return " NVL(" + expression + "," + isNullBack + ")";
			case bp.sys.DBSrcType.MySQL:
			case DBSrcType.GBASE8CByMySQL:
			case DBSrcType.GBASE8A:
				return " IFNULL(" + expression + "," + isNullBack + ")";
			case bp.sys.DBSrcType.PostgreSQL:
			case bp.sys.DBSrcType.UX:
			case bp.sys.DBSrcType.HGDB:
				return " COALESCE(" + expression + "," + isNullBack + ")";
			case bp.sys.DBSrcType.KingBaseR3:
			case bp.sys.DBSrcType.KingBaseR6:
				return " ISNULL(" + expression + "," + isNullBack + ")";
			default:
				throw new RuntimeException("GetIsNullInSQL未涉及的数据库类型");
		}
	}

	/**
	 * 获取所属的数据库名
	 * @return
	 * @throws Exception
	 */
	public String GetTableSchema() throws Exception {
		//判断所属的数据库名
		String schema = "";
		String dbType = this.getDBSrcType();
		if (bp.sys.DBSrcType.local.equals(dbType))
		{
			//如果是本地就使用SystemConfig
			schema = bp.difference.SystemConfig.getAppCenterDBDatabase();
		}
		else
		{
			//如果非本地数据库，需要解析连接字符串获取DBName
			Hashtable ht = getPairs();
			//C#的连接字符串与Java不同
			schema = ht.get("DBName").toString();
		}

		return schema;
	}

	/**
	 * 获得json.
	 * @return
	 * @throws Exception
	 */
	public String GetTablesJson() throws Exception {
		return bp.tools.Json.ToJson(this.GetTables(true));
	}

	public final String GetTableFieldsJson(String tableName) throws Exception {

		String sql = "";
		switch (bp.difference.SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
				sql = "SELECT column_name as FNAME, data_type as FTYPE, CHARACTER_MAXIMUM_LENGTH as FLEN , column_name as FDESC FROM information_schema.columns where table_name='" + tableName + "'";
				break;
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				sql = "SELECT COLUMN_NAME as FNAME,DATA_TYPE as FTYPE,DATA_LENGTH as FLEN,COLUMN_NAME as FDESC FROM all_tab_columns WHERE table_name = upper('" + tableName + "')";
				break;
			case MySQL:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql = "SELECT COLUMN_NAME FNAME,DATA_TYPE FTYPE,CHARACTER_MAXIMUM_LENGTH FLEN,COLUMN_COMMENT FDESC FROM information_schema.columns WHERE table_name='" + tableName + "' and TABLE_SCHEMA='" + GetTableSchema() + "'";
				break;
			case HGDB:
				sql="SELECT c.column_name as FNAME,c.data_type as FTYPE,c.character_maximum_length as FLEN,t.description as FDESC FROM    pg_class as  p LEFT JOIN information_schema.columns as c ON  p.relname=c.table_name  " +
						"LEFT  JOIN  pg_description as t ON   t.objoid=p.oid and  c.ordinal_position=t.objsubid WHERE p.relname = '"+tableName+"'";
			default:
				break;
		}

		DataTable dt = this.RunSQLReturnTable(sql);

		//设置名称. @dsm
		for (DataRow dr : dt.Rows)
		{
			String val = dr.getValue(3) instanceof String ? dr.getValue(3).toString() : null;
			if (DataType.IsNullOrEmpty(val))
			{
				dr.setValue(3, dr.getValue(0));
			}

			dr.setValue(0, dr.getValue(0) + "=" + dr.getValue(1));
		}

		dt.Columns.get(0).ColumnName = "No"; //字段名.
		dt.Columns.get(1).ColumnName = "FType"; //类型.
		dt.Columns.get(2).ColumnName = "FLen"; //长度.
		dt.Columns.get(3).ColumnName = "Name"; //描述.

		return bp.tools.Json.ToJson(dt);
	}

	/**
	 获取表的字段信息

	 @param tableName 表/视图名称
	 @return 有四个列 No,Name,DBType,DBLength 分别标识  列的字段名，列描述，类型，长度。
	*/
	public final DataTable GetColumns(String tableName) throws Exception {
		//SqlServer数据库
		StringBuilder sql = new StringBuilder();

		String dbType = this.getDBSrcType();
		if (Objects.equals(dbType, bp.sys.DBSrcType.local))
		{
			switch (bp.difference.SystemConfig.getAppCenterDBType())
			{
				case MSSQL:
					dbType = bp.sys.DBSrcType.MSSQL;
					break;
				case Oracle:
					dbType = Oracle;
					break;
				case DM:
					dbType = bp.sys.DBSrcType.DM;
					break;
				case MySQL:
					dbType = bp.sys.DBSrcType.MySQL;
					break;
				case Informix:
					dbType = bp.sys.DBSrcType.Informix;
					break;
				case KingBaseR3:
					dbType = bp.sys.DBSrcType.KingBaseR3;
					break;
				case KingBaseR6:
					dbType = bp.sys.DBSrcType.KingBaseR6;
					break;
				case GBASE8CByOracle:
					dbType = bp.sys.DBSrcType.GBASE8CByOracle;
					break;
				case GBASE8CByMySQL:
					dbType = bp.sys.DBSrcType.GBASE8CByMySQL;
					break;
				case GBASE8A:
					dbType = bp.sys.DBSrcType.GBASE8A;
					break;
				default:
					throw new RuntimeException("没有涉及到的连接测试类型...");
			}
		}

		this.setDBSrcType(dbType);

		switch (dbType)
		{
			case bp.sys.DBSrcType.MSSQL:
				sql.append("SELECT sc.name as No," + "\r\n");
				sql.append("       st.name AS [DBType]," + "\r\n");
				sql.append("       (" + "\r\n");
				sql.append("           CASE " + "\r\n");
				sql.append("                WHEN st.name = 'nchar' OR st.name = 'nvarchar' THEN sc.length / 2" + "\r\n");
				sql.append("                ELSE sc.length" + "\r\n");
				sql.append("           END" + "\r\n");
				sql.append("       ) AS DBLength," + "\r\n");
				sql.append("       sc.colid," + "\r\n");
				sql.append(String.format("       %1$s AS [Name]", GetIsNullInSQL("ep.[value]", "''")) + "\r\n");
				sql.append("FROM   dbo.syscolumns sc" + "\r\n");
				sql.append("       INNER JOIN dbo.systypes st" + "\r\n");
				sql.append("            ON  sc.xtype = st.xusertype" + "\r\n");
				sql.append("       LEFT OUTER JOIN sys.extended_properties ep" + "\r\n");
				sql.append("            ON  sc.id = ep.major_id" + "\r\n");
				sql.append("            AND sc.colid = ep.minor_id" + "\r\n");
				sql.append("            AND ep.name = 'MS_Description'" + "\r\n");
				sql.append(String.format("WHERE  sc.id = OBJECT_ID('dbo.%1$s')", tableName) + "\r\n");
				break;
			case Oracle:
			case bp.sys.DBSrcType.DM:
			case bp.sys.DBSrcType.KingBaseR3:
			case bp.sys.DBSrcType.KingBaseR6:
			case DBSrcType.GBASE8CByOracle:
				sql.append("SELECT utc.COLUMN_NAME AS No," + "\r\n");
				sql.append("       utc.DATA_TYPE   AS DBType," + "\r\n");
				sql.append("       utc.CHAR_LENGTH AS DBLength," + "\r\n");
				sql.append("       utc.COLUMN_ID   AS colid," + "\r\n");
				sql.append(String.format("       %1$s    AS Name", GetIsNullInSQL("ucc.comments", "''")) + "\r\n");
				sql.append("  FROM user_tab_cols utc" + "\r\n");
				sql.append("  LEFT JOIN user_col_comments ucc" + "\r\n");
				sql.append("    ON ucc.table_name = utc.TABLE_NAME" + "\r\n");
				sql.append("   AND ucc.column_name = utc.COLUMN_NAME" + "\r\n");
				sql.append(String.format(" WHERE utc.TABLE_NAME = '%1$s'", tableName.toUpperCase()) + "\r\n");
				sql.append(" ORDER BY utc.COLUMN_ID ASC" + "\r\n");

				break;
			case bp.sys.DBSrcType.MySQL:
			case DBSrcType.GBASE8CByMySQL:
			case DBSrcType.GBASE8A:
				//分别代表字段名,类型，描述，类型加长度（char（11））
				sql.append("SELECT " + "\r\n");
				sql.append("    column_name AS 'No'," + "\r\n");
				sql.append("    data_type AS 'DBType'," + "\r\n");
				sql.append(String.format("    %1$s AS DBLength,", GetIsNullInSQL("character_maximum_length", "numeric_precision")) + "\r\n");
				sql.append("    ordinal_position AS colid," + "\r\n");
				sql.append("    column_comment AS 'Name'" + "\r\n");
				sql.append("FROM" + "\r\n");
				sql.append("    information_schema.columns" + "\r\n");
				sql.append("WHERE" + "\r\n");
				sql.append(String.format("    table_schema = '%1$s'", GetTableSchema()  + "\r\n"));
				sql.append(String.format("        AND table_name = '%1$s';", tableName) + "\r\n");
				break;
			case bp.sys.DBSrcType.Informix:
				break;
			default:
				throw new RuntimeException("没有涉及到的连接测试类型...");
		}

		DataTable dt = null;
		if (this.getNo().equals("local") == true)
		{
			dt = DBAccess.RunSQLReturnTable(sql.toString());
			return dt;
		}
		return RunSQLReturnTable(sql.toString());

	}

	@Override
	protected boolean beforeDelete() throws Exception
	{
		if (Objects.equals(this.getNo(), "local"))
		{
			throw new RuntimeException("@默认连接(local)不允许删除、更新.");
		}

		String str = "";
		MapDatas mds = new MapDatas();
		mds.Retrieve(MapDataAttr.DBSrc, this.getNo());
		if (mds.size()!= 0)
		{
			str += "如下表单使用了该数据源，您不能删除它。";
			for (MapData md : mds.ToJavaList())
			{
				str += "@\t\n" + md.getNo() + " - " + md.getName();
			}
		}

		SFTables tabs = new SFTables();
		tabs.Retrieve(SFTableAttr.FK_SFDBSrc, this.getNo());
		if (tabs.size()!= 0)
		{
			str += "如下 table 使用了该数据源，您不能删除它。";
			for (SFTable tab : tabs.ToJavaList())
			{
				str += "@\t\n" + tab.getNo() + " - " + tab.getName();
			}
		}

		if (!Objects.equals(str, ""))
		{
			throw new RuntimeException("@删除数据源的时候检查，是否有引用，出现错误：" + str);
		}

		return super.beforeDelete();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		if (Objects.equals(this.getNo(), "local"))
		{
			throw new RuntimeException("@默认连接(local)不允许删除、更新.");
		}
		return super.beforeUpdate();
	}
	//added by liuxc,2015-11-10,新建修改时，判断只能加一个本地主库数据源
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		if (!Objects.equals(this.getNo(), "local") && Objects.equals(this.getDBSrcType(), bp.sys.DBSrcType.local))
		{
			throw new RuntimeException("@在该系统中只能有一个本地连接，请选择其他数据源类型。");
		}

		//测试数据库连接.
		DoConn();

		return super.beforeUpdateInsertAction();
	}

		///#endregion 方法.

}
