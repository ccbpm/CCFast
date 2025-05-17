package bp.en;

import bp.da.*;

/** 
 树实体
*/
public abstract class EntityTree extends Entity
{

		///属性
	public final boolean getItIsRoot() throws Exception {
		if (this.getParentNo().equals("-1") || this.getParentNo().equals("0"))
		{
			return true;
		}

		if (this.getNo().equals(this.getParentNo()))
		{
			return true;
		}

		return false;
	}
	/** 
	 唯一标示
	*/
	public final String getNo()
	{
		return this.GetValStringByKey(EntityTreeAttr.No);
	}
	public final void setNo(String value){
		this.SetValByKey(EntityTreeAttr.No, value);
	}
	/** 
	 名称
	 * @throws Exception 
	*/
	public final String getName()  {
		return this.GetValStringByKey(EntityTreeAttr.Name);
	}
	public final void setName(String value){
		this.SetValByKey(EntityTreeAttr.Name, value);
	}
	/** 
	 父节点编号
	*/
	public String getParentNo()  {
		return this.GetValStringByKey(EntityTreeAttr.ParentNo);
	}
	public void setParentNo(String value){
		this.SetValByKey(EntityTreeAttr.ParentNo, value);
	}
	/** 
	 图标
	*/
	public final String getICON()  {
		return this.GetValStringByKey(EntityTreeAttr.ICON);
	}
	public final void setICON(String value){
		this.SetValByKey(EntityTreeAttr.ICON, value);
	}
	/** 
	 顺序号
	*/
	public int getIdx()  {
		return this.GetValIntByKey(EntityTreeAttr.Idx);
	}
	public void setIdx(int value){
		this.SetValByKey(EntityTreeAttr.Idx, value);
	}

		///构造函数
	/** 
	 主键
	*/
	@Override
	public String getPK()
	{
		return EntityTreeAttr.No;
	}
	/** 
	 树结构编号
	*/
	public EntityTree()
	{
	}
	/** 
	 树结构编号
	 
	 param no 编号
	 * @throws Exception 
	*/
	public EntityTree(String no) throws Exception 
	{
		if (DataType.IsNullOrEmpty(no))
		{
			throw new RuntimeException(this.getEnDesc() + "@对表[" + this.getEnDesc() + "]进行查询前必须指定编号。");
		}

		this.setNo(no);
		if (this.Retrieve() == 0)
		{
			throw new RuntimeException("@没有" + this.getEnMap().getPhysicsTable() + ", No = " + this.getNo() + "的记录。");
		}
	}

		///


		///业务逻辑处理
	/** 
	 重新设置treeNo
	*/
	public final void ResetTreeNo()
	{
	}
	/** 
	 检查名称的问题.
	 
	 @return 
	 * @throws Exception 
	*/
	@Override
	protected boolean beforeInsert() throws Exception {
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

		if (DataType.IsNullOrEmpty(this.getNo()))
		{
			this.setNo(this.GenerNewNoByKey("No"));
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

		///


		///可让子类调用的方法
	/** 
	 新建同级节点
	 
	 @return 
	 * @throws Exception 
	*/
	public  EntityTree DoCreateSameLevelNode() throws Exception
	{
		bp.en.Entity tempVar = this.CreateInstance();
		EntityTree en = tempVar instanceof EntityTree ? (EntityTree)tempVar : null;
		if (this.getClassID().contains("FlowSort") || this.getClassID().contains("SysFormTree"))
			en.setNo(DBAccess.GenerGUID());
		else
			en.setNo(String.valueOf(DBAccess.GenerOID(this.toString()))); // en.GenerNewNoByKey(EntityTreeAttr.No);
		en.setName("新建节点" + en.getNo());
		en.setParentNo(this.getParentNo());
	   // en.MenuType = this.MenuType;
	  //  en.IsDir = false;
	   // en.TreeNo = this.GenerNewNoByKey(EntityTreeAttr.TreeNo, EntityTreeAttr.ParentNo, this.ParentNo);
		en.Insert();
		return en;
	}
	/** 
	 新建同级节点
	 
	 @return 
	 * @throws Exception 
	*/
	public  EntityTree DoCreateSameLevelNode(String name) throws Exception
	{
		bp.en.Entity tempVar = this.CreateInstance();
		EntityTree en = tempVar instanceof EntityTree ? (EntityTree)tempVar : null;
		if (this.getClassID().contains("FlowSort") || this.getClassID().contains("SysFormTree"))
			en.setNo(DBAccess.GenerGUID());
		else
			en.setNo(String.valueOf(DBAccess.GenerOID(this.toString()))); // en.GenerNewNoByKey(EntityTreeAttr.No);
		
		 if (name == null)
			 en.setName("新建节点" + en.getNo());
         else
             en.setName(name);
		en.setParentNo(this.getParentNo());
		en.Insert();
		return en;
	}

	public  EntityTree DoCreateSubNode() throws Exception
	{
		return DoCreateSubNode(null);
	}
	/** 
	 新建子节点
	 @return 
	 * @throws Exception 
	*/
	public  EntityTree DoCreateSubNode(String name) throws Exception
	{
		bp.en.Entity tempVar = this.CreateInstance();
		EntityTree en = tempVar instanceof EntityTree ? (EntityTree)tempVar : null;
		if (this.getClassID().contains("FlowSort") || this.getClassID().contains("SysFormTree"))
			en.setNo(DBAccess.GenerGUID(10, null, null));
		else
			en.setNo(String.valueOf(DBAccess.GenerOID(this.toString())));
		 // en.GenerNewNoByKey(EntityTreeAttr.No);

		if (name == null)
			en.setName("新建节点" + en.getNo());
        else
        	en.setName(name);
		en.setParentNo(this.getNo());
	 
		en.Insert();

	
		return en;
	}
	/** 
	 上移
	 
	 @return 
	*/
	public  String DoUp()throws Exception
	{
		this.DoOrderUp(EntityTreeAttr.ParentNo, this.getParentNo(), EntityTreeAttr.Idx);
		return "执行成功.";
	}
	/** 
	 下移
	 
	 @return 
	*/
	public  String DoDown() throws Exception
	{
		this.DoOrderDown(EntityTreeAttr.ParentNo, this.getParentNo(), EntityTreeAttr.Idx);
		return "执行成功.";
	}

		///
}