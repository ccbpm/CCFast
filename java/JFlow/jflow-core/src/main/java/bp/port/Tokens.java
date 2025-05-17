package bp.port;

import bp.en.*;
import bp.sys.*;
import bp.difference.*;
import java.util.*;

/** 
 登录记录
*/
public class Tokens extends EntitiesMyPK
{

		///#region 构造方法..
	/** 
	 登录记录s
	*/
	public Tokens()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Token();
	}

		///#endregion 构造方法..


		///#region 查询..
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

		//按照orgNo查询.
		return this.Retrieve("OrgNo", bp.web.WebUser.getOrgNo(), "Idx");
	}

		///#endregion 查询..


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Token> ToJavaList()
	{
		return (java.util.List<Token>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Token> Tolist()
	{
		ArrayList<Token> list = new ArrayList<Token>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Token)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
