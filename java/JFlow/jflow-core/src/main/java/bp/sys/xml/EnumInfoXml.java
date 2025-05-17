package bp.sys.xml;


/** 
 EnumInfoXml 的摘要说明，属性的配置。
*/
public class EnumInfoXml extends XmlEn
{

		///#region 属性
	public final String getName()  {
		return this.GetValStringByKey(bp.web.WebUser.getSysLang());
	}
	public final String getKey()  {
		return this.GetValStringByKey("Key");
	}
	/** 
	 Vals
	*/
	public final String getVals() throws Exception {
		String str = bp.web.WebUser.getSysLang();
		str = "CH";
		return this.GetValStringByKey(str);
	}

		///#endregion


		///#region 构造
	public EnumInfoXml()
	{
	}
	public EnumInfoXml(String key) throws Exception {
		this.RetrieveByPK("Key", key);
	}

	/** 
	 获取一个实例
	*/
	@Override
	public XmlEns getNewEntities()
	{
		return new EnumInfoXmls();
	}

		///#endregion
}
