package bp.da;

import java.util.HashMap;
import java.util.Map;

public class DataColumn
{
	
	/**
	 * DataColumn所屬的DataTable
	 */
	private DataTable table;
	/**
	 * DataColumn的欄位名稱
	 */
	public String ColumnName; // 欄名，當做DataRow的key
	public String oldColumnName; // 欄名，當做DataRow的key
	
	public void setColumnName(String val)
	{
	  if (oldColumnName==null)
		  oldColumnName= ColumnName;
	  
	   ColumnName=val;	  
	}
	
	public Object DataType;
	
	public Object getDataType()throws Exception
	{
		return DataType;
	}
	
	public void setDataType(Object dataType)
	{
		DataType = dataType;
	}
	
	/**
	 * DataColumn被建立時，一定要指定欄名
	 * 
	 * param columnName
	 *            欄名
	 */
	public DataColumn(String columnName)
	{
		this.ColumnName = columnName;
		this.oldColumnName = columnName;
	}
	
	public DataColumn()throws Exception
	{
		
	}
	
	public DataColumn(String columnName, Object DataType)
	{
		this.ColumnName = columnName;
		this.oldColumnName = columnName;
		this.DataType = DataType;
	}
	
	//区分大小写
	public DataColumn(String columnName, Object DataType, boolean cases)
	{
		this.ColumnName = columnName;
		this.oldColumnName = columnName;
		this.DataType = DataType;
	}
	
	public DataColumn(String columnName, Object DataType, String str)
	{
		this.ColumnName = columnName;
		this.oldColumnName = columnName;
		this.DataType = DataType;
	}
	
	/**
	 * 給DataColumnCollection加入DataColumn時設定所屬的DataTable的方法，同一個package才用到
	 * 
	 * param table
	 */
	void setTable(DataTable table)
	{
		this.table = table;
	}
	
	/**
	 * 取得DataColumn所屬的DataTable，唯讀
	 * 
	 * @return DataTable
	 */
	public DataTable getTable()throws Exception
	{
		return this.table;
	}
	
	/**
	 * DataColumn物件的toString()，會回傳自己的欄名
	 * 
	 * @return
	 */
	@Override
	public String toString()  {
		return this.ColumnName;
	}

	private int ordinal;

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public final Properties ExtendedProperties = new Properties();

	public static class Properties {

		private Map<Object, Object> properties = new HashMap<Object, Object>();

		public void Add(Object key, Object value) {
			this.properties.put(key, value);
		}

		public boolean ContainsKey(Object key) {
			return this.properties.containsKey(key);
		}

		public Object get(Object key) {
			return this.properties.get(key);
		}

	}

}
