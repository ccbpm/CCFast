package bp.da;

import java.util.ArrayList;
import java.util.Map;


import bp.difference.SystemConfig;

public class DataRowCollection extends ArrayList<DataRow>
{
	
	/**  
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * DataRowCollection所屬的DataTable，唯讀
	 */
	private transient DataTable Table;
	
	/**
	 * DataRowCollection被建立時，一定要指定所屬的DataTable
	 * 
	 * param table
	 */
	public DataRowCollection(DataTable table)
	{
		this.Table = table;
		
	}
	
	/**
	 * 取得所屬的DataTable
	 * 
	 * @return DataTable
	 */
	public DataTable getTable()throws Exception
	{
		return this.Table;
	}
	
	public DataRow AddRow(DataRow vals)
	{
		return AddRow(vals,-1);
	}
	 
	
	public DataRow AddRow(DataRow vals, int idx){
		
		if (vals==null)
			return null;
		 
		DataRow row = new DataRow(Table);
		
		Map ap=   ((Map<String, Object>) vals);
		
		for(DataColumn dc : Table.Columns)
		{

            String key= dc.ColumnName;
            String valStr=String.valueOf( ap.get(key)); 
            if((DataType.IsNullOrEmpty(valStr) ||"null".equals(valStr))
            		&& SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase){
            	valStr=String.valueOf( ap.get(key.toLowerCase())); 
            }
			
			if (valStr==null)
				valStr="";
			row.setValue(key, valStr);
		}
       
		if (idx==-1)
	    	this.add(row);
		else
			this.add(row);
			
		return row;
	}
	
	public DataRow AddDatas(Object... vals){
		
		if (vals==null)
			return null;
		 
		
		DataRow row = new DataRow(Table);	
		int i=0;
			for (Object val : vals) {
				 
				
				String key = Table.Columns.get(i++).ColumnName;
				
				Object value;
				if (val instanceof Map) {
					value = ((Map<String, Object>) val).get(key);
				} else {
					value = val;
				}
				
				if (value==null)
					value="";
				
				row.put(key, value); 
			}
			this.add(row);
		
		return row;
	}
}
