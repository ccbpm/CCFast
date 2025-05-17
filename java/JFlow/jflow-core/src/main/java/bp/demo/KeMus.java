package bp.demo;

import bp.en.EntitiesNoName;
import bp.en.Entity;

/**
 * 科目
 */
public class KeMus extends EntitiesNoName
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 143653647L;
	
	/**
	 * 得到它的 Entity
	 */
	@Override
	public Entity getNewEntity()
	{
		return new KeMu();
	}
	
	/**
	 * 构造方法
	 */
	public KeMus()
	{
	}
}