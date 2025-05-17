package bp.port;

import bp.difference.*;
import bp.en.*;
import bp.sys.*;
import bp.web.*;
import java.util.*;

/** 
 用户组s
*/
public class Teams extends EntitiesNoName
{

		///#region 构造
	/** 
	 用户组s
	*/
	public Teams()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Team();
	}

		///#endregion 构造


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
	public final java.util.List<Team> ToJavaList()
	{
		return (java.util.List<Team>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Team> Tolist()
	{
		ArrayList<Team> list = new ArrayList<Team>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Team)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
