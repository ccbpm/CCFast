package bp.sys;

import bp.en.*;
import bp.web.*;
import bp.difference.*;
import java.util.*;

/** 
 用户自定义表s
*/
public class SFProcedures extends EntitiesNoName
{

		///#region 构造
	/** 
	 用户自定义表s
	*/
	public SFProcedures()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SFProcedure();
	}
	/** 
	  重写过程全部的方法
	 
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
	public final java.util.List<SFProcedure> ToJavaList()
	{
		return (java.util.List<SFProcedure>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SFProcedure> Tolist()
	{
		ArrayList<SFProcedure> list = new ArrayList<SFProcedure>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SFProcedure)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
