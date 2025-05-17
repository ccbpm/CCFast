package bp.sys.xml;

import bp.en.*;
import bp.difference.*;

/** 
 属性集合
*/
public class EnumInfoXmls extends XmlEns
{

		///#region 构造
	/** 
	 考核过错行为的数据元素
	*/
	public EnumInfoXmls()
	{
	}


		///#endregion


		///#region 重写基类属性或方法。
	/** 
	 得到它的 Entity 
	*/
	@Override
	public XmlEn getNewEntity()
	{
		return new EnumInfoXml();
	}
	@Override
	public String getFile() {
		return SystemConfig.getPathOfXML() + "Enum/";
	}
	/** 
	 物理表名
	*/
	@Override
	public String getTableName()
	{
		return "Item";
	}
	@Override
	public Entities getRefEns()
	{
		return null;
	}

	public final java.util.List<EnumInfoXml> ToJavaList() {
		return (java.util.List<EnumInfoXml>)(Object)this;
	}
		///#endregion

}
