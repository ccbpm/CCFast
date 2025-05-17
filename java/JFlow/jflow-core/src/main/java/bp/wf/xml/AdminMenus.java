package bp.wf.xml;

import bp.en.*;
import bp.sys.xml.*;
import bp.sys.*;

/** 
 
*/
public class AdminMenus extends XmlEns
{

		///#region 构造
	/** 
	 考核率的数据元素
	*/
	public AdminMenus()
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
		return new AdminMenu();
	}
	@Override
	public String getFile()
	{
		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			return bp.difference.SystemConfig.getPathOfWebApp() + "DataUser/XML/AdminMenu.xml";
		}

		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			if (bp.web.WebUser.getNo().equals("admin") == true)
			{
				return bp.difference.SystemConfig.getPathOfWebApp() + "DataUser/XML/AdminMenuSAAS.xml";
			}
			else
			{
				return bp.difference.SystemConfig.getPathOfWebApp() + "DataUser/XML/Admin2MenuSAAS.xml";
			}
		}

		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
		{
			if (bp.web.WebUser.getNo().equals("admin") == true)
			{
				return bp.difference.SystemConfig.getPathOfWebApp() + "DataUser/XML/AdminMenuGroup.xml";
			}
			else
			{
				return bp.difference.SystemConfig.getPathOfWebApp() + "DataUser/XML/Admin2MenuGroup.xml";
			}
		}
		throw new RuntimeException("err@系统错误....");
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
