package bp.sys.xml;

/** 
 ActiveAttr 的摘要说明。
 考核过错行为的数据元素
 1，它是 ActiveAttr 的一个明细。
 2，它表示一个数据元素。
*/
public class ActiveAttr extends XmlEn
{

		///#region 属性
	/** 
	 选择这个属性时间需要的条件
	*/
	public final String getCondition()  {
		return this.GetValStringByKey(ActiveAttrAttr.Condition);
	}
	public final String getAttrKey()  {
		return this.GetValStringByKey(ActiveAttrAttr.AttrKey);
	}
	public final String getAttrName()  {
		return this.GetValStringByKey(ActiveAttrAttr.AttrName);
	}
	public final String getExp()  {
		return this.GetValStringByKey(ActiveAttrAttr.Exp);
	}
	public final String getExpApp()  {
		return this.GetValStringByKey(ActiveAttrAttr.ExpApp);
	}
	public final String getFor()  {
		return this.GetValStringByKey(ActiveAttrAttr.For);
	}

		///#endregion


		///#region 构造
	public ActiveAttr()
	{
	}
	/** 
	 获取一个实例
	*/
	@Override
	public XmlEns getNewEntities()
	{
		return new ActiveAttrs();
	}

		///#endregion
}
