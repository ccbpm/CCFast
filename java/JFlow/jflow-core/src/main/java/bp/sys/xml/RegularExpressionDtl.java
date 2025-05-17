package bp.sys.xml;


/** 
  RegularExpressionDtl 正则表达模版
*/
public class RegularExpressionDtl extends XmlEn
{

		///#region 属性
	/** 
	 编号
	*/
	public final String getItemNo()  {
		return this.GetValStringByKey("ItemNo");
	}
	/** 
	 名称
	*/
	public final String getName()  {
		return this.GetValStringByKey("Name");
	}
	public final String getNote()  {
		return this.GetValStringByKey("Note");
	}
	public final String getExp()  {
		return this.GetValStringByKey("Exp");
	}
	public final String getForEvent()  {
		return this.GetValStringByKey("ForEvent");
	}
	public final String getMsg()  {
		return this.GetValStringByKey("Msg");
	}

		///#endregion


		///#region 构造
	/** 
	 节点扩展信息
	*/
	public RegularExpressionDtl()
	{
	}
	/** 
	 获取一个实例
	*/
	@Override
	public XmlEns getNewEntities()
	{
		return new RegularExpressionDtls();
	}

		///#endregion
}
