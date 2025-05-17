package bp.sys;

import bp.sys.xml.*;

/** 
 多音字
*/
public class ChMulToneXml extends XmlEn
{

		///#region 属性
	public final String getNo() {
		return this.GetValStringByKey("No");
	}
	public final String getName() {
		return this.GetValStringByKey("Name");
	}
	public final String getDesc() {
		return this.GetValStringByKey("No");
	}

		///#endregion


		///#region 构造
	/** 
	 节点扩展信息
	*/
	public ChMulToneXml()
	{
	}
	/** 
	 获取一个实例s
	*/
	@Override
	public XmlEns getNewEntities()
	{
		return new ChMulToneXmls();
	}


}
