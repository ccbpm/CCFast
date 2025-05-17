package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 系统字典表s
*/
public class SFTableDtls extends EntitiesMyPK
{

		///#region 构造
	/** 
	 系统字典表s
	*/
	public SFTableDtls()
	{
	}

	public SFTableDtls(String fk_sftable) throws Exception {
		this.Retrieve(SFTableDtlAttr.FK_SFTable, fk_sftable);
	}


	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SFTableDtl();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SFTableDtl> ToJavaList()
	{
		return (java.util.List<SFTableDtl>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SFTableDtl> Tolist()
	{
		ArrayList<SFTableDtl> list = new ArrayList<SFTableDtl>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SFTableDtl)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
