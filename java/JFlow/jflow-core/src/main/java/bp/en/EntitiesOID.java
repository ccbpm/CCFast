package bp.en;


public abstract class EntitiesOID extends Entities
{
	/** 
	 构造
	*/
	public EntitiesOID()
	{

	}


		///查询方法, 专用于与语言有关的实体
	/** 
	 查询出来, 所有中文的实例 . 
	 * @throws Exception 
	*/
	public final void RetrieveAllCNEntities() throws Exception {
		this.RetrieveByLanguageNo("CH");
	}
	/** 
	 按语言查询。 
	 * @throws Exception 
	*/
	public final void RetrieveByLanguageNo(String LanguageNo) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere("LanguageNo", LanguageNo);
		qo.DoQuery();
	}

    public abstract Entity getNewEntity();

    ///
}