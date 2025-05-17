package bp.ccfast.portal.windowext;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import bp.ccfast.portal.*;
import java.util.*;

/** 
 百分比扇形图s
*/
public class ChartRates extends EntitiesNoName
{

		///#region 构造
	/** 
	 百分比扇形图s
	*/
	public ChartRates()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new ChartRate();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<ChartRate> ToJavaList()
	{
		return (java.util.List<ChartRate>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<ChartRate> Tolist()
	{
		ArrayList<ChartRate> list = new ArrayList<ChartRate>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((ChartRate)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
