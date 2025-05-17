package bp.wf.xml;

import bp.sys.xml.*;

/** 
 管理员
*/
public class AdminMenu extends XmlEn
{
		///#region 属性
	/** 
	 菜单编号
	*/
	public final String getNo() {
		return this.GetValStringByKey("No");
	}
	/**
	 分组编号
	*/
	public final String getGroupNo() {
		return this.GetValStringByKey("GroupNo");
	}

	/** 
	 名称
	*/
	public final String getName() {
		return this.GetValStringByKey("Name");
	}

	/** 
	 应用范围
	*/
	public final String getFor() {
		return this.GetValStringByKey("For");
	}

	/** 
	 Url菜单
	*/
	public final String getUrl() {
		return this.GetValStringByKey("Url");
	}
		///#endregion
		///#region 构造
	/** 
	 节点扩展信息
	*/
	public AdminMenu()
	{
	}
	/** 
	 获取一个实例
	*/
	@Override
	public XmlEns getNewEntities()
	{
		return new AdminMenus();
	}

		///#endregion
}
