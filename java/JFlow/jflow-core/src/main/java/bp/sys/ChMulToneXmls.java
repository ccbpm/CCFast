package bp.sys;

import bp.en.*;
import bp.sys.xml.*;
import java.util.List;

/** 
 多音字s
*/
public class ChMulToneXmls extends XmlEns
{

		///#region 构造
	/** 
	 多音字s
	*/
	public ChMulToneXmls()
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
		return new ChMulToneXml();
	}
	@Override
	public String getFile()
	{
		return bp.difference.SystemConfig.getPathOfData() + "XML/XmlDB.xml";
	}
	/** 
	 物理表名
	*/
	@Override
	public String getTableName()
	{
		return "PinYin";
	}
	@Override
	public Entities getRefEns()
	{
		return null;
	}

	public final List<ChMulToneXml> ToJavaList() {
		return (List<ChMulToneXml>)(Object)this;
	}

}
