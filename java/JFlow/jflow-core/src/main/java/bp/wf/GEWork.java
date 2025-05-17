package bp.wf;

import bp.da.*;
import bp.en.*;
import bp.en.Map;

/** 
 普通工作
*/
public class GEWork extends Work
{

		///#region 与_SQLCache 操作有关
	private SQLCache _SQLCache = null;
	@Override
	public SQLCache getSQLCache() throws Exception {
		if (_SQLCache == null)
		{
			_SQLCache = Cache.GetSQL(this.NodeFrmID.toString());
			if (_SQLCache == null)
			{
				_SQLCache = new SQLCache(this);
				Cache.SetSQL(this.NodeFrmID.toString(), _SQLCache);
			}
		}
		return _SQLCache;
	}
	@Override
	public void setSQLCache(SQLCache value)
	{
		_SQLCache = value;
	}

		///#endregion


		///#region 构造函数
	/** 
	 普通工作
	*/
	public GEWork()
	{
	}
	/** 
	 普通工作
	 
	 @param nodeid 节点ID
	*/
	public GEWork(int nodeid, String nodeFrmID)
	{
		this.NodeFrmID = nodeFrmID;
		this.setNodeID(nodeid);
		this.setSQLCache(null);
	}
	/** 
	 普通工作
	 
	 @param nodeid 节点ID
	 @param _oid OID
	*/
	public GEWork(int nodeid, String nodeFrmID, long _oid) throws Exception {
		this.NodeFrmID = nodeFrmID;
		this.setNodeID(nodeid);
		this.setOID(_oid);
		this.setSQLCache(null);
	}

		///#endregion


		///#region Map
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap(){
		try{
			this.set_enMap(bp.sys.MapData.GenerHisMap(this.NodeFrmID));
		}catch(Exception e){

		}
		return this.get_enMap();
	}
	/** 
	 GEWorks
	*/
	@Override
	public Entities GetNewEntities()
	{
		if (this.getNodeID() == 0)
		{
			return new GEWorks();
		}
		return new GEWorks(this.getNodeID(), this.NodeFrmID);
	}

		///#endregion

	/** 
	 重写toString 返回fromID.
	 
	 @return 
	*/
	@Override
	public String toString()
	{
		return this.NodeFrmID;
	}
}
