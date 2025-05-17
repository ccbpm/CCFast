package bp.ccfast.ccmenu;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.ccfast.*;
import java.util.*;

/** 
 菜单s
*/
public class Menus extends EntitiesNoName
{

		///#region 构造
	/** 
	 菜单s
	*/
	public Menus()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Menu();
	}
	@Override
	public int RetrieveAll() throws Exception {
		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.SAAS)
		{
			return super.RetrieveAll("Idx");
		}

		////集团模式下的角色体系: @0=每套组织都有自己的角色体系@1=所有的组织共享一套岗则体系.
		//if (bp.difference.SystemConfig.getGroupStationModel() == 1)
		//    return base.RetrieveAll("Idx");

		//按照orgNo查询.
		return this.Retrieve("OrgNo", bp.web.WebUser.getOrgNo(), "Idx");
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Menu> ToJavaList()
	{
		return (java.util.List<Menu>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Menu> Tolist()
	{
		ArrayList<Menu> list = new ArrayList<Menu>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Menu)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
