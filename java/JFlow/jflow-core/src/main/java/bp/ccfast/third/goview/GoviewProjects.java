package bp.ccfast.third.goview;

import bp.en.*;

/** 
 登录记录
*/
public class GoviewProjects extends EntitiesMyPK
{

		/**
	 * 
	 */
	private static final long serialVersionUID = -8811650441209428338L;
	///#region 构造方法..
	/** 
	 登录记录s
	*/
	public GoviewProjects()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new GoviewProject();
	}

	
}
