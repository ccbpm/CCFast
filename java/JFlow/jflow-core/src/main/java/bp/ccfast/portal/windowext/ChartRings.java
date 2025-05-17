package bp.ccfast.portal.windowext;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import bp.ccfast.portal.*;
import java.util.*;

/** 
 环形图s
*/
public class ChartRings extends EntitiesNoName
{

		///#region 构造
	/** 
	 环形图s
	*/
	public ChartRings()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new ChartRing();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<ChartRing> ToJavaList()
	{
		return (java.util.List<ChartRing>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<ChartRing> Tolist()
	{
		ArrayList<ChartRing> list = new ArrayList<ChartRing>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((ChartRing)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
