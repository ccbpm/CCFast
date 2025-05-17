package bp.da;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.UUID;

import bp.web.WebUser;
import org.apache.commons.lang3.StringUtils;
import bp.difference.ContextHolderUtils;
import bp.difference.SystemConfig;
import bp.en.*; import bp.en.Map;
import bp.tools.CRC32Helper;
import bp.web.*;


/**
 * 数据库访问。 这个类负责处理了 实体信息
 */
public class DBAccess {

	public static void UpdateTableColumnDefaultVal(String table, String colName, Object defaultVal) throws Exception {
		String sql = "";

		//是否存在该列?
		if (DBAccess.IsExitsTableCol(table, colName) == false || DBAccess.IsView(table) == true)
			return;

		switch (SystemConfig.getAppCenterDBType())
		{
			case MySQL:
			case KingBaseR3:
			case KingBaseR6:
			case PostgreSQL:
			case UX:
			case HGDB:
			case GBASE8CByMySQL:
			case GBASE8A:
				if (defaultVal.getClass() == Integer.class || defaultVal.getClass() == Float.class || defaultVal.getClass() == BigDecimal.class)
					sql = "ALTER TABLE " + table + " ALTER COLUMN " + colName + " SET DEFAULT " + defaultVal.toString();
				else
					sql = "ALTER TABLE " + table + " ALTER COLUMN " + colName + " SET DEFAULT '" + defaultVal.toString() + "'";
				break;
			case Oracle:
			case DM:
			case GBASE8CByOracle:
				if (defaultVal.getClass() == Integer.class || defaultVal.getClass() == Float.class || defaultVal.getClass() == BigDecimal.class)
					sql = "ALTER TABLE " + table + " MODIFY " + colName + " DEFAULT " + defaultVal.toString();
                else
					sql ="ALTER TABLE "+table+ " MODIFY "+colName+ " DEFAULT '" + defaultVal.toString() + "'";
				break;
			case MSSQL:
				sql = "SELECT  b.name FROM sysobjects b join syscolumns a on b.id = a.cdefault WHERE a.id = object_id('" + table + "') AND a.name = '" + colName + "'";
				String yueShu = DBAccess.RunSQLReturnStringIsNull(sql, null);
				if (yueShu != null)
				{
					sql = "ALTER TABLE " + table + " DROP constraint " + yueShu;
					DBAccess.RunSQL(sql); //删除约束.
				}
				if (defaultVal.getClass() == Integer.class || defaultVal.getClass() == Float.class || defaultVal.getClass() == BigDecimal.class)
				{
					sql = "ALTER TABLE " + table + " ADD DEFAULT " + defaultVal.toString() + " FOR  " + colName;
				}
				else
				{
					sql = "ALTER TABLE " + table + " ADD DEFAULT '" + defaultVal.toString() + "' FOR  " + colName;
				}
				break;
			default:
				break;
		}

		if (DataType.IsNullOrEmpty(sql) == true)
		{
			throw new RuntimeException("err@没有判断的数据库类型.");
		}

		//设置默认值.
		bp.da.DBAccess.RunSQL(sql);
	}

