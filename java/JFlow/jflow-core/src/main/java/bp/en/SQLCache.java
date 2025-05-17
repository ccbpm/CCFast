package bp.en;

import java.io.Serializable;
import java.util.*;

/**
 缓存SQL
*/
public class SQLCache  implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String EnName = null;
	public String Insert = null;
	public String Update = null;
	public String Delete = null;
	public String Select = null;
	public SQLCache() throws Exception
	{

	}
	public SQLCache(Entity en) throws Exception
	{
		this.EnName=en.toString();
		this.Insert = SqlBuilder.InsertForPara(en);
		this.Update = SqlBuilder.UpdateForPara(en, null);
		this.Delete = SqlBuilder.DeleteForPara(en);
		this.Select = SqlBuilder.RetrieveForPara(en);
		this._UpdateSQLs=new Hashtable();
	}
	/**
	 获取指定的key, 返回更新的语句。

	 param keys
	 @return
	 * @throws Exception
	*/
	public final String GetUpdateSQL(Entity en, String[] keys) throws Exception
	{
		if (keys == null)
		{
			return this.Update;
		}

		String mykey = "";
		for (String k : keys)
		{
			mykey += k;
		}

		Object tempVar = this.getUpdateSQLs().get(mykey);
		String sql = tempVar instanceof String ? (String)tempVar : null;
		if (sql == null)
		{
			getUpdateSQLs().put(mykey, SqlBuilder.UpdateForPara(en, keys));
		}

		Object tempVar2 = getUpdateSQLs().get(mykey);
		sql = tempVar2 instanceof String ? (String)tempVar2 : null;

		if (sql == null)
		{
			throw new RuntimeException("@error");
		}

		return sql;
	}


		///UpdateSQLs
	private Hashtable _UpdateSQLs;
	public final Hashtable getUpdateSQLs()throws Exception
	{
		if (_UpdateSQLs == null)
		{
			_UpdateSQLs = new Hashtable();
		}
		return _UpdateSQLs;
	}
	public final void setUpdateSQLs(Hashtable updateSQLs){
		this._UpdateSQLs = updateSQLs;
	}

		///
}
