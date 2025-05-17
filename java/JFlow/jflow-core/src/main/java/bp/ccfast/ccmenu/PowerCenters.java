package bp.ccfast.ccmenu;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.ccfast.*;
import java.util.*;

/** 
 权限中心s
*/
public class PowerCenters extends EntitiesMyPK
{
	@Override
	public int RetrieveAll() throws Exception {
		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.SAAS)
		{
			return super.RetrieveAll();
		}

		/*翻译ccflow去掉了//集团模式下的角色体系: @0=每套组织都有自己的角色体系@1=所有的组织共享一套岗则体系.
		if (bp.difference.SystemConfig.getGroupStationModel() == 1)
		{
			return super.RetrieveAll();
		}
*/
		//按照orgNo查询.
		return this.Retrieve("OrgNo", bp.web.WebUser.getOrgNo(), null);
	}


		///#region 构造
	/** 
	 权限中心s
	*/
	public PowerCenters()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new PowerCenter();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<PowerCenter> ToJavaList()
	{
		return (java.util.List<PowerCenter>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<PowerCenter> Tolist()
	{
		ArrayList<PowerCenter> list = new ArrayList<PowerCenter>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((PowerCenter)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
