package bp.port;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.difference.*;
import bp.*;
import java.util.*;

/** 
 角色类型
*/
public class StationTypes extends EntitiesNoName
{

		///#region 构造方法..
	/** 
	 角色类型s
	*/
	public StationTypes()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new StationType();
	}

		///#endregion 构造方法..


		///#region 查询..

	@Override
	public int RetrieveAllFromDBSource() throws Exception {
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			return super.RetrieveAllFromDBSource("Idx");
		}

		//角色(岗位)体系设置: @0=每套组织都有自己的角色(岗位)体系@1=所有的组织共享一套角色(岗位)体系
		if (SystemConfig.getGroupStationModel() == 1)
		{
			return super.RetrieveAllFromDBSource("Idx");
		}

		//按照orgNo查询.
		return this.Retrieve("OrgNo", bp.web.WebUser.getOrgNo(), "Idx");
	}

	/**
	 查询全部
	 
	 @return 
	*/
	@Override
	public int RetrieveAll() throws Exception {
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			return super.RetrieveAll("Idx");
		}

		//集团模式下的角色体系: @0=每套组织都有自己的角色体系@1=所有的组织共享一套岗则体系.
		if (SystemConfig.getGroupStationModel() == 1)
		{
			return super.RetrieveAll("Idx");
		}

		//按照orgNo查询.
		return this.Retrieve("OrgNo", bp.web.WebUser.getOrgNo(), "Idx");
	}
	/**
	 查询全部

	 @return
	 */
	@Override
	public int RetrieveAll(String idx) throws Exception {
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			return super.RetrieveAll(idx);
		}

		//集团模式下的角色体系: @0=每套组织都有自己的角色体系@1=所有的组织共享一套岗则体系.
		if (SystemConfig.getGroupStationModel() == 1)
		{
			return super.RetrieveAll(idx);
		}

		//按照orgNo查询.
		return this.Retrieve("OrgNo", bp.web.WebUser.getOrgNo(), idx);
	}
		///#endregion 查询..


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<StationType> ToJavaList()
	{
		return (java.util.List<StationType>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<StationType> Tolist()
	{
		ArrayList<StationType> list = new ArrayList<StationType>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((StationType)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