	/**
	 获得数据表字段描述的SQL
	*/
	public static String SQLOfTableFieldDesc(String table)
	{
		if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
		{
			return "SELECT column_name as FName,data_type as FType,CHARACTER_MAXIMUM_LENGTH as FLen from information_schema.columns where table_name='" + table + "'";
		}

		if (SystemConfig.getAppCenterDBType() == DBType.Oracle
				|| SystemConfig.getAppCenterDBType() == DBType.KingBaseR3
				|| SystemConfig.getAppCenterDBType() == DBType.KingBaseR6
				|| SystemConfig.getAppCenterDBType() == DBType.DM
				|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{
		}

		if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{

		}

		if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL)
		{

		}
		throw new RuntimeException("@没有涉及到的数据库类型");
	}
	/**
	 删除指定字段的约束

	 param table 表名
	 param colName 列名
	 @return 返回删除约束的个数
	*/
	public static int DropConstraintOfSQL(String table, String colName)
	{

		if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
		{
			//获得约束.
			String sql = "select b.name from sysobjects b join syscolumns a on b.id = a.cdefault ";
			sql += " where a.id = object_id('" + table + "') ";
			sql += " and a.Name='" + colName + "' ";
			//遍历并删除它们.
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			for (DataRow dr : dt.Rows)
			{
				String name = dr.getValue(0).toString();
				DBAccess.RunSQL("exec('alter table " + table + " drop constraint " + name + " ' )");
			}
			//返回执行的个数.
			return dt.Rows.size();
		}

		return 0;
	}
	/**
	 获得约束

	 param table
	 @return
	*/
	public static String SQLOfTableFieldYueShu(String table)
	{
		if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
		{
			return "SELECT b.name, a.name FName from sysobjects b join syscolumns a on b.id = a.cdefault where a.id = object_id('" + table + "') ";
		}

		if (SystemConfig.getAppCenterDBType() == DBType.Oracle
				|| SystemConfig.getAppCenterDBType() == DBType.DM
				|| SystemConfig.getAppCenterDBType() == DBType.KingBaseR3
				|| SystemConfig.getAppCenterDBType() == DBType.KingBaseR6
				|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
		{

		}

		if (SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
		{

		}

		if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL)
		{

		}
		throw new RuntimeException("@没有涉及到的数据库类型");

	}



	 /// <summary>
    /// 是否大小写敏感
    /// </summary>
    public static Boolean IsCaseSensitive() throws Exception
    {
    	if (DBAccess.IsExitsObject("TEST") == true)
            DBAccess.RunSQL("DROP TABLE TEST ");


   		if (DBAccess.IsExitsObject("test") == true)
            DBAccess.RunSQL("DROP TABLE test ");

            String mysql = "CREATE TABLE TEST(OID int NOT NULL )";
            DBAccess.RunSQL(mysql);
            if (DBAccess.IsExitsObject("test") == false)
            {
                DBAccess.RunSQL("DROP TABLE TEST ");
                return true;
            }
            if (DBAccess.IsExitsTableCol("test", "oid") == false)
            {
                DBAccess.RunSQL("DROP TABLE TEST ");
                return true;
            }

            return false;


    }

	public static void SaveBytesToDB(byte[] bytes, String tableName, String tablePK, String pkVal,
			String saveToFileField) throws Exception {

		// 更新文件数据到库
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		try {

			String sql = "UPDATE " + tableName + " SET " + saveToFileField + "=?" + " WHERE " + tablePK + " =?";

			if (SystemConfig.getAppCenterDBType() == DBType.MSSQL) {
				conn = DBAccess.getGetAppCenterDBConn_MSSQL();
				pstmt = conn.prepareStatement(sql);
			} else if (SystemConfig.getAppCenterDBType() == DBType.Oracle
					|| SystemConfig.getAppCenterDBType() == DBType.KingBaseR3
					|| SystemConfig.getAppCenterDBType() == DBType.KingBaseR6
					|| SystemConfig.getAppCenterDBType() == DBType.DM
					|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle
			) {
				conn = DBAccess.getGetAppCenterDBConn_Oracle();
				pstmt = conn.prepareStatement(sql);
			} else if (SystemConfig.getAppCenterDBType() == DBType.MySQL  || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A) {
				conn = DBAccess.getGetAppCenterDBConn_MySQL();
				pstmt = conn.prepareStatement(sql);
			} else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL ||SystemConfig.getAppCenterDBType() == DBType.HGDB) {
				conn = DBAccess.getGetAppCenterDBConn_PSQL();
				pstmt = conn.prepareStatement(sql);
			}

			// 数据库字段类型不一致增加判断

			pstmt.setBytes(1, bytes);
			if(DataType.IsNumStr(pkVal))
				pstmt.setInt(2, Integer.parseInt(pkVal));
			else
				pstmt.setString(2, pkVal);
			pstmt.execute();
		} catch (Exception ex) {
			if (SystemConfig.getAppCenterDBType().getValue() == DBType.MSSQL.getValue()){
				if (DBAccess.IsExitsTableCol(tableName, saveToFileField) == false)
				{
					/*如果没有此列，就自动创建此列.*/
					String sql = "ALTER TABLE " + tableName + " ADD  " + saveToFileField + " Image ";
					DBAccess.RunSQL(sql);
					SaveBytesToDB(bytes, tableName, tablePK, pkVal, saveToFileField);
					return;
				}
			}
			if (SystemConfig.getAppCenterDBType().getValue() == DBType.PostgreSQL.getValue() || SystemConfig.getAppCenterDBType().getValue() == DBType.HGDB.getValue()){
				if (DBAccess.IsExitsTableCol(tableName, saveToFileField) == false)
				{
					/*如果没有此列，就自动创建此列.*/
					String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + saveToFileField + " bytea ";
					DBAccess.RunSQL(sql);
					SaveBytesToDB(bytes, tableName, tablePK, pkVal, saveToFileField);
					return;
				}
			}
			if (SystemConfig.getAppCenterDBType() == DBType.Oracle|| SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle ){
				if (DBAccess.IsExitsTableCol(tableName, saveToFileField) == false)
				{

					/*如果没有此列，就自动创建此列.*/
					//修改数据类型   oracle 不存在image类型   edited by qin 16.7.1
					String sql = "ALTER TABLE " + tableName + " ADD  " + saveToFileField + " blob ";
					DBAccess.RunSQL(sql);
					SaveBytesToDB(bytes,tableName, tablePK, pkVal, saveToFileField);
					return;

				}
			}
			if (SystemConfig.getAppCenterDBType().getValue() == DBType.KingBaseR3.getValue()
					|| SystemConfig.getAppCenterDBType().getValue() == DBType.KingBaseR6.getValue()){
				if (DBAccess.IsExitsTableCol(tableName, saveToFileField) == false)
				{

					/*如果没有此列，就自动创建此列.*/
					//修改数据类型   oracle 不存在image类型   edited by qin 16.7.1
					String sql = "ALTER TABLE " + tableName + " ADD  COLUMN  " + saveToFileField + "  blob ";
					DBAccess.RunSQL(sql);
					SaveBytesToDB(bytes,tableName, tablePK, pkVal, saveToFileField);
					return;

				}
			}
			if (SystemConfig.getAppCenterDBType().getValue() == DBType.MySQL.getValue()  || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A){
				if (DBAccess.IsExitsTableCol(tableName, saveToFileField) == false)
				{
					/*如果没有此列，就自动创建此列.*/
					String sql = "ALTER TABLE " + tableName + " ADD  " + saveToFileField + " BLOB NULL ";
					DBAccess.RunSQL(sql);
					SaveBytesToDB(bytes,tableName, tablePK, pkVal, saveToFileField);
					return;
				}
			}

			throw new RuntimeException(ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return;
	}

	/**
	 * 保存文件到数据库
	 *
	 * param fullFileName
	 *            完成的文件路径
	 * param tableName
	 *            表名称
	 * param tablePK
	 *            表主键
	 * param pkVal
	 *            主键值
	 * param saveToFileField
	 *            保存到字段
	 * @throws Exception
	 */
	public static void SaveFileToDB(String fullFileName, String tableName, String tablePK, String pkVal,
			String saveToFileField) throws Exception {

		// 读取文件内容
		FileInputStream fs = null;
		byte[] bytes = null;
		String line = "";
		try {
			fs = new FileInputStream(fullFileName);
			int len = fs.available();
			bytes = new byte[len];
			fs.read(bytes);

			//执行保存.
			SaveBytesToDB(bytes, tableName, tablePK, pkVal, saveToFileField);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (fs != null) {
				fs.close();
			}
		}

	}

	/**
	 * 保存文件到数据库
	 * param tableName
	 *            表名称
	 * param tablePK
	 *            表主键
	 * param pkVal
	 *            主键值
	 * param saveToFileField
	 *            保存到字段
	 * @throws Exception
	 */
	public static void SaveBigTextToDB(String docs, String tableName, String tablePK, String pkVal,
			String saveToFileField) throws Exception {
		//对于特殊的数据库进行判断.
		if (SystemConfig.getAppCenterDBType() == DBType.Oracle
				//|| SystemConfig.getAppCenterDBType() == DBType.PostgreSQL
				|| SystemConfig.getAppCenterDBType() == DBType.DM
				||  SystemConfig.getAppCenterDBType() == DBType.KingBaseR3
				||  SystemConfig.getAppCenterDBType() == DBType.KingBaseR6)
		{
			SaveBytesToDB(docs.getBytes("UTF-8"),tableName, tablePK, pkVal, saveToFileField);
			return;
		}
		Paras ps = new Paras();
		ps.SQL="UPDATE " + tableName + " SET " + saveToFileField + "=" + SystemConfig.getAppCenterDBVarStr() + "MyDocs WHERE " + tablePK + "=" + SystemConfig.getAppCenterDBVarStr() + "PKVal";
		ps.Add("MyDocs", docs);
		ps.Add("PKVal", pkVal);

		try
		{
			DBAccess.RunSQL(ps);
		}
		catch (Exception ex)
		{
			/*如果没有此列，就自动创建此列.*/
			if (DBAccess.IsExitsTableCol(tableName, saveToFileField) == false)
			{
				String sql = "ALTER TABLE " + tableName + " ADD  " + saveToFileField + " text ";
				DBAccess.RunSQL(sql);
				DBAccess.RunSQL(ps);
				return;
			}
			throw ex;
		}
	}

	/**
	 * 从数据库里提取文件
	 *
	 * param fullFileName
	 *            要存储的文件路径
	 * param tableName
	 *            表名
	 * param tablePK
	 *            表主键
	 * param pkVal
	 *            主键值
	 * param fileSaveField
	 *            字段
	 * @throws IOException
	 */
	public static byte[] GetFileFromDB(String fullFileName, String tableName, String tablePK, String pkVal,
			String fileSaveField) throws IOException {

		// 清空文件内容
		File file = new File(fullFileName);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		// 清空文件内容

		String strSQL = "SELECT [" + fileSaveField + "] FROM " + tableName + " WHERE " + tablePK + "='" + pkVal + "'";

		if (SystemConfig.getAppCenterDBType() == DBType.Oracle
			||SystemConfig.getAppCenterDBType() == DBType.KingBaseR3
			||SystemConfig.getAppCenterDBType() == DBType.KingBaseR6
			||SystemConfig.getAppCenterDBType() == DBType.PostgreSQL
			|| SystemConfig.getAppCenterDBType() == DBType.DM
			|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle) {
			strSQL = strSQL.replace("[", "").replace("]", "");
		}

		if (SystemConfig.getAppCenterDBType() == DBType.MySQL  || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A) {
			strSQL = strSQL.replace("[", "`").replace("]", "`");
		}

		DataTable dt = DBAccess.RunSQLReturnTable(strSQL);
		byte[] byteFile = null;
		try {
			if (dt.Rows.size() > 0) {
				if (dt.Rows.get(0).getValue(0) != null && !"".equals(dt.Rows.get(0).getValue(0))) {
					Object a = dt.Rows.get(0).getValue(0);
					if (a instanceof java.sql.Blob) {
						java.sql.Blob b = (java.sql.Blob) dt.getValue(0, 0);
						InputStream is = b.getBinaryStream();
						byteFile = new byte[(int) b.length()];
						is.read(byteFile);
						is.close();
					} else if (a instanceof java.lang.String) {
						byteFile = ((String) a).getBytes("UTF-8");
					} else {
						byteFile = (byte[]) dt.getValue(0, 0);
					}
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(fullFileName);
						fos.write(byteFile);
					} finally {
						if (fos != null) {
							fos.close();
						}
					}
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return byteFile;
	}

	/**
	 * 从数据库一个表里把img字段读取出来，转化成string返回.
	 *
	 * param fullFileName
	 *            要存储的文件路径名称
	 * param tableName
	 *            表名称
	 * param tablePK
	 *            表主键
	 * param pkVal
	 *            主键值
	 * param fileSaveField
	 *            表字段
	 * @return 读取出来的文本文件
	 * @throws IOException
	 */
	public static String GetTextFileFromDB(String fullFileName, String tableName, String tablePK, String pkVal,
			String fileSaveField) throws IOException {
		GetFileFromDB(fullFileName, tableName, tablePK, pkVal, fileSaveField);
		return DataType.ReadTextFile(fullFileName);
	}

	/**
	 * 从数据库一个表里把img字段读取出来，转化成string返回.
	 *
	 * param fullFileName
	 *            要存储的文件路径名称
	 * param tableName
	 *            表名称
	 * param tablePK
	 *            表主键
	 * param pkVal
	 *            主键值
	 * param fileSaveField
	 *            表字段
	 * param codeType
	 *            编码格式
	 * @return 读取出来的文本文件
	 * @throws IOException
	 */
	public static String GetTextFileFromDB(String fullFileName, String tableName, String tablePK, String pkVal,
			String fileSaveField, String codeType) throws IOException {
		GetFileFromDB(fullFileName, tableName, tablePK, pkVal, fileSaveField);
		return DataType.ReadTextFile(fullFileName, codeType);
	}

	// 文件存储数据库处理.
	public static Paras DealParasBySQL(String sql, Paras ps) {
		Paras myps = new Paras();
		for (Para p : ps) {
			if (!sql.contains(":" + p.ParaName)) {
				continue;
			}
			myps.Add(p);
		}
		return myps;
	}

	public static int RunSP(String spName, String paraKey, Object paraVal){
		Paras pas = new Paras();
		pas.Add(paraKey, paraVal);
		return DBAccess.RunSP(spName, pas);
	}

	/**
	 * 运行存储过程
	 *
	 * param spName
	 *            名称
	 * @return 返回影响的行数
	 */
	public static int RunSP(String spName) {
		// int i = 0;
		switch (SystemConfig.getAppCenterDBType()) {
		case MSSQL:
		case Access:
			throw new RuntimeException("@没有实现...");
		case Oracle:
		case KingBaseR3:
		case KingBaseR6:
		case GBASE8CByOracle:
			throw new RuntimeException("@没有实现...");
		case Informix:
			throw new RuntimeException("@没有实现...");
		case DM:
			throw new RuntimeException("@没有实现...");
		default:
			throw new RuntimeException("Error: " + SystemConfig.getAppCenterDBType());
		}
	}

	public static int RunSPReturnInt(String spName) {
		switch (SystemConfig.getAppCenterDBType()) {
		case MSSQL:
			throw new RuntimeException("@没有实现...");
		case MySQL:
		case GBASE8CByMySQL:
		case GBASE8A:
			throw new RuntimeException("@没有实现...");
		case Informix:
			throw new RuntimeException("@没有实现...");
		case Access:
		case Oracle:
		case KingBaseR3:
		case KingBaseR6:
		case GBASE8CByOracle:
			throw new RuntimeException("@没有实现...");
		case DM:
			throw new RuntimeException("@没有实现...");
		default:
			throw new RuntimeException("Error: " + SystemConfig.getAppCenterDBType());
		}
	}

	/**
	 * 运行存储过程
	 *
	 * param spName
	 *            名称
	 * param paras
	 *            参数
	 * @return 返回影响的行数
	 */
	public static int RunSP(String spName, Paras paras) {
		// int i = 0;
		switch (SystemConfig.getAppCenterDBType()) {
		case MSSQL:
			throw new RuntimeException("@没有实现...");
		case MySQL:
		case GBASE8CByMySQL:
		case GBASE8A:
			throw new RuntimeException("@没有实现...");
		case Oracle:
		case KingBaseR3:
		case KingBaseR6:
		case DM:
		case GBASE8CByOracle:
			throw new RuntimeException("@没有实现...");
		case Informix:
			throw new RuntimeException("@没有实现...");
		default:
			throw new RuntimeException("Error " + SystemConfig.getAppCenterDBType());
		}
	}

	// 运行存储过程返回 DataTable
	/**
	 * 运行存储过程
	 *
	 * param spName
	 *            名称
	 * @return DataTable
	 */
	public static DataTable RunSPReTable(String spName) {
		switch (SystemConfig.getAppCenterDBType()) {
		case MSSQL:
		case Access:
		case GBASE8CByMySQL:
		case GBASE8A:
			throw new RuntimeException("@没有实现...");
		case Oracle:
		case KingBaseR3:
		case KingBaseR6:
		case DM:
		case GBASE8CByOracle:
			throw new RuntimeException("@没有实现...");
		case Informix:
			throw new RuntimeException("@没有实现...");
		default:
			throw new RuntimeException("Error " + SystemConfig.getAppCenterDBType());

		}
	}

	/**
	 * 运行存储过程
	 *
	 * param spName
	 *            名称
	 * param paras
	 *            参数
	 * @return DataTable
	 */
	public static DataTable RunSPReTable(String spName, Paras paras) {
		switch (SystemConfig.getAppCenterDBType()) {
		case MSSQL:
			throw new RuntimeException("@没有实现...");
		case Oracle:
		case KingBaseR3:
		case KingBaseR6:
		case DM:
		case GBASE8CByOracle:
			throw new RuntimeException("@没有实现...");
		case Informix:
			throw new RuntimeException("@没有实现...");
		case Access:
		default:
			throw new RuntimeException("Error " + SystemConfig.getAppCenterDBType());
		}
	}

	public static void copyDirectory(String Src, String Dst) {
		copyFolder(new File(Src), new File(Dst));
	}

	private static void copyFolder(File src, File dest) {
		try {
			if (src.isDirectory()) {
				if (!dest.exists()) {
					dest.mkdirs();
				}
				String files[] = src.list();
				for (String file : files) {
					File srcFile = new File(src, file);
					File destFile = new File(dest, file);
					// 递归复制
					copyFolder(srcFile, destFile);
				}
			} else {
				InputStream in = new FileInputStream(src);
				OutputStream out = new FileOutputStream(dest);

				byte[] buffer = new byte[1024];

				int length;

				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				in.close();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 运行中定义的变量
	public static Hashtable<String, Object> CurrentSys_Serial = new Hashtable<String, Object>();
	private static Hashtable<String, Object> KeyLockState = new Hashtable<String, Object>();
	private static int readCount = -1;

	/**
	 * 根据标识产生的序列号
	 *
	 * param type
	 *            OID
	 * @return
	 * @throws Exception
	 */
	public static int GenerSequenceNumber(String type) throws Exception {
		if (readCount == -1) // 系统第一次运行时
		{
			DataTable tb = DBAccess.RunSQLReturnTable("SELECT CfgKey, IntVal FROM Sys_Serial ");
			for (DataRow row : tb.Rows) {
				String str = row.getValue(0).toString().trim();
				int id = Integer.parseInt(str);
				try {
					CurrentSys_Serial.put(str, id);
					KeyLockState.put(str, false);
				} catch (java.lang.Exception e) {
				}
			}
			readCount++;
		}
		if (!CurrentSys_Serial.containsKey(type)) {
			DBAccess.RunSQL("insert into Sys_Serial values('" + type + "',1 )");
			return 1;
		}

		while (true) {
			while (!Boolean.parseBoolean(KeyLockState.get(type).toString())) {
				KeyLockState.put(type, true);
				int cur = Integer.parseInt(CurrentSys_Serial.get(type).toString());
				if (readCount++ % 10 == 0) {
					readCount = 1;
					int n = Integer.parseInt(CurrentSys_Serial.get(type).toString()) + 10;

					Paras ps = new Paras();
					ps.Add("intVal", n);
					ps.Add("CfgKey", type);

					String upd = "update Sys_Serial set intVal=" + SystemConfig.getAppCenterDBVarStr()
							+ "intVal WHERE CfgKey=" + SystemConfig.getAppCenterDBVarStr() + "CfgKey";
					DBAccess.RunSQL(upd, ps);
				}

				cur++;
				CurrentSys_Serial.put(type, cur);
				KeyLockState.put(type, false);
				return cur;
			}
		}
	}

	/**
	 * 通过table的第1列，组成一个查询的where in 字符串.
	 *
	 * param dt
	 * @return
	 */
	public static String GenerWhereInPKsString(DataTable dt) {
		String pks = "";
		for (DataRow dr : dt.Rows) {
			pks += "'" + dr.getValue(0) + "',";
		}
		if ("".equals(pks))
			return "";
		return pks.substring(0, pks.length() - 1);
	}

	/**
	 * 生成 GenerOIDByGUID.
	 *
	 * @return
	 */
	public static int GenerOIDByGUID() {
		/*
		 * warning int i = CRC32Helper.GetCRC32(Guid.NewGuid().toString());
		 */
		int i = CRC32Helper.GetCRC32(UUID.randomUUID().toString());
		if (i <= 0) {
			i = -i;
		}
		return i;
	}

	/**
	 * 生成 GenerOIDByGUID.
	 *
	 * @return
	 */
	public static int GenerOIDByGUID(String strs) {
		int i = CRC32Helper.GetCRC32(strs);
		if (i <= 0) {
			i = -i;
		}
		return i;
	}

	/**
	 * 生成 GenerGUID
	 *
	 * @return
	 */
	public static String GenerGUID() {
		return GenerGUID(0,null,null);
	}
	public static String GenerGUID(int length) {
		return GenerGUID(length,null,null);
	}

	public static String GenerGUID(int length,String ptable,String colName) {
		String guid = UUID.randomUUID().toString().replace("-", "");
		if(length==0)
			return guid;
		if(ptable ==null)
			return guid.substring(0,length);
		while (true)
		{
			String str = guid.substring(0,length);
			String sql = "SELECT COUNT(" + colName + ") as Num FROM " + ptable + " WHERE " + colName + " ='" + str + "'";
			if (DBAccess.RunSQLReturnValInt(sql) == 0)
				return str;
		}
	}
	/**
	 * 锁定OID
	 */
	private static boolean lock_OID = false;
	/**
	 * 产生一个OID
	 *
	 * @return
	 * @throws Exception
	 */
	public static int GenerOID() {
		lock_OID = true;
		if (DBAccess.RunSQL("UPDATE Sys_Serial SET IntVal=IntVal+1 WHERE CfgKey='OID'") == 0) {
			DBAccess.RunSQL("INSERT INTO Sys_Serial (CfgKey,IntVal) VALUES ('OID',100)");
		}
		int oid = DBAccess.RunSQLReturnValInt("SELECT  IntVal FROM Sys_Serial WHERE CfgKey='OID'");
		lock_OID = false;
		return oid;
	}

	// 第二版本的生成 OID。
	/**
	 * 锁
	 */
	private static boolean lock_HT_CfgKey = false;
	private static Hashtable<String, Object> lock_HT = new Hashtable<String, Object>();

	/**
	 * 生成唯一的序列号
	 *
	 * param cfgKey
	 *            配置信息
	 * @return 唯一的序列号
	 * @throws Exception
	 */
	public static long GenerOID(String cfgKey) {
		/*if (SystemConfig.getAppCenterDBType() == DBType.KingBaseR3
				|| SystemConfig.getAppCenterDBType() == DBType.KingBaseR6)
		{
			String sql="SELECT COUNT(*) FROM sys_serial WHERE cfgKey='"+cfgKey+"'";
			int num=DBAccess.RunSQLReturnValInt(sql);
			if (num==0) {
				sql="insert into sys_serial(cfgKey,intval) values('"+cfgKey+"',100)";
				DBAccess.RunSQL(sql);
			}else{
				int val=DBAccess.RunSQLReturnValInt("SELECT func_NextSerialVal('"+cfgKey+"') Num");
				if (val!=0) {
					return val;
				}
			}
			return 100;
		}
		/*int val=DBAccess.RunSQLReturnValInt("SELECT func_NextSerialVal('"+cfgKey+"') Num");
		if (val==0) {
			String sql="SELECT COUNT(*) FROM sys_serial WHERE cfgKey='"+cfgKey+"'";
			int num=DBAccess.RunSQLReturnValInt(sql);
			if (num==0) {
				sql="insert into sys_serial(cfgKey,intval) values('"+cfgKey+"',100)";
				DBAccess.RunSQL(sql);
			}
			return 100;
		}*/

		Paras ps = new Paras();
		ps.Add("CfgKey", cfgKey);
		String sql = "UPDATE Sys_Serial SET IntVal=IntVal+1 WHERE CfgKey=" + SystemConfig.getAppCenterDBVarStr()
				+ "CfgKey";
		int num = DBAccess.RunSQL(sql, ps);
		if (num == 0) {
			sql = "INSERT INTO Sys_Serial (CFGKEY,INTVAL) VALUES ('" + cfgKey + "',100)";
			DBAccess.RunSQL(sql);
			lock_HT_CfgKey = false;

			lock_HT.put(cfgKey, 200);
			return 100;
		}
		sql = "SELECT IntVal FROM Sys_Serial WHERE CfgKey=" + SystemConfig.getAppCenterDBVarStr() + "CfgKey";
		num = DBAccess.RunSQLReturnValInt(sql, ps);
		return num;
		//return val;
	}

	// 第二版本的生成 OID。

	/**
	 * 获取一个从OID, 更新到OID. 用例: 我已经明确知道要用到260个OID, 但是为了避免多次取出造成效率浪费，就可以一次性取出
	 * 260个OID.
	 *
	 * param cfgKey
	 * param getOIDNum
	 *            要获取的OID数量.
	 * @return 从OID
	 * @throws Exception
	 */
	public static long GenerOID(String cfgKey, int getOIDNum) throws Exception {
		Paras ps = new Paras();
		ps.Add("CfgKey", cfgKey);
		String sql = "UPDATE Sys_Serial SET IntVal=IntVal+" + getOIDNum + " WHERE CfgKey="
				+ SystemConfig.getAppCenterDBVarStr() + "CfgKey";
		int num = DBAccess.RunSQL(sql, ps);
		if (num == 0) {
			getOIDNum = getOIDNum + 100;
			sql = "INSERT INTO Sys_Serial (CFGKEY,INTVAL) VALUES (" + SystemConfig.getAppCenterDBVarStr() + "CfgKey,"
					+ getOIDNum + ")";
			DBAccess.RunSQL(sql, ps);
			return 100;
		}
		sql = "SELECT  IntVal FROM Sys_Serial WHERE CfgKey=" + SystemConfig.getAppCenterDBVarStr() + "CfgKey";
		return DBAccess.RunSQLReturnValInt(sql, ps) - getOIDNum;
	}

	public static int GenerOIDByKey32(String intKey) throws Exception {
		Paras ps = new Paras();
		ps.Add("CfgKey", intKey);

		String sql = "";
		sql = "UPDATE Sys_Serial SET IntVal=IntVal+1 WHERE CfgKey=" + SystemConfig.getAppCenterDBVarStr() + "CfgKey";
		int num = DBAccess.RunSQL(sql, ps);
		if (num == 0) {
			sql = "INSERT INTO Sys_Serial (CFGKEY,INTVAL) VALUES (" + SystemConfig.getAppCenterDBVarStr()
					+ "CfgKey,'100')";
			DBAccess.RunSQL(sql, ps);
			return Integer.parseInt("100");
		}
		sql = "SELECT IntVal FROM Sys_Serial WHERE CfgKey=" + SystemConfig.getAppCenterDBVarStr() + "CfgKey";
		int val = DBAccess.RunSQLReturnValInt(sql, ps);
		return val;
	}

	public static long GenerOID(String table, String intKey) throws Exception {
		Paras ps = new Paras();
		ps.Add("CfgKey", intKey);

		String sql = "";
		sql = "UPDATE " + table + " SET IntVal=IntVal+1 WHERE CfgKey=" + SystemConfig.getAppCenterDBVarStr() + "CfgKey";
		int num = DBAccess.RunSQL(sql, ps);
		if (num == 0) {
			sql = "INSERT INTO " + table + " (CFGKEY,INTVAL) VALUES (" + SystemConfig.getAppCenterDBVarStr()
					+ "CfgKey,100)";
			DBAccess.RunSQL(sql, ps);
			return Integer.parseInt(intKey + "100");
		}
		sql = "SELECT  IntVal FROM " + table + " WHERE CfgKey=" + SystemConfig.getAppCenterDBVarStr() + "CfgKey";
		int val = DBAccess.RunSQLReturnValInt(sql, ps);

		return Long.parseLong(intKey + (new Integer(val)).toString());
	}

	// 取得连接对象 ，CS、BS共用属性【关键属性】
	public static Connection getGetAppCenterDBConn_MSSQL() throws Exception {

		return ContextHolderUtils.getInstance().getDataSource().getConnection();
	}

	private static Hashtable<String, Connection> _HashtableConn = null;

	public static void SetConnOfTransactionForMySQL(String buessID, Connection conn) {
		if (_HashtableConn == null)
			_HashtableConn = new Hashtable();

		_HashtableConn.put(buessID, conn);
	}

	public static Connection GetConnOfTransactionForMySQL(String buessID) throws Exception {
		if (buessID == null)
			return null;

		if (_HashtableConn == null) {
			_HashtableConn = new Hashtable();
			return null;
		}

		if (_HashtableConn.containsKey(buessID) == false)
			return null;

		Connection conn = _HashtableConn.get(buessID);
		return conn;
	}

	public static Connection getGetAppCenterDBConn_MySQL() throws Exception {

		return ContextHolderUtils.getInstance().getDataSource().getConnection();

		/*
		 * String id= WebUser.getNoOfRel(); if (id==null) return
		 * ContextHolderUtils.getInstance().getDataSource().getConnection();
		 *
		 * Connection conn= GetConn(id); if (conn==null) return
		 * ContextHolderUtils.getInstance().getDataSource().getConnection();
		 *
		 * return conn;
		 */

	}

	public static Connection getGetAppCenterDBConn_Oracle() throws Exception {

		return ContextHolderUtils.getInstance().getDataSource().getConnection();
	}
	public static Connection getGetAppCenterDBConn_PSQL() throws Exception {

		return ContextHolderUtils.getInstance().getDataSource().getConnection();
	}

	public static Connection getGetAppCenterDBConn_GBASE8CByOracle() throws Exception {

		return ContextHolderUtils.getInstance().getDataSource().getConnection();
	}
	// 取得连接对象 ，CS、BS共用属性

	/**
	 * AppCenterDBType
	 */
	public static DBType getAppCenterDBType() {
		return SystemConfig.getAppCenterDBType();
	}


	public static String _connectionUserID = null;
	/**
	 连接用户的ID
	*/
	public static String getConnectionUserID()throws Exception
	{
		if (_connectionUserID == null)
		{
			String[] strs = SystemConfig.getAppCenterDSN().split("[;]", -1);
			for (String str : strs)
			{
				if (str.toLowerCase().contains("user ") == true)
				{
					_connectionUserID = str.split("[=]", -1)[1];
					break;
				}
			}
		}
		return _connectionUserID;
	}

	// 运行 SQL

	// 通过主应用程序在其他库上运行sql

	// pk
	/**
	 * 建立主键
	 *
	 * param tab
	 *            物理表
	 * param pk
	 *            主键
	 * @throws Exception
	 */
	public static void CreatePK(String tab, String pk, DBType db) {
		if (tab == null || "".equals(tab))
			return;

		if (DBAccess.IsExitsTabPK(tab)) {
			return;
		}
		String sql="";
		switch (db)
		{
			case Informix:
				sql = "ALTER TABLE " + tab.toUpperCase() + " ADD CONSTRAINT  PRIMARY KEY(" + pk + ") CONSTRAINT " + tab + "pk ";
				break;
			case MySQL:
				sql = "ALTER TABLE " + tab + " ADD CONSTRAINT  " + tab + "px PRIMARY KEY(" + pk + ")";
				break;
			case HGDB:
				sql="ALTER TABLE " + tab.toUpperCase() + " ADD  PRIMARY KEY (" + pk.toUpperCase() + ")";
				break;
			default:
				sql = "ALTER TABLE " + tab.toUpperCase() + " ADD CONSTRAINT " + tab + "pk PRIMARY KEY(" + pk.toUpperCase() + ")";
				break;
		}

		//这个地方不应该出现异常, 需要处理一下在向数据库计划流程中出现.
		try
		{
			DBAccess.RunSQL(sql);
		}
		catch (Exception ex)
		{
		}
	}

	public static void CreatePK(String tab, String pk1, String pk2, DBType db) {

		if (DBAccess.IsExitsTabPK(tab)) {
			return;
		}

		String sql;
		sql = "ALTER TABLE " + tab.toUpperCase() + " ADD CONSTRAINT " + tab + "pk  PRIMARY KEY(" + pk1.toUpperCase()
				+ "," + pk2.toUpperCase() + ")";

		DBAccess.RunSQL(sql);
	}

	public static void CreatePK(String tab, String pk1, String pk2, String pk3, DBType db) {

		if (DBAccess.IsExitsTabPK(tab)) {
			return;
		}

		String sql;

		sql = "ALTER TABLE " + tab.toUpperCase() + " ADD CONSTRAINT " + tab + "pk PRIMARY KEY(" + pk1.toUpperCase()
				+ "," + pk2.toUpperCase() + "," + pk3.toUpperCase() + ")";
		DBAccess.RunSQL(sql);
	}

	// index
	public static void CreatIndex(String table, String fields, DBType db) {

		String myFields = fields.replace(",", "_");
		String idxName = table + "_" + myFields;
		try {
			if (DBAccess.IsExitsTableIndex(table, idxName)) return;
		} catch (Exception ex) {
			Log.DebugWriteError(ex);
		}
		String sql="";
		switch (db)
		{
			case HGDB:
				sql="CREATE INDEX ON " + table+ " USING btree (" + fields + ")";
				break;
			default:
				 sql = "CREATE INDEX " + idxName + " ON " + table + " (" + fields + ")";
				break;
		}

		DBAccess.RunSQL(sql);

	}

	public static void CreatIndex(String table, String pk1, String pk2) {
		String idxName = table + "_" + pk1+"_"+pk2;
		try {
			if (DBAccess.IsExitsTableIndex(table, idxName)) return;
		} catch (Exception ex) {
			Log.DebugWriteError(ex);
		}
		DBAccess.RunSQL("CREATE INDEX " + idxName + " ON " + table + " (" + pk1 + "," + pk2 + ")");
	}

	public static void CreatIndex(String table, String pk1, String pk2, String pk3) {
		String idxName = table + "_" + pk1 + "_" + pk2+"_"+pk3;
		try {
			if (DBAccess.IsExitsTableIndex(table, idxName)) return;
		} catch (Exception ex) {
			Log.DebugWriteError(ex);
		}
		DBAccess.RunSQL("CREATE INDEX " + idxName + " ON " + table + " (" + pk1 + "," + pk2 + "," + pk3 + ")");
	}




	public static DataTable ReadProText(String proName) throws Exception {
		String sql = "";
		switch (SystemConfig.getAppCenterDBType()) {
		case Oracle:
		case KingBaseR3:
		case KingBaseR6:
		case DM:
		case GBASE8CByOracle:
			sql = "SELECT text FROM user_source WHERE name=UPPER('" + proName + "') ORDER BY LINE ";
			break;
		default:
			sql = "SP_Help  " + proName;
			break;
		}

		try {
			return DBAccess.RunSQLReturnTable(sql);
		} catch (java.lang.Exception e) {
			sql = "select * from Port_Emp WHERE 1=2";
			return DBAccess.RunSQLReturnTable(sql);
		}
	}

	public static void RunSQLScript(String sqlOfScriptFilePath) {

		String str = DataType.ReadTextFile(sqlOfScriptFilePath);
		String[] strs = str.split("[;]", -1);
		for (String s : strs) {
			/*
			 * warning if (DataType.IsNullOrEmpty(s) ||
			 * StringHelper.isNullOrWhiteSpace(s)) { continue; }
			 */
			if (DataType.IsNullOrEmpty(s)) {
				continue;
			}

			if (s.contains("--")) {
				continue;
			}

			if (s.contains("/*")) {
				continue;
			}

			DBAccess.RunSQL(s);
		}
	}

	/**
	 * 执行具有Go的sql 文本。
	 *
	 * param sqlOfScriptFilePath
	 * @throws IOException
	 * @throws Exception
	 */
	public static void RunSQLScriptGo(String sqlOfScriptFilePath) throws IOException, Exception {
		String str = DataType.ReadTextFile(sqlOfScriptFilePath);
		/*
		 * warning String[] strs = str.split(new String[] { "--GO--" },
		 * StringSplitOptions.RemoveEmptyEntries);
		 */
		String[] strs = str.split("--GO--");
		for (String s : strs) {
			/*
			 * warning if (DataType.IsNullOrEmpty(s) ||
			 * StringHelper.isNullOrWhiteSpace(s)) { continue; }
			 */
			if (DataType.IsNullOrEmpty(s)) {
				continue;
			}

			// if (s.contains("--"))
			// continue;

			if (s.contains("/**")) {
				continue;
			}

			String mysql = s.replace("--GO--", "");
			if (DataType.IsNullOrEmpty(mysql.trim())) {
				continue;
			}

			DBAccess.RunSQL(mysql);
		}
	}

	public static void RunSQLs(String sql) {
		if (DataType.IsNullOrEmpty(sql)) {
			return;
		}

		sql = sql.replace("@GO", "~");
		sql = sql.replace("@", "~");
		String[] strs = sql.split("[~]", -1);
		for (String str : strs) {
			if (DataType.IsNullOrEmpty(str)) {
				continue;
			}

			if (str.trim().startsWith("--") || str.trim().startsWith("/*")){
				continue;
			}

			RunSQL(str);
		}
	}

	/**
	 * 运行带有参数的sql
	 *
	 * param ps
	 * @return
	 * @throws Exception
	 */
	public static int RunSQL(Paras ps) {
		return RunSQL(ps.SQL, ps);
	}

	/**
	 * 运行sql
	 *
	 * param sql
	 * @return
	 * @throws Exception
	 */
	public static int RunSQL(String sql) {

		if (DataType.IsNullOrEmpty(sql) == true) {
			return 1;
		}

		Paras ps = new Paras();
		ps.SQL=sql;
		return RunSQL(ps);
	}

	public static int RunSQL(String sql, String paraKey, Object val) {
		Paras ens = new Paras();
		ens.Add(paraKey, val);
		return RunSQL(sql, ens);
	}

	public static int RunSQL(String sql, String paraKey1, Object val1, String paraKey2, Object val2) {
		Paras ens = new Paras();
		ens.Add(paraKey1, val1);
		ens.Add(paraKey2, val2);
		return RunSQL(sql, ens);
	}

	public static int RunSQL(String sql, String paraKey1, Object val1, String paraKey2, Object val2, String k3,
			Object v3) {
		Paras ens = new Paras();
		ens.Add(paraKey1, val1);
		ens.Add(paraKey2, val2);
		ens.Add(k3, v3);
		return RunSQL(sql, ens);
	}

	// 执行SQL锁，暂时没有用
	private static boolean lockRunSQL = false;

	/**
	 * 执行sql
	 *
	 * param sql
	 * param paras
	 * @return
	 * @throws Exception
	 */
	public static int RunSQL(String sql, Paras paras) {
		if (sql == null || sql.trim().equals("")) {
			return 1;
		}

		lockRunSQL = true;

		int result = 0;
		try {
			switch (getAppCenterDBType()) {
			case MSSQL:
				result = RunSQL_200705_SQL(sql, paras);
				break;
			case Oracle:
			case GBASE8CByOracle:
				result = RunSQL_200705_Ora(sql.replace("]", "").replace("[", ""), paras);
				break;
			case MySQL:
			case GBASE8A:
			case GBASE8CByMySQL:
				result = RunSQL_200705_MySQL(sql, paras);
				break;
			case PostgreSQL:
			case HGDB:
				result = RunSQL_201902_PSQL(sql, paras);
				break;
			case DM:
				result = RunSQL_20191230_DM(sql.replace("]", "").replace("[", ""), paras);
				break;
			case KingBaseR3:
			case KingBaseR6:
				result = RunSQL_200705_Ora(sql.replace("]", "").replace("[", ""), paras);
				break;
			default:
				throw new RuntimeException("发现未知的数据库连接类型！");
			}
			return result;
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			lockRunSQL = false;
		}
	}

	/**
	 * 运行sql返回结果
	 *
	 * param sql
	 *            sql
	 * param paras
	 *            参数
	 * @return 执行的结果
	 * @throws Exception
	 */
	private static int RunSQL_200705_SQL(String sql, Paras paras) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		try {
			conn = DBAccess.getGetAppCenterDBConn_MSSQL();
			int i = 0;
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				i = pstmt.executeUpdate();
			} else {
				stmt = conn.createStatement();// 创建用于执行静态sql语句的Statement对象，st属局部变量
				i = stmt.executeUpdate(sql);
			}

			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + i);
			}

			return i;
		} catch (Exception ex) {

			String msg = "@运行更新在(RunSQL_200705_SQL)出错。\n  @SQL: " + sql + "\n  @Param: " + paras.getDebugInfo()
					+ "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);

			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static int RunSQL_200705_Ora(String sql, Paras paras) {
		if (sql.endsWith(";") == true && SystemConfig.getAppCenterDBType()!=DBType.KingBaseR3)
			sql = "begin " + sql + " end;";
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		try {
			conn = DBAccess.getGetAppCenterDBConn_Oracle();
			int i = 0;
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				i = pstmt.executeUpdate();
			} else {
				stmt = conn.createStatement();// 创建用于执行静态sql语句的Statement对象，st属局部变量
				i = stmt.executeUpdate(sql);
			}
			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + i);
			}
			return i;
		} catch (Exception ex) {
			String msg = "@运行更新在(RunSQL_200705_Ora)出错。\n  @SQL: " + sql + "\n  @Param: " + paras.getDebugInfo()
					+ "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * RunSQL_200705_MySQL
	 *
	 * param sql
	 * param paras
	 * @return
	 * @throws Exception
	 */
	private static int RunSQL_200705_MySQL(String sql, Paras paras) {

		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		boolean isTtrack = false;
		try {
			// 首先取具有事务的conn. @xushuhao
			conn = GetConnOfTransactionForMySQL(WebUser.getNo());

			if (conn == null)
				conn = DBAccess.getGetAppCenterDBConn_MySQL();
			else
				isTtrack = true;


			int i = 0;
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				i = pstmt.executeUpdate();

			} else {
				stmt = conn.createStatement();// 创建用于执行静态sql语句的Statement对象，st属局部变量
				i = stmt.executeUpdate(sql);
			}

			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + i);
			}
			// conn.commit();
			return i;
		} catch (Exception ex) {
			String msg = "@运行更新在(RunSQL_200705_MySQL)出错。\n  @SQL: " + sql + "\n  @Param: " + paras.getDebugInfo()
					+ "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {

				if (stmt != null)
					stmt.close();

				if (pstmt != null)
					pstmt.close();

				if (isTtrack == false && conn != null)
					conn.close();
				String msg = " @SQL: " + sql + "\n  @Param: " + (paras == null ? "" : paras.getDebugInfo());
				Log.DefaultLogWriteLineInfo(msg);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	private static int RunSQL_201902_PSQL(String sql, Paras paras){
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		try {
			conn = DBAccess.getGetAppCenterDBConn_PSQL();
			int i = 0;
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				i = pstmt.executeUpdate();
			} else {
				stmt = conn.createStatement();// 创建用于执行静态sql语句的Statement对象，st属局部变量
				i = stmt.executeUpdate(sql);
			}
			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + i);
			}
			return i;
		} catch (Exception ex) {
			String msg = "@运行更新在(RunSQL_201902_PSQL)出错。\n  @SQL: " + sql + "\n  @Param: " + paras.getDebugInfo()
					+ "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 适配达梦
	 * param sql
	 * param paras
	 * @return
	 */
	private static int RunSQL_20191230_DM(String sql, Paras paras)
	{
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		try {
			conn = DBAccess.getGetAppCenterDBConn_Oracle();
			int i = 0;
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				i = pstmt.executeUpdate();
			} else {
				stmt = conn.createStatement();// 创建用于执行静态sql语句的Statement对象，st属局部变量
				i = stmt.executeUpdate(sql);
			}
			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + i);
			}
			return i;
		} catch (Exception ex) {
			String msg = "@运行更新在(RunSQL_20191230_DM)出错。\n  @SQL: " + sql + "\n  @Param: " + paras.getDebugInfo()
					+ "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static int RunSQLReturnResultSet(String sql, Paras paras, Entity en, Attrs attrs) {

		if (DataType.IsNullOrEmpty(sql)) {
			throw new RuntimeException("要执行的 sql =null ");
		}

		try {
			switch (getAppCenterDBType()) {
			case MSSQL:
				return RunSQLReturnResultSet_201809_SQL(sql, paras, en, attrs);
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				return RunSQLReturnResultSet_201809_Ora(sql, paras, en, attrs);
			case MySQL:
			case GBASE8A:
			case GBASE8CByMySQL:
				return RunSQLReturnResultSet_201809_MySQL(sql, paras, en, attrs);
			case DM:
				return RunSQLReturnResultSet_201809_DM(sql, paras, en, attrs);
			case PostgreSQL:
			case HGDB:
				 return RunSQLReturnTable_201902_PSQL(sql, paras, en, attrs);
			default:
				throw new RuntimeException("@发现未知的数据库连接类型！");
			}
		} catch (RuntimeException ex) {
			throw ex;
		}
	}

