package bp.sys;

import bp.en.*;
import bp.web.*;
import bp.difference.*;
import java.util.*;

/** 
 用户自定义表s
*/
public class SFSearchs extends EntitiesNoName
{

		///#region 构造
	/** 
	 用户自定义表s
	*/
	public SFSearchs()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SFSearch();
	}
	/** 
	  重写查询全部的方法
	 
	 @return 
	*/
	@Override
	public int RetrieveAll() throws Exception {
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			return super.RetrieveAll("RDT");
		}
		return this.Retrieve("OrgNo", WebUser.getOrgNo(), "RDT");
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SFSearch> ToJavaList()
	{
		return (java.util.List<SFSearch>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SFSearch> Tolist()
	{
		ArrayList<SFSearch> list = new ArrayList<SFSearch>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SFSearch)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
