package bp.en;


/** 
 编号实体集合。
*/
public abstract class EntitiesNo extends Entities
{

	/**
	 * 构造函数
	 */
	public EntitiesNo()  {

	}

	@Override
	public int RetrieveAllFromDBSource() throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.addOrderBy("No");
		return qo.DoQuery();
	}
}