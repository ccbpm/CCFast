package bp.da;

import java.util.LinkedHashMap;
import bp.tools.StringUtils;

public class DataRow extends LinkedHashMap<String, Object>
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 此資料列所屬的DataTable，唯讀
	 */
	public transient DataTable table;

	/**
	 * 在getValue()和setValue()時候，程式碼須透過此成員的欄位名稱來找出Map字典裡的物件
	 */
	public transient DataColumnCollection columns;

	public transient Object ItemArray;


	public Object getItemArray() {
		return ItemArray;
	}

	public void setItemArray(Object itemArray) {
		ItemArray = itemArray;
	}

	/**
	 * DataRow被建立時，必須指定所屬的DataTable
	 *
	 * param table
	 */
	public DataRow(DataTable table)
	{
		this.table = table;
		this.columns = table.Columns;
	}

	/**
	 * 取得DataRow所屬的DataTable
	 *
	 * @return DataTable
	 */
	public DataTable getTable()throws Exception
	{
		return this.table;
	}

	/**
	 * 設定該列該行的值
	 *
	 * param columnIndex
	 *            行索引(從0算起)
	 * param value
	 *            要設定的值
	 */
	public void setValue(int columnIndex, Object value)
	{setValue(this.columns.get(columnIndex), value);
	}

	public void setDataType(int columnIndex, Object dataType)
	{
		this.columns.get(columnIndex).setDataType(dataType);
	}

	/**
	 * 設定該列該行的值
	 *
	 * param columnName
	 *            行名稱
	 * param value
	 *            要設定的值
	 */
	public void setValue(String columnName, Object value)
	{
		this.put(columnName, value);
		// this.put(columnName, value);
	}

	public void setValue(String columnName, Boolean value)
	{
		if (value==true)
			this.put(columnName, 1);
		else
			this.put(columnName, 0);
	}

	public void setValueStr(String columnName, String value)
	{this.put(columnName, "\""+value+"\"");
		// this.put(columnName, value);
	}
	/**
	 * 設定該列該行的值 区分大小写
	 *
	 * param columnName
	 *            行名稱
	 * param value
	 *            要設定的值
	 */
	public void setValue2017(String columnName, Object value)
	{this.put(columnName, value);
	}

	/**
	 * 設定該列該行的值
	 *
	 * param column
	 *            DataColumn物件
	 * param value
	 *            要設定的值
	 */
	public void setValue(DataColumn column, Object value)
	{if (column != null)
		{
			String lowerColumnName = column.ColumnName;
			if (this.containsKey(lowerColumnName))
				this.remove(lowerColumnName);
			this.put(lowerColumnName, value);
		}
	}

	/**
	 * 区分大小写
	 *
	 * param column
	 * param value
	 */
	public void setValue_UL(DataColumn column, Object value)throws Exception
	{if (column != null)
		{
			String lowerColumnName = column.ColumnName;
			// String lowerColumnName = column.ColumnName;
			if (this.containsKey(lowerColumnName))
				this.remove(lowerColumnName);
			this.put(lowerColumnName, value);
		}
	}

	/**
	 * 取得該列該行的值
	 *
	 * param columnIndex
	 *            行索引(從0算起)
	 * @return Object
	 */
	public Object getValue(int columnIndex)
	{
		Object obj = this.get(this.columns.get(columnIndex).ColumnName);
		if (obj == null)
		{
			obj = "";
		}
		return obj;
		// return
		// this.get(this.columns.get(columnIndex).ColumnName.toLowerCase());
	}

	/**
	 * 取得該列該行的值
	 *
	 * param columnName
	 *            行名稱
	 * @return Object
	 */
	public Object getValue(String columnName)
	{

		Object obj = this.get(columnName);
		if (obj == null)
		{
			if(!DataType.IsNullOrEmpty(this.get(columnName.toLowerCase())))
				return this.get(columnName.toLowerCase());
			if(!DataType.IsNullOrEmpty(this.get(columnName.toUpperCase())))
				return this.get(columnName.toUpperCase());
			return "";
		}
		return obj;
	}

	/**
	 * 取得該列該行的值
	 *
	 * param column
	 *            DataColumn物件
	 * @return Object
	 */
	public Object getValue(DataColumn column)
	{
		Object obj = this.get(column.ColumnName);
		if (obj == null)
		{
			obj = "";
		}
		return obj;
	}

	public void set(String string, Object object) {

		if (object==null)
			object="";

		this.set(string, object.toString());
	}
}
