package bp.en;

import java.util.*;

/** 
 row 集合
*/
public class Rows extends ArrayList<Object>
{
	private static final long serialVersionUID = 1L;
	public Rows()throws Exception
	{
	}
	public final Row get(int index)
	{
		return (Row)this.get(index);
	}
	/** 
	 增加一个Row .
	 
	 param r row
	*/
	public final void Add(Row r)
	{
		this.add(r);
	}
}