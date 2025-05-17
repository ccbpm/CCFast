package bp.demo;

import bp.en.EntitiesOID;
import bp.en.Entity;

public class QingJias extends EntitiesOID
{
	
	// 得到它的 Entity
	private static final long serialVersionUID = 1L;
	
	@Override
	public Entity getNewEntity()
	{
		return new QingJia();
	}
	
	// 请假s
	public QingJias()
	{
	}

}
