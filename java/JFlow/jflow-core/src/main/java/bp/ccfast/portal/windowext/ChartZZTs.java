package bp.ccfast.portal.windowext;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import bp.ccfast.portal.*;
import java.util.*;

/** 
 柱状图s
*/
public class ChartZZTs extends EntitiesNoName
{

		///#region 构造
	/** 
	 柱状图s
	*/
	public ChartZZTs()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new ChartZZT();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<ChartZZT> ToJavaList()
	{
		return (java.util.List<ChartZZT>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<ChartZZT> Tolist()
	{
		ArrayList<ChartZZT> list = new ArrayList<ChartZZT>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((ChartZZT)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
