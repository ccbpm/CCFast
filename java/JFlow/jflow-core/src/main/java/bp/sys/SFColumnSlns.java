package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 查询列s
*/
public class SFColumnSlns extends EntitiesMyPK
{

		///#region 构造
	/** 
	 查询列s
	*/
	public SFColumnSlns()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SFColumnSln();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SFColumnSln> ToJavaList()
	{
		return (java.util.List<SFColumnSln>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SFColumnSln> Tolist()
	{
		ArrayList<SFColumnSln> list = new ArrayList<SFColumnSln>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SFColumnSln)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
