package bp.en;


/**
 具有编号名称的基类实体
*/
public abstract class 	EntityNoName extends EntityNo
{
	private static final long serialVersionUID = 1L;
	/**
	 名称
	*/
	public final String getName()  {
		return this.GetValStringByKey(EntityNoNameAttr.Name);
	}
	public final void setName(String value){
		this.SetValByKey(EntityNoNameAttr.Name, value);
	}

	/**

	*/
	public EntityNoName()
	{
	}
	/**
	 param _No
	 * @throws Exception
	*/
	protected EntityNoName(String _No) throws Exception {
		super(_No);
	}

		///


		///业务逻辑处理
	/**
	 检查名称的问题.

	 @return
	 * @throws Exception
	*/
	@Override
	protected boolean beforeInsert() throws Exception {
		if (this.getNo().trim().length() == 0)
		{
			if (this.getEnMap().getItIsAutoGenerNo())
			{
				this.setNo(this.GenerNewNo());
			}
			else
			{
				throw new RuntimeException("@没有给[" + this.getEnDesc() + " " + this.toString() + " , " + this.getName() + "]设置主键,能执行插入.");
			}
		}

		if (this.getEnMap().getIsAllowRepeatName() == false)
		{
			if (this.getPKCount() == 1)
			{
				if (this.ExitsValueNum("Name", this.getName()) >= 1)
				{
					throw new RuntimeException("@插入失败[" + this.getEnMap().getEnDesc() + "] 编号[" + this.getNo() + "]名称[" + getName() + "]重复.");
				}
			}
		}
		return super.beforeInsert();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		if (this.getEnMap().getIsAllowRepeatName() == false)
		{
			if (this.getPKCount() == 1)
			{
				if (this.ExitsValueNum("Name", this.getName()) >= 2)
				{
					throw new RuntimeException("@更新失败[" + this.getEnMap().getEnDesc() + "] 编号[" + this.getNo() + "]名称[" + getName() + "]重复.");
				}
			}
		}
		return super.beforeUpdate();
	}
	@Override
	public  int Save() throws Exception
	{
		/*如果包含编号。 */
		if (this.getIsExits())
		{
			return this.Update();
		}
		else
		{
			if (this.getEnMap().getItIsAutoGenerNo()
					&& this.getEnMap().GetAttrByKey("No").getUIIsReadonly())
				this.setNo(this.GenerNewNo());

			this.Insert();
			return 0;
		}
	}


}
