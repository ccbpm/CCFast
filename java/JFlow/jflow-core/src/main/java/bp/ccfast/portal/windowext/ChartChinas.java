package bp.ccfast.portal.windowext;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import bp.ccfast.portal.*;
import java.util.*;

/** 
 柱状图s
*/
public class ChartChinas extends EntitiesNoName
{

		///#region 构造
	/** 
	 柱状图s
	*/
	public ChartChinas()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new ChartChina();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<ChartChina> ToJavaList()
	{
		return (java.util.List<ChartChina>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<ChartChina> Tolist()
	{
		ArrayList<ChartChina> list = new ArrayList<ChartChina>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((ChartChina)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
