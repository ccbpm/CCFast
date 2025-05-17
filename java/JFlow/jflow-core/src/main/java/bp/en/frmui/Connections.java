package bp.en.frmui;

import bp.*;
import bp.en.*; import bp.en.Map;

import java.util.ArrayList;

/** 
 级联s
*/
public class Connections extends ArrayList<Connection>
{
	/** 
	 构造
	*/
	public Connections()
	{
	}
	/** 
	 获得数据
	 
	 @param index
	 @return 
	*/
	public final Connection get(int index)
	{
		Object tempVar = this.get(index);
		return tempVar instanceof Connection ? (Connection)tempVar : null;
	}
	/** 
	 增加级联关系.
	 
	 @param keyOfEn
	 @param refKeyOfEn
	 @param sql
	*/
	public final void Add(String keyOfEn, String refKeyOfEn, String sql)
	{
		Connection en = new Connection();
		en.KeyOfEn = keyOfEn;
		en.RelKeyOfEn = refKeyOfEn;
		en.SQL=sql;

	}
}
