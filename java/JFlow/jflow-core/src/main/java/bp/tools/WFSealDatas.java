package bp.tools;

import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.en.*; import bp.en.Map;
import bp.en.Map;

/**
 * 用户日志s
 */
public class WFSealDatas extends EntitiesMyPK
{
	// 构造
	public WFSealDatas()
	{
	}
	
	/**
	 * param workID
	 * @throws Exception 
	 */
	public WFSealDatas(String workID, String node) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(WFSealDataAttr.OID, workID);
		qo.AddWhere(WFSealDataAttr.FK_Node, node);
		qo.DoQuery();
	}
	
	// 重写
	/**
	 * 得到它的 Entity
	 */
	@Override
	public Entity getNewEntity()  {
		return new bp.tools.WFSealData();
	}

	
}
