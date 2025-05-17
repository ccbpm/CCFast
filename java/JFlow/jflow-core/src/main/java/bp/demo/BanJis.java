package bp.demo;

import bp.en.EntitiesNoName;
import bp.en.Entity;

/**
 * 班级组
 */
public class BanJis extends EntitiesNoName
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 124553636L;
	
	/**
	 * 得到它的 Entity
	 */
	@Override
	public Entity getNewEntity()
	{
		return new BanJi();
	}
	
	/**
	 * 构造方法
	 */
	public BanJis()
	{
	}
}