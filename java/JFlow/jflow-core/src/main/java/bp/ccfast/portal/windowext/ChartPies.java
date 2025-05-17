package bp.ccfast.portal.windowext;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import bp.ccfast.portal.*;
import java.util.*;

/** 
 饼图s
*/
public class ChartPies extends EntitiesNoName
{

		///#region 构造
	/** 
	 饼图s
	*/
	public ChartPies()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new ChartPie();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<ChartPie> ToJavaList()
	{
		return (java.util.List<ChartPie>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<ChartPie> Tolist()
	{
		ArrayList<ChartPie> list = new ArrayList<ChartPie>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((ChartPie)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
