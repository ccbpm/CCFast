package bp.port;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.difference.*;
import bp.*;
import java.util.*;

/** 
 角色s
*/
public class Stations extends EntitiesNoName
{
	/** 
	 角色
	*/
	public Stations()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Station();
	}
	/** 
	 查询全部
	 
	 @param orderBy 排序
	 @return 
	*/
	@Override
	public int RetrieveAll(String orderBy) throws Exception {
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			return super.RetrieveAll(orderBy);
		}

		//集团模式下的角色体系: @0=每套组织都有自己的角色体系@1=所有的组织共享一套岗则体系.
		if (SystemConfig.getGroupStationModel() == 1)
		{
			return super.RetrieveAll();
		}

		//按照orgNo查询.
		return this.Retrieve("OrgNo", bp.web.WebUser.getOrgNo(), orderBy);
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
		if (SystemConfig.getGroupStationModel() == 0)
		{
			return super.Retrieve(StationAttr.OrgNo, bp.web.WebUser.getOrgNo());
		}

		if (SystemConfig.getGroupStationModel() == 2)
		{
			return super.Retrieve(StationAttr.FK_Dept, bp.web.WebUser.getDeptNo());
		}

		//按照orgNo查询.
		return this.Retrieve("OrgNo", bp.web.WebUser.getOrgNo(), "Idx");
	}

	@Override
	public int RetrieveAllFromDBSource() throws Exception {
		return this.RetrieveAll();
	}

	@Override
	public int RetrieveAllFromDBSource(String orderBY) throws Exception {
		return this.RetrieveAll(orderBY);
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Station> ToJavaList()
	{
		return (java.util.List<Station>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Station> Tolist()
	{
		ArrayList<Station> list = new ArrayList<Station>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Station)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
