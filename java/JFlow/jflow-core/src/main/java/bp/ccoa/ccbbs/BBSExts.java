package bp.ccoa.ccbbs;

import bp.web.*;
import bp.en.*;
import bp.sys.*;
import java.util.*;

/** 
 信息 s
*/
public class BBSExts extends EntitiesNoName
{

		///#region 构造函数.
	/** 
	 信息
	*/
	public BBSExts()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new BBSExt();
	}

		///#endregion 构造函数.

	@Override
	public int RetrieveAll() throws Exception {

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			return this.Retrieve(BBSAttr.OrgNo, WebUser.getOrgNo(), null);
		}

		return super.RetrieveAll();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<BBSExt> ToJavaList()
	{
		return (java.util.List<BBSExt>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<BBSExt> Tolist()
	{
		ArrayList<BBSExt> list = new ArrayList<BBSExt>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((BBSExt)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