	public static int RunSQLReturnResultSet(String sql, Paras paras, Entities ens, Attrs attrs) {

		if (DataType.IsNullOrEmpty(sql)) {
			throw new RuntimeException("要执行的 sql =null ");
		}

		try {
			ResultSet rs = null;
			switch (getAppCenterDBType()) {
			case MSSQL:
				return RunSQLReturnResultSet_201809_SQL(sql, paras, ens, attrs);
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				return RunSQLReturnResultSet_201809_Ora(sql, paras, ens, attrs);
			case MySQL:
			case GBASE8A:
			case GBASE8CByMySQL:
				return RunSQLReturnResultSet_201809_MySQL(sql, paras, ens, attrs);
			case DM:
			case HGDB:
			case PostgreSQL:
				return RunSQLReturnResultSet_201809_DM(sql, paras, ens, attrs);
			default:
				throw new RuntimeException("@发现未知的数据库连接类型！");
			}
		} catch (RuntimeException ex) {
			throw ex;
		}

	}

	public static DataTable RunSQLReturnTable(String sql, Paras paras) {
		if (DataType.IsNullOrEmpty(sql)) {
			throw new RuntimeException("要执行的 sql =null ");
		}
		try {
			DataTable dt = null;
			switch (getAppCenterDBType()) {
			case MSSQL:
				dt = RunSQLReturnTable_200705_SQL(sql, paras);
				break;
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				dt = RunSQLReturnTable_200705_Ora(sql, paras);
				break;
			case MySQL:
			case GBASE8A:
			case GBASE8CByMySQL:
				dt = RunSQLReturnTable_200705_MySQL(sql, paras);
				break;
			case DM:
				dt = RunSQLReturnTable_200705_DM(sql, paras);
				break;
			case PostgreSQL:
			case HGDB:
				dt = RunSQLReturnTable_201902_PSQL(sql, paras);
				break;
			default:
				throw new RuntimeException("@发现未知的数据库连接类型！");
			}
			return dt;
		} catch (RuntimeException ex) {
			throw ex;
		}
	}

