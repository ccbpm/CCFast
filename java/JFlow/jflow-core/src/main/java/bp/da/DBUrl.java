package bp.da;


import bp.difference.SystemConfig;

import java.io.Serializable;

/**
 DBUrl 的摘要说明。
*/
public class DBUrl implements Serializable
{
	private static final long serialVersionUID = 1L;
	/**
	 连接
	*/
	public DBUrl()
	{
	}
	/**
	 连接

	 param type 连接type
	*/
	public DBUrl(DBUrlType type)
	{
		this.setDBUrlType(type);
	}
	/**
	 创建数据库连接

	 param dbSrc
	 * @throws Exception
	*/
	public DBUrl(String dbSrc) throws Exception {
		//数据库类型.
		this.setDBUrlType(DBUrlType.DBSrc);

		//数据库连接.
		this.setHisDBSrc(new bp.sys.SFDBSrc(dbSrc));
	}


		///其他数据源.
	private bp.sys.SFDBSrc _HisDBSrc = null;
	public final bp.sys.SFDBSrc getHisDBSrc()
	{
		return _HisDBSrc;
	}
	public final void setHisDBSrc(bp.sys.SFDBSrc value)
	{_HisDBSrc = value;
	}

		/// 其他数据源.

	/**
	 默认值
	*/
	public DBUrlType _DBUrlType = DBUrlType.AppCenterDSN;
	/**
	 要连接的到的库。
	*/
	public final DBUrlType getDBUrlType()
	{
		return _DBUrlType;
	}
	public final void setDBUrlType(DBUrlType value)
	{_DBUrlType = value;
	}
	public final String getDBVarStr() throws Exception {
		switch (this.getDBType()) {
			case MSSQL:
			case Oracle:
			case KingBaseR3:
			case KingBaseR6:
			case DM:
			case PostgreSQL:
			case HGDB:
			case MySQL:
			case GBASE8CByOracle:
			case GBASE8CByMySQL:
			case GBASE8A:
				return ":";
			case Informix:
				return "?";
			default:
				return "@";
		}

	}
	/**
	 数据库类型
	 * @throws Exception
	*/
	public final DBType getDBType()  {
		switch (this.getDBUrlType())
		{
			case AppCenterDSN:
				return DBAccess.getAppCenterDBType();
			case DBAccessOfMSSQL1:
			case DBAccessOfMSSQL2:
				return DBType.MSSQL;
			case DBAccessOfOLE:
				return DBType.Access;
			case DBAccessOfOracle1:
			case DBAccessOfOracle2:
				return DBType.Oracle;
			case DBSrc:
				return this.getHisDBSrc().getHisDBType();
			default:
				throw new RuntimeException("不明确的连接");
		}
	}
}
