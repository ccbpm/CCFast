package bp.sys.xml;
import bp.en.*;

/** 
 属性集合
*/
public class SQLLists extends XmlEns
{

		///#region 构造
	/** 
	 考核过错行为的数据元素
	*/
	public SQLLists()
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
		return new SQLList();
	}
	@Override
	public String getFile() {
		return bp.difference.SystemConfig.getPathOfXML() + "SQLList.xml";
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

		///#endregion
}
