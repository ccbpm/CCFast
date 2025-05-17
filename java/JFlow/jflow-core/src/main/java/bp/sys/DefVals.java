package bp.sys;
import bp.en.*;

/** 
 默认值s
*/
public class DefVals extends EntitiesMyPK
{
	/** 
	 默认值
	*/
	public DefVals()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new DefVal();
	}
}
