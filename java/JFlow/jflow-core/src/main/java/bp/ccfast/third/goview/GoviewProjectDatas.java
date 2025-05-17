package bp.ccfast.third.goview;

import bp.en.EntitiesOID;
import bp.en.Entity;

/**
 * 简历组
 */
public class GoviewProjectDatas extends EntitiesOID
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1242424L;
	
	/**
	 * 得到它的 Entity
	 */
	@Override
	public Entity getNewEntity()
	{
		return new GoviewProjectData();
	}
	
	/**
	 * 构造函数
	 */
	public GoviewProjectDatas()
	{
	}
}