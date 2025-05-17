package bp.da;



/**
 数据库类型
*/
public enum DBType
{
	/**
	 sqlserver
	*/
	MSSQL(0),
	/**
	 oracle
	*/
	Oracle(1),
	/**
	 Access
	*/
	Access(2),
	/**
	 PostgreSQL
	*/
	PostgreSQL(3),
	/**
	 DB2
	*/
	DB2(4),
	/**
	 MySQL
	*/
	MySQL(5),
	/**
	 Informix
	*/
	Informix(6),
	/**
	 达梦
	*/
	DM(7),
	/**
	 * 人大金仓R3
	 */
	KingBaseR3(8),
	/**
	 * 人大金仓R6
	 */
	KingBaseR6(9),
	/**
	 * 优炫
	 */
	UX(10),
	/**
	 * 瀚高
	 */
	HGDB(11),
	/**
	 * 人大金仓R8
	 */
	KingBaseR8(12),
	/**
	 * 南大通用8c基于oracle
	 */
	GBASE8CByOracle(13),
	/**
	 * 南大通用8c基于mysql
	 */
	GBASE8CByMySQL(14),
	/**
	 * 南大通用8a基于oracle
	 */
	GBASE8A(15);


	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, DBType> mappings;
	private static java.util.HashMap<Integer, DBType> getMappings()  {
		if (mappings == null)
		{
			synchronized (DBType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, DBType>();
				}
			}
		}
		return mappings;
	}

	private DBType(int value)
	{intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static DBType forValue(int value)
	{return getMappings().get(value);
	}
}
