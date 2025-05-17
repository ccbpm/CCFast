package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 查询列s
*/
public class SFColumns extends EntitiesMyPK
{

		///#region 构造
	/** 
	 查询列s
	*/
	public SFColumns()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SFColumn();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SFColumn> ToJavaList()
	{
		return (java.util.List<SFColumn>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SFColumn> Tolist()
	{
		ArrayList<SFColumn> list = new ArrayList<SFColumn>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SFColumn)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
