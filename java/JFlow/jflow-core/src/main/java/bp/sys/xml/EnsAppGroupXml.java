package bp.sys.xml;

/** 
 EnsAppGroupXml 的摘要说明，属性的配置。
*/
public class EnsAppGroupXml extends XmlEnNoName
{

		///#region 属性
	/** 
	 类名
	*/
	public final String getEnsName()  {
		return this.GetValStringByKey(EnsAppGroupXmlEnsName.EnsName);
	}
	/** 
	 数据类型
	*/
	public final String getGroupName()  {
		return this.GetValStringByKey(EnsAppGroupXmlEnsName.GroupName);
	}
	/** 
	 描述
	*/
	public final String getGroupKey()  {
		return this.GetValStringByKey(EnsAppGroupXmlEnsName.GroupKey);
	}

		///#endregion


		///#region 构造
	public EnsAppGroupXml()
	{
	}
	/** 
	 获取一个实例
	*/
	@Override
	public XmlEns getNewEntities()
	{
		return new EnsAppGroupXmls();
	}

		///#endregion
}
