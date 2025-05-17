package bp.en;

import bp.da.DataType;

/**
 NoEntity 的摘要说明。
*/
public abstract class EntityNo extends Entity
{

		///提供的属性
	@Override
	public String getPK()
	{
		return "No";
	}
	/**
	 编号
	*/
	public String getNo()
	{
		return this.GetValStringByKey(EntityNoNameAttr.No);
	}
	public void setNo(String value)
	{
		this.SetValByKey(EntityNoNameAttr.No, value);
	}

		///


		///与编号有关的逻辑操作(这个属性只与dict EntityNo, 基类有关系。)
	/**
	 Insert 之前的操作。

	 @return
	 * @throws Exception
	*/
	@Override
	protected boolean beforeInsert() throws Exception {
		if(this.getNo().trim().isEmpty()) {
			this.setNo(this.GenerNewNo());
		}
		return super.beforeInsert();
	}

	/**
	 事例化一个实体
	*/
	public EntityNo()
	{
	}
	/**
	 通过编号得到实体。

	 param _no 编号
	 * @throws Exception
	*/
	public EntityNo(String _no) throws Exception {
		if (_no == null || _no.equals(""))
		{
			throw new RuntimeException(this.getEnDesc() + "@对表[" + this.getEnDesc() + "]进行查询前必须指定编号。");
		}

		this.setNo(_no);
		try {
			if (this.Retrieve() == 0)
			{
				throw new RuntimeException("@没有" + this.getEnMap().getPhysicsTable() + ", No = " + getNo() + "的记录。");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public int Save() throws Exception
	{
		/*如果包含编号。 */
		if (this.getIsExits())
		{
			return this.Update();
		}
		else
		{
			if (this.getEnMap().getItIsAutoGenerNo() && this.getEnMap().GetAttrByKey("No").getUIIsReadonly())
			{
				this.setNo(this.GenerNewNo());
			}

			this.Insert();
			return 0;
		}

	}



		///提供的查寻方法
	/**
	 生成一个编号
	 * @throws Exception
	*/
	public final String GenerNewNo() throws Exception
	{
		return this.GenerNewNoByKey("No");
	}
	/**
	 生成编号

	 @return
	 * @throws Exception
	*/
	public final String GenerNewEntityNo() throws Exception
	{
		return this.GenerNewNoByKey("No");
	}
	/**
	 按 No 查询。

	 @return
	 * @throws Exception
	*/
	public final int RetrieveByNo() throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(EntityNoAttr.No, this.getNo());
		return qo.DoQuery();
	}
	/**
	 按 No 查询。

	 param _No No
	 @return
	 * @throws Exception
	*/
	public final int RetrieveByNo(String _No) throws Exception
	{
		this.setNo(_No);
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(EntityNoAttr.No, this.getNo());
		return qo.DoQuery();
	}

}
