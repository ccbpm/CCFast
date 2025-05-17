package bp.da;

import java.util.ArrayList;

public class DataColumnCollection extends ArrayList<DataColumn>
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * DataColumnCollection所屬的DataTable，唯讀
	 */
	private DataTable Table;
	
	/**
	 * DataColumnCollection被建立時，一定要指定所屬的DataTable
	 * 
	 * param table
	 */
	public DataColumnCollection(DataTable table)
	{
		this.Table = table;
	}
	
	/**
	 * 取得DataColumnCollection所屬的DataTable
	 * 
	 * @return DataTable
	 */
	public DataTable getTable()throws Exception
	{
		return this.Table;
	}
	
	/**
	 * 加入一個DataColumn物件，程式碼會設定該DataColumn的DataTable和呼叫Add()
	 * 方法的DataColumnCollection同一個DataTable
	 * 
	 * param column
	 */
	public void Add(DataColumn column)
	{
		column.setTable(this.Table);
		this.add(column);
	}
	
	/**
	 * 給欄位名稱 <br/>
	 * 加入一個DataColumn物件，程式碼會設定該DataColumn的DataTable和呼叫Add()
	 * 方法的DataColumnCollection同一個DataTable
	 * 
	 * param columnName
	 * @return
	 */
	public DataColumn Add(String columnName)
	{
		DataColumn column = new DataColumn(columnName);
		column.setTable(this.Table);
		this.add(column);
		return column;
	}
	
	
	public DataColumn Add(String columnName, Object DataType)
	{
		DataColumn column = new DataColumn(columnName, DataType);
		column.setTable(this.Table);
		
		this.add(column);
		return column;
	}
	
	public DataColumn Add_UL(String columnName, Object DataType)
	{
		DataColumn column = new DataColumn(columnName, DataType, null);
		column.setTable(this.Table);
		this.add(column);
		return column;
	}
	
	/**
	 * 依據欄名，取得DataColumn
	 * 
	 * param columnName
	 *            欄名
	 * @return DataColumn
	 */
	public DataColumn get(String columnName)
	{
		DataColumn column = null;
		for (DataColumn dataColumn : this)
		{
			 
			if (dataColumn.ColumnName.toLowerCase().equals(
					columnName.toLowerCase()))
			{
				
				if (dataColumn.oldColumnName==null   )
					dataColumn.oldColumnName= dataColumn.ColumnName;
				 
				return dataColumn;
			}
		}
		return column;
	}

	/**
	 * 比较时忽略大小写
	 */
	@Override
	public boolean contains(Object o) {
		for (int i = 0; i < this.size(); i++) {
			if (o.toString().equalsIgnoreCase(this.get(i).toString())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 区分大小写
	 * @param o
	 * @return
	 */
	public boolean containsMatchCase(Object o) {
		for (int i = 0; i < this.size(); i++) {
			if (o.toString().equals(this.get(i).toString())) {
				return true;
			}
		}
		return false;
	}
}
