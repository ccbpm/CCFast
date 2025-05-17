package bp.en;
import java.util.Hashtable;
import bp.da.DataColumn;
import bp.da.DataRow;
import bp.da.DataTable;
import bp.da.DataType;

/**
 * Row 的摘要说明。 用来处理一条记录
 */
public class Row extends Hashtable<String, Object>
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 初始化数据.
	 *
	 * param attrs
	 */
	public final void LoadAttrs(Attrs attrs)  {
		this.clear();
		for (Attr attr : attrs)
		{
			switch (attr.getMyDataType())
			{
				case bp.da.DataType.AppInt:
					if(attr.getDefaultVal() ==null)
						this.put(attr.getKey(),"");
					else
						this.put(attr.getKey(), Integer.parseInt(attr.getDefaultVal().toString()));
					break;
				case bp.da.DataType.AppFloat:
				case bp.da.DataType.AppMoney:
					if(attr.getDefaultVal() ==null)
						this.put(attr.getKey(),"");
					else
						this.put(attr.getKey(), Float.parseFloat(attr.getDefaultVal().toString()));
				case bp.da.DataType.AppDouble:
					if(attr.getDefaultVal() ==null)
						this.put(attr.getKey(),"");
					else
						this.put(attr.getKey(), Double.parseDouble(attr.getDefaultVal().toString()));
					break;
				default:
					this.put(attr.getKey(), attr.getDefaultVal());
					break;
			}
		}

	}

	/**
	 * LoadAttrs
	 *
	 * param dt
	 */
	public final void LoadDataTable(DataTable dt, DataRow dr)
	{
		this.clear();
		for (DataColumn dc : dt.Columns)
		{
			if (dr.containsKey(dc.ColumnName))
				this.put(dc.ColumnName, dr.getValue(dc.ColumnName));

		}
	}

	/**
	 * 设置一个值by key .
	 *
	 * param key
	 * param val
	 */
	public final void SetValByKey(String key, Object val)
	{
		if (key==null)
			return;

		if (val == null || val.equals("")  )
		{
			this.put(key, "");
			return;
		}

		if (val.getClass() == Enum.class)
		{
			this.put(key, ((Integer) val).intValue());
		} else
		{
			this.put(key, val);
		}
	}



	public final boolean GetBoolenByKey(String key)
	{
		Object obj = this.get(key);
		if (obj == null || DataType.IsNullOrEmpty(obj.toString())
				|| obj.toString().equals("0"))
		{
			return false;
		}
		return true;
	}

	public final String GetValStrByKey(String key)
	{
		return this.get(key)  == null ? null:this.get(key).toString();
		//this.get(key) instanceof String ? (String)this.get(key) : null;
	}


	public final Object GetValByKey(String key)
	{
		return this.get(key);

	}
}
