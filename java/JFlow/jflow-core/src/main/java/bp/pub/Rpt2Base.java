package bp.pub;

/** 
 报表基类
*/
public abstract class Rpt2Base
{

		///#region 构造方法
	/** 
	 报表基类
	*/
	public Rpt2Base()
	{
	}

		///#endregion 构造方法


		///#region 要求子类强制重写的属性.
	/** 
	 显示的标题.
	*/
	public abstract String getTitle();
	/** 
	 默认选择的属性.
	*/
	public abstract int getAttrDefSelected();
	/** 
	 分组显示属性, 多个属性用@符号隔开.
	*/
	public abstract Rpt2Attrs getAttrsOfGroup();

		///#endregion 要求子类重写的属性.


		///#region 提供给操作者的方法.

		///#endregion 提供给操作者的方法
}
