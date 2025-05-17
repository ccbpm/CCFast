package bp.da;



/** 
　连接到哪个库上．
  他们存放在 web.config 的列表内．
*/
public enum DBUrlType
{
	/** 
	 主应用程序
	*/
	AppCenterDSN,
	/** 
	 1号连接
	*/
	DBAccessOfOracle1,
	/** 
	 2号连接
	*/
	DBAccessOfOracle2,
	/** 
	 1号连接
	*/
	DBAccessOfMSSQL1,
	/** 
	 2号连接
	*/
	DBAccessOfMSSQL2,
	/** 
	 access的连接．
	*/
	DBAccessOfOLE,
	/** 
	 ODBC连接
	*/
	DBAccessOfODBC,
	/** 
	 数据源连接
	*/
	DBSrc;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static DBUrlType forValue(int value) 
	{
		return values()[value];
	}
}