package bp.port;

import bp.da.*;
import bp.difference.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.web.*;
import bp.*;
import java.util.*;

/** 
 用户组类型
*/
public class TeamTypes extends EntitiesNoName
{

		///#region 构造.
	/** 
	 用户组类型s
	*/
	public TeamTypes()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new TeamType();
	}

		///#endregion 构造.



		///#region 查询..
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

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			return this.Retrieve("OrgNo", WebUser.getOrgNo(), "Idx");
		}

		//集团模式下的角色体系: @0=每套组织都有自己的角色体系@1=所有的组织共享一套岗则体系.
		if (SystemConfig.getGroupStationModel() == 1)
		{
			return super.RetrieveAll("Idx");
		}

		//按照orgNo查询.
		return this.Retrieve("OrgNo", WebUser.getOrgNo(), "Idx");
	}
	@Override
	public int RetrieveAllFromDBSource() throws Exception {
		return this.RetrieveAll();
	}

		///#endregion 查询..


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<TeamType> ToJavaList()
	{
		return (java.util.List<TeamType>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<TeamType> Tolist()
	{
		ArrayList<TeamType> list = new ArrayList<TeamType>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((TeamType)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
