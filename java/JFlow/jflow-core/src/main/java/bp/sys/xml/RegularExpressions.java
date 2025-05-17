package bp.sys.xml;
import bp.en.*;
import bp.difference.*;

/** 
 
*/
public class RegularExpressions extends XmlEns
{

		///#region 构造
	/** 
	 正则表达模版
	*/
	public RegularExpressions()
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
		return new RegularExpression();
	}
	/** 
	 文件路径
	*/
	@Override
	public String getFile()
	{
		return SystemConfig.getPathOfData() + "XML/RegularExpression.xml";
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