	public static DataTable RunSQLReturnTable_200705_SQL(String sql, Paras paras) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		if (sql.trim().endsWith("WHERE")) {
			sql = sql.replace("WHERE", "");
		}
		try {
			conn = DBAccess.getGetAppCenterDBConn_MSSQL();
			DataTable oratb = new DataTable("otb");
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
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
									dr.setValue(i, null);
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
				rs = stmt.executeQuery(sql);
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
									dr.setValue(i, null);
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
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + oratb.Rows.size());
			}
			return oratb;
		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnTable_200705_SQL)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static int RunSQLReturnResultSet_201809_SQL(String sql, Paras paras, Entity en, Attrs attrs) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		if (sql.trim().endsWith("WHERE")) {
			sql = sql.replace("WHERE", "");
		}
		try {
			conn = DBAccess.getGetAppCenterDBConn_MSSQL();

			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				rs = pstmt.executeQuery();
			} else {
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(sql);
			}
			if (rs.next() == false)
				return 0;

			Hashtable ht = en.getRow();

			for (int idx = 0; idx < attrs.size(); idx++) {
				Attr attr = attrs.get(idx);

				Object val = rs.getObject(idx + 1);

				if (val == null) {
					if (attr.getItIsNum() == true)
						val = "";
					else
						val = "";
				}

				ht.put(attr.getKey(), val);

			}
			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + rs.getRow());
			}
			return 1;
		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnTable_200705_SQL)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static int RunSQLReturnResultSet_201809_SQL(String sql, Paras paras, Entities ens, Attrs attrs) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		if (sql.trim().endsWith("WHERE")) {
			sql = sql.replace("WHERE", "");
		}
		try {
			conn = DBAccess.getGetAppCenterDBConn_MSSQL();

			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				rs = pstmt.executeQuery();
			} else {
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(sql);
			}
			Attr attr = null;
			Entity myen = ens.getNewEntity();
			SQLCache sqlCache = myen.getSQLCache();
			bp.en.Map map = myen.getEnMap();

			while (rs.next()) {

				Entity en = ens.getNewEntity();
				en.setSQLCache(sqlCache);
				en.set_enMap(map);

				Hashtable ht = en.getRow();
				for (int idx = 0; idx < attrs.size(); idx++) {
					attr = attrs.get(idx);
					Object val = rs.getObject(idx + 1);
					if (val == null) {
						/*if (attr.getItIsNum() == true)
							val = 0;
						else*/
							val = "";
					}
					ht.put(attr.getKey(), val);
				}
				ens.add(en); // 加入里面去.
			}

			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + ens.size());
			}
			return ens.size();

		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnResultSet_201809_SQL)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static DataTable RunSQLReturnTable_200705_Ora(String sql, Paras paras) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		if (sql.trim().endsWith("WHERE")) {
			sql = sql.replace("WHERE", "");
		}
		try {
			conn = DBAccess.getGetAppCenterDBConn_Oracle();

			DataTable oratb = new DataTable("otb");
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
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
						if (SystemConfig.getAppCenterDBType()==DBType.KingBaseR3 &&(rsmd.getColumnType(i + 1)==2 ||rsmd.getColumnType(i + 1)==3)) {
							if (val == null) {
								dr.setValue(i, null);
							} else {
								if(val.toString().indexOf(".")!=-1)
									dr.setValue(i, rs.getBigDecimal(i+1));
								else
									dr.setValue(i, rs.getBigDecimal(i+1).longValue());
							}

						} else {
							dr.setValue(i, val);
						}
					}
					oratb.Rows.add(dr);// DataTable加入此DataRow
				}
			} else {
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();
				int size = rsmd.getColumnCount();
				for (int i = 0; i < size; i++) {
					oratb.Columns.Add(rsmd.getColumnName(i + 1), Para.getDAType(rsmd.getColumnType(i + 1)));
				}
				while (rs.next()) {
					DataRow dr = oratb.NewRow();// 產生一列DataRow

					for (int i = 0; i < size; i++) {

						Object val = rs.getObject(i + 1);
						if (SystemConfig.getAppCenterDBType()==DBType.KingBaseR3 &&(rsmd.getColumnType(i + 1)==2 ||rsmd.getColumnType(i + 1)==3)) {
							if (val == null) {
								dr.setValue(i, null);
							} else {
								if(val.toString().indexOf(".")!=-1)
									dr.setValue(i, rs.getBigDecimal(i+1));
								else
									dr.setValue(i, rs.getBigDecimal(i+1).longValue());
							}

						} else {
							dr.setValue(i, val);
						}
					}
					oratb.Rows.add(dr);// DataTable加入此DataRow
				}
			}
			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + oratb.Rows.size());
			}
			return oratb;
		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnTable_200705_Ora)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static int RunSQLReturnResultSet_201809_Ora(String sql, Paras paras, Entity en, Attrs attrs) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		if (sql.trim().endsWith("WHERE")) {
			sql = sql.replace("WHERE", "");
		}
		try {
			conn = DBAccess.getGetAppCenterDBConn_Oracle();
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				rs = pstmt.executeQuery();
			} else {
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(sql);
			}
			if (rs.next() == false)
				return 0;

			Hashtable ht = en.getRow();

			for (int idx = 0; idx < attrs.size(); idx++) {
				Attr attr = attrs.get(idx);

				Object val = rs.getObject(idx + 1);

				if (val == null) {
					/*if (attr.getItIsNum() == true)
						val = 0;
					else*/
						val = "";
				}
				if(SystemConfig.getAppCenterDBType() == DBType.KingBaseR3)
					ht.put(attr.getKey(), bp.sys.base.Glo.GenerRealType(attrs,attr.getKey(),val));
				else
					ht.put(attr.getKey(), val);

			}
			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + rs.getRow());
			}
			return 1;
		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnTable_200705_Ora)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static int RunSQLReturnResultSet_201809_DM(String sql, Paras paras, Entity en, Attrs attrs) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		if (sql.trim().endsWith("WHERE")) {
			sql = sql.replace("WHERE", "");
		}
		try {
			conn = DBAccess.getGetAppCenterDBConn_Oracle();
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				rs = pstmt.executeQuery();
			} else {
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(sql);
			}
			if (rs.next() == false)
				return 0;

			Hashtable ht = en.getRow();

			for (int idx = 0; idx < attrs.size(); idx++) {
				Attr attr = attrs.get(idx);

				Object val = rs.getObject(idx + 1);

				if (val == null) {
					/*if (attr.getItIsNum() == true)
						val = 0;
					else*/
						val = "";
				}

				ht.put(attr.getKey(), val);

			}
			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + rs.getRow());
			}
			return 1;
		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnTable_200705_Ora)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static int RunSQLReturnTable_201902_PSQL(String sql, Paras paras, Entity en, Attrs attrs) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		if (sql.trim().endsWith("WHERE")) {
			sql = sql.replace("WHERE", "");
		}
		try {
			conn = DBAccess.getGetAppCenterDBConn_Oracle();
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				rs = pstmt.executeQuery();
			} else {
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(sql);
			}
			if (rs.next() == false)
				return 0;

			Hashtable ht = en.getRow();

			for (int idx = 0; idx < attrs.size(); idx++) {
				Attr attr = attrs.get(idx);

				Object val = rs.getObject(idx + 1);

				if (val == null) {
					/*if (attr.getItIsNum() == true)
						val = 0;
					else*/
						val = "";
				}

				ht.put(attr.getKey(), val);

			}
			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + rs.getRow());
			}
			return 1;
		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnTable_200705_Ora)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	private static int RunSQLReturnResultSet_201809_Ora(String sql, Paras paras, Entities ens, Attrs attrs) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		if (sql.trim().endsWith("WHERE")) {
			sql = sql.replace("WHERE", "");
		}
		try {
			conn = DBAccess.getGetAppCenterDBConn_Oracle();
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				rs = pstmt.executeQuery();
			} else {
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(sql);
			}
			Attr attr = null;
			Entity myen = ens.getNewEntity();
			SQLCache sqlCache = myen.getSQLCache();
			bp.en.Map map = myen.getEnMap();

			while (rs.next()) {

				Entity en = ens.getNewEntity();
				en.setSQLCache(sqlCache);
				en.set_enMap(map);

				Hashtable ht = en.getRow();
				for (int idx = 0; idx < attrs.size(); idx++) {
					attr = attrs.get(idx);
					Object val = rs.getObject(idx + 1);
					if (val == null) {
						/*if (attr.getItIsNum() == true)
							val = 0;
						else*/
							val = "";
					}
					if(SystemConfig.getAppCenterDBType() == DBType.KingBaseR3)
						ht.put(attr.getKey(), bp.sys.base.Glo.GenerRealType(attrs,attr.getKey(),val));
					else
						ht.put(attr.getKey(), val);
				}
				ens.add(en); // 加入里面去.
			}

			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + ens.size());
			}
			return ens.size();

		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnTable_200705_Ora)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static int RunSQLReturnResultSet_201809_DM(String sql, Paras paras, Entities ens, Attrs attrs) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		if (sql.trim().endsWith("WHERE")) {
			sql = sql.replace("WHERE", "");
		}
		try {
			conn = DBAccess.getGetAppCenterDBConn_Oracle();
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				rs = pstmt.executeQuery();
			} else {
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(sql);
			}
			Attr attr = null;
			Entity myen = ens.getNewEntity();
			SQLCache sqlCache = myen.getSQLCache();
			bp.en.Map map = myen.getEnMap();

			while (rs.next()) {

				Entity en = ens.getNewEntity();
				en.setSQLCache(sqlCache);
				en.set_enMap(map);
				// en.setRow( sqlCache.CreateNewRow() );

				Hashtable ht = en.getRow();
				for (int idx = 0; idx < attrs.size(); idx++) {
					attr = attrs.get(idx);
					Object val = rs.getObject(idx + 1);
					if (val == null) {
						/*if (attr.getItIsNum() == true)
							val = 0;
						else*/
							val = "";
					}
					ht.put(attr.getKey(), val);
				}
				ens.add(en); // 加入里面去.
			}

			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + ens.size());
			}
			return ens.size();

		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnResultSet_201809_DM)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * RunSQLReturnTable_200705_MySQL
	 *
	 * param sql
	 *            要执行的sql
	 * @return 返回table
	 * @throws Exception
	 */
	private static DataTable RunSQLReturnTable_200705_MySQL(String sql, Paras paras) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;

		try {
			conn = DBAccess.getGetAppCenterDBConn_MySQL();
			DataTable oratb = new DataTable("otb");
			String nameLabel = "";
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				rs = pstmt.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				int size = rsmd.getColumnCount();
				for (int i = 0; i < size; i++) {
					// 原来的 oratb.Columns.Add(rsmd.getColumnName(i + 1),
					// Para.getDAType(rsmd.getColumnType(i + 1)));
					// mysql取值导致流程名称重复，改为label取值
					nameLabel = rsmd.getColumnLabel(i + 1);
					oratb.Columns.Add(nameLabel, Para.getDAType(rsmd.getColumnType(i + 1)));
				}
				while (rs.next()) {
					DataRow dr = oratb.NewRow();// 產生一列DataRow
					for (int i = 0; i < size; i++) {
						Object val = rs.getObject(i + 1);
						// Object val = rs.getObject(i + 1);
						if (dr != null && dr.columns.size() > 0 && dr.columns.get(i).DataType != null) {
							if (dr.columns.get(i).DataType.toString().contains("Integer")
									|| dr.columns.get(i).DataType.toString().contains("Float")) {
								if (val == null) {
									dr.setValue(i, null);
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
			} else {

				if("bp".equals(SystemConfig.getRunOnPlant())) {
					stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				}else { // jeesite平台时
					stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
				}

				rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();
				int size = rsmd.getColumnCount();
				for (int i = 0; i < size; i++) {
					// oratb.Columns.Add(rsmd.getColumnName(i + 1),
					// Para.getDAType(rsmd.getColumnType(i + 1)));
					nameLabel = rsmd.getColumnLabel(i + 1);
					oratb.Columns.Add(nameLabel, Para.getDAType(rsmd.getColumnType(i + 1)));
				}
				while (rs.next()) {
					DataRow dr = oratb.NewRow();// 產生一列DataRow
					for (int i = 0; i < size; i++) {

						Object val = rs.getObject(i + 1);
						if (dr != null && dr.columns.size() > 0 && dr.columns.get(i).DataType != null) {
							if (dr.columns.get(i).DataType.toString().contains("Integer")
									|| dr.columns.get(i).DataType.toString().contains("Float")) {
								if (val == null) {
									dr.setValue(i, null);
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
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras!=null?paras.getDebugInfo():"" + ", Result: Rows=" + oratb.Rows.size());
			}
			return oratb;
		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnTable_200705_MySQL)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * RunSQLReturnTable_200705_DM
	 *
	 * param sql
	 *            要执行的sql
	 * @return 返回table
	 * @throws Exception
	 */
	private static DataTable RunSQLReturnTable_200705_DM(String sql, Paras paras) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		if (sql.trim().endsWith("WHERE")) {
			sql = sql.replace("WHERE", "");
		}
		try {
			conn = DBAccess.getGetAppCenterDBConn_Oracle();

			DataTable oratb = new DataTable("otb");
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				rs = pstmt.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				int size = rsmd.getColumnCount();
				for (int i = 0; i < size; i++) {
					String s = rsmd.getColumnLabel(i + 1);
					if ("NO".equals(s) && SystemConfig.getAppCenterDBType() == DBType.DM){
						s="No";
					}
					oratb.Columns.Add(s, Para.getDAType(rsmd.getColumnType(i + 1)));
				}
				while (rs.next()) {
					DataRow dr = oratb.NewRow();// 產生一列DataRow
					for (int i = 0; i < size; i++) {
						Object val = rs.getObject(i + 1);
						if(dr.columns.get(i).DataType.toString().contains("String")){
							val = rs.getString(i + 1);
						}
						if (dr.columns.get(i).DataType.toString().contains("BigDecimal")) {
							if (val == null) {
								dr.setValue(i, null);
							} else {
								dr.setValue(i, val);
							}

						} else {
							dr.setValue(i, val);
						}
					}
					oratb.Rows.add(dr);// DataTable加入此DataRow
				}
			} else {
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();
				int size = rsmd.getColumnCount();
				for (int i = 0; i < size; i++) {
					String s = rsmd.getColumnLabel(i + 1);
					if ("NO".equals(s) && SystemConfig.getAppCenterDBType() == DBType.DM){
						s="No";
					}
					oratb.Columns.Add(s, Para.getDAType(rsmd.getColumnType(i + 1)));
				}
				while (rs.next()) {
					DataRow dr = oratb.NewRow();// 產生一列DataRow

					for (int i = 0; i < size; i++) {

						Object val = rs.getObject(i + 1);
						if(dr.columns.get(i).DataType.toString().contains("String")){
							val = rs.getString(i + 1);
						}
						if (dr.columns.get(i).DataType.toString().contains("BigDecimal")) {
							if (val == null) {
								dr.setValue(i, null);
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
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + oratb.Rows.size());
			}
			return oratb;
		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnTable_200705_DM)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static DataTable RunSQLReturnTable_201902_PSQL(String sql, Paras paras) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;
		if (sql.trim().endsWith("WHERE")) {
			sql = sql.replace("WHERE", "");
		}
		try {
			conn = DBAccess.getGetAppCenterDBConn_PSQL();
			DataTable oratb = new DataTable("otb");
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
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
						if(dr.columns.get(i).DataType.toString().contains("String")){
							val = rs.getString(i + 1);
						}
						if (dr.columns.get(i).DataType.toString().contains("BigDecimal")) {
							if (val == null) {
								dr.setValue(i, null);
							} else {
								dr.setValue(i, val);
							}

						} else {
							dr.setValue(i, val);
						}
					}
					oratb.Rows.add(dr);// DataTable加入此DataRow
				}
			} else {
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();
				int size = rsmd.getColumnCount();
				for (int i = 0; i < size; i++) {
					oratb.Columns.Add(rsmd.getColumnName(i + 1), Para.getDAType(rsmd.getColumnType(i + 1)));
				}
				while (rs.next()) {
					DataRow dr = oratb.NewRow();// 產生一列DataRow

					for (int i = 0; i < size; i++) {

						Object val = rs.getObject(i + 1);
						if(dr.columns.get(i).DataType.toString().contains("String")){
							val = rs.getString(i + 1);
						}
						if (dr.columns.get(i).DataType.toString().contains("BigDecimal")) {
							if (val == null) {
								dr.setValue(i, null);
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
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + oratb.Rows.size());
			}
			return oratb;
		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnTable_201902_PSQL)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	private static int RunSQLReturnResultSet_201809_MySQL(String sql, Paras paras, Entities ens, Attrs attrs) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;

		try {
			conn = DBAccess.getGetAppCenterDBConn_MySQL();
			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				rs = pstmt.executeQuery();
			} else {
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(sql);
			}

			Attr attr = null;
			Entity myen = ens.getNewEntity();
			SQLCache sqlCache = myen.getSQLCache();
			bp.en.Map map = myen.getEnMap();

			while (rs.next()) {

				Entity en = ens.getNewEntity();
				en.setSQLCache(sqlCache);
				en.set_enMap(map);
				// en.setRow( sqlCache.CreateNewRow() );

				Hashtable ht = en.getRow();
				for (int idx = 0; idx < attrs.size(); idx++) {
					attr = attrs.get(idx);
					Object val = rs.getObject(idx + 1);
					if (val == null) {
						/*if (attr.getItIsNum() == true)
							val = 0;
						else*/
							val = "";
					}
					ht.put(attr.getKey(), val);
				}
				ens.add(en); // 加入里面去.
			}

			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + rs.getRow());
			}
			return ens.size();

		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnResultSet_201809_MySQL)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
				String msg = "@SQL: " + sql + "\n  @Param: " + (paras == null ? "" : paras.getDebugInfo());
				Log.DefaultLogWriteLineInfo(msg);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static int RunSQLReturnResultSet_201809_MySQL(String sql, Paras paras, Entity en, Attrs attrs) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		NamedParameterStatement pstmt = null;

		try {
			conn = DBAccess.getGetAppCenterDBConn_MySQL();

			if (null != paras && paras.size() > 0) {
				pstmt = new NamedParameterStatement(conn, sql);
				PrepareCommand(pstmt, paras);
				rs = pstmt.executeQuery();
			} else {
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(sql);
			}

			if (rs.next() == false)
				return 0;

			Hashtable ht = en.getRow();

			for (int idx = 0; idx < attrs.size(); idx++) {
				Attr attr = attrs.get(idx);

				Object val = rs.getObject(idx + 1);

				if (val == null) {
					/*if (attr.getItIsNum() == true)
						val = 0;
					else*/
						val = "";
				}

				ht.put(attr.getKey(), val);

				// ht.pu
				// en.ger
				// en.SetValByKey(attr.getKey(), val);
			}

			if (Log.isLoggerDebugEnabled()) {
				Log.DefaultLogWriteLineDebug("SQL: " + sql);
				Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + rs.getRow());
			}
			return 1;

		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnResultSet_201809_MySQL)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
				String msg = "@SQL: " + sql + "\n  @Param: " + (paras == null ? "" : paras.getDebugInfo());
				Log.DefaultLogWriteLineInfo(msg);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
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
					ps.setInt(para.ParaName, (int)para.val);
				} else if (para.DAType == Float.class) {
					ps.setFloat(para.ParaName, (Float) para.val);
				} else if (para.DAType == Double.class) {
					ps.setDouble(para.ParaName, (Double) para.val);
				} else if (para.DAType == BigDecimal.class) {
					ps.setBigDecimal(para.ParaName, (BigDecimal) para.val);
				} else if (para.DAType == Date.class) {
					Date date = (Date) para.val;
					if (date == null) {
						ps.setDate(para.ParaName, null);
					} else {
						ps.setDate(para.ParaName, new java.sql.Date(date.getTime()));
					}
				} else if (para.DAType == Boolean.class) {
					ps.setBoolean(para.ParaName, (Boolean) para.val);
				}else if (para.DAType == ArrayList.class){
					ps.setNull(para.ParaName,Types.NULL);
				}
			}
		} catch (SQLException ex) {
			String msg = "@运行查询在(PrepareCommand)出错  @异常信息：" + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg, ex);
		}
	}
	private static void PrepareCallProCommand(CallableStatement ps, Paras params) throws Exception {
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
				} else if (para.DAType == Date.class) {
					Date date = (Date) para.val;
					if (date == null) {
						ps.setDate(para.ParaName, null);
					} else {
						ps.setDate(para.ParaName, new java.sql.Date(date.getTime()));
					}
				} else if (para.DAType == Boolean.class) {
					ps.setBoolean(para.ParaName, (Boolean) para.val);
				}else if (para.DAType == ArrayList.class){
					ps.setNull(para.ParaName,Types.NULL);
				}
			}
		} catch (SQLException ex) {
			String msg = "@运行查询在(PrepareCommand)出错  @异常信息：" + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg, ex);
		}
	}
	// 在当前Connection上执行
	public static DataTable RunSQLReturnTable(Paras ps) {
		return RunSQLReturnTable(ps.SQL, ps);
	}

	public static int RunSQLReturnTableCount = 0;

	/**
	 * 传递一个select 语句返回一个查询结果集合。
	 *
	 * param sql
	 *            select sql
	 * @return 查询结果集合DataTable
	 * @throws Exception
	 */
	public static DataTable RunSQLReturnTable(String sql) {
		Paras ps = new Paras();
		return RunSQLReturnTable(sql, ps);
	}

	public static DataTable RunSQLReturnTable(String sql, String key1, Object v1, String key2, Object v2)
			throws Exception {
		Paras ens = new Paras();
		ens.Add(key1, v1);
		ens.Add(key2, v2);
		return RunSQLReturnTable(sql, ens);
	}

	public static DataTable RunSQLReturnTable(String sql, String key, Object val) {
		Paras ens = new Paras();
		ens.Add(key, val);
		return RunSQLReturnTable(sql, ens);
	}

	private static boolean lockRunSQLReTable = false;

	public static DataSet RunSQLReturnDataSet(String sqls) {
		DataSet ds = new DataSet();
		String[] str = sqls.split(";");
		for (String s : str) {
			if (DataType.IsNullOrEmpty(s) == true)
				continue;
			DataTable dt = DBAccess.RunSQLReturnTable(s);
			ds.Tables.add(dt);
		}
		return ds;
	}

	// 查询单个值的方法.

	// OleDbConnection
	public static float RunSQLReturnValFloat(Paras ps) throws Exception {
		return RunSQLReturnValFloat(ps.SQL, ps, 0);
	}

	public static float RunSQLReturnValFloat(String sql, Paras ps, float val) throws Exception {
		ps.SQL=sql;
		Object obj = DBAccess.RunSQLReturnVal(ps);

		try {
			if (obj == null || obj.toString().equals("")) {
				return val;
			} else {
				return Float.parseFloat(obj.toString());
			}
		} catch (RuntimeException ex) {
			throw new RuntimeException(ex.getMessage() + sql + " @OBJ=" + obj, ex);
		}
	}

	/**
	 * 运行sql返回float
	 *
	 * param sql
	 *            要执行的sql,返回一行一列.
	 * param isNullAsVal
	 *            如果是空值就返回的默认值
	 * @return float的返回值
	 * @throws Exception
	 */
	public static float RunSQLReturnValFloat(String sql, float isNullAsVal) throws Exception {
		return RunSQLReturnValFloat(sql, new Paras(), isNullAsVal);
	}

	public static float RunSQLReturnValFloat(String sql) throws Exception {
		try {
			return Float.parseFloat(DBAccess.RunSQLReturnVal(sql).toString());
		} catch (RuntimeException ex) {
			throw new RuntimeException(ex.getMessage() + sql, ex);
		}
	}

	public static int RunSQLReturnValInt(Paras ps, int IsNullReturnVal) {
		return RunSQLReturnValInt(ps.SQL, ps, IsNullReturnVal);
	}
	public static int RunSQLReturnValInt(String sql, int IsNullReturnVal) {
		Object obj = "";
		obj = DBAccess.RunSQLReturnVal(sql);
		if (obj == null || obj.toString().equals("")) {
			return IsNullReturnVal;
		} else {
			if (obj.toString().indexOf(".") != -1) {
				return Integer.parseInt(obj.toString().substring(0, obj.toString().indexOf(".")));
			} else {
				return Integer.parseInt(obj.toString());
			}

		}
	}

	public static long RunSQLReturnValLong(String sql, int IsNullReturnVal) {
		Object obj = "";
		obj = DBAccess.RunSQLReturnVal(sql);
		if (obj == null || obj.toString().equals("")) {
			return IsNullReturnVal;
		} else {
			if (obj.toString().indexOf(".") != -1) {
				return Long.parseLong(obj.toString().substring(0, obj.toString().indexOf(".")));
			} else {
				return Long.parseLong(obj.toString());
			}

		}
	}

	public static int RunSQLReturnValInt(String sql, int IsNullReturnVal, Paras paras) throws Exception {
		Object obj = "";

		obj = DBAccess.RunSQLReturnVal(sql, paras);
		if (obj == null || obj.toString().equals("")) {
			return IsNullReturnVal;
		} else {
			return Integer.parseInt(obj.toString());
		}
	}

	public static BigDecimal RunSQLReturnValDecimal(String sql, BigDecimal IsNullReturnVal, int blws) {
		Paras ps = new Paras();
		ps.SQL=sql;
		return RunSQLReturnValDecimal(ps, IsNullReturnVal, blws);
	}

	public static BigDecimal RunSQLReturnValDecimal(Paras ps, BigDecimal IsNullReturnVal, int blws) {
		try {
			Object obj = DBAccess.RunSQLReturnVal(ps);
			if (obj == null || "".equals(obj.toString())) {
				return IsNullReturnVal;
			} else {
				BigDecimal d = new BigDecimal(Double.valueOf(obj.toString()));
				/*
				 * warning return BigDecimal.(d, blws);
				 */
				return d.setScale(blws, BigDecimal.ROUND_HALF_UP);
			}
		} catch (RuntimeException ex) {
			throw new RuntimeException("RunSQLReturnValDecimal " + ex.getMessage() + ps.SQL, ex);
		}
	}

	// 在当前的Connection执行 SQL 语句，返回首行首列
	public static int RunSQLReturnCOUNT(String sql) {
		return RunSQLReturnTable(sql).Rows.size();
	}

	public static int RunSQLReturnValInt(Paras ps) {

		String str = DBAccess.RunSQLReturnString(ps.SQL, ps);
		if (str.contains(".")) {
			str = str.substring(0, str.indexOf("."));
		}
		try {
			if (DataType.IsNullOrEmpty(str)) {
				return 0;
			}
			return Integer.parseInt(str);
		} catch (RuntimeException ex) {
			throw new RuntimeException("@" + ps.SQL + "   Val=" + str + ex.getMessage(), ex);
		}
	}

	public static int RunSQLReturnValInt(String sql) {

		Object obj = DBAccess.RunSQLReturnVal(sql);
		/*
		 * warning if (obj == null || obj == DBNull.getValue()) { throw new
		 * RuntimeException("@没有获取您要查询的数据,请检查SQL:" + sql +
		 * " @关于查询出来的详细信息已经记录日志文件，请处理。"); }
		 */
		if (obj == null) {
			throw new RuntimeException("@没有获取您要查询的数据,请检查SQL:" + sql + " @关于查询出来的详细信息已经记录日志文件，请处理。");
		}
		String s = obj.toString();
		if (s.contains(".")) {
			s = s.substring(0, s.indexOf("."));
		}
		return Integer.parseInt(s);
	}

	public static int RunSQLReturnValInt(String sql, Paras paras) {
		return DBAccess.RunSQLReturnValInt(sql, paras, 0);
	}

	public static int RunSQLReturnValInt(String sql, Paras paras, int isNullAsVal) {
		try {
			Object obj = DBAccess.RunSQLReturnVal(sql, paras);
			if (obj == null ) {
				return isNullAsVal;
			} else {
				return Integer.parseInt(obj.toString());
			}
		} catch (java.lang.Exception e) {
			return isNullAsVal;
		}
	}

	public static String RunSQLReturnString(String sql, Paras ps) {
		if (ps == null) {
			ps = new Paras();
		}
		Object obj = DBAccess.RunSQLReturnVal(sql, ps);
		if (obj == null) {
			return null;
		} else {
			return obj.toString();
		}
	}

	/**
	 * 执行查询返回结果,如果为dbNull 返回 null.
	 *
	 * param sql
	 *            will run sql.
	 * @return ,如果为dbNull 返回 null.
	 * @throws Exception
	 */
	public static String RunSQLReturnString(String sql) {
		return RunSQLReturnString(sql, new Paras());

	}

	public static String RunSQLReturnStringIsNull(Paras ps, String isNullAsVal) throws Exception {
		String v = RunSQLReturnString(ps);
		if (v == null) {
			return isNullAsVal;
		} else {
			return v;
		}
	}

	/**
	 * 运行sql返回一个值
	 *
	 * @param sql
	 * @param isNullAsVal
	 * @return
	 * @throws Exception
	 */
	public static String RunSQLReturnStringIsNull(String sql, String isNullAsVal) {
		String s = RunSQLReturnString(sql, new Paras());
		if (s == null) {
			return isNullAsVal;
		}
		return s;
	}

	public static String RunSQLReturnString(Paras ps) {
		return RunSQLReturnString(ps.SQL, ps);
	}

	// 在当前的Connection执行 SQL 语句，返回首行首列
	public static Object RunSQLReturnVal(String sql, String pkey, Object val) throws Exception {
		Paras ps = new Paras();
		ps.Add(pkey, val);
		return RunSQLReturnVal(sql, ps);
	}

	public static Object RunSQLReturnVal(String sql, Paras paras) {
		RunSQLReturnTableCount++;
		DataTable dt = null;
		switch (SystemConfig.getAppCenterDBType()) {
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				dt = DBAccess.RunSQLReturnTable_200705_Ora(sql, paras);
				break;
			case MSSQL:
				dt = DBAccess.RunSQLReturnTable_200705_SQL(sql, paras);
				break;
			case MySQL:
			case GBASE8CByMySQL:
			case GBASE8A:
				dt = DBAccess.RunSQLReturnTable_200705_MySQL(sql, paras);
				break;
			case DM:
				dt = DBAccess.RunSQLReturnTable_200705_DM(sql, paras);
				break;
			case PostgreSQL:
			case HGDB:
				dt = DBAccess.RunSQLReturnTable_201902_PSQL(sql, paras);
				break;
			default:
				throw new RuntimeException("@没有判断的数据库类型");
		}
		if (dt.Rows.size() == 0) {
			return null;
		}
		return dt.Rows.get(0).getValue(0);
	}

	public static Object RunSQLReturnVal(Paras ps) {
		return RunSQLReturnVal(ps.SQL, ps);
	}

	public static Object RunSQLReturnVal(String sql) {
		RunSQLReturnTableCount++;
		DataTable dt = null;
		// System.out.print(SystemConfig.getAppCenterDBType());
		switch (SystemConfig.getAppCenterDBType()) {
		case Oracle:
		case KingBaseR3:
		case KingBaseR6:
		case GBASE8CByOracle:
			dt = DBAccess.RunSQLReturnTable_200705_Ora(sql, new Paras());
			break;
		case MSSQL:
			dt = DBAccess.RunSQLReturnTable_200705_SQL(sql, new Paras());
			break;
		case MySQL:
		case GBASE8CByMySQL:
		case GBASE8A:
			dt = DBAccess.RunSQLReturnTable_200705_MySQL(sql, new Paras());
			break;
		case DM:
			dt = DBAccess.RunSQLReturnTable_200705_DM(sql, new Paras());
			break;
		case PostgreSQL:
		case HGDB:
			dt = DBAccess.RunSQLReturnTable_201902_PSQL(sql, new Paras());
			break;
		default:
			throw new RuntimeException("@没有判断的数据库类型");
		}
		if (dt.Rows.size() == 0)
			return null;

		return dt.Rows.get(0).getValue(0);
	}

	// 检查是不是存在
	/**
	 * 检查是不是存在
	 *
	 * param sql
	 *            sql
	 * @return 检查是不是存在
	 * @throws Exception
	 */
	public static boolean IsExits(String sql) {
		if (RunSQLReturnVal(sql) == null) {
			return false;
		}
		return true;
	}

	public static boolean IsExits(String sql, Paras ps) {
		if (RunSQLReturnVal(sql, ps) == null) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否存在主键pk .
	 *
	 * param tab
	 *            物理表
	 * @return 是否存在
	 * @throws Exception
	 */
	public static boolean IsExitsTabPK(String tab) {
		if (DBAccess.GetTablePKName(tab) == null)
			return false;
		else
			return true;

		/*Paras ps = new Paras();
		ps.Add("Tab", tab);
		String sql = "";
		switch (getAppCenterDBType()) {
		case MSSQL:
			sql = "SELECT column_name, table_name,CONSTRAINT_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE table_name =:Tab ";
			break;
		case Oracle:
			sql = "SELECT constraint_name, constraint_type,search_condition, r_constraint_name  from user_constraints WHERE table_name = upper(:Tab) AND constraint_type = 'P'";
			break;
		case MySQL:
			sql = "SELECT column_name, table_name, CONSTRAINT_NAME from INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE table_name =:Tab and table_schema='"
					+ SystemConfig.getAppCenterDBDatabase() + "' ";
			break;
		case DM:
			sql = "SELECT constraint_name, constraint_type,search_condition, r_constraint_name  from user_constraints WHERE table_name = upper(:Tab) AND constraint_type = 'P'";
			break;
		case KingBaseR3:
		case KingBaseR6:
			sql = "SELECT column_name, table_name, CONSTRAINT_NAME from INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE upper(table_name) =upper(:Tab) ";
			break;
		case PostgreSQL:
		case HGDB:
			sql = " SELECT ";
			sql += " pg_constraint.conname AS pk_name ";
			sql += " FROM ";
			sql += " pg_constraint ";
			sql += " INNER JOIN pg_class ON pg_constraint.conrelid = pg_class.oid ";
			sql += " WHERE ";
			sql += " pg_class.relname =:Tab ";
			sql += " AND pg_constraint.contype = 'p' ";
			ps.Add("Tab", tab.toLowerCase());
			break;
		default:
			throw new RuntimeException("数据库连接配置失败！ ");
		}
		DataTable dt = DBAccess.RunSQLReturnTable(sql, ps);
		if (dt.Rows.size() >= 1) {
			return true;
		} else {
			return false;
		}*/
	}

	/**
	 * 判断系统中是否存在对象.
	 *
	 * param obj
	 * @return
	 * @throws Exception
	 */
	public static boolean IsExitsObject(String obj) throws Exception {
		return IsExitsObject(new DBUrl(DBUrlType.AppCenterDSN), obj);
	}

	/**
	 判断系统中是否存在对象.

	 param
	 @return
	*/
	public static boolean IsExitsObject(DBUrl dburl, String obj) throws Exception {
		//有的同事写的表名包含dbo.导致创建失败.
		obj = obj.replace("dbo.", "");
		obj=obj.trim();

		// 增加参数.
		Paras ps = new Paras();
		ps.Add("obj", obj);
		try
		{
			switch (getAppCenterDBType())
			{
				case Oracle:
				case DM:
					if (obj.indexOf(".") != -1)
					{
						obj = obj.split("[.]", -1)[1];
					}
					return IsExits("select object_name from all_objects WHERE  object_name = upper(:obj) and OWNER='" + SystemConfig.getUser().toUpperCase() + "' ", ps);
//				case DM:
//					if (obj.indexOf(".") != -1)
//					{
//						obj = obj.split("[.]", -1)[1];
//					}
//					return IsExits("select object_name from all_objects WHERE  object_name = upper(:obj) and OWNER='" + SystemConfig.getUser().toUpperCase() + "' ", ps);
				case MSSQL:
					return IsExits("SELECT name FROM sysobjects WHERE name = '" + obj + "'");
				case PostgreSQL:
				case HGDB:
				case GBASE8CByOracle:
					return IsExits("SELECT relname FROM pg_class WHERE relname = '" + obj.toLowerCase() + "'");
				case Informix:
					return IsExits("select tabname from systables where tabname = '" + obj.toLowerCase() + "'");
				case MySQL:
				case GBASE8A:
				case GBASE8CByMySQL:
					/*如果不是检查的PK.*/
					if (obj.indexOf(".") != -1)
					{
						obj = obj.split("[.]", -1)[1];
					}

					// *** 屏蔽到下面的代码, 不需要从那个数据库里取，jflow 发现的bug  edit by :zhoupeng   2016.01.26 for fuzhou.
					return IsExits("SELECT table_name, table_type FROM information_schema.tables  WHERE  table_name = '" + obj + "' AND TABLE_SCHEMA='" + SystemConfig.getAppCenterDBDatabase() + "' ");

				case Access:
					//return false ; //IsExits("SELECT * FROM MSysObjects WHERE (((MSysObjects.getName()) =  '"+obj+"' ))");
					return IsExits("SELECT * FROM MSysObjects WHERE Name =  '" + obj + "'");
				case KingBaseR3:
				case KingBaseR6:
					if (obj.indexOf(".") != -1)
					{
						obj = obj.split("[.]", -1)[1];
					}
					return IsExits("select object_name from all_objects WHERE  object_name = :obj ", ps);

				default:
					throw new RuntimeException("没有识别的数据库编号");
			}
		}catch (Exception ex)
		{
			throw new Exception("err@判断对象是否存在出现错误,请检查数据库设置不能区分大消息,参考:https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=9400758&doc_id=31094, 详细信息:" + ex.getMessage());
		}
	}

	/**
	 * 是否存在索引？
	 * @param table 表名称
	 * @param indexName 索引名称
	 * @return 是否存在索引？
	 */
	public static Boolean IsExitsTableIndex(String table, String indexName) throws Exception {
		String sql = "";

		int i = 0;
		switch (DBAccess.getAppCenterDBType())
		{
			case MSSQL:
				i = DBAccess.RunSQLReturnValInt("SELECT COUNT(*) FROM sys.indexes  WHERE object_id=OBJECT_ID('" + table + "', N'U') and NAME='" + indexName + "'", 0);
				break;
			case MySQL:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql = "SELECT count(*) FROM information_schema.statistics WHERE table_schema='" + bp.difference.SystemConfig.getAppCenterDBDatabase() + "' AND table_name = '" + table + "' AND index_name = '" + indexName + "' ";
				i = DBAccess.RunSQLReturnValInt(sql);
				break;
			case PostgreSQL:
			case UX:
			case HGDB:
				sql = "SELECT count(*) FROM pg_indexes WHERE  tablename = '" + table.toLowerCase() + "' and indexname = '" + indexName.toLowerCase() + "'";
				//string sql1 = "select count(*) from information_schema.statistics where   table_name ='" + table.ToLower() + "' and  index_name='" + indexName.ToLower() + "'";
				i = DBAccess.RunSQLReturnValInt(sql);
				break;
			case DM:
				if (table.indexOf(".") != -1)
					table = table.split(".")[1];
				i = DBAccess.RunSQLReturnValInt("SELECT COUNT(*) from user_indexes   WHERE table_name= upper('" + table + "') ");
				break;
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				if (table.indexOf(".") != -1)
					table = table.split(".")[1];
				i = DBAccess.RunSQLReturnValInt("SELECT COUNT(*) from user_indexes   WHERE table_name= upper('" + table + "') ");
				break;

			default:
				throw new Exception("err@IsExitsTableCol没有判断的数据库类型.");
		}
        return i >= 1;
	}

	/**
	 * 表中是否存在指定的列
	 *
	 * param table
	 *            表名
	 * param col
	 *            列名
	 * @return 是否存在
	 * @throws Exception
	 */
	public static boolean IsExitsTableCol(String table, String col) throws Exception {
		Paras ps = new Paras();
		ps.Add("tab", table);
		ps.Add("col", col);

		int i = 0;
		switch (DBAccess.getAppCenterDBType()) {
		// case Access:
		// return false;
		// break;
		case MSSQL:
			i = DBAccess.RunSQLReturnValInt("SELECT  COUNT(*) FROM information_schema.COLUMNS  WHERE TABLE_NAME='"
					+ table + "' AND COLUMN_NAME='" + col + "'", 0);
			break;
		case MySQL:
		case GBASE8CByMySQL:
		case GBASE8A:
			String sql = "select count(*) FROM information_schema.columns WHERE TABLE_SCHEMA='"
					+ SystemConfig.getAppCenterDBDatabase() + "' AND table_name ='" + table
					+ "' and column_Name='" + col + "'";
			i = DBAccess.RunSQLReturnValInt(sql);
			break;
		case Oracle:
		case KingBaseR3:
		case KingBaseR6:
		case GBASE8CByOracle:
			if (table.indexOf(".") != -1) {
				table = table.split("[.]", -1)[1];
			}
			i = DBAccess.RunSQLReturnValInt(
					"SELECT COUNT(*) from user_tab_columns  WHERE table_name= upper(:tab) AND column_name= upper(:col) ",
					ps);
			break;
		case DM:
			if (table.indexOf(".") != -1) {
				table = table.split("[.]", -1)[1];
			}
			i = DBAccess.RunSQLReturnValInt(
					"SELECT COUNT(*) AS CNT  FROM DBA_TAB_COLUMNS where TABLE_NAME=upper(:tab) and COLUMN_NAME = upper(:col) AND OWNER='" + SystemConfig.getUser().toUpperCase() + "'",
					ps);
			break;
		case PostgreSQL:
		case HGDB:
			String sql1 = "select count(*) from information_schema.columns where   table_name ='" + table.toLowerCase() + "' and  column_name='" + col.toLowerCase() + "'";
			i = DBAccess.RunSQLReturnValInt(sql1);
			break;
		default:
			throw new RuntimeException("error");
		}
		if (i == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获得表的基础信息，返回如下列: 1, 字段名称，字段描述，字段类型，字段长度.
	 *
	 * param tableName
	 *            表名
	 */
	public static DataTable GetTableSchema(String tableName) {
		String sql = "";
		switch (SystemConfig.getAppCenterDBType()) {
		case MSSQL:
			sql = "SELECT column_name as FName, data_type as FType, CHARACTER_MAXIMUM_LENGTH as FLen , column_name as FDesc FROM information_schema.columns where table_name='"
					+ tableName + "'";
			break;
		case Oracle:
		case KingBaseR3:
		case KingBaseR6:
		case DM:
		case GBASE8CByOracle:
			sql = "SELECT COLUMN_NAME as FName,DATA_TYPE as FType,DATA_LENGTH as FLen,COLUMN_NAME as FDesc FROM all_tab_columns WHERE table_name = upper('"
					+ tableName + "')";
			break;
		case MySQL:
		case GBASE8CByMySQL:
		case GBASE8A:
			sql = "SELECT COLUMN_NAME FName,DATA_TYPE FType,CHARACTER_MAXIMUM_LENGTH FLen,COLUMN_COMMENT FDesc FROM information_schema.columns WHERE table_name='"
					+ tableName + "' and TABLE_SCHEMA='" + SystemConfig.getAppCenterDBDatabase() + "'";
			break;

		default:
			break;
		}

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel()!=FieldCaseModel.None ) {
			dt.Columns.get(0).setColumnName("FName");
			dt.Columns.get(1).setColumnName("FType");
			dt.Columns.get(2).setColumnName("FLen");
			dt.Columns.get(3).setColumnName("FDesc");
		}
		return dt;
	}

	/**
	 * 安装前检查数据库用户权限
	 *
	 * @return
	 */
	public static boolean isCanInstall() {
		String checkSql = "CREATE TABLE CHECKDB(NO int)";
		try {
			// 建表
			RunSQL(checkSql);
			// 建视图
			checkSql = "CREATE VIEW CHECKDBVIEW AS SELECT * FROM CHECKDB";
			RunSQL(checkSql);
			// 删除表
			checkSql = "DROP TABLE CHECKDB";
			RunSQL(checkSql);
			// 删除视图
			checkSql = "DROP VIEW CHECKDBVIEW";
			RunSQL(checkSql);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static void AlterColumnToBigInt(String table, String fieldName) {
		String sql = "";
		switch (SystemConfig.getAppCenterDBType()) {
			case Oracle:
			case DM:
			case MSSQL:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				sql = "ALTER TABLE " + table + " MODIFY " + fieldName + " NUMBER";
				break;
			case PostgreSQL:
			case HGDB:
				sql = "ALTER TABLE " + table.toLowerCase() + " ALTER COLUMN " + fieldName.toLowerCase() + " SET DATA TYPE BIGINT";
				break;
			case MySQL:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql = "ALTER TABLE " + table + " MODIFY " + fieldName + " bigint(64)";
				break;

			default:
				Log.DebugWriteError("修改MyPK类型错误： " + "@不支持的数据库类型." + SystemConfig.getAppCenterDBType());
				throw new RuntimeException("@不支持的数据库类型." + SystemConfig.getAppCenterDBType());
		}
		DBAccess.RunSQL(sql);
	}

	/**
	 * 删除表的主键
	 *
	 * param table
	 *            表名称
	 */
	public static void DropTablePK(String table) {
		String pkName = DBAccess.GetTablePKName(table);
		if (pkName == null) {
			return;
		}

		String sql = "";
		switch (SystemConfig.getAppCenterDBType()) {
		case Oracle:
		case DM:
		case MSSQL:
		case KingBaseR3:
		case KingBaseR6:
		case GBASE8CByOracle:
			sql = "ALTER TABLE " + table + " DROP CONSTRAINT " + pkName;
			break;
		case PostgreSQL:
		case HGDB:
				sql = "ALTER TABLE " + table.toLowerCase() + " DROP CONSTRAINT " + pkName.toLowerCase();
				break;
		case MySQL:
		case GBASE8CByMySQL:
		case GBASE8A:
			sql = "ALTER TABLE " + table + " DROP primary key";
			break;

		default:
			Log.DebugWriteError("DBAccess DropTablePK " + "@不支持的数据库类型." + SystemConfig.getAppCenterDBType());
			throw new RuntimeException("@不支持的数据库类型." + SystemConfig.getAppCenterDBType());
		}
		DBAccess.RunSQL(sql);
	}

	/**
	 * 获得table的主键
	 *
	 * param table
	 *            表名称
	 * @return 主键名称、没有返回为空.
	 */
	public static String GetTablePKName(String table) {
		String sql = "";
		switch (getAppCenterDBType()) {
		case Access:
			return null;
		case MSSQL:
			sql = "SELECT CONSTRAINT_NAME,column_name FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE table_name ='"
					+ table + "'";
			break;
		case Oracle:
		case KingBaseR3:
		case KingBaseR6:
		case DM:
			sql = "SELECT constraint_name, constraint_type,search_condition, r_constraint_name  from user_constraints WHERE table_name = upper('"
					+ table + "') AND constraint_type = 'P'";
			break;
		case MySQL:
		case GBASE8CByMySQL:
		case GBASE8A:
		case GBASE8CByOracle:
				sql = "SELECT CONSTRAINT_NAME , column_name, table_name CONSTRAINT_NAME from INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE table_name ='"
					+ table + "' and table_schema='" + SystemConfig.getAppCenterDBDatabase() + "' ";
			break;
		case Informix:
			sql = "SELECT * FROM sysconstraints c inner j = lower('"
					+ table.toLowerCase() + "') and constrtype = 'P'";
			break;
		case PostgreSQL:
		case HGDB:
			sql = " SELECT ";
			sql += " pg_constraint.conname AS pk_name ";
			sql += " FROM ";
			sql += " pg_constraint ";
			sql += " INNER JOIN pg_class ON pg_constraint.conrelid = pg_class.oid ";
			sql += " WHERE ";
			sql += " pg_class.relname ='"+table.toLowerCase()+"'";
			sql += " AND pg_constraint.contype = 'p' ";
			break;
		default:
			Log.DebugWriteError("DBAccess GetTablePKName " + "@没有判断的数据库类型.");
			throw new RuntimeException("@没有判断的数据库类型.");
		}
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0) {
			return null;
		}
		return dt.getValue(0, 0).toString();
	}

	/**
	 * 从数据库里获得文本
	 *
	 * param tableName
	 *            表名
	 * param tablePK
	 *            主键
	 * param pkVal
	 *            主键值
	 * param fileSaveField
	 *            保存字段
	 * @return
	 * @throws Exception
	 */
	public static String GetBigTextFromDB(String tableName, String tablePK, String pkVal, String fileSaveField)throws Exception {
		//对于特殊的数据库进行判断.
		if (SystemConfig.getAppCenterDBType() == DBType.Oracle
				|| SystemConfig.getAppCenterDBType() == DBType.DM
				|| SystemConfig.getAppCenterDBType() == DBType.KingBaseR3
				|| SystemConfig.getAppCenterDBType() == DBType.KingBaseR6) {
			byte[] byteFile = GetByteFromDB(tableName, tablePK, pkVal, fileSaveField);
			if (byteFile == null) {
				return "";
			}
			return new String(byteFile,"UTF-8");
		}
		//其他的数据库类型直接从 text字段去.
		if(DataType.IsNullOrEmpty(pkVal)==true)
			return "";
		try {
			String strSQL = "SELECT " + fileSaveField + " FROM " + tableName + " WHERE " + tablePK + "='" + pkVal + "'";
			return DBAccess.RunSQLReturnStringIsNull(strSQL, "");
		}catch(Exception e){
            if (DBAccess.IsExitsTableCol(tableName, fileSaveField) == false)
            {
                String sql = "ALTER TABLE " + tableName + " ADD  " + fileSaveField + " text ";
                DBAccess.RunSQL(sql);
            }
            String getSql = "SELECT " + fileSaveField + " FROM " + tableName + " WHERE " + tablePK + " = '" + pkVal + "'";
            return DBAccess.RunSQLReturnString(getSql);
		}
	}

	/**
	 * 从数据库里提取文件
	 *
	 * param tableName
	 *            表名
	 * param tablePK
	 *            表主键
	 * param pkVal
	 *            主键值
	 * param fileSaveField
	 *            字段
	 */
	public static byte[] GetByteFromDB(String tableName, String tablePK, String pkVal, String fileSaveField)  throws Exception {
        //增加对oracle数据库的逻辑 qin
        if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.Oracle
				|| SystemConfig.getAppCenterDBType() == DBType.DM
        		|| SystemConfig.getAppCenterDBType() == DBType.KingBaseR3
        		|| SystemConfig.getAppCenterDBType() == DBType.KingBaseR6
				|| SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle) {
            Connection conn = DBAccess.getGetAppCenterDBConn_Oracle();
            String strSQL = "SELECT " + fileSaveField + " FROM " + tableName + " WHERE " + tablePK + "='" + pkVal + "'";
            PreparedStatement pstmt = conn.prepareStatement(strSQL);
            ResultSet rs = null;
            // 执行它.
            try {
                rs = pstmt.executeQuery();
                byte[] byteFile = null;
                if (rs.next()) {
                    byteFile = rs.getBytes(fileSaveField);
                }
                return byteFile;
            } catch (Exception ex) {
                if (DBAccess.IsExitsTableCol(tableName, fileSaveField) == false) {
                    /*如果没有此列，就自动创建此列.*/
                    String sql = "ALTER TABLE " + tableName + " ADD  " + fileSaveField + " blob ";
                    DBAccess.RunSQL(sql);
                }
                try{
                    GetByteFromDB(tableName, tablePK,pkVal, fileSaveField);
                }catch(Exception e){
                    throw new Exception("表:"+tableName+"查询字段"+fileSaveField+"失败" + ex.getMessage());
                }

                throw new Exception("@缺少此字段,有可能系统自动修复." + ex.getMessage());
            } finally {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }
        }

        if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.MSSQL) {
			Connection conn = DBAccess.getGetAppCenterDBConn_MSSQL();
			String strSQL = "SELECT " + fileSaveField + " FROM " + tableName + " WHERE " + tablePK + "='" + pkVal + "'";
			PreparedStatement pstmt = conn.prepareStatement(strSQL);
			ResultSet rs = null;
			// 执行它.
			try {
				rs = pstmt.executeQuery();
				byte[] byteFile = null;
				if (rs.next()) {
					byteFile = rs.getBytes(fileSaveField);
				}
				return byteFile;
			} catch (Exception ex) {
				if (DBAccess.IsExitsTableCol(tableName, fileSaveField) == false) {
					/*如果没有此列，就自动创建此列.*/
					String sql = "ALTER TABLE " + tableName + " ADD  " + fileSaveField + " image ";
					DBAccess.RunSQL(sql);
				}
				throw new Exception("@缺少此字段,有可能系统自动修复." + ex.getMessage());
			} finally {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			}
		}
		if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A) {
			Connection conn = DBAccess.getGetAppCenterDBConn_MySQL();
			String strSQL = "SELECT " + fileSaveField + " FROM " + tableName + " WHERE " + tablePK + "='" + pkVal + "'";
			PreparedStatement pstmt = conn.prepareStatement(strSQL);
			ResultSet rs = null;
			// 执行它.
			try {
				rs = pstmt.executeQuery();
				byte[] byteFile = null;
				if (rs.next()) {
					byteFile = rs.getBytes(fileSaveField);
				}
				return byteFile;
			} catch (Exception ex) {
				if (DBAccess.IsExitsTableCol(tableName, fileSaveField) == false) {
					/*如果没有此列，就自动创建此列.*/
					String sql = "ALTER TABLE " + tableName + " ADD  " + fileSaveField + " longblob ";
					DBAccess.RunSQL(sql);
				}
				throw new Exception("@缺少此字段,有可能系统自动修复." + ex.getMessage());
			} finally {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			}
		}
		if (bp.difference.SystemConfig.getAppCenterDBType() == DBType.PostgreSQL ||bp.difference.SystemConfig.getAppCenterDBType() == DBType.HGDB) {
			Connection conn = DBAccess.getGetAppCenterDBConn_PSQL();
			String strSQL = "SELECT " + fileSaveField + " FROM " + tableName + " WHERE " + tablePK + "='" + pkVal + "'";
			PreparedStatement pstmt = conn.prepareStatement(strSQL);
			ResultSet rs = null;
			// 执行它.
			try {
				rs = pstmt.executeQuery();
				byte[] byteFile = null;
				if (rs.next()) {
					byteFile = rs.getBytes(fileSaveField);
				}
				return byteFile;
			} catch (Exception ex) {
				if (DBAccess.IsExitsTableCol(tableName, fileSaveField) == false) {
					/*如果没有此列，就自动创建此列.*/
					String sql = "ALTER TABLE " + tableName + " ADD  " + fileSaveField + " bytea  ";
					DBAccess.RunSQL(sql);
				}
				throw new Exception("@缺少此字段,有可能系统自动修复." + ex.getMessage());
			} finally {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			}
		}
        return null;
    }



	/**
	 * 开启事物 @xushuaho
	 */
	public static void DoTransactionBegin() throws Exception {

		Connection conn = ContextHolderUtils.getInstance().getDataSource().getConnection();
		conn.setAutoCommit(false); // 设置不能自动提交.
		SetConnOfTransactionForMySQL(WebUser.getNo(), conn);
	}

	// 提交事物 @xushuaho
	public static void DoTransactionCommit() throws Exception {

		String id = WebUser.getNo();
		Connection conn = DBAccess.GetConnOfTransactionForMySQL(id);
		conn.commit();
		conn.setAutoCommit(true); // 设置不能自动提交.
		conn.close();
		_HashtableConn.remove(id);
	}

	// 回滚事物 @xushuaho
	public static void DoTransactionRollback() throws Exception {

		String buessID = WebUser.getNo();

		Connection conn = DBAccess.GetConnOfTransactionForMySQL(buessID);
		conn.rollback();
		conn.setAutoCommit(true); // 设置不能自动提交.
		conn.close();
		_HashtableConn.remove(buessID);

	}

	public static DataTable ToLower(DataTable dt) {
		// 把列名转成小写.
		for (int i = 0; i < dt.Columns.size(); i++) {
			dt.Columns.get(i).setColumnName(dt.Columns.get(i).ColumnName.toLowerCase());
		}
		return dt;
	}

	/**
	 * 检查是否连接成功.
	 *
	 * @return
	 */
	public static boolean TestIsConnection() {
		try {
			switch (SystemConfig.getAppCenterDBType()) {
			case MSSQL:
			case HGDB:
			case PostgreSQL:
				DBAccess.RunSQLReturnString("SELECT 1+2 ");
				break;
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case DM:
			case MySQL:
			case GBASE8CByOracle:
			case GBASE8CByMySQL:
			case GBASE8A:
				DBAccess.RunSQLReturnString("SELECT 1+2 FROM DUAL ");
				break;
			case Informix:
				DBAccess.RunSQLReturnString("SELECT 1+2 FROM DUAL ");
				break;
			default:
				break;
			}
			return true;
		} catch (RuntimeException ex) {
			return false;
		}
	}

	/**
	 * 通用SQL查询分页返回DataTable
	 *
	 * param sql
	 *            SQL语句，不带排序（Order By）语句
	 * param pageSize
	 *            每页记录数量
	 * param pageIdx
	 *            请求页码
	 * param key
	 *            记录主键（不能为空，不能有重复，必须包含在返回字段中）
	 * param orderKey
	 *            排序字段（此字段必须包含在返回字段中）
	 * param orderType
	 *            排序方式，ASC/DESC
	 * @return
	 */
	public static DataTable RunSQLReturnTable(String sql, int pageSize, int pageIdx, String key, String orderKey,
			String orderType) {
		switch (DBAccess.getAppCenterDBType()) {
		case MSSQL:
			return RunSQLReturnTable_201612_SQL(sql, pageSize, pageIdx, key, orderKey, orderType);
		case Oracle:
		case KingBaseR3:
		case KingBaseR6:
		case GBASE8CByOracle:
			return RunSQLReturnTable_201612_Ora(sql, pageSize, pageIdx, orderKey, orderType);
		case MySQL:
		case GBASE8CByMySQL:
		case GBASE8A:
			return RunSQLReturnTable_201612_MySql(sql, pageSize, pageIdx, key, orderKey, orderType);
		case DM:
			return RunSQLReturnTable_201612_DM(sql, pageSize, pageIdx, orderKey, orderType);
		case PostgreSQL:
		case HGDB:
			return RunSQLReturnTable_201612_PostgreSQL(sql, pageSize, pageIdx, key, orderKey, orderType);
		default:
			throw new RuntimeException("@未涉及的数据库类型！");
		}
	}

	/**
	 * 通用SqlServer查询分页返回DataTable
	 *
	 * param sql
	 *            SQL语句，不带排序（Order By）语句
	 * param pageSize
	 *            每页记录数量
	 * param pageIdx
	 *            请求页码
	 * param key
	 *            记录主键（不能为空，不能有重复，必须包含在返回字段中）
	 * param orderKey
	 *            排序字段（此字段必须包含在返回字段中）
	 * param orderType
	 *            排序方式，ASC/DESC
	 * @return
	 */
	private static DataTable RunSQLReturnTable_201612_SQL(String sql, int pageSize, int pageIdx, String key,
			String orderKey, String orderType) {
		String sqlstr = "";

		orderType = DataType.IsNullOrEmpty(orderType) ? "ASC" : orderType.toUpperCase();

		if (pageIdx < 1) {
			pageIdx = 1;
		}

		if (pageIdx == 1) {
			sqlstr = "SELECT TOP " + pageSize + " * FROM (" + sql + ") T1" + (DataType.IsNullOrEmpty(orderKey) ? ""
					: String.format(" ORDER BY T1.%1$s %2$s", orderKey, orderType));
		} else {
			sqlstr = "SELECT TOP " + pageSize + " * FROM (" + sql + ") T1" + " WHERE T1." + key
					+ (orderType.equals("ASC") ? " > " : " < ") + "(" + " SELECT "
					+ (orderType.equals("ASC") ? "MAX(T3." : "MIN(T3.") + key + ") FROM (" + " SELECT TOP ((" + pageIdx
					+ " - 1) * 10) T2." + key + "FROM (" + sql + ") T2"
					+ (DataType.IsNullOrEmpty(orderKey) ? ""
							: String.format(" ORDER BY T2.%1$s %2$s", orderKey, orderType))
					+ " ) T3)" + (DataType.IsNullOrEmpty(orderKey) ? ""
							: String.format(" ORDER BY T.%1$s %2$s", orderKey, orderType));
		}

		return RunSQLReturnTable(sqlstr);
	}

	/**
	 * 通用Oracle查询分页返回DataTable
	 *
	 * param sql
	 *            SQL语句，不带排序（Order By）语句
	 * param pageSize
	 *            每页记录数量
	 * param pageIdx
	 *            请求页码
	 * param orderKey
	 *            排序字段（此字段必须包含在返回字段中）
	 * param orderType
	 *            排序方式，ASC/DESC
	 * @return
	 */
	private static DataTable RunSQLReturnTable_201612_Ora(String sql, int pageSize, int pageIdx, String orderKey,
			String orderType) {
		if (pageIdx < 1) {
			pageIdx = 1;
		}

		int start = (pageIdx - 1) * pageSize + 1;
		int end = pageSize * pageIdx;

		orderType = DataType.IsNullOrEmpty(orderType) ? "ASC" : orderType.toUpperCase();

		String sqlstr = "SELECT * FROM ( SELECT T1.*, ROWNUM RN " + "FROM (SELECT * FROM  (" + sql + ") T2 "
				+ (DataType.IsNullOrEmpty(orderType) ? "" : String.format("ORDER BY T2.%1$s %2$s", orderKey, orderType))
				+ ") T1 WHERE ROWNUM <= " + end + " ) WHERE RN >=" + start;

		return RunSQLReturnTable(sqlstr);
	}

	/**
	 * 通用DM查询分页返回DataTable
	 *
	 * param sql
	 *            SQL语句，不带排序（Order By）语句
	 * param pageSize
	 *            每页记录数量
	 * param pageIdx
	 *            请求页码
	 * param orderKey
	 *            排序字段（此字段必须包含在返回字段中）
	 * param orderType
	 *            排序方式，ASC/DESC
	 * @return
	 */
	private static DataTable RunSQLReturnTable_201612_DM(String sql, int pageSize, int pageIdx, String orderKey,
														  String orderType) {
		if (pageIdx < 1) {
			pageIdx = 1;
		}

		int start = (pageIdx - 1) * pageSize + 1;
		int end = pageSize * pageIdx;

		orderType = DataType.IsNullOrEmpty(orderType) ? "ASC" : orderType.toUpperCase();

		String sqlstr = "SELECT * FROM ( SELECT T1.*, ROWNUM RN " + "FROM (SELECT * FROM  (" + sql + ") T2 "
				+ (DataType.IsNullOrEmpty(orderType) ? "" : String.format("ORDER BY T2.%1$s %2$s", orderKey, orderType))
				+ ") T1 WHERE ROWNUM <= " + end + " ) WHERE RN >=" + start;

		return RunSQLReturnTable(sqlstr);
	}


	/**
	 * 通用PostgreSQL/HGDB查询分页返回DataTable
	 *
	 * param sql
	 *            SQL语句，不带排序（Order By）语句
	 * param pageSize
	 *            每页记录数量
	 * param pageIdx
	 *            请求页码
	 * param orderKey
	 *            排序字段（此字段必须包含在返回字段中）
	 * param orderType
	 *            排序方式，ASC/DESC
	 * @return
	 */
	private static DataTable RunSQLReturnTable_201612_PostgreSQL(String sql, int pageSize, int pageIdx, String key, String orderKey,
														 String orderType) {
		if (pageIdx < 1) {
			pageIdx = 1;
		}


		orderType = DataType.IsNullOrEmpty(orderType) ? "ASC" : orderType.toUpperCase();

		String sqlstr = "SELECT * FROM (" + sql + ") T1 WHERE T1." + key + (orderType.equals("ASC") ? " >= " : " <= ")
				+ "(SELECT T2." + key + " FROM (" + sql + ") T2"
				+ (DataType.IsNullOrEmpty(orderKey) ? "" : String.format(" ORDER BY T2.%1$s %2$s", orderKey, orderType))
				+ " LIMIT 1 offset " + ((pageIdx - 1) * pageSize) + ") LIMIT " + pageSize;


		return RunSQLReturnTable(sqlstr);
	}
	/**
	 * 通用MySql查询分页返回DataTable
	 *
	 * param sql
	 *            SQL语句，不带排序（Order By）语句
	 * param pageSize
	 *            每页记录数量
	 * param pageIdx
	 *            请求页码
	 * param key
	 *            记录主键（不能为空，不能有重复，必须包含在返回字段中）
	 * param orderKey
	 *            排序字段（此字段必须包含在返回字段中）
	 * param orderType
	 *            排序方式，ASC/DESC
	 * @return
	 */
	private static DataTable RunSQLReturnTable_201612_MySql(String sql, int pageSize, int pageIdx, String key,
			String orderKey, String orderType) {
		String sqlstr = "";
		orderType = DataType.IsNullOrEmpty(orderType) ? "ASC" : orderType.toUpperCase();

		if (pageIdx < 1) {
			pageIdx = 1;
		}

		sqlstr = "SELECT * FROM (" + sql + ") T1 WHERE T1." + key + (orderType.equals("ASC") ? " >= " : " <= ")
				+ "(SELECT T2." + key + " FROM (" + sql + ") T2"
				+ (DataType.IsNullOrEmpty(orderKey) ? "" : String.format(" ORDER BY T2.%1$s %2$s", orderKey, orderType))
				+ " LIMIT " + ((pageIdx - 1) * pageSize) + ",1) LIMIT " + pageSize;

		return RunSQLReturnTable(sqlstr);
	}
	/**
	 * 是否是view
	 *
	 * param tabelOrViewName
	 * @return
	 */
	public static boolean IsView(String tabelOrViewName)
	{
		return IsView(tabelOrViewName, SystemConfig.getAppCenterDBType());
	}
	/**
	 * 是否是view
	 *
	 * param tabelOrViewName
	 * @return
	 */
	public static boolean IsView(String tabelOrViewName,DBType dbType) {
		String sql = "";
		switch (dbType) {
		case Oracle:
			sql = "SELECT TABTYPE  FROM TAB WHERE UPPER(TNAME)=:v";
			DataTable oradt = DBAccess.RunSQLReturnTable(sql, "v", tabelOrViewName.toUpperCase());
			if (oradt.Rows.size() == 0) {
				throw new RuntimeException("@表不存在[" + tabelOrViewName + "]");
			}
			if (oradt.Rows.get(0).getValue(0).toString().toUpperCase().trim().equals("VIEW")) {
				return true;
			} else {
				return false;
			}
		case DM:
			sql = "SELECT VIEW_NAME  FROM user_views WHERE VIEW_NAME =:v";
			oradt = DBAccess.RunSQLReturnTable(sql, "v", tabelOrViewName.toUpperCase());
			if (oradt.Rows.size() == 0) {
				return false;
			}
			if (oradt.Rows.get(0).getValue(0).toString().toUpperCase().trim().equals(tabelOrViewName.toUpperCase())) {
				return true;
			} else {
				return false;
			}
		case Access:
			sql = "select   Type   from   msysobjects   WHERE   UCASE(name)='" + tabelOrViewName.toUpperCase() + "'";
			DataTable dtw = DBAccess.RunSQLReturnTable(sql);
			if (dtw.Rows.size() == 0) {
				throw new RuntimeException("@表不存在[" + tabelOrViewName + "]");
			}
			if (dtw.Rows.get(0).getValue(0).toString().trim().equals("5")) {
				return true;
			} else {
				return false;
			}
		case MSSQL:
			sql = "select xtype from sysobjects WHERE name =" + SystemConfig.getAppCenterDBVarStr() + "v";
			DataTable dt1 = DBAccess.RunSQLReturnTable(sql, "v", tabelOrViewName);
			if (dt1.Rows.size() == 0) {
				throw new RuntimeException("@表不存在[" + tabelOrViewName + "]");
			}

			if (dt1.Rows.get(0).getValue(0).toString().toUpperCase().trim().equals("V")) {
				return true;
			} else {
				return false;
			}
		case Informix:
			sql = "select tabtype from systables where tabname = '" + tabelOrViewName.toLowerCase() + "'";
			DataTable dtaa = DBAccess.RunSQLReturnTable(sql);
			if (dtaa.Rows.size() == 0) {
				throw new RuntimeException("@表不存在[" + tabelOrViewName + "]");
			}

			if (dtaa.Rows.get(0).getValue(0).toString().toUpperCase().trim().equals("V")) {
				return true;
			} else {
				return false;
			}
		case MySQL:
		case GBASE8CByMySQL:
		case GBASE8A:
			sql = "SELECT Table_Type FROM information_schema.TABLES WHERE UPPER(table_name)='" + tabelOrViewName.toUpperCase()
					+ "' and table_schema='" + SystemConfig.getAppCenterDBDatabase() + "'";
			DataTable dt2 = DBAccess.RunSQLReturnTable(sql);
			if (dt2.Rows.size() == 0) {
				throw new RuntimeException("@表不存在[" + tabelOrViewName + "]");
			}

			if (dt2.Rows.get(0).getValue(0).toString().toUpperCase().trim().equals("VIEW")) {
				return true;
			} else {
				return false;
			}
		case KingBaseR3:
		case KingBaseR6:
			sql = "SELECT Table_Type FROM information_schema.TABLES WHERE UPPER(table_name)='" + tabelOrViewName.toUpperCase() +"'";
			DataTable dt3 = DBAccess.RunSQLReturnTable(sql);
			if (dt3.Rows.size() == 0) {
				throw new RuntimeException("@表不存在[" + tabelOrViewName + "]KingBase");
			}

			if (dt3.Rows.get(0).getValue(0).toString().toUpperCase().trim().equals("VIEW")) {
				return true;
			} else {
				return false;
			}
		case HGDB:
		case PostgreSQL:
		case GBASE8CByOracle:

				sql="SELECT relkind FROM pg_class WHERE relname='"+tabelOrViewName.toLowerCase()+"'";
			DataTable dt4 = DBAccess.RunSQLReturnTable(sql);
			if (dt4.Rows.size() == 0) {
				throw new RuntimeException("@表不存在[" + tabelOrViewName + "]");
			}

			if (dt4.Rows.get(0).getValue(0).toString().toUpperCase().trim().equals("V")) {
				return true;
			} else {
				return false;
			}
		default:
			throw new RuntimeException("@没有做的判断。");
		}

		/*
		 * DataTable dt = DBAccess.RunSQLReturnTable(sql, "v",
		 * tabelOrViewName.toUpperCase()); if (dt.Rows.size() == 0) { throw new
		 * RuntimeException("@表不存在[" + tabelOrViewName + "]"); }
		 *
		 * if (dt.Rows.get(0).getValue(0).toString().equals("VIEW")) { return
		 * true; } else { return false; } return true;
		 */
	}

	 public static void DropTableColumn(String table, String columnName)
     {
         try
         {
             DBAccess.DropConstraintOfSQL(table, columnName);

             String sql = "ALTER TABLE " + table + " DROP COLUMN " + columnName;
             DBAccess.RunSQL(sql);
         }catch(Exception ex)
         {

         }
     }

	public static DataTable RunProcReturnTable(String sql, Paras paras)
	{
		if (DataType.IsNullOrEmpty(sql))
		{
			throw new RuntimeException("要执行的 sql = null ");
		}

		try
		{
			DataTable dt = null;
			switch (DBAccess.getAppCenterDBType())
			{
				case MSSQL:
					dt = RunProcReturnTable_SQL(sql, paras);
					break;
				case MySQL:
				case GBASE8CByMySQL:
				case GBASE8A:
					dt = RunProcReturnTable_MySQL(sql, paras);
					break;
				case Oracle:
				case DM:
				case PostgreSQL:
				case UX:
				case KingBaseR3:
				case KingBaseR6:
				case HGDB:
				case GBASE8CByOracle:
					throw new RuntimeException("err@RunProcReturnTable数据库类型还未处理！");

				default:
					throw new RuntimeException("err@RunProcReturnTable发现未知的数据库连接类型！");
			}
			return dt;
		}
		catch (RuntimeException ex)
		{
			bp.da.Log.DebugWriteError(ex.getMessage());
			throw ex;
		}
	}
	private static DataTable RunProcReturnTable_SQL(String sql, Paras paras)
	{
		ResultSet rs = null;
		Connection conn = null;
		CallableStatement callableStatement = null;
		DataTable oratb = new DataTable("otb");
		try {
			conn = DBAccess.getGetAppCenterDBConn_MSSQL();

			callableStatement = conn.prepareCall(sql);
			if (null != paras && paras.size() > 0) {

				PrepareCallProCommand(callableStatement, paras);
			}
			rs = callableStatement.executeQuery();
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
								dr.setValue(i, null);
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
					if (Log.isLoggerDebugEnabled()) {
						Log.DefaultLogWriteLineDebug("SQL: " + sql);
						Log.DefaultLogWriteLineDebug("Param: " + paras.getDebugInfo() + ", Result: Rows=" + i);
					}
				}

				oratb.Rows.add(dr);// DataTable加入此DataRow
			}


			return oratb;
		} catch (Exception ex) {

			String msg = "@运行更新在(RunSQL_200705_SQL)出错。\n  @SQL: " + sql + "\n  @Param: " + paras.getDebugInfo()
					+ "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);

			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (callableStatement != null)
					callableStatement.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	private static DataTable RunProcReturnTable_MySQL(String sql, Paras paras)
	{

		ResultSet rs = null;
		Connection conn = null;
		CallableStatement  pstmt = null;

		try {
			conn = DBAccess.getGetAppCenterDBConn_MySQL();
			DataTable oratb = new DataTable("otb");
			String nameLabel = "";
			if (null != paras && paras.size() > 0) {
				pstmt = conn.prepareCall(sql);
				PrepareCallProCommand(pstmt, paras);
			}
			rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int size = rsmd.getColumnCount();
			for (int i = 0; i < size; i++) {
				nameLabel = rsmd.getColumnLabel(i + 1);
				oratb.Columns.Add(nameLabel, Para.getDAType(rsmd.getColumnType(i + 1)));
			}
			while (rs.next()) {
				DataRow dr = oratb.NewRow();// 產生一列DataRow
				for (int i = 0; i < size; i++) {
					Object val = rs.getObject(i + 1);
					// Object val = rs.getObject(i + 1);
					if (dr != null && dr.columns.size() > 0 && dr.columns.get(i).DataType != null) {
						if (dr.columns.get(i).DataType.toString().contains("Integer")
								|| dr.columns.get(i).DataType.toString().contains("Float")) {
							if (val == null) {
								dr.setValue(i, null);
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
				Log.DefaultLogWriteLineDebug("Param: " + paras!=null?paras.getDebugInfo():"" + ", Result: Rows=" + oratb.Rows.size());
			}
			return oratb;
		} catch (Exception ex) {
			String msg = "@运行查询在(RunSQLReturnTable_200705_MySQL)出错。\n  @SQL: " + sql + "\n  @Param: "
					+ paras.getDebugInfo() + "\n  @异常信息: " + StringUtils.replace(ex.getMessage(), "\n", " ");
			Log.DefaultLogWriteLineError(msg);
			throw new RuntimeException(msg, ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	/***
	 * 创建字段
	 * @param tableName
	 * @param columnName
	 * @param attrMyDataType
	 * @param desc
	 * @param defaultVal
	 */
	public static void CreateTableColumn(String tableName, String columnName,int attrMyDataType,String desc,String defaultVal) throws Exception {
		if (DBAccess.IsExitsTableCol(tableName, columnName))
			return;

		/*不存在此列 , 就增加此列。*/
		switch (attrMyDataType)
		{
			case DataType.AppString:
				DBAccess.RunSQL("ALTER TABLE " + tableName + " ADD " + columnName + " NVARCHAR(" + 300 + ") DEFAULT '" + defaultVal + "' NULL ");
				break;
			case DataType.AppDate:
			case DataType.AppDateTime:
				DBAccess.RunSQL("ALTER TABLE " + tableName + " ADD " + columnName + " NVARCHAR(20) DEFAULT '" + defaultVal + "' NULL ");
				break;
			case DataType.AppInt:
			case DataType.AppBoolean:
				DBAccess.RunSQL("ALTER TABLE " + tableName + " ADD " + columnName + " INT DEFAULT '" + defaultVal + "' NULL ");
				break;
			case DataType.AppFloat:
				DBAccess.RunSQL("ALTER TABLE " + tableName + " ADD " + columnName + " FLOAT (11,2) DEFAULT '" + defaultVal + "' NULL ");
				break;
			case DataType.AppMoney:
				DBAccess.RunSQL("ALTER TABLE " + tableName + " ADD " + columnName + " DECIMAL (20,4) DEFAULT '" + defaultVal + "' NULL ");
				break;
			case DataType.AppDouble:
				DBAccess.RunSQL("ALTER TABLE " + tableName + " ADD " + columnName + " DOUBLE DEFAULT '" + defaultVal + "' NULL ");
				break;
			default:
				throw new Exception("error MyFieldType= " + attrMyDataType + " key=" + columnName);
		}
	}
	public static void RenameTableField(String table, String oldfield, String newfield) throws Exception {
		if (oldfield.equals(newfield) == true)
		{
			return;
		}
		if (DBAccess.IsExitsTableCol(table, oldfield) == false)
		{
			return;
		}
		if (DBAccess.IsExitsTableCol(table, newfield) == true)
		{
			return;
		}
		String sql = "";
		DataTable dt = null;
		switch (SystemConfig.getAppCenterDBType())
		{
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case PostgreSQL:
			case HGDB:
			case GBASE8CByOracle:
				sql = "alter table " + table + " rename column " + oldfield + " to " + newfield;
				break;
			case MSSQL:
				sql = "exec sp_rename '" + table + ".[" + oldfield + "]','" + newfield + "','column'";
				break;

			case MySQL:
			case DM:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql = "select COLUMN_TYPE, COLUMN_DEFAULT  FROM information_schema.columns WHERE TABLE_SCHEMA='" + SystemConfig.getAppCenterDBDatabase() + "' AND table_name='" + table + "' AND  column_Name='" + oldfield + "'";
				dt = DBAccess.RunSQLReturnTable(sql);
				sql = "ALTER TABLE " + table + " CHANGE " + oldfield + " " + newfield + " " + dt.Rows.get(0).getValue(0);
				break;
			default:
				throw new RuntimeException("err@DropTablePK不支持的数据库类型." + SystemConfig.getAppCenterDBType());
		}
		DBAccess.RunSQL(sql);
	}


}
